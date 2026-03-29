import 'package:json_annotation/json_annotation.dart';
import 'package:mobx/mobx.dart';

part 'scheduled_message_recall_model.g.dart';

/// 消息定时撤回模型
/// 支持设置消息在指定时间后自动撤回

enum ConversationType {
  @JsonValue('PRIVATE')
  private,
  @JsonValue('GROUP')
  group,
  @JsonValue('CHANNEL')
  channel,
}

enum RecallStatus {
  @JsonValue('PENDING')
  pending,
  @JsonValue('EXECUTED')
  executed,
  @JsonValue('CANCELLED')
  cancelled,
  @JsonValue('FAILED')
  failed,
  @JsonValue('EXPIRED')
  expired,
}

@JsonSerializable()
class ScheduledMessageRecallModel extends _ScheduledMessageRecallModelBase
    with _$ScheduledMessageRecallModel {
  ScheduledMessageRecallModel();

  factory ScheduledMessageRecallModel.fromJson(Map<String, dynamic> json) =>
      _$ScheduledMessageRecallModelFromJson(json);

  Map<String, dynamic> toJson() => _$ScheduledMessageRecallModelToJson(this);
}

abstract class _ScheduledMessageRecallModelBase with Store {
  @observable
  int? id;

  @observable
  int? userId;

  @observable
  int? messageId;

  @observable
  int? conversationId;

  @observable
  @JsonKey(unknownEnumValue: ConversationType.private)
  ConversationType? conversationType;

  @observable
  String? messageContent;

  @observable
  String? messageContentPreview;

  @observable
  @JsonKey(fromJson: _dateTimeFromJson, toJson: _dateTimeToJson)
  DateTime? scheduledRecallTime;

  @observable
  int? scheduledSeconds;

  @observable
  @JsonKey(unknownEnumValue: RecallStatus.pending)
  RecallStatus? status;

  @observable
  String? statusDisplay;

  @observable
  String? recallReason;

  @observable
  bool? notifyReceivers;

  @observable
  String? customNotifyMessage;

  @observable
  bool? isCancelable;

  @observable
  @JsonKey(fromJson: _dateTimeFromJson, toJson: _dateTimeToJson)
  DateTime? cancelDeadline;

  @observable
  @JsonKey(fromJson: _dateTimeFromJson, toJson: _dateTimeToJson)
  DateTime? executedAt;

  @observable
  @JsonKey(fromJson: _dateTimeFromJson, toJson: _dateTimeToJson)
  DateTime? createdAt;

  @observable
  @JsonKey(fromJson: _dateTimeFromJson, toJson: _dateTimeToJson)
  DateTime? updatedAt;

  // 扩展字段
  @observable
  int? remainingSeconds;

  @observable
  bool? canCancel;

  @observable
  String? senderName;

  @observable
  String? senderAvatar;

  @observable
  String? conversationName;

  /// 获取状态显示文本
  String get statusText {
    switch (status) {
      case RecallStatus.pending:
        return '待执行';
      case RecallStatus.executed:
        return '已撤回';
      case RecallStatus.cancelled:
        return '已取消';
      case RecallStatus.failed:
        return '执行失败';
      case RecallStatus.expired:
        return '已过期';
      default:
        return '未知';
    }
  }

  /// 获取状态颜色
  int get statusColor {
    switch (status) {
      case RecallStatus.pending:
        return 0xFFFF9800; // Orange
      case RecallStatus.executed:
        return 0xFF4CAF50; // Green
      case RecallStatus.cancelled:
        return 0xFF9E9E9E; // Grey
      case RecallStatus.failed:
        return 0xFFF44336; // Red
      case RecallStatus.expired:
        return 0xFF795548; // Brown
      default:
        return 0xFF000000;
    }
  }

  /// 检查是否可以取消
  bool get canBeCancelled {
    if (status != RecallStatus.pending) return false;
    if (isCancelable == false) return false;
    if (cancelDeadline != null && DateTime.now().isAfter(cancelDeadline!)) {
      return false;
    }
    if (scheduledRecallTime != null && DateTime.now().isAfter(scheduledRecallTime!)) {
      return false;
    }
    return true;
  }

  /// 计算剩余秒数
  int get remainingTimeSeconds {
    if (status != RecallStatus.pending || scheduledRecallTime == null) {
      return 0;
    }
    final now = DateTime.now();
    if (now.isAfter(scheduledRecallTime!)) return 0;
    return scheduledRecallTime!.difference(now).inSeconds;
  }

