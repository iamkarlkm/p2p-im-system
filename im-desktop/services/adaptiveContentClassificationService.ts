import axios from 'axios';
import {
  AdaptiveContentClassificationConfig,
  ContentClassificationResult,
  CreateClassificationConfigRequest,
  UpdateClassificationConfigRequest,
  ClassifyContentRequest,
  BatchClassifyRequest,
  PageResponse,
  ApiResponse,
  ConfigStats,
  ClassificationStats,
  ClassificationTrend
} from './adaptiveContentClassificationTypes';

const API_BASE_URL = '/api/v1/adaptive-content-classification';

/**
 * 自适应内容分类服务
 * 提供分类配置管理和内容分类的 API 调用
 */
class AdaptiveContentClassificationService {
  
  private axiosInstance = axios.create({
    baseURL: API_BASE_URL,
    timeout: 30000,
    headers: {
      'Content-Type': 'application/json'
    }
  });

  // ========== 配置管理 API ==========

  /**
   * 创建分类配置
   */
  async createConfig(config: CreateClassificationConfigRequest): Promise<AdaptiveContentClassificationConfig> {
    const response = await this.axiosInstance.post<ApiResponse<AdaptiveContentClassificationConfig>>(
      '/configs',
      config
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 获取配置详情
   */
  async getConfig(configId: number): Promise<AdaptiveContentClassificationConfig> {
    const response = await this.axiosInstance.get<ApiResponse<AdaptiveContentClassificationConfig>>(
      `/configs/${configId}`
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 更新分类配置
   */
  async updateConfig(
    configId: number,
    updatedConfig: UpdateClassificationConfigRequest
  ): Promise<AdaptiveContentClassificationConfig> {
    const response = await this.axiosInstance.put<ApiResponse<AdaptiveContentClassificationConfig>>(
      `/configs/${configId}`,
      updatedConfig
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 删除分类配置
   */
  async deleteConfig(configId: number): Promise<void> {
    const response = await this.axiosInstance.delete<ApiResponse<void>>(
      `/configs/${configId}`
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
  }

  /**
   * 查询用户配置列表
   */
  async getUserConfigs(userId: number): Promise<AdaptiveContentClassificationConfig[]> {
    const response = await this.axiosInstance.get<ApiResponse<AdaptiveContentClassificationConfig[]>>(
      `/configs/user/${userId}`
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 分页查询用户配置
   */
  async getUserConfigsPage(
    userId: number,
    page: number = 0,
    size: number = 20,
    sort: string = 'createdAt'
  ): Promise<PageResponse<AdaptiveContentClassificationConfig>> {
    const response = await this.axiosInstance.get<ApiResponse<PageResponse<AdaptiveContentClassificationConfig>>>(
      `/configs/user/${userId}/page`,
      {
        params: { page, size, sort }
      }
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 搜索配置
   */
  async searchConfigs(keyword: string): Promise<AdaptiveContentClassificationConfig[]> {
    const response = await this.axiosInstance.get<ApiResponse<AdaptiveContentClassificationConfig[]>>(
      '/configs/search',
      {
        params: { keyword }
      }
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 获取配置统计信息
   */
  async getConfigStats(configId: number): Promise<ConfigStats> {
    const response = await this.axiosInstance.get<ApiResponse<ConfigStats>>(
      `/configs/${configId}/stats`
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 获取用户配置统计
   */
  async getUserConfigStats(userId: number): Promise<ConfigStats> {
    const response = await this.axiosInstance.get<ApiResponse<ConfigStats>>(
      `/configs/user/${userId}/stats`
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  // ========== 内容分类 API ==========

  /**
   * 分类单个内容
   */
  async classifyContent(
    configId: number,
    contentData: ClassifyContentRequest
  ): Promise<ContentClassificationResult> {
    const response = await this.axiosInstance.post<ApiResponse<ContentClassificationResult>>(
      `/configs/${configId}/classify`,
      contentData
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 批量分类内容
   */
  async batchClassifyContent(
    configId: number,
    contents: ClassifyContentRequest[]
  ): Promise<ContentClassificationResult[]> {
    const response = await this.axiosInstance.post<ApiResponse<ContentClassificationResult[]>>(
      `/configs/${configId}/batch-classify`,
      { contents }
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 获取分类结果
   */
  async getClassificationResult(resultId: number): Promise<ContentClassificationResult> {
    const response = await this.axiosInstance.get<ApiResponse<ContentClassificationResult>>(
      `/results/${resultId}`
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 查询配置的分类结果
   */
  async getConfigResults(configId: number): Promise<ContentClassificationResult[]> {
    const response = await this.axiosInstance.get<ApiResponse<ContentClassificationResult[]>>(
      `/configs/${configId}/results`
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 分页查询配置的分类结果
   */
  async getConfigResultsPage(
    configId: number,
    page: number = 0,
    size: number = 50,
    sort: string = 'createdAt'
  ): Promise<PageResponse<ContentClassificationResult>> {
    const response = await this.axiosInstance.get<ApiResponse<PageResponse<ContentClassificationResult>>>(
      `/configs/${configId}/results/page`,
      {
        params: { page, size, sort }
      }
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 查询高置信度结果
   */
  async getHighConfidenceResults(
    configId: number,
    minConfidence: number = 80
  ): Promise<ContentClassificationResult[]> {
    const response = await this.axiosInstance.get<ApiResponse<ContentClassificationResult[]>>(
      `/configs/${configId}/results/high-confidence`,
      {
        params: { minConfidence }
      }
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 查询低置信度结果
   */
  async getLowConfidenceResults(
    configId: number,
    maxConfidence: number = 60
  ): Promise<ContentClassificationResult[]> {
    const response = await this.axiosInstance.get<ApiResponse<ContentClassificationResult[]>>(
      `/configs/${configId}/results/low-confidence`,
      {
        params: { maxConfidence }
      }
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 搜索分类结果
   */
  async searchResults(
    configId: number,
    keyword: string
  ): Promise<ContentClassificationResult[]> {
    const response = await this.axiosInstance.get<ApiResponse<ContentClassificationResult[]>>(
      `/configs/${configId}/results/search`,
      {
        params: { keyword }
      }
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 获取分类统计信息
   */
  async getClassificationStats(configId: number): Promise<ClassificationStats> {
    const response = await this.axiosInstance.get<ApiResponse<ClassificationStats>>(
      `/configs/${configId}/classification-stats`
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 获取分类趋势分析
   */
  async getClassificationTrend(
    configId: number,
    days: number = 30
  ): Promise<ClassificationTrend> {
    const response = await this.axiosInstance.get<ApiResponse<ClassificationTrend>>(
      `/configs/${configId}/trend`,
      {
        params: { days }
      }
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  // ========== 增量学习 API ==========

  /**
   * 执行增量学习
   */
  async performIncrementalLearning(configId: number): Promise<void> {
    const response = await this.axiosInstance.post<ApiResponse<void>>(
      `/configs/${configId}/incremental-learning`
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
  }

  /**
   * 批量执行增量学习
   */
  async batchIncrementalLearning(configIds: number[]): Promise<void> {
    const response = await this.axiosInstance.post<ApiResponse<void>>(
      '/configs/batch-incremental-learning',
      { configIds }
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
  }

  /**
   * 自动执行增量学习
   */
  async autoIncrementalLearning(): Promise<void> {
    const response = await this.axiosInstance.post<ApiResponse<void>>(
      '/configs/auto-incremental-learning'
    );
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
  }

  // ========== 系统管理 API ==========

  /**
   * 系统健康检查
   */
  async healthCheck(): Promise<{ status: string; version: string; timestamp: number }> {
    const response = await this.axiosInstance.get<ApiResponse<any>>('/health');
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }

  /**
   * 获取系统统计
   */
  async getSystemStats(): Promise<Record<string, any>> {
    const response = await this.axiosInstance.get<ApiResponse<Record<string, any>>>('/system-stats');
    
    if (!response.data.success) {
      throw new Error(response.data.message);
    }
    
    return response.data.data;
  }
}

// 导出单例
export const adaptiveContentClassificationService = new AdaptiveContentClassificationService();

// 导出类以便测试
export default AdaptiveContentClassificationService;