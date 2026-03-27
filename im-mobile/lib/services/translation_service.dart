import 'dart:convert';
import 'dart:async';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/translation_model.dart';

class TranslationService extends ChangeNotifier {
  static const String baseUrl = 'https://api.im-system.com';
  static const String wsUrl = 'wss://ws.im-system.com/translation';
  
  final List<TranslationRecord> _history = [];
  bool _isLoading = false;
  String? _error;
  String _sourceLanguage = 'auto';
  String _targetLanguage = 'zh-CN';
  StreamSubscription? _wsSubscription;
  WebSocketChannel? _webSocket;
  
  final Map<String, String> _supportedLanguages = {
    'auto': '自动检测',
    'zh-CN': '简体中文',
    'zh-TW': '繁體中文',
    'en': 'English',
    'ja': '日本語',
    'ko': '한국어',
    'fr': 'Français',
    'de': 'Deutsch',
    'es': 'Español',
    'ru': 'Русский',
    'ar': 'العربية',
    'pt': 'Português',
    'it': 'Italiano',
    'th': 'ไทย',
    'vi': 'Tiếng Việt',
    'id': 'Bahasa Indonesia',
  };
  
  List<TranslationRecord> get history => List.unmodifiable(_history);
  bool get isLoading => _isLoading;
  String? get error => _error;
  String get sourceLanguage => _sourceLanguage;
  String get targetLanguage => _targetLanguage;
  Map<String, String> get supportedLanguages => _supportedLanguages;
  
  TranslationService() {
    _loadSettings();
    _loadHistory();
    _connectWebSocket();
  }
  
