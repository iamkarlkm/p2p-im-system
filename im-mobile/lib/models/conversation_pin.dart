class ConversationPin {
  final int conversationId;
  final String conversationName;
  final int sortOrder;
  final DateTime pinnedAt;
  final String? pinNote;

  ConversationPin({
    required this.conversationId,
    required this.conversationName,
    required this.sortOrder,
    required this.pinnedAt,
    this.pinNote,
  });

  factory ConversationPin.fromJson(Map<String, dynamic> json) {
    return ConversationPin(
      conversationId: json['conversationId'],
      conversationName: json['conversationName'],
      sortOrder: json['sortOrder'],
      pinnedAt: DateTime.parse(json['pinnedAt']),
      pinNote: json['pinNote'],
    );
  }
}

class PinConversationRequest {
  final int conversationId;
  final int? sortOrder;
  final String? pinNote;

  PinConversationRequest({
    required this.conversationId,
    this.sortOrder,
    this.pinNote,
  });

  Map<String, dynamic> toJson() => {
    'conversationId': conversationId,
    if (sortOrder != null) 'sortOrder': sortOrder,
    if (pinNote != null) 'pinNote': pinNote,
  };
}

class PinConversationResponse {
  final bool success;
  final String message;
  final List<ConversationPin>? pinnedConversations;

  PinConversationResponse({
    required this.success,
    required this.message,
    this.pinnedConversations,
  });

  factory PinConversationResponse.fromJson(Map<String, dynamic> json) {
    return PinConversationResponse(
      success: json['success'],
      message: json['message'],
      pinnedConversations: json['pinnedConversations'] != null
          ? (json['pinnedConversations'] as List)
              .map((e) => ConversationPin.fromJson(e))
              .toList()
          : null,
    );
  }
}
