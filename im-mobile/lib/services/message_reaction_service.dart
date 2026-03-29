import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/message_reaction_model.dart';

/// 消息表情回应服务
class MessageReactionService {
  static const String _baseUrl = 'http://localhost:8080/api/v1';
  static const String _reactionsEndpoint = '$_baseUrl/reactions';

  final http.Client _client;

  MessageReactionService({http.Client? client}) : _client = client ?? http.Client();

  /// 添加表情回应
  Future<MessageReaction> addReaction(AddReactionRequest request) async {
    final response = await _client.post(
      Uri.parse(_reactionsEndpoint),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body)['data'];
      return MessageReaction.fromJson(data);
    }
    throw Exception('Failed to add reaction: ${response.statusCode}');
  }

  /// 切换表情回应
  Future<MessageReaction?> toggleReaction(AddReactionRequest request) async {
    final response = await _client.post(
      Uri.parse('$_reactionsEndpoint/toggle'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body)['data'];
      if (data == null) return null;
      return MessageReaction.fromJson(data);
    }
    throw Exception('Failed to toggle reaction: ${response.statusCode}');
  }

  /// 移除表情回应
  Future<void> removeReaction(int messageId, int userId, String emojiCode) async {
    final encodedEmoji = Uri.encodeComponent(emojiCode);
    final response = await _client.delete(
      Uri.parse('$_reactionsEndpoint/message/$messageId/user/$userId/emoji/$encodedEmoji'),
    );

    if (response.statusCode != 200) {
      throw Exception('Failed to remove reaction: ${response.statusCode}');
    }
  }

  /// 获取消息的表情回应汇总
  Future<ReactionSummary> getReactionSummary(int messageId, int currentUserId) async {
    final response = await _client.get(
      Uri.parse('$_reactionsEndpoint/message/$messageId/summary').replace(
        queryParameters: {'currentUserId': currentUserId.toString()},
      ),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body)['data'];
      return ReactionSummary.fromJson(data);
    }
    throw Exception('Failed to get reaction summary: ${response.statusCode}');
  }

  /// 批量获取消息的表情回应汇总
  Future<List<ReactionSummary>> getReactionSummaries(
    List<int> messageIds,
    int currentUserId,
  ) async {
    final response = await _client.post(
      Uri.parse('$_reactionsEndpoint/summaries').replace(
        queryParameters: {'currentUserId': currentUserId.toString()},
      ),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(messageIds),
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body)['data'];
      return data.map((e) => ReactionSummary.fromJson(e)).toList();
    }
    throw Exception('Failed to get reaction summaries: ${response.statusCode}');
  }

  /// 获取会话中的热门表情
  Future<List<EmojiCount>> getPopularEmojis(int conversationId, {int limit = 10}) async {
    final response = await _client.get(
      Uri.parse('$_reactionsEndpoint/conversation/$conversationId/popular').replace(
        queryParameters: {'limit': limit.toString()},
      ),
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body)['data'];
      return data.map((e) => EmojiCount.fromJson(e)).toList();
    }
    throw Exception('Failed to get popular emojis: ${response.statusCode}');
  }

  /// 检查用户是否回应了消息
  Future<bool> hasUserReacted(int messageId, int userId) async {
    final response = await _client.get(
      Uri.parse('$_reactionsEndpoint/message/$messageId/user/$userId/has-reacted'),
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body)['data'] as bool;
    }
    throw Exception('Failed to check reaction: ${response.statusCode}');
  }

  /// 删除用户对消息的所有回应
  Future<void> removeAllReactions(int messageId, int userId) async {
    final response = await _client.delete(
      Uri.parse('$_reactionsEndpoint/message/$messageId/user/$userId'),
    );

    if (response.statusCode != 200) {
      throw Exception('Failed to remove all reactions: ${response.statusCode}');
    }
  }

  void dispose() {
    _client.close();
  }
}
