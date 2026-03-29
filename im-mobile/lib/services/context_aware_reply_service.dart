import 'dart:convert';
import 'package:http/http.dart' as http;

/// 意图枚举
enum ReplyIntent {
  BUSINESS,
  PERSONAL,
  SOCIAL,
  QUESTION,
  GREETING,
  APPRECIATION,
  EMOTIONAL,
}

/// 语言风格枚举
enum LanguageStyle {
  FORMAL,
  CASUAL,
  FRIENDLY,
  PROFESSIONAL,
}

/// 回复长度枚举
enum ReplyLength {
  SHORT,
  MEDIUM,
  LONG,
}

/// 状态枚举
enum ReplyStatus {
  GENERATED,
  SELECTED,
  REJECTED,
  EXPIRED,
}

/// 上下文感知智能回复生成器模型
class ContextAwareReply {
  final int id;
  final String userId;
  final String? sessionId;
  final String? triggerMessageId;
  final String? triggerMessageContent;
  final String? contextSummary;
  final String? detectedIntent;
  final double? intentConfidence;
  final List<String>? replyCandidates;
  final String? selectedReply;
  final List<String>? recommendedEmojis;
  final String? languageStyle;
  final String? replyLength;
  final String? sensitivityCheckResult;
  final bool? sensitivityPassed;
  final Map<String, dynamic>? personalizationFeatures;
  final int? userFeedbackScore;
  final String? userFeedbackComment;
  final bool? used;
  final int? generationTimeMs;
  final String? modelVersion;
  final Map<String, dynamic>? generationOptions;
  final String status;
  final DateTime? expiresAt;
  final DateTime? createdAt;
  final DateTime? updatedAt;
  final String? indexKey;

  ContextAwareReply({
    required this.id,
    required this.userId,
    this.sessionId,
    this.triggerMessageId,
    this.triggerMessageContent,
    this.contextSummary,
    this.detectedIntent,
    this.intentConfidence,
    this.replyCandidates,
    this.selectedReply,
    this.recommendedEmojis,
    this.languageStyle,
    this.replyLength,
    this.sensitivityCheckResult,
    this.sensitivityPassed,
    this.personalizationFeatures,
    this.userFeedbackScore,
    this.userFeedbackComment,
    this.used,
    this.generationTimeMs,
    this.modelVersion,
    this.generationOptions,
    required this.status,
    this.expiresAt,
    this.createdAt,
    this.updatedAt,
    this.indexKey,
  });

  factory ContextAwareReply.fromJson(Map<String, dynamic> json) {
    return ContextAwareReply(
      id: json['id'] ?? 0,
      userId: json['userId'] ?? '',
      sessionId: json['sessionId'],
      triggerMessageId: json['triggerMessageId'],
      triggerMessageContent: json['triggerMessageContent'],
      contextSummary: json['contextSummary'],
      detectedIntent: json['detectedIntent'],
      intentConfidence: json['intentConfidence']?.toDouble(),
      replyCandidates: json['replyCandidates'] != null
          ? List<String>.from(json['replyCandidates'])
          : null,
      selectedReply: json['selectedReply'],
      recommendedEmojis: json['recommendedEmojis'] != null
          ? List<String>.from(json['recommendedEmojis'])
          : null,
      languageStyle: json['languageStyle'],
      replyLength: json['replyLength'],
      sensitivityCheckResult: json['sensitivityCheckResult'],
      sensitivityPassed: json['sensitivityPassed'],
      personalizationFeatures: json['personalizationFeatures'] != null
          ? Map<String, dynamic>.from(json['personalizationFeatures'])
          : null,
      userFeedbackScore: json['userFeedbackScore'],
      userFeedbackComment: json['userFeedbackComment'],
      used: json['used'],
      generationTimeMs: json['generationTimeMs'],
      modelVersion: json['modelVersion'],
      generationOptions: json['generationOptions'] != null
          ? Map<String, dynamic>.from(json['generationOptions'])
          : null,
      status: json['status'] ?? 'GENERATED',
      expiresAt: json['expiresAt'] != null
          ? DateTime.parse(json['expiresAt'])
          : null,
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'])
          : null,
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'])
          : null,
      indexKey: json['indexKey'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'sessionId': sessionId,
      'triggerMessageId': triggerMessageId,
      'triggerMessageContent': triggerMessageContent,
      'contextSummary': contextSummary,
      'detectedIntent': detectedIntent,
      'intentConfidence': intentConfidence,
      'replyCandidates': replyCandidates,
      'selectedReply': selectedReply,
      'recommendedEmojis': recommendedEmojis,
      'languageStyle': languageStyle,
      'replyLength': replyLength,
      'sensitivityCheckResult': sensitivityCheckResult,
      'sensitivityPassed': sensitivityPassed,
      'personalizationFeatures': personalizationFeatures,
      'userFeedbackScore': userFeedbackScore,
      'userFeedbackComment': userFeedbackComment,
      'used': used,
      'generationTimeMs': generationTimeMs,
      'modelVersion': modelVersion,
      'generationOptions': generationOptions,
      'status': status,
      'expiresAt': expiresAt?.toIso8601String(),
      'createdAt': createdAt?.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
      'indexKey': indexKey,
    };
  }

  bool get isHighQuality => (userFeedbackScore ?? 0) >= 4;
  bool get isExpired => expiresAt != null && DateTime.now().isAfter(expiresAt!);
  bool get isUsable =>
      status == 'GENERATED' && !isExpired && (sensitivityPassed ?? true);
}

/// 生成回复请求
class GenerateReplyRequest {
  final String userId;
  final String? sessionId;
  final String triggerMessageContent;
  final Map<String, dynamic>? context;
  final String? languageStyle;
  final String? replyLength;
  final Map<String, dynamic>? generationOptions;

