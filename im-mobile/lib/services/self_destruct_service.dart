import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'dart:convert';

/**
 * 阅后即焚服务 - 移动端
 * 消息自动销毁功能
 */

/// 销毁计时器类型
enum TimerType {
  seconds5('SECONDS_5', '5秒', 5, '⏱️'),
  seconds30('SECONDS_30', '30秒', 30, '⏱️'),
  minute1('MINUTE_1', '1分钟', 60, '🔥'),
  minutes5('MINUTES_5', '5分钟', 300, '🔥'),
  hour1('HOUR_1', '1小时', 3600, '💣'),
  hours24('HOURS_24', '24小时', 86400, '💣'),
  custom('CUSTOM', '自定义', -1, '⚙️');

  final String value;
  final String label;
  final int seconds;
  final String icon;

  const TimerType(this.value, this.label, this.seconds, this.icon);
}

/// 销毁状态
enum DestroyStatus {
  pending('PENDING'),
  counting('COUNTING'),
  destroyed('DESTROYED'),
  expired('EXPIRED');

  final String value;

  const DestroyStatus(this.value);
}

/// 阅后即焚消息数据
class SelfDestructMessage {
  final int id;
  final int messageId;
  final int senderId;
  final int receiverId;
  final String timerType;
  final int durationSeconds;
  final DestroyStatus status;
  final DateTime createdAt;
  final DateTime? readAt;
  final DateTime? destroyedAt;
  final String? destroyReason;

  SelfDestructMessage({
    required this.id,
    required this.messageId,
    required this.senderId,
    required this.receiverId,
    required this.timerType,
    required this.durationSeconds,
    required this.status,
    required this.createdAt,
    this.readAt,
    this.destroyedAt,
    this.destroyReason,
  });

  factory SelfDestructMessage.fromJson(Map<String, dynamic> json) {
    return SelfDestructMessage(
      id: json['id'] ?? 0,
      messageId: json['messageId'] ?? 0,
      senderId: json['senderId'] ?? 0,
      receiverId: json['receiverId'] ?? 0,
      timerType: json['timerType'] ?? '',
      durationSeconds: json['durationSeconds'] ?? 0,
      status: DestroyStatus.values.firstWhere(
        (e) => e.value == json['status'],
        orElse: () => DestroyStatus.pending,
      ),
      createdAt: DateTime.parse(json['createdAt'] ?? DateTime.now().toIso8601String()),
      readAt: json['readAt'] != null ? DateTime.parse(json['readAt']) : null,
      destroyedAt: json['destroyedAt'] != null ? DateTime.parse(json['destroyedAt']) : null,
      destroyReason: json['destroyReason'],
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'messageId': messageId,
    'senderId': senderId,
    'receiverId': receiverId,
    'timerType': timerType,
    'durationSeconds': durationSeconds,
    'status': status.value,
    'createdAt': createdAt.toIso8601String(),
    'readAt': readAt?.toIso8601String(),
    'destroyedAt': destroyedAt?.toIso8601String(),
    'destroyReason': destroyReason,
  };
}

/// 销毁记录
class DestroyRecord {
  final int messageId;
  final String reason;
  final int? operatorId;
  final DateTime destroyTime;
  final String? note;

  DestroyRecord({
    required this.messageId,
    required this.reason,
    this.operatorId,
    required this.destroyTime,
    this.note,
  });

  factory DestroyRecord.fromJson(Map<String, dynamic> json) {
    return DestroyRecord(
      messageId: json['messageId'] ?? 0,
      reason: json['reason'] ?? '',
      operatorId: json['operatorId'],
      destroyTime: DateTime.parse(json['destroyTime'] ?? DateTime.now().toIso8601String()),
      note: json['note'],
    );
  }
}

/// 销毁状态响应
class DestroyStatusResponse {
  final DestroyStatus? status;
  final int remainingSeconds;

  DestroyStatusResponse({
    this.status,
    required this.remainingSeconds,
  });

  factory DestroyStatusResponse.fromJson(Map<String, dynamic> json) {
    return DestroyStatusResponse(
      status: json['status'] != null 
        ? DestroyStatus.values.firstWhere(
            (e) => e.value == json['status'],
            orElse: () => DestroyStatus.pending,
          )
        : null,
      remainingSeconds: json['remainingSeconds'] ?? -1,
    );
  }
}

/// 倒计时条目
class CountdownEntry {
  final int messageId;
  Timer? timer;
  int remainingSeconds;
  final Function(int)? onTick;
  final VoidCallback? onComplete;

  CountdownEntry({
    required this.messageId,
    required this.remainingSeconds,
    this.onTick,
    this.onComplete,
  });
}

/// 阅后即焚服务
class SelfDestructService {
  static const String _baseUrl = 'http://localhost:8080/api';
  
  final Map<int, CountdownEntry> _activeCountdowns = {};
  final Set<int> _destroyedMessages = {};

  /// API客户端
  final http.Client _client;
  final Map<String, String> _headers = {
    'Content-Type': 'application/json',
  };

