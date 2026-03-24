/// 去中心化身份系统移动端数据模型
///
/// 基于区块链和W3C DID标准的身份验证系统移动端模型定义
///
/// @version 1.0.0
/// @since 2026-03-24

import 'package:json_annotation/json_annotation.dart';

part 'decentralized_identity_model.g.dart';

/// 区块链类型枚举
enum BlockchainType {
  @JsonValue('ETHEREUM')
  ethereum,
  @JsonValue('POLYGON')
  polygon,
  @JsonValue('SOLANA')
  solana,
  @JsonValue('BINANCE_SMART_CHAIN')
  binanceSmartChain,
  @JsonValue('ARBITRUM')
  arbitrum,
  @JsonValue('OPTIMISM')
  optimism,
  @JsonValue('BASE')
  base,
  @JsonValue('AVALANCHE')
  avalanche,
  @JsonValue('NEAR')
  near,
  @JsonValue('COSMOS')
  cosmos,
  @JsonValue('CELESTIA')
  celestia,
  @JsonValue('APTOS')
  aptos,
  @JsonValue('SUI')
  sui,
  @JsonValue('CARDANO')
  cardano,
  @JsonValue('TEZOS')
  tezos,
  @JsonValue('ALGORAND')
  algorand,
  @JsonValue('HEDERA')
  hedera,
  @JsonValue('STARKNET')
  starknet,
  @JsonValue('ZKSYNC')
  zksync,
}

/// 身份状态枚举
enum IdentityStatus {
  @JsonValue('ACTIVE')
  active,
  @JsonValue('INACTIVE')
  inactive,
  @JsonValue('SUSPENDED')
  suspended,
  @JsonValue('PENDING_VERIFICATION')
  pendingVerification,
  @JsonValue('REVOKED')
  revoked,
}

/// 凭证类型枚举
enum CredentialType {
  @JsonValue('IDENTITY_VERIFICATION')
  identityVerification,
  @JsonValue('KYC_COMPLIANCE')
  kycCompliance,
  @JsonValue('AGE_VERIFICATION')
  ageVerification,
  @JsonValue('RESIDENCE_VERIFICATION')
  residenceVerification,
  @JsonValue('EDUCATION_CREDENTIAL')
  educationCredential,
  @JsonValue('PROFESSIONAL_LICENSE')
  professionalLicense,
  @JsonValue('MEMBERSHIP_CERTIFICATE')
  membershipCertificate,
  @JsonValue('SKILL_CERTIFICATION')
  skillCertification,
  @JsonValue('REPUTATION_SCORE')
  reputationScore,
  @JsonValue('TRANSACTION_HISTORY')
  transactionHistory,
  @JsonValue('GOVERNMENT_ID')
  governmentId,
  @JsonValue('PASSPORT')
  passport,
  @JsonValue('DRIVERS_LICENSE')
  driversLicense,
}

/// 凭证状态枚举
enum CredentialStatus {
  @JsonValue('VALID')
  valid,
  @JsonValue('EXPIRED')
  expired,
  @JsonValue('REVOKED')
  revoked,
  @JsonValue('SUSPENDED')
  suspended,
  @JsonValue('PENDING_ISSUANCE')
  pendingIssuance,
}

/// 零知识证明类型枚举
enum ZKPProofType {
  @JsonValue('IDENTITY_REVEAL')
  identityReveal,
  @JsonValue('AGE_RANGE')
  ageRange,
  @JsonValue('COUNTRY_RESIDENCE')
  countryResidence,
  @JsonValue('BALANCE_RANGE')
  balanceRange,
  @JsonValue('TRANSACTION_HISTORY')
  transactionHistory,
  @JsonValue('REPUTATION_SCORE')
  reputationScore,
  @JsonValue('MEMBERSHIP_PROOF')
  membershipProof,
  @JsonValue('CREDENTIAL_VERIFICATION')
  credentialVerification,
  @JsonValue('ATTRIBUTE_NON_DISCLOSURE')
  attributeNonDisclosure,
}

/// 隐私级别枚举
enum PrivacyLevel {
  @JsonValue('PUBLIC')
  public,
  @JsonValue('PRIVATE')
  private,
  @JsonValue('CONFIDENTIAL')
  confidential,
  @JsonValue('SECRET')
  secret,
  @JsonValue('TOP_SECRET')
  topSecret,
}

