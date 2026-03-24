/**
 * 边缘视频处理服务
 * 提供与后端边缘计算 API 的交互能力
 */

import axios, { AxiosInstance, AxiosResponse } from 'axios';
import {
  EdgeVideoProcessingTask,
  CreateTaskRequest,
  ProcessingOptions,
  ApiResponse,
  TaskCreationResponse,
  SystemStatistics,
  SupportedCodecs,
  AiEnhancementOptions,
  BandwidthOptimizationOptions,
  SystemConfiguration,
  HealthCheckResponse,
  TaskListResponse,
  TaskOperationResponse,
  DEFAULT_PROCESSING_OPTIONS,
  MediaType,
  ProcessingStatus
} from '../types/edgeVideoProcessing';

// 服务配置
const SERVICE_CONFIG = {
  baseURL: '/api/v1/edge-video',
  timeout: 30000,
  retryAttempts: 3,
  retryDelay: 1000
};

/**
 * 边缘视频处理服务类
 */
export class EdgeVideoProcessingService {
  private apiClient: AxiosInstance;
  private taskPollingInterval: number = 2000;
  private activePollers: Map<string, NodeJS.Timeout> = new Map();

  constructor(baseURL?: string) {
    this.apiClient = axios.create({
      baseURL: baseURL || SERVICE_CONFIG.baseURL,
      timeout: SERVICE_CONFIG.timeout,
      headers: {
        'Content-Type': 'application/json'
      }
    });

    this.setupInterceptors();
  }

