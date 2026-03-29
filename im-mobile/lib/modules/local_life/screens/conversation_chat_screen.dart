import 'package:flutter/material.dart';
import 'package:flutter_riverpod/flutter_riverpod.dart';
import '../providers/conversation_provider.dart';
import '../providers/semantic_search_provider.dart';
import '../widgets/search_chat_bubble.dart';
import '../widgets/voice_search_button.dart';
import '../widgets/poi_answer_card.dart';

/// 智能对话搜索页面
/// 支持自然语言POI搜索、多轮对话、语音输入
/// 
/// Author: IM Development Team
/// Since: 2026-03-28
class ConversationChatScreen extends ConsumerStatefulWidget {
  final String? initialQuery;
  final String? sessionId;

  const ConversationChatScreen({
    Key? key,
    this.initialQuery,
    this.sessionId,
  }) : super(key: key);

  @override
  ConsumerState<ConversationChatScreen> createState() => _ConversationChatScreenState();
}

class _ConversationChatScreenState extends ConsumerState<ConversationChatScreen> {
  final TextEditingController _messageController = TextEditingController();
  final ScrollController _scrollController = ScrollController();
  bool _isVoiceMode = false;

  @override
  void initState() {
    super.initState();
    if (widget.initialQuery != null) {
      _sendMessage(widget.initialQuery!);
    }
  }

  @override
  void dispose() {
    _messageController.dispose();
    _scrollController.dispose();
    super.dispose();
  }

  void _sendMessage(String text) {
    if (text.trim().isEmpty) return;
    
    ref.read(conversationProvider.notifier).sendQuery(text);
    _messageController.clear();
    _scrollToBottom();
  }

  void _scrollToBottom() {
    Future.delayed(const Duration(milliseconds: 100), () {
      if (_scrollController.hasClients) {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      }
    });
  }

  void _onVoiceResult(String text) {
    _sendMessage(text);
  }

  void _onSuggestionTap(String suggestion) {
    _sendMessage(suggestion);
  }

  @override
  Widget build(BuildContext context) {
    final conversationState = ref.watch(conversationProvider);
    final messages = conversationState.messages;
    final isLoading = conversationState.isLoading;

    return Scaffold(
      appBar: AppBar(
        title: const Text('智能搜索助手'),
        actions: [
          IconButton(
            icon: const Icon(Icons.history),
            onPressed: () => _showHistory(context),
          ),
          IconButton(
            icon: const Icon(Icons.more_vert),
            onPressed: () => _showOptions(context),
          ),
        ],
      ),
      body: Column(
        children: [
          // 搜索建议
          if (conversationState.suggestions.isNotEmpty)
            _buildSuggestions(conversationState.suggestions),
          
          // 消息列表
          Expanded(
            child: messages.isEmpty
                ? _buildEmptyState()
                : ListView.builder(
                    controller: _scrollController,
                    padding: const EdgeInsets.all(16),
                    itemCount: messages.length,
                    itemBuilder: (context, index) {
                      final message = messages[index];
                      return SearchChatBubble(
                        message: message,
                        onPoiTap: (poiId) => _navigateToPoiDetail(poiId),
                        onNavigateTap: (poi) => _startNavigation(poi),
                      );
                    },
                  ),
          ),
          
          // 加载指示器
          if (isLoading)
            const Padding(
              padding: EdgeInsets.all(8.0),
              child: LinearProgressIndicator(),
            ),
          
          // 输入区域
          _buildInputArea(),
        ],
      ),
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.chat_bubble_outline,
            size: 80,
            color: Colors.grey[300],
          ),
          const SizedBox(height: 16),
          Text(
            '智能搜索助手',
            style: TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.bold,
              color: Colors.grey[600],
            ),
          ),
          const SizedBox(height: 8),
          Text(
            '试着说：附近好吃的火锅、适合遛娃的公园',
            style: TextStyle(
              fontSize: 14,
              color: Colors.grey[500],
            ),
          ),
          const SizedBox(height: 24),
          _buildQuickActions(),
        ],
      ),
    );
  }

  Widget _buildQuickActions() {
    final actions = [
      '附近美食',
      '热门景点',
      '商场购物',
      '电影院',
      '停车场',
      '加油站',
    ];

    return Wrap(
      spacing: 8,
      runSpacing: 8,
      children: actions.map((action) {
        return ActionChip(
          label: Text(action),
          onPressed: () => _onSuggestionTap(action),
        );
      }).toList(),
    );
  }

  Widget _buildSuggestions(List<String> suggestions) {
    return Container(
      height: 50,
      padding: const EdgeInsets.symmetric(horizontal: 16),
      child: ListView.separated(
        scrollDirection: Axis.horizontal,
        itemCount: suggestions.length,
        separatorBuilder: (_, __) => const SizedBox(width: 8),
        itemBuilder: (context, index) {
          return Chip(
            label: Text(suggestions[index]),
            backgroundColor: Colors.blue[50],
            onDeleted: () => _onSuggestionTap(suggestions[index]),
            deleteIcon: const Icon(Icons.arrow_forward, size: 16),
          );
        },
      ),
    );
  }

  Widget _buildInputArea() {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.white,
        border: Border(
          top: BorderSide(color: Colors.grey[300]!),
        ),
      ),
      child: Row(
        children: [
          // 语音按钮
          VoiceSearchButton(
            onResult: _onVoiceResult,
            isListening: _isVoiceMode,
            onListeningChanged: (listening) {
              setState(() => _isVoiceMode = listening);
            },
          ),
          const SizedBox(width: 8),
          
          // 输入框
          Expanded(
            child: TextField(
              controller: _messageController,
              decoration: InputDecoration(
                hintText: '搜索附近的美食、景点、服务...',
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(24),
                  borderSide: BorderSide.none,
                ),
                filled: true,
                fillColor: Colors.grey[100],
                contentPadding: const EdgeInsets.symmetric(
                  horizontal: 16,
                  vertical: 12,
                ),
              ),
              onSubmitted: _sendMessage,
            ),
          ),
          const SizedBox(width: 8),
          
          // 发送按钮
          IconButton(
            icon: const Icon(Icons.send, color: Colors.blue),
            onPressed: () => _sendMessage(_messageController.text),
          ),
        ],
      ),
    );
  }

  void _navigateToPoiDetail(String poiId) {
    Navigator.pushNamed(context, '/poi/detail', arguments: poiId);
  }

  void _startNavigation(dynamic poi) {
    Navigator.pushNamed(context, '/navigation', arguments: poi);
  }

  void _showHistory(BuildContext context) {
    // 显示搜索历史
  }

  void _showOptions(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (context) => Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          ListTile(
            leading: const Icon(Icons.clear_all),
            title: const Text('清空对话'),
            onTap: () {
              ref.read(conversationProvider.notifier).clearMessages();
              Navigator.pop(context);
            },
          ),
          ListTile(
            leading: const Icon(Icons.feedback),
            title: const Text('反馈问题'),
            onTap: () => Navigator.pop(context),
          ),
        ],
      ),
    );
  }
}
