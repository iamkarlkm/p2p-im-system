/**
 * 投票服务 (Poll Service)
 * Flutter Mobile 实现
 */

import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/poll.dart';

class PollService {
  final String baseUrl;
  final String userId;
  http.Client? _client;

  PollService({
    required this.baseUrl,
    required this.userId,
  }) {
    _client = http.Client();
  }

  void dispose() {
    _client?.close();
    _client = null;
  }

  // ==================== HTTP API ====================

  Future<Poll> createPoll(CreatePollRequest request) async {
    final response = await (_client ?? http.Client()).post(
      Uri.parse('$baseUrl/poll'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode(request.toJson()),
    );
    final result = _parseResponse(response);
    return Poll.fromJson(result['data']);
  }

  Future<Poll> getPoll(String pollId) async {
    final response = await (_client ?? http.Client()).get(
      Uri.parse('$baseUrl/poll/$pollId?userId=$userId'),
    );
    final result = _parseResponse(response);
    return Poll.fromJson(result['data']);
  }

  Future<Poll> vote(String pollId, List<String> optionIds) async {
    final response = await (_client ?? http.Client()).post(
      Uri.parse('$baseUrl/poll/$pollId/vote'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'userId': userId, 'optionIds': optionIds}),
    );
    final result = _parseResponse(response);
    return Poll.fromJson(result['data']);
  }

  Future<Poll> cancelVote(String pollId) async {
    final response = await (_client ?? http.Client()).delete(
      Uri.parse('$baseUrl/poll/$pollId/vote?userId=$userId'),
    );
    final result = _parseResponse(response);
    return Poll.fromJson(result['data']);
  }

  Future<Poll> closePoll(String pollId) async {
    final response = await (_client ?? http.Client()).post(
      Uri.parse('$baseUrl/poll/$pollId/close'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'userId': userId}),
    );
    final result = _parseResponse(response);
    return Poll.fromJson(result['data']);
  }

  Future<bool> deletePoll(String pollId) async {
    final response = await (_client ?? http.Client()).delete(
      Uri.parse('$baseUrl/poll/$pollId?userId=$userId'),
    );
    final result = _parseResponse(response);
    return result['data'] ?? false;
  }

  Future<Poll> addOption(String pollId, String optionText) async {
    final response = await (_client ?? http.Client()).post(
      Uri.parse('$baseUrl/poll/$pollId/options'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({'userId': userId, 'optionText': optionText}),
    );
    final result = _parseResponse(response);
    return Poll.fromJson(result['data']);
  }

  Future<List<Poll>> getGroupPolls(String groupId) async {
    final response = await (_client ?? http.Client()).get(
      Uri.parse('$baseUrl/poll/group/$groupId?userId=$userId'),
    );
    final result = _parseResponse(response);
    return (result['data'] as List)
        .map((p) => Poll.fromJson(p))
        .toList();
  }

  Future<List<Poll>> getGroupActivePolls(String groupId) async {
    final response = await (_client ?? http.Client()).get(
      Uri.parse('$baseUrl/poll/group/$groupId/active?userId=$userId'),
    );
    final result = _parseResponse(response);
    return (result['data'] as List)
        .map((p) => Poll.fromJson(p))
        .toList();
  }

  Future<List<Poll>> getUserVotedPolls() async {
    final response = await (_client ?? http.Client()).get(
      Uri.parse('$baseUrl/poll/user/$userId/voted'),
    );
    final result = _parseResponse(response);
    return (result['data'] as List)
        .map((p) => Poll.fromJson(p))
        .toList();
  }

  // ==================== 工具方法 ====================

  Map<String, dynamic> _parseResponse(http.Response response) {
    if (response.statusCode < 200 || response.statusCode >= 300) {
      throw Exception('Request failed: ${response.statusCode}');
    }
    return jsonDecode(response.body) as Map<String, dynamic>;
  }

  /// 快捷创建投票
  Future<Poll> createQuickPoll(
    String groupId,
    String question,
    List<String> options, {
    bool anonymous = false,
    bool multiSelect = false,
    int? deadlineMinutes,
  }) async {
    final request = CreatePollRequest(
      creatorId: userId,
      groupId: groupId,
      question: question,
      optionTexts: options,
      anonymous: anonymous,
      multiSelect: multiSelect,
      deadline: deadlineMinutes != null
          ? DateTime.now().add(Duration(minutes: deadlineMinutes))
          : null,
    );
    return createPoll(request);
  }
}
