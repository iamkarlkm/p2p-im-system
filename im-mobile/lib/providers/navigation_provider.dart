import 'package:flutter/material.dart';
import 'package:latlong2/latlong.dart';

import '../models/navigation_models.dart';
import '../services/navigation_service.dart';
import 'package:flutter_tts/flutter_tts.dart';

/// 导航状态管理Provider
/// Navigation State Management Provider
class NavigationProvider extends ChangeNotifier {
  final NavigationService _navigationService;
  final FlutterTts _flutterTts = FlutterTts();

  NavigationProvider(this._navigationService);

  // 状态
  bool _isLoading = false;
  bool _isNavigating = false;
  bool _isPaused = false;
  bool _isSimulated = false;
  bool _isVoiceEnabled = true;
  bool _showTurnHint = false;
  bool _isOffRoute = false;
  bool _isRerouting = false;

  // 导航数据
  RoutePlan? _currentRoute;
  NavigationStatus? _navigationStatus;
  LatLng? _currentPosition;
  String _turnInstruction = '';
  String _remainingDistanceText = '';
  IconData _turnIcon = Icons.straight;

  // Getters
  bool get isLoading => _isLoading;
  bool get isNavigating => _isNavigating;
  bool get isPaused => _isPaused;
  bool get isVoiceEnabled => _isVoiceEnabled;
  bool get showTurnHint => _showTurnHint;
  bool get isOffRoute => _isOffRoute;
  bool get isRerouting => _isRerouting;
  RoutePlan? get currentRoute => _currentRoute;
  NavigationStatus? get navigationStatus => _navigationStatus;
  LatLng? get currentPosition => _currentPosition;
  String get turnInstruction => _turnInstruction;
  String get remainingDistanceText => _remainingDistanceText;
  IconData get turnIcon => _turnIcon;
  List<TrafficSegment> get trafficSegments => _navigationStatus?.trafficSegments ?? [];

  // 定时器
  Timer? _locationUpdateTimer;
  Timer? _simulationTimer;

