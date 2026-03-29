package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息归档记录实体
 * 记录已归档到对象存储的消息
 */
@Entity
@Table(name = "message_archive", indexes = {
    @Index(name = "idx_archive_message_id", columnList = "messageId"),
    @Index(name = "idx_archive_strategy_id", columnList = "storageStrategyId"),
    @Index(name = "idx_archive_sender_id", columnList = "senderId"),
    @Index(name = "idx_archive_session_id", columnList = "sessionId"),
    @Index(name = "idx_archive_archive_time", columnList = "archiveTime"),
    @Index(name = "idx_archive_status", columnList = "status")
})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageArchiveEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 原始消息ID
     */
    @Column(name = "message_id", nullable = false)
    private Long messageId;
    
    /**
     * 原始消息内容
     */
    @Column(name = "original_content", columnDefinition = "TEXT")
    private String originalContent;
    
    /**
     * 原始消息创建时间
     */
    @Column(name = "original_created_at", nullable = false)
    private LocalDateTime originalCreatedAt;
    
    /**
     * 发送者ID
     */
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    
    /**
     * 会话ID
     */
    @Column(name = "session_id", nullable = false)
    private Long sessionId;
    
    /**
     * 消息类型：TEXT, IMAGE, VOICE, VIDEO, FILE, SYSTEM
     */
    @Column(name = "message_type", nullable = false, length = 50)
    private String messageType;
    
    /**
     * 存储策略ID
     */
    @Column(name = "storage_strategy_id", nullable = false)
    private Long storageStrategyId;
    
    /**
     * 归档时间
     */
    @Column(name = "archive_time", nullable = false)
    @Builder.Default
    private LocalDateTime archiveTime = LocalDateTime.now();
    
    /**
     * 存储路径（对象存储中的路径）
     */
    @Column(name = "storage_path", length = 500)
    private String storagePath;
    
    /**
     * 存储桶名称
     */
    @Column(name = "storage_bucket", length = 200)
    private String storageBucket;
    
    /**
     * 压缩格式
     */
    @Column(name = "compression_format", length = 20)
    private String compressionFormat;
    
    /**
     * 是否启用加密
     */
    @Column(name = "encryption_enabled")
    private Boolean encryptionEnabled;
    
    /**
     * 加密算法
     */
    @Column(name = "encryption_algorithm", length = 50)
    private String encryptionAlgorithm;
    
    /**
     * 归档后的消息大小（字节）
     */
    @Column(name = "archived_size")
    private Long archivedSize;
    
    /**
     * 状态：
     * - ARCHIVED: 已归档（数据库记录）
     * - UPLOADED: 已上传到对象存储
     * - RESTORED: 已恢复
     * - DELETED: 已删除
     * - ERROR: 错误
     */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ARCHIVED";
    
    /**
     * 恢复时间（如果已恢复）
     */
    @Column(name = "restored_time")
    private LocalDateTime restoredTime;
    
    /**
     * 恢复者ID（如果已恢复）
     */
    @Column(name = "restored_by")
    private Long restoredBy;
    
    /**
     * 删除时间（如果已删除）
     */
    @Column(name = "deleted_time")
    private LocalDateTime deletedTime;
    
    /**
     * 删除者ID（如果已删除）
     */
    @Column(name = "deleted_by")
    private Long deletedBy;
    
    /**
     * 错误信息
     */
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
    /**
     * 校验和（用于验证数据完整性）
     */
    @Column(name = "checksum", length = 64)
    private String checksum;
    
    /**
     * 元数据（JSON格式，存储附加信息）
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;
    
    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
    
    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();
    
    /**
     * 获取归档文件名
     */
    public String getArchiveFileName() {
        if (storagePath == null) {
            return null;
        }
        int lastSlash = storagePath.lastIndexOf('/');
        if (lastSlash >= 0) {
            return storagePath.substring(lastSlash + 1);
        }
        return storagePath;
    }
    
    /**
     * 判断是否已上传到对象存储
     */
    public boolean isUploaded() {
        return "UPLOADED".equals(status) || "RESTORED".equals(status);
    }
    
    /**
     * 判断是否可以恢复
     */
    public boolean canRestore() {
        return "UPLOADED".equals(status) && storagePath != null && storageBucket != null;
    }
    
    /**
     * 判断是否可以删除
     */
    public boolean canDelete() {
        return !"DELETED".equals(status);
    }
    
    /**
     * 标记为已上传
     */
    public void markAsUploaded(String path, String bucket, Long size) {
        this.storagePath = path;
        this.storageBucket = bucket;
        this.archivedSize = size;
        this.status = "UPLOADED";
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 标记为已恢复
     */
    public void markAsRestored(Long restoredBy) {
        this.restoredTime = LocalDateTime.now();
        this.restoredBy = restoredBy;
        this.status = "RESTORED";
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 标记为已删除
     */
    public void markAsDeleted(Long deletedBy) {
        this.deletedTime = LocalDateTime.now();
        this.deletedBy = deletedBy;
        this.status = "DELETED";
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 标记为错误
     */
    public void markAsError(String errorMessage) {
        this.errorMessage = errorMessage;
        this.status = "ERROR";
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 计算校验和
     */
    public void calculateChecksum() {
        // 简单实现，实际应该使用SHA-256等算法
        String data = String.format("%d|%s|%s|%d|%d|%s", 
            messageId, originalContent, originalCreatedAt, senderId, sessionId, messageType);
        this.checksum = Integer.toHexString(data.hashCode());
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}