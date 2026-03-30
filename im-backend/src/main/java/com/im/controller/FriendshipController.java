package com.im.controller;

import com.im.service.IFriendshipService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 好友关系控制器
 * 功能 #4: 好友关系管理系统 - REST API
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/friends")
public class FriendshipController {
    
    @Autowired
    private IFriendshipService friendshipService;
    
    @PostMapping("/request")
    public ResponseEntity<?> sendFriendRequest(@RequestParam String userId,
                                                @RequestParam String friendId,
                                                @RequestParam(required = false) String message) {
        var friendship = friendshipService.sendFriendRequest(userId, friendId, message);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "friendshipId", friendship.getFriendshipId(),
            "status", friendship.getStatus()
        ));
    }
    
    @PostMapping("/accept/{friendshipId}")
    public ResponseEntity<?> acceptFriendRequest(@PathVariable String friendshipId) {
        boolean accepted = friendshipService.acceptFriendRequest(friendshipId);
        return ResponseEntity.ok(Map.of("success", accepted));
    }
    
    @PostMapping("/reject/{friendshipId}")
    public ResponseEntity<?> rejectFriendRequest(@PathVariable String friendshipId) {
        boolean rejected = friendshipService.rejectFriendRequest(friendshipId);
        return ResponseEntity.ok(Map.of("success", rejected));
    }
    
    @DeleteMapping("/{userId}/{friendId}")
    public ResponseEntity<?> deleteFriend(@PathVariable String userId, @PathVariable String friendId) {
        boolean deleted = friendshipService.deleteFriend(userId, friendId);
        return ResponseEntity.ok(Map.of("success", deleted));
    }
    
    @GetMapping("/{userId}/list")
    public ResponseEntity<?> getFriendList(@PathVariable String userId) {
        var friends = friendshipService.getFriendList(userId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", friends,
            "count", friends.size()
        ));
    }
    
    @GetMapping("/{userId}/pending")
    public ResponseEntity<?> getPendingRequests(@PathVariable String userId) {
        var requests = friendshipService.getPendingRequests(userId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", requests,
            "count", requests.size()
        ));
    }
    
    @PutMapping("/remark")
    public ResponseEntity<?> updateRemark(@RequestParam String userId,
                                          @RequestParam String friendId,
                                          @RequestParam String remark) {
        boolean updated = friendshipService.updateRemark(userId, friendId, remark);
        return ResponseEntity.ok(Map.of("success", updated));
    }
    
    @GetMapping("/check")
    public ResponseEntity<?> isFriend(@RequestParam String userId, @RequestParam String friendId) {
        boolean isFriend = friendshipService.isFriend(userId, friendId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "isFriend", isFriend
        ));
    }
}
