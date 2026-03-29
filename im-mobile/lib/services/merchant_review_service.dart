import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/merchant_review.dart';
import '../models/page_result.dart';
import '../config/api_config.dart';

/// 商户评价服务
class MerchantReviewService {
  static final MerchantReviewService _instance = MerchantReviewService._internal();
  factory MerchantReviewService() => _instance;
  MerchantReviewService._internal();

  /// 创建评价
  Future<MerchantReview> createReview({
    required int merchantId,
    required double overallRating,
    String? content,
    List<String>? images,
    String? videoUrl,
    String? videoCover,
    double? tasteRating,
    double? environmentRating,
    double? serviceRating,
    double? valueRating,
    double? consumptionAmount,
    double? perCapitaCost,
    bool anonymous = false,
    DateTime? diningTime,
    int? diningPeople,
    List<String>? tags,
    int? experienceType,
  }) async {
    final body = {
      'merchantId': merchantId,
      'overallRating': overallRating,
      'content': content ?? '',
      if (images != null) 'images': images,
      if (videoUrl != null) 'videoUrl': videoUrl,
      if (videoCover != null) 'videoCover': videoCover,
      if (tasteRating != null) 'tasteRating': tasteRating,
      if (environmentRating != null) 'environmentRating': environmentRating,
      if (serviceRating != null) 'serviceRating': serviceRating,
      if (valueRating != null) 'valueRating': valueRating,
      if (consumptionAmount != null) 'consumptionAmount': consumptionAmount,
      if (perCapitaCost != null) 'perCapitaCost': perCapitaCost,
      'anonymous': anonymous,
      if (diningTime != null) 'diningTime': diningTime.toIso8601String(),
      if (diningPeople != null) 'diningPeople': diningPeople,
      if (tags != null) 'tags': tags,
      if (experienceType != null) 'experienceType': experienceType,
    };

    final response = await http.post(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/review/reviews'),
      headers: ApiConfig.headers,
      body: jsonEncode(body),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body)['data'];
      return MerchantReview.fromJson(data);
    } else {
      throw Exception('创建评价失败: ${response.body}');
    }
  }

  /// 获取评价详情
  Future<MerchantReview> getReviewDetail(int reviewId) async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/review/reviews/$reviewId'),
      headers: ApiConfig.headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body)['data'];
      return MerchantReview.fromJson(data);
    } else {
      throw Exception('获取评价详情失败');
    }
  }

  /// 获取商户评价列表
  Future<PageResult<MerchantReview>> getMerchantReviews({
    required int merchantId,
    ReviewSortType sort = ReviewSortType.latest,
    int page = 1,
    int size = 10,
  }) async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/review/merchant/$merchantId/reviews'
          '?sort=${sort.code}&page=$page&size=$size'),
      headers: ApiConfig.headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body)['data'];
      return PageResult<MerchantReview>.fromJson(
        data,
        (json) => MerchantReview.fromJson(json),
      );
    } else {
      throw Exception('获取商户评价失败');
    }
  }

  /// 获取用户的评价列表
  Future<PageResult<MerchantReview>> getUserReviews({
    int page = 1,
    int size = 10,
  }) async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/review/user/reviews'
          '?page=$page&size=$size'),
      headers: ApiConfig.headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body)['data'];
      return PageResult<MerchantReview>.fromJson(
        data,
        (json) => MerchantReview.fromJson(json),
      );
    } else {
      throw Exception('获取用户评价失败');
    }
  }

  /// 点赞评价
  Future<void> likeReview(int reviewId) async {
    final response = await http.post(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/review/reviews/$reviewId/like'),
      headers: ApiConfig.headers,
    );

    if (response.statusCode != 200) {
      throw Exception('点赞失败');
    }
  }

  /// 取消点赞
  Future<void> unlikeReview(int reviewId) async {
    final response = await http.delete(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/review/reviews/$reviewId/like'),
      headers: ApiConfig.headers,
    );

    if (response.statusCode != 200) {
      throw Exception('取消点赞失败');
    }
  }

  /// 删除评价
  Future<void> deleteReview(int reviewId) async {
    final response = await http.delete(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/review/reviews/$reviewId'),
      headers: ApiConfig.headers,
    );

    if (response.statusCode != 200) {
      throw Exception('删除评价失败');
    }
  }

  /// 获取商户评价统计
  Future<MerchantReviewStatistic> getMerchantStatistic(int merchantId) async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/review/merchant/$merchantId/statistic'),
      headers: ApiConfig.headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body)['data'];
      return MerchantReviewStatistic.fromJson(data);
    } else {
      throw Exception('获取商户统计失败');
    }
  }

  /// 获取有图评价
  Future<PageResult<MerchantReview>> getReviewsWithImage({
    required int merchantId,
    int page = 1,
    int size = 10,
  }) async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/review/merchant/$merchantId/reviews/with-image'
          '?page=$page&size=$size'),
      headers: ApiConfig.headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body)['data'];
      return PageResult<MerchantReview>.fromJson(
        data,
        (json) => MerchantReview.fromJson(json),
      );
    } else {
      throw Exception('获取有图评价失败');
    }
  }

  /// 获取视频评价
  Future<PageResult<MerchantReview>> getReviewsWithVideo({
    required int merchantId,
    int page = 1,
    int size = 10,
  }) async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/review/merchant/$merchantId/reviews/with-video'
          '?page=$page&size=$size'),
      headers: ApiConfig.headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body)['data'];
      return PageResult<MerchantReview>.fromJson(
        data,
        (json) => MerchantReview.fromJson(json),
      );
    } else {
      throw Exception('获取视频评价失败');
    }
  }

  /// 获取优质推荐评价
  Future<List<MerchantReview>> getRecommendedReviews({
    required int merchantId,
    int limit = 5,
  }) async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/review/merchant/$merchantId/reviews/recommended'
          '?limit=$limit'),
      headers: ApiConfig.headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body)['data'] as List;
      return data.map((json) => MerchantReview.fromJson(json)).toList();
    } else {
      throw Exception('获取推荐评价失败');
    }
  }

  /// 增加浏览数
  Future<void> incrementViewCount(int reviewId) async {
    await http.post(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/review/reviews/$reviewId/view'),
      headers: ApiConfig.headers,
    );
  }
}
