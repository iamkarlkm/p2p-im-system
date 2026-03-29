/// 配送订单模型类 - 即时配送运力调度系统
/// 用于移动端展示订单信息和配送状态
class DeliveryOrderModel {
  final int id;
  final int? businessOrderId;
  final String orderNo;
  final int merchantId;
  final String merchantName;
  final String merchantAddress;
  final double merchantLongitude;
  final double merchantLatitude;
  final String? merchantPhone;
  final int customerId;
  final String customerName;
  final String customerPhone;
  final String deliveryAddress;
  final double deliveryLongitude;
  final double deliveryLatitude;
  final String? addressDetail;
  final double orderAmount;
  final double deliveryFee;
  final String goodsSummary;
  final int goodsCount;
  final double? weight;
  final String? remark;
  final String status;
  final int? riderId;
  final String? riderName;
  final String? riderPhone;
  final DateTime? assignedAt;
  final DateTime? acceptedAt;
  final DateTime? arrivedAt;
  final DateTime? pickedAt;
  final DateTime? deliveredAt;
  final DateTime? completedAt;
  final DateTime? estimatedDeliveryTime;
  final DateTime? requiredDeliveryTime;
  final int? deliveryDistance;
  final int? actualDeliveryMinutes;
  final int? zoneId;
  final String? deliveryPath;
  final String? exceptionFlag;
  final String? exceptionReason;
  final String? cancelReason;
  final String? cancelledBy;
  final DateTime? cancelledAt;
  final int? customerRating;
  final String? customerComment;
  final String? riderComment;
  final String deliveryType;
  final String priority;
  final List<String>? tags;
  final DateTime createdAt;
  final DateTime updatedAt;

  DeliveryOrderModel({
    required this.id,
    this.businessOrderId,
    required this.orderNo,
    required this.merchantId,
    required this.merchantName,
    required this.merchantAddress,
    required this.merchantLongitude,
    required this.merchantLatitude,
    this.merchantPhone,
    required this.customerId,
    required this.customerName,
    required this.customerPhone,
    required this.deliveryAddress,
    required this.deliveryLongitude,
    required this.deliveryLatitude,
    this.addressDetail,
    required this.orderAmount,
    required this.deliveryFee,
    required this.goodsSummary,
    required this.goodsCount,
    this.weight,
    this.remark,
    required this.status,
    this.riderId,
    this.riderName,
    this.riderPhone,
    this.assignedAt,
    this.acceptedAt,
    this.arrivedAt,
    this.pickedAt,
    this.deliveredAt,
    this.completedAt,
    this.estimatedDeliveryTime,
    this.requiredDeliveryTime,
    this.deliveryDistance,
    this.actualDeliveryMinutes,
    this.zoneId,
    this.deliveryPath,
    this.exceptionFlag,
    this.exceptionReason,
    this.cancelReason,
    this.cancelledBy,
    this.cancelledAt,
    this.customerRating,
    this.customerComment,
    this.riderComment,
    this.deliveryType = 'IMMEDIATE',
    this.priority = 'NORMAL',
    this.tags,
    required this.createdAt,
    required this.updatedAt,
  });

