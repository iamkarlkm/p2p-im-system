import 'dart:convert';
import 'package:http/http.dart' as http;
import 'zkp_verifiable_computation_model.dart';

class ZKPApiClient {
  final String baseUrl;
  final http.Client client;

  ZKPApiClient({this.baseUrl = '/api/zkp', http.Client? client}) : client = client ?? http.Client();

  Future<Map<String, dynamic>> _request(String endpoint, {String method = 'GET', Map<String, dynamic>? body}) async {
    final url = Uri.parse('$baseUrl$endpoint');
    final response = await client.request(url, method: method, headers: {'Content-Type': 'application/json'}, body: body != null ? jsonEncode(body) : null);
    if (response.statusCode >= 200 && response.statusCode < 300) return jsonDecode(response.body);
    throw Exception('API Error: ${response.statusCode}');
  }

  Future<ZKPComputation?> getComputation(String computationId) async {
    try { final data = await _request('/computations/$computationId'); return ZKPComputation.fromJson(data['computation']); }
    catch (e) { return null; }
  }

  Future<List<ZKPComputation>> getUserComputations(String userId, {int limit = 20}) async {
    try { final data = await _request('/users/$userId/computations?limit=$limit'); return (data['computations'] as List).map((e) => ZKPComputation.fromJson(e)).toList(); }
    catch (e) { return []; }
  }

  Future<String?> createComputation(String userId, String computationType, {String? sessionId, String circuitType = 'GROTH16', int securityLevel = 128}) async {
    try { final data = await _request('/computations/create?userId=$userId&computationType=$computationType&circuitType=$circuitType&securityLevel=$securityLevel${sessionId != null ? '&sessionId=$sessionId' : ''}', method: 'POST'); return data['computationId']; }
    catch (e) { return null; }
  }

  Future<bool> generateProof(String computationId, {Map<String, dynamic>? publicInputs, Map<String, dynamic>? privateInputs}) async {
    try { await _request('/computations/$computationId/generate-proof', method: 'POST', body: {'public': publicInputs, 'private': privateInputs}); return true; }
    catch (e) { return false; }
  }

  Future<bool> verifyProof(String computationId) async {
    try { await _request('/computations/$computationId/verify', method: 'POST'); return true; }
    catch (e) { return false; }
  }
}

class ZKPPrivacyApiClient {
  final String baseUrl;
  final http.Client client;

  ZKPPrivacyApiClient({this.baseUrl = '/api/zkp', http.Client? client}) : client = client ?? http.Client();

  Future<Map<String, dynamic>> _request(String endpoint, {String method = 'GET', Map<String, dynamic>? body}) async {
    final url = Uri.parse('$baseUrl$endpoint');
    final response = await client.request(url, method: method, headers: {'Content-Type': 'application/json'}, body: body != null ? jsonEncode(body) : null);
    if (response.statusCode >= 200 && response.statusCode < 300) return jsonDecode(response.body);
    throw Exception('API Error: ${response.statusCode}');
  }

  Future<ZKPPrivacyProtection?> getProtection(String protectionId) async {
    try { final data = await _request('/privacy-protections/$protectionId'); return ZKPPrivacyProtection.fromJson(data['protection']); }
    catch (e) { return null; }
  }

  Future<List<ZKPPrivacyProtection>> getUserProtections(String userId, {int limit = 20}) async {
    try { final data = await _request('/users/$userId/privacy-protections?limit=$limit'); return (data['protections'] as List).map((e) => ZKPPrivacyProtection.fromJson(e)).toList(); }
    catch (e) { return []; }
  }

  Future<String?> createProtection(String userId, String protectionType, Map<String, dynamic> attributes, {DateTime? validTo}) async {
    try { final data = await _request('/privacy-protections/create', method: 'POST', body: {'userId': userId, 'protectionType': protectionType, 'attributes': attributes, 'validTo': validTo?.toIso8601String()}); return data['protectionId']; }
    catch (e) { return null; }
  }

  Future<bool> verifyProtection(String protectionId, Map<String, dynamic> disclosedAttributes, {List<Map<String, dynamic>>? predicates}) async {
    try { await _request('/privacy-protections/$protectionId/verify', method: 'POST', body: {'disclosedAttributes': disclosedAttributes, 'predicates': predicates}); return true; }
    catch (e) { return false; }
  }
}