  GenerateReplyRequest({
    required this.userId,
    this.sessionId,
    required this.triggerMessageContent,
    this.context,
    this.languageStyle,
    this.replyLength,
    this.generationOptions,
  });

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'sessionId': sessionId,
      'triggerMessageContent': triggerMessageContent,
      'context': context,
      'languageStyle': languageStyle,
      'replyLength': replyLength,
      'generationOptions': generationOptions,
    };
  }
}

/// 反馈请求
class FeedbackRequest {
  final int score;
  final String? comment;

  FeedbackRequest({
    required this.score,
    this.comment,
  });

  Map<String, dynamic> toJson() {
    return {
      'score': score,
      'comment': comment,
    };
  }

  bool get isValid => score >= 1 && score <= 5;
}

/// 上下文感知智能回复生成器 API 服务
class ContextAwareReplyApiService {
  final String baseUrl;
  final http.Client _client;
  String? _authToken;

  ContextAwareReplyApiService({
    this.baseUrl = '/api/v1',
    http.Client? client,
  })  : _client = client ?? http.Client();

  void setAuthToken(String token) {
    _authToken = token;
  }

  Map<String, String> get _headers => {
        'Content-Type': 'application/json',
        if (_authToken != null) 'Authorization': 'Bearer $_authToken',
      };

  // ==================== 基础 CRUD 操作 ====================

