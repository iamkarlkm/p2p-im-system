import 'package:dio/dio.dart';
import '../models/semantic_search_models.dart';
import '../models/conversation_models.dart';

/// 搜索服务
/// 提供语义搜索、多轮对话、POI问答等功能
/// 
/// Author: IM Development Team
/// Version: 1.0.0
/// Since: 2026-03-28
class SearchService {
  static final SearchService _instance = SearchService._internal();
  factory SearchService() => _instance;
  SearchService._internal();

  final Dio _dio = Dio(BaseOptions(
    baseUrl: 'https://api.im.example.com',
    connectTimeout: const Duration(seconds: 10),
    receiveTimeout: const Duration(seconds: 15),
  ));

  /// 当前会话ID
  String? _currentSessionId;
  
  /// 当前会话
  ConversationSession? _currentSession;

  /// 获取当前会话ID
  String? get currentSessionId => _currentSessionId;
  
  /// 获取当前会话
  ConversationSession? get currentSession => _currentSession;

  /// 语义搜索
  /// 
  /// [query] 搜索查询
  /// [longitude] 经度
  /// [latitude] 纬度
  /// [isVoiceInput] 是否为语音输入
  Future<SemanticSearchResponse> semanticSearch({
    required String query,
    required double longitude,
    required double latitude,
    bool isVoiceInput = false,
    String? voiceData,
    SearchFilter? filters,
    String? sortBy,
    int pageNum = 1,
    int pageSize = 20,
  }) async {
    final request = SemanticSearchRequest(
      query: query,
      sessionId: _currentSessionId,
      longitude: longitude,
      latitude: latitude,
      isVoiceInput: isVoiceInput,
      voiceData: voiceData,
      filters: filters,
      sortBy: sortBy,
      pageNum: pageNum,
      pageSize: pageSize,
    );

    try {
      final response = await _dio.post(
        '/api/v1/search/semantic',
        data: request.toJson(),
      );

      final result = SemanticSearchResponse.fromJson(response.data['data']);
      
      // 更新会话ID
      if (result.sessionId != null) {
        _currentSessionId = result.sessionId;
      }

      return result;
    } catch (e) {
      throw Exception('语义搜索失败: $e');
    }
  }

  /// 多轮对话搜索
  /// 
  /// [query] 跟进查询
  Future<SemanticSearchResponse> multiTurnSearch(String query) async {
    if (_currentSessionId == null) {
      throw Exception('没有活跃的会话，请先进行首次搜索');
    }

    try {
      final response = await _dio.post(
        '/api/v1/search/semantic/session/$_currentSessionId',
        data: {
          'query': query,
        },
      );

      return SemanticSearchResponse.fromJson(response.data['data']);
    } catch (e) {
      throw Exception('多轮搜索失败: $e');
    }
  }

  /// POI智能问答
  /// 
  /// [poiId] POI ID
  /// [question] 问题
  Future<POIQAResponse> poiQA({
    required int poiId,
    required String question,
    bool isVoiceInput = false,
    String? voiceData,
  }) async {
    try {
      final response = await _dio.post(
        '/api/v1/search/poi-qa',
        data: {
          'poiId': poiId,
          'question': question,
          'sessionId': _currentSessionId,
          'isVoiceInput': isVoiceInput,
          'voiceData': voiceData,
        },
      );

      return POIQAResponse.fromJson(response.data['data']);
    } catch (e) {
      throw Exception('POI问答失败: $e');
    }
  }

  /// 获取搜索建议
  /// 
  /// [query] 部分查询文本
  Future<List<String>> getSearchSuggestions(String query) async {
    if (query.length < 2) return [];

    try {
      final response = await _dio.get(
        '/api/v1/search/suggestions',
        queryParameters: {'query': query},
      );

      return (response.data['data'] as List).cast<String>();
    } catch (e) {
      return [];
    }
  }

  /// 获取热门搜索
  /// 
  /// [cityCode] 城市代码
  /// [limit] 数量限制
  Future<List<String>> getHotSearches({
    String? cityCode,
    int limit = 10,
  }) async {
    try {
      final response = await _dio.get(
        '/api/v1/search/hot',
        queryParameters: {
          'cityCode': cityCode,
          'limit': limit,
        },
      );

      return (response.data['data'] as List).cast<String>();
    } catch (e) {
      return [];
    }
  }

  /// 获取个性化推荐
  /// 
  /// [limit] 数量限制
  Future<List<String>> getPersonalizedSuggestions({int limit = 5}) async {
    try {
      final response = await _dio.get(
        '/api/v1/search/personalized',
        queryParameters: {'limit': limit},
      );

      return (response.data['data'] as List).cast<String>();
    } catch (e) {
      return [];
    }
  }

  /// 结束当前会话
  Future<void> endSession() async {
    if (_currentSessionId == null) return;

    try {
      await _dio.post('/api/v1/search/session/$_currentSessionId/end');
      _currentSessionId = null;
      _currentSession = null;
    } catch (e) {
      // 忽略错误
    }
  }

  /// 创建新会话
  void startNewSession() {
    _currentSessionId = null;
    _currentSession = null;
  }
}

/// POI问答响应
class POIQAResponse {
  /// 响应状态
  final String status;
  
  /// 会话ID
  final String? sessionId;
  
  /// 问题类型
  final String? questionType;
  
  /// 系统回答
  final String answer;
  
  /// 详细答案
  final Map<String, dynamic>? detailedAnswer;
  
  /// 回答置信度
  final double confidence;
  
  /// 是否为实时信息
  final bool isRealTime;
  
  /// 建议操作
  final List<String>? suggestedActions;
  
  /// 相关问题
  final List<String>? relatedQuestions;
  
  /// 是否需要转人工
  final bool needsHumanTransfer;
  
  /// 转人工原因
  final String? transferReason;
  
  /// 响应时间
  final int responseTimeMs;

  POIQAResponse({
    required this.status,
    this.sessionId,
    this.questionType,
    required this.answer,
    this.detailedAnswer,
    required this.confidence,
    this.isRealTime = false,
    this.suggestedActions,
    this.relatedQuestions,
    this.needsHumanTransfer = false,
    this.transferReason,
    this.responseTimeMs = 0,
  });

  factory POIQAResponse.fromJson(Map<String, dynamic> json) => POIQAResponse(
    status: json['status'],
    sessionId: json['sessionId'],
    questionType: json['questionType'],
    answer: json['answer'],
    detailedAnswer: json['detailedAnswer'],
    confidence: json['confidence'] ?? 0.0,
    isRealTime: json['isRealTime'] ?? false,
    suggestedActions: json['suggestedActions']?.cast<String>(),
    relatedQuestions: json['relatedQuestions']?.cast<String>(),
    needsHumanTransfer: json['needsHumanTransfer'] ?? false,
    transferReason: json['transferReason'],
    responseTimeMs: json['responseTimeMs'] ?? 0,
  );

  /// 是否成功
  bool get isSuccess => status == 'SUCCESS';
  
  /// 是否高置信度
  bool get isHighConfidence => confidence >= 0.8;
}
