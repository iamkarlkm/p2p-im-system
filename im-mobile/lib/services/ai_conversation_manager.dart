import 'dart:async';
import 'dart:convert';
import '../models/ai_assistant.dart';
import '../models/multimodal_message.dart';
import '../utils/api_client.dart';

/// AI助手对话管理器
class AIAssistantConversationManager {
  static final AIAssistantConversationManager _instance = 
      AIAssistantConversationManager._internal();
  factory AIAssistantConversationManager() => _instance;
  AIAssistantConversationManager._internal();

  final ApiClient _apiClient = ApiClient();
  
  /// 当前活跃的对话
  AIConversation? _currentConversation;
  
  /// 对话历史缓存
  final Map<String, List<MultimodalMessage>> _conversationHistory = {};
  
  /// 流式响应控制器
  final Map<String, StreamController<StreamChunk>> _streamControllers = {};
  
  /// 消息历史监听器
  final _messageHistoryController = StreamController<List<MultimodalMessage>>.broadcast();
  Stream<List<MultimodalMessage>> get messageHistoryStream => _messageHistoryController.stream;

  /// 获取当前对话
  AIConversation? get currentConversation => _currentConversation;

  /// 创建新对话
  Future<AIConversation?> createConversation({
    required AIAssistantModel assistant,
    String? title,
    Map<String, dynamic>? initialContext,
  }) async {
    try {
      final response = await _apiClient.post(
        '/api/multimodal/conversations',
        body: {
          'assistantId': assistant.id,
          'title': title ?? '新对话',
          'initialContext': initialContext,
        },
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        final data = jsonDecode(response.body);
        final conversation = AIConversation.fromJson({
          ...data,
          'assistant': assistant.toJson(),
        });
        
        _currentConversation = conversation;
        _conversationHistory[conversation.id] = [];
        
        return conversation;
      }
      return null;
    } catch (e) {
      // 离线模式：创建本地对话
      final localConversation = AIConversation(
        id: DateTime.now().millisecondsSinceEpoch.toString(),
        title: title ?? '新对话',
        assistant: assistant,
        createdAt: DateTime.now(),
        updatedAt: DateTime.now(),
      );
      
      _currentConversation = localConversation;
      _conversationHistory[localConversation.id] = [];
      
      return localConversation;
    }
  }

