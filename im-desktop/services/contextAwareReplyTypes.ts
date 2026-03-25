/**
 * 上下文感知智能回复生成器 - TypeScript 类型定义
 */

// 意图枚举
export enum ReplyIntent {
  BUSINESS = 'BUSINESS',
  PERSONAL = 'PERSONAL',
  SOCIAL = 'SOCIAL',
  QUESTION = 'QUESTION',
  GREETING = 'GREETING',
  APPRECIATION = 'APPRECIATION',
  EMOTIONAL = 'EMOTIONAL'
}

// 语言风格枚举
export enum LanguageStyle {
  FORMAL = 'FORMAL',
  CASUAL = 'CASUAL',
  FRIENDLY = 'FRIENDLY',
  PROFESSIONAL = 'PROFESSIONAL'
}

// 回复长度枚举
export enum ReplyLength {
  SHORT = 'SHORT',
  MEDIUM = 'MEDIUM',
  LONG = 'LONG'
}

// 状态枚举
export enum ReplyStatus {
  GENERATED = 'GENERATED',
  SELECTED = 'SELECTED',
  REJECTED = 'REJECTED',
  EXPIRED = 'EXPIRED'
}

// 回复记录接口
export interface ContextAwareReply {
  id: number;
  userId: string;
  sessionId?: string;
  triggerMessageId?: string;
  triggerMessageContent?: string;
  contextSummary?: string;
  detectedIntent?: ReplyIntent | string;
  intentConfidence?: number;
  replyCandidates?: string[];
  selectedReply?: string;
  recommendedEmojis?: string[];
  languageStyle?: LanguageStyle | string;
  replyLength?: ReplyLength | string;
  sensitivityCheckResult?: string;
  sensitivityPassed?: boolean;
  personalizationFeatures?: Record<string, any>;
  userFeedbackScore?: number;
  userFeedbackComment?: string;
  used?: boolean;
  generationTimeMs?: number;
  modelVersion?: string;
  generationOptions?: Record<string, any>;
  status: ReplyStatus | string;
  expiresAt?: string;
  createdAt?: string;
  updatedAt?: string;
  indexKey?: string;
}

// 生成请求接口
export interface GenerateReplyRequest {
  userId: string;
  sessionId?: string;
  triggerMessageContent: string;
  context?: Record<string, any>;
  languageStyle?: LanguageStyle | string;
  replyLength?: ReplyLength | string;
  generationOptions?: {
    temperature?: number;
    maxTokens?: number;
    numCandidates?: number;
    [key: string]: any;
  };
}

// 生成响应接口
export interface GenerateReplyResponse {
  reply: ContextAwareReply;
  candidates: string[];
  recommendedEmojis: string[];
  success: boolean;
  message: string;
  generationTimeMs: number;
}

// 反馈请求接口
export interface FeedbackRequest {
  score: number; // 1-5
  comment?: string;
}

// 搜索请求接口
export interface SearchReplyRequest {
  keyword?: string;
  userId?: string;
  sessionId?: string;
  intent?: ReplyIntent | string;
  languageStyle?: LanguageStyle | string;
  startDate?: string;
  endDate?: string;
  status?: ReplyStatus | string;
  used?: boolean;
  minFeedbackScore?: number;
  page?: number;
  size?: number;
}

// 分页响应接口
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

// 统计响应接口
export interface StatisticsResponse {
  totalReplies: number;
  totalUsers: number;
  usedReplies: number;
  highQualityReplies: number;
  averageFeedbackScore: number;
  averageGenerationTimeMs: number;
  intentDistribution: Record<string, number>;
  languageStyleDistribution: Record<string, number>;
  statusDistribution: Record<string, number>;
}

// 健康检查响应接口
export interface HealthResponse {
  status: 'UP' | 'DOWN';
  timestamp: string;
  totalReplies: number;
  usedReplies: number;
  highQualityReplies: number;
  averageFeedbackScore: number;
  averageGenerationTimeMs: number;
  version: string;
  details: Record<string, any>;
}

// 用户意图统计接口
export interface UserIntentStats {
  intent: string;
  count: number;
  percentage: number;
}

// 用户语言风格统计接口
export interface UserLanguageStyleStats {
  style: string;
  count: number;
  percentage: number;
}

// 回复质量指标接口
export interface ReplyQualityMetrics {
  totalGenerated: number;
  usedRate: number;
  highQualityRate: number;
  averageScore: number;
  averageGenerationTime: number;
}

// 工具函数：判断回复是否高质量
export const isHighQuality = (reply: ContextAwareReply): boolean => {
  return (reply.userFeedbackScore ?? 0) >= 4;
};

