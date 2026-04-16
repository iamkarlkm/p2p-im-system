import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:web_socket_channel/web_socket_channel.dart';
import 'package:web_socket_channel/io.dart';

/// WebSocket客户端 - 提供实时消息推送功能
class WebSocketClient {
  static final WebSocketClient _instance = WebSocketClient._internal();
  factory WebSocketClient() => _instance;
  WebSocketClient._internal();

  WebSocketChannel? _channel;
  String? _wsUrl;
  String? _authToken;
  String? _userId;

  // 连接状态
  bool _isConnected = false;
  bool _isConnecting = false;
  int _reconnectAttempts = 0;
  Timer? _reconnectTimer;
  Timer? _heartbeatTimer;

  // 流控制器
  final _messageController = StreamController<Map<String, dynamic>>.broadcast();
  final _connectionController = StreamController<bool>.broadcast();

  // 回调函数
  Function(Map<String, dynamic>)? onMessage;
  Function()? onConnect;
  Function(String)? onDisconnect;
  Function(dynamic)? onError;

  // Getters
  bool get isConnected => _isConnected;
  bool get isConnecting => _isConnecting;
  Stream<Map<String, dynamic>> get messageStream => _messageController.stream;
  Stream<bool> get connectionStream => _connectionController.stream;

  /// 初始化WebSocket客户端
  void initialize(String wsUrl, {String? authToken, String? userId}) {
    _wsUrl = wsUrl;
    _authToken = authToken;
    _userId = userId;
  }

  /// 连接WebSocket
  Future<void> connect() async {
    if (_isConnected || _isConnecting) return;
    if (_wsUrl == null) throw Exception('WebSocket URL not initialized');

    _isConnecting = true;

    try {
      // 构建连接URL
      final uri = Uri.parse(_wsUrl!).replace(queryParameters: {
        if (_authToken != null) 'token': _authToken,
        if (_userId != null) 'userId': _userId,
      });

      // 建立连接
      _channel = IOWebSocketChannel.connect(uri.toString());

      // 监听消息
      _channel!.stream.listen(
        _onMessage,
        onError: _onError,
        onDone: _onDone,
      );

      _isConnected = true;
      _isConnecting = false;
      _reconnectAttempts = 0;

      // 启动心跳
      _startHeartbeat();

      // 通知连接成功
      _connectionController.add(true);
      onConnect?.call();

      // 发送连接确认消息
      _sendConnectAck();

    } catch (e) {
      _isConnecting = false;
      _scheduleReconnect();
      onError?.call(e);
    }
  }

  /// 断开连接
  Future<void> disconnect() async {
    _reconnectTimer?.cancel();
    _heartbeatTimer?.cancel();

    if (_channel != null) {
      // 发送断开连接消息
      _sendDisconnectMessage();
      await _channel!.sink.close();
      _channel = null;
    }

    _isConnected = false;
    _isConnecting = false;
    _connectionController.add(false);
  }

  /// 重新连接
  Future<void> reconnect() async {
    await disconnect();
    await Future.delayed(Duration(seconds: 1));
    await connect();
  }

  /// 发送消息
  void send(String message) {
    if (!_isConnected) {
      throw Exception('WebSocket not connected');
    }
    _channel!.sink.add(message);
  }

  /// 发送JSON消息
  void sendJson(Map<String, dynamic> data) {
    send(jsonEncode(data));
  }

  /// 发送聊天消息
  void sendChatMessage({
    required String conversationId,
    required String content,
    String messageType = 'TEXT',
    Map<String, dynamic>? extra,
  }) {
    sendJson({
      'type': 'CHAT_MESSAGE',
      'conversationId': conversationId,
      'content': content,
      'messageType': messageType,
      'extra': extra,
      'timestamp': DateTime.now().millisecondsSinceEpoch,
    });
  }

  /// 发送消息已读回执
  void sendReadReceipt(String messageId) {
    sendJson({
      'type': 'READ_RECEIPT',
      'messageId': messageId,
      'timestamp': DateTime.now().millisecondsSinceEpoch,
    });
  }

  /// 发送正在输入状态
  void sendTypingStatus(String conversationId, bool isTyping) {
    sendJson({
      'type': 'TYPING_STATUS',
      'conversationId': conversationId,
      'isTyping': isTyping,
      'timestamp': DateTime.now().millisecondsSinceEpoch,
    });
  }

