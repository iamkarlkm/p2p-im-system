package com.im.backend.service;

import com.im.backend.entity.DeviceTrustStateEntity;
import com.im.backend.entity.ZeroTrustAccessRequestEntity;
import com.im.backend.entity.ZeroTrustPolicyEntity;
import com.im.backend.repository.DeviceTrustStateRepository;
import com.im.backend.repository.ZeroTrustAccessRequestRepository;
import com.im.backend.repository.ZeroTrustPolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 零信任安全网关核心服务
 * 实现零信任架构的核心逻辑：永不信任，始终验证
 */
@Service
public class ZeroTrustGatewayService {

    private static final Logger logger = LoggerFactory.getLogger(ZeroTrustGatewayService.class);

    @Autowired
    private ZeroTrustAccessRequestRepository accessRequestRepository;

    @Autowired
    private ZeroTrustPolicyRepository policyRepository;

    @Autowired
    private DeviceTrustStateRepository deviceTrustStateRepository;

    @Autowired
    private RiskAssessmentService riskAssessmentService;

    // 活跃会话缓存
    private final Map<String, ZeroTrustSession> activeSessions = new ConcurrentHashMap<>();

    // 微隔离策略缓存
    private final Map<String, MicroSegmentPolicy> microSegmentPolicies = new ConcurrentHashMap<>();

    /**
     * 执行访问控制决策
     * 核心零信任验证流程
     */
    @Transactional
    public AccessDecision evaluateAccess(AccessContext context) {
        logger.info("Evaluating access for user {} to resource {}", 
            context.getUserId(), context.getResourceId());

        // 1. 创建访问请求记录
        ZeroTrustAccessRequestEntity request = createAccessRequest(context);

        // 2. 验证设备信任状态
        DeviceTrustStateEntity deviceState = validateDeviceTrust(context.getDeviceId());
        if (deviceState == null || deviceState.isQuarantined()) {
            request.setStatus(ZeroTrustAccessRequestEntity.AccessStatus.DENIED);
            request.setDenyReason("Device not trusted or quarantined");
            accessRequestRepository.save(request);
            return AccessDecision.deny("设备未通过信任验证或已被隔离");
        }

        request.setDeviceTrustScore(deviceState.getTrustScore());

        // 3. 进行风险评估
        RiskAssessment riskAssessment = riskAssessmentService.assessRisk(context, deviceState);
        request.setUserBehaviorScore(riskAssessment.getBehaviorScore());
        request.setNetworkRiskScore(riskAssessment.getNetworkScore());
        request.calculateRiskScore();

        // 4. 评估策略
        PolicyEvaluationResult policyResult = evaluatePolicies(context, deviceState, riskAssessment);
        request.setAppliedPolicies(policyResult.getAppliedPolicyIds());
        request.setViolatedPolicies(policyResult.getViolatedPolicyIds());

        // 5. 做出访问决策
        AccessDecision decision = makeAccessDecision(request, policyResult, riskAssessment);

        // 6. 更新请求状态
        request.setStatus(decision.isAllowed() ? 
            ZeroTrustAccessRequestEntity.AccessStatus.ALLOWED : 
            ZeroTrustAccessRequestEntity.AccessStatus.DENIED);
        request.setDecisionTime(LocalDateTime.now());
        
        if (!decision.isAllowed()) {
            request.setDenyReason(decision.getReason());
        }

        accessRequestRepository.save(request);

        // 7. 如果允许访问，创建/更新会话
        if (decision.isAllowed()) {
            createOrUpdateSession(context, deviceState, request.getRiskScore());
        }

        logger.info("Access decision for user {}: {} (risk score: {})",
            context.getUserId(), decision.isAllowed() ? "ALLOWED" : "DENIED", 
            request.getRiskScore());

        return decision;
    }

