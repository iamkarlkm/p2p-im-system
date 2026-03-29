import 'dart:async';
import 'dart:convert';
import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:geolocator/geolocator.dart';

import '../../models/parking_lot.dart';
import '../../api/local_life_api.dart';
import '../geofence/service/geofence_service_manager.dart';
import '../geofence/models/geofence_model.dart';

/// 智能停车服务
/// 
/// 提供：
/// - 周边停车场搜索
/// - 空位预测
/// - 停车记录管理
/// - 反向寻车导航
/// - 缴费闭环
class ParkingService extends ChangeNotifier {
  static final ParkingService _instance = ParkingService._internal();
  factory ParkingService() => _instance;
  ParkingService._internal();

  final LocalLifeApi _api = LocalLifeApi();
  final GeofenceServiceManager _geofenceManager = GeofenceServiceManager();
  
  /// 附近停车场列表
  List<ParkingLot> _nearbyParkingLots = [];
  
  /// 当前停车记录
  ParkingRecord? _currentParking;
  
  /// 历史停车记录
  final List<ParkingRecord> _parkingHistory = [];
  
  /// 是否正在搜索
  bool _isSearching = false;
  
  /// 最后搜索位置
  Position? _lastSearchPosition;
  
  /// 搜索半径（米）
  double _searchRadius = 1000;
  
  /// 停车场围栏映射
  final Map<String, String> _parkingGeofenceMap = {};
  
  /// 事件流控制器
  final _eventController = StreamController<ParkingEvent>.broadcast();
  
  StreamSubscription<GeofenceTriggerEvent>? _geofenceSubscription;
  
  bool _initialized = false;

  // ==================== Getters ====================
  
  List<ParkingLot> get nearbyParkingLots => List.unmodifiable(_nearbyParkingLots);
  ParkingRecord? get currentParking => _currentParking;
  List<ParkingRecord> get parkingHistory => List.unmodifiable(_parkingHistory);
  bool get isSearching => _isSearching;
  Position? get lastSearchPosition => _lastSearchPosition;
  double get searchRadius => _searchRadius;
  Stream<ParkingEvent> get eventStream => _eventController.stream;

  // ==================== 初始化 ====================
  
  Future<void> initialize() async {
    if (_initialized) return;
    
    // 订阅地理围栏事件（用于自动检测停车）
    _geofenceSubscription = _geofenceManager.eventStream.listen(
      _handleGeofenceEvent,
    );
    
    // 加载当前停车记录
    await _loadCurrentParking();
    
    _initialized = true;
    debugPrint('ParkingService initialized');
  }

  // ==================== 停车场搜索 ====================
  
  /// 搜索附近停车场
  Future<List<ParkingLot>> searchNearbyParking({
    Position? location,
    double? radius,
    ParkingSearchFilter? filter,
  }) async {
    _isSearching = true;
    notifyListeners();
    
    try {
      Position? searchLocation = location;
      
      if (searchLocation == null) {
        searchLocation = await Geolocator.getCurrentPosition();
      }
      
      _lastSearchPosition = searchLocation;
      _searchRadius = radius ?? 1000;
      
      final results = await _api.searchNearbyParking(
        latitude: searchLocation.latitude,
        longitude: searchLocation.longitude,
        radius: _searchRadius,
        filter: filter,
      );
      
      _nearbyParkingLots = results;
      
      // 为停车场注册地理围栏（用于自动检测）
      await _registerParkingGeofences(results);
      
      return results;
    } catch (e) {
      debugPrint('Error searching parking: $e');
      return [];
    } finally {
      _isSearching = false;
      notifyListeners();
    }
  }
  
  /// 刷新附近停车场
  Future<void> refreshNearbyParking() async {
    if (_lastSearchPosition != null) {
      await searchNearbyParking(
        location: _lastSearchPosition,
        radius: _searchRadius,
      );
    }
  }
  
  /// 获取停车场详情
  Future<ParkingLot?> getParkingLotDetail(String parkingId) async {
    try {
      return await _api.getParkingLotDetail(parkingId);
    } catch (e) {
      debugPrint('Error getting parking detail: $e');
      return null;
    }
  }
  
  /// 预测空位数量
  Future<ParkingAvailabilityPrediction> predictAvailability(
    String parkingId, {
    DateTime? targetTime,
  }) async {
    try {
      return await _api.predictParkingAvailability(
        parkingId: parkingId,
        targetTime: targetTime ?? DateTime.now(),
      );
    } catch (e) {
      debugPrint('Error predicting availability: $e');
      return ParkingAvailabilityPrediction(
        parkingId: parkingId,
        predictedAvailable: 0,
        confidence: 0,
      );
    }
  }

