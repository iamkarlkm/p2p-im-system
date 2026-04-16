import 'package:flutter/material.dart';
import '../services/api_service.dart';
import '../services/websocket_client.dart';
import 'message_bubble.dart';

/// 聊天界面组件
class ChatScreen extends StatefulWidget {
  final String conversationId;
  final String? conversationName;
  final String? avatar;

  const ChatScreen({
    Key? key,
    required this.conversationId,
    this.conversationName,
    this.avatar,
  }) : super(key: key);

  @override
  State<ChatScreen> createState() => _ChatScreenState();
}

class _ChatScreenState extends State<ChatScreen> {
  final ApiService _apiService = ApiService();
  final WebSocketClient _webSocketClient = WebSocketClient();
  final TextEditingController _messageController = TextEditingController();
  final ScrollController _scrollController = ScrollController();

  List<Map<String, dynamic>> _messages = [];
  bool _isLoading = true;
  bool _isSending = false;
  bool _isTyping = false;
  String? _currentUserId;

  @override
  void initState() {
    super.initState();
    _currentUserId = _apiService.userId;
    _initializeWebSocket();
    _loadMessages();
  }

  @override
  void dispose() {
    _messageController.dispose();
    _scrollController.dispose();
    _webSocketClient.leaveConversation(widget.conversationId);
    super.dispose();
  }

  /// 初始化WebSocket
  void _initializeWebSocket() {
    _webSocketClient.onMessage = _handleWebSocketMessage;
    _webSocketClient.onConnect = () {
      // 加入会话
      _webSocketClient.joinConversation(widget.conversationId);
    };

    if (_webSocketClient.isConnected) {
      _webSocketClient.joinConversation(widget.conversationId);
    }
  }

  /// 处理WebSocket消息
  void _handleWebSocketMessage(Map<String, dynamic> message) {
    switch (message['type']) {
      case 'CHAT_MESSAGE':
        if (message['conversationId'] == widget.conversationId) {
          setState(() {
            _messages.insert(0, message);
          });
          _scrollToBottom();
        }
        break;
      case 'TYPING_STATUS':
        if (message['conversationId'] == widget.conversationId) {
          setState(() {
            _isTyping = message['isTyping'] ?? false;
          });
        }
        break;
      case 'READ_RECEIPT':
        _updateMessageReadStatus(message['messageId']);
        break;
    }
  }

  /// 更新消息已读状态
  void _updateMessageReadStatus(String messageId) {
    setState(() {
      for (var msg in _messages) {
        if (msg['id'] == messageId) {
          msg['isRead'] = true;
        }
      }
    });
  }

  /// 加载消息历史
  Future<void> _loadMessages() async {
    try {
      setState(() => _isLoading = true);
      final messages = await _apiService.getMessages(
        conversationId: widget.conversationId,
        page: 0,
        size: 50,
      );
      setState(() {
        _messages = List<Map<String, dynamic>>.from(messages.reversed);
        _isLoading = false;
      });
      _scrollToBottom();
    } catch (e) {
      setState(() => _isLoading = false);
      _showError('Failed to load messages');
    }
  }

  /// 发送消息
  Future<void> _sendMessage() async {
    final content = _messageController.text.trim();
    if (content.isEmpty) return;

    _messageController.clear();

    // 发送正在输入状态
    _webSocketClient.sendTypingStatus(widget.conversationId, false);

    // 创建临时消息
    final tempMessage = {
      'id': 'temp_${DateTime.now().millisecondsSinceEpoch}',
      'content': content,
      'senderId': _currentUserId,
      'timestamp': DateTime.now().millisecondsSinceEpoch,
      'status': 'sending',
      'isMe': true,
    };

    setState(() {
      _messages.insert(0, tempMessage);
      _isSending = true;
    });

    _scrollToBottom();

    try {
      // 通过API发送
      final response = await _apiService.sendMessage(
        conversationId: widget.conversationId,
        content: content,
      );

      // 更新消息状态
      setState(() {
        final index = _messages.indexWhere((m) => m['id'] == tempMessage['id']);
        if (index != -1) {
          _messages[index] = {
            ..._messages[index],
            'id': response['id'],
            'status': 'sent',
          };
        }
        _isSending = false;
      });

      // 通过WebSocket广播
      _webSocketClient.sendChatMessage(
        conversationId: widget.conversationId,
        content: content,
      );
    } catch (e) {
      // 更新消息状态为失败
      setState(() {
        final index = _messages.indexWhere((m) => m['id'] == tempMessage['id']);
        if (index != -1) {
          _messages[index]['status'] = 'failed';
        }
        _isSending = false;
      });
      _showError('Failed to send message');
    }
  }

