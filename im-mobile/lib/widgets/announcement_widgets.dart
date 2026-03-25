import 'package:flutter/material.dart';
import '../models/announcement.dart';
import '../services/announcement_service.dart';

/**
 * 群公告UI组件 - Flutter移动端
 */

// ============ 公告卡片组件 ============

class AnnouncementCard extends StatelessWidget {
    final Announcement announcement;
    final VoidCallback? onTap;
    final VoidCallback? onConfirm;
    final VoidCallback? onEdit;
    final VoidCallback? onDelete;
    final String currentUserId;

    const AnnouncementCard({
        super.key,
        required this.announcement,
        required this.currentUserId,
        this.onTap,
        this.onConfirm,
        this.onEdit,
        this.onDelete,
    });

    @override
    Widget build(BuildContext context) {
        return Card(
            margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
            elevation: 1,
            shape: RoundedRectangleBorder(
                borderRadius: BorderRadius.circular(10),
                side: BorderSide(
                    color: _getBorderColor(),
                    width: 2,
                ),
            ),
            child: InkWell(
                onTap: onTap,
                borderRadius: BorderRadius.circular(10),
                child: Padding(
                    padding: const EdgeInsets.all(14),
                    child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                            _buildHeader(context),
                            const SizedBox(height: 8),
                            Text(
                                announcement.title,
                                style: const TextStyle(
                                    fontSize: 16,
                                    fontWeight: FontWeight.bold,
                                ),
                                maxLines: 2,
                                overflow: TextOverflow.ellipsis,
                            ),
                            const SizedBox(height: 8),
                            Text(
                                announcement.content,
                                style: TextStyle(
                                    fontSize: 14,
                                    color: Colors.grey[700],
                                ),
                                maxLines: 3,
                                overflow: TextOverflow.ellipsis,
                            ),
                            const SizedBox(height: 10),
                            _buildFooter(context),
                        ],
                    ),
                ),
            ),
        );
    }

    Color _getBorderColor() {
        if (announcement.pinned) return Colors.orange;
        if (announcement.type == AnnouncementType.important) return Colors.red;
        return Colors.transparent;
    }

    Widget _buildHeader(BuildContext context) {
        return Row(
            children: [
                _buildTypeTag(),
                const SizedBox(width: 8),
                if (announcement.pinned)
                    Container(
                        padding: const EdgeInsets.symmetric(
                            horizontal: 6, vertical: 2),
                        decoration: BoxDecoration(
                            color: Colors.orange[50],
                            borderRadius: BorderRadius.circular(4),
                        ),
                        child: const Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                                Icon(Icons.push_pin,
                                    size: 12, color: Colors.orange),
                                SizedBox(width: 2),
                                Text('置顶',
                                    style: TextStyle(
                                        fontSize: 11,
                                        color: Colors.orange)),
                            ],
                        ),
                    ),
                const Spacer(),
                Text(
                    _formatTime(announcement.createdAt),
                    style: TextStyle(fontSize: 12, color: Colors.grey[500]),
                ),
                if (announcement.authorId == currentUserId) ...[
                    const SizedBox(width: 8),
                    PopupMenuButton<String>(
                        icon: Icon(Icons.more_vert, color: Colors.grey[400], size: 20),
                        onSelected: (value) {
                            if (value == 'edit') onEdit?.call();
                            if (value == 'delete') onDelete?.call();
                        },
                        itemBuilder: (context) => [
                            const PopupMenuItem(value: 'edit', child: Text('编辑')),
                            const PopupMenuItem(
                                value: 'delete',
                                child: Text('删除', style: TextStyle(color: Colors.red)),
                            ),
                        ],
                    ),
                ],
            ],
        );
    }

    Widget _buildTypeTag() {
        Color bgColor;
        Color textColor;
        String label = announcement.typeLabel;

        switch (announcement.type) {
            case AnnouncementType.important:
                bgColor = Colors.red[50]!;
                textColor = Colors.red;
                break;
            case AnnouncementType.pinned:
                bgColor = Colors.orange[50]!;
                textColor = Colors.orange;
                break;
            default:
                bgColor = Colors.blue[50]!;
                textColor = Colors.blue;
        }

        return Container(
            padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
            decoration: BoxDecoration(
                color: bgColor,
                borderRadius: BorderRadius.circular(4),
            ),
            child: Text(label,
                style: TextStyle(fontSize: 11, color: textColor)),
        );
    }

    Widget _buildFooter(BuildContext context) {
        final hasConfirmed = announcement.hasConfirmed(currentUserId);

        return Row(
            children: [
                Text(
                    announcement.authorName,
                    style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                ),
                const Spacer(),
                Icon(Icons.visibility, size: 14, color: Colors.grey[400]),
                const SizedBox(width: 2),
                Text(
                    '${announcement.viewCount}',
                    style: TextStyle(fontSize: 12, color: Colors.grey[500]),
                ),
                const SizedBox(width: 12),
                GestureDetector(
                    onTap: onConfirm,
                    child: Row(
                        children: [
                            Icon(
                                hasConfirmed
                                    ? Icons.check_circle
                                    : Icons.check_circle_outline,
                                size: 14,
                                color: hasConfirmed ? Colors.green : Colors.grey[400],
                            ),
                            const SizedBox(width: 2),
                            Text(
                                '${announcement.confirmCount}',
                                style: TextStyle(
                                    fontSize: 12,
                                    color: hasConfirmed ? Colors.green : Colors.grey[500],
                                ),
                            ),
                        ],
                    ),
                ),
            ],
        );
    }

    String _formatTime(DateTime dateTime) {
        final now = DateTime.now();
        final diff = now.difference(dateTime);
        if (diff.inMinutes < 1) return '刚刚';
        if (diff.inHours < 1) return '${diff.inMinutes}分钟前';
        if (diff.inDays < 1) return '${diff.inHours}小时前';
        if (diff.inDays < 7) return '${diff.inDays}天前';
        return '${dateTime.month}/${dateTime.day}';
    }
}

