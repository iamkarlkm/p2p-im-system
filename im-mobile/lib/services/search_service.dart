import 'dart:convert';
import 'package:http/http.dart' as http;

/// Message Search Service
///
/// 消息搜索服务 - Flutter 移动端实现
/// 提供全文搜索、搜索建议、搜索历史等功能

// ==================== 类型定义 ====================

class SearchHit {
  final int messageId;
  final int conversationId;
  final int conversationType; // 1-私聊 2-群聊
  final int senderId;
  final String senderNickname;
  final int messageType;
  final String content; // 摘要/片段
  final String fullContent; // 完整高亮内容
  final String? fileName;
  final DateTime messageTime;

  SearchHit({
    required this.messageId,
    required this.conversationId,
    required this.conversationType,
    required this.senderId,
    required this.senderNickname,
    required this.messageType,
    required this.content,
    required this.fullContent,
    this.fileName,
    required this.messageTime,
  });

  factory SearchHit.fromJson(Map<String, dynamic> json) {
    return SearchHit(
      messageId: json['messageId'] ?? 0,
      conversationId: json['conversationId'] ?? 0,
      conversationType: json['conversationType'] ?? 1,
      senderId: json['senderId'] ?? 0,
      senderNickname: json['senderNickname'] ?? '',
      messageType: json['messageType'] ?? 1,
      content: json['content'] ?? '',
      fullContent: json['fullContent'] ?? '',
      fileName: json['fileName'],
      messageTime: json['messageTime'] != null
          ? DateTime.parse(json['messageTime'])
          : DateTime.now(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'messageId': messageId,
      'conversationId': conversationId,
      'conversationType': conversationType,
      'senderId': senderId,
      'senderNickname': senderNickname,
      'messageType': messageType,
      'content': content,
      'fullContent': fullContent,
      'fileName': fileName,
      'messageTime': messageTime.toIso8601String(),
    };
  }
}

class SearchResult {
  final List<SearchHit> hits;
  final int total;
  final int page;
  final int size;
  final int totalPages;
  final bool hasNext;
  final bool hasPrevious;
  final String keyword;

  SearchResult({
    required this.hits,
    required this.total,
    required this.page,
    required this.size,
    required this.totalPages,
    required this.hasNext,
    required this.hasPrevious,
    required this.keyword,
  });

  factory SearchResult.empty() {
    return SearchResult(
      hits: [],
      total: 0,
      page: 0,
      size: 0,
      totalPages: 0,
      hasNext: false,
      hasPrevious: false,
      keyword: '',
    );
  }

  factory SearchResult.fromJson(Map<String, dynamic> json) {
    return SearchResult(
      hits: (json['hits'] as List<dynamic>?)
              ?.map((e) => SearchHit.fromJson(e as Map<String, dynamic>))
              .toList() ??
          [],
      total: json['total'] ?? 0,
      page: json['page'] ?? 0,
      size: json['size'] ?? 0,
      totalPages: json['totalPages'] ?? 0,
      hasNext: json['hasNext'] ?? false,
      hasPrevious: json['hasPrevious'] ?? false,
      keyword: json['keyword'] ?? '',
    );
  }
}

class SearchRequest {
  final String keyword;
  final int? userId;
  final int page;
  final int size;

  SearchRequest({
    required this.keyword,
    this.userId,
    this.page = 0,
    this.size = 20,
  });

  Map<String, dynamic> toJson() {
    return {
      'keyword': keyword,
      if (userId != null) 'userId': userId,
      'page': page,
      'size': size,
    };
  }
}

class AdvancedSearchRequest {
  final String? keyword;
  final int? conversationId;
  final int? conversationType;
  final int? senderId;
  final int? messageType;
  final DateTime? startTime;
  final DateTime? endTime;
  final int page;
  final int size;

  AdvancedSearchRequest({
    this.keyword,
    this.conversationId,
    this.conversationType,
    this.senderId,
    this.messageType,
    this.startTime,
    this.endTime,
    this.page = 0,
    this.size = 20,
  });

