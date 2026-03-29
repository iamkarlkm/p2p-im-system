/**
 * 消息已读回执服务 - 桌面端
 */

import { apiClient } from './api_client';
import { wsClient } from './websocket';

// 已读回执数据
export interface ReadReceipt {
  id: number;
  messageId: number;
  userId: number;
  conversationId: number;
  readAt: string;
  readStatus: string;
}

// 已读统计
export interface ReadStatistics {
  messageId: number;
  totalReads: number;
  readUserIds: number[];
  firstReadAt: string | null;
  lastReadAt: string | null;
}

class ReadReceiptService {
  private unreadCounts: Map<string, number> = new Map();

  constructor() {
    this.initWebSocket();
  }

  /**
   * 初始化WebSocket监听
   */
  private initWebSocket() {
    wsClient.on('read_receipt:new', (data: any) => {
      this.handleNewReadReceipt(data);
    });

    wsClient.on('read_receipt:unread_update', (data: any) => {
      this.handleUnreadUpdate(data);
    });
  }

  /**
   * 标记消息已读
   */
  async markAsRead(messageId: number, userId: number): Promise<ReadReceipt> {
    const response = await apiClient.post<any>('/read-receipts/mark-read', null, {
      params: { messageId, userId },
    });

    return response.data;
  }

  /**
   * 标记会话中所有消息已读
   */
  async markConversationAsRead(conversationId: number, userId: number): Promise<ReadReceipt[]> {
    const response = await apiClient.post<any>('/read-receipts/mark-conversation-read', null, {
      params: { conversationId, userId },
    });

    // 更新本地未读计数
    this.setUnreadCount(conversationId, userId, 0);

    return response.data;
  }

  /**
   * 批量标记已读
   */
  async batchMarkAsRead(messageIds: number[], userId: number): Promise<number> {
    const response = await apiClient.post<any>('/read-receipts/batch-mark-read', {
      messageIds,
      userId,
    });

    return response.data.count;
  }

  /**
   * 获取消息的已读用户列表
   */
  async getReadReceipts(messageId: number): Promise<ReadReceipt[]> {
    const response = await apiClient.get<any>(`/read-receipts/message/${messageId}`);
    return response.data;
  }

  /**
   * 检查消息是否已被阅读
   */
  async isReadByUser(messageId: number, userId: number): Promise<boolean> {
    const response = await apiClient.get<any>('/read-receipts/check', {
      params: { messageId, userId },
    });

    return response.data.isRead;
  }

  /**
   * 获取会话未读消息数量
   */
  async getUnreadCount(conversationId: number, userId: number): Promise<number> {
    const response = await apiClient.get<any>('/read-receipts/unread-count', {
      params: { conversationId, userId },
    });

    const count = response.data.count;
    this.setUnreadCount(conversationId, userId, count);

    return count;
  }

  /**
   * 获取多个会话的未读数量
   */
  async getUnreadCounts(conversationIds: number[], userId: number): Promise<Map<number, number>> {
    const response = await apiClient.post<any>('/read-receipts/unread-counts', {
      conversationIds,
      userId,
    });

    const counts = response.data as Map<number, number>;
    
    // 更新本地缓存
    for (const [convId, count] of Object.entries(counts)) {
      this.setUnreadCount(parseInt(convId), userId, count);
    }

    return counts;
  }

  /**
   * 获取已读统计信息
   */
  async getReadStatistics(messageId: number): Promise<ReadStatistics> {
    const response = await apiClient.get<any>(`/read-receipts/statistics/${messageId}`);
    return response.data;
  }

  /**
   * 获取用户的所有未读会话
   */
  async getUnreadConversations(userId: number): Promise<number[]> {
    const response = await apiClient.get<any>('/read-receipts/unread-conversations', {
      params: { userId },
    });

    return response.data;
  }

  /**
   * 获取本地未读计数
   */
  getLocalUnreadCount(conversationId: number, userId: number): number {
    const key = `${conversationId}:${userId}`;
    return this.unreadCounts.get(key) || 0;
  }

  /**
   * 设置本地未读计数
   */
  setUnreadCount(conversationId: number, userId: number, count: number): void {
    const key = `${conversationId}:${userId}`;
    this.unreadCounts.set(key, count);
  }

  /**
   * 减少未读计数
   */
  decrementUnreadCount(conversationId: number, userId: number, amount: number = 1): void {
    const key = `${conversationId}:${userId}`;
    const current = this.unreadCounts.get(key) || 0;
    this.unreadCounts.set(key, Math.max(0, current - amount));
  }

  /**
   * 增加未读计数
   */
  incrementUnreadCount(conversationId: number, userId: number, amount: number = 1): void {
    const key = `${conversationId}:${userId}`;
    const current = this.unreadCounts.get(key) || 0;
    this.unreadCounts.set(key, current + amount);
  }

  /**
   * 处理新的已读回执
   */
  private handleNewReadReceipt(data: any): void {
    const { messageId, userId, readAt } = data;
    
    // 触发事件
    window.dispatchEvent(new CustomEvent('read_receipt', {
      detail: { messageId, userId, readAt },
    }));
  }

  /**
   * 处理未读数更新
   */
  private handleUnreadUpdate(data: any): void {
    const { conversationId, userId, count } = data;
    this.setUnreadCount(conversationId, userId, count);
    
    // 触发事件
    window.dispatchEvent(new CustomEvent('unread_update', {
      detail: { conversationId, userId, count },
    }));
  }

  /**
   * 渲染已读状态徽章
   */
  renderReadStatus(container: HTMLElement, messageId: number, readCount: number): void {
    if (readCount === 0) {
      container.innerHTML = '';
      return;
    }

    container.innerHTML = `
      <div class="read-status-badge">
        <span class="read-icon">✓✓</span>
        <span class="read-count">${readCount}</span>
      </div>
    `;
  }

  /**
   * 格式化已读时间
   */
  formatReadTime(readAt: string): string {
    const date = new Date(readAt);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    
    const minutes = Math.floor(diff / 60000);
    const hours = Math.floor(diff / 3600000);
    const days = Math.floor(diff / 86400000);
    
    if (minutes < 1) return '刚刚';
    if (minutes < 60) return `${minutes}分钟前`;
    if (hours < 24) return `${hours}小时前`;
    if (days < 7) return `${days}天前`;
    
    return date.toLocaleDateString();
  }
}

export const readReceiptService = new ReadReceiptService();
