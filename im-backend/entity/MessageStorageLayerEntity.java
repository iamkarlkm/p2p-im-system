package com.im.backend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * 消息存储分层配置实体
 * 用于管理消息存储的冷热分层策略
 */
@Entity
@Table(name = "message_storage_layer")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageStorageLayerEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 分层策略名称
     */
    @Column(name = "strategy_name", nullable = false, unique = true, length = 100)
    private String strategyName;
    
    /**
     * 策略描述
     */
    @Column(name = "description", length = 500)
    private String description;
    
    /**
     * 热存储天数（默认30天）
     */
    @Column(name = "hot_storage_days", nullable = false)
    @Builder.Default
    private Integer hotStorageDays = 30;
    
    /**
     * 温存储天数（30-90天）
     */
    @Column(name = "warm_storage_days", nullable = false)
    @Builder.Default
    private Integer warmStorageDays = 90;
    
    /**
     * 冷存储类型：
     * - S3: AWS S3存储
     * - AZURE_BLOB: Azure Blob存储
     * - GCS: Google Cloud Storage
     * - OSS: 阿里云OSS
     * - CUSTOM: 自定义存储
     */
    @Column(name = "cold_storage_type", nullable = false, length = 50)
    @Builder.Default
    private String coldStorageType = "S3";
    
    /**
     * 冷存储桶名称
     */
    @Column(name = "cold_storage_bucket", length = 200)
    private String coldStorageBucket;
    
    /**
     * 冷存储路径前缀
     */
    @Column(name = "cold_storage_prefix", length = 200)
    @Builder.Default
    private String coldStoragePrefix = "messages/";
    
    /**
     * 归档压缩格式：
     * - NONE: 不压缩
     * - GZIP: GZIP压缩
     * - ZSTD: Zstandard压缩
     * - LZ4: LZ4压缩
     */
    @Column(name = "compression_format", length = 20)
    @Builder.Default
    private String compressionFormat = "GZIP";
    
    /**
     * 是否启用加密
     */
    @Column(name = "encryption_enabled")
    @Builder.Default
    private Boolean encryptionEnabled = false;
    
    /**
     * 加密算法（如果启用加密）
     */
    @Column(name = "encryption_algorithm", length = 50)
    private String encryptionAlgorithm;
    
    /**
     * 归档批次大小（每次归档的消息数量）
     */
    @Column(name = "archive_batch_size")
    @Builder.Default
    private Integer archiveBatchSize = 1000;
    
    /**
     * 归档时间间隔（分钟）
     */
    @Column(name = "archive_interval_minutes")
    @Builder.Default
    private Integer archiveIntervalMinutes = 60;
    
    /**
     * 归档任务并发数
     */
    @Column(name = "archive_concurrency")
    @Builder.Default
    private Integer archiveConcurrency = 3;
    
    /**
     * 是否启用自动归档
     */
    @Column(name = "auto_archive_enabled")
    @Builder.Default
    private Boolean autoArchiveEnabled = true;
    
    /**
     * 是否启用自动清理（删除已归档消息）
     */
    @Column(name = "auto_cleanup_enabled")
    @Builder.Default
    private Boolean autoCleanupEnabled = true;
    
    /**
     * 清理保留天数（归档后保留多少天清理）
     */
    @Column(name = "cleanup_retention_days")
    @Builder.Default
    private Integer cleanupRetentionDays = 7;
    
    /**
     * 是否启用智能分层（根据访问频率自动调整）
     */
    @Column(name = "smart_layering_enabled")
    @Builder.Default
    private Boolean smartLayeringEnabled = false;
    
    /**
     * 智能分层访问阈值（天）
     */
    @Column(name = "smart_access_threshold")
    @Builder.Default
    private Integer smartAccessThreshold = 180;
    
    /**
     * 状态：ENABLED, DISABLED, ERROR
     */
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private String status = "ENABLED";
    
    /**
     * 最后归档时间
     */
    @Column(name = "last_archive_time")
    private LocalDateTime lastArchiveTime;
    
    /**
     * 最后归档消息ID
     */
    @Column(name = "last_archive_message_id")
    private Long lastArchiveMessageId;
    
    /**
     * 已归档消息数量
     */
    @Column(name = "archived_messages_count")
    @Builder.Default
    private Long archivedMessagesCount = 0L;
    
    /**
     * 已归档消息大小（字节）
     */
    @Column(name = "archived_messages_size")
    @Builder.Default
    private Long archivedMessagesSize = 0L;
    
    /**
     * 最后清理时间
     */
    @Column(name = "last_cleanup_time")
    private LocalDateTime lastCleanupTime;
    
    /**
     * 已清理消息数量
     */
    @Column(name = "cleaned_messages_count")
    @Builder.Default
    private Long cleanedMessagesCount = 0L;
    
    /**
     * 错误信息（如果有）
     */
    @Column(name = "error_message", length = 1000)
    private String errorMessage;
    
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
     * 获取存储层类型
     * @param messageDate 消息时间
     * @return HOT, WARM, COLD
     */
    public String getStorageLayer(LocalDateTime messageDate) {
        LocalDateTime now = LocalDateTime.now();
        long daysDiff = java.time.Duration.between(messageDate, now).toDays();
        
        if (daysDiff <= hotStorageDays) {
            return "HOT";
        } else if (daysDiff <= warmStorageDays) {
            return "WARM";
        } else {
            return "COLD";
        }
    }
    
    /**
     * 判断消息是否需要归档
     * @param messageDate 消息时间
     * @return 是否需要归档
     */
    public boolean shouldArchive(LocalDateTime messageDate) {
        return getStorageLayer(messageDate).equals("COLD") && autoArchiveEnabled;
    }
    
    /**
     * 判断消息是否需要清理
     * @param messageDate 消息时间
     * @return 是否需要清理
     */
    public boolean shouldCleanup(LocalDateTime messageDate) {
        if (!autoCleanupEnabled) {
            return false;
        }
        
        LocalDateTime now = LocalDateTime.now();
        long daysDiff = java.time.Duration.between(messageDate, now).toDays();
        return daysDiff > warmStorageDays + cleanupRetentionDays;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}