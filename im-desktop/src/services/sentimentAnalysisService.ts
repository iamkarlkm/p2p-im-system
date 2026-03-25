// TypeScript API 服务 - 情感分析系统
// 基于深度学习的情感分析系统客户端 API

import axios, { AxiosInstance, AxiosResponse } from 'axios';

// 情感分析结果接口
export interface SentimentAnalysisResult {
  id: number;
  messageId: number;
  conversationId: number;
  senderId: number;
  analysisTime: string;
  primaryEmotion: string;
  secondaryEmotion: string;
  sentimentIntensity: number;
  emergencyFlag: boolean;
  emergencyReason?: string;
  confidenceScore: number;
  multimodalFusionScore?: number;
  textEmotion: string;
  audioEmotion?: string;
  visualEmotion?: string;
  baselineDeviation?: number;
  modelVersion: string;
  processingLatencyMs: number;
  createdAt: string;
  updatedAt: string;
}

// 情感分析请求接口
export interface SentimentAnalysisRequest {
  messageId: number;
  conversationId: number;
  senderId: number;
  messageText: string;
  context?: Record<string, any>;
}

// 批量分析请求接口
export interface BatchAnalysisRequest {
  messages: Array<{
    messageId: number;
    conversationId: number;
    senderId: number;
    messageText: string;
    context?: Record<string, any>;
  }>;
}

// 情感趋势分析结果接口
export interface SentimentTrendAnalysis {
  emotionTrends: Record<string, Array<{
    date: string;
    count: number;
    avgIntensity: number;
  }>>;
  timeRange: {
    start: string;
    end: string;
  };
}

// 用户情感基线接口
export interface UserSentimentBaseline {
  userId: number;
  avgIntensity: number;
  stdDev: number;
  sampleCount: number;
  timeRange: {
    start: string;
    end: string;
  };
  hasData: boolean;
}

// 情感异常用户接口
export interface EmotionalAnomalyUser {
  userId: number;
  avgIntensity: number;
  messageCount: number;
  detectedTime: string;
}

// 统计信息接口
export interface SentimentStatistics {
  timeRange: {
    start: string;
    end: string;
  };
  totalMessages: number;
  avgIntensity: number;
  minIntensity: number;
  maxIntensity: number;
  emotionDistribution: Record<string, number>;
  modelPerformance: Array<{
    version: string;
    count: number;
    avgLatency: number;
    avgConfidence: number;
  }>;
  emergencyStatistics: Record<string, number>;
}

// 分页响应接口
export interface PaginatedResponse<T> {
  success: boolean;
  pageNumber: number;
  pageSize: number;
  totalPages: number;
  totalElements: number;
  analyses: T[];
}

// 通用API响应接口
export interface ApiResponse<T = any> {
  success: boolean;
  data?: T;
  error?: string;
  message?: string;
}

/**
 * 情感分析服务类
 * 提供基于深度学习的情感分析系统客户端 API
 */
class SentimentAnalysisService {
  private client: AxiosInstance;
  private baseURL: string;

