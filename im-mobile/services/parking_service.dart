import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/parking/parking_lot_model.dart';
import '../models/parking/parking_record_model.dart';
import '../utils/api_config.dart';
import '../utils/location_util.dart';

/// 停车服务类
/// 提供停车场查询、停车记录、反向寻车等API调用
class ParkingService {
  static final ParkingService _instance = ParkingService._internal();
  factory ParkingService() => _instance;
  ParkingService._internal();

  final String _baseUrl = ApiConfig.baseUrl;

  // ==================== 停车场查询接口 ====================

  /// 搜索附近停车场
  Future<List<NearbyParkingLotModel>> searchNearbyParkingLots({
    required double longitude,
    required double latitude,
    int radius = 2000,
    int pageNum = 1,
    int pageSize = 20,
  }) async {
    try {
      final response = await http.get(
        Uri.parse(
          '$_baseUrl/api/v1/parking/lots/nearby?longitude=$longitude&latitude=$latitude&radius=$radius&pageNum=$pageNum&pageSize=$pageSize',
        ),
        headers: ApiConfig.headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['code'] == 200) {
          final List<dynamic> records = data['data']['records'];
          return records.map((e) => NearbyParkingLotModel.fromJson(e)).toList();
        }
      }
      return [];
    } catch (e) {
      print('搜索附近停车场失败: $e');
      return [];
    }
  }

  /// 智能推荐停车场
  Future<List<RecommendParkingLotModel>> recommendParkingLots({
    required double longitude,
    required double latitude,
    int limit = 5,
  }) async {
    try {
      final response = await http.get(
        Uri.parse(
          '$_baseUrl/api/v1/parking/lots/recommend?longitude=$longitude&latitude=$latitude&limit=$limit',
        ),
        headers: ApiConfig.headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['code'] == 200) {
          final List<dynamic> list = data['data'];
          return list.map((e) => RecommendParkingLotModel.fromJson(e)).toList();
        }
      }
      return [];
    } catch (e) {
      print('推荐停车场失败: $e');
      return [];
    }
  }

  /// 搜索目的地周边停车场
  Future<List<NearbyParkingLotModel>> searchParkingLotsByDestination({
    required double destLongitude,
    required double destLatitude,
    int radius = 1000,
  }) async {
    try {
      final response = await http.get(
        Uri.parse(
          '$_baseUrl/api/v1/parking/lots/around-destination?destLongitude=$destLongitude&destLatitude=$destLatitude&radius=$radius',
        ),
        headers: ApiConfig.headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['code'] == 200) {
          final List<dynamic> list = data['data'];
          return list.map((e) => NearbyParkingLotModel.fromJson(e)).toList();
        }
      }
      return [];
    } catch (e) {
      print('搜索目的地周边停车场失败: $e');
      return [];
    }
  }

  /// 获取停车场详情
  Future<ParkingLotModel?> getParkingLotDetail(String parkingLotId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/api/v1/parking/lots/$parkingLotId'),
        headers: ApiConfig.headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['code'] == 200) {
          return ParkingLotModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('获取停车场详情失败: $e');
      return null;
    }
  }

  /// 搜索停车场
  Future<List<ParkingLotModel>> searchParkingLots({
    required String keyword,
    String? cityCode,
    int pageNum = 1,
    int pageSize = 10,
  }) async {
    try {
      var url = '$_baseUrl/api/v1/parking/lots/search?keyword=$keyword&pageNum=$pageNum&pageSize=$pageSize';
      if (cityCode != null) {
        url += '&cityCode=$cityCode';
      }

      final response = await http.get(
        Uri.parse(url),
        headers: ApiConfig.headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['code'] == 200) {
          final List<dynamic> records = data['data']['records'];
          return records.map((e) => ParkingLotModel.fromJson(e)).toList();
        }
      }
      return [];
    } catch (e) {
      print('搜索停车场失败: $e');
      return [];
    }
  }

  /// 计算停车费用
  Future<double> calculateParkingFee({
    required String parkingLotId,
    required int durationMinutes,
  }) async {
    try {
      final response = await http.get(
        Uri.parse(
          '$_baseUrl/api/v1/parking/lots/$parkingLotId/calculate-fee?duration=$durationMinutes',
        ),
        headers: ApiConfig.headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['code'] == 200) {
          return data['data'].toDouble();
        }
      }
      return 0.0;
    } catch (e) {
      print('计算停车费用失败: $e');
      return 0.0;
    }
  }

  // ==================== 停车记录接口 ====================

  /// 创建停车记录（入场）
  Future<String?> createParkingRecord({
    required String userId,
    required String parkingLotId,
    required String plateNumber,
    required double longitude,
    required double latitude,
    int entryMethod = 1,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/api/v1/parking/records/entry'),
        headers: ApiConfig.headers,
        body: jsonEncode({
          'userId': userId,
          'parkingLotId': parkingLotId,
          'plateNumber': plateNumber,
          'longitude': longitude,
          'latitude': latitude,
          'entryMethod': entryMethod,
          'entryTime': DateTime.now().toIso8601String(),
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['code'] == 200) {
          return data['data'].toString();
        }
      }
      return null;
    } catch (e) {
      print('创建停车记录失败: $e');
      return null;
    }
  }

  /// 获取当前停车记录
  Future<ParkingRecordModel?> getCurrentParkingRecord(String userId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/api/v1/parking/records/current/$userId'),
        headers: ApiConfig.headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['code'] == 200 && data['data'] != null) {
          return ParkingRecordModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('获取当前停车记录失败: $e');
      return null;
    }
  }

  /// 获取用户停车历史
  Future<List<ParkingRecordModel>> getUserParkingHistory({
    required String userId,
    int? status,
    int pageNum = 1,
    int pageSize = 10,
  }) async {
    try {
      var url = '$_baseUrl/api/v1/parking/records/user/$userId?pageNum=$pageNum&pageSize=$pageSize';
      if (status != null) {
        url += '&status=$status';
      }

      final response = await http.get(
        Uri.parse(url),
        headers: ApiConfig.headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['code'] == 200) {
          final List<dynamic> records = data['data']['records'];
          return records.map((e) => ParkingRecordModel.fromJson(e)).toList();
        }
      }
      return [];
    } catch (e) {
      print('获取用户停车历史失败: $e');
      return [];
    }
  }

  /// 标记停车位置
  Future<bool> markParkingLocation({
    required String recordId,
    String? parkingFloor,
    String? parkingArea,
    String? parkingSpaceNumber,
    String? photoUrl,
    double? longitude,
    double? latitude,
    String? note,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/api/v1/parking/records/$recordId/mark-location'),
        headers: ApiConfig.headers,
        body: jsonEncode({
          'parkingFloor': parkingFloor,
          'parkingArea': parkingArea,
          'parkingSpaceNumber': parkingSpaceNumber,
          'photoUrl': photoUrl,
          'longitude': longitude,
          'latitude': latitude,
          'note': note,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['code'] == 200 && data['data'] == true;
      }
      return false;
    } catch (e) {
      print('标记停车位置失败: $e');
      return false;
    }
  }

  /// 获取用户停车概览
  Future<UserParkingOverviewModel?> getUserParkingOverview(String userId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/api/v1/parking/records/user/$userId/overview'),
        headers: ApiConfig.headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['code'] == 200) {
          return UserParkingOverviewModel.fromJson(data['data']);
        }
      }
      return null;
    } catch (e) {
      print('获取用户停车概览失败: $e');
      return null;
    }
  }

  // ==================== 反向寻车接口 ====================

  /// 开始反向寻车
  Future<String?> startCarFinding({
    required String userId,
    required String parkingRecordId,
    double? currentLongitude,
    double? currentLatitude,
    int findingMethod = 1,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/api/v1/parking/car-finding/start'),
        headers: ApiConfig.headers,
        body: jsonEncode({
          'userId': userId,
          'parkingRecordId': parkingRecordId,
          'currentLongitude': currentLongitude,
          'currentLatitude': currentLatitude,
          'findingMethod': findingMethod,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['code'] == 200) {
          return data['data'].toString();
        }
      }
      return null;
    } catch (e) {
      print('开始反向寻车失败: $e');
      return null;
    }
  }

  /// 获取寻车导航路径
  Future<Map<String, dynamic>?> getCarFindingNavigation({
    required String parkingRecordId,
    required double currentLongitude,
    required double currentLatitude,
  }) async {
    try {
      final response = await http.get(
        Uri.parse(
          '$_baseUrl/api/v1/parking/car-finding/navigation-path?parkingRecordId=$parkingRecordId&currentLongitude=$currentLongitude&currentLatitude=$currentLatitude',
        ),
        headers: ApiConfig.headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['code'] == 200) {
          return data['data'];
        }
      }
      return null;
    } catch (e) {
      print('获取寻车导航路径失败: $e');
      return null;
    }
  }

  /// 完成寻车
  Future<bool> completeCarFinding({
    required String findingId,
    required bool success,
  }) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/api/v1/parking/car-finding/$findingId/complete?success=$success'),
        headers: ApiConfig.headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['code'] == 200 && data['data'] == true;
      }
      return false;
    } catch (e) {
      print('完成寻车失败: $e');
      return false;
    }
  }

  // ==================== 支付接口 ====================

  /// 创建支付订单
  Future<String?> createPaymentOrder(String parkingRecordId) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/api/v1/parking/payments/create-order?parkingRecordId=$parkingRecordId'),
        headers: ApiConfig.headers,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        if (data['code'] == 200) {
          return data['data'].toString();
        }
      }
      return null;
    } catch (e) {
      print('创建支付订单失败: $e');
      return null;
    }
  }

  // ==================== 工具方法 ====================

  /// 格式化距离显示
  String formatDistance(double distance) {
    if (distance < 1000) {
      return '${distance.round()}米';
    } else {
      return '${(distance / 1000).toStringAsFixed(1)}公里';
    }
  }

  /// 估算步行时间（分钟）
  int estimateWalkTime(double distance) {
    // 假设步行速度为5km/h = 83.3m/min
    return (distance / 83.3).round();
  }

  /// 估算驾车时间（分钟）
  int estimateDriveTime(double distance) {
    // 假设驾车速度为30km/h = 500m/min（城市道路）
    return (distance / 500).round();
  }
}
