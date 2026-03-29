package com.im.backend.model;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * 风险评分实体类
 * 
 * 用于记录用户的风险评分信息，包括各种风险维度得分、综合风险等级、
 * 风险因子分析等，是零信任持续风险评估的核心数据模型。
 * 
 * @author ZeroTrust Team
 * @since 1.0.0
 */
public class RiskScore {
    
    /**
     * 风险等级枚举
     */
    public enum RiskLevel {
        /** 低风险 - 正常访问 */
        LOW(1, "低风险", "#4CAF50"),
        /** 中低风险 - 需关注 */
        LOW_MEDIUM(2, "中低风险", "#8BC34A"),
        /** 中风险 - 需验证 */
        MEDIUM(3, "中风险", "#FFC107"),
        /** 中高风险 - 需二次认证 */
        MEDIUM_HIGH(4, "中高风险", "#FF9800"),
        /** 高风险 - 需管理员审批 */
        HIGH(5, "高风险", "#FF5722"),
        /** 极高风险 - 自动阻断 */
        CRITICAL(6, "极高风险", "#F44336");
        
        private final int level;
        private final String displayName;
        private final String color;
        
        RiskLevel(int level, String displayName, String color) {
            this.level = level;
            this.displayName = displayName;
            this.color = color;
        }
        
        public int getLevel() { return level; }
        public String getDisplayName() { return displayName; }
        public String getColor() { return color; }
        
        /**
         * 根据分数获取风险等级
         */
        public static RiskLevel fromScore(double score) {
            if (score <= 20) return LOW;
            if (score <= 40) return LOW_MEDIUM;
            if (score <= 60) return MEDIUM;
            if (score <= 75) return MEDIUM_HIGH;
            if (score <= 90) return HIGH;
            return CRITICAL;
        }
    }
    
    /**
     * 风险因子类型枚举
     */
    public enum RiskFactorType {
        USER_BEHAVIOR("用户行为异常"),
        DEVICE_TRUST("设备信任度"),
        LOCATION_ANOMALY("位置异常"),
        TIME_ANOMALY("时间异常"),
        ACCESS_PATTERN("访问模式异常"),
        CREDENTIAL_RISK("凭据风险"),
        DATA_SENSITIVITY("数据敏感度"),
        NETWORK_RISK("网络环境风险"),
        VELOCITY_ANOMALY("速度异常"),
        THREAT_INTELLIGENCE("威胁情报匹配");
        
        private final String displayName;
        
        RiskFactorType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() { return displayName; }
    }
    
    private Long id;
    private String userId;
    private String deviceId;
    private String sessionId;
    private double compositeScore;
    private RiskLevel riskLevel;
    private Map<RiskFactorType, RiskFactor> riskFactors;
    private String assessmentId;
    private LocalDateTime assessedAt;
    private LocalDateTime expiresAt;
    private String riskPolicyId;
    private String triggeredRules;
    private boolean requiresReauthentication;
    private boolean requiresStepUpAuth;
    private String recommendedAction;
    private String threatIndicators;
    private String geolocation;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    /**
     * 风险因子内部类
     */
    public static class RiskFactor {
        private RiskFactorType type;
        private double score;
        private double weight;
        private String description;
        private String evidence;
        private RiskLevel level;
        private boolean triggered;
        private LocalDateTime detectedAt;
        
        public RiskFactor() {}
        
        public RiskFactor(RiskFactorType type, double score, double weight, 
                         String description, String evidence) {
            this.type = type;
            this.score = score;
            this.weight = weight;
            this.description = description;
            this.evidence = evidence;
            this.level = RiskLevel.fromScore(score);
            this.triggered = score >= 60;
            this.detectedAt = LocalDateTime.now();
        }
        
        public double getWeightedScore() {
            return score * weight;
        }
        
        // Getters and Setters
        public RiskFactorType getType() { return type; }
        public void setType(RiskFactorType type) { this.type = type; }
        public double getScore() { return score; }
        public void setScore(double score) { this.score = score; }
        public double getWeight() { return weight; }
        public void setWeight(double weight) { this.weight = weight; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getEvidence() { return evidence; }
        public void setEvidence(String evidence) { this.evidence = evidence; }
        public RiskLevel getLevel() { return level; }
        public void setLevel(RiskLevel level) { this.level = level; }
        public boolean isTriggered() { return triggered; }
        public void setTriggered(boolean triggered) { this.triggered = triggered; }
        public LocalDateTime getDetectedAt() { return detectedAt; }
        public void setDetectedAt(LocalDateTime detectedAt) { this.detectedAt = detectedAt; }
    }
    