  // ==================== 停车记录管理 ====================
  
  /// 开始停车记录
  Future<ParkingRecord> startParking({
    required String parkingLotId,
    String? parkingLotName,
    String? floor,
    String? area,
    String? spotNumber,
    String? photoPath,
    String? notes,
    Position? location,
  }) async {
    final record = ParkingRecord(
      id: 'park_${DateTime.now().millisecondsSinceEpoch}',
      parkingLotId: parkingLotId,
      parkingLotName: parkingLotName ?? '未知停车场',
      startTime: DateTime.now(),
      floor: floor,
      area: area,
      spotNumber: spotNumber,
      photoPath: photoPath,
      notes: notes,
      location: location != null
          ? ParkingLocation(
              latitude: location.latitude,
              longitude: location.longitude,
              accuracy: location.accuracy,
            )
          : null,
      status: ParkingStatus.parking,
    );
    
    _currentParking = record;
    
    // 保存到本地
    await _saveCurrentParking();
    
    // 上报服务器
    await _api.startParkingRecord(record);
    
    // 发送事件
    _eventController.add(ParkingEvent.started(record));
    
    notifyListeners();
    debugPrint('Parking started: ${record.id}');
    
    return record;
  }
  
  /// 更新停车记录（添加照片、备注等）
  Future<void> updateParkingRecord({
    String? floor,
    String? area,
    String? spotNumber,
    String? photoPath,
    String? notes,
  }) async {
    if (_currentParking == null) return;
    
    _currentParking = _currentParking!.copyWith(
      floor: floor,
      area: area,
      spotNumber: spotNumber,
      photoPath: photoPath ?? _currentParking!.photoPath,
      notes: notes,
    );
    
    await _saveCurrentParking();
    await _api.updateParkingRecord(_currentParking!);
    
    notifyListeners();
  }
  
  /// 结束停车
  Future<ParkingRecord> endParking({
    PaymentMethod? paymentMethod,
    String? couponId,
  }) async {
    if (_currentParking == null) {
      throw StateError('No active parking record');
    }
    
    final endTime = DateTime.now();
    final duration = endTime.difference(_currentParking!.startTime);
    
    // 计算费用
    final fee = await _calculateParkingFee(
      _currentParking!.parkingLotId,
      duration,
      couponId: couponId,
    );
    
    final completedRecord = _currentParking!.copyWith(
      endTime: endTime,
      duration: duration,
      fee: fee,
      status: ParkingStatus.completed,
      paymentMethod: paymentMethod,
      couponId: couponId,
    );
    
    // 添加到历史
    _parkingHistory.insert(0, completedRecord);
    if (_parkingHistory.length > 50) {
      _parkingHistory.removeLast();
    }
    
    // 清除当前停车
    _currentParking = null;
    await _clearCurrentParking();
    
    // 上报服务器
    await _api.completeParkingRecord(completedRecord);
    
    // 发送事件
    _eventController.add(ParkingEvent.ended(completedRecord));
    
    notifyListeners();
    debugPrint('Parking ended: ${completedRecord.id}');
    
    return completedRecord;
  }
  
  /// 计算停车费用
  Future<ParkingFee> _calculateParkingFee(
    String parkingLotId,
    Duration duration, {
    String? couponId,
  }) async {
    try {
      return await _api.calculateParkingFee(
        parkingLotId: parkingLotId,
        durationMinutes: duration.inMinutes,
        couponId: couponId,
      );
    } catch (e) {
      debugPrint('Error calculating fee: $e');
      return ParkingFee(
        baseAmount: 0,
        discountAmount: 0,
        finalAmount: 0,
        currency: 'CNY',
      );
    }
  }

  // ==================== 反向寻车 ====================
  
  /// 获取到停车点的导航
  Future<NavigationRoute> getNavigationToParking() async {
    if (_currentParking == null) {
      throw StateError('No active parking record');
    }
    
    if (_currentParking!.location == null) {
      throw StateError('Parking location not recorded');
    }
    
    final currentPosition = await Geolocator.getCurrentPosition();
    
    // 调用地图API获取导航路线
    final route = await _api.getWalkingNavigation(
      fromLat: currentPosition.latitude,
      fromLng: currentPosition.longitude,
      toLat: _currentParking!.location!.latitude,
      toLng: _currentParking!.location!.longitude,
    );
    
    return route;
  }
  
