import { EventEmitter } from 'events';
import { ipcRenderer } from 'electron';

/**
 * DID身份验证服务
 * 管理去中心化身份标识的创建、验证和管理
 */

export enum DIDMethod {
  ETHR = 'ethr',
  WEB = 'web',
  KEY = 'key',
  SOV = 'sov',
  ELEM = 'elem',
  JOLIET = 'joliet'
}

export enum VerificationStatus {
  UNVERIFIED = 'unverified',
  PENDING = 'pending',
  VERIFIED = 'verified',
  EXPIRED = 'expired',
  REVOKED = 'revoked',
  FAILED = 'failed'
}

export enum IdentityType {
  PERSONAL = 'personal',
  ENTERPRISE = 'enterprise',
  DEVICE = 'device',
  SERVICE = 'service',
  ANONYMOUS = 'anonymous'
}

export interface DIDDocument {
  '@context': string | string[];
  id: string;
  verificationMethod: VerificationMethod[];
  authentication: string[];
  assertionMethod: string[];
  keyAgreement: string[];
  capabilityInvocation: string[];
  capabilityDelegation: string[];
  service: ServiceEndpoint[];
  created: string;
  updated: string;
}

export interface VerificationMethod {
  id: string;
  type: string;
  controller: string;
  publicKeyBase58?: string;
  publicKeyHex?: string;
  publicKeyMultibase?: string;
  blockchainAccountId?: string;
}

export interface ServiceEndpoint {
  id: string;
  type: string;
  serviceEndpoint: string;
}

export interface DIDIdentity {
  did: string;
  method: DIDMethod;
  type: IdentityType;
  document: DIDDocument;
  metadata: IdentityMetadata;
  credentials: VerifiableCredential[];
  status: VerificationStatus;
  createdAt: number;
  updatedAt: number;
}

export interface IdentityMetadata {
  name?: string;
  description?: string;
  avatar?: string;
  email?: string;
  phone?: string;
  organization?: string;
  location?: string;
  website?: string;
  socialLinks?: Record<string, string>;
  customFields?: Record<string, any>;
}

export interface VerifiableCredential {
  '@context': string[];
  id: string;
  type: string[];
  issuer: string;
  issuanceDate: string;
  expirationDate?: string;
  credentialSubject: CredentialSubject;
  proof: CredentialProof;
  status: VerificationStatus;
}

export interface CredentialSubject {
  id: string;
  [key: string]: any;
}

export interface CredentialProof {
  type: string;
  created: string;
  proofPurpose: string;
  verificationMethod: string;
  jws?: string;
  proofValue?: string;
}

export interface AuthChallenge {
  challengeId: string;
  domain: string;
  nonce: string;
  issuedAt: number;
  expiresAt: number;
  purpose: string;
}

export interface AuthResponse {
  challengeId: string;
  did: string;
  signature: string;
  timestamp: number;
}

export interface BlockchainConfig {
  networkId: string;
  rpcUrl: string;
  registryAddress?: string;
  resolverAddress?: string;
  chainId: number;
}

export interface DIDAuthConfig {
  defaultMethod: DIDMethod;
  resolverUrl: string;
  blockchainConfigs: Map<string, BlockchainConfig>;
  credentialTimeout: number;
  challengeExpiry: number;
  autoResolve: boolean;
  cacheEnabled: boolean;
  cacheTTL: number;
}

export interface AuthSession {
  sessionId: string;
  did: string;
  identity: DIDIdentity;
  authenticatedAt: number;
  expiresAt: number;
  permissions: string[];
  metadata: Record<string, any>;
}

export class DIDAuthService extends EventEmitter {
  private static instance: DIDAuthService;
  private identities: Map<string, DIDIdentity> = new Map();
  private sessions: Map<string, AuthSession> = new Map();
  private activeSession: AuthSession | null = null;
  private config: DIDAuthConfig;
  private documentCache: Map<string, { document: DIDDocument; timestamp: number }> = new Map();
  private challenges: Map<string, AuthChallenge> = new Map();

  private constructor() {
    super();
    this.config = this.getDefaultConfig();
    this.loadStoredIdentities();
    this.setupIpcListeners();
  }