  SelfDestructService({http.Client? client}) : _client = client ?? http.Client();

  /// 设置消息阅后即焚
  Future<SelfDestructMessage> setupSelfDestruct({
    required int messageId,
    required int senderId,
    required int receiverId,
    required TimerType timerType,
    int? customSeconds,
  }) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/self-destruct/setup'),
      headers: _headers,
      body: jsonEncode({
        'messageId': messageId,
        'senderId': senderId,
        'receiverId': receiverId,
        'timerType': timerType.value,
        'customSeconds': customSeconds,
      }),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return SelfDestructMessage.fromJson(data['data']);
    } else {
      throw Exception('设置阅后即焚失败: ${response.statusCode}');
    }
  }

  /// 标记消息已读
  Future<void> markAsRead({
    required int messageId,
    required int readerId,
  }) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/self-destruct/read?messageId=$messageId&readerId=$readerId'),
      headers: _headers,
    );

    if (response.statusCode != 200) {
      throw Exception('标记已读失败: ${response.statusCode}');
    }
  }

  /// 获取消息销毁状态
  Future<DestroyStatusResponse> getStatus(int messageId) async {
    final response = await _client.get(
      Uri.parse('$_baseUrl/self-destruct/status?messageId=$messageId'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return DestroyStatusResponse.fromJson(data);
    } else {
      throw Exception('获取状态失败: ${response.statusCode}');
    }
  }

  /// 手动销毁消息
  Future<void> destroyMessage({
    required int messageId,
    required int operatorId,
  }) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/self-destruct/destroy?messageId=$messageId&operatorId=$operatorId'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      _clearCountdown(messageId);
    } else {
      throw Exception('销毁消息失败: ${response.statusCode}');
    }
  }

  /// 批量销毁消息
  Future<void> batchDestroy({
    required List<int> messageIds,
    required int operatorId,
  }) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/self-destruct/batch-destroy'),
      headers: _headers,
      body: jsonEncode({
        'messageIds': messageIds,
        'operatorId': operatorId,
      }),
    );

    if (response.statusCode == 200) {
      for (final id in messageIds) {
        _clearCountdown(id);
      }
    } else {
      throw Exception('批量销毁失败: ${response.statusCode}');
    }
  }

  /// 获取销毁历史
  Future<List<DestroyRecord>> getDestroyHistory({
    required int userId,
    int page = 0,
    int size = 20,
  }) async {
    final response = await _client.get(
      Uri.parse('$_baseUrl/self-destruct/history?userId=$userId&page=$page&size=$size'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final List<dynamic> records = data['data'] ?? [];
      return records.map((e) => DestroyRecord.fromJson(e)).toList();
    } else {
      throw Exception('获取销毁历史失败: ${response.statusCode}');
    }
  }

  /// 开始本地倒计时
  void startLocalCountdown({
    required int messageId,
    required int durationSeconds,
    Function(int)? onTick,
    VoidCallback? onComplete,
  }) {
    // 清除已有倒计时
    _clearCountdown(messageId);

    int remaining = durationSeconds;

    final timer = Timer.periodic(const Duration(seconds: 1), (timer) {
      remaining--;
      onTick?.call(remaining);

      if (remaining <= 0) {
        _clearCountdown(messageId);
        _markAsDestroyed(messageId);
        onComplete?.call();
      }
    });

    _activeCountdowns[messageId] = CountdownEntry(
      messageId: messageId,
      remainingSeconds: remaining,
      onTick: onTick,
      onComplete: onComplete,
    )..timer = timer;
  }

  /// 清除倒计时
  void _clearCountdown(int messageId) {
    final entry = _activeCountdowns[messageId];
    if (entry != null) {
      entry.timer?.cancel();
      _activeCountdowns.remove(messageId);
    }
  }

  /// 标记消息为已销毁
  void _markAsDestroyed(int messageId) {
    _destroyedMessages.add(messageId);
    _clearCountdown(messageId);
  }

  /// 检查消息是否已销毁
  bool isDestroyed(int messageId) => _destroyedMessages.contains(messageId);

  /// 获取剩余秒数
  int getRemainingSeconds(int messageId) {
    final entry = _activeCountdowns[messageId];
    return entry?.remainingSeconds ?? -1;
  }

  /// 格式化剩余时间
  String formatRemainingTime(int seconds) {
    if (seconds < 0) return '';
    if (seconds < 60) return '${seconds}秒';
    if (seconds < 3600) return '${seconds ~/ 60}分${seconds % 60}秒';
    return '${seconds ~/ 3600}小时${(seconds % 3600) ~/ 60}分';
  }

  /// 获取计时器配置
  TimerType? getTimerConfig(String type) {
    try {
      return TimerType.values.firstWhere((e) => e.value == type);
    } catch (e) {
      return null;
    }
  }

  /// 释放资源
  void dispose() {
    for (final entry in _activeCountdowns.values) {
      entry.timer?.cancel();
    }
    _activeCountdowns.clear();
    _client.close();
  }
}
