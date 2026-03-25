import 'package:flutter/material.dart';
import '../models/conversation_note.dart';
import '../services/conversation_note_service.dart';

class ConversationNotesScreen extends StatefulWidget {
  final String conversationId;
  final ConversationNoteService service;

  const ConversationNotesScreen({
    super.key,
    required this.conversationId,
    required this.service,
  });

  @override
  State<ConversationNotesScreen> createState() => _ConversationNotesScreenState();
}

class _ConversationNotesScreenState extends State<ConversationNotesScreen> {
  List<Note> _notes = [];
  List<TagInfo> _tags = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    try {
      final results = await Future.wait([
        widget.service.getNotes(widget.conversationId),
        widget.service.getTags(),
      ]);
      setState(() {
        _notes = (results[0] as NotePage).items;
        _tags = results[1] as List<TagInfo>;
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _deleteNote(int noteId) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('删除笔记'),
        content: const Text('确定删除此笔记吗？'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(ctx, false), child: const Text('取消')),
          TextButton(onPressed: () => Navigator.pop(ctx, true), child: const Text('删除')),
        ],
      ),
    );
    if (confirmed == true) {
      await widget.service.deleteNote(noteId);
      setState(() => _notes.removeWhere((n) => n.id == noteId));
    }
  }

  void _showCreateDialog({Note? editingNote}) {
    final contentController = TextEditingController(text: editingNote?.content ?? '');
    final selectedTags = <int>{}..addAll(editingNote?.tags.map((t) => t.id) ?? []);

    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (ctx) => StatefulBuilder(
        builder: (ctx, setSheetState) => Padding(
          padding: EdgeInsets.fromLTRB(
            16, 16, 16, MediaQuery.of(ctx).viewInsets.bottom + 16),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(editingNote != null ? '编辑笔记' : '新建笔记',
                  style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
              const SizedBox(height: 12),
              TextField(
                controller: contentController,
                maxLines: 5,
                decoration: const InputDecoration(
                  hintText: '写下你的笔记...',
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 12),
              Wrap(
                spacing: 8,
                runSpacing: 8,
                children: [
                  ..._tags.map((tag) => FilterChip(
                    label: Text(tag.name),
                    selected: selectedTags.contains(tag.id),
                    selectedColor: Color(int.parse(tag.color.replaceFirst('#', '0xFF'))).withAlpha(50),
                    onSelected: (selected) {
                      setSheetState(() {
                        if (selected) selectedTags.add(tag.id);
                        else selectedTags.remove(tag.id);
                      });
                    },
                  )),
                ],
              ),
              const SizedBox(height: 16),
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  TextButton(
                    onPressed: () => Navigator.pop(ctx),
                    child: const Text('取消'),
                  ),
                  const SizedBox(width: 8),
                  ElevatedButton(
                    onPressed: () async {
                      final content = contentController.text.trim();
                      if (content.isEmpty) return;
                      final req = NoteRequest(
                        id: editingNote?.id,
                        conversationId: widget.conversationId,
                        content: content,
                        tagIds: selectedTags.toList(),
                      );
                      Navigator.pop(ctx);
                      if (editingNote != null) {
                        final updated = await widget.service.updateNote(req);
                        setState(() {
                          final idx = _notes.indexWhere((n) => n.id == updated.id);
                          if (idx >= 0) _notes[idx] = updated;
                        });
                      } else {
                        final created = await widget.service.createNote(req);
                        setState(() => _notes.insert(0, created));
                      }
                    },
                    child: Text(editingNote != null ? '保存' : '创建'),
                  ),
                ],
              ),
            ],
          ),
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('对话笔记'),
        actions: [
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: () => _showCreateDialog(),
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _notes.isEmpty
              ? Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      const Icon(Icons.note_alt_outlined, size: 64, color: Colors.grey),
                      const SizedBox(height: 12),
                      const Text('暂无笔记', style: TextStyle(color: Colors.grey)),
                      const SizedBox(height: 8),
                      TextButton(
                        onPressed: () => _showCreateDialog(),
                        child: const Text('+ 新建笔记'),
                      ),
                    ],
                  ),
                )
              : RefreshIndicator(
                  onRefresh: _loadData,
                  child: ListView.separated(
                    padding: const EdgeInsets.all(12),
                    itemCount: _notes.length,
                    separatorBuilder: (_, __) => const SizedBox(height: 8),
                    itemBuilder: (context, index) {
                      final note = _notes[index];
                      return Card(
                        child: InkWell(
                          onTap: () => _showCreateDialog(editingNote: note),
                          borderRadius: BorderRadius.circular(12),
                          child: Padding(
                            padding: const EdgeInsets.all(12),
                            child: Column(
                              crossAxisAlignment: CrossAxisAlignment.start,
                              children: [
                                if (note.quotedMessageContent != null) ...[
                                  Container(
                                    padding: const EdgeInsets.all(8),
                                    decoration: BoxDecoration(
                                      color: Colors.grey[100],
                                      borderRadius: BorderRadius.circular(6),
                                      border: Border(
                                        left: BorderSide(
                                          color: Theme.of(context).primaryColor,
                                          width: 3,
                                        ),
                                      ),
                                    ),
                                    child: Text(
                                      note.quotedMessageContent!,
                                      style: const TextStyle(fontSize: 13, color: Colors.black87),
                                      maxLines: 2,
                                      overflow: TextOverflow.ellipsis,
                                    ),
                                  ),
                                  const SizedBox(height: 8),
                                ],
                                Text(note.content, style: const TextStyle(fontSize: 14)),
                                const SizedBox(height: 8),
                                Row(
                                  children: [
                                    Expanded(
                                      child: Wrap(
                                        spacing: 4,
                                        children: note.tags.map((t) => Chip(
                                          label: Text(t.name, style: const TextStyle(fontSize: 11)),
                                          padding: EdgeInsets.zero,
                                          materialTapTargetSize: MaterialTapTargetSize.shrinkWrap,
                                          backgroundColor: Color(
                                            int.parse(t.color.replaceFirst('#', '0xFF')),
                                          ).withAlpha(30),
                                        )).toList(),
                                      ),
                                    ),
                                    Text(
                                      '${note.createdAt.month}/${note.createdAt.day} ${note.createdAt.hour}:${note.createdAt.minute.toString().padLeft(2, '0')}',
                                      style: const TextStyle(fontSize: 11, color: Colors.grey),
                                    ),
                                    IconButton(
                                      icon: const Icon(Icons.delete_outline, size: 18),
                                      onPressed: () => _deleteNote(note.id),
                                      padding: EdgeInsets.zero,
                                      constraints: const BoxConstraints(),
                                    ),
                                  ],
                                ),
                              ],
                            ),
                          ),
                        ),
                      );
                    },
                  ),
                ),
    );
  }
}
