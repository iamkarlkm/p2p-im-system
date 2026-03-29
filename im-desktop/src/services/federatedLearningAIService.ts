/**
 * 联邦学习 AI API 服务
 * 提供与联邦学习后端 API 的完整交互功能
 * 
 * @version 1.0
 * @created 2026-03-23
 */

import {
  FLServerConfig,
  FLServer,
  ServerStatusResponse,
  FLModel,
  FLModelUpdate,
  FLClient,
  FLTrainingRound,
  PrivacyBudget,
  RegisterServerRequest,
  RegisterServerResponse,
  StartTrainingRoundResponse,
  ClientUpdateData,
  SmartReplyResponse,
  SpamDetectionResponse,
  SentimentAnalysisResponse,
  MessageCategorizationResponse,
  ApiResponse,
  PaginatedResponse,
  PaginationParams
} from '../types/federatedLearningAI';

// ==================== 服务配置 ====================

const DEFAULT_BASE_URL = '/api/fl';
const DEFAULT_TIMEOUT = 30000; // 30 秒
const DEFAULT_RETRY_COUNT = 3;
const DEFAULT_RETRY_DELAY = 1000; // 1 秒

/** 服务配置接口 */
interface ServiceConfig {
  baseUrl: string;
  timeout: number;
  retryCount: number;
  retryDelay: number;
  headers: Record<string, string>;
}

/** 默认服务配置 */
const defaultConfig: ServiceConfig = {
  baseUrl: DEFAULT_BASE_URL,
  timeout: DEFAULT_TIMEOUT,
  retryCount: DEFAULT_RETRY_COUNT,
  retryDelay: DEFAULT_RETRY_DELAY,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
};

// ==================== HTTP 客户端 ====================

/**
 * 执行 HTTP 请求
 */
async function httpRequest<T>(
  url: string,
  options: RequestInit,
  config: ServiceConfig
): Promise<ApiResponse<T>> {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), config.timeout);

  try {
    const response = await fetch(url, {
      ...options,
      signal: controller.signal,
      headers: {
        ...config.headers,
        ...options.headers
      }
    });

    clearTimeout(timeoutId);

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new Error(errorData.message || `HTTP ${response.status}: ${response.statusText}`);
    }

    const data = await response.json();
    return {
      success: true,
      data: data as T,
      timestamp: new Date().toISOString()
    };
  } catch (error) {
    clearTimeout(timeoutId);
    
    if (error instanceof Error) {
      return {
        success: false,
        error: error.message,
        statusCode: 500,
        timestamp: new Date().toISOString()
      };
    }
    
    return {
      success: false,
      error: 'Unknown error occurred',
      statusCode: 500,
      timestamp: new Date().toISOString()
    };
  }
}

/**
 * 带重试的 HTTP 请求
 */
async function httpRequestWithRetry<T>(
  url: string,
  options: RequestInit,
  config: ServiceConfig
): Promise<ApiResponse<T>> {
  let lastError: Error | null = null;

  for (let attempt = 0; attempt < config.retryCount; attempt++) {
    try {
      const result = await httpRequest<T>(url, options, config);
      
      if (result.success) {
        return result;
      }
      
      lastError = new Error(result.error);
    } catch (error) {
      lastError = error instanceof Error ? error : new Error('Unknown error');
    }

    if (attempt < config.retryCount - 1) {
      await new Promise(resolve => 
        setTimeout(resolve, config.retryDelay * Math.pow(2, attempt))
      );
    }
  }

  return {
    success: false,
    error: lastError?.message || 'Request failed after all retries',
    statusCode: 500,
    timestamp: new Date().toISOString()
  };
}

// ==================== 联邦学习 AI 服务类 ====================

export class FederatedLearningAIService {
  private config: ServiceConfig;

  constructor(config?: Partial<ServiceConfig>) {
    this.config = { ...defaultConfig, ...config };
  }

  /**
   * 更新服务配置
   */
  updateConfig(newConfig: Partial<ServiceConfig>): void {
    this.config = { ...this.config, ...newConfig };
  }

  // ==================== 服务器管理 API ====================

  /**
   * 注册联邦学习服务器
   */
  async registerServer(request: RegisterServerRequest): Promise<ApiResponse<RegisterServerResponse>> {
    const url = `${this.config.baseUrl}/servers/register`;
    
    return httpRequestWithRetry<RegisterServerResponse>(url, {
      method: 'POST',
      body: JSON.stringify(request)
    }, this.config);
  }

