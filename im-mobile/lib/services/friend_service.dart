import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/friend.dart';
import '../models/user.dart';

class FriendService {
  static const String _baseUrl = 'http://localhost:8080/api';
  
  String? _token;
  
  void setToken(String token) {
    _token = token;
  }
  
  Map<String, String> get _headers => {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer $_token',
  };
  
  /// 获取好友列表
  Future<List<User>> getFriends() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/friends'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data['code'] == 200) {
          List<dynamic> list = data['data'] ?? [];
          return list.map((item) {
            // 返回好友的用户信息
            final userData = item['user'] ?? {};
            return User.fromJson(userData);
          }).toList();
        }
      }
      return [];
    } catch (e) {
      print('Get friends error: $e');
      return [];
    }
  }
  
  /// 添加好友
  Future<bool> addFriend(String friendId, {String? remark}) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/friends'),
        headers: _headers,
        body: json.encode({
          'friendId': friendId,
          'remark': remark,
        }),
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['code'] == 200;
      }
      return false;
    } catch (e) {
      print('Add friend error: $e');
      return false;
    }
  }
  
  /// 删除好友
  Future<bool> deleteFriend(String friendId) async {
    try {
      final response = await http.delete(
        Uri.parse('$_baseUrl/friends/$friendId'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['code'] == 200;
      }
      return false;
    } catch (e) {
      print('Delete friend error: $e');
      return false;
    }
  }
  
  /// 拉黑好友
  Future<bool> blockFriend(String friendId) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/friends/block/$friendId'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['code'] == 200;
      }
      return false;
    } catch (e) {
      print('Block friend error: $e');
      return false;
    }
  }
  
  /// 检查是否为好友
  Future<bool> isFriend(String friendId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/friends/check/$friendId'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['code'] == 200 && data['data'] == true;
      }
      return false;
    } catch (e) {
      print('Check friend error: $e');
      return false;
    }
  }
  
  /// 发送好友请求
  Future<bool> sendFriendRequest(String friendId, {String? message}) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/friend-requests'),
        headers: _headers,
        body: json.encode({
          'toUserId': friendId,
          'message': message,
        }),
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['code'] == 200;
      }
      return false;
    } catch (e) {
      print('Send friend request error: $e');
      return false;
    }
  }
  
  /// 获取收到的好友请求列表
  Future<List<Map<String, dynamic>>> getReceivedRequests() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/friend-requests/received'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data['code'] == 200) {
          return List<Map<String, dynamic>>.from(data['data'] ?? []);
        }
      }
      return [];
    } catch (e) {
      print('Get received requests error: $e');
      return [];
    }
  }
  
  /// 获取发送的好友请求列表
  Future<List<Map<String, dynamic>>> getSentRequests() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/friend-requests/sent'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data['code'] == 200) {
          return List<Map<String, dynamic>>.from(data['data'] ?? []);
        }
      }
      return [];
    } catch (e) {
      print('Get sent requests error: $e');
      return [];
    }
  }
  
  /// 接受好友请求
  Future<bool> acceptFriendRequest(String requestId) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/friend-requests/$requestId/accept'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['code'] == 200;
      }
      return false;
    } catch (e) {
      print('Accept friend request error: $e');
      return false;
    }
  }
  
  /// 拒绝好友请求
  Future<bool> rejectFriendRequest(String requestId) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/friend-requests/$requestId/reject'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['code'] == 200;
      }
      return false;
    } catch (e) {
      print('Reject friend request error: $e');
      return false;
    }
  }
  
  /// 获取未处理的好友请求数量
  Future<int> getUnreadRequestCount() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/friend-requests/unread-count'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data['code'] == 200) {
          return data['data'] ?? 0;
        }
      }
      return 0;
    } catch (e) {
      print('Get unread request count error: $e');
      return 0;
    }
  }
  
  /// 搜索用户
  Future<List<User>> searchUsers(String keyword) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/users/search?keyword=$keyword'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data['code'] == 200) {
          List<dynamic> list = data['data'] ?? [];
          return list.map((item) => User.fromJson(item)).toList();
        }
      }
      return [];
    } catch (e) {
      print('Search users error: $e');
      return [];
    }
  }
}
