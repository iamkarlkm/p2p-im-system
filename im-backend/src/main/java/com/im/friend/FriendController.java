package com.im.friend;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * 好友控制器
 * 功能 #4: 好友关系管理系统 - REST API
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/friend")
public class FriendController {
    
    private static final Logger logger = LoggerFactory.getLogger(FriendController.class);
    
    @Autowired
    private FriendService friendService;
    
    // ==================== 好友申请 ====================
    
    /**
     * 发送好友申请
     */
    @PostMapping("/request")
    public ResponseEntity<Map<String, Object>> sendFriendRequest(
            @RequestParam String fromUserId,
            @RequestParam String toUserId,
            @RequestParam(required = false) String message,
            @RequestParam(required = false) String remark) {
        
        FriendService.FriendResult result = friendService.sendFriendRequest(
            fromUserId, toUserId, message, remark);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        if (result.getData() != null) {
            response.put("requestId", result.getData());
        }
        
        return result.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 接受好友申请
     */
    @PostMapping("/accept/{requestId}")
    public ResponseEntity<Map<String, Object>> acceptFriendRequest(
            @RequestParam String userId,
            @PathVariable String requestId) {
        
        FriendService.FriendResult result = friendService.acceptFriendRequest(userId, requestId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        
        return result.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 拒绝好友申请
     */
    @PostMapping("/reject/{requestId}")
    public ResponseEntity<Map<String, Object>> rejectFriendRequest(
            @RequestParam String userId,
            @PathVariable String requestId,
            @RequestParam(required = false) String reason) {
        
        FriendService.FriendResult result = friendService.rejectFriendRequest(userId, requestId, reason);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        
        return result.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }
    
    /**
     * 获取好友申请列表
     */
    @GetMapping("/requests")
    public ResponseEntity<Map<String, Object>> getFriendRequests(
            @RequestParam String userId,
            @RequestParam(required = false) String status) {
        
        FriendService.FriendRequestStatus filterStatus = null;
        if (status != null) {
            try {
                filterStatus = FriendService.FriendRequestStatus.valueOf(status.toUpperCase());
            } catch (IllegalArgumentException e) {
                // 无效状态，返回全部
            }
        }
        
        List<FriendService.FriendRequest> requests = friendService.getFriendRequests(userId, filterStatus);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("requests", requests);
        response.put("count", requests.size());
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== 好友管理 ====================
    
    /**
     * 获取好友列表
     */
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getFriendList(@RequestParam String userId) {
        List<FriendService.FriendInfo> friends = friendService.getFriendList(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("friends", friends);
        response.put("count", friends.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 删除好友
     */
    @PostMapping("/delete")
    public ResponseEntity<Map<String, Object>> deleteFriend(
            @RequestParam String userId,
            @RequestParam String friendId) {
        
        FriendService.FriendResult result = friendService.deleteFriend(userId, friendId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 修改好友备注
     */
    @PostMapping("/remark")
    public ResponseEntity<Map<String, Object>> updateRemark(
            @RequestParam String userId,
            @RequestParam String friendId,
            @RequestParam String remark) {
        
        FriendService.FriendResult result = friendService.updateFriendRemark(userId, friendId, remark);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 移动好友到分组
     */
    @PostMapping("/move")
    public ResponseEntity<Map<String, Object>> moveToGroup(
            @RequestParam String userId,
            @RequestParam String friendId,
            @RequestParam String groupName) {
        
        FriendService.FriendResult result = friendService.moveToGroup(userId, friendId, groupName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        
        return ResponseEntity.ok(response);
    }
    
    // ==================== 分组管理 ====================
    
    /**
     * 获取好友分组
     */
    @GetMapping("/groups")
    public ResponseEntity<Map<String, Object>> getFriendGroups(@RequestParam String userId) {
        List<FriendService.FriendGroup> groups = friendService.getFriendGroups(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("groups", groups);
        response.put("count", groups.size());
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 创建分组
     */
    @PostMapping("/group/create")
    public ResponseEntity<Map<String, Object>> createGroup(
            @RequestParam String userId,
            @RequestParam String groupName) {
        
        FriendService.FriendResult result = friendService.createGroup(userId, groupName);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", result.isSuccess());
        response.put("message", result.getMessage());
        if (result.getData() != null) {
            response.put("groupId", result.getData());
        }
        
        return result.isSuccess() ? ResponseEntity.ok(response) : ResponseEntity.badRequest().body(response);
    }
    
    // ==================== 统计信息 ====================
    
    /**
     * 检查好友关系
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkFriendship(
            @RequestParam String userId,
            @RequestParam String friendId) {
        
        boolean isFriend = friendService.isFriend(userId, friendId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("isFriend", isFriend);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取好友数量
     */
    @GetMapping("/count")
    public ResponseEntity<Map<String, Object>> getFriendCount(@RequestParam String userId) {
        int count = friendService.getFriendCount(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }
}
