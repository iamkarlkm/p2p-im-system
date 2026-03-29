/**
 * 量子抗性加密类型定义
 * 支持后量子密码学系统
 */

// 算法类型枚举
export enum AlgorithmType {
  LATTICE_BASED = 'LATTICE_BASED',
  HASH_BASED = 'HASH_BASED',
  CODE_BASED = 'CODE_BASED',
  MULTIVARIATE = 'MULTIVARIATE',
  SIKE = 'SIKE',
  HYBRID = 'HYBRID'
}

// NIST标准算法名称
export enum NISTAlgorithmName {
  KYBER = 'CRYSTALS-Kyber',
  DILITHIUM = 'CRYSTALS-Dilithium',
  FALCON = 'FALCON',
  SPHINCS_PLUS = 'SPHINCS+',
  MCELIECE = 'Classic McEliece',
  RAINBOW = 'Rainbow'
}

// 支持的算法家族
export interface SupportedAlgorithms {
  [key: string]: string[];
}

// 量子抗性加密配置
export interface QuantumResistantEncryption {
  id: number;
  algorithmName: string;
  algorithmType: AlgorithmType;
  securityLevel: number;
  nistStandard: boolean;
  nistAlgorithmName?: NISTAlgorithmName;
  keySize: number;
  publicKey?: string;
  privateKey?: string;
  ciphertext?: string;
  signature?: string;
  keyPairGenerationTime?: string;
  encryptionTimeMs?: number;
  decryptionTimeMs?: number;
  signatureTimeMs?: number;
  verificationTimeMs?: number;
  memoryUsageKb?: number;
  cpuUsagePercent?: number;
  performanceScore?: number;
  supportedOperations?: string[];
  algorithmParameters?: Record<string, string>;
  hybridWithTraditional: boolean;
  traditionalAlgorithm?: string;
  quantumKeyDistribution: boolean;
  qkdProtocol?: string;
  qkdSuccessRate?: number;
  complianceCertification?: string;
  implementationLanguage?: string;
  libraryVersion?: string;
  optimizationLevel?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt?: string;
  createdBy?: string;
  description?: string;
}

// 后量子签名
export interface PostQuantumSignature {
  id: number;
  signatureId: string;
  algorithmName: string;
  algorithmFamily: string;
  securityLevel: number;
  nistStandard: boolean;
  nistAlgorithmName?: string;
  signatureSizeBytes: number;
  publicKeySizeBytes: number;
  privateKeySizeBytes: number;
  publicKey?: string;
  privateKey?: string;
  signatureData?: string;
  signedMessageHash?: string;
  messageToSign?: string;
  signatureGenerationTime?: string;
  signatureVerificationTime?: string;
  keyGenerationTimeMs?: number;
  signingTimeMs?: number;
  verificationTimeMs?: number;
  memoryUsageDuringSigningKb?: number;
  cpuUsageDuringSigningPercent?: number;
  verificationSuccess: boolean;
  signatureValidityPeriodDays?: number;
  expiresAt?: string;
  revocationStatus: boolean;
  revocationReason?: string;
  revokedAt?: string;
  keyUsageCount: number;
  maxKeyUsageLimit?: number;
  hashFunctionUsed?: string;
  treeHeight?: number;
  treeWidth?: number;
  stateful: boolean;
  stateInformation?: string;
  supportedFeatures?: string[];
  algorithmParameters?: Record<string, string>;
  performanceScore: number;
  securityScore: number;
  implementationLanguage?: string;
  libraryName?: string;
  libraryVersion?: string;
  optimizationLevel?: string;
  certificationStatus?: string;
  certificationBody?: string;
  certificationDate?: string;
  isActive: boolean;
  createdAt: string;
  updatedAt?: string;
  createdBy?: string;
  description?: string;
}

// 密钥对生成请求
export interface KeyPairGenerationRequest {
  algorithmType: AlgorithmType | string;
  algorithmName: string;
  securityLevel: number;
  parameters?: Record<string, string>;
}

// 加密请求
export interface EncryptionRequest {
  encryptionId: number;
  plaintext: string;
}

// 解密请求
export interface DecryptionRequest {
  encryptionId: number;
  ciphertext: string;
}

// 签名请求
export interface SignatureCreationRequest {
  algorithmName: string;
  message: string;
  securityLevel: number;
  parameters?: Record<string, string>;
}

// 签名验证请求
export interface SignatureVerificationRequest {
  signatureId: string;
  signatureData: string;
  message: string;
  publicKey: string;
  algorithmName: string;
}

// 吊销请求
export interface RevocationRequest {
  reason?: string;
}

// 加密响应
export interface EncryptionResponse {
  encryptionId: number;
  ciphertext: string;
  encryptionTimeMs: number;
  algorithm: string;
}

// 解密响应
export interface DecryptionResponse {
  encryptionId: number;
  plaintext: string;
  decryptionSuccess: boolean;
}

// 签名创建响应
export interface SignatureCreationResponse {
  signatureId: string;
  signatureData: string;
  signingTimeMs: number;
  verificationSuccess: boolean;
  performanceScore: number;
  securityScore: number;
}

