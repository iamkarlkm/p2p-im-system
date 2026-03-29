import 'dart:async';
import 'dart:convert';
import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:flutter/material.dart';
import 'package:im_mobile/models/location/poi_model.dart';
import 'package:im_mobile/models/navigation/route_plan_model.dart';
import 'package:im_mobile/models/navigation/navigation_config.dart';
import 'package:im_mobile/services/location/location_service.dart';
import 'package:im_mobile/utils/coordinate_utils.dart';
import 'package:im_mobile/utils/logger.dart';

/// 导航服务类型枚举
enum NavigationProvider {
  amap,      // 高德地图
  tencent,   // 腾讯地图
  baidu,     // 百度地图
  system,    // 系统地图
}

/// 导航模式枚举
enum NavigationMode {
  driving,     // 驾车
  walking,     // 步行
  riding,      // 骑行
  transit,     // 公交
  truck,       // 货车
}

/// 导航服务管理器
/// 
/// 提供智能路线规划、实时导航、位置追踪等功能
/// 支持高德、腾讯、百度等多地图服务商
class NavigationService extends ChangeNotifier {
  static final NavigationService _instance = NavigationService._internal();
  factory NavigationService() => _instance;
  NavigationService._internal();

  // ==================== 依赖服务 ====================
  final LocationService _locationService = LocationService();
  
  // ==================== 状态管理 ====================
  NavigationProvider _currentProvider = NavigationProvider.amap;
  NavigationMode _currentMode = NavigationMode.driving;
  bool _isNavigating = false;
  bool _isOfflineMode = false;
  
  // 当前路线
  RoutePlanModel? _currentRoute;
  List<RoutePlanModel> _alternativeRoutes = [];
  
  // 导航状态
  int _currentStepIndex = 0;
  double _remainingDistance = 0.0;
  int _remainingDuration = 0;
  DateTime? _navigationStartTime;
  
  // 实时路况
  TrafficCondition _trafficCondition = TrafficCondition.smooth;
  
  // 位置流订阅
  StreamSubscription? _locationSubscription;
  
  // ==================== 缓存管理 ====================
  final Map<String, List<RoutePlanModel>> _routeCache = {};
  final Map<String, OfflineMapRegion> _offlineRegions = {};
  
  // ==================== 事件流 ====================
  final StreamController<NavigationEvent> _eventController = 
      StreamController<NavigationEvent>.broadcast();
  Stream<NavigationEvent> get eventStream => _eventController.stream;
  
  // ==================== Getters ====================
  NavigationProvider get currentProvider => _currentProvider;
  NavigationMode get currentMode => _currentMode;
  bool get isNavigating => _isNavigating;
  bool get isOfflineMode => _isOfflineMode;
  RoutePlanModel? get currentRoute => _currentRoute;
  List<RoutePlanModel> get alternativeRoutes => _alternativeRoutes;
  int get currentStepIndex => _currentStepIndex;
  double get remainingDistance => _remainingDistance;
  int get remainingDuration => _remainingDuration;
  TrafficCondition get trafficCondition => _trafficCondition;
  
  /// 初始化导航服务
  Future<void> initialize() async {
    IMLogger.i('NavigationService', 'Initializing navigation service...');
    
    await _locationService.initialize();
    await _loadOfflineRegions();
    
    IMLogger.i('NavigationService', 'Navigation service initialized');
  }
  
  /// 设置导航服务商
  void setProvider(NavigationProvider provider) {
    if (_currentProvider != provider) {
      _currentProvider = provider;
      notifyListeners();
      IMLogger.i('NavigationService', 'Provider changed to: $provider');
    }
  }
  
  /// 设置导航模式
  void setMode(NavigationMode mode) {
    if (_currentMode != mode) {
      _currentMode = mode;
      notifyListeners();
      IMLogger.i('NavigationService', 'Mode changed to: $mode');
    }
  }
  
  // ==================== 路线规划 ====================
  
