import 'dart:async';
import 'dart:convert';
import 'dart:math';
import 'package:flutter/material.dart';
import 'package:geolocator/geolocator.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import 'package:http/http.dart' as http;
import '../models/location_model.dart';
import '../models/poi_model.dart';
import '../models/user_model.dart';
import '../services/api_service.dart';
import '../services/websocket_service.dart';
import '../utils/logger.dart';

/// LBS位置服务 - 核心地理位置管理
/// 提供定位、位置更新、地理围栏等功能
class LocationService extends ChangeNotifier {
  static final LocationService _instance = LocationService._internal();
  factory LocationService() => _instance;
  LocationService._internal();

  // 状态
  bool _isInitialized = false;
  bool _isTracking = false;
  bool _hasPermission = false;
  Position? _currentPosition;
  StreamSubscription<Position>? _positionStream;
  
  // 地理围栏
  final Map<String, GeofenceRegion> _geofences = {};
  final StreamController<GeofenceEvent> _geofenceController = 
      StreamController<GeofenceEvent>.broadcast();
  
  // 位置历史
  final List<UserLocation> _locationHistory = [];
  final StreamController<UserLocation> _locationUpdateController = 
      StreamController<UserLocation>.broadcast();
  
  // API服务
  final ApiService _apiService = ApiService();
  final WebSocketService _wsService = WebSocketService();
  
  // 配置
  static const Duration _locationUpdateInterval = Duration(seconds: 30);
  static const double _minDistanceFilter = 50.0; // 米
  static const int _maxHistorySize = 100;

  // Getters
  bool get isInitialized => _isInitialized;
  bool get isTracking => _isTracking;
  bool get hasPermission => _hasPermission;
  Position? get currentPosition => _currentPosition;
  LatLng? get currentLatLng => _currentPosition != null 
      ? LatLng(_currentPosition!.latitude, _currentPosition!.longitude)
      : null;
  List<UserLocation> get locationHistory => List.unmodifiable(_locationHistory);
  Stream<GeofenceEvent> get geofenceEvents => _geofenceController.stream;
  Stream<UserLocation> get locationUpdates => _locationUpdateController.stream;

  /// 初始化位置服务
  Future<bool> initialize() async {
    if (_isInitialized) return true;
    
    try {
      // 检查位置服务是否启用
      bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
      if (!serviceEnabled) {
        Logger.w('LocationService', '位置服务未启用');
        return false;
      }

      // 请求权限
      LocationPermission permission = await Geolocator.checkPermission();
      if (permission == LocationPermission.denied) {
        permission = await Geolocator.requestPermission();
        if (permission == LocationPermission.denied) {
          Logger.w('LocationService', '位置权限被拒绝');
          return false;
        }
      }

      if (permission == LocationPermission.deniedForever) {
        Logger.w('LocationService', '位置权限被永久拒绝');
        return false;
      }

      _hasPermission = true;
      _isInitialized = true;
      
      // 获取当前位置
      await _getCurrentPosition();
      
      Logger.i('LocationService', '位置服务初始化成功');
      notifyListeners();
      return true;
    } catch (e) {
      Logger.e('LocationService', '初始化失败: $e');
      return false;
    }
  }

