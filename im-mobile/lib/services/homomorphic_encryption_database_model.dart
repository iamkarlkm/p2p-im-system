/**
 * 同态加密数据库 Dart 模型
 * 支持全同态加密（BGV、BFV、CKKS）、加密 SQL 查询引擎、隐私保护查询
 */

// 加密方案枚举
enum EncryptionScheme { bgv, bfv, ckks, paillier, elgamal, rsaHomomorphic }

// 安全级别枚举
enum SecurityLevel { low, medium, high, veryHigh, military }

// 数据库类型枚举
enum DatabaseType { message, userProfile, group, fileMetadata, auditLog }

// 查询类型枚举
enum QueryType { select, insert, update, delete, aggregate, join, range, fuzzy, spatial, temporal }

// 查询复杂度枚举
enum QueryComplexity { simple, moderate, complex, veryComplex, extreme }

// 隐私级别枚举
enum PrivacyLevel { public, low, medium, high, veryHigh, confidential, secret }

// 加密方法枚举
enum EncryptionMethod { homomorphic, orderPreserving, deterministic, searchable, randomized }

// 查询状态枚举
enum QueryStatus {
  pending,
  executing,
  encrypting,
  decrypting,
  compressing,
  success,
  failed,
  timeout,
  cancelled,
  partialSuccess
}

// 数据库状态枚举
enum DatabaseStatus { active, inactive, maintenance, archived, deleted }

/// 同态加密数据库模型类
class HomomorphicEncryptionDatabase {
  final int databaseId;
  final int userId;
  final String? sessionId;
  final String databaseName;
  final DatabaseType databaseType;
  final EncryptionScheme encryptionScheme;
  final SecurityLevel securityLevel;
  final int keySize;
  final int modulusSize;
  final int plaintextModulus;
  final int noiseBudget;
  final String publicKeyHash;
  final String secretKeyHash;
  final int encryptedDataCount;
  final int totalDataSizeBytes;
  final double encryptionTimeAvgMs;
  final double decryptionTimeAvgMs;
  final double homomorphicOpTimeAvgMs;
  final bool compressionEnabled;
  final String? compressionAlgorithm;
  final double compressionRatio;
  final bool indexingEnabled;
  final String? indexType;
  final int indexCount;
  final bool queryCacheEnabled;
  final double cacheHitRate;
  final bool parallelismEnabled;
  final int maxParallelThreads;
  final bool hardwareAcceleration;
  final String? acceleratorType;
  final double privacyBudget;
  final double privacyBudgetConsumed;
  final bool differentialPrivacyEnabled;
  final double dpEpsilon;
  final double dpDelta;
  final int maxQueryComplexity;
  final DatabaseStatus status;
  final DateTime createdTime;
  final DateTime updatedTime;
  final DateTime? lastAccessedTime;
  final double healthScore;
  final double securityScore;
  final double privacyScore;
  final double costEstimateUsdPerMonth;

  HomomorphicEncryptionDatabase({
    required this.databaseId,
    required this.userId,
    this.sessionId,
    required this.databaseName,
    required this.databaseType,
    required this.encryptionScheme,
    required this.securityLevel,
    required this.keySize,
    required this.modulusSize,
    required this.plaintextModulus,
    required this.noiseBudget,
    required this.publicKeyHash,
    required this.secretKeyHash,
    required this.encryptedDataCount,
    required this.totalDataSizeBytes,
    required this.encryptionTimeAvgMs,
    required this.decryptionTimeAvgMs,
    required this.homomorphicOpTimeAvgMs,
    required this.compressionEnabled,
    this.compressionAlgorithm,
    required this.compressionRatio,
    required this.indexingEnabled,
    this.indexType,
    required this.indexCount,
    required this.queryCacheEnabled,
    required this.cacheHitRate,
    required this.parallelismEnabled,
    required this.maxParallelThreads,
    required this.hardwareAcceleration,
    this.acceleratorType,
    required this.privacyBudget,
    required this.privacyBudgetConsumed,
    required this.differentialPrivacyEnabled,
    required this.dpEpsilon,
    required this.dpDelta,
    required this.maxQueryComplexity,
    required this.status,
    required this.createdTime,
    required this.updatedTime,
    this.lastAccessedTime,
    required this.healthScore,
    required this.securityScore,
    required this.privacyScore,
    required this.costEstimateUsdPerMonth,
  });

