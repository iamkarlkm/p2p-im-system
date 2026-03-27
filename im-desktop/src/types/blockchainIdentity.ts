/**
 * 区块链身份验证与去中心化身份 (DID) 类型定义
 * 支持以太坊、Polygon等区块链网络的身份管理
 */

// DID 文档类型
export enum DIDDocumentType {
  VERIFICATION_METHOD = 'VerificationMethod',
  SERVICE_ENDPOINT = 'ServiceEndpoint',
  AUTHENTICATION = 'Authentication',
  ASSERTION_METHOD = 'AssertionMethod',
  KEY_AGREEMENT = 'KeyAgreement',
  CAPABILITY_INVOCATION = 'CapabilityInvocation',
  CAPABILITY_DELEGATION = 'CapabilityDelegation'
}

// 区块链网络类型
export enum BlockchainNetwork {
  ETHEREUM_MAINNET = 'ethereum:1',
  ETHEREUM_SEPOLIA = 'ethereum:11155111',
  POLYGON_MAINNET = 'polygon:137',
  POLYGON_MUMBAI = 'polygon:80001',
  ARBITRUM_ONE = 'arbitrum:42161',
  OPTIMISM = 'optimism:10',
  BSC_MAINNET = 'bsc:56',
  AVALANCHE = 'avalanche:43114'
}

// 验证方法类型
export enum VerificationMethodType {
  ECDSA_SECP256K1 = 'EcdsaSecp256k1VerificationKey2019',
  ED25519 = 'Ed25519VerificationKey2020',
  JSON_WEB_KEY = 'JsonWebKey2020',
  ETH_ADDRESS = 'EthereumAddress',
  SOLANA_ADDRESS = 'SolanaAddress'
}

// DID 状态
export enum DIDStatus {
  ACTIVE = 'ACTIVE',
  DEACTIVATED = 'DEACTIVATED',
  SUSPENDED = 'SUSPENDED',
  REVOKED = 'REVOKED',
  EXPIRED = 'EXPIRED'
}

// 身份凭证类型
export enum CredentialType {
  VERIFIABLE_CREDENTIAL = 'VerifiableCredential',
  VERIFIABLE_PRESENTATION = 'VerifiablePresentation',
  KYC_CREDENTIAL = 'KYCCredential',
  AGE_CREDENTIAL = 'AgeVerificationCredential',
  MEMBERSHIP_CREDENTIAL = 'MembershipCredential',
  REPUTATION_CREDENTIAL = 'ReputationCredential'
}

// 凭证状态
export enum CredentialStatus {
  ISSUED = 'ISSUED',
  REVOKED = 'REVOKED',
  EXPIRED = 'EXPIRED',
  SUSPENDED = 'SUSPENDED',
  VERIFIED = 'VERIFIED'
}

// DID 验证方法
export interface VerificationMethod {
  id: string;
  type: VerificationMethodType;
  controller: string;
  publicKeyBase58?: string;
  publicKeyBase64?: string;
  publicKeyHex?: string;
  publicKeyJwk?: JsonWebKey;
  blockchainAddress?: string;
  ethereumAddress?: string;
}

// 服务端点
export interface ServiceEndpoint {
  id: string;
  type: string;
  serviceEndpoint: string | string[];
  description?: string;
}

// DID 文档
export interface DIDDocument {
  '@context': string[];
  id: string;
  created: string;
  updated: string;
  versionId?: string;
  deactivated?: boolean;
  verificationMethod: VerificationMethod[];
  authentication: (string | VerificationMethod)[];
  assertionMethod?: (string | VerificationMethod)[];
  keyAgreement?: (string | VerificationMethod)[];
  capabilityInvocation?: (string | VerificationMethod)[];
  capabilityDelegation?: (string | VerificationMethod)[];
  service?: ServiceEndpoint[];
  publicKey?: VerificationMethod[];
}

// 区块链身份
export interface BlockchainIdentity {
  id: string;
  did: string;
  userId: string;
  walletAddress: string;
  network: BlockchainNetwork;
  chainId: number;
  didDocument: DIDDocument;
  status: DIDStatus;
  isPrimary: boolean;
  credentials: VerifiableCredential[];
  reputationScore: number;
  trustLevel: TrustLevel;
  nonce: string;
  createdAt: number;
  updatedAt: number;
  lastVerifiedAt?: number;
}

