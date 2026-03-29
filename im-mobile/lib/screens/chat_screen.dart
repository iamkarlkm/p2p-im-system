import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/websocket_service.dart';
import '../models/message.dart';
import '../models/user.dart';

class ChatScreen extends StatefulWidget {
  const ChatScreen({super.key});

  @override
  State<ChatScreen> createState() => _ChatScreenState();
}

class _ChatScreenState extends State<ChatScreen> {
  final _messageController = TextEditingController();
  User? _selectedUser;
  final List<User> _friends = [
    User(id: 'user_1', nickname: '张三'),
    User(id: 'user_2', nickname: '李四'),
    User(id: 'user_3', nickname: '王五'),
  ];

  @override
  void dispose() {
    _messageController.dispose();
    super.dispose();
  }

  void _sendMessage() {
    if (_messageController.text.isEmpty || _selectedUser == null) return;

    final wsService = Provider.of<WebSocketService>(context, listen: false);
    final authService = Provider.of<AuthService>(context, listen: false);

    final message = Message(
      from: authService.currentUserId!,
      to: _selectedUser!.id,
      content: _messageController.text,
      timestamp: DateTime.now().millisecondsSinceEpoch,
      msgId: 'msg_${DateTime.now().millisecondsSinceEpoch}',
    );

    wsService.sendMessage(message);
    _messageController.clear();
  }

  @override
  Widget build(BuildContext context) {
    final authService = Provider.of<AuthService>(context);
    final wsService = Provider.of<WebSocketService>(context);

    return Scaffold(
      appBar: AppBar(
        title: Text('欢迎, ${authService.currentUser?.nickname ?? "用户"}'),
        actions: [
          IconButton(
            icon: const Icon(Icons.logout),
            onPressed: () {
              authService.logout();
              wsService.disconnect();
              Navigator.of(context).pop();
            },
          ),
        ],
      ),
      body: Row(
        children: [
          // 好友列表
          SizedBox(
            width: 100,
            child: ListView.builder(
              itemCount: _friends.length,
              itemBuilder: (context, index) {
                final friend = _friends[index];
                return ListTile(
                  title: Text(
                    friend.nickname,
                    style: const TextStyle(fontSize: 12),
                  ),
                  selected: _selectedUser?.id == friend.id,
                  onTap: () {
                    setState(() {
                      _selectedUser = friend;
                    });
                  },
                );
              },
            ),
          ),
          const VerticalDivider(width: 1),
          // 聊天区域
          Expanded(
            child: Column(
              children: [
                if (_selectedUser != null) ...[
                  Container(
                    padding: const EdgeInsets.all(8),
                    color: Colors.grey[100],
                    child: Row(
                      children: [
                        const Icon(Icons.person),
                        const SizedBox(width: 8),
                        Text(_selectedUser!.nickname),
                      ],
                    ),
                  ),
                  Expanded(
                    child: StreamBuilder<List<Message>>(
                      stream: wsService.messagesStream,
                      builder: (context, snapshot) {
                        final messages = snapshot.data ?? [];
                        final chatMessages = messages
                            .where((m) =>
                                (m.from == _selectedUser!.id ||
                                    m.to == _selectedUser!.id))
                            .toList();

                        return ListView.builder(
                          itemCount: chatMessages.length,
                          itemBuilder: (context, index) {
                            final message = chatMessages[index];
                            final isSent = message.from == authService.currentUserId;

                            return Align(
                              alignment: isSent
                                  ? Alignment.centerRight
                                  : Alignment.centerLeft,
                              child: Container(
                                margin: const EdgeInsets.all(8),
                                padding: const EdgeInsets.all(12),
                                decoration: BoxDecoration(
                                  color: isSent
                                      ? Colors.blue
                                      : Colors.grey[200],
                                  borderRadius: BorderRadius.circular(8),
                                ),
                                child: Text(
                                  message.content,
                                  style: TextStyle(
                                    color: isSent ? Colors.white : Colors.black,
                                  ),
                                ),
                              ),
                            );
                          },
                        );
                      },
                    ),
                  ),
                  Padding(
                    padding: const EdgeInsets.all(8),
                    child: Row(
                      children: [
                        Expanded(
                          child: TextField(
                            controller: _messageController,
                            decoration: InputDecoration(
                              hintText: '输入消息...',
                              border: OutlineInputBorder(
                                borderRadius: BorderRadius.circular(24),
                              ),
                              contentPadding: const EdgeInsets.symmetric(
                                horizontal: 16,
                                vertical: 8,
                              ),
                            ),
                          ),
                        ),
                        IconButton(
                          icon: const Icon(Icons.send),
                          onPressed: _sendMessage,
                        ),
                      ],
                    ),
                  ),
                ] else
                  const Center(
                    child: Text('选择好友开始聊天'),
                  ),
              ],
            ),
          ),
        ],
      ),
    );
  }
}
