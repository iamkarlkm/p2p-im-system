/**
 * 联邦学习 AI 类型定义
 * 为即时通讯系统的联邦学习功能提供完整的 TypeScript 类型支持
 * 
 * @version 1.0
 * @created 2026-03-23
 */

// ==================== 基础枚举类型 ====================

/** 服务器状态 */
export enum ServerStatus {
  ACTIVE = 'ACTIVE',           // 活跃状态
  INACTIVE = 'INACTIVE',       // 非活跃状态
  MAINTENANCE = 'MAINTENANCE', // 维护中
  DEGRADED = 'DEGRADED',       // 性能降级
  ERROR = 'ERROR',             // 错误状态
  SCALING = 'SCALING'          // 自动扩展中
}

/** 服务器类型 */
export enum ServerType {
  CENTRAL = 'CENTRAL',         // 中央服务器
  REGIONAL = 'REGIONAL',       // 区域服务器
  EDGE = 'EDGE',               // 边缘服务器
  HYBRID = 'HYBRID',           // 混合服务器
  MOBILE = 'MOBILE'            // 移动服务器
}

/** 模型状态 */
export enum ModelStatus {
  INITIALIZING = 'INITIALIZING',   // 初始化中
  ACTIVE = 'ACTIVE',               // 活跃状态
  TRAINING = 'TRAINING',           // 训练中
  AGGREGATING = 'AGGREGATING',     // 聚合中
  CONVERGED = 'CONVERGED',         // 已收敛
  MAX_ROUNDS_REACHED = 'MAX_ROUNDS_REACHED', // 达到最大轮次
  ERROR = 'ERROR'                  // 错误状态
}

/** 模型类型 */
export enum ModelType {
  SMART_REPLY = 'SMART_REPLY',           // 智能回复
  SPAM_DETECTION = 'SPAM_DETECTION',     // 垃圾检测
  SENTIMENT_ANALYSIS = 'SENTIMENT_ANALYSIS', // 情感分析
  MESSAGE_CATEGORIZATION = 'MESSAGE_CATEGORIZATION' // 消息分类
}

/** 更新状态 */
export enum UpdateStatus {
  PENDING = 'PENDING',         // 待处理
  RECEIVED = 'RECEIVED',       // 已接收
  VERIFYING = 'VERIFYING',     // 验证中
  VERIFIED = 'VERIFIED',       // 已验证
  REJECTED = 'REJECTED',       // 已拒绝
  AGGREGATING = 'AGGREGATING', // 聚合中
  AGGREGATED = 'AGGREGATED',   // 已聚合
  EXPIRED = 'EXPIRED',         // 已过期
  ERROR = 'ERROR'              // 错误
}

/** 更新类型 */
export enum UpdateType {
  GRADIENT = 'GRADIENT',       // 梯度更新
  WEIGHT = 'WEIGHT',           // 权重更新
  MOMENTUM = 'MOMENTUM',       // 动量更新
  ADAM = 'ADAM',               // Adam更新
  SPARSE = 'SPARSE',           // 稀疏更新
  DIFFERENTIAL = 'DIFFERENTIAL' // 差分更新
}

/** 隐私级别 */
export enum PrivacyLevel {
  MINIMAL = 'MINIMAL',         // 最小隐私保护
  BASIC = 'BASIC',             // 基本隐私保护
  STANDARD = 'STANDARD',       // 标准隐私保护
  ENHANCED = 'ENHANCED',       // 增强隐私保护
  MAXIMUM = 'MAXIMUM'          // 最大隐私保护
}

/** 训练轮次状态 */
export enum RoundStatus {
  SELECTING_CLIENTS = 'SELECTING_CLIENTS', // 选择客户端中
  TRAINING = 'TRAINING',                   // 训练中
  COLLECTING_UPDATES = 'COLLECTING_UPDATES', // 收集更新中
  VERIFYING = 'VERIFYING',                 // 验证中
  AGGREGATING = 'AGGREGATING',             // 聚合中
  COMPLETED = 'COMPLETED',                 // 已完成
  FAILED = 'FAILED'                        // 失败
}

/** 设备类型 */
export enum DeviceType {
  MOBILE = 'MOBILE',           // 移动设备
  DESKTOP = 'DESKTOP',         // 桌面设备
  TABLET = 'TABLET',           // 平板设备
  WEB = 'WEB',                 // Web端
  IOT = 'IOT'                  // 物联网设备
}

// ==================== 核心接口 ====================

