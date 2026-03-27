import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import '../models/quantum_certificate.dart';
import 'secure_storage_service.dart';

/// 证书验证结果
class CertificateVerificationResult {
  final bool isValid;
  final bool isTrusted;
  final bool isRevoked;
  final bool hasValidChain;
  final DateTime? verifiedAt;
  final String? errorMessage;
  final List<String> warnings;
  final Map<String, dynamic>? details;
  final int chainLength;
  final String? rootCaSubject;

  CertificateVerificationResult({
    required this.isValid,
    required this.isTrusted,
    required this.isRevoked,
    required this.hasValidChain,
    this.verifiedAt,
    this.errorMessage,
    this.warnings = const [],
    this.details,
    this.chainLength = 0,
    this.rootCaSubject,
  });

  factory CertificateVerificationResult.valid({
    required int chainLength,
    String? rootCaSubject,
    Map<String, dynamic>? details,
    List<String> warnings = const [],
  }) {
    return CertificateVerificationResult(
      isValid: true,
      isTrusted: true,
      isRevoked: false,
      hasValidChain: true,
      verifiedAt: DateTime.now(),
      chainLength: chainLength,
      rootCaSubject: rootCaSubject,
      details: details,
      warnings: warnings,
    );
  }

  factory CertificateVerificationResult.invalid(String reason, {
    List<String> warnings = const [],
    Map<String, dynamic>? details,
  }) {
    return CertificateVerificationResult(
      isValid: false,
      isTrusted: false,
      isRevoked: false,
      hasValidChain: false,
      verifiedAt: DateTime.now(),
      errorMessage: reason,
      warnings: warnings,
      details: details,
    );
  }

  factory CertificateVerificationResult.revoked(String reason, {
    List<String> warnings = const [],
    Map<String, dynamic>? details,
  }) {
    return CertificateVerificationResult(
      isValid: false,
      isTrusted: false,
      isRevoked: true,
      hasValidChain: false,
      verifiedAt: DateTime.now(),
      errorMessage: reason,
      warnings: warnings,
      details: details,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'isValid': isValid,
      'isTrusted': isTrusted,
      'isRevoked': isRevoked,
      'hasValidChain': hasValidChain,
      'verifiedAt': verifiedAt?.toIso8601String(),
      'errorMessage': errorMessage,
      'warnings': warnings,
      'details': details,
      'chainLength': chainLength,
      'rootCaSubject': rootCaSubject,
    };
  }
}

/// 量子安全证书验证服务
/// 支持本地验证和远程CRL/OCSP验证
class QuantumCertificateVerificationService {
  static final QuantumCertificateVerificationService _instance = 
      QuantumCertificateVerificationService._internal();
  factory QuantumCertificateVerificationService() => _instance;
  QuantumCertificateVerificationService._internal();

  final SecureStorageService _storage = SecureStorageService();
  
  // 缓存
  final Map<String, QuantumSecureCertificate> _certificateCache = {};
  final Map<String, CertificateVerificationResult> _verificationCache = {};
  final Map<String, DateTime> _crlCacheTimestamps = {};
  final Map<String, List<String>> _crlCache = {};
  
  // 配置
  String? _apiBaseUrl;
  Duration _cacheValidity = const Duration(minutes: 5);
  Duration _crlCacheValidity = const Duration(hours: 1);
  bool _enableOnlineValidation = true;
  bool _enableCrlCheck = true;
  bool _enableOcspCheck = true;

  // 受信任的根CA指纹列表
  final Set<String> _trustedRootFingerprints = {};

  /// 初始化服务
  Future<void> initialize({
    String? apiBaseUrl,
    Duration cacheValidity = const Duration(minutes: 5),
    Duration crlCacheValidity = const Duration(hours: 1),
    bool enableOnlineValidation = true,
    bool enableCrlCheck = true,
    bool enableOcspCheck = true,
  }) async {
    _apiBaseUrl = apiBaseUrl;
    _cacheValidity = cacheValidity;
    _crlCacheValidity = crlCacheValidity;
    _enableOnlineValidation = enableOnlineValidation;
    _enableCrlCheck = enableCrlCheck;
    _enableOcspCheck = enableOcspCheck;

    // 加载受信任的根CA
    await _loadTrustedRoots();
    
    // 加载缓存
    await _loadCache();
  }

