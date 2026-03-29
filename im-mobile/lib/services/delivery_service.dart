import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/delivery/delivery_order_model.dart';
import '../models/delivery/delivery_rider_model.dart';

/// 配送服务类 - 即时配送运力调度系统
/// 处理配送订单相关的API请求
class DeliveryService {
  static const String baseUrl = 'https://api.example.com/api/v1/delivery';
  
  final String? token;
  
  DeliveryService({this.token});

  Map<String, String> get _headers => {
    'Content-Type': 'application/json',
    if (token != null) 'Authorization': 'Bearer $token',
  };

  /// 创建配送订单
  Future<DeliveryOrderModel?> createOrder(DeliveryOrderModel order) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/orders'),
        headers: _headers,
        body: jsonEncode(order.toJson()),
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return DeliveryOrderModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('创建配送订单失败: $e');
      return null;
    }
  }

  /// 获取订单详情
  Future<DeliveryOrderModel?> getOrder(int orderId) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/orders/$orderId'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return DeliveryOrderModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('获取订单详情失败: $e');
      return null;
    }
  }

  /// 分配订单给骑手
  Future<DeliveryOrderModel?> assignOrder(int orderId) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/orders/$orderId/assign'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return DeliveryOrderModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('分配订单失败: $e');
      return null;
    }
  }

  /// 骑手接单
  Future<DeliveryOrderModel?> acceptOrder(int orderId, int riderId) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/orders/$orderId/accept?riderId=$riderId'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return DeliveryOrderModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('接单失败: $e');
      return null;
    }
  }

  /// 骑手到店
  Future<DeliveryOrderModel?> arriveAtMerchant(int orderId, int riderId) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/orders/$orderId/arrive?riderId=$riderId'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return DeliveryOrderModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('到店确认失败: $e');
      return null;
    }
  }

  /// 骑手取货
  Future<DeliveryOrderModel?> pickUpOrder(int orderId, int riderId) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/orders/$orderId/pickup?riderId=$riderId'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return DeliveryOrderModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('取货失败: $e');
      return null;
    }
  }

  /// 订单送达
  Future<DeliveryOrderModel?> deliverOrder(int orderId, int riderId) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/orders/$orderId/deliver?riderId=$riderId'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return DeliveryOrderModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('送达确认失败: $e');
      return null;
    }
  }

  /// 完成订单
  Future<DeliveryOrderModel?> completeOrder(int orderId) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/orders/$orderId/complete'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return DeliveryOrderModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('完成订单失败: $e');
      return null;
    }
  }

  /// 取消订单
  Future<DeliveryOrderModel?> cancelOrder(
    int orderId,
    String reason,
    String cancelledBy,
  ) async {
    try {
      final response = await http.post(
        Uri.parse(
          '$baseUrl/orders/$orderId/cancel?reason=${Uri.encodeComponent(reason)}&cancelledBy=$cancelledBy',
        ),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return DeliveryOrderModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('取消订单失败: $e');
      return null;
    }
  }

  /// 获取骑手订单列表
  Future<List<DeliveryOrderModel>> getRiderOrders(
    int riderId, {
    String? status,
    int page = 1,
    int size = 10,
  }) async {
    try {
      final queryParams = <String, String>{
        'page': page.toString(),
        'size': size.toString(),
        if (status != null) 'status': status,
      };
      
      final response = await http.get(
        Uri.parse('$baseUrl/riders/$riderId/orders').replace(
          queryParameters: queryParams,
        ),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          final List<dynamic> list = data['data'];
          return list.map((e) => DeliveryOrderModel.fromJson(e)).toList();
        }
      }
      return [];
    } catch (e) {
      print('获取骑手订单列表失败: $e');
      return [];
    }
  }

  /// 获取顾客订单列表
  Future<List<DeliveryOrderModel>> getCustomerOrders(
    int customerId, {
    String? status,
    int page = 1,
    int size = 10,
  }) async {
    try {
      final queryParams = <String, String>{
        'page': page.toString(),
        'size': size.toString(),
        if (status != null) 'status': status,
      };
      
      final response = await http.get(
        Uri.parse('$baseUrl/customers/$customerId/orders').replace(
          queryParameters: queryParams,
        ),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          final List<dynamic> list = data['data'];
          return list.map((e) => DeliveryOrderModel.fromJson(e)).toList();
        }
      }
      return [];
    } catch (e) {
      print('获取顾客订单列表失败: $e');
      return [];
    }
  }

  /// 获取订单配送进度
  Future<Map<String, dynamic>?> getOrderProgress(int orderId) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/orders/$orderId/progress'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return data['data'];
        }
      }
      return null;
    } catch (e) {
      print('获取订单进度失败: $e');
      return null;
    }
  }

  /// 订单评价
  Future<DeliveryOrderModel?> rateOrder(
    int orderId,
    int rating, {
    String? comment,
  }) async {
    try {
      final response = await http.post(
        Uri.parse(
          '$baseUrl/orders/$orderId/rate?rating=$rating${comment != null ? '&comment=${Uri.encodeComponent(comment)}' : ''}',
        ),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return DeliveryOrderModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('订单评价失败: $e');
      return null;
    }
  }

  /// 计算配送费
  Future<double?> calculateDeliveryFee(
    int merchantId,
    double deliveryLng,
    double deliveryLat, {
    double? weight,
  }) async {
    try {
      final queryParams = <String, String>{
        'merchantId': merchantId.toString(),
        'deliveryLng': deliveryLng.toString(),
        'deliveryLat': deliveryLat.toString(),
        if (weight != null) 'weight': weight.toString(),
      };
      
      final response = await http.get(
        Uri.parse('$baseUrl/orders/calculate-fee').replace(
          queryParameters: queryParams,
        ),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return data['data']?.toDouble();
        }
      }
      return null;
    } catch (e) {
      print('计算配送费失败: $e');
      return null;
    }
  }
}