  /**
   * 获取服务器状态
   */
  async getServerStatus(serverId: string): Promise<ApiResponse<ServerStatusResponse>> {
    const url = `${this.config.baseUrl}/servers/${serverId}/status`;
    
    return httpRequestWithRetry<ServerStatusResponse>(url, {
      method: 'GET'
    }, this.config);
  }

  /**
   * 更新服务器配置
   */
  async updateServerConfig(
    serverId: string, 
    config: Partial<FLServerConfig>
  ): Promise<ApiResponse<{ serverId: string; configUpdated: string[]; updatedAt: string }>> {
    const url = `${this.config.baseUrl}/servers/${serverId}/config`;
    
    return httpRequestWithRetry(url, {
      method: 'PUT',
      body: JSON.stringify(config)
    }, this.config);
  }

  /**
   * 注销服务器
   */
  async unregisterServer(serverId: string): Promise<ApiResponse<{ serverId: string; unregisteredAt: string }>> {
    const url = `${this.config.baseUrl}/servers/${serverId}`;
    
    return httpRequestWithRetry(url, {
      method: 'DELETE'
    }, this.config);
  }

  // ==================== 模型管理 API ====================

  /**
   * 初始化全局模型
   */
  async initializeModel(request: {
    serverId: string;
    modelType: string;
    language?: string;
    modelName: string;
  }): Promise<ApiResponse<{ modelId: string; status: string; version: string }>> {
    const url = `${this.config.baseUrl}/models/init`;
    
    return httpRequestWithRetry(url, {
      method: 'POST',
      body: JSON.stringify(request)
    }, this.config);
  }

  /**
   * 开始训练轮次
   */
  async startTrainingRound(
    modelId: string, 
    targetClientCount?: number
  ): Promise<ApiResponse<StartTrainingRoundResponse>> {
    const url = `${this.config.baseUrl}/models/${modelId}/round/start`;
    
    return httpRequestWithRetry(url, {
      method: 'POST',
      body: JSON.stringify({ targetClientCount })
    }, this.config);
  }

  /**
   * 获取训练轮次状态
   */
  async getTrainingRoundStatus(
    modelId: string, 
    roundId: string
  ): Promise<ApiResponse<{
    modelId: string;
    roundId: string;
    status: string;
    selectedClients: number;
    receivedUpdates: number;
    verifiedUpdates: number;
    progress: number;
    estimatedCompletionTime: string;
  }>> {
    const url = `${this.config.baseUrl}/models/${modelId}/round/${roundId}`;
    
    return httpRequestWithRetry(url, {
      method: 'GET'
    }, this.config);
  }

  /**
   * 执行模型聚合
   */
  async aggregateModelUpdates(
    modelId: string, 
    roundId: string
  ): Promise<ApiResponse<{
    modelId: string;
    roundNumber: number;
    aggregatedUpdates: number;
    totalUpdates: number;
    averageAccuracy: number;
    aggregationAlgorithm: string;
    modelConverged: boolean;
    newModelVersion: string;
  }>> {
    const url = `${this.config.baseUrl}/models/${modelId}/round/${roundId}/aggregate`;
    
    return httpRequestWithRetry(url, {
      method: 'POST'
    }, this.config);
  }

  /**
   * 获取模型版本
   */
  async getModelVersion(
    modelId: string, 
    versionId: string
  ): Promise<ApiResponse<{
    modelId: string;
    versionId: string;
    version: string;
    accuracy: number;
    loss: number;
    downloadUrl: string;
  }>> {
    const url = `${this.config.baseUrl}/models/${modelId}/version/${versionId}`;
    
    return httpRequestWithRetry(url, {
      method: 'GET'
    }, this.config);
  }

  /**
   * 分发模型
   */
  async distributeModel(
    modelId: string, 
    versionId: string, 
    clientIds: string[]
  ): Promise<ApiResponse<{
    modelId: string;
    versionId: string;
    distributedTo: number;
    distributionStatus: string;
  }>> {
    const url = `${this.config.baseUrl}/models/${modelId}/version/${versionId}/distribute`;
    
    return httpRequestWithRetry(url, {
      method: 'POST',
      body: JSON.stringify({ clientIds })
    }, this.config);
  }

