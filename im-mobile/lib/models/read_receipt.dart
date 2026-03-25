// Read Receipt Model for Flutter
class ReadReceipt {
  final int userId;
  final String conversationId;
  final String messageId;
  final int readAt;
  final List<int>? readByUsers;

  ReadReceipt({
    required this.userId,
    required this.conversationId,
    required this.messageId,
    required this.readAt,
    this.readByUsers,
  });

  factory ReadReceipt.fromJson(Map<String, dynamic> json) {
    return ReadReceipt(
      userId: json['userId'] ?? 0,
      conversationId: json['conversationId'] ?? '',
      messageId: json['messageId'] ?? '',
      readAt: json['readAt'] ?? 0,
      readByUsers: json['readByUsers'] != null
          ? List<int>.from(json['readByUsers'])
          : null,
    );
  }
}
