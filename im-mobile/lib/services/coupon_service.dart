import 'dart:convert';
import 'package:http/http.dart' as http;
import '../config/api_config.dart';
import '../models/coupon_model.dart';
import '../utils/storage_util.dart';

/// 优惠券服务类
/// 
/// 提供优惠券相关的API调用
/// 
/// @author IM Development Team
/// @version 1.0
/// @since 2026-03-28
class CouponService {
  static final http.Client _client = http.Client();

  /// 获取附近优惠券
  static Future<ApiResponse<List<CouponModel>>> getNearbyCoupons({
    required double lat,
    required double lng,
    int radius = 5000,
    String? category,
    String? cityCode,
    String sortBy = 'DISTANCE',
    int page = 0,
    int size = 20,
    String? keyword,
  }) async {
    try {
      final token = await StorageUtil.getToken();
      
      final queryParams = <String, String>{
        'lat': lat.toString(),
        'lng': lng.toString(),
        'radius': radius.toString(),
        'sortBy': sortBy,
        'page': page.toString(),
        'size': size.toString(),
      };

      if (category != null) queryParams['category'] = category;
      if (cityCode != null) queryParams['cityCode'] = cityCode;
      if (keyword != null) queryParams['keyword'] = keyword;

      final uri = Uri.parse('${ApiConfig.baseUrl}/coupons/nearby')
          .replace(queryParameters: queryParams);

      final response = await _client.get(
        uri,
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
      );

      if (response.statusCode == 200) {
        final jsonData = json.decode(response.body);
        if (jsonData['success'] == true) {
          final List<dynamic> data = jsonData['data'] ?? [];
          final coupons = data.map((item) => CouponModel.fromJson(item)).toList();
          return ApiResponse(success: true, data: coupons);
        } else {
          return ApiResponse(
            success: false,
            message: jsonData['message'] ?? '获取失败',
          );
        }
      } else {
        return ApiResponse(
          success: false,
          message: '请求失败: ${response.statusCode}',
        );
      }
    } catch (e) {
      return ApiResponse(success: false, message: '网络错误: $e');
    }
  }

  /// 领取优惠券
  static Future<ApiResponse<String>> claimCoupon(String couponId) async {
    try {
      final token = await StorageUtil.getToken();

      final response = await _client.post(
        Uri.parse('${ApiConfig.baseUrl}/coupons/claim'),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
        body: json.encode({
          'couponId': couponId,
          'claimChannel': 'APP',
          'claimSource': 'SELF',
        }),
      );

      if (response.statusCode == 200) {
        final jsonData = json.decode(response.body);
        return ApiResponse(
          success: jsonData['success'] == true,
          data: jsonData['data'],
          message: jsonData['message'],
        );
      } else {
        return ApiResponse(
          success: false,
          message: '请求失败: ${response.statusCode}',
        );
      }
    } catch (e) {
      return ApiResponse(success: false, message: '网络错误: $e');
    }
  }

  /// 获取我的优惠券
  static Future<ApiResponse<List<UserCouponModel>>> getMyCoupons({
    String? status,
    int page = 0,
    int size = 20,
  }) async {
    try {
      final token = await StorageUtil.getToken();

      final queryParams = <String, String>{
        'page': page.toString(),
        'size': size.toString(),
      };

      if (status != null) queryParams['status'] = status;

      final uri = Uri.parse('${ApiConfig.baseUrl}/coupons/my')
          .replace(queryParameters: queryParams);

      final response = await _client.get(
        uri,
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
      );

      if (response.statusCode == 200) {
        final jsonData = json.decode(response.body);
        if (jsonData['success'] == true) {
          final List<dynamic> data = jsonData['data'] ?? [];
          final coupons = data.map((item) => UserCouponModel.fromJson(item)).toList();
          return ApiResponse(success: true, data: coupons);
        } else {
          return ApiResponse(
            success: false,
            message: jsonData['message'] ?? '获取失败',
          );
        }
      } else {
        return ApiResponse(
          success: false,
          message: '请求失败: ${response.statusCode}',
        );
      }
    } catch (e) {
      return ApiResponse(success: false, message: '网络错误: $e');
    }
  }