  /// 加载受信任的根证书
  Future<void> _loadTrustedRoots() async {
    try {
      final rootsJson = await _storage.read('trusted_root_cas');
      if (rootsJson != null) {
        final List<dynamic> roots = jsonDecode(rootsJson);
        _trustedRootFingerprints.addAll(roots.cast<String>());
      }
    } catch (e) {
      if (kDebugMode) {
        print('加载受信任根证书失败: $e');
      }
    }
  }

  /// 保存受信任的根证书
  Future<void> _saveTrustedRoots() async {
    try {
      await _storage.write(
        'trusted_root_cas',
        jsonEncode(_trustedRootFingerprints.toList()),
      );
    } catch (e) {
      if (kDebugMode) {
        print('保存受信任根证书失败: $e');
      }
    }
  }

  /// 加载缓存
  Future<void> _loadCache() async {
    try {
      final cacheJson = await _storage.read('verification_cache');
      if (cacheJson != null) {
        final Map<String, dynamic> cache = jsonDecode(cacheJson);
        final now = DateTime.now();
        
        cache.forEach((key, value) {
          final verifiedAt = DateTime.parse(value['verifiedAt']);
          if (now.difference(verifiedAt) < _cacheValidity) {
            _verificationCache[key] = CertificateVerificationResult(
              isValid: value['isValid'],
              isTrusted: value['isTrusted'],
              isRevoked: value['isRevoked'],
              hasValidChain: value['hasValidChain'],
              verifiedAt: verifiedAt,
              errorMessage: value['errorMessage'],
              warnings: List<String>.from(value['warnings'] ?? []),
              details: value['details'],
              chainLength: value['chainLength'] ?? 0,
              rootCaSubject: value['rootCaSubject'],
            );
          }
        });
      }
    } catch (e) {
      if (kDebugMode) {
        print('加载验证缓存失败: $e');
      }
    }
  }

  /// 保存缓存
  Future<void> _saveCache() async {
    try {
      final cacheMap = <String, dynamic>{};
      _verificationCache.forEach((key, value) {
        cacheMap[key] = value.toJson();
      });
      await _storage.write('verification_cache', jsonEncode(cacheMap));
    } catch (e) {
      if (kDebugMode) {
        print('保存验证缓存失败: $e');
      }
    }
  }

  /// 验证证书
  Future<CertificateVerificationResult> verifyCertificate(
    QuantumSecureCertificate certificate, {
    bool forceRefresh = false,
    String? expectedHostname,
  }) async {
    // 检查缓存
    if (!forceRefresh) {
      final cached = _verificationCache[certificate.certificateId];
      if (cached != null) {
        final age = DateTime.now().difference(cached.verifiedAt ?? DateTime.now());
        if (age < _cacheValidity) {
          return cached;
        }
      }
    }

    final warnings = <String>[];

    // 1. 基础验证
    final basicCheck = _verifyBasicProperties(certificate);
    if (!basicCheck.isValid) {
      _verificationCache[certificate.certificateId] = basicCheck;
      return basicCheck;
    }
    warnings.addAll(basicCheck.warnings);

    // 2. 有效期验证
    final validityCheck = _verifyValidityPeriod(certificate);
    if (!validityCheck.isValid) {
      _verificationCache[certificate.certificateId] = validityCheck;
      return validityCheck;
    }
    warnings.addAll(validityCheck.warnings);

    // 3. 主机名验证
    if (expectedHostname != null) {
      final hostnameCheck = _verifyHostname(certificate, expectedHostname);
      if (!hostnameCheck.isValid) {
        _verificationCache[certificate.certificateId] = hostnameCheck;
        return hostnameCheck;
      }
      warnings.addAll(hostnameCheck.warnings);
    }

    // 4. 吊销检查
    if (_enableCrlCheck && _enableOnlineValidation) {
      final revocationCheck = await _checkRevocation(certificate);
      if (revocationCheck.isRevoked) {
        _verificationCache[certificate.certificateId] = revocationCheck;
        return revocationCheck;
      }
      warnings.addAll(revocationCheck.warnings);
    }

    // 5. 证书链验证
    final chainResult = await _verifyCertificateChain(certificate);
    if (!chainResult.hasValidChain) {
      _verificationCache[certificate.certificateId] = chainResult;
      return chainResult;
    }
    warnings.addAll(chainResult.warnings);

    // 6. 构建成功结果
    final result = CertificateVerificationResult.valid(
      chainLength: chainResult.chainLength,
      rootCaSubject: chainResult.rootCaSubject,
      details: {
        'certificateId': certificate.certificateId,
        'algorithm': certificate.algorithmType,
        'keySize': certificate.keySize,
        'validUntil': certificate.validUntil.toIso8601String(),
        'serialNumber': certificate.serialNumber,
      },
      warnings: warnings,
    );

    _verificationCache[certificate.certificateId] = result;
    await _saveCache();

    return result;
  }

