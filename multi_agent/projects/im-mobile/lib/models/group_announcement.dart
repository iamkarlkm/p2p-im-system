/**
 * 群公告数据模型 - Flutter移动端
 * 
 * @author IM System
 * @version 1.0
 */

import 'dart:convert';

/// 群公告模型
class GroupAnnouncement {
  final String announcementId;
  final String groupId;
  final String authorId;
  final String authorName;
  final String? authorAvatar;
  final String title;
  final String content;
  final String? summary;
  final bool pinned;
  final bool edited;
  final DateTime? editedAt;
  final DateTime createdAt;
  final DateTime updatedAt;
  final int readCount;
  final int unreadCount;
  final int commentCount;

  GroupAnnouncement({
    required this.announcementId,
    required this.groupId,
    required this.authorId,
    required this.authorName,
    this.authorAvatar,
    required this.title,
    required this.content,
    this.summary,
    this.pinned = false,
    this.edited = false,
    this.editedAt,
    required this.createdAt,
    required this.updatedAt,
    this.readCount = 0,
    this.unreadCount = 0,
    this.commentCount = 0,
  });

  /// 从JSON创建
  factory GroupAnnouncement.fromJson(Map<String, dynamic> json) {
    return GroupAnnouncement(
      announcementId: json['announcementId'] as String,
      groupId: json['groupId'] as String,
      authorId: json['authorId'] as String,
      authorName: json['authorName'] as String,
      authorAvatar: json['authorAvatar'] as String?,
      title: json['title'] as String,
      content: json['content'] as String,
      summary: json['summary'] as String?,
      pinned: json['pinned'] as bool? ?? false,
      edited: json['edited'] as bool? ?? false,
      editedAt: json['editedAt'] != null
          ? DateTime.parse(json['editedAt'] as String)
          : null,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
      readCount: json['readCount'] as int? ?? 0,
      unreadCount: json['unreadCount'] as int? ?? 0,
      commentCount: json['commentCount'] as int? ?? 0,
    );
  }

  /// 转换为JSON
  Map<String, dynamic> toJson() {
    return {
      'announcementId': announcementId,
      'groupId': groupId,
      'authorId': authorId,
      'authorName': authorName,
      'authorAvatar': authorAvatar,
      'title': title,
      'content': content,
      'summary': summary,
      'pinned': pinned,
      'edited': edited,
      'editedAt': editedAt?.toIso8601String(),
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'readCount': readCount,
      'unreadCount': unreadCount,
      'commentCount': commentCount,
    };
  }

  /// 复制并修改
  GroupAnnouncement copyWith({
    String? announcementId,
    String? groupId,
    String? authorId,
    String? authorName,
    String? authorAvatar,
    String? title,
    String? content,
    String? summary,
    bool? pinned,
    bool? edited,
    DateTime? editedAt,
    DateTime? createdAt,
    DateTime? updatedAt,
    int? readCount,
    int? unreadCount,
    int? commentCount,
  }) {
    return GroupAnnouncement(
      announcementId: announcementId ?? this.announcementId,
      groupId: groupId ?? this.groupId,
      authorId: authorId ?? this.authorId,
      authorName: authorName ?? this.authorName,
      authorAvatar: authorAvatar ?? this.authorAvatar,
      title: title ?? this.title,
      content: content ?? this.content,
      summary: summary ?? this.summary,
      pinned: pinned ?? this.pinned,
      edited: edited ?? this.edited,
      editedAt: editedAt ?? this.editedAt,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      readCount: readCount ?? this.readCount,
      unreadCount: unreadCount ?? this.unreadCount,
      commentCount: commentCount ?? this.commentCount,
    );
  }

  @override
  String toString() {
    return 'GroupAnnouncement(announcementId: $announcementId, title: $title, pinned: $pinned)';
  }
}

/// 公告已读状态
class AnnouncementReadStatus {
  final String statusId;
  final String announcementId;
  final String userId;
  final bool read;
  final DateTime? readAt;

  AnnouncementReadStatus({
    required this.statusId,
    required this.announcementId,
    required this.userId,
    this.read = false,
    this.readAt,
  });

  factory AnnouncementReadStatus.fromJson(Map<String, dynamic> json) {
    return AnnouncementReadStatus(
      statusId: json['statusId'] as String,
      announcementId: json['announcementId'] as String,
      userId: json['userId'] as String,
      read: json['read'] as bool? ?? false,
      readAt: json['readAt'] != null
          ? DateTime.parse(json['readAt'] as String)
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'statusId': statusId,
      'announcementId': announcementId,
      'userId': userId,
      'read': read,
      'readAt': readAt?.toIso8601String(),
    };
  }
}

/// 创建公告请求
class CreateAnnouncementRequest {
  final String groupId;
  final String authorId;
  final String authorName;
  final String? authorAvatar;
  final String title;
  final String content;

  CreateAnnouncementRequest({
    required this.groupId,
    required this.authorId,
    required this.authorName,
    this.authorAvatar,
    required this.title,
    required this.content,
  });

  Map<String, dynamic> toJson() {
    return {
      'groupId': groupId,
      'authorId': authorId,
      'authorName': authorName,
      'authorAvatar': authorAvatar,
      'title': title,
      'content': content,
    };
  }
}

/// 更新公告请求
class UpdateAnnouncementRequest {
  final String userId;
  final String title;
  final String content;

  UpdateAnnouncementRequest({
    required this.userId,
    required this.title,
    required this.content,
  });

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'title': title,
      'content': content,
    };
  }
}

/// 批量标记已读请求
class BatchMarkReadRequest {
  final String groupId;
  final String userId;
  final List<String> announcementIds;

  BatchMarkReadRequest({
    required this.groupId,
    required this.userId,
    required this.announcementIds,
  });

  Map<String, dynamic> toJson() {
    return {
      'groupId': groupId,
      'userId': userId,
      'announcementIds': announcementIds,
    };
  }
}

/// 公告列表响应
class AnnouncementListResponse {
  final List<GroupAnnouncement> announcements;
  final int total;
  final int page;
  final int size;

  AnnouncementListResponse({
    required this.announcements,
    required this.total,
    required this.page,
    required this.size,
  });

  factory AnnouncementListResponse.fromJson(Map<String, dynamic> json) {
    return AnnouncementListResponse(
      announcements: (json['announcements'] as List<dynamic>)
          .map((e) => GroupAnnouncement.fromJson(e as Map<String, dynamic>))
          .toList(),
      total: json['total'] as int,
      page: json['page'] as int,
      size: json['size'] as int,
    );
  }
}

/// 公告已读统计
class AnnouncementReadStats {
  final String announcementId;
  final int readCount;
  final int unreadCount;
  final int totalMembers;

  AnnouncementReadStats({
    required this.announcementId,
    required this.readCount,
    required this.unreadCount,
    required this.totalMembers,
  });

  factory AnnouncementReadStats.fromJson(Map<String, dynamic> json) {
    return AnnouncementReadStats(
      announcementId: json['announcementId'] as String,
      readCount: json['readCount'] as int,
      unreadCount: json['unreadCount'] as int,
      totalMembers: json['totalMembers'] as int,
    );
  }
}
