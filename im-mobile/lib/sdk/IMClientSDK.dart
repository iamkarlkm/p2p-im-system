import 'dart:async';
import 'dart:convert';
import 'dart:io';

import 'package:web_socket_channel/web_socket_channel.dart';
import 'package:web_socket_channel/io.dart';

import 'ConnectionManager.dart';
import 'MessageHandler.dart';
import 'MessageClient.dart';

/// 基础IM客户端SDK
/// 功能#9: 基础IM客户端SDK
class IMClientSDK {
  static final IMClientSDK _instance = IMClientSDK._internal();
  factory IMClientSDK() => _instance;
  IMClientSDK._internal();

  // 组件
  late final ConnectionManager _connectionManager;
  late final MessageHandler _messageHandler;
  late final MessageClient _messageClient;

  // 配置
  String _serverUrl = '';
  String _token = '';
  String _userId = '';
  bool _initialized = false;

  // 状态监听
  final StreamController<ConnectionStatus> _statusController = 
      StreamController<ConnectionStatus>.broadcast();
  Stream<ConnectionStatus> get statusStream => _statusController.stream;

  /// 初始化SDK
  Future<void> initialize({
    required String serverUrl,
    required String token,
    required String userId,
  }) async {
    if (_initialized) {
      throw Exception('SDK already initialized');
    }

    _serverUrl = serverUrl;
    _token = token;
    _userId = userId;

    // 初始化组件
    _connectionManager = ConnectionManager(
      serverUrl: serverUrl,
      token: token,
    );
    
    _messageHandler = MessageHandler();
    _messageClient = MessageClient(
      connectionManager: _connectionManager,
      messageHandler: _messageHandler,
    );

    // 监听连接状态
    _connectionManager.statusStream.listen((status) {
      _statusController.add(status);
    });

    _initialized = true;
    print('IMClientSDK initialized for user: $userId');
  }

  /// 连接服务器
  Future<void> connect() async {
    _ensureInitialized();
    await _connectionManager.connect();
  }

  /// 断开连接
  Future<void> disconnect() async {
    _ensureInitialized();
    await _connectionManager.disconnect();
  }

  /// 发送消息
  Future<void> sendMessage({
    required String conversationId,
    required String content,
    String type = 'text',
  }) async {
    _ensureInitialized();
    await _messageClient.sendMessage(
      conversationId: conversationId,
      content: content,
      type: type,
    );
  }

  /// 发送私聊消息
  Future<void> sendPrivateMessage({
    required String toUserId,
    required String content,
    String type = 'text',
  }) async {
    _ensureInitialized();
    await _messageClient.sendPrivateMessage(
      fromUserId: _userId,
      toUserId: toUserId,
      content: content,
      type: type,
    );
  }

  /// 发送群聊消息
  Future<void> sendGroupMessage({
    required String groupId,
    required String content,
    String type = 'text',
  }) async {
    _ensureInitialized();
    await _messageClient.sendGroupMessage(
      fromUserId: _userId,
      groupId: groupId,
      content: content,
      type: type,
    );
  }

  /// 获取消息流
  Stream<ChatMessage> get messageStream => _messageHandler.messageStream;

  /// 获取历史消息
  Future<List<ChatMessage>> getHistoryMessages({
    required String conversationId,
    int limit = 20,
    String? beforeMessageId,
  }) async {
    _ensureInitialized();
    return await _messageClient.getHistoryMessages(
      conversationId: conversationId,
      limit: limit,
      beforeMessageId: beforeMessageId,
    );
  }

  /// 标记消息已读
  Future<void> markAsRead({
    required String conversationId,
    required String messageId,
  }) async {
    _ensureInitialized();
    await _messageClient.markAsRead(
      conversationId: conversationId,
      messageId: messageId,
    );
  }

  /// 获取会话列表
  Future<List<Conversation>> getConversations() async {
    _ensureInitialized();
    return await _messageClient.getConversations(userId: _userId);
  }

  /// 创建群组
  Future<String> createGroup({
    required String name,
    List<String> memberIds = const [],
  }) async {
    _ensureInitialized();
    return await _messageClient.createGroup(
      creatorId: _userId,
      name: name,
      memberIds: memberIds,
    );
  }

  /// 加入群组
  Future<void> joinGroup(String groupId) async {
    _ensureInitialized();
    await _messageClient.joinGroup(
      userId: _userId,
      groupId: groupId,
    );
  }

  /// 离开群组
  Future<void> leaveGroup(String groupId) async {
    _ensureInitialized();
    await _messageClient.leaveGroup(
      userId: _userId,
      groupId: groupId,
    );
  }

  /// 获取连接状态
  ConnectionStatus get connectionStatus => _connectionManager.currentStatus;

  /// 是否已连接
  bool get isConnected => _connectionManager.isConnected;

  /// 释放资源
  Future<void> dispose() async {
    await disconnect();
    await _statusController.close();
    _initialized = false;
  }

  void _ensureInitialized() {
    if (!_initialized) {
      throw Exception('SDK not initialized. Call initialize() first.');
    }
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

/// 消息模型
class ChatMessage {
  final String id;
  final String conversationId;
  final String senderId;
  final String content;
  final String type;
  final int timestamp;
  final bool isRead;

  ChatMessage({
    required this.id,
    required this.conversationId,
    required this.senderId,
    required this.content,
    required this.type,
    required this.timestamp,
    this.isRead = false,
  });

  factory ChatMessage.fromJson(Map<String, dynamic> json) {
    return ChatMessage(
      id: json['id'],
      conversationId: json['conversationId'],
      senderId: json['senderId'],
      content: json['content'],
      type: json['type'],
      timestamp: json['timestamp'],
      isRead: json['isRead'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'conversationId': conversationId,
      'senderId': senderId,
      'content': content,
      'type': type,
      'timestamp': timestamp,
      'isRead': isRead,
    };
  }
}

/// 会话模型
class Conversation {
  final String id;
  final String type; // 'private' or 'group'
  final String name;
  final String? avatar;
  final String lastMessage;
  final int lastMessageTime;
  final int unreadCount;

  Conversation({
    required this.id,
    required this.type,
    required this.name,
    this.avatar,
    required this.lastMessage,
    required this.lastMessageTime,
    this.unreadCount = 0,
  });

  factory Conversation.fromJson(Map<String, dynamic> json) {
    return Conversation(
      id: json['id'],
      type: json['type'],
      name: json['name'],
      avatar: json['avatar'],
      lastMessage: json['lastMessage'],
      lastMessageTime: json['lastMessageTime'],
      unreadCount: json['unreadCount'] ?? 0,
    );
  }
}
