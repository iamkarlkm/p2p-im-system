import 'dart:convert';
import 'package:http/http.dart' as http;

class MessageQuote {
  final String id;
  final String messageId;
  final String quotedMessageId;
  final String conversationId;
  final String userId;
  final String? quotedContent;
  final String? quotedSenderId;
  final String? quotedSenderName;
  final String quoteType;
  final bool isDeleted;
  final DateTime createdAt;
  final DateTime updatedAt;
  final String? quotePreview;
  final int attachmentCount;
  final bool hasAttachment;

  MessageQuote({
    required this.id,
    required this.messageId,
    required this.quotedMessageId,
    required this.conversationId,
    required this.userId,
    this.quotedContent,
    this.quotedSenderId,
    this.quotedSenderName,
    required this.quoteType,
    this.isDeleted = false,
    required this.createdAt,
    required this.updatedAt,
    this.quotePreview,
    this.attachmentCount = 0,
    this.hasAttachment = false,
  });

  factory MessageQuote.fromJson(Map<String, dynamic> json) {
    return MessageQuote(
      id: json['id'] as String,
      messageId: json['messageId'] as String,
      quotedMessageId: json['quotedMessageId'] as String,
      conversationId: json['conversationId'] as String,
      userId: json['userId'] as String,
      quotedContent: json['quotedContent'] as String?,
      quotedSenderId: json['quotedSenderId'] as String?,
      quotedSenderName: json['quotedSenderName'] as String?,
      quoteType: json['quoteType'] as String? ?? 'TEXT',
      isDeleted: json['isDeleted'] as bool? ?? false,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
      quotePreview: json['quotePreview'] as String?,
      attachmentCount: json['attachmentCount'] as int? ?? 0,
      hasAttachment: json['hasAttachment'] as bool? ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'messageId': messageId,
      'quotedMessageId': quotedMessageId,
      'conversationId': conversationId,
      'userId': userId,
      'quotedContent': quotedContent,
      'quotedSenderId': quotedSenderId,
      'quotedSenderName': quotedSenderName,
      'quoteType': quoteType,
      'isDeleted': isDeleted,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'quotePreview': quotePreview,
      'attachmentCount': attachmentCount,
      'hasAttachment': hasAttachment,
    };
  }
}

class MessageQuoteService {
  static final MessageQuoteService _instance = MessageQuoteService._internal();
  late final http.Client _client;
  String _baseUrl = '/api/v1/message-quotes';

  factory MessageQuoteService() {
    return _instance;
  }

  MessageQuoteService._internal() {
    _client = http.Client();
  }

  void setBaseUrl(String baseUrl) {
    _baseUrl = baseUrl;
  }

  // Basic CRUD operations
  Future<MessageQuote> createQuote(Map<String, dynamic> requestData) async {
    final response = await _client.post(
      Uri.parse(_baseUrl),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(requestData),
    );
    
    if (response.statusCode == 200 || response.statusCode == 201) {
      return MessageQuote.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to create quote: ${response.body}');
    }
  }

  Future<MessageQuote> getQuoteById(String quoteId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/$quoteId'));
    
    if (response.statusCode == 200) {
      return MessageQuote.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to get quote: ${response.body}');
    }
  }

  Future<MessageQuote> getQuoteByIdAndConversationId(String quoteId, String conversationId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/$quoteId/conversation/$conversationId'));
    
    if (response.statusCode == 200) {
      return MessageQuote.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to get quote: ${response.body}');
    }
  }

  Future<MessageQuote> getQuoteByMessageIdAndConversationId(String messageId, String conversationId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/message/$messageId/conversation/$conversationId'));
    
    if (response.statusCode == 200) {
      return MessageQuote.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to get quote: ${response.body}');
    }
  }

  Future<MessageQuote> updateQuote(String quoteId, Map<String, dynamic> requestData) async {
    final response = await _client.put(
      Uri.parse('$_baseUrl/$quoteId'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(requestData),
    );
    
    if (response.statusCode == 200) {
      return MessageQuote.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to update quote: ${response.body}');
    }
  }

  Future<void> deleteQuote(String quoteId) async {
    final response = await _client.delete(Uri.parse('$_baseUrl/$quoteId'));
    
    if (response.statusCode != 200 && response.statusCode != 204) {
      throw Exception('Failed to delete quote: ${response.body}');
    }
  }

  // Query operations
  Future<List<MessageQuote>> getQuotesByMessageId(String messageId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/message/$messageId'));
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => MessageQuote.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get quotes: ${response.body}');
    }
  }

