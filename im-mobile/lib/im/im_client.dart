import 'dart:async';
import 'dart:convert';
import 'package:web_socket_channel/web_socket_channel.dart';

/// IM客户端核心类
class IMClient {
  static final IMClient _instance = IMClient._internal();
  factory IMClient() => _instance;
  IMClient._internal();

  WebSocketChannel? _channel;
  final _messageController = StreamController<IMMessage>.broadcast();
  final _statusController = StreamController<ConnectionStatus>.broadcast();
  
  String? _token;
  String? _userId;
  ConnectionStatus _status = ConnectionStatus.disconnected;
  Timer? _heartbeatTimer;
  Timer? _reconnectTimer;
  
  static const String _baseUrl = 'wss://api.im.example.com/ws/v1';
  static const int _heartbeatInterval = 30;
  static const int _reconnectInterval = 5;

  /// 消息流
  Stream<IMMessage> get messageStream => _messageController.stream;
  
  /// 连接状态流
  Stream<ConnectionStatus> get statusStream => _statusController.stream;
  
  /// 当前连接状态
  ConnectionStatus get status => _status;
  
  /// 是否已连接
  bool get isConnected => _status == ConnectionStatus.connected;

  /// 初始化并连接
  Future<void> connect(String token, String userId) async {
    _token = token;
    _userId = userId;
    await _connect();
  }

  /// 建立WebSocket连接
  Future<void> _connect() async {
    if (_token == null || _userId == null) {
      throw IMException('Token or userId not set');
    }

    try {
      _updateStatus(ConnectionStatus.connecting);
      
      final wsUrl = '$_baseUrl?token=$_token&userId=$_userId';
      _channel = WebSocketChannel.connect(Uri.parse(wsUrl));
      
      _channel!.stream.listen(
        _onMessage,
        onError: _onError,
        onDone: _onDisconnected,
      );

      _updateStatus(ConnectionStatus.connected);
      _startHeartbeat();
      
    } catch (e) {
      _updateStatus(ConnectionStatus.error);
      _scheduleReconnect();
    }
  }

  /// 接收消息处理
  void _onMessage(dynamic data) {
    try {
      final json = jsonDecode(data);
      final message = IMMessage.fromJson(json);
      _messageController.add(message);
    } catch (e) {
      print('Failed to parse message: $e');
    }
  }

  /// 发送消息
  Future<void> sendMessage(IMMessage message) async {
    if (!isConnected) {
      throw IMException('Not connected');
    }
    
    final data = jsonEncode(message.toJson());
    _channel?.sink.add(data);
  }

  /// 发送文本消息
  Future<void> sendTextMessage(String to, String content, {MessageType type = MessageType.single}) async {
    final message = IMMessage(
      id: _generateMessageId(),
      from: _userId!,
      to: to,
      type: type,
      contentType: ContentType.text,
      content: content,
      timestamp: DateTime.now().millisecondsSinceEpoch,
    );
    await sendMessage(message);
  }

  /// 发送图片消息
  Future<void> sendImageMessage(String to, String imageUrl, {MessageType type = MessageType.single}) async {
    final message = IMMessage(
      id: _generateMessageId(),
      from: _userId!,
      to: to,
      type: type,
      contentType: ContentType.image,
      content: imageUrl,
      timestamp: DateTime.now().millisecondsSinceEpoch,
    );
    await sendMessage(message);
  }

  /// 错误处理
  void _onError(error) {
    _updateStatus(ConnectionStatus.error);
    _scheduleReconnect();
  }

  /// 断开连接处理
  void _onDisconnected() {
    _updateStatus(ConnectionStatus.disconnected);
    _scheduleReconnect();
  }

  /// 启动心跳
  void _startHeartbeat() {
    _heartbeatTimer?.cancel();
    _heartbeatTimer = Timer.periodic(
      Duration(seconds: _heartbeatInterval),
      (_) => _sendHeartbeat(),
    );
  }

  /// 发送心跳
  void _sendHeartbeat() {
    if (isConnected) {
      _channel?.sink.add(jsonEncode({'type': 'heartbeat'}));
    }
  }

  /// 计划重连
  void _scheduleReconnect() {
    _reconnectTimer?.cancel();
    _reconnectTimer = Timer(
      Duration(seconds: _reconnectInterval),
      () => _connect(),
    );
  }

  /// 更新状态
  void _updateStatus(ConnectionStatus status) {
    _status = status;
    _statusController.add(status);
  }

  /// 断开连接
  Future<void> disconnect() async {
    _heartbeatTimer?.cancel();
    _reconnectTimer?.cancel();
    await _channel?.sink.close();
    _updateStatus(ConnectionStatus.disconnected);
  }

  /// 生成消息ID
  String _generateMessageId() {
    return '${_userId}_${DateTime.now().millisecondsSinceEpoch}_${_randomString(6)}';
  }

  String _randomString(int length) {
    const chars = 'abcdefghijklmnopqrstuvwxyz0123456789';
    return List.generate(length, (_) => chars[DateTime.now().microsecond % chars.length]).join();
  }

  /// 释放资源
  void dispose() {
    disconnect();
    _messageController.close();
    _statusController.close();
  }
}

/// 连接状态
enum ConnectionStatus {
  disconnected,
  connecting,
  connected,
  error,
}