/// 跨链同步状态枚举
enum CrossChainSyncStatus {
  @JsonValue('SYNCED')
  synced,
  @JsonValue('SYNCING')
  syncing,
  @JsonValue('FAILED')
  failed,
  @JsonValue('PENDING')
  pending,
  @JsonValue('NOT_SUPPORTED')
  notSupported,
}

/// 去中心化身份实体模型
@JsonSerializable(explicitToJson: true)
class DecentralizedIdentity {
  @JsonKey(name: 'identityId')
  final String identityId;

  @JsonKey(name: 'userId')
  final String userId;

  @JsonKey(name: 'identityAddress')
  final String identityAddress;

  @JsonKey(name: 'blockchainType')
  final BlockchainType blockchainType;

  @JsonKey(name: 'publicKey')
  final String publicKey;

  @JsonKey(name: 'status')
  final IdentityStatus status;

  @JsonKey(name: 'didDocument')
  final DIDDocument? didDocument;

  @JsonKey(name: 'metadata')
  final Map<String, dynamic>? metadata;

  @JsonKey(name: 'reputationScore')
  final double reputationScore;

  @JsonKey(name: 'reputationRank')
  final int reputationRank;

  @JsonKey(name: 'verificationCount')
  final int verificationCount;

  @JsonKey(name: 'lastVerificationTime')
  final int? lastVerificationTime;

  @JsonKey(name: 'createdTime')
  final int createdTime;

  @JsonKey(name: 'updatedTime')
  final int updatedTime;

  @JsonKey(name: 'syncedChains')
  final List<BlockchainType> syncedChains;

  @JsonKey(name: 'attributes')
  final Map<String, dynamic>? attributes;

  DecentralizedIdentity({
    required this.identityId,
    required this.userId,
    required this.identityAddress,
    required this.blockchainType,
    required this.publicKey,
    required this.status,
    this.didDocument,
    this.metadata,
    required this.reputationScore,
    required this.reputationRank,
    required this.verificationCount,
    this.lastVerificationTime,
    required this.createdTime,
    required this.updatedTime,
    required this.syncedChains,
    this.attributes,
  });

  factory DecentralizedIdentity.fromJson(Map<String, dynamic> json) =>
      _$DecentralizedIdentityFromJson(json);

  Map<String, dynamic> toJson() => _$DecentralizedIdentityToJson(this);

  /// 获取DID标识符
  String get didIdentifier {
    switch (blockchainType) {
      case BlockchainType.ethereum:
        return 'did:eth:$identityAddress';
      case BlockchainType.polygon:
        return 'did:polygon:$identityAddress';
      case BlockchainType.solana:
        return 'did:sol:$identityAddress';
      case BlockchainType.binanceSmartChain:
        return 'did:bsc:$identityAddress';
      case BlockchainType.arbitrum:
        return 'did:arbitrum:$identityAddress';
      case BlockchainType.optimism:
        return 'did:optimism:$identityAddress';
      case BlockchainType.base:
        return 'did:base:$identityAddress';
      case BlockchainType.avalanche:
        return 'did:avax:$identityAddress';
      case BlockchainType.near:
        return 'did:near:$identityAddress';
      case BlockchainType.cosmos:
        return 'did:cosmos:$identityAddress';
      case BlockchainType.celestia:
        return 'did:celestia:$identityAddress';
      case BlockchainType.aptos:
        return 'did:aptos:$identityAddress';
      case BlockchainType.sui:
        return 'did:sui:$identityAddress';
      case BlockchainType.cardano:
        return 'did:cardano:$identityAddress';
      case BlockchainType.tezos:
        return 'did:tezos:$identityAddress';
      case BlockchainType.algorand:
        return 'did:algo:$identityAddress';
      case BlockchainType.hedera:
        return 'did:hedera:$identityAddress';
      case BlockchainType.starknet:
        return 'did:starknet:$identityAddress';
      case BlockchainType.zksync:
        return 'did:zksync:$identityAddress';
    }
  }

  /// 检查身份是否活跃
  bool get isActive => status == IdentityStatus.active;

  /// 检查身份是否已验证
  bool get isVerified => verificationCount > 0;

