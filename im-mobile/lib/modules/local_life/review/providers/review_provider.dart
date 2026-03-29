import 'package:flutter/foundation.dart';
import '../models/review_model.dart';
import '../services/review_service.dart';

/// 评价Provider
/// 管理评价相关的状态和操作
class ReviewProvider extends ChangeNotifier {
  final ReviewService _reviewService = ReviewService();

  // 评价列表状态
  List<ReviewDetail> _reviews = [];
  List<ReviewDetail> get reviews => _reviews;

  // 分页状态
  int _currentPage = 1;
  int _totalPages = 1;
  bool _hasMore = true;
  bool get hasMore => _hasMore;

  // 加载状态
  bool _isLoading = false;
  bool get isLoading => _isLoading;

  // 当前评价详情
  ReviewDetail? _currentReview;
  ReviewDetail? get currentReview => _currentReview;

  // 口碑统计
  ReputationStatistics? _reputationStats;
  ReputationStatistics? get reputationStats => _reputationStats;

  // 错误信息
  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  /// 获取商户评价列表
  Future<void> loadMerchantReviews({
    required String merchantId,
    String? reviewType,
    String? sortBy,
    bool refresh = false,
  }) async {
    if (_isLoading) return;

    if (refresh) {
      _currentPage = 1;
      _reviews = [];
      _hasMore = true;
    }

    if (!_hasMore && !refresh) return;

    _isLoading = true;
    notifyListeners();

    try {
      final result = await _reviewService.getMerchantReviews(
        merchantId: merchantId,
        reviewType: reviewType,
        sortBy: sortBy,
        pageNum: _currentPage,
        pageSize: 10,
      );

      if (refresh) {
        _reviews = result.list;
      } else {
        _reviews.addAll(result.list);
      }

      _totalPages = result.totalPages;
      _hasMore = _currentPage < _totalPages;
      _currentPage++;
      _errorMessage = null;
    } catch (e) {
      _errorMessage = e.toString();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// 获取评价详情
  Future<ReviewDetail> getReviewDetail(String reviewId) async {
    try {
      final review = await _reviewService.getReviewDetail(reviewId);
      _currentReview = review;
      notifyListeners();
      return review;
    } catch (e) {
      _errorMessage = e.toString();
      throw e;
    }
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
    try {
      await _reviewService.submitReview(
        merchantId: merchantId,
        orderId: orderId,
        overallRating: overallRating,
        tasteRating: tasteRating,
        environmentRating: environmentRating,
        serviceRating: serviceRating,
        valueRating: valueRating,
        content: content,
        images: images,
        videoUrl: videoUrl,
        tags: tags,
        consumeAmount: consumeAmount,
        anonymous: anonymous,
        recommended: recommended,
      );
      _errorMessage = null;
    } catch (e) {
      _errorMessage = e.toString();
      throw e;
    }
  }

  /// 点赞/取消点赞
  Future<void> likeReview(String reviewId, bool like) async {
    try {
      await _reviewService.likeReview(reviewId, like);
      
      // 更新本地状态
      final index = _reviews.indexWhere((r) => r.reviewId == reviewId);
      if (index != -1) {
        _reviews[index] = _reviews[index].copyWith(
          hasLiked: like,
          likeCount: like 
              ? _reviews[index].likeCount + 1 
              : _reviews[index].likeCount - 1,
        );
        notifyListeners();
      }
      
      if (_currentReview?.reviewId == reviewId) {
        _currentReview = _currentReview!.copyWith(
          hasLiked: like,
          likeCount: like 
              ? _currentReview!.likeCount + 1 
              : _currentReview!.likeCount - 1,
        );
        notifyListeners();
      }
    } catch (e) {
      _errorMessage = e.toString();
      throw e;
    }
  }

  /// 获取商户口碑统计
  Future<void> loadReputationStats(String merchantId) async {
    try {
      final stats = await _reviewService.getMerchantReputation(merchantId);
      _reputationStats = stats;
      notifyListeners();
    } catch (e) {
      _errorMessage = e.toString();
    }
  }

  /// 加载更多评价
  Future<void> loadMore(String merchantId) async {
    if (_isLoading || !_hasMore) return;
    await loadMerchantReviews(merchantId: merchantId);
  }

  /// 刷新评价列表
  Future<void> refresh(String merchantId) async {
    await loadMerchantReviews(merchantId: merchantId, refresh: true);
  }

  /// 清除错误
  void clearError() {
    _errorMessage = null;
    notifyListeners();
  }
}
