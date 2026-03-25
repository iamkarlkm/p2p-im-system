/// 联邦学习模型 - Flutter 移动端

enum ModelType {
  recommendation,
  classification,
  clustering,
  regression,
  neuralNetwork,
  deepLearning,
  transformer,
  collaborativeFiltering
}

enum ModelScope {
  global,
  regional,
  organization,
  group,
  personalized
}

enum ConvergenceStatus {
  training,
  converging,
  converged,
  diverging,
  paused,
  failed
}

enum RecommendationType {
  messageSuggestion,
  contactSuggestion,
  groupSuggestion,
  contentSuggestion,
  channelSuggestion,
  botSuggestion,
  actionSuggestion,
  personalizedFeed,
  trendingTopic,
  similarUser
}

enum PrivacyLevel {
  minimal,
  basic,
  standard,
  enhanced,
  maximum
}

enum RecommendationStatus {
  pending,
  generating,
  ready,
  delivered,
  interacted,
  expired,
  failed,
  withdrawn
}

class FederatedLearningModel {
  final String modelId;
  final String modelName;
  final ModelType modelType;
  final String modelVersion;
  final ModelScope modelScope;
  final String aggregationAlgorithm;
  final double? privacyBudget;
  final double? noiseScale;
  final double? learningRate;
  final int? batchSize;
  final int? epochs;
  final int? participatingClients;
  final int minimumClients;
  final int aggregationRound;
  final double? modelAccuracy;
  final double? modelLoss;
  final ConvergenceStatus convergenceStatus;
  final bool isActive;
  final bool isEncrypted;
  final DateTime createdAt;
  final DateTime updatedAt;

  FederatedLearningModel({
    required this.modelId,
    required this.modelName,
    required this.modelType,
    required this.modelVersion,
    required this.modelScope,
    required this.aggregationAlgorithm,
    this.privacyBudget,
    this.noiseScale,
    this.learningRate,
    this.batchSize,
    this.epochs,
    this.participatingClients,
    this.minimumClients = 10,
    this.aggregationRound = 0,
    this.modelAccuracy,
    this.modelLoss,
    this.convergenceStatus = ConvergenceStatus.training,
    this.isActive = true,
    this.isEncrypted = true,
    required this.createdAt,
    required this.updatedAt,
  });

  factory FederatedLearningModel.fromJson(Map<String, dynamic> json) {
    return FederatedLearningModel(
      modelId: json['modelId'] ?? '',
      modelName: json['modelName'] ?? '',
      modelType: ModelType.values.firstWhere(
        (e) => e.toString().split('.').last == json['modelType'],
        orElse: () => ModelType.recommendation,
      ),
      modelVersion: json['modelVersion'] ?? 'v1.0.0',
      modelScope: ModelScope.values.firstWhere(
        (e) => e.toString().split('.').last == json['modelScope'],
        orElse: () => ModelScope.global,
      ),
      aggregationAlgorithm: json['aggregationAlgorithm'] ?? 'FedAvg',
      privacyBudget: json['privacyBudget']?.toDouble(),
      noiseScale: json['noiseScale']?.toDouble(),
      learningRate: json['learningRate']?.toDouble(),
      batchSize: json['batchSize'],
      epochs: json['epochs'],
      participatingClients: json['participatingClients'],
      minimumClients: json['minimumClients'] ?? 10,
      aggregationRound: json['aggregationRound'] ?? 0,
      modelAccuracy: json['modelAccuracy']?.toDouble(),
      modelLoss: json['modelLoss']?.toDouble(),
      convergenceStatus: ConvergenceStatus.values.firstWhere(
        (e) => e.toString().split('.').last == json['convergenceStatus'],
        orElse: () => ConvergenceStatus.training,
      ),
      isActive: json['isActive'] ?? true,
      isEncrypted: json['isEncrypted'] ?? true,
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'])
          : DateTime.now(),
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'])
          : DateTime.now(),
    );
  }

  bool get canAggregate =>
      (participatingClients ?? 0) >= minimumClients &&
      convergenceStatus != ConvergenceStatus.converged &&
      convergenceStatus != ConvergenceStatus.failed;
}

