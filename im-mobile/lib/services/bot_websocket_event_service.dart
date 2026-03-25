import 'dart:convert';
import 'package:http/http.dart' as http;

class BotWebSocketEvent {
  final String id;
  final String botId;
  final String? sessionId;
  final String eventType;
  final String? eventSubtype;
  final String payload;
  final String? metadata;
  final String status;
  final int deliveryAttempts;
  final int maxAttempts;
  final DateTime? nextRetryAt;
  final DateTime? deliveredAt;
  final DateTime? acknowledgedAt;
  final DateTime createdAt;
  final DateTime updatedAt;
  final DateTime? processedAt;
  final String? webhookUrl;
  final int? webhookResponseCode;
  final String? webhookResponseBody;
  final int priority;
  final String? errorMessage;
  final String? sourceUserId;
  final String? sourceDeviceId;
  final String? sourceMessageId;
  final String? sourceConversationId;
  final String? tags;

  BotWebSocketEvent({
    required this.id,
    required this.botId,
    this.sessionId,
    required this.eventType,
    this.eventSubtype,
    required this.payload,
    this.metadata,
    required this.status,
    this.deliveryAttempts = 0,
    this.maxAttempts = 5,
    this.nextRetryAt,
    this.deliveredAt,
    this.acknowledgedAt,
    required this.createdAt,
    required this.updatedAt,
    this.processedAt,
    this.webhookUrl,
    this.webhookResponseCode,
    this.webhookResponseBody,
    this.priority = 1,
    this.errorMessage,
    this.sourceUserId,
    this.sourceDeviceId,
    this.sourceMessageId,
    this.sourceConversationId,
    this.tags,
  });

  factory BotWebSocketEvent.fromJson(Map<String, dynamic> json) {
    return BotWebSocketEvent(
      id: json['id'] as String,
      botId: json['botId'] as String,
      sessionId: json['sessionId'] as String?,
      eventType: json['eventType'] as String,
      eventSubtype: json['eventSubtype'] as String?,
      payload: json['payload'] as String,
      metadata: json['metadata'] as String?,
      status: json['status'] as String,
      deliveryAttempts: json['deliveryAttempts'] as int? ?? 0,
      maxAttempts: json['maxAttempts'] as int? ?? 5,
      nextRetryAt: json['nextRetryAt'] != null 
          ? DateTime.parse(json['nextRetryAt'] as String) 
          : null,
      deliveredAt: json['deliveredAt'] != null 
          ? DateTime.parse(json['deliveredAt'] as String) 
          : null,
      acknowledgedAt: json['acknowledgedAt'] != null 
          ? DateTime.parse(json['acknowledgedAt'] as String) 
          : null,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: DateTime.parse(json['updatedAt'] as String),
      processedAt: json['processedAt'] != null 
          ? DateTime.parse(json['processedAt'] as String) 
          : null,
      webhookUrl: json['webhookUrl'] as String?,
      webhookResponseCode: json['webhookResponseCode'] as int?,
      webhookResponseBody: json['webhookResponseBody'] as String?,
      priority: json['priority'] as int? ?? 1,
      errorMessage: json['errorMessage'] as String?,
      sourceUserId: json['sourceUserId'] as String?,
      sourceDeviceId: json['sourceDeviceId'] as String?,
      sourceMessageId: json['sourceMessageId'] as String?,
      sourceConversationId: json['sourceConversationId'] as String?,
      tags: json['tags'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'botId': botId,
      'sessionId': sessionId,
      'eventType': eventType,
      'eventSubtype': eventSubtype,
      'payload': payload,
      'metadata': metadata,
      'status': status,
      'deliveryAttempts': deliveryAttempts,
      'maxAttempts': maxAttempts,
      'nextRetryAt': nextRetryAt?.toIso8601String(),
      'deliveredAt': deliveredAt?.toIso8601String(),
      'acknowledgedAt': acknowledgedAt?.toIso8601String(),
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'processedAt': processedAt?.toIso8601String(),
      'webhookUrl': webhookUrl,
      'webhookResponseCode': webhookResponseCode,
      'webhookResponseBody': webhookResponseBody,
      'priority': priority,
      'errorMessage': errorMessage,
      'sourceUserId': sourceUserId,
      'sourceDeviceId': sourceDeviceId,
      'sourceMessageId': sourceMessageId,
      'sourceConversationId': sourceConversationId,
      'tags': tags,
    };
  }
}

class BotWebSocketEventService {
  static final BotWebSocketEventService _instance = BotWebSocketEventService._internal();
  late final http.Client _client;
  String _baseUrl = '/api/v1/bot-websocket-events';

