/**
 * AI 前端框架服务 - TypeScript 实现
 * 提供本地 AI 推理引擎的管理、配置和调用功能
 */

import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { 
  AiFrameworkConfig, 
  AiModelInfo, 
  InferenceStats, 
  FeatureStatus,
  PerformanceLevel,
  PrivacyMode,
  ModelEngineType,
  InferenceBackendType
} from './types/aiFrontendFramework';

// API 基础配置
const API_BASE_URL = process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080/api';
const API_TIMEOUT = 30000; // 30秒超时

/**
 * AI 前端框架服务类
 */
class AiFrontendFrameworkService {
  private client: AxiosInstance;
  private userId: number | null = null;
  private deviceId: string | null = null;

  constructor() {
    this.client = axios.create({
      baseURL: API_BASE_URL,
      timeout: API_TIMEOUT,
      headers: {
        'Content-Type': 'application/json',
        'Accept': 'application/json'
      }
    });

    // 请求拦截器
    this.client.interceptors.request.use(
      (config) => {
        // 添加认证头
        const token = localStorage.getItem('auth_token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // 响应拦截器
    this.client.interceptors.response.use(
      (response) => response,
      (error) => {
        console.error('AI Framework API Error:', error);
        return Promise.reject(error);
      }
    );
  }

  /**
   * 设置当前用户和设备
   */
  setCurrentUser(userId: number, deviceId: string): void {
    this.userId = userId;
    this.deviceId = deviceId;
  }

  /**
   * 创建 AI 前端框架配置
   */
  async createFramework(frameworkVersion: string): Promise<AiFrameworkConfig> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    const response: AxiosResponse<AiFrameworkConfig> = await this.client.post(
      '/ai-frontend-framework',
      null,
      {
        params: {
          userId: this.userId,
          deviceId: this.deviceId,
          frameworkVersion
        }
      }
    );

    return response.data;
  }

  /**
   * 获取当前框架配置
   */
  async getFramework(): Promise<AiFrameworkConfig | null> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    try {
      const response: AxiosResponse<AiFrameworkConfig> = await this.client.get(
        `/ai-frontend-framework/user/${this.userId}/device/${this.deviceId}`
      );
      return response.data;
    } catch (error: any) {
      if (error.response?.status === 404) {
        return null; // 配置不存在
      }
      throw error;
    }
  }

  /**
   * 更新框架配置
   */
  async updateFramework(updates: Partial<AiFrameworkConfig>): Promise<AiFrameworkConfig> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    const response: AxiosResponse<AiFrameworkConfig> = await this.client.put(
      `/ai-frontend-framework/user/${this.userId}/device/${this.deviceId}`,
      updates
    );

    return response.data;
  }

  /**
   * 配置 AI 模型
   */
  async configureModel(
    modelName: string,
    modelVersion: string,
    localModelEngine: ModelEngineType,
    inferenceBackend: InferenceBackendType
  ): Promise<AiFrameworkConfig> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    const response: AxiosResponse<AiFrameworkConfig> = await this.client.post(
      '/ai-frontend-framework/configure-model',
      null,
      {
        params: {
          userId: this.userId,
          deviceId: this.deviceId,
          modelName,
          modelVersion,
          localModelEngine,
          inferenceBackend
        }
      }
    );

