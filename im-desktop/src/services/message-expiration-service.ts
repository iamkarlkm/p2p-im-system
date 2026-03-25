/**
 * 消息过期服务
 */
import { apiClient } from './api-client';
import type { ExpirationRule, ExpirationRuleRequest } from '../types/message-expiration';

export class MessageExpirationService {

  /**
   * 创建会话级过期规则
   */
  async createRule(conversationId: string, request: ExpirationRuleRequest): Promise<ExpirationRule> {
    return apiClient.post<ExpirationRule>('/api/expiration/rules', {
      ...request,
      conversationId,
    });
  }

  /**
   * 更新过期规则
   */
  async updateRule(ruleId: number, request: ExpirationRuleRequest): Promise<ExpirationRule> {
    return apiClient.put<ExpirationRule>(`/api/expiration/rules/${ruleId}`, request);
  }

  /**
   * 获取用户所有规则
   */
  async getUserRules(): Promise<ExpirationRule[]> {
    return apiClient.get<ExpirationRule[]>('/api/expiration/rules');
  }

  /**
   * 获取会话生效规则
   */
  async getEffectiveRule(conversationId: string): Promise<ExpirationRule | null> {
    return apiClient.get<ExpirationRule>(`/api/expiration/rules/conversation/${conversationId}`);
  }

  /**
   * 获取全局默认规则
   */
  async getGlobalRule(): Promise<ExpirationRule | null> {
    return apiClient.get<ExpirationRule>('/api/expiration/rules/global');
  }

  /**
   * 删除规则
   */
  async deleteRule(ruleId: number): Promise<void> {
    return apiClient.delete(`/api/expiration/rules/${ruleId}`);
  }

  /**
   * 启用/禁用规则
   */
  async toggleRule(ruleId: number, enabled: boolean): Promise<ExpirationRule> {
    return apiClient.patch<ExpirationRule>(`/api/expiration/rules/${ruleId}/toggle?enabled=${enabled}`);
  }

  /**
   * 获取消息剩余存活时间
   */
  async getRemainingTime(messageId: number): Promise<number> {
    const resp = await apiClient.get<{ messageId: number; remainingSeconds: number }>(
      `/api/expiration/messages/${messageId}/remaining`
    );
    return resp.remainingSeconds ?? -1;
  }

  /**
   * 记录消息被阅读（启动阅后即焚计时）
   */
  async recordRead(messageId: number): Promise<void> {
    return apiClient.post(`/api/expiration/messages/${messageId}/read`);
  }

  /**
   * 格式化过期时长为可读字符串
   */
  static formatExpiration(seconds: number): string {
    if (seconds < 60) return `${seconds}秒`;
    if (seconds < 3600) return `${Math.floor(seconds / 60)}分钟`;
    if (seconds < 86400) return `${Math.floor(seconds / 3600)}小时`;
    return `${Math.floor(seconds / 86400)}天`;
  }

  /**
   * 格式化相对时间
   */
  static formatRelative(seconds: number | null): string {
    if (!seconds) return '未设置';
    return this.formatExpiration(seconds);
  }
}

export const expirationService = new MessageExpirationService();
