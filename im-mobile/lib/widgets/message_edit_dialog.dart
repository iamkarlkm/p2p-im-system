/**
 * 消息编辑对话框组件
 * 
 * @author IM Development Team
 * @since 2026-03-27
 */

import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/message_edit_model.dart';
import '../providers/message_edit_provider.dart';

/// 消息编辑对话框
class MessageEditDialog extends StatefulWidget {
  final int messageId;
  final String currentContent;
  final int editCount;
  final int maxEditCount;
  final int remainingEdits;

  const MessageEditDialog({
    super.key,
    required this.messageId,
    required this.currentContent,
    required this.editCount,
    required this.maxEditCount,
    required this.remainingEdits,
  });

  @override
  State<MessageEditDialog> createState() => _MessageEditDialogState();
}

class _MessageEditDialogState extends State<MessageEditDialog> {
  late TextEditingController _contentController;
  late TextEditingController _reasonController;
  EditType _selectedType = EditType.normal;

  @override
  void initState() {
    super.initState();
    _contentController = TextEditingController(text: widget.currentContent);
    _reasonController = TextEditingController();
    
    // 初始化 Provider
    WidgetsBinding.instance.addPostFrameCallback((_) {
      context.read<MessageEditProvider>().startEditing(
        widget.messageId,
        widget.currentContent,
      );
    });
  }

  @override
  void dispose() {
    _contentController.dispose();
    _reasonController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<MessageEditProvider>(
      builder: (context, provider, child) {
        return AlertDialog(
          title: Row(
            children: [
              const Icon(Icons.edit, size: 20),
              const SizedBox(width: 8),
              const Text('编辑消息'),
              const SizedBox(width: 8),
              Chip(
                label: Text('第 ${widget.editCount + 1} 次'),
                backgroundColor: Colors.blue.shade100,
                labelStyle: const TextStyle(fontSize: 12),
              ),
            ],
          ),
          content: SingleChildScrollView(
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                // 编辑限制提示
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: Colors.blue.shade50,
                    borderRadius: BorderRadius.circular(8),
                  ),
                  child: Row(
                    children: [
                      Icon(Icons.info_outline, size: 16, color: Colors.blue.shade700),
                      const SizedBox(width: 8),
                      Expanded(
                        child: Text(
                          '剩余可编辑次数: ${widget.remainingEdits}/${widget.maxEditCount} · 30分钟内有效',
                          style: TextStyle(
                            fontSize: 13,
                            color: Colors.blue.shade700,
                          ),
                        ),
                      ),
                    ],
                  ),
                ),

                if (provider.error != null) ...[
                  const SizedBox(height: 12),
                  Container(
                    padding: const EdgeInsets.all(12),
                    decoration: BoxDecoration(
                      color: Colors.red.shade50,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Row(
                      children: [
                        Icon(Icons.error_outline, size: 16, color: Colors.red.shade700),
                        const SizedBox(width: 8),
                        Expanded(
                          child: Text(
                            provider.error!,
                            style: TextStyle(
                              fontSize: 13,
                              color: Colors.red.shade700,
                            ),
                          ),
                        ),
                      ],
                    ),
                  ),
                ],

                const SizedBox(height: 16),

                // 当前内容
                Text(
                  '当前内容',
                  style: TextStyle(
                    fontSize: 13,
                    fontWeight: FontWeight.w500,
                    color: Colors.grey.shade600,
                  ),
                ),
                const SizedBox(height: 4),
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: Colors.grey.shade100,
                    borderRadius: BorderRadius.circular(8),
                    border: Border.all(color: Colors.grey.shade300),
                  ),
                  constraints: const BoxConstraints(maxHeight: 100),
                  child: SingleChildScrollView(
                    child: Text(
                      widget.currentContent,
                      style: TextStyle(
                        fontSize: 14,
                        color: Colors.grey.shade600,
                      ),
                    ),
                  ),
                ),

                const Divider(height: 24),

                // 编辑类型
                DropdownButtonFormField<EditType>(
                  value: _selectedType,
                  decoration: const InputDecoration(
                    labelText: '编辑类型',
                    border: OutlineInputBorder(),
                    contentPadding: EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                  ),
                  items: [
                    EditType.normal,
                    EditType.correction,
                    EditType.formatting,
                    EditType.contentUpdate,
                  ].map((type) {
                    return DropdownMenuItem(
                      value: type,
                      child: Text(type.label),
                    );
                  }).toList(),
                  onChanged: (value) {
                    if (value != null) {
                      setState(() => _selectedType = value);
                      provider.updateEditType(value);
                    }
                  },
                ),

                const SizedBox(height: 16),

                // 新内容输入
                TextField(
                  controller: _contentController,
                  maxLines: 5,
                  maxLength: 10000,
                  decoration: const InputDecoration(
                    labelText: '新内容 *',
                    hintText: '输入新的消息内容...',
                    border: OutlineInputBorder(),
                    counterText: '',
                  ),
                  onChanged: provider.updateEditedContent,
                ),

                const SizedBox(height: 16),

                // 编辑原因
                TextField(
                  controller: _reasonController,
                  decoration: const InputDecoration(
                    labelText: '编辑原因（可选）',
                    hintText: '简要说明编辑原因',
                    border: OutlineInputBorder(),
                  ),
                  onChanged: provider.updateEditReason,
                ),
              ],
            ),
          ),
          actions: [
            TextButton(
              onPressed: provider.isSubmitting
                  ? null
                  : () {
                      provider.cancelEditing();
                      Navigator.of(context).pop();
                    },
              child: const Text('取消'),
            ),
            ElevatedButton.icon(
              onPressed: provider.isSubmitting || !_canSubmit(provider)
                  ? null
                  : () => _submit(provider),
              icon: provider.isSubmitting
                  ? const SizedBox(
                      width: 16,
                      height: 16,
                      child: CircularProgressIndicator(strokeWidth: 2),
                    )
                  : const Icon(Icons.save, size: 18),
              label: Text(provider.isSubmitting ? '保存中...' : '保存编辑'),
            ),
          ],
        );
      },
    );
  }

  bool _canSubmit(MessageEditProvider provider) {
    final content = _contentController.text.trim();
    return content.isNotEmpty && content != widget.currentContent;
  }

  Future<void> _submit(MessageEditProvider provider) async {
    final success = await provider.submitEdit();
    if (success && mounted) {
      Navigator.of(context).pop(true);
    }
  }
}

