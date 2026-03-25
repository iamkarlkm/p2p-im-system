/// 联邦学习 AI 数据模型
/// 为即时通讯系统的联邦学习功能提供完整的 Dart 数据模型支持
/// 
/// @version 1.0
/// @created 2026-03-23

import 'dart:convert';

// ==================== 枚举类型 ====================

/// 服务器状态
enum ServerStatus {
  active,           // 活跃状态
  inactive,         // 非活跃状态
  maintenance,      // 维护中
  degraded,         // 性能降级
  error,            // 错误状态
  scaling           // 自动扩展中
}

/// 服务器类型
enum ServerType {
  central,          // 中央服务器
  regional,         // 区域服务器
  edge,             // 边缘服务器
  hybrid,           // 混合服务器
  mobile            // 移动服务器
}

/// 模型状态
enum ModelStatus {
  initializing,     // 初始化中
  active,           // 活跃状态
  training,         // 训练中
  aggregating,      // 聚合中
  converged,        // 已收敛
  maxRoundsReached, // 达到最大轮次
  error             // 错误状态
}

/// 模型类型
enum ModelType {
  smartReply,           // 智能回复
  spamDetection,        // 垃圾检测
  sentimentAnalysis,    // 情感分析
  messageCategorization // 消息分类
}

/// 更新状态
enum UpdateStatus {
  pending,        // 待处理
  received,       // 已接收
  verifying,      // 验证中
  verified,       // 已验证
  rejected,       // 已拒绝
  aggregating,    // 聚合中
  aggregated,     // 已聚合
  expired,        // 已过期
  error           // 错误
}

/// 更新类型
enum UpdateType {
  gradient,       // 梯度更新
  weight,         // 权重更新
  momentum,       // 动量更新
  adam,           // Adam 更新
  sparse,         // 稀疏更新
  differential    // 差分更新
}

/// 隐私级别
enum PrivacyLevel {
  minimal,        // 最小隐私保护
  basic,          // 基本隐私保护
  standard,       // 标准隐私保护
  enhanced,       // 增强隐私保护
  maximum         // 最大隐私保护
}

/// 训练轮次状态
enum RoundStatus {
  selectingClients,   // 选择客户端中
  training,           // 训练中
  collectingUpdates,  // 收集更新中
  verifying,          // 验证中
  aggregating,        // 聚合中
  completed,          // 已完成
  failed              // 失败
}

/// 设备类型
enum DeviceType {
  mobile,         // 移动设备
  desktop,        // 桌面设备
  tablet,         // 平板设备
  web,            // Web 端
  iot             // 物联网设备
}

// ==================== 核心数据模型 ====================

/// 联邦学习服务器配置
class FLServerConfig {
  final String serverName;
  final String serverUrl;
  final String region;
  final ServerType? serverType;
  final int? maxConcurrentClients;
  final int? maxClientsPerRound;
  final int? minClientsPerRound;
  final bool? enableDifferentialPrivacy;
  final double? privacyEpsilon;
  final bool? enableSecureAggregation;
  final String? aggregationAlgorithm;

  FLServerConfig({
    required this.serverName,
    required this.serverUrl,
    required this.region,
    this.serverType,
    this.maxConcurrentClients,
    this.maxClientsPerRound,
    this.minClientsPerRound,
    this.enableDifferentialPrivacy,
    this.privacyEpsilon,
    this.enableSecureAggregation,
    this.aggregationAlgorithm,
  });

  Map<String, dynamic> toJson() => {
    'serverName': serverName,
    'serverUrl': serverUrl,
    'region': region,
    'serverType': serverType?.name.toUpperCase(),
    'maxConcurrentClients': maxConcurrentClients,
    'maxClientsPerRound': maxClientsPerRound,
    'minClientsPerRound': minClientsPerRound,
    'enableDifferentialPrivacy': enableDifferentialPrivacy,
    'privacyEpsilon': privacyEpsilon,
    'enableSecureAggregation': enableSecureAggregation,
    'aggregationAlgorithm': aggregationAlgorithm,
  };

  factory FLServerConfig.fromJson(Map<String, dynamic> json) => FLServerConfig(
    serverName: json['serverName'] ?? '',
    serverUrl: json['serverUrl'] ?? '',
    region: json['region'] ?? '',
    serverType: ServerType.values.firstWhere(
      (e) => e.name.toUpperCase() == json['serverType'],
      orElse: () => ServerType.central,
    ),
    maxConcurrentClients: json['maxConcurrentClients'],
    maxClientsPerRound: json['maxClientsPerRound'],
    minClientsPerRound: json['minClientsPerRound'],
    enableDifferentialPrivacy: json['enableDifferentialPrivacy'],
    privacyEpsilon: json['privacyEpsilon']?.toDouble(),
    enableSecureAggregation: json['enableSecureAggregation'],
    aggregationAlgorithm: json['aggregationAlgorithm'],
  );
}