// 签名验证响应
export interface SignatureVerificationResponse {
  signatureId: string;
  verificationResult: boolean;
  verificationTime: number;
}

// 性能指标
export interface PerformanceMetrics {
  algorithmName: string;
  averageTimeMs: number;
  minTimeMs: number;
  maxTimeMs: number;
  sampleCount: number;
  samples?: number[];
}

// 系统统计信息
export interface QuantumEncryptionStatistics {
  totalEncryptions: number;
  totalSignatures: number;
  latticeBasedAlgorithms: number;
  hashBasedAlgorithms: number;
  codeBasedAlgorithms: number;
  multivariateAlgorithms: number;
  sikeAlgorithms: number;
  hybridAlgorithms: number;
  nistStandardAlgorithms: number;
  averagePerformanceScore: string;
  highSecurityCount: number;
  quantumKeyDistributionEnabled: number;
  lastUpdated: number;
}

// 健康检查响应
export interface HealthCheckResponse {
  status: string;
  service: string;
  timestamp: number;
  supportedAlgorithmTypes: number;
  nistStandardAlgorithms: number;
  activeEncryptions?: number;
  validSignatures?: number;
  details: string;
}

// 算法配置
export interface AlgorithmConfiguration {
  name: string;
  type: AlgorithmType;
  securityLevels: number[];
  keySizes: number[];
  features: string[];
  performanceRating: number; // 1-5
  securityRating: number; // 1-5
  recommendedUseCases: string[];
}

// 量子密钥分发配置
export interface QKDConfiguration {
  protocol: string;
  successRate: number;
  maxDistance: number; // km
  keyGenerationRate: number; // bits per second
  errorCorrectionEnabled: boolean;
  privacyAmplificationEnabled: boolean;
}

// 混合加密配置
export interface HybridEncryptionConfig {
  postQuantumAlgorithm: string;
  traditionalAlgorithm: string;
  combinedSecurityLevel: number;
  compatibilityMode: boolean;
  fallbackToTraditional: boolean;
}

// 算法比较结果
export interface AlgorithmComparison {
  algorithms: string[];
  encryptionTime: Record<string, number>;
  decryptionTime: Record<string, number>;
  keyGenerationTime: Record<string, number>;
  signatureSize: Record<string, number>;
  publicKeySize: Record<string, number>;
  memoryUsage: Record<string, number>;
  securityLevel: Record<string, number>;
  recommendation: string;
}

// API错误响应
export interface QuantumEncryptionError {
  error: string;
  message: string;
  timestamp: number;
}

// 操作结果
export interface OperationResult<T> {
  success: boolean;
  data?: T;
  error?: string;
  message?: string;
  timestamp: number;
}

// 过滤器
export interface EncryptionFilter {
  algorithmType?: AlgorithmType;
  nistStandard?: boolean;
  isActive?: boolean;
  minSecurityLevel?: number;
  maxSecurityLevel?: number;
}

// 签名过滤器
export interface SignatureFilter {
  algorithmFamily?: string;
  nistStandard?: boolean;
  isActive?: boolean;
  revocationStatus?: boolean;
  validOnly?: boolean;
}

// 排序选项
export interface SortOptions {
  field: string;
  direction: 'ASC' | 'DESC';
}

// 分页请求
export interface PagedRequest {
  page: number;
  size: number;
  sort?: SortOptions;
  filters?: EncryptionFilter;
}

// 分页响应
export interface PagedResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

// 导出到传统加密类型
export type EncryptionAlgorithm = QuantumResistantEncryption | PostQuantumSignature;

// 密钥操作类型
export type KeyOperation = 'GENERATE' | 'EXPORT' | 'IMPORT' | 'DELETE' | 'ROTATE';

// 安全级别类型
export type SecurityLevel = 128 | 192 | 256;

// 性能评级类型
export type PerformanceRating = 'excellent' | 'good' | 'average' | 'poor';

// 合规认证类型
export type ComplianceCertification = 'NIST' | 'BSI' | 'ANSSI' | 'ISO' | 'GDPR' | 'FIPS';

// 优化级别类型
export type OptimizationLevel = 'LOW' | 'MEDIUM' | 'HIGH';

// 算法参数类型
export type AlgorithmParameterValue = string | number | boolean;

// 完整的算法参数映射
export interface AlgorithmParameters {
  [key: string]: AlgorithmParameterValue;
}

// 算法元数据
export interface AlgorithmMetadata {
  name: string;
  type: AlgorithmType;
  description: string;
  inventor: string;
  yearIntroduced: number;
  securityProofs: string[];
  knownAttacks: string[];
  implementationNotes: string;
  references: string[];
}

// 部署配置
export interface DeploymentConfiguration {
  environment: 'development' | 'staging' | 'production';
  autoKeyRotation: boolean;
  rotationIntervalDays: number;
  backupEnabled: boolean;
  auditLoggingEnabled: boolean;
  complianceMode: ComplianceCertification;
  performanceMonitoringEnabled: boolean;
}

// 审计日志条目
export interface AuditLogEntry {
  id: number;
  operation: string;
  algorithm: string;
  securityLevel: number;
  userId?: string;
  timestamp: string;
  success: boolean;
  details?: string;
  ipAddress?: string;
}