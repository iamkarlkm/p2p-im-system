package com.im.backend.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 消息存储冷热分层配置实体
 * 定义消息数据的热存储和冷存储策略
 * 
 * @author im-backend
 * @version 1.0.0
 * @since 2026-03-22
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "im_message_storage_policy")
public class MessageStoragePolicy implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    /**
     * 策略ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 策略名称
     */
    @Column(name = "policy_name", length = 100, nullable = false)
    private String policyName;
    
    /**
     * 策略描述
     */
    @Column(name = "description", length = 500)
    private String description;
    
    /**
     * 热存储天数 (默认30天)
     * 在此天数内的消息存储在MySQL热存储中
     */
    @Column(name = "hot_storage_days", nullable = false)
    private Integer hotStorageDays = 30;
    
    /**
     * 冷存储策略类型
     * - NONE: 不启用冷存储
     * - S3: Amazon S3
     * - OSS: 阿里云OSS
     * - COS: 腾讯云COS
     * - MINIO: MinIO对象存储
     */
    @Column(name = "cold_storage_type", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private ColdStorageType coldStorageType = ColdStorageType.NONE;
    
    /**
     * 冷存储桶名称
     */
    @Column(name = "cold_bucket_name", length = 100)
    private String coldBucketName;
    
    /**
     * 冷存储端点URL
     */
    @Column(name = "cold_storage_endpoint", length = 255)
    private String coldStorageEndpoint;
    
    /**
     * 冷存储区域
     */
    @Column(name = "cold_storage_region", length = 50)
    private String coldStorageRegion;
    
    /**
     * 是否启用压缩
     */
    @Column(name = "enable_compression", nullable = false)
    private Boolean enableCompression = true;
    
    /**
     * 压缩算法 (GZIP, SNAPPY, LZ4)
     */
    @Column(name = "compression_algorithm", length = 20)
    private String compressionAlgorithm = "GZIP";
    
    /**
     * 是否启用加密
     */
    @Column(name = "enable_encryption", nullable = false)
    private Boolean enableEncryption = false;
    
    /**
     * 加密密钥ID
     */
    @Column(name = "encryption_key_id", length = 100)
    private String encryptionKeyId;
    
    /**
     * 归档文件格式 (JSON, PARQUET, AVRO)
     */
    @Column(name = "archive_format", length = 20, nullable = false)
    private String archiveFormat = "JSON";
    
    /**
     * 归档文件大小阈值 (MB)
     * 达到此大小后创建新的归档文件
     */
    @Column(name = "archive_file_size_mb", nullable = false)
    private Integer archiveFileSizeMb = 100;
    
    /**
     * 归档文件命名模式
     * 支持变量：{date}, {session_id}, {timestamp}
     */
    @Column(name = "archive_naming_pattern", length = 200)
    private String archiveNamingPattern = "messages/{date}/{session_id}_{timestamp}.json";
    
    /**
     * 是否启用自动归档
     */
    @Column(name = "enable_auto_archive", nullable = false)
    private Boolean enableAutoArchive = true;
    
    /**
     * 自动归档时间 (Cron表达式)
     */
    @Column(name = "auto_archive_cron", length = 50)
    private String autoArchiveCron = "0 2 * * * ?"; // 每天凌晨2点
    
    /**
     * 归档后是否删除热存储数据
     */
    @Column(name = "delete_after_archive", nullable = false)
    private Boolean deleteAfterArchive = false;
    
    /**
     * 归档保留天数 (0表示永久保留)
     */
    @Column(name = "archive_retention_days", nullable = false)
    private Integer archiveRetentionDays = 0;
    
    /**
     * 是否启用归档统计
     */
    @Column(name = "enable_archive_statistics", nullable = false)
    private Boolean enableArchiveStatistics = true;
    
    /**
     * 策略优先级 (数字越小优先级越高)
     */
    @Column(name = "priority", nullable = false)
    private Integer priority = 10;
    
    /**
     * 策略状态
     */
    @Column(name = "status", length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private PolicyStatus status = PolicyStatus.ACTIVE;
    
    /**
     * 适用会话类型 (ALL, PRIVATE, GROUP, CHANNEL)
     */
    @Column(name = "session_type", length = 20)
    private String sessionType = "ALL";
    
    /**
     * 适用的会话ID列表 (为空表示适用所有会话)
     */
    @Column(name = "applicable_session_ids", columnDefinition = "TEXT")
    private String applicableSessionIds;
    
    /**
     * 创建时间
     */
    @Column(name = "created_time", nullable = false, updatable = false)
    private LocalDateTime createdTime = LocalDateTime.now();
    
    /**
     * 更新时间
     */
    @Column(name = "updated_time")
    private LocalDateTime updatedTime;
    
    /**
     * 创建者ID
     */
    @Column(name = "created_by", length = 50)
    private String createdBy;
    
    /**
     * 更新者ID
     */
    @Column(name = "updated_by", length = 50)
    private String updatedBy;
    
    /**
     * 版本号
     */
    @Version
    @Column(name = "version", nullable = false)
    private Integer version = 0;
    
    /**
     * 冷存储类型枚举
     */
    public enum ColdStorageType {
        NONE,   // 不启用冷存储
        S3,     // Amazon S3
        OSS,    // 阿里云OSS
        COS,    // 腾讯云COS
        MINIO,  // MinIO对象存储
        AZURE,  // Azure Blob Storage
        GCS     // Google Cloud Storage
    }
    
    /**
     * 策略状态枚举
     */
    public enum PolicyStatus {
        ACTIVE,     // 启用
        INACTIVE,   // 禁用
        TESTING,    // 测试中
        ARCHIVED    // 已归档
    }
}