import { apiRequest } from './apiClient';
import type { Announcement } from '../types/message';

export const announcementService = {
  /** 获取群公告列表 */
  async getGroupAnnouncements(groupId: number): Promise<Announcement[]> {
    return apiRequest(`/announcements/group/${groupId}`);
  },

  /** 分页获取公告 */
  async getAnnouncementsPaged(groupId: number, page = 0, size = 20): Promise<any> {
    return apiRequest(`/announcements/group/${groupId}/page?page=${page}&size=${size}`);
  },

  /** 发布公告 */
  async publish(data: {
    groupId: number;
    authorId: number;
    title?: string;
    content: string;
    pinned?: boolean;
    requiredRead?: boolean;
    urgent?: boolean;
    type?: string;
    expireTime?: string;
    attachments?: number[];
  }): Promise<Announcement> {
    return apiRequest('/announcements', { method: 'POST', body: JSON.stringify(data) });
  },

  /** 标记已读 */
  async markAsRead(announcementId: number, userId: number, deviceType?: string): Promise<void> {
    return apiRequest(`/announcements/${announcementId}/read`, {
      method: 'POST',
      headers: { 'X-User-Id': String(userId), 'X-Device-Type': deviceType || 'web' },
    });
  },

  /** 确认紧急公告 */
  async confirmAnnouncement(announcementId: number, userId: number): Promise<void> {
    return apiRequest(`/announcements/${announcementId}/confirm`, {
      method: 'POST',
      headers: { 'X-User-Id': String(userId) },
    });
  },

  /** 获取已读统计 */
  async getReadStats(announcementId: number, totalMembers: number): Promise<{
    readCount: number;
    totalMemberCount: number;
    unreadCount: number;
    readRate: number;
  }> {
    return apiRequest(`/announcements/${announcementId}/stats?totalMembers=${totalMembers}`);
  },

  /** 撤销公告 */
  async revokeAnnouncement(announcementId: number, userId: number): Promise<void> {
    return apiRequest(`/announcements/${announcementId}`, {
      method: 'DELETE',
      headers: { 'X-User-Id': String(userId) },
    });
  },
};

export interface Announcement {
  id: number;
  groupId: number;
  authorId: number;
  authorName?: string;
  title?: string;
  content: string;
  pinned: boolean;
  requiredRead: boolean;
  urgent: boolean;
  attachmentIds?: number[];
  type: 'normal' | 'rule' | 'notice' | 'event';
  publishTime: string;
  expireTime?: string;
  isRead: boolean;
  isConfirmed: boolean;
  readCount?: number;
  totalMemberCount?: number;
  createdAt: string;
}
