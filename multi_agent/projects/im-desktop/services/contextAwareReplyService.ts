import axios, { AxiosInstance } from 'axios';
import {
  ContextAwareReply,
  GenerateReplyRequest,
  GenerateReplyResponse,
  FeedbackRequest,
  SearchReplyRequest,
  PageResponse,
  StatisticsResponse,
  HealthResponse,
  ALL_STATUSES
} from './contextAwareReplyTypes';

/**
 * 上下文感知智能回复生成器 API 服务
 */
class ContextAwareReplyService {
  private api: AxiosInstance;
  private baseURL: string;

  constructor(baseURL: string = '/api/v1') {
    this.baseURL = baseURL;
    this.api = axios.create({
      baseURL,
      timeout: 30000,
      headers: {
        'Content-Type': 'application/json'
      }
    });

    // 请求拦截器
    this.api.interceptors.request.use(
      (config) => {
        // 添加认证token
        const token = localStorage.getItem('token');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => Promise.reject(error)
    );

    // 响应拦截器
    this.api.interceptors.response.use(
      (response) => response.data,
      (error) => {
        console.error('API Error:', error.response?.data || error.message);
        return Promise.reject(error);
      }
    );
  }

  // ==================== 基础 CRUD 操作 ====================

  /**
   * 创建智能回复记录
   */
  async createReply(reply: Partial<ContextAwareReply>): Promise<ContextAwareReply> {
    const response = await this.api.post('/context-aware-reply', reply);
    return response as ContextAwareReply;
  }

  /**
   * 批量创建回复记录
   */
  async createReplies(replies: Partial<ContextAwareReply>[]): Promise<ContextAwareReply[]> {
    const response = await this.api.post('/context-aware-reply/batch', replies);
    return response as ContextAwareReply[];
  }

  /**
   * 获取回复详情
   */
  async getReplyById(id: number): Promise<ContextAwareReply> {
    const response = await this.api.get(`/context-aware-reply/${id}`);
    return response as ContextAwareReply;
  }

  /**
   * 更新回复记录
   */
  async updateReply(id: number, updateData: Partial<ContextAwareReply>): Promise<ContextAwareReply> {
    const response = await this.api.put(`/context-aware-reply/${id}`, updateData);
    return response as ContextAwareReply;
  }

  /**
   * 删除回复记录
   */
  async deleteReply(id: number): Promise<void> {
    await this.api.delete(`/context-aware-reply/${id}`);
  }

  /**
   * 批量删除回复记录
   */
  async deleteReplies(ids: number[]): Promise<void> {
    await this.api.delete('/context-aware-reply/batch', { data: ids });
  }

  // ==================== 查询操作 ====================

  /**
   * 获取用户的回复记录
   */
  async getRepliesByUser(userId: string): Promise<ContextAwareReply[]> {
    const response = await this.api.get(`/context-aware-reply/user/${userId}`);
    return response as ContextAwareReply[];
  }

  /**
   * 分页获取用户的回复记录
   */
  async getRepliesByUserPaged(
    userId: string,
    page: number = 0,
    size: number = 20
  ): Promise<PageResponse<ContextAwareReply>> {
    const response = await this.api.get(`/context-aware-reply/user/${userId}/paged`, {
      params: { page, size }
    });
    return response as PageResponse<ContextAwareReply>;
  }

  /**
   * 获取会话的回复记录
   */
  async getRepliesBySession(sessionId: string): Promise<ContextAwareReply[]> {
    const response = await this.api.get(`/context-aware-reply/session/${sessionId}`);
    return response as ContextAwareReply[];
  }

  /**
   * 获取用户和会话的回复记录
   */
  async getRepliesByUserAndSession(
    userId: string,
    sessionId: string
  ): Promise<ContextAwareReply[]> {
    const response = await this.api.get(
      `/context-aware-reply/user/${userId}/session/${sessionId}`
    );
    return response as ContextAwareReply[];
  }

  /**
   * 获取触发消息的回复记录
   */
  async getReplyByTriggerMessage(messageId: string): Promise<ContextAwareReply | null> {
    try {
      const response = await this.api.get(`/context-aware-reply/trigger-message/${messageId}`);
      return response as ContextAwareReply;
    } catch (error: any) {
      if (error.response?.status === 404) {
        return null;
      }
      throw error;
    }
  }

  /**
   * 获取指定状态的回复记录
   */
  async getRepliesByStatus(status: string): Promise<ContextAwareReply[]> {
    const response = await this.api.get(`/context-aware-reply/status/${status}`);
    return response as ContextAwareReply[];
  }

  /**
   * 获取已使用的回复记录
   */
  async getUsedReplies(): Promise<ContextAwareReply[]> {
    const response = await this.api.get('/context-aware-reply/used');
    return response as ContextAwareReply[];
  }

  /**
   * 获取高质量的回复记录
   */
  async getHighQualityReplies(): Promise<ContextAwareReply[]> {
    const response = await this.api.get('/context-aware-reply/high-quality');
    return response as ContextAwareReply[];
  }

  /**
   * 获取高置信度的回复记录
   */
  async getHighConfidenceReplies(): Promise<ContextAwareReply[]> {
    const response = await this.api.get('/context-aware-reply/high-confidence');
    return response as ContextAwareReply[];
  }

  // ==================== 意图相关查询 ====================

  /**
   * 获取指定意图的回复记录
   */
  async getRepliesByIntent(intent: string): Promise<ContextAwareReply[]> {
    const response = await this.api.get(`/context-aware-reply/intent/${intent}`);
    return response as ContextAwareReply[];
  }

  /**
   * 获取用户指定意图的回复记录
   */
  async getRepliesByUserAndIntent(
    userId: string,
    intent: string
  ): Promise<ContextAwareReply[]> {
    const response = await this.api.get(`/context-aware-reply/user/${userId}/intent/${intent}`);
    return response as ContextAwareReply[];
  }

  /**
   * 获取用户最常用的意图
   */
  async getUserTopIntents(userId: string): Promise<Record<string, number>> {
    const response = await this.api.get(`/context-aware-reply/user/${userId}/top-intents`);
    return response as Record<string, number>;
  }

  // ==================== 语言风格相关查询 ====================

  /**
   * 获取指定语言风格的回复记录
   */
  async getRepliesByLanguageStyle(style: string): Promise<ContextAwareReply[]> {
    const response = await this.api.get(`/context-aware-reply/language-style/${style}`);
    return response as ContextAwareReply[];
  }

  /**
   * 获取用户指定语言风格的回复记录
   */
  async getRepliesByUserAndLanguageStyle(
    userId: string,
    style: string
  ): Promise<ContextAwareReply[]> {
    const response = await this.api.get(
      `/context-aware-reply/user/${userId}/language-style/${style}`
    );
    return response as ContextAwareReply[];
  }

  /**
   * 获取用户最常用的语言风格
   */
  async getUserTopLanguageStyles(userId: string): Promise<Record<string, number>> {
    const response = await this.api.get(`/context-aware-reply/user/${userId}/top-language-styles`);
    return response as Record<string, number>;
  }

  // ==================== 时间范围查询 ====================

  /**
   * 获取时间范围内的回复记录
   */
  async getRepliesByDateRange(
    start: string,
    end: string
  ): Promise<ContextAwareReply[]> {
    const response = await this.api.get('/context-aware-reply/date-range', {
      params: { start, end }
    });
    return response as ContextAwareReply[];
  }

  /**
   * 获取用户时间范围内的回复记录
   */
  async getRepliesByUserAndDateRange(
    userId: string,
    start: string,
    end: string
  ): Promise<ContextAwareReply[]> {
    const response = await this.api.get(`/context-aware-reply/user/${userId}/date-range`, {
      params: { start, end }
    });
    return response as ContextAwareReply[];
  }

  // ==================== 统计操作 ====================

  /**
   * 统计用户回复记录数量
   */
  async countRepliesByUser(userId: string): Promise<number> {
    const response = await this.api.get(`/context-aware-reply/count/user/${userId}`);
    return response as number;
  }

  /**
   * 统计会话回复记录数量
   */
  async countRepliesBySession(sessionId: string): Promise<number> {
    const response = await this.api.get(`/context-aware-reply/count/session/${sessionId}`);
    return response as number;
  }

  /**
   * 统计指定状态回复记录数量
   */
  async countRepliesByStatus(status: string): Promise<number> {
    const response = await this.api.get(`/context-aware-reply/count/status/${status}`);
    return response as number;
  }

  /**
   * 统计已使用回复记录数量
   */
  async countUsedReplies(): Promise<number> {
    const response = await this.api.get('/context-aware-reply/count/used');
    return response as number;
  }

  /**
   * 统计高质量回复记录数量
   */
  async countHighQualityReplies(): Promise<number> {
    const response = await this.api.get('/context-aware-reply/count/high-quality');
    return response as number;
  }

  /**
   * 获取意图分布统计
   */
  async getIntentDistribution(): Promise<Record<string, number>> {
    const response = await this.api.get('/context-aware-reply/stats/intent-distribution');
    return response as Record<string, number>;
  }

  /**
   * 获取语言风格分布统计
   */
  async getLanguageStyleDistribution(): Promise<Record<string, number>> {
    const response = await this.api.get('/context-aware-reply/stats/language-style-distribution');
    return response as Record<string, number>;
  }

  /**
   * 获取平均反馈评分
   */
  async getAverageFeedbackScore(): Promise<number> {
    const response = await this.api.get('/context-aware-reply/stats/average-feedback-score');
    return response as number;
  }

  /**
   * 获取平均生成时间
   */
  async getAverageGenerationTime(): Promise<number> {
    const response = await this.api.get('/context-aware-reply/stats/average-generation-time');
    return response as number;
  }

  /**
   * 获取完整统计信息
   */
  async getStatistics(): Promise<StatisticsResponse> {
    const [
      totalReplies,
      usedReplies,
      highQualityReplies,
      averageFeedbackScore,
      averageGenerationTimeMs,
      intentDistribution,
      languageStyleDistribution
    ] = await Promise.all([
      this.countRepliesByStatus(ALL_STATUSES[0]), // 简单处理，实际需要调整
      this.countUsedReplies(),
      this.countHighQualityReplies(),
      this.getAverageFeedbackScore(),
      this.getAverageGenerationTime(),
      this.getIntentDistribution(),
      this.getLanguageStyleDistribution()
    ]);

    return {
      totalReplies,
      totalUsers: 0, // 需要后端支持
      usedReplies,
      highQualityReplies,
      averageFeedbackScore,
      averageGenerationTimeMs,
      intentDistribution,
      languageStyleDistribution,
      statusDistribution: {}
    };
  }

  // ==================== 高级操作 ====================

  /**
   * 搜索回复记录
   */
  async searchReplies(keyword: string): Promise<ContextAwareReply[]> {
    const response = await this.api.get('/context-aware-reply/search', {
      params: { keyword }
    });
    return response as ContextAwareReply[];
  }

  /**
   * 标记回复为已使用
   */
  async markAsUsed(id: number): Promise<void> {
    await this.api.post(`/context-aware-reply/${id}/mark-used`);
  }

  /**
   * 批量标记为已使用
   */
  async markMultipleAsUsed(ids: number[]): Promise<void> {
    await this.api.post('/context-aware-reply/batch/mark-used', ids);
  }

  /**
   * 提交用户反馈
   */
  async submitFeedback(id: number, score: number, comment?: string): Promise<void> {
    await this.api.post(`/context-aware-reply/${id}/feedback`, null, {
      params: { score, comment }
    });
  }

  /**
   * 获取最近N条用户回复
   */
  async getRecentRepliesByUser(userId: string, limit: number = 10): Promise<ContextAwareReply[]> {
    const response = await this.api.get(`/context-aware-reply/user/${userId}/recent`, {
      params: { limit }
    });
    return response as ContextAwareReply[];
  }

  /**
   * 清理过期回复
   */
  async cleanupExpiredReplies(): Promise<number> {
    const response = await this.api.post('/context-aware-reply/cleanup/expired');
    return response as number;
  }

  /**
   * 清理低质量回复
   */
  async cleanupLowQualityReplies(): Promise<number> {
    const response = await this.api.post('/context-aware-reply/cleanup/low-quality');
    return response as number;
  }

  // ==================== 内容获取 ====================

  /**
   * 获取回复候选列表
   */
  async getReplyCandidates(id: number): Promise<string[]> {
    const response = await this.api.get(`/context-aware-reply/${id}/candidates`);
    return response as string[];
  }

  /**
   * 获取推荐的表情符号列表
   */
  async getRecommendedEmojis(id: number): Promise<string[]> {
    const response = await this.api.get(`/context-aware-reply/${id}/emojis`);
    return response as string[];
  }

  /**
   * 获取个性化特征
   */
  async getPersonalizationFeatures(id: number): Promise<Record<string, any>> {
    const response = await this.api.get(`/context-aware-reply/${id}/personalization`);
    return response as Record<string, any>;
  }

  /**
   * 获取生成选项
   */
  async getGenerationOptions(id: number): Promise<Record<string, any>> {
    const response = await this.api.get(`/context-aware-reply/${id}/options`);
    return response as Record<string, any>;
  }

  // ==================== 智能生成 ====================

  /**
   * 生成智能回复
   */
  async generateReply(request: GenerateReplyRequest): Promise<ContextAwareReply> {
    const response = await this.api.post('/context-aware-reply/generate', null, {
      params: {
        userId: request.userId,
        sessionId: request.sessionId,
        triggerMessageContent: request.triggerMessageContent
      }
    });
    return response as ContextAwareReply;
  }

  // ==================== 健康检查 ====================

  /**
   * 健康检查
   */
  async healthCheck(): Promise<HealthResponse> {
    const response = await this.api.get('/context-aware-reply/health');
    return response as HealthResponse;
  }
}

// 导出单例
export const contextAwareReplyService = new ContextAwareReplyService();
export default contextAwareReplyService;