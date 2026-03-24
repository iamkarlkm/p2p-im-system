import 'dart:convert';
import 'package:http/http.dart' as http;

class Announcement {
  final int id;
  final int groupId;
  final int authorId;
  final String? authorName;
  final String? title;
  final String content;
  final bool pinned;
  final bool requiredRead;
  final bool urgent;
  final String type;
  final DateTime publishTime;
  final DateTime? expireTime;
  final bool isRead;
  final bool isConfirmed;

  Announcement({
    required this.id,
    required this.groupId,
    required this.authorId,
    this.authorName,
    this.title,
    required this.content,
    required this.pinned,
    required this.requiredRead,
    required this.urgent,
    required this.type,
    required this.publishTime,
    this.expireTime,
    required this.isRead,
    required this.isConfirmed,
  });

  factory Announcement.fromJson(Map<String, dynamic> json) {
    return Announcement(
      id: json['id'] as int,
      groupId: json['groupId'] as int,
      authorId: json['authorId'] as int,
      authorName: json['authorName'] as String?,
      title: json['title'] as String?,
      content: json['content'] as String,
      pinned: json['pinned'] as bool? ?? false,
      requiredRead: json['requiredRead'] as bool? ?? false,
      urgent: json['urgent'] as bool? ?? false,
      type: json['type'] as String? ?? 'normal',
      publishTime: DateTime.parse(json['publishTime'] as String),
      expireTime: json['expireTime'] != null ? DateTime.parse(json['expireTime'] as String) : null,
      isRead: json['isRead'] as bool? ?? false,
      isConfirmed: json['isConfirmed'] as bool? ?? false,
    );
  }
}

class AnnouncementService {
  final String _baseUrl;
  final http.Client _client;

  AnnouncementService({String? baseUrl, http.Client? client})
      : _baseUrl = baseUrl ?? 'http://localhost:8080/api',
        _client = client ?? http.Client();

  Future<List<Announcement>> getGroupAnnouncements(int groupId, {int? userId}) async {
    final uri = Uri.parse('$_baseUrl/announcements/group/$groupId');
    final resp = await _client.get(uri, headers: {
      if (userId != null) 'X-User-Id': userId.toString(),
    });
    if (resp.statusCode != 200) throw Exception('加载公告失败: ${resp.statusCode}');
    final List<dynamic> data = jsonDecode(resp.body);
    return data.map((e) => Announcement.fromJson(e)).toList();
  }

  Future<Announcement> publish({
    required int groupId,
    required int authorId,
    String? title,
    required String content,
    String type = 'normal',
    bool pinned = false,
    bool urgent = false,
    bool requiredRead = false,
    DateTime? expireTime,
  }) async {
    final resp = await _client.post(
      Uri.parse('$_baseUrl/announcements'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'groupId': groupId,
        'authorId': authorId,
        'title': title,
        'content': content,
        'type': type,
        'pinned': pinned,
        'urgent': urgent,
        'requiredRead': requiredRead,
        'expireTime': expireTime?.toIso8601String(),
      }),
    );
    if (resp.statusCode != 200) throw Exception('发布公告失败');
    return Announcement.fromJson(jsonDecode(resp.body));
  }

  Future<void> markAsRead(int announcementId, int userId) async {
    await _client.post(
      Uri.parse('$_baseUrl/announcements/$announcementId/read'),
      headers: {'X-User-Id': userId.toString()},
    );
  }

  Future<void> confirmAnnouncement(int announcementId, int userId) async {
    await _client.post(
      Uri.parse('$_baseUrl/announcements/$announcementId/confirm'),
      headers: {'X-User-Id': userId.toString()},
    );
  }
}
