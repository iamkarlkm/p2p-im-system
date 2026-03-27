import 'dart:async';
import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:shared_preferences/shared_preferences.dart';
import '../models/search_result.dart';
import '../models/message.dart';

class MessageSearchService extends ChangeNotifier {
  static const String _baseUrl = 'http://localhost:8080/api';
  static const String _searchHistoryKey = 'message_search_history';
  static const int _maxHistorySize = 20;

  List<String> _searchHistory = [];
  List<SearchResult> _currentResults = [];
  bool _isSearching = false;
  String? _lastQuery;
  String? _error;

  List<String> get searchHistory => List.unmodifiable(_searchHistory);
  List<SearchResult> get currentResults => List.unmodifiable(_currentResults);
  bool get isSearching => _isSearching;
  String? get lastQuery => _lastQuery;
  String? get error => _error;

  MessageSearchService() {
    _loadSearchHistory();
  }

  Future<void> _loadSearchHistory() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final history = prefs.getStringList(_searchHistoryKey);
      if (history != null) {
        _searchHistory = history;
        notifyListeners();
      }
    } catch (e) {
      debugPrint('加载搜索历史失败: $e');
    }
  }

  Future<void> _saveSearchHistory() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.setStringList(_searchHistoryKey, _searchHistory);
    } catch (e) {
      debugPrint('保存搜索历史失败: $e');
    }
  }

  void _addToHistory(String query) {
    if (query.trim().isEmpty) return;
    
    _searchHistory.remove(query);
    _searchHistory.insert(0, query);
    
    if (_searchHistory.length > _maxHistorySize) {
      _searchHistory = _searchHistory.sublist(0, _maxHistorySize);
    }
    
    _saveSearchHistory();
    notifyListeners();
  }

  Future<void> searchMessages({
    required String query,
    String? conversationId,
    String? senderId,
    DateTime? startDate,
    DateTime? endDate,
    List<String>? messageTypes,
    int page = 0,
    int size = 20,
  }) async {
    if (query.trim().isEmpty) {
      _currentResults = [];
      _error = null;
      notifyListeners();
      return;
    }

    _isSearching = true;
    _lastQuery = query;
    _error = null;
    notifyListeners();

    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('auth_token');
      if (token == null) {
        _error = '未登录';
        _isSearching = false;
        notifyListeners();
        return;
      }

      final params = <String, String>{
        'q': query,
        'page': page.toString(),
        'size': size.toString(),
      };

      if (conversationId != null) params['conversationId'] = conversationId;
      if (senderId != null) params['senderId'] = senderId;
      if (startDate != null) params['startDate'] = startDate.toIso8601String();
      if (endDate != null) params['endDate'] = endDate.toIso8601String();
      if (messageTypes != null) params['types'] = messageTypes.join(',');

      final uri = Uri.parse('$_baseUrl/search/messages').replace(queryParameters: params);
      
      final response = await http.get(
        uri,
        headers: {'Authorization': 'Bearer $token'},
      );

      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(response.body);
        _currentResults = data.map((e) => SearchResult.fromJson(e)).toList();
        _addToHistory(query);
      } else {
        _error = '搜索失败: ${response.statusCode}';
        _currentResults = [];
      }
    } catch (e) {
      _error = '搜索出错: $e';
      _currentResults = [];
    } finally {
      _isSearching = false;
      notifyListeners();
    }
  }

  Future<List<SearchResult>> searchInConversation({
    required String conversationId,
    required String query,
    int page = 0,
    int size = 20,
  }) async {
    await searchMessages(
      query: query,
      conversationId: conversationId,
      page: page,
      size: size,
    );
    return _currentResults;
  }

  Future<List<Message>> searchByDate({
    required String conversationId,
    required DateTime date,
  }) async {
    final startOfDay = DateTime(date.year, date.month, date.day);
    final endOfDay = startOfDay.add(const Duration(days: 1));
    
    await searchMessages(
      query: '*',
      conversationId: conversationId,
      startDate: startOfDay,
      endDate: endOfDay,
      size: 100,
    );
    
    return _currentResults.map((r) => r.message).toList();
  }

  Future<Map<String, int>> getSearchStats(String conversationId) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('auth_token');
      if (token == null) return {};

      final response = await http.get(
        Uri.parse('$_baseUrl/search/stats/$conversationId'),
        headers: {'Authorization': 'Bearer $token'},
      );

      if (response.statusCode == 200) {
        final Map<String, dynamic> data = jsonDecode(response.body);
        return data.map((key, value) => MapEntry(key, value as int));
      }
    } catch (e) {
      debugPrint('获取搜索统计失败: $e');
    }
    return {};
  }

  List<String> getSuggestions(String partial) {
    if (partial.isEmpty) return _searchHistory.take(5).toList();
    
    return _searchHistory
        .where((h) => h.toLowerCase().contains(partial.toLowerCase()))
        .take(5)
        .toList();
  }

  void removeFromHistory(String query) {
    _searchHistory.remove(query);
    _saveSearchHistory();
    notifyListeners();
  }

  void clearHistory() {
    _searchHistory.clear();
    _saveSearchHistory();
    notifyListeners();
  }

  void clearResults() {
    _currentResults = [];
    _lastQuery = null;
    _error = null;
    notifyListeners();
  }

  Future<void> searchUsers(String query) async {
    if (query.trim().isEmpty) return;

    _isSearching = true;
    notifyListeners();

    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('auth_token');
      if (token == null) return;

      final response = await http.get(
        Uri.parse('$_baseUrl/search/users?q=$query'),
        headers: {'Authorization': 'Bearer $token'},
      );

      if (response.statusCode == 200) {
        _addToHistory(query);
      }
    } catch (e) {
      debugPrint('搜索用户失败: $e');
    } finally {
      _isSearching = false;
      notifyListeners();
    }
  }

  Future<void> searchGroups(String query) async {
    if (query.trim().isEmpty) return;

    _isSearching = true;
    notifyListeners();

    try {
      final prefs = await SharedPreferences.getInstance();
      final token = prefs.getString('auth_token');
      if (token == null) return;

      final response = await http.get(
        Uri.parse('$_baseUrl/search/groups?q=$query'),
        headers: {'Authorization': 'Bearer $token'},
      );

      if (response.statusCode == 200) {
        _addToHistory(query);
      }
    } catch (e) {
      debugPrint('搜索群组失败: $e');
    } finally {
      _isSearching = false;
      notifyListeners();
    }
  }
}
