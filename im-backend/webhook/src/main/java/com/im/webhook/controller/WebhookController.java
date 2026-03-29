package com.im.webhook.controller;

import com.im.webhook.model.WebhookConfig;
import com.im.webhook.model.WebhookEvent;
import com.im.webhook.service.WebhookService;
import com.im.webhook.service.WebhookRetryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Webhook管理控制器
 * 提供Webhook配置的CRUD接口
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/webhooks")
@RequiredArgsConstructor
public class WebhookController {
    
    private final WebhookService webhookService;
    private final WebhookRetryService retryService;
    
    /**
     * 创建Webhook配置
     */
    @PostMapping
    public ResponseEntity<ApiResponse<WebhookConfig>> createWebhook(
            @RequestBody WebhookConfig config) {
        WebhookConfig created = webhookService.createWebhook(config);
        return ResponseEntity.ok(ApiResponse.success(created));
    }
    
    /**
     * 获取Webhook配置详情
     */
    @GetMapping("/{webhookId}")
    public ResponseEntity<ApiResponse<WebhookConfig>> getWebhook(
            @PathVariable String webhookId) {
        WebhookConfig config = webhookService.getWebhook(webhookId);
        return ResponseEntity.ok(ApiResponse.success(config));
    }
    
    /**
     * 更新Webhook配置
     */
    @PutMapping("/{webhookId}")
    public ResponseEntity<ApiResponse<WebhookConfig>> updateWebhook(
            @PathVariable String webhookId,
            @RequestBody WebhookConfig config) {
        WebhookConfig updated = webhookService.updateWebhook(webhookId, config);
        return ResponseEntity.ok(ApiResponse.success(updated));
    }
    
    /**
     * 删除Webhook配置
     */
    @DeleteMapping("/{webhookId}")
    public ResponseEntity<ApiResponse<Void>> deleteWebhook(
            @PathVariable String webhookId) {
        webhookService.deleteWebhook(webhookId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * 获取应用的所有Webhook
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<WebhookConfig>>> listWebhooks(
            @RequestParam String appId) {
        List<WebhookConfig> configs = webhookService.getWebhooksByApp(appId);
        return ResponseEntity.ok(ApiResponse.success(configs));
    }
    
    /**
     * 获取Webhook事件列表
     */
    @GetMapping("/{webhookId}/events")
    public ResponseEntity<ApiResponse<List<WebhookEvent>>> listEvents(
            @PathVariable String webhookId,
            @RequestParam(required = false) WebhookEvent.EventStatus status,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
            @RequestParam(defaultValue = "20") int limit) {
        
        // 简化实现
        Map<String, Object> result = new HashMap<>();
        result.put("webhookId", webhookId);
        result.put("status", status);
        
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * 获取Webhook统计
     */
    @GetMapping("/{webhookId}/stats")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getStats(
            @PathVariable String webhookId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        if (start == null) {
            start = LocalDateTime.now().minusDays(7);
        }
        if (end == null) {
            end = LocalDateTime.now();
        }
        
        Map<String, Object> stats = webhookService.getEventStats(webhookId, start, end);
        return ResponseEntity.ok(ApiResponse.success(stats));
    }
    
    /**
     * 手动触发重试
     */
    @PostMapping("/events/{eventId}/retry")
    public ResponseEntity<ApiResponse<Boolean>> retryEvent(
            @PathVariable String eventId) {
        boolean result = retryService.retryNow(eventId);
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    /**
     * 批量重试
     */
    @PostMapping("/{webhookId}/retry-all")
    public ResponseEntity<ApiResponse<Integer>> retryAll(
            @PathVariable String webhookId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {
        
        if (start == null) {
            start = LocalDateTime.now().minusDays(1);
        }
        if (end == null) {
            end = LocalDateTime.now();
        }
        
        int count = retryService.retryFailedEvents(webhookId, start, end);
        return ResponseEntity.ok(ApiResponse.success(count));
    }
    
    /**
     * 暂停Webhook
     */
    @PostMapping("/{webhookId}/pause")
    public ResponseEntity<ApiResponse<Void>> pauseWebhook(
            @PathVariable String webhookId) {
        WebhookConfig config = new WebhookConfig();
        config.setStatus(WebhookConfig.WebhookStatus.PAUSED);
        webhookService.updateWebhook(webhookId, config);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * 启用Webhook
     */
    @PostMapping("/{webhookId}/resume")
    public ResponseEntity<ApiResponse<Void>> resumeWebhook(
            @PathVariable String webhookId) {
        WebhookConfig config = new WebhookConfig();
        config.setStatus(WebhookConfig.WebhookStatus.ACTIVE);
        webhookService.updateWebhook(webhookId, config);
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * 刷新配置缓存
     */
    @PostMapping("/refresh-cache")
    public ResponseEntity<ApiResponse<Void>> refreshCache() {
        webhookService.refreshConfigCache();
        return ResponseEntity.ok(ApiResponse.success(null));
    }
    
    /**
     * 测试Webhook
     */
    @PostMapping("/{webhookId}/test")
    public ResponseEntity<ApiResponse<Map<String, Object>>> testWebhook(
            @PathVariable String webhookId) {
        
        // 发送测试事件
        Map<String, Object> testPayload = new HashMap<>();
        testPayload.put("test", true);
        testPayload.put("message", "This is a test event");
        testPayload.put("timestamp", System.currentTimeMillis());
        
        WebhookConfig config = webhookService.getWebhook(webhookId);
        webhookService.triggerEvent(config.getAppId(), "webhook.test", testPayload);
        
        Map<String, Object> result = new HashMap<>();
        result.put("message", "Test event sent");
        result.put("webhookId", webhookId);
        result.put("appId", config.getAppId());
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }
    
    /**
     * 通用API响应
     */
    public static class ApiResponse<T> {
        private int code;
        private String message;
        private T data;
        private long timestamp;
        
        public static <T> ApiResponse<T> success(T data) {
            ApiResponse<T> response = new ApiResponse<>();
            response.code = 200;
            response.message = "success";
            response.data = data;
            response.timestamp = System.currentTimeMillis();
            return response;
        }
        
        public static <T> ApiResponse<T> error(int code, String message) {
            ApiResponse<T> response = new ApiResponse<>();
            response.code = code;
            response.message = message;
            response.timestamp = System.currentTimeMillis();
            return response;
        }
        
        // Getters
        public int getCode() { return code; }
        public String getMessage() { return message; }
        public T getData() { return data; }
        public long getTimestamp() { return timestamp; }
    }
}
