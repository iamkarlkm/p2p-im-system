import 'package:json_annotation/json_annotation.dart';

part 'queue_ticket.g.dart';

/// 排队叫号模型类
/// 本地生活服务排队叫号系统 - 移动端数据模型
/// 
/// @author IM Development Team
/// @since 2026-03-28
@JsonSerializable()
class QueueTicket {
  /// 排队号ID
  final int? id;

  /// 排队号
  final String? ticketNo;

  /// 用户ID
  final int? userId;

  /// 商户ID
  final int merchantId;

  /// 服务类型ID
  final int serviceTypeId;

  /// 服务类型名称
  final String? serviceTypeName;

  /// 队列ID
  final String? queueId;

  /// 队列名称
  final String? queueName;

  /// 排队状态: WAITING-等待中, CALLING-叫号中, SERVING-服务中, COMPLETED-已完成, MISSED-已过号, CANCELLED-已取消
  final String? status;

  /// 当前排队序号
  final int? queueNumber;

  /// 前方等待人数
  final int? peopleAhead;

  /// 预估等待时间(分钟)
  final int? estimatedWaitMinutes;

  /// 实际等待时间(分钟)
  final int? actualWaitMinutes;

  /// 取号时间
  final DateTime? takeTime;

  /// 叫号时间
  final DateTime? callTime;

  /// 开始服务时间
  final DateTime? serveStartTime;

  /// 完成服务时间
  final DateTime? serveEndTime;

  /// 过号时间
  final DateTime? missTime;

  /// 取消时间
  final DateTime? cancelTime;

  /// 预计可到达时间
  final DateTime? estimatedArrivalTime;

  /// 客户姓名
  final String? customerName;

  /// 客户电话
  final String? customerPhone;

  /// 备注
  final String? remark;

  /// 人数
  final int? peopleCount;

  /// 取号方式
  final String? source;

  /// 优先级
  final String? priority;

  /// 服务窗口名称
  final String? serviceWindowName;

  /// 是否提醒过
  final bool? reminded;

  /// 关联预约ID
  final int? appointmentId;

  /// 是否预约用户
  final bool? hasAppointment;

  /// 创建时间
  final DateTime? createTime;

  QueueTicket({
    this.id,
    this.ticketNo,
    this.userId,
    required this.merchantId,
    required this.serviceTypeId,
    this.serviceTypeName,
    this.queueId,
    this.queueName,
    this.status,
    this.queueNumber,
    this.peopleAhead,
    this.estimatedWaitMinutes,
    this.actualWaitMinutes,
    this.takeTime,
    this.callTime,
    this.serveStartTime,
    this.serveEndTime,
    this.missTime,
    this.cancelTime,
    this.estimatedArrivalTime,
    this.customerName,
    this.customerPhone,
    this.remark,
    this.peopleCount,
    this.source,
    this.priority,
    this.serviceWindowName,
    this.reminded,
    this.appointmentId,
    this.hasAppointment,
    this.createTime,
  });

  factory QueueTicket.fromJson(Map<String, dynamic> json) =>
      _$QueueTicketFromJson(json);

  Map<String, dynamic> toJson() => _$QueueTicketToJson(this);

  /// 获取状态显示文本
  String get statusText {
    switch (status) {
      case 'WAITING':
        return '等待中';
      case 'CALLING':
        return '叫号中';
      case 'SERVING':
        return '服务中';
      case 'COMPLETED':
        return '已完成';
      case 'MISSED':
        return '已过号';
      case 'CANCELLED':
        return '已取消';
      default:
        return '未知';
    }
  }

  /// 获取状态颜色
  String get statusColor {
    switch (status) {
      case 'WAITING':
        return '#FF9500';
      case 'CALLING':
        return '#007AFF';
      case 'SERVING':
        return '#34C759';
      case 'COMPLETED':
        return '#8E8E93';
      case 'MISSED':
      case 'CANCELLED':
        return '#FF3B30';
      default:
        return '#8E8E93';
    }
  }

  /// 是否正在等待
  bool get isWaiting => status == 'WAITING';

  /// 是否正在叫号
  bool get isCalling => status == 'CALLING';

  /// 是否可以取消
  bool get canCancel => status == 'WAITING';

  /// 是否可以重排
  bool get canRequeue => status == 'MISSED';

