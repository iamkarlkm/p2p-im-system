import 'dart:convert';

/// 群管理日志模型
class GroupManagementLog {
  final String id;
  final String groupId;
  final String operatorId;
  final String operatorType;
  final String? targetUserId;
  final String actionType;
  final String? actionSubType;
  final String? description;
  final Map<String, dynamic>? details;
  final Map<String, dynamic>? beforeState;
  final Map<String, dynamic>? afterState;
  final String? ipAddress;
  final String? userAgent;
  final String? deviceInfo;
  final String result;
  final String? errorMessage;
  final bool important;
  final bool needNotification;
  final bool notified;
  final bool archived;
  final DateTime? archivedAt;
  final String? tenantId;
  final int? version;
  final DateTime createdAt;
  final DateTime updatedAt;

  GroupManagementLog({
    required this.id,
    required this.groupId,
    required this.operatorId,
    required this.operatorType,
    this.targetUserId,
    required this.actionType,
    this.actionSubType,
    this.description,
    this.details,
    this.beforeState,
    this.afterState,
    this.ipAddress,
    this.userAgent,
    this.deviceInfo,
    required this.result,
    this.errorMessage,
    this.important = false,
    this.needNotification = false,
    this.notified = false,
    this.archived = false,
    this.archivedAt,
    this.tenantId,
    this.version,
    required this.createdAt,
    required this.updatedAt,
  });

  factory GroupManagementLog.fromJson(Map<String, dynamic> json) {
    return GroupManagementLog(
      id: json['id'] as String,
      groupId: json['groupId'] as String,
      operatorId: json['operatorId'] as String,
      operatorType: json['operatorType'] as String,
      targetUserId: json['targetUserId'] as String?,
      actionType: json['actionType'] as String,
      actionSubType: json['actionSubType'] as String?,
      description: json['description'] as String?,
      details: json['details'] != null
          ? Map<String, dynamic>.from(json['details'])
          : null,
      beforeState: json['beforeState'] != null
          ? Map<String, dynamic>.from(json['beforeState'])
          : null,
      afterState: json['afterState'] != null
          ? Map<String, dynamic>.from(json['afterState'])
          : null,
      ipAddress: json['ipAddress'] as String?,
      userAgent: json['userAgent'] as String?,
      deviceInfo: json['deviceInfo'] as String?,
      result: json['result'] as String,
      errorMessage: json['errorMessage'] as String?,
      important: json['important'] as bool? ?? false,
      needNotification: json['needNotification'] as bool? ?? false,
      notified: json['notified'] as bool? ?? false,
      archived: json['archived'] as bool? ?? false,
      archivedAt: json['archivedAt'] != null
          ? DateTime.parse(json['archivedAt'])
          : null,
      tenantId: json['tenantId'] as String?,
      version: json['version'] as int?,
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'groupId': groupId,
      'operatorId': operatorId,
      'operatorType': operatorType,
      'targetUserId': targetUserId,
      'actionType': actionType,
      'actionSubType': actionSubType,
      'description': description,
      'details': details,
      'beforeState': beforeState,
      'afterState': afterState,
      'ipAddress': ipAddress,
      'userAgent': userAgent,
      'deviceInfo': deviceInfo,
      'result': result,
      'errorMessage': errorMessage,
      'important': important,
      'needNotification': needNotification,
      'notified': notified,
      'archived': archived,
      'archivedAt': archivedAt?.toIso8601String(),
      'tenantId': tenantId,
      'version': version,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
    };
  }

