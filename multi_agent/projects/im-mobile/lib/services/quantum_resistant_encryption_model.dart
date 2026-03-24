import 'package:json_annotation/json_annotation.dart';

part 'quantum_resistant_encryption_model.g.dart';

/**
 * 量子抗性加密模型
 * 后量子密码学密钥管理和加密操作的Dart数据模型
 */

/**
 * 量子抗性加密密钥模型
 */
@JsonSerializable()
class QuantumResistantEncryptionKey {
  @JsonKey(name: 'id')
  final String id;
  
  @JsonKey(name: 'algorithmType')
  final String algorithmType;
  
  @JsonKey(name: 'algorithmParameter')
  final String algorithmParameter;
  
  @JsonKey(name: 'publicKey')
  final String publicKey;
  
  @JsonKey(name: 'privateKeyEncrypted')
  final String? privateKeyEncrypted;
  
  @JsonKey(name: 'encryptionKeyId')
  final String? encryptionKeyId;
  
  @JsonKey(name: 'keySize')
  final int keySize;
  
  @JsonKey(name: 'securityLevel')
  final String securityLevel;
  
  @JsonKey(name: 'keyUsage')
  final String keyUsage;
  
  @JsonKey(name: 'encryptionMode')
  final String encryptionMode;
  
  @JsonKey(name: 'keyGeneratedAt')
  final String keyGeneratedAt;
  
  @JsonKey(name: 'keyExpiresAt')
  final String? keyExpiresAt;
  
  @JsonKey(name: 'lastUsedAt')
  final String? lastUsedAt;
  
  @JsonKey(name: 'usageCount')
  final int usageCount;
  
  @JsonKey(name: 'keyStatus')
  final String keyStatus;
  
  @JsonKey(name: 'metadata')
  final Map<String, dynamic>? metadata;
  
  @JsonKey(name: 'performanceBenchmarks')
  final Map<String, dynamic>? performanceBenchmarks;
  
  @JsonKey(name: 'nistStatus')
  final String? nistStatus;
  
  @JsonKey(name: 'qkdIntegrationId')
  final String? qkdIntegrationId;
  
  @JsonKey(name: 'hsmEnabled')
  final bool hsmEnabled;
  
  @JsonKey(name: 'hsmIdentifier')
  final String? hsmIdentifier;
  
  @JsonKey(name: 'createdAt')
  final String createdAt;
  
  @JsonKey(name: 'updatedAt')
  final String updatedAt;

  QuantumResistantEncryptionKey({
    required this.id,
    required this.algorithmType,
    required this.algorithmParameter,
    required this.publicKey,
    this.privateKeyEncrypted,
    this.encryptionKeyId,
    required this.keySize,
    required this.securityLevel,
    required this.keyUsage,
    required this.encryptionMode,
    required this.keyGeneratedAt,
    this.keyExpiresAt,
    this.lastUsedAt,
    required this.usageCount,
    required this.keyStatus,
    this.metadata,
    this.performanceBenchmarks,
    this.nistStatus,
    this.qkdIntegrationId,
    required this.hsmEnabled,
    this.hsmIdentifier,
    required this.createdAt,
    required this.updatedAt,
  });

  factory QuantumResistantEncryptionKey.fromJson(Map<String, dynamic> json) =>
      _$QuantumResistantEncryptionKeyFromJson(json);

  Map<String, dynamic> toJson() => _$QuantumResistantEncryptionKeyToJson(this);

  /**
   * 检查密钥是否有效
   */
  bool isValid() {
    if (keyStatus != 'ACTIVE') {
      return false;
    }
    
    if (keyExpiresAt != null) {
      final expiresAt = DateTime.tryParse(keyExpiresAt!);
      if (expiresAt != null && expiresAt.isBefore(DateTime.now())) {
        return false;
      }
    }
    
    return true;
  }

  /**
   * 获取显示名称
   */
  String getDisplayName() {
    return '$algorithmType:$algorithmParameter:$securityLevel';
  }

  /**
   * 检查是否为NIST标准化算法
   */
  bool isNistStandardized() {
    return nistStatus == 'NIST_STANDARDIZED';
  }

  /**
   * 检查是否为混合模式
   */
  bool isHybridMode() {
    return encryptionMode == 'HYBRID';
  }
}

/**
 * 后量子签名模型
 */
@JsonSerializable()
class PostQuantumSignature {
  @JsonKey(name: 'id')
  final String id;
  
