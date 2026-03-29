import 'dart:convert';
import 'package:http/http.dart' as http;

/// AI 聊天机器人服务 - Flutter Dart API 封装
/// 支持 OpenAI/Claude/Gemini/Custom Webhook 多平台 AI 模型

class Bot {
  final String botId;
  final String name;
  final String? description;
  final String? avatarUrl;
  final String ownerId;
  final String botType; // OPENAI/CLAUDE/GEMINI/CUSTOM/LOCAL
  final String modelName;
  final String? apiKey;
  final String? apiBaseUrl;
  final String? webhookUrl;
  final String? webhookSecret;
  final String? systemPrompt;
  final int maxTokens;
  final double temperature;
  final String status; // ACTIVE/INACTIVE/DELETED
  final bool isPublic;
  final bool enableImageGen;
  final bool enableSpeechToText;
  final int rateLimit;
  final int sessionCount;
  final int messageCount;
  final int totalTokensUsed;
  final String? accessToken;
  final DateTime createdAt;
  final DateTime? updatedAt;
  final DateTime? lastActiveAt;

  Bot({
    required this.botId,
    required this.name,
    this.description,
    this.avatarUrl,
    required this.ownerId,
    required this.botType,
    required this.modelName,
    this.apiKey,
    this.apiBaseUrl,
    this.webhookUrl,
    this.webhookSecret,
    this.systemPrompt,
    required this.maxTokens,
    required this.temperature,
    required this.status,
    required this.isPublic,
    required this.enableImageGen,
    required this.enableSpeechToText,
    required this.rateLimit,
    required this.sessionCount,
    required this.messageCount,
    required this.totalTokensUsed,
    this.accessToken,
    required this.createdAt,
    this.updatedAt,
    this.lastActiveAt,
  });

  factory Bot.fromJson(Map<String, dynamic> json) {
    return Bot(
      botId: json['botId'],
      name: json['name'],
      description: json['description'],
      avatarUrl: json['avatarUrl'],
      ownerId: json['ownerId'],
      botType: json['botType'],
      modelName: json['modelName'],
      apiKey: json['apiKey'],
      apiBaseUrl: json['apiBaseUrl'],
      webhookUrl: json['webhookUrl'],
      webhookSecret: json['webhookSecret'],
      systemPrompt: json['systemPrompt'],
      maxTokens: json['maxTokens'] ?? 4096,
      temperature: (json['temperature'] ?? 0.7).toDouble(),
      status: json['status'] ?? 'INACTIVE',
      isPublic: json['isPublic'] ?? false,
      enableImageGen: json['enableImageGen'] ?? false,
      enableSpeechToText: json['enableSpeechToText'] ?? false,
      rateLimit: json['rateLimit'] ?? 60,
      sessionCount: json['sessionCount'] ?? 0,
      messageCount: json['messageCount'] ?? 0,
      totalTokensUsed: json['totalTokensUsed'] ?? 0,
      accessToken: json['accessToken'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt']) : null,
      lastActiveAt: json['lastActiveAt'] != null ? DateTime.parse(json['lastActiveAt']) : null,
    );
  }
}

class BotSession {
  final String sessionId;
  final String botId;
  final String userId;
  final String conversationId;
  final int contextTokens;
  final int turnCount;
  final int totalTokensUsed;
  final String status; // ACTIVE/ENDED/TIMEOUT
  final String? endReason;
  final String? promptVersion;
  final DateTime createdAt;
  final DateTime lastMessageAt;
  final DateTime? endedAt;

  BotSession({
    required this.sessionId,
    required this.botId,
    required this.userId,
    required this.conversationId,
    required this.contextTokens,
    required this.turnCount,
    required this.totalTokensUsed,
    required this.status,
    this.endReason,
    this.promptVersion,
    required this.createdAt,
    required this.lastMessageAt,
    this.endedAt,
  });

  factory BotSession.fromJson(Map<String, dynamic> json) {
    return BotSession(
      sessionId: json['sessionId'],
      botId: json['botId'],
      userId: json['userId'],
      conversationId: json['conversationId'],
      contextTokens: json['contextTokens'] ?? 0,
      turnCount: json['turnCount'] ?? 0,
      totalTokensUsed: json['totalTokensUsed'] ?? 0,
      status: json['status'] ?? 'ACTIVE',
      endReason: json['endReason'],
      promptVersion: json['promptVersion'],
      createdAt: DateTime.parse(json['createdAt']),
      lastMessageAt: DateTime.parse(json['lastMessageAt']),
      endedAt: json['endedAt'] != null ? DateTime.parse(json['endedAt']) : null,
    );
  }
}

class BotStats {
  final int totalSessions;
  final int activeSessions;
  final int totalTokens;
  final double avgTurns;
  final int totalMessages;
  final int totalTokensUsed;

  BotStats({
    required this.totalSessions,
    required this.activeSessions,
    required this.totalTokens,
    required this.avgTurns,
    required this.totalMessages,
    required this.totalTokensUsed,
  });

