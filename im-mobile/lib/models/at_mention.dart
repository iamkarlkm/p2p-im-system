/**
 * @提及数据模型
 */
class AtMention {
  final int id;
  final int messageId;
  final int senderUserId;
  final String? senderNickname;
  final int mentionedUserId;
  final int? roomId;
  final bool isRead;
  final bool isAtAll;
  final bool notified;
  final DateTime mentionedAt;
  final String messagePreview;
  final String? conversationId;
  final String? roomName;

  AtMention({
    required this.id,
    required this.messageId,
    required this.senderUserId,
    this.senderNickname,
    required this.mentionedUserId,
    this.roomId,
    required this.isRead,
    required this.isAtAll,
    required this.notified,
    required this.mentionedAt,
    required this.messagePreview,
    this.conversationId,
    this.roomName,
  });

  factory AtMention.fromJson(Map<String, dynamic> json) {
    return AtMention(
      id: json['id'] ?? 0,
      messageId: json['messageId'] ?? 0,
      senderUserId: json['senderUserId'] ?? 0,
      senderNickname: json['senderNickname'],
      mentionedUserId: json['mentionedUserId'] ?? 0,
      roomId: json['roomId'],
      isRead: json['isRead'] ?? false,
      isAtAll: json['isAtAll'] ?? false,
      notified: json['notified'] ?? false,
      mentionedAt: DateTime.tryParse(json['mentionedAt'] ?? '') ?? DateTime.now(),
      messagePreview: json['messagePreview'] ?? '',
      conversationId: json['conversationId'],
      roomName: json['roomName'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'messageId': messageId,
      'senderUserId': senderUserId,
      'senderNickname': senderNickname,
      'mentionedUserId': mentionedUserId,
      'roomId': roomId,
      'isRead': isRead,
      'isAtAll': isAtAll,
      'notified': notified,
      'mentionedAt': mentionedAt.toIso8601String(),
      'messagePreview': messagePreview,
      'conversationId': conversationId,
      'roomName': roomName,
    };
  }

  AtMention copyWith({
    int? id,
    int? messageId,
    int? senderUserId,
    String? senderNickname,
    int? mentionedUserId,
    int? roomId,
    bool? isRead,
    bool? isAtAll,
    bool? notified,
    DateTime? mentionedAt,
    String? messagePreview,
    String? conversationId,
    String? roomName,
  }) {
    return AtMention(
      id: id ?? this.id,
      messageId: messageId ?? this.messageId,
      senderUserId: senderUserId ?? this.senderUserId,
      senderNickname: senderNickname ?? this.senderNickname,
      mentionedUserId: mentionedUserId ?? this.mentionedUserId,
      roomId: roomId ?? this.roomId,
      isRead: isRead ?? this.isRead,
      isAtAll: isAtAll ?? this.isAtAll,
      notified: notified ?? this.notified,
      mentionedAt: mentionedAt ?? this.mentionedAt,
      messagePreview: messagePreview ?? this.messagePreview,
      conversationId: conversationId ?? this.conversationId,
      roomName: roomName ?? this.roomName,
    );
  }
}

class AtMentionSettings {
  final int? id;
  final int userId;
  final bool enabled;
  final bool onlyAtAll;
  final bool allowStrangerAt;
  final bool syncToOtherDevices;
  final bool dndEnabled;
  final String? dndStartTime;
  final String? dndEndTime;

  AtMentionSettings({
    this.id,
    required this.userId,
    this.enabled = true,
    this.onlyAtAll = false,
    this.allowStrangerAt = true,
    this.syncToOtherDevices = true,
    this.dndEnabled = false,
    this.dndStartTime,
    this.dndEndTime,
  });

  factory AtMentionSettings.fromJson(Map<String, dynamic> json) {
    return AtMentionSettings(
      id: json['id'],
      userId: json['userId'] ?? 0,
      enabled: json['enabled'] ?? true,
      onlyAtAll: json['onlyAtAll'] ?? false,
      allowStrangerAt: json['allowStrangerAt'] ?? true,
      syncToOtherDevices: json['syncToOtherDevices'] ?? true,
      dndEnabled: json['dndEnabled'] ?? false,
      dndStartTime: json['dndStartTime'],
      dndEndTime: json['dndEndTime'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'enabled': enabled,
      'onlyAtAll': onlyAtAll,
      'allowStrangerAt': allowStrangerAt,
      'syncToOtherDevices': syncToOtherDevices,
      'dndEnabled': dndEnabled,
      'dndStartTime': dndStartTime,
      'dndEndTime': dndEndTime,
    };
  }

  AtMentionSettings copyWith({
    int? id,
    int? userId,
    bool? enabled,
    bool? onlyAtAll,
    bool? allowStrangerAt,
    bool? syncToOtherDevices,
    bool? dndEnabled,
    String? dndStartTime,
    String? dndEndTime,
  }) {
    return AtMentionSettings(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      enabled: enabled ?? this.enabled,
      onlyAtAll: onlyAtAll ?? this.onlyAtAll,
      allowStrangerAt: allowStrangerAt ?? this.allowStrangerAt,
      syncToOtherDevices: syncToOtherDevices ?? this.syncToOtherDevices,
      dndEnabled: dndEnabled ?? this.dndEnabled,
      dndStartTime: dndStartTime ?? this.dndStartTime,
      dndEndTime: dndEndTime ?? this.dndEndTime,
    );
  }
}
