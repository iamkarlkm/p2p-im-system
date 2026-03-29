import 'package:flutter/material.dart';
import '../services/read_receipt_service.dart';

/**
 * 消息已读回执UI组件
 */

/// 未读消息数徽章
class UnreadBadge extends StatelessWidget {
  final int count;
  final double size;
  final Color? color;

  const UnreadBadge({
    Key? key,
    required this.count,
    this.size = 20,
    this.color,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    if (count <= 0) {
      return const SizedBox.shrink();
    }

    final displayCount = count > 99 ? '99+' : count.toString();
    
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
      constraints: BoxConstraints(
        minWidth: size,
        minHeight: size,
      ),
      decoration: BoxDecoration(
        color: color ?? const Color(0xFFFF4757),
        borderRadius: BorderRadius.circular(size / 2),
      ),
      child: Text(
        displayCount,
        style: TextStyle(
          color: Colors.white,
          fontSize: size * 0.6,
          fontWeight: FontWeight.bold,
        ),
        textAlign: TextAlign.center,
      ),
    );
  }
}

/// 已读状态徽章
class ReadStatusBadge extends StatelessWidget {
  final int readCount;
  final bool isRead;

  const ReadStatusBadge({
    Key? key,
    required this.readCount,
    this.isRead = false,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    if (readCount == 0 && !isRead) {
      return const SizedBox.shrink();
    }

    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        Icon(
          isRead ? Icons.done_all : Icons.done,
          size: 14,
          color: isRead ? const Color(0xFF4ADE80) : Colors.grey,
        ),
        if (readCount > 0) ...[
          const SizedBox(width: 2),
          Text(
            readCount.toString(),
            style: TextStyle(
              fontSize: 11,
              color: isRead ? const Color(0xFF4ADE80) : Colors.grey,
            ),
          ),
        ],
      ],
    );
  }
}

/// 已读用户列表弹窗
class ReadReceiptsModal extends StatelessWidget {
  final List<ReadReceipt> receipts;
  final ReadReceiptService service;

  const ReadReceiptsModal({
    Key? key,
    required this.receipts,
    required this.service,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          // 头部
          Container(
            padding: const EdgeInsets.all(16),
            decoration: const BoxDecoration(
              border: Border(
                bottom: BorderSide(color: Colors.grey, width: 0.5),
              ),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  '已读 (${receipts.length})',
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                IconButton(
                  icon: const Icon(Icons.close),
                  onPressed: () => Navigator.pop(context),
                ),
              ],
            ),
          ),
          
          // 列表
          Flexible(
            child: ListView.builder(
              shrinkWrap: true,
              itemCount: receipts.length,
              itemBuilder: (context, index) {
                final receipt = receipts[index];
                return ListTile(
                  leading: CircleAvatar(
                    backgroundColor: const Color(0xFF667EEA),
                    child: Text(
                      receipt.userId.toString(),
                      style: const TextStyle(color: Colors.white),
                    ),
                  ),
                  title: Text('用户 ${receipt.userId}'),
                  subtitle: Text(service.formatReadTime(receipt.readAt)),
                  trailing: const Icon(
                    Icons.check,
                    color: Color(0xFF4ADE80),
                    size: 18,
                  ),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}

/// 已读统计卡片
class ReadStatsCard extends StatelessWidget {
  final ReadStatistics statistics;

  const ReadStatsCard({
    Key? key,
    required this.statistics,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.grey[100],
        borderRadius: BorderRadius.circular(12),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          _buildStatItem('已读人数', statistics.totalReads.toString()),
          _buildStatItem(
            '首次已读',
            statistics.firstReadAt != null 
              ? _formatTime(statistics.firstReadAt!)
              : '-',
          ),
          _buildStatItem(
            '最后已读',
            statistics.lastReadAt != null 
              ? _formatTime(statistics.lastReadAt!)
              : '-',
          ),
        ],
      ),
    );
  }

  Widget _buildStatItem(String label, String value) {
    return Column(
      children: [
        Text(
          value,
          style: const TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.bold,
            color: Color(0xFF667EEA),
          ),
        ),
        const SizedBox(height: 4),
        Text(
          label,
          style: const TextStyle(
            fontSize: 12,
            color: Colors.grey,
          ),
        ),
      ],
    );
  }

  String _formatTime(DateTime time) {
    final now = DateTime.now();
    final diff = now.difference(time);
    
    if (diff.inMinutes < 1) return '刚刚';
    if (diff.inMinutes < 60) return '${diff.inMinutes}分钟前';
    if (diff.inHours < 24) return '${diff.inHours}小时前';
    return '${diff.inDays}天前';
  }
}
