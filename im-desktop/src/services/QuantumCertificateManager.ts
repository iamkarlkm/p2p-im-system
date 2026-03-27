import { EventEmitter } from 'events';
import { apiClient } from '../utils/apiClient';

/**
 * 量子安全证书管理器
 * 支持NIST PQC标准的量子安全证书全生命周期管理
 */

export interface QuantumSecureCertificate {
  id: string;
  serialNumber: string;
  subjectName: string;
  issuerName: string;
  algorithmType: PQCAlgorithmType;
  keySize: number;
  publicKeyFingerprint: string;
  validFrom: Date;
  validTo: Date;
  status: CertificateStatus;
  caId: string;
  parentCertificateId?: string;
  certificateChain: string[];
  revocationReason?: RevocationReason;
  revokedAt?: Date;
  crlDistributionPoint?: string;
  ocspResponderUrl?: string;
  transparencyLogId?: string;
  transparencyTimestamp?: Date;
  signatureAlgorithm: SignatureAlgorithm;
  keyUsage: KeyUsage[];
  extendedKeyUsage: ExtendedKeyUsage[];
  isCA: boolean;
  pathLengthConstraint?: number;
  subjectAlternativeNames: string[];
  autoRenewalEnabled: boolean;
  renewalThresholdDays: number;
  createdAt: Date;
  updatedAt: Date;
}

export interface QuantumCertificateAuthority {
  id: string;
  name: string;
  caType: CAType;
  algorithmType: PQCAlgorithmType;
  status: CAStatus;
  certificateId: string;
  parentCaId?: string;
  certificateCount: number;
  maxCertificates: number;
  validityDays: number;
  crlPublishInterval: number;
  ocspEnabled: boolean;
  transparencyLoggingEnabled: boolean;
  createdAt: Date;
  updatedAt: Date;
}

export enum PQCAlgorithmType {
  CRYSTALS_KYBER = 'CRYSTALS_KYBER',
  CRYSTALS_DILITHIUM = 'CRYSTALS_DILITHIUM',
  FALCON = 'FALCON',
  SPHINCS_PLUS = 'SPHINCS_PLUS',
  CLASSIC_MCELIECE = 'CLASSIC_MCELIECE',
  BIKE = 'BIKE',
  HQC = 'HQC',
  HYBRID_DILITHIUM_RSA = 'HYBRID_DILITHIUM_RSA',
  HYBRID_FALCON_ECDSA = 'HYBRID_FALCON_ECDSA'
}

export enum CertificateStatus {
  ACTIVE = 'ACTIVE',
  EXPIRED = 'EXPIRED',
  REVOKED = 'REVOKED',
  PENDING = 'PENDING',
  SUSPENDED = 'SUSPENDED'
}

export enum CAStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  COMPROMISED = 'COMPROMISED'
}

export enum CAType {
  ROOT = 'ROOT',
  INTERMEDIATE = 'INTERMEDIATE',
  ISSUING = 'ISSUING'
}

export enum RevocationReason {
  UNSPECIFIED = 'UNSPECIFIED',
  KEY_COMPROMISE = 'KEY_COMPROMISE',
  CA_COMPROMISE = 'CA_COMPROMISE',
  AFFILIATION_CHANGED = 'AFFILIATION_CHANGED',
  SUPERSEDED = 'SUPERSEDED',
  CESSATION_OF_OPERATION = 'CESSATION_OF_OPERATION',
  CERTIFICATE_HOLD = 'CERTIFICATE_HOLD',
  REMOVE_FROM_CRL = 'REMOVE_FROM_CRL',
  PRIVILEGE_WITHDRAWN = 'PRIVILEGE_WITHDRAWN',
  AA_COMPROMISE = 'AA_COMPROMISE'
}

export enum SignatureAlgorithm {
  DILITHIUM2 = 'DILITHIUM2',
  DILITHIUM3 = 'DILITHIUM3',
  DILITHIUM5 = 'DILITHIUM5',
  FALCON512 = 'FALCON512',
  FALCON1024 = 'FALCON1024',
  SPHINCS_SHA256_128S = 'SPHINCS_SHA256_128S',
  SPHINCS_SHA256_192S = 'SPHINCS_SHA256_192S',
  SPHINCS_SHA256_256S = 'SPHINCS_SHA256_256S'
}

export enum KeyUsage {
  DIGITAL_SIGNATURE = 'DIGITAL_SIGNATURE',
  NON_REPUDIATION = 'NON_REPUDIATION',
  KEY_ENCIPHERMENT = 'KEY_ENCIPHERMENT',
  DATA_ENCIPHERMENT = 'KEY_AGREEMENT',
  CERTIFICATE_SIGN = 'CERTIFICATE_SIGN',
  CRL_SIGN = 'CRL_SIGN',
  ENCIPHER_ONLY = 'ENCIPHER_ONLY',
  DECIPHER_ONLY = 'DECIPHER_ONLY'
}

