import 'package:dio/dio.dart';
import '../models/geofencing/smart_arrival_message_model.dart';
import '../models/geofencing/personalized_recommendation_model.dart';
import '../utils/http_client.dart';
import '../utils/logger.dart';

/// 智能到店消息服务类
/// 
/// @author IM Development Team
/// @since 2026-03-28
class SmartArrivalMessageService {
  static final SmartArrivalMessageService _instance = SmartArrivalMessageService._internal();
  factory SmartArrivalMessageService() => _instance;
  SmartArrivalMessageService._internal();

  final HttpClient _httpClient = HttpClient();
  final Logger _logger = Logger('SmartArrivalMessageService');

  /// 获取用户消息列表
  Future<List<SmartArrivalMessage>> getUserMessages(
    String userId, {
    int page = 1,
    int size = 20,
  }) async {
    try {
      _logger.info('Getting user messages: $userId, page=$page, size=$size');
      final response = await _httpClient.get(
        '/api/v1/geofencing/users/$userId/messages',
        queryParameters: {
          'page': page,
          'size': size,
        },
      );
      return (response.data as List)
          .map((e) => SmartArrivalMessage.fromJson(e))
          .toList();
    } catch (e) {
      _logger.error('Failed to get user messages: $e');
      throw Exception('获取消息列表失败: $e');
    }
  }

  /// 标记消息已读
  Future<void> markMessageAsRead(String messageId, String userId) async {
    try {
      _logger.info('Marking message as read: $messageId');
      await _httpClient.post(
        '/api/v1/geofencing/messages/$messageId/read',
        queryParameters: {'userId': userId},
      );
    } catch (e) {
      _logger.error('Failed to mark message as read: $e');
      throw Exception('标记消息已读失败: $e');
    }
  }

  /// 获取未读消息数量
  Future<int> getUnreadMessageCount(String userId) async {
    try {
      _logger.info('Getting unread message count: $userId');
      final response = await _httpClient.get(
        '/api/v1/geofencing/users/$userId/messages/unread-count',
      );
      return response.data as int;
    } catch (e) {
      _logger.error('Failed to get unread message count: $e');
      return 0;
    }
  }

  /// 生成个性化推荐
  Future<PersonalizedRecommendation> generateRecommendations(
    String userId,
    String merchantId,
  ) async {
    try {
      _logger.info('Generating recommendations: user=$userId, merchant=$merchantId');
      final response = await _httpClient.get(
        '/api/v1/geofencing/users/$userId/merchants/$merchantId/recommendations',
      );
      return PersonalizedRecommendation.fromJson(response.data);
    } catch (e) {
      _logger.error('Failed to generate recommendations: $e');
      throw Exception('生成推荐失败: $e');
    }
  }

  /// 匹配最佳优惠券
  Future<Map<String, dynamic>> matchBestCoupon(String userId, String merchantId) async {
    try {
      _logger.info('Matching best coupon: user=$userId, merchant=$merchantId');
      final response = await _httpClient.get(
        '/api/v1/geofencing/users/$userId/merchants/$merchantId/best-coupon',
      );
      return Map<String, dynamic>.from(response.data);
    } catch (e) {
      _logger.error('Failed to match best coupon: $e');
      throw Exception('匹配优惠券失败: $e');
    }
  }

  /// 获取围栏统计信息
  Future<Map<String, dynamic>> getGeofenceStatistics(String merchantId) async {
    try {
      _logger.info('Getting geofence statistics: $merchantId');
      final response = await _httpClient.get(
        '/api/v1/geofencing/merchants/$merchantId/statistics',
      );
      return Map<String, dynamic>.from(response.data);
    } catch (e) {
      _logger.error('Failed to get geofence statistics: $e');
      throw Exception('获取围栏统计失败: $e');
    }
  }
}