// 信任等级
export enum TrustLevel {
  UNTRUSTED = 0,
  LOW = 1,
  MEDIUM = 2,
  HIGH = 3,
  VERIFIED = 4,
  ENTERPRISE = 5
}

// 可验证凭证
export interface VerifiableCredential {
  '@context': string[];
  id: string;
  type: string[];
  issuer: string | Issuer;
  issuanceDate: string;
  expirationDate?: string;
  credentialSubject: CredentialSubject;
  proof?: CredentialProof;
  credentialStatus?: CredentialStatusInfo;
  status: CredentialStatus;
}

// 凭证颁发者
export interface Issuer {
  id: string;
  name?: string;
  type?: string;
  image?: string;
  url?: string;
}

// 凭证主题
export interface CredentialSubject {
  id: string;
  [key: string]: any;
}

// 凭证证明
export interface CredentialProof {
  type: string;
  created: string;
  proofPurpose: string;
  verificationMethod: string;
  jws?: string;
  proofValue?: string;
  signatureValue?: string;
  nonce?: string;
}

// 凭证状态信息
export interface CredentialStatusInfo {
  id: string;
  type: string;
  statusListIndex?: number;
  statusListCredential?: string;
}

// 可验证陈述
export interface VerifiablePresentation {
  '@context': string[];
  type: string[];
  verifiableCredential: VerifiableCredential[];
  proof: CredentialProof;
  holder: string;
}

// 身份验证请求
export interface AuthenticationRequest {
  id: string;
  challenge: string;
  domain: string;
  origin: string;
  requestedCredentials?: string[];
  requestedNetworks?: BlockchainNetwork[];
  expirationTime: number;
}

// 身份验证响应
export interface AuthenticationResponse {
  id: string;
  requestId: string;
  did: string;
  presentation: VerifiablePresentation;
  signature: string;
  timestamp: number;
}

// 钱包连接信息
export interface WalletConnection {
  provider: string;
  address: string;
  chainId: number;
  network: BlockchainNetwork;
  isConnected: boolean;
  isConnecting: boolean;
  error?: string;
}

// 身份验证结果
export interface IdentityVerificationResult {
  success: boolean;
  identity?: BlockchainIdentity;
  error?: string;
  errorCode?: string;
  verifiedAt: number;
  verificationMethod: string;
}

// DID 解析结果
export interface DIDResolutionResult {
  didDocument: DIDDocument;
  didDocumentMetadata: {
    created: string;
    updated: string;
    versionId?: string;
    deactivated?: boolean;
    nextUpdate?: string;
    nextVersionId?: string;
  };
  didResolutionMetadata: {
    contentType: string;
    error?: string;
    retrieved: string;
  };
}

// 声誉评分
export interface ReputationScore {
  overall: number;
  breakdown: {
    transactionHistory: number;
    credentialCount: number;
    networkActivity: number;
    socialProof: number;
    longevity: number;
  };
  lastUpdated: number;
  sources: ReputationSource[];
}

// 声誉来源
export interface ReputationSource {
  name: string;
  score: number;
  weight: number;
  timestamp: number;
}

// 身份恢复信息
export interface IdentityRecovery {
  recoveryPhrase?: string[];
  recoveryContacts: RecoveryContact[];
  socialRecoveryEnabled: boolean;
  multiSigRequired: boolean;
  threshold: number;
}

// 恢复联系人
export interface RecoveryContact {
  id: string;
  name: string;
  did: string;
  email?: string;
  verified: boolean;
}

// 身份事件
export interface IdentityEvent {
  id: string;
  type: IdentityEventType;
  did: string;
  timestamp: number;
  data: any;
  transactionHash?: string;
  blockNumber?: number;
}

// 身份事件类型
export enum IdentityEventType {
  CREATED = 'CREATED',
  UPDATED = 'UPDATED',
  DEACTIVATED = 'DEACTIVATED',
  CREDENTIAL_ISSUED = 'CREDENTIAL_ISSUED',
  CREDENTIAL_REVOKED = 'CREDENTIAL_REVOKED',
  VERIFIED = 'VERIFIED',
  RECOVERY_INITIATED = 'RECOVERY_INITIATED',
  RECOVERY_COMPLETED = 'RECOVERY_COMPLETED',
  OWNERSHIP_TRANSFERRED = 'OWNERSHIP_TRANSFERRED'
}