/// 联邦学习服务器实体
class FLServer {
  final String serverId;
  final String serverName;
  final String serverUrl;
  final String region;
  final ServerStatus status;
  final ServerType serverType;
  final String version;
  final String aggregationAlgorithm;
  final int maxConcurrentClients;
  final int maxClientsPerRound;
  final int minClientsPerRound;
  final int trainingRoundDurationMinutes;
  final double targetAccuracy;
  final int maxTrainingRounds;
  final bool enableDifferentialPrivacy;
  final double privacyEpsilon;
  final double privacyDelta;
  final bool enableSecureAggregation;
  final String secureAggregationProtocol;
  final int minClientsForSecureAggregation;
  final bool enableModelCompression;
  final int modelCompressionRatio;
  final bool enableEnergyAwareScheduling;
  final bool requireChargingForTraining;
  final bool requireWifiForTraining;
  final int minimumBatteryLevel;
  final List<String> supportedModels;
  final List<String> supportedLanguages;
  final int activeModelCount;
  final int activeClientCount;
  final int completedTrainingRounds;
  final double averageTrainingAccuracy;
  final double averageTrainingLoss;
  final double averageRoundDurationMinutes;
  final DateTime createdAt;
  final DateTime lastHeartbeatTime;
  final DateTime? lastTrainingRoundTime;
  final bool autoScalingEnabled;
  final int maxAutoScaleInstances;
  final double cpuUsageThreshold;
  final double memoryUsageThreshold;
  final double networkBandwidthMbps;
  final String healthCheckEndpoint;
  final int healthCheckIntervalSeconds;
  final int healthCheckTimeoutSeconds;
  final String? description;

  FLServer({
    required this.serverId,
    required this.serverName,
    required this.serverUrl,
    required this.region,
    required this.status,
    required this.serverType,
    required this.version,
    required this.aggregationAlgorithm,
    required this.maxConcurrentClients,
    required this.maxClientsPerRound,
    required this.minClientsPerRound,
    required this.trainingRoundDurationMinutes,
    required this.targetAccuracy,
    required this.maxTrainingRounds,
    required this.enableDifferentialPrivacy,
    required this.privacyEpsilon,
    required this.privacyDelta,
    required this.enableSecureAggregation,
    required this.secureAggregationProtocol,
    required this.minClientsForSecureAggregation,
    required this.enableModelCompression,
    required this.modelCompressionRatio,
    required this.enableEnergyAwareScheduling,
    required this.requireChargingForTraining,
    required this.requireWifiForTraining,
    required this.minimumBatteryLevel,
    required this.supportedModels,
    required this.supportedLanguages,
    required this.activeModelCount,
    required this.activeClientCount,
    required this.completedTrainingRounds,
    required this.averageTrainingAccuracy,
    required this.averageTrainingLoss,
    required this.averageRoundDurationMinutes,
    required this.createdAt,
    required this.lastHeartbeatTime,
    this.lastTrainingRoundTime,
    required this.autoScalingEnabled,
    required this.maxAutoScaleInstances,
    required this.cpuUsageThreshold,
    required this.memoryUsageThreshold,
    required this.networkBandwidthMbps,
    required this.healthCheckEndpoint,
    required this.healthCheckIntervalSeconds,
    required this.healthCheckTimeoutSeconds,
    this.description,
  });

  factory FLServer.fromJson(Map<String, dynamic> json) => FLServer(
    serverId: json['serverId'] ?? '',
    serverName: json['serverName'] ?? '',
    serverUrl: json['serverUrl'] ?? '',
    region: json['region'] ?? '',
    status: ServerStatus.values.firstWhere(
      (e) => e.name.toUpperCase() == json['status'],
      orElse: () => ServerStatus.inactive,
    ),
    serverType: ServerType.values.firstWhere(
      (e) => e.name.toUpperCase() == json['serverType'],
      orElse: () => ServerType.central,
    ),
    version: json['version'] ?? '1.0.0',
    aggregationAlgorithm: json['aggregationAlgorithm'] ?? 'FedAvg',
    maxConcurrentClients: json['maxConcurrentClients'] ?? 100,
    maxClientsPerRound: json['maxClientsPerRound'] ?? 10,
    minClientsPerRound: json['minClientsPerRound'] ?? 3,
    trainingRoundDurationMinutes: json['trainingRoundDurationMinutes'] ?? 30,
    targetAccuracy: (json['targetAccuracy'] ?? 0.95).toDouble(),
    maxTrainingRounds: json['maxTrainingRounds'] ?? 100,
    enableDifferentialPrivacy: json['enableDifferentialPrivacy'] ?? true,
    privacyEpsilon: (json['privacyEpsilon'] ?? 1.0).toDouble(),
    privacyDelta: (json['privacyDelta'] ?? 0.00001).toDouble(),
    enableSecureAggregation: json['enableSecureAggregation'] ?? true,
    secureAggregationProtocol: json['secureAggregationProtocol'] ?? 'SECOA',
    minClientsForSecureAggregation: json['minClientsForSecureAggregation'] ?? 3,
    enableModelCompression: json['enableModelCompression'] ?? true,
    modelCompressionRatio: json['modelCompressionRatio'] ?? 50,
    enableEnergyAwareScheduling: json['enableEnergyAwareScheduling'] ?? true,
    requireChargingForTraining: json['requireChargingForTraining'] ?? true,
    requireWifiForTraining: json['requireWifiForTraining'] ?? true,
    minimumBatteryLevel: json['minimumBatteryLevel'] ?? 50,
    supportedModels: List<String>.from(json['supportedModels'] ?? []),
    supportedLanguages: List<String>.from(json['supportedLanguages'] ?? []),
    activeModelCount: json['activeModelCount'] ?? 0,
    activeClientCount: json['activeClientCount'] ?? 0,
    completedTrainingRounds: json['completedTrainingRounds'] ?? 0,
    averageTrainingAccuracy: (json['averageTrainingAccuracy'] ?? 0.0).toDouble(),
    averageTrainingLoss: (json['averageTrainingLoss'] ?? 0.0).toDouble(),
    averageRoundDurationMinutes: (json['averageRoundDurationMinutes'] ?? 0.0).toDouble(),
    createdAt: DateTime.parse(json['createdAt']),
    lastHeartbeatTime: DateTime.parse(json['lastHeartbeatTime']),
    lastTrainingRoundTime: json['lastTrainingRoundTime'] != null 
        ? DateTime.parse(json['lastTrainingRoundTime']) 
        : null,
    autoScalingEnabled: json['autoScalingEnabled'] ?? true,
    maxAutoScaleInstances: json['maxAutoScaleInstances'] ?? 5,
    cpuUsageThreshold: (json['cpuUsageThreshold'] ?? 80.0).toDouble(),
    memoryUsageThreshold: (json['memoryUsageThreshold'] ?? 80.0).toDouble(),
    networkBandwidthMbps: (json['networkBandwidthMbps'] ?? 1000.0).toDouble(),
    healthCheckEndpoint: json['healthCheckEndpoint'] ?? '/health',
    healthCheckIntervalSeconds: json['healthCheckIntervalSeconds'] ?? 60,
    healthCheckTimeoutSeconds: json['healthCheckTimeoutSeconds'] ?? 10,
    description: json['description'],
  );

