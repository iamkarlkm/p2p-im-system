// 自适应内容分类服务 - Dart Flutter 模型
// 支持自定义分类体系、增量学习和多模态内容分类

import 'dart:convert';

/// 分类类型枚举
enum ClassificationType {
  hierarchical,  // 层级分类
  flat,          // 扁平分类
  hybrid         // 混合分类
}

/// 内容模态枚举
enum ContentModality {
  textOnly,      // 仅文本
  imageOnly,     // 仅图像
  audioOnly,     // 仅音频
  videoOnly,     // 仅视频
  multimodal     // 多模态
}

/// 分类隐私级别枚举
enum ClassificationPrivacyLevel {
  public,        // 公开
  protected,     // 受保护
  private,       // 私有
  confidential   // 机密
}

/// 分类状态枚举
enum ClassificationStatus {
  draft,         // 草稿
  active,        // 活跃
  inactive,      // 非活跃
  archived,      // 已归档
  deleted        // 已删除
}

/// 内容类型枚举
enum ContentType {
  textMessage,
  imageMessage,
  audioMessage,
  videoMessage,
  fileMessage,
  locationMessage,
  contactMessage,
  systemMessage,
  linkMessage,
  pollMessage,
  emojiMessage,
  stickerMessage
}

/// 分类配置模型
class AdaptiveContentClassificationConfig {
  final int? id;
  final String name;
  final String? description;
  final int userId;
  final int? sessionId;
  final String categoryHierarchy;
  final ClassificationType classificationType;
  final ContentModality contentModality;
  final int minConfidenceScore;
  final bool enableIncrementalLearning;
  final int incrementalLearningBatchSize;
  final bool enableContextAwareness;
  final int contextWindowSize;
  final bool enableMultiLanguage;
  final String? supportedLanguages;
  final bool enableAutoLabelRecommendation;
  final int maxLabelRecommendations;
  final bool enablePrivacyProtection;
  final ClassificationPrivacyLevel privacyLevel;
  final bool enableEvolutionTracking;
  final int evolutionTrackingDepth;
  final ClassificationStatus status;
  final int? version;
  final String? versionNotes;
  final double? accuracyScore;
  final int? totalClassifications;
  final int? correctClassifications;
  final String? feedbackStatistics;
  final String? performanceMetrics;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  AdaptiveContentClassificationConfig({
    this.id,
    required this.name,
    this.description,
    required this.userId,
    this.sessionId,
    required this.categoryHierarchy,
    required this.classificationType,
    required this.contentModality,
    this.minConfidenceScore = 70,
    this.enableIncrementalLearning = true,
    this.incrementalLearningBatchSize = 100,
    this.enableContextAwareness = true,
    this.contextWindowSize = 10,
    this.enableMultiLanguage = true,
    this.supportedLanguages,
    this.enableAutoLabelRecommendation = true,
    this.maxLabelRecommendations = 5,
    this.enablePrivacyProtection = true,
    this.privacyLevel = ClassificationPrivacyLevel.private,
    this.enableEvolutionTracking = true,
    this.evolutionTrackingDepth = 30,
    required this.status,
    this.version,
    this.versionNotes,
    this.accuracyScore,
    this.totalClassifications,
    this.correctClassifications,
    this.feedbackStatistics,
    this.performanceMetrics,
    this.createdAt,
    this.updatedAt,
  });

