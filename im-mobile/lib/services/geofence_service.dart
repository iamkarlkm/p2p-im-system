import 'package:dio/dio.dart';
import '../models/geofencing/geofence_zone_model.dart';
import '../models/geofencing/geofence_event_model.dart';
import '../utils/http_client.dart';
import '../utils/logger.dart';

/// 地理围栏服务类
/// 提供围栏相关的API调用
/// 
/// @author IM Development Team
/// @since 2026-03-28
class GeofenceService {
  static final GeofenceService _instance = GeofenceService._internal();
  factory GeofenceService() => _instance;
  GeofenceService._internal();

  final HttpClient _httpClient = HttpClient();
  final Logger _logger = Logger('GeofenceService');

  /// 创建地理围栏
  Future<String> createGeofence(GeofenceZone geofence) async {
    try {
      _logger.info('Creating geofence: ${geofence.name}');
      final response = await _httpClient.post(
        '/api/v1/geofencing/geofences',
        data: geofence.toJson(),
      );
      return response.data.toString();
    } catch (e) {
      _logger.error('Failed to create geofence: $e');
      throw Exception('创建围栏失败: $e');
    }
  }

  /// 更新地理围栏
  Future<void> updateGeofence(String id, GeofenceZone geofence) async {
    try {
      _logger.info('Updating geofence: $id');
      await _httpClient.put(
        '/api/v1/geofencing/geofences/$id',
        data: geofence.toJson(),
      );
    } catch (e) {
      _logger.error('Failed to update geofence: $e');
      throw Exception('更新围栏失败: $e');
    }
  }

  /// 删除地理围栏
  Future<void> deleteGeofence(String id) async {
    try {
      _logger.info('Deleting geofence: $id');
      await _httpClient.delete('/api/v1/geofencing/geofences/$id');
    } catch (e) {
      _logger.error('Failed to delete geofence: $e');
      throw Exception('删除围栏失败: $e');
    }
  }

  /// 获取围栏详情
  Future<GeofenceZone> getGeofenceDetail(String id) async {
    try {
      _logger.info('Getting geofence detail: $id');
      final response = await _httpClient.get('/api/v1/geofencing/geofences/$id');
      return GeofenceZone.fromJson(response.data);
    } catch (e) {
      _logger.error('Failed to get geofence detail: $e');
      throw Exception('获取围栏详情失败: $e');
    }
  }

  /// 获取商户围栏列表
  Future<List<GeofenceZone>> getMerchantGeofences(String merchantId) async {
    try {
      _logger.info('Getting merchant geofences: $merchantId');
      final response = await _httpClient.get(
        '/api/v1/geofencing/merchants/$merchantId/geofences',
      );
      return (response.data as List)
          .map((e) => GeofenceZone.fromJson(e))
          .toList();
    } catch (e) {
      _logger.error('Failed to get merchant geofences: $e');
      throw Exception('获取商户围栏失败: $e');
    }
  }

  /// 获取POI围栏列表
  Future<List<GeofenceZone>> getPoiGeofences(String poiId) async {
    try {
      _logger.info('Getting POI geofences: $poiId');
      final response = await _httpClient.get(
        '/api/v1/geofencing/pois/$poiId/geofences',
      );
      return (response.data as List)
          .map((e) => GeofenceZone.fromJson(e))
          .toList();
    } catch (e) {
      _logger.error('Failed to get POI geofences: $e');
      throw Exception('获取POI围栏失败: $e');
    }
  }

  /// 查询附近围栏
  Future<List<GeofenceZone>> findNearbyGeofences(
    double longitude,
    double latitude, {
    int radius = 1000,
  }) async {
    try {
      _logger.info('Finding nearby geofences: ($longitude, $latitude), radius=$radius');
      final response = await _httpClient.get(
        '/api/v1/geofencing/geofences/nearby',
        queryParameters: {
          'longitude': longitude,
          'latitude': latitude,
          'radius': radius,
        },
      );
      return (response.data as List)
          .map((e) => GeofenceZone.fromJson(e))
          .toList();
    } catch (e) {
      _logger.error('Failed to find nearby geofences: $e');
      throw Exception('查询附近围栏失败: $e');
    }
  }

