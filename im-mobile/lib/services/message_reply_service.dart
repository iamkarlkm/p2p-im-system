import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import '../models/message_reply.dart';

/// 消息引用/回复服务
/// 
/// 提供功能：
/// - 发送带引用的回复
/// - 获取引用详情
/// - 获取回复线程
/// - 获取回复统计
/// - 获取会话中的所有引用
/// - 删除回复
/// - 高亮引用
class MessageReplyService {
  static const String _baseUrl = 'http://10.0.2.2:9000/api'; // Android模拟器访问主机

  final String? _token;
  final http.Client _client;

  MessageReplyService({String? token, http.Client? client})
      : _token = token,
        _client = client ?? http.Client();

  Map<String, String> get _headers => {
        'Content-Type': 'application/json',
        if (_token != null) 'Authorization': 'Bearer $_token',
      };

  /**
   * 发送带引用的回复
   * 
   * @param originalMsgId   被引用的原消息ID
   * @param replyContent    回复内容（可为null表示纯引用）
   * @param replyMsgType    回复消息类型，默认1(文本)
   * @param chatType        聊天类型 1私聊 2群聊
   * @param chatId          聊天ID
   * @param replyRemark     引用备注（可选）
   * @return 创建的引用记录
   */
  Future<MessageReply> createReply({
    required String originalMsgId,
    String? replyContent,
    int replyMsgType = 1,
    required int chatType,
    required int chatId,
    String? replyRemark,
  }) async {
    try {
      final response = await _client.post(
        Uri.parse('$_baseUrl/message/reply/create'),
        headers: _headers,
        body: jsonEncode({
          'originalMsgId': originalMsgId,
          'replyContent': replyContent,
          'replyMsgType': replyMsgType,
          'chatType': chatType,
          'chatId': chatId,
          'replyRemark': replyRemark,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return MessageReply.fromJson(data['data'] ?? data);
      } else {
        throw Exception('创建回复失败: ${response.statusCode}');
      }
    } catch (e) {
      debugPrint('创建消息回复错误: $e');
      rethrow;
    }
  }

  /**
   * 获取消息的引用详情
   */
  Future<MessageReply?> getReplyByOriginalMsgId(String originalMsgId, int replyUserId) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/message/reply?originalMsgId=$originalMsgId&replyUserId=$replyUserId'),
        headers: _headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['data'] == null) return null;
        return MessageReply.fromJson(data['data']);
      }
      return null;
    } catch (e) {
      debugPrint('获取引用详情错误: $e');
      return null;
    }
  }

  /**
   * 获取消息的所有直接回复
   */
  Future<List<MessageReply>> getDirectReplies(String originalMsgId) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/message/reply/direct/$originalMsgId'),
        headers: _headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final List<dynamic> list = data['data'] ?? [];
        return list.map((e) => MessageReply.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      debugPrint('获取直接回复错误: $e');
      return [];
    }
  }

  /**
   * 获取消息的引用链（包含所有层级回复）
   */
  Future<List<MessageReply>> getReplyChain(String originalMsgId) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/message/reply/chain/$originalMsgId'),
        headers: _headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final List<dynamic> list = data['data'] ?? [];
        return list.map((e) => MessageReply.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      debugPrint('获取引用链错误: $e');
      return [];
    }
  }

  /**
   * 获取回复线程（包含原消息和所有回复）
   */
  Future<ReplyThread?> getReplyThread(String originalMsgId) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/message/reply/thread/$originalMsgId'),
        headers: _headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return ReplyThread.fromJson(data['data'] ?? data);
      }
      return null;
    } catch (e) {
      debugPrint('获取回复线程错误: $e');
      return null;
    }
  }

  /**
   * 获取回复统计
   */
  Future<ReplyStats?> getReplyStats(String originalMsgId) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/message/reply/stats/$originalMsgId'),
        headers: _headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return ReplyStats.fromJson(data['data'] ?? data);
      }
      return null;
    } catch (e) {
      debugPrint('获取回复统计错误: $e');
      return null;
    }
  }

  /**
   * 删除回复（软删除）
   */
  Future<bool> deleteReply(int replyId) async {
    try {
      final response = await _client.delete(
        Uri.parse('$_baseUrl/message/reply/$replyId'),
        headers: _headers,
      );

      return response.statusCode == 200;
    } catch (e) {
      debugPrint('删除回复错误: $e');
      return false;
    }
  }

  /**
   * 高亮引用
   */
  Future<bool> toggleHighlight(int replyId, bool highlight) async {
    try {
      final response = await _client.put(
        Uri.parse('$_baseUrl/message/reply/highlight/$replyId'),
        headers: _headers,
        body: jsonEncode({'highlight': highlight}),
      );

      return response.statusCode == 200;
    } catch (e) {
      debugPrint('高亮引用错误: $e');
      return false;
    }
  }

  /**
   * 获取某用户最近参与的引用回复
   */
  Future<List<MessageReply>> getUserRecentReplies({int limit = 20}) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/message/reply/recent?limit=$limit'),
        headers: _headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final List<dynamic> list = data['data'] ?? [];
        return list.map((e) => MessageReply.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      debugPrint('获取最近回复错误: $e');
      return [];
    }
  }

  /**
   * 获取某会话中所有带引用的消息
   */
  Future<List<MessageReply>> getRepliesInChat(int chatType, int chatId) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/message/reply/chat?chatType=$chatType&chatId=$chatId'),
        headers: _headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final List<dynamic> list = data['data'] ?? [];
        return list.map((e) => MessageReply.fromJson(e)).toList();
      }
      return [];
    } catch (e) {
      debugPrint('获取会话引用错误: $e');
      return [];
    }
  }

  /**
   * 检查消息是否被引用过
   */
  Future<bool> isMessageReplied(String msgId) async {
    try {
      final response = await _client.get(
        Uri.parse('$_baseUrl/message/reply/exists/$msgId'),
        headers: _headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['data'] == true;
      }
      return false;
    } catch (e) {
      debugPrint('检查消息引用状态错误: $e');
      return false;
    }
  }

  void dispose() {
    _client.close();
  }
}

/// 回复线程数据
class ReplyThread {
  final Map<String, dynamic> originalMessage;
  final List<MessageReply> replies;

  ReplyThread({required this.originalMessage, required this.replies});

  factory ReplyThread.fromJson(Map<String, dynamic> json) {
    return ReplyThread(
      originalMessage: json['originalMessage'] as Map<String, dynamic>? ?? {},
      replies: (json['replies'] as List<dynamic>?)
              ?.map((e) => MessageReply.fromJson(e as Map<String, dynamic>))
              .toList() ??
          [],
    );
  }
}

/// 回复统计
class ReplyStats {
  final String originalMsgId;
  final int totalCount;
  final int textCount;
  final int imageCount;
  final int fileCount;

  ReplyStats({
    required this.originalMsgId,
    required this.totalCount,
    required this.textCount,
    required this.imageCount,
    required this.fileCount,
  });

  factory ReplyStats.fromJson(Map<String, dynamic> json) {
    return ReplyStats(
      originalMsgId: json['originalMsgId'] as String? ?? '',
      totalCount: json['totalCount'] as int? ?? 0,
      textCount: json['textCount'] as int? ?? 0,
      imageCount: json['imageCount'] as int? ?? 0,
      fileCount: json['fileCount'] as int? ?? 0,
    );
  }
}
