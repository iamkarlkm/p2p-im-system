import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'dart:math';

import 'package:web_socket_channel/web_socket_channel.dart';
import 'package:web_socket_channel/io.dart';

/// 连接管理器
/// 负责WebSocket连接的生命周期管理
class ConnectionManager {
  final String serverUrl;
  final String token;

  WebSocketChannel? _channel;
  Timer? _heartbeatTimer;
  Timer? _reconnectTimer;

  ConnectionStatus _currentStatus = ConnectionStatus.disconnected;
  ConnectionStatus get currentStatus => _currentStatus;
  bool get isConnected => _currentStatus == ConnectionStatus.connected;

  // 重连配置
  int _reconnectAttempts = 0;
  static const int maxReconnectAttempts = 5;
  static const List<int> reconnectDelays = [1, 2, 5, 10, 30]; // 秒

  // 心跳配置
  static const int heartbeatInterval = 30; // 秒
  static const int heartbeatTimeout = 10; // 秒

  // 状态流
  final StreamController<ConnectionStatus> _statusController = 
      StreamController<ConnectionStatus>.broadcast();
  Stream<ConnectionStatus> get statusStream => _statusController.stream;

  // 消息流
  final StreamController<String> _messageController = 
      StreamController<String>.broadcast();
  Stream<String> get messageStream => _messageController.stream;

  ConnectionManager({
    required this.serverUrl,
    required this.token,
  });

  /// 连接服务器
  Future<void> connect() async {
    if (_currentStatus == ConnectionStatus.connecting ||
        _currentStatus == ConnectionStatus.connected) {
      return;
    }

    _updateStatus(ConnectionStatus.connecting);

    try {
      final wsUrl = '$serverUrl?token=$token';
      _channel = IOWebSocketChannel.connect(wsUrl);

      _channel!.stream.listen(
        _onMessage,
        onError: _onError,
        onDone: _onDisconnected,
        cancelOnError: false,
      );

      _updateStatus(ConnectionStatus.connected);
      _reconnectAttempts = 0;
      _startHeartbeat();

      print('WebSocket connected to $serverUrl');
    } catch (e) {
      print('WebSocket connection failed: $e');
      _updateStatus(ConnectionStatus.error);
      _scheduleReconnect();
    }
  }

  /// 断开连接
  Future<void> disconnect() async {
    _stopHeartbeat();
    _stopReconnect();

    await _channel?.sink.close();
    _channel = null;

    _updateStatus(ConnectionStatus.disconnected);
    print('WebSocket disconnected');
  }

  /// 发送消息
  Future<void> send(String message) async {
    if (!isConnected) {
      throw Exception('WebSocket not connected');
    }

    _channel!.sink.add(message);
  }

  /// 处理收到的消息
  void _onMessage(dynamic message) {
    if (message is String) {
      _messageController.add(message);

      // 处理pong响应
      try {
        final data = jsonDecode(message);
        if (data['type'] == 'pong') {
          // 心跳响应，连接正常
        }
      } catch (e) {
        // 非JSON消息，忽略
      }
    }
  }

  /// 处理错误
  void _onError(dynamic error) {
    print('WebSocket error: $error');
    _updateStatus(ConnectionStatus.error);
  }

  /// 处理断开连接
  void _onDisconnected() {
    print('WebSocket connection closed');
    
    if (_currentStatus != ConnectionStatus.disconnected) {
      _updateStatus(ConnectionStatus.disconnected);
      _scheduleReconnect();
    }
  }

  /// 启动心跳
  void _startHeartbeat() {
    _heartbeatTimer?.cancel();
    _heartbeatTimer = Timer.periodic(
      Duration(seconds: heartbeatInterval),
      (_) => _sendHeartbeat(),
    );
  }

  /// 停止心跳
  void _stopHeartbeat() {
    _heartbeatTimer?.cancel();
    _heartbeatTimer = null;
  }

  /// 发送心跳
  void _sendHeartbeat() async {
    if (!isConnected) return;

    try {
      await send(jsonEncode({'type': 'ping', 'timestamp': DateTime.now().millisecondsSinceEpoch}));
    } catch (e) {
      print('Heartbeat failed: $e');
      _scheduleReconnect();
    }
  }

  /// 安排重连
  void _scheduleReconnect() {
    if (_reconnectAttempts >= maxReconnectAttempts) {
      print('Max reconnect attempts reached');
      return;
    }

    _updateStatus(ConnectionStatus.reconnecting);

    final delay = reconnectDelays[min(_reconnectAttempts, reconnectDelays.length - 1)];
    print('Reconnecting in $delay seconds... (attempt ${_reconnectAttempts + 1}/$maxReconnectAttempts)');

    _reconnectTimer?.cancel();
    _reconnectTimer = Timer(Duration(seconds: delay), () async {
      _reconnectAttempts++;
      await connect();
    });
  }

  /// 停止重连
  void _stopReconnect() {
    _reconnectTimer?.cancel();
    _reconnectTimer = null;
    _reconnectAttempts = 0;
  }

  /// 更新状态
  void _updateStatus(ConnectionStatus status) {
    _currentStatus = status;
    _statusController.add(status);
  }

  /// 释放资源
  void dispose() {
    disconnect();
    _statusController.close();
    _messageController.close();
  }
}

/// 连接状态枚举
enum ConnectionStatus {
  disconnected,
  connecting,
  connected,
  reconnecting,
  error,
}