  factory DeliveryOrderModel.fromJson(Map<String, dynamic> json) {
    return DeliveryOrderModel(
      id: json['id'],
      businessOrderId: json['businessOrderId'],
      orderNo: json['orderNo'],
      merchantId: json['merchantId'],
      merchantName: json['merchantName'],
      merchantAddress: json['merchantAddress'],
      merchantLongitude: json['merchantLongitude']?.toDouble() ?? 0.0,
      merchantLatitude: json['merchantLatitude']?.toDouble() ?? 0.0,
      merchantPhone: json['merchantPhone'],
      customerId: json['customerId'],
      customerName: json['customerName'],
      customerPhone: json['customerPhone'],
      deliveryAddress: json['deliveryAddress'],
      deliveryLongitude: json['deliveryLongitude']?.toDouble() ?? 0.0,
      deliveryLatitude: json['deliveryLatitude']?.toDouble() ?? 0.0,
      addressDetail: json['addressDetail'],
      orderAmount: json['orderAmount']?.toDouble() ?? 0.0,
      deliveryFee: json['deliveryFee']?.toDouble() ?? 0.0,
      goodsSummary: json['goodsSummary'] ?? '',
      goodsCount: json['goodsCount'] ?? 0,
      weight: json['weight']?.toDouble(),
      remark: json['remark'],
      status: json['status'] ?? 'WAITING',
      riderId: json['riderId'],
      riderName: json['riderName'],
      riderPhone: json['riderPhone'],
      assignedAt: json['assignedAt'] != null
          ? DateTime.parse(json['assignedAt'])
          : null,
      acceptedAt: json['acceptedAt'] != null
          ? DateTime.parse(json['acceptedAt'])
          : null,
      arrivedAt: json['arrivedAt'] != null
          ? DateTime.parse(json['arrivedAt'])
          : null,
      pickedAt: json['pickedAt'] != null
          ? DateTime.parse(json['pickedAt'])
          : null,
      deliveredAt: json['deliveredAt'] != null
          ? DateTime.parse(json['deliveredAt'])
          : null,
      completedAt: json['completedAt'] != null
          ? DateTime.parse(json['completedAt'])
          : null,
      estimatedDeliveryTime: json['estimatedDeliveryTime'] != null
          ? DateTime.parse(json['estimatedDeliveryTime'])
          : null,
      requiredDeliveryTime: json['requiredDeliveryTime'] != null
          ? DateTime.parse(json['requiredDeliveryTime'])
          : null,
      deliveryDistance: json['deliveryDistance'],
      actualDeliveryMinutes: json['actualDeliveryMinutes'],
      zoneId: json['zoneId'],
      deliveryPath: json['deliveryPath'],
      exceptionFlag: json['exceptionFlag'],
      exceptionReason: json['exceptionReason'],
      cancelReason: json['cancelReason'],
      cancelledBy: json['cancelledBy'],
      cancelledAt: json['cancelledAt'] != null
          ? DateTime.parse(json['cancelledAt'])
          : null,
      customerRating: json['customerRating'],
      customerComment: json['customerComment'],
      riderComment: json['riderComment'],
      deliveryType: json['deliveryType'] ?? 'IMMEDIATE',
      priority: json['priority'] ?? 'NORMAL',
      tags: json['tags']?.cast<String>(),
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'businessOrderId': businessOrderId,
      'orderNo': orderNo,
      'merchantId': merchantId,
      'merchantName': merchantName,
      'merchantAddress': merchantAddress,
      'merchantLongitude': merchantLongitude,
      'merchantLatitude': merchantLatitude,
      'merchantPhone': merchantPhone,
      'customerId': customerId,
      'customerName': customerName,
      'customerPhone': customerPhone,
      'deliveryAddress': deliveryAddress,
      'deliveryLongitude': deliveryLongitude,
      'deliveryLatitude': deliveryLatitude,
      'addressDetail': addressDetail,
      'orderAmount': orderAmount,
      'deliveryFee': deliveryFee,
      'goodsSummary': goodsSummary,
      'goodsCount': goodsCount,
      'weight': weight,
      'remark': remark,
      'status': status,
      'riderId': riderId,
      'riderName': riderName,
      'riderPhone': riderPhone,
      'assignedAt': assignedAt?.toIso8601String(),
      'acceptedAt': acceptedAt?.toIso8601String(),
      'arrivedAt': arrivedAt?.toIso8601String(),
      'pickedAt': pickedAt?.toIso8601String(),
      'deliveredAt': deliveredAt?.toIso8601String(),
      'completedAt': completedAt?.toIso8601String(),
      'estimatedDeliveryTime': estimatedDeliveryTime?.toIso8601String(),
      'requiredDeliveryTime': requiredDeliveryTime?.toIso8601String(),
      'deliveryDistance': deliveryDistance,
      'actualDeliveryMinutes': actualDeliveryMinutes,
      'zoneId': zoneId,
      'deliveryPath': deliveryPath,
      'exceptionFlag': exceptionFlag,
      'exceptionReason': exceptionReason,
      'cancelReason': cancelReason,
      'cancelledBy': cancelledBy,
      'cancelledAt': cancelledAt?.toIso8601String(),
      'customerRating': customerRating,
      'customerComment': customerComment,
      'riderComment': riderComment,
      'deliveryType': deliveryType,
      'priority': priority,
      'tags': tags,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
    };
  }

