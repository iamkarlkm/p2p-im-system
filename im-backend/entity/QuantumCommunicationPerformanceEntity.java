package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 量子通信性能优化实体
 * 记录量子密钥分发性能指标和优化策略
 * 
 * @author IM System
 * @version 1.0.0
 * @since 2026-03-25
 */
@Entity
@Table(name = "quantum_communication_performance", indexes = {
    @Index(name = "idx_quantum_perf_session_id", columnList = "sessionId"),
    @Index(name = "idx_quantum_perf_user_id", columnList = "userId"),
    @Index(name = "idx_quantum_perf_created_at", columnList = "createdAt"),
    @Index(name = "idx_quantum_perf_optimization_strategy", columnList = "optimizationStrategy")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuantumCommunicationPerformanceEntity {

    /**
     * 主键ID
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false, updatable = false)
    private Long id;

    /**
     * 会话ID（与通信会话关联）
     */
    @Column(name = "session_id", nullable = false, length = 64)
    private String sessionId;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false, length = 32)
    private String userId;

    /**
     * 设备ID（发送或接收设备）
     */
    @Column(name = "device_id", length = 64)
    private String deviceId;

    /**
     * 通信类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "communication_type", nullable = false, length = 32)
    private CommunicationType communicationType;

    /**
     * 量子密钥分发协议类型
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "qkd_protocol", length = 32)
    private QKDProtocol qkdProtocol;

    /**
     * 传统加密算法（用于混合策略）
     */
    @Column(name = "traditional_algorithm", length = 32)
    private String traditionalAlgorithm;

    /**
     * 量子安全算法
     */
    @Column(name = "quantum_algorithm", length = 32)
    private String quantumAlgorithm;

    /**
     * 密钥协商开始时间
     */
    @Column(name = "key_negotiation_start_time")
    private LocalDateTime keyNegotiationStartTime;

    /**
     * 密钥协商结束时间
     */
    @Column(name = "key_negotiation_end_time")
    private LocalDateTime keyNegotiationEndTime;

    /**
     * 密钥协商延迟（毫秒）
     */
    @Column(name = "key_negotiation_latency_ms")
    private Long keyNegotiationLatencyMs;

    /**
     * 密钥生成速率（密钥/秒）
     */
    @Column(name = "key_generation_rate", precision = 10, scale = 2)
    private BigDecimal keyGenerationRate;

    /**
     * 密钥分发成功率（百分比）
     */
    @Column(name = "key_distribution_success_rate", precision = 5, scale = 2)
    private BigDecimal keyDistributionSuccessRate;

    /**
     * 量子误码率（QBER）
     */
    @Column(name = "quantum_bit_error_rate", precision = 5, scale = 4)
    private BigDecimal quantumBitErrorRate;

    /**
     * 链路质量评分（0-100）
     */
    @Column(name = "link_quality_score")
    private Integer linkQualityScore;

    /**
     * 信号强度（dBm）
     */
    @Column(name = "signal_strength", precision = 5, scale = 2)
    private BigDecimal signalStrength;

    /**
     * 环境噪声级别（dB）
     */
    @Column(name = "environment_noise_level", precision = 5, scale = 2)
    private BigDecimal environmentNoiseLevel;

    /**
     * 温度（摄氏度）
     */
    @Column(name = "temperature", precision = 5, scale = 2)
    private BigDecimal temperature;

    /**
     * 湿度（百分比）
     */
    @Column(name = "humidity", precision = 5, scale = 2)
    private BigDecimal humidity;

    /**
     * 加密策略
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "encryption_strategy", nullable = false, length = 32)
    private EncryptionStrategy encryptionStrategy;

    /**
     * 优化策略
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "optimization_strategy", nullable = false, length = 32)
    private OptimizationStrategy optimizationStrategy;

    /**
     * 协议协商版本
     */
    @Column(name = "protocol_version", length = 16)
    private String protocolVersion;

    /**
     * 链路监控状态
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "link_monitoring_status", length = 32)
    private LinkMonitoringStatus linkMonitoringStatus;

    /**
     * 性能优化建议
     */
    @Column(name = "performance_recommendation", length = 512)
    private String performanceRecommendation;

    /**
     * 实际应用的优化措施
     */
    @Column(name = "applied_optimization", length = 512)
    private String appliedOptimization;

    /**
     * 优化效果提升百分比
     */
    @Column(name = "optimization_improvement_percent", precision = 5, scale = 2)
    private BigDecimal optimizationImprovementPercent;

    /**
     * 带宽节省（KB）
     */
    @Column(name = "bandwidth_saved_kb")
    private Long bandwidthSavedKb;

    /**
     * 延迟减少（毫秒）
     */
    @Column(name = "latency_reduced_ms")
    private Long latencyReducedMs;

    /**
     * 能量消耗节省（J）
     */
    @Column(name = "energy_saved_j", precision = 10, scale = 2)
    private BigDecimal energySavedJ;

    /**
     * 配置参数JSON
     */
    @Column(name = "configuration_params", columnDefinition = "TEXT")
    private String configurationParams;

    /**
     * 性能数据JSON
     */
    @Column(name = "performance_data", columnDefinition = "TEXT")
    private String performanceData;

    /**
     * 分析报告JSON
     */
    @Column(name = "analysis_report", columnDefinition = "TEXT")
    private String analysisReport;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * 性能采样时间戳
     */
    @Column(name = "sampled_at", nullable = false)
    private LocalDateTime sampledAt;

    /**
     * 是否为实时监控数据
     */
    @Column(name = "is_realtime_monitoring")
    private Boolean isRealtimeMonitoring;

    /**
     * 数据有效性标志
     */
    @Column(name = "is_valid")
    private Boolean isValid;

    /**
     * 备注信息
     */
    @Column(name = "remarks", length = 1024)
    private String remarks;

    /**
     * 通信类型枚举
     */
    public enum CommunicationType {
        QUANTUM_ONLY,           // 纯量子通信
        HYBRID_QUANTUM_CLASSIC, // 混合量子-经典通信
        CLASSIC_FALLBACK,       // 经典回退通信
        ADAPTIVE_SWITCHING,     // 自适应切换
        DUAL_PROTOCOL,          // 双协议并行
        PROGRESSIVE_MIGRATION   // 渐进迁移
    }

    /**
     * QKD协议枚举
     */
    public enum QKDProtocol {
        BB84,                   // BB84协议
        B92,                    // B92协议
        E91,                    // E91协议（纠缠态）
        SARG04,                 // SARG04协议
        COW,                    // COW协议（相干单向）
        DPS,                    // DPS协议（差分相位）
        TF_QKD,                 // 双场量子密钥分发
        MDI_QKD,                // 测量设备无关量子密钥分发
        CV_QKD,                 // 连续变量量子密钥分发
        DV_QKD,                 // 离散变量量子密钥分发
        SATELLITE_QKD,          // 卫星量子密钥分发
        MEMORY_QKD              // 量子存储器辅助QKD
    }

    /**
     * 加密策略枚举
     */
    public enum EncryptionStrategy {
        QUANTUM_ONLY,           // 仅量子加密
        CLASSIC_ONLY,           // 仅经典加密
        HYBRID_PARALLEL,        // 混合并行加密
        HYBRID_SEQUENTIAL,      // 混合顺序加密
        ADAPTIVE_SWITCHING,     // 自适应切换
        LOAD_BALANCED,          // 负载均衡
        SECURITY_PRIORITY,      // 安全优先
        PERFORMANCE_PRIORITY    // 性能优先
    }

    /**
     * 优化策略枚举
     */
    public enum OptimizationStrategy {
        LATENCY_OPTIMIZATION,   // 延迟优化
        THROUGHPUT_OPTIMIZATION, // 吞吐量优化
        BANDWIDTH_OPTIMIZATION, // 带宽优化
        ENERGY_OPTIMIZATION,    // 能量优化
        RELIABILITY_OPTIMIZATION, // 可靠性优化
        SECURITY_OPTIMIZATION,  // 安全性优化
        COST_OPTIMIZATION,      // 成本优化
        ADAPTIVE_MULTI_OBJECTIVE, // 自适应多目标优化
        AI_DRIVEN_OPTIMIZATION, // AI驱动优化
        REAL_TIME_ADAPTATION    // 实时自适应优化
    }

    /**
     * 链路监控状态枚举
     */
    public enum LinkMonitoringStatus {
        STABLE,                 // 稳定
        DEGRADED,               // 降级
        FLUCTUATING,            // 波动
        CRITICAL,               // 临界
        RECOVERING,             // 恢复中
        OPTIMIZED,              // 已优化
        MANUAL_INTERVENTION,    // 需人工干预
        AUTO_ADJUSTING          // 自动调整中
    }

    /**
     * 默认构造函数，初始化时间戳
     */
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (sampledAt == null) {
            sampledAt = LocalDateTime.now();
        }
        if (isValid == null) {
            isValid = true;
        }
        if (isRealtimeMonitoring == null) {
            isRealtimeMonitoring = false;
        }
    }

    /**
     * 更新时自动更新时间戳
     */
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    /**
     * 计算密钥协商延迟
     */
    public void calculateKeyNegotiationLatency() {
        if (keyNegotiationStartTime != null && keyNegotiationEndTime != null) {
            long startMillis = keyNegotiationStartTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            long endMillis = keyNegotiationEndTime.atZone(java.time.ZoneId.systemDefault()).toInstant().toEpochMilli();
            this.keyNegotiationLatencyMs = endMillis - startMillis;
        }
    }

    /**
     * 计算链路质量评分
     * 综合量子误码率、信号强度、环境噪声等因素
     */
    public void calculateLinkQualityScore() {
        if (quantumBitErrorRate == null || signalStrength == null || environmentNoiseLevel == null) {
            this.linkQualityScore = null;
            return;
        }
        
        // 量子误码率评分（QBER越低越好）
        double qberScore = Math.max(0, 100 - quantumBitErrorRate.doubleValue() * 10000);
        
        // 信号强度评分（信号越强越好）
        double signalScore = Math.max(0, Math.min(100, (signalStrength.doubleValue() + 100) * 0.5));
        
        // 环境噪声评分（噪声越低越好）
        double noiseScore = Math.max(0, Math.min(100, 100 - environmentNoiseLevel.doubleValue() * 2));
        
        // 综合评分（加权平均）
        double compositeScore = qberScore * 0.4 + signalScore * 0.3 + noiseScore * 0.3;
        
        this.linkQualityScore = (int) Math.round(compositeScore);
    }

    /**
     * 生成性能优化建议
     */
    public String generatePerformanceRecommendation() {
        if (linkQualityScore == null || keyNegotiationLatencyMs == null) {
            return "数据不完整，无法生成优化建议";
        }
        
        StringBuilder recommendation = new StringBuilder();
        
        if (linkQualityScore < 60) {
            recommendation.append("链路质量较差(").append(linkQualityScore).append("/100)，建议：");
            if (quantumBitErrorRate != null && quantumBitErrorRate.doubleValue() > 0.05) {
                recommendation.append("1. 降低量子误码率（当前QBER=").append(quantumBitErrorRate).append("）; ");
            }
            if (signalStrength != null && signalStrength.doubleValue() < -80) {
                recommendation.append("2. 提升信号强度（当前").append(signalStrength).append(" dBm）; ");
            }
            if (environmentNoiseLevel != null && environmentNoiseLevel.doubleValue() > 30) {
                recommendation.append("3. 降低环境噪声（当前").append(environmentNoiseLevel).append(" dB）; ");
            }
        }
        
        if (keyNegotiationLatencyMs != null && keyNegotiationLatencyMs > 1000) {
            if (recommendation.length() > 0) recommendation.append("; ");
            recommendation.append("密钥协商延迟过高(").append(keyNegotiationLatencyMs).append("ms)，建议优化协商协议");
        }
        
        if (keyDistributionSuccessRate != null && keyDistributionSuccessRate.doubleValue() < 90) {
            if (recommendation.length() > 0) recommendation.append("; ");
            recommendation.append("密钥分发成功率较低(").append(keyDistributionSuccessRate).append("%)，建议检查链路稳定性");
        }
        
        if (recommendation.length() == 0) {
            recommendation.append("性能良好，当前策略优化有效");
        }
        
        return recommendation.toString();
    }

    /**
     * 评估是否应切换到经典加密
     */
    public boolean shouldSwitchToClassicEncryption() {
        if (linkQualityScore == null || linkQualityScore < 30) {
            return true; // 链路质量极差，切换到经典加密
        }
        
        if (keyDistributionSuccessRate != null && keyDistributionSuccessRate.doubleValue() < 70) {
            return true; // 密钥分发成功率过低
        }
        
        if (quantumBitErrorRate != null && quantumBitErrorRate.doubleValue() > 0.1) {
            return true; // 量子误码率过高
        }
        
        return false;
    }

    /**
     * 评估是否应启用量子加密
     */
    public boolean shouldEnableQuantumEncryption() {
        if (linkQualityScore == null || linkQualityScore < 60) {
            return false; // 链路质量不足
        }
        
        if (keyDistributionSuccessRate != null && keyDistributionSuccessRate.doubleValue() < 85) {
            return false; // 密钥分发成功率不足
        }
        
        return true;
    }

    /**
     * 获取推荐的优化策略
     */
    public OptimizationStrategy getRecommendedOptimizationStrategy() {
        if (linkQualityScore == null) {
            return OptimizationStrategy.RELIABILITY_OPTIMIZATION;
        }
        
        if (linkQualityScore < 40) {
            return OptimizationStrategy.RELIABILITY_OPTIMIZATION;
        } else if (keyNegotiationLatencyMs != null && keyNegotiationLatencyMs > 500) {
            return OptimizationStrategy.LATENCY_OPTIMIZATION;
        } else if (keyGenerationRate != null && keyGenerationRate.doubleValue() < 100) {
            return OptimizationStrategy.THROUGHPUT_OPTIMIZATION;
        } else if (energySavedJ != null && energySavedJ.doubleValue() < 0) {
            return OptimizationStrategy.ENERGY_OPTIMIZATION;
        } else {
            return OptimizationStrategy.ADAPTIVE_MULTI_OBJECTIVE;
        }
    }

    /**
     * 验证数据有效性
     */
    public boolean validateData() {
        if (sessionId == null || sessionId.isEmpty()) {
            return false;
        }
        if (userId == null || userId.isEmpty()) {
            return false;
        }
        if (communicationType == null) {
            return false;
        }
        if (encryptionStrategy == null) {
            return false;
        }
        if (optimizationStrategy == null) {
            return false;
        }
        if (sampledAt == null) {
            return false;
        }
        
        // 验证数值范围
        if (linkQualityScore != null && (linkQualityScore < 0 || linkQualityScore > 100)) {
            return false;
        }
        if (quantumBitErrorRate != null && (quantumBitErrorRate.doubleValue() < 0 || quantumBitErrorRate.doubleValue() > 1)) {
            return false;
        }
        if (keyDistributionSuccessRate != null && (keyDistributionSuccessRate.doubleValue() < 0 || keyDistributionSuccessRate.doubleValue() > 100)) {
            return false;
        }
        
        return true;
    }

    /**
     * 转换为性能摘要字符串
     */
    public String toPerformanceSummary() {
        return String.format("量子通信性能[会话:%s, 用户:%s, 类型:%s, QBER:%.4f, 延迟:%dms, 成功率:%.2f%%, 质量:%d/100]",
            sessionId, userId, communicationType,
            quantumBitErrorRate != null ? quantumBitErrorRate.doubleValue() : 0.0,
            keyNegotiationLatencyMs != null ? keyNegotiationLatencyMs : 0,
            keyDistributionSuccessRate != null ? keyDistributionSuccessRate.doubleValue() : 0.0,
            linkQualityScore != null ? linkQualityScore : 0);
    }
}