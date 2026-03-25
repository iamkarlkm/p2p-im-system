/**
 * 同态加密数据库 Dart API 服务
 * 提供加密数据库管理、隐私保护查询的 API 调用封装
 */

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'homomorphic_encryption_database_model.dart';

/// API 基础 URL
const String _baseUrl = '/api/v1/homomorphic-encryption';

/// 同态加密数据库 API 服务类
class HomomorphicEncryptionDatabaseApiService {
  final http.Client _client;
  final String? authToken;

  HomomorphicEncryptionDatabaseApiService({
    http.Client? client,
    this.authToken,
  }) : _client = client ?? http.Client();

  Map<String, String> get _headers => {
        'Content-Type': 'application/json',
        if (authToken != null) 'Authorization': 'Bearer $authToken',
      };

  /// 创建同态加密数据库
  Future<Map<String, dynamic>> createDatabase({
    required int userId,
    required String databaseName,
    String? sessionId,
    String databaseType = 'MESSAGE',
    String encryptionScheme = 'CKKS',
    String securityLevel = 'HIGH',
    int keySize = 4096,
    int modulusSize = 16384,
    int plaintextModulus = 65537,
    int noiseBudget = 100,
    bool compressionEnabled = true,
    String compressionAlgorithm = 'ZSTD',
    bool indexingEnabled = true,
    String indexType = 'BALANCED_TREE',
    bool queryCacheEnabled = true,
    bool parallelismEnabled = true,
    int maxParallelThreads = 8,
    bool hardwareAcceleration = false,
    double privacyBudget = 100.0,
    bool differentialPrivacyEnabled = true,
    double dpEpsilon = 1.0,
    double dpDelta = 0.00001,
    int dataRetentionDays = 365,
    bool auditLoggingEnabled = true,
  }) async {
    try {
      final response = await _client.post(
        Uri.parse(_baseUrl + '/database'),
        headers: _headers,
        body: jsonEncode({
          'userId': userId,
          'sessionId': sessionId,
          'databaseName': databaseName,
          'databaseType': databaseType,
          'encryptionScheme': encryptionScheme,
          'securityLevel': securityLevel,
          'keySize': keySize,
          'modulusSize': modulusSize,
          'plaintextModulus': plaintextModulus,
          'noiseBudget': noiseBudget,
          'compressionEnabled': compressionEnabled,
          'compressionAlgorithm': compressionAlgorithm,
          'indexingEnabled': indexingEnabled,
          'indexType': indexType,
          'queryCacheEnabled': queryCacheEnabled,
          'parallelismEnabled': parallelismEnabled,
          'maxParallelThreads': maxParallelThreads,
          'hardwareAcceleration': hardwareAcceleration,
          'privacyBudget': privacyBudget,
          'differentialPrivacyEnabled': differentialPrivacyEnabled,
          'dpEpsilon': dpEpsilon,
          'dpDelta': dpDelta,
          'dataRetentionDays': dataRetentionDays,
          'auditLoggingEnabled': auditLoggingEnabled,
        }),
      );

      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '创建数据库失败：$e'};
    }
  }

  /// 获取数据库信息
  Future<Map<String, dynamic>> getDatabase(int databaseId) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/database/$databaseId'),
        headers: _headers,
      );
      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '获取数据库失败：$e'};
    }
  }

  /// 获取用户的所有数据库
  Future<Map<String, dynamic>> getUserDatabases(int userId) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/user/$userId/databases'),
        headers: _headers,
      );
      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '获取数据库列表失败：$e'};
    }
  }

  /// 更新数据库状态
  Future<Map<String, dynamic>> updateDatabaseStatus(
    int databaseId,
    String status,
  ) async {
    try {
      final response = await _client.put(
        Uri.parse('$_baseUrl/database/$databaseId/status'),
        headers: _headers,
        body: jsonEncode({'status': status}),
      );
      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '更新数据库状态失败：$e'};
    }
  }

  /// 优化数据库性能
  Future<Map<String, dynamic>> optimizeDatabase(int databaseId) async {
    try {
      final response = await _client.post(
        Uri.parse('$_baseUrl/database/$databaseId/optimize'),
        headers: _headers,
      );
      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '优化数据库失败：$e'};
    }
  }

  /// 重新生成密钥
  Future<Map<String, dynamic>> rekeyDatabase(int databaseId) async {
    try {
      final response = await _client.post(
        Uri.parse('$_baseUrl/database/$databaseId/rekey'),
        headers: _headers,
      );
      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '重新生成密钥失败：$e'};
    }
  }

  /// 备份数据库
  Future<Map<String, dynamic>> backupDatabase(int databaseId) async {
    try {
      final response = await _client.post(
        Uri.parse('$_baseUrl/database/$databaseId/backup'),
        headers: _headers,
      );
      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '备份数据库失败：$e'};
    }
  }

  /// 获取数据库统计信息
  Future<Map<String, dynamic>> getDatabaseStatistics(int databaseId) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/database/$databaseId/statistics'),
        headers: _headers,
      );
      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '获取统计信息失败：$e'};
    }
  }

  /// 从 JSON 创建 HomomorphicEncryptionDatabase 对象
  HomomorphicEncryptionDatabase? parseDatabase(Map<String, dynamic> json) {
    try {
      return HomomorphicEncryptionDatabase.fromJson(json);
    } catch (e) {
      return null;
    }
  }
}

