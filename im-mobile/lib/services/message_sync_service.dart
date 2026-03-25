import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/message_sync.dart';

class MessageSyncService {
  static const String _baseUrl = '/api/v1/sync';

  String get _userId => '1';

  Map<String, String> get _headers => {
        'Content-Type': 'application/json',
        'X-User-Id': _userId,
      };

  Future<SyncResponse> pullSync(SyncRequest request) async {
    final resp = await http.post(
      Uri.parse('$_baseUrl/pull'),
      headers: _headers,
      body: json.encode(request.toJson()),
    );
    if (resp.statusCode == 200) {
      return SyncResponse.fromJson(json.decode(resp.body));
    }
    throw Exception('Sync pull failed: ${resp.statusCode}');
  }

  Future<SyncResponse> fetchHistory(int conversationId, {int? lastId, int limit = 50}) async {
    final params = <String, String>{'limit': limit.toString()};
    if (lastId != null) params['lastId'] = lastId.toString();
    final uri = Uri.parse('$_baseUrl/history/$conversationId').replace(queryParameters: params);
    final resp = await http.get(uri, headers: _headers);
    if (resp.statusCode == 200) {
      return SyncResponse.fromJson(json.decode(resp.body));
    }
    throw Exception('Fetch history failed: ${resp.statusCode}');
  }

  Future<List<SyncCheckpoint>> fetchCheckpoints(String deviceId) async {
    final resp = await http.get(
      Uri.parse('$_baseUrl/checkpoints?deviceId=$deviceId'),
      headers: _headers,
    );
    if (resp.statusCode == 200) {
      final data = json.decode(resp.body);
      return (data['checkpoints'] as List?)
              ?.map((c) => SyncCheckpoint.fromJson(c))
              .toList() ??
          [];
    }
    throw Exception('Fetch checkpoints failed: ${resp.statusCode}');
  }

  Future<void> deleteMessageFromHistory(int messageId) async {
    final resp = await http.delete(
      Uri.parse('$_baseUrl/history/$messageId'),
      headers: _headers,
    );
    if (resp.statusCode != 200) {
      throw Exception('Delete from history failed: ${resp.statusCode}');
    }
  }
}
