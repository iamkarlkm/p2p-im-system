import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/appointment_models.dart';
import '../providers/appointment_provider.dart';

/// 我的排队列表页面
class MyQueuesScreen extends StatefulWidget {
  const MyQueuesScreen({Key? key}) : super(key: key);

  @override
  State<MyQueuesScreen> createState() => _MyQueuesScreenState();
}

class _MyQueuesScreenState extends State<MyQueuesScreen> {
  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    final provider = context.read<AppointmentProvider>();
    await provider.loadMyQueues();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('我的排队'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadData,
          ),
        ],
      ),
      body: Consumer<AppointmentProvider>(
        builder: (context, provider, child) {
          if (provider.isLoading) {
            return const Center(child: CircularProgressIndicator());
          }

          if (provider.error != null) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text('加载失败: ${provider.error}'),
                  const SizedBox(height: 16),
                  ElevatedButton(
                    onPressed: _loadData,
                    child: const Text('重试'),
                  ),
                ],
              ),
            );
          }

          final activeQueues = provider.queueTickets
              .where((q) =>
                  q.status == QueueStatus.waiting ||
                  q.status == QueueStatus.called)
              .toList();

          final historyQueues = provider.queueTickets
              .where((q) =>
                  q.status != QueueStatus.waiting &&
                  q.status != QueueStatus.called)
              .toList();

          return RefreshIndicator(
            onRefresh: _loadData,
            child: ListView(
              padding: const EdgeInsets.all(16),
              children: [
                if (activeQueues.isNotEmpty) ...[
                  const Text(
                    '当前排队',
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 12),
                  ...activeQueues.map((q) => ActiveQueueCard(ticket: q)),
                  const SizedBox(height: 24),
                ],
                if (historyQueues.isNotEmpty) ...[
                  const Text(
                    '历史记录',
                    style: TextStyle(
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                  const SizedBox(height: 12),
                  ...historyQueues.map((q) => QueueHistoryCard(ticket: q)),
                ],
                if (provider.queueTickets.isEmpty)
                  Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.format_list_numbered_outlined,
                            size: 64, color: Colors.grey[400]),
                        const SizedBox(height: 16),
                        Text(
                          '暂无排队记录',
                          style: TextStyle(
                              color: Colors.grey[600], fontSize: 16),
                        ),
                      ],
                    ),
                  ),
              ],
            ),
          );
        },
      ),
    );
  }
}

/// 活跃排队卡片
class ActiveQueueCard extends StatefulWidget {
  final QueueTicketModel ticket;

  const ActiveQueueCard({Key? key, required this.ticket}) : super(key: key);

  @override
  State<ActiveQueueCard> createState() => _ActiveQueueCardState();
}

