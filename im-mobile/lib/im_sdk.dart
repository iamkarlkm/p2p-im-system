library im_sdk;

import 'dart:async';

/// IM SDK主类
class IMSDK {
  static final IMSDK _instance = IMSDK._internal();
  factory IMSDK() => _instance;
  IMSDK._internal();
  
  final StreamController<String> _messageController = StreamController<String>.broadcast();
  Stream<String> get messageStream => _messageController.stream;
  
  void connect(String userId) {
    print('IM SDK connected: $userId');
  }
  
  void disconnect() {
    print('IM SDK disconnected');
  }
  
  void sendMessage(String toUser, String content) {
    _messageController.add('Message to $toUser: $content');
  }
}