// ============ 创建公告对话框 ============

class CreateAnnouncementDialog extends StatefulWidget {
    final Function(String title, String content, String type) onSubmit;

    const CreateAnnouncementDialog({super.key, required this.onSubmit});

    @override
    State<CreateAnnouncementDialog> createState() => _CreateAnnouncementDialogState();
}

class _CreateAnnouncementDialogState extends State<CreateAnnouncementDialog> {
    final _titleController = TextEditingController();
    final _contentController = TextEditingController();
    String _selectedType = 'NORMAL';

    @override
    Widget build(BuildContext context) {
        return Dialog(
            shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
            child: Padding(
                padding: const EdgeInsets.all(20),
                child: SingleChildScrollView(
                    child: Column(
                        mainAxisSize: MainAxisSize.min,
                        crossAxisAlignment: CrossAxisAlignment.stretch,
                        children: [
                            const Text('发布群公告',
                                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                            const SizedBox(height: 16),
                            TextField(
                                controller: _titleController,
                                decoration: const InputDecoration(
                                    labelText: '标题',
                                    border: OutlineInputBorder(),
                                ),
                                maxLength: 200,
                            ),
                            const SizedBox(height: 12),
                            DropdownButtonFormField<String>(
                                value: _selectedType,
                                decoration: const InputDecoration(
                                    labelText: '类型',
                                    border: OutlineInputBorder(),
                                ),
                                items: const [
                                    DropdownMenuItem(value: 'NORMAL', child: Text('普通公告')),
                                    DropdownMenuItem(value: 'IMPORTANT', child: Text('重要公告')),
                                ],
                                onChanged: (v) => setState(() => _selectedType = v!),
                            ),
                            const SizedBox(height: 12),
                            TextField(
                                controller: _contentController,
                                decoration: const InputDecoration(
                                    labelText: '内容 (支持Markdown)',
                                    border: OutlineInputBorder(),
                                    hintText: '支持 # 标题、**粗体**、*斜体*、`代码`',
                                ),
                                maxLines: 6,
                                maxLength: 50000,
                            ),
                            const SizedBox(height: 16),
                            Row(
                                mainAxisAlignment: MainAxisAlignment.end,
                                children: [
                                    TextButton(
                                        onPressed: () => Navigator.pop(context),
                                        child: const Text('取消'),
                                    ),
                                    const SizedBox(width: 12),
                                    ElevatedButton(
                                        onPressed: _submit,
                                        child: const Text('发布'),
                                    ),
                                ],
                            ),
                        ],
                    ),
                ),
            ),
        );
    }

    void _submit() {
        final title = _titleController.text.trim();
        final content = _contentController.text.trim();
        if (title.isEmpty || content.isEmpty) {
            ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('请填写标题和内容')));
            return;
        }
        widget.onSubmit(title, content, _selectedType);
        Navigator.pop(context);
    }
}

// ============ 公告列表页面 ============

class AnnouncementListPage extends StatefulWidget {
    final String groupId;
    final String currentUserId;
    final AnnouncementService service;

    const AnnouncementListPage({
        super.key,
        required this.groupId,
        required this.currentUserId,
        required this.service,
    });

    @override
    State<AnnouncementListPage> createState() => _AnnouncementListPageState();
}

class _AnnouncementListPageState extends State<AnnouncementListPage> {
    List<Announcement> _announcements = [];
    bool _loading = false;
    int _currentPage = 1;
    bool _hasMore = true;

    @override
    void initState() {
        super.initState();
        _loadAnnouncements();
    }

