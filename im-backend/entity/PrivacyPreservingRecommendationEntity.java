package com.im.system.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 隐私保护推荐实体
 * 用于存储基于联邦学习的个性化消息推荐结果和用户隐私设置
 */
@Entity
@Table(name = "privacy_preserving_recommendations")
public class PrivacyPreservingRecommendationEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "recommendation_id", nullable = false, unique = true)
    private String recommendationId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "model_id", nullable = false)
    private String modelId;

    @Column(name = "model_version")
    private String modelVersion;

    @Column(name = "recommendation_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecommendationType recommendationType;

    @Column(name = "recommendation_context")
    private String recommendationContext;

    @Column(name = "recommended_content_ids")
    private String recommendedContentIds;

    @Column(name = "recommendation_scores")
    private String recommendationScores;

    @Column(name = "recommendation_rank")
    private Integer recommendationRank;

    @Column(name = "total_recommendations")
    private Integer totalRecommendations;

    @Column(name = "user_feedback_score")
    private Double userFeedbackScore;

    @Column(name = "user_interaction_type")
    private String userInteractionType;

    @Column(name = "interaction_timestamp")
    private LocalDateTime interactionTimestamp;

    @Column(name = "click_through")
    private Boolean clickThrough;

    @Column(name = "dwell_time_seconds")
    private Integer dwellTimeSeconds;

    @Column(name = "privacy_level", nullable = false)
    @Enumerated(EnumType.STRING)
    private PrivacyLevel privacyLevel = PrivacyLevel.STANDARD;

    @Column(name = "differential_privacy_enabled")
    private Boolean differentialPrivacyEnabled = true;

    @Column(name = "privacy_budget_consumed")
    private Double privacyBudgetConsumed = 0.0;

    @Column(name = "local_model_update_enabled")
    private Boolean localModelUpdateEnabled = true;

    @Column(name = "gradient_clipping_enabled")
    private Boolean gradientClippingEnabled = true;

    @Column(name = "noise_injection_enabled")
    private Boolean noiseInjectionEnabled = true;

    @Column(name = "secure_aggregation_enabled")
    private Boolean secureAggregationEnabled = true;

    @Column(name = "encryption_method")
    private String encryptionMethod;

    @Column(name = "data_retention_days")
    private Integer dataRetentionDays = 30;

    @Column(name = "anonymization_level")
    @Enumerated(EnumType.STRING)
    private AnonymizationLevel anonymizationLevel = AnonymizationLevel.PSEUDONYMIZED;

    @Column(name = "consent_given")
    private Boolean consentGiven = true;

    @Column(name = "consent_timestamp")
    private LocalDateTime consentTimestamp;

    @Column(name = "consent_withdrawn")
    private Boolean consentWithdrawn = false;

    @Column(name = "consent_withdrawal_timestamp")
    private LocalDateTime consentWithdrawalTimestamp;

    @Column(name = "client_identifier")
    private String clientIdentifier;

    @Column(name = "device_type")
    private String deviceType;

    @Column(name = "app_version")
    private String appVersion;

    @Column(name = "geographic_region")
    private String geographicRegion;

    @Column(name = "time_zone")
    private String timeZone;

    @Column(name = "local_gradient_path")
    private String localGradientPath;

    @Column(name = "encrypted_gradient_hash")
    private String encryptedGradientHash;

    @Column(name = "aggregation_round")
    private Integer aggregationRound = 0;

    @Column(name = "contribution_weight")
    private Double contributionWeight = 1.0;

    @Column(name = "quality_score")
    private Double qualityScore;

    @Column(name = "training_samples_count")
    private Integer trainingSamplesCount;

    @Column(name = "feature_vector_dimensions")
    private Integer featureVectorDimensions;

    @Column(name = "metadata_json")
    @Lob
    private String metadataJson;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "processed_at")
    private LocalDateTime processedAt;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "status", nullable = false)
    @Enumerated(EnumType.STRING)
    private RecommendationStatus status = RecommendationStatus.PENDING;

    // 构造函数
    public PrivacyPreservingRecommendationEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public PrivacyPreservingRecommendationEntity(String recommendationId, String userId,
                                                 String modelId, RecommendationType recommendationType) {
        this();
        this.recommendationId = recommendationId;
        this.userId = userId;
        this.modelId = modelId;
        this.recommendationType = recommendationType;
        this.status = RecommendationStatus.PENDING;
    }

    // 枚举类型定义
    public enum RecommendationType {
        MESSAGE_SUGGESTION,
        CONTACT_SUGGESTION,
        GROUP_SUGGESTION,
        CONTENT_SUGGESTION,
        CHANNEL_SUGGESTION,
        BOT_SUGGESTION,
        ACTION_SUGGESTION,
        PERSONALIZED_FEED,
        TRENDING_TOPIC,
        SIMILAR_USER
    }

    public enum PrivacyLevel {
        MINIMAL,
        BASIC,
        STANDARD,
        ENHANCED,
        MAXIMUM
    }

    public enum AnonymizationLevel {
        RAW,
        PSEUDONYMIZED,
        ANONYMIZED,
        AGGREGATED,
        DIFFERENTIALLY_PRIVATE
    }

    public enum RecommendationStatus {
        PENDING,
        GENERATING,
        READY,
        DELIVERED,
        INTERACTED,
        EXPIRED,
        FAILED,
        WITHDRAWN
    }

    // Getter 和 Setter 方法
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getRecommendationId() {
        return recommendationId;
    }

    public void setRecommendationId(String recommendationId) {
        this.recommendationId = recommendationId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public RecommendationType getRecommendationType() {
        return recommendationType;
    }

    public void setRecommendationType(RecommendationType recommendationType) {
        this.recommendationType = recommendationType;
    }

    public String getRecommendationContext() {
        return recommendationContext;
    }

    public void setRecommendationContext(String recommendationContext) {
        this.recommendationContext = recommendationContext;
    }

    public String getRecommendedContentIds() {
        return recommendedContentIds;
    }

    public void setRecommendedContentIds(String recommendedContentIds) {
        this.recommendedContentIds = recommendedContentIds;
    }

    public String getRecommendationScores() {
        return recommendationScores;
    }

    public void setRecommendationScores(String recommendationScores) {
        this.recommendationScores = recommendationScores;
    }

    public Integer getRecommendationRank() {
        return recommendationRank;
    }

    public void setRecommendationRank(Integer recommendationRank) {
        this.recommendationRank = recommendationRank;
    }

    public Integer getTotalRecommendations() {
        return totalRecommendations;
    }

    public void setTotalRecommendations(Integer totalRecommendations) {
        this.totalRecommendations = totalRecommendations;
    }

    public Double getUserFeedbackScore() {
        return userFeedbackScore;
    }

    public void setUserFeedbackScore(Double userFeedbackScore) {
        this.userFeedbackScore = userFeedbackScore;
    }

    public String getUserInteractionType() {
        return userInteractionType;
    }

    public void setUserInteractionType(String userInteractionType) {
        this.userInteractionType = userInteractionType;
    }

    public LocalDateTime getInteractionTimestamp() {
        return interactionTimestamp;
    }

    public void setInteractionTimestamp(LocalDateTime interactionTimestamp) {
        this.interactionTimestamp = interactionTimestamp;
    }

    public Boolean getClickThrough() {
        return clickThrough;
    }

    public void setClickThrough(Boolean clickThrough) {
        this.clickThrough = clickThrough;
    }

    public Integer getDwellTimeSeconds() {
        return dwellTimeSeconds;
    }

    public void setDwellTimeSeconds(Integer dwellTimeSeconds) {
        this.dwellTimeSeconds = dwellTimeSeconds;
    }

    public PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    public void setPrivacyLevel(PrivacyLevel privacyLevel) {
        this.privacyLevel = privacyLevel;
    }

    public Boolean getDifferentialPrivacyEnabled() {
        return differentialPrivacyEnabled;
    }

    public void setDifferentialPrivacyEnabled(Boolean differentialPrivacyEnabled) {
        this.differentialPrivacyEnabled = differentialPrivacyEnabled;
    }

    public Double getPrivacyBudgetConsumed() {
        return privacyBudgetConsumed;
    }

    public void setPrivacyBudgetConsumed(Double privacyBudgetConsumed) {
        this.privacyBudgetConsumed = privacyBudgetConsumed;
    }

    public Boolean getLocalModelUpdateEnabled() {
        return localModelUpdateEnabled;
    }

    public void setLocalModelUpdateEnabled(Boolean localModelUpdateEnabled) {
        this.localModelUpdateEnabled = localModelUpdateEnabled;
    }

    public Boolean getGradientClippingEnabled() {
        return gradientClippingEnabled;
    }

    public void setGradientClippingEnabled(Boolean gradientClippingEnabled) {
        this.gradientClippingEnabled = gradientClippingEnabled;
    }

    public Boolean getNoiseInjectionEnabled() {
        return noiseInjectionEnabled;
    }

    public void setNoiseInjectionEnabled(Boolean noiseInjectionEnabled) {
        this.noiseInjectionEnabled = noiseInjectionEnabled;
    }

    public Boolean getSecureAggregationEnabled() {
        return secureAggregationEnabled;
    }

    public void setSecureAggregationEnabled(Boolean secureAggregationEnabled) {
        this.secureAggregationEnabled = secureAggregationEnabled;
    }

    public String getEncryptionMethod() {
        return encryptionMethod;
    }

    public void setEncryptionMethod(String encryptionMethod) {
        this.encryptionMethod = encryptionMethod;
    }

    public Integer getDataRetentionDays() {
        return dataRetentionDays;
    }

    public void setDataRetentionDays(Integer dataRetentionDays) {
        this.dataRetentionDays = dataRetentionDays;
    }

    public AnonymizationLevel getAnonymizationLevel() {
        return anonymizationLevel;
    }

    public void setAnonymizationLevel(AnonymizationLevel anonymizationLevel) {
        this.anonymizationLevel = anonymizationLevel;
    }

    public Boolean getConsentGiven() {
        return consentGiven;
    }

    public void setConsentGiven(Boolean consentGiven) {
        this.consentGiven = consentGiven;
    }

    public LocalDateTime getConsentTimestamp() {
        return consentTimestamp;
    }

    public void setConsentTimestamp(LocalDateTime consentTimestamp) {
        this.consentTimestamp = consentTimestamp;
    }

    public Boolean getConsentWithdrawn() {
        return consentWithdrawn;
    }

    public void setConsentWithdrawn(Boolean consentWithdrawn) {
        this.consentWithdrawn = consentWithdrawn;
    }

    public LocalDateTime getConsentWithdrawalTimestamp() {
        return consentWithdrawalTimestamp;
    }

    public void setConsentWithdrawalTimestamp(LocalDateTime consentWithdrawalTimestamp) {
        this.consentWithdrawalTimestamp = consentWithdrawalTimestamp;
    }

    public String getClientIdentifier() {
        return clientIdentifier;
    }

    public void setClientIdentifier(String clientIdentifier) {
        this.clientIdentifier = clientIdentifier;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getAppVersion() {
        return appVersion;
    }

    public void setAppVersion(String appVersion) {
        this.appVersion = appVersion;
    }

    public String getGeographicRegion() {
        return geographicRegion;
    }

    public void setGeographicRegion(String geographicRegion) {
        this.geographicRegion = geographicRegion;
    }

    public String getTimeZone() {
        return timeZone;
    }

    public void setTimeZone(String timeZone) {
        this.timeZone = timeZone;
    }

    public String getLocalGradientPath() {
        return localGradientPath;
    }

    public void setLocalGradientPath(String localGradientPath) {
        this.localGradientPath = localGradientPath;
    }

    public String getEncryptedGradientHash() {
        return encryptedGradientHash;
    }

    public void setEncryptedGradientHash(String encryptedGradientHash) {
        this.encryptedGradientHash = encryptedGradientHash;
    }

    public Integer getAggregationRound() {
        return aggregationRound;
    }

    public void setAggregationRound(Integer aggregationRound) {
        this.aggregationRound = aggregationRound;
    }

    public Double getContributionWeight() {
        return contributionWeight;
    }

    public void setContributionWeight(Double contributionWeight) {
        this.contributionWeight = contributionWeight;
    }

    public Double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Double qualityScore) {
        this.qualityScore = qualityScore;
    }

    public Integer getTrainingSamplesCount() {
        return trainingSamplesCount;
    }

    public void setTrainingSamplesCount(Integer trainingSamplesCount) {
        this.trainingSamplesCount = trainingSamplesCount;
    }

    public Integer getFeatureVectorDimensions() {
        return featureVectorDimensions;
    }

    public void setFeatureVectorDimensions(Integer featureVectorDimensions) {
        this.featureVectorDimensions = featureVectorDimensions;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
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

    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }

    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public void setProcessedAt(LocalDateTime processedAt) {
        this.processedAt = processedAt;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(Integer retryCount) {
        this.retryCount = retryCount;
    }

    public RecommendationStatus getStatus() {
        return status;
    }

    public void setStatus(RecommendationStatus status) {
        this.status = status;
    }

    // 业务方法
    public void recordInteraction(String interactionType, Double feedbackScore, Integer dwellTime) {
        this.userInteractionType = interactionType;
        this.userFeedbackScore = feedbackScore;
        this.dwellTimeSeconds = dwellTime;
        this.interactionTimestamp = LocalDateTime.now();
        this.status = RecommendationStatus.INTERACTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void recordClickThrough() {
        this.clickThrough = true;
        this.interactionTimestamp = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDelivered() {
        this.status = RecommendationStatus.DELIVERED;
        this.processedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void failRecommendation(String errorMessage) {
        this.status = RecommendationStatus.FAILED;
        this.errorMessage = errorMessage;
        this.updatedAt = LocalDateTime.now();
    }

    public void withdrawConsent() {
        this.consentWithdrawn = true;
        this.consentWithdrawalTimestamp = LocalDateTime.now();
        this.status = RecommendationStatus.WITHDRAWN;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canUseForTraining() {
        return consentGiven &&
               !consentWithdrawn &&
               status != RecommendationStatus.FAILED &&
               status != RecommendationStatus.WITHDRAWN;
    }

    public void incrementRetryCount() {
        this.retryCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void consumePrivacyBudget(double amount) {
        if (this.privacyBudgetConsumed == null) {
            this.privacyBudgetConsumed = 0.0;
        }
        this.privacyBudgetConsumed += amount;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "PrivacyPreservingRecommendationEntity{" +
                "id=" + id +
                ", recommendationId='" + recommendationId + '\'' +
                ", userId='" + userId + '\'' +
                ", recommendationType=" + recommendationType +
                ", privacyLevel=" + privacyLevel +
                ", status=" + status +
                ", clickThrough=" + clickThrough +
                '}';
    }
}