  /// 获取当前位置
  Future<Position?> _getCurrentPosition() async {
    try {
      _currentPosition = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.high,
      );
      _addToHistory(_currentPosition!);
      notifyListeners();
      return _currentPosition;
    } catch (e) {
      Logger.e('LocationService', '获取位置失败: $e');
      return null;
    }
  }

  /// 开始持续定位
  Future<void> startTracking() async {
    if (_isTracking) return;
    if (!_isInitialized) {
      final initialized = await initialize();
      if (!initialized) return;
    }

    _isTracking = true;
    notifyListeners();

    // 配置位置更新流
    final locationSettings = LocationSettings(
      accuracy: LocationAccuracy.high,
      distanceFilter: _minDistanceFilter.toInt(),
    );

    _positionStream = Geolocator.getPositionStream(
      locationSettings: locationSettings,
    ).listen(
      _onPositionUpdate,
      onError: (error) {
        Logger.e('LocationService', '位置流错误: $error');
      },
    );

    Logger.i('LocationService', '开始位置追踪');
  }

  /// 停止持续定位
  Future<void> stopTracking() async {
    if (!_isTracking) return;

    await _positionStream?.cancel();
    _positionStream = null;
    _isTracking = false;
    notifyListeners();

    Logger.i('LocationService', '停止位置追踪');
  }

  /// 位置更新回调
  void _onPositionUpdate(Position position) async {
    _currentPosition = position;
    _addToHistory(position);
    
    // 通知UI更新
    notifyListeners();
    
    // 发送位置更新流事件
    final userLocation = UserLocation.fromPosition(position);
    _locationUpdateController.add(userLocation);
    
    // 发送到服务器（节流控制）
    await _sendLocationToServer(userLocation);
    
    // 检查地理围栏
    _checkGeofences(position);
  }

  /// 添加到历史记录
  void _addToHistory(Position position) {
    final location = UserLocation.fromPosition(position);
    _locationHistory.add(location);
    
    // 限制历史记录大小
    if (_locationHistory.length > _maxHistorySize) {
      _locationHistory.removeAt(0);
    }
  }

  /// 发送位置到服务器
  DateTime? _lastServerUpdate;
  
  Future<void> _sendLocationToServer(UserLocation location) async {
    // 节流控制: 30秒间隔
    if (_lastServerUpdate != null) {
      final elapsed = DateTime.now().difference(_lastServerUpdate!);
      if (elapsed < _locationUpdateInterval) return;
    }

    try {
      await _apiService.post('/lbs/location/update', {
        'latitude': location.latitude,
        'longitude': location.longitude,
        'accuracy': location.accuracy,
        'altitude': location.altitude,
        'speed': location.speed,
        'timestamp': location.timestamp.toIso8601String(),
      });
      
      _lastServerUpdate = DateTime.now();
      Logger.d('LocationService', '位置已同步到服务器');
    } catch (e) {
      Logger.w('LocationService', '位置同步失败: $e');
    }
  }

  /// 单次获取当前位置
  Future<LatLng?> getCurrentLocation() async {
    if (!_isInitialized) {
      final initialized = await initialize();
      if (!initialized) return null;
    }
    
    final position = await _getCurrentPosition();
    if (position == null) return null;
    
    return LatLng(position.latitude, position.longitude);
  }

  /// 计算两点之间的距离（米）
  double calculateDistance(LatLng from, LatLng to) {
    return Geolocator.distanceBetween(
      from.latitude,
      from.longitude,
      to.latitude,
      to.longitude,
    );
  }

  /// 计算Haversine距离
  double haversineDistance(LatLng from, LatLng to) {
    const double R = 6371000; // 地球半径（米）
    final double lat1Rad = from.latitude * pi / 180;
    final double lat2Rad = to.latitude * pi / 180;
    final double deltaLat = (to.latitude - from.latitude) * pi / 180;
    final double deltaLon = (to.longitude - from.longitude) * pi / 180;

    final double a = sin(deltaLat / 2) * sin(deltaLat / 2) +
        cos(lat1Rad) * cos(lat2Rad) * sin(deltaLon / 2) * sin(deltaLon / 2);
    final double c = 2 * atan2(sqrt(a), sqrt(1 - a));

    return R * c;
  }

  // ==================== 地理围栏功能 ====================

  /// 添加地理围栏
  Future<bool> addGeofence({
    required String id,
    required LatLng center,
    required double radius,
    String? name,
    Map<String, dynamic>? metadata,
  }) async {
    try {
      final geofence = GeofenceRegion(
        id: id,
        center: center,
        radius: radius,
        name: name ?? '围栏_$id',
        metadata: metadata,
      );
      
      _geofences[id] = geofence;
      
      // 同步到服务器
      await _apiService.post('/lbs/geofence/create', {
        'id': id,
        'name': geofence.name,
        'latitude': center.latitude,
        'longitude': center.longitude,
        'radius': radius,
        'metadata': metadata,
      });
      
      Logger.i('LocationService', '添加地理围栏: $id, 半径: ${radius}m');
      return true;
    } catch (e) {
      Logger.e('LocationService', '添加地理围栏失败: $e');
      return false;
    }
  }

  /// 移除地理围栏
  Future<bool> removeGeofence(String id) async {
    try {
      _geofences.remove(id);
      
      await _apiService.delete('/lbs/geofence/$id');
      
      Logger.i('LocationService', '移除地理围栏: $id');
      return true;
    } catch (e) {
      Logger.e('LocationService', '移除地理围栏失败: $e');
      return false;
    }
  }

  /// 检查所有地理围栏
  void _checkGeofences(Position position) {
    final userLocation = LatLng(position.latitude, position.longitude);
    
    for (final geofence in _geofences.values) {
      final distance = calculateDistance(userLocation, geofence.center);
      final isInside = distance <= geofence.radius;
      
      // 检测状态变化
      if (isInside != geofence.isInside) {
        geofence.isInside = isInside;
        
        final event = GeofenceEvent(
          geofenceId: geofence.id,
          geofenceName: geofence.name,
          eventType: isInside ? GeofenceEventType.enter : GeofenceEventType.exit,
          timestamp: DateTime.now(),
          location: userLocation,
        );
        
        _geofenceController.add(event);
        _sendGeofenceEventToServer(event);
        
        Logger.i('LocationService', 
            '地理围栏事件: ${geofence.name} - ${isInside ? "进入" : "离开"}');
      }
    }
  }

  /// 发送地理围栏事件到服务器
  Future<void> _sendGeofenceEventToServer(GeofenceEvent event) async {
    try {
      await _apiService.post('/lbs/geofence/event', {
        'geofenceId': event.geofenceId,
        'eventType': event.eventType.toString().split('.').last,
        'timestamp': event.timestamp.toIso8601String(),
        'latitude': event.location.latitude,
        'longitude': event.location.longitude,
      });
    } catch (e) {
      Logger.w('LocationService', '发送地理围栏事件失败: $e');
    }
  }

  /// 获取用户地理围栏列表
  List<GeofenceRegion> getGeofences() {
    return List.unmodifiable(_geofences.values);
  }

  /// 清除所有地理围栏
  Future<void> clearGeofences() async {
    _geofences.clear();
    Logger.i('LocationService', '清除所有地理围栏');
  }

  // ==================== 清理 ====================

  @override
  void dispose() {
    stopTracking();
    _geofenceController.close();
    _locationUpdateController.close();
    super.dispose();
  }
}

/// 地理围栏区域
class GeofenceRegion {
  final String id;
  final LatLng center;
  final double radius;
  final String name;
  final Map<String, dynamic>? metadata;
  bool isInside = false;

  GeofenceRegion({
    required this.id,
    required this.center,
    required this.radius,
    required this.name,
    this.metadata,
  });

  Map<String, dynamic> toJson() => {
    'id': id,
    'name': name,
    'center': {'lat': center.latitude, 'lng': center.longitude},
    'radius': radius,
    'metadata': metadata,
  };
}

/// 地理围栏事件
class GeofenceEvent {
  final String geofenceId;
  final String geofenceName;
  final GeofenceEventType eventType;
  final DateTime timestamp;
  final LatLng location;

  GeofenceEvent({
    required this.geofenceId,
    required this.geofenceName,
    required this.eventType,
    required this.timestamp,
    required this.location,
  });

  Map<String, dynamic> toJson() => {
    'geofenceId': geofenceId,
    'geofenceName': geofenceName,
    'eventType': eventType.toString().split('.').last,
    'timestamp': timestamp.toIso8601String(),
    'location': {'lat': location.latitude, 'lng': location.longitude},
  };
}

enum GeofenceEventType { enter, exit }
