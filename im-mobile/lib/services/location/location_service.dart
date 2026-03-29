import 'dart:async';
import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:geolocator/geolocator.dart';
import 'package:im_mobile/services/navigation/navigation_service.dart';
import 'package:im_mobile/utils/logger.dart';

/// 位置服务
/// 
/// 提供定位、地理编码、逆地理编码等功能
class LocationService {
  static final LocationService _instance = LocationService._internal();
  factory LocationService() => _instance;
  LocationService._internal();

  // ==================== 状态 ====================
  bool _isInitialized = false;
  bool _isTracking = false;
  
  // 当前位置
  NavigationLocation? _currentLocation;
  
  // 位置权限状态
  LocationPermission _permission = LocationPermission.denied;
  
  // 位置流控制器
  final StreamController<NavigationLocation> _locationController = 
      StreamController<NavigationLocation>.broadcast();
  
  // ==================== Getters ====================
  bool get isInitialized => _isInitialized;
  bool get isTracking => _isTracking;
  NavigationLocation? get currentLocation => _currentLocation;
  LocationPermission get permission => _permission;
  
  Stream<NavigationLocation> get locationStream => _locationController.stream;

  /// 初始化位置服务
  Future<void> initialize() async {
    if (_isInitialized) return;
    
    IMLogger.i('LocationService', 'Initializing location service...');
    
    // 检查定位服务是否启用
    bool serviceEnabled = await Geolocator.isLocationServiceEnabled();
    if (!serviceEnabled) {
      IMLogger.w('LocationService', 'Location services are disabled');
      return;
    }
    
    // 检查权限
    _permission = await Geolocator.checkPermission();
    if (_permission == LocationPermission.denied) {
      _permission = await Geolocator.requestPermission();
      if (_permission == LocationPermission.denied) {
        IMLogger.w('LocationService', 'Location permissions are denied');
        return;
      }
    }
    
    if (_permission == LocationPermission.deniedForever) {
      IMLogger.w('LocationService', 'Location permissions are permanently denied');
      return;
    }
    
    _isInitialized = true;
    IMLogger.i('LocationService', 'Location service initialized');
  }

  /// 获取当前位置
  Future<LatLng> getCurrentLocation() async {
    if (!_isInitialized) {
      await initialize();
    }
    
    if (_currentLocation != null) {
      return _currentLocation!.latLng;
    }
    
    try {
      final position = await Geolocator.getCurrentPosition(
        desiredAccuracy: LocationAccuracy.best,
      );
      
      return LatLng(position.latitude, position.longitude);
    } catch (e) {
      IMLogger.e('LocationService', 'Failed to get current location', e);
      // 返回默认位置（北京天安门）
      return LatLng(39.9042, 116.4074);
    }
  }

  /// 开始持续定位
  Future<void> startTracking({
    LocationAccuracy accuracy = LocationAccuracy.best,
    int distanceFilter = 10,
  }) async {
    if (!_isInitialized) {
      await initialize();
    }
    
    if (_isTracking) return;
    
    IMLogger.i('LocationService', 'Starting location tracking...');
    
    final locationSettings = LocationSettings(
      accuracy: accuracy,
      distanceFilter: distanceFilter,
    );
    
    Geolocator.getPositionStream(locationSettings: locationSettings).listen(
      (Position position) {
        _onPositionUpdate(position);
      },
      onError: (error) {
        IMLogger.e('LocationService', 'Location stream error', error);
      },
    );
    
    _isTracking = true;
  }

  /// 停止定位
  void stopTracking() {
    _isTracking = false;
    IMLogger.i('LocationService', 'Location tracking stopped');
  }

  void _onPositionUpdate(Position position) {
    _currentLocation = NavigationLocation(
      latitude: position.latitude,
      longitude: position.longitude,
      speed: position.speed,
      heading: position.heading,
      timestamp: position.timestamp ?? DateTime.now(),
    );
    
    _locationController.add(_currentLocation!);
  }

  /// 地理编码（地址转坐标）
  Future<LatLng?> geocode(String address) async {
    // 实际实现需要调用地图服务商API
    // 这里返回模拟数据
    IMLogger.i('LocationService', 'Geocoding: $address');
    return LatLng(39.9042, 116.4074);
  }

  /// 逆地理编码（坐标转地址）
  Future<AddressInfo?> reverseGeocode(LatLng location) async {
    // 实际实现需要调用地图服务商API
    IMLogger.i('LocationService', 
      'Reverse geocoding: ${location.latitude}, ${location.longitude}');
    
    return AddressInfo(
      formattedAddress: '北京市东城区长安街1号',
      country: '中国',
      province: '北京市',
      city: '北京市',
      district: '东城区',
      street: '长安街',
      streetNumber: '1号',
      building: '东方广场',
    );
  }

  /// 计算两点间距离
  double calculateDistance(LatLng start, LatLng end) {
    return Geolocator.distanceBetween(
      start.latitude,
      start.longitude,
      end.latitude,
      end.longitude,
    );
  }

  /// 计算方位角
  double calculateBearing(LatLng start, LatLng end) {
    return Geolocator.bearingBetween(
      start.latitude,
      start.longitude,
      end.latitude,
      end.longitude,
    );
  }

  /// 释放资源
  void dispose() {
    stopTracking();
    _locationController.close();
  }
}

/// 地址信息
class AddressInfo {
  final String formattedAddress;
  final String? country;
  final String? province;
  final String? city;
  final String? district;
  final String? street;
  final String? streetNumber;
  final String? building;
  final String? postalCode;

  AddressInfo({
    required this.formattedAddress,
    this.country,
    this.province,
    this.city,
    this.district,
    this.street,
    this.streetNumber,
    this.building,
    this.postalCode,
  });

  /// 简短地址（市+区+街道）
  String get shortAddress {
    final parts = <String>[];
    if (city != null) parts.add(city!);
    if (district != null) parts.add(district!);
    if (street != null) parts.add(street!);
    return parts.join('');
  }

  @override
  String toString() => formattedAddress;
}
