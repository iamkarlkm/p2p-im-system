import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/conversation_batch_operation.dart';

class BatchOperationService {
  final String baseUrl;
  final String authToken;

  BatchOperationService({
    required this.baseUrl,
    required this.authToken,
  });

  Map<String, String> get _headers => {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $authToken',
        'X-User-Id': authToken,
      };

  Future<BatchOperationResponse> executeBatchOperation(
      BatchOperationRequest request) async {
    final response = await http.post(
      Uri.parse('$baseUrl/conversations/batch/execute'),
      headers: _headers,
      body: jsonEncode(request.toJson()),
    );

    if (response.statusCode == 200) {
      return BatchOperationResponse.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to execute batch operation: ${response.body}');
    }
  }

  Future<List<BatchOperationHistory>> getBatchOperationHistory() async {
    final response = await http.get(
      Uri.parse('$baseUrl/conversations/batch/history'),
      headers: _headers,
    );

    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);
      return data.map((e) => BatchOperationHistory.fromJson(e)).toList();
    } else {
      throw Exception('Failed to load history: ${response.body}');
    }
  }

  Future<BatchOperationResponse> batchMarkAsRead(
      List<int> conversationIds) async {
    final response = await http.post(
      Uri.parse('$baseUrl/conversations/batch/mark-read'),
      headers: _headers,
      body: jsonEncode({'conversationIds': conversationIds}),
    );

    if (response.statusCode == 200) {
      return BatchOperationResponse.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to mark as read: ${response.body}');
    }
  }

  Future<BatchOperationResponse> batchArchive(
      List<int> conversationIds) async {
    final response = await http.post(
      Uri.parse('$baseUrl/conversations/batch/archive'),
      headers: _headers,
      body: jsonEncode({'conversationIds': conversationIds}),
    );

    if (response.statusCode == 200) {
      return BatchOperationResponse.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to archive: ${response.body}');
    }
  }

  Future<BatchOperationResponse> batchDelete(
      List<int> conversationIds) async {
    final response = await http.post(
      Uri.parse('$baseUrl/conversations/batch/delete'),
      headers: _headers,
      body: jsonEncode({'conversationIds': conversationIds}),
    );

    if (response.statusCode == 200) {
      return BatchOperationResponse.fromJson(jsonDecode(response.body));
    } else {
      throw Exception('Failed to delete: ${response.body}');
    }
  }

  Future<BatchOperationResponse> batchPin(List<int> conversationIds) async {
    final request = BatchOperationRequest(
      conversationIds: conversationIds,
      operationType: BatchOperationType.pin.value,
    );
    return executeBatchOperation(request);
  }

  Future<BatchOperationResponse> batchUnpin(
      List<int> conversationIds) async {
    final request = BatchOperationRequest(
      conversationIds: conversationIds,
      operationType: BatchOperationType.unpin.value,
    );
    return executeBatchOperation(request);
  }

  Future<BatchOperationResponse> batchMute(List<int> conversationIds) async {
    final request = BatchOperationRequest(
      conversationIds: conversationIds,
      operationType: BatchOperationType.mute.value,
    );
    return executeBatchOperation(request);
  }

  Future<BatchOperationResponse> batchUnmute(
      List<int> conversationIds) async {
    final request = BatchOperationRequest(
      conversationIds: conversationIds,
      operationType: BatchOperationType.unmute.value,
    );
    return executeBatchOperation(request);
  }
}
