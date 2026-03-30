package com.im.controller;

import com.im.service.IMessageStoreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 消息存储控制器
 * 功能 #6: 消息存储与检索引擎 - REST API
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@RestController
@RequestMapping("/api/v1/messages")
public class MessageStoreController {
    
    @Autowired
    private IMessageStoreService messageStoreService;
    
    @GetMapping("/{messageId}")
    public ResponseEntity<?> getMessage(@PathVariable String messageId) {
        var message = messageStoreService.getMessageById(messageId);
        if (message == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(Map.of("success", true, "data", message));
    }
    
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<?> getConversationHistory(@PathVariable String conversationId,
                                                     @RequestParam(defaultValue = "20") int limit,
                                                     @RequestParam(defaultValue = "0") long beforeTimestamp) {
        var messages = messageStoreService.getConversationHistory(conversationId, limit, beforeTimestamp);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", messages,
            "count", messages.size()
        ));
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchMessages(@RequestParam String userId,
                                            @RequestParam String keyword,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
                                            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        var messages = messageStoreService.searchMessages(userId, keyword, startTime, endTime);
        return ResponseEntity.ok(Map.of(
            "success", true,
            "data", messages,
            "count", messages.size()
        ));
    }
    
    @PostMapping("/{messageId}/recall")
    public ResponseEntity<?> recallMessage(@PathVariable String messageId,
                                           @RequestParam String operatorId) {
        boolean recalled = messageStoreService.recallMessage(messageId, operatorId);
        return ResponseEntity.ok(Map.of("success", recalled));
    }
    
    @PostMapping("/{messageId}/read")
    public ResponseEntity<?> markMessageRead(@PathVariable String messageId,
                                             @RequestParam String userId) {
        boolean marked = messageStoreService.markMessageRead(messageId, userId);
        return ResponseEntity.ok(Map.of("success", marked));
    }
    
    @GetMapping("/unread/count")
    public ResponseEntity<?> getUnreadCount(@RequestParam String userId,
                                            @RequestParam(required = false) String conversationId) {
        int count;
        if (conversationId != null) {
            count = messageStoreService.getUnreadCount(userId, conversationId);
        } else {
            count = messageStoreService.getTotalUnreadCount(userId);
        }
        return ResponseEntity.ok(Map.of(
            "success", true,
            "unreadCount", count
        ));
    }
    
    @DeleteMapping("/{messageId}")
    public ResponseEntity<?> deleteMessage(@PathVariable String messageId,
                                           @RequestParam String userId) {
        boolean deleted = messageStoreService.deleteMessageForUser(messageId, userId);
        return ResponseEntity.ok(Map.of("success", deleted));
    }
}
