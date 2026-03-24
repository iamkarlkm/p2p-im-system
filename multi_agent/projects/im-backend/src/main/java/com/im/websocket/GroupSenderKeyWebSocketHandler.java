package com.im.websocket;

import com.im.entity.GroupSenderKeyEntity;
import com.im.service.GroupSenderKeyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.Optional;

/**
 * 群组Sender Key WebSocket处理器
 * Signal Protocol群组加密的实时消息通知
 * 
 * 支持的WebSocket事件：
 * - 密钥分发通知
 * - 密钥确认通知
 * - 密钥轮换事件
 * - 密钥撤销通知
 * - 消息密钥派生请求
 */
@Slf4j
@Controller
@RequiredArgsConstructor
public class GroupSenderKeyWebSocketHandler {
    
    private final SimpMessagingTemplate messagingTemplate;
    private final GroupSenderKeyService senderKeyService;
    
    // ==================== 密钥分发 ====================
    
    /**
     * 请求生成并分发Sender Key
     * 客户端: /app/sender-key/{groupId}/generate
     * 响应: /topic/sender-key/{groupId}/distributed
     */
    @MessageMapping("/sender-key/{groupId}/generate")
    public void generateAndDistribute(
            @DestinationVariable String groupId,
            @Payload GenerateKeyPayload payload) {
        
        log.info("Generating sender key for group: {}, sender: {}", groupId, payload.senderId());
        
        try {
            // 生成分发给所有成员
            List<GroupSenderKeyEntity> keys = senderKeyService.generateAndDistributeToMembers(
                groupId, payload.senderId(), payload.memberIds());
            
            // 通知所有接收者有新密钥
            Map<String, Object> response = new HashMap<>();
            response.put("type", "SENDER_KEY_DISTRIBUTED");
            response.put("groupId", groupId);
            response.put("senderId", payload.senderId());
            response.put("keyVersion", keys.isEmpty() ? 1 : keys.get(0).getKeyVersion());
            response.put("recipientCount", keys.size());
            response.put("timestamp", System.currentTimeMillis());
            
            // 广播到群组主题
            messagingTemplate.convertAndSend(
                "/topic/sender-key/" + groupId + "/distributed", response);
            
            // 分别通知每个接收者
            for (GroupSenderKeyEntity key : keys) {
                Map<String, Object> recipientNotify = new HashMap<>();
                recipientNotify.put("type", "SENDER_KEY_RECEIVED");
                recipientNotify.put("senderKeyId", key.getSenderKeyId());
                recipientNotify.put("senderId", payload.senderId());
                recipientNotify.put("keyVersion", key.getKeyVersion());
                recipientNotify.put("chainKey", key.getChainKey());
                recipientNotify.put("signingPublicKey", key.getSigningPublicKey());
                recipientNotify.put("timestamp", System.currentTimeMillis());
                
                messagingTemplate.convertAndSend(
                    "/topic/sender-key/" + groupId + "/" + key.getReceiverId() + "/receive",
                    recipientNotify);
            }
            
            log.info("Sender key generated and distributed: {} recipients", keys.size());
            
        } catch (Exception e) {
            log.error("Failed to generate sender key", e);
            Map<String, Object> error = new HashMap<>();
            error.put("type", "ERROR");
            error.put("error", "Failed to generate sender key: " + e.getMessage());
            error.put("groupId", groupId);
            error.put("timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSend(
                "/topic/sender-key/" + groupId + "/error", error);
        }
    }
    
    /**
     * 确认收到Sender Key
     * 客户端: /app/sender-key/{senderKeyId}/acknowledge
     */
    @MessageMapping("/sender-key/{senderKeyId}/acknowledge")
    public void acknowledgeSenderKey(
            @DestinationVariable String senderKeyId,
            @Payload AcknowledgePayload payload) {
        
        log.info("Acknowledging sender key: {}", senderKeyId);
        
        try {
            GroupSenderKeyEntity senderKey = senderKeyService.acknowledgeSenderKey(senderKeyId);
            
            // 通知发送者密钥已被确认
            Map<String, Object> response = new HashMap<>();
            response.put("type", "SENDER_KEY_ACKNOWLEDGED");
            response.put("senderKeyId", senderKeyId);
            response.put("receiverId", senderKey.getReceiverId());
            response.put("senderId", senderKey.getSenderId());
            response.put("groupId", senderKey.getGroupId());
            response.put("timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSend(
                "/topic/sender-key/" + senderKey.getGroupId() + "/acknowledged",
                response);
            
            log.info("Sender key acknowledged: {}", senderKeyId);
            
        } catch (Exception e) {
            log.error("Failed to acknowledge sender key: {}", senderKeyId, e);
        }
    }
    
