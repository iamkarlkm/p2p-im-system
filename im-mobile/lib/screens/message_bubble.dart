import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

/// 消息气泡组件
class MessageBubble extends StatelessWidget {
  final Map<String, dynamic> message;
  final bool isMe;
  final VoidCallback? onRetry;

  const MessageBubble({
    Key? key,
    required this.message,
    required this.isMe,
    this.onRetry,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    final content = message['content'] ?? '';
    final timestamp = message['timestamp'] as int?;
    final status = message['status'] ?? 'sent';
    final messageType = message['messageType'] ?? 'TEXT';

    return Container(
      margin: EdgeInsets.symmetric(vertical: 4, horizontal: 8),
      child: Row(
        mainAxisAlignment: isMe ? MainAxisAlignment.end : MainAxisAlignment.start,
        crossAxisAlignment: CrossAxisAlignment.end,
        children: [
          if (!isMe) _buildAvatar(),
          SizedBox(width: 8),
          Flexible(
            child: Column(
              crossAxisAlignment: isMe ? CrossAxisAlignment.end : CrossAxisAlignment.start,
              children: [
                // 发送者名称（非自己时显示）
                if (!isMe && message['senderName'] != null)
                  Padding(
                    padding: EdgeInsets.only(left: 12, bottom: 2),
                    child: Text(
                      message['senderName'],
                      style: TextStyle(
                        fontSize: 12,
                        color: Colors.grey[600],
                      ),
                    ),
                  ),

                // 消息气泡
                GestureDetector(
                  onLongPress: () => _showMessageOptions(context),
                  child: Container(
                    padding: EdgeInsets.symmetric(horizontal: 16, vertical: 10),
                    decoration: BoxDecoration(
                      color: isMe
                          ? Theme.of(context).primaryColor
                          : Colors.grey[200],
                      borderRadius: BorderRadius.only(
                        topLeft: Radius.circular(16),
                        topRight: Radius.circular(16),
                        bottomLeft: Radius.circular(isMe ? 16 : 4),
                        bottomRight: Radius.circular(isMe ? 4 : 16),
                      ),
                    ),
                    child: _buildMessageContent(content, messageType, context),
                  ),
                ),

                // 时间和状态
                Padding(
                  padding: EdgeInsets.only(top: 2, left: 4, right: 4),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Text(
                        _formatTimestamp(timestamp),
                        style: TextStyle(
                          fontSize: 10,
                          color: Colors.grey[500],
                        ),
                      ),
                      if (isMe) ...[
                        SizedBox(width: 4),
                        _buildStatusIcon(status),
                      ],
                    ],
                  ),
                ),
              ],
            ),
          ),
          SizedBox(width: 8),
          if (isMe) _buildAvatar(isMe: true),
        ],
      ),
    );
  }

  /// 构建头像
  Widget _buildAvatar({bool isMe = false}) {
    final avatar = message['senderAvatar'];
    final name = message['senderName'] ?? 'U';

    return Container(
      width: 36,
      height: 36,
      child: avatar != null
          ? CircleAvatar(
              backgroundImage: NetworkImage(avatar),
              radius: 18,
            )
          : CircleAvatar(
              backgroundColor: isMe ? Colors.blue : Colors.grey[400],
              radius: 18,
              child: Text(
                name.substring(0, 1).toUpperCase(),
                style: TextStyle(
                  color: Colors.white,
                  fontSize: 14,
                  fontWeight: FontWeight.bold,
                ),
              ),
            ),
    );
  }

  /// 构建消息内容
  Widget _buildMessageContent(String content, String messageType, BuildContext context) {
    switch (messageType) {
      case 'IMAGE':
        return _buildImageContent(content);
      case 'FILE':
        return _buildFileContent(content);
      case 'VOICE':
        return _buildVoiceContent(content);
      case 'LOCATION':
        return _buildLocationContent(content);
      default:
        return _buildTextContent(content, context);
    }
  }

  /// 构建文本内容
  Widget _buildTextContent(String content, BuildContext context) {
    return Text(
      content,
      style: TextStyle(
        color: isMe ? Colors.white : Colors.black87,
        fontSize: 15,
      ),
    );
  }

  /// 构建图片内容
  Widget _buildImageContent(String content) {
    return ClipRRect(
      borderRadius: BorderRadius.circular(8),
      child: Image.network(
        content,
        width: 200,
        height: 200,
        fit: BoxFit.cover,
        loadingBuilder: (context, child, loadingProgress) {
          if (loadingProgress == null) return child;
          return Container(
            width: 200,
            height: 200,
            color: Colors.grey[300],
            child: Center(
              child: CircularProgressIndicator(
                value: loadingProgress.expectedTotalBytes != null
                    ? loadingProgress.cumulativeBytesLoaded /
                        loadingProgress.expectedTotalBytes!
                    : null,
              ),
            ),
          );
        },
        errorBuilder: (context, error, stackTrace) {
          return Container(
            width: 200,
            height: 200,
            color: Colors.grey[300],
            child: Icon(Icons.broken_image, color: Colors.grey),
          );
        },
      ),
    );
  }

  /// 构建文件内容
  Widget _buildFileContent(String content) {
    final fileName = message['fileName'] ?? 'File';
    final fileSize = message['fileSize'] ?? 0;

    return Container(
      width: 200,
      padding: EdgeInsets.all(8),
      decoration: BoxDecoration(
        color: isMe ? Colors.blue[700] : Colors.grey[300],
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        children: [
          Icon(
            Icons.insert_drive_file,
            color: isMe ? Colors.white : Colors.grey[700],
            size: 40,
          ),
          SizedBox(width: 8),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  fileName,
                  style: TextStyle(
                    color: isMe ? Colors.white : Colors.black87,
                    fontSize: 14,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
                Text(
                  _formatFileSize(fileSize),
                  style: TextStyle(
                    color: isMe ? Colors.white70 : Colors.grey[600],
                    fontSize: 12,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  /// 构建语音内容
  Widget _buildVoiceContent(String content) {
    final duration = message['duration'] ?? 0;

    return Container(
      width: 120 + (duration * 2).clamp(0, 100).toDouble(),
      child: Row(
        children: [
          Icon(
            Icons.play_arrow,
            color: isMe ? Colors.white : Colors.black87,
          ),
          SizedBox(width: 8),
          Expanded(
            child: Container(
              height: 20,
              decoration: BoxDecoration(
                color: isMe ? Colors.blue[700] : Colors.grey[300],
                borderRadius: BorderRadius.circular(10),
              ),
            ),
          ),
          SizedBox(width: 8),
          Text(
            '${duration}"',
            style: TextStyle(
              color: isMe ? Colors.white : Colors.black87,
              fontSize: 12,
            ),
          ),
        ],
      ),
    );
  }

  /// 构建位置内容
  Widget _buildLocationContent(String content) {
    final locationName = message['locationName'] ?? 'Location';

    return Container(
      width: 200,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Container(
            height: 120,
            color: Colors.grey[300],
            child: Center(
              child: Icon(Icons.map, size: 48, color: Colors.grey),
            ),
          ),
          Padding(
            padding: EdgeInsets.all(8),
            child: Text(
              locationName,
              style: TextStyle(
                color: isMe ? Colors.white : Colors.black87,
                fontSize: 14,
              ),
            ),
          ),
        ],
      ),
    );
  }

  /// 构建状态图标
  Widget _buildStatusIcon(String status) {
    switch (status) {
      case 'sending':
        return SizedBox(
          width: 12,
          height: 12,
          child: CircularProgressIndicator(
            strokeWidth: 1.5,
            valueColor: AlwaysStoppedAnimation<Color>(Colors.grey),
          ),
        );
      case 'sent':
        return Icon(Icons.check, size: 12, color: Colors.grey);
      case 'delivered':
        return Icon(Icons.done_all, size: 12, color: Colors.grey);
      case 'read':
        return Icon(Icons.done_all, size: 12, color: Colors.blue);
      case 'failed':
        return GestureDetector(
          onTap: onRetry,
          child: Icon(Icons.error_outline, size: 12, color: Colors.red),
        );
      default:
        return SizedBox.shrink();
    }
  }

  /// 格式化时间戳
  String _formatTimestamp(int? timestamp) {
    if (timestamp == null) return '';
    final date = DateTime.fromMillisecondsSinceEpoch(timestamp);
    final now = DateTime.now();
    final today = DateTime(now.year, now.month, now.day);
    final messageDate = DateTime(date.year, date.month, date.day);

    if (messageDate == today) {
      return DateFormat('HH:mm').format(date);
    } else if (messageDate == today.subtract(Duration(days: 1))) {
      return 'Yesterday ${DateFormat('HH:mm').format(date)}';
    } else {
      return DateFormat('MM/dd HH:mm').format(date);
    }
  }

  /// 格式化文件大小
  String _formatFileSize(int bytes) {
    if (bytes < 1024) return '$bytes B';
    if (bytes < 1024 * 1024) return '${(bytes / 1024).toStringAsFixed(1)} KB';
    return '${(bytes / (1024 * 1024)).toStringAsFixed(1)} MB';
  }

  /// 显示消息选项
  void _showMessageOptions(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: Icon(Icons.copy),
              title: Text('Copy'),
              onTap: () {
                // 复制消息内容
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: Icon(Icons.reply),
              title: Text('Reply'),
              onTap: () {
                Navigator.pop(context);
              },
            ),
            ListTile(
              leading: Icon(Icons.delete),
              title: Text('Delete'),
              onTap: () {
                Navigator.pop(context);
              },
            ),
            if (isMe && message['status'] != 'failed')
              ListTile(
                leading: Icon(Icons.undo),
                title: Text('Recall'),
                onTap: () {
                  Navigator.pop(context);
                },
              ),
            if (message['status'] == 'failed' && onRetry != null)
              ListTile(
                leading: Icon(Icons.refresh),
                title: Text('Retry'),
                onTap: () {
                  Navigator.pop(context);
                  onRetry!();
                },
              ),
          ],
        ),
      ),
    );
  }
}