  /// 获取预估等待时间显示
  String get estimatedWaitText {
    if (estimatedWaitMinutes == null || estimatedWaitMinutes == 0) {
      return '即将到达';
    }
    if (estimatedWaitMinutes! < 60) {
      return '约${estimatedWaitMinutes}分钟';
    }
    final hours = estimatedWaitMinutes! ~/ 60;
    final minutes = estimatedWaitMinutes! % 60;
    if (minutes == 0) {
      return '约${hours}小时';
    }
    return '约${hours}小时${minutes}分钟';
  }

  /// 获取前方等待人数显示
  String get peopleAheadText {
    if (peopleAhead == null || peopleAhead == 0) {
      return '马上到您';
    }
    return '前方${peopleAhead}人';
  }

  /// 获取排队号显示
  String get ticketNoDisplay {
    return ticketNo ?? '---';
  }

  /// 获取优先级显示
  String get priorityText {
    switch (priority) {
      case 'VIP':
        return 'VIP';
      case 'MEMBER':
        return '会员';
      case 'EMERGENCY':
        return '紧急';
      default:
        return '普通';
    }
  }

  /// 是否即将叫到（前方<=3人）
  bool get isApproaching => (peopleAhead ?? 999) <= 3;

  /// 复制并修改
  QueueTicket copyWith({
    int? id,
    String? ticketNo,
    int? userId,
    int? merchantId,
    int? serviceTypeId,
    String? serviceTypeName,
    String? queueId,
    String? queueName,
    String? status,
    int? queueNumber,
    int? peopleAhead,
    int? estimatedWaitMinutes,
    int? actualWaitMinutes,
    DateTime? takeTime,
    DateTime? callTime,
    DateTime? serveStartTime,
    DateTime? serveEndTime,
    DateTime? missTime,
    DateTime? cancelTime,
    DateTime? estimatedArrivalTime,
    String? customerName,
    String? customerPhone,
    String? remark,
    int? peopleCount,
    String? source,
    String? priority,
    String? serviceWindowName,
    bool? reminded,
    int? appointmentId,
    bool? hasAppointment,
    DateTime? createTime,
  }) {
    return QueueTicket(
      id: id ?? this.id,
      ticketNo: ticketNo ?? this.ticketNo,
      userId: userId ?? this.userId,
      merchantId: merchantId ?? this.merchantId,
      serviceTypeId: serviceTypeId ?? this.serviceTypeId,
      serviceTypeName: serviceTypeName ?? this.serviceTypeName,
      queueId: queueId ?? this.queueId,
      queueName: queueName ?? this.queueName,
      status: status ?? this.status,
      queueNumber: queueNumber ?? this.queueNumber,
      peopleAhead: peopleAhead ?? this.peopleAhead,
      estimatedWaitMinutes: estimatedWaitMinutes ?? this.estimatedWaitMinutes,
      actualWaitMinutes: actualWaitMinutes ?? this.actualWaitMinutes,
      takeTime: takeTime ?? this.takeTime,
      callTime: callTime ?? this.callTime,
      serveStartTime: serveStartTime ?? this.serveStartTime,
      serveEndTime: serveEndTime ?? this.serveEndTime,
      missTime: missTime ?? this.missTime,
      cancelTime: cancelTime ?? this.cancelTime,
      estimatedArrivalTime: estimatedArrivalTime ?? this.estimatedArrivalTime,
      customerName: customerName ?? this.customerName,
      customerPhone: customerPhone ?? this.customerPhone,
      remark: remark ?? this.remark,
      peopleCount: peopleCount ?? this.peopleCount,
      source: source ?? this.source,
      priority: priority ?? this.priority,
      serviceWindowName: serviceWindowName ?? this.serviceWindowName,
      reminded: reminded ?? this.reminded,
      appointmentId: appointmentId ?? this.appointmentId,
      hasAppointment: hasAppointment ?? this.hasAppointment,
      createTime: createTime ?? this.createTime,
    );
  }
}

/// 队列信息模型
@JsonSerializable()
class QueueInfo {
  final String queueId;
  final String queueName;
  final int waitingCount;
  final int avgWaitMinutes;
  final bool isOpen;

  QueueInfo({
    required this.queueId,
    required this.queueName,
    required this.waitingCount,
    required this.avgWaitMinutes,
    required this.isOpen,
  });

  factory QueueInfo.fromJson(Map<String, dynamic> json) =>
      _$QueueInfoFromJson(json);

  Map<String, dynamic> toJson() => _$QueueInfoToJson(this);

  String get waitTimeText {
    if (avgWaitMinutes < 60) {
      return '约${avgWaitMinutes}分钟';
    }
    final hours = avgWaitMinutes ~/ 60;
    final minutes = avgWaitMinutes % 60;
    return '约${hours}小时${minutes}分钟';
  }
}
