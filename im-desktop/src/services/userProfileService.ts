import axios from 'axios';
import type { UserProfileDTO, UserProfileRequest } from '../types/profile';

const API_BASE = '/api/v1/profiles';

export const userProfileService = {
  /** 获取当前用户完整资料 */
  async getMyProfile(): Promise<UserProfileDTO> {
    const res = await axios.get(`${API_BASE}/me`, {
      headers: { 'X-User-Id': localStorage.getItem('userId') || '' }
    });
    return res.data;
  },

  /** 更新当前用户资料 */
  async updateMyProfile(data: Partial<UserProfileRequest>): Promise<UserProfileDTO> {
    const res = await axios.put(`${API_BASE}/me`, data, {
      headers: { 'X-User-Id': localStorage.getItem('userId') || '' }
    });
    return res.data;
  },

  /** 获取指定用户的公开资料 */
  async getPublicProfile(userId: string): Promise<UserProfileDTO> {
    const res = await axios.get(`${API_BASE}/${userId}`);
    return res.data;
  },

  /** 批量获取用户资料 */
  async getBatchProfiles(userIds: string[]): Promise<UserProfileDTO[]> {
    const res = await axios.post(`${API_BASE}/batch`, { userIds });
    return res.data;
  },

  /** 搜索用户资料 */
  async searchProfiles(keyword: string): Promise<string[]> {
    const res = await axios.get(`${API_BASE}/search`, { params: { keyword } });
    return res.data;
  },

  /** 检查资料是否存在 */
  async profileExists(userId: string): Promise<boolean> {
    const res = await axios.get(`${API_BASE}/${userId}/exists`);
    return res.data.exists;
  }
};

export type { UserProfileDTO, UserProfileRequest };
