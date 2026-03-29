import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import 'package:im_mobile/models/scheduled_message_model.dart';
import 'package:im_mobile/stores/scheduled_message_store.dart';
import 'package:im_mobile/widgets/scheduled_message_card.dart';
import 'package:im_mobile/widgets/scheduled_message_bottom_sheet.dart';
import 'package:intl/intl.dart';

class ScheduledMessagePage extends StatefulWidget {
  const ScheduledMessagePage({Key? key}) : super(key: key);

  @override
  _ScheduledMessagePageState createState() => _ScheduledMessagePageState();
}

class _ScheduledMessagePageState extends State<ScheduledMessagePage> {
  final ScheduledMessageStore _store = ScheduledMessageStore();
  final ScrollController _scrollController = ScrollController();

  @override
  void initState() {
    super.initState();
    _store.fetchMessages(reset: true);
    _store.fetchStats();
    
    _scrollController.addListener(() {
      if (_scrollController.position.pixels >=
          _scrollController.position.maxScrollExtent - 200) {
        _store.loadMore();
      }
    });
  }

  @override
  void dispose() {
    _scrollController.dispose();
    super.dispose();
  }

  void _showCreateBottomSheet() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => ScheduledMessageBottomSheet(
        onSubmit: (receiverId, content, scheduledTime) async {
          final success = await _store.createMessage(
            receiverId: receiverId,
            content: content,
            scheduledTime: scheduledTime,
          );
          if (success && mounted) {
            Navigator.pop(context);
            _store.fetchStats();
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('定时消息创建成功')),
            );
          }
        },
      ),
    );
  }

  void _showFilterDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('筛选状态'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              title: const Text('全部'),
              selected: _store.filterStatus == null,
              onTap: () {
                _store.setFilterStatus(null);
                Navigator.pop(context);
              },
            ),
            ...ScheduledMessageStatus.values.map((status) => ListTile(
              title: Text(_getStatusLabel(status)),
              selected: _store.filterStatus == status,
              onTap: () {
                _store.setFilterStatus(status);
                Navigator.pop(context);
              },
            )),
          ],
        ),
      ),
    );
  }

  String _getStatusLabel(ScheduledMessageStatus status) {
    switch (status) {
      case ScheduledMessageStatus.PENDING:
        return '待发送';
      case ScheduledMessageStatus.SENT:
        return '已发送';
      case ScheduledMessageStatus.CANCELLED:
        return '已取消';
      case ScheduledMessageStatus.FAILED:
        return '发送失败';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('定时消息'),
        actions: [
          Observer(
            builder: (_) => _store.pendingCount > 0
                ? Badge(
                    label: Text('${_store.pendingCount}'),
                    child: IconButton(
                      icon: const Icon(Icons.filter_list),
                      onPressed: _showFilterDialog,
                    ),
                  )
                : IconButton(
                    icon: const Icon(Icons.filter_list),
                    onPressed: _showFilterDialog,
                  ),
          ),
        ],
      ),
      body: Observer(
        builder: (_) {
          if (_store.loading && _store.messages.isEmpty) {
            return const Center(child: CircularProgressIndicator());
          }

          if (_store.error != null && _store.messages.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text(_store.error!, style: const TextStyle(color: Colors.red)),
                  ElevatedButton(
                    onPressed: () => _store.fetchMessages(reset: true),
                    child: const Text('重试'),
                  ),
                ],
              ),
            );
          }

          if (_store.messages.isEmpty) {
            return const Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Icon(Icons.schedule, size: 64, color: Colors.grey),
                  SizedBox(height: 16),
                  Text('暂无定时消息', style: TextStyle(color: Colors.grey)),
                ],
              ),
            );
          }

          return RefreshIndicator(
            onRefresh: () => _store.fetchMessages(reset: true),
            child: ListView.builder(
              controller: _scrollController,
              itemCount: _store.messages.length + (_store.loading ? 1 : 0),
              itemBuilder: (context, index) {
                if (index == _store.messages.length) {
                  return const Center(
                    child: Padding(
                      padding: EdgeInsets.all(16),
                      child: CircularProgressIndicator(),
                    ),
                  );
                }

                final message = _store.messages[index];
                return ScheduledMessageCard(
                  message: message,
                  onCancel: message.canCancel
                      ? () async {
                          final success = await _store.cancelMessage(message.id);
                          if (success && mounted) {
                            _store.fetchStats();
                            ScaffoldMessenger.of(context).showSnackBar(
                              const SnackBar(content: Text('已取消')),
                            );
                          }
                        }
                      : null,
                  onDelete: () async {
                    final confirm = await showDialog<bool>(
                      context: context,
                      builder: (context) => AlertDialog(
                        title: const Text('确认删除'),
                        content: const Text('确定要删除这条定时消息吗？'),
                        actions: [
                          TextButton(
                            onPressed: () => Navigator.pop(context, false),
                            child: const Text('取消'),
                          ),
                          TextButton(
                            onPressed: () => Navigator.pop(context, true),
                            child: const Text('删除', style: TextStyle(color: Colors.red)),
                          ),
                        ],
                      ),
                    );
                    
                    if (confirm == true) {
                      final success = await _store.deleteMessage(message.id);
                      if (success && mounted) {
                        _store.fetchStats();
                        ScaffoldMessenger.of(context).showSnackBar(
                          const SnackBar(content: Text('已删除')),
                        );
                      }
                    }
                  },
                );
              },
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: _showCreateBottomSheet,
        child: const Icon(Icons.add),
      ),
    );
  }
}
