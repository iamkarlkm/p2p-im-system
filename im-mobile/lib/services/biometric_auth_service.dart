import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:local_auth/local_auth.dart';

class BiometricCredential {
  final String id;
  final String userId;
  final String deviceId;
  final String biometricType;
  final String publicKey;
  final String keyHandle;
  final String credentialId;
  final String deviceName;
  final String deviceOS;
  final String securityLevel;
  final bool isEnabled;
  final DateTime lastUsedAt;
  final DateTime createdAt;
  final bool backupEligible;
  final bool backupState;
  final bool cloneWarning;
  
  BiometricCredential({
    required this.id,
    required this.userId,
    required this.deviceId,
    required this.biometricType,
    required this.publicKey,
    required this.keyHandle,
    required this.credentialId,
    required this.deviceName,
    required this.deviceOS,
    required this.securityLevel,
    required this.isEnabled,
    required this.lastUsedAt,
    required this.createdAt,
    required this.backupEligible,
    required this.backupState,
    required this.cloneWarning,
  });
  
  factory BiometricCredential.fromJson(Map<String, dynamic> json) {
    return BiometricCredential(
      id: json['id'],
      userId: json['userId'],
      deviceId: json['deviceId'],
      biometricType: json['biometricType'],
      publicKey: json['publicKey'],
      keyHandle: json['keyHandle'],
      credentialId: json['credentialId'],
      deviceName: json['deviceName'],
      deviceOS: json['deviceOS'],
      securityLevel: json['securityLevel'],
      isEnabled: json['isEnabled'],
      lastUsedAt: DateTime.parse(json['lastUsedAt']),
      createdAt: DateTime.parse(json['createdAt']),
      backupEligible: json['backupEligible'],
      backupState: json['backupState'],
      cloneWarning: json['cloneWarning'],
    );
  }
  
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'deviceId': deviceId,
      'biometricType': biometricType,
      'publicKey': publicKey,
      'keyHandle': keyHandle,
      'credentialId': credentialId,
      'deviceName': deviceName,
      'deviceOS': deviceOS,
      'securityLevel': securityLevel,
      'isEnabled': isEnabled,
      'lastUsedAt': lastUsedAt.toIso8601String(),
      'createdAt': createdAt.toIso8601String(),
      'backupEligible': backupEligible,
      'backupState': backupState,
      'cloneWarning': cloneWarning,
    };
  }
}

class AuthenticationResult {
  final bool success;
  final String? userId;
  final String? errorMessage;
  final String? warning;
  
  AuthenticationResult({
    required this.success,
    this.userId,
    this.errorMessage,
    this.warning,
  });
  
  factory AuthenticationResult.fromJson(Map<String, dynamic> json) {
    return AuthenticationResult(
      success: json['success'],
      userId: json['userId'],
      errorMessage: json['errorMessage'],
      warning: json['warning'],
    );
  }
}

class BiometricAuthService {
  static final BiometricAuthService _instance = BiometricAuthService._internal();
  final LocalAuthentication _localAuth = LocalAuthentication();
  final String _baseUrl = 'http://localhost:8080/api/v1/biometric-auth';
  
  factory BiometricAuthService() {
    return _instance;
  }
  
  BiometricAuthService._internal();
  
  // Platform detection
  String detectPlatform() {
    // This would be implemented with platform detection logic
    // For now, return a placeholder
    return 'mobile';
  }
  
  Future<List<String>> detectBiometricCapability() async {
    List<String> capabilities = [];
    
    try {
      // Check for local biometric support
      final bool canCheckBiometrics = await _localAuth.canCheckBiometrics;
      final List<BiometricType> availableBiometrics = await _localAuth.getAvailableBiometrics();
      
      if (canCheckBiometrics) {
        for (var biometric in availableBiometrics) {
          switch (biometric) {
            case BiometricType.face:
              capabilities.add('FACE_ID');
              break;
            case BiometricType.fingerprint:
              capabilities.add('FINGERPRINT');
              break;
            case BiometricType.iris:
              capabilities.add('IRIS');
              break;
            case BiometricType.strong:
              capabilities.add('STRONG_BIOMETRIC');
              break;
            case BiometricType.weak:
              capabilities.add('WEAK_BIOMETRIC');
              break;
          }
        }
      }
    } catch (e) {
      print('Error detecting biometric capability: $e');
    }
    
    return capabilities;
  }
  
