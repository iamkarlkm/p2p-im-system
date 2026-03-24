/**
 * 用户资料类型定义
 */

export interface UserProfile {
  userId: number;
  nickname: string;
  avatarUrl: string;
  bio: string;
  gender: 0 | 1 | 2; // 0-未知, 1-男, 2-女
  birthday?: string;
  email?: string;
  phone?: string;
  onlineStatus: OnlineStatus;
  statusText?: string;
  country?: string;
  city?: string;
  language?: string;
  timezone?: string;
  updatedAt: string;
}

export type OnlineStatus = 'ONLINE' | 'AWAY' | 'BUSY' | 'DND' | 'INVISIBLE' | 'OFFLINE';

export interface FriendGroup {
  id: number;
  userId: number;
  groupName: string;
  sortOrder: number;
  createdAt: string;
  updatedAt: string;
}

export interface FriendRemark {
  id: number;
  userId: number;
  friendId: number;
  remarkName?: string;
  groupId?: number;
  isPinned: boolean;
  addedAt: string;
  updatedAt: string;
}

export interface ProfileUpdateRequest {
  nickname?: string;
  avatarUrl?: string;
  bio?: string;
  gender?: number;
  birthday?: string;
  email?: string;
  country?: string;
  city?: string;
  language?: string;
  timezone?: string;
}

export interface OnlineStatusRequest {
  status: OnlineStatus;
  statusText?: string;
}

export interface FriendRemarkRequest {
  friendId: number;
  remarkName?: string;
  groupId?: number;
  isPinned?: boolean;
}

// 在线状态对应的UI显示
export const OnlineStatusLabels: Record<OnlineStatus, string> = {
  ONLINE: '在线',
  AWAY: '离开',
  BUSY: '忙碌',
  DND: '请勿打扰',
  INVISIBLE: '隐身',
  OFFLINE: '离线'
};

// 在线状态对应的颜色
export const OnlineStatusColors: Record<OnlineStatus, string> = {
  ONLINE: '#52c41a',
  AWAY: '#faad14',
  BUSY: '#f5222d',
  DND: '#f5222d',
  INVISIBLE: '#d9d9d9',
  OFFLINE: '#d9d9d9'
};
