import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'quantum_resistant_encryption_model.dart';

/**
 * 量子抗性加密 API 服务
 * 后量子密码学密钥管理和加密操作的Dart HTTP客户端
 */

class QuantumResistantEncryptionApiService {
  final String baseUrl;
  final int timeoutMs;
  final http.Client _client;

  QuantumResistantEncryptionApiService({
    this.baseUrl = '/api',
    this.timeoutMs = 30000,
  }) : _client = http.Client();

  /**
   * 生成新的量子抗性密钥对
   */
  Future<QuantumResistantEncryptionKey> generateKeyPair(
    KeyGenerationRequest request,
  ) async {
    final queryParams = <String, String>{
      'algorithmType': request.algorithmType,
      'algorithmParameter': request.algorithmParameter,
      if (request.keyUsage != null) 'keyUsage': request.keyUsage!,
      if (request.encryptionMode != null) 'encryptionMode': request.encryptionMode!,
      if (request.securityLevel != null) 'securityLevel': request.securityLevel!,
      if (request.keySize != null) 'keySize': request.keySize!.toString(),
      if (request.expiresAt != null) 'expiresAt': request.expiresAt!,
    };

    final uri = Uri.parse('$baseUrl/v1/quantum-resistant-encryption/keys/generate')
        .replace(queryParameters: queryParams);

    final response = await _client.post(uri).timeout(Duration(milliseconds: timeoutMs));

    if (response.statusCode == 201) {
      return QuantumResistantEncryptionKey.fromJson(
        jsonDecode(utf8.decode(response.bodyBytes)),
      );
    } else {
      throw HttpException(
        'Failed to generate key pair: ${response.statusCode} - ${response.body}',
        uri: uri,
      );
    }
  }

  /**
   * 获取指定密钥
   */
  Future<QuantumResistantEncryptionKey?> getKey(String keyId) async {
    final uri = Uri.parse('$baseUrl/v1/quantum-resistant-encryption/keys/$keyId');
    final response = await _client.get(uri).timeout(Duration(milliseconds: timeoutMs));

    if (response.statusCode == 200) {
      return QuantumResistantEncryptionKey.fromJson(
        jsonDecode(utf8.decode(response.bodyBytes)),
      );
    } else if (response.statusCode == 404) {
      return null;
    } else {
      throw HttpException(
        'Failed to get key: ${response.statusCode}',
        uri: uri,
      );
    }
  }

