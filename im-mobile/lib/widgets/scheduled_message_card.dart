import 'package:flutter/material.dart';
import 'package:im_mobile/models/scheduled_message_model.dart';
import 'package:intl/intl.dart';

class ScheduledMessageCard extends StatelessWidget {
  final ScheduledMessageModel message;
  final VoidCallback? onCancel;
  final VoidCallback onDelete;

  const ScheduledMessageCard({
    Key? key,
    required this.message,
    this.onCancel,
    required this.onDelete,
  }) : super(key: key);

  Color _getStatusColor() {
    switch (message.status) {
      case ScheduledMessageStatus.PENDING:
        return Colors.blue;
      case ScheduledMessageStatus.SENT:
        return Colors.green;
      case ScheduledMessageStatus.CANCELLED:
        return Colors.grey;
      case ScheduledMessageStatus.FAILED:
        return Colors.red;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                CircleAvatar(
                  backgroundImage: message.receiverAvatar != null
                      ? NetworkImage(message.receiverAvatar!)
                      : null,
                  child: message.receiverAvatar == null
                      ? Text(message.receiverNickname?.substring(0, 1) ?? '?')
                      : null,
                ),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        message.receiverNickname ?? '用户${message.receiverId}',
                        style: const TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 16,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Chip(
                        label: Text(
                          message.statusLabel,
                          style: const TextStyle(fontSize: 12),
                        ),
                        backgroundColor: _getStatusColor().withOpacity(0.1),
                        side: BorderSide.none,
                        padding: EdgeInsets.zero,
                        labelStyle: TextStyle(color: _getStatusColor()),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Text(
              message.content,
              style: const TextStyle(fontSize: 14),
              maxLines: 3,
              overflow: TextOverflow.ellipsis,
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                Icon(Icons.schedule, size: 16, color: Colors.grey[600]),
                const SizedBox(width: 4),
                Text(
                  DateFormat('yyyy-MM-dd HH:mm').format(message.scheduledTime),
                  style: TextStyle(color: Colors.grey[600], fontSize: 13),
                ),
              ],
            ),
            if (message.sentTime != null) ...[
              const SizedBox(height: 4),
              Row(
                children: [
                  Icon(Icons.check_circle, size: 16, color: Colors.grey[600]),
                  const SizedBox(width: 4),
                  Text(
                    '实际发送: ${DateFormat('yyyy-MM-dd HH:mm').format(message.sentTime!)}',
                    style: TextStyle(color: Colors.grey[600], fontSize: 13),
                  ),
                ],
              ),
            ],
            if (message.failureReason != null) ...[
              const SizedBox(height: 4),
              Row(
                children: [
                  Icon(Icons.error, size: 16, color: Colors.red[300]),
                  const SizedBox(width: 4),
                  Expanded(
                    child: Text(
                      '失败原因: ${message.failureReason}',
                      style: TextStyle(color: Colors.red[300], fontSize: 13),
                    ),
                  ),
                ],
              ),
            ],
            const SizedBox(height: 12),
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                if (onCancel != null)
                  TextButton.icon(
                    onPressed: onCancel,
                    icon: const Icon(Icons.cancel, size: 18),
                    label: const Text('取消'),
                    style: TextButton.styleFrom(foregroundColor: Colors.orange),
                  ),
                TextButton.icon(
                  onPressed: onDelete,
                  icon: const Icon(Icons.delete, size: 18),
                  label: const Text('删除'),
                  style: TextButton.styleFrom(foregroundColor: Colors.red),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