class _ActiveQueueCardState extends State<ActiveQueueCard> {
  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 16),
      color: widget.ticket.status == QueueStatus.called
          ? Colors.blue[50]
          : null,
      child: Padding(
        padding: const EdgeInsets.all(20),
        child: Column(
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        widget.ticket.merchantName,
                        style: const TextStyle(
                          fontSize: 18,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        widget.ticket.queueTypeName,
                        style: TextStyle(
                          color: Colors.grey[600],
                          fontSize: 14,
                        ),
                      ),
                    ],
                  ),
                ),
                Container(
                  padding: const EdgeInsets.symmetric(
                    horizontal: 12,
                    vertical: 6,
                  ),
                  decoration: BoxDecoration(
                    color: widget.ticket.statusColor.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(20),
                  ),
                  child: Text(
                    widget.ticket.statusText,
                    style: TextStyle(
                      color: widget.ticket.statusColor,
                      fontSize: 14,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
              ],
            ),
            const Divider(height: 32),
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _buildQueueInfo(
                  '我的号码',
                  widget.ticket.queueNumber.toString().padLeft(3, '0'),
                  Icons.confirmation_number_outlined,
                ),
                Container(
                  height: 50,
                  width: 1,
                  color: Colors.grey[300],
                ),
                _buildQueueInfo(
                  '当前叫号',
                  widget.ticket.currentNumber?.toString().padLeft(3, '0') ??
                      '--',
                  Icons.campaign_outlined,
                ),
                Container(
                  height: 50,
                  width: 1,
                  color: Colors.grey[300],
                ),
                _buildQueueInfo(
                  '预计等待',
                  widget.ticket.formattedWaitTime,
                  Icons.timer_outlined,
                ),
              ],
            ),
            if (widget.ticket.showAheadCount) ...[
              const SizedBox(height: 20),
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: Colors.orange[50],
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Row(
                  mainAxisAlignment: MainAxisAlignment.center,
                  children: [
                    Icon(Icons.people_outline,
                        size: 20, color: Colors.orange[700]),
                    const SizedBox(width: 8),
                    Text(
                      '前面还有 ${widget.ticket.aheadCount} 人',
                      style: TextStyle(
                        fontSize: 16,
                        color: Colors.orange[800],
                        fontWeight: FontWeight.w500,
                      ),
                    ),
                  ],
                ),
              ),
            ],
            if (widget.ticket.status == QueueStatus.called) ...[
              const SizedBox(height: 20),
              Container(
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.green[50],
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.green[300]!),
                ),
                child: Row(
                  children: [
                    Icon(Icons.notifications_active,
                        color: Colors.green[700]),
                    const SizedBox(width: 12),
                    Expanded(
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          Text(
                            '到您了！',
                            style: TextStyle(
                              fontSize: 16,
                              fontWeight: FontWeight.bold,
                              color: Colors.green[800],
                            ),
                          ),
                          Text(
                            '请尽快前往 ${widget.ticket.tableName ?? '收银台'}',
                            style: TextStyle(
                              fontSize: 14,
                              color: Colors.green[700],
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ),
              ),
            ],
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: OutlinedButton(
                    onPressed: () => _showCancelDialog(context),
                    style: OutlinedButton.styleFrom(
                      foregroundColor: Colors.red,
                    ),
                    child: const Text('取消排队'),
                  ),
                ),
                const SizedBox(width: 12),
                if (widget.ticket.status == QueueStatus.called)
                  Expanded(
                    child: ElevatedButton(
                      onPressed: () => _confirmArrive(context),
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.green,
                        foregroundColor: Colors.white,
                      ),
                      child: const Text('确认到达'),
                    ),
                  ),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildQueueInfo(String label, String value, IconData icon) {
    return Column(
      children: [
        Icon(icon, size: 24, color: Colors.grey[500]),
        const SizedBox(height: 8),
        Text(
          value,
          style: const TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          label,
          style: TextStyle(
            fontSize: 12,
            color: Colors.grey[600],
          ),
        ),
      ],
    );
  }

  void _showCancelDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('取消排队'),
        content: const Text('确定要取消当前排队吗？取消后需要重新取号。'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('再等等'),
          ),
          ElevatedButton(
            onPressed: () async {
              Navigator.pop(context);
              final provider = context.read<AppointmentProvider>();
              final success = await provider.cancelQueue(widget.ticket.id);
              if (success && context.mounted) {
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('排队已取消')),
                );
              }
            },
            style: ElevatedButton.styleFrom(
              backgroundColor: Colors.red,
              foregroundColor: Colors.white,
            ),
            child: const Text('确认取消'),
          ),
        ],
      ),
    );
  }

  void _confirmArrive(BuildContext context) async {
    final provider = context.read<AppointmentProvider>();
    final success = await provider.confirmArrive(widget.ticket.id);
    if (success && context.mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('已确认到达')),
      );
    }
  }
}

/// 排队历史卡片
class QueueHistoryCard extends StatelessWidget {
  final QueueTicketModel ticket;

  const QueueHistoryCard({Key? key, required this.ticket}) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 12),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: ticket.statusColor.withOpacity(0.1),
          child: Icon(
            Icons.format_list_numbered,
            color: ticket.statusColor,
          ),
        ),
        title: Text(ticket.merchantName),
        subtitle: Text(
          '${ticket.queueTypeName} · 号码${ticket.queueNumber.toString().padLeft(3, '0')}',
        ),
        trailing: Container(
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
          decoration: BoxDecoration(
            color: Colors.grey[200],
            borderRadius: BorderRadius.circular(4),
          ),
          child: Text(
            ticket.statusText,
            style: TextStyle(
              color: Colors.grey[700],
              fontSize: 12,
            ),
          ),
        ),
      ),
    );
  }
}
