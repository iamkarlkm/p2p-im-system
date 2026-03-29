package com.im.federated.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.List;
import java.util.UUID;

/**
 * 联邦学习实体类
 * 用于存储联邦学习训练任务的核心信息
 */
@Entity
@Table(name = "federated_learning_tasks")
public class FederatedLearningEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "task_name", nullable = false, unique = true)
    private String taskName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "model_type", nullable = false)
    private String modelType; // 模型类型: text_classification, sentiment_analysis, spam_detection, etc.

    @Column(name = "algorithm_type", nullable = false)
    private String algorithmType; // 算法类型: fedavg, fedprox, fednova, etc.

    @Column(name = "privacy_protection_level", nullable = false)
    private String privacyProtectionLevel; // 隐私保护等级: none, dp, ldp, sdp

    @Column(name = "epsilon")
    private Double epsilon; // 差分隐私的ε值

    @Column(name = "delta")
    private Double delta; // 差分隐私的δ值

    @Column(name = "clipping_norm")
    private Double clippingNorm; // 梯度裁剪范数

    @Column(name = "num_clients", nullable = false)
    private Integer numClients; // 客户端总数

    @Column(name = "active_clients")
    private Integer activeClients = 0; // 活跃客户端数

    @Column(name = "participation_rate")
    private Double participationRate = 0.5; // 参与率

    @Column(name = "rounds_total", nullable = false)
    private Integer roundsTotal; // 总轮数

    @Column(name = "current_round")
    private Integer currentRound = 0; // 当前轮数

    @Column(name = "batch_size")
    private Integer batchSize = 32; // 批大小

    @Column(name = "learning_rate")
    private Double learningRate = 0.01; // 学习率

    @Column(name = "convergence_threshold")
    private Double convergenceThreshold = 0.001; // 收敛阈值

    @Column(name = "max_epochs_per_round")
    private Integer maxEpochsPerRound = 5; // 每轮最大迭代次数

    @Column(name = "status", nullable = false)
    private String status; // 状态: created, running, paused, completed, failed

    @Column(name = "accuracy")
    private Double accuracy; // 模型准确率

    @Column(name = "loss")
    private Double loss; // 模型损失

    @ElementCollection
    @CollectionTable(name = "federated_learning_metrics", 
                     joinColumns = @JoinColumn(name = "task_id"))
    @MapKeyColumn(name = "metric_name")
    @Column(name = "metric_value")
    private Map<String, Double> metrics; // 训练指标

    @Column(name = "model_path")
    private String modelPath; // 模型保存路径

    @Column(name = "checkpoint_path")
    private String checkpointPath; // 检查点路径

    @ElementCollection
    @CollectionTable(name = "federated_learning_clients", 
                     joinColumns = @JoinColumn(name = "task_id"))
    @Column(name = "client_id")
    private List<String> clientIds; // 参与的客户端ID列表

    @Column(name = "aggregation_method", nullable = false)
    private String aggregationMethod; // 聚合方法: weighted_average, secure_aggregation, etc.

    @Column(name = "secure_aggregation_enabled")
    private Boolean secureAggregationEnabled = false; // 是否启用安全聚合

    @Column(name = "communication_efficient")
    private Boolean communicationEfficient = true; // 是否启用通信效率优化

    @Column(name = "compression_ratio")
    private Double compressionRatio = 0.5; // 梯度压缩比率

    @Column(name = "heterogeneous_data_supported")
    private Boolean heterogeneousDataSupported = true; // 是否支持异构数据

    @Column(name = "personalization_enabled")
    private Boolean personalizationEnabled = false; // 是否启用个性化模型

    @Column(name = "fault_tolerance_enabled")
    private Boolean faultToleranceEnabled = true; // 是否启用容错机制

    @Column(name = "max_failures_allowed")
    private Integer maxFailuresAllowed = 3; // 允许的最大失败次数

    @Column(name = "data_distribution_type")
    private String dataDistributionType; // 数据分布类型: iid, non_iid, extreme_non_iid

    @Column(name = "created_by", nullable = false)
    private String createdBy; // 创建者

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "estimated_completion_time")
    private LocalDateTime estimatedCompletionTime; // 预计完成时间

    @Column(name = "total_training_time_minutes")
    private Long totalTrainingTimeMinutes = 0L; // 总训练时间（分钟）

    @Column(name = "total_communication_size_mb")
    private Long totalCommunicationSizeMB = 0L; // 总通信数据大小（MB）

    @Column(name = "total_model_updates")
    private Long totalModelUpdates = 0L; // 总模型更新次数

    @Column(name = "privacy_budget_consumed")
    private Double privacyBudgetConsumed = 0.0; // 已消耗的隐私预算

    @Column(name = "is_deleted")
    private Boolean isDeleted = false; // 软删除标记

    @Version
    private Long version; // 乐观锁版本

    // 构造函数
    public FederatedLearningEntity() {
        this.createdAt = LocalDateTime.now();
        this.status = "created";
    }

    public FederatedLearningEntity(String taskName, String modelType, String algorithmType, 
                                   Integer numClients, Integer roundsTotal, String createdBy) {
        this();
        this.taskName = taskName;
        this.modelType = modelType;
        this.algorithmType = algorithmType;
        this.numClients = numClients;
        this.roundsTotal = roundsTotal;
        this.createdBy = createdBy;
        this.privacyProtectionLevel = "dp"; // 默认启用差分隐私
        this.epsilon = 1.0;
        this.delta = 1e-5;
        this.clippingNorm = 1.0;
        this.aggregationMethod = "weighted_average";
    }

    // Getter和Setter方法
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getTaskName() { return taskName; }
    public void setTaskName(String taskName) { this.taskName = taskName; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getModelType() { return modelType; }
    public void setModelType(String modelType) { this.modelType = modelType; }

    public String getAlgorithmType() { return algorithmType; }
    public void setAlgorithmType(String algorithmType) { this.algorithmType = algorithmType; }

    public String getPrivacyProtectionLevel() { return privacyProtectionLevel; }
    public void setPrivacyProtectionLevel(String privacyProtectionLevel) { this.privacyProtectionLevel = privacyProtectionLevel; }

    public Double getEpsilon() { return epsilon; }
    public void setEpsilon(Double epsilon) { this.epsilon = epsilon; }

    public Double getDelta() { return delta; }
    public void setDelta(Double delta) { this.delta = delta; }

    public Double getClippingNorm() { return clippingNorm; }
    public void setClippingNorm(Double clippingNorm) { this.clippingNorm = clippingNorm; }

    public Integer getNumClients() { return numClients; }
    public void setNumClients(Integer numClients) { this.numClients = numClients; }

    public Integer getActiveClients() { return activeClients; }
    public void setActiveClients(Integer activeClients) { this.activeClients = activeClients; }

    public Double getParticipationRate() { return participationRate; }
    public void setParticipationRate(Double participationRate) { this.participationRate = participationRate; }

    public Integer getRoundsTotal() { return roundsTotal; }
    public void setRoundsTotal(Integer roundsTotal) { this.roundsTotal = roundsTotal; }

    public Integer getCurrentRound() { return currentRound; }
    public void setCurrentRound(Integer currentRound) { this.currentRound = currentRound; }

    public Integer getBatchSize() { return batchSize; }
    public void setBatchSize(Integer batchSize) { this.batchSize = batchSize; }

    public Double getLearningRate() { return learningRate; }
    public void setLearningRate(Double learningRate) { this.learningRate = learningRate; }

    public Double getConvergenceThreshold() { return convergenceThreshold; }
    public void setConvergenceThreshold(Double convergenceThreshold) { this.convergenceThreshold = convergenceThreshold; }

    public Integer getMaxEpochsPerRound() { return maxEpochsPerRound; }
    public void setMaxEpochsPerRound(Integer maxEpochsPerRound) { this.maxEpochsPerRound = maxEpochsPerRound; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Double getAccuracy() { return accuracy; }
    public void setAccuracy(Double accuracy) { this.accuracy = accuracy; }

    public Double getLoss() { return loss; }
    public void setLoss(Double loss) { this.loss = loss; }

    public Map<String, Double> getMetrics() { return metrics; }
    public void setMetrics(Map<String, Double> metrics) { this.metrics = metrics; }

    public String getModelPath() { return modelPath; }
    public void setModelPath(String modelPath) { this.modelPath = modelPath; }

    public String getCheckpointPath() { return checkpointPath; }
    public void setCheckpointPath(String checkpointPath) { this.checkpointPath = checkpointPath; }

    public List<String> getClientIds() { return clientIds; }
    public void setClientIds(List<String> clientIds) { this.clientIds = clientIds; }

    public String getAggregationMethod() { return aggregationMethod; }
    public void setAggregationMethod(String aggregationMethod) { this.aggregationMethod = aggregationMethod; }

    public Boolean getSecureAggregationEnabled() { return secureAggregationEnabled; }
    public void setSecureAggregationEnabled(Boolean secureAggregationEnabled) { this.secureAggregationEnabled = secureAggregationEnabled; }

    public Boolean getCommunicationEfficient() { return communicationEfficient; }
    public void setCommunicationEfficient(Boolean communicationEfficient) { this.communicationEfficient = communicationEfficient; }

    public Double getCompressionRatio() { return compressionRatio; }
    public void setCompressionRatio(Double compressionRatio) { this.compressionRatio = compressionRatio; }

    public Boolean getHeterogeneousDataSupported() { return heterogeneousDataSupported; }
    public void setHeterogeneousDataSupported(Boolean heterogeneousDataSupported) { this.heterogeneousDataSupported = heterogeneousDataSupported; }

    public Boolean getPersonalizationEnabled() { return personalizationEnabled; }
    public void setPersonalizationEnabled(Boolean personalizationEnabled) { this.personalizationEnabled = personalizationEnabled; }

    public Boolean getFaultToleranceEnabled() { return faultToleranceEnabled; }
    public void setFaultToleranceEnabled(Boolean faultToleranceEnabled) { this.faultToleranceEnabled = faultToleranceEnabled; }

    public Integer getMaxFailuresAllowed() { return maxFailuresAllowed; }
    public void setMaxFailuresAllowed(Integer maxFailuresAllowed) { this.maxFailuresAllowed = maxFailuresAllowed; }

    public String getDataDistributionType() { return dataDistributionType; }
    public void setDataDistributionType(String dataDistributionType) { this.dataDistributionType = dataDistributionType; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public LocalDateTime getEstimatedCompletionTime() { return estimatedCompletionTime; }
    public void setEstimatedCompletionTime(LocalDateTime estimatedCompletionTime) { this.estimatedCompletionTime = estimatedCompletionTime; }

    public Long getTotalTrainingTimeMinutes() { return totalTrainingTimeMinutes; }
    public void setTotalTrainingTimeMinutes(Long totalTrainingTimeMinutes) { this.totalTrainingTimeMinutes = totalTrainingTimeMinutes; }

    public Long getTotalCommunicationSizeMB() { return totalCommunicationSizeMB; }
    public void setTotalCommunicationSizeMB(Long totalCommunicationSizeMB) { this.totalCommunicationSizeMB = totalCommunicationSizeMB; }

    public Long getTotalModelUpdates() { return totalModelUpdates; }
    public void setTotalModelUpdates(Long totalModelUpdates) { this.totalModelUpdates = totalModelUpdates; }

    public Double getPrivacyBudgetConsumed() { return privacyBudgetConsumed; }
    public void setPrivacyBudgetConsumed(Double privacyBudgetConsumed) { this.privacyBudgetConsumed = privacyBudgetConsumed; }

    public Boolean getIsDeleted() { return isDeleted; }
    public void setIsDeleted(Boolean isDeleted) { this.isDeleted = isDeleted; }

    public Long getVersion() { return version; }
    public void setVersion(Long version) { this.version = version; }

    // 辅助方法
    public void incrementCurrentRound() {
        this.currentRound++;
        this.updatedAt = LocalDateTime.now();
    }

    public void addActiveClient(String clientId) {
        if (this.clientIds == null) {
            this.clientIds = new java.util.ArrayList<>();
        }
        if (!this.clientIds.contains(clientId)) {
            this.clientIds.add(clientId);
            this.activeClients++;
        }
    }

    public void removeActiveClient(String clientId) {
        if (this.clientIds != null && this.clientIds.contains(clientId)) {
            this.clientIds.remove(clientId);
            if (this.activeClients > 0) {
                this.activeClients--;
            }
        }
    }

    public void updateMetric(String metricName, Double value) {
        if (this.metrics == null) {
            this.metrics = new java.util.HashMap<>();
        }
        this.metrics.put(metricName, value);
        this.updatedAt = LocalDateTime.now();
    }

    public void startTask() {
        this.status = "running";
        this.startedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void completeTask(Double accuracy, Double loss) {
        this.status = "completed";
        this.accuracy = accuracy;
        this.loss = loss;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void failTask(String reason) {
        this.status = "failed";
        this.updatedAt = LocalDateTime.now();
    }

    public void pauseTask() {
        this.status = "paused";
        this.updatedAt = LocalDateTime.now();
    }

    public void resumeTask() {
        if ("paused".equals(this.status)) {
            this.status = "running";
            this.updatedAt = LocalDateTime.now();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("FederatedLearningEntity{id=%s, taskName='%s', modelType='%s', status='%s', currentRound=%d/%d}",
                id, taskName, modelType, status, currentRound, roundsTotal);
    }
}