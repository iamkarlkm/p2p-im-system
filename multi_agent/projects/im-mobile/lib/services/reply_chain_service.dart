// Reply Chain Service for im-mobile

import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/message_reply_chain.dart';

class ReplyChainService {
  static const String _baseUrl = 'http://localhost:8080/api/reply-chain';
  final String userId;
  final String nickname;

  ReplyChainService({required this.userId, required this.nickname});

  Map<String, String> get _headers => {
        'Content-Type': 'application/json',
        'X-User-Id': userId,
        'X-User-Nickname': nickname,
      };

  Future<MessageReplyChain> createReplyChain(ReplyChainRequest request) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/create'),
      headers: _headers,
      body: jsonEncode(request.toJson()),
    );
    if (response.statusCode == 200) {
      return MessageReplyChain.fromJson(jsonDecode(response.body));
    }
    throw Exception('Failed to create reply chain: ${response.body}');
  }

  Future<MessageReplyChain> getReplyChain(int chainId) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/$chainId'),
      headers: _headers,
    );
    if (response.statusCode == 200) {
      return MessageReplyChain.fromJson(jsonDecode(response.body));
    }
    throw Exception('Failed to get reply chain');
  }

  Future<List<MessageReplyChain>> getConversationReplyChains(int conversationId) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/conversation/$conversationId'),
      headers: _headers,
    );
    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);
      return data.map((e) => MessageReplyChain.fromJson(e)).toList();
    }
    throw Exception('Failed to get conversation chains');
  }

  Future<MessageReplyChain> getBranchTree(int rootMessageId) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/branch/$rootMessageId'),
      headers: _headers,
    );
    if (response.statusCode == 200) {
      return MessageReplyChain.fromJson(jsonDecode(response.body));
    }
    throw Exception('Failed to get branch tree');
  }

  Future<void> markMessageDeleted(int messageId) async {
    final response = await http.put(
      Uri.parse('$_baseUrl/message/$messageId/deleted'),
      headers: _headers,
    );
    if (response.statusCode != 200) {
      throw Exception('Failed to mark message deleted');
    }
  }

  Future<void> deleteChain(int chainId) async {
    final response = await http.delete(
      Uri.parse('$_baseUrl/$chainId'),
      headers: _headers,
    );
    if (response.statusCode != 200) {
      throw Exception('Failed to delete chain');
    }
  }

  int calculateMaxDepth(MessageReplyChain chain) {
    if (chain.branchNodes.isEmpty) return chain.depth;
    int maxChild = 0;
    for (var node in chain.branchNodes) {
      final childDepth = calculateMaxDepthFromNode(node);
      if (childDepth > maxChild) maxChild = childDepth;
    }
    return maxChild > chain.depth ? maxChild : chain.depth;
  }

  int calculateMaxDepthFromNode(ReplyChainNode node) {
    return 0;
  }
}
