import 'dart:convert';
import 'package:http/http.dart' as http;

/// API客户端
class ApiClient {
  static final ApiClient _instance = ApiClient._internal();
  factory ApiClient() => _instance;
  ApiClient._internal();

  String _baseUrl = 'https://api.example.com';
  String? _authToken;
  final Map<String, String> _defaultHeaders = {
    'Content-Type': 'application/json',
    'Accept': 'application/json',
  };

  String get baseUrl => _baseUrl;

  Map<String, String> get headers {
    final headers = Map<String, String>.from(_defaultHeaders);
    if (_authToken != null) {
      headers['Authorization'] = 'Bearer $_authToken';
    }
    return headers;
  }

  void setBaseUrl(String url) {
    _baseUrl = url;
  }

  void setAuthToken(String token) {
    _authToken = token;
  }

  void clearAuthToken() {
    _authToken = null;
  }

  Future<http.Response> get(
    String path, {
    Map<String, String>? queryParams,
  }) async {
    var uri = Uri.parse('$_baseUrl$path');
    if (queryParams != null) {
      uri = uri.replace(queryParameters: queryParams);
    }
    return http.get(uri, headers: headers);
  }

  Future<http.Response> post(
    String path, {
    Map<String, dynamic>? body,
  }) async {
    final uri = Uri.parse('$_baseUrl$path');
    return http.post(
      uri,
      headers: headers,
      body: body != null ? jsonEncode(body) : null,
    );
  }

  Future<http.Response> put(
    String path, {
    Map<String, dynamic>? body,
  }) async {
    final uri = Uri.parse('$_baseUrl$path');
    return http.put(
      uri,
      headers: headers,
      body: body != null ? jsonEncode(body) : null,
    );
  }

  Future<http.Response> delete(String path) async {
    final uri = Uri.parse('$_baseUrl$path');
    return http.delete(uri, headers: headers);
  }

  Future<http.StreamedRequest> createStreamRequest(
    String path, {
    Map<String, dynamic>? body,
  }) async {
    final uri = Uri.parse('$_baseUrl$path');
    final request = http.StreamedRequest('POST', uri);
    request.headers.addAll(headers);
    
    if (body != null) {
      request.sink.add(utf8.encode(jsonEncode(body)));
      request.sink.close();
    }
    
    return request;
  }

  Future<http.StreamedResponse> stream(
    String path, {
    Map<String, dynamic>? body,
  }) async {
    final request = await createStreamRequest(path, body: body);
    return request.send();
  }
}
