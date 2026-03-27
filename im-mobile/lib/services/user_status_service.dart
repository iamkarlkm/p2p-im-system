import 'dart:async';
import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/user_status.dart';

class UserStatusService extends ChangeNotifier {
  static const String _baseUrl = 'http://localhost:8080/api';
  static const String _statusCacheKey = 'user_status_cache';
  static const String _subscriptionsCacheKey = 'status_subscriptions';

  UserStatus? _currentUserStatus;
  final Map<String, UserStatus> _friendStatuses = {};
  final Map<String, UserStatus> _groupMemberStatuses = {};
  final Set<String> _subscriptions = {};
  final StreamController<UserStatus> _statusUpdateController = StreamController<UserStatus>.broadcast();
  Timer? _heartbeatTimer;
  Timer? _refreshTimer;
  bool _isInitialized = false;

  UserStatus? get currentUserStatus => _currentUserStatus;
  Map<String, UserStatus> get friendStatuses => Map.unmodifiable(_friendStatuses);
  Map<String, UserStatus> get groupMemberStatuses => Map.unmodifiable(_groupMemberStatuses);
  Stream<UserStatus> get statusUpdateStream => _statusUpdateController.stream;
  bool get isInitialized => _isInitialized;

  UserStatusService() {
    _init();
  }

  Future<void> _init() async {
    await _loadCachedStatus();
    await _loadSubscriptions();
    _startRefreshTimer();
    _isInitialized = true;
    notifyListeners();
  }

