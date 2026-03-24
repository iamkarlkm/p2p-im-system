/**
 * 量子抗性加密与后量子密码学类型定义
 * 
 * 支持算法：
 * 1. 基于格的加密 (CRYSTALS-Kyber)
 * 2. 基于哈希的签名 (SPHINCS+)
 * 3. 基于编码的加密 (Classic McEliece)
 * 4. 多变量密码 (Rainbow)
 * 5. 超奇异椭圆曲线同源 (SIKE)
 * 6. NIST标准化算法 (Dilithium, FALCON)
 */

export interface QuantumResistantEncryption {
  id: string;
  name: string;
  algorithmType: AlgorithmType;
  specificAlgorithm: SpecificAlgorithm;
  securityLevel: SecurityLevel;
  keySize: number;
  publicKey?: string;
  privateKey?: string;
  signatureKey?: string;
  isActive: boolean;
  expiresAt?: string;
  createdAt: string;
  updatedAt?: string;
  userId: string;
  description?: string;
  supportsQuantumKeyDistribution: boolean;
  isHybridEncryption: boolean;
  encryptionRounds: number;
  performanceScore?: number;
  securityScore?: number;
  metadata?: string;
  version?: number;
}

export interface PostQuantumSignature {
  id: string;
  signatureName: string;
  signatureAlgorithm: SignatureAlgorithm;
  specificVariant: string;
  signatureSize: number;
  publicKeySize: number;
  privateKeySize: number;
  signatureData?: string;
  publicKeyData?: string;
  privateKeyData?: string;
  messageId: string;
  userId: string;
  signedAt: string;
  expiresAt?: string;
  verificationStatus: VerificationStatus;
  verifiedAt?: string;
  verificationNotes?: string;
  securityLevel: SecurityLevel;
  isRevocable: boolean;
  supportsBatchVerification: boolean;
  verificationTimeMs?: number;
  signatureGenerationTimeMs?: number;
  signatureCount: number;
  signaturePurpose?: string;
  isTimestamped: boolean;
  timestampAuthorityUrl?: string;
  timestampVerifiedAt?: string;
  isCompliant: boolean;
  complianceDetails?: string;
  version?: number;
}

export type AlgorithmType = 
  | 'LATTICE' 
  | 'HASH_BASED' 
  | 'CODE_BASED' 
  | 'MULTIVARIATE' 
  | 'SIKE';

export type SpecificAlgorithm = 
  | 'CRYSTALS-Kyber' 
  | 'CRYSTALS-Kyber-768' 
  | 'Classic-McEliece'
  | 'SIKEp434'
  | 'SIKEp503'
  | 'CRYSTALS-Dilithium'
  | 'CRYSTALS-Dilithium3'
  | 'FALCON-512'
  | 'FALCON-1024'
  | 'SPHINCS+-SHA256-128f-simple'
  | 'Rainbow-Ia-Classic';

export type SignatureAlgorithm = 
  | 'DILITHIUM' 
  | 'FALCON' 
  | 'SPHINCS_PLUS' 
  | 'RAINBOW' 
  | 'ED448';

export type SecurityLevel = 1 | 2 | 3 | 4 | 5;

export type VerificationStatus = 'PENDING' | 'VERIFIED' | 'INVALID' | 'REVOKED';

export interface AlgorithmInfo {
  algorithmType: AlgorithmType;
  specificAlgorithm: SpecificAlgorithm;
  securityLevel: SecurityLevel;
  keySize: number;
  signatureSize: number;
}

export interface SupportedAlgorithmsResponse {
  encryptionAlgorithms: AlgorithmInfo[];
  signatureAlgorithms: AlgorithmInfo[];
  nistStandardized: boolean;
  quantumResistant: boolean;
  hybridEncryptionSupported: boolean;
  quantumKeyDistributionSupported: boolean;
  lastUpdated: string;
}

export interface SystemStatusResponse {
  systemName: string;
  version: string;
  status: 'ACTIVE' | 'MAINTENANCE' | 'OFFLINE';
  uptime: string;
  totalKeysGenerated: number;
  totalSignaturesCreated: number;
  successfulVerifications: number;
  failedVerifications: number;
  hybridSchemesCreated: number;
  lastHealthCheck: string;
  quantumSafetyLevel: string;
  nistComplianceLevel: string;
}

export interface CreateKeyPairRequest {
  name: string;
  algorithmType: AlgorithmType;
  specificAlgorithm: SpecificAlgorithm;
  securityLevel: SecurityLevel;
  keySize: number;
  userId: string;
  description?: string;
}

export interface UpdateKeyRequest {
  name?: string;
  description?: string;
  isActive?: boolean;
  expiresAt?: string;
}

export interface RotateKeyRequest {
  newKeyName: string;
  userId: string;
}

export interface CreateSignatureRequest {
  signatureName: string;
  signatureAlgorithm: SignatureAlgorithm;
  specificVariant: string;
  messageId: string;
  userId: string;
  signaturePurpose?: string;
}

export interface SignatureVerificationResponse {
  signatureId: string;
  valid: boolean;
  verifiedAt: string;
}

export interface BatchVerificationRequest {
  signatureIds: string[];
}

export interface BatchVerificationResponse {
  totalSignatures: number;
  verifiedCount: number;
  failedCount: number;
  verificationTime: string;
}

export interface RevokeSignatureRequest {
  reason?: string;
}

export interface CreateHybridSchemeRequest {
  traditionalKeyId: string;
  quantumKeyId: string;
  userId: string;
}

export interface HybridSchemeResponse {
  schemeId: string;
  traditionalKeyId: string;
  quantumKeyId: string;
  userId: string;
  createdAt: string;
  type: string;
}

export interface HybridEncryptionScheme {
  schemeId: string;
  traditionalKeyId: string;
  quantumKeyId: string;
  userId: string;
  createdAt: string;
  type: 'HYBRID_RSA_KYBER' | 'HYBRID_ECDH_KYBER' | 'HYBRID_X25519_KYBER';
}

// 分页响应
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

// 统计信息
export interface EncryptionStatistics {
  totalKeys: number;
  activeKeys: number;
  expiredKeys: number;
  byAlgorithmType: Record<AlgorithmType, number>;
  bySecurityLevel: Record<SecurityLevel, number>;
  totalSignatures: number;
  verifiedSignatures: number;
  revokedSignatures: number;
  averagePerformanceScore: number;
  averageSecurityScore: number;
}

// 密钥轮换配置
export interface KeyRotationConfig {
  enabled: boolean;
  rotationPeriodDays: number;
  notificationDaysBeforeRotation: number;
  autoRotateExpiredKeys: boolean;
  preserveOldKeysCount: number;
}

// 安全策略
export interface QuantumSecurityPolicy {
  minimumSecurityLevel: SecurityLevel;
  allowedAlgorithms: AlgorithmType[];
  requireHybridEncryption: boolean;
  requireTimeStamping: boolean;
  allowNISTOnly: boolean;
  maxKeyAgeDays: number;
  signatureValidityDays: number;
}