  static getInstance(): DIDAuthService {
    if (!DIDAuthService.instance) {
      DIDAuthService.instance = new DIDAuthService();
    }
    return DIDAuthService.instance;
  }

  private getDefaultConfig(): DIDAuthConfig {
    return {
      defaultMethod: DIDMethod.ETHR,
      resolverUrl: 'https://resolver.identity.foundation',
      blockchainConfigs: new Map([
        ['mainnet', {
          networkId: 'mainnet',
          rpcUrl: 'https://mainnet.infura.io/v3/demo',
          registryAddress: '0xdca7ef03e98e0dc2b855be647c39abe984fcf21b',
          resolverAddress: '0x907b384772cd8acd5bf11a738855ee80b78f755e',
          chainId: 1
        }],
        ['sepolia', {
          networkId: 'sepolia',
          rpcUrl: 'https://sepolia.infura.io/v3/demo',
          registryAddress: '0xdca7ef03e98e0dc2b855be647c39abe984fcf21b',
          resolverAddress: '0x907b384772cd8acd5bf11a738855ee80b78f755e',
          chainId: 11155111
        }]
      ]),
      credentialTimeout: 300000,
      challengeExpiry: 300000,
      autoResolve: true,
      cacheEnabled: true,
      cacheTTL: 3600000
    };
  }

  private setupIpcListeners(): void {
    ipcRenderer.on('did:document-resolved', (_, data) => {
      this.handleDocumentResolved(data);
    });

    ipcRenderer.on('did:credential-verified', (_, data) => {
      this.handleCredentialVerified(data);
    });

    ipcRenderer.on('did:challenge-received', (_, data) => {
      this.handleChallengeReceived(data);
    });

    ipcRenderer.on('did:auth-completed', (_, data) => {
      this.handleAuthCompleted(data);
    });
  }

  // ==================== 身份管理 ====================

  async createIdentity(
    method: DIDMethod = this.config.defaultMethod,
    type: IdentityType = IdentityType.PERSONAL,
    metadata?: Partial<IdentityMetadata>
  ): Promise<DIDIdentity> {
    try {
      const did = await this.generateDID(method);
      const document = await this.createDIDDocument(did, method);
      
      const identity: DIDIdentity = {
        did,
        method,
        type,
        document,
        metadata: metadata || {},
        credentials: [],
        status: VerificationStatus.UNVERIFIED,
        createdAt: Date.now(),
        updatedAt: Date.now()
      };

      this.identities.set(did, identity);
      await this.storeIdentity(identity);
      
      this.emit('identity:created', identity);
      return identity;
    } catch (error) {
      this.emit('error', { type: 'identity_creation_failed', error });
      throw error;
    }
  }

  async importIdentity(document: DIDDocument, privateKey?: string): Promise<DIDIdentity> {
    try {
      const existing = this.identities.get(document.id);
      if (existing) {
        throw new Error('Identity already exists');
      }

      const method = this.detectDIDMethod(document.id);
      const identity: DIDIdentity = {
        did: document.id,
        method,
        type: IdentityType.PERSONAL,
        document,
        metadata: {},
        credentials: [],
        status: VerificationStatus.VERIFIED,
        createdAt: Date.now(),
        updatedAt: Date.now()
      };

      this.identities.set(document.id, identity);
      await this.storeIdentity(identity);
      
      this.emit('identity:imported', identity);
      return identity;
    } catch (error) {
      this.emit('error', { type: 'identity_import_failed', error });
      throw error;
    }
  }

  async updateIdentity(did: string, updates: Partial<IdentityMetadata>): Promise<DIDIdentity> {
    const identity = this.identities.get(did);
    if (!identity) {
      throw new Error('Identity not found');
    }

    identity.metadata = { ...identity.metadata, ...updates };
    identity.updatedAt = Date.now();
    
    await this.storeIdentity(identity);
    this.emit('identity:updated', identity);
    
    return identity;
  }

  async deleteIdentity(did: string): Promise<void> {
    const identity = this.identities.get(did);
    if (!identity) {
      throw new Error('Identity not found');
    }

    this.identities.delete(did);
    await this.removeStoredIdentity(did);
    
    if (this.activeSession?.did === did) {
      this.logout();
    }
    
    this.emit('identity:deleted', { did });
  }

  getIdentity(did: string): DIDIdentity | undefined {
    return this.identities.get(did);
  }

