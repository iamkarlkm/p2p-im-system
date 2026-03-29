package com.im.server.webhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * Webhook调度器
 * 负责管理Webhook订阅、分发事件、重试机制
 */
@Component
public class WebhookDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(WebhookDispatcher.class);

    // 订阅者列表（按事件类型分组）
    private final Map<String, List<WebhookSubscription>> subscriptions = new ConcurrentHashMap<>();

    // 事件队列
    private final BlockingQueue<WebhookEvent> eventQueue = new LinkedBlockingQueue<>(10000);

    // 投递任务线程池
    private final ExecutorService deliveryExecutor = Executors.newFixedThreadPool(10);

    // HTTP客户端（简化实现）
    private final WebhookHttpClient httpClient;

    // 重试处理器
    private final WebhookRetryHandler retryHandler;

    // Webhook配置
    private final WebhookConfig config;

    // 统计信息
    private final Map<String, WebhookStats> statsMap = new ConcurrentHashMap<>();

    public WebhookDispatcher(WebhookConfig config, WebhookRetryHandler retryHandler) {
        this.config = config;
        this.retryHandler = retryHandler;
        this.httpClient = new WebhookHttpClient(config);
        startEventProcessor();
    }

    /**
     * 启动事件处理器
     */
    private void startEventProcessor() {
        // 启动事件处理线程
        for (int i = 0; i < config.getProcessorThreads(); i++) {
            deliveryExecutor.submit(new EventProcessor());
        }
        logger.info("Webhook事件处理器已启动");
    }

    /**
     * 订阅Webhook
     */
    public String subscribe(String url, String secret, String... eventTypes) {
        String subscriptionId = UUID.randomUUID().toString();

        WebhookSubscription subscription = new WebhookSubscription();
        subscription.setId(subscriptionId);
        subscription.setUrl(url);
        subscription.setSecret(secret);
        subscription.setEventTypes(Arrays.asList(eventTypes));
        subscription.setActive(true);
        subscription.setCreatedAt(System.currentTimeMillis());

        // 按事件类型注册订阅
        for (String eventType : eventTypes) {
            subscriptions.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>())
                    .add(subscription);
        }

        logger.info("订阅成功: {} -> {}", subscriptionId, url);

        return subscriptionId;
    }

    /**
     * 取消订阅
     */
    public boolean unsubscribe(String subscriptionId) {
        for (List<WebhookSubscription> subs : subscriptions.values()) {
            subs.removeIf(sub -> sub.getId().equals(subscriptionId));
        }
        logger.info("取消订阅: {}", subscriptionId);
        return true;
    }

    /**
     * 触发Webhook事件
     */
    public void dispatch(WebhookEvent event) {
        if (event == null || event.getEventType() == null) {
            logger.warn("无效的事件，跳过");
            return;
        }

        event.setSource("IM-SERVER");

        // 加入事件队列
        boolean offered = eventQueue.offer(event);
        if (!offered) {
            logger.error("事件队列已满，跳过事件: {}", event.getEventId());
        }
    }

    /**
     * 同步触发Webhook事件
     */
    public List<WebhookDeliveryResult> dispatchSync(WebhookEvent event) {
        if (event == null || event.getEventType() == null) {
            return Collections.emptyList();
        }

        event.setSource("IM-SERVER");

        List<WebhookSubscription> subs = getSubscriptions(event.getEventType());
        if (subs.isEmpty()) {
            logger.debug("没有订阅者: {}", event.getEventType());
            return Collections.emptyList();
        }

        List<WebhookDeliveryResult> results = Collections.synchronizedList(new ArrayList<>());

        // 并行投递
        subs.parallelStream().forEach(sub -> {
            if (!sub.isActive()) return;

            WebhookDeliveryResult result = deliverEvent(sub, event);
            results.add(result);

            // 更新统计
            updateStats(sub.getId(), result);
        });

        return results;
    }

    /**
     * 获取事件类型的订阅者
     */
    private List<WebhookSubscription> getSubscriptions(String eventType) {
        List<WebhookSubscription> result = new ArrayList<>();

        // 精确匹配
        List<WebhookSubscription> exact = subscriptions.get(eventType);
        if (exact != null) {
            result.addAll(exact);
        }

        // 通配符匹配
        List<WebhookSubscription> wildcard = subscriptions.get("*");
        if (wildcard != null) {
            result.addAll(wildcard);
        }

        return result;
    }

    /**
     * 投递事件到订阅者
     */
    private WebhookDeliveryResult deliverEvent(WebhookSubscription subscription, WebhookEvent event) {
        WebhookDeliveryResult result = new WebhookDeliveryResult();
        result.setSubscriptionId(subscription.getId());
        result.setEventId(event.getEventId());
        result.setUrl(subscription.getUrl());
        result.setAttemptTime(System.currentTimeMillis());

        try {
            // 生成签名
            String signature = WebhookSignatureVerifier.sign(
                    event.toJson(), subscription.getSecret());

            // 发送HTTP请求
            Map<String, String> headers = new HashMap<>();
            headers.put("Content-Type", "application/json");
            headers.put("X-Webhook-Event", event.getEventType());
            headers.put("X-Webhook-Delivery", event.getEventId());
            headers.put("X-Webhook-Signature", signature);
            headers.put("X-Webhook-Timestamp", String.valueOf(event.getTimestamp()));

            // 发送请求
            WebhookHttpClient.HttpResponse response = httpClient.post(
                    subscription.getUrl(), event.toJson(), headers);

            result.setStatusCode(response.getStatusCode());
            result.setSuccess(response.isSuccess());
            result.setResponseBody(response.getBody());
            result.setDuration(System.currentTimeMillis() - result.getAttemptTime());

            if (!result.isSuccess()) {
                result.setError("HTTP " + response.getStatusCode());
            }

        } catch (Exception e) {
            logger.error("投递事件失败: {}", subscription.getId(), e);
            result.setSuccess(false);
            result.setError(e.getMessage());
            result.setDuration(System.currentTimeMillis() - result.getAttemptTime());
        }

        return result;
    }

    /**
     * 更新统计信息
     */
    private void updateStats(String subscriptionId, WebhookDeliveryResult result) {
        WebhookStats stats = statsMap.computeIfAbsent(subscriptionId, k -> new WebhookStats(subscriptionId));

        if (result.isSuccess()) {
            stats.incrementSuccess();
        } else {
            stats.incrementFailure();
        }

        if (result.getDuration() > 0) {
            stats.addLatency(result.getDuration());
        }
    }

    /**
     * 事件处理器
     */
    private class EventProcessor implements Runnable {
        @Override
        public void run() {
            Thread.currentThread().setName("webhook-processor-" + Thread.currentThread().getId());

            while (!Thread.currentThread().isInterrupted()) {
                try {
                    WebhookEvent event = eventQueue.poll(1, TimeUnit.SECONDS);
                    if (event == null) continue;

                    List<WebhookSubscription> subs = getSubscriptions(event.getEventType());
                    if (subs.isEmpty()) continue;

                    // 异步投递
                    for (WebhookSubscription sub : subs) {
                        if (!sub.isActive()) continue;

                        final WebhookSubscription subscription = sub;
                        deliveryExecutor.submit(() -> {
                            try {
                                WebhookDeliveryResult result = deliverEvent(subscription, event);
                                updateStats(subscription.getId(), result);

                                // 失败时加入重试队列
                                if (!result.isSuccess() && subscription.isRetryEnabled()) {
                                    retryHandler.scheduleRetry(event, subscription);
                                }
                            } catch (Exception e) {
                                logger.error("投递事件异常: {}", subscription.getId(), e);
                            }
                        });
                    }

                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                } catch (Exception e) {
                    logger.error("事件处理异常", e);
                }
            }
        }
    }

    // ==================== 统计和管理 ====================

    /**
     * 获取统计信息
     */
    public WebhookStats getStats(String subscriptionId) {
        return statsMap.get(subscriptionId);
    }

    /**
     * 获取所有统计
     */
    public Map<String, WebhookStats> getAllStats() {
        return new HashMap<>(statsMap);
    }

    /**
     * 获取所有订阅
     */
    public List<WebhookSubscription> getAllSubscriptions() {
        return subscriptions.values().stream()
                .flatMap(List::stream)
                .distinct()
                .collect(Collectors.toList());
    }

    /**
     * 获取订阅
     */
    public WebhookSubscription getSubscription(String subscriptionId) {
        for (List<WebhookSubscription> subs : subscriptions.values()) {
            for (WebhookSubscription sub : subs) {
                if (sub.getId().equals(subscriptionId)) {
                    return sub;
                }
            }
        }
        return null;
    }

    /**
     * 暂停订阅
     */
    public boolean pauseSubscription(String subscriptionId) {
        WebhookSubscription sub = getSubscription(subscriptionId);
        if (sub != null) {
            sub.setActive(false);
            return true;
        }
        return false;
    }

    /**
     * 恢复订阅
     */
    public boolean resumeSubscription(String subscriptionId) {
        WebhookSubscription sub = getSubscription(subscriptionId);
        if (sub != null) {
            sub.setActive(true);
            return true;
        }
        return false;
    }

    // ==================== 内部类 ====================

    /**
     * Webhook订阅
     */
    public static class WebhookSubscription {
        private String id;
        private String url;
        private String secret;
        private List<String> eventTypes;
        private boolean active = true;
        private boolean retryEnabled = true;
        private long createdAt;
        private long lastDeliveryTime;
        private int successCount;
        private int failureCount;

        public String getId() { return id; }
        public void setId(String id) { this.id = id; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public String getSecret() { return secret; }
        public void setSecret(String secret) { this.secret = secret; }
        public List<String> getEventTypes() { return eventTypes; }
        public void setEventTypes(List<String> eventTypes) { this.eventTypes = eventTypes; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
        public boolean isRetryEnabled() { return retryEnabled; }
        public void setRetryEnabled(boolean retryEnabled) { this.retryEnabled = retryEnabled; }
        public long getCreatedAt() { return createdAt; }
        public void setCreatedAt(long createdAt) { this.createdAt = createdAt; }
        public long getLastDeliveryTime() { return lastDeliveryTime; }
        public void setLastDeliveryTime(long lastDeliveryTime) { this.lastDeliveryTime = lastDeliveryTime; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        public void incrementSuccess() { this.successCount++; this.lastDeliveryTime = System.currentTimeMillis(); }
        public void incrementFailure() { this.failureCount++; this.lastDeliveryTime = System.currentTimeMillis(); }
    }

    /**
     * 投递结果
     */
    public static class WebhookDeliveryResult {
        private String subscriptionId;
        private String eventId;
        private String url;
        private long attemptTime;
        private boolean success;
        private int statusCode;
        private String responseBody;
        private String error;
        private long duration;

        public String getSubscriptionId() { return subscriptionId; }
        public void setSubscriptionId(String subscriptionId) { this.subscriptionId = subscriptionId; }
        public String getEventId() { return eventId; }
        public void setEventId(String eventId) { this.eventId = eventId; }
        public String getUrl() { return url; }
        public void setUrl(String url) { this.url = url; }
        public long getAttemptTime() { return attemptTime; }
        public void setAttemptTime(long attemptTime) { this.attemptTime = attemptTime; }
        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public int getStatusCode() { return statusCode; }
        public void setStatusCode(int statusCode) { this.statusCode = statusCode; }
        public String getResponseBody() { return responseBody; }
        public void setResponseBody(String responseBody) { this.responseBody = responseBody; }
        public String getError() { return error; }
        public void setError(String error) { this.error = error; }
        public long getDuration() { return duration; }
        public void setDuration(long duration) { this.duration = duration; }
    }

    /**
     * Webhook统计
     */
    public static class WebhookStats {
        private final String subscriptionId;
        private int successCount;
        private int failureCount;
        private long totalLatency;
        private long maxLatency;
        private long minLatency;
        private int latencyCount;
        private long lastUpdated;

        public WebhookStats(String subscriptionId) {
            this.subscriptionId = subscriptionId;
            this.lastUpdated = System.currentTimeMillis();
        }

        public String getSubscriptionId() { return subscriptionId; }
        public int getSuccessCount() { return successCount; }
        public void setSuccessCount(int successCount) { this.successCount = successCount; }
        public int getFailureCount() { return failureCount; }
        public void setFailureCount(int failureCount) { this.failureCount = failureCount; }
        public long getTotalLatency() { return totalLatency; }
        public long getMaxLatency() { return maxLatency; }
        public long getMinLatency() { return minLatency; }
        public int getLatencyCount() { return latencyCount; }
        public long getLastUpdated() { return lastUpdated; }

        public void incrementSuccess() { successCount++; lastUpdated = System.currentTimeMillis(); }
        public void incrementFailure() { failureCount++; lastUpdated = System.currentTimeMillis(); }

        public void addLatency(long latency) {
            totalLatency += latency;
            if (latency > maxLatency) maxLatency = latency;
            if (minLatency == 0 || latency < minLatency) minLatency = latency;
            latencyCount++;
            lastUpdated = System.currentTimeMillis();
        }

        public double getAverageLatency() {
            return latencyCount > 0 ? (double) totalLatency / latencyCount : 0;
        }

        public double getSuccessRate() {
            int total = successCount + failureCount;
            return total > 0 ? (double) successCount / total : 0;
        }
    }
}
