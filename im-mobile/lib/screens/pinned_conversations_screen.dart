import 'package:flutter/material.dart';
import '../models/conversation_pin.dart';
import '../services/pin_service.dart';

class PinnedConversationsScreen extends StatefulWidget {
  const PinnedConversationsScreen({super.key});

  @override
  State<PinnedConversationsScreen> createState() => _PinnedConversationsScreenState();
}

class _PinnedConversationsScreenState extends State<PinnedConversationsScreen> {
  List<ConversationPin> _pinned = [];
  bool _loading = false;

  @override
  void initState() {
    super.initState();
    _loadPinned();
  }

  Future<void> _loadPinned() async {
    setState(() => _loading = true);
    try {
      _pinned = await PinService.getPinned();
    } finally {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _handleReorder(int oldIndex, int newIndex) async {
    if (oldIndex < newIndex) newIndex -= 1;
    final item = _pinned.removeAt(oldIndex);
    _pinned.insert(newIndex, item);
    setState(() {});
    final ids = _pinned.map((p) => p.conversationId).toList();
    await PinService.reorder(ids);
  }

  Future<void> _handleUnpin(int conversationId) async {
    final result = await PinService.unpin(conversationId);
    if (result.success && result.pinnedConversations != null) {
      setState(() => _pinned = result.pinnedConversations!);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('Pinned Conversations')),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _pinned.isEmpty
              ? const Center(child: Text('No pinned conversations'))
              : ReorderableListView.builder(
                  itemCount: _pinned.length,
                  onReorder: _handleReorder,
                  itemBuilder: (ctx, i) {
                    final conv = _pinned[i];
                    return ListTile(
                      key: ValueKey(conv.conversationId),
                      leading: const Icon(Icons.push_pin, size: 20),
                      title: Text(conv.conversationName),
                      subtitle: conv.pinNote != null ? Text(conv.pinNote!) : null,
                      trailing: IconButton(
                        icon: const Icon(Icons.remove_circle_outline),
                        onPressed: () => _handleUnpin(conv.conversationId),
                      ),
                    );
                  },
                ),
    );
  }
}