  getAllIdentities(): DIDIdentity[] {
    return Array.from(this.identities.values());
  }

  getIdentitiesByType(type: IdentityType): DIDIdentity[] {
    return this.getAllIdentities().filter(id => id.type === type);
  }

  // ==================== DID操作 ====================

  private async generateDID(method: DIDMethod): Promise<string> {
    const networkId = 'sepolia';
    const address = await this.generateAddress();
    return `did:${method}:${networkId}:${address}`;
  }

  private async generateAddress(): Promise<string> {
    const array = new Uint8Array(20);
    crypto.getRandomValues(array);
    return '0x' + Array.from(array, b => b.toString(16).padStart(2, '0')).join('');
  }

  private async createDIDDocument(did: string, method: DIDMethod): Promise<DIDDocument> {
    const keyPair = await this.generateKeyPair();
    const timestamp = new Date().toISOString();
    
    return {
      '@context': [
        'https://www.w3.org/ns/did/v1',
        'https://w3id.org/security/suites/ed25519-2020/v1'
      ],
      id: did,
      verificationMethod: [{
        id: `${did}#keys-1`,
        type: 'Ed25519VerificationKey2020',
        controller: did,
        publicKeyMultibase: keyPair.publicKey
      }],
      authentication: [`${did}#keys-1`],
      assertionMethod: [`${did}#keys-1`],
      keyAgreement: [`${did}#keys-1`],
      capabilityInvocation: [`${did}#keys-1`],
      capabilityDelegation: [`${did}#keys-1`],
      service: [],
      created: timestamp,
      updated: timestamp
    };
  }

  private async generateKeyPair(): Promise<{ publicKey: string; privateKey: string }> {
    const privateArray = new Uint8Array(32);
    const publicArray = new Uint8Array(32);
    crypto.getRandomValues(privateArray);
    crypto.getRandomValues(publicArray);
    
    return {
      privateKey: 'z' + btoa(String.fromCharCode(...privateArray)),
      publicKey: 'z' + btoa(String.fromCharCode(...publicArray))
    };
  }

  private detectDIDMethod(did: string): DIDMethod {
    const match = did.match(/^did:(\w+):/);
    if (match) {
      const method = match[1] as DIDMethod;
      if (Object.values(DIDMethod).includes(method)) {
        return method;
      }
    }
    return DIDMethod.KEY;
  }

  // ==================== 解析与缓存 ====================

  async resolveDID(did: string, forceRefresh = false): Promise<DIDDocument> {
    if (!forceRefresh && this.config.cacheEnabled) {
      const cached = this.documentCache.get(did);
      if (cached && Date.now() - cached.timestamp < this.config.cacheTTL) {
        return cached.document;
      }
    }

    try {
      const document = await this.fetchDIDDocument(did);
      
      if (this.config.cacheEnabled) {
        this.documentCache.set(did, { document, timestamp: Date.now() });
      }
      
      this.emit('did:resolved', { did, document });
      return document;
    } catch (error) {
      this.emit('error', { type: 'did_resolution_failed', did, error });
      throw error;
    }
  }

  private async fetchDIDDocument(did: string): Promise<DIDDocument> {
    const response = await fetch(`${this.config.resolverUrl}/1.0/identifiers/${did}`);
    
    if (!response.ok) {
      throw new Error(`Failed to resolve DID: ${response.statusText}`);
    }
    
    const result = await response.json();
    return result.didDocument;
  }

  private handleDocumentResolved(data: { did: string; document: DIDDocument }): void {
    if (this.config.cacheEnabled) {
      this.documentCache.set(data.did, { 
        document: data.document, 
        timestamp: Date.now() 
      });
    }
    this.emit('did:resolved', data);
  }

  // ==================== 凭证管理 ====================

  async addCredential(did: string, credential: VerifiableCredential): Promise<void> {
    const identity = this.identities.get(did);
    if (!identity) {
      throw new Error('Identity not found');
    }

    const verified = await this.verifyCredential(credential);
    credential.status = verified ? VerificationStatus.VERIFIED : VerificationStatus.FAILED;
    
    identity.credentials.push(credential);
    identity.updatedAt = Date.now();
    
    await this.storeIdentity(identity);
    this.emit('credential:added', { did, credential });
  }

