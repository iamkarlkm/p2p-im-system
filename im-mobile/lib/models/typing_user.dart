class TypingUser {
  final String conversationId;
  final String conversationType;
  final String userId;
  final String? userName;
  final DateTime? updatedAt;

  TypingUser({
    required this.conversationId,
    required this.conversationType,
    required this.userId,
    this.userName,
    this.updatedAt,
  });

  factory TypingUser.fromJson(Map<String, dynamic> json) {
    return TypingUser(
      conversationId: json['conversationId'] as String? ?? '',
      conversationType: json['conversationType'] as String? ?? '',
      userId: json['userId'] as String? ?? '',
      userName: json['userName'] as String?,
      updatedAt: json['updatedAt'] != null
          ? DateTime.tryParse(json['updatedAt'] as String)
          : null,
    );
  }
}
