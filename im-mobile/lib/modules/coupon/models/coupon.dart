import 'package:json_annotation/json_annotation.dart';

part 'coupon.g.dart';

/// 优惠券模型
/// 支持满减券、折扣券、无门槛券等多种类型
@JsonSerializable()
class Coupon {
  final int? id;
  final int? templateId;
  final int? merchantId;
  final String? name;
  final String? description;
  final int? type;
  final String? typeName;
  final double? value;
  final double? minSpend;
  final double? maxDiscount;
  final int? totalQuantity;
  final int? receivedQuantity;
  final int? remainingQuantity;
  final int? limitPerUser;
  final DateTime? startTime;
  final DateTime? endTime;
  final int? scopeType;
  final String? scopeTypeName;
  final int? merchantScope;
  final bool? newUserOnly;
  final bool? stackable;
  final int? status;
  final String? statusName;
  final DateTime? createTime;
  
  // 扩展字段
  final String? merchantName;
  final String? merchantLogo;
  final double? distance;
  final bool? hasReceived;
  final int? userReceivedCount;

  Coupon({
    this.id,
    this.templateId,
    this.merchantId,
    this.name,
    this.description,
    this.type,
    this.typeName,
    this.value,
    this.minSpend,
    this.maxDiscount,
    this.totalQuantity,
    this.receivedQuantity,
    this.remainingQuantity,
    this.limitPerUser,
    this.startTime,
    this.endTime,
    this.scopeType,
    this.scopeTypeName,
    this.merchantScope,
    this.newUserOnly,
    this.stackable,
    this.status,
    this.statusName,
    this.createTime,
    this.merchantName,
    this.merchantLogo,
    this.distance,
    this.hasReceived,
    this.userReceivedCount,
  });

  factory Coupon.fromJson(Map<String, dynamic> json) => _$CouponFromJson(json);
  Map<String, dynamic> toJson() => _$CouponToJson(this);

  /// 检查优惠券是否有效
  bool get isValid {
    if (status != 1) return false;
    final now = DateTime.now();
    if (startTime == null || endTime == null) return false;
    return now.isAfter(startTime!) && now.isBefore(endTime!);
  }

  /// 是否还有库存
  bool get hasStock => (remainingQuantity ?? 0) > 0;

  /// 格式化显示文本
  String get displayText {
    switch (type) {
      case 1: // 满减券
        return '满${minSpend?.toStringAsFixed(0) ?? 0}减${value?.toStringAsFixed(0) ?? 0}';
      case 2: // 折扣券
        final discount = ((value ?? 0) * 10).toStringAsFixed(1);
        return '$discount折';
      case 3: // 无门槛券
        return '${value?.toStringAsFixed(0) ?? 0}元';
      case 4: // 兑换券
        return '兑换券';
      default:
        return '';
    }
  }

  /// 获取状态颜色
  String get statusColor {
    switch (status) {
      case 1:
        return '#52C41A'; // 进行中 - 绿色
      case 0:
        return '#FAAD14'; // 未开始 - 橙色
      case 2:
      case 3:
        return '#999999'; // 已结束/停发 - 灰色
      default:
        return '#999999';
    }
  }

  /// 格式化距离显示
  String? get distanceText {
    if (distance == null) return null;
    if (distance! < 1000) {
      return '${distance!.toStringAsFixed(0)}m';
    } else {
      return '${(distance! / 1000).toStringAsFixed(1)}km';
    }
  }

  /// 剩余天数
  int? get remainingDays {
    if (endTime == null) return null;
    final now = DateTime.now();
    final diff = endTime!.difference(now);
    return diff.inDays;
  }

  /// 是否即将过期（3天内）
  bool get isExpiringSoon {
    final days = remainingDays;
    return days != null && days <= 3 && days >= 0;
  }