/** 联邦学习服务器配置 */
export interface FLServerConfig {
  serverName: string;
  serverUrl: string;
  region: string;
  serverType?: ServerType;
  maxConcurrentClients?: number;
  maxClientsPerRound?: number;
  minClientsPerRound?: number;
  enableDifferentialPrivacy?: boolean;
  privacyEpsilon?: number;
  enableSecureAggregation?: boolean;
  aggregationAlgorithm?: string;
}

/** 联邦学习服务器实体 */
export interface FLServer {
  serverId: string;
  serverName: string;
  serverUrl: string;
  region: string;
  status: ServerStatus;
  serverType: ServerType;
  version: string;
  aggregationAlgorithm: string;
  maxConcurrentClients: number;
  maxClientsPerRound: number;
  minClientsPerRound: number;
  trainingRoundDurationMinutes: number;
  targetAccuracy: number;
  maxTrainingRounds: number;
  enableDifferentialPrivacy: boolean;
  privacyEpsilon: number;
  privacyDelta: number;
  enableSecureAggregation: boolean;
  secureAggregationProtocol: string;
  minClientsForSecureAggregation: number;
  enableModelCompression: boolean;
  modelCompressionRatio: number;
  enableEnergyAwareScheduling: boolean;
  requireChargingForTraining: boolean;
  requireWifiForTraining: boolean;
  minimumBatteryLevel: number;
  supportedModels: string[];
  supportedLanguages: string[];
  activeModelCount: number;
  activeClientCount: number;
  completedTrainingRounds: number;
  averageTrainingAccuracy: number;
  averageTrainingLoss: number;
  averageRoundDurationMinutes: number;
  createdAt: string;
  lastHeartbeatTime: string;
  lastTrainingRoundTime?: string;
  autoScalingEnabled: boolean;
  maxAutoScaleInstances: number;
  cpuUsageThreshold: number;
  memoryUsageThreshold: number;
  networkBandwidthMbps: number;
  healthCheckEndpoint: string;
  healthCheckIntervalSeconds: number;
  healthCheckTimeoutSeconds: number;
  description?: string;
}

/** 服务器状态响应 */
export interface ServerStatusResponse {
  serverId: string;
  serverName: string;
  serverUrl: string;
  status: ServerStatus;
  isHealthy: boolean;
  activeModelCount: number;
  activeClientCount: number;
  completedTrainingRounds: number;
  averageTrainingAccuracy: number;
  averageTrainingLoss: number;
  averageRoundDurationMinutes: number;
  lastHeartbeatTime: string;
  lastTrainingRoundTime?: string;
  canAcceptClient: boolean;
  supportedModels: string[];
  supportedLanguages: string[];
  pendingUpdates: number;
  verifiedUpdates: number;
  aggregatedUpdates: number;
}

/** 联邦学习模型实体 */
export interface FLModel {
  modelId: string;
  serverId: string;
  userId?: string;
  modelType: ModelType;
  modelName: string;
  language: string;
  version: string;
  status: ModelStatus;
  aggregationAlgorithm: string;
  targetAccuracy: number;
  maxTrainingRounds: number;
  currentTrainingRound: number;
  trainingRoundDurationMinutes: number;
  minClientsPerRound: number;
  maxClientsPerRound: number;
  requireChargingForTraining: boolean;
  requireWifiForTraining: boolean;
  minimumBatteryLevel: number;
  enableDifferentialPrivacy: boolean;
  privacyBudgetPerRound: number;
  privacyDelta: number;
  dpNoiseScale: number;
  dpClipNorm: number;
  privacyLevel: string;
  enableSecureAggregation: boolean;
  minClientsForSecureAggregation: number;
  enableModelCompression: boolean;
  modelCompressionRatio: number;
  selectedClientCount: number;
  receivedUpdateCount: number;
  verifiedUpdateCount: number;
  aggregatedClientCount: number;
  averageClientAccuracy: number;
  lastAggregationTime?: string;
  lastAggregationRound?: number;
  roundStartTime?: string;
  createdAt: string;
  updatedAt: string;
}

