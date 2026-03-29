import 'package:dio/dio.dart';
import '../models/friend_group.dart';
import '../utils/http_client.dart';

class FriendGroupService {
  static final FriendGroupService _instance = FriendGroupService._internal();
  factory FriendGroupService() => _instance;
  FriendGroupService._internal();

  final Dio _dio = HttpClient().dio;
  static const String _basePath = '/api/v1/friend-groups';

  Future<List<FriendGroup>> getGroups() async {
    try {
      final response = await _dio.get(_basePath);
      final List<dynamic> data = response.data['data'] ?? [];
      return data.map((e) => FriendGroup.fromJson(e)).toList();
    } catch (e) {
      throw Exception('获取分组列表失败: $e');
    }
  }

  Future<FriendGroup> createGroup(CreateGroupRequest request) async {
    try {
      final response = await _dio.post(_basePath, data: request.toJson());
      return FriendGroup.fromJson(response.data['data']);
    } catch (e) {
      throw Exception('创建分组失败: $e');
    }
  }

  Future<FriendGroup> updateGroup(String groupId, UpdateGroupRequest request) async {
    try {
      final response = await _dio.put('$_basePath/$groupId', data: request.toJson());
      return FriendGroup.fromJson(response.data['data']);
    } catch (e) {
      throw Exception('更新分组失败: $e');
    }
  }

  Future<void> deleteGroup(String groupId) async {
    try {
      await _dio.delete('$_basePath/$groupId');
    } catch (e) {
      throw Exception('删除分组失败: $e');
    }
  }

  Future<List<FriendGroupMember>> getGroupMembers(String groupId) async {
    try {
      final response = await _dio.get('$_basePath/$groupId/members');
      final List<dynamic> data = response.data['data'] ?? [];
      return data.map((e) => FriendGroupMember.fromJson(e)).toList();
    } catch (e) {
      throw Exception('获取分组成员失败: $e');
    }
  }

  Future<void> addMember(String groupId, String friendId) async {
    try {
      await _dio.post('$_basePath/$groupId/members', data: {'friendId': friendId});
    } catch (e) {
      throw Exception('添加成员失败: $e');
    }
  }

  Future<void> removeMember(String groupId, String friendId) async {
    try {
      await _dio.delete('$_basePath/$groupId/members/$friendId');
    } catch (e) {
      throw Exception('移除成员失败: $e');
    }
  }

  Future<void> moveMember(String groupId, String friendId, String targetGroupId) async {
    try {
      await _dio.post('$_basePath/$groupId/members/$friendId/move', 
          data: {'targetGroupId': targetGroupId});
    } catch (e) {
      throw Exception('移动成员失败: $e');
    }
  }

  Future<void> toggleStar(String groupId, String friendId, bool isStarred) async {
    try {
      await _dio.put('$_basePath/$groupId/members/$friendId/star', 
          data: {'isStarred': isStarred});
    } catch (e) {
      throw Exception('设置星标失败: $e');
    }
  }

  Future<void> toggleMute(String groupId, String friendId, bool isMuted) async {
    try {
      await _dio.put('$_basePath/$groupId/members/$friendId/mute', 
          data: {'isMuted': isMuted});
    } catch (e) {
      throw Exception('设置免打扰失败: $e');
    }
  }

  Future<void> reorderGroups(List<String> groupIds) async {
    try {
      await _dio.put('$_basePath/reorder', data: {'groupIds': groupIds});
    } catch (e) {
      throw Exception('排序分组失败: $e');
    }
  }
}