  /// 获取状态显示文本
  String get statusText {
    switch (status) {
      case 'WAITING':
        return '待分配';
      case 'ASSIGNED':
        return '已分配';
      case 'PICKING':
        return '取货中';
      case 'DELIVERING':
        return '配送中';
      case 'ARRIVED':
        return '已送达';
      case 'COMPLETED':
        return '已完成';
      case 'CANCELLED':
        return '已取消';
      default:
        return '未知';
    }
  }

  /// 获取进度百分比
  int get progressPercentage {
    switch (status) {
      case 'WAITING':
        return 0;
      case 'ASSIGNED':
        return 20;
      case 'PICKING':
        return 40;
      case 'DELIVERING':
        return 70;
      case 'ARRIVED':
        return 90;
      case 'COMPLETED':
        return 100;
      default:
        return 0;
    }
  }

  /// 检查是否可取消
  bool get isCancellable => ['WAITING', 'ASSIGNED'].contains(status);

  /// 检查是否已分配骑手
  bool get hasRider => riderId != null;

  /// 检查是否延误
  bool get isDelayed {
    if (estimatedDeliveryTime == null) return false;
    return DateTime.now().isAfter(estimatedDeliveryTime!) &&
        status != 'COMPLETED' &&
        status != 'CANCELLED';
  }

  /// 获取剩余预计时间文本
  String? get remainingTimeText {
    if (estimatedDeliveryTime == null) return null;
    final remaining = estimatedDeliveryTime!.difference(DateTime.now());
    if (remaining.isNegative) return '已超时';
    final minutes = remaining.inMinutes;
    if (minutes < 1) return '即将送达';
    if (minutes < 60) return '约${minutes}分钟';
    final hours = remaining.inHours;
    return '约${hours}小时${minutes % 60}分钟';
  }

