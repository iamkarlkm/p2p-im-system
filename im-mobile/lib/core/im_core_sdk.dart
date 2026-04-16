import 'dart:async';
import 'dart:convert';
import 'package:web_socket_channel/web_socket_channel.dart';

/// IM核心SDK - 功能#9 基础IM客户端SDK (Dart)
class IMCoreSDK {
  static final IMCoreSDK _instance = IMCoreSDK._internal();
  factory IMCoreSDK() => _instance;
  IMCoreSDK._internal();

  WebSocketChannel? _channel;
  final _messageController = StreamController<IMMessage>.broadcast();
  final _statusController = StreamController<ConnectionStatus>.broadcast();
  
  String? _token;
  String? _userId;
  bool _isConnected = false;
  
  /// 消息流
  Stream<IMMessage> get messageStream => _messageController.stream;
  
  /// 连接状态流
  Stream<ConnectionStatus> get statusStream => _statusController.stream;
  
  /// 是否已连接
  bool get isConnected => _isConnected;
  
  /// 当前用户ID
  String? get currentUserId => _userId;

  /// 初始化SDK
  Future<void> init({required String serverUrl}) async {
    print('IM SDK initializing...');
    // 初始化数据库、缓存等
  }

  /// 连接服务器
  Future<bool> connect({required String token, required String userId}) async {
    try {
      _token = token;
      _userId = userId;
      
      _channel = WebSocketChannel.connect(
        Uri.parse('wss://im.example.com/ws?token=$token'),
      );
      
      _channel!.stream.listen(
        (data) => _handleMessage(data),
        onError: (error) => _handleError(error),
        onDone: () => _handleDisconnect(),
      );
      
      _isConnected = true;
      _statusController.add(ConnectionStatus.connected);
      
      print('IM SDK connected: userId=$userId');
      return true;
    } catch (e) {
      print('IM SDK connect error: $e');
      _statusController.add(ConnectionStatus.error);
      return false;
    }
  }

  /// 断开连接
  Future<void> disconnect() async {
    _isConnected = false;
    await _channel?.sink.close();
    _statusController.add(ConnectionStatus.disconnected);
    print('IM SDK disconnected');
  }

  /// 发送消息
  Future<bool> sendMessage(IMMessage message) async {
    if (!_isConnected) {
      print('IM SDK not connected');
      return false;
    }
    
    try {
      final data = jsonEncode(message.toJson());
      _channel?.sink.add(data);
      print('Message sent: ${message.messageId}');
      return true;
    } catch (e) {
      print('Send message error: $e');
      return false;
    }
  }

  /// 处理收到的消息
  void _handleMessage(dynamic data) {
    try {
      final json = jsonDecode(data);
      final message = IMMessage.fromJson(json);
      _messageController.add(message);
      print('Message received: ${message.messageId}');
    } catch (e) {
      print('Handle message error: $e');
    }
  }

  void _handleError(dynamic error) {
    print('WebSocket error: $error');
    _statusController.add(ConnectionStatus.error);
  }

  void _handleDisconnect() {
    _isConnected = false;
    _statusController.add(ConnectionStatus.disconnected);
  }

  /// 销毁SDK
  void dispose() {
    _messageController.close();
    _statusController.close();
    _channel?.sink.close();
  }
}

/// 连接状态
enum ConnectionStatus {
  connected,
  disconnected,
  connecting,
  error,
}

/// IM消息
class IMMessage {
  final String messageId;
  final String senderId;
  final String? receiverId;
  final String? groupId;
  final String conversationId;
  final String messageType;
  final String content;
  final int timestamp;

  IMMessage({
    required this.messageId,
    required this.senderId,
    this.receiverId,
    this.groupId,
    required this.conversationId,
    required this.messageType,
    required this.content,
    required this.timestamp,
  });

  Map<String, dynamic> toJson() => {
    'messageId': messageId,
    'senderId': senderId,
    'receiverId': receiverId,
    'groupId': groupId,
    'conversationId': conversationId,
    'messageType': messageType,
    'content': content,
    'timestamp': timestamp,
  };

  factory IMMessage.fromJson(Map<String, dynamic> json) => IMMessage(
    messageId: json['messageId'],
    senderId: json['senderId'],
    receiverId: json['receiverId'],
    groupId: json['groupId'],
    conversationId: json['conversationId'],
    messageType: json['messageType'],
    content: json['content'],
    timestamp: json['timestamp'],
  );
}
