library chat_client;

import 'dart:async';

/// 聊天客户端
class ChatClient {
  final String userId;
  final StreamController<String> _controller = StreamController<String>.broadcast();
  
  ChatClient(this.userId);
  
  Stream<String> get messageStream => _controller.stream;
  
  void sendPrivateMessage(String toUser, String content) {
    _controller.add('Private to $toUser: $content');
  }
  
  void sendGroupMessage(String groupId, String content) {
    _controller.add('Group $groupId: $content');
  }
}