  /// 单目的地路线规划
  /// 
  /// [destination] 目的地POI
  /// [start] 起点坐标（可选，默认当前位置）
  /// [waypoints] 途经点列表
  /// [avoidCongestion] 是否避开拥堵
  /// [avoidTolls] 是否避开收费
  /// [avoidHighways] 是否避开高速
  Future<List<RoutePlanModel>> planRoute({
    required POIModel destination,
    LatLng? start,
    List<LatLng>? waypoints,
    bool avoidCongestion = true,
    bool avoidTolls = false,
    bool avoidHighways = false,
  }) async {
    try {
      final origin = start ?? await _locationService.getCurrentLocation();
      
      final cacheKey = _generateCacheKey(
        origin: origin,
        destination: destination.location,
        mode: _currentMode,
      );
      
      // 检查缓存
      if (_routeCache.containsKey(cacheKey)) {
        IMLogger.i('NavigationService', 'Returning cached routes');
        return _routeCache[cacheKey]!;
      }
      
      IMLogger.i('NavigationService', 
        'Planning route from (${origin.latitude}, ${origin.longitude}) to '
        '(${destination.location.latitude}, ${destination.location.longitude})'
      );
      
      // 调用对应服务商API
      List<RoutePlanModel> routes = await _planRouteWithProvider(
        provider: _currentProvider,
        origin: origin,
        destination: destination,
        waypoints: waypoints,
        avoidCongestion: avoidCongestion,
        avoidTolls: avoidTolls,
        avoidHighways: avoidHighways,
      );
      
      // 如果主服务商失败，尝试备用服务商
      if (routes.isEmpty) {
        routes = await _tryBackupProviders(
          origin: origin,
          destination: destination,
          waypoints: waypoints,
        );
      }
      
      // 缓存结果
      if (routes.isNotEmpty) {
        _routeCache[cacheKey] = routes;
        
        // 设置默认选中第一条路线
        _currentRoute = routes.first;
        _alternativeRoutes = routes.skip(1).toList();
        
        notifyListeners();
      }
      
      return routes;
    } catch (e) {
      IMLogger.e('NavigationService', 'Route planning failed', e);
      return [];
    }
  }
  
  /// 多目的地路线规划（TSP优化）
  /// 
  /// 使用贪心算法+局部搜索优化多目的地路线
  Future<RoutePlanModel> planMultiStopRoute({
    required List<POIModel> destinations,
    LatLng? start,
    LatLng? end,
    bool optimizeOrder = true,
  }) async {
    if (destinations.isEmpty) {
      throw ArgumentError('Destinations cannot be empty');
    }
    
    final origin = start ?? await _locationService.getCurrentLocation();
    final finalDestination = end ?? origin;
    
    List<POIModel> orderedDestinations = List.from(destinations);
    
    // 使用最近邻算法优化路线顺序
    if (optimizeOrder && destinations.length > 2) {
      orderedDestinations = await _optimizeRouteOrder(
        origin: origin,
        destinations: destinations,
        finalDestination: finalDestination,
      );
    }
    
    // 构建完整路线
    return await _buildMultiStopRoute(
      origin: origin,
      destinations: orderedDestinations,
      finalDestination: finalDestination,
    );
  }
  
  /// 实时路线重新规划
  /// 
  /// 根据当前位置和路况重新计算最优路线
  Future<RoutePlanModel?> recalculateRoute() async {
    if (_currentRoute == null) return null;
    
    final currentLocation = await _locationService.getCurrentLocation();
    final destination = _currentRoute!.destination;
    
    final routes = await planRoute(
      destination: POIModel(
        id: 'temp_destination',
        name: 'Destination',
        location: destination,
      ),
      start: currentLocation,
      avoidCongestion: true,
    );
    
    if (routes.isNotEmpty) {
      _currentRoute = routes.first;
      notifyListeners();
      
      _eventController.add(NavigationEvent.routeRecalculated);
      
      return _currentRoute;
    }
    
    return null;
  }
  
  // ==================== 导航控制 ====================
  
  /// 开始导航
  /// 
  /// [route] 选择的路线
  /// [simulate] 是否模拟导航（测试用）
  Future<void> startNavigation({
    required RoutePlanModel route,
    bool simulate = false,
  }) async {
    if (_isNavigating) {
      await stopNavigation();
    }
    
    _currentRoute = route;
    _isNavigating = true;
    _currentStepIndex = 0;
    _remainingDistance = route.totalDistance;
    _remainingDuration = route.totalDuration;
    _navigationStartTime = DateTime.now();
    
    // 开始位置追踪
    if (simulate) {
      _startSimulatedNavigation();
    } else {
      _startRealNavigation();
    }
    
    notifyListeners();
    _eventController.add(NavigationEvent.navigationStarted);
    
    IMLogger.i('NavigationService', 'Navigation started');
  }
  
