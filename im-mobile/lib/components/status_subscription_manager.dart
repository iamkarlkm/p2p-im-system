import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../services/user_status_service.dart';

class StatusSubscriptionManager extends StatefulWidget {
  final VoidCallback? onSubscriptionsChanged;

  const StatusSubscriptionManager({
    super.key,
    this.onSubscriptionsChanged,
  });

  @override
  State<StatusSubscriptionManager> createState() => _StatusSubscriptionManagerState();
}

class _StatusSubscriptionManagerState extends State<StatusSubscriptionManager> {
  final TextEditingController _searchController = TextEditingController();
  String _searchQuery = '';

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<UserStatusService>(
      builder: (context, service, child) {
        return Column(
          children: [
            _buildSearchBar(),
            Expanded(
              child: _searchQuery.isEmpty
                  ? _buildSubscriptionList(service)
                  : _buildSearchResults(service),
            ),
          ],
        );
      },
    );
  }

  Widget _buildSearchBar() {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: TextField(
        controller: _searchController,
        decoration: InputDecoration(
          hintText: '搜索用户ID...',
          prefixIcon: const Icon(Icons.search),
          suffixIcon: _searchQuery.isNotEmpty
              ? IconButton(
                  icon: const Icon(Icons.clear),
                  onPressed: () {
                    _searchController.clear();
                    setState(() => _searchQuery = '');
                  },
                )
              : null,
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(12),
          ),
        ),
        onChanged: (value) => setState(() => _searchQuery = value),
      ),
    );
  }

  Widget _buildSubscriptionList(UserStatusService service) {
    final subscriptions = service.friendStatuses.keys.toList();

    if (subscriptions.isEmpty) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.notifications_off_outlined, size: 64, color: Colors.grey),
            SizedBox(height: 16),
            Text(
              '暂无状态订阅',
              style: TextStyle(fontSize: 16, color: Colors.grey),
            ),
            SizedBox(height: 8),
            Text(
              '搜索用户ID添加订阅',
              style: TextStyle(fontSize: 14, color: Colors.grey),
            ),
          ],
        ),
      );
    }

    return ListView.builder(
      itemCount: subscriptions.length + 1,
      itemBuilder: (context, index) {
        if (index == 0) {
          return Padding(
            padding: const EdgeInsets.fromLTRB(16, 8, 16, 8),
            child: Text(
              '已订阅 (${subscriptions.length})',
              style: TextStyle(
                fontSize: 14,
                fontWeight: FontWeight.bold,
                color: Colors.grey.shade700,
              ),
            ),
          );
        }

        final userId = subscriptions[index - 1];
        final status = service.friendStatuses[userId];

        return Card(
          margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 4),
          child: ListTile(
            leading: CircleAvatar(
              backgroundColor: status?.statusColor ?? Colors.grey,
              child: const Icon(Icons.person, color: Colors.white),
            ),
            title: Text(
              '用户 ${userId.substring(0, userId.length > 8 ? 8 : userId.length)}',
              style: const TextStyle(fontWeight: FontWeight.w500),
            ),
            subtitle: status != null
                ? Text(status.displayStatus)
                : const Text('未知状态'),
            trailing: IconButton(
              icon: const Icon(Icons.delete_outline, color: Colors.red),
              onPressed: () => _unsubscribe(userId),
            ),
          ),
        );
      },
    );
  }

  Widget _buildSearchResults(UserStatusService service) {
    // 这里应该实现搜索逻辑，简化版直接显示添加按钮
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Card(
            child: Padding(
              padding: const EdgeInsets.all(16),
              child: Column(
                children: [
                  const Icon(Icons.person_add, size: 48, color: Colors.blue),
                  const SizedBox(height: 16),
                  Text(
                    '添加订阅: $_searchQuery',
                    style: const TextStyle(fontSize: 16),
                  ),
                  const SizedBox(height: 8),
                  Text(
                    '订阅后你将实时收到该用户的状态更新',
                    style: TextStyle(fontSize: 14, color: Colors.grey.shade600),
                    textAlign: TextAlign.center,
                  ),
                  const SizedBox(height: 16),
                  ElevatedButton.icon(
                    onPressed: () => _subscribe(_searchQuery.trim()),
                    icon: const Icon(Icons.add),
                    label: const Text('添加订阅'),
                  ),
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Future<void> _subscribe(String userId) async {
    if (userId.isEmpty) return;

    final service = context.read<UserStatusService>();
    final success = await service.subscribeToStatus(userId);

    if (mounted) {
      if (success) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('已订阅用户 $userId')),
        );
        _searchController.clear();
        setState(() => _searchQuery = '');
        widget.onSubscriptionsChanged?.call();
      } else {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(
            content: Text('订阅失败，请检查用户ID'),
            backgroundColor: Colors.red,
          ),
        );
      }
    }
  }

  Future<void> _unsubscribe(String userId) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('取消订阅'),
        content: Text('确定要取消对用户 $userId 的状态订阅吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('确定', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      final service = context.read<UserStatusService>();
      final success = await service.unsubscribeFromStatus(userId);

      if (mounted) {
        if (success) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('已取消订阅')),
          );
          widget.onSubscriptionsChanged?.call();
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(
              content: Text('操作失败'),
              backgroundColor: Colors.red,
            ),
          );
        }
      }
    }
  }
}

