/**
 * 多模态内容理解引擎 - TypeScript 类型定义
 */

// ==================== 配置相关类型 ====================

export interface MultimodalConfig {
  id?: number;
  name: string;
  description?: string;
  enabled: boolean;
  version?: number;
  createdAt?: string;
  updatedAt?: string;
  
  // 文本理解配置
  textEnabled: boolean;
  textModel?: string;
  textMaxLength?: number;
  textLanguages?: string;
  
  // 图像理解配置
  imageEnabled: boolean;
  imageModel?: string;
  imageMaxSize?: number;
  imageSupportedFormats?: string;
  
  // 语音理解配置
  audioEnabled: boolean;
  audioModel?: string;
  audioMaxDuration?: number;
  audioSupportedFormats?: string;
  
  // 视频理解配置
  videoEnabled: boolean;
  videoModel?: string;
  videoMaxDuration?: number;
  videoMaxSize?: number;
  
  // 多模态融合配置
  multimodalFusionEnabled: boolean;
  fusionMethod?: string;
  crossModalWeighting?: string;
  
  // 缓存配置
  cacheEnabled: boolean;
  cacheTtlHours?: number;
  cacheMaxSize?: number;
  
  // 性能配置
  concurrentWorkers?: number;
  timeoutMs?: number;
  batchSize?: number;
  
  // 质量配置
  confidenceThreshold?: number;
  fallbackEnabled?: boolean;
  fallbackModel?: string;
  
  // 监控配置
  metricsEnabled: boolean;
  metricsIntervalMinutes?: number;
  alertThresholdErrorRate?: number;
  alertThresholdLatencyMs?: number;
  
  // 隐私配置
  privacyEnabled: boolean;
  anonymizationEnabled?: boolean;
  dataRetentionDays?: number;
  
  // 自定义配置
  customConfig?: Record<string, any>;
}

export interface ConfigStats {
  totalConfigs: number;
  enabledConfigs: number;
  textAnalysisConfigs: number;
  imageAnalysisConfigs: number;
  audioAnalysisConfigs: number;
  videoAnalysisConfigs: number;
  fallbackEnabledConfigs: number;
}

// ==================== 分析结果类型 ====================

export type AnalysisStatus = 'pending' | 'processing' | 'completed' | 'failed';
export type ContentType = 'text' | 'image' | 'audio' | 'video' | 'mixed';
export type SentimentType = 'positive' | 'negative' | 'neutral' | 'mixed';
export type QualityRating = 'high' | 'medium' | 'low';

export interface AnalysisRequest {
  sessionId?: string;
  userId?: number;
  messageId?: number;
  contentType: ContentType;
  businessContext?: string;
  priority?: number;
  textContent?: string;
  imageUrl?: string;
  audioUrl?: string;
  videoUrl?: string;
}

export interface AnalysisResult {
  requestId: string;
  sessionId?: string;
  userId?: number;
  messageId?: number;
  contentType: ContentType;
  contentHash?: string;
  analysisStatus: AnalysisStatus;
  createdAt: string;
  updatedAt: string;
  completedAt?: string;
  
  // 文本分析结果
  textAnalysis?: TextAnalysis;
  
  // 图像分析结果
  imageAnalysis?: ImageAnalysis;
  
  // 音频分析结果
  audioAnalysis?: AudioAnalysis;
  
  // 视频分析结果
  videoAnalysis?: VideoAnalysis;
  
  // 多模态融合结果
  multimodalFusion?: MultimodalFusion;
  
  // 质量评估
  confidenceScore?: number;
  qualityRating?: QualityRating;
  processingTimeMs?: number;
  modelUsed?: string;
  costUnits?: number;
  
  // 错误信息
  errorMessage?: string;
  errorCode?: string;
  retryCount?: number;
}

export interface TextAnalysis {
  summary?: string;
  keywords: string[];
  sentiment?: SentimentType;
  sentimentScore?: number;
  intent?: string;
  entities?: NamedEntity[];
}

export interface NamedEntity {
  text: string;
  type: string;
  start: number;
  end: number;
}

export interface ImageAnalysis {
  description?: string;
  tags: string[];
  scene?: string;
  objects?: DetectedObject[];
  faces?: FaceDetection[];
  colors?: ColorAnalysis[];
}

export interface DetectedObject {
  name: string;
  confidence: number;
  bbox: BoundingBox;
}

export interface BoundingBox {
  x: number;
  y: number;
  width: number;
  height: number;
}

export interface FaceDetection {
  bbox: BoundingBox;
  confidence: number;
  gender?: string;
  age?: number;
  emotion?: string;
}

