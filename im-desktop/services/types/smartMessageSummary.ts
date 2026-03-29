/**
 * 智能消息摘要类型定义
 * Smart Message Summary Types for IM Desktop
 */

/** 摘要状态枚举 */
export enum SummaryStatus {
  PENDING = 'PENDING',
  PROCESSING = 'PROCESSING',
  COMPLETED = 'COMPLETED',
  FAILED = 'FAILED',
  EXPIRED = 'EXPIRED',
  DELETED = 'DELETED',
  CANCELLED = 'CANCELLED',
  NEEDS_REGEN = 'NEEDS_REGEN'
}

/** 摘要类型枚举 */
export enum SummaryType {
  SINGLE_MESSAGE = 'SINGLE_MESSAGE',
  CONVERSATION = 'CONVERSATION',
  GROUP_CONVERSATION = 'GROUP_CONVERSATION',
  PRIVATE_CONVERSATION = 'PRIVATE_CONVERSATION',
  TOPIC = 'TOPIC',
  TIME_RANGE = 'TIME_RANGE',
  USER_SPEECH = 'USER_SPEECH',
  KEY_DECISIONS = 'KEY_DECISIONS',
  ACTION_PLAN = 'ACTION_PLAN',
  QNA = 'QNA',
  SENTIMENT = 'SENTIMENT',
  MULTILINGUAL = 'MULTILINGUAL',
  CUSTOM = 'CUSTOM'
}

/** 摘要质量枚举 */
export enum SummaryQuality {
  LOW = 'LOW',
  MEDIUM = 'MEDIUM',
  HIGH = 'HIGH',
  EXCELLENT = 'EXCELLENT'
}

/** 智能消息摘要接口 */
export interface SmartMessageSummary {
  id: number;
  sessionId: string;
  messageId?: string;
  userId: string;
  status: SummaryStatus;
  summaryType: SummaryType;
  summaryContent: string;
  originalContent?: string;
  languageCode?: string;
  quality: SummaryQuality;
  qualityScore: number;
  summaryLength?: number;
  targetLength?: number;
  version: number;
  summaryStyle?: string;
  keyPoints?: string[];
  metadata?: Record<string, any>;
  userRating?: number;
  userFeedback?: string;
  isFavorite: boolean;
  offlineCached: boolean;
  cacheExpiryTime?: string;
  createdAt: string;
  updatedAt: string;
  generatedAt?: string;
  isRead: boolean;
  readAt?: string;
  sharedUserIds?: string[];
  tags?: string[];
  businessData?: Record<string, any>;
  deleted: boolean;
  deletedAt?: string;
}

/** 创建摘要请求 */
export interface CreateSummaryRequest {
  sessionId: string;
  userId: string;
  messageId?: string;
  summaryType: SummaryType;
  summaryContent: string;
  originalContent?: string;
  languageCode?: string;
  summaryStyle?: string;
  targetLength?: number;
}

/** 更新摘要请求 */
export interface UpdateSummaryRequest {
  summaryContent?: string;
  status?: SummaryStatus;
  quality?: SummaryQuality;
  qualityScore?: number;
  summaryStyle?: string;
  userRating?: number;
  userFeedback?: string;
  isFavorite?: boolean;
  offlineCached?: boolean;
  cacheExpiryTime?: string;
  tags?: string[];
}

/** 生成摘要请求 */
export interface GenerateSummaryRequest {
  sessionId: string;
  userId: string;
  summaryType: SummaryType;
  originalContent: string;
}

/** 查询参数 */
export interface SummaryQueryParams {
  page?: number;
  size?: number;
  status?: SummaryStatus;
  quality?: SummaryQuality;
  summaryType?: SummaryType;
  keyword?: string;
  startDate?: string;
  endDate?: string;
}

/** 分页响应 */
export interface PaginationInfo {
  totalElements: number;
  totalPages: number;
  currentPage: number;
  pageSize: number;
  hasNext: boolean;
  hasPrevious: boolean;
}

/** API 响应基础类型 */
export interface ApiResponse<T> {
  success: boolean;
  data?: T;
  message?: string;
  error?: string;
}

/** 分页响应 */
export interface PageResponse<T> {
  summaries: T[];
  pagination: PaginationInfo;
}

/** 用户统计信息 */
export interface UserSummaryStats {
  totalCount: number;
  completedCount: number;
  pendingCount: number;
  highQualityCount: number;
  excellentCount: number;
  qualityDistribution: Record<string, number>;
  styleStats: Record<string, { count: number; averageScore: number }>;
}

