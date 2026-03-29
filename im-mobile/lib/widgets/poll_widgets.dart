/**
 * 投票组件 (Poll Widgets)
 * Flutter Mobile 实现
 */

import 'dart:async';
import 'package:flutter/material.dart';
import '../models/poll.dart';
import '../services/poll_service.dart';

// ==================== 工具函数 ====================

String formatRemainingTime(int? seconds) {
  if (seconds == null || seconds <= 0) return '已结束';
  if (seconds < 60) return '${seconds}秒';
  if (seconds < 3600) return '${(seconds / 60).floor()}分钟';
  if (seconds < 86400) {
    final h = (seconds / 3600).floor();
    final m = ((seconds % 3600) / 60).floor();
    return '${h}小时${m}分';
  }
  final d = (seconds / 86400).floor();
  final h = ((seconds % 86400) / 3600).floor();
  return '${d}天${h}小时';
}

String formatVoteCount(int count) {
  if (count == 0) return '暂无投票';
  return '$count 票';
}

// ==================== 投票卡片组件 ====================

class PollCardWidget extends StatefulWidget {
  final Poll poll;
  final String currentUserId;
  final Function(String pollId, List<String> optionIds)? onVote;
  final Function(String pollId)? onCancelVote;
  final Function(String pollId)? onClose;
  final Function(String pollId)? onDelete;
  final Function(String pollId, String text)? onAddOption;
  final VoidCallback? onTap;

  const PollCardWidget({
    super.key,
    required this.poll,
    required this.currentUserId,
    this.onVote,
    this.onCancelVote,
    this.onClose,
    this.onDelete,
    this.onAddOption,
    this.onTap,
  });

  @override
  State<PollCardWidget> createState() => _PollCardWidgetState();
}

class _PollCardWidgetState extends State<PollCardWidget> {
  Set<String> _selectedOptions = {};
  Timer? _countdownTimer;
  int _remainingSeconds = 0;

  bool get _isCreator => widget.poll.creatorId == widget.currentUserId;
  bool get _isActive => widget.poll.isActive;

  @override
  void initState() {
    super.initState();
    _selectedOptions = Set.from(widget.poll.votedOptionIds);
    _remainingSeconds = widget.poll.remainingSeconds ?? 0;
    _startCountdown();
  }

  @override
  void dispose() {
    _countdownTimer?.cancel();
    super.dispose();
  }

  void _startCountdown() {
    if (widget.poll.deadline != null) {
      _countdownTimer = Timer.periodic(const Duration(seconds: 1), (_) {
        if (mounted && _remainingSeconds > 0) {
          setState(() => _remainingSeconds--);
        }
      });
    }
  }

  void _toggleOption(String optionId) {
    if (!_isActive) return;
    setState(() {
      if (!widget.poll.multiSelect) {
        _selectedOptions.clear();
        if (!_selectedOptions.contains(optionId)) {
          _selectedOptions.add(optionId);
        }
      } else {
        if (_selectedOptions.contains(optionId)) {
          _selectedOptions.remove(optionId);
        } else {
          _selectedOptions.add(optionId);
        }
      }
    });
  }

  void _submitVote() {
    if (_selectedOptions.isEmpty) return;
    widget.onVote?.call(widget.poll.pollId, _selectedOptions.toList());
  }

