/**
 * 消息编辑服务 - TypeScript API 客户端服务
 * 用于与后端消息编辑 API 通信
 */

import { apiClient } from './apiClient';

export interface MessageEdit {
  id: string;
  messageId: string;
  originalMessageId: string;
  userId: string;
  conversationId: string;
  contentType: 'TEXT' | 'MARKDOWN' | 'HTML' | 'JSON';
  content: string;
  originalContent?: string;
  editType: 'CREATE' | 'EDIT' | 'REPLACE' | 'CORRECT' | 'ENHANCE' | 'ROLLBACK';
  version: number;
  isLatest: boolean;
  clientMessageId?: string;
  editReason?: string;
  metadata?: string;
  contentHash?: string;
  originalContentHash?: string;
  diffPatch?: string;
  editSizeDelta?: number;
  editWordCount?: number;
  hasAttachments?: boolean;
  attachmentsJson?: string;
  mentionsJson?: string;
  linksJson?: string;
  formattingJson?: string;
  readCount: number;
  reactionCount: number;
  replyCount: number;
  editCount: number;
  auditStatus: 'PENDING' | 'APPROVED' | 'REJECTED' | 'FLAGGED';
  auditNotes?: string;
  auditorId?: string;
  auditTimestamp?: string;
  status: 'ACTIVE' | 'DELETED' | 'ARCHIVED' | 'HIDDEN';
  privacyLevel: 'PUBLIC' | 'STANDARD' | 'PRIVATE' | 'CONFIDENTIAL';
  deviceId?: string;
  clientVersion?: string;
  platform?: 'WEB' | 'DESKTOP' | 'IOS' | 'ANDROID';
  ipAddress?: string;
  userAgent?: string;
  syncStatus: 'SYNCED' | 'PENDING' | 'FAILED' | 'CONFLICT' | 'RESOLVED';
  conflictResolution?: 'KEEP_NEWEST' | 'KEEP_ORIGINAL' | 'MERGE' | 'MANUAL';
  conflictDetails?: string;
  createdAt: string;
  updatedAt: string;
  deletedAt?: string;
  archivedAt?: string;
  expiresAt?: string;
  versionExpiresAt?: string;
  lastAccessedAt?: string;
  lastModifiedBy?: string;
  tags?: string;
  customFields?: string;
}

export interface CreateMessageEditRequest {
  messageId: string;
  originalMessageId: string;
  userId: string;
  conversationId: string;
  contentType: 'TEXT' | 'MARKDOWN' | 'HTML' | 'JSON';
  content: string;
  originalContent?: string;
  editType?: 'CREATE' | 'EDIT' | 'REPLACE' | 'CORRECT' | 'ENHANCE';
  version?: number;
  isLatest?: boolean;
  editReason?: string;
  metadata?: string;
  deviceId?: string;
  clientVersion?: string;
  platform?: 'WEB' | 'DESKTOP' | 'IOS' | 'ANDROID';
}

export interface UpdateMessageEditRequest {
  content?: string;
  editReason?: string;
  metadata?: string;
  status?: 'ACTIVE' | 'DELETED' | 'ARCHIVED' | 'HIDDEN';
  auditStatus?: 'PENDING' | 'APPROVED' | 'REJECTED' | 'FLAGGED';
  auditNotes?: string;
  auditorId?: string;
  tags?: string;
  customFields?: string;
}

export interface AuditRequest {
  auditStatus: 'APPROVED' | 'REJECTED' | 'FLAGGED';
  auditNotes?: string;
  auditorId?: string;
}

export interface SearchParams {
  messageId?: string;
  userId?: string;
  conversationId?: string;
  editType?: string;
  status?: string;
  auditStatus?: string;
  platform?: string;
  startDate?: string;
  endDate?: string;
}

export interface StatsResponse {
  [key: string]: any;
}

export interface BatchAuditRequest {
  editIds: string[];
  auditStatus: string;
  auditNotes?: string;
  auditorId?: string;
}

export interface RollbackRequest {
  reason?: string;
}

export class MessageEditService {
  private baseUrl = '/api/message-edits';