  Map<String, dynamic> toJson() => {
    'serverId': serverId,
    'serverName': serverName,
    'serverUrl': serverUrl,
    'region': region,
    'status': status.name.toUpperCase(),
    'serverType': serverType.name.toUpperCase(),
    'version': version,
    'aggregationAlgorithm': aggregationAlgorithm,
    'maxConcurrentClients': maxConcurrentClients,
    'maxClientsPerRound': maxClientsPerRound,
    'minClientsPerRound': minClientsPerRound,
    'trainingRoundDurationMinutes': trainingRoundDurationMinutes,
    'targetAccuracy': targetAccuracy,
    'maxTrainingRounds': maxTrainingRounds,
    'enableDifferentialPrivacy': enableDifferentialPrivacy,
    'privacyEpsilon': privacyEpsilon,
    'privacyDelta': privacyDelta,
    'enableSecureAggregation': enableSecureAggregation,
    'secureAggregationProtocol': secureAggregationProtocol,
    'minClientsForSecureAggregation': minClientsForSecureAggregation,
    'enableModelCompression': enableModelCompression,
    'modelCompressionRatio': modelCompressionRatio,
    'enableEnergyAwareScheduling': enableEnergyAwareScheduling,
    'requireChargingForTraining': requireChargingForTraining,
    'requireWifiForTraining': requireWifiForTraining,
    'minimumBatteryLevel': minimumBatteryLevel,
    'supportedModels': supportedModels,
    'supportedLanguages': supportedLanguages,
    'activeModelCount': activeModelCount,
    'activeClientCount': activeClientCount,
    'completedTrainingRounds': completedTrainingRounds,
    'averageTrainingAccuracy': averageTrainingAccuracy,
    'averageTrainingLoss': averageTrainingLoss,
    'averageRoundDurationMinutes': averageRoundDurationMinutes,
    'createdAt': createdAt.toIso8601String(),
    'lastHeartbeatTime': lastHeartbeatTime.toIso8601String(),
    'lastTrainingRoundTime': lastTrainingRoundTime?.toIso8601String(),
    'autoScalingEnabled': autoScalingEnabled,
    'maxAutoScaleInstances': maxAutoScaleInstances,
    'cpuUsageThreshold': cpuUsageThreshold,
    'memoryUsageThreshold': memoryUsageThreshold,
    'networkBandwidthMbps': networkBandwidthMbps,
    'healthCheckEndpoint': healthCheckEndpoint,
    'healthCheckIntervalSeconds': healthCheckIntervalSeconds,
    'healthCheckTimeoutSeconds': healthCheckTimeoutSeconds,
    'description': description,
  };

  /// 检查服务器是否健康
  bool get isHealthy => 
      status == ServerStatus.active &&
      lastHeartbeatTime.isAfter(DateTime.now().subtract(const Duration(seconds: 120)));

  /// 检查是否可以接受新客户端
  bool get canAcceptClient => isHealthy && activeClientCount < maxConcurrentClients;
}

/// 联邦学习模型实体
class FLModel {
  final String modelId;
  final String serverId;
  final String? userId;
  final ModelType modelType;
  final String modelName;
  final String language;
  final String version;
  final ModelStatus status;
  final String aggregationAlgorithm;
  final double targetAccuracy;
  final int maxTrainingRounds;
  final int currentTrainingRound;
  final int trainingRoundDurationMinutes;
  final int minClientsPerRound;
  final int maxClientsPerRound;
  final bool requireChargingForTraining;
  final bool requireWifiForTraining;
  final int minimumBatteryLevel;
  final bool enableDifferentialPrivacy;
  final double privacyBudgetPerRound;
  final double privacyDelta;
  final double dpNoiseScale;
  final double dpClipNorm;
  final String privacyLevel;
  final bool enableSecureAggregation;
  final int minClientsForSecureAggregation;
  final bool enableModelCompression;
  final int modelCompressionRatio;
  final int selectedClientCount;
  final int receivedUpdateCount;
  final int verifiedUpdateCount;
  final int aggregatedClientCount;
  final double averageClientAccuracy;
  final DateTime? lastAggregationTime;
  final int? lastAggregationRound;
  final DateTime? roundStartTime;
  final DateTime createdAt;
  final DateTime updatedAt;

