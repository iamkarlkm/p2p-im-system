import 'dart:convert';
import 'package:flutter_secure_storage/flutter_secure_storage.dart';

/// 安全存储服务
/// 使用设备的安全存储机制保存敏感数据
class SecureStorageService {
  static final SecureStorageService _instance = SecureStorageService._internal();
  factory SecureStorageService() => _instance;
  SecureStorageService._internal();

  final FlutterSecureStorage _storage = const FlutterSecureStorage(
    aOptions: AndroidOptions(
      encryptedSharedPreferences: true,
    ),
    iOptions: IOSOptions(
      accessibility: KeychainAccessibility.first_unlock_this_device,
    ),
  );

  /// 写入数据
  Future<void> write(String key, String value) async {
    await _storage.write(key: key, value: value);
  }

  /// 读取数据
  Future<String?> read(String key) async {
    return await _storage.read(key: key);
  }

  /// 删除数据
  Future<void> delete(String key) async {
    await _storage.delete(key: key);
  }

  /// 删除所有数据
  Future<void> deleteAll() async {
    await _storage.deleteAll();
  }

  /// 获取所有键
  Future<Map<String, String>> readAll() async {
    return await _storage.readAll();
  }

  /// 存储JSON对象
  Future<void> writeJson(String key, Map<String, dynamic> json) async {
    await write(key, jsonEncode(json));
  }

  /// 读取JSON对象
  Future<Map<String, dynamic>?> readJson(String key) async {
    final value = await read(key);
    if (value == null) return null;
    try {
      return jsonDecode(value) as Map<String, dynamic>;
    } catch (e) {
      return null;
    }
  }
}
