import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../models/coupon_model.dart';
import '../services/coupon_service.dart';

/// 附近优惠券状态
class CouponListState {
  final List<CouponModel> coupons;
  final bool isLoading;
  final String? error;
  final bool hasMore;
  final int currentPage;

  const CouponListState({
    this.coupons = const [],
    this.isLoading = false,
    this.error,
    this.hasMore = true,
    this.currentPage = 0,
  });

  CouponListState copyWith({
    List<CouponModel>? coupons,
    bool? isLoading,
    String? error,
    bool? hasMore,
    int? currentPage,
  }) {
    return CouponListState(
      coupons: coupons ?? this.coupons,
      isLoading: isLoading ?? this.isLoading,
      error: error,
      hasMore: hasMore ?? this.hasMore,
      currentPage: currentPage ?? this.currentPage,
    );
  }
}

/// 附近优惠券Provider
final nearbyCouponsProvider = StateNotifierProvider<NearbyCouponsNotifier, CouponListState>((ref) {
  return NearbyCouponsNotifier();
});

/// 附近优惠券Notifier
class NearbyCouponsNotifier extends StateNotifier<CouponListState> {
  NearbyCouponsNotifier() : super(const CouponListState());

  /// 加载附近优惠券
  Future<void> loadNearbyCoupons({
    required double lat,
    required double lng,
    String? category,
    String sortBy = 'DISTANCE',
    int page = 0,
    int size = 20,
    String? keyword,
  }) async {
    if (state.isLoading) return;

    state = state.copyWith(isLoading: true, error: null);

    try {
      final response = await CouponService.getNearbyCoupons(
        lat: lat,
        lng: lng,
        category: category,
        sortBy: sortBy,
        page: page,
        size: size,
        keyword: keyword,
      );

      if (response.success) {
        final newCoupons = response.data ?? [];
        final allCoupons = page == 0 ? newCoupons : [...state.coupons, ...newCoupons];
        
        state = state.copyWith(
          coupons: allCoupons,
          isLoading: false,
          hasMore: newCoupons.length >= size,
          currentPage: page,
        );
      } else {
        state = state.copyWith(
          isLoading: false,
          error: response.message ?? '加载失败',
        );
      }
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.toString(),
      );
    }
  }

  /// 刷新列表
  Future<void> refresh(double lat, double lng) async {
    await loadNearbyCoupons(lat: lat, lng: lng, page: 0);
  }

  /// 加载更多
  Future<void> loadMore(double lat, double lng) async {
    if (!state.hasMore || state.isLoading) return;
    await loadNearbyCoupons(
      lat: lat,
      lng: lng,
      page: state.currentPage + 1,
    );
  }
}

/// 我的优惠券状态
class MyCouponState {
  final List<UserCouponModel> unusedCoupons;
  final List<UserCouponModel> usedCoupons;
  final List<UserCouponModel> expiredCoupons;
  final bool isLoading;
  final String? error;

  const MyCouponState({
    this.unusedCoupons = const [],
    this.usedCoupons = const [],
    this.expiredCoupons = const [],
    this.isLoading = false,
    this.error,
  });

  MyCouponState copyWith({
    List<UserCouponModel>? unusedCoupons,
    List<UserCouponModel>? usedCoupons,
    List<UserCouponModel>? expiredCoupons,
    bool? isLoading,
    String? error,
  }) {
    return MyCouponState(
      unusedCoupons: unusedCoupons ?? this.unusedCoupons,
      usedCoupons: usedCoupons ?? this.usedCoupons,
      expiredCoupons: expiredCoupons ?? this.expiredCoupons,
      isLoading: isLoading ?? this.isLoading,
      error: error,
    );
  }

  /// 获取指定状态的优惠券数量
  int getCountByStatus(String status) {
    switch (status) {
      case 'UNUSED':
        return unusedCoupons.length;
      case 'USED':
        return usedCoupons.length;
      case 'EXPIRED':
        return expiredCoupons.length;
      default:
        return 0;
    }
  }
}

/// 我的优惠券Provider
final myCouponsProvider = StateNotifierProvider<MyCouponsNotifier, MyCouponState>((ref) {
  return MyCouponsNotifier();
});

/// 我的优惠券Notifier
class MyCouponsNotifier extends StateNotifier<MyCouponState> {
  MyCouponsNotifier() : super(const MyCouponState());

