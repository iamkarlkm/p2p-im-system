package com.im.backend.entity;

import lombok.*;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 多模态内容理解 - 分析结果实体
 * 存储每次多模态内容分析的结果
 */
@Entity
@Table(name = "multimodal_analysis_result")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
public class MultimodalAnalysisResultEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "request_id", nullable = false, unique = true, length = 64)
    private String requestId;
    
    @Column(name = "session_id", length = 64)
    private String sessionId;
    
    @Column(name = "user_id")
    private Long userId;
    
    @Column(name = "message_id")
    private Long messageId;
    
    @Column(name = "content_type", nullable = false, length = 50)
    private String contentType;  // text/image/audio/video/mixed
    
    @Column(name = "content_hash", length = 64)
    private String contentHash;
    
    @Column(name = "analysis_status", nullable = false, length = 20)
    private String analysisStatus;  // pending/processing/completed/failed
    
    // 文本分析结果
    @Column(name = "text_analysis_result", columnDefinition = "JSON")
    private String textAnalysisResult;
    
    @Column(name = "text_summary", columnDefinition = "TEXT")
    private String textSummary;
    
    @Column(name = "text_keywords")
    private String textKeywords;  // 逗号分隔的关键词
    
    @Column(name = "text_sentiment")
    private String textSentiment;  // positive/negative/neutral/mixed
    
    @Column(name = "text_sentiment_score")
    private Double textSentimentScore;
    
    @Column(name = "text_intent")
    private String textIntent;  // 意图分类
    
    @Column(name = "text_entities", columnDefinition = "JSON")
    private String textEntities;  // 命名实体识别结果
    
    // 图像分析结果
    @Column(name = "image_analysis_result", columnDefinition = "JSON")
    private String imageAnalysisResult;
    
    @Column(name = "image_description", columnDefinition = "TEXT")
    private String imageDescription;
    
    @Column(name = "image_tags")
    private String imageTags;  // 逗号分隔的标签
    
    @Column(name = "image_objects", columnDefinition = "JSON")
    private String imageObjects;  // 检测到的物体
    
    @Column(name = "image_faces", columnDefinition = "JSON")
    private String imageFaces;  // 人脸检测结果
    
    @Column(name = "image_scene")
    private String imageScene;  // 场景分类
    
    @Column(name = "image_colors", columnDefinition = "JSON")
    private String imageColors;  // 主要颜色
    
    // 音频分析结果
    @Column(name = "audio_analysis_result", columnDefinition = "JSON")
    private String audioAnalysisResult;
    
    @Column(name = "audio_transcription", columnDefinition = "TEXT")
    private String audioTranscription;
    
    @Column(name = "audio_emotion")
    private String audioEmotion;  // 音频情感
    
    @Column(name = "audio_emotion_score")
    private Double audioEmotionScore;
    
    @Column(name = "audio_speakers")
    private Integer audioSpeakers;  // 说话人数量
    
    @Column(name = "audio_keywords")
    private String audioKeywords;  // 音频关键词
    
    // 视频分析结果
    @Column(name = "video_analysis_result", columnDefinition = "JSON")
    private String videoAnalysisResult;
    
    @Column(name = "video_description", columnDefinition = "TEXT")
    private String videoDescription;
    
    @Column(name = "video_scenes", columnDefinition = "JSON")
    private String videoScenes;  // 场景分割结果
    
    @Column(name = "video_key_frames", columnDefinition = "JSON")
    private String videoKeyFrames;  // 关键帧信息
    
    @Column(name = "video_motion_analysis", columnDefinition = "JSON")
    private String videoMotionAnalysis;
    
    // 多模态融合结果
    @Column(name = "multimodal_fusion_result", columnDefinition = "JSON")
    private String multimodalFusionResult;
    
    @Column(name = "multimodal_summary", columnDefinition = "TEXT")
    private String multimodalSummary;
    
    @Column(name = "multimodal_tags")
    private String multimodalTags;
    
    @Column(name = "multimodal_sentiment")
    private String multimodalSentiment;
    
    @Column(name = "multimodal_sentiment_score")
    private Double multimodalSentimentScore;
    
    @Column(name = "multimodal_intent")
    private String multimodalIntent;
    
    @Column(name = "cross_modal_correlations", columnDefinition = "JSON")
    private String crossModalCorrelations;  // 跨模态相关性
    
    // 质量评估
    @Column(name = "confidence_score")
    private Double confidenceScore;
    
    @Column(name = "quality_rating")
    private String qualityRating;  // high/medium/low
    
    @Column(name = "processing_time_ms")
    private Long processingTimeMs;
    
    @Column(name = "model_used")
    private String modelUsed;
    
    // 元数据
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "completed_at")
    private LocalDateTime completedAt;
    
    @Column(name = "version", nullable = false)
    @Version
    private Integer version;
    
    // 错误信息
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "error_code")
    private String errorCode;
    
    @Column(name = "retry_count")
    private Integer retryCount;
    
    // 缓存相关
    @Column(name = "is_cached")
    private Boolean cached;
    
    @Column(name = "cache_key", length = 128)
    private String cacheKey;
    
    @Column(name = "cache_expiry_at")
    private LocalDateTime cacheExpiryAt;
    
    // 业务相关
    @Column(name = "business_context")
    private String businessContext;
    
    @Column(name = "priority")
    private Integer priority;  // 处理优先级
    
    @Column(name = "cost_units")
    private Double costUnits;  // 计算成本单位
    
    // 审计字段
    @Column(name = "created_by", length = 100)
    private String createdBy;
    
    @Column(name = "updated_by", length = 100)
    private String updatedBy;
    
    // 扩展字段 (JSON格式)
    @Column(name = "extended_attributes", columnDefinition = "JSON")
    private String extendedAttributes;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (analysisStatus == null) {
            analysisStatus = "pending";
        }
        if (priority == null) {
            priority = 5;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        if ("completed".equals(analysisStatus) && completedAt == null) {
            completedAt = LocalDateTime.now();
        }
    }
    
    // 辅助方法
    public boolean isCompleted() {
        return "completed".equals(analysisStatus);
    }
    
    public boolean isFailed() {
        return "failed".equals(analysisStatus);
    }
    
    public boolean isProcessing() {
        return "processing".equals(analysisStatus);
    }
    
    public void markAsProcessing() {
        this.analysisStatus = "processing";
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsCompleted() {
        this.analysisStatus = "completed";
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsFailed(String errorMessage) {
        this.analysisStatus = "failed";
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }
}