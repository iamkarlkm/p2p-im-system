/**
 * 用户资料服务
 */

import 'package:im_mobile/models/user_profile.dart';
import 'package:im_mobile/services/api_client.dart';

class UserProfileService {
  final ApiClient _api = ApiClient();

  /**
   * 获取当前用户资料
   */
  Future<UserProfile> getMyProfile() async {
    final data = await _api.get('/api/v1/profile/me');
    return UserProfile.fromJson(data['data'] ?? data);
  }

  /**
   * 获取指定用户资料
   */
  Future<UserProfile> getProfile(int userId) async {
    final data = await _api.get('/api/v1/profile/$userId');
    return UserProfile.fromJson(data['data'] ?? data);
  }

  /**
   * 批量获取用户资料
   */
  Future<List<UserProfile>> getProfiles(List<int> userIds) async {
    final data = await _api.post('/api/v1/profile/batch', {'userIds': userIds});
    final list = data['data'] ?? data;
    return (list as List).map((e) => UserProfile.fromJson(e)).toList();
  }

  /**
   * 更新个人资料
   */
  Future<UserProfile> updateProfile(Map<String, dynamic> updates) async {
    final data = await _api.put('/api/v1/profile/me', updates);
    return UserProfile.fromJson(data['data'] ?? data);
  }

  /**
   * 更新在线状态
   */
  Future<UserProfile> updateOnlineStatus(OnlineStatus status, {String? statusText}) async {
    final data = await _api.put('/api/v1/profile/me/status', {
      'status': status.value,
      if (statusText != null) 'statusText': statusText,
    });
    return UserProfile.fromJson(data['data'] ?? data);
  }

  /**
   * 上传头像
   */
  Future<String> uploadAvatar(String base64Data) async {
    final data = await _api.post('/api/v1/profile/me/avatar', {
      'fileData': base64Data,
    });
    return data['data']['avatarUrl'] ?? '';
  }

  /**
   * 获取好友分组列表
   */
  Future<List<FriendGroup>> getFriendGroups() async {
    final data = await _api.get('/api/v1/profile/me/friend-groups');
    final list = data['data'] ?? data;
    return (list as List).map((e) => FriendGroup.fromJson(e)).toList();
  }

  /**
   * 创建好友分组
   */
  Future<FriendGroup> createFriendGroup(String groupName) async {
    final data = await _api.post('/api/v1/profile/me/friend-groups', {
      'groupName': groupName,
    });
    return FriendGroup.fromJson(data['data'] ?? data);
  }

  /**
   * 更新好友备注
   */
  Future<FriendRemark> updateFriendRemark(FriendRemark remark) async {
    final data = await _api.put(
      '/api/v1/profile/me/friend-remarks/${remark.friendId}',
      remark.toJson(),
    );
    return FriendRemark.fromJson(data['data'] ?? data);
  }

  /**
   * 获取好友备注
   */
  Future<FriendRemark?> getFriendRemark(int friendId) async {
    try {
      final data = await _api.get('/api/v1/profile/me/friend-remarks/$friendId');
      final remarkData = data['data'] ?? data;
      if (remarkData['id'] == null) return null;
      return FriendRemark.fromJson(remarkData);
    } catch (e) {
      return null;
    }
  }

  /**
   * 快速设置在线状态
   */
  Future<void> setOnlineStatus(OnlineStatus status) async {
    await updateOnlineStatus(status);
  }
}