  /// 启用围栏
  Future<void> enableGeofence(String id) async {
    try {
      _logger.info('Enabling geofence: $id');
      await _httpClient.post('/api/v1/geofencing/geofences/$id/enable');
    } catch (e) {
      _logger.error('Failed to enable geofence: $e');
      throw Exception('启用围栏失败: $e');
    }
  }

  /// 禁用围栏
  Future<void> disableGeofence(String id) async {
    try {
      _logger.info('Disabling geofence: $id');
      await _httpClient.post('/api/v1/geofencing/geofences/$id/disable');
    } catch (e) {
      _logger.error('Failed to disable geofence: $e');
      throw Exception('禁用围栏失败: $e');
    }
  }

  /// 复制围栏
  Future<String> copyGeofence(String id, String targetPoiId) async {
    try {
      _logger.info('Copying geofence: $id to POI: $targetPoiId');
      final response = await _httpClient.post(
        '/api/v1/geofencing/geofences/$id/copy',
        queryParameters: {'targetPoiId': targetPoiId},
      );
      return response.data.toString();
    } catch (e) {
      _logger.error('Failed to copy geofence: $e');
      throw Exception('复制围栏失败: $e');
    }
  }

  /// 获取围栏层级树
  Future<List<Map<String, dynamic>>> getGeofenceTree(String merchantId) async {
    try {
      _logger.info('Getting geofence tree: $merchantId');
      final response = await _httpClient.get(
        '/api/v1/geofencing/merchants/$merchantId/geofence-tree',
      );
      return List<Map<String, dynamic>>.from(response.data);
    } catch (e) {
      _logger.error('Failed to get geofence tree: $e');
      throw Exception('获取围栏树失败: $e');
    }
  }

  /// 上报位置
  Future<List<GeofenceEvent>> reportLocation({
    required String userId,
    required double longitude,
    required double latitude,
    double? accuracy,
    String? source,
    double? altitude,
    double? speed,
    double? bearing,
    String? deviceId,
    String? appVersion,
  }) async {
    try {
      _logger.info('Reporting location: user=$userId, ($longitude, $latitude)');
      final response = await _httpClient.post(
        '/api/v1/geofencing/location/report',
        data: {
          'userId': userId,
          'longitude': longitude,
          'latitude': latitude,
          'accuracy': accuracy,
          'source': source,
          'altitude': altitude,
          'speed': speed,
          'bearing': bearing,
          'deviceId': deviceId,
          'appVersion': appVersion,
        },
      );
      return (response.data as List)
          .map((e) => GeofenceEvent.fromJson(e))
          .toList();
    } catch (e) {
      _logger.error('Failed to report location: $e');
      throw Exception('上报位置失败: $e');
    }
  }

  /// 查询点是否在围栏内
  Future<bool> isPointInGeofence(String geofenceId, double longitude, double latitude) async {
    try {
      final response = await _httpClient.get(
        '/api/v1/geofencing/geofences/$geofenceId/contains',
        queryParameters: {
          'longitude': longitude,
          'latitude': latitude,
        },
      );
      return response.data as bool;
    } catch (e) {
      _logger.error('Failed to check point in geofence: $e');
      return false;
    }
  }

  /// 查询点所在的所有围栏
  Future<List<String>> findGeofencesByPoint(double longitude, double latitude) async {
    try {
      final response = await _httpClient.get(
        '/api/v1/geofencing/geofences/contains-point',
        queryParameters: {
          'longitude': longitude,
          'latitude': latitude,
        },
      );
      return (response.data as List).map((e) => e.toString()).toList();
    } catch (e) {
      _logger.error('Failed to find geofences by point: $e');
      return [];
    }
  }
}