  @JsonKey(name: 'signatureAlgorithm')
  final String signatureAlgorithm;
  
  @JsonKey(name: 'algorithmParameter')
  final String algorithmParameter;
  
  @JsonKey(name: 'signatureData')
  final String signatureData;
  
  @JsonKey(name: 'signatureHash')
  final String signatureHash;
  
  @JsonKey(name: 'messageHash')
  final String messageHash;
  
  @JsonKey(name: 'signerId')
  final String signerId;
  
  @JsonKey(name: 'signerPublicKey')
  final String signerPublicKey;
  
  @JsonKey(name: 'signatureTimestamp')
  final String signatureTimestamp;
  
  @JsonKey(name: 'validFrom')
  final String validFrom;
  
  @JsonKey(name: 'validUntil')
  final String? validUntil;
  
  @JsonKey(name: 'signaturePurpose')
  final String signaturePurpose;
  
  @JsonKey(name: 'signatureContext')
  final Map<String, dynamic>? signatureContext;
  
  @JsonKey(name: 'securityLevel')
  final String securityLevel;
  
  @JsonKey(name: 'keySize')
  final int keySize;
  
  @JsonKey(name: 'signatureSize')
  final int signatureSize;
  
  @JsonKey(name: 'validityStatus')
  final String validityStatus;
  
  @JsonKey(name: 'lastVerifiedAt')
  final String? lastVerifiedAt;
  
  @JsonKey(name: 'verificationCount')
  final int verificationCount;
  
  @JsonKey(name: 'certificateChainId')
  final String? certificateChainId;
  
  @JsonKey(name: 'timestampToken')
  final String? timestampToken;
  
  @JsonKey(name: 'timestampAuthorityId')
  final String? timestampAuthorityId;
  
  @JsonKey(name: 'quantumSafeTimestamp')
  final String? quantumSafeTimestamp;
  
  @JsonKey(name: 'performanceMetrics')
  final Map<String, dynamic>? performanceMetrics;
  
  @JsonKey(name: 'nistStatus')
  final String? nistStatus;
  
  @JsonKey(name: 'auditLog')
  final Map<String, dynamic>? auditLog;
  
  @JsonKey(name: 'batchSignature')
  final bool batchSignature;
  
  @JsonKey(name: 'batchId')
  final String? batchId;
  
  @JsonKey(name: 'batchIndex')
  final int? batchIndex;
  
  @JsonKey(name: 'advancedFeatures')
  final String? advancedFeatures;
  
  @JsonKey(name: 'createdAt')
  final String createdAt;
  
  @JsonKey(name: 'updatedAt')
  final String updatedAt;

  PostQuantumSignature({
    required this.id,
    required this.signatureAlgorithm,
    required this.algorithmParameter,
    required this.signatureData,
    required this.signatureHash,
    required this.messageHash,
    required this.signerId,
    required this.signerPublicKey,
    required this.signatureTimestamp,
    required this.validFrom,
    this.validUntil,
    required this.signaturePurpose,
    this.signatureContext,
    required this.securityLevel,
    required this.keySize,
    required this.signatureSize,
    required this.validityStatus,
    this.lastVerifiedAt,
    required this.verificationCount,
    this.certificateChainId,
    this.timestampToken,
    this.timestampAuthorityId,
    this.quantumSafeTimestamp,
    this.performanceMetrics,
    this.nistStatus,
    this.auditLog,
    required this.batchSignature,
    this.batchId,
    this.batchIndex,
    this.advancedFeatures,
    required this.createdAt,
    required this.updatedAt,
  });

  factory PostQuantumSignature.fromJson(Map<String, dynamic> json) =>
      _$PostQuantumSignatureFromJson(json);

  Map<String, dynamic> toJson() => _$PostQuantumSignatureToJson(this);

  /**
   * 检查签名是否有效
   */
  bool isValid() {
    if (validityStatus != 'VALID') {
      return false;
    }
    
    final validFromDate = DateTime.tryParse(validFrom);
    if (validFromDate != null && validFromDate.isAfter(DateTime.now())) {
      return false;
    }
    
    if (validUntil != null) {
      final validUntilDate = DateTime.tryParse(validUntil!);
      if (validUntilDate != null && validUntilDate.isBefore(DateTime.now())) {
        return false;
      }
    }
    
    return true;
  }

