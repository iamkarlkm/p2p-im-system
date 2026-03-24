package com.im.server.controller;

import com.im.server.service.SelfDestructService;
import com.im.server.service.SelfDestructService.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 阅后即焚控制器
 */
@RestController
@RequestMapping("/api/self-destruct")
public class SelfDestructController {

    @Autowired
    private SelfDestructService service;

    /**
     * 设置消息阅后即焚
     */
    @PostMapping("/setup")
    public ResponseEntity<Map<String, Object>> setupSelfDestruct(
            @RequestParam Long messageId,
            @RequestParam Long senderId,
            @RequestParam Long receiverId,
            @RequestParam String timerType,
            @RequestParam(required = false) Integer customSeconds) {
        
        TimerType type = TimerType.valueOf(timerType);
        int custom = customSeconds != null ? customSeconds : 30;
        
        SelfDestructMessage result = service.setupSelfDestruct(messageId, senderId, receiverId, type, custom);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", result);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 标记消息已读
     */
    @PostMapping("/read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @RequestParam Long messageId,
            @RequestParam Long readerId) {
        
        service.markAsRead(messageId, readerId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "消息已标记为已读，倒计时开始");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取消息销毁状态
     */
    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus(@RequestParam Long messageId) {
        DestroyStatus status = service.getStatus(messageId);
        long remaining = service.getRemainingSeconds(messageId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("status", status != null ? status.name() : null);
        response.put("remainingSeconds", remaining);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 手动销毁消息
     */
    @PostMapping("/destroy")
    public ResponseEntity<Map<String, Object>> destroyMessage(
            @RequestParam Long messageId,
            @RequestParam Long operatorId) {
        
        service.destroyMessage(messageId, operatorId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "消息已销毁");
        
        return ResponseEntity.ok(response);
    }

    /**
     * 批量销毁消息
     */
    @PostMapping("/batch-destroy")
    public ResponseEntity<Map<String, Object>> batchDestroy(
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        List<Long> messageIds = (List<Long>) request.get("messageIds");
        Long operatorId = Long.valueOf(request.get("operatorId").toString());
        
        service.batchDestroy(messageIds, operatorId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("message", "批量销毁完成");
        response.put("count", messageIds.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取销毁历史
     */
    @GetMapping("/history")
    public ResponseEntity<Map<String, Object>> getDestroyHistory(
            @RequestParam Long userId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        
        List<DestroyRecord> records = service.getDestroyHistory(userId, page, size);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", records);
        
        return ResponseEntity.ok(response);
    }
}
