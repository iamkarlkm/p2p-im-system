/**
 * 用户黑名单服务
 * 支持拉黑/解黑用户、批量检查黑名单状态
 */

import { apiClient } from './apiClient';

export interface BlockedUserDTO {
  id: number;
  blockedId: number;
  blockedUsername: string | null;
  blockedAvatar: string | null;
  reason: string | null;
  blockedAt: string;
  hideOnlineStatus: boolean;
  muteMessages: boolean;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

class BlockedUserService {
  /**
   * 获取黑名单列表
   */
  async getBlockedUsers(): Promise<BlockedUserDTO[]> {
    const response = await apiClient.get<ApiResponse<BlockedUserDTO[]>>('/api/blocked-users');
    return response.data.data;
  }

  /**
   * 获取黑名单ID列表
   */
  async getBlockedUserIds(): Promise<number[]> {
    const response = await apiClient.get<ApiResponse<number[]>>('/api/blocked-users/ids');
    return response.data.data;
  }

  /**
   * 获取黑名单数量
   */
  async getBlockedCount(): Promise<number> {
    const response = await apiClient.get<ApiResponse<number>>('/api/blocked-users/count');
    return response.data.data;
  }

  /**
   * 拉黑用户
   */
  async blockUser(blockedId: number, reason?: string): Promise<BlockedUserDTO> {
    const response = await apiClient.post<ApiResponse<BlockedUserDTO>>(
      `/api/blocked-users/${blockedId}`,
      { reason }
    );
    return response.data.data;
  }

  /**
   * 解除拉黑
   */
  async unblockUser(blockedId: number): Promise<void> {
    await apiClient.delete<ApiResponse<null>>(`/api/blocked-users/${blockedId}`);
  }

  /**
   * 检查是否拉黑了指定用户
   */
  async checkBlocked(blockedId: number): Promise<boolean> {
    const response = await apiClient.get<ApiResponse<boolean>>(
      `/api/blocked-users/check/${blockedId}`
    );
    return response.data.data;
  }

  /**
   * 批量检查是否在黑名单中
   */
  async checkBlockedBatch(userIds: number[]): Promise<number[]> {
    const response = await apiClient.post<ApiResponse<number[]>>(
      '/api/blocked-users/check-batch',
      userIds
    );
    return response.data.data;
  }

  /**
   * 检查双向拉黑
   */
  async checkMutualBlock(userId: number): Promise<boolean> {
    const response = await apiClient.get<ApiResponse<boolean>>(
      `/api/blocked-users/mutual/${userId}`
    );
    return response.data.data;
  }

  /**
   * 格式化拉黑时间
   */
  formatBlockedTime(blockedAt: string): string {
    const date = new Date(blockedAt);
    const now = new Date();
    const diffMs = now.getTime() - date.getTime();
    const diffDays = Math.floor(diffMs / (1000 * 60 * 60 * 24));
    
    if (diffDays === 0) {
      return '今天';
    } else if (diffDays === 1) {
      return '昨天';
    } else if (diffDays < 7) {
      return `${diffDays} 天前`;
    } else if (diffDays < 30) {
      return `${Math.floor(diffDays / 7)} 周前`;
    } else if (diffDays < 365) {
      return `${Math.floor(diffDays / 30)} 个月前`;
    } else {
      return `${Math.floor(diffDays / 365)} 年前`;
    }
  }

  /**
   * 判断用户是否在本地缓存的黑名单中
   */
  isUserBlockedLocally(blockedIds: Set<number>, userId: number): boolean {
    return blockedIds.has(userId);
  }
}

export const blockedUserService = new BlockedUserService();
