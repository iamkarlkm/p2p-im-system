package com.im.system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * 量子通信性能实体
 * 用于存储和追踪量子密钥分发(QKD)的性能指标和优化数据
 * 
 * 核心功能:
 * 1. 量子通信链路性能监控
 * 2. 性能优化参数记录
 * 3. 多目标优化策略存储
 * 4. 性能历史趋势追踪
 */
@Entity
@Table(name = "quantum_communication_performance")
public class QuantumCommunicationPerformanceEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 量子通信链路唯一标识符
     * 格式: QKD_[节点ID]_[对端ID]_[链路类型]
     */
    @Column(name = "link_id", nullable = false, unique = true, length = 100)
    private String linkId;

    /**
     * 本地节点ID
     */
    @Column(name = "local_node_id", nullable = false, length = 50)
    private String localNodeId;

    /**
     * 对端节点ID
     */
    @Column(name = "remote_node_id", nullable = false, length = 50)
    private String remoteNodeId;

    /**
     * 量子通信协议类型
     * BB84, E91, B92, COW, SARG04, MDI-QKD等
     */
    @Column(name = "protocol_type", nullable = false, length = 50)
    private String protocolType;

    /**
     * 链路状态
     * ACTIVE: 活跃状态
     * DEGRADED: 性能降级
     * FAILED: 链路失败
     * MAINTENANCE: 维护中
     * DISABLED: 已禁用
     */
    @Column(name = "link_status", nullable = false, length = 20)
    private String linkStatus;

    /**
     * 基础性能指标
     */
    
    // 量子密钥分发速率 (bits/sec)
    @Column(name = "key_rate", nullable = false)
    private Double keyRate;

    // 误码率 (Bit Error Rate)
    @Column(name = "qber", nullable = false)
    private Double qber;

    // 链路延迟 (毫秒)
    @Column(name = "latency", nullable = false)
    private Double latency;

    // 链路抖动 (毫秒)
    @Column(name = "jitter", nullable = false)
    private Double jitter;

    // 可用性百分比
    @Column(name = "availability", nullable = false)
    private Double availability;

    /**
     * 链路质量指标
     */
    
    // 链路质量综合评分 (0-100)
    @Column(name = "quality_score", nullable = false)
    private Double qualityScore;

    // 安全评分 (0-100)
    @Column(name = "security_score", nullable = false)
    private Double securityScore;

    // 效率评分 (0-100)
    @Column(name = "efficiency_score", nullable = false)
    private Double efficiencyScore;

    // 稳定性评分 (0-100)
    @Column(name = "stability_score", nullable = false)
    private Double stabilityScore;

    /**
     * 多目标优化权重
     * 四个维度的优化权重，总和为1.0
     */
    
    @Column(name = "weight_latency", nullable = false)
    private Double weightLatency;

    @Column(name = "weight_bandwidth", nullable = false)
    private Double weightBandwidth;

    @Column(name = "weight_energy", nullable = false)
    private Double weightEnergy;

    @Column(name = "weight_security", nullable = false)
    private Double weightSecurity;

    /**
     * 优化策略相关字段
     */
    
    // 当前优化策略ID
    @Column(name = "current_strategy_id", length = 50)
    private String currentStrategyId;

    // 策略生效时间
    @Column(name = "strategy_applied_at")
    private LocalDateTime strategyAppliedAt;

    // 策略性能提升百分比
    @Column(name = "strategy_improvement")
    private Double strategyImprovement;

    // 混合加密策略状态
    @Column(name = "hybrid_encryption_status", length = 30)
    private String hybridEncryptionStatus;

    /**
     * 性能历史数据
     */
    
    // 过去24小时平均性能
    @Column(name = "avg_performance_24h")
    private Double avgPerformance24h;

    // 性能趋势 (UP: 上升, DOWN: 下降, STABLE: 稳定)
    @Column(name = "performance_trend", length = 20)
    private String performanceTrend;

    // 最近一次性能检查时间
    @Column(name = "last_check_time")
    private LocalDateTime lastCheckTime;

    // 下次优化建议时间
    @Column(name = "next_optimization_time")
    private LocalDateTime nextOptimizationTime;

    /**
     * 环境与配置参数
     */
    
    // 量子信道类型 (光纤/自由空间/卫星)
    @Column(name = "channel_type", length = 30)
    private String channelType;

    // 信道长度 (公里)
    @Column(name = "channel_length")
    private Double channelLength;

    // 信道损耗 (dB)
    @Column(name = "channel_loss")
    private Double channelLoss;

    // 环境温度 (摄氏度)
    @Column(name = "temperature")
    private Double temperature;

    // 环境湿度 (百分比)
    @Column(name = "humidity")
    private Double humidity;

    /**
     * 扩展性能参数 (JSON格式存储)
     */
    @Column(name = "extended_params", columnDefinition = "TEXT")
    private String extendedParams;

    /**
     * 元数据字段
     */
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "version", nullable = false)
    private Integer version;

    // 构造函数
    public QuantumCommunicationPerformanceEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.version = 1;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLinkId() {
        return linkId;
    }

    public void setLinkId(String linkId) {
        this.linkId = linkId;
    }

    public String getLocalNodeId() {
        return localNodeId;
    }

    public void setLocalNodeId(String localNodeId) {
        this.localNodeId = localNodeId;
    }

    public String getRemoteNodeId() {
        return remoteNodeId;
    }

    public void setRemoteNodeId(String remoteNodeId) {
        this.remoteNodeId = remoteNodeId;
    }

    public String getProtocolType() {
        return protocolType;
    }

    public void setProtocolType(String protocolType) {
        this.protocolType = protocolType;
    }

    public String getLinkStatus() {
        return linkStatus;
    }

    public void setLinkStatus(String linkStatus) {
        this.linkStatus = linkStatus;
    }

    public Double getKeyRate() {
        return keyRate;
    }

    public void setKeyRate(Double keyRate) {
        this.keyRate = keyRate;
    }

    public Double getQber() {
        return qber;
    }

    public void setQber(Double qber) {
        this.qber = qber;
    }

    public Double getLatency() {
        return latency;
    }

    public void setLatency(Double latency) {
        this.latency = latency;
    }

    public Double getJitter() {
        return jitter;
    }

    public void setJitter(Double jitter) {
        this.jitter = jitter;
    }

    public Double getAvailability() {
        return availability;
    }

    public void setAvailability(Double availability) {
        this.availability = availability;
    }

    public Double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Double qualityScore) {
        this.qualityScore = qualityScore;
    }

    public Double getSecurityScore() {
        return securityScore;
    }

    public void setSecurityScore(Double securityScore) {
        this.securityScore = securityScore;
    }

    public Double getEfficiencyScore() {
        return efficiencyScore;
    }

    public void setEfficiencyScore(Double efficiencyScore) {
        this.efficiencyScore = efficiencyScore;
    }

    public Double getStabilityScore() {
        return stabilityScore;
    }

    public void setStabilityScore(Double stabilityScore) {
        this.stabilityScore = stabilityScore;
    }

    public Double getWeightLatency() {
        return weightLatency;
    }

    public void setWeightLatency(Double weightLatency) {
        this.weightLatency = weightLatency;
    }

    public Double getWeightBandwidth() {
        return weightBandwidth;
    }

    public void setWeightBandwidth(Double weightBandwidth) {
        this.weightBandwidth = weightBandwidth;
    }

    public Double getWeightEnergy() {
        return weightEnergy;
    }

    public void setWeightEnergy(Double weightEnergy) {
        this.weightEnergy = weightEnergy;
    }

    public Double getWeightSecurity() {
        return weightSecurity;
    }

    public void setWeightSecurity(Double weightSecurity) {
        this.weightSecurity = weightSecurity;
    }

    public String getCurrentStrategyId() {
        return currentStrategyId;
    }

    public void setCurrentStrategyId(String currentStrategyId) {
        this.currentStrategyId = currentStrategyId;
    }

    public LocalDateTime getStrategyAppliedAt() {
        return strategyAppliedAt;
    }

    public void setStrategyAppliedAt(LocalDateTime strategyAppliedAt) {
        this.strategyAppliedAt = strategyAppliedAt;
    }

    public Double getStrategyImprovement() {
        return strategyImprovement;
    }

    public void setStrategyImprovement(Double strategyImprovement) {
        this.strategyImprovement = strategyImprovement;
    }

    public String getHybridEncryptionStatus() {
        return hybridEncryptionStatus;
    }

    public void setHybridEncryptionStatus(String hybridEncryptionStatus) {
        this.hybridEncryptionStatus = hybridEncryptionStatus;
    }

    public Double getAvgPerformance24h() {
        return avgPerformance24h;
    }

    public void setAvgPerformance24h(Double avgPerformance24h) {
        this.avgPerformance24h = avgPerformance24h;
    }

    public String getPerformanceTrend() {
        return performanceTrend;
    }

    public void setPerformanceTrend(String performanceTrend) {
        this.performanceTrend = performanceTrend;
    }

    public LocalDateTime getLastCheckTime() {
        return lastCheckTime;
    }

    public void setLastCheckTime(LocalDateTime lastCheckTime) {
        this.lastCheckTime = lastCheckTime;
    }

    public LocalDateTime getNextOptimizationTime() {
        return nextOptimizationTime;
    }

    public void setNextOptimizationTime(LocalDateTime nextOptimizationTime) {
        this.nextOptimizationTime = nextOptimizationTime;
    }

    public String getChannelType() {
        return channelType;
    }

    public void setChannelType(String channelType) {
        this.channelType = channelType;
    }

    public Double getChannelLength() {
        return channelLength;
    }

    public void setChannelLength(Double channelLength) {
        this.channelLength = channelLength;
    }

    public Double getChannelLoss() {
        return channelLoss;
    }

    public void setChannelLoss(Double channelLoss) {
        this.channelLoss = channelLoss;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Double getHumidity() {
        return humidity;
    }

    public void setHumidity(Double humidity) {
        this.humidity = humidity;
    }

    public String getExtendedParams() {
        return extendedParams;
    }

    public void setExtendedParams(String extendedParams) {
        this.extendedParams = extendedParams;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    /**
     * 计算综合质量评分
     * 权重公式: 质量 = (latencyScore + availabilityScore + securityScore) / 3
     * 
     * @return 综合质量评分 (0-100)
     */
    public Double calculateQualityScore() {
        // 延迟评分 (越低越好)
        double latencyScore = 100 - Math.min(this.latency / 10.0, 100);
        
        // 可用性评分 (越高越好)
        double availabilityScore = this.availability;
        
        // 安全评分 (QBER越低越好)
        double securityScore = 100 - (this.qber * 1000);
        
        this.qualityScore = (latencyScore + availabilityScore + securityScore) / 3.0;
        return this.qualityScore;
    }

    /**
     * 验证权重总和是否为1.0
     * 
     * @return 是否验证通过
     */
    public boolean validateWeights() {
        double sum = this.weightLatency + this.weightBandwidth + 
                    this.weightEnergy + this.weightSecurity;
        return Math.abs(sum - 1.0) < 0.001;
    }

    /**
     * 获取性能状态描述
     * 
     * @return 性能状态描述
     */
    public String getPerformanceStatus() {
        if (this.qualityScore >= 90) {
            return "EXCELLENT";
        } else if (this.qualityScore >= 70) {
            return "GOOD";
        } else if (this.qualityScore >= 50) {
            return "FAIR";
        } else if (this.qualityScore >= 30) {
            return "POOR";
        } else {
            return "CRITICAL";
        }
    }

    /**
     * 判断是否需要优化
     * 
     * @return 是否需要优化
     */
    public boolean needsOptimization() {
        return this.qualityScore < 60 || 
               this.performanceTrend.equals("DOWN") ||
               (this.nextOptimizationTime != null && 
                LocalDateTime.now().isAfter(this.nextOptimizationTime));
    }

    /**
     * 更新实体时自动更新时间戳和版本
     */
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
        this.version++;
    }

    @Override
    public String toString() {
        return "QuantumCommunicationPerformanceEntity{" +
                "id=" + id +
                ", linkId='" + linkId + '\'' +
                ", localNodeId='" + localNodeId + '\'' +
                ", remoteNodeId='" + remoteNodeId + '\'' +
                ", protocolType='" + protocolType + '\'' +
                ", linkStatus='" + linkStatus + '\'' +
                ", qualityScore=" + qualityScore +
                ", needsOptimization=" + needsOptimization() +
                '}';
    }
}