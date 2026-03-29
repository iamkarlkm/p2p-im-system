package com.im.controller;

import com.im.entity.ViewOnceMessageEntity;
import com.im.service.ViewOnceMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 一次性媒体消息控制器
 */
@RestController
@RequestMapping("/api/view-once")
@RequiredArgsConstructor
@Slf4j
public class ViewOnceMessageController {
    
    private final ViewOnceMessageService service;
    
    /**
     * 创建一次性媒体消息
     */
    @PostMapping("/messages")
    public ResponseEntity<Map<String, Object>> createViewOnceMessage(
            @RequestBody Map<String, Object> request) {
        
        String messageId = (String) request.get("messageId");
        String conversationId = (String) request.get("conversationId");
        String senderId = (String) request.get("senderId");
        String receiverId = (String) request.get("receiverId");
        String mediaType = (String) request.get("mediaType");
        String mediaUrl = (String) request.get("mediaUrl");
        Long mediaSize = request.get("mediaSize") != null ? 
                Long.valueOf(request.get("mediaSize").toString()) : null;
        String mimeType = (String) request.get("mimeType");
        String encryptionKey = (String) request.get("encryptionKey");
        Boolean screenshotDetection = (Boolean) request.get("screenshotDetection");
        
        ViewOnceMessageEntity entity = service.createViewOnceMessage(
                messageId, conversationId, senderId, receiverId,
                mediaType, mediaUrl, mediaSize, mimeType, encryptionKey,
                null, screenshotDetection, null);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messageId", entity.getMessageId());
        response.put("createdAt", entity.getCreatedAt());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取一次性媒体消息内容
     * 需要验证接收者身份
     */
    @GetMapping("/messages/{messageId}")
    public ResponseEntity<Map<String, Object>> getViewOnceMessage(
            @PathVariable String messageId,
            @RequestParam String receiverId,
            HttpServletRequest request) {
        
        String ip = getClientIp(request);
        String deviceId = request.getHeader("X-Device-Id");
        
        return service.getViewOnceMessage(messageId, receiverId)
                .map(entity -> {
                    // 标记为已查看
                    service.markAsViewed(messageId, receiverId, ip, deviceId);
                    
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("messageId", entity.getMessageId());
                    response.put("mediaType", entity.getMediaType());
                    response.put("mediaUrl", entity.getMediaUrl());
                    response.put("mimeType", entity.getMimeType());
                    response.put("mediaSize", entity.getMediaSize());
                    response.put("encryptionKey", entity.getEncryptionKey());
                    response.put("metadata", entity.getMetadata());
                    response.put("viewed", entity.getViewed());
                    response.put("viewedAt", entity.getViewedAt());
                    
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("error", "Message not found or access denied");
                    return ResponseEntity.status(404).body(response);
                });
    }
    
    /**
     * 获取一次性媒体消息元数据（不标记为已查看）
     */
    @GetMapping("/messages/{messageId}/metadata")
    public ResponseEntity<Map<String, Object>> getViewOnceMessageMetadata(
            @PathVariable String messageId,
            @RequestParam String receiverId) {
        
        return service.getViewOnceMessage(messageId, receiverId)
                .map(entity -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", true);
                    response.put("messageId", entity.getMessageId());
                    response.put("mediaType", entity.getMediaType());
                    response.put("mimeType", entity.getMimeType());
                    response.put("mediaSize", entity.getMediaSize());
                    response.put("viewed", entity.getViewed());
                    response.put("active", entity.getActive());
                    response.put("createdAt", entity.getCreatedAt());
                    
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("success", false);
                    response.put("error", "Message not found or access denied");
                    return ResponseEntity.status(404).body(response);
                });
    }
    
