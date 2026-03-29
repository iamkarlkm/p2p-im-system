// Message Reminder Model for IM Mobile
class MessageReminderModel {
  final int id;
  final int userId;
  final int messageId;
  final int conversationId;
  final DateTime reminderTime;
  final String? note;
  final bool isTriggered;
  final bool isDismissed;
  final DateTime createdAt;
  final String? repeatType;
  final int? remindBeforeMinutes;
  final String? messagePreview;
  final String? conversationName;

  MessageReminderModel({
    required this.id,
    required this.userId,
    required this.messageId,
    required this.conversationId,
    required this.reminderTime,
    this.note,
    required this.isTriggered,
    required this.isDismissed,
    required this.createdAt,
    this.repeatType,
    this.remindBeforeMinutes,
    this.messagePreview,
    this.conversationName,
  });

  factory MessageReminderModel.fromJson(Map<String, dynamic> json) {
    return MessageReminderModel(
      id: json['id'] ?? 0,
      userId: json['userId'] ?? 0,
      messageId: json['messageId'] ?? 0,
      conversationId: json['conversationId'] ?? 0,
      reminderTime: DateTime.parse(json['reminderTime']),
      note: json['note'],
      isTriggered: json['isTriggered'] ?? false,
      isDismissed: json['isDismissed'] ?? false,
      createdAt: DateTime.parse(json['createdAt']),
      repeatType: json['repeatType'],
      remindBeforeMinutes: json['remindBeforeMinutes'],
      messagePreview: json['messagePreview'],
      conversationName: json['conversationName'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'messageId': messageId,
      'conversationId': conversationId,
      'reminderTime': reminderTime.toIso8601String(),
      'note': note,
      'isTriggered': isTriggered,
      'isDismissed': isDismissed,
      'createdAt': createdAt.toIso8601String(),
      'repeatType': repeatType,
      'remindBeforeMinutes': remindBeforeMinutes,
      'messagePreview': messagePreview,
      'conversationName': conversationName,
    };
  }
}

class ReminderRequest {
  final int messageId;
  final int conversationId;
  final DateTime reminderTime;
  final String? note;
  final String? repeatType;
  final int? remindBeforeMinutes;

  ReminderRequest({
    required this.messageId,
    required this.conversationId,
    required this.reminderTime,
    this.note,
    this.repeatType,
    this.remindBeforeMinutes,
  });

  Map<String, dynamic> toJson() {
    return {
      'messageId': messageId,
      'conversationId': conversationId,
      'reminderTime': reminderTime.toIso8601String(),
      if (note != null) 'note': note,
      if (repeatType != null) 'repeatType': repeatType,
      if (remindBeforeMinutes != null) 'remindBeforeMinutes': remindBeforeMinutes,
    };
  }
}
