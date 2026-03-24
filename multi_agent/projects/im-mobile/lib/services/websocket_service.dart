import 'dart:async';
import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:web_socket_channel/web_socket_channel.dart';
import '../models/message.dart';

class WebSocketService extends ChangeNotifier {
  WebSocketChannel? _channel;
  final _messagesController = StreamController<List<Message>>.broadcast();
  
  List<Message> _messages = [];
  bool _isConnected = false;
  String? _token;

  Stream<List<Message>> get messagesStream => _messagesController.stream;
  bool get isConnected => _isConnected;

  static const String _wsUrl = 'ws://10.0.2.2:9000/ws'; // Android模拟器访问主机

  void connect(String token) {
    _token = token;
    _connect();
  }

  void _connect() {
    if (_token == null) return;

    try {
      _channel = WebSocketChannel.connect(Uri.parse(_wsUrl));
      
      _channel!.stream.listen(
        (data) {
          _handleMessage(data);
        },
        onDone: () {
          _isConnected = false;
          notifyListeners();
          // 重新连接
          Future.delayed(const Duration(seconds: 5), () {
            if (_token != null) {
              _connect();
            }
          });
        },
        onError: (error) {
          _isConnected = false;
          notifyListeners();
        },
      );

      // 发送认证消息
      final authMessage = {
        'type': 'auth',
        'content': _token,
      };
      _channel!.sink.add(jsonEncode(authMessage));

      _isConnected = true;
      notifyListeners();
    } catch (e) {
      _isConnected = false;
      notifyListeners();
    }
  }

  void _handleMessage(String data) {
    try {
      final message = jsonDecode(data);
      final type = message['type'];

      switch (type) {
        case 'auth_ack':
          // 认证成功
          break;
        case 'chat':
          final chatMessage = Message.fromJson(message);
          _messages.add(chatMessage);
          _messagesController.add(List.from(_messages));
          break;
        case 'presence':
          // 处理在线状态
          break;
        case 'ack':
          // 处理消息确认
          break;
      }
    } catch (e) {
      debugPrint('处理WebSocket消息错误: $e');
    }
  }

  void sendMessage(Message message) {
    if (_channel != null && _isConnected) {
      _channel!.sink.add(jsonEncode(message.toJson()));
      
      // 添加到本地消息列表
      _messages.add(message);
      _messagesController.add(List.from(_messages));
    }
  }

  void disconnect() {
    _channel?.sink.close();
    _channel = null;
    _isConnected = false;
    _token = null;
    notifyListeners();
  }

  @override
  void dispose() {
    disconnect();
    _messagesController.close();
    super.dispose();
  }
}