  /// 获取操作类型中文描述
  String get actionTypeDescription {
    switch (actionType) {
      case 'MEMBER_ADD':
        return '添加成员';
      case 'MEMBER_REMOVE':
        return '移除成员';
      case 'MEMBER_KICK':
        return '踢出成员';
      case 'MEMBER_LEAVE':
        return '成员离开';
      case 'ROLE_CHANGE':
        return '角色变更';
      case 'ADMIN_ADD':
        return '添加管理员';
      case 'ADMIN_REMOVE':
        return '移除管理员';
      case 'OWNER_TRANSFER':
        return '群主转让';
      case 'GROUP_CREATE':
        return '创建群组';
      case 'GROUP_DELETE':
        return '删除群组';
      case 'GROUP_UPDATE':
        return '更新群组信息';
      case 'GROUP_RENAME':
        return '重命名群组';
      case 'GROUP_AVATAR':
        return '更改群头像';
      case 'GROUP_DESC':
        return '更改群描述';
      case 'GROUP_SETTINGS':
        return '更改群设置';
      case 'ANNOUNCEMENT':
        return '发布公告';
      case 'ANNOUNCEMENT_EDIT':
        return '编辑公告';
      case 'ANNOUNCEMENT_DELETE':
        return '删除公告';
      case 'MUTE_MEMBER':
        return '禁言成员';
      case 'UNMUTE_MEMBER':
        return '解除禁言';
      case 'BAN_MEMBER':
        return '封禁成员';
      case 'UNBAN_MEMBER':
        return '解封成员';
      case 'INVITE_SEND':
        return '发送邀请';
      case 'INVITE_ACCEPT':
        return '接受邀请';
      case 'INVITE_REJECT':
        return '拒绝邀请';
      case 'INVITE_REVOKE':
        return '撤销邀请';
      case 'JOIN_REQUEST':
        return '加入请求';
      case 'JOIN_APPROVE':
        return '批准加入';
      case 'JOIN_REJECT':
        return '拒绝加入';
      case 'MESSAGE_DELETE':
        return '删除消息';
      case 'MESSAGE_PIN':
        return '置顶消息';
      case 'MESSAGE_UNPIN':
        return '取消置顶';
      case 'FILE_UPLOAD':
        return '上传文件';
      case 'FILE_DELETE':
        return '删除文件';
      case 'POLL_CREATE':
        return '创建投票';
      case 'POLL_CLOSE':
        return '关闭投票';
      case 'EVENT_CREATE':
        return '创建事件';
      case 'EVENT_UPDATE':
        return '更新事件';
      case 'EVENT_DELETE':
        return '删除事件';
      default:
        return actionType;
    }
  }

  /// 获取操作者类型中文描述
  String get operatorTypeDescription {
    switch (operatorType) {
      case 'SYSTEM':
        return '系统';
      case 'USER':
        return '用户';
      case 'ADMIN':
        return '管理员';
      case 'BOT':
        return '机器人';
      default:
        return operatorType;
    }
  }

  /// 获取操作结果中文描述
  String get resultDescription {
    switch (result) {
      case 'SUCCESS':
        return '成功';
      case 'FAILED':
        return '失败';
      case 'PARTIAL':
        return '部分成功';
      default:
        return result;
    }
  }

  /// 是否为成功操作
  bool get isSuccess => result == 'SUCCESS';

  /// 是否为失败操作
  bool get isFailed => result == 'FAILED';

  /// 是否需要关注
  bool get needsAttention => important || isFailed || needNotification;

  @override
  String toString() {
    return 'GroupManagementLog{id: $id, groupId: $groupId, actionType: $actionType, result: $result}';
  }
}

/// 群管理日志 DTO
class GroupManagementLogDTO {
  final String groupId;
  final String operatorId;
  final String operatorType;
  final String? targetUserId;
  final String actionType;
  final String? actionSubType;
  final String? description;
  final Map<String, dynamic>? details;
  final Map<String, dynamic>? beforeState;
  final Map<String, dynamic>? afterState;
  final String? ipAddress;
  final String? userAgent;
  final String? deviceInfo;
  final String result;
  final String? errorMessage;
  final bool important;
  final bool needNotification;
  final String? tenantId;

  GroupManagementLogDTO({
    required this.groupId,
    required this.operatorId,
    required this.operatorType,
    this.targetUserId,
    required this.actionType,
    this.actionSubType,
    this.description,
    this.details,
    this.beforeState,
    this.afterState,
    this.ipAddress,
    this.userAgent,
    this.deviceInfo,
    this.result = 'SUCCESS',
    this.errorMessage,
    this.important = false,
    this.needNotification = false,
    this.tenantId,
  });

