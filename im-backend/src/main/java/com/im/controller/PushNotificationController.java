package com.im.controller;

import com.im.service.IPushNotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 推送通知控制器
 * 功能 #8: 消息推送通知系统 - REST API
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/push")
public class PushNotificationController {
    
    @Autowired
    private IPushNotificationService pushService;
    
    @PostMapping("/register")
    public ResponseEntity<?> registerDevice(@RequestParam String userId,
                                            @RequestParam String deviceType,
                                            @RequestParam String deviceToken) {
        boolean registered = pushService.registerDeviceToken(userId, deviceType, deviceToken);
        return ResponseEntity.ok(Map.of("success", registered));
    }
    
    @PostMapping("/send")
    public ResponseEntity<?> sendPush(@RequestParam String userId,
                                      @RequestParam String title,
                                      @RequestParam String content,
                                      @RequestParam(required = false) Map<String, Object> extras) {
        boolean sent = pushService.pushToAllDevices(userId, title, content, extras);
        return ResponseEntity.ok(Map.of("success", sent));
    }
    
    @GetMapping("/stats/{userId}")
    public ResponseEntity<?> getPushStats(@PathVariable String userId) {
        var stats = pushService.getPushStats(userId);
        return ResponseEntity.ok(Map.of("success", true, "data", stats));
    }
    
    @PostMapping("/rate-limit")
    public ResponseEntity<?> setRateLimit(@RequestParam String userId,
                                          @RequestParam int maxPerMinute) {
        pushService.setPushRateLimit(userId, maxPerMinute);
        return ResponseEntity.ok(Map.of("success", true));
    }
}