  /**
   * 创建新的消息编辑记录
   */
  async createMessageEdit(request: CreateMessageEditRequest): Promise<MessageEdit> {
    const response = await apiClient.post<MessageEdit>(this.baseUrl, request);
    return response.data;
  }

  /**
   * 批量创建消息编辑记录
   */
  async batchCreateMessageEdits(requests: CreateMessageEditRequest[]): Promise<MessageEdit[]> {
    const response = await apiClient.post<MessageEdit[]>(`${this.baseUrl}/batch`, requests);
    return response.data;
  }

  /**
   * 获取消息编辑记录详情
   */
  async getMessageEdit(editId: string): Promise<MessageEdit> {
    const response = await apiClient.get<MessageEdit>(`${this.baseUrl}/${editId}`);
    return response.data;
  }

  /**
   * 更新消息编辑记录
   */
  async updateMessageEdit(editId: string, request: UpdateMessageEditRequest): Promise<MessageEdit> {
    const response = await apiClient.put<MessageEdit>(`${this.baseUrl}/${editId}`, request);
    return response.data;
  }

  /**
   * 删除消息编辑记录（软删除）
   */
  async deleteMessageEdit(editId: string): Promise<void> {
    await apiClient.delete(`${this.baseUrl}/${editId}`);
  }

  /**
   * 恢复已删除的消息编辑记录
   */
  async restoreMessageEdit(editId: string): Promise<MessageEdit> {
    const response = await apiClient.post<MessageEdit>(`${this.baseUrl}/${editId}/restore`, {});
    return response.data;
  }

  /**
   * 永久删除消息编辑记录
   */
  async permanentlyDeleteMessageEdit(editId: string): Promise<void> {
    await apiClient.delete(`${this.baseUrl}/${editId}/permanent`);
  }

  // ==================== Version Management ====================

  /**
   * 获取消息的所有编辑版本
   */
  async getMessageEdits(messageId: string): Promise<MessageEdit[]> {
    const response = await apiClient.get<MessageEdit[]>(`${this.baseUrl}/message/${messageId}`);
    return response.data;
  }

  /**
   * 获取消息的特定版本
   */
  async getMessageEditByVersion(messageId: string, version: number): Promise<MessageEdit> {
    const response = await apiClient.get<MessageEdit>(`${this.baseUrl}/message/${messageId}/version/${version}`);
    return response.data;
  }

  /**
   * 获取消息的最新版本
   */
  async getLatestMessageEdit(messageId: string): Promise<MessageEdit> {
    const response = await apiClient.get<MessageEdit>(`${this.baseUrl}/message/${messageId}/latest`);
    return response.data;
  }

  /**
   * 获取特定版本的前一个版本
   */
  async getPreviousVersion(messageId: string, version: number): Promise<MessageEdit | null> {
    try {
      const response = await apiClient.get<MessageEdit>(`${this.baseUrl}/message/${messageId}/version/${version}/previous`);
      return response.data;
    } catch (error) {
      if ((error as any).response?.status === 404) {
        return null;
      }
      throw error;
    }
  }

  /**
   * 获取特定版本的后一个版本
   */
  async getNextVersion(messageId: string, version: number): Promise<MessageEdit | null> {
    try {
      const response = await apiClient.get<MessageEdit>(`${this.baseUrl}/message/${messageId}/version/${version}/next`);
      return response.data;
    } catch (error) {
      if ((error as any).response?.status === 404) {
        return null;
      }
      throw error;
    }
  }

  /**
   * 回滚到特定版本
   */
  async rollbackToVersion(messageId: string, version: number, reason?: string): Promise<MessageEdit> {
    const request: RollbackRequest = reason ? { reason } : {};
    const response = await apiClient.post<MessageEdit>(`${this.baseUrl}/message/${messageId}/rollback/${version}`, request);
    return response.data;
  }

  // ==================== Search and Filter ====================

  /**
   * 搜索消息编辑记录
   */
  async searchMessageEdits(params: SearchParams): Promise<MessageEdit[]> {
    const queryParams = new URLSearchParams();
    
    Object.entries(params).forEach(([key, value]) => {
      if (value !== undefined && value !== null && value !== '') {
        queryParams.append(key, value.toString());
      }
    });
    
    const queryString = queryParams.toString();
    const url = queryString ? `${this.baseUrl}/search?${queryString}` : `${this.baseUrl}/search`;
    
    const response = await apiClient.get<MessageEdit[]>(url);
    return response.data;
  }

