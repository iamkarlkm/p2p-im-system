package com.im.backend.service;

import com.im.backend.model.RiskScore;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态访问控制决策引擎服务
 * 
 * 基于实时风险评估结果，动态调整用户的访问权限，
 * 实现细粒度的自适应访问控制。核心功能包括：
 * - 实时访问决策
 * - 动态权限调整
 * - 会话风险管理
 * - 访问策略执行
 * 
 * @author ZeroTrust Team
 * @since 1.0.0
 */
@Service
public class DynamicAccessControlService {
    
    @Autowired
    private RiskAssessmentService riskAssessmentService;
    
    @Autowired
    private PolicyDecisionPointService pdpService;
    
    @Autowired
    private SessionRiskManager sessionRiskManager;
    
    // 会话状态缓存 - sessionId -> 会话风险状态
    private final ConcurrentHashMap<String, SessionRiskState> sessionStates = new ConcurrentHashMap<>();
    
    // 访问决策缓存 - 减少重复评估
    private final ConcurrentHashMap<String, AccessDecision> decisionCache = new ConcurrentHashMap<>();
    
    // 默认风险阈值配置
    private static final double ALLOW_THRESHOLD = 30.0;
    private static final double CHALLENGE_THRESHOLD = 60.0;
    private static final double DENY_THRESHOLD = 85.0;
    
    /**
     * 访问决策结果枚举
     */
    public enum AccessDecision {
        /** 允许访问 */
        ALLOW(true, "允许访问"),
        /** 允许访问，但需记录 */
        ALLOW_WITH_LOGGING(true, "允许访问（需记录）"),
        /** 需要重新认证 */
        REQUIRE_REAUTH(false, "需要重新认证"),
        /** 需要二次认证(MFA) */
        REQUIRE_STEP_UP(false, "需要二次认证"),
        /** 需要管理员审批 */
        REQUIRE_APPROVAL(false, "需要管理员审批"),
        /** 拒绝访问 */
        DENY(false, "拒绝访问"),
        /** 临时阻断（冷却期） */
        TEMPORARY_BLOCK(false, "临时阻断访问"),
        /** 需要安全检查 */
        SECURITY_CHECK(false, "需要安全检查");
        
        private final boolean allowed;
        private final String description;
        
        AccessDecision(boolean allowed, String description) {
            this.allowed = allowed;
            this.description = description;
        }
        
        public boolean isAllowed() { return allowed; }
        public String getDescription() { return description; }
    }
    
    /**
     * 访问决策请求
     */
    public static class AccessRequest {
        private String userId;
        private String sessionId;
        private String deviceId;
        private String resource;
        private String action;
        private String ipAddress;
        private String userAgent;
        private Map<String, Object> attributes;
        private LocalDateTime requestTime;
        
        public AccessRequest() {
            this.requestTime = LocalDateTime.now();
            this.attributes = new HashMap<>();
        }
        
        // Builder pattern
        public static AccessRequest builder() {
            return new AccessRequest();
        }
        
        public AccessRequest userId(String userId) {
            this.userId = userId;
            return this;
        }
        
        public AccessRequest sessionId(String sessionId) {
            this.sessionId = sessionId;
            return this;
        }
        
        public AccessRequest deviceId(String deviceId) {
            this.deviceId = deviceId;
            return this;
        }
        
        public AccessRequest resource(String resource) {
            this.resource = resource;
            return this;
        }
        
        public AccessRequest action(String action) {
            this.action = action;
            return this;
        }
        
        public AccessRequest ipAddress(String ipAddress) {
            this.ipAddress = ipAddress;
            return this;
        }
        
        public AccessRequest userAgent(String userAgent) {
            this.userAgent = userAgent;
            return this;
        }
        
        public AccessRequest build() {
            return this;
        }
        
        // Getters
        public String getUserId() { return userId; }
        public String getSessionId() { return sessionId; }
        public String getDeviceId() { return deviceId; }
        public String getResource() { return resource; }
        public String getAction() { return action; }
        public String getIpAddress() { return ipAddress; }
        public String getUserAgent() { return userAgent; }
        public Map<String, Object> getAttributes() { return attributes; }
        public LocalDateTime getRequestTime() { return requestTime; }
    }
    
    /**
     * 访问决策响应
     */
    public static class AccessDecisionResult {
        private AccessDecision decision;
        private RiskScore riskScore;
        private String reason;
        private List<String> obligations;
        private List<String> advice;
        private LocalDateTime decisionTime;
        private long processingTimeMs;
        private String policyId;
        private Map<String, Object> additionalInfo;
        
