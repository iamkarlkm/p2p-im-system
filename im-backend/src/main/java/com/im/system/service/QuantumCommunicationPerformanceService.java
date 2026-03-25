package com.im.system.service;

import com.im.system.entity.QuantumCommunicationPerformanceEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 量子通信性能优化服务
 * 
 * 核心功能:
 * 1. 量子通信链路性能监控与分析
 * 2. 自适应协议切换优化
 * 3. 混合加密策略管理
 * 4. 多目标优化决策
 * 5. AI驱动性能调优
 */
@Service
@Transactional
public class QuantumCommunicationPerformanceService {

    // 支持的QKD协议列表
    private static final List<String> SUPPORTED_QKD_PROTOCOLS = Arrays.asList(
        "BB84", "E91", "B92", "COW", "SARG04", "MDI-QKD", 
        "TF-QKD", "DPS-QKD", "GG02", "SixState"
    );

    // 协议性能特征映射
    private static final Map<String, ProtocolCharacteristics> PROTOCOL_CHARACTERISTICS = new HashMap<>();
    
    static {
        PROTOCOL_CHARACTERISTICS.put("BB84", new ProtocolCharacteristics(85.0, 0.02, 5.0, 95.0));
        PROTOCOL_CHARACTERISTICS.put("E91", new ProtocolCharacteristics(75.0, 0.015, 7.0, 98.0));
        PROTOCOL_CHARACTERISTICS.put("B92", new ProtocolCharacteristics(90.0, 0.03, 4.0, 90.0));
        PROTOCOL_CHARACTERISTICS.put("COW", new ProtocolCharacteristics(95.0, 0.025, 6.0, 92.0));
        PROTOCOL_CHARACTERISTICS.put("SARG04", new ProtocolCharacteristics(80.0, 0.018, 8.0, 96.0));
        PROTOCOL_CHARACTERISTICS.put("MDI-QKD", new ProtocolCharacteristics(70.0, 0.01, 10.0, 99.0));
        PROTOCOL_CHARACTERISTICS.put("TF-QKD", new ProtocolCharacteristics(65.0, 0.008, 12.0, 99.5));
        PROTOCOL_CHARACTERISTICS.put("DPS-QKD", new ProtocolCharacteristics(88.0, 0.022, 5.5, 93.0));
        PROTOCOL_CHARACTERISTICS.put("GG02", new ProtocolCharacteristics(82.0, 0.02, 6.5, 94.0));
        PROTOCOL_CHARACTERISTICS.put("SixState", new ProtocolCharacteristics(78.0, 0.016, 7.5, 97.0));
    }

    /**
     * 分析量子通信链路性能并提供优化建议
     * 
     * @param performanceEntity 性能实体
     * @return 优化建议结果
     */
    public OptimizationResult analyzeAndOptimize(QuantumCommunicationPerformanceEntity performanceEntity) {
        OptimizationResult result = new OptimizationResult();
        
        // 1. 计算当前链路质量
        double currentQuality = calculateLinkQuality(performanceEntity);
        result.setCurrentQualityScore(currentQuality);
        
        // 2. 检测性能问题
        List<PerformanceIssue> issues = detectPerformanceIssues(performanceEntity);
        result.setPerformanceIssues(issues);
        
        // 3. 生成优化建议
        List<OptimizationSuggestion> suggestions = generateOptimizationSuggestions(performanceEntity, issues);
        result.setSuggestions(suggestions);
        
        // 4. 推荐最佳协议
        String recommendedProtocol = recommendOptimalProtocol(performanceEntity);
        result.setRecommendedProtocol(recommendedProtocol);
        
        // 5. 评估优化潜力
        double optimizationPotential = calculateOptimizationPotential(performanceEntity, recommendedProtocol);
        result.setOptimizationPotential(optimizationPotential);
        
        // 6. 生成混合加密策略
        HybridEncryptionStrategy hybridStrategy = generateHybridEncryptionStrategy(performanceEntity);
        result.setHybridEncryptionStrategy(hybridStrategy);
        
        // 7. 设置优化优先级
        result.setPriority(calculateOptimizationPriority(performanceEntity, issues, optimizationPotential));
        
        return result;
    }

