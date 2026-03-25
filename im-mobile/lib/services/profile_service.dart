/// 用户资料服务 - Flutter移动端

import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/profile.dart';

class ProfileService {
  static const String _baseUrl = 'http://localhost:8080/api';
  final String _token;

  ProfileService({String token = ''}) : _token = token;

  Map<String, String> get _headers => {
        'Content-Type': 'application/json',
        if (_token.isNotEmpty) 'Authorization': 'Bearer $_token',
      };

  Future<UserProfile> getProfile(String userId) async {
    final resp = await http.get(
      Uri.parse('$_baseUrl/profile/$userId'),
      headers: _headers,
    );
    return UserProfile.fromJson(jsonDecode(resp.body)['data']);
  }

  Future<UserProfile> updateProfile(String userId, Map<String, dynamic> payload) async {
    final resp = await http.put(
      Uri.parse('$_baseUrl/profile/$userId'),
      headers: _headers,
      body: jsonEncode(payload),
    );
    return UserProfile.fromJson(jsonDecode(resp.body)['data']);
  }

  Future<UserProfile> updateAvatar(String userId, String avatarUrl) async {
    final resp = await http.patch(
      Uri.parse('$_baseUrl/profile/$userId/avatar'),
      headers: _headers,
      body: jsonEncode({'avatarUrl': avatarUrl}),
    );
    return UserProfile.fromJson(jsonDecode(resp.body)['data']);
  }

  Future<UserProfile> updateNickname(String userId, String nickname) async {
    final resp = await http.patch(
      Uri.parse('$_baseUrl/profile/$userId/nickname'),
      headers: _headers,
      body: jsonEncode({'nickname': nickname}),
    );
    return UserProfile.fromJson(jsonDecode(resp.body)['data']);
  }

  Future<UserProfile> updateSignature(String userId, String signature) async {
    final resp = await http.patch(
      Uri.parse('$_baseUrl/profile/$userId/signature'),
      headers: _headers,
      body: jsonEncode({'signature': signature}),
    );
    return UserProfile.fromJson(jsonDecode(resp.body)['data']);
  }

  Future<UserProfile> updateStatus(String userId, UserStatus status) async {
    final resp = await http.patch(
      Uri.parse('$_baseUrl/profile/$userId/status'),
      headers: _headers,
      body: jsonEncode({'status': status.apiValue}),
    );
    return UserProfile.fromJson(jsonDecode(resp.body)['data']);
  }

  Future<List<UserProfile>> searchUsers(String keyword, {int limit = 20}) async {
    final resp = await http.post(
      Uri.parse('$_baseUrl/profile/search'),
      headers: _headers,
      body: jsonEncode({'keyword': keyword, 'limit': limit}),
    );
    final List<dynamic> data = jsonDecode(resp.body)['data'];
    return data.map((e) => UserProfile.fromJson(e)).toList();
  }

  Future<Map<String, UserProfile>> getProfiles(List<String> userIds) async {
    final resp = await http.post(
      Uri.parse('$_baseUrl/profile/batch'),
      headers: _headers,
      body: jsonEncode({'userIds': userIds}),
    );
    final Map<String, dynamic> data = jsonDecode(resp.body)['data'];
    return data.map((k, v) => MapEntry(k, UserProfile.fromJson(v)));
  }

  Future<List<String>> getOnlineUsers() async {
    final resp = await http.get(
      Uri.parse('$_baseUrl/profile/online'),
      headers: _headers,
    );
    return List<String>.from(jsonDecode(resp.body)['data']);
  }

  // 好友备注
  Future<FriendRemark> setFriendRemark(String userId, String friendId,
      {String remark = '', String groupName = '', List<String> tags = const []}) async {
    final resp = await http.put(
      Uri.parse('$_baseUrl/profile/friend-remark'),
      headers: _headers,
      body: jsonEncode({
        'userId': userId,
        'friendId': friendId,
        'remark': remark,
        'groupName': groupName,
        'tags': tags,
      }),
    );
    return FriendRemark.fromJson(jsonDecode(resp.body)['data']);
  }

  Future<FriendRemark?> getFriendRemark(String userId, String friendId) async {
    try {
      final resp = await http.get(
        Uri.parse('$_baseUrl/profile/$userId/friend-remark/$friendId'),
        headers: _headers,
      );
      return FriendRemark.fromJson(jsonDecode(resp.body)['data']);
    } catch (_) {
      return null;
    }
  }

  Future<List<FriendRemark>> getAllFriendRemarks(String userId) async {
    final resp = await http.get(
      Uri.parse('$_baseUrl/profile/$userId/friend-remarks'),
      headers: _headers,
    );
    return (jsonDecode(resp.body)['data'] as List)
        .map((e) => FriendRemark.fromJson(e))
        .toList();
  }

  // 好友分组
  Future<FriendGroup> createFriendGroup(String userId, String name, {int sortOrder = 0}) async {
    final resp = await http.post(
      Uri.parse('$_baseUrl/profile/$userId/friend-group'),
      headers: _headers,
      body: jsonEncode({'name': name, 'sortOrder': sortOrder}),
    );
    return FriendGroup.fromJson(jsonDecode(resp.body)['data']);
  }

  Future<List<FriendGroup>> getFriendGroups(String userId) async {
    final resp = await http.get(
      Uri.parse('$_baseUrl/profile/$userId/friend-groups'),
      headers: _headers,
    );
    return (jsonDecode(resp.body)['data'] as List)
        .map((e) => FriendGroup.fromJson(e))
        .toList();
  }

  Future<void> deleteFriendGroup(String userId, String groupId) async {
    await http.delete(
      Uri.parse('$_baseUrl/profile/$userId/friend-group/$groupId'),
      headers: _headers,
    );
  }
}
