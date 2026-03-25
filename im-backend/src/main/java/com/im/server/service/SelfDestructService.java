package com.im.server.service;

import com.im.server.entity.Message;
import com.im.server.entity.SelfDestructMessage;
import com.im.server.repository.SelfDestructMessageRepository;
import com.im.server.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 阅后即焚服务 - 消息自动销毁
 * 
 * 功能特性：
 * 1. 支持多种销毁计时器：5秒/30秒/1分钟/5分钟/1小时/24小时
 * 2. 消息读取后自动开始倒计时
 * 3. 支持查看消息状态（已读/未读/已销毁）
 * 4. 销毁时保留元数据（发送者、时间戳）
 * 5. 销毁记录审计日志
 * 6. 支持批量销毁和手动销毁
 * 7. 销毁前通知发送者
 */
@Service
public class SelfDestructService {

    @Autowired
    private SelfDestructMessageRepository repository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    // 销毁计时器类型枚举
    public enum TimerType {
        SECONDS_5(5, "5秒"),
        SECONDS_30(30, "30秒"),
        MINUTE_1(60, "1分钟"),
        MINUTES_5(300, "5分钟"),
        HOUR_1(3600, "1小时"),
        HOURS_24(86400, "24小时"),
        CUSTOM(-1, "自定义");

        private final int seconds;
        private final String label;

        TimerType(int seconds, String label) {
            this.seconds = seconds;
            this.label = label;
        }

        public int getSeconds() { return seconds; }
        public String getLabel() { return label; }
    }

    // 消息销毁状态
    public enum DestroyStatus {
        PENDING,      // 待销毁（未读）
        COUNTING,     // 倒计时中（已读）
        DESTROYED,    // 已销毁
        EXPIRED       // 超时未读
    }

    // 内存缓存：正在倒计时的消息（避免频繁Redis查询）
    private final Map<String, CountdownEntry> countdownCache = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(4);

    // 销毁记录缓存（用于审计）
    private final Map<String, DestroyRecord> destroyRecords = new ConcurrentHashMap<>();

    /**
     * 初始化服务
     */
    @PostConstruct
    public void init() {
        // 从数据库加载所有待销毁消息
        loadPendingMessages();
        
        // 启动定时检查任务（每10秒检查一次）
        scheduler.scheduleAtFixedRate(this::checkAndDestroyMessages, 10, 10, TimeUnit.SECONDS);
    }

    /**
     * 设置消息阅后即焚
     */
    public SelfDestructMessage setupSelfDestruct(Long messageId, Long senderId, Long receiverId, 
                                                  TimerType timerType, int customSeconds) {
        int duration = timerType.getSeconds();
        if (timerType == TimerType.CUSTOM) {
            duration = customSeconds;
        }

        Message message = messageRepository.findById(messageId).orElse(null);
        if (message == null) {
            throw new IllegalArgumentException("消息不存在");
        }

        SelfDestructMessage sdm = new SelfDestructMessage();
        sdm.setMessageId(messageId);
        sdm.setSenderId(senderId);
        sdm.setReceiverId(receiverId);
        sdm.setTimerType(timerType);
        sdm.setDurationSeconds(duration);
        sdm.setStatus(DestroyStatus.PENDING);
        sdm.setCreatedAt(Instant.now());

        // 保存到数据库
        SelfDestructMessage saved = repository.save(sdm);

        // 缓存到Redis
        String redisKey = getRedisKey(messageId);
        redisTemplate.opsForHash().put(redisKey, "status", DestroyStatus.PENDING.name());
        redisTemplate.opsForHash().put(redisKey, "duration", String.valueOf(duration));
        redisTemplate.opsForHash().put(redisKey, "createdAt", String.valueOf(Instant.now().toEpochMilli()));

        return saved;
    }