export enum ExtendedKeyUsage {
  SERVER_AUTH = 'SERVER_AUTH',
  CLIENT_AUTH = 'CLIENT_AUTH',
  CODE_SIGNING = 'CODE_SIGNING',
  EMAIL_PROTECTION = 'EMAIL_PROTECTION',
  TIME_STAMPING = 'TIME_STAMPING',
  OCSP_SIGNING = 'OCSP_SIGNING'
}

export interface CertificateCreateRequest {
  subjectName: string;
  algorithmType: PQCAlgorithmType;
  validityDays: number;
  caId: string;
  keyUsage: KeyUsage[];
  extendedKeyUsage: ExtendedKeyUsage[];
  subjectAlternativeNames?: string[];
  autoRenewalEnabled?: boolean;
}

export interface CertificateValidationResult {
  valid: boolean;
  certificateId: string;
  chainValidation: boolean;
  signatureValidation: boolean;
  expirationValidation: boolean;
  revocationValidation: boolean;
  transparencyValidation: boolean;
  algorithmStrength: 'HIGH' | 'MEDIUM' | 'LOW';
  warnings: string[];
  errors: string[];
  validatedAt: Date;
}

export interface CertificateChainValidationResult {
  valid: boolean;
  chainLength: number;
  rootCAValid: boolean;
  intermediateCAValid: boolean;
  leafCertificateValid: boolean;
  chainTrust: boolean;
  warnings: string[];
  errors: string[];
}

export interface BulkOperationResult {
  success: boolean;
  processedCount: number;
  successCount: number;
  failureCount: number;
  failures: { certificateId: string; error: string }[];
}

export class QuantumCertificateManager extends EventEmitter {
  private certificates: Map<string, QuantumSecureCertificate> = new Map();
  private certificateAuthorities: Map<string, QuantumCertificateAuthority> = new Map();
  private refreshInterval: NodeJS.Timeout | null = null;

  constructor() {
    super();
    this.startAutoRefresh();
  }

  /**
   * 创建新证书
   */
  async createCertificate(request: CertificateCreateRequest): Promise<QuantumSecureCertificate> {
    try {
      const response = await apiClient.post<QuantumSecureCertificate>('/api/v1/quantum-certificates', request);
      const certificate = this.processCertificateDates(response);
      this.certificates.set(certificate.id, certificate);
      this.emit('certificateCreated', certificate);
      return certificate;
    } catch (error) {
      this.emit('error', { operation: 'createCertificate', error });
      throw error;
    }
  }

  /**
   * 获取证书详情
   */
  async getCertificate(certificateId: string): Promise<QuantumSecureCertificate | null> {
    // 先查本地缓存
    if (this.certificates.has(certificateId)) {
      const cached = this.certificates.get(certificateId)!;
      if (!this.isCacheExpired(cached)) {
        return cached;
      }
    }

    try {
      const response = await apiClient.get<QuantumSecureCertificate>(`/api/v1/quantum-certificates/${certificateId}`);
      const certificate = this.processCertificateDates(response);
      this.certificates.set(certificateId, certificate);
      return certificate;
    } catch (error) {
      this.emit('error', { operation: 'getCertificate', certificateId, error });
      return null;
    }
  }

  /**
   * 获取所有证书
   */
  async getAllCertificates(status?: CertificateStatus): Promise<QuantumSecureCertificate[]> {
    try {
      const params = status ? { status } : {};
      const response = await apiClient.get<QuantumSecureCertificate[]>('/api/v1/quantum-certificates', params);
      const certificates = response.map(cert => this.processCertificateDates(cert));
      
      // 更新本地缓存
      certificates.forEach(cert => {
        this.certificates.set(cert.id, cert);
      });
      
      return certificates;
    } catch (error) {
      this.emit('error', { operation: 'getAllCertificates', error });
      return Array.from(this.certificates.values());
    }
  }

  /**
   * 验证证书
   */
  async validateCertificate(certificateId: string): Promise<CertificateValidationResult> {
    try {
      const result = await apiClient.post<CertificateValidationResult>(`/api/v1/quantum-certificates/${certificateId}/validate`);
      result.validatedAt = new Date();
      this.emit('certificateValidated', result);
      return result;
    } catch (error) {
      this.emit('error', { operation: 'validateCertificate', certificateId, error });
      throw error;
    }
  }

  /**
   * 批量验证证书
   */
  async validateCertificates(certificateIds: string[]): Promise<CertificateValidationResult[]> {
    try {
      const results = await apiClient.post<CertificateValidationResult[]>('/api/v1/quantum-certificates/validate/batch', {
        certificateIds
      });
      results.forEach(result => {
        result.validatedAt = new Date();
      });
      this.emit('certificatesValidated', results);
      return results;
    } catch (error) {
      this.emit('error', { operation: 'validateCertificates', error });
      throw error;
    }
  }