    // ==================== 消息密钥操作 ====================
    
    /**
     * 请求派生消息密钥
     * 客户端: /app/sender-key/{senderKeyId}/derive
     */
    @MessageMapping("/sender-key/{senderKeyId}/derive")
    public void deriveMessageKey(
            @DestinationVariable String senderKeyId,
            @Payload DeriveKeyPayload payload) {
        
        try {
            Optional<GroupSenderKeyEntity> optKey = senderKeyService.getSenderKey(senderKeyId);
            if (optKey.isEmpty()) {
                sendError(payload.senderId(), "Sender key not found: " + senderKeyId);
                return;
            }
            
            GroupSenderKeyEntity senderKey = optKey.get();
            String messageKey = senderKeyService.deriveMessageKey(
                senderKey.getChainKey(), payload.index());
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "MESSAGE_KEY_DERIVED");
            response.put("senderKeyId", senderKeyId);
            response.put("messageKey", messageKey);
            response.put("chainKeyIndex", payload.index());
            response.put("timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSend(
                "/topic/sender-key/" + senderKey.getGroupId() + "/" + payload.senderId() + "/derived",
                response);
            
        } catch (Exception e) {
            log.error("Failed to derive message key: {}", senderKeyId, e);
            sendError(payload.senderId(), "Failed to derive message key: " + e.getMessage());
        }
    }
    
    /**
     * 请求获取下一条消息的密钥
     * 客户端: /app/sender-key/{senderKeyId}/next-key
     */
    @MessageMapping("/sender-key/{senderKeyId}/next-key")
    public void getNextMessageKey(
            @DestinationVariable String senderKeyId,
            @Payload RequestPayload payload) {
        
        try {
            Optional<GroupSenderKeyEntity> optKey = senderKeyService.getSenderKey(senderKeyId);
            if (optKey.isEmpty()) {
                sendError(payload.senderId(), "Sender key not found: " + senderKeyId);
                return;
            }
            
            GroupSenderKeyEntity senderKey = optKey.get();
            String messageKey = senderKeyService.getNextMessageKey(senderKeyId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "NEXT_MESSAGE_KEY");
            response.put("senderKeyId", senderKeyId);
            response.put("messageKey", messageKey);
            response.put("newChainKeyIndex", senderKey.getChainKeyIndex() + 1);
            response.put("timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSend(
                "/topic/sender-key/" + senderKey.getGroupId() + "/" + payload.senderId() + "/next-key",
                response);
            
        } catch (Exception e) {
            log.error("Failed to get next message key: {}", senderKeyId, e);
            sendError(payload.senderId(), "Failed to get next message key: " + e.getMessage());
        }
    }
    
    // ==================== 密钥轮换 ====================
    
    /**
     * 开始密钥轮换
     * 客户端: /app/sender-key/{groupId}/{senderId}/rotation/start
     */
    @MessageMapping("/sender-key/{groupId}/{senderId}/rotation/start")
    public void startRotation(
            @DestinationVariable String groupId,
            @DestinationVariable String senderId) {
        
        log.info("Starting sender key rotation: group={}, sender={}", groupId, senderId);
        
        try {
            GroupSenderKeyEntity key = senderKeyService.startRotation(groupId, senderId);
            
            // 通知群组成员密钥即将轮换
            Map<String, Object> response = new HashMap<>();
            response.put("type", "ROTATION_STARTED");
            response.put("groupId", groupId);
            response.put("senderId", senderId);
            response.put("timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSend(
                "/topic/sender-key/" + groupId + "/rotation",
                response);
            
        } catch (Exception e) {
            log.error("Failed to start rotation", e);
        }
    }
    
    /**
     * 完成密钥轮换
     * 客户端: /app/sender-key/{groupId}/{senderId}/rotation/complete
     */
    @MessageMapping("/sender-key/{groupId}/{senderId}/rotation/complete")
    public void completeRotation(
            @DestinationVariable String groupId,
            @DestinationVariable String senderId,
            @Payload CompleteRotationPayload payload) {
        
        log.info("Completing sender key rotation: group={}, sender={}", groupId, senderId);
        
        try {
            List<GroupSenderKeyEntity> newKeys = senderKeyService.completeRotation(
                groupId, senderId, payload.receiverIds());
            
            // 广播新密钥给所有接收者
            Map<String, Object> response = new HashMap<>();
            response.put("type", "ROTATION_COMPLETED");
            response.put("groupId", groupId);
            response.put("senderId", senderId);
            response.put("keyVersion", newKeys.isEmpty() ? 1 : newKeys.get(0).getKeyVersion());
            response.put("recipientCount", newKeys.size());
            response.put("timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSend(
                "/topic/sender-key/" + groupId + "/rotation",
                response);
            
            // 分别通知每个接收者新密钥
            for (GroupSenderKeyEntity key : newKeys) {
                Map<String, Object> recipientNotify = new HashMap<>();
                recipientNotify.put("type", "NEW_SENDER_KEY_AFTER_ROTATION");
                recipientNotify.put("senderKeyId", key.getSenderKeyId());
                recipientNotify.put("keyVersion", key.getKeyVersion());
                recipientNotify.put("chainKey", key.getChainKey());
                recipientNotify.put("signingPublicKey", key.getSigningPublicKey());
                recipientNotify.put("timestamp", System.currentTimeMillis());
                
                messagingTemplate.convertAndSend(
                    "/topic/sender-key/" + groupId + "/" + key.getReceiverId() + "/rotation-complete",
                    recipientNotify);
            }
            
        } catch (Exception e) {
            log.error("Failed to complete rotation", e);
        }
    }
    
    // ==================== 密钥撤销 ====================
    
    /**
     * 撤销Sender Key
     * 客户端: /app/sender-key/{groupId}/{senderId}/revoke
     */
    @MessageMapping("/sender-key/{groupId}/{senderId}/revoke")
    public void revokeSenderKeys(
            @DestinationVariable String groupId,
            @DestinationVariable String senderId,
            @Payload RevokePayload payload) {
        
        log.info("Revoking sender keys: group={}, sender={}, reason={}", 
            groupId, senderId, payload.reason());
        
        try {
            int count = senderKeyService.revokeAllBySender(groupId, senderId);
            
            // 广播撤销通知
            Map<String, Object> response = new HashMap<>();
            response.put("type", "SENDER_KEY_REVOKED");
            response.put("groupId", groupId);
            response.put("senderId", senderId);
            response.put("reason", payload.reason());
            response.put("revokedCount", count);
            response.put("timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSend(
                "/topic/sender-key/" + groupId + "/revoked",
                response);
            
        } catch (Exception e) {
            log.error("Failed to revoke sender keys", e);
        }
    }
    
    // ==================== 查询请求 ====================
    
    /**
     * 请求群组密钥状态
     * 客户端: /app/sender-key/{groupId}/status
     */
    @MessageMapping("/sender-key/{groupId}/status")
    public void getGroupKeyStatus(
            @DestinationVariable String groupId,
            @Payload RequestPayload payload) {
        
        try {
            GroupSenderKeyService.SenderKeyStats stats = senderKeyService.getStats(groupId);
            List<GroupSenderKeyEntity> activeKeys = senderKeyService.getActiveKeysInGroup(groupId);
            
            Map<String, Object> response = new HashMap<>();
            response.put("type", "KEY_STATUS");
            response.put("groupId", groupId);
            response.put("stats", Map.of(
                "totalKeys", stats.totalKeys(),
                "activeKeys", stats.activeKeys(),
                "acknowledgedKeys", stats.acknowledgedKeys(),
                "pendingKeys", stats.pendingKeys(),
                "replacedKeys", stats.replacedKeys()
            ));
            response.put("activeKeys", activeKeys.stream()
                .map(k -> Map.of(
                    "senderKeyId", k.getSenderKeyId(),
                    "senderId", k.getSenderId(),
                    "keyVersion", k.getKeyVersion(),
                    "chainKeyIndex", k.getChainKeyIndex(),
                    "acknowledged", k.getAcknowledged()
                ))
                .toList());
            response.put("timestamp", System.currentTimeMillis());
            
            messagingTemplate.convertAndSend(
                "/topic/sender-key/" + groupId + "/" + payload.senderId() + "/status",
                response);
            
        } catch (Exception e) {
            log.error("Failed to get key status", e);
            sendError(payload.senderId(), "Failed to get key status: " + e.getMessage());
        }
    }
    
    // ==================== 辅助方法 ====================
    
    private void sendError(String userId, String errorMessage) {
        Map<String, Object> error = new HashMap<>();
        error.put("type", "ERROR");
        error.put("error", errorMessage);
        error.put("timestamp", System.currentTimeMillis());
        
        messagingTemplate.convertAndSend("/topic/sender-key/error/" + userId, error);
    }
    
    // ==================== Payload DTOs ====================
    
    public record GenerateKeyPayload(
        String senderId,
        List<String> memberIds
    ) {}
    
    public record AcknowledgePayload(
        String receiverId
    ) {}
    
    public record DeriveKeyPayload(
        String senderId,
        long index
    ) {}
    
    public record RequestPayload(
        String senderId
    ) {}
    
    public record CompleteRotationPayload(
        List<String> receiverIds
    ) {}
    
    public record RevokePayload(
        String reason
    ) {}
}
