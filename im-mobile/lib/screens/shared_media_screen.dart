import 'package:flutter/material.dart';
import '../models/shared_media.dart';
import '../services/shared_media_service.dart';

class SharedMediaScreen extends StatefulWidget {
  final String conversationId;
  final SharedMediaService mediaService;

  const SharedMediaScreen({
    super.key,
    required this.conversationId,
    required this.mediaService,
  });

  @override
  State<SharedMediaScreen> createState() => _SharedMediaScreenState();
}

class _SharedMediaScreenState extends State<SharedMediaScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;
  List<SharedMedia> _mediaList = [];
  MediaStatistics? _stats;
  bool _isLoading = false;
  String _currentFilter = 'ALL';
  final ScrollController _scrollController = ScrollController();

  final List<Map<String, dynamic>> _tabs = [
    {'type': 'ALL', 'label': '全部', 'icon': '📁'},
    {'type': 'IMAGE', 'label': '图片', 'icon': '🖼️'},
    {'type': 'VIDEO', 'label': '视频', 'icon': '🎬'},
    {'type': 'AUDIO', 'label': '音频', 'icon': '🎵'},
    {'type': 'FILE', 'label': '文件', 'icon': '📎'},
    {'type': 'LINK', 'label': '链接', 'icon': '🔗'},
  ];

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: _tabs.length, vsync: this);
    _tabController.addListener(_onTabChanged);
    _loadData();
  }

  @override
  void dispose() {
    _tabController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  void _onTabChanged() {
    if (!_tabController.indexIsChanging) {
      setState(() {
        _currentFilter = _tabs[_tabController.index]['type'] as String;
      });
      _loadData();
    }
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    try {
      final page = await widget.mediaService.getSharedMedia(
        conversationId: widget.conversationId,
        mediaType: _currentFilter == 'ALL' ? null : _currentFilter,
        page: 0,
        size: 50,
      );
      final stats = await widget.mediaService.getMediaStatistics(
          widget.conversationId);
      setState(() {
        _mediaList = page.items;
        _stats = stats;
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _loadMore() async {
    if (_isLoading) return;
    setState(() => _isLoading = true);
    try {
      final page = await widget.mediaService.getSharedMedia(
        conversationId: widget.conversationId,
        mediaType: _currentFilter == 'ALL' ? null : _currentFilter,
        page: _mediaList.length ~/ 50,
        size: 50,
      );
      setState(() {
        _mediaList.addAll(page.items);
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('共同媒体'),
        bottom: TabBar(
          controller: _tabController,
          isScrollable: true,
          tabs: _tabs.map((tab) {
            return Tab(
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Text('${tab['icon']} '),
                  Text('${tab['label']}'),
                  if (_stats != null) ...[
                    const SizedBox(width: 4),
                    Text(
                      _getCount(tab['type'] as String).toString(),
                      style: const TextStyle(fontSize: 12),
                    ),
                  ],
                ],
              ),
            );
          }).toList(),
        ),
      ),
      body: _isLoading && _mediaList.isEmpty
          ? const Center(child: CircularProgressIndicator())
          : _mediaList.isEmpty
              ? const Center(child: Text('暂无媒体内容'))
              : _buildMediaGrid(),
    );
  }

  int _getCount(String type) {
    if (_stats == null) return 0;
    switch (type) {
      case 'IMAGE': return _stats!.imageCount;
      case 'VIDEO': return _stats!.videoCount;
      case 'AUDIO': return _stats!.audioCount;
      case 'FILE': return _stats!.fileCount;
      case 'LINK': return _stats!.linkCount;
      default:
        return _stats!.imageCount +
            _stats!.videoCount +
            _stats!.audioCount +
            _stats!.fileCount +
            _stats!.linkCount;
    }
  }

  Widget _buildMediaGrid() {
    if (_currentFilter == 'LINK') {
      return _buildLinksList();
    }
    return NotificationListener<ScrollNotification>(
      onNotification: (notification) {
        if (notification is ScrollEndNotification &&
            _scrollController.position.pixels >=
                _scrollController.position.maxScrollExtent - 200) {
          _loadMore();
        }
        return false;
      },
      child: GridView.builder(
        controller: _scrollController,
        padding: const EdgeInsets.all(8),
        gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: 3,
          crossAxisSpacing: 4,
          mainAxisSpacing: 4,
        ),
        itemCount: _mediaList.length,
        itemBuilder: (context, index) {
          final media = _mediaList[index];
          return _buildMediaTile(media);
        },
      ),
    );
  }

  Widget _buildMediaTile(SharedMedia media) {
    return GestureDetector(
      onTap: () => _openMediaDetail(media),
      child: Container(
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(8),
          color: Colors.grey[200],
        ),
        clipBehavior: Clip.antiAlias,
        child: media.mediaType == MediaType.IMAGE
            ? Image.network(
                media.thumbnailUrl ?? media.fileUrl,
                fit: BoxFit.cover,
                errorBuilder: (_, __, ___) => _buildPlaceholder(media),
              )
            : _buildPlaceholder(media),
      ),
    );
  }

  Widget _buildPlaceholder(SharedMedia media) {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Text(
            widget.mediaService.getMediaTypeIcon(media.mediaType),
            style: const TextStyle(fontSize: 28),
          ),
          const SizedBox(height: 4),
          Padding(
            padding: const EdgeInsets.symmetric(horizontal: 4),
            child: Text(
              media.fileName,
              style: const TextStyle(fontSize: 10),
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildLinksList() {
    return FutureBuilder<List<LinkPreview>>(
      future: widget.mediaService.getSharedLinks(widget.conversationId),
      builder: (context, snapshot) {
        if (!snapshot.hasData) {
          return const Center(child: CircularProgressIndicator());
        }
        final links = snapshot.data!;
        if (links.isEmpty) {
          return const Center(child: Text('暂无链接'));
        }
        return ListView.separated(
          padding: const EdgeInsets.all(8),
          itemCount: links.length,
          separatorBuilder: (_, __) => const Divider(height: 1),
          itemBuilder: (context, index) {
            final link = links[index];
            return ListTile(
              leading: link.image != null
                  ? ClipRRect(
                      borderRadius: BorderRadius.circular(4),
                      child: Image.network(link.image!, width: 48, height: 48, fit: BoxFit.cover),
                    )
                  : const Icon(Icons.link),
              title: Text(link.title ?? link.url, maxLines: 1, overflow: TextOverflow.ellipsis),
              subtitle: Text(link.domain ?? '', style: const TextStyle(fontSize: 12)),
              onTap: () {},
            );
          },
        );
      },
    );
  }

  void _openMediaDetail(SharedMedia media) {
    showModalBottomSheet(
      context: context,
      builder: (context) => Container(
        padding: const EdgeInsets.all(16),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(media.fileName, style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            const SizedBox(height: 8),
            Text('类型: ${media.mediaType.name}'),
            if (media.fileSize != null)
              Text('大小: ${widget.mediaService.formatFileSize(media.fileSize!)}'),
            Text('发送时间: ${media.createdAt.toString().substring(0, 16)}'),
            const SizedBox(height: 16),
            Row(
              children: [
                Expanded(
                  child: ElevatedButton.icon(
                    onPressed: () {},
                    icon: const Icon(Icons.open_in_new),
                    label: const Text('查看'),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
