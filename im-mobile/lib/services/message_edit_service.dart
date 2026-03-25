/// 消息编辑服务 - Dart Flutter 移动端服务
/// 用于与后端消息编辑 API 通信

import 'dart:convert';
import 'package:http/http.dart' as http;

/// 消息编辑数据模型
class MessageEdit {
  final String id;
  final String messageId;
  final String originalMessageId;
  final String userId;
  final String conversationId;
  final String contentType;
  final String content;
  final String? originalContent;
  final String editType;
  final int version;
  final bool isLatest;
  final String? clientMessageId;
  final String? editReason;
  final String? metadata;
  final String? contentHash;
  final String? originalContentHash;
  final String? diffPatch;
  final int? editSizeDelta;
  final int? editWordCount;
  final bool? hasAttachments;
  final String? attachmentsJson;
  final String? mentionsJson;
  final String? linksJson;
  final String? formattingJson;
  final int readCount;
  final int reactionCount;
  final int replyCount;
  final int editCount;
  final String auditStatus;
  final String? auditNotes;
  final String? auditorId;
  final String? auditTimestamp;
  final String status;
  final String privacyLevel;
  final String? deviceId;
  final String? clientVersion;
  final String? platform;
  final String? ipAddress;
  final String? userAgent;
  final String syncStatus;
  final String? conflictResolution;
  final String? conflictDetails;
  final DateTime createdAt;
  final DateTime updatedAt;
  final DateTime? deletedAt;
  final DateTime? archivedAt;
  final DateTime? expiresAt;
  final DateTime? versionExpiresAt;
  final DateTime? lastAccessedAt;
  final String? lastModifiedBy;
  final String? tags;
  final String? customFields;

  MessageEdit({
    required this.id,
    required this.messageId,
    required this.originalMessageId,
    required this.userId,
    required this.conversationId,
    required this.contentType,
    required this.content,
    this.originalContent,
    required this.editType,
    required this.version,
    required this.isLatest,
    this.clientMessageId,
    this.editReason,
    this.metadata,
    this.contentHash,
    this.originalContentHash,
    this.diffPatch,
    this.editSizeDelta,
    this.editWordCount,
    this.hasAttachments,
    this.attachmentsJson,
    this.mentionsJson,
    this.linksJson,
    this.formattingJson,
    required this.readCount,
    required this.reactionCount,
    required this.replyCount,
    required this.editCount,
    required this.auditStatus,
    this.auditNotes,
    this.auditorId,
    this.auditTimestamp,
    required this.status,
    required this.privacyLevel,
    this.deviceId,
    this.clientVersion,
    this.platform,
    this.ipAddress,
    this.userAgent,
    required this.syncStatus,
    this.conflictResolution,
    this.conflictDetails,
    required this.createdAt,
    required this.updatedAt,
    this.deletedAt,
    this.archivedAt,
    this.expiresAt,
    this.versionExpiresAt,
    this.lastAccessedAt,
    this.lastModifiedBy,
    this.tags,
    this.customFields,
  });

