package com.im.backend.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * 多模态内容理解引擎 - 配置实体
 * 用于存储和管理多模态理解的全局配置
 */
@Entity
@Table(name = "multimodal_config")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class MultimodalConfigEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "description", columnDefinition = "TEXT")
    private String description;
    
    @Column(name = "is_enabled", nullable = false)
    private Boolean enabled;
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "version", nullable = false)
    @Version
    private Integer version;
    
    // 文本理解配置
    @Column(name = "text_enabled", nullable = false)
    private Boolean textEnabled;
    
    @Column(name = "text_model", length = 100)
    private String textModel;
    
    @Column(name = "text_max_length")
    private Integer textMaxLength;
    
    @Column(name = "text_languages")
    private String textLanguages;  // 逗号分隔的语言列表
    
    // 图像理解配置
    @Column(name = "image_enabled", nullable = false)
    private Boolean imageEnabled;
    
    @Column(name = "image_model", length = 100)
    private String imageModel;
    
    @Column(name = "image_max_size")
    private Integer imageMaxSize;  // 最大图片尺寸 (KB)
    
    @Column(name = "image_supported_formats")
    private String imageSupportedFormats;  // 支持的图片格式
    
    // 语音理解配置
    @Column(name = "audio_enabled", nullable = false)
    private Boolean audioEnabled;
    
    @Column(name = "audio_model", length = 100)
    private String audioModel;
    
    @Column(name = "audio_max_duration")
    private Integer audioMaxDuration;  // 最大音频时长 (秒)
    
    @Column(name = "audio_supported_formats")
    private String audioSupportedFormats;  // 支持的音频格式
    
    // 视频理解配置
    @Column(name = "video_enabled", nullable = false)
    private Boolean videoEnabled;
    
    @Column(name = "video_model", length = 100)
    private String videoModel;
    
    @Column(name = "video_max_duration")
    private Integer videoMaxDuration;  // 最大视频时长 (秒)
    
    @Column(name = "video_max_size")
    private Integer videoMaxSize;  // 最大视频尺寸 (MB)
    
    // 多模态融合配置
    @Column(name = "multimodal_fusion_enabled", nullable = false)
    private Boolean multimodalFusionEnabled;
    
    @Column(name = "fusion_method", length = 50)
    private String fusionMethod;  // early/late/hybrid
    
    @Column(name = "cross_modal_weighting")
    private String crossModalWeighting;  // JSON格式的权重配置
    
    // 缓存配置
    @Column(name = "cache_enabled", nullable = false)
    private Boolean cacheEnabled;
    
    @Column(name = "cache_ttl_hours")
    private Integer cacheTtlHours;
    
    @Column(name = "cache_max_size")
    private Integer cacheMaxSize;
    
    // 性能配置
    @Column(name = "concurrent_workers")
    private Integer concurrentWorkers;
    
    @Column(name = "timeout_ms")
    private Integer timeoutMs;
    
    @Column(name = "batch_size")
    private Integer batchSize;
    
    // 质量配置
    @Column(name = "confidence_threshold")
    private Double confidenceThreshold;
    
    @Column(name = "fallback_enabled")
    private Boolean fallbackEnabled;
    
    @Column(name = "fallback_model", length = 100)
    private String fallbackModel;
    
    // 监控配置
    @Column(name = "metrics_enabled", nullable = false)
    private Boolean metricsEnabled;
    
    @Column(name = "metrics_interval_minutes")
    private Integer metricsIntervalMinutes;
    
    @Column(name = "alert_threshold_error_rate")
    private Double alertThresholdErrorRate;
    
    @Column(name = "alert_threshold_latency_ms")
    private Integer alertThresholdLatencyMs;
    
    // 隐私配置
    @Column(name = "privacy_enabled", nullable = false)
    private Boolean privacyEnabled;
    
    @Column(name = "anonymization_enabled")
    private Boolean anonymizationEnabled;
    
    @Column(name = "data_retention_days")
    private Integer dataRetentionDays;
    
    // 自定义配置 (JSON格式)
    @Column(name = "custom_config", columnDefinition = "JSON")
    private String customConfig;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}