  factory HomomorphicEncryptionDatabase.fromJson(Map<String, dynamic> json) {
    return HomomorphicEncryptionDatabase(
      databaseId: json['databaseId'] ?? 0,
      userId: json['userId'] ?? 0,
      sessionId: json['sessionId'],
      databaseName: json['databaseName'] ?? '',
      databaseType: DatabaseType.values.firstWhere(
        (e) => e.toString().split('.').last == json['databaseType'],
        orElse: () => DatabaseType.message,
      ),
      encryptionScheme: EncryptionScheme.values.firstWhere(
        (e) => e.toString().split('.').last.toLowerCase() == (json['encryptionScheme'] ?? '').toLowerCase(),
        orElse: () => EncryptionScheme.ckks,
      ),
      securityLevel: SecurityLevel.values.firstWhere(
        (e) => e.toString().split('.').last == json['securityLevel'],
        orElse: () => SecurityLevel.high,
      ),
      keySize: json['keySize'] ?? 4096,
      modulusSize: json['modulusSize'] ?? 16384,
      plaintextModulus: json['plaintextModulus'] ?? 65537,
      noiseBudget: json['noiseBudget'] ?? 100,
      publicKeyHash: json['publicKeyHash'] ?? '',
      secretKeyHash: json['secretKeyHash'] ?? '',
      encryptedDataCount: json['encryptedDataCount'] ?? 0,
      totalDataSizeBytes: json['totalDataSizeBytes'] ?? 0,
      encryptionTimeAvgMs: json['encryptionTimeAvgMs'] ?? 0.0,
      decryptionTimeAvgMs: json['decryptionTimeAvgMs'] ?? 0.0,
      homomorphicOpTimeAvgMs: json['homomorphicOpTimeAvgMs'] ?? 0.0,
      compressionEnabled: json['compressionEnabled'] ?? false,
      compressionAlgorithm: json['compressionAlgorithm'],
      compressionRatio: json['compressionRatio'] ?? 1.0,
      indexingEnabled: json['indexingEnabled'] ?? false,
      indexType: json['indexType'],
      indexCount: json['indexCount'] ?? 0,
      queryCacheEnabled: json['queryCacheEnabled'] ?? false,
      cacheHitRate: json['cacheHitRate'] ?? 0.0,
      parallelismEnabled: json['parallelismEnabled'] ?? false,
      maxParallelThreads: json['maxParallelThreads'] ?? 1,
      hardwareAcceleration: json['hardwareAcceleration'] ?? false,
      acceleratorType: json['acceleratorType'],
      privacyBudget: json['privacyBudget'] ?? 100.0,
      privacyBudgetConsumed: json['privacyBudgetConsumed'] ?? 0.0,
      differentialPrivacyEnabled: json['differentialPrivacyEnabled'] ?? false,
      dpEpsilon: json['dpEpsilon'] ?? 1.0,
      dpDelta: json['dpDelta'] ?? 0.00001,
      maxQueryComplexity: json['maxQueryComplexity'] ?? 100,
      status: DatabaseStatus.values.firstWhere(
        (e) => e.toString().split('.').last == json['status'],
        orElse: () => DatabaseStatus.active,
      ),
      createdTime: DateTime.parse(json['createdTime'] ?? DateTime.now().toIso8601String()),
      updatedTime: DateTime.parse(json['updatedTime'] ?? DateTime.now().toIso8601String()),
      lastAccessedTime: json['lastAccessedTime'] != null ? DateTime.parse(json['lastAccessedTime']) : null,
      healthScore: json['healthScore'] ?? 100.0,
      securityScore: json['securityScore'] ?? 100.0,
      privacyScore: json['privacyScore'] ?? 100.0,
      costEstimateUsdPerMonth: json['costEstimateUsdPerMonth'] ?? 0.0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'databaseId': databaseId,
      'userId': userId,
      'sessionId': sessionId,
      'databaseName': databaseName,
      'databaseType': databaseType.toString().split('.').last,
      'encryptionScheme': encryptionScheme.toString().split('.').last,
      'securityLevel': securityLevel.toString().split('.').last,
      'keySize': keySize,
      'modulusSize': modulusSize,
      'plaintextModulus': plaintextModulus,
      'noiseBudget': noiseBudget,
      'publicKeyHash': publicKeyHash,
      'secretKeyHash': secretKeyHash,
      'encryptedDataCount': encryptedDataCount,
      'totalDataSizeBytes': totalDataSizeBytes,
      'encryptionTimeAvgMs': encryptionTimeAvgMs,
      'decryptionTimeAvgMs': decryptionTimeAvgMs,
      'homomorphicOpTimeAvgMs': homomorphicOpTimeAvgMs,
      'compressionEnabled': compressionEnabled,
      'compressionAlgorithm': compressionAlgorithm,
      'compressionRatio': compressionRatio,
      'indexingEnabled': indexingEnabled,
      'indexType': indexType,
      'indexCount': indexCount,
      'queryCacheEnabled': queryCacheEnabled,
      'cacheHitRate': cacheHitRate,
      'parallelismEnabled': parallelismEnabled,
      'maxParallelThreads': maxParallelThreads,
      'hardwareAcceleration': hardwareAcceleration,
      'acceleratorType': acceleratorType,
      'privacyBudget': privacyBudget,
      'privacyBudgetConsumed': privacyBudgetConsumed,
      'differentialPrivacyEnabled': differentialPrivacyEnabled,
      'dpEpsilon': dpEpsilon,
      'dpDelta': dpDelta,
      'maxQueryComplexity': maxQueryComplexity,
      'status': status.toString().split('.').last,
      'createdTime': createdTime.toIso8601String(),
      'updatedTime': updatedTime.toIso8601String(),
      'lastAccessedTime': lastAccessedTime?.toIso8601String(),
      'healthScore': healthScore,
      'securityScore': securityScore,
      'privacyScore': privacyScore,
      'costEstimateUsdPerMonth': costEstimateUsdPerMonth,
    };
  }

