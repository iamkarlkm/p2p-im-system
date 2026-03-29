import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/dnd_settings.dart';

class DndSettingsService {
  static const String _baseUrl = '/api/v1/dnd';

  String get _userId => '1';

  Map<String, String> get _headers => {
        'Content-Type': 'application/json',
        'X-User-Id': _userId,
      };

  Future<DndSettings> fetchSettings() async {
    final resp = await http.get(
      Uri.parse('$_baseUrl/settings'),
      headers: _headers,
    );
    if (resp.statusCode == 200) {
      return DndSettings.fromJson(json.decode(resp.body));
    }
    throw Exception('Failed to fetch DND settings: ${resp.statusCode}');
  }

  Future<DndSettings> saveSettings(DndSettings settings) async {
    final resp = await http.post(
      Uri.parse('$_baseUrl/settings'),
      headers: _headers,
      body: json.encode(settings.toJson()),
    );
    if (resp.statusCode == 200) {
      return DndSettings.fromJson(json.decode(resp.body));
    }
    throw Exception('Failed to save DND settings: ${resp.statusCode}');
  }

  Future<DndStatus> fetchStatus() async {
    final resp = await http.get(
      Uri.parse('$_baseUrl/status'),
      headers: _headers,
    );
    if (resp.statusCode == 200) {
      return DndStatus.fromJson(json.decode(resp.body));
    }
    throw Exception('Failed to fetch DND status: ${resp.statusCode}');
  }

  Future<void> deleteSettings() async {
    final resp = await http.delete(
      Uri.parse('$_baseUrl/settings'),
      headers: _headers,
    );
    if (resp.statusCode != 200) {
      throw Exception('Failed to delete DND settings: ${resp.statusCode}');
    }
  }

  String formatRepeatDays(String repeatDays) {
    const dayNames = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日'];
    final days = repeatDays
        .split(',')
        .map((d) => int.tryParse(d.trim()))
        .where((d) => d != null && d >= 1 && d <= 7)
        .cast<int>()
        .toList();
    if (days.length == 7) return '每天';
    if (days.length == 5 && !days.contains(6) && !days.contains(7)) return '工作日';
    if (days.length == 2 && days.contains(6) && days.contains(7)) return '周末';
    return days.map((d) => dayNames[d]).join('、');
  }
}
