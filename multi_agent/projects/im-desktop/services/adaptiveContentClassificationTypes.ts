// 自适应内容分类类型定义
// 支持自定义分类体系、增量学习和多模态内容分类

/**
 * 分类类型枚举
 */
export enum ClassificationType {
  HIERARCHICAL = 'HIERARCHICAL',  // 层级分类
  FLAT = 'FLAT',                   // 扁平分类
  HYBRID = 'HYBRID'                // 混合分类
}

/**
 * 内容模态枚举
 */
export enum ContentModality {
  TEXT_ONLY = 'TEXT_ONLY',         // 仅文本
  IMAGE_ONLY = 'IMAGE_ONLY',       // 仅图像
  AUDIO_ONLY = 'AUDIO_ONLY',       // 仅音频
  VIDEO_ONLY = 'VIDEO_ONLY',       // 仅视频
  MULTIMODAL = 'MULTIMODAL'        // 多模态
}

/**
 * 分类隐私级别枚举
 */
export enum ClassificationPrivacyLevel {
  PUBLIC = 'PUBLIC',               // 公开
  PROTECTED = 'PROTECTED',         // 受保护
  PRIVATE = 'PRIVATE',             // 私有
  CONFIDENTIAL = 'CONFIDENTIAL'    // 机密
}

/**
 * 分类状态枚举
 */
export enum ClassificationStatus {
  DRAFT = 'DRAFT',                 // 草稿
  ACTIVE = 'ACTIVE',               // 活跃
  INACTIVE = 'INACTIVE',           // 非活跃
  ARCHIVED = 'ARCHIVED',           // 已归档
  DELETED = 'DELETED'              // 已删除
}

/**
 * 内容类型枚举
 */
export enum ContentType {
  TEXT_MESSAGE = 'TEXT_MESSAGE',
  IMAGE_MESSAGE = 'IMAGE_MESSAGE',
  AUDIO_MESSAGE = 'AUDIO_MESSAGE',
  VIDEO_MESSAGE = 'VIDEO_MESSAGE',
  FILE_MESSAGE = 'FILE_MESSAGE',
  LOCATION_MESSAGE = 'LOCATION_MESSAGE',
  CONTACT_MESSAGE = 'CONTACT_MESSAGE',
  SYSTEM_MESSAGE = 'SYSTEM_MESSAGE',
  LINK_MESSAGE = 'LINK_MESSAGE',
  POLL_MESSAGE = 'POLL_MESSAGE',
  EMOJI_MESSAGE = 'EMOJI_MESSAGE',
  STICKER_MESSAGE = 'STICKER_MESSAGE'
}

/**
 * 分类配置接口
 */
export interface AdaptiveContentClassificationConfig {
  id?: number;
  name: string;
  description?: string;
  userId: number;
  sessionId?: number;
  categoryHierarchy: string;  // JSON 格式
  classificationType: ClassificationType;
  contentModality: ContentModality;
  minConfidenceScore: number;
  enableIncrementalLearning: boolean;
  incrementalLearningBatchSize: number;
  enableContextAwareness: boolean;
  contextWindowSize: number;
  enableMultiLanguage: boolean;
  supportedLanguages?: string;  // JSON 数组
  enableAutoLabelRecommendation: boolean;
  maxLabelRecommendations: number;
  enablePrivacyProtection: boolean;
  privacyLevel: ClassificationPrivacyLevel;
  enableEvolutionTracking: boolean;
  evolutionTrackingDepth: number;
  status: ClassificationStatus;
  version?: number;
  versionNotes?: string;
  accuracyScore?: number;
  totalClassifications?: number;
  correctClassifications?: number;
  feedbackStatistics?: string;  // JSON 格式
  performanceMetrics?: string;  // JSON 格式
  createdAt?: string;
  updatedAt?: string;
}

/**
 * 分类结果接口
 */
