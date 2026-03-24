import 'dart:convert';

class Reaction {
  final String reactionId;
  final String messageId;
  final String userId;
  final String emoji;
  final String type;
  final DateTime createdAt;

  Reaction({
    required this.reactionId,
    required this.messageId,
    required this.userId,
    required this.emoji,
    required this.type,
    required this.createdAt,
  });

  factory Reaction.fromJson(Map<String, dynamic> json) {
    return Reaction(
      reactionId: json['reactionId'] ?? '',
      messageId: json['messageId'] ?? '',
      userId: json['userId'] ?? '',
      emoji: json['emoji'] ?? '',
      type: json['type'] ?? 'EMOJI',
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'])
          : DateTime.now(),
    );
  }

  Map<String, dynamic> toJson() => {
    'reactionId': reactionId,
    'messageId': messageId,
    'userId': userId,
    'emoji': emoji,
    'type': type,
    'createdAt': createdAt.toIso8601String(),
  };
}

class ReactionWithUsers {
  final String emoji;
  final int count;
  final List<String> userIds;

  ReactionWithUsers({required this.emoji, required this.count, required this.userIds});

  factory ReactionWithUsers.fromJson(Map<String, dynamic> json) {
    return ReactionWithUsers(
      emoji: json['emoji'] ?? '',
      count: json['count'] ?? 0,
      userIds: List<String>.from(json['userIds'] ?? []),
    );
  }
}

class ReactionStats {
  final String messageId;
  final Map<String, int> counts;

  ReactionStats({required this.messageId, required this.counts});

  factory ReactionStats.fromJson(Map<String, dynamic> json) {
    final countsRaw = json['counts'] as Map<String, dynamic>? ?? {};
    return ReactionStats(
      messageId: json['messageId'] ?? '',
      counts: countsRaw.map((k, v) => MapEntry(k, (v as num).toInt())),
    );
  }
}
