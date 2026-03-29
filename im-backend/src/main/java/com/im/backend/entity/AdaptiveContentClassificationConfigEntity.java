package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 自适应内容分类配置实体
 * 支持自定义分类体系、增量学习和多模态内容分类
 */
@Entity
@Table(name = "adaptive_content_classification_config", 
       indexes = {
           @Index(name = "idx_user_id", columnList = "userId"),
           @Index(name = "idx_session_id", columnList = "sessionId"),
           @Index(name = "idx_status", columnList = "status"),
           @Index(name = "idx_created_at", columnList = "createdAt")
       })
public class AdaptiveContentClassificationConfigEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private Long userId;
    
    private Long sessionId;
    
    @Column(nullable = false)
    private String categoryHierarchy; // JSON 格式的分类层级
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassificationType classificationType;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentModality contentModality;
    
    @Column(nullable = false)
    private Integer minConfidenceScore = 70;
    
    @Column(nullable = false)
    private Boolean enableIncrementalLearning = true;
    
    @Column(nullable = false)
    private Integer incrementalLearningBatchSize = 100;
    
    @Column(nullable = false)
    private Boolean enableContextAwareness = true;
    
    @Column(nullable = false)
    private Integer contextWindowSize = 10;
    
    @Column(nullable = false)
    private Boolean enableMultiLanguage = true;
    
    private String supportedLanguages; // JSON 数组格式
    
    @Column(nullable = false)
    private Boolean enableAutoLabelRecommendation = true;
    
    @Column(nullable = false)
    private Integer maxLabelRecommendations = 5;
    
    @Column(nullable = false)
    private Boolean enablePrivacyProtection = true;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassificationPrivacyLevel privacyLevel;
    
    @Column(nullable = false)
    private Boolean enableEvolutionTracking = true;
    
    @Column(nullable = false)
    private Integer evolutionTrackingDepth = 30;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassificationStatus status;
    
    @Column(nullable = false)
    private Integer version = 1;
    
    private String versionNotes;
    
    @Column(nullable = false)
    private Double accuracyScore;
    
    @Column(nullable = false)
    private Integer totalClassifications;
    
    @Column(nullable = false)
    private Integer correctClassifications;
    
    private String feedbackStatistics; // JSON 格式的反馈统计
    
    private String performanceMetrics; // JSON 格式的性能指标
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        
        if (accuracyScore == null) accuracyScore = 0.0;
        if (totalClassifications == null) totalClassifications = 0;
        if (correctClassifications == null) correctClassifications = 0;
        if (version == null) version = 1;
        if (minConfidenceScore == null) minConfidenceScore = 70;
        if (incrementalLearningBatchSize == null) incrementalLearningBatchSize = 100;
        if (contextWindowSize == null) contextWindowSize = 10;
        if (maxLabelRecommendations == null) maxLabelRecommendations = 5;
        if (evolutionTrackingDepth == null) evolutionTrackingDepth = 30;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 枚举定义
    
    public enum ClassificationType {
        HIERARCHICAL,  // 层级分类
        FLAT,          // 扁平分类
        HYBRID         // 混合分类
    }
    
    public enum ContentModality {
        TEXT_ONLY,      // 仅文本
        IMAGE_ONLY,     // 仅图像
        AUDIO_ONLY,     // 仅音频
        VIDEO_ONLY,     // 仅视频
        MULTIMODAL      // 多模态
    }
    
    public enum ClassificationPrivacyLevel {
        PUBLIC,         // 公开
        PROTECTED,      // 受保护
        PRIVATE,        // 私有
        CONFIDENTIAL    // 机密
    }
    
    public enum ClassificationStatus {
        DRAFT,          // 草稿
        ACTIVE,         // 活跃
        INACTIVE,       // 非活跃
        ARCHIVED,       // 已归档
        DELETED         // 已删除
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public Long getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }
    
    public String getCategoryHierarchy() {
        return categoryHierarchy;
    }
    
    public void setCategoryHierarchy(String categoryHierarchy) {
        this.categoryHierarchy = categoryHierarchy;
    }
    
    public ClassificationType getClassificationType() {
        return classificationType;
    }
    
    public void setClassificationType(ClassificationType classificationType) {
        this.classificationType = classificationType;
    }
    
    public ContentModality getContentModality() {
        return contentModality;
    }
    
    public void setContentModality(ContentModality contentModality) {
        this.contentModality = contentModality;
    }
    
    public Integer getMinConfidenceScore() {
        return minConfidenceScore;
    }
    
    public void setMinConfidenceScore(Integer minConfidenceScore) {
        this.minConfidenceScore = minConfidenceScore;
    }
    
    public Boolean getEnableIncrementalLearning() {
        return enableIncrementalLearning;
    }
    
    public void setEnableIncrementalLearning(Boolean enableIncrementalLearning) {
        this.enableIncrementalLearning = enableIncrementalLearning;
    }
    
    public Integer getIncrementalLearningBatchSize() {
        return incrementalLearningBatchSize;
    }
    
    public void setIncrementalLearningBatchSize(Integer incrementalLearningBatchSize) {
        this.incrementalLearningBatchSize = incrementalLearningBatchSize;
    }
    
    public Boolean getEnableContextAwareness() {
        return enableContextAwareness;
    }
    
    public void setEnableContextAwareness(Boolean enableContextAwareness) {
        this.enableContextAwareness = enableContextAwareness;
    }
    
    public Integer getContextWindowSize() {
        return contextWindowSize;
    }
    
    public void setContextWindowSize(Integer contextWindowSize) {
        this.contextWindowSize = contextWindowSize;
    }
    
    public Boolean getEnableMultiLanguage() {
        return enableMultiLanguage;
    }
    
    public void setEnableMultiLanguage(Boolean enableMultiLanguage) {
        this.enableMultiLanguage = enableMultiLanguage;
    }
    
    public String getSupportedLanguages() {
        return supportedLanguages;
    }
    
    public void setSupportedLanguages(String supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }
    
    public Boolean getEnableAutoLabelRecommendation() {
        return enableAutoLabelRecommendation;
    }
    
    public void setEnableAutoLabelRecommendation(Boolean enableAutoLabelRecommendation) {
        this.enableAutoLabelRecommendation = enableAutoLabelRecommendation;
    }
    
    public Integer getMaxLabelRecommendations() {
        return maxLabelRecommendations;
    }
    
    public void setMaxLabelRecommendations(Integer maxLabelRecommendations) {
        this.maxLabelRecommendations = maxLabelRecommendations;
    }
    
    public Boolean getEnablePrivacyProtection() {
        return enablePrivacyProtection;
    }
    
    public void setEnablePrivacyProtection(Boolean enablePrivacyProtection) {
        this.enablePrivacyProtection = enablePrivacyProtection;
    }
    
    public ClassificationPrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }
    
    public void setPrivacyLevel(ClassificationPrivacyLevel privacyLevel) {
        this.privacyLevel = privacyLevel;
    }
    
    public Boolean getEnableEvolutionTracking() {
        return enableEvolutionTracking;
    }
    
    public void setEnableEvolutionTracking(Boolean enableEvolutionTracking) {
        this.enableEvolutionTracking = enableEvolutionTracking;
    }
    
    public Integer getEvolutionTrackingDepth() {
        return evolutionTrackingDepth;
    }
    
    public void setEvolutionTrackingDepth(Integer evolutionTrackingDepth) {
        this.evolutionTrackingDepth = evolutionTrackingDepth;
    }
    
    public ClassificationStatus getStatus() {
        return status;
    }
    
    public void setStatus(ClassificationStatus status) {
        this.status = status;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    public String getVersionNotes() {
        return versionNotes;
    }
    
    public void setVersionNotes(String versionNotes) {
        this.versionNotes = versionNotes;
    }
    
    public Double getAccuracyScore() {
        return accuracyScore;
    }
    
    public void setAccuracyScore(Double accuracyScore) {
        this.accuracyScore = accuracyScore;
    }
    
    public Integer getTotalClassifications() {
        return totalClassifications;
    }
    
    public void setTotalClassifications(Integer totalClassifications) {
        this.totalClassifications = totalClassifications;
    }
    
    public Integer getCorrectClassifications() {
        return correctClassifications;
    }
    
    public void setCorrectClassifications(Integer correctClassifications) {
        this.correctClassifications = correctClassifications;
    }
    
    public String getFeedbackStatistics() {
        return feedbackStatistics;
    }
    
    public void setFeedbackStatistics(String feedbackStatistics) {
        this.feedbackStatistics = feedbackStatistics;
    }
    
    public String getPerformanceMetrics() {
        return performanceMetrics;
    }
    
    public void setPerformanceMetrics(String performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}