  // Biometric authentication
  Future<AuthenticationResult> authenticateWithBiometric({
    required String reason,
    bool stickyAuth = true,
    bool sensitiveTransaction = true,
  }) async {
    try {
      final bool didAuthenticate = await _localAuth.authenticate(
        localizedReason: reason,
        options: AuthenticationOptions(
          stickyAuth: stickyAuth,
          sensitiveTransaction: sensitiveTransaction,
          biometricOnly: true,
        ),
      );
      
      if (didAuthenticate) {
        return AuthenticationResult(
          success: true,
          userId: 'authenticated-user', // In real app, get from secure storage
        );
      } else {
        return AuthenticationResult(
          success: false,
          errorMessage: 'Authentication failed',
        );
      }
    } catch (e) {
      print('Biometric authentication error: $e');
      return AuthenticationResult(
        success: false,
        errorMessage: 'Authentication error: $e',
      );
    }
  }
  
  // Server communication
  Future<BiometricCredential> registerBiometric(Map<String, dynamic> data) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/register'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(data),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return BiometricCredential.fromJson(responseData['data']);
      } else {
        throw Exception('Failed to register biometric: ${response.statusCode}');
      }
    } catch (e) {
      print('Registration error: $e');
      throw Exception('Registration failed: $e');
    }
  }
  
  Future<AuthenticationResult> authenticateWithServer(Map<String, dynamic> data) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/authenticate'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode(data),
      );
      
      if (response.statusCode == 200 || response.statusCode == 401) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return AuthenticationResult.fromJson(responseData['data']);
      } else {
        throw Exception('Authentication failed: ${response.statusCode}');
      }
    } catch (e) {
      print('Server authentication error: $e');
      return AuthenticationResult(
        success: false,
        errorMessage: 'Server authentication failed: $e',
      );
    }
  }
  
  Future<List<BiometricCredential>> getUserBiometrics(String userId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/user/$userId'),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        final List<dynamic> biometricsJson = responseData['data'];
        return biometricsJson.map((json) => BiometricCredential.fromJson(json)).toList();
      } else {
        throw Exception('Failed to get biometrics: ${response.statusCode}');
      }
    } catch (e) {
      print('Get biometrics error: $e');
      throw Exception('Failed to get biometrics: $e');
    }
  }
  
  Future<List<BiometricCredential>> getEnabledBiometrics(String userId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/user/$userId/enabled'),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        final List<dynamic> biometricsJson = responseData['data'];
        return biometricsJson.map((json) => BiometricCredential.fromJson(json)).toList();
      } else {
        throw Exception('Failed to get enabled biometrics: ${response.statusCode}');
      }
    } catch (e) {
      print('Get enabled biometrics error: $e');
      throw Exception('Failed to get enabled biometrics: $e');
    }
  }
  
  Future<bool> enableBiometric(String id, bool enable) async {
    try {
      final response = await http.put(
        Uri.parse('$_baseUrl/$id/enable?enable=$enable'),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return responseData['success'];
      } else {
        throw Exception('Failed to enable biometric: ${response.statusCode}');
      }
    } catch (e) {
      print('Enable biometric error: $e');
      throw Exception('Failed to enable biometric: $e');
    }
  }
  
  Future<bool> deleteBiometric(String id) async {
    try {
      final response = await http.delete(
        Uri.parse('$_baseUrl/$id'),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return responseData['success'];
      } else {
        throw Exception('Failed to delete biometric: ${response.statusCode}');
      }
    } catch (e) {
      print('Delete biometric error: $e');
      throw Exception('Failed to delete biometric: $e');
    }
  }
  
  Future<int> deleteAllUserBiometrics(String userId) async {
    try {
      final response = await http.delete(
        Uri.parse('$_baseUrl/user/$userId'),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return responseData['data']['count'];
      } else {
        throw Exception('Failed to delete user biometrics: ${response.statusCode}');
      }
    } catch (e) {
      print('Delete user biometrics error: $e');
      throw Exception('Failed to delete user biometrics: $e');
    }
  }
  
  // Security
  Future<List<BiometricCredential>> getClonedBiometrics(String userId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/user/$userId/cloned'),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        final List<dynamic> biometricsJson = responseData['data'];
        return biometricsJson.map((json) => BiometricCredential.fromJson(json)).toList();
      } else {
        throw Exception('Failed to get cloned biometrics: ${response.statusCode}');
      }
    } catch (e) {
      print('Get cloned biometrics error: $e');
      throw Exception('Failed to get cloned biometrics: $e');
    }
  }
  
  Future<bool> markAsCloned(String id, bool cloned) async {
    try {
      final response = await http.put(
        Uri.parse('$_baseUrl/$id/clone-warning?cloned=$cloned'),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return responseData['success'];
      } else {
        throw Exception('Failed to mark as cloned: ${response.statusCode}');
      }
    } catch (e) {
      print('Mark as cloned error: $e');
      throw Exception('Failed to mark as cloned: $e');
    }
  }
  
  Future<bool> disableAllUserBiometrics(String userId) async {
    try {
      final response = await http.put(
        Uri.parse('$_baseUrl/user/$userId/disable-all'),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return responseData['success'];
      } else {
        throw Exception('Failed to disable all biometrics: ${response.statusCode}');
      }
    } catch (e) {
      print('Disable all biometrics error: $e');
      throw Exception('Failed to disable all biometrics: $e');
    }
  }
  
  // Statistics
  Future<int> countUserBiometrics(String userId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/user/$userId/count'),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return responseData['data']['count'];
      } else {
        throw Exception('Failed to count biometrics: ${response.statusCode}');
      }
    } catch (e) {
      print('Count biometrics error: $e');
      throw Exception('Failed to count biometrics: $e');
    }
  }
  
  Future<List<String>> getUserBiometricTypes(String userId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/user/$userId/types'),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return List<String>.from(responseData['data']);
      } else {
        throw Exception('Failed to get biometric types: ${response.statusCode}');
      }
    } catch (e) {
      print('Get biometric types error: $e');
      throw Exception('Failed to get biometric types: $e');
    }
  }
  
  // FIDO2 specific
  Future<BiometricCredential> getFido2Credential(String credentialId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/fido2/credential/$credentialId'),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return BiometricCredential.fromJson(responseData['data']);
      } else {
        throw Exception('Failed to get FIDO2 credential: ${response.statusCode}');
      }
    } catch (e) {
      print('Get FIDO2 credential error: $e');
      throw Exception('Failed to get FIDO2 credential: $e');
    }
  }
  
  Future<List<BiometricCredential>> getResidentKeyCredentials(String userId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/user/$userId/fido2/resident-keys'),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        final List<dynamic> biometricsJson = responseData['data'];
        return biometricsJson.map((json) => BiometricCredential.fromJson(json)).toList();
      } else {
        throw Exception('Failed to get resident key credentials: ${response.statusCode}');
      }
    } catch (e) {
      print('Get resident key credentials error: $e');
      throw Exception('Failed to get resident key credentials: $e');
    }
  }
  
  // Utility methods
  Future<bool> isBiometricAvailable() async {
    try {
      return await _localAuth.canCheckBiometrics;
    } catch (e) {
      print('Error checking biometric availability: $e');
      return false;
    }
  }
  
  Future<void> authenticateForAction(String reason) async {
    final result = await authenticateWithBiometric(reason: reason);
    if (!result.success) {
      throw Exception('Biometric authentication required: ${result.errorMessage}');
    }
  }
  
  // Health check
  Future<Map<String, dynamic>> healthCheck() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/health'),
      );
      
      if (response.statusCode == 200) {
        final Map<String, dynamic> responseData = jsonDecode(response.body);
        return responseData['data'];
      } else {
        throw Exception('Health check failed: ${response.statusCode}');
      }
    } catch (e) {
      print('Health check error: $e');
      throw Exception('Health check failed: $e');
    }
  }
}