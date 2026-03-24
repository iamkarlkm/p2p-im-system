class BatchOperationRequest {
  final List<int> conversationIds;
  final String operationType;
  final bool? notifyParticipants;
  final String? reason;

  BatchOperationRequest({
    required this.conversationIds,
    required this.operationType,
    this.notifyParticipants,
    this.reason,
  });

  Map<String, dynamic> toJson() {
    return {
      'conversationIds': conversationIds,
      'operationType': operationType,
      if (notifyParticipants != null) 'notifyParticipants': notifyParticipants,
      if (reason != null) 'reason': reason,
    };
  }
}

class BatchOperationResponse {
  final int operationId;
  final String operationType;
  final int totalCount;
  final int successCount;
  final int failureCount;
  final String status;
  final List<int> failedConversationIds;
  final String? message;
  final DateTime createdAt;
  final DateTime? completedAt;

  BatchOperationResponse({
    required this.operationId,
    required this.operationType,
    required this.totalCount,
    required this.successCount,
    required this.failureCount,
    required this.status,
    required this.failedConversationIds,
    this.message,
    required this.createdAt,
    this.completedAt,
  });

  factory BatchOperationResponse.fromJson(Map<String, dynamic> json) {
    return BatchOperationResponse(
      operationId: json['operationId'],
      operationType: json['operationType'],
      totalCount: json['totalCount'],
      successCount: json['successCount'],
      failureCount: json['failureCount'],
      status: json['status'],
      failedConversationIds: List<int>.from(json['failedConversationIds'] ?? []),
      message: json['message'],
      createdAt: DateTime.parse(json['createdAt']),
      completedAt: json['completedAt'] != null
          ? DateTime.parse(json['completedAt'])
          : null,
    );
  }

  bool get isSuccess => failureCount == 0;
  bool get isPartial => successCount > 0 && failureCount > 0;
  bool get isFailed => successCount == 0 && failureCount > 0;
}

class BatchOperationHistory {
  final int operationId;
  final String operationType;
  final int totalCount;
  final int successCount;
  final int failureCount;
  final String status;
  final DateTime createdAt;
  final DateTime? completedAt;

  BatchOperationHistory({
    required this.operationId,
    required this.operationType,
    required this.totalCount,
    required this.successCount,
    required this.failureCount,
    required this.status,
    required this.createdAt,
    this.completedAt,
  });

  factory BatchOperationHistory.fromJson(Map<String, dynamic> json) {
    return BatchOperationHistory(
      operationId: json['operationId'],
      operationType: json['operationType'],
      totalCount: json['totalCount'],
      successCount: json['successCount'],
      failureCount: json['failureCount'],
      status: json['status'],
      createdAt: DateTime.parse(json['createdAt']),
      completedAt: json['completedAt'] != null
          ? DateTime.parse(json['completedAt'])
          : null,
    );
  }
}

enum BatchOperationType {
  markRead('mark_read', 'Mark as Read'),
  archive('archive', 'Archive'),
  delete('delete', 'Delete'),
  pin('pin', 'Pin'),
  unpin('unpin', 'Unpin'),
  mute('mute', 'Mute'),
  unmute('unmute', 'Unmute');

  final String value;
  final String label;

  const BatchOperationType(this.value, this.label);

  static BatchOperationType fromValue(String value) {
    return BatchOperationType.values.firstWhere(
      (e) => e.value == value,
      orElse: () => BatchOperationType.markRead,
    );
  }
}

class ConversationSelection {
  final int conversationId;
  final String name;
  final String? lastMessage;
  final DateTime? lastMessageTime;
  final int unreadCount;
  bool isSelected;

  ConversationSelection({
    required this.conversationId,
    required this.name,
    this.lastMessage,
    this.lastMessageTime,
    this.unreadCount = 0,
    this.isSelected = false,
  });

  factory ConversationSelection.fromJson(Map<String, dynamic> json) {
    return ConversationSelection(
      conversationId: json['conversationId'],
      name: json['name'],
      lastMessage: json['lastMessage'],
      lastMessageTime: json['lastMessageTime'] != null
          ? DateTime.parse(json['lastMessageTime'])
          : null,
      unreadCount: json['unreadCount'] ?? 0,
    );
  }
}
