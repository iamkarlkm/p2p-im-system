package com.im.backend.controller;

import com.im.backend.entity.DeviceTrustStateEntity;
import com.im.backend.entity.ZeroTrustAccessRequestEntity;
import com.im.backend.service.RiskAssessmentService;
import com.im.backend.service.ZeroTrustGatewayService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 零信任安全网关REST API控制器
 */
@RestController
@RequestMapping("/api/v1/zero-trust")
public class ZeroTrustGatewayController {

    private static final Logger logger = LoggerFactory.getLogger(ZeroTrustGatewayController.class);

    @Autowired
    private ZeroTrustGatewayService gatewayService;

    @Autowired
    private RiskAssessmentService riskAssessmentService;

    /**
     * 执行访问控制评估
     */
    @PostMapping("/access/evaluate")
    public ResponseEntity<AccessEvaluationResponse> evaluateAccess(
            @RequestBody AccessEvaluationRequest request) {
        
        logger.info("Access evaluation request from user {} for resource {}",
            request.getUserId(), request.getResourceId());

        ZeroTrustGatewayService.AccessContext context = new ZeroTrustGatewayService.AccessContext();
        context.setUserId(request.getUserId());
        context.setDeviceId(request.getDeviceId());
        context.setResourceId(request.getResourceId());
        context.setResourceType(request.getResourceType());
        context.setAction(request.getAction());
        context.setIpAddress(request.getIpAddress());
        context.setUserAgent(request.getUserAgent());
        context.setLocation(request.getLocation());
        context.setSessionId(request.getSessionId());
        context.setAttributes(request.getAttributes());

        ZeroTrustGatewayService.AccessDecision decision = gatewayService.evaluateAccess(context);

        AccessEvaluationResponse response = new AccessEvaluationResponse();
        response.setAllowed(decision.isAllowed());
        response.setMfaRequired(decision.isMfaRequired());
        response.setReviewRequired(decision.isReviewRequired());
        response.setReason(decision.getReason());
        response.setTimestamp(LocalDateTime.now());

        return ResponseEntity.ok(response);
    }

    /**
     * MFA验证
     */
    @PostMapping("/access/mfa-verify")
    public ResponseEntity<MFAVerifyResponse> verifyMFA(
            @RequestBody MFAVerifyRequest request) {
        
        logger.info("MFA verification for request {}", request.getRequestId());

        boolean verified = gatewayService.verifyMFA(request.getRequestId(), request.getMfaCode());

        MFAVerifyResponse response = new MFAVerifyResponse();
        response.setSuccess(verified);
        response.setMessage(verified ? "MFA verification successful" : "Invalid MFA code");

        return ResponseEntity.ok(response);
    }

    /**
     * 设备注册
     */
    @PostMapping("/devices/register")
    public ResponseEntity<DeviceRegistrationResponse> registerDevice(
            @RequestBody DeviceRegistrationRequest request) {
        
        logger.info("Device registration request from user {}", request.getUserId());

        ZeroTrustGatewayService.DeviceRegistrationRequest regRequest = 
            new ZeroTrustGatewayService.DeviceRegistrationRequest();
        regRequest.setUserId(request.getUserId());
        regRequest.setDeviceName(request.getDeviceName());
        regRequest.setDeviceType(request.getDeviceType());
        regRequest.setOsType(request.getOsType());
        regRequest.setOsVersion(request.getOsVersion());
        regRequest.setIpAddress(request.getIpAddress());
        regRequest.setMacAddress(request.getMacAddress());
        regRequest.setHasAntivirus(request.isHasAntivirus());
        regRequest.setFirewallEnabled(request.isFirewallEnabled());
        regRequest.setDiskEncrypted(request.isDiskEncrypted());
        regRequest.setScreenLockEnabled(request.isScreenLockEnabled());
        regRequest.setJailbroken(request.isJailbroken());

        DeviceTrustStateEntity device = gatewayService.registerDevice(regRequest);

        DeviceRegistrationResponse response = new DeviceRegistrationResponse();
        response.setSuccess(true);
        response.setDeviceId(device.getDeviceId());
        response.setTrustScore(device.getTrustScore());
        response.setTrustStatus(device.getTrustStatus());
        response.setMessage("Device registered successfully");

        return ResponseEntity.ok(response);
    }