/** 系统统计信息 */
export interface SystemSummaryStats {
  totalSummaries: number;
  needsRegenerationCount: number;
  expiredCacheCount: number;
  highQualitySummaries: number;
  lowQualitySummaries: number;
}

/** 批量操作请求 */
export interface BatchOperationRequest {
  ids: number[];
  status?: SummaryStatus;
}

/** 摘要风格选项 */
export const SUMMARY_STYLES = [
  { value: 'concise', label: '简洁' },
  { value: 'detailed', label: '详细' },
  { value: 'bullet', label: '要点式' },
  { value: 'narrative', label: '叙事式' },
  { value: 'formal', label: '正式' },
  { value: 'casual', label: '随意' }
] as const;

/** 摘要类型选项 */
export const SUMMARY_TYPE_OPTIONS = [
  { value: SummaryType.SINGLE_MESSAGE, label: '单条消息' },
  { value: SummaryType.CONVERSATION, label: '会话摘要' },
  { value: SummaryType.GROUP_CONVERSATION, label: '群聊摘要' },
  { value: SummaryType.PRIVATE_CONVERSATION, label: '私聊摘要' },
  { value: SummaryType.KEY_DECISIONS, label: '关键决策' },
  { value: SummaryType.ACTION_PLAN, label: '行动计划' },
  { value: SummaryType.QNA, label: '问题解答' }
] as const;

/** 质量颜色映射 */
export const QUALITY_COLORS: Record<SummaryQuality, string> = {
  [SummaryQuality.LOW]: '#ff4444',
  [SummaryQuality.MEDIUM]: '#ffaa00',
  [SummaryQuality.HIGH]: '#00aa00',
  [SummaryQuality.EXCELLENT]: '#0088ff'
};

/** 质量图标映射 */
export const QUALITY_ICONS: Record<SummaryQuality, string> = {
  [SummaryQuality.LOW]: '⚠️',
  [SummaryQuality.MEDIUM]: '🔶',
  [SummaryQuality.HIGH]: '✅',
  [SummaryQuality.EXCELLENT]: '⭐'
};

/** 状态颜色映射 */
export const STATUS_COLORS: Record<SummaryStatus, string> = {
  [SummaryStatus.PENDING]: '#999999',
  [SummaryStatus.PROCESSING]: '#0088ff',
  [SummaryStatus.COMPLETED]: '#00aa00',
  [SummaryStatus.FAILED]: '#ff4444',
  [SummaryStatus.EXPIRED]: '#ffaa00',
  [SummaryStatus.DELETED]: '#666666',
  [SummaryStatus.CANCELLED]: '#999999',
  [SummaryStatus.NEEDS_REGEN]: '#ff6600'
};

/** 工具函数：格式化质量显示 */
export const formatQuality = (quality: SummaryQuality): string => {
  const labels: Record<SummaryQuality, string> = {
    [SummaryQuality.LOW]: '低质量',
    [SummaryQuality.MEDIUM]: '中等',
    [SummaryQuality.HIGH]: '高质量',
    [SummaryQuality.EXCELLENT]: '优质'
  };
  return labels[quality];
};

/** 工具函数：格式化状态显示 */
export const formatStatus = (status: SummaryStatus): string => {
  const labels: Record<SummaryStatus, string> = {
    [SummaryStatus.PENDING]: '待处理',
    [SummaryStatus.PROCESSING]: '处理中',
    [SummaryStatus.COMPLETED]: '已完成',
    [SummaryStatus.FAILED]: '失败',
    [SummaryStatus.EXPIRED]: '已过期',
    [SummaryStatus.DELETED]: '已删除',
    [SummaryStatus.CANCELLED]: '已取消',
    [SummaryStatus.NEEDS_REGEN]: '需重新生成'
  };
  return labels[status];
};

/** 工具函数：检查是否为高质量 */
export const isHighQuality = (summary: SmartMessageSummary): boolean => {
  return summary.quality === SummaryQuality.HIGH || 
         summary.quality === SummaryQuality.EXCELLENT ||
         summary.qualityScore >= 80;
};

/** 工具函数：检查是否需要重新生成 */
export const needsRegeneration = (summary: SmartMessageSummary): boolean => {
  return summary.qualityScore < 60 || summary.status === SummaryStatus.NEEDS_REGEN;
};

/** 工具函数：检查是否已过期 */
export const isExpired = (summary: SmartMessageSummary): boolean => {
  if (!summary.cacheExpiryTime) return false;
  return new Date(summary.cacheExpiryTime) < new Date();
};