export interface ContentClassificationResult {
  id?: number;
  classificationConfigId: number;
  contentId: number;
  contentType: ContentType;
  userId: number;
  sessionId?: number;
  primaryCategory: string;
  secondaryCategories?: string;  // JSON 数组
  confidenceScore: number;
  classificationEvidence?: string;  // JSON 格式
  isContextAware: boolean;
  contextInformation?: string;  // JSON 格式
  isMultiModal: boolean;
  multiModalAnalysis?: string;  // JSON 格式
  isAutoLabelRecommended: boolean;
  recommendedLabels?: string;  // JSON 数组
  hasUserFeedback: boolean;
  userFeedback?: string;  // JSON 格式
  isPrivacyProtected: boolean;
  privacyLevel: ClassificationPrivacyLevel;
  isEvolutionTracked: boolean;
  evolutionHistory?: string;  // JSON 格式
  classificationVersion: number;
  versionChanges?: string;  // JSON 格式
  accuracyContribution: number;
  isTrainingExample: boolean;
  modelFeatures?: string;  // JSON 格式
  isAnomalyDetected: boolean;
  anomalyDetails?: string;  // JSON 格式
  languageCode: string;
  contentLanguage: string;
  crossLanguageMapping?: string;  // JSON 格式
  contentCreatedAt: string;
  createdAt?: string;
  updatedAt?: string;
}

/**
 * 创建分类配置请求
 */
export interface CreateClassificationConfigRequest {
  name: string;
  description?: string;
  userId: number;
  sessionId?: number;
  categoryHierarchy: string;
  classificationType?: ClassificationType;
  contentModality?: ContentModality;
  minConfidenceScore?: number;
  enableIncrementalLearning?: boolean;
  incrementalLearningBatchSize?: number;
  enableContextAwareness?: boolean;
  contextWindowSize?: number;
  enableMultiLanguage?: boolean;
  supportedLanguages?: string[];
  enableAutoLabelRecommendation?: boolean;
  maxLabelRecommendations?: number;
  enablePrivacyProtection?: boolean;
  privacyLevel?: ClassificationPrivacyLevel;
  enableEvolutionTracking?: boolean;
  evolutionTrackingDepth?: number;
}

/**
 * 更新分类配置请求
 */
export interface UpdateClassificationConfigRequest {
  name?: string;
  description?: string;
  categoryHierarchy?: string;
  classificationType?: ClassificationType;
  contentModality?: ContentModality;
  minConfidenceScore?: number;
  enableIncrementalLearning?: boolean;
  incrementalLearningBatchSize?: number;
  enableContextAwareness?: boolean;
  contextWindowSize?: number;
  enableMultiLanguage?: boolean;
  supportedLanguages?: string[];
  enableAutoLabelRecommendation?: boolean;
  maxLabelRecommendations?: number;
  enablePrivacyProtection?: boolean;
  privacyLevel?: ClassificationPrivacyLevel;
  enableEvolutionTracking?: boolean;
  evolutionTrackingDepth?: number;
  status?: ClassificationStatus;
  versionNotes?: string;
  performanceMetrics?: string;
}

/**
 * 分类内容请求
 */
export interface ClassifyContentRequest {
  contentId: number;
  contentType: ContentType;
  content: string;
  sessionId?: number;
  imageUrl?: string;
  audioUrl?: string;
  videoUrl?: string;
  metadata?: Record<string, any>;
}

/**
 * 批量分类请求
 */
export interface BatchClassifyRequest {
  contents: ClassifyContentRequest[];
}

/**
 * 分页响应接口
 */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}

/**
 * API 响应接口
 */
export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
  timestamp: number;
}

/**
 * 配置统计接口
 */
export interface ConfigStats {
  totalConfigs: number;
  activeConfigs: number;
  typeDistribution: Record<string, number>;
  modalityDistribution: Record<string, number>;
  averageAccuracy: number;
  totalClassifications: number;
}

/**
 * 分类统计接口
 */
export interface ClassificationStats {
  totalResults: number;
  confidenceDistribution: Record<string, number>;
  categoryDistribution: Record<string, number>;
  contentTypeDistribution: Record<string, number>;
  averageConfidence: number;
  highConfidencePercentage: number;
}

/**
 * 分类趋势接口
 */