  /// 获取格式化地址
  String get formattedAddress {
    const prefixes = {
      BlockchainType.ethereum: '0x',
      BlockchainType.polygon: '0x',
      BlockchainType.binanceSmartChain: '0x',
      BlockchainType.arbitrum: '0x',
      BlockchainType.optimism: '0x',
      BlockchainType.base: '0x',
      BlockchainType.avalanche: '0x',
      BlockchainType.solana: '',
      BlockchainType.near: '',
      BlockchainType.cosmos: 'cosmos1',
      BlockchainType.celestia: 'celestia1',
      BlockchainType.aptos: '0x',
      BlockchainType.sui: '0x',
      BlockchainType.cardano: 'addr1',
      BlockchainType.tezos: 'tz1',
      BlockchainType.algorand: '',
      BlockchainType.hedera: '0.0.',
      BlockchainType.starknet: '0x',
      BlockchainType.zksync: '0x',
    };

    final prefix = prefixes[blockchainType];
    if (prefix != null && !identityAddress.startsWith(prefix)) {
      return '$prefix$identityAddress';
    }
    return identityAddress;
  }
}

/// W3C DID 文档模型
@JsonSerializable(explicitToJson: true)
class DIDDocument {
  @JsonKey(name: '@context')
  final List<String> context;

  @JsonKey(name: 'id')
  final String id;

  @JsonKey(name: 'controller')
  final List<String> controller;

  @JsonKey(name: 'verificationMethod')
  final List<VerificationMethod> verificationMethod;

  @JsonKey(name: 'authentication')
  final List<String> authentication;

  @JsonKey(name: 'assertionMethod')
  final List<String> assertionMethod;

  @JsonKey(name: 'keyAgreement')
  final List<String> keyAgreement;

  @JsonKey(name: 'capabilityInvocation')
  final List<String> capabilityInvocation;

  @JsonKey(name: 'capabilityDelegation')
  final List<String> capabilityDelegation;

  @JsonKey(name: 'service')
  final List<ServiceEndpoint>? service;

  @JsonKey(name: 'created')
  final String? created;

  @JsonKey(name: 'updated')
  final String? updated;

  @JsonKey(name: 'proof')
  final LinkedDataProof? proof;

  DIDDocument({
    required this.context,
    required this.id,
    required this.controller,
    required this.verificationMethod,
    required this.authentication,
    required this.assertionMethod,
    required this.keyAgreement,
    required this.capabilityInvocation,
    required this.capabilityDelegation,
    this.service,
    this.created,
    this.updated,
    this.proof,
  });

  factory DIDDocument.fromJson(Map<String, dynamic> json) =>
      _$DIDDocumentFromJson(json);

  Map<String, dynamic> toJson() => _$DIDDocumentToJson(this);
}

/// 验证方法模型
@JsonSerializable(explicitToJson: true)
class VerificationMethod {
  @JsonKey(name: 'id')
  final String id;

  @JsonKey(name: 'type')
  final String type;

  @JsonKey(name: 'controller')
  final String controller;

  @JsonKey(name: 'publicKeyMultibase')
  final String? publicKeyMultibase;

  @JsonKey(name: 'publicKeyJwk')
  final Map<String, dynamic>? publicKeyJwk;

  @JsonKey(name: 'blockchainAccountId')
  final String? blockchainAccountId;

  VerificationMethod({
    required this.id,
    required this.type,
    required this.controller,
    this.publicKeyMultibase,
    this.publicKeyJwk,
    this.blockchainAccountId,
  });

  factory VerificationMethod.fromJson(Map<String, dynamic> json) =>
      _$VerificationMethodFromJson(json);

  Map<String, dynamic> toJson() => _$VerificationMethodToJson(this);
}

/// 服务端点模型
@JsonSerializable(explicitToJson: true)
class ServiceEndpoint {
  @JsonKey(name: 'id')
  final String id;

  @JsonKey(name: 'type')
  final String type;

  @JsonKey(name: 'serviceEndpoint')
  final String serviceEndpoint;

  @JsonKey(name: 'description')
  final String? description;

  ServiceEndpoint({
    required this.id,
    required this.type,
    required this.serviceEndpoint,
    this.description,
  });

  factory ServiceEndpoint.fromJson(Map<String, dynamic> json) =>
      _$ServiceEndpointFromJson(json);

  Map<String, dynamic> toJson() => _$ServiceEndpointToJson(this);
}

/// 链接数据证明模型
@JsonSerializable(explicitToJson: true)
class LinkedDataProof {
  @JsonKey(name: 'type')
  final String type;

  @JsonKey(name: 'created')
  final String created;

  @JsonKey(name: 'verificationMethod')
  final String verificationMethod;

  @JsonKey(name: 'proofPurpose')
  final String proofPurpose;

  @JsonKey(name: 'jws')
  final String? jws;

  @JsonKey(name: 'proofValue')
  final String? proofValue;

