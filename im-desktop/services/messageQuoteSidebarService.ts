import { MessageQuoteSidebar } from '../types/messageQuoteSidebar';

/**
 * 引用消息侧边栏服务
 * 提供与后端 API 的交互接口
 */
export class MessageQuoteSidebarService {
  private static instance: MessageQuoteSidebarService;
  private baseUrl: string;

  private constructor(baseUrl: string = '/api/v1') {
    this.baseUrl = baseUrl;
  }

  public static getInstance(baseUrl?: string): MessageQuoteSidebarService {
    if (!MessageQuoteSidebarService.instance) {
      MessageQuoteSidebarService.instance = new MessageQuoteSidebarService(baseUrl);
    }
    return MessageQuoteSidebarService.instance;
  }

  /**
   * 通用 API 请求方法
   */
  private async request<T>(
    endpoint: string,
    method: 'GET' | 'POST' | 'PUT' | 'DELETE' = 'GET',
    data?: any
  ): Promise<{ success: boolean; message: string; data?: T }> {
    try {
      const options: RequestInit = {
        method,
        headers: {
          'Content-Type': 'application/json',
        },
      };

      if (data && method !== 'GET') {
        options.body = JSON.stringify(data);
      }

      const url = `${this.baseUrl}${endpoint}`;
      const response = await fetch(url, options);

      if (!response.ok) {
        throw new Error(`HTTP error! status: ${response.status}`);
      }

      const result = await response.json();
      return result;
    } catch (error) {
      console.error('API request failed:', error);
      return {
        success: false,
        message: error instanceof Error ? error.message : '请求失败',
      };
    }
  }

  // ==================== 基础 CRUD 操作 ====================

  /**
   * 添加引用消息到侧边栏
   */
  async addToSidebar(
    userId: number,
    sessionId: number,
    quoteId: number
  ): Promise<{ success: boolean; message: string; data?: MessageQuoteSidebar }> {
    return this.request<MessageQuoteSidebar>(
      `/message-quote-sidebar/add?userId=${userId}&sessionId=${sessionId}&quoteId=${quoteId}`,
      'POST'
    );
  }

  /**
   * 批量添加引用消息到侧边栏
   */
  async batchAddToSidebar(
    userId: number,
    sessionId: number,
    quoteIds: number[]
  ): Promise<{ success: boolean; message: string; data?: MessageQuoteSidebar[] }> {
    return this.request<MessageQuoteSidebar[]>(
      `/message-quote-sidebar/batch-add?userId=${userId}&sessionId=${sessionId}`,
      'POST',
      quoteIds
    );
  }

  /**
   * 从侧边栏移除引用消息
   */
  async removeFromSidebar(
    userId: number,
    quoteId: number
  ): Promise<{ success: boolean; message: string }> {
    return this.request(
      `/message-quote-sidebar/remove?userId=${userId}&quoteId=${quoteId}`,
      'DELETE'
    );
  }

  /**
   * 批量从侧边栏移除引用消息
   */
  async batchRemoveFromSidebar(
    userId: number,
    quoteIds: number[]
  ): Promise<{ success: boolean; message: string }> {
    return this.request(
      `/message-quote-sidebar/batch-remove?userId=${userId}`,
      'DELETE',
      quoteIds
    );
  }

  /**
   * 清除会话的所有侧边栏记录
   */
  async clearSessionSidebar(
    userId: number,
    sessionId: number
  ): Promise<{ success: boolean; message: string }> {
    return this.request(
      `/message-quote-sidebar/clear-session?userId=${userId}&sessionId=${sessionId}`,
      'DELETE'
    );
  }

  // ==================== 查询操作 ====================

  /**
   * 获取用户的侧边栏记录列表
   */
  async getUserSidebarItems(
    userId: number,
    sessionId: number
  ): Promise<{ success: boolean; message: string; data?: MessageQuoteSidebar[] }> {
    return this.request<MessageQuoteSidebar[]>(
      `/message-quote-sidebar/user-session-items?userId=${userId}&sessionId=${sessionId}`
    );
  }

  /**
   * 获取用户的侧边栏记录列表（分页）
   */
  async getUserSidebarItemsPage(
    userId: number,
    sessionId: number,
    page: number = 0,
    size: number = 20
  ): Promise<{ success: boolean; message: string; data?: any }> {
    return this.request(
      `/message-quote-sidebar/user-session-items-page?userId=${userId}&sessionId=${sessionId}&page=${page}&size=${size}`
    );
  }

