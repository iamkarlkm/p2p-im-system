class GroupCall {
  final int callId;
  final int conversationId;
  final int initiatorId;
  final String callType;
  final String status;
  final int currentParticipants;
  final int maxParticipants;
  final DateTime startedAt;
  final DateTime? endedAt;

  GroupCall({
    required this.callId,
    required this.conversationId,
    required this.initiatorId,
    required this.callType,
    required this.status,
    required this.currentParticipants,
    required this.maxParticipants,
    required this.startedAt,
    this.endedAt,
  });

  factory GroupCall.fromJson(Map<String, dynamic> json) {
    return GroupCall(
      callId: json['callId'],
      conversationId: json['conversationId'],
      initiatorId: json['initiatorId'],
      callType: json['callType'],
      status: json['status'],
      currentParticipants: json['currentParticipants'],
      maxParticipants: json['maxParticipants'],
      startedAt: DateTime.parse(json['startedAt']),
      endedAt: json['endedAt'] != null ? DateTime.parse(json['endedAt']) : null,
    );
  }
}

class GroupCallParticipant {
  final int userId;
  final String status;
  final bool isMuted;
  final bool isVideoEnabled;
  final bool isScreenSharing;
  final DateTime joinedAt;
  final DateTime? leftAt;

  GroupCallParticipant({
    required this.userId,
    required this.status,
    required this.isMuted,
    required this.isVideoEnabled,
    required this.isScreenSharing,
    required this.joinedAt,
    this.leftAt,
  });

  factory GroupCallParticipant.fromJson(Map<String, dynamic> json) {
    return GroupCallParticipant(
      userId: json['userId'],
      status: json['status'],
      isMuted: json['isMuted'] ?? false,
      isVideoEnabled: json['isVideoEnabled'] ?? false,
      isScreenSharing: json['isScreenSharing'] ?? false,
      joinedAt: DateTime.parse(json['joinedAt']),
      leftAt: json['leftAt'] != null ? DateTime.parse(json['leftAt']) : null,
    );
  }
}