  /// 暂停导航
  void pauseNavigation() {
    if (!_isNavigating) return;
    
    _locationSubscription?.pause();
    
    _eventController.add(NavigationEvent.navigationPaused);
    IMLogger.i('NavigationService', 'Navigation paused');
  }
  
  /// 恢复导航
  void resumeNavigation() {
    if (!_isNavigating) return;
    
    _locationSubscription?.resume();
    
    _eventController.add(NavigationEvent.navigationResumed);
    IMLogger.i('NavigationService', 'Navigation resumed');
  }
  
  /// 停止导航
  Future<void> stopNavigation() async {
    if (!_isNavigating) return;
    
    await _locationSubscription?.cancel();
    _locationSubscription = null;
    
    _isNavigating = false;
    _currentStepIndex = 0;
    _remainingDistance = 0.0;
    _remainingDuration = 0;
    
    notifyListeners();
    _eventController.add(NavigationEvent.navigationStopped);
    
    IMLogger.i('NavigationService', 'Navigation stopped');
  }
  
  /// 切换到备选路线
  void switchToAlternativeRoute(int index) {
    if (index < 0 || index >= _alternativeRoutes.length) return;
    
    final newRoute = _alternativeRoutes[index];
    _alternativeRoutes[index] = _currentRoute!;
    _currentRoute = newRoute;
    
    if (_isNavigating) {
      _remainingDistance = newRoute.totalDistance;
      _remainingDuration = newRoute.totalDuration;
    }
    
    notifyListeners();
    _eventController.add(NavigationEvent.routeSwitched);
  }
  
  // ==================== 实时位置处理 ====================
  
  void _startRealNavigation() {
    _locationSubscription = _locationService.locationStream.listen(
      (location) => _onLocationUpdate(location),
      onError: (error) {
        IMLogger.e('NavigationService', 'Location stream error', error);
        _eventController.add(NavigationEvent.locationError);
      },
    );
  }
  
  void _startSimulatedNavigation() {
    if (_currentRoute == null) return;
    
    final steps = _currentRoute!.steps;
    int currentStep = 0;
    
    Timer.periodic(const Duration(seconds: 2), (timer) {
      if (!_isNavigating) {
        timer.cancel();
        return;
      }
      
      if (currentStep >= steps.length) {
        _onNavigationComplete();
        timer.cancel();
        return;
      }
      
      final step = steps[currentStep];
      _onLocationUpdate(NavigationLocation(
        latitude: step.endLocation.latitude,
        longitude: step.endLocation.longitude,
        speed: 15.0,
        heading: step.heading,
        timestamp: DateTime.now(),
      ));
      
      currentStep++;
    });
  }
  
  void _onLocationUpdate(NavigationLocation location) {
    if (_currentRoute == null) return;
    
    // 检查是否偏离路线
    final deviation = _checkRouteDeviation(location);
    if (deviation > 100) {
      // 偏离超过100米，重新规划
      _eventController.add(NavigationEvent.routeDeviated);
      recalculateRoute();
      return;
    }
    
    // 更新当前步骤
    _updateCurrentStep(location);
    
    // 计算剩余距离和时间
    _updateProgress(location);
    
    // 检查是否到达
    if (_remainingDistance < 20) {
      _onNavigationComplete();
      return;
    }
    
    // 播报导航语音
    _announceTurnIfNeeded(location);
    
    notifyListeners();
  }
  
  double _checkRouteDeviation(NavigationLocation location) {
    if (_currentRoute == null) return 0.0;
    
    double minDistance = double.infinity;
    
    for (final step in _currentRoute!.steps) {
      final distance = CoordinateUtils.distanceToLineSegment(
        location.latitude,
        location.longitude,
        step.startLocation.latitude,
        step.startLocation.longitude,
        step.endLocation.latitude,
        step.endLocation.longitude,
      );
      
      if (distance < minDistance) {
        minDistance = distance;
      }
    }
    
    return minDistance;
  }
  
  void _updateCurrentStep(NavigationLocation location) {
    if (_currentRoute == null) return;
    
    final steps = _currentRoute!.steps;
    
    for (int i = _currentStepIndex; i < steps.length; i++) {
      final step = steps[i];
      final distanceToEnd = CoordinateUtils.calculateDistance(
        location.latitude,
        location.longitude,
        step.endLocation.latitude,
        step.endLocation.longitude,
      );
      
      if (distanceToEnd < 30) {
        // 接近步骤终点
        if (i > _currentStepIndex) {
          _currentStepIndex = i;
          _eventController.add(NavigationEvent.stepChanged);
        }
        break;
      }
    }
  }
  
