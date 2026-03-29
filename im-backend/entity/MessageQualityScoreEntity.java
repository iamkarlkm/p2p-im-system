package com.im.entity;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "message_quality_score")
public class MessageQualityScoreEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message_id", nullable = false, unique = true)
    private String messageId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "sender_id", nullable = false)
    private String senderId;

    @Column(name = "receiver_id", nullable = false)
    private String receiverId;

    @Column(name = "message_type", nullable = false)
    private String messageType;

    @Column(name = "content", columnDefinition = "TEXT")
    private String content;

    @Column(name = "spam_score", nullable = false)
    private Double spamScore = 0.0;

    @Column(name = "suspicious_score", nullable = false)
    private Double suspiciousScore = 0.0;

    @Column(name = "toxicity_score", nullable = false)
    private Double toxicityScore = 0.0;

    @Column(name = "quality_score", nullable = false)
    private Double qualityScore = 0.0;

    @Column(name = "ai_confidence", nullable = false)
    private Double aiConfidence = 0.0;

    @Column(name = "is_spam", nullable = false)
    private Boolean isSpam = false;

    @Column(name = "is_suspicious", nullable = false)
    private Boolean isSuspicious = false;

    @Column(name = "is_toxic", nullable = false)
    private Boolean isToxic = false;

    @Column(name = "needs_review", nullable = false)
    private Boolean needsReview = false;

    @Column(name = "review_status", nullable = false)
    private String reviewStatus = "PENDING";

    @Column(name = "language")
    private String language;

    @Column(name = "sentiment_score")
    private Double sentimentScore;

    @Column(name = "keyword_tags", columnDefinition = "TEXT")
    private String keywordTags;

    @Column(name = "ai_model_version")
    private String aiModelVersion;

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "reviewed_by")
    private String reviewedBy;

    @Column(name = "review_notes", columnDefinition = "TEXT")
    private String reviewNotes;

    @Column(name = "action_taken")
    private String actionTaken;

    @Column(name = "flagged_by_system", nullable = false)
    private Boolean flaggedBySystem = false;

    @Column(name = "flagged_by_user", nullable = false)
    private Boolean flaggedByUser = false;

    @Column(name = "user_feedback_count", nullable = false)
    private Integer userFeedbackCount = 0;

    @Column(name = "appeal_count", nullable = false)
    private Integer appealCount = 0;

    @Column(name = "appeal_status")
    private String appealStatus;

    // Constructors
    public MessageQualityScoreEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public MessageQualityScoreEntity(String messageId, String sessionId, String senderId, String receiverId, 
                                     String messageType, String content) {
        this();
        this.messageId = messageId;
        this.sessionId = sessionId;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.messageType = messageType;
        this.content = content;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Double getSpamScore() {
        return spamScore;
    }

    public void setSpamScore(Double spamScore) {
        this.spamScore = spamScore;
        this.isSpam = spamScore >= 0.7;
        this.needsReview = spamScore >= 0.5 || suspiciousScore >= 0.5 || toxicityScore >= 0.5;
    }

    public Double getSuspiciousScore() {
        return suspiciousScore;
    }

    public void setSuspiciousScore(Double suspiciousScore) {
        this.suspiciousScore = suspiciousScore;
        this.isSuspicious = suspiciousScore >= 0.7;
        this.needsReview = spamScore >= 0.5 || suspiciousScore >= 0.5 || toxicityScore >= 0.5;
    }

    public Double getToxicityScore() {
        return toxicityScore;
    }

    public void setToxicityScore(Double toxicityScore) {
        this.toxicityScore = toxicityScore;
        this.isToxic = toxicityScore >= 0.7;
        this.needsReview = spamScore >= 0.5 || suspiciousScore >= 0.5 || toxicityScore >= 0.5;
    }

    public Double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Double qualityScore) {
        this.qualityScore = qualityScore;
    }

    public Double getAiConfidence() {
        return aiConfidence;
    }

    public void setAiConfidence(Double aiConfidence) {
        this.aiConfidence = aiConfidence;
    }

    public Boolean getIsSpam() {
        return isSpam;
    }

    public void setIsSpam(Boolean isSpam) {
        this.isSpam = isSpam;
    }

    public Boolean getIsSuspicious() {
        return isSuspicious;
    }

    public void setIsSuspicious(Boolean isSuspicious) {
        this.isSuspicious = isSuspicious;
    }

    public Boolean getIsToxic() {
        return isToxic;
    }

    public void setIsToxic(Boolean isToxic) {
        this.isToxic = isToxic;
    }

    public Boolean getNeedsReview() {
        return needsReview;
    }

    public void setNeedsReview(Boolean needsReview) {
        this.needsReview = needsReview;
    }

    public String getReviewStatus() {
        return reviewStatus;
    }

    public void setReviewStatus(String reviewStatus) {
        this.reviewStatus = reviewStatus;
        if ("REVIEWED".equals(reviewStatus)) {
            this.reviewedAt = LocalDateTime.now();
        }
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Double getSentimentScore() {
        return sentimentScore;
    }

    public void setSentimentScore(Double sentimentScore) {
        this.sentimentScore = sentimentScore;
    }

    public String getKeywordTags() {
        return keywordTags;
    }

    public void setKeywordTags(String keywordTags) {
        this.keywordTags = keywordTags;
    }

    public String getAiModelVersion() {
        return aiModelVersion;
    }

    public void setAiModelVersion(String aiModelVersion) {
        this.aiModelVersion = aiModelVersion;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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

    public LocalDateTime getReviewedAt() {
        return reviewedAt;
    }

    public void setReviewedAt(LocalDateTime reviewedAt) {
        this.reviewedAt = reviewedAt;
    }

    public String getReviewedBy() {
        return reviewedBy;
    }

    public void setReviewedBy(String reviewedBy) {
        this.reviewedBy = reviewedBy;
    }

    public String getReviewNotes() {
        return reviewNotes;
    }

    public void setReviewNotes(String reviewNotes) {
        this.reviewNotes = reviewNotes;
    }

    public String getActionTaken() {
        return actionTaken;
    }

    public void setActionTaken(String actionTaken) {
        this.actionTaken = actionTaken;
    }

    public Boolean getFlaggedBySystem() {
        return flaggedBySystem;
    }

    public void setFlaggedBySystem(Boolean flaggedBySystem) {
        this.flaggedBySystem = flaggedBySystem;
    }

    public Boolean getFlaggedByUser() {
        return flaggedByUser;
    }

    public void setFlaggedByUser(Boolean flaggedByUser) {
        this.flaggedByUser = flaggedByUser;
        if (flaggedByUser) {
            this.userFeedbackCount++;
        }
    }

    public Integer getUserFeedbackCount() {
        return userFeedbackCount;
    }

    public void setUserFeedbackCount(Integer userFeedbackCount) {
        this.userFeedbackCount = userFeedbackCount;
    }

    public Integer getAppealCount() {
        return appealCount;
    }

    public void setAppealCount(Integer appealCount) {
        this.appealCount = appealCount;
    }

    public String getAppealStatus() {
        return appealStatus;
    }

    public void setAppealStatus(String appealStatus) {
        this.appealStatus = appealStatus;
    }

    // Helper methods
    public void incrementAppealCount() {
        this.appealCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementUserFeedbackCount() {
        this.userFeedbackCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateScores(Double spamScore, Double suspiciousScore, Double toxicityScore, Double aiConfidence, String aiModelVersion) {
        this.spamScore = spamScore;
        this.suspiciousScore = suspiciousScore;
        this.toxicityScore = toxicityScore;
        this.aiConfidence = aiConfidence;
        this.aiModelVersion = aiModelVersion;
        
        this.isSpam = spamScore >= 0.7;
        this.isSuspicious = suspiciousScore >= 0.7;
        this.isToxic = toxicityScore >= 0.7;
        this.needsReview = spamScore >= 0.5 || suspiciousScore >= 0.5 || toxicityScore >= 0.5;
        
        // Calculate overall quality score (1.0 = best, 0.0 = worst)
        this.qualityScore = 1.0 - Math.max(spamScore, Math.max(suspiciousScore, toxicityScore));
        
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsReviewed(String reviewedBy, String reviewNotes, String actionTaken) {
        this.reviewStatus = "REVIEWED";
        this.reviewedAt = LocalDateTime.now();
        this.reviewedBy = reviewedBy;
        this.reviewNotes = reviewNotes;
        this.actionTaken = actionTaken;
        this.needsReview = false;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsAppealed(String appealStatus) {
        this.appealStatus = appealStatus;
        this.appealCount++;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}