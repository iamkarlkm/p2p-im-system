import 'package:flutter/material.dart';
import '../services/search_service.dart';

/// Message Search Widgets
///
/// 消息搜索组件集
/// 包括搜索栏、搜索结果、热门搜索、搜索历史等组件

// ==================== 搜索栏组件 ====================

class SearchBar extends StatefulWidget {
  final Function(String) onSearch;
  final Function(String)? onChanged;
  final Function(String)? onSubmitted;
  final String? placeholder;
  final TextEditingController? controller;
  final bool autofocus;

  const SearchBar({
    super.key,
    required this.onSearch,
    this.onChanged,
    this.onSubmitted,
    this.placeholder,
    this.controller,
    this.autofocus = false,
  });

  @override
  State<SearchBar> createState() => _SearchBarState();
}

class _SearchBarState extends State<SearchBar> {
  late TextEditingController _controller;
  bool _showClear = false;

  @override
  void initState() {
    super.initState();
    _controller = widget.controller ?? TextEditingController();
    _controller.addListener(_onTextChanged);
  }

  @override
  void dispose() {
    if (widget.controller == null) {
      _controller.dispose();
    }
    super.dispose();
  }

  void _onTextChanged() {
    setState(() {
      _showClear = _controller.text.isNotEmpty;
    });
    widget.onChanged?.call(_controller.text);
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border(
          bottom: BorderSide(color: Colors.grey.shade200),
        ),
      ),
      child: Row(
        children: [
          const Icon(Icons.search, color: Colors.grey, size: 20),
          const SizedBox(width: 8),
          Expanded(
            child: TextField(
              controller: _controller,
              autofocus: widget.autofocus,
              decoration: InputDecoration(
                hintText: widget.placeholder ?? '搜索消息...',
                hintStyle: TextStyle(color: Colors.grey.shade400),
                border: InputBorder.none,
                contentPadding: EdgeInsets.zero,
                isDense: true,
              ),
              textInputAction: TextInputAction.search,
              onSubmitted: (value) {
                widget.onSubmitted?.call(value);
                widget.onSearch(value);
              },
            ),
          ),
          if (_showClear)
            GestureDetector(
              onTap: () {
                _controller.clear();
                widget.onSearch('');
              },
              child: Container(
                padding: const EdgeInsets.all(4),
                decoration: BoxDecoration(
                  color: Colors.grey.shade300,
                  shape: BoxShape.circle,
                ),
                child: const Icon(Icons.close, size: 14, color: Colors.white),
              ),
            ),
        ],
      ),
    );
  }
}

// ==================== 热门搜索组件 ====================

class HotSearchSection extends StatelessWidget {
  final List<String> keywords;
  final Function(String) onKeywordTap;
  final bool loading;
  final Future<List<String>> Function() onRefresh;

  const HotSearchSection({
    super.key,
    required this.keywords,
    required this.onKeywordTap,
    this.loading = false,
    required this.onRefresh,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              const Icon(Icons.trending_up, size: 16, color: Colors.orange),
              const SizedBox(width: 6),
              const Text(
                '热门搜索',
                style: TextStyle(
                  fontSize: 13,
                  color: Colors.grey,
                  fontWeight: FontWeight.w500,
                ),
              ),
              const Spacer(),
              if (loading)
                const SizedBox(
                  width: 14,
                  height: 14,
                  child: CircularProgressIndicator(strokeWidth: 2),
                )
              else
                GestureDetector(
                  onTap: () => onRefresh(),
                  child: const Icon(Icons.refresh, size: 16, color: Colors.grey),
                ),
            ],
          ),
          const SizedBox(height: 12),
          Wrap(
            spacing: 8,
            runSpacing: 8,
            children: keywords.asMap().entries.map((entry) {
              return _HotTag(
                keyword: entry.value,
                rank: entry.key + 1,
                onTap: () => onKeywordTap(entry.value),
              );
            }).toList(),
          ),
        ],
      ),
    );
  }
}

