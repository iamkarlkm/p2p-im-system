package com.im.system.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.im.system.entity.WebhookEventEntity;
import com.im.system.repository.WebhookEventRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Webhook 事件服务
 * 负责事件的创建、投递、重试、统计等
 */
@Service
public class WebhookEventService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookEventService.class);
    
    @Autowired
    private WebhookEventRepository webhookEventRepository;
    
    @Autowired
    private RestTemplate restTemplate;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
    
    // ===================== 事件创建 =====================
    
    /**
     * 创建新的 Webhook 事件
     */
    @Transactional
    public WebhookEventEntity createEvent(String eventType, String eventData, UUID subscriptionId, 
                                         String webhookUrl, String webhookSecret, Integer priority) {
        WebhookEventEntity event = new WebhookEventEntity();
        event.setEventType(eventType);
        event.setEventData(eventData);
        event.setSubscriptionId(subscriptionId);
        event.setWebhookUrl(webhookUrl);
        event.setWebhookSecret(webhookSecret);
        event.setPriority(priority != null ? priority : 5);
        event.setEventTimestamp(LocalDateTime.now());
        event.setDeliveryStatus(WebhookEventEntity.DeliveryStatus.PENDING);
        
        WebhookEventEntity saved = webhookEventRepository.save(event);
        logger.info("Created webhook event: {} for URL: {}", saved.getId(), webhookUrl);
        return saved;
    }
    
    /**
     * 批量创建事件
     */
    @Transactional
    public List<WebhookEventEntity> createEvents(List<Map<String, Object>> events) {
        List<WebhookEventEntity> savedEvents = new ArrayList<>();
        for (Map<String, Object> eventData : events) {
            WebhookEventEntity event = new WebhookEventEntity();
            event.setEventType((String) eventData.get("eventType"));
            event.setEventData((String) eventData.get("eventData"));
            event.setSubscriptionId((UUID) eventData.get("subscriptionId"));
            event.setWebhookUrl((String) eventData.get("webhookUrl"));
            event.setPriority((Integer) eventData.getOrDefault("priority", 5));
            event.setEventTimestamp(LocalDateTime.now());
            event.setDeliveryStatus(WebhookEventEntity.DeliveryStatus.PENDING);
            savedEvents.add(webhookEventRepository.save(event));
        }
        logger.info("Created {} webhook events", savedEvents.size());
        return savedEvents;
    }
    
    // ===================== 事件投递 =====================
    
    /**
     * 异步投递事件
     */
    @Async
    @Transactional
    public CompletableFuture<Boolean> deliverEventAsync(UUID eventId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return deliverEvent(eventId);
            } catch (Exception e) {
                logger.error("Async delivery failed for event: {}", eventId, e);
                return false;
            }
        }, executorService);
    }
    
    /**
     * 投递单个事件
     */
    @Transactional
    public boolean deliverEvent(UUID eventId) {
        Optional<WebhookEventEntity> eventOpt = webhookEventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            logger.warn("Event not found: {}", eventId);
            return false;
        }
        
        WebhookEventEntity event = eventOpt.get();
        if (event.getWebhookUrl() == null || event.getWebhookUrl().trim().isEmpty()) {
            logger.warn("No webhook URL for event: {}", eventId);
            event.setDeliveryStatus(WebhookEventEntity.DeliveryStatus.FAILED);
            event.setErrorMessage("Webhook URL is empty");
            webhookEventRepository.save(event);
            return false;
        }
        
        long startTime = System.currentTimeMillis();
        event.setDeliveryStatus(WebhookEventEntity.DeliveryStatus.DELIVERING);
        event.setLastDeliveryAttempt(LocalDateTime.now());
        event.incrementDeliveryAttempts();
        event.setProcessingNode(getHostname());
        
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("X-Webhook-Event-Type", event.getEventType());
            headers.set("X-Webhook-Event-ID", event.getId().toString());
            headers.set("X-Webhook-Timestamp", event.getEventTimestamp().toString());
            
            // 添加签名
            if (event.getWebhookSecret() != null && !event.getWebhookSecret().isEmpty()) {
                String signature = generateSignature(event.getEventData(), event.getWebhookSecret());
                headers.set("X-Webhook-Signature", signature);
            }
            
            // 添加自定义 headers
            if (event.getWebhookHeaders() != null) {
                Map<String, String> customHeaders = parseHeaders(event.getWebhookHeaders());
                customHeaders.forEach(headers::set);
            }
            
            HttpEntity<String> requestEntity = new HttpEntity<>(event.getEventData(), headers);
            ResponseEntity<String> response = restTemplate.exchange(
                event.getWebhookUrl(), 
                HttpMethod.POST, 
                requestEntity, 
                String.class
            );
            
            long latency = System.currentTimeMillis() - startTime;
            event.setDeliveryLatencyMs(latency);
            event.setDeliveryStatus(WebhookEventEntity.DeliveryStatus.DELIVERED);
            event.setLastDeliveryStatusCode(response.getStatusCodeValue());
            event.setLastDeliveryResponse(truncate(response.getBody(), 1000));
            event.incrementDeliverySuccess();
            
            logger.info("Event {} delivered successfully in {}ms, status: {}", 
                       eventId, latency, response.getStatusCode());
            
            // 处理回调
            if (event.getCallbackUrl() != null) {
                handleCallback(event, true, response.getBody());
            }
            
            webhookEventRepository.save(event);
            return true;
            
        } catch (HttpClientErrorException e) {
            handleDeliveryError(event, e, startTime, "HTTP Client Error: " + e.getStatusCode());
            return false;
        } catch (HttpServerErrorException e) {
            handleDeliveryError(event, e, startTime, "HTTP Server Error: " + e.getStatusCode());
            return false;
        } catch (Exception e) {
            handleDeliveryError(event, e, startTime, "Delivery failed: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * 处理投递错误
     */
    private void handleDeliveryError(WebhookEventEntity event, Exception e, long startTime, String errorMessage) {
        long latency = System.currentTimeMillis() - startTime;
        event.setDeliveryLatencyMs(latency);
        event.setErrorMessage(truncate(errorMessage, 2000));
        event.incrementDeliveryFailure();
        event.incrementErrorCount();
        
        if (e instanceof HttpClientErrorException || e instanceof HttpServerErrorException) {
            if (e instanceof HttpClientErrorException) {
                event.setLastDeliveryStatusCode(((HttpClientErrorException) e).getRawStatusCode());
            } else {
                event.setLastDeliveryStatusCode(((HttpServerErrorException) e).getRawStatusCode());
            }
        }
        
        // 判断是否需要重试
        if (event.isRetryable()) {
            long nextRetryDelay = event.calculateNextRetryDelay();
            LocalDateTime nextAttempt = LocalDateTime.now().plusSeconds(nextRetryDelay);
            event.setNextDeliveryAttempt(nextAttempt);
            event.setDeliveryStatus(WebhookEventEntity.DeliveryStatus.FAILED);
            logger.warn("Event {} delivery failed, will retry in {}s. Attempt: {}/{}", 
                       event.getId(), nextRetryDelay, event.getDeliveryAttempts(), 
                       event.getMaxDeliveryAttempts());
        } else {
            event.setDeadLettered(true);
            event.setDeadLetterReason("Max retry attempts exceeded: " + errorMessage);
            event.setDeadLetteredAt(LocalDateTime.now());
            event.setDeliveryStatus(WebhookEventEntity.DeliveryStatus.DEAD_LETTER);
            logger.error("Event {} moved to dead letter queue after {} attempts", 
                        event.getId(), event.getDeliveryAttempts());
        }
        
        // 处理回调（失败情况）
        if (event.getCallbackUrl() != null) {
            handleCallback(event, false, errorMessage);
        }
        
        webhookEventRepository.save(event);
    }
    
    /**
     * 处理回调
     */
    private void handleCallback(WebhookEventEntity event, boolean success, String response) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            
            Map<String, Object> callbackData = new HashMap<>();
            callbackData.put("eventId", event.getId().toString());
            callbackData.put("eventType", event.getEventType());
            callbackData.put("success", success);
            callbackData.put("timestamp", LocalDateTime.now().toString());
            callbackData.put("response", response);
            callbackData.put("attempts", event.getDeliveryAttempts());
            
            String jsonCallback = objectMapper.writeValueAsString(callbackData);
            HttpEntity<String> requestEntity = new HttpEntity<>(jsonCallback, headers);
            
            restTemplate.exchange(event.getCallbackUrl(), HttpMethod.POST, requestEntity, String.class);
            logger.info("Callback sent for event: {}", event.getId());
            
        } catch (Exception e) {
            logger.error("Failed to send callback for event: {}", event.getId(), e);
        }
    }
    
    // ===================== 重试处理 =====================
    
    /**
     * 处理所有需要重试的事件
     */
    @Transactional
    public int processRetryQueue() {
        LocalDateTime now = LocalDateTime.now();
        List<WebhookEventEntity> retryEvents = webhookEventRepository.findImmediateRetryEvents(now);
        
        int successCount = 0;
        for (WebhookEventEntity event : retryEvents) {
            try {
                if (deliverEvent(event.getId())) {
                    successCount++;
                }
            } catch (Exception e) {
                logger.error("Retry failed for event: {}", event.getId(), e);
            }
        }
        
        logger.info("Processed {} retry events, {} succeeded", retryEvents.size(), successCount);
        return successCount;
    }
    
    /**
     * 手动重试事件
     */
    @Transactional
    public boolean retryEvent(UUID eventId) {
        Optional<WebhookEventEntity> eventOpt = webhookEventRepository.findById(eventId);
        if (eventOpt.isEmpty()) {
            return false;
        }
        
        WebhookEventEntity event = eventOpt.get();
        event.setDeliveryStatus(WebhookEventEntity.DeliveryStatus.PENDING);
        event.setNextDeliveryAttempt(LocalDateTime.now());
        event.setErrorMessage(null);
        webhookEventRepository.save(event);
        
        return deliverEvent(eventId);
    }
    
    // ===================== 统计查询 =====================
    
    /**
     * 获取投递统计
     */
    public Map<String, Object> getDeliveryStats() {
        Map<String, Object> stats = new HashMap<>();
        
        List<Object[]> byStatus = webhookEventRepository.countByDeliveryStatus();
        Map<String, Long> statusCounts = new HashMap<>();
        for (Object[] row : byStatus) {
            statusCounts.put(row[0].toString(), (Long) row[1]);
        }
        stats.put("byStatus", statusCounts);
        
        Double successRate = webhookEventRepository.calculateDeliverySuccessRate();
        stats.put("successRate", successRate != null ? successRate * 100 : 0);
        
        Double avgLatency = webhookEventRepository.calculateAverageDeliveryLatency();
        stats.put("avgLatencyMs", avgLatency != null ? avgLatency : 0);
        
        stats.put("deadLetterCount", webhookEventRepository.countByDeadLetteredTrue());
        
        return stats;
    }
    
    /**
     * 获取事件类型统计
     */
    public Map<String, Long> getEventTypeStats() {
        Map<String, Long> stats = new HashMap<>();
        List<Object[]> byType = webhookEventRepository.countByEventType();
        for (Object[] row : byType) {
            stats.put(row[0].toString(), (Long) row[1]);
        }
        return stats;
    }
    
    // ===================== 工具方法 =====================
    
    private String generateSignature(String payload, String secret) 
            throws NoSuchAlgorithmException, InvalidKeyException {
        javax.crypto.Mac mac = javax.crypto.Mac.getInstance("HmacSHA256");
        mac.init(new javax.crypto.spec.SecretKeySpec(
            secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] hmac = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(hmac);
    }
    
    private Map<String, String> parseHeaders(String headersJson) {
        try {
            return objectMapper.readValue(headersJson, Map.class);
        } catch (JsonProcessingException e) {
            logger.warn("Failed to parse webhook headers", e);
            return new HashMap<>();
        }
    }
    
    private String truncate(String str, int maxLen) {
        if (str == null) return null;
        return str.length() <= maxLen ? str : str.substring(0, maxLen);
    }
    
    private String getHostname() {
        try {
            return java.net.InetAddress.getLocalHost().getHostName();
        } catch (Exception e) {
            return "unknown";
        }
    }
    
    /**
     * 清理过期事件
     */
    @Transactional
    public int cleanupExpiredEvents(int ttlDays) {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(ttlDays);
        int archived = 0;
        int deleted = 0;
        
        List<WebhookEventEntity> toArchive = webhookEventRepository.findEventsReadyForArchiving(LocalDateTime.now());
        if (!toArchive.isEmpty()) {
            List<UUID> ids = toArchive.stream().map(WebhookEventEntity::getId).toList();
            archived = webhookEventRepository.archiveEvents(ids);
        }
        
        deleted = webhookEventRepository.deleteArchivedEventsBefore(cutoff);
        logger.info("Cleanup: archived {} events, deleted {} old archived events", archived, deleted);
        
        return archived + deleted;
    }
}