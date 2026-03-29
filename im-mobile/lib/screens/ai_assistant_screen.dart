import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../models/ai_message_model.dart';
import '../services/ai_assistant_service.dart';
import '../services/voice_service.dart';
import '../widgets/ai_message_bubble.dart';
import '../widgets/ai_input_bar.dart';
import '../widgets/ai_recommendation_bar.dart';
import 'ai_chat_history_screen.dart';

class AiAssistantScreen extends StatefulWidget {
  const AiAssistantScreen({super.key});

  @override
  State<AiAssistantScreen> createState() => _AiAssistantScreenState();
}

class _AiAssistantScreenState extends State<AiAssistantScreen> {
  final ScrollController _scrollController = ScrollController();
  final TextEditingController _textController = TextEditingController();
  final FocusNode _focusNode = FocusNode();
  bool _showRecommendations = true;
  
  @override
  void initState() {
    super.initState();
    WidgetsBinding.instance.addPostFrameCallback((_) {
      _scrollToBottom();
    });
  }

  void _scrollToBottom() {
    if (_scrollController.hasClients) {
      _scrollController.animateTo(
        _scrollController.position.maxScrollExtent,
        duration: const Duration(milliseconds: 300),
        curve: Curves.easeOut,
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return MultiProvider(
      providers: [
        ChangeNotifierProvider(create: (_) => AiAssistantService()),
        ChangeNotifierProvider(create: (_) => VoiceService()..initialize()),
      ],
      child: Builder(
        builder: (context) {
          final aiService = context.watch<AiAssistantService>();
          
          WidgetsBinding.instance.addPostFrameCallback((_) {
            _scrollToBottom();
          });
          
          return Scaffold(
            appBar: AppBar(
              title: const Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    'AI智能助手',
                    style: TextStyle(fontSize: 18, fontWeight: FontWeight.w600),
                  ),
                  Text(
                    '在线',
                    style: TextStyle(fontSize: 12, color: Colors.green),
                  ),
                ],
              ),
              actions: [
                IconButton(
                  icon: const Icon(Icons.history),
                  onPressed: () => Navigator.push(
                    context,
                    MaterialPageRoute(
                      builder: (_) => const AiChatHistoryScreen(),
                    ),
                  ),
                ),
                PopupMenuButton<String>(
                  onSelected: (value) {
                    if (value == 'clear') {
                      _showClearDialog(context);
                    }
                  },
                  itemBuilder: (context) => [
                    const PopupMenuItem(
                      value: 'clear',
                      child: Row(
                        children: [
                          Icon(Icons.delete_outline, size: 20),
                          SizedBox(width: 8),
                          Text('清空对话'),
                        ],
                      ),
                    ),
                  ],
                ),
              ],
            ),
            body: Column(
              children: [
                Expanded(
                  child: GestureDetector(
                    onTap: () => _focusNode.unfocus(),
                    child: ListView.builder(
                      controller: _scrollController,
                      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                      itemCount: aiService.messages.length,
                      itemBuilder: (context, index) {
                        final message = aiService.messages[index];
                        return AiMessageBubble(
                          message: message,
                          onSuggestionTap: (suggestion) {
                            _textController.text = suggestion;
                            _sendMessage(context);
                          },
                        );
                      },
                    ),
                  ),
                ),
                if (_showRecommendations && aiService.messages.length < 3)
                  AiRecommendationBar(
                    onRecommendationTap: (text) {
                      _textController.text = text;
                      _sendMessage(context);
                      setState(() => _showRecommendations = false);
                    },
                  ),
                AiInputBar(
                  controller: _textController,
                  focusNode: _focusNode,
                  isLoading: aiService.isLoading,
                  onSend: () => _sendMessage(context),
                  onVoiceRecord: (path) => _sendVoiceMessage(context, path),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  void _sendMessage(BuildContext context) {
    final text = _textController.text.trim();
    if (text.isEmpty) return;
    
    _textController.clear();
    context.read<AiAssistantService>().sendMessage(text);
    setState(() => _showRecommendations = false);
  }

  void _sendVoiceMessage(BuildContext context, String path) {
    context.read<AiAssistantService>().sendVoiceMessage(path);
    setState(() => _showRecommendations = false);
  }

  void _showClearDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('清空对话'),
        content: const Text('确定要清空当前对话吗？此操作不可撤销。'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          FilledButton(
            onPressed: () {
              context.read<AiAssistantService>().clearHistory();
              Navigator.pop(context);
              setState(() => _showRecommendations = true);
            },
            child: const Text('清空'),
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    _scrollController.dispose();
    _textController.dispose();
    _focusNode.dispose();
    super.dispose();
  }
}