  /// 基础属性验证
  CertificateVerificationResult _verifyBasicProperties(
    QuantumSecureCertificate certificate,
  ) {
    final warnings = <String>[];

    // 检查必要的字段
    if (certificate.certificatePem.isEmpty) {
      return CertificateVerificationResult.invalid('证书内容为空');
    }

    if (certificate.publicKeyPem.isEmpty) {
      return CertificateVerificationResult.invalid('公钥为空');
    }

    // 检查算法类型
    final validAlgorithms = [
      'ML-KEM-512', 'ML-KEM-768', 'ML-KEM-1024',
      'ML-DSA-44', 'ML-DSA-65', 'ML-DSA-87',
      'SLH-DSA-SHA2-128s', 'SLH-DSA-SHA2-128f',
      'SLH-DSA-SHAKE-128s', 'SLH-DSA-SHAKE-128f',
      'FN-DSA-512', 'FN-DSA-1024',
    ];
    
    if (!validAlgorithms.contains(certificate.algorithmType)) {
      warnings.add('未知的后量子算法类型: ${certificate.algorithmType}');
    }

    // 检查密钥大小
    if (certificate.keySize < 64) {
      return CertificateVerificationResult.invalid('密钥大小过小');
    }

    return CertificateVerificationResult.valid(
      chainLength: 0,
      warnings: warnings,
    );
  }

  /// 有效期验证
  CertificateVerificationResult _verifyValidityPeriod(
    QuantumSecureCertificate certificate,
  ) {
    final warnings = <String>[];
    final now = DateTime.now();

    // 检查是否已过期
    if (now.isAfter(certificate.validUntil)) {
      return CertificateVerificationResult.invalid(
        '证书已过期 (${certificate.validUntil})',
        details: {'expiredAt': certificate.validUntil.toIso8601String()},
      );
    }

    // 检查是否尚未生效
    if (now.isBefore(certificate.validFrom)) {
      return CertificateVerificationResult.invalid(
        '证书尚未生效 (${certificate.validFrom})',
        details: {'validFrom': certificate.validFrom.toIso8601String()},
      );
    }

    // 检查是否即将过期
    final daysUntilExpiry = certificate.validUntil.difference(now).inDays;
    if (daysUntilExpiry <= 7) {
      warnings.add('证书将在 $daysUntilExpiry 天内过期');
    } else if (daysUntilExpiry <= 30) {
      warnings.add('证书将在 $daysUntilExpiry 天内过期');
    }

    return CertificateVerificationResult.valid(
      chainLength: 0,
      warnings: warnings,
      details: {'daysUntilExpiry': daysUntilExpiry},
    );
  }

  /// 主机名验证
  CertificateVerificationResult _verifyHostname(
    QuantumSecureCertificate certificate,
    String hostname,
  ) {
    final warnings = <String>[];

    // 检查CN匹配
    if (certificate.subjectName.contains(hostname)) {
      return CertificateVerificationResult.valid(chainLength: 0);
    }

    // 检查SAN DNS名称
    if (certificate.sanDnsNames != null) {
      for (final san in certificate.sanDnsNames!) {
        if (_matchHostname(hostname, san)) {
          return CertificateVerificationResult.valid(chainLength: 0);
        }
      }
    }

    // 检查SAN IP地址
    if (certificate.sanIpAddresses != null) {
      if (certificate.sanIpAddresses!.contains(hostname)) {
        return CertificateVerificationResult.valid(chainLength: 0);
      }
    }

    return CertificateVerificationResult.invalid(
      '主机名不匹配: $hostname',
      details: {
        'expectedHostname': hostname,
        'subjectName': certificate.subjectName,
        'sanDnsNames': certificate.sanDnsNames,
      },
    );
  }