  /**
   * 获取用户最近查看的侧边栏记录
   */
  async getRecentSidebarItems(
    userId: number,
    sessionId: number
  ): Promise<{ success: boolean; message: string; data?: MessageQuoteSidebar[] }> {
    return this.request<MessageQuoteSidebar[]>(
      `/message-quote-sidebar/recent-items?userId=${userId}&sessionId=${sessionId}`
    );
  }

  /**
   * 获取用户所有固定的侧边栏记录
   */
  async getPinnedSidebarItems(
    userId: number
  ): Promise<{ success: boolean; message: string; data?: MessageQuoteSidebar[] }> {
    return this.request<MessageQuoteSidebar[]>(
      `/message-quote-sidebar/pinned-items?userId=${userId}`
    );
  }

  /**
   * 获取会话中固定的侧边栏记录
   */
  async getPinnedSidebarItemsInSession(
    userId: number,
    sessionId: number
  ): Promise<{ success: boolean; message: string; data?: MessageQuoteSidebar[] }> {
    return this.request<MessageQuoteSidebar[]>(
      `/message-quote-sidebar/pinned-items-in-session?userId=${userId}&sessionId=${sessionId}`
    );
  }

  /**
   * 检查引用消息是否已在侧边栏
   */
  async checkInSidebar(
    userId: number,
    quoteId: number
  ): Promise<{ success: boolean; message: string; data?: { isInSidebar: boolean } }> {
    return this.request(
      `/message-quote-sidebar/check-in-sidebar?userId=${userId}&quoteId=${quoteId}`
    );
  }

  /**
   * 获取侧边栏记录详情
   */
  async getSidebarItem(
    userId: number,
    quoteId: number
  ): Promise<{ success: boolean; message: string; data?: MessageQuoteSidebar }> {
    return this.request<MessageQuoteSidebar>(
      `/message-quote-sidebar/item-detail?userId=${userId}&quoteId=${quoteId}`
    );
  }

  // ==================== 更新操作 ====================

  /**
   * 切换固定状态
   */
  async togglePinStatus(
    userId: number,
    quoteId: number,
    isPinned: boolean
  ): Promise<{ success: boolean; message: string; data?: MessageQuoteSidebar }> {
    return this.request<MessageQuoteSidebar>(
      `/message-quote-sidebar/toggle-pin?userId=${userId}&quoteId=${quoteId}&isPinned=${isPinned}`,
      'PUT'
    );
  }

  /**
   * 批量更新固定状态
   */
  async batchTogglePinStatus(
    userId: number,
    quoteIds: number[],
    isPinned: boolean
  ): Promise<{ success: boolean; message: string }> {
    return this.request(
      `/message-quote-sidebar/batch-toggle-pin?userId=${userId}&isPinned=${isPinned}`,
      'PUT',
      quoteIds
    );
  }

  /**
   * 更新侧边栏位置索引
   */
  async updateSidebarIndex(
    userId: number,
    quoteId: number,
    newIndex: number
  ): Promise<{ success: boolean; message: string; data?: MessageQuoteSidebar }> {
    return this.request<MessageQuoteSidebar>(
      `/message-quote-sidebar/update-index?userId=${userId}&quoteId=${quoteId}&newIndex=${newIndex}`,
      'PUT'
    );
  }

  /**
   * 批量更新侧边栏位置索引
   */
  async batchUpdateSidebarIndices(
    userId: number,
    quoteIds: number[],
    indices: number[]
  ): Promise<{ success: boolean; message: string }> {
    return this.request(
      `/message-quote-sidebar/batch-update-indices?userId=${userId}`,
      'PUT',
      { quoteIds, indices }
    );
  }

  /**
   * 更新最后查看时间
   */
  async updateLastViewedAt(
    userId: number,
    quoteId: number
  ): Promise<{ success: boolean; message: string }> {
    return this.request(
      `/message-quote-sidebar/update-last-viewed?userId=${userId}&quoteId=${quoteId}`,
      'PUT'
    );
  }

  /**
   * 批量更新最后查看时间
   */
  async batchUpdateLastViewedAt(
    userId: number,
    quoteIds: number[]
  ): Promise<{ success: boolean; message: string }> {
    return this.request(
      `/message-quote-sidebar/batch-update-last-viewed?userId=${userId}`,
      'PUT',
      quoteIds
    );
  }

