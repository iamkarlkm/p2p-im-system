import axios, { AxiosInstance, AxiosResponse } from 'axios';
import {
  QuantumResistantEncryption,
  PostQuantumSignature,
  SupportedAlgorithms,
  KeyPairGenerationRequest,
  EncryptionRequest,
  DecryptionRequest,
  SignatureCreationRequest,
  SignatureVerificationRequest,
  RevocationRequest,
  EncryptionResponse,
  DecryptionResponse,
  SignatureCreationResponse,
  SignatureVerificationResponse,
  PerformanceMetrics,
  QuantumEncryptionStatistics,
  HealthCheckResponse,
  AlgorithmConfiguration,
  AlgorithmComparison,
  OperationResult,
  QuantumEncryptionError
} from '../types/quantumResistantEncryption';

/**
 * 量子抗性加密 API 服务
 * 提供与后端后量子密码学系统的交互接口
 */
export class QuantumResistantEncryptionService {
  private apiClient: AxiosInstance;
  private baseURL: string;
  private cache: Map<string, any> = new Map();
  private cacheExpiry: Map<string, number> = new Map();

  constructor(baseURL: string = 'http://localhost:8080/api/v1/quantum-resistant-encryption') {
    this.baseURL = baseURL;
    this.apiClient = axios.create({
      baseURL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json'
      }
    });

    // 请求拦截器
    this.apiClient.interceptors.request.use(
      config => {
        // 可以在这里添加认证token
        return config;
      },
      error => Promise.reject(error)
    );