    /**
     * 标记消息已读（开始倒计时）
     */
    public void markAsRead(Long messageId, Long readerId) {
        String redisKey = getRedisKey(messageId);
        
        // 检查是否已标记过
        String status = (String) redisTemplate.opsForHash().get(redisKey, "status");
        if (status != null && !status.equals(DestroyStatus.PENDING.name())) {
            return; // 已经读过了
        }

        // 更新状态为倒计时中
        redisTemplate.opsForHash().put(redisKey, "status", DestroyStatus.COUNTING.name());
        redisTemplate.opsForHash().put(redisKey, "readAt", String.valueOf(Instant.now().toEpochMilli()));

        // 获取销毁时长
        String durationStr = (String) redisTemplate.opsForHash().get(redisKey, "duration");
        int duration = durationStr != null ? Integer.parseInt(durationStr) : 30;

        // 添加到倒计时缓存
        CountdownEntry entry = new CountdownEntry(messageId, Instant.now(), duration);
        countdownCache.put(messageId.toString(), entry);

        // 更新数据库状态
        repository.updateStatus(messageId, DestroyStatus.COUNTING);

        // 安排销毁任务
        scheduleDestroy(messageId, duration);
    }

    /**
     * 手动销毁消息
     */
    public void destroyMessage(Long messageId, Long operatorId) {
        performDestroy(messageId, "MANUAL", operatorId);
    }

    /**
     * 批量销毁消息
     */
    public void batchDestroy(List<Long> messageIds, Long operatorId) {
        for (Long messageId : messageIds) {
            try {
                destroyMessage(messageId, operatorId);
            } catch (Exception e) {
                // 记录错误但继续处理其他消息
                System.err.println("销毁消息失败: " + messageId + ", 错误: " + e.getMessage());
            }
        }
    }

    /**
     * 获取消息销毁状态
     */
    public DestroyStatus getStatus(Long messageId) {
        String redisKey = getRedisKey(messageId);
        String status = (String) redisTemplate.opsForHash().get(redisKey, "status");
        
        if (status != null) {
            return DestroyStatus.valueOf(status);
        }

        // 从数据库查询
        return repository.findByMessageId(messageId)
                .map(SelfDestructMessage::getStatus)
                .orElse(null);
    }

    /**
     * 获取销毁倒计时剩余秒数
     */
    public long getRemainingSeconds(Long messageId) {
        CountdownEntry entry = countdownCache.get(messageId.toString());
        if (entry == null) {
            return -1;
        }
        long elapsed = Instant.now().getEpochSecond() - entry.startTime.getEpochSecond();
        return Math.max(0, entry.duration - elapsed);
    }

    /**
     * 获取销毁历史记录
     */
    public List<DestroyRecord> getDestroyHistory(Long userId, int page, int size) {
        return destroyRecords.values().stream()
                .filter(r -> r.operatorId.equals(userId))
                .sorted((a, b) -> b.destroyTime.compareTo(a.destroyTime))
                .skip((long) page * size)
                .limit(size)
                .toList();
    }

    /**
     * 定时检查任务
     */
    private void checkAndDestroyMessages() {
        try {
            // 检查超时的待销毁消息
            List<SelfDestructMessage> pendingMessages = repository.findByStatus(DestroyStatus.PENDING);
            Instant threshold = Instant.now().minusSeconds(86400 * 7); // 7天未读标记为过期
            
            for (SelfDestructMessage sdm : pendingMessages) {
                if (sdm.getCreatedAt().isBefore(threshold)) {
                    // 标记为过期
                    repository.updateStatus(sdm.getMessageId(), DestroyStatus.EXPIRED);
                    
                    DestroyRecord record = new DestroyRecord(
                        sdm.getMessageId(), 
                        "EXPIRED", 
                        null, 
                        Instant.now(),
                        "消息超过7天未读，自动过期"
                    );
                    destroyRecords.put(sdm.getMessageId().toString(), record);
                }
            }

            // 清理已完成的倒计时缓存（节省内存）
            countdownCache.entrySet().removeIf(entry -> {
                long elapsed = Instant.now().getEpochSecond() - entry.getValue().startTime.getEpochSecond();
                return elapsed > entry.getValue().duration + 60; // 销毁后保留1分钟
            });

        } catch (Exception e) {
            System.err.println("检查销毁消息失败: " + e.getMessage());
        }
    }

