/**
 * 推送通知服务 (Push Notification Service)
 * 
 * 支持 iOS (APNs) 和 Android (FCM/厂商通道)
 */

import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:flutter/foundation.dart';

// ==================== 枚举定义 ====================

enum PushPlatform {
  iOS('iOS'),
  Android('Android'),
  Web('Web'),
  Unknown('Unknown');

  final String value;
  const PushPlatform(this.value);
}

enum PushType {
  notification('notification'),
  data('data'),
  silent('silent'),
  voip('voip');

  final String value;
  const PushType(this.value);
}

enum PushPriority {
  high('high'),
  normal('normal'),
  low('low');

  final String value;
  const PushPriority(this.value);
}

enum NotificationChannel {
  chat('chat', '聊天消息', '聊天消息通知'),
  system('system', '系统通知', '系统通知'),
  friend('friend', '好友通知', '好友动态通知'),
  call('call', '通话', '来电通知'),
  activity('activity', '互动', '互动提醒');

  final String id;
  final String name;
  final String desc;
  const NotificationChannel(this.id, this.name, this.desc);
}

// ==================== 数据模型 ====================

class PushMessage {
  final String? title;
  final String? body;
  final String? subtitle;
  final String? icon;
  final String? sound;
  final String? badge;
  final String? color;
  final String? tag;
  final String? channelId;
  final String? category;
  final Map<String, String>? data;
  final PushPriority priority;
  final PushType type;
  final String? messageId;
  final String? conversationId;
  final String? conversationType;
  final int? senderId;
  final String? senderName;
  final String? senderAvatar;
  final String? mergeKey;

  PushMessage({
    this.title,
    this.body,
    this.subtitle,
    this.icon,
    this.sound,
    this.badge,
    this.color,
    this.tag,
    this.channelId,
    this.category,
    this.data,
    this.priority = PushPriority.normal,
    this.type = PushType.notification,
    this.messageId,
    this.conversationId,
    this.conversationType,
    this.senderId,
    this.senderName,
    this.senderAvatar,
    this.mergeKey,
  });

  factory PushMessage.fromMap(Map<String, dynamic> map) {
    return PushMessage(
      title: map['title'] as String?,
      body: map['body'] as String?,
      subtitle: map['subtitle'] as String?,
      icon: map['icon'] as String?,
      sound: map['sound'] as String?,
      badge: map['badge'] as String?,
      color: map['color'] as String?,
      tag: map['tag'] as String?,
      channelId: map['channel_id'] as String?,
      category: map['category'] as String?,
      data: map['data'] != null ? Map<String, String>.from(map['data']) : null,
      priority: PushPriority.values.firstWhere(
        (e) => e.value == map['priority'],
        orElse: () => PushPriority.normal,
      ),
      type: PushType.values.firstWhere(
        (e) => e.value == map['type'],
        orElse: () => PushType.notification,
      ),
      messageId: map['message_id']?.toString(),
      conversationId: map['conversation_id']?.toString(),
      conversationType: map['conversation_type'] as String?,
      senderId: map['sender_id'] as int?,
      senderName: map['sender_name'] as String?,
      senderAvatar: map['sender_avatar'] as String?,
      mergeKey: map['merge_key'] as String?,
    );
  }

  Map<String, dynamic> toMap() {
    return {
      if (title != null) 'title': title,
      if (body != null) 'body': body,
      if (subtitle != null) 'subtitle': subtitle,
      if (icon != null) 'icon': icon,
      if (sound != null) 'sound': sound,
      if (badge != null) 'badge': badge,
      if (color != null) 'color': color,
      if (tag != null) 'tag': tag,
      if (channelId != null) 'channel_id': channelId,
      if (category != null) 'category': category,
      if (data != null) 'data': data,
      'priority': priority.value,
      'type': type.value,
      if (messageId != null) 'message_id': messageId,
      if (conversationId != null) 'conversation_id': conversationId,
      if (conversationType != null) 'conversation_type': conversationType,
      if (senderId != null) 'sender_id': senderId,
      if (senderName != null) 'sender_name': senderName,
      if (senderAvatar != null) 'sender_avatar': senderAvatar,
      if (mergeKey != null) 'merge_key': mergeKey,
    };
  }
}

