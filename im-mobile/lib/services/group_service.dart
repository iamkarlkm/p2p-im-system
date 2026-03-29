import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/group.dart';

class GroupService {
  static const String _baseUrl = 'http://localhost:8080/api';
  
  String? _token;
  
  void setToken(String token) {
    _token = token;
  }
  
  Map<String, String> get _headers => {
    'Content-Type': 'application/json',
    'Authorization': 'Bearer $_token',
  };
  
  /// 获取用户加入的群组列表
  Future<List<Group>> getMyGroups() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/groups/my'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data['code'] == 200) {
          List<dynamic> list = data['data'] ?? [];
          return list.map((item) {
            // 从item中提取group信息
            final groupData = item['group'] ?? item;
            return Group.fromJson(groupData);
          }).toList();
        }
      }
      return [];
    } catch (e) {
      print('Get my groups error: $e');
      return [];
    }
  }
  
  /// 获取群组信息
  Future<Group?> getGroup(String groupId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/groups/$groupId'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data['code'] == 200) {
          return Group.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('Get group error: $e');
      return null;
    }
  }
  
  /// 创建群组
  Future<Group?> createGroup(String groupName, {String? avatarUrl, String? notice}) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/groups'),
        headers: _headers,
        body: json.encode({
          'groupName': groupName,
          'avatarUrl': avatarUrl,
          'notice': notice,
        }),
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data['code'] == 200) {
          return Group.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('Create group error: $e');
      return null;
    }
  }
  
  /// 加入群组
  Future<bool> joinGroup(String groupId, {String? nickname}) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/groups/$groupId/join'),
        headers: _headers,
        body: json.encode({
          'nickname': nickname,
        }),
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['code'] == 200;
      }
      return false;
    } catch (e) {
      print('Join group error: $e');
      return false;
    }
  }
  
  /// 退出群组
  Future<bool> leaveGroup(String groupId) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/groups/$groupId/leave'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['code'] == 200;
      }
      return false;
    } catch (e) {
      print('Leave group error: $e');
      return false;
    }
  }
  
  /// 获取群成员列表
  Future<List<GroupMember>> getGroupMembers(String groupId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/groups/$groupId/members'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data['code'] == 200) {
          List<dynamic> list = data['data'] ?? [];
          return list.map((item) {
            Map<String, dynamic> memberData = item['member'] ?? {};
            memberData['user'] = item['user'];
            return GroupMember.fromJson(memberData);
          }).toList();
        }
      }
      return [];
    } catch (e) {
      print('Get group members error: $e');
      return [];
    }
  }
  
  /// 更新群组信息
  Future<Group?> updateGroup(String groupId, {String? groupName, String? avatarUrl, String? notice}) async {
    try {
      final response = await http.put(
        Uri.parse('$_baseUrl/groups/$groupId'),
        headers: _headers,
        body: json.encode({
          'groupName': groupName,
          'avatarUrl': avatarUrl,
          'notice': notice,
        }),
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data['code'] == 200) {
          return Group.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('Update group error: $e');
      return null;
    }
  }
  
  /// 邀请用户加入群组
  Future<bool> inviteUser(String groupId, String userId) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/groups/$groupId/invite'),
        headers: _headers,
        body: json.encode({
          'userId': userId,
        }),
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['code'] == 200;
      }
      return false;
    } catch (e) {
      print('Invite user error: $e');
      return false;
    }
  }
  
  /// 将用户移出群组
  Future<bool> removeMember(String groupId, String userId) async {
    try {
      final response = await http.delete(
        Uri.parse('$_baseUrl/groups/$groupId/members/$userId'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['code'] == 200;
      }
      return false;
    } catch (e) {
      print('Remove member error: $e');
      return false;
    }
  }
  
  /// 解散群组
  Future<bool> dissolveGroup(String groupId) async {
    try {
      final response = await http.delete(
        Uri.parse('$_baseUrl/groups/$groupId'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['code'] == 200;
      }
      return false;
    } catch (e) {
      print('Dissolve group error: $e');
      return false;
    }
  }
  
  /// 搜索群组
  Future<List<Group>> searchGroups(String keyword) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/groups/search?keyword=$keyword'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data['code'] == 200) {
          List<dynamic> list = data['data'] ?? [];
          return list.map((item) => Group.fromJson(item)).toList();
        }
      }
      return [];
    } catch (e) {
      print('Search groups error: $e');
      return [];
    }
  }
  
  /// 获取群组公告
  Future<String?> getGroupNotice(String groupId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/groups/$groupId/notice'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        if (data['code'] == 200) {
          return data['data'];
        }
      }
      return null;
    } catch (e) {
      print('Get group notice error: $e');
      return null;
    }
  }
  
  /// 更新群组公告
  Future<bool> updateGroupNotice(String groupId, String notice) async {
    try {
      final response = await http.put(
        Uri.parse('$_baseUrl/groups/$groupId/notice'),
        headers: _headers,
        body: json.encode({
          'notice': notice,
        }),
      );
      
      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['code'] == 200;
      }
      return false;
    } catch (e) {
      print('Update group notice error: $e');
      return false;
    }
  }
}
