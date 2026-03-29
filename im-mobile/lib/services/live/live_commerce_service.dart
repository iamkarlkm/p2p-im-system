import 'dart:convert';
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import '../../models/live/live_product_model.dart';
import '../../models/live/live_room_model.dart';
import '../../models/order/order_model.dart';
import '../../utils/logger.dart';
import '../../config/api_config.dart';

/// 直播电商服务
/// 提供直播带货商品管理、下单支付、售后等本地电商闭环功能
class LiveCommerceService extends ChangeNotifier {
  static final LiveCommerceService _instance = LiveCommerceService._internal();
  factory LiveCommerceService() => _instance;
  LiveCommerceService._internal();

  // 当前直播间购物车
  final Map<String, List<CartItem>> _roomCarts = {};
  
  // 当前直播间订单
  final Map<String, List<LiveOrderModel>> _roomOrders = {};

  // 秒杀活动缓存
  final Map<String, LiveFlashSale> _flashSales = {};

  // 优惠券缓存
  final Map<String, LiveCoupon> _coupons = {};

  // 红包缓存
  final Map<String, LiveRedPacket> _redPackets = {};

  // 错误信息
  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  // 加载状态
  bool _isLoading = false;
  bool get isLoading => _isLoading;

  /// 获取直播间购物车
  List<CartItem> getCartItems(String roomId) {
    return List.unmodifiable(_roomCarts[roomId] ?? []);
  }

  /// 获取购物车商品数量
  int getCartItemCount(String roomId) {
    return _roomCarts[roomId]?.fold(0, (sum, item) => sum! + item.quantity) ?? 0;
  }

  /// 获取购物车总金额
  double getCartTotalAmount(String roomId) {
    return _roomCarts[roomId]?.fold(
      0.0, 
      (sum, item) => sum! + (item.product.price * item.quantity),
    ) ?? 0.0;
  }

  /// 添加商品到购物车
  Future<void> addToCart(String roomId, LiveProductModel product, {int quantity = 1}) async {
    Logger.log('LiveCommerceService', 'Adding product to cart: ${product.name}, qty: $quantity');
    
    // 检查库存
    if (product.stock < quantity) {
      throw Exception('商品库存不足');
    }

    _roomCarts.putIfAbsent(roomId, () => []);
    
    final cart = _roomCarts[roomId]!;
    final existingIndex = cart.indexWhere((item) => item.product.id == product.id);
    
    if (existingIndex >= 0) {
      // 更新数量
      final existing = cart[existingIndex];
      final newQuantity = existing.quantity + quantity;
      
      if (product.stock < newQuantity) {
        throw Exception('商品库存不足，当前库存: ${product.stock}');
      }
      
      cart[existingIndex] = existing.copyWith(quantity: newQuantity);
    } else {
      // 添加新商品
      cart.add(CartItem(
        product: product,
        quantity: quantity,
        addedAt: DateTime.now(),
      ));
    }
    
    notifyListeners();
    Logger.log('LiveCommerceService', 'Product added to cart successfully');
  }

  /// 从购物车移除商品
  Future<void> removeFromCart(String roomId, String productId) async {
    Logger.log('LiveCommerceService', 'Removing product from cart: $productId');
    
    final cart = _roomCarts[roomId];
    if (cart != null) {
      cart.removeWhere((item) => item.product.id == productId);
      notifyListeners();
    }
  }

  /// 更新购物车商品数量
  Future<void> updateCartQuantity(String roomId, String productId, int quantity) async {
    if (quantity <= 0) {
      await removeFromCart(roomId, productId);
      return;
    }

    final cart = _roomCarts[roomId];
    if (cart == null) return;

    final index = cart.indexWhere((item) => item.product.id == productId);
    if (index >= 0) {
      final item = cart[index];
      
      // 检查库存
      if (item.product.stock < quantity) {
        throw Exception('商品库存不足，当前库存: ${item.product.stock}');
      }
      
      cart[index] = item.copyWith(quantity: quantity);
      notifyListeners();
    }
  }

  /// 清空购物车
  Future<void> clearCart(String roomId) async {
    _roomCarts[roomId]?.clear();
    notifyListeners();
  }

