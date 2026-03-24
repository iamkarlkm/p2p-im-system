package com.imsystem.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 可验证凭证实体 (W3C Verifiable Credentials 标准)
 * 支持 JSON-LD、JWT 等多种凭证格式
 */
@Entity
@Table(name = "verifiable_credentials", indexes = {
    @Index(name = "idx_credential_id", columnList = "credential_id", unique = true),
    @Index(name = "idx_issuer_did", columnList = "issuer_did"),
    @Index(name = "idx_holder_did", columnList = "holder_did"),
    @Index(name = "idx_type", columnList = "type"),
    @Index(name = "idx_status", columnList = "status"),
    @Index(name = "idx_expires_at", columnList = "expires_at")
})
public class VerifiableCredentialEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "credential_id", nullable = false, length = 512)
    private String credentialId; // 凭证唯一标识符

    @Column(name = "credential_type", nullable = false, length = 100)
    private String credentialType; // 凭证类型: VerifiableCredential, VerifiablePresentation

    @Column(name = "format", nullable = false, length = 50)
    private String format; // 格式: json-ld, jwt, cwt, sd-jwt

    @Column(name = "issuer_did", nullable = false, length = 512)
    private String issuerDid; // 发行者 DID

    @Column(name = "issuer_name", length = 255)
    private String issuerName; // 发行者名称

    @Column(name = "holder_did", nullable = false, length = 512)
    private String holderDid; // 持有者 DID

    @Column(name = "holder_name", length = 255)
    private String holderName; // 持有者名称

    @Column(name = "credential_subject", columnDefinition = "JSON")
    private String credentialSubject; // 凭证主体数据 (JSON)

    @Column(name = "credential_schema", columnDefinition = "JSON")
    private String credentialSchema; // 凭证模式定义 (JSON)

    @Column(name = "credential_status", columnDefinition = "JSON")
    private String credentialStatus; // 凭证状态信息 (JSON)

    @Column(name = "proof", columnDefinition = "JSON")
    private String proof; // 凭证证明 (JSON)

    @Column(name = "proof_type", length = 100)
    private String proofType; // 证明类型: JsonWebSignature2020, Ed25519Signature2018, BbsBlsSignature2020

    @Column(name = "signature_algorithm", length = 100)
    private String signatureAlgorithm; // 签名算法: Ed25519, ES256K, BLS12-381

    @Column(name = "verification_method", length = 512)
    private String verificationMethod; // 验证方法

    @Column(name = "issuance_date", nullable = false)
    private LocalDateTime issuanceDate; // 发行日期

    @Column(name = "expiration_date")
    private LocalDateTime expirationDate; // 过期日期

    @Column(name = "valid_from")
    private LocalDateTime validFrom; // 有效期开始

    @Column(name = "valid_until")
    private LocalDateTime validUntil; // 有效期截止

    @Column(name = "revocation_date")
    private LocalDateTime revocationDate; // 吊销日期

    @Column(name = "status", length = 20)
    private String status = "ISSUED"; // 状态: ISSUED, REVOKED, SUSPENDED, EXPIRED

    @Column(name = "credential_tags", columnDefinition = "JSON")
    private String credentialTags; // 凭证标签，用于分类和搜索

    @Column(name = "credential_metadata", columnDefinition = "JSON")
    private String credentialMetadata; // 凭证元数据

    @Column(name = "verification_count")
    private Integer verificationCount = 0; // 验证次数

    @Column(name = "last_verified_at")
    private LocalDateTime lastVerifiedAt; // 最后验证时间

    @Column(name = "verification_result", columnDefinition = "JSON")
    private String verificationResult; // 验证结果历史

    @Column(name = "zkp_enabled")
    private Boolean zkpEnabled = false; // 是否支持零知识证明

    @Column(name = "zkp_schema", columnDefinition = "JSON")
    private String zkpSchema; // ZKP 模式定义

    @Column(name = "selective_disclosure_allowed")
    private Boolean selectiveDisclosureAllowed = false; // 是否允许选择性披露

    @Column(name = "credential_hash", length = 256)
    private String credentialHash; // 凭证哈希值，用于链上存储

    @Column(name = "blockchain_tx_hash", length = 256)
    private String blockchainTxHash; // 区块链交易哈希

    @Column(name = "stored_on_chain")
    private Boolean storedOnChain = false; // 是否存储在链上

    @Column(name = "chain_type", length = 50)
    private String chainType; // 链类型

    @Column(name = "credential_size")
    private Integer credentialSize = 0; // 凭证大小（字节）

    @Column(name = "compression_enabled")
    private Boolean compressionEnabled = false; // 是否启用压缩

    @Column(name = "encrypted")
    private Boolean encrypted = false; // 凭证是否加密

    @Column(name = "encryption_algorithm", length = 50)
    private String encryptionAlgorithm; // 加密算法

    @Column(name = "confidentiality_level", length = 20)
    private String confidentialityLevel = "PUBLIC"; // 机密级别: PUBLIC, PRIVATE, SENSITIVE

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "version")
    private Integer version = 1;

    @PrePersist
    protected void onCreate() {
        if (this.credentialId == null) {
            this.credentialId = "urn:uuid:" + UUID.randomUUID().toString();
        }
        this.issuanceDate = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getCredentialId() {
        return credentialId;
    }

    public void setCredentialId(String credentialId) {
        this.credentialId = credentialId;
    }

    public String getCredentialType() {
        return credentialType;
    }

    public void setCredentialType(String credentialType) {
        this.credentialType = credentialType;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public String getIssuerDid() {
        return issuerDid;
    }

    public void setIssuerDid(String issuerDid) {
        this.issuerDid = issuerDid;
    }

    public String getIssuerName() {
        return issuerName;
    }

    public void setIssuerName(String issuerName) {
        this.issuerName = issuerName;
    }

    public String getHolderDid() {
        return holderDid;
    }

    public void setHolderDid(String holderDid) {
        this.holderDid = holderDid;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getCredentialSubject() {
        return credentialSubject;
    }

    public void setCredentialSubject(String credentialSubject) {
        this.credentialSubject = credentialSubject;
    }

    public String getCredentialSchema() {
        return credentialSchema;
    }

    public void setCredentialSchema(String credentialSchema) {
        this.credentialSchema = credentialSchema;
    }

    public String getCredentialStatus() {
        return credentialStatus;
    }

    public void setCredentialStatus(String credentialStatus) {
        this.credentialStatus = credentialStatus;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public String getProofType() {
        return proofType;
    }

    public void setProofType(String proofType) {
        this.proofType = proofType;
    }

    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }

    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }

    public String getVerificationMethod() {
        return verificationMethod;
    }

    public void setVerificationMethod(String verificationMethod) {
        this.verificationMethod = verificationMethod;
    }

    public LocalDateTime getIssuanceDate() {
        return issuanceDate;
    }

    public void setIssuanceDate(LocalDateTime issuanceDate) {
        this.issuanceDate = issuanceDate;
    }

    public LocalDateTime getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(LocalDateTime expirationDate) {
        this.expirationDate = expirationDate;
    }

    public LocalDateTime getValidFrom() {
        return validFrom;
    }

    public void setValidFrom(LocalDateTime validFrom) {
        this.validFrom = validFrom;
    }

    public LocalDateTime getValidUntil() {
        return validUntil;
    }

    public void setValidUntil(LocalDateTime validUntil) {
        this.validUntil = validUntil;
    }

    public LocalDateTime getRevocationDate() {
        return revocationDate;
    }

    public void setRevocationDate(LocalDateTime revocationDate) {
        this.revocationDate = revocationDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCredentialTags() {
        return credentialTags;
    }

    public void setCredentialTags(String credentialTags) {
        this.credentialTags = credentialTags;
    }

    public String getCredentialMetadata() {
        return credentialMetadata;
    }

    public void setCredentialMetadata(String credentialMetadata) {
        this.credentialMetadata = credentialMetadata;
    }

    public Integer getVerificationCount() {
        return verificationCount;
    }

    public void setVerificationCount(Integer verificationCount) {
        this.verificationCount = verificationCount;
    }

    public LocalDateTime getLastVerifiedAt() {
        return lastVerifiedAt;
    }

    public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) {
        this.lastVerifiedAt = lastVerifiedAt;
    }

    public String getVerificationResult() {
        return verificationResult;
    }

    public void setVerificationResult(String verificationResult) {
        this.verificationResult = verificationResult;
    }

    public Boolean getZkpEnabled() {
        return zkpEnabled;
    }

    public void setZkpEnabled(Boolean zkpEnabled) {
        this.zkpEnabled = zkpEnabled;
    }

    public String getZkpSchema() {
        return zkpSchema;
    }

    public void setZkpSchema(String zkpSchema) {
        this.zkpSchema = zkpSchema;
    }

    public Boolean getSelectiveDisclosureAllowed() {
        return selectiveDisclosureAllowed;
    }

    public void setSelectiveDisclosureAllowed(Boolean selectiveDisclosureAllowed) {
        this.selectiveDisclosureAllowed = selectiveDisclosureAllowed;
    }

    public String getCredentialHash() {
        return credentialHash;
    }

    public void setCredentialHash(String credentialHash) {
        this.credentialHash = credentialHash;
    }

    public String getBlockchainTxHash() {
        return blockchainTxHash;
    }

    public void setBlockchainTxHash(String blockchainTxHash) {
        this.blockchainTxHash = blockchainTxHash;
    }

    public Boolean getStoredOnChain() {
        return storedOnChain;
    }

    public void setStoredOnChain(Boolean storedOnChain) {
        this.storedOnChain = storedOnChain;
    }

    public String getChainType() {
        return chainType;
    }

    public void setChainType(String chainType) {
        this.chainType = chainType;
    }

    public Integer getCredentialSize() {
        return credentialSize;
    }

    public void setCredentialSize(Integer credentialSize) {
        this.credentialSize = credentialSize;
    }

    public Boolean getCompressionEnabled() {
        return compressionEnabled;
    }

    public void setCompressionEnabled(Boolean compressionEnabled) {
        this.compressionEnabled = compressionEnabled;
    }

    public Boolean getEncrypted() {
        return encrypted;
    }

    public void setEncrypted(Boolean encrypted) {
        this.encrypted = encrypted;
    }

    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getConfidentialityLevel() {
        return confidentialityLevel;
    }

    public void setConfidentialityLevel(String confidentialityLevel) {
        this.confidentialityLevel = confidentialityLevel;
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

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    @Override
    public String toString() {
        return "VerifiableCredentialEntity{" +
                "id=" + id +
                ", credentialId='" + credentialId + '\'' +
                ", credentialType='" + credentialType + '\'' +
                ", issuerDid='" + issuerDid + '\'' +
                ", holderDid='" + holderDid + '\'' +
                ", status='" + status + '\'' +
                ", issuanceDate=" + issuanceDate +
                '}';
    }
}