package com.im.controller;

import com.im.service.IOnlineStatusService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 在线状态控制器
 * 功能 #7: 实时在线状态服务 - REST API
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/status")
public class OnlineStatusController {
    
    @Autowired
    private IOnlineStatusService onlineStatusService;
    
    @GetMapping("/{userId}")
    public ResponseEntity<?> getUserStatus(@PathVariable String userId) {
        boolean online = onlineStatusService.isOnline(userId);
        var lastActive = onlineStatusService.getLastActiveTime(userId);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "userId", userId,
            "online", online,
            "lastActiveTime", lastActive
        ));
    }
    
    @PostMapping("/{userId}/heartbeat")
    public ResponseEntity<?> heartbeat(@PathVariable String userId) {
        onlineStatusService.heartbeat(userId);
        return ResponseEntity.ok(Map.of("success", true));
    }
    
    @GetMapping("/online/count")
    public ResponseEntity<?> getOnlineCount() {
        int count = onlineStatusService.getOnlineCount();
        return ResponseEntity.ok(Map.of(
            "success", true,
            "onlineCount", count
        ));
    }
    
    @GetMapping("/online/list")
    public ResponseEntity<?> getOnlineUsers() {
        var users = onlineStatusService.getOnlineUsers();
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", users,
            "count", users.size()
        ));
    }
    
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestParam String subscriberId,
                                       @RequestParam String targetUserId) {
        onlineStatusService.subscribeStatus(subscriberId, targetUserId);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
