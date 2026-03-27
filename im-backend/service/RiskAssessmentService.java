package com.im.backend.service;

import com.im.backend.entity.DeviceTrustStateEntity;
import com.im.backend.entity.ZeroTrustAccessRequestEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 风险评估服务
 * 实现UEBA用户行为分析和实时风险评分
 */
@Service
public class RiskAssessmentService {

    private static final Logger logger = LoggerFactory.getLogger(RiskAssessmentService.class);

    // 用户行为基线数据
    private final Map<Long, UserBehaviorProfile> userProfiles = new ConcurrentHashMap<>();

    // 异常行为计数器
    private final Map<String, AtomicInteger> anomalyCounters = new ConcurrentHashMap<>();

    // 登录失败记录
    private final Map<String, LoginFailureRecord> loginFailures = new ConcurrentHashMap<>();

    /**
     * 执行综合风险评估
     */
    public RiskAssessment assessRisk(ZeroTrustGatewayService.AccessContext context,
                                      DeviceTrustStateEntity deviceState) {
        RiskAssessment assessment = new RiskAssessment();

        // 1. 设备风险评分
        int deviceScore = assessDeviceRisk(deviceState);
        assessment.setDeviceScore(deviceScore);

        // 2. 用户行为评分
        int behaviorScore = assessUserBehavior(context);
        assessment.setBehaviorScore(behaviorScore);

        // 3. 网络风险评分
        int networkScore = assessNetworkRisk(context);
        assessment.setNetworkScore(networkScore);

        // 4. 上下文风险评分
        int contextScore = assessContextRisk(context);
        assessment.setContextScore(contextScore);

        // 5. 计算综合风险评分
        int overallScore = calculateOverallRisk(assessment);
        assessment.setOverallScore(overallScore);
        assessment.setRiskLevel(determineRiskLevel(overallScore));

        // 6. 识别威胁指标
        Map<String, String> threatIndicators = identifyThreatIndicators(context, deviceState, assessment);
        assessment.setThreatIndicators(threatIndicators);

        logger.debug("Risk assessment for user {}: overall score={}, level={}",
            context.getUserId(), overallScore, assessment.getRiskLevel());

        return assessment;
    }

    /**
     * 评估设备风险
     */
    private int assessDeviceRisk(DeviceTrustStateEntity device) {
        if (device == null) {
            return 100;  // 未知设备 = 最高风险
        }

        int riskScore = 0;

        // 基于信任分数计算风险
        int trustScore = device.getTrustScore() != null ? device.getTrustScore() : 0;
        riskScore += (100 - trustScore) * 0.4;

        // 设备越狱/Root检测
        if (device.getIsJailbroken() != null && device.getIsJailbroken()) {
            riskScore += 30;
        }

        // 未合规设备
        if (device.getIsCompliant() != null && !device.getIsCompliant()) {
            riskScore += 20;
        }

        // 存在漏洞
        if (device.getVulnerabilities() != null && !device.getVulnerabilities().isEmpty()) {
            riskScore += Math.min(25, device.getVulnerabilities().size() * 5);
        }

        // 长时间未更新
        if (device.getLastPatchDate() != null) {
            long daysSincePatch = ChronoUnit.DAYS.between(device.getLastPatchDate(), LocalDateTime.now());
            if (daysSincePatch > 90) {
                riskScore += 15;
            } else if (daysSincePatch > 30) {
                riskScore += 5;
            }
        }

        // 连续评估失败
        if (device.getConsecutiveFailures() != null && device.getConsecutiveFailures() > 0) {
            riskScore += device.getConsecutiveFailures() * 5;
        }

        return Math.min(100, riskScore);
    }

    /**
     * 评估用户行为风险
     */
    private int assessUserBehavior(ZeroTrustGatewayService.AccessContext context) {
        Long userId = context.getUserId();
        UserBehaviorProfile profile = userProfiles.computeIfAbsent(userId, 
            k -> new UserBehaviorProfile(userId));

        int riskScore = 50;  // 基础分数

        // 检查异常登录时间
        int currentHour = LocalDateTime.now().getHour();
        if (!profile.isNormalLoginHour(currentHour)) {
            riskScore += 15;
        }

        // 检查地理位置异常
        String location = context.getLocation();
        if (location != null && !profile.isKnownLocation(location)) {
            riskScore += 20;
        }

        // 检查设备变化
        String deviceId = context.getDeviceId();
        if (!profile.isKnownDevice(deviceId)) {
            riskScore += 10;
        }

        // 检查访问频率异常
        String accessKey = userId + ":" + context.getResourceId();
        AtomicInteger accessCount = anomalyCounters.computeIfAbsent(accessKey, k -> new AtomicInteger(0));
        int count = accessCount.incrementAndGet();
        if (count > profile.getNormalAccessRate() * 2) {
            riskScore += 15;
        }

        // 更新用户行为档案
        profile.recordAccess(context);

        return Math.min(100, riskScore);
    }

