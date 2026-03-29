package com.im.webhook.service;

import com.im.webhook.model.WebhookConfig;
import com.im.webhook.model.WebhookEvent;
import com.im.webhook.model.WebhookDelivery;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.concurrent.*;

/**
 * Webhook重试服务
 * 管理失败事件的重试调度
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookRetryService {
    
    private final WebhookService webhookService;
    private final WebhookEventRepository eventRepository;
    private final WebhookDeliveryRepository deliveryRepository;
    
    /** 重试调度线程池 */
    private ScheduledExecutorService retryScheduler;
    
    /** 正在重试的任务 */
    private final ConcurrentHashMap<String, ScheduledFuture<?>> retryTasks = new ConcurrentHashMap<>();
    
    @PostConstruct
    public void init() {
        retryScheduler = Executors.newScheduledThreadPool(5, r -> {
            Thread t = new Thread(r, "webhook-retry-" + ThreadLocalRandom.current().nextInt(100));
            t.setDaemon(true);
            return t;
        });
        
        log.info("Webhook重试服务初始化完成");
    }
    
    /**
     * 调度重试
     */
    public void scheduleRetry(WebhookConfig config, WebhookEvent event, WebhookDelivery failedDelivery) {
        int retryCount = event.getRetryCount();
        
        // 计算下次重试时间
        long delayMs = config.getRetryPolicy().calculateRetryDelay(retryCount);
        
        LocalDateTime nextRetryAt = LocalDateTime.now().plusNanos(delayMs * 1_000_000);
        failedDelivery.setNextRetryAt(nextRetryAt);
        deliveryRepository.save(failedDelivery);
        
        // 取消已存在的重试任务
        cancelRetry(event.getEventId());
        
        // 调度新的重试任务
        ScheduledFuture<?> future = retryScheduler.schedule(() -> {
            executeRetry(config, event);
            retryTasks.remove(event.getEventId());
        }, delayMs, TimeUnit.MILLISECONDS);
        
        retryTasks.put(event.getEventId(), future);
        
        log.info("Webhook重试已调度: eventId={}, retryCount={}, delay={}ms, nextRetryAt={}",
                event.getEventId(), retryCount, delayMs, nextRetryAt);
    }
    
    /**
     * 执行重试
     */
    private void executeRetry(WebhookConfig config, WebhookEvent event) {
        log.info("开始执行Webhook重试: eventId={}, retryCount={}", 
                event.getEventId(), event.getRetryCount());
        
        // 调用WebhookService的投递逻辑
        // 这里简化处理，实际应该复用投递逻辑
        webhookService.processRetryEvent(config, event);
    }
    
    /**
     * 取消重试任务
     */
    public void cancelRetry(String eventId) {
        ScheduledFuture<?> future = retryTasks.remove(eventId);
        if (future != null) {
            future.cancel(false);
            log.debug("取消Webhook重试任务: eventId={}", eventId);
        }
    }
    
    /**
     * 立即重试指定事件
     */
    public boolean retryNow(String eventId) {
        WebhookEvent event = eventRepository.findById(eventId).orElse(null);
        if (event == null || !event.getStatus().canRetry()) {
            return false;
        }
        
        // 取消已存在的重试任务
        cancelRetry(eventId);
        
        // 立即执行
        retryScheduler.execute(() -> {
            WebhookConfig config = webhookService.getWebhook(event.getWebhookId());
            executeRetry(config, event);
        });
        
        return true;
    }
    
    /**
     * 批量重试失败事件
     */
    public int retryFailedEvents(String webhookId, LocalDateTime start, LocalDateTime end) {
        // 查询失败的、可以重试的事件
        // 简化实现
        log.info("批量重试失败事件: webhookId={}, start={}, end={}", webhookId, start, end);
        return 0;
    }
    
    /**
     * 清理过期重试任务
     */
    @Scheduled(cron = "0 0 */6 * * ?") // 每6小时执行
    public void cleanupStaleRetries() {
        LocalDateTime cutoff = LocalDateTime.now().minusHours(24);
        
        retryTasks.entrySet().removeIf(entry -> {
            String eventId = entry.getKey();
            WebhookEvent event = eventRepository.findById(eventId).orElse(null);
            
            if (event == null) {
                entry.getValue().cancel(false);
                return true;
            }
            
            // 如果事件已经是终态或者超过24小时，取消任务
            if (event.getStatus().isTerminal() || event.getCreatedAt().isBefore(cutoff)) {
                entry.getValue().cancel(false);
                return true;
            }
            
            return false;
        });
        
        log.debug("清理过期重试任务完成，当前活跃任务数: {}", retryTasks.size());
    }
    
    /**
     * 获取重试统计
     */
    public Map<String, Object> getRetryStats() {
        Map<String, Object> stats = new ConcurrentHashMap<>();
        stats.put("activeRetryTasks", retryTasks.size());
        stats.put("schedulerActive", !retryScheduler.isShutdown());
        return stats;
    }
}
