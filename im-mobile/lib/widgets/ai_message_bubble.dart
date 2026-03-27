import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import '../models/ai_message_model.dart';
import 'ai_voice_player.dart';

class AiMessageBubble extends StatelessWidget {
  final AiMessage message;
  final Function(String)? onSuggestionTap;

  const AiMessageBubble({
    super.key,
    required this.message,
    this.onSuggestionTap,
  });

  @override
  Widget build(BuildContext context) {
    final isUser = message.type == AiMessageType.user;
    final isError = message.type == AiMessageType.error;

    return Container(
      margin: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment: isUser ? MainAxisAlignment.end : MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (!isUser) _buildAvatar(),
          const SizedBox(width: 8),
          Flexible(
            child: Column(
              crossAxisAlignment: isUser ? CrossAxisAlignment.end : CrossAxisAlignment.start,
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                  decoration: BoxDecoration(
                    color: isError
                        ? Colors.red.shade100
                        : isUser
                            ? Theme.of(context).colorScheme.primary
                            : Colors.grey.shade200,
                    borderRadius: BorderRadius.circular(20).copyWith(
                      bottomLeft: isUser ? const Radius.circular(20) : const Radius.circular(4),
                      bottomRight: isUser ? const Radius.circular(4) : const Radius.circular(20),
                    ),
                  ),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      if (message.isVoice && message.audioUrl != null)
                        AiVoicePlayer(audioPath: message.audioUrl!)
                      else
                        Text(
                          message.content,
                          style: TextStyle(
                            color: isUser ? Colors.white : Colors.black87,
                            fontSize: 15,
                            height: 1.4,
                          ),
                        ),
                      if (message.isVoiceResponse && message.audioUrl != null)
                        Padding(
                          padding: const EdgeInsets.only(top: 8),
                          child: AiVoicePlayer(audioPath: message.audioUrl!, isSmall: true),
                        ),
                    ],
                  ),
                ),
                if (message.suggestions != null && message.suggestions!.isNotEmpty)
                  _buildSuggestions(context),
                const SizedBox(height: 2),
                Text(
                  DateFormat('HH:mm').format(message.timestamp),
                  style: TextStyle(
                    fontSize: 11,
                    color: Colors.grey.shade500,
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(width: 8),
          if (isUser) _buildUserAvatar(),
        ],
      ),
    );
  }

  Widget _buildAvatar() {
    return Container(
      width: 36,
      height: 36,
      decoration: BoxDecoration(
        gradient: const LinearGradient(
          colors: [Colors.blue, Colors.purple],
          begin: Alignment.topLeft,
          end: Alignment.bottomRight,
        ),
        borderRadius: BorderRadius.circular(18),
      ),
      child: const Icon(
        Icons.smart_toy,
        color: Colors.white,
        size: 20,
      ),
    );
  }

  Widget _buildUserAvatar() {
    return Container(
      width: 36,
      height: 36,
      decoration: BoxDecoration(
        color: Colors.grey.shade300,
        borderRadius: BorderRadius.circular(18),
      ),
      child: Icon(
        Icons.person,
        color: Colors.grey.shade600,
        size: 20,
      ),
    );
  }

  Widget _buildSuggestions(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 8),
      child: Wrap(
        spacing: 8,
        runSpacing: 8,
        children: message.suggestions!.map((suggestion) {
          return ActionChip(
            label: Text(
              suggestion,
              style: const TextStyle(fontSize: 12),
            ),
            backgroundColor: Colors.blue.shade50,
            side: BorderSide(color: Colors.blue.shade200),
            onPressed: () => onSuggestionTap?.call(suggestion),
          );
        }).toList(),
      ),
    );
  }
}
