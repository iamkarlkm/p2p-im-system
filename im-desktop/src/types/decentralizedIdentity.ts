/**
 * 去中心化身份系统 TypeScript 类型定义
 * 
 * 基于区块链和W3C DID标准的身份验证系统前端接口
 * 
 * @version 1.0.0
 * @since 2026-03-24
 */

/**
 * 区块链类型枚举
 */
export enum BlockchainType {
  ETHEREUM = 'ETHEREUM',
  POLYGON = 'POLYGON', 
  SOLANA = 'SOLANA',
  BINANCE_SMART_CHAIN = 'BINANCE_SMART_CHAIN',
  ARBITRUM = 'ARBITRUM',
  OPTIMISM = 'OPTIMISM',
  BASE = 'BASE',
  AVALANCHE = 'AVALANCHE',
  NEAR = 'NEAR',
  COSMOS = 'COSMOS',
  CELESTIA = 'CELESTIA',
  APTOS = 'APTOS',
  SUI = 'SUI',
  CARDANO = 'CARDANO',
  TEZOS = 'TEZOS',
  ALGORAND = 'ALGORAND',
  HEDERA = 'HEDERA',
  STARKNET = 'STARKNET',
  ZKSYNC = 'ZKSYNC'
}

/**
 * 身份状态枚举
 */
export enum IdentityStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  SUSPENDED = 'SUSPENDED',
  PENDING_VERIFICATION = 'PENDING_VERIFICATION',
  REVOKED = 'REVOKED'
}

/**
 * 凭证类型枚举
 */
export enum CredentialType {
  IDENTITY_VERIFICATION = 'IDENTITY_VERIFICATION',
  KYC_COMPLIANCE = 'KYC_COMPLIANCE',
  AGE_VERIFICATION = 'AGE_VERIFICATION',
  RESIDENCE_VERIFICATION = 'RESIDENCE_VERIFICATION',
  EDUCATION_CREDENTIAL = 'EDUCATION_CREDENTIAL',
  PROFESSIONAL_LICENSE = 'PROFESSIONAL_LICENSE',
  MEMBERSHIP_CERTIFICATE = 'MEMBERSHIP_CERTIFICATE',
  SKILL_CERTIFICATION = 'SKILL_CERTIFICATION',
  REPUTATION_SCORE = 'REPUTATION_SCORE',
  TRANSACTION_HISTORY = 'TRANSACTION_HISTORY',
  GOVERNMENT_ID = 'GOVERNMENT_ID',
  PASSPORT = 'PASSPORT',
  DRIVERS_LICENSE = 'DRIVERS_LICENSE'
}

/**
 * 凭证状态枚举
 */
export enum CredentialStatus {
  VALID = 'VALID',
  EXPIRED = 'EXPIRED',
  REVOKED = 'REVOKED',
  SUSPENDED = 'SUSPENDED',
  PENDING_ISSUANCE = 'PENDING_ISSUANCE'
}

/**
 * 零知识证明类型枚举
 */
export enum ZKPProofType {
  IDENTITY_REVEAL = 'IDENTITY_REVEAL',
  AGE_RANGE = 'AGE_RANGE',
  COUNTRY_RESIDENCE = 'COUNTRY_RESIDENCE',
  BALANCE_RANGE = 'BALANCE_RANGE',
  TRANSACTION_HISTORY = 'TRANSACTION_HISTORY',
  REPUTATION_SCORE = 'REPUTATION_SCORE',
  MEMBERSHIP_PROOF = 'MEMBERSHIP_PROOF',
  CREDENTIAL_VERIFICATION = 'CREDENTIAL_VERIFICATION',
  ATTRIBUTE_NON_DISCLOSURE = 'ATTRIBUTE_NON_DISCLOSURE'
}

/**
 * 隐私级别枚举
 */
export enum PrivacyLevel {
  PUBLIC = 'PUBLIC',
  PRIVATE = 'PRIVATE',
  CONFIDENTIAL = 'CONFIDENTIAL',
  SECRET = 'SECRET',
  TOP_SECRET = 'TOP_SECRET'
}

/**
 * 跨链同步状态枚举
 */
export enum CrossChainSyncStatus {
  SYNCED = 'SYNCED',
  SYNCING = 'SYNCING',
  FAILED = 'FAILED',
  PENDING = 'PENDING',
  NOT_SUPPORTED = 'NOT_SUPPORTED'
}

/**
 * 去中心化身份实体接口
 */
export interface DecentralizedIdentity {
  identityId: string;
  userId: string;
  identityAddress: string;
  blockchainType: BlockchainType;
  publicKey: string;
  status: IdentityStatus;
  didDocument?: DIDDocument;
  metadata?: Record<string, any>;
  reputationScore: number;
  reputationRank: number;
  verificationCount: number;
  lastVerificationTime?: number;
  createdTime: number;
  updatedTime: number;
  syncedChains: BlockchainType[];
  attributes?: Record<string, any>;
}

/**
 * W3C DID 文档接口
 */
