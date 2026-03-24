class MessageSyncItem {
  final int messageId;
  final int conversationId;
  final int senderId;
  final String content;
  final String contentType;
  final DateTime sentAt;
  final bool deleted;
  final String syncAction;

  MessageSyncItem({
    required this.messageId,
    required this.conversationId,
    required this.senderId,
    required this.content,
    required this.contentType,
    required this.sentAt,
    required this.deleted,
    required this.syncAction,
  });

  factory MessageSyncItem.fromJson(Map<String, dynamic> json) {
    return MessageSyncItem(
      messageId: json['messageId'] as int,
      conversationId: json['conversationId'] as int,
      senderId: json['senderId'] as int,
      content: json['content'] as String? ?? '',
      contentType: json['contentType'] as String? ?? 'text',
      sentAt: DateTime.tryParse(json['sentAt'] ?? '') ?? DateTime.now(),
      deleted: json['deleted'] as bool? ?? false,
      syncAction: json['syncAction'] as String? ?? 'upsert',
    );
  }
}

class SyncRequest {
  final String deviceId;
  final int? conversationId;
  final int? lastMessageId;
  final String? syncToken;
  final int? limit;
  final DateTime? since;

  SyncRequest({
    required this.deviceId,
    this.conversationId,
    this.lastMessageId,
    this.syncToken,
    this.limit,
    this.since,
  });

  Map<String, dynamic> toJson() {
    return {
      'deviceId': deviceId,
      if (conversationId != null) 'conversationId': conversationId,
      if (lastMessageId != null) 'lastMessageId': lastMessageId,
      if (syncToken != null) 'syncToken': syncToken,
      if (limit != null) 'limit': limit,
      if (since != null) 'since': since!.toIso8601String(),
    };
  }
}

class SyncResponse {
  final List<MessageSyncItem> messages;
  final int? nextMessageId;
  final String? nextSyncToken;
  final DateTime syncTimestamp;
  final bool hasMore;
  final int totalSynced;

  SyncResponse({
    required this.messages,
    this.nextMessageId,
    this.nextSyncToken,
    required this.syncTimestamp,
    required this.hasMore,
    required this.totalSynced,
  });

  factory SyncResponse.fromJson(Map<String, dynamic> json) {
    return SyncResponse(
      messages: (json['messages'] as List?)
              ?.map((m) => MessageSyncItem.fromJson(m))
              .toList() ??
          [],
      nextMessageId: json['nextMessageId'] as int?,
      nextSyncToken: json['nextSyncToken'] as String?,
      syncTimestamp: DateTime.tryParse(json['syncTimestamp'] ?? '') ?? DateTime.now(),
      hasMore: json['hasMore'] as bool? ?? false,
      totalSynced: json['totalSynced'] as int? ?? 0,
    );
  }
}

class SyncCheckpoint {
  final int id;
  final int userId;
  final String deviceId;
  final int conversationId;
  final int lastMessageId;
  final DateTime lastSyncedAt;
  final String? syncToken;

  SyncCheckpoint({
    required this.id,
    required this.userId,
    required this.deviceId,
    required this.conversationId,
    required this.lastMessageId,
    required this.lastSyncedAt,
    this.syncToken,
  });

  factory SyncCheckpoint.fromJson(Map<String, dynamic> json) {
    return SyncCheckpoint(
      id: json['id'] as int,
      userId: json['userId'] as int,
      deviceId: json['deviceId'] as String,
      conversationId: json['conversationId'] as int,
      lastMessageId: json['lastMessageId'] as int,
      lastSyncedAt: DateTime.tryParse(json['lastSyncedAt'] ?? '') ?? DateTime.now(),
      syncToken: json['syncToken'] as String?,
    );
  }
}