  /// 滚动到底部
  void _scrollToBottom() {
    if (_scrollController.hasClients) {
      Future.delayed(Duration(milliseconds: 100), () {
        _scrollController.animateTo(
          0,
          duration: Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      });
    }
  }

  /// 显示错误提示
  void _showError(String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message)),
    );
  }

  /// 处理输入变化
  void _onInputChanged(String text) {
    if (text.isNotEmpty) {
      _webSocketClient.sendTypingStatus(widget.conversationId, true);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Row(
          children: [
            if (widget.avatar != null)
              CircleAvatar(
                radius: 16,
                backgroundImage: NetworkImage(widget.avatar!),
              )
            else
              CircleAvatar(
                radius: 16,
                child: Text(
                  (widget.conversationName ?? 'U').substring(0, 1).toUpperCase(),
                ),
              ),
            SizedBox(width: 8),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    widget.conversationName ?? 'Chat',
                    style: TextStyle(fontSize: 16),
                  ),
                  if (_isTyping)
                    Text(
                      'typing...',
                      style: TextStyle(fontSize: 12, color: Colors.grey),
                    ),
                ],
              ),
            ),
          ],
        ),
        actions: [
          IconButton(
            icon: Icon(Icons.more_vert),
            onPressed: () {
              // 显示更多选项
            },
          ),
        ],
      ),
      body: Column(
        children: [
          // 消息列表
          Expanded(
            child: _isLoading
                ? Center(child: CircularProgressIndicator())
                : _messages.isEmpty
                    ? Center(child: Text('No messages yet'))
                    : ListView.builder(
                        controller: _scrollController,
                        reverse: true,
                        itemCount: _messages.length,
                        itemBuilder: (context, index) {
                          final message = _messages[index];
                          final isMe = message['senderId'] == _currentUserId ||
                                      message['isMe'] == true;
                          return MessageBubble(
                            message: message,
                            isMe: isMe,
                            onRetry: message['status'] == 'failed'
                                ? () => _retryMessage(message)
                                : null,
                          );
                        },
                      ),
          ),

          // 输入框
          Container(
            padding: EdgeInsets.all(8),
            decoration: BoxDecoration(
              color: Colors.white,
              boxShadow: [
                BoxShadow(
                  color: Colors.black12,
                  blurRadius: 4,
                  offset: Offset(0, -2),
                ),
              ],
            ),
            child: SafeArea(
              child: Row(
                children: [
                  IconButton(
                    icon: Icon(Icons.add_circle_outline),
                    onPressed: () {
                      // 显示附件选项
                    },
                  ),
                  Expanded(
                    child: TextField(
                      controller: _messageController,
                      onChanged: _onInputChanged,
                      decoration: InputDecoration(
                        hintText: 'Type a message...',
                        border: OutlineInputBorder(
                          borderRadius: BorderRadius.circular(24),
                          borderSide: BorderSide.none,
                        ),
                        filled: true,
                        fillColor: Colors.grey[200],
                        contentPadding: EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 12,
                        ),
                      ),
                      maxLines: null,
                      textInputAction: TextInputAction.send,
                      onSubmitted: (_) => _sendMessage(),
                    ),
                  ),
                  SizedBox(width: 8),
                  _isSending
                      ? SizedBox(
                          width: 24,
                          height: 24,
                          child: CircularProgressIndicator(strokeWidth: 2),
                        )
                      : IconButton(
                          icon: Icon(Icons.send, color: Theme.of(context).primaryColor),
                          onPressed: _sendMessage,
                        ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  /// 重试发送失败的消息
  void _retryMessage(Map<String, dynamic> message) {
    final content = message['content'];
    setState(() {
      _messages.removeWhere((m) => m['id'] == message['id']);
    });
    _messageController.text = content;
    _sendMessage();
  }
}