  /// 创建订单（直播间一键下单）
  Future<LiveOrderModel> createOrder({
    required String roomId,
    required String streamerId,
    List<CartItem>? items,
    String? couponId,
    String? remark,
    required String receiverName,
    required String receiverPhone,
    required String receiverAddress,
    String? deliveryType, // express(快递), self_pickup(自提), same_day(同城配送)
    String? pickupPointId, // 自提点ID
  }) async {
    Logger.log('LiveCommerceService', 'Creating order for room: $roomId');
    
    _isLoading = true;
    notifyListeners();

    try {
      final cartItems = items ?? _roomCarts[roomId] ?? [];
      
      if (cartItems.isEmpty) {
        throw Exception('购物车为空');
      }

      final orderItems = cartItems.map((item) => {
        'productId': item.product.id,
        'productName': item.product.name,
        'productImage': item.product.images?.firstOrNull,
        'price': item.product.price,
        'quantity': item.quantity,
        'subtotal': item.product.price * item.quantity,
      }).toList();

      final response = await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/orders'),
        headers: await ApiConfig.getHeaders(),
        body: json.encode({
          'roomId': roomId,
          'streamerId': streamerId,
          'items': orderItems,
          'couponId': couponId,
          'remark': remark,
          'receiverName': receiverName,
          'receiverPhone': receiverPhone,
          'receiverAddress': receiverAddress,
          'deliveryType': deliveryType ?? 'express',
          'pickupPointId': pickupPointId,
        }),
      );

      if (response.statusCode == 201 || response.statusCode == 200) {
        final data = json.decode(response.body);
        final order = LiveOrderModel.fromJson(data);
        
        // 保存订单
        _roomOrders.putIfAbsent(roomId, () => []);
        _roomOrders[roomId]!.add(order);
        
        // 清空购物车
        await clearCart(roomId);
        
        Logger.log('LiveCommerceService', 'Order created: ${order.id}');
        notifyListeners();
        
        return order;
      } else {
        throw Exception('创建订单失败: ${response.statusCode}');
      }
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to create order', e, stackTrace);
      _errorMessage = '创建订单失败: $e';
      notifyListeners();
      rethrow;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// 快速购买（单个商品直接下单）
  Future<LiveOrderModel> quickBuy({
    required String roomId,
    required String streamerId,
    required LiveProductModel product,
    required int quantity,
    String? couponId,
    String? remark,
    required String receiverName,
    required String receiverPhone,
    required String receiverAddress,
    String? deliveryType,
    String? pickupPointId,
  }) async {
    final cartItem = CartItem(
      product: product,
      quantity: quantity,
      addedAt: DateTime.now(),
    );

    return createOrder(
      roomId: roomId,
      streamerId: streamerId,
      items: [cartItem],
      couponId: couponId,
      remark: remark,
      receiverName: receiverName,
      receiverPhone: receiverPhone,
      receiverAddress: receiverAddress,
      deliveryType: deliveryType,
      pickupPointId: pickupPointId,
    );
  }

  /// 支付订单
  Future<bool> payOrder({
    required String orderId,
    required String paymentMethod, // wechat(微信支付), alipay(支付宝), balance(余额)
    String? password, // 余额支付密码
  }) async {
    Logger.log('LiveCommerceService', 'Paying order: $orderId, method: $paymentMethod');
    
    _isLoading = true;
    notifyListeners();

    try {
      final response = await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/orders/$orderId/pay'),
        headers: await ApiConfig.getHeaders(),
        body: json.encode({
          'paymentMethod': paymentMethod,
          'password': password,
        }),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        
        // 处理不同支付方式
        if (paymentMethod == 'wechat' || paymentMethod == 'alipay') {
          // 返回支付参数，调起原生支付
          final payParams = data['payParams'];
          // TODO: 调用原生支付SDK
          Logger.log('LiveCommerceService', 'Third-party payment params received');
        }
        
        Logger.log('LiveCommerceService', 'Order paid successfully: $orderId');
        notifyListeners();
        return true;
      } else {
        throw Exception('支付失败: ${response.statusCode}');
      }
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to pay order', e, stackTrace);
      _errorMessage = '支付失败: $e';
      notifyListeners();
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// 获取订单详情
  Future<LiveOrderModel?> getOrderDetail(String orderId) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/orders/$orderId'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return LiveOrderModel.fromJson(data);
      }
      return null;
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to get order detail', e, stackTrace);
      return null;
    }
  }

  /// 获取直播间订单列表
  Future<List<LiveOrderModel>> getRoomOrders(String roomId) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/orders'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final orders = (data['orders'] as List)
            .map((o) => LiveOrderModel.fromJson(o))
            .toList();
        
        _roomOrders[roomId] = orders;
        notifyListeners();
        return orders;
      }
      return [];
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to get room orders', e, stackTrace);
      return [];
    }
  }

  /// 取消订单
  Future<bool> cancelOrder(String orderId, {String? reason}) async {
    try {
      final response = await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/orders/$orderId/cancel'),
        headers: await ApiConfig.getHeaders(),
        body: json.encode({'reason': reason}),
      );

      if (response.statusCode == 200) {
        Logger.log('LiveCommerceService', 'Order cancelled: $orderId');
        notifyListeners();
        return true;
      }
      return false;
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to cancel order', e, stackTrace);
      return false;
    }
  }

  /// 申请退款
  Future<bool> requestRefund({
    required String orderId,
    required String reason,
    String? description,
    List<String>? images,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/orders/$orderId/refund'),
        headers: await ApiConfig.getHeaders(),
        body: json.encode({
          'reason': reason,
          'description': description,
          'images': images,
        }),
      );

      if (response.statusCode == 200) {
        Logger.log('LiveCommerceService', 'Refund requested: $orderId');
        notifyListeners();
        return true;
      }
      return false;
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to request refund', e, stackTrace);
      return false;
    }
  }

  /// 获取可用优惠券
  Future<List<LiveCoupon>> getAvailableCoupons(String roomId) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/coupons'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final coupons = (data['coupons'] as List)
            .map((c) => LiveCoupon.fromJson(c))
            .toList();
        
        // 缓存优惠券
        for (final coupon in coupons) {
          _coupons[coupon.id] = coupon;
        }
        
        return coupons;
      }
      return [];
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to get coupons', e, stackTrace);
      return [];
    }
  }

  /// 领取优惠券
  Future<bool> claimCoupon(String couponId) async {
    try {
      final response = await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/coupons/$couponId/claim'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        Logger.log('LiveCommerceService', 'Coupon claimed: $couponId');
        notifyListeners();
        return true;
      }
      return false;
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to claim coupon', e, stackTrace);
      return false;
    }
  }

  /// 抢红包
  Future<double?> grabRedPacket({
    required String roomId,
    required String redPacketId,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/red-packets/$redPacketId/grab'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final amount = (data['amount'] as num).toDouble();
        Logger.log('LiveCommerceService', 'Red packet grabbed: $amount');
        return amount;
      }
      return null;
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to grab red packet', e, stackTrace);
      return null;
    }
  }

  /// 获取秒杀活动
  Future<LiveFlashSale?> getFlashSale(String roomId) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/flash-sale'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final flashSale = LiveFlashSale.fromJson(data);
        _flashSales[flashSale.id] = flashSale;
        return flashSale;
      }
      return null;
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to get flash sale', e, stackTrace);
      return null;
    }
  }

  /// 参与秒杀
  Future<bool> joinFlashSale({
    required String flashSaleId,
    required String productId,
  }) async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/flash-sales/$flashSaleId/join'),
        headers: await ApiConfig.getHeaders(),
        body: json.encode({'productId': productId}),
      );

      if (response.statusCode == 200) {
        Logger.log('LiveCommerceService', 'Joined flash sale: $flashSaleId');
        return true;
      }
      return false;
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to join flash sale', e, stackTrace);
      return false;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// 获取自提点列表
  Future<List<PickupPoint>> getPickupPoints(String roomId) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/pickup-points'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return (data['points'] as List)
            .map((p) => PickupPoint.fromJson(p))
            .toList();
      }
      return [];
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to get pickup points', e, stackTrace);
      return [];
    }
  }

  /// 获取配送信息
  Future<DeliveryInfo?> getDeliveryInfo(String orderId) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/orders/$orderId/delivery'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return DeliveryInfo.fromJson(data);
      }
      return null;
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to get delivery info', e, stackTrace);
      return null;
    }
  }

  /// 确认收货
  Future<bool> confirmReceipt(String orderId) async {
    try {
      final response = await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/orders/$orderId/confirm'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        Logger.log('LiveCommerceService', 'Order confirmed: $orderId');
        notifyListeners();
        return true;
      }
      return false;
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to confirm receipt', e, stackTrace);
      return false;
    }
  }

  /// 评价订单
  Future<bool> reviewOrder({
    required String orderId,
    required int rating,
    String? content,
    List<String>? images,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/orders/$orderId/review'),
        headers: await ApiConfig.getHeaders(),
        body: json.encode({
          'rating': rating,
          'content': content,
          'images': images,
        }),
      );

      if (response.statusCode == 200) {
        Logger.log('LiveCommerceService', 'Order reviewed: $orderId');
        notifyListeners();
        return true;
      }
      return false;
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to review order', e, stackTrace);
      return false;
    }
  }

  /// 获取直播销售统计（主播端）
  Future<LiveSalesStatistics> getSalesStatistics(String roomId) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/sales-statistics'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return LiveSalesStatistics.fromJson(data);
      }
      return LiveSalesStatistics.empty();
    } catch (e, stackTrace) {
      Logger.error('LiveCommerceService', 'Failed to get sales statistics', e, stackTrace);
      return LiveSalesStatistics.empty();
    }
  }

  /// 清除错误信息
  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }
}

