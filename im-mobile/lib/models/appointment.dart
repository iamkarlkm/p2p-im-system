import 'package:json_annotation/json_annotation.dart';

part 'appointment.g.dart';

/// 预约模型类
/// 本地生活服务预约与排班管理系统 - 移动端数据模型
/// 
/// @author IM Development Team
/// @since 2026-03-28
@JsonSerializable()
class Appointment {
  /// 预约ID
  final int? id;

  /// 预约编号
  final String? appointmentNo;

  /// 用户ID
  final int? userId;

  /// 商户ID
  final int merchantId;

  /// 服务类型ID
  final int serviceTypeId;

  /// 服务类型名称
  final String? serviceTypeName;

  /// 预约日期
  final DateTime appointmentDate;

  /// 预约开始时间
  final String startTime;

  /// 预约结束时间
  final String? endTime;

  /// 预约状态: PENDING-待确认, CONFIRMED-已确认, IN_SERVICE-服务中, COMPLETED-已完成, CANCELLED-已取消
  final String? status;

  /// 服务人员ID
  final int? staffId;

  /// 服务人员名称
  final String? staffName;

  /// 服务资源ID
  final int? resourceId;

  /// 服务资源名称
  final String? resourceName;

  /// 预约人数
  final int peopleCount;

  /// 客户姓名
  final String customerName;

  /// 客户电话
  final String customerPhone;

  /// 客户备注
  final String? customerRemark;

  /// 预估服务时长(分钟)
  final int? estimatedDuration;

  /// 预估价格
  final double? estimatedPrice;

  /// 实际价格
  final double? actualPrice;

  /// 是否会员预约
  final bool? isMember;

  /// 会员等级
  final String? memberLevel;

  /// 预约来源
  final String? source;

  /// 创建时间
  final DateTime? createTime;

  /// 确认时间
  final DateTime? confirmTime;

  /// 服务开始时间
  final DateTime? serviceStartTime;

  /// 服务完成时间
  final DateTime? serviceEndTime;

  /// 取消原因
  final String? cancelReason;

  /// 是否使用优惠券
  final bool? useCoupon;

  /// 优惠券ID
  final int? couponId;

  /// 提醒状态
  final String? remindStatus;

  Appointment({
    this.id,
    this.appointmentNo,
    this.userId,
    required this.merchantId,
    required this.serviceTypeId,
    this.serviceTypeName,
    required this.appointmentDate,
    required this.startTime,
    this.endTime,
    this.status,
    this.staffId,
    this.staffName,
    this.resourceId,
    this.resourceName,
    required this.peopleCount,
    required this.customerName,
    required this.customerPhone,
    this.customerRemark,
    this.estimatedDuration,
    this.estimatedPrice,
    this.actualPrice,
    this.isMember,
    this.memberLevel,
    this.source,
    this.createTime,
    this.confirmTime,
    this.serviceStartTime,
    this.serviceEndTime,
    this.cancelReason,
    this.useCoupon,
    this.couponId,
    this.remindStatus,
  });

  factory Appointment.fromJson(Map<String, dynamic> json) => 
      _$AppointmentFromJson(json);

  Map<String, dynamic> toJson() => _$AppointmentToJson(this);

  /// 获取状态显示文本
  String get statusText {
    switch (status) {
      case 'PENDING':
        return '待确认';
      case 'CONFIRMED':
        return '已确认';
      case 'IN_SERVICE':
        return '服务中';
      case 'COMPLETED':
        return '已完成';
      case 'CANCELLED':
        return '已取消';
      case 'NO_SHOW':
        return '爽约';
      default:
        return '未知';
    }
  }

  /// 获取状态颜色
  String get statusColor {
    switch (status) {
      case 'PENDING':
        return '#FF9500';
      case 'CONFIRMED':
        return '#34C759';
      case 'IN_SERVICE':
        return '#007AFF';
      case 'COMPLETED':
        return '#8E8E93';
      case 'CANCELLED':
      case 'NO_SHOW':
        return '#FF3B30';
      default:
        return '#8E8E93';
    }
  }

  /// 是否可以取消
  bool get canCancel {
    return status == 'PENDING' || status == 'CONFIRMED';
  }

  /// 是否可以修改
  bool get canModify {
    return status == 'PENDING';
  }

  /// 获取预约时间显示
  String get appointmentTimeText {
    if (endTime != null) {
      return '$startTime - $endTime';
    }
    return startTime;
  }

