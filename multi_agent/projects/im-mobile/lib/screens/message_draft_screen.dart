// Message Draft Screen for Flutter
import 'package:flutter/material.dart';
import '../models/message_draft.dart';
import '../services/message_draft_service.dart';

class MessageDraftScreen extends StatefulWidget {
  final int userId;

  MessageDraftScreen({required this.userId});

  @override
  State<MessageDraftScreen> createState() => _MessageDraftScreenState();
}

class _MessageDraftScreenState extends State<MessageDraftScreen> {
  late MessageDraftService _service;
  List<MessageDraft> _drafts = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _service = MessageDraftService(userId: widget.userId);
    _loadDrafts();
  }

  Future<void> _loadDrafts() async {
    setState(() => _isLoading = true);
    try {
      final drafts = await _service.getAllDrafts();
      setState(() {
        _drafts = drafts;
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to load drafts: $e')),
        );
      }
    }
  }

  Future<void> _deleteDraft(String conversationId) async {
    try {
      await _service.deleteDraft(conversationId);
      setState(() {
        _drafts.removeWhere((d) => d.conversationId == conversationId);
      });
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('Failed to delete draft: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('Message Drafts'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadDrafts,
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _drafts.isEmpty
              ? const Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Icon(Icons.drafts, size: 64, color: Colors.grey),
                      SizedBox(height: 16),
                      Text('No drafts', style: TextStyle(color: Colors.grey, fontSize: 18)),
                    ],
                  ),
                )
              : ListView.builder(
                  itemCount: _drafts.length,
                  itemBuilder: (context, index) {
                    final draft = _drafts[index];
                    return Card(
                      margin: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                      child: ListTile(
                        title: Text(
                          draft.conversationId,
                          style: const TextStyle(fontWeight: FontWeight.bold),
                        ),
                        subtitle: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            Text(
                              draft.content,
                              maxLines: 2,
                              overflow: TextOverflow.ellipsis,
                            ),
                            const SizedBox(height: 4),
                            Text(
                              _formatTime(draft.updatedAt),
                              style: const TextStyle(fontSize: 12, color: Colors.grey),
                            ),
                          ],
                        ),
                        trailing: IconButton(
                          icon: const Icon(Icons.delete_outline, color: Colors.red),
                          onPressed: () => _deleteDraft(draft.conversationId),
                        ),
                        isThreeLine: true,
                      ),
                    );
                  },
                ),
    );
  }

  String _formatTime(int timestamp) {
    final date = DateTime.fromMillisecondsSinceEpoch(timestamp);
    return '${date.hour}:${date.minute.toString().padLeft(2, '0')} ${date.day}/${date.month}/${date.year}';
  }
}