  Coupon copyWith({
    int? id,
    int? templateId,
    int? merchantId,
    String? name,
    String? description,
    int? type,
    String? typeName,
    double? value,
    double? minSpend,
    double? maxDiscount,
    int? totalQuantity,
    int? receivedQuantity,
    int? remainingQuantity,
    int? limitPerUser,
    DateTime? startTime,
    DateTime? endTime,
    int? scopeType,
    String? scopeTypeName,
    int? merchantScope,
    bool? newUserOnly,
    bool? stackable,
    int? status,
    String? statusName,
    DateTime? createTime,
    String? merchantName,
    String? merchantLogo,
    double? distance,
    bool? hasReceived,
    int? userReceivedCount,
  }) {
    return Coupon(
      id: id ?? this.id,
      templateId: templateId ?? this.templateId,
      merchantId: merchantId ?? this.merchantId,
      name: name ?? this.name,
      description: description ?? this.description,
      type: type ?? this.type,
      typeName: typeName ?? this.typeName,
      value: value ?? this.value,
      minSpend: minSpend ?? this.minSpend,
      maxDiscount: maxDiscount ?? this.maxDiscount,
      totalQuantity: totalQuantity ?? this.totalQuantity,
      receivedQuantity: receivedQuantity ?? this.receivedQuantity,
      remainingQuantity: remainingQuantity ?? this.remainingQuantity,
      limitPerUser: limitPerUser ?? this.limitPerUser,
      startTime: startTime ?? this.startTime,
      endTime: endTime ?? this.endTime,
      scopeType: scopeType ?? this.scopeType,
      scopeTypeName: scopeTypeName ?? this.scopeTypeName,
      merchantScope: merchantScope ?? this.merchantScope,
      newUserOnly: newUserOnly ?? this.newUserOnly,
      stackable: stackable ?? this.stackable,
      status: status ?? this.status,
      statusName: statusName ?? this.statusName,
      createTime: createTime ?? this.createTime,
      merchantName: merchantName ?? this.merchantName,
      merchantLogo: merchantLogo ?? this.merchantLogo,
      distance: distance ?? this.distance,
      hasReceived: hasReceived ?? this.hasReceived,
      userReceivedCount: userReceivedCount ?? this.userReceivedCount,
    );
  }
}

/// 用户优惠券模型
@JsonSerializable()
class UserCoupon {
  final int? id;
  final int? userId;
  final int? couponId;
  final int? templateId;
  final String? couponName;
  final int? couponType;
  final String? couponTypeName;
  final double? couponValue;
  final double? minSpend;
  final double? maxDiscount;
  final DateTime? validStartTime;
  final DateTime? validEndTime;
  final int? status;
  final String? statusName;
  final DateTime? useTime;
  final int? orderId;
  final double? orderAmount;
  final double? discountAmount;
  final DateTime? receiveTime;
  final int? receiveChannel;
  final String? receiveChannelName;
  
  // 商户信息
  final int? merchantId;
  final String? merchantName;
  final String? merchantLogo;
  
  // 扩展字段
  final bool? expiringSoon;
  final int? remainingDays;
  final String? displayText;

  UserCoupon({
    this.id,
    this.userId,
    this.couponId,
    this.templateId,
    this.couponName,
    this.couponType,
    this.couponTypeName,
    this.couponValue,
    this.minSpend,
    this.maxDiscount,
    this.validStartTime,
    this.validEndTime,
    this.status,
    this.statusName,
    this.useTime,
    this.orderId,
    this.orderAmount,
    this.discountAmount,
    this.receiveTime,
    this.receiveChannel,
    this.receiveChannelName,
    this.merchantId,
    this.merchantName,
    this.merchantLogo,
    this.expiringSoon,
    this.remainingDays,
    this.displayText,
  });

  factory UserCoupon.fromJson(Map<String, dynamic> json) => _$UserCouponFromJson(json);
  Map<String, dynamic> toJson() => _$UserCouponToJson(this);

  /// 是否可用
  bool get isUsable => status == 0;

  /// 是否已使用
  bool get isUsed => status == 1;

  /// 是否已过期
  bool get isExpired => status == 2;

  /// 获取状态颜色
  String get statusColor {
    switch (status) {
      case 0:
        return '#52C41A'; // 未使用 - 绿色
      case 1:
        return '#999999'; // 已使用 - 灰色
      case 2:
        return '#FF4D4F'; // 已过期 - 红色
      case 3:
        return '#999999'; // 已作废 - 灰色
      default:
        return '#999999';
    }
  }

