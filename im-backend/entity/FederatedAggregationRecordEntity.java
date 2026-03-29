package com.im.federated.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.UUID;

/**
 * 联邦学习聚合记录实体类
 * 用于记录模型聚合过程的信息
 */
@Entity
@Table(name = "federated_aggregation_records", 
       indexes = {
           @Index(name = "idx_task_id", columnList = "task_id"),
           @Index(name = "idx_round_number", columnList = "round_number"),
           @Index(name = "idx_aggregation_status", columnList = "status"),
           @Index(name = "idx_aggregation_time", columnList = "completed_at")
       })
public class FederatedAggregationRecordEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "task_id", nullable = false)
    private UUID taskId; // 关联的联邦学习任务ID

    @Column(name = "round_number", nullable = false)
    private Integer roundNumber; // 聚合轮次

    @Column(name = "aggregation_method", nullable = false)
    private String aggregationMethod; // 聚合方法: fedavg, fedprox, fednova, etc.

    @Column(name = "status", nullable = false)
    private String status; // 状态: pending, aggregating, completed, failed

    @Column(name = "total_clients_participated")
    private Integer totalClientsParticipated; // 参与聚合的客户端总数

    @ElementCollection
    @CollectionTable(name = "federated_aggregation_clients", 
                     joinColumns = @JoinColumn(name = "aggregation_id"))
    @Column(name = "client_id")
    private List<String> clientIds; // 参与聚合的客户端ID列表

    @Column(name = "clients_successful")
    private Integer clientsSuccessful; // 成功参与聚合的客户端数

    @Column(name = "clients_failed")
    private Integer clientsFailed; // 聚合失败的客户端数

    @Column(name = "clients_timed_out")
    private Integer clientsTimedOut; // 超时的客户端数

    @ElementCollection
    @CollectionTable(name = "federated_client_weights", 
                     joinColumns = @JoinColumn(name = "aggregation_id"))
    @MapKeyColumn(name = "client_id")
    @Column(name = "client_weight")
    private Map<String, Double> clientWeights; // 客户端权重映射

    @Column(name = "total_data_samples")
    private Long totalDataSamples; // 总数据样本数

    @Column(name = "average_accuracy_before")
    private Double averageAccuracyBefore; // 聚合前平均准确率

    @Column(name = "average_loss_before")
    private Double averageLossBefore; // 聚合前平均损失

    @Column(name = "accuracy_after")
    private Double accuracyAfter; // 聚合后准确率

    @Column(name = "loss_after")
    private Double lossAfter; // 聚合后损失

    @Column(name = "accuracy_improvement")
    private Double accuracyImprovement; // 准确率提升

    @Column(name = "loss_reduction")
    private Double lossReduction; // 损失减少

    @ElementCollection
    @CollectionTable(name = "federated_aggregation_metrics", 
                     joinColumns = @JoinColumn(name = "aggregation_id"))
    @MapKeyColumn(name = "metric_name")
    @Column(name = "metric_value")
    private Map<String, Double> metrics; // 聚合指标

    @Column(name = "aggregation_time_seconds")
    private Long aggregationTimeSeconds; // 聚合耗时（秒）

    @Column(name = "model_update_size_bytes")
    private Long modelUpdateSizeBytes; // 模型更新数据大小（字节）

    @Column(name = "aggregated_model_size_bytes")
    private Long aggregatedModelSizeBytes; // 聚合后模型大小（字节）

    @Column(name = "secure_aggregation_applied")
    private Boolean secureAggregationApplied = false; // 是否应用了安全聚合

    @Column(name = "secure_aggregation_protocol")
    private String secureAggregationProtocol; // 安全聚合协议

    @Column(name = "secure_aggregation_key_id")
    private String secureAggregationKeyId; // 安全聚合密钥ID

    @Column(name = "privacy_budget_consumed")
    private Double privacyBudgetConsumed; // 消耗的隐私预算

    @Column(name = "differential_privacy_epsilon")
    private Double differentialPrivacyEpsilon; // 差分隐私ε值

    @Column(name = "differential_privacy_delta")
    private Double differentialPrivacyDelta; // 差分隐私δ值

    @Column(name = "noise_added_level")
    private Double noiseAddedLevel; // 添加的噪声级别

    @Column(name = "clipping_applied")
    private Boolean clippingApplied = false; // 是否应用了梯度裁剪

    @Column(name = "clipping_threshold")
    private Double clippingThreshold; // 裁剪阈值

    @Column(name = "compression_applied")
    private Boolean compressionApplied = false; // 是否应用了压缩

    @Column(name = "compression_ratio")
    private Double compressionRatio; // 压缩比率

    @Column(name = "compression_error")
    private Double compressionError; // 压缩误差

    @Column(name = "communication_efficiency_score")
    private Double communicationEfficiencyScore; // 通信效率评分

    @Column(name = "aggregation_quality_score")
    private Double aggregationQualityScore; // 聚合质量评分

    @Column(name = "model_convergence_indicator")
    private Boolean modelConvergenceIndicator; // 模型收敛指标

    @Column(name = "model_divergence_indicator")
    private Double modelDivergenceIndicator; // 模型发散指标

    @Column(name = "gradient_drift_indicator")
    private Double gradientDriftIndicator; // 梯度漂移指标

    @Column(name = "client_contribution_variance")
    private Double clientContributionVariance; // 客户端贡献方差

    @Column(name = "data_distribution_skewness")
    private Double dataDistributionSkewness; // 数据分布偏度

    @Column(name = "fairness_score")
    private Double fairnessScore; // 公平性评分

    @Column(name = "robustness_score")
    private Double robustnessScore; // 鲁棒性评分

    @Column(name = "fault_tolerance_score")
    private Double faultToleranceScore; // 容错性评分

    @Column(name = "aggregated_model_path")
    private String aggregatedModelPath; // 聚合模型保存路径

    @Column(name = "aggregated_gradient_path")
    private String aggregatedGradientPath; // 聚合梯度保存路径

    @Column(name = "aggregation_checkpoint_path")
    private String aggregationCheckpointPath; // 聚合检查点路径

    @Column(name = "validation_results_path")
    private String validationResultsPath; // 验证结果路径

    @Column(name = "performance_report_path")
    private String performanceReportPath; // 性能报告路径

    @Column(name = "aggregation_log_path")
    private String aggregationLogPath; // 聚合日志路径

    @Column(name = "aggregation_hash")
    private String aggregationHash; // 聚合数据哈希值

    @Column(name = "aggregation_signature")
    private String aggregationSignature; // 聚合数据签名

    @Column(name = "aggregation_certificate_id")
    private String aggregationCertificateId; // 聚合证书ID

    @Column(name = "verification_status")
    private String verificationStatus; // 验证状态

    @Column(name = "audit_trail", columnDefinition = "TEXT")
    private String auditTrail; // 审计追踪

    @Column(name = "error_details", columnDefinition = "TEXT")
    private String errorDetails; // 错误详情

    @Column(name = "retry_count")
    private Integer retryCount = 0; // 重试次数

    @Column(name = "max_retries")
    private Integer maxRetries = 3; // 最大重试次数

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "broadcast_at")
    private LocalDateTime broadcastAt; // 广播时间

    @Column(name = "synchronized_at")
    private LocalDateTime synchronizedAt; // 同步时间

    @Column(name = "is_consolidated")
    private Boolean isConsolidated = false; // 是否已合并

    @Column(name = "is_validated")
    private Boolean isValidated = false; // 是否已验证

    @Column(name = "is_audited")
    private Boolean isAudited = false; // 是否已审计

    @Column(name = "is_finalized")
    private Boolean isFinalized = false; // 是否已最终化

    @Version
    private Long version; // 乐观锁版本

    // 构造函数
    public FederatedAggregationRecordEntity() {
        this.createdAt = LocalDateTime.now();
        this.status = "pending";
    }

    public FederatedAggregationRecordEntity(UUID taskId, Integer roundNumber, String aggregationMethod) {
        this();
        this.taskId = taskId;
        this.roundNumber = roundNumber;
        this.aggregationMethod = aggregationMethod;
    }

    // Getter和Setter方法
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getTaskId() { return taskId; }
    public void setTaskId(UUID taskId) { this.taskId = taskId; }

    public Integer getRoundNumber() { return roundNumber; }
    public void setRoundNumber(Integer roundNumber) { this.roundNumber = roundNumber; }

    public String getAggregationMethod() { return aggregationMethod; }
    public void setAggregationMethod(String aggregationMethod) { this.aggregationMethod = aggregationMethod; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getTotalClientsParticipated() { return totalClientsParticipated; }
    public void setTotalClientsParticipated(Integer totalClientsParticipated) { this.totalClientsParticipated = totalClientsParticipated; }

    public List<String> getClientIds() { return clientIds; }
    public void setClientIds(List<String> clientIds) { this.clientIds = clientIds; }

    public Integer getClientsSuccessful() { return clientsSuccessful; }
    public void setClientsSuccessful(Integer clientsSuccessful) { this.clientsSuccessful = clientsSuccessful; }

    public Integer getClientsFailed() { return clientsFailed; }
    public void setClientsFailed(Integer clientsFailed) { this.clientsFailed = clientsFailed; }

    public Integer getClientsTimedOut() { return clientsTimedOut; }
    public void setClientsTimedOut(Integer clientsTimedOut) { this.clientsTimedOut = clientsTimedOut; }

    public Map<String, Double> getClientWeights() { return clientWeights; }
    public void setClientWeights(Map<String, Double> clientWeights) { this.clientWeights = clientWeights; }

    public Long getTotalDataSamples() { return totalDataSamples; }
    public void setTotalDataSamples(Long totalDataSamples) { this.totalDataSamples = totalDataSamples; }

    public Double getAverageAccuracyBefore() { return averageAccuracyBefore; }
    public void setAverageAccuracyBefore(Double averageAccuracyBefore) { this.averageAccuracyBefore = averageAccuracyBefore; }

    public Double getAverageLossBefore() { return averageLossBefore; }
    public void setAverageLossBefore(Double averageLossBefore) { this.averageLossBefore = averageLossBefore; }

    public Double getAccuracyAfter() { return accuracyAfter; }
    public void setAccuracyAfter(Double accuracyAfter) { this.accuracyAfter = accuracyAfter; }

    public Double getLossAfter() { return lossAfter; }
    public void setLossAfter(Double lossAfter) { this.lossAfter = lossAfter; }

    public Double getAccuracyImprovement() { return accuracyImprovement; }
    public void setAccuracyImprovement(Double accuracyImprovement) { this.accuracyImprovement = accuracyImprovement; }

    public Double getLossReduction() { return lossReduction; }
    public void setLossReduction(Double lossReduction) { this.lossReduction = lossReduction; }

    public Map<String, Double> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Double> metrics) { this.metrics = metrics; }

    public Long getAggregationTimeSeconds() { return aggregationTimeSeconds; }
    public void setAggregationTimeSeconds(Long aggregationTimeSeconds) { this.aggregationTimeSeconds = aggregationTimeSeconds; }

    public Long getModelUpdateSizeBytes() { return modelUpdateSizeBytes; }
    public void setModelUpdateSizeBytes(Long modelUpdateSizeBytes) { this.modelUpdateSizeBytes = modelUpdateSizeBytes; }

    public Long getAggregatedModelSizeBytes() { return aggregatedModelSizeBytes; }
    public void setAggregatedModelSizeBytes(Long aggregatedModelSizeBytes) { this.aggregatedModelSizeBytes = aggregatedModelSizeBytes; }

    public Boolean getSecureAggregationApplied() { return secureAggregationApplied; }
    public void setSecureAggregationApplied(Boolean secureAggregationApplied) { this.secureAggregationApplied = secureAggregationApplied; }

    public String getSecureAggregationProtocol() { return secureAggregationProtocol; }
    public void setSecureAggregationProtocol(String secureAggregationProtocol) { this.secureAggregationProtocol = secureAggregationProtocol; }

    public String getSecureAggregationKeyId() { return secureAggregationKeyId; }
    public void setSecureAggregationKeyId(String secureAggregationKeyId) { this.secureAggregationKeyId = secureAggregationKeyId; }

    public Double getPrivacyBudgetConsumed() { return privacyBudgetConsumed; }
    public void setPrivacyBudgetConsumed(Double privacyBudgetConsumed) { this.privacyBudgetConsumed = privacyBudgetConsumed; }

    public Double getDifferentialPrivacyEpsilon() { return differentialPrivacyEpsilon; }
    public void setDifferentialPrivacyEpsilon(Double differentialPrivacyEpsilon) { this.differentialPrivacyEpsilon = differentialPrivacyEpsilon; }

    public Double getDifferentialPrivacyDelta() { return differentialPrivacyDelta; }
    public void setDifferentialPrivacyDelta(Double differentialPrivacyDelta) { this.differentialPrivacyDelta = differentialPrivacyDelta; }

    public Double getNoiseAddedLevel() { return noiseAddedLevel; }
    public void setNoiseAddedLevel(Double noiseAddedLevel) { this.noiseAddedLevel = noiseAddedLevel; }

    public Boolean getClippingApplied() { return clippingApplied; }
    public void setClippingApplied(Boolean clippingApplied) { this.clippingApplied = clippingApplied; }

    public Double getClippingThreshold() { return clippingThreshold; }
    public void setClippingThreshold(Double clippingThreshold) { this.clippingThreshold = clippingThreshold; }

    public Boolean getCompressionApplied() { return compressionApplied; }
    public void setCompressionApplied(Boolean compressionApplied) { this.compressionApplied = compressionApplied; }

    public Double getCompressionRatio() { return compressionRatio; }
    public void setCompressionRatio(Double compressionRatio) { this.compressionRatio = compressionRatio; }

    public Double getCompressionError() { return compressionError; }
    public void setCompressionError(Double compressionError) { this.compressionError = compressionError; }

    public Double getCommunicationEfficiencyScore() { return communicationEfficiencyScore; }
    public void setCommunicationEfficiencyScore(Double communicationEfficiencyScore) { this.communicationEfficiencyScore = communicationEfficiencyScore; }

    public Double getAggregationQualityScore() { return aggregationQualityScore; }
    public void setAggregationQualityScore(Double aggregationQualityScore) { this.aggregationQualityScore = aggregationQualityScore; }

    public Boolean getModelConvergenceIndicator() { return modelConvergenceIndicator; }
    public void setModelConvergenceIndicator(Boolean modelConvergenceIndicator) { this.modelConvergenceIndicator = modelConvergenceIndicator; }

    public Double getModelDivergenceIndicator() { return modelDivergenceIndicator; }
    public void setModelDivergenceIndicator(Double modelDivergenceIndicator) { this.modelDivergenceIndicator = modelDivergenceIndicator; }

    public Double getGradientDriftIndicator() { return gradientDriftIndicator; }
    public void setGradientDriftIndicator(Double gradientDriftIndicator) { this.gradientDriftIndicator = gradientDriftIndicator; }

    public Double getClientContributionVariance() { return clientContributionVariance; }
    public void setClientContributionVariance(Double clientContributionVariance) { this.clientContributionVariance = clientContributionVariance; }

    public Double getDataDistributionSkewness() { return dataDistributionSkewness; }
    public void setDataDistributionSkewness(Double dataDistributionSkewness) { this.dataDistributionSkewness = dataDistributionSkewness; }

    public Double getFairnessScore() { return fairnessScore; }
    public void setFairnessScore(Double fairnessScore) { this.fairnessScore = fairnessScore; }

    public Double getRobustnessScore() { return robustnessScore; }
    public void setRobustnessScore(Double robustnessScore) { this.robustnessScore = robustnessScore; }

    public Double getFaultToleranceScore() { return faultToleranceScore; }
    public void setFaultToleranceScore(Double faultToleranceScore) { this.faultToleranceScore = faultToleranceScore; }

    public String getAggregatedModelPath() { return aggregatedModelPath; }
    public void setAggregatedModelPath(String aggregatedModelPath) { this.aggregatedModelPath = aggregatedModelPath; }

    public String getAggregatedGradientPath() { return aggregatedGradientPath; }
    public void setAggregatedGradientPath(String aggregatedGradientPath) { this.aggregatedGradientPath = aggregatedGradientPath; }

    public String getAggregationCheckpointPath() { return aggregationCheckpointPath; }
    public void setAggregationCheckpointPath(String aggregationCheckpointPath) { this.aggregationCheckpointPath = aggregationCheckpointPath; }

    public String getValidationResultsPath() { return validationResultsPath; }
    public void setValidationResultsPath(String validationResultsPath) { this.validationResultsPath = validationResultsPath; }

    public String getPerformanceReportPath() { return performanceReportPath; }
    public void setPerformanceReportPath(String performanceReportPath) { this.performanceReportPath = performanceReportPath; }

    public String getAggregationLogPath() { return aggregationLogPath; }
    public void setAggregationLogPath(String aggregationLogPath) { this.aggregationLogPath = aggregationLogPath; }

    public String getAggregationHash() { return aggregationHash; }
    public void setAggregationHash(String aggregationHash) { this.aggregationHash = aggregationHash; }

    public String getAggregationSignature() { return aggregationSignature; }
    public void setAggregationSignature(String aggregationSignature) { this.aggregationSignature = aggregationSignature; }

    public String getAggregationCertificateId() { return aggregationCertificateId; }
    public void setAggregationCertificateId(String aggregationCertificateId) { this.aggregationCertificateId = aggregationCertificateId; }

    public String getVerificationStatus() { return verificationStatus; }
    public void setVerificationStatus(String verificationStatus) { this.verificationStatus = verificationStatus; }

    public String getAuditTrail() { return auditTrail; }
    public void setAuditTrail(String auditTrail) { this.auditTrail = auditTrail; }

    public String getErrorDetails() { return errorDetails; }
    public void setErrorDetails(String errorDetails) { this.errorDetails = errorDetails; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getBroadcastAt() { return broadcastAt; }
    public void setBroadcastAt(LocalDateTime broadcastAt) { this.broadcastAt = broadcastAt; }

    public LocalDateTime getSynchronizedAt() { return synchronizedAt; }
    public void setSynchronizedAt(LocalDateTime synchronizedAt) { this.synchronizedAt = synchronizedAt; }

    public Boolean getIsConsolidated() { return isConsolidated; }
    public void setIsConsolidated(Boolean isConsolidated) { this.isConsolidated = isConsolidated; }

    public Boolean getIsValidated() { return isValidated; }
    public void setIsValidated(Boolean isValidated) { this.isValidated = isValidated; }

    public Boolean getIsAudited() { return isAudited; }
    public void setIsAudited(Boolean isAudited) { this.isAudited = isAudited; }

    public Boolean getIsFinalized() { return isFinalized; }
    public void setIsFinalized(Boolean isFinalized) { this.isFinalized = isFinalized; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    // 辅助方法
    public void startAggregation() {
        this.status = "aggregating";
        this.startedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void completeAggregation(Double accuracyAfter, Double lossAfter, Long aggregationTimeSeconds) {
        this.status = "completed";
        this.accuracyAfter = accuracyAfter;
        this.lossAfter = lossAfter;
        this.aggregationTimeSeconds = aggregationTimeSeconds;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        
        // 计算提升指标
        if (this.averageAccuracyBefore != null && this.accuracyAfter != null) {
            this.accuracyImprovement = this.accuracyAfter - this.averageAccuracyBefore;
        }
        if (this.averageLossBefore != null && this.lossAfter != null) {
            this.lossReduction = this.averageLossBefore - this.lossAfter;
        }
    }

    public void failAggregation(String errorDetails) {
        this.status = "failed";
        this.errorDetails = errorDetails;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void addClient(String clientId, Double weight, Long dataSamples) {
        if (this.clientIds == null) {
            this.clientIds = new java.util.ArrayList<>();
        }
        if (this.clientWeights == null) {
            this.clientWeights = new java.util.HashMap<>();
        }
        
        if (!this.clientIds.contains(clientId)) {
            this.clientIds.add(clientId);
        }
        
        this.clientWeights.put(clientId, weight);
        
        if (this.totalDataSamples == null) {
            this.totalDataSamples = 0L;
        }
        this.totalDataSamples += dataSamples;
        
        if (this.totalClientsParticipated == null) {
            this.totalClientsParticipated = 0;
        }
        this.totalClientsParticipated++;
    }

    public void markClientSuccessful(String clientId) {
        if (this.clientsSuccessful == null) {
            this.clientsSuccessful = 0;
        }
        this.clientsSuccessful++;
        this.updatedAt = LocalDateTime.now();
    }

    public void markClientFailed(String clientId) {
        if (this.clientsFailed == null) {
            this.clientsFailed = 0;
        }
        this.clientsFailed++;
        this.updatedAt = LocalDateTime.now();
    }

    public void markClientTimedOut(String clientId) {
        if (this.clientsTimedOut == null) {
            this.clientsTimedOut = 0;
        }
        this.clientsTimedOut++;
        this.updatedAt = LocalDateTime.now();
    }

    public void broadcastModel() {
        this.broadcastAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsSynchronized() {
        this.synchronizedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsConsolidated() {
        this.isConsolidated = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsValidated() {
        this.isValidated = true;
        this.verificationStatus = "validated";
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsAudited(String auditNotes) {
        this.isAudited = true;
        this.auditTrail = auditNotes;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsFinalized() {
        this.isFinalized = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateMetric(String metricName, Double value) {
        if (this.metrics == null) {
            this.metrics = new java.util.HashMap<>();
        }
        this.metrics.put(metricName, value);
        this.updatedAt = LocalDateTime.now();
    }

    public void setPrivacyParameters(Double privacyBudgetConsumed, Double epsilon, Double delta, Double noiseLevel) {
        this.privacyBudgetConsumed = privacyBudgetConsumed;
        this.differentialPrivacyEpsilon = epsilon;
        this.differentialPrivacyDelta = delta;
        this.noiseAddedLevel = noiseLevel;
        this.updatedAt = LocalDateTime.now();
    }

    public void setSecurityParameters(String protocol, String keyId, String certificateId) {
        this.secureAggregationProtocol = protocol;
        this.secureAggregationKeyId = keyId;
        this.aggregationCertificateId = certificateId;
        this.secureAggregationApplied = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCompressionParameters(Boolean applied, Double ratio, Double error) {
        this.compressionApplied = applied;
        this.compressionRatio = ratio;
        this.compressionError = error;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementRetryCount() {
        this.retryCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public void calculateQualityScores() {
        // 计算聚合质量评分（简化版）
        double qualityScore = 0.0;
        double communicationScore = 0.0;
        double robustnessScore = 0.0;
        
        if (this.totalClientsParticipated != null && this.totalClientsParticipated > 0) {
            double participationRate = (double) this.clientsSuccessful / this.totalClientsParticipated;
            qualityScore = participationRate * 100;
        }
        
        if (this.modelUpdateSizeBytes != null && this.aggregationTimeSeconds != null) {
            double throughput = this.modelUpdateSizeBytes / Math.max(this.aggregationTimeSeconds, 1.0);
            communicationScore = Math.min(throughput / (1024 * 1024), 100.0); // 归一化到0-100
        }
        
        if (this.clientsFailed != null && this.totalClientsParticipated != null) {
            double failureRate = (double) this.clientsFailed / Math.max(this.totalClientsParticipated, 1);
            robustnessScore = (1.0 - failureRate) * 100;
        }
        
        this.aggregationQualityScore = qualityScore;
        this.communicationEfficiencyScore = communicationScore;
        this.robustnessScore = robustnessScore;
        this.faultToleranceScore = robustnessScore;
        
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("FederatedAggregationRecordEntity{id=%s, taskId=%s, round=%d, method='%s', status='%s', clients=%d/%d}",
                id, taskId, roundNumber, aggregationMethod, status, 
                clientsSuccessful != null ? clientsSuccessful : 0,
                totalClientsParticipated != null ? totalClientsParticipated : 0);
    }
}