  @override
  Widget build(BuildContext context) {
    final theme = Theme.of(context);
    final poll = widget.poll;

    return Card(
      margin: const EdgeInsets.symmetric(vertical: 4, horizontal: 8),
      elevation: 0,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: BorderSide(color: Colors.grey.shade200),
      ),
      child: InkWell(
        onTap: widget.onTap,
        borderRadius: BorderRadius.circular(12),
        child: Padding(
          padding: const EdgeInsets.all(14),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              // 头部
              Row(
                children: [
                  Expanded(
                    child: Text(
                      poll.question,
                      style: const TextStyle(
                        fontSize: 15,
                        fontWeight: FontWeight.w600,
                      ),
                    ),
                  ),
                  _buildStatusBadge(poll.status),
                ],
              ),
              const SizedBox(height: 6),

              // 标签
              Wrap(
                spacing: 6,
                children: [
                  if (poll.anonymous)
                    _buildTag('🔒 匿名', Colors.grey.shade100),
                  if (poll.multiSelect)
                    _buildTag('☑️ 多选', Colors.grey.shade100),
                ],
              ),
              const SizedBox(height: 8),

              // 统计
              Text(
                '${formatVoteCount(poll.totalVotes)} · ${poll.totalParticipants} 人参与',
                style: TextStyle(
                  fontSize: 12,
                  color: Colors.grey.shade600,
                ),
              ),

              // 截止时间
              if (poll.deadline != null && _remainingSeconds > 0) ...[
                const SizedBox(height: 4),
                Text(
                  '⏰ ${formatRemainingTime(_remainingSeconds)}',
                  style: const TextStyle(
                    fontSize: 12,
                    color: Colors.orange,
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ],
              const SizedBox(height: 12),

              // 选项列表
              ...poll.options.map((opt) => _buildOptionRow(opt, theme)),

              const SizedBox(height: 10),

              // 操作按钮
              _buildActions(),
            ],
          ),
        ),
      ),
    );
  }

  Widget _buildStatusBadge(PollStatus status) {
    Color bgColor;
    switch (status) {
      case PollStatus.active:
        bgColor = Colors.green;
        break;
      case PollStatus.closed:
        bgColor = Colors.grey;
        break;
      case PollStatus.cancelled:
        bgColor = Colors.red;
        break;
    }
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(
        color: bgColor,
        borderRadius: BorderRadius.circular(10),
      ),
      child: Text(
        status.label,
        style: const TextStyle(color: Colors.white, fontSize: 11),
      ),
    );
  }

  Widget _buildTag(String text, Color bgColor) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(
        color: bgColor,
        borderRadius: BorderRadius.circular(10),
      ),
      child: Text(text, style: const TextStyle(fontSize: 11)),
    );
  }

  Widget _buildOptionRow(PollOption option, ThemeData theme) {
    final isSelected = _selectedOptions.contains(option.optionId);
    final isInteractive = _isActive && widget.currentUserId != widget.poll.creatorId;

    return GestureDetector(
      onTap: isInteractive ? () => _toggleOption(option.optionId) : null,
      child: Container(
        margin: const EdgeInsets.only(bottom: 6),
        decoration: BoxDecoration(
          border: Border.all(
            color: isSelected ? Colors.green : Colors.grey.shade300,
            width: isSelected ? 1.5 : 1,
          ),
          borderRadius: BorderRadius.circular(8),
        ),
        child: Stack(
          children: [
            // 结果条
            if (!_isActive)
              Positioned.fill(
                child: FractionallySizedBox(
                  alignment: Alignment.centerLeft,
                  widthFactor: option.percentage / 100,
                  child: Container(
                    decoration: BoxDecoration(
                      color: Colors.green.withValues(alpha: 0.1),
                      borderRadius: BorderRadius.circular(7),
                    ),
                  ),
                ),
              ),
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
              child: Row(
                children: [
                  // 选择指示器
                  if (widget.poll.multiSelect)
                    Icon(
                      isSelected
                          ? Icons.check_box
                          : Icons.check_box_outline_blank,
                      size: 18,
                      color: isSelected ? Colors.green : Colors.grey,
                    )
                  else
                    Icon(
                      isSelected
                          ? Icons.radio_button_checked
                          : Icons.radio_button_unchecked,
                      size: 18,
                      color: isSelected ? Colors.green : Colors.grey,
                    ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      option.optionText,
                      style: const TextStyle(fontSize: 14),
                    ),
                  ),
                  Text(
                    '${formatVoteCount(option.voteCount)} (${option.percentage.toStringAsFixed(1)}%)',
                    style: TextStyle(
                      fontSize: 12,
                      color: Colors.grey.shade600,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildActions() {
    final canVote = _isActive && !_isCreator;
    final isCreator = _isCreator && _isActive;

    return Column(
      children: [
        if (canVote) ...[
          Row(
            children: [
              Expanded(
                child: ElevatedButton(
                  onPressed: _selectedOptions.isNotEmpty ? _submitVote : null,
                  style: ElevatedButton.styleFrom(
                    backgroundColor: Colors.green,
                    foregroundColor: Colors.white,
                    elevation: 0,
                    shape: RoundedRectangleBorder(
                      borderRadius: BorderRadius.circular(8),
                    ),
                  ),
                  child: const Text('投票'),
                ),
              ),
              const SizedBox(width: 8),
              OutlinedButton(
                onPressed: () => widget.onCancelVote?.call(widget.poll.pollId),
                style: OutlinedButton.styleFrom(
                  foregroundColor: Colors.grey.shade700,
                  side: BorderSide(color: Colors.grey.shade300),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
                child: const Text('取消'),
              ),
            ],
          ),
        ],
        if (isCreator) ...[
          // 添加选项
          _AddOptionField(
            onSubmit: (text) =>
                widget.onAddOption?.call(widget.poll.pollId, text),
          ),
          const SizedBox(height: 8),
          Row(
            children: [
              if (widget.poll.status == PollStatus.active)
                Expanded(
                  child: OutlinedButton(
                    onPressed: () => widget.onClose?.call(widget.poll.pollId),
                    style: OutlinedButton.styleFrom(
                      foregroundColor: Colors.orange,
                      side: const BorderSide(color: Colors.orange),
                      shape: RoundedRectangleBorder(
                        borderRadius: BorderRadius.circular(8),
                      ),
                    ),
                    child: const Text('结束投票'),
                  ),
                ),
              const SizedBox(width: 8),
              OutlinedButton(
                onPressed: () => widget.onDelete?.call(widget.poll.pollId),
                style: OutlinedButton.styleFrom(
                  foregroundColor: Colors.red,
                  side: const BorderSide(color: Colors.red),
                  shape: RoundedRectangleBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
                child: const Text('删除'),
              ),
            ],
          ),
        ],
        const SizedBox(height: 4),
        Text(
          '由 ${widget.poll.creatorId} 创建',
          style: TextStyle(fontSize: 11, color: Colors.grey.shade400),
        ),
      ],
    );
  }
}

// ==================== 添加选项输入框 ====================

class _AddOptionField extends StatefulWidget {
  final Function(String) onSubmit;

  const _AddOptionField({required this.onSubmit});

  @override
  State<_AddOptionField> createState() => _AddOptionFieldState();
}

class _AddOptionFieldState extends State<_AddOptionField> {
  final _controller = TextEditingController();
  bool _show = false;

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  void _submit() {
    final text = _controller.text.trim();
    if (text.isEmpty) return;
    widget.onSubmit(text);
    _controller.clear();
    setState(() => _show = false);
  }

  @override
  Widget build(BuildContext context) {
    if (!_show) {
      return TextButton.icon(
        onPressed: () => setState(() => _show = true),
        icon: const Icon(Icons.add, size: 18),
        label: const Text('添加选项'),
        style: TextButton.styleFrom(
          foregroundColor: Colors.green,
        ),
      );
    }
    return Row(
      children: [
        Expanded(
          child: TextField(
            controller: _controller,
            decoration: InputDecoration(
              hintText: '输入新选项...',
              isDense: true,
              contentPadding:
                  const EdgeInsets.symmetric(horizontal: 10, vertical: 8),
              border: OutlineInputBorder(
                borderRadius: BorderRadius.circular(8),
              ),
            ),
            maxLength: 200,
            onSubmitted: (_) => _submit(),
          ),
        ),
        const SizedBox(width: 6),
        IconButton(
          onPressed: _submit,
          icon: const Icon(Icons.check, color: Colors.green),
        ),
        IconButton(
          onPressed: () {
            _controller.clear();
            setState(() => _show = false);
          },
          icon: const Icon(Icons.close, color: Colors.grey),
        ),
      ],
    );
  }
}

// ==================== 创建投票对话框 ====================

class CreatePollDialog extends StatefulWidget {
  final String groupId;
  final String currentUserId;
  final Function(CreatePollRequest request) onSubmit;
  final VoidCallback onCancel;

  const CreatePollDialog({
    super.key,
    required this.groupId,
    required this.currentUserId,
    required this.onSubmit,
    required this.onCancel,
  });

  @override
  State<CreatePollDialog> createState() => _CreatePollDialogState();
}

class _CreatePollDialogState extends State<CreatePollDialog> {
  final _questionController = TextEditingController();
  final List<TextEditingController> _optionControllers = [
    TextEditingController(),
    TextEditingController(),
  ];
  bool _anonymous = false;
  bool _multiSelect = false;
  int? _deadlineMinutes;

  @override
  void dispose() {
    _questionController.dispose();
    for (var c in _optionControllers) {
      c.dispose();
    }
    super.dispose();
  }

  void _addOption() {
    if (_optionControllers.length >= 10) return;
    setState(() {
      _optionControllers.add(TextEditingController());
    });
  }

  void _removeOption(int index) {
    if (_optionControllers.length <= 2) return;
    setState(() {
      _optionControllers[index].dispose();
      _optionControllers.removeAt(index);
    });
  }

  void _submit() {
    final question = _questionController.text.trim();
    final options = _optionControllers
        .map((c) => c.text.trim())
        .where((t) => t.isNotEmpty)
        .toList();

    if (question.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('请输入投票问题')),
      );
      return;
    }
    if (options.length < 2) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('至少需要2个选项')),
      );
      return;
    }

    final request = CreatePollRequest(
      creatorId: widget.currentUserId,
      groupId: widget.groupId,
      question: question,
      optionTexts: options,
      anonymous: _anonymous,
      multiSelect: _multiSelect,
      deadline: _deadlineMinutes != null
          ? DateTime.now().add(Duration(minutes: _deadlineMinutes!))
          : null,
    );

    widget.onSubmit(request);
  }

  @override
  Widget build(BuildContext context) {
    return Dialog(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: SingleChildScrollView(
        child: Padding(
          padding: const EdgeInsets.all(20),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text(
                '创建投票',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              const SizedBox(height: 16),

              // 问题
              TextField(
                controller: _questionController,
                decoration: InputDecoration(
                  labelText: '投票问题',
                  hintText: '例如：周末去哪里聚餐？',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                ),
                maxLength: 500,
                maxLines: 2,
              ),
              const SizedBox(height: 12),

              // 选项
              const Text('投票选项（2-10个）',
                  style: TextStyle(fontSize: 13, fontWeight: FontWeight.w500)),
              const SizedBox(height: 6),
              ..._optionControllers.asMap().entries.map((entry) {
                final index = entry.key;
                final controller = entry.value;
                return Padding(
                  padding: const EdgeInsets.only(bottom: 6),
                  child: Row(
                    children: [
                      Expanded(
                        child: TextField(
                          controller: controller,
                          decoration: InputDecoration(
                            hintText: '选项 ${index + 1}',
                            isDense: true,
                            border: OutlineInputBorder(
                              borderRadius: BorderRadius.circular(8),
                            ),
                          ),
                          maxLength: 200,
                        ),
                      ),
                      if (_optionControllers.length > 2)
                        IconButton(
                          onPressed: () => _removeOption(index),
                          icon: const Icon(Icons.close, color: Colors.red),
                          iconSize: 20,
                        ),
                    ],
                  ),
                );
              }),
              if (_optionControllers.length < 10)
                TextButton.icon(
                  onPressed: _addOption,
                  icon: const Icon(Icons.add, size: 18),
                  label: const Text('添加选项'),
                ),
              const SizedBox(height: 12),

              // 设置
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: Colors.grey.shade100,
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Column(
                  children: [
                    CheckboxListTile(
                      title: const Text('匿名投票', style: TextStyle(fontSize: 14)),
                      value: _anonymous,
                      onChanged: (v) => setState(() => _anonymous = v ?? false),
                      dense: true,
                      contentPadding: EdgeInsets.zero,
                    ),
                    CheckboxListTile(
                      title: const Text('多选', style: TextStyle(fontSize: 14)),
                      value: _multiSelect,
                      onChanged: (v) => setState(() => _multiSelect = v ?? false),
                      dense: true,
                      contentPadding: EdgeInsets.zero,
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 12),

              // 截止时间
              DropdownButtonFormField<int?>(
                decoration: InputDecoration(
                  labelText: '截止时间（可选）',
                  border: OutlineInputBorder(
                    borderRadius: BorderRadius.circular(8),
                  ),
                  isDense: true,
                ),
                value: _deadlineMinutes,
                items: const [
                  DropdownMenuItem(value: null, child: Text('无截止')),
                  DropdownMenuItem(value: 60, child: Text('1小时后')),
                  DropdownMenuItem(value: 360, child: Text('6小时后')),
                  DropdownMenuItem(value: 1440, child: Text('24小时后')),
                  DropdownMenuItem(value: 10080, child: Text('7天后')),
                ],
                onChanged: (v) => setState(() => _deadlineMinutes = v),
              ),
              const SizedBox(height: 20),

              // 按钮
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  TextButton(
                    onPressed: widget.onCancel,
                    child: const Text('取消'),
                  ),
                  const SizedBox(width: 8),
                  ElevatedButton(
                    onPressed: _submit,
                    style: ElevatedButton.styleFrom(
                      backgroundColor: Colors.green,
                      foregroundColor: Colors.white,
                    ),
                    child: const Text('创建投票'),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }
}

// ==================== 投票柱状图结果 ====================

class PollResultChart extends StatelessWidget {
  final List<PollOption> options;
  final int totalVotes;

  const PollResultChart({
    super.key,
    required this.options,
    required this.totalVotes,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      children: options.map((opt) {
        return Padding(
          padding: const EdgeInsets.only(bottom: 8),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Expanded(child: Text(opt.optionText)),
                  Text(
                    '${formatVoteCount(opt.voteCount)} (${opt.percentage.toStringAsFixed(1)}%)',
                    style: TextStyle(
                      fontSize: 12,
                      color: Colors.grey.shade600,
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 4),
              ClipRRect(
                borderRadius: BorderRadius.circular(4),
                child: LinearProgressIndicator(
                  value: opt.percentage / 100,
                  backgroundColor: Colors.grey.shade200,
                  valueColor: const AlwaysStoppedAnimation<Color>(Colors.green),
                  minHeight: 8,
                ),
              ),
            ],
          ),
        );
      }).toList(),
    );
  }
}