    /**
     * 计算链路质量评分
     * 
     * @param entity 性能实体
     * @return 质量评分 (0-100)
     */
    private double calculateLinkQuality(QuantumCommunicationPerformanceEntity entity) {
        double qualityScore = entity.getQualityScore();
        if (qualityScore == null) {
            qualityScore = entity.calculateQualityScore();
        }
        
        // 考虑环境因素调整
        double environmentFactor = calculateEnvironmentFactor(entity);
        
        // 考虑历史性能趋势
        double trendFactor = calculateTrendFactor(entity);
        
        // 综合质量评分
        double adjustedScore = qualityScore * environmentFactor * trendFactor;
        
        return Math.min(100.0, Math.max(0.0, adjustedScore));
    }

    /**
     * 检测性能问题
     * 
     * @param entity 性能实体
     * @return 性能问题列表
     */
    private List<PerformanceIssue> detectPerformanceIssues(QuantumCommunicationPerformanceEntity entity) {
        List<PerformanceIssue> issues = new ArrayList<>();
        
        // 1. 检查QBER阈值
        if (entity.getQber() > 0.05) {
            issues.add(new PerformanceIssue(
                "HIGH_QBER",
                "量子误码率过高",
                "当前QBER: " + entity.getQber() + "，建议阈值: 0.05",
                "CRITICAL",
                "降低环境噪声或切换协议"
            ));
        }
        
        // 2. 检查密钥速率
        if (entity.getKeyRate() < 1000.0) {
            issues.add(new PerformanceIssue(
                "LOW_KEY_RATE",
                "密钥生成速率过低",
                "当前速率: " + entity.getKeyRate() + " bits/sec，建议阈值: 1000 bits/sec",
                "HIGH",
                "优化信道或使用更高效率协议"
            ));
        }
        
        // 3. 检查链路延迟
        if (entity.getLatency() > 50.0) {
            issues.add(new PerformanceIssue(
                "HIGH_LATENCY",
                "链路延迟过高",
                "当前延迟: " + entity.getLatency() + " ms，建议阈值: 50 ms",
                "HIGH",
                "优化网络路径或使用低延迟协议"
            ));
        }
        
        // 4. 检查可用性
        if (entity.getAvailability() < 99.0) {
            issues.add(new PerformanceIssue(
                "LOW_AVAILABILITY",
                "链路可用性不足",
                "当前可用性: " + entity.getAvailability() + "%，建议阈值: 99%",
                "MEDIUM",
                "检查硬件状态或增加冗余链路"
            ));
        }
        
        // 5. 检查抖动
        if (entity.getJitter() > 10.0) {
            issues.add(new PerformanceIssue(
                "HIGH_JITTER",
                "链路抖动过大",
                "当前抖动: " + entity.getJitter() + " ms，建议阈值: 10 ms",
                "MEDIUM",
                "优化缓存或使用抗抖动算法"
            ));
        }
        
        // 6. 检查环境条件
        if (entity.getChannelLoss() != null && entity.getChannelLoss() > 20.0) {
            issues.add(new PerformanceIssue(
                "HIGH_CHANNEL_LOSS",
                "信道损耗过高",
                "当前损耗: " + entity.getChannelLoss() + " dB，建议阈值: 20 dB",
                "HIGH",
                "检查光纤连接或调整发射功率"
            ));
        }
        
        return issues;
    }

    /**
     * 生成优化建议
     * 
     * @param entity 性能实体
     * @param issues 检测到的问题
     * @return 优化建议列表
     */
    private List<OptimizationSuggestion> generateOptimizationSuggestions(
            QuantumCommunicationPerformanceEntity entity, 
            List<PerformanceIssue> issues) {
        
        List<OptimizationSuggestion> suggestions = new ArrayList<>();
        
        // 基于问题生成具体建议
        for (PerformanceIssue issue : issues) {
            switch (issue.getCode()) {
                case "HIGH_QBER":
                    suggestions.add(new OptimizationSuggestion(
                        "PROTOCOL_SWITCH",
                        "切换至低QBER协议",
                        "建议从 " + entity.getProtocolType() + " 切换至 MDI-QKD 或 TF-QKD",
                        "QBER可降低30-50%",
                        0.8,
                        "immediate"
                    ));
                    break;
                    
                case "LOW_KEY_RATE":
                    suggestions.add(new OptimizationSuggestion(
                        "PARAMETER_TUNING",
                        "优化发射功率和检测效率",
                        "调整发射功率至最佳点，优化单光子检测器效率",
                        "密钥速率可提升20-40%",
                        0.6,
                        "short_term"
                    ));
                    break;
                    
                case "HIGH_LATENCY":
                    suggestions.add(new OptimizationSuggestion(
                        "NETWORK_OPTIMIZATION",
                        "优化网络路径和缓存",
                        "减少中继节点数量，启用数据压缩和缓存预取",
                        "延迟可降低30-60%",
                        0.7,
                        "medium_term"
                    ));
                    break;
                    
                case "LOW_AVAILABILITY":
                    suggestions.add(new OptimizationSuggestion(
                        "REDUNDANCY_ADDITION",
                        "增加链路冗余",
                        "添加备用量子信道，实现主备自动切换",
                        "可用性可提升至99.9%",
                        0.9,
                        "long_term"
                    ));
                    break;
            }
        }
        
        // 通用优化建议
        suggestions.add(new OptimizationSuggestion(
            "HYBRID_ENCRYPTION",
            "启用混合加密策略",
            "在QKD性能不足时自动切换至后量子加密算法",
            "确保通信的连续性和安全性",
            0.5,
            "immediate"
        ));
        
        suggestions.add(new OptimizationSuggestion(
            "AI_OPTIMIZATION",
            "启用AI驱动的参数调优",
            "基于历史性能数据训练AI模型，实时优化系统参数",
            "整体性能可提升15-25%",
            0.4,
            "long_term"
        ));
        
        return suggestions;
    }

