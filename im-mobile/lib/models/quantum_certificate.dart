import 'dart:convert';
import 'dart:typed_data';

/// 量子安全证书模型
/// 支持NIST PQC标准的后量子加密证书
class QuantumSecureCertificate {
  final String id;
  final String certificateId;
  final String subjectId;
  final String issuerId;
  final String subjectName;
  final String issuerName;
  final String certificateType;
  final String algorithmType;
  final String publicKeyPem;
  final String? privateKeyPem;
  final String certificatePem;
  final DateTime validFrom;
  final DateTime validUntil;
  final String serialNumber;
  final int keySize;
  final List<String> keyUsage;
  final List<String> extendedKeyUsage;
  final List<String>? sanDnsNames;
  final List<String>? sanIpAddresses;
  final String? sanEmail;
  final String status;
  final bool isRevoked;
  final DateTime? revokedAt;
  final String? revocationReason;
  final String? parentCertificateId;
  final int chainDepth;
  final DateTime createdAt;
  final DateTime? lastVerifiedAt;
  final int verificationCount;
  final Map<String, dynamic>? metadata;
  final String? fingerprint;
  final String? qrCodeData;

  QuantumSecureCertificate({
    required this.id,
    required this.certificateId,
    required this.subjectId,
    required this.issuerId,
    required this.subjectName,
    required this.issuerName,
    required this.certificateType,
    required this.algorithmType,
    required this.publicKeyPem,
    this.privateKeyPem,
    required this.certificatePem,
    required this.validFrom,
    required this.validUntil,
    required this.serialNumber,
    required this.keySize,
    required this.keyUsage,
    required this.extendedKeyUsage,
    this.sanDnsNames,
    this.sanIpAddresses,
    this.sanEmail,
    required this.status,
    this.isRevoked = false,
    this.revokedAt,
    this.revocationReason,
    this.parentCertificateId,
    required this.chainDepth,
    required this.createdAt,
    this.lastVerifiedAt,
    this.verificationCount = 0,
    this.metadata,
    this.fingerprint,
    this.qrCodeData,
  });

  /// 从JSON构造
  factory QuantumSecureCertificate.fromJson(Map<String, dynamic> json) {
    return QuantumSecureCertificate(
      id: json['id'] as String,
      certificateId: json['certificateId'] as String,
      subjectId: json['subjectId'] as String,
      issuerId: json['issuerId'] as String,
      subjectName: json['subjectName'] as String,
      issuerName: json['issuerName'] as String,
      certificateType: json['certificateType'] as String,
      algorithmType: json['algorithmType'] as String,
      publicKeyPem: json['publicKeyPem'] as String,
      privateKeyPem: json['privateKeyPem'] as String?,
      certificatePem: json['certificatePem'] as String,
      validFrom: DateTime.parse(json['validFrom'] as String),
      validUntil: DateTime.parse(json['validUntil'] as String),
      serialNumber: json['serialNumber'] as String,
      keySize: json['keySize'] as int,
      keyUsage: List<String>.from(json['keyUsage'] ?? []),
      extendedKeyUsage: List<String>.from(json['extendedKeyUsage'] ?? []),
      sanDnsNames: json['sanDnsNames'] != null 
          ? List<String>.from(json['sanDnsNames']) 
          : null,
      sanIpAddresses: json['sanIpAddresses'] != null 
          ? List<String>.from(json['sanIpAddresses']) 
          : null,
      sanEmail: json['sanEmail'] as String?,
      status: json['status'] as String,
      isRevoked: json['isRevoked'] as bool? ?? false,
      revokedAt: json['revokedAt'] != null 
          ? DateTime.parse(json['revokedAt'] as String) 
          : null,
      revocationReason: json['revocationReason'] as String?,
      parentCertificateId: json['parentCertificateId'] as String?,
      chainDepth: json['chainDepth'] as int,
      createdAt: DateTime.parse(json['createdAt'] as String),
      lastVerifiedAt: json['lastVerifiedAt'] != null 
          ? DateTime.parse(json['lastVerifiedAt'] as String) 
          : null,
      verificationCount: json['verificationCount'] as int? ?? 0,
      metadata: json['metadata'] as Map<String, dynamic>?,
      fingerprint: json['fingerprint'] as String?,
      qrCodeData: json['qrCodeData'] as String?,
    );
  }

