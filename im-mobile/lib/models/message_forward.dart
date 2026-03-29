class MessageForward {
  final int id;
  final int originalMessageId;
  final int targetConversationId;
  final int forwardedBy;
  final DateTime forwardedAt;
  final String? comment;
  final ForwardType forwardType;

  MessageForward({
    required this.id,
    required this.originalMessageId,
    required this.targetConversationId,
    required this.forwardedBy,
    required this.forwardedAt,
    this.comment,
    required this.forwardType,
  });

  factory MessageForward.fromJson(Map<String, dynamic> json) {
    return MessageForward(
      id: json['id'],
      originalMessageId: json['originalMessageId'],
      targetConversationId: json['targetConversationId'],
      forwardedBy: json['forwardedBy'],
      forwardedAt: DateTime.parse(json['forwardedAt']),
      comment: json['comment'],
      forwardType: ForwardType.values.firstWhere(
        (e) => e.name == json['forwardType'],
        orElse: () => ForwardType.SINGLE,
      ),
    );
  }
}

enum ForwardType { SINGLE, MERGED }

class ForwardRequest {
  final List<int> messageIds;
  final int targetConversationId;
  final String? comment;
  final bool merged;
  final String? mergedTitle;

  ForwardRequest({
    required this.messageIds,
    required this.targetConversationId,
    this.comment,
    this.merged = false,
    this.mergedTitle,
  });

  Map<String, dynamic> toJson() => {
    'messageIds': messageIds,
    'targetConversationId': targetConversationId,
    if (comment != null) 'comment': comment,
    'merged': merged,
    if (mergedTitle != null) 'mergedTitle': mergedTitle,
  };
}

class ForwardResponse {
  final bool success;
  final String message;
  final List<int>? newMessageIds;
  final String? mergedForwardId;
  final DateTime forwardedAt;

  ForwardResponse({
    required this.success,
    required this.message,
    this.newMessageIds,
    this.mergedForwardId,
    required this.forwardedAt,
  });

  factory ForwardResponse.fromJson(Map<String, dynamic> json) {
    return ForwardResponse(
      success: json['success'],
      message: json['message'],
      newMessageIds: json['newMessageIds'] != null
          ? List<int>.from(json['newMessageIds'])
          : null,
      mergedForwardId: json['mergedForwardId'],
      forwardedAt: DateTime.parse(json['forwardedAt']),
    );
  }
}