  factory BotStats.fromJson(Map<String, dynamic> json) {
    return BotStats(
      totalSessions: json['totalSessions'] ?? 0,
      activeSessions: json['activeSessions'] ?? 0,
      totalTokens: json['totalTokens'] ?? 0,
      avgTurns: (json['avgTurns'] ?? 0).toDouble(),
      totalMessages: json['totalMessages'] ?? 0,
      totalTokensUsed: json['totalTokensUsed'] ?? 0,
    );
  }
}

class BotService {
  static const String _baseUrl = '/api/bots';

  final http.Client _client;

  BotService({http.Client? client}) : _client = client ?? http.Client();

  // ========== Bot CRUD ==========

  Future<Bot> createBot({
    required String name,
    String? description,
    String? botType,
    String? modelName,
    required String ownerId,
  }) async {
    final response = await _client.post(
      Uri.parse(_baseUrl),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'name': name,
        'description': description,
        'botType': botType,
        'modelName': modelName,
        'ownerId': ownerId,
      }),
    );
    if (response.statusCode != 200) throw Exception('创建机器人失败: ${response.statusCode}');
    return Bot.fromJson(jsonDecode(response.body));
  }

  Future<Bot> getBot(String botId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/$botId'));
    if (response.statusCode != 200) throw Exception('获取机器人失败: ${response.statusCode}');
    return Bot.fromJson(jsonDecode(response.body));
  }

  Future<List<Bot>> getMyBots(String ownerId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/my?ownerId=$ownerId'));
    if (response.statusCode != 200) throw Exception('获取我的机器人失败: ${response.statusCode}');
    final List<dynamic> data = jsonDecode(response.body);
    return data.map((json) => Bot.fromJson(json)).toList();
  }

  Future<List<Bot>> getPublicBots() async {
    final response = await _client.get(Uri.parse('$_baseUrl/public'));
    if (response.statusCode != 200) throw Exception('获取公开机器人失败: ${response.statusCode}');
    final List<dynamic> data = jsonDecode(response.body);
    return data.map((json) => Bot.fromJson(json)).toList();
  }

  Future<Bot> updateBot(String botId, Map<String, dynamic> updates) async {
    final response = await _client.put(
      Uri.parse('$_baseUrl/$botId'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(updates),
    );
    if (response.statusCode != 200) throw Exception('更新机器人失败: ${response.statusCode}');
    return Bot.fromJson(jsonDecode(response.body));
  }

  Future<void> deleteBot(String botId) async {
    final response = await _client.delete(Uri.parse('$_baseUrl/$botId'));
    if (response.statusCode != 204) throw Exception('删除机器人失败: ${response.statusCode}');
  }

  // ========== Chat ==========

  Future<Map<String, dynamic>> chatWithBot({
    required String botId,
    required String userId,
    required String conversationId,
    required String message,
  }) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/$botId/chat'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'userId': userId,
        'conversationId': conversationId,
        'message': message,
      }),
    );
    if (response.statusCode != 200) throw Exception('AI 对话失败: ${response.statusCode}');
    return jsonDecode(response.body);
  }

  // ========== Sessions ==========

  Future<BotSession> getSession(String sessionId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/sessions/$sessionId'));
    if (response.statusCode != 200) throw Exception('获取会话失败: ${response.statusCode}');
    return BotSession.fromJson(jsonDecode(response.body));
  }

  Future<List<BotSession>> getUserSessions(String userId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/sessions/user/$userId'));
    if (response.statusCode != 200) throw Exception('获取用户会话失败: ${response.statusCode}');
    final List<dynamic> data = jsonDecode(response.body);
    return data.map((json) => BotSession.fromJson(json)).toList();
  }

  Future<void> endSession(String sessionId, {String reason = 'USER_END'}) async {
    final response = await _client.post(
      Uri.parse('$_baseUrl/sessions/$sessionId/end'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'reason': reason}),
    );
    if (response.statusCode != 200) throw Exception('结束会话失败: ${response.statusCode}');
  }

  // ========== Stats ==========

  Future<BotStats> getBotStats(String botId) async {
    final response = await _client.get(Uri.parse('$_baseUrl/$botId/stats'));
    if (response.statusCode != 200) throw Exception('获取统计失败: ${response.statusCode}');
    return BotStats.fromJson(jsonDecode(response.body));
  }

  Future<List<Map<String, dynamic>>> getBotLeaderboard() async {
    final response = await _client.get(Uri.parse('$_baseUrl/leaderboard'));
    if (response.statusCode != 200) throw Exception('获取排行榜失败: ${response.statusCode}');
    final List<dynamic> data = jsonDecode(response.body);
    return data.cast<Map<String, dynamic>>();
  }

  // ========== Presets ==========

  Future<List<Map<String, String>>> getBotPresets() async {
    final response = await _client.get(Uri.parse('$_baseUrl/presets'));
    if (response.statusCode != 200) throw Exception('获取预设失败: ${response.statusCode}');
    final List<dynamic> data = jsonDecode(response.body);
    return data.cast<Map<String, String>>();
  }
}
