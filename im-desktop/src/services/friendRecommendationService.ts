import { apiClient } from '../utils/apiClient';

export interface RecommendationRequest {
  page: number;
  size: number;
  algorithm?: 'mutual_friends' | 'interest_tags' | 'group_relation' | 'mixed';
}

export interface RecommendationResponse {
  userId: string;
  nickname: string;
  avatar: string;
  reasonType: 'mutual_friends' | 'interest_tags' | 'group_relation' | 'mixed';
  reasonDescription: string;
  score: number;
  mutualFriendsCount?: number;
  commonTags?: string[];
  commonGroups?: string[];
}

export interface RecommendationResult {
  data: RecommendationResponse[];
  total: number;
  page: number;
  size: number;
}

export interface RecommendationStats {
  totalRecommended: number;
  totalIgnored: number;
  totalAccepted: number;
  acceptanceRate: number;
}

class FriendRecommendationService {
  private readonly baseUrl = '/api/v1/friends/recommendations';

  async getRecommendations(request: RecommendationRequest): Promise<RecommendationResult> {
    const params = new URLSearchParams();
    params.append('page', request.page.toString());
    params.append('size', request.size.toString());
    if (request.algorithm && request.algorithm !== 'mixed') {
      params.append('algorithm', request.algorithm);
    }

    const response = await apiClient.get<RecommendationResult>(
      `${this.baseUrl}?${params.toString()}`
    );
    return response.data;
  }

  async sendFriendRequest(userId: string): Promise<void> {
    await apiClient.post('/api/v1/friends/requests', {
      toUserId: userId,
      source: 'recommendation'
    });
  }

  async ignoreRecommendation(userId: string): Promise<void> {
    await apiClient.post(`${this.baseUrl}/${userId}/ignore`);
  }

  async getRecommendationStats(): Promise<RecommendationStats> {
    const response = await apiClient.get<RecommendationStats>(`${this.baseUrl}/stats`);
    return response.data;
  }

  async refreshRecommendations(): Promise<RecommendationResult> {
    const response = await apiClient.post<RecommendationResult>(`${this.baseUrl}/refresh`);
    return response.data;
  }
}

export const friendRecommendationService = new FriendRecommendationService();