    /**
     * 验证MFA
     */
    @Transactional
    public boolean verifyMFA(String requestId, String mfaCode) {
        Optional<ZeroTrustAccessRequestEntity> requestOpt = accessRequestRepository.findById(requestId);
        if (requestOpt.isEmpty()) {
            return false;
        }

        ZeroTrustAccessRequestEntity request = requestOpt.get();
        
        // 验证MFA代码（简化实现，实际应调用MFA服务）
        boolean verified = validateMFACode(request.getUserId(), mfaCode);
        
        if (verified) {
            request.setMfaVerified(true);
            request.setStatus(ZeroTrustAccessRequestEntity.AccessStatus.ALLOWED);
            request.setDecisionTime(LocalDateTime.now());
            accessRequestRepository.save(request);
            
            logger.info("MFA verified for request {}", requestId);
        } else {
            logger.warn("MFA verification failed for request {}", requestId);
        }

        return verified;
    }

    /**
     * 设备注册
     */
    @Transactional
    public DeviceTrustStateEntity registerDevice(DeviceRegistrationRequest request) {
        logger.info("Registering device for user {}", request.getUserId());

        DeviceTrustStateEntity device = new DeviceTrustStateEntity();
        device.setUserId(request.getUserId());
        device.setDeviceName(request.getDeviceName());
        device.setDeviceType(request.getDeviceType());
        device.setOsType(request.getOsType());
        device.setOsVersion(request.getOsVersion());
        device.setIpAddress(request.getIpAddress());
        device.setMacAddress(request.getMacAddress());
        device.setTrustStatus(DeviceTrustStateEntity.TrustStatus.PENDING);
        device.setTrustScore(50);  // 初始信任分数

        // 评估初始合规性
        evaluateDeviceCompliance(device, request);

        deviceTrustStateRepository.save(device);
        
        logger.info("Device registered: {} for user {}", device.getDeviceId(), request.getUserId());
        
        return device;
    }

    /**
     * 更新设备状态
     */
    @Transactional
    public DeviceTrustStateEntity updateDeviceState(String deviceId, DeviceHealthReport report) {
        Optional<DeviceTrustStateEntity> deviceOpt = deviceTrustStateRepository.findByDeviceId(deviceId);
        if (deviceOpt.isEmpty()) {
            throw new IllegalArgumentException("Device not found: " + deviceId);
        }

        DeviceTrustStateEntity device = deviceOpt.get();
        
        // 更新健康信息
        device.setHasAntivirus(report.isHasAntivirus());
        device.setFirewallEnabled(report.isFirewallEnabled());
        device.setDiskEncrypted(report.isDiskEncrypted());
        device.setScreenLockEnabled(report.isScreenLockEnabled());
        device.setScreenLockType(report.getScreenLockType());
        device.setIsJailbroken(report.isJailbroken());
        
        if (report.getInstalledApps() != null) {
            device.setInstalledApps(report.getInstalledApps());
        }
        
        if (report.getVulnerabilities() != null) {
            device.setVulnerabilities(report.getVulnerabilities());
        }

        // 重新评估信任分数
        evaluateDeviceTrust(device);
        device.updateLastSeen();
        
        deviceTrustStateRepository.save(device);
        
        logger.info("Device state updated: {}, trust score: {}", deviceId, device.getTrustScore());
        
        return device;
    }

    /**
     * 隔离设备
     */
    @Transactional
    public void quarantineDevice(String deviceId, String reason) {
        Optional<DeviceTrustStateEntity> deviceOpt = deviceTrustStateRepository.findByDeviceId(deviceId);
        if (deviceOpt.isPresent()) {
            DeviceTrustStateEntity device = deviceOpt.get();
            device.setTrustStatus(DeviceTrustStateEntity.TrustStatus.QUARANTINED);
            device.setQuarantineStart(LocalDateTime.now());
            device.setQuarantineReason(reason);
            deviceTrustStateRepository.save(device);
            
            // 终止该设备的所有会话
            terminateDeviceSessions(deviceId);
            
            logger.warn("Device quarantined: {}, reason: {}", deviceId, reason);
        }
    }