  /// 创建智能回复记录
  Future<ContextAwareReply> createReply(ContextAwareReply reply) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/context-aware-reply'),
      headers: _headers,
      body: jsonEncode(reply.toJson()),
    );
    _checkError(response);
    return ContextAwareReply.fromJson(jsonDecode(response.body));
  }

  /// 获取回复详情
  Future<ContextAwareReply> getReplyById(int id) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/$id'),
      headers: _headers,
    );
    _checkError(response);
    return ContextAwareReply.fromJson(jsonDecode(response.body));
  }

  /// 更新回复记录
  Future<ContextAwareReply> updateReply(
      int id, Map<String, dynamic> updateData) async {
    final response = await _client.put(
      Uri.parse('$baseUrl/context-aware-reply/$id'),
      headers: _headers,
      body: jsonEncode(updateData),
    );
    _checkError(response);
    return ContextAwareReply.fromJson(jsonDecode(response.body));
  }

  /// 删除回复记录
  Future<void> deleteReply(int id) async {
    final response = await _client.delete(
      Uri.parse('$baseUrl/context-aware-reply/$id'),
      headers: _headers,
    );
    _checkError(response);
  }

  // ==================== 查询操作 ====================

  /// 获取用户的回复记录
  Future<List<ContextAwareReply>> getRepliesByUser(String userId) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/user/$userId'),
      headers: _headers,
    );
    _checkError(response);
    final List<dynamic> jsonList = jsonDecode(response.body);
    return jsonList.map((json) => ContextAwareReply.fromJson(json)).toList();
  }

  /// 获取会话的回复记录
  Future<List<ContextAwareReply>> getRepliesBySession(String sessionId) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/session/$sessionId'),
      headers: _headers,
    );
    _checkError(response);
    final List<dynamic> jsonList = jsonDecode(response.body);
    return jsonList.map((json) => ContextAwareReply.fromJson(json)).toList();
  }

  /// 获取用户和会话的回复记录
  Future<List<ContextAwareReply>> getRepliesByUserAndSession(
      String userId, String sessionId) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/user/$userId/session/$sessionId'),
      headers: _headers,
    );
    _checkError(response);
    final List<dynamic> jsonList = jsonDecode(response.body);
    return jsonList.map((json) => ContextAwareReply.fromJson(json)).toList();
  }

  /// 获取指定状态的回复记录
  Future<List<ContextAwareReply>> getRepliesByStatus(String status) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/status/$status'),
      headers: _headers,
    );
    _checkError(response);
    final List<dynamic> jsonList = jsonDecode(response.body);
    return jsonList.map((json) => ContextAwareReply.fromJson(json)).toList();
  }

  /// 获取已使用的回复记录
  Future<List<ContextAwareReply>> getUsedReplies() async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/used'),
      headers: _headers,
    );
    _checkError(response);
    final List<dynamic> jsonList = jsonDecode(response.body);
    return jsonList.map((json) => ContextAwareReply.fromJson(json)).toList();
  }

  /// 获取高质量的回复记录
  Future<List<ContextAwareReply>> getHighQualityReplies() async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/high-quality'),
      headers: _headers,
    );
    _checkError(response);
    final List<dynamic> jsonList = jsonDecode(response.body);
    return jsonList.map((json) => ContextAwareReply.fromJson(json)).toList();
  }

  // ==================== 意图相关查询 ====================

  /// 获取指定意图的回复记录
  Future<List<ContextAwareReply>> getRepliesByIntent(String intent) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/intent/$intent'),
      headers: _headers,
    );
    _checkError(response);
    final List<dynamic> jsonList = jsonDecode(response.body);
    return jsonList.map((json) => ContextAwareReply.fromJson(json)).toList();
  }

  /// 获取用户指定意图的回复记录
  Future<List<ContextAwareReply>> getRepliesByUserAndIntent(
      String userId, String intent) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/user/$userId/intent/$intent'),
      headers: _headers,
    );
    _checkError(response);
    final List<dynamic> jsonList = jsonDecode(response.body);
    return jsonList.map((json) => ContextAwareReply.fromJson(json)).toList();
  }

  /// 获取用户最常用的意图
  Future<Map<String, int>> getUserTopIntents(String userId) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/user/$userId/top-intents'),
      headers: _headers,
    );
    _checkError(response);
    final Map<String, dynamic> jsonMap = jsonDecode(response.body);
    return jsonMap.map((key, value) => MapEntry(key, value as int));
  }

  // ==================== 统计操作 ====================

  /// 统计用户回复记录数量
  Future<int> countRepliesByUser(String userId) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/count/user/$userId'),
      headers: _headers,
    );
    _checkError(response);
    return jsonDecode(response.body) as int;
  }

  /// 统计已使用回复记录数量
  Future<int> countUsedReplies() async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/count/used'),
      headers: _headers,
    );
    _checkError(response);
    return jsonDecode(response.body) as int;
  }

  /// 统计高质量回复记录数量
  Future<int> countHighQualityReplies() async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/count/high-quality'),
      headers: _headers,
    );
    _checkError(response);
    return jsonDecode(response.body) as int;
  }

  /// 获取平均反馈评分
  Future<double> getAverageFeedbackScore() async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/stats/average-feedback-score'),
      headers: _headers,
    );
    _checkError(response);
    return (jsonDecode(response.body) as num).toDouble();
  }

  /// 获取平均生成时间
  Future<double> getAverageGenerationTime() async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/stats/average-generation-time'),
      headers: _headers,
    );
    _checkError(response);
    return (jsonDecode(response.body) as num).toDouble();
  }

  // ==================== 高级操作 ====================

  /// 标记回复为已使用
  Future<void> markAsUsed(int id) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/context-aware-reply/$id/mark-used'),
      headers: _headers,
    );
    _checkError(response);
  }

  /// 提交用户反馈
  Future<void> submitFeedback(int id, int score, String? comment) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/context-aware-reply/$id/feedback'),
      headers: _headers,
      body: jsonEncode({'score': score, 'comment': comment}),
    );
    _checkError(response);
  }

  /// 获取最近 N 条用户回复
  Future<List<ContextAwareReply>> getRecentRepliesByUser(
      String userId, int limit) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/user/$userId/recent?limit=$limit'),
      headers: _headers,
    );
    _checkError(response);
    final List<dynamic> jsonList = jsonDecode(response.body);
    return jsonList.map((json) => ContextAwareReply.fromJson(json)).toList();
  }

  // ==================== 内容获取 ====================

  /// 获取回复候选列表
  Future<List<String>> getReplyCandidates(int id) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/$id/candidates'),
      headers: _headers,
    );
    _checkError(response);
    final List<dynamic> jsonList = jsonDecode(response.body);
    return jsonList.map((item) => item as String).toList();
  }

  /// 获取推荐的表情符号列表
  Future<List<String>> getRecommendedEmojis(int id) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/$id/emojis'),
      headers: _headers,
    );
    _checkError(response);
    final List<dynamic> jsonList = jsonDecode(response.body);
    return jsonList.map((item) => item as String).toList();
  }

  // ==================== 智能生成 ====================

  /// 生成智能回复
  Future<ContextAwareReply> generateReply(GenerateReplyRequest request) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/context-aware-reply/generate'),
      headers: _headers,
      body: jsonEncode({
        'userId': request.userId,
        'sessionId': request.sessionId,
        'triggerMessageContent': request.triggerMessageContent,
      }),
    );
    _checkError(response);
    return ContextAwareReply.fromJson(jsonDecode(response.body));
  }

  // ==================== 健康检查 ====================

  /// 健康检查
  Future<Map<String, dynamic>> healthCheck() async {
    final response = await _client.get(
      Uri.parse('$baseUrl/context-aware-reply/health'),
      headers: _headers,
    );
    _checkError(response);
    return jsonDecode(response.body) as Map<String, dynamic>;
  }

  void _checkError(http.Response response) {
    if (response.statusCode >= 400) {
      throw ApiException(
        statusCode: response.statusCode,
        message: response.body,
      );
    }
  }

  void dispose() {
    _client.close();
  }
}

