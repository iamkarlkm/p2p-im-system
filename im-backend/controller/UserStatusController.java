package com.im.backend.controller;

import com.im.backend.dto.UserStatusDTO;
import com.im.backend.service.StatusSubscriptionService;
import com.im.backend.service.UserStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户状态API控制器
 */
@RestController
@RequestMapping("/api/status")
public class UserStatusController {

    @Autowired
    private UserStatusService userStatusService;

    @Autowired
    private StatusSubscriptionService statusSubscriptionService;

    /**
     * 用户上线
     */
    @PostMapping("/online")
    public ResponseEntity<?> userOnline(
            @RequestParam Long userId,
            @RequestParam(required = false) String deviceType,
            @RequestParam(required = false) String ipAddress) {
        try {
            userStatusService.userOnline(userId, deviceType, ipAddress);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户已上线");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 用户离线
     */
    @PostMapping("/offline")
    public ResponseEntity<?> userOffline(@RequestParam Long userId) {
        try {
            userStatusService.userOffline(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "用户已离线");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 更新用户活动
     */
    @PostMapping("/activity")
    public ResponseEntity<?> updateActivity(@RequestParam Long userId) {
        try {
            userStatusService.updateActivity(userId);
            return ResponseEntity.ok(createSuccessResponse("活动状态已更新"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取用户状态
     */
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserStatus(
            @PathVariable Long userId,
            @RequestParam Long requesterId) {
        try {
            UserStatusDTO status = userStatusService.getUserStatus(userId, requesterId);
            return ResponseEntity.ok(status);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 批量获取用户状态
     */
    @PostMapping("/batch")
    public ResponseEntity<?> getUserStatuses(
            @RequestBody List<Long> userIds,
            @RequestParam Long requesterId) {
        try {
            List<UserStatusDTO> statuses = userStatusService.getUserStatuses(userIds, requesterId);
            return ResponseEntity.ok(statuses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 设置自定义状态
     */
    @PostMapping("/custom")
    public ResponseEntity<?> setCustomStatus(
            @RequestParam Long userId,
            @RequestParam(required = false) String customStatus,
            @RequestParam(required = false) String statusMessage) {
        try {
            userStatusService.setCustomStatus(userId, customStatus, statusMessage);
            return ResponseEntity.ok(createSuccessResponse("自定义状态已设置"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取在线用户列表
     */
    @GetMapping("/online/list")
    public ResponseEntity<?> getOnlineUsers() {
        try {
            List<UserStatusDTO> onlineUsers = userStatusService.getOnlineUsers();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", onlineUsers.size());
            response.put("users", onlineUsers);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 检查用户是否在线
     */
    @GetMapping("/online/check/{userId}")
    public ResponseEntity<?> isUserOnline(@PathVariable Long userId) {
        try {
            boolean isOnline = userStatusService.isUserOnline(userId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("userId", userId);
            response.put("isOnline", isOnline);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取在线用户数量
     */
    @GetMapping("/online/count")
    public ResponseEntity<?> getOnlineUserCount() {
        try {
            Long count = userStatusService.getOnlineUserCount();
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("count", count);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 设置隐私选项
     */
    @PostMapping("/privacy")
    public ResponseEntity<?> setPrivacyOptions(
            @RequestParam Long userId,
            @RequestParam Boolean isVisible,
            @RequestParam Boolean showLastSeen) {
        try {
            userStatusService.setPrivacyOptions(userId, isVisible, showLastSeen);
            return ResponseEntity.ok(createSuccessResponse("隐私设置已更新"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 订阅用户状态
     */
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribeStatus(
            @RequestParam Long subscriberId,
            @RequestParam Long targetUserId) {
        try {
            statusSubscriptionService.subscribeStatus(subscriberId, targetUserId);
            return ResponseEntity.ok(createSuccessResponse("已订阅用户状态"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 取消订阅用户状态
     */
    @PostMapping("/unsubscribe")
    public ResponseEntity<?> unsubscribeStatus(
            @RequestParam Long subscriberId,
            @RequestParam Long targetUserId) {
        try {
            statusSubscriptionService.unsubscribeStatus(subscriberId, targetUserId);
            return ResponseEntity.ok(createSuccessResponse("已取消订阅"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    /**
     * 获取用户的订阅列表
     */
    @GetMapping("/subscriptions/{subscriberId}")
    public ResponseEntity<?> getSubscriptions(@PathVariable Long subscriberId) {
        try {
            var subscriptions = statusSubscriptionService.getSubscriptions(subscriberId);
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("subscriberId", subscriberId);
            response.put("subscriptions", subscriptions);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }

    private Map<String, Object> createSuccessResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", message);
        return response;
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("error", message);
        return response;
    }
}
