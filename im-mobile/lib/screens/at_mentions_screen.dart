import 'package:flutter/material.dart';
import 'package:flutter_bloc/flutter_bloc.dart';
import '../models/at_mention.dart';
import '../services/at_mention_service.dart';
import '../bloc/auth/auth_bloc.dart';

class AtMentionsScreen extends StatefulWidget {
  const AtMentionsScreen({super.key});

  @override
  State<AtMentionsScreen> createState() => _AtMentionsScreenState();
}

class _AtMentionsScreenState extends State<AtMentionsScreen> with SingleTickerProviderStateMixin {
  late TabController _tabController;
  List<AtMention> _mentions = [];
  int _unreadCount = 0;
  bool _loading = false;
  int _currentPage = 0;
  bool _hasMore = true;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    _loadData();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  String get _token {
    final authState = context.read<AuthBloc>().state;
    return authState is AuthAuthenticated ? authState.token : '';
  }

  int get _userId {
    final authState = context.read<AuthBloc>().state;
    return authState is AuthAuthenticated ? authState.user.id : 0;
  }

  AtMentionService get _service => AtMentionService(_token);

  Future<void> _loadData() async {
    if (_loading) return;
    setState(() => _loading = true);
    try {
      final results = await Future.wait([
        _service.getMentionList(_userId),
        _service.getUnreadCount(_userId),
      ]);
      if (mounted) {
        setState(() {
          _mentions = results[0] as List<AtMention>;
          _unreadCount = results[1] as int;
          _currentPage = 0;
          _hasMore = _mentions.length >= 20;
          _loading = false;
        });
      }
    } catch (e) {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _loadMore() async {
    if (_loading || !_hasMore) return;
    setState(() => _loading = true);
    try {
      final more = await _service.getMentionList(_userId, page: _currentPage + 1);
      if (mounted) {
        setState(() {
          _mentions.addAll(more);
          _currentPage++;
          _hasMore = more.length >= 20;
          _loading = false;
        });
      }
    } catch (e) {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _markRead(AtMention mention) async {
    if (mention.isRead) return;
    try {
      await _service.markAsRead(_userId, [mention.id]);
      if (mounted) {
        setState(() {
          _mentions = _mentions.map((m) =>
            m.id == mention.id ? m.copyWith(isRead: true) : m
          ).toList();
          _unreadCount = _unreadCount > 0 ? _unreadCount - 1 : 0;
        });
      }
    } catch (e) {
      // ignore
    }
  }

  List<AtMention> get _filteredMentions {
    if (_tabController.index == 1) {
      return _mentions.where((m) => !m.isRead).toList();
    }
    return _mentions;
  }

  String _formatTime(DateTime date) {
    final now = DateTime.now();
    final diff = now.difference(date);
    if (diff.inMinutes < 1) return '刚刚';
    if (diff.inMinutes < 60) return '${diff.inMinutes}分钟前';
    if (diff.inHours < 24) return '${diff.inHours}小时前';
    if (diff.inDays < 7) return '${diff.inDays}天前';
    return '${date.month}/${date.day}';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('消息@提醒'),
        actions: [
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () => _showSettingsDialog(context),
          ),
        ],
        bottom: TabBar(
          controller: _tabController,
          tabs: [
            const Tab(text: '全部'),
            Tab(
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  const Text('未读'),
                  if (_unreadCount > 0) ...[
                    const SizedBox(width: 4),
                    Container(
                      padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                      decoration: BoxDecoration(
                        color: Colors.red,
                        borderRadius: BorderRadius.circular(10),
                      ),
                      child: Text(
                        '$_unreadCount',
                        style: const TextStyle(color: Colors.white, fontSize: 10),
                      ),
                    ),
                  ],
                ],
              ),
            ),
          ],
        ),
      ),
      body: _loading && _mentions.isEmpty
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadData,
              child: _filteredMentions.isEmpty
                  ? ListView(
                      children: [
                        SizedBox(height: MediaQuery.of(context).size.height * 0.3),
                        const Center(
                          child: Column(
                            children: [
                              Icon(Icons.alternate_email, size: 48, color: Colors.grey),
                              SizedBox(height: 16),
                              Text('暂无@提及', style: TextStyle(color: Colors.grey)),
                            ],
                          ),
                        ),
                      ],
                    )
                  : ListView.builder(
                      itemCount: _filteredMentions.length + (_hasMore ? 1 : 0),
                      itemBuilder: (context, index) {
                        if (index == _filteredMentions.length) {
                          return Padding(
                            padding: const EdgeInsets.all(16),
                            child: _loading
                                ? const Center(child: CircularProgressIndicator())
                                : TextButton(
                                    onPressed: _loadMore,
                                    child: const Text('加载更多'),
                                  ),
                          );
                        }
                        final mention = _filteredMentions[index];
                        return _MentionTile(
                          mention: mention,
                          onTap: () {
                            _markRead(mention);
                            if (mention.conversationId != null) {
                              Navigator.of(context).pop(mention.conversationId);
                            }
                          },
                          formatTime: _formatTime,
                        );
                      },
                    ),
            ),
    );
  }

  void _showSettingsDialog(BuildContext context) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (context) => _MentionSettingsSheet(
        userId: _userId,
        service: _service,
      ),
    );
  }
}

