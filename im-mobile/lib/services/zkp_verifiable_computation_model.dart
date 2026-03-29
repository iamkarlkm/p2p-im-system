enum ComputationType { federatedLearning, privacyAuth, messageIntegrity, selectiveCredential, homomorphicVerify, mpcProof, psiProof, verifiableRandom }
enum CircuitType { groth16, plonk, marlin, sonic, aurora, fractal }
enum ComputationStatus { pending, generatingProof, proofGenerated, verifyingProof, verified, failed, cancelled }
enum ProtectionType { identityAuth, selectiveDisclosure, anonymousCredential, rangeProof, membershipProof, nonMembershipProof, bulkVerification, crossChainProof }
enum ProtectionStatus { active, revoked, expired, suspended, pendingVerification }

class ZKPComputation {
  final String id;
  final String computationId;
  final String userId;
  final String? sessionId;
  final ComputationType computationType;
  final CircuitType circuitType;
  final int? circuitSize;
  final Map<String, dynamic>? publicInputs;
  final String? privateInputsHash;
  final bool proofGenerated;
  final bool proofVerified;
  final String? proofData;
  final String? verificationKeyHash;
  final int? proofSizeBytes;
  final int? generationTimeMs;
  final int? verificationTimeMs;
  final int securityLevel;
  final ComputationStatus computationStatus;
  final String? errorMessage;
  final int retryCount;
  final DateTime createdAt;
  final DateTime updatedAt;

  ZKPComputation({required this.id, required this.computationId, required this.userId, this.sessionId, required this.computationType, required this.circuitType, this.circuitSize, this.publicInputs, this.privateInputsHash, required this.proofGenerated, required this.proofVerified, this.proofData, this.verificationKeyHash, this.proofSizeBytes, this.generationTimeMs, this.verificationTimeMs, required this.securityLevel, required this.computationStatus, this.errorMessage, required this.retryCount, required this.createdAt, required this.updatedAt});

  factory ZKPComputation.fromJson(Map<String, dynamic> json) => ZKPComputation(
    id: json['id'], computationId: json['computationId'], userId: json['userId'],
    sessionId: json['sessionId'], computationType: ComputationType.values.firstWhere((e) => e.name == json['computationType']),
    circuitType: CircuitType.values.firstWhere((e) => e.name == json['circuitType']), circuitSize: json['circuitSize'],
    publicInputs: json['publicInputs'], privateInputsHash: json['privateInputsHash'], proofGenerated: json['proofGenerated'] ?? false,
    proofVerified: json['proofVerified'] ?? false, proofData: json['proofData'], verificationKeyHash: json['verificationKeyHash'],
    proofSizeBytes: json['proofSizeBytes'], generationTimeMs: json['generationTimeMs'], verificationTimeMs: json['verificationTimeMs'],
    securityLevel: json['securityLevel'] ?? 128, computationStatus: ComputationStatus.values.firstWhere((e) => e.name == json['computationStatus']),
    errorMessage: json['errorMessage'], retryCount: json['retryCount'] ?? 0, createdAt: DateTime.parse(json['createdAt']), updatedAt: DateTime.parse(json['updatedAt']));

  Map<String, dynamic> toJson() => {'id': id, 'computationId': computationId, 'userId': userId, 'sessionId': sessionId, 'computationType': computationType.name, 'circuitType': circuitType.name, 'circuitSize': circuitSize, 'proofGenerated': proofGenerated, 'proofVerified': proofVerified, 'securityLevel': securityLevel, 'computationStatus': computationStatus.name, 'retryCount': retryCount, 'createdAt': createdAt.toIso8601String(), 'updatedAt': updatedAt.toIso8601String()};
}

class ZKPPrivacyProtection {
  final String id;
  final String protectionId;
  final String userId;
  final ProtectionType protectionType;
  final Map<String, dynamic>? credentialAttributes;
  final List<String>? disclosedAttributes;
  final bool? predicateSatisfied;
  final bool verificationResult;
  final double? verificationScore;
  final double? privacyPreservationScore;
  final ProtectionStatus protectionStatus;
  final DateTime validFrom;
  final DateTime? validTo;
  final DateTime createdAt;
  final DateTime updatedAt;

  ZKPPrivacyProtection({required this.id, required this.protectionId, required this.userId, required this.protectionType, this.credentialAttributes, this.disclosedAttributes, this.predicateSatisfied, required this.verificationResult, this.verificationScore, this.privacyPreservationScore, required this.protectionStatus, required this.validFrom, this.validTo, required this.createdAt, required this.updatedAt});

  factory ZKPPrivacyProtection.fromJson(Map<String, dynamic> json) => ZKPPrivacyProtection(
    id: json['id'], protectionId: json['protectionId'], userId: json['userId'],
    protectionType: ProtectionType.values.firstWhere((e) => e.name == json['protectionType']),
    credentialAttributes: json['credentialAttributes'], disclosedAttributes: json['disclosedAttributes']?.cast<String>(),
    predicateSatisfied: json['predicateSatisfied'], verificationResult: json['verificationResult'] ?? false,
    verificationScore: json['verificationScore']?.toDouble(), privacyPreservationScore: json['privacyPreservationScore']?.toDouble(),
    protectionStatus: ProtectionStatus.values.firstWhere((e) => e.name == json['protectionStatus']),
    validFrom: DateTime.parse(json['validFrom']), validTo: json['validTo'] != null ? DateTime.parse(json['validTo']) : null,
    createdAt: DateTime.parse(json['createdAt']), updatedAt: DateTime.parse(json['updatedAt']));

  Map<String, dynamic> toJson() => {'id': id, 'protectionId': protectionId, 'userId': userId, 'protectionType': protectionType.name, 'verificationResult': verificationResult, 'protectionStatus': protectionStatus.name, 'validFrom': validFrom.toIso8601String(), 'createdAt': createdAt.toIso8601String(), 'updatedAt': updatedAt.toIso8601String()};
}

class ZKPComputationApiService {
  // API methods would be implemented here
  Future<ZKPComputation?> getComputation(String computationId) async => null;
  Future<List<ZKPComputation>> getUserComputations(String userId, {int limit = 20}) async => [];
  Future<bool> createComputation(String userId, ComputationType type, CircuitType circuitType) async => false;
  Future<bool> generateProof(String computationId) async => false;
  Future<bool> verifyProof(String computationId) async => false;
}

class ZKPPrivacyApiService {
  // API methods would be implemented here
  Future<ZKPPrivacyProtection?> getProtection(String protectionId) async => null;
  Future<List<ZKPPrivacyProtection>> getUserProtections(String userId, {int limit = 20}) async => [];
  Future<bool> createProtection(String userId, ProtectionType type, Map<String, dynamic> attributes) async => false;
  Future<bool> verifyProtection(String protectionId, Map<String, dynamic> disclosedAttributes) async => false;
}
