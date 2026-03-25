package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 多模态 AI 推理管道配置实体
 * 统一的多模态 AI 推理管道，整合文本、图像、语音、视频分析
 */
@Entity
@Table(name = "multimodal_ai_inference_config",
       indexes = {
           @Index(name = "idx_user_id", columnList = "userId"),
           @Index(name = "idx_status", columnList = "status"),
           @Index(name = "idx_created_at", columnList = "createdAt")
       })
public class MultimodalAIInferenceConfigEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(length = 500)
    private String description;
    
    @Column(nullable = false)
    private Long userId;
    
    @Column(nullable = false)
    private Boolean enableTextAnalysis = true;
    
    @Column(nullable = false)
    private Boolean enableImageAnalysis = true;
    
    @Column(nullable = false)
    private Boolean enableAudioAnalysis = true;
    
    @Column(nullable = false)
    private Boolean enableVideoAnalysis = true;
    
    @Column(nullable = false)
    private Boolean enableCrossModalRetrieval = true;
    
    @Column(nullable = false)
    private Boolean enableVisualQA = true;
    
    @Column(nullable = false)
    private Boolean enableAudioSentimentAnalysis = true;
    
    @Column(nullable = false)
    private Boolean enableVideoContentParsing = true;
    
    @Column(nullable = false)
    private Boolean enableMultimodalSummarization = true;
    
    @Column(nullable = false)
    private Boolean enableCrossModalAssociation = true;
    
    @Column(nullable = false)
    private Boolean enableRealTimeInference = true;
    
    @Column(nullable = false)
    private Integer inferenceTimeoutMs = 30000;
    
    @Column(nullable = false)
    private Integer maxBatchSize = 100;
    
    @Column(nullable = false)
    private String modelConfiguration; // JSON 格式
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private InferenceStatus status;
    
    @Column(nullable = false)
    private Integer version = 1;
    
    @Column(nullable = false)
    private Double averageLatencyMs;
    
    @Column(nullable = false)
    private Long totalInferences;
    
    @Column(nullable = false)
    private Long successfulInferences;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private LocalDateTime updatedAt;
    
    public enum InferenceStatus {
        DRAFT, ACTIVE, INACTIVE, ARCHIVED, DELETED
    }
    
    @PrePersist
    protected void onCreate() {
        LocalDateTime now = LocalDateTime.now();
        createdAt = now;
        updatedAt = now;
        if (averageLatencyMs == null) averageLatencyMs = 0.0;
        if (totalInferences == null) totalInferences = 0L;
        if (successfulInferences == null) successfulInferences = 0L;
        if (version == null) version = 1;
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // Getters and Setters (省略以节省空间)
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Boolean getEnableTextAnalysis() { return enableTextAnalysis; }
    public void setEnableTextAnalysis(Boolean enableTextAnalysis) { this.enableTextAnalysis = enableTextAnalysis; }
    public Boolean getEnableImageAnalysis() { return enableImageAnalysis; }
    public void setEnableImageAnalysis(Boolean enableImageAnalysis) { this.enableImageAnalysis = enableImageAnalysis; }
    public Boolean getEnableAudioAnalysis() { return enableAudioAnalysis; }
    public void setEnableAudioAnalysis(Boolean enableAudioAnalysis) { this.enableAudioAnalysis = enableAudioAnalysis; }
    public Boolean getEnableVideoAnalysis() { return enableVideoAnalysis; }
    public void setEnableVideoAnalysis(Boolean enableVideoAnalysis) { this.enableVideoAnalysis = enableVideoAnalysis; }
    public Boolean getEnableCrossModalRetrieval() { return enableCrossModalRetrieval; }
    public void setEnableCrossModalRetrieval(Boolean enableCrossModalRetrieval) { this.enableCrossModalRetrieval = enableCrossModalRetrieval; }
    public Boolean getEnableVisualQA() { return enableVisualQA; }
    public void setEnableVisualQA(Boolean enableVisualQA) { this.enableVisualQA = enableVisualQA; }
    public Boolean getEnableAudioSentimentAnalysis() { return enableAudioSentimentAnalysis; }
    public void setEnableAudioSentimentAnalysis(Boolean enableAudioSentimentAnalysis) { this.enableAudioSentimentAnalysis = enableAudioSentimentAnalysis; }
    public Boolean getEnableVideoContentParsing() { return enableVideoContentParsing; }
    public void setEnableVideoContentParsing(Boolean enableVideoContentParsing) { this.enableVideoContentParsing = enableVideoContentParsing; }
    public Boolean getEnableMultimodalSummarization() { return enableMultimodalSummarization; }
    public void setEnableMultimodalSummarization(Boolean enableMultimodalSummarization) { this.enableMultimodalSummarization = enableMultimodalSummarization; }
    public Boolean getEnableCrossModalAssociation() { return enableCrossModalAssociation; }
    public void setEnableCrossModalAssociation(Boolean enableCrossModalAssociation) { this.enableCrossModalAssociation = enableCrossModalAssociation; }
    public Boolean getEnableRealTimeInference() { return enableRealTimeInference; }
    public void setEnableRealTimeInference(Boolean enableRealTimeInference) { this.enableRealTimeInference = enableRealTimeInference; }
    public Integer getInferenceTimeoutMs() { return inferenceTimeoutMs; }
    public void setInferenceTimeoutMs(Integer inferenceTimeoutMs) { this.inferenceTimeoutMs = inferenceTimeoutMs; }
    public Integer getMaxBatchSize() { return maxBatchSize; }
    public void setMaxBatchSize(Integer maxBatchSize) { this.maxBatchSize = maxBatchSize; }
    public String getModelConfiguration() { return modelConfiguration; }
    public void setModelConfiguration(String modelConfiguration) { this.modelConfiguration = modelConfiguration; }
    public InferenceStatus getStatus() { return status; }
    public void setStatus(InferenceStatus status) { this.status = status; }
    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }
    public Double getAverageLatencyMs() { return averageLatencyMs; }
    public void setAverageLatencyMs(Double averageLatencyMs) { this.averageLatencyMs = averageLatencyMs; }
    public Long getTotalInferences() { return totalInferences; }
    public void setTotalInferences(Long totalInferences) { this.totalInferences = totalInferences; }
    public Long getSuccessfulInferences() { return successfulInferences; }
    public void setSuccessfulInferences(Long successfulInferences) { this.successfulInferences = successfulInferences; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}