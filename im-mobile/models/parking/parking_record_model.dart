import 'package:json_annotation/json_annotation.dart';

part 'parking_record_model.g.dart';

/// 停车记录模型
/// 记录用户每次停车的完整信息
@JsonSerializable()
class ParkingRecordModel {
  /// 停车记录ID
  final String id;

  /// 用户ID
  final String userId;

  /// 停车场ID
  final String parkingLotId;

  /// 停车场名称
  final String? parkingLotName;

  /// 车牌号
  final String plateNumber;

  /// 车牌颜色：blue,yellow,green,white,black
  final String? plateColor;

  /// 车辆类型：1-小型车 2-中型车 3-大型车 4-新能源车
  final int? vehicleType;

  /// 车辆ID
  final String? vehicleId;

  /// 入场时间
  final DateTime entryTime;

  /// 入场经度
  final double? entryLongitude;

  /// 入场纬度
  final double? entryLatitude;

  /// 入场位置名称
  final String? entryLocationName;

  /// 入场方式：1-自动识别 2-扫码 3-预约 4-共享停车
  final int entryMethod;

  /// 入场照片URL
  final String? entryPhotoUrl;

  /// 停车楼层
  final String? parkingFloor;

  /// 停车区域
  final String? parkingArea;

  /// 停车位编号
  final String? parkingSpaceNumber;

  /// 车位照片URL
  final String? parkingSpacePhotoUrl;

  /// 车位经度
  final double? spaceLongitude;

  /// 车位纬度
  final double? spaceLatitude;

  /// 车位标记备注
  final String? spaceMarkNote;

  /// 出场时间
  final DateTime? exitTime;

  /// 出场经度
  final double? exitLongitude;

  /// 出场纬度
  final double? exitLatitude;

  /// 出场方式：1-自动识别 2-扫码 3-无感支付 4-人工处理
  final int? exitMethod;

  /// 出场照片URL
  final String? exitPhotoUrl;

  /// 停车状态：1-停车中 2-已离场 3-已取消
  final int status;

  /// 停车时长（分钟）
  final int? parkingDuration;

  /// 应缴金额
  final double? payableAmount;

  /// 优惠金额
  final double? discountAmount;

  /// 实缴金额
  final double? actualAmount;

  /// 支付方式：wechat,alipay,unionpay,cash,points
  final String? paymentMethod;

  /// 支付时间
  final DateTime? paymentTime;

  /// 是否使用优惠券
  final bool? usedCoupon;

  /// 优惠券名称
  final String? couponName;

  /// 是否使用积分抵扣
  final bool? usedPoints;

  /// 抵扣积分数量
  final int? usedPointsAmount;

  /// 积分抵扣金额
  final double? pointsDiscountAmount;

  /// 发票状态：0-未开具 1-已申请 2-已开具
  final int? invoiceStatus;

  /// 是否预约停车
  final bool? isReservation;

  /// 是否为共享停车
  final bool? isSharedParking;

  /// 订单备注
  final String? remark;

  /// 创建时间
  final DateTime? createTime;

  ParkingRecordModel({
    required this.id,
    required this.userId,
    required this.parkingLotId,
    this.parkingLotName,
    required this.plateNumber,
    this.plateColor,
    this.vehicleType,
    this.vehicleId,
    required this.entryTime,
    this.entryLongitude,
    this.entryLatitude,
    this.entryLocationName,
    required this.entryMethod,
    this.entryPhotoUrl,
    this.parkingFloor,
    this.parkingArea,
    this.parkingSpaceNumber,
    this.parkingSpacePhotoUrl,
    this.spaceLongitude,
    this.spaceLatitude,
    this.spaceMarkNote,
    this.exitTime,
    this.exitLongitude,
    this.exitLatitude,
    this.exitMethod,
    this.exitPhotoUrl,
    required this.status,
    this.parkingDuration,
    this.payableAmount,
    this.discountAmount,
    this.actualAmount,
    this.paymentMethod,
    this.paymentTime,
    this.usedCoupon,
    this.couponName,
    this.usedPoints,
    this.usedPointsAmount,
    this.pointsDiscountAmount,
    this.invoiceStatus,
    this.isReservation,
    this.isSharedParking,
    this.remark,
    this.createTime,
  });

  factory ParkingRecordModel.fromJson(Map<String, dynamic> json) =>
      _$ParkingRecordModelFromJson(json);

  Map<String, dynamic> toJson() => _$ParkingRecordModelToJson(this);

  /// 计算停车时长
  int get calculatedDuration {
    if (parkingDuration != null) return parkingDuration!;

    final end = exitTime ?? DateTime.now();
    return end.difference(entryTime).inMinutes;
  }

  /// 获取格式化的停车时长
  String get formattedDuration {
    final minutes = calculatedDuration;
    if (minutes == 0) return '0分钟';

    final hours = minutes ~/ 60;
    final mins = minutes % 60;

    if (hours > 0 && mins > 0) {
      return '${hours}小时${mins}分钟';
    } else if (hours > 0) {
      return '${hours}小时';
    } else {
      return '${mins}分钟';
    }
  }

  /// 是否停车中
  bool get isParking {
    return status == 1;
  }

  /// 是否已完成
  bool get isCompleted {
    return status == 2;
  }

  /// 是否已支付
  bool get isPaid {
    return actualAmount != null && actualAmount! > 0 && paymentTime != null;
  }

  /// 获取状态文本
  String get statusText {
    switch (status) {
      case 1:
        return '停车中';
      case 2:
        return '已离场';
      case 3:
        return '已取消';
      default:
        return '未知';
    }
  }