  factory AdaptiveContentClassificationConfig.fromJson(Map<String, dynamic> json) {
    return AdaptiveContentClassificationConfig(
      id: json['id'] as int?,
      name: json['name'] as String,
      description: json['description'] as String?,
      userId: json['userId'] as int,
      sessionId: json['sessionId'] as int?,
      categoryHierarchy: json['categoryHierarchy'] as String,
      classificationType: ClassificationType.values.firstWhere(
        (e) => e.name == json['classificationType'],
        orElse: () => ClassificationType.hierarchical,
      ),
      contentModality: ContentModality.values.firstWhere(
        (e) => e.name == json['contentModality'],
        orElse: () => ContentModality.textOnly,
      ),
      minConfidenceScore: json['minConfidenceScore'] as int? ?? 70,
      enableIncrementalLearning: json['enableIncrementalLearning'] as bool? ?? true,
      incrementalLearningBatchSize: json['incrementalLearningBatchSize'] as int? ?? 100,
      enableContextAwareness: json['enableContextAwareness'] as bool? ?? true,
      contextWindowSize: json['contextWindowSize'] as int? ?? 10,
      enableMultiLanguage: json['enableMultiLanguage'] as bool? ?? true,
      supportedLanguages: json['supportedLanguages'] as String?,
      enableAutoLabelRecommendation: json['enableAutoLabelRecommendation'] as bool? ?? true,
      maxLabelRecommendations: json['maxLabelRecommendations'] as int? ?? 5,
      enablePrivacyProtection: json['enablePrivacyProtection'] as bool? ?? true,
      privacyLevel: ClassificationPrivacyLevel.values.firstWhere(
        (e) => e.name == json['privacyLevel'],
        orElse: () => ClassificationPrivacyLevel.private,
      ),
      enableEvolutionTracking: json['enableEvolutionTracking'] as bool? ?? true,
      evolutionTrackingDepth: json['evolutionTrackingDepth'] as int? ?? 30,
      status: ClassificationStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => ClassificationStatus.draft,
      ),
      version: json['version'] as int?,
      versionNotes: json['versionNotes'] as String?,
      accuracyScore: (json['accuracyScore'] as num?)?.toDouble(),
      totalClassifications: json['totalClassifications'] as int?,
      correctClassifications: json['correctClassifications'] as int?,
      feedbackStatistics: json['feedbackStatistics'] as String?,
      performanceMetrics: json['performanceMetrics'] as String?,
      createdAt: json['createdAt'] != null 
          ? DateTime.parse(json['createdAt'] as String) 
          : null,
      updatedAt: json['updatedAt'] != null 
          ? DateTime.parse(json['updatedAt'] as String) 
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      'name': name,
      if (description != null) 'description': description,
      'userId': userId,
      if (sessionId != null) 'sessionId': sessionId,
      'categoryHierarchy': categoryHierarchy,
      'classificationType': classificationType.name,
      'contentModality': contentModality.name,
      'minConfidenceScore': minConfidenceScore,
      'enableIncrementalLearning': enableIncrementalLearning,
      'incrementalLearningBatchSize': incrementalLearningBatchSize,
      'enableContextAwareness': enableContextAwareness,
      'contextWindowSize': contextWindowSize,
      'enableMultiLanguage': enableMultiLanguage,
      if (supportedLanguages != null) 'supportedLanguages': supportedLanguages,
      'enableAutoLabelRecommendation': enableAutoLabelRecommendation,
      'maxLabelRecommendations': maxLabelRecommendations,
      'enablePrivacyProtection': enablePrivacyProtection,
      'privacyLevel': privacyLevel.name,
      'enableEvolutionTracking': enableEvolutionTracking,
      'evolutionTrackingDepth': evolutionTrackingDepth,
      'status': status.name,
      if (version != null) 'version': version,
      if (versionNotes != null) 'versionNotes': versionNotes,
      if (accuracyScore != null) 'accuracyScore': accuracyScore,
      if (totalClassifications != null) 'totalClassifications': totalClassifications,
      if (correctClassifications != null) 'correctClassifications': correctClassifications,
      if (feedbackStatistics != null) 'feedbackStatistics': feedbackStatistics,
      if (performanceMetrics != null) 'performanceMetrics': performanceMetrics,
      if (createdAt != null) 'createdAt': createdAt!.toIso8601String(),
      if (updatedAt != null) 'updatedAt': updatedAt!.toIso8601String(),
    };
  }

  /// 计算准确率
  double calculateAccuracy() {
    if (totalClassifications == null || totalClassifications! == 0) {
      return 0.0;
    }
    if (correctClassifications == null) {
      return 0.0;
    }
    return ((correctClassifications! / totalClassifications!) * 100 * 100).round() / 100;
  }

  /// 判断是否为活跃状态
  bool get isActive => status == ClassificationStatus.active;

  /// 判断是否启用增量学习
  bool get isIncrementalLearningEnabled => enableIncrementalLearning;

  /// 判断是否启用上下文感知
  bool get isContextAwarenessEnabled => enableContextAwareness;

  /// 判断是否启用多语言支持
  bool get isMultiLanguageEnabled => enableMultiLanguage;

  @override
  String toString() {
    return 'AdaptiveContentClassificationConfig(id: $id, name: $name, status: $status)';
  }
}

