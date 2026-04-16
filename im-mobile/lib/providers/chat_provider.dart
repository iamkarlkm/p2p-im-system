import 'dart:async';
import 'package:flutter/foundation.dart';
import '../models/message_model.dart';
import '../models/user_model.dart';
import '../services/im_service.dart';
import '../services/websocket_service.dart';

/// 聊天状态管理Provider - 功能#9 基础IM客户端SDK
/// 时间: 2026-04-01 09:26
class ChatProvider extends ChangeNotifier {
  final IMService _imService = IMService();
  final WebSocketService _wsService = WebSocketService();

  // 会话列表
  List<ConversationModel> _conversations = [];
  
  // 当前会话消息
  Map<String, List<MessageModel>> _messages = {};
  
  // 当前用户信息
  UserModel? _currentUser;
  
  // 连接状态
  bool _isConnected = false;
  String _connectionStatus = 'DISCONNECTED';

  // Getters
  List<ConversationModel> get conversations => _conversations;
  UserModel? get currentUser => _currentUser;
  bool get isConnected => _isConnected;
  String get connectionStatus => _connectionStatus;

  /// 获取会话消息列表
  List<MessageModel> getMessages(String conversationId) {
    return _messages[conversationId] ?? [];
  }

  /// 初始化并连接
  Future<void> initialize(String userId, String token) async {
    try {
      await _wsService.connect(userId, token);
      
      // 监听连接状态
      _wsService.connectionStream.listen((status) {
        _connectionStatus = status;
        _isConnected = status == 'CONNECTED';
        notifyListeners();
      });

      // 监听消息
      _wsService.messageStream.listen((data) {
        _handleIncomingMessage(data);
      });

      // 加载会话列表
      await _loadConversations();
      
    } catch (e) {
      _connectionStatus = 'ERROR';
      notifyListeners();
      throw Exception('初始化失败: $e');
    }
  }

  /// 断开连接
  Future<void> disconnect() async {
    await _wsService.disconnect();
    _isConnected = false;
    _connectionStatus = 'DISCONNECTED';
    notifyListeners();
  }

  /// 发送文本消息
  Future<void> sendTextMessage(String conversationId, String content) async {
    if (content.trim().isEmpty) return;

    // 创建本地消息
    final message = MessageModel(
      id: 'local_${DateTime.now().millisecondsSinceEpoch}',
      conversationId: conversationId,
      senderId: _currentUser?.id ?? '',
      senderName: _currentUser?.displayName ?? '',
      senderAvatar: _currentUser?.avatar ?? '',
      content: content,
      messageType: 'TEXT',
      timestamp: DateTime.now().millisecondsSinceEpoch,
      status: MessageStatus.sending,
    );

    // 添加到本地列表
    _addMessage(conversationId, message);

    try {
      // 发送消息
      await _wsService.sendMessage(conversationId, content, 'TEXT');
      
      // 更新消息状态
      _updateMessageStatus(conversationId, message.id, MessageStatus.sent);
      
      // 更新会话最后消息
      _updateConversationLastMessage(conversationId, content, 'TEXT');
      
    } catch (e) {
      _updateMessageStatus(conversationId, message.id, MessageStatus.failed);
      throw Exception('发送失败: $e');
    }
  }

  /// 发送图片消息
  Future<void> sendImageMessage(String conversationId, String imageUrl) async {
    final message = MessageModel(
      id: 'local_${DateTime.now().millisecondsSinceEpoch}',
      conversationId: conversationId,
      senderId: _currentUser?.id ?? '',
      senderName: _currentUser?.displayName ?? '',
      senderAvatar: _currentUser?.avatar ?? '',
      content: imageUrl,
      messageType: 'IMAGE',
      timestamp: DateTime.now().millisecondsSinceEpoch,
      status: MessageStatus.sending,
    );

    _addMessage(conversationId, message);

    try {
      await _wsService.sendMessage(conversationId, imageUrl, 'IMAGE');
      _updateMessageStatus(conversationId, message.id, MessageStatus.sent);
      _updateConversationLastMessage(conversationId, '[图片]', 'IMAGE');
    } catch (e) {
      _updateMessageStatus(conversationId, message.id, MessageStatus.failed);
      throw Exception('发送失败: $e');
    }
  }