  /// 格式化显示文本
  String get formattedDisplayText {
    if (displayText != null && displayText!.isNotEmpty) {
      return displayText!;
    }
    switch (couponType) {
      case 1: // 满减券
        return '满${minSpend?.toStringAsFixed(0) ?? 0}减${couponValue?.toStringAsFixed(0) ?? 0}';
      case 2: // 折扣券
        final discount = ((couponValue ?? 0) * 10).toStringAsFixed(1);
        return '$discount折';
      case 3: // 无门槛券
        return '${couponValue?.toStringAsFixed(0) ?? 0}元';
      default:
        return '';
    }
  }

  /// 有效期文本
  String get validityText {
    if (validStartTime == null || validEndTime == null) return '';
    final start = '${validStartTime!.month}.${validStartTime!.day}';
    final end = '${validEndTime!.month}.${validEndTime!.day}';
    return '$start-$end';
  }

  UserCoupon copyWith({
    int? id,
    int? userId,
    int? couponId,
    int? templateId,
    String? couponName,
    int? couponType,
    String? couponTypeName,
    double? couponValue,
    double? minSpend,
    double? maxDiscount,
    DateTime? validStartTime,
    DateTime? validEndTime,
    int? status,
    String? statusName,
    DateTime? useTime,
    int? orderId,
    double? orderAmount,
    double? discountAmount,
    DateTime? receiveTime,
    int? receiveChannel,
    String? receiveChannelName,
    int? merchantId,
    String? merchantName,
    String? merchantLogo,
    bool? expiringSoon,
    int? remainingDays,
    String? displayText,
  }) {
    return UserCoupon(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      couponId: couponId ?? this.couponId,
      templateId: templateId ?? this.templateId,
      couponName: couponName ?? this.couponName,
      couponType: couponType ?? this.couponType,
      couponTypeName: couponTypeName ?? this.couponTypeName,
      couponValue: couponValue ?? this.couponValue,
      minSpend: minSpend ?? this.minSpend,
      maxDiscount: maxDiscount ?? this.maxDiscount,
      validStartTime: validStartTime ?? this.validStartTime,
      validEndTime: validEndTime ?? this.validEndTime,
      status: status ?? this.status,
      statusName: statusName ?? this.statusName,
      useTime: useTime ?? this.useTime,
      orderId: orderId ?? this.orderId,
      orderAmount: orderAmount ?? this.orderAmount,
      discountAmount: discountAmount ?? this.discountAmount,
      receiveTime: receiveTime ?? this.receiveTime,
      receiveChannel: receiveChannel ?? this.receiveChannel,
      receiveChannelName: receiveChannelName ?? this.receiveChannelName,
      merchantId: merchantId ?? this.merchantId,
      merchantName: merchantName ?? this.merchantName,
      merchantLogo: merchantLogo ?? this.merchantLogo,
      expiringSoon: expiringSoon ?? this.expiringSoon,
      remainingDays: remainingDays ?? this.remainingDays,
      displayText: displayText ?? this.displayText,
    );
  }
}

/// 领取优惠券请求
@JsonSerializable()
class ReceiveCouponRequest {
  final int couponId;
  final int? receiveChannel;
  final int? sourceUserId;
  final double? longitude;
  final double? latitude;

  ReceiveCouponRequest({
    required this.couponId,
    this.receiveChannel = 1,
    this.sourceUserId,
    this.longitude,
    this.latitude,
  });

  factory ReceiveCouponRequest.fromJson(Map<String, dynamic> json) => 
      _$ReceiveCouponRequestFromJson(json);
  Map<String, dynamic> toJson() => _$ReceiveCouponRequestToJson(this);
}

/// 使用优惠券请求
@JsonSerializable()
class UseCouponRequest {
  final int userCouponId;
  final int orderId;
  final double orderAmount;
  final List<int>? productIds;
  final int? merchantId;

  UseCouponRequest({
    required this.userCouponId,
    required this.orderId,
    required this.orderAmount,
    this.productIds,
    this.merchantId,
  });

  factory UseCouponRequest.fromJson(Map<String, dynamic> json) => 
      _$UseCouponRequestFromJson(json);
  Map<String, dynamic> toJson() => _$UseCouponRequestToJson(this);
}