  /// 加载对话
  Future<AIConversation?> loadConversation(String conversationId) async {
    try {
      final response = await _apiClient.get('/api/multimodal/conversations/$conversationId');
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final conversation = AIConversation.fromJson(data);
        
        _currentConversation = conversation;
        await loadMessageHistory(conversationId);
        
        return conversation;
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  /// 加载消息历史
  Future<List<MultimodalMessage>> loadMessageHistory(String conversationId) async {
    try {
      final response = await _apiClient.get(
        '/api/multimodal/conversations/$conversationId/messages',
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body) as List;
        final messages = data.map((m) => MultimodalMessage.fromJson(m)).toList();
        
        _conversationHistory[conversationId] = messages;
        _messageHistoryController.add(messages);
        
        return messages;
      }
      return [];
    } catch (e) {
      return _conversationHistory[conversationId] ?? [];
    }
  }

  /// 发送消息并获取流式响应
  Future<void> sendMessage({
    required String content,
    List<MessageAttachment> attachments = const [],
    Map<String, dynamic>? context,
    required Function(String chunk, bool isDone) onResponse,
  }) async {
    if (_currentConversation == null) return;

    final conversationId = _currentConversation!.id;
    final assistantId = _currentConversation!.assistant.id;

    // 添加用户消息到历史
    final userMessage = MultimodalMessage(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      conversationId: conversationId,
      type: attachments.isEmpty ? MessageType.text : MessageType.mixed,
      content: content,
      attachments: attachments,
      timestamp: DateTime.now(),
    );

    _addMessageToHistory(conversationId, userMessage);

    // 创建AI消息占位
    final aiMessageId = '${DateTime.now().millisecondsSinceEpoch}_ai';
    final aiMessage = MultimodalMessage(
      id: aiMessageId,
      conversationId: conversationId,
      type: MessageType.text,
      content: '',
      timestamp: DateTime.now(),
      isStreaming: true,
    );
    
    _addMessageToHistory(conversationId, aiMessage);

    // 构建完整上下文
    final fullContext = {
      ...?context,
      'conversationHistory': _conversationHistory[conversationId]
          ?.where((m) => m.id != aiMessageId)
          .map((m) => m.toJson())
          .toList(),
    };

    // 发送流式请求
    try {
      final request = await _apiClient.createStreamRequest(
        '/api/multimodal/assistants/$assistantId/stream',
        body: {
          'conversationId': conversationId,
          'content': content,
          'attachments': attachments.map((a) => a.toJson()).toList(),
          'context': fullContext,
        },
      );

      final response = await request.send();
      
      if (response.statusCode == 200) {
        final buffer = StringBuffer();
        
        await for (final chunk in response.stream
            .transform(utf8.decoder)
            .transform(const LineSplitter())) {
          
          if (chunk.startsWith('data: ')) {
            final data = chunk.substring(6);
            
            if (data == '[DONE]') {
              _updateStreamingMessage(conversationId, aiMessageId, buffer.toString(), false);
              onResponse('', true);
            } else {
              try {
                final json = jsonDecode(data);
                final content = json['content'] ?? json['delta'] ?? '';
                buffer.write(content);
                _updateStreamingMessage(conversationId, aiMessageId, buffer.toString(), true);
                onResponse(content, false);
              } catch (_) {
                buffer.write(data);
                _updateStreamingMessage(conversationId, aiMessageId, buffer.toString(), true);
                onResponse(data, false);
              }
            }
          }
        }
      } else {
        _updateStreamingMessage(conversationId, aiMessageId, '抱歉，服务暂时不可用。', false);
        onResponse('抱歉，服务暂时不可用。', true);
      }
    } catch (e) {
      _updateStreamingMessage(conversationId, aiMessageId, '网络错误，请稍后重试。', false);
      onResponse('网络错误，请稍后重试。', true);
    }
  }

  /// 添加消息到历史
  void _addMessageToHistory(String conversationId, MultimodalMessage message) {
    if (!_conversationHistory.containsKey(conversationId)) {
      _conversationHistory[conversationId] = [];
    }
    _conversationHistory[conversationId]!.add(message);
    _messageHistoryController.add(_conversationHistory[conversationId]!);
  }

  /// 更新流式消息
  void _updateStreamingMessage(String conversationId, String messageId, String content, bool isStreaming) {
    final messages = _conversationHistory[conversationId];
    if (messages == null) return;

    final index = messages.indexWhere((m) => m.id == messageId);
    if (index != -1) {
      messages[index] = messages[index].copyWith(
        content: content,
        isStreaming: isStreaming,
        streamingContent: content,
      );
      _messageHistoryController.add(List.unmodifiable(messages));
    }
  }

  /// 重新生成回复
  Future<void> regenerateMessage({
    required String messageId,
    required Function(String chunk, bool isDone) onResponse,
  }) async {
    if (_currentConversation == null) return;

    final conversationId = _currentConversation!.id;
    final messages = _conversationHistory[conversationId];
    if (messages == null) return;

    // 找到要重新生成的消息及其前一条用户消息
    final aiIndex = messages.indexWhere((m) => m.id == messageId);
    if (aiIndex <= 0) return;

    final userMessage = messages[aiIndex - 1];
    
    // 删除当前AI消息
    messages.removeAt(aiIndex);
    
    // 重新发送
    await sendMessage(
      content: userMessage.content,
      attachments: userMessage.attachments,
      onResponse: onResponse,
    );
  }

  /// 删除消息
  Future<void> deleteMessage(String conversationId, String messageId) async {
    try {
      await _apiClient.delete('/api/multimodal/messages/$messageId');
    } catch (_) {}

    final messages = _conversationHistory[conversationId];
    if (messages != null) {
      messages.removeWhere((m) => m.id == messageId);
      _messageHistoryController.add(List.unmodifiable(messages));
    }
  }

  /// 清空对话历史
  Future<void> clearConversation(String conversationId) async {
    _conversationHistory[conversationId]?.clear();
    _messageHistoryController.add([]);
    
    try {
      await _apiClient.delete('/api/multimodal/conversations/$conversationId/messages');
    } catch (_) {}
  }

  /// 获取对话列表
  Future<List<AIConversation>> getConversations({
    String? assistantId,
    bool includeArchived = false,
  }) async {
    try {
      final queryParams = <String, String>{};
      if (assistantId != null) queryParams['assistantId'] = assistantId;
      if (includeArchived) queryParams['includeArchived'] = 'true';

      final response = await _apiClient.get(
        '/api/multimodal/conversations',
        queryParams: queryParams,
      );

      if (response.statusCode == 200) {
        final data = jsonDecode(response.body) as List;
        return data.map((c) => AIConversation.fromJson(c)).toList();
      }
      return [];
    } catch (e) {
      return [];
    }
  }

  /// 删除对话
  Future<void> deleteConversation(String conversationId) async {
    _conversationHistory.remove(conversationId);
    
    if (_currentConversation?.id == conversationId) {
      _currentConversation = null;
    }

    try {
      await _apiClient.delete('/api/multimodal/conversations/$conversationId');
    } catch (_) {}
  }

  /// 导出对话
  Future<String> exportConversation(String conversationId, {String format = 'markdown'}) async {
    final messages = _conversationHistory[conversationId] ?? [];
    
    if (format == 'markdown') {
      final buffer = StringBuffer();
      buffer.writeln('# 对话记录\n');
      
      for (final msg in messages) {
        final role = msg.senderId == null ? 'AI' : '用户';
        buffer.writeln('**$role**: ${msg.content}\n');
      }
      
      return buffer.toString();
    } else if (format == 'json') {
      return jsonEncode(messages.map((m) => m.toJson()).toList());
    }
    
    return '';
  }

  /// 获取消息历史（同步）
  List<MultimodalMessage>? getMessageHistory(String conversationId) {
    return _conversationHistory[conversationId];
  }

  /// 释放资源
  void dispose() {
    _messageHistoryController.close();
    
    for (final controller in _streamControllers.values) {
      controller.close();
    }
    _streamControllers.clear();
  }
}
