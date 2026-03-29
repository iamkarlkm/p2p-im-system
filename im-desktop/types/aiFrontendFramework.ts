/**
 * AI 前端框架类型定义
 * 用于 TypeScript 桌面端的类型系统
 */

/**
 * 模型引擎类型
 */
export type ModelEngineType = 
  | 'tensorflow.js'
  | 'onnxruntime'
  | 'transformers.js'
  | 'mediapipe'
  | 'custom';

/**
 * 推理后端类型
 */
export type InferenceBackendType = 
  | 'wasm'
  | 'webgl'
  | 'webgpu'
  | 'cpu'
  | 'auto';

/**
 * 性能级别
 */
export type PerformanceLevel = 
  | 'low'
  | 'balanced'
  | 'high'
  | 'ultra';

/**
 * 隐私模式类型
 */
export type PrivacyMode = 
  | 'local'
  | 'cloud'
  | 'hybrid';

/**
 * 模型更新频率
 */
export type ModelUpdateFrequency = 
  | 'daily'
  | 'weekly'
  | 'monthly'
  | 'manual';

/**
 * 功能状态接口
 */
export interface FeatureStatus {
  smartReply: boolean;
  messageSummary: boolean;
  sentimentAnalysis: boolean;
}

/**
 * AI 模型信息接口
 */
export interface AiModelInfo {
  name: string;
  version: string;
  sizeMb: number;
  engine: ModelEngineType;
  backend: InferenceBackendType;
  loaded: boolean;
  loadTime?: string; // ISO 8601 时间戳
  metadata?: Record<string, any>;
}

/**
 * 推理统计接口
 */
export interface InferenceStats {
  total: number;
  success: number;
  avgLatencyMs: number;
  successRate: number;
}

/**
 * AI 前端框架配置接口
 */
export interface AiFrameworkConfig {
  id: number;
  userId: number;
  deviceId: string;
  frameworkVersion: string;
  enabled: boolean;
  localModelEngine?: ModelEngineType;
  modelName?: string;
  modelVersion?: string;
  modelSizeMb?: number;
  modelLoaded: boolean;
  modelLoadTime?: string;
  inferenceBackend?: InferenceBackendType;
  maxMemoryMb: number;
  featureEnabledSmartReply: boolean;
  featureEnabledMessageSummary: boolean;
  featureEnabledSentimentAnalysis: boolean;
  privacyMode: boolean;
  offlineMode: boolean;
  performanceLevel: PerformanceLevel;
  inferenceBatchSize: number;
  modelUpdateFrequency: ModelUpdateFrequency;
  lastModelUpdate?: string;
  inferenceStatsTotal: number;
  inferenceStatsSuccess: number;
  inferenceStatsAvgLatencyMs: number;
  customConfig?: Record<string, any>;
  modelMetadata?: Record<string, any>;
  createdAt: string;
  updatedAt: string;
}

/**
 * 框架状态接口
 */
export interface FrameworkStatus {
  enabled: boolean;
  modelLoaded: boolean;
  modelName?: string;
  modelVersion?: string;
  localModelEngine?: ModelEngineType;
  inferenceBackend?: InferenceBackendType;
  privacyMode: boolean;
  offlineMode: boolean;
  performanceLevel: PerformanceLevel;
  featureEnabledSmartReply: boolean;
  featureEnabledMessageSummary: boolean;
  featureEnabledSentimentAnalysis: boolean;
  inferenceStatsTotal: number;
  inferenceStatsSuccess: number;
  inferenceStatsAvgLatencyMs: number;
  lastUpdate: string;
}

/**
 * 系统统计接口
 */
export interface SystemStatistics {
  activeFrameworks: number;
  loadedModels: number;
  privacyEnabled: number;
  offlineDevices: number;
  uniqueUsers: number;
  uniqueDevices: number;
  averageInferenceLatencyMs: number;
  totalInferences: number;
  successInferences: number;
  successRate: number;
  smartReplyEnabled: number;
  messageSummaryEnabled: number;
  sentimentAnalysisEnabled: number;
}

/**
 * 模型使用统计接口
 */
export interface ModelUsageStatistics {
  modelName: string;
  count: number;
  avgLatencyMs: number;
  totalInferences: number;
}

/**
 * 分布统计接口
 */
export interface DistributionStatistics {
  category: string;
  count: number;
  percentage: number;
}

/**
 * 健康检查响应接口
 */
export interface HealthCheckResponse {
  status: 'UP' | 'DOWN' | 'DEGRADED';
  timestamp: string;
  database?: string;
  statistics?: SystemStatistics;
  error?: string;
}

/**
 * 智能回复建议接口
 */
export interface SmartReplySuggestion {
  text: string;
  confidence: number;
  sentiment?: 'positive' | 'neutral' | 'negative';
  category?: 'greeting' | 'acknowledgment' | 'question' | 'farewell' | 'custom';
}

/**
 * 消息摘要接口
 */
export interface MessageSummary {
  originalLength: number;
  summaryLength: number;
  summary: string;
  compressionRatio: number;
  keyPoints?: string[];
  language?: string;
}

/**
 * 情感分析结果接口
 */
export interface SentimentAnalysisResult {
  sentiment: 'positive' | 'neutral' | 'negative' | 'mixed';
  confidence: number;
  emotions?: {
    joy?: number;
    sadness?: number;
    anger?: number;
    fear?: number;
    surprise?: number;
    disgust?: number;
  };
  intensity?: 'low' | 'medium' | 'high';
}

/**
 * 推理请求接口
 */