        public AccessDecisionResult() {
            this.decisionTime = LocalDateTime.now();
            this.obligations = new ArrayList<>();
            this.advice = new ArrayList<>();
            this.additionalInfo = new HashMap<>();
        }
        
        // Getters and Setters
        public AccessDecision getDecision() { return decision; }
        public void setDecision(AccessDecision decision) { this.decision = decision; }
        public RiskScore getRiskScore() { return riskScore; }
        public void setRiskScore(RiskScore riskScore) { this.riskScore = riskScore; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public List<String> getObligations() { return obligations; }
        public void setObligations(List<String> obligations) { this.obligations = obligations; }
        public List<String> getAdvice() { return advice; }
        public void setAdvice(List<String> advice) { this.advice = advice; }
        public LocalDateTime getDecisionTime() { return decisionTime; }
        public void setDecisionTime(LocalDateTime decisionTime) { this.decisionTime = decisionTime; }
        public long getProcessingTimeMs() { return processingTimeMs; }
        public void setProcessingTimeMs(long processingTimeMs) { this.processingTimeMs = processingTimeMs; }
        public String getPolicyId() { return policyId; }
        public void setPolicyId(String policyId) { this.policyId = policyId; }
        public Map<String, Object> getAdditionalInfo() { return additionalInfo; }
        public void setAdditionalInfo(Map<String, Object> additionalInfo) { this.additionalInfo = additionalInfo; }
        
        public boolean isAllowed() {
            return decision != null && decision.isAllowed();
        }
        
        public void addObligation(String obligation) {
            this.obligations.add(obligation);
        }
        
        public void addAdvice(String advice) {
            this.advice.add(advice);
        }
    }
    
    /**
     * 会话风险状态
     */
    public static class SessionRiskState {
        private String sessionId;
        private String userId;
        private RiskScore currentRiskScore;
        private AccessDecision lastDecision;
        private List<RiskEvent> riskEvents;
        private LocalDateTime sessionStartTime;
        private LocalDateTime lastAccessTime;
        private int consecutiveDenials;
        private int stepUpAuthCount;
        private boolean isLocked;
        private LocalDateTime lockedUntil;
        
        public SessionRiskState(String sessionId, String userId) {
            this.sessionId = sessionId;
            this.userId = userId;
            this.riskEvents = new ArrayList<>();
            this.sessionStartTime = LocalDateTime.now();
            this.lastAccessTime = LocalDateTime.now();
        }
        
        public void recordEvent(RiskEvent event) {
            this.riskEvents.add(event);
            this.lastAccessTime = LocalDateTime.now();
        }
        
        public void incrementDenial() {
            this.consecutiveDenials++;
        }
        
        public void resetDenials() {
            this.consecutiveDenials = 0;
        }
        
        public void incrementStepUpCount() {
            this.stepUpAuthCount++;
        }
        
        public boolean isLocked() {
            if (lockedUntil == null) return false;
            return LocalDateTime.now().isBefore(lockedUntil);
        }
        
        public void lock(Duration duration) {
            this.isLocked = true;
            this.lockedUntil = LocalDateTime.now().plus(duration);
        }
        
        public void unlock() {
            this.isLocked = false;
            this.lockedUntil = null;
        }
        
        // Getters and Setters
        public String getSessionId() { return sessionId; }
        public String getUserId() { return userId; }
        public RiskScore getCurrentRiskScore() { return currentRiskScore; }
        public void setCurrentRiskScore(RiskScore currentRiskScore) { this.currentRiskScore = currentRiskScore; }
        public AccessDecision getLastDecision() { return lastDecision; }
        public void setLastDecision(AccessDecision lastDecision) { this.lastDecision = lastDecision; }
        public List<RiskEvent> getRiskEvents() { return riskEvents; }
        public LocalDateTime getSessionStartTime() { return sessionStartTime; }
        public LocalDateTime getLastAccessTime() { return lastAccessTime; }
        public int getConsecutiveDenials() { return consecutiveDenials; }
        public int getStepUpAuthCount() { return stepUpAuthCount; }
    }
    
    /**
     * 风险事件
     */
    public static class RiskEvent {
        private String eventType;
        private String description;
        private double riskImpact;
        private LocalDateTime timestamp;
        private Map<String, Object> details;
        
        public RiskEvent(String eventType, String description, double riskImpact) {
            this.eventType = eventType;
            this.description = description;
            this.riskImpact = riskImpact;
            this.timestamp = LocalDateTime.now();
            this.details = new HashMap<>();
        }
        
        public String getEventType() { return eventType; }
        public String getDescription() { return description; }
        public double getRiskImpact() { return riskImpact; }
        public LocalDateTime getTimestamp() { return timestamp; }
    }
    