/// 分类结果模型
class ContentClassificationResult {
  final int? id;
  final int classificationConfigId;
  final int contentId;
  final ContentType contentType;
  final int userId;
  final int? sessionId;
  final String primaryCategory;
  final String? secondaryCategories;
  final int confidenceScore;
  final String? classificationEvidence;
  final bool isContextAware;
  final String? contextInformation;
  final bool isMultiModal;
  final String? multiModalAnalysis;
  final bool isAutoLabelRecommended;
  final String? recommendedLabels;
  final bool hasUserFeedback;
  final String? userFeedback;
  final bool isPrivacyProtected;
  final ClassificationPrivacyLevel privacyLevel;
  final bool isEvolutionTracked;
  final String? evolutionHistory;
  final int classificationVersion;
  final String? versionChanges;
  final double accuracyContribution;
  final bool isTrainingExample;
  final String? modelFeatures;
  final bool isAnomalyDetected;
  final String? anomalyDetails;
  final String languageCode;
  final String contentLanguage;
  final String? crossLanguageMapping;
  final DateTime contentCreatedAt;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  ContentClassificationResult({
    this.id,
    required this.classificationConfigId,
    required this.contentId,
    required this.contentType,
    required this.userId,
    this.sessionId,
    required this.primaryCategory,
    this.secondaryCategories,
    required this.confidenceScore,
    this.classificationEvidence,
    this.isContextAware = false,
    this.contextInformation,
    this.isMultiModal = false,
    this.multiModalAnalysis,
    this.isAutoLabelRecommended = false,
    this.recommendedLabels,
    this.hasUserFeedback = false,
    this.userFeedback,
    this.isPrivacyProtected = false,
    required this.privacyLevel,
    this.isEvolutionTracked = false,
    this.evolutionHistory,
    required this.classificationVersion,
    this.versionChanges,
    required this.accuracyContribution,
    required this.isTrainingExample,
    this.modelFeatures,
    required this.isAnomalyDetected,
    this.anomalyDetails,
    required this.languageCode,
    required this.contentLanguage,
    this.crossLanguageMapping,
    required this.contentCreatedAt,
    this.createdAt,
    this.updatedAt,
  });

