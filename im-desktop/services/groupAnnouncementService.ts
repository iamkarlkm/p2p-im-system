import { apiClient } from '../api/client';

/**
 * 群公告服务
 * 功能ID: #30
 * 功能名称: 群公告
 * 
 * @author developer-agent
 * @since 2026-03-30
 */

export interface GroupAnnouncement {
  id: number;
  groupId: number;
  title: string;
  content: string;
  creatorId: number;
  creatorNickname: string;
  creatorAvatar: string;
  pinned: boolean;
  pinnedAt: string;
  confirmed: boolean;
  readCount: number;
  totalMembers: number;
  readPercentage: number;
  attachments: string[];
  createdAt: string;
  updatedAt: string;
  hasRead: boolean;
}

export interface CreateAnnouncementRequest {
  groupId: number;
  title: string;
  content: string;
  pinned?: boolean;
  attachments?: string[];
}

export interface UpdateAnnouncementRequest {
  title: string;
  content: string;
  pinned?: boolean;
  attachments?: string[];
}

export const groupAnnouncementService = {
  /**
   * 创建群公告
   */
  createAnnouncement: async (data: CreateAnnouncementRequest): Promise<GroupAnnouncement> => {
    const response = await apiClient.post<GroupAnnouncement>('/group-announcement/create', data);
    return response.data;
  },

  /**
   * 更新群公告
   */
  updateAnnouncement: async (id: number, data: UpdateAnnouncementRequest): Promise<GroupAnnouncement> => {
    const response = await apiClient.put<GroupAnnouncement>(`/group-announcement/${id}`, data);
    return response.data;
  },

  /**
   * 删除群公告
   */
  deleteAnnouncement: async (id: number): Promise<void> => {
    await apiClient.delete(`/group-announcement/${id}`);
  },

  /**
   * 获取公告详情
   */
  getAnnouncement: async (id: number): Promise<GroupAnnouncement> => {
    const response = await apiClient.get<GroupAnnouncement>(`/group-announcement/${id}`);
    return response.data;
  },

  /**
   * 获取群组的公告列表
   */
  getGroupAnnouncements: async (groupId: number): Promise<GroupAnnouncement[]> => {
    const response = await apiClient.get<GroupAnnouncement[]>(`/group-announcement/group/${groupId}`);
    return response.data;
  },

  /**
   * 分页获取群公告
   */
  getGroupAnnouncementsPaged: async (groupId: number, page: number = 0, size: number = 20): Promise<{
    content: GroupAnnouncement[];
    totalElements: number;
    totalPages: number;
  }> => {
    const response = await apiClient.get(`/group-announcement/group/${groupId}/paged`, {
      params: { page, size }
    });
    return response.data;
  },

  /**
   * 获取最新公告
   */
  getLatestAnnouncement: async (groupId: number): Promise<GroupAnnouncement | null> => {
    try {
      const response = await apiClient.get<GroupAnnouncement>(`/group-announcement/group/${groupId}/latest`);
      return response.data;
    } catch (error: any) {
      if (error.response?.status === 404) {
        return null;
      }
      throw error;
    }
  },

  /**
   * 获取置顶公告
   */
  getPinnedAnnouncements: async (groupId: number): Promise<GroupAnnouncement[]> => {
    const response = await apiClient.get<GroupAnnouncement[]>(`/group-announcement/group/${groupId}/pinned`);
    return response.data;
  },

  /**
   * 标记公告已读
   */
  markAsRead: async (id: number): Promise<void> => {
    await apiClient.post(`/group-announcement/${id}/read`);
  },

  /**
   * 批量标记已读
   */
  markAllAsRead: async (groupId: number): Promise<void> => {
    await apiClient.post(`/group-announcement/group/${groupId}/read-all`);
  },

  /**
   * 置顶/取消置顶公告
   */
  pinAnnouncement: async (id: number, pinned: boolean): Promise<void> => {
    await apiClient.post(`/group-announcement/${id}/pin`, null, {
      params: { pinned }
    });
  },

  /**
   * 获取已读人数
   */
  getReadCount: async (id: number): Promise<number> => {
    const response = await apiClient.get<{ readCount: number }>(`/group-announcement/${id}/read-count`);
    return response.data.readCount;
  },

  /**
   * 搜索公告
   */
  searchAnnouncements: async (groupId: number, keyword: string): Promise<GroupAnnouncement[]> => {
    const response = await apiClient.get<GroupAnnouncement[]>(`/group-announcement/group/${groupId}/search`, {
      params: { keyword }
    });
    return response.data;
  },

  /**
   * 获取未读公告数量
   */
  getUnreadCount: async (groupId: number): Promise<number> => {
    const response = await apiClient.get<{ unreadCount: number }>(`/group-announcement/group/${groupId}/unread-count`);
    return response.data.unreadCount;
  },

  /**
   * 检查用户是否为创建者
   */
  isCreator: async (id: number): Promise<boolean> => {
    const response = await apiClient.get<{ isCreator: boolean }>(`/group-announcement/${id}/is-creator`);
    return response.data.isCreator;
  }
};