class StatusSubscriptionButton extends StatelessWidget {
  final String userId;
  final bool isSubscribed;
  final VoidCallback? onSubscriptionChanged;

  const StatusSubscriptionButton({
    super.key,
    required this.userId,
    required this.isSubscribed,
    this.onSubscriptionChanged,
  });

  @override
  Widget build(BuildContext context) {
    return IconButton(
      icon: Icon(
        isSubscribed ? Icons.notifications : Icons.notifications_outlined,
        color: isSubscribed ? Colors.blue : null,
      ),
      onPressed: () => _toggleSubscription(context),
    );
  }

  Future<void> _toggleSubscription(BuildContext context) async {
    final service = context.read<UserStatusService>();
    
    if (isSubscribed) {
      final success = await service.unsubscribeFromStatus(userId);
      if (success) {
        onSubscriptionChanged?.call();
        _showSnackBar(context, '已取消状态订阅');
      }
    } else {
      final success = await service.subscribeToStatus(userId);
      if (success) {
        onSubscriptionChanged?.call();
        _showSnackBar(context, '已订阅状态更新');
      }
    }
  }

  void _showSnackBar(BuildContext context, String message) {
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text(message), duration: const Duration(seconds: 1)),
    );
  }
}

class BatchSubscriptionDialog extends StatefulWidget {
  final List<String> userIds;

  const BatchSubscriptionDialog({
    super.key,
    required this.userIds,
  });

  @override
  State<BatchSubscriptionDialog> createState() => _BatchSubscriptionDialogState();
}

class _BatchSubscriptionDialogState extends State<BatchSubscriptionDialog> {
  final Set<String> _selectedUsers = {};
  bool _isProcessing = false;
  int _successCount = 0;

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('批量订阅状态'),
      content: SizedBox(
        width: double.maxFinite,
        height: 300,
        child: _isProcessing
            ? _buildProgressView()
            : _buildUserList(),
      ),
      actions: _isProcessing
          ? null
          : [
              TextButton(
                onPressed: () => Navigator.pop(context),
                child: const Text('取消'),
              ),
              TextButton(
                onPressed: _selectedUsers.isEmpty ? null : _subscribeSelected,
                child: Text('订阅 (${_selectedUsers.length})'),
              ),
            ],
    );
  }

  Widget _buildUserList() {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.only(bottom: 8),
          child: Row(
            children: [
              TextButton(
                onPressed: () => setState(() => _selectedUsers.addAll(widget.userIds)),
                child: const Text('全选'),
              ),
              TextButton(
                onPressed: () => setState(() => _selectedUsers.clear()),
                child: const Text('清空'),
              ),
            ],
          ),
        ),
        Expanded(
          child: ListView.builder(
            itemCount: widget.userIds.length,
            itemBuilder: (context, index) {
              final userId = widget.userIds[index];
              final isSelected = _selectedUsers.contains(userId);

              return CheckboxListTile(
                value: isSelected,
                onChanged: (value) {
                  setState(() {
                    if (value == true) {
                      _selectedUsers.add(userId);
                    } else {
                      _selectedUsers.remove(userId);
                    }
                  });
                },
                title: Text('用户 ${userId.substring(0, userId.length > 8 ? 8 : userId.length)}'),
              );
            },
          ),
        ),
      ],
    );
  }

  Widget _buildProgressView() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const CircularProgressIndicator(),
          const SizedBox(height: 16),
          Text('正在订阅... $_successCount/${_selectedUsers.length}'),
        ],
      ),
    );
  }

  Future<void> _subscribeSelected() async {
    setState(() => _isProcessing = true);

    final service = context.read<UserStatusService>();
    _successCount = 0;

    for (final userId in _selectedUsers) {
      final success = await service.subscribeToStatus(userId);
      if (success) {
        setState(() => _successCount++);
      }
    }

    if (mounted) {
      Navigator.pop(context, _successCount);
    }
  }
}
