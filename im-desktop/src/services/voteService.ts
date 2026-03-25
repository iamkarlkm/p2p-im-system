import { apiClient } from './apiClient';
import { MessageType, VoteMessage } from '../types/message';

export interface VoteOption {
  text: string;
  votes: number;
  percentage: number;
}

export interface Vote {
  id: string;
  messageId: string;
  groupId: string;
  userId: string;
  title: string;
  description?: string;
  options: string[];
  optionVoteCounts?: number[];
  isAnonymous: boolean;
  allowMultipleChoice: boolean;
  endTime?: string;
  totalVotes: number;
  isClosed: boolean;
  createdAt: string;
  updatedAt: string;
}

export interface VoteCreateRequest {
  messageId: string;
  groupId: string;
  userId: string;
  title: string;
  description?: string;
  options: string[];
  isAnonymous?: boolean;
  allowMultipleChoice?: boolean;
  endTime?: string;
}

export interface VoteSubmitRequest {
  voteId: string;
  userId: string;
  selectedOptions: number[];
}

export interface VoteStatistics {
  totalVotes: number;
  uniqueParticipants: number;
  optionVoteCounts: number[];
}

export class VoteService {
  
  /**
   * 创建投票
   */
  async createVote(request: VoteCreateRequest): Promise<Vote> {
    const response = await apiClient.post<Vote>('/api/votes', request);
    return response.data;
  }
  
  /**
   * 提交投票
   */
  async submitVote(voteId: string, request: VoteSubmitRequest): Promise<Vote> {
    const response = await apiClient.post<Vote>(`/api/votes/${voteId}/submit`, request);
    return response.data;
  }
  
  /**
   * 获取投票详情
   */
  async getVote(voteId: string): Promise<Vote> {
    const response = await apiClient.get<Vote>(`/api/votes/${voteId}`);
    return response.data;
  }
  
  /**
   * 根据消息获取投票
   */
  async getVoteByMessage(messageId: string): Promise<Vote> {
    const response = await apiClient.get<Vote>(`/api/votes/message/${messageId}`);
    return response.data;
  }
  
  /**
   * 获取群组投票列表
   */
  async getGroupVotes(groupId: string, activeOnly?: boolean): Promise<Vote[]> {
    const params: any = {};
    if (activeOnly !== undefined) {
      params.activeOnly = activeOnly;
    }
    
    const response = await apiClient.get<Vote[]>(`/api/votes/group/${groupId}`, { params });
    return response.data;
  }
  
  /**
   * 关闭投票
   */
  async closeVote(voteId: string): Promise<void> {
    await apiClient.post(`/api/votes/${voteId}/close`);
  }
  
  /**
   * 获取投票统计
   */
  async getVoteStatistics(voteId: string): Promise<VoteStatistics> {
    const response = await apiClient.get<VoteStatistics>(`/api/votes/${voteId}/statistics`);
    return response.data;
  }
  
  /**
   * 检查用户是否已投票
   */
  async hasUserVoted(voteId: string, userId: string): Promise<boolean> {
    try {
      const response = await apiClient.get<boolean>(`/api/votes/${voteId}/has-voted`, {
        params: { userId }
      });
      return response.data;
    } catch (error) {
      return false;
    }
  }
  
  /**
   * 计算投票选项百分比
   */
  calculateVotePercentages(vote: Vote): VoteOption[] {
    const totalVotes = vote.totalVotes;
    const voteCounts = vote.optionVoteCounts || [];
    
    return vote.options.map((text, index) => {
      const voteCount = voteCounts[index] || 0;
      const percentage = totalVotes > 0 ? Math.round((voteCount / totalVotes) * 100) : 0;
      
      return {
        text,
        votes: voteCount,
        percentage
      };
    });
  }
  
  /**
   * 检查投票是否已过期
   */
  isVoteExpired(vote: Vote): boolean {
    if (!vote.endTime) return false;
    const endTime = new Date(vote.endTime);
    return new Date() > endTime;
  }
  
  /**
   * 检查投票是否可提交
   */
  canSubmitVote(vote: Vote, userId?: string): { canSubmit: boolean; reason?: string } {
    if (vote.isClosed) {
      return { canSubmit: false, reason: '投票已结束' };
    }
    
    if (vote.endTime && new Date() > new Date(vote.endTime)) {
      return { canSubmit: false, reason: '投票已过期' };
    }
    
    // 如果已投票且非匿名投票，检查是否允许重新投票
    if (userId && !vote.isAnonymous && vote.allowMultipleChoice === false) {
      // 这里需要调用后端检查是否已投票
      // 暂时返回可以投票
    }
    
    return { canSubmit: true };
  }
  
  /**
   * 从消息对象解析投票信息
   */
  parseVoteFromMessage(message: MessageType): VoteMessage | null {
    if (message.type === 'vote' && message.voteData) {
      return message.voteData;
    }
    return null;
  }
}

export const voteService = new VoteService();