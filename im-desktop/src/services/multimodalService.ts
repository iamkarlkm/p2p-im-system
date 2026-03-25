/**
 * 多模态内容理解引擎 - TypeScript API 服务
 */

import {
  MultimodalConfig,
  AnalysisRequest,
  AnalysisResult,
  ConfigStats,
  ResultStats,
  AnalysisStatusResponse,
  HistoryItem,
  AnalysisQueryParams,
  SentimentDistribution,
  IntentDistribution,
  SceneDistribution,
  ApiResponse,
} from '../types/multimodal';

const API_BASE = '/api/multimodal';

/**
 * 多模态内容理解 API 服务
 */
class MultimodalService {
  private baseUrl: string;

  constructor(baseUrl: string = API_BASE) {
    this.baseUrl = baseUrl;
  }

  // ==================== HTTP 工具方法 ====================

  private async request<T>(
    endpoint: string,
    method: string = 'GET',
    body?: any
  ): Promise<ApiResponse<T>> {
    const options: RequestInit = {
      method,
      headers: {
        'Content-Type': 'application/json',
      },
    };

    if (body) {
      options.body = JSON.stringify(body);
    }

    const response = await fetch(`${this.baseUrl}${endpoint}`, options);
    
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({ message: '请求失败' }));
      throw new Error(errorData.message || `HTTP ${response.status}`);
    }

    return response.json();
  }

  // ==================== 配置管理 API ====================

  /**
   * 创建新的多模态配置
   */
  async createConfig(config: MultimodalConfig): Promise<MultimodalConfig> {
    const response = await this.request<MultimodalConfig>('/configs', 'POST', config);
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data!;
  }

  /**
   * 更新配置
   */
  async updateConfig(id: number, config: Partial<MultimodalConfig>): Promise<MultimodalConfig> {
    const response = await this.request<MultimodalConfig>(`/configs/${id}`, 'PUT', config);
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data!;
  }

  /**
   * 根据 ID 获取配置
   */
  async getConfigById(id: number): Promise<MultimodalConfig> {
    const response = await this.request<MultimodalConfig>(`/configs/${id}`);
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data!;
  }

  /**
   * 根据名称获取配置
   */
  async getConfigByName(name: string): Promise<MultimodalConfig> {
    const response = await this.request<MultimodalConfig>(`/configs/name/${name}`);
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data!;
  }

  /**
   * 获取所有启用的配置
   */
  async getAllEnabledConfigs(): Promise<MultimodalConfig[]> {
    const response = await this.request<MultimodalConfig[]>('/configs/enabled');
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data || [];
  }

  /**
   * 获取默认配置
   */
  async getDefaultConfig(): Promise<MultimodalConfig> {
    const response = await this.request<MultimodalConfig>('/configs/default');
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data!;
  }

  /**
   * 禁用配置
   */
  async disableConfig(id: number): Promise<void> {
    await this.request(`/configs/${id}/disable`, 'POST');
  }

  /**
   * 启用配置
   */
  async enableConfig(id: number): Promise<void> {
    await this.request(`/configs/${id}/enable`, 'POST');
  }

  /**
   * 删除配置
   */
  async deleteConfig(id: number): Promise<void> {
    await this.request(`/configs/${id}`, 'DELETE');
  }

  /**
   * 获取配置统计信息
   */
  async getConfigStats(): Promise<ConfigStats> {
    const response = await this.request<ConfigStats>('/configs/stats');
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data!;
  }

  // ==================== 分析结果 API ====================

  /**
   * 提交分析请求
   */
  async analyze(request: AnalysisRequest): Promise<{ requestId: string; status: string }> {
    const response = await this.request<{ requestId: string; status: string }>('/analyze', 'POST', request);
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data!;
  }

  /**
   * 获取分析结果状态
   */
  async getAnalysisStatus(requestId: string): Promise<AnalysisStatusResponse> {
    const response = await this.request<AnalysisStatusResponse>(`/results/${requestId}/status`);
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data!;
  }

  /**
   * 获取分析结果详情
   */
  async getAnalysisResult(requestId: string): Promise<AnalysisResult | { status: string }> {
    const response = await this.request<AnalysisResult>(`/results/${requestId}`);
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data!;
  }

  /**
   * 根据消息 ID 获取分析结果
   */
  async getAnalysisResultByMessageId(messageId: number): Promise<AnalysisResult | null> {
    const response = await this.request<AnalysisResult | null>(`/results/by-message/${messageId}`);
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data || null;
  }

  /**
   * 获取用户分析历史
   */
  async getUserAnalysisHistory(userId: number, limit: number = 20): Promise<HistoryItem[]> {
    const response = await this.request<HistoryItem[]>(`/users/${userId}/history?limit=${limit}`);
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data || [];
  }

  /**
   * 获取会话分析历史
   */
  async getSessionAnalysisHistory(sessionId: string, limit: number = 20): Promise<HistoryItem[]> {
    const response = await this.request<HistoryItem[]>(`/sessions/${sessionId}/history?limit=${limit}`);
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data || [];
  }

  /**
   * 获取分析结果统计
   */
  async getResultStats(): Promise<ResultStats> {
    const response = await this.request<ResultStats>('/results/stats');
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data!;
  }

  /**
   * 批量查询分析结果
   */
  async searchResults(params: AnalysisQueryParams): Promise<{
    page: number;
    size: number;
    total: number;
    results: HistoryItem[];
  }> {
    const queryParams = new URLSearchParams();
    
    if (params.userId) queryParams.append('userId', params.userId.toString());
    if (params.contentType) queryParams.append('contentType', params.contentType);
    if (params.status) queryParams.append('status', params.status);
    if (params.startDate) queryParams.append('startDate', params.startDate);
    if (params.endDate) queryParams.append('endDate', params.endDate);
    queryParams.append('page', params.page?.toString() || '0');
    queryParams.append('size', params.size?.toString() || '20');
    queryParams.append('sortBy', params.sortBy || 'createdAt');
    queryParams.append('sortDir', params.sortDir || 'DESC');

    const response = await this.request(`/results?${queryParams.toString()}`);
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data!;
  }

  // ==================== 系统管理 API ====================

  /**
   * 清理过期缓存
   */
  async cleanupExpiredCache(): Promise<{ cleanedCount: number; timestamp: string }> {
    const response = await this.request('/admin/cleanup-cache', 'POST');
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data!;
  }

  /**
   * 重试失败请求
   */
  async retryFailedRequests(): Promise<{ retriedCount: number; requests: Array<{ requestId: string; retryCount: number }> }> {
    const response = await this.request('/admin/retry-failed', 'POST');
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data!;
  }

  // ==================== 统计分析 API ====================

  /**
   * 获取情感分布
   */
  async getSentimentDistribution(): Promise<SentimentDistribution[]> {
    const response = await this.request<SentimentDistribution[]>('/stats/sentiment-distribution');
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data || [];
  }

  /**
   * 获取意图分布
   */
  async getIntentDistribution(): Promise<IntentDistribution[]> {
    const response = await this.request<IntentDistribution[]>('/stats/intent-distribution');
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data || [];
  }

  /**
   * 获取场景分布
   */
  async getSceneDistribution(): Promise<SceneDistribution[]> {
    const response = await this.request<SceneDistribution[]>('/stats/scene-distribution');
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data || [];
  }

  /**
   * 系统健康检查
   */
  async healthCheck(): Promise<{
    status: string;
    timestamp: string;
    configStats: ConfigStats;
    resultStats: ResultStats;
    message: string;
  }> {
    const response = await this.request('/health');
    if (!response.success) {
      throw new Error(response.message);
    }
    return response.data!;
  }

  // ==================== 辅助方法 ====================

  /**
   * 轮询分析结果直到完成
   */
  async pollUntilComplete(
    requestId: string,
    intervalMs: number = 1000,
    timeoutMs: number = 60000
  ): Promise<AnalysisResult> {
    const startTime = Date.now();

    while (Date.now() - startTime < timeoutMs) {
      const status = await this.getAnalysisStatus(requestId);

      if (status.status === 'completed') {
        const result = await this.getAnalysisResult(requestId);
        if ('analysisStatus' in result) {
          return result;
        }
        throw new Error('分析结果格式异常');
      }

      if (status.status === 'failed') {
        throw new Error(`分析失败: ${status.errorMessage}`);
      }

      // 等待下一次轮询
      await new Promise(resolve => setTimeout(resolve, intervalMs));
    }

    throw new Error('分析超时');
  }

  /**
   * 分析文本内容
   */
  async analyzeText(
    text: string,
    options?: {
      sessionId?: string;
      userId?: number;
      messageId?: number;
      priority?: number;
    }
  ): Promise<string> {
    const response = await this.analyze({
      contentType: 'text',
      textContent: text,
      ...options,
    });
    return response.requestId;
  }

  /**
   * 分析图像内容
   */
  async analyzeImage(
    imageUrl: string,
    options?: {
      sessionId?: string;
      userId?: number;
      messageId?: number;
      priority?: number;
    }
  ): Promise<string> {
    const response = await this.analyze({
      contentType: 'image',
      imageUrl,
      ...options,
    });
    return response.requestId;
  }

  /**
   * 分析音频内容
   */
  async analyzeAudio(
    audioUrl: string,
    options?: {
      sessionId?: string;
      userId?: number;
      messageId?: number;
      priority?: number;
    }
  ): Promise<string> {
    const response = await this.analyze({
      contentType: 'audio',
      audioUrl,
      ...options,
    });
    return response.requestId;
  }

  /**
   * 分析视频内容
   */
  async analyzeVideo(
    videoUrl: string,
    options?: {
      sessionId?: string;
      userId?: number;
      messageId?: number;
      priority?: number;
    }
  ): Promise<string> {
    const response = await this.analyze({
      contentType: 'video',
      videoUrl,
      ...options,
    });
    return response.requestId;
  }

  /**
   * 分析多模态内容
   */
  async analyzeMultimodal(
    content: {
      text?: string;
      imageUrl?: string;
      audioUrl?: string;
      videoUrl?: string;
    },
    options?: {
      sessionId?: string;
      userId?: number;
      messageId?: number;
      priority?: number;
      businessContext?: string;
    }
  ): Promise<string> {
    const response = await this.analyze({
      contentType: 'mixed',
      ...content,
      ...options,
    });
    return response.requestId;
  }
}

// 导出单例
export const multimodalService = new MultimodalService();
export default multimodalService;