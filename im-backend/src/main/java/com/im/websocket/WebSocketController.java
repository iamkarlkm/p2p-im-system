package com.im.websocket;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * WebSocket控制器
 * 功能 #2: WebSocket实时推送服务 - REST API
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/push")
public class WebSocketController {
    
    @Autowired
    private WebSocketPushService pushService;
    
    @Autowired
    private WebSocketConnectionManager connectionManager;
    
    /**
     * 推送消息到指定用户
     */
    @PostMapping("/user/{userId}")
    public ResponseEntity<?> pushToUser(@PathVariable String userId, @RequestBody String message) {
        boolean sent = pushService.pushToUser(userId, message);
        return ResponseEntity.ok(Map.of(
            "success", sent,
            "userId", userId
        ));
    }
    
    /**
     * 广播消息
     */
    @PostMapping("/broadcast")
    public ResponseEntity<?> broadcast(@RequestBody String message) {
        pushService.broadcast(message);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "onlineCount", connectionManager.getOnlineCount()
        ));
    }
    
    /**
     * 获取在线用户数
     */
    @GetMapping("/stats/online")
    public ResponseEntity<?> getOnlineStats() {
        return ResponseEntity.ok(Map.of(
            "onlineCount", connectionManager.getOnlineCount()
        ));
    }
    
    /**
     * 获取推送统计
     */
    @GetMapping("/stats/push")
    public ResponseEntity<?> getPushStats() {
        return ResponseEntity.ok(pushService.getStats());
    }
    
    /**
     * 检查用户在线状态
     */
    @GetMapping("/user/{userId}/online")
    public ResponseEntity<?> isUserOnline(@PathVariable String userId) {
        return ResponseEntity.ok(Map.of(
            "userId", userId,
            "online", connectionManager.isUserOnline(userId)
        ));
    }
}