    /**
     * 推荐最优协议
     * 
     * @param entity 性能实体
     * @return 推荐协议名称
     */
    private String recommendOptimalProtocol(QuantumCommunicationPerformanceEntity entity) {
        String currentProtocol = entity.getProtocolType();
        double currentQuality = entity.getQualityScore();
        
        // 如果当前协议质量良好，保持当前协议
        if (currentQuality != null && currentQuality >= 80.0) {
            return currentProtocol;
        }
        
        // 根据权重偏好选择最优协议
        Map<String, Double> protocolScores = new HashMap<>();
        
        for (Map.Entry<String, ProtocolCharacteristics> entry : PROTOCOL_CHARACTERISTICS.entrySet()) {
            String protocol = entry.getKey();
            ProtocolCharacteristics chars = entry.getValue();
            
            // 计算协议综合评分
            double score = calculateProtocolScore(chars, entity);
            protocolScores.put(protocol, score);
        }
        
        // 选择评分最高的协议
        return protocolScores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(currentProtocol);
    }

    /**
     * 计算协议评分
     * 
     * @param chars 协议特征
     * @param entity 性能实体
     * @return 协议评分
     */
    private double calculateProtocolScore(ProtocolCharacteristics chars, QuantumCommunicationPerformanceEntity entity) {
        double latencyScore = 100 - chars.avgLatency;
        double qberScore = 100 - (chars.avgQber * 1000);
        double keyRateScore = chars.avgKeyRate / 10.0;
        double securityScore = chars.avgSecurity;
        
        // 应用权重
        double weightedScore = 
            latencyScore * entity.getWeightLatency() +
            qberScore * entity.getWeightBandwidth() +
            keyRateScore * entity.getWeightEnergy() +
            securityScore * entity.getWeightSecurity();
        
        return weightedScore;
    }

    /**
     * 计算优化潜力
     * 
     * @param entity 性能实体
     * @param recommendedProtocol 推荐协议
     * @return 优化潜力 (0-1)
     */
    private double calculateOptimizationPotential(QuantumCommunicationPerformanceEntity entity, String recommendedProtocol) {
        double currentScore = entity.getQualityScore();
        if (currentScore == null) {
            currentScore = 50.0; // 默认值
        }
        
        ProtocolCharacteristics recommendedChars = PROTOCOL_CHARACTERISTICS.get(recommendedProtocol);
        if (recommendedChars == null) {
            return 0.0;
        }
        
        double estimatedScore = calculateProtocolScore(recommendedChars, entity);
        
        return Math.min(1.0, Math.max(0.0, (estimatedScore - currentScore) / 100.0));
    }