  Future<void> _loadSettings() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      _sourceLanguage = prefs.getString('translation_source_lang') ?? 'auto';
      _targetLanguage = prefs.getString('translation_target_lang') ?? 'zh-CN';
      notifyListeners();
    } catch (e) {
      debugPrint('加载翻译设置失败: $e');
    }
  }
  
  Future<void> _saveSettings() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('translation_source_lang', _sourceLanguage);
      await prefs.setString('translation_target_lang', _targetLanguage);
    } catch (e) {
      debugPrint('保存翻译设置失败: $e');
    }
  }
  
  Future<void> _loadHistory() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final historyJson = prefs.getStringList('translation_history') ?? [];
      _history.clear();
      for (final json in historyJson) {
        try {
          final data = jsonDecode(json);
          _history.add(TranslationRecord.fromJson(data));
        } catch (e) {
          debugPrint('解析翻译历史失败: $e');
        }
      }
      notifyListeners();
    } catch (e) {
      debugPrint('加载翻译历史失败: $e');
    }
  }
  
  Future<void> _saveHistory() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final historyJson = _history
          .take(100)
          .map((r) => jsonEncode(r.toJson()))
          .toList();
      await prefs.setStringList('translation_history', historyJson);
    } catch (e) {
      debugPrint('保存翻译历史失败: $e');
    }
  }
  
  void _connectWebSocket() {
    try {
      _webSocket = IOWebSocketChannel.connect(wsUrl);
      _wsSubscription = _webSocket!.stream.listen(
        _handleWebSocketMessage,
        onError: (error) {
          debugPrint('翻译WebSocket错误: $error');
          _error = '实时翻译连接失败';
          notifyListeners();
        },
        onDone: () {
          debugPrint('翻译WebSocket连接关闭');
          Future.delayed(const Duration(seconds: 5), _connectWebSocket);
        },
      );
    } catch (e) {
      debugPrint('连接翻译WebSocket失败: $e');
    }
  }
  
  void _handleWebSocketMessage(dynamic message) {
    try {
      final data = jsonDecode(message);
      if (data['type'] == 'translation_result') {
        final record = TranslationRecord(
          id: data['id'] ?? DateTime.now().millisecondsSinceEpoch.toString(),
          sourceText: data['sourceText'] ?? '',
          translatedText: data['translatedText'] ?? '',
          sourceLanguage: data['sourceLanguage'] ?? 'auto',
          targetLanguage: data['targetLanguage'] ?? _targetLanguage,
          timestamp: DateTime.now(),
          isOffline: false,
        );
        _addToHistory(record);
      }
    } catch (e) {
      debugPrint('处理WebSocket消息失败: $e');
    }
  }
  
  Future<TranslationResult> translate(String text) async {
    if (text.trim().isEmpty) {
      return TranslationResult.error('请输入要翻译的文本');
    }
    
    _isLoading = true;
    _error = null;
    notifyListeners();
    
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/api/v1/translation'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ${await _getToken()}',
        },
        body: jsonEncode({
          'text': text,
          'sourceLanguage': _sourceLanguage,
          'targetLanguage': _targetLanguage,
        }),
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final result = TranslationResult.fromJson(data);
        
        if (result.success) {
          final record = TranslationRecord(
            id: DateTime.now().millisecondsSinceEpoch.toString(),
            sourceText: text,
            translatedText: result.translatedText,
            sourceLanguage: result.detectedSourceLanguage ?? _sourceLanguage,
            targetLanguage: _targetLanguage,
            timestamp: DateTime.now(),
            isOffline: false,
          );
          _addToHistory(record);
        }
        
        _isLoading = false;
        notifyListeners();
        return result;
      } else {
        throw Exception('翻译请求失败: ${response.statusCode}');
      }
    } catch (e) {
      _isLoading = false;
      _error = '翻译失败: $e';
      notifyListeners();
      
      final offlineResult = await _tryOfflineTranslation(text);
      if (offlineResult != null) {
        return offlineResult;
      }
      
      return TranslationResult.error(_error!);
    }
  }
  
  Future<TranslationResult?> _tryOfflineTranslation(String text) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final offlineEnabled = prefs.getBool('offline_translation_enabled') ?? false;
      
      if (!offlineEnabled) return null;
      
      final offlineData = prefs.getString('offline_translation_cache');
      if (offlineData == null) return null;
      
      final cache = jsonDecode(offlineData) as Map<String, dynamic>;
      final key = '${_sourceLanguage}_${_targetLanguage}_$text';
      
      if (cache.containsKey(key)) {
        final record = TranslationRecord(
          id: DateTime.now().millisecondsSinceEpoch.toString(),
          sourceText: text,
          translatedText: cache[key],
          sourceLanguage: _sourceLanguage,
          targetLanguage: _targetLanguage,
          timestamp: DateTime.now(),
          isOffline: true,
        );
        _addToHistory(record);
        
        return TranslationResult(
          success: true,
          translatedText: cache[key],
          detectedSourceLanguage: _sourceLanguage,
          isOffline: true,
        );
      }
      
      return null;
    } catch (e) {
      debugPrint('离线翻译失败: $e');
      return null;
    }
  }
  
  void _addToHistory(TranslationRecord record) {
    _history.insert(0, record);
    if (_history.length > 100) {
      _history.removeLast();
    }
    _saveHistory();
    notifyListeners();
  }
  
  Future<String> translateMessage(String messageId, String text) async {
    final result = await translate(text);
    if (result.success) {
      try {
        _webSocket?.sink.add(jsonEncode({
          'type': 'translate_message',
          'messageId': messageId,
          'translatedText': result.translatedText,
          'targetLanguage': _targetLanguage,
        }));
      } catch (e) {
        debugPrint('发送翻译消息失败: $e');
      }
      return result.translatedText;
    }
    return text;
  }
  
  void setSourceLanguage(String langCode) {
    if (_supportedLanguages.containsKey(langCode)) {
      _sourceLanguage = langCode;
      _saveSettings();
      notifyListeners();
    }
  }
  
  void setTargetLanguage(String langCode) {
    if (_supportedLanguages.containsKey(langCode)) {
      _targetLanguage = langCode;
      _saveSettings();
      notifyListeners();
    }
  }
  
  void swapLanguages() {
    if (_sourceLanguage != 'auto') {
      final temp = _sourceLanguage;
      _sourceLanguage = _targetLanguage;
      _targetLanguage = temp;
      _saveSettings();
      notifyListeners();
    }
  }
  
  Future<void> clearHistory() async {
    _history.clear();
    await _saveHistory();
    notifyListeners();
  }
  
  Future<void> deleteRecord(String id) async {
    _history.removeWhere((r) => r.id == id);
    await _saveHistory();
    notifyListeners();
  }
  
  Future<void> addToFavorites(String id) async {
    final index = _history.indexWhere((r) => r.id == id);
    if (index != -1) {
      _history[index] = _history[index].copyWith(isFavorite: true);
      await _saveHistory();
      notifyListeners();
    }
  }
  
  List<TranslationRecord> getFavoriteRecords() {
    return _history.where((r) => r.isFavorite).toList();
  }
  
  Future<String> detectLanguage(String text) async {
    if (text.trim().isEmpty) return 'auto';
    
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/api/v1/translation/detect'),
        headers: {
          'Content-Type': 'application/json',
          'Authorization': 'Bearer ${await _getToken()}',
        },
        body: jsonEncode({'text': text}),
      );
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return data['languageCode'] ?? 'auto';
      }
    } catch (e) {
      debugPrint('语言检测失败: $e');
    }
    return 'auto';
  }
  
  Future<String> _getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('auth_token') ?? '';
  }
  
  @override
  void dispose() {
    _wsSubscription?.cancel();
    _webSocket?.sink.close();
    super.dispose();
  }
}

class TranslationResult {
  final bool success;
  final String translatedText;
  final String? detectedSourceLanguage;
  final bool isOffline;
  final String? error;
  
  TranslationResult({
    required this.success,
    required this.translatedText,
    this.detectedSourceLanguage,
    this.isOffline = false,
    this.error,
  });
  
  factory TranslationResult.fromJson(Map<String, dynamic> json) {
    return TranslationResult(
      success: json['success'] ?? false,
      translatedText: json['translatedText'] ?? '',
      detectedSourceLanguage: json['detectedSourceLanguage'],
      isOffline: json['isOffline'] ?? false,
      error: json['error'],
    );
  }
  
  factory TranslationResult.error(String message) {
    return TranslationResult(
      success: false,
      translatedText: '',
      error: message,
    );
  }
}
