import 'dart:convert';
import 'package:http/http.dart' as http;
import 'adaptive_content_classification_model.dart';

/// 自适应内容分类 API 服务
/// 提供分类配置管理和内容分类的 API 调用
class AdaptiveContentClassificationApiService {
  final String baseUrl;
  final http.Client _client;

  AdaptiveContentClassificationApiService({
    this.baseUrl = '/api/v1/adaptive-content-classification',
    http.Client? client,
  }) : _client = client ?? http.Client();

  // ========== 配置管理 API ==========

  /// 创建分类配置
  Future<AdaptiveContentClassificationConfig> createConfig(
    AdaptiveContentClassificationConfig config,
  ) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/configs'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode(config.toJson()),
    );

    if (response.statusCode != 201 && response.statusCode != 200) {
      throw Exception('创建配置失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '创建配置失败');
    }

    return AdaptiveContentClassificationConfig.fromJson(
      data['data'] as Map<String, dynamic>,
    );
  }

  /// 获取配置详情
  Future<AdaptiveContentClassificationConfig> getConfig(int configId) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/configs/$configId'),
    );

    if (response.statusCode != 200) {
      throw Exception('获取配置失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '获取配置失败');
    }

    return AdaptiveContentClassificationConfig.fromJson(
      data['data'] as Map<String, dynamic>,
    );
  }

  /// 更新分类配置
  Future<AdaptiveContentClassificationConfig> updateConfig(
    int configId,
    Map<String, dynamic> updates,
  ) async {
    final response = await _client.put(
      Uri.parse('$baseUrl/configs/$configId'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode(updates),
    );

    if (response.statusCode != 200) {
      throw Exception('更新配置失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '更新配置失败');
    }

    return AdaptiveContentClassificationConfig.fromJson(
      data['data'] as Map<String, dynamic>,
    );
  }

  /// 删除分类配置
  Future<void> deleteConfig(int configId) async {
    final response = await _client.delete(
      Uri.parse('$baseUrl/configs/$configId'),
    );

    if (response.statusCode != 200) {
      throw Exception('删除配置失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '删除配置失败');
    }
  }

  /// 查询用户配置列表
  Future<List<AdaptiveContentClassificationConfig>> getUserConfigs(
    int userId,
  ) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/configs/user/$userId'),
    );

    if (response.statusCode != 200) {
      throw Exception('获取用户配置失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '获取用户配置失败');
    }

    final List<dynamic> configs = data['data'] as List;
    return configs
        .map((c) => AdaptiveContentClassificationConfig.fromJson(
              c as Map<String, dynamic>,
            ))
        .toList();
  }

  /// 获取配置统计信息
  Future<ConfigStats> getConfigStats(int configId) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/configs/$configId/stats'),
    );

    if (response.statusCode != 200) {
      throw Exception('获取配置统计失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '获取配置统计失败');
    }

    return ConfigStats.fromJson(data['data'] as Map<String, dynamic>);
  }

  // ========== 内容分类 API ==========

  /// 分类单个内容
  Future<ContentClassificationResult> classifyContent(
    int configId,
    Map<String, dynamic> contentData,
  ) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/configs/$configId/classify'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode(contentData),
    );

    if (response.statusCode != 201 && response.statusCode != 200) {
      throw Exception('分类内容失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '分类内容失败');
    }

    return ContentClassificationResult.fromJson(
      data['data'] as Map<String, dynamic>,
    );
  }

  /// 批量分类内容
  Future<List<ContentClassificationResult>> batchClassifyContent(
    int configId,
    List<Map<String, dynamic>> contents,
  ) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/configs/$configId/batch-classify'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode({'contents': contents}),
    );

    if (response.statusCode != 201 && response.statusCode != 200) {
      throw Exception('批量分类失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '批量分类失败');
    }

    final List<dynamic> results = data['data'] as List;
    return results
        .map((r) => ContentClassificationResult.fromJson(
              r as Map<String, dynamic>,
            ))
        .toList();
  }

  /// 获取分类结果
  Future<ContentClassificationResult> getClassificationResult(
    int resultId,
  ) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/results/$resultId'),
    );

    if (response.statusCode != 200) {
      throw Exception('获取分类结果失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '获取分类结果失败');
    }

    return ContentClassificationResult.fromJson(
      data['data'] as Map<String, dynamic>,
    );
  }

  /// 查询配置的分类结果
  Future<List<ContentClassificationResult>> getConfigResults(
    int configId,
  ) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/configs/$configId/results'),
    );

    if (response.statusCode != 200) {
      throw Exception('获取配置分类结果失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '获取配置分类结果失败');
    }

    final List<dynamic> results = data['data'] as List;
    return results
        .map((r) => ContentClassificationResult.fromJson(
              r as Map<String, dynamic>,
            ))
        .toList();
  }

  /// 查询高置信度结果
  Future<List<ContentClassificationResult>> getHighConfidenceResults(
    int configId, {
    int minConfidence = 80,
  }) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/configs/$configId/results/high-confidence').replace(
        queryParameters: {'minConfidence': minConfidence.toString()},
      ),
    );

    if (response.statusCode != 200) {
      throw Exception('获取高置信度结果失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '获取高置信度结果失败');
    }

    final List<dynamic> results = data['data'] as List;
    return results
        .map((r) => ContentClassificationResult.fromJson(
              r as Map<String, dynamic>,
            ))
        .toList();
  }

  /// 查询低置信度结果
  Future<List<ContentClassificationResult>> getLowConfidenceResults(
    int configId, {
    int maxConfidence = 60,
  }) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/configs/$configId/results/low-confidence').replace(
        queryParameters: {'maxConfidence': maxConfidence.toString()},
      ),
    );

    if (response.statusCode != 200) {
      throw Exception('获取低置信度结果失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '获取低置信度结果失败');
    }

    final List<dynamic> results = data['data'] as List;
    return results
        .map((r) => ContentClassificationResult.fromJson(
              r as Map<String, dynamic>,
            ))
        .toList();
  }

  /// 获取分类统计信息
  Future<ClassificationStats> getClassificationStats(int configId) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/configs/$configId/classification-stats'),
    );

    if (response.statusCode != 200) {
      throw Exception('获取分类统计失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '获取分类统计失败');
    }

    return ClassificationStats.fromJson(data['data'] as Map<String, dynamic>);
  }

  /// 获取分类趋势分析
  Future<ClassificationTrend> getClassificationTrend(
    int configId, {
    int days = 30,
  }) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/configs/$configId/trend').replace(
        queryParameters: {'days': days.toString()},
      ),
    );

    if (response.statusCode != 200) {
      throw Exception('获取分类趋势失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '获取分类趋势失败');
    }

    return ClassificationTrend.fromJson(data['data'] as Map<String, dynamic>);
  }

  // ========== 增量学习 API ==========

  /// 执行增量学习
  Future<void> performIncrementalLearning(int configId) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/configs/$configId/incremental-learning'),
    );

    if (response.statusCode != 200) {
      throw Exception('执行增量学习失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '执行增量学习失败');
    }
  }

  /// 批量执行增量学习
  Future<void> batchIncrementalLearning(List<int> configIds) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/configs/batch-incremental-learning'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode({'configIds': configIds}),
    );

    if (response.statusCode != 200) {
      throw Exception('批量增量学习失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '批量增量学习失败');
    }
  }

  /// 自动执行增量学习
  Future<void> autoIncrementalLearning() async {
    final response = await _client.post(
      Uri.parse('$baseUrl/configs/auto-incremental-learning'),
    );

    if (response.statusCode != 200) {
      throw Exception('自动增量学习失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '自动增量学习失败');
    }
  }

  // ========== 系统管理 API ==========

  /// 系统健康检查
  Future<Map<String, dynamic>> healthCheck() async {
    final response = await _client.get(
      Uri.parse('$baseUrl/health'),
    );

    if (response.statusCode != 200) {
      throw Exception('健康检查失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '健康检查失败');
    }

    return data['data'] as Map<String, dynamic>;
  }

  /// 获取系统统计
  Future<Map<String, dynamic>> getSystemStats() async {
    final response = await _client.get(
      Uri.parse('$baseUrl/system-stats'),
    );

    if (response.statusCode != 200) {
      throw Exception('获取系统统计失败：${response.body}');
    }

    final data = json.decode(response.body) as Map<String, dynamic>;
    if (data['success'] != true) {
      throw Exception(data['message'] ?? '获取系统统计失败');
    }

    return data['data'] as Map<String, dynamic>;
  }

  /// 关闭 HTTP 客户端
  void dispose() {
    _client.close();
  }
}