  /// 加载我的优惠券
  Future<void> loadMyCoupons() async {
    state = state.copyWith(isLoading: true, error: null);

    try {
      // 并行加载三种状态的优惠券
      final results = await Future.wait([
        CouponService.getMyCoupons(status: 'UNUSED'),
        CouponService.getMyCoupons(status: 'USED'),
        CouponService.getMyCoupons(status: 'EXPIRED'),
      ]);

      final unusedResult = results[0];
      final usedResult = results[1];
      final expiredResult = results[2];

      if (unusedResult.success && usedResult.success && expiredResult.success) {
        state = state.copyWith(
          unusedCoupons: unusedResult.data ?? [],
          usedCoupons: usedResult.data ?? [],
          expiredCoupons: expiredResult.data ?? [],
          isLoading: false,
        );
      } else {
        state = state.copyWith(
          isLoading: false,
          error: '加载失败',
        );
      }
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.toString(),
      );
    }
  }

  /// 添加新领取的优惠券
  void addCoupon(UserCouponModel coupon) {
    final updatedList = [coupon, ...state.unusedCoupons];
    state = state.copyWith(unusedCoupons: updatedList);
  }

  /// 标记优惠券为已使用
  void markAsUsed(String userCouponId) {
    final coupon = state.unusedCoupons.firstWhere(
      (c) => c.userCouponId == userCouponId,
      orElse: () => null as UserCouponModel,
    );
    
    if (coupon != null) {
      final updatedUnused = state.unusedCoupons
          .where((c) => c.userCouponId != userCouponId)
          .toList();
      final updatedUsed = [coupon.copyWith(status: 'USED'), ...state.usedCoupons];
      
      state = state.copyWith(
        unusedCoupons: updatedUnused,
        usedCoupons: updatedUsed,
      );
    }
  }
}

/// 优惠券操作状态
class CouponActionState {
  final bool isLoading;
  final String? error;
  final String? successMessage;

  const CouponActionState({
    this.isLoading = false,
    this.error,
    this.successMessage,
  });

  CouponActionState copyWith({
    bool? isLoading,
    String? error,
    String? successMessage,
  }) {
    return CouponActionState(
      isLoading: isLoading ?? this.isLoading,
      error: error,
      successMessage: successMessage,
    );
  }
}

/// 优惠券操作Provider
final couponActionProvider = StateNotifierProvider<CouponActionNotifier, CouponActionState>((ref) {
  return CouponActionNotifier();
});

/// 优惠券操作Notifier
class CouponActionNotifier extends StateNotifier<CouponActionState> {
  CouponActionNotifier() : super(const CouponActionState());

  /// 领取优惠券
  Future<void> claimCoupon(String couponId) async {
    state = state.copyWith(isLoading: true, error: null, successMessage: null);

    try {
      final response = await CouponService.claimCoupon(couponId);

      if (response.success) {
        state = state.copyWith(
          isLoading: false,
          successMessage: '领取成功',
        );
      } else {
        state = state.copyWith(
          isLoading: false,
          error: response.message ?? '领取失败',
        );
      }
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.toString(),
      );
    }
  }

  /// 转赠优惠券
  Future<void> transferCoupon(String userCouponId, String targetUserId, {String? message}) async {
    state = state.copyWith(isLoading: true, error: null, successMessage: null);

    try {
      final response = await CouponService.transferCoupon(
        userCouponId: userCouponId,
        targetUserId: targetUserId,
        message: message,
      );

      if (response.success) {
        state = state.copyWith(
          isLoading: false,
          successMessage: '转赠成功',
        );
      } else {
        state = state.copyWith(
          isLoading: false,
          error: response.message ?? '转赠失败',
        );
      }
    } catch (e) {
      state = state.copyWith(
        isLoading: false,
        error: e.toString(),
      );
    }
  }

  /// 分享优惠券
  Future<String?> shareCoupon(String couponId) async {
    state = state.copyWith(isLoading: true, error: null);

    try {
      final response = await CouponService.shareCoupon(couponId);

      state = state.copyWith(isLoading: false);
      
      if (response.success) {
        return response.data;
      } else {
        state = state.copyWith(error: response.message ?? '分享失败');
        return null;
      }
    } catch (e) {
      state = state.copyWith(isLoading: false, error: e.toString());
      return null;
    }
  }

  /// 清除状态
  void clearState() {
    state = const CouponActionState();
  }
}