    /**
     * 评估网络风险
     */
    private int assessNetworkRisk(ZeroTrustGatewayService.AccessContext context) {
        int riskScore = 0;
        String ipAddress = context.getIpAddress();

        if (ipAddress == null) {
            return 50;  // 未知IP
        }

        // 检查是否为已知的恶意IP
        if (isKnownMaliciousIP(ipAddress)) {
            riskScore += 50;
        }

        // 检查IP地理位置异常
        if (isGeoAnomaly(context)) {
            riskScore += 25;
        }

        // 检查是否使用VPN/代理
        if (isVPNOrProxy(ipAddress)) {
            riskScore += 15;
        }

        // 检查是否来自高风险国家/地区
        if (isHighRiskRegion(ipAddress)) {
            riskScore += 20;
        }

        // 检查TOR出口节点
        if (isTorExitNode(ipAddress)) {
            riskScore += 30;
        }

        return Math.min(100, riskScore);
    }

    /**
     * 评估上下文风险
     */
    private int assessContextRisk(ZeroTrustGatewayService.AccessContext context) {
        int riskScore = 0;

        // 检查敏感操作
        ZeroTrustAccessRequestEntity.AccessAction action = context.getAction();
        if (action == ZeroTrustAccessRequestEntity.AccessAction.DELETE ||
            action == ZeroTrustAccessRequestEntity.AccessAction.ADMIN) {
            riskScore += 15;
        }

        // 检查敏感资源访问
        ZeroTrustAccessRequestEntity.ResourceType resourceType = context.getResourceType();
        if (resourceType == ZeroTrustAccessRequestEntity.ResourceType.ADMIN_PANEL ||
            resourceType == ZeroTrustAccessRequestEntity.ResourceType.CONFIGURATION) {
            riskScore += 20;
        }

        // 检查并发会话异常
        String sessionId = context.getSessionId();
        if (sessionId != null && isConcurrentSessionAnomaly(context.getUserId(), sessionId)) {
            riskScore += 20;
        }

        return Math.min(100, riskScore);
    }

    /**
     * 计算综合风险评分
     */
    private int calculateOverallRisk(RiskAssessment assessment) {
        // 加权计算
        double weightedScore = 
            assessment.getDeviceScore() * 0.30 +
            assessment.getBehaviorScore() * 0.35 +
            assessment.getNetworkScore() * 0.25 +
            assessment.getContextScore() * 0.10;

        return (int) Math.min(100, weightedScore);
    }

    /**
     * 确定风险等级
     */
    private ZeroTrustAccessRequestEntity.RiskLevel determineRiskLevel(int score) {
        return ZeroTrustAccessRequestEntity.RiskLevel.fromScore(score);
    }

    /**
     * 识别威胁指标
     */
    private Map<String, String> identifyThreatIndicators(
            ZeroTrustGatewayService.AccessContext context,
            DeviceTrustStateEntity device,
            RiskAssessment assessment) {
        
        Map<String, String> indicators = new HashMap<>();

        if (assessment.getDeviceScore() > 70) {
            indicators.put("HIGH_DEVICE_RISK", "设备存在严重安全风险");
        }

        if (assessment.getBehaviorScore() > 70) {
            indicators.put("ANOMALOUS_BEHAVIOR", "检测到异常用户行为");
        }

        if (assessment.getNetworkScore() > 60) {
            indicators.put("SUSPICIOUS_NETWORK", "网络环境可疑");
        }

        if (device != null && device.getIsJailbroken() != null && device.getIsJailbroken()) {
            indicators.put("JAILBROKEN_DEVICE", "设备已越狱/Root");
        }

        if (isImpossibleTravel(context)) {
            indicators.put("IMPOSSIBLE_TRAVEL", "检测到不可能的地理位置跳转");
        }

        return indicators;
    }

    /**
     * 记录登录失败
     */
    public void recordLoginFailure(String identifier, String ipAddress) {
        LoginFailureRecord record = loginFailures.computeIfAbsent(identifier, 
            k -> new LoginFailureRecord());
        record.recordFailure(ipAddress);

        // 检查是否需要锁定
        if (record.getFailureCount() >= 5) {
            logger.warn("Multiple login failures detected for {} from {}", identifier, ipAddress);
        }
    }

    /**
     * 清除登录失败记录
     */
    public void clearLoginFailures(String identifier) {
        loginFailures.remove(identifier);
    }