  /**
   * 设置请求/响应拦截器
   */
  private setupInterceptors() {
    // 请求拦截器
    this.apiClient.interceptors.request.use(
      (config) => {
        const token = localStorage.getItem('access_token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // 响应拦截器
    this.apiClient.interceptors.response.use(
      (response) => response,
      (error) => {
        if (error.response?.status === 401) {
          // Token 过期，重新登录
          window.location.href = '/login';
        }
        return Promise.reject(error);
      }
    );
  }

  /**
   * 创建视频处理任务
   */
  async createTask(request: CreateTaskRequest): Promise<TaskCreationResponse> {
    try {
      const response: AxiosResponse<ApiResponse<TaskCreationResponse>> = 
        await this.apiClient.post('/tasks', request);
      
      if (response.data.success) {
        return response.data.data!;
      } else {
        throw new Error(response.data.error || 'Failed to create task');
      }
    } catch (error) {
      console.error('Error creating task:', error);
      throw error;
    }
  }

  /**
   * 获取任务状态
   */
  async getTaskStatus(taskId: string): Promise<EdgeVideoProcessingTask> {
    try {
      const response: AxiosResponse<ApiResponse<EdgeVideoProcessingTask>> = 
        await this.apiClient.get(`/tasks/${taskId}/status`);
      
      if (response.data.success) {
        return response.data.data!;
      } else {
        throw new Error(response.data.error || 'Task not found');
      }
    } catch (error) {
      console.error('Error getting task status:', error);
      throw error;
    }
  }

  /**
   * 获取用户的所有任务
   */
  async getUserTasks(userId: string): Promise<EdgeVideoProcessingTask[]> {
    try {
      const response: AxiosResponse<ApiResponse<TaskListResponse>> = 
        await this.apiClient.get(`/users/${userId}/tasks`);
      
      if (response.data.success) {
        return response.data.data?.tasks || [];
      } else {
        throw new Error(response.data.error || 'Failed to get tasks');
      }
    } catch (error) {
      console.error('Error getting user tasks:', error);
      throw error;
    }
  }

  /**
   * 获取会话的所有任务
   */
  async getSessionTasks(sessionId: string): Promise<EdgeVideoProcessingTask[]> {
    try {
      const response: AxiosResponse<ApiResponse<TaskListResponse>> = 
        await this.apiClient.get(`/sessions/${sessionId}/tasks`);
      
      if (response.data.success) {
        return response.data.data?.tasks || [];
      } else {
        throw new Error(response.data.error || 'Failed to get tasks');
      }
    } catch (error) {
      console.error('Error getting session tasks:', error);
      throw error;
    }
  }

  /**
   * 取消任务
   */
  async cancelTask(taskId: string): Promise<boolean> {
    try {
      const response: AxiosResponse<TaskOperationResponse> = 
        await this.apiClient.post(`/tasks/${taskId}/cancel`);
      
      return response.data.success;
    } catch (error) {
      console.error('Error cancelling task:', error);
      return false;
    }
  }

  /**
   * 暂停任务
   */
  async pauseTask(taskId: string): Promise<boolean> {
    try {
      const response: AxiosResponse<TaskOperationResponse> = 
        await this.apiClient.post(`/tasks/${taskId}/pause`);
      
      return response.data.success;
    } catch (error) {
      console.error('Error pausing task:', error);
      return false;
    }
  }

  /**
   * 恢复任务
   */
  async resumeTask(taskId: string): Promise<boolean> {
    try {
      const response: AxiosResponse<TaskOperationResponse> = 
        await this.apiClient.post(`/tasks/${taskId}/resume`);
      
      return response.data.success;
    } catch (error) {
      console.error('Error resuming task:', error);
      return false;
    }
  }

  /**
   * 轮询任务状态直到完成
   */
  pollTaskStatus(
    taskId: string,
    onProgress: (task: EdgeVideoProcessingTask) => void,
    onComplete: (task: EdgeVideoProcessingTask) => void,
    onError?: (error: Error) => void
  ): () => void {
    const poll = async () => {
      try {
        const task = await this.getTaskStatus(taskId);
        onProgress(task);

        if (
          task.processingStatus === ProcessingStatus.COMPLETED ||
          task.processingStatus === ProcessingStatus.FAILED ||
          task.processingStatus === ProcessingStatus.CANCELLED
        ) {
          this.stopPolling(taskId);
          onComplete(task);
        }
      } catch (error) {
        this.stopPolling(taskId);
        if (onError) {
          onError(error as Error);
        }
      }
    };

    // 立即执行一次
    poll();

    // 设置轮询
    const intervalId = setInterval(poll, this.taskPollingInterval);
    this.activePollers.set(taskId, intervalId);

    // 返回停止轮询的函数
    return () => this.stopPolling(taskId);
  }

  /**
   * 停止轮询
   */
  private stopPolling(taskId: string) {
    const intervalId = this.activePollers.get(taskId);
    if (intervalId) {
      clearInterval(intervalId);
      this.activePollers.delete(taskId);
    }
  }

  /**
   * 获取系统统计信息
   */
  async getSystemStatistics(): Promise<SystemStatistics> {
    try {
      const response: AxiosResponse<ApiResponse<SystemStatistics>> = 
        await this.apiClient.get('/statistics');
      
      if (response.data.success) {
        return response.data.data!;
      } else {
        throw new Error(response.data.error || 'Failed to get statistics');
      }
    } catch (error) {
      console.error('Error getting statistics:', error);
      throw error;
    }
  }

  /**
   * 获取支持的编解码器
   */
  async getSupportedCodecs(): Promise<SupportedCodecs> {
    try {
      const response: AxiosResponse<ApiResponse<SupportedCodecs>> = 
        await this.apiClient.get('/supported-codecs');
      
      if (response.data.success) {
        return response.data.data!;
      } else {
        throw new Error(response.data.error || 'Failed to get codecs');
      }
    } catch (error) {
      console.error('Error getting codecs:', error);
      throw error;
    }
  }

  /**
   * 获取 AI 增强选项
   */
  async getAiEnhancements(): Promise<AiEnhancementOptions> {
    try {
      const response: AxiosResponse<ApiResponse<AiEnhancementOptions>> = 
        await this.apiClient.get('/ai-enhancements');
      
      if (response.data.success) {
        return response.data.data!;
      } else {
        throw new Error(response.data.error || 'Failed to get enhancements');
      }
    } catch (error) {
      console.error('Error getting enhancements:', error);
      throw error;
    }
  }

  /**
   * 获取带宽优化选项
   */
  async getBandwidthOptimizationOptions(): Promise<BandwidthOptimizationOptions> {
    try {
      const response: AxiosResponse<ApiResponse<BandwidthOptimizationOptions>> = 
        await this.apiClient.get('/bandwidth-optimization');
      
      if (response.data.success) {
        return response.data.data!;
      } else {
        throw new Error(response.data.error || 'Failed to get optimization options');
      }
    } catch (error) {
      console.error('Error getting optimization options:', error);
      throw error;
    }
  }

  /**
   * 获取系统配置
   */
  async getConfiguration(): Promise<SystemConfiguration> {
    try {
      const response: AxiosResponse<ApiResponse<SystemConfiguration>> = 
        await this.apiClient.get('/configuration');
      
      if (response.data.success) {
        return response.data.data!;
      } else {
        throw new Error(response.data.error || 'Failed to get configuration');
      }
    } catch (error) {
      console.error('Error getting configuration:', error);
      throw error;
    }
  }

  /**
   * 健康检查
   */
  async healthCheck(): Promise<HealthCheckResponse> {
    try {
      const response: AxiosResponse<ApiResponse<HealthCheckResponse>> = 
        await this.apiClient.get('/health');
      
      if (response.data.success) {
        return response.data.data!;
      } else {
        throw new Error(response.data.error || 'Health check failed');
      }
    } catch (error) {
      console.error('Health check failed:', error);
      throw error;
    }
  }

  /**
   * 清理过期任务
   */
  async cleanupExpiredTasks(daysToKeep: number = 30): Promise<boolean> {
    try {
      const response: AxiosResponse<ApiResponse> = 
        await this.apiClient.post('/cleanup', null, {
          params: { daysToKeep }
        });
      
      return response.data.success;
    } catch (error) {
      console.error('Error cleaning up tasks:', error);
      return false;
    }
  }

  /**
   * 测试节点连接
   */
  async testNodeConnection(nodeId: string): Promise<{
    nodeId: string;
    status: string;
    responseTimeMs: number;
    timestamp: number;
  }> {
    try {
      const response: AxiosResponse<ApiResponse> = 
        await this.apiClient.post(`/nodes/${nodeId}/test-connection`);
      
      if (response.data.success) {
        return response.data.data as any;
      } else {
        throw new Error(response.data.error || 'Connection test failed');
      }
    } catch (error) {
      console.error('Error testing node connection:', error);
      throw error;
    }
  }

  /**
   * 创建快速处理任务（使用默认选项）
   */
  async createQuickTask(
    sessionId: string,
    userId: string,
    mediaType: MediaType,
    inputSource: string
  ): Promise<TaskCreationResponse> {
    return this.createTask({
      sessionId,
      userId,
      mediaType,
      inputSource,
      processingOptions: DEFAULT_PROCESSING_OPTIONS
    });
  }

  /**
   * 创建高清处理任务
   */
  async createHDTtask(
    sessionId: string,
    userId: string,
    inputSource: string,
    enableAI: boolean = false
  ): Promise<TaskCreationResponse> {
    return this.createTask({
      sessionId,
      userId,
      mediaType: MediaType.VIDEO_WITH_AUDIO,
      inputSource,
      processingOptions: {
        videoCodec: 'H.264/AVC',
        audioCodec: 'AAC',
        resolutionWidth: 1920,
        resolutionHeight: 1080,
        frameRate: 30,
        bitrateKbps: 5000,
        aiEnhancementsEnabled: enableAI,
        bandwidthOptimizationEnabled: true,
        compressionLevel: 6,
        priorityLevel: 7,
        maxRetries: 3
      }
    });
  }

  /**
   * 创建低带宽优化任务
   */
  async createLowBandwidthTask(
    sessionId: string,
    userId: string,
    inputSource: string
  ): Promise<TaskCreationResponse> {
    return this.createTask({
      sessionId,
      userId,
      mediaType: MediaType.VIDEO_WITH_AUDIO,
      inputSource,
      processingOptions: {
        videoCodec: 'H.264/AVC',
        audioCodec: 'Opus',
        resolutionWidth: 640,
        resolutionHeight: 360,
        frameRate: 15,
        bitrateKbps: 500,
        aiEnhancementsEnabled: false,
        bandwidthOptimizationEnabled: true,
        compressionLevel: 8,
        priorityLevel: 5,
        maxRetries: 3
      }
    });
  }

  /**
   * 设置轮询间隔
   */
  setPollingInterval(intervalMs: number) {
    this.taskPollingInterval = intervalMs;
  }

  /**
   * 停止所有活跃轮询
   */
  stopAllPolling() {
    this.activePollers.forEach((intervalId, taskId) => {
      clearInterval(intervalId);
    });
    this.activePollers.clear();
  }
}

// 导出单例实例
export const edgeVideoProcessingService = new EdgeVideoProcessingService();

// 默认导出
export default edgeVideoProcessingService;