  /**
   * 获取待审核的编辑记录
   */
  async getPendingAuditEdits(): Promise<MessageEdit[]> {
    const response = await apiClient.get<MessageEdit[]>(`${this.baseUrl}/pending-audit`);
    return response.data;
  }

  // ==================== Audit Operations ====================

  /**
   * 审核消息编辑记录
   */
  async auditMessageEdit(editId: string, request: AuditRequest): Promise<MessageEdit> {
    const response = await apiClient.post<MessageEdit>(`${this.baseUrl}/${editId}/audit`, request);
    return response.data;
  }

  /**
   * 批量审核消息编辑记录
   */
  async batchAuditMessageEdits(request: BatchAuditRequest): Promise<{ updatedCount: number; message: string }> {
    const response = await apiClient.post<{ updatedCount: number; message: string }>(
      `${this.baseUrl}/batch-audit`,
      request
    );
    return response.data;
  }

  // ==================== Statistics and Reports ====================

  /**
   * 获取用户的编辑统计
   */
  async getUserEditStats(userId: string): Promise<StatsResponse> {
    const response = await apiClient.get<StatsResponse>(`${this.baseUrl}/user/${userId}/stats`);
    return response.data;
  }

  /**
   * 获取会话的编辑统计
   */
  async getConversationEditStats(conversationId: string): Promise<StatsResponse> {
    const response = await apiClient.get<StatsResponse>(`${this.baseUrl}/conversation/${conversationId}/stats`);
    return response.data;
  }

  /**
   * 获取编辑类型统计
   */
  async getEditTypeStats(): Promise<Array<{ editType: string; count: number }>> {
    const response = await apiClient.get<Array<{ editType: string; count: number }>>(`${this.baseUrl}/stats/edit-types`);
    return response.data;
  }

  /**
   * 获取平台统计
   */
  async getPlatformStats(): Promise<Array<{ platform: string; count: number }>> {
    const response = await apiClient.get<Array<{ platform: string; count: number }>>(`${this.baseUrl}/stats/platforms`);
    return response.data;
  }

  /**
   * 获取审核状态统计
   */
  async getAuditStatusStats(): Promise<Array<{ auditStatus: string; count: number }>> {
    const response = await apiClient.get<Array<{ auditStatus: string; count: number }>>(`${this.baseUrl}/stats/audit-status`);
    return response.data;
  }

  // ==================== Maintenance Operations ====================

  /**
   * 归档旧的消息编辑记录
   */
  async archiveOldEdits(beforeDate?: string): Promise<{ archivedCount: number; beforeDate: string; timestamp: string }> {
    const request = beforeDate ? { before: beforeDate } : {};
    const response = await apiClient.post<{ archivedCount: number; beforeDate: string; timestamp: string }>(
      `${this.baseUrl}/maintenance/archive`,
      request
    );
    return response.data;
  }

  /**
   * 清理过期的消息编辑记录
   */
  async cleanupExpiredEdits(): Promise<{ deletedCount: number; timestamp: string }> {
    const response = await apiClient.post<{ deletedCount: number; timestamp: string }>(
      `${this.baseUrl}/maintenance/cleanup-expired`,
      {}
    );
    return response.data;
  }

  /**
   * 清理过期的版本
   */
  async cleanupExpiredVersions(): Promise<{ deletedCount: number; timestamp: string }> {
    const response = await apiClient.post<{ deletedCount: number; timestamp: string }>(
      `${this.baseUrl}/maintenance/cleanup-expired-versions`,
      {}
    );
    return response.data;
  }

  /**
   * 同步待同步的编辑记录
   */
  async syncPendingEdits(beforeDate?: string, newStatus?: string): Promise<{ syncedCount: number; beforeDate: string; newStatus: string; timestamp: string }> {
    const request: any = {};
    if (beforeDate) request.before = beforeDate;
    if (newStatus) request.newStatus = newStatus;
    
    const response = await apiClient.post<{ syncedCount: number; beforeDate: string; newStatus: string; timestamp: string }>(
      `${this.baseUrl}/maintenance/sync-pending`,
      request
    );
    return response.data;
  }

