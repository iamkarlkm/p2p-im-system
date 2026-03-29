/// 小程序直播与本地电商 - 订单模型
/// 
/// 作者: IM Development Team
/// 创建时间: 2026-03-28

/// 订单状态枚举
enum LiveOrderStatus {
  pendingPayment,   // 待付款
  paid,             // 已付款
  shipped,          // 已发货
  received,         // 已收货
  completed,        // 已完成
  cancelled,        // 已取消
  refunding,        // 退款中
  refunded,         // 已退款
}

/// 配送方式枚举
enum DeliveryType {
  express,          // 快递
  sameDay,          // 同城配送
  selfPickup,       // 到店自提
}

/// 直播订单模型
class LiveOrder {
  final String orderId;
  final String orderNo;
  final String roomId;
  final String? roomTitle;
  final String anchorId;
  final String? anchorNickname;
  final LiveOrderStatus status;
  final double productAmount;
  final double freightAmount;
  final double discountAmount;
  final double payAmount;
  final int? payType;
  final DateTime? payTime;
  final String receiverName;
  final String receiverPhone;
  final String receiverAddress;
  final String? receiverDetail;
  final DeliveryType deliveryType;
  final String? pickupCode;
  final DateTime? pickupTime;
  final String? logisticsCompany;
  final String? logisticsNo;
  final DateTime? shipTime;
  final DateTime? receiveTime;
  final DateTime? completeTime;
  final DateTime? cancelTime;
  final String? cancelReason;
  final String? buyerRemark;
  final List<LiveOrderItem> items;
  final DateTime createTime;
  final int? remainingPaySeconds;

  LiveOrder({
    required this.orderId,
    required this.orderNo,
    required this.roomId,
    this.roomTitle,
    required this.anchorId,
    this.anchorNickname,
    required this.status,
    required this.productAmount,
    this.freightAmount = 0,
    this.discountAmount = 0,
    required this.payAmount,
    this.payType,
    this.payTime,
    required this.receiverName,
    required this.receiverPhone,
    required this.receiverAddress,
    this.receiverDetail,
    required this.deliveryType,
    this.pickupCode,
    this.pickupTime,
    this.logisticsCompany,
    this.logisticsNo,
    this.shipTime,
    this.receiveTime,
    this.completeTime,
    this.cancelTime,
    this.cancelReason,
    this.buyerRemark,
    required this.items,
    required this.createTime,
    this.remainingPaySeconds,
  });

  factory LiveOrder.fromJson(Map<String, dynamic> json) => LiveOrder(
    orderId: json['orderId'].toString(),
    orderNo: json['orderNo'] ?? '',
    roomId: json['roomId'].toString(),
    roomTitle: json['roomTitle'],
    anchorId: json['anchorId'].toString(),
    anchorNickname: json['anchorNickname'],
    status: LiveOrderStatus.values.byName(json['status'] ?? 'pendingPayment'),
    productAmount: (json['productAmount'] ?? 0).toDouble(),
    freightAmount: (json['freightAmount'] ?? 0).toDouble(),
    discountAmount: (json['discountAmount'] ?? 0).toDouble(),
    payAmount: (json['payAmount'] ?? 0).toDouble(),
    payType: json['payType'],
    payTime: json['payTime'] != null ? DateTime.parse(json['payTime']) : null,
    receiverName: json['receiverName'] ?? '',
    receiverPhone: json['receiverPhone'] ?? '',
    receiverAddress: json['receiverAddress'] ?? '',
    receiverDetail: json['receiverDetail'],
    deliveryType: DeliveryType.values.byName(json['deliveryType'] ?? 'express'),
    pickupCode: json['pickupCode'],
    pickupTime: json['pickupTime'] != null 
        ? DateTime.parse(json['pickupTime']) : null,
    logisticsCompany: json['logisticsCompany'],
    logisticsNo: json['logisticsNo'],
    shipTime: json['shipTime'] != null 
        ? DateTime.parse(json['shipTime']) : null,
    receiveTime: json['receiveTime'] != null 
        ? DateTime.parse(json['receiveTime']) : null,
    completeTime: json['completeTime'] != null 
        ? DateTime.parse(json['completeTime']) : null,
    cancelTime: json['cancelTime'] != null 
        ? DateTime.parse(json['cancelTime']) : null,
    cancelReason: json['cancelReason'],
    buyerRemark: json['buyerRemark'],
    items: json['items']?.map<LiveOrderItem>(
        (e) => LiveOrderItem.fromJson(e)).toList() ?? [],
    createTime: DateTime.parse(json['createTime'] ?? DateTime.now().toIso8601String()),
    remainingPaySeconds: json['remainingPaySeconds'],
  );