    public RiskScore() {
        this.riskFactors = new HashMap<>();
        this.compositeScore = 0.0;
        this.riskLevel = RiskLevel.LOW;
        this.assessedAt = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 添加风险因子
     */
    public void addRiskFactor(RiskFactor factor) {
        riskFactors.put(factor.getType(), factor);
        recalculateCompositeScore();
    }
    
    /**
     * 重新计算综合风险分数
     */
    public void recalculateCompositeScore() {
        if (riskFactors.isEmpty()) {
            this.compositeScore = 0.0;
            this.riskLevel = RiskLevel.LOW;
            return;
        }
        
        double totalWeightedScore = 0.0;
        double totalWeight = 0.0;
        
        for (RiskFactor factor : riskFactors.values()) {
            totalWeightedScore += factor.getWeightedScore();
            totalWeight += factor.getWeight();
        }
        
        if (totalWeight > 0) {
            this.compositeScore = totalWeightedScore / totalWeight;
        }
        
        this.riskLevel = RiskLevel.fromScore(this.compositeScore);
        this.updatedAt = LocalDateTime.now();
        
        // 根据风险等级设置建议操作
        updateRecommendedAction();
    }
    
    /**
     * 更新建议操作
     */
    private void updateRecommendedAction() {
        switch (riskLevel) {
            case LOW:
                this.requiresReauthentication = false;
                this.requiresStepUpAuth = false;
                this.recommendedAction = "允许访问";
                break;
            case LOW_MEDIUM:
                this.requiresReauthentication = false;
                this.requiresStepUpAuth = false;
                this.recommendedAction = "允许访问，记录日志";
                break;
            case MEDIUM:
                this.requiresReauthentication = true;
                this.requiresStepUpAuth = false;
                this.recommendedAction = "要求重新认证";
                break;
            case MEDIUM_HIGH:
                this.requiresReauthentication = true;
                this.requiresStepUpAuth = true;
                this.recommendedAction = "要求二次认证(MFA)";
                break;
            case HIGH:
                this.requiresReauthentication = true;
                this.requiresStepUpAuth = true;
                this.recommendedAction = "要求管理员审批";
                break;
            case CRITICAL:
                this.requiresReauthentication = true;
                this.requiresStepUpAuth = true;
                this.recommendedAction = "立即阻断访问，通知安全团队";
                break;
        }
    }
    
    /**
     * 获取触发的风险因子列表
     */
    public Map<RiskFactorType, RiskFactor> getTriggeredFactors() {
        Map<RiskFactorType, RiskFactor> triggered = new HashMap<>();
        for (Map.Entry<RiskFactorType, RiskFactor> entry : riskFactors.entrySet()) {
            if (entry.getValue().isTriggered()) {
                triggered.put(entry.getKey(), entry.getValue());
            }
        }
        return triggered;
    }
    
    /**
     * 检查是否有高风险因子
     */
    public boolean hasHighRiskFactor() {
        for (RiskFactor factor : riskFactors.values()) {
            if (factor.getLevel().ordinal() >= RiskLevel.HIGH.ordinal()) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * 检查是否已过期
     */
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    /**
     * 获取风险因子数量
     */
    public int getFactorCount() {
        return riskFactors.size();
    }
    
    /**
     * 获取触发的风险因子数量
     */
    public int getTriggeredFactorCount() {
        return getTriggeredFactors().size();
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    public double getCompositeScore() { return compositeScore; }
    public void setCompositeScore(double compositeScore) { 
        this.compositeScore = compositeScore;
        this.riskLevel = RiskLevel.fromScore(compositeScore);
    }
    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }
    public Map<RiskFactorType, RiskFactor> getRiskFactors() { return riskFactors; }
    public void setRiskFactors(Map<RiskFactorType, RiskFactor> riskFactors) { 
        this.riskFactors = riskFactors;
        recalculateCompositeScore();
    }
    public String getAssessmentId() { return assessmentId; }
    public void setAssessmentId(String assessmentId) { this.assessmentId = assessmentId; }
    public LocalDateTime getAssessedAt() { return assessedAt; }
    public void setAssessedAt(LocalDateTime assessedAt) { this.assessedAt = assessedAt; }
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    public String getRiskPolicyId() { return riskPolicyId; }
    public void setRiskPolicyId(String riskPolicyId) { this.riskPolicyId = riskPolicyId; }
    public String getTriggeredRules() { return triggeredRules; }
    public void setTriggeredRules(String triggeredRules) { this.triggeredRules = triggeredRules; }
    public boolean isRequiresReauthentication() { return requiresReauthentication; }
    public void setRequiresReauthentication(boolean requiresReauthentication) { 
        this.requiresReauthentication = requiresReauthentication; 
    }
    public boolean isRequiresStepUpAuth() { return requiresStepUpAuth; }
    public void setRequiresStepUpAuth(boolean requiresStepUpAuth) { 
        this.requiresStepUpAuth = requiresStepUpAuth; 
    }
    public String getRecommendedAction() { return recommendedAction; }
    public void setRecommendedAction(String recommendedAction) { this.recommendedAction = recommendedAction; }
    public String getThreatIndicators() { return threatIndicators; }
    public void setThreatIndicators(String threatIndicators) { this.threatIndicators = threatIndicators; }
    public String getGeolocation() { return geolocation; }
    public void setGeolocation(String geolocation) { this.geolocation = geolocation; }
    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    @Override
    public String toString() {
        return String.format("RiskScore{id=%d, userId='%s', score=%.2f, level=%s, factors=%d}",
                id, userId, compositeScore, riskLevel, riskFactors.size());
    }
}
