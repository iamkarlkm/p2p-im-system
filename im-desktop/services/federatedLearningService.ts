/**
 * 联邦学习服务
 * 提供与后端联邦学习 API 的交互能力
 */

import axios, { AxiosInstance } from 'axios';
import {
  FederatedLearningModel,
  PrivacyPreservingRecommendation,
  CreateModelRequest,
  GenerateRecommendationRequest,
  GradientUpdateRequest,
  FeedbackRequest,
  SystemStatistics,
  PrivacyOptions,
  AggregationAlgorithmInfo,
  ModelConfig,
  RecommendationOptions,
  DEFAULT_MODEL_CONFIG,
  DEFAULT_RECOMMENDATION_OPTIONS
} from '../types/federatedLearning';

export class FederatedLearningService {
  private apiClient: AxiosInstance;

  constructor(baseURL: string = '/api/v1/federated-learning') {
    this.apiClient = axios.create({
      baseURL,
      timeout: 30000,
      headers: { 'Content-Type': 'application/json' }
    });
  }

  async createModel(request: CreateModelRequest): Promise<{ modelId: string }> {
    const response = await this.apiClient.post('/models', {
      ...request,
      config: { ...DEFAULT_MODEL_CONFIG, ...request.config }
    });
    return response.data;
  }

  async getModelStatus(modelId: string): Promise<FederatedLearningModel> {
    const response = await this.apiClient.get(`/models/${modelId}/status`);
    return response.data;
  }

  async generateRecommendation(request: GenerateRecommendationRequest): Promise<{ recommendationId: string }> {
    const response = await this.apiClient.post('/recommendations', {
      ...request,
      options: { ...DEFAULT_RECOMMENDATION_OPTIONS, ...request.options }
    });
    return response.data;
  }

  async getRecommendationStatus(recommendationId: string): Promise<PrivacyPreservingRecommendation> {
    const response = await this.apiClient.get(`/recommendations/${recommendationId}/status`);
    return response.data;
  }

  async getUserRecommendations(userId: string, limit: number = 20): Promise<PrivacyPreservingRecommendation[]> {
    const response = await this.apiClient.get(`/users/${userId}/recommendations`, { params: { limit } });
    return response.data.recommendations || [];
  }

  async submitGradient(request: GradientUpdateRequest, modelId: string): Promise<void> {
    await this.apiClient.post(`/models/${modelId}/gradients`, request);
  }

  async performAggregation(modelId: string): Promise<any> {
    const response = await this.apiClient.post(`/models/${modelId}/aggregate`);
    return response.data.aggregationResult;
  }

  async recordFeedback(recommendationId: string, request: FeedbackRequest): Promise<void> {
    await this.apiClient.post(`/recommendations/${recommendationId}/feedback`, request);
  }

  async getSystemStatistics(): Promise<SystemStatistics> {
    const response = await this.apiClient.get('/statistics');
    return response.data.statistics;
  }

  async pauseModel(modelId: string): Promise<boolean> {
    const response = await this.apiClient.post(`/models/${modelId}/pause`);
    return response.data.success;
  }

  async resumeModel(modelId: string): Promise<boolean> {
    const response = await this.apiClient.post(`/models/${modelId}/resume`);
    return response.data.success;
  }

  async withdrawUserConsent(userId: string): Promise<void> {
    await this.apiClient.post(`/users/${userId}/withdraw-consent`);
  }

  async cleanupExpiredData(daysToKeep: number = 30): Promise<void> {
    await this.apiClient.post('/cleanup', null, { params: { daysToKeep } });
  }

  async getPrivacyOptions(): Promise<PrivacyOptions> {
    const response = await this.apiClient.get('/privacy-options');
    return response.data.privacyOptions;
  }

  async getAggregationAlgorithms(): Promise<AggregationAlgorithmInfo> {
    const response = await this.apiClient.get('/aggregation-algorithms');
    return response.data.algorithms;
  }

  async healthCheck(): Promise<any> {
    const response = await this.apiClient.get('/health');
    return response.data;
  }
}

export const federatedLearningService = new FederatedLearningService();
export default federatedLearningService;