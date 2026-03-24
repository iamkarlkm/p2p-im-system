import 'package:flutter/material.dart';
import '../services/typing_service.dart';
import '../models/typing_user.dart';

class TypingIndicatorWidget extends StatefulWidget {
  final String conversationId;
  final String currentUserId;

  const TypingIndicatorWidget({
    super.key,
    required this.conversationId,
    required this.currentUserId,
  });

  @override
  State<TypingIndicatorWidget> createState() => _TypingIndicatorWidgetState();
}

class _TypingIndicatorWidgetState extends State<TypingIndicatorWidget>
    with SingleTickerProviderStateMixin {
  final TypingService _service = TypingService();
  List<TypingUser> _typingUsers = [];

  @override
  void initState() {
    super.initState();
    _loadInitial();
    _service.onTypingChanged(widget.conversationId, _onTypingChanged);
  }

  void _loadInitial() async {
    final users = await _service.fetchTypingStatus(widget.conversationId);
    if (mounted) {
      setState(() {
        _typingUsers = users.where((u) => u.userId != widget.currentUserId).toList();
      });
    }
  }

  void _onTypingChanged(List<TypingUser> users) {
    if (mounted) {
      setState(() {
        _typingUsers = users.where((u) => u.userId != widget.currentUserId).toList();
      });
    }
  }

  @override
  void dispose() {
    _service.dispose();
    super.dispose();
  }

  String _buildText() {
    if (_typingUsers.isEmpty) return '';
    final names = _typingUsers.map((u) => u.userName ?? u.userId).toList();
    if (names.length == 1) return '${names[0]} 正在输入...';
    if (names.length == 2) return '${names[0]} 和 ${names[1]} 正在输入...';
    return '${names[0]}、${names[1]} 等${names.length}人正在输入...';
  }

  @override
  Widget build(BuildContext context) {
    if (_typingUsers.isEmpty) return const SizedBox.shrink();
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
      child: Row(
        children: [
          _buildDots(),
          const SizedBox(width: 8),
          Expanded(
            child: Text(
              _buildText(),
              style: const TextStyle(fontSize: 12, color: Colors.grey),
              overflow: TextOverflow.ellipsis,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildDots() {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: List.generate(3, (i) {
        return TweenAnimationBuilder<double>(
          tween: Tween(begin: 0.0, end: 1.0),
          duration: Duration(milliseconds: 600 + i * 200),
          builder: (context, value, child) {
            return Container(
              margin: const EdgeInsets.symmetric(horizontal: 1.5),
              width: 6,
              height: 6,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: Colors.grey.shade400.withOpacity(0.5 + value * 0.5),
              ),
            );
          },
        );
      }),
    );
  }
}