    /**
     * 生成混合加密策略
     * 
     * @param entity 性能实体
     * @return 混合加密策略
     */
    private HybridEncryptionStrategy generateHybridEncryptionStrategy(QuantumCommunicationPerformanceEntity entity) {
        HybridEncryptionStrategy strategy = new HybridEncryptionStrategy();
        
        // 根据性能评分决定策略
        double qualityScore = entity.getQualityScore();
        if (qualityScore == null) {
            qualityScore = 50.0;
        }
        
        if (qualityScore >= 80.0) {
            strategy.setPrimaryMethod("QKD_ONLY");
            strategy.setFallbackMethod("PQC_BACKUP");
            strategy.setSwitchThreshold(60.0);
            strategy.setAutoFallback(true);
        } else if (qualityScore >= 60.0) {
            strategy.setPrimaryMethod("HYBRID_QKD_PQC");
            strategy.setFallbackMethod("PQC_ONLY");
            strategy.setSwitchThreshold(40.0);
            strategy.setAutoFallback(true);
        } else {
            strategy.setPrimaryMethod("PQC_ONLY");
            strategy.setFallbackMethod("CLASSICAL_AES");
            strategy.setSwitchThreshold(20.0);
            strategy.setAutoFallback(true);
        }
        
        strategy.setQkdProtocol(entity.getProtocolType());
        strategy.setPqcAlgorithm("CRYSTALS-Kyber");
        strategy.setClassicalAlgorithm("AES-256-GCM");
        strategy.setGeneratedAt(LocalDateTime.now());
        
        return strategy;
    }

    /**
     * 计算优化优先级
     * 
     * @param entity 性能实体
     * @param issues 性能问题
     * @param optimizationPotential 优化潜力
     * @return 优先级 (CRITICAL, HIGH, MEDIUM, LOW)
     */
    private String calculateOptimizationPriority(
            QuantumCommunicationPerformanceEntity entity, 
            List<PerformanceIssue> issues,
            double optimizationPotential) {
        
        // 检查是否有CRITICAL级别的问题
        boolean hasCriticalIssue = issues.stream()
                .anyMatch(issue -> "CRITICAL".equals(issue.getSeverity()));
        
        if (hasCriticalIssue) {
            return "CRITICAL";
        }
        
        // 检查质量评分
        if (entity.getQualityScore() != null && entity.getQualityScore() < 40.0) {
            return "HIGH";
        }
        
        // 检查优化潜力
        if (optimizationPotential > 0.3) {
            return "HIGH";
        }
        
        // 检查是否有HIGH级别的问题
        boolean hasHighIssue = issues.stream()
                .anyMatch(issue -> "HIGH".equals(issue.getSeverity()));
        
        if (hasHighIssue) {
            return "MEDIUM";
        }
        
        return "LOW";
    }

    /**
     * 计算环境因素影响
     * 
     * @param entity 性能实体
     * @return 环境因素系数 (0.8-1.2)
     */
    private double calculateEnvironmentFactor(QuantumCommunicationPerformanceEntity entity) {
        double factor = 1.0;
        
        // 温度影响
        if (entity.getTemperature() != null) {
            if (entity.getTemperature() < 10.0 || entity.getTemperature() > 30.0) {
                factor *= 0.9;
            }
        }
        
        // 湿度影响
        if (entity.getHumidity() != null && entity.getHumidity() > 80.0) {
            factor *= 0.95;
        }
        
        // 信道长度影响
        if (entity.getChannelLength() != null && entity.getChannelLength() > 50.0) {
            factor *= (50.0 / entity.getChannelLength());
        }
        
        return Math.max(0.8, Math.min(1.2, factor));
    }

    /**
     * 计算趋势因素
     * 
     * @param entity 性能实体
     * @return 趋势因素系数 (0.9-1.1)
     */
    private double calculateTrendFactor(QuantumCommunicationPerformanceEntity entity) {
        if ("UP".equals(entity.getPerformanceTrend())) {
            return 1.05; // 趋势向上，给予正向调整
        } else if ("DOWN".equals(entity.getPerformanceTrend())) {
            return 0.95; // 趋势向下，给予负向调整
        } else {
            return 1.0; // 趋势稳定
        }
    }

    // 内部辅助类
    
    private static class ProtocolCharacteristics {
        double avgKeyRate;      // 平均密钥速率 (kbps)
        double avgQber;         // 平均误码率
        double avgLatency;      // 平均延迟 (ms)
        double avgSecurity;     // 平均安全评分
        
        ProtocolCharacteristics(double keyRate, double qber, double latency, double security) {
            this.avgKeyRate = keyRate;
            this.avgQber = qber;
            this.avgLatency = latency;
            this.avgSecurity = security;
        }
    }
    
    public static class OptimizationResult {
        private double currentQualityScore;
        private List<PerformanceIssue> performanceIssues;
        private List<OptimizationSuggestion> suggestions;
        private String recommendedProtocol;
        private double optimizationPotential;
        private HybridEncryptionStrategy hybridEncryptionStrategy;
        private String priority;
        
        // Getters and Setters
        public double getCurrentQualityScore() { return currentQualityScore; }
        public void setCurrentQualityScore(double currentQualityScore) { this.currentQualityScore = currentQualityScore; }
        