class _HotTag extends StatelessWidget {
  final String keyword;
  final int rank;
  final VoidCallback onTap;

  const _HotTag({
    required this.keyword,
    required this.rank,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    Color bgColor;
    Color textColor;

    if (rank <= 3) {
      bgColor = Colors.red.shade50;
      textColor = Colors.red;
    } else {
      bgColor = Colors.grey.shade100;
      textColor = Colors.grey.shade700;
    }

    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
        decoration: BoxDecoration(
          color: bgColor,
          borderRadius: BorderRadius.circular(16),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            if (rank <= 3)
              Container(
                padding: const EdgeInsets.symmetric(horizontal: 4, vertical: 1),
                margin: const EdgeInsets.only(right: 4),
                decoration: BoxDecoration(
                  color: Colors.red,
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  '$rank',
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 10,
                    fontWeight: FontWeight.bold,
                  ),
                ),
              ),
            Text(
              keyword,
              style: TextStyle(
                fontSize: 13,
                color: textColor,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// ==================== 搜索历史组件 ====================

class SearchHistorySection extends StatelessWidget {
  final List<String> history;
  final Function(String) onHistoryTap;
  final VoidCallback onClear;
  final bool loading;

  const SearchHistorySection({
    super.key,
    required this.history,
    required this.onHistoryTap,
    required this.onClear,
    this.loading = false,
  });

  @override
  Widget build(BuildContext context) {
    if (history.isEmpty && !loading) {
      return const SizedBox.shrink();
    }

    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Row(
            children: [
              const Icon(Icons.history, size: 16, color: Colors.grey),
              const SizedBox(width: 6),
              const Text(
                '搜索历史',
                style: TextStyle(
                  fontSize: 13,
                  color: Colors.grey,
                  fontWeight: FontWeight.w500,
                ),
              ),
              const Spacer(),
              GestureDetector(
                onTap: onClear,
                child: const Text(
                  '清空',
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.blue,
                  ),
                ),
              ),
            ],
          ),
          const SizedBox(height: 8),
          ...history.map((keyword) => _HistoryItem(
                keyword: keyword,
                onTap: () => onHistoryTap(keyword),
              )),
        ],
      ),
    );
  }
}

class _HistoryItem extends StatelessWidget {
  final String keyword;
  final VoidCallback onTap;

  const _HistoryItem({
    required this.keyword,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Padding(
        padding: const EdgeInsets.symmetric(vertical: 8),
        child: Row(
          children: [
            const Icon(Icons.access_time, size: 16, color: Colors.grey),
            const SizedBox(width: 8),
            Expanded(
              child: Text(
                keyword,
                style: const TextStyle(fontSize: 14),
              ),
            ),
            const Icon(Icons.north_west, size: 14, color: Colors.grey),
          ],
        ),
      ),
    );
  }
}

// ==================== 搜索结果组件 ====================

class SearchResultsList extends StatelessWidget {
  final SearchResult result;
  final Function(SearchHit) onResultTap;
  final VoidCallback? onLoadMore;
  final ScrollController? scrollController;

  const SearchResultsList({
    super.key,
    required this.result,
    required this.onResultTap,
    this.onLoadMore,
    this.scrollController,
  });

  @override
  Widget build(BuildContext context) {
    if (result.hits.isEmpty) {
      return _EmptyResults(keyword: result.keyword);
    }

    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        // 统计信息
        Padding(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          child: Text(
            '找到 ${result.total} 条相关消息',
            style: TextStyle(
              fontSize: 13,
              color: Colors.grey.shade600,
            ),
          ),
        ),
        // 结果列表
        ListView.builder(
          controller: scrollController,
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          itemCount: result.hits.length,
          itemBuilder: (context, index) {
            return _SearchResultItem(
              hit: result.hits[index],
              keyword: result.keyword,
              onTap: () => onResultTap(result.hits[index]),
            );
          },
        ),
        // 加载更多
        if (result.hasNext)
          Center(
            child: TextButton(
              onPressed: onLoadMore,
              child: const Text('加载更多'),
            ),
          ),
      ],
    );
  }
}