  FLModel({
    required this.modelId,
    required this.serverId,
    this.userId,
    required this.modelType,
    required this.modelName,
    required this.language,
    required this.version,
    required this.status,
    required this.aggregationAlgorithm,
    required this.targetAccuracy,
    required this.maxTrainingRounds,
    required this.currentTrainingRound,
    required this.trainingRoundDurationMinutes,
    required this.minClientsPerRound,
    required this.maxClientsPerRound,
    required this.requireChargingForTraining,
    required this.requireWifiForTraining,
    required this.minimumBatteryLevel,
    required this.enableDifferentialPrivacy,
    required this.privacyBudgetPerRound,
    required this.privacyDelta,
    required this.dpNoiseScale,
    required this.dpClipNorm,
    required this.privacyLevel,
    required this.enableSecureAggregation,
    required this.minClientsForSecureAggregation,
    required this.enableModelCompression,
    required this.modelCompressionRatio,
    required this.selectedClientCount,
    required this.receivedUpdateCount,
    required this.verifiedUpdateCount,
    required this.aggregatedClientCount,
    required this.averageClientAccuracy,
    this.lastAggregationTime,
    this.lastAggregationRound,
    this.roundStartTime,
    required this.createdAt,
    required this.updatedAt,
  });

  factory FLModel.fromJson(Map<String, dynamic> json) => FLModel(
    modelId: json['modelId'] ?? '',
    serverId: json['serverId'] ?? '',
    userId: json['userId'],
    modelType: ModelType.values.firstWhere(
      (e) => e.name.toUpperCase() == json['modelType'],
      orElse: () => ModelType.smartReply,
    ),
    modelName: json['modelName'] ?? '',
    language: json['language'] ?? 'en',
    version: json['version'] ?? '1.0.0',
    status: ModelStatus.values.firstWhere(
      (e) => e.name.toUpperCase() == json['status'],
      orElse: () => ModelStatus.initializing,
    ),
    aggregationAlgorithm: json['aggregationAlgorithm'] ?? 'FedAvg',
    targetAccuracy: (json['targetAccuracy'] ?? 0.85).toDouble(),
    maxTrainingRounds: json['maxTrainingRounds'] ?? 50,
    currentTrainingRound: json['currentTrainingRound'] ?? 0,
    trainingRoundDurationMinutes: json['trainingRoundDurationMinutes'] ?? 30,
    minClientsPerRound: json['minClientsPerRound'] ?? 3,
    maxClientsPerRound: json['maxClientsPerRound'] ?? 10,
    requireChargingForTraining: json['requireChargingForTraining'] ?? true,
    requireWifiForTraining: json['requireWifiForTraining'] ?? true,
    minimumBatteryLevel: json['minimumBatteryLevel'] ?? 50,
    enableDifferentialPrivacy: json['enableDifferentialPrivacy'] ?? true,
    privacyBudgetPerRound: (json['privacyBudgetPerRound'] ?? 1.0).toDouble(),
    privacyDelta: (json['privacyDelta'] ?? 0.00001).toDouble(),
    dpNoiseScale: (json['dpNoiseScale'] ?? 1.0).toDouble(),
    dpClipNorm: (json['dpClipNorm'] ?? 1.0).toDouble(),
    privacyLevel: json['privacyLevel'] ?? 'STANDARD',
    enableSecureAggregation: json['enableSecureAggregation'] ?? true,
    minClientsForSecureAggregation: json['minClientsForSecureAggregation'] ?? 3,
    enableModelCompression: json['enableModelCompression'] ?? true,
    modelCompressionRatio: json['modelCompressionRatio'] ?? 50,
    selectedClientCount: json['selectedClientCount'] ?? 0,
    receivedUpdateCount: json['receivedUpdateCount'] ?? 0,
    verifiedUpdateCount: json['verifiedUpdateCount'] ?? 0,
    aggregatedClientCount: json['aggregatedClientCount'] ?? 0,
    averageClientAccuracy: (json['averageClientAccuracy'] ?? 0.0).toDouble(),
    lastAggregationTime: json['lastAggregationTime'] != null 
        ? DateTime.parse(json['lastAggregationTime']) 
        : null,
    lastAggregationRound: json['lastAggregationRound'],
    roundStartTime: json['roundStartTime'] != null 
        ? DateTime.parse(json['roundStartTime']) 
        : null,
    createdAt: DateTime.parse(json['createdAt']),
    updatedAt: DateTime.parse(json['updatedAt']),
  );

