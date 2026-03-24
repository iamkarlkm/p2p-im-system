package com.im.server.webhook;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.*;

/**
 * Webhook重试处理器
 */
public class WebhookRetryHandler {

    private static final Logger logger = LoggerFactory.getLogger(WebhookRetryHandler.class);

    private final WebhookConfig config;
    private final WebhookHttpClient httpClient;

    // 重试任务队列
    private final Map<String, RetryTask> retryTasks = new ConcurrentHashMap<>();

    // 调度线程池
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(2);

    // 重试间隔序列（毫秒）
    private static final long[] RETRY_DELAYS = {
            1000,      // 1秒
            5000,      // 5秒
            30000,     // 30秒
            120000,    // 2分钟
            600000,    // 10分钟
            3600000    // 1小时
    };

    public WebhookRetryHandler(WebhookConfig config, WebhookHttpClient httpClient) {
        this.config = config;
        this.httpClient = httpClient;
    }

    /**
     * 调度重试
     */
    public void scheduleRetry(WebhookEvent event, WebhookDispatcher.WebhookSubscription subscription) {
        if (event.getRetryCount() >= config.getMaxRetries()) {
            logger.warn("超过最大重试次数，放弃: {}", event.getEventId());
            updateEventStatus(event, "EXPIRED");
            return;
        }

        String taskKey = event.getEventId() + "-" + subscription.getId();
        if (retryTasks.containsKey(taskKey)) {
            return;
        }

        int retryIndex = Math.min(event.getRetryCount(), RETRY_DELAYS.length - 1);
        long delay = RETRY_DELAYS[retryIndex];

        RetryTask task = new RetryTask(event, subscription, taskKey);
        retryTasks.put(taskKey, task);

        scheduler.schedule(() -> executeRetry(task), delay, TimeUnit.MILLISECONDS);

        logger.info("调度重试: {}, 延迟: {}ms, 重试次数: {}",
                event.getEventId(), delay, event.getRetryCount() + 1);
    }

    /**
     * 执行重试
     */
    private void executeRetry(RetryTask task) {
        try {
            WebhookEvent event = task.getEvent();
            WebhookDispatcher.WebhookSubscription subscription = task.getSubscription();

            // 生成签名
            String signature = WebhookSignatureVerifier.sign(
                    event.toJson(), subscription.getSecret());

            Map<String, String> headers = Map.of(
                    "Content-Type", "application/json",
                    "X-Webhook-Event", event.getEventType(),
                    "X-Webhook-Delivery", event.getEventId(),
                    "X-Webhook-Signature", signature,
                    "X-Webhook-Timestamp", String.valueOf(event.getTimestamp()),
                    "X-Webhook-Retry", String.valueOf(event.getRetryCount())
            );

            WebhookHttpClient.HttpResponse response = httpClient.post(
                    subscription.getUrl(), event.toJson(), headers);

            if (response.isSuccess()) {
                logger.info("重试成功: {}", event.getEventId());
                retryTasks.remove(task.getTaskKey());
            } else {
                event.incrementRetry();
                if (event.getRetryCount() >= config.getMaxRetries()) {
                    logger.warn("重试次数耗尽: {}", event.getEventId());
                    retryTasks.remove(task.getTaskKey());
                } else {
                    // 继续调度下一次重试
                    scheduleRetry(event, subscription);
                }
            }

        } catch (Exception e) {
            logger.error("重试执行异常: {}", task.getEvent().getEventId(), e);
        }
    }

    private void updateEventStatus(WebhookEvent event, String status) {
        event.setStatus(status);
    }

    /**
     * 获取待重试任务数
     */
    public int getPendingRetryCount() {
        return retryTasks.size();
    }

    /**
     * 清理任务
     */
    public void cleanup() {
        retryTasks.clear();
        scheduler.shutdown();
    }

    /**
     * 重试任务
     */
    private static class RetryTask {
        private final WebhookEvent event;
        private final WebhookDispatcher.WebhookSubscription subscription;
        private final String taskKey;

        public RetryTask(WebhookEvent event, WebhookDispatcher.WebhookSubscription subscription, String taskKey) {
            this.event = event;
            this.subscription = subscription;
            this.taskKey = taskKey;
        }

        public WebhookEvent getEvent() { return event; }
        public WebhookDispatcher.WebhookSubscription getSubscription() { return subscription; }
        public String getTaskKey() { return taskKey; }
    }
}