  /// 获取停车照片
  String? getParkingPhoto() {
    return _currentParking?.photoPath;
  }
  
  /// 获取停车位置文本描述
  String getParkingLocationDescription() {
    if (_currentParking == null) return '无停车记录';
    
    final parts = <String>[];
    if (_currentParking!.floor != null) {
      parts.add('${_currentParking!.floor}层');
    }
    if (_currentParking!.area != null) {
      parts.add('${_currentParking!.area}区');
    }
    if (_currentParking!.spotNumber != null) {
      parts.add('${_currentParking!.spotNumber}号车位');
    }
    
    return parts.isEmpty ? '位置未记录' : parts.join(' · ');
  }

  // ==================== 自动检测 ====================
  
  /// 注册停车场地理围栏
  Future<void> _registerParkingGeofences(List<ParkingLot> lots) async {
    for (final lot in lots) {
      // 检查是否已注册
      if (_parkingGeofenceMap.containsKey(lot.id)) continue;
      
      final geofence = Geofence(
        id: 'parking_${lot.id}',
        name: '${lot.name}停车场',
        type: GeofenceType.circle,
        latitude: lot.latitude,
        longitude: lot.longitude,
        radius: lot.radius ?? 100,
        triggers: const [GeofenceEvent.enter, GeofenceEvent.exit],
        dwellTime: 30000, // 30秒，用于快速进入停车场检测
        merchantId: lot.id,
        metadata: {
          'type': 'parking',
          'name': lot.name,
          'address': lot.address,
        },
        createdAt: DateTime.now(),
      );
      
      await _geofenceManager.registerGeofence(geofence);
      _parkingGeofenceMap[lot.id] = geofence.id;
    }
  }
  
  /// 处理地理围栏事件（自动停车检测）
  Future<void> _handleGeofenceEvent(GeofenceTriggerEvent event) async {
    final metadata = event.extraData;
    if (metadata == null || metadata['type'] != 'parking') return;
    
    switch (event.eventType) {
      case GeofenceEvent.enter:
        // 自动开始停车（可选，根据用户设置）
        debugPrint('Entered parking lot: ${metadata['name']}');
        _eventController.add(ParkingEvent.nearby(metadata['name'] as String));
        break;
        
      case GeofenceEvent.exit:
        // 如果当前在这个停车场停车，提醒用户
        if (_currentParking != null && 
            _currentParking!.parkingLotId == event.geofenceId.replaceFirst('parking_', '')) {
          _eventController.add(ParkingEvent.exited(_currentParking!));
        }
        break;
        
      default:
        break;
    }
  }

  // ==================== 支付 ====================
  
  /// 获取支付信息
  Future<PaymentInfo> getPaymentInfo({String? couponId}) async {
    if (_currentParking == null) {
      throw StateError('No active parking record');
    }
    
    final duration = DateTime.now().difference(_currentParking!.startTime);
    final fee = await _calculateParkingFee(
      _currentParking!.parkingLotId,
      duration,
      couponId: couponId,
    );
    
    return PaymentInfo(
      recordId: _currentParking!.id,
      duration: duration,
      fee: fee,
      availableCoupons: await _getAvailableCoupons(),
    );
  }
  
  /// 获取可用优惠券
  Future<List<ParkingCoupon>> _getAvailableCoupons() async {
    try {
      return await _api.getParkingCoupons();
    } catch (e) {
      debugPrint('Error getting coupons: $e');
      return [];
    }
  }
  
  /// 处理支付
  Future<PaymentResult> processPayment({
    required PaymentMethod method,
    String? couponId,
  }) async {
    final record = await endParking(
      paymentMethod: method,
      couponId: couponId,
    );
    
    return PaymentResult(
      success: true,
      record: record,
      message: '支付成功',
    );
  }

  // ==================== 持久化 ====================
  
  /// 保存当前停车记录
  Future<void> _saveCurrentParking() async {
    // 使用SharedPreferences或Hive保存
    // 简化实现
  }
  
  /// 加载当前停车记录
  Future<void> _loadCurrentParking() async {
    // 从本地存储加载
    // 简化实现
  }
  
  /// 清除当前停车记录
  Future<void> _clearCurrentParking() async {
    // 清除本地存储
    // 简化实现
  }

  // ==================== 共享停车 ====================
  