export interface DIDDocument {
  '@context': string[];
  id: string;
  controller: string[];
  verificationMethod: VerificationMethod[];
  authentication: string[];
  assertionMethod: string[];
  keyAgreement: string[];
  capabilityInvocation: string[];
  capabilityDelegation: string[];
  service?: ServiceEndpoint[];
  created?: string;
  updated?: string;
  proof?: LinkedDataProof;
}

/**
 * 验证方法接口
 */
export interface VerificationMethod {
  id: string;
  type: string;
  controller: string;
  publicKeyMultibase?: string;
  publicKeyJwk?: Record<string, any>;
  blockchainAccountId?: string;
}

/**
 * 服务端点接口
 */
export interface ServiceEndpoint {
  id: string;
  type: string;
  serviceEndpoint: string;
  description?: string;
}

/**
 * 链接数据证明接口
 */
export interface LinkedDataProof {
  type: string;
  created: string;
  verificationMethod: string;
  proofPurpose: string;
  jws?: string;
  proofValue?: string;
}

/**
 * 可验证凭证实体接口
 */
export interface VerifiableCredential {
  credentialId: string;
  issuerId: string;
  subjectId: string;
  credentialType: CredentialType;
  credentialSchema: {
    id: string;
    type: string;
  };
  credentialSubject: Record<string, any>;
  issuanceDate: number;
  expirationDate?: number;
  proof: {
    type: string;
    created: number;
    verificationMethod: string;
    proofPurpose: string;
    jws?: string;
    proofValue?: string;
  };
  status: CredentialStatus;
  revocationRegistryId?: string;
  credentialRevocationId?: string;
  metadata?: Record<string, any>;
  createdTime: number;
  updatedTime: number;
}

/**
 * 零知识证明结果接口
 */
export interface ZKPProofResult {
  proofId: string;
  proofData: string;
  verificationKey: string;
  generationTimeMs: number;
  proofSizeBytes: number;
  proofType: ZKPProofType;
  privacyLevel: PrivacyLevel;
  circuitType?: string;
  proofHash?: string;
}

/**
 * 声誉评分接口
 */
export interface ReputationScore {
  identityId: string;
  score: number;
  rank: number;
  lastUpdateTime: number;
  positiveFactors: string[];
  negativeFactors: string[];
  historicalScores?: number[];
  trend?: 'INCREASING' | 'DECREASING' | 'STABLE';
}

/**
 * 跨链同步结果接口
 */
export interface CrossChainSyncResult {
  identityId: string;
  chainSyncStatus: Record<BlockchainType, SyncStatus>;
  failedChains: BlockchainType[];
  syncTime: number;
}

/**
 * 同步状态接口
 */
export interface SyncStatus {
  chain: BlockchainType;
  synced: boolean;
  transactionHash?: string;
  blockNumber?: number;
  timestamp?: number;
  gasUsed?: number;
}

/**
 * 系统统计信息接口
 */
export interface DIDSystemStatistics {
  totalIdentities: number;
  activeIdentities: number;
  identitiesByChain: Record<BlockchainType, number>;
  totalCredentials: number;
  validCredentials: number;
  totalZKProofs: number;
  averageReputationScore: number;
  totalCrossChainSyncs: number;
  performanceMetrics: {
    averageVerificationTimeMs: number;
    averageProofGenerationTimeMs: number;
    successRate: number;
    errorRate: number;
  };
}

// API 请求/响应接口

export interface RegisterIdentityRequest {
  userId: string;
  identityAddress: string;
  blockchainType: BlockchainType;
  publicKey: string;
  metadata?: Record<string, any>;
}

export interface SignatureVerificationRequest {
  message: string;
  signature: string;
}

export interface VerificationResult {
  identityId: string;
  isValid: boolean;
  verificationTime: number;
  verificationMethod: string;
}

export interface CreateCredentialRequest {
  issuerId: string;
  subjectId: string;
  credentialType: CredentialType;
  attributes: Record<string, any>;
  expiryTime?: number;
  revocable: boolean;
  metadata?: Record<string, any>;
}

export interface CredentialVerificationResult {
  credentialId: string;
  isValid: boolean;
  verificationTime: number;
  revoked?: boolean;
  expiryTime?: number;
}

export interface GenerateZKPRequest {
  userId: string;
  proofType: ZKPProofType;
  witnessData: Record<string, any>;
  revealedAttributes: string[];
  privacyLevel: PrivacyLevel;
}

export interface VerifyZKPRequest {
  proofData: string;
  verificationKey: string;
  statement: string;
}

export interface ZKPVerificationResult {
  isValid: boolean;
  verificationTimeMs: number;
  errorMessage?: string;
}

export interface UpdateReputationRequest {
  scoreDelta: number;
  reason: string;
  evidence: string;
}

export interface CrossChainSyncRequest {
  targetChains: BlockchainType[];
  forceSync: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: {
    code: string;
    message: string;
    details?: Record<string, any>;
  };
  timestamp: number;
}

export interface PaginatedResponse<T> {
  items: T[];
  total: number;
  page: number;
  pageSize: number;
  hasNext: boolean;
  hasPrev: boolean;
}