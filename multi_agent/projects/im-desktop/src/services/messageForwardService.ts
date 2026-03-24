/**
 * 消息合并转发服务
 * 支持多条消息合并转发、选择转发、逐条转发
 */

import { apiClient } from './apiClient';

export interface ForwardBundle {
  id: number;
  bundleId: string;
  sourceConversationId: number;
  targetConversationId?: number;
  createdBy: number;
  forwardType: 'MERGE' | 'SELECT' | 'INDIVIDUAL';
  title?: string;
  messageIds: number[];
  messageCount: number;
  hasMedia: boolean;
  mediaCount: number;
  status: 'DRAFT' | 'PENDING' | 'SENT' | 'FAILED' | 'CANCELLED';
  sendMode: 'MERGE' | 'SEPARATE';
  includeSenderInfo: boolean;
  includeTimestamp: boolean;
  anonymizeSenders: boolean;
  customComment?: string;
  createdAt: string;
  updatedAt: string;
  forwardedAt?: string;
}

export interface ForwardConfig {
  sendMode?: 'MERGE' | 'SEPARATE';
  includeSenderInfo?: boolean;
  includeTimestamp?: boolean;
  anonymizeSenders?: boolean;
  customComment?: string;
  title?: string;
}

export interface ForwardStats {
  total: number;
  successful: number;
  failed: number;
  byType: {
    MERGE: number;
    SELECT: number;
    INDIVIDUAL: number;
  };
}

export class MessageForwardService {
  private static instance: MessageForwardService;
  
  public static getInstance(): MessageForwardService {
    if (!MessageForwardService.instance) {
      MessageForwardService.instance = new MessageForwardService();
    }
    return MessageForwardService.instance;
  }
  
  /**
   * 创建转发草稿
   */
  async createDraft(
    sourceConversationId: number,
    forwardType: 'MERGE' | 'SELECT' | 'INDIVIDUAL',
    messageIds: number[]
  ): Promise<{
    success: boolean;
    bundleId: string;
    id: number;
    messageCount: number;
    createdAt: string;
  }> {
    try {
      const response = await apiClient.post('/message/forward/draft', messageIds, {
        params: { sourceConversationId, forwardType }
      });
      return response.data;
    } catch (error) {
      console.error('创建转发草稿失败:', error);
      throw new Error('创建转发草稿失败');
    }
  }
  
  /**
   * 添加消息到草稿
   */
  async addMessage(bundleId: string, messageId: number): Promise<{
    success: boolean;
    bundleId: string;
    messageCount: number;
    updatedAt: string;
  }> {
    try {
      const response = await apiClient.post(`/message/forward/draft/${bundleId}/add`, null, {
        params: { messageId }
      });
      return response.data;
    } catch (error) {
      console.error('添加消息到草稿失败:', error);
      throw new Error('添加消息失败');
    }
  }
  
  /**
   * 从草稿移除消息
   */
  async removeMessage(bundleId: string, messageId: number): Promise<{
    success: boolean;
    bundleId: string;
    messageCount: number;
    updatedAt: string;
  }> {
    try {
      const response = await apiClient.post(`/message/forward/draft/${bundleId}/remove`, null, {
        params: { messageId }
      });
      return response.data;
    } catch (error) {
      console.error('从草稿移除消息失败:', error);
      throw new Error('移除消息失败');
    }
  }
  
  /**
   * 更新转发配置
   */
  async updateConfig(bundleId: string, config: ForwardConfig): Promise<{
    success: boolean;
    bundleId: string;
    sendMode: string;
    includeSenderInfo: boolean;
    includeTimestamp: boolean;
    anonymizeSenders: boolean;
  }> {
    try {
      const response = await apiClient.put(`/message/forward/draft/${bundleId}/config`, config);
      return response.data;
    } catch (error) {
      console.error('更新转发配置失败:', error);
      throw new Error('更新配置失败');
    }
  }
  
  /**
   * 执行转发
   */
  async executeForward(
    bundleId: string,
    targetConversationId: number
  ): Promise<{
    success: boolean;
    bundleId: string;
    status: string;
    forwardedAt: string;
    targetConversationId: number;
    error?: string;
  }> {
    try {
      const response = await apiClient.post(`/message/forward/draft/${bundleId}/send`, null, {
        params: { targetConversationId }
      });
      return response.data;
    } catch (error: any) {
      console.error('执行转发失败:', error);
      if (error.response?.data?.error) {
        throw new Error(error.response.data.error);
      }
      throw new Error('执行转发失败');
    }
  }
  
  /**
   * 取消草稿
   */
  async cancelDraft(bundleId: string): Promise<{
    success: boolean;
    bundleId: string;
    cancelledAt: string;
  }> {
    try {
      const response = await apiClient.post(`/message/forward/draft/${bundleId}/cancel`);
      return response.data;
    } catch (error) {
      console.error('取消草稿失败:', error);
      throw new Error('取消草稿失败');
    }
  }
  
  /**
   * 获取草稿列表
   */
  async getDrafts(): Promise<{
    success: boolean;
    count: number;
    drafts: Array<{
      id: number;
      bundleId: string;
      sourceConversationId: number;
      messageCount: number;
      forwardType: string;
      sendMode: string;
      title?: string;
      customComment?: string;
      createdAt: string;
      updatedAt: string;
    }>;
  }> {
    try {
      const response = await apiClient.get('/message/forward/drafts');
      return response.data;
    } catch (error) {
      console.error('获取草稿列表失败:', error);
      throw new Error('获取草稿列表失败');
    }
  }
  
  /**
   * 获取转发历史
   */
  async getHistory(limit: number = 20): Promise<{
    success: boolean;
    count: number;
    history: Array<{
      id: number;
      bundleId: string;
      sourceConversationId: number;
      targetConversationId: number;
      messageCount: number;
      forwardType: string;
      title?: string;
      forwardedAt: string;
    }>;
  }> {
    try {
      const response = await apiClient.get('/message/forward/history', {
        params: { limit }
      });
      return response.data;
    } catch (error) {
      console.error('获取转发历史失败:', error);
      throw new Error('获取转发历史失败');
    }
  }
  
  /**
   * 获取转发统计
   */
  async getStats(): Promise<{
    success: boolean;
    userId: number;
    stats: ForwardStats;
  }> {
    try {
      const response = await apiClient.get('/message/forward/stats');
      return response.data;
    } catch (error) {
      console.error('获取转发统计失败:', error);
      throw new Error('获取转发统计失败');
    }
  }
  
  /**
   * 健康检查
   */
  async healthCheck(): Promise<{
    service: string;
    status: string;
    timestamp: string;
  }> {
    try {
      const response = await apiClient.get('/message/forward/health');
      return response.data;
    } catch (error) {
      console.error('消息转发服务健康检查失败:', error);
      throw new Error('消息转发服务不可用');
    }
  }
  
  /**
   * 生成转发预览
   */
  generateForwardPreview(messageIds: number[], includeSenderInfo: boolean): string {
    // 生成转发预览文本
    const preview = `转发 ${messageIds.length} 条消息${includeSenderInfo ? '（包含发送者信息）' : ''}`;
    return preview;
  }
  
  /**
   * 验证消息选择
   */
  validateMessageSelection(messageIds: number[]): { valid: boolean; error?: string } {
    if (messageIds.length === 0) {
      return { valid: false, error: '请选择至少一条消息' };
    }
    if (messageIds.length > 100) {
      return { valid: false, error: '最多选择 100 条消息' };
    }
    return { valid: true };
  }
}

export const messageForwardService = MessageForwardService.getInstance();