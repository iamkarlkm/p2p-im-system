// Read Receipt Service for Flutter
import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/read_receipt.dart';

class ReadReceiptService {
  final String baseUrl = 'http://localhost:8080/api/read-receipt';
  final int userId;

  ReadReceiptService({required this.userId});

  Future<ReadReceipt> markAsRead(String conversationId, String messageId) async {
    final response = await http.post(
      Uri.parse('$baseUrl/mark'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'userId': userId,
        'conversationId': conversationId,
        'messageId': messageId,
      }),
    );
    if (response.statusCode == 200) {
      return ReadReceipt.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to mark as read: ${response.statusCode}');
    }
  }

  Future<List<ReadReceipt>> markBatchAsRead(String conversationId, List<String> messageIds) async {
    final response = await http.post(
      Uri.parse('$baseUrl/mark-batch'),
      headers: {'Content-Type': 'application/json'},
      body: jsonEncode({
        'userId': userId,
        'conversationId': conversationId,
        'messageIds': messageIds,
      }),
    );
    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);
      return data.map((e) => ReadReceipt.fromJson(e)).toList();
    } else {
      throw Exception('Failed to batch mark as read: ${response.statusCode}');
    }
  }

  Future<List<ReadReceipt>> getReadReceipts(String conversationId, String messageId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/list').replace(queryParameters: {
        'conversationId': conversationId,
        'messageId': messageId,
      }),
    );
    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);
      return data.map((e) => ReadReceipt.fromJson(e)).toList();
    } else {
      throw Exception('Failed to get receipts: ${response.statusCode}');
    }
  }
}