/** 模型更新实体 */
export interface FLModelUpdate {
  updateId: string;
  modelId: string;
  serverId: string;
  clientId: string;
  trainingRound: number;
  status: UpdateStatus;
  updateType: UpdateType;
  encryptedUpdateData: string;
  encryptionAlgorithm: string;
  encryptionKeyId: string;
  dataSizeBytes: number;
  localTrainingSamples: number;
  trainingLoss: number;
  trainingAccuracy: number;
  trainingEpochs: number;
  trainingBatchSize: number;
  learningRate: number;
  trainingDurationMs: number;
  deviceCpuUsage: number;
  deviceMemoryUsage: number;
  deviceBatteryLevel: number;
  deviceWasCharging: boolean;
  networkType: string;
  networkBandwidthMbps: number;
  networkLatencyMs: number;
  enableDifferentialPrivacy: boolean;
  privacyEpsilonUsed: number;
  privacyDeltaUsed: number;
  dpNoiseType: string;
  dpNoiseScale: number;
  dpClipNorm: number;
  privacyLevel: PrivacyLevel;
  enableSecureAggregation: boolean;
  aggregationGroupId: string;
  aggregationGroupSize: number;
  aggregationClientIndex: number;
  enableModelCompression: boolean;
  compressionRatio: number;
  compressionAlgorithm: string;
  compressionQualityScore: number;
  enableQuantization: boolean;
  quantizationBits: number;
  trainingMetrics: Record<string, number>;
  labelDistribution: Record<string, number>;
  modelHash: string;
  updateSignature: string;
  clientPublicKey: string;
  verificationStatus: string;
  verificationNotes?: string;
  qualityScore: number;
  contributionScore: number;
  isAnomalous: boolean;
  anomalyReason?: string;
  anomalyScore: number;
  includedInAggregation: boolean;
  aggregationRound?: number;
  createdAt: string;
  updatedAt: string;
  verifiedAt?: string;
  aggregatedAt?: string;
  clientVersion: string;
  clientPlatform: string;
  clientOsVersion: string;
  clientDeviceModel: string;
  clientIpAddress?: string;
  clientLocation?: string;
}

/** 客户端实体 */
export interface FLClient {
  clientId: string;
  serverId: string;
  userId: string;
  deviceType: DeviceType;
  platform: string;
  osVersion: string;
  deviceModel: string;
  appVersion: string;
  supportedModels: string[];
  supportedLanguages: string[];
  isOnline: boolean;
  lastSeenAt: string;
  isCharging: boolean;
  batteryLevel: number;
  networkType: string;
  networkBandwidthMbps: number;
  networkLatencyMs: number;
  cpuUsage: number;
  memoryUsage: number;
  availableStorage: number;
  totalTrainingRounds: number;
  lastTrainingTime?: string;
  lastTrainingAccuracy?: number;
  privacyBudgetUsed: number;
  privacyBudgetRemaining: number;
  contributionScore: number;
  reputationScore: number;
  createdAt: string;
  updatedAt: string;
}

/** 训练轮次实体 */
export interface FLTrainingRound {
  roundId: string;
  modelId: string;
  roundNumber: number;
  serverId: string;
  status: RoundStatus;
  targetClientCount: number;
  selectedClientCount: number;
  receivedUpdateCount: number;
  verifiedUpdateCount: number;
  aggregatedUpdateCount: number;
  startTime: string;
  endTime?: string;
  estimatedEndTime?: string;
  durationSeconds?: number;
  selectedClients: string[];
  aggregateAccuracy?: number;
  aggregateLoss?: number;
  privacyBudgetUsed?: number;
  createdAt: string;
  updatedAt: string;
}

/** 隐私预算实体 */
export interface PrivacyBudget {
  budgetId: string;
  clientId: string;
  modelId: string;
  totalEpsilon: number;
  usedEpsilon: number;
  remainingEpsilon: number;
  maxRounds: number;
  completedRounds: number;
  resetSchedule: string;
  nextResetTime?: string;
  createdAt: string;
  updatedAt: string;
}

// ==================== API 请求/响应类型 ====================

/** 注册服务器请求 */
export interface RegisterServerRequest {
  serverName: string;
  serverUrl: string;
  region: string;
  serverType?: ServerType;
}

/** 注册服务器响应 */
export interface RegisterServerResponse {
  success: boolean;
  serverId: string;
  serverName: string;
  serverUrl: string;
  region: string;
  serverType: string;
  status: string;
  version: string;
  registeredAt: string;
  message: string;
}

/** 训练轮次启动响应 */
export interface StartTrainingRoundResponse {
  roundId: string;
  modelId: string;
  roundNumber: number;
  selectedClients: Array<{
    clientId: string;
    deviceType: string;
    lastTrainingTime?: string;
  }>;
  targetAccuracy: number;
  maxTrainingRounds: number;
  privacyBudgetPerRound: number;
  estimatedCompletionTime: string;
}

/** 客户端更新数据 */
export interface ClientUpdateData {
  encryptedUpdateData: string;
  encryptionKeyId: string;
  dataSizeBytes: number;
  localTrainingSamples: number;
  trainingLoss: number;
  trainingAccuracy: number;
  trainingEpochs: number;
  trainingBatchSize: number;
  learningRate: number;
  trainingDurationMs: number;
  deviceCpuUsage: number;
  deviceMemoryUsage: number;
  deviceBatteryLevel: number;
  deviceWasCharging: boolean;
  networkType: string;
  networkBandwidthMbps: number;
  networkLatencyMs: number;
}