  /// 获取优惠券详情
  static Future<ApiResponse<CouponModel>> getCouponDetail(String couponId) async {
    try {
      final token = await StorageUtil.getToken();

      final response = await _client.get(
        Uri.parse('${ApiConfig.baseUrl}/coupons/$couponId'),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
      );

      if (response.statusCode == 200) {
        final jsonData = json.decode(response.body);
        if (jsonData['success'] == true) {
          return ApiResponse(
            success: true,
            data: CouponModel.fromJson(jsonData['data']),
          );
        } else {
          return ApiResponse(
            success: false,
            message: jsonData['message'] ?? '获取失败',
          );
        }
      } else {
        return ApiResponse(
          success: false,
          message: '请求失败: ${response.statusCode}',
        );
      }
    } catch (e) {
      return ApiResponse(success: false, message: '网络错误: $e');
    }
  }

  /// 获取商户优惠券
  static Future<ApiResponse<List<CouponModel>>> getMerchantCoupons(String merchantId) async {
    try {
      final token = await StorageUtil.getToken();

      final response = await _client.get(
        Uri.parse('${ApiConfig.baseUrl}/coupons/merchant/$merchantId'),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
      );

      if (response.statusCode == 200) {
        final jsonData = json.decode(response.body);
        if (jsonData['success'] == true) {
          final List<dynamic> data = jsonData['data'] ?? [];
          final coupons = data.map((item) => CouponModel.fromJson(item)).toList();
          return ApiResponse(success: true, data: coupons);
        } else {
          return ApiResponse(
            success: false,
            message: jsonData['message'] ?? '获取失败',
          );
        }
      } else {
        return ApiResponse(
          success: false,
          message: '请求失败: ${response.statusCode}',
        );
      }
    } catch (e) {
      return ApiResponse(success: false, message: '网络错误: $e');
    }
  }

  /// 使用优惠券
  static Future<ApiResponse<double>> useCoupon({
    required String userCouponId,
    required String orderId,
    required double orderAmount,
  }) async {
    try {
      final token = await StorageUtil.getToken();

      final response = await _client.post(
        Uri.parse('${ApiConfig.baseUrl}/coupons/use'),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
        body: json.encode({
          'userCouponId': userCouponId,
          'orderId': orderId,
          'orderAmount': orderAmount,
        }),
      );

      if (response.statusCode == 200) {
        final jsonData = json.decode(response.body);
        return ApiResponse(
          success: jsonData['success'] == true,
          data: jsonData['data']?.toDouble(),
          message: jsonData['message'],
        );
      } else {
        return ApiResponse(
          success: false,
          message: '请求失败: ${response.statusCode}',
        );
      }
    } catch (e) {
      return ApiResponse(success: false, message: '网络错误: $e');
    }
  }

  /// 转赠优惠券
  static Future<ApiResponse<void>> transferCoupon({
    required String userCouponId,
    required String targetUserId,
    String? message,
  }) async {
    try {
      final token = await StorageUtil.getToken();

      final response = await _client.post(
        Uri.parse('${ApiConfig.baseUrl}/coupons/transfer'),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
        body: json.encode({
          'userCouponId': userCouponId,
          'targetUserId': targetUserId,
          'message': message,
        }),
      );

      if (response.statusCode == 200) {
        final jsonData = json.decode(response.body);
        return ApiResponse(
          success: jsonData['success'] == true,
          message: jsonData['message'],
        );
      } else {
        return ApiResponse(
          success: false,
          message: '请求失败: ${response.statusCode}',
        );
      }
    } catch (e) {
      return ApiResponse(success: false, message: '网络错误: $e');
    }
  }

  /// 分享优惠券
  static Future<ApiResponse<String>> shareCoupon(String couponId) async {
    try {
      final token = await StorageUtil.getToken();

      final response = await _client.post(
        Uri.parse('${ApiConfig.baseUrl}/coupons/$couponId/share'),
        headers: {
          'Authorization': 'Bearer $token',
          'Content-Type': 'application/json',
        },
      );

      if (response.statusCode == 200) {
        final jsonData = json.decode(response.body);
        return ApiResponse(
          success: jsonData['success'] == true,
          data: jsonData['data'],
          message: jsonData['message'],
        );
      } else {
        return ApiResponse(
          success: false,
          message: '请求失败: ${response.statusCode}',
        );
      }
    } catch (e) {
      return ApiResponse(success: false, message: '网络错误: $e');
    }
  }
}

/// API响应封装
class ApiResponse<T> {
  final bool success;
  final T? data;
  final String? message;

  ApiResponse({
    required this.success,
    this.data,
    this.message,
  });
}
