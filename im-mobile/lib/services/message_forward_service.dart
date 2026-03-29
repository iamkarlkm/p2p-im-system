/// 消息合并转发服务
/// 支持多条消息合并转发、选择转发、逐条转发

import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;

/// 转发包实体
class ForwardBundle {
  final int id;
  final String bundleId;
  final int sourceConversationId;
  final int? targetConversationId;
  final int createdBy;
  final String forwardType;
  final String? title;
  final List<int> messageIds;
  final int messageCount;
  final bool hasMedia;
  final int mediaCount;
  final String status;
  final String sendMode;
  final bool includeSenderInfo;
  final bool includeTimestamp;
  final bool anonymizeSenders;
  final String? customComment;
  final DateTime createdAt;
  final DateTime updatedAt;
  final DateTime? forwardedAt;

  ForwardBundle({
    required this.id,
    required this.bundleId,
    required this.sourceConversationId,
    this.targetConversationId,
    required this.createdBy,
    required this.forwardType,
    this.title,
    required this.messageIds,
    required this.messageCount,
    required this.hasMedia,
    required this.mediaCount,
    required this.status,
    required this.sendMode,
    required this.includeSenderInfo,
    required this.includeTimestamp,
    required this.anonymizeSenders,
    this.customComment,
    required this.createdAt,
    required this.updatedAt,
    this.forwardedAt,
  });