/** 智能回复建议响应 */
export interface SmartReplyResponse {
  userId: string;
  modelId: string;
  modelVersion: string;
  suggestions: string[];
  privacyProtected: boolean;
  localTrainingOnly: boolean;
  generatedAt: string;
}

/** 垃圾检测结果响应 */
export interface SpamDetectionResponse {
  userId: string;
  messageId?: string;
  isSpam: boolean;
  confidence: number;
  reasons: string[];
  modelId: string;
  modelVersion: string;
  privacyProtected: boolean;
  detectedAt: string;
}

/** 情感分析响应 */
export interface SentimentAnalysisResponse {
  userId: string;
  message: string;
  sentiment: 'POSITIVE' | 'NEGATIVE' | 'NEUTRAL';
  positiveScore: number;
  negativeScore: number;
  neutralScore: number;
  dominantSentiment: 'POSITIVE' | 'NEGATIVE' | 'NEUTRAL';
  modelId: string;
  modelVersion: string;
  privacyProtected: boolean;
  analyzedAt: string;
}

/** 消息分类响应 */
export interface MessageCategorizationResponse {
  userId: string;
  message: string;
  category: 'WORK' | 'SOCIAL' | 'ENTERTAINMENT' | 'IMPORTANT' | 'SPAM';
  confidence: number;
  allCategories: Record<string, number>;
  categorizedAt: string;
}

// ==================== 工具函数类型 ====================

/** API 响应通用包装器 */
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  error?: string;
  statusCode?: number;
  timestamp?: string;
}

/** 分页参数 */
export interface PaginationParams {
  page?: number;
  limit?: number;
  offset?: number;
  sortBy?: string;
  sortOrder?: 'asc' | 'desc';
}

/** 分页响应 */
export interface PaginatedResponse<T> {
  items: T[];
  total: number;
  page: number;
  limit: number;
  hasMore: boolean;
}

// ==================== 工具函数 ====================

/**
 * 检查服务器是否健康
 */
export function isServerHealthy(server: FLServer): boolean {
  return server.status === ServerStatus.ACTIVE &&
         new Date(server.lastHeartbeatTime) > new Date(Date.now() - 120000);
}

/**
 * 检查客户端是否符合训练条件
 */
export function isClientEligibleForTraining(client: FLClient): boolean {
  return client.isOnline &&
         client.batteryLevel >= 50 &&
         (client.isCharging || client.batteryLevel >= 80) &&
         client.networkType === 'WIFI';
}

/**
 * 计算隐私预算使用率
 */
export function calculatePrivacyBudgetUsage(budget: PrivacyBudget): number {
  return (budget.usedEpsilon / budget.totalEpsilon) * 100;
}

/**
 * 格式化时间戳为本地时间
 */
export function formatTimestamp(timestamp: string): string {
  return new Date(timestamp).toLocaleString('zh-CN');
}

/**
 * 生成默认服务器配置
 */
export function getDefaultServerConfig(region: string): FLServerConfig {
  return {
    serverName: `FL-Server-${region}`,
    serverUrl: `https://fl-${region.toLowerCase()}.im-system.com`,
    region,
    serverType: ServerType.REGIONAL,
    maxConcurrentClients: 100,
    maxClientsPerRound: 10,
    minClientsPerRound: 3,
    enableDifferentialPrivacy: true,
    privacyEpsilon: 1.0,
    enableSecureAggregation: true,
    aggregationAlgorithm: 'FedAvg'
  };
}

/**
 * 生成默认隐私配置
 */
export function getDefaultPrivacyConfig(): {
  privacyLevel: PrivacyLevel;
  epsilon: number;
  delta: number;
  enableSecureAggregation: boolean;
} {
  return {
    privacyLevel: PrivacyLevel.STANDARD,
    epsilon: 1.0,
    delta: 0.00001,
    enableSecureAggregation: true
  };
}

// ==================== 默认导出 ====================

export default {
  ServerStatus,
  ServerType,
  ModelStatus,
  ModelType,
  UpdateStatus,
  UpdateType,
  PrivacyLevel,
  RoundStatus,
  DeviceType,
  isServerHealthy,
  isClientEligibleForTraining,
  calculatePrivacyBudgetUsage,
  formatTimestamp,
  getDefaultServerConfig,
  getDefaultPrivacyConfig
};
