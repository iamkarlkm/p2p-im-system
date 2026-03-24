import 'dart:convert';
import 'dart:math';
import 'dart:typed_data';
import 'package:crypto/crypto.dart' as crypto;

/// 端到端加密服务
/// 提供消息加密、解密、密钥生成和交换功能
class EncryptionService {
  static final EncryptionService _instance = EncryptionService._internal();
  factory EncryptionService() => _instance;
  EncryptionService._internal();

  // 密钥缓存
  final Map<String, Uint8List> _sessionKeys = {};
  Map<String, dynamic>? _keyPair;

  /// 初始化密钥对
  Future<void> initialize(String userId, {String? storedPrivateKey, String? storedPublicKey}) async {
    if (storedPrivateKey != null && storedPublicKey != null) {
      _keyPair = {
        'privateKey': base64Decode(storedPrivateKey),
        'publicKey': base64Decode(storedPublicKey),
      };
    } else {
      // 生成新的密钥对
      _keyPair = _generateRSAKeyPair();
    }
  }

  /// 生成RSA密钥对（简化版本，实际使用需使用专门库）
  Map<String, dynamic> _generateRSAKeyPair() {
    // 注意：实际实现需要使用 pointycastle 或 crypton 库
    // 这里生成模拟密钥
    final random = Random.secure();
    final privateKey = Uint8List(256);
    final publicKey = Uint8List(256);
    for (int i = 0; i < 256; i++) {
      privateKey[i] = random.nextInt(256);
      publicKey[i] = random.nextInt(256);
    }
    return {
      'privateKey': privateKey,
      'publicKey': publicKey,
    };
  }

  /// 获取公钥
  Uint8List? getPublicKey() {
    return _keyPair?['publicKey'] as Uint8List?;
  }

  /// 获取公钥Base64
  String? getPublicKeyBase64() {
    final publicKey = getPublicKey();
    return publicKey != null ? base64Encode(publicKey) : null;
  }

  /// 从Base64导入公钥
  Uint8List? importPublicKeyFromBase64(String base64Key) {
    try {
      return base64Decode(base64Key);
    } catch (e) {
      return null;
    }
  }

  /// 生成AES会话密钥
  Uint8List generateSessionKey() {
    final random = Random.secure();
    final key = Uint8List(32); // 256位
    for (int i = 0; i < 32; i++) {
      key[i] = random.nextInt(256);
    }
    return key;
  }

  /// 导出会话密钥
  String exportSessionKey(Uint8List key) {
    return base64Encode(key);
  }

  /// 导入会话密钥
  Uint8List? importSessionKey(String base64Key) {
    try {
      return base64Decode(base64Key);
    } catch (e) {
      return null;
    }
  }

  /// 加密会话密钥（简化版本）
  Uint8List encryptSessionKey(Uint8List sessionKey, Uint8List publicKey) {
    // 简化实现：使用XOR加密（实际应使用真正的RSA加密）
    final encrypted = Uint8List(sessionKey.length);
    for (int i = 0; i < sessionKey.length; i++) {
      encrypted[i] = sessionKey[i] ^ publicKey[i % publicKey.length];
    }
    return encrypted;
  }

  /// 解密会话密钥（简化版本）
  Uint8List decryptSessionKey(Uint8List encryptedKey, Uint8List privateKey) {
    // 简化实现：使用XOR解密（实际应使用真正的RSA解密）
    final decrypted = Uint8List(encryptedKey.length);
    for (int i = 0; i < encryptedKey.length; i++) {
      decrypted[i] = encryptedKey[i] ^ privateKey[i % privateKey.length];
    }
    return decrypted;
  }

  /// AES加密（简化版本）
  Map<String, String> encryptMessage(String plainText, Uint8List sessionKey) {
    // 生成随机IV
    final iv = Uint8List(12);
    final random = Random.secure();
    for (int i = 0; i < 12; i++) {
      iv[i] = random.nextInt(256);
    }

    // 简化实现：使用AES-like加密
    final plainBytes = utf8.encode(plainText);
    final cipherText = Uint8List(plainBytes.length);

    for (int i = 0; i < plainBytes.length; i++) {
      final keyByte = sessionKey[i % sessionKey.length];
      final ivByte = iv[i % iv.length];
      cipherText[i] = (plainBytes[i] ^ keyByte ^ ivByte) % 256;
    }

    return {
      'cipherText': base64Encode(cipherText),
      'iv': base64Encode(iv),
    };
  }

