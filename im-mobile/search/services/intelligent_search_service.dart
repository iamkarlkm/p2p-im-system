import 'dart:async';
import 'package:flutter/foundation.dart';
import '../api/api_client.dart';
import '../models/poi_search_model.dart';
import '../utils/location_manager.dart';

/// 智能搜索服务
class IntelligentSearchService {
  static final IntelligentSearchService _instance = IntelligentSearchService._internal();
  factory IntelligentSearchService() => _instance;
  IntelligentSearchService._internal();

  final ApiClient _apiClient = ApiClient();
  final LocationManager _locationManager = LocationManager();

  // 搜索防抖计时器
  Timer? _debounceTimer;
  
  // 当前会话ID（用于多轮对话）
  String? _currentSessionId;

  /// 自然语言搜索
  /// 支持口语化查询，如"附近好吃的火锅"、"适合遛娃的公园"
  Future<SearchResponseModel> naturalLanguageSearch({
    required String query,
    int radius = 5000,
    String? cityCode,
    String? category,
    int? minPrice,
    int? maxPrice,
    double? minRating,
    List<String>? tags,
    String sortBy = 'distance',
    int page = 0,
    int size = 20,
    bool enablePersonalization = true,
  }) async {
    try {
      // 获取当前位置
      final position = await _locationManager.getCurrentPosition();
      if (position == null) {
        throw Exception('无法获取当前位置');
      }

      final response = await _apiClient.post('/api/v1/search/nlp', data: {
        'query': query,
        'longitude': position.longitude,
        'latitude': position.latitude,
        'radius': radius,
        'cityCode': cityCode,
        'category': category,
        'minPrice': minPrice,
        'maxPrice': maxPrice,
        'minRating': minRating,
        'tags': tags,
        'sortBy': sortBy,
        'page': page,
        'size': size,
        'sessionId': _currentSessionId,
        'enableSemantic': true,
        'enablePersonalization': enablePersonalization,
      });

      if (response['code'] == 200) {
        final data = response['data'];
        return SearchResponseModel.fromJson(data);
      } else {
        throw Exception(response['message'] ?? '搜索失败');
      }
    } catch (e) {
      debugPrint('自然语言搜索失败: $e');
      rethrow;
    }
  }

  /// 附近搜索（简化版）
  Future<SearchResponseModel> nearbySearch({
    required String keyword,
    int radius = 5000,
    int page = 0,
    int size = 20,
  }) async {
    try {
      final position = await _locationManager.getCurrentPosition();
      if (position == null) {
        throw Exception('无法获取当前位置');
      }

      final response = await _apiClient.get('/api/v1/search/nearby', queryParameters: {
        'keyword': keyword,
        'longitude': position.longitude,
        'latitude': position.latitude,
        'radius': radius,
        'page': page,
        'size': size,
      });

      if (response['code'] == 200) {
        return SearchResponseModel.fromJson(response['data']);
      } else {
        throw Exception(response['message'] ?? '搜索失败');
      }
    } catch (e) {
      debugPrint('附近搜索失败: $e');
      rethrow;
    }
  }

  /// 语义搜索
  Future<SearchResponseModel> semanticSearch({
    required String query,
    int radius = 5000,
  }) async {
    try {
      final position = await _locationManager.getCurrentPosition();
      if (position == null) {
        throw Exception('无法获取当前位置');
      }

      final response = await _apiClient.get('/api/v1/search/semantic', queryParameters: {
        'query': query,
        'longitude': position.longitude,
        'latitude': position.latitude,
        'radius': radius,
      });

      if (response['code'] == 200) {
        return SearchResponseModel.fromJson(response['data']);
      } else {
        throw Exception(response['message'] ?? '语义搜索失败');
      }
    } catch (e) {
      debugPrint('语义搜索失败: $e');
      rethrow;
    }
  }

  /// 解析搜索意图
  Future<SearchIntentModel> parseIntent(String query) async {
    try {
      final response = await _apiClient.post('/api/v1/search/intent', data: {
        'query': query,
        'sessionId': _currentSessionId,
      });

      if (response['code'] == 200) {
        return SearchIntentModel.fromJson(response['data']);
      } else {
        throw Exception(response['message'] ?? '意图解析失败');
      }
    } catch (e) {
      debugPrint('意图解析失败: $e');
      rethrow;
    }
  }

  /// 获取搜索建议（带防抖）
  Future<List<String>> getSearchSuggestions(String keyword, {String? cityCode}) async {
    // 取消之前的定时器
    _debounceTimer?.cancel();
    
    // 如果关键词为空，返回空列表
    if (keyword.isEmpty) {
      return [];
    }

    // 创建新的防抖定时器
    final completer = Completer<List<String>>();
    
    _debounceTimer = Timer(const Duration(milliseconds: 300), () async {
      try {
        final response = await _apiClient.get('/api/v1/search/suggestions', queryParameters: {
          'keyword': keyword,
          'cityCode': cityCode,
        });

        if (response['code'] == 200) {
          final List<dynamic> suggestions = response['data'] ?? [];
          completer.complete(suggestions.map((e) => e.toString()).toList());
        } else {
          completer.complete([]);
        }
      } catch (e) {
        debugPrint('获取搜索建议失败: $e');
        completer.complete([]);
      }
    });

    return completer.future;
  }

  /// 获取热门搜索
  Future<List<String>> getHotSearches({String? cityCode}) async {
    try {
      final response = await _apiClient.get('/api/v1/search/hot', queryParameters: {
        'cityCode': cityCode,
      });

      if (response['code'] == 200) {
        final List<dynamic> hotSearches = response['data'] ?? [];
        return hotSearches.map((e) => e.toString()).toList();
      } else {
        return [];
      }
    } catch (e) {
      debugPrint('获取热门搜索失败: $e');
      return [];
    }
  }

  /// 多轮对话搜索
  Future<SearchResponseModel> dialogSearch(String query) async {
    try {
      final position = await _locationManager.getCurrentPosition();
      if (position == null) {
        throw Exception('无法获取当前位置');
      }

      final response = await _apiClient.post('/api/v1/search/dialog', data: {
        'query': query,
        'sessionId': _currentSessionId,
        'longitude': position.longitude,
        'latitude': position.latitude,
      });

      if (response['code'] == 200) {
        return SearchResponseModel.fromJson(response['data']);
      } else {
        throw Exception(response['message'] ?? '对话搜索失败');
      }
    } catch (e) {
      debugPrint('对话搜索失败: $e');
      rethrow;
    }
  }

  /// 智能问答
  Future<String> intelligentQA(String question, {String? poiId}) async {
    try {
      final response = await _apiClient.get('/api/v1/search/qa', queryParameters: {
        'question': question,
        'poiId': poiId,
      });

      if (response['code'] == 200) {
        return response['data']?.toString() ?? '';
      } else {
        return '抱歉，暂时无法回答您的问题';
      }
    } catch (e) {
      debugPrint('智能问答失败: $e');
      return '抱歉，暂时无法回答您的问题';
    }
  }

  /// 设置会话ID（用于多轮对话）
  void setSessionId(String? sessionId) {
    _currentSessionId = sessionId;
  }

  /// 清除会话
  void clearSession() {
    _currentSessionId = null;
  }

  /// 获取当前会话ID
  String? get currentSessionId => _currentSessionId;
}
