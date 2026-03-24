package com.im.system.controller;

import com.im.system.entity.WebhookEventEntity;
import com.im.system.service.WebhookEventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Webhook 事件 REST API 控制器
 * 提供事件创建、查询、投递、重试、统计等接口
 */
@RestController
@RequestMapping("/api/v1/webhook-events")
@CrossOrigin(origins = "*")
public class WebhookEventController {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookEventController.class);
    
    @Autowired
    private WebhookEventService webhookEventService;
    
    // ===================== 事件创建 =====================
    
    /**
     * 创建新的 Webhook 事件
     * POST /api/v1/webhook-events
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createEvent(@RequestBody Map<String, Object> request) {
        try {
            String eventType = (String) request.get("eventType");
            String eventData = (String) request.get("eventData");
            UUID subscriptionId = request.get("subscriptionId") != null ? 
                UUID.fromString(request.get("subscriptionId").toString()) : null;
            String webhookUrl = (String) request.get("webhookUrl");
            String webhookSecret = (String) request.get("webhookSecret");
            Integer priority = request.get("priority") != null ? 
                Integer.parseInt(request.get("priority").toString()) : 5;
            
            if (eventType == null || eventType.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(errorResponse("eventType is required"));
            }
            
            WebhookEventEntity event = webhookEventService.createEvent(
                eventType, eventData, subscriptionId, webhookUrl, webhookSecret, priority);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("eventId", event.getId().toString());
            response.put("status", event.getDeliveryStatus().toString());
            response.put("createdAt", event.getCreatedAt().toString());
            
            logger.info("Created webhook event via API: {}", event.getId());
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (IllegalArgumentException e) {
            logger.error("Invalid request parameters", e);
            return ResponseEntity.badRequest().body(errorResponse("Invalid parameters: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Failed to create webhook event", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Failed to create event: " + e.getMessage()));
        }
    }
    
    /**
     * 批量创建事件
     * POST /api/v1/webhook-events/batch
     */
    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> createEvents(@RequestBody List<Map<String, Object>> requests) {
        try {
            if (requests == null || requests.isEmpty()) {
                return ResponseEntity.badRequest().body(errorResponse("Request list cannot be empty"));
            }
            
            List<WebhookEventEntity> events = webhookEventService.createEvents(requests);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", events.size());
            response.put("eventIds", events.stream().map(e -> e.getId().toString()).toList());
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            logger.error("Failed to create batch events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Failed to create events: " + e.getMessage()));
        }
    }
    
    // ===================== 事件查询 =====================
    
    /**
     * 获取事件列表（分页）
     * GET /api/v1/webhook-events?page=0&size=20&status=PENDING&eventType=order.created
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getEvents(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String eventType,
            @RequestParam(required = false) String eventTypeSubtype,
            @RequestParam(required = false) UUID subscriptionId) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
            Page<WebhookEventEntity> eventPage;
            
            if (status != null && !status.isEmpty()) {
                WebhookEventEntity.DeliveryStatus deliveryStatus = 
                    WebhookEventEntity.DeliveryStatus.valueOf(status.toUpperCase());
                eventPage = webhookEventRepository.findByDeliveryStatus(deliveryStatus, pageable);
            } else if (eventType != null && !eventType.isEmpty()) {
                if (eventSubtype != null && !eventSubtype.isEmpty()) {
                    eventPage = webhookEventRepository.findByEventTypeAndEventSubtype(eventType, eventSubtype, pageable);
                } else {
                    eventPage = webhookEventRepository.findByEventType(eventType, pageable);
                }
            } else if (subscriptionId != null) {
                // 需要自定义查询
                List<WebhookEventEntity> events = webhookEventRepository.findBySubscriptionId(subscriptionId);
                eventPage = createPage(events, pageable);
            } else {
                eventPage = webhookEventRepository.findAll(pageable);
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", eventPage.getContent());
            response.put("pagination", paginationResponse(eventPage));
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(errorResponse("Invalid status: " + e.getMessage()));
        } catch (Exception e) {
            logger.error("Failed to get events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Failed to get events: " + e.getMessage()));
        }
    }
    
    /**
     * 获取单个事件详情
     * GET /api/v1/webhook-events/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> getEvent(@PathVariable UUID id) {
        try {
            WebhookEventEntity event = webhookEventRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Event not found: " + id));
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", event);
            
            return ResponseEntity.ok(response);
            
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse(e.getMessage()));
        } catch (Exception e) {
            logger.error("Failed to get event: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Failed to get event: " + e.getMessage()));
        }
    }
    
    // ===================== 事件投递 =====================
    
    /**
     * 手动触发事件投递
     * POST /api/v1/webhook-events/{id}/deliver
     */
    @PostMapping("/{id}/deliver")
    public ResponseEntity<Map<String, Object>> deliverEvent(@PathVariable UUID id) {
        try {
            boolean success = webhookEventService.deliverEvent(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("delivered", success);
            response.put("message", success ? "Event delivered successfully" : "Delivery failed");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to deliver event: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Failed to deliver event: " + e.getMessage()));
        }
    }
    
    /**
     * 异步投递事件
     * POST /api/v1/webhook-events/{id}/deliver-async
     */
    @PostMapping("/{id}/deliver-async")
    public ResponseEntity<Map<String, Object>> deliverEventAsync(@PathVariable UUID id) {
        try {
            webhookEventService.deliverEventAsync(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Delivery queued for async processing");
            
            return ResponseEntity.accepted().body(response);
            
        } catch (Exception e) {
            logger.error("Failed to queue async delivery: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Failed to queue delivery: " + e.getMessage()));
        }
    }
    
    // ===================== 重试操作 =====================
    
    /**
     * 手动重试失败的事件
     * POST /api/v1/webhook-events/{id}/retry
     */
    @PostMapping("/{id}/retry")
    public ResponseEntity<Map<String, Object>> retryEvent(@PathVariable UUID id) {
        try {
            boolean success = webhookEventService.retryEvent(id);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("retried", success);
            response.put("message", success ? "Event retry successful" : "Event retry failed");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to retry event: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Failed to retry event: " + e.getMessage()));
        }
    }
    
    /**
     * 批量重试失败的事件
     * POST /api/v1/webhook-events/retry-batch
     */
    @PostMapping("/retry-batch")
    public ResponseEntity<Map<String, Object>> retryEvents(@RequestBody List<UUID> eventIds) {
        try {
            int successCount = 0;
            for (UUID id : eventIds) {
                if (webhookEventService.retryEvent(id)) {
                    successCount++;
                }
            }
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("total", eventIds.size());
            response.put("succeeded", successCount);
            response.put("failed", eventIds.size() - successCount);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to retry batch events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Failed to retry events: " + e.getMessage()));
        }
    }
    
    /**
     * 处理重试队列
     * POST /api/v1/webhook-events/process-retry-queue
     */
    @PostMapping("/process-retry-queue")
    public ResponseEntity<Map<String, Object>> processRetryQueue() {
        try {
            int successCount = webhookEventService.processRetryQueue();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("processedCount", successCount);
            response.put("message", "Retry queue processed");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to process retry queue", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Failed to process retry queue: " + e.getMessage()));
        }
    }
    
    // ===================== 统计接口 =====================
    
    /**
     * 获取投递统计
     * GET /api/v1/webhook-events/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        try {
            Map<String, Object> stats = webhookEventService.getDeliveryStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to get stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Failed to get stats: " + e.getMessage()));
        }
    }
    
    /**
     * 获取事件类型统计
     * GET /api/v1/webhook-events/stats/by-type
     */
    @GetMapping("/stats/by-type")
    public ResponseEntity<Map<String, Object>> getEventTypeStats() {
        try {
            Map<String, Long> stats = webhookEventService.getEventTypeStats();
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("data", stats);
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to get event type stats", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Failed to get stats: " + e.getMessage()));
        }
    }
    
    // ===================== 管理接口 =====================
    
    /**
     * 清理过期事件
     * POST /api/v1/webhook-events/cleanup
     */
    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupEvents(
            @RequestParam(defaultValue = "30") int ttlDays) {
        try {
            int cleanedCount = webhookEventService.cleanupExpiredEvents(ttlDays);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("cleanedCount", cleanedCount);
            response.put("message", "Cleanup completed");
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Failed to cleanup events", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(errorResponse("Failed to cleanup: " + e.getMessage()));
        }
    }
    
    // ===================== 辅助方法 =====================
    
    private Map<String, Object> errorResponse(String message) {
        Map<String, Object> error = new HashMap<>();
        error.put("success", false);
        error.put("error", message);
        return error;
    }
    
    private Map<String, Object> paginationResponse(Page<?> page) {
        Map<String, Object> pagination = new HashMap<>();
        pagination.put("totalElements", page.getTotalElements());
        pagination.put("totalPages", page.getTotalPages());
        pagination.put("currentPage", page.getNumber());
        pagination.put("pageSize", page.getSize());
        pagination.put("isFirst", page.isFirst());
        pagination.put("isLast", page.isLast());
        return pagination;
    }
    
    private <T> Page<T> createPage(List<T> list, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), list.size());
        return new org.springframework.data.domain.PageImpl<>(
            list.subList(start, end), pageable, list.size());
    }
}