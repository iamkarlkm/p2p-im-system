package com.im.system.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * AI 增强前端框架配置实体
 * 用于存储本地 AI 推理引擎的配置、模型信息和用户偏好
 */
@Entity
@Table(name = "ai_frontend_framework")
public class AiFrontendFrameworkEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "device_id", nullable = false)
    private String deviceId;

    @Column(name = "framework_version", nullable = false)
    private String frameworkVersion;

    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;

    @Column(name = "local_model_engine")
    private String localModelEngine; // 本地推理引擎类型: tensorflow.js, onnxruntime, transformers.js

    @Column(name = "model_name")
    private String modelName; // 模型名称

    @Column(name = "model_version")
    private String modelVersion; // 模型版本

    @Column(name = "model_size_mb")
    private Integer modelSizeMb; // 模型大小(MB)

    @Column(name = "model_loaded", nullable = false)
    private Boolean modelLoaded = false;

    @Column(name = "model_load_time")
    private LocalDateTime modelLoadTime;

    @Column(name = "inference_backend")
    private String inferenceBackend; // 推理后端: wasm, webgl, webgpu

    @Column(name = "max_memory_mb")
    private Integer maxMemoryMb = 512; // 最大内存限制

    @Column(name = "feature_enabled_smart_reply")
    private Boolean featureEnabledSmartReply = true; // 智能回复功能

    @Column(name = "feature_enabled_message_summary")
    private Boolean featureEnabledMessageSummary = true; // 消息摘要功能

    @Column(name = "feature_enabled_sentiment_analysis")
    private Boolean featureEnabledSentimentAnalysis = true; // 情感分析功能

    @Column(name = "privacy_mode", nullable = false)
    private Boolean privacyMode = true; // 隐私模式(本地推理)

    @Column(name = "offline_mode", nullable = false)
    private Boolean offlineMode = false; // 离线模式

    @Column(name = "performance_level")
    private String performanceLevel = "balanced"; // 性能级别: low, balanced, high

    @Column(name = "inference_batch_size")
    private Integer inferenceBatchSize = 4; // 推理批处理大小

    @Column(name = "model_update_frequency")
    private String modelUpdateFrequency = "weekly"; // 模型更新频率

    @Column(name = "last_model_update")
    private LocalDateTime lastModelUpdate;

    @Column(name = "inference_stats_total")
    private Long inferenceStatsTotal = 0L; // 总推理次数

    @Column(name = "inference_stats_success")
    private Long inferenceStatsSuccess = 0L; // 成功推理次数

    @Column(name = "inference_stats_avg_latency_ms")
    private Double inferenceStatsAvgLatencyMs = 0.0; // 平均延迟

    @Column(name = "custom_config")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> customConfig; // 自定义配置

    @Column(name = "model_metadata")
    @Convert(converter = JsonConverter.class)
    private Map<String, Object> modelMetadata; // 模型元数据

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 构造函数
    public AiFrontendFrameworkEntity() {
    }

    public AiFrontendFrameworkEntity(Long userId, String deviceId, String frameworkVersion) {
        this.userId = userId;
        this.deviceId = deviceId;
        this.frameworkVersion = frameworkVersion;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getFrameworkVersion() { return frameworkVersion; }
    public void setFrameworkVersion(String frameworkVersion) { this.frameworkVersion = frameworkVersion; }

    public Boolean getEnabled() { return enabled; }
    public void setEnabled(Boolean enabled) { this.enabled = enabled; }

    public String getLocalModelEngine() { return localModelEngine; }
    public void setLocalModelEngine(String localModelEngine) { this.localModelEngine = localModelEngine; }

    public String getModelName() { return modelName; }
    public void setModelName(String modelName) { this.modelName = modelName; }

    public String getModelVersion() { return modelVersion; }
    public void setModelVersion(String modelVersion) { this.modelVersion = modelVersion; }

    public Integer getModelSizeMb() { return modelSizeMb; }
    public void setModelSizeMb(Integer modelSizeMb) { this.modelSizeMb = modelSizeMb; }

    public Boolean getModelLoaded() { return modelLoaded; }
    public void setModelLoaded(Boolean modelLoaded) { this.modelLoaded = modelLoaded; }

    public LocalDateTime getModelLoadTime() { return modelLoadTime; }
    public void setModelLoadTime(LocalDateTime modelLoadTime) { this.modelLoadTime = modelLoadTime; }

    public String getInferenceBackend() { return inferenceBackend; }
    public void setInferenceBackend(String inferenceBackend) { this.inferenceBackend = inferenceBackend; }

    public Integer getMaxMemoryMb() { return maxMemoryMb; }
    public void setMaxMemoryMb(Integer maxMemoryMb) { this.maxMemoryMb = maxMemoryMb; }

    public Boolean getFeatureEnabledSmartReply() { return featureEnabledSmartReply; }
    public void setFeatureEnabledSmartReply(Boolean featureEnabledSmartReply) { this.featureEnabledSmartReply = featureEnabledSmartReply; }

    public Boolean getFeatureEnabledMessageSummary() { return featureEnabledMessageSummary; }
    public void setFeatureEnabledMessageSummary(Boolean featureEnabledMessageSummary) { this.featureEnabledMessageSummary = featureEnabledMessageSummary; }

    public Boolean getFeatureEnabledSentimentAnalysis() { return featureEnabledSentimentAnalysis; }
    public void setFeatureEnabledSentimentAnalysis(Boolean featureEnabledSentimentAnalysis) { this.featureEnabledSentimentAnalysis = featureEnabledSentimentAnalysis; }

    public Boolean getPrivacyMode() { return privacyMode; }
    public void setPrivacyMode(Boolean privacyMode) { this.privacyMode = privacyMode; }

    public Boolean getOfflineMode() { return offlineMode; }
    public void setOfflineMode(Boolean offlineMode) { this.offlineMode = offlineMode; }

    public String getPerformanceLevel() { return performanceLevel; }
    public void setPerformanceLevel(String performanceLevel) { this.performanceLevel = performanceLevel; }

    public Integer getInferenceBatchSize() { return inferenceBatchSize; }
    public void setInferenceBatchSize(Integer inferenceBatchSize) { this.inferenceBatchSize = inferenceBatchSize; }

    public String getModelUpdateFrequency() { return modelUpdateFrequency; }
    public void setModelUpdateFrequency(String modelUpdateFrequency) { this.modelUpdateFrequency = modelUpdateFrequency; }

    public LocalDateTime getLastModelUpdate() { return lastModelUpdate; }
    public void setLastModelUpdate(LocalDateTime lastModelUpdate) { this.lastModelUpdate = lastModelUpdate; }

    public Long getInferenceStatsTotal() { return inferenceStatsTotal; }
    public void setInferenceStatsTotal(Long inferenceStatsTotal) { this.inferenceStatsTotal = inferenceStatsTotal; }

    public Long getInferenceStatsSuccess() { return inferenceStatsSuccess; }
    public void setInferenceStatsSuccess(Long inferenceStatsSuccess) { this.inferenceStatsSuccess = inferenceStatsSuccess; }

    public Double getInferenceStatsAvgLatencyMs() { return inferenceStatsAvgLatencyMs; }
    public void setInferenceStatsAvgLatencyMs(Double inferenceStatsAvgLatencyMs) { this.inferenceStatsAvgLatencyMs = inferenceStatsAvgLatencyMs; }

    public Map<String, Object> getCustomConfig() { return customConfig; }
    public void setCustomConfig(Map<String, Object> customConfig) { this.customConfig = customConfig; }

    public Map<String, Object> getModelMetadata() { return modelMetadata; }
    public void setModelMetadata(Map<String, Object> modelMetadata) { this.modelMetadata = modelMetadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    // 辅助方法
    public void incrementInferenceStats(boolean success, long latencyMs) {
        this.inferenceStatsTotal++;
        if (success) {
            this.inferenceStatsSuccess++;
        }
        // 更新平均延迟
        if (this.inferenceStatsSuccess > 0) {
            double newAvg = ((this.inferenceStatsAvgLatencyMs * (this.inferenceStatsSuccess - 1)) + latencyMs) / this.inferenceStatsSuccess;
            this.inferenceStatsAvgLatencyMs = newAvg;
        }
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isFeatureEnabled(String feature) {
        switch (feature) {
            case "smart_reply": return Boolean.TRUE.equals(featureEnabledSmartReply);
            case "message_summary": return Boolean.TRUE.equals(featureEnabledMessageSummary);
            case "sentiment_analysis": return Boolean.TRUE.equals(featureEnabledSentimentAnalysis);
            default: return false;
        }
    }

    public void enableFeature(String feature, boolean enable) {
        switch (feature) {
            case "smart_reply": this.featureEnabledSmartReply = enable; break;
            case "message_summary": this.featureEnabledMessageSummary = enable; break;
            case "sentiment_analysis": this.featureEnabledSentimentAnalysis = enable; break;
        }
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "AiFrontendFrameworkEntity{" +
                "id=" + id +
                ", userId=" + userId +
                ", deviceId='" + deviceId + '\'' +
                ", frameworkVersion='" + frameworkVersion + '\'' +
                ", enabled=" + enabled +
                ", modelLoaded=" + modelLoaded +
                ", privacyMode=" + privacyMode +
                '}';
    }
}