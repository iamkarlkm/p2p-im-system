import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/group_management_log.dart';
import '../utils/api_client.dart';

/// 群管理日志服务
class GroupManagementLogService {
  final String baseUrl = '/api/v1/group-management-logs';

  /// 记录群管理操作日志
  Future<GroupManagementLog> logOperation(GroupManagementLogDTO logDTO) async {
    try {
      final response = await ApiClient.post(baseUrl, data: logDTO.toJson());
      return GroupManagementLog.fromJson(response.data);
    } catch (e) {
      throw ApiException('记录群管理操作日志失败: $e');
    }
  }

  /// 批量记录操作日志
  Future<List<GroupManagementLog>> batchLogOperations(
      List<GroupManagementLogDTO> logDTOs) async {
    try {
      final response = await ApiClient.post(
        '$baseUrl/batch',
        data: jsonEncode(logDTOs.map((e) => e.toJson()).toList()),
      );
      return (response.data as List)
          .map((e) => GroupManagementLog.fromJson(e))
          .toList();
    } catch (e) {
      throw ApiException('批量记录群管理操作日志失败: $e');
    }
  }

  /// 根据 ID 查询日志
  Future<GroupManagementLog> getLogById(String id) async {
    try {
      final response = await ApiClient.get('$baseUrl/$id');
      return GroupManagementLog.fromJson(response.data);
    } catch (e) {
      throw ApiException('查询日志失败: $e');
    }
  }

  /// 根据群组 ID 查询日志
  Future<List<GroupManagementLog>> getLogsByGroupId(String groupId) async {
    try {
      final response = await ApiClient.get('$baseUrl/group/$groupId');
      return (response.data as List)
          .map((e) => GroupManagementLog.fromJson(e))
          .toList();
    } catch (e) {
      throw ApiException('查询群组日志失败: $e');
    }
  }

  /// 根据群组 ID 分页查询日志
  Future<Map<String, dynamic>> getLogsByGroupIdPage(
    String groupId, {
    int page = 0,
    int size = 20,
    String sort = 'createdAt,desc',
  }) async {
    try {
      final response = await ApiClient.get(
        '$baseUrl/group/$groupId/page',
        queryParameters: {'page': page, 'size': size, 'sort': sort},
      );
      return {
        'content': (response.data['content'] as List)
            .map((e) => GroupManagementLog.fromJson(e))
            .toList(),
        'totalElements': response.data['totalElements'],
        'totalPages': response.data['totalPages'],
      };
    } catch (e) {
      throw ApiException('分页查询群组日志失败: $e');
    }
  }

  /// 根据操作者 ID 查询日志
  Future<List<GroupManagementLog>> getLogsByOperatorId(String operatorId) async {
    try {
      final response = await ApiClient.get('$baseUrl/operator/$operatorId');
      return (response.data as List)
          .map((e) => GroupManagementLog.fromJson(e))
          .toList();
    } catch (e) {
      throw ApiException('查询操作者日志失败: $e');
    }
  }

  /// 根据目标用户 ID 查询日志
  Future<List<GroupManagementLog>> getLogsByTargetUserId(String targetUserId) async {
    try {
      final response = await ApiClient.get('$baseUrl/target/$targetUserId');
      return (response.data as List)
          .map((e) => GroupManagementLog.fromJson(e))
          .toList();
    } catch (e) {
      throw ApiException('查询目标用户日志失败: $e');
    }
  }

  /// 根据操作类型查询日志
  Future<List<GroupManagementLog>> getLogsByActionType(String actionType) async {
    try {
      final response = await ApiClient.get('$baseUrl/action/$actionType');
      return (response.data as List)
          .map((e) => GroupManagementLog.fromJson(e))
          .toList();
    } catch (e) {
      throw ApiException('查询操作类型日志失败: $e');
    }
  }

  /// 根据时间范围查询日志
  Future<List<GroupManagementLog>> getLogsByTimeRange(
    DateTime startDate,
    DateTime endDate,
  ) async {
    try {
      final response = await ApiClient.get(
        '$baseUrl/time-range',
        queryParameters: {
          'start': startDate.toIso8601String(),
          'end': endDate.toIso8601String(),
        },
      );
      return (response.data as List)
          .map((e) => GroupManagementLog.fromJson(e))
          .toList();
    } catch (e) {
      throw ApiException('查询时间范围日志失败: $e');
    }
  }

