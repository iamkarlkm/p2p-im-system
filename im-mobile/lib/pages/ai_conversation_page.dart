import 'package:flutter/material.dart';
import '../models/ai_assistant.dart';
import '../models/multimodal_message.dart';
import '../services/ai_conversation_manager.dart';
import '../services/multimodal_message_processor.dart';
import '../components/ai_assistant_selector.dart';
import '../components/multimodal_input.dart';
import '../components/streaming_message.dart';

/// AI对话页面
class AIConversationPage extends StatefulWidget {
  final AIAssistantModel? initialAssistant;
  final String? conversationId;

  const AIConversationPage({
    super.key,
    this.initialAssistant,
    this.conversationId,
  });

  @override
  State<AIConversationPage> createState() => _AIConversationPageState();
}

class _AIConversationPageState extends State<AIConversationPage> {
  final AIAssistantConversationManager _conversationManager = AIAssistantConversationManager();
  final MultimodalMessageProcessor _messageProcessor = MultimodalMessageProcessor();
  final ScrollController _scrollController = ScrollController();
  final List<AIAssistantModel> _availableAssistants = [];

  AIConversation? _conversation;
  List<MultimodalMessage> _messages = [];
  bool _isLoading = false;
  bool _isStreaming = false;

  @override
  void initState() {
    super.initState();
    _initialize();
  }

  Future<void> _initialize() async {
    await _loadAssistants();
    
    if (widget.conversationId != null) {
      await _loadExistingConversation(widget.conversationId!);
    } else if (widget.initialAssistant != null) {
      await _createConversation(widget.initialAssistant!);
    }
  }

  Future<void> _loadAssistants() async {
    // 模拟加载助手列表，实际应从API获取
    setState(() {
      _availableAssistants.addAll([
        AIAssistantModel(
          id: 'gpt-4',
          name: 'GPT-4',
          description: 'OpenAI最强大的多模态大语言模型',
          provider: AIProvider.openai,
          capabilities: [
            AIAssistantCapability.textGeneration,
            AIAssistantCapability.imageAnalysis,
            AIAssistantCapability.codeGeneration,
            AIAssistantCapability.reasoning,
          ],
        ),
        AIAssistantModel(
          id: 'claude-3',
          name: 'Claude 3',
          description: 'Anthropic的多模态AI助手',
          provider: AIProvider.claude,
          capabilities: [
            AIAssistantCapability.textGeneration,
            AIAssistantCapability.imageAnalysis,
            AIAssistantCapability.codeGeneration,
            AIAssistantCapability.summarization,
          ],
        ),
        AIAssistantModel(
          id: 'gemini-pro',
          name: 'Gemini Pro',
          description: 'Google的多模态AI模型',
          provider: AIProvider.gemini,
          capabilities: [
            AIAssistantCapability.textGeneration,
            AIAssistantCapability.imageAnalysis,
            AIAssistantCapability.audioTranscription,
          ],
        ),
      ]);
    });
  }

