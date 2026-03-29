import 'dart:convert';
import '../../../core/services/http_client.dart';
import '../models/semantic_search_result.dart';
import '../providers/semantic_search_provider.dart';

/// 语义搜索服务
/// 处理自然语言POI搜索相关的API调用
/// 
/// Author: IM Development Team
/// Since: 2026-03-28
class SemanticSearchService {
  static final SemanticSearchService _instance = SemanticSearchService._internal();
  factory SemanticSearchService() => _instance;
  SemanticSearchService._internal();

  final HttpClient _httpClient = HttpClient();

  /// 执行语义搜索
  Future<List<SemanticSearchResult>> search({
    required String query,
    double? latitude,
    double? longitude,
    int radius = 3000,
    String? sessionId,
    bool isVoiceInput = false,
  }) async {
    final response = await _httpClient.post(
      '/api/v1/semantic-search/search',
      data: {
        'query': query,
        'latitude': latitude,
        'longitude': longitude,
        'radius': radius,
        'sessionId': sessionId,
        'isVoiceInput': isVoiceInput,
      },
    );

    if (response.statusCode == 200) {
      final data = response.data['data'] as List;
      return data.map((json) => SemanticSearchResult.fromJson(json)).toList();
    }

    throw Exception('搜索失败: ${response.statusMessage}');
  }

  /// 识别搜索意图
  Future<SearchIntent> recognizeIntent(String query, {String? sessionId}) async {
    final response = await _httpClient.post(
      '/api/v1/semantic-search/intent',
      queryParameters: {
        'query': query,
        'sessionId': sessionId,
      },
    );

    if (response.statusCode == 200) {
      return SearchIntent.fromJson(response.data['data']);
    }

    throw Exception('意图识别失败');
  }

  /// 获取搜索建议
  Future<List<String>> getSuggestions({
    required String query,
    double? latitude,
    double? longitude,
  }) async {
    final response = await _httpClient.get(
      '/api/v1/semantic-search/suggestions',
      queryParameters: {
        'query': query,
        'latitude': latitude,
        'longitude': longitude,
      },
    );

    if (response.statusCode == 200) {
      final data = response.data['data'] as List;
      return data.cast<String>();
    }

    return [];
  }

  /// 获取热门搜索
  Future<List<String>> getHotSearches({
    double? latitude,
    double? longitude,
    int limit = 10,
  }) async {
    final response = await _httpClient.get(
      '/api/v1/semantic-search/hot-searches',
      queryParameters: {
        'latitude': latitude,
        'longitude': longitude,
        'limit': limit,
      },
    );

    if (response.statusCode == 200) {
      final data = response.data['data'] as List;
      return data.cast<String>();
    }

    return [];
  }

  /// 获取搜索历史
  Future<List<String>> getSearchHistory({int limit = 20}) async {
    final response = await _httpClient.get(
      '/api/v1/semantic-search/history',
      queryParameters: {'limit': limit},
    );

    if (response.statusCode == 200) {
      final data = response.data['data'] as List;
      return data.cast<String>();
    }

    return [];
  }

  /// 清除搜索历史
  Future<void> clearSearchHistory() async {
    await _httpClient.delete('/api/v1/semantic-search/history');
  }

  /// 基于上下文的搜索
  Future<List<SemanticSearchResult>> searchWithContext({
    required String sessionId,
    required String query,
  }) async {
    final response = await _httpClient.post(
      '/api/v1/semantic-search/context-search',
      queryParameters: {
        'sessionId': sessionId,
        'query': query,
      },
    );

    if (response.statusCode == 200) {
      final data = response.data['data'] as List;
      return data.map((json) => SemanticSearchResult.fromJson(json)).toList();
    }

    return [];
  }

  /// 获取澄清问题
  Future<List<String>> getClarificationQuestions(String query) async {
    final response = await _httpClient.post(
      '/api/v1/semantic-search/clarify',
      queryParameters: {'query': query},
    );

    if (response.statusCode == 200) {
      final data = response.data['data'] as List;
      return data.cast<String>();
    }

    return [];
  }
}