    /**
     * 核心决策方法 - 评估访问请求并返回决策
     */
    public AccessDecisionResult evaluateAccess(AccessRequest request) {
        long startTime = System.currentTimeMillis();
        AccessDecisionResult result = new AccessDecisionResult();
        
        try {
            // 1. 检查会话状态
            SessionRiskState sessionState = getOrCreateSessionState(request.getSessionId(), request.getUserId());
            
            // 检查会话是否被锁定
            if (sessionState.isLocked()) {
                result.setDecision(AccessDecision.TEMPORARY_BLOCK);
                result.setReason("会话临时锁定，请" + getRemainingLockTime(sessionState) + "后重试");
                return result;
            }
            
            // 2. 执行风险评估
            RiskScore riskScore = riskAssessmentService.assessRisk(
                request.getUserId(),
                request.getDeviceId(),
                request.getSessionId(),
                buildAssessmentContext(request)
            );
            
            result.setRiskScore(riskScore);
            sessionState.setCurrentRiskScore(riskScore);
            
            // 3. 策略决策点评估
            PolicyDecision policyDecision = pdpService.evaluatePolicy(request, riskScore);
            result.setPolicyId(policyDecision.getPolicyId());
            
            // 4. 根据风险分数和策略做出决策
            AccessDecision decision = makeDecision(riskScore, policyDecision, sessionState);
            result.setDecision(decision);
            result.setReason(buildDecisionReason(riskScore, decision));
            
            // 5. 添加义务和建议
            addObligationsAndAdvice(result, riskScore, decision);
            
            // 6. 更新会话状态
            updateSessionState(sessionState, decision, riskScore);
            
            // 7. 记录决策日志
            logAccessDecision(request, result);
            
        } catch (Exception e) {
            // 异常情况下，采取保守策略
            result.setDecision(AccessDecision.REQUIRE_STEP_UP);
            result.setReason("风险评估系统异常，需要额外验证: " + e.getMessage());
        }
        
        result.setProcessingTimeMs(System.currentTimeMillis() - startTime);
        return result;
    }
    
    /**
     * 基于风险分数做出访问决策
     */
    private AccessDecision makeDecision(RiskScore riskScore, PolicyDecision policy, SessionRiskState sessionState) {
        double score = riskScore.getCompositeScore();
        
        // 首先检查策略决策
        if (policy != null && policy.isOverride()) {
            return policy.getDecision();
        }
        
        // 检查连续拒绝次数
        if (sessionState.getConsecutiveDenials() >= 3) {
            sessionState.lock(Duration.ofMinutes(15));
            return AccessDecision.TEMPORARY_BLOCK;
        }
        
        // 检查二次认证次数
        if (sessionState.getStepUpAuthCount() >= 5) {
            return AccessDecision.REQUIRE_APPROVAL;
        }
        
        // 基于风险分数决策
        if (score >= DENY_THRESHOLD) {
            return AccessDecision.DENY;
        } else if (score >= CHALLENGE_THRESHOLD) {
            if (riskScore.isRequiresStepUpAuth()) {
                return AccessDecision.REQUIRE_STEP_UP;
            }
            return AccessDecision.REQUIRE_REAUTH;
        } else if (score >= ALLOW_THRESHOLD) {
            return AccessDecision.ALLOW_WITH_LOGGING;
        }
        
        // 低风险，允许访问
        return AccessDecision.ALLOW;
    }
    
    /**
     * 更新会话状态
     */
    private void updateSessionState(SessionRiskState sessionState, AccessDecision decision, RiskScore riskScore) {
        sessionState.setLastDecision(decision);
        
        if (!decision.isAllowed()) {
            sessionState.incrementDenial();
            sessionState.recordEvent(new RiskEvent(
                "ACCESS_DENIED",
                "访问被拒绝: " + decision.getDescription(),
                riskScore.getCompositeScore()
            ));
            
            if (decision == AccessDecision.REQUIRE_STEP_UP) {
                sessionState.incrementStepUpCount();
            }
        } else {
            sessionState.resetDenials();
        }
        
        sessionStates.put(sessionState.getSessionId(), sessionState);
    }
    
    /**
     * 获取或创建会话状态
     */
    private SessionRiskState getOrCreateSessionState(String sessionId, String userId) {
        return sessionStates.computeIfAbsent(sessionId, k -> new SessionRiskState(sessionId, userId));
    }
    
    /**
     * 构建评估上下文
     */
    private Map<String, Object> buildAssessmentContext(AccessRequest request) {
        Map<String, Object> context = new HashMap<>();
        context.put("ipAddress", request.getIpAddress());
        context.put("userAgent", request.getUserAgent());
        context.put("resource", request.getResource());
        context.put("action", request.getAction());
        context.put("timestamp", request.getRequestTime());
        context.putAll(request.getAttributes());
        return context;
    }
    
