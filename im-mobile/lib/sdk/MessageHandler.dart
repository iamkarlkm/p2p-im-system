import 'dart:async';
import 'dart:convert';

import 'IMClientSDK.dart';

/// 消息处理器
/// 负责解析和处理收到的消息
class MessageHandler {
  // 消息流
  final StreamController<ChatMessage> _messageController = 
      StreamController<ChatMessage>.broadcast();
  Stream<ChatMessage> get messageStream => _messageController.stream;

  // 响应流（用于请求-响应模式）
  final StreamController<Map<String, dynamic>> _responseController = 
      StreamController<Map<String, dynamic>>.broadcast();
  Stream<Map<String, dynamic>> get responseStream => _responseController.stream;

  // 系统消息流
  final StreamController<SystemMessage> _systemMessageController = 
      StreamController<SystemMessage>.broadcast();
  Stream<SystemMessage> get systemMessageStream => _systemMessageController.stream;

  /// 处理收到的消息
  void handleMessage(String rawMessage) {
    try {
      final data = jsonDecode(rawMessage);
      final messageType = data['type'];

      switch (messageType) {
        case 'chat':
          _handleChatMessage(data);
          break;
        case 'system':
          _handleSystemMessage(data);
          break;
        case 'response':
          _handleResponse(data);
          break;
        case 'ack':
          _handleAck(data);
          break;
        case 'presence':
          _handlePresence(data);
          break;
        default:
          print('Unknown message type: $messageType');
      }
    } catch (e) {
      print('Failed to parse message: $e');
    }
  }

  /// 处理聊天消息
  void _handleChatMessage(Map<String, dynamic> data) {
    final message = ChatMessage(
      id: data['id'] ?? '',
      conversationId: data['conversationId'] ?? '',
      senderId: data['senderId'] ?? '',
      content: data['content'] ?? '',
      type: data['messageType'] ?? 'text',
      timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
      isRead: false,
    );

    _messageController.add(message);
  }

  /// 处理系统消息
  void _handleSystemMessage(Map<String, dynamic> data) {
    final message = SystemMessage(
      id: data['id'] ?? '',
      type: data['subType'] ?? 'unknown',
      content: data['content'] ?? '',
      timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
    );

    _systemMessageController.add(message);
  }

  /// 处理响应消息
  void _handleResponse(Map<String, dynamic> data) {
    _responseController.add(data);
  }

  /// 处理确认消息
  void _handleAck(Map<String, dynamic> data) {
    // 确认消息通常用于请求-响应模式
    _responseController.add(data);
  }

  /// 处理在线状态消息
  void _handlePresence(Map<String, dynamic> data) {
    final presence = PresenceUpdate(
      userId: data['userId'] ?? '',
      status: data['status'] ?? 'offline',
      timestamp: data['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
    );

    // TODO: 分发到状态监听
    print('User ${presence.userId} is ${presence.status}');
  }

  /// 释放资源
  void dispose() {
    _messageController.close();
    _responseController.close();
    _systemMessageController.close();
  }
}

/// 系统消息模型
class SystemMessage {
  final String id;
  final String type;
  final String content;
  final int timestamp;

  SystemMessage({
    required this.id,
    required this.type,
    required this.content,
    required this.timestamp,
  });
}

/// 在线状态更新模型
class PresenceUpdate {
  final String userId;
  final String status; // 'online', 'offline', 'away'
  final int timestamp;

  PresenceUpdate({
    required this.userId,
    required this.status,
    required this.timestamp,
  });
}