/// API 异常
class ApiException implements Exception {
  final int statusCode;
  final String? message;

  ApiException({required this.statusCode, this.message});

  @override
  String toString() {
    return 'ApiException(statusCode: $statusCode, message: $message)';
  }
}

// 工具函数
extension ReplyIntentExtension on ReplyIntent {
  String get value => name;
  
  static ReplyIntent fromString(String value) {
    return ReplyIntent.values.firstWhere(
      (e) => e.name == value,
      orElse: () => ReplyIntent.SOCIAL,
    );
  }
  
  String get displayName {
    switch (this) {
      case ReplyIntent.BUSINESS:
        return '商务';
      case ReplyIntent.PERSONAL:
        return '个人';
      case ReplyIntent.SOCIAL:
        return '社交';
      case ReplyIntent.QUESTION:
        return '提问';
      case ReplyIntent.GREETING:
        return '问候';
      case ReplyIntent.APPRECIATION:
        return '感谢';
      case ReplyIntent.EMOTIONAL:
        return '情感';
    }
  }
}

extension LanguageStyleExtension on LanguageStyle {
  String get value => name;
  
  static LanguageStyle fromString(String value) {
    return LanguageStyle.values.firstWhere(
      (e) => e.name == value,
      orElse: () => LanguageStyle.CASUAL,
    );
  }
  
  String get displayName {
    switch (this) {
      case LanguageStyle.FORMAL:
        return '正式';
      case LanguageStyle.CASUAL:
        return '随意';
      case LanguageStyle.FRIENDLY:
        return '友好';
      case LanguageStyle.PROFESSIONAL:
        return '专业';
    }
  }
}

extension ReplyStatusExtension on ReplyStatus {
  String get value => name;
  
  static ReplyStatus fromString(String value) {
    return ReplyStatus.values.firstWhere(
      (e) => e.name == value,
      orElse: () => ReplyStatus.GENERATED,
    );
  }
  
  String get displayName {
    switch (this) {
      case ReplyStatus.GENERATED:
        return '已生成';
      case ReplyStatus.SELECTED:
        return '已选择';
      case ReplyStatus.REJECTED:
        return '已拒绝';
      case ReplyStatus.EXPIRED:
        return '已过期';
    }
  }
}