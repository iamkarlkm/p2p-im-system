/// 身份指纹验证服务
/// 处理安全码验证、二维码扫描、密钥变更通知等移动端逻辑

import 'dart:convert';
import 'dart:math';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:device_info_plus/device_info_plus.dart';
import 'package:connectivity_plus/connectivity_plus.dart';
import 'package:shared_preferences/shared_preferences.dart';
import 'package:web_socket_channel/web_socket_channel.dart';

/// 指纹验证实体
class FingerprintVerification {
  final int id;
  final String type;
  final String status;
  final String? verificationCode;
  final DateTime? expiresAt;
  final DateTime? verifiedAt;
  final String? deviceId;
  final String? deviceName;
  final String? deviceType;
  final String? qrImageUrl;
  final DateTime createdAt;
  final DateTime updatedAt;

  FingerprintVerification({
    required this.id,
    required this.type,
    required this.status,
    this.verificationCode,
    this.expiresAt,
    this.verifiedAt,
    this.deviceId,
    this.deviceName,
    this.deviceType,
    this.qrImageUrl,
    required this.createdAt,
    required this.updatedAt,
  });

  factory FingerprintVerification.fromJson(Map<String, dynamic> json) {
    return FingerprintVerification(
      id: json['id'] as int,
      type: json['type'] as String,
      status: json['status'] as String,
      verificationCode: json['verificationCode'],
      expiresAt: json['expiresAt'] != null ? DateTime.parse(json['expiresAt']) : null,
      verifiedAt: json['verifiedAt'] != null ? DateTime.parse(json['verifiedAt']) : null,
      deviceId: json['deviceId'],
      deviceName: json['deviceName'],
      deviceType: json['deviceType'],
      qrImageUrl: json['qrImageUrl'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'type': type,
      'status': status,
      'verificationCode': verificationCode,
      'expiresAt': expiresAt?.toIso8601String(),
      'verifiedAt': verifiedAt?.toIso8601String(),
      'deviceId': deviceId,
      'deviceName': deviceName,
      'deviceType': deviceType,
      'qrImageUrl': qrImageUrl,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
    };
  }
}

/// 安全码请求参数
class SafetyCodeRequest {
  final String? deviceId;
  final String? deviceName;
  final String? ipAddress;
  final String? userAgent;

  SafetyCodeRequest({
    this.deviceId,
    this.deviceName,
    this.ipAddress,
    this.userAgent,
  });

  Map<String, dynamic> toJson() {
    return {
      if (deviceId != null) 'deviceId': deviceId,
      if (deviceName != null) 'deviceName': deviceName,
      if (ipAddress != null) 'ipAddress': ipAddress,
      if (userAgent != null) 'userAgent': userAgent,
    };
  }
}

/// 安全码验证参数
class SafetyCodeVerify {
  final String verificationCode;
  final String deviceId;

  SafetyCodeVerify({
    required this.verificationCode,
    required this.deviceId,
  });

  Map<String, dynamic> toJson() {
    return {
      'verificationCode': verificationCode,
      'deviceId': deviceId,
    };
  }
}

/// 二维码请求参数
class QrCodeRequest {
  final String deviceId;
  final String deviceName;

  QrCodeRequest({
    required this.deviceId,
    required this.deviceName,
  });

  Map<String, dynamic> toJson() {
    return {
      'deviceId': deviceId,
      'deviceName': deviceName,
    };
  }
}

/// 二维码验证参数
class QrCodeVerify {
  final String qrData;
  final String scanningDeviceId;

  QrCodeVerify({
    required this.qrData,
    required this.scanningDeviceId,
  });

  Map<String, dynamic> toJson() {
    return {
      'qrData': qrData,
      'scanningDeviceId': scanningDeviceId,
    };
  }
}

/// 密钥变更通知参数
class KeyChangeNotify {
  final String deviceId;
  final String keyType;
  final String changeReason;

  KeyChangeNotify({
    required this.deviceId,
    required this.keyType,
    required this.changeReason,
  });

  Map<String, dynamic> toJson() {
    return {
      'deviceId': deviceId,
      'keyType': keyType,
      'changeReason': changeReason,
    };
  }
}

/// 身份指纹验证服务
class IdentityFingerprintService {
  static final IdentityFingerprintService _instance = IdentityFingerprintService._internal();
  
  factory IdentityFingerprintService() {
    return _instance;
  }
  
  IdentityFingerprintService._internal();
  
  final String _baseUrl = 'https://api.example.com/api/security/fingerprint';
  late String _authToken;
  final DeviceInfoPlugin _deviceInfo = DeviceInfoPlugin();
  
  /// 初始化服务
  Future<void> initialize(String authToken) async {
    _authToken = authToken;
    await _setupDeviceInfo();
  }
  
