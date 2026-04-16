import 'dart:async';
import 'dart:convert';

import 'ConnectionManager.dart';
import 'MessageHandler.dart';

/// 消息客户端
/// 负责消息的发送和接收
class MessageClient {
  final ConnectionManager _connectionManager;
  final MessageHandler _messageHandler;

  // 请求等待池
  final Map<String, Completer<dynamic>> _pendingRequests = {};

  MessageClient({
    required ConnectionManager connectionManager,
    required MessageHandler messageHandler,
  })  : _connectionManager = connectionManager,
        _messageHandler = messageHandler {
    // 监听响应消息
    _messageHandler.responseStream.listen(_handleResponse);
  }

  /// 发送消息
  Future<void> sendMessage({
    required String conversationId,
    required String content,
    required String type,
  }) async {
    final message = {
      'action': 'send_message',
      'conversationId': conversationId,
      'content': content,
      'type': type,
      'timestamp': DateTime.now().millisecondsSinceEpoch,
    };

    await _sendWithAck(message);
  }

  /// 发送私聊消息
  Future<void> sendPrivateMessage({
    required String fromUserId,
    required String toUserId,
    required String content,
    required String type,
  }) async {
    final message = {
      'action': 'send_private',
      'fromUserId': fromUserId,
      'toUserId': toUserId,
      'content': content,
      'type': type,
      'timestamp': DateTime.now().millisecondsSinceEpoch,
    };

    await _sendWithAck(message);
  }

  /// 发送群聊消息
  Future<void> sendGroupMessage({
    required String fromUserId,
    required String groupId,
    required String content,
    required String type,
  }) async {
    final message = {
      'action': 'send_group',
      'fromUserId': fromUserId,
      'groupId': groupId,
      'content': content,
      'type': type,
      'timestamp': DateTime.now().millisecondsSinceEpoch,
    };

    await _sendWithAck(message);
  }

  /// 获取历史消息
  Future<List<dynamic>> getHistoryMessages({
    required String conversationId,
    required int limit,
    String? beforeMessageId,
  }) async {
    final request = {
      'action': 'get_history',
      'conversationId': conversationId,
      'limit': limit,
      if (beforeMessageId != null) 'beforeMessageId': beforeMessageId,
    };

    final response = await _sendWithResponse(request);
    return response['messages'] ?? [];
  }

  /// 标记消息已读
  Future<void> markAsRead({
    required String conversationId,
    required String messageId,
  }) async {
    final message = {
      'action': 'mark_read',
      'conversationId': conversationId,
      'messageId': messageId,
    };

    await _sendWithAck(message);
  }

  /// 获取会话列表
  Future<List<dynamic>> getConversations({required String userId}) async {
    final request = {
      'action': 'get_conversations',
      'userId': userId,
    };

    final response = await _sendWithResponse(request);
    return response['conversations'] ?? [];
  }

  /// 创建群组
  Future<String> createGroup({
    required String creatorId,
    required String name,
    required List<String> memberIds,
  }) async {
    final request = {
      'action': 'create_group',
      'creatorId': creatorId,
      'name': name,
      'memberIds': memberIds,
    };

    final response = await _sendWithResponse(request);
    return response['groupId'];
  }

  /// 加入群组
  Future<void> joinGroup({
    required String userId,
    required String groupId,
  }) async {
    final message = {
      'action': 'join_group',
      'userId': userId,
      'groupId': groupId,
    };

    await _sendWithAck(message);
  }

  /// 离开群组
  Future<void> leaveGroup({
    required String userId,
    required String groupId,
  }) async {
    final message = {
      'action': 'leave_group',
      'userId': userId,
      'groupId': groupId,
    };

    await _sendWithAck(message);
  }

  /// 发送消息并等待确认
  Future<void> _sendWithAck(Map<String, dynamic> message) async {
    final completer = Completer<void>();
    final requestId = _generateRequestId();
    message['requestId'] = requestId;

    _pendingRequests[requestId] = completer;

    await _connectionManager.send(jsonEncode(message));

    // 5秒超时
    return completer.future.timeout(
      Duration(seconds: 5),
      onTimeout: () {
        _pendingRequests.remove(requestId);
        throw Exception('Request timeout');
      },
    );
  }

  /// 发送消息并等待响应
  Future<Map<String, dynamic>> _sendWithResponse(
      Map<String, dynamic> message) async {
    final completer = Completer<Map<String, dynamic>>();
    final requestId = _generateRequestId();
    message['requestId'] = requestId;

    _pendingRequests[requestId] = completer;

    await _connectionManager.send(jsonEncode(message));

    return completer.future.timeout(
      Duration(seconds: 5),
      onTimeout: () {
        _pendingRequests.remove(requestId);
        throw Exception('Request timeout');
      },
    );
  }

  /// 处理响应
  void _handleResponse(Map<String, dynamic> response) {
    final requestId = response['requestId'];
    if (requestId == null) return;

    final completer = _pendingRequests.remove(requestId);
    if (completer == null) return;

    if (response['error'] != null) {
      completer.completeError(Exception(response['error']));
    } else {
      completer.complete(response);
    }
  }

  String _generateRequestId() {
    return '${DateTime.now().millisecondsSinceEpoch}_${Random().nextInt(10000)}';
  }
}

class Random {
  static final _random = Random();
  int nextInt(int max) => _random.nextInt(max);
}
