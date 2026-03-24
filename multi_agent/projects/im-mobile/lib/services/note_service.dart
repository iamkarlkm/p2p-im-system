import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:im_mobile/models/note.dart';

class NoteService {
  static const String _baseUrl = 'http://localhost:8080/api/v1/notes';
  final String? authToken;

  NoteService({this.authToken});

  // ==================== 笔记 API ====================

  Future<ConversationNote> createNote({
    required int conversationId,
    String? title,
    String? content,
    String? color,
    List<String>? tags,
  }) async {
    final uri = Uri.parse(_baseUrl);
    final response = await http.post(
      uri,
      headers: _buildHeaders(),
      body: jsonEncode({
        'conversationId': conversationId,
        if (title != null) 'title': title,
        if (content != null) 'content': content,
        if (color != null) 'color': color,
        if (tags != null) 'tags': tags,
      }),
    );
    return _handleResponse(response);
  }

  Future<NoteListResponse> getNotes({
    int? conversationId,
    int page = 0,
    int size = 20,
    String sortBy = 'createdAt',
    String sortDir = 'desc',
  }) async {
    final params = {
      if (conversationId != null) 'conversationId': conversationId.toString(),
      'page': page.toString(),
      'size': size.toString(),
      'sortBy': sortBy,
      'sortDir': sortDir,
    };
    final uri = Uri.parse(_baseUrl).replace(queryParameters: params);
    final response = await http.get(uri, headers: _buildHeaders());
    return _handleResponse(response);
  }

  Future<ConversationNote> getNote(int noteId) async {
    final uri = Uri.parse('$_baseUrl/$noteId');
    final response = await http.get(uri, headers: _buildHeaders());
    return _handleResponse(response);
  }

  Future<ConversationNote> updateNote(
    int noteId, {
    String? title,
    String? content,
    String? color,
    List<String>? tags,
  }) async {
    final uri = Uri.parse('$_baseUrl/$noteId');
    final response = await http.put(
      uri,
      headers: _buildHeaders(),
      body: jsonEncode({
        if (title != null) 'title': title,
        if (content != null) 'content': content,
        if (color != null) 'color': color,
        if (tags != null) 'tags': tags,
      }),
    );
    return _handleResponse(response);
  }

  Future<void> deleteNote(int noteId) async {
    final uri = Uri.parse('$_baseUrl/$noteId');
    final response = await http.delete(uri, headers: _buildHeaders());
    _handleResponse(response);
  }

  Future<ConversationNote> pinNote(int noteId, {bool pinned = true}) async {
    final uri = Uri.parse('$_baseUrl/$noteId/pin?pinned=$pinned');
    final response = await http.patch(uri, headers: _buildHeaders());
    return _handleResponse(response);
  }

  Future<NoteListResponse> searchNotes(String keyword, {int page = 0, int size = 20}) async {
    final uri = Uri.parse('$_baseUrl/search').replace(
      queryParameters: {'keyword': keyword, 'page': page.toString(), 'size': size.toString()},
    );
    final response = await http.get(uri, headers: _buildHeaders());
    return _handleResponse(response);
  }

  Future<NoteListResponse> getNotesByTag(String tag, {int page = 0, int size = 20}) async {
    final uri = Uri.parse('$_baseUrl/by-tag').replace(
      queryParameters: {'tag': tag, 'page': page.toString(), 'size': size.toString()},
    );
    final response = await http.get(uri, headers: _buildHeaders());
    return _handleResponse(response);
  }

  // ==================== 标签 API ====================

  Future<NoteTag> createTag({
    required String tagName,
    String? color,
    String? icon,
  }) async {
    final uri = Uri.parse('$_baseUrl/tags');
    final response = await http.post(
      uri,
      headers: _buildHeaders(),
      body: jsonEncode({
        'tagName': tagName,
        if (color != null) 'color': color,
        if (icon != null) 'icon': icon,
      }),
    );
    return _handleResponse(response);
  }

  Future<List<NoteTag>> getAllTags() async {
    final uri = Uri.parse('$_baseUrl/tags');
    final response = await http.get(uri, headers: _buildHeaders());
    return _handleResponse(response);
  }

  Future<List<NoteTag>> getTopTags({int limit = 10}) async {
    final uri = Uri.parse('$_baseUrl/tags/top').replace(
      queryParameters: {'limit': limit.toString()},
    );
    final response = await http.get(uri, headers: _buildHeaders());
    return _handleResponse(response);
  }

