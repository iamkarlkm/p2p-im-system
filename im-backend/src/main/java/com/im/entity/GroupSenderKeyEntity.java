package com.im.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;

/**
 * 群组Sender Key实体
 * Signal Protocol群组加密方案：每个群成员持有一个Sender Key
 * Sender Key = 消息加密密钥链，用于群组消息的端到端加密
 * 
 * 加密流程：
 * 1. 发送者生成Sender Key（消息密钥链 + 签名密钥对）
 * 2. 通过群组KeyExchange协议将Sender Key分发给所有群成员
 * 3. 发送消息时，从Sender Key链派生消息密钥，加密消息
 * 4. 每个消息使用不同的消息密钥（向前保密）
 * 5. 群成员收到消息后，用发送者的Sender Key解密
 */
@Data
@Entity
@Table(name = "group_sender_keys", indexes = {
    @Index(name = "idx_sender_key_id", columnList = "senderKeyId", unique = true),
    @Index(name = "idx_group_id", columnList = "groupId"),
    @Index(name = "idx_sender_id", columnList = "senderId"),
    @Index(name = "idx_receiver_id", columnList = "receiverId"),
    @Index(name = "idx_chain_key_index", columnList = "chainKeyIndex"),
    @Index(name = "idx_sender_key_status", columnList = "senderKeyStatus")
})
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GroupSenderKeyEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * Sender Key唯一标识符
     * 格式: {groupId}_{senderId}_{receiverId}_{keyVersion}
     */
    @Column(nullable = false, unique = true, length = 256)
    private String senderKeyId;
    
    /**
     * 群组ID
     */
    @Column(nullable = false, length = 64)
    private String groupId;
    
    /**
     * 发送者ID（持有此Sender Key的用户）
     */
    @Column(nullable = false, length = 64)
    private String senderId;
    
    /**
     * 接收者ID（持有此Sender Key副本的用户）
     */
    @Column(nullable = false, length = 64)
    private String receiverId;
    
    /**
     * 密钥版本号（递增，用于密钥轮换）
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer keyVersion = 1;
    
    /**
     * Sender Key的链密钥（Chain Key）
     * 用于派生消息密钥（Message Key）
     * Ratchet机制：每次派生后更新
     */
    @Column(nullable = false, length = 512)
    private String chainKey;
    
    /**
     * 链密钥索引（递增的消息计数器）
     */
    @Column(nullable = false)
    @Builder.Default
    private Long chainKeyIndex = 0L;
    
    /**
     * 当前消息密钥（Message Key）
     * 由链密钥派生，用于加密单条消息
     */
    @Column(length = 512)
    private String currentMessageKey;
    
    /**
     * 签名公钥（Base64编码）
     * 用于验证Sender Key的所有权
     */
    @Column(nullable = false, length = 256)
    private String signingPublicKey;
    
    /**
     * 签名私钥（Base64编码，高敏感）
     * 服务器端存储用于密钥托管，可选
     */
    @Column(length = 512)
    private String signingPrivateKey;
    
    /**
     * Sender Key状态:
     * - INITIAL: 初始状态，尚未激活
     * - ACTIVE: 激活状态，可用于加解密
     * - RATCHETING: 正在更新密钥链
     * - EXPIRED: 已过期，需要重新分发
     * - REVOKED: 已撤销，不再可用
     * - REPLACED: 已被新版本替换
     */
    @Column(nullable = false, length = 16)
    @Builder.Default
    private String senderKeyStatus = "INITIAL";
    
    /**
     * Sender Key分发状态:
     * - PENDING: 待分发
     * - DISTRIBUTED: 已分发
     * - ACKNOWLEDGED: 已确认收到
     */
    @Column(nullable = false, length = 16)
    @Builder.Default
    private String distributionStatus = "PENDING";
    
    /**
     * 是否启用前向保密（Forward Secrecy）
     * 启用后，密钥使用后立即销毁
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean forwardSecrecyEnabled = true;
    
    /**
     * 是否启用未来保密（Future Secrecy）
     * 定期轮换Sender Key，防止长期密钥泄露
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean futureSecrecyEnabled = true;
    
    /**
     * 密钥轮换周期（天数）
     * 0表示不自动轮换
     */
    @Column(nullable = false)
    @Builder.Default
    private Integer rotationPeriodDays = 30;
    
    /**
     * 上次轮换时间
     */
    private LocalDateTime lastRotationTime;
    
    /**
     * 下次轮换时间
     */
    private LocalDateTime nextRotationTime;
    
    /**
     * 密钥过期时间（可选）
     */
    private LocalDateTime expireAt;
    
    /**
     * 是否已确认（接收者确认收到）
     */
    @Column(nullable = false)
    @Builder.Default
    private Boolean acknowledged = false;
    
    /**
     * 确认时间
     */
    private LocalDateTime acknowledgedAt;
    
    /**
     * 分发时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime distributedAt;
    
    /**
     * 创建时间
     */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
    
    /**
     * 元数据（JSON）
     * 存储额外的加密参数、算法标识等
     */
    @Column(columnDefinition = "TEXT")
    private String metadata;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        distributedAt = LocalDateTime.now();
        if (nextRotationTime == null && rotationPeriodDays > 0) {
            nextRotationTime = createdAt.plusDays(rotationPeriodDays);
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    /**
     * 生成SenderKeyId
     */
    public static String generateSenderKeyId(String groupId, String senderId, String receiverId, Integer keyVersion) {
        return groupId + "_" + senderId + "_" + receiverId + "_v" + keyVersion;
    }
    
    /**
     * 激活Sender Key
     */
    public void activate() {
        this.senderKeyStatus = "ACTIVE";
        this.distributionStatus = "DISTRIBUTED";
    }
    
    /**
     * 确认收到Sender Key
     */
    public void acknowledge() {
        this.acknowledged = true;
        this.acknowledged = true;
        this.distributionStatus = "ACKNOWLEDGED";
        this.acknowledgedAt = LocalDateTime.now();
    }
    
    /**
     * 更新链密钥索引
     */
    public void incrementChainKeyIndex() {
        this.chainKeyIndex++;
    }
    
    /**
     * 开始密钥轮换
     */
    public void startRotation() {
        this.senderKeyStatus = "RATCHETING";
    }
    
    /**
     * 完成密钥轮换
     */
    public void completeRotation(String newChainKey, Integer newKeyVersion) {
        this.chainKey = newChainKey;
        this.keyVersion = newKeyVersion;
        this.chainKeyIndex = 0L;
        this.senderKeyStatus = "ACTIVE";
        this.lastRotationTime = LocalDateTime.now();
        if (rotationPeriodDays > 0) {
            this.nextRotationTime = LocalDateTime.now().plusDays(rotationPeriodDays);
        }
    }
    
    /**
     * 标记为过期
     */
    public void markExpired() {
        this.senderKeyStatus = "EXPIRED";
    }
    
    /**
     * 撤销Sender Key
     */
    public void revoke() {
        this.senderKeyStatus = "REVOKED";
    }
    
    /**
     * 替换为新版本
     */
    public void replace() {
        this.senderKeyStatus = "REPLACED";
    }
    
    /**
     * 是否可用
     */
    public boolean isUsable() {
        return "ACTIVE".equals(senderKeyStatus) && acknowledged;
    }
    
    /**
     * 是否需要轮换
     */
    public boolean needsRotation() {
        if (nextRotationTime != null && LocalDateTime.now().isAfter(nextRotationTime)) {
            return true;
        }
        if (expireAt != null && LocalDateTime.now().isAfter(expireAt)) {
            return true;
        }
        return false;
    }
}