        public List<PerformanceIssue> getPerformanceIssues() { return performanceIssues; }
        public void setPerformanceIssues(List<PerformanceIssue> performanceIssues) { this.performanceIssues = performanceIssues; }
        
        public List<OptimizationSuggestion> getSuggestions() { return suggestions; }
        public void setSuggestions(List<OptimizationSuggestion> suggestions) { this.suggestions = suggestions; }
        
        public String getRecommendedProtocol() { return recommendedProtocol; }
        public void setRecommendedProtocol(String recommendedProtocol) { this.recommendedProtocol = recommendedProtocol; }
        
        public double getOptimizationPotential() { return optimizationPotential; }
        public void setOptimizationPotential(double optimizationPotential) { this.optimizationPotential = optimizationPotential; }
        
        public HybridEncryptionStrategy getHybridEncryptionStrategy() { return hybridEncryptionStrategy; }
        public void setHybridEncryptionStrategy(HybridEncryptionStrategy hybridEncryptionStrategy) { this.hybridEncryptionStrategy = hybridEncryptionStrategy; }
        
        public String getPriority() { return priority; }
        public void setPriority(String priority) { this.priority = priority; }
    }
    
    public static class PerformanceIssue {
        private String code;
        private String title;
        private String description;
        private String severity; // CRITICAL, HIGH, MEDIUM, LOW
        private String suggestedAction;
        
        public PerformanceIssue(String code, String title, String description, String severity, String suggestedAction) {
            this.code = code;
            this.title = title;
            this.description = description;
            this.severity = severity;
            this.suggestedAction = suggestedAction;
        }
        
        // Getters
        public String getCode() { return code; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getSeverity() { return severity; }
        public String getSuggestedAction() { return suggestedAction; }
    }
    
    public static class OptimizationSuggestion {
        private String type;
        private String title;
        private String description;
        private String expectedBenefit;
        private double confidence; // 0-1
        private String timeframe; // immediate, short_term, medium_term, long_term
        
        public OptimizationSuggestion(String type, String title, String description, String expectedBenefit, double confidence, String timeframe) {
            this.type = type;
            this.title = title;
            this.description = description;
            this.expectedBenefit = expectedBenefit;
            this.confidence = confidence;
            this.timeframe = timeframe;
        }
        
        // Getters
        public String getType() { return type; }
        public String getTitle() { return title; }
        public String getDescription() { return description; }
        public String getExpectedBenefit() { return expectedBenefit; }
        public double getConfidence() { return confidence; }
        public String getTimeframe() { return timeframe; }
    }
    
    public static class HybridEncryptionStrategy {
        private String primaryMethod; // QKD_ONLY, HYBRID_QKD_PQC, PQC_ONLY
        private String fallbackMethod;
        private double switchThreshold; // 切换阈值 (质量评分)
        private boolean autoFallback;
        private String qkdProtocol;
        private String pqcAlgorithm;
        private String classicalAlgorithm;
        private LocalDateTime generatedAt;
        
        // Getters and Setters
        public String getPrimaryMethod() { return primaryMethod; }
        public void setPrimaryMethod(String primaryMethod) { this.primaryMethod = primaryMethod; }
        
        public String getFallbackMethod() { return fallbackMethod; }
        public void setFallbackMethod(String fallbackMethod) { this.fallbackMethod = fallbackMethod; }
        
        public double getSwitchThreshold() { return switchThreshold; }
        public void setSwitchThreshold(double switchThreshold) { this.switchThreshold = switchThreshold; }
        
        public boolean isAutoFallback() { return autoFallback; }
        public void setAutoFallback(boolean autoFallback) { this.autoFallback = autoFallback; }
        
        public String getQkdProtocol() { return qkdProtocol; }
        public void setQkdProtocol(String qkdProtocol) { this.qkdProtocol = qkdProtocol; }
        
        public String getPqcAlgorithm() { return pqcAlgorithm; }
        public void setPqcAlgorithm(String pqcAlgorithm) { this.pqcAlgorithm = pqcAlgorithm; }
        
        public String getClassicalAlgorithm() { return classicalAlgorithm; }
        public void setClassicalAlgorithm(String classicalAlgorithm) { this.classicalAlgorithm = classicalAlgorithm; }
        
        public LocalDateTime getGeneratedAt() { return generatedAt; }
        public void setGeneratedAt(LocalDateTime generatedAt) { this.generatedAt = generatedAt; }
    }
}