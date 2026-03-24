import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/group_call.dart';

class GroupCallService {
  final String baseUrl;
  final String authToken;

  GroupCallService({required this.baseUrl, required this.authToken});

  Map<String, String> get _headers => {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer $authToken',
        'X-User-Id': authToken,
      };

  Future<GroupCall> createCall(int conversationId, String callType) async {
    final response = await http.post(
      Uri.parse('$baseUrl/calls/group/create'),
      headers: _headers,
      body: jsonEncode({'conversationId': conversationId, 'callType': callType}),
    );
    return GroupCall.fromJson(jsonDecode(response.body));
  }

  Future<void> joinCall(int callId) async {
    await http.post(
      Uri.parse('$baseUrl/calls/group/$callId/join'),
      headers: _headers,
    );
  }

  Future<void> leaveCall(int callId) async {
    await http.post(
      Uri.parse('$baseUrl/calls/group/$callId/leave'),
      headers: _headers,
    );
  }

  Future<void> toggleMute(int callId) async {
    await http.post(
      Uri.parse('$baseUrl/calls/group/$callId/mute'),
      headers: _headers,
    );
  }

  Future<void> toggleVideo(int callId) async {
    await http.post(
      Uri.parse('$baseUrl/calls/group/$callId/video'),
      headers: _headers,
    );
  }

  Future<void> toggleScreenShare(int callId, bool enable) async {
    await http.post(
      Uri.parse('$baseUrl/calls/group/$callId/screen-share'),
      headers: _headers,
      body: jsonEncode({'enable': enable}),
    );
  }

  Future<void> endCall(int callId) async {
    await http.post(
      Uri.parse('$baseUrl/calls/group/$callId/end'),
      headers: _headers,
    );
  }
}