  /// 主机名匹配（支持通配符）
  bool _matchHostname(String hostname, String pattern) {
    if (pattern == hostname) return true;
    
    // 通配符匹配
    if (pattern.startsWith('*.')) {
      final domain = pattern.substring(2);
      return hostname == domain || hostname.endsWith('.$domain');
    }
    
    return false;
  }

  /// 吊销检查
  Future<CertificateVerificationResult> _checkRevocation(
    QuantumSecureCertificate certificate,
  ) async {
    // 本地吊销状态检查
    if (certificate.isRevoked) {
      return CertificateVerificationResult.revoked(
        '证书已被吊销: ${certificate.revocationReason ?? "原因未知"}',
        details: {
          'revokedAt': certificate.revokedAt?.toIso8601String(),
          'reason': certificate.revocationReason,
        },
      );
    }

    if (!_enableOnlineValidation || _apiBaseUrl == null) {
      return CertificateVerificationResult.valid(
        chainLength: 0,
        warnings: ['在线吊销检查已禁用'],
      );
    }

    try {
      // 远程验证
      final response = await http.post(
        Uri.parse('$_apiBaseUrl/api/v1/certificates/verify'),
        headers: {'Content-Type': 'application/json'},
        body: jsonEncode({
          'certificateId': certificate.certificateId,
          'serialNumber': certificate.serialNumber,
        }),
      );

      if (response.statusCode == 200) {
        final result = jsonDecode(response.body);
        if (result['revoked'] == true) {
          return CertificateVerificationResult.revoked(
            result['reason'] ?? '证书已被吊销',
            details: result,
          );
        }
        return CertificateVerificationResult.valid(
          chainLength: 0,
          details: result,
        );
      }
    } catch (e) {
      return CertificateVerificationResult.valid(
        chainLength: 0,
        warnings: ['吊销检查失败: $e'],
      );
    }

    return CertificateVerificationResult.valid(chainLength: 0);
  }

  /// 证书链验证
  Future<CertificateVerificationResult> _verifyCertificateChain(
    QuantumSecureCertificate certificate,
  ) async {
    // 根证书不需要链验证
    if (certificate.certificateType == 'ROOT_CA') {
      final isTrusted = await _isTrustedRoot(certificate);
      return CertificateVerificationResult(
        isValid: isTrusted,
        isTrusted: isTrusted,
        isRevoked: false,
        hasValidChain: true,
        chainLength: 1,
        rootCaSubject: certificate.subjectName,
        warnings: isTrusted ? [] : ['不受信任的根证书'],
      );
    }

    // 尝试构建证书链
    int chainLength = 1;
    String? currentCertId = certificate.parentCertificateId;
    String? rootCaSubject;
    final warnings = <String>[];

    while (currentCertId != null && chainLength < 10) {
      final parentCert = await _getCertificate(currentCertId);
      if (parentCert == null) {
        warnings.add('无法获取父证书: $currentCertId');
        break;
      }

      chainLength++;

      // 检查父证书是否有效
      if (!parentCert.isValid) {
        return CertificateVerificationResult.invalid(
          '父证书无效: ${parentCert.certificateId}',
          warnings: warnings,
        );
      }

      // 检查父证书是否被吊销
      if (parentCert.isRevoked) {
        return CertificateVerificationResult.revoked(
          '父证书已被吊销: ${parentCert.certificateId}',
          warnings: warnings,
        );
      }

      // 如果是根证书，检查是否受信任
      if (parentCert.certificateType == 'ROOT_CA') {
        final isTrusted = await _isTrustedRoot(parentCert);
        rootCaSubject = parentCert.subjectName;
        
        if (!isTrusted) {
          warnings.add('根证书不受信任: ${parentCert.subjectName}');
        }

        return CertificateVerificationResult(
          isValid: !warnings.contains('根证书不受信任'),
          isTrusted: isTrusted,
          isRevoked: false,
          hasValidChain: true,
          chainLength: chainLength,
          rootCaSubject: rootCaSubject,
          warnings: warnings,
        );
      }

      currentCertId = parentCert.parentCertificateId;
    }

    return CertificateVerificationResult(
      isValid: warnings.isEmpty,
      isTrusted: false,
      isRevoked: false,
      hasValidChain: chainLength > 1,
      chainLength: chainLength,
      rootCaSubject: rootCaSubject,
      warnings: warnings..add('证书链不完整'),
    );
  }

