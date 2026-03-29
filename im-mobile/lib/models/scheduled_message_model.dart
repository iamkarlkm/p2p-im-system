import 'package:json_annotation/json_annotation.dart';

part 'scheduled_message_model.g.dart';

enum ScheduledMessageStatus {
  PENDING,
  SENT,
  CANCELLED,
  FAILED,
}

@JsonSerializable()
class ScheduledMessageModel {
  final int id;
  final int senderId;
  final int receiverId;
  final String content;
  final ScheduledMessageStatus status;
  final DateTime scheduledTime;
  final DateTime? sentTime;
  final String? failureReason;
  final DateTime createdAt;
  final String? receiverNickname;
  final String? receiverAvatar;

  ScheduledMessageModel({
    required this.id,
    required this.senderId,
    required this.receiverId,
    required this.content,
    required this.status,
    required this.scheduledTime,
    this.sentTime,
    this.failureReason,
    required this.createdAt,
    this.receiverNickname,
    this.receiverAvatar,
  });

  factory ScheduledMessageModel.fromJson(Map<String, dynamic> json) =>
      _$ScheduledMessageModelFromJson(json);

  Map<String, dynamic> toJson() => _$ScheduledMessageModelToJson(this);

  String get statusLabel {
    switch (status) {
      case ScheduledMessageStatus.PENDING:
        return '待发送';
      case ScheduledMessageStatus.SENT:
        return '已发送';
      case ScheduledMessageStatus.CANCELLED:
        return '已取消';
      case ScheduledMessageStatus.FAILED:
        return '发送失败';
    }
  }

  bool get canEdit => status == ScheduledMessageStatus.PENDING;
  bool get canCancel => status == ScheduledMessageStatus.PENDING;
}