  factory MessageEdit.fromJson(Map<String, dynamic> json) {
    return MessageEdit(
      id: json['id'] as String,
      messageId: json['messageId'] as String,
      originalMessageId: json['originalMessageId'] as String,
      userId: json['userId'] as String,
      conversationId: json['conversationId'] as String,
      contentType: json['contentType'] as String,
      content: json['content'] as String,
      originalContent: json['originalContent'] as String?,
      editType: json['editType'] as String,
      version: json['version'] as int,
      isLatest: json['isLatest'] as bool,
      clientMessageId: json['clientMessageId'] as String?,
      editReason: json['editReason'] as String?,
      metadata: json['metadata'] as String?,
      contentHash: json['contentHash'] as String?,
      originalContentHash: json['originalContentHash'] as String?,
      diffPatch: json['diffPatch'] as String?,
      editSizeDelta: json['editSizeDelta'] as int?,
      editWordCount: json['editWordCount'] as int?,
      hasAttachments: json['hasAttachments'] as bool?,
      attachmentsJson: json['attachmentsJson'] as String?,
      mentionsJson: json['mentionsJson'] as String?,
      linksJson: json['linksJson'] as String?,
      formattingJson: json['formattingJson'] as String?,
      readCount: json['readCount'] as int,
      reactionCount: json['reactionCount'] as int,
      replyCount: json['replyCount'] as int,
      editCount: json['editCount'] as int,
      auditStatus: json['auditStatus'] as String,
      auditNotes: json['auditNotes'] as String?,
      auditorId: json['auditorId'] as String?,
      auditTimestamp: json['auditTimestamp'] as String?,
      status: json['status'] as String,
      privacyLevel: json['privacyLevel'] as String,
      deviceId: json['deviceId'] as String?,
      clientVersion: json['clientVersion'] as String?,
      platform: json['platform'] as String?,
      ipAddress: json['ipAddress'] as String?,
      userAgent: json['userAgent'] as String?,
      syncStatus: json['syncStatus'] as String,
      conflictResolution: json['conflictResolution'] as String?,
      conflictDetails: json['conflictDetails'] as String?,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
      deletedAt: json['deletedAt'] != null ? DateTime.parse(json['deletedAt'] as String) : null,
      archivedAt: json['archivedAt'] != null ? DateTime.parse(json['archivedAt'] as String) : null,
      expiresAt: json['expiresAt'] != null ? DateTime.parse(json['expiresAt'] as String) : null,
      versionExpiresAt: json['versionExpiresAt'] != null ? DateTime.parse(json['versionExpiresAt'] as String) : null,
      lastAccessedAt: json['lastAccessedAt'] != null ? DateTime.parse(json['lastAccessedAt'] as String) : null,
      lastModifiedBy: json['lastModifiedBy'] as String?,
      tags: json['tags'] as String?,
      customFields: json['customFields'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'messageId': messageId,
      'originalMessageId': originalMessageId,
      'userId': userId,
      'conversationId': conversationId,
      'contentType': contentType,
      'content': content,
      'originalContent': originalContent,
      'editType': editType,
      'version': version,
      'isLatest': isLatest,
      'clientMessageId': clientMessageId,
      'editReason': editReason,
      'metadata': metadata,
      'contentHash': contentHash,
      'originalContentHash': originalContentHash,
      'diffPatch': diffPatch,
      'editSizeDelta': editSizeDelta,
      'editWordCount': editWordCount,
      'hasAttachments': hasAttachments,
      'attachmentsJson': attachmentsJson,
      'mentionsJson': mentionsJson,
      'linksJson': linksJson,
      'formattingJson': formattingJson,
      'readCount': readCount,
      'reactionCount': reactionCount,
      'replyCount': replyCount,
      'editCount': editCount,
      'auditStatus': auditStatus,
      'auditNotes': auditNotes,
      'auditorId': auditorId,
      'auditTimestamp': auditTimestamp,
      'status': status,
      'privacyLevel': privacyLevel,
      'deviceId': deviceId,
      'clientVersion': clientVersion,
      'platform': platform,
      'ipAddress': ipAddress,
      'userAgent': userAgent,
      'syncStatus': syncStatus,
      'conflictResolution': conflictResolution,
      'conflictDetails': conflictDetails,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'deletedAt': deletedAt?.toIso8601String(),
      'archivedAt': archivedAt?.toIso8601String(),
      'expiresAt': expiresAt?.toIso8601String(),
      'versionExpiresAt': versionExpiresAt?.toIso8601String(),
      'lastAccessedAt': lastAccessedAt?.toIso8601String(),
      'lastModifiedBy': lastModifiedBy,
      'tags': tags,
      'customFields': customFields,
    };
  }
}

/// 创建消息编辑请求
class CreateMessageEditRequest {
  final String messageId;
  final String originalMessageId;
  final String userId;
  final String conversationId;
  final String contentType;
  final String content;
  final String? originalContent;
  final String? editType;
  final int? version;
  final bool? isLatest;
  final String? editReason;
  final String? metadata;
  final String? deviceId;
  final String? clientVersion;
  final String? platform;

  CreateMessageEditRequest({
    required this.messageId,
    required this.originalMessageId,
    required this.userId,
    required this.conversationId,
    required this.contentType,
    required this.content,
    this.originalContent,
    this.editType,
    this.version,
    this.isLatest,
    this.editReason,
    this.metadata,
    this.deviceId,
    this.clientVersion,
    this.platform,
  });

