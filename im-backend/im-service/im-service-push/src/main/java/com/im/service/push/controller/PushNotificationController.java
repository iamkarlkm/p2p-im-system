package com.im.service.push.controller;

import com.im.service.push.dto.PushNotificationDTO;
import com.im.service.push.dto.PushRequestDTO;
import com.im.service.push.dto.BatchPushRequestDTO;
import com.im.service.push.entity.PushNotification;
import com.im.service.push.service.PushNotificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 推送通知 REST API - 离线消息推送
 */
@RestController
@RequestMapping("/api/push")
public class PushNotificationController {

    private final PushNotificationService pushService;

    public PushNotificationController(PushNotificationService pushService) {
        this.pushService = pushService;
    }

    // ========== 发送推送 ==========

    @PostMapping("/send")
    public ResponseEntity<PushNotification> sendPush(@RequestBody PushRequestDTO request) {
        PushNotification notification = pushService.sendPush(
            request.getUserId(), 
            request.getTitle(), 
            request.getBody(),
            request.getNotificationType(), 
            request.getSenderId(), 
            request.getSenderName(),
            request.getConversationId(), 
            request.getMessageId(), 
            request.getPriority()
        );
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/silent")
    public ResponseEntity<PushNotification> sendSilentPush(@RequestBody Map<String, Object> body) {
        String userId = (String) body.get("userId");
        String deviceId = (String) body.get("deviceId");
        String deviceToken = (String) body.get("deviceToken");
        String pushType = (String) body.get("pushType");
        String silentType = (String) body.get("silentType");
        String priority = (String) body.get("priority");
        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) body.get("data");

        PushNotification notification = pushService.sendSilentPush(
            userId, deviceId, deviceToken, pushType, silentType, data, priority
        );
        return ResponseEntity.ok(notification);
    }

    @PostMapping("/batch")
    public ResponseEntity<Map<String, Object>> sendBatchPush(@RequestBody BatchPushRequestDTO request) {
        pushService.sendBatchPush(
            request.getUserIds(), 
            request.getTitle(), 
            request.getBody(),
            request.getNotificationType(), 
            request.getSenderId(), 
            request.getSenderName(),
            request.getConversationId(), 
            request.getMessageId()
        );

        Map<String, Object> result = new HashMap<>();
        result.put("batchSize", request.getUserIds().size());
        result.put("status", "queued");
        return ResponseEntity.ok(result);
    }

    // ========== 查询推送 ==========

    @GetMapping("/{notificationId}")
    public ResponseEntity<PushNotification> getNotification(@PathVariable String notificationId) {
        return pushService.getNotification(notificationId)
            .map(ResponseEntity::ok)
            .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<PushNotification>> getUserNotifications(
            @PathVariable String userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<PushNotification> notifications = pushService.getUserNotifications(userId, page, size);
        return ResponseEntity.ok(notifications);
    }

    @GetMapping("/user/{userId}/stats")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable String userId) {
        Map<String, Long> stats = pushService.getUserNotificationStats(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("stats", stats);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/stats/daily")
    public ResponseEntity<List<Object[]>> getDailyStats(@RequestParam(defaultValue = "7") int days) {
        List<Object[]> stats = pushService.getDailyStats(days);
        return ResponseEntity.ok(stats);
    }

    // ========== 状态更新 ==========

    @PostMapping("/{notificationId}/sent")
    public ResponseEntity<Map<String, Object>> markSent(
            @PathVariable String notificationId,
            @RequestBody Map<String, String> body) {
        String apnsId = body.get("apnsId");
        String fcmId = body.get("fcmMessageId");
        pushService.markSent(notificationId, apnsId, fcmId);
        Map<String, Object> result = new HashMap<>();
        result.put("notificationId", notificationId);
        result.put("status", "SENT");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{notificationId}/delivered")
    public ResponseEntity<Map<String, Object>> markDelivered(@PathVariable String notificationId) {
        pushService.markDelivered(notificationId);
        Map<String, Object> result = new HashMap<>();
        result.put("notificationId", notificationId);
        result.put("status", "DELIVERED");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{notificationId}/failed")
    public ResponseEntity<Map<String, Object>> markFailed(
            @PathVariable String notificationId,
            @RequestBody Map<String, String> body) {
        String reason = body.get("reason");
        pushService.markFailed(notificationId, reason);
        Map<String, Object> result = new HashMap<>();
        result.put("notificationId", notificationId);
        result.put("status", "FAILED");
        result.put("reason", reason);
        return ResponseEntity.ok(result);
    }

    // ========== 推送管理 ==========

    @PostMapping("/user/{userId}/silence")
    public ResponseEntity<Map<String, Object>> silenceUser(@PathVariable String userId) {
        pushService.silenceUser(userId);
        Map<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("action", "silenced");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/cleanup")
    public ResponseEntity<Map<String, Object>> cleanupExpired() {
        pushService.cleanupExpired();
        Map<String, Object> result = new HashMap<>();
        result.put("action", "cleanup_completed");
        return ResponseEntity.ok(result);
    }

    @PostMapping("/{notificationId}/retry")
    public ResponseEntity<Map<String, Object>> retry(@PathVariable String notificationId) {
        pushService.retryFailed(notificationId);
        Map<String, Object> result = new HashMap<>();
        result.put("notificationId", notificationId);
        result.put("action", "retry_queued");
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{notificationId}")
    public ResponseEntity<Map<String, Object>> cancel(@PathVariable String notificationId) {
        pushService.cancelNotification(notificationId);
        Map<String, Object> result = new HashMap<>();
        result.put("notificationId", notificationId);
        result.put("action", "cancelled");
        return ResponseEntity.ok(result);
    }
}
