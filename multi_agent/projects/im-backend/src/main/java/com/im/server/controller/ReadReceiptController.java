package com.im.server.controller;

import com.im.server.service.ReadReceiptService;
import com.im.server.service.ReadReceiptService.ReadStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息已读回执控制器
 */
@RestController
@RequestMapping("/api/read-receipts")
public class ReadReceiptController {

    @Autowired
    private ReadReceiptService service;

    /**
     * 标记消息已读
     */
    @PostMapping("/mark-read")
    public ResponseEntity<Map<String, Object>> markAsRead(
            @RequestParam Long messageId,
            @RequestParam Long userId) {
        
        var receipt = service.markAsRead(messageId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", receipt);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 标记会话中所有消息已读
     */
    @PostMapping("/mark-conversation-read")
    public ResponseEntity<Map<String, Object>> markConversationAsRead(
            @RequestParam Long conversationId,
            @RequestParam Long userId) {
        
        var receipts = service.markConversationAsRead(conversationId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", receipts.size());
        response.put("data", receipts);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 批量标记消息已读
     */
    @PostMapping("/batch-mark-read")
    public ResponseEntity<Map<String, Object>> batchMarkAsRead(
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        List<Long> messageIds = (List<Long>) request.get("messageIds");
        Long userId = Long.valueOf(request.get("userId").toString());
        
        int count = 0;
        for (Long messageId : messageIds) {
            try {
                service.markAsRead(messageId, userId);
                count++;
            } catch (Exception e) {
                // 忽略已读的消息
            }
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取消息的已读用户列表
     */
    @GetMapping("/message/{messageId}")
    public ResponseEntity<Map<String, Object>> getReadReceipts(
            @PathVariable Long messageId) {
        
        var receipts = service.getReadReceipts(messageId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", receipts);
        response.put("count", receipts.size());
        
        return ResponseEntity.ok(response);
    }

    /**
     * 检查消息是否已被某用户阅读
     */
    @GetMapping("/check")
    public ResponseEntity<Map<String, Object>> checkReadStatus(
            @RequestParam Long messageId,
            @RequestParam Long userId) {
        
        boolean isRead = service.isReadByUser(messageId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("isRead", isRead);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取会话未读消息数量
     */
    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @RequestParam Long conversationId,
            @RequestParam Long userId) {
        
        long count = service.getUnreadCount(conversationId, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", count);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取多个会话的未读数量
     */
    @PostMapping("/unread-counts")
    public ResponseEntity<Map<String, Object>> getUnreadCounts(
            @RequestBody Map<String, Object> request) {
        
        @SuppressWarnings("unchecked")
        List<Long> conversationIds = (List<Long>) request.get("conversationIds");
        Long userId = Long.valueOf(request.get("userId").toString());
        
        Map<Long, Long> counts = service.getUnreadCounts(conversationIds, userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", counts);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取已读统计信息
     */
    @GetMapping("/statistics/{messageId}")
    public ResponseEntity<Map<String, Object>> getReadStatistics(
            @PathVariable Long messageId) {
        
        ReadStatistics stats = service.getReadStatistics(messageId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", stats);
        
        return ResponseEntity.ok(response);
    }

    /**
     * 获取用户的所有未读会话
     */
    @GetMapping("/unread-conversations")
    public ResponseEntity<Map<String, Object>> getUnreadConversations(
            @RequestParam Long userId) {
        
        List<Long> conversations = service.getUnreadConversations(userId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("data", conversations);
        
        return ResponseEntity.ok(response);
    }
}
