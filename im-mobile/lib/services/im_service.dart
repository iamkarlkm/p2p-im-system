import 'dart:async';
import 'dart:convert';
import 'package:web_socket_channel/web_socket_channel.dart';

/// IM服务类 - 功能#9 基础IM客户端SDK
class IMService {
  static final IMService _instance = IMService._internal();
  factory IMService() => _instance;
  IMService._internal();

  WebSocketChannel? _channel;
  final StreamController<Map<String, dynamic>> _messageController = StreamController.broadcast();
  final StreamController<String> _connectionController = StreamController.broadcast();
  
  String? _token;
  String? _userId;
  bool _isConnected = false;
  Timer? _heartbeatTimer;

  /// 消息流
  Stream<Map<String, dynamic>> get messageStream => _messageController.stream;
  
  /// 连接状态流
  Stream<String> get connectionStream => _connectionController.stream;
  
  /// 是否已连接
  bool get isConnected => _isConnected;

  /// 初始化连接
  Future<void> connect(String url, String token, String userId) async {
    _token = token;
    _userId = userId;
    
    try {
      _channel = WebSocketChannel.connect(Uri.parse(url));
      
      _channel!.stream.listen(
        (message) => _handleMessage(message),
        onError: (error) => _handleError(error),
        onDone: () => _handleDisconnect(),
      );
      
      // 发送认证消息
      _sendAuthMessage();
      
      // 启动心跳
      _startHeartbeat();
      
      _isConnected = true;
      _connectionController.add('CONNECTED');
    } catch (e) {
      _connectionController.add('ERROR');
      throw Exception('连接失败: $e');
    }
  }

  /// 断开连接
  Future<void> disconnect() async {
    _heartbeatTimer?.cancel();
    await _channel?.sink.close();
    _isConnected = false;
    _connectionController.add('DISCONNECTED');
  }

  /// 发送消息
  Future<void> sendMessage(String conversationId, String content, String messageType) async {
    if (!_isConnected) throw Exception('未连接');
    
    final message = {
      'type': 'MESSAGE',
      'conversationId': conversationId,
      'content': content,
      'messageType': messageType,
      'timestamp': DateTime.now().millisecondsSinceEpoch,
    };
    
    _channel?.sink.add(jsonEncode(message));
  }

  /// 发送文本消息
  Future<void> sendText(String conversationId, String text) async {
    await sendMessage(conversationId, text, 'TEXT');
  }

  /// 发送图片消息
  Future<void> sendImage(String conversationId, String imageUrl) async {
    await sendMessage(conversationId, imageUrl, 'IMAGE');
  }

  /// 处理收到的消息
  void _handleMessage(String message) {
    try {
      final data = jsonDecode(message);
      _messageController.add(data);
    } catch (e) {
      print('解析消息失败: $e');
    }
  }

  /// 处理错误
  void _handleError(dynamic error) {
    print('WebSocket错误: $error');
    _connectionController.add('ERROR');
  }

  /// 处理断开连接
  void _handleDisconnect() {
    _isConnected = false;
    _connectionController.add('DISCONNECTED');
  }

  /// 发送认证消息
  void _sendAuthMessage() {
    final authMessage = {
      'type': 'AUTH',
      'token': _token,
      'userId': _userId,
    };
    _channel?.sink.add(jsonEncode(authMessage));
  }

  /// 启动心跳
  void _startHeartbeat() {
    _heartbeatTimer = Timer.periodic(Duration(seconds: 30), (timer) {
      if (_isConnected) {
        _channel?.sink.add(jsonEncode({'type': 'HEARTBEAT'}));
      }
    });
  }
}