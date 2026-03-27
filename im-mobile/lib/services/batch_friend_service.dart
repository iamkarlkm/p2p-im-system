import 'package:dio/dio.dart';
import '../models/user.dart';
import '../utils/http_util.dart';

/// 批量好友操作服务
class BatchFriendService {
  static final BatchFriendService _instance = BatchFriendService._internal();
  factory BatchFriendService() => _instance;
  BatchFriendService._internal();

  final Dio _dio = HttpUtil().dio;

  /// 获取不在指定分组中的好友
  Future<List<User>> getFriendsWithoutGroup(String? excludeGroupId) async {
    try {
      final response = await _dio.get(
        '/api/friends/batch/available',
        queryParameters: excludeGroupId != null
            ? {'excludeGroupId': excludeGroupId}
            : null,
      );
      
      if (response.data['code'] == 200) {
        final List<dynamic> data = response.data['data'] ?? [];
        return data.map((json) => User.fromJson(json)).toList();
      }
      return [];
    } catch (e) {
      throw Exception('获取好友列表失败: $e');
    }
  }

  /// 批量移动好友到分组
  Future<void> batchMoveToGroup(List<String> friendIds, String groupId) async {
    try {
      final response = await _dio.post(
        '/api/friends/batch/move',
        data: {
          'friendIds': friendIds,
          'groupId': groupId,
        },
      );
      
      if (response.data['code'] != 200) {
        throw Exception(response.data['message'] ?? '移动失败');
      }
    } catch (e) {
      throw Exception('批量移动失败: $e');
    }
  }

  /// 批量从分组移除好友
  Future<void> batchRemoveFromGroup(List<String> friendIds) async {
    try {
      final response = await _dio.post(
        '/api/friends/batch/remove-from-group',
        data: {
          'friendIds': friendIds,
        },
      );
      
      if (response.data['code'] != 200) {
        throw Exception(response.data['message'] ?? '移除失败');
      }
    } catch (e) {
      throw Exception('批量移除失败: $e');
    }
  }

  /// 批量设置星标好友
  Future<void> batchSetStarred(List<String> friendIds, bool isStarred) async {
    try {
      final response = await _dio.post(
        '/api/friends/batch/star',
        data: {
          'friendIds': friendIds,
          'isStarred': isStarred,
        },
      );
      
      if (response.data['code'] != 200) {
        throw Exception(response.data['message'] ?? '设置失败');
      }
    } catch (e) {
      throw Exception('批量设置星标失败: $e');
    }
  }

  /// 批量设置消息免打扰
  Future<void> batchSetMute(List<String> friendIds, bool isMuted) async {
    try {
      final response = await _dio.post(
        '/api/friends/batch/mute',
        data: {
          'friendIds': friendIds,
          'isMuted': isMuted,
        },
      );
      
      if (response.data['code'] != 200) {
        throw Exception(response.data['message'] ?? '设置失败');
      }
    } catch (e) {
      throw Exception('批量设置免打扰失败: $e');
    }
  }

  /// 批量删除好友
  Future<void> batchDeleteFriends(List<String> friendIds) async {
    try {
      final response = await _dio.post(
        '/api/friends/batch/delete',
        data: {
          'friendIds': friendIds,
        },
      );
      
      if (response.data['code'] != 200) {
        throw Exception(response.data['message'] ?? '删除失败');
      }
    } catch (e) {
      throw Exception('批量删除失败: $e');
    }
  }

  /// 获取批量操作进度
  Future<BatchOperationProgress?> getBatchProgress(String operationId) async {
    try {
      final response = await _dio.get(
        '/api/friends/batch/progress/$operationId',
      );
      
      if (response.data['code'] == 200) {
        return BatchOperationProgress.fromJson(response.data['data']);
      }
      return null;
    } catch (e) {
      return null;
    }
  }
}

/// 批量操作进度
class BatchOperationProgress {
  final String operationId;
  final String status; // pending, processing, completed, failed
  final int totalCount;
  final int processedCount;
  final int successCount;
  final int failedCount;
  final String? errorMessage;
  final DateTime createdAt;
  final DateTime? completedAt;

  BatchOperationProgress({
    required this.operationId,
    required this.status,
    required this.totalCount,
    required this.processedCount,
    required this.successCount,
    required this.failedCount,
    this.errorMessage,
    required this.createdAt,
    this.completedAt,
  });

  factory BatchOperationProgress.fromJson(Map<String, dynamic> json) {
    return BatchOperationProgress(
      operationId: json['operationId'],
      status: json['status'],
      totalCount: json['totalCount'],
      processedCount: json['processedCount'],
      successCount: json['successCount'],
      failedCount: json['failedCount'],
      errorMessage: json['errorMessage'],
      createdAt: DateTime.parse(json['createdAt']),
      completedAt: json['completedAt'] != null
          ? DateTime.parse(json['completedAt'])
          : null,
    );
  }

  double get progressPercent => totalCount > 0
      ? (processedCount / totalCount) * 100
      : 0;

  bool get isCompleted => status == 'completed' || status == 'failed';
  bool get isProcessing => status == 'processing';
}
