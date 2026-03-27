import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:im_mobile/config/api_config.dart';
import 'package:im_mobile/models/recommended_user_model.dart';
import 'package:im_mobile/utils/http_util.dart';

class FriendRecommendationService {
  static final FriendRecommendationService _instance = FriendRecommendationService._internal();
  factory FriendRecommendationService() => _instance;
  FriendRecommendationService._internal();

  final Dio _dio = HttpUtil().dio;

  Future<List<RecommendedUserModel>> getRecommendations({
    required int page,
    required int size,
    String? algorithm,
  }) async {
    try {
      final response = await _dio.get(
        '${ApiConfig.baseUrl}/friends/recommendations',
        queryParameters: {
          'page': page,
          'size': size,
          if (algorithm != null && algorithm != 'mixed') 'algorithm': algorithm,
        },
      );

      if (response.statusCode == 200 && response.data != null) {
        final data = response.data['data'] as List<dynamic>?;
        if (data != null) {
          return data.map((json) => RecommendedUserModel.fromJson(json)).toList();
        }
      }
      return [];
    } catch (e) {
      throw Exception('Failed to load recommendations: $e');
    }
  }

  Future<void> sendFriendRequest(String userId) async {
    try {
      final response = await _dio.post(
        '${ApiConfig.baseUrl}/friends/requests',
        data: {
          'toUserId': userId,
          'source': 'recommendation',
        },
      );

      if (response.statusCode != 200 && response.statusCode != 201) {
        throw Exception('Failed to send friend request');
      }
    } catch (e) {
      throw Exception('Failed to send friend request: $e');
    }
  }

  Future<void> ignoreRecommendation(String userId) async {
    try {
      final response = await _dio.post(
        '${ApiConfig.baseUrl}/friends/recommendations/$userId/ignore',
      );

      if (response.statusCode != 200) {
        throw Exception('Failed to ignore recommendation');
      }
    } catch (e) {
      throw Exception('Failed to ignore recommendation: $e');
    }
  }

  Future<Map<String, dynamic>> getRecommendationStats() async {
    try {
      final response = await _dio.get(
        '${ApiConfig.baseUrl}/friends/recommendations/stats',
      );

      if (response.statusCode == 200 && response.data != null) {
        return response.data;
      }
      return {};
    } catch (e) {
      throw Exception('Failed to load stats: $e');
    }
  }

  Future<List<RecommendedUserModel>> refreshRecommendations() async {
    try {
      final response = await _dio.post(
        '${ApiConfig.baseUrl}/friends/recommendations/refresh',
      );

      if (response.statusCode == 200 && response.data != null) {
        final data = response.data['data'] as List<dynamic>?;
        if (data != null) {
          return data.map((json) => RecommendedUserModel.fromJson(json)).toList();
        }
      }
      return [];
    } catch (e) {
      throw Exception('Failed to refresh recommendations: $e');
    }
  }
}
