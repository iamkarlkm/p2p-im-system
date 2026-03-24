import { apiRequest } from './apiClient';

export interface Reaction {
  emoji: string;
  isCustom: boolean;
  count: number;
  userReacted: boolean;
}

export interface ReactionDTO {
  messageId: number;
  reactions: Reaction[];
}

export const reactionService = {
  /** 添加/切换表情反应 */
  async toggleReaction(messageId: number, emoji: string, userId: number, isCustom = false): Promise<ReactionDTO> {
    return apiRequest(`/reactions/message/${messageId}?emoji=${encodeURIComponent(emoji)}&isCustom=${isCustom}`, {
      method: 'POST',
      headers: { 'X-User-Id': String(userId) },
    });
  },

  /** 获取消息反应统计 */
  async getReactions(messageId: number, userId?: number): Promise<ReactionDTO> {
    return apiRequest(`/reactions/message/${messageId}`, {
      headers: userId ? { 'X-User-Id': String(userId) } : {},
    });
  },

  /** 批量获取多个消息的反应 */
  async batchGetReactions(messageIds: number[], userId?: number): Promise<Record<number, ReactionDTO>> {
    return apiRequest('/reactions/batch', {
      method: 'POST',
      body: JSON.stringify(messageIds),
      headers: { 'Content-Type': 'application/json', ...(userId ? { 'X-User-Id': String(userId) } : {}) },
    });
  },
};

// 预设表情列表
export const QUICK_EMOJIS = [
  '👍', '👎', '❤️', '😊', '😂', '😮', '😢', '😡',
  '🎉', '🔥', '👀', '🙌', '✨', '💯', '🚀', '👏',
];
