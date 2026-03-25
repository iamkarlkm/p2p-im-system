export enum ComputationType {
  FEDERATED_LEARNING = 'FEDERATED_LEARNING',
  PRIVACY_AUTH = 'PRIVACY_AUTH',
  MESSAGE_INTEGRITY = 'MESSAGE_INTEGRITY',
  SELECTIVE_CREDENTIAL = 'SELECTIVE_CREDENTIAL',
  HOMOMORPHIC_VERIFY = 'HOMOMORPHIC_VERIFY',
  MPC_PROOF = 'MPC_PROOF',
  PSI_PROOF = 'PSI_PROOF',
  VERIFIABLE_RANDOM = 'VERIFIABLE_RANDOM'
}

export enum CircuitType {
  GROTH16 = 'GROTH16',
  PLONK = 'PLONK',
  MARLIN = 'MARLIN',
  SONIC = 'SONIC',
  AURORA = 'AURORA',
  FRACTAL = 'FRACTAL'
}

export enum ComputationStatus {
  PENDING = 'PENDING',
  GENERATING_PROOF = 'GENERATING_PROOF',
  PROOF_GENERATED = 'PROOF_GENERATED',
  VERIFYING_PROOF = 'VERIFYING_PROOF',
  VERIFIED = 'VERIFIED',
  FAILED = 'FAILED',
  CANCELLED = 'CANCELLED'
}

export enum ProtectionType {
  IDENTITY_AUTH = 'IDENTITY_AUTH',
  SELECTIVE_DISCLOSURE = 'SELECTIVE_DISCLOSURE',
  ANONYMOUS_CREDENTIAL = 'ANONYMOUS_CREDENTIAL',
  RANGE_PROOF = 'RANGE_PROOF',
  MEMBERSHIP_PROOF = 'MEMBERSHIP_PROOF',
  NON_MEMBERSHIP_PROOF = 'NON_MEMBERSHIP_PROOF',
  BULK_VERIFICATION = 'BULK_VERIFICATION',
  CROSS_CHAIN_PROOF = 'CROSS_CHAIN_PROOF'
}

export enum ProtectionStatus {
  ACTIVE = 'ACTIVE',
  REVOKED = 'REVOKED',
  EXPIRED = 'EXPIRED',
  SUSPENDED = 'SUSPENDED',
  PENDING_VERIFICATION = 'PENDING_VERIFICATION'
}

export interface ZKPComputation {
  id: string;
  computationId: string;
  userId: string;
  sessionId?: string;
  computationType: ComputationType;
  circuitType: CircuitType;
  circuitSize?: number;
  publicInputs?: Record<string, any>;
  privateInputsHash?: string;
  proofGenerated: boolean;
  proofVerified: boolean;
  proofData?: string;
  verificationKeyHash?: string;
  proofSizeBytes?: number;
  generationTimeMs?: number;
  verificationTimeMs?: number;
  securityLevel: number;
  computationStatus: ComputationStatus;
  errorMessage?: string;
  retryCount: number;
  createdAt: string;
  updatedAt: string;
  proofGeneratedAt?: string;
  proofVerifiedAt?: string;
}

export interface ZKPPrivacyProtection {
  id: string;
  protectionId: string;
  userId: string;
  protectionType: ProtectionType;
  credentialAttributes?: Record<string, any>;
  disclosedAttributes?: string[];
  predicates?: Array<{
    attribute: string;
    operator: string;
    value: any;
  }>;
  predicateSatisfied?: boolean;
  verificationResult: boolean;
  verificationScore?: number;
  privacyPreservationScore?: number;
  protectionStatus: ProtectionStatus;
  validFrom: string;
  validTo?: string;
  createdAt: string;
  updatedAt: string;
}

export interface CreateComputationRequest {
  userId: string;
  sessionId?: string;
  computationType: ComputationType;
  circuitType?: CircuitType;
  securityLevel?: number;
}

export interface GenerateProofRequest {
  publicInputs: Record<string, any>;
  privateInputs: Record<string, any>;
}

export interface VerifyPrivacyProtectionRequest {
  disclosedAttributes: Record<string, any>;
  predicates?: Array<{
    attribute: string;
    operator: string;
    value: any;
  }>;
}

export interface CreatePrivacyProtectionRequest {
  userId: string;
  protectionType: ProtectionType;
  attributes: Record<string, any>;
  validTo?: string;
}

export interface ZKPStatistics {
  totalComputations: number;
  totalProofsGenerated: number;
  totalProofsVerified: number;
  averageGenerationTimeMs: number;
  averageVerificationTimeMs: number;
  successRate: number;
}

export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
}