class RemoteMessage {
  final String messageId;
  final Map<String, dynamic> data;
  final String? from;
  final int sentTime;
  final int? ttl;
  final String? collapseKey;
  final String? messageType;
  final String? priority;
  final NotificationData? notification;

  RemoteMessage({
    required this.messageId,
    required this.data,
    this.from,
    required this.sentTime,
    this.ttl,
    this.collapseKey,
    this.messageType,
    this.priority,
    this.notification,
  });

  factory RemoteMessage.fromMap(Map<String, dynamic> map) {
    return RemoteMessage(
      messageId: map['message_id'] ?? '',
      data: Map<String, dynamic>.from(map['data'] ?? {}),
      from: map['from'] as String?,
      sentTime: map['sent_time'] ?? DateTime.now().millisecondsSinceEpoch,
      ttl: map['ttl'] as int?,
      collapseKey: map['collapse_key'] as String?,
      messageType: map['message_type'] as String?,
      priority: map['priority'] as String?,
      notification: map['notification'] != null
          ? NotificationData.fromMap(map['notification'])
          : null,
    );
  }
}

class NotificationData {
  final String? title;
  final String? body;
  final String? image;

  NotificationData({this.title, this.body, this.image});

  factory NotificationData.fromMap(Map<String, dynamic> map) {
    return NotificationData(
      title: map['title'] as String?,
      body: map['body'] as String?,
      image: map['image'] as String?,
    );
  }
}

class PushResult {
  final bool success;
  final String message;
  final String? messageId;
  final int timestamp;

  PushResult({
    required this.success,
    required this.message,
    this.messageId,
    required this.timestamp,
  });

  factory PushResult.fromMap(Map<String, dynamic> map) {
    return PushResult(
      success: map['success'] ?? false,
      message: map['message'] ?? '',
      messageId: map['message_id']?.toString(),
      timestamp: map['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
    );
  }
}

class DeviceInfo {
  final PushPlatform platform;
  final String version;
  final String model;
  final String name;

  DeviceInfo({
    required this.platform,
    required this.version,
    required this.model,
    required this.name,
  });

  Map<String, dynamic> toMap() {
    return {
      'platform': platform.value.toLowerCase(),
      'version': version,
      'model': model,
      'name': name,
    };
  }
}

class QuietHours {
  final int startHour;
  final int startMinute;
  final int endHour;
  final int endMinute;
  final List<int> weekdays; // 0=周日, 1=周一...

  QuietHours({
    this.startHour = 22,
    this.startMinute = 0,
    this.endHour = 8,
    this.endMinute = 0,
    List<int>? weekdays,
  }) : weekdays = weekdays ?? [0, 1, 2, 3, 4, 5, 6];

  bool isInQuietHours() {
    final now = DateTime.now();
    final currentTime = now.hour * 60 + now.minute;
    final startTime = startHour * 60 + startMinute;
    final endTime = endHour * 60 + endMinute;

    if (!weekdays.contains(now.weekday % 7)) {
      return false;
    }

    if (startTime <= endTime) {
      return currentTime >= startTime && currentTime <= endTime;
    } else {
      return currentTime >= startTime || currentTime <= endTime;
    }
  }
}

// ==================== 推送服务 ====================

class PushService {
  static final PushService _instance = PushService._internal();
  factory PushService() => _instance;
  PushService._internal();

  // 配置
  String? _vapidKey;
  String? _fcmToken;
  String? _apnsToken;
  int? _userId;
  DeviceInfo? _deviceInfo;

  // 回调
  Function(RemoteMessage)? onMessageReceived;
  Function(String)? onTokenRefresh;
  Function(Map<String, dynamic>)? onNotificationTapped;

  // 状态
  bool _initialized = false;
  QuietHours _quietHours = QuietHours();

  // ==================== 初始化 ====================

  Future<bool> initialize({
    required int userId,
    String? vapidKey,
    String? fcmSenderId,
    Function(RemoteMessage)? onMessageReceived,
    Function(String)? onTokenRefresh,
    Function(Map<String, dynamic>)? onNotificationTapped,
  }) async {
    if (_initialized) return true;

    _userId = userId;
    _vapidKey = vapidKey;
    this.onMessageReceived = onMessageReceived;
    this.onTokenRefresh = onTokenRefresh;
    this.onNotificationTapped = onNotificationTapped;

    _deviceInfo = await _getDeviceInfo();

    // 根据平台初始化
    if (Platform.isIOS) {
      return await _initIOS();
    } else if (Platform.isAndroid) {
      return await _initAndroid(fcmSenderId);
    }

    return false;
  }

