import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../models/ai_message_model.dart';
import '../services/ai_assistant_service.dart';
import 'ai_assistant_screen.dart';

class AiChatHistoryScreen extends StatefulWidget {
  const AiChatHistoryScreen({super.key});

  @override
  State<AiChatHistoryScreen> createState() => _AiChatHistoryScreenState();
}

class _AiChatHistoryScreenState extends State<AiChatHistoryScreen> {
  final List<AiConversation> _conversations = [];
  bool _isLoading = true;

  @override
  void initState() {
    super.initState();
    _loadConversations();
  }

  Future<void> _loadConversations() async {
    await Future.delayed(const Duration(milliseconds: 500));
    
    setState(() {
      _conversations.addAll([
        AiConversation(
          id: 'conv_1',
          title: '关于Flutter状态管理的讨论',
          createdAt: DateTime.now().subtract(const Duration(hours: 2)),
          updatedAt: DateTime.now().subtract(const Duration(hours: 2)),
          messageCount: 12,
          preview: 'Provider和GetX各有优缺点...',
        ),
        AiConversation(
          id: 'conv_2',
          title: 'Java Spring Boot问题',
          createdAt: DateTime.now().subtract(const Duration(days: 1)),
          updatedAt: DateTime.now().subtract(const Duration(days: 1)),
          messageCount: 8,
          preview: '关于事务注解的使用...',
        ),
        AiConversation(
          id: 'conv_3',
          title: '代码优化建议',
          createdAt: DateTime.now().subtract(const Duration(days: 2)),
          updatedAt: DateTime.now().subtract(const Duration(days: 2)),
          messageCount: 15,
          preview: '你可以考虑使用工厂模式...',
        ),
      ]);
      _isLoading = false;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('对话历史'),
        actions: [
          TextButton.icon(
            onPressed: _startNewChat,
            icon: const Icon(Icons.add),
            label: const Text('新建对话'),
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _conversations.isEmpty
              ? _buildEmptyState()
              : _buildConversationList(),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.chat_bubble_outline,
            size: 80,
            color: Colors.grey.shade300,
          ),
          const SizedBox(height: 16),
          Text(
            '暂无对话历史',
            style: TextStyle(
              fontSize: 16,
              color: Colors.grey.shade600,
            ),
          ),
          const SizedBox(height: 24),
          FilledButton.icon(
            onPressed: _startNewChat,
            icon: const Icon(Icons.add),
            label: const Text('开始新对话'),
          ),
        ],
      ),
    );
  }

  Widget _buildConversationList() {
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: _conversations.length,
      itemBuilder: (context, index) {
        final conversation = _conversations[index];
        return _buildConversationCard(conversation);
      },
    );
  }

  Widget _buildConversationCard(AiConversation conversation) {
    return Dismissible(
      key: Key(conversation.id),
      direction: DismissDirection.endToStart,
      background: Container(
        alignment: Alignment.centerRight,
        padding: const EdgeInsets.only(right: 20),
        decoration: BoxDecoration(
          color: Colors.red.shade400,
          borderRadius: BorderRadius.circular(12),
        ),
        child: const Icon(Icons.delete, color: Colors.white),
      ),
      onDismissed: (_) => _deleteConversation(conversation),
      child: Card(
        margin: const EdgeInsets.only(bottom: 12),
        elevation: 0,
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(12),
          side: BorderSide(color: Colors.grey.shade200),
        ),
        child: ListTile(
          contentPadding: const EdgeInsets.all(16),
          leading: Container(
            width: 48,
            height: 48,
            decoration: BoxDecoration(
              gradient: const LinearGradient(
                colors: [Colors.blue, Colors.purple],
              ),
              borderRadius: BorderRadius.circular(12),
            ),
            child: const Icon(Icons.smart_toy, color: Colors.white),
          ),
          title: Row(
            children: [
              Expanded(
                child: Text(
                  conversation.title,
                  style: const TextStyle(
                    fontWeight: FontWeight.w600,
                    fontSize: 15,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
              if (conversation.isPinned)
                Icon(Icons.push_pin, size: 16, color: Colors.blue.shade400),
            ],
          ),
          subtitle: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const SizedBox(height: 4),
              Text(
                conversation.preview ?? '',
                style: TextStyle(
                  color: Colors.grey.shade600,
                  fontSize: 13,
                ),
                maxLines: 1,
                overflow: TextOverflow.ellipsis,
              ),
              const SizedBox(height: 8),
              Row(
                children: [
                  Icon(Icons.chat_bubble_outline, size: 14, color: Colors.grey.shade400),
                  const SizedBox(width: 4),
                  Text(
                    '${conversation.messageCount}条消息',
                    style: TextStyle(
                      color: Colors.grey.shade500,
                      fontSize: 12,
                    ),
                  ),
                  const Spacer(),
                  Text(
                    conversation.formattedDate,
                    style: TextStyle(
                      color: Colors.grey.shade500,
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ],
          ),
          onTap: () => _openConversation(conversation),
        ),
      ),
    );
  }

  void _startNewChat() {
    Navigator.pushReplacement(
      context,
      MaterialPageRoute(builder: (_) => const AiAssistantScreen()),
    );
  }

  void _openConversation(AiConversation conversation) {
    Navigator.push(
      context,
      MaterialPageRoute(builder: (_) => const AiAssistantScreen()),
    );
  }

  void _deleteConversation(AiConversation conversation) {
    setState(() {
      _conversations.removeWhere((c) => c.id == conversation.id);
    });
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: const Text('对话已删除'),
        action: SnackBarAction(
          label: '撤销',
          onPressed: () {
            setState(() {
              _conversations.insert(0, conversation);
            });
          },
        ),
      ),
    );
  }
}