  /// 获取状态颜色
  String get statusColor {
    switch (status) {
      case 1:
        return '#4CAF50'; // 绿色
      case 2:
        return '#9E9E9E'; // 灰色
      case 3:
        return '#F44336'; // 红色
      default:
        return '#9E9E9E';
    }
  }

  /// 获取支付方式文本
  String? get paymentMethodText {
    switch (paymentMethod) {
      case 'wechat':
        return '微信支付';
      case 'alipay':
        return '支付宝';
      case 'unionpay':
        return '银联支付';
      case 'cash':
        return '现金支付';
      case 'points':
        return '积分抵扣';
      default:
        return null;
    }
  }

  /// 获取完整停车位置信息
  String get fullParkingLocation {
    final parts = <String>[];
    if (parkingFloor != null && parkingFloor!.isNotEmpty) {
      parts.add('${parkingFloor}层');
    }
    if (parkingArea != null && parkingArea!.isNotEmpty) {
      parts.add('${parkingArea}区');
    }
    if (parkingSpaceNumber != null && parkingSpaceNumber!.isNotEmpty) {
      parts.add('${parkingSpaceNumber}号车位');
    }
    return parts.join(' ');
  }

  /// 获取发票状态文本
  String get invoiceStatusText {
    switch (invoiceStatus) {
      case 0:
        return '未开具';
      case 1:
        return '申请中';
      case 2:
        return '已开具';
      default:
        return '未知';
    }
  }

  /// 获取停车天数
  int get parkingDays {
    return calculatedDuration ~/ (24 * 60);
  }

  /// 是否可以使用反向寻车
  bool get canUseCarFinding {
    return isParking || (isCompleted && exitTime != null &&
        DateTime.now().difference(exitTime!).inHours < 24);
  }
}

/// 停车记录统计模型
@JsonSerializable()
class ParkingRecordStatisticsModel {
  /// 总停车次数
  final int totalParkingCount;

  /// 总停车时长（分钟）
  final int totalParkingDuration;

  /// 总消费金额
  final double totalAmount;

  /// 优惠总金额
  final double totalDiscount;

  /// 平均停车时长（分钟）
  final int avgParkingDuration;

  /// 平均消费金额
  final double avgAmount;

  /// 最常去的停车场数量
  final int frequentParkingLotCount;

  /// 本月停车次数
  final int thisMonthCount;

  /// 本月消费金额
  final double thisMonthAmount;

  ParkingRecordStatisticsModel({
    required this.totalParkingCount,
    required this.totalParkingDuration,
    required this.totalAmount,
    required this.totalDiscount,
    required this.avgParkingDuration,
    required this.avgAmount,
    required this.frequentParkingLotCount,
    required this.thisMonthCount,
    required this.thisMonthAmount,
  });

  factory ParkingRecordStatisticsModel.fromJson(Map<String, dynamic> json) =>
      _$ParkingRecordStatisticsModelFromJson(json);

  Map<String, dynamic> toJson() => _$ParkingRecordStatisticsModelToJson(this);

  /// 格式化总停车时长
  String get formattedTotalDuration {
    final days = totalParkingDuration ~/ (24 * 60);
    final hours = (totalParkingDuration % (24 * 60)) ~/ 60;

    if (days > 0) {
      return '${days}天${hours}小时';
    } else if (hours > 0) {
      return '${hours}小时';
    } else {
      return '${totalParkingDuration}分钟';
    }
  }

  /// 格式化平均停车时长
  String get formattedAvgDuration {
    final hours = avgParkingDuration ~/ 60;
    final mins = avgParkingDuration % 60;

    if (hours > 0 && mins > 0) {
      return '${hours}小时${mins}分钟';
    } else if (hours > 0) {
      return '${hours}小时';
    } else {
      return '${mins}分钟';
    }
  }
}

/// 用户停车概览模型
@JsonSerializable()
class UserParkingOverviewModel {
  /// 当前是否有停车记录
  final bool hasCurrentParking;

  /// 当前停车记录
  final ParkingRecordModel? currentParking;

  /// 本月停车次数
  final int thisMonthCount;

  /// 本月消费金额
  final double thisMonthAmount;

  /// 常用车牌号列表
  final List<String> frequentPlateNumbers;

  /// 常去停车场列表
  final List<ParkingLotSimpleModel> frequentParkingLots;

  /// 最近停车记录列表
  final List<ParkingRecordModel> recentRecords;

  UserParkingOverviewModel({
    required this.hasCurrentParking,
    this.currentParking,
    required this.thisMonthCount,
    required this.thisMonthAmount,
    required this.frequentPlateNumbers,
    required this.frequentParkingLots,
    required this.recentRecords,
  });

  factory UserParkingOverviewModel.fromJson(Map<String, dynamic> json) =>
      _$UserParkingOverviewModelFromJson(json);

  Map<String, dynamic> toJson() => _$UserParkingOverviewModelToJson(this);
}

/// 简易停车场模型（用于概览展示）
@JsonSerializable()
class ParkingLotSimpleModel {
  /// 停车场ID
  final String id;

  /// 停车场名称
  final String name;

  /// 地址
  final String address;

  /// 经度
  final double longitude;

  /// 纬度
  final double latitude;

  ParkingLotSimpleModel({
    required this.id,
    required this.name,
    required this.address,
    required this.longitude,
    required this.latitude,
  });

  factory ParkingLotSimpleModel.fromJson(Map<String, dynamic> json) =>
      _$ParkingLotSimpleModelFromJson(json);

  Map<String, dynamic> toJson() => _$ParkingLotSimpleModelToJson(this);
}
