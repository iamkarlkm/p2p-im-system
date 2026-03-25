import 'package:flutter/material.dart';
import '../models/bot.dart';
import '../services/bot_service.dart';

class BotCardWidget extends StatelessWidget {
  final Bot bot;
  final VoidCallback? onTap;
  final VoidCallback? onEdit;
  final VoidCallback? onDelete;
  final Function(bool)? onToggle;

  const BotCardWidget({
    Key? key,
    required this.bot,
    this.onTap,
    this.onEdit,
    this.onDelete,
    this.onToggle,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  _buildAvatar(),
                  const SizedBox(width: 16),
                  Expanded(
                    child: Column(
                      crossAxisAlignment: CrossAxisAlignment.start,
                      children: [
                        Text(
                          bot.name,
                          style: const TextStyle(
                            fontSize: 18,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                        const SizedBox(height: 4),
                        Text(
                          bot.description,
                          style: TextStyle(
                            fontSize: 14,
                            color: Colors.grey[600],
                          ),
                          maxLines: 2,
                          overflow: TextOverflow.ellipsis,
                        ),
                      ],
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              Row(
                children: [
                  _buildBotTypeChip(),
                  const SizedBox(width: 8),
                  _buildStatusChip(),
                  const Spacer(),
                  IconButton(
                    icon: const Icon(Icons.edit),
                    onPressed: onEdit,
                    tooltip: 'Edit',
                  ),
                  IconButton(
                    icon: const Icon(Icons.delete),
                    onPressed: onDelete,
                    tooltip: 'Delete',
                  ),
                ],
              ),
              if (bot.botType == 'AI' && bot.aiProvider != null) ...[
                const SizedBox(height: 8),
                Row(
                  children: [
                    Chip(
                      label: Text(bot.aiProvider!),
                      backgroundColor: Colors.blue[100],
                    ),
                    if (bot.aiModel != null) ...[
                      const SizedBox(width: 8),
                      Chip(
                        label: Text(bot.aiModel!),
                        backgroundColor: Colors.green[100],
                      ),
                    ],
                  ],
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildAvatar() {
    if (bot.avatarUrl.isNotEmpty) {
      return CircleAvatar(
        radius: 30,
        backgroundImage: NetworkImage(bot.avatarUrl),
      );
    }
    return CircleAvatar(
      radius: 30,
      backgroundColor: Colors.blue[200],
      child: Text(
        bot.name.isNotEmpty ? bot.name[0].toUpperCase() : 'B',
        style: const TextStyle(
          fontSize: 24,
          fontWeight: FontWeight.bold,
          color: Colors.white,
        ),
      ),
    );
  }

  Widget _buildBotTypeChip() {
    IconData icon;
    Color color;
    switch (bot.botType) {
      case 'AI':
        icon = Icons.smart_toy;
        color = Colors.purple;
        break;
      case 'WEBHOOK':
        icon = Icons.link;
        color = Colors.orange;
        break;
      case 'SCRIPTED':
        icon = Icons.code;
        color = Colors.teal;
        break;
      default:
        icon = Icons.help;
        color = Colors.grey;
    }
    return Chip(
      avatar: Icon(icon, size: 18, color: color),
      label: Text(bot.botType),
      backgroundColor: color.withOpacity(0.1),
    );
  }

  Widget _buildStatusChip() {
    return GestureDetector(
      onTap: () => onToggle?.call(!bot.enabled),
      child: Chip(
        avatar: Icon(
          bot.enabled ? Icons.check_circle : Icons.cancel,
          size: 18,
          color: bot.enabled ? Colors.green : Colors.red,
        ),
        label: Text(bot.enabled ? 'Online' : 'Offline'),
        backgroundColor: bot.enabled
            ? Colors.green.withOpacity(0.1)
            : Colors.red.withOpacity(0.1),
      ),
    );
  }
}

class BotChatWidget extends StatefulWidget {
  final Bot bot;
  final String userId;

  const BotChatWidget({
    Key? key,
    required this.bot,
    required this.userId,
  }) : super(key: key);

  @override
  State<BotChatWidget> createState() => _BotChatWidgetState();
}

class _BotChatWidgetState extends State<BotChatWidget> {
  final BotService _botService = BotService();
  final TextEditingController _messageController = TextEditingController();
  final List<BotMessage> _messages = [];
  bool _isTyping = false;

  @override
  void dispose() {
    _messageController.dispose();
    super.dispose();
  }

  Future<void> _sendMessage() async {
    final content = _messageController.text.trim();
    if (content.isEmpty) return;

    setState(() {
      _messages.add(BotMessage(
        id: DateTime.now().millisecondsSinceEpoch.toString(),
        botId: widget.bot.id,
        senderId: widget.userId,
        content: content,
        messageType: 'text',
        timestamp: DateTime.now(),
      ));
      _isTyping = true;
    });

    _messageController.clear();

    try {
      final response = await _botService.sendMessage(
        widget.bot.id,
        widget.userId,
        content,
      );
      setState(() {
        _messages.add(BotMessage(
          id: (DateTime.now().millisecondsSinceEpoch + 1).toString(),
          botId: widget.bot.id,
          senderId: widget.bot.id,
          content: response,
          messageType: 'text',
          timestamp: DateTime.now(),
        ));
        _isTyping = false;
      });
    } catch (e) {
      setState(() {
        _isTyping = false;
      });
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Error: ${e.toString()}')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        _buildHeader(),
        Expanded(child: _buildMessageList()),
        if (_isTyping) _buildTypingIndicator(),
        _buildInputArea(),
      ],
    );
  }

  Widget _buildHeader() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Theme.of(context).primaryColor,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            blurRadius: 4,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Row(
        children: [
          if (widget.bot.avatarUrl.isNotEmpty)
            CircleAvatar(
              backgroundImage: NetworkImage(widget.bot.avatarUrl),
            )
          else
            CircleAvatar(
              backgroundColor: Colors.white,
              child: Text(
                widget.bot.name[0].toUpperCase(),
                style: TextStyle(color: Theme.of(context).primaryColor),
              ),
            ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  widget.bot.name,
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                Text(
                  widget.bot.botTypeLabel,
                  style: const TextStyle(
                    color: Colors.white70,
                    fontSize: 14,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildMessageList() {
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: _messages.length,
      itemBuilder: (context, index) {
        final message = _messages[index];
        final isUser = message.senderId == widget.userId;
        return _buildMessageBubble(message, isUser);
      },
    );
  }

  Widget _buildMessageBubble(BotMessage message, bool isUser) {
    return Align(
      alignment: isUser ? Alignment.centerRight : Alignment.centerLeft,
      child: Container(
        margin: const EdgeInsets.only(bottom: 8),
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 10),
        decoration: BoxDecoration(
          color: isUser ? Colors.blue : Colors.grey[300],
          borderRadius: BorderRadius.circular(16),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(
              message.content,
              style: TextStyle(
                color: isUser ? Colors.white : Colors.black,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              _formatTime(message.timestamp),
              style: TextStyle(
                fontSize: 10,
                color: isUser ? Colors.white70 : Colors.grey[600],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildTypingIndicator() {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          Text(
            '${widget.bot.name} is typing...',
            style: TextStyle(
              color: Colors.grey[600],
              fontStyle: FontStyle.italic,
            ),
          ),
          const SizedBox(width: 8),
          const SizedBox(
            width: 20,
            height: 20,
            child: CircularProgressIndicator(strokeWidth: 2),
          ),
        ],
      ),
    );
  }

  Widget _buildInputArea() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            blurRadius: 4,
            offset: const Offset(0, -2),
          ),
        ],
      ),
      child: Row(
        children: [
          Expanded(
            child: TextField(
              controller: _messageController,
              decoration: InputDecoration(
                hintText: 'Type a message...',
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(24),
                ),
                contentPadding: const EdgeInsets.symmetric(
                  horizontal: 16,
                  vertical: 8,
                ),
              ),
              onSubmitted: (_) => _sendMessage(),
            ),
          ),
          const SizedBox(width: 8),
          IconButton(
            icon: const Icon(Icons.send),
            onPressed: _sendMessage,
            color: Theme.of(context).primaryColor,
          ),
        ],
      ),
    );
  }

  String _formatTime(DateTime time) {
    return '${time.hour.toString().padLeft(2, '0')}:${time.minute.toString().padLeft(2, '0')}';
  }
}