  Map<String, dynamic> toJson() {
    return {
      'groupId': groupId,
      'operatorId': operatorId,
      'operatorType': operatorType,
      'targetUserId': targetUserId,
      'actionType': actionType,
      'actionSubType': actionSubType,
      'description': description,
      'details': details,
      'beforeState': beforeState,
      'afterState': afterState,
      'ipAddress': ipAddress,
      'userAgent': userAgent,
      'deviceInfo': deviceInfo,
      'result': result,
      'errorMessage': errorMessage,
      'important': important,
      'needNotification': needNotification,
      'tenantId': tenantId,
    };
  }

  /// 转换为日志实体
  GroupManagementLog toLog(String id, DateTime createdAt, DateTime updatedAt) {
    return GroupManagementLog(
      id: id,
      groupId: groupId,
      operatorId: operatorId,
      operatorType: operatorType,
      targetUserId: targetUserId,
      actionType: actionType,
      actionSubType: actionSubType,
      description: description,
      details: details,
      beforeState: beforeState,
      afterState: afterState,
      ipAddress: ipAddress,
      userAgent: userAgent,
      deviceInfo: deviceInfo,
      result: result,
      errorMessage: errorMessage,
      important: important,
      needNotification: needNotification,
      createdAt: createdAt,
      updatedAt: updatedAt,
    );
  }
}

/// 群管理日志统计信息
class GroupManagementLogStatistics {
  final int total;
  final int success;
  final int failed;
  final int partial;
  final int important;
  final int pendingNotification;

  GroupManagementLogStatistics({
    required this.total,
    required this.success,
    required this.failed,
    required this.partial,
    required this.important,
    required this.pendingNotification,
  });

  factory GroupManagementLogStatistics.fromJson(Map<String, dynamic> json) {
    return GroupManagementLogStatistics(
      total: json['total'] as int? ?? 0,
      success: json['success'] as int? ?? 0,
      failed: json['failed'] as int? ?? 0,
      partial: json['partial'] as int? ?? 0,
      important: json['important'] as int? ?? 0,
      pendingNotification: json['pendingNotification'] as int? ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'total': total,
      'success': success,
      'failed': failed,
      'partial': partial,
      'important': important,
      'pendingNotification': pendingNotification,
    };
  }

  /// 成功率
  double get successRate => total > 0 ? success / total : 0.0;

  /// 失败率
  double get failureRate => total > 0 ? failed / total : 0.0;

  @override
  String toString() {
    return 'GroupManagementLogStatistics{total: $total, success: $success, failed: $failed}';
  }
}

/// 群管理日志筛选条件
class GroupManagementLogFilter {
  final String? groupId;
  final String? operatorId;
  final String? targetUserId;
  final String? actionType;
  final String? actionSubType;
  final String? result;
  final bool? important;
  final bool? needNotification;
  final bool? notified;
  final DateTime? startDate;
  final DateTime? endDate;

  GroupManagementLogFilter({
    this.groupId,
    this.operatorId,
    this.targetUserId,
    this.actionType,
    this.actionSubType,
    this.result,
    this.important,
    this.needNotification,
    this.notified,
    this.startDate,
    this.endDate,
  });

  Map<String, dynamic> toMap() {
    return {
      if (groupId != null) 'groupId': groupId,
      if (operatorId != null) 'operatorId': operatorId,
      if (targetUserId != null) 'targetUserId': targetUserId,
      if (actionType != null) 'actionType': actionType,
      if (actionSubType != null) 'actionSubType': actionSubType,
      if (result != null) 'result': result,
      if (important != null) 'important': important,
      if (needNotification != null) 'needNotification': needNotification,
      if (notified != null) 'notified': notified,
      if (startDate != null) 'startDate': startDate?.toIso8601String(),
      if (endDate != null) 'endDate': endDate?.toIso8601String(),
    };
  }
}

/// API 异常
class ApiException implements Exception {
  final String message;
  ApiException(this.message);

  @override
  String toString() => 'ApiException: $message';
}