  factory ContentClassificationResult.fromJson(Map<String, dynamic> json) {
    return ContentClassificationResult(
      id: json['id'] as int?,
      classificationConfigId: json['classificationConfigId'] as int,
      contentId: json['contentId'] as int,
      contentType: ContentType.values.firstWhere(
        (e) => e.name == json['contentType'],
        orElse: () => ContentType.textMessage,
      ),
      userId: json['userId'] as int,
      sessionId: json['sessionId'] as int?,
      primaryCategory: json['primaryCategory'] as String,
      secondaryCategories: json['secondaryCategories'] as String?,
      confidenceScore: json['confidenceScore'] as int,
      classificationEvidence: json['classificationEvidence'] as String?,
      isContextAware: json['isContextAware'] as bool? ?? false,
      contextInformation: json['contextInformation'] as String?,
      isMultiModal: json['isMultiModal'] as bool? ?? false,
      multiModalAnalysis: json['multiModalAnalysis'] as String?,
      isAutoLabelRecommended: json['isAutoLabelRecommended'] as bool? ?? false,
      recommendedLabels: json['recommendedLabels'] as String?,
      hasUserFeedback: json['hasUserFeedback'] as bool? ?? false,
      userFeedback: json['userFeedback'] as String?,
      isPrivacyProtected: json['isPrivacyProtected'] as bool? ?? false,
      privacyLevel: ClassificationPrivacyLevel.values.firstWhere(
        (e) => e.name == json['privacyLevel'],
        orElse: () => ClassificationPrivacyLevel.private,
      ),
      isEvolutionTracked: json['isEvolutionTracked'] as bool? ?? false,
      evolutionHistory: json['evolutionHistory'] as String?,
      classificationVersion: json['classificationVersion'] as int,
      versionChanges: json['versionChanges'] as String?,
      accuracyContribution: (json['accuracyContribution'] as num).toDouble(),
      isTrainingExample: json['isTrainingExample'] as bool,
      modelFeatures: json['modelFeatures'] as String?,
      isAnomalyDetected: json['isAnomalyDetected'] as bool,
      anomalyDetails: json['anomalyDetails'] as String?,
      languageCode: json['languageCode'] as String,
      contentLanguage: json['contentLanguage'] as String,
      crossLanguageMapping: json['crossLanguageMapping'] as String?,
      contentCreatedAt: DateTime.parse(json['contentCreatedAt'] as String),
      createdAt: json['createdAt'] != null 
          ? DateTime.parse(json['createdAt'] as String) 
          : null,
      updatedAt: json['updatedAt'] != null 
          ? DateTime.parse(json['updatedAt'] as String) 
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      'classificationConfigId': classificationConfigId,
      'contentId': contentId,
      'contentType': contentType.name,
      'userId': userId,
      if (sessionId != null) 'sessionId': sessionId,
      'primaryCategory': primaryCategory,
      if (secondaryCategories != null) 'secondaryCategories': secondaryCategories,
      'confidenceScore': confidenceScore,
      if (classificationEvidence != null) 'classificationEvidence': classificationEvidence,
      'isContextAware': isContextAware,
      if (contextInformation != null) 'contextInformation': contextInformation,
      'isMultiModal': isMultiModal,
      if (multiModalAnalysis != null) 'multiModalAnalysis': multiModalAnalysis,
      'isAutoLabelRecommended': isAutoLabelRecommended,
      if (recommendedLabels != null) 'recommendedLabels': recommendedLabels,
      'hasUserFeedback': hasUserFeedback,
      if (userFeedback != null) 'userFeedback': userFeedback,
      'isPrivacyProtected': isPrivacyProtected,
      'privacyLevel': privacyLevel.name,
      'isEvolutionTracked': isEvolutionTracked,
      if (evolutionHistory != null) 'evolutionHistory': evolutionHistory,
      'classificationVersion': classificationVersion,
      if (versionChanges != null) 'versionChanges': versionChanges,
      'accuracyContribution': accuracyContribution,
      'isTrainingExample': isTrainingExample,
      if (modelFeatures != null) 'modelFeatures': modelFeatures,
      'isAnomalyDetected': isAnomalyDetected,
      if (anomalyDetails != null) 'anomalyDetails': anomalyDetails,
      'languageCode': languageCode,
      'contentLanguage': contentLanguage,
      if (crossLanguageMapping != null) 'crossLanguageMapping': crossLanguageMapping,
      'contentCreatedAt': contentCreatedAt.toIso8601String(),
      if (createdAt != null) 'createdAt': createdAt!.toIso8601String(),
      if (updatedAt != null) 'updatedAt': updatedAt!.toIso8601String(),
    };
  }

  /// 获取置信度级别
  String get confidenceLevel {
    if (confidenceScore >= 90) return '很高';
    if (confidenceScore >= 80) return '高';
    if (confidenceScore >= 70) return '中';
    if (confidenceScore >= 60) return '低';
    return '很低';
  }

  /// 判断是否为高置信度
  bool get isHighConfidence => confidenceScore >= 80;

  /// 判断是否为低置信度
  bool get isLowConfidence => confidenceScore < 60;

  /// 解析推荐的标签列表
  List<String> getRecommendedLabelsList() {
    if (recommendedLabels == null) return [];
    try {
      final List<dynamic> list = json.decode(recommendedLabels!);
      return list.map((e) => e.toString()).toList();
    } catch (e) {
      return [];
    }
  }

  /// 解析次要类别列表
  List<String> getSecondaryCategoriesList() {
    if (secondaryCategories == null) return [];
    try {
      final List<dynamic> list = json.decode(secondaryCategories!);
      return list.map((e) => e.toString()).toList();
    } catch (e) {
      return [];
    }
  }

  @override
  String toString() {
    return 'ContentClassificationResult(id: $id, primaryCategory: $primaryCategory, confidence: $confidenceScore)';
  }
}