class PrivacyPreservingRecommendation {
  final String recommendationId;
  final String userId;
  final String? sessionId;
  final String modelId;
  final RecommendationType recommendationType;
  final String? recommendationContext;
  final String? recommendedContentIds;
  final int? totalRecommendations;
  final double? userFeedbackScore;
  final bool? clickThrough;
  final int? dwellTimeSeconds;
  final PrivacyLevel privacyLevel;
  final bool differentialPrivacyEnabled;
  final double? privacyBudgetConsumed;
  final RecommendationStatus status;
  final double? qualityScore;
  final DateTime createdAt;
  final DateTime? processedAt;

  PrivacyPreservingRecommendation({
    required this.recommendationId,
    required this.userId,
    this.sessionId,
    required this.modelId,
    required this.recommendationType,
    this.recommendationContext,
    this.recommendedContentIds,
    this.totalRecommendations,
    this.userFeedbackScore,
    this.clickThrough,
    this.dwellTimeSeconds,
    this.privacyLevel = PrivacyLevel.standard,
    this.differentialPrivacyEnabled = true,
    this.privacyBudgetConsumed,
    this.status = RecommendationStatus.pending,
    this.qualityScore,
    required this.createdAt,
    this.processedAt,
  });

  factory PrivacyPreservingRecommendation.fromJson(Map<String, dynamic> json) {
    return PrivacyPreservingRecommendation(
      recommendationId: json['recommendationId'] ?? '',
      userId: json['userId'] ?? '',
      sessionId: json['sessionId'],
      modelId: json['modelId'] ?? '',
      recommendationType: RecommendationType.values.firstWhere(
        (e) => e.toString().split('.').last == json['recommendationType'],
        orElse: () => RecommendationType.messageSuggestion,
      ),
      recommendationContext: json['recommendationContext'],
      recommendedContentIds: json['recommendedContentIds'],
      totalRecommendations: json['totalRecommendations'],
      userFeedbackScore: json['userFeedbackScore']?.toDouble(),
      clickThrough: json['clickThrough'],
      dwellTimeSeconds: json['dwellTimeSeconds'],
      privacyLevel: PrivacyLevel.values.firstWhere(
        (e) => e.toString().split('.').last == json['privacyLevel'],
        orElse: () => PrivacyLevel.standard,
      ),
      differentialPrivacyEnabled: json['differentialPrivacyEnabled'] ?? true,
      privacyBudgetConsumed: json['privacyBudgetConsumed']?.toDouble(),
      status: RecommendationStatus.values.firstWhere(
        (e) => e.toString().split('.').last == json['status'],
        orElse: () => RecommendationStatus.pending,
      ),
      qualityScore: json['qualityScore']?.toDouble(),
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'])
          : DateTime.now(),
      processedAt: json['processedAt'] != null
          ? DateTime.parse(json['processedAt'])
          : null,
    );
  }

  bool get isDelivered => status == RecommendationStatus.delivered;
  bool get isInteracted => status == RecommendationStatus.interacted;
}

class ModelConfig {
  final double privacyBudget;
  final double noiseScale;
  final double clipNorm;
  final double learningRate;
  final int batchSize;
  final int epochs;
  final int minimumClients;
  final String aggregationAlgorithm;

  ModelConfig({
    this.privacyBudget = 1.0,
    this.noiseScale = 0.1,
    this.clipNorm = 1.0,
    this.learningRate = 0.01,
    this.batchSize = 32,
    this.epochs = 10,
    this.minimumClients = 10,
    this.aggregationAlgorithm = 'FedAvg',
  });

  Map<String, dynamic> toJson() => {
        'privacyBudget': privacyBudget,
        'noiseScale': noiseScale,
        'clipNorm': clipNorm,
        'learningRate': learningRate,
        'batchSize': batchSize,
        'epochs': epochs,
        'minimumClients': minimumClients,
        'aggregationAlgorithm': aggregationAlgorithm,
      };
}