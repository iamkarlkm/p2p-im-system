import 'dart:async';
import 'package:dio/dio.dart';
import '../models/typing_user.dart';
import '../services/api_service.dart';

class TypingService {
  final ApiService _api = ApiService();
  final Map<String, Timer> _debounceTimers = {};
  final Map<String, Timer> _stopTimers = {};
  final Map<String, List<void Function(List<TypingUser>)>> _listeners = {};
  final Map<String, List<TypingUser>> _typingUsers = {};

  static const _debounceMs = 1500;
  static const _stopDelayMs = 3000;

  void onTypingChanged(String conversationId, void Function(List<TypingUser>) callback) {
    _listeners[conversationId] ??= [];
    _listeners[conversationId]!.add(callback);
  }

  void removeListener(String conversationId, void Function(List<TypingUser>) callback) {
    _listeners[conversationId]?.remove(callback);
  }

  void startTyping(String conversationId, String conversationType) {
    _stopTimers[conversationId]?.cancel();
    _debounceTimers[conversationId]?.cancel();

    _debounceTimers[conversationId] = Timer(const Duration(milliseconds: _debounceMs), () async {
      try {
        await _api.post('/typing/start', data: {
          'conversationId': conversationId,
          'conversationType': conversationType,
        });
      } catch (e) {
        // silent
      }
      _debounceTimers.remove(conversationId);

      _stopTimers[conversationId] = Timer(const Duration(milliseconds: _stopDelayMs), () {
        stopTyping(conversationId, conversationType);
      });
    });
  }

  Future<void> stopTyping(String conversationId, String conversationType) async {
    _debounceTimers[conversationId]?.cancel();
    _debounceTimers.remove(conversationId);
    _stopTimers[conversationId]?.cancel();
    _stopTimers.remove(conversationId);
    try {
      await _api.post('/typing/stop', data: {
        'conversationId': conversationId,
        'conversationType': conversationType,
      });
    } catch (e) {
      // silent
    }
  }

  Future<List<TypingUser>> fetchTypingStatus(String conversationId) async {
    try {
      final resp = await _api.get('/typing/${Uri.encodeComponent(conversationId)}');
      final list = resp.data as List<dynamic>;
      return list.map((e) => TypingUser.fromJson(e as Map<String, dynamic>)).toList();
    } catch (e) {
      return [];
    }
  }

  void handleWebSocketEvent(Map<String, dynamic> payload) {
    final type = payload['type'] as String?;
    final data = payload['data'] as Map<String, dynamic>?;
    if (data == null) return;
    final convId = data['conversationId'] as String?;
    if (convId == null) return;

    final user = TypingUser.fromJson(data);
    if (type == 'typing') {
      _addTypingUser(convId, user);
    } else if (type == 'stop_typing') {
      _removeTypingUser(convId, user.userId);
    }
  }

  void _addTypingUser(String convId, TypingUser user) {
    _typingUsers[convId] ??= [];
    final idx = _typingUsers[convId]!.indexWhere((u) => u.userId == user.userId);
    if (idx >= 0) {
      _typingUsers[convId]![idx] = user;
    } else {
      _typingUsers[convId]!.add(user);
    }
    _notifyListeners(convId);
  }

  void _removeTypingUser(String convId, String userId) {
    _typingUsers[convId]?.removeWhere((u) => u.userId == userId);
    _notifyListeners(convId);
  }

  void _notifyListeners(String convId) {
    final cbs = _listeners[convId] ?? [];
    final users = _typingUsers[convId] ?? [];
    for (final cb in cbs) {
      cb(List.from(users));
    }
  }

  void dispose() {
    for (final t in _debounceTimers.values) {
      t.cancel();
    }
    for (final t in _stopTimers.values) {
      t.cancel();
    }
    _debounceTimers.clear();
    _stopTimers.clear();
    _listeners.clear();
    _typingUsers.clear();
  }
}