    /**
     * 解除隔离
     */
    @Transactional
    public void unquarantineDevice(String deviceId) {
        Optional<DeviceTrustStateEntity> deviceOpt = deviceTrustStateRepository.findByDeviceId(deviceId);
        if (deviceOpt.isPresent()) {
            DeviceTrustStateEntity device = deviceOpt.get();
            device.setTrustStatus(DeviceTrustStateEntity.TrustStatus.PENDING);
            device.setQuarantineStart(null);
            device.setQuarantineReason(null);
            deviceTrustStateRepository.save(device);
            
            logger.info("Device unquarantined: {}", deviceId);
        }
    }

    /**
     * 创建微隔离段
     */
    @Transactional
    public MicroSegment createMicroSegment(MicroSegmentRequest request) {
        String segmentId = UUID.randomUUID().toString();
        
        MicroSegmentPolicy policy = new MicroSegmentPolicy();
        policy.setSegmentId(segmentId);
        policy.setName(request.getName());
        policy.setDescription(request.getDescription());
        policy.setAllowedResources(request.getAllowedResources());
        policy.setDeniedResources(request.getDeniedResources());
        policy.setMinTrustScore(request.getMinTrustScore());
        policy.setRequireMFA(request.isRequireMFA());
        policy.setCreatedAt(LocalDateTime.now());
        
        microSegmentPolicies.put(segmentId, policy);
        
        logger.info("Micro-segment created: {}", segmentId);
        
        return new MicroSegment(segmentId, policy.getName(), policy.getDescription());
    }

    /**
     * 获取会话信息
     */
    public ZeroTrustSession getSession(String sessionId) {
        return activeSessions.get(sessionId);
    }

    /**
     * 终止会话
     */
    public void terminateSession(String sessionId) {
        ZeroTrustSession session = activeSessions.remove(sessionId);
        if (session != null) {
            logger.info("Session terminated: {}", sessionId);
        }
    }

    /**
     * 定期清理过期会话
     */
    @Scheduled(fixedRate = 60000)  // 每分钟执行
    public void cleanupExpiredSessions() {
        LocalDateTime now = LocalDateTime.now();
        List<String> expiredSessions = activeSessions.entrySet().stream()
            .filter(entry -> entry.getValue().getExpiresAt().isBefore(now))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        expiredSessions.forEach(activeSessions::remove);
        
        if (!expiredSessions.isEmpty()) {
            logger.info("Cleaned up {} expired sessions", expiredSessions.size());
        }
    }

    // ============ 私有方法 ============

    private ZeroTrustAccessRequestEntity createAccessRequest(AccessContext context) {
        ZeroTrustAccessRequestEntity request = new ZeroTrustAccessRequestEntity();
        request.setUserId(context.getUserId());
        request.setDeviceId(context.getDeviceId());
        request.setResourceId(context.getResourceId());
        request.setResourceType(context.getResourceType());
        request.setAction(context.getAction());
        request.setIpAddress(context.getIpAddress());
        request.setUserAgent(context.getUserAgent());
        request.setLocation(context.getLocation());
        request.setSessionId(context.getSessionId());
        request.setContextAttributes(context.getAttributes());
        return accessRequestRepository.save(request);
    }

    private DeviceTrustStateEntity validateDeviceTrust(String deviceId) {
        return deviceTrustStateRepository.findByDeviceId(deviceId).orElse(null);
    }

