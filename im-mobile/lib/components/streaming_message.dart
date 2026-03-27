import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/multimodal_message.dart';

/// 流式消息气泡组件
class StreamingMessageBubble extends StatelessWidget {
  final MultimodalMessage message;
  final bool isMe;
  final VoidCallback? onRetry;
  final VoidCallback? onCopy;
  final VoidCallback? onRegenerate;

  const StreamingMessageBubble({
    super.key,
    required this.message,
    required this.isMe,
    this.onRetry,
    this.onCopy,
    this.onRegenerate,
  });

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final backgroundColor = isMe
        ? theme.colorScheme.primaryContainer
        : theme.colorScheme.surfaceContainerHighest;
    final textColor = isMe
        ? theme.colorScheme.onPrimaryContainer
        : theme.colorScheme.onSurfaceVariant;

    return Align(
      alignment: isMe ? Alignment.centerRight : Alignment.centerLeft,
      child: Container(
        constraints: BoxConstraints(
          maxWidth: MediaQuery.of(context).size.width * 0.75,
        ),
        margin: const EdgeInsets.symmetric(vertical: 4, horizontal: 8),
        child: Column(
          crossAxisAlignment: isMe ? CrossAxisAlignment.end : CrossAxisAlignment.start,
          children: [
            // 消息内容
            GestureDetector(
              onLongPress: () => _showMessageOptions(context),
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                decoration: BoxDecoration(
                  color: backgroundColor,
                  borderRadius: BorderRadius.circular(18).copyWith(
                    bottomLeft: isMe ? const Radius.circular(18) : const Radius.circular(4),
                    bottomRight: isMe ? const Radius.circular(4) : const Radius.circular(18),
                  ),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    // 附件
                    if (message.attachments.isNotEmpty) 
                      _buildAttachments(context),
                    
                    // 文本内容
                    if (message.content.isNotEmpty)
                      _buildContent(context, textColor),
                    
                    // 流式指示器
                    if (message.isStreaming)
                      _buildStreamingIndicator(context),
                  ],
                ),
              ),
            ),

            // 时间戳和操作按钮
            Padding(
              padding: const EdgeInsets.only(top: 4, left: 4, right: 4),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text(
                    _formatTime(message.timestamp),
                    style: theme.textTheme.bodySmall?.copyWith(
                      color: theme.colorScheme.outline,
                      fontSize: 11,
                    ),
                  ),
                  if (message.status == MessageStatus.sending) ...[
                    const SizedBox(width: 4),
                    SizedBox(
                      width: 10,
                      height: 10,
                      child: CircularProgressIndicator(
                        strokeWidth: 1.5,
                        color: theme.colorScheme.outline,
                      ),
                    ),
                  ],
                  if (message.status == MessageStatus.failed) ...[
                    const SizedBox(width: 4),
                    IconButton(
                      icon: const Icon(Icons.error_outline, size: 14, color: Colors.red),
                      onPressed: onRetry,
                      padding: EdgeInsets.zero,
                      constraints: const BoxConstraints(),
                    ),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildContent(BuildContext context, Color textColor) {
    // 简单的代码块检测和渲染
    final content = message.content;
    final codeBlocks = _parseCodeBlocks(content);
    
    if (codeBlocks.length == 1 && codeBlocks[0].isCode) {
      return _buildCodeBlock(codeBlocks[0]);
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: codeBlocks.map((block) {
        if (block.isCode) {
          return _buildCodeBlock(block);
        } else {
          return Text(
            block.content,
            style: TextStyle(color: textColor, height: 1.4),
          );
        }
      }).toList(),
    );
  }

  Widget _buildCodeBlock(_CodeBlock block) {
    return Container(
      width: double.infinity,
      margin: const EdgeInsets.symmetric(vertical: 8),
      decoration: BoxDecoration(
        color: Colors.black87,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 语言标签和复制按钮
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
            decoration: BoxDecoration(
              color: Colors.grey[900],
              borderRadius: const BorderRadius.vertical(top: Radius.circular(8)),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  block.language ?? 'code',
                  style: const TextStyle(
                    color: Colors.grey,
                    fontSize: 12,
                  ),
                ),
                InkWell(
                  onTap: () {
                    Clipboard.setData(ClipboardData(text: block.content));
                  },
                  child: const Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Icon(Icons.copy, size: 14, color: Colors.grey),
                      SizedBox(width: 4),
                      Text(
                        '复制',
                        style: TextStyle(color: Colors.grey, fontSize: 12),
                      ),
                    ],
                  ),
                ),
              ],
            ),
          ),
          // 代码内容
          Container(
            width: double.infinity,
            padding: const EdgeInsets.all(12),
            child: Text(
              block.content,
              style: const TextStyle(
                color: Colors.white,
                fontFamily: 'monospace',
                fontSize: 13,
                height: 1.5,
              ),
            ),
          ),
        ],
      ),
    );
  }

  List<_CodeBlock> _parseCodeBlocks(String content) {
    final blocks = <_CodeBlock>[];
    final regex = RegExp(r'```(\w*)\n([\s\S]*?)```');
    var lastEnd = 0;

    for (final match in regex.allMatches(content)) {
      if (match.start > lastEnd) {
        blocks.add(_CodeBlock(
          content: content.substring(lastEnd, match.start).trim(),
          isCode: false,
        ));
      }
      blocks.add(_CodeBlock(
        content: match.group(2)!.trim(),
        language: match.group(1)?.isNotEmpty == true ? match.group(1) : null,
        isCode: true,
      ));
      lastEnd = match.end;
    }

    if (lastEnd < content.length) {
      blocks.add(_CodeBlock(
        content: content.substring(lastEnd).trim(),
        isCode: false,
      ));
    }

    if (blocks.isEmpty) {
      blocks.add(_CodeBlock(content: content, isCode: false));
    }

    return blocks;
  }

  Widget _buildAttachments(BuildContext context) {
    if (message.attachments.length == 1) {
      return _buildSingleAttachment(message.attachments.first);
    }

    return Wrap(
      spacing: 8,
      runSpacing: 8,
      children: message.attachments.map((a) => _buildSingleAttachment(a)).toList(),
    );
  }

  Widget _buildSingleAttachment(MessageAttachment attachment) {
    final isImage = attachment.fileType.startsWith('image/');
    final isVideo = attachment.fileType.startsWith('video/');
    final isAudio = attachment.fileType.startsWith('audio/');

    if (isImage) {
      return ClipRRect(
        borderRadius: BorderRadius.circular(12),
        child: Image.network(
          attachment.url ?? '',
          width: 200,
          fit: BoxFit.cover,
          loadingBuilder: (context, child, loadingProgress) {
            if (loadingProgress == null) return child;
            return Container(
              width: 200,
              height: 150,
              color: Colors.grey[300],
              child: const Center(child: CircularProgressIndicator()),
            );
          },
          errorBuilder: (_, __, ___) => _buildFilePlaceholder(attachment),
        ),
      );
    }

    if (isVideo) {
      return Container(
        width: 200,
        height: 150,
        decoration: BoxDecoration(
          color: Colors.black87,
          borderRadius: BorderRadius.circular(12),
        ),
        child: Stack(
          alignment: Alignment.center,
          children: [
            if (attachment.thumbnailUrl != null)
              ClipRRect(
                borderRadius: BorderRadius.circular(12),
                child: Image.network(
                  attachment.thumbnailUrl!,
                  width: 200,
                  height: 150,
                  fit: BoxFit.cover,
                ),
              ),
            Container(
              width: 50,
              height: 50,
              decoration: BoxDecoration(
                color: Colors.black54,
                shape: BoxShape.circle,
              ),
              child: const Icon(Icons.play_arrow, color: Colors.white, size: 32),
            ),
            Positioned(
              bottom: 8,
              right: 8,
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                decoration: BoxDecoration(
                  color: Colors.black54,
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  _formatDuration(attachment.duration),
                  style: const TextStyle(color: Colors.white, fontSize: 12),
                ),
              ),
            ),
          ],
        ),
      );
    }

    if (isAudio) {
      return Container(
        width: 250,
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: Colors.grey[200],
          borderRadius: BorderRadius.circular(12),
        ),
        child: Row(
          children: [
            const Icon(Icons.audiotrack, color: Colors.blue),
            const SizedBox(width: 8),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    attachment.fileName,
                    maxLines: 1,
                    overflow: TextOverflow.ellipsis,
                    style: const TextStyle(fontWeight: FontWeight.w500),
                  ),
                  if (attachment.duration != null)
                    Text(
                      _formatDuration(attachment.duration),
                      style: TextStyle(color: Colors.grey[600], fontSize: 12),
                    ),
                ],
              ),
            ),
            IconButton(
              icon: const Icon(Icons.play_arrow),
              onPressed: () {},
            ),
          ],
        ),
      );
    }

    return _buildFilePlaceholder(attachment);
  }

  Widget _buildFilePlaceholder(MessageAttachment attachment) {
    return Container(
      width: 200,
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.grey[200],
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        children: [
          Icon(Icons.insert_drive_file, color: Colors.grey[600]),
          const SizedBox(width: 8),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  attachment.fileName,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: const TextStyle(fontWeight: FontWeight.w500),
                ),
                Text(
                  _formatFileSize(attachment.fileSize),
                  style: TextStyle(color: Colors.grey[600], fontSize: 12),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildStreamingIndicator(BuildContext context) {
    return Container(
      margin: const EdgeInsets.only(top: 8),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          _buildDot(0),
          _buildDot(1),
          _buildDot(2),
        ],
      ),
    );
  }

  Widget _buildDot(int index) {
    return AnimatedContainer(
      duration: const Duration(milliseconds: 300),
      margin: const EdgeInsets.symmetric(horizontal: 2),
      width: 6,
      height: 6,
      decoration: BoxDecoration(
        color: Colors.grey[400],
        shape: BoxShape.circle,
      ),
    );
  }

  void _showMessageOptions(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: const Icon(Icons.copy),
              title: const Text('复制'),
              onTap: () {
                Clipboard.setData(ClipboardData(text: message.content));
                Navigator.pop(context);
              },
            ),
            if (onRegenerate != null && !isMe)
              ListTile(
                leading: const Icon(Icons.refresh),
                title: const Text('重新生成'),
                onTap: () {
                  Navigator.pop(context);
                  onRegenerate?.call();
                },
              ),
          ],
        ),
      ),
    );
  }

  String _formatTime(DateTime time) {
    final now = DateTime.now();
    final diff = now.difference(time);
    
    if (diff.inDays > 0) {
      return '${time.month}/${time.day}';
    }
    return '${time.hour.toString().padLeft(2, '0')}:${time.minute.toString().padLeft(2, '0')}';
  }

  String _formatDuration(int? seconds) {
    if (seconds == null) return '00:00';
    final mins = seconds ~/ 60;
    final secs = seconds % 60;
    return '${mins.toString().padLeft(2, '0')}:${secs.toString().padLeft(2, '0')}';
  }

  String _formatFileSize(int bytes) {
    if (bytes < 1024) return '$bytes B';
    if (bytes < 1024 * 1024) return '${(bytes / 1024).toStringAsFixed(1)} KB';
    return '${(bytes / (1024 * 1024)).toStringAsFixed(1)} MB';
  }
}

class _CodeBlock {
  final String content;
  final String? language;
  final bool isCode;

  _CodeBlock({
    required this.content,
    this.language,
    required this.isCode,
  });
}