  Future<NoteTag> updateTag(
    int tagId, {
    String? tagName,
    String? color,
    String? icon,
  }) async {
    final uri = Uri.parse('$_baseUrl/tags/$tagId');
    final response = await http.put(
      uri,
      headers: _buildHeaders(),
      body: jsonEncode({
        if (tagName != null) 'tagName': tagName,
        if (color != null) 'color': color,
        if (icon != null) 'icon': icon,
      }),
    );
    return _handleResponse(response);
  }

  Future<void> deleteTag(int tagId) async {
    final uri = Uri.parse('$_baseUrl/tags/$tagId');
    final response = await http.delete(uri, headers: _buildHeaders());
    _handleResponse(response);
  }

  // ==================== 消息标注 API ====================

  Future<MessageAnnotation> annotateMessage({
    required int messageId,
    required int conversationId,
    String? annotationType,
    bool starred = false,
    String? note,
    String? color,
    String? emoji,
  }) async {
    final uri = Uri.parse('$_baseUrl/annotations');
    final response = await http.post(
      uri,
      headers: _buildHeaders(),
      body: jsonEncode({
        'messageId': messageId,
        'conversationId': conversationId,
        if (annotationType != null) 'annotationType': annotationType,
        'starred': starred,
        if (note != null) 'note': note,
        if (color != null) 'color': color,
        if (emoji != null) 'emoji': emoji,
      }),
    );
    return _handleResponse(response);
  }

  Future<AnnotationListResponse> getAnnotations({
    int? conversationId,
    int page = 0,
    int size = 50,
  }) async {
    final params = {
      if (conversationId != null) 'conversationId': conversationId.toString(),
      'page': page.toString(),
      'size': size.toString(),
    };
    final uri = Uri.parse('$_baseUrl/annotations').replace(queryParameters: params);
    final response = await http.get(uri, headers: _buildHeaders());
    return _handleResponse(response);
  }

  Future<AnnotationListResponse> getStarredMessages({int page = 0, int size = 50}) async {
    final uri = Uri.parse('$_baseUrl/annotations/starred').replace(
      queryParameters: {'page': page.toString(), 'size': size.toString()},
    );
    final response = await http.get(uri, headers: _buildHeaders());
    return _handleResponse(response);
  }

  Future<void> toggleStar(int annotationId) async {
    final uri = Uri.parse('$_baseUrl/annotations/$annotationId/star');
    final response = await http.patch(uri, headers: _buildHeaders());
    _handleResponse(response);
  }

  Future<void> deleteAnnotation(int annotationId) async {
    final uri = Uri.parse('$_baseUrl/annotations/$annotationId');
    final response = await http.delete(uri, headers: _buildHeaders());
    _handleResponse(response);
  }

  // ==================== 统计 API ====================

  Future<Map<String, dynamic>> getStats() async {
    final uri = Uri.parse('$_baseUrl/stats');
    final response = await http.get(uri, headers: _buildHeaders());
    return _handleResponse(response);
  }

  // ==================== 辅助方法 ====================

  Map<String, String> _buildHeaders() {
    final headers = {
      'Content-Type': 'application/json',
      'Accept': 'application/json',
    };
    if (authToken != null) {
      headers['Authorization'] = 'Bearer $authToken';
    }
    return headers;
  }

  dynamic _handleResponse(http.Response response) {
    if (response.statusCode >= 200 && response.statusCode < 300) {
      if (response.body.isEmpty) return null;
      return jsonDecode(response.body);
    } else {
      throw Exception('HTTP ${response.statusCode}: ${response.body}');
    }
  }
}

class NoteListResponse {
  final List<ConversationNote> content;
  final int totalPages;
  final int totalElements;

  NoteListResponse({
    required this.content,
    required this.totalPages,
    required this.totalElements,
  });

  factory NoteListResponse.fromJson(Map<String, dynamic> json) {
    return NoteListResponse(
      content: (json['content'] as List).map((e) => ConversationNote.fromJson(e)).toList(),
      totalPages: json['totalPages'],
      totalElements: json['totalElements'],
    );
  }
}

class AnnotationListResponse {
  final List<MessageAnnotation> content;
  final int totalPages;
  final int totalElements;

  AnnotationListResponse({
    required this.content,
    required this.totalPages,
    required this.totalElements,
  });

  factory AnnotationListResponse.fromJson(Map<String, dynamic> json) {
    return AnnotationListResponse(
      content: (json['content'] as List).map((e) => MessageAnnotation.fromJson(e)).toList(),
      totalPages: json['totalPages'],
      totalElements: json['totalElements'],
    );
  }
}
