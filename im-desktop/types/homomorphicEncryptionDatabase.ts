/**
 * 同态加密数据库 TypeScript 类型定义
 * 支持全同态加密（BGV、BFV、CKKS）、加密 SQL 查询引擎、隐私保护查询
 */

// 加密方案枚举
export enum EncryptionScheme {
  BGV = 'BGV',
  BFV = 'BFV',
  CKKS = 'CKKS',
  PAILLIER = 'PAILLIER',
  ELGAMAL = 'ELGAMAL',
  RSA_HOMOMORPHIC = 'RSA_HOMOMORPHIC'
}

// 安全级别枚举
export enum SecurityLevel {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  VERY_HIGH = 'VERY_HIGH',
  MILITARY = 'MILITARY'
}

// 数据库类型枚举
export enum DatabaseType {
  MESSAGE = 'MESSAGE',
  USER_PROFILE = 'USER_PROFILE',
  GROUP = 'GROUP',
  FILE_METADATA = 'FILE_METADATA',
  AUDIT_LOG = 'AUDIT_LOG'
}

// 查询类型枚举
export enum QueryType {
  SELECT = 'SELECT',
  INSERT = 'INSERT',
  UPDATE = 'UPDATE',
  DELETE = 'DELETE',
  AGGREGATE = 'AGGREGATE',
  JOIN = 'JOIN',
  RANGE = 'RANGE',
  FUZZY = 'FUZZY',
  SPATIAL = 'SPATIAL',
  TEMPORAL = 'TEMPORAL'
}

// 查询复杂度枚举
export enum QueryComplexity {
  SIMPLE = 'SIMPLE',
  MODERATE = 'MODERATE',
  COMPLEX = 'COMPLEX',
  VERY_COMPLEX = 'VERY_COMPLEX',
  EXTREME = 'EXTREME'
}

// 隐私级别枚举
export enum PrivacyLevel {
  PUBLIC = 'PUBLIC',
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  VERY_HIGH = 'VERY_HIGH',
  CONFIDENTIAL = 'CONFIDENTIAL',
  SECRET = 'SECRET'
}

// 加密方法枚举
export enum EncryptionMethod {
  HOMOMORPHIC = 'HOMOMORPHIC',
  ORDER_PRESERVING = 'ORDER_PRESERVING',
  DETERMINISTIC = 'DETERMINISTIC',
  SEARCHABLE = 'SEARCHABLE',
  RANDOMIZED = 'RANDOMIZED'
}

// 查询状态枚举
export enum QueryStatus {
  PENDING = 'PENDING',
  EXECUTING = 'EXECUTING',
  ENCRYPTING = 'ENCRYPTING',
  DECRYPTING = 'DECRYPTING',
  COMPRESSING = 'COMPRESSING',
  SUCCESS = 'SUCCESS',
  FAILED = 'FAILED',
  TIMEOUT = 'TIMEOUT',
  CANCELLED = 'CANCELLED',
  PARTIAL_SUCCESS = 'PARTIAL_SUCCESS'
}

// 数据库状态枚举
export enum DatabaseStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  MAINTENANCE = 'MAINTENANCE',
  ARCHIVED = 'ARCHIVED',
  DELETED = 'DELETED'
}

// 同态加密数据库接口
export interface HomomorphicEncryptionDatabase {
  databaseId: number;
  userId: number;
  sessionId?: string;
  databaseName: string;
  databaseType: DatabaseType;
  encryptionScheme: EncryptionScheme;
  securityLevel: SecurityLevel;
  keySize: number;
  modulusSize: number;
  plaintextModulus: number;
  noiseBudget: number;
  publicKeyHash: string;
  secretKeyHash: string;
  encryptedDataCount: number;
  totalDataSizeBytes: number;
  encryptionTimeAvgMs: number;
  decryptionTimeAvgMs: number;
  homomorphicOpTimeAvgMs: number;
  compressionEnabled: boolean;
  compressionAlgorithm?: string;
  compressionRatio: number;
  indexingEnabled: boolean;
  indexType?: string;
  indexCount: number;
  queryCacheEnabled: boolean;
  cacheHitRate: number;
  parallelismEnabled: boolean;
  maxParallelThreads: number;
  hardwareAcceleration: boolean;
  acceleratorType?: string;
  privacyBudget: number;
  privacyBudgetConsumed: number;
  differentialPrivacyEnabled: boolean;
  dpEpsilon: number;
  dpDelta: number;
  maxQueryComplexity: number;
  status: DatabaseStatus;
  createdTime: string;
  updatedTime: string;
  lastAccessedTime?: string;
  healthScore: number;
  securityScore: number;
  privacyScore: number;
  costEstimateUsdPerMonth: number;
}