  // ==================== 客户端管理 API ====================

  /**
   * 获取在线客户端列表
   */
  async getOnlineClients(serverId?: string): Promise<ApiResponse<{
    onlineClients: Array<{
      clientId: string;
      deviceType: string;
      platform: string;
      isCharging: boolean;
      batteryLevel: number;
      networkType: string;
      lastSeenAt: string;
    }>;
    totalCount: number;
  }>> {
    const url = `${this.config.baseUrl}/clients/online${serverId ? `?serverId=${serverId}` : ''}`;
    
    return httpRequestWithRetry(url, {
      method: 'GET'
    }, this.config);
  }

  /**
   * 选择客户端参与训练
   */
  async selectClient(
    clientId: string, 
    modelId: string, 
    roundId: string
  ): Promise<ApiResponse<{
    clientId: string;
    modelId: string;
    roundId: string;
    selected: boolean;
    trainingTask: string;
    estimatedTrainingTime: number;
  }>> {
    const url = `${this.config.baseUrl}/clients/${clientId}/select`;
    
    return httpRequestWithRetry(url, {
      method: 'POST',
      body: JSON.stringify({ modelId, roundId })
    }, this.config);
  }

  /**
   * 获取客户端更新
   */
  async getClientUpdates(
    clientId: string, 
    modelId: string, 
    round: number
  ): Promise<ApiResponse<{
    clientId: string;
    modelId: string;
    round: number;
    hasUpdate: boolean;
    updateSize: number;
    updateStatus: string;
  }>> {
    const url = `${this.config.baseUrl}/clients/${clientId}/updates?modelId=${modelId}&round=${round}`;
    
    return httpRequestWithRetry(url, {
      method: 'GET'
    }, this.config);
  }

  /**
   * 验证客户端更新
   */
  async verifyClientUpdate(
    clientId: string, 
    updateId: string, 
    signature: string
  ): Promise<ApiResponse<{
    updateId: string;
    clientId: string;
    verified: boolean;
    verifiedAt: string;
  }>> {
    const url = `${this.config.baseUrl}/clients/${clientId}/updates/verify`;
    
    return httpRequestWithRetry(url, {
      method: 'POST',
      body: JSON.stringify({ updateId, signature })
    }, this.config);
  }

  // ==================== 隐私保护 API ====================

  /**
   * 获取隐私预算状态
   */
  async getPrivacyBudget(serverId: string): Promise<ApiResponse<{
    serverId: string;
    totalBudget: number;
    usedBudget: number;
    remainingBudget: number;
    budgetPeriod: string;
    resetDate: string;
    clientCount: number;
    averageBudgetPerClient: number;
  }>> {
    const url = `${this.config.baseUrl}/privacy/${serverId}/budget`;
    
    return httpRequestWithRetry(url, {
      method: 'GET'
    }, this.config);
  }

  /**
   * 分配隐私预算
   */
  async allocatePrivacyBudget(
    serverId: string, 
    epsilonPerRound: number
  ): Promise<ApiResponse<{
    serverId: string;
    epsilonPerRound: number;
    maxRounds: number;
    allocatedAt: string;
  }>> {
    const url = `${this.config.baseUrl}/privacy/${serverId}/budget/allocate`;
    
    return httpRequestWithRetry(url, {
      method: 'POST',
      body: JSON.stringify({ epsilonPerRound })
    }, this.config);
  }

  // ==================== 性能监控 API ====================

  /**
   * 获取模型性能指标
   */
  async getModelPerformanceMetrics(modelId: string): Promise<ApiResponse<{
    modelId: string;
    metrics: {
      accuracy: number;
      loss: number;
      f1Score: number;
      precision: number;
      recall: number;
      convergenceRate: number;
      trainingTime: number;
      clientParticipationRate: number;
      averageUpdateSize: number;
    };
    collectedAt: string;
  }>> {
    const url = `${this.config.baseUrl}/performance/${modelId}/metrics`;
    
    return httpRequestWithRetry(url, {
      method: 'GET'
    }, this.config);
  }

