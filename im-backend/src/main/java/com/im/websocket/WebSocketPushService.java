package com.im.websocket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.*;

/**
 * WebSocket推送服务
 * 功能 #2: WebSocket实时推送服务 - 消息推送确认机制
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Component
public class WebSocketPushService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebSocketPushService.class);
    
    @Autowired
    private WebSocketConnectionManager connectionManager;
    
    // 推送确认等待池
    private final Map<String, CompletableFuture<Boolean>> pendingAcks = new ConcurrentHashMap<>();
    
    // 推送历史
    private final Map<String, PushRecord> pushHistory = new ConcurrentHashMap<>();
    
    // 重试队列
    private final BlockingQueue<RetryTask> retryQueue = new LinkedBlockingQueue<>();
    
    // 重试执行器
    private final ScheduledExecutorService retryExecutor = Executors.newSingleThreadScheduledExecutor();
    
    public WebSocketPushService() {
        // 启动重试处理器
        retryExecutor.scheduleAtFixedRate(this::processRetryQueue, 1, 1, TimeUnit.SECONDS);
    }
    
    // ==================== 推送方法 ====================
    
    /**
     * 推送消息到用户（带确认）
     */
    public boolean pushToUser(String userId, String message) {
        return pushToUser(userId, message, true);
    }
    
    /**
     * 推送消息到用户
     */
    public boolean pushToUser(String userId, String message, boolean requireAck) {
        String pushId = generatePushId();
        
        // 包装消息
        String wrappedMessage = wrapMessage(pushId, message, requireAck);
        
        // 记录推送
        PushRecord record = new PushRecord(pushId, userId, message);
        pushHistory.put(pushId, record);
        
        // 发送
        boolean sent = connectionManager.sendToUser(userId, wrappedMessage);
        record.setSent(sent);
        
        if (sent && requireAck) {
            // 等待确认
            return waitForAck(pushId, 5);
        }
        
        return sent;
    }
    
    /**
     * 广播消息
     */
    public void broadcast(String message) {
        String pushId = generatePushId();
        String wrappedMessage = wrapMessage(pushId, message, false);
        
        connectionManager.broadcast(wrappedMessage);
        
        PushRecord record = new PushRecord(pushId, "ALL", message);
        record.setSent(true);
        pushHistory.put(pushId, record);
    }
    
    /**
     * 组播消息
     */
    public void multicast(String groupId, String message) {
        String pushId = generatePushId();
        String wrappedMessage = wrapMessage(pushId, message, false);
        
        connectionManager.multicast(groupId, wrappedMessage);
    }
    
    // ==================== 确认机制 ====================
    
    /**
     * 处理客户端确认
     */
    public void handleAck(String pushId) {
        PushRecord record = pushHistory.get(pushId);
        if (record != null) {
            record.setAcknowledged(true);
            record.setAckTime(System.currentTimeMillis());
        }
        
        CompletableFuture<Boolean> future = pendingAcks.remove(pushId);
        if (future != null) {
            future.complete(true);
        }
        
        logger.debug("Push acknowledged: {}", pushId);
    }
    
    /**
     * 等待确认
     */
    private boolean waitForAck(String pushId, int timeoutSeconds) {
        CompletableFuture<Boolean> future = new CompletableFuture<>();
        pendingAcks.put(pushId, future);
        
        try {
            return future.get(timeoutSeconds, TimeUnit.SECONDS);
        } catch (Exception e) {
            pendingAcks.remove(pushId);
            PushRecord record = pushHistory.get(pushId);
            if (record != null) {
                record.setAcknowledged(false);
                // 加入重试队列
                retryQueue.offer(new RetryTask(record, 1));
            }
            return false;
        }
    }
    
    // ==================== 重试机制 ====================
    
    private void processRetryQueue() {
        RetryTask task = retryQueue.poll();
        if (task == null) return;
        
        if (task.getRetryCount() < 3) {
            PushRecord record = task.getRecord();
            logger.info("Retrying push: {}, attempt: {}", record.getPushId(), task.getRetryCount());
            
            boolean sent = connectionManager.sendToUser(record.getUserId(), 
                wrapMessage(record.getPushId(), record.getMessage(), true));
            
            if (!sent) {
                retryQueue.offer(new RetryTask(record, task.getRetryCount() + 1));
            }
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private String generatePushId() {
        return "PUSH_" + System.currentTimeMillis() + "_" + ThreadLocalRandom.current().nextInt(10000);
    }
    
    private String wrapMessage(String pushId, String message, boolean requireAck) {
        return "{\"pushId\":\"" + pushId + "\",\"requireAck\":" + requireAck + 
               ",\"data\":" + message + ",\"timestamp\":" + System.currentTimeMillis() + "}";
    }
    
    /**
     * 获取推送统计
     */
    public Map<String, Object> getStats() {
        long total = pushHistory.size();
        long acknowledged = pushHistory.values().stream().filter(PushRecord::isAcknowledged).count();
        long pending = pendingAcks.size();
        
        return Map.of(
            "total", total,
            "acknowledged", acknowledged,
            "pending", pending,
            "successRate", total > 0 ? (acknowledged * 100.0 / total) : 0
        );
    }
    
    // ==================== 内部类 ====================
    
    private static class PushRecord {
        private final String pushId;
        private final String userId;
        private final String message;
        private final long pushTime;
        private boolean sent;
        private boolean acknowledged;
        private long ackTime;
        
        PushRecord(String pushId, String userId, String message) {
            this.pushId = pushId;
            this.userId = userId;
            this.message = message;
            this.pushTime = System.currentTimeMillis();
        }
        
        String getPushId() { return pushId; }
        String getUserId() { return userId; }
        String getMessage() { return message; }
        boolean isAcknowledged() { return acknowledged; }
        void setAcknowledged(boolean acknowledged) { this.acknowledged = acknowledged; }
        void setSent(boolean sent) { this.sent = sent; }
        void setAckTime(long ackTime) { this.ackTime = ackTime; }
    }
    
    private static class RetryTask {
        private final PushRecord record;
        private final int retryCount;
        
        RetryTask(PushRecord record, int retryCount) {
            this.record = record;
            this.retryCount = retryCount;
        }
        
        PushRecord getRecord() { return record; }
        int getRetryCount() { return retryCount; }
    }
}