  void _updateProgress(NavigationLocation location) {
    if (_currentRoute == null) return;
    
    double totalRemaining = 0.0;
    
    for (int i = _currentStepIndex; i < _currentRoute!.steps.length; i++) {
      final step = _currentRoute!.steps[i];
      
      if (i == _currentStepIndex) {
        final distanceToEnd = CoordinateUtils.calculateDistance(
          location.latitude,
          location.longitude,
          step.endLocation.latitude,
          step.endLocation.longitude,
        );
        totalRemaining += distanceToEnd;
      } else {
        totalRemaining += step.distance;
      }
    }
    
    _remainingDistance = totalRemaining;
    
    // 估算剩余时间（基于平均速度）
    final avgSpeed = location.speed > 0 ? location.speed : 15.0;
    _remainingDuration = (_remainingDistance / avgSpeed * 3.6).round();
  }
  
  void _announceTurnIfNeeded(NavigationLocation location) {
    if (_currentRoute == null) return;
    
    final currentStep = _currentRoute!.steps[_currentStepIndex];
    final distanceToTurn = CoordinateUtils.calculateDistance(
      location.latitude,
      location.longitude,
      currentStep.endLocation.latitude,
      currentStep.endLocation.longitude,
    );
    
    // 在特定距离播报转向提示
    if ((distanceToTurn - 200).abs() < 10 && !currentStep.announced200m) {
      _eventController.add(NavigationEvent.announceTurn(
        instruction: currentStep.instruction,
        distance: 200,
      ));
      currentStep.announced200m = true;
    } else if ((distanceToTurn - 50).abs() < 10 && !currentStep.announced50m) {
      _eventController.add(NavigationEvent.announceTurn(
        instruction: currentStep.instruction,
        distance: 50,
      ));
      currentStep.announced50m = true;
    }
  }
  
  void _onNavigationComplete() {
    stopNavigation();
    _eventController.add(NavigationEvent.navigationCompleted);
    IMLogger.i('NavigationService', 'Navigation completed');
  }
  
  // ==================== 离线地图 ====================
  
  /// 下载离线地图区域
  Future<void> downloadOfflineRegion({
    required String regionId,
    required LatLngBounds bounds,
    required int minZoom,
    required int maxZoom,
  }) async {
    final region = OfflineMapRegion(
      id: regionId,
      bounds: bounds,
      minZoom: minZoom,
      maxZoom: maxZoom,
      downloadTime: DateTime.now(),
    );
    
    _offlineRegions[regionId] = region;
    
    // 触发下载任务
    _eventController.add(NavigationEvent.offlineDownloadStarted(regionId));
    
    // 模拟下载完成
    await Future.delayed(const Duration(seconds: 5));
    
    region.status = OfflineRegionStatus.completed;
    _eventController.add(NavigationEvent.offlineDownloadCompleted(regionId));
    
    IMLogger.i('NavigationService', 'Offline region downloaded: $regionId');
  }
  
  /// 检查是否支持离线导航
  bool isOfflineAvailable(LatLng location) {
    for (final region in _offlineRegions.values) {
      if (region.bounds.contains(location) && region.isAvailable) {
        return true;
      }
    }
    return false;
  }
  
  Future<void> _loadOfflineRegions() async {
    // 从本地存储加载已下载的离线区域
    IMLogger.i('NavigationService', 
      'Loaded ${_offlineRegions.length} offline regions');
  }
  
  // ==================== 路线优化算法 ====================
  
  Future<List<POIModel>> _optimizeRouteOrder({
    required LatLng origin,
    required List<POIModel> destinations,
    required LatLng finalDestination,
  }) async {
    // 使用最近邻算法
    final unvisited = List<POIModel>.from(destinations);
    final optimized = <POIModel>[];
    var current = origin;
    
    while (unvisited.isNotEmpty) {
      POIModel? nearest;
      double minDistance = double.infinity;
      
      for (final dest in unvisited) {
        final distance = CoordinateUtils.calculateDistance(
          current.latitude,
          current.longitude,
          dest.location.latitude,
          dest.location.longitude,
        );
        
        if (distance < minDistance) {
          minDistance = distance;
          nearest = dest;
        }
      }
      
      if (nearest != null) {
        optimized.add(nearest);
        unvisited.remove(nearest);
        current = nearest.location;
      }
    }
    
    // 2-opt局部搜索优化
    return _twoOptOptimization(optimized, origin, finalDestination);
  }
  