    /**
     * 构建决策原因
     */
    private String buildDecisionReason(RiskScore riskScore, AccessDecision decision) {
        StringBuilder reason = new StringBuilder();
        reason.append("风险评分: ").append(String.format("%.1f", riskScore.getCompositeScore()))
              .append(" (").append(riskScore.getRiskLevel().getDisplayName()).append(")");
        
        if (!riskScore.getTriggeredFactors().isEmpty()) {
            reason.append(" | 触发因子: ")
                  .append(riskScore.getTriggeredFactorCount())
                  .append("个");
        }
        
        reason.append(" | 决策: ").append(decision.getDescription());
        
        return reason.toString();
    }
    
    /**
     * 添加义务和建议
     */
    private void addObligationsAndAdvice(AccessDecisionResult result, RiskScore riskScore, AccessDecision decision) {
        // 根据风险等级添加义务
        switch (riskScore.getRiskLevel()) {
            case MEDIUM:
                result.addObligation("记录详细访问日志");
                break;
            case MEDIUM_HIGH:
                result.addObligation("记录详细访问日志");
                result.addObligation("通知管理员");
                break;
            case HIGH:
            case CRITICAL:
                result.addObligation("记录详细访问日志");
                result.addObligation("立即通知安全团队");
                result.addObligation("启动安全审查流程");
                break;
            default:
                break;
        }
        
        // 添加建议
        if (riskScore.hasHighRiskFactor()) {
            result.addAdvice("建议加强用户身份验证");
        }
        
        if (decision == AccessDecision.ALLOW_WITH_LOGGING) {
            result.addAdvice("建议定期检查此用户的访问模式");
        }
    }
    
    /**
     * 获取剩余锁定时间
     */
    private String getRemainingLockTime(SessionRiskState sessionState) {
        // 简化实现
        return "15分钟";
    }
    
    /**
     * 记录访问决策
     */
    private void logAccessDecision(AccessRequest request, AccessDecisionResult result) {
        // 实际实现应该写入审计日志
        System.out.println(String.format("[ACCESS_DECISION] User=%s, Resource=%s, Decision=%s, Risk=%.1f",
            request.getUserId(),
            request.getResource(),
            result.getDecision(),
            result.getRiskScore() != null ? result.getRiskScore().getCompositeScore() : 0.0
        ));
    }
    
    /**
     * 强制重新评估会话风险
     */
    public void reevaluateSession(String sessionId) {
        SessionRiskState state = sessionStates.get(sessionId);
        if (state != null) {
            // 清除缓存，强制重新评估
            decisionCache.remove(sessionId);
            state.setCurrentRiskScore(null);
        }
    }
    
    /**
     * 手动解锁会话
     */
    public boolean unlockSession(String sessionId) {
        SessionRiskState state = sessionStates.get(sessionId);
        if (state != null && state.isLocked()) {
            state.unlock();
            state.resetDenials();
            return true;
        }
        return false;
    }
    
    /**
     * 获取会话风险状态
     */
    public SessionRiskState getSessionState(String sessionId) {
        return sessionStates.get(sessionId);
    }
    
    /**
     * 清除过期会话
     */
    public void cleanupExpiredSessions(Duration maxAge) {
        LocalDateTime cutoff = LocalDateTime.now().minus(maxAge);
        sessionStates.entrySet().removeIf(entry -> 
            entry.getValue().getLastAccessTime().isBefore(cutoff)
        );
    }
    
    // ========== 依赖服务接口定义 ==========
    
    /**
     * 策略决策
     */
    public static class PolicyDecision {
        private String policyId;
        private AccessDecision decision;
        private boolean override;
        private String reason;
        
        public String getPolicyId() { return policyId; }
        public void setPolicyId(String policyId) { this.policyId = policyId; }
        public AccessDecision getDecision() { return decision; }
        public void setDecision(AccessDecision decision) { this.decision = decision; }
        public boolean isOverride() { return override; }
        public void setOverride(boolean override) { this.override = override; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
    }
    
    /**
     * 风险评估服务接口
     */
    public interface RiskAssessmentService {
        RiskScore assessRisk(String userId, String deviceId, String sessionId, Map<String, Object> context);
    }
    
    /**
     * 策略决策点服务接口
     */
    public interface PolicyDecisionPointService {
        PolicyDecision evaluatePolicy(AccessRequest request, RiskScore riskScore);
    }
    
    /**
     * 会话风险管理器接口
     */
    public interface SessionRiskManager {
        void onRiskLevelChange(String sessionId, RiskScore.RiskLevel newLevel, RiskScore.RiskLevel oldLevel);
    }
}
