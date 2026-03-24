package com.im.system.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 边缘视频处理实体
 * 用于存储边缘计算节点的实时音视频处理任务状态和配置
 */
@Entity
@Table(name = "edge_video_processing")
public class EdgeVideoProcessingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "task_id", nullable = false, unique = true)
    private String taskId;

    @Column(name = "session_id", nullable = false)
    private String sessionId;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "edge_node_id", nullable = false)
    private String edgeNodeId;

    @Column(name = "media_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private MediaType mediaType;

    @Column(name = "input_source", nullable = false)
    private String inputSource;

    @Column(name = "output_destination")
    private String outputDestination;

    @Column(name = "processing_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ProcessingStatus processingStatus;

    @Column(name = "video_codec")
    private String videoCodec;

    @Column(name = "audio_codec")
    private String audioCodec;

    @Column(name = "resolution_width")
    private Integer resolutionWidth;

    @Column(name = "resolution_height")
    private Integer resolutionHeight;

    @Column(name = "frame_rate")
    private Integer frameRate;

    @Column(name = "bitrate_kbps")
    private Integer bitrateKbps;

    @Column(name = "ai_enhancements_enabled")
    private Boolean aiEnhancementsEnabled;

    @Column(name = "enhancement_type")
    private String enhancementType;

    @Column(name = "bandwidth_optimization_enabled")
    private Boolean bandwidthOptimizationEnabled;

    @Column(name = "compression_level")
    private Integer compressionLevel;

    @Column(name = "latency_ms")
    private Integer latencyMs;

    @Column(name = "processing_start_time")
    private LocalDateTime processingStartTime;

    @Column(name = "processing_end_time")
    private LocalDateTime processingEndTime;

    @Column(name = "processing_duration_ms")
    private Long processingDurationMs;

    @Column(name = "cpu_usage_percent")
    private Double cpuUsagePercent;

    @Column(name = "memory_usage_mb")
    private Integer memoryUsageMb;

    @Column(name = "network_bandwidth_mbps")
    private Double networkBandwidthMbps;

    @Column(name = "quality_score")
    private Double qualityScore;

    @Column(name = "error_message")
    private String errorMessage;

    @Column(name = "retry_count")
    private Integer retryCount = 0;

    @Column(name = "max_retries")
    private Integer maxRetries = 3;

    @Column(name = "priority_level")
    private Integer priorityLevel = 5;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "expires_at")
    private LocalDateTime expiresAt;

    @Column(name = "metadata_json")
    @Lob
    private String metadataJson;

    // 构造函数
    public EdgeVideoProcessingEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public EdgeVideoProcessingEntity(String taskId, String sessionId, String userId, String edgeNodeId, 
                                   MediaType mediaType, String inputSource) {
        this();
        this.taskId = taskId;
        this.sessionId = sessionId;
        this.userId = userId;
        this.edgeNodeId = edgeNodeId;
        this.mediaType = mediaType;
        this.inputSource = inputSource;
        this.processingStatus = ProcessingStatus.PENDING;
    }

    // 枚举类型定义
    public enum MediaType {
        VIDEO_ONLY,
        AUDIO_ONLY,
        VIDEO_WITH_AUDIO,
        SCREEN_SHARE,
        VIDEO_CONFERENCE,
        LIVE_STREAMING,
        VOD_PROCESSING
    }

    public enum ProcessingStatus {
        PENDING,
        QUEUED,
        PROCESSING,
        PAUSED,
        COMPLETED,
        FAILED,
        CANCELLED,
        TIMEOUT
    }

    // Getter 和 Setter 方法
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEdgeNodeId() {
        return edgeNodeId;
    }

    public void setEdgeNodeId(String edgeNodeId) {
        this.edgeNodeId = edgeNodeId;
    }

    public MediaType getMediaType() {
        return mediaType;
    }

    public void setMediaType(MediaType mediaType) {
        this.mediaType = mediaType;
    }

    public String getInputSource() {
        return inputSource;
    }

    public void setInputSource(String inputSource) {
        this.inputSource = inputSource;
    }

    public String getOutputDestination() {
        return outputDestination;
    }

    public void setOutputDestination(String outputDestination) {
        this.outputDestination = outputDestination;
    }

    public ProcessingStatus getProcessingStatus() {
        return processingStatus;
    }

    public void setProcessingStatus(ProcessingStatus processingStatus) {
        this.processingStatus = processingStatus;
    }

    public String getVideoCodec() {
        return videoCodec;
    }

    public void setVideoCodec(String videoCodec) {
        this.videoCodec = videoCodec;
    }

    public String getAudioCodec() {
        return audioCodec;
    }

    public void setAudioCodec(String audioCodec) {
        this.audioCodec = audioCodec;
    }

    public Integer getResolutionWidth() {
        return resolutionWidth;
    }

    public void setResolutionWidth(Integer resolutionWidth) {
        this.resolutionWidth = resolutionWidth;
    }

    public Integer getResolutionHeight() {
        return resolutionHeight;
    }

    public void setResolutionHeight(Integer resolutionHeight) {
        this.resolutionHeight = resolutionHeight;
    }

    public Integer getFrameRate() {
        return frameRate;
    }

    public void setFrameRate(Integer frameRate) {
        this.frameRate = frameRate;
    }

    public Integer getBitrateKbps() {
        return bitrateKbps;
    }

    public void setBitrateKbps(Integer bitrateKbps) {
        this.bitrateKbps = bitrateKbps;
    }

    public Boolean getAiEnhancementsEnabled() {
        return aiEnhancementsEnabled;
    }

    public void setAiEnhancementsEnabled(Boolean aiEnhancementsEnabled) {
        this.aiEnhancementsEnabled = aiEnhancementsEnabled;
    }

    public String getEnhancementType() {
        return enhancementType;
    }

    public void setEnhancementType(String enhancementType) {
        this.enhancementType = enhancementType;
    }

    public Boolean getBandwidthOptimizationEnabled() {
        return bandwidthOptimizationEnabled;
    }

    public void setBandwidthOptimizationEnabled(Boolean bandwidthOptimizationEnabled) {
        this.bandwidthOptimizationEnabled = bandwidthOptimizationEnabled;
    }

    public Integer getCompressionLevel() {
        return compressionLevel;
    }

    public void setCompressionLevel(Integer compressionLevel) {
        this.compressionLevel = compressionLevel;
    }

    public Integer getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Integer latencyMs) {
        this.latencyMs = latencyMs;
    }

    public LocalDateTime getProcessingStartTime() {
        return processingStartTime;
    }

    public void setProcessingStartTime(LocalDateTime processingStartTime) {
        this.processingStartTime = processingStartTime;
    }

    public LocalDateTime getProcessingEndTime() {
        return processingEndTime;
    }

    public void setProcessingEndTime(LocalDateTime processingEndTime) {
        this.processingEndTime = processingEndTime;
    }

    public Long getProcessingDurationMs() {
        return processingDurationMs;
    }

    public void setProcessingDurationMs(Long processingDurationMs) {
        this.processingDurationMs = processingDurationMs;
    }

    public Double getCpuUsagePercent() {
        return cpuUsagePercent;
    }

    public void setCpuUsagePercent(Double cpuUsagePercent) {
        this.cpuUsagePercent = cpuUsagePercent;
    }

    public Integer getMemoryUsageMb() {
        return memoryUsageMb;
    }

    public void setMemoryUsageMb(Integer memoryUsageMb) {
        this.memoryUsageMb = memoryUsageMb;
    }

    public Double getNetworkBandwidthMbps() {
        return networkBandwidthMbps;
    }

    public void setNetworkBandwidthMbps(Double networkBandwidthMbps) {
        this.networkBandwidthMbps = networkBandwidthMbps;
    }

    public Double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Double qualityScore) {
        this.qualityScore = qualityScore;
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

    public Integer getMaxRetries() {
        return maxRetries;
    }

    public void setMaxRetries(Integer maxRetries) {
        this.maxRetries = maxRetries;
    }

    public Integer getPriorityLevel() {
        return priorityLevel;
    }

    public void setPriorityLevel(Integer priorityLevel) {
        this.priorityLevel = priorityLevel;
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

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    // 业务方法
    public void startProcessing() {
        this.processingStatus = ProcessingStatus.PROCESSING;
        this.processingStartTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void completeProcessing(String outputDestination, Double qualityScore) {
        this.processingStatus = ProcessingStatus.COMPLETED;
        this.processingEndTime = LocalDateTime.now();
        this.outputDestination = outputDestination;
        this.qualityScore = qualityScore;
        
        if (this.processingStartTime != null) {
            this.processingDurationMs = java.time.Duration.between(
                this.processingStartTime, this.processingEndTime
            ).toMillis();
        }
        
        this.updatedAt = LocalDateTime.now();
    }

    public void failProcessing(String errorMessage) {
        this.processingStatus = ProcessingStatus.FAILED;
        this.errorMessage = errorMessage;
        this.processingEndTime = LocalDateTime.now();
        
        if (this.processingStartTime != null) {
            this.processingDurationMs = java.time.Duration.between(
                this.processingStartTime, this.processingEndTime
            ).toMillis();
        }
        
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canRetry() {
        return this.retryCount < this.maxRetries;
    }

    public void incrementRetryCount() {
        this.retryCount++;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "EdgeVideoProcessingEntity{" +
                "id=" + id +
                ", taskId='" + taskId + '\'' +
                ", sessionId='" + sessionId + '\'' +
                ", userId='" + userId + '\'' +
                ", edgeNodeId='" + edgeNodeId + '\'' +
                ", mediaType=" + mediaType +
                ", processingStatus=" + processingStatus +
                ", latencyMs=" + latencyMs +
                '}';
    }
}