/// 骑手服务类 - 即时配送运力调度系统
/// 处理骑手相关的API请求
class RiderService {
  static const String baseUrl = 'https://api.example.com/api/v1/delivery/riders';
  
  final String? token;
  
  RiderService({this.token});

  Map<String, String> get _headers => {
    'Content-Type': 'application/json',
    if (token != null) 'Authorization': 'Bearer $token',
  };

  /// 骑手注册
  Future<DeliveryRiderModel?> registerRider(DeliveryRiderModel rider) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/register'),
        headers: _headers,
        body: jsonEncode(rider.toJson()),
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return DeliveryRiderModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('骑手注册失败: $e');
      return null;
    }
  }

  /// 获取骑手信息
  Future<DeliveryRiderModel?> getRider(int riderId) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/$riderId'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return DeliveryRiderModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('获取骑手信息失败: $e');
      return null;
    }
  }

  /// 更新工作状态
  Future<bool> updateWorkStatus(int riderId, String status) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/$riderId/status?status=$status'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['success'] == true;
      }
      return false;
    } catch (e) {
      print('更新工作状态失败: $e');
      return false;
    }
  }

  /// 上报位置
  Future<bool> updateLocation(
    int riderId,
    double longitude,
    double latitude, {
    String source = 'GPS',
  }) async {
    try {
      final response = await http.post(
        Uri.parse(
          '$baseUrl/$riderId/location?longitude=$longitude&latitude=$latitude&source=$source',
        ),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['success'] == true;
      }
      return false;
    } catch (e) {
      print('上报位置失败: $e');
      return false;
    }
  }

  /// 骑手签到
  Future<bool> checkIn(int riderId) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/$riderId/checkin'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['success'] == true;
      }
      return false;
    } catch (e) {
      print('骑手签到失败: $e');
      return false;
    }
  }

  /// 骑手签退
  Future<bool> checkOut(int riderId) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/$riderId/checkout'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['success'] == true;
      }
      return false;
    } catch (e) {
      print('骑手签退失败: $e');
      return false;
    }
  }

  /// 获取今日统计
  Future<Map<String, dynamic>?> getTodayStats(int riderId) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/$riderId/stats/today'),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          return data['data'];
        }
      }
      return null;
    } catch (e) {
      print('获取今日统计失败: $e');
      return null;
    }
  }

  /// 获取附近可用骑手
  Future<List<DeliveryRiderModel>> getNearbyRiders(
    double longitude,
    double latitude, {
    int radius = 5000,
  }) async {
    try {
      final response = await http.get(
        Uri.parse(
          '$baseUrl/nearby?longitude=$longitude&latitude=$latitude&radius=$radius',
        ),
        headers: _headers,
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['success'] == true) {
          final List<dynamic> list = data['data'];
          return list.map((e) => DeliveryRiderModel.fromJson(e)).toList();
        }
      }
      return [];
    } catch (e) {
      print('获取附近骑手失败: $e');
      return [];
    }
  }
}