  @override
  String toString() {
    return 'HomomorphicEncryptionDatabase{databaseId: $databaseId, databaseName: $databaseName, status: $status, healthScore: $healthScore, privacyScore: $privacyScore}';
  }
}

/// 隐私保护查询模型类
class PrivacyPreservingQuery {
  final int queryId;
  final int databaseId;
  final int userId;
  final String? sessionId;
  final String queryUuid;
  final QueryType queryType;
  final QueryComplexity queryComplexity;
  final String querySql;
  final String? encryptedQuerySql;
  final String? queryParameters;
  final String? encryptedQueryParameters;
  final PrivacyLevel privacyLevel;
  final EncryptionMethod encryptionMethod;
  final String? encryptionKeyHash;
  final bool resultEncryptionEnabled;
  final String? resultEncryptionMethod;
  final double privacyBudgetConsumed;
  final bool differentialPrivacyEnabled;
  final double dpEpsilonConsumed;
  final double dpDeltaConsumed;
  final double noiseAddedAmount;
  final String? noiseDistribution;
  final bool queryOptimizationEnabled;
  final String? optimizationStrategy;
  final bool parallelExecutionEnabled;
  final int parallelDegree;
  final bool cacheEnabled;
  final bool cacheHit;
  final int cacheTtlSeconds;
  final bool auditTrailEnabled;
  final bool accessControlEnforced;
  final bool complianceCheckEnabled;
  final String? complianceStatus;
  final int resultRowCount;
  final int resultDataSizeBytes;
  final int encryptedResultDataSizeBytes;
  final bool compressionEnabled;
  final double compressionRatio;
  final String? compressionAlgorithm;
  final int queryExecutionTimeMs;
  final int encryptionTimeMs;
  final int decryptionTimeMs;
  final int networkTransferTimeMs;
  final int totalLatencyMs;
  final double cpuUsagePercent;
  final int memoryUsageBytes;
  final int diskIoBytes;
  final int networkIoBytes;
  final double costEstimateUsd;
  final bool errorOccurred;
  final String? errorMessage;
  final String? errorStackTrace;
  final int retryCount;
  final int maxRetries;
  final QueryStatus queryStatus;
  final bool resultVerificationEnabled;
  final bool verificationResult;
  final String? verificationMethod;
  final String? verificationProof;
  final int verificationTimeMs;
  final DateTime createdTime;
  final DateTime? startTime;
  final DateTime? endTime;
  final DateTime lastUpdatedTime;
  final DateTime? expirationTime;
  final double performanceScore;
  final double privacyScore;
  final double accuracyScore;
  final double complianceScore;
  final double userSatisfactionScore;

