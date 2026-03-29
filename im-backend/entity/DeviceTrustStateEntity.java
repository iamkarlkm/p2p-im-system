package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 设备信任状态实体
 * 零信任架构中的设备健康度和信任评分
 */
@Entity
@Table(name = "device_trust_states", indexes = {
    @Index(name = "idx_device_user", columnList = "userId"),
    @Index(name = "idx_device_status", columnList = "trustStatus"),
    @Index(name = "idx_device_score", columnList = "trustScore"),
    @Index(name = "idx_device_last_seen", columnList = "lastSeenAt")
})
public class DeviceTrustStateEntity {

    @Id
    private String id;

    @Column(nullable = false, unique = true)
    private String deviceId;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String deviceName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType deviceType;

    @Column(length = 100)
    private String osType;

    @Column(length = 50)
    private String osVersion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TrustStatus trustStatus;

    @Column(nullable = false)
    private Integer trustScore = 50;  // 0-100

    @Column(name = "compliance_score")
    private Integer complianceScore = 50;

    @Column(name = "security_score")
    private Integer securityScore = 50;

    @Column(name = "health_score")
    private Integer healthScore = 50;

    private Boolean isManaged = false;

    private Boolean isCompliant = false;

    private Boolean isJailbroken = false;

    private Boolean hasAntivirus = false;

    private Boolean firewallEnabled = false;

    private Boolean diskEncrypted = false;

    private Boolean screenLockEnabled = false;

    @Column(name = "screen_lock_type")
    private String screenLockType;

    @Column(name = "last_patch_date")
    private LocalDateTime lastPatchDate;

    @Column(name = "antivirus_last_scan")
    private LocalDateTime antivirusLastScan;

    @Column(name = "certificate_fingerprint")
    private String certificateFingerprint;

    @Column(name = "device_certificate")
    private String deviceCertificate;