  LinkedDataProof({
    required this.type,
    required this.created,
    required this.verificationMethod,
    required this.proofPurpose,
    this.jws,
    this.proofValue,
  });

  factory LinkedDataProof.fromJson(Map<String, dynamic> json) =>
      _$LinkedDataProofFromJson(json);

  Map<String, dynamic> toJson() => _$LinkedDataProofToJson(this);
}

/// 可验证凭证实体模型
@JsonSerializable(explicitToJson: true)
class VerifiableCredential {
  @JsonKey(name: 'credentialId')
  final String credentialId;

  @JsonKey(name: 'issuerId')
  final String issuerId;

  @JsonKey(name: 'subjectId')
  final String subjectId;

  @JsonKey(name: 'credentialType')
  final CredentialType credentialType;

  @JsonKey(name: 'credentialSchema')
  final Map<String, dynamic> credentialSchema;

  @JsonKey(name: 'credentialSubject')
  final Map<String, dynamic> credentialSubject;

  @JsonKey(name: 'issuanceDate')
  final int issuanceDate;

  @JsonKey(name: 'expirationDate')
  final int? expirationDate;

  @JsonKey(name: 'proof')
  final Map<String, dynamic> proof;

  @JsonKey(name: 'status')
  final CredentialStatus status;

  @JsonKey(name: 'revocationRegistryId')
  final String? revocationRegistryId;

  @JsonKey(name: 'credentialRevocationId')
  final String? credentialRevocationId;

  @JsonKey(name: 'metadata')
  final Map<String, dynamic>? metadata;

  @JsonKey(name: 'createdTime')
  final int createdTime;

  @JsonKey(name: 'updatedTime')
  final int updatedTime;

  VerifiableCredential({
    required this.credentialId,
    required this.issuerId,
    required this.subjectId,
    required this.credentialType,
    required this.credentialSchema,
    required this.credentialSubject,
    required this.issuanceDate,
    this.expirationDate,
    required this.proof,
    required this.status,
    this.revocationRegistryId,
    this.credentialRevocationId,
    this.metadata,
    required this.createdTime,
    required this.updatedTime,
  });

  factory VerifiableCredential.fromJson(Map<String, dynamic> json) =>
      _$VerifiableCredentialFromJson(json);

  Map<String, dynamic> toJson() => _$VerifiableCredentialToJson(this);

  /// 检查凭证是否有效
  bool get isValid => status == CredentialStatus.valid;

  /// 检查凭证是否已过期
  bool get isExpired => expirationDate != null && 
      DateTime.fromMillisecondsSinceEpoch(expirationDate!).isBefore(DateTime.now());

  /// 检查凭证是否可撤销
  bool get isRevocable => revocationRegistryId != null && 
      credentialRevocationId != null;
}

/// 零知识证明结果模型
@JsonSerializable(explicitToJson: true)
class ZKPProofResult {
  @JsonKey(name: 'proofId')
  final String proofId;

  @JsonKey(name: 'proofData')
  final String proofData;

  @JsonKey(name: 'verificationKey')
  final String verificationKey;

  @JsonKey(name: 'generationTimeMs')
  final int generationTimeMs;

  @JsonKey(name: 'proofSizeBytes')
  final int proofSizeBytes;

  @JsonKey(name: 'proofType')
  final ZKPProofType proofType;

  @JsonKey(name: 'privacyLevel')
  final PrivacyLevel privacyLevel;

  @JsonKey(name: 'circuitType')
  final String? circuitType;

  @JsonKey(name: 'proofHash')
  final String? proofHash;

  ZKPProofResult({
    required this.proofId,
    required this.proofData,
    required this.verificationKey,
    required this.generationTimeMs,
    required this.proofSizeBytes,
    required this.proofType,
    required this.privacyLevel,
    this.circuitType,
    this.proofHash,
  });

  factory ZKPProofResult.fromJson(Map<String, dynamic> json) =>
      _$ZKPProofResultFromJson(json);

  Map<String, dynamic> toJson() => _$ZKPProofResultToJson(this);
}

/// 声誉评分模型
@JsonSerializable(explicitToJson: true)
class ReputationScore {
  @JsonKey(name: 'identityId')
  final String identityId;

  @JsonKey(name: 'score')
  final double score;

  @JsonKey(name: 'rank')
  final int rank;

  @JsonKey(name: 'lastUpdateTime')
  final int lastUpdateTime;

  @JsonKey(name: 'positiveFactors')
  final List<String> positiveFactors;

  @JsonKey(name: 'negativeFactors')
  final List<String> negativeFactors;

