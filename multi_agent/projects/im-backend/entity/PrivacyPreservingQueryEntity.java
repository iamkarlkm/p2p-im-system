package com.im.backend.entity;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * 隐私保护查询实体
 * 支持加密SQL查询引擎、隐私保护连接、安全聚合查询、范围查询优化、模糊查询支持
 */
@Entity
@Table(name = "privacy_preserving_query")
public class PrivacyPreservingQueryEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "query_id")
    private Long queryId;
    
    @Column(name = "database_id", nullable = false)
    private Long databaseId;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "session_id", length = 64)
    private String sessionId;
    
    @Column(name = "query_uuid", nullable = false, length = 64, unique = true)
    private String queryUuid;
    
    @Column(name = "query_type", nullable = false, length = 32)
    private String queryType; // "SELECT", "INSERT", "UPDATE", "DELETE", "AGGREGATE", "JOIN", "RANGE", "FUZZY", "SPATIAL", "TEMPORAL"
    
    @Column(name = "query_complexity", nullable = false, length = 32)
    private String queryComplexity; // "SIMPLE", "MODERATE", "COMPLEX", "VERY_COMPLEX", "EXTREME"
    
    @Column(name = "query_sql", columnDefinition = "TEXT", nullable = false)
    private String querySql;
    
    @Column(name = "encrypted_query_sql", columnDefinition = "TEXT")
    private String encryptedQuerySql;
    
    @Column(name = "query_parameters", columnDefinition = "TEXT")
    private String queryParameters; // JSON格式的查询参数
    
    @Column(name = "encrypted_query_parameters", columnDefinition = "TEXT")
    private String encryptedQueryParameters;
    
    @Column(name = "privacy_level", nullable = false, length = 32)
    private String privacyLevel; // "PUBLIC", "LOW", "MEDIUM", "HIGH", "VERY_HIGH", "CONFIDENTIAL", "SECRET"
    
    @Column(name = "encryption_method", nullable = false, length = 32)
    private String encryptionMethod; // "HOMOMORPHIC", "ORDER_PRESERVING", "DETERMINISTIC", "SEARCHABLE", "RANDOMIZED"
    
    @Column(name = "encryption_key_hash", length = 64)
    private String encryptionKeyHash;
    
    @Column(name = "encryption_context", columnDefinition = "TEXT")
    private String encryptionContext; // JSON格式的加密上下文
    
    @Column(name = "query_filters", columnDefinition = "TEXT")
    private String queryFilters; // JSON格式的查询过滤条件
    
    @Column(name = "encrypted_filters", columnDefinition = "TEXT")
    private String encryptedFilters;
    
    @Column(name = "projection_fields", columnDefinition = "TEXT")
    private String projectionFields; // JSON数组：需要投影的字段
    
    @Column(name = "encrypted_projection", columnDefinition = "TEXT")
    private String encryptedProjection;
    
    @Column(name = "sort_fields", columnDefinition = "TEXT")
    private String sortFields; // JSON数组：排序字段
    
    @Column(name = "encrypted_sort", columnDefinition = "TEXT")
    private String encryptedSort;
    
    @Column(name = "limit_value", nullable = false)
    private Integer limitValue;
    
    @Column(name = "offset_value", nullable = false)
    private Integer offsetValue;
    
    @Column(name = "result_encryption_enabled", nullable = false)
    private Boolean resultEncryptionEnabled;
    
    @Column(name = "result_encryption_method", length = 32)
    private String resultEncryptionMethod;
    
    @Column(name = "result_encryption_key_hash", length = 64)
    private String resultEncryptionKeyHash;
    
    @Column(name = "privacy_budget_consumed", nullable = false)
    private Double privacyBudgetConsumed;
    
    @Column(name = "differential_privacy_enabled", nullable = false)
    private Boolean differentialPrivacyEnabled;
    
    @Column(name = "dp_epsilon_consumed", nullable = false)
    private Double dpEpsilonConsumed;
    
    @Column(name = "dp_delta_consumed", nullable = false)
    private Double dpDeltaConsumed;
    
    @Column(name = "noise_added_amount", nullable = false)
    private Double noiseAddedAmount;
    
    @Column(name = "noise_distribution", length = 32)
    private String noiseDistribution; // "LAPLACE", "GAUSSIAN", "EXPONENTIAL", "GEOMETRIC"
    
    @Column(name = "query_optimization_enabled", nullable = false)
    private Boolean queryOptimizationEnabled;
    
    @Column(name = "optimization_strategy", length = 64)
    private String optimizationStrategy; // "INDEX_ONLY", "BLOOM_FILTER", "PREDICATE_PUSHDOWN", "COLUMN_PRUNE", "JOIN_REORDER"
    
    @Column(name = "parallel_execution_enabled", nullable = false)
    private Boolean parallelExecutionEnabled;
    
    @Column(name = "parallel_degree", nullable = false)
    private Integer parallelDegree;
    
    @Column(name = "cache_enabled", nullable = false)
    private Boolean cacheEnabled;
    
    @Column(name = "cache_key", length = 128)
    private String cacheKey;
    
    @Column(name = "cache_hit", nullable = false)
    private Boolean cacheHit;
    
    @Column(name = "cache_ttl_seconds", nullable = false)
    private Integer cacheTtlSeconds;
    
    @Column(name = "audit_trail_enabled", nullable = false)
    private Boolean auditTrailEnabled;
    
    @Column(name = "audit_trail_id", length = 64)
    private String auditTrailId;
    
    @Column(name = "access_control_enforced", nullable = false)
    private Boolean accessControlEnforced;
    
    @Column(name = "access_policy_id", length = 64)
    private String accessPolicyId;
    
    @Column(name = "compliance_check_enabled", nullable = false)
    private Boolean complianceCheckEnabled;
    
    @Column(name = "compliance_status", length = 32)
    private String complianceStatus; // "COMPLIANT", "NON_COMPLIANT", "WARNING", "EXEMPT"
    
    @Column(name = "result_row_count", nullable = false)
    private Integer resultRowCount;
    
    @Column(name = "result_data_size_bytes", nullable = false)
    private Long resultDataSizeBytes;
    
    @Column(name = "encrypted_result_data_size_bytes", nullable = false)
    private Long encryptedResultDataSizeBytes;
    
    @Column(name = "compression_enabled", nullable = false)
    private Boolean compressionEnabled;
    
    @Column(name = "compression_ratio", nullable = false)
    private Double compressionRatio;
    
    @Column(name = "compression_algorithm", length = 32)
    private String compressionAlgorithm;
    
    @Column(name = "query_execution_time_ms", nullable = false)
    private Long queryExecutionTimeMs;
    
    @Column(name = "encryption_time_ms", nullable = false)
    private Long encryptionTimeMs;
    
    @Column(name = "decryption_time_ms", nullable = false)
    private Long decryptionTimeMs;
    
    @Column(name = "network_transfer_time_ms", nullable = false)
    private Long networkTransferTimeMs;
    
    @Column(name = "total_latency_ms", nullable = false)
    private Long totalLatencyMs;
    
    @Column(name = "cpu_usage_percent", nullable = false)
    private Double cpuUsagePercent;
    
    @Column(name = "memory_usage_bytes", nullable = false)
    private Long memoryUsageBytes;
    
    @Column(name = "disk_io_bytes", nullable = false)
    private Long diskIoBytes;
    
    @Column(name = "network_io_bytes", nullable = false)
    private Long networkIoBytes;
    
    @Column(name = "cost_estimate_usd", nullable = false)
    private BigDecimal costEstimateUsd;
    
    @Column(name = "error_occurred", nullable = false)
    private Boolean errorOccurred;
    
    @Column(name = "error_message", columnDefinition = "TEXT")
    private String errorMessage;
    
    @Column(name = "error_stack_trace", columnDefinition = "TEXT")
    private String errorStackTrace;
    
    @Column(name = "retry_count", nullable = false)
    private Integer retryCount;
    
    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries;
    
    @Column(name = "query_status", nullable = false, length = 32)
    private String queryStatus; // "PENDING", "EXECUTING", "ENCRYPTING", "DECRYPTING", "COMPRESSING", "SUCCESS", "FAILED", "TIMEOUT", "CANCELLED", "PARTIAL_SUCCESS"
    
    @Column(name = "result_verification_enabled", nullable = false)
    private Boolean resultVerificationEnabled;
    
    @Column(name = "verification_result", nullable = false)
    private Boolean verificationResult;
    
    @Column(name = "verification_method", length = 32)
    private String verificationMethod; // "ZK_SNARK", "ZK_STARK", "BULK_PROOF", "MERKLE_TREE", "SIGNATURE"
    
    @Column(name = "verification_proof", columnDefinition = "TEXT")
    private String verificationProof;
    
    @Column(name = "verification_time_ms", nullable = false)
    private Long verificationTimeMs;
    
    @Column(name = "created_time", nullable = false)
    private LocalDateTime createdTime;
    
    @Column(name = "start_time")
    private LocalDateTime startTime;
    
    @Column(name = "end_time")
    private LocalDateTime endTime;
    
    @Column(name = "last_updated_time", nullable = false)
    private LocalDateTime lastUpdatedTime;
    
    @Column(name = "expiration_time")
    private LocalDateTime expirationTime;
    
    @Column(name = "query_metrics", columnDefinition = "TEXT")
    private String queryMetrics; // JSON格式的查询指标
    
    @Column(name = "performance_score", nullable = false)
    private Double performanceScore;
    
    @Column(name = "privacy_score", nullable = false)
    private Double privacyScore;
    
    @Column(name = "accuracy_score", nullable = false)
    private Double accuracyScore;
    
    @Column(name = "compliance_score", nullable = false)
    private Double complianceScore;
    
    @Column(name = "user_satisfaction_score", nullable = false)
    private Double userSatisfactionScore;
    
    @Column(name = "result_delivery_method", length = 32)
    private String resultDeliveryMethod; // "STREAM", "BATCH", "PAGINATED", "PUSH_NOTIFICATION", "WEBHOOK"
    
    @Column(name = "result_destination", length = 512)
    private String resultDestination; // URL、队列、主题等
    
    @Column(name = "notification_sent", nullable = false)
    private Boolean notificationSent;
    
    @Column(name = "notification_time")
    private LocalDateTime notificationTime;
    
    @Column(name = "custom_metadata", columnDefinition = "TEXT")
    private String customMetadata; // JSON格式的自定义元数据
    
    // 构造函数
    public PrivacyPreservingQueryEntity() {
        this.queryUuid = java.util.UUID.randomUUID().toString();
        this.createdTime = LocalDateTime.now();
        this.lastUpdatedTime = LocalDateTime.now();
        this.limitValue = 100;
        this.offsetValue = 0;
        this.resultEncryptionEnabled = true;
        this.privacyBudgetConsumed = 0.0;
        this.differentialPrivacyEnabled = false;
        this.dpEpsilonConsumed = 0.0;
        this.dpDeltaConsumed = 0.0;
        this.noiseAddedAmount = 0.0;
        this.queryOptimizationEnabled = true;
        this.parallelExecutionEnabled = false;
        this.parallelDegree = 1;
        this.cacheEnabled = true;
        this.cacheHit = false;
        this.cacheTtlSeconds = 300;
        this.auditTrailEnabled = true;
        this.accessControlEnforced = true;
        this.complianceCheckEnabled = true;
        this.resultRowCount = 0;
        this.resultDataSizeBytes = 0L;
        this.encryptedResultDataSizeBytes = 0L;
        this.compressionEnabled = false;
        this.compressionRatio = 1.0;
        this.queryExecutionTimeMs = 0L;
        this.encryptionTimeMs = 0L;
        this.decryptionTimeMs = 0L;
        this.networkTransferTimeMs = 0L;
        this.totalLatencyMs = 0L;
        this.cpuUsagePercent = 0.0;
        this.memoryUsageBytes = 0L;
        this.diskIoBytes = 0L;
        this.networkIoBytes = 0L;
        this.costEstimateUsd = BigDecimal.ZERO;
        this.errorOccurred = false;
        this.retryCount = 0;
        this.maxRetries = 3;
        this.queryStatus = "PENDING";
        this.resultVerificationEnabled = false;
        this.verificationResult = false;
        this.verificationTimeMs = 0L;
        this.performanceScore = 0.0;
        this.privacyScore = 100.0;
        this.accuracyScore = 100.0;
        this.complianceScore = 100.0;
        this.userSatisfactionScore = 0.0;
        this.notificationSent = false;
    }
    
    // Getter 和 Setter 方法
    public Long getQueryId() { return queryId; }
    public void setQueryId(Long queryId) { this.queryId = queryId; }
    
    public Long getDatabaseId() { return databaseId; }
    public void setDatabaseId(Long databaseId) { this.databaseId = databaseId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }
    
    public String getQueryUuid() { return queryUuid; }
    public void setQueryUuid(String queryUuid) { this.queryUuid = queryUuid; }
    
    public String getQueryType() { return queryType; }
    public void setQueryType(String queryType) { this.queryType = queryType; }
    
    public String getQueryComplexity() { return queryComplexity; }
    public void setQueryComplexity(String queryComplexity) { this.queryComplexity = queryComplexity; }
    
    public String getQuerySql() { return querySql; }
    public void setQuerySql(String querySql) { this.querySql = querySql; }
    
    public String getEncryptedQuerySql() { return encryptedQuerySql; }
    public void setEncryptedQuerySql(String encryptedQuerySql) { this.encryptedQuerySql = encryptedQuerySql; }
    
    public String getQueryParameters() { return queryParameters; }
    public void setQueryParameters(String queryParameters) { this.queryParameters = queryParameters; }
    
    public String getEncryptedQueryParameters() { return encryptedQueryParameters; }
    public void setEncryptedQueryParameters(String encryptedQueryParameters) { this.encryptedQueryParameters = encryptedQueryParameters; }
    
    public String getPrivacyLevel() { return privacyLevel; }
    public void setPrivacyLevel(String privacyLevel) { this.privacyLevel = privacyLevel; }
    
    public String getEncryptionMethod() { return encryptionMethod; }
    public void setEncryptionMethod(String encryptionMethod) { this.encryptionMethod = encryptionMethod; }
    
    public String getEncryptionKeyHash() { return encryptionKeyHash; }
    public void setEncryptionKeyHash(String encryptionKeyHash) { this.encryptionKeyHash = encryptionKeyHash; }
    
    public String getEncryptionContext() { return encryptionContext; }
    public void setEncryptionContext(String encryptionContext) { this.encryptionContext = encryptionContext; }
    
    public String getQueryFilters() { return queryFilters; }
    public void setQueryFilters(String queryFilters) { this.queryFilters = queryFilters; }
    
    public String getEncryptedFilters() { return encryptedFilters; }
    public void setEncryptedFilters(String encryptedFilters) { this.encryptedFilters = encryptedFilters; }
    
    public String getProjectionFields() { return projectionFields; }
    public void setProjectionFields(String projectionFields) { this.projectionFields = projectionFields; }
    
    public String getEncryptedProjection() { return encryptedProjection; }
    public void setEncryptedProjection(String encryptedProjection) { this.encryptedProjection = encryptedProjection; }
    
    public String getSortFields() { return sortFields; }
    public void setSortFields(String sortFields) { this.sortFields = sortFields; }
    
    public String getEncryptedSort() { return encryptedSort; }
    public void setEncryptedSort(String encryptedSort) { this.encryptedSort = encryptedSort; }
    
    public Integer getLimitValue() { return limitValue; }
    public void setLimitValue(Integer limitValue) { this.limitValue = limitValue; }
    
    public Integer getOffsetValue() { return offsetValue; }
    public void setOffsetValue(Integer offsetValue) { this.offsetValue = offsetValue; }
    
    public Boolean getResultEncryptionEnabled() { return resultEncryptionEnabled; }
    public void setResultEncryptionEnabled(Boolean resultEncryptionEnabled) { this.resultEncryptionEnabled = resultEncryptionEnabled; }
    
    public String getResultEncryptionMethod() { return resultEncryptionMethod; }
    public void setResultEncryptionMethod(String resultEncryptionMethod) { this.resultEncryptionMethod = resultEncryptionMethod; }
    
    public String getResultEncryptionKeyHash() { return resultEncryptionKeyHash; }
    public void setResultEncryptionKeyHash(String resultEncryptionKeyHash) { this.resultEncryptionKeyHash = resultEncryptionKeyHash; }
    
    public Double getPrivacyBudgetConsumed() { return privacyBudgetConsumed; }
    public void setPrivacyBudgetConsumed(Double privacyBudgetConsumed) { this.privacyBudgetConsumed = privacyBudgetConsumed; }
    
    public Boolean getDifferentialPrivacyEnabled() { return differentialPrivacyEnabled; }
    public void setDifferentialPrivacyEnabled(Boolean differentialPrivacyEnabled) { this.differentialPrivacyEnabled = differentialPrivacyEnabled; }
    
    public Double getDpEpsilonConsumed() { return dpEpsilonConsumed; }
    public void setDpEpsilonConsumed(Double dpEpsilonConsumed) { this.dpEpsilonConsumed = dpEpsilonConsumed; }
    
    public Double getDpDeltaConsumed() { return dpDeltaConsumed; }
    public void setDpDeltaConsumed(Double dpDeltaConsumed) { this.dpDeltaConsumed = dpDeltaConsumed; }
    
    public Double getNoiseAddedAmount() { return noiseAddedAmount; }
    public void setNoiseAddedAmount(Double noiseAddedAmount) { this.noiseAddedAmount = noiseAddedAmount; }
    
    public String getNoiseDistribution() { return noiseDistribution; }
    public void setNoiseDistribution(String noiseDistribution) { this.noiseDistribution = noiseDistribution; }
    
    public Boolean getQueryOptimizationEnabled() { return queryOptimizationEnabled; }
    public void setQueryOptimizationEnabled(Boolean queryOptimizationEnabled) { this.queryOptimizationEnabled = queryOptimizationEnabled; }
    
    public String getOptimizationStrategy() { return optimizationStrategy; }
    public void setOptimizationStrategy(String optimizationStrategy) { this.optimizationStrategy = optimizationStrategy; }
    
    public Boolean getParallelExecutionEnabled() { return parallelExecutionEnabled; }
    public void setParallelExecutionEnabled(Boolean parallelExecutionEnabled) { this.parallelExecutionEnabled = parallelExecutionEnabled; }
    
    public Integer getParallelDegree() { return parallelDegree; }
    public void setParallelDegree(Integer parallelDegree) { this.parallelDegree = parallelDegree; }
    
    public Boolean getCacheEnabled() { return cacheEnabled; }
    public void setCacheEnabled(Boolean cacheEnabled) { this.cacheEnabled = cacheEnabled; }
    
    public String getCacheKey() { return cacheKey; }
    public void setCacheKey(String cacheKey) { this.cacheKey = cacheKey; }
    
    public Boolean getCacheHit() { return cacheHit; }
    public void setCacheHit(Boolean cacheHit) { this.cacheHit = cacheHit; }
    
    public Integer getCacheTtlSeconds() { return cacheTtlSeconds; }
    public void setCacheTtlSeconds(Integer cacheTtlSeconds) { this.cacheTtlSeconds = cacheTtlSeconds; }
    
    public Boolean getAuditTrailEnabled() { return auditTrailEnabled; }
    public void setAuditTrailEnabled(Boolean auditTrailEnabled) { this.auditTrailEnabled = auditTrailEnabled; }
    
    public String getAuditTrailId() { return auditTrailId; }
    public void setAuditTrailId(String auditTrailId) { this.auditTrailId = auditTrailId; }
    
    public Boolean getAccessControlEnforced() { return accessControlEnforced; }
    public void setAccessControlEnforced(Boolean accessControlEnforced) { this.accessControlEnforced = accessControlEnforced; }
    
    public String getAccessPolicyId() { return accessPolicyId; }
    public void setAccessPolicyId(String accessPolicyId) { this.accessPolicyId = accessPolicyId; }
    
    public Boolean getComplianceCheckEnabled() { return complianceCheckEnabled; }
    public void setComplianceCheckEnabled(Boolean complianceCheckEnabled) { this.complianceCheckEnabled = complianceCheckEnabled; }
    
    public String getComplianceStatus() { return complianceStatus; }
    public void setComplianceStatus(String complianceStatus) { this.complianceStatus = complianceStatus; }
    
    public Integer getResultRowCount() { return resultRowCount; }
    public void setResultRowCount(Integer resultRowCount) { this.resultRowCount = resultRowCount; }
    
    public Long getResultDataSizeBytes() { return resultDataSizeBytes; }
    public void setResultDataSizeBytes(Long resultDataSizeBytes) { this.resultDataSizeBytes = resultDataSizeBytes; }
    
    public Long getEncryptedResultDataSizeBytes() { return encryptedResultDataSizeBytes; }
    public void setEncryptedResultDataSizeBytes(Long encryptedResultDataSizeBytes) { this.encryptedResultDataSizeBytes = encryptedResultDataSizeBytes; }
    
    public Boolean getCompressionEnabled() { return compressionEnabled; }
    public void setCompressionEnabled(Boolean compressionEnabled) { this.compressionEnabled = compressionEnabled; }
    
    public Double getCompressionRatio() { return compressionRatio; }
    public void setCompressionRatio(Double compressionRatio) { this.compressionRatio = compressionRatio; }
    
    public String getCompressionAlgorithm() { return compressionAlgorithm; }
    public void setCompressionAlgorithm(String compressionAlgorithm) { this.compressionAlgorithm = compressionAlgorithm; }
    
    public Long getQueryExecutionTimeMs() { return queryExecutionTimeMs; }
    public void setQueryExecutionTimeMs(Long queryExecutionTimeMs) { this.queryExecutionTimeMs = queryExecutionTimeMs; }
    
    public Long getEncryptionTimeMs() { return encryptionTimeMs; }
    public void setEncryptionTimeMs(Long encryptionTimeMs) { this.encryptionTimeMs = encryptionTimeMs; }
    
    public Long getDecryptionTimeMs() { return decryptionTimeMs; }
    public void setDecryptionTimeMs(Long decryptionTimeMs) { this.decryptionTimeMs = decryptionTimeMs; }
    
    public Long getNetworkTransferTimeMs() { return networkTransferTimeMs; }
    public void setNetworkTransferTimeMs(Long networkTransferTimeMs) { this.networkTransferTimeMs = networkTransferTimeMs; }
    
    public Long getTotalLatencyMs() { return totalLatencyMs; }
    public void setTotalLatencyMs(Long totalLatencyMs) { this.totalLatencyMs = totalLatencyMs; }
    
    public Double getCpuUsagePercent() { return cpuUsagePercent; }
    public void setCpuUsagePercent(Double cpuUsagePercent) { this.cpuUsagePercent = cpuUsagePercent; }
    
    public Long getMemoryUsageBytes() { return memoryUsageBytes; }
    public void setMemoryUsageBytes(Long memoryUsageBytes) { this.memoryUsageBytes = memoryUsageBytes; }
    
    public Long getDiskIoBytes() { return diskIoBytes; }
    public void setDiskIoBytes(Long diskIoBytes) { this.diskIoBytes = diskIoBytes; }
    
    public Long getNetworkIoBytes() { return networkIoBytes; }
    public void setNetworkIoBytes(Long networkIoBytes) { this.networkIoBytes = networkIoBytes; }
    
    public BigDecimal getCostEstimateUsd() { return costEstimateUsd; }
    public void setCostEstimateUsd(BigDecimal costEstimateUsd) { this.costEstimateUsd = costEstimateUsd; }
    
    public Boolean getErrorOccurred() { return errorOccurred; }
    public void setErrorOccurred(Boolean errorOccurred) { this.errorOccurred = errorOccurred; }
    
    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    
    public String getErrorStackTrace() { return errorStackTrace; }
    public void setErrorStackTrace(String errorStackTrace) { this.errorStackTrace = errorStackTrace; }
    
    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }
    
    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }
    
    public String getQueryStatus() { return queryStatus; }
    public void setQueryStatus(String queryStatus) { this.queryStatus = queryStatus; }
    
    public Boolean getResultVerificationEnabled() { return resultVerificationEnabled; }
    public void setResultVerificationEnabled(Boolean resultVerificationEnabled) { this.resultVerificationEnabled = resultVerificationEnabled; }
    
    public Boolean getVerificationResult() { return verificationResult; }
    public void setVerificationResult(Boolean verificationResult) { this.verificationResult = verificationResult; }
    
    public String getVerificationMethod() { return verificationMethod; }
    public void setVerificationMethod(String verificationMethod) { this.verificationMethod = verificationMethod; }
    
    public String getVerificationProof() { return verificationProof; }
    public void setVerificationProof(String verificationProof) { this.verificationProof = verificationProof; }
    
    public Long getVerificationTimeMs() { return verificationTimeMs; }
    public void setVerificationTimeMs(Long verificationTimeMs) { this.verificationTimeMs = verificationTimeMs; }
    
    public LocalDateTime getCreatedTime() { return createdTime; }
    public void setCreatedTime(LocalDateTime createdTime) { this.createdTime = createdTime; }
    
    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
    
    public LocalDateTime getLastUpdatedTime() { return lastUpdatedTime; }
    public void setLastUpdatedTime(LocalDateTime lastUpdatedTime) { this.lastUpdatedTime = lastUpdatedTime; }
    
    public LocalDateTime getExpirationTime() { return expirationTime; }
    public void setExpirationTime(LocalDateTime expirationTime) { this.expirationTime = expirationTime; }
    
    public String getQueryMetrics() { return queryMetrics; }
    public void setQueryMetrics(String queryMetrics) { this.queryMetrics = queryMetrics; }
    
    public Double getPerformanceScore() { return performanceScore; }
    public void setPerformanceScore(Double performanceScore) { this.performanceScore = performanceScore; }
    
    public Double getPrivacyScore() { return privacyScore; }
    public void setPrivacyScore(Double privacyScore) { this.privacyScore = privacyScore; }
    
    public Double getAccuracyScore() { return accuracyScore; }
    public void setAccuracyScore(Double accuracyScore) { this.accuracyScore = accuracyScore; }
    
    public Double getComplianceScore() { return complianceScore; }
    public void setComplianceScore(Double complianceScore) { this.complianceScore = complianceScore; }
    
    public Double getUserSatisfactionScore() { return userSatisfactionScore; }
    public void setUserSatisfactionScore(Double userSatisfactionScore) { this.userSatisfactionScore = userSatisfactionScore; }
    
    public String getResultDeliveryMethod() { return resultDeliveryMethod; }
    public void setResultDeliveryMethod(String resultDeliveryMethod) { this.resultDeliveryMethod = resultDeliveryMethod; }
    
    public String getResultDestination() { return resultDestination; }
    public void setResultDestination(String resultDestination) { this.resultDestination = resultDestination; }
    
    public Boolean getNotificationSent() { return notificationSent; }
    public void setNotificationSent(Boolean notificationSent) { this.notificationSent = notificationSent; }
    
    public LocalDateTime getNotificationTime() { return notificationTime; }
    public void setNotificationTime(LocalDateTime notificationTime) { this.notificationTime = notificationTime; }
    
    public String getCustomMetadata() { return customMetadata; }
    public void setCustomMetadata(String customMetadata) { this.customMetadata = customMetadata; }
    
    // 辅助方法
    public void startExecution() {
        this.startTime = LocalDateTime.now();
        this.queryStatus = "EXECUTING";
        this.lastUpdatedTime = LocalDateTime.now();
    }
    
    public void completeExecution(Integer rowCount, Long dataSizeBytes, Long encryptedSizeBytes) {
        this.endTime = LocalDateTime.now();
        this.resultRowCount = rowCount;
        this.resultDataSizeBytes = dataSizeBytes;
        this.encryptedResultDataSizeBytes = encryptedSizeBytes;
        this.totalLatencyMs = java.time.Duration.between(startTime, endTime).toMillis();
        this.queryStatus = "SUCCESS";
        this.lastUpdatedTime = LocalDateTime.now();
        
        // 计算性能评分
        if (totalLatencyMs > 0) {
            this.performanceScore = Math.min(100.0, 10000.0 / Math.sqrt(totalLatencyMs));
        }
        
        // 计算准确度评分
        this.accuracyScore = errorOccurred ? 0.0 : 100.0;
    }
    
    public void failExecution(String errorMessage, String stackTrace) {
        this.endTime = LocalDateTime.now();
        this.errorOccurred = true;
        this.errorMessage = errorMessage;
        this.errorStackTrace = stackTrace;
        this.totalLatencyMs = java.time.Duration.between(startTime, endTime).toMillis();
        this.queryStatus = "FAILED";
        this.lastUpdatedTime = LocalDateTime.now();
        this.performanceScore = 0.0;
        this.accuracyScore = 0.0;
        this.privacyScore = Math.max(0.0, privacyScore - 10.0);
    }
    
    public void retryQuery() {
        this.retryCount++;
        this.queryStatus = "PENDING";
        this.errorOccurred = false;
        this.errorMessage = null;
        this.errorStackTrace = null;
        this.lastUpdatedTime = LocalDateTime.now();
    }
    
    public void consumePrivacyBudget(double amount) {
        this.privacyBudgetConsumed += amount;
        this.privacyScore = Math.max(0.0, 100.0 - (privacyBudgetConsumed * 10.0));
        this.lastUpdatedTime = LocalDateTime.now();
    }
    
    public void consumeDifferentialPrivacy(double epsilon, double delta) {
        this.dpEpsilonConsumed += epsilon;
        this.dpDeltaConsumed += delta;
        this.privacyScore = Math.max(0.0, 100.0 - (dpEpsilonConsumed * 50.0 + dpDeltaConsumed * 10000.0));
        this.lastUpdatedTime = LocalDateTime.now();
    }
    
    public void addNoise(double amount, String distribution) {
        this.noiseAddedAmount = amount;
        this.noiseDistribution = distribution;
        this.privacyScore = Math.min(100.0, privacyScore + (amount * 5.0));
        this.accuracyScore = Math.max(0.0, accuracyScore - (amount * 2.0));
        this.lastUpdatedTime = LocalDateTime.now();
    }
    
    public void updateCacheHit(boolean hit) {
        this.cacheHit = hit;
        if (hit) {
            this.performanceScore = Math.min(100.0, performanceScore + 20.0);
            this.totalLatencyMs = Math.max(0L, totalLatencyMs - 100);
        }
        this.lastUpdatedTime = LocalDateTime.now();
    }
    
    public void updateCompressionRatio(double ratio) {
        this.compressionRatio = ratio;
        if (ratio > 1.0) {
            this.performanceScore = Math.min(100.0, performanceScore + (ratio * 5.0));
        }
        this.lastUpdatedTime = LocalDateTime.now();
    }
    
    public void verifyResult(boolean valid, String method, String proof, long timeMs) {
        this.verificationResult = valid;
        this.verificationMethod = method;
        this.verificationProof = proof;
        this.verificationTimeMs = timeMs;
        this.complianceScore = valid ? Math.min(100.0, complianceScore + 10.0) : Math.max(0.0, complianceScore - 30.0);
        this.accuracyScore = valid ? Math.min(100.0, accuracyScore + 5.0) : 0.0;
        this.lastUpdatedTime = LocalDateTime.now();
    }
    
    public void sendNotification() {
        this.notificationSent = true;
        this.notificationTime = LocalDateTime.now();
        this.userSatisfactionScore = Math.min(100.0, userSatisfactionScore + 10.0);
        this.lastUpdatedTime = LocalDateTime.now();
    }
    
    public boolean canRetry() {
        return retryCount < maxRetries;
    }
    
    public boolean isExpired() {
        if (expirationTime == null) return false;
        return LocalDateTime.now().isAfter(expirationTime);
    }
    
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("queryId", queryId);
        map.put("databaseId", databaseId);
        map.put("userId", userId);
        map.put("queryUuid", queryUuid);
        map.put("queryType", queryType);
        map.put("queryComplexity", queryComplexity);
        map.put("privacyLevel", privacyLevel);
        map.put("resultRowCount", resultRowCount);
        map.put("resultDataSizeBytes", resultDataSizeBytes);
        map.put("queryExecutionTimeMs", queryExecutionTimeMs);
        map.put("totalLatencyMs", totalLatencyMs);
        map.put("queryStatus", queryStatus);
        map.put("errorOccurred", errorOccurred);
        map.put("performanceScore", performanceScore);
        map.put("privacyScore", privacyScore);
        map.put("accuracyScore", accuracyScore);
        map.put("complianceScore", complianceScore);
        map.put("userSatisfactionScore", userSatisfactionScore);
        map.put("createdTime", createdTime);
        map.put("startTime", startTime);
        map.put("endTime", endTime);
        return map;
    }
    
    @Override
    public String toString() {
        return "PrivacyPreservingQueryEntity{" +
                "queryId=" + queryId +
                ", databaseId=" + databaseId +
                ", userId=" + userId +
                ", queryUuid='" + queryUuid + '\'' +
                ", queryType='" + queryType + '\'' +
                ", resultRowCount=" + resultRowCount +
                ", queryExecutionTimeMs=" + queryExecutionTimeMs +
                ", totalLatencyMs=" + totalLatencyMs +
                ", queryStatus='" + queryStatus + '\'' +
                ", errorOccurred=" + errorOccurred +
                ", performanceScore=" + performanceScore +
                ", privacyScore=" + privacyScore +
                ", accuracyScore=" + accuracyScore +
                '}';
    }
}