  Map<String, dynamic> toJson() {
    return {
      'messageId': messageId,
      'originalMessageId': originalMessageId,
      'userId': userId,
      'conversationId': conversationId,
      'contentType': contentType,
      'content': content,
      'originalContent': originalContent,
      'editType': editType,
      'version': version,
      'isLatest': isLatest,
      'editReason': editReason,
      'metadata': metadata,
      'deviceId': deviceId,
      'clientVersion': clientVersion,
      'platform': platform,
    };
  }
}

/// 更新消息编辑请求
class UpdateMessageEditRequest {
  final String? content;
  final String? editReason;
  final String? metadata;
  final String? status;
  final String? auditStatus;
  final String? auditNotes;
  final String? auditorId;
  final String? tags;
  final String? customFields;

  UpdateMessageEditRequest({
    this.content,
    this.editReason,
    this.metadata,
    this.status,
    this.auditStatus,
    this.auditNotes,
    this.auditorId,
    this.tags,
    this.customFields,
  });

  Map<String, dynamic> toJson() {
    return {
      if (content != null) 'content': content,
      if (editReason != null) 'editReason': editReason,
      if (metadata != null) 'metadata': metadata,
      if (status != null) 'status': status,
      if (auditStatus != null) 'auditStatus': auditStatus,
      if (auditNotes != null) 'auditNotes': auditNotes,
      if (auditorId != null) 'auditorId': auditorId,
      if (tags != null) 'tags': tags,
      if (customFields != null) 'customFields': customFields,
    };
  }
}

/// 审核请求
class AuditRequest {
  final String auditStatus;
  final String? auditNotes;
  final String? auditorId;

  AuditRequest({
    required this.auditStatus,
    this.auditNotes,
    this.auditorId,
  });

  Map<String, dynamic> toJson() {
    return {
      'auditStatus': auditStatus,
      if (auditNotes != null) 'auditNotes': auditNotes,
      if (auditorId != null) 'auditorId': auditorId,
    };
  }
}

/// 搜索参数
class SearchParams {
  final String? messageId;
  final String? userId;
  final String? conversationId;
  final String? editType;
  final String? status;
  final String? auditStatus;
  final String? platform;
  final DateTime? startDate;
  final DateTime? endDate;

  SearchParams({
    this.messageId,
    this.userId,
    this.conversationId,
    this.editType,
    this.status,
    this.auditStatus,
    this.platform,
    this.startDate,
    this.endDate,
  });

  Map<String, String> toQueryParams() {
    return {
      if (messageId != null) 'messageId': messageId!,
      if (userId != null) 'userId': userId!,
      if (conversationId != null) 'conversationId': conversationId!,
      if (editType != null) 'editType': editType!,
      if (status != null) 'status': status!,
      if (auditStatus != null) 'auditStatus': auditStatus!,
      if (platform != null) 'platform': platform!,
      if (startDate != null) 'startDate': startDate!.toIso8601String(),
      if (endDate != null) 'endDate': endDate!.toIso8601String(),
    };
  }
}

/// 消息编辑服务类
class MessageEditService {
  final String baseUrl;
  final http.Client _client;

  MessageEditService({
    this.baseUrl = '/api/message-edits',
    http.Client? client,
  }) : _client = client ?? http.Client();

  /// 创建新的消息编辑记录
  Future<MessageEdit> createMessageEdit(CreateMessageEditRequest request) async {
    final response = await _client.post(
      Uri.parse(baseUrl),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 201) {
      return MessageEdit.fromJson(jsonDecode(response.body));
    } else {
      throw ApiException('Failed to create message edit: ${response.statusCode}');
    }
  }