  List<POIModel> _twoOptOptimization(
    List<POIModel> route,
    LatLng origin,
    LatLng finalDestination,
  ) {
    bool improved = true;
    var bestRoute = List<POIModel>.from(route);
    
    while (improved) {
      improved = false;
      
      for (int i = 0; i < bestRoute.length - 1; i++) {
        for (int j = i + 1; j < bestRoute.length; j++) {
          final newRoute = _twoOptSwap(bestRoute, i, j);
          
          if (_calculateRouteDistance(newRoute, origin, finalDestination) <
              _calculateRouteDistance(bestRoute, origin, finalDestination)) {
            bestRoute = newRoute;
            improved = true;
          }
        }
      }
    }
    
    return bestRoute;
  }
  
  List<POIModel> _twoOptSwap(List<POIModel> route, int i, int j) {
    final newRoute = <POIModel>[];
    
    for (int k = 0; k <= i - 1; k++) {
      newRoute.add(route[k]);
    }
    
    for (int k = j; k >= i; k--) {
      newRoute.add(route[k]);
    }
    
    for (int k = j + 1; k < route.length; k++) {
      newRoute.add(route[k]);
    }
    
    return newRoute;
  }
  
  double _calculateRouteDistance(
    List<POIModel> route,
    LatLng origin,
    LatLng finalDestination,
  ) {
    double total = 0.0;
    var current = origin;
    
    for (final dest in route) {
      total += CoordinateUtils.calculateDistance(
        current.latitude,
        current.longitude,
        dest.location.latitude,
        dest.location.longitude,
      );
      current = dest.location;
    }
    
    total += CoordinateUtils.calculateDistance(
      current.latitude,
      current.longitude,
      finalDestination.latitude,
      finalDestination.longitude,
    );
    
    return total;
  }
  
  // ==================== 辅助方法 ====================
  
  String _generateCacheKey({
    required LatLng origin,
    required LatLng destination,
    required NavigationMode mode,
  }) {
    final originHash = '${origin.latitude.toStringAsFixed(4)}_${origin.longitude.toStringAsFixed(4)}';
    final destHash = '${destination.latitude.toStringAsFixed(4)}_${destination.longitude.toStringAsFixed(4)}';
    return '${originHash}_${destHash}_${mode.name}';
  }
  
  Future<List<RoutePlanModel>> _planRouteWithProvider({
    required NavigationProvider provider,
    required LatLng origin,
    required POIModel destination,
    List<LatLng>? waypoints,
    required bool avoidCongestion,
    required bool avoidTolls,
    required bool avoidHighways,
  }) async {
    // 实际实现会调用各服务商的SDK
    // 这里返回模拟数据
    return _generateMockRoutes(origin, destination);
  }
  
  Future<List<RoutePlanModel>> _tryBackupProviders({
    required LatLng origin,
    required POIModel destination,
    List<LatLng>? waypoints,
  }) async {
    final backupProviders = [
      NavigationProvider.tencent,
      NavigationProvider.baidu,
    ];
    
    for (final provider in backupProviders) {
      if (provider == _currentProvider) continue;
      
      try {
        final routes = await _planRouteWithProvider(
          provider: provider,
          origin: origin,
          destination: destination,
          waypoints: waypoints,
          avoidCongestion: true,
          avoidTolls: false,
          avoidHighways: false,
        );
        
        if (routes.isNotEmpty) {
          IMLogger.w('NavigationService', 'Using backup provider: $provider');
          return routes;
        }
      } catch (e) {
        IMLogger.e('NavigationService', 'Backup provider $provider failed', e);
      }
    }
    
    return [];
  }
  
