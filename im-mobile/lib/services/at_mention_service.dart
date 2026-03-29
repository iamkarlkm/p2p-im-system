import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/at_mention.dart';

class AtMentionService {
  static const String _baseUrl = '/api/at-mention';
  final String _token;

  AtMentionService(this._token);

  Map<String, String> get _headers => {
    'Authorization': 'Bearer $_token',
    'Content-Type': 'application/json',
  };

  Future<List<AtMention>> processMentions({
    required int messageId,
    required int senderUserId,
    List<int>? mentionedUserIds,
    bool isAtAll = false,
    String? conversationId,
    int? roomId,
    String? messagePreview,
  }) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/process'),
      headers: _headers,
      body: jsonEncode({
        'messageId': messageId,
        'senderUserId': senderUserId,
        'mentionedUserIds': mentionedUserIds,
        'isAtAll': isAtAll,
        'conversationId': conversationId,
        'roomId': roomId,
        'messagePreview': messagePreview,
      }),
    );
    final data = jsonDecode(utf8.decode(response.bodyBytes));
    return (data['data'] as List).map((e) => AtMention.fromJson(e)).toList();
  }

  Future<List<AtMention>> getMentionList(int userId, {int page = 0, int size = 20}) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/list').replace(queryParameters: {
        'userId': userId.toString(),
        'page': page.toString(),
        'size': size.toString(),
      }),
      headers: _headers,
    );
    final data = jsonDecode(utf8.decode(response.bodyBytes));
    return (data['data'] as List).map((e) => AtMention.fromJson(e)).toList();
  }

  Future<int> getUnreadCount(int userId) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/unread-count').replace(queryParameters: {
        'userId': userId.toString(),
      }),
      headers: _headers,
    );
    final data = jsonDecode(utf8.decode(response.bodyBytes));
    return data['data'] ?? 0;
  }

  Future<int> getUnreadCountInRoom(int userId, int roomId) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/unread-count/room').replace(queryParameters: {
        'userId': userId.toString(),
        'roomId': roomId.toString(),
      }),
      headers: _headers,
    );
    final data = jsonDecode(utf8.decode(response.bodyBytes));
    return data['data'] ?? 0;
  }

  Future<int> markAsRead(int userId, List<int> mentionIds) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/mark-read').replace(queryParameters: {
        'userId': userId.toString(),
      }),
      headers: _headers,
      body: jsonEncode(mentionIds),
    );
    final data = jsonDecode(utf8.decode(response.bodyBytes));
    return data['updated'] ?? 0;
  }

  Future<int> markAllAsReadInRoom(int userId, int roomId) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/mark-read/room').replace(queryParameters: {
        'userId': userId.toString(),
        'roomId': roomId.toString(),
      }),
      headers: _headers,
    );
    final data = jsonDecode(utf8.decode(response.bodyBytes));
    return data['updated'] ?? 0;
  }

  Future<AtMentionSettings> getMentionSettings(int userId) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/settings').replace(queryParameters: {
        'userId': userId.toString(),
      }),
      headers: _headers,
    );
    final data = jsonDecode(utf8.decode(response.bodyBytes));
    return AtMentionSettings.fromJson(data['data']);
  }

  Future<AtMentionSettings> updateMentionSettings(int userId, AtMentionSettings settings) async {
    final response = await http.put(
      Uri.parse('$_baseUrl/settings').replace(queryParameters: {
        'userId': userId.toString(),
      }),
      headers: _headers,
      body: jsonEncode(settings.toJson()),
    );
    final data = jsonDecode(utf8.decode(response.bodyBytes));
    return AtMentionSettings.fromJson(data['data']);
  }
}