export interface ClassificationTrend {
  dailyCounts: Record<string, number>;
  dailyAvgConfidence: Record<string, number>;
  totalDays: number;
  totalClassifications: number;
}

/**
 * 工具函数：创建分类配置
 */
export function createClassificationConfig(
  name: string,
  userId: number,
  categoryHierarchy: string,
  options?: Partial<CreateClassificationConfigRequest>
): CreateClassificationConfigRequest {
  return {
    name,
    userId,
    categoryHierarchy,
    classificationType: ClassificationType.HIERARCHICAL,
    contentModality: ContentModality.TEXT_ONLY,
    minConfidenceScore: 70,
    enableIncrementalLearning: true,
    incrementalLearningBatchSize: 100,
    enableContextAwareness: true,
    contextWindowSize: 10,
    enableMultiLanguage: true,
    enableAutoLabelRecommendation: true,
    maxLabelRecommendations: 5,
    enablePrivacyProtection: true,
    privacyLevel: ClassificationPrivacyLevel.PRIVATE,
    enableEvolutionTracking: true,
    evolutionTrackingDepth: 30,
    ...options
  };
}

/**
 * 工具函数：获取置信度级别
 */
export function getConfidenceLevel(score: number): string {
  if (score >= 90) return '很高';
  if (score >= 80) return '高';
  if (score >= 70) return '中';
  if (score >= 60) return '低';
  return '很低';
}

/**
 * 工具函数：获取置信度颜色
 */
export function getConfidenceColor(score: number): string {
  if (score >= 90) return '#22c55e';  // green-500
  if (score >= 80) return '#84cc16';  // lime-500
  if (score >= 70) return '#eab308';  // yellow-500
  if (score >= 60) return '#f97316';  // orange-500
  return '#ef4444';  // red-500
}

/**
 * 工具函数：获取状态文本
 */
export function getStatusText(status: ClassificationStatus): string {
  const statusMap: Record<ClassificationStatus, string> = {
    [ClassificationStatus.DRAFT]: '草稿',
    [ClassificationStatus.ACTIVE]: '活跃',
    [ClassificationStatus.INACTIVE]: '非活跃',
    [ClassificationStatus.ARCHIVED]: '已归档',
    [ClassificationStatus.DELETED]: '已删除'
  };
  return statusMap[status] || status;
}

/**
 * 工具函数：获取状态颜色
 */
export function getStatusColor(status: ClassificationStatus): string {
  const colorMap: Record<ClassificationStatus, string> = {
    [ClassificationStatus.DRAFT]: '#6b7280',    // gray-500
    [ClassificationStatus.ACTIVE]: '#22c55e',   // green-500
    [ClassificationStatus.INACTIVE]: '#f97316', // orange-500
    [ClassificationStatus.ARCHIVED]: '#6366f1', // indigo-500
    [ClassificationStatus.DELETED]: '#ef4444'   // red-500
  };
  return colorMap[status] || '#6b7280';
}

/**
 * 工具函数：解析 JSON 字段
 */
export function parseJsonField<T>(jsonString: string | null | undefined, defaultValue: T): T {
  if (!jsonString) return defaultValue;
  try {
    return JSON.parse(jsonString);
  } catch {
    return defaultValue;
  }
}

/**
 * 工具函数：序列化数组
 */
export function serializeArray<T>(array: T[]): string {
  return JSON.stringify(array);
}

/**
 * 工具函数：计算准确率
 */
export function calculateAccuracy(correct: number, total: number): number {
  if (total === 0) return 0;
  return Math.round((correct / total) * 10000) / 100;
}

/**
 * 工具函数：格式化日期
 */
export function formatDate(dateString: string | null | undefined): string {
  if (!dateString) return '-';
  try {
    const date = new Date(dateString);
    return date.toLocaleString('zh-CN');
  } catch {
    return dateString;
  }
}

/**
 * 工具函数：验证分类配置
 */
