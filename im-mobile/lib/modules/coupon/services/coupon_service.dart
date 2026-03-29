import 'package:dio/dio.dart';
import 'package:im_mobile/core/network/api_client.dart';
import 'package:im_mobile/core/result/api_result.dart';
import 'package:im_mobile/modules/coupon/models/coupon.dart';

/// 优惠券服务
/// 提供优惠券查询、领取、使用等功能
class CouponService {
  final ApiClient _apiClient = ApiClient();

  static const String _baseUrl = '/api/v1/coupon';

  /// 获取优惠券详情
  Future<ApiResult<Coupon>> getCouponById(int id) async {
    try {
      final response = await _apiClient.get('$_baseUrl/coupons/$id');
      return ApiResult.success(Coupon.fromJson(response.data));
    } on DioException catch (e) {
      return ApiResult.failure(e.message ?? '获取优惠券失败');
    }
  }

  /// 获取附近优惠券列表
  Future<ApiResult<List<Coupon>>> getNearbyCoupons({
    required double lat,
    required double lng,
    double radius = 5000,
    int page = 1,
    int size = 20,
  }) async {
    try {
      final response = await _apiClient.get(
        '$_baseUrl/coupons/nearby',
        queryParameters: {
          'lat': lat,
          'lng': lng,
          'radius': radius,
          'page': page,
          'size': size,
        },
      );
      
      final List<dynamic> data = response.data['records'] ?? [];
      final coupons = data.map((e) => Coupon.fromJson(e)).toList();
      return ApiResult.success(coupons);
    } on DioException catch (e) {
      return ApiResult.failure(e.message ?? '获取附近优惠券失败');
    }
  }

  /// 获取商户优惠券列表
  Future<ApiResult<List<Coupon>>> getMerchantCoupons(int merchantId) async {
    try {
      final response = await _apiClient.get(
        '$_baseUrl/merchant/$merchantId/coupons',
      );
      
      final List<dynamic> data = response.data ?? [];
      final coupons = data.map((e) => Coupon.fromJson(e)).toList();
      return ApiResult.success(coupons);
    } on DioException catch (e) {
      return ApiResult.failure(e.message ?? '获取商户优惠券失败');
    }
  }

  /// 领取优惠券
  Future<ApiResult<UserCoupon>> receiveCoupon(ReceiveCouponRequest request) async {
    try {
      final response = await _apiClient.post(
        '$_baseUrl/coupons/receive',
        data: request.toJson(),
      );
      return ApiResult.success(UserCoupon.fromJson(response.data));
    } on DioException catch (e) {
      return ApiResult.failure(e.message ?? '领取优惠券失败');
    }
  }

  /// 获取我的优惠券列表
  Future<ApiResult<List<UserCoupon>>> getUserCoupons() async {
    try {
      final response = await _apiClient.get('$_baseUrl/user/coupons');
      
      final List<dynamic> data = response.data ?? [];
      final coupons = data.map((e) => UserCoupon.fromJson(e)).toList();
      return ApiResult.success(coupons);
    } on DioException catch (e) {
      return ApiResult.failure(e.message ?? '获取优惠券列表失败');
    }
  }

  /// 获取可用优惠券列表
  Future<ApiResult<List<UserCoupon>>> getUsableCoupons({
    int page = 1,
    int size = 20,
  }) async {
    try {
      final response = await _apiClient.get(
        '$_baseUrl/user/coupons/usable',
        queryParameters: {'page': page, 'size': size},
      );
      
      final List<dynamic> data = response.data['records'] ?? [];
      final coupons = data.map((e) => UserCoupon.fromJson(e)).toList();
      return ApiResult.success(coupons);
    } on DioException catch (e) {
      return ApiResult.failure(e.message ?? '获取可用优惠券失败');
    }
  }

  /// 获取即将过期的优惠券
  Future<ApiResult<List<UserCoupon>>> getExpiringSoonCoupons() async {
    try {
      final response = await _apiClient.get('$_baseUrl/user/coupons/expiring-soon');
      
      final List<dynamic> data = response.data ?? [];
      final coupons = data.map((e) => UserCoupon.fromJson(e)).toList();
      return ApiResult.success(coupons);
    } on DioException catch (e) {
      return ApiResult.failure(e.message ?? '获取即将过期优惠券失败');
    }
  }

  /// 使用优惠券
  Future<ApiResult<double>> useCoupon(UseCouponRequest request) async {
    try {
      final response = await _apiClient.post(
        '$_baseUrl/coupons/use',
        data: request.toJson(),
      );
      return ApiResult.success((response.data as num).toDouble());
    } on DioException catch (e) {
      return ApiResult.failure(e.message ?? '使用优惠券失败');
    }
  }