  /// 加载会话历史消息
  Future<void> loadHistoryMessages(String conversationId, {int limit = 20, int beforeTime = 0}) async {
    // 模拟加载历史消息，实际应该调用API
    await Future.delayed(Duration(milliseconds: 500));
    
    // 这里可以添加实际的API调用
    notifyListeners();
  }

  /// 标记消息已读
  Future<void> markConversationAsRead(String conversationId) async {
    final index = _conversations.indexWhere((c) => c.id == conversationId);
    if (index != -1 && _conversations[index].unreadCount > 0) {
      _conversations[index] = ConversationModel(
        id: _conversations[index].id,
        type: _conversations[index].type,
        name: _conversations[index].name,
        avatar: _conversations[index].avatar,
        lastMessage: _conversations[index].lastMessage,
        lastMessageType: _conversations[index].lastMessageType,
        lastMessageTime: _conversations[index].lastMessageTime,
        unreadCount: 0,
        memberIds: _conversations[index].memberIds,
      );
      notifyListeners();
    }
  }

  /// 设置当前用户
  void setCurrentUser(UserModel user) {
    _currentUser = user;
    notifyListeners();
  }

  /// 添加或更新会话
  void addOrUpdateConversation(ConversationModel conversation) {
    final index = _conversations.indexWhere((c) => c.id == conversation.id);
    if (index != -1) {
      _conversations[index] = conversation;
    } else {
      _conversations.insert(0, conversation);
    }
    notifyListeners();
  }

  /// 处理收到的消息
  void _handleIncomingMessage(Map<String, dynamic> data) {
    final type = data['type'];
    
    if (type == 'MESSAGE') {
      final message = MessageModel.fromJson(data);
      _addMessage(message.conversationId, message);
      
      // 更新会话未读数
      _incrementUnreadCount(message.conversationId);
      
      // 更新会话最后消息
      _updateConversationLastMessage(
        message.conversationId, 
        message.content, 
        message.messageType,
      );
    }
  }

  /// 添加消息到列表
  void _addMessage(String conversationId, MessageModel message) {
    if (!_messages.containsKey(conversationId)) {
      _messages[conversationId] = [];
    }
    
    // 检查是否已存在（避免重复）
    final exists = _messages[conversationId]!.any((m) => m.id == message.id);
    if (!exists) {
      _messages[conversationId]!.add(message);
      _messages[conversationId]!.sort((a, b) => a.timestamp.compareTo(b.timestamp));
      notifyListeners();
    }
  }

  /// 更新消息状态
  void _updateMessageStatus(String conversationId, String messageId, MessageStatus status) {
    if (_messages.containsKey(conversationId)) {
      final index = _messages[conversationId]!.indexWhere((m) => m.id == messageId);
      if (index != -1) {
        _messages[conversationId]![index] = _messages[conversationId]![index].copyWith(status: status);
        notifyListeners();
      }
    }
  }

  /// 更新会话最后消息
  void _updateConversationLastMessage(String conversationId, String content, String type) {
    final index = _conversations.indexWhere((c) => c.id == conversationId);
    if (index != -1) {
      _conversations[index] = ConversationModel(
        id: _conversations[index].id,
        type: _conversations[index].type,
        name: _conversations[index].name,
        avatar: _conversations[index].avatar,
        lastMessage: content,
        lastMessageType: type,
        lastMessageTime: DateTime.now().millisecondsSinceEpoch,
        unreadCount: _conversations[index].unreadCount,
        memberIds: _conversations[index].memberIds,
      );
      
      // 移动到顶部
      final conversation = _conversations.removeAt(index);
      _conversations.insert(0, conversation);
      
      notifyListeners();
    }
  }

  /// 增加未读数
  void _incrementUnreadCount(String conversationId) {
    final index = _conversations.indexWhere((c) => c.id == conversationId);
    if (index != -1) {
      _conversations[index] = ConversationModel(
        id: _conversations[index].id,
        type: _conversations[index].type,
        name: _conversations[index].name,
        avatar: _conversations[index].avatar,
        lastMessage: _conversations[index].lastMessage,
        lastMessageType: _conversations[index].lastMessageType,
        lastMessageTime: _conversations[index].lastMessageTime,
        unreadCount: _conversations[index].unreadCount + 1,
        memberIds: _conversations[index].memberIds,
      );
      notifyListeners();
    }
  }

  /// 加载会话列表
  Future<void> _loadConversations() async {
    // 模拟加载，实际应该调用API
    await Future.delayed(Duration(milliseconds: 300));
    notifyListeners();
  }
}
