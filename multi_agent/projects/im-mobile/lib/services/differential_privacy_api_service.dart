import 'dart:convert';
import 'package:http/http.dart' as http;
import 'differential_privacy_service.dart';

/// 差分隐私 API 服务
class DifferentialPrivacyApiService {
  final String baseUrl;
  final http.Client _client;

  DifferentialPrivacyApiService({
    this.baseUrl = '/api/v1/differential-privacy',
    http.Client? client,
  }) : _client = client ?? http.Client();

  String get _configEndpoint => '$baseUrl/config';
  String get _budgetEndpoint => '$baseUrl/budget';
  String get _impactEndpoint => '$baseUrl/impact';

  // ==================== 配置管理 ====================

  /// 获取配置
  Future<DifferentialPrivacyConfig> getConfig(String configKey) async {
    final response = await _client.get(Uri.parse('$_configEndpoint/$configKey'));
    if (response.statusCode == 200) {
      return DifferentialPrivacyConfig.fromJson(json.decode(response.body));
    } else if (response.statusCode == 404) {
      throw Exception('Config not found: $configKey');
    } else {
      throw Exception('Failed to get config: ${response.statusCode}');
    }
  }

  /// 获取所有活跃配置
  Future<List<DifferentialPrivacyConfig>> getAllActiveConfigs() async {
    final response = await _client.get(Uri.parse(_configEndpoint));
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = json.decode(response.body);
      return jsonList.map((json) => DifferentialPrivacyConfig.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get configs: ${response.statusCode}');
    }
  }

  /// 创建配置
  Future<DifferentialPrivacyConfig> createConfig(Map<String, dynamic> configData) async {
    final response = await _client.post(
      Uri.parse(_configEndpoint),
      headers: {'Content-Type': 'application/json'},
      body: json.encode(configData),
    );
    if (response.statusCode == 200) {
      return DifferentialPrivacyConfig.fromJson(json.decode(response.body));
    } else {
      throw Exception('Failed to create config: ${response.statusCode}');
    }
  }

  /// 更新配置
  Future<DifferentialPrivacyConfig> updateConfig(
    String configKey,
    Map<String, dynamic> updates,
  ) async {
    final response = await _client.put(
      Uri.parse('$_configEndpoint/$configKey'),
      headers: {'Content-Type': 'application/json'},
      body: json.encode(updates),
    );
    if (response.statusCode == 200) {
      return DifferentialPrivacyConfig.fromJson(json.decode(response.body));
    } else if (response.statusCode == 404) {
      throw Exception('Config not found: $configKey');
    } else {
      throw Exception('Failed to update config: ${response.statusCode}');
    }
  }

  /// 删除配置
  Future<void> deleteConfig(String configKey) async {
    final response = await _client.delete(Uri.parse('$_configEndpoint/$configKey'));
    if (response.statusCode != 204) {
      throw Exception('Failed to delete config: ${response.statusCode}');
    }
  }

  /// 获取待审批配置
  Future<List<DifferentialPrivacyConfig>> getPendingApprovals() async {
    final response = await _client.get(Uri.parse('$_configEndpoint/approval/pending'));
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = json.decode(response.body);
      return jsonList.map((json) => DifferentialPrivacyConfig.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get pending approvals: ${response.statusCode}');
    }
  }