export interface ColorAnalysis {
  color: string;
  percentage: number;
  hex: string;
}

export interface AudioAnalysis {
  transcription?: string;
  emotion?: string;
  emotionScore?: number;
  speakers?: number;
  keywords?: string;
}

export interface VideoAnalysis {
  description?: string;
  scenes?: VideoScene[];
  keyFrames?: KeyFrame[];
  motionAnalysis?: MotionAnalysis;
}

export interface VideoScene {
  startFrame: number;
  endFrame: number;
  description?: string;
  tags?: string[];
}

export interface KeyFrame {
  frameNumber: number;
  description?: string;
  significance?: number;
}

export interface MotionAnalysis {
  motionIntensity?: number;
  vectors?: MotionVector[];
  movingObjects?: MovingObject[];
}

export interface MotionVector {
  x: number;
  y: number;
  magnitude: number;
  direction: number;
}

export interface MovingObject {
  bbox: BoundingBox;
  speed?: number;
  direction?: string;
}

export interface MultimodalFusion {
  summary?: string;
  tags: string[];
  sentiment?: SentimentType;
  sentimentScore?: number;
  intent?: string;
  correlations?: Record<string, number>;
}

// ==================== 统计类型 ====================

export interface ResultStats {
  totalResults: number;
  completedResults: number;
  failedResults: number;
  processingResults: number;
  pendingResults: number;
  averageProcessingTime?: number;
  averageConfidenceScore?: number;
  averageSentimentScore?: number;
  totalCost?: number;
}

export interface SentimentDistribution {
  sentiment: string;
  count: number;
}

export interface IntentDistribution {
  intent: string;
  count: number;
}

export interface SceneDistribution {
  scene: string;
  count: number;
}

// ==================== API 响应类型 ====================

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data?: T;
  timestamp: number;
}

export interface AnalysisStatusResponse {
  requestId: string;
  status: AnalysisStatus;
  createdAt: string;
  updatedAt: string;
  completedAt?: string;
  confidenceScore?: number;
  processingTimeMs?: number;
  hasSummary?: boolean;
  errorMessage?: string;
  retryCount?: number;
}

export interface HistoryItem {
  requestId: string;
  contentType: ContentType;
  analysisStatus: AnalysisStatus;
  createdAt: string;
  completedAt?: string;
  confidenceScore?: number;
  processingTimeMs?: number;
  summary?: string;
}

// ==================== 查询参数类型 ====================

export interface AnalysisQueryParams {
  userId?: number;
  contentType?: ContentType;
  status?: AnalysisStatus;
  startDate?: string;
  endDate?: string;
  page?: number;
  size?: number;
  sortBy?: string;
  sortDir?: 'ASC' | 'DESC';
}

// ==================== 工具函数 ====================

export function isAnalysisCompleted(result: AnalysisResult): boolean {
  return result.analysisStatus === 'completed';
}

export function isAnalysisFailed(result: AnalysisResult): boolean {
  return result.analysisStatus === 'failed';
}

export function isAnalysisProcessing(result: AnalysisResult): boolean {
  return result.analysisStatus === 'processing';
}

export function getSentimentColor(sentiment?: SentimentType): string {
  switch (sentiment) {
    case 'positive':
      return '#4CAF50';
    case 'negative':
      return '#F44336';
    case 'neutral':
      return '#9E9E9E';
    case 'mixed':
      return '#FF9800';
    default:
      return '#757575';
  }
}

export function getSentimentLabel(sentiment?: SentimentType): string {
  switch (sentiment) {
    case 'positive':
      return '积极';
    case 'negative':
      return '消极';
    case 'neutral':
      return '中性';
    case 'mixed':
      return '混合';
    default:
      return '未知';
  }
}

export function getStatusLabel(status: AnalysisStatus): string {
  switch (status) {
    case 'pending':
      return '等待中';
    case 'processing':
      return '处理中';
    case 'completed':
      return '已完成';
    case 'failed':
      return '失败';
    default:
      return '未知';
  }
}

export function getQualityColor(rating?: QualityRating): string {
  switch (rating) {
    case 'high':
      return '#4CAF50';
    case 'medium':
      return '#FF9800';
    case 'low':
      return '#F44336';
    default:
      return '#757575';
  }
}

export function formatProcessingTime(ms?: number): string {
  if (ms === undefined || ms === null) return '-';
  if (ms < 1000) return `${ms}ms`;
  return `${(ms / 1000).toFixed(2)}s`;
}

export function formatTimestamp(timestamp?: string): string {
  if (!timestamp) return '-';
  const date = new Date(timestamp);
  return date.toLocaleString('zh-CN');
}