  /**
   * 验证证书链
   */
  async validateCertificateChain(certificateId: string): Promise<CertificateChainValidationResult> {
    try {
      const result = await apiClient.post<CertificateChainValidationResult>(`/api/v1/quantum-certificates/${certificateId}/validate-chain`);
      this.emit('certificateChainValidated', { certificateId, result });
      return result;
    } catch (error) {
      this.emit('error', { operation: 'validateCertificateChain', certificateId, error });
      throw error;
    }
  }

  /**
   * 撤销证书
   */
  async revokeCertificate(certificateId: string, reason: RevocationReason): Promise<void> {
    try {
      await apiClient.post(`/api/v1/quantum-certificates/${certificateId}/revoke`, { reason });
      
      // 更新本地缓存
      const cert = this.certificates.get(certificateId);
      if (cert) {
        cert.status = CertificateStatus.REVOKED;
        cert.revocationReason = reason;
        cert.revokedAt = new Date();
      }
      
      this.emit('certificateRevoked', { certificateId, reason });
    } catch (error) {
      this.emit('error', { operation: 'revokeCertificate', certificateId, error });
      throw error;
    }
  }

  /**
   * 续期证书
   */
  async renewCertificate(certificateId: string, validityDays?: number): Promise<QuantumSecureCertificate> {
    try {
      const response = await apiClient.post<QuantumSecureCertificate>(`/api/v1/quantum-certificates/${certificateId}/renew`, {
        validityDays
      });
      const certificate = this.processCertificateDates(response);
      this.certificates.set(certificateId, certificate);
      this.emit('certificateRenewed', certificate);
      return certificate;
    } catch (error) {
      this.emit('error', { operation: 'renewCertificate', certificateId, error });
      throw error;
    }
  }

  /**
   * 删除证书
   */
  async deleteCertificate(certificateId: string): Promise<void> {
    try {
      await apiClient.delete(`/api/v1/quantum-certificates/${certificateId}`);
      this.certificates.delete(certificateId);
      this.emit('certificateDeleted', certificateId);
    } catch (error) {
      this.emit('error', { operation: 'deleteCertificate', certificateId, error });
      throw error;
    }
  }

  /**
   * 批量删除证书
   */
  async bulkDeleteCertificates(certificateIds: string[]): Promise<BulkOperationResult> {
    try {
      const result = await apiClient.post<BulkOperationResult>('/api/v1/quantum-certificates/bulk-delete', {
        certificateIds
      });
      
      if (result.success) {
        certificateIds.forEach(id => this.certificates.delete(id));
      }
      
      this.emit('certificatesBulkDeleted', result);
      return result;
    } catch (error) {
      this.emit('error', { operation: 'bulkDeleteCertificates', error });
      throw error;
    }
  }

  /**
   * 导出证书
   */
  async exportCertificate(certificateId: string, format: 'PEM' | 'DER' | 'PKCS12'): Promise<Blob> {
    try {
      const response = await apiClient.get<Blob>(`/api/v1/quantum-certificates/${certificateId}/export`, {
        format
      }, { responseType: 'blob' });
      this.emit('certificateExported', { certificateId, format });
      return response;
    } catch (error) {
      this.emit('error', { operation: 'exportCertificate', certificateId, error });
      throw error;
    }
  }