  Map<String, dynamic> toJson() {
    return {
      if (keyword != null) 'keyword': keyword,
      if (conversationId != null) 'conversationId': conversationId,
      if (conversationType != null) 'conversationType': conversationType,
      if (senderId != null) 'senderId': senderId,
      if (messageType != null) 'messageType': messageType,
      if (startTime != null) 'startTime': startTime!.toIso8601String(),
      if (endTime != null) 'endTime': endTime!.toIso8601String(),
      'page': page,
      'size': size,
    };
  }
}

// ==================== API 配置 ====================

class SearchApiConfig {
  static const String baseUrl = 'http://localhost:8080/api';

  // 端点
  static const String search = '/search';
  static const String searchConversation = '/search/conversation';
  static const String searchAdvanced = '/search/advanced';
  static const String suggestions = '/search/suggestions';
  static const String hot = '/search/hot';
  static const String history = '/search/history';
}

// ==================== 搜索服务类 ====================

class MessageSearchService {
  static MessageSearchService? _instance;
  static MessageSearchService get instance {
    _instance ??= MessageSearchService._();
    return _instance!;
  }

  MessageSearchService._();

  int? _currentUserId;
  final Map<String, _CacheEntry> _cache = {};
  final Duration _cacheTTL = const Duration(minutes: 5);

  /// 设置当前用户ID
  void setCurrentUser(int userId) {
    _currentUserId = userId;
  }

  /// 全局搜索
  Future<SearchResult> search(String keyword, {int page = 0, int size = 20}) async {
    if (keyword.trim().isEmpty) {
      return SearchResult.empty();
    }
    keyword = keyword.trim();

    // 检查缓存
    final cacheKey = 'search:${keyword.toLowerCase()}:$page:$size';
    final cached = _getCached(cacheKey);
    if (cached != null) {
      return cached;
    }

    final request = SearchRequest(
      keyword: keyword,
      userId: _currentUserId,
      page: page,
      size: size,
    );

    final result = await _post(SearchApiConfig.search, request.toJson());
    final searchResult = SearchResult.fromJson(result);

    _setCached(cacheKey, searchResult);
    return searchResult;
  }

  /// 会话内搜索
  Future<SearchResult> searchInConversation(
    int conversationId,
    String keyword, {
    int? conversationType,
    int page = 0,
    int size = 20,
  }) async {
    if (keyword.trim().isEmpty) {
      return SearchResult.empty();
    }
    keyword = keyword.trim();

    final body = {
      'keyword': keyword,
      'conversationId': conversationId,
      if (conversationType != null) 'conversationType': conversationType,
      'page': page,
      'size': size,
    };

    final result = await _post(SearchApiConfig.searchConversation, body);
    return SearchResult.fromJson(result);
  }

  /// 高级搜索（多条件）
  Future<SearchResult> advancedSearch(AdvancedSearchRequest request) async {
    final result = await _post(SearchApiConfig.searchAdvanced, request.toJson());
    return SearchResult.fromJson(result);
  }

  /// 获取搜索建议
  Future<List<String>> getSuggestions(String prefix, {int limit = 10}) async {
    if (prefix.trim().isEmpty) {
      return [];
    }

    try {
      final uri = Uri.parse('${SearchApiConfig.baseUrl}${SearchApiConfig.suggestions}')
          .replace(queryParameters: {
        'prefix': prefix,
        'limit': limit.toString(),
      });

      final response = await http.get(uri);
      final json = jsonDecode(response.body);

      if (json['code'] == 200) {
        return List<String>.from(json['data'] ?? []);
      }
    } catch (e) {
      // 静默处理错误
    }
    return [];
  }

  /// 获取热门搜索
  Future<List<String>> getHotSearch({int limit = 10}) async {
    try {
      final uri = Uri.parse('${SearchApiConfig.baseUrl}${SearchApiConfig.hot}')
          .replace(queryParameters: {'limit': limit.toString()});

      final response = await http.get(uri);
      final json = jsonDecode(response.body);

      if (json['code'] == 200) {
        return List<String>.from(json['data'] ?? []);
      }
    } catch (e) {
      // 静默处理错误
    }
    return [];
  }

