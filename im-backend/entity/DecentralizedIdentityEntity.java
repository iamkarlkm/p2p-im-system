package com.imsystem.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 去中心化身份实体 (W3C DID 标准兼容)
 * 支持以太坊、Polygon、Solana 等多链身份绑定
 */
@Entity
@Table(name = "decentralized_identities", indexes = {
    @Index(name = "idx_did", columnList = "did", unique = true),
    @Index(name = "idx_user_id", columnList = "user_id"),
    @Index(name = "idx_blockchain_address", columnList = "blockchain_address"),
    @Index(name = "idx_status", columnList = "status")
})
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DecentralizedIdentityEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "did", nullable = false, length = 512)
    private String did; // W3C DID 标识符，如: did:ethr:0x1234...

    @Column(name = "user_id", nullable = false)
    private Long userId; // 关联的用户ID

    @Column(name = "blockchain_type", nullable = false, length = 50)
    private String blockchainType; // 区块链类型: ethereum, polygon, solana, bitcoin, etc.

    @Column(name = "blockchain_address", nullable = false, length = 256)
    private String blockchainAddress; // 区块链地址

    @Column(name = "public_key", columnDefinition = "TEXT")
    private String publicKey; // 公钥 (PEM 格式)

    @Column(name = "private_key_encrypted", columnDefinition = "TEXT")
    private String privateKeyEncrypted; // 加密存储的私钥

    @Column(name = "key_type", length = 50)
    private String keyType; // 密钥类型: secp256k1, ed25519, bls12381

    @Column(name = "did_document", columnDefinition = "JSON")
    private String didDocument; // W3C DID 文档 (JSON)

    @Column(name = "verification_methods", columnDefinition = "JSON")
    private String verificationMethods; // 验证方法列表 (JSON)

    @Column(name = "service_endpoints", columnDefinition = "JSON")
    private String serviceEndpoints; // 服务端点 (JSON)

    @Column(name = "verifiable_credentials", columnDefinition = "JSON")
    private String verifiableCredentials; // 可验证凭证列表 (JSON)

    @Column(name = "reputation_score", precision = 5, scale = 2)
    private Double reputationScore = 0.0; // 链上声誉评分

    @Column(name = "identity_proof_level")
    private Integer identityProofLevel = 0; // 身份证明等级 (0-5)

    @Column(name = "zkp_enabled")
    private Boolean zkpEnabled = false; // 是否启用零知识证明

    @Column(name = "cross_chain_synced")
    private Boolean crossChainSynced = false; // 是否跨链同步

    @Column(name = "active")
    private Boolean active = true; // 是否激活

    @Column(name = "status", length = 20)
    private String status = "PENDING"; // 状态: PENDING, VERIFIED, REVOKED, SUSPENDED

    @Column(name = "last_verified_at")
    private LocalDateTime lastVerifiedAt;

    @Column(name = "verification_expires_at")
    private LocalDateTime verificationExpiresAt;

    @Column(name = "metadata", columnDefinition = "JSON")
    private String metadata; // 扩展元数据

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "version")
    private Integer version = 1; // 乐观锁版本

    @PrePersist
    protected void onCreate() {
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

    public String getDid() {
        return did;
    }

    public void setDid(String did) {
        this.did = did;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getBlockchainType() {
        return blockchainType;
    }

    public void setBlockchainType(String blockchainType) {
        this.blockchainType = blockchainType;
    }

    public String getBlockchainAddress() {
        return blockchainAddress;
    }

    public void setBlockchainAddress(String blockchainAddress) {
        this.blockchainAddress = blockchainAddress;
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

    public String getKeyType() {
        return keyType;
    }

    public void setKeyType(String keyType) {
        this.keyType = keyType;
    }

    public String getDidDocument() {
        return didDocument;
    }

    public void setDidDocument(String didDocument) {
        this.didDocument = didDocument;
    }

    public String getVerificationMethods() {
        return verificationMethods;
    }

    public void setVerificationMethods(String verificationMethods) {
        this.verificationMethods = verificationMethods;
    }

    public String getServiceEndpoints() {
        return serviceEndpoints;
    }

    public void setServiceEndpoints(String serviceEndpoints) {
        this.serviceEndpoints = serviceEndpoints;
    }

    public String getVerifiableCredentials() {
        return verifiableCredentials;
    }

    public void setVerifiableCredentials(String verifiableCredentials) {
        this.verifiableCredentials = verifiableCredentials;
    }

    public Double getReputationScore() {
        return reputationScore;
    }

    public void setReputationScore(Double reputationScore) {
        this.reputationScore = reputationScore;
    }

    public Integer getIdentityProofLevel() {
        return identityProofLevel;
    }

    public void setIdentityProofLevel(Integer identityProofLevel) {
        this.identityProofLevel = identityProofLevel;
    }

    public Boolean getZkpEnabled() {
        return zkpEnabled;
    }

    public void setZkpEnabled(Boolean zkpEnabled) {
        this.zkpEnabled = zkpEnabled;
    }

    public Boolean getCrossChainSynced() {
        return crossChainSynced;
    }

    public void setCrossChainSynced(Boolean crossChainSynced) {
        this.crossChainSynced = crossChainSynced;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getLastVerifiedAt() {
        return lastVerifiedAt;
    }

    public void setLastVerifiedAt(LocalDateTime lastVerifiedAt) {
        this.lastVerifiedAt = lastVerifiedAt;
    }

    public LocalDateTime getVerificationExpiresAt() {
        return verificationExpiresAt;
    }

    public void setVerificationExpiresAt(LocalDateTime verificationExpiresAt) {
        this.verificationExpiresAt = verificationExpiresAt;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
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
        return "DecentralizedIdentityEntity{" +
                "id=" + id +
                ", did='" + did + '\'' +
                ", userId=" + userId +
                ", blockchainType='" + blockchainType + '\'' +
                ", blockchainAddress='" + blockchainAddress + '\'' +
                ", keyType='" + keyType + '\'' +
                ", reputationScore=" + reputationScore +
                ", status='" + status + '\'' +
                ", active=" + active +
                '}';
    }
}