  factory ForwardBundle.fromJson(Map<String, dynamic> json) {
    return ForwardBundle(
      id: json['id'] as int,
      bundleId: json['bundleId'] as String,
      sourceConversationId: json['sourceConversationId'] as int,
      targetConversationId: json['targetConversationId'],
      createdBy: json['createdBy'] as int,
      forwardType: json['forwardType'] as String,
      title: json['title'],
      messageIds: (json['messageIds'] as List<dynamic>?)?.map((e) => e as int).toList() ?? [],
      messageCount: json['messageCount'] as int,
      hasMedia: json['hasMedia'] as bool,
      mediaCount: json['mediaCount'] as int,
      status: json['status'] as String,
      sendMode: json['sendMode'] as String,
      includeSenderInfo: json['includeSenderInfo'] as bool,
      includeTimestamp: json['includeTimestamp'] as bool,
      anonymizeSenders: json['anonymizeSenders'] as bool,
      customComment: json['customComment'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
      forwardedAt: json['forwardedAt'] != null ? DateTime.parse(json['forwardedAt']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'bundleId': bundleId,
      'sourceConversationId': sourceConversationId,
      'targetConversationId': targetConversationId,
      'createdBy': createdBy,
      'forwardType': forwardType,
      'title': title,
      'messageIds': messageIds,
      'messageCount': messageCount,
      'hasMedia': hasMedia,
      'mediaCount': mediaCount,
      'status': status,
      'sendMode': sendMode,
      'includeSenderInfo': includeSenderInfo,
      'includeTimestamp': includeTimestamp,
      'anonymizeSenders': anonymizeSenders,
      'customComment': customComment,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'forwardedAt': forwardedAt?.toIso8601String(),
    };
  }
}

/// 转发配置
class ForwardConfig {
  final String? sendMode;
  final bool? includeSenderInfo;
  final bool? includeTimestamp;
  final bool? anonymizeSenders;
  final String? customComment;
  final String? title;

  ForwardConfig({
    this.sendMode,
    this.includeSenderInfo,
    this.includeTimestamp,
    this.anonymizeSenders,
    this.customComment,
    this.title,
  });

  Map<String, dynamic> toJson() {
    return {
      if (sendMode != null) 'sendMode': sendMode,
      if (includeSenderInfo != null) 'includeSenderInfo': includeSenderInfo,
      if (includeTimestamp != null) 'includeTimestamp': includeTimestamp,
      if (anonymizeSenders != null) 'anonymizeSenders': anonymizeSenders,
      if (customComment != null) 'customComment': customComment,
      if (title != null) 'title': title,
    };
  }
}

/// 消息合并转发服务
class MessageForwardService {
  static final MessageForwardService _instance = MessageForwardService._internal();
  
  factory MessageForwardService() {
    return _instance;
  }
  
  MessageForwardService._internal();
  
  final String _baseUrl = 'https://api.example.com/api/message/forward';
  late String _authToken;
  
  /// 初始化服务
  void initialize(String authToken) {
    _authToken = authToken;
  }
  
  /// 创建转发草稿
  Future<Map<String, dynamic>> createDraft({
    required int sourceConversationId,
    required String forwardType,
    required List<int> messageIds,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/draft').replace(queryParameters: {
          'sourceConversationId': sourceConversationId.toString(),
          'forwardType': forwardType,
        }),
        headers: _buildHeaders(),
        body: jsonEncode(messageIds),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('创建转发草稿失败：${response.statusCode}');
      }
    } catch (e) {
      debugPrint('创建转发草稿失败：$e');
      rethrow;
    }
  }
  
  /// 添加消息到草稿
  Future<Map<String, dynamic>> addMessage(String bundleId, int messageId) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/draft/$bundleId/add').replace(queryParameters: {
          'messageId': messageId.toString(),
        }),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('添加消息失败：${response.statusCode}');
      }
    } catch (e) {
      debugPrint('添加消息失败：$e');
      rethrow;
    }
  }
  
  /// 从草稿移除消息
  Future<Map<String, dynamic>> removeMessage(String bundleId, int messageId) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/draft/$bundleId/remove').replace(queryParameters: {
          'messageId': messageId.toString(),
        }),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('移除消息失败：${response.statusCode}');
      }
    } catch (e) {
      debugPrint('移除消息失败：$e');
      rethrow;
    }
  }
  
  /// 更新转发配置
  Future<Map<String, dynamic>> updateConfig(String bundleId, ForwardConfig config) async {
    try {
      final response = await http.put(
        Uri.parse('$_baseUrl/draft/$bundleId/config'),
        headers: _buildHeaders(),
        body: jsonEncode(config.toJson()),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('更新配置失败：${response.statusCode}');
      }
    } catch (e) {
      debugPrint('更新配置失败：$e');
      rethrow;
    }
  }
  
  /// 执行转发
  Future<Map<String, dynamic>> executeForward(String bundleId, int targetConversationId) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/draft/$bundleId/send').replace(queryParameters: {
          'targetConversationId': targetConversationId.toString(),
        }),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        final errorData = jsonDecode(response.body);
        throw Exception(errorData['error'] ?? '执行转发失败');
      }
    } catch (e) {
      debugPrint('执行转发失败：$e');
      rethrow;
    }
  }
  
  /// 取消草稿
  Future<Map<String, dynamic>> cancelDraft(String bundleId) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/draft/$bundleId/cancel'),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('取消草稿失败：${response.statusCode}');
      }
    } catch (e) {
      debugPrint('取消草稿失败：$e');
      rethrow;
    }
  }
  
  /// 获取草稿列表
  Future<Map<String, dynamic>> getDrafts() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/drafts'),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('获取草稿列表失败：${response.statusCode}');
      }
    } catch (e) {
      debugPrint('获取草稿列表失败：$e');
      rethrow;
    }
  }
  
  /// 获取转发历史
  Future<Map<String, dynamic>> getHistory({int limit = 20}) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/history').replace(queryParameters: {
          'limit': limit.toString(),
        }),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('获取转发历史失败：${response.statusCode}');
      }
    } catch (e) {
      debugPrint('获取转发历史失败：$e');
      rethrow;
    }
  }
  
  /// 获取转发统计
  Future<Map<String, dynamic>> getStats() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/stats'),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('获取转发统计失败：${response.statusCode}');
      }
    } catch (e) {
      debugPrint('获取转发统计失败：$e');
      rethrow;
    }
  }
  
  /// 健康检查
  Future<Map<String, dynamic>> healthCheck() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/health'),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('健康检查失败：${response.statusCode}');
      }
    } catch (e) {
      debugPrint('健康检查失败：$e');
      rethrow;
    }
  }
  
  /// 验证消息选择
  Map<String, dynamic> validateMessageSelection(List<int> messageIds) {
    if (messageIds.isEmpty) {
      return {'valid': false, 'error': '请选择至少一条消息'};
    }
    if (messageIds.length > 100) {
      return {'valid': false, 'error': '最多选择 100 条消息'};
    }
    return {'valid': true};
  }
  
  /// 构建请求头
  Map<String, String> _buildHeaders() {
    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $_authToken',
      'User-Agent': 'IM-Mobile-App',
    };
  }
}

/// 全局服务实例
final messageForwardService = MessageForwardService();