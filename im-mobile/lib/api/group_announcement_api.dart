import 'package:dio/dio.dart';
import '../models/group_announcement.dart';

/// 群公告API客户端
/// 功能ID: #30
/// 功能名称: 群公告
/// 
/// @author developer-agent
/// @since 2026-03-30

class GroupAnnouncementApi {
  final Dio _dio;

  GroupAnnouncementApi(this._dio);

  /// 创建群公告
  Future<GroupAnnouncement> createAnnouncement(CreateAnnouncementRequest request) async {
    final response = await _dio.post(
      '/group-announcement/create',
      data: request.toJson(),
    );
    return GroupAnnouncement.fromJson(response.data);
  }

  /// 更新群公告
  Future<GroupAnnouncement> updateAnnouncement(int id, UpdateAnnouncementRequest request) async {
    final response = await _dio.put(
      '/group-announcement/$id',
      data: request.toJson(),
    );
    return GroupAnnouncement.fromJson(response.data);
  }

  /// 删除群公告
  Future<void> deleteAnnouncement(int id) async {
    await _dio.delete('/group-announcement/$id');
  }

  /// 获取公告详情
  Future<GroupAnnouncement> getAnnouncement(int id) async {
    final response = await _dio.get('/group-announcement/$id');
    return GroupAnnouncement.fromJson(response.data);
  }

  /// 获取群组的公告列表
  Future<List<GroupAnnouncement>> getGroupAnnouncements(int groupId) async {
    final response = await _dio.get('/group-announcement/group/$groupId');
    return (response.data as List)
        .map((json) => GroupAnnouncement.fromJson(json))
        .toList();
  }

  /// 分页获取群公告
  Future<{
    List<GroupAnnouncement> content,
    int totalElements,
    int totalPages,
  }> getGroupAnnouncementsPaged(int groupId, {int page = 0, int size = 20}) async {
    final response = await _dio.get(
      '/group-announcement/group/$groupId/paged',
      queryParameters: {'page': page, 'size': size},
    );
    final data = response.data;
    return (
      content: (data['content'] as List)
          .map((json) => GroupAnnouncement.fromJson(json))
          .toList(),
      totalElements: data['totalElements'] as int,
      totalPages: data['totalPages'] as int,
    );
  }

  /// 获取最新公告
  Future<GroupAnnouncement?> getLatestAnnouncement(int groupId) async {
    try {
      final response = await _dio.get('/group-announcement/group/$groupId/latest');
      return GroupAnnouncement.fromJson(response.data);
    } on DioException catch (e) {
      if (e.response?.statusCode == 404) {
        return null;
      }
      rethrow;
    }
  }

  /// 获取置顶公告
  Future<List<GroupAnnouncement>> getPinnedAnnouncements(int groupId) async {
    final response = await _dio.get('/group-announcement/group/$groupId/pinned');
    return (response.data as List)
        .map((json) => GroupAnnouncement.fromJson(json))
        .toList();
  }

  /// 标记公告已读
  Future<void> markAsRead(int id) async {
    await _dio.post('/group-announcement/$id/read');
  }

  /// 批量标记已读
  Future<void> markAllAsRead(int groupId) async {
    await _dio.post('/group-announcement/group/$groupId/read-all');
  }

  /// 置顶/取消置顶公告
  Future<void> pinAnnouncement(int id, bool pinned) async {
    await _dio.post(
      '/group-announcement/$id/pin',
      queryParameters: {'pinned': pinned},
    );
  }

  /// 获取已读人数
  Future<int> getReadCount(int id) async {
    final response = await _dio.get('/group-announcement/$id/read-count');
    return response.data['readCount'] as int;
  }

  /// 搜索公告
  Future<List<GroupAnnouncement>> searchAnnouncements(int groupId, String keyword) async {
    final response = await _dio.get(
      '/group-announcement/group/$groupId/search',
      queryParameters: {'keyword': keyword},
    );
    return (response.data as List)
        .map((json) => GroupAnnouncement.fromJson(json))
        .toList();
  }

  /// 获取未读公告数量
  Future<int> getUnreadCount(int groupId) async {
    final response = await _dio.get('/group-announcement/group/$groupId/unread-count');
    return response.data['unreadCount'] as int;
  }

  /// 检查用户是否为创建者
  Future<bool> isCreator(int id) async {
    final response = await _dio.get('/group-announcement/$id/is-creator');
    return response.data['isCreator'] as bool;
  }
}
