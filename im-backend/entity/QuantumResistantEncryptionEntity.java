package com.imsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * 量子抗性加密实体
 * 
 * 基于后量子密码学的量子抗性加密配置和状态管理
 * 支持 NIST 标准化算法: CRYSTALS-Kyber, CRYSTALS-Dilithium, FALCON, SPHINCS+
 * 
 * 作者: 编程开发代理
 * 创建时间: 2026-03-24 10:02
 */
@Entity
@Table(name = "quantum_resistant_encryption")
public class QuantumResistantEncryptionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "algorithm_type", nullable = false, length = 50)
    private String algorithmType; // Kyber, Dilithium, Falcon, SPHINCS+, SIKE, NTRU, etc.
    
    @Column(name = "key_size", nullable = false)
    private Integer keySize; // 128, 192, 256
    
    @Column(name = "security_level", nullable = false)
    private String securityLevel; // Level 1, Level 3, Level 5 (NIST 标准化)
    
    @Column(name = "public_key", columnDefinition = "TEXT")
    private String publicKey;
    
    @Column(name = "private_key_encrypted", columnDefinition = "TEXT")
    private String privateKeyEncrypted; // 加密后的私钥
    
    @Column(name = "certificate_data", columnDefinition = "TEXT")
    private String certificateData; // X.509 证书数据
    
    @Column(name = "signature_scheme", length = 50)
    private String signatureScheme; // Dilithium2, Dilithium3, Falcon-512, Falcon-1024, SPHINCS+
    
    @Column(name = "encryption_scheme", length = 50)
    private String encryptionScheme; // Kyber512, Kyber768, Kyber1024, NTRU, SIKE
    
    @Column(name = "key_exchange_protocol", length = 50)
    private String keyExchangeProtocol; // Kyber, NTRU, SIKE, HQC, BIKE
    
    @Column(name = "hybrid_mode", nullable = false)
    private Boolean hybridMode = true; // 是否使用混合加密模式 (后量子 + 传统)
    
    @Column(name = "key_lifetime_days")
    private Integer keyLifetimeDays = 365; // 密钥生命周期
    
    @Column(name = "rotation_interval_days")
    private Integer rotationIntervalDays = 90; // 密钥轮换间隔
    
    @Column(name = "last_rotation_time")
    private LocalDateTime lastRotationTime;
    
    @Column(name = "next_rotation_time")
    private LocalDateTime nextRotationTime;
    
    @Column(name = "key_version", nullable = false)
    private Integer keyVersion = 1;
    
    @Column(name = "key_fingerprint", length = 64)
    private String keyFingerprint; // SHA-256 指纹
    
    @Column(name = "key_status", nullable = false, length = 20)
    private String keyStatus = "ACTIVE"; // ACTIVE, EXPIRED, REVOKED, SUSPENDED
    
    @Column(name = "revocation_reason", length = 100)
    private String revocationReason;
    
    @Column(name = "revocation_time")
    private LocalDateTime revocationTime;
    
    @Column(name = "key_metadata", columnDefinition = "JSON")
    private String keyMetadata; // 额外的密钥元数据
    
    @Column(name = "performance_metrics", columnDefinition = "JSON")
    private String performanceMetrics; // 性能指标数据
    
    @Column(name = "compliance_tags", columnDefinition = "TEXT")
    private String complianceTags; // 合规性标签: NIST, FIPS, GDPR, etc.
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "is_default", nullable = false)
    private Boolean isDefault = false; // 是否默认配置
    
    @Column(name = "enabled", nullable = false)
    private Boolean enabled = true;
    
    // 构造方法
    public QuantumResistantEncryptionEntity() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
    }
    
    // Getter 和 Setter 方法
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getUserId() {
        return userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    
    public String getAlgorithmType() {
        return algorithmType;
    }
    
    public void setAlgorithmType(String algorithmType) {
        this.algorithmType = algorithmType;
    }
    
    public Integer getKeySize() {
        return keySize;
    }
    
    public void setKeySize(Integer keySize) {
        this.keySize = keySize;
    }
    
    public String getSecurityLevel() {
        return securityLevel;
    }
    
    public void setSecurityLevel(String securityLevel) {
        this.securityLevel = securityLevel;
    }
    
    public String getPublicKey() {
        return publicKey;
    }
    
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }
    
    public String getPrivateKeyEncrypted() {
        return privateKeyEncrypted;
    }
    
    public void setPrivateKeyEncrypted(String privateKeyEncrypted) {
        this.privateKeyEncrypted = privateKeyEncrypted;
    }
    
    public String getCertificateData() {
        return certificateData;
    }
    
    public void setCertificateData(String certificateData) {
        this.certificateData = certificateData;
    }
    
    public String getSignatureScheme() {
        return signatureScheme;
    }
    
    public void setSignatureScheme(String signatureScheme) {
        this.signatureScheme = signatureScheme;
    }
    
    public String getEncryptionScheme() {
        return encryptionScheme;
    }
    
    public void setEncryptionScheme(String encryptionScheme) {
        this.encryptionScheme = encryptionScheme;
    }
    
    public String getKeyExchangeProtocol() {
        return keyExchangeProtocol;
    }
    
    public void setKeyExchangeProtocol(String keyExchangeProtocol) {
        this.keyExchangeProtocol = keyExchangeProtocol;
    }
    
    public Boolean getHybridMode() {
        return hybridMode;
    }
    
    public void setHybridMode(Boolean hybridMode) {
        this.hybridMode = hybridMode;
    }
    
    public Integer getKeyLifetimeDays() {
        return keyLifetimeDays;
    }
    
    public void setKeyLifetimeDays(Integer keyLifetimeDays) {
        this.keyLifetimeDays = keyLifetimeDays;
    }
    
    public Integer getRotationIntervalDays() {
        return rotationIntervalDays;
    }
    
    public void setRotationIntervalDays(Integer rotationIntervalDays) {
        this.rotationIntervalDays = rotationIntervalDays;
    }
    
    public LocalDateTime getLastRotationTime() {
        return lastRotationTime;
    }
    
    public void setLastRotationTime(LocalDateTime lastRotationTime) {
        this.lastRotationTime = lastRotationTime;
    }
    
    public LocalDateTime getNextRotationTime() {
        return nextRotationTime;
    }
    
    public void setNextRotationTime(LocalDateTime nextRotationTime) {
        this.nextRotationTime = nextRotationTime;
    }
    
    public Integer getKeyVersion() {
        return keyVersion;
    }
    
    public void setKeyVersion(Integer keyVersion) {
        this.keyVersion = keyVersion;
    }
    
    public String getKeyFingerprint() {
        return keyFingerprint;
    }
    
    public void setKeyFingerprint(String keyFingerprint) {
        this.keyFingerprint = keyFingerprint;
    }
    
    public String getKeyStatus() {
        return keyStatus;
    }
    
    public void setKeyStatus(String keyStatus) {
        this.keyStatus = keyStatus;
    }
    
    public String getRevocationReason() {
        return revocationReason;
    }
    
    public void setRevocationReason(String revocationReason) {
        this.revocationReason = revocationReason;
    }
    
    public LocalDateTime getRevocationTime() {
        return revocationTime;
    }
    
    public void setRevocationTime(LocalDateTime revocationTime) {
        this.revocationTime = revocationTime;
    }
    
    public String getKeyMetadata() {
        return keyMetadata;
    }
    
    public void setKeyMetadata(String keyMetadata) {
        this.keyMetadata = keyMetadata;
    }
    
    public String getPerformanceMetrics() {
        return performanceMetrics;
    }
    
    public void setPerformanceMetrics(String performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
    }
    
    public String getComplianceTags() {
        return complianceTags;
    }
    
    public void setComplianceTags(String complianceTags) {
        this.complianceTags = complianceTags;
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
    
    public Boolean getIsDefault() {
        return isDefault;
    }
    
    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }
    
    public Boolean getEnabled() {
        return enabled;
    }
    
    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }
    
    // 实用方法
    
    /**
     * 检查密钥是否过期
     */
    public boolean isExpired() {
        if (lastRotationTime == null || keyLifetimeDays == null) {
            return false;
        }
        LocalDateTime expirationTime = lastRotationTime.plusDays(keyLifetimeDays);
        return LocalDateTime.now().isAfter(expirationTime);
    }
    
    /**
     * 检查是否需要轮换密钥
     */
    public boolean needsRotation() {
        if (lastRotationTime == null || rotationIntervalDays == null) {
            return false;
        }
        LocalDateTime nextRotation = lastRotationTime.plusDays(rotationIntervalDays);
        return LocalDateTime.now().isAfter(nextRotation);
    }
    
    /**
     * 更新密钥状态
     */
    public void updateStatus() {
        if ("REVOKED".equals(keyStatus) || "SUSPENDED".equals(keyStatus)) {
            return;
        }
        
        if (isExpired()) {
            this.keyStatus = "EXPIRED";
        } else {
            this.keyStatus = "ACTIVE";
        }
        
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 获取 NIST 安全级别描述
     */
    public String getSecurityLevelDescription() {
        switch (securityLevel) {
            case "Level 1":
                return "128-bit security (Comparable to AES-128)";
            case "Level 3":
                return "192-bit security (Comparable to AES-192)";
            case "Level 5":
                return "256-bit security (Comparable to AES-256)";
            default:
                return "Unknown security level";
        }
    }
    
    /**
     * 获取算法分类
     */
    public String getAlgorithmCategory() {
        if (algorithmType == null) {
            return "Unknown";
        }
        
        if (algorithmType.contains("Kyber") || algorithmType.contains("NTRU") || algorithmType.contains("SIKE")) {
            return "Lattice-based";
        } else if (algorithmType.contains("Dilithium") || algorithmType.contains("Falcon")) {
            return "Signature";
        } else if (algorithmType.contains("SPHINCS")) {
            return "Hash-based";
        } else if (algorithmType.contains("HQC") || algorithmType.contains("BIKE")) {
            return "Code-based";
        } else {
            return "Other";
        }
    }
    
    @Override
    public String toString() {
        return String.format("QuantumResistantEncryptionEntity{id=%d, userId=%d, algorithmType='%s', securityLevel='%s', keySize=%d, status='%s'}",
                id, userId, algorithmType, securityLevel, keySize, keyStatus);
    }
}