    /**
     * 更新设备健康状态
     */
    @PostMapping("/devices/{deviceId}/health")
    public ResponseEntity<DeviceHealthResponse> updateDeviceHealth(
            @PathVariable String deviceId,
            @RequestBody DeviceHealthRequest request) {
        
        logger.info("Device health update for {}", deviceId);

        ZeroTrustGatewayService.DeviceHealthReport report = new ZeroTrustGatewayService.DeviceHealthReport();
        report.setHasAntivirus(request.isHasAntivirus());
        report.setFirewallEnabled(request.isFirewallEnabled());
        report.setDiskEncrypted(request.isDiskEncrypted());
        report.setScreenLockEnabled(request.isScreenLockEnabled());
        report.setScreenLockType(request.getScreenLockType());
        report.setJailbroken(request.isJailbroken());
        report.setInstalledApps(request.getInstalledApps());
        report.setVulnerabilities(request.getVulnerabilities());

        DeviceTrustStateEntity device = gatewayService.updateDeviceState(deviceId, report);

        DeviceHealthResponse response = new DeviceHealthResponse();
        response.setDeviceId(deviceId);
        response.setTrustScore(device.getTrustScore());
        response.setTrustStatus(device.getTrustStatus());
        response.setComplianceScore(device.getComplianceScore());
        response.setSecurityScore(device.getSecurityScore());
        response.setHealthScore(device.getHealthScore());
        response.setCompliant(device.getIsCompliant());

        return ResponseEntity.ok(response);
    }

    /**
     * 隔离设备
     */
    @PostMapping("/devices/{deviceId}/quarantine")
    public ResponseEntity<Map<String, Object>> quarantineDevice(
            @PathVariable String deviceId,
            @RequestBody QuarantineRequest request) {
        
        logger.warn("Quarantining device: {}, reason: {}", deviceId, request.getReason());

        gatewayService.quarantineDevice(deviceId, request.getReason());

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("deviceId", deviceId);
        response.put("status", "QUARANTINED");
        response.put("reason", request.getReason());

        return ResponseEntity.ok(response);
    }