  Future<void> _createConversation(AIAssistantModel assistant) async {
    setState(() => _isLoading = true);
    
    final conversation = await _conversationManager.createConversation(
      assistant: assistant,
    );
    
    if (conversation != null) {
      setState(() {
        _conversation = conversation;
        _messages = [];
        _isLoading = false;
      });
    } else {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _loadExistingConversation(String conversationId) async {
    setState(() => _isLoading = true);
    
    final conversation = await _conversationManager.loadConversation(conversationId);
    
    if (conversation != null) {
      setState(() {
        _conversation = conversation;
        _messages = _conversationManager.getMessageHistory(conversationId) ?? [];
        _isLoading = false;
      });
    } else {
      setState(() => _isLoading = false);
    }
  }

  void _sendMessage(String text, List<MessageAttachment> attachments) async {
    if (_conversation == null) return;

    setState(() => _isStreaming = true);

    await _conversationManager.sendMessage(
      content: text,
      attachments: attachments,
      onResponse: (chunk, isDone) {
        if (isDone) {
          setState(() => _isStreaming = false);
        }
        _scrollToBottom();
      },
    );
  }

  void _scrollToBottom() {
    if (_scrollController.hasClients) {
      Future.delayed(const Duration(milliseconds: 100), () {
        _scrollController.animateTo(
          _scrollController.position.maxScrollExtent,
          duration: const Duration(milliseconds: 300),
          curve: Curves.easeOut,
        );
      });
    }
  }

  void _showAssistantSelector() async {
    final assistant = await showAIAssistantSelector(
      context,
      assistants: _availableAssistants,
      selectedAssistant: _conversation?.assistant,
    );

    if (assistant != null && assistant.id != _conversation?.assistant.id) {
      await _createConversation(assistant);
    }
  }

  void _showConversationOptions() {
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: const Icon(Icons.edit),
              title: const Text('重命名对话'),
              onTap: () {
                Navigator.pop(context);
                _showRenameDialog();
              },
            ),
            ListTile(
              leading: const Icon(Icons.download),
              title: const Text('导出对话'),
              onTap: () {
                Navigator.pop(context);
                _showExportDialog();
              },
            ),
            ListTile(
              leading: const Icon(Icons.delete_outline, color: Colors.red),
              title: const Text('清空历史', style: TextStyle(color: Colors.red)),
              onTap: () {
                Navigator.pop(context);
                _clearHistory();
              },
            ),
          ],
        ),
      ),
    );
  }

  void _showRenameDialog() {
    final controller = TextEditingController(text: _conversation?.title);
    
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('重命名对话'),
        content: TextField(
          controller: controller,
          decoration: const InputDecoration(hintText: '输入对话名称'),
          autofocus: true,
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              setState(() {
                _conversation = _conversation?.copyWith(title: controller.text);
              });
              Navigator.pop(context);
            },
            child: const Text('保存'),
          ),
        ],
      ),
    );
  }

  void _showExportDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('导出对话'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: const Icon(Icons.description),
              title: const Text('Markdown'),
              onTap: () {
                Navigator.pop(context);
                _exportConversation('markdown');
              },
            ),
            ListTile(
              leading: const Icon(Icons.code),
              title: const Text('JSON'),
              onTap: () {
                Navigator.pop(context);
                _exportConversation('json');
              },
            ),
          ],
        ),
      ),
    );
  }

  Future<void> _exportConversation(String format) async {
    if (_conversation == null) return;
    
    final content = await _conversationManager.exportConversation(
      _conversation!.id,
      format: format,
    );
    
    // 实际应用中使用share_plus分享
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(content: Text('对话已导出为 $format')),
    );
  }

  Future<void> _clearHistory() async {
    if (_conversation == null) return;
    
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('确认清空'),
        content: const Text('确定要清空此对话的所有历史消息吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('清空', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      await _conversationManager.clearConversation(_conversation!.id);
      setState(() => _messages = []);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: _buildAppBarTitle(),
        actions: [
          IconButton(
            icon: const Icon(Icons.more_vert),
            onPressed: _showConversationOptions,
          ),
        ],
      ),
      body: _isLoading && _conversation == null
          ? const Center(child: CircularProgressIndicator())
          : _conversation == null
              ? _buildEmptyState()
              : _buildConversationBody(),
    );
  }

  Widget _buildAppBarTitle() {
    if (_conversation == null) {
      return const Text('AI助手');
    }

    return InkWell(
      onTap: _showAssistantSelector,
      child: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          if (_conversation!.assistant.avatarUrl != null)
            CircleAvatar(
              radius: 16,
              backgroundImage: NetworkImage(_conversation!.assistant.avatarUrl!),
            )
          else
            CircleAvatar(
              radius: 16,
              child: Text(_conversation!.assistant.name.substring(0, 1)),
            ),
          const SizedBox(width: 8),
          Flexible(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              mainAxisSize: MainAxisSize.min,
              children: [
                Text(
                  _conversation!.title ?? _conversation!.assistant.name,
                  style: const TextStyle(fontSize: 16),
                  overflow: TextOverflow.ellipsis,
                ),
                Text(
                  _conversation!.assistant.name,
                  style: TextStyle(
                    fontSize: 12,
                    color: Theme.of(context).colorScheme.onSurface.withValues(alpha: 0.6),
                  ),
                ),
              ],
            ),
          ),
          const Icon(Icons.arrow_drop_down, size: 20),
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
            Icons.smart_toy_outlined,
            size: 64,
            color: Theme.of(context).colorScheme.primary.withValues(alpha: 0.5),
          ),
          const SizedBox(height: 16),
          Text(
            '选择一个AI助手开始对话',
            style: Theme.of(context).textTheme.bodyLarge,
          ),
          const SizedBox(height: 24),
          ElevatedButton.icon(
            onPressed: _showAssistantSelector,
            icon: const Icon(Icons.add),
            label: const Text('新建对话'),
          ),
        ],
      ),
    );
  }

  Widget _buildConversationBody() {
    return Column(
      children: [
        // 消息列表
        Expanded(
          child: StreamBuilder<List<MultimodalMessage>>(
            stream: _conversationManager.messageHistoryStream,
            initialData: _messages,
            builder: (context, snapshot) {
              final messages = snapshot.data ?? [];
              
              if (messages.isEmpty) {
                return _buildWelcomeMessage();
              }

              return ListView.builder(
                controller: _scrollController,
                padding: const EdgeInsets.symmetric(vertical: 16),
                itemCount: messages.length,
                itemBuilder: (context, index) {
                  final message = messages[index];
                  final isMe = message.senderId != null;
                  
                  return StreamingMessageBubble(
                    message: message,
                    isMe: isMe,
                    onRegenerate: !isMe ? () => _regenerateMessage(message.id) : null,
                  );
                },
              );
            },
          ),
        ),

        // 输入框
        MultimodalInput(
          onSend: _sendMessage,
          isLoading: _isStreaming,
          hintText: '给 ${_conversation!.assistant.name} 发送消息...',
        ),
      ],
    );
  }

  Widget _buildWelcomeMessage() {
    return Center(
      child: Padding(
        padding: const EdgeInsets.all(32),
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Container(
              padding: const EdgeInsets.all(16),
              decoration: BoxDecoration(
                color: Theme.of(context).colorScheme.primaryContainer,
                shape: BoxShape.circle,
              ),
              child: Icon(
                Icons.smart_toy,
                size: 48,
                color: Theme.of(context).colorScheme.primary,
              ),
            ),
            const SizedBox(height: 24),
            Text(
              '你好！我是 ${_conversation!.assistant.name}',
              style: Theme.of(context).textTheme.titleLarge,
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 8),
            Text(
              _conversation!.assistant.description,
              style: Theme.of(context).textTheme.bodyMedium?.copyWith(
                color: Theme.of(context).colorScheme.onSurface.withValues(alpha: 0.7),
              ),
              textAlign: TextAlign.center,
            ),
            const SizedBox(height: 24),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              alignment: WrapAlignment.center,
              children: _conversation!.assistant.capabilities
                  .take(4)
                  .map((cap) => _buildCapabilityChip(cap))
                  .toList(),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildCapabilityChip(AIAssistantCapability capability) {
    final (icon, label) = _getCapabilityInfo(capability);
    
    return Chip(
      avatar: Icon(icon, size: 16),
      label: Text(label),
      visualDensity: VisualDensity.compact,
    );
  }

  (IconData, String) _getCapabilityInfo(AIAssistantCapability capability) {
    switch (capability) {
      case AIAssistantCapability.textGeneration:
        return (Icons.text_fields, '文本生成');
      case AIAssistantCapability.imageAnalysis:
        return (Icons.image, '图像理解');
      case AIAssistantCapability.codeGeneration:
        return (Icons.code, '代码');
      case AIAssistantCapability.audioTranscription:
        return (Icons.mic, '语音');
      default:
        return (Icons.star, 'AI能力');
    }
  }

  void _regenerateMessage(String messageId) {
    _conversationManager.regenerateMessage(
      messageId: messageId,
      onResponse: (chunk, isDone) {
        if (isDone) {
          setState(() => _isStreaming = false);
        } else {
          setState(() => _isStreaming = true);
        }
      },
    );
  }

  @override
  void dispose() {
    _scrollController.dispose();
    _conversationManager.dispose();
    super.dispose();
  }
}