class _SearchResultItem extends StatelessWidget {
  final SearchHit hit;
  final String keyword;
  final VoidCallback onTap;

  const _SearchResultItem({
    required this.hit,
    required this.keyword,
    required this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return InkWell(
      onTap: onTap,
      child: Container(
        margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 6),
        padding: const EdgeInsets.all(12),
        decoration: BoxDecoration(
          color: Colors.white,
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 头部信息
            Row(
              children: [
                CircleAvatar(
                  radius: 16,
                  backgroundColor: Colors.grey.shade200,
                  child: Text(
                    hit.senderNickname.isNotEmpty
                        ? hit.senderNickname[0].toUpperCase()
                        : '?',
                    style: const TextStyle(fontSize: 14, color: Colors.grey),
                  ),
                ),
                const SizedBox(width: 8),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        hit.senderNickname,
                        style: const TextStyle(
                          fontSize: 14,
                          fontWeight: FontWeight.w500,
                        ),
                      ),
                      Text(
                        SearchUtils.formatMessageTime(hit.messageTime),
                        style: TextStyle(
                          fontSize: 12,
                          color: Colors.grey.shade500,
                        ),
                      ),
                    ],
                  ),
                ),
                _ConversationBadge(
                  type: hit.conversationType,
                ),
              ],
            ),
            const SizedBox(height: 8),
            // 消息内容
            _HighlightedText(
              text: SearchUtils.stripHighlight(hit.content),
              keyword: keyword,
            ),
            const SizedBox(height: 8),
            // 元信息
            Row(
              children: [
                Text(
                  SearchUtils.getMessageTypeIcon(hit.messageType),
                  style: const TextStyle(fontSize: 12),
                ),
                const SizedBox(width: 4),
                Text(
                  SearchUtils.getMessageTypeName(hit.messageType),
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.grey.shade500,
                  ),
                ),
                if (hit.fileName != null) ...[
                  const SizedBox(width: 12),
                  const Icon(Icons.attach_file, size: 14, color: Colors.grey),
                  const SizedBox(width: 4),
                  Expanded(
                    child: Text(
                      hit.fileName!,
                      style: TextStyle(
                        fontSize: 12,
                        color: Colors.grey.shade500,
                      ),
                      overflow: TextOverflow.ellipsis,
                    ),
                  ),
                ],
              ],
            ),
          ],
        ),
      ),
    );
  }
}

class _ConversationBadge extends StatelessWidget {
  final int type;

  const _ConversationBadge({required this.type});

  @override
  Widget build(BuildContext context) {
    final isPrivate = type == 1;
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
      decoration: BoxDecoration(
        color: isPrivate ? Colors.blue.shade50 : Colors.green.shade50,
        borderRadius: BorderRadius.circular(4),
      ),
      child: Text(
        isPrivate ? '私聊' : '群聊',
        style: TextStyle(
          fontSize: 11,
          color: isPrivate ? Colors.blue : Colors.green,
        ),
      ),
    );
  }
}

class _HighlightedText extends StatelessWidget {
  final String text;
  final String keyword;

  const _HighlightedText({
    required this.text,
    required this.keyword,
  });