class _MentionTile extends StatelessWidget {
  final AtMention mention;
  final VoidCallback onTap;
  final String Function(DateTime) formatTime;

  const _MentionTile({
    required this.mention,
    required this.onTap,
    required this.formatTime,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        decoration: BoxDecoration(
          color: mention.isRead ? Colors.white : Colors.blue.shade50,
          border: Border(
            bottom: BorderSide(color: Colors.grey.shade200),
          ),
        ),
        child: Row(
          children: [
            CircleAvatar(
              radius: 24,
              backgroundColor: mention.isAtAll ? Colors.orange : Colors.blue,
              child: Text(
                mention.isAtAll
                    ? 'ALL'
                    : (mention.senderNickname ?? 'U${mention.senderUserId}').substring(0, 2).toUpperCase(),
                style: const TextStyle(color: Colors.white, fontSize: 12),
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Expanded(
                        child: Text(
                          mention.senderNickname ?? '用户${mention.senderUserId}',
                          style: const TextStyle(fontWeight: FontWeight.w600, fontSize: 14),
                        ),
                      ),
                      Text(
                        formatTime(mention.mentionedAt),
                        style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
                      ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                        decoration: BoxDecoration(
                          color: Colors.blue,
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: Text(
                          mention.isAtAll ? '@所有人' : '@了你',
                          style: const TextStyle(color: Colors.white, fontSize: 10),
                        ),
                      ),
                      const SizedBox(width: 6),
                      Expanded(
                        child: Text(
                          mention.messagePreview,
                          maxLines: 1,
                          overflow: TextOverflow.ellipsis,
                          style: TextStyle(fontSize: 13, color: Colors.grey.shade700),
                        ),
                      ),
                    ],
                  ),
                  if (mention.roomName != null) ...[
                    const SizedBox(height: 2),
                    Row(
                      children: [
                        Icon(Icons.group, size: 12, color: Colors.grey.shade500),
                        const SizedBox(width: 4),
                        Text(
                          mention.roomName!,
                          style: TextStyle(fontSize: 11, color: Colors.grey.shade500),
                        ),
                      ],
                    ),
                  ],
                ],
              ),
            ),
            if (!mention.isRead)
              Container(
                width: 8,
                height: 8,
                decoration: const BoxDecoration(
                  color: Colors.blue,
                  shape: BoxShape.circle,
                ),
              ),
          ],
        ),
      ),
    );
  }
}

class _MentionSettingsSheet extends StatefulWidget {
  final int userId;
  final AtMentionService service;

  const _MentionSettingsSheet({required this.userId, required this.service});

  @override
  State<_MentionSettingsSheet> createState() => _MentionSettingsSheetState();
}

class _MentionSettingsSheetState extends State<_MentionSettingsSheet> {
  AtMentionSettings? _settings;
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _loadSettings();
  }

  Future<void> _loadSettings() async {
    try {
      final settings = await widget.service.getMentionSettings(widget.userId);
      if (mounted) setState(() { _settings = settings; _loading = false; });
    } catch (e) {
      if (mounted) setState(() => _loading = false);
    }
  }

  Future<void> _saveSettings() async {
    if (_settings == null) return;
    try {
      await widget.service.updateMentionSettings(widget.userId, _settings!);
      if (mounted) Navigator.of(context).pop();
    } catch (e) {
      // ignore
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      child: _loading
          ? const Center(child: CircularProgressIndicator())
          : Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text('@提醒设置', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                const SizedBox(height: 16),
                SwitchListTile(
                  title: const Text('开启@提醒'),
                  value: _settings?.enabled ?? true,
                  onChanged: (v) => setState(() => _settings = _settings?.copyWith(enabled: v)),
                ),
                SwitchListTile(
                  title: const Text('仅@所有人时提醒'),
                  value: _settings?.onlyAtAll ?? false,
                  onChanged: (v) => setState(() => _settings = _settings?.copyWith(onlyAtAll: v)),
                ),
                SwitchListTile(
                  title: const Text('允许陌生人@'),
                  value: _settings?.allowStrangerAt ?? true,
                  onChanged: (v) => setState(() => _settings = _settings?.copyWith(allowStrangerAt: v)),
                ),
                const Divider(),
                SwitchListTile(
                  title: const Text('免打扰模式'),
                  value: _settings?.dndEnabled ?? false,
                  onChanged: (v) => setState(() => _settings = _settings?.copyWith(dndEnabled: v)),
                ),
                const SizedBox(height: 16),
                SizedBox(
                  width: double.infinity,
                  child: ElevatedButton(
                    onPressed: _saveSettings,
                    child: const Text('保存'),
                  ),
                ),
                const SizedBox(height: 16),
              ],
            ),
    );
  }
}