  Future<bool> _initIOS() async {
    // TODO: 集成 flutter_local_notifications
    // TODO: 集成 firebase_messaging
    print('[Push] iOS push init');
    return true;
  }

  Future<bool> _initAndroid(String? fcmSenderId) async {
    // TODO: 集成 firebase_messaging
    // TODO: 初始化厂商通道
    print('[Push] Android push init, fcmSenderId: $fcmSenderId');
    return true;
  }

  Future<DeviceInfo> _getDeviceInfo() async {
    // 简化实现
    PushPlatform platform;
    if (Platform.isIOS) {
      platform = PushPlatform.iOS;
    } else if (Platform.isAndroid) {
      platform = PushPlatform.Android;
    } else {
      platform = PushPlatform.Unknown;
    }

    return DeviceInfo(
      platform: platform,
      version: '1.0.0',
      model: platform.value,
      name: 'Mobile Device',
    );
  }

  // ==================== Token 管理 ====================

  String? get fcmToken => _fcmToken;
  String? get apnsToken => _apnsToken;

  void setToken(String token) {
    if (_deviceInfo?.platform == PushPlatform.iOS) {
      _apnsToken = token;
    } else {
      _fcmToken = token;
    }
  }

  Future<bool> registerToken() async {
    try {
      final token = _deviceInfo?.platform == PushPlatform.iOS ? _apnsToken : _fcmToken;
      if (token == null || _userId == null) return false;

      final response = await _httpPost('/api/push/device/register', {
        'userId': _userId,
        'token': token,
        'platform': _deviceInfo?.platform.value.toLowerCase(),
        'deviceType': _deviceInfo?.model,
        'deviceName': _deviceInfo?.name,
        'osVersion': _deviceInfo?.version,
        'appVersion': '1.0.0',
      });

      return response['success'] == true;
    } catch (e) {
      print('[Push] Register token error: $e');
      return false;
    }
  }

  Future<bool> updateToken(String oldToken, String newToken) async {
    try {
      final response = await _httpPut('/api/push/device/update', {
        'oldToken': oldToken,
        'newToken': newToken,
      });
      return response['success'] == true;
    } catch (e) {
      print('[Push] Update token error: $e');
      return false;
    }
  }

  Future<bool> deleteToken(String token) async {
    try {
      final response = await _httpDelete('/api/push/device/${Uri.encodeComponent(token)}');
      return response['success'] == true;
    } catch (e) {
      print('[Push] Delete token error: $e');
      return false;
    }
  }

  // ==================== 消息处理 ====================

  void handleRemoteMessage(Map<String, dynamic> messageData) {
    try {
      final message = RemoteMessage.fromMap(messageData);
      print('[Push] Handle message: ${message.messageId}');

      // 检查免打扰
      if (_quietHours.isInQuietHours() && message.priority != 'high') {
        print('[Push] Skipped due to quiet hours');
        return;
      }

      // 触发回调
      onMessageReceived?.call(message);
    } catch (e) {
      print('[Push] Handle message error: $e');
    }
  }

  void handleNotificationTap(Map<String, dynamic> data) {
    onNotificationTapped?.call(data);
  }

  // ==================== 发送推送 ====================

  Future<PushResult> sendTestPush(int userId) async {
    try {
      final response = await _httpPost('/api/push/test/$userId', {});
      return PushResult.fromMap(response);
    } catch (e) {
      return PushResult(
        success: false,
        message: e.toString(),
        timestamp: DateTime.now().millisecondsSinceEpoch,
      );
    }
  }

  Future<PushResult> sendPush(int userId, PushMessage message) async {
    try {
      final response = await _httpPost('/api/push/send', {
        'userId': userId,
        ...message.toMap(),
      });
      return PushResult.fromMap(response);
    } catch (e) {
      return PushResult(
        success: false,
        message: e.toString(),
        timestamp: DateTime.now().millisecondsSinceEpoch,
      );
    }
  }

