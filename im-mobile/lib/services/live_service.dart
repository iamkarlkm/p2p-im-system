/// 小程序直播与本地电商 - 直播服务
/// 
/// 作者: IM Development Team
/// 创建时间: 2026-03-28

import 'package:dio/dio.dart';
import '../models/live_room_models.dart';
import '../models/live_product_models.dart';
import '../models/live_order_models.dart';
import 'api_client.dart';

class LiveService {
  final ApiClient _apiClient;
  
  LiveService({ApiClient? apiClient}) : _apiClient = apiClient ?? ApiClient();

  // ==================== 直播间管理 ====================

  /// 获取直播间列表
  Future<List<LiveRoom>> getLiveRoomList({
    int page = 1,
    int size = 20,
    int? status,
    int? liveType,
    String? keyword,
    double? latitude,
    double? longitude,
  }) async {
    final response = await _apiClient.get('/api/v1/live/rooms', queryParameters: {
      'page': page,
      'size': size,
      if (status != null) 'status': status,
      if (liveType != null) 'liveType': liveType,
      if (keyword != null) 'keyword': keyword,
      if (latitude != null) 'latitude': latitude,
      if (longitude != null) 'longitude': longitude,
    });

    if (response.data['code'] == 200) {
      final List<dynamic> list = response.data['data']['list'] ?? [];
      return list.map((json) => LiveRoom.fromJson(json)).toList();
    }
    throw Exception(response.data['message'] ?? '获取直播间列表失败');
  }

  /// 获取直播间详情
  Future<LiveRoom> getLiveRoomDetail(String roomId) async {
    final response = await _apiClient.get('/api/v1/live/rooms/$roomId');
    
    if (response.data['code'] == 200) {
      return LiveRoom.fromJson(response.data['data']);
    }
    throw Exception(response.data['message'] ?? '获取直播间详情失败');
  }

  /// 获取推荐直播间
  Future<List<LiveRoom>> getRecommendedRooms({int limit = 10}) async {
    final response = await _apiClient.get('/api/v1/live/rooms/recommended', 
        queryParameters: {'limit': limit});

    if (response.data['code'] == 200) {
      final List<dynamic> list = response.data['data'] ?? [];
      return list.map((json) => LiveRoom.fromJson(json)).toList();
    }
    throw Exception(response.data['message'] ?? '获取推荐直播间失败');
  }

  /// 获取附近直播间
  Future<List<LiveRoom>> getNearbyRooms({
    required double latitude,
    required double longitude,
    int radius = 5000,
    int limit = 20,
  }) async {
    final response = await _apiClient.get('/api/v1/live/rooms/nearby', 
        queryParameters: {
          'latitude': latitude,
          'longitude': longitude,
          'radius': radius,
          'limit': limit,
        });

    if (response.data['code'] == 200) {
      final List<dynamic> list = response.data['data'] ?? [];
      return list.map((json) => LiveRoom.fromJson(json)).toList();
    }
    throw Exception(response.data['message'] ?? '获取附近直播间失败');
  }

  /// 进入直播间
  Future<void> enterRoom(String roomId) async {
    final response = await _apiClient.post('/api/v1/live/rooms/$roomId/enter');
    
    if (response.data['code'] != 200) {
      throw Exception(response.data['message'] ?? '进入直播间失败');
    }
  }

  /// 离开直播间
  Future<void> leaveRoom(String roomId) async {
    final response = await _apiClient.post('/api/v1/live/rooms/$roomId/leave');
    
    if (response.data['code'] != 200) {
      throw Exception(response.data['message'] ?? '离开直播间失败');
    }
  }

  /// 发送弹幕/评论
  Future<LiveComment> sendComment(String roomId, String content) async {
    final response = await _apiClient.post(
        '/api/v1/live/rooms/$roomId/comments',
        queryParameters: {'content': content});

    if (response.data['code'] == 200) {
      return LiveComment.fromJson(response.data['data']);
    }
    throw Exception(response.data['message'] ?? '发送评论失败');
  }

  /// 点赞
  Future<void> likeLive(String roomId, {int count = 1}) async {
    final response = await _apiClient.post(
        '/api/v1/live/rooms/$roomId/like',
        queryParameters: {'count': count});

    if (response.data['code'] != 200) {
      throw Exception(response.data['message'] ?? '点赞失败');
    }
  }

