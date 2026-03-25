import axios from 'axios';
import { AtMention, AtMentionRequest, AtMentionSettings, AtMentionListResponse, AtMentionCountResponse } from '../types/at-mention';
import { useAuthStore } from '../stores/auth-store';

const API_BASE = '/api/at-mention';

function getHeaders() {
  const token = useAuthStore.getState().token;
  return { Authorization: `Bearer ${token}` };
}

/**
 * 处理消息@提及
 */
export async function processMentions(request: AtMentionRequest): Promise<AtMention[]> {
  const response = await axios.post(`${API_BASE}/process`, request, { headers: getHeaders() });
  return response.data.data;
}

/**
 * 获取用户@提及列表
 */
export async function getMentionList(userId: number, page = 0, size = 20): Promise<{ data: AtMention[], totalPages: number, totalElements: number }> {
  const response = await axios.get<AtMentionListResponse>(`${API_BASE}/list`, {
    params: { userId, page, size },
    headers: getHeaders()
  });
  return {
    data: response.data.data,
    totalPages: response.data.totalPages,
    totalElements: response.data.totalElements
  };
}

/**
 * 获取未读@提及数量
 */
export async function getUnreadCount(userId: number): Promise<number> {
  const response = await axios.get<AtMentionCountResponse>(`${API_BASE}/unread-count`, {
    params: { userId },
    headers: getHeaders()
  });
  return response.data.data;
}

/**
 * 获取群聊未读@提及数量
 */
export async function getUnreadCountInRoom(userId: number, roomId: number): Promise<number> {
  const response = await axios.get<AtMentionCountResponse>(`${API_BASE}/unread-count/room`, {
    params: { userId, roomId },
    headers: getHeaders()
  });
  return response.data.data;
}

/**
 * 标记@提及已读
 */
export async function markAsRead(userId: number, mentionIds: number[]): Promise<number> {
  const response = await axios.post(`${API_BASE}/mark-read`, mentionIds, {
    params: { userId },
    headers: getHeaders()
  });
  return response.data.updated;
}

/**
 * 标记群聊内所有@已读
 */
export async function markAllAsReadInRoom(userId: number, roomId: number): Promise<number> {
  const response = await axios.post(`${API_BASE}/mark-read/room`, {}, {
    params: { userId, roomId },
    headers: getHeaders()
  });
  return response.data.updated;
}

/**
 * 获取@提及设置
 */
export async function getMentionSettings(userId: number): Promise<AtMentionSettings> {
  const response = await axios.get(`${API_BASE}/settings`, {
    params: { userId },
    headers: getHeaders()
  });
  return response.data.data;
}

/**
 * 更新@提及设置
 */
export async function updateMentionSettings(userId: number, settings: AtMentionSettings): Promise<AtMentionSettings> {
  const response = await axios.put(`${API_BASE}/settings`, settings, {
    params: { userId },
    headers: getHeaders()
  });
  return response.data.data;
}
