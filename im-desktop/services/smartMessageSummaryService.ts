/**
 * 智能消息摘要 API 服务
 * Smart Message Summary API Service for IM Desktop
 */

import axios, { AxiosInstance } from 'axios';
import {
  SmartMessageSummary,
  CreateSummaryRequest,
  UpdateSummaryRequest,
  GenerateSummaryRequest,
  SummaryQueryParams,
  ApiResponse,
  PageResponse,
  UserSummaryStats,
  SystemSummaryStats,
  BatchOperationRequest,
  SummaryStatus,
  SummaryQuality
} from '../types/smartMessageSummary';

// API 基础 URL
const BASE_URL = '/api/v1/smart-summary';

// 创建 axios 实例
const apiClient: AxiosInstance = axios.create({
  baseURL: BASE_URL,
  headers: {
    'Content-Type': 'application/json'
  }
});

/**
 * 智能消息摘要服务类
 */
class SmartMessageSummaryService {
  
  // ==================== CRUD 操作 ====================

  /**
   * 创建摘要
   */
  async createSummary(request: CreateSummaryRequest): Promise<ApiResponse<SmartMessageSummary>> {
    const response = await apiClient.post<ApiResponse<SmartMessageSummary>>('', request);
    return response.data;
  }

  /**
   * 获取摘要详情
   */
  async getSummary(id: number): Promise<ApiResponse<SmartMessageSummary>> {
    const response = await apiClient.get<ApiResponse<SmartMessageSummary>>(`/${id}`);
    return response.data;
  }

  /**
   * 获取摘要详情（带用户验证）
   */
  async getSummaryByUser(id: number, userId: string): Promise<ApiResponse<SmartMessageSummary>> {
    const response = await apiClient.get<ApiResponse<SmartMessageSummary>>(`/${id}/user/${userId}`);
    return response.data;
  }

  /**
   * 更新摘要
   */
  async updateSummary(id: number, request: UpdateSummaryRequest): Promise<ApiResponse<SmartMessageSummary>> {
    const response = await apiClient.put<ApiResponse<SmartMessageSummary>>(`/${id}`, request);
    return response.data;
  }

  /**
   * 删除摘要（逻辑删除）
   */
  async deleteSummary(id: number): Promise<ApiResponse<void>> {
    const response = await apiClient.delete<ApiResponse<void>>(`/${id}`);
    return response.data;
  }

  // ==================== 查询操作 ====================

  /**
   * 查询用户摘要列表
   */
  async getUserSummaries(
    userId: string,
    params: SummaryQueryParams = {}
  ): Promise<ApiResponse<PageResponse<SmartMessageSummary>>> {
    const { page = 0, size = 20, ...rest } = params;
    const response = await apiClient.get<ApiResponse<PageResponse<SmartMessageSummary>>>(
      `/user/${userId}`,
      { params: { page, size, ...rest } }
    );
    return response.data;
  }

  /**
   * 查询会话摘要列表
   */
  async getSessionSummaries(
    sessionId: string,
    params: SummaryQueryParams = {}
  ): Promise<ApiResponse<PageResponse<SmartMessageSummary>>> {
    const { page = 0, size = 20, ...rest } = params;
    const response = await apiClient.get<ApiResponse<PageResponse<SmartMessageSummary>>>(
      `/session/${sessionId}`,
      { params: { page, size, ...rest } }
    );
    return response.data;
  }

  /**
   * 查询指定状态的摘要
   */
  async getSummariesByStatus(
    status: SummaryStatus,
    params: SummaryQueryParams = {}
  ): Promise<ApiResponse<PageResponse<SmartMessageSummary>>> {
    const { page = 0, size = 20, ...rest } = params;
    const response = await apiClient.get<ApiResponse<PageResponse<SmartMessageSummary>>>(
      `/status/${status}`,
      { params: { page, size, ...rest } }
    );
    return response.data;
  }

