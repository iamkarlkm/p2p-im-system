import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

/// 预约模型类
class AppointmentModel {
  final int id;
  final int userId;
  final int merchantId;
  final String merchantName;
  final String? merchantLogo;
  final int serviceId;
  final String serviceName;
  final DateTime appointmentDate;
  final String startTime;
  final String endTime;
  final String contactName;
  final String contactPhone;
  final String? remark;
  final int numberOfPeople;
  final AppointmentStatus status;
  final String? cancelReason;
  final DateTime? cancelTime;
  final DateTime? checkInTime;
  final DateTime? completeTime;
  final int? staffId;
  final String? staffName;
  final int? seatId;
  final String? seatName;
  final double? price;
  final double? deposit;
  final bool depositPaid;
  final bool reminderSent;
  final DateTime? overTime;
  final String? source;
  final DateTime createdAt;

  AppointmentModel({
    required this.id,
    required this.userId,
    required this.merchantId,
    required this.merchantName,
    this.merchantLogo,
    required this.serviceId,
    required this.serviceName,
    required this.appointmentDate,
    required this.startTime,
    required this.endTime,
    required this.contactName,
    required this.contactPhone,
    this.remark,
    this.numberOfPeople = 1,
    required this.status,
    this.cancelReason,
    this.cancelTime,
    this.checkInTime,
    this.completeTime,
    this.staffId,
    this.staffName,
    this.seatId,
    this.seatName,
    this.price,
    this.deposit,
    this.depositPaid = false,
    this.reminderSent = false,
    this.overTime,
    this.source,
    required this.createdAt,
  });

  factory AppointmentModel.fromJson(Map<String, dynamic> json) {
    return AppointmentModel(
      id: json['id'],
      userId: json['userId'],
      merchantId: json['merchantId'],
      merchantName: json['merchantName'],
      merchantLogo: json['merchantLogo'],
      serviceId: json['serviceId'],
      serviceName: json['serviceName'],
      appointmentDate: DateTime.parse(json['appointmentDate']),
      startTime: json['startTime'],
      endTime: json['endTime'],
      contactName: json['contactName'],
      contactPhone: json['contactPhone'],
      remark: json['remark'],
      numberOfPeople: json['numberOfPeople'] ?? 1,
      status: AppointmentStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => AppointmentStatus.pending,
      ),
      cancelReason: json['cancelReason'],
      cancelTime: json['cancelTime'] != null
          ? DateTime.parse(json['cancelTime'])
          : null,
      checkInTime: json['checkInTime'] != null
          ? DateTime.parse(json['checkInTime'])
          : null,
      completeTime: json['completeTime'] != null
          ? DateTime.parse(json['completeTime'])
          : null,
      staffId: json['staffId'],
      staffName: json['staffName'],
      seatId: json['seatId'],
      seatName: json['seatName'],
      price: json['price']?.toDouble(),
      deposit: json['deposit']?.toDouble(),
      depositPaid: json['depositPaid'] ?? false,
      reminderSent: json['reminderSent'] ?? false,
      overTime: json['overTime'] != null
          ? DateTime.parse(json['overTime'])
          : null,
      source: json['source'],
      createdAt: DateTime.parse(json['createdAt']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'merchantId': merchantId,
      'merchantName': merchantName,
      'merchantLogo': merchantLogo,
      'serviceId': serviceId,
      'serviceName': serviceName,
      'appointmentDate': appointmentDate.toIso8601String(),
      'startTime': startTime,
      'endTime': endTime,
      'contactName': contactName,
      'contactPhone': contactPhone,
      'remark': remark,
      'numberOfPeople': numberOfPeople,
      'status': status.name,
      'cancelReason': cancelReason,
      'cancelTime': cancelTime?.toIso8601String(),
      'checkInTime': checkInTime?.toIso8601String(),
      'completeTime': completeTime?.toIso8601String(),
      'staffId': staffId,
      'staffName': staffName,
      'seatId': seatId,
      'seatName': seatName,
      'price': price,
      'deposit': deposit,
      'depositPaid': depositPaid,
      'reminderSent': reminderSent,
      'overTime': overTime?.toIso8601String(),
      'source': source,
      'createdAt': createdAt.toIso8601String(),
    };
  }

