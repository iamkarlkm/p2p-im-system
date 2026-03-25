import 'package:flutter/material.dart';
import '../services/announcement_service.dart';

class AnnouncementPage extends StatefulWidget {
  final int groupId;
  final int currentUserId;
  final bool isAdmin;

  const AnnouncementPage({
    super.key,
    required this.groupId,
    required this.currentUserId,
    this.isAdmin = false,
  });

  @override
  State<AnnouncementPage> createState() => _AnnouncementPageState();
}

class _AnnouncementPageState extends State<AnnouncementPage> {
  final _service = AnnouncementService();
  List<Announcement> _announcements = [];
  bool _loading = false;
  String _filter = 'all';

  @override
  void initState() {
    super.initState();
    _load();
  }

  Future<void> _load() async {
    setState(() => _loading = true);
    try {
      final data = await _service.getGroupAnnouncements(widget.groupId, userId: widget.currentUserId);
      setState(() { _announcements = data; _loading = false; });
    } catch (e) {
      setState(() => _loading = false);
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('加载失败: $e')));
    }
  }

  Future<void> _markRead(int id) async {
    try {
      await _service.markAsRead(id, widget.currentUserId);
      setState(() {
        _announcements = _announcements.map((a) => a.id == id ? Announcement(
          id: a.id, groupId: a.groupId, authorId: a.authorId, authorName: a.authorName,
          title: a.title, content: a.content, pinned: a.pinned, requiredRead: a.requiredRead,
          urgent: a.urgent, type: a.type, publishTime: a.publishTime, expireTime: a.expireTime,
          isRead: true, isConfirmed: a.isConfirmed,
        ) : a).toList();
      });
    } catch (_) {}
  }

  Future<void> _confirm(int id) async {
    await _service.confirmAnnouncement(id, widget.currentUserId);
    setState(() {
      _announcements = _announcements.map((a) => a.id == id ? Announcement(
        id: a.id, groupId: a.groupId, authorId: a.authorId, authorName: a.authorName,
        title: a.title, content: a.content, pinned: a.pinned, requiredRead: a.requiredRead,
        urgent: a.urgent, type: a.type, publishTime: a.publishTime, expireTime: a.expireTime,
        isRead: true, isConfirmed: true,
      ) : a).toList();
    });
  }

  List<Announcement> get _filtered {
    if (_filter == 'unread') return _announcements.where((a) => !a.isRead).toList();
    if (_filter == 'pinned') return _announcements.where((a) => a.pinned).toList();
    return _announcements;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('群公告'),
        actions: [
          if (widget.isAdmin) IconButton(
            icon: const Icon(Icons.add),
            onPressed: () => Navigator.push(context, MaterialPageRoute(
              builder: (_) => _PublishPage(groupId: widget.groupId, authorId: widget.currentUserId, onPublished: _load),
            )),
          ),
        ],
      ),
      body: Column(
        children: [
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
            child: Row(
              children: ['all', 'unread', 'pinned'].map((f) => Padding(
                padding: const EdgeInsets.only(right: 8),
                child: FilterChip(
                  label: Text({'all': '全部', 'unread': '未读', 'pinned': '置顶'}[f]!),
                  selected: _filter == f,
                  onSelected: (_) => setState(() => _filter = f),
                ),
              )).toList(),
            ),
          ),
          Expanded(
            child: _loading ? const Center(child: CircularProgressIndicator()) :
            _filtered.isEmpty ? const Center(child: Text('暂无公告')) :
            ListView.builder(
              padding: const EdgeInsets.all(12),
              itemCount: _filtered.length,
              itemBuilder: (ctx, i) => _AnnouncementTile(
                ann: _filtered[i],
                onRead: () => _markRead(_filtered[i].id),
                onConfirm: () => _confirm(_filtered[i].id),
                isOwner: _filtered[i].authorId == widget.currentUserId,
              ),
            ),
          ),
        ],
      ),
    );
  }
}

class _AnnouncementTile extends StatelessWidget {
  final Announcement ann;
  final VoidCallback onRead;
  final VoidCallback onConfirm;
  final bool isOwner;

