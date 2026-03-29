import 'package:flutter/material.dart';
import '../services/self_destruct_service.dart';

/**
 * 阅后即焚UI组件
 */

/// 计时器选择器
class TimerSelector extends StatelessWidget {
  final Function(TimerType, int?) onSelect;
  final String? selectedType;

  const TimerSelector({
    Key? key,
    required this.onSelect,
    this.selectedType,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: const Color(0xFF1E1E1E),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text(
            '阅后即焚',
            style: TextStyle(
              color: Color(0xFFFF6B6B),
              fontSize: 14,
              fontWeight: FontWeight.w600,
            ),
          ),
          const SizedBox(height: 12),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: TimerType.values.map((type) {
              final isSelected = type.value == selectedType;
              return GestureDetector(
                onTap: () => onSelect(type, null),
                child: Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 8,
                  ),
                  decoration: BoxDecoration(
                    color: const Color(0xFF2D2D2D),
                    borderRadius: BorderRadius.circular(8),
                    border: isSelected
                      ? Border.all(color: const Color(0xFFFF6B6B), width: 2)
                      : null,
                  ),
                  child: Row(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Text(type.icon, style: const TextStyle(fontSize: 16)),
                      const SizedBox(width: 4),
                      Text(
                        type.label,
                        style: const TextStyle(
                          color: Colors.white70,
                          fontSize: 12,
                        ),
                      ),
                    ],
                  ),
                ),
              );
            }).toList(),
          ),
        ],
      ),
    );
  }
}

/// 倒计时显示组件
class CountdownDisplay extends StatefulWidget {
  final int messageId;
  final int durationSeconds;
  final VoidCallback? onComplete;

  const CountdownDisplay({
    Key? key,
    required this.messageId,
    required this.durationSeconds,
    this.onComplete,
  }) : super(key: key);

  @override
  State<CountdownDisplay> createState() => _CountdownDisplayState();
}

class _CountdownDisplayState extends State<CountdownDisplay> {
  late int _remaining;
  final _service = SelfDestructService();

  @override
  void initState() {
    super.initState();
    _remaining = widget.durationSeconds;
    _startCountdown();
  }

  void _startCountdown() {
    _service.startLocalCountdown(
      messageId: widget.messageId,
      durationSeconds: widget.durationSeconds,
      onTick: (remaining) {
        if (mounted) {
          setState(() => _remaining = remaining);
        }
      },
      onComplete: () {
        widget.onComplete?.call();
      },
    );
  }

  @override
  void dispose() {
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    final progress = _remaining / widget.durationSeconds;
    
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.black87,
        borderRadius: BorderRadius.circular(8),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          const Text('🔥', style: TextStyle(fontSize: 24)),
          const SizedBox(height: 4),
          Text(
            _service.formatRemainingTime(_remaining),
            style: const TextStyle(
              color: Color(0xFFFF6B6B),
              fontSize: 14,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          LinearProgressIndicator(
            value: progress.clamp(0.0, 1.0),
            backgroundColor: Colors.grey[800],
            valueColor: const AlwaysStoppedAnimation<Color>(Color(0xFFFF6B6B)),
          ),
        ],
      ),
    );
  }
}

/// 销毁状态徽章
class DestroyStatusBadge extends StatelessWidget {
  final DestroyStatus status;
  final int? remainingSeconds;

  const DestroyStatusBadge({
    Key? key,
    required this.status,
    this.remainingSeconds,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    IconData icon;
    Color color;
    String text;

    switch (status) {
      case DestroyStatus.pending:
        icon = Icons.lock;
        color = Colors.grey;
        text = '阅后即焚';
        break;
      case DestroyStatus.counting:
        icon = Icons.local_fire_department;
        color = const Color(0xFFFF6B6B);
        text = remainingSeconds != null 
          ? '${remainingSeconds}秒' 
          : '销毁中';
        break;
      case DestroyStatus.destroyed:
        icon = Icons.delete_forever;
        color = Colors.red;
        text = '已销毁';
        break;
      case DestroyStatus.expired:
        icon = Icons.schedule;
        color = Colors.orange;
        text = '已过期';
        break;
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: color.withOpacity(0.2),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Icon(icon, size: 14, color: color),
          const SizedBox(width: 4),
          Text(
            text,
            style: TextStyle(fontSize: 11, color: color),
          ),
        ],
      ),
    );
  }
}

/// 销毁历史列表项
class DestroyHistoryItem extends StatelessWidget {
  final DestroyRecord record;

  const DestroyHistoryItem({
    Key? key,
    required this.record,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    String reasonText;
    switch (record.reason) {
      case 'TIMER':
        reasonText = '定时销毁';
        break;
      case 'MANUAL':
        reasonText = '手动销毁';
        break;
      case 'EXPIRED':
        reasonText = '过期未读';
        break;
      default:
        reasonText = record.reason;
    }

    return Container(
      margin: const EdgeInsets.only(bottom: 8),
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.grey[100],
        borderRadius: BorderRadius.circular(8),
      ),
      child: Row(
        children: [
          const Text('💥', style: TextStyle(fontSize: 24)),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  reasonText,
                  style: const TextStyle(
                    fontSize: 14,
                    color: Colors.black87,
                  ),
                ),
                Text(
                  _formatDateTime(record.destroyTime),
                  style: const TextStyle(
                    fontSize: 12,
                    color: Colors.grey,
                  ),
                ),
              ],
            ),
          ),
        ],
      ),
    );
  }

  String _formatDateTime(DateTime dt) {
    return '${dt.year}-${dt.month.toString().padLeft(2, '0')}-${dt.day.toString().padLeft(2, '0')} '
           '${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
  }
}
