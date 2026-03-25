package com.im.system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 量子安全证书颁发机构实体
 * 管理CA层级结构和证书策略
 * 
 * @since 2026-03-25
 * @version 1.0.0
 */
@Entity
@Table(name = "quantum_certificate_authorities", 
       indexes = {
           @Index(name = "idx_ca_name", columnList = "caName"),
           @Index(name = "idx_ca_parent", columnList = "parentCaId"),
           @Index(name = "idx_ca_status", columnList = "caStatus"),
           @Index(name = "idx_ca_crl", columnList = "crlDistributionPoint")
       })
public class QuantumCertificateAuthorityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /**
     * CA名称
     * 示例：IM Quantum Root CA, IM Intermediate CA 1, IM Device CA
     */
    @Column(name = "ca_name", nullable = false, unique = true, length = 256)
    private String caName;

    /**
     * CA唯一标识（OID格式）
     * 示例：1.3.6.1.4.1.12345.1.1
     */
    @Column(name = "ca_oid", nullable = false, unique = true, length = 128)
    private String caOid;

    /**
     * 父级CA ID（如果是根CA则为null）
     */
    @Column(name = "parent_ca_id")
    private UUID parentCaId;

    /**
     * CA层级深度
     * 0 - 根CA
     * 1 - 中间CA
     * 2 - 颁发CA
     */
    @Column(name = "ca_level", nullable = false)
    private Integer caLevel = 0;

    /**
     * CA状态
     * ACTIVE - 活跃
     * SUSPENDED - 已暂停
     * REVOKED - 已撤销
     * DEPRECATED - 已弃用
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "ca_status", nullable = false, length = 20)
    private CaStatus caStatus = CaStatus.ACTIVE;

    /**
     * CA密钥对标识符
     */
    @Column(name = "key_pair_id", nullable = false, length = 64)
    private String keyPairId;

    /**
     * CA公钥指纹（SHA-256）
     */
    @Column(name = "public_key_fingerprint", nullable = false, length = 64)
    private String publicKeyFingerprint;

    /**
     * CA证书序列号
     */
    @Column(name = "ca_certificate_serial", length = 64)
    private String caCertificateSerial;

    /**
     * CA证书有效期开始
     */
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    /**
     * CA证书有效期结束
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    /**
     * 支持的签名算法
     * 多个算法用逗号分隔
     */
    @Column(name = "supported_algorithms", nullable = false, length = 512)
    private String supportedAlgorithms;

    /**
     * 证书策略OID
     * 多个OID用逗号分隔
     */
    @Column(name = "certificate_policies", columnDefinition = "TEXT")
    private String certificatePolicies;

    /**
     * CRL分发点URL
     */
    @Column(name = "crl_distribution_point", length = 512)
    private String crlDistributionPoint;

    /**
     * OCSP响应者URL
     */
    @Column(name = "ocsp_responder_url", length = 512)
    private String ocspResponderUrl;

    /**
     * 证书透明度日志URL
     */
    @Column(name = "certificate_transparency_logs", columnDefinition = "TEXT")
    private String certificateTransparencyLogs;

    /**
     * CA颁发限制（JSON格式）
     * 包含：最大路径长度、允许的证书类型、密钥用法限制等
     */
    @Column(name = "issuance_restrictions", columnDefinition = "TEXT")
    private String issuanceRestrictions;

    /**
     * 已颁发证书数量
     */
    @Column(name = "issued_certificate_count")
    private Long issuedCertificateCount = 0L;

    /**
     * 已撤销证书数量
     */
    @Column(name = "revoked_certificate_count")
    private Long revokedCertificateCount = 0L;

    /**
     * 最后证书序列号
     */
    @Column(name = "last_serial_number", length = 32)
    private String lastSerialNumber;

    /**
     * CA元数据（JSON格式）
     * 包含：地理位置、联系人、组织信息等
     */
    @Column(name = "ca_metadata", columnDefinition = "TEXT")
    private String caMetadata;

    /**
     * 是否支持混合证书（传统+量子安全）
     */
    @Column(name = "supports_hybrid_certificates")
    private Boolean supportsHybridCertificates = false;

    /**
     * 是否启用自动续期
     */
    @Column(name = "auto_renewal_enabled")
    private Boolean autoRenewalEnabled = true;

    /**
     * 续期提前通知天数
     */
    @Column(name = "renewal_notification_days")
    private Integer renewalNotificationDays = 30;

    /**
     * 密钥轮换策略（JSON格式）
     */
    @Column(name = "key_rotation_policy", columnDefinition = "TEXT")
    private String keyRotationPolicy;

    /**
     * 审计日志配置（JSON格式）
     */
    @Column(name = "audit_configuration", columnDefinition = "TEXT")
    private String auditConfiguration;

    /**
     * 版本号
     */
    @Version
    @Column(name = "version")
    private Long version;

    /**
     * 创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    /**
     * 更新时间
     */
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    // 枚举：CA状态
    public enum CaStatus {
        ACTIVE,       // 活跃
        SUSPENDED,    // 已暂停
        REVOKED,      // 已撤销
        DEPRECATED    // 已弃用
    }

    // 构造函数
    public QuantumCertificateAuthorityEntity() {
    }

    public QuantumCertificateAuthorityEntity(String caName, String caOid, Integer caLevel,
                                           String keyPairId, String publicKeyFingerprint,
                                           LocalDateTime validFrom, LocalDateTime expiryDate,
                                           String supportedAlgorithms) {
        this.caName = caName;
        this.caOid = caOid;
        this.caLevel = caLevel;
        this.keyPairId = keyPairId;
        this.publicKeyFingerprint = publicKeyFingerprint;
        this.validFrom = validFrom;
        this.expiryDate = expiryDate;
        this.supportedAlgorithms = supportedAlgorithms;
        this.caStatus = CaStatus.ACTIVE;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getCaName() {
        return caName;
    }

    public void setCaName(String caName) {
        this.caName = caName;
    }

    public String getCaOid() {
        return caOid;
    }

    public void setCaOid(String caOid) {
        this.caOid = caOid;
    }

    public UUID getParentCaId() {
        return parentCaId;
    }

    public void setParentCaId(UUID parentCaId) {
        this.parentCaId = parentCaId;
    }

    public Integer getCaLevel() {
        return caLevel;
    }

    public void setCaLevel(Integer caLevel) {
        this.caLevel = caLevel;
    }

    public CaStatus getCaStatus() {
        return caStatus;
    }

    public void setCaStatus(CaStatus caStatus) {
        this.caStatus = caStatus;
    }

    public String getKeyPairId() {
        return keyPairId;
    }

    public void setKeyPairId(String keyPairId) {
        this.keyPairId = keyPairId;
    }

    public String getPublicKeyFingerprint() {
        return publicKeyFingerprint;
    }

    public void setPublicKeyFingerprint(String publicKeyFingerprint) {
        this.publicKeyFingerprint = publicKeyFingerprint;
    }

    public String getCaCertificateSerial() {
        return caCertificateSerial;
    }

    public void setCaCertificateSerial(String caCertificateSerial) {
        this.caCertificateSerial = caCertificateSerial;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDateTime expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getSupportedAlgorithms() {
        return supportedAlgorithms;
    }

    public void setSupportedAlgorithms(String supportedAlgorithms) {
        this.supportedAlgorithms = supportedAlgorithms;
    }

    public String getCertificatePolicies() {
        return certificatePolicies;
    }

    public void setCertificatePolicies(String certificatePolicies) {
        this.certificatePolicies = certificatePolicies;
    }

    public String getCrlDistributionPoint() {
        return crlDistributionPoint;
    }

    public void setCrlDistributionPoint(String crlDistributionPoint) {
        this.crlDistributionPoint = crlDistributionPoint;
    }

    public String getOcspResponderUrl() {
        return ocspResponderUrl;
    }

    public void setOcspResponderUrl(String ocspResponderUrl) {
        this.ocspResponderUrl = ocspResponderUrl;
    }

    public String getCertificateTransparencyLogs() {
        return certificateTransparencyLogs;
    }

    public void setCertificateTransparencyLogs(String certificateTransparencyLogs) {
        this.certificateTransparencyLogs = certificateTransparencyLogs;
    }

    public String getIssuanceRestrictions() {
        return issuanceRestrictions;
    }

    public void setIssuanceRestrictions(String issuanceRestrictions) {
        this.issuanceRestrictions = issuanceRestrictions;
    }

    public Long getIssuedCertificateCount() {
        return issuedCertificateCount;
    }

    public void setIssuedCertificateCount(Long issuedCertificateCount) {
        this.issuedCertificateCount = issuedCertificateCount;
    }

    public Long getRevokedCertificateCount() {
        return revokedCertificateCount;
    }

    public void setRevokedCertificateCount(Long revokedCertificateCount) {
        this.revokedCertificateCount = revokedCertificateCount;
    }

    public String getLastSerialNumber() {
        return lastSerialNumber;
    }

    public void setLastSerialNumber(String lastSerialNumber) {
        this.lastSerialNumber = lastSerialNumber;
    }

    public String getCaMetadata() {
        return caMetadata;
    }

    public void setCaMetadata(String caMetadata) {
        this.caMetadata = caMetadata;
    }

    public Boolean getSupportsHybridCertificates() {
        return supportsHybridCertificates;
    }

    public void setSupportsHybridCertificates(Boolean supportsHybridCertificates) {
        this.supportsHybridCertificates = supportsHybridCertificates;
    }

    public Boolean getAutoRenewalEnabled() {
        return autoRenewalEnabled;
    }

    public void setAutoRenewalEnabled(Boolean autoRenewalEnabled) {
        this.autoRenewalEnabled = autoRenewalEnabled;
    }

    public Integer getRenewalNotificationDays() {
        return renewalNotificationDays;
    }

    public void setRenewalNotificationDays(Integer renewalNotificationDays) {
        this.renewalNotificationDays = renewalNotificationDays;
    }

    public String getKeyRotationPolicy() {
        return keyRotationPolicy;
    }

    public void setKeyRotationPolicy(String keyRotationPolicy) {
        this.keyRotationPolicy = keyRotationPolicy;
    }

    public String getAuditConfiguration() {
        return auditConfiguration;
    }

    public void setAuditConfiguration(String auditConfiguration) {
        this.auditConfiguration = auditConfiguration;
    }

    public Long getVersion() {
        return version;
    }

    public void setVersion(Long version) {
        this.version = version;
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

    // 业务方法
    public boolean isRootCA() {
        return caLevel == 0;
    }

    public boolean isIntermediateCA() {
        return caLevel == 1;
    }

    public boolean isIssuingCA() {
        return caLevel == 2;
    }

    public boolean isActive() {
        return caStatus == CaStatus.ACTIVE;
    }

    public boolean isSuspended() {
        return caStatus == CaStatus.SUSPENDED;
    }

    public boolean isRevoked() {
        return caStatus == CaStatus.REVOKED;
    }

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(expiryDate);
    }

    public void incrementIssuedCount() {
        this.issuedCertificateCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementRevokedCount() {
        this.revokedCertificateCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public String generateNextSerialNumber() {
        // 生成格式：QSCA-{YYYYMMDD}-{6位递增数字}
        String datePart = LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        if (lastSerialNumber == null) {
            lastSerialNumber = "QSCA-" + datePart + "-000001";
        } else {
            // 解析并递增序列号
            String[] parts = lastSerialNumber.split("-");
            if (parts.length == 3 && parts[0].equals("QSCA") && parts[1].equals(datePart)) {
                int sequence = Integer.parseInt(parts[2]);
                sequence++;
                lastSerialNumber = String.format("QSCA-%s-%06d", datePart, sequence);
            } else {
                // 日期变化或格式不同，重置序列号
                lastSerialNumber = "QSCA-" + datePart + "-000001";
            }
        }
        
        return lastSerialNumber;
    }

    public boolean supportsAlgorithm(String algorithm) {
        if (supportedAlgorithms == null || algorithm == null) {
            return false;
        }
        return supportedAlgorithms.contains(algorithm);
    }

    public void suspend(String reason) {
        this.caStatus = CaStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
        // 记录审计日志
        // 实际实现中，这里会调用审计服务
    }

    public void revoke(String reason) {
        this.caStatus = CaStatus.REVOKED;
        this.updatedAt = LocalDateTime.now();
        // 记录审计日志
        // 实际实现中，这里会调用审计服务
    }

    public void activate() {
        if (isExpired()) {
            throw new IllegalStateException("Cannot activate an expired CA");
        }
        this.caStatus = CaStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    public void renew(LocalDateTime newExpiryDate) {
        this.expiryDate = newExpiryDate;
        this.caStatus = CaStatus.ACTIVE;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "QuantumCertificateAuthorityEntity{" +
                "id=" + id +
                ", caName='" + caName + '\'' +
                ", caOid='" + caOid + '\'' +
                ", caLevel=" + caLevel +
                ", caStatus=" + caStatus +
                ", validFrom=" + validFrom +
                ", expiryDate=" + expiryDate +
                ", issuedCertificateCount=" + issuedCertificateCount +
                '}';
    }
}