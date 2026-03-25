package com.im.server.controller;

import com.im.server.dto.ApiResponse;
import com.im.server.entity.FriendRequest;
import com.im.server.service.FriendRequestService;
import com.im.server.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 好友请求控制器
 */
@RestController
@RequestMapping("/api/friend-requests")
@RequiredArgsConstructor
public class FriendRequestController {

    private final FriendRequestService friendRequestService;
    private final UserService userService;

    /**
     * 发送好友请求
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FriendRequest>> sendRequest(
            @RequestBody Map<String, Object> request,
            @RequestHeader("Authorization") String token) {
        
        try {
            Long userId = extractUserId(token);
            Long toUserId = Long.valueOf(request.get("toUserId").toString());
            String message = (String) request.get("message");
            
            FriendRequest result = friendRequestService.sendRequest(userId, toUserId, message);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取待处理的好友请求列表
     */
    @GetMapping("/pending")
    public ResponseEntity<ApiResponse<List<FriendRequest>>> getPendingRequests(
            @RequestHeader("Authorization") String token) {
        
        Long userId = extractUserId(token);
        List<FriendRequest> requests = friendRequestService.getPendingRequests(userId);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    /**
     * 获取已发送的好友请求列表
     */
    @GetMapping("/sent")
    public ResponseEntity<ApiResponse<List<FriendRequest>>> getSentRequests(
            @RequestHeader("Authorization") String token) {
        
        Long userId = extractUserId(token);
        List<FriendRequest> requests = friendRequestService.getSentRequests(userId);
        return ResponseEntity.ok(ApiResponse.success(requests));
    }

    /**
     * 同意好友请求
     */
    @PostMapping("/{id}/accept")
    public ResponseEntity<ApiResponse<FriendRequest>> acceptRequest(
            @PathVariable("id") Long requestId,
            @RequestHeader("Authorization") String token) {
        
        try {
            Long userId = extractUserId(token);
            FriendRequest result = friendRequestService.acceptRequest(requestId, userId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 拒绝好友请求
     */
    @PostMapping("/{id}/reject")
    public ResponseEntity<ApiResponse<FriendRequest>> rejectRequest(
            @PathVariable("id") Long requestId,
            @RequestHeader("Authorization") String token) {
        
        try {
            Long userId = extractUserId(token);
            FriendRequest result = friendRequestService.rejectRequest(requestId, userId);
            return ResponseEntity.ok(ApiResponse.success(result));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 取消好友请求
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> cancelRequest(
            @PathVariable("id") Long requestId,
            @RequestHeader("Authorization") String token) {
        
        try {
            Long userId = extractUserId(token);
            friendRequestService.cancelRequest(requestId, userId);
            return ResponseEntity.ok(ApiResponse.success(null));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(ApiResponse.error(e.getMessage()));
        }
    }

    /**
     * 获取未读请求数量
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<Map<String, Integer>>> getUnreadCount(
            @RequestHeader("Authorization") String token) {
        
        Long userId = extractUserId(token);
        int count = friendRequestService.getUnreadCount(userId);
        
        Map<String, Integer> result = new HashMap<>();
        result.put("count", count);
        
        return ResponseEntity.ok(ApiResponse.success(result));
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