  /// 获取搜索历史
  Future<List<String>> getSearchHistory(int userId, {int limit = 20}) async {
    try {
      final uri = Uri.parse('${SearchApiConfig.baseUrl}${SearchApiConfig.history}')
          .replace(queryParameters: {
        'userId': userId.toString(),
        'limit': limit.toString(),
      });

      final response = await http.get(uri);
      final json = jsonDecode(response.body);

      if (json['code'] == 200) {
        return List<String>.from(json['data'] ?? []);
      }
    } catch (e) {
      // 静默处理错误
    }
    return [];
  }

  /// 清空搜索历史
  Future<void> clearSearchHistory(int userId) async {
    try {
      final uri = Uri.parse('${SearchApiConfig.baseUrl}${SearchApiConfig.history}')
          .replace(queryParameters: {'userId': userId.toString()});

      await http.delete(uri);
    } catch (e) {
      // 静默处理错误
    }
  }

  // ==================== 内部方法 ====================

  Future<Map<String, dynamic>> _post(String endpoint, Map<String, dynamic> body) async {
    final uri = Uri.parse('${SearchApiConfig.baseUrl}$endpoint');
    
    final response = await http.post(
      uri,
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(body),
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body) as Map<String, dynamic>;
    } else {
      throw Exception('Search request failed: ${response.statusCode}');
    }
  }

  // ==================== 缓存管理 ====================

  SearchResult? _getCached(String key) {
    final entry = _cache[key];
    if (entry != null && DateTime.now().difference(entry.timestamp) < _cacheTTL) {
      return entry.result;
    }
    _cache.remove(key);
    return null;
  }

  void _setCached(String key, SearchResult result) {
    // 限制缓存大小
    if (_cache.length >= 100) {
      final oldestKey = _cache.keys.first;
      _cache.remove(oldestKey);
    }
    _cache[key] = _CacheEntry(result, DateTime.now());
  }

  void clearCache() {
    _cache.clear();
  }
}

class _CacheEntry {
  final SearchResult result;
  final DateTime timestamp;

  _CacheEntry(this.result, this.timestamp);
}

// ==================== 工具方法 ====================

class SearchUtils {
  /// 移除高亮标签
  static String stripHighlight(String html) {
    return html.replaceAll('<em>', '').replaceAll('</em>', '');
  }

  /// 格式化消息时间
  static String formatMessageTime(DateTime dateTime) {
    final now = DateTime.now();
    final diff = now.difference(dateTime);
    final days = diff.inDays;

    if (days == 0) {
      return '${dateTime.hour.toString().padLeft(2, '0')}:${dateTime.minute.toString().padLeft(2, '0')}';
    } else if (days == 1) {
      return '昨天 ${dateTime.hour.toString().padLeft(2, '0')}:${dateTime.minute.toString().padLeft(2, '0')}';
    } else if (days < 7) {
      const weekdays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
      return '${weekdays[dateTime.weekday - 1]} ${dateTime.hour.toString().padLeft(2, '0')}:${dateTime.minute.toString().padLeft(2, '0')}';
    } else {
      return '${dateTime.month}/${dateTime.day}';
    }
  }

  /// 获取消息类型名称
  static String getMessageTypeName(int type) {
    const types = {
      1: '文本',
      2: '图片',
      3: '语音',
      4: '视频',
      5: '文件',
      6: '位置',
      7: '名片',
      8: '撤回',
      9: '引用',
      10: '阅后即焚',
    };
    return types[type] ?? '未知';
  }

  /// 获取消息类型图标
  static String getMessageTypeIcon(int type) {
    const icons = {
      1: '📝',
      2: '🖼️',
      3: '🎤',
      4: '🎬',
      5: '📎',
      6: '📍',
      7: '👤',
      8: '🗑️',
      9: '💬',
      10: '🔥',
    };
    return icons[type] ?? '📨';
  }
}