  Map<String, dynamic> toJson() => {
    'orderId': orderId,
    'orderNo': orderNo,
    'roomId': roomId,
    'roomTitle': roomTitle,
    'anchorId': anchorId,
    'anchorNickname': anchorNickname,
    'status': status.name,
    'productAmount': productAmount,
    'freightAmount': freightAmount,
    'discountAmount': discountAmount,
    'payAmount': payAmount,
    'payType': payType,
    'payTime': payTime?.toIso8601String(),
    'receiverName': receiverName,
    'receiverPhone': receiverPhone,
    'receiverAddress': receiverAddress,
    'receiverDetail': receiverDetail,
    'deliveryType': deliveryType.name,
    'pickupCode': pickupCode,
    'pickupTime': pickupTime?.toIso8601String(),
    'logisticsCompany': logisticsCompany,
    'logisticsNo': logisticsNo,
    'shipTime': shipTime?.toIso8601String(),
    'receiveTime': receiveTime?.toIso8601String(),
    'completeTime': completeTime?.toIso8601String(),
    'cancelTime': cancelTime?.toIso8601String(),
    'cancelReason': cancelReason,
    'buyerRemark': buyerRemark,
    'items': items.map((e) => e.toJson()).toList(),
    'createTime': createTime.toIso8601String(),
    'remainingPaySeconds': remainingPaySeconds,
  };

  /// 状态文本
  String get statusText {
    switch (status) {
      case LiveOrderStatus.pendingPayment:
        return '待付款';
      case LiveOrderStatus.paid:
        return '待发货';
      case LiveOrderStatus.shipped:
        return '待收货';
      case LiveOrderStatus.received:
        return '待评价';
      case LiveOrderStatus.completed:
        return '已完成';
      case LiveOrderStatus.cancelled:
        return '已取消';
      case LiveOrderStatus.refunding:
        return '退款中';
      case LiveOrderStatus.refunded:
        return '已退款';
    }
  }

  /// 配送方式文本
  String get deliveryTypeText {
    switch (deliveryType) {
      case DeliveryType.express:
        return '快递配送';
      case DeliveryType.sameDay:
        return '同城配送';
      case DeliveryType.selfPickup:
        return '到店自提';
    }
  }

  /// 支付方式文本
  String get payTypeText {
    switch (payType) {
      case 1:
        return '微信支付';
      case 2:
        return '支付宝';
      case 3:
        return '余额支付';
      default:
        return '';
    }
  }

  /// 是否待付款
  bool get isPendingPayment => status == LiveOrderStatus.pendingPayment;

  /// 是否已付款
  bool get isPaid => status.index >= LiveOrderStatus.paid.index;

  /// 是否已完成
  bool get isCompleted => status == LiveOrderStatus.completed;

  /// 是否已取消
  bool get isCancelled => status == LiveOrderStatus.cancelled;

  /// 是否可以取消
  bool get canCancel => isPendingPayment;

  /// 是否可以申请退款
  bool get canRefund => isPaid && status.index < LiveOrderStatus.completed.index;

  /// 是否可以确认收货
  bool get canReceive => status == LiveOrderStatus.shipped;

  /// 商品总数
  int get totalQuantity => items.fold(0, (sum, item) => sum + item.quantity);
}

/// 订单商品项
class LiveOrderItem {
  final String productId;
  final String productName;
  final String? productImage;
  final String? specification;
  final double price;
  final int quantity;
  final double subtotal;

  LiveOrderItem({
    required this.productId,
    required this.productName,
    this.productImage,
    this.specification,
    required this.price,
    required this.quantity,
    required this.subtotal,
  });

  factory LiveOrderItem.fromJson(Map<String, dynamic> json) => LiveOrderItem(
    productId: json['productId'].toString(),
    productName: json['productName'] ?? '',
    productImage: json['productImage'],
    specification: json['specification'],
    price: (json['price'] ?? 0).toDouble(),
    quantity: json['quantity'] ?? 0,
    subtotal: (json['subtotal'] ?? 0).toDouble(),
  );

  Map<String, dynamic> toJson() => {
    'productId': productId,
    'productName': productName,
    'productImage': productImage,
    'specification': specification,
    'price': price,
    'quantity': quantity,
    'subtotal': subtotal,
  };
}