  async removeCredential(did: string, credentialId: string): Promise<void> {
    const identity = this.identities.get(did);
    if (!identity) {
      throw new Error('Identity not found');
    }

    identity.credentials = identity.credentials.filter(c => c.id !== credentialId);
    identity.updatedAt = Date.now();
    
    await this.storeIdentity(identity);
    this.emit('credential:removed', { did, credentialId });
  }

  async verifyCredential(credential: VerifiableCredential): Promise<boolean> {
    try {
      if (credential.expirationDate) {
        const expiry = new Date(credential.expirationDate).getTime();
        if (expiry < Date.now()) {
          credential.status = VerificationStatus.EXPIRED;
          return false;
        }
      }

      const issuerDoc = await this.resolveDID(credential.issuer);
      const verificationMethod = issuerDoc.verificationMethod.find(
        vm => vm.id === credential.proof.verificationMethod
      );

      if (!verificationMethod) {
        return false;
      }

      this.emit('credential:verified', { credential, result: true });
      return true;
    } catch (error) {
      this.emit('error', { type: 'credential_verification_failed', credential, error });
      return false;
    }
  }

  private handleCredentialVerified(data: { credential: VerifiableCredential; result: boolean }): void {
    this.emit('credential:verified', data);
  }

  // ==================== 认证流程 ====================

  async authenticate(did: string, domain: string, purpose: string = 'authentication'): Promise<AuthSession> {
    try {
      const identity = this.identities.get(did);
      if (!identity) {
        throw new Error('Identity not found');
      }

      const challenge = await this.createChallenge(domain, purpose);
      const response = await this.signChallenge(challenge, identity);
      
      const verified = await this.verifyResponse(response, challenge);
      if (!verified) {
        throw new Error('Authentication verification failed');
      }

      const session = await this.createSession(identity, ['read', 'write', 'manage']);
      this.activeSession = session;
      
      this.emit('auth:success', { did, session });
      return session;
    } catch (error) {
      this.emit('auth:failed', { did, error });
      throw error;
    }
  }

  async authenticateWithQR(sessionToken: string): Promise<AuthSession> {
    try {
      this.emit('auth:qr-pending', { sessionToken });
      
      const response = await fetch(`${this.config.resolverUrl}/auth/session/${sessionToken}`);
      const data = await response.json();
      
      if (!data.verified) {
        throw new Error('QR authentication failed');
      }

      const identity = this.identities.get(data.did);
      if (!identity) {
        throw new Error('Identity not found in local storage');
      }

      const session = await this.createSession(identity, data.permissions);
      this.activeSession = session;
      
      this.emit('auth:success', { did: data.did, session });
      return session;
    } catch (error) {
      this.emit('auth:failed', { sessionToken, error });
      throw error;
    }
  }

  private async createChallenge(domain: string, purpose: string): Promise<AuthChallenge> {
    const challenge: AuthChallenge = {
      challengeId: this.generateId(),
      domain,
      nonce: this.generateNonce(),
      issuedAt: Date.now(),
      expiresAt: Date.now() + this.config.challengeExpiry,
      purpose
    };
    
    this.challenges.set(challenge.challengeId, challenge);
    return challenge;
  }

  private generateId(): string {
    return 'challenge_' + Math.random().toString(36).substring(2, 15);
  }

  private generateNonce(): string {
    const array = new Uint8Array(32);
    crypto.getRandomValues(array);
    return btoa(String.fromCharCode(...array));
  }

  private async signChallenge(challenge: AuthChallenge, identity: DIDIdentity): Promise<AuthResponse> {
    const message = `${challenge.domain}:${challenge.nonce}:${challenge.issuedAt}`;
    const signature = await this.createSignature(message, identity);
    
    return {
      challengeId: challenge.challengeId,
      did: identity.did,
      signature,
      timestamp: Date.now()
    };
  }

  private async createSignature(message: string, identity: DIDIdentity): Promise<string> {
    const encoder = new TextEncoder();
    const data = encoder.encode(message);
    const signature = new Uint8Array(64);
    crypto.getRandomValues(signature);
    return btoa(String.fromCharCode(...signature));
  }

