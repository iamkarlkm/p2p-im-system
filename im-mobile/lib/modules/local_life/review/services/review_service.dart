import 'package:dio/dio.dart';
import '../../../core/services/http_service.dart';
import '../models/review_model.dart';

/// 评价服务
class ReviewService {
  final HttpService _http = HttpService();

  /// 获取商户评价列表
  Future<PageResult<ReviewDetail>> getMerchantReviews({
    required String merchantId,
    String? reviewType,
    String? sortBy,
    required int pageNum,
    required int pageSize,
  }) async {
    final response = await _http.get(
      '/api/v1/reviews/merchant/$merchantId',
      queryParameters: {
        'reviewType': reviewType ?? 'ALL',
        'sortBy': sortBy ?? 'NEWEST',
        'pageNum': pageNum,
        'pageSize': pageSize,
      },
    );

    return PageResult.fromJson(
      response.data,
      (json) => ReviewDetail.fromJson(json),
    );
  }

  /// 获取评价详情
  Future<ReviewDetail> getReviewDetail(String reviewId) async {
    final response = await _http.get('/api/v1/reviews/$reviewId');
    return ReviewDetail.fromJson(response.data);
  }

  /// 提交评价
  Future<void> submitReview({
    required String merchantId,
    String? orderId,
    required int overallRating,
    required int tasteRating,
    required int environmentRating,
    required int serviceRating,
    required int valueRating,
    required String content,
    List<String>? images,
    String? videoUrl,
    List<String>? tags,
    double? consumeAmount,
    bool? anonymous,
    bool? recommended,
  }) async {
    await _http.post('/api/v1/reviews', data: {
      'merchantId': merchantId,
      'orderId': orderId,
      'overallRating': overallRating,
      'tasteRating': tasteRating,
      'environmentRating': environmentRating,
      'serviceRating': serviceRating,
      'valueRating': valueRating,
      'content': content,
      'images': images ?? [],
      'videoUrl': videoUrl,
      'tags': tags ?? [],
      'consumeAmount': consumeAmount,
      'anonymous': anonymous,
      'recommended': recommended,
    });
  }

  /// 点赞/取消点赞
  Future<void> likeReview(String reviewId, bool like) async {
    await _http.post('/api/v1/reviews/like', data: {
      'reviewId': reviewId,
      'like': like,
    });
  }

  /// 获取商户口碑统计
  Future<ReputationStatistics> getMerchantReputation(String merchantId) async {
    final response = await _http.get('/api/v1/reputation/merchant/$merchantId');
    return ReputationStatistics.fromJson(response.data);
  }

  /// 获取评价回复列表
  Future<PageResult<ReviewReply>> getReviewReplies({
    required String reviewId,
    required int pageNum,
    required int pageSize,
  }) async {
    final response = await _http.get(
      '/api/v1/reviews/$reviewId/replies',
      queryParameters: {
        'pageNum': pageNum,
        'pageSize': pageSize,
      },
    );

    return PageResult.fromJson(
      response.data,
      (json) => ReviewReply.fromJson(json),
    );
  }

  /// 回复评价
  Future<void> replyReview({
    required String reviewId,
    String? parentReplyId,
    required String content,
  }) async {
    await _http.post('/api/v1/reviews/reply', data: {
      'reviewId': reviewId,
      'parentReplyId': parentReplyId,
      'content': content,
    });
  }

  /// 删除评价
  Future<void> deleteReview(String reviewId) async {
    await _http.delete('/api/v1/reviews/$reviewId');
  }

  /// 举报评价
  Future<void> reportReview({
    required String reviewId,
    required String reason,
    String? description,
  }) async {
    await _http.post('/api/v1/reviews/$reviewId/report', data: {
      'reason': reason,
      'description': description,
    });
  }

  /// 获取我的评价列表
  Future<PageResult<ReviewDetail>> getMyReviews({
    required int pageNum,
    required int pageSize,
  }) async {
    final response = await _http.get(
      '/api/v1/reviews/my',
      queryParameters: {
        'pageNum': pageNum,
        'pageSize': pageSize,
      },
    );

    return PageResult.fromJson(
      response.data,
      (json) => ReviewDetail.fromJson(json),
    );
  }
}

class ReviewReply {
  final String replyId;
  final String? parentReplyId;
  final ReplyUser user;
  final String content;
  final String replyTime;
  final int likeCount;
  final bool isMerchantReply;
  final bool isOfficialReply;

  ReviewReply({
    required this.replyId,
    this.parentReplyId,
    required this.user,
    required this.content,
    required this.replyTime,
    required this.likeCount,
    required this.isMerchantReply,
    required this.isOfficialReply,
  });

  factory ReviewReply.fromJson(Map<String, dynamic> json) {
    return ReviewReply(
      replyId: json['replyId'].toString(),
      parentReplyId: json['parentReplyId']?.toString(),
      user: ReplyUser.fromJson(json['user']),
      content: json['content'],
      replyTime: json['replyTime'],
      likeCount: json['likeCount'] ?? 0,
      isMerchantReply: json['merchantReply'] ?? false,
      isOfficialReply: json['officialReply'] ?? false,
    );
  }
}

class ReplyUser {
  final String userId;
  final String nickname;
  final String avatar;
  final int level;

  ReplyUser({
    required this.userId,
    required this.nickname,
    required this.avatar,
    required this.level,
  });

  factory ReplyUser.fromJson(Map<String, dynamic> json) {
    return ReplyUser(
      userId: json['userId'].toString(),
      nickname: json['nickname'],
      avatar: json['avatar'] ?? '',
      level: json['level'] ?? 0,
    );
  }
}