  Map<String, dynamic> toJson() => {
    'modelId': modelId,
    'serverId': serverId,
    'userId': userId,
    'modelType': modelType.name.toUpperCase(),
    'modelName': modelName,
    'language': language,
    'version': version,
    'status': status.name.toUpperCase(),
    'aggregationAlgorithm': aggregationAlgorithm,
    'targetAccuracy': targetAccuracy,
    'maxTrainingRounds': maxTrainingRounds,
    'currentTrainingRound': currentTrainingRound,
    'trainingRoundDurationMinutes': trainingRoundDurationMinutes,
    'minClientsPerRound': minClientsPerRound,
    'maxClientsPerRound': maxClientsPerRound,
    'requireChargingForTraining': requireChargingForTraining,
    'requireWifiForTraining': requireWifiForTraining,
    'minimumBatteryLevel': minimumBatteryLevel,
    'enableDifferentialPrivacy': enableDifferentialPrivacy,
    'privacyBudgetPerRound': privacyBudgetPerRound,
    'privacyDelta': privacyDelta,
    'dpNoiseScale': dpNoiseScale,
    'dpClipNorm': dpClipNorm,
    'privacyLevel': privacyLevel,
    'enableSecureAggregation': enableSecureAggregation,
    'minClientsForSecureAggregation': minClientsForSecureAggregation,
    'enableModelCompression': enableModelCompression,
    'modelCompressionRatio': modelCompressionRatio,
    'selectedClientCount': selectedClientCount,
    'receivedUpdateCount': receivedUpdateCount,
    'verifiedUpdateCount': verifiedUpdateCount,
    'aggregatedClientCount': aggregatedClientCount,
    'averageClientAccuracy': averageClientAccuracy,
    'lastAggregationTime': lastAggregationTime?.toIso8601String(),
    'lastAggregationRound': lastAggregationRound,
    'roundStartTime': roundStartTime?.toIso8601String(),
    'createdAt': createdAt.toIso8601String(),
    'updatedAt': updatedAt.toIso8601String(),
  };
}

/// 模型更新实体
class FLModelUpdate {
  final String updateId;
  final String modelId;
  final String serverId;
  final String clientId;
  final int trainingRound;
  final UpdateStatus status;
  final UpdateType updateType;
  final String encryptedUpdateData;
  final String encryptionAlgorithm;
  final String encryptionKeyId;
  final int dataSizeBytes;
  final int localTrainingSamples;
  final double trainingLoss;
  final double trainingAccuracy;
  final int trainingEpochs;
  final int trainingBatchSize;
  final double learningRate;
  final int trainingDurationMs;
  final double deviceCpuUsage;
  final double deviceMemoryUsage;
  final int deviceBatteryLevel;
  final bool deviceWasCharging;
  final String networkType;
  final double networkBandwidthMbps;
  final double networkLatencyMs;
  final bool enableDifferentialPrivacy;
  final double privacyEpsilonUsed;
  final double privacyDeltaUsed;
  final String dpNoiseType;
  final double dpNoiseScale;
  final double dpClipNorm;
  final PrivacyLevel privacyLevel;
  final bool enableSecureAggregation;
  final String aggregationGroupId;
  final int aggregationGroupSize;
  final int aggregationClientIndex;
  final bool enableModelCompression;
  final int compressionRatio;
  final String compressionAlgorithm;
  final double compressionQualityScore;
  final bool enableQuantization;
  final int quantizationBits;
  final Map<String, double> trainingMetrics;
  final Map<String, int> labelDistribution;
  final String modelHash;
  final String updateSignature;
  final String clientPublicKey;
  final String verificationStatus;
  final String? verificationNotes;
  final double qualityScore;
  final double contributionScore;
  final bool isAnomalous;
  final String? anomalyReason;
  final double anomalyScore;
  final bool includedInAggregation;
  final int? aggregationRound;
  final DateTime createdAt;
  final DateTime updatedAt;
  final DateTime? verifiedAt;
  final DateTime? aggregatedAt;
  final String clientVersion;
  final String clientPlatform;
  final String clientOsVersion;
  final String clientDeviceModel;
  final String? clientIpAddress;
  final String? clientLocation;

  FLModelUpdate({
    required this.updateId,
    required this.modelId,
    required this.serverId,
    required this.clientId,
    required this.trainingRound,
    required this.status,
    required this.updateType,
    required this.encryptedUpdateData,
    required this.encryptionAlgorithm,
    required this.encryptionKeyId,
    required this.dataSizeBytes,
    required this.localTrainingSamples,
    required this.trainingLoss,
    required this.trainingAccuracy,
    required this.trainingEpochs,
    required this.trainingBatchSize,
    required this.learningRate,
    required this.trainingDurationMs,
    required this.deviceCpuUsage,
    required this.deviceMemoryUsage,
    required this.deviceBatteryLevel,
    required this.deviceWasCharging,
    required this.networkType,
    required this.networkBandwidthMbps,
    required this.networkLatencyMs,
    required this.enableDifferentialPrivacy,
    required this.privacyEpsilonUsed,
    required this.privacyDeltaUsed,
    required this.dpNoiseType,
    required this.dpNoiseScale,
    required this.dpClipNorm,
    required this.privacyLevel,
    required this.enableSecureAggregation,
    required this.aggregationGroupId,
    required this.aggregationGroupSize,
    required this.aggregationClientIndex,
    required this.enableModelCompression,
    required this.compressionRatio,
    required this.compressionAlgorithm,
    required this.compressionQualityScore,
    required this.enableQuantization,
    required this.quantizationBits,
    required this.trainingMetrics,
    required this.labelDistribution,
    required this.modelHash,
    required this.updateSignature,
    required this.clientPublicKey,
    required this.verificationStatus,
    this.verificationNotes,
    required this.qualityScore,
    required this.contributionScore,
    required this.isAnomalous,
    this.anomalyReason,
    required this.anomalyScore,
    required this.includedInAggregation,
    this.aggregationRound,
    required this.createdAt,
    required this.updatedAt,
    this.verifiedAt,
    this.aggregatedAt,
    required this.clientVersion,
    required this.clientPlatform,
    required this.clientOsVersion,
    required this.clientDeviceModel,
    this.clientIpAddress,
    this.clientLocation,
  });

