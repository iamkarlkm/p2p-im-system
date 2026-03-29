package com.im.federated.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;

/**
 * 联邦学习训练记录实体类
 * 用于记录每个训练轮次的具体信息
 */
@Entity
@Table(name = "federated_training_records", 
       indexes = {
           @Index(name = "idx_task_id", columnList = "task_id"),
           @Index(name = "idx_round_number", columnList = "round_number"),
           @Index(name = "idx_client_id", columnList = "client_id"),
           @Index(name = "idx_status", columnList = "status")
       })
public class FederatedTrainingRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "task_id", nullable = false)
    private UUID taskId; // 关联的联邦学习任务ID

    @Column(name = "client_id", nullable = false)
    private String clientId; // 客户端ID

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber; // 训练轮次

    @Column(name = "record_type", nullable = false)
    private String recordType; // 记录类型: gradient_update, model_aggregation, validation, etc.

    @Column(name = "status", nullable = false)
    private String status; // 状态: pending, processing, completed, failed

    @Column(name = "gradient_size_bytes")
    private Long gradientSizeBytes; // 梯度数据大小（字节）

    @Column(name = "model_update_size_bytes")
    private Long modelUpdateSizeBytes; // 模型更新数据大小（字节）

    @Column(name = "local_accuracy")
    private Double localAccuracy; // 本地模型准确率

    @Column(name = "local_loss")
    private Double localLoss; // 本地模型损失

    @Column(name = "global_accuracy")
    private Double globalAccuracy; // 全局模型准确率

    @Column(name = "global_loss")
    private Double globalLoss; // 全局模型损失

    @ElementCollection
    @CollectionTable(name = "federated_training_metrics", 
                     joinColumns = @JoinColumn(name = "record_id"))
    @MapKeyColumn(name = "metric_name")
    @Column(name = "metric_value")
    private Map<String, Double> metrics; // 训练指标

    @Column(name = "training_time_seconds")
    private Long trainingTimeSeconds; // 训练耗时（秒）

    @Column(name = "communication_time_seconds")
    private Long communicationTimeSeconds; // 通信耗时（秒）

    @Column(name = "data_samples_used")
    private Integer dataSamplesUsed; // 使用的数据样本数

    @Column(name = "privacy_budget_used")
    private Double privacyBudgetUsed; // 使用的隐私预算

    @Column(name = "noise_added_level")
    private Double noiseAddedLevel; // 添加的噪声级别

    @Column(name = "gradient_norm_before_clipping")
    private Double gradientNormBeforeClipping; // 裁剪前的梯度范数

    @Column(name = "gradient_norm_after_clipping")
    private Double gradientNormAfterClipping; // 裁剪后的梯度范数

    @Column(name = "clipping_applied")
    private Boolean clippingApplied = false; // 是否应用了梯度裁剪

    @Column(name = "secure_aggregation_used")
    private Boolean secureAggregationUsed = false; // 是否使用了安全聚合

    @Column(name = "compression_applied")
    private Boolean compressionApplied = false; // 是否应用了压缩

    @Column(name = "compression_ratio_actual")
    private Double compressionRatioActual; // 实际压缩比率

    @Column(name = "compression_error")
    private Double compressionError; // 压缩误差

    @Column(name = "model_updates_count")
    private Integer modelUpdatesCount; // 模型更新次数

    @Column(name = "optimizer_type")
    private String optimizerType; // 优化器类型: sgd, adam, etc.

    @Column(name = "learning_rate_actual")
    private Double learningRateActual; // 实际学习率

    @Column(name = "batch_size_actual")
    private Integer batchSizeActual; // 实际批大小

    @Column(name = "epochs_completed")
    private Integer epochsCompleted; // 完成的迭代次数

    @Column(name = "loss_reduction_rate")
    private Double lossReductionRate; // 损失减少率

    @Column(name = "accuracy_improvement_rate")
    private Double accuracyImprovementRate; // 准确率提升率

    @Column(name = "data_distribution_label")
    private String dataDistributionLabel; // 数据分布标签

    @Column(name = "client_computation_capacity")
    private String clientComputationCapacity; // 客户端计算能力

    @Column(name = "client_network_bandwidth")
    private String clientNetworkBandwidth; // 客户端网络带宽

    @Column(name = "client_storage_available")
    private Long clientStorageAvailable; // 客户端可用存储（字节）

    @Column(name = "federated_averaging_weight")
    private Double federatedAveragingWeight; // 联邦平均权重

    @Column(name = "contribution_score")
    private Double contributionScore; // 贡献度评分

    @Column(name = "data_quality_score")
    private Double dataQualityScore; // 数据质量评分

    @Column(name = "model_convergence_indicator")
    private Boolean modelConvergenceIndicator; // 模型收敛指标

    @Column(name = "gradient_drift_indicator")
    private Double gradientDriftIndicator; // 梯度漂移指标

    @Column(name = "model_divergence_indicator")
    private Double modelDivergenceIndicator; // 模型发散指标

    @Column(name = "fault_recovery_attempts")
    private Integer faultRecoveryAttempts = 0; // 故障恢复尝试次数

    @Column(name = "failure_reason")
    private String failureReason; // 失败原因

    @Column(name = "error_details", columnDefinition = "TEXT")
    private String errorDetails; // 错误详情

    @Column(name = "retry_count")
    private Integer retryCount = 0; // 重试次数

    @Column(name = "max_retries")
    private Integer maxRetries = 3; // 最大重试次数

    @Column(name = "checkpoint_path")
    private String checkpointPath; // 检查点路径

    @Column(name = "gradient_data_path")
    private String gradientDataPath; // 梯度数据路径

    @Column(name = "model_update_path")
    private String modelUpdatePath; // 模型更新路径

    @Column(name = "validation_results_path")
    private String validationResultsPath; // 验证结果路径

    @Column(name = "hash_value")
    private String hashValue; // 数据哈希值

    @Column(name = "signature")
    private String signature; // 数据签名

    @Column(name = "encryption_key_id")
    private String encryptionKeyId; // 加密密钥ID

    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // 元数据（JSON格式）

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt; // 提交时间

    @Column(name = "acknowledged_at")
    private LocalDateTime acknowledgedAt; // 确认时间

    @Column(name = "processed_at")
    private LocalDateTime processedAt; // 处理时间

    @Column(name = "synchronized_at")
    private LocalDateTime synchronizedAt; // 同步时间

    @Column(name = "is_consolidated")
    private Boolean isConsolidated = false; // 是否已合并

    @Column(name = "is_validated")
    private Boolean isValidated = false; // 是否已验证

    @Column(name = "is_audited")
    private Boolean isAudited = false; // 是否已审计

    @Column(name = "audit_trail", columnDefinition = "TEXT")
    private String auditTrail; // 审计追踪

    @Version
    private Long version; // 乐观锁版本

    // 构造函数
    public FederatedTrainingRecordEntity() {
        this.createdAt = LocalDateTime.now();
        this.status = "pending";
    }

    public FederatedTrainingRecordEntity(UUID taskId, String clientId, Integer roundNumber, String recordType) {
        this();
        this.taskId = taskId;
        this.clientId = clientId;
        this.roundNumber = roundNumber;
        this.recordType = recordType;
    }

    // Getter和Setter方法
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTaskId() { return taskId; }
    public void setTaskId(UUID taskId) { this.taskId = taskId; }

    public String getClientId() { return clientId; }
    public void setClientId(String clientId) { this.clientId = clientId; }

    public Integer getRoundNumber() { return roundNumber; }
    public void setRoundNumber(Integer roundNumber) { this.roundNumber = roundNumber; }

    public String getRecordType() { return recordType; }
    public void setRecordType(String recordType) { this.recordType = recordType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getGradientSizeBytes() { return gradientSizeBytes; }
    public void setGradientSizeBytes(Long gradientSizeBytes) { this.gradientSizeBytes = gradientSizeBytes; }

    public Long getModelUpdateSizeBytes() { return modelUpdateSizeBytes; }
    public void setModelUpdateSizeBytes(Long modelUpdateSizeBytes) { this.modelUpdateSizeBytes = modelUpdateSizeBytes; }

    public Double getLocalAccuracy() { return localAccuracy; }
    public void setLocalAccuracy(Double localAccuracy) { this.localAccuracy = localAccuracy; }

    public Double getLocalLoss() { return localLoss; }
    public void setLocalLoss(Double localLoss) { this.localLoss = localLoss; }

    public Double getGlobalAccuracy() { return globalAccuracy; }
    public void setGlobalAccuracy(Double globalAccuracy) { this.globalAccuracy = globalAccuracy; }

    public Double getGlobalLoss() { return globalLoss; }
    public void setGlobalLoss(Double globalLoss) { this.globalLoss = globalLoss; }

    public Map<String, Double> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Double> metrics) { this.metrics = metrics; }

    public Long getTrainingTimeSeconds() { return trainingTimeSeconds; }
    public void setTrainingTimeSeconds(Long trainingTimeSeconds) { this.trainingTimeSeconds = trainingTimeSeconds; }

    public Long getCommunicationTimeSeconds() { return communicationTimeSeconds; }
    public void setCommunicationTimeSeconds(Long communicationTimeSeconds) { this.communicationTimeSeconds = communicationTimeSeconds; }

    public Integer getDataSamplesUsed() { return dataSamplesUsed; }
    public void setDataSamplesUsed(Integer dataSamplesUsed) { this.dataSamplesUsed = dataSamplesUsed; }

    public Double getPrivacyBudgetUsed() { return privacyBudgetUsed; }
    public void setPrivacyBudgetUsed(Double privacyBudgetUsed) { this.privacyBudgetUsed = privacyBudgetUsed; }

    public Double getNoiseAddedLevel() { return noiseAddedLevel; }
    public void setNoiseAddedLevel(Double noiseAddedLevel) { this.noiseAddedLevel = noiseAddedLevel; }

    public Double getGradientNormBeforeClipping() { return gradientNormBeforeClipping; }
    public void setGradientNormBeforeClipping(Double gradientNormBeforeClipping) { this.gradientNormBeforeClipping = gradientNormBeforeClipping; }

    public Double getGradientNormAfterClipping() { return gradientNormAfterClipping; }
    public void setGradientNormAfterClipping(Double gradientNormAfterClipping) { this.gradientNormAfterClipping = gradientNormAfterClipping; }

    public Boolean getClippingApplied() { return clippingApplied; }
    public void setClippingApplied(Boolean clippingApplied) { this.clippingApplied = clippingApplied; }

    public Boolean getSecureAggregationUsed() { return secureAggregationUsed; }
    public void setSecureAggregationUsed(Boolean secureAggregationUsed) { this.secureAggregationUsed = secureAggregationUsed; }

    public Boolean getCompressionApplied() { return compressionApplied; }
    public void setCompressionApplied(Boolean compressionApplied) { this.compressionApplied = compressionApplied; }

    public Double getCompressionRatioActual() { return compressionRatioActual; }
    public void setCompressionRatioActual(Double compressionRatioActual) { this.compressionRatioActual = compressionRatioActual; }

    public Double getCompressionError() { return compressionError; }
    public void setCompressionError(Double compressionError) { this.compressionError = compressionError; }

    public Integer getModelUpdatesCount() { return modelUpdatesCount; }
    public void setModelUpdatesCount(Integer modelUpdatesCount) { this.modelUpdatesCount = modelUpdatesCount; }

    public String getOptimizerType() { return optimizerType; }
    public void setOptimizerType(String optimizerType) { this.optimizerType = optimizerType; }

    public Double getLearningRateActual() { return learningRateActual; }
    public void setLearningRateActual(Double learningRateActual) { this.learningRateActual = learningRateActual; }

    public Integer getBatchSizeActual() { return batchSizeActual; }
    public void setBatchSizeActual(Integer batchSizeActual) { this.batchSizeActual = batchSizeActual; }

    public Integer getEpochsCompleted() { return epochsCompleted; }
    public void setEpochsCompleted(Integer epochsCompleted) { this.epochsCompleted = epochsCompleted; }

    public Double getLossReductionRate() { return lossReductionRate; }
    public void setLossReductionRate(Double lossReductionRate) { this.lossReductionRate = lossReductionRate; }

    public Double getAccuracyImprovementRate() { return accuracyImprovementRate; }
    public void setAccuracyImprovementRate(Double accuracyImprovementRate) { this.accuracyImprovementRate = accuracyImprovementRate; }

    public String getDataDistributionLabel() { return dataDistributionLabel; }
    public void setDataDistributionLabel(String dataDistributionLabel) { this.dataDistributionLabel = dataDistributionLabel; }

    public String getClientComputationCapacity() { return clientComputationCapacity; }
    public void setClientComputationCapacity(String clientComputationCapacity) { this.clientComputationCapacity = clientComputationCapacity; }

    public String getClientNetworkBandwidth() { return clientNetworkBandwidth; }
    public void setClientNetworkBandwidth(String clientNetworkBandwidth) { this.clientNetworkBandwidth = clientNetworkBandwidth; }

    public Long getClientStorageAvailable() { return clientStorageAvailable; }
    public void setClientStorageAvailable(Long clientStorageAvailable) { this.clientStorageAvailable = clientStorageAvailable; }

    public Double getFederatedAveragingWeight() { return federatedAveragingWeight; }
    public void setFederatedAveragingWeight(Double federatedAveragingWeight) { this.federatedAveragingWeight = federatedAveragingWeight; }

    public Double getContributionScore() { return contributionScore; }
    public void setContributionScore(Double contributionScore) { this.contributionScore = contributionScore; }

    public Double getDataQualityScore() { return dataQualityScore; }
    public void setDataQualityScore(Double dataQualityScore) { this.dataQualityScore = dataQualityScore; }

    public Boolean getModelConvergenceIndicator() { return modelConvergenceIndicator; }
    public void setModelConvergenceIndicator(Boolean modelConvergenceIndicator) { this.modelConvergenceIndicator = modelConvergenceIndicator; }

    public Double getGradientDriftIndicator() { return gradientDriftIndicator; }
    public void setGradientDriftIndicator(Double gradientDriftIndicator) { this.gradientDriftIndicator = gradientDriftIndicator; }

    public Double getModelDivergenceIndicator() { return modelDivergenceIndicator; }
    public void setModelDivergenceIndicator(Double modelDivergenceIndicator) { this.modelDivergenceIndicator = modelDivergenceIndicator; }

    public Integer getFaultRecoveryAttempts() { return faultRecoveryAttempts; }
    public void setFaultRecoveryAttempts(Integer faultRecoveryAttempts) { this.faultRecoveryAttempts = faultRecoveryAttempts; }

    public String getFailureReason() { return failureReason; }
    public void setFailureReason(String failureReason) { this.failureReason = failureReason; }

    public String getErrorDetails() { return errorDetails; }
    public void setErrorDetails(String errorDetails) { this.errorDetails = errorDetails; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }

    public String getCheckpointPath() { return checkpointPath; }
    public void setCheckpointPath(String checkpointPath) { this.checkpointPath = checkpointPath; }

    public String getGradientDataPath() { return gradientDataPath; }
    public void setGradientDataPath(String gradientDataPath) { this.gradientDataPath = gradientDataPath; }

    public String getModelUpdatePath() { return modelUpdatePath; }
    public void setModelUpdatePath(String modelUpdatePath) { this.modelUpdatePath = modelUpdatePath; }

    public String getValidationResultsPath() { return validationResultsPath; }
    public void setValidationResultsPath(String validationResultsPath) { this.validationResultsPath = validationResultsPath; }

    public String getHashValue() { return hashValue; }
    public void setHashValue(String hashValue) { this.hashValue = hashValue; }

    public String getSignature() { return signature; }
    public void setSignature(String signature) { this.signature = signature; }

    public String getEncryptionKeyId() { return encryptionKeyId; }
    public void setEncryptionKeyId(String encryptionKeyId) { this.encryptionKeyId = encryptionKeyId; }

    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getAcknowledgedAt() { return acknowledgedAt; }
    public void setAcknowledgedAt(LocalDateTime acknowledgedAt) { this.acknowledgedAt = acknowledgedAt; }

    public LocalDateTime getProcessedAt() { return processedAt; }
    public void setProcessedAt(LocalDateTime processedAt) { this.processedAt = processedAt; }

    public LocalDateTime getSynchronizedAt() { return synchronizedAt; }
    public void setSynchronizedAt(LocalDateTime synchronizedAt) { this.synchronizedAt = synchronizedAt; }

    public Boolean getIsConsolidated() { return isConsolidated; }
    public void setIsConsolidated(Boolean isConsolidated) { this.isConsolidated = isConsolidated; }

    public Boolean getIsValidated() { return isValidated; }
    public void setIsValidated(Boolean isValidated) { this.isValidated = isValidated; }

    public Boolean getIsAudited() { return isAudited; }
    public void setIsAudited(Boolean isAudited) { this.isAudited = isAudited; }

    public String getAuditTrail() { return auditTrail; }
    public void setAuditTrail(String auditTrail) { this.auditTrail = auditTrail; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    // 辅助方法
    public void startProcessing() {
        this.status = "processing";
        this.startedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void completeProcessing(Double localAccuracy, Double localLoss, Long trainingTimeSeconds) {
        this.status = "completed";
        this.localAccuracy = localAccuracy;
        this.localLoss = localLoss;
        this.trainingTimeSeconds = trainingTimeSeconds;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void failProcessing(String failureReason, String errorDetails) {
        this.status = "failed";
        this.failureReason = failureReason;
        this.errorDetails = errorDetails;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void submitForAggregation() {
        this.submittedAt = LocalDateTime.now();
        this.status = "submitted";
        this.updatedAt = LocalDateTime.now();
    }

    public void acknowledgeSubmission() {
        this.acknowledgedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsConsolidated() {
        this.isConsolidated = true;
        this.synchronizedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsValidated() {
        this.isValidated = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsAudited(String auditNotes) {
        this.isAudited = true;
        this.auditTrail = auditNotes;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementRetryCount() {
        this.retryCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementFaultRecoveryAttempts() {
        this.faultRecoveryAttempts++;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateMetric(String metricName, Double value) {
        if (this.metrics == null) {
            this.metrics = new java.util.HashMap<>();
        }
        this.metrics.put(metricName, value);
        this.updatedAt = LocalDateTime.now();
    }

    public void setPrivacyParameters(Double privacyBudgetUsed, Double noiseAddedLevel) {
        this.privacyBudgetUsed = privacyBudgetUsed;
        this.noiseAddedLevel = noiseAddedLevel;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCompressionParameters(Boolean compressionApplied, Double compressionRatioActual, Double compressionError) {
        this.compressionApplied = compressionApplied;
        this.compressionRatioActual = compressionRatioActual;
        this.compressionError = compressionError;
        this.updatedAt = LocalDateTime.now();
    }

    public void setGradientClippingParameters(Double gradientNormBeforeClipping, Double gradientNormAfterClipping) {
        this.gradientNormBeforeClipping = gradientNormBeforeClipping;
        this.gradientNormAfterClipping = gradientNormAfterClipping;
        this.clippingApplied = true;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("FederatedTrainingRecordEntity{id=%s, taskId=%s, clientId='%s', round=%d, type='%s', status='%s'}",
                id, taskId, clientId, roundNumber, recordType, status);
    }
}