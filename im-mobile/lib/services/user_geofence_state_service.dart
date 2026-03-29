import 'package:dio/dio.dart';
import '../models/geofencing/user_geofence_state_model.dart';
import '../models/geofencing/geofence_zone_model.dart';
import '../utils/http_client.dart';
import '../utils/logger.dart';

/// 用户围栏状态服务类
/// 
/// @author IM Development Team
/// @since 2026-03-28
class UserGeofenceStateService {
  static final UserGeofenceStateService _instance = UserGeofenceStateService._internal();
  factory UserGeofenceStateService() => _instance;
  UserGeofenceStateService._internal();

  final HttpClient _httpClient = HttpClient();
  final Logger _logger = Logger('UserGeofenceStateService');

  /// 获取用户当前所在的所有围栏
  Future<List<UserGeofenceState>> getUserCurrentGeofences(String userId) async {
    try {
      _logger.info('Getting user current geofences: $userId');
      final response = await _httpClient.get(
        '/api/v1/geofencing/users/$userId/current-geofences',
      );
      return (response.data as List)
          .map((e) => UserGeofenceState.fromJson(e))
          .toList();
    } catch (e) {
      _logger.error('Failed to get user current geofences: $e');
      throw Exception('获取用户当前围栏失败: $e');
    }
  }

  /// 订阅围栏
  Future<void> subscribeGeofence(String userId, String geofenceId) async {
    try {
      _logger.info('User $userId subscribing geofence: $geofenceId');
      await _httpClient.post(
        '/api/v1/geofencing/users/$userId/subscribe/$geofenceId',
      );
    } catch (e) {
      _logger.error('Failed to subscribe geofence: $e');
      throw Exception('订阅围栏失败: $e');
    }
  }

  /// 取消订阅围栏
  Future<void> unsubscribeGeofence(String userId, String geofenceId) async {
    try {
      _logger.info('User $userId unsubscribing geofence: $geofenceId');
      await _httpClient.post(
        '/api/v1/geofencing/users/$userId/unsubscribe/$geofenceId',
      );
    } catch (e) {
      _logger.error('Failed to unsubscribe geofence: $e');
      throw Exception('取消订阅围栏失败: $e');
    }
  }

  /// 获取用户订阅的围栏列表
  Future<List<GeofenceZone>> getUserSubscribedGeofences(String userId) async {
    try {
      _logger.info('Getting user subscribed geofences: $userId');
      final response = await _httpClient.get(
        '/api/v1/geofencing/users/$userId/subscribed-geofences',
      );
      return (response.data as List)
          .map((e) => GeofenceZone.fromJson(e))
          .toList();
    } catch (e) {
      _logger.error('Failed to get user subscribed geofences: $e');
      throw Exception('获取用户订阅围栏失败: $e');
    }
  }

  /// 获取用户围栏历史记录
  Future<List<Map<String, dynamic>>> getUserGeofenceHistory(
    String userId, {
    int days = 30,
  }) async {
    try {
      _logger.info('Getting user geofence history: $userId, days=$days');
      final response = await _httpClient.get(
        '/api/v1/geofencing/users/$userId/geofence-history',
        queryParameters: {'days': days},
      );
      return List<Map<String, dynamic>>.from(response.data);
    } catch (e) {
      _logger.error('Failed to get user geofence history: $e');
      throw Exception('获取围栏历史失败: $e');
    }
  }

  /// 获取用户到店统计
  Future<Map<String, dynamic>> getUserArrivalStatistics(
    String userId, {
    int days = 30,
  }) async {
    try {
      _logger.info('Getting user arrival statistics: $userId, days=$days');
      final response = await _httpClient.get(
        '/api/v1/geofencing/users/$userId/arrival-statistics',
        queryParameters: {'days': days},
      );
      return Map<String, dynamic>.from(response.data);
    } catch (e) {
      _logger.error('Failed to get user arrival statistics: $e');
      throw Exception('获取到店统计失败: $e');
    }
  }
}