  /// 计算订单可用优惠券
  Future<ApiResult<List<UserCoupon>>> calculateAvailableCoupons({
    required double orderAmount,
    int? merchantId,
  }) async {
    try {
      final response = await _apiClient.get(
        '$_baseUrl/coupons/available',
        queryParameters: {
          'orderAmount': orderAmount,
          if (merchantId != null) 'merchantId': merchantId,
        },
      );
      
      final List<dynamic> data = response.data ?? [];
      final coupons = data.map((e) => UserCoupon.fromJson(e)).toList();
      return ApiResult.success(coupons);
    } on DioException catch (e) {
      return ApiResult.failure(e.message ?? '计算可用优惠券失败');
    }
  }

  /// 检查优惠券是否可用
  Future<ApiResult<bool>> checkCouponUsable({
    required int userCouponId,
    required double orderAmount,
  }) async {
    try {
      final response = await _apiClient.get(
        '$_baseUrl/coupons/$userCouponId/check',
        queryParameters: {'orderAmount': orderAmount},
      );
      return ApiResult.success(response.data as bool);
    } on DioException catch (e) {
      return ApiResult.failure(e.message ?? '检查优惠券失败');
    }
  }

  /// 快速领取优惠券（简化版）
  Future<ApiResult<UserCoupon>> quickReceive(int couponId, {double? lat, double? lng}) async {
    return receiveCoupon(ReceiveCouponRequest(
      couponId: couponId,
      receiveChannel: 1,
      latitude: lat,
      longitude: lng,
    ));
  }

  /// 获取最优优惠券
  Future<UserCoupon?> getBestCoupon(double orderAmount, {int? merchantId}) async {
    final result = await calculateAvailableCoupons(
      orderAmount: orderAmount,
      merchantId: merchantId,
    );
    
    return result.when(
      success: (coupons) => coupons.isNotEmpty ? coupons.first : null,
      failure: (_) => null,
    );
  }

  /// 批量领取优惠券（用于系统发放）
  Future<ApiResult<List<UserCoupon>>> batchReceiveCoupons(List<int> couponIds) async {
    final List<UserCoupon> results = [];
    
    for (final id in couponIds) {
      final result = await receiveCoupon(ReceiveCouponRequest(
        couponId: id,
        receiveChannel: 2, // 系统发放
      ));
      
      result.when(
        success: (coupon) => results.add(coupon),
        failure: (_) {}, // 忽略单个失败
      );
    }
    
    return ApiResult.success(results);
  }
}

/// 优惠券状态管理Provider
class CouponProvider extends ChangeNotifier {
  final CouponService _service = CouponService();
  
  List<Coupon> _nearbyCoupons = [];
  List<UserCoupon> _userCoupons = [];
  List<UserCoupon> _usableCoupons = [];
  bool _isLoading = false;
  String? _error;

  List<Coupon> get nearbyCoupons => _nearbyCoupons;
  List<UserCoupon> get userCoupons => _userCoupons;
  List<UserCoupon> get usableCoupons => _usableCoupons;
  bool get isLoading => _isLoading;
  String? get error => _error;

  /// 加载附近优惠券
  Future<void> loadNearbyCoupons(double lat, double lng) async {
    _isLoading = true;
    _error = null;
    notifyListeners();

    final result = await _service.getNearbyCoupons(lat: lat, lng: lng);
    
    result.when(
      success: (coupons) {
        _nearbyCoupons = coupons;
        _isLoading = false;
      },
      failure: (msg) {
        _error = msg;
        _isLoading = false;
      },
    );
    notifyListeners();
  }

  /// 加载用户优惠券
  Future<void> loadUserCoupons() async {
    _isLoading = true;
    notifyListeners();

    final result = await _service.getUserCoupons();
    
    result.when(
      success: (coupons) {
        _userCoupons = coupons;
        _isLoading = false;
      },
      failure: (msg) {
        _error = msg;
        _isLoading = false;
      },
    );
    notifyListeners();
  }

  /// 领取优惠券
  Future<bool> receiveCoupon(int couponId) async {
    final result = await _service.quickReceive(couponId);
    
    return result.when(
      success: (coupon) {
        // 更新附近优惠券的领取状态
        final index = _nearbyCoupons.indexWhere((c) => c.id == couponId);
        if (index != -1) {
          _nearbyCoupons[index] = _nearbyCoupons[index].copyWith(
            hasReceived: true,
          );
        }
        notifyListeners();
        return true;
      },
      failure: (_) => false,
    );
  }
}
