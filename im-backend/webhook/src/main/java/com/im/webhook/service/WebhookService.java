package com.im.webhook.service;

import com.im.webhook.model.WebhookConfig;
import com.im.webhook.model.WebhookEvent;
import com.im.webhook.model.WebhookDelivery;
import com.im.webhook.util.WebhookSignatureUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;

/**
 * Webhook服务
 * 管理Webhook配置、触发事件、处理回调投递
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebhookService {
    
    private final WebhookConfigRepository configRepository;
    private final WebhookEventRepository eventRepository;
    private final WebhookDeliveryRepository deliveryRepository;
    private final WebhookRetryService retryService;
    private final RestTemplate restTemplate;
    
    /** 内存缓存：webhookId -> WebhookConfig */
    private final ConcurrentHashMap<String, WebhookConfig> configCache = new ConcurrentHashMap<>();
    
    /** 内存缓存：appId -> List<WebhookConfig> */
    private final ConcurrentHashMap<String, List<WebhookConfig>> appWebhookCache = new ConcurrentHashMap<>();
    
    /** 事件队列 */
    private final BlockingQueue<WebhookEvent> eventQueue = new LinkedBlockingQueue<>(10000);
    
    /** 是否运行中 */
    private volatile boolean running = true;
    
    /** 事件处理器线程池 */
    private ExecutorService eventProcessorPool;
    
    @PostConstruct
    public void init() {
        // 初始化线程池
        eventProcessorPool = Executors.newFixedThreadPool(10, r -> {
            Thread t = new Thread(r, "webhook-processor-" + ThreadLocalRandom.current().nextInt(100));
            t.setDaemon(true);
            return t;
        });
        
        // 加载所有活跃配置到缓存
        refreshConfigCache();
        
        // 启动事件处理器
        startEventProcessor();
        
        log.info("Webhook服务初始化完成，缓存了 {} 个配置", configCache.size());
    }
    
    /**
     * 刷新配置缓存
     */
    public void refreshConfigCache() {
        List<WebhookConfig> activeConfigs = configRepository.findByStatus(WebhookConfig.WebhookStatus.ACTIVE);
        
        configCache.clear();
        appWebhookCache.clear();
        
        for (WebhookConfig config : activeConfigs) {
            configCache.put(config.getWebhookId(), config);
            appWebhookCache.computeIfAbsent(config.getAppId(), k -> new ArrayList<>()).add(config);
        }
    }
    
    /**
     * 创建Webhook配置
     */
    public WebhookConfig createWebhook(WebhookConfig config) {
        config.setWebhookId(UUID.randomUUID().toString().replace("-", ""));
        config.setCreatedAt(LocalDateTime.now());
        config.setUpdatedAt(LocalDateTime.now());
        config.setStatus(WebhookConfig.WebhookStatus.ACTIVE);
        config.setTriggerCount(0L);
        config.setFailureCount(0L);
        
        // 默认重试策略
        if (config.getRetryPolicy() == null) {
            config.setRetryPolicy(WebhookConfig.RetryPolicy.builder()
                    .maxRetries(3)
                    .retryIntervalSeconds(5)
                    .strategy(WebhookConfig.RetryPolicy.RetryStrategy.EXPONENTIAL)
                    .build());
        }
        
        // 默认超时
        if (config.getTimeoutSeconds() == null) {
            config.setTimeoutSeconds(30);
        }
        
        configRepository.save(config);
        refreshConfigCache();
        
        log.info("创建Webhook配置成功: {}, appId: {}", config.getWebhookId(), config.getAppId());
        return config;
    }
    
    /**
     * 更新Webhook配置
     */
    public WebhookConfig updateWebhook(String webhookId, WebhookConfig update) {
        WebhookConfig existing = configRepository.findById(webhookId)
                .orElseThrow(() -> new WebhookNotFoundException("Webhook not found: " + webhookId));
        
        if (update.getName() != null) existing.setName(update.getName());
        if (update.getCallbackUrl() != null) existing.setCallbackUrl(update.getCallbackUrl());
        if (update.getSecret() != null) existing.setSecret(update.getSecret());
        if (update.getEventTypes() != null) existing.setEventTypes(update.getEventTypes());
        if (update.getHeaders() != null) existing.setHeaders(update.getHeaders());
        if (update.getRetryPolicy() != null) existing.setRetryPolicy(update.getRetryPolicy());
        if (update.getTimeoutSeconds() != null) existing.setTimeoutSeconds(update.getTimeoutSeconds());
        if (update.getDescription() != null) existing.setDescription(update.getDescription());
        
        existing.setUpdatedAt(LocalDateTime.now());
        configRepository.save(existing);
        refreshConfigCache();
        
        log.info("更新Webhook配置成功: {}", webhookId);
        return existing;
    }
    
    /**
     * 删除Webhook配置
     */
    public void deleteWebhook(String webhookId) {
        configRepository.deleteById(webhookId);
        configCache.remove(webhookId);
        refreshConfigCache();
        log.info("删除Webhook配置: {}", webhookId);
    }
    
    /**
     * 获取Webhook配置
     */
    public WebhookConfig getWebhook(String webhookId) {
        return configCache.getOrDefault(webhookId,
                configRepository.findById(webhookId)
                        .orElseThrow(() -> new WebhookNotFoundException("Webhook not found: " + webhookId)));
    }
    
    /**
     * 获取应用的所有Webhook
     */
    public List<WebhookConfig> getWebhooksByApp(String appId) {
        return appWebhookCache.getOrDefault(appId, 
                configRepository.findByAppIdAndStatus(appId, WebhookConfig.WebhookStatus.ACTIVE));
    }
    
    /**
     * 触发Webhook事件（异步）
     */
    @Async("webhookTaskExecutor")
    public void triggerEvent(String appId, String eventType, Map<String, Object> payload) {
        List<WebhookConfig> webhooks = appWebhookCache.get(appId);
        if (webhooks == null || webhooks.isEmpty()) {
            return;
        }
        
        for (WebhookConfig config : webhooks) {
            // 检查是否订阅了该事件类型
            if (!isSubscribed(config, eventType)) {
                continue;
            }
            
            // 创建事件记录
            WebhookEvent event = WebhookEvent.builder()
                    .eventId(UUID.randomUUID().toString().replace("-", ""))
                    .webhookId(config.getWebhookId())
                    .appId(appId)
                    .eventType(eventType)
                    .payload(payload)
                    .status(WebhookEvent.EventStatus.PENDING)
                    .createdAt(LocalDateTime.now())
                    .retryCount(0)
                    .requestId(UUID.randomUUID().toString())
                    .build();
            
            eventRepository.save(event);
            
            // 加入处理队列
            if (!eventQueue.offer(event)) {
                log.warn("Webhook事件队列已满，丢弃事件: {}", event.getEventId());
            }
        }
    }
    
    /**
     * 启动事件处理器
     */
    private void startEventProcessor() {
        for (int i = 0; i < 5; i++) {
            eventProcessorPool.submit(() -> {
                while (running && !Thread.currentThread().isInterrupted()) {
                    try {
                        WebhookEvent event = eventQueue.poll(1, TimeUnit.SECONDS);
                        if (event != null) {
                            processEvent(event);
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    } catch (Exception e) {
                        log.error("处理Webhook事件时出错", e);
                    }
                }
            });
        }
    }
    
    /**
     * 处理单个事件
     */
    private void processEvent(WebhookEvent event) {
        WebhookConfig config = configCache.get(event.getWebhookId());
        if (config == null) {
            log.warn("Webhook配置不存在或已删除: {}", event.getWebhookId());
            event.setStatus(WebhookEvent.EventStatus.CANCELLED);
            eventRepository.save(event);
            return;
        }
        
        // 更新状态为发送中
        event.setStatus(WebhookEvent.EventStatus.SENDING);
        event.setSentAt(LocalDateTime.now());
        eventRepository.save(event);
        
        // 执行投递
        WebhookDelivery delivery = deliver(config, event);
        
        // 更新事件状态
        if (delivery.isSuccess()) {
            event.setStatus(WebhookEvent.EventStatus.SUCCESS);
            event.setCompletedAt(LocalDateTime.now());
            event.setHttpStatusCode(delivery.getResponseStatusCode());
            event.setResponseBody(delivery.getResponseBody());
            
            // 更新统计
            config.setTriggerCount(config.getTriggerCount() + 1);
            config.setLastTriggeredAt(LocalDateTime.now());
            configRepository.save(config);
        } else if (delivery.shouldRetry() && event.getRetryCount() < config.getRetryPolicy().getMaxRetries()) {
            // 需要重试
            event.setStatus(WebhookEvent.EventStatus.RETRYING);
            event.setRetryCount(event.getRetryCount() + 1);
            event.setErrorMessage(delivery.getErrorDetails());
            
            // 调度重试
            retryService.scheduleRetry(config, event, delivery);
        } else {
            // 最终失败
            event.setStatus(WebhookEvent.EventStatus.FAILED);
            event.setCompletedAt(LocalDateTime.now());
            event.setErrorMessage(delivery.getErrorDetails());
            
            // 更新失败统计
            config.setFailureCount(config.getFailureCount() + 1);
            configRepository.save(config);
        }
        
        eventRepository.save(event);
    }
    
    /**
     * 执行回调投递
     */
    private WebhookDelivery deliver(WebhookConfig config, WebhookEvent event) {
        String deliveryId = UUID.randomUUID().toString().replace("-", "");
        long startTime = System.currentTimeMillis();
        
        WebhookDelivery delivery = WebhookDelivery.builder()
                .deliveryId(deliveryId)
                .eventId(event.getEventId())
                .webhookId(config.getWebhookId())
                .attemptNumber(event.getRetryCount() + 1)
                .requestMethod("POST")
                .requestUrl(config.getCallbackUrl())
                .deliveredAt(LocalDateTime.now())
                .build();
        
        try {
            // 构建请求体
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("event_id", event.getEventId());
            requestBody.put("event_type", event.getEventType());
            requestBody.put("timestamp", System.currentTimeMillis() / 1000);
            requestBody.put("payload", event.getPayload());
            
            // 生成签名
            String signature = WebhookSignatureUtil.sign(requestBody, config.getSecret());
            
            // 构建请求头
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Webhook-ID", config.getWebhookId());
            headers.set("X-Event-ID", event.getEventId());
            headers.set("X-Request-ID", event.getRequestId());
            headers.set("X-Webhook-Signature", signature);
            headers.set("X-Webhook-Timestamp", String.valueOf(System.currentTimeMillis() / 1000));
            headers.set("User-Agent", "IM-Webhook/1.0");
            
            // 添加自定义请求头
            if (config.getHeaders() != null) {
                config.getHeaders().forEach(headers::set);
            }
            
            delivery.setRequestHeaders(headers.toSingleValueMap());
            delivery.setRequestSignature(signature);
            
            // 发送请求
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                    config.getCallbackUrl(),
                    HttpMethod.POST,
                    entity,
                    String.class
            );
            
            // 记录响应
            long responseTime = System.currentTimeMillis() - startTime;
            delivery.setResponseTimeMs(responseTime);
            delivery.setResponseStatusCode(response.getStatusCodeValue());
            delivery.setResponseBody(response.getBody());
            delivery.setStatus(WebhookDelivery.DeliveryStatus.SUCCESS);
            
            log.debug("Webhook投递成功: eventId={}, status={}, time={}ms", 
                    event.getEventId(), response.getStatusCodeValue(), responseTime);
            
        } catch (ResourceAccessException e) {
            handleDeliveryError(delivery, e, startTime, true);
        } catch (Exception e) {
            handleDeliveryError(delivery, e, startTime, false);
        }
        
        deliveryRepository.save(delivery);
        return delivery;
    }
    
    /**
     * 处理投递错误
     */
    private void handleDeliveryError(WebhookDelivery delivery, Exception e, long startTime, boolean retryable) {
        long responseTime = System.currentTimeMillis() - startTime;
        delivery.setResponseTimeMs(responseTime);
        delivery.setErrorType(WebhookDelivery.ErrorType.fromException(e));
        delivery.setErrorDetails(e.getMessage());
        delivery.setStatus(retryable ? WebhookDelivery.DeliveryStatus.FAILED_RETRYABLE : WebhookDelivery.DeliveryStatus.FAILED_FINAL);
        
        log.warn("Webhook投递失败: eventId={}, error={}", delivery.getEventId(), e.getMessage());
    }
    
    /**
     * 检查是否订阅了事件类型
     */
    private boolean isSubscribed(WebhookConfig config, String eventType) {
        if (config.getEventTypes() == null || config.getEventTypes().isEmpty()) {
            return true; // 空列表表示订阅所有事件
        }
        return config.getEventTypes().contains(eventType) || config.getEventTypes().contains("*");
    }
    
    /**
     * 获取事件统计
     */
    public Map<String, Object> getEventStats(String webhookId, LocalDateTime start, LocalDateTime end) {
        return eventRepository.getStats(webhookId, start, end);
    }
    
    /**
     * 关闭服务
     */
    public void shutdown() {
        running = false;
        if (eventProcessorPool != null) {
            eventProcessorPool.shutdown();
        }
    }
}