    private PolicyEvaluationResult evaluatePolicies(AccessContext context, 
                                                     DeviceTrustStateEntity device,
                                                     RiskAssessment riskAssessment) {
        List<ZeroTrustPolicyEntity> policies = policyRepository.findByEnabledTrueOrderByPriorityAsc();
        
        PolicyEvaluationResult result = new PolicyEvaluationResult();
        List<String> appliedPolicies = new ArrayList<>();
        List<String> violatedPolicies = new ArrayList<>();
        
        for (ZeroTrustPolicyEntity policy : policies) {
            if (!policy.matchesResource(context.getResourceType(), context.getResourceId())) {
                continue;
            }
            
            appliedPolicies.add(policy.getId());
            
            boolean conditionsMet = evaluatePolicyConditions(policy, context, device, riskAssessment);
            
            if (policy.getEffect() == ZeroTrustPolicyEntity.PolicyEffect.DENY && conditionsMet) {
                violatedPolicies.add(policy.getId());
                result.setDenied(true);
                result.setDenyReason("Policy violated: " + policy.getName());
                break;
            }
            
            if (policy.getEffect() == ZeroTrustPolicyEntity.PolicyEffect.ALLOW && conditionsMet) {
                if (policy.getRequireMFA()) {
                    result.setMfaRequired(true);
                }
                result.setAllowed(true);
            }
        }
        
        result.setAppliedPolicyIds(String.join(",", appliedPolicies));
        result.setViolatedPolicyIds(String.join(",", violatedPolicies));
        
        return result;
    }

    private boolean evaluatePolicyConditions(ZeroTrustPolicyEntity policy, 
                                              AccessContext context,
                                              DeviceTrustStateEntity device,
                                              RiskAssessment riskAssessment) {
        // 简化实现：实际应根据条件类型和操作符进行复杂评估
        return true;  // 默认满足条件
    }

    private AccessDecision makeAccessDecision(ZeroTrustAccessRequestEntity request,
                                               PolicyEvaluationResult policyResult,
                                               RiskAssessment riskAssessment) {
        // 如果策略明确拒绝
        if (policyResult.isDenied()) {
            return AccessDecision.deny(policyResult.getDenyReason());
        }
        
        // 如果需要MFA
        if (policyResult.isMfaRequired() || request.getRiskLevel().requiresMFA()) {
            request.setMfaRequired(true);
            return AccessDecision.mfaRequired();
        }
        
        // 如果风险等级需要审核
        if (request.getRiskLevel().requiresReview()) {
            request.setStatus(ZeroTrustAccessRequestEntity.AccessStatus.RISK_REVIEW);
            return AccessDecision.reviewRequired();
        }
        
        // 默认允许
        return AccessDecision.allow();
    }

    private void createOrUpdateSession(AccessContext context, 
                                       DeviceTrustStateEntity device,
                                       Integer riskScore) {
        String sessionId = context.getSessionId();
        if (sessionId == null) {
            sessionId = UUID.randomUUID().toString();
        }
        
        ZeroTrustSession session = new ZeroTrustSession();
        session.setSessionId(sessionId);
        session.setUserId(context.getUserId());
        session.setDeviceId(context.getDeviceId());
        session.setTrustScore(device.getTrustScore());
        session.setRiskScore(riskScore);
        session.setCreatedAt(LocalDateTime.now());
        session.setLastAccessed(LocalDateTime.now());
        session.setExpiresAt(LocalDateTime.now().plusMinutes(device.getTrustScore() > 80 ? 60 : 30));
        
        activeSessions.put(sessionId, session);
    }

    private void terminateDeviceSessions(String deviceId) {
        List<String> sessionsToTerminate = activeSessions.entrySet().stream()
            .filter(entry -> entry.getValue().getDeviceId().equals(deviceId))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        sessionsToTerminate.forEach(activeSessions::remove);
        
        logger.info("Terminated {} sessions for device {}", 
            sessionsToTerminate.size(), deviceId);
    }

    private void evaluateDeviceCompliance(DeviceTrustStateEntity device, 
                                          DeviceRegistrationRequest request) {
        // 简化实现：根据设备信息评估初始合规性
        int complianceScore = 50;
        
        if (request.isDiskEncrypted()) complianceScore += 15;
        if (request.isScreenLockEnabled()) complianceScore += 10;
        if (request.isFirewallEnabled()) complianceScore += 10;
        if (request.isHasAntivirus()) complianceScore += 10;
        if (!request.isJailbroken()) complianceScore += 5;
        
        device.setComplianceScore(Math.min(100, complianceScore));
        device.setIsCompliant(device.checkCompliance());
    }

