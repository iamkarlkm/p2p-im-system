package com.im.system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 后量子签名实体
 * 基于后量子密码学的数字签名系统
 * 
 * 支持的签名算法：
 * 1. CRYSTALS-Dilithium (NIST标准化)
 * 2. FALCON (NIST标准化)
 * 3. SPHINCS+ (基于哈希的签名)
 * 4. Rainbow (多变量签名)
 * 5. Ed448 (传统椭圆曲线，具有量子抗性扩展)
 */
@Entity
@Table(name = "post_quantum_signature", 
       indexes = {
           @Index(name = "idx_signature_algorithm", columnList = "signatureAlgorithm"),
           @Index(name = "idx_user_id_signature", columnList = "userId"),
           @Index(name = "idx_created_at_signature", columnList = "createdAt"),
           @Index(name = "idx_verification_status", columnList = "verificationStatus")
       })
public class PostQuantumSignatureEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 100)
    private String signatureName;
    
    @Column(nullable = false, length = 50)
    private String signatureAlgorithm; // DILITHIUM, FALCON, SPHINCS_PLUS, RAINBOW, ED448
    
    @Column(nullable = false, length = 100)
    private String specificVariant; // Dilithium2, Dilithium3, Falcon-512, Falcon-1024, SPHINCS+-SHA256-128f-simple
    
    @Column(nullable = false)
    private Integer signatureSize; // 签名大小（字节）
    
    @Column(nullable = false)
    private Integer publicKeySize; // 公钥大小（字节）
    
    @Column(nullable = false)
    private Integer privateKeySize; // 私钥大小（字节）
    
    @Column(length = 5000)
    private String signatureData; // 实际的签名数据
    
    @Column(length = 5000)
    private String publicKeyData; // 公钥数据
    
    @Column(length = 5000)
    private String privateKeyData; // 私钥数据（加密存储）
    
    @Column(nullable = false)
    private UUID messageId; // 签名的消息ID
    
    @Column(nullable = false)
    private UUID userId; // 签名者用户ID
    
    @Column(nullable = false)
    private LocalDateTime signedAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private String verificationStatus = "PENDING"; // PENDING, VERIFIED, INVALID, REVOKED
    
    @Column
    private LocalDateTime verifiedAt;
    
    @Column(length = 1000)
    private String verificationNotes; // 验证备注
    
    @Column(nullable = false)
    private Integer securityLevel = 3; // 1-5级，1为最高安全
    
    @Column
    private Boolean isRevocable = true; // 是否可撤销
    
    @Column
    private Boolean supportsBatchVerification = false; // 是否支持批量验证
    
    @Column
    private Double verificationTimeMs; // 验证时间（毫秒）
    
    @Column
    private Double signatureGenerationTimeMs; // 签名生成时间（毫秒）
    
    @Column
    private Integer signatureCount = 1; // 签名次数
    
    @Column(length = 500)
    private String signaturePurpose; // 签名用途
    
    @Column
    private Boolean isTimestamped = true; // 是否带时间戳
    
    @Column(length = 1000)
    private String timestampAuthorityUrl; // 时间戳权威URL
    
    @Column
    private LocalDateTime timestampVerifiedAt; // 时间戳验证时间
    
    @Column
    private Boolean isCompliant = true; // 是否符合NIST标准
    
    @Column(length = 1000)
    private String complianceDetails; // 合规详情
    
    @Version
    private Integer version;
    
    // 构造方法
    public PostQuantumSignatureEntity() {}
    
    public PostQuantumSignatureEntity(String signatureName, String signatureAlgorithm, 
                                      String specificVariant, UUID messageId, UUID userId) {
        this.signatureName = signatureName;
        this.signatureAlgorithm = signatureAlgorithm;
        this.specificVariant = specificVariant;
        this.messageId = messageId;
        this.userId = userId;
    }
    
    // Getter和Setter方法
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getSignatureName() {
        return signatureName;
    }
    
    public void setSignatureName(String signatureName) {
        this.signatureName = signatureName;
    }
    
    public String getSignatureAlgorithm() {
        return signatureAlgorithm;
    }
    
    public void setSignatureAlgorithm(String signatureAlgorithm) {
        this.signatureAlgorithm = signatureAlgorithm;
    }
    
    public String getSpecificVariant() {
        return specificVariant;
    }
    
    public void setSpecificVariant(String specificVariant) {
        this.specificVariant = specificVariant;
    }
    
    public Integer getSignatureSize() {
        return signatureSize;
    }
    
    public void setSignatureSize(Integer signatureSize) {
        this.signatureSize = signatureSize;
    }
    
    public Integer getPublicKeySize() {
        return publicKeySize;
    }
    
    public void setPublicKeySize(Integer publicKeySize) {
        this.publicKeySize = publicKeySize;
    }
    
    public Integer getPrivateKeySize() {
        return privateKeySize;
    }
    
    public void setPrivateKeySize(Integer privateKeySize) {
        this.privateKeySize = privateKeySize;
    }
    
    public String getSignatureData() {
        return signatureData;
    }
    
    public void setSignatureData(String signatureData) {
        this.signatureData = signatureData;
    }
    
    public String getPublicKeyData() {
        return publicKeyData;
    }
    
    public void setPublicKeyData(String publicKeyData) {
        this.publicKeyData = publicKeyData;
    }
    
    public String getPrivateKeyData() {
        return privateKeyData;
    }
    
    public void setPrivateKeyData(String privateKeyData) {
        this.privateKeyData = privateKeyData;
    }
    
    public UUID getMessageId() {
        return messageId;
    }
    
    public void setMessageId(UUID messageId) {
        this.messageId = messageId;
    }
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
    }
    
    public LocalDateTime getSignedAt() {
        return signedAt;
    }
    
    public void setSignedAt(LocalDateTime signedAt) {
        this.signedAt = signedAt;
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
    }
    
    public String getVerificationStatus() {
        return verificationStatus;
    }
    
    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
        if (verificationStatus.equals("VERIFIED")) {
            this.verifiedAt = LocalDateTime.now();
        }
    }
    
    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }
    
    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }
    
    public String getVerificationNotes() {
        return verificationNotes;
    }
    
    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }
    
    public Integer getSecurityLevel() {
        return securityLevel;
    }
    
    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
    }
    
    public Boolean getIsRevocable() {
        return isRevocable;
    }
    
    public void setIsRevocable(Boolean isRevocable) {
        this.isRevocable = isRevocable;
    }
    
    public Boolean getSupportsBatchVerification() {
        return supportsBatchVerification;
    }
    
    public void setSupportsBatchVerification(Boolean supportsBatchVerification) {
        this.supportsBatchVerification = supportsBatchVerification;
    }
    
    public Double getVerificationTimeMs() {
        return verificationTimeMs;
    }
    
    public void setVerificationTimeMs(Double verificationTimeMs) {
        this.verificationTimeMs = verificationTimeMs;
    }
    
    public Double getSignatureGenerationTimeMs() {
        return signatureGenerationTimeMs;
    }
    
    public void setSignatureGenerationTimeMs(Double signatureGenerationTimeMs) {
        this.signatureGenerationTimeMs = signatureGenerationTimeMs;
    }
    
    public Integer getSignatureCount() {
        return signatureCount;
    }
    
    public void setSignatureCount(Integer signatureCount) {
        this.signatureCount = signatureCount;
    }
    
    public String getSignaturePurpose() {
        return signaturePurpose;
    }
    
    public void setSignaturePurpose(String signaturePurpose) {
        this.signaturePurpose = signaturePurpose;
    }
    
    public Boolean getIsTimestamped() {
        return isTimestamped;
    }
    
    public void setIsTimestamped(Boolean isTimestamped) {
        this.isTimestamped = isTimestamped;
    }
    
    public String getTimestampAuthorityUrl() {
        return timestampAuthorityUrl;
    }
    
    public void setTimestampAuthorityUrl(String timestampAuthorityUrl) {
        this.timestampAuthorityUrl = timestampAuthorityUrl;
    }
    
    public LocalDateTime getTimestampVerifiedAt() {
        return timestampVerifiedAt;
    }
    
    public void setTimestampVerifiedAt(LocalDateTime timestampVerifiedAt) {
        this.timestampVerifiedAt = timestampVerifiedAt;
    }
    
    public Boolean getIsCompliant() {
        return isCompliant;
    }
    
    public void setIsCompliant(Boolean isCompliant) {
        this.isCompliant = isCompliant;
    }
    
    public String getComplianceDetails() {
        return complianceDetails;
    }
    
    public void setComplianceDetails(String complianceDetails) {
        this.complianceDetails = complianceDetails;
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    // 业务方法
    public boolean isVerified() {
        return "VERIFIED".equals(verificationStatus);
    }
    
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isNISTStandardized() {
        return specificVariant != null && 
               (specificVariant.contains("Dilithium") || 
                specificVariant.contains("FALCON") ||
                specificVariant.contains("SPHINCS+"));
    }
    
    public boolean isHashBasedSignature() {
        return "SPHINCS_PLUS".equals(signatureAlgorithm);
    }
    
    public boolean isLatticeBasedSignature() {
        return "DILITHIUM".equals(signatureAlgorithm) || "FALCON".equals(signatureAlgorithm);
    }
    
    public boolean requiresLargeKeySize() {
        return signatureAlgorithm != null && 
               (signatureAlgorithm.equals("SPHINCS_PLUS") || 
                signatureAlgorithm.equals("RAINBOW"));
    }
    
    public void incrementSignatureCount() {
        if (signatureCount == null) {
            signatureCount = 1;
        } else {
            signatureCount++;
        }
    }
    
    public void revokeSignature(String reason) {
        this.verificationStatus = "REVOKED";
        this.verificationNotes = "Revoked: " + (reason != null ? reason : "No reason provided");
        this.verifiedAt = LocalDateTime.now();
    }
    
    @Override
    public String toString() {
        return "PostQuantumSignatureEntity{" +
                "id=" + id +
                ", signatureName='" + signatureName + '\'' +
                ", signatureAlgorithm='" + signatureAlgorithm + '\'' +
                ", specificVariant='" + specificVariant + '\'' +
                ", verificationStatus='" + verificationStatus + '\'' +
                ", userId=" + userId +
                ", messageId=" + messageId +
                '}';
    }
}