export function validateClassificationConfig(
  config: CreateClassificationConfigRequest
): { valid: boolean; errors: string[] } {
  const errors: string[] = [];

  if (!config.name || config.name.trim().length === 0) {
    errors.push('配置名称不能为空');
  }

  if (config.name && config.name.length > 100) {
    errors.push('配置名称不能超过 100 个字符');
  }

  if (config.minConfidenceScore !== undefined) {
    if (config.minConfidenceScore < 0 || config.minConfidenceScore > 100) {
      errors.push('最小置信度必须在 0-100 之间');
    }
  }

  if (config.contextWindowSize !== undefined) {
    if (config.contextWindowSize < 1 || config.contextWindowSize > 100) {
      errors.push('上下文窗口大小必须在 1-100 之间');
    }
  }

  if (config.maxLabelRecommendations !== undefined) {
    if (config.maxLabelRecommendations < 1 || config.maxLabelRecommendations > 20) {
      errors.push('最大标签推荐数必须在 1-20 之间');
    }
  }

  return {
    valid: errors.length === 0,
    errors
  };
}

/**
 * 工具函数：获取内容类型文本
 */
export function getContentTypeText(type: ContentType): string {
  const typeMap: Record<ContentType, string> = {
    [ContentType.TEXT_MESSAGE]: '文本消息',
    [ContentType.IMAGE_MESSAGE]: '图片消息',
    [ContentType.AUDIO_MESSAGE]: '音频消息',
    [ContentType.VIDEO_MESSAGE]: '视频消息',
    [ContentType.FILE_MESSAGE]: '文件消息',
    [ContentType.LOCATION_MESSAGE]: '位置消息',
    [ContentType.CONTACT_MESSAGE]: '联系人消息',
    [ContentType.SYSTEM_MESSAGE]: '系统消息',
    [ContentType.LINK_MESSAGE]: '链接消息',
    [ContentType.POLL_MESSAGE]: '投票消息',
    [ContentType.EMOJI_MESSAGE]: '表情消息',
    [ContentType.STICKER_MESSAGE]: '贴纸消息'
  };
  return typeMap[type] || type;
}

/**
 * 工具函数：获取分类类型文本
 */
export function getClassificationTypeText(type: ClassificationType): string {
  const typeMap: Record<ClassificationType, string> = {
    [ClassificationType.HIERARCHICAL]: '层级分类',
    [ClassificationType.FLAT]: '扁平分类',
    [ClassificationType.HYBRID]: '混合分类'
  };
  return typeMap[type] || type;
}

/**
 * 工具函数：获取内容模态文本
 */
export function getContentModalityText(modality: ContentModality): string {
  const modalityMap: Record<ContentModality, string> = {
    [ContentModality.TEXT_ONLY]: '仅文本',
    [ContentModality.IMAGE_ONLY]: '仅图像',
    [ContentModality.AUDIO_ONLY]: '仅音频',
    [ContentModality.VIDEO_ONLY]: '仅视频',
    [ContentModality.MULTIMODAL]: '多模态'
  };
  return modalityMap[modality] || modality;
}

/**
 * 工具函数：获取隐私级别文本
 */
export function getPrivacyLevelText(level: ClassificationPrivacyLevel): string {
  const levelMap: Record<ClassificationPrivacyLevel, string> = {
    [ClassificationPrivacyLevel.PUBLIC]: '公开',
    [ClassificationPrivacyLevel.PROTECTED]: '受保护',
    [ClassificationPrivacyLevel.PRIVATE]: '私有',
    [ClassificationPrivacyLevel.CONFIDENTIAL]: '机密'
  };
  return levelMap[level] || level;
}

/**
 * 默认导出
 */
export default {
  ClassificationType,
  ContentModality,
  ClassificationPrivacyLevel,
  ClassificationStatus,
  ContentType,
  createClassificationConfig,
  getConfidenceLevel,
  getConfidenceColor,
  getStatusText,
  getStatusColor,
  parseJsonField,
  serializeArray,
  calculateAccuracy,
  formatDate,
  validateClassificationConfig,
  getContentTypeText,
  getClassificationTypeText,
  getContentModalityText,
  getPrivacyLevelText
};