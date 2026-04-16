import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;

/// API服务类 - 提供IM系统的HTTP API接口
class ApiService {
  static final ApiService _instance = ApiService._internal();
  factory ApiService() => _instance;
  ApiService._internal();

  String? _baseUrl;
  String? _authToken;
  String? _userId;

  // Getters
  String? get baseUrl => _baseUrl;
  String? get authToken => _authToken;
  String? get userId => _userId;

  /// 初始化API服务
  void initialize(String baseUrl, {String? authToken, String? userId}) {
    _baseUrl = baseUrl.endsWith('/') ? baseUrl.substring(0, baseUrl.length - 1) : baseUrl;
    _authToken = authToken;
    _userId = userId;
  }

  /// 设置认证令牌
  void setAuthToken(String token) {
    _authToken = token;
  }

  /// 设置用户ID
  void setUserId(String userId) {
    _userId = userId;
  }

  /// 构建请求头
  Map<String, String> _buildHeaders() {
    final headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };
    if (_authToken != null) {
      headers['Authorization'] = 'Bearer $_authToken';
    }
    return headers;
  }

  /// 处理HTTP响应
  dynamic _handleResponse(http.Response response) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      if (response.body.isEmpty) return null;
      return jsonDecode(utf8.decode(response.bodyBytes));
    } else {
      throw ApiException(
        statusCode: response.statusCode,
        message: _extractErrorMessage(response),
      );
    }
  }

  /// 提取错误信息
  String _extractErrorMessage(http.Response response) {
    try {
      final data = jsonDecode(response.body);
      return data['message'] ?? data['error'] ?? 'Request failed';
    } catch (_) {
      return 'HTTP ${response.statusCode}';
    }
  }

  // ==================== 用户认证接口 ====================

  /// 用户登录
  Future<Map<String, dynamic>> login(String username, String password) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/auth/login'),
      headers: _buildHeaders(),
      body: jsonEncode({
        'username': username,
        'password': password,
      }),
    );
    final data = _handleResponse(response);
    if (data['token'] != null) {
      setAuthToken(data['token']);
    }
    if (data['userId'] != null) {
      setUserId(data['userId']);
    }
    return data;
  }

  /// 用户注册
  Future<Map<String, dynamic>> register({
    required String username,
    required String password,
    String? nickname,
    String? email,
    String? phone,
  }) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/auth/register'),
      headers: _buildHeaders(),
      body: jsonEncode({
        'username': username,
        'password': password,
        'nickname': nickname,
        'email': email,
        'phone': phone,
      }),
    );
    return _handleResponse(response);
  }

  /// 刷新令牌
  Future<Map<String, dynamic>> refreshToken() async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/auth/refresh'),
      headers: _buildHeaders(),
    );
    final data = _handleResponse(response);
    if (data['token'] != null) {
      setAuthToken(data['token']);
    }
    return data;
  }

  /// 用户登出
  Future<void> logout() async {
    try {
      await http.post(
        Uri.parse('$_baseUrl/api/auth/logout'),
        headers: _buildHeaders(),
      );
    } finally {
      _authToken = null;
      _userId = null;
    }
  }

  // ==================== 消息接口 ====================

  /// 发送消息
  Future<Map<String, dynamic>> sendMessage({
    required String conversationId,
    required String content,
    String messageType = 'TEXT',
    Map<String, dynamic>? extra,
  }) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/messages'),
      headers: _buildHeaders(),
      body: jsonEncode({
        'conversationId': conversationId,
        'content': content,
        'messageType': messageType,
        'extra': extra,
        'senderId': _userId,
      }),
    );
    return _handleResponse(response);
  }

  /// 获取消息列表
  Future<List<dynamic>> getMessages({
    required String conversationId,
    int page = 0,
    int size = 20,
    String? beforeMessageId,
  }) async {
    final queryParams = {
      'conversationId': conversationId,
      'page': page.toString(),
      'size': size.toString(),
      if (beforeMessageId != null) 'before': beforeMessageId,
    };
    final uri = Uri.parse('$_baseUrl/api/messages').replace(queryParameters: queryParams);
    final response = await http.get(uri, headers: _buildHeaders());
    final data = _handleResponse(response);
    return data['content'] ?? data['messages'] ?? data ?? [];
  }

  /// 撤回消息
  Future<void> recallMessage(String messageId) async {
    await http.post(
      Uri.parse('$_baseUrl/api/messages/$messageId/recall'),
      headers: _buildHeaders(),
    );
  }

  /// 删除消息
  Future<void> deleteMessage(String messageId) async {
    await http.delete(
      Uri.parse('$_baseUrl/api/messages/$messageId'),
      headers: _buildHeaders(),
    );
  }

  // ==================== 会话接口 ====================

  /// 获取会话列表
  Future<List<dynamic>> getConversations({int page = 0, int size = 20}) async {
    final uri = Uri.parse('$_baseUrl/api/conversations').replace(queryParameters: {
      'page': page.toString(),
      'size': size.toString(),
    });
    final response = await http.get(uri, headers: _buildHeaders());
    final data = _handleResponse(response);
    return data['content'] ?? data['conversations'] ?? data ?? [];
  }

  /// 创建会话
  Future<Map<String, dynamic>> createConversation({
    required List<String> participantIds,
    String? name,
    String type = 'DIRECT',
  }) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/conversations'),
      headers: _buildHeaders(),
      body: jsonEncode({
        'participantIds': participantIds,
        'name': name,
        'type': type,
      }),
    );
    return _handleResponse(response);
  }

  /// 获取会话详情
  Future<Map<String, dynamic>> getConversation(String conversationId) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/api/conversations/$conversationId'),
      headers: _buildHeaders(),
    );
    return _handleResponse(response);
  }

  /// 标记会话已读
  Future<void> markConversationRead(String conversationId) async {
    await http.post(
      Uri.parse('$_baseUrl/api/conversations/$conversationId/read'),
      headers: _buildHeaders(),
    );
  }

  // ==================== 好友接口 ====================

  /// 获取好友列表
  Future<List<dynamic>> getFriends() async {
    final response = await http.get(
      Uri.parse('$_baseUrl/api/friends'),
      headers: _buildHeaders(),
    );
    final data = _handleResponse(response);
    return data['friends'] ?? data ?? [];
  }

  /// 发送好友请求
  Future<Map<String, dynamic>> sendFriendRequest(String userId, {String? message}) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/friends/requests'),
      headers: _buildHeaders(),
      body: jsonEncode({
        'targetUserId': userId,
        'message': message,
      }),
    );
    return _handleResponse(response);
  }

  /// 处理好友请求
  Future<void> handleFriendRequest(String requestId, bool accept) async {
    await http.post(
      Uri.parse('$_baseUrl/api/friends/requests/$requestId/${accept ? 'accept' : 'reject'}'),
      headers: _buildHeaders(),
    );
  }

  /// 删除好友
  Future<void> deleteFriend(String friendId) async {
    await http.delete(
      Uri.parse('$_baseUrl/api/friends/$friendId'),
      headers: _buildHeaders(),
    );
  }

  // ==================== 群组接口 ====================

  /// 创建群组
  Future<Map<String, dynamic>> createGroup({
    required String name,
    List<String>? memberIds,
    String? avatar,
    String? description,
  }) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/api/groups'),
      headers: _buildHeaders(),
      body: jsonEncode({
        'name': name,
        'memberIds': memberIds ?? [],
        'avatar': avatar,
        'description': description,
      }),
    );
    return _handleResponse(response);
  }

  /// 获取群组列表
  Future<List<dynamic>> getGroups() async {
    final response = await http.get(
      Uri.parse('$_baseUrl/api/groups'),
      headers: _buildHeaders(),
    );
    final data = _handleResponse(response);
    return data['groups'] ?? data ?? [];
  }

  /// 获取群组详情
  Future<Map<String, dynamic>> getGroup(String groupId) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/api/groups/$groupId'),
      headers: _buildHeaders(),
    );
    return _handleResponse(response);
  }

  /// 加入群组
  Future<void> joinGroup(String groupId) async {
    await http.post(
      Uri.parse('$_baseUrl/api/groups/$groupId/join'),
      headers: _buildHeaders(),
    );
  }

  /// 退出群组
  Future<void> leaveGroup(String groupId) async {
    await http.post(
      Uri.parse('$_baseUrl/api/groups/$groupId/leave'),
      headers: _buildHeaders(),
    );
  }

  // ==================== 用户接口 ====================

  /// 获取用户信息
  Future<Map<String, dynamic>> getUserInfo(String userId) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/api/users/$userId'),
      headers: _buildHeaders(),
    );
    return _handleResponse(response);
  }

  /// 更新用户信息
  Future<Map<String, dynamic>> updateUserInfo({
    String? nickname,
    String? avatar,
    String? signature,
  }) async {
    final response = await http.put(
      Uri.parse('$_baseUrl/api/users/me'),
      headers: _buildHeaders(),
      body: jsonEncode({
        'nickname': nickname,
        'avatar': avatar,
        'signature': signature,
      }),
    );
    return _handleResponse(response);
  }

  /// 搜索用户
  Future<List<dynamic>> searchUsers(String keyword) async {
    final uri = Uri.parse('$_baseUrl/api/users/search').replace(queryParameters: {
      'keyword': keyword,
    });
    final response = await http.get(uri, headers: _buildHeaders());
    final data = _handleResponse(response);
    return data['users'] ?? data ?? [];
  }

  /// 上传文件
  Future<Map<String, dynamic>> uploadFile(File file) async {
    final request = http.MultipartRequest(
      'POST',
      Uri.parse('$_baseUrl/api/files/upload'),
    );
    request.headers.addAll(_buildHeaders());
    request.files.add(await http.MultipartFile.fromPath('file', file.path));

    final streamedResponse = await request.send();
    final response = await http.Response.fromStream(streamedResponse);
    return _handleResponse(response);
  }
}

/// API异常类
class ApiException implements Exception {
  final int statusCode;
  final String message;

  ApiException({required this.statusCode, required this.message});

  @override
  String toString() => 'ApiException($statusCode): $message';
}
