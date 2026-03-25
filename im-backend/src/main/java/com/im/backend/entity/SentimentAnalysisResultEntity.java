package com.im.backend.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 情感分析结果实体
 * 存储基于深度学习的情感分析结果
 */
@Entity
@Table(name = "sentiment_analysis_result")
public class SentimentAnalysisResultEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "message_id", nullable = false, unique = true)
    private Long messageId;
    
    @Column(name = "conversation_id", nullable = false)
    private Long conversationId;
    
    @Column(name = "sender_id", nullable = false)
    private Long senderId;
    
    @Column(name = "analysis_time", nullable = false)
    private LocalDateTime analysisTime;
    
    // 多标签情感分类结果 (JSON格式存储)
    @Column(name = "sentiment_scores", nullable = false, columnDefinition = "TEXT")
    private String sentimentScores;
    
    @Column(name = "primary_emotion")
    private String primaryEmotion;
    
    @Column(name = "secondary_emotion")
    private String secondaryEmotion;
    
    // 情感强度 (0.0-1.0)
    @Column(name = "sentiment_intensity")
    private Double sentimentIntensity;
    
    // 上下文情感影响因子 (JSON格式)
    @Column(name = "context_factors", columnDefinition = "TEXT")
    private String contextFactors;
    
    // 紧急情绪标记
    @Column(name = "emergency_flag")
    private Boolean emergencyFlag;
    
    @Column(name = "emergency_reason")
    private String emergencyReason;
    
    // 情感趋势分析标记
    @Column(name = "trend_marker")
    private String trendMarker;
    
    @Column(name = "confidence_score")
    private Double confidenceScore;
    
    // 多模态情感融合结果
    @Column(name = "multimodal_fusion_score")
    private Double multimodalFusionScore;
    
    @Column(name = "text_emotion")
    private String textEmotion;
    
    @Column(name = "audio_emotion")
    private String audioEmotion;
    
    @Column(name = "visual_emotion")
    private String visualEmotion;
    
    // 个性化情感基线偏差
    @Column(name = "baseline_deviation")
    private Double baselineDeviation;
    
    // 情感可视化数据
    @Column(name = "visualization_data", columnDefinition = "TEXT")
    private String visualizationData;
    
    // 离线模型预测标记
    @Column(name = "offline_prediction")
    private Boolean offlinePrediction;
    
    @Column(name = "model_version")
    private String modelVersion;
    
    @Column(name = "processing_latency_ms")
    private Long processingLatencyMs;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        analysisTime = now;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    
    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }
    
    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }
    
    public LocalDateTime getAnalysisTime() { return analysisTime; }
    public void setAnalysisTime(LocalDateTime analysisTime) { this.analysisTime = analysisTime; }
    
    public String getSentimentScores() { return sentimentScores; }
    public void setSentimentScores(String sentimentScores) { this.sentimentScores = sentimentScores; }
    
    public String getPrimaryEmotion() { return primaryEmotion; }
    public void setPrimaryEmotion(String primaryEmotion) { this.primaryEmotion = primaryEmotion; }
    
    public String getSecondaryEmotion() { return secondaryEmotion; }
    public void setSecondaryEmotion(String secondaryEmotion) { this.secondaryEmotion = secondaryEmotion; }
    
    public Double getSentimentIntensity() { return sentimentIntensity; }
    public void setSentimentIntensity(Double sentimentIntensity) { this.sentimentIntensity = sentimentIntensity; }
    
    public String getContextFactors() { return contextFactors; }
    public void setContextFactors(String contextFactors) { this.contextFactors = contextFactors; }
    
    public Boolean getEmergencyFlag() { return emergencyFlag; }
    public void setEmergencyFlag(Boolean emergencyFlag) { this.emergencyFlag = emergencyFlag; }
    
    public String getEmergencyReason() { return emergencyReason; }
    public void setEmergencyReason(String emergencyReason) { this.emergencyReason = emergencyReason; }
    
    public String getTrendMarker() { return trendMarker; }
    public void setTrendMarker(String trendMarker) { this.trendMarker = trendMarker; }
    
    public Double getConfidenceScore() { return confidenceScore; }
    public void setConfidenceScore(Double confidenceScore) { this.confidenceScore = confidenceScore; }
    
    public Double getMultimodalFusionScore() { return multimodalFusionScore; }
    public void setMultimodalFusionScore(Double multimodalFusionScore) { this.multimodalFusionScore = multimodalFusionScore; }
    
    public String getTextEmotion() { return textEmotion; }
    public void setTextEmotion(String textEmotion) { this.textEmotion = textEmotion; }
    
    public String getAudioEmotion() { return audioEmotion; }
    public void setAudioEmotion(String audioEmotion) { this.audioEmotion = audioEmotion; }
    
    public String getVisualEmotion() { return visualEmotion; }
    public void setVisualEmotion(String visualEmotion) { this.visualEmotion = visualEmotion; }
    
    public Double getBaselineDeviation() { return baselineDeviation; }
    public void setBaselineDeviation(Double baselineDeviation) { this.baselineDeviation = baselineDeviation; }
    
    public String getVisualizationData() { return visualizationData; }
    public void setVisualizationData(String visualizationData) { this.visualizationData = visualizationData; }
    
    public Boolean getOfflinePrediction() { return offlinePrediction; }
    public void setOfflinePrediction(Boolean offlinePrediction) { this.offlinePrediction = offlinePrediction; }
    
    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }
    
    public Long getProcessingLatencyMs() { return processingLatencyMs; }
    public void setProcessingLatencyMs(Long processingLatencyMs) { this.processingLatencyMs = processingLatencyMs; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}