  Future<Map<int, PushResult>> sendBatchPush(List<int> userIds, PushMessage message) async {
    try {
      final response = await _httpPost('/api/push/send/batch', {
        'userIds': userIds,
        ...message.toMap(),
      });
      return (response['data'] as Map<String, dynamic>?)?.map(
            (key, value) => MapEntry(int.parse(key), PushResult.fromMap(value)),
          ) ??
          {};
    } catch (e) {
      return {for (var id in userIds) id: PushResult(success: false, message: e.toString(), timestamp: DateTime.now().millisecondsSinceEpoch)};
    }
  }

  Future<PushResult> sendSilentPush(int userId, Map<String, String> data) async {
    try {
      final response = await _httpPost('/api/push/silent', {
        'userId': userId,
        'data': data,
      });
      return PushResult.fromMap(response);
    } catch (e) {
      return PushResult(
        success: false,
        message: e.toString(),
        timestamp: DateTime.now().millisecondsSinceEpoch,
      );
    }
  }

  // ==================== 免打扰 ====================

  QuietHours get quietHours => _quietHours;

  void setQuietHours(QuietHours hours) {
    _quietHours = hours;
  }

  bool isInQuietHours() => _quietHours.isInQuietHours();

  // ==================== 工具 ====================

  Future<Map<String, dynamic>> _httpPost(String path, Map<String, dynamic> body) async {
    // 简化实现，实际应该用 http 包
    print('[Push] POST $path: $body');
    return {'success': true, 'message': 'OK'};
  }

  Future<Map<String, dynamic>> _httpPut(String path, Map<String, dynamic> body) async {
    print('[Push] PUT $path: $body');
    return {'success': true, 'message': 'OK'};
  }

  Future<Map<String, dynamic>> _httpDelete(String path) async {
    print('[Push] DELETE $path');
    return {'success': true, 'message': 'OK'};
  }

  String truncateContent(String content, int maxLength) {
    if (content.length <= maxLength) return content;
    return '${content.substring(0, maxLength)}...';
  }
}

// ==================== 推送消息构建器 ====================

class PushMessageBuilder {
  static PushMessage buildChatMessage({
    required int senderId,
    required String senderName,
    String? senderAvatar,
    required String conversationId,
    required String conversationType,
    required int messageId,
    required String messageType,
    required String messageContent,
    int unreadCount = 1,
  }) {
    String title = senderName;
    String body = messageContent;

    if (unreadCount > 1) {
      title = '$senderName ($unreadCount 条新消息)';
    }

    return PushMessage(
      title: title,
      body: body,
      senderId: senderId,
      senderName: senderName,
      senderAvatar: senderAvatar,
      conversationId: conversationId,
      conversationType: conversationType,
      messageId: messageId.toString(),
      messageType: messageType,
      data: {
        'type': 'chat_message',
        'sender_id': senderId.toString(),
        'conversation_id': conversationId,
        'conversation_type': conversationType,
        'message_id': messageId.toString(),
        'message_type': messageType,
        'content': messageContent,
      },
      category: 'chat_message',
      priority: PushPriority.high,
      mergeKey: 'chat:$conversationId',
    );
  }

  static PushMessage buildSystemNotification({
    required String title,
    required String content,
    Map<String, String>? extra,
  }) {
    final data = <String, String>{
      'type': 'system_notification',
      'title': title,
      'content': content,
      ...?extra,
    };

    return PushMessage(
      title: title,
      body: content,
      data: data,
      category: 'system',
      priority: PushPriority.normal,
    );
  }

  static PushMessage buildCallInvitation({
    required int callerId,
    required String callerName,
    String? callerAvatar,
    required String callType,
    required String callId,
    Map<String, String>? extras,
  }) {
    final data = <String, String>{
      'type': 'call_invitation',
      'call_id': callId,
      'call_type': callType,
      'caller_id': callerId.toString(),
      'action': 'incoming_call',
      ...?extras,
    };

    return PushMessage(
      title: callerName,
      body: callType == 'video' ? '视频通话邀请' : '语音通话邀请',
      senderId: callerId,
      senderName: callerName,
      senderAvatar: callerAvatar,
      data: data,
      type: PushType.voip,
      category: 'incoming_call',
      priority: PushPriority.high,
    );
  }
}

// ==================== 导出 ====================

final pushService = PushService();