    return response.data;
  }

  /**
   * 标记模型已加载
   */
  async markModelLoaded(modelSizeMb: number): Promise<AiFrameworkConfig> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    const response: AxiosResponse<AiFrameworkConfig> = await this.client.post(
      '/ai-frontend-framework/mark-model-loaded',
      null,
      {
        params: {
          userId: this.userId,
          deviceId: this.deviceId,
          modelSizeMb
        }
      }
    );

    return response.data;
  }

  /**
   * 标记模型未加载
   */
  async markModelUnloaded(): Promise<AiFrameworkConfig> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    const response: AxiosResponse<AiFrameworkConfig> = await this.client.post(
      '/ai-frontend-framework/mark-model-unloaded',
      null,
      {
        params: {
          userId: this.userId,
          deviceId: this.deviceId
        }
      }
    );

    return response.data;
  }

  /**
   * 启用/禁用功能
   */
  async enableFeature(feature: keyof FeatureStatus, enable: boolean): Promise<AiFrameworkConfig> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    const response: AxiosResponse<AiFrameworkConfig> = await this.client.post(
      '/ai-frontend-framework/enable-feature',
      null,
      {
        params: {
          userId: this.userId,
          deviceId: this.deviceId,
          feature,
          enable
        }
      }
    );

    return response.data;
  }

  /**
   * 设置性能级别
   */
  async setPerformanceLevel(performanceLevel: PerformanceLevel): Promise<AiFrameworkConfig> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    const response: AxiosResponse<AiFrameworkConfig> = await this.client.post(
      '/ai-frontend-framework/set-performance-level',
      null,
      {
        params: {
          userId: this.userId,
          deviceId: this.deviceId,
          performanceLevel
        }
      }
    );

    return response.data;
  }

  /**
   * 设置隐私模式
   */
  async setPrivacyMode(privacyMode: boolean): Promise<AiFrameworkConfig> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    const response: AxiosResponse<AiFrameworkConfig> = await this.client.post(
      '/ai-frontend-framework/set-privacy-mode',
      null,
      {
        params: {
          userId: this.userId,
          deviceId: this.deviceId,
          privacyMode
        }
      }
    );

    return response.data;
  }

  /**
   * 设置离线模式
   */
  async setOfflineMode(offlineMode: boolean): Promise<AiFrameworkConfig> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    const response: AxiosResponse<AiFrameworkConfig> = await this.client.post(
      '/ai-frontend-framework/set-offline-mode',
      null,
      {
        params: {
          userId: this.userId,
          deviceId: this.deviceId,
          offlineMode
        }
      }
    );

    return response.data;
  }

  /**
   * 记录推理统计
   */
  async recordInference(success: boolean, latencyMs: number): Promise<AiFrameworkConfig> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    const response: AxiosResponse<AiFrameworkConfig> = await this.client.post(
      '/ai-frontend-framework/record-inference',
      null,
      {
        params: {
          userId: this.userId,
          deviceId: this.deviceId,
          success,
          latencyMs
        }
      }
    );

    return response.data;
  }

  /**
   * 重置推理统计
   */
  async resetInferenceStats(): Promise<AiFrameworkConfig> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    const response: AxiosResponse<AiFrameworkConfig> = await this.client.post(
      '/ai-frontend-framework/reset-inference-stats',
      null,
      {
        params: {
          userId: this.userId,
          deviceId: this.deviceId
        }
      }
    );

    return response.data;
  }

  /**
   * 获取框架状态
   */
  async getFrameworkStatus(): Promise<any> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    const response: AxiosResponse = await this.client.get(
      `/ai-frontend-framework/status/${this.userId}/${this.deviceId}`
    );

    return response.data;
  }

  /**
   * 验证框架配置
   */
  async validateConfiguration(): Promise<boolean> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    try {
      const response: AxiosResponse<{ valid: boolean }> = await this.client.get(
        `/ai-frontend-framework/validate/${this.userId}/${this.deviceId}`
      );
      return response.data.valid;
    } catch (error) {
      console.error('Validation failed:', error);
      return false;
    }
  }

  /**
   * 检查功能是否启用
   */
  async isFeatureEnabled(feature: keyof FeatureStatus): Promise<boolean> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    try {
      const response: AxiosResponse<{ enabled: boolean }> = await this.client.get(
        `/ai-frontend-framework/feature-enabled/${this.userId}/${this.deviceId}/${feature}`
      );
      return response.data.enabled;
    } catch (error) {
      console.error(`Feature check failed for ${feature}:`, error);
      return false;
    }
  }

  /**
   * 检查隐私模式是否启用
   */
  async isPrivacyModeEnabled(): Promise<boolean> {
    if (!this.userId || !this.deviceId) {
      throw new Error('User ID and Device ID must be set');
    }

    try {
      const response: AxiosResponse<{ enabled: boolean }> = await this.client.get(
        `/ai-frontend-framework/privacy-mode/${this.userId}/${this.deviceId}`
      );
      return response.data.enabled;
    } catch (error) {
      console.error('Privacy mode check failed:', error);
      return true; // 默认启用隐私模式
    }
  }

  /**
   * 获取系统统计信息
   */
  async getSystemStatistics(): Promise<any> {
    try {
      const response: AxiosResponse = await this.client.get('/ai-frontend-framework/statistics');
      return response.data;
    } catch (error) {
      console.error('Failed to get system statistics:', error);
      return null;
    }
  }

  /**
   * 健康检查
   */
  async healthCheck(): Promise<any> {
    try {
      const response: AxiosResponse = await this.client.get('/ai-frontend-framework/health');
      return response.data;
    } catch (error) {
      console.error('Health check failed:', error);
      return { status: 'DOWN', error: error.message };
    }
  }

  /**
   * 批量更新配置
   */
  async batchUpdate(ids: number[], updates: Partial<AiFrameworkConfig>): Promise<AiFrameworkConfig[]> {
    const response: AxiosResponse<AiFrameworkConfig[]> = await this.client.put(
      '/ai-frontend-framework/batch-update',
      updates,
      {
        params: { ids: ids.join(',') }
      }
    );

    return response.data;
  }

  /**
   * 批量启用功能
   */
  async batchEnableFeature(userIds: number[], feature: keyof FeatureStatus, enable: boolean): Promise<number> {
    const response: AxiosResponse<{ updatedCount: number }> = await this.client.post(
      '/ai-frontend-framework/batch-enable-feature',
      null,
      {
        params: {
          userIds: userIds.join(','),
          feature,
          enable
        }
      }
    );

    return response.data.updatedCount;
  }

  /**
   * 搜索框架配置
   */
  async searchFrameworks(keyword: string, page: number = 0, size: number = 20): Promise<any> {
    const response: AxiosResponse = await this.client.get('/ai-frontend-framework/search', {
      params: { keyword, page, size }
    });

    return response.data;
  }

  /**
   * 获取活动框架列表
   */
  async getActiveFrameworks(page: number = 0, size: number = 20): Promise<any> {
    const response: AxiosResponse = await this.client.get('/ai-frontend-framework/active', {
      params: { page, size }
    });

    return response.data;
  }

  /**
   * 获取已加载模型列表
   */
  async getLoadedModels(page: number = 0, size: number = 20): Promise<any> {
    const response: AxiosResponse = await this.client.get('/ai-frontend-framework/loaded-models', {
      params: { page, size }
    });

    return response.data;
  }

  /**
   * 清理不活动的框架
   */
  async cleanupInactiveFrameworks(threshold: string): Promise<number> {
    const response: AxiosResponse<{ cleanedCount: number }> = await this.client.post(
      '/ai-frontend-framework/cleanup/inactive',
      null,
      {
        params: { threshold }
      }
    );

    return response.data.cleanedCount;
  }

  /**
   * 清理未加载的模型
   */
  async cleanupUnloadedModels(threshold: string): Promise<number> {
    const response: AxiosResponse<{ cleanedCount: number }> = await this.client.post(
      '/ai-frontend-framework/cleanup/unloaded-models',
      null,
      {
        params: { threshold }
      }
    );

    return response.data.cleanedCount;
  }

  /**
   * 导出配置为 CSV
   */
  async exportToCsv(userId?: number, page: number = 0, size: number = 1000): Promise<any> {
    const response: AxiosResponse = await this.client.get('/ai-frontend-framework/export/csv', {
      params: { userId, page, size }
    });

    return response.data;
  }

  /**
   * 导出配置为 JSON
   */
  async exportToJson(userId?: number, page: number = 0, size: number = 1000): Promise<any> {
    const response: AxiosResponse = await this.client.get('/ai-frontend-framework/export/json', {
      params: { userId, page, size }
    });

    return response.data;
  }

  /**
   * 刷新系统缓存
   */
  async refreshSystemCache(): Promise<any> {
    const response: AxiosResponse = await this.client.post('/ai-frontend-framework/system/refresh-cache');
    return response.data;
  }

  /**
   * 重建数据库索引
   */
  async reindexDatabase(): Promise<any> {
    const response: AxiosResponse = await this.client.post('/ai-frontend-framework/system/reindex');
    return response.data;
  }

  /**
   * 获取推理后端分布
   */
  async getInferenceBackendDistribution(): Promise<any[]> {
    const response: AxiosResponse = await this.client.get('/ai-frontend-framework/statistics/inference-backend');
    return response.data;
  }

  /**
   * 获取模型引擎分布
   */
  async getModelEngineDistribution(): Promise<any[]> {
    const response: AxiosResponse = await this.client.get('/ai-frontend-framework/statistics/model-engine');
    return response.data;
  }

  /**
   * 获取性能级别分布
   */
  async getPerformanceLevelDistribution(): Promise<any[]> {
    const response: AxiosResponse = await this.client.get('/ai-frontend-framework/statistics/performance-level');
    return response.data;
  }

  /**
   * 获取模型使用统计
   */
  async getModelUsageStatistics(): Promise<any[]> {
    const response: AxiosResponse = await this.client.get('/ai-frontend-framework/statistics/model-usage');
    return response.data;
  }

  /**
   * 获取设备分布
   */
  async getDeviceDistribution(): Promise<any[]> {
    const response: AxiosResponse = await this.client.get('/ai-frontend-framework/statistics/device-distribution');
    return response.data;
  }
}

// 导出单例实例
const aiFrontendFrameworkService = new AiFrontendFrameworkService();
export default aiFrontendFrameworkService;