  const _AnnouncementTile({required this.ann, required this.onRead, required this.onConfirm, required this.isOwner});

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      color: ann.urgent ? Colors.red.shade50 : ann.isRead ? null : Colors.blue.shade50,
      child: InkWell(
        onTap: ann.isRead ? null : onRead,
        child: Padding(
          padding: const EdgeInsets.all(12),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  if (ann.pinned) _badge('置顶', Colors.orange),
                  if (ann.urgent) _badge('紧急', Colors.red),
                  if (ann.requiredRead) _badge('必须读', Colors.purple),
                  const Spacer(),
                  Text('${ann.publishTime.month}/${ann.publishTime.day}', style: const TextStyle(fontSize: 12, color: Colors.grey)),
                ],
              ),
              if (ann.title != null) ...[
                const SizedBox(height: 4),
                Text(ann.title!, style: const TextStyle(fontSize: 15, fontWeight: FontWeight.bold)),
              ],
              const SizedBox(height: 4),
              Text(ann.content, maxLines: 3, overflow: TextOverflow.ellipsis, style: const TextStyle(fontSize: 13)),
              if (ann.requiredRead && !ann.isConfirmed) ...[
                const SizedBox(height: 8),
                Align(
                  alignment: Alignment.centerRight,
                  child: ElevatedButton(
                    onPressed: onConfirm,
                    style: ElevatedButton.styleFrom(backgroundColor: Colors.purple),
                    child: const Text('确认阅读', style: TextStyle(color: Colors.white)),
                  ),
                ),
              ],
              if (!ann.isRead) Padding(
                padding: const EdgeInsets.only(top: 4),
                child: Container(width: 8, height: 8, decoration: const BoxDecoration(color: Colors.blue, shape: BoxShape.circle)),
              ),
            ],
          ),
        ),
      ),
    );
  }

  Widget _badge(String label, Color color) => Container(
    margin: const EdgeInsets.only(right: 4),
    padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 1),
    decoration: BoxDecoration(color: color, borderRadius: BorderRadius.circular(3)),
    child: Text(label, style: const TextStyle(fontSize: 10, color: Colors.white)),
  );
}

class _PublishPage extends StatefulWidget {
  final int groupId;
  final int authorId;
  final VoidCallback onPublished;

  const _PublishPage({required this.groupId, required this.authorId, required this.onPublished});

  @override
  State<_PublishPage> createState() => _PublishPageState();
}

class _PublishPageState extends State<_PublishPage> {
  final _titleCtrl = TextEditingController();
  final _contentCtrl = TextEditingController();
  String _type = 'normal';
  bool _pinned = false, _urgent = false, _requiredRead = false;
  bool _loading = false;

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('发布公告')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            TextField(controller: _titleCtrl, decoration: const InputDecoration(labelText: '标题（可选）'), maxLength: 200),
            const SizedBox(height: 8),
            TextField(controller: _contentCtrl, decoration: const InputDecoration(labelText: '内容 *'), maxLines: 6, required: true),
            const SizedBox(height: 8),
            DropdownButtonFormField<String>(
              value: _type,
              decoration: const InputDecoration(labelText: '类型'),
              items: const [
                DropdownMenuItem(value: 'normal', child: Text('普通公告')),
                DropdownMenuItem(value: 'rule', child: Text('群规')),
                DropdownMenuItem(value: 'notice', child: Text('通知')),
                DropdownMenuItem(value: 'event', child: Text('活动')),
              ],
              onChanged: (v) => setState(() => _type = v!),
            ),
            const SizedBox(height: 8),
            CheckboxListTile(title: const Text('置顶'), value: _pinned, onChanged: (v) => setState(() => _pinned = v!)),
            CheckboxListTile(title: const Text('紧急'), value: _urgent, onChanged: (v) => setState(() => _urgent = v!)),
            CheckboxListTile(title: const Text('必须阅读'), value: _requiredRead, onChanged: (v) => setState(() => _requiredRead = v!)),
            const SizedBox(height: 16),
            SizedBox(
              width: double.infinity,
              child: ElevatedButton(
                onPressed: _loading ? null : () async {
                  if (_contentCtrl.text.trim().isEmpty) return;
                  setState(() => _loading = true);
                  try {
                    final svc = AnnouncementService();
                    await svc.publish(groupId: widget.groupId, authorId: widget.authorId,
                      title: _titleCtrl.text.isEmpty ? null : _titleCtrl.text,
                      content: _contentCtrl.text, type: _type,
                      pinned: _pinned, urgent: _urgent, requiredRead: _requiredRead);
                    widget.onPublished();
                    if (mounted) Navigator.pop(context);
                  } catch (e) {
                    if (mounted) ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('发布失败: $e')));
                  } finally {
                    if (mounted) setState(() => _loading = false);
                  }
                },
                child: _loading ? const CircularProgressIndicator() : const Text('发布'),
              ),
            ),
          ],
        ),
      ),
    );
  }
}