    /**
     * 检查是否被锁定
     */
    public boolean isLocked(String identifier) {
        LoginFailureRecord record = loginFailures.get(identifier);
        return record != null && record.getFailureCount() >= 5 && 
               record.getLastFailureTime().isAfter(LocalDateTime.now().minusMinutes(30));
    }

    // ============ 辅助方法 ============

    private boolean isKnownMaliciousIP(String ipAddress) {
        // 简化实现：实际应查询威胁情报数据库
        return false;
    }

    private boolean isGeoAnomaly(ZeroTrustGatewayService.AccessContext context) {
        // 简化实现：实际应比较历史地理位置
        return false;
    }

    private boolean isVPNOrProxy(String ipAddress) {
        // 简化实现：实际应查询VPN/代理数据库
        return false;
    }

    private boolean isHighRiskRegion(String ipAddress) {
        // 简化实现
        return false;
    }

    private boolean isTorExitNode(String ipAddress) {
        // 简化实现
        return false;
    }

    private boolean isConcurrentSessionAnomaly(Long userId, String sessionId) {
        // 简化实现
        return false;
    }

    private boolean isImpossibleTravel(ZeroTrustGatewayService.AccessContext context) {
        // 简化实现：检测短时间内不可能的地理位置变化
        return false;
    }

    // ============ 内部类 ============

    public static class RiskAssessment {
        private int deviceScore;
        private int behaviorScore;
        private int networkScore;
        private int contextScore;
        private int overallScore;
        private ZeroTrustAccessRequestEntity.RiskLevel riskLevel;
        private Map<String, String> threatIndicators = new HashMap<>();

        public int getDeviceScore() { return deviceScore; }
        public void setDeviceScore(int deviceScore) { this.deviceScore = deviceScore; }
        public int getBehaviorScore() { return behaviorScore; }
        public void setBehaviorScore(int behaviorScore) { this.behaviorScore = behaviorScore; }
        public int getNetworkScore() { return networkScore; }
        public void setNetworkScore(int networkScore) { this.networkScore = networkScore; }
        public int getContextScore() { return contextScore; }
        public void setContextScore(int contextScore) { this.contextScore = contextScore; }
        public int getOverallScore() { return overallScore; }
        public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
        public ZeroTrustAccessRequestEntity.RiskLevel getRiskLevel() { return riskLevel; }
        public void setRiskLevel(ZeroTrustAccessRequestEntity.RiskLevel riskLevel) { this.riskLevel = riskLevel; }
        public Map<String, String> getThreatIndicators() { return threatIndicators; }
        public void setThreatIndicators(Map<String, String> threatIndicators) { this.threatIndicators = threatIndicators; }
    }

    public static class UserBehaviorProfile {
        private final Long userId;
        private final Map<String, Integer> loginHourDistribution = new HashMap<>();
        private final Map<String, Boolean> knownLocations = new HashMap<>();
        private final Map<String, Boolean> knownDevices = new HashMap<>();
        private int normalAccessRate = 10;
        private LocalDateTime lastAccessTime;

        public UserBehaviorProfile(Long userId) {
            this.userId = userId;
            // 初始化正常登录时间（9:00-18:00）
            for (int i = 9; i <= 18; i++) {
                loginHourDistribution.put(String.valueOf(i), 10);
            }
        }

        public void recordAccess(ZeroTrustGatewayService.AccessContext context) {
            int hour = LocalDateTime.now().getHour();
            loginHourDistribution.merge(String.valueOf(hour), 1, Integer::sum);
            
            if (context.getLocation() != null) {
                knownLocations.put(context.getLocation(), true);
            }
            if (context.getDeviceId() != null) {
                knownDevices.put(context.getDeviceId(), true);
            }
            
            lastAccessTime = LocalDateTime.now();
        }

        public boolean isNormalLoginHour(int hour) {
            return loginHourDistribution.getOrDefault(String.valueOf(hour), 0) > 0;
        }

        public boolean isKnownLocation(String location) {
            return knownLocations.containsKey(location);
        }

        public boolean isKnownDevice(String deviceId) {
            return knownDevices.containsKey(deviceId);
        }

        public int getNormalAccessRate() {
            return normalAccessRate;
        }

        public Long getUserId() { return userId; }
        public LocalDateTime getLastAccessTime() { return lastAccessTime; }
    }

    public static class LoginFailureRecord {
        private final AtomicInteger failureCount = new AtomicInteger(0);
        private String lastIpAddress;
        private LocalDateTime lastFailureTime;

        public void recordFailure(String ipAddress) {
            failureCount.incrementAndGet();
            this.lastIpAddress = ipAddress;
            this.lastFailureTime = LocalDateTime.now();
        }

        public int getFailureCount() { return failureCount.get(); }
        public String getLastIpAddress() { return lastIpAddress; }
        public LocalDateTime getLastFailureTime() { return lastFailureTime; }
    }
}
