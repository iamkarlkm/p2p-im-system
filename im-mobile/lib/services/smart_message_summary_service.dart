/// 智能消息摘要服务
/// Smart Message Summary Service for IM Mobile (Flutter)

import 'dart:convert';
import 'package:http/http.dart' as http;

/// 摘要状态枚举
enum SummaryStatus {
  pending,
  processing,
  completed,
  failed,
  expired,
  deleted,
  cancelled,
  needsRegen,
}

/// 摘要类型枚举
enum SummaryType {
  singleMessage,
  conversation,
  groupConversation,
  privateConversation,
  topic,
  timeRange,
  userSpeech,
  keyDecisions,
  actionPlan,
  qna,
  sentiment,
  multilingual,
  custom,
}

/// 摘要质量枚举
enum SummaryQuality {
  low,
  medium,
  high,
  excellent,
}

/// 智能消息摘要模型
class SmartMessageSummary {
  final int id;
  final String sessionId;
  final String? messageId;
  final String userId;
  final SummaryStatus status;
  final SummaryType summaryType;
  final String summaryContent;
  final String? originalContent;
  final String? languageCode;
  final SummaryQuality quality;
  final int qualityScore;
  final int? summaryLength;
  final int? targetLength;
  final int version;
  final String? summaryStyle;
  final List<String>? keyPoints;
  final Map<String, dynamic>? metadata;
  final int? userRating;
  final String? userFeedback;
  final bool isFavorite;
  final bool offlineCached;
  final DateTime? cacheExpiryTime;
  final DateTime createdAt;
  final DateTime updatedAt;
  final DateTime? generatedAt;
  final bool isRead;
  final DateTime? readAt;
  final List<String>? sharedUserIds;
  final List<String>? tags;
  final Map<String, dynamic>? businessData;
  final bool deleted;
  final DateTime? deletedAt;

  SmartMessageSummary({
    required this.id,
    required this.sessionId,
    this.messageId,
    required this.userId,
    required this.status,
    required this.summaryType,
    required this.summaryContent,
    this.originalContent,
    this.languageCode,
    required this.quality,
    required this.qualityScore,
    this.summaryLength,
    this.targetLength,
    this.version = 1,
    this.summaryStyle,
    this.keyPoints,
    this.metadata,
    this.userRating,
    this.userFeedback,
    this.isFavorite = false,
    this.offlineCached = false,
    this.cacheExpiryTime,
    required this.createdAt,
    required this.updatedAt,
    this.generatedAt,
    this.isRead = false,
    this.readAt,
    this.sharedUserIds,
    this.tags,
    this.businessData,
    this.deleted = false,
    this.deletedAt,
  });

  factory SmartMessageSummary.fromJson(Map<String, dynamic> json) {
    return SmartMessageSummary(
      id: json['id'] as int,
      sessionId: json['sessionId'] as String,
      messageId: json['messageId'] as String?,
      userId: json['userId'] as String,
      status: SummaryStatus.values.firstWhere(
        (e) => e.toString().split('.').last == json['status'],
        orElse: () => SummaryStatus.pending,
      ),
      summaryType: SummaryType.values.firstWhere(
        (e) => e.toString().split('.').last == json['summaryType'],
        orElse: () => SummaryType.singleMessage,
      ),
      summaryContent: json['summaryContent'] as String,
      originalContent: json['originalContent'] as String?,
      languageCode: json['languageCode'] as String?,
      quality: SummaryQuality.values.firstWhere(
        (e) => e.toString().split('.').last == json['quality'],
        orElse: () => SummaryQuality.medium,
      ),
      qualityScore: json['qualityScore'] as int? ?? 70,
      summaryLength: json['summaryLength'] as int?,
      targetLength: json['targetLength'] as int?,
      version: json['version'] as int? ?? 1,
      summaryStyle: json['summaryStyle'] as String?,
      keyPoints: json['keyPoints'] != null 
          ? List<String>.from(json['keyPoints']) 
          : null,
      metadata: json['metadata'] as Map<String, dynamic>?,
      userRating: json['userRating'] as int?,
      userFeedback: json['userFeedback'] as String?,
      isFavorite: json['isFavorite'] as bool? ?? false,
      offlineCached: json['offlineCached'] as bool? ?? false,
      cacheExpiryTime: json['cacheExpiryTime'] != null
          ? DateTime.parse(json['cacheExpiryTime'])
          : null,
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
      generatedAt: json['generatedAt'] != null
          ? DateTime.parse(json['generatedAt'])
          : null,
      isRead: json['isRead'] as bool? ?? false,
      readAt: json['readAt'] != null ? DateTime.parse(json['readAt']) : null,
      sharedUserIds: json['sharedUserIds'] != null
          ? List<String>.from(json['sharedUserIds'])
          : null,
      tags: json['tags'] != null ? List<String>.from(json['tags']) : null,
      businessData: json['businessData'] as Map<String, dynamic>?,
      deleted: json['deleted'] as bool? ?? false,
      deletedAt: json['deletedAt'] != null
          ? DateTime.parse(json['deletedAt'])
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'sessionId': sessionId,
      'messageId': messageId,
      'userId': userId,
      'status': status.toString().split('.').last,
      'summaryType': summaryType.toString().split('.').last,
      'summaryContent': summaryContent,
      'originalContent': originalContent,
      'languageCode': languageCode,
      'quality': quality.toString().split('.').last,
      'qualityScore': qualityScore,
      'summaryLength': summaryLength,
      'targetLength': targetLength,
      'version': version,
      'summaryStyle': summaryStyle,
      'keyPoints': keyPoints,
      'metadata': metadata,
      'userRating': userRating,
      'userFeedback': userFeedback,
      'isFavorite': isFavorite,
      'offlineCached': offlineCached,
      'cacheExpiryTime': cacheExpiryTime?.toIso8601String(),
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'generatedAt': generatedAt?.toIso8601String(),
      'isRead': isRead,
      'readAt': readAt?.toIso8601String(),
      'sharedUserIds': sharedUserIds,
      'tags': tags,
      'businessData': businessData,
      'deleted': deleted,
      'deletedAt': deletedAt?.toIso8601String(),
    };
  }