  /// 加入会话
  void joinConversation(String conversationId) {
    sendJson({
      'type': 'JOIN_CONVERSATION',
      'conversationId': conversationId,
    });
  }

  /// 离开会话
  void leaveConversation(String conversationId) {
    sendJson({
      'type': 'LEAVE_CONVERSATION',
      'conversationId': conversationId,
    });
  }

  // ==================== 私有方法 ====================

  /// 消息处理
  void _onMessage(dynamic data) {
    try {
      final message = jsonDecode(data as String) as Map<String, dynamic>;

      // 处理服务器响应
      switch (message['type']) {
        case 'PONG':
          // 心跳响应，忽略
          break;
        case 'ACK':
          // 确认消息，忽略
          break;
        case 'ERROR':
          // 错误消息
          onError?.call(message['error'] ?? 'Unknown error');
          break;
        default:
          // 转发消息
          _messageController.add(message);
          onMessage?.call(message);
      }
    } catch (e) {
      print('Error parsing message: $e');
    }
  }

  /// 错误处理
  void _onError(dynamic error) {
    print('WebSocket error: $error');
    _isConnected = false;
    _isConnecting = false;
    _connectionController.add(false);
    onError?.call(error);
    _scheduleReconnect();
  }

  /// 连接关闭处理
  void _onDone() {
    print('WebSocket connection closed');
    _isConnected = false;
    _isConnecting = false;
    _connectionController.add(false);
    onDisconnect?.call('Connection closed');
    _scheduleReconnect();
  }

  /// 安排重连
  void _scheduleReconnect() {
    if (_reconnectTimer != null && _reconnectTimer!.isActive) return;
    if (_reconnectAttempts >= 10) return; // 最大重试次数

    _reconnectAttempts++;
    final delay = Duration(seconds: _calculateReconnectDelay());

    print('Scheduling reconnect in ${delay.inSeconds}s (attempt $_reconnectAttempts)');

    _reconnectTimer = Timer(delay, () {
      connect();
    });
  }

  /// 计算重连延迟（指数退避）
  int _calculateReconnectDelay() {
    // 指数退避: 1s, 2s, 4s, 8s, 16s, 30s, 30s...
    final delay = _reconnectAttempts <= 5
        ? (1 << _reconnectAttempts)
        : 30;
    return delay;
  }

  /// 启动心跳
  void _startHeartbeat() {
    _heartbeatTimer?.cancel();
    _heartbeatTimer = Timer.periodic(Duration(seconds: 30), (_) {
      if (_isConnected) {
        _sendPing();
      }
    });
  }

  /// 发送Ping
  void _sendPing() {
    sendJson({'type': 'PING', 'timestamp': DateTime.now().millisecondsSinceEpoch});
  }

  /// 发送连接确认
  void _sendConnectAck() {
    sendJson({
      'type': 'CONNECT',
      'userId': _userId,
      'timestamp': DateTime.now().millisecondsSinceEpoch,
    });
  }

  /// 发送断开连接消息
  void _sendDisconnectMessage() {
    try {
      sendJson({
        'type': 'DISCONNECT',
        'timestamp': DateTime.now().millisecondsSinceEpoch,
      });
    } catch (_) {
      // 忽略错误
    }
  }

  /// 清理资源
  void dispose() {
    disconnect();
    _messageController.close();
    _connectionController.close();
  }
}

/// WebSocket消息类型
class WebSocketMessageType {
  static const String CHAT_MESSAGE = 'CHAT_MESSAGE';
  static const String READ_RECEIPT = 'READ_RECEIPT';
  static const String TYPING_STATUS = 'TYPING_STATUS';
  static const String JOIN_CONVERSATION = 'JOIN_CONVERSATION';
  static const String LEAVE_CONVERSATION = 'LEAVE_CONVERSATION';
  static const String USER_ONLINE = 'USER_ONLINE';
  static const String USER_OFFLINE = 'USER_OFFLINE';
  static const String GROUP_JOIN = 'GROUP_JOIN';
  static const String GROUP_LEAVE = 'GROUP_LEAVE';
  static const String SYSTEM_NOTICE = 'SYSTEM_NOTICE';
  static const String PING = 'PING';
  static const String PONG = 'PONG';
}
