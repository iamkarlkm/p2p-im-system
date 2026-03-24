/**
 * 用户资料服务 - 桌面端
 * 负责用户资料、在线状态、好友备注的获取和更新
 */

import { WebSocketService } from './websocket';
import { ApiClient } from './api_client';

export interface UserProfile {
  userId: string;
  nickname: string;
  avatarUrl: string;
  signature: string;
  email?: string;
  phone?: string;
  gender?: string;
  birthday?: string;
  region?: string;
  language?: string;
  status: UserStatus;
  customStatus?: Record<string, unknown>;
  createdAt: string;
  updatedAt: string;
}

export type UserStatus = 'ONLINE' | 'AWAY' | 'BUSY' | 'DO_NOT_DISTURB' | 'INVISIBLE' | 'OFFLINE';

export interface FriendRemark {
  friendId: string;
  remark: string;
  groupName: string;
  tags: string[];
  updatedAt: string;
}

export interface FriendGroup {
  groupId: string;
  name: string;
  sortOrder: number;
  memberCount: number;
}

export interface ProfileUpdatePayload {
  nickname?: string;
  avatarUrl?: string;
  signature?: string;
  email?: string;
  phone?: string;
  gender?: string;
  birthday?: string;
  region?: string;
  language?: string;
  status?: UserStatus;
}

class ProfileService {
  private wsHandler: WebSocketService | null = null;
  private currentUserId: string | null = null;
  private onlineUsers: Set<string> = new Set();
  private statusListeners: Set<(data: StatusChangeEvent) => void> = new Set();

  setWebSocketHandler(ws: WebSocketService) {
    this.wsHandler = ws;
    this.wsHandler.on('status_change', (data: StatusChangeEvent) => {
      if (data.status === 'ONLINE') {
        this.onlineUsers.add(data.userId);
      } else {
        this.onlineUsers.delete(data.userId);
      }
      this.statusListeners.forEach(cb => cb(data));
    });
  }

  async getProfile(userId: string): Promise<UserProfile> {
    const resp = await ApiClient.get<UserProfile>(`/api/profile/${userId}`);
    return resp.data;
  }

  async getPublicProfile(userId: string): Promise<UserProfile> {
    const resp = await ApiClient.get<UserProfile>(`/api/profile/${userId}/public`);
    return resp.data;
  }

  async updateProfile(userId: string, payload: ProfileUpdatePayload): Promise<UserProfile> {
    const resp = await ApiClient.put<UserProfile>(`/api/profile/${userId}`, payload);
    return resp.data;
  }

  async updateAvatar(userId: string, avatarUrl: string): Promise<UserProfile> {
    const resp = await ApiClient.patch<UserProfile>(`/api/profile/${userId}/avatar`, { avatarUrl });
    return resp.data;
  }

  async updateNickname(userId: string, nickname: string): Promise<UserProfile> {
    const resp = await ApiClient.patch<UserProfile>(`/api/profile/${userId}/nickname`, { nickname });
    return resp.data;
  }

  async updateSignature(userId: string, signature: string): Promise<UserProfile> {
    const resp = await ApiClient.patch<UserProfile>(`/api/profile/${userId}/signature`, { signature });
    return resp.data;
  }

  async updateStatus(userId: string, status: UserStatus): Promise<UserProfile> {
    const resp = await ApiClient.patch<UserProfile>(`/api/profile/${userId}/status`, { status });
    this.wsHandler?.send({
      type: 'profile_update',
      action: 'status_update',
      status: status
    });
    return resp.data;
  }

  async searchUsers(keyword: string, limit = 20): Promise<UserProfile[]> {
    const resp = await ApiClient.post<UserProfile[]>('/api/profile/search', { keyword, limit });
    return resp.data;
  }

  async getProfiles(userIds: string[]): Promise<Record<string, UserProfile>> {
    const resp = await ApiClient.post<Record<string, UserProfile>>('/api/profile/batch', { userIds });
    return resp.data;
  }

  async getOnlineUsers(): Promise<string[]> {
    const resp = await ApiClient.get<string[]>('/api/profile/online');
    return resp.data;
  }

  isOnline(userId: string): boolean {
    return this.onlineUsers.has(userId);
  }

  onStatusChange(callback: (data: StatusChangeEvent) => void): () => void {
    this.statusListeners.add(callback);
    return () => this.statusListeners.delete(callback);
  }

  // 好友备注
  async setFriendRemark(userId: string, friendId: string, remark: string, groupName?: string, tags?: string[]): Promise<FriendRemark> {
    const resp = await ApiClient.put<FriendRemark>('/api/profile/friend-remark', {
      userId, friendId, remark, groupName: groupName || '', tags: tags || []
    });
    return resp.data;
  }

  async getFriendRemark(userId: string, friendId: string): Promise<FriendRemark | null> {
    try {
      const resp = await ApiClient.get<FriendRemark>(`/api/profile/${userId}/friend-remark/${friendId}`);
      return resp.data;
    } catch {
      return null;
    }
  }

  async getAllFriendRemarks(userId: string): Promise<FriendRemark[]> {
    const resp = await ApiClient.get<FriendRemark[]>(`/api/profile/${userId}/friend-remarks`);
    return resp.data;
  }

  async removeFriendRemark(userId: string, friendId: string): Promise<void> {
    await ApiClient.delete(`/api/profile/${userId}/friend-remark/${friendId}`);
  }

  // 好友分组
  async createFriendGroup(userId: string, name: string, sortOrder = 0): Promise<FriendGroup> {
    const resp = await ApiClient.post<FriendGroup>(`/api/profile/${userId}/friend-group`, { name, sortOrder });
    return resp.data;
  }

  async getFriendGroups(userId: string): Promise<FriendGroup[]> {
    const resp = await ApiClient.get<FriendGroup[]>(`/api/profile/${userId}/friend-groups`);
    return resp.data;
  }

  async deleteFriendGroup(userId: string, groupId: string): Promise<void> {
    await ApiClient.delete(`/api/profile/${userId}/friend-group/${groupId}`);
  }
}

export interface StatusChangeEvent {
  userId: string;
  status: UserStatus;
}

export const profileService = new ProfileService();