  /**
   * 获取所有活动密钥
   */
  Future<List<QuantumResistantEncryptionKey>> getActiveKeys() async {
    final uri = Uri.parse('$baseUrl/v1/quantum-resistant-encryption/keys/active');
    final response = await _client.get(uri).timeout(Duration(milliseconds: timeoutMs));

    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(utf8.decode(response.bodyBytes));
      return jsonList.map((json) => QuantumResistantEncryptionKey.fromJson(json)).toList();
    } else {
      throw HttpException(
        'Failed to get active keys: ${response.statusCode}',
        uri: uri,
      );
    }
  }

  /**
   * 根据算法类型获取密钥
   */
  Future<List<QuantumResistantEncryptionKey>> getKeysByAlgorithm(String algorithmType) async {
    final uri = Uri.parse('$baseUrl/v1/quantum-resistant-encryption/keys/algorithm/$algorithmType');
    final response = await _client.get(uri).timeout(Duration(milliseconds: timeoutMs));

    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(utf8.decode(response.bodyBytes));
      return jsonList.map((json) => QuantumResistantEncryptionKey.fromJson(json)).toList();
    } else {
      throw HttpException(
        'Failed to get keys by algorithm: ${response.statusCode}',
        uri: uri,
      );
    }
  }

  /**
   * 根据安全级别获取密钥
   */
  Future<List<QuantumResistantEncryptionKey>> getKeysBySecurityLevel(String securityLevel) async {
    final uri = Uri.parse('$baseUrl/v1/quantum-resistant-encryption/keys/security-level/$securityLevel');
    final response = await _client.get(uri).timeout(Duration(milliseconds: timeoutMs));

    if (response.statusCode == 200) {
      final List<dynamic> jsonList = jsonDecode(utf8.decode(response.bodyBytes));
      return jsonList.map((json) => QuantumResistantEncryptionKey.fromJson(json)).toList();
    } else {
      throw HttpException(
        'Failed to get keys by security level: ${response.statusCode}',
        uri: uri,
      );
    }
  }

  /**
   * 加密数据
   */
  Future<EncryptionResponse> encryptData(
    String keyId,
    String plaintext,
    String? additionalData,
  ) async {
    final uri = Uri.parse('$baseUrl/v1/quantum-resistant-encryption/encrypt/$keyId');
    final request = EncryptionRequest(plaintext: plaintext, additionalData: additionalData);

    final response = await _client
        .post(
          uri,
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode(request.toJson()),
        )
        .timeout(Duration(milliseconds: timeoutMs));

    if (response.statusCode == 200) {
      return EncryptionResponse.fromJson(jsonDecode(utf8.decode(response.bodyBytes)));
    } else {
      throw HttpException(
        'Failed to encrypt data: ${response.statusCode} - ${response.body}',
        uri: uri,
      );
    }
  }

  /**
   * 解密数据
   */
  Future<DecryptionResponse> decryptData(
    String keyId,
    String encryptedData,
    String? additionalData,
  ) async {
    final uri = Uri.parse('$baseUrl/v1/quantum-resistant-encryption/decrypt/$keyId');
    final request = DecryptionRequest(encryptedData: encryptedData, additionalData: additionalData);

    final response = await _client
        .post(
          uri,
          headers: {'Content-Type': 'application/json'},
          body: jsonEncode(request.toJson()),
        )
        .timeout(Duration(milliseconds: timeoutMs));

    if (response.statusCode == 200) {
      return DecryptionResponse.fromJson(jsonDecode(utf8.decode(response.bodyBytes)));
    } else {
      throw HttpException(
        'Failed to decrypt data: ${response.statusCode} - ${response.body}',
        uri: uri,
      );
    }
  }

  /**
   * 撤销密钥
   */
  Future<ApiResponseMessage> revokeKey(String keyId, String reason) async {
    final uri = Uri.parse(
      '$baseUrl/v1/quantum-resistant-encryption/keys/$keyId/revoke',
    ).replace(queryParameters: {'reason': reason});

    final response = await _client.post(uri).timeout(Duration(milliseconds: timeoutMs));

    if (response.statusCode == 200) {
      return ApiResponseMessage.fromJson(jsonDecode(utf8.decode(response.bodyBytes)));
    } else {
      throw HttpException(
        'Failed to revoke key: ${response.statusCode}',
        uri: uri,
      );
    }
  }

  /**
   * 轮换密钥
   */
  Future<KeyRotationResponse> rotateKey(String keyId, String newAlgorithmParameter) async {
    final uri = Uri.parse(
      '$baseUrl/v1/quantum-resistant-encryption/keys/$keyId/rotate',
    ).replace(queryParameters: {'newAlgorithmParameter': newAlgorithmParameter});

    final response = await _client.post(uri).timeout(Duration(milliseconds: timeoutMs));

    if (response.statusCode == 200) {
      return KeyRotationResponse.fromJson(jsonDecode(utf8.decode(response.bodyBytes)));
    } else {
      throw HttpException(
        'Failed to rotate key: ${response.statusCode}',
        uri: uri,
      );
    }
  }

  /**
   * 更新密钥过期时间
   */
  Future<ApiResponseMessage> updateKeyExpiration(String keyId, String newExpiresAt) async {
    final uri = Uri.parse(
      '$baseUrl/v1/quantum-resistant-encryption/keys/$keyId/expiration',
    ).replace(queryParameters: {'newExpiresAt': newExpiresAt});

    final response = await _client.put(uri).timeout(Duration(milliseconds: timeoutMs));

    if (response.statusCode == 200) {
      return ApiResponseMessage.fromJson(jsonDecode(utf8.decode(response.bodyBytes)));
    } else {
      throw HttpException(
        'Failed to update