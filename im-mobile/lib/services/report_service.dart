import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/report.dart';

class ReportService {
  static const String _baseUrl = '/api/report';

  Future<Report?> submitReport({
    required int reportedMessageId,
    required int reportedUserId,
    required int conversationId,
    required String conversationType,
    required String reportReason,
    required String reportCategory,
    required String description,
  }) async {
    final userId = 1;
    final username = 'User';
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/submit'),
        headers: {
          'Content-Type': 'application/json',
          'X-User-Id': '$userId',
          'X-Username': username,
        },
        body: jsonEncode({
          'reportedMessageId': reportedMessageId,
          'reportedUserId': reportedUserId,
          'conversationId': conversationId,
          'conversationType': conversationType,
          'reportReason': reportReason,
          'reportCategory': reportCategory,
          'description': description,
          'evidence': '',
        }),
      );
      if (response.statusCode == 200) return Report.fromJson(jsonDecode(response.body));
    } catch (e) { }
    return null;
  }

  Future<List<Report>> getMyReports() async {
    final userId = 1;
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/my-reports'),
        headers: { 'X-User-Id': '$userId' },
      );
      if (response.statusCode == 200) {
        final List<dynamic> list = jsonDecode(response.body);
        return list.map((e) => Report.fromJson(e)).toList();
      }
    } catch (e) { }
    return [];
  }
}