    Future<void> _loadAnnouncements() async {
        if (_loading) return;
        setState(() => _loading = true);
        try {
            final data = await widget.service.getAnnouncementsPaged(
                widget.groupId,
                page: _currentPage,
            );
            setState(() {
                if (_currentPage == 1) {
                    _announcements = data.announcements;
                } else {
                    _announcements.addAll(data.announcements);
                }
                _hasMore = _currentPage < data.totalPages;
                _loading = false;
            });
        } catch (e) {
            setState(() => _loading = false);
            if (mounted) {
                ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('加载失败: $e')));
            }
        }
    }

    void _showCreateDialog() {
        showDialog(
            context: context,
            builder: (context) => CreateAnnouncementDialog(
                onSubmit: (title, content, type) async {
                    try {
                        final announcement =
                            await widget.service.createAnnouncement(
                                CreateAnnouncementRequest(
                                    groupId: widget.groupId,
                                    authorId: widget.currentUserId,
                                    authorName: '我',
                                    title: title,
                                    content: content,
                                    type: type,
                                ),
                            );
                        setState(() {
                            _announcements.insert(0, announcement);
                        });
                    } catch (e) {
                        if (mounted) {
                            ScaffoldMessenger.of(context).showSnackBar(
                                SnackBar(content: Text('创建失败: $e')));
                        }
                    }
                },
            ),
        );
    }

    @override
    Widget build(BuildContext context) {
        return Scaffold(
            appBar: AppBar(
                title: const Text('群公告'),
                actions: [
                    IconButton(
                        icon: const Icon(Icons.add),
                        onPressed: _showCreateDialog,
                    ),
                ],
            ),
            body: RefreshIndicator(
                onRefresh: () async {
                    _currentPage = 1;
                    await _loadAnnouncements();
                },
                child: _announcements.isEmpty && !_loading
                    ? const Center(child: Text('暂无公告'))
                    : ListView.builder(
                        itemCount: _announcements.length + (_hasMore ? 1 : 0),
                        itemBuilder: (context, index) {
                            if (index == _announcements.length) {
                                if (_loading) {
                                    return const Padding(
                                        padding: EdgeInsets.all(16),
                                        child: Center(
                                            child: CircularProgressIndicator(),
                                        ),
                                    );
                                }
                                return TextButton(
                                    onPressed: () {
                                        _currentPage++;
                                        _loadAnnouncements();
                                    },
                                    child: const Text('加载更多'),
                                );
                            }
                            final announcement = _announcements[index];
                            return AnnouncementCard(
                                announcement: announcement,
                                currentUserId: widget.currentUserId,
                                onTap: () => _viewAnnouncement(announcement),
                                onConfirm: () => _confirmAnnouncement(announcement),
                            );
                        },
                    ),
            ),
            floatingActionButton: FloatingActionButton(
                onPressed: _showCreateDialog,
                child: const Icon(Icons.add),
            ),
        );
    }

    void _viewAnnouncement(Announcement announcement) {
        showModalBottomSheet(
            context: context,
            isScrollControlled: true,
            shape: const RoundedRectangleBorder(
                borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
            ),
            builder: (context) => DraggableScrollableSheet(
                initialChildSize: 0.7,
                minChildSize: 0.4,
                maxChildSize: 0.95,
                expand: false,
                builder: (context, scrollController) => SingleChildScrollView(
                    controller: scrollController,
                    padding: const EdgeInsets.all(20),
                    child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                            Center(
                                child: Container(
                                    width: 40,
                                    height: 4,
                                    decoration: BoxDecoration(
                                        color: Colors.grey[300],
                                        borderRadius: BorderRadius.circular(2),
                                    ),
                                ),
                            ),
                            const SizedBox(height: 16),
                            Text(announcement.title,
                                style: const TextStyle(
                                    fontSize: 20, fontWeight: FontWeight.bold)),
                            const SizedBox(height: 8),
                            Row(
                                children: [
                                    Text(announcement.authorName,
                                        style: TextStyle(color: Colors.grey[600])),
                                    const SizedBox(width: 12),
                                    Text(_formatTime(announcement.createdAt),
                                        style: TextStyle(color: Colors.grey[500])),
                                ],
                            ),
                            const SizedBox(height: 16),
                            const Divider(),
                            const SizedBox(height: 16),
                            Text(announcement.content,
                                style: const TextStyle(fontSize: 15, height: 1.6)),
                        ],
                    ),
                ),
            ),
        );
    }

    Future<void> _confirmAnnouncement(Announcement announcement) async {
        try {
            await widget.service.confirmAnnouncement(
                announcementId: announcement.announcementId,
                userId: widget.currentUserId,
            );
            setState(() {
                final idx = _announcements.indexOf(announcement);
                if (idx >= 0) {
                    _announcements[idx] = announcement.copyWith(
                        confirmCount: announcement.confirmCount + 1,
                        confirmedUserIds: {
                            ...announcement.confirmedUserIds,
                            widget.currentUserId
                        },
                    );
                }
            });
        } catch (e) {
            if (mounted) {
                ScaffoldMessenger.of(context).showSnackBar(
                    SnackBar(content: Text('确认失败: $e')));
            }
        }
    }

    String _formatTime(DateTime dateTime) {
        return '${dateTime.year}-${dateTime.month.toString().padLeft(2, '0')}-${dateTime.day.toString().padLeft(2, '0')} ${dateTime.hour.toString().padLeft(2, '0')}:${dateTime.minute.toString().padLeft(2, '0')}';
    }
}