  Future<RoutePlanModel> _buildMultiStopRoute({
    required LatLng origin,
    required List<POIModel> destinations,
    required LatLng finalDestination,
  }) async {
    // 构建多段路线
    final allSteps = <RouteStep>[];
    double totalDistance = 0.0;
    int totalDuration = 0;
    
    var currentOrigin = origin;
    
    for (final dest in destinations) {
      final segment = await _planRouteWithProvider(
        provider: _currentProvider,
        origin: currentOrigin,
        destination: dest,
        waypoints: null,
        avoidCongestion: true,
        avoidTolls: false,
        avoidHighways: false,
      );
      
      if (segment.isNotEmpty) {
        final route = segment.first;
        allSteps.addAll(route.steps);
        totalDistance += route.totalDistance;
        totalDuration += route.totalDuration;
      }
      
      currentOrigin = dest.location;
    }
    
    // 最后一段到终点
    final finalSegment = await _planRouteWithProvider(
      provider: _currentProvider,
      origin: currentOrigin,
      destination: POIModel(
        id: 'final',
        name: 'Final',
        location: finalDestination,
      ),
      waypoints: null,
      avoidCongestion: true,
      avoidTolls: false,
      avoidHighways: false,
    );
    
    if (finalSegment.isNotEmpty) {
      final route = finalSegment.first;
      allSteps.addAll(route.steps);
      totalDistance += route.totalDistance;
      totalDuration += route.totalDuration;
    }
    
    return RoutePlanModel(
      id: 'multi_stop_${DateTime.now().millisecondsSinceEpoch}',
      origin: origin,
      destination: finalDestination,
      steps: allSteps,
      totalDistance: totalDistance,
      totalDuration: totalDuration,
      tollCost: 0,
      trafficCondition: TrafficCondition.smooth,
      polyline: '',
      isMultiStop: true,
      waypoints: destinations.map((d) => d.location).toList(),
    );
  }
  
  List<RoutePlanModel> _generateMockRoutes(LatLng origin, POIModel destination) {
    final distance = CoordinateUtils.calculateDistance(
      origin.latitude,
      origin.longitude,
      destination.location.latitude,
      destination.location.longitude,
    );
    
    final duration = (distance / 15.0 * 3.6).round();
    
    // 推荐路线
    final recommendedRoute = RoutePlanModel(
      id: 'route_recommended',
      origin: origin,
      destination: destination.location,
      steps: [
        RouteStep(
          instruction: '从当前位置出发',
          distance: 100,
          duration: 20,
          startLocation: origin,
          endLocation: LatLng(
            origin.latitude + 0.001,
            origin.longitude + 0.001,
          ),
          heading: 45,
          roadName: '起点路',
          action: RouteAction.start,
        ),
        RouteStep(
          instruction: '沿主干道行驶',
          distance: distance * 0.8,
          duration: (duration * 0.8).round(),
          startLocation: LatLng(
            origin.latitude + 0.001,
            origin.longitude + 0.001,
          ),
          endLocation: LatLng(
            destination.location.latitude - 0.001,
            destination.location.longitude - 0.001,
          ),
          heading: 90,
          roadName: '主干道',
          action: RouteAction.straight,
        ),
        RouteStep(
          instruction: '到达目的地: ${destination.name}',
          distance: distance * 0.2,
          duration: (duration * 0.2).round(),
          startLocation: LatLng(
            destination.location.latitude - 0.001,
            destination.location.longitude - 0.001,
          ),
          endLocation: destination.location,
          heading: 45,
          roadName: '终点路',
          action: RouteAction.arrive,
        ),
      ],
      totalDistance: distance,
      totalDuration: duration,
      tollCost: 0,
      trafficCondition: TrafficCondition.smooth,
      polyline: 'mock_polyline_${DateTime.now().millisecondsSinceEpoch}',
    );
    
    // 备选路线（距离稍长但可能更快）
    final alternativeRoute = RoutePlanModel(
      id: 'route_alternative',
      origin: origin,
      destination: destination.location,
      steps: recommendedRoute.steps,
      totalDistance: distance * 1.1,
      totalDuration: (duration * 0.95).round(),
      tollCost: 5,
      trafficCondition: TrafficCondition.moderate,
      polyline: 'mock_polyline_alt_${DateTime.now().millisecondsSinceEpoch}',
    );
    
    return [recommendedRoute, alternativeRoute];
  }
  
  /// 清理缓存
  void clearCache() {
    _routeCache.clear();
    IMLogger.i('NavigationService', 'Route cache cleared');
  }
  
  /// 释放资源
  @override
  void dispose() {
    stopNavigation();
    _eventController.close();
    super.dispose();
  }
}

// ==================== 数据模型 ====================