    private void evaluateDeviceTrust(DeviceTrustStateEntity device) {
        // 安全评分计算
        int securityScore = 50;
        if (device.getHasAntivirus()) securityScore += 10;
        if (device.getFirewallEnabled()) securityScore += 10;
        if (device.getDiskEncrypted()) securityScore += 15;
        if (device.getScreenLockEnabled()) securityScore += 10;
        if (!device.getIsJailbroken()) securityScore += 10;
        if (device.getVulnerabilities().isEmpty()) securityScore += 5;
        
        device.setSecurityScore(Math.min(100, securityScore));
        
        // 健康评分计算
        int healthScore = 70;
        if (device.getLastPatchDate() != null && 
            device.getLastPatchDate().isAfter(LocalDateTime.now().minusDays(30))) {
            healthScore += 20;
        }
        if (device.getAntivirusLastScan() != null && 
            device.getAntivirusLastScan().isAfter(LocalDateTime.now().minusDays(7))) {
            healthScore += 10;
        }
        
        device.setHealthScore(Math.min(100, healthScore));
        device.setIsCompliant(device.checkCompliance());
        device.calculateTrustScore();
    }

    private boolean validateMFACode(Long userId, String mfaCode) {
        // 简化实现：实际应调用MFA服务验证
        return mfaCode != null && mfaCode.length() == 6;
    }

    // ============ 内部类 ============

    public static class AccessContext {
        private Long userId;
        private String deviceId;
        private String resourceId;
        private ZeroTrustAccessRequestEntity.ResourceType resourceType;
        private ZeroTrustAccessRequestEntity.AccessAction action;
        private String ipAddress;
        private String userAgent;
        private String location;
        private String sessionId;
        private Map<String, String> attributes = new HashMap<>();