    // 响应拦截器
    this.apiClient.interceptors.response.use(
      response => response,
      error => {
        console.error('Quantum encryption API error:', error);
        return Promise.reject(this.handleError(error));
      }
    );
  }

  /**
   * 获取支持的算法列表
   */
  async getSupportedAlgorithms(): Promise<SupportedAlgorithms> {
    const cacheKey = 'supportedAlgorithms';
    const cached = this.getFromCache(cacheKey);
    if (cached) return cached;

    const response: AxiosResponse<SupportedAlgorithms> = await this.apiClient.get('/supported-algorithms');
    this.setCache(cacheKey, response.data, 300000); // 缓存5分钟
    return response.data;
  }

  /**
   * 生成量子抗性密钥对
   */
  async generateKeyPair(request: KeyPairGenerationRequest): Promise<QuantumResistantEncryption> {
    const response: AxiosResponse<QuantumResistantEncryption> = await this.apiClient.post('/generate-keypair', request);
    return response.data;
  }

  /**
   * 获取所有激活的加密配置
   */
  async getActiveEncryptions(): Promise<QuantumResistantEncryption[]> {
    const response: AxiosResponse<QuantumResistantEncryption[]> = await this.apiClient.get('/active-encryptions');
    return response.data;
  }

  /**
   * 获取特定加密配置
   */
  async getEncryption(id: number): Promise<QuantumResistantEncryption> {
    const response: AxiosResponse<QuantumResistantEncryption> = await this.apiClient.get(`/encryption/${id}`);
    return response.data;
  }

  /**
   * 加密数据
   */
  async encryptData(request: EncryptionRequest): Promise<EncryptionResponse> {
    const response: AxiosResponse<EncryptionResponse> = await this.apiClient.post('/encrypt', request);
    return response.data;
  }

  /**
   * 解密数据
   */
  async decryptData(request: DecryptionRequest): Promise<DecryptionResponse> {
    const response: AxiosResponse<DecryptionResponse> = await this.apiClient.post('/decrypt', request);
    return response.data;
  }

  /**
   * 批量加密
   */
  async encryptBatch(requests: EncryptionRequest[]): Promise<OperationResult<EncryptionResponse>[]> {
    const promises = requests.map(req => this.encryptData(req)
      .then(data => ({ success: true, data, timestamp: Date.now() } as OperationResult<EncryptionResponse>))
      .catch(error => ({ success: false, error: error.message, timestamp: Date.now() } as OperationResult<EncryptionResponse>))
    );
    return Promise.all(promises);
  }

  /**
   * 批量解密
   */
  async decryptBatch(requests: DecryptionRequest[]): Promise<OperationResult<DecryptionResponse>[]> {
    const promises = requests.map(req => this.decryptData(req)
      .then(data => ({ success: true, data, timestamp: Date.now() } as OperationResult<DecryptionResponse>))
      .catch(error => ({ success: false, error: error.message, timestamp: Date.now() } as OperationResult<DecryptionResponse>))
    );
    return Promise.all(promises);
  }

  /**
   * 创建后量子签名
   */
  async createSignature(request: SignatureCreationRequest): Promise<SignatureCreationResponse> {
    const response: AxiosResponse<SignatureCreationResponse> = await this.apiClient.post('/create-signature', request);
    return response.data;
  }

  /**
   * 验证签名
   */
  async verifySignature(request: SignatureVerificationRequest): Promise<SignatureVerificationResponse> {
    const response: AxiosResponse<SignatureVerificationResponse> = await this.apiClient.post('/verify-signature', request);
    return response.data;
  }

  /**
   * 批量创建签名
   */
  async createSignatureBatch(requests: SignatureCreationRequest[]): Promise<OperationResult<SignatureCreationResponse>[]> {
    const promises = requests.map(req => this.createSignature(req)
      .then(data => ({ success: true, data, timestamp: Date.now() } as OperationResult<SignatureCreationResponse>))
      .catch(error => ({ success: false, error: error.message, timestamp: Date.now() } as OperationResult<SignatureCreationResponse>))
    );
    return Promise.all(promises);
  }

  /**
   * 批量验证签名
   */
  async verifySignatureBatch(requests: SignatureVerificationRequest[]): Promise<OperationResult<SignatureVerificationResponse>[]> {
    const promises = requests.map(req => this.verifySignature(req)
      .then(data => ({ success: true, data, timestamp: Date.now() } as OperationResult<SignatureVerificationResponse>))
      .catch(error => ({ success: false, error: error.message, timestamp: Date.now() } as OperationResult<SignatureVerificationResponse>))
    );
    return Promise.all(promises);
  }

  /**
   * 获取算法性能统计
   */
  async getAlgorithmPerformance(algorithmName: string): Promise<PerformanceMetrics> {
    const cacheKey = `performance_${algorithmName}`;
    const cached = this.getFromCache(cacheKey);
    if (cached) return cached;

    const response: AxiosResponse<PerformanceMetrics> = await this.apiClient.get(`/performance/${algorithmName}`);
    this.setCache(cacheKey, response.data, 60000); // 缓存1分钟
    return response.data;
  }

  /**
   * 获取所有有效的签名
   */
  async getValidSignatures(): Promise<PostQuantumSignature[]> {
    const response: AxiosResponse<PostQuantumSignature[]> = await this.apiClient.get('/valid-signatures');
    return response.data;
  }

  /**
   * 停用加密配置
   */
  async deactivateEncryption(id: number): Promise<{ encryptionId: number; algorithmName: string; status: string; deactivatedAt: string }> {
    const response: AxiosResponse<{ encryptionId: number; algorithmName: string; status: string; deactivatedAt: string }> = 
      await this.apiClient.put(`/deactivate/${id}`);
    return response.data;
  }

  /**
   * 吊销签名
   */
  async revokeSignature(id: number, reason?: string): Promise<{ signatureId: string; status: string; revocationReason?: string; revokedAt: string }> {
    const response: AxiosResponse<{ signatureId: string; status: string; revocationReason?: string; revokedAt: string }> = 
      await this.apiClient.put(`/revoke-signature/${id}`, { reason } as RevocationRequest);
    return response.data;
  }

  /**
   * 健康检查
   */
  async healthCheck(): Promise<HealthCheckResponse> {
    const response: AxiosResponse<HealthCheckResponse> = await this.apiClient.get('/health');
    return response.data;
  }

  /**
   * 获取系统统计信息
   */
  async getStatistics(): Promise<QuantumEncryptionStatistics> {
    const cacheKey = 'statistics';
    const cached = this.getFromCache(cacheKey);
    if (cached) return cached;

    const response: AxiosResponse<QuantumEncryptionStatistics> = await this.apiClient.get('/