/// 导航位置
class NavigationLocation {
  final double latitude;
  final double longitude;
  final double speed;
  final double heading;
  final DateTime timestamp;
  
  NavigationLocation({
    required this.latitude,
    required this.longitude,
    required this.speed,
    required this.heading,
    required this.timestamp,
  });
  
  LatLng get latLng => LatLng(latitude, longitude);
}

/// 经纬度
class LatLng {
  final double latitude;
  final double longitude;
  
  LatLng(this.latitude, this.longitude);
  
  @override
  String toString() => 'LatLng($latitude, $longitude)';
}

/// 经纬度边界
class LatLngBounds {
  final LatLng southwest;
  final LatLng northeast;
  
  LatLngBounds({
    required this.southwest,
    required this.northeast,
  });
  
  bool contains(LatLng point) {
    return point.latitude >= southwest.latitude &&
           point.latitude <= northeast.latitude &&
           point.longitude >= southwest.longitude &&
           point.longitude <= northeast.longitude;
  }
}

/// 路况条件
enum TrafficCondition {
  smooth,      // 畅通
  moderate,    // 缓行
  congested,   // 拥堵
  blocked,     // 严重拥堵
}

/// 离线地图区域
class OfflineMapRegion {
  final String id;
  final LatLngBounds bounds;
  final int minZoom;
  final int maxZoom;
  final DateTime downloadTime;
  OfflineRegionStatus status;
  double progress;
  
  OfflineMapRegion({
    required this.id,
    required this.bounds,
    required this.minZoom,
    required this.maxZoom,
    required this.downloadTime,
    this.status = OfflineRegionStatus.pending,
    this.progress = 0.0,
  });
  
  bool get isAvailable => status == OfflineRegionStatus.completed;
}

enum OfflineRegionStatus {
  pending,
  downloading,
  completed,
  failed,
}

/// 导航事件
abstract class NavigationEvent {
  const NavigationEvent();
  
  static const navigationStarted = NavigationStartedEvent();
  static const navigationStopped = NavigationStoppedEvent();
  static const navigationPaused = NavigationPausedEvent();
  static const navigationResumed = NavigationResumedEvent();
  static const navigationCompleted = NavigationCompletedEvent();
  static const routeRecalculated = RouteRecalculatedEvent();
  static const routeDeviated = RouteDeviatedEvent();
  static const routeSwitched = RouteSwitchedEvent();
  static const stepChanged = StepChangedEvent();
  static const locationError = LocationErrorEvent();
  
  static NavigationEvent announceTurn({
    required String instruction,
    required int distance,
  }) => AnnounceTurnEvent(instruction: instruction, distance: distance);
  
  static NavigationEvent offlineDownloadStarted(String regionId) =>
      OfflineDownloadStartedEvent(regionId: regionId);
  
  static NavigationEvent offlineDownloadCompleted(String regionId) =>
      OfflineDownloadCompletedEvent(regionId: regionId);
}

class NavigationStartedEvent extends NavigationEvent {
  const NavigationStartedEvent();
}

class NavigationStoppedEvent extends NavigationEvent {
  const NavigationStoppedEvent();
}

class NavigationPausedEvent extends NavigationEvent {
  const NavigationPausedEvent();
}

class NavigationResumedEvent extends NavigationEvent {
  const NavigationResumedEvent();
}

class NavigationCompletedEvent extends NavigationEvent {
  const NavigationCompletedEvent();
}

class RouteRecalculatedEvent extends NavigationEvent {
  const RouteRecalculatedEvent();
}

class RouteDeviatedEvent extends NavigationEvent {
  const RouteDeviatedEvent();
}

class RouteSwitchedEvent extends NavigationEvent {
  const RouteSwitchedEvent();
}

class StepChangedEvent extends NavigationEvent {
  const StepChangedEvent();
}

class LocationErrorEvent extends NavigationEvent {
  const LocationErrorEvent();
}

class AnnounceTurnEvent extends NavigationEvent {
  final String instruction;
  final int distance;
  
  const AnnounceTurnEvent({
    required this.instruction,
    required this.distance,
  });
}

class OfflineDownloadStartedEvent extends NavigationEvent {
  final String regionId;
  
  const OfflineDownloadStartedEvent({required this.regionId});
}

class OfflineDownloadCompletedEvent extends NavigationEvent {
  final String regionId;
  
  const OfflineDownloadCompletedEvent({required this.regionId});
}