  /// 分享直播间
  Future<void> shareLive(String roomId) async {
    final response = await _apiClient.post('/api/v1/live/rooms/$roomId/share');
    
    if (response.data['code'] != 200) {
      throw Exception(response.data['message'] ?? '分享失败');
    }
  }

  // ==================== 商品管理 ====================

  /// 获取直播间商品列表
  Future<List<LiveProduct>> getRoomProducts(String roomId, {int? status}) async {
    final response = await _apiClient.get(
        '/api/v1/live/rooms/$roomId/products',
        queryParameters: status != null ? {'status': status} : null);

    if (response.data['code'] == 200) {
      final List<dynamic> list = response.data['data'] ?? [];
      return list.map((json) => LiveProduct.fromJson(json)).toList();
    }
    throw Exception(response.data['message'] ?? '获取商品列表失败');
  }

  /// 获取商品详情
  Future<LiveProduct> getProductDetail(String productId) async {
    final response = await _apiClient.get('/api/v1/live/products/$productId');
    
    if (response.data['code'] == 200) {
      return LiveProduct.fromJson(response.data['data']);
    }
    throw Exception(response.data['message'] ?? '获取商品详情失败');
  }

  // ==================== 订单管理 ====================

  /// 创建订单
  Future<Map<String, dynamic>> createOrder({
    required String roomId,
    required List<Map<String, dynamic>> items,
    required String receiverName,
    required String receiverPhone,
    required String receiverAddress,
    required DeliveryType deliveryType,
    String? receiverDetail,
    double? latitude,
    double? longitude,
    String? buyerRemark,
    String? couponId,
  }) async {
    final response = await _apiClient.post('/api/v1/live/orders', data: {
      'roomId': roomId,
      'items': items,
      'receiverName': receiverName,
      'receiverPhone': receiverPhone,
      'receiverAddress': receiverAddress,
      'deliveryType': deliveryType.index + 1,
      if (receiverDetail != null) 'receiverDetail': receiverDetail,
      if (latitude != null) 'latitude': latitude,
      if (longitude != null) 'longitude': longitude,
      if (buyerRemark != null) 'buyerRemark': buyerRemark,
      if (couponId != null) 'couponId': couponId,
    });

    if (response.data['code'] == 200) {
      return response.data['data'];
    }
    throw Exception(response.data['message'] ?? '创建订单失败');
  }

  /// 获取订单详情
  Future<LiveOrder> getOrderDetail(String orderId) async {
    final response = await _apiClient.get('/api/v1/live/orders/$orderId');
    
    if (response.data['code'] == 200) {
      return LiveOrder.fromJson(response.data['data']);
    }
    throw Exception(response.data['message'] ?? '获取订单详情失败');
  }

  /// 获取用户订单列表
  Future<List<LiveOrder>> getUserOrders({
    int? status,
    int page = 1,
    int size = 20,
  }) async {
    final response = await _apiClient.get('/api/v1/live/orders/my', queryParameters: {
      if (status != null) 'status': status,
      'page': page,
      'size': size,
    });

    if (response.data['code'] == 200) {
      final List<dynamic> list = response.data['data']['list'] ?? [];
      return list.map((json) => LiveOrder.fromJson(json)).toList();
    }
    throw Exception(response.data['message'] ?? '获取订单列表失败');
  }

  /// 取消订单
  Future<void> cancelOrder(String orderId, {String? reason}) async {
    final response = await _apiClient.post(
        '/api/v1/live/orders/$orderId/cancel',
        queryParameters: reason != null ? {'reason': reason} : null);

    if (response.data['code'] != 200) {
      throw Exception(response.data['message'] ?? '取消订单失败');
    }
  }

  /// 确认收货
  Future<void> confirmReceive(String orderId) async {
    final response = await _apiClient.post('/api/v1/live/orders/$orderId/receive');
    
    if (response.data['code'] != 200) {
      throw Exception(response.data['message'] ?? '确认收货失败');
    }
  }

  /// 申请退款
  Future<Map<String, dynamic>> applyRefund({
    required String orderId,
    required String reason,
    double? refundAmount,
  }) async {
    final response = await _apiClient.post('/api/v1/live/orders/$orderId/refund', data: {
      'reason': reason,
      if (refundAmount != null) 'refundAmount': (refundAmount * 100).toInt(),
    });

    if (response.data['code'] == 200) {
      return response.data['data'];
    }
    throw Exception(response.data['message'] ?? '申请退款失败');
  }
}