  /// 高级搜索日志
  Future<Map<String, dynamic>> searchLogs({
    String? groupId,
    String? operatorId,
    String? targetUserId,
    String? actionType,
    String? actionSubType,
    String? result,
    bool? important,
    bool? needNotification,
    bool? notified,
    DateTime? startDate,
    DateTime? endDate,
    int page = 0,
    int size = 20,
    String sort = 'createdAt,desc',
  }) async {
    try {
      final params = <String, dynamic>{
        if (groupId != null) 'groupId': groupId,
        if (operatorId != null) 'operatorId': operatorId,
        if (targetUserId != null) 'targetUserId': targetUserId,
        if (actionType != null) 'actionType': actionType,
        if (actionSubType != null) 'actionSubType': actionSubType,
        if (result != null) 'result': result,
        if (important != null) 'important': important,
        if (needNotification != null) 'needNotification': needNotification,
        if (notified != null) 'notified': notified,
        if (startDate != null) 'startDate': startDate.toIso8601String(),
        if (endDate != null) 'endDate': endDate.toIso8601String(),
        'page': page,
        'size': size,
        'sort': sort,
      };

      final response = await ApiClient.get('$baseUrl/search', queryParameters: params);
      return {
        'content': (response.data['content'] as List)
            .map((e) => GroupManagementLog.fromJson(e))
            .toList(),
        'totalElements': response.data['totalElements'],
        'totalPages': response.data['totalPages'],
      };
    } catch (e) {
      throw ApiException('搜索日志失败: $e');
    }
  }

  /// 获取群组最近的操作日志
  Future<List<GroupManagementLog>> getRecentLogsByGroupId(
    String groupId, {
    int limit = 10,
  }) async {
    try {
      final response = await ApiClient.get(
        '$baseUrl/group/$groupId/recent',
        queryParameters: {'limit': limit},
      );
      return (response.data as List)
          .map((e) => GroupManagementLog.fromJson(e))
          .toList();
    } catch (e) {
      throw ApiException('获取最近日志失败: $e');
    }
  }

  /// 获取需要通知的日志
  Future<List<GroupManagementLog>> getPendingNotificationLogs() async {
    try {
      final response = await ApiClient.get('$baseUrl/pending-notifications');
      return (response.data as List)
          .map((e) => GroupManagementLog.fromJson(e))
          .toList();
    } catch (e) {
      throw ApiException('获取待通知日志失败: $e');
    }
  }

  /// 获取重要操作日志
  Future<List<GroupManagementLog>> getImportantLogs() async {
    try {
      final response = await ApiClient.get('$baseUrl/important');
      return (response.data as List)
          .map((e) => GroupManagementLog.fromJson(e))
          .toList();
    } catch (e) {
      throw ApiException('获取重要日志失败: $e');
    }
  }

  /// 标记日志为已通知
  Future<int> markLogsAsNotified(List<String> logIds) async {
    try {
      final response = await ApiClient.put(
        '$baseUrl/mark-notified',
        data: jsonEncode(logIds),
      );
      return response.data;
    } catch (e) {
      throw ApiException('标记日志为已通知失败: $e');
    }
  }

  /// 获取操作统计信息
  Future<GroupManagementLogStatistics> getStatistics() async {
    try {
      final response = await ApiClient.get('$baseUrl/statistics');
      return GroupManagementLogStatistics.fromJson(response.data);
    } catch (e) {
      throw ApiException('获取统计信息失败: $e');
    }
  }

  /// 获取群组操作统计信息
  Future<GroupManagementLogStatistics> getStatisticsByGroupId(String groupId) async {
    try {
      final response = await ApiClient.get('$baseUrl/statistics/group/$groupId');
      return GroupManagementLogStatistics.fromJson(response.data);
    } catch (e) {
      throw ApiException('获取群组统计信息失败: $e');
    }
  }

  /// 获取热门操作类型
  Future<List<Map<String, dynamic>>> getTopActionTypes() async {
    try {
      final response = await ApiClient.get('$baseUrl/statistics/top-actions');
      return (response.data as List).map((e) => Map<String, dynamic>.from(e)).toList();
    } catch (e) {
      throw ApiException('获取热门操作类型失败: $e');
    }
  }

  /// 获取活跃操作者
  Future<List<Map<String, dynamic>>> getTopOperators() async {
    try {
      final response = await ApiClient.get('$baseUrl/statistics/top-operators');
      return (response.data as List).map((e) => Map<String, dynamic>.from(e)).toList();
    } catch (e) {
      throw ApiException('获取活跃操作者失败: $e');
    }
  }

