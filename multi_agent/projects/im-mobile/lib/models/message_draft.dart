// Message Draft Model for Flutter
class MessageDraft {
  final int id;
  final int userId;
  final String conversationId;
  final String content;
  final String mentionIds;
  final String replyMessageId;
  final String messageType;
  final int updatedAt;

  MessageDraft({
    required this.id,
    required this.userId,
    required this.conversationId,
    required this.content,
    this.mentionIds = '',
    this.replyMessageId = '',
    this.messageType = 'text',
    required this.updatedAt,
  });

  factory MessageDraft.fromJson(Map<String, dynamic> json) {
    return MessageDraft(
      id: json['id'] ?? 0,
      userId: json['userId'] ?? 0,
      conversationId: json['conversationId'] ?? '',
      content: json['content'] ?? '',
      mentionIds: json['mentionIds'] ?? '',
      replyMessageId: json['replyMessageId'] ?? '',
      messageType: json['messageType'] ?? 'text',
      updatedAt: json['updatedAt'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'conversationId': conversationId,
      'content': content,
      'mentionIds': mentionIds,
      'replyMessageId': replyMessageId,
      'messageType': messageType,
      'updatedAt': updatedAt,
    };
  }
}
