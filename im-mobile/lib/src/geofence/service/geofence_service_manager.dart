import 'dart:async';
import 'dart:collection';
import 'dart:convert';
import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:geolocator/geolocator.dart';
import 'package:shared_preferences/shared_preferences.dart';

import '../models/geofence_model.dart';
import '../engine/geofence_detector.dart';

/// 地理围栏服务管理器
/// 
/// 负责：
/// - 管理所有围栏的注册/注销
/// - 监控位置变化
/// - 触发围栏事件
/// - 状态持久化
class GeofenceServiceManager extends ChangeNotifier {
  static final GeofenceServiceManager _instance = GeofenceServiceManager._internal();
  factory GeofenceServiceManager() => _instance;
  GeofenceServiceManager._internal();

  /// 围栏列表
  final Map<String, Geofence> _geofences = {};
  
  /// 围栏监控状态
  final Map<String, GeofenceMonitoringState> _monitoringStates = {};
  
  /// 事件流控制器
  final _eventController = StreamController<GeofenceTriggerEvent>.broadcast();
  
  /// 位置流订阅
  StreamSubscription<Position>? _positionSubscription;
  
  /// 是否正在监控
  bool _isMonitoring = false;
  
  /// 监控配置
  GeofenceMonitorConfig _config = const GeofenceMonitorConfig();
  
  /// 最后已知位置
  Position? _lastPosition;
  
  /// 位置历史（用于置信度计算）
  final Queue<Position> _positionHistory = Queue<Position>();
  
  /// SharedPreferences实例
  SharedPreferences? _prefs;
  
  /// 是否已初始化
  bool _initialized = false;

  // ==================== Getters ====================
  
  /// 事件流
  Stream<GeofenceTriggerEvent> get eventStream => _eventController.stream;
  
  /// 是否正在监控
  bool get isMonitoring => _isMonitoring;
  
  /// 已注册围栏数量
  int get geofenceCount => _geofences.length;
  
  /// 所有围栏
  List<Geofence> get allGeofences => List.unmodifiable(_geofences.values);
  
  /// 活跃围栏
  List<Geofence> get activeGeofences => _geofences.values
      .where((g) => g.isActive && (g.expiresAt == null || g.expiresAt!.isAfter(DateTime.now())))
      .toList();
  
  /// 最后已知位置
  Position? get lastPosition => _lastPosition;
  
  /// 当前配置
  GeofenceMonitorConfig get config => _config;

  // ==================== 初始化 ====================
  
  /// 初始化服务
  Future<void> initialize({GeofenceMonitorConfig? config}) async {
    if (_initialized) return;
    
    _prefs = await SharedPreferences.getInstance();
    
    if (config != null) {
      _config = config;
    }
    
    // 恢复持久化的围栏和状态
    await _restoreGeofences();
    await _restoreMonitoringStates();
    
    _initialized = true;
    debugPrint('GeofenceServiceManager initialized');
  }
  
  /// 检查并请求位置权限
  Future<bool> checkPermission() async {
    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      return false;
    }
    
    LocationPermission permission = await Geolocator.checkPermission();
    if (permission == LocationPermission.denied) {
      permission = await Geolocator.requestPermission();
      if (permission == LocationPermission.denied) {
        return false;
      }
    }
    
    if (permission == LocationPermission.deniedForever) {
      return false;
    }
    