/// 隐私保护查询 API 服务类
class PrivacyPreservingQueryApiService {
  final http.Client _client;
  final String? authToken;

  PrivacyPreservingQueryApiService({
    http.Client? client,
    this.authToken,
  }) : _client = client ?? http.Client();

  Map<String, String> get _headers => {
        'Content-Type': 'application/json',
        if (authToken != null) 'Authorization': 'Bearer $authToken',
      };

  /// 创建隐私保护查询
  Future<Map<String, dynamic>> createQuery({
    required int databaseId,
    required int userId,
    required String querySql,
    String? sessionId,
    String queryType = 'SELECT',
    String privacyLevel = 'HIGH',
    String encryptionMethod = 'HOMOMORPHIC',
    String? queryParameters,
    String? queryFilters,
    List<String>? projectionFields,
    List<String>? sortFields,
    int limit = 100,
    int offset = 0,
    bool differentialPrivacyEnabled = true,
    bool resultEncryptionEnabled = true,
    bool queryOptimizationEnabled = true,
    bool parallelExecutionEnabled = true,
    int parallelDegree = 4,
    bool cacheEnabled = true,
    bool resultVerificationEnabled = true,
    String verificationMethod = 'MERKLE_TREE',
    bool auditTrailEnabled = true,
    bool accessControlEnforced = true,
    bool complianceCheckEnabled = true,
  }) async {
    try {
      final response = await _client.post(
        Uri.parse(_baseUrl + '/query'),
        headers: _headers,
        body: jsonEncode({
          'databaseId': databaseId,
          'userId': userId,
          'sessionId': sessionId,
          'queryType': queryType,
          'querySql': querySql,
          'privacyLevel': privacyLevel,
          'encryptionMethod': encryptionMethod,
          'queryParameters': queryParameters,
          'queryFilters': queryFilters,
          'projectionFields': projectionFields,
          'sortFields': sortFields,
          'limit': limit,
          'offset': offset,
          'differentialPrivacyEnabled': differentialPrivacyEnabled,
          'resultEncryptionEnabled': resultEncryptionEnabled,
          'queryOptimizationEnabled': queryOptimizationEnabled,
          'parallelExecutionEnabled': parallelExecutionEnabled,
          'parallelDegree': parallelDegree,
          'cacheEnabled': cacheEnabled,
          'resultVerificationEnabled': resultVerificationEnabled,
          'verificationMethod': verificationMethod,
          'auditTrailEnabled': auditTrailEnabled,
          'accessControlEnforced': accessControlEnforced,
          'complianceCheckEnabled': complianceCheckEnabled,
        }),
      );

      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '创建查询失败：$e'};
    }
  }

  /// 执行查询
  Future<Map<String, dynamic>> executeQuery(String queryUuid) async {
    try {
      final response = await _client.post(
        Uri.parse('$_baseUrl/query/$queryUuid/execute'),
        headers: _headers,
      );
      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '执行查询失败：$e'};
    }
  }

  /// 获取查询信息
  Future<Map<String, dynamic>> getQuery(String queryUuid) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/query/$queryUuid'),
        headers: _headers,
      );
      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '获取查询失败：$e'};
    }
  }

  /// 获取用户的所有查询
  Future<Map<String, dynamic>> getUserQueries(int userId) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/user/$userId/queries'),
        headers: _headers,
      );
      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '获取查询列表失败：$e'};
    }
  }

  /// 取消查询
  Future<Map<String, dynamic>> cancelQuery(String queryUuid) async {
    try {
      final response = await _client.post(
        Uri.parse('$_baseUrl/query/$queryUuid/cancel'),
        headers: _headers,
      );
      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '取消查询失败：$e'};
    }
  }

  /// 批量执行查询
  Future<Map<String, dynamic>> batchExecuteQueries(List<String> queryUuids) async {
    try {
      final response = await _client.post(
        Uri.parse('$_baseUrl/query/batch-execute'),
        headers: _headers,
        body: jsonEncode(queryUuids),
      );
      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '批量执行查询失败：$e'};
    }
  }

  /// 获取查询统计信息
  Future<Map<String, dynamic>> getQueryStatistics(int databaseId) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/database/$databaseId/query-statistics'),
        headers: _headers,
      );
      return jsonDecode(response.body);
    } catch (e) {
      return {'success': false, 'error': '获取查询统计失败：$e'};
    }
  }

  /// 监控查询执行状态（轮询）
  Future<PrivacyPreservingQuery?> monitorQueryExecution(
    String queryUuid, {
    Function(PrivacyPreservingQuery)? onProgress,
    int intervalMs = 1000,
    int timeoutMs = 300000,
  }) async {
    final startTime = DateTime.now().millisecondsSinceEpoch;

    while (DateTime.now().millisecondsSinceEpoch - startTime < timeoutMs) {
      final result = await getQuery(queryUuid);
      if (!(result['success'] as bool) || result['data'] == null) {
        return null;
      }

      final query = PrivacyPreservingQuery.fromJson(result['data'] as Map<String, dynamic>);
      
      if (onProgress != null) {
        onProgress(query);
      }

      // 检查是否完成
      if (query.isCompleted) {
        return query;
      }

      // 等待下次轮询
      await Future.delayed(Duration(milliseconds: intervalMs));
    }

    return null;
  }

  /// 等待查询完成并返回结果
  Future<PrivacyPreservingQuery?> waitForQueryCompletion(
    String queryUuid, {
    int timeoutMs = 300000,
  }) async {
    return await monitorQueryExecution(
      queryUuid,
      onProgress: (_) {},
      timeoutMs: timeoutMs,
    );
  }

  /// 重试失败的查询
  Future<Map<String, dynamic>> retryQuery(String queryUuid) async {
    // 先获取查询信息
    final getResult = await getQuery(queryUuid);
    if (!(getResult['success'] as bool) || getResult['data'] == null) {
      return {'success': false, 'error': '无法获取查询信息'};
    }

    final query = PrivacyPreservingQuery.fromJson(getResult['data'] as Map<String, dynamic>);

    // 检查是否可以重试
    if (!query.canRetry) {
      return {'success': false, 'error': '已达到最大重试次数'};
    }

    // 重新执行查询
    return await executeQuery(queryUuid);
  }

  /// 验证查询结果
  Future<Map<String, dynamic>> verifyQueryResult(String queryUuid) async {
    try {
      final result = await getQuery(queryUuid);
      
      if (result['success'] as bool && result['data'] != null) {
        final query = PrivacyPreservingQuery.fromJson(result['data'] as Map<String, dynamic>);
        
        if (query.resultVerificationEnabled && query.verificationResult) {
          return {'success': true, 'message': '查询结果验证通过', 'data': result['data']};
        } else if (query.resultVerificationEnabled && !query.verificationResult) {
          return {'success': false, 'error': '查询结果验证失败', 'data': result['data']};
        } else {
          return {'success': true, 'message': '查询结果未启用验证', 'data': result['data']};
        }
      }
      
      return result;
    } catch (e) {
      return {'success': false, 'error': '验证查询结果失败：$e'};
    }
  }

  /// 从 JSON 创建 PrivacyPreservingQuery 对象
  PrivacyPreservingQuery? parseQuery(Map<String, dynamic> json) {
    try {
      return PrivacyPreservingQuery.fromJson(json);
    } catch (e) {
      return null;
    }
  }
}

/// 工具函数：格式化数据大小
String formatDataSize(int bytes) {
  final units = ['B', 'KB', 'MB', 'GB', 'TB'];
  double size = bytes.toDouble();
  int unitIndex = 0;

  while (size >= 1024 && unitIndex < units.length - 1) {
    size /= 1024;
    unitIndex++;
  }

  return '${size.toStringAsFixed(2)} ${units[unitIndex]}';
}

/// 工具函数：格式化持续时间
String formatDuration(int ms) {
  if (ms < 1000) {
    return '${ms}ms';
  } else if (ms < 60000) {
    return '${(ms / 1000).toStringAsFixed(2)}s';
  } else {
    final minutes = ms ~/ 60000;
    final seconds = ((ms % 60000) / 1000).toStringAsFixed(1);
    return '${minutes}m ${seconds}s';
  }
}