    /**
     * 解除隔离
     */
    @PostMapping("/devices/{deviceId}/unquarantine")
    public ResponseEntity<Map<String, Object>> unquarantineDevice(
            @PathVariable String deviceId) {
        
        logger.info("Unquarantining device: {}", deviceId);

        gatewayService.unquarantineDevice(deviceId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("deviceId", deviceId);
        response.put("status", "PENDING");

        return ResponseEntity.ok(response);
    }

    /**
     * 创建微隔离段
     */
    @PostMapping("/micro-segments")
    public ResponseEntity<MicroSegmentResponse> createMicroSegment(
            @RequestBody MicroSegmentRequest request) {
        
        logger.info("Creating micro-segment: {}", request.getName());

        ZeroTrustGatewayService.MicroSegmentRequest segRequest = 
            new ZeroTrustGatewayService.MicroSegmentRequest();
        segRequest.setName(request.getName());
        segRequest.setDescription(request.getDescription());
        segRequest.setAllowedResources(request.getAllowedResources());
        segRequest.setDeniedResources(request.getDeniedResources());
        segRequest.setMinTrustScore(request.getMinTrustScore());
        segRequest.setRequireMFA(request.isRequireMFA());

        ZeroTrustGatewayService.MicroSegment segment = gatewayService.createMicroSegment(segRequest);

        MicroSegmentResponse response = new MicroSegmentResponse();
        response.setSegmentId(segment.getSegmentId());
        response.setName(segment.getName());
        response.setDescription(segment.getDescription());

        return ResponseEntity.ok(response);
    }

    /**
     * 获取会话信息
     */
    @GetMapping("/sessions/{sessionId}")
    public ResponseEntity<SessionInfoResponse> getSessionInfo(@PathVariable String sessionId) {
        ZeroTrustGatewayService.ZeroTrustSession session = gatewayService.getSession(sessionId);
        
        if (session == null) {
            return ResponseEntity.notFound().build();
        }

        SessionInfoResponse response = new SessionInfoResponse();
        response.setSessionId(session.getSessionId());
        response.setUserId(session.getUserId());
        response.setDeviceId(session.getDeviceId());
        response.setTrustScore(session.getTrustScore());
        response.setRiskScore(session.getRiskScore());
        response.setCreatedAt(session.getCreatedAt());
        response.setExpiresAt(session.getExpiresAt());

        return ResponseEntity.ok(response);
    }

    /**
     * 终止会话
     */
    @DeleteMapping("/sessions/{sessionId}")
    public ResponseEntity<Map<String, Object>> terminateSession(@PathVariable String sessionId) {
        gatewayService.terminateSession(sessionId);

        Map<String, Object> response = new HashMap<>();
        response.put("success", true);
        response.put("sessionId", sessionId);
        response.put("status", "TERMINATED");

        return ResponseEntity.ok(response);
    }

    /**
     * 执行风险评估
     */
    @PostMapping("/risk/assess")
    public ResponseEntity<RiskAssessmentResponse> assessRisk(
            @RequestBody RiskAssessmentRequest request) {
        
        logger.info("Risk assessment request for user {}", request.getUserId());

        // 简化实现：实际应构建完整的AccessContext
        RiskAssessmentService.RiskAssessment assessment = new RiskAssessmentService.RiskAssessment();
        assessment.setOverallScore(50);
        assessment.setRiskLevel(ZeroTrustAccessRequestEntity.RiskLevel.MEDIUM);

        RiskAssessmentResponse response = new RiskAssessmentResponse();
        response.setOverallScore(assessment.getOverallScore());
        response.setRiskLevel(assessment.getRiskLevel().name());
        response.setDeviceScore(assessment.getDeviceScore());
        response.setBehaviorScore(assessment.getBehaviorScore());
        response.setNetworkScore(assessment.getNetworkScore());
        response.setContextScore(assessment.getContextScore());

        return ResponseEntity.ok(response);
    }

    // ============ 请求/响应DTO ============

    public static class AccessEvaluationRequest {
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

    public static class AccessEvaluationResponse {
        private boolean allowed;
        private boolean mfaRequired;
        private boolean reviewRequired;
        private String reason;
        private LocalDateTime timestamp;

        public boolean isAllowed() { return allowed; }
        public void setAllowed(boolean allowed) { this.allowed = allowed; }
        public boolean isMfaRequired() { return mfaRequired; }
        public void setMfaRequired(boolean mfaRequired) { this.mfaRequired = mfaRequired; }
        public boolean isReviewRequired() { return reviewRequired; }
        public void setReviewRequired(boolean reviewRequired) { this.reviewRequired = reviewRequired; }
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
        public LocalDateTime getTimestamp() { return timestamp; }
        public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
    }

    public static class MFAVerifyRequest {
        private String requestId;
        private String mfaCode;

        public String getRequestId() { return requestId; }
        public void setRequestId(String requestId) { this.requestId = requestId; }
        public String getMfaCode() { return mfaCode; }
        public void setMfaCode(String mfaCode) { this.mfaCode = mfaCode; }
    }

    public static class MFAVerifyResponse {
        private boolean success;
        private String message;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
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
        private boolean jailbroken;

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
        public boolean isJailbroken() { return jailbroken; }
        public void setJailbroken(boolean jailbroken) { this.jailbroken = jailbroken; }
    }

    public static class DeviceRegistrationResponse {
        private boolean success;
        private String deviceId;
        private Integer trustScore;
        private DeviceTrustStateEntity.TrustStatus trustStatus;
        private String message;

        public boolean isSuccess() { return success; }
        public void setSuccess(boolean success) { this.success = success; }
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public Integer getTrustScore() { return trustScore; }
        public void setTrustScore(Integer trustScore) { this.trustScore = trustScore; }
        public DeviceTrustStateEntity.TrustStatus getTrustStatus() { return trustStatus; }
        public void setTrustStatus(DeviceTrustStateEntity.TrustStatus trustStatus) { this.trustStatus = trustStatus; }
        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
    }

    public static class DeviceHealthRequest {
        private boolean hasAntivirus;
        private boolean firewallEnabled;
        private boolean diskEncrypted;
        private boolean screenLockEnabled;
        private String screenLockType;
        private boolean jailbroken;
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
        public boolean isJailbroken() { return jailbroken; }
        public void setJailbroken(boolean jailbroken) { this.jailbroken = jailbroken; }
        public Map<String, String> getInstalledApps() { return installedApps; }
        public void setInstalledApps(Map<String, String> installedApps) { this.installedApps = installedApps; }
        public Map<String, String> getVulnerabilities() { return vulnerabilities; }
        public void setVulnerabilities(Map<String, String> vulnerabilities) { this.vulnerabilities = vulnerabilities; }
    }

    public static class DeviceHealthResponse {
        private String deviceId;
        private Integer trustScore;
        private DeviceTrustStateEntity.TrustStatus trustStatus;
        private Integer complianceScore;
        private Integer securityScore;
        private Integer healthScore;
        private Boolean compliant;

        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public Integer getTrustScore() { return trustScore; }
        public void setTrustScore(Integer trustScore) { this.trustScore = trustScore; }
        public DeviceTrustStateEntity.TrustStatus getTrustStatus() { return trustStatus; }
        public void setTrustStatus(DeviceTrustStateEntity.TrustStatus trustStatus) { this.trustStatus = trustStatus; }
        public Integer getComplianceScore() { return complianceScore; }
        public void setComplianceScore(Integer complianceScore) { this.complianceScore = complianceScore; }
        public Integer getSecurityScore() { return securityScore; }
        public void setSecurityScore(Integer securityScore) { this.securityScore = securityScore; }
        public Integer getHealthScore() { return healthScore; }
        public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }
        public Boolean getCompliant() { return compliant; }
        public void setCompliant(Boolean compliant) { this.compliant = compliant; }
    }

    public static class QuarantineRequest {
        private String reason;
        public String getReason() { return reason; }
        public void setReason(String reason) { this.reason = reason; }
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

    public static class MicroSegmentResponse {
        private String segmentId;
        private String name;
        private String description;

        public String getSegmentId() { return segmentId; }
        public void setSegmentId(String segmentId) { this.segmentId = segmentId; }
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }

    public static class SessionInfoResponse {
        private String sessionId;
        private Long userId;
        private String deviceId;
        private Integer trustScore;
        private Integer riskScore;
        private LocalDateTime createdAt;
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
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    }

    public static class RiskAssessmentRequest {
        private Long userId;
        private String deviceId;
        private String resourceId;

        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        public String getDeviceId() { return deviceId; }
        public void setDeviceId(String deviceId) { this.deviceId = deviceId; }
        public String getResourceId() { return resourceId; }
        public void setResourceId(String resourceId) { this.resourceId = resourceId; }
    }

    public static class RiskAssessmentResponse {
        private int overallScore;
        private String riskLevel;
        private int deviceScore;
        private int behaviorScore;
        private int networkScore;
        private int contextScore;

        public int getOverallScore() { return overallScore; }
        public void setOverallScore(int overallScore) { this.overallScore = overallScore; }
        public String getRiskLevel() { return riskLevel; }
        public void setRiskLevel(String riskLevel) { this.riskLevel = riskLevel; }
        public int getDeviceScore() { return deviceScore; }
        public void setDeviceScore(int deviceScore) { this.deviceScore = deviceScore; }
        public int getBehaviorScore() { return behaviorScore; }
        public void setBehaviorScore(int behaviorScore) { this.behaviorScore = behaviorScore; }
        public int getNetworkScore() { return networkScore; }
        public void setNetworkScore(int networkScore) { this.networkScore = networkScore; }
        public int getContextScore() { return contextScore; }
        public void setContextScore(int contextScore) { this.contextScore = contextScore; }
    }
}
