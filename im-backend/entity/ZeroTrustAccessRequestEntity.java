package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 零信任访问请求实体
 * 记录每次资源访问请求的详细信息和验证结果
 */
@Entity
@Table(name = "zero_trust_access_requests", indexes = {
    @Index(name = "idx_request_user_id", columnList = "userId"),
    @Index(name = "idx_request_resource", columnList = "resourceId,resourceType"),
    @Index(name = "idx_request_status", columnList = "status"),
    @Index(name = "idx_request_time", columnList = "requestTime"),
    @Index(name = "idx_request_device", columnList = "deviceId")
})
public class ZeroTrustAccessRequestEntity {

    @Id
    private String id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String deviceId;

    @Column(nullable = false)
    private String resourceId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ResourceType resourceType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessAction action;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccessStatus status;

    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Column(length = 50)
    private String authMethod;

    private Boolean mfaRequired;

    private Boolean mfaVerified;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 500)
    private String userAgent;

    private String location;

    private Double latitude;

    private Double longitude;

    @ElementCollection
    @CollectionTable(name = "request_context_attrs", joinColumns = @JoinColumn(name = "request_id"))
    @MapKeyColumn(name = "attr_key")
    @Column(name = "attr_value", length = 500)
    private Map<String, String> contextAttributes = new HashMap<>();

    @Column(name = "device_trust_score")
    private Integer deviceTrustScore;

    @Column(name = "user_behavior_score")
    private Integer userBehaviorScore;

    @Column(name = "network_risk_score")
    private Integer networkRiskScore;

    @Column(name = "violated_policies", length = 2000)
    private String violatedPolicies;

    @Column(name = "applied_policies", length = 2000)
    private String appliedPolicies;

    @Column(name = "deny_reason", length = 500)
    private String denyReason;

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    @Column(name = "decision_time")
    private LocalDateTime decisionTime;

    @Column(name = "session_id")
    private String sessionId;

    @Column(name = "microsegment_id")
    private String microsegmentId;

    @Column(name = "gateway_node")
    private String gatewayNode;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (requestTime == null) {
            requestTime = LocalDateTime.now();
        }
        if (status == null) {
            status = AccessStatus.PENDING;
        }
    }

    // 计算综合风险评分
    public void calculateRiskScore() {
        int score = 0;
        if (deviceTrustScore != null) score += deviceTrustScore * 0.3;
        if (userBehaviorScore != null) score += userBehaviorScore * 0.4;
        if (networkRiskScore != null) score += networkRiskScore * 0.3;
        this.riskScore = score;
        this.riskLevel = RiskLevel.fromScore(score);
    }

    // 判断是否允许访问
    public boolean isAllowed() {
        return status == AccessStatus.ALLOWED;
    }

    // 判断是否需要MFA
    public boolean requiresMFA() {
        return mfaRequired != null && mfaRequired && (mfaVerified == null || !mfaVerified);
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public String getResourceId() { return resourceId; }
    public void setResourceId(String resourceId) { this.resourceId = resourceId; }

    public ResourceType getResourceType() { return resourceType; }
    public void setResourceType(ResourceType resourceType) { this.resourceType = resourceType; }

    public AccessAction getAction() { return action; }
    public void setAction(AccessAction action) { this.action = action; }

    public AccessStatus getStatus() { return status; }
    public void setStatus(AccessStatus status) { this.status = status; }

    public Integer getRiskScore() { return riskScore; }
    public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }

    public RiskLevel getRiskLevel() { return riskLevel; }
    public void setRiskLevel(RiskLevel riskLevel) { this.riskLevel = riskLevel; }

    public String getAuthMethod() { return authMethod; }
    public void setAuthMethod(String authMethod) { this.authMethod = authMethod; }

    public Boolean getMfaRequired() { return mfaRequired; }
    public void setMfaRequired(Boolean mfaRequired) { this.mfaRequired = mfaRequired; }

    public Boolean getMfaVerified() { return mfaVerified; }
    public void setMfaVerified(Boolean mfaVerified) { this.mfaVerified = mfaVerified; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }

    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }

    public Map<String, String> getContextAttributes() { return contextAttributes; }
    public void setContextAttributes(Map<String, String> contextAttributes) { this.contextAttributes = contextAttributes; }

    public Integer getDeviceTrustScore() { return deviceTrustScore; }
    public void setDeviceTrustScore(Integer deviceTrustScore) { this.deviceTrustScore = deviceTrustScore; }

    public Integer getUserBehaviorScore() { return userBehaviorScore; }
    public void setUserBehaviorScore(Integer userBehaviorScore) { this.userBehaviorScore = userBehaviorScore; }

    public Integer getNetworkRiskScore() { return networkRiskScore; }
    public void setNetworkRiskScore(Integer networkRiskScore) { this.networkRiskScore = networkRiskScore; }

    public String getViolatedPolicies() { return violatedPolicies; }
    public void setViolatedPolicies(String violatedPolicies) { this.violatedPolicies = violatedPolicies; }

    public String getAppliedPolicies() { return appliedPolicies; }
    public void setAppliedPolicies(String appliedPolicies) { this.appliedPolicies = appliedPolicies; }

    public String getDenyReason() { return denyReason; }
    public void setDenyReason(String denyReason) { this.denyReason = denyReason; }

    public LocalDateTime getRequestTime() { return requestTime; }
    public void setRequestTime(LocalDateTime requestTime) { this.requestTime = requestTime; }

    public LocalDateTime getDecisionTime() { return decisionTime; }
    public void setDecisionTime(LocalDateTime decisionTime) { this.decisionTime = decisionTime; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getMicrosegmentId() { return microsegmentId; }
    public void setMicrosegmentId(String microsegmentId) { this.microsegmentId = microsegmentId; }

    public String getGatewayNode() { return gatewayNode; }
    public void setGatewayNode(String gatewayNode) { this.gatewayNode = gatewayNode; }

    // 资源类型枚举
    public enum ResourceType {
        API_ENDPOINT,      // API端点
        MESSAGE,           // 消息
        FILE,              // 文件
        CONTACT,           // 联系人
        GROUP,             // 群组
        CHANNEL,           // 频道
        ADMIN_PANEL,       // 管理后台
        CONFIGURATION,     // 配置
        DATABASE,          // 数据库
        SERVICE            // 服务
    }

    // 访问动作枚举
    public enum AccessAction {
        READ,              // 读取
        WRITE,             // 写入
        DELETE,            // 删除
        EXECUTE,           // 执行
        ADMIN              // 管理
    }

    // 访问状态枚举
    public enum AccessStatus {
        PENDING,           // 待处理
        ALLOWED,           // 允许
        DENIED,            // 拒绝
        MFA_REQUIRED,      // 需要MFA
        RISK_REVIEW,       // 风险审核中
        QUARANTINED,       // 已隔离
        EXPIRED            // 已过期
    }

    // 风险等级枚举
    public enum RiskLevel {
        CRITICAL(90, 100, "严重风险"),
        HIGH(70, 89, "高风险"),
        MEDIUM(40, 69, "中等风险"),
        LOW(20, 39, "低风险"),
        MINIMAL(0, 19, "极低风险");

        private final int minScore;
        private final int maxScore;
        private final String description;

        RiskLevel(int minScore, int maxScore, String description) {
            this.minScore = minScore;
            this.maxScore = maxScore;
            this.description = description;
        }

        public static RiskLevel fromScore(int score) {
            for (RiskLevel level : values()) {
                if (score >= level.minScore && score <= level.maxScore) {
                    return level;
                }
            }
            return CRITICAL;
        }

        public boolean requiresMFA() {
            return this == HIGH || this == CRITICAL;
        }

        public boolean requiresReview() {
            return this == CRITICAL;
        }

        public String getDescription() { return description; }
    }
}
