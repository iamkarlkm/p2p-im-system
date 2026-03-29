import { apiClient, wsClient } from './client';

export interface ReactionStats {
  messageId: string;
  counts: Record<string, number>;
}

export interface ReactionWithUsers {
  emoji: string;
  count: number;
  userIds: string[];
}

class ReactionService {
  async addReaction(messageId: string, emoji: string, type = 'EMOJI'): Promise<void> {
    await apiClient.post('/api/reactions/add', null, {
      params: { messageId, userId: apiClient.userId, emoji, type }
    });
  }

  async removeReaction(messageId: string, emoji: string): Promise<void> {
    await apiClient.post('/api/reactions/remove', null, {
      params: { messageId, userId: apiClient.userId, emoji }
    });
  }

  async getReactions(messageId: string): Promise<ReactionWithUsers[]> {
    const res = await apiClient.get(`/api/reactions/message/${messageId}`);
    return res.data;
  }

  async getStats(messageId: string): Promise<ReactionStats> {
    const res = await apiClient.get(`/api/reactions/stats/${messageId}`);
    return res.data;
  }

  onReactionUpdate(callback: (messageId: string, stats: ReactionStats) => void): void {
    wsClient.on('reaction_update', (data: any) => {
      callback(data.messageId, { messageId: data.messageId, counts: data.stats });
    });
  }
}

export const reactionService = new ReactionService();
