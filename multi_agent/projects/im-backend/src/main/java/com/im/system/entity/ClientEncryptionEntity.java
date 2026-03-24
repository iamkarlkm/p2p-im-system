package com.im.system.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 客户端加密本地存储实体
 * 管理客户端加密密钥、加密配置和离线消息队列
 */
@Entity
@Table(name = "client_encryption")
@Data
public class ClientEncryptionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户 ID
     */
    @Column(name = "user_id", nullable = false, unique = true)
    private Long userId;
    
    /**
     * 主加密密钥（AES-256）
     */
    @Column(name = "master_key", columnDefinition = "TEXT")
    private String masterKey;
    
    /**
     * 密钥派生盐值
     */
    @Column(name = "key_derivation_salt", length = 128)
    private String keyDerivationSalt;
    
    /**
     * 加密算法：AES-256-GCM/ChaCha20-Poly1305
     */
    @Column(name = "encryption_algorithm", length = 32)
    private String encryptionAlgorithm = "AES-256-GCM";
    
    /**
     * 密钥版本
     */
    @Column(name = "key_version")
    private Integer keyVersion = 1;
    
    /**
     * 密钥创建时间
     */
    @Column(name = "key_created_at")
    private LocalDateTime keyCreatedAt;
    
    /**
     * 密钥最后使用时间
     */
    @Column(name = "key_last_used_at")
    private LocalDateTime keyLastUsedAt;
    
    /**
     * 密钥过期时间（可选）
     */
    @Column(name = "key_expires_at")
    private LocalDateTime keyExpiresAt;
    
    /**
     * 是否启用加密：true/false
     */
    @Column(name = "encryption_enabled")
    private Boolean encryptionEnabled = false;
    
    /**
     * 加密范围：ALL/MESSAGES_ONLY/SENSITIVE_ONLY
     */
    @Column(name = "encryption_scope", length = 32)
    private String encryptionScope = "ALL";
    
    /**
     * 离线消息队列大小限制
     */
    @Column(name = "offline_queue_limit")
    private Integer offlineQueueLimit = 1000;
    
    /**
     * 离线消息保留天数
     */
    @Column(name = "offline_retention_days")
    private Integer offlineRetentionDays = 7;
    
    /**
     * 最后同步时间
     */
    @Column(name = "last_sync_at")
    private LocalDateTime lastSyncAt;
    
    /**
     * 同步状态：SYNCED/SYNCING/PENDING/ERROR
     */
    @Column(name = "sync_status", length = 32)
    private String syncStatus = "SYNCED";
    
    /**
     * 加密统计：已加密消息数
     */
    @Column(name = "encrypted_message_count")
    private Long encryptedMessageCount = 0L;
    
    /**
     * 加密统计：已解密消息数
     */
    @Column(name = "decrypted_message_count")
    private Long decryptedMessageCount = 0L;
    
    /**
     * 加密统计：加密失败次数
     */
    @Column(name = "encryption_failure_count")
    private Long encryptionFailureCount = 0L;
    
    /**
     * 加密统计：解密失败次数
     */
    @Column(name = "decryption_failure_count")
    private Long decryptionFailureCount = 0L;
    
    /**
     * 客户端信息（JSON 格式）
     */
    @Column(name = "client_info", columnDefinition = "TEXT")
    private String clientInfo;
    
    /**
     * 加密配置（JSON 格式）
     */
    @Column(name = "encryption_config", columnDefinition = "TEXT")
    private String encryptionConfig;
    
    /**
     * 备份密钥（用于恢复，加密存储）
     */
    @Column(name = "backup_key", columnDefinition = "TEXT")
    private String backupKey;
    
    /**
     * 备份密钥创建时间
     */
    @Column(name = "backup_key_created_at")
    private LocalDateTime backupKeyCreatedAt;
    
    /**
     * 是否已备份：true/false
     */
    @Column(name = "is_backed_up")
    private Boolean backedUp = false;
    
    /**
     * 创建设备 ID
     */
    @Column(name = "device_id", length = 128)
    private String deviceId;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        keyCreatedAt = LocalDateTime.now();
        keyLastUsedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        keyLastUsedAt = LocalDateTime.now();
    }
    
    /**
     * 加密算法枚举
     */
    public enum EncryptionAlgorithm {
        AES_256_GCM,
        CHACHA20_POLY1305,
        AES_128_GCM
    }
    
    /**
     * 加密范围枚举
     */
    public enum EncryptionScope {
        ALL,            // 所有数据
        MESSAGES_ONLY,  // 仅消息
        SENSITIVE_ONLY  // 仅敏感数据
    }
    
    /**
     * 同步状态枚举
     */
    public enum SyncStatus {
        SYNCED,     // 已同步
        SYNCING,    // 正在同步
        PENDING,    // 等待同步
        ERROR       // 同步错误
    }
}