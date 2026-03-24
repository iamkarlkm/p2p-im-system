/**
 * 联邦学习类型定义
 * 用于 TypeScript 桌面端应用
 */

export enum ModelType {
  RECOMMENDATION = 'RECOMMENDATION',
  CLASSIFICATION = 'CLASSIFICATION',
  CLUSTERING = 'CLUSTERING',
  REGRESSION = 'REGRESSION',
  NEURAL_NETWORK = 'NEURAL_NETWORK',
  DEEP_LEARNING = 'DEEP_LEARNING',
  TRANSFORMER = 'TRANSFORMER',
  COLLABORATIVE_FILTERING = 'COLLABORATIVE_FILTERING'
}

export enum ModelScope {
  GLOBAL = 'GLOBAL',
  REGIONAL = 'REGIONAL',
  ORGANIZATION = 'ORGANIZATION',
  GROUP = 'GROUP',
  PERSONALIZED = 'PERSONALIZED'
}

export enum ConvergenceStatus {
  TRAINING = 'TRAINING',
  CONVERGING = 'CONVERGING',
  CONVERGED = 'CONVERGED',
  DIVERGING = 'DIVERGING',
  PAUSED = 'PAUSED',
  FAILED = 'FAILED'
}

export enum RecommendationType {
  MESSAGE_SUGGESTION = 'MESSAGE_SUGGESTION',
  CONTACT_SUGGESTION = 'CONTACT_SUGGESTION',
  GROUP_SUGGESTION = 'GROUP_SUGGESTION',
  CONTENT_SUGGESTION = 'CONTENT_SUGGESTION',
  CHANNEL_SUGGESTION = 'CHANNEL_SUGGESTION',
  BOT_SUGGESTION = 'BOT_SUGGESTION',
  ACTION_SUGGESTION = 'ACTION_SUGGESTION',
  PERSONALIZED_FEED = 'PERSONALIZED_FEED',
  TRENDING_TOPIC = 'TRENDING_TOPIC',
  SIMILAR_USER = 'SIMILAR_USER'
}

export enum PrivacyLevel {
  MINIMAL = 'MINIMAL',
  BASIC = 'BASIC',
  STANDARD = 'STANDARD',
  ENHANCED = 'ENHANCED',
  MAXIMUM = 'MAXIMUM'
}

export enum AnonymizationLevel {
  RAW = 'RAW',
  PSEUDONYMIZED = 'PSEUDONYMIZED',
  ANONYMIZED = 'ANONYMIZED',
  AGGREGATED = 'AGGREGATED',
  DIFFERENTIALLY_PRIVATE = 'DIFFERENTIALLY_PRIVATE'
}

export enum RecommendationStatus {
  PENDING = 'PENDING',
  GENERATING = 'GENERATING',
  READY = 'READY',
  DELIVERED = 'DELIVERED',
  INTERACTED = 'INTERACTED',
  EXPIRED = 'EXPIRED',
  FAILED = 'FAILED',
  WITHDRAWN = 'WITHDRAWN'
}

export interface FederatedLearningModel {
  id?: string;
  modelId: string;
  modelName: string;
  modelType: ModelType;
  modelVersion: string;
  modelScope: ModelScope;
  aggregationAlgorithm: string;
  privacyBudget?: number;
  noiseScale?: number;
  learningRate?: number;
  batchSize?: number;
  epochs?: number;
  participatingClients?: number;
  minimumClients: number;
  aggregationRound: number;
  modelAccuracy?: number;
  modelLoss?: number;
  convergenceStatus: ConvergenceStatus;
  isActive: boolean;
  isEncrypted: boolean;
  createdAt: Date;
  updatedAt: Date;
  lastAggregationTime?: Date;
  nextAggregationTime?: Date;
}

export interface PrivacyPreservingRecommendation {
  id?: string;
  recommendationId: string;
  userId: string;
  sessionId?: string;
  modelId: string;
  recommendationType: RecommendationType;
  recommendationContext?: string;
  recommendedContentIds?: string;
  recommendationScores?: string;
  totalRecommendations?: number;
  userFeedbackScore?: number;
  userInteractionType?: string;
  clickThrough?: boolean;
  dwellTimeSeconds?: number;
  privacyLevel: PrivacyLevel;
  differentialPrivacyEnabled: boolean;
  privacyBudgetConsumed?: number;
  status: RecommendationStatus;
  qualityScore?: number;
  createdAt: Date;
  processedAt?: Date;
}

export interface CreateModelRequest {
  modelName: string;
  modelType: ModelType;
  modelScope: ModelScope;
  config?: ModelConfig;
}

export interface ModelConfig {
  privacyBudget?: number;
  noiseScale?: number;
  clipNorm?: number;
  learningRate?: number;
  batchSize?: number;
  epochs?: number;
  minimumClients?: number;
  aggregationAlgorithm?: string;
}

export interface GenerateRecommendationRequest {
  userId: string;
  sessionId: string;
  recommendationType: RecommendationType;
  context?: string;
  options?: RecommendationOptions;
}

export interface RecommendationOptions {
  privacyLevel?: PrivacyLevel;
  differentialPrivacyEnabled?: boolean;
  dataRetentionDays?: number;
}

export interface GradientUpdateRequest {
  clientId: string;
  gradientUpdate: Record<string, any>;
  trainingSamples: number;
}

export interface FeedbackRequest {
  interactionType: string;
  feedbackScore?: number;
  dwellTimeSeconds?: number;
}

export interface SystemStatistics {
  totalModels: number;
  activeModels: number;
  totalRecommendations: number;
  deliveredRecommendations: number;
  interactedRecommendations: number;
  interactionRate?: number;
  averageModelAccuracy: number;
}

export interface PrivacyOptions {
  privacyLevels: string[];
  anonymizationLevels: string[];
  differentialPrivacy: {
    enabled: boolean;
    defaultEpsilon: number;
    defaultDelta: number;
    noiseMechanism: string;
  };
  secureAggregation: {
    enabled: boolean;
    protocol: string;
    minClients: number;
  };
}

export interface AggregationAlgorithmInfo {
  available: string[];
  default: string;
  descriptions: Record<string, string>;
}

export const DEFAULT_MODEL_CONFIG: ModelConfig = {
  privacyBudget: 1.0,
  noiseScale: 0.1,
  clipNorm: 1.0,
  learningRate: 0.01,
  batchSize: 32,
  epochs: 10,
  minimumClients: 10,
  aggregationAlgorithm: 'FedAvg'
};

export const DEFAULT_RECOMMENDATION_OPTIONS: RecommendationOptions = {
  privacyLevel: PrivacyLevel.STANDARD,
  differentialPrivacyEnabled: true,
  dataRetentionDays: 30
};