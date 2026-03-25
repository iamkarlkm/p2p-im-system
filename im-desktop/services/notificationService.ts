import { apiClient } from './apiClient';

export interface NotificationItem {
  id: number;
  userId: number;
  type: 'SYSTEM' | 'FRIEND_REQUEST' | 'GROUP_INVITE' | 'MESSAGE' | 'VOTE' | 'ANNOUNCEMENT' | 'SECURITY';
  title: string;
  content: string;
  refType: string | null;
  refId: number | null;
  conversationId: number | null;
  senderId: number | null;
  senderNickname: string;
  senderAvatar: string | null;
  isRead: boolean;
  readAt: string | null;
  isHandled: boolean;
  handleResult: string | null;
  dndLevel: 'ALL' | 'PRIORITY_ONLY' | 'NONE';
  expiresAt: string | null;
  extraData: string | null;
  createdAt: string;
}

export interface UnreadStats {
  total: number;
  byType: Record<string, number>;
}

class NotificationService {
  /**
   * 获取通知列表
   */
  async getNotifications(params: {
    page?: number;
    size?: number;
    type?: string;
    isRead?: boolean;
  }): Promise<NotificationItem[]> {
    const query = new URLSearchParams({
      page: String(params.page ?? 0),
      size: String(params.size ?? 20),
    });
    if (params.type) query.set('type', params.type);
    if (params.isRead !== undefined) query.set('isRead', String(params.isRead));

    const resp = await apiClient.get(`/api/notifications?${query.toString()}`);
    return resp.content ?? [];
  }

  /**
   * 获取未读统计
   */
  async getUnreadCount(): Promise<UnreadStats> {
    return apiClient.get('/api/notifications/unread-count');
  }

  /**
   * 获取各类型未读数量
   */
  async getStats(): Promise<Record<string, number>> {
    return apiClient.get('/api/notifications/stats');
  }

  /**
   * 标记单条已读
   */
  async markAsRead(id: number): Promise<boolean> {
    const result = await apiClient.post(`/api/notifications/${id}/read`, {});
    return result.success === true;
  }

  /**
   * 批量标记已读
   */
  async batchMarkAsRead(ids: number[]): Promise<number> {
    const result = await apiClient.post('/api/notifications/read-batch', { ids });
    return result.count ?? 0;
  }

  /**
   * 全部标记已读
   */
  async markAllAsRead(): Promise<number> {
    const result = await apiClient.post('/api/notifications/read-all', {});
    return result.count ?? 0;
  }

  /**
   * 处理通知 (接受/拒绝好友请求或群邀请)
   */
  async handleNotification(id: number, result: 'ACCEPTED' | 'REJECTED'): Promise<boolean> {
    const resp = await apiClient.post(`/api/notifications/${id}/handle`, { result });
    return resp.success === true;
  }

  /**
   * 获取通知类型的中文标签
   */
  getTypeLabel(type: string): string {
    const map: Record<string, string> = {
      SYSTEM: '系统通知',
      FRIEND_REQUEST: '好友请求',
      GROUP_INVITE: '群聊邀请',
      MESSAGE: '新消息',
      VOTE: '投票提醒',
      ANNOUNCEMENT: '群公告',
      SECURITY: '安全通知',
    };
    return map[type] ?? type;
  }

  /**
   * 获取通知类型的图标
   */
  getTypeIcon(type: string): string {
    const map: Record<string, string> = {
      SYSTEM: '🔔',
      FRIEND_REQUEST: '👤',
      GROUP_INVITE: '👥',
      MESSAGE: '💬',
      VOTE: '🗳️',
      ANNOUNCEMENT: '📢',
      SECURITY: '🔒',
    };
    return map[type] ?? '🔔';
  }

  /**
   * 格式化时间
   */
  formatTime(isoString: string): string {
    const date = new Date(isoString);
    const now = new Date();
    const diff = now.getTime() - date.getTime();
    const seconds = Math.floor(diff / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const days = Math.floor(hours / 24);

    if (seconds < 60) return '刚刚';
    if (minutes < 60) return `${minutes} 分钟前`;
    if (hours < 24) return `${hours} 小时前`;
    if (days < 7) return `${days} 天前`;
    return date.toLocaleDateString('zh-CN');
  }
}

export const notificationService = new NotificationService();
