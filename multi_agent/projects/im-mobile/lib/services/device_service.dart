import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/device.dart';

class DeviceService {
  static const String baseUrl = '/api/devices';
  final String Function() getToken;

  DeviceService({required this.getToken});

  Map<String, String> get _headers => {
        'Content-Type': 'application/json',
        'Authorization': 'Bearer ${getToken()}',
      };

  Future<Device> registerDevice(DeviceRegistrationRequest request) async {
    final response = await http.post(
      Uri.parse('$baseUrl/register'),
      headers: _headers,
      body: jsonEncode(request.toJson()),
    );
    if (response.statusCode == 200) {
      return Device.fromJson(jsonDecode(response.body));
    }
    throw Exception('Failed to register device: ${response.statusCode}');
  }

  Future<List<Device>> getUserDevices() async {
    final response = await http.get(
      Uri.parse(baseUrl),
      headers: _headers,
    );
    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);
      return data.map((e) => Device.fromJson(e)).toList();
    }
    throw Exception('Failed to get devices: ${response.statusCode}');
  }

  Future<List<Device>> getActiveDevices() async {
    final response = await http.get(
      Uri.parse('$baseUrl/active'),
      headers: _headers,
    );
    if (response.statusCode == 200) {
      final List<dynamic> data = jsonDecode(response.body);
      return data.map((e) => Device.fromJson(e)).toList();
    }
    throw Exception('Failed to get active devices: ${response.statusCode}');
  }

  Future<DeviceStats> getDeviceStats() async {
    final response = await http.get(
      Uri.parse('$baseUrl/stats'),
      headers: _headers,
    );
    if (response.statusCode == 200) {
      return DeviceStats.fromJson(jsonDecode(response.body));
    }
    throw Exception('Failed to get device stats: ${response.statusCode}');
  }

  Future<LoginHistoryPage> getLoginHistory({int page = 0, int size = 20}) async {
    final response = await http.get(
      Uri.parse('$baseUrl/history?page=$page&size=$size'),
      headers: _headers,
    );
    if (response.statusCode == 200) {
      return LoginHistoryPage.fromJson(jsonDecode(response.body));
    }
    throw Exception('Failed to get login history: ${response.statusCode}');
  }

  Future<void> updateDevice(int deviceId, {String? deviceName, bool? isTrusted}) async {
    final body = <String, dynamic>{
      'deviceId': deviceId,
    };
    if (deviceName != null) body['deviceName'] = deviceName;
    if (isTrusted != null) body['isTrusted'] = isTrusted;

    final response = await http.put(
      Uri.parse('$baseUrl/$deviceId'),
      headers: _headers,
      body: jsonEncode(body),
    );
    if (response.statusCode != 200) {
      throw Exception('Failed to update device: ${response.statusCode}');
    }
  }

  Future<void> deactivateDevice(int deviceId) async {
    final response = await http.post(
      Uri.parse('$baseUrl/$deviceId/deactivate'),
      headers: _headers,
    );
    if (response.statusCode != 200) {
      throw Exception('Failed to deactivate device: ${response.statusCode}');
    }
  }

  Future<void> removeDevice(int deviceId) async {
    final response = await http.delete(
      Uri.parse('$baseUrl/$deviceId'),
      headers: _headers,
    );
    if (response.statusCode != 200) {
      throw Exception('Failed to remove device: ${response.statusCode}');
    }
  }

  Future<void> trustDevice(int deviceId) async {
    final response = await http.post(
      Uri.parse('$baseUrl/$deviceId/trust'),
      headers: _headers,
    );
    if (response.statusCode != 200) {
      throw Exception('Failed to trust device: ${response.statusCode}');
    }
  }

  Future<void> untrustDevice(int deviceId) async {
    final response = await http.post(
      Uri.parse('$baseUrl/$deviceId/untrust'),
      headers: _headers,
    );
    if (response.statusCode != 200) {
      throw Exception('Failed to untrust device: ${response.statusCode}');
    }
  }

  Future<void> setCurrentDevice(int deviceId) async {
    final response = await http.post(
      Uri.parse('$baseUrl/$deviceId/set-current'),
      headers: _headers,
    );
    if (response.statusCode != 200) {
      throw Exception('Failed to set current device: ${response.statusCode}');
    }
  }

  String getDeviceIcon(DeviceType type) {
    switch (type) {
      case DeviceType.desktop:
        return '💻';
      case DeviceType.mobile:
        return '📱';
      case DeviceType.tablet:
        return '📲';
      case DeviceType.web:
        return '🌐';
      case DeviceType.other:
        return '📟';
    }
  }

  String formatLastActive(DateTime timestamp) {
    final now = DateTime.now();
    final diff = now.difference(timestamp);
    if (diff.inMinutes < 1) return '刚刚';
    if (diff.inMinutes < 60) return '${diff.inMinutes}分钟前';
    if (diff.inHours < 24) return '${diff.inHours}小时前';
    if (diff.inDays < 7) return '${diff.inDays}天前';
    return '${timestamp.month}/${timestamp.day}/${timestamp.year}';
  }
}