    @ElementCollection
    @CollectionTable(name = "device_installed_apps", joinColumns = @JoinColumn(name = "device_id"))
    @MapKeyColumn(name = "app_name")
    @Column(name = "app_version")
    private Map<String, String> installedApps = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "device_vulnerabilities", joinColumns = @JoinColumn(name = "device_id"))
    @MapKeyColumn(name = "vuln_id")
    @Column(name = "vuln_severity")
    private Map<String, String> vulnerabilities = new HashMap<>();

    @Column(name = "network_interfaces", length = 2000)
    private String networkInterfaces;  // JSON

    @Column(name = "ip_address")
    private String ipAddress;

    @Column(name = "mac_address")
    private String macAddress;

    @Column(name = "registered_at")
    private LocalDateTime registeredAt;

    @Column(name = "last_seen_at")
    private LocalDateTime lastSeenAt;

    @Column(name = "last_evaluated_at")
    private LocalDateTime lastEvaluatedAt;

    @Column(name = "quarantine_start")
    private LocalDateTime quarantineStart;

    @Column(name = "quarantine_reason")
    private String quarantineReason;

    @Column(name = "evaluation_count")
    private Integer evaluationCount = 0;

    @Column(name = "consecutive_failures")
    private Integer consecutiveFailures = 0;

    @Version
    private Long version;

    @PrePersist
    public void prePersist() {
        if (id == null) {
            id = UUID.randomUUID().toString();
        }
        if (deviceId == null) {
            deviceId = UUID.randomUUID().toString();
        }
        if (registeredAt == null) {
            registeredAt = LocalDateTime.now();
        }
        lastSeenAt = LocalDateTime.now();
    }

    // 计算综合信任评分
    public void calculateTrustScore() {
        int score = 0;
        if (complianceScore != null) score += complianceScore * 0.35;
        if (securityScore != null) score += securityScore * 0.40;
        if (healthScore != null) score += healthScore * 0.25;
        this.trustScore = Math.min(100, Math.max(0, score));
        updateTrustStatus();
    }

    // 根据分数更新状态
    public void updateTrustStatus() {
        if (trustScore >= 80) {
            trustStatus = TrustStatus.TRUSTED;
        } else if (trustScore >= 60) {
            trustStatus = TrustStatus.LOW_RISK;
        } else if (trustScore >= 40) {
            trustStatus = TrustStatus.MEDIUM_RISK;
        } else if (trustScore >= 20) {
            trustStatus = TrustStatus.HIGH_RISK;
        } else {
            trustStatus = TrustStatus.UNTRUSTED;
        }
    }

    // 检查是否被隔离
    public boolean isQuarantined() {
        return trustStatus == TrustStatus.QUARANTINED;
    }

    // 检查是否需要重新评估
    public boolean needsReevaluation() {
        if (lastEvaluatedAt == null) return true;
        return lastEvaluatedAt.plusHours(24).isBefore(LocalDateTime.now());
    }

    // 更新最后活跃时间
    public void updateLastSeen() {
        this.lastSeenAt = LocalDateTime.now();
    }

    // 记录评估
    public void recordEvaluation(boolean success) {
        this.lastEvaluatedAt = LocalDateTime.now();
        this.evaluationCount++;
        if (success) {
            this.consecutiveFailures = 0;
        } else {
            this.consecutiveFailures++;
        }
    }

    // 检查合规性
    public boolean checkCompliance() {
        return !isJailbroken &&
               diskEncrypted &&
               screenLockEnabled &&
               firewallEnabled &&
               vulnerabilities.isEmpty();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getDeviceId() { return deviceId; }
    public void setDeviceId(String deviceId) { this.deviceId = deviceId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getDeviceName() { return deviceName; }
    public void setDeviceName(String deviceName) { this.deviceName = deviceName; }

    public DeviceType getDeviceType() { return deviceType; }
    public void setDeviceType(DeviceType deviceType) { this.deviceType = deviceType; }

    public String getOsType() { return osType; }
    public void setOsType(String osType) { this.osType = osType; }

    public String getOsVersion() { return osVersion; }
    public void setOsVersion(String osVersion) { this.osVersion = osVersion; }

    public TrustStatus getTrustStatus() { return trustStatus; }
    public void setTrustStatus(TrustStatus trustStatus) { this.trustStatus = trustStatus; }

    public Integer getTrustScore() { return trustScore; }
    public void setTrustScore(Integer trustScore) { this.trustScore = trustScore; }

    public Integer getComplianceScore() { return complianceScore; }
    public void setComplianceScore(Integer complianceScore) { this.complianceScore = complianceScore; }

    public Integer getSecurityScore() { return securityScore; }
    public void setSecurityScore(Integer securityScore) { this.securityScore = securityScore; }

    public Integer getHealthScore() { return healthScore; }
    public void setHealthScore(Integer healthScore) { this.healthScore = healthScore; }

    public Boolean getIsManaged() { return isManaged; }
    public void setIsManaged(Boolean isManaged) { this.isManaged = isManaged; }

    public Boolean getIsCompliant() { return isCompliant; }
    public void setIsCompliant(Boolean isCompliant) { this.isCompliant = isCompliant; }

    public Boolean getIsJailbroken() { return isJailbroken; }
    public void setIsJailbroken(Boolean isJailbroken) { this.isJailbroken = isJailbroken; }

    public Boolean getHasAntivirus() { return hasAntivirus; }
    public void setHasAntivirus(Boolean hasAntivirus) { this.hasAntivirus = hasAntivirus; }

    public Boolean getFirewallEnabled() { return firewallEnabled; }
    public void setFirewallEnabled(Boolean firewallEnabled) { this.firewallEnabled = firewallEnabled; }

    public Boolean getDiskEncrypted() { return diskEncrypted; }
    public void setDiskEncrypted(Boolean diskEncrypted) { this.diskEncrypted = diskEncrypted; }

    public Boolean getScreenLockEnabled() { return screenLockEnabled; }
    public void setScreenLockEnabled(Boolean screenLockEnabled) { this.screenLockEnabled = screenLockEnabled; }

    public String getScreenLockType() { return screenLockType; }
    public void setScreenLockType(String screenLockType) { this.screenLockType = screenLockType; }

    public LocalDateTime getLastPatchDate() { return lastPatchDate; }
    public void setLastPatchDate(LocalDateTime lastPatchDate) { this.lastPatchDate = lastPatchDate; }

    public LocalDateTime getAntivirusLastScan() { return antivirusLastScan; }
    public void setAntivirusLastScan(LocalDateTime antivirusLastScan) { this.antivirusLastScan = antivirusLastScan; }

    public String getCertificateFingerprint() { return certificateFingerprint; }
    public void setCertificateFingerprint(String certificateFingerprint) { this.certificateFingerprint = certificateFingerprint; }

    public String getDeviceCertificate() { return deviceCertificate; }
    public void setDeviceCertificate(String deviceCertificate) { this.deviceCertificate = deviceCertificate; }

    public Map<String, String> getInstalledApps() { return installedApps; }
    public void setInstalledApps(Map<String, String> installedApps) { this.installedApps = installedApps; }

    public Map<String, String> getVulnerabilities() { return vulnerabilities; }
    public void setVulnerabilities(Map<String, String> vulnerabilities) { this.vulnerabilities = vulnerabilities; }

    public String getNetworkInterfaces() { return networkInterfaces; }
    public void setNetworkInterfaces(String networkInterfaces) { this.networkInterfaces = networkInterfaces; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getMacAddress() { return macAddress; }
    public void setMacAddress(String macAddress) { this.macAddress = macAddress; }

    public LocalDateTime getRegisteredAt() { return registeredAt; }
    public void setRegisteredAt(LocalDateTime registeredAt) { this.registeredAt = registeredAt; }

    public LocalDateTime getLastSeenAt() { return lastSeenAt; }
    public void setLastSeenAt(LocalDateTime lastSeenAt) { this.lastSeenAt = lastSeenAt; }

    public LocalDateTime getLastEvaluatedAt() { return lastEvaluatedAt; }
    public void setLastEvaluatedAt(LocalDateTime lastEvaluatedAt) { this.lastEvaluatedAt = lastEvaluatedAt; }

    public LocalDateTime getQuarantineStart() { return quarantineStart; }
    public void setQuarantineStart(LocalDateTime quarantineStart) { this.quarantineStart = quarantineStart; }

    public String getQuarantineReason() { return quarantineReason; }
    public void setQuarantineReason(String quarantineReason) { this.quarantineReason = quarantineReason; }

    public Integer getEvaluationCount() { return evaluationCount; }
    public void setEvaluationCount(Integer evaluationCount) { this.evaluationCount = evaluationCount; }

    public Integer getConsecutiveFailures() { return consecutiveFailures; }
    public void setConsecutiveFailures(Integer consecutiveFailures) { this.consecutiveFailures = consecutiveFailures; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    // 设备类型枚举
    public enum DeviceType {
        DESKTOP,      // 桌面电脑
        LAPTOP,       // 笔记本
        MOBILE,       // 手机
        TABLET,       // 平板
        SERVER,       // 服务器
        IOT,          // 物联网设备
        UNKNOWN       // 未知
    }

    // 信任状态枚举
    public enum TrustStatus {
        TRUSTED,      // 已信任 (80-100分)
        LOW_RISK,     // 低风险 (60-79分)
        MEDIUM_RISK,  // 中风险 (40-59分)
        HIGH_RISK,    // 高风险 (20-39分)
        UNTRUSTED,    // 不可信 (0-19分)
        QUARANTINED,  // 已隔离
        PENDING       // 待评估
    }
}
