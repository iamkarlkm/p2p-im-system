package com.im.system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 量子安全证书实体
 * 基于NIST PQC标准的证书颁发机构
 * 
 * @since 2026-03-25
 * @version 1.0.0
 */
@Entity
@Table(name = "quantum_secure_certificates", 
       indexes = {
           @Index(name = "idx_cert_serial_number", columnList = "serialNumber"),
           @Index(name = "idx_cert_issuer_subject", columnList = "issuerName, subjectName"),
           @Index(name = "idx_cert_status_expiry", columnList = "certificateStatus, expiryDate"),
           @Index(name = "idx_cert_algorithm_type", columnList = "signatureAlgorithm, algorithmType")
       })
public class QuantumSecureCertificateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", nullable = false, updatable = false)
    private UUID id;

    /**
     * 证书序列号（唯一标识）
     * 格式：QSC-{YYYYMMDD}-{8位随机字符}
     */
    @Column(name = "serial_number", nullable = false, unique = true, length = 64)
    private String serialNumber;

    /**
     * 证书颁发者名称
     * 格式：CN=IM Quantum Root CA, O=IM System, C=CN
     */
    @Column(name = "issuer_name", nullable = false, length = 512)
    private String issuerName;

    /**
     * 证书主题名称
     * 格式：CN={用户名}, OU={部门}, O={组织}, C={国家}
     */
    @Column(name = "subject_name", nullable = false, length = 512)
    private String subjectName;

    /**
     * 证书主体公钥指纹（SHA-256）
     */
    @Column(name = "subject_public_key_fingerprint", nullable = false, length = 64)
    private String subjectPublicKeyFingerprint;

    /**
     * 签名算法类型
     * 示例：CRYSTALS-Dilithium3、FALCON-512、SPHINCS+-SHAKE256-256f-simple
     */
    @Column(name = "signature_algorithm", nullable = false, length = 64)
    private String signatureAlgorithm;

    /**
     * 加密算法类型
     * 支持：RSA、ECC、CRYSTALS-Kyber768、NTRU-HRSS-KEM、SIKE
     */
    @Column(name = "algorithm_type", nullable = false, length = 32)
    private String algorithmType;

    /**
     * 证书状态
     * VALID - 有效
     * REVOKED - 已撤销
     * EXPIRED - 已过期
     * SUSPENDED - 已暂停
     * PENDING - 待批准
     */
    @Enumerated(EnumType.STRING)
    @Column(name = "certificate_status", nullable = false, length = 20)
    private CertificateStatus certificateStatus = CertificateStatus.PENDING;

    /**
     * 证书有效起始时间
     */
    @Column(name = "valid_from", nullable = false)
    private LocalDateTime validFrom;

    /**
     * 证书有效结束时间
     */
    @Column(name = "expiry_date", nullable = false)
    private LocalDateTime expiryDate;

    /**
     * 证书链路径
     * JSON格式存储上级证书ID数组
     */
    @Column(name = "certificate_chain", columnDefinition = "TEXT")
    private String certificateChain;

    /**
     * 扩展字段（X.509 v3扩展）
     * 包含：密钥用法、增强密钥用法、基本约束、CRL分发点等
     */
    @Column(name = "extensions", columnDefinition = "TEXT")
    private String extensions;

    /**
     * 证书撤销原因（如果已撤销）
     * UNSPECIFIED、KEY_COMPROMISE、CA_COMPROMISE、AFFILIATION_CHANGED、SUPERSEDED、CESSATION_OF_OPERATION
     */
    @Column(name = "revocation_reason", length = 32)
    private String revocationReason;

    /**
     * 证书撤销时间
     */
    @Column(name = "revocation_date")
    private LocalDateTime revocationDate;

    /**
     * 证书签名值（Base64编码）
     */
    @Column(name = "signature_value", columnDefinition = "TEXT")
    private String signatureValue;

    /**
     * 证书PEM格式（完整证书）
     */
    @Column(name = "pem_certificate", columnDefinition = "TEXT")
    private String pemCertificate;

    /**
     * 证书DER格式（Base64编码）
     */
    @Lob
    @Column(name = "der_certificate")
    private byte[] derCertificate;

    /**
     * 证书透明度日志条目ID
     */
    @Column(name = "certificate_transparency_id", length = 128)
    private String certificateTransparencyId;

    /**
     * 证书元数据
     * 包含：创建者、审核信息、策略OID等
     */
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata;

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

    // 枚举：证书状态
    public enum CertificateStatus {
        VALID,           // 有效
        REVOKED,         // 已撤销
        EXPIRED,         // 已过期
        SUSPENDED,       // 已暂停
        PENDING          // 待批准
    }

    // 构造函数
    public QuantumSecureCertificateEntity() {
    }

    public QuantumSecureCertificateEntity(String serialNumber, String issuerName, String subjectName,
                                        String subjectPublicKeyFingerprint, String signatureAlgorithm,
                                        String algorithmType, LocalDateTime validFrom, LocalDateTime expiryDate) {
        this.serialNumber = serialNumber;
        this.issuerName = issuerName;
        this.subjectName = subjectName;
        this.subjectPublicKeyFingerprint = subjectPublicKeyFingerprint;
        this.signatureAlgorithm = signatureAlgorithm;
        this.algorithmType = algorithmType;
        this.validFrom = validFrom;
        this.expiryDate = expiryDate;
        this.certificateStatus = CertificateStatus.PENDING;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectPublicKeyFingerprint() {
        return subjectPublicKeyFingerprint;
    }

    public void setSubjectPublicKeyFingerprint(String subjectPublicKeyFingerprint) {
        this.subjectPublicKeyFingerprint = subjectPublicKeyFingerprint;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public String getAlgorithmType() {
        return algorithmType;
    }

    public void setAlgorithmType(String algorithmType) {
        this.algorithmType = algorithmType;
    }

    public CertificateStatus getCertificateStatus() {
        return certificateStatus;
    }

    public void setCertificateStatus(CertificateStatus certificateStatus) {
        this.certificateStatus = certificateStatus;
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

    public String getCertificateChain() {
        return certificateChain;
    }

    public void setCertificateChain(String certificateChain) {
        this.certificateChain = certificateChain;
    }

    public String getExtensions() {
        return extensions;
    }

    public void setExtensions(String extensions) {
        this.extensions = extensions;
    }

    public String getRevocationReason() {
        return revocationReason;
    }

    public void setRevocationReason(String revocationReason) {
        this.revocationReason = revocationReason;
    }

    public LocalDateTime getRevocationDate() {
        return revocationDate;
    }

    public void setRevocationDate(LocalDateTime revocationDate) {
        this.revocationDate = revocationDate;
    }

    public String getSignatureValue() {
        return signatureValue;
    }

    public void setSignatureValue(String signatureValue) {
        this.signatureValue = signatureValue;
    }

    public String getPemCertificate() {
        return pemCertificate;
    }

    public void setPemCertificate(String pemCertificate) {
        this.pemCertificate = pemCertificate;
    }

    public byte[] getDerCertificate() {
        return derCertificate;
    }

    public void setDerCertificate(byte[] derCertificate) {
        this.derCertificate = derCertificate;
    }

    public String getCertificateTransparencyId() {
        return certificateTransparencyId;
    }

    public void setCertificateTransparencyId(String certificateTransparencyId) {
        this.certificateTransparencyId = certificateTransparencyId;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return certificateStatus == CertificateStatus.VALID && 
               now.isAfter(validFrom) && 
               now.isBefore(expiryDate);
    }

    public boolean isExpired() {
        LocalDateTime now = LocalDateTime.now();
        return now.isAfter(expiryDate);
    }

    public boolean isRevoked() {
        return certificateStatus == CertificateStatus.REVOKED;
    }

    public void revoke(String reason) {
        this.certificateStatus = CertificateStatus.REVOKED;
        this.revocationReason = reason;
        this.revocationDate = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void suspend() {
        this.certificateStatus = CertificateStatus.SUSPENDED;
        this.updatedAt = LocalDateTime.now();
    }

    public void activate() {
        if (isExpired()) {
            throw new IllegalStateException("Cannot activate an expired certificate");
        }
        this.certificateStatus = CertificateStatus.VALID;
        this.updatedAt = LocalDateTime.now();
    }

    public void renew(LocalDateTime newExpiryDate) {
        this.expiryDate = newExpiryDate;
        this.certificateStatus = CertificateStatus.VALID;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "QuantumSecureCertificateEntity{" +
                "id=" + id +
                ", serialNumber='" + serialNumber + '\'' +
                ", issuerName='" + issuerName + '\'' +
                ", subjectName='" + subjectName + '\'' +
                ", signatureAlgorithm='" + signatureAlgorithm + '\'' +
                ", certificateStatus=" + certificateStatus +
                ", validFrom=" + validFrom +
                ", expiryDate=" + expiryDate +
                '}';
    }
}