  /// 获取状态显示文本
  String get statusText {
    switch (status) {
      case AppointmentStatus.pending:
        return '待确认';
      case AppointmentStatus.confirmed:
        return '已确认';
      case AppointmentStatus.checkedIn:
        return '已到店';
      case AppointmentStatus.inService:
        return '服务中';
      case AppointmentStatus.completed:
        return '已完成';
      case AppointmentStatus.cancelled:
        return '已取消';
      case AppointmentStatus.noShow:
        return '爽约';
      case AppointmentStatus.expired:
        return '已过期';
    }
  }

  /// 获取状态颜色
  Color get statusColor {
    switch (status) {
      case AppointmentStatus.pending:
        return Colors.orange;
      case AppointmentStatus.confirmed:
        return Colors.blue;
      case AppointmentStatus.checkedIn:
      case AppointmentStatus.inService:
        return Colors.green;
      case AppointmentStatus.completed:
        return Colors.grey;
      case AppointmentStatus.cancelled:
      case AppointmentStatus.noShow:
      case AppointmentStatus.expired:
        return Colors.red;
    }
  }

  /// 格式化预约时间
  String get formattedTime {
    return '$startTime - $endTime';
  }

  /// 格式化日期
  String get formattedDate {
    return DateFormat('yyyy年MM月dd日').format(appointmentDate);
  }

  /// 是否可取消
  bool get canCancel {
    return status == AppointmentStatus.pending ||
        status == AppointmentStatus.confirmed;
  }

  /// 是否可签到
  bool get canCheckIn {
    return status == AppointmentStatus.confirmed;
  }
}

/// 预约状态枚举
enum AppointmentStatus {
  pending,
  confirmed,
  checkedIn,
  inService,
  completed,
  cancelled,
  noShow,
  expired,
}

/// 可预约时段模型
class AvailableTimeSlot {
  final DateTime date;
  final String startTime;
  final String endTime;
  final int duration;
  final int maxCapacity;
  final int bookedCount;
  final int remainingCapacity;
  final bool isAvailable;
  final bool isFull;
  final double? price;
  final double? deposit;
  final int scheduleId;

  AvailableTimeSlot({
    required this.date,
    required this.startTime,
    required this.endTime,
    required this.duration,
    required this.maxCapacity,
    required this.bookedCount,
    required this.remainingCapacity,
    required this.isAvailable,
    required this.isFull,
    this.price,
    this.deposit,
    required this.scheduleId,
  });

  factory AvailableTimeSlot.fromJson(Map<String, dynamic> json) {
    return AvailableTimeSlot(
      date: DateTime.parse(json['date']),
      startTime: json['startTime'],
      endTime: json['endTime'],
      duration: json['duration'],
      maxCapacity: json['maxCapacity'],
      bookedCount: json['bookedCount'],
      remainingCapacity: json['remainingCapacity'],
      isAvailable: json['isAvailable'],
      isFull: json['isFull'],
      price: json['price']?.toDouble(),
      deposit: json['deposit']?.toDouble(),
      scheduleId: json['scheduleId'],
    );
  }
}

/// 排队票号模型
class QueueTicketModel {
  final int id;
  final String queueCode;
  final int userId;
  final int merchantId;
  final String merchantName;
  final String? merchantLogo;
  final String queueType;
  final String queueTypeName;
  final int queueNumber;
  final int? currentNumber;
  final int? aheadCount;
  final int peopleCount;
  final int? estimatedWaitTime;
  final QueueStatus status;
  final String? tableType;
  final int priority;
  final DateTime takeTime;
  final DateTime? callTime;
  final DateTime? arriveTime;
  final DateTime? completeTime;
  final DateTime? expireTime;
  final String? tableName;
  final String? staffName;
  final String contactName;
  final String contactPhone;
  final String? remark;
  final bool notified;
  final double? distanceWhenTake;
  final String? source;
  final DateTime createdAt;

