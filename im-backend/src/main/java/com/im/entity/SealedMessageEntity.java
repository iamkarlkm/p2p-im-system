package com.im.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import java.time.Instant;

/**
 * SealedMessageEntity - 密封发送者消息实体
 * 
 * 密封发送者 (Sealed Sender) 是一种隐私保护机制，用于：
 * 1. 隐藏消息的发送方元数据（服务器只知道接收方，不知道发送方）
 * 2. 采用双层加密信封机制
 * 3. 只有接收方能够验证发送方的身份
 * 
 * 工作原理：
 * - 第一层加密：发送方使用接收方的公钥加密消息
 * - 第二层加密：使用密封密钥再次加密，隐藏发送方身份
 * - 服务器仅能看到：消息ID、接收方ID、时间戳
 * - 接收方解密后：能看到完整的消息内容和发送方信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "sealed_messages")
public class SealedMessageEntity {
    
    /**
     * 消息唯一标识
     */
    @Id
    private String id;
    
    /**
     * 消息的唯一哈希值（用于去重和验证完整性）
     * SHA-256(message_content + sender_identity_key + timestamp)
     */
    @Indexed(unique = true)
    private String messageHash;
    
    /**
     * 接收方用户ID（服务器唯一能看到的元数据）
     */
    @Indexed
    private String recipientId;
    
    /**
     * 接收方设备ID（支持多设备投递）
     */
    private String recipientDeviceId;
    
    /**
     * 密封信封内容（服务器无法解密）
     * 包含：加密的消息内容 + 发送方的密封公钥
     */
    private String sealedEnvelope;
    
    /**
     * 密封密钥指纹（用于接收方识别密封密钥）
     */
    private String sealedKeyFingerprint;
    
    /**
     * 消息类型：TEXT/IMAGE/FILE/AUDIO/VIDEO
     */
    private String messageType;
    
    /**
     * 消息创建时间戳
     */
    @Indexed
    private Instant createdAt;
    
    /**
     * 消息投递状态：PENDING/DELIVERED/READ/EXPIRED
     */
    private String deliveryStatus;
    
    /**
     * 消息过期时间（可选，用于临时密封消息）
     */
    private Instant expiresAt;
    
    /**
     * 目标会话ID（群聊或单聊）
     */
    @Indexed
    private String conversationId;
    
    /**
     * 会话类型：DIRECT/GROUP/CHANNEL
     */
    private String conversationType;
    
    /**
     * 加密算法标识：AES-256-GCM/X25519
     */
    private String encryptionAlgorithm;
    
    /**
     * 消息序列号（用于消息顺序验证）
     */
    private Long sequenceNumber;
    
    /**
     * 目标消息ID（回复/引用其他消息）
     */
    private String replyToMessageId;
    
    /**
     * 服务器接收时间
     */
    private Instant serverReceivedAt;
    
    /**
     * 消息元数据签名（用于防篡改）
     */
    private String metadataSignature;
    
    /**
     * 密封发送者类型：
     * - HIDDEN: 完全隐藏发送方
     * - PARTIAL: 显示发送方但加密内容
     * - DISCLOSED: 发送方信息可见
     */
    private String sealedSenderType;
    
    /**
     * 验证标记（接收方用于验证发送方身份）
     */
    private String authTag;
    
    /**
     * 创建索引以优化查询性能
     * 这个类需要与 MongoDB 的复合索引配合使用
     */
    public static class Indexes {
        public static final String CONVERSATION_CREATED = "conversationId_createdAt";
        public static final String RECIPIENT_STATUS = "recipientId_deliveryStatus";
        public static final String MESSAGE_HASH = "messageHash";
    }
}
