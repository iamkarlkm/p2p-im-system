import 'package:flutter/material.dart';
import '../models/conversation_batch_operation.dart';
import '../services/batch_operation_service.dart';

class BatchOperationScreen extends StatefulWidget {
  const BatchOperationScreen({Key? key}) : super(key: key);

  @override
  State<BatchOperationScreen> createState() => _BatchOperationScreenState();
}

class _BatchOperationScreenState extends State<BatchOperationScreen> {
  List<ConversationSelection> _conversations = [];
  Set<int> _selectedIds = {};
  List<BatchOperationHistory> _history = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _loadConversations();
    _loadHistory();
  }

  void _loadConversations() {
    _conversations = [
      ConversationSelection(
        conversationId: 1,
        name: 'John Doe',
        lastMessage: 'Hello!',
        lastMessageTime: DateTime.now(),
        unreadCount: 2,
      ),
      ConversationSelection(
        conversationId: 2,
        name: 'Jane Smith',
        lastMessage: 'See you later',
        lastMessageTime: DateTime.now().subtract(const Duration(hours: 1)),
        unreadCount: 0,
      ),
      ConversationSelection(
        conversationId: 3,
        name: 'Project Team',
        lastMessage: 'Meeting at 3pm',
        lastMessageTime: DateTime.now().subtract(const Duration(hours: 2)),
        unreadCount: 5,
      ),
      ConversationSelection(
        conversationId: 4,
        name: 'Family Group',
        lastMessage: 'Happy birthday!',
        lastMessageTime: DateTime.now().subtract(const Duration(days: 1)),
        unreadCount: 0,
      ),
    ];
  }

  Future<void> _loadHistory() async {
    // Mock history for demonstration
    setState(() {
      _history = [];
    });
  }

  void _toggleSelection(int conversationId) {
    setState(() {
      if (_selectedIds.contains(conversationId)) {
        _selectedIds.remove(conversationId);
      } else {
        _selectedIds.add(conversationId);
      }
    });
  }

  void _selectAll() {
    setState(() {
      _selectedIds = _conversations.map((c) => c.conversationId).toSet();
    });
  }

  void _clearSelection() {
    setState(() {
      _selectedIds.clear();
    });
  }

  Future<void> _executeBatchOperation(BatchOperationType type) async {
    if (_selectedIds.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('No conversations selected')),
      );
      return;
    }

    setState(() => _isLoading = true);

    try {
      // Simulate API call
      await Future.delayed(const Duration(seconds: 1));
      
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(
            '${type.label} completed for ${_selectedIds.length} conversations',
          ),
          backgroundColor: Colors.green,
        ),
      );
      
      _clearSelection();
      _loadHistory();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text('Operation failed: $e'),
          backgroundColor: Colors.red,
        ),
      );
    } finally {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Batch Operations'),
        actions: [
          if (_selectedIds.isNotEmpty)
            TextButton(
              onPressed: _clearSelection,
              child: const Text('Clear', style: TextStyle(color: Colors.white)),
            ),
        ],
      ),
      body: Column(
        children: [
          if (_selectedIds.isNotEmpty)
            Container(
              padding: const EdgeInsets.all(12),
              color: Colors.blue.shade50,
              child: Row(
                children: [
                  Text(
                    '${_selectedIds.length} selected',
                    style: const TextStyle(fontWeight: FontWeight.bold),
                  ),
                  const Spacer(),
                  TextButton(
                    onPressed: _selectAll,
                    child: const Text('Select All'),
                  ),
                ],
              ),
            ),
          _buildActionBar(),
          Expanded(
            child: _buildConversationsList(),
          ),
          if (_history.isNotEmpty) _buildHistorySection(),
        ],
      ),
    );
  }

  Widget _buildActionBar() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Wrap(
        spacing: 8,
        runSpacing: 8,
        children: [
          _buildActionButton(
            Icons.done_all,
            'Mark Read',
            () => _executeBatchOperation(BatchOperationType.markRead),
          ),
          _buildActionButton(
            Icons.archive,
            'Archive',
            () => _executeBatchOperation(BatchOperationType.archive),
          ),
          _buildActionButton(
            Icons.delete,
            'Delete',
            () => _executeBatchOperation(BatchOperationType.delete),
            isDanger: true,
          ),
          _buildActionButton(
            Icons.push_pin,
            'Pin',
            () => _executeBatchOperation(BatchOperationType.pin),
          ),
          _buildActionButton(
            Icons.notifications_off,
            'Mute',
            () => _executeBatchOperation(BatchOperationType.mute),
          ),
        ],
      ),
    );
  }

  Widget _buildActionButton(
    IconData icon,
    String label,
    VoidCallback onPressed, {
    bool isDanger = false,
  }) {
    return ElevatedButton.icon(
      onPressed: _isLoading ? null : onPressed,
      icon: Icon(icon, size: 18),
      label: Text(label),
      style: ElevatedButton.styleFrom(
        backgroundColor: isDanger ? Colors.red : Colors.blue,
        foregroundColor: Colors.white,
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      ),
    );
  }

  Widget _buildConversationsList() {
    return ListView.builder(
      itemCount: _conversations.length,
      itemBuilder: (context, index) {
        final conversation = _conversations[index];
        final isSelected = _selectedIds.contains(conversation.conversationId);

        return ListTile(
          leading: Checkbox(
            value: isSelected,
            onChanged: (_) => _toggleSelection(conversation.conversationId),
          ),
          title: Text(conversation.name),
          subtitle: Text(
            conversation.lastMessage ?? '',
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
          ),
          trailing: conversation.unreadCount > 0
              ? Container(
                  padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
                  decoration: BoxDecoration(
                    color: Colors.blue,
                    borderRadius: BorderRadius.circular(10),
                  ),
                  child: Text(
                    '${conversation.unreadCount}',
                    style: const TextStyle(
                      color: Colors.white,
                      fontSize: 12,
                    ),
                  ),
                )
              : null,
          onTap: () => _toggleSelection(conversation.conversationId),
        );
      },
    );
  }

  Widget _buildHistorySection() {
    return ExpansionTile(
      title: const Text('Operation History'),
      children: _history.map((operation) {
        return ListTile(
          leading: Icon(
            _getStatusIcon(operation.status),
            color: _getStatusColor(operation.status),
          ),
          title: Text(BatchOperationType.fromValue(operation.operationType).label),
          subtitle: Text(
            '${operation.successCount}/${operation.totalCount} succeeded',
          ),
          trailing: Chip(
            label: Text(
              operation.status,
              style: const TextStyle(fontSize: 12),
            ),
            backgroundColor: _getStatusColor(operation.status).withOpacity(0.2),
          ),
        );
      }).toList(),
    );
  }

  IconData _getStatusIcon(String status) {
    switch (status) {
      case 'completed':
        return Icons.check_circle;
      case 'failed':
        return Icons.error;
      case 'partial':
        return Icons.warning;
      default:
        return Icons.pending;
    }
  }

  Color _getStatusColor(String status) {
    switch (status) {
      case 'completed':
        return Colors.green;
      case 'failed':
        return Colors.red;
      case 'partial':
        return Colors.orange;
      default:
        return Colors.grey;
    }
  }
}
