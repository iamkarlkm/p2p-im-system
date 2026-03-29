/**
 * 免打扰设置服务
 * 支持会话静音和全局免打扰时段控制
 */

import { apiClient } from './apiClient';

export interface MuteSettingDTO {
  id: number;
  userId: number;
  conversationId: number | null;
  conversationType: 'PERSONAL' | 'GROUP' | null;
  isMuted: boolean;
  dndStartTime: string | null;
  dndEndTime: string | null;
  dndEnabled: boolean;
  dndRepeatDays: string | null;
  createdAt: string;
  updatedAt: string;
  inDndPeriod: boolean;
}

export interface MuteSettingRequest {
  conversationId?: number;
  isMuted?: boolean;
  dndStartTime?: string;
  dndEndTime?: string;
  dndEnabled?: boolean;
  dndRepeatDays?: string;
}

export interface ApiResponse<T> {
  code: number;
  message: string;
  data: T;
}

class MuteService {
  /**
   * 静音会话
   */
  async muteConversation(conversationId: number): Promise<MuteSettingDTO> {
    const response = await apiClient.put<ApiResponse<MuteSettingDTO>>(
      `/api/mute/conversations/${conversationId}`
    );
    return response.data.data;
  }

  /**
   * 取消会话静音
   */
  async unmuteConversation(conversationId: number): Promise<MuteSettingDTO> {
    const response = await apiClient.delete<ApiResponse<MuteSettingDTO>>(
      `/api/mute/conversations/${conversationId}`
    );
    return response.data.data;
  }

  /**
   * 获取所有会话的静音设置
   */
  async getConversationMuteSettings(): Promise<MuteSettingDTO[]> {
    const response = await apiClient.get<ApiResponse<MuteSettingDTO[]>>(
      '/api/mute/conversations'
    );
    return response.data.data;
  }

  /**
   * 获取所有已静音的会话ID列表
   */
  async getMutedConversationIds(): Promise<number[]> {
    const response = await apiClient.get<ApiResponse<number[]>>(
      '/api/mute/conversations/muted'
    );
    return response.data.data;
  }

  /**
   * 检查会话是否被静音
   */
  async isConversationMuted(conversationId: number): Promise<boolean> {
    const response = await apiClient.get<ApiResponse<boolean>>(
      `/api/mute/conversations/${conversationId}/check`
    );
    return response.data.data;
  }

  /**
   * 批量检查会话是否被静音
   */
  async batchCheckMuted(conversationIds: number[]): Promise<number[]> {
    const response = await apiClient.post<ApiResponse<number[]>>(
      '/api/mute/conversations/batch-check',
      conversationIds
    );
    return response.data.data;
  }

  /**
   * 设置全局免打扰时段
   */
  async setGlobalDnd(request: MuteSettingRequest): Promise<MuteSettingDTO> {
    const response = await apiClient.put<ApiResponse<MuteSettingDTO>>(
      '/api/mute/dnd',
      request
    );
    return response.data.data;
  }

  /**
   * 获取全局免打扰设置
   */
  async getGlobalDnd(): Promise<MuteSettingDTO | null> {
    const response = await apiClient.get<ApiResponse<MuteSettingDTO | null>>(
      '/api/mute/dnd'
    );
    return response.data.data;
  }

  /**
   * 删除全局免打扰设置
   */
  async deleteGlobalDnd(): Promise<void> {
    await apiClient.delete<ApiResponse<null>>('/api/mute/dnd');
  }

  /**
   * 检查是否可以接收消息
   */
  async canReceiveNotification(conversationId: number): Promise<boolean> {
    const response = await apiClient.get<ApiResponse<boolean>>(
      `/api/mute/can-receive/${conversationId}`
    );
    return response.data.data;
  }

  /**
   * 批量检查是否可以接收消息
   */
  async batchCanReceive(conversationIds: number[]): Promise<boolean[]> {
    const response = await apiClient.post<ApiResponse<boolean[]>>(
      '/api/mute/can-receive/batch',
      conversationIds
    );
    return response.data.data;
  }

  /**
   * 删除会话的静音设置
   */
  async deleteConversationSetting(conversationId: number): Promise<void> {
    await apiClient.delete<ApiResponse<null>>(
      `/api/mute/conversations/${conversationId}/setting`
    );
  }

  /**
   * 格式化免打扰时间范围显示
   */
  formatDndRange(setting: MuteSettingDTO | null): string {
    if (!setting || !setting.dndStartTime || !setting.dndEndTime) {
      return '未设置';
    }
    return `${setting.dndStartTime} - ${setting.dndEndTime}`;
  }

  /**
   * 获取免打扰重复周期的友好显示
   */
  formatDndRepeatDays(repeatDays: string | null): string {
    if (!repeatDays) return '每天';
    if (repeatDays === 'daily') return '每天';
    
    const dayMap: Record<string, string> = {
      'MON': '周一',
      'TUE': '周二',
      'WED': '周三',
      'THU': '周四',
      'FRI': '周五',
      'SAT': '周六',
      'SUN': '周日'
    };
    
    return repeatDays.split(',').map(day => dayMap[day] || day).join(', ');
  }

  /**
   * 解析免打扰重复周期
   */
  parseDndRepeatDays(selectedDays: string[]): string {
    const dayMap: Record<string, string> = {
      'Monday': 'MON',
      'Tuesday': 'TUE',
      'Wednesday': 'WED',
      'Thursday': 'THU',
      'Friday': 'FRI',
      'Saturday': 'SAT',
      'Sunday': 'SUN'
    };
    
    return selectedDays.map(day => dayMap[day] || day.toUpperCase().slice(0, 3)).join(',');
  }
}

export const muteService = new MuteService();
