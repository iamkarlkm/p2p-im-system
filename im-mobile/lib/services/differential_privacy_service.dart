import 'dart:convert';
import 'package:http/http.dart' as http;

/// 差分隐私配置模型
class DifferentialPrivacyConfig {
  final int? id;
  final String configKey;
  final String? configValue;
  final String? description;
  final String dataType;
  final bool? isSensitive;
  final double? privacyBudgetLimit;
  final double? epsilon;
  final double? delta;
  final String? noiseMechanism;
  final DateTime? createdAt;
  final DateTime? updatedAt;
  final String? createdBy;
  final String? updatedBy;
  final bool? isActive;
  final int? version;
  final bool? requiresApproval;
  final String? approvalStatus;

  DifferentialPrivacyConfig({
    this.id,
    required this.configKey,
    this.configValue,
    this.description,
    required this.dataType,
    this.isSensitive,
    this.privacyBudgetLimit,
    this.epsilon,
    this.delta,
    this.noiseMechanism,
    this.createdAt,
    this.updatedAt,
    this.createdBy,
    this.updatedBy,
    this.isActive,
    this.version,
    this.requiresApproval,
    this.approvalStatus,
  });

  factory DifferentialPrivacyConfig.fromJson(Map<String, dynamic> json) {
    return DifferentialPrivacyConfig(
      id: json['id'] as int?,
      configKey: json['configKey'] as String,
      configValue: json['configValue'] as String?,
      description: json['description'] as String?,
      dataType: json['dataType'] as String,
      isSensitive: json['isSensitive'] as bool?,
      privacyBudgetLimit: json['privacyBudgetLimit'] as double?,
      epsilon: json['epsilon'] as double?,
      delta: json['delta'] as double?,
      noiseMechanism: json['noiseMechanism'] as String?,
      createdAt: json['createdAt'] != null 
          ? DateTime.parse(json['createdAt'] as String) 
          : null,
      updatedAt: json['updatedAt'] != null 
          ? DateTime.parse(json['updatedAt'] as String) 
          : null,
      createdBy: json['createdBy'] as String?,
      updatedBy: json['updatedBy'] as String?,
      isActive: json['isActive'] as bool?,
      version: json['version'] as int?,
      requiresApproval: json['requiresApproval'] as bool?,
      approvalStatus: json['approvalStatus'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'configKey': configKey,
      'configValue': configValue,
      'description': description,
      'dataType': dataType,
      'isSensitive': isSensitive,
      'privacyBudgetLimit': privacyBudgetLimit,
      'epsilon': epsilon,
      'delta': delta,
      'noiseMechanism': noiseMechanism,
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
      'createdBy': createdBy,
      'updatedBy': updatedBy,
      'isActive': isActive,
      'version': version,
      'requiresApproval': requiresApproval,
      'approvalStatus': approvalStatus,
    };
  }
}

/// 隐私预算模型
class PrivacyBudget {
  final int? id;
  final String userId;
  final String? sessionId;
  final String budgetType;
  final double totalBudget;
  final double? consumedBudget;
  final double? remainingBudget;
  final String budgetPeriod;
  final DateTime periodStart;
  final DateTime periodEnd;
  final DateTime? lastConsumedAt;
  final int? consumptionCount;
  final double? avgEpsilonPerConsumption;
  final double? maxEpsilonPerConsumption;
  final int? violationCount;
  final double? warningThreshold;
  final double? blockThreshold;
  final bool? isBlocked;
  final String? blockReason;
  final DateTime? createdAt;
  final DateTime? updatedAt;
  final String? metadataJson;

  PrivacyBudget({
    this.id,
    required this.userId,
    this.sessionId,
    required this.budgetType,
    required this.totalBudget,
    this.consumedBudget,
    this.remainingBudget,
    required this.budgetPeriod,
    required this.periodStart,
    required this.periodEnd,
    this.lastConsumedAt,
    this.consumptionCount,
    this.avgEpsilonPerConsumption,
    this.maxEpsilonPerConsumption,
    this.violationCount,
    this.warningThreshold,
    this.blockThreshold,
    this.isBlocked,
    this.blockReason,
    this.createdAt,
    this.updatedAt,
    this.metadataJson,
  });