  /// 格式化日期显示
  String get formattedDate {
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final appointmentDay = DateTime(
      appointmentDate.year,
      appointmentDate.month,
      appointmentDate.day,
    );
    
    final difference = appointmentDay.difference(today).inDays;
    
    if (difference == 0) {
      return '今天';
    } else if (difference == 1) {
      return '明天';
    } else if (difference == 2) {
      return '后天';
    } else {
      return '${appointmentDate.month}月${appointmentDate.day}日';
    }
  }

  /// 是否即将过期（30分钟内）
  bool get isExpiringSoon {
    if (status != 'CONFIRMED') return false;
    
    final now = DateTime.now();
    final appointmentDateTime = DateTime(
      appointmentDate.year,
      appointmentDate.month,
      appointmentDate.day,
      int.parse(startTime.split(':')[0]),
      int.parse(startTime.split(':')[1]),
    );
    
    final difference = appointmentDateTime.difference(now).inMinutes;
    return difference > 0 && difference <= 30;
  }

  /// 复制并修改
  Appointment copyWith({
    int? id,
    String? appointmentNo,
    int? userId,
    int? merchantId,
    int? serviceTypeId,
    String? serviceTypeName,
    DateTime? appointmentDate,
    String? startTime,
    String? endTime,
    String? status,
    int? staffId,
    String? staffName,
    int? resourceId,
    String? resourceName,
    int? peopleCount,
    String? customerName,
    String? customerPhone,
    String? customerRemark,
    int? estimatedDuration,
    double? estimatedPrice,
    double? actualPrice,
    bool? isMember,
    String? memberLevel,
    String? source,
    DateTime? createTime,
    DateTime? confirmTime,
    DateTime? serviceStartTime,
    DateTime? serviceEndTime,
    String? cancelReason,
    bool? useCoupon,
    int? couponId,
    String? remindStatus,
  }) {
    return Appointment(
      id: id ?? this.id,
      appointmentNo: appointmentNo ?? this.appointmentNo,
      userId: userId ?? this.userId,
      merchantId: merchantId ?? this.merchantId,
      serviceTypeId: serviceTypeId ?? this.serviceTypeId,
      serviceTypeName: serviceTypeName ?? this.serviceTypeName,
      appointmentDate: appointmentDate ?? this.appointmentDate,
      startTime: startTime ?? this.startTime,
      endTime: endTime ?? this.endTime,
      status: status ?? this.status,
      staffId: staffId ?? this.staffId,
      staffName: staffName ?? this.staffName,
      resourceId: resourceId ?? this.resourceId,
      resourceName: resourceName ?? this.resourceName,
      peopleCount: peopleCount ?? this.peopleCount,
      customerName: customerName ?? this.customerName,
      customerPhone: customerPhone ?? this.customerPhone,
      customerRemark: customerRemark ?? this.customerRemark,
      estimatedDuration: estimatedDuration ?? this.estimatedDuration,
      estimatedPrice: estimatedPrice ?? this.estimatedPrice,
      actualPrice: actualPrice ?? this.actualPrice,
      isMember: isMember ?? this.isMember,
      memberLevel: memberLevel ?? this.memberLevel,
      source: source ?? this.source,
      createTime: createTime ?? this.createTime,
      confirmTime: confirmTime ?? this.confirmTime,
      serviceStartTime: serviceStartTime ?? this.serviceStartTime,
      serviceEndTime: serviceEndTime ?? this.serviceEndTime,
      cancelReason: cancelReason ?? this.cancelReason,
      useCoupon: useCoupon ?? this.useCoupon,
      couponId: couponId ?? this.couponId,
      remindStatus: remindStatus ?? this.remindStatus,
    );
  }
}

/// 预约时段模型
@JsonSerializable()
class AppointmentSlot {
  final String startTime;
  final String endTime;
  final bool available;
  final int maxCapacity;
  final int bookedCount;
  final int availableCount;

  AppointmentSlot({
    required this.startTime,
    required this.endTime,
    required this.available,
    required this.maxCapacity,
    required this.bookedCount,
    required this.availableCount,
  });

  factory AppointmentSlot.fromJson(Map<String, dynamic> json) =>
      _$AppointmentSlotFromJson(json);

  Map<String, dynamic> toJson() => _$AppointmentSlotToJson(this);

  String get timeRange => '$startTime-$endTime';
}
