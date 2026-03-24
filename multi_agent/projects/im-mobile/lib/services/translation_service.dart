import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/message_translation.dart';

class TranslationService {
  static const String _baseUrl = '/api/translation';
  final String _token;

  TranslationService(this._token);

  Map<String, String> get _headers => {
    'Authorization': 'Bearer $_token',
    'Content-Type': 'application/json',
  };

  Future<MessageTranslation> translate({
    required int messageId,
    required int userId,
    required String text,
    required String targetLang,
    String? sourceLang,
    bool autoTranslate = false,
  }) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/translate'),
      headers: _headers,
      body: jsonEncode({
        'messageId': messageId,
        'userId': userId,
        'text': text,
        'targetLang': targetLang,
        'sourceLang': sourceLang,
        'autoTranslate': autoTranslate,
      }),
    );
    final data = jsonDecode(utf8.decode(response.bodyBytes));
    return MessageTranslation.fromJson(data['data']);
  }

  Future<List<MessageTranslation>> batchTranslate(List<Map<String, dynamic>> requests) async {
    final response = await http.post(
      Uri.parse('$_baseUrl/translate/batch'),
      headers: _headers,
      body: jsonEncode(requests),
    );
    final data = jsonDecode(utf8.decode(response.bodyBytes));
    return (data['data'] as List).map((e) => MessageTranslation.fromJson(e)).toList();
  }

  Future<TranslationSettings> getSettings(int userId) async {
    final response = await http.get(
      Uri.parse('$_baseUrl/settings').replace(queryParameters: {'userId': userId.toString()}),
      headers: _headers,
    );
    final data = jsonDecode(utf8.decode(response.bodyBytes));
    return TranslationSettings.fromJson(data['data']);
  }

  Future<TranslationSettings> updateSettings(int userId, TranslationSettings settings) async {
    final response = await http.put(
      Uri.parse('$_baseUrl/settings').replace(queryParameters: {'userId': userId.toString()}),
      headers: _headers,
      body: jsonEncode(settings.toJson()),
    );
    final data = jsonDecode(utf8.decode(response.bodyBytes));
    return TranslationSettings.fromJson(data['data']);
  }
}