    /**
     * 标记消息为已查看
     */
    @PostMapping("/messages/{messageId}/view")
    public ResponseEntity<Map<String, Object>> markAsViewed(
            @PathVariable String messageId,
            @RequestParam String receiverId,
            HttpServletRequest request) {
        
        String ip = getClientIp(request);
        String deviceId = request.getHeader("X-Device-Id");
        
        boolean success = service.markAsViewed(messageId, receiverId, ip, deviceId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("messageId", messageId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 销毁一次性媒体消息
     */
    @PostMapping("/messages/{messageId}/destroy")
    public ResponseEntity<Map<String, Object>> destroyMessage(
            @PathVariable String messageId,
            @RequestParam(required = false, defaultValue = "MANUAL") String reason) {
        
        boolean success = service.destroyMessage(messageId, reason);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("messageId", messageId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 批量销毁消息
     */
    @PostMapping("/messages/destroy-batch")
    public ResponseEntity<Map<String, Object>> destroyMessages(
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        List<String> messageIds = (List<String>) request.get("messageIds");
        String reason = (String) request.getOrDefault("reason", "MANUAL");
        
        int count = service.destroyMessages(messageIds, reason);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("destroyedCount", count);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 记录截图
     */
    @PostMapping("/messages/{messageId}/screenshot")
    public ResponseEntity<Map<String, Object>> recordScreenshot(
            @PathVariable String messageId,
            @RequestBody Map<String, Object> request) {
        
        String timestamp = (String) request.get("timestamp");
        String details = (String) request.get("details");
        
        boolean success = service.recordScreenshot(messageId, timestamp, details);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", success);
        response.put("messageId", messageId);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取会话的所有一次性媒体消息
     */
    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<Map<String, Object>> getConversationMessages(
            @PathVariable String conversationId) {
        
        List<ViewOnceMessageEntity> messages = service.getConversationViewOnceMessages(conversationId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("conversationId", conversationId);
        response.put("count", messages.size());
        response.put("messages", messages);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取用户收到的一次性媒体消息列表
     */
    @GetMapping("/users/{userId}/messages")
    public ResponseEntity<Map<String, Object>> getUserMessages(
            @PathVariable String userId,
            @RequestParam(required = false, defaultValue = "false") boolean unviewedOnly) {
        
        List<ViewOnceMessageEntity> messages;
        if (unviewedOnly) {
            messages = service.getUnviewedMessages(userId);
        } else {
            messages = service.getUserViewOnceMessages(userId);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", userId);
        response.put("count", messages.size());
        response.put("messages", messages);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取用户的一次性媒体统计
     */
    @GetMapping("/users/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String userId) {
        
        var stats = service.getUserStats(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("userId", userId);
        response.put("totalReceived", stats.getTotalReceived());
        response.put("totalViewed", stats.getTotalViewed());
        response.put("totalUnviewed", stats.getTotalUnviewed());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取会话的一次性媒体统计
     */
    @GetMapping("/conversations/{conversationId}/stats")
    public ResponseEntity<Map<String, Object>> getConversationStats(
            @PathVariable String conversationId) {
        
        var stats = service.getConversationStats(conversationId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("conversationId", conversationId);
        response.put("totalMessages", stats.getTotalMessages());
        response.put("unviewedCount", stats.getUnviewedCount());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 处理过期的消息（管理员接口）
     */
    @PostMapping("/admin/cleanup/expired")
    public ResponseEntity<Map<String, Object>> cleanupExpiredMessages() {
        
        int count = service.processExpiredMessages();
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("processedCount", count);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 清理已销毁的消息记录（管理员接口）
     */
    @PostMapping("/admin/cleanup/destroyed")
    public ResponseEntity<Map<String, Object>> cleanupDestroyedMessages(
            @RequestParam(required = false, defaultValue = "30") int retentionDays) {
        
        int count = service.cleanupDestroyedMessages(retentionDays);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("cleanedCount", count);
        response.put("retentionDays", retentionDays);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 检查消息是否已被查看
     */
    @GetMapping("/messages/{messageId}/status")
    public ResponseEntity<Map<String, Object>> getMessageStatus(@PathVariable String messageId) {
        
        boolean viewed = service.isMessageViewed(messageId);
        boolean active = service.isMessageActive(messageId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messageId", messageId);
        response.put("viewed", viewed);
        response.put("active", active);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取客户端IP
     */
    private String getClientIp(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