  factory PrivacyBudget.fromJson(Map<String, dynamic> json) {
    return PrivacyBudget(
      id: json['id'] as int?,
      userId: json['userId'] as String,
      sessionId: json['sessionId'] as String?,
      budgetType: json['budgetType'] as String,
      totalBudget: (json['totalBudget'] as num).toDouble(),
      consumedBudget: (json['consumedBudget'] as num?)?.toDouble(),
      remainingBudget: (json['remainingBudget'] as num?)?.toDouble(),
      budgetPeriod: json['budgetPeriod'] as String,
      periodStart: DateTime.parse(json['periodStart'] as String),
      periodEnd: DateTime.parse(json['periodEnd'] as String),
      lastConsumedAt: json['lastConsumedAt'] != null 
          ? DateTime.parse(json['lastConsumedAt'] as String) 
          : null,
      consumptionCount: json['consumptionCount'] as int?,
      avgEpsilonPerConsumption: (json['avgEpsilonPerConsumption'] as num?)?.toDouble(),
      maxEpsilonPerConsumption: (json['maxEpsilonPerConsumption'] as num?)?.toDouble(),
      violationCount: json['violationCount'] as int?,
      warningThreshold: (json['warningThreshold'] as num?)?.toDouble(),
      blockThreshold: (json['blockThreshold'] as num?)?.toDouble(),
      isBlocked: json['isBlocked'] as bool?,
      blockReason: json['blockReason'] as String?,
      createdAt: json['createdAt'] != null 
          ? DateTime.parse(json['createdAt'] as String) 
          : null,
      updatedAt: json['updatedAt'] != null 
          ? DateTime.parse(json['updatedAt'] as String) 
          : null,
      metadataJson: json['metadataJson'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'sessionId': sessionId,
      'budgetType': budgetType,
      'totalBudget': totalBudget,
      'consumedBudget': consumedBudget,
      'remainingBudget': remainingBudget,
      'budgetPeriod': budgetPeriod,
      'periodStart': periodStart.toIso8601String(),
      'periodEnd': periodEnd.toIso8601String(),
      'lastConsumedAt': lastConsumedAt?.toIso8601String(),
      'consumptionCount': consumptionCount,
      'avgEpsilonPerConsumption': avgEpsilonPerConsumption,
      'maxEpsilonPerConsumption': maxEpsilonPerConsumption,
      'violationCount': violationCount,
      'warningThreshold': warningThreshold,
      'blockThreshold': blockThreshold,
      'isBlocked': isBlocked,
      'blockReason': blockReason,
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
      'metadataJson': metadataJson,
    };
  }

  /// 检查是否可以消耗预算
  bool canConsume(double epsilon) {
    if (isBlocked == true) return false;
    return (consumedBudget ?? 0) + epsilon <= totalBudget;
  }
}

/// 隐私影响评估模型
class PrivacyImpact {
  final int? id;
  final String operationId;
  final String operationType;
  final String? userId;
  final String? sessionId;
  final String? aiModelId;
  final String? datasetId;
  final double epsilonConsumed;
  final double? deltaConsumed;
  final double? privacyBudgetBefore;
  final double? privacyBudgetAfter;
  final double? dataSensitivityScore;
  final String? impactSeverity;
  final String? riskLevel;
  final String? mitigationMeasuresApplied;
  final bool? complianceCheckPassed;
  final double? dataMinimizationScore;
  final double? purposeLimitationScore;
  final double? storageLimitationScore;
  final double? integrityConfidentialityScore;
  final double? accountabilityScore;
  final double? overallImpactScore;
  final String? recommendationsJson;
  final String? auditTrailJson;
  final DateTime? createdAt;
  final DateTime? completedAt;
  final int? processingTimeMs;
  final bool? processingSuccess;
  final String? errorMessage;
  final bool? userConsentObtained;
  final DateTime? consentTimestamp;
  final int? dataRetentionPeriodDays;
  final double? reidentificationRiskScore;
  final double? inferenceAttackRiskScore;
  final double? membershipInferenceRiskScore;
  final double? attributeInferenceRiskScore;
  final double? overallRiskScore;

  PrivacyImpact({
    this.id,
    required this.operationId,
    required this.operationType,
    this.userId,
    this.sessionId,
    this.aiModelId,
    this.datasetId,
    required this.epsilonConsumed,
    this.deltaConsumed,
    this.privacyBudgetBefore,
    this.privacyBudgetAfter,
    this.dataSensitivityScore,
    this.impactSeverity,
    this.riskLevel,
    this.mitigationMeasuresApplied,
    this.complianceCheckPassed,
    this.dataMinimizationScore,
    this.purposeLimitationScore,
    this.storageLimitationScore,
    this.integrityConfidentialityScore,
    this.accountabilityScore,
    this.overallImpactScore,
    this.recommendationsJson,
    this.auditTrailJson,
    this.createdAt,
    this.completedAt,
    this.processingTimeMs,
    this.processingSuccess,
    this.errorMessage,
    this.userConsentObtained,
    this.consentTimestamp,
    this.dataRetentionPeriodDays,
    this.reidentificationRiskScore,
    this.inferenceAttackRiskScore,
    this.membershipInferenceRiskScore,
    this.attributeInferenceRiskScore,
    this.overallRiskScore,
  });