  /// 复制并修改
  DeliveryOrderModel copyWith({
    int? id,
    int? businessOrderId,
    String? orderNo,
    int? merchantId,
    String? merchantName,
    String? merchantAddress,
    double? merchantLongitude,
    double? merchantLatitude,
    String? merchantPhone,
    int? customerId,
    String? customerName,
    String? customerPhone,
    String? deliveryAddress,
    double? deliveryLongitude,
    double? deliveryLatitude,
    String? addressDetail,
    double? orderAmount,
    double? deliveryFee,
    String? goodsSummary,
    int? goodsCount,
    double? weight,
    String? remark,
    String? status,
    int? riderId,
    String? riderName,
    String? riderPhone,
    DateTime? assignedAt,
    DateTime? acceptedAt,
    DateTime? arrivedAt,
    DateTime? pickedAt,
    DateTime? deliveredAt,
    DateTime? completedAt,
    DateTime? estimatedDeliveryTime,
    DateTime? requiredDeliveryTime,
    int? deliveryDistance,
    int? actualDeliveryMinutes,
    int? zoneId,
    String? deliveryPath,
    String? exceptionFlag,
    String? exceptionReason,
    String? cancelReason,
    String? cancelledBy,
    DateTime? cancelledAt,
    int? customerRating,
    String? customerComment,
    String? riderComment,
    String? deliveryType,
    String? priority,
    List<String>? tags,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return DeliveryOrderModel(
      id: id ?? this.id,
      businessOrderId: businessOrderId ?? this.businessOrderId,
      orderNo: orderNo ?? this.orderNo,
      merchantId: merchantId ?? this.merchantId,
      merchantName: merchantName ?? this.merchantName,
      merchantAddress: merchantAddress ?? this.merchantAddress,
      merchantLongitude: merchantLongitude ?? this.merchantLongitude,
      merchantLatitude: merchantLatitude ?? this.merchantLatitude,
      merchantPhone: merchantPhone ?? this.merchantPhone,
      customerId: customerId ?? this.customerId,
      customerName: customerName ?? this.customerName,
      customerPhone: customerPhone ?? this.customerPhone,
      deliveryAddress: deliveryAddress ?? this.deliveryAddress,
      deliveryLongitude: deliveryLongitude ?? this.deliveryLongitude,
      deliveryLatitude: deliveryLatitude ?? this.deliveryLatitude,
      addressDetail: addressDetail ?? this.addressDetail,
      orderAmount: orderAmount ?? this.orderAmount,
      deliveryFee: deliveryFee ?? this.deliveryFee,
      goodsSummary: goodsSummary ?? this.goodsSummary,
      goodsCount: goodsCount ?? this.goodsCount,
      weight: weight ?? this.weight,
      remark: remark ?? this.remark,
      status: status ?? this.status,
      riderId: riderId ?? this.riderId,
      riderName: riderName ?? this.riderName,
      riderPhone: riderPhone ?? this.riderPhone,
      assignedAt: assignedAt ?? this.assignedAt,
      acceptedAt: acceptedAt ?? this.acceptedAt,
      arrivedAt: arrivedAt ?? this.arrivedAt,
      pickedAt: pickedAt ?? this.pickedAt,
      deliveredAt: deliveredAt ?? this.deliveredAt,
      completedAt: completedAt ?? this.completedAt,
      estimatedDeliveryTime: estimatedDeliveryTime ?? this.estimatedDeliveryTime,
      requiredDeliveryTime: requiredDeliveryTime ?? this.requiredDeliveryTime,
      deliveryDistance: deliveryDistance ?? this.deliveryDistance,
      actualDeliveryMinutes: actualDeliveryMinutes ?? this.actualDeliveryMinutes,
      zoneId: zoneId ?? this.zoneId,
      deliveryPath: deliveryPath ?? this.deliveryPath,
      exceptionFlag: exceptionFlag ?? this.exceptionFlag,
      exceptionReason: exceptionReason ?? this.exceptionReason,
      cancelReason: cancelReason ?? this.cancelReason,
      cancelledBy: cancelledBy ?? this.cancelledBy,
      cancelledAt: cancelledAt ?? this.cancelledAt,
      customerRating: customerRating ?? this.customerRating,
      customerComment: customerComment ?? this.customerComment,
      riderComment: riderComment ?? this.riderComment,
      deliveryType: deliveryType ?? this.deliveryType,
      priority: priority ?? this.priority,
      tags: tags ?? this.tags,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }
}

/// 配送状态枚举
enum DeliveryStatus {
  waiting('WAITING', '待分配', 0xFF9E9E9E),
  assigned('ASSIGNED', '已分配', 0xFF2196F3),
  picking('PICKING', '取货中', 0xFFFF9800),
  delivering('DELIVERING', '配送中', 0xFF4CAF50),
  arrived('ARRIVED', '已送达', 0xFF9C27B0),
  completed('COMPLETED', '已完成', 0xFF00BCD4),
  cancelled('CANCELLED', '已取消', 0xFFF44336);

  final String code;
  final String name;
  final int color;

  const DeliveryStatus(this.code, this.name, this.color);

  static DeliveryStatus fromCode(String code) {
    return DeliveryStatus.values.firstWhere(
      (e) => e.code == code,
      orElse: () => DeliveryStatus.waiting,
    );
  }
}
