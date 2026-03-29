package com.imsystem.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * 后量子签名实体
 * 
 * 基于后量子密码学的数字签名配置和状态管理
 * 支持 NIST 标准化签名算法: CRYSTALS-Dilithium, FALCON, SPHINCS+
 * 
 * 作者: 编程开发代理
 * 创建时间: 2026-03-24 10:02
 */
@Entity
@Table(name = "post_quantum_signature")
public class PostQuantumSignatureEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "signature_id", nullable = false, length = 64, unique = true)
    private String signatureId; // 签名唯一标识符
    
    @Column(name = "algorithm", nullable = false, length = 50)
    private String algorithm; // Dilithium2, Dilithium3, Falcon-512, Falcon-1024, SPHINCS+-SHAKE256
    
    @Column(name = "key_size", nullable = false)
    private Integer keySize; // 密钥大小
    
    @Column(name = "security_level", nullable = false)
    private String securityLevel; // Level 1, Level 3, Level 5 (NIST 标准化)
    
    @Column(name = "public_key", columnDefinition = "TEXT")
    private String publicKey;
    
    @Column(name = "private_key_encrypted", columnDefinition = "TEXT")
    private String privateKeyEncrypted; // 加密后的私钥
    
    @Column(name = "signature_scheme", length = 50)
    private String signatureScheme; // Randomized, Deterministic
    
    @Column(name = "signature_size_bytes")
    private Integer signatureSizeBytes; // 签名大小（字节）
    
    @Column(name = "public_key_size_bytes")
    private Integer publicKeySizeBytes; // 公钥大小（字节）
    
    @Column(name = "private_key_size_bytes")
    private Integer privateKeySizeBytes; // 私钥大小（字节）
    
    @Column(name = "signing_time_ms")
    private Long signingTimeMs; // 签名时间（毫秒）
    
    @Column(name = "verification_time_ms")
    private Long verificationTimeMs; // 验证时间（毫秒）
    
    @Column(name = "key_lifetime_days")
    private Integer keyLifetimeDays = 365;
    
    @Column(name = "key_version", nullable = false)
    private Integer keyVersion = 1;
    
    @Column(name = "certificate_chain", columnDefinition = "TEXT")
    private String certificateChain; // X.509 证书链
    
    @Column(name = "root_ca_thumbprint", length = 64)
    private String rootCaThumbprint; // 根 CA 指纹
    
    @Column(name = "timestamp_service_url", length = 200)
    private String timestampServiceUrl; // 时间戳服务 URL
    
    @Column(name = "timestamp_token", columnDefinition = "TEXT")
    private String timestampToken; // RFC 3161 时间戳令牌
    
    @Column(name = "timestamp_verified")
    private Boolean timestampVerified = false;
    
    @Column(name = "timestamp_verification_time")
    private LocalDateTime timestampVerificationTime;
    
    @Column(name = "signature_purpose", length = 50)
    private String signaturePurpose; // Authentication, Non-repudiation, Integrity
    
    @Column(name = "signed_document_hash", length = 64)
    private String signedDocumentHash; // 被签名文档的哈希
    
    @Column(name = "signature_data", columnDefinition = "TEXT")
    private String signatureData; // Base64 编码的签名数据
    
    @Column(name = "signature_format", length = 30)
    private String signatureFormat = "RAW"; // RAW, DER, PEM, CMS
    
    @Column(name = "revocation_status", length = 20)
    private String revocationStatus = "VALID"; // VALID, REVOKED, SUSPENDED, EXPIRED
    
    @Column(name = "revocation_time")
    private LocalDateTime revocationTime;
    
    @Column(name = "revocation_reason", length = 100)
    private String revocationReason;
    
    @Column(name = "ocsp_response", columnDefinition = "TEXT")
    private String ocspResponse; // OCSP 响应数据
    
    @Column(name = "crl_distribution_points", columnDefinition = "TEXT")
    private String crlDistributionPoints; // CRL 分发点
    
    @Column(name = "audit_trail", columnDefinition = "TEXT")
    private String auditTrail; // 审计轨迹数据
    
    @Column(name = "compliance_info", columnDefinition = "JSON")
    private String complianceInfo; // 合规性信息
    
    @Column(name = "performance_metrics", columnDefinition = "JSON")
    private String performanceMetrics; // 性能指标
    
    @Column(name = "error_count")
    private Integer errorCount = 0;
    
    @Column(name = "last_error_message", columnDefinition = "TEXT")
    private String lastErrorMessage;
    
    @Column(name = "last_error_time")
    private LocalDateTime lastErrorTime;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @Column(name = "is_default_signature", nullable = false)
    private Boolean isDefaultSignature = false;
    
    // 构造方法
    public PostQuantumSignatureEntity() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        this.signatureId = generateSignatureId();
    }
    
    /**
     * 生成签名唯一标识符
     */
    private String generateSignatureId() {
        return String.format("SIG-%s-%d", 
            LocalDateTime.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMddHHmmss")),
            System.currentTimeMillis() % 10000);
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
    
    public String getSignatureId() {
        return signatureId;
    }
    
    public void setSignatureId(String signatureId) {
        this.signatureId = signatureId;
    }
    
    public String getAlgorithm() {
        return algorithm;
    }
    
    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
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
    
    public String getSignatureScheme() {
        return signatureScheme;
    }
    
    public void setSignatureScheme(String signatureScheme) {
        this.signatureScheme = signatureScheme;
    }
    
    public Integer getSignatureSizeBytes() {
        return signatureSizeBytes;
    }
    
    public void setSignatureSizeBytes(Integer signatureSizeBytes) {
        this.signatureSizeBytes = signatureSizeBytes;
    }
    
    public Integer getPublicKeySizeBytes() {
        return publicKeySizeBytes;
    }
    
    public void setPublicKeySizeBytes(Integer publicKeySizeBytes) {
        this.publicKeySizeBytes = publicKeySizeBytes;
    }
    
    public Integer getPrivateKeySizeBytes() {
        return privateKeySizeBytes;
    }
    
    public void setPrivateKeySizeBytes(Integer privateKeySizeBytes) {
        this.privateKeySizeBytes = privateKeySizeBytes;
    }
    
    public Long getSigningTimeMs() {
        return signingTimeMs;
    }
    
    public void setSigningTimeMs(Long signingTimeMs) {
        this.signingTimeMs = signingTimeMs;
    }
    
    public Long getVerificationTimeMs() {
        return verificationTimeMs;
    }
    
    public void setVerificationTimeMs(Long verificationTimeMs) {
        this.verificationTimeMs = verificationTimeMs;
    }
    
    public Integer getKeyLifetimeDays() {
        return keyLifetimeDays;
    }
    
    public void setKeyLifetimeDays(Integer keyLifetimeDays) {
        this.keyLifetimeDays = keyLifetimeDays;
    }
    
    public Integer getKeyVersion() {
        return keyVersion;
    }
    
    public void setKeyVersion(Integer keyVersion) {
        this.keyVersion = keyVersion;
    }
    
    public String getCertificateChain() {
        return certificateChain;
    }
    
    public void setCertificateChain(String certificateChain) {
        this.certificateChain = certificateChain;
    }
    
    public String getRootCaThumbprint() {
        return rootCaThumbprint;
    }
    
    public void setRootCaThumbprint(String rootCaThumbprint) {
        this.rootCaThumbprint = rootCaThumbprint;
    }
    
    public String getTimestampServiceUrl() {
        return timestampServiceUrl;
    }
    
    public void setTimestampServiceUrl(String timestampServiceUrl) {
        this.timestampServiceUrl = timestampServiceUrl;
    }
    
    public String getTimestampToken() {
        return timestampToken;
    }
    
    public void setTimestampToken(String timestampToken) {
        this.timestampToken = timestampToken;
    }
    
    public Boolean getTimestampVerified() {
        return timestampVerified;
    }
    
    public void setTimestampVerified(Boolean timestampVerified) {
        this.timestampVerified = timestampVerified;
    }
    
    public LocalDateTime getTimestampVerificationTime() {
        return timestampVerificationTime;
    }
    
    public void setTimestampVerificationTime(LocalDateTime timestampVerificationTime) {
        this.timestampVerificationTime = timestampVerificationTime;
    }
    
    public String getSignaturePurpose() {
        return signaturePurpose;
    }
    
    public void setSignaturePurpose(String signaturePurpose) {
        this.signaturePurpose = signaturePurpose;
    }
    
    public String getSignedDocumentHash() {
        return signedDocumentHash;
    }
    
    public void setSignedDocumentHash(String signedDocumentHash) {
        this.signedDocumentHash = signedDocumentHash;
    }
    
    public String getSignatureData() {
        return signatureData;
    }
    
    public void setSignatureData(String signatureData) {
        this.signatureData = signatureData;
    }
    
    public String getSignatureFormat() {
        return signatureFormat;
    }
    
    public void setSignatureFormat(String signatureFormat) {
        this.signatureFormat = signatureFormat;
    }
    
    public String getRevocationStatus() {
        return revocationStatus;
    }
    
    public void setRevocationStatus(String revocationStatus) {
        this.revocationStatus = revocationStatus;
    }
    
    public LocalDateTime getRevocationTime() {
        return revocationTime;
    }
    
    public void setRevocationTime(LocalDateTime revocationTime) {
        this.revocationTime = revocationTime;
    }
    
    public String getRevocationReason() {
        return revocationReason;
    }
    
    public void setRevocationReason(String revocationReason) {
        this.revocationReason = revocationReason;
    }
    
    public String getOcspResponse() {
        return ocspResponse;
    }
    
    public void setOcspResponse(String ocspResponse) {
        this.ocspResponse = ocspResponse;
    }
    
    public String getCrlDistributionPoints() {
        return crlDistributionPoints;
    }
    
    public void setCrlDistributionPoints(String crlDistributionPoints) {
        this.crlDistributionPoints = crlDistributionPoints;
    }
    
    public String getAuditTrail() {
        return auditTrail;
    }
    
    public void setAuditTrail(String auditTrail) {
        this.auditTrail = auditTrail;
    }
    
    public String getComplianceInfo() {
        return complianceInfo;
    }
    
    public void setComplianceInfo(String complianceInfo) {
        this.complianceInfo = complianceInfo;
    }
    
    public String getPerformanceMetrics() {
        return performanceMetrics;
    }
    
    public void setPerformanceMetrics(String performanceMetrics) {
        this.performanceMetrics = performanceMetrics;
    }
    
    public Integer getErrorCount() {
        return errorCount;
    }
    
    public void setErrorCount(Integer errorCount) {
        this.errorCount = errorCount;
    }
    
    public String getLastErrorMessage() {
        return lastErrorMessage;
    }
    
    public void setLastErrorMessage(String lastErrorMessage) {
        this.lastErrorMessage = lastErrorMessage;
    }
    
    public LocalDateTime getLastErrorTime() {
        return lastErrorTime;
    }
    
    public void setLastErrorTime(LocalDateTime lastErrorTime) {
        this.lastErrorTime = lastErrorTime;
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
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public Boolean getIsDefaultSignature() {
        return isDefaultSignature;
    }
    
    public void setIsDefaultSignature(Boolean isDefaultSignature) {
        this.isDefaultSignature = isDefaultSignature;
    }
    
    // 实用方法
    
    /**
     * 检查签名是否过期
     */
    public boolean isExpired() {
        if (createdAt == null || keyLifetimeDays == null) {
            return false;
        }
        LocalDateTime expirationTime = createdAt.plusDays(keyLifetimeDays);
        return LocalDateTime.now().isAfter(expirationTime);
    }
    
    /**
     * 检查签名是否有效
     */
    public boolean isValid() {
        if (!isActive) {
            return false;
        }
        
        if ("REVOKED".equals(revocationStatus) || "SUSPENDED".equals(revocationStatus)) {
            return false;
        }
        
        if (isExpired()) {
            return false;
        }
        
        return timestampVerified != null && timestampVerified;
    }
    
    /**
     * 更新签名状态
     */
    public void updateStatus() {
        if (isExpired()) {
            this.revocationStatus = "EXPIRED";
            this.isActive = false;
        }
        
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 记录错误
     */
    public void recordError(String errorMessage) {
        this.errorCount++;
        this.lastErrorMessage = errorMessage;
        this.lastErrorTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * 获取算法类型描述
     */
    public String getAlgorithmDescription() {
        if (algorithm == null) {
            return "Unknown algorithm";
        }
        
        switch (algorithm) {
            case "Dilithium2":
                return "CRYSTALS-Dilithium (Level 2 security)";
            case "Dilithium3":
                return "CRYSTALS-Dilithium (Level 3 security)";
            case "Falcon-512":
                return "FALCON (512-bit security)";
            case "Falcon-1024":
                return "FALCON (1024-bit security)";
            case "SPHINCS+-SHAKE256":
                return "SPHINCS+ with SHAKE256 hash";
            default:
                return algorithm;
        }
    }
    
    /**
     * 获取 NIST 推荐状态
     */
    public String getNistStatus() {
        if (algorithm == null) {
            return "Not NIST recommended";
        }
        
        if (algorithm.contains("Dilithium") || algorithm.contains("Falcon") || algorithm.contains("SPHINCS")) {
            return "NIST Standardized (Round 4 Finalist)";
        } else {
            return "Experimental / Under evaluation";
        }
    }
    
    /**
     * 计算签名效率评分
     */
    public double calculateEfficiencyScore() {
        double score = 100.0;
        
        // 基于签名时间扣分
        if (signingTimeMs != null && signingTimeMs > 100) {
            score -= (signingTimeMs - 100) * 0.1;
        }
        
        // 基于验证时间扣分
        if (verificationTimeMs != null && verificationTimeMs > 50) {
            score -= (verificationTimeMs - 50) * 0.2;
        }
        
        // 基于签名大小扣分
        if (signatureSizeBytes != null && signatureSizeBytes > 5000) {
            score -= (signatureSizeBytes - 5000) * 0.01;
        }
        
        // 基于错误数量扣分
        if (errorCount != null) {
            score -= errorCount * 5.0;
        }
        
        return Math.max(0, score);
    }
    
    @Override
    public String toString() {
        return String.format("PostQuantumSignatureEntity{id=%d, signatureId='%s', algorithm='%s', securityLevel='%s', status='%s'}",
                id, signatureId, algorithm, securityLevel, revocationStatus);
    }
}