  Future<List<MessageQuote>> getQuotesByQuotedMessageId(String quotedMessageId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/quoted/$quotedMessageId'));
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => MessageQuote.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get quotes: ${response.body}');
    }
  }

  Future<List<MessageQuote>> getQuotesByConversationId(String conversationId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/conversation/$conversationId'));
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => MessageQuote.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get quotes: ${response.body}');
    }
  }

  Future<List<MessageQuote>> getQuotesByUserId(String userId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/user/$userId'));
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => MessageQuote.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get quotes: ${response.body}');
    }
  }

  Future<List<MessageQuote>> getQuotesByConversationIdAndUserId(String conversationId, String userId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/conversation/$conversationId/user/$userId'));
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => MessageQuote.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get quotes: ${response.body}');
    }
  }

  Future<List<MessageQuote>> getQuotesForMessage(String conversationId, String quotedMessageId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/conversation/$conversationId/message/$quotedMessageId'));
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => MessageQuote.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get quotes: ${response.body}');
    }
  }

  Future<List<MessageQuote>> getQuotesByQuotedSenderId(String quotedSenderId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/sender/$quotedSenderId'));
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => MessageQuote.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get quotes: ${response.body}');
    }
  }

  // Statistics methods
  Future<int> countQuotesForMessage(String quotedMessageId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/quoted/$quotedMessageId/count'));
    
    if (response.statusCode == 200) {
      final json = jsonDecode(response.body);
      return json['count'] as int;
    } else {
      throw Exception('Failed to count quotes: ${response.body}');
    }
  }

  Future<int> countQuotesInConversation(String conversationId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/conversation/$conversationId/count'));
    
    if (response.statusCode == 200) {
      final json = jsonDecode(response.body);
      return json['count'] as int;
    } else {
      throw Exception('Failed to count quotes: ${response.body}');
    }
  }

  Future<int> countQuotesByUser(String userId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/user/$userId/count'));
    
    if (response.statusCode == 200) {
      final json = jsonDecode(response.body);
      return json['count'] as int;
    } else {
      throw Exception('Failed to count quotes: ${response.body}');
    }
  }

  // Delete operations
  Future<void> markQuoteAsDeleted(String messageId) async {
    final response = await _client.delete(Uri.parse('$_baseUrl/message/$messageId/mark-deleted'));
    
    if (response.statusCode != 200 && response.statusCode != 204) {
      throw Exception('Failed to mark quote as deleted: ${response.body}');
    }
  }

  Future<void> markQuotesForDeletedMessage(String quotedMessageId) async {
    final response = await _client.delete(Uri.parse('$_baseUrl/quoted/$quotedMessageId/mark-deleted'));
    
    if (response.statusCode != 200 && response.statusCode != 204) {
      throw Exception('Failed to mark quotes as deleted: ${response.body}');
    }
  }

  Future<int> cleanupDeletedQuotes(String conversationId, String threshold) async {
    final response = await _client.delete(
      Uri.parse('$_baseUrl/conversation/$conversationId/cleanup-deleted'),
      headers: {'Content-Type': 'application/json'},
    );
    
    if (response.statusCode == 200) {
      final json = jsonDecode(response.body);
      return json['deletedCount'] as int;
    } else {
      throw Exception('Failed to cleanup deleted quotes: ${response.body}');
    }
  }

  // Search operations
  Future<List<MessageQuote>> searchQuotesByPreview(String conversationId, String keyword) async {
    final response = await _client.get(Uri.parse('$_baseUrl/conversation/$conversationId/search?keyword=$keyword'));
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => MessageQuote.fromJson(json)).toList();
    } else {
      throw Exception('Failed to search quotes: ${response.body}');
    }
  }

  Future<List<MessageQuote>> searchQuotesByContent(String keyword) async {
    final response = await _client.get(Uri.parse('$_baseUrl/search?keyword=$keyword'));
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => MessageQuote.fromJson(json)).toList();
    } else {
      throw Exception('Failed to search quotes: ${response.body}');
    }
  }

  // Helper methods
  Future<MessageQuote> createTextQuote({
    required String messageId,
    required String quotedMessageId,
    required String conversationId,
    required String userId,
    required String quotedContent,
    required String quotedSenderId,
    required String quotedSenderName,
    required String quotePreview,
  }) async {
    final requestData = {
      'messageId': messageId,
      'quotedMessageId': quotedMessageId,
      'conversationId': conversationId,
      'userId': userId,
      'quotedContent': quotedContent,
      'quotedSenderId': quotedSenderId,
      'quotedSenderName': quotedSenderName,
      'quoteType': 'TEXT',
      'quotePreview': quotePreview,
      'hasAttachment': false,
      'attachmentCount': 0,
    };
    return createQuote(requestData);
  }

  Future<MessageQuote> createAttachmentQuote({
    required String messageId,
    required String quotedMessageId,
    required String conversationId,
    required String userId,
    required String quotedContent,
    required String quotedSenderId,
    required String quotedSenderName,
    required String quotePreview,
    required int attachmentCount,
  }) async {
    final requestData = {
      'messageId': messageId,
      'quotedMessageId': quotedMessageId,
      'conversationId': conversationId,
      'userId': userId,
      'quotedContent': quotedContent,
      'quotedSenderId': quotedSenderId,
      'quotedSenderName': quotedSenderName,
      'quoteType': 'ATTACHMENT',
      'quotePreview': quotePreview,
      'hasAttachment': true,
      'attachmentCount': attachmentCount,
    };
    return createQuote(requestData);
  }

  void dispose() {
    _client.close();
  }
}