  /**
   * 搜索摘要
   */
  async searchSummaries(
    keyword: string,
    userId?: string,
    params: SummaryQueryParams = {}
  ): Promise<ApiResponse<{ summaries: SmartMessageSummary[]; total: number }>> {
    const { page = 0, size = 20 } = params;
    const response = await apiClient.get<ApiResponse<{ summaries: SmartMessageSummary[]; total: number }>>(
      '/search',
      { params: { keyword, userId, page, size } }
    );
    return response.data;
  }

  // ==================== 摘要生成 ====================

  /**
   * 生成消息摘要
   */
  async generateSummary(request: GenerateSummaryRequest): Promise<ApiResponse<SmartMessageSummary>> {
    const response = await apiClient.post<ApiResponse<SmartMessageSummary>>('/generate', request);
    return response.data;
  }

  /**
   * 重新生成摘要
   */
  async regenerateSummary(id: number): Promise<ApiResponse<SmartMessageSummary>> {
    const response = await apiClient.post<ApiResponse<SmartMessageSummary>>(`/${id}/regenerate`);
    return response.data;
  }

  // ==================== 批量操作 ====================

  /**
   * 批量更新状态
   */
  async batchUpdateStatus(request: BatchOperationRequest): Promise<ApiResponse<{ updatedCount: number }>> {
    const response = await apiClient.put<ApiResponse<{ updatedCount: number }>>('/batch/status', request);
    return response.data;
  }

  /**
   * 批量标记为已读
   */
  async batchMarkAsRead(ids: number[]): Promise<ApiResponse<{ updatedCount: number }>> {
    const response = await apiClient.put<ApiResponse<{ updatedCount: number }>>('/batch/read', { ids });
    return response.data;
  }

  /**
   * 批量标记为喜欢
   */
  async batchMarkAsFavorite(ids: number[], favorite: boolean): Promise<ApiResponse<SmartMessageSummary>> {
    const responses = await Promise.all(
      ids.map(id => this.updateSummary(id, { isFavorite: favorite }))
    );
    return responses[0];
  }

  /**
   * 批量删除
   */
  async batchDelete(ids: number[]): Promise<void> {
    await Promise.all(ids.map(id => this.deleteSummary(id)));
  }

  // ==================== 统计操作 ====================

  /**
   * 获取用户摘要统计
   */
  async getUserStats(userId: string): Promise<ApiResponse<UserSummaryStats>> {
    const response = await apiClient.get<ApiResponse<UserSummaryStats>>(`/stats/user/${userId}`);
    return response.data;
  }

  /**
   * 获取系统统计
   */
  async getSystemStats(): Promise<ApiResponse<SystemSummaryStats>> {
    const response = await apiClient.get<ApiResponse<SystemSummaryStats>>('/stats/system');
    return response.data;
  }

  // ==================== 清理和维护 ====================

  /**
   * 清理过期缓存
   */
  async cleanupExpiredCache(): Promise<ApiResponse<{ cleanedCount: number }>> {
    const response = await apiClient.post<ApiResponse<{ cleanedCount: number }>>('/maintenance/cleanup-cache');
    return response.data;
  }

  /**
   * 归档旧摘要
   */
  async archiveOldSummaries(beforeTime: string): Promise<ApiResponse<{ archivedCount: number }>> {
    const response = await apiClient.post<ApiResponse<{ archivedCount: number }>>('/maintenance/archive', {
      beforeTime
    });
    return response.data;
  }

  /**
   * 标记低质量摘要需要重新生成
   */
  async markLowQualityForRegeneration(): Promise<ApiResponse<{ markedCount: number }>> {
    const response = await apiClient.post<ApiResponse<{ markedCount: number }>>('/maintenance/mark-regen');
    return response.data;
  }

  // ==================== 便捷方法 ====================

