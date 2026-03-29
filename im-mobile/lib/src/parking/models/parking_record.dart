import 'dart:convert';

import 'parking_lot.dart';

/// 停车记录
class ParkingRecord {
  /// 记录ID
  final String id;
  
  /// 停车场ID
  final String parkingLotId;
  
  /// 停车场名称
  final String parkingLotName;
  
  /// 开始停车时间
  final DateTime startTime;
  
  /// 结束停车时间
  final DateTime? endTime;
  
  /// 停车时长
  final Duration? duration;
  
  /// 楼层
  final String? floor;
  
  /// 区域
  final String? area;
  
  /// 车位号
  final String? spotNumber;
  
  /// 停车位置照片
  final String? photoPath;
  
  /// 备注
  final String? notes;
  
  /// 停车位置
  final ParkingLocation? location;
  
  /// 停车状态
  final ParkingStatus status;
  
  /// 费用
  final ParkingFee? fee;
  
  /// 支付方式
  final PaymentMethod? paymentMethod;
  
  /// 使用的优惠券ID
  final String? couponId;
  
  /// 创建时间
  final DateTime createdAt;
  
  /// 更新时间
  final DateTime? updatedAt;

  ParkingRecord({
    required this.id,
    required this.parkingLotId,
    required this.parkingLotName,
    required this.startTime,
    this.endTime,
    this.duration,
    this.floor,
    this.area,
    this.spotNumber,
    this.photoPath,
    this.notes,
    this.location,
    this.status = ParkingStatus.parking,
    this.fee,
    this.paymentMethod,
    this.couponId,
    DateTime? createdAt,
    this.updatedAt,
  }) : createdAt = createdAt ?? DateTime.now();