/// 购物车项
class CartItem {
  final LiveProductModel product;
  final int quantity;
  final DateTime addedAt;

  CartItem({
    required this.product,
    required this.quantity,
    required this.addedAt,
  });

  double get subtotal => product.price * quantity;

  CartItem copyWith({
    LiveProductModel? product,
    int? quantity,
    DateTime? addedAt,
  }) {
    return CartItem(
      product: product ?? this.product,
      quantity: quantity ?? this.quantity,
      addedAt: addedAt ?? this.addedAt,
    );
  }
}

/// 直播订单模型
class LiveOrderModel {
  final String id;
  final String roomId;
  final String streamerId;
  final String userId;
  final List<OrderItem> items;
  final double totalAmount;
  final double discountAmount;
  final double payableAmount;
  final String? couponId;
  final String status; // pending(待支付), paid(已支付), shipped(已发货), completed(已完成), cancelled(已取消), refunding(退款中)
  final String? remark;
  final String receiverName;
  final String receiverPhone;
  final String receiverAddress;
  final String deliveryType;
  final String? pickupPointId;
  final DateTime createdAt;
  final DateTime updatedAt;

  LiveOrderModel({
    required this.id,
    required this.roomId,
    required this.streamerId,
    required this.userId,
    required this.items,
    required this.totalAmount,
    this.discountAmount = 0.0,
    required this.payableAmount,
    this.couponId,
    required this.status,
    this.remark,
    required this.receiverName,
    required this.receiverPhone,
    required this.receiverAddress,
    required this.deliveryType,
    this.pickupPointId,
    required this.createdAt,
    required this.updatedAt,
  });