  /// 获取格式化的剩余时间
  String get formattedRemainingTime {
    final seconds = remainingTimeSeconds;
    if (seconds <= 0) return '即将撤回';
    if (seconds < 60) return '${seconds}秒后撤回';
    if (seconds < 3600) {
      final minutes = seconds ~/ 60;
      final remainingSecs = seconds % 60;
      return '${minutes}分${remainingSecs}秒后撤回';
    }
    final hours = seconds ~/ 3600;
    final remainingMins = (seconds % 3600) ~/ 60;
    return '${hours}小时${remainingMins}分后撤回';
  }

  /// 获取定时时间显示
  String get scheduledTimeDisplay {
    if (scheduledRecallTime == null) return '';
    final now = DateTime.now();
    final diff = scheduledRecallTime!.difference(now);
    
    if (diff.inDays > 0) {
      return '${diff.inDays}天后';
    } else if (diff.inHours > 0) {
      return '${diff.inHours}小时后';
    } else if (diff.inMinutes > 0) {
      return '${diff.inMinutes}分钟后';
    } else {
      return '${diff.inSeconds}秒后';
    }
  }

  /// 获取推荐的时间选项（秒）
  static List<int> get recommendedTimeOptions => [
    30,    // 30秒
    60,    // 1分钟
    120,   // 2分钟
    300,   // 5分钟
    600,   // 10分钟
    1800,  // 30分钟
    3600,  // 1小时
  ];

  /// 格式化时间选项
  static String formatTimeOption(int seconds) {
    if (seconds < 60) return '${seconds}秒';
    if (seconds < 3600) return '${seconds ~/ 60}分钟';
    return '${seconds ~/ 3600}小时';
  }

  /// 复制模型
  ScheduledMessageRecallModel copyWith({
    int? id,
    int? userId,
    int? messageId,
    int? conversationId,
    ConversationType? conversationType,
    String? messageContent,
    String? messageContentPreview,
    DateTime? scheduledRecallTime,
    int? scheduledSeconds,
    RecallStatus? status,
    String? statusDisplay,
    String? recallReason,
    bool? notifyReceivers,
    String? customNotifyMessage,
    bool? isCancelable,
    DateTime? cancelDeadline,
    DateTime? executedAt,
    DateTime? createdAt,
    DateTime? updatedAt,
    int? remainingSeconds,
    bool? canCancel,
    String? senderName,
    String? senderAvatar,
    String? conversationName,
  }) {
    final model = ScheduledMessageRecallModel()
      ..id = id ?? this.id
      ..userId = userId ?? this.userId
      ..messageId = messageId ?? this.messageId
      ..conversationId = conversationId ?? this.conversationId
      ..conversationType = conversationType ?? this.conversationType
      ..messageContent = messageContent ?? this.messageContent
      ..messageContentPreview = messageContentPreview ?? this.messageContentPreview
      ..scheduledRecallTime = scheduledRecallTime ?? this.scheduledRecallTime
      ..scheduledSeconds = scheduledSeconds ?? this.scheduledSeconds
      ..status = status ?? this.status
      ..statusDisplay = statusDisplay ?? this.statusDisplay
      ..recallReason = recallReason ?? this.recallReason
      ..notifyReceivers = notifyReceivers ?? this.notifyReceivers
      ..customNotifyMessage = customNotifyMessage ?? this.customNotifyMessage
      ..isCancelable = isCancelable ?? this.isCancelable
      ..cancelDeadline = cancelDeadline ?? this.cancelDeadline
      ..executedAt = executedAt ?? this.executedAt
      ..createdAt = createdAt ?? this.createdAt
      ..updatedAt = updatedAt ?? this.updatedAt
      ..remainingSeconds = remainingSeconds ?? this.remainingSeconds
      ..canCancel = canCancel ?? this.canCancel
      ..senderName = senderName ?? this.senderName
      ..senderAvatar = senderAvatar ?? this.senderAvatar
      ..conversationName = conversationName ?? this.conversationName;
    return model;
  }

  /// 从JSON列表转换
  static List<ScheduledMessageRecallModel> fromJsonList(List<dynamic> jsonList) {
    return jsonList
        .map((json) => ScheduledMessageRecallModel.fromJson(json as Map<String, dynamic>))
        .toList();
  }

  // JSON日期转换辅助方法
  static DateTime? _dateTimeFromJson(dynamic value) {
    if (value == null) return null;
    if (value is String) return DateTime.parse(value);
    return null;
  }

  static String? _dateTimeToJson(DateTime? dateTime) {
    return dateTime?.toIso8601String();
  }
}