  PrivacyPreservingQuery({
    required this.queryId,
    required this.databaseId,
    required this.userId,
    this.sessionId,
    required this.queryUuid,
    required this.queryType,
    required this.queryComplexity,
    required this.querySql,
    this.encryptedQuerySql,
    this.queryParameters,
    this.encryptedQueryParameters,
    required this.privacyLevel,
    required this.encryptionMethod,
    this.encryptionKeyHash,
    required this.resultEncryptionEnabled,
    this.resultEncryptionMethod,
    required this.privacyBudgetConsumed,
    required this.differentialPrivacyEnabled,
    required this.dpEpsilonConsumed,
    required this.dpDeltaConsumed,
    required this.noiseAddedAmount,
    this.noiseDistribution,
    required this.queryOptimizationEnabled,
    this.optimizationStrategy,
    required this.parallelExecutionEnabled,
    required this.parallelDegree,
    required this.cacheEnabled,
    required this.cacheHit,
    required this.cacheTtlSeconds,
    required this.auditTrailEnabled,
    required this.accessControlEnforced,
    required this.complianceCheckEnabled,
    this.complianceStatus,
    required this.resultRowCount,
    required this.resultDataSizeBytes,
    required this.encryptedResultDataSizeBytes,
    required this.compressionEnabled,
    required this.compressionRatio,
    this.compressionAlgorithm,
    required this.queryExecutionTimeMs,
    required this.encryptionTimeMs,
    required this.decryptionTimeMs,
    required this.networkTransferTimeMs,
    required this.totalLatencyMs,
    required this.cpuUsagePercent,
    required this.memoryUsageBytes,
    required this.diskIoBytes,
    required this.networkIoBytes,
    required this.costEstimateUsd,
    required this.errorOccurred,
    this.errorMessage,
    this.errorStackTrace,
    required this.retryCount,
    required this.maxRetries,
    required this.queryStatus,
    required this.resultVerificationEnabled,
    required this.verificationResult,
    this.verificationMethod,
    this.verificationProof,
    required this.verificationTimeMs,
    required this.createdTime,
    this.startTime,
    this.endTime,
    required this.lastUpdatedTime,
    this.expirationTime,
    required this.performanceScore,
    required this.privacyScore,
    required this.accuracyScore,
    required this.complianceScore,
    required this.userSatisfactionScore,
  });

