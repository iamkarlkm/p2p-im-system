package com.im.backend.controller;

import com.im.backend.dto.MessageReadReceiptResponse;
import com.im.backend.entity.MessageReadReceipt;
import com.im.backend.service.MessageReadReceiptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 消息已读回执控制器
 * 对应功能 #16 - 消息已读回执功能
 * 
 * API列表:
 * POST   /api/message/read/private/{messageId}     - 标记私聊消息已读
 * POST   /api/message/read/group/{messageId}       - 标记群聊消息已读
 * POST   /api/message/read/batch                   - 批量标记已读
 * GET    /api/message/{messageId}/receipt          - 获取消息已读详情
 * GET    /api/message/{messageId}/read-count       - 获取已读数量
 */
@RestController
@RequestMapping("/api/message")
public class MessageReadReceiptController {

    @Autowired
    private MessageReadReceiptService receiptService;
    
    /**
     * 标记私聊消息已读
     */
    @PostMapping("/read/private/{messageId}")
    public ResponseEntity<?> markPrivateMessageRead(@RequestAttribute("userId") Long userId,
                                                     @PathVariable Long messageId,
                                                     @RequestParam Long conversationId) {
        try {
            receiptService.markPrivateMessageRead(messageId, userId, conversationId);
            return ResponseEntity.ok(createSuccessResponse("已标记为已读"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 标记群聊消息已读
     */
    @PostMapping("/read/group/{messageId}")
    public ResponseEntity<?> markGroupMessageRead(@RequestAttribute("userId") Long userId,
                                                   @PathVariable Long messageId,
                                                   @RequestParam Long groupId) {
        try {
            receiptService.markGroupMessageRead(messageId, userId, groupId);
            return ResponseEntity.ok(createSuccessResponse("已标记为已读"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 批量标记消息已读
     */
    @PostMapping("/read/batch")
    public ResponseEntity<?> markMessagesReadBatch(@RequestAttribute("userId") Long userId,
                                                    @RequestBody Map<String, Object> request) {
        try {
            @SuppressWarnings("unchecked")
            List<Long> messageIds = (List<Long>) request.get("messageIds");
            String typeStr = (String) request.get("conversationType");
            Long conversationId = Long.valueOf(request.get("conversationId").toString());
            
            if (messageIds == null || messageIds.isEmpty()) {
                return ResponseEntity.badRequest().body(createErrorResponse("messageIds不能为空"));
            }
            
            MessageReadReceipt.ConversationType type = 
                MessageReadReceipt.ConversationType.valueOf(typeStr.toUpperCase());
            
            receiptService.markConversationMessagesRead(messageIds, userId, type, conversationId);
            return ResponseEntity.ok(createSuccessResponse("批量标记已读成功"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取消息已读回执详情
     */
    @GetMapping("/{messageId}/receipt")
    public ResponseEntity<?> getMessageReceipt(@PathVariable Long messageId) {
        try {
            MessageReadReceiptResponse receipt = receiptService.getMessageReadReceipt(messageId);
            return ResponseEntity.ok(createSuccessResponse(receipt));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 获取消息已读数量
     */
    @GetMapping("/{messageId}/read-count")
    public ResponseEntity<?> getMessageReadCount(@PathVariable Long messageId,
                                                  @RequestParam(required = false) Long groupId) {
        try {
            Map<String, Object> result = new HashMap<>();
            
            if (groupId != null) {
                // 群消息返回详细统计
                MessageReadReceiptResponse stats = receiptService.getGroupMessageReadStats(messageId, groupId);
                result.put("readCount", stats.getReadCount());
                result.put("totalCount", stats.getTotalCount());
                result.put("readUsers", stats.getReadUsers());
            } else {
                // 私聊消息只返回数量
                long count = receiptService.getMessageReadCount(messageId);
                result.put("readCount", count);
            }
            
            return ResponseEntity.ok(createSuccessResponse(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 检查用户是否已读消息
     */
    @GetMapping("/{messageId}/is-read")
    public ResponseEntity<?> isMessageRead(@RequestAttribute("userId") Long userId,
                                            @PathVariable Long messageId) {
        try {
            boolean isRead = receiptService.isMessageReadByUser(messageId, userId);
            Map<String, Object> result = new HashMap<>();
            result.put("isRead", isRead);
            return ResponseEntity.ok(createSuccessResponse(result));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(createErrorResponse(e.getMessage()));
        }
    }
    
    /**
     * 创建成功响应
     */
    private Map<String, Object> createSuccessResponse(Object data) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 200);
        response.put("message", "success");
        response.put("data", data);
        return response;
    }
    
    /**
     * 创建错误响应
     */
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("code", 400);
        response.put("message", message);
        response.put("data", null);
        return response;
    }
}