  @JsonKey(name: 'historicalScores')
  final List<double>? historicalScores;

  @JsonKey(name: 'trend')
  final String? trend;

  ReputationScore({
    required this.identityId,
    required this.score,
    required this.rank,
    required this.lastUpdateTime,
    required this.positiveFactors,
    required this.negativeFactors,
    this.historicalScores,
    this.trend,
  });

  factory ReputationScore.fromJson(Map<String, dynamic> json) =>
      _$ReputationScoreFromJson(json);

  Map<String, dynamic> toJson() => _$ReputationScoreToJson(this);

  /// 获取趋势枚举
  ReputationTrend? get trendEnum {
    switch (trend?.toLowerCase()) {
      case 'increasing':
        return ReputationTrend.increasing;
      case 'decreasing':
        return ReputationTrend.decreasing;
      case 'stable':
        return ReputationTrend.stable;
      default:
        return null;
    }
  }
}

/// 声誉趋势枚举
enum ReputationTrend {
  increasing,
  decreasing,
  stable,
}

/// 跨链同步结果模型
@JsonSerializable(explicitToJson: true)
class CrossChainSyncResult {
  @JsonKey(name: 'identityId')
  final String identityId;

  @JsonKey(name: 'chainSyncStatus')
  final Map<String, SyncStatus> chainSyncStatus;

  @JsonKey(name: 'failedChains')
  final List<String> failedChains;

  @JsonKey(name: 'syncTime')
  final int syncTime;

  CrossChainSyncResult({
    required this.identityId,
    required this.chainSyncStatus,
    required this.failedChains,
    required this.syncTime,
  });

  factory CrossChainSyncResult.fromJson(Map<String, dynamic> json) =>
      _$CrossChainSyncResultFromJson(json);

  Map<String, dynamic> toJson() => _$CrossChainSyncResultToJson(this);

  /// 获取同步成功的链
  List<String> get syncedChains => chainSyncStatus.entries
      .where((entry) => entry.value.synced)
      .map((entry) => entry.key)
      .toList();

  /// 检查是否全部同步成功
  bool get allSynced => failedChains.isEmpty;
}

/// 同步状态模型
@JsonSerializable(explicitToJson: true)
class SyncStatus {
  @JsonKey(name: 'chain')
  final String chain;

  @JsonKey(name: 'synced')
  final bool synced;

  @JsonKey(name: 'transactionHash')
  final String? transactionHash;

  @JsonKey(name: 'blockNumber')
  final int? blockNumber;

  @JsonKey(name: 'timestamp')
  final int? timestamp;

  @JsonKey(name: 'gasUsed')
  final int? gasUsed;

  SyncStatus({
    required this.chain,
    required this.synced,
    this.transactionHash,
    this.blockNumber,
    this.timestamp,
    this.gasUsed,
  });

  factory SyncStatus.fromJson(Map<String, dynamic> json) =>
      _$SyncStatusFromJson(json);

  Map<String, dynamic> toJson() => _$SyncStatusToJson(this);
}

/// 系统统计信息模型
@JsonSerializable(explicitToJson: true)
class DIDSystemStatistics {
  @JsonKey(name: 'totalIdentities')
  final int totalIdentities;

  @JsonKey(name: 'activeIdentities')
  final int activeIdentities;

  @JsonKey(name: 'identitiesByChain')
  final Map<String, int> identitiesByChain;

  @JsonKey(name: 'totalCredentials')
  final int totalCredentials;

  @JsonKey(name: 'validCredentials')
  final int validCredentials;

  @JsonKey(name: 'totalZKProofs')
  final int totalZKProofs;

  @JsonKey(name: 'averageReputationScore')
  final double averageReputationScore;

  @JsonKey(name: 'totalCrossChainSyncs')
  final int totalCrossChainSyncs;

  @JsonKey(name: 'performanceMetrics')
  final Map<String, dynamic> performanceMetrics;

  DIDSystemStatistics({
    required this.totalIdentities,
    required this.activeIdentities,
    required this.identitiesByChain,
    required this.totalCredentials,
    required this.validCredentials,
    required this.totalZKProofs,
    required this.averageReputationScore,
    required this.totalCrossChainSyncs,
    required this.performanceMetrics,
  });

  factory DIDSystemStatistics.fromJson(Map<String, dynamic> json) =>
      _$DIDSystemStatisticsFromJson(json);

  Map<String, dynamic> toJson() => _$DIDSystemStatisticsToJson(this);
}