/// 编辑标记组件
class EditMark extends StatelessWidget {
  final int editCount;
  final DateTime? lastEditedAt;
  final VoidCallback? onTap;

  const EditMark({
    super.key,
    required this.editCount,
    this.lastEditedAt,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
        decoration: BoxDecoration(
          color: Colors.grey.shade200,
          borderRadius: BorderRadius.circular(4),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(Icons.edit, size: 12, color: Colors.grey.shade600),
            const SizedBox(width: 2),
            Text(
              '已编辑',
              style: TextStyle(
                fontSize: 11,
                color: Colors.grey.shade600,
              ),
            ),
            if (lastEditedAt != null) ...[
              const SizedBox(width: 4),
              Text(
                _formatTime(lastEditedAt!),
                style: TextStyle(
                  fontSize: 10,
                  color: Colors.grey.shade500,
                ),
              ),
            ],
          ],
        ),
      ),
    );
  }

  String _formatTime(DateTime time) {
    final now = DateTime.now();
    if (time.year == now.year && time.month == now.month && time.day == now.day) {
      return '${time.hour.toString().padLeft(2, '0')}:${time.minute.toString().padLeft(2, '0')}';
    }
    return '${time.month}/${time.day}';
  }
}

/// 显示编辑对话框
Future<bool?> showMessageEditDialog(
  BuildContext context, {
  required int messageId,
  required String currentContent,
  required int editCount,
  int maxEditCount = 10,
  required int remainingEdits,
}) {
  return showDialog<bool>(
    context: context,
    builder: (context) => MessageEditDialog(
      messageId: messageId,
      currentContent: currentContent,
      editCount: editCount,
      maxEditCount: maxEditCount,
      remainingEdits: remainingEdits,
    ),
  );
}
