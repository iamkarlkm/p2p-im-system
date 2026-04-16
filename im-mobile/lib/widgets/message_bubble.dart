import 'package:flutter/material.dart';
import '../im/im_message.dart';

/// 消息气泡组件
class MessageBubble extends StatelessWidget {
  final IMMessage message;
  final bool isMe;
  final String? avatarUrl;

  const MessageBubble({
    Key? key,
    required this.message,
    required this.isMe,
    this.avatarUrl,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.symmetric(vertical: 4, horizontal: 8),
      child: Row(
        mainAxisAlignment: isMe ? MainAxisAlignment.end : MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (!isMe) _buildAvatar(),
          SizedBox(width: 8),
          Flexible(
            child: Container(
              padding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
              decoration: BoxDecoration(
                color: isMe ? Color(0xFF95EC69) : Colors.white,
                borderRadius: BorderRadius.circular(4),
                boxShadow: [
                  BoxShadow(
                    color: Colors.black.withOpacity(0.05),
                    blurRadius: 2,
                    offset: Offset(0, 1),
                  ),
                ],
              ),
              child: _buildContent(),
            ),
          ),
          SizedBox(width: 8),
          if (isMe) _buildAvatar(),
        ],
      ),
    );
  }

  Widget _buildAvatar() {
    return CircleAvatar(
      radius: 20,
      backgroundImage: avatarUrl != null ? NetworkImage(avatarUrl!) : null,
      child: avatarUrl == null ? Icon(Icons.person) : null,
    );
  }

  Widget _buildContent() {
    switch (message.contentType) {
      case ContentType.text:
        return Text(
          message.content,
          style: TextStyle(fontSize: 16),
        );
      case ContentType.image:
        return ClipRRect(
          borderRadius: BorderRadius.circular(4),
          child: Image.network(
            message.content,
            width: 200,
            fit: BoxFit.cover,
            loadingBuilder: (context, child, loadingProgress) {
              if (loadingProgress == null) return child;
              return Container(
                width: 200,
                height: 150,
                color: Colors.grey[300],
                child: Center(child: CircularProgressIndicator()),
              );
            },
          ),
        );
      default:
        return Text(
          '[${message.contentType.name}]',
          style: TextStyle(color: Colors.grey),
        );
    }
  }
}