  /// 获取消息的所有编辑版本
  Future<List<MessageEdit>> getMessageEdits(String messageId) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/message/$messageId'),
    );

    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => MessageEdit.fromJson(json)).toList();
    } else {
      throw ApiException('Failed to get message edits: ${response.statusCode}');
    }
  }

  /// 获取消息的特定版本
  Future<MessageEdit> getMessageEditByVersion(String messageId, int version) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/message/$messageId/version/$version'),
    );

    if (response.statusCode == 200) {
      return MessageEdit.fromJson(jsonDecode(response.body));
    } else {
      throw ApiException('Failed to get message edit version: ${response.statusCode}');
    }
  }

  /// 获取消息的最新版本
  Future<MessageEdit> getLatestMessageEdit(String messageId) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/message/$messageId/latest'),
    );

    if (response.statusCode == 200) {
      return MessageEdit.fromJson(jsonDecode(response.body));
    } else {
      throw ApiException('Failed to get latest message edit: ${response.statusCode}');
    }
  }

  /// 更新消息编辑记录
  Future<MessageEdit> updateMessageEdit(String editId, UpdateMessageEditRequest request) async {
    final response = await _client.put(
      Uri.parse('$baseUrl/$editId'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 200) {
      return MessageEdit.fromJson(jsonDecode(response.body));
    } else {
      throw ApiException('Failed to update message edit: ${response.statusCode}');
    }
  }

  /// 删除消息编辑记录（软删除）
  Future<void> deleteMessageEdit(String editId) async {
    final response = await _client.delete(
      Uri.parse('$baseUrl/$editId'),
    );

    if (response.statusCode != 204) {
      throw ApiException('Failed to delete message edit: ${response.statusCode}');
    }
  }

  /// 恢复已删除的消息编辑记录
  Future<MessageEdit> restoreMessageEdit(String editId) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/$editId/restore'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({}),
    );

    if (response.statusCode == 200) {
      return MessageEdit.fromJson(jsonDecode(response.body));
    } else {
      throw ApiException('Failed to restore message edit: ${response.statusCode}');
    }
  }

  /// 审核消息编辑记录
  Future<MessageEdit> auditMessageEdit(String editId, AuditRequest request) async {
    final response = await _client.post(
      Uri.parse('$baseUrl/$editId/audit'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 200) {
      return MessageEdit.fromJson(jsonDecode(response.body));
    } else {
      throw ApiException('Failed to audit message edit: ${response.statusCode}');
    }
  }

  /// 搜索消息编辑记录
  Future<List<MessageEdit>> searchMessageEdits(SearchParams params) async {
    final queryParams = params.toQueryParams();
    final uri = Uri.parse('$baseUrl/search').replace(
      queryParameters: queryParams.isNotEmpty ? queryParams : null,
    );

    final response = await _client.get(uri);

    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => MessageEdit.fromJson(json)).toList();
    } else {
      throw ApiException('Failed to search message edits: ${response.statusCode}');
    }
  }

  /// 获取用户的编辑统计
  Future<Map<String, dynamic>> getUserEditStats(String userId) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/user/$userId/stats'),
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw ApiException('Failed to get user edit stats: ${response.statusCode}');
    }
  }

  /// 获取会话的编辑统计
  Future<Map<String, dynamic>> getConversationEditStats(String conversationId) async {
    final response = await _client.get(
      Uri.parse('$baseUrl/conversation/$conversationId/stats'),
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw ApiException('Failed to get conversation edit stats: ${response.statusCode}');
    }
  }

  /// 健康检查
  Future<Map<String, dynamic>> healthCheck() async {
    final response = await _client.get(
      Uri.parse('$baseUrl/health'),
    );

    if (response.statusCode == 200) {
      return jsonDecode(response.body);
    } else {
      throw ApiException('Health check failed: ${response.statusCode}');
    }
  }

  /// 关闭客户端
  void dispose() {
    _client.close();
  }
}

/// API 异常
class ApiException implements Exception {
  final String message;
  ApiException(this.message);

  @override
  String toString() => 'ApiException: $message';
}

/// 编辑类型扩展
extension EditTypeExtension on String {
  String get displayName {
    const displayNames = {
      'CREATE': '创建',
      'EDIT': '编辑',
      'REPLACE': '替换',
      'CORRECT': '纠正',
      'ENHANCE': '增强',
      'ROLLBACK': '回滚',
    };
    return displayNames[this] ?? this;
  }
}

/// 状态扩展
extension StatusExtension on String {
  String get displayName {
    const displayNames = {
      'ACTIVE': '活跃',
      'DELETED': '已删除',
      'ARCHIVED': '已归档',
      'HIDDEN': '隐藏',
    };
    return displayNames[this] ?? this;
  }
}

/// 审核状态扩展
extension AuditStatusExtension on String {
  String get displayName {
    const displayNames = {
      'PENDING': '待审核',
      'APPROVED': '已批准',
      'REJECTED': '已拒绝',
      'FLAGGED': '已标记',
    };
    return displayNames[this] ?? this;
  }
}
