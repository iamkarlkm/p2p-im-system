import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import '../config/api_config.dart';
import '../models/intelligent_search_models.dart';
import '../utils/storage_util.dart';

/// 智能搜索服务
class IntelligentSearchService {
  static final IntelligentSearchService _instance = IntelligentSearchService._internal();
  factory IntelligentSearchService() => _instance;
  IntelligentSearchService._internal();

  /// 当前搜索会话ID
  String? _currentSessionId;
  
  /// 获取当前会话ID
  String? get currentSessionId => _currentSessionId;

  /// 智能搜索
  Future<IntelligentSearchResult> intelligentSearch({
    required String query,
    String? sessionId,
    double? longitude,
    double? latitude,
    String? cityCode,
    bool voiceSearch = false,
    String sortBy = 'DEFAULT',
    int pageNum = 1,
    int pageSize = 20,
  }) async {
    final token = await StorageUtil.getToken();
    final deviceId = await StorageUtil.getDeviceId();
    
    final response = await http.post(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/search/intelligent'),
      headers: {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $token',
      },
      body: jsonEncode({
        'query': query,
        'sessionId': sessionId ?? _currentSessionId,
        'longitude': longitude,
        'latitude': latitude,
        'cityCode': cityCode,
        'voiceSearch': voiceSearch,
        'sortBy': sortBy,
        'pageNum': pageNum,
        'pageSize': pageSize,
        'source': 'APP',
        'deviceId': deviceId,
      }),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      if (data['code'] == 200) {
        final result = IntelligentSearchResult.fromJson(data['data']);
        // 保存会话ID用于多轮对话
        if (result.sessionId != null) {
          _currentSessionId = result.sessionId;
        }
        return result;
      }
      throw Exception(data['message'] ?? '搜索失败');
    }
    throw Exception('网络请求失败: ${response.statusCode}');
  }

  /// 语义理解
  Future<Map<String, dynamic>> semanticUnderstand({
    required String query,
    double? longitude,
    double? latitude,
    String? cityCode,
  }) async {
    final response = await http.post(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/search/understand'),
      headers: {
        'Content-Type': 'application/json',
      },
      body: jsonEncode({
        'query': query,
        'longitude': longitude,
        'latitude': latitude,
        'cityCode': cityCode,
      }),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      if (data['code'] == 200) {
        return data['data'];
      }
      throw Exception(data['message'] ?? '语义理解失败');
    }
    throw Exception('网络请求失败: ${response.statusCode}');
  }

  /// 获取搜索建议
  Future<SearchSuggestion> getSuggestions({
    required String keyword,
    String? cityCode,
    double? longitude,
    double? latitude,
    int limit = 10,
  }) async {
    final token = await StorageUtil.getToken();
    
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/search/suggestions'
          '?keyword=$keyword'
          '${cityCode != null ? '&cityCode=$cityCode' : ''}'
          '${longitude != null ? '&longitude=$longitude' : ''}'
          '${latitude != null ? '&latitude=$latitude' : ''}'
          '&limit=$limit'),
      headers: {
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      if (data['code'] == 200) {
        return SearchSuggestion.fromJson(data['data']);
      }
      throw Exception(data['message'] ?? '获取建议失败');
    }
    throw Exception('网络请求失败: ${response.statusCode}');
  }

  /// 获取热门搜索
  Future<List<HotSearch>> getHotSearches({
    String? cityCode,
    int limit = 10,
  }) async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/search/hot'
          '${cityCode != null ? '?cityCode=$cityCode' : ''}'
          '&limit=$limit'),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      if (data['code'] == 200) {
        return (data['data'] as List)
            .map((e) => HotSearch.fromJson(e))
            .toList();
      }
      throw Exception(data['message'] ?? '获取热门搜索失败');
    }
    throw Exception('网络请求失败: ${response.statusCode}');
  }

  /// 获取搜索历史
  Future<List<String>> getSearchHistory({int limit = 20}) async {
    final token = await StorageUtil.getToken();
    
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/search/history?limit=$limit'),
      headers: {
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      if (data['code'] == 200) {
        return (data['data'] as List).map((e) => e.toString()).toList();
      }
      throw Exception(data['message'] ?? '获取搜索历史失败');
    }
    throw Exception('网络请求失败: ${response.statusCode}');
  }

  /// 清除搜索历史
  Future<bool> clearSearchHistory() async {
    final token = await StorageUtil.getToken();
    
    final response = await http.delete(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/search/history'),
      headers: {
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return data['code'] == 200;
    }
    return false;
  }

  /// 删除单条搜索历史
  Future<bool> deleteSearchHistory(String keyword) async {
    final token = await StorageUtil.getToken();
    
    final response = await http.delete(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/search/history/$keyword'),
      headers: {
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return data['code'] == 200;
    }
    return false;
  }

  /// 获取搜索发现
  Future<List<String>> getSearchDiscovery({String? cityCode}) async {
    final token = await StorageUtil.getToken();
    
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/search/discovery'
          '${cityCode != null ? '?cityCode=$cityCode' : ''}'),
      headers: {
        'Authorization': 'Bearer $token',
      },
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      if (data['code'] == 200) {
        return (data['data'] as List).map((e) => e.toString()).toList();
      }
      throw Exception(data['message'] ?? '获取搜索发现失败');
    }
    throw Exception('网络请求失败: ${response.statusCode}');
  }

  /// 清除当前会话
  void clearSession() {
    _currentSessionId = null;
  }
}