  factory PrivacyImpact.fromJson(Map<String, dynamic> json) {
    return PrivacyImpact(
      id: json['id'] as int?,
      operationId: json['operationId'] as String,
      operationType: json['operationType'] as String,
      userId: json['userId'] as String?,
      sessionId: json['sessionId'] as String?,
      aiModelId: json['aiModelId'] as String?,
      datasetId: json['datasetId'] as String?,
      epsilonConsumed: (json['epsilonConsumed'] as num).toDouble(),
      deltaConsumed: (json['deltaConsumed'] as num?)?.toDouble(),
      privacyBudgetBefore: (json['privacyBudgetBefore'] as num?)?.toDouble(),
      privacyBudgetAfter: (json['privacyBudgetAfter'] as num?)?.toDouble(),
      dataSensitivityScore: (json['dataSensitivityScore'] as num?)?.toDouble(),
      impactSeverity: json['impactSeverity'] as String?,
      riskLevel: json['riskLevel'] as String?,
      mitigationMeasuresApplied: json['mitigationMeasuresApplied'] as String?,
      complianceCheckPassed: json['complianceCheckPassed'] as bool?,
      dataMinimizationScore: (json['dataMinimizationScore'] as num?)?.toDouble(),
      purposeLimitationScore: (json['purposeLimitationScore'] as num?)?.toDouble(),
      storageLimitationScore: (json['storageLimitationScore'] as num?)?.toDouble(),
      integrityConfidentialityScore: (json['integrityConfidentialityScore'] as num?)?.toDouble(),
      accountabilityScore: (json['accountabilityScore'] as num?)?.toDouble(),
      overallImpactScore: (json['overallImpactScore'] as num?)?.toDouble(),
      recommendationsJson: json['recommendationsJson'] as String?,
      auditTrailJson: json['auditTrailJson'] as String?,
      createdAt: json['createdAt'] != null 
          ? DateTime.parse(json['createdAt'] as String) 
          : null,
      completedAt: json['completedAt'] != null 
          ? DateTime.parse(json['completedAt'] as String) 
          : null,
      processingTimeMs: json['processingTimeMs'] as int?,
      processingSuccess: json['processingSuccess'] as bool?,
      errorMessage: json['errorMessage'] as String?,
      userConsentObtained: json['userConsentObtained'] as bool?,
      consentTimestamp: json['consentTimestamp'] != null 
          ? DateTime.parse(json['consentTimestamp'] as String) 
          : null,
      dataRetentionPeriodDays: json['dataRetentionPeriodDays'] as int?,
      reidentificationRiskScore: (json['reidentificationRiskScore'] as num?)?.toDouble(),
      inferenceAttackRiskScore: (json['inferenceAttackRiskScore'] as num?)?.toDouble(),
      membershipInferenceRiskScore: (json['membershipInferenceRiskScore'] as num?)?.toDouble(),
      attributeInferenceRiskScore: (json['attributeInferenceRiskScore'] as num?)?.toDouble(),
      overallRiskScore: (json['overallRiskScore'] as num?)?.toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'operationId': operationId,
      'operationType': operationType,
      'userId': userId,
      'sessionId': sessionId,
      'aiModelId': aiModelId,
      'datasetId': datasetId,
      'epsilonConsumed': epsilonConsumed,
      'deltaConsumed': deltaConsumed,
      'privacyBudgetBefore': privacyBudgetBefore,
      'privacyBudgetAfter': privacyBudgetAfter,
      'dataSensitivityScore': dataSensitivityScore,
      'impactSeverity': impactSeverity,
      'riskLevel': riskLevel,
      'mitigationMeasuresApplied': mitigationMeasuresApplied,
      'complianceCheckPassed': complianceCheckPassed,
      'dataMinimizationScore': dataMinimizationScore,
      'purposeLimitationScore': purposeLimitationScore,
      'storageLimitationScore': storageLimitationScore,
      'integrityConfidentialityScore': integrityConfidentialityScore,
      'accountabilityScore': accountabilityScore,
      'overallImpactScore': overallImpactScore,
      'recommendationsJson': recommendationsJson,
      'auditTrailJson': auditTrailJson,
      'createdAt': createdAt?.toIso8601String(),
      'completedAt': completedAt?.toIso8601String(),
      'processingTimeMs': processingTimeMs,
      'processingSuccess': processingSuccess,
      'errorMessage': errorMessage,
      'userConsentObtained': userConsentObtained,
      'consentTimestamp': consentTimestamp?.toIso8601String(),
      'dataRetentionPeriodDays': dataRetentionPeriodDays,
      'reidentificationRiskScore': reidentificationRiskScore,
      'inferenceAttackRiskScore': inferenceAttackRiskScore,
      'membershipInferenceRiskScore': membershipInferenceRiskScore,
      'attributeInferenceRiskScore': attributeInferenceRiskScore,
      'overallRiskScore': overallRiskScore,
    };
  }
}