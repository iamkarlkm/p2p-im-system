// 2FA Service for IM Mobile
import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/two_factor_auth.dart';

class TwoFactorAuthService {
  final String baseUrl;
  final Map<String, String> _headers = {
    'Content-Type': 'application/json',
  };

  TwoFactorAuthService({this.baseUrl = ''});

  void setUserId(int userId) {
    _headers['X-User-Id'] = userId.toString();
  }

  Future<TwoFactorSetupResponse> setup2FA(TwoFactorSetupRequest request) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/v1/2fa/setup'),
      headers: _headers,
      body: jsonEncode(request.toJson()),
    );
    if (response.statusCode != 200) {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Failed to setup 2FA');
    }
    return TwoFactorSetupResponse.fromJson(jsonDecode(response.body));
  }

  Future<TwoFactorVerifyResponse> verify2FA(TwoFactorVerifyRequest request) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/v1/2fa/verify'),
      headers: _headers,
      body: jsonEncode(request.toJson()),
    );
    final data = jsonDecode(response.body);
    if (response.statusCode != 200) {
      throw Exception(data['message'] ?? 'Verification failed');
    }
    return TwoFactorVerifyResponse.fromJson(data);
  }

  Future<TwoFactorSetupResponse> enable2FA(String code) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/v1/2fa/enable'),
      headers: _headers,
      body: jsonEncode({'code': code}),
    );
    if (response.statusCode != 200) {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Failed to enable 2FA');
    }
    return TwoFactorSetupResponse.fromJson(jsonDecode(response.body));
  }

  Future<void> disable2FA(String password, String code) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/v1/2fa/disable'),
      headers: _headers,
      body: jsonEncode({'password': password, 'code': code}),
    );
    if (response.statusCode != 200) {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Failed to disable 2FA');
    }
  }

  Future<List<String>> regenerateBackupCodes(String code) async {
    final response = await http.post(
      Uri.parse('$baseUrl/api/v1/2fa/backup-codes/regenerate'),
      headers: _headers,
      body: jsonEncode({'code': code}),
    );
    if (response.statusCode != 200) {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Failed to regenerate backup codes');
    }
    return List<String>.from(jsonDecode(response.body));
  }

  Future<TwoFactorStatusResponse> getStatus() async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/v1/2fa/status'),
      headers: _headers,
    );
    if (response.statusCode != 200) {
      final error = jsonDecode(response.body);
      throw Exception(error['message'] ?? 'Failed to get 2FA status');
    }
    return TwoFactorStatusResponse.fromJson(jsonDecode(response.body));
  }

  Future<bool> check2FARequired(int userId) async {
    final response = await http.get(
      Uri.parse('$baseUrl/api/v1/2fa/check/$userId'),
      headers: _headers,
    );
    if (response.statusCode != 200) return false;
    final data = jsonDecode(response.body);
    return data['required'] ?? false;
  }

  Future<TwoFactorVerifyResponse> authenticate2FA(String code) async {
    final isBackup = code.length == 8;
    return verify2FA(TwoFactorVerifyRequest(
      code: code,
      isBackupCode: isBackup,
    ));
  }

  String parseBackupCode(String code) {
    return code.replaceAll(RegExp(r'[^0-9]'), '').substring(0, 8);
  }

  bool validateCode(String code) {
    final clean = code.replaceAll(RegExp(r'[^0-9]'), '');
    return clean.length == 6 || clean.length == 8;
  }

  bool isBackupCode(String code) {
    return code.length == 8 && int.tryParse(code) != null;
  }
}