  factory BotWebSocketEventService() {
    return _instance;
  }

  BotWebSocketEventService._internal() {
    _client = http.Client();
  }

  void setBaseUrl(String baseUrl) {
    _baseUrl = baseUrl;
  }

  // Basic CRUD operations
  Future<BotWebSocketEvent> createEvent(Map<String, dynamic> requestData) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(requestData),
    );
    
    if (response.statusCode == 200 || response.statusCode == 201) {
      return BotWebSocketEvent.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to create event: ${response.body}');
    }
  }

  Future<BotWebSocketEvent> getEventById(String eventId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/$eventId'));
    
    if (response.statusCode == 200) {
      return BotWebSocketEvent.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to get event: ${response.body}');
    }
  }

  Future<BotWebSocketEvent> getEventByIdAndBotId(String eventId, String botId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/$eventId/bot/$botId'));
    
    if (response.statusCode == 200) {
      return BotWebSocketEvent.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to get event: ${response.body}');
    }
  }

  Future<BotWebSocketEvent> updateEvent(String eventId, Map<String, dynamic> requestData) async {
    final response = await _client.put(
      Uri.parse('$_baseUrl/$eventId'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(requestData),
    );
    
    if (response.statusCode == 200) {
      return BotWebSocketEvent.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to update event: ${response.body}');
    }
  }

  Future<void> deleteEvent(String eventId) async {
    final response = await _client.delete(Uri.parse('$_baseUrl/$eventId'));
    
    if (response.statusCode != 200 && response.statusCode != 204) {
      throw Exception('Failed to delete event: ${response.body}');
    }
  }

  // Query operations
  Future<List<BotWebSocketEvent>> getEventsByBotId(String botId, {Map<String, String>? params}) async {
    final uri = params != null 
        ? Uri.parse('$_baseUrl/bot/$botId').replace(queryParameters: params)
        : Uri.parse('$_baseUrl/bot/$botId');
    
    final response = await _client.get(uri);
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => BotWebSocketEvent.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get events: ${response.body}');
    }
  }

  Future<List<BotWebSocketEvent>> getEventsBySessionId(String sessionId, {Map<String, String>? params}) async {
    final uri = params != null 
        ? Uri.parse('$_baseUrl/session/$sessionId').replace(queryParameters: params)
        : Uri.parse('$_baseUrl/session/$sessionId');
    
    final response = await _client.get(uri);
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => BotWebSocketEvent.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get events: ${response.body}');
    }
  }

  Future<List<BotWebSocketEvent>> getEventsByEventType(String eventType, {Map<String, String>? params}) async {
    final uri = params != null 
        ? Uri.parse('$_baseUrl/type/$eventType').replace(queryParameters: params)
        : Uri.parse('$_baseUrl/type/$eventType');
    
    final response = await _client.get(uri);
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => BotWebSocketEvent.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get events: ${response.body}');
    }
  }

  Future<List<BotWebSocketEvent>> getEventsByStatus(String status, {Map<String, String>? params}) async {
    final uri = params != null 
        ? Uri.parse('$_baseUrl/status/$status').replace(queryParameters: params)
        : Uri.parse('$_baseUrl/status/$status');
    
    final response = await _client.get(uri);
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => BotWebSocketEvent.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get events: ${response.body}');
    }
  }

  // Event creation helper methods
  Future<BotWebSocketEvent> createMessageEvent({
    required String botId,
    required String sessionId,
    required String sourceMessageId,
    required String sourceUserId,
    required String sourceConversationId,
    required String payload,
    String? metadata,
  }) async {
    final requestData = {
      'botId': botId,
      'sessionId': sessionId,
      'eventType': 'MESSAGE_RECEIVED',
      'eventSubtype': 'TEXT',
      'payload': payload,
      'metadata': metadata ?? '',
      'sourceMessageId': sourceMessageId,
      'sourceUserId': sourceUserId,
      'sourceConversationId': sourceConversationId,
      'priority': 1,
    };
    return createEvent(requestData);
  }

  Future<BotWebSocketEvent> createMessageEditedEvent({
    required String botId,
    required String sessionId,
    required String sourceMessageId,
    required String sourceUserId,
    required String sourceConversationId,
    required String oldContent,
    required String newContent,
  }) async {
    final requestData = {
      'botId': botId,
      'sessionId': sessionId,
      'eventType': 'MESSAGE_EDITED',
      'eventSubtype': 'TEXT',
      'payload': newContent,
      'metadata': jsonEncode({'old_content': oldContent}),
      'sourceMessageId': sourceMessageId,
      'sourceUserId': sourceUserId,
      'sourceConversationId': sourceConversationId,
      'priority': 2,
    };
    return createEvent(requestData);
  }

  Future<BotWebSocketEvent> createMessageDeletedEvent({
    required String botId,
    required String sessionId,
    required String sourceMessageId,
    required String sourceUserId,
    required String sourceConversationId,
  }) async {
    final requestData = {
      'botId': botId,
      'sessionId': sessionId,
      'eventType': 'MESSAGE_DELETED',
      'payload': 'Message deleted',
      'sourceMessageId': sourceMessageId,
      'sourceUserId': sourceUserId,
      'sourceConversationId': sourceConversationId,
      'priority': 2,
    };
    return createEvent(requestData);
  }

  Future<BotWebSocketEvent> createConversationCreatedEvent({
    required String botId,
    required String sessionId,
    required String sourceConversationId,
    required String sourceUserId,
    required String conversationName,
  }) async {
    final requestData = {
      'botId': botId,
      'sessionId': sessionId,
      'eventType': 'CONVERSATION_CREATED',
      'payload': conversationName,
      'sourceConversationId': sourceConversationId,
      'sourceUserId': sourceUserId,
      'priority': 3,
    };
    return createEvent(requestData);
  }

  Future<BotWebSocketEvent> createUserJoinedEvent({
    required String botId,
    required String sessionId,
    required String sourceConversationId,
    required String sourceUserId,
    required String username,
  }) async {
    final requestData = {
      'botId': botId,
      'sessionId': sessionId,
      'eventType': 'USER_JOINED',
      'payload': username,
      'sourceConversationId': sourceConversationId,
      'sourceUserId': sourceUserId,
      'priority': 3,
    };
    return createEvent(requestData);
  }

  Future<BotWebSocketEvent> createUserLeftEvent({
    required String botId,
    required String sessionId,
    required String sourceConversationId,
    required String sourceUserId,
    required String username,
  }) async {
    final requestData = {
      'botId': botId,
      'sessionId': sessionId,
      'eventType': 'USER_LEFT',
      'payload': username,
      'sourceConversationId': sourceConversationId,
      'sourceUserId': sourceUserId,
      'priority': 3,
    };
    return createEvent(requestData);
  }

  Future<BotWebSocketEvent> createWebhookEvent({
    required String botId,
    required String webhookUrl,
    required String eventType,
    required String payload,
    String? metadata,
  }) async {
    final requestData = {
      'botId': botId,
      'eventType': eventType,
      'webhookUrl': webhookUrl,
      'payload': payload,
      'metadata': metadata ?? '',
      'priority': 1,
    };
    return createEvent(requestData);
  }

  Future<BotWebSocketEvent> createCustomEvent({
    required String botId,
    String? sessionId,
    required String eventType,
    String? eventSubtype,
    required String payload,
    String? metadata,
    int priority = 1,
    String? tags,
  }) async {
    final requestData = {
      'botId': botId,
      'sessionId': sessionId,
      'eventType': eventType,
      'eventSubtype': eventSubtype,
      'payload': payload,
      'metadata': metadata ?? '',
      'priority': priority,
      'tags': tags,
    };
    return createEvent(requestData);
  }

  // Processing methods
  Future<List<BotWebSocketEvent>> getReadyForProcessing() async {
    final response = await _client.get(Uri.parse('$_baseUrl/ready'));
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => BotWebSocketEvent.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get ready events: ${response.body}');
    }
  }

  Future<List<BotWebSocketEvent>> getPendingEventsByBotId(String botId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/bot/$botId/pending'));
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => BotWebSocketEvent.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get pending events: ${response.body}');
    }
  }

  Future<List<BotWebSocketEvent>> getRetryableFailedEvents() async {
    final response = await _client.get(Uri.parse('$_baseUrl/retryable'));
    
    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(response.body);
      return jsonList.map((json) => BotWebSocketEvent.fromJson(json)).toList();
    } else {
      throw Exception('Failed to get retryable events: ${response.body}');
    }
  }

  Future<void> markEventsForRetry(List<String> eventIds, String nextRetryAt) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/batch/retry'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'eventIds': eventIds, 'nextRetryAt': nextRetryAt}),
    );
    
    if (response.statusCode != 200 && response.statusCode != 204) {
      throw Exception('Failed to mark events for retry: ${response.body}');
    }
  }

  Future<void> markEventsAsDelivered(List<String> eventIds) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/batch/delivered'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'eventIds': eventIds}),
    );
    
    if (response.statusCode != 200 && response.statusCode != 204) {
      throw Exception('Failed to mark events as delivered: ${response.body}');
    }
  }

  Future<void> markEventsAsAcknowledged(List<String> eventIds) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/batch/acknowledged'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'eventIds': eventIds}),
    );
    
    if (response.statusCode != 200 && response.statusCode != 204) {
      throw Exception('Failed to mark events as acknowledged: ${response.body}');
    }
  }

  Future<void> markEventsAsProcessed(List<String> eventIds) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/batch/processed'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'eventIds': eventIds}),
    );
    
    if (response.statusCode != 200 && response.statusCode != 204) {
      throw Exception('Failed to mark events as processed: ${response.body}');
    }
  }

  Future<void> markEventsAsFailed(List<String> eventIds, String errorMessage) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/batch/failed'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'eventIds': eventIds, 'errorMessage': errorMessage}),
    );
    
    if (response.statusCode != 200 && response.statusCode != 204) {
      throw Exception('Failed to mark events as failed: ${response.body}');
    }
  }

  // Statistics methods
  Future<int> countEventsByBotIdAndStatus(String botId, String status) async {
    final response = await _client.get(Uri.parse('$_baseUrl/bot/$botId/status/$status/count'));
    
    if (response.statusCode == 200) {
      final json = jsonDecode(response.body);
      return json['count'] as int;
    } else {
      throw Exception('Failed to count events: ${response.body}');
    }
  }

  Future<int> countPermanentlyFailedEventsByBot(String botId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/bot/$botId/failed/permanent/count'));
    
    if (response.statusCode == 200) {
      final json = jsonDecode(response.body);
      return json['count'] as int;
    } else {
      throw Exception('Failed to count failed events: ${response.body}');
    }
  }

  // Health check methods
  Future<bool> isBotEventQueueHealthy(String botId) async {
    try {
      final response = await _client.get(Uri.parse('$_baseUrl/bot/$botId/health'));
      
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body);
        return json['healthy'] as bool;
      }
      return false;
    } catch (e) {
      print('Health check failed: $e');
      return false;
    }
  }

  Future<String> getBotEventQueueStatus(String botId) async {
    try {
      final response = await _client.get(Uri.parse('$_baseUrl/bot/$botId/queue-status'));
      
      if (response.statusCode == 200) {
        final json = jsonDecode(response.body);
        return json['status'] as String;
      }
      return 'UNKNOWN';
    } catch (e) {
      print('Failed to get queue status: $e');
      return 'UNKNOWN';
    }
  }

  void dispose() {
    _client.close();
  }
}