/// 联邦学习 API 服务 - Flutter 移动端

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'federated_learning_model.dart';

class FederatedLearningApiService {
  final String baseUrl;
  final String? authToken;

  FederatedLearningApiService({
    this.baseUrl = '/api/v1/federated-learning',
    this.authToken,
  });

  Future<Map<String, dynamic>> createModel({
    required String modelName,
    required ModelType modelType,
    required ModelScope modelScope,
    ModelConfig? config,
  }) async {
    final response = await _post('/models', {
      'modelName': modelName,
      'modelType': _modelTypeToString(modelType),
      'modelScope': _modelScopeToString(modelScope),
      'config': (config ?? ModelConfig()).toJson(),
    });
    return response['data'];
  }

  Future<FederatedLearningModel> getModelStatus(String modelId) async {
    final response = await _get('/models/$modelId/status');
    return FederatedLearningModel.fromJson(response['data']);
  }

  Future<Map<String, dynamic>> generateRecommendation({
    required String userId,
    required String sessionId,
    required RecommendationType recommendationType,
    String? context,
    Map<String, dynamic>? options,
  }) async {
    final response = await _post('/recommendations', {
      'userId': userId,
      'sessionId': sessionId,
      'recommendationType': _recommendationTypeToString(recommendationType),
      'context': context,
      'options': options,
    });
    return response['data'];
  }

  Future<PrivacyPreservingRecommendation> getRecommendationStatus(
      String recommendationId) async {
    final response = await _get('/recommendations/$recommendationId/status');
    return PrivacyPreservingRecommendation.fromJson(response['data']);
  }

  Future<List<PrivacyPreservingRecommendation>> getUserRecommendations(
      String userId, {
      int limit = 20,
    }) async {
    final response = await _get('/users/$userId/recommendations',
        queryParams: {'limit': limit.toString()});
    final List tasks = response['data']['recommendations'] ?? [];
    return tasks.map((t) => PrivacyPreservingRecommendation.fromJson(t)).toList();
  }

  Future<void> submitGradient({
    required String modelId,
    required String clientId,
    required Map<String, dynamic> gradientUpdate,
    required int trainingSamples,
  }) async {
    await _post('/models/$modelId/gradients', {
      'clientId': clientId,
      'gradientUpdate': gradientUpdate,
      'trainingSamples': trainingSamples,
    });
  }

  Future<Map<String, dynamic>> performAggregation(String modelId) async {
    final response = await _post('/models/$modelId/aggregate', {});
    return response['aggregationResult'];
  }

  Future<void> recordFeedback({
    required String recommendationId,
    required String interactionType,
    double? feedbackScore,
    int? dwellTimeSeconds,
  }) async {
    await _post('/recommendations/$recommendationId/feedback', {
      'interactionType': interactionType,
      'feedbackScore': feedbackScore,
      'dwellTimeSeconds': dwellTimeSeconds,
    });
  }

  Future<Map<String, dynamic>> getSystemStatistics() async {
    final response = await _get('/statistics');
    return response['statistics'];
  }

  Future<bool> pauseModel(String modelId) async {
    final response = await _post('/models/$modelId/pause', {});
    return response['success'];
  }

  Future<bool> resumeModel(String modelId) async {
    final response = await _post('/models/$modelId/resume', {});
    return response['success'];
  }

  Future<void> withdrawUserConsent(String userId) async {
    await _post('/users/$userId/withdraw-consent', {});
  }

  Future<void> cleanupExpiredData({int daysToKeep = 30}) async {
    await _post('/cleanup', {}, queryParams: {'daysToKeep': daysToKeep.toString()});
  }

  Future<Map<String, dynamic>> getPrivacyOptions() async {
    final response = await _get('/privacy-options');
    return response['privacyOptions'];
  }

  Future<Map<String, dynamic>> getAggregationAlgorithms() async {
    final response = await _get('/aggregation-algorithms');
    return response['algorithms'];
  }

  Future<Map<String, dynamic>> healthCheck() async {
    return await _get('/health');
  }

  String _modelTypeToString(ModelType type) {
    return type.toString().split('.').last.toUpperCase();
  }

  String _modelScopeToString(ModelScope scope) {
    return scope.toString().split('.').last.toUpperCase();
  }

  String _recommendationTypeToString(RecommendationType type) {
    return type.toString().split('.').last.toUpperCase();
  }

  Future<Map<String, dynamic>> _get(String path, {Map<String, String>? queryParams}) async {
    return await _request('GET', path, null, queryParams: queryParams);
  }

  Future<Map<String, dynamic>> _post(String path, Map<String, dynamic> body, {Map<String, String>? queryParams}) async {
    return await _request('POST', path, body, queryParams: queryParams);
  }

  Future<Map<String, dynamic>> _request(
    String method,
    String path,
    Map<String, dynamic>? body, {
    Map<String, String>? queryParams,
  }) async {
    final url = Uri.parse(baseUrl + path).replace(queryParameters: queryParams);
    final headers = {'Content-Type': 'application/json'};
    if (authToken != null) {
      headers['Authorization'] = 'Bearer $authToken';
    }

    final response = method == 'GET'
        ? await http.get(url, headers: headers)
        : await http.post(url, headers: headers, body: jsonEncode(body));

    if (response.statusCode >= 200 && response.statusCode < 300) {
      return jsonDecode(response.body);
    } else {
      throw Exception('HTTP ${response.statusCode}: ${response.reasonPhrase}');
    }
  }
}

final federatedLearningApiService = FederatedLearningApiService();