/// 配置统计模型
class ConfigStats {
  final int totalConfigs;
  final int activeConfigs;
  final Map<String, int> typeDistribution;
  final Map<String, int> modalityDistribution;
  final double averageAccuracy;
  final int totalClassifications;

  ConfigStats({
    required this.totalConfigs,
    required this.activeConfigs,
    required this.typeDistribution,
    required this.modalityDistribution,
    required this.averageAccuracy,
    required this.totalClassifications,
  });

  factory ConfigStats.fromJson(Map<String, dynamic> json) {
    return ConfigStats(
      totalConfigs: json['totalConfigs'] as int,
      activeConfigs: json['activeConfigs'] as int,
      typeDistribution: (json['typeDistribution'] as Map<String, dynamic>?)?.map(
        (k, v) => MapEntry(k, v as int),
      ) ?? {},
      modalityDistribution: (json['modalityDistribution'] as Map<String, dynamic>?)?.map(
        (k, v) => MapEntry(k, v as int),
      ) ?? {},
      averageAccuracy: (json['averageAccuracy'] as num?)?.toDouble() ?? 0.0,
      totalClassifications: json['totalClassifications'] as int? ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'totalConfigs': totalConfigs,
      'activeConfigs': activeConfigs,
      'typeDistribution': typeDistribution,
      'modalityDistribution': modalityDistribution,
      'averageAccuracy': averageAccuracy,
      'totalClassifications': totalClassifications,
    };
  }
}

/// 分类统计模型
class ClassificationStats {
  final int totalResults;
  final Map<String, int> confidenceDistribution;
  final Map<String, int> categoryDistribution;
  final Map<String, int> contentTypeDistribution;
  final double averageConfidence;
  final double highConfidencePercentage;

  ClassificationStats({
    required this.totalResults,
    required this.confidenceDistribution,
    required this.categoryDistribution,
    required this.contentTypeDistribution,
    required this.averageConfidence,
    required this.highConfidencePercentage,
  });

  factory ClassificationStats.fromJson(Map<String, dynamic> json) {
    return ClassificationStats(
      totalResults: json['totalResults'] as int,
      confidenceDistribution: (json['confidenceDistribution'] as Map<String, dynamic>?)?.map(
        (k, v) => MapEntry(k, v as int),
      ) ?? {},
      categoryDistribution: (json['categoryDistribution'] as Map<String, dynamic>?)?.map(
        (k, v) => MapEntry(k, v as int),
      ) ?? {},
      contentTypeDistribution: (json['contentTypeDistribution'] as Map<String, dynamic>?)?.map(
        (k, v) => MapEntry(k, v as int),
      ) ?? {},
      averageConfidence: (json['averageConfidence'] as num?)?.toDouble() ?? 0.0,
      highConfidencePercentage: (json['highConfidencePercentage'] as num?)?.toDouble() ?? 0.0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'totalResults': totalResults,
      'confidenceDistribution': confidenceDistribution,
      'categoryDistribution': categoryDistribution,
      'contentTypeDistribution': contentTypeDistribution,
      'averageConfidence': averageConfidence,
      'highConfidencePercentage': highConfidencePercentage,
    };
  }
}

/// 分类趋势模型
class ClassificationTrend {
  final Map<String, int> dailyCounts;
  final Map<String, double> dailyAvgConfidence;
  final int totalDays;
  final int totalClassifications;

  ClassificationTrend({
    required this.dailyCounts,
    required this.dailyAvgConfidence,
    required this.totalDays,
    required this.totalClassifications,
  });

  factory ClassificationTrend.fromJson(Map<String, dynamic> json) {
    return ClassificationTrend(
      dailyCounts: (json['dailyCounts'] as Map<String, dynamic>?)?.map(
        (k, v) => MapEntry(k, v as int),
      ) ?? {},
      dailyAvgConfidence: (json['dailyAvgConfidence'] as Map<String, dynamic>?)?.map(
        (k, v) => MapEntry(k, (v as num).toDouble()),
      ) ?? {},
      totalDays: json['totalDays'] as int,
      totalClassifications: json['totalClassifications'] as int,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'dailyCounts': dailyCounts,
      'dailyAvgConfidence': dailyAvgConfidence,
      'totalDays': totalDays,
      'totalClassifications': totalClassifications,
    };
  }
}