/// API响应模型
@JsonSerializable(explicitToJson: true, genericArgumentFactories: true)
class ApiResponse<T> {
  @JsonKey(name: 'success')
  final bool success;

  @JsonKey(name: 'data')
  final T? data;

  @JsonKey(name: 'error')
  final ApiError? error;

  @JsonKey(name: 'timestamp')
  final int timestamp;

  ApiResponse({
    required this.success,
    this.data,
    this.error,
    required this.timestamp,
  });

  factory ApiResponse.fromJson(
    Map<String, dynamic> json,
    T Function(Object? json) fromJsonT,
  ) =>
      _$ApiResponseFromJson(json, fromJsonT);

  Map<String, dynamic> toJson(Object? Function(T value) toJsonT) =>
      _$ApiResponseToJson(this, toJsonT);
}

/// API错误模型
@JsonSerializable(explicitToJson: true)
class ApiError {
  @JsonKey(name: 'code')
  final String code;

  @JsonKey(name: 'message')
  final String message;

  @JsonKey(name: 'details')
  final Map<String, dynamic>? details;

  ApiError({
    required this.code,
    required this.message,
    this.details,
  });

  factory ApiError.fromJson(Map<String, dynamic> json) =>
      _$ApiErrorFromJson(json);

  Map<String, dynamic> toJson() => _$ApiErrorToJson(this);
}

/// 分页响应模型
@JsonSerializable(explicitToJson: true, genericArgumentFactories: true)
class PaginatedResponse<T> {
  @JsonKey(name: 'items')
  final List<T> items;

  @JsonKey(name: 'total')
  final int total;

  @JsonKey(name: 'page')
  final int page;

  @JsonKey(name: 'pageSize')
  final int pageSize;

  @JsonKey(name: 'hasNext')
  final bool hasNext;

  @JsonKey(name: 'hasPrev')
  final bool hasPrev;

  PaginatedResponse({
    required this.items,
    required this.total,
    required this.page,
    required this.pageSize,
    required this.hasNext,
    required this.hasPrev,
  });

  factory PaginatedResponse.fromJson(
    Map<String, dynamic> json,
    T Function(Object? json) fromJsonT,
  ) =>
      _$PaginatedResponseFromJson(json, fromJsonT);

  Map<String, dynamic> toJson(Object? Function(T value) toJsonT) =>
      _$PaginatedResponseToJson(this, toJsonT);
}

/// 请求模型类

@JsonSerializable(explicitToJson: true)
class RegisterIdentityRequest {
  @JsonKey(name: 'userId')
  final String userId;

  @JsonKey(name: 'identityAddress')
  final String identityAddress;

  @JsonKey(name: 'blockchainType')
  final BlockchainType blockchainType;

  @JsonKey(name: 'publicKey')
  final String publicKey;

  @JsonKey(name: 'metadata')
  final Map<String, dynamic>? metadata;

  RegisterIdentityRequest({
    required this.userId,
    required this.identityAddress,
    required this.blockchainType,
    required this.publicKey,
    this.metadata,
  });

  factory RegisterIdentityRequest.fromJson(Map<String, dynamic> json) =>
      _$RegisterIdentityRequestFromJson(json);

  Map<String, dynamic> toJson() => _$RegisterIdentityRequestToJson(this);
}

@JsonSerializable(explicitToJson: true)
class SignatureVerificationRequest {
  @JsonKey(name: 'message')
  final String message;

  @JsonKey(name: 'signature')
  final String signature;

  SignatureVerificationRequest({
    required this.message,
    required this.signature,
  });

  factory SignatureVerificationRequest.fromJson(Map<String, dynamic> json) =>
      _$SignatureVerificationRequestFromJson(json);

  Map<String, dynamic> toJson() => _$SignatureVerificationRequestToJson(this);
}

@JsonSerializable(explicitToJson: true)
class VerificationResult {
  @JsonKey(name: 'identityId')
  final String identityId;

  @JsonKey(name: 'isValid')
  final bool isValid;

  @JsonKey(name: 'verificationTime')
  final int verificationTime;

  @JsonKey(name: 'verificationMethod')
  final String verificationMethod;

  VerificationResult({
    required this.identityId,
    required this.isValid,
    required this.verificationTime,
    required this.verificationMethod,
  });

  factory VerificationResult.fromJson(Map<String, dynamic> json) =>
      _$VerificationResultFromJson(json);

  Map<String, dynamic> toJson() => _$VerificationResultToJson(this);
}