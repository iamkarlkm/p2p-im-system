package com.im.federatedlearning.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.ArrayList;

/**
 * 联邦学习服务器实体
 * 负责管理联邦学习服务器的配置、状态和调度信息
 * 
 * @version 1.0
 * @created 2026-03-23
 */
@Entity
@Table(name = "federated_learning_servers", 
       indexes = {
           @Index(name = "idx_fl_server_status", columnList = "status"),
           @Index(name = "idx_fl_server_region", columnList = "region"),
           @Index(name = "idx_fl_server_last_heartbeat", columnList = "lastHeartbeatTime")
       })
public class FederatedLearningServerEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String serverId;

    @Column(nullable = false, unique = true)
    private String serverName;

    @Column(nullable = false)
    private String serverUrl;

    @Column(nullable = false)
    private String region;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServerStatus status = ServerStatus.INACTIVE;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServerType serverType = ServerType.CENTRAL;

    @Column(nullable = false)
    private String version = "1.0.0";

    @Column(nullable = false)
    private String aggregationAlgorithm = "FedAvg";

    @Column(nullable = false)
    private Integer maxConcurrentClients = 100;

    @Column(nullable = false)
    private Integer maxClientsPerRound = 10;

    @Column(nullable = false)
    private Integer minClientsPerRound = 3;

    @Column(nullable = false)
    private Integer trainingRoundDurationMinutes = 30;

    @Column(nullable = false)
    private Double targetAccuracy = 0.95;

    @Column(nullable = false)
    private Integer maxTrainingRounds = 100;

    @Column(nullable = false)
    private Boolean enableDifferentialPrivacy = true;

    @Column(nullable = false)
    private Double privacyEpsilon = 1.0;

    @Column(nullable = false)
    private Double privacyDelta = 0.00001;

    @Column(nullable = false)
    private Boolean enableSecureAggregation = true;

    @Column(nullable = false)
    private String secureAggregationProtocol = "SECOA";

    @Column(nullable = false)
    private Integer minClientsForSecureAggregation = 3;

    @Column(nullable = false)
    private Boolean enableModelCompression = true;

    @Column(nullable = false)
    private Integer modelCompressionRatio = 50; // 百分比

    @Column(nullable = false)
    private Boolean enableEnergyAwareScheduling = true;

    @Column(nullable = false)
    private Boolean requireChargingForTraining = true;

    @Column(nullable = false)
    private Boolean requireWifiForTraining = true;

    @Column(nullable = false)
    private Integer minimumBatteryLevel = 50; // 百分比

    @ElementCollection
    @CollectionTable(name = "fl_server_supported_models", 
                     joinColumns = @JoinColumn(name = "serverId"))
    @Column(name = "model_type")
    private List<String> supportedModels = new ArrayList<>();

    @ElementCollection
    @CollectionTable(name = "fl_server_supported_languages", 
                     joinColumns = @JoinColumn(name = "serverId"))
    @Column(name = "language_code")
    private List<String> supportedLanguages = new ArrayList<>();

    @Column(nullable = false)
    private Integer activeModelCount = 0;

    @Column(nullable = false)
    private Integer activeClientCount = 0;

    @Column(nullable = false)
    private Integer completedTrainingRounds = 0;

    @Column(nullable = false)
    private Double averageTrainingAccuracy = 0.0;

    @Column(nullable = false)
    private Double averageTrainingLoss = 0.0;

    @Column(nullable = false)
    private Double averageRoundDurationMinutes = 0.0;

    @Column(nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime lastHeartbeatTime = LocalDateTime.now();

    @Column(nullable = false)
    private LocalDateTime lastTrainingRoundTime;

    @Column(nullable = false)
    private Boolean autoScalingEnabled = true;

    @Column(nullable = false)
    private Integer maxAutoScaleInstances = 5;

    @Column(nullable = false)
    private Double cpuUsageThreshold = 80.0; // 百分比

    @Column(nullable = false)
    private Double memoryUsageThreshold = 80.0; // 百分比

    @Column(nullable = false)
    private Double networkBandwidthMbps = 1000.0;

    @Column(nullable = false)
    private String healthCheckEndpoint = "/health";

    @Column(nullable = false)
    private Integer healthCheckIntervalSeconds = 60;

    @Column(nullable = false)
    private Integer healthCheckTimeoutSeconds = 10;

    @Column
    private String description;

    // 枚举类型定义
    public enum ServerStatus {
        ACTIVE,                 // 活跃状态
        INACTIVE,               // 非活跃状态  
        MAINTENANCE,            // 维护中
        DEGRADED,               // 性能降级
        ERROR,                  // 错误状态
        SCALING                 // 自动扩展中
    }

    public enum ServerType {
        CENTRAL,                // 中央服务器
        REGIONAL,               // 区域服务器
        EDGE,                   // 边缘服务器
        HYBRID,                 // 混合服务器
        MOBILE                  // 移动服务器
    }

    // 构造方法
    public FederatedLearningServerEntity() {
    }

    public FederatedLearningServerEntity(String serverName, String serverUrl, String region) {
        this.serverName = serverName;
        this.serverUrl = serverUrl;
        this.region = region;
        this.status = ServerStatus.INACTIVE;
        this.createdAt = LocalDateTime.now();
        this.lastHeartbeatTime = LocalDateTime.now();
    }

    // Getters 和 Setters
    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public String getServerName() {
        return serverName;
    }

    public void setServerName(String serverName) {
        this.serverName = serverName;
    }

    public String getServerUrl() {
        return serverUrl;
    }

    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }

    public String getRegion() {
        return region;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public ServerStatus getStatus() {
        return status;
    }

    public void setStatus(ServerStatus status) {
        this.status = status;
    }

    public ServerType getServerType() {
        return serverType;
    }

    public void setServerType(ServerType serverType) {
        this.serverType = serverType;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getAggregationAlgorithm() {
        return aggregationAlgorithm;
    }

    public void setAggregationAlgorithm(String aggregationAlgorithm) {
        this.aggregationAlgorithm = aggregationAlgorithm;
    }

    public Integer getMaxConcurrentClients() {
        return maxConcurrentClients;
    }

    public void setMaxConcurrentClients(Integer maxConcurrentClients) {
        this.maxConcurrentClients = maxConcurrentClients;
    }

    public Integer getMaxClientsPerRound() {
        return maxClientsPerRound;
    }

    public void setMaxClientsPerRound(Integer maxClientsPerRound) {
        this.maxClientsPerRound = maxClientsPerRound;
    }

    public Integer getMinClientsPerRound() {
        return minClientsPerRound;
    }

    public void setMinClientsPerRound(Integer minClientsPerRound) {
        this.minClientsPerRound = minClientsPerRound;
    }

    public Integer getTrainingRoundDurationMinutes() {
        return trainingRoundDurationMinutes;
    }

    public void setTrainingRoundDurationMinutes(Integer trainingRoundDurationMinutes) {
        this.trainingRoundDurationMinutes = trainingRoundDurationMinutes;
    }

    public Double getTargetAccuracy() {
        return targetAccuracy;
    }

    public void setTargetAccuracy(Double targetAccuracy) {
        this.targetAccuracy = targetAccuracy;
    }

    public Integer getMaxTrainingRounds() {
        return maxTrainingRounds;
    }

    public void setMaxTrainingRounds(Integer maxTrainingRounds) {
        this.maxTrainingRounds = maxTrainingRounds;
    }

    public Boolean getEnableDifferentialPrivacy() {
        return enableDifferentialPrivacy;
    }

    public void setEnableDifferentialPrivacy(Boolean enableDifferentialPrivacy) {
        this.enableDifferentialPrivacy = enableDifferentialPrivacy;
    }

    public Double getPrivacyEpsilon() {
        return privacyEpsilon;
    }

    public void setPrivacyEpsilon(Double privacyEpsilon) {
        this.privacyEpsilon = privacyEpsilon;
    }

    public Double getPrivacyDelta() {
        return privacyDelta;
    }

    public void setPrivacyDelta(Double privacyDelta) {
        this.privacyDelta = privacyDelta;
    }

    public Boolean getEnableSecureAggregation() {
        return enableSecureAggregation;
    }

    public void setEnableSecureAggregation(Boolean enableSecureAggregation) {
        this.enableSecureAggregation = enableSecureAggregation;
    }

    public String getSecureAggregationProtocol() {
        return secureAggregationProtocol;
    }

    public void setSecureAggregationProtocol(String secureAggregationProtocol) {
        this.secureAggregationProtocol = secureAggregationProtocol;
    }

    public Integer getMinClientsForSecureAggregation() {
        return minClientsForSecureAggregation;
    }

    public void setMinClientsForSecureAggregation(Integer minClientsForSecureAggregation) {
        this.minClientsForSecureAggregation = minClientsForSecureAggregation;
    }

    public Boolean getEnableModelCompression() {
        return enableModelCompression;
    }

    public void setEnableModelCompression(Boolean enableModelCompression) {
        this.enableModelCompression = enableModelCompression;
    }

    public Integer getModelCompressionRatio() {
        return modelCompressionRatio;
    }

    public void setModelCompressionRatio(Integer modelCompressionRatio) {
        this.modelCompressionRatio = modelCompressionRatio;
    }

    public Boolean getEnableEnergyAwareScheduling() {
        return enableEnergyAwareScheduling;
    }

    public void setEnableEnergyAwareScheduling(Boolean enableEnergyAwareScheduling) {
        this.enableEnergyAwareScheduling = enableEnergyAwareScheduling;
    }

    public Boolean getRequireChargingForTraining() {
        return requireChargingForTraining;
    }

    public void setRequireChargingForTraining(Boolean requireChargingForTraining) {
        this.requireChargingForTraining = requireChargingForTraining;
    }

    public Boolean getRequireWifiForTraining() {
        return requireWifiForTraining;
    }

    public void setRequireWifiForTraining(Boolean requireWifiForTraining) {
        this.requireWifiForTraining = requireWifiForTraining;
    }

    public Integer getMinimumBatteryLevel() {
        return minimumBatteryLevel;
    }

    public void setMinimumBatteryLevel(Integer minimumBatteryLevel) {
        this.minimumBatteryLevel = minimumBatteryLevel;
    }

    public List<String> getSupportedModels() {
        return supportedModels;
    }

    public void setSupportedModels(List<String> supportedModels) {
        this.supportedModels = supportedModels;
    }

    public List<String> getSupportedLanguages() {
        return supportedLanguages;
    }

    public void setSupportedLanguages(List<String> supportedLanguages) {
        this.supportedLanguages = supportedLanguages;
    }

    public Integer getActiveModelCount() {
        return activeModelCount;
    }

    public void setActiveModelCount(Integer activeModelCount) {
        this.activeModelCount = activeModelCount;
    }

    public Integer getActiveClientCount() {
        return activeClientCount;
    }

    public void setActiveClientCount(Integer activeClientCount) {
        this.activeClientCount = activeClientCount;
    }

    public Integer getCompletedTrainingRounds() {
        return completedTrainingRounds;
    }

    public void setCompletedTrainingRounds(Integer completedTrainingRounds) {
        this.completedTrainingRounds = completedTrainingRounds;
    }

    public Double getAverageTrainingAccuracy() {
        return averageTrainingAccuracy;
    }

    public void setAverageTrainingAccuracy(Double averageTrainingAccuracy) {
        this.averageTrainingAccuracy = averageTrainingAccuracy;
    }

    public Double getAverageTrainingLoss() {
        return averageTrainingLoss;
    }

    public void setAverageTrainingLoss(Double averageTrainingLoss) {
        this.averageTrainingLoss = averageTrainingLoss;
    }

    public Double getAverageRoundDurationMinutes() {
        return averageRoundDurationMinutes;
    }

    public void setAverageRoundDurationMinutes(Double averageRoundDurationMinutes) {
        this.averageRoundDurationMinutes = averageRoundDurationMinutes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getLastHeartbeatTime() {
        return lastHeartbeatTime;
    }

    public void setLastHeartbeatTime(LocalDateTime lastHeartbeatTime) {
        this.lastHeartbeatTime = lastHeartbeatTime;
    }

    public LocalDateTime getLastTrainingRoundTime() {
        return lastTrainingRoundTime;
    }

    public void setLastTrainingRoundTime(LocalDateTime lastTrainingRoundTime) {
        this.lastTrainingRoundTime = lastTrainingRoundTime;
    }

    public Boolean getAutoScalingEnabled() {
        return autoScalingEnabled;
    }

    public void setAutoScalingEnabled(Boolean autoScalingEnabled) {
        this.autoScalingEnabled = autoScalingEnabled;
    }

    public Integer getMaxAutoScaleInstances() {
        return maxAutoScaleInstances;
    }

    public void setMaxAutoScaleInstances(Integer maxAutoScaleInstances) {
        this.maxAutoScaleInstances = maxAutoScaleInstances;
    }

    public Double getCpuUsageThreshold() {
        return cpuUsageThreshold;
    }

    public void setCpuUsageThreshold(Double cpuUsageThreshold) {
        this.cpuUsageThreshold = cpuUsageThreshold;
    }

    public Double getMemoryUsageThreshold() {
        return memoryUsageThreshold;
    }

    public void setMemoryUsageThreshold(Double memoryUsageThreshold) {
        this.memoryUsageThreshold = memoryUsageThreshold;
    }

    public Double getNetworkBandwidthMbps() {
        return networkBandwidthMbps;
    }

    public void setNetworkBandwidthMbps(Double networkBandwidthMbps) {
        this.networkBandwidthMbps = networkBandwidthMbps;
    }

    public String getHealthCheckEndpoint() {
        return healthCheckEndpoint;
    }

    public void setHealthCheckEndpoint(String healthCheckEndpoint) {
        this.healthCheckEndpoint = healthCheckEndpoint;
    }

    public Integer getHealthCheckIntervalSeconds() {
        return healthCheckIntervalSeconds;
    }

    public void setHealthCheckIntervalSeconds(Integer healthCheckIntervalSeconds) {
        this.healthCheckIntervalSeconds = healthCheckIntervalSeconds;
    }

    public Integer getHealthCheckTimeoutSeconds() {
        return healthCheckTimeoutSeconds;
    }

    public void setHealthCheckTimeoutSeconds(Integer healthCheckTimeoutSeconds) {
        this.healthCheckTimeoutSeconds = healthCheckTimeoutSeconds;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    // 业务方法
    public boolean isHealthy() {
        LocalDateTime now = LocalDateTime.now();
        return status == ServerStatus.ACTIVE && 
               lastHeartbeatTime.isAfter(now.minusSeconds(healthCheckIntervalSeconds * 2));
    }

    public boolean canAcceptClient() {
        return isHealthy() && activeClientCount < maxConcurrentClients;
    }

    public void addSupportedModel(String modelType) {
        if (!this.supportedModels.contains(modelType)) {
            this.supportedModels.add(modelType);
        }
    }

    public void addSupportedLanguage(String languageCode) {
        if (!this.supportedLanguages.contains(languageCode)) {
            this.supportedLanguages.add(languageCode);
        }
    }

    public void updateHeartbeat() {
        this.lastHeartbeatTime = LocalDateTime.now();
    }

    public void updateAfterTrainingRound(Double accuracy, Double loss, Integer durationMinutes) {
        this.completedTrainingRounds++;
        this.lastTrainingRoundTime = LocalDateTime.now();
        
        // 更新平均准确率（指数移动平均）
        this.averageTrainingAccuracy = 0.9 * this.averageTrainingAccuracy + 0.1 * accuracy;
        this.averageTrainingLoss = 0.9 * this.averageTrainingLoss + 0.1 * loss;
        this.averageRoundDurationMinutes = 0.9 * this.averageRoundDurationMinutes + 0.1 * durationMinutes;
    }

    @Override
    public String toString() {
        return "FederatedLearningServerEntity{" +
                "serverId='" + serverId + '\'' +
                ", serverName='" + serverName + '\'' +
                ", serverUrl='" + serverUrl + '\'' +
                ", region='" + region + '\'' +
                ", status=" + status +
                ", serverType=" + serverType +
                ", version='" + version + '\'' +
                ", activeModelCount=" + activeModelCount +
                ", activeClientCount=" + activeClientCount +
                ", completedTrainingRounds=" + completedTrainingRounds +
                ", averageTrainingAccuracy=" + averageTrainingAccuracy +
                '}';
    }
}