    /**
     * 执行消息销毁
     */
    private void performDestroy(Long messageId, String reason, Long operatorId) {
        String redisKey = getRedisKey(messageId);

        // 获取当前状态
        String status = (String) redisTemplate.opsForHash().get(redisKey, "status");
        if (status != null && status.equals(DestroyStatus.DESTROYED.name())) {
            return; // 已经销毁了
        }

        // 更新Redis状态
        redisTemplate.opsForHash().put(redisKey, "status", DestroyStatus.DESTROYED.name());
        redisTemplate.opsForHash().put(redisKey, "destroyedAt", String.valueOf(Instant.now().toEpochMilli()));
        redisTemplate.opsForHash().put(redisKey, "destroyReason", reason);

        // 更新数据库
        repository.updateStatus(messageId, DestroyStatus.DESTROYED);

        // 从倒计时缓存移除
        countdownCache.remove(messageId.toString());

        // 记录销毁
        DestroyRecord record = new DestroyRecord(messageId, reason, operatorId, Instant.now(), null);
        destroyRecords.put(messageId.toString(), record);

        // 通知发送者（通过WebSocket或推送）
        notifySender(messageId, reason);

        System.out.println("消息已销毁: messageId=" + messageId + ", reason=" + reason);
    }

    /**
     * 安排销毁任务
     */
    private void scheduleDestroy(Long messageId, int delaySeconds) {
        scheduler.schedule(() -> {
            try {
                performDestroy(messageId, "TIMER", null);
            } catch (Exception e) {
                System.err.println("定时销毁失败: " + messageId + ", 错误: " + e.getMessage());
            }
        }, delaySeconds, TimeUnit.SECONDS);
    }

    /**
     * 通知发送者
     */
    private void notifySender(Long messageId, String reason) {
        // TODO: 通过WebSocket或消息推送通知发送者
        // 可以发送一个系统消息或推送通知
    }

    /**
     * 加载待销毁消息
     */
    private void loadPendingMessages() {
        List<SelfDestructMessage> pendingMessages = repository.findByStatus(DestroyStatus.PENDING);
        for (SelfDestructMessage sdm : pendingMessages) {
            String redisKey = getRedisKey(sdm.getMessageId());
            redisTemplate.opsForHash().put(redisKey, "status", sdm.getStatus().name());
            redisTemplate.opsForHash().put(redisKey, "duration", String.valueOf(sdm.getDurationSeconds()));
            redisTemplate.opsForHash().put(redisKey, "createdAt", String.valueOf(sdm.getCreatedAt().toEpochMilli()));
        }

        List<SelfDestructMessage> countingMessages = repository.findByStatus(DestroyStatus.COUNTING);
        for (SelfDestructMessage sdm : countingMessages) {
            String redisKey = getRedisKey(sdm.getMessageId());
            Long readAt = (Long) redisTemplate.opsForHash().get(redisKey, "readAt");
            
            if (readAt != null) {
                long elapsed = Instant.now().getEpochSecond() - (readAt / 1000);
                int remaining = sdm.getDurationSeconds() - (int) elapsed;
                
                if (remaining > 0) {
                    CountdownEntry entry = new CountdownEntry(
                        sdm.getMessageId(), 
                        Instant.ofEpochMilli(readAt), 
                        sdm.getDurationSeconds()
                    );
                    countdownCache.put(sdm.getMessageId().toString(), entry);
                    scheduleDestroy(sdm.getMessageId(), remaining);
                } else {
                    // 已经超时，立即销毁
                    performDestroy(sdm.getMessageId(), "TIMER", null);
                }
            }
        }
    }

    private String getRedisKey(Long messageId) {
        return "self_destruct:" + messageId;
    }

    // 内部类：倒计时条目
    private static class CountdownEntry {
        Long messageId;
        Instant startTime;
        int duration;

        CountdownEntry(Long messageId, Instant startTime, int duration) {
            this.messageId = messageId;
            this.startTime = startTime;
            this.duration = duration;
        }
    }

    // 内部类：销毁记录
    public static class DestroyRecord {
        public Long messageId;
        public String reason;
        public Long operatorId;
        public Instant destroyTime;
        public String note;

        public DestroyRecord(Long messageId, String reason, Long operatorId, 
                            Instant destroyTime, String note) {
            this.messageId = messageId;
            this.reason = reason;
            this.operatorId = operatorId;
            this.destroyTime = destroyTime;
            this.note = note;
        }
    }
}