  // ==================== Health and Monitoring ====================

  /**
   * 健康检查
   */
  async healthCheck(): Promise<{ status: string; activeEdits: number; timestamp: string }> {
    const response = await apiClient.get<{ status: string; activeEdits: number; timestamp: string }>(
      `${this.baseUrl}/health`
    );
    return response.data;
  }

  /**
   * 获取系统信息
   */
  async getSystemInfo(): Promise<{ service: string; version: string; description: string; timestamp: string }> {
    const response = await apiClient.get<{ service: string; version: string; description: string; timestamp: string }>(
      `${this.baseUrl}/info`
    );
    return response.data;
  }

  // ==================== Utility Methods ====================

  /**
   * 检查编辑是否与原始内容有差异
   */
  hasContentChanged(originalContent: string | undefined, newContent: string): boolean {
    if (originalContent === undefined && newContent === '') return false;
    if (originalContent === undefined) return true;
    return originalContent !== newContent;
  }

  /**
   * 计算编辑差异的简单实现
   */
  calculateEditDiff(originalContent: string | undefined, newContent: string): string {
    const originalLength = originalContent?.length || 0;
    const newLength = newContent.length;
    const delta = newLength - originalLength;
    
    return JSON.stringify({
      originalLength,
      newLength,
      delta,
      changed: this.hasContentChanged(originalContent, newContent),
      timestamp: new Date().toISOString()
    });
  }

  /**
   * 生成内容哈希的简单实现（生产环境应使用更安全的方法）
   */
  calculateContentHash(content: string): string {
    // Simple hash for demonstration
    let hash = 0;
    for (let i = 0; i < content.length; i++) {
      const char = content.charCodeAt(i);
      hash = ((hash << 5) - hash) + char;
      hash = hash & hash; // Convert to 32bit integer
    }
    return Math.abs(hash).toString(16);
  }

  /**
   * 验证编辑权限（简化版）
   */
  canEditMessage(userId: string, editUserId: string): boolean {
    return userId === editUserId;
  }

  /**
   * 格式化编辑原因
   */
  formatEditReason(editType: string, originalReason?: string): string {
    const reasons: Record<string, string> = {
      'CREATE': '创建消息',
      'EDIT': '编辑消息',
      'REPLACE': '替换消息',
      'CORRECT': '纠正错误',
      'ENHANCE': '增强内容',
      'ROLLBACK': '回滚版本'
    };
    
    const baseReason = reasons[editType] || '编辑消息';
    return originalReason ? `${baseReason}: ${originalReason}` : baseReason;
  }

  /**
   * 获取编辑类型的显示名称
   */
  getEditTypeDisplayName(editType: string): string {
    const displayNames: Record<string, string> = {
      'CREATE': '创建',
      'EDIT': '编辑',
      'REPLACE': '替换',
      'CORRECT': '纠正',
      'ENHANCE': '增强',
      'ROLLBACK': '回滚'
    };
    
    return displayNames[editType] || editType;
  }

  /**
   * 获取状态的显示名称
   */
  getStatusDisplayName(status: string): string {
    const displayNames: Record<string, string> = {
      'ACTIVE': '活跃',
      'DELETED': '已删除',
      'ARCHIVED': '已归档',
      'HIDDEN': '隐藏'
    };
    
    return displayNames[status] || status;
  }

  /**
   * 获取审核状态的显示名称
   */
  getAuditStatusDisplayName(auditStatus: string): string {
    const displayNames: Record<string, string> = {
      'PENDING': '待审核',
      'APPROVED': '已批准',
      'REJECTED': '已拒绝',
      'FLAGGED': '已标记'
    };
    
    return displayNames[auditStatus] || auditStatus;
  }

  /**
   * 获取隐私级别的显示名称
   */
  getPrivacyLevelDisplayName(privacyLevel: string): string {
    const displayNames: Record<string, string> = {
      'PUBLIC': '公开',
      'STANDARD': '标准',
      'PRIVATE': '私有',
      'CONFIDENTIAL': '机密'
    };
    
    return displayNames[privacyLevel] || privacyLevel;
  }
}

// Export singleton instance
export const messageEditService = new MessageEditService();