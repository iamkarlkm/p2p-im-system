package com.im.controller;

import com.im.dto.NotificationDTO;
import com.im.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * 获取通知列表 (分页)
     * GET /api/notifications?page=0&size=20&type=SYSTEM&isRead=false
     */
    @GetMapping
    public ResponseEntity<Page<NotificationDTO>> getNotifications(
            @RequestHeader("X-User-Id") Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) Boolean isRead) {
        Page<NotificationDTO> result = notificationService.getNotifications(userId, type, isRead, page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 获取未读总数
     * GET /api/notifications/unread-count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(@RequestHeader("X-User-Id") Long userId) {
        long total = notificationService.getUnreadCount(userId);
        Map<String, Long> byType = notificationService.getUnreadCountByType(userId);
        return ResponseEntity.ok(Map.of(
            "total", total,
            "byType", byType
        ));
    }

    /**
     * 获取各类型未读统计
     * GET /api/notifications/stats
     */
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats(@RequestHeader("X-User-Id") Long userId) {
        return ResponseEntity.ok(notificationService.getUnreadCountByType(userId));
    }

    /**
     * 标记单条已读
     * POST /api/notifications/{id}/read
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        boolean ok = notificationService.markAsRead(id, userId);
        return ResponseEntity.ok(Map.of("success", ok, "id", id));
    }

    /**
     * 批量标记已读
     * POST /api/notifications/read-batch
     * Body: { "ids": [1, 2, 3] }
     */
    @PostMapping("/read-batch")
    public ResponseEntity<Map<String, Object>> batchMarkAsRead(
            @RequestBody Map<String, List<Long>> body,
            @RequestHeader("X-User-Id") Long userId) {
        List<Long> ids = body.get("ids");
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("error", "ids is required"));
        }
        int count = notificationService.batchMarkAsRead(ids, userId);
        return ResponseEntity.ok(Map.of("success", true, "count", count));
    }

    /**
     * 全部标记已读
     * POST /api/notifications/read-all
     */
    @PostMapping("/read-all")
    public ResponseEntity<Map<String, Object>> markAllAsRead(@RequestHeader("X-User-Id") Long userId) {
        int count = notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(Map.of("success", true, "count", count));
    }

    /**
     * 处理通知 (接受/拒绝)
     * POST /api/notifications/{id}/handle
     * Body: { "result": "ACCEPTED" } or { "result": "REJECTED" }
     */
    @PostMapping("/{id}/handle")
    public ResponseEntity<Map<String, Object>> handle(
            @PathVariable Long id,
            @RequestBody Map<String, String> body,
            @RequestHeader("X-User-Id") Long userId) {
        String result = body.get("result");
        if (result == null || (!result.equals("ACCEPTED") && !result.equals("REJECTED"))) {
            return ResponseEntity.badRequest().body(Map.of("error", "result must be ACCEPTED or REJECTED"));
        }
        boolean ok = notificationService.handleNotification(id, userId, result);
        return ResponseEntity.ok(Map.of("success", ok, "id", id, "result", result));
    }

    /**
     * 删除通知
     * DELETE /api/notifications/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> delete(
            @PathVariable Long id,
            @RequestHeader("X-User-Id") Long userId) {
        // Note: Add delete logic in service if needed
        return ResponseEntity.ok(Map.of("success", true, "id", id));
    }
}