  /**
   * 检查签名是否过期
   */
  bool isExpired() {
    if (validUntil == null) {
      return false;
    }
    
    final validUntilDate = DateTime.tryParse(validUntil!);
    if (validUntilDate != null) {
      return validUntilDate.isBefore(DateTime.now());
    }
    
    return false;
  }

  /**
   * 获取显示名称
   */
  String getDisplayName() {
    return '$signatureAlgorithm:$algorithmParameter:$signerId';
  }
}

/**
 * 加密请求模型
 */
@JsonSerializable()
class EncryptionRequest {
  @JsonKey(name: 'plaintext')
  final String plaintext;
  
  @JsonKey(name: 'additionalData')
  final String? additionalData;

  EncryptionRequest({
    required this.plaintext,
    this.additionalData,
  });

  factory EncryptionRequest.fromJson(Map<String, dynamic> json) =>
      _$EncryptionRequestFromJson(json);

  Map<String, dynamic> toJson() => _$EncryptionRequestToJson(this);
}

/**
 * 加密响应模型
 */
@JsonSerializable()
class EncryptionResponse {
  @JsonKey(name: 'keyId')
  final String keyId;
  
  @JsonKey(name: 'encryptedData')
  final String encryptedData;
  
  @JsonKey(name: 'algorithm')
  final String algorithm;
  
  @JsonKey(name: 'timestamp')
  final String timestamp;
  
  @JsonKey(name: 'success')
  final bool success;
  
  @JsonKey(name: 'errorMessage')
  final String? errorMessage;

  EncryptionResponse({
    required this.keyId,
    required this.encryptedData,
    required this.algorithm,
    required this.timestamp,
    required this.success,
    this.errorMessage,
  });

  factory EncryptionResponse.fromJson(Map<String, dynamic> json) =>
      _$EncryptionResponseFromJson(json);

  Map<String, dynamic> toJson() => _$EncryptionResponseToJson(this);
}

/**
 * 解密请求模型
 */
@JsonSerializable()
class DecryptionRequest {
  @JsonKey(name: 'encryptedData')
  final String encryptedData;
  
  @JsonKey(name: 'additionalData')
  final String? additionalData;

  DecryptionRequest({
    required this.encryptedData,
    this.additionalData,
  });

  factory DecryptionRequest.fromJson(Map<String, dynamic> json) =>
      _$DecryptionRequestFromJson(json);

  Map<String, dynamic> toJson() => _$DecryptionRequestToJson(this);
}

/**
 * 解密响应模型
 */
@JsonSerializable()
class DecryptionResponse {
  @JsonKey(name: 'keyId')
  final String keyId;
  
  @JsonKey(name: 'plaintext')
  final String plaintext;
  
  @JsonKey(name: 'algorithm')
  final String algorithm;
  
  @JsonKey(name: 'timestamp')
  final String timestamp;
  
  @JsonKey(name: 'success')
  final bool success;
  
  @JsonKey(name: 'errorMessage')
  final String? errorMessage;

  DecryptionResponse({
    required this.keyId,
    required this.plaintext,
    required this.algorithm,
    required this.timestamp,
    required this.success,
    this.errorMessage,
  });

  factory DecryptionResponse.fromJson(Map<String, dynamic> json) =>
      _$DecryptionResponseFromJson(json);

  Map<String, dynamic> toJson() => _$DecryptionResponseToJson(this);
}

/**
 * 密钥生成请求模型
 */
@JsonSerializable()
class KeyGenerationRequest {
  @JsonKey(name: 'algorithmType')
  final String algorithmType;
  
  @JsonKey(name: 'algorithmParameter')
  final String algorithmParameter;
  
  @JsonKey(name: 'keyUsage')
  final String? keyUsage;
  
  @JsonKey(name: 'encryptionMode')
  final String? encryptionMode;
  
  @JsonKey(name: 'securityLevel')
  final String? securityLevel;
  
  @JsonKey(name: 'keySize')
  final int? keySize;
  
  @JsonKey(name: 'expiresAt')
  final String? expiresAt;

  KeyGenerationRequest({
    required this.algorithmType,
    required this.algorithmParameter,
    this.keyUsage,
    this.encryptionMode,
    this.securityLevel,
    this.keySize,
    this.expiresAt,
  });

  factory KeyGenerationRequest.fromJson(Map<String, dynamic> json) =>
      _$KeyGenerationRequestFromJson(json);

  Map<String, dynamic> toJson() => _$KeyGenerationRequestToJson(this);
}