// 隐私保护查询接口
export interface PrivacyPreservingQuery {
  queryId: number;
  databaseId: number;
  userId: number;
  sessionId?: string;
  queryUuid: string;
  queryType: QueryType;
  queryComplexity: QueryComplexity;
  querySql: string;
  encryptedQuerySql?: string;
  queryParameters?: string;
  encryptedQueryParameters?: string;
  privacyLevel: PrivacyLevel;
  encryptionMethod: EncryptionMethod;
  encryptionKeyHash?: string;
  resultEncryptionEnabled: boolean;
  resultEncryptionMethod?: string;
  privacyBudgetConsumed: number;
  differentialPrivacyEnabled: boolean;
  dpEpsilonConsumed: number;
  dpDeltaConsumed: number;
  noiseAddedAmount: number;
  noiseDistribution?: string;
  queryOptimizationEnabled: boolean;
  optimizationStrategy?: string;
  parallelExecutionEnabled: boolean;
  parallelDegree: number;
  cacheEnabled: boolean;
  cacheHit: boolean;
  cacheTtlSeconds: number;
  auditTrailEnabled: boolean;
  accessControlEnforced: boolean;
  complianceCheckEnabled: boolean;
  complianceStatus?: string;
  resultRowCount: number;
  resultDataSizeBytes: number;
  encryptedResultDataSizeBytes: number;
  compressionEnabled: boolean;
  compressionRatio: number;
  compressionAlgorithm?: string;
  queryExecutionTimeMs: number;
  encryptionTimeMs: number;
  decryptionTimeMs: number;
  networkTransferTimeMs: number;
  totalLatencyMs: number;
  cpuUsagePercent: number;
  memoryUsageBytes: number;
  diskIoBytes: number;
  networkIoBytes: number;
  costEstimateUsd: number;
  errorOccurred: boolean;
  errorMessage?: string;
  errorStackTrace?: string;
  retryCount: number;
  maxRetries: number;
  queryStatus: QueryStatus;
  resultVerificationEnabled: boolean;
  verificationResult: boolean;
  verificationMethod?: string;
  verificationProof?: string;
  verificationTimeMs: number;
  createdTime: string;
  startTime?: string;
  endTime?: string;
  lastUpdatedTime: string;
  expirationTime?: string;
  performanceScore: number;
  privacyScore: number;
  accuracyScore: number;
  complianceScore: number;
  userSatisfactionScore: number;
}

// 创建数据库请求接口
export interface CreateDatabaseRequest {
  userId: number;
  sessionId?: string;
  databaseName: string;
  databaseType?: DatabaseType;
  encryptionScheme?: EncryptionScheme;
  securityLevel?: SecurityLevel;
  keySize?: number;
  modulusSize?: number;
  plaintextModulus?: number;
  noiseBudget?: number;
  compressionEnabled?: boolean;
  compressionAlgorithm?: string;
  indexingEnabled?: boolean;
  indexType?: string;
  queryCacheEnabled?: boolean;
  parallelismEnabled?: boolean;
  maxParallelThreads?: number;
  hardwareAcceleration?: boolean;
  privacyBudget?: number;
  differentialPrivacyEnabled?: boolean;
  dpEpsilon?: number;
  dpDelta?: number;
  dataRetentionDays?: number;
  auditLoggingEnabled?: boolean;
}

// 创建查询请求接口
export interface CreateQueryRequest {
  databaseId: number;
  userId: number;
  sessionId?: string;
  queryType?: QueryType;
  querySql: string;
  privacyLevel?: PrivacyLevel;
  encryptionMethod?: EncryptionMethod;
  queryParameters?: string;
  queryFilters?: string;
  projectionFields?: string[];
  sortFields?: string[];
  limit?: number;
  offset?: number;
  differentialPrivacyEnabled?: boolean;
  resultEncryptionEnabled?: boolean;
  queryOptimizationEnabled?: boolean;
  parallelExecutionEnabled?: boolean;
  parallelDegree?: number;
  cacheEnabled?: boolean;
  resultVerificationEnabled?: boolean;
  verificationMethod?: string;
  auditTrailEnabled?: boolean;
  accessControlEnforced?: boolean;
  complianceCheckEnabled?: boolean;
}

// 数据库统计信息接口
export interface DatabaseStatistics {
  databaseId: number;
  databaseName: string;
  encryptedDataCount: number;
  totalDataSizeBytes: number;
  encryptionTimeAvgMs: number;
  decryptionTimeAvgMs: number;
  homomorphicOpTimeAvgMs: number;
  compressionRatio: number;
  cacheHitRate: number;
  healthScore: number;
  securityScore: number;
  privacyScore: number;
  privacyBudget: number;
  privacyBudgetConsumed: number;
  status: DatabaseStatus;
}

// 查询统计信息接口
export interface QueryStatistics {
  totalQueries: number;
  successfulQueries: number;
  failedQueries: number;
  pendingQueries: number;
  avgExecutionTimeMs: number;
  avgPrivacyScore: number;
  avgAccuracyScore: number;
  totalPrivacyBudgetConsumed: number;
}

// API 响应接口
export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data?: T;
  timestamp?: string;
  error?: string;
  queryUuid?: string;
  queryStatus?: string;
  resultRowCount?: number;
  resultDataSizeBytes?: number;
  executionTimeMs?: number;
  count?: number;
}

// 数据库列表响应接口
export interface DatabaseListResponse {
  success: boolean;
  count: number;
  data: HomomorphicEncryptionDatabase[];
}

// 查询列表响应接口
export interface QueryListResponse {
  success: boolean;
  count: number;
  data: PrivacyPreservingQuery[];
}