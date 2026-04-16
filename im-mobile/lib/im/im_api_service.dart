import 'dart:convert';
import 'package:http/http.dart' as http;

/// IM API服务
class IMApiService {
  static const String _baseUrl = 'https://api.im.example.com/api/v1';
  String? _token;

  void setToken(String token) {
    _token = token;
  }

  Map<String, String> get _headers => {
    'Content-Type': 'application/json',
    if (_token != null) 'Authorization': 'Bearer $_token',
  };

  /// 用户登录
  Future<LoginResult> login(String username, String password) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/auth/login'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'username': username,
        'password': password,
      }),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return LoginResult.fromJson(data['data']);
    } else {
      throw Exception('Login failed: ${response.body}');
    }
  }

  /// 获取消息历史
  Future<List<MessageHistory>> getMessageHistory(String conversationId, {int page = 1, int size = 20}) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/messages/history?conversationId=$conversationId&page=$page&size=$size'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final List<dynamic> list = data['data'] ?? [];
      return list.map((e) => MessageHistory.fromJson(e)).toList();
    } else {
      throw Exception('Failed to get message history');
    }
  }

  /// 获取会话列表
  Future<List<Conversation>> getConversations() async {
    final response = await http.get(
      Uri.parse('$_baseUrl/conversations'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final List<dynamic> list = data['data'] ?? [];
      return list.map((e) => Conversation.fromJson(e)).toList();
    } else {
      throw Exception('Failed to get conversations');
    }
  }

  /// 标记消息已读
  Future<void> markMessageAsRead(String messageId) async {
    await http.post(
      Uri.parse('$_baseUrl/messages/$messageId/read'),
      headers: _headers,
    );
  }

  /// 撤回消息
  Future<void> recallMessage(String messageId) async {
    await http.post(
      Uri.parse('$_baseUrl/messages/$messageId/recall'),
      headers: _headers,
    );
  }

  /// 获取好友列表
  Future<List<Friend>> getFriends() async {
    final response = await http.get(
      Uri.parse('$_baseUrl/friends'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final List<dynamic> list = data['data'] ?? [];
      return list.map((e) => Friend.fromJson(e)).toList();
    } else {
      throw Exception('Failed to get friends');
    }
  }

  /// 搜索用户
  Future<List<UserInfo>> searchUsers(String keyword) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/users/search?keyword=$keyword'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final List<dynamic> list = data['data'] ?? [];
      return list.map((e) => UserInfo.fromJson(e)).toList();
    } else {
      throw Exception('Failed to search users');
    }
  }
}

/// 登录结果
class LoginResult {
  final String token;
  final String userId;
  final String username;
  final String? avatar;

  LoginResult({
    required this.token,
    required this.userId,
    required this.username,
    this.avatar,
  });

  factory LoginResult.fromJson(Map<String, dynamic> json) {
    return LoginResult(
      token: json['token'] ?? '',
      userId: json['userId'] ?? '',
      username: json['username'] ?? '',
      avatar: json['avatar'],
    );
  }
}

/// 消息历史
class MessageHistory {
  final String id;
  final String from;
  final String content;
  final int timestamp;

  MessageHistory({
    required this.id,
    required this.from,
    required this.content,
    required this.timestamp,
  });

  factory MessageHistory.fromJson(Map<String, dynamic> json) {
    return MessageHistory(
      id: json['id'] ?? '',
      from: json['from'] ?? '',
      content: json['content'] ?? '',
      timestamp: json['timestamp'] ?? 0,
    );
  }
}

/// 会话
class Conversation {
  final String id;
  final String type;
  final String targetId;
  final String targetName;
  final String? targetAvatar;
  final String lastMessage;
  final int unreadCount;
  final int lastMessageTime;

  Conversation({
    required this.id,
    required this.type,
    required this.targetId,
    required this.targetName,
    this.targetAvatar,
    required this.lastMessage,
    required this.unreadCount,
    required this.lastMessageTime,
  });

  factory Conversation.fromJson(Map<String, dynamic> json) {
    return Conversation(
      id: json['id'] ?? '',
      type: json['type'] ?? '',
      targetId: json['targetId'] ?? '',
      targetName: json['targetName'] ?? '',
      targetAvatar: json['targetAvatar'],
      lastMessage: json['lastMessage'] ?? '',
      unreadCount: json['unreadCount'] ?? 0,
      lastMessageTime: json['lastMessageTime'] ?? 0,
    );
  }
}

/// 好友
class Friend {
  final String userId;
  final String username;
  final String? avatar;
  final String? remark;

  Friend({
    required this.userId,
    required this.username,
    this.avatar,
    this.remark,
  });

  factory Friend.fromJson(Map<String, dynamic> json) {
    return Friend(
      userId: json['userId'] ?? '',
      username: json['username'] ?? '',
      avatar: json['avatar'],
      remark: json['remark'],
    );
  }
}

/// 用户信息
class UserInfo {
  final String userId;
  final String username;
  final String? avatar;
  final String? signature;

  UserInfo({
    required this.userId,
    required this.username,
    this.avatar,
    this.signature,
  });

  factory UserInfo.fromJson(Map<String, dynamic> json) {
    return UserInfo(
      userId: json['userId'] ?? '',
      username: json['username'] ?? '',
      avatar: json['avatar'],
      signature: json['signature'],
    );
  }
}
