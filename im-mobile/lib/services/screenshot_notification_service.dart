import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/screenshot_notification.dart';

class ScreenshotNotificationService {
  static const String _baseUrl = '/api/screenshot';

  Future<ScreenshotEvent?> reportScreenshot(int conversationId, String conversationType) async {
    final userId = await _getUserId();
    final username = await _getUsername();

    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/report'),
        headers: {
          'Content-Type': 'application/json',
          'X-User-Id': '$userId',
          'X-Username': username,
        },
        body: jsonEncode({
          'conversationId': conversationId,
          'conversationType': conversationType,
          'deviceType': 'mobile',
          'deviceInfo': 'Flutter Mobile',
        }),
      );

      if (response.statusCode == 200) {
        return ScreenshotEvent.fromJson(jsonDecode(response.body));
      }
    } catch (e) {
      // Silently fail
    }
    return null;
  }

  Future<ScreenshotSettings> getSettings() async {
    final userId = await _getUserId();
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/settings'),
        headers: { 'X-User-Id': '$userId' },
      );
      if (response.statusCode == 200) {
        return ScreenshotSettings.fromJson(jsonDecode(response.body));
      }
    } catch (e) {
      // Return default
    }
    return ScreenshotSettings(userId: userId);
  }

  Future<ScreenshotSettings> updateSettings(ScreenshotSettings settings) async {
    final response = await http.put(
      Uri.parse('$_baseUrl/settings'),
      headers: {
        'Content-Type': 'application/json',
        'X-User-Id': '${settings.userId}',
      },
      body: jsonEncode(settings.toJson()),
    );
    return ScreenshotSettings.fromJson(jsonDecode(response.body));
  }

  Future<List<ScreenshotEvent>> getHistory({int limit = 50}) async {
    final userId = await _getUserId();
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/history?limit=$limit'),
        headers: { 'X-User-Id': '$userId' },
      );
      if (response.statusCode == 200) {
        final List<dynamic> list = jsonDecode(response.body);
        return list.map((e) => ScreenshotEvent.fromJson(e)).toList();
      }
    } catch (e) {
      // Return empty
    }
    return [];
  }

  Future<int> _getUserId() async {
    return 1;
  }

  Future<String> _getUsername() async {
    return 'User';
  }
}