  private async verifyResponse(response: AuthResponse, challenge: AuthChallenge): Promise<boolean> {
    if (Date.now() > challenge.expiresAt) {
      return false;
    }

    try {
      const document = await this.resolveDID(response.did);
      const verificationMethod = document.verificationMethod.find(
        vm => document.authentication.includes(vm.id)
      );

      return !!verificationMethod;
    } catch {
      return false;
    }
  }

  private handleChallengeReceived(challenge: AuthChallenge): void {
    this.challenges.set(challenge.challengeId, challenge);
    this.emit('challenge:received', challenge);
  }

  private handleAuthCompleted(data: { did: string; session: AuthSession }): void {
    this.activeSession = data.session;
    this.emit('auth:completed', data);
  }

  private async createSession(identity: DIDIdentity, permissions: string[]): Promise<AuthSession> {
    const session: AuthSession = {
      sessionId: this.generateId(),
      did: identity.did,
      identity,
      authenticatedAt: Date.now(),
      expiresAt: Date.now() + 86400000,
      permissions,
      metadata: {}
    };

    this.sessions.set(session.sessionId, session);
    return session;
  }

  // ==================== 会话管理 ====================

  getActiveSession(): AuthSession | null {
    return this.activeSession;
  }

  isAuthenticated(): boolean {
    if (!this.activeSession) return false;
    return Date.now() < this.activeSession.expiresAt;
  }

  logout(): void {
    if (this.activeSession) {
      this.sessions.delete(this.activeSession.sessionId);
      this.emit('session:ended', this.activeSession);
      this.activeSession = null;
    }
  }

  async refreshSession(): Promise<AuthSession> {
    if (!this.activeSession) {
      throw new Error('No active session');
    }

    this.activeSession.expiresAt = Date.now() + 86400000;
    this.emit('session:refreshed', this.activeSession);
    return this.activeSession;
  }

  // ==================== 存储管理 ====================

  private async loadStoredIdentities(): Promise<void> {
    try {
      const stored = localStorage.getItem('did:identities');
      if (stored) {
        const identities: DIDIdentity[] = JSON.parse(stored);
        identities.forEach(id => this.identities.set(id.did, id));
      }
    } catch (error) {
      console.error('Failed to load identities:', error);
    }
  }

  private async storeIdentity(identity: DIDIdentity): Promise<void> {
    try {
      const identities = this.getAllIdentities();
      localStorage.setItem('did:identities', JSON.stringify(identities));
    } catch (error) {
      console.error('Failed to store identity:', error);
    }
  }

  private async removeStoredIdentity(did: string): Promise<void> {
    try {
      const identities = this.getAllIdentities().filter(id => id.did !== did);
      localStorage.setItem('did:identities', JSON.stringify(identities));
    } catch (error) {
      console.error('Failed to remove identity:', error);
    }
  }

  // ==================== 配置管理 ====================

  getConfig(): DIDAuthConfig {
    return { ...this.config };
  }

  updateConfig(updates: Partial<DIDAuthConfig>): void {
    this.config = { ...this.config, ...updates };
    this.emit('config:updated', this.config);
  }

  addBlockchainConfig(networkId: string, config: BlockchainConfig): void {
    this.config.blockchainConfigs.set(networkId, config);
    this.emit('config:blockchain-added', { networkId, config });
  }

  // ==================== 工具方法 ====================

  exportIdentity(did: string, includePrivateKey = false): string {
    const identity = this.identities.get(did);
    if (!identity) {
      throw new Error('Identity not found');
    }

    const exportData = {
      did: identity.did,
      method: identity.method,
      type: identity.type,
      document: identity.document,
      metadata: identity.metadata,
      credentials: identity.credentials,
      createdAt: identity.createdAt
    };

    return JSON.stringify(exportData, null, 2);
  }

  async validateDID(did: string): Promise<boolean> {
    try {
      await this.resolveDID(did);
      return true;
    } catch {
      return false;
    }
  }

  clearCache(): void {
    this.documentCache.clear();
    this.emit('cache:cleared');
  }

  dispose(): void {
    this.removeAllListeners();
    this.identities.clear();
    this.sessions.clear();
    this.documentCache.clear();
    this.challenges.clear();
    this.activeSession = null;
    DIDAuthService.instance = null as any;
  }
}

export default DIDAuthService.getInstance();