  constructor(baseURL: string = 'http://localhost:8080/api/v1/sentiment') {
    this.baseURL = baseURL;
    this.client = axios.create({
      baseURL: this.baseURL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json',
      },
    });
  }

  /**
   * 分析单条消息的情感
   */
  async analyzeMessage(request: SentimentAnalysisRequest): Promise<ApiResponse<SentimentAnalysisResult>> {
    try {
      const response: AxiosResponse = await this.client.post('/analyze', null, {
        params: {
          messageId: request.messageId,
          conversationId: request.conversationId,
          senderId: request.senderId,
          messageText: request.messageText,
        },
        data: request.context,
      });

      if (response.data.success) {
        // 获取完整的分析结果
        const analysis = await this.getAnalysisByMessageId(request.messageId);
        return {
          success: true,
          data: analysis.data,
          message: '情感分析成功',
        };
      } else {
        return {
          success: false,
          error: response.data.error || '情感分析失败',
        };
      }
    } catch (error: any) {
      console.error('情感分析失败:', error);
      return {
        success: false,
        error: error.response?.data?.error || error.message || '网络错误',
      };
    }
  }

  /**
   * 批量分析消息情感
   */
  async batchAnalyzeMessages(request: BatchAnalysisRequest): Promise<ApiResponse<number[]>> {
    try {
      const response: AxiosResponse = await this.client.post('/batch-analyze', request.messages);

      if (response.data.success) {
        return {
          success: true,
          data: response.data.analysisIds,
          message: `成功分析了 ${response.data.analyzedCount} 条消息`,
        };
      } else {
        return {
          success: false,
          error: response.data.error || '批量分析失败',
        };
      }
    } catch (error: any) {
      console.error('批量情感分析失败:', error);
      return {
        success: false,
        error: error.response?.data?.error || error.message || '网络错误',
      };
    }
  }

  /**
   * 获取消息情感分析结果
   */
  async getAnalysisByMessageId(messageId: number): Promise<ApiResponse<SentimentAnalysisResult>> {
    try {
      const response: AxiosResponse = await this.client.get(`/message/${messageId}`);

      if (response.data.success && response.data.analysis) {
        return {
          success: true,
          data: response.data.analysis as SentimentAnalysisResult,
        };
      } else {
        return {
          success: false,
          error: response.data.error || '未找到分析结果',
        };
      }
    } catch (error: any) {
      console.error('获取情感分析结果失败:', error);
      return {
        success: false,
        error: error.response?.data?.error || error.message || '网络错误',
      };
    }
  }

  /**
   * 获取会话情感分析历史
   */
  async getConversationAnalysis(
    conversationId: number,
    page: number = 0,
    size: number = 20
  ): Promise<ApiResponse<PaginatedResponse<SentimentAnalysisResult>>> {
    try {
      const response: AxiosResponse = await this.client.get(`/conversation/${conversationId}`, {
        params: { page, size },
      });

      if (response.data.success) {
        return {
          success: true,
          data: response.data as PaginatedResponse<SentimentAnalysisResult>,
        };
      } else {
        return {
          success: false,
          error: response.data.error || '获取会话分析历史失败',
        };
      }
    } catch (error: any) {
      console.error('获取会话情感分析历史失败:', error);
      return {
        success: false,
        error: error.response?.data?.error || error.message || '网络错误',
      };
    }
  }

  /**
   * 获取用户情感分析历史
   */
  async getUserAnalysis(userId: number): Promise<ApiResponse<SentimentAnalysisResult[]>> {
    try {
      const response: AxiosResponse = await this.client.get(`/user/${userId}`);

      if (response.data.success) {
        return {
          success: true,
          data: response.data.analyses as SentimentAnalysisResult[],
          message: `找到 ${response.data.analysisCount} 条分析记录`,
        };
      } else {
        return {
          success: false,
          error: response.data.error || '获取用户分析历史失败',
        };
      }
    } catch (error: any) {
      console.error('获取用户情感分析历史失败:', error);
      return {
        success: false,
        error: error.response?.data?.error || error.message || '网络错误',
      };
    }
  }

  /**
   * 获取情感趋势分析
   */
  async getSentimentTrend(
    conversationId: number,
    startTime: string,
    endTime: string
  ): Promise<ApiResponse<SentimentTrendAnalysis>> {
    try {
      const response: AxiosResponse = await this.client.get(`/trend/${conversationId}`, {
        params: { startTime, endTime },
      });

      if (response.data.success) {
        return {
          success: true,
          data: response.data.trendAnalysis as SentimentTrendAnalysis,
        };
      } else {
        return {
          success: false,
          error: response.data.error || '获取情感趋势分析失败',
        };
      }
    } catch (error: any) {
      console.error('获取情感趋势分析失败:', error);
      return {
        success: false,
        error: error.response?.data?.error || error.message || '网络错误',
      };
    }
  }

  /**
   * 获取紧急情绪检测
   */
  async getEmergencyEmotions(): Promise<ApiResponse<SentimentAnalysisResult[]>> {
    try {
      const response: AxiosResponse = await this.client.get('/emergency');

      if (response.data.success) {
        return {
          success: true,
          data: response.data.emergencies as SentimentAnalysisResult[],
          message: `检测到 ${response.data.emergencyCount} 条紧急情绪`,
        };
      } else {
        return {
          success: false,
          error: response.data.error || '获取紧急情绪检测失败',
        };
      }
    } catch (error: any) {
      console.error('获取紧急情绪检测失败:', error);
      return {
        success: false,
        error: error.response?.data?.error || error.message || '网络错误',
      };
    }
  }

  /**
   * 获取用户情感基线
   */
  async getUserBaseline(
    userId: number,
    startTime?: string,
    endTime?: string
  ): Promise<ApiResponse<UserSentimentBaseline>> {
    try {
      const params: Record<string, string> = {};
      if (startTime) params.startTime = startTime;
      if (endTime) params.endTime = endTime;

      const response: AxiosResponse = await this.client.get(`/baseline/${userId}`, { params });

      if (response.data.success) {
        return {
          success: true,
          data: response.data.baseline as UserSentimentBaseline,
        };
      } else {
        return {
          success: false,
          error: response.data.error || '获取用户情感基线失败',
        };
      }
    } catch (error: any) {
      console.error('获取用户情感基线失败:', error);
      return {
        success: false,
        error: error.response?.data?.error || error.message || '网络错误',
      };
    }
  }

  /**
   * 查找情感异常用户
   */
  async findEmotionalAnomalies(
    recentTime?: string,
    lowThreshold: number = 0.2,
    highThreshold: number = 0.8
  ): Promise<ApiResponse<EmotionalAnomalyUser[]>> {
    try {
      const params: Record<string, any> = { lowThreshold, highThreshold };
      if (recentTime) params.recentTime = recentTime;

      const response: AxiosResponse = await this.client.get('/anomalies', { params });

      if (response.data.success) {
        return {
          success: true,
          data: response.data.anomalies as EmotionalAnomalyUser[],
          message: `找到 ${response.data.anomalyCount} 个情感异常用户`,
        };
      } else {
        return {
          success: false,
          error: response.data.error || '查找情感异常用户失败',
        };
      }
    } catch (error: any) {
      console.error('查找情感异常用户失败:', error);
      return {
        success: false,
        error: error.response?.data?.error || error.message || '网络错误',
      };
    }
  }

  /**
   * 获取情感统计信息
   */
  async getStatistics(
    startTime?: string,
    endTime?: string
  ): Promise<ApiResponse<SentimentStatistics>> {
    try {
      const params: Record<string, string> = {};
      if (startTime) params.startTime = startTime;
      if (endTime) params.endTime = endTime;

      const response: AxiosResponse = await this.client.get('/statistics', { params });

      if (response.data.success) {
        return {
          success: true,
          data: response.data.statistics as SentimentStatistics,
        };
      } else {
        return {
          success: false,
          error: response.data.error || '获取情感统计信息失败',
        };
      }
    } catch (error: any) {
      console.error('获取情感统计信息失败:', error);
      return {
        success: false,
        error: error.response?.data?.error || error.message || '网络错误',
      };
    }
  }

  /**
   * 清理旧记录
   */
  async cleanupOldRecords(cutoffTime: string): Promise<ApiResponse<number>> {
    try {
      const response: AxiosResponse = await this.client.delete('/cleanup', {
        params: { cutoffTime },
      });

      if (response.data.success) {
        return {
          success: true,
          data: response.data.deletedCount as number,
          message: response.data.message,
        };
      } else {
        return {
          success: false,
          error: response.data.error || '清理旧记录失败',
        };
      }
    } catch (error: any) {
      console.error('清理旧记录失败:', error);
      return {
        success: false,
        error: error.response?.data?.error || error.message || '网络错误',
      };
    }
  }

  /**
   * 高级搜索
   */
  async advancedSearch(
    filters: {
      conversationId?: number;
      senderId?: number;
      primaryEmotion?: string;
      emergencyFlag?: boolean;
      startTime: string;
      endTime: string;
    },
    page: number = 0,
    size: number = 20
  ): Promise<ApiResponse<PaginatedResponse<SentimentAnalysisResult>>> {
    try {
      const params = {
        ...filters,
        page,
        size,
      };

      const response: AxiosResponse = await this.client.get('/search', { params });

      if (response.data.success) {
        return {
          success: true,
          data: response.data as PaginatedResponse<SentimentAnalysisResult>,
        };
      } else {
        return {
          success: false,
          error: response.data.error || '高级搜索失败',
        };
      }
    } catch (error: any) {
      console.error('高级搜索失败:', error);
      return {
        success: false,
        error: error.response?.data?.error || error.message || '网络错误',
      };
    }
  }

  /**
   * 健康检查
   */
  async healthCheck(): Promise<ApiResponse<{
    status: string;
    timestamp: string;
    system: string;
    version: string;
    recentAnalyses: number;
    message: string;
  }>> {
    try {
      const response: AxiosResponse = await this.client.get('/health');

      if (response.data.status === 'UP') {
        return {
          success: true,
          data: response.data,
          message: '情感分析系统健康',
        };
      } else {
        return {
          success: false,
          error: response.data.error || '系统异常',
        };
      }
    } catch (error: any) {
      console.error('健康检查失败:', error);
      return {
        success: false,
        error: error.response?.data?.error || error.message || '网络错误',
      };
    }
  }

  /**
   * 实时情感分析 (流式处理)
   */
  async realTimeAnalysis(
    messageId: number,
    messageText: string,
    onProgress?: (progress: number) => void
  ): Promise<ApiResponse<SentimentAnalysisResult>> {
    try {
      // 模拟实时分析进度
      if (onProgress) {
        for (let i = 0; i <= 100; i += 10) {
          setTimeout(() => onProgress(i), i * 10);
        }
      }

      // 等待模拟分析完成
      await new Promise(resolve => setTimeout(resolve, 1000));

      // 调用分析API
      return await this.analyzeMessage({
        messageId,
        conversationId: 1, // 默认会话ID
        senderId: 1, // 默认发送者ID
        messageText,
      });
    } catch (error: any) {
      console.error('实时情感分析失败:', error);
      return {
        success: false,
        error: error.message || '实时分析失败',
      };
    }
  }

  /**
   * 情感可视化数据生成
   */
  generateVisualizationData(analysis: SentimentAnalysisResult): any {
    return {
      type: 'radar',
      data: {
        labels: ['情感强度', '置信度', '紧急程度', '基线偏差', '多模态融合'],
        datasets: [{
          label: '情感分析结果',
          data: [
            analysis.sentimentIntensity * 100,
            analysis.confidenceScore * 100,
            analysis.emergencyFlag ? 80 : 20,
            (analysis.baselineDeviation || 0) * 100 + 50,
            (analysis.multimodalFusionScore || 0.5) * 100,
          ],
          backgroundColor: 'rgba(54, 162, 235, 0.2)',
          borderColor: 'rgba(54, 162, 235, 1)',
          borderWidth: 2,
        }],
      },
      options: {
        scale: {
          ticks: {
            beginAtZero: true,
            max: 100,
          },
        },
      },
    };
  }

  /**
   * 情感预警检查
   */
  checkEmotionWarning(analysis: SentimentAnalysisResult): {
    level: 'normal' | 'warning' | 'danger';
    message: string;
    recommendations: string[];
  } {
    const warnings = [];

    if (analysis.emergencyFlag) {
      warnings.push('检测到紧急情绪');
    }

    if (analysis.sentimentIntensity > 0.8) {
      warnings.push('情感强度过高');
    }

    if (analysis.sentimentIntensity < 0.2) {
      warnings.push('情感强度过低');
    }

    if (analysis.baselineDeviation && Math.abs(analysis.baselineDeviation) > 0.3) {
      warnings.push('情感基线偏差过大');
    }

    let level: 'normal' | 'warning' | 'danger' = 'normal';
    if (warnings.length > 0) {
      level = analysis.emergencyFlag ? 'danger' : 'warning';
    }

    const recommendations = [];
    if (level === 'danger') {
      recommendations.push('立即关注此消息');
      recommendations.push('考虑联系相关人员');
    } else if (level === 'warning') {
      recommendations.push('持续监控情感变化');
      recommendations.push('适时介入沟通');
    }

    return {
      level,
      message: warnings.length > 0 ? warnings.join('; ') : '情感状态正常',
      recommendations,
    };
  }
}

