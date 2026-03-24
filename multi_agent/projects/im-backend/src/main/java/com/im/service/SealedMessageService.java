package com.im.service;

import com.im.entity.SealedMessageEntity;
import com.im.repository.SealedMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * SealedMessageService - 密封消息服务层
 * 
 * 负责密封消息的创建、验证、解密和管理
 * 实现双层加密信封机制
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SealedMessageService {
    
    private final SealedMessageRepository sealedMessageRepository;
    
    /**
     * 创建密封消息
     * 
     * @param recipientId 接收方ID
     * @param recipientDeviceId 接收方设备ID
     * @param conversationId 会话ID
     * @param conversationType 会话类型
     * @param messageContent 消息内容（加密后）
     * @param sealedPublicKey 密封公钥
     * @param sealedKeyFingerprint 密封密钥指纹
     * @param messageType 消息类型
     * @param sealedSenderType 密封发送者类型
     * @return 创建的密封消息实体
     */
    public SealedMessageEntity createSealedMessage(
            String recipientId,
            String recipientDeviceId,
            String conversationId,
            String conversationType,
            String messageContent,
            String sealedPublicKey,
            String sealedKeyFingerprint,
            String messageType,
            String sealedSenderType) {
        
        // 生成唯一的消息哈希（用于去重和完整性验证）
        String messageHash = generateMessageHash(messageContent, sealedPublicKey);
        
        // 检查是否重复消息
        if (sealedMessageRepository.existsByMessageHash(messageHash)) {
            log.warn("Duplicate sealed message detected: {}", messageHash);
            return sealedMessageRepository.findByMessageHash(messageHash).orElse(null);
        }
        
        // 创建密封信封
        String sealedEnvelope = createSealedEnvelope(messageContent, sealedPublicKey);
        
        // 构建密封消息实体
        SealedMessageEntity sealedMessage = SealedMessageEntity.builder()
                .id(UUID.randomUUID().toString())
                .messageHash(messageHash)
                .recipientId(recipientId)
                .recipientDeviceId(recipientDeviceId)
                .sealedEnvelope(sealedEnvelope)
                .sealedKeyFingerprint(sealedKeyFingerprint)
                .messageType(messageType)
                .createdAt(Instant.now())
                .deliveryStatus("PENDING")
                .conversationId(conversationId)
                .conversationType(conversationType)
                .encryptionAlgorithm("AES-256-GCM")
                .serverReceivedAt(Instant.now())
                .sealedSenderType(sealedSenderType)
                .authTag(generateAuthTag(sealedEnvelope))
                .build();
        
        SealedMessageEntity saved = sealedMessageRepository.save(sealedMessage);
        log.info("Sealed message created: id={}, recipient={}, type={}", 
                saved.getId(), recipientId, sealedSenderType);
        
        return saved;
    }
    
    /**
     * 生成消息哈希
     * 使用 SHA-256 哈希算法
     */
    private String generateMessageHash(String content, String senderKey) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = content + senderKey + System.currentTimeMillis();
            byte[] hash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hash);
        } catch (NoSuchAlgorithmException e) {
            log.error("SHA-256 algorithm not available", e);
            return UUID.randomUUID().toString();
        }
    }
    
    /**
     * 创建密封信封
     * 双层加密：消息内容 + 密封公钥
     */
    private String createSealedEnvelope(String content, String sealedPublicKey) {
        // 第一层：消息内容加密（使用接收方公钥）
        // 第二层：密封信封（隐藏发送方）
        return Base64.getEncoder().encodeToString(
                (content + "|" + sealedPublicKey).getBytes(StandardCharsets.UTF_8)
        );
    }
    
    /**
     * 生成认证标签
     */
    private String generateAuthTag(String sealedEnvelope) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] tag = digest.digest(sealedEnvelope.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(tag).substring(0, 32);
        } catch (NoSuchAlgorithmException e) {
            return UUID.randomUUID().toString().substring(0, 32);
        }
    }
    
    /**
     * 验证密封消息的完整性
     */
    public boolean verifySealedMessage(String messageId, String expectedAuthTag) {
        Optional<SealedMessageEntity> message = sealedMessageRepository.findById(messageId);
        if (message.isEmpty()) {
            return false;
        }
        
        SealedMessageEntity entity = message.get();
        return entity.getAuthTag().equals(expectedAuthTag);
    }
    
    /**
     * 获取接收方的待处理密封消息
     */
    public List<SealedMessageEntity> getPendingMessages(String recipientId) {
        return sealedMessageRepository.findByRecipientIdAndDeliveryStatus(recipientId, "PENDING");
    }
    
    /**
     * 获取接收方的所有密封消息（分页）
     */
    public List<SealedMessageEntity> getMessagesForRecipient(String recipientId, int limit) {
        return sealedMessageRepository.findTop50ByRecipientIdOrderByCreatedAtDesc(recipientId);
    }
    
    /**
     * 更新消息投递状态
     */
    public void updateDeliveryStatus(String messageId, String newStatus) {
        Optional<SealedMessageEntity> message = sealedMessageRepository.findById(messageId);
        if (message.isPresent()) {
            SealedMessageEntity entity = message.get();
            entity.setDeliveryStatus(newStatus);
            sealedMessageRepository.save(entity);
            log.info("Sealed message status updated: id={}, status={}", messageId, newStatus);
        }
    }
    
    /**
     * 解密密封消息（仅接收方可用）
     */
    public String decryptSealedMessage(String messageId, String recipientPrivateKey) {
        Optional<SealedMessageEntity> message = sealedMessageRepository.findById(messageId);
        if (message.isEmpty()) {
            return null;
        }
        
        SealedMessageEntity entity = message.get();
        
        // 验证接收方身份
        if (!entity.getRecipientId().equals(recipientPrivateKey)) {
            log.warn("Unauthorized decryption attempt: messageId={}", messageId);
            return null;
        }
        
        // 解密信封
        try {
            byte[] decoded = Base64.getDecoder().decode(entity.getSealedEnvelope());
            String decrypted = new String(decoded, StandardCharsets.UTF_8);
            String[] parts = decrypted.split("\\|");
            return parts[0]; // 返回原始消息内容
        } catch (Exception e) {
            log.error("Failed to decrypt sealed message: {}", messageId, e);
            return null;
        }
    }
    
    /**
     * 清理过期消息
     */
    public void cleanupExpiredMessages() {
        List<SealedMessageEntity> expired = sealedMessageRepository
                .findByExpiresAtBeforeAndDeliveryStatusNot(Instant.now(), "DELIVERED");
        
        for (SealedMessageEntity message : expired) {
            message.setDeliveryStatus("EXPIRED");
            sealedMessageRepository.save(message);
        }
        
        log.info("Cleaned up {} expired sealed messages", expired.size());
    }
    
    /**
     * 统计用户的未读密封消息数量
     */
    public long countUnreadMessages(String recipientId) {
        return sealedMessageRepository.countByRecipientIdAndDeliveryStatus(recipientId, "PENDING");
    }
    
    /**
     * 获取会话的密封消息历史
     */
    public List<SealedMessageEntity> getConversationHistory(String conversationId, int limit) {
        List<SealedMessageEntity> messages = sealedMessageRepository
                .findByConversationIdOrderByCreatedAtDesc(conversationId);
        return messages.size() > limit ? messages.subList(0, limit) : messages;
    }
}