  /// 导出日志为 CSV 格式
  Future<Uint8List> exportToCsv({
    String? groupId,
    DateTime? startDate,
    DateTime? endDate,
  }) async {
    try {
      final params = <String, dynamic>{
        if (groupId != null) 'groupId': groupId,
        if (startDate != null) 'startDate': startDate.toIso8601String(),
        if (endDate != null) 'endDate': endDate.toIso8601String(),
      };

      final response = await ApiClient.get(
        '$baseUrl/export/csv',
        queryParameters: params,
        options: Options(responseType: ResponseType.bytes),
      );
      return response.data;
    } catch (e) {
      throw ApiException('导出 CSV 失败: $e');
    }
  }

  /// 导出日志为 JSON 格式
  Future<Uint8List> exportToJson({
    String? groupId,
    DateTime? startDate,
    DateTime? endDate,
  }) async {
    try {
      final params = <String, dynamic>{
        if (groupId != null) 'groupId': groupId,
        if (startDate != null) 'startDate': startDate.toIso8601String(),
        if (endDate != null) 'endDate': endDate.toIso8601String(),
      };

      final response = await ApiClient.get(
        '$baseUrl/export/json',
        queryParameters: params,
        options: Options(responseType: ResponseType.bytes),
      );
      return response.data;
    } catch (e) {
      throw ApiException('导出 JSON 失败: $e');
    }
  }

  /// 批量归档日志
  Future<int> archiveLogs(List<String> logIds) async {
    try {
      final response = await ApiClient.put(
        '$baseUrl/archive',
        data: jsonEncode(logIds),
      );
      return response.data;
    } catch (e) {
      throw ApiException('归档日志失败: $e');
    }
  }

  /// 清理已归档的旧日志
  Future<int> cleanupArchivedLogs(DateTime cutoffDate) async {
    try {
      final response = await ApiClient.delete(
        '$baseUrl/cleanup',
        queryParameters: {'cutoffDate': cutoffDate.toIso8601String()},
      );
      return response.data;
    } catch (e) {
      throw ApiException('清理归档日志失败: $e');
    }
  }

  /// 检查重复操作
  Future<bool> checkDuplicateOperation({
    required String groupId,
    required String operatorId,
    required String actionType,
    String? targetUserId,
    int withinMinutes = 5,
  }) async {
    try {
      final params = <String, dynamic>{
        'groupId': groupId,
        'operatorId': operatorId,
        'actionType': actionType,
        'withinMinutes': withinMinutes,
        if (targetUserId != null) 'targetUserId': targetUserId,
      };

      final response = await ApiClient.get(
        '$baseUrl/check-duplicate',
        queryParameters: params,
      );
      return response.data;
    } catch (e) {
      throw ApiException('检查重复操作失败: $e');
    }
  }

  /// 健康检查
  Future<Map<String, String>> healthCheck() async {
    try {
      final response = await ApiClient.get('$baseUrl/health');
      return Map<String, String>.from(response.data);
    } catch (e) {
      throw ApiException('健康检查失败: $e');
    }
  }

  /// 创建添加成员日志
  GroupManagementLogDTO createMemberAddLog({
    required String groupId,
    required String operatorId,
    required String targetUserId,
    String operatorType = 'ADMIN',
    Map<String, dynamic>? details,
  }) {
    return GroupManagementLogDTO(
      groupId: groupId,
      operatorId: operatorId,
      operatorType: operatorType,
      targetUserId: targetUserId,
      actionType: 'MEMBER_ADD',
      description: '添加新成员',
      details: details,
      result: 'SUCCESS',
      important: false,
      needNotification: true,
    );
  }

  /// 创建移除成员日志
  GroupManagementLogDTO createMemberRemoveLog({
    required String groupId,
    required String operatorId,
    required String targetUserId,
    String operatorType = 'ADMIN',
    String? reason,
    Map<String, dynamic>? details,
  }) {
    return GroupManagementLogDTO(
      groupId: groupId,
      operatorId: operatorId,
      operatorType: operatorType,
      targetUserId: targetUserId,
      actionType: 'MEMBER_REMOVE',
      description: '移除成员${reason != null ? ' ($reason)' : ''}',
      details: details,
      result: 'SUCCESS',
      important: true,
      needNotification: true,
    );
  }

  /// 创建角色变更日志
  GroupManagementLogDTO createRoleChangeLog({
    required String groupId,
    required String operatorId,
    required String targetUserId,
    String operatorType = 'ADMIN',
    required String fromRole,
    required String toRole,
    Map<String, dynamic>? details,
  }) {
    return GroupManagementLogDTO(
      groupId: groupId,
      operatorId: operatorId,
      operatorType: operatorType,
      targetUserId: targetUserId,
      actionType: 'ROLE_CHANGE',
      description: '角色变更：$fromRole → $toRole',
      details: details,
      result: 'SUCCESS',
      important: true,
      needNotification: true,
    );
  }
}