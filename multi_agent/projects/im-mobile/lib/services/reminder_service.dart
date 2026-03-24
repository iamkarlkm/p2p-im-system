// Message Reminder Service for IM Mobile
import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/message_reminder.dart';

class ReminderService {
  final String baseUrl;
  final Map<String, String> _headers = {
    'Content-Type': 'application/json',
  };

  ReminderService({this.baseUrl = ''});

  void setUserId(int userId) {
    _headers['X-User-Id'] = userId.toString();
  }

  Future<MessageReminderModel> createReminder(ReminderRequest request) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/v1/reminders'),
      headers: _headers,
      body: jsonEncode(request.toJson()),
    );
    if (response.statusCode != 200) {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Failed to create reminder');
    }
    return MessageReminderModel.fromJson(jsonDecode(response.body));
  }

  Future<MessageReminderModel> updateReminder(int id, ReminderRequest request) async {
    final response = await http.put(
      Uri.parse('$baseUrl/api/v1/reminders/$id'),
      headers: _headers,
      body: jsonEncode(request.toJson()),
    );
    if (response.statusCode != 200) {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Failed to update reminder');
    }
    return MessageReminderModel.fromJson(jsonDecode(response.body));
  }

  Future<void> deleteReminder(int id) async {
    final response = await http.delete(
      Uri.parse('$baseUrl/api/v1/reminders/$id'),
      headers: _headers,
    );
    if (response.statusCode != 200) {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Failed to delete reminder');
    }
  }

  Future<MessageReminderModel> dismissReminder(int id) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/v1/reminders/$id/dismiss'),
      headers: _headers,
    );
    if (response.statusCode != 200) {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Failed to dismiss reminder');
    }
    return MessageReminderModel.fromJson(jsonDecode(response.body));
  }

  Future<List<MessageReminderModel>> getUserReminders() async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/v1/reminders'),
      headers: _headers,
    );
    if (response.statusCode != 200) {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Failed to get reminders');
    }
    final List<dynamic> data = jsonDecode(response.body);
    return data.map((e) => MessageReminderModel.fromJson(e)).toList();
  }

  Future<List<MessageReminderModel>> getPendingReminders() async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/v1/reminders/pending'),
      headers: _headers,
    );
    if (response.statusCode != 200) {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Failed to get pending reminders');
    }
    final List<dynamic> data = jsonDecode(response.body);
    return data.map((e) => MessageReminderModel.fromJson(e)).toList();
  }

  Future<int> getPendingCount() async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/v1/reminders/count'),
      headers: _headers,
    );
    if (response.statusCode != 200) return 0;
    final data = jsonDecode(response.body);
    return data['count'] ?? 0;
  }

  DateTime setReminderTime(int minutesFromNow) {
    return DateTime.now().add(Duration(minutes: minutesFromNow));
  }

  String formatReminderDisplay(DateTime dateTime) {
    final now = DateTime.now();
    final diff = dateTime.difference(now);
    final minutes = diff.inMinutes;
    if (minutes < 60) return '$minutes min';
    final hours = diff.inHours;
    if (hours < 24) return '${hours}h';
    final days = diff.inDays;
    return '${days}d';
  }
}
