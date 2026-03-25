package com.im.system.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 量子抗性加密实体
 * 基于后量子密码学的量子抗性加密系统
 * 
 * 支持算法类型：
 * 1. 基于格的加密 (Lattice-based): CRYSTALS-Kyber
 * 2. 基于哈希的签名 (Hash-based): SPHINCS+
 * 3. 基于编码的加密 (Code-based): Classic McEliece
 * 4. 多变量密码 (Multivariate): Rainbow
 * 5. 超奇异椭圆曲线同源 (SIKE): SIKEp434, SIKEp503
 * 6. NIST标准化算法: CRYSTALS-Dilithium, FALCON
 */
@Entity
@Table(name = "quantum_resistant_encryption", 
       indexes = {
           @Index(name = "idx_algorithm_type", columnList = "algorithmType"),
           @Index(name = "idx_user_id", columnList = "userId"),
           @Index(name = "idx_created_at", columnList = "createdAt")
       })
public class QuantumResistantEncryptionEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(nullable = false, length = 50)
    private String algorithmType; // LATTICE, HASH_BASED, CODE_BASED, MULTIVARIATE, SIKE
    
    @Column(nullable = false, length = 100)
    private String specificAlgorithm; // CRYSTALS-Kyber, SPHINCS+, Classic-McEliece, Rainbow, SIKEp434
    
    @Column(nullable = false)
    private Integer securityLevel; // 1-5级，1为最高安全
    
    @Column(nullable = false)
    private Integer keySize; // 密钥大小（位）
    
    @Column(length = 2000)
    private String publicKey;
    
    @Column(length = 2000)
    private String privateKey;
    
    @Column(length = 2000)
    private String signatureKey;
    
    @Column(nullable = false)
    private Boolean isActive = true;
    
    @Column
    private LocalDateTime expiresAt;
    
    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();
    
    @Column
    private LocalDateTime updatedAt;
    
    @Column(nullable = false)
    private UUID userId;
    
    @Column(length = 500)
    private String description;
    
    @Column
    private Boolean supportsQuantumKeyDistribution = false;
    
    @Column
    private Boolean isHybridEncryption = true; // 默认使用混合加密
    
    @Column
    private Integer encryptionRounds = 1; // 加密轮数
    
    @Column
    private Double performanceScore; // 性能评分 0-100
    
    @Column
    private Double securityScore; // 安全评分 0-100
    
    @Column(length = 1000)
    private String metadata; // JSON格式的元数据
    
    @Version
    private Integer version;
    
    // 构造方法
    public QuantumResistantEncryptionEntity() {}
    
    public QuantumResistantEncryptionEntity(String name, String algorithmType, String specificAlgorithm, 
                                            Integer securityLevel, Integer keySize, UUID userId) {
        this.name = name;
        this.algorithmType = algorithmType;
        this.specificAlgorithm = specificAlgorithm;
        this.securityLevel = securityLevel;
        this.keySize = keySize;
        this.userId = userId;
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getter和Setter方法
    public UUID getId() {
        return id;
    }
    
    public void setId(UUID id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getAlgorithmType() {
        return algorithmType;
    }
    
    public void setAlgorithmType(String algorithmType) {
        this.algorithmType = algorithmType;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getSpecificAlgorithm() {
        return specificAlgorithm;
    }
    
    public void setSpecificAlgorithm(String specificAlgorithm) {
        this.specificAlgorithm = specificAlgorithm;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getSecurityLevel() {
        return securityLevel;
    }
    
    public void setSecurityLevel(Integer securityLevel) {
        this.securityLevel = securityLevel;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getKeySize() {
        return keySize;
    }
    
    public void setKeySize(Integer keySize) {
        this.keySize = keySize;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPublicKey() {
        return publicKey;
    }
    
    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getPrivateKey() {
        return privateKey;
    }
    
    public void setPrivateKey(String privateKey) {
        this.privateKey = privateKey;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getSignatureKey() {
        return signatureKey;
    }
    
    public void setSignatureKey(String signatureKey) {
        this.signatureKey = signatureKey;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.updatedAt = LocalDateTime.now();
    }
    
    public LocalDateTime getExpiresAt() {
        return expiresAt;
    }
    
    public void setExpiresAt(LocalDateTime expiresAt) {
        this.expiresAt = expiresAt;
        this.updatedAt = LocalDateTime.now();
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
    
    public UUID getUserId() {
        return userId;
    }
    
    public void setUserId(UUID userId) {
        this.userId = userId;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Boolean getSupportsQuantumKeyDistribution() {
        return supportsQuantumKeyDistribution;
    }
    
    public void setSupportsQuantumKeyDistribution(Boolean supportsQuantumKeyDistribution) {
        this.supportsQuantumKeyDistribution = supportsQuantumKeyDistribution;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Boolean getIsHybridEncryption() {
        return isHybridEncryption;
    }
    
    public void setIsHybridEncryption(Boolean isHybridEncryption) {
        this.isHybridEncryption = isHybridEncryption;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getEncryptionRounds() {
        return encryptionRounds;
    }
    
    public void setEncryptionRounds(Integer encryptionRounds) {
        this.encryptionRounds = encryptionRounds;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Double getPerformanceScore() {
        return performanceScore;
    }
    
    public void setPerformanceScore(Double performanceScore) {
        this.performanceScore = performanceScore;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Double getSecurityScore() {
        return securityScore;
    }
    
    public void setSecurityScore(Double securityScore) {
        this.securityScore = securityScore;
        this.updatedAt = LocalDateTime.now();
    }
    
    public String getMetadata() {
        return metadata;
    }
    
    public void setMetadata(String metadata) {
        this.metadata = metadata;
        this.updatedAt = LocalDateTime.now();
    }
    
    public Integer getVersion() {
        return version;
    }
    
    public void setVersion(Integer version) {
        this.version = version;
    }
    
    // 业务方法
    public boolean isExpired() {
        return expiresAt != null && LocalDateTime.now().isAfter(expiresAt);
    }
    
    public boolean isQuantumSafe() {
        return algorithmType != null && 
               (algorithmType.equals("LATTICE") || 
                algorithmType.equals("HASH_BASED") || 
                algorithmType.equals("CODE_BASED") ||
                algorithmType.equals("MULTIVARIATE") ||
                algorithmType.equals("SIKE"));
    }
    
    public boolean supportsPostQuantumSignatures() {
        return specificAlgorithm != null && 
               (specificAlgorithm.contains("Dilithium") || 
                specificAlgorithm.contains("FALCON") ||
                specificAlgorithm.contains("SPHINCS+") ||
                specificAlgorithm.contains("Rainbow"));
    }
    
    @Override
    public String toString() {
        return "QuantumResistantEncryptionEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", algorithmType='" + algorithmType + '\'' +
                ", specificAlgorithm='" + specificAlgorithm + '\'' +
                ", securityLevel=" + securityLevel +
                ", keySize=" + keySize +
                ", isActive=" + isActive +
                ", userId=" + userId +
                '}';
    }
}