  /// 检查是否为高质量摘要
  bool get isHighQuality => quality == SummaryQuality.high || 
                            quality == SummaryQuality.excellent || 
                            qualityScore >= 80;

  /// 检查是否需要重新生成
  bool get needsRegeneration => qualityScore < 60 || 
                                status == SummaryStatus.needsRegen;

  /// 检查是否已过期
  bool get isExpired => cacheExpiryTime != null && 
                        DateTime.now().isAfter(cacheExpiryTime!);

  /// 获取关键点数量
  int get keyPointsCount => keyPoints?.length ?? 0;
}

/// 智能消息摘要 API 服务
class SmartMessageSummaryService {
  static const String _baseUrl = '/api/v1/smart-summary';
  final http.Client _client;

  SmartMessageSummaryService({http.Client? client}) 
      : _client = client ?? http.Client();

  // ==================== CRUD 操作 ====================

  /// 创建摘要
  Future<SmartMessageSummary> createSummary(Map<String, dynamic> request) async {
    final response = await _client.post(
      Uri.parse(_baseUrl),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return SmartMessageSummary.fromJson(data['data']);
    } else {
      throw Exception('创建摘要失败：${response.body}');
    }
  }

  /// 获取摘要详情
  Future<SmartMessageSummary> getSummary(int id) async {
    final response = await _client.get(Uri.parse('$_baseUrl/$id'));

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return SmartMessageSummary.fromJson(data['data']);
    } else {
      throw Exception('获取摘要失败：${response.body}');
    }
  }

  /// 更新摘要
  Future<SmartMessageSummary> updateSummary(
    int id, 
    Map<String, dynamic> request
  ) async {
    final response = await _client.put(
      Uri.parse('$_baseUrl/$id'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return SmartMessageSummary.fromJson(data['data']);
    } else {
      throw Exception('更新摘要失败：${response.body}');
    }
  }

  /// 删除摘要
  Future<void> deleteSummary(int id) async {
    final response = await _client.delete(Uri.parse('$_baseUrl/$id'));

    if (response.statusCode != 200) {
      throw Exception('删除摘要失败：${response.body}');
    }
  }

  // ==================== 查询操作 ====================

  /// 查询用户摘要列表
  Future<List<SmartMessageSummary>> getUserSummaries({
    required String userId,
    int page = 0,
    int size = 20,
    SummaryStatus? status,
    SummaryQuality? quality,
    SummaryType? summaryType,
  }) async {
    final params = {
      'page': page.toString(),
      'size': size.toString(),
      if (status != null) 'status': status.toString().split('.').last,
      if (quality != null) 'quality': quality.toString().split('.').last,
      if (summaryType != null) 'summaryType': summaryType.toString().split('.').last,
    };

    final uri = Uri.parse('$_baseUrl/user/$userId').replace(queryParameters: params);
    final response = await _client.get(uri);

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final List summaries = data['data']['summaries'];
      return summaries.map((s) => SmartMessageSummary.fromJson(s)).toList();
    } else {
      throw Exception('查询用户摘要失败：${response.body}');
    }
  }

  /// 查询会话摘要列表
  Future<List<SmartMessageSummary>> getSessionSummaries({
    required String sessionId,
    int page = 0,
    int size = 20,
  }) async {
    final params = {
      'page': page.toString(),
      'size': size.toString(),
    };

    final uri = Uri.parse('$_baseUrl/session/$sessionId').replace(queryParameters: params);
    final response = await _client.get(uri);

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final List summaries = data['data']['summaries'];
      return summaries.map((s) => SmartMessageSummary.fromJson(s)).toList();
    } else {
      throw Exception('查询会话摘要失败：${response.body}');
    }
  }

  /// 搜索摘要
  Future<List<SmartMessageSummary>> searchSummaries({
    required String keyword,
    String? userId,
    int page = 0,
    int size = 20,
  }) async {
    final params = {
      'keyword': keyword,
      if (userId != null) 'userId': userId,
      'page': page.toString(),
      'size': size.toString(),
    };

    final response = await _client.get(
      Uri.parse('$_baseUrl/search').replace(queryParameters: params)
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final List summaries = data['data']['summaries'];
      return summaries.map((s) => SmartMessageSummary.fromJson(s)).toList();
    } else {
      throw Exception('搜索摘要失败：${response.body}');
    }
  }

  // ==================== 摘要生成 ====================

  /// 生成消息摘要
  Future<SmartMessageSummary> generateSummary({
    required String sessionId,
    required String userId,
    required SummaryType summaryType,
    required String originalContent,
  }) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/generate'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'sessionId': sessionId,
        'userId': userId,
        'summaryType': summaryType.toString().split('.').last,
        'originalContent': originalContent,
      }),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return SmartMessageSummary.fromJson(data['data']);
    } else {
      throw Exception('生成摘要失败：${response.body}');
    }
  }

  /// 重新生成摘要
  Future<SmartMessageSummary> regenerateSummary(int id) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/$id/regenerate'),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return SmartMessageSummary.fromJson(data['data']);
    } else {
      throw Exception('重新生成摘要失败：${response.body}');
    }
  }

  // ==================== 便捷方法 ====================

  /// 生成会话摘要
  Future<SmartMessageSummary> summarizeConversation({
    required String sessionId,
    required String userId,
    required String content,
  }) {
    return generateSummary(
      sessionId: sessionId,
      userId: userId,
      summaryType: SummaryType.conversation,
      originalContent: content,
    );
  }

  /// 生成群聊摘要
  Future<SmartMessageSummary> summarizeGroupChat({
    required String sessionId,
    required String userId,
    required String content,
  }) {
    return generateSummary(
      sessionId: sessionId,
      userId: userId,
      summaryType: SummaryType.groupConversation,
      originalContent: content,
    );
  }

  /// 提取关键决策
  Future<SmartMessageSummary> extractKeyDecisions({
    required String sessionId,
    required String userId,
    required String content,
  }) {
    return generateSummary(
      sessionId: sessionId,
      userId: userId,
      summaryType: SummaryType.keyDecisions,
      originalContent: content,
    );
  }

  /// 提取行动计划
  Future<SmartMessageSummary> extractActionPlan({
    required String sessionId,
    required String userId,
    required String content,
  }) {
    return generateSummary(
      sessionId: sessionId,
      userId: userId,
      summaryType: SummaryType.actionPlan,
      originalContent: content,
    );
  }

  /// 更新用户反馈
  Future<SmartMessageSummary> submitFeedback({
    required int id,
    required int rating,
    String? feedback,
  }) {
    return updateSummary(id, {
      'userRating': rating,
      'userFeedback': feedback,
    });
  }

  /// 切换喜欢状态
  Future<SmartMessageSummary> toggleFavorite(int id, bool currentStatus) {
    return updateSummary(id, {'isFavorite': !currentStatus});
  }

  /// 标记为已读
  Future<void> markAsRead(int id) async {
    await _client.put(
      Uri.parse('$_baseUrl/$id/read'),
    );
  }

  /// 清理过期缓存
  Future<int> cleanupExpiredCache() async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/maintenance/cleanup-cache'),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return data['data']['cleanedCount'] as int;
    } else {
      throw Exception('清理缓存失败：${response.body}');
    }
  }
}