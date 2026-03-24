package com.im.controller;

import com.im.entity.SealedMessageEntity;
import com.im.service.SealedMessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SealedMessageController - 密封消息 REST 控制器
 * 
 * 提供密封消息的 API 端点，包括：
 * - 创建密封消息
 * - 获取待处理消息
 * - 更新投递状态
 * - 消息解密
 * - 历史记录查询
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/sealed-messages")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SealedMessageController {
    
    private final SealedMessageService sealedMessageService;
    
    /**
     * 创建新的密封消息
     * 
     * POST /api/v1/sealed-messages
     * 
     * @param request 包含 recipientId, conversationId, messageContent 等字段
     * @return 创建的密封消息
     */
    @PostMapping
    public ResponseEntity<Map<String, Object>> createSealedMessage(
            @RequestBody Map<String, String> request) {
        
        try {
            String recipientId = request.get("recipientId");
            String recipientDeviceId = request.get("recipientDeviceId");
            String conversationId = request.get("conversationId");
            String conversationType = request.get("conversationType");
            String messageContent = request.get("messageContent");
            String sealedPublicKey = request.get("sealedPublicKey");
            String sealedKeyFingerprint = request.get("sealedKeyFingerprint");
            String messageType = request.get("messageType");
            String sealedSenderType = request.get("sealedSenderType");
            
            SealedMessageEntity sealedMessage = sealedMessageService.createSealedMessage(
                    recipientId,
                    recipientDeviceId,
                    conversationId,
                    conversationType,
                    messageContent,
                    sealedPublicKey,
                    sealedKeyFingerprint,
                    messageType,
                    sealedSenderType
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("messageId", sealedMessage.getId());
            response.put("messageHash", sealedMessage.getMessageHash());
            response.put("sealedKeyFingerprint", sealedMessage.getSealedKeyFingerprint());
            response.put("authTag", sealedMessage.getAuthTag());
            response.put("createdAt", sealedMessage.getCreatedAt().toString());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to create sealed message", e);
            Map<String, Object> error = new HashMap<>();
            error.put("success", false);
            error.put("error", e.getMessage());
            return ResponseEntity.internalServerError().body(error);
        }
    }
    
    /**
     * 获取接收方的待处理密封消息
     * 
     * GET /api/v1/sealed-messages/pending/{recipientId}
     */
    @GetMapping("/pending/{recipientId}")
    public ResponseEntity<Map<String, Object>> getPendingMessages(
            @PathVariable String recipientId) {
        
        List<SealedMessageEntity> messages = sealedMessageService.getPendingMessages(recipientId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", messages.size());
        response.put("messages", messages);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取接收方的所有密封消息
     * 
     * GET /api/v1/sealed-messages/{recipientId}
     */
    @GetMapping("/{recipientId}")
    public ResponseEntity<Map<String, Object>> getMessagesForRecipient(
            @PathVariable String recipientId,
            @RequestParam(defaultValue = "50") int limit) {
        
        List<SealedMessageEntity> messages = sealedMessageService.getMessagesForRecipient(recipientId, limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("count", messages.size());
        response.put("messages", messages);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 更新消息投递状态
     * 
     * PUT /api/v1/sealed-messages/{messageId}/status
     */
    @PutMapping("/{messageId}/status")
    public ResponseEntity<Map<String, Object>> updateStatus(
            @PathVariable String messageId,
            @RequestBody Map<String, String> request) {
        
        String newStatus = request.get("status");
        sealedMessageService.updateDeliveryStatus(messageId, newStatus);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messageId", messageId);
        response.put("newStatus", newStatus);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 解密密封消息
     * 
     * POST /api/v1/sealed-messages/{messageId}/decrypt
     */
    @PostMapping("/{messageId}/decrypt")
    public ResponseEntity<Map<String, Object>> decryptMessage(
            @PathVariable String messageId,
            @RequestBody Map<String, String> request) {
        
        String recipientPrivateKey = request.get("recipientPrivateKey");
        String decryptedContent = sealedMessageService.decryptSealedMessage(messageId, recipientPrivateKey);
        
        Map<String, Object> response = new HashMap<>();
        if (decryptedContent != null) {
            response.put("success", true);
            response.put("decryptedContent", decryptedContent);
        } else {
            response.put("success", false);
            response.put("error", "Decryption failed or unauthorized");
        }
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 验证密封消息完整性
     * 
     * GET /api/v1/sealed-messages/{messageId}/verify
     */
    @GetMapping("/{messageId}/verify")
    public ResponseEntity<Map<String, Object>> verifyMessage(
            @PathVariable String messageId,
            @RequestParam String authTag) {
        
        boolean isValid = sealedMessageService.verifySealedMessage(messageId, authTag);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("messageId", messageId);
        response.put("isValid", isValid);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 获取会话的密封消息历史
     * 
     * GET /api/v1/sealed-messages/conversation/{conversationId}
     */
    @GetMapping("/conversation/{conversationId}")
    public ResponseEntity<Map<String, Object>> getConversationHistory(
            @PathVariable String conversationId,
            @RequestParam(defaultValue = "50") int limit) {
        
        List<SealedMessageEntity> messages = sealedMessageService.getConversationHistory(conversationId, limit);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("conversationId", conversationId);
        response.put("count", messages.size());
        response.put("messages", messages);
        
        return ResponseEntity.ok(response);
    }
    
    /**
     * 统计未读消息数量
     * 
     * GET /api/v1/sealed-messages/{recipientId}/unread/count
     */
    @GetMapping("/{recipientId}/unread/count")
    public ResponseEntity<Map<String, Object>> getUnreadCount(
            @PathVariable String recipientId) {
        
        long count = sealedMessageService.countUnreadMessages(recipientId);
        
        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("recipientId", recipientId);
        response.put("unreadCount", count);
        
        return ResponseEntity.ok(response);
    }
}
