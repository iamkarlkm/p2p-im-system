package com.im.server.controller;

import com.im.server.dto.ApiResponse;
import com.im.server.entity.Notification;
import com.im.server.service.NotificationService;
import com.im.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 通知控制器
 */
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final UserService userService;

    /**
     * 获取当前用户的所有通知
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<Notification>>> getNotifications(
            @RequestHeader("Authorization") String token) {
        
        Long userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(401, "Token无效"));
        }
        
        List<Notification> notifications = notificationService.getUserNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    /**
     * 获取当前用户的未读通知
     */
    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<Notification>>> getUnreadNotifications(
            @RequestHeader("Authorization") String token) {
        
        Long userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(401, "Token无效"));
        }
        
        List<Notification> notifications = notificationService.getUnreadNotifications(userId);
        return ResponseEntity.ok(ApiResponse.success(notifications));
    }

    /**
     * 获取未读通知数量
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUnreadCount(
            @RequestHeader("Authorization") String token) {
        
        Long userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(401, "Token无效"));
        }
        
        int count = notificationService.getUnreadCount(userId);
        
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        
        return ResponseEntity.ok(ApiResponse.success(result));
    }

    /**
     * 标记通知为已读
     */
    @PostMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable("id") Long notificationId,
            @RequestHeader("Authorization") String token) {
        
        Long userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(401, "Token无效"));
        }
        
        notificationService.markAsRead(notificationId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 标记所有通知为已读
     */
    @PostMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @RequestHeader("Authorization") String token) {
        
        Long userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(401, "Token无效"));
        }
        
        notificationService.markAllAsRead(userId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(
            @PathVariable("id") Long notificationId,
            @RequestHeader("Authorization") String token) {
        
        Long userId = extractUserId(token);
        if (userId == null) {
            return ResponseEntity.badRequest()
                    .body(ApiResponse.error(401, "Token无效"));
        }
        
        notificationService.deleteNotification(notificationId);
        return ResponseEntity.ok(ApiResponse.success(null));
    }

    /**
     * 从Token中提取用户ID
     */
    private Long extractUserId(String token) {
        if (token != null && token.startsWith("Bearer ")) {
            token = token.substring(7);
        }
        return userService.verifyToken(token);
    }
}