  factory PrivacyPreservingQuery.fromJson(Map<String, dynamic> json) {
    return PrivacyPreservingQuery(
      queryId: json['queryId'] ?? 0,
      databaseId: json['databaseId'] ?? 0,
      userId: json['userId'] ?? 0,
      sessionId: json['sessionId'],
      queryUuid: json['queryUuid'] ?? '',
      queryType: QueryType.values.firstWhere(
        (e) => e.toString().split('.').last == json['queryType'],
        orElse: () => QueryType.select,
      ),
      queryComplexity: QueryComplexity.values.firstWhere(
        (e) => e.toString().split('.').last == json['queryComplexity'],
        orElse: () => QueryComplexity.moderate,
      ),
      querySql: json['querySql'] ?? '',
      encryptedQuerySql: json['encryptedQuerySql'],
      queryParameters: json['queryParameters'],
      encryptedQueryParameters: json['encryptedQueryParameters'],
      privacyLevel: PrivacyLevel.values.firstWhere(
        (e) => e.toString().split('.').last == json['privacyLevel'],
        orElse: () => PrivacyLevel.high,
      ),
      encryptionMethod: EncryptionMethod.values.firstWhere(
        (e) => e.toString().split('.').last == json['encryptionMethod'],
        orElse: () => EncryptionMethod.homomorphic,
      ),
      encryptionKeyHash: json['encryptionKeyHash'],
      resultEncryptionEnabled: json['resultEncryptionEnabled'] ?? false,
      resultEncryptionMethod: json['resultEncryptionMethod'],
      privacyBudgetConsumed: json['privacyBudgetConsumed'] ?? 0.0,
      differentialPrivacyEnabled: json['differentialPrivacyEnabled'] ?? false,
      dpEpsilonConsumed: json['dpEpsilonConsumed'] ?? 0.0,
      dpDeltaConsumed: json['dpDeltaConsumed'] ?? 0.0,
      noiseAddedAmount: json['noiseAddedAmount'] ?? 0.0,
      noiseDistribution: json['noiseDistribution'],
      queryOptimizationEnabled: json['queryOptimizationEnabled'] ?? false,
      optimizationStrategy: json['optimizationStrategy'],
      parallelExecutionEnabled: json['parallelExecutionEnabled'] ?? false,
      parallelDegree: json['parallelDegree'] ?? 1,
      cacheEnabled: json['cacheEnabled'] ?? false,
      cacheHit: json['cacheHit'] ?? false,
      cacheTtlSeconds: json['cacheTtlSeconds'] ?? 300,
      auditTrailEnabled: json['auditTrailEnabled'] ?? true,
      accessControlEnforced: json['accessControlEnforced'] ?? true,
      complianceCheckEnabled: json['complianceCheckEnabled'] ?? true,
      complianceStatus: json['complianceStatus'],
      resultRowCount: json['resultRowCount'] ?? 0,
      resultDataSizeBytes: json['resultDataSizeBytes'] ?? 0,
      encryptedResultDataSizeBytes: json['encryptedResultDataSizeBytes'] ?? 0,
      compressionEnabled: json['compressionEnabled'] ?? false,
      compressionRatio: json['compressionRatio'] ?? 1.0,
      compressionAlgorithm: json['compressionAlgorithm'],
      queryExecutionTimeMs: json['queryExecutionTimeMs'] ?? 0,
      encryptionTimeMs: json['encryptionTimeMs'] ?? 0,
      decryptionTimeMs: json['decryptionTimeMs'] ?? 0,
      networkTransferTimeMs: json['networkTransferTimeMs'] ?? 0,
      totalLatencyMs: json['totalLatencyMs'] ?? 0,
      cpuUsagePercent: json['cpuUsagePercent'] ?? 0.0,
      memoryUsageBytes: json['memoryUsageBytes'] ?? 0,
      diskIoBytes: json['diskIoBytes'] ?? 0,
      networkIoBytes: json['networkIoBytes'] ?? 0,
      costEstimateUsd: json['costEstimateUsd'] ?? 0.0,
      errorOccurred: json['errorOccurred'] ?? false,
      errorMessage: json['errorMessage'],
      errorStackTrace: json['errorStackTrace'],
      retryCount: json['retryCount'] ?? 0,
      maxRetries: json['maxRetries'] ?? 3,
      queryStatus: QueryStatus.values.firstWhere(
        (e) => e.toString().split('.').last == json['queryStatus'],
        orElse: () => QueryStatus.pending,
      ),
      resultVerificationEnabled: json['resultVerificationEnabled'] ?? false,
      verificationResult: json['verificationResult'] ?? false,
      verificationMethod: json['verificationMethod'],
      verificationProof: json['verificationProof'],
      verificationTimeMs: json['verificationTimeMs'] ?? 0,
      createdTime: DateTime.parse(json['createdTime'] ?? DateTime.now().toIso8601String()),
      startTime: json['startTime'] != null ? DateTime.parse(json['startTime']) : null,
      endTime: json['endTime'] != null ? DateTime.parse(json['endTime']) : null,
      lastUpdatedTime: DateTime.parse(json['lastUpdatedTime'] ?? DateTime.now().toIso8601String()),
      expirationTime: json['expirationTime'] != null ? DateTime.parse(json['expirationTime']) : null,
      performanceScore: json['performanceScore'] ?? 0.0,
      privacyScore: json['privacyScore'] ?? 100.0,
      accuracyScore: json['accuracyScore'] ?? 100.0,
      complianceScore: json['complianceScore'] ?? 100.0,
      userSatisfactionScore: json['userSatisfactionScore'] ?? 0.0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'queryId': queryId,
      'databaseId': databaseId,
      'userId': userId,
      'sessionId': sessionId,
      'queryUuid': queryUuid,
      'queryType': queryType.toString().split('.').last,
      'queryComplexity': queryComplexity.toString().split('.').last,
      'querySql': querySql,
      'encryptedQuerySql': encryptedQuerySql,
      'queryParameters': queryParameters,
      'encryptedQueryParameters': encryptedQueryParameters,
      'privacyLevel': privacyLevel.toString().split('.').last,
      'encryptionMethod': encryptionMethod.toString().split('.').last,
      'resultEncryptionEnabled': resultEncryptionEnabled,
      'privacyBudgetConsumed': privacyBudgetConsumed,
      'differentialPrivacyEnabled': differentialPrivacyEnabled,
      'queryOptimizationEnabled': queryOptimizationEnabled,
      'parallelExecutionEnabled': parallelExecutionEnabled,
      'parallelDegree': parallelDegree,
      'cacheEnabled': cacheEnabled,
      'cacheHit': cacheHit,
      'resultRowCount': resultRowCount,
      'resultDataSizeBytes': resultDataSizeBytes,
      'queryExecutionTimeMs': queryExecutionTimeMs,
      'totalLatencyMs': totalLatencyMs,
      'queryStatus': queryStatus.toString().split('.').last,
      'errorOccurred': errorOccurred,
      'performanceScore': performanceScore,
      'privacyScore': privacyScore,
      'accuracyScore': accuracyScore,
    };
  }

  bool get isCompleted =>
      [QueryStatus.success, QueryStatus.failed, QueryStatus.timeout, QueryStatus.cancelled]
          .contains(queryStatus);

  bool get canRetry => retryCount < maxRetries && queryStatus == QueryStatus.failed;

  @override
  String toString() {
    return 'PrivacyPreservingQuery{queryUuid: $queryUuid, queryType: $queryType, status: $queryStatus, rowCount: $resultRowCount, latency: ${totalLatencyMs}ms}';
  }
}