  /**
   * 生成会话摘要（便捷方法）
   */
  async summarizeConversation(
    sessionId: string,
    userId: string,
    originalContent: string
  ): Promise<ApiResponse<SmartMessageSummary>> {
    return this.generateSummary({
      sessionId,
      userId,
      summaryType: 'CONVERSATION' as any,
      originalContent
    });
  }

  /**
   * 生成群聊摘要（便捷方法）
   */
  async summarizeGroupChat(
    sessionId: string,
    userId: string,
    originalContent: string
  ): Promise<ApiResponse<SmartMessageSummary>> {
    return this.generateSummary({
      sessionId,
      userId,
      summaryType: 'GROUP_CONVERSATION' as any,
      originalContent
    });
  }

  /**
   * 生成关键决策摘要（便捷方法）
   */
  async extractKeyDecisions(
    sessionId: string,
    userId: string,
    originalContent: string
  ): Promise<ApiResponse<SmartMessageSummary>> {
    return this.generateSummary({
      sessionId,
      userId,
      summaryType: 'KEY_DECISIONS' as any,
      originalContent
    });
  }

  /**
   * 生成行动计划摘要（便捷方法）
   */
  async extractActionPlan(
    sessionId: string,
    userId: string,
    originalContent: string
  ): Promise<ApiResponse<SmartMessageSummary>> {
    return this.generateSummary({
      sessionId,
      userId,
      summaryType: 'ACTION_PLAN' as any,
      originalContent
    });
  }

  /**
   * 获取用户高质量摘要
   */
  async getUserHighQualitySummaries(userId: string, limit: number = 10): Promise<SmartMessageSummary[]> {
    const response = await this.getUserSummaries(userId, { size: 100 });
    if (response.success && response.data?.summaries) {
      return response.data.summaries
        .filter(s => s.qualityScore >= 80)
        .slice(0, limit);
    }
    return [];
  }

  /**
   * 获取用户最近的摘要
   */
  async getUserRecentSummaries(userId: string, limit: number = 10): Promise<SmartMessageSummary[]> {
    const response = await this.getUserSummaries(userId, { size: limit });
    if (response.success && response.data?.summaries) {
      return response.data.summaries;
    }
    return [];
  }

  /**
   * 获取用户最喜欢的摘要
   */
  async getUserFavoriteSummaries(userId: string): Promise<SmartMessageSummary[]> {
    const response = await this.getUserSummaries(userId, { size: 100 });
    if (response.success && response.data?.summaries) {
      return response.data.summaries.filter(s => s.isFavorite);
    }
    return [];
  }

  /**
   * 获取需要重新生成的摘要
   */
  async getSummariesNeedingRegeneration(userId: string): Promise<SmartMessageSummary[]> {
    const response = await this.getUserSummaries(userId, { size: 100 });
    if (response.success && response.data?.summaries) {
      return response.data.summaries.filter(s => s.qualityScore < 60);
    }
    return [];
  }

  /**
   * 更新摘要的用户反馈
   */
  async submitFeedback(id: number, rating: number, feedback?: string): Promise<ApiResponse<SmartMessageSummary>> {
    return this.updateSummary(id, { userRating: rating, userFeedback: feedback });
  }

  /**
   * 切换摘要的喜欢状态
   */
  async toggleFavorite(id: number, currentStatus: boolean): Promise<ApiResponse<SmartMessageSummary>> {
    return this.updateSummary(id, { isFavorite: !currentStatus });
  }

  /**
   * 设置摘要缓存
   */
  async setCacheExpiry(id: number, expiryTime: string): Promise<ApiResponse<SmartMessageSummary>> {
    return this.updateSummary(id, { offlineCached: true, cacheExpiryTime: expiryTime });
  }

  /**
   * 清除摘要缓存
   */
  async clearCache(id: number): Promise<ApiResponse<SmartMessageSummary>> {
    return this.updateSummary(id, { offlineCached: false });
  }
}

// 导出单例
export const smartMessageSummaryService = new SmartMessageSummaryService();
export default smartMessageSummaryService;