  // ==================== 搜索功能 ====================

  /**
   * 搜索侧边栏记录
   */
  async searchSidebarItems(
    userId: number,
    keyword: string
  ): Promise<{ success: boolean; message: string; data?: MessageQuoteSidebar[] }> {
    return this.request<MessageQuoteSidebar[]>(
      `/message-quote-sidebar/search?userId=${userId}&keyword=${encodeURIComponent(keyword)}`
    );
  }

  /**
   * 按消息类型搜索侧边栏记录
   */
  async searchByMessageType(
    userId: number,
    messageType: string
  ): Promise<{ success: boolean; message: string; data?: MessageQuoteSidebar[] }> {
    return this.request<MessageQuoteSidebar[]>(
      `/message-quote-sidebar/search-by-type?userId=${userId}&messageType=${encodeURIComponent(messageType)}`
    );
  }

  /**
   * 按发送者搜索侧边栏记录
   */
  async searchBySender(
    userId: number,
    senderId: number
  ): Promise<{ success: boolean; message: string; data?: MessageQuoteSidebar[] }> {
    return this.request<MessageQuoteSidebar[]>(
      `/message-quote-sidebar/search-by-sender?userId=${userId}&senderId=${senderId}`
    );
  }

  // ==================== 统计功能 ====================

  /**
   * 统计用户侧边栏记录数量
   */
  async countUserSidebarItems(
    userId: number
  ): Promise<{ success: boolean; message: string; data?: { count: number; userId: number } }> {
    return this.request(
      `/message-quote-sidebar/count-user-items?userId=${userId}`
    );
  }

  /**
   * 统计会话侧边栏记录数量
   */
  async countSessionSidebarItems(
    sessionId: number
  ): Promise<{ success: boolean; message: string; data?: { count: number; sessionId: number } }> {
    return this.request(
      `/message-quote-sidebar/count-session-items?sessionId=${sessionId}`
    );
  }

  /**
   * 统计用户固定侧边栏记录数量
   */
  async countPinnedSidebarItems(
    userId: number
  ): Promise<{ success: boolean; message: string; data?: { count: number; userId: number } }> {
    return this.request(
      `/message-quote-sidebar/count-pinned-items?userId=${userId}`
    );
  }

  /**
   * 按消息类型统计侧边栏记录
   */
  async getSidebarStatsByMessageType(
    userId: number
  ): Promise<{ success: boolean; message: string; data?: any[] }> {
    return this.request(
      `/message-quote-sidebar/stats-by-type?userId=${userId}`
    );
  }

  // ==================== 清理和维护操作 ====================

  /**
   * 清理超过指定天数未查看的非固定侧边栏记录
   */
  async cleanupStaleSidebarItems(
    daysThreshold: number = 30
  ): Promise<{ success: boolean; message: string; data?: { cleanedCount: number; daysThreshold: number } }> {
    return this.request(
      `/message-quote-sidebar/cleanup-stale?daysThreshold=${daysThreshold}`,
      'DELETE'
    );
  }

  /**
   * 自动清理侧边栏记录
   */
  async autoCleanupSidebarItems(): Promise<{ 
    success: boolean; 
    message: string; 
    data?: { cleanedCount: number; thresholdDays: number } 
  }> {
    return this.request(
      `/message-quote-sidebar/auto-cleanup`,
      'DELETE'
    );
  }

  // ==================== 管理功能 ====================

  /**
   * 获取所有侧边栏记录（管理用）
   */
  async getAllSidebarItems(): Promise<{ 
    success: boolean; 
    message: string; 
    data?: MessageQuoteSidebar[] 
  }> {
    return this.request<MessageQuoteSidebar[]>(
      `/message-quote-sidebar/admin/all-items`
    );
  }

  /**
   * 获取所有侧边栏记录（分页，管理用）
   */
  async getAllSidebarItemsPage(
    page: number = 0,
    size: number = 50
  ): Promise<{ success: boolean; message: string; data?: any }> {
    return this.request(
      `/message-quote-sidebar/admin/all-items-page?page=${page}&size=${size}`
    );
  }
}

// 导出单例实例
export const messageQuoteSidebarService = MessageQuoteSidebarService.getInstance();
