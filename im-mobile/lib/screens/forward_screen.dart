import 'package:flutter/material.dart';
import '../models/message_forward.dart';
import '../models/conversation.dart';
import '../services/forward_service.dart';

class ForwardScreen extends StatefulWidget {
  final List<Message> selectedMessages;
  final List<Conversation> conversations;

  const ForwardScreen({
    super.key,
    required this.selectedMessages,
    required this.conversations,
  });

  @override
  State<ForwardScreen> createState() => _ForwardScreenState();
}

class _ForwardScreenState extends State<ForwardScreen> {
  int? selectedTarget;
  bool isMerged = false;
  bool isForwarding = false;
  String mergedTitle = '';
  String searchQuery = '';

  List<Conversation> get filteredConversations {
    if (searchQuery.isEmpty) return widget.conversations;
    return widget.conversations
        .where((c) => c.name.toLowerCase().contains(searchQuery.toLowerCase()))
        .toList();
  }

  Future<void> _handleForward() async {
    if (selectedTarget == null) return;
    setState(() => isForwarding = true);
    try {
      final result = await ForwardService.forwardMessage(ForwardRequest(
        messageIds: widget.selectedMessages.map((m) => m.id).toList(),
        targetConversationId: selectedTarget!,
        merged: isMerged,
        mergedTitle: isMerged ? mergedTitle : null,
      ));
      if (mounted) {
        if (result.success) {
          Navigator.of(context).pop(result);
        } else {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text(result.message), backgroundColor: Colors.red),
          );
        }
      }
    } finally {
      if (mounted) setState(() => isForwarding = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    final hasMultiple = widget.selectedMessages.length > 1;

    return Scaffold(
      appBar: AppBar(
        title: const Text('Forward Messages'),
        actions: [
          TextButton(
            onPressed: selectedTarget != null && !isForwarding ? _handleForward : null,
            child: isForwarding
                ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2))
                : const Text('Send'),
          ),
        ],
      ),
      body: Column(
        children: [
          if (hasMultiple)
            Padding(
              padding: const EdgeInsets.all(12),
              child: Row(
                children: [
                  Checkbox(
                    value: isMerged,
                    onChanged: (v) => setState(() => isMerged = v ?? false),
                  ),
                  const Expanded(child: Text('Merge into one message')),
                ],
              ),
            ),
          if (isMerged)
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 12),
              child: TextField(
                decoration: const InputDecoration(
                  hintText: 'Title (optional)',
                  border: OutlineInputBorder(),
                  isDense: true,
                ),
                onChanged: (v) => setState(() => mergedTitle = v),
              ),
            ),
          Padding(
            padding: const EdgeInsets.all(12),
            child: TextField(
              decoration: const InputDecoration(
                hintText: 'Search conversations...',
                prefixIcon: Icon(Icons.search),
                border: OutlineInputBorder(),
                isDense: true,
              ),
              onChanged: (v) => setState(() => searchQuery = v),
            ),
          ),
          Expanded(
            child: ListView.builder(
              itemCount: filteredConversations.length,
              itemBuilder: (ctx, i) {
                final conv = filteredConversations[i];
                final isSelected = selectedTarget == conv.id;
                return ListTile(
                  leading: CircleAvatar(
                    backgroundColor: isSelected ? Colors.blue : Colors.grey[300],
                    child: Text(conv.name.isNotEmpty ? conv.name[0].toUpperCase() : '?'),
                  ),
                  title: Text(conv.name),
                  subtitle: Text(conv.type),
                  trailing: isSelected ? const Icon(Icons.check, color: Colors.blue) : null,
                  selected: isSelected,
                  selectedTileColor: Colors.blue.withValues(alpha: 0.1),
                  onTap: () => setState(() => selectedTarget = conv.id),
                );
              },
            ),
          ),
        ],
      ),
    );
  }
}