  factory FLModelUpdate.fromJson(Map<String, dynamic> json) => FLModelUpdate(
    updateId: json['updateId'] ?? '',
    modelId: json['modelId'] ?? '',
    serverId: json['serverId'] ?? '',
    clientId: json['clientId'] ?? '',
    trainingRound: json['trainingRound'] ?? 0,
    status: UpdateStatus.values.firstWhere(
      (e) => e.name.toUpperCase() == json['status'],
      orElse: () => UpdateStatus.pending,
    ),
    updateType: UpdateType.values.firstWhere(
      (e) => e.name.toUpperCase() == json['updateType'],
      orElse: () => UpdateType.gradient,
    ),
    encryptedUpdateData: json['encryptedUpdateData'] ?? '',
    encryptionAlgorithm: json['encryptionAlgorithm'] ?? 'AES-256-GCM',
    encryptionKeyId: json['encryptionKeyId'] ?? '',
    dataSizeBytes: json['dataSizeBytes'] ?? 0,
    localTrainingSamples: json['localTrainingSamples'] ?? 0,
    trainingLoss: (json['trainingLoss'] ?? 0.0).toDouble(),
    trainingAccuracy: (json['trainingAccuracy'] ?? 0.0).toDouble(),
    trainingEpochs: json['trainingEpochs'] ?? 0,
    trainingBatchSize: json['trainingBatchSize'] ?? 0,
    learningRate: (json['learningRate'] ?? 0.001).toDouble(),
    trainingDurationMs: json['trainingDurationMs'] ?? 0,
    deviceCpuUsage: (json['deviceCpuUsage'] ?? 0.0).toDouble(),
    deviceMemoryUsage: (json['deviceMemoryUsage'] ?? 0.0).toDouble(),
    deviceBatteryLevel: json['deviceBatteryLevel'] ?? 0,
    deviceWasCharging: json['deviceWasCharging'] ?? false,
    networkType: json['networkType'] ?? 'UNKNOWN',
    networkBandwidthMbps: (json['networkBandwidthMbps'] ?? 0.0).toDouble(),
    networkLatencyMs: (json['networkLatencyMs'] ?? 0.0).toDouble(),
    enableDifferentialPrivacy: json['enableDifferentialPrivacy'] ?? true,
    privacyEpsilonUsed: (json['privacyEpsilonUsed'] ?? 0.0).toDouble(),
    privacyDeltaUsed: (json['privacyDeltaUsed'] ?? 0.0).toDouble(),
    dpNoiseType: json['dpNoiseType'] ?? 'Gaussian',
    dpNoiseScale: (json['dpNoiseScale'] ?? 1.0).toDouble(),
    dpClipNorm: (json['dpClipNorm'] ?? 1.0).toDouble(),
    privacyLevel: PrivacyLevel.values.firstWhere(
      (e) => e.name.toUpperCase() == json['privacyLevel'],
      orElse: () => PrivacyLevel.standard,
    ),
    enableSecureAggregation: json['enableSecureAggregation'] ?? true,
    aggregationGroupId: json['aggregationGroupId'] ?? '',
    aggregationGroupSize: json['aggregationGroupSize'] ?? 0,
    aggregationClientIndex: json['aggregationClientIndex'] ?? 0,
    enableModelCompression: json['enableModelCompression'] ?? true,
    compressionRatio: json['compressionRatio'] ?? 50,
    compressionAlgorithm: json['compressionAlgorithm'] ?? 'Pruning',
    compressionQualityScore: (json['compressionQualityScore'] ?? 0.0).toDouble(),
    enableQuantization: json['enableQuantization'] ?? false,
    quantizationBits: json['quantizationBits'] ?? 8,
    trainingMetrics: Map<String, double>.from(json['trainingMetrics'] ?? {}),
    labelDistribution: Map<String, int>.from(json['labelDistribution'] ?? {}),
    modelHash: json['modelHash'] ?? '',
    updateSignature: json['updateSignature'] ?? '',
    clientPublicKey: json['clientPublicKey'] ?? '',
    verificationStatus: json['verificationStatus'] ?? 'PENDING',
    verificationNotes: json['verificationNotes'],
    qualityScore: (json['qualityScore'] ?? 0.0).toDouble(),
    contributionScore: (json['contributionScore'] ?? 0.0).toDouble(),
    isAnomalous: json['isAnomalous'] ?? false,
    anomalyReason: json['anomalyReason'],
    anomalyScore: (json['anomalyScore'] ?? 0.0).toDouble(),
    includedInAggregation: json['includedInAggregation'] ?? false,
    aggregationRound: json['aggregationRound'],
    createdAt: DateTime.parse(json['createdAt']),
    updatedAt: DateTime.parse(json['updatedAt']),
    verifiedAt: json['verifiedAt'] != null ? DateTime.parse(json['verifiedAt']) : null,
    aggregatedAt: json['aggregatedAt'] != null ? DateTime.parse(json['aggregatedAt']) : null,
    clientVersion: json['clientVersion'] ?? '1.0.0',
    clientPlatform: json['clientPlatform'] ?? 'Unknown',
    clientOsVersion: json['clientOsVersion'] ?? 'Unknown',
    clientDeviceModel: json['clientDeviceModel'] ?? 'Unknown',
    clientIpAddress: json['clientIpAddress'],
    clientLocation: json['clientLocation'],
  );

