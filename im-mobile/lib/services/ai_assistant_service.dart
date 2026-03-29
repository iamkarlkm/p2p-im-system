import 'dart:convert';
import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import '../models/ai_message_model.dart';

class AiAssistantService extends ChangeNotifier {
  static const String baseUrl = 'https://api.im-system.com';
  static const String wsUrl = 'wss://ws.im-system.com/ai';
  
  final List<AiMessage> _messages = [];
  bool _isLoading = false;
  bool _isTyping = false;
  String? _sessionId;
  String? _error;
  StreamSubscription? _wsSubscription;
  
  List<AiMessage> get messages => List.unmodifiable(_messages);
  bool get isLoading => _isLoading;
  bool get isTyping => _isTyping;
  String? get sessionId => _sessionId;
  String? get error => _error;
  
  AiAssistantService() {
    _sessionId = _generateSessionId();
    _loadHistory();
  }
  
  String _generateSessionId() {
    return 'ai_${DateTime.now().millisecondsSinceEpoch}_${_generateRandomString(8)}';
  }
  
  String _generateRandomString(int length) {
    const chars = 'abcdefghijklmnopqrstuvwxyz0123456789';
    return List.generate(length, (_) => chars[DateTime.now().microsecond % chars.length]).join();
  }
  
  void _loadHistory() {
    _messages.add(AiMessage(
      id: 'welcome',
      content: '你好！我是你的AI智能助手。我可以帮你：\n\n'
          '💬 回答问题和聊天\n'
          '🔍 搜索知识和信息\n'
          '📝 协助写作和编辑\n'
          '💡 提供建议和推荐\n\n'
          '有什么我可以帮你的吗？',
      type: AiMessageType.assistant,
      timestamp: DateTime.now(),
      metadata: {'type': 'welcome'},
    ));
    notifyListeners();
  }
  
  Future<void> sendMessage(String content, {Map<String, dynamic>? context}) async {
    if (content.trim().isEmpty) return;
    
    final userMessage = AiMessage(
      id: 'user_${DateTime.now().millisecondsSinceEpoch}',
      content: content.trim(),
      type: AiMessageType.user,
      timestamp: DateTime.now(),
      metadata: context,
    );
    
    _messages.add(userMessage);
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/api/ai/chat'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ${_getToken()}',
        },
        body: jsonEncode({
          'message': content,
          'sessionId': _sessionId,
          'context': context,
        }),
      ).timeout(const Duration(seconds: 30));
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final aiResponse = AiMessage(
          id: data['id'] ?? 'ai_${DateTime.now().millisecondsSinceEpoch}',
          content: data['content'] ?? '抱歉，我没有理解您的问题。',
          type: AiMessageType.assistant,
          timestamp: DateTime.now(),
          metadata: {
            'intent': data['intent'],
            'confidence': data['confidence'],
            'sources': data['sources'],
            'suggestions': data['suggestions'],
          },
        );
        _messages.add(aiResponse);
      } else {
        _error = '服务器响应错误: ${response.statusCode}';
        _addErrorMessage();
      }
    } on TimeoutException {
      _error = '请求超时，请稍后重试';
      _addErrorMessage();
    } catch (e) {
      _error = '网络错误: $e';
      _addErrorMessage();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
  
  void _addErrorMessage() {
    _messages.add(AiMessage(
      id: 'error_${DateTime.now().millisecondsSinceEpoch}',
      content: _error ?? '发生未知错误',
      type: AiMessageType.error,
      timestamp: DateTime.now(),
    ));
  }
  
  Future<void> sendVoiceMessage(String audioPath, {String language = 'zh-CN'}) async {
    _isLoading = true;
    notifyListeners();
    
    try {
      final request = http.MultipartRequest(
        'POST',
        Uri.parse('$baseUrl/api/ai/voice'),
      );
      
      request.headers['Authorization'] = 'Bearer ${_getToken()}';
      request.fields['sessionId'] = _sessionId!;
      request.fields['language'] = language;
      request.files.add(await http.MultipartFile.fromPath('audio', audioPath));
      
      final response = await request.send().timeout(const Duration(seconds: 60));
      
      if (response.statusCode == 200) {
        final responseData = await response.stream.bytesToString();
        final data = jsonDecode(responseData);
        
        _messages.add(AiMessage(
          id: 'voice_${DateTime.now().millisecondsSinceEpoch}',
          content: data['transcription'] ?? '',
          type: AiMessageType.user,
          timestamp: DateTime.now(),
          metadata: {'isVoice': true, 'audioUrl': audioPath},
        ));
        
        _messages.add(AiMessage(
          id: data['id'] ?? 'ai_voice_${DateTime.now().millisecondsSinceEpoch}',
          content: data['content'] ?? '抱歉，我没有听清楚。',
          type: AiMessageType.assistant,
          timestamp: DateTime.now(),
          metadata: {
            'isVoiceResponse': true,
            'audioUrl': data['audioUrl'],
          },
        ));
      }
    } catch (e) {
      _error = '语音处理失败: $e';
      _addErrorMessage();
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
  
  Future<List<Map<String, dynamic>>> getSuggestions(String query) async {
    if (query.length < 2) return [];
    
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/api/ai/suggestions?q=${Uri.encodeComponent(query)}'),
        headers: {'Authorization': 'Bearer ${_getToken()}'},
      ).timeout(const Duration(seconds: 5));
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return List<Map<String, dynamic>>.from(data['suggestions'] ?? []);
      }
    } catch (e) {
      if (kDebugMode) print('获取建议失败: $e');
    }
    return [];
  }
  
  Future<List<AiMessage>> loadChatHistory({int page = 0, int size = 20}) async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/api/ai/history?page=$page&size=$size'),
        headers: {'Authorization': 'Bearer ${_getToken()}'},
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final List<dynamic> history = data['messages'] ?? [];
        return history.map((m) => AiMessage.fromJson(m)).toList();
      }
    } catch (e) {
      if (kDebugMode) print('加载历史失败: $e');
    }
    return [];
  }
  
  Future<void> clearHistory() async {
    try {
      await http.delete(
        Uri.parse('$baseUrl/api/ai/session/$_sessionId'),
        headers: {'Authorization': 'Bearer ${_getToken()}'},
      );
      
      _messages.clear();
      _sessionId = _generateSessionId();
      _loadHistory();
      notifyListeners();
    } catch (e) {
      if (kDebugMode) print('清除历史失败: $e');
    }
  }
  
  Future<Map<String, dynamic>> getSessionStats() async {
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/api/ai/session/$_sessionId/stats'),
        headers: {'Authorization': 'Bearer ${_getToken()}'},
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      }
    } catch (e) {
      if (kDebugMode) print('获取统计失败: $e');
    }
    return {};
  }
  
  void setTyping(bool typing) {
    _isTyping = typing;
    notifyListeners();
  }
  
  void clearError() {
    _error = null;
    notifyListeners();
  }
  
  String _getToken() {
    return 'dummy_token';
  }
  
  @override
  void dispose() {
    _wsSubscription?.cancel();
    super.dispose();
  }
}
