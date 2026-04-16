library message_manager;

import 'dart:collection';

/// 消息管理器
class MessageManager {
  final Queue<String> _messages = Queue<String>();
  final int _maxSize = 100;
  
  void addMessage(String message) {
    if (_messages.length >= _maxSize) {
      _messages.removeFirst();
    }
    _messages.add(message);
  }
  
  List<String> getMessages() {
    return _messages.toList();
  }
  
  void clear() {
    _messages.clear();
  }
}
