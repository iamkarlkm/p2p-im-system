import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/message_search_service.dart';
import '../models/search_result.dart';

class MessageSearchScreen extends StatefulWidget {
  final String? conversationId;
  final String? conversationName;

  const MessageSearchScreen({
    super.key,
    this.conversationId,
    this.conversationName,
  });

  @override
  State<MessageSearchScreen> createState() => _MessageSearchScreenState();
}

class _MessageSearchScreenState extends State<MessageSearchScreen> {
  final TextEditingController _searchController = TextEditingController();
  final FocusNode _focusNode = FocusNode();
  bool _showFilters = false;

  @override
  void initState() {
    super.initState();
    _focusNode.requestFocus();
  }

  @override
  void dispose() {
    _searchController.dispose();
    _focusNode.dispose();
    super.dispose();
  }

  void _performSearch(String query) {
    context.read<MessageSearchService>().searchMessages(
      query: query,
      conversationId: widget.conversationId,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: TextField(
          controller: _searchController,
          focusNode: _focusNode,
          decoration: InputDecoration(
            hintText: widget.conversationName != null
                ? '在 "${widget.conversationName}" 中搜索...'
                : '搜索消息...',
            border: InputBorder.none,
            suffixIcon: _searchController.text.isNotEmpty
                ? IconButton(
                    icon: const Icon(Icons.clear),
                    onPressed: () {
                      _searchController.clear();
                      context.read<MessageSearchService>().clearResults();
                    },
                  )
                : null,
          ),
          onChanged: (value) => setState(() {}),
          onSubmitted: _performSearch,
        ),
        actions: [
          IconButton(
            icon: Icon(_showFilters ? Icons.filter_list_off : Icons.filter_list),
            onPressed: () => setState(() => _showFilters = !_showFilters),
          ),
        ],
      ),
      body: Column(
        children: [
          if (_showFilters) _buildFilterPanel(),
          Expanded(
            child: Consumer<MessageSearchService>(
              builder: (context, service, child) {
                if (service.isSearching) {
                  return const Center(child: CircularProgressIndicator());
                }

                if (service.error != null) {
                  return Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        const Icon(Icons.error_outline, size: 48, color: Colors.red),
                        const SizedBox(height: 8),
                        Text(service.error!, style: const TextStyle(color: Colors.red)),
                      ],
                    ),
                  );
                }

                if (_searchController.text.isEmpty) {
                  return _buildHistoryPanel(service);
                }

                if (service.currentResults.isEmpty) {
                  return const Center(
                    child: Column(
                      mainAxisAlignment: MainAxisAlignment.center,
                      children: [
                        Icon(Icons.search_off, size: 64, color: Colors.grey),
                        SizedBox(height: 16),
                        Text('未找到相关消息', style: TextStyle(color: Colors.grey)),
                      ],
                    ),
                  );
                }

                return _buildResultsList(service);
              },
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildFilterPanel() {
    return Container(
      padding: const EdgeInsets.all(16),
      color: Colors.grey.shade100,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          const Text('筛选条件', style: TextStyle(fontWeight: FontWeight.bold)),
          const SizedBox(height: 8),
          Wrap(
            spacing: 8,
            children: [
              FilterChip(
                label: const Text('文本'),
                onSelected: (v) {},
              ),
              FilterChip(
                label: const Text('图片'),
                onSelected: (v) {},
              ),
              FilterChip(
                label: const Text('文件'),
                onSelected: (v) {},
              ),
              FilterChip(
                label: const Text('最近7天'),
                onSelected: (v) {},
              ),
              FilterChip(
                label: const Text('最近30天'),
                onSelected: (v) {},
              ),
            ],
          ),
        ],
      ),
    );
  }

  Widget _buildHistoryPanel(MessageSearchService service) {
    if (service.searchHistory.isEmpty) {
      return const Center(
        child: Text('开始搜索你的消息', style: TextStyle(color: Colors.grey)),
      );
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
          child: Row(
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            children: [
              const Text(
                '搜索历史',
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
              TextButton(
                onPressed: () => service.clearHistory(),
                child: const Text('清空'),
              ),
            ],
          ),
        ),
        Expanded(
          child: ListView.builder(
            itemCount: service.searchHistory.length,
            itemBuilder: (context, index) {
              final query = service.searchHistory[index];
              return ListTile(
                leading: const Icon(Icons.history),
                title: Text(query),
                trailing: IconButton(
                  icon: const Icon(Icons.close, size: 18),
                  onPressed: () => service.removeFromHistory(query),
                ),
                onTap: () {
                  _searchController.text = query;
                  _performSearch(query);
                },
              );
            },
          ),
        ),
      ],
    );
  }

  Widget _buildResultsList(MessageSearchService service) {
    return ListView.builder(
      itemCount: service.currentResults.length,
      itemBuilder: (context, index) {
        final result = service.currentResults[index];
        return _SearchResultTile(result: result);
      },
    );
  }
}

class _SearchResultTile extends StatelessWidget {
  final SearchResult result;

  const _SearchResultTile({required this.result});

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: CircleAvatar(
        backgroundColor: Colors.blue.shade100,
        child: Text(result.senderName[0].toUpperCase()),
      ),
      title: Row(
        children: [
          Expanded(
            child: Text(
              result.senderName,
              overflow: TextOverflow.ellipsis,
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
          ),
          Text(
            result.formattedTimestamp,
            style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
          ),
        ],
      ),
      subtitle: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          if (widget.conversationName == null && result.conversationName.isNotEmpty)
            Text(
              result.conversationName,
              style: TextStyle(fontSize: 12, color: Colors.grey.shade600),
            ),
          Text(
            result.highlightedContent.isNotEmpty
                ? result.highlightedContent
                : result.message.displayContent,
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
        ],
      ),
      onTap: () {
        // 跳转到对应消息位置
      },
    );
  }
}

// 修复使用外部变量的方法
class MessageSearchDelegate extends SearchDelegate<String> {
  @override
  List<Widget> buildActions(BuildContext context) {
    return [
      if (query.isNotEmpty)
        IconButton(
          icon: const Icon(Icons.clear),
          onPressed: () {
            query = '';
          },
        ),
    ];
  }

  @override
  Widget buildLeading(BuildContext context) {
    return IconButton(
      icon: const Icon(Icons.arrow_back),
      onPressed: () => close(context, ''),
    );
  }

  @override
  Widget buildResults(BuildContext context) {
    final service = context.read<MessageSearchService>();
    service.searchMessages(query: query);
    
    return Consumer<MessageSearchService>(
      builder: (context, service, child) {
        if (service.isSearching) {
          return const Center(child: CircularProgressIndicator());
        }

        if (service.currentResults.isEmpty) {
          return const Center(child: Text('未找到结果'));
        }

        return ListView.builder(
          itemCount: service.currentResults.length,
          itemBuilder: (context, index) {
            final result = service.currentResults[index];
            return ListTile(
              title: Text(result.senderName),
              subtitle: Text(result.message.content),
              onTap: () => close(context, result.message.id),
            );
          },
        );
      },
    );
  }

  @override
  Widget buildSuggestions(BuildContext context) {
    final service = context.read<MessageSearchService>();
    final suggestions = service.getSuggestions(query);

    if (suggestions.isEmpty) {
      return const Center(child: Text('输入关键词开始搜索'));
    }

    return ListView.builder(
      itemCount: suggestions.length,
      itemBuilder: (context, index) {
        final suggestion = suggestions[index];
        return ListTile(
          leading: const Icon(Icons.history),
          title: Text(suggestion),
          onTap: () {
            query = suggestion;
            showResults(context);
          },
        );
      },
    );
  }
}