  /// 发布共享停车位
  Future<SharedParkingSpot> publishSharedSpot({
    required String location,
    required DateTime availableFrom,
    required DateTime availableTo,
    required double hourlyRate,
    String? description,
    List<String>? photos,
  }) async {
    final spot = SharedParkingSpot(
      id: 'shared_${DateTime.now().millisecondsSinceEpoch}',
      ownerId: 'current_user_id',
      location: location,
      availableFrom: availableFrom,
      availableTo: availableTo,
      hourlyRate: hourlyRate,
      description: description,
      photos: photos,
      status: SharedSpotStatus.available,
      createdAt: DateTime.now(),
    );
    
    await _api.publishSharedParkingSpot(spot);
    return spot;
  }
  
  /// 搜索共享停车位
  Future<List<SharedParkingSpot>> searchSharedSpots({
    required double latitude,
    required double longitude,
    double radius = 2000,
    DateTime? startTime,
    DateTime? endTime,
  }) async {
    try {
      return await _api.searchSharedParkingSpots(
        latitude: latitude,
        longitude: longitude,
        radius: radius,
        startTime: startTime,
        endTime: endTime,
      );
    } catch (e) {
      debugPrint('Error searching shared spots: $e');
      return [];
    }
  }

  @override
  void dispose() {
    _geofenceSubscription?.cancel();
    _eventController.close();
    super.dispose();
  }
}

// ==================== 数据模型 ====================

/// 停车搜索筛选
class ParkingSearchFilter {
  final double? minPrice;
  final double? maxPrice;
  final bool? hasEmptySpots;
  final bool? supportsReservation;
  final bool? supportsEVCharging;
  final List<String>? parkingTypes;

  ParkingSearchFilter({
    this.minPrice,
    this.maxPrice,
    this.hasEmptySpots,
    this.supportsReservation,
    this.supportsEVCharging,
    this.parkingTypes,
  });
}

/// 停车场空位预测
class ParkingAvailabilityPrediction {
  final String parkingId;
  final int predictedAvailable;
  final double confidence;
  final DateTime? predictionTime;

  ParkingAvailabilityPrediction({
    required this.parkingId,
    required this.predictedAvailable,
    required this.confidence,
    this.predictionTime,
  });
}

/// 停车事件
sealed class ParkingEvent {
  const ParkingEvent();
  
  factory ParkingEvent.started(ParkingRecord record) = ParkingStartedEvent;
  factory ParkingEvent.ended(ParkingRecord record) = ParkingEndedEvent;
  factory ParkingEvent.nearby(String parkingName) = ParkingNearbyEvent;
  factory ParkingEvent.exited(ParkingRecord record) = ParkingExitedEvent;
  factory ParkingEvent.paid(PaymentResult result) = ParkingPaidEvent;
}

class ParkingStartedEvent extends ParkingEvent {
  final ParkingRecord record;
  const ParkingStartedEvent(this.record);
}

class ParkingEndedEvent extends ParkingEvent {
  final ParkingRecord record;
  const ParkingEndedEvent(this.record);
}

class ParkingNearbyEvent extends ParkingEvent {
  final String parkingName;
  const ParkingNearbyEvent(this.parkingName);
}

class ParkingExitedEvent extends ParkingEvent {
  final ParkingRecord record;
  const ParkingExitedEvent(this.record);
}

class ParkingPaidEvent extends ParkingEvent {
  final PaymentResult result;
  const ParkingPaidEvent(this.result);
}

/// 支付方式
enum PaymentMethod {
  wechat,
  alipay,
  unionPay,
  applePay,
  autoDeduction,
}

/// 支付信息
class PaymentInfo {
  final String recordId;
  final Duration duration;
  final ParkingFee fee;
  final List<ParkingCoupon> availableCoupons;

  PaymentInfo({
    required this.recordId,
    required this.duration,
    required this.fee,
    required this.availableCoupons,
  });
}

/// 支付结果
class PaymentResult {
  final bool success;
  final ParkingRecord? record;
  final String message;

  PaymentResult({
    required this.success,
    this.record,
    required this.message,
  });
}

/// 导航路线占位
class NavigationRoute {
  final double distance;
  final int duration; // 秒
  final List<RouteStep> steps;

  NavigationRoute({
    required this.distance,
    required this.duration,
    required this.steps,
  });
}

class RouteStep {
  final String instruction;
  final double distance;
  final int duration;

  RouteStep({
    required this.instruction,
    required this.distance,
    required this.duration,
  });
}