  /// 开始导航
  Future<void> startNavigation({
    required RoutePlan routePlan,
    bool isSimulated = false,
  }) async {
    _isLoading = true;
    notifyListeners();

    try {
      _currentRoute = routePlan;
      _isSimulated = isSimulated;

      final response = await _navigationService.startNavigation(routePlan.id);
      _navigationStatus = response;
      _currentPosition = routePlan.origin;
      _isNavigating = true;
      _isPaused = false;

      // 初始化TTS
      await _flutterTts.setLanguage('zh-CN');
      await _flutterTts.setSpeechRate(0.5);

      // 播报开始导航
      if (_isVoiceEnabled) {
        await _speak('开始导航，全程${routePlan.distanceText}，预计${routePlan.durationText}到达');
      }

      // 开始位置更新
      _startLocationUpdates();

    } catch (e) {
      debugPrint('开始导航失败: $e');
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// 暂停导航
  void pauseNavigation() {
    _isPaused = true;
    _locationUpdateTimer?.pause();
    notifyListeners();
  }

  /// 恢复导航
  void resumeNavigation() {
    _isPaused = false;
    _locationUpdateTimer?.resume();
    notifyListeners();
  }

  /// 结束导航
  Future<void> endNavigation() async {
    _stopLocationUpdates();
    
    if (_navigationStatus?.sessionId != null) {
      try {
        await _navigationService.endNavigation(_navigationStatus!.sessionId);
      } catch (e) {
        debugPrint('结束导航失败: $e');
      }
    }

    if (_isVoiceEnabled) {
      await _speak('导航结束');
    }

    _isNavigating = false;
    _isPaused = false;
    _currentRoute = null;
    _navigationStatus = null;
    _currentPosition = null;
    notifyListeners();
  }

  /// 切换语音播报
  void toggleVoice() {
    _isVoiceEnabled = !_isVoiceEnabled;
    notifyListeners();
  }

  /// 开始位置更新
  void _startLocationUpdates() {
    if (_isSimulated) {
      _startSimulation();
    } else {
      _startRealLocationUpdates();
    }
  }

  /// 开始模拟导航
  void _startSimulation() {
    if (_currentRoute == null) return;

    final points = _currentRoute!.routePoints;
    int currentIndex = 0;

    _simulationTimer = Timer.periodic(const Duration(seconds: 2), (timer) async {
      if (_isPaused || !_isNavigating) {
        timer.cancel();
        return;
      }

      currentIndex += 5;
      if (currentIndex >= points.length) {
        currentIndex = points.length - 1;
        timer.cancel();
        await _onArrival();
        return;
      }

      _currentPosition = points[currentIndex];
      await _updateLocation(points[currentIndex]);
    });
  }

  /// 开始真实位置更新
  void _startRealLocationUpdates() {
    _locationUpdateTimer = Timer.periodic(
      const Duration(seconds: 3),
      (timer) async {
        if (_isPaused || !_isNavigating) {
          timer.cancel();
          return;
        }

        // 获取当前位置并更新
        final position = await _getCurrentPosition();
        if (position != null) {
          _currentPosition = position;
          await _updateLocation(position);
        }
      },
    );
  }

  /// 更新位置
  Future<void> _updateLocation(LatLng position) async {
    try {
      final response = await _navigationService.updateLocation(
        sessionId: _navigationStatus!.sessionId,
        lng: position.longitude,
        lat: position.latitude,
      );

      _navigationStatus = response;

      // 检查偏航
      if (response.isOffRoute && !_isOffRoute) {
        _isOffRoute = true;
        _handleOffRoute();
      } else if (!response.isOffRoute && _isOffRoute) {
        _isOffRoute = false;
      }

      // 更新转向提示
      if (response.currentStep != null) {
        _updateTurnHint(response.currentStep!);
      }

      // 检查是否到达
      if (response.remainingDistance < 50) {
        await _onArrival();
      }

      notifyListeners();
    } catch (e) {
      debugPrint('位置更新失败: $e');
    }
  }

  /// 更新转向提示
  void _updateTurnHint(CurrentStep step) {
    _turnInstruction = step.instruction;
    _remainingDistanceText = step.remainingDistanceText;
    _turnIcon = _getTurnIcon(step.action);

    // 接近转向点时显示全屏提示
    if (step.remainingDistance < 100 && !_showTurnHint) {
      _showTurnHint = true;
      if (_isVoiceEnabled) {
        _speak(step.voiceText);
      }

      // 3秒后隐藏
      Future.delayed(const Duration(seconds: 3), () {
        _showTurnHint = false;
        notifyListeners();
      });
    }
  }

  /// 处理偏航
  Future<void> _handleOffRoute() async {
    _isRerouting = true;
    notifyListeners();

    if (_isVoiceEnabled) {
      await _speak('您已偏航，正在重新规划路线');
    }

    try {
      final newRoute = await _navigationService.reroute(
        _navigationStatus!.sessionId,
      );
      _currentRoute = newRoute;
      
      if (_isVoiceEnabled) {
        await _speak('路线已重新规划');
      }
    } catch (e) {
      debugPrint('重新规划路线失败: $e');
    } finally {
      _isRerouting = false;
      notifyListeners();
    }
  }

  /// 到达终点
  Future<void> _onArrival() async {
    _stopLocationUpdates();
    
    if (_isVoiceEnabled) {
      await _speak('已到达目的地附近，导航结束');
    }

    _isNavigating = false;
    notifyListeners();
  }

  /// 停止位置更新
  void _stopLocationUpdates() {
    _locationUpdateTimer?.cancel();
    _simulationTimer?.cancel();
    _locationUpdateTimer = null;
    _simulationTimer = null;
  }

  /// 语音播报
  Future<void> _speak(String text) async {
    if (_isVoiceEnabled) {
      await _flutterTts.speak(text);
    }
  }

  /// 获取转向图标
  IconData _getTurnIcon(String action) {
    switch (action.toLowerCase()) {
      case 'turn_left':
        return Icons.turn_left;
      case 'turn_right':
        return Icons.turn_right;
      case 'uturn':
        return Icons.u_turn_left;
      case 'straight':
        return Icons.straight;
      case 'slight_left':
        return Icons.turn_slight_left;
      case 'slight_right':
        return Icons.turn_slight_right;
      case 'roundabout':
        return Icons.roundabout_left;
      default:
        return Icons.navigation;
    }
  }

  /// 获取当前位置（模拟）
  Future<LatLng?> _getCurrentPosition() async {
    // 实际项目中应使用geolocator获取真实位置
    return _currentPosition;
  }

  @override
  void dispose() {
    _stopLocationUpdates();
    _flutterTts.stop();
    super.dispose();
  }
}

// Timer扩展
extension TimerExtension on Timer {
  void pause() {}
  void resume() {}
}

// 导入
import 'dart:async';
