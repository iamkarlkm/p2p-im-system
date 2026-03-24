/**
 * 用户资料服务
 * 处理用户资料、在线状态、好友备注等API调用
 */

import { ApiClient } from './api-client';
import {
  UserProfile,
  ProfileUpdateRequest,
  OnlineStatusRequest,
  OnlineStatus,
  FriendGroup,
  FriendRemark,
  FriendRemarkRequest
} from '../types/user-profile';

const api = ApiClient.getInstance();

class UserProfileService {
  
  /**
   * 获取当前用户资料
   */
  async getMyProfile(): Promise<UserProfile> {
    return api.get<UserProfile>('/api/v1/profile/me');
  }

  /**
   * 获取指定用户资料
   */
  async getProfile(userId: number): Promise<UserProfile> {
    return api.get<UserProfile>(`/api/v1/profile/${userId}`);
  }

  /**
   * 批量获取用户资料
   */
  async getProfiles(userIds: number[]): Promise<UserProfile[]> {
    return api.post<UserProfile[]>('/api/v1/profile/batch', { userIds });
  }

  /**
   * 更新个人资料
   */
  async updateProfile(request: ProfileUpdateRequest): Promise<UserProfile> {
    return api.put<UserProfile>('/api/v1/profile/me', request);
  }

  /**
   * 更新在线状态
   */
  async updateOnlineStatus(request: OnlineStatusRequest): Promise<UserProfile> {
    return api.put<UserProfile>('/api/v1/profile/me/status', request);
  }

  /**
   * 上传头像
   */
  async uploadAvatar(fileData: string): Promise<string> {
    const response = await api.post<{ avatarUrl: string }>('/api/v1/profile/me/avatar', { fileData });
    return response.avatarUrl;
  }

  /**
   * 获取好友分组列表
   */
  async getFriendGroups(): Promise<FriendGroup[]> {
    return api.get<FriendGroup[]>('/api/v1/profile/me/friend-groups');
  }

  /**
   * 创建好友分组
   */
  async createFriendGroup(groupName: string): Promise<FriendGroup> {
    return api.post<FriendGroup>('/api/v1/profile/me/friend-groups', { groupName });
  }

  /**
   * 更新好友备注
   */
  async updateFriendRemark(request: FriendRemarkRequest): Promise<FriendRemark> {
    return api.put<FriendRemark>(
      `/api/v1/profile/me/friend-remarks/${request.friendId}`,
      request
    );
  }

  /**
   * 获取好友备注
   */
  async getFriendRemark(friendId: number): Promise<FriendRemark | null> {
    try {
      return await api.get<FriendRemark>(`/api/v1/profile/me/friend-remarks/${friendId}`);
    } catch {
      return null;
    }
  }

  /**
   * 快速设置在线状态
   */
  async setOnlineStatus(status: OnlineStatus): Promise<void> {
    await this.updateOnlineStatus({ status });
  }

  /**
   * 设置自定义状态
   */
  async setCustomStatus(status: OnlineStatus, statusText: string): Promise<void> {
    await this.updateOnlineStatus({ status, statusText });
  }

  /**
   * 获取所有在线用户的ID列表
   */
  async getOnlineUsers(): Promise<number[]> {
    // 通过批量获取资料，检查哪些用户在线
    const profile = await this.getMyProfile();
    // 实际应通过WebSocket或专门的API获取
    return [profile.userId];
  }
}

export const userProfileService = new UserProfileService();
