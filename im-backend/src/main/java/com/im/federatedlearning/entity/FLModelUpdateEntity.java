package com.im.federatedlearning.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.HashMap;

/**
 * 联邦学习模型更新实体
 * 记录客户端上传的模型更新信息，包括训练统计和隐私保护参数
 * 
 * @version 1.0
 * @created 2026-03-23
 */
@Entity
@Table(name = "fl_model_updates",
       indexes = {
           @Index(name = "idx_fl_update_model_round", columnList = "modelId, trainingRound"),
           @Index(name = "idx_fl_update_client_status", columnList = "clientId, status"),
           @Index(name = "idx_fl_update_server_timestamp", columnList = "serverId, createdAt"),
           @Index(name = "idx_fl_update_privacy_level", columnList = "privacyLevel")
       })
public class FLModelUpdateEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String updateId;

    @Column(nullable = false)
    private String modelId;

    @Column(nullable = false)
    private String serverId;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private Integer trainingRound;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UpdateStatus status = UpdateStatus.PENDING;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UpdateType updateType = UpdateType.GRADIENT;

    @Lob
    @Column(nullable = false, columnDefinition = "TEXT")
    private String encryptedUpdateData;

    @Column(nullable = false)
    private String encryptionAlgorithm = "AES-256-GCM";

    @Column(nullable = false)
    private String encryptionKeyId;

    @Column(nullable = false)
    private Integer dataSizeBytes;

    @Column(nullable = false)
    private Integer localTrainingSamples;

    @Column(nullable = false)
    private Double trainingLoss;

    @Column(nullable = false)
    private Double trainingAccuracy;

    @Column(nullable = false)
    private Integer trainingEpochs;

    @Column(nullable = false)
    private Integer trainingBatchSize;

    @Column(nullable = false)
    private Double learningRate;

    @Column(nullable = false)
    private Long trainingDurationMs;

    @Column(nullable = false)
    private Double deviceCpuUsage;

    @Column(nullable = false)
    private Double deviceMemoryUsage;

    @Column(nullable = false)
    private Integer deviceBatteryLevel;

    @Column(nullable = false)
    private Boolean deviceWasCharging;

    @Column(nullable = false)
    private String networkType;

    @Column(nullable = false)
    private Double networkBandwidthMbps;

    @Column(nullable = false)
    private Double networkLatencyMs;

    @Column(nullable = false)
    private Boolean enableDifferentialPrivacy;

    @Column(nullable = false)
    private Double privacyEpsilonUsed;

    @Column(nullable = false)
    private Double privacyDeltaUsed;

    @Column(nullable = false)
    private String dpNoiseType = "Gaussian";

    @Column(nullable = false)
    private Double dpNoiseScale;

    @Column(nullable = false)
    private Double dpClipNorm;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrivacyLevel privacyLevel = PrivacyLevel.STANDARD;

    @Column(nullable = false)
    private Boolean enableSecureAggregation;

    @Column(nullable = false)
    private String aggregationGroupId;

    @Column(nullable = false)
    private Integer aggregationGroupSize;

    @Column(nullable = false)
    private Integer aggregationClientIndex;

    @Column(nullable = false)
    private Boolean enableModelCompression;

    @Column(nullable = false)
    private Integer compressionRatio;

    @Column(nullable = false)
    private String compressionAlgorithm = "Pruning";

    @Column(nullable = false)
    private Double compressionQualityScore;

    @Column(nullable = false)
    private Boolean enableQuantization;

    @Column(nullable = false)
    private Integer quantizationBits;

    @ElementCollection
    @CollectionTable(name = "fl_update_metrics", 
                     joinColumns = @JoinColumn(name = "updateId"))
    @MapKeyColumn(name = "metric_name")
    @Column(name = "metric_value")
    private Map<String, Double> trainingMetrics = new HashMap<>();

    @ElementCollection
    @CollectionTable(name = "fl_update_labels", 
                     joinColumns = @JoinColumn(name = "updateId"))
    @Column(name = "label")
    private Map<String, Integer> labelDistribution = new HashMap<>();

    @Column(nullable = false)
    private String modelHash;

    @Column(nullable = false)
    private String updateSignature;

    @Column(nullable = false)
    private String clientPublicKey;

    @Column(nullable = false)
    private String verificationStatus = "PENDING";

    @Column
    private String verificationNotes;

    @Column(nullable = false)
    private Double qualityScore;

    @Column(nullable = false)
    private Double contributionScore;

    @Column(nullable = false)
    private Boolean isAnomalous = false;

    @Column
    private String anomalyReason;

    @Column(nullable = false)
    private Double anomalyScore;

    @Column(nullable = false)
    private Boolean includedInAggregation = false;

    @Column(nullable = false)
    private Integer aggregationRound;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime verifiedAt;

    @Column(nullable = false)
    private LocalDateTime aggregatedAt;

    @Column(nullable = false)
    private String clientVersion;

    @Column(nullable = false)
    private String clientPlatform;

    @Column(nullable = false)
    private String clientOsVersion;

    @Column(nullable = false)
    private String clientDeviceModel;

    @Column
    private String clientIpAddress;

    @Column
    private String clientLocation;

    // 枚举类型定义
    public enum UpdateStatus {
        PENDING,                // 待处理
        RECEIVED,               // 已接收
        VERIFYING,              // 验证中
        VERIFIED,               // 已验证
        REJECTED,               // 已拒绝
        AGGREGATING,            // 聚合中
        AGGREGATED,             // 已聚合
        EXPIRED,                // 已过期
        ERROR                   // 错误
    }

    public enum UpdateType {
        GRADIENT,               // 梯度更新
        WEIGHT,                 // 权重更新
        MOMENTUM,               // 动量更新
        ADAM,                   // Adam更新
        SPARSE,                 // 稀疏更新
        DIFFERENTIAL            // 差分更新
    }

    public enum PrivacyLevel {
        MINIMAL,                // 最小隐私保护
        BASIC,                  // 基本隐私保护
        STANDARD,               // 标准隐私保护
        ENHANCED,               // 增强隐私保护
        MAXIMUM                 // 最大隐私保护
    }

    // 构造方法
    public FLModelUpdateEntity() {
    }

    public FLModelUpdateEntity(String modelId, String serverId, String clientId, Integer trainingRound) {
        this.modelId = modelId;
        this.serverId = serverId;
        this.clientId = clientId;
        this.trainingRound = trainingRound;
        this.status = UpdateStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters 和 Setters
    public String getUpdateId() {
        return updateId;
    }

    public void setUpdateId(String updateId) {
        this.updateId = updateId;
    }

    public String getModelId() {
        return modelId;
    }

    public void setModelId(String modelId) {
        this.modelId = modelId;
    }

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Integer getTrainingRound() {
        return trainingRound;
    }

    public void setTrainingRound(Integer trainingRound) {
        this.trainingRound = trainingRound;
    }

    public UpdateStatus getStatus() {
        return status;
    }

    public void setStatus(UpdateStatus status) {
        this.status = status;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    public void setUpdateType(UpdateType updateType) {
        this.updateType = updateType;
    }

    public String getEncryptedUpdateData() {
        return encryptedUpdateData;
    }

    public void setEncryptedUpdateData(String encryptedUpdateData) {
        this.encryptedUpdateData = encryptedUpdateData;
    }

    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    public void setEncryptionAlgorithm(String encryptionAlgorithm) {
        this.encryptionAlgorithm = encryptionAlgorithm;
    }

    public String getEncryptionKeyId() {
        return encryptionKeyId;
    }

    public void setEncryptionKeyId(String encryptionKeyId) {
        this.encryptionKeyId = encryptionKeyId;
    }

    public Integer getDataSizeBytes() {
        return dataSizeBytes;
    }

    public void setDataSizeBytes(Integer dataSizeBytes) {
        this.dataSizeBytes = dataSizeBytes;
    }

    public Integer getLocalTrainingSamples() {
        return localTrainingSamples;
    }

    public void setLocalTrainingSamples(Integer localTrainingSamples) {
        this.localTrainingSamples = localTrainingSamples;
    }

    public Double getTrainingLoss() {
        return trainingLoss;
    }

    public void setTrainingLoss(Double trainingLoss) {
        this.trainingLoss = trainingLoss;
    }

    public Double getTrainingAccuracy() {
        return trainingAccuracy;
    }

    public void setTrainingAccuracy(Double trainingAccuracy) {
        this.trainingAccuracy = trainingAccuracy;
    }

    public Integer getTrainingEpochs() {
        return trainingEpochs;
    }

    public void setTrainingEpochs(Integer trainingEpochs) {
        this.trainingEpochs = trainingEpochs;
    }

    public Integer getTrainingBatchSize() {
        return trainingBatchSize;
    }

    public void setTrainingBatchSize(Integer trainingBatchSize) {
        this.trainingBatchSize = trainingBatchSize;
    }

    public Double getLearningRate() {
        return learningRate;
    }

    public void setLearningRate(Double learningRate) {
        this.learningRate = learningRate;
    }

    public Long getTrainingDurationMs() {
        return trainingDurationMs;
    }

    public void setTrainingDurationMs(Long trainingDurationMs) {
        this.trainingDurationMs = trainingDurationMs;
    }

    public Double getDeviceCpuUsage() {
        return deviceCpuUsage;
    }

    public void setDeviceCpuUsage(Double deviceCpuUsage) {
        this.deviceCpuUsage = deviceCpuUsage;
    }

    public Double getDeviceMemoryUsage() {
        return deviceMemoryUsage;
    }

    public void setDeviceMemoryUsage(Double deviceMemoryUsage) {
        this.deviceMemoryUsage = deviceMemoryUsage;
    }

    public Integer getDeviceBatteryLevel() {
        return deviceBatteryLevel;
    }

    public void setDeviceBatteryLevel(Integer deviceBatteryLevel) {
        this.deviceBatteryLevel = deviceBatteryLevel;
    }

    public Boolean getDeviceWasCharging() {
        return deviceWasCharging;
    }

    public void setDeviceWasCharging(Boolean deviceWasCharging) {
        this.deviceWasCharging = deviceWasCharging;
    }

    public String getNetworkType() {
        return networkType;
    }

    public void setNetworkType(String networkType) {
        this.networkType = networkType;
    }

    public Double getNetworkBandwidthMbps() {
        return networkBandwidthMbps;
    }

    public void setNetworkBandwidthMbps(Double networkBandwidthMbps) {
        this.networkBandwidthMbps = networkBandwidthMbps;
    }

    public Double getNetworkLatencyMs() {
        return networkLatencyMs;
    }

    public void setNetworkLatencyMs(Double networkLatencyMs) {
        this.networkLatencyMs = networkLatencyMs;
    }

    public Boolean getEnableDifferentialPrivacy() {
        return enableDifferentialPrivacy;
    }

    public void setEnableDifferentialPrivacy(Boolean enableDifferentialPrivacy) {
        this.enableDifferentialPrivacy = enableDifferentialPrivacy;
    }

    public Double getPrivacyEpsilonUsed() {
        return privacyEpsilonUsed;
    }

    public void setPrivacyEpsilonUsed(Double privacyEpsilonUsed) {
        this.privacyEpsilonUsed = privacyEpsilonUsed;
    }

    public Double getPrivacyDeltaUsed() {
        return privacyDeltaUsed;
    }

    public void setPrivacyDeltaUsed(Double privacyDeltaUsed) {
        this.privacyDeltaUsed = privacyDeltaUsed;
    }

    public String getDpNoiseType() {
        return dpNoiseType;
    }

    public void setDpNoiseType(String dpNoiseType) {
        this.dpNoiseType = dpNoiseType;
    }

    public Double getDpNoiseScale() {
        return dpNoiseScale;
    }

    public void setDpNoiseScale(Double dpNoiseScale) {
        this.dpNoiseScale = dpNoiseScale;
    }

    public Double getDpClipNorm() {
        return dpClipNorm;
    }

    public void setDpClipNorm(Double dpClipNorm) {
        this.dpClipNorm = dpClipNorm;
    }

    public PrivacyLevel getPrivacyLevel() {
        return privacyLevel;
    }

    public void setPrivacyLevel(PrivacyLevel privacyLevel) {
        this.privacyLevel = privacyLevel;
    }

    public Boolean getEnableSecureAggregation() {
        return enableSecureAggregation;
    }

    public void setEnableSecureAggregation(Boolean enableSecureAggregation) {
        this.enableSecureAggregation = enableSecureAggregation;
    }

    public String getAggregationGroupId() {
        return aggregationGroupId;
    }

    public void setAggregationGroupId(String aggregationGroupId) {
        this.aggregationGroupId = aggregationGroupId;
    }

    public Integer getAggregationGroupSize() {
        return aggregationGroupSize;
    }

    public void setAggregationGroupSize(Integer aggregationGroupSize) {
        this.aggregationGroupSize = aggregationGroupSize;
    }

    public Integer getAggregationClientIndex() {
        return aggregationClientIndex;
    }

    public void setAggregationClientIndex(Integer aggregationClientIndex) {
        this.aggregationClientIndex = aggregationClientIndex;
    }

    public Boolean getEnableModelCompression() {
        return enableModelCompression;
    }

    public void setEnableModelCompression(Boolean enableModelCompression) {
        this.enableModelCompression = enableModelCompression;
    }

    public Integer getCompressionRatio() {
        return compressionRatio;
    }

    public void setCompressionRatio(Integer compressionRatio) {
        this.compressionRatio = compressionRatio;
    }

    public String getCompressionAlgorithm() {
        return compressionAlgorithm;
    }

    public void setCompressionAlgorithm(String compressionAlgorithm) {
        this.compressionAlgorithm = compressionAlgorithm;
    }

    public Double getCompressionQualityScore() {
        return compressionQualityScore;
    }

    public void setCompressionQualityScore(Double compressionQualityScore) {
        this.compressionQualityScore = compressionQualityScore;
    }

    public Boolean getEnableQuantization() {
        return enableQuantization;
    }

    public void setEnableQuantization(Boolean enableQuantization) {
        this.enableQuantization = enableQuantization;
    }

    public Integer getQuantizationBits() {
        return quantizationBits;
    }

    public void setQuantizationBits(Integer quantizationBits) {
        this.quantizationBits = quantizationBits;
    }

    public Map<String, Double> getTrainingMetrics() {
        return trainingMetrics;
    }

    public void setTrainingMetrics(Map<String, Double> trainingMetrics) {
        this.trainingMetrics = trainingMetrics;
    }

    public Map<String, Integer> getLabelDistribution() {
        return labelDistribution;
    }

    public void setLabelDistribution(Map<String, Integer> labelDistribution) {
        this.labelDistribution = labelDistribution;
    }

    public String getModelHash() {
        return modelHash;
    }

    public void setModelHash(String modelHash) {
        this.modelHash = modelHash;
    }

    public String getUpdateSignature() {
        return updateSignature;
    }

    public void setUpdateSignature(String updateSignature) {
        this.updateSignature = updateSignature;
    }

    public String getClientPublicKey() {
        return clientPublicKey;
    }

    public void setClientPublicKey(String clientPublicKey) {
        this.clientPublicKey = clientPublicKey;
    }

    public String getVerificationStatus() {
        return verificationStatus;
    }

    public void setVerificationStatus(String verificationStatus) {
        this.verificationStatus = verificationStatus;
    }

    public String getVerificationNotes() {
        return verificationNotes;
    }

    public void setVerificationNotes(String verificationNotes) {
        this.verificationNotes = verificationNotes;
    }

    public Double getQualityScore() {
        return qualityScore;
    }

    public void setQualityScore(Double qualityScore) {
        this.qualityScore = qualityScore;
    }

    public Double getContributionScore() {
        return contributionScore;
    }

    public void setContributionScore(Double contributionScore) {
        this.contributionScore = contributionScore;
    }

    public Boolean getIsAnomalous() {
        return isAnomalous;
    }

    public void setIsAnomalous(Boolean isAnomalous) {
        this.isAnomalous = isAnomalous;
    }

    public String getAnomalyReason() {
        return anomalyReason;
    }

    public void setAnomalyReason(String anomalyReason) {
        this.anomalyReason = anomalyReason;
    }

    public Double getAnomalyScore() {
        return anomalyScore;
    }

    public void setAnomalyScore(Double anomalyScore) {
        this.anomalyScore = anomalyScore;
    }

    public Boolean getIncludedInAggregation() {
        return includedInAggregation;
    }

    public void setIncludedInAggregation(Boolean includedInAggregation) {
        this.includedInAggregation = includedInAggregation;
    }

    public Integer getAggregationRound() {
        return aggregationRound;
    }

    public void setAggregationRound(Integer aggregationRound) {
        this.aggregationRound = aggregationRound;
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

    public LocalDateTime getVerifiedAt() {
        return verifiedAt;
    }

    public void setVerifiedAt(LocalDateTime verifiedAt) {
        this.verifiedAt = verifiedAt;
    }

    public LocalDateTime getAggregatedAt() {
        return aggregatedAt;
    }

    public void setAggregatedAt(LocalDateTime aggregatedAt) {
        this.aggregatedAt = aggregatedAt;
    }

    public String getClientVersion() {
        return clientVersion;
    }

    public void setClientVersion(String clientVersion) {
        this.clientVersion = clientVersion;
    }

    public String getClientPlatform() {
        return clientPlatform;
    }

    public void setClientPlatform(String clientPlatform) {
        this.clientPlatform = clientPlatform;
    }

    public String getClientOsVersion() {
        return clientOsVersion;
    }

    public void setClientOsVersion(String clientOsVersion) {
        this.clientOsVersion = clientOsVersion;
    }

    public String getClientDeviceModel() {
        return clientDeviceModel;
    }

    public void setClientDeviceModel(String clientDeviceModel) {
        this.clientDeviceModel = clientDeviceModel;
    }

    public String getClientIpAddress() {
        return clientIpAddress;
    }

    public void setClientIpAddress(String clientIpAddress) {
        this.clientIpAddress = clientIpAddress;
    }

    public String getClientLocation() {
        return clientLocation;
    }

    public void setClientLocation(String clientLocation) {
        this.clientLocation = clientLocation;
    }

    // 业务方法
    public void addTrainingMetric(String metricName, Double value) {
        this.trainingMetrics.put(metricName, value);
    }

    public void addLabelCount(String label, Integer count) {
        this.labelDistribution.put(label, count);
    }

    public void updateStatus(UpdateStatus newStatus) {
        this.status = newStatus;
        this.updatedAt = LocalDateTime.now();
        
        if (newStatus == UpdateStatus.VERIFIED) {
            this.verifiedAt = LocalDateTime.now();
        } else if (newStatus == UpdateStatus.AGGREGATED) {
            this.aggregatedAt = LocalDateTime.now();
        }
    }

    public boolean isReadyForAggregation() {
        return status == UpdateStatus.VERIFIED && 
               !isAnomalous && 
               qualityScore != null && 
               qualityScore >= 0.7;
    }

    public double calculateContributionScore() {
        double sampleWeight = Math.min(localTrainingSamples / 1000.0, 1.0);
        double accuracyWeight = trainingAccuracy != null ? trainingAccuracy : 0.5;
        double privacyWeight = enableDifferentialPrivacy ? 1.0 : 0.5;
        
        return 0.4 * sampleWeight + 0.3 * accuracyWeight + 0.3 * privacyWeight;
    }

    public boolean hasPrivacyViolation() {
        if (!enableDifferentialPrivacy) {
            return privacyEpsilonUsed != null && privacyEpsilonUsed > 10.0;
        }
        return false;
    }

    public void calculateQualityScore() {
        double accuracyScore = trainingAccuracy != null ? trainingAccuracy : 0.0;
        double sampleScore = Math.min(localTrainingSamples / 500.0, 1.0);
        double privacyScore = enableDifferentialPrivacy ? 1.0 : 0.7;
        double deviceScore = deviceWasCharging && deviceBatteryLevel >= 50 ? 1.0 : 0.6;
        
        this.qualityScore = 0.3 * accuracyScore + 0.2 * sampleScore + 0.3 * privacyScore + 0.2 * deviceScore;
    }

    public boolean isExpired() {
        LocalDateTime expiryTime = createdAt.plusHours(24);
        return LocalDateTime.now().isAfter(expiryTime);
    }

    public String getSummary() {
        return String.format("Update[%s] for Model[%s] from Client[%s] - Accuracy: %.2f, Samples: %d, Privacy: %s",
                updateId.substring(0, 8), modelId.substring(0, 8), clientId.substring(0, 8),
                trainingAccuracy, localTrainingSamples, privacyLevel);
    }

    @Override
    public String toString() {
        return "FLModelUpdateEntity{" +
                "updateId='" + updateId + '\'' +
                ", modelId='" + modelId + '\'' +
                ", serverId='" + serverId + '\'' +
                ", clientId='" + clientId + '\'' +
                ", trainingRound=" + trainingRound +
                ", status=" + status +
                ", updateType=" + updateType +
                ", localTrainingSamples=" + localTrainingSamples +
                ", trainingAccuracy=" + trainingAccuracy +
                ", privacyLevel=" + privacyLevel +
                ", qualityScore=" + qualityScore +
                ", isAnomalous=" + isAnomalous +
                '}';
    }
}