        // Getters and Setters
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public String getResourceId() { return resourceId; }
        public void setResourceId(String resourceId) { this.resourceId = resourceId; }
        public ZeroTrustAccessRequestEntity.ResourceType getResourceType() { return resourceType; }
        public void setResourceType(ZeroTrustAccessRequestEntity.ResourceType resourceType) { this.resourceType = resourceType; }
        public ZeroTrustAccessRequestEntity.AccessAction getAction() { return action; }
        public void setAction(ZeroTrustAccessRequestEntity.AccessAction action) { this.action = action; }
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        public String getUserAgent() { return userAgent; }
        public void setUserAgent(String userAgent) { this.userAgent = userAgent; }
        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }
        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public Map<String, String> getAttributes() { return attributes; }
        public void setAttributes(Map<String, String> attributes) { this.attributes = attributes; }
    }

    public static class AccessDecision {
        private boolean allowed;
        private boolean mfaRequired;
        private boolean reviewRequired;
        private String reason;

        public static AccessDecision allow() {
            AccessDecision d = new AccessDecision();
            d.allowed = true;
            return d;
        }

        public static AccessDecision deny(String reason) {
            AccessDecision d = new AccessDecision();
            d.allowed = false;
            d.reason = reason;
            return d;
        }

        public static AccessDecision mfaRequired() {
            AccessDecision d = new AccessDecision();
            d.mfaRequired = true;
            d.reason = "MFA verification required";
            return d;
        }

        public static AccessDecision reviewRequired() {
            AccessDecision d = new AccessDecision();
            d.reviewRequired = true;
            d.reason = "Risk review required";
            return d;
        }

        public boolean isAllowed() { return allowed; }
        public boolean isMfaRequired() { return mfaRequired; }
        public boolean isReviewRequired() { return reviewRequired; }
        public String getReason() { return reason; }
    }

    public static class ZeroTrustSession {
        private String sessionId;
        private Long userId;
        private String deviceId;
        private Integer trustScore;
        private Integer riskScore;
        private LocalDateTime createdAt;
        private LocalDateTime lastAccessed;
        private LocalDateTime expiresAt;

        public String getSessionId() { return sessionId; }
        public void setSessionId(String sessionId) { this.sessionId = sessionId; }
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public Integer getTrustScore() { return trustScore; }
        public void setTrustScore(Integer trustScore) { this.trustScore = trustScore; }
        public Integer getRiskScore() { return riskScore; }
        public void setRiskScore(Integer riskScore) { this.riskScore = riskScore; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
        public LocalDateTime getLastAccessed() { return lastAccessed; }
        public void setLastAccessed(LocalDateTime lastAccessed) { this.lastAccessed = lastAccessed; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    }

    // 其他内部类定义...
    public static class PolicyEvaluationResult {
        private boolean allowed;
        private boolean denied;
        private boolean mfaRequired;
        private String denyReason;
        private String appliedPolicyIds;
        private String violatedPolicyIds;

        public boolean isAllowed() { return allowed; }
        public void setAllowed(boolean allowed) { this.allowed = allowed; }
        public boolean isDenied() { return denied; }
        public void setDenied(boolean denied) { this.denied = denied; }
        public boolean isMfaRequired() { return mfaRequired; }
        public void setMfaRequired(boolean mfaRequired) { this.mfaRequired = mfaRequired; }
        public String getDenyReason() { return denyReason; }
        public void setDenyReason(String denyReason) { this.denyReason = denyReason; }
        public String getAppliedPolicyIds() { return appliedPolicyIds; }
        public void setAppliedPolicyIds(String appliedPolicyIds) { this.appliedPolicyIds = appliedPolicyIds; }
        public String getViolatedPolicyIds() { return violatedPolicyIds; }
        public void setViolatedPolicyIds(String violatedPolicyIds) { this.violatedPolicyIds = violatedPolicyIds; }
    }

    public static class DeviceRegistrationRequest {
        private Long userId;
        private String deviceName;
        private DeviceTrustStateEntity.DeviceType deviceType;
        private String osType;
        private String osVersion;
        private String ipAddress;
        private String macAddress;
        private boolean hasAntivirus;
        private boolean firewallEnabled;
        private boolean diskEncrypted;
        private boolean screenLockEnabled;
        private boolean isJailbroken;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getDeviceName() { return deviceName; }
        public void setDeviceName(String deviceName) { this.deviceName = deviceName; }
        public DeviceTrustStateEntity.DeviceType getDeviceType() { return deviceType; }
        public void setDeviceType(DeviceTrustStateEntity.DeviceType deviceType) { this.deviceType = deviceType; }
        public String getOsType() { return osType; }
        public void setOsType(String osType) { this.osType = osType; }
        public String getOsVersion() { return osVersion; }
        public void setOsVersion(String osVersion) { this.osVersion = osVersion; }
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        public String getMacAddress() { return macAddress; }
        public void setMacAddress(String macAddress) { this.macAddress = macAddress; }
        public boolean isHasAntivirus() { return hasAntivirus; }
        public void setHasAntivirus(boolean hasAntivirus) { this.hasAntivirus = hasAntivirus; }
        public boolean isFirewallEnabled() { return firewallEnabled; }
        public void setFirewallEnabled(boolean firewallEnabled) { this.firewallEnabled = firewallEnabled; }
        public boolean isDiskEncrypted() { return diskEncrypted; }
        public void setDiskEncrypted(boolean diskEncrypted) { this.diskEncrypted = diskEncrypted; }
        public boolean isScreenLockEnabled() { return screenLockEnabled; }
        public void setScreenLockEnabled(boolean screenLockEnabled) { this.screenLockEnabled = screenLockEnabled; }
        public boolean isJailbroken() { return isJailbroken; }
        public void setJailbroken(boolean jailbroken) { isJailbroken = jailbroken; }
    }

    public static class DeviceHealthReport {
        private boolean hasAntivirus;
        private boolean firewallEnabled;
        private boolean diskEncrypted;
        private boolean screenLockEnabled;
        private String screenLockType;
        private boolean isJailbroken;
        private Map<String, String> installedApps;
        private Map<String, String> vulnerabilities;

        public boolean isHasAntivirus() { return hasAntivirus; }
        public void setHasAntivirus(boolean hasAntivirus) { this.hasAntivirus = hasAntivirus; }
        public boolean isFirewallEnabled() { return firewallEnabled; }
        public void setFirewallEnabled(boolean firewallEnabled) { this.firewallEnabled = firewallEnabled; }
        public boolean isDiskEncrypted() { return diskEncrypted; }
        public void setDiskEncrypted(boolean diskEncrypted) { this.diskEncrypted = diskEncrypted; }
        public boolean isScreenLockEnabled() { return screenLockEnabled; }
        public void setScreenLockEnabled(boolean screenLockEnabled) { this.screenLockEnabled = screenLockEnabled; }
        public String getScreenLockType() { return screenLockType; }
        public void setScreenLockType(String screenLockType) { this.screenLockType = screenLockType; }
        public boolean isJailbroken() { return isJailbroken; }
        public void setJailbroken(boolean jailbroken) { isJailbroken = jailbroken; }
        public Map<String, String> getInstalledApps() { return installedApps; }
        public void setInstalledApps(Map<String, String> installedApps) { this.installedApps = installedApps; }
        public Map<String, String> getVulnerabilities() { return vulnerabilities; }
        public void setVulnerabilities(Map<String, String> vulnerabilities) { this.vulnerabilities = vulnerabilities; }
    }

    public static class MicroSegment {
        private String segmentId;
        private String name;
        private String description;

        public MicroSegment(String segmentId, String name, String description) {
            this.segmentId = segmentId;
            this.name = name;
            this.description = description;
        }

        public String getSegmentId() { return segmentId; }
        public String getName() { return name; }
        public String getDescription() { return description; }
    }

    public static class MicroSegmentRequest {
        private String name;
        private String description;
        private List<String> allowedResources;
        private List<String> deniedResources;
        private Integer minTrustScore;
        private boolean requireMFA;

        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getAllowedResources() { return allowedResources; }
        public void setAllowedResources(List<String> allowedResources) { this.allowedResources = allowedResources; }
        public List<String> getDeniedResources() { return deniedResources; }
        public void setDeniedResources(List<String> deniedResources) { this.deniedResources = deniedResources; }
        public Integer getMinTrustScore() { return minTrustScore; }
        public void setMinTrustScore(Integer minTrustScore) { this.minTrustScore = minTrustScore; }
        public boolean isRequireMFA() { return requireMFA; }
        public void setRequireMFA(boolean requireMFA) { this.requireMFA = requireMFA; }
    }

    public static class MicroSegmentPolicy {
        private String segmentId;
        private String name;
        private String description;
        private List<String> allowedResources;
        private List<String> deniedResources;
        private Integer minTrustScore;
        private boolean requireMFA;
        private LocalDateTime createdAt;

        public String getSegmentId() { return segmentId; }
        public void setSegmentId(String segmentId) { this.segmentId = segmentId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public List<String> getAllowedResources() { return allowedResources; }
        public void setAllowedResources(List<String> allowedResources) { this.allowedResources = allowedResources; }
        public List<String> getDeniedResources() { return deniedResources; }
        public void setDeniedResources(List<String> deniedResources) { this.deniedResources = deniedResources; }
        public Integer getMinTrustScore() { return minTrustScore; }
        public void setMinTrustScore(Integer minTrustScore) { this.minTrustScore = minTrustScore; }
        public boolean isRequireMFA() { return requireMFA; }
        public void setRequireMFA(boolean requireMFA) { this.requireMFA = requireMFA; }
        public LocalDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    }
}
