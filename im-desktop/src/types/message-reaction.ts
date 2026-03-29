/**
 * 消息表情回应类型定义
 * @module types/message-reaction
 */

/**
 * 表情回应类型
 */
export enum ReactionType {
  EMOJI = 'EMOJI',
  CUSTOM_EMOJI = 'CUSTOM_EMOJI',
  STICKER = 'STICKER',
  SHORTCUT = 'SHORTCUT',
}

/**
 * 表情回应接口
 */
export interface MessageReaction {
  id: number;
  messageId: number;
  userId: number;
  userName?: string;
  userAvatar?: string;
  conversationId: number;
  emojiCode: string;
  emojiDescription?: string;
  skinTone?: number;
  reactionType: ReactionType;
  isAnonymous?: boolean;
  clientMessageId?: string;
  createdAt: string;
  updatedAt?: string;
}

/**
 * 表情回应DTO
 */
export interface MessageReactionDTO {
  id?: number;
  messageId: number;
  userId: number;
  userName?: string;
  userAvatar?: string;
  conversationId: number;
  emojiCode: string;
  emojiDescription?: string;
  skinTone?: number;
  reactionType: ReactionType;
  isAnonymous?: boolean;
  clientMessageId?: string;
  createdAt?: string;
  updatedAt?: string;
}

/**
 * 表情计数
 */
export interface EmojiCount {
  emojiCode: string;
  emojiDescription?: string;
  count: number;
  userIds: number[];
  isCurrentUserIncluded: boolean;
}

/**
 * 反应用户
 */
export interface ReactionUser {
  userId: number;
  userName: string;
  userAvatar?: string;
  emojiCode: string;
  reactedAt: string;
}

/**
 * 表情回应汇总
 */
export interface ReactionSummary {
  messageId: number;
  conversationId: number;
  totalReactions: number;
  uniqueEmojiCount: number;
  emojiCounts: EmojiCount[];
  recentUsers?: ReactionUser[];
  hasCurrentUserReacted: boolean;
  currentUserEmoji?: string;
}

/**
 * 添加表情回应请求
 */
export interface AddReactionRequest {
  messageId: number;
  userId: number;
  conversationId: number;
  emojiCode: string;
  emojiDescription?: string;
  skinTone?: number;
  reactionType: ReactionType;
  isAnonymous?: boolean;
  clientMessageId?: string;
}

/**
 * 移除表情回应请求
 */
export interface RemoveReactionRequest {
  messageId: number;
  userId: number;
  emojiCode: string;
}

/**
 * 切换表情回应请求
 */
export interface ToggleReactionRequest {
  messageId: number;
  userId: number;
  conversationId: number;
  emojiCode: string;
  emojiDescription?: string;
  reactionType: ReactionType;
}

/**
 * 批量获取汇总请求
 */
export interface BatchSummaryRequest {
  messageIds: number[];
  currentUserId: number;
}

/**
 * 表情回应过滤器
 */
export interface ReactionFilter {
  messageId?: number;
  userId?: number;
  conversationId?: number;
  emojiCode?: string;
  reactionType?: ReactionType;
}

/**
 * 表情回应排序选项
 */
export enum ReactionSortOption {
  NEWEST = 'NEWEST',
  OLDEST = 'OLDEST',
  MOST_POPULAR = 'MOST_POPULAR',
}

/**
 * 分页请求
 */
export interface PageRequest {
  page: number;
  size: number;
  sort?: ReactionSortOption;
}

/**
 * 分页响应
 */
export interface PageResponse<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  number: number;
  size: number;
  first: boolean;
  last: boolean;
}

/**
 * 热门表情
 */
export interface PopularEmoji {
  emojiCode: string;
  count: number;
}

/**
 * 常用表情配置
 */
export interface FrequentEmoji {
  emojiCode: string;
  usageCount: number;
  lastUsedAt: string;
}

/**
 * 用户表情偏好
 */
export interface UserEmojiPreferences {
  userId: number;
  frequentlyUsed: FrequentEmoji[];
  favoriteEmojis: string[];
  customEmojis?: string[];
  skinTone: number;
}

/**
 * 表情选择器配置
 */
export interface EmojiPickerConfig {
  showFrequentlyUsed: boolean;
  showSkinToneSelector: boolean;
  defaultSkinTone: number;
  maxFrequentlyUsed: number;
  categories: EmojiCategory[];
}

/**
 * 表情分类
 */
export interface EmojiCategory {
  id: string;
  name: string;
  icon: string;
  emojiCodes: string[];
}

/**
 * 表情回应事件
 */
export interface ReactionEvent {
  type: 'ADDED' | 'REMOVED' | 'TOGGLED';
  reaction?: MessageReaction;
  messageId: number;
  userId: number;
  emojiCode?: string;
  timestamp: string;
}

/**
 * 表情回应通知
 */
export interface ReactionNotification {
  id: string;
  type: 'REACTION_ADDED' | 'REACTION_REMOVED';
  messageId: number;
  conversationId: number;
  reactorId: number;
  reactorName: string;
  reactorAvatar?: string;
  emojiCode: string;
  timestamp: string;
}

/**
 * 消息表情状态
 */
export interface MessageReactionState {
  messageId: number;
  summary?: ReactionSummary;
  userReactions: MessageReaction[];
  loading: boolean;
  error?: string;
}

/**
 * 创建表情回应错误
 */
export class ReactionError extends Error {
  constructor(
    message: string,
    public code: string,
    public details?: Record<string, unknown>
  ) {
    super(message);
    this.name = 'ReactionError';
  }
}

/**
 * 常用表情代码列表
 */
export const COMMON_EMOJIS: string[] = [
  '👍', '❤️', '😂', '😮', '😢', '😡', '🎉', '🔥',
  '👏', '🙏', '🤔', '👀', '✅', '❌', '⭐', '💯',
  '🚀', '💪', '🤝', '👌', '🤩', '😍', '🤯', '🥳',
  '😎', '🤓', '🤗', '😴', '🤤', '😷', '🤒', '🤕',
];

/**
 * 表情分类定义
 */
export const EMOJI_CATEGORIES: EmojiCategory[] = [
  {
    id: 'smileys',
    name: '表情与情感',
    icon: '😀',
    emojiCodes: ['😀', '😃', '😄', '😁', '😅', '😂', '🤣', '😊', '😇', '🙂', '🙃', '😉', '😌', '😍', '🥰', '😘'],
  },
  {
    id: 'people',
    name: '人物与身体',
    icon: '👍',
    emojiCodes: ['👍', '👎', '👏', '🙌', '🤝', '👊', '✊', '🤛', '🤜', '🤞', '✌️', '🤟', '🤘', '👌', '🤏', '👋'],
  },
  {
    id: 'symbols',
    name: '符号',
    icon: '❤️',
    emojiCodes: ['❤️', '🧡', '💛', '💚', '💙', '💜', '🖤', '🤍', '🤎', '💔', '❣️', '💕', '💞', '💓', '💗', '💖'],
  },
  {
    id: 'objects',
    name: '物品',
    icon: '🎉',
    emojiCodes: ['🎉', '🎊', '🎁', '🎈', '🎀', '🎗️', '🏆', '🥇', '🥈', '🥉', '🏅', '🎖️', '🏵️', '🎗️', '🎫', '🎟️'],
  },
];