  /**
   * 导入证书
   */
  async importCertificate(data: Blob, format: 'PEM' | 'DER' | 'PKCS12'): Promise<QuantumSecureCertificate> {
    try {
      const formData = new FormData();
      formData.append('file', data);
      formData.append('format', format);
      
      const response = await apiClient.post<QuantumSecureCertificate>('/api/v1/quantum-certificates/import', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      
      const certificate = this.processCertificateDates(response);
      this.certificates.set(certificate.id, certificate);
      this.emit('certificateImported', certificate);
      return certificate;
    } catch (error) {
      this.emit('error', { operation: 'importCertificate', error });
      throw error;
    }
  }

  /**
   * 检查证书是否需要续期
   */
  async checkRenewalNeeded(certificateId: string): Promise<boolean> {
    const certificate = await this.getCertificate(certificateId);
    if (!certificate || !certificate.autoRenewalEnabled) {
      return false;
    }
    
    const daysUntilExpiry = Math.ceil((certificate.validTo.getTime() - Date.now()) / (1000 * 60 * 60 * 24));
    return daysUntilExpiry <= certificate.renewalThresholdDays;
  }

  /**
   * 获取即将过期的证书
   */
  async getExpiringCertificates(daysThreshold: number = 30): Promise<QuantumSecureCertificate[]> {
    try {
      const response = await apiClient.get<QuantumSecureCertificate[]>('/api/v1/quantum-certificates/expiring', {
        daysThreshold
      });
      return response.map(cert => this.processCertificateDates(cert));
    } catch (error) {
      this.emit('error', { operation: 'getExpiringCertificates', error });
      // 从本地缓存计算
      return Array.from(this.certificates.values()).filter(cert => {
        const daysUntilExpiry = Math.ceil((cert.validTo.getTime() - Date.now()) / (1000 * 60 * 60 * 24));
        return daysUntilExpiry <= daysThreshold && daysUntilExpiry > 0;
      });
    }
  }

  /**
   * 获取证书颁发机构列表
   */
  async getCertificateAuthorities(): Promise<QuantumCertificateAuthority[]> {
    try {
      const response = await apiClient.get<QuantumCertificateAuthority[]>('/api/v1/quantum-certificate-authorities');
      const cas = response.map(ca => this.processCADates(ca));
      
      cas.forEach(ca => {
        this.certificateAuthorities.set(ca.id, ca);
      });
      
      return cas;
    } catch (error) {
      this.emit('error', { operation: 'getCertificateAuthorities', error });
      return Array.from(this.certificateAuthorities.values());
    }
  }

  /**
   * 获取推荐的算法类型
   */
  async getRecommendedAlgorithm(): Promise<PQCAlgorithmType> {
    try {
      const response = await apiClient.get<{ algorithm: PQCAlgorithmType }>('/api/v1/quantum-certificates/recommended-algorithm');
      return response.algorithm;
    } catch (error) {
      // 默认推荐 CRYSTALS_DILITHIUM
      return PQCAlgorithmType.CRYSTALS_DILITHIUM;
    }
  }

  /**
   * 获取算法安全强度评估
   */
  async getAlgorithmSecurityAssessment(algorithm: PQCAlgorithmType): Promise<{
    algorithm: PQCAlgorithmType;
    securityLevel: 'HIGH' | 'MEDIUM' | 'LOW';
    nistCategory: number;
    keySize: number;
    signatureSize: number;
    performanceRating: number;
    recommendation: string;
  }> {
    try {
      return await apiClient.get(`/api/v1/quantum-certificates/algorithm-assessment/${algorithm}`);
    } catch (error) {
      this.emit('error', { operation: 'getAlgorithmSecurityAssessment', algorithm, error });
      throw error;
    }
  }

  /**
   * 本地缓存管理
   */
  clearCache(): void {
    this.certificates.clear();
    this.certificateAuthorities.clear();
    this.emit('cacheCleared');
  }

  getCachedCertificates(): QuantumSecureCertificate[] {
    return Array.from(this.certificates.values());
  }

  /**
   * 启动自动刷新
   */
  private startAutoRefresh(intervalMs: number = 60000): void {
    this.refreshInterval = setInterval(async () => {
      await this.refreshAllCertificates();
    }, intervalMs);
  }

  /**
   * 停止自动刷新
   */
  stopAutoRefresh(): void {
    if (this.refreshInterval) {
      clearInterval(this.refreshInterval);
      this.refreshInterval = null;
    }
  }

  /**
   * 刷新所有证书
   */
  private async refreshAllCertificates(): Promise<void> {
    try {
      await this.getAllCertificates();
      this.emit('certificatesRefreshed');
    } catch (error) {
      this.emit('error', { operation: 'refreshAllCertificates', error });
    }
  }

  /**
   * 处理证书日期
   */
  private processCertificateDates(cert: any): QuantumSecureCertificate {
    return {
      ...cert,
      validFrom: new Date(cert.validFrom),
      validTo: new Date(cert.validTo),
      revokedAt: cert.revokedAt ? new Date(cert.revokedAt) : undefined,
      transparencyTimestamp: cert.transparencyTimestamp ? new Date(cert.transparencyTimestamp) : undefined,
      createdAt: new Date(cert.createdAt),
      updatedAt: new Date(cert.updatedAt)
    };
  }

  /**
   * 处理CA日期
   */
  private processCADates(ca: any): QuantumCertificateAuthority {
    return {
      ...ca,
      createdAt: new Date(ca.createdAt),
      updatedAt: new Date(ca.updatedAt)
    };
  }

  /**
   * 检查缓存是否过期
   */
  private isCacheExpired(cert: QuantumSecureCertificate): boolean {
    const cacheAge = Date.now() - cert.updatedAt.getTime();
    return cacheAge > 300000; // 5分钟过期
  }

  /**
   * 销毁管理器
   */
  destroy(): void {
    this.stopAutoRefresh();
    this.clearCache();
    this.removeAllListeners();
  }
}

// 导出单例实例
export const quantumCertificateManager = new QuantumCertificateManager();