  /// 转为JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'certificateId': certificateId,
      'subjectId': subjectId,
      'issuerId': issuerId,
      'subjectName': subjectName,
      'issuerName': issuerName,
      'certificateType': certificateType,
      'algorithmType': algorithmType,
      'publicKeyPem': publicKeyPem,
      'privateKeyPem': privateKeyPem,
      'certificatePem': certificatePem,
      'validFrom': validFrom.toIso8601String(),
      'validUntil': validUntil.toIso8601String(),
      'serialNumber': serialNumber,
      'keySize': keySize,
      'keyUsage': keyUsage,
      'extendedKeyUsage': extendedKeyUsage,
      'sanDnsNames': sanDnsNames,
      'sanIpAddresses': sanIpAddresses,
      'sanEmail': sanEmail,
      'status': status,
      'isRevoked': isRevoked,
      'revokedAt': revokedAt?.toIso8601String(),
      'revocationReason': revocationReason,
      'parentCertificateId': parentCertificateId,
      'chainDepth': chainDepth,
      'createdAt': createdAt.toIso8601String(),
      'lastVerifiedAt': lastVerifiedAt?.toIso8601String(),
      'verificationCount': verificationCount,
      'metadata': metadata,
      'fingerprint': fingerprint,
      'qrCodeData': qrCodeData,
    };
  }

  /// 检查证书是否有效
  bool get isValid {
    if (isRevoked) return false;
    final now = DateTime.now();
    return now.isAfter(validFrom) && now.isBefore(validUntil);
  }

  /// 获取有效期剩余天数
  int get daysUntilExpiry {
    final now = DateTime.now();
    return validUntil.difference(now).inDays;
  }

  /// 检查是否即将过期（30天内）
  bool get isExpiringSoon {
    return daysUntilExpiry <= 30 && daysUntilExpiry > 0;
  }

  /// 检查是否已过期
  bool get isExpired {
    return DateTime.now().isAfter(validUntil);
  }

  /// 获取算法显示名称
  String get algorithmDisplayName {
    switch (algorithmType) {
      case 'ML-KEM-512':
      case 'ML-KEM-768':
      case 'ML-KEM-1024':
        return 'CRYSTALS-Kyber $_keySizeDisplay';
      case 'ML-DSA-44':
      case 'ML-DSA-65':
      case 'ML-DSA-87':
        return 'CRYSTALS-Dilithium $_keySizeDisplay';
      case 'SLH-DSA-SHA2-128s':
      case 'SLH-DSA-SHA2-128f':
      case 'SLH-DSA-SHAKE-128s':
      case 'SLH-DSA-SHAKE-128f':
        return 'SPHINCS+ $_keySizeDisplay';
      case 'FN-DSA-512':
      case 'FN-DSA-1024':
        return 'FALCON $_keySizeDisplay';
      default:
        return algorithmType;
    }
  }

  String get _keySizeDisplay {
    return '${keySize * 8} 位';
  }

  /// 获取证书类型显示名称
  String get certificateTypeDisplay {
    switch (certificateType) {
      case 'ROOT_CA':
        return '根证书颁发机构';
      case 'INTERMEDIATE_CA':
        return '中间证书颁发机构';
      case 'END_ENTITY':
        return '终端实体证书';
      case 'SERVER':
        return '服务器证书';
      case 'CLIENT':
        return '客户端证书';
      case 'DEVICE':
        return '设备证书';
      case 'CODE_SIGNING':
        return '代码签名证书';
      default:
        return certificateType;
    }
  }

  /// 获取状态显示文本
  String get statusDisplay {
    if (isRevoked) return '已吊销';
    if (isExpired) return '已过期';
    if (isExpiringSoon) return '即将过期';
    switch (status) {
      case 'ACTIVE':
        return '有效';
      case 'PENDING':
        return '待激活';
      case 'SUSPENDED':
        return '已暂停';
      default:
        return status;
    }
  }

  /// 获取状态颜色
  String get statusColor {
    if (isRevoked) return '#F44336';
    if (isExpired) return '#9E9E9E';
    if (isExpiringSoon) return '#FF9800';
    switch (status) {
      case 'ACTIVE':
        return '#4CAF50';
      case 'PENDING':
        return '#FFC107';
      case 'SUSPENDED':
        return '#FF5722';
      default:
        return '#9E9E9E';
    }
  }

  /// 复制并修改
  QuantumSecureCertificate copyWith({
    String? id,
    String? certificateId,
    String? subjectId,
    String? issuerId,
    String? subjectName,
    String? issuerName,
    String? certificateType,
    String? algorithmType,
    String? publicKeyPem,
    String? privateKeyPem,
    String? certificatePem,
    DateTime? validFrom,
    DateTime? validUntil,
    String? serialNumber,
    int? keySize,
    List<String>? keyUsage,
    List<String>? extendedKeyUsage,
    List<String>? sanDnsNames,
    List<String>? sanIpAddresses,
    String? sanEmail,
    String? status,
    bool? isRevoked,
    DateTime? revokedAt,
    String? revocationReason,
    String? parentCertificateId,
    int? chainDepth,
    DateTime? createdAt,
    DateTime? lastVerifiedAt,
    int? verificationCount,
    Map<String, dynamic>? metadata,
    String? fingerprint,
    String? qrCodeData,
  }) {
    return QuantumSecureCertificate(
      id: id ?? this.id,
      certificateId: certificateId ?? this.certificateId,
      subjectId: subjectId ?? this.subjectId,
      issuerId: issuerId ?? this.issuerId,
      subjectName: subjectName ?? this.subjectName,
      issuerName: issuerName ?? this.issuerName,
      certificateType: certificateType ?? this.certificateType,
      algorithmType: algorithmType ?? this.algorithmType,
      publicKeyPem: publicKeyPem ?? this.publicKeyPem,
      privateKeyPem: privateKeyPem ?? this.privateKeyPem,
      certificatePem: certificatePem ?? this.certificatePem,
      validFrom: validFrom ?? this.validFrom,
      validUntil: validUntil ?? this.validUntil,
      serialNumber: serialNumber ?? this.serialNumber,
      keySize: keySize ?? this.keySize,
      keyUsage: keyUsage ?? this.keyUsage,
      extendedKeyUsage: extendedKeyUsage ?? this.extendedKeyUsage,
      sanDnsNames: sanDnsNames ?? this.sanDnsNames,
      sanIpAddresses: sanIpAddresses ?? this.sanIpAddresses,
      sanEmail: sanEmail ?? this.sanEmail,
      status: status ?? this.status,
      isRevoked: isRevoked ?? this.isRevoked,
      revokedAt: revokedAt ?? this.revokedAt,
      revocationReason: revocationReason ?? this.revocationReason,
      parentCertificateId: parentCertificateId ?? this.parentCertificateId,
      chainDepth: chainDepth ?? this.chainDepth,
      createdAt: createdAt ?? this.createdAt,
      lastVerifiedAt: lastVerifiedAt ?? this.lastVerifiedAt,
      verificationCount: verificationCount ?? this.verificationCount,
      metadata: metadata ?? this.metadata,
      fingerprint: fingerprint ?? this.fingerprint,
      qrCodeData: qrCodeData ?? this.qrCodeData,
    );
  }

  @override
  String toString() {
    return 'QuantumSecureCertificate($subjectName, $algorithmType, $statusDisplay)';
  }
}