// 工具函数：判断回复是否已过期
export const isExpired = (reply: ContextAwareReply): boolean => {
  if (!reply.expiresAt) return false;
  return new Date(reply.expiresAt) < new Date();
};

// 工具函数：判断回复是否可用
export const isUsable = (reply: ContextAwareReply): boolean => {
  return reply.status === ReplyStatus.GENERATED && 
         !isExpired(reply) && 
         (reply.sensitivityPassed ?? true);
};

// 工具函数：获取意图显示名称
export const getIntentDisplayName = (intent: ReplyIntent | string): string => {
  const displayNames: Record<string, string> = {
    [ReplyIntent.BUSINESS]: '商务',
    [ReplyIntent.PERSONAL]: '个人',
    [ReplyIntent.SOCIAL]: '社交',
    [ReplyIntent.QUESTION]: '提问',
    [ReplyIntent.GREETING]: '问候',
    [ReplyIntent.APPRECIATION]: '感谢',
    [ReplyIntent.EMOTIONAL]: '情感'
  };
  return displayNames[intent as string] || intent;
};

// 工具函数：获取语言风格显示名称
export const getLanguageStyleDisplayName = (style: LanguageStyle | string): string => {
  const displayNames: Record<string, string> = {
    [LanguageStyle.FORMAL]: '正式',
    [LanguageStyle.CASUAL]: '随意',
    [LanguageStyle.FRIENDLY]: '友好',
    [LanguageStyle.PROFESSIONAL]: '专业'
  };
  return displayNames[style as string] || style;
};

// 工具函数：获取状态显示名称
export const getStatusDisplayName = (status: ReplyStatus | string): string => {
  const displayNames: Record<string, string> = {
    [ReplyStatus.GENERATED]: '已生成',
    [ReplyStatus.SELECTED]: '已选择',
    [ReplyStatus.REJECTED]: '已拒绝',
    [ReplyStatus.EXPIRED]: '已过期'
  };
  return displayNames[status as string] || status;
};

// 工具函数：获取反馈评分的emoji
export const getFeedbackScoreEmoji = (score: number): string => {
  if (score >= 5) return '⭐⭐⭐⭐⭐';
  if (score >= 4) return '⭐⭐⭐⭐';
  if (score >= 3) return '⭐⭐⭐';
  if (score >= 2) return '⭐⭐';
  return '⭐';
};

// 工具函数：格式化生成时间
export const formatGenerationTime = (ms: number): string => {
  if (ms < 1000) return `${ms}ms`;
  return `${(ms / 1000).toFixed(2)}s`;
};

// 工具函数：创建空回复对象
export const createEmptyReply = (): ContextAwareReply => ({
  id: 0,
  userId: '',
  status: ReplyStatus.GENERATED,
  createdAt: new Date().toISOString(),
  updatedAt: new Date().toISOString()
});

// 工具函数：验证反馈请求
export const validateFeedbackRequest = (request: FeedbackRequest): boolean => {
  return request.score >= 1 && request.score <= 5;
};

// 工具函数：验证生成请求
export const validateGenerateRequest = (request: GenerateReplyRequest): boolean => {
  return !!request.userId && !!request.triggerMessageContent;
};

// 常量：所有意图列表
export const ALL_INTENTS = Object.values(ReplyIntent);

// 常量：所有语言风格列表
export const ALL_LANGUAGE_STYLES = Object.values(LanguageStyle);

// 常量：所有回复长度列表
export const ALL_REPLY_LENGTHS = Object.values(ReplyLength);

// 常量：所有状态列表
export const ALL_STATUSES = Object.values(ReplyStatus);

// 常量：意图对应的推荐emoji
export const INTENT_EMOJIS: Record<ReplyIntent, string[]> = {
  [ReplyIntent.GREETING]: ['👋', '😊', '🙌'],
  [ReplyIntent.QUESTION]: ['🤔', '💭', '❓'],
  [ReplyIntent.APPRECIATION]: ['🙏', '😄', '👍'],
  [ReplyIntent.BUSINESS]: ['💼', '📊', '✅'],
  [ReplyIntent.PERSONAL]: ['🏠', '❤️', '✨'],
  [ReplyIntent.SOCIAL]: ['🎉', '🍻', '🎊'],
  [ReplyIntent.EMOTIONAL]: ['😢', '😡', '😍']
};

// 常量：语言风格对应的描述
export const LANGUAGE_STYLE_DESCRIPTIONS: Record<LanguageStyle, string> = {
  [LanguageStyle.FORMAL]: '正式场合使用，措辞严谨',
  [LanguageStyle.CASUAL]: '日常交流使用，轻松随意',
  [LanguageStyle.FRIENDLY]: '友好亲切，拉近距离',
  [LanguageStyle.PROFESSIONAL]: '专业术语，商务场景'
};