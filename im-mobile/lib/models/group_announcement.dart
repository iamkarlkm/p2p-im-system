/// 群公告模型
/// 功能ID: #30
/// 功能名称: 群公告
/// 
/// @author developer-agent
/// @since 2026-03-30

class GroupAnnouncement {
  final int id;
  final int groupId;
  final String title;
  final String content;
  final int creatorId;
  final String creatorNickname;
  final String? creatorAvatar;
  final bool pinned;
  final DateTime? pinnedAt;
  final bool confirmed;
  final int readCount;
  final int totalMembers;
  final double readPercentage;
  final List<String> attachments;
  final DateTime createdAt;
  final DateTime updatedAt;
  final bool hasRead;

  GroupAnnouncement({
    required this.id,
    required this.groupId,
    required this.title,
    required this.content,
    required this.creatorId,
    required this.creatorNickname,
    this.creatorAvatar,
    required this.pinned,
    this.pinnedAt,
    required this.confirmed,
    required this.readCount,
    required this.totalMembers,
    required this.readPercentage,
    required this.attachments,
    required this.createdAt,
    required this.updatedAt,
    required this.hasRead,
  });

  factory GroupAnnouncement.fromJson(Map<String, dynamic> json) {
    return GroupAnnouncement(
      id: json['id'],
      groupId: json['groupId'],
      title: json['title'],
      content: json['content'],
      creatorId: json['creatorId'],
      creatorNickname: json['creatorNickname'] ?? '用户${json['creatorId']}',
      creatorAvatar: json['creatorAvatar'],
      pinned: json['pinned'] ?? false,
      pinnedAt: json['pinnedAt'] != null ? DateTime.parse(json['pinnedAt']) : null,
      confirmed: json['confirmed'] ?? false,
      readCount: json['readCount'] ?? 0,
      totalMembers: json['totalMembers'] ?? 0,
      readPercentage: (json['readPercentage'] ?? 0.0).toDouble(),
      attachments: List<String>.from(json['attachments'] ?? []),
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
      hasRead: json['hasRead'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'groupId': groupId,
      'title': title,
      'content': content,
      'creatorId': creatorId,
      'creatorNickname': creatorNickname,
      'creatorAvatar': creatorAvatar,
      'pinned': pinned,
      'pinnedAt': pinnedAt?.toIso8601String(),
      'confirmed': confirmed,
      'readCount': readCount,
      'totalMembers': totalMembers,
      'readPercentage': readPercentage,
      'attachments': attachments,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'hasRead': hasRead,
    };
  }

  /// 创建副本并修改指定字段
  GroupAnnouncement copyWith({
    int? id,
    int? groupId,
    String? title,
    String? content,
    int? creatorId,
    String? creatorNickname,
    String? creatorAvatar,
    bool? pinned,
    DateTime? pinnedAt,
    bool? confirmed,
    int? readCount,
    int? totalMembers,
    double? readPercentage,
    List<String>? attachments,
    DateTime? createdAt,
    DateTime? updatedAt,
    bool? hasRead,
  }) {
    return GroupAnnouncement(
      id: id ?? this.id,
      groupId: groupId ?? this.groupId,
      title: title ?? this.title,
      content: content ?? this.content,
      creatorId: creatorId ?? this.creatorId,
      creatorNickname: creatorNickname ?? this.creatorNickname,
      creatorAvatar: creatorAvatar ?? this.creatorAvatar,
      pinned: pinned ?? this.pinned,
      pinnedAt: pinnedAt ?? this.pinnedAt,
      confirmed: confirmed ?? this.confirmed,
      readCount: readCount ?? this.readCount,
      totalMembers: totalMembers ?? this.totalMembers,
      readPercentage: readPercentage ?? this.readPercentage,
      attachments: attachments ?? this.attachments,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      hasRead: hasRead ?? this.hasRead,
    );
  }
}

/// 创建公告请求
class CreateAnnouncementRequest {
  final int groupId;
  final String title;
  final String content;
  final bool pinned;
  final List<String> attachments;

  CreateAnnouncementRequest({
    required this.groupId,
    required this.title,
    required this.content,
    this.pinned = false,
    this.attachments = const [],
  });

  Map<String, dynamic> toJson() {
    return {
      'groupId': groupId,
      'title': title,
      'content': content,
      'pinned': pinned,
      'attachments': attachments,
    };
  }
}

/// 更新公告请求
class UpdateAnnouncementRequest {
  final String title;
  final String content;
  final bool pinned;
  final List<String> attachments;

  UpdateAnnouncementRequest({
    required this.title,
    required this.content,
    this.pinned = false,
    this.attachments = const [],
  });

  Map<String, dynamic> toJson() {
    return {
      'title': title,
      'content': content,
      'pinned': pinned,
      'attachments': attachments,
    };
  }
}
