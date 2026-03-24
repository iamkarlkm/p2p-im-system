package com.im.backend.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * 同态加密数据库实体
 * 支持全同态加密（BGV、BFV、CKKS）、加密SQL查询引擎、部分同态优化、加密索引结构
 */
@Entity
@Table(name = "homomorphic_encryption_database")
public class HomomorphicEncryptionDatabaseEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "database_id")
    private Long databaseId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "session_id", length = 64)
    private String sessionId;
    
    @Column(name = "database_name", nullable = false, length = 128)
    private String databaseName;
    
    @Column(name = "database_type", nullable = false, length = 32)
    private String databaseType; // "MESSAGE", "USER_PROFILE", "GROUP", "FILE_METADATA", "AUDIT_LOG"
    
    @Column(name = "encryption_scheme", nullable = false, length = 32)
    private String encryptionScheme; // "BGV", "BFV", "CKKS", "PAILLIER", "ELGAMAL", "RSA_HOMOMORPHIC"
    
    @Column(name = "security_level", nullable = false, length = 32)
    private String securityLevel; // "LOW", "MEDIUM", "HIGH", "VERY_HIGH", "MILITARY"
    
    @Column(name = "key_size", nullable = false)
    private Integer keySize; // 1024, 2048, 3072, 4096, 8192
    
    @Column(name = "modulus_size", nullable = false)
    private Integer modulusSize; // 模数大小
    
    @Column(name = "plaintext_modulus", nullable = false)
    private Long plaintextModulus;
    
    @Column(name = "noise_budget", nullable = false)
    private Integer noiseBudget; // 噪声预算
    
    @Column(name = "relin_keys", columnDefinition = "TEXT")
    private String relinKeys; // 重线性化密钥
    
    @Column(name = "galois_keys", columnDefinition = "TEXT")
    private String galoisKeys; // Galois密钥
    
    @Column(name = "rotation_keys", columnDefinition = "TEXT")
    private String rotationKeys; // 旋转密钥
    
    @Column(name = "public_key_hash", nullable = false, length = 64)
    private String publicKeyHash;
    
    @Column(name = "secret_key_hash", nullable = false, length = 64)
    private String secretKeyHash;
    
    @Column(name = "encrypted_data_count", nullable = false)
    private Integer encryptedDataCount;
    
    @Column(name = "total_data_size_bytes", nullable = false)
    private Long totalDataSizeBytes;
    
    @Column(name = "encryption_time_avg_ms", nullable = false)
    private Double encryptionTimeAvgMs;
    
    @Column(name = "decryption_time_avg_ms", nullable = false)
    private Double decryptionTimeAvgMs;
    
    @Column(name = "homomorphic_op_time_avg_ms", nullable = false)
    private Double homomorphicOpTimeAvgMs;
    
    @Column(name = "compression_enabled", nullable = false)
    private Boolean compressionEnabled;
    
    @Column(name = "compression_algorithm", length = 32)
    private String compressionAlgorithm; // "ZLIB", "GZIP", "BROTLI", "ZSTD"
    
    @Column(name = "compression_ratio", nullable = false)
    private Double compressionRatio;
    
    @Column(name = "indexing_enabled", nullable = false)
    private Boolean indexingEnabled;
    
    @Column(name = "index_type", length = 32)
    private String indexType; // "BALANCED_TREE", "HASH", "BLOOM_FILTER", "RANGE_TREE"
    
    @Column(name = "index_count", nullable = false)
    private Integer indexCount;
    
    @Column(name = "query_cache_enabled", nullable = false)
    private Boolean queryCacheEnabled;
    
    @Column(name = "cache_hit_rate", nullable = false)
    private Double cacheHitRate;
    
    @Column(name = "parallelism_enabled", nullable = false)
    private Boolean parallelismEnabled;
    
    @Column(name = "max_parallel_threads", nullable = false)
    private Integer maxParallelThreads;
    
    @Column(name = "hardware_acceleration", nullable = false)
    private Boolean hardwareAcceleration;
    
    @Column(name = "accelerator_type", length = 32)
    private String acceleratorType; // "GPU", "FPGA", "TPU", "ASIC"
    
    @Column(name = "encryption_context", columnDefinition = "TEXT")
    private String encryptionContext; // JSON格式的加密上下文
    
    @Column(name = "metadata_schema", columnDefinition = "TEXT")
    private String metadataSchema; // JSON格式的元数据模式
    
    @Column(name = "access_control_policy", columnDefinition = "TEXT")
    private String accessControlPolicy; // JSON格式的访问控制策略
    
    @Column(name = "data_retention_days", nullable = false)
    private Integer dataRetentionDays;
    
    @Column(name = "auto_rekeying_enabled", nullable = false)
    private Boolean autoRekeyingEnabled;
    
    @Column(name = "rekeying_interval_days", nullable = false)
    private Integer rekeyingIntervalDays;
    
    @Column(name = "last_rekeying_time")
    private LocalDateTime lastRekeyingTime;
    
    @Column(name = "backup_enabled", nullable = false)
    private Boolean backupEnabled;
    
    @Column(name = "backup_interval_hours", nullable = false)
    private Integer backupIntervalHours;
    
    @Column(name = "last_backup_time")
    private LocalDateTime lastBackupTime;
    
    @Column(name = "replication_factor", nullable = false)
    private Integer replicationFactor;
    
    @Column(name = "consistency_level", nullable = false, length = 32)
    private String consistencyLevel; // "STRONG", "EVENTUAL", "CAUSAL", "LINEARIZABLE"
    
    @Column(name = "sharding_enabled", nullable = false)
    private Boolean shardingEnabled;
    
    @Column(name = "shard_count", nullable = false)
    private Integer shardCount;
    
    @Column(name = "shard_key", length = 64)
    private String shardKey;
    
    @Column(name = "audit_logging_enabled", nullable = false)
    private Boolean auditLoggingEnabled;
    
    @Column(name = "audit_retention_days", nullable = false)
    private Integer auditRetentionDays;
    
    @Column(name = "compliance_standards", columnDefinition = "TEXT")
    private String complianceStandards; // JSON数组: ["GDPR", "CCPA", "HIPAA", "SOX", "PCI-DSS"]
    
    @Column(name = "privacy_budget", nullable = false)
    private Double privacyBudget;
    
    @Column(name = "privacy_budget_consumed", nullable = false)
    private Double privacyBudgetConsumed;
    
    @Column(name = "differential_privacy_enabled", nullable = false)
    private Boolean differentialPrivacyEnabled;
    
    @Column(name = "dp_epsilon", nullable = false)
    private Double dpEpsilon;
    
    @Column(name = "dp_delta", nullable = false)
    private Double dpDelta;
    
    @Column(name = "max_query_complexity", nullable = false)
    private Integer maxQueryComplexity;
    
    @Column(name = "status", nullable = false, length = 32)
    private String status; // "ACTIVE", "INACTIVE", "MAINTENANCE", "ARCHIVED", "DELETED"
    
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;
    
    @Column(name = "updated_time", nullable = false)
    private LocalDateTime updatedTime;
    
    @Column(name = "last_accessed_time")
    private LocalDateTime lastAccessedTime;
    
    @Column(name = "performance_metrics", columnDefinition = "TEXT")
    private String performanceMetrics; // JSON格式的性能指标
    
    @Column(name = "health_score", nullable = false)
    private Double healthScore;
    
    @Column(name = "security_score", nullable = false)
    private Double securityScore;
    
    @Column(name = "privacy_score", nullable = false)
    private Double privacyScore;
    
    @Column(name = "cost_estimate_usd_per_month", nullable = false)
    private BigDecimal costEstimateUsdPerMonth;
    
    // 构造函数
    public HomomorphicEncryptionDatabaseEntity() {
        this.createdTime = LocalDateTime.now();
        this.updatedTime = LocalDateTime.now();
        this.encryptedDataCount = 0;
        this.totalDataSizeBytes = 0L;
        this.encryptionTimeAvgMs = 0.0;
        this.decryptionTimeAvgMs = 0.0;
        this.homomorphicOpTimeAvgMs = 0.0;
        this.compressionEnabled = false;
        this.compressionRatio = 1.0;
        this.indexingEnabled = false;
        this.indexCount = 0;
        this.queryCacheEnabled = false;
        this.cacheHitRate = 0.0;
        this.parallelismEnabled = false;
        this.maxParallelThreads = 1;
        this.hardwareAcceleration = false;
        this.dataRetentionDays = 365;
        this.autoRekeyingEnabled = false;
        this.rekeyingIntervalDays = 90;
        this.backupEnabled = true;
        this.backupIntervalHours = 24;
        this.replicationFactor = 1;
        this.consistencyLevel = "STRONG";
        this.shardingEnabled = false;
        this.shardCount = 1;
        this.auditLoggingEnabled = true;
        this.auditRetentionDays = 90;
        this.privacyBudget = 100.0;
        this.privacyBudgetConsumed = 0.0;
        this.differentialPrivacyEnabled = false;
        this.dpEpsilon = 1.0;
        this.dpDelta = 0.00001;
        this.maxQueryComplexity = 100;
        this.status = "ACTIVE";
        this.healthScore = 100.0;
        this.securityScore = 100.0;
        this.privacyScore = 100.0;
        this.costEstimateUsdPerMonth = BigDecimal.ZERO;
    }
    
    // Getter 和 Setter 方法
    public Long getDatabaseId() { return databaseId; }
    public void setDatabaseId(Long databaseId) { this.databaseId = databaseId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getDatabaseName() { return databaseName; }
    public void setDatabaseName(String databaseName) { this.databaseName = databaseName; }
    
    public String getDatabaseType() { return databaseType; }
    public void setDatabaseType(String databaseType) { this.databaseType = databaseType; }
    
    public String getEncryptionScheme() { return encryptionScheme; }
    public void setEncryptionScheme(String encryptionScheme) { this.encryptionScheme = encryptionScheme; }
    
    public String getSecurityLevel() { return securityLevel; }
    public void setSecurityLevel(String securityLevel) { this.securityLevel = securityLevel; }
    
    public Integer getKeySize() { return keySize; }
    public void setKeySize(Integer keySize) { this.keySize = keySize; }
    
    public Integer getModulusSize() { return modulusSize; }
    public void setModulusSize(Integer modulusSize) { this.modulusSize = modulusSize; }
    
    public Long getPlaintextModulus() { return plaintextModulus; }
    public void setPlaintextModulus(Long plaintextModulus) { this.plaintextModulus = plaintextModulus; }
    
    public Integer getNoiseBudget() { return noiseBudget; }
    public void setNoiseBudget(Integer noiseBudget) { this.noiseBudget = noiseBudget; }
    
    public String getRelinKeys() { return relinKeys; }
    public void setRelinKeys(String relinKeys) { this.relinKeys = relinKeys; }
    
    public String getGaloisKeys() { return galoisKeys; }
    public void setGaloisKeys(String galoisKeys) { this.galoisKeys = galoisKeys; }
    
    public String getRotationKeys() { return rotationKeys; }
    public void setRotationKeys(String rotationKeys) { this.rotationKeys = rotationKeys; }
    
    public String getPublicKeyHash() { return publicKeyHash; }
    public void setPublicKeyHash(String publicKeyHash) { this.publicKeyHash = publicKeyHash; }
    
    public String getSecretKeyHash() { return secretKeyHash; }
    public void setSecretKeyHash(String secretKeyHash) { this.secretKeyHash = secretKeyHash; }
    
    public Integer getEncryptedDataCount() { return encryptedDataCount; }
    public void setEncryptedDataCount(Integer encryptedDataCount) { this.encryptedDataCount = encryptedDataCount; }
    
    public Long getTotalDataSizeBytes() { return totalDataSizeBytes; }
    public void setTotalDataSizeBytes(Long totalDataSizeBytes) { this.totalDataSizeBytes = totalDataSizeBytes; }
    
    public Double getEncryptionTimeAvgMs() { return encryptionTimeAvgMs; }
    public void setEncryptionTimeAvgMs(Double encryptionTimeAvgMs) { this.encryptionTimeAvgMs = encryptionTimeAvgMs; }
    
    public Double getDecryptionTimeAvgMs() { return decryptionTimeAvgMs; }
    public void setDecryptionTimeAvgMs(Double decryptionTimeAvgMs) { this.decryptionTimeAvgMs = decryptionTimeAvgMs; }
    
    public Double getHomomorphicOpTimeAvgMs() { return homomorphicOpTimeAvgMs; }
    public void setHomomorphicOpTimeAvgMs(Double homomorphicOpTimeAvgMs) { this.homomorphicOpTimeAvgMs = homomorphicOpTimeAvgMs; }
    
    public Boolean getCompressionEnabled() { return compressionEnabled; }
    public void setCompressionEnabled(Boolean compressionEnabled) { this.compressionEnabled = compressionEnabled; }
    
    public String getCompressionAlgorithm() { return compressionAlgorithm; }
    public void setCompressionAlgorithm(String compressionAlgorithm) { this.compressionAlgorithm = compressionAlgorithm; }
    
    public Double getCompressionRatio() { return compressionRatio; }
    public void setCompressionRatio(Double compressionRatio) { this.compressionRatio = compressionRatio; }
    
    public Boolean getIndexingEnabled() { return indexingEnabled; }
    public void setIndexingEnabled(Boolean indexingEnabled) { this.indexingEnabled = indexingEnabled; }
    
    public String getIndexType() { return indexType; }
    public void setIndexType(String indexType) { this.indexType = indexType; }
    
    public Integer getIndexCount() { return indexCount; }
    public void setIndexCount(Integer indexCount) { this.indexCount = indexCount; }
    
    public Boolean getQueryCacheEnabled() { return queryCacheEnabled; }
    public void setQueryCacheEnabled(Boolean queryCacheEnabled) { this.queryCacheEnabled = queryCacheEnabled; }
    
    public Double getCacheHitRate() { return cacheHitRate; }
    public void setCacheHitRate(Double cacheHitRate) { this.cacheHitRate = cacheHitRate; }
    
    public Boolean getParallelismEnabled() { return parallelismEnabled; }
    public void setParallelismEnabled(Boolean parallelismEnabled) { this.parallelismEnabled = parallelismEnabled; }
    
    public Integer getMaxParallelThreads() { return maxParallelThreads; }
    public void setMaxParallelThreads(Integer maxParallelThreads) { this.maxParallelThreads = maxParallelThreads; }
    
    public Boolean getHardwareAcceleration() { return hardwareAcceleration; }
    public void setHardwareAcceleration(Boolean hardwareAcceleration) { this.hardwareAcceleration = hardwareAcceleration; }
    
    public String getAcceleratorType() { return acceleratorType; }
    public void setAcceleratorType(String acceleratorType) { this.acceleratorType = acceleratorType; }
    
    public String getEncryptionContext() { return encryptionContext; }
    public void setEncryptionContext(String encryptionContext) { this.encryptionContext = encryptionContext; }
    
    public String getMetadataSchema() { return metadataSchema; }
    public void setMetadataSchema(String metadataSchema) { this.metadataSchema = metadataSchema; }
    
    public String getAccessControlPolicy() { return accessControlPolicy; }
    public void setAccessControlPolicy(String accessControlPolicy) { this.accessControlPolicy = accessControlPolicy; }
    
    public Integer getDataRetentionDays() { return dataRetentionDays; }
    public void setDataRetentionDays(Integer dataRetentionDays) { this.dataRetentionDays = dataRetentionDays; }
    
    public Boolean getAutoRekeyingEnabled() { return autoRekeyingEnabled; }
    public void setAutoRekeyingEnabled(Boolean autoRekeyingEnabled) { this.autoRekeyingEnabled = autoRekeyingEnabled; }
    
    public Integer getRekeyingIntervalDays() { return rekeyingIntervalDays; }
    public void setRekeyingIntervalDays(Integer rekeyingIntervalDays) { this.rekeyingIntervalDays = rekeyingIntervalDays; }
    
    public LocalDateTime getLastRekeyingTime() { return lastRekeyingTime; }
    public void setLastRekeyingTime(LocalDateTime lastRekeyingTime) { this.lastRekeyingTime = lastRekeyingTime; }
    
    public Boolean getBackupEnabled() { return backupEnabled; }
    public void setBackupEnabled(Boolean backupEnabled) { this.backupEnabled = backupEnabled; }
    
    public Integer getBackupIntervalHours() { return backupIntervalHours; }
    public void setBackupIntervalHours(Integer backupIntervalHours) { this.backupIntervalHours = backupIntervalHours; }
    
    public LocalDateTime getLastBackupTime() { return lastBackupTime; }
    public void setLastBackupTime(LocalDateTime lastBackupTime) { this.lastBackupTime = lastBackupTime; }
    
    public Integer getReplicationFactor() { return replicationFactor; }
    public void setReplicationFactor(Integer replicationFactor) { this.replicationFactor = replicationFactor; }
    
    public String getConsistencyLevel() { return consistencyLevel; }
    public void setConsistencyLevel(String consistencyLevel) { this.consistencyLevel = consistencyLevel; }
    
    public Boolean getShardingEnabled() { return shardingEnabled; }
    public void setShardingEnabled(Boolean shardingEnabled) { this.shardingEnabled = shardingEnabled; }
    
    public Integer getShardCount() { return shardCount; }
    public void setShardCount(Integer shardCount) { this.shardCount = shardCount; }
    
    public String getShardKey() { return shardKey; }
    public void setShardKey(String shardKey) { this.shardKey = shardKey; }
    
    public Boolean getAuditLoggingEnabled() { return auditLoggingEnabled; }
    public void setAuditLoggingEnabled(Boolean auditLoggingEnabled) { this.auditLoggingEnabled = auditLoggingEnabled; }
    
    public Integer getAuditRetentionDays() { return auditRetentionDays; }
    public void setAuditRetentionDays(Integer auditRetentionDays) { this.auditRetentionDays = auditRetentionDays; }
    
    public String getComplianceStandards() { return complianceStandards; }
    public void setComplianceStandards(String complianceStandards) { this.complianceStandards = complianceStandards; }
    
    public Double getPrivacyBudget() { return privacyBudget; }
    public void setPrivacyBudget(Double privacyBudget) { this.privacyBudget = privacyBudget; }
    
    public Double getPrivacyBudgetConsumed() { return privacyBudgetConsumed; }
    public void setPrivacyBudgetConsumed(Double privacyBudgetConsumed) { this.privacyBudgetConsumed = privacyBudgetConsumed; }
    
    public Boolean getDifferentialPrivacyEnabled() { return differentialPrivacyEnabled; }
    public void setDifferentialPrivacyEnabled(Boolean differentialPrivacyEnabled) { this.differentialPrivacyEnabled = differentialPrivacyEnabled; }
    
    public Double getDpEpsilon() { return dpEpsilon; }
    public void setDpEpsilon(Double dpEpsilon) { this.dpEpsilon = dpEpsilon; }
    
    public Double getDpDelta() { return dpDelta; }
    public void setDpDelta(Double dpDelta) { this.dpDelta = dpDelta; }
    
    public Integer getMaxQueryComplexity() { return maxQueryComplexity; }
    public void setMaxQueryComplexity(Integer maxQueryComplexity) { this.maxQueryComplexity = maxQueryComplexity; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    
    public LocalDateTime getUpdatedTime() { return updatedTime; }
    public void setUpdatedTime(LocalDateTime updatedTime) { this.updatedTime = updatedTime; }
    
    public LocalDateTime getLastAccessedTime() { return lastAccessedTime; }
    public void setLastAccessedTime(LocalDateTime lastAccessedTime) { this.lastAccessedTime = lastAccessedTime; }
    
    public String getPerformanceMetrics() { return performanceMetrics; }
    public void setPerformanceMetrics(String performanceMetrics) { this.performanceMetrics = performanceMetrics; }
    
    public Double getHealthScore() { return healthScore; }
    public void setHealthScore(Double healthScore) { this.healthScore = healthScore; }
    
    public Double getSecurityScore() { return securityScore; }
    public void setSecurityScore(Double securityScore) { this.securityScore = securityScore; }
    
    public Double getPrivacyScore() { return privacyScore; }
    public void setPrivacyScore(Double privacyScore) { this.privacyScore = privacyScore; }
    
    public BigDecimal getCostEstimateUsdPerMonth() { return costEstimateUsdPerMonth; }
    public void setCostEstimateUsdPerMonth(BigDecimal costEstimateUsdPerMonth) { this.costEstimateUsdPerMonth = costEstimateUsdPerMonth; }
    
    // 辅助方法
    public void incrementEncryptedDataCount() {
        this.encryptedDataCount++;
        this.updatedTime = LocalDateTime.now();
    }
    
    public void addDataSize(long sizeBytes) {
        this.totalDataSizeBytes += sizeBytes;
        this.updatedTime = LocalDateTime.now();
    }
    
    public void updateEncryptionTime(double timeMs) {
        this.encryptionTimeAvgMs = (this.encryptionTimeAvgMs * (encryptedDataCount - 1) + timeMs) / encryptedDataCount;
        this.updatedTime = LocalDateTime.now();
    }
    
    public void updateDecryptionTime(double timeMs) {
        this.decryptionTimeAvgMs = (this.decryptionTimeAvgMs * (encryptedDataCount - 1) + timeMs) / encryptedDataCount;
        this.updatedTime = LocalDateTime.now();
    }
    
    public void updateHomomorphicOpTime(double timeMs) {
        this.homomorphicOpTimeAvgMs = (this.homomorphicOpTimeAvgMs * (encryptedDataCount - 1) + timeMs) / encryptedDataCount;
        this.updatedTime = LocalDateTime.now();
    }
    
    public void consumePrivacyBudget(double amount) {
        this.privacyBudgetConsumed += amount;
        if (this.privacyBudgetConsumed > this.privacyBudget) {
            this.privacyBudgetConsumed = this.privacyBudget;
        }
        this.privacyScore = 100.0 * (1.0 - (this.privacyBudgetConsumed / this.privacyBudget));
        this.updatedTime = LocalDateTime.now();
    }
    
    public void resetPrivacyBudget() {
        this.privacyBudgetConsumed = 0.0;
        this.privacyScore = 100.0;
        this.updatedTime = LocalDateTime.now();
    }
    
    public void updateLastAccessedTime() {
        this.lastAccessedTime = LocalDateTime.now();
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("databaseId", databaseId);
        map.put("userId", userId);
        map.put("sessionId", sessionId);
        map.put("databaseName", databaseName);
        map.put("databaseType", databaseType);
        map.put("encryptionScheme", encryptionScheme);
        map.put("securityLevel", securityLevel);
        map.put("keySize", keySize);
        map.put("modulusSize", modulusSize);
        map.put("plaintextModulus", plaintextModulus);
        map.put("noiseBudget", noiseBudget);
        map.put("encryptedDataCount", encryptedDataCount);
        map.put("totalDataSizeBytes", totalDataSizeBytes);
        map.put("status", status);
        map.put("createdTime", createdTime);
        map.put("updatedTime", updatedTime);
        map.put("lastAccessedTime", lastAccessedTime);
        map.put("healthScore", healthScore);
        map.put("securityScore", securityScore);
        map.put("privacyScore", privacyScore);
        map.put("privacyBudget", privacyBudget);
        map.put("privacyBudgetConsumed", privacyBudgetConsumed);
        return map;
    }
    
    @Override
    public String toString() {
        return "HomomorphicEncryptionDatabaseEntity{" +
                "databaseId=" + databaseId +
                ", userId=" + userId +
                ", databaseName='" + databaseName + '\'' +
                ", databaseType='" + databaseType + '\'' +
                ", encryptionScheme='" + encryptionScheme + '\'' +
                ", securityLevel='" + securityLevel + '\'' +
                ", encryptedDataCount=" + encryptedDataCount +
                ", totalDataSizeBytes=" + totalDataSizeBytes +
                ", status='" + status + '\'' +
                ", createdTime=" + createdTime +
                ", healthScore=" + healthScore +
                ", securityScore=" + securityScore +
                ", privacyScore=" + privacyScore +
                '}';
    }
}