  Map<String, dynamic> toJson() => {
    'updateId': updateId,
    'modelId': modelId,
    'serverId': serverId,
    'clientId': clientId,
    'trainingRound': trainingRound,
    'status': status.name.toUpperCase(),
    'updateType': updateType.name.toUpperCase(),
    'encryptedUpdateData': encryptedUpdateData,
    'encryptionAlgorithm': encryptionAlgorithm,
    'encryptionKeyId': encryptionKeyId,
    'dataSizeBytes': dataSizeBytes,
    'localTrainingSamples': localTrainingSamples,
    'trainingLoss': trainingLoss,
    'trainingAccuracy': trainingAccuracy,
    'trainingEpochs': trainingEpochs,
    'trainingBatchSize': trainingBatchSize,
    'learningRate': learningRate,
    'trainingDurationMs': trainingDurationMs,
    'deviceCpuUsage': deviceCpuUsage,
    'deviceMemoryUsage': deviceMemoryUsage,
    'deviceBatteryLevel': deviceBatteryLevel,
    'deviceWasCharging': deviceWasCharging,
    'networkType': networkType,
    'networkBandwidthMbps': networkBandwidthMbps,
    'networkLatencyMs': networkLatencyMs,
    'enableDifferentialPrivacy': enableDifferentialPrivacy,
    'privacyEpsilonUsed': privacyEpsilonUsed,
    'privacyDeltaUsed': privacyDeltaUsed,
    'dpNoiseType': dpNoiseType,
    'dpNoiseScale': dpNoiseScale,
    'dpClipNorm': dpClipNorm,
    'privacyLevel': privacyLevel.name.toUpperCase(),
    'enableSecureAggregation': enableSecureAggregation,
    'aggregationGroupId': aggregationGroupId,
    'aggregationGroupSize': aggregationGroupSize,
    'aggregationClientIndex': aggregationClientIndex,
    'enableModelCompression': enableModelCompression,
    'compressionRatio': compressionRatio,
    'compressionAlgorithm': compressionAlgorithm,
    'compressionQualityScore': compressionQualityScore,
    'enableQuantization': enableQuantization,
    'quantizationBits': quantizationBits,
    'trainingMetrics': trainingMetrics,
    'labelDistribution': labelDistribution,
    'modelHash': modelHash,
    'updateSignature': updateSignature,
    'clientPublicKey': clientPublicKey,
    'verificationStatus': verificationStatus,
    'verificationNotes': verificationNotes,
    'qualityScore': qualityScore,
    'contributionScore': contributionScore,
    'isAnomalous': isAnomalous,
    'anomalyReason': anomalyReason,
    'anomalyScore': anomalyScore,
    'includedInAggregation': includedInAggregation,
    'aggregationRound': aggregationRound,
    'createdAt': createdAt.toIso8601String(),
    'updatedAt': updatedAt.toIso8601String(),
    'verifiedAt': verifiedAt?.toIso8601String(),
    'aggregatedAt': aggregatedAt?.toIso8601String(),
    'clientVersion': clientVersion,
    'clientPlatform': clientPlatform,
    'clientOsVersion': clientOsVersion,
    'clientDeviceModel': clientDeviceModel,
    'clientIpAddress': clientIpAddress,
    'clientLocation': clientLocation,
  };

  /// 检查是否已准备好聚合
  bool get isReadyForAggregation => 
      status == UpdateStatus.verified &&
      !isAnomalous &&
      qualityScore >= 0.7;

  /// 检查是否过期
  bool get isExpired => DateTime.now().isAfter(createdAt.add(const Duration(hours: 24)));
}

/// 智能回复建议响应
class SmartReplyResponse {
  final String userId;
  final String modelId;
  final String modelVersion;
  final List<String> suggestions;
  final bool privacyProtected;
  final bool localTrainingOnly;
  final DateTime generatedAt;

  SmartReplyResponse({
    required this.userId,
    required this.modelId,
    required this.modelVersion,
    required this.suggestions,
    required this.privacyProtected,
    required this.localTrainingOnly,
    required this.generatedAt,
  });

  factory SmartReplyResponse.fromJson(Map<String, dynamic> json) => SmartReplyResponse(
    userId: json['userId'] ?? '',
    modelId: json['modelId'] ?? '',
    modelVersion: json['modelVersion'] ?? '1.0.0',
    suggestions: List<String>.from(json['suggestions'] ?? []),
    privacyProtected: json['privacyProtected'] ?? true,
    localTrainingOnly: json['localTrainingOnly'] ?? true,
    generatedAt: DateTime.parse(json['generatedAt']),
  );

  Map<String, dynamic> toJson() => {
    'userId': userId,
    'modelId': modelId,
    'modelVersion': modelVersion,
    'suggestions': suggestions,
    'privacyProtected': privacyProtected,
    'localTrainingOnly': localTrainingOnly,
    'generatedAt': generatedAt.toIso8601String(),
  };
}

/// 垃圾检测结果响应
class SpamDetectionResponse {
  final String userId;
  final String? messageId;
  final bool isSpam;
  final double confidence;
  final List<String> reasons;
  final String modelId;
  final String modelVersion;
  final bool privacyProtected;
  final DateTime detectedAt;

  SpamDetectionResponse({
    required this.userId,
    this.messageId,
    required this.isSpam,
    required this.confidence,
    required this.reasons,
    required this.modelId,
    required this.modelVersion,
    required this.privacyProtected,
    required this.detectedAt,
  });