/**
 * 密钥轮换响应模型
 */
@JsonSerializable()
class KeyRotationResponse {
  @JsonKey(name: 'oldKeyId')
  final String oldKeyId;
  
  @JsonKey(name: 'newKeyId')
  final String newKeyId;
  
  @JsonKey(name: 'newAlgorithmParameter')
  final String newAlgorithmParameter;
  
  @JsonKey(name: 'rotationTime')
  final String rotationTime;
  
  @JsonKey(name: 'success')
  final bool success;
  
  @JsonKey(name: 'errorMessage')
  final String? errorMessage;

  KeyRotationResponse({
    required this.oldKeyId,
    required this.newKeyId,
    required this.newAlgorithmParameter,
    required this.rotationTime,
    required this.success,
    this.errorMessage,
  });

  factory KeyRotationResponse.fromJson(Map<String, dynamic> json) =>
      _$KeyRotationResponseFromJson(json);

  Map<String, dynamic> toJson() => _$KeyRotationResponseToJson(this);
}

/**
 * API响应消息模型
 */
@JsonSerializable()
class ApiResponseMessage {
  @JsonKey(name: 'message')
  final String message;
  
  @JsonKey(name: 'timestamp')
  final String timestamp;

  ApiResponseMessage({
    required this.message,
    required this.timestamp,
  });

  factory ApiResponseMessage.fromJson(Map<String, dynamic> json) =>
      _$ApiResponseMessageFromJson(json);

  Map<String, dynamic> toJson() => _$ApiResponseMessageToJson(this);
}

/**
 * 清理响应模型
 */
@JsonSerializable()
class CleanupResponse {
  @JsonKey(name: 'cleanedCount')
  final int cleanedCount;
  
  @JsonKey(name: 'cleanupTime')
  final String cleanupTime;
  
  @JsonKey(name: 'message')
  final String message;

  CleanupResponse({
    required this.cleanedCount,
    required this.cleanupTime,
    required this.message,
  });

  factory CleanupResponse.fromJson(Map<String, dynamic> json) =>
      _$CleanupResponseFromJson(json);

  Map<String, dynamic> toJson() => _$CleanupResponseToJson(this);
}

/**
 * 统计响应模型
 */
@JsonSerializable()
class StatisticsResponse {
  @JsonKey(name: 'statistics')
  final String statistics;
  
  @JsonKey(name: 'timestamp')
  final String timestamp;

  StatisticsResponse({
    required this.statistics,
    required this.timestamp,
  });

  factory StatisticsResponse.fromJson(Map<String, dynamic> json) =>
      _$StatisticsResponseFromJson(json);

  Map<String, dynamic> toJson() => _$StatisticsResponseToJson(this);
}

/**
 * 算法类型常量
 */
class QuantumResistantAlgorithmTypes {
  static const String LATTICE_BASED = 'LATTICE_BASED';
  static const String HASH_BASED = 'HASH_BASED';
  static const String CODE_BASED = 'CODE_BASED';
  static const String MULTIVARIATE = 'MULTIVARIATE';
  static const String SIKE = 'SIKE';
}

/**
 * 安全级别常量
 */
class SecurityLevels {
  static const String LEVEL_1 = 'LEVEL_1';
  static const String LEVEL_3 = 'LEVEL_3';
  static const String LEVEL_5 = 'LEVEL_5';
}

/**
 * 密钥用途常量
 */
class KeyUsages {
  static const String KEY_AGREEMENT = 'KEY_AGREEMENT';
  static const String DIGITAL_SIGNATURE = 'DIGITAL_SIGNATURE';
  static const String PUBLIC_KEY_ENCRYPTION = 'PUBLIC_KEY_ENCRYPTION';
  static const String HYBRID_ENCRYPTION = 'HYBRID_ENCRYPTION';
}

/**
 * 加密模式常量
 */
class EncryptionModes {
  static const String PURE_PQC = 'PURE_PQC';
  static const String HYBRID = 'HYBRID';
  static const String TRANSITIONAL = 'TRANSITIONAL';
}

/**
 * 密钥状态常量
 */
class KeyStatuses {
  static const String ACTIVE = 'ACTIVE';
  static const String INACTIVE = 'INACTIVE';
  static const String EXPIRED = 'EXPIRED';
  static const String REVOKED = 'REVOKED';
  static const String ARCHIVED = 'ARCHIVED';
}