  /// 检查是否为受信任的根证书
  Future<bool> _isTrustedRoot(QuantumSecureCertificate certificate) async {
    if (_trustedRootFingerprints.contains(certificate.fingerprint)) {
      return true;
    }
    // 如果指纹匹配内置根证书
    if (certificate.issuerId == certificate.subjectId) {
      // 自签名根证书，检查是否在白名单中
      return _trustedRootFingerprints.isEmpty || 
             _trustedRootFingerprints.contains(certificate.fingerprint);
    }
    return false;
  }

  /// 获取证书（从缓存或远程）
  Future<QuantumSecureCertificate?> _getCertificate(String certificateId) async {
    // 检查缓存
    if (_certificateCache.containsKey(certificateId)) {
      return _certificateCache[certificateId];
    }

    // 从存储加载
    try {
      final certJson = await _storage.read('cert_$certificateId');
      if (certJson != null) {
        final cert = QuantumSecureCertificate.fromJson(jsonDecode(certJson));
        _certificateCache[certificateId] = cert;
        return cert;
      }
    } catch (e) {
      if (kDebugMode) {
        print('加载证书失败: $e');
      }
    }

    // 从远程加载
    if (_apiBaseUrl != null) {
      try {
        final response = await http.get(
          Uri.parse('$_apiBaseUrl/api/v1/certificates/$certificateId'),
        );
        if (response.statusCode == 200) {
          final cert = QuantumSecureCertificate.fromJson(jsonDecode(response.body));
          _certificateCache[certificateId] = cert;
          await _storage.write('cert_$certificateId', jsonEncode(cert.toJson()));
          return cert;
        }
      } catch (e) {
        if (kDebugMode) {
          print('远程加载证书失败: $e');
        }
      }
    }

    return null;
  }

  /// 添加受信任的根证书
  Future<void> addTrustedRoot(String fingerprint) async {
    _trustedRootFingerprints.add(fingerprint);
    await _saveTrustedRoots();
  }

  /// 移除受信任的根证书
  Future<void> removeTrustedRoot(String fingerprint) async {
    _trustedRootFingerprints.remove(fingerprint);
    await _saveTrustedRoots();
  }

  /// 获取所有受信任的根证书
  Set<String> get trustedRootFingerprints => 
      Set.unmodifiable(_trustedRootFingerprints);

  /// 清除缓存
  Future<void> clearCache() async {
    _certificateCache.clear();
    _verificationCache.clear();
    await _storage.delete('verification_cache');
  }

  /// 批量验证证书
  Future<Map<String, CertificateVerificationResult>> verifyCertificates(
    List<QuantumSecureCertificate> certificates, {
    bool forceRefresh = false,
  }) async {
    final results = <String, CertificateVerificationResult>{};
    
    for (final cert in certificates) {
      results[cert.certificateId] = await verifyCertificate(
        cert,
        forceRefresh: forceRefresh,
      );
    }

    return results;
  }

  /// 导出验证报告
  Future<String> exportVerificationReport(
    QuantumSecureCertificate certificate,
    CertificateVerificationResult result,
  ) async {
    final report = {
      'exportTime': DateTime.now().toIso8601String(),
      'certificate': certificate.toJson(),
      'verificationResult': result.toJson(),
      'clientInfo': {
        'platform': Platform.operatingSystem,
        'version': Platform.operatingSystemVersion,
      },
    };
    return jsonEncode(report);
  }
}