  factory LiveOrderModel.fromJson(Map<String, dynamic> json) {
    return LiveOrderModel(
      id: json['id'] ?? '',
      roomId: json['roomId'] ?? '',
      streamerId: json['streamerId'] ?? '',
      userId: json['userId'] ?? '',
      items: (json['items'] as List?)
          ?.map((i) => OrderItem.fromJson(i))
          .toList() ?? [],
      totalAmount: (json['totalAmount'] ?? 0.0).toDouble(),
      discountAmount: (json['discountAmount'] ?? 0.0).toDouble(),
      payableAmount: (json['payableAmount'] ?? 0.0).toDouble(),
      couponId: json['couponId'],
      status: json['status'] ?? 'pending',
      remark: json['remark'],
      receiverName: json['receiverName'] ?? '',
      receiverPhone: json['receiverPhone'] ?? '',
      receiverAddress: json['receiverAddress'] ?? '',
      deliveryType: json['deliveryType'] ?? 'express',
      pickupPointId: json['pickupPointId'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'roomId': roomId,
    'streamerId': streamerId,
    'userId': userId,
    'items': items.map((i) => i.toJson()).toList(),
    'totalAmount': totalAmount,
    'discountAmount': discountAmount,
    'payableAmount': payableAmount,
    'couponId': couponId,
    'status': status,
    'remark': remark,
    'receiverName': receiverName,
    'receiverPhone': receiverPhone,
    'receiverAddress': receiverAddress,
    'deliveryType': deliveryType,
    'pickupPointId': pickupPointId,
    'createdAt': createdAt.toIso8601String(),
    'updatedAt': updatedAt.toIso8601String(),
  };

  /// 是否待支付
  bool get isPending => status == 'pending';

  /// 是否已支付
  bool get isPaid => status == 'paid';

  /// 是否已完成
  bool get isCompleted => status == 'completed';
}

/// 订单项
class OrderItem {
  final String productId;
  final String productName;
  final String? productImage;
  final double price;
  final int quantity;
  final double subtotal;

  OrderItem({
    required this.productId,
    required this.productName,
    this.productImage,
    required this.price,
    required this.quantity,
    required this.subtotal,
  });

  factory OrderItem.fromJson(Map<String, dynamic> json) {
    return OrderItem(
      productId: json['productId'] ?? '',
      productName: json['productName'] ?? '',
      productImage: json['productImage'],
      price: (json['price'] ?? 0.0).toDouble(),
      quantity: json['quantity'] ?? 0,
      subtotal: (json['subtotal'] ?? 0.0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'productId': productId,
    'productName': productName,
    'productImage': productImage,
    'price': price,
    'quantity': quantity,
    'subtotal': subtotal,
  };
}

/// 直播优惠券
class LiveCoupon {
  final String id;
  final String roomId;
  final String name;
  final String type; // discount(折扣券), reduction(满减券)
  final double? discount; // 折扣率 (0.8 = 8折)
  final double? minAmount; // 最低消费
  final double? reductionAmount; // 减免金额
  final DateTime validFrom;
  final DateTime validUntil;
  final int totalCount;
  final int claimedCount;
  final bool isClaimed;

  LiveCoupon({
    required this.id,
    required this.roomId,
    required this.name,
    required this.type,
    this.discount,
    this.minAmount,
    this.reductionAmount,
    required this.validFrom,
    required this.validUntil,
    required this.totalCount,
    this.claimedCount = 0,
    this.isClaimed = false,
  });

  factory LiveCoupon.fromJson(Map<String, dynamic> json) {
    return LiveCoupon(
      id: json['id'] ?? '',
      roomId: json['roomId'] ?? '',
      name: json['name'] ?? '',
      type: json['type'] ?? 'reduction',
      discount: json['discount'] != null ? (json['discount'] as num).toDouble() : null,
      minAmount: json['minAmount'] != null ? (json['minAmount'] as num).toDouble() : null,
      reductionAmount: json['reductionAmount'] != null ? (json['reductionAmount'] as num).toDouble() : null,
      validFrom: DateTime.parse(json['validFrom']),
      validUntil: DateTime.parse(json['validUntil']),
      totalCount: json['totalCount'] ?? 0,
      claimedCount: json['claimedCount'] ?? 0,
      isClaimed: json['isClaimed'] ?? false,
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'roomId': roomId,
    'name': name,
    'type': type,
    'discount': discount,
    'minAmount': minAmount,
    'reductionAmount': reductionAmount,
    'validFrom': validFrom.toIso8601String(),
    'validUntil': validUntil.toIso8601String(),
    'totalCount': totalCount,
    'claimedCount': claimedCount,
    'isClaimed': isClaimed,
  };

  /// 计算优惠后金额
  double calculateDiscount(double amount) {
    if (minAmount != null && amount < minAmount!) {
      return 0.0;
    }

    if (type == 'discount' && discount != null) {
      return amount * (1 - discount!);
    } else if (type == 'reduction' && reductionAmount != null) {
      return reductionAmount!;
    }

    return 0.0;
  }

  /// 是否已领完
  bool get isExhausted => claimedCount >= totalCount;

  /// 是否已过期
  bool get isExpired => DateTime.now().isAfter(validUntil);

  /// 是否有效
  bool get isValid => !isExhausted && !isExpired;
}

/// 直播红包
class LiveRedPacket {
  final String id;
  final String roomId;
  final String senderId;
  final String senderName;
  final double totalAmount;
  final int totalCount;
  final int grabbedCount;
  final String type; // random(随机), fixed(固定)
  final String? message;
  final DateTime createdAt;

  LiveRedPacket({
    required this.id,
    required this.roomId,
    required this.senderId,
    required this.senderName,
    required this.totalAmount,
    required this.totalCount,
    this.grabbedCount = 0,
    required this.type,
    this.message,
    required this.createdAt,
  });

  factory LiveRedPacket.fromJson(Map<String, dynamic> json) {
    return LiveRedPacket(
      id: json['id'] ?? '',
      roomId: json['roomId'] ?? '',
      senderId: json['senderId'] ?? '',
      senderName: json['senderName'] ?? '',
      totalAmount: (json['totalAmount'] ?? 0.0).toDouble(),
      totalCount: json['totalCount'] ?? 0,
      grabbedCount: json['grabbedCount'] ?? 0,
      type: json['type'] ?? 'random',
      message: json['message'],
      createdAt: DateTime.parse(json['createdAt']),
    );
  }

  /// 是否已抢完
  bool get isExhausted => grabbedCount >= totalCount;
}

/// 秒杀活动
class LiveFlashSale {
  final String id;
  final String roomId;
  final List<FlashSaleItem> items;
  final DateTime startTime;
  final DateTime endTime;
  final String status; // upcoming(即将开始), active(进行中), ended(已结束)

  LiveFlashSale({
    required this.id,
    required this.roomId,
    required this.items,
    required this.startTime,
    required this.endTime,
    required this.status,
  });

  factory LiveFlashSale.fromJson(Map<String, dynamic> json) {
    return LiveFlashSale(
      id: json['id'] ?? '',
      roomId: json['roomId'] ?? '',
      items: (json['items'] as List?)
          ?.map((i) => FlashSaleItem.fromJson(i))
          .toList() ?? [],
      startTime: DateTime.parse(json['startTime']),
      endTime: DateTime.parse(json['endTime']),
      status: json['status'] ?? 'upcoming',
    );
  }

  /// 是否进行中
  bool get isActive => status == 'active';

  /// 获取倒计时
  String get countdownText {
    final now = DateTime.now();
    
    if (status == 'ended') {
      return '已结束';
    }
    
    final targetTime = status == 'upcoming' ? startTime : endTime;
    final diff = targetTime.difference(now);
    
    if (diff.isNegative) {
      return status == 'upcoming' ? '即将开始' : '已结束';
    }
    
    if (diff.inHours > 0) {
      return '${diff.inHours}:${(diff.inMinutes % 60).toString().padLeft(2, '0')}:${(diff.inSeconds % 60).toString().padLeft(2, '0')}';
    } else {
      return '${diff.inMinutes}:${(diff.inSeconds % 60).toString().padLeft(2, '0')}';
    }
  }
}

/// 秒杀项
class FlashSaleItem {
  final String productId;
  final String productName;
  final String? productImage;
  final double originalPrice;
  final double salePrice;
  final int totalStock;
  final int soldStock;

  FlashSaleItem({
    required this.productId,
    required this.productName,
    this.productImage,
    required this.originalPrice,
    required this.salePrice,
    required this.totalStock,
    this.soldStock = 0,
  });

  factory FlashSaleItem.fromJson(Map<String, dynamic> json) {
    return FlashSaleItem(
      productId: json['productId'] ?? '',
      productName: json['productName'] ?? '',
      productImage: json['productImage'],
      originalPrice: (json['originalPrice'] ?? 0.0).toDouble(),
      salePrice: (json['salePrice'] ?? 0.0).toDouble(),
      totalStock: json['totalStock'] ?? 0,
      soldStock: json['soldStock'] ?? 0,
    );
  }

  /// 折扣
  double get discount => (salePrice / originalPrice * 10).clamp(0.0, 10.0);

  /// 剩余库存
  int get remainingStock => totalStock - soldStock;

  /// 已售百分比
  double get soldPercentage => (soldStock / totalStock * 100).clamp(0.0, 100.0);

  /// 是否已售罄
  bool get isSoldOut => remainingStock <= 0;
}

/// 自提点
class PickupPoint {
  final String id;
  final String name;
  final String address;
  final double? latitude;
  final double? longitude;
  final String? phone;
  final String? businessHours;

  PickupPoint({
    required this.id,
    required this.name,
    required this.address,
    this.latitude,
    this.longitude,
    this.phone,
    this.businessHours,
  });

  factory PickupPoint.fromJson(Map<String, dynamic> json) {
    return PickupPoint(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      address: json['address'] ?? '',
      latitude: json['latitude'] != null ? (json['latitude'] as num).toDouble() : null,
      longitude: json['longitude'] != null ? (json['longitude'] as num).toDouble() : null,
      phone: json['phone'],
      businessHours: json['businessHours'],
    );
  }
}

/// 配送信息
class DeliveryInfo {
  final String orderId;
  final String deliveryType;
  final String? trackingNumber;
  final String? carrierName;
  final List<DeliveryTrack>? tracks;
  final String? riderName;
  final String? riderPhone;
  final double? riderLatitude;
  final double? riderLongitude;
  final DateTime? estimatedArrival;

  DeliveryInfo({
    required this.orderId,
    required this.deliveryType,
    this.trackingNumber,
    this.carrierName,
    this.tracks,
    this.riderName,
    this.riderPhone,
    this.riderLatitude,
    this.riderLongitude,
    this.estimatedArrival,
  });

  factory DeliveryInfo.fromJson(Map<String, dynamic> json) {
    return DeliveryInfo(
      orderId: json['orderId'] ?? '',
      deliveryType: json['deliveryType'] ?? 'express',
      trackingNumber: json['trackingNumber'],
      carrierName: json['carrierName'],
      tracks: (json['tracks'] as List?)
          ?.map((t) => DeliveryTrack.fromJson(t))
          .toList(),
      riderName: json['riderName'],
      riderPhone: json['riderPhone'],
      riderLatitude: json['riderLatitude'] != null 
          ? (json['riderLatitude'] as num).toDouble() 
          : null,
      riderLongitude: json['riderLongitude'] != null 
          ? (json['riderLongitude'] as num).toDouble() 
          : null,
      estimatedArrival: json['estimatedArrival'] != null 
          ? DateTime.parse(json['estimatedArrival']) 
          : null,
    );
  }
}

/// 配送轨迹
class DeliveryTrack {
  final String status;
  final String description;
  final DateTime time;

  DeliveryTrack({
    required this.status,
    required this.description,
    required this.time,
  });

  factory DeliveryTrack.fromJson(Map<String, dynamic> json) {
    return DeliveryTrack(
      status: json['status'] ?? '',
      description: json['description'] ?? '',
      time: DateTime.parse(json['time']),
    );
  }
}

/// 直播销售统计
class LiveSalesStatistics {
  final String roomId;
  final int totalOrders;
  final int paidOrders;
  final double totalSales;
  final int totalProducts;
  final int soldProducts;
  final Map<String, int> productSales;
  final List<ProductRankingItem> productRanking;

  LiveSalesStatistics({
    required this.roomId,
    required this.totalOrders,
    required this.paidOrders,
    required this.totalSales,
    required this.totalProducts,
    required this.soldProducts,
    required this.productSales,
    required this.productRanking,
  });

  factory LiveSalesStatistics.fromJson(Map<String, dynamic> json) {
    return LiveSalesStatistics(
      roomId: json['roomId'] ?? '',
      totalOrders: json['totalOrders'] ?? 0,
      paidOrders: json['paidOrders'] ?? 0,
      totalSales: (json['totalSales'] ?? 0.0).toDouble(),
      totalProducts: json['totalProducts'] ?? 0,
      soldProducts: json['soldProducts'] ?? 0,
      productSales: Map<String, int>.from(json['productSales'] ?? {}),
      productRanking: (json['productRanking'] as List?)
          ?.map((r) => ProductRankingItem.fromJson(r))
          .toList() ?? [],
    );
  }

  factory LiveSalesStatistics.empty() => LiveSalesStatistics(
    roomId: '',
    totalOrders: 0,
    paidOrders: 0,
    totalSales: 0.0,
    totalProducts: 0,
    soldProducts: 0,
    productSales: {},
    productRanking: [],
  );

  /// 支付率
  double get paymentRate => totalOrders > 0 ? paidOrders / totalOrders : 0.0;

  /// 客单价
  double get averageOrderValue => paidOrders > 0 ? totalSales / paidOrders : 0.0;
}

/// 商品排行项
class ProductRankingItem {
  final String productId;
  final String productName;
  final int salesCount;
  final double salesAmount;

  ProductRankingItem({
    required this.productId,
    required this.productName,
    required this.salesCount,
    required this.salesAmount,
  });

  factory ProductRankingItem.fromJson(Map<String, dynamic> json) {
    return ProductRankingItem(
      productId: json['productId'] ?? '',
      productName: json['productName'] ?? '',
      salesCount: json['salesCount'] ?? 0,
      salesAmount: (json['salesAmount'] ?? 0.0).toDouble(),
    );
  }
}