// 导出单例实例
export const sentimentAnalysisService = new SentimentAnalysisService();

// 导出类型和工具函数
export * from './types/sentimentAnalysisTypes';

// 工具函数：格式化情感分析结果
export function formatSentimentResult(analysis: SentimentAnalysisResult): string {
  const intensityText = analysis.sentimentIntensity > 0.7 ? '强烈' :
                       analysis.sentimentIntensity > 0.4 ? '中等' :
                       analysis.sentimentIntensity > 0.2 ? '轻微' : '微弱';

  let result = `情感分析结果：
- 主要情感：${analysis.primaryEmotion}
- 次要情感：${analysis.secondaryEmotion}
- 情感强度：${intensityText} (${(analysis.sentimentIntensity * 100).toFixed(1)}%)
- 置信度：${(analysis.confidenceScore * 100).toFixed(1)}%
- 模型版本：${analysis.modelVersion}
- 分析耗时：${analysis.processingLatencyMs}ms`;

  if (analysis.emergencyFlag) {
    result += `\n⚠️ 紧急情绪警报：${analysis.emergencyReason}`;
  }

  if (analysis.baselineDeviation) {
    const deviationText = analysis.baselineDeviation > 0 ? '高于' : '低于';
    result += `\n📊 情感基线：${deviationText}基线 ${Math.abs(analysis.baselineDeviation * 100).toFixed(1)}%`;
  }

  return result;
}

// 工具函数：情感强度颜色映射
export function getSentimentColor(intensity: number): string {
  if (intensity > 0.8) return '#ff4444'; // 红色 - 强烈
  if (intensity > 0.6) return '#ff8844'; // 橙色 - 较强
  if (intensity > 0.4) return '#ffaa44'; // 黄色 - 中等
  if (intensity > 0.2) return '#aadd44'; // 黄绿色 - 轻微
  return '#44aa44'; // 绿色 - 微弱
}

// 工具函数：情感类型图标映射
export function getEmotionIcon(emotion: string): string {
  const iconMap: Record<string, string> = {
    joy: '😊',
    sadness: '😢',
    anger: '😠',
    fear: '😨',
    surprise: '😲',
    disgust: '🤢',
    neutral: '😐',
  };
  return iconMap[emotion] || '❓';
}