  /// 请求安全码验证
  Future<Map<String, dynamic>> requestSafetyCodeVerification(SafetyCodeRequest request) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/safety-code/request'),
        headers: _buildHeaders(),
        body: jsonEncode(request.toJson()),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('请求安全码验证失败: ${response.statusCode}');
      }
    } catch (e) {
      debugPrint('请求安全码验证失败: $e');
      rethrow;
    }
  }
  
  /// 验证安全码
  Future<Map<String, dynamic>> verifySafetyCode(SafetyCodeVerify verify) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/safety-code/verify'),
        headers: _buildHeaders(),
        body: jsonEncode(verify.toJson()),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        final errorData = jsonDecode(response.body);
        throw Exception(errorData['error'] ?? '验证安全码失败');
      }
    } catch (e) {
      debugPrint('验证安全码失败: $e');
      rethrow;
    }
  }
  
  /// 请求二维码验证
  Future<Map<String, dynamic>> requestQrCodeVerification(QrCodeRequest request) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/qr-code/request'),
        headers: _buildHeaders(),
        body: jsonEncode(request.toJson()),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('请求二维码验证失败: ${response.statusCode}');
      }
    } catch (e) {
      debugPrint('请求二维码验证失败: $e');
      rethrow;
    }
  }
  
  /// 验证二维码扫描
  Future<Map<String, dynamic>> verifyQrCodeScan(QrCodeVerify verify) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/qr-code/verify'),
        headers: _buildHeaders(),
        body: jsonEncode(verify.toJson()),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        final errorData = jsonDecode(response.body);
        throw Exception(errorData['error'] ?? '验证二维码扫描失败');
      }
    } catch (e) {
      debugPrint('验证二维码扫描失败: $e');
      rethrow;
    }
  }
  
  /// 发送密钥变更通知
  Future<Map<String, dynamic>> notifyKeyChange(KeyChangeNotify notify) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/key-change/notify'),
        headers: _buildHeaders(),
        body: jsonEncode(notify.toJson()),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('发送密钥变更通知失败: ${response.statusCode}');
      }
    } catch (e) {
      debugPrint('发送密钥变更通知失败: $e');
      rethrow;
    }
  }
  
  /// 获取验证历史
  Future<Map<String, dynamic>> getVerificationHistory({int limit = 10}) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/history').replace(queryParameters: {'limit': limit.toString()}),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('获取验证历史失败: ${response.statusCode}');
      }
    } catch (e) {
      debugPrint('获取验证历史失败: $e');
      rethrow;
    }
  }
  
  /// 获取待处理验证
  Future<Map<String, dynamic>> getPendingVerifications() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/pending'),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('获取待处理验证失败: ${response.statusCode}');
      }
    } catch (e) {
      debugPrint('获取待处理验证失败: $e');
      rethrow;
    }
  }
  
  /// 撤销指纹验证
  Future<Map<String, dynamic>> revokeFingerprint(int fingerprintId, String reason) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/$fingerprintId/revoke').replace(queryParameters: {'reason': reason}),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('撤销指纹验证失败: ${response.statusCode}');
      }
    } catch (e) {
      debugPrint('撤销指纹验证失败: $e');
      rethrow;
    }
  }
  
  /// 获取验证统计
  Future<Map<String, dynamic>> getVerificationStats() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/stats'),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('获取验证统计失败: ${response.statusCode}');
      }
    } catch (e) {
      debugPrint('获取验证统计失败: $e');
      rethrow;
    }
  }
  
  /// 检查验证状态
  Future<Map<String, dynamic>> checkVerificationStatus(int fingerprintId) async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/$fingerprintId/status'),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('检查验证状态失败: ${response.statusCode}');
      }
    } catch (e) {
      debugPrint('检查验证状态失败: $e');
      rethrow;
    }
  }
  
  /// 重新发送验证码
  Future<Map<String, dynamic>> resendVerificationCode(int fingerprintId) async {
    try {
      final response = await http.post(
        Uri.parse('$_baseUrl/$fingerprintId/resend'),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('重新发送验证码失败: ${response.statusCode}');
      }
    } catch (e) {
      debugPrint('重新发送验证码失败: $e');
      rethrow;
    }
  }
  
  /// 获取支持的验证方法
  Future<Map<String, dynamic>> getSupportedMethods() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/methods'),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('获取支持的验证方法失败: ${response.statusCode}');
      }
    } catch (e) {
      debugPrint('获取支持的验证方法失败: $e');
      rethrow;
    }
  }
  
  /// 健康检查
  Future<Map<String, dynamic>> healthCheck() async {
    try {
      final response = await http.get(
        Uri.parse('$_baseUrl/health'),
        headers: _buildHeaders(),
      );
      
      if (response.statusCode == 200) {
        return jsonDecode(response.body);
      } else {
        throw Exception('健康检查失败: ${response.statusCode}');
      }
    } catch (e) {
      debugPrint('健康检查失败: $e');
      rethrow;
    }
  }
  
  /// 生成设备ID
  Future<String> generateDeviceId() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final savedDeviceId = prefs.getString('device_id');
      
      if (savedDeviceId != null) {
        return savedDeviceId;
      }
      
      // 生成基于设备信息的设备ID
      String deviceInfoString;
      
      if (defaultTargetPlatform == TargetPlatform.android) {
        final androidInfo = await _deviceInfo.androidInfo;
        deviceInfoString = '${androidInfo.model}-${androidInfo.id}-${androidInfo.brand}';
      } else if (defaultTargetPlatform == TargetPlatform.iOS) {
        final iosInfo = await _deviceInfo.iosInfo;
        deviceInfoString = '${iosInfo.model}-${iosInfo.identifierForVendor}';
      } else {
        deviceInfoString = '${DateTime.now().millisecondsSinceEpoch}-${Random().nextInt(1000000)}';
      }
      
      // 简单哈希
      var hash = 0;
      for (var i = 0; i < deviceInfoString.length; i++) {
        final char = deviceInfoString.codeUnitAt(i);
        hash = ((hash << 5) - hash) + char;
        hash = hash & hash; // 转换为32位整数
      }
      
      final deviceId = 'mobile_device_${hash.abs()}';
      await prefs.setString('device_id', deviceId);
      
      return deviceId;
    } catch (e) {
      debugPrint('生成设备ID失败: $e');
      // 使用备用方法
      return 'mobile_device_${DateTime.now().millisecondsSinceEpoch}_${Random().nextInt(1000000)}';
    }
  }
  
  /// 获取设备信息
  Future<Map<String, dynamic>> getDeviceInfo() async {
    try {
      final deviceId = await generateDeviceId();
      final prefs = await SharedPreferences.getInstance();
      final deviceName = prefs.getString('device_name') ?? await _getDefaultDeviceName();
      final deviceType = 'MOBILE';
      
      // 获取网络信息
      final connectivity = await Connectivity().checkConnectivity();
      final networkType = connectivity.name;
      
      return {
        'deviceId': deviceId,
        'deviceName': deviceName,
        'deviceType': deviceType,
        'networkType': networkType,
        'platform': defaultTargetPlatform.name,
      };
    } catch (e) {
      debugPrint('获取设备信息失败: $e');
      return {
        'deviceId': 'unknown_device',
        'deviceName': 'Mobile Device',
        'deviceType': 'MOBILE',
        'networkType': 'unknown',
        'platform': 'unknown',
      };
    }
  }
  
  /// 保存设备名称
  Future<void> saveDeviceName(String name) async {
    try {
      final prefs = await SharedPreferences.getInstance();
      await prefs.setString('device_name', name);
    } catch (e) {
      debugPrint('保存设备名称失败: $e');
    }
  }
  
  /// 生成验证码
  String generateVerificationCode() {
    final random = Random();
    return '${random.nextInt(9)+1}${random.nextInt(10)}${random.nextInt(10)}${random.nextInt(10)}${random.nextInt(10)}${random.nextInt(10)}';
  }
  
  /// 生成二维码数据
  String generateQrData() {
    final timestamp = DateTime.now().millisecondsSinceEpoch;
    final random = Random().nextInt(100000000);
    return 'IM_QR_${timestamp}_$random';
  }
  
  /// 验证二维码数据格式
  bool validateQrData(String qrData) {
    return qrData.startsWith('IM_QR_') || qrData.startsWith('IM_VERIFY_');
  }
  
  /// 构建请求头
  Map<String, String> _buildHeaders() {
    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $_authToken',
      'User-Agent': 'IM-Mobile-App',
    };
  }
  
  /// 设置设备信息
  Future<void> _setupDeviceInfo() async {
    try {
      // 确保设备ID已生成
      await generateDeviceId();
    } catch (e) {
      debugPrint('设置设备信息失败: $e');
    }
  }
  
  /// 获取默认设备名称
  Future<String> _getDefaultDeviceName() async {
    try {
      if (defaultTargetPlatform == TargetPlatform.android) {
        final androidInfo = await _deviceInfo.androidInfo;
        return '${androidInfo.brand} ${androidInfo.model}';
      } else if (defaultTargetPlatform == TargetPlatform.iOS) {
        final iosInfo = await _deviceInfo.iosInfo;
        return '${iosInfo.model}';
      } else {
        return 'Mobile Device';
      }
    } catch (e) {
      debugPrint('获取默认设备名称失败: $e');
      return 'Mobile Device';
    }
  }
  
  /// 显示通知（简化版本）
  void showNotification(String title, String message, String type) {
    debugPrint('[$type] $title: $message');
    
    // 在实际应用中，这里应该集成Flutter的通知插件
    // 例如：flutter_local_notifications
  }
}

// 全局服务实例
final identityFingerprintService = IdentityFingerprintService();