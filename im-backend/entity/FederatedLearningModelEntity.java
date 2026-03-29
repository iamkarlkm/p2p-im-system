package com.im.system.entity;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * 联邦学习模型实体
 * 用于存储分布式联邦学习模型的版本、参数和聚合状态
 */
@Entity
@Table(name = "federated_learning_models")
public class FederatedLearningModelEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "model_id", nullable = false, unique = true)
    private String modelId;

    @Column(name = "model_name", nullable = false)
    private String modelName;

    @Column(name = "model_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ModelType modelType;

    @Column(name = "model_version", nullable = false)
    private String modelVersion;

    @Column(name = "parent_model_id")
    private String parentModelId;

    @Column(name = "model_scope", nullable = false)
    @Enumerated(EnumType.STRING)
    private ModelScope modelScope;

    @Column(name = "model_format")
    private String modelFormat;

    @Column(name = "model_size_mb")
    private Double modelSizeMb;

    @Column(name = "parameter_count")
    private Long parameterCount;

    @Column(name = "aggregation_algorithm", nullable = false)
    private String aggregationAlgorithm;

    @Column(name = "privacy_budget")
    private Double privacyBudget;

    @Column(name = "noise_scale")
    private Double noiseScale;

    @Column(name = "clip_norm")
    private Double clipNorm;

    @Column(name = "learning_rate")
    private Double learningRate;

    @Column(name = "batch_size")
    private Integer batchSize;

    @Column(name = "epochs")
    private Integer epochs;

    @Column(name = "participating_clients")
    private Integer participatingClients;

    @Column(name = "minimum_clients")
    private Integer minimumClients = 10;

    @Column(name = "aggregation_round")
    private Integer aggregationRound = 0;

    @Column(name = "model_accuracy")
    private Double modelAccuracy;

    @Column(name = "model_loss")
    private Double modelLoss;

    @Column(name = "convergence_status", nullable = false)
    @Enumerated(EnumType.STRING)
    private ConvergenceStatus convergenceStatus = ConvergenceStatus.TRAINING;

    @Column(name = "global_model_weights_path")
    private String globalModelWeightsPath;

    @Column(name = "metadata_json")
    @Lob
    private String metadataJson;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "is_encrypted", nullable = false)
    private Boolean isEncrypted = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "last_aggregation_time")
    private LocalDateTime lastAggregationTime;

    @Column(name = "next_aggregation_time")
    private LocalDateTime nextAggregationTime;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "description")
    private String description;

    @Column(name = "tags")
    private String tags;

    // 构造函数
    public FederatedLearningModelEntity() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public FederatedLearningModelEntity(String modelId, String modelName, ModelType modelType,
                                       ModelScope modelScope, String aggregationAlgorithm) {
        this();
        this.modelId = modelId;
        this.modelName = modelName;
        this.modelType = modelType;
        this.modelScope = modelScope;
        this.aggregationAlgorithm = aggregationAlgorithm;
        this.modelVersion = "v1.0.0";
        this.convergenceStatus = ConvergenceStatus.TRAINING;
    }

    // 枚举类型定义
    public enum ModelType {
        RECOMMENDATION,
        CLASSIFICATION,
        CLUSTERING,
        REGRESSION,
        NEURAL_NETWORK,
        DEEP_LEARNING,
        TRANSFORMER,
        COLLABORATIVE_FILTERING
    }

    public enum ModelScope {
        GLOBAL,
        REGIONAL,
        ORGANIZATION,
        GROUP,
        PERSONALIZED
    }

    public enum ConvergenceStatus {
        TRAINING,
        CONVERGING,
        CONVERGED,
        DIVERGING,
        PAUSED,
        FAILED
    }

    // Getter 和 Setter 方法
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public ModelType getModelType() {
        return modelType;
    }

    public void setModelType(ModelType modelType) {
        this.modelType = modelType;
    }

    public String getModelVersion() {
        return modelVersion;
    }

    public void setModelVersion(String modelVersion) {
        this.modelVersion = modelVersion;
    }

    public String getParentModelId() {
        return parentModelId;
    }

    public void setParentModelId(String parentModelId) {
        this.parentModelId = parentModelId;
    }

    public ModelScope getModelScope() {
        return modelScope;
    }

    public void setModelScope(ModelScope modelScope) {
        this.modelScope = modelScope;
    }

    public String getModelFormat() {
        return modelFormat;
    }

    public void setModelFormat(String modelFormat) {
        this.modelFormat = modelFormat;
    }

    public Double getModelSizeMb() {
        return modelSizeMb;
    }

    public void setModelSizeMb(Double modelSizeMb) {
        this.modelSizeMb = modelSizeMb;
    }

    public Long getParameterCount() {
        return parameterCount;
    }

    public void setParameterCount(Long parameterCount) {
        this.parameterCount = parameterCount;
    }

    public String getAggregationAlgorithm() {
        return aggregationAlgorithm;
    }

    public void setAggregationAlgorithm(String aggregationAlgorithm) {
        this.aggregationAlgorithm = aggregationAlgorithm;
    }

    public Double getPrivacyBudget() {
        return privacyBudget;
    }

    public void setPrivacyBudget(Double privacyBudget) {
        this.privacyBudget = privacyBudget;
    }

    public Double getNoiseScale() {
        return noiseScale;
    }

    public void setNoiseScale(Double noiseScale) {
        this.noiseScale = noiseScale;
    }

    public Double getClipNorm() {
        return clipNorm;
    }

    public void setClipNorm(Double clipNorm) {
        this.clipNorm = clipNorm;
    }

    public Double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(Double learningRate) {
        this.learningRate = learningRate;
    }

    public Integer getBatchSize() {
        return batchSize;
    }

    public void setBatchSize(Integer batchSize) {
        this.batchSize = batchSize;
    }

    public Integer getEpochs() {
        return epochs;
    }

    public void setEpochs(Integer epochs) {
        this.epochs = epochs;
    }

    public Integer getParticipatingClients() {
        return participatingClients;
    }

    public void setParticipatingClients(Integer participatingClients) {
        this.participatingClients = participatingClients;
    }

    public Integer getMinimumClients() {
        return minimumClients;
    }

    public void setMinimumClients(Integer minimumClients) {
        this.minimumClients = minimumClients;
    }

    public Integer getAggregationRound() {
        return aggregationRound;
    }

    public void setAggregationRound(Integer aggregationRound) {
        this.aggregationRound = aggregationRound;
    }

    public Double getModelAccuracy() {
        return modelAccuracy;
    }

    public void setModelAccuracy(Double modelAccuracy) {
        this.modelAccuracy = modelAccuracy;
    }

    public Double getModelLoss() {
        return modelLoss;
    }

    public void setModelLoss(Double modelLoss) {
        this.modelLoss = modelLoss;
    }

    public ConvergenceStatus getConvergenceStatus() {
        return convergenceStatus;
    }

    public void setConvergenceStatus(ConvergenceStatus convergenceStatus) {
        this.convergenceStatus = convergenceStatus;
    }

    public String getGlobalModelWeightsPath() {
        return globalModelWeightsPath;
    }

    public void setGlobalModelWeightsPath(String globalModelWeightsPath) {
        this.globalModelWeightsPath = globalModelWeightsPath;
    }

    public String getMetadataJson() {
        return metadataJson;
    }

    public void setMetadataJson(String metadataJson) {
        this.metadataJson = metadataJson;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    public Boolean getIsEncrypted() {
        return isEncrypted;
    }

    public void setIsEncrypted(Boolean isEncrypted) {
        this.isEncrypted = isEncrypted;
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

    public LocalDateTime getLastAggregationTime() {
        return lastAggregationTime;
    }

    public void setLastAggregationTime(LocalDateTime lastAggregationTime) {
        this.lastAggregationTime = lastAggregationTime;
    }

    public LocalDateTime getNextAggregationTime() {
        return nextAggregationTime;
    }

    public void setNextAggregationTime(LocalDateTime nextAggregationTime) {
        this.nextAggregationTime = nextAggregationTime;
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    // 业务方法
    public void incrementAggregationRound() {
        this.aggregationRound++;
        this.lastAggregationTime = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void updateModelMetrics(Double accuracy, Double loss) {
        this.modelAccuracy = accuracy;
        this.modelLoss = loss;
        this.updatedAt = LocalDateTime.now();
        
        // 自动判断收敛状态
        if (loss != null && loss < 0.01) {
            this.convergenceStatus = ConvergenceStatus.CONVERGED;
        } else if (loss != null && loss > 1.0) {
            this.convergenceStatus = ConvergenceStatus.DIVERGING;
        }
    }

    public void pauseTraining() {
        this.convergenceStatus = ConvergenceStatus.PAUSED;
        this.updatedAt = LocalDateTime.now();
    }

    public void resumeTraining() {
        if (this.convergenceStatus == ConvergenceStatus.PAUSED) {
            this.convergenceStatus = ConvergenceStatus.TRAINING;
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean canAggregate() {
        return this.participatingClients != null &&
               this.participatingClients >= this.minimumClients &&
               this.convergenceStatus != ConvergenceStatus.CONVERGED &&
               this.convergenceStatus != ConvergenceStatus.FAILED;
    }

    public String getNextVersion() {
        if (this.modelVersion == null) {
            return "v1.0.0";
        }
        
        // 简单版本号递增逻辑
        String[] parts = this.modelVersion.replace("v", "").split("\\.");
        if (parts.length >= 3) {
            int patch = Integer.parseInt(parts[2]) + 1;
            return String.format("v%s.%s.%d", parts[0], parts[1], patch);
        }
        return this.modelVersion + ".1";
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return "FederatedLearningModelEntity{" +
                "id=" + id +
                ", modelId='" + modelId + '\'' +
                ", modelName='" + modelName + '\'' +
                ", modelType=" + modelType +
                ", modelVersion='" + modelVersion + '\'' +
                ", aggregationRound=" + aggregationRound +
                ", convergenceStatus=" + convergenceStatus +
                ", modelAccuracy=" + modelAccuracy +
                '}';
    }
}