  /// 搜索配置
  Future<List<DifferentialPrivacyConfig>> searchConfigs(String keyword) async {
    final response = await _client.get(
      Uri.parse('$_configEndpoint/search').replace(queryParameters: {'keyword': keyword}),
    );
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = json.decode(response.body);
      return jsonList.map((json) => DifferentialPrivacyConfig.fromJson(json)).toList();
    } else {
      throw Exception('Failed to search configs: ${response.statusCode}');
    }
  }

  // ==================== 隐私预算管理 ====================

  /// 获取用户所有预算
  Future<List<PrivacyBudget>> getUserBudgets(String userId) async {
    final response = await _client.get(Uri.parse('$_budgetEndpoint/user/$userId'));
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = json.decode(response.body);
      return jsonList.map((json) => PrivacyBudget.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get user budgets: ${response.statusCode}');
    }
  }

  /// 获取用户指定类型预算
  Future<PrivacyBudget> getUserBudget(String userId, String budgetType) async {
    final response = await _client.get(Uri.parse('$_budgetEndpoint/user/$userId/type/$budgetType'));
    if (response.statusCode == 200) {
      return PrivacyBudget.fromJson(json.decode(response.body));
    } else if (response.statusCode == 404) {
      throw Exception('Budget not found for user: $userId');
    } else {
      throw Exception('Failed to get budget: ${response.statusCode}');
    }
  }

  /// 创建预算
  Future<PrivacyBudget> createBudget(Map<String, dynamic> budgetData) async {
    final response = await _client.post(
      Uri.parse(_budgetEndpoint),
      headers: {'Content-Type': 'application/json'},
      body: json.encode(budgetData),
    );
    if (response.statusCode == 200) {
      return PrivacyBudget.fromJson(json.decode(response.body));
    } else {
      throw Exception('Failed to create budget: ${response.statusCode}');
    }
  }

  /// 消耗预算
  Future<PrivacyBudget> consumeBudget(String userId, String budgetType, double epsilon) async {
    final response = await _client.post(
      Uri.parse('$_budgetEndpoint/consume').replace(
        queryParameters: {
          'userId': userId,
          'budgetType': budgetType,
          'epsilon': epsilon.toString(),
        },
      ),
    );
    if (response.statusCode == 200) {
      return PrivacyBudget.fromJson(json.decode(response.body));
    } else {
      final errorData = json.decode(response.body);
      throw Exception(errorData['error'] ?? 'Failed to consume budget');
    }
  }

  /// 检查预算是否充足
  Future<bool> checkBudget(String userId, String budgetType, double epsilon) async {
    final response = await _client.post(
      Uri.parse('$_budgetEndpoint/check').replace(
        queryParameters: {
          'userId': userId,
          'budgetType': budgetType,
          'epsilon': epsilon.toString(),
        },
      ),
    );
    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return data['sufficient'] as bool;
    } else {
      throw Exception('Failed to check budget: ${response.statusCode}');
    }
  }

  /// 获取已封锁预算
  Future<List<PrivacyBudget>> getBlockedBudgets() async {
    final response = await _client.get(Uri.parse('$_budgetEndpoint/blocked'));
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = json.decode(response.body);
      return jsonList.map((json) => PrivacyBudget.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get blocked budgets: ${response.statusCode}');
    }
  }

  /// 解除预算封锁
  Future<void> unblockBudget(int budgetId) async {
    final response = await _client.post(Uri.parse('$_budgetEndpoint/unblock/$budgetId'));
    if (response.statusCode != 204) {
      throw Exception('Failed to unblock budget: ${response.statusCode}');
    }
  }

  // ==================== 隐私影响评估 ====================

  /// 创建评估
  Future<PrivacyImpact> createAssessment(Map<String, dynamic> assessmentData) async {
    final response = await _client.post(
      Uri.parse(_impactEndpoint),
      headers: {'Content-Type': 'application/json'},
      body: json.encode(assessmentData),
    );
    if (response.statusCode == 200) {
      return PrivacyImpact.fromJson(json.decode(response.body));
    } else {
      throw Exception('Failed to create assessment: ${response.statusCode}');
    }
  }

  /// 完成评估
  Future<PrivacyImpact> completeAssessment(int id) async {
    final response = await _client.post(Uri.parse('$_impactEndpoint/$id/complete'));
    if (response.statusCode == 200) {
      return PrivacyImpact.fromJson(json.decode(response.body));
    } else if (response.statusCode == 404) {
      throw Exception('Assessment not found: $id');
    } else {
      throw Exception('Failed to complete assessment: ${response.statusCode}');
    }
  }

  /// 按操作 ID 获取评估
  Future<List<PrivacyImpact>> getByOperationId(String operationId) async {
    final response = await _client.get(Uri.parse('$_impactEndpoint/operation/$operationId'));
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = json.decode(response.body);
      return jsonList.map((json) => PrivacyImpact.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get assessments: ${response.statusCode}');
    }
  }

  /// 按用户 ID 获取评估
  Future<List<PrivacyImpact>> getByUserId(String userId) async {
    final response = await _client.get(Uri.parse('$_impactEndpoint/user/$userId'));
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = json.decode(response.body);
      return jsonList.map((json) => PrivacyImpact.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get assessments: ${response.statusCode}');
    }
  }

  /// 获取高风险评估
  Future<List<PrivacyImpact>> getHighRiskAssessments(double threshold, int page, int size) async {
    final response = await _client.get(
      Uri.parse('$_impactEndpoint/risk/high').replace(
        queryParameters: {
          'threshold': threshold.toString(),
          'page': page.toString(),
          'size': size.toString(),
        },
      ),
    );
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = json.decode(response.body);
      return jsonList.map((json) => PrivacyImpact.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get high risk assessments: ${response.statusCode}');
    }
  }

  /// 获取总览统计
  Future<Map<String, dynamic>> getOverviewStats() async {
    final response = await _client.get(Uri.parse('$_impactEndpoint/stats/overview'));
    if (response.statusCode == 200) {
      return json.decode(response.body) as Map<String, dynamic>;
    } else {
      throw Exception('Failed to get overview stats: ${response.statusCode}');
    }
  }

  void dispose() {
    _client.close();
  }
}