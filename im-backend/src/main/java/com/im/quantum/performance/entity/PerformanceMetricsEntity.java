package com.im.quantum.performance.entity;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

/**
 * 量子通信性能指标实体类
 * 用于存储量子密钥分发(QKD)系统的性能数据
 * 
 * @author Quantum Performance Team
 * @since 2026-03-26
 */
@Entity
@Table(name = "quantum_performance_metrics", indexes = {
    @Index(name = "idx_metrics_timestamp", columnList = "timestamp DESC"),
    @Index(name = "idx_metrics_session", columnList = "sessionId"),
    @Index(name = "idx_metrics_metric_type", columnList = "metricType, timestamp DESC")
})
public class PerformanceMetricsEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;
    
    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "metric_type", nullable = false, length = 32)
    private MetricType metricType;
    
    @Column(name = "timestamp", nullable = false)
    private Instant timestamp;
    
    @Column(name = "source_node", length = 128)
    private String sourceNode;
    
    @Column(name = "target_node", length = 128)
    private String targetNode;
    
    // 核心性能指标
    @Column(name = "key_generation_rate")
    private Double keyGenerationRate;
    
    @Column(name = "quantum_bit_error_rate")
    private Double quantumBitErrorRate;
    
    @Column(name = "latency_ms")
    private Double latencyMs;
    
    @Column(name = "throughput_mbps")
    private Double throughputMbps;
    
    @Column(name = "packet_loss_rate")
    private Double packetLossRate;
    
    @Column(name = "connection_stability")
    private Double connectionStability;
    
    @Column(name = "channel_quality_index")
    private Double channelQualityIndex;
    
    // 安全指标
    @Column(name = "eavesdropper_detection_rate")
    private Double eavesdropperDetectionRate;
    
    @Column(name = "security_level_score")
    private Integer securityLevelScore;
    
    @Column(name = "authentication_success_rate")
    private Double authenticationSuccessRate;
    
    // 资源使用指标
    @Column(name = "cpu_usage_percent")
    private Double cpuUsagePercent;
    
    @Column(name = "memory_usage_mb")
    private Double memoryUsageMb;
    
    @Column(name = "network_io_mbps")
    private Double networkIoMbps;
    
    @Column(name = "active_connections")
    private Integer activeConnections;
    
    @Column(name = "pending_operations")
    private Integer pendingOperations;
    
    // 告警和状态
    @Column(name = "alert_level", length = 16)
    @Enumerated(EnumType.STRING)
    private AlertLevel alertLevel;
    
    @Column(name = "alert_message", length = 512)
    private String alertMessage;
    
    @Column(name = "is_optimized")
    private Boolean isOptimized = false;
    
    @Column(name = "optimization_count")
    private Integer optimizationCount = 0;
    
    // 扩展属性存储为JSON
    @ElementCollection
    @CollectionTable(name = "quantum_metrics_attributes", joinColumns = @JoinColumn(name = "metric_id"))
    @MapKeyColumn(name = "attr_key", length = 64)
    @Column(name = "attr_value", length = 256)
    private Map<String, String> attributes = new HashMap<>();
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
    
    @Column(name = "updated_at")
    private Instant updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        if (timestamp == null) {
            timestamp = createdAt;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
    
    // 枚举定义
    public enum MetricType {
        QKD_PERFORMANCE,
        CHANNEL_QUALITY,
        SECURITY_METRICS,
        RESOURCE_USAGE,
        NETWORK_LATENCY,
        KEY_GENERATION,
        ERROR_CORRECTION,
        PRIVACY_AMPLIFICATION,
        OVERALL_SYSTEM
    }
    
    public enum AlertLevel {
        NONE,
        INFO,
        WARNING,
        CRITICAL,
        EMERGENCY
    }
    
    // 构造函数
    public PerformanceMetricsEntity() {}
    
    public PerformanceMetricsEntity(String sessionId, MetricType metricType) {
        this.sessionId = sessionId;
        this.metricType = metricType;
        this.timestamp = Instant.now();
    }
    
    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public MetricType getMetricType() { return metricType; }
    public void setMetricType(MetricType metricType) { this.metricType = metricType; }
    
    public Instant getTimestamp() { return timestamp; }
    public void setTimestamp(Instant timestamp) { this.timestamp = timestamp; }
    
    public String getSourceNode() { return sourceNode; }
    public void setSourceNode(String sourceNode) { this.sourceNode = sourceNode; }
    
    public String getTargetNode() { return targetNode; }
    public void setTargetNode(String targetNode) { this.targetNode = targetNode; }
    
    public Double getKeyGenerationRate() { return keyGenerationRate; }
    public void setKeyGenerationRate(Double keyGenerationRate) { this.keyGenerationRate = keyGenerationRate; }
    
    public Double getQuantumBitErrorRate() { return quantumBitErrorRate; }
    public void setQuantumBitErrorRate(Double quantumBitErrorRate) { this.quantumBitErrorRate = quantumBitErrorRate; }
    
    public Double getLatencyMs() { return latencyMs; }
    public void setLatencyMs(Double latencyMs) { this.latencyMs = latencyMs; }
    
    public Double getThroughputMbps() { return throughputMbps; }
    public void setThroughputMbps(Double throughputMbps) { this.throughputMbps = throughputMbps; }
    
    public Double getPacketLossRate() { return packetLossRate; }
    public void setPacketLossRate(Double packetLossRate) { this.packetLossRate = packetLossRate; }
    
    public Double getConnectionStability() { return connectionStability; }
    public void setConnectionStability(Double connectionStability) { this.connectionStability = connectionStability; }
    
    public Double getChannelQualityIndex() { return channelQualityIndex; }
    public void setChannelQualityIndex(Double channelQualityIndex) { this.channelQualityIndex = channelQualityIndex; }
    
    public Double getEavesdropperDetectionRate() { return eavesdropperDetectionRate; }
    public void setEavesdropperDetectionRate(Double eavesdropperDetectionRate) { this.eavesdropperDetectionRate = eavesdropperDetectionRate; }
    
    public Integer getSecurityLevelScore() { return securityLevelScore; }
    public void setSecurityLevelScore(Integer securityLevelScore) { this.securityLevelScore = securityLevelScore; }
    
    public Double getAuthenticationSuccessRate() { return authenticationSuccessRate; }
    public void setAuthenticationSuccessRate(Double authenticationSuccessRate) { this.authenticationSuccessRate = authenticationSuccessRate; }
    
    public Double getCpuUsagePercent() { return cpuUsagePercent; }
    public void setCpuUsagePercent(Double cpuUsagePercent) { this.cpuUsagePercent = cpuUsagePercent; }
    
    public Double getMemoryUsageMb() { return memoryUsageMb; }
    public void setMemoryUsageMb(Double memoryUsageMb) { this.memoryUsageMb = memoryUsageMb; }
    
    public Double getNetworkIoMbps() { return networkIoMbps; }
    public void setNetworkIoMbps(Double networkIoMbps) { this.networkIoMbps = networkIoMbps; }
    
    public Integer getActiveConnections() { return activeConnections; }
    public void setActiveConnections(Integer activeConnections) { this.activeConnections = activeConnections; }
    
    public Integer getPendingOperations() { return pendingOperations; }
    public void setPendingOperations(Integer pendingOperations) { this.pendingOperations = pendingOperations; }
    
    public AlertLevel getAlertLevel() { return alertLevel; }
    public void setAlertLevel(AlertLevel alertLevel) { this.alertLevel = alertLevel; }
    
    public String getAlertMessage() { return alertMessage; }
    public void setAlertMessage(String alertMessage) { this.alertMessage = alertMessage; }
    
    public Boolean getIsOptimized() { return isOptimized; }
    public void setIsOptimized(Boolean isOptimized) { this.isOptimized = isOptimized; }
    
    public Integer getOptimizationCount() { return optimizationCount; }
    public void setOptimizationCount(Integer optimizationCount) { this.optimizationCount = optimizationCount; }
    
    public Map<String, String> getAttributes() { return attributes; }
    public void setAttributes(Map<String, String> attributes) { this.attributes = attributes; }
    
    public void addAttribute(String key, String value) {
        this.attributes.put(key, value);
    }
    
    public Instant getCreatedAt() { return createdAt; }
    public Instant getUpdatedAt() { return updatedAt; }
    
    /**
     * 计算综合性能分数
     * @return 0-100之间的性能分数
     */
    public Double calculatePerformanceScore() {
        double score = 100.0;
        
        // 根据各项指标计算得分
        if (quantumBitErrorRate != null) {
            score -= quantumBitErrorRate * 50; // QBER越高，分数越低
        }
        if (latencyMs != null && latencyMs > 100) {
            score -= Math.min(20, (latencyMs - 100) / 10);
        }
        if (packetLossRate != null) {
            score -= packetLossRate * 100;
        }
        if (connectionStability != null) {
            score += (connectionStability - 0.5) * 20;
        }
        if (securityLevelScore != null) {
            score += (securityLevelScore - 50) * 0.2;
        }
        
        return Math.max(0, Math.min(100, score));
    }
    
    /**
     * 检测是否需要告警
     * @return 告警级别
     */
    public AlertLevel detectAlertLevel() {
        if (quantumBitErrorRate != null && quantumBitErrorRate > 0.15) {
            return AlertLevel.CRITICAL;
        }
        if (latencyMs != null && latencyMs > 500) {
            return AlertLevel.WARNING;
        }
        if (packetLossRate != null && packetLossRate > 0.05) {
            return AlertLevel.WARNING;
        }
        if (connectionStability != null && connectionStability < 0.3) {
            return AlertLevel.CRITICAL;
        }
        if (cpuUsagePercent != null && cpuUsagePercent > 90) {
            return AlertLevel.WARNING;
        }
        return AlertLevel.NONE;
    }
    
    @Override
    public String toString() {
        return String.format("PerformanceMetrics[id=%s, session=%s, type=%s, timestamp=%s, score=%.2f]",
            id, sessionId, metricType, timestamp, calculatePerformanceScore());
    }
}