  @override
  Widget build(BuildContext context) {
    if (keyword.isEmpty) {
      return Text(
        text,
        style: const TextStyle(fontSize: 14, color: Colors.black87),
        maxLines: 3,
        overflow: TextOverflow.ellipsis,
      );
    }

    final lowerText = text.toLowerCase();
    final lowerKeyword = keyword.toLowerCase();
    final index = lowerText.indexOf(lowerKeyword);

    if (index == -1) {
      return Text(
        text,
        style: const TextStyle(fontSize: 14, color: Colors.black87),
        maxLines: 3,
        overflow: TextOverflow.ellipsis,
      );
    }

    return RichText(
      maxLines: 3,
      overflow: TextOverflow.ellipsis,
      text: TextSpan(
        style: const TextStyle(fontSize: 14, color: Colors.black87),
        children: [
          TextSpan(text: text.substring(0, index)),
          TextSpan(
            text: text.substring(index, index + keyword.length),
            style: TextStyle(
              backgroundColor: Colors.yellow.shade200,
              fontWeight: FontWeight.w600,
            ),
          ),
          TextSpan(text: text.substring(index + keyword.length)),
        ],
      ),
    );
  }
}

class _EmptyResults extends StatelessWidget {
  final String keyword;

  const _EmptyResults({required this.keyword});

  @override
  Widget build(BuildContext context) {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(60),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(
              Icons.search_off,
              size: 64,
              color: Colors.grey.shade300,
            ),
            const SizedBox(height: 16),
            Text(
              keyword.isEmpty ? '请输入搜索关键词' : '未找到 "$keyword" 相关消息',
              style: TextStyle(
                fontSize: 16,
                color: Colors.grey.shade600,
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 8),
            Text(
              '尝试其他关键词或修改筛选条件',
              style: TextStyle(
                fontSize: 14,
                color: Colors.grey.shade400,
              ),
            ),
          ],
        ),
      ),
    );
  }
}

// ==================== 搜索建议组件 ====================

class SearchSuggestionsList extends StatelessWidget {
  final List<String> suggestions;
  final String prefix;
  final Function(String) onSuggestionTap;

  const SearchSuggestionsList({
    super.key,
    required this.suggestions,
    required this.prefix,
    required this.onSuggestionTap,
  });

  @override
  Widget build(BuildContext context) {
    if (suggestions.isEmpty) {
      return const SizedBox.shrink();
    }

    return Container(
      decoration: BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.circular(12),
      ),
      child: Column(
        children: suggestions.asMap().entries.map((entry) {
          return InkWell(
            onTap: () => onSuggestionTap(entry.value),
            child: Padding(
              padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
              child: Row(
                children: [
                  const Icon(Icons.search, size: 16, color: Colors.blue),
                  const SizedBox(width: 10),
                  Expanded(
                    child: _SuggestionText(
                      text: entry.value,
                      prefix: prefix,
                    ),
                  ),
                ],
              ),
            ),
          );
        }).toList(),
      ),
    );
  }
}

class _SuggestionText extends StatelessWidget {
  final String text;
  final String prefix;

  const _SuggestionText({
    required this.text,
    required this.prefix,
  });

  @override
  Widget build(BuildContext context) {
    final lowerText = text.toLowerCase();
    final lowerPrefix = prefix.toLowerCase();
    final index = lowerText.indexOf(lowerPrefix);

    if (index == -1) {
      return Text(text, style: const TextStyle(fontSize: 14));
    }

    return RichText(
      text: TextSpan(
        style: const TextStyle(fontSize: 14, color: Colors.black87),
        children: [
          TextSpan(text: text.substring(0, index)),
          TextSpan(
            text: text.substring(index, index + prefix.length),
            style: const TextStyle(
              color: Colors.blue,
              fontWeight: FontWeight.w600,
            ),
          ),
          TextSpan(text: text.substring(index + prefix.length)),
        ],
      ),
    );
  }
}

// ==================== 加载状态组件 ====================

class SearchLoading extends StatelessWidget {
  const SearchLoading({super.key});

  @override
  Widget build(BuildContext context) {
    return const Center(
      child: Padding(
        padding: EdgeInsets.all(40),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            CircularProgressIndicator(),
            SizedBox(height: 16),
            Text(
              '搜索中...',
              style: TextStyle(color: Colors.grey),
            ),
          ],
        ),
      ),
    );
  }
}