  /// AES解密（简化版本）
  String decryptMessage(Map<String, String> encryptedMessage, Uint8List sessionKey) {
    final cipherText = base64Decode(encryptedMessage['cipherText']!);
    final iv = base64Decode(encryptedMessage['iv']!);

    // 解密
    final plainBytes = Uint8List(cipherText.length);
    for (int i = 0; i < cipherText.length; i++) {
      final keyByte = sessionKey[i % sessionKey.length];
      final ivByte = iv[i % iv.length];
      plainBytes[i] = (cipherText[i] ^ keyByte ^ ivByte) % 256;
    }

    return utf8.decode(plainBytes);
  }

  /// 存储会话密钥
  void storeSessionKey(String chatId, Uint8List sessionKey) {
    _sessionKeys[chatId] = sessionKey;
  }

  /// 获取会话密钥
  Uint8List? getSessionKey(String chatId) {
    return _sessionKeys[chatId];
  }

  /// 清除会话密钥
  void clearSessionKey(String chatId) {
    _sessionKeys.remove(chatId);
  }

  /// 加密消息（使用会话密钥）
  Map<String, String> encrypt(String plainText, String chatId) {
    var sessionKey = _sessionKeys[chatId];
    if (sessionKey == null) {
      sessionKey = generateSessionKey();
      _sessionKeys[chatId] = sessionKey;
    }
    return encryptMessage(plainText, sessionKey);
  }

  /// 解密消息
  String decrypt(Map<String, String> encryptedMessage, String chatId) {
    final sessionKey = _sessionKeys[chatId];
    if (sessionKey == null) {
      throw Exception('Session key not found for chat: $chatId');
    }
    return decryptMessage(encryptedMessage, sessionKey);
  }

  /// 设置会话密钥
  void setSessionKey(String chatId, Uint8List sessionKey) {
    _sessionKeys[chatId] = sessionKey;
  }

  /// 清除所有密钥
  void clearAllKeys() {
    _sessionKeys.clear();
    _keyPair = null;
  }

  /// 生成哈希
  String generateHash(String input) {
    final bytes = utf8.encode(input);
    final digest = crypto.sha256.convert(bytes);
    return digest.toString();
  }

  /// 生成随机ID
  String generateRandomId() {
    final random = Random.secure();
    final values = List<int>.generate(32, (i) => random.nextInt(256));
    return base64Encode(values).replaceAll('+', 'x').replaceAll('/', 'y').replaceAll('=', '');
  }
}

/// 加密消息类
class EncryptedMessage {
  final String cipherText;
  final String iv;

  EncryptedMessage({
    required this.cipherText,
    required this.iv,
  });

  Map<String, String> toJson() => {
        'cipherText': cipherText,
        'iv': iv,
      };

  factory EncryptedMessage.fromJson(Map<String, dynamic> json) {
    return EncryptedMessage(
      cipherText: json['cipherText'] as String,
      iv: json['iv'] as String,
    );
  }
}

/// 密钥交换请求类
class KeyExchangeRequest {
  final String senderId;
  final String recipientId;
  final String chatId;
  final String senderPublicKey;
  final int timestamp;

  KeyExchangeRequest({
    required this.senderId,
    required this.recipientId,
    required this.chatId,
    required this.senderPublicKey,
    required this.timestamp,
  });

  Map<String, dynamic> toJson() => {
        'senderId': senderId,
        'recipientId': recipientId,
        'chatId': chatId,
        'senderPublicKey': senderPublicKey,
        'timestamp': timestamp,
      };

  factory KeyExchangeRequest.fromJson(Map<String, dynamic> json) {
    return KeyExchangeRequest(
      senderId: json['senderId'] as String,
      recipientId: json['recipientId'] as String,
      chatId: json['chatId'] as String,
      senderPublicKey: json['senderPublicKey'] as String,
      timestamp: json['timestamp'] as int,
    );
  }
}

/// 密钥交换响应类
class KeyExchangeResponse {
  final String senderId;
  final String recipientId;
  final String chatId;
  final String encryptedSessionKey;
  final String recipientPublicKey;
  final int timestamp;

  KeyExchangeResponse({
    required this.senderId,
    required this.recipientId,
    required this.chatId,
    required this.encryptedSessionKey,
    required this.recipientPublicKey,
    required this.timestamp,
  });

  Map<String, dynamic> toJson() => {
        'senderId': senderId,
        'recipientId': recipientId,
        'chatId': chatId,
        'encryptedSessionKey': encryptedSessionKey,
        'recipientPublicKey': recipientPublicKey,
        'timestamp': timestamp,
      };

  factory KeyExchangeResponse.fromJson(Map<String, dynamic> json) {
    return KeyExchangeResponse(
      senderId: json['senderId'] as String,
      recipientId: json['recipientId'] as String,
      chatId: json['chatId'] as String,
      encryptedSessionKey: json['encryptedSessionKey'] as String,
      recipientPublicKey: json['recipientPublicKey'] as String,
      timestamp: json['timestamp'] as int,
    );
  }
}
