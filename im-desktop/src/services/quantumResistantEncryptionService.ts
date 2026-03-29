/**
 * 量子抗性加密 API 服务
 * 提供后量子密码学密钥管理和加密操作的HTTP客户端服务
 */

import axios, { AxiosInstance, AxiosResponse } from 'axios';
import {
  QuantumResistantEncryptionKey,
  PostQuantumSignature,
  KeyGenerationRequest,
  EncryptionRequest,
  EncryptionResponse,
  DecryptionRequest,
  DecryptionResponse,
  KeyRotationResponse,
  CleanupResponse,
  StatisticsResponse,
  ApiResponseMessage,
  QuantumResistantAlgorithmType,
  KeyUsage,
  EncryptionMode,
  SecurityLevel,
} from '../types/quantumResistantEncryption';

/**
 * 量子抗性加密服务类
 */
export class QuantumResistantEncryptionService {
  private apiClient: AxiosInstance;
  
  /**
   * 构造函数
   * @param baseURL API基础URL
   * @param timeout 请求超时时间（毫秒）
   */
  constructor(baseURL: string = '/api', timeout: number = 30000) {
    this.apiClient = axios.create({
      baseURL,
      timeout,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json',
      },
    });
  }
  
  /**
   * 生成新的量子抗性密钥对
   */
  async generateKeyPair(request: KeyGenerationRequest): Promise<QuantumResistantEncryptionKey> {
    try {
      const params = new URLSearchParams();
      params.append('algorithmType', request.algorithmType);
      params.append('algorithmParameter', request.algorithmParameter);
      params.append('keyUsage', request.keyUsage || KeyUsage.KEY_AGREEMENT);
      params.append('encryptionMode', request.encryptionMode || EncryptionMode.PURE_PQC);
      params.append('securityLevel', request.securityLevel || SecurityLevel.LEVEL_3);
      params.append('keySize', (request.keySize || 256).toString());
      
      if (request.expiresAt) {
        params.append('expiresAt', request.expiresAt);
      }
      
      const response: AxiosResponse<QuantumResistantEncryptionKey> = await this.apiClient.post(
        '/v1/quantum-resistant-encryption/keys/generate',
        null,
        { params }
      );
      
      return response.data;
    } catch (error) {
      this.handleApiError(error, 'Failed to generate quantum-resistant key pair');
    }
  }
  
  /**
   * 获取指定密钥信息
   */
  async getKey(keyId: string): Promise<QuantumResistantEncryptionKey> {
    try {
      const response: AxiosResponse<QuantumResistantEncryptionKey> = await this.apiClient.get(
        `/v1/quantum-resistant-encryption/keys/${keyId}`
      );
      
      return response.data;
    } catch (error) {
      this.handleApiError(error, `Failed to get key: ${keyId}`);
    }
  }
  
  /**
   * 获取所有活动密钥
   */
  async getActiveKeys(): Promise<QuantumResistantEncryptionKey[]> {
    try {
      const response: AxiosResponse<QuantumResistantEncryptionKey[]> = await this.apiClient.get(
        '/v1/quantum-resistant-encryption/keys/active'
      );
      
      return response.data;
    } catch (error) {
      this.handleApiError(error, 'Failed to get active keys');
    }
  }
  
  /**
   * 根据算法类型获取密钥
   */
  async getKeysByAlgorithm(algorithmType: QuantumResistantAlgorithmType): Promise<QuantumResistantEncryptionKey[]> {
    try {
      const response: AxiosResponse<QuantumResistantEncryptionKey[]> = await this.apiClient.get(
        `/v1/quantum-resistant-encryption/keys/algorithm/${algorithmType}`
      );
      
      return response.data;
    } catch (error) {
      this.handleApiError(error, `Failed to get keys by algorithm: ${algorithmType}`);
    }
  }
  
  /**
   * 根据安全级别获取密钥
   */
  async getKeysBySecurityLevel(securityLevel: SecurityLevel): Promise<QuantumResistantEncryptionKey[]> {
    try {
      const response: AxiosResponse<QuantumResistantEncryptionKey[]> = await this.apiClient.get(
        `/v1/quantum-resistant-encryption/keys/security-level/${securityLevel}`
      );
      
      return response.data;
    } catch (error) {
      this.handleApiError(error, `Failed to get keys by security level: ${securityLevel}`);
    }
  }
  
  /**
   * 加密数据
   */
  async encryptData(keyId: string, plaintext: string, additionalData?: string): Promise<EncryptionResponse> {
    try {
      const request: EncryptionRequest = { plaintext, additionalData };
      
      const response: AxiosResponse<EncryptionResponse> = await this.apiClient.post(
        `/v1/quantum-resistant-encryption/encrypt/${keyId}`,
        request
      );
      
      return response.data;
    } catch (error) {
      this.handleApiError(error, `Failed to encrypt data with key: ${keyId}`);
    }
  }
  
  /**
   * 解密数据
   */
  async decryptData(keyId: string, encryptedData: string, additionalData?: string): Promise<DecryptionResponse> {
    try {
      const request: DecryptionRequest = { encryptedData, additionalData };
      
      const response: AxiosResponse<DecryptionResponse> = await this.apiClient.post(
        `/v1/quantum-resistant-encryption/decrypt/${keyId}`,
        request
      );
      
      return response.data;
    } catch (error) {
      this.handleApiError(error, `Failed to decrypt data with key: ${keyId}`);
    }
  }
  
  /**
   * 撤销密钥
   */
  async revokeKey(keyId: string, reason: string = 'Manual revocation'): Promise<ApiResponseMessage> {
    try {
      const params = new URLSearchParams();
      params.append('reason', reason);
      
      const response: AxiosResponse<ApiResponseMessage> = await this.apiClient.post(
        `/v1/quantum-resistant-encryption/keys/${keyId}/revoke`,
        null,
        { params }
      );
      
      return response.data;
    } catch (error) {
      this.handleApiError(error, `Failed to revoke key: ${keyId}`);
    }
  }
  
  /**
   * 轮换密钥
   */
  async rotateKey(keyId: string, newAlgorithmParameter: string = 'Kyber768'): Promise<KeyRotationResponse> {
    try {
      const params = new URLSearchParams();
      params.append('newAlgorithmParameter', newAlgorithmParameter);
      
      const response: AxiosResponse<KeyRotationResponse> = await this.apiClient.post(
        `/v1/quantum-resistant-encryption/keys/${keyId}/rotate`,
        null,
        { params }
      );
      
      return response.data;
    } catch (error) {
      this.handleApiError(error, `Failed to rotate key: ${keyId}`);
    }
  }
  
  /**
   * 更新密钥过期时间
   */
  async updateKeyExpiration(keyId: string, newExpiresAt: string): Promise<ApiResponseMessage> {
    try {
      const params = new URLSearchParams();
      params.append('newExpiresAt', newExpiresAt);
      
      const response: AxiosResponse<ApiResponseMessage> = await this.apiClient.put(
        `/v1/quantum-resistant-encryption/keys/${keyId}/expiration`,
        null,
        { params }
      );
      
      return response.data;
    } catch (error) {
      this.handleApiError(error, `Failed to update key expiration: ${keyId}`);
    }
  }
  
  /**
   * 清理过期密钥
   */
  async cleanExpiredKeys(): Promise<CleanupResponse> {
    try {
      const response: AxiosResponse<CleanupResponse> = await this.apiClient.post(
        '/v1/quantum-resistant-encryption/keys/cleanup-expired'
      );
      
      return response.data;
    } catch (error) {
      this.handleApiError(error, 'Failed to clean expired keys');
    }
  }
  
  /**
   * 启用HSM支持
   */
  async enableHsmSupport(keyId: string, hsmIdentifier: string): Promise<ApiResponseMessage> {
    try {
      const params = new URLSearchParams();
      params.append('hsmIdentifier', hsmIdentifier);
      
      const response: AxiosResponse<ApiResponseMessage> = await this.apiClient.post(
        `/v1/quantum-resistant-encryption/keys/${keyId}/enable-hsm`,
        null,
        { params }
      );
      
      return response.data;
    } catch (error) {
      this.handleApiError(error, `Failed to enable HSM support for key: ${keyId}`);
    }
  }
  
  /**
   * 集成量子密钥分发
   */
  async integrateQkd(keyId: string, qkdIntegrationId: string): Promise<ApiResponseMessage> {
    try {
      const params = new URLSearchParams();
      params.append('qkdIntegrationId', qkdIntegrationId);
      
      const response: AxiosResponse<ApiResponseMessage> = await this.apiClient.post(
        `/v1/quantum-resistant-encryption/keys/${keyId}/integrate-qkd`,
        null,
        { params }
      );
      
      return response.data;
    } catch (error) {
      this.handleApiError(error, `Failed to integrate QKD for key: ${keyId}`);
    }
  }
  
  /**
   * 获取性能统计
   */
  async getPerformanceStatistics(): Promise<StatisticsResponse> {
    try {
      const response: AxiosResponse<StatisticsResponse> = await this.apiClient.get(
        '/v1/quantum-resistant-encryption/statistics'
      );
      
      return response.data;
    } catch (error) {
      this.handleApiError(error, 'Failed to get performance statistics');
    }
  }
  
  /**
   * 批量加密数据
   */
  async batchEncrypt(
    keyId: string,
    plaintexts: string[],
    additionalData?: string
  ): Promise<EncryptionResponse[]> {
    try {
      const promises = plaintexts.map(plaintext =>
        this.encryptData(keyId, plaintext, additionalData)
      );
      
      return await Promise.all(promises);
    } catch (error) {
      this.handleApiError(error, 'Failed to batch encrypt data');
    }
  }
  
  /**
   * 批量解密数据
   */
  async batchDecrypt(
    keyId: string,
    encryptedDataList: string[],
    additionalData?: string
  ): Promise<DecryptionResponse[]> {
    try {
      const promises = encryptedDataList.map(encryptedData =>
        this.decryptData(keyId, encryptedData, additionalData)
      );
      
      return await Promise.all(promises);
    } catch (error) {
      this.handleApiError(error, 'Failed to batch decrypt data');
    }
  }
  
  /**
   * 检查密钥有效性
   */
  async validateKey(keyId: string): Promise<{ valid: boolean; reason?: string }> {
    try {
      const key = await this.getKey(keyId);
      
      if (!key) {
        return { valid: false, reason: 'Key not found' };
      }
      
      const now = new Date();
      const expiresAt = key.keyExpiresAt ? new Date(key.keyExpiresAt) : null;
      
      if (key.keyStatus !== 'ACTIVE') {
        return { valid: false, reason: `Key status is ${key.keyStatus}` };
      }
      
      if (expiresAt && expiresAt < now) {
        return { valid: false, reason: 'Key expired' };
      }
      
      return { valid: true };
    } catch (error) {
      return { valid: false, reason: 'Key validation failed' };
    }
  }
  
  /**
   * 获取密钥使用统计
   */
  async getKeyUsageStatistics(keyId: string): Promise<{
    usageCount: number;
    lastUsedAt?: string;
    averageUsagePerDay?: number;
  }> {
    try {
      const key = await this.getKey(keyId);
      
      if (!key) {
        throw new Error('Key not found');
      }
      
      let averageUsagePerDay: number | undefined;
      
      if (key.keyGeneratedAt) {
        const generatedAt = new Date(key.keyGeneratedAt);
        const now = new Date();
        const daysDiff = Math.max(1, (now.getTime() - generatedAt.getTime()) / (1000 * 60 * 60 * 24));
        averageUsagePerDay = key.usageCount / daysDiff;
      }
      
      return {
        usageCount: key.usageCount,
        lastUsedAt: key.lastUsedAt,
        averageUsagePerDay,
      };
    } catch (error) {
      this.handleApiError(error, `Failed to get usage statistics for key: ${keyId}`);
    }
  }
  
  /**
   * 导出密钥信息
   */
  async exportKey(keyId: string, includePrivateKey: boolean = false): Promise<string> {
    try {
      const key = await this.getKey(keyId);
      
      if (!key) {
        throw new Error('Key not found');
      }
      
      const exportData = {
        id: key.id,
        algorithmType: key.algorithmType,
        algorithmParameter: key.algorithmParameter,
        publicKey: key.publicKey,
        privateKeyEncrypted: includePrivateKey ? key.privateKeyEncrypted : undefined,
        keySize: key.keySize,
        securityLevel: key.securityLevel,
        keyUsage: key.keyUsage,
        encryptionMode: key.encryptionMode,
        keyGeneratedAt: key.keyGeneratedAt,
        keyExpiresAt: key.keyExpiresAt,
        keyStatus: key.keyStatus,
        metadata: key.metadata,
        exportTimestamp: new Date().toISOString(),
        exportFormat: 'PQC_KEY_EXPORT_V1',
      };
      
      return JSON.stringify(exportData, null, 2);
    } catch (error) {
      this.handleApiError(error, `Failed to export key: ${keyId}`);
    }
  }
  
  /**
   * 导入密钥
   */
  async importKey(exportData: string): Promise<QuantumResistantEncryptionKey> {
    try {
      const parsed = JSON.parse(exportData);
      
      if (parsed.exportFormat !== 'PQC_KEY_EXPORT_V1') {
        throw new Error('Unsupported export format');
      }
      
      // 这里应该调用后端的导入API
      // 由于后端API未提供导入接口，这里模拟返回
      const simulatedKey: QuantumResistantEncryptionKey = {
        id: parsed.id || `imported_${Date.now()}`,
        algorithmType: parsed.algorithmType,
        algorithmParameter: parsed.algorithmParameter,
        publicKey: parsed.publicKey,
        privateKeyEncrypted: parsed.privateKeyEncrypted,
        keySize: parsed.keySize,
        securityLevel: parsed.securityLevel,
        keyUsage: parsed.keyUsage,
        encryptionMode: parsed.encryptionMode,
        keyGeneratedAt: parsed.keyGeneratedAt || new Date().toISOString(),
        keyExpiresAt: parsed.keyExpiresAt,
        usageCount: 0,
        keyStatus: 'ACTIVE',
        metadata: {
          ...parsed.metadata,
          importedAt: new Date().toISOString(),
          originalId: parsed.id,
        },
        hsmEnabled: false,
        createdAt: new Date().toISOString(),
        updatedAt: new Date().toISOString(),
      };
      
      return simulatedKey;
    } catch (error) {
      this.handleApiError(error, 'Failed to import key');
    }
  }
  
  /**
   * 设置请求拦截器
   */
  setRequestInterceptor(
    onSuccess?: (config: any) => any,
    onError?: (error: any) => any
  ): void {
    this.apiClient.interceptors.request.use(onSuccess, onError);
  }
  
  /**
   * 设置响应拦截器
   */
  setResponseInterceptor(
    onSuccess?: (response: any) => any,
    onError?: (error: any) => any
  ): void {
    this.apiClient.interceptors.response.use(onSuccess, onError);
  }
  
  /**
   * 处理API错误
   */
  private handleApiError(error: any, defaultMessage: string): never {
    if (error.response) {
      // 服务器响应错误
      const status = error.response.status;
      const data = error.response.data;
      
      throw new Error(
        `${defaultMessage}: ${status} - ${data?.message || data?.errorMessage || 'Unknown error'}`
      );
    } else if (error.request) {
      // 请求发送失败
      throw new Error(`${defaultMessage}: Network error - ${error.message}`);
    } else {
      // 请求配置错误
      throw new Error(`${defaultMessage}: ${error.message}`);
    }
  }
}

/**
 * 创建量子抗性加密服务实例
 */
export const createQuantumResistantEncryptionService = (
  baseURL?: string,
  timeout?: number
): QuantumResistantEncryptionService => {
  return new QuantumResistantEncryptionService(baseURL, timeout);
};

/**
 * 默认服务实例（单例模式）
 */
let defaultServiceInstance: QuantumResistantEncryptionService | null = null;

export const getQuantumResistantEncryptionService = (
  baseURL: string = '/api',
  timeout: number = 30000
): QuantumResistantEncryptionService => {
  if (!defaultServiceInstance) {
    defaultServiceInstance = createQuantumResistantEncryptionService(baseURL, timeout);
  }
  return defaultServiceInstance;
};