  Future<void> _loadCachedStatus() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final cachedData = prefs.getString(_statusCacheKey);
      if (cachedData != null) {
        final Map<String, dynamic> decoded = jsonDecode(cachedData);
        if (decoded.containsKey('current')) {
          _currentUserStatus = UserStatus.fromJson(decoded['current']);
        }
        if (decoded.containsKey('friends')) {
          final friendsMap = decoded['friends'] as Map<String, dynamic>;
          _friendStatuses.clear();
          friendsMap.forEach((key, value) {
            _friendStatuses[key] = UserStatus.fromJson(value);
          });
        }
      }
    } catch (e) {
      debugPrint('加载缓存状态失败: $e');
    }
  }

  Future<void> _saveCachedStatus() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final data = {
        'current': _currentUserStatus?.toJson(),
        'friends': _friendStatuses.map((key, value) => MapEntry(key, value.toJson())),
      };
      await prefs.setString(_statusCacheKey, jsonEncode(data));
    } catch (e) {
      debugPrint('保存缓存状态失败: $e');
    }
  }

  Future<void> _loadSubscriptions() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final subs = prefs.getStringList(_subscriptionsCacheKey);
      if (subs != null) {
        _subscriptions.addAll(subs);
      }
    } catch (e) {
      debugPrint('加载订阅列表失败: $e');
    }
  }

  Future<void> _saveSubscriptions() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.setStringList(_subscriptionsCacheKey, _subscriptions.toList());
    } catch (e) {
      debugPrint('保存订阅列表失败: $e');
    }
  }

  void _startRefreshTimer() {
    _refreshTimer?.cancel();
    _refreshTimer = Timer.periodic(const Duration(seconds: 30), (_) {
      _refreshAllStatuses();
    });
  }

  void _startHeartbeat() {
    _heartbeatTimer?.cancel();
    _heartbeatTimer = Timer.periodic(const Duration(seconds: 60), (_) {
      _sendHeartbeat();
    });
  }

  Future<void> _sendHeartbeat() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('auth_token');
      if (token == null) return;

      await http.post(
        Uri.parse('$_baseUrl/status/heartbeat'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
        },
      );
    } catch (e) {
      debugPrint('发送心跳失败: $e');
    }
  }

  Future<void> _refreshAllStatuses() async {
    await getCurrentUserStatus();
    if (_subscriptions.isNotEmpty) {
      await batchGetStatuses(_subscriptions.toList());
    }
  }

  Future<UserStatus?> getCurrentUserStatus() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('auth_token');
      if (token == null) return null;

      final response = await http.get(
        Uri.parse('$_baseUrl/status/current'),
        headers: {'Authorization': 'Bearer $token'},
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _currentUserStatus = UserStatus.fromJson(data);
        await _saveCachedStatus();
        notifyListeners();
        return _currentUserStatus;
      }
    } catch (e) {
      debugPrint('获取当前用户状态失败: $e');
    }
    return _currentUserStatus;
  }

  Future<bool> updateStatus({
    required String status,
    String? customStatus,
    String? customStatusEmoji,
  }) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('auth_token');
      if (token == null) return false;

      final response = await http.put(
        Uri.parse('$_baseUrl/status/update'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
        },
        body: jsonEncode({
          'status': status,
          'customStatus': customStatus,
          'customStatusEmoji': customStatusEmoji,
        }),
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        _currentUserStatus = UserStatus.fromJson(data);
        await _saveCachedStatus();
        notifyListeners();
        return true;
      }
    } catch (e) {
      debugPrint('更新状态失败: $e');
    }
    return false;
  }

  Future<UserStatus?> getUserStatus(String userId) async {
    if (_friendStatuses.containsKey(userId)) {
      return _friendStatuses[userId];
    }

    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('auth_token');
      if (token == null) return null;

      final response = await http.get(
        Uri.parse('$_baseUrl/status/user/$userId'),
        headers: {'Authorization': 'Bearer $token'},
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final userStatus = UserStatus.fromJson(data);
        _friendStatuses[userId] = userStatus;
        notifyListeners();
        return userStatus;
      }
    } catch (e) {
      debugPrint('获取用户状态失败: $e');
    }
    return null;
  }

  Future<Map<String, UserStatus>> batchGetStatuses(List<String> userIds) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('auth_token');
      if (token == null) return {};

      final response = await http.post(
        Uri.parse('$_baseUrl/status/batch'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer $token',
        },
        body: jsonEncode({'userIds': userIds}),
      );

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(response.body);
        final result = <String, UserStatus>{};
        for (final item in data) {
          final status = UserStatus.fromJson(item);
          result[status.userId] = status;
          if (_subscriptions.contains(status.userId)) {
            _friendStatuses[status.userId] = status;
          }
        }
        await _saveCachedStatus();
        notifyListeners();
        return result;
      }
    } catch (e) {
      debugPrint('批量获取状态失败: $e');
    }
    return {};
  }

  Future<bool> subscribeToStatus(String userId) async {
    if (_subscriptions.contains(userId)) return true;

    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('auth_token');
      if (token == null) return false;

      final response = await http.post(
        Uri.parse('$_baseUrl/status/subscribe/$userId'),
        headers: {'Authorization': 'Bearer $token'},
      );

      if (response.statusCode == 200) {
        _subscriptions.add(userId);
        await _saveSubscriptions();
        await getUserStatus(userId);
        return true;
      }
    } catch (e) {
      debugPrint('订阅状态失败: $e');
    }
    return false;
  }

  Future<bool> unsubscribeFromStatus(String userId) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('auth_token');
      if (token == null) return false;

      final response = await http.post(
        Uri.parse('$_baseUrl/status/unsubscribe/$userId'),
        headers: {'Authorization': 'Bearer $token'},
      );

      if (response.statusCode == 200) {
        _subscriptions.remove(userId);
        _friendStatuses.remove(userId);
        await _saveSubscriptions();
        notifyListeners();
        return true;
      }
    } catch (e) {
      debugPrint('取消订阅失败: $e');
    }
    return false;
  }

  void handleStatusUpdate(Map<String, dynamic> data) {
    try {
      final status = UserStatus.fromJson(data);
      if (status.userId == _currentUserStatus?.userId) {
        _currentUserStatus = status;
      } else {
        _friendStatuses[status.userId] = status;
      }
      _statusUpdateController.add(status);
      _saveCachedStatus();
      notifyListeners();
    } catch (e) {
      debugPrint('处理状态更新失败: $e');
    }
  }

  void updateStatusFromWebSocket(Map<String, dynamic> data) {
    handleStatusUpdate(data);
  }

  UserStatus? getFriendStatus(String friendId) {
    return _friendStatuses[friendId];
  }

  List<UserStatus> getOnlineFriends() {
    return _friendStatuses.values
        .where((s) => s.isOnline)
        .toList()
      ..sort((a, b) => b.lastSeen.compareTo(a.lastSeen));
  }

  List<UserStatus> getFriendsByStatus(String status) {
    return _friendStatuses.values
        .where((s) => s.status == status)
        .toList();
  }

  bool isFriendOnline(String friendId) {
    final status = _friendStatuses[friendId];
    return status?.isOnline ?? false;
  }

  String? getFriendCustomStatus(String friendId) {
    return _friendStatuses[friendId]?.customStatus;
  }

  Future<void> logout() async {
    _heartbeatTimer?.cancel();
    _refreshTimer?.cancel();
    _currentUserStatus = null;
    _friendStatuses.clear();
    _groupMemberStatuses.clear();
    _subscriptions.clear();
    
    final prefs = await SharedPreferences.getInstance();
    await prefs.remove(_statusCacheKey);
    await prefs.remove(_subscriptionsCacheKey);
    
    notifyListeners();
  }

  @override
  void dispose() {
    _heartbeatTimer?.cancel();
    _refreshTimer?.cancel();
    _statusUpdateController.close();
    super.dispose();
  }
}