  /// 从JSON创建
  factory ParkingRecord.fromJson(Map<String, dynamic> json) {
    return ParkingRecord(
      id: json['id'] as String,
      parkingLotId: json['parkingLotId'] as String,
      parkingLotName: json['parkingLotName'] as String,
      startTime: DateTime.parse(json['startTime'] as String),
      endTime: json['endTime'] != null
          ? DateTime.parse(json['endTime'] as String)
          : null,
      duration: json['durationMinutes'] != null
          ? Duration(minutes: json['durationMinutes'] as int)
          : null,
      floor: json['floor'] as String?,
      area: json['area'] as String?,
      spotNumber: json['spotNumber'] as String?,
      photoPath: json['photoPath'] as String?,
      notes: json['notes'] as String?,
      location: json['location'] != null
          ? ParkingLocation.fromJson(json['location'] as Map<String, dynamic>)
          : null,
      status: ParkingStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => ParkingStatus.parking,
      ),
      fee: json['fee'] != null
          ? ParkingFee.fromJson(json['fee'] as Map<String, dynamic>)
          : null,
      paymentMethod: json['paymentMethod'] != null
          ? PaymentMethod.values.firstWhere(
              (e) => e.name == json['paymentMethod'],
              orElse: () => PaymentMethod.wechat,
            )
          : null,
      couponId: json['couponId'] as String?,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'] as String)
          : null,
    );
  }

  /// 转换为JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'parkingLotId': parkingLotId,
      'parkingLotName': parkingLotName,
      'startTime': startTime.toIso8601String(),
      'endTime': endTime?.toIso8601String(),
      'durationMinutes': duration?.inMinutes,
      'floor': floor,
      'area': area,
      'spotNumber': spotNumber,
      'photoPath': photoPath,
      'notes': notes,
      'location': location?.toJson(),
      'status': status.name,
      'fee': fee?.toJson(),
      'paymentMethod': paymentMethod?.name,
      'couponId': couponId,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }

  /// 复制并修改
  ParkingRecord copyWith({
    String? id,
    String? parkingLotId,
    String? parkingLotName,
    DateTime? startTime,
    DateTime? endTime,
    Duration? duration,
    String? floor,
    String? area,
    String? spotNumber,
    String? photoPath,
    String? notes,
    ParkingLocation? location,
    ParkingStatus? status,
    ParkingFee? fee,
    PaymentMethod? paymentMethod,
    String? couponId,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return ParkingRecord(
      id: id ?? this.id,
      parkingLotId: parkingLotId ?? this.parkingLotId,
      parkingLotName: parkingLotName ?? this.parkingLotName,
      startTime: startTime ?? this.startTime,
      endTime: endTime ?? this.endTime,
      duration: duration ?? this.duration,
      floor: floor ?? this.floor,
      area: area ?? this.area,
      spotNumber: spotNumber ?? this.spotNumber,
      photoPath: photoPath ?? this.photoPath,
      notes: notes ?? this.notes,
      location: location ?? this.location,
      status: status ?? this.status,
      fee: fee ?? this.fee,
      paymentMethod: paymentMethod ?? this.paymentMethod,
      couponId: couponId ?? this.couponId,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  /// 获取当前停车时长（如果还在停车中）
  Duration get currentDuration {
    if (endTime != null && duration != null) {
      return duration!;
    }
    return DateTime.now().difference(startTime);
  }

  /// 获取停车时长文本
  String get durationText {
    final d = currentDuration;
    final hours = d.inHours;
    final minutes = d.inMinutes % 60;
    
    if (hours > 0) {
      return '${hours}小时${minutes > 0 ? '${minutes}分钟' : ''}';
    }
    return '${minutes}分钟';
  }

  /// 格式化开始时间
  String get formattedStartTime {
    return '${startTime.month}月${startTime.day}日 ${startTime.hour.toString().padLeft(2, '0')}:${startTime.minute.toString().padLeft(2, '0')}';
  }

  /// 是否已完成
  bool get isCompleted => status == ParkingStatus.completed;

  /// 是否正在停车
  bool get isParking => status == ParkingStatus.parking;

  @override
  String toString() {
    return 'ParkingRecord(id: $id, lot: $parkingLotName, status: ${status.name})';
  }
}

/// 停车状态
enum ParkingStatus {
  /// 停车中
  parking,
  
  /// 已完成
  completed,
  
  /// 已取消
  cancelled,
}

/// 停车位置
class ParkingLocation {
  /// 纬度
  final double latitude;
  
  /// 经度
  final double longitude;
  
  /// 海拔
  final double? altitude;
  
  /// 精度（米）
  final double accuracy;
  
  /// 楼层（室内定位时）
  final int? floor;

  ParkingLocation({
    required this.latitude,
    required this.longitude,
    this.altitude,
    this.accuracy = 0,
    this.floor,
  });

  factory ParkingLocation.fromJson(Map<String, dynamic> json) {
    return ParkingLocation(
      latitude: (json['latitude'] as num).toDouble(),
      longitude: (json['longitude'] as num).toDouble(),
      altitude: json['altitude'] != null ? (json['altitude'] as num).toDouble() : null,
      accuracy: (json['accuracy'] as num?)?.toDouble() ?? 0,
      floor: json['floor'] as int?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'latitude': latitude,
      'longitude': longitude,
      'altitude': altitude,
      'accuracy': accuracy,
      'floor': floor,
    };
  }
}

/// 停车费用
class ParkingFee {
  /// 基础金额
  final double baseAmount;
  
  /// 折扣金额
  final double discountAmount;
  
  /// 最终金额
  final double finalAmount;
  
  /// 货币
  final String currency;
  
  /// 费用明细
  final List<FeeItem>? items;

  ParkingFee({
    required this.baseAmount,
    required this.discountAmount,
    required this.finalAmount,
    this.currency = 'CNY',
    this.items,
  });

  factory ParkingFee.fromJson(Map<String, dynamic> json) {
    return ParkingFee(
      baseAmount: (json['baseAmount'] as num).toDouble(),
      discountAmount: (json['discountAmount'] as num).toDouble(),
      finalAmount: (json['finalAmount'] as num).toDouble(),
      currency: json['currency'] as String? ?? 'CNY',
      items: (json['items'] as List?)
          ?.map((i) => FeeItem.fromJson(i as Map<String, dynamic>))
          .toList(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'baseAmount': baseAmount,
      'discountAmount': discountAmount,
      'finalAmount': finalAmount,
      'currency': currency,
      'items': items?.map((i) => i.toJson()).toList(),
    };
  }

  /// 格式化金额
  String get formattedAmount {
    return '¥${finalAmount.toStringAsFixed(2)}';
  }

  /// 是否有折扣
  bool get hasDiscount => discountAmount > 0;
}

/// 费用明细项
class FeeItem {
  /// 项目名称
  final String name;
  
  /// 金额
  final double amount;
  
  /// 类型
  final FeeItemType type;

  FeeItem({
    required this.name,
    required this.amount,
    this.type = FeeItemType.normal,
  });

  factory FeeItem.fromJson(Map<String, dynamic> json) {
    return FeeItem(
      name: json['name'] as String,
      amount: (json['amount'] as num).toDouble(),
      type: FeeItemType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => FeeItemType.normal,
      ),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'amount': amount,
      'type': type.name,
    };
  }
}

/// 费用项类型
enum FeeItemType {
  /// 正常费用
  normal,
  
  /// 停车费
  parking,
  
  /// 折扣
  discount,
  
  /// 优惠券抵扣
  coupon,
  
  /// 服务费
  service,
}

/// 停车优惠券
class ParkingCoupon {
  /// 优惠券ID
  final String id;
  
  /// 标题
  final String title;
  
  /// 描述
  final String? description;
  
  /// 优惠金额
  final double amount;
  
  /// 优惠类型
  final CouponType type;
  
  /// 最低消费金额
  final double? minSpend;
  
  /// 有效期开始
  final DateTime validFrom;
  
  /// 有效期结束
  final DateTime validUntil;
  
  /// 适用停车场ID列表
  final List<String>? applicableParkingIds;
  
  /// 是否已使用
  final bool isUsed;
  
  /// 使用时间
  final DateTime? usedAt;
  
  /// 使用记录ID
  final String? usedRecordId;

  ParkingCoupon({
    required this.id,
    required this.title,
    this.description,
    required this.amount,
    this.type = CouponType.fixed,
    this.minSpend,
    required this.validFrom,
    required this.validUntil,
    this.applicableParkingIds,
    this.isUsed = false,
    this.usedAt,
    this.usedRecordId,
  });

  factory ParkingCoupon.fromJson(Map<String, dynamic> json) {
    return ParkingCoupon(
      id: json['id'] as String,
      title: json['title'] as String,
      description: json['description'] as String?,
      amount: (json['amount'] as num).toDouble(),
      type: CouponType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => CouponType.fixed,
      ),
      minSpend: json['minSpend'] != null ? (json['minSpend'] as num).toDouble() : null,
      validFrom: DateTime.parse(json['validFrom'] as String),
      validUntil: DateTime.parse(json['validUntil'] as String),
      applicableParkingIds: (json['applicableParkingIds'] as List?)?.cast<String>(),
      isUsed: json['isUsed'] as bool? ?? false,
      usedAt: json['usedAt'] != null ? DateTime.parse(json['usedAt'] as String) : null,
      usedRecordId: json['usedRecordId'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'description': description,
      'amount': amount,
      'type': type.name,
      'minSpend': minSpend,
      'validFrom': validFrom.toIso8601String(),
      'validUntil': validUntil.toIso8601String(),
      'applicableParkingIds': applicableParkingIds,
      'isUsed': isUsed,
      'usedAt': usedAt?.toIso8601String(),
      'usedRecordId': usedRecordId,
    };
  }

  /// 是否有效
  bool get isValid {
    final now = DateTime.now();
    return now.isAfter(validFrom) &&
           now.isBefore(validUntil) &&
           !isUsed;
  }

  /// 格式化有效期
  String get validityText {
    final now = DateTime.now();
    final daysLeft = validUntil.difference(now).inDays;
    
    if (daysLeft < 0) return '已过期';
    if (daysLeft == 0) return '今天过期';
    if (daysLeft <= 3) return '$daysLeft天后过期';
    return '有效期至${validUntil.month}月${validUntil.day}日';
  }

  /// 格式化金额
  String get formattedAmount {
    if (type == CouponType.fixed) {
      return '¥${amount.toStringAsFixed(0)}';
    } else {
      return '${amount.toStringAsFixed(0)}折';
    }
  }
}

/// 优惠券类型
enum CouponType {
  /// 固定金额
  fixed,
  
  /// 折扣
  discount,
}

/// 共享停车位
class SharedParkingSpot {
  /// 车位ID
  final String id;
  
  /// 发布者ID
  final String ownerId;
  
  /// 位置描述
  final String location;
  
  /// 可用开始时间
  final DateTime availableFrom;
  
  /// 可用结束时间
  final DateTime availableTo;
  
  /// 每小时价格
  final double hourlyRate;
  
  /// 描述
  final String? description;
  
  /// 照片列表
  final List<String>? photos;
  
  /// 状态
  final SharedSpotStatus status;
  
  /// 创建时间
  final DateTime createdAt;

  SharedParkingSpot({
    required this.id,
    required this.ownerId,
    required this.location,
    required this.availableFrom,
    required this.availableTo,
    required this.hourlyRate,
    this.description,
    this.photos,
    this.status = SharedSpotStatus.available,
    required this.createdAt,
  });

  factory SharedParkingSpot.fromJson(Map<String, dynamic> json) {
    return SharedParkingSpot(
      id: json['id'] as String,
      ownerId: json['ownerId'] as String,
      location: json['location'] as String,
      availableFrom: DateTime.parse(json['availableFrom'] as String),
      availableTo: DateTime.parse(json['availableTo'] as String),
      hourlyRate: (json['hourlyRate'] as num).toDouble(),
      description: json['description'] as String?,
      photos: (json['photos'] as List?)?.cast<String>(),
      status: SharedSpotStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => SharedSpotStatus.available,
      ),
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'ownerId': ownerId,
      'location': location,
      'availableFrom': availableFrom.toIso8601String(),
      'availableTo': availableTo.toIso8601String(),
      'hourlyRate': hourlyRate,
      'description': description,
      'photos': photos,
      'status': status.name,
      'createdAt': createdAt.toIso8601String(),
    };
  }

  /// 可用时长（小时）
  double get availableHours {
    return availableTo.difference(availableFrom).inMinutes / 60;
  }

  /// 是否当前可用
  bool get isCurrentlyAvailable {
    final now = DateTime.now();
    return status == SharedSpotStatus.available &&
           now.isAfter(availableFrom) &&
           now.isBefore(availableTo);
  }
}

/// 共享车位状态
enum SharedSpotStatus {
  /// 可用
  available,
  
  /// 已预约
  reserved,
  
  /// 使用中
  inUse,
  
  /// 已下架
  unavailable,
}