  QueueTicketModel({
    required this.id,
    required this.queueCode,
    required this.userId,
    required this.merchantId,
    required this.merchantName,
    this.merchantLogo,
    required this.queueType,
    required this.queueTypeName,
    required this.queueNumber,
    this.currentNumber,
    this.aheadCount,
    required this.peopleCount,
    this.estimatedWaitTime,
    required this.status,
    this.tableType,
    required this.priority,
    required this.takeTime,
    this.callTime,
    this.arriveTime,
    this.completeTime,
    this.expireTime,
    this.tableName,
    this.staffName,
    required this.contactName,
    required this.contactPhone,
    this.remark,
    required this.notified,
    this.distanceWhenTake,
    this.source,
    required this.createdAt,
  });

  factory QueueTicketModel.fromJson(Map<String, dynamic> json) {
    return QueueTicketModel(
      id: json['id'],
      queueCode: json['queueCode'],
      userId: json['userId'],
      merchantId: json['merchantId'],
      merchantName: json['merchantName'],
      merchantLogo: json['merchantLogo'],
      queueType: json['queueType'],
      queueTypeName: json['queueTypeName'],
      queueNumber: json['queueNumber'],
      currentNumber: json['currentNumber'],
      aheadCount: json['aheadCount'],
      peopleCount: json['peopleCount'],
      estimatedWaitTime: json['estimatedWaitTime'],
      status: QueueStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => QueueStatus.waiting,
      ),
      tableType: json['tableType'],
      priority: json['priority'] ?? 0,
      takeTime: DateTime.parse(json['takeTime']),
      callTime: json['callTime'] != null
          ? DateTime.parse(json['callTime'])
          : null,
      arriveTime: json['arriveTime'] != null
          ? DateTime.parse(json['arriveTime'])
          : null,
      completeTime: json['completeTime'] != null
          ? DateTime.parse(json['completeTime'])
          : null,
      expireTime: json['expireTime'] != null
          ? DateTime.parse(json['expireTime'])
          : null,
      tableName: json['tableName'],
      staffName: json['staffName'],
      contactName: json['contactName'],
      contactPhone: json['contactPhone'],
      remark: json['remark'],
      notified: json['notified'] ?? false,
      distanceWhenTake: json['distanceWhenTake']?.toDouble(),
      source: json['source'],
      createdAt: DateTime.parse(json['createdAt']),
    );
  }

  /// 获取状态显示文本
  String get statusText {
    switch (status) {
      case QueueStatus.waiting:
        return '等待中';
      case QueueStatus.called:
        return '已叫号';
      case QueueStatus.arrived:
        return '已到达';
      case QueueStatus.serving:
        return '服务中';
      case QueueStatus.completed:
        return '已完成';
      case QueueStatus.cancelled:
        return '已取消';
      case QueueStatus.expired:
        return '已过期';
      case QueueStatus.passed:
        return '已过号';
    }
  }

  /// 获取状态颜色
  Color get statusColor {
    switch (status) {
      case QueueStatus.waiting:
        return Colors.orange;
      case QueueStatus.called:
        return Colors.blue;
      case QueueStatus.arrived:
      case QueueStatus.serving:
        return Colors.green;
      case QueueStatus.completed:
        return Colors.grey;
      case QueueStatus.cancelled:
      case QueueStatus.expired:
      case QueueStatus.passed:
        return Colors.red;
    }
  }

  /// 格式化等待时间
  String get formattedWaitTime {
    if (estimatedWaitTime == null) return '计算中...';
    if (estimatedWaitTime! < 60) return '$estimatedWaitTime分钟';
    final hours = estimatedWaitTime! ~/ 60;
    final mins = estimatedWaitTime! % 60;
    return '${hours}小时${mins > 0 ? '${mins}分' : ''}';
  }

  /// 是否显示前面等待人数
  bool get showAheadCount {
    return aheadCount != null && status == QueueStatus.waiting;
  }
}

/// 排队状态枚举
enum QueueStatus {
  waiting,
  called,
  arrived,
  serving,
  completed,
  cancelled,
  expired,
  passed,
}