  /**
   * 优化模型性能
   */
  async optimizeModelPerformance(
    modelId: string, 
    optimizationType?: string
  ): Promise<ApiResponse<{
    modelId: string;
    optimizationType: string;
    optimizations: string[];
    expectedImprovement: number;
    optimizedAt: string;
  }>> {
    const url = `${this.config.baseUrl}/performance/${modelId}/optimize`;
    
    return httpRequestWithRetry(url, {
      method: 'POST',
      body: JSON.stringify({ optimizationType })
    }, this.config);
  }

  // ==================== AI 功能 API ====================

  /**
   * 获取智能回复建议
   */
  async getSmartReplySuggestions(request: {
    userId: string;
    message: string;
    context?: string;
    language?: string;
    maxSuggestions?: number;
  }): Promise<ApiResponse<SmartReplyResponse>> {
    const url = `${this.config.baseUrl}/ai/smart-reply`;
    
    return httpRequestWithRetry<SmartReplyResponse>(url, {
      method: 'POST',
      body: JSON.stringify(request)
    }, this.config);
  }

  /**
   * 检测垃圾消息
   */
  async detectSpam(request: {
    userId: string;
    message: string;
    sender: string;
    metadata?: Record<string, any>;
  }): Promise<ApiResponse<SpamDetectionResponse>> {
    const url = `${this.config.baseUrl}/ai/spam-detection`;
    
    return httpRequestWithRetry<SpamDetectionResponse>(url, {
      method: 'POST',
      body: JSON.stringify(request)
    }, this.config);
  }

  /**
   * 分析消息情感
   */
  async analyzeSentiment(request: {
    userId: string;
    message: string;
    language?: string;
  }): Promise<ApiResponse<SentimentAnalysisResponse>> {
    const url = `${this.config.baseUrl}/ai/sentiment-analysis`;
    
    return httpRequestWithRetry<SentimentAnalysisResponse>(url, {
      method: 'POST',
      body: JSON.stringify(request)
    }, this.config);
  }

  /**
   * 分类消息
   */
  async categorizeMessage(request: {
    userId: string;
    message: string;
  }): Promise<ApiResponse<MessageCategorizationResponse>> {
    const url = `${this.config.baseUrl}/ai/message-categorization`;
    
    return httpRequestWithRetry<MessageCategorizationResponse>(url, {
      method: 'POST',
      body: JSON.stringify(request)
    }, this.config);
  }

  // ==================== 工具方法 ====================

  /**
   * 健康检查
   */
  async healthCheck(): Promise<ApiResponse<{
    status: string;
    timestamp: string;
    service: string;
    version: string;
  }>> {
    const url = `${this.config.baseUrl}/health`;
    
    return httpRequestWithRetry(url, {
      method: 'GET'
    }, this.config);
  }

  /**
   * 上传客户端模型更新
   */
  async uploadClientUpdate(
    modelId: string,
    clientId: string,
    updateData: ClientUpdateData
  ): Promise<ApiResponse<{
    updateId: string;
    modelId: string;
    clientId: string;
    trainingRound: number;
    status: string;
    qualityScore: number;
    uploadedAt: string;
  }>> {
    const url = `${this.config.baseUrl}/models/${modelId}/clients/${clientId}/updates`;
    
    return httpRequestWithRetry(url, {
      method: 'POST',
      body: JSON.stringify(updateData)
    }, this.config);
  }

  /**
   * 下载全局模型
   */
  async downloadModel(modelId: string, version?: string): Promise<ApiResponse<Blob>> {
    const url = `${this.config.baseUrl}/models/${modelId}/download${version ? `?version=${version}` : ''}`;
    
    const controller = new AbortController();
    const timeoutId = setTimeout(() => controller.abort(), this.config.timeout);

    try {
      const response = await fetch(url, {
        method: 'GET',
        signal: controller.signal,
        headers: this.config.headers
      });

      clearTimeout(timeoutId);

      if (!response.ok) {
        throw new Error(`HTTP ${response.status}: ${response.statusText}`);
      }

      const blob = await response.blob();
      
      return {
        success: true,
        data: blob,
        timestamp: new Date().toISOString()
      };
    } catch (error) {
      clearTimeout(timeoutId);
      
      return {
        success: false,
        error: error instanceof Error ? error.message : 'Download failed',
        statusCode: 500,
        timestamp: new Date().toISOString()
      };
    }
  }
}

// ==================== 导出单例实例 ====================

export const flAIService = new FederatedLearningAIService();

export default FederatedLearningAIService;