    return true;
  }

  // ==================== 围栏管理 ====================
  
  /// 注册单个围栏
  Future<bool> registerGeofence(Geofence geofence) async {
    if (!_initialized) {
      throw StateError('GeofenceServiceManager not initialized');
    }
    
    _geofences[geofence.id] = geofence;
    
    // 初始化监控状态
    _monitoringStates[geofence.id] = GeofenceMonitoringState(
      geofenceId: geofence.id,
      status: GeofenceStatus.outside,
      lastUpdateTime: DateTime.now(),
    );
    
    // 持久化
    await _persistGeofences();
    await _persistMonitoringStates();
    
    notifyListeners();
    debugPrint('Registered geofence: ${geofence.id}');
    return true;
  }
  
  /// 批量注册围栏
  Future<void> registerGeofences(List<Geofence> geofences) async {
    for (final geofence in geofences) {
      _geofences[geofence.id] = geofence;
      _monitoringStates[geofence.id] = GeofenceMonitoringState(
        geofenceId: geofence.id,
        status: GeofenceStatus.outside,
        lastUpdateTime: DateTime.now(),
      );
    }
    
    await _persistGeofences();
    await _persistMonitoringStates();
    
    notifyListeners();
    debugPrint('Registered ${geofences.length} geofences');
  }
  
  /// 注销围栏
  Future<bool> unregisterGeofence(String geofenceId) async {
    if (!_geofences.containsKey(geofenceId)) {
      return false;
    }
    
    _geofences.remove(geofenceId);
    _monitoringStates.remove(geofenceId);
    
    await _persistGeofences();
    await _persistMonitoringStates();
    
    notifyListeners();
    debugPrint('Unregistered geofence: $geofenceId');
    return true;
  }
  
  /// 批量注销围栏
  Future<void> unregisterGeofences(List<String> geofenceIds) async {
    for (final id in geofenceIds) {
      _geofences.remove(id);
      _monitoringStates.remove(id);
    }
    
    await _persistGeofences();
    await _persistMonitoringStates();
    
    notifyListeners();
    debugPrint('Unregistered ${geofenceIds.length} geofences');
  }
  
  /// 获取围栏
  Geofence? getGeofence(String geofenceId) {
    return _geofences[geofenceId];
  }
  
  /// 获取围栏监控状态
  GeofenceMonitoringState? getMonitoringState(String geofenceId) {
    return _monitoringStates[geofenceId];
  }
  
  /// 清空所有围栏
  Future<void> clearAllGeofences() async {
    _geofences.clear();
    _monitoringStates.clear();
    
    await _persistGeofences();
    await _persistMonitoringStates();
    
    notifyListeners();
    debugPrint('Cleared all geofences');
  }
  
  /// 更新围栏状态
  Future<void> updateGeofenceState(String geofenceId, bool isActive) async {
    final geofence = _geofences[geofenceId];
    if (geofence != null) {
      _geofences[geofenceId] = geofence.copyWith(isActive: isActive);
      await _persistGeofences();
      notifyListeners();
    }
  }

  // ==================== 监控管理 ====================
  
  /// 开始监控
  Future<void> startMonitoring({GeofenceMonitorConfig? config}) async {
    if (_isMonitoring) return;
    
    if (config != null) {
      _config = config;
    }
    
    final hasPermission = await checkPermission();
    if (!hasPermission) {
      throw Exception('Location permission not granted');
    }
    
    // 配置位置设置
    final locationSettings = LocationSettings(
      accuracy: _config.accuracy,
      distanceFilter: _config.distanceFilter.toInt(),
      timeLimit: _config.timeout,
    );
    
    // 订阅位置更新
    _positionSubscription = Geolocator.getPositionStream(
      locationSettings: locationSettings,
    ).listen(
      _onPositionUpdate,
      onError: (error) {
        debugPrint('Position stream error: $error');
      },
    );
    
    _isMonitoring = true;
    notifyListeners();
    debugPrint('Started geofence monitoring');
  }
  
  /// 停止监控
  Future<void> stopMonitoring() async {
    await _positionSubscription?.cancel();
    _positionSubscription = null;
    _isMonitoring = false;
    
    notifyListeners();
    debugPrint('Stopped geofence monitoring');
  }
  
  /// 单次位置检查
  Future<void> checkOnce() async {
    final hasPermission = await checkPermission();
    if (!hasPermission) {
      throw Exception('Location permission not granted');
    }
    
    try {
      final position = await Geolocator.getCurrentPosition(
        desiredAccuracy: _config.accuracy,
      );
      await _onPositionUpdate(position);
    } catch (e) {
      debugPrint('Error getting current position: $e');
    }
  }

  // ==================== 位置处理 ====================
  
  /// 位置更新处理
  Future<void> _onPositionUpdate(Position position) async {
    _lastPosition = position;
    
    // 保存位置历史
    _positionHistory.addLast(position);
    while (_positionHistory.length > _config.positionHistorySize) {
      _positionHistory.removeFirst();
    }
    
    // 检查每个围栏
    for (final geofence in activeGeofences) {
      await _checkGeofence(geofence, position);
    }
  }
  
  /// 检查单个围栏
  Future<void> _checkGeofence(Geofence geofence, Position position) async {
    final currentState = _monitoringStates[geofence.id];
    if (currentState == null) return;
    
    // 快速包围盒检查（优化）
    if (geofence.type == GeofenceType.polygon && geofence.polygonPoints != null) {
      final bbox = GeofenceDetector.calculateBoundingBox(geofence.polygonPoints!);
      final buffer = 0.001; // 约100米
      final inBBox = GeofenceDetector.quickBoundingBoxCheck(
        position.latitude,
        position.longitude,
        bbox.$1,
        bbox.$2,
        bbox.$3,
        bbox.$4,
        buffer,
      );
      if (!inBBox) {
        // 快速排除，处理离开事件
        if (currentState.status != GeofenceStatus.outside) {
          await _handleExit(geofence, position, 0.95);
        }
        return;
      }
    }
    
    // 精确检测
    final (isInside, confidence) = GeofenceDetector.detectWithConfidence(
      geofence,
      position,
    );
    
    // 检查置信度阈值
    if (confidence < geofence.minConfidence) {
      return;
    }
    
    // 平滑处理：需要连续检测才改变状态
    int consecutive = currentState.consecutiveDetections;
    if (isInside == (currentState.status != GeofenceStatus.outside)) {
      consecutive++;
    } else {
      consecutive = 0;
    }
    
    // 需要连续检测3次才确认状态变化
    if (consecutive < _config.consecutiveDetectionThreshold) {
      _monitoringStates[geofence.id] = currentState.copyWith(
        consecutiveDetections: consecutive,
        lastUpdateTime: DateTime.now(),
      );
      return;
    }
    
    // 处理状态变化
    if (isInside) {
      if (currentState.status == GeofenceStatus.outside) {
        // 进入围栏
        await _handleEnter(geofence, position, confidence);
      } else {
        // 在围栏内，检查停留
        await _handleDwell(geofence, position, confidence);
      }
    } else {
      if (currentState.status != GeofenceStatus.outside) {
        // 离开围栏
        await _handleExit(geofence, position, confidence);
      }
    }
    
    // 更新监控状态
    _monitoringStates[geofence.id] = GeofenceMonitoringState(
      geofenceId: geofence.id,
      status: isInside ? GeofenceStatus.inside : GeofenceStatus.outside,
      lastUpdateTime: DateTime.now(),
      enterTime: isInside ? (currentState.enterTime ?? DateTime.now()) : null,
      dwellDuration: isInside && currentState.enterTime != null
          ? DateTime.now().difference(currentState.enterTime!).inMilliseconds
          : 0,
      lastPosition: position,
      enterCount: isInside && currentState.status == GeofenceStatus.outside
          ? currentState.enterCount + 1
          : currentState.enterCount,
      consecutiveDetections: 0,
    );
    
    await _persistMonitoringStates();
  }
  
  /// 处理进入事件
  Future<void> _handleEnter(
    Geofence geofence, 
    Position position, 
    double confidence,
  ) async {
    if (!geofence.triggers.contains(GeofenceEvent.enter)) return;
    
    final event = GeofenceTriggerEvent(
      id: _generateEventId(),
      geofenceId: geofence.id,
      eventType: GeofenceEvent.enter,
      timestamp: DateTime.now(),
      position: position,
      confidence: confidence,
      status: GeofenceStatus.inside,
      extraData: geofence.metadata,
    );
    
    _eventController.add(event);
    debugPrint('Geofence enter event: ${geofence.id}');
  }
  
  /// 处理停留事件
  Future<void> _handleDwell(
    Geofence geofence, 
    Position position, 
    double confidence,
  ) async {
    if (!geofence.triggers.contains(GeofenceEvent.dwell)) return;
    
    final currentState = _monitoringStates[geofence.id];
    if (currentState?.enterTime == null) return;
    
    final dwellDuration = DateTime.now().difference(currentState!.enterTime!).inMilliseconds;
    
    // 检查是否已达到停留时间且未触发过
    if (dwellDuration >= geofence.dwellTime &&
        currentState.status != GeofenceStatus.dwelling) {
      final event = GeofenceTriggerEvent(
        id: _generateEventId(),
        geofenceId: geofence.id,
        eventType: GeofenceEvent.dwell,
        timestamp: DateTime.now(),
        position: position,
        confidence: confidence,
        status: GeofenceStatus.dwelling,
        dwellDuration: dwellDuration,
        extraData: geofence.metadata,
      );
      
      _eventController.add(event);
      debugPrint('Geofence dwell event: ${geofence.id}, duration: ${dwellDuration}ms');
    }
  }
  
  /// 处理离开事件
  Future<void> _handleExit(
    Geofence geofence, 
    Position position, 
    double confidence,
  ) async {
    if (!geofence.triggers.contains(GeofenceEvent.exit)) return;
    
    final currentState = _monitoringStates[geofence.id];
    final dwellDuration = currentState?.enterTime != null
        ? DateTime.now().difference(currentState!.enterTime!).inMilliseconds
        : 0;
    
    final event = GeofenceTriggerEvent(
      id: _generateEventId(),
      geofenceId: geofence.id,
      eventType: GeofenceEvent.exit,
      timestamp: DateTime.now(),
      position: position,
      confidence: confidence,
      status: GeofenceStatus.outside,
      dwellDuration: dwellDuration,
      extraData: {
        ...?geofence.metadata,
        'dwellDuration': dwellDuration,
      },
    );
    
    _eventController.add(event);
    debugPrint('Geofence exit event: ${geofence.id}');
  }

  // ==================== 持久化 ====================
  
  /// 持久化围栏
  Future<void> _persistGeofences() async {
    if (_prefs == null) return;
    
    final geofenceList = _geofences.values.map((g) => g.toJson()).toList();
    final jsonStr = jsonEncode(geofenceList);
    await _prefs!.setString('geofences', jsonStr);
  }
  
  /// 恢复围栏
  Future<void> _restoreGeofences() async {
    if (_prefs == null) return;
    
    final jsonStr = _prefs!.getString('geofences');
    if (jsonStr == null) return;
    
    try {
      final List<dynamic> jsonList = jsonDecode(jsonStr);
      for (final json in jsonList) {
        final geofence = Geofence.fromJson(json as Map<String, dynamic>);
        _geofences[geofence.id] = geofence;
      }
      debugPrint('Restored ${_geofences.length} geofences');
    } catch (e) {
      debugPrint('Error restoring geofences: $e');
    }
  }
  
  /// 持久化监控状态
  Future<void> _persistMonitoringStates() async {
    if (_prefs == null) return;
    
    final stateList = _monitoringStates.values.map((s) => s.toJson()).toList();
    final jsonStr = jsonEncode(stateList);
    await _prefs!.setString('geofence_states', jsonStr);
  }
  
  /// 恢复监控状态
  Future<void> _restoreMonitoringStates() async {
    if (_prefs == null) return;
    
    final jsonStr = _prefs!.getString('geofence_states');
    if (jsonStr == null) return;
    
    try {
      final List<dynamic> jsonList = jsonDecode(jsonStr);
      for (final json in jsonList) {
        final state = GeofenceMonitoringState.fromJson(json as Map<String, dynamic>);
        _monitoringStates[state.geofenceId] = state;
      }
      debugPrint('Restored ${_monitoringStates.length} monitoring states');
    } catch (e) {
      debugPrint('Error restoring monitoring states: $e');
    }
  }

  // ==================== 工具方法 ====================
  
  /// 生成事件ID
  String _generateEventId() {
    return 'evt_${DateTime.now().millisecondsSinceEpoch}_${_randomString(6)}';
  }
  
  /// 生成随机字符串
  String _randomString(int length) {
    const chars = 'abcdefghijklmnopqrstuvwxyz0123456789';
    final random = Random();
    return String.fromCharCodes(
      Iterable.generate(length, (_) => chars.codeUnitAt(random.nextInt(chars.length))),
    );
  }
  
  /// 销毁
  @override
  void dispose() {
    _positionSubscription?.cancel();
    _eventController.close();
    super.dispose();
  }
}

/// 监控配置
class GeofenceMonitorConfig {
  /// 位置精度
  final LocationAccuracy accuracy;
  
  /// 最小移动距离触发（米）
  final double distanceFilter;
  
  /// 位置获取超时
  final Duration timeout;
  
  /// 位置历史记录大小
  final int positionHistorySize;
  
  /// 连续检测阈值（用于平滑处理）
  final int consecutiveDetectionThreshold;

  const GeofenceMonitorConfig({
    this.accuracy = LocationAccuracy.high,
    this.distanceFilter = 10.0,
    this.timeout = const Duration(seconds: 30),
    this.positionHistorySize = 10,
    this.consecutiveDetectionThreshold = 3,
  });
}