  factory SpamDetectionResponse.fromJson(Map<String, dynamic> json) => SpamDetectionResponse(
    userId: json['userId'] ?? '',
    messageId: json['messageId'],
    isSpam: json['isSpam'] ?? false,
    confidence: (json['confidence'] ?? 0.0).toDouble(),
    reasons: List<String>.from(json['reasons'] ?? []),
    modelId: json['modelId'] ?? '',
    modelVersion: json['modelVersion'] ?? '1.0.0',
    privacyProtected: json['privacyProtected'] ?? true,
    detectedAt: DateTime.parse(json['detectedAt']),
  );

  Map<String, dynamic> toJson() => {
    'userId': userId,
    'messageId': messageId,
    'isSpam': isSpam,
    'confidence': confidence,
    'reasons': reasons,
    'modelId': modelId,
    'modelVersion': modelVersion,
    'privacyProtected': privacyProtected,
    'detectedAt': detectedAt.toIso8601String(),
  };
}

/// 情感分析响应
class SentimentAnalysisResponse {
  final String userId;
  final String message;
  final String sentiment;
  final double positiveScore;
  final double negativeScore;
  final double neutralScore;
  final String dominantSentiment;
  final String modelId;
  final String modelVersion;
  final bool privacyProtected;
  final DateTime analyzedAt;

  SentimentAnalysisResponse({
    required this.userId,
    required this.message,
    required this.sentiment,
    required this.positiveScore,
    required this.negativeScore,
    required this.neutralScore,
    required this.dominantSentiment,
    required this.modelId,
    required this.modelVersion,
    required this.privacyProtected,
    required this.analyzedAt,
  });

  factory SentimentAnalysisResponse.fromJson(Map<String, dynamic> json) => SentimentAnalysisResponse(
    userId: json['userId'] ?? '',
    message: json['message'] ?? '',
    sentiment: json['sentiment'] ?? 'NEUTRAL',
    positiveScore: (json['positiveScore'] ?? 0.0).toDouble(),
    negativeScore: (json['negativeScore'] ?? 0.0).toDouble(),
    neutralScore: (json['neutralScore'] ?? 0.0).toDouble(),
    dominantSentiment: json['dominantSentiment'] ?? 'NEUTRAL',
    modelId: json['modelId'] ?? '',
    modelVersion: json['modelVersion'] ?? '1.0.0',
    privacyProtected: json['privacyProtected'] ?? true,
    analyzedAt: DateTime.parse(json['analyzedAt']),
  );

  Map<String, dynamic> toJson() => {
    'userId': userId,
    'message': message,
    'sentiment': sentiment,
    'positiveScore': positiveScore,
    'negativeScore': negativeScore,
    'neutralScore': neutralScore,
    'dominantSentiment': dominantSentiment,
    'modelId': modelId,
    'modelVersion': modelVersion,
    'privacyProtected': privacyProtected,
    'analyzedAt': analyzedAt.toIso8601String(),
  };
}

/// API 响应包装器
class ApiResponse<T> {
  final bool success;
  final T? data;
  final String? error;
  final int? statusCode;
  final String? timestamp;

  ApiResponse({
    required this.success,
    this.data,
    this.error,
    this.statusCode,
    this.timestamp,
  });

  factory ApiResponse.fromJson(Map<String, dynamic> json, T? Function(Map<String, dynamic>)? fromJson) => ApiResponse(
    success: json['success'] ?? false,
    data: json['data'] != null && fromJson != null ? fromJson(json['data']) : null,
    error: json['error'],
    statusCode: json['statusCode'],
    timestamp: json['timestamp'],
  );

  Map<String, dynamic> toJson() => {
    'success': success,
    'data': data is Map ? data : null,
    'error': error,
    'statusCode': statusCode,
    'timestamp': timestamp,
  };
}

// ==================== 工具函数 ====================

/// 检查服务器是否健康
bool isServerHealthy(FLServer server) {
  return server.status == ServerStatus.active &&
         server.lastHeartbeatTime.isAfter(DateTime.now().subtract(const Duration(seconds: 120)));
}

/// 检查客户端是否符合训练条件
bool isClientEligibleForTraining({
  required bool isOnline,
  required int batteryLevel,
  required bool isCharging,
  required String networkType,
}) {
  return isOnline &&
         batteryLevel >= 50 &&
         (isCharging || batteryLevel >= 80) &&
         networkType.toUpperCase() == 'WIFI';
}

/// 计算隐私预算使用率
double calculatePrivacyBudgetUsage({
  required double usedEpsilon,
  required double totalEpsilon,
}) {
  if (totalEpsilon == 0) return 0;
  return (usedEpsilon / totalEpsilon) * 100;
}

/// 格式化时间戳为本地时间字符串
String formatTimestamp(DateTime timestamp) {
  return '${timestamp.year}-${timestamp.month.toString().padLeft(2, '0')}-${timestamp.day.toString().padLeft(2, '0')} '
      '${timestamp.hour.toString().padLeft(2, '0')}:${timestamp.minute.toString().padLeft(2, '0')}';
}

/// 生成默认服务器配置
FLServerConfig getDefaultServerConfig(String region) {
  return FLServerConfig(
    serverName: 'FL-Server-$region',
    serverUrl: 'https://fl-${region.toLowerCase()}.im-system.com',
    region: region,
    serverType: ServerType.regional,
    maxConcurrentClients: 100,
    maxClientsPerRound: 10,
    minClientsPerRound: 3,
    enableDifferentialPrivacy: true,
    privacyEpsilon: 1.0,
    enableSecureAggregation: true,
    aggregationAlgorithm: 'FedAvg',
  );
}
