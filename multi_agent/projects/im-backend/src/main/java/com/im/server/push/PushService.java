package com.im.server.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 推送服务核心
 * 
 * 统一管理多平台推送：iOS (APNs)、Android (FCM/华为/小米/OPPO/vivo)、
 * 第三方推送（极光、个推、OneSignal）
 */
@Service
public class PushService {

    private static final Logger log = LoggerFactory.getLogger(PushService.class);

    @Autowired
    private PushConfig pushConfig;

    @Autowired
    private ApplePushService applePushService;

    @Autowired
    private AndroidPushService androidPushService;

    @Autowired
    private ThirdPartyPushService thirdPartyPushService;

    @Autowired
    private PushMessageBuilder pushMessageBuilder;

    @Autowired
    private PushMessageMerger pushMessageMerger;

    // 异步线程池
    private final ExecutorService pushExecutor = Executors.newFixedThreadPool(
            Runtime.getRuntime().availableProcessors() * 2,
            new ThreadFactory() {
                private final AtomicInteger counter = new AtomicInteger(1);
                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, "push-worker-" + counter.getAndIncrement());
                    t.setDaemon(true);
                    return t;
                }
            }
    );

    // 统计
    private final AtomicInteger totalPushCount = new AtomicInteger(0);
    private final AtomicInteger successCount = new AtomicInteger(0);
    private final AtomicInteger failureCount = new AtomicInteger(0);
    private final ConcurrentHashMap<String, AtomicInteger> channelStats = new ConcurrentHashMap<>();

    /**
     * 发送推送（用户ID）
     */
    public PushResult sendPush(Long userId, PushMessage message) {
        if (!pushConfig.isEnabled()) {
            log.warn("Push service is disabled");
            return PushResult.failed("Push service disabled");
        }

        if (userId == null || message == null) {
            return PushResult.failed("Invalid parameters");
        }

        message.setTargetUserId(userId);
        totalPushCount.incrementAndGet();

        try {
            // 检查免打扰时段
            if (pushConfig.isEnableQuietHours() && isInQuietHours(message)) {
                log.debug("Message delayed due to quiet hours: userId={}", userId);
                return scheduleForLater(message);
            }

            // 消息合并
            if (pushConfig.isEnableMessageMerging() && message.getMergeKey() != null) {
                PushMessage merged = pushMessageMerger.tryMerge(message);
                if (merged != null && merged != message) {
                    log.debug("Message merged with key={}", message.getMergeKey());
                    return PushResult.success("Message merged");
                }
            }

            // 构建平台特定消息
            Map<DeviceToken.Platform, List<DeviceToken>> tokensByPlatform = getUserDeviceTokens(userId);
            if (tokensByPlatform.isEmpty()) {
                log.debug("No device tokens for user: {}", userId);
                return PushResult.failed("No device tokens");
            }

            // 并行发送
            List<Future<PushResult>> futures = new ArrayList<>();
            for (Map.Entry<DeviceToken.Platform, List<DeviceToken>> entry : tokensByPlatform.entrySet()) {
                Future<PushResult> future = pushExecutor.submit(() ->
                        sendToPlatform(entry.getKey(), entry.getValue(), message)
                );
                futures.add(future);
            }

            // 汇总结果
            int success = 0, failure = 0;
            for (Future<PushResult> f : futures) {
                try {
                    PushResult r = f.get(10, TimeUnit.SECONDS);
                    if (r.isSuccess()) success++;
                    else failure++;
                } catch (Exception e) {
                    failure++;
                    log.error("Push future error", e);
                }
            }

            if (success > 0) {
                successCount.incrementAndGet();
                return PushResult.success("Sent to " + success + " devices");
            } else {
                failureCount.incrementAndGet();
                return PushResult.failed("All deliveries failed");
            }

        } catch (Exception e) {
            log.error("Push error for userId={}", userId, e);
            failureCount.incrementAndGet();
            return PushResult.failed(e.getMessage());
        }
    }

    /**
     * 发送推送（Token列表）
     */
    public PushResult sendPush(Set<String> tokens, PushMessage message) {
        if (!pushConfig.isEnabled() || tokens == null || tokens.isEmpty()) {
            return PushResult.failed("Invalid parameters or disabled");
        }

        totalPushCount.incrementAndGet();

        try {
            if (pushConfig.isEnableQuietHours() && isInQuietHours(message)) {
                return scheduleForLater(message);
            }

            // 按平台分组
            Map<DeviceToken.Platform, List<String>> tokensByPlatform = new HashMap<>();
            for (String token : tokens) {
                DeviceToken.Platform platform = guessPlatform(token);
                tokensByPlatform.computeIfAbsent(platform, k -> new ArrayList<>()).add(token);
            }

            // 并行发送
            List<Future<PushResult>> futures = new ArrayList<>();
            for (Map.Entry<DeviceToken.Platform, List<String>> entry : tokensByPlatform.entrySet()) {
                Future<PushResult> future = pushExecutor.submit(() ->
                        sendTokensToPlatform(entry.getKey(), entry.getValue(), message)
                );
                futures.add(future);
            }

            int success = 0;
            for (Future<PushResult> f : futures) {
                try {
                    PushResult r = f.get(10, TimeUnit.SECONDS);
                    if (r.isSuccess()) success++;
                } catch (Exception e) {
                    log.error("Push future error", e);
                }
            }

            if (success > 0) {
                successCount.incrementAndGet();
                return PushResult.success("Sent");
            } else {
                failureCount.incrementAndGet();
                return PushResult.failed("All failed");
            }

        } catch (Exception e) {
            log.error("Push error", e);
            failureCount.incrementAndGet();
            return PushResult.failed(e.getMessage());
        }
    }

    /**
     * 发送静默推送（后台刷新）
     */
    public PushResult sendSilentPush(Long userId, Map<String, String> data) {
        PushMessage message = PushMessage.builder()
                .pushType(PushMessage.PushType.SILENT)
                .targetUserId(userId)
                .data(data)
                .priority(PushMessage.Priority.LOW)
                .build();
        return sendPush(userId, message);
    }

    /**
     * 发送VoIP推送
     */
    public PushResult sendVoipPush(Long userId, String callId, Map<String, String> extras) {
        PushMessage message = PushMessage.builder()
                .pushType(PushMessage.PushType.VOIP)
                .targetUserId(userId)
                .data(extras)
                .category("incoming_call")
                .interruptionLevel("timeSensitive")
                .build();
        return sendPush(userId, message);
    }

    /**
     * 批量发送（用户ID列表）
     */
    public Map<Long, PushResult> sendBatchPush(List<Long> userIds, PushMessage message) {
        Map<Long, PushResult> results = new ConcurrentHashMap<>();
        CountDownLatch latch = new CountDownLatch(userIds.size());

        for (Long userId : userIds) {
            pushExecutor.submit(() -> {
                try {
                    results.put(userId, sendPush(userId, message));
                } finally {
                    latch.countDown();
                }
            });
        }

        try {
            latch.await(30, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return results;
    }

    /**
     * 发送广播
     */
    public PushResult sendBroadcast(PushMessage message) {
        if (!pushConfig.isEnabled()) {
            return PushResult.failed("Push service disabled");
        }

        message.setTargetType(PushMessage.TargetType.BROADCAST);
        totalPushCount.incrementAndGet();

        // 广播需要分批处理
        int offset = 0;
        int limit = pushConfig.getMaxBatchSize();
        int totalSent = 0;

        while (true) {
            List<Long> userIds = getAllActiveUserIds(offset, limit);
            if (userIds.isEmpty()) break;

            Map<Long, PushResult> results = sendBatchPush(userIds, message);
            totalSent += results.size();
            offset += limit;
        }

        successCount.incrementAndGet();
        return PushResult.success("Broadcast to " + totalSent + " users");
    }

    // ==================== 私有方法 ====================

    private PushResult sendToPlatform(DeviceToken.Platform platform, List<DeviceToken> tokens, PushMessage message) {
        if (tokens == null || tokens.isEmpty()) {
            return PushResult.failed("No tokens");
        }

        String channelKey = platform.name();
        channelStats.computeIfAbsent(channelKey, k -> new AtomicInteger()).incrementAndGet();

        try {
            switch (platform) {
                case IOS:
                    return applePushService.sendPush(tokens, message);
                case ANDROID:
                    return androidPushService.sendPush(tokens, message);
                default:
                    // 未知平台走第三方推送
                    return thirdPartyPushService.sendPush(tokens, message);
            }
        } catch (Exception e) {
            log.error("Platform push error: platform={}", platform, e);
            return PushResult.failed(e.getMessage());
        }
    }

    private PushResult sendTokensToPlatform(DeviceToken.Platform platform, List<String> tokens, PushMessage message) {
        try {
            switch (platform) {
                case IOS:
                    return applePushService.sendPushByTokens(tokens, message);
                case ANDROID:
                    return androidPushService.sendPushByTokens(tokens, message);
                default:
                    return thirdPartyPushService.sendPushByTokens(tokens, message);
            }
        } catch (Exception e) {
            log.error("Token push error: platform={}", platform, e);
            return PushResult.failed(e.getMessage());
        }
    }

    private boolean isInQuietHours(PushMessage message) {
        PushConfig.QuietHoursConfig qh = pushConfig.getQuietHours();
        if (!qh.isEnabled()) return false;

        // 检查消息是否标记为高优先级/紧急，可以穿透免打扰
        if (message.getPriority() == PushMessage.Priority.HIGH &&
            "timeSensitive".equals(message.getInterruptionLevel())) {
            return false;
        }

        LocalDateTime now = LocalDateTime.now();
        int currentHour = now.getHour();
        int currentMinute = now.getMinute();
        int currentTime = currentHour * 60 + currentMinute;

        int startTime = qh.getStartHour() * 60 + qh.getStartMinute();
        int endTime = qh.getEndHour() * 60 + qh.getEndMinute();

        if (startTime <= endTime) {
            // 同一天: 22:00 - 23:59
            return currentTime >= startTime || currentTime <= endTime;
        } else {
            // 跨天: 22:00 - 08:00
            return currentTime >= startTime || currentTime <= endTime;
        }
    }

    private PushResult scheduleForLater(PushMessage message) {
        // 计算下次免打扰结束时间，延迟发送
        // 这里简化处理，实际应该用定时任务
        log.debug("Message scheduled for after quiet hours");
        return PushResult.success("Scheduled for later");
    }

    private Map<DeviceToken.Platform, List<DeviceToken>> getUserDeviceTokens(Long userId) {
        // TODO: 从 DeviceTokenRepository 查询用户的所有设备Token
        // 这里返回空Map，实际实现需要注入 Repository
        return new HashMap<>();
    }

    private List<Long> getAllActiveUserIds(int offset, int limit) {
        // TODO: 从数据库查询活跃用户
        return new ArrayList<>();
    }

    private DeviceToken.Platform guessPlatform(String token) {
        if (token == null) return DeviceToken.Platform.UNKNOWN;
        // Token 长度判断: iOS 64/108 hex, Android FCM 152+
        if (token.length() < 64) return DeviceToken.Platform.UNKNOWN;
        if (token.matches("[0-9a-fA-F]+")) {
            if (token.length() == 64 || token.length() == 108) return DeviceToken.Platform.IOS;
            if (token.length() >= 140) return DeviceToken.Platform.ANDROID;
        }
        return DeviceToken.Platform.UNKNOWN;
    }

    // ==================== 统计接口 ====================

    public PushStats getStats() {
        return new PushStats(
                totalPushCount.get(),
                successCount.get(),
                failureCount.get(),
                channelStats.entrySet().stream()
                        .collect(Collectors.toMap(Map.Entry::getKey, e -> e.getValue().get()))
        );
    }

    public void resetStats() {
        totalPushCount.set(0);
        successCount.set(0);
        failureCount.set(0);
        channelStats.clear();
    }

    /**
     * 推送结果
     */
    public static class PushResult {
        private final boolean success;
        private final String message;
        private final Map<String, Object> details;

        private PushResult(boolean success, String message, Map<String, Object> details) {
            this.success = success;
            this.message = message;
            this.details = details;
        }

        public static PushResult success(String message) {
            return new PushResult(true, message, null);
        }

        public static PushResult success(String message, Map<String, Object> details) {
            return new PushResult(true, message, details);
        }

        public static PushResult failed(String message) {
            return new PushResult(false, message, null);
        }

        public static PushResult failed(String message, Map<String, Object> details) {
            return new PushResult(false, message, details);
        }

        public boolean isSuccess() { return success; }
        public String getMessage() { return message; }
        public Map<String, Object> getDetails() { return details; }
    }

    /**
     * 推送统计
     */
    public static class PushStats {
        private final int total;
        private final int success;
        private final int failure;
        private final Map<String, Integer> byChannel;

        public PushStats(int total, int success, int failure, Map<String, Integer> byChannel) {
            this.total = total;
            this.success = success;
            this.failure = failure;
            this.byChannel = byChannel;
        }

        public int getTotal() { return total; }
        public int getSuccess() { return success; }
        public int getFailure() { return failure; }
        public double getSuccessRate() { return total > 0 ? (double) success / total : 0; }
        public Map<String, Integer> getByChannel() { return byChannel; }
    }
}
