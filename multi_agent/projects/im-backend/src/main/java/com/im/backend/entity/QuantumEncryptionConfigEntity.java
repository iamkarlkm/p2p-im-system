package com.im.backend.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 量子安全加密配置实体
 * 支持后量子密码学算法配置和量子密钥分发
 */
@Entity
@Table(name = "quantum_encryption_config", indexes = {
    @Index(name = "idx_user_id", columnList = "userId"),
    @Index(name = "idx_conversation_id", columnList = "conversationId"),
    @Index(name = "idx_encryption_mode", columnList = "encryptionMode"),
    @Index(name = "idx_created_at", columnList = "createdAt")
})
public class QuantumEncryptionConfigEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    
    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;
    
    @Column(name = "conversation_id", length = 128)
    private String conversationId;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "encryption_mode", nullable = false, length = 30)
    private EncryptionMode encryptionMode;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "pqc_algorithm", length = 50)
    private PqcAlgorithm pqcAlgorithm;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "traditional_algorithm", length = 50)
    private String traditionalAlgorithm;
    
    @Column(name = "hybrid_mode_enabled")
    private Boolean hybridModeEnabled;
    
    @Column(name = "key_size_bits")
    private Integer keySizeBits;
    
    @Column(name = "security_level", length = 20)
    private String securityLevel;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "kdf_algorithm", length = 30)
    private KdfAlgorithm kdfAlgorithm;
    
    @Column(name = "key_rotation_interval_hours")
    private Integer keyRotationIntervalHours;
    
    @Column(name = "key_derivation_salt", length = 256)
    private String keyDerivationSalt;
    
    @Column(name = "quantum_key_distribution_enabled")
    private Boolean quantumKeyDistributionEnabled;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "qkd_protocol", length = 30)
    private QkdProtocol qkdProtocol;
    
    @Column(name = "qkd_key_rate_bps")
    private Double qkdKeyRateBps;
    
    @Column(name = "qkd_error_rate")
    private Double qkdErrorRate;
    
    @Column(name = "qkd_distance_km")
    private Double qkdDistanceKm;
    
    @Column(name = "qkd_channel_loss_db")
    private Double qkdChannelLossDb;
    
    @Column(name = "privacy_amplification_enabled")
    private Boolean privacyAmplificationEnabled;
    
    @Column(name = "privacy_amplification_ratio")
    private Double privacyAmplificationRatio;
    
    @Column(name = "information_reconciliation_enabled")
    private Boolean informationReconciliationEnabled;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "reconciliation_protocol", length = 30)
    private ReconciliationProtocol reconciliationProtocol;
    
    @Column(name = "authentication_tag_length_bits")
    private Integer authenticationTagLengthBits;
    
    @Column(name = "nonce_length_bytes")
    private Integer nonceLengthBytes;
    
    @Column(name = "performance_optimization_enabled")
    private Boolean performanceOptimizationEnabled;
    
    @Column(name = "hardware_acceleration_enabled")
    private Boolean hardwareAccelerationEnabled;
    
    @Column(name = "batch_encryption_enabled")
    private Boolean batchEncryptionEnabled;
    
    @Column(name = "batch_size")
    private Integer batchSize;
    
    @Column(name = "compression_before_encryption")
    private Boolean compressionBeforeEncryption;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "compression_algorithm", length = 20)
    private String compressionAlgorithm;
    
    @Column(name = "quantum_resistance_score")
    private Double quantumResistanceScore;
    
    @Column(name = "security_audit_timestamp")
    private LocalDateTime securityAuditTimestamp;
    
    @Column(name = "security_audit_result", columnDefinition = "TEXT")
    private String securityAuditResult;
    
    @Column(name = "compliance_standards", columnDefinition = "JSON")
    private String complianceStandards;
    
    @Column(name = "migration_plan", columnDefinition = "TEXT")
    private String migrationPlan;
    
    @Column(name = "migration_progress_percentage")
    private Double migrationProgressPercentage;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    private ConfigStatus status;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "last_key_rotation_at")
    private LocalDateTime lastKeyRotationAt;
    
    @Column(name = "next_key_rotation_at")
    private LocalDateTime nextKeyRotationAt;
    
    @Column(name = "total_keys_generated")
    private Long totalKeysGenerated;
    
    @Column(name = "total_messages_encrypted")
    private Long totalMessagesEncrypted;
    
    @Column(name = "total_bytes_encrypted")
    private Long totalBytesEncrypted;
    
    @Column(name = "average_encryption_time_ms")
    private Double averageEncryptionTimeMs;
    
    @Column(name = "average_decryption_time_ms")
    private Double averageDecryptionTimeMs;
    
    @Column(name = "additional_metadata", columnDefinition = "JSON")
    private String additionalMetadata;
    
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @Column(name = "expires_at")
    private LocalDateTime expiresAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (status == null) {
            status = ConfigStatus.ACTIVE;
        }
        if (hybridModeEnabled == null) {
            hybridModeEnabled = true;
        }
        if (quantumKeyDistributionEnabled == null) {
            quantumKeyDistributionEnabled = false;
        }
        if (performanceOptimizationEnabled == null) {
            performanceOptimizationEnabled = true;
        }
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    // 构造函数
    public QuantumEncryptionConfigEntity() {}
    
    public QuantumEncryptionConfigEntity(String userId, EncryptionMode encryptionMode, PqcAlgorithm pqcAlgorithm) {
        this.userId = userId;
        this.encryptionMode = encryptionMode;
        this.pqcAlgorithm = pqcAlgorithm;
        this.hybridModeEnabled = true;
        this.status = ConfigStatus.ACTIVE;
    }
    
    // Getters and Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    
    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }
    
    public EncryptionMode getEncryptionMode() { return encryptionMode; }
    public void setEncryptionMode(EncryptionMode encryptionMode) { this.encryptionMode = encryptionMode; }
    
    public PqcAlgorithm getPqcAlgorithm() { return pqcAlgorithm; }
    public void setPqcAlgorithm(PqcAlgorithm pqcAlgorithm) { this.pqcAlgorithm = pqcAlgorithm; }
    
    public String getTraditionalAlgorithm() { return traditionalAlgorithm; }
    public void setTraditionalAlgorithm(String traditionalAlgorithm) { this.traditionalAlgorithm = traditionalAlgorithm; }
    
    public Boolean getHybridModeEnabled() { return hybridModeEnabled; }
    public void setHybridModeEnabled(Boolean hybridModeEnabled) { this.hybridModeEnabled = hybridModeEnabled; }
    
    public Integer getKeySizeBits() { return keySizeBits; }
    public void setKeySizeBits(Integer keySizeBits) { this.keySizeBits = keySizeBits; }
    
    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) { this.securityLevel = securityLevel; }
    
    public KdfAlgorithm getKdfAlgorithm() { return kdfAlgorithm; }
    public void setKdfAlgorithm(KdfAlgorithm kdfAlgorithm) { this.kdfAlgorithm = kdfAlgorithm; }
    
    public Integer getKeyRotationIntervalHours() { return keyRotationIntervalHours; }
    public void setKeyRotationIntervalHours(Integer keyRotationIntervalHours) { this.keyRotationIntervalHours = keyRotationIntervalHours; }
    
    public String getKeyDerivationSalt() { return keyDerivationSalt; }
    public void setKeyDerivationSalt(String keyDerivationSalt) { this.keyDerivationSalt = keyDerivationSalt; }
    
    public Boolean getQuantumKeyDistributionEnabled() { return quantumKeyDistributionEnabled; }
    public void setQuantumKeyDistributionEnabled(Boolean quantumKeyDistributionEnabled) { this.quantumKeyDistributionEnabled = quantumKeyDistributionEnabled; }
    
    public QkdProtocol getQkdProtocol() { return qkdProtocol; }
    public void setQkdProtocol(QkdProtocol qkdProtocol) { this.qkdProtocol = qkdProtocol; }
    
    public Double getQkdKeyRateBps() { return qkdKeyRateBps; }
    public void setQkdKeyRateBps(Double qkdKeyRateBps) { this.qkdKeyRateBps = qkdKeyRateBps; }
    
    public Double getQkdErrorRate() { return qkdErrorRate; }
    public void setQkdErrorRate(Double qkdErrorRate) { this.qkdErrorRate = qkdErrorRate; }
    
    public Double getQkdDistanceKm() { return qkdDistanceKm; }
    public void setQkdDistanceKm(Double qkdDistanceKm) { this.qkdDistanceKm = qkdDistanceKm; }
    
    public Double getQkdChannelLossDb() { return qkdChannelLossDb; }
    public void setQkdChannelLossDb(Double qkdChannelLossDb) { this.qkdChannelLossDb = qkdChannelLossDb; }
    
    public Boolean getPrivacyAmplificationEnabled() { return privacyAmplificationEnabled; }
    public void setPrivacyAmplificationEnabled(Boolean privacyAmplificationEnabled) { this.privacyAmplificationEnabled = privacyAmplificationEnabled; }
    
    public Double getPrivacyAmplificationRatio() { return privacyAmplificationRatio; }
    public void setPrivacyAmplificationRatio(Double privacyAmplificationRatio) { this.privacyAmplificationRatio = privacyAmplificationRatio; }
    
    public Boolean getInformationReconciliationEnabled() { return informationReconciliationEnabled; }
    public void setInformationReconciliationEnabled(Boolean informationReconciliationEnabled) { this.informationReconciliationEnabled = informationReconciliationEnabled; }
    
    public ReconciliationProtocol getReconciliationProtocol() { return reconciliationProtocol; }
    public void setReconciliationProtocol(ReconciliationProtocol reconciliationProtocol) { this.reconciliationProtocol = reconciliationProtocol; }
    
    public Integer getAuthenticationTagLengthBits() { return authenticationTagLengthBits; }
    public void setAuthenticationTagLengthBits(Integer authenticationTagLengthBits) { this.authenticationTagLengthBits = authenticationTagLengthBits; }
    
    public Integer getNonceLengthBytes() { return nonceLengthBytes; }
    public void setNonceLengthBytes(Integer nonceLengthBytes) { this.nonceLengthBytes = nonceLengthBytes; }
    
    public Boolean getPerformanceOptimizationEnabled() { return performanceOptimizationEnabled; }
    public void setPerformanceOptimizationEnabled(Boolean performanceOptimizationEnabled) { this.performanceOptimizationEnabled = performanceOptimizationEnabled; }
    
    public Boolean getHardwareAccelerationEnabled() { return hardwareAccelerationEnabled; }
    public void setHardwareAccelerationEnabled(Boolean hardwareAccelerationEnabled) { this.hardwareAccelerationEnabled = hardwareAccelerationEnabled; }
    
    public Boolean getBatchEncryptionEnabled() { return batchEncryptionEnabled; }
    public void setBatchEncryptionEnabled(Boolean batchEncryptionEnabled) { this.batchEncryptionEnabled = batchEncryptionEnabled; }
    
    public Integer getBatchSize() { return batchSize; }
    public void setBatchSize(Integer batchSize) { this.batchSize = batchSize; }
    
    public Boolean getCompressionBeforeEncryption() { return compressionBeforeEncryption; }
    public void setCompressionBeforeEncryption(Boolean compressionBeforeEncryption) { this.compressionBeforeEncryption = compressionBeforeEncryption; }
    
    public String getCompressionAlgorithm() { return compressionAlgorithm; }
    public void setCompressionAlgorithm(String compressionAlgorithm) { this.compressionAlgorithm = compressionAlgorithm; }
    
    public Double getQuantumResistanceScore() { return quantumResistanceScore; }
    public void setQuantumResistanceScore(Double quantumResistanceScore) { this.quantumResistanceScore = quantumResistanceScore; }
    
    public LocalDateTime getSecurityAuditTimestamp() { return securityAuditTimestamp; }
    public void setSecurityAuditTimestamp(LocalDateTime securityAuditTimestamp) { this.securityAuditTimestamp = securityAuditTimestamp; }
    
    public String getSecurityAuditResult() { return securityAuditResult; }
    public void setSecurityAuditResult(String securityAuditResult) { this.securityAuditResult = securityAuditResult; }
    
    public String getComplianceStandards() { return complianceStandards; }
    public void setComplianceStandards(String complianceStandards) { this.complianceStandards = complianceStandards; }
    
    public String getMigrationPlan() { return migrationPlan; }
    public void setMigrationPlan(String migrationPlan) { this.migrationPlan = migrationPlan; }
    
    public Double getMigrationProgressPercentage() { return migrationProgressPercentage; }
    public void setMigrationProgressPercentage(Double migrationProgressPercentage) { this.migrationProgressPercentage = migrationProgressPercentage; }
    
    public ConfigStatus getStatus() { return status; }
    public void setStatus(ConfigStatus status) { this.status = status; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public LocalDateTime getLastKeyRotationAt() { return lastKeyRotationAt; }
    public void setLastKeyRotationAt(LocalDateTime lastKeyRotationAt) { this.lastKeyRotationAt = lastKeyRotationAt; }
    
    public LocalDateTime getNextKeyRotationAt() { return nextKeyRotationAt; }
    public void setNextKeyRotationAt(LocalDateTime nextKeyRotationAt) { this.nextKeyRotationAt = nextKeyRotationAt; }
    
    public Long getTotalKeysGenerated() { return totalKeysGenerated; }
    public void setTotalKeysGenerated(Long totalKeysGenerated) { this.totalKeysGenerated = totalKeysGenerated; }
    
    public Long getTotalMessagesEncrypted() { return totalMessagesEncrypted; }
    public void setTotalMessagesEncrypted(Long totalMessagesEncrypted) { this.totalMessagesEncrypted = totalMessagesEncrypted; }
    
    public Long getTotalBytesEncrypted() { return totalBytesEncrypted; }
    public void setTotalBytesEncrypted(Long totalBytesEncrypted) { this.totalBytesEncrypted = totalBytesEncrypted; }
    
    public Double getAverageEncryptionTimeMs() { return averageEncryptionTimeMs; }
    public void setAverageEncryptionTimeMs(Double averageEncryptionTimeMs) { this.averageEncryptionTimeMs = averageEncryptionTimeMs; }
    
    public Double getAverageDecryptionTimeMs() { return averageDecryptionTimeMs; }
    public void setAverageDecryptionTimeMs(Double averageDecryptionTimeMs) { this.averageDecryptionTimeMs = averageDecryptionTimeMs; }
    
    public String getAdditionalMetadata() { return additionalMetadata; }
    public void setAdditionalMetadata(String additionalMetadata) { this.additionalMetadata = additionalMetadata; }
    
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
    
    public LocalDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(LocalDateTime expiresAt) { this.expiresAt = expiresAt; }
    
    // 枚举类型
    public enum EncryptionMode {
        CLASSICAL_ONLY,
        HYBRID,
        QUANTUM_SAFE_ONLY,
        QUANTUM_KEY_DISTRIBUTION
    }
    
    public enum PqcAlgorithm {
        CRYSTALS_KYBER,        // 密钥封装机制 (KEM)
        CRYSTALS_DILITHIUM,    // 数字签名
        FALCON,                // 数字签名 (高性能)
        SPHINCS_PLUS,          // 无状态哈希签名
        BIKE,                  // 基于编码的 KEM
        HQC,                   // 基于编码的 KEM
        NTRU,                  // 基于格的 KEM
        SABER,                 // 基于模学习误差的 KEM
        CUSTOM
    }
    
    public enum KdfAlgorithm {
        HKDF_SHA256,
        HKDF_SHA384,
        HKDF_SHA512,
        PBKDF2,
        ARGON2,
        SCRYPT,
        CUSTOM
    }
    
    public enum QkdProtocol {
        BB84,
        E91,
        B92,
        SIX_STATE,
        CV_QKD,
        MDI_QKD,
        CUSTOM
    }
    
    public enum ReconciliationProtocol {
        CASCADE,
        WINNOW,
        LDPC,
        POLAR_CODES,
        CUSTOM
    }
    
    public enum ConfigStatus {
        ACTIVE,
        INACTIVE,
        PENDING_MIGRATION,
        MIGRATING,
        ERROR,
        EXPIRED
    }
    
    // 辅助方法
    public boolean isHybridMode() {
        return hybridModeEnabled != null && hybridModeEnabled;
    }
    
    public boolean isQuantumSafe() {
        return encryptionMode == EncryptionMode.QUANTUM_SAFE_ONLY || 
               encryptionMode == EncryptionMode.QUANTUM_KEY_DISTRIBUTION ||
               (encryptionMode == EncryptionMode.HYBRID && pqcAlgorithm != null);
    }
    
    public boolean needsKeyRotation() {
        if (nextKeyRotationAt == null) {
            return false;
        }
        return LocalDateTime.now().isAfter(nextKeyRotationAt);
    }
    
    public boolean isActive() {
        return status == ConfigStatus.ACTIVE;
    }
    
    public boolean hasError() {
        return status == ConfigStatus.ERROR;
    }
    
    // 计算量子抵抗评分
    public void calculateQuantumResistanceScore() {
        double score = 0.0;
        
        // 基于加密模式评分
        switch (encryptionMode) {
            case QUANTUM_KEY_DISTRIBUTION:
                score += 40.0;
                break;
            case QUANTUM_SAFE_ONLY:
                score += 35.0;
                break;
            case HYBRID:
                score += 25.0;
                break;
            case CLASSICAL_ONLY:
                score += 0.0;
                break;
        }
        
        // 基于 PQC 算法评分
        if (pqcAlgorithm != null) {
            switch (pqcAlgorithm) {
                case CRYSTALS_KYBER:
                case CRYSTALS_DILITHIUM:
                    score += 30.0;
                    break;
                case FALCON:
                case SPHINCS_PLUS:
                    score += 25.0;
                    break;
                default:
                    score += 20.0;
            }
        }
        
        // 基于密钥长度评分
        if (keySizeBits != null && keySizeBits >= 256) {
            score += 15.0;
        } else if (keySizeBits != null && keySizeBits >= 128) {
            score += 10.0;
        }
        
        // 基于 QKD 评分
        if (quantumKeyDistributionEnabled != null && quantumKeyDistributionEnabled) {
            score += 15.0;
        }
        
        quantumResistanceScore = Math.min(score, 100.0);
    }
    
    // 生成安全配置报告
    public String generateSecurityReport() {
        StringBuilder report = new StringBuilder();
        report.append("量子安全加密配置报告\n");
        report.append("========================\n");
        report.append("用户 ID: ").append(userId).append("\n");
        report.append("加密模式: ").append(encryptionMode).append("\n");
        report.append("PQC 算法: ").append(pqcAlgorithm != null ? pqcAlgorithm : "未配置").append("\n");
        report.append("混合模式: ").append(isHybridMode() ? "已启用" : "未启用").append("\n");
        report.append("密钥长度: ").append(keySizeBits != null ? keySizeBits + " bits" : "默认").append("\n");
        report.append("量子抵抗评分: ").append(String.format("%.1f/100", quantumResistanceScore != null ? quantumResistanceScore : 0)).append("\n");
        
        if (quantumKeyDistributionEnabled != null && quantumKeyDistributionEnabled) {
            report.append("QKD 协议: ").append(qkdProtocol).append("\n");
            report.append("QKD 密钥率: ").append(qkdKeyRateBps).append(" bps\n");
            report.append("QKD 距离: ").append(qkdDistanceKm).append(" km\n");
        }
        
        report.append("状态: ").append(status).append("\n");
        report.append("总加密消息数: ").append(totalMessagesEncrypted != null ? totalMessagesEncrypted : 0).append("\n");
        report.append("平均加密时间: ").append(averageEncryptionTimeMs != null ? String.format("%.2f ms", averageEncryptionTimeMs) : "N/A").append("\n");
        
        if (needsKeyRotation()) {
            report.append("⚠️ 警告: 需要密钥轮换\n");
        }
        
        if (hasError()) {
            report.append("❌ 错误: ").append(errorMessage).append("\n");
        }
        
        return report.toString();
    }
}