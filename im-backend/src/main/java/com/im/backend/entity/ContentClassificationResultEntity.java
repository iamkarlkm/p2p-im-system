package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 内容分类结果实体
 * 存储分类结果、置信度评分和演进追踪信息
 */
@Entity
@Table(name = "content_classification_result", 
       indexes = {
           @Index(name = "idx_classification_config_id", columnList = "classificationConfigId"),
           @Index(name = "idx_content_id", columnList = "contentId"),
           @Index(name = "idx_content_type", columnList = "contentType"),
           @Index(name = "idx_user_id", columnList = "userId"),
           @Index(name = "idx_session_id", columnList = "sessionId"),
           @Index(name = "idx_primary_category", columnList = "primaryCategory"),
           @Index(name = "idx_confidence_score", columnList = "confidenceScore"),
           @Index(name = "idx_created_at", columnList = "createdAt")
       })
public class ContentClassificationResultEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private Long classificationConfigId;
    
    @Column(nullable = false)
    private Long contentId;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;
    
    @Column(nullable = false)
    private Long userId;
    
    private Long sessionId;
    
    @Column(nullable = false)
    private String primaryCategory;
    
    @Column(length = 1000)
    private String secondaryCategories; // JSON 数组格式
    
    @Column(nullable = false)
    private Integer confidenceScore;
    
    @Column(length = 2000)
    private String classificationEvidence; // JSON 格式的分类依据
    
    @Column(nullable = false)
    private Boolean isContextAware = false;
    
    @Column(length = 2000)
    private String contextInformation; // JSON 格式的上下文信息
    
    @Column(nullable = false)
    private Boolean isMultiModal = false;
    
    @Column(length = 2000)
    private String multiModalAnalysis; // JSON 格式的多模态分析结果
    
    @Column(nullable = false)
    private Boolean isAutoLabelRecommended = false;
    
    @Column(length = 2000)
    private String recommendedLabels; // JSON 数组格式的推荐标签
    
    @Column(nullable = false)
    private Boolean hasUserFeedback = false;
    
    @Column(length = 1000)
    private String userFeedback; // JSON 格式的用户反馈
    
    @Column(nullable = false)
    private Boolean isPrivacyProtected = false;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ClassificationPrivacyLevel privacyLevel;
    
    @Column(nullable = false)
    private Boolean isEvolutionTracked = false;
    
    @Column(length = 2000)
    private String evolutionHistory; // JSON 格式的演进历史
    
    @Column(nullable = false)
    private Integer classificationVersion;
    
    @Column(length = 1000)
    private String versionChanges; // JSON 格式的版本变更说明
    
    @Column(nullable = false)
    private Double accuracyContribution;
    
    @Column(nullable = false)
    private Boolean isTrainingExample = false;
    
    @Column(length = 2000)
    private String modelFeatures; // JSON 格式的模型特征
    
    @Column(nullable = false)
    private Boolean isAnomalyDetected = false;
    
    @Column(length = 1000)
    private String anomalyDetails; // JSON 格式的异常详情
    
    @Column(nullable = false)
    private String languageCode;
    
    @Column(nullable = false)
    private String contentLanguage;
    
    @Column(length = 2000)
    private String crossLanguageMapping; // JSON 格式的跨语言映射
    
    @Column(nullable = false)
    private LocalDateTime contentCreatedAt;
    
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        
        if (confidenceScore == null) confidenceScore = 0;
        if (classificationVersion == null) classificationVersion = 1;
        if (accuracyContribution == null) accuracyContribution = 0.0;
        if (languageCode == null) languageCode = "en";
        if (contentLanguage == null) contentLanguage = "en";
        if (contentCreatedAt == null) contentCreatedAt = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 枚举定义
    
    public enum ContentType {
        TEXT_MESSAGE,    // 文本消息
        IMAGE_MESSAGE,   // 图片消息
        AUDIO_MESSAGE,   // 音频消息
        VIDEO_MESSAGE,   // 视频消息
        FILE_MESSAGE,    // 文件消息
        LOCATION_MESSAGE, // 位置消息
        CONTACT_MESSAGE, // 联系人消息
        SYSTEM_MESSAGE,  // 系统消息
        LINK_MESSAGE,    // 链接消息
        POLL_MESSAGE,    // 投票消息
        EMOJI_MESSAGE,   // 表情消息
        STICKER_MESSAGE  // 贴纸消息
    }
    
    public enum ClassificationPrivacyLevel {
        PUBLIC,         // 公开
        PROTECTED,      // 受保护
        PRIVATE,        // 私有
        CONFIDENTIAL    // 机密
    }
    
    // Getters and Setters
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getClassificationConfigId() {
        return classificationConfigId;
    }
    
    public void setClassificationConfigId(Long classificationConfigId) {
        this.classificationConfigId = classificationConfigId;
    }
    
    public Long getContentId() {
        return contentId;
    }
    
    public void setContentId(Long contentId) {
        this.contentId = contentId;
    }
    
    public ContentType getContentType() {
        return contentType;
    }
    
    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
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
    
    public String getPrimaryCategory() {
        return primaryCategory;
    }
    
    public void setPrimaryCategory(String primaryCategory) {
        this.primaryCategory = primaryCategory;
    }
    
    public String getSecondaryCategories() {
        return secondaryCategories;
    }
    
    public void setSecondaryCategories(String secondaryCategories) {
        this.secondaryCategories = secondaryCategories;
    }
    
    public Integer getConfidenceScore() {
        return confidenceScore;
    }
    
    public void setConfidenceScore(Integer confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
    
    public String getClassificationEvidence() {
        return classificationEvidence;
    }
    
    public void setClassificationEvidence(String classificationEvidence) {
        this.classificationEvidence = classificationEvidence;
    }
    
    public Boolean getIsContextAware() {
        return isContextAware;
    }
    
    public void setIsContextAware(Boolean isContextAware) {
        this.isContextAware = isContextAware;
    }
    
    public String getContextInformation() {
        return contextInformation;
    }
    
    public void setContextInformation(String contextInformation) {
        this.contextInformation = contextInformation;
    }
    
    public Boolean getIsMultiModal() {
        return isMultiModal;
    }
    
    public void setIsMultiModal(Boolean isMultiModal) {
        this.isMultiModal = isMultiModal;
    }
    
    public String getMultiModalAnalysis() {
        return multiModalAnalysis;
    }
    
    public void setMultiModalAnalysis(String multiModalAnalysis) {
        this.multiModalAnalysis = multiModalAnalysis;
    }
    
    public Boolean getIsAutoLabelRecommended() {
        return isAutoLabelRecommended;
    }
    
    public void setIsAutoLabelRecommended(Boolean isAutoLabelRecommended) {
        this.isAutoLabelRecommended = isAutoLabelRecommended;
    }
    
    public String getRecommendedLabels() {
        return recommendedLabels;
    }
    
    public void setRecommendedLabels(String recommendedLabels) {
        this.recommendedLabels = recommendedLabels;
    }
    
    public Boolean getHasUserFeedback() {
        return hasUserFeedback;
    }
    
    public void setHasUserFeedback(Boolean hasUserFeedback) {
        this.hasUserFeedback = hasUserFeedback;
    }
    
    public String getUserFeedback() {
        return userFeedback;
    }
    
    public void setUserFeedback(String userFeedback) {
        this.userFeedback = userFeedback;
    }
    
    public Boolean getIsPrivacyProtected() {
        return isPrivacyProtected;
    }
    
    public void setIsPrivacyProtected(Boolean isPrivacyProtected) {
        this.isPrivacyProtected = isPrivacyProtected;
    }
    
    public ClassificationPrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }
    
    public void setPrivacyLevel(ClassificationPrivacyLevel privacyLevel) {
        this.privacyLevel = privacyLevel;
    }
    
    public Boolean getIsEvolutionTracked() {
        return isEvolutionTracked;
    }
    
    public void setIsEvolutionTracked(Boolean isEvolutionTracked) {
        this.isEvolutionTracked = isEvolutionTracked;
    }
    
    public String getEvolutionHistory() {
        return evolutionHistory;
    }
    
    public void setEvolutionHistory(String evolutionHistory) {
        this.evolutionHistory = evolutionHistory;
    }
    
    public Integer getClassificationVersion() {
        return classificationVersion;
    }
    
    public void setClassificationVersion(Integer classificationVersion) {
        this.classificationVersion = classificationVersion;
    }
    
    public String getVersionChanges() {
        return versionChanges;
    }
    
    public void setVersionChanges(String versionChanges) {
        this.versionChanges = versionChanges;
    }
    
    public Double getAccuracyContribution() {
        return accuracyContribution;
    }
    
    public void setAccuracyContribution(Double accuracyContribution) {
        this.accuracyContribution = accuracyContribution;
    }
    
    public Boolean getIsTrainingExample() {
        return isTrainingExample;
    }
    
    public void setIsTrainingExample(Boolean isTrainingExample) {
        this.isTrainingExample = isTrainingExample;
    }
    
    public String getModelFeatures() {
        return modelFeatures;
    }
    
    public void setModelFeatures(String modelFeatures) {
        this.modelFeatures = modelFeatures;
    }
    
    public Boolean getIsAnomalyDetected() {
        return isAnomalyDetected;
    }
    
    public void setIsAnomalyDetected(Boolean isAnomalyDetected) {
        this.isAnomalyDetected = isAnomalyDetected;
    }
    
    public String getAnomalyDetails() {
        return anomalyDetails;
    }
    
    public void setAnomalyDetails(String anomalyDetails) {
        this.anomalyDetails = anomalyDetails;
    }
    
    public String getLanguageCode() {
        return languageCode;
    }
    
    public void setLanguageCode(String languageCode) {
        this.languageCode = languageCode;
    }
    
    public String getContentLanguage() {
        return contentLanguage;
    }
    
    public void setContentLanguage(String contentLanguage) {
        this.contentLanguage = contentLanguage;
    }
    
    public String getCrossLanguageMapping() {
        return crossLanguageMapping;
    }
    
    public void setCrossLanguageMapping(String crossLanguageMapping) {
        this.crossLanguageMapping = crossLanguageMapping;
    }
    
    public LocalDateTime getContentCreatedAt() {
        return contentCreatedAt;
    }
    
    public void setContentCreatedAt(LocalDateTime contentCreatedAt) {
        this.contentCreatedAt = contentCreatedAt;
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