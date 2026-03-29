import axios from 'axios';
import type { 
  FriendGroup, 
  FriendGroupMember,
  FriendGroupWithMembers,
  CreateGroupRequest,
  UpdateGroupRequest,
  MoveFriendToGroupRequest,
  UpdateMemberRequest 
} from '../types/friendGroup';

const API_BASE = '/api/v1/friend-groups';

/**
 * 创建好友分组
 */
export const createFriendGroup = async (data: CreateGroupRequest): Promise<FriendGroup> => {
  const response = await axios.post(API_BASE, data);
  return response.data;
};

/**
 * 获取所有分组
 */
export const getFriendGroups = async (): Promise<FriendGroup[]> => {
  const response = await axios.get(API_BASE);
  return response.data;
};

/**
 * 获取分组详情（包含成员）
 */
export const getFriendGroupWithMembers = async (groupId: string): Promise<FriendGroupWithMembers> => {
  const response = await axios.get(`${API_BASE}/${groupId}/members`);
  return response.data;
};

/**
 * 更新分组信息
 */
export const updateFriendGroup = async (groupId: string, data: UpdateGroupRequest): Promise<FriendGroup> => {
  const response = await axios.put(`${API_BASE}/${groupId}`, data);
  return response.data;
};

/**
 * 删除分组
 */
export const deleteFriendGroup = async (groupId: string): Promise<void> => {
  await axios.delete(`${API_BASE}/${groupId}`);
};

/**
 * 添加好友到分组
 */
export const addFriendToGroup = async (groupId: string, friendId: string): Promise<FriendGroupMember> => {
  const response = await axios.post(`${API_BASE}/${groupId}/members`, { friendId });
  return response.data;
};

/**
 * 从分组移除好友
 */
export const removeFriendFromGroup = async (groupId: string, friendId: string): Promise<void> => {
  await axios.delete(`${API_BASE}/${groupId}/members/${friendId}`);
};

/**
 * 移动好友到其他分组
 */
export const moveFriendToGroup = async (data: MoveFriendToGroupRequest): Promise<void> => {
  await axios.post(`${API_BASE}/move-friend`, data);
};

/**
 * 更新分组成员设置
 */
export const updateGroupMember = async (
  groupId: string, 
  friendId: string, 
  data: UpdateMemberRequest
): Promise<FriendGroupMember> => {
  const response = await axios.put(`${API_BASE}/${groupId}/members/${friendId}`, data);
  return response.data;
};

/**
 * 对分组重新排序
 */
export const reorderGroups = async (groupIds: string[]): Promise<void> => {
  await axios.post(`${API_BASE}/reorder`, { groupIds });
};

/**
 * 对分组内好友排序
 */
export const reorderGroupMembers = async (groupId: string, friendIds: string[]): Promise<void> => {
  await axios.post(`${API_BASE}/${groupId}/members/reorder`, { friendIds });
};