export interface InferenceRequest {
  input: string | string[];
  task: 'smart_reply' | 'summary' | 'sentiment' | 'custom';
  options?: {
    maxLength?: number;
    temperature?: number;
    topK?: number;
    topP?: number;
    language?: string;
  };
}

/**
 * 推理响应接口
 */
export interface InferenceResponse {
  success: boolean;
  data: any;
  latencyMs: number;
  modelVersion?: string;
  error?: string;
}

/**
 * 模型加载进度接口
 */
export interface ModelLoadProgress {
  loaded: number;
  total: number;
  percentage: number;
  status: 'loading' | 'validating' | 'ready' | 'error';
  error?: string;
}

/**
 * AI 框架事件类型
 */
export type AiFrameworkEventType = 
  | 'model_loaded'
  | 'model_unloaded'
  | 'inference_started'
  | 'inference_completed'
  | 'inference_error'
  | 'config_updated'
  | 'privacy_mode_changed'
  | 'offline_mode_changed';

/**
 * AI 框架事件接口
 */
export interface AiFrameworkEvent {
  type: AiFrameworkEventType;
  timestamp: string;
  data?: any;
}

/**
 * 模型配置预设接口
 */
export interface ModelPreset {
  id: string;
  name: string;
  description: string;
  modelName: string;
  modelVersion: string;
  engine: ModelEngineType;
  backend: InferenceBackendType;
  performanceLevel: PerformanceLevel;
  recommendedFor: string[];
}

/**
 * 预定义模型预设
 */
export const MODEL_PRESETS: ModelPreset[] = [
  {
    id: 'balanced',
    name: '平衡模式',
    description: '性能和准确性的平衡选择',
    modelName: 'distilbert-base',
    modelVersion: '1.0.0',
    engine: 'transformers.js',
    backend: 'wasm',
    performanceLevel: 'balanced',
    recommendedFor: ['日常使用', '低配置设备']
  },
  {
    id: 'performance',
    name: '性能模式',
    description: '更快的推理速度，适合实时场景',
    modelName: 'tinybert',
    modelVersion: '1.0.0',
    engine: 'onnxruntime',
    backend: 'webgl',
    performanceLevel: 'high',
    recommendedFor: ['实时回复', '游戏场景']
  },
  {
    id: 'accuracy',
    name: '精度模式',
    description: '最高的准确性，适合重要场景',
    modelName: 'bert-large',
    modelVersion: '1.0.0',
    engine: 'tensorflow.js',
    backend: 'webgpu',
    performanceLevel: 'ultra',
    recommendedFor: ['专业场景', '高精度需求']
  }
];

/**
 * 默认框架配置
 */
export const DEFAULT_FRAMEWORK_CONFIG: Partial<AiFrameworkConfig> = {
  enabled: true,
  maxMemoryMb: 512,
  featureEnabledSmartReply: true,
  featureEnabledMessageSummary: true,
  featureEnabledSentimentAnalysis: true,
  privacyMode: true,
  offlineMode: false,
  performanceLevel: 'balanced',
  inferenceBatchSize: 4,
  modelUpdateFrequency: 'weekly'
};

/**
 * 性能级别配置
 */
export const PERFORMANCE_CONFIG: Record<PerformanceLevel, { maxMemoryMb: number; batchSize: number }> = {
  low: { maxMemoryMb: 256, batchSize: 1 },
  balanced: { maxMemoryMb: 512, batchSize: 4 },
  high: { maxMemoryMb: 1024, batchSize: 8 },
  ultra: { maxMemoryMb: 2048, batchSize: 16 }
};

/**
 * 验证模型名称
 */
export function validateModelName(name: string): boolean {
  const validPattern = /^[a-zA-Z0-9_-]+$/;
  return validPattern.test(name) && name.length >= 3 && name.length <= 50;
}

/**
 * 验证版本号
 */
export function validateVersion(version: string): boolean {
  const semverPattern = /^\d+\.\d+\.\d+$/;
  return semverPattern.test(version);
}

/**
 * 计算压缩比
 */
export function calculateCompressionRatio(original: number, compressed: number): number {
  if (original === 0) return 0;
  return Math.round(((original - compressed) / original) * 100);
}

/**
 * 格式化延迟时间
 */
export function formatLatency(ms: number): string {
  if (ms < 1) {
    return `${(ms * 1000).toFixed(0)}μs`;
  } else if (ms < 1000) {
    return `${ms.toFixed(1)}ms`;
  } else {
    return `${(ms / 1000).toFixed(2)}s`;
  }
}

/**
 * 获取性能级别颜色
 */
export function getPerformanceLevelColor(level: PerformanceLevel): string {
  const colors: Record<PerformanceLevel, string> = {
    low: '#4ade80',
    balanced: '#3b82f6',
    high: '#f59e0b',
    ultra: '#ef4444'
  };
  return colors[level];
}

/**
 * 获取情感颜色
 */
export function getSentimentColor(sentiment: string): string {
  const colors: Record<string, string> = {
    positive: '#22c55e',
    neutral: '#6b7280',
    negative: '#ef4444',
    mixed: '#a855f7'
  };
  return colors[sentiment] || colors.neutral;
}

/**
 * 深拷贝框架配置
 */
export function cloneFrameworkConfig(config: AiFrameworkConfig): AiFrameworkConfig {
  return JSON.parse(JSON.stringify(config));
}

/**
 * 合并配置
 */
export function mergeConfigs(
  base: Partial<AiFrameworkConfig>,
  updates: Partial<AiFrameworkConfig>
): Partial<AiFrameworkConfig> {
  return { ...base, ...updates };
}