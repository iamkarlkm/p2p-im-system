/**
 * 消息编辑系统类型定义
 * 
 * @module types/message-edit
 * @since 2026-03-27
 */

/**
 * 编辑类型枚举
 */
export enum EditType {
  NORMAL = 'NORMAL',
  CORRECTION = 'CORRECTION',
  FORMATTING = 'FORMATTING',
  CONTENT_UPDATE = 'CONTENT_UPDATE',
  REVERT = 'REVERT',
  SYSTEM = 'SYSTEM'
}

/**
 * 差异段类型
 */
export enum DiffType {
  ADDED = 'ADDED',
  REMOVED = 'REMOVED',
  UNCHANGED = 'UNCHANGED'
}

/**
 * 编辑记录
 */
export interface MessageEdit {
  id: number;
  messageId: number;
  userId: number;
  userNickname?: string;
  userAvatar?: string;
  originalContent: string;
  editedContent: string;
  editReason?: string;
  editSequence: number;
  editType: EditType;
  editedAt: string;
  canEditFurther?: boolean;
  remainingEditCount?: number;
  editTimeLimitMinutes?: number;
  showEditMark?: boolean;
  editMarkText?: string;
  contentChangeStats?: ContentChangeStats;
}

/**
 * 内容变化统计
 */
export interface ContentChangeStats {
  originalLength: number;
  editedLength: number;
  changeLength: number;
  changePercentage: number;
  increased: boolean;
  decreased: boolean;
}

/**
 * 差异段
 */
export interface DiffSegment {
  type: DiffType;
  content: string;
  startIndex: number;
  endIndex: number;
}

/**
 * 内容差异
 */
export interface ContentDiff {
  segments: DiffSegment[];
  addedCount: number;
  removedCount: number;
  unchangedCount: number;
}

/**
 * 用户摘要
 */
export interface UserSummary {
  id: number;
  nickname: string;
  avatar?: string;
}

/**
 * 编辑历史项
 */
export interface EditHistoryItem {
  editId: number;
  sequence: number;
  beforeContent: string;
  afterContent: string;
  editReason?: string;
  editType: string;
  editedAt: string;
  editedBy: UserSummary;
  editTimeMillis?: number;
  contentDiff?: ContentDiff;
}

/**
 * 编辑统计
 */
export interface EditStatistics {
  totalEdits: number;
  editsByOwner: number;
  editsByAdmin: number;
  averageEditIntervalMinutes: number;
  totalContentAdded: number;
  totalContentRemoved: number;
  mostActiveEditHour?: string;
  commonEditReasons?: string[];
}

/**
 * 编辑历史DTO
 */
export interface MessageEditHistory {
  messageId: number;
  currentContent: string;
  originalContent: string;
  totalEditCount: number;
  lastEditedAt: string;
  lastEditedBy?: UserSummary;
  canEdit: boolean;
  cannotEditReason?: string;
  editHistory: EditHistoryItem[];
  editTimeWindowMinutes: number;
  maxEditCount: number;
  statistics?: EditStatistics;
}

/**
 * 编辑请求DTO
 */
export interface EditMessageRequest {
  messageId: number;
  originalContent: string;
  editedContent: string;
  editReason?: string;
  editType?: EditType;
}

/**
 * 可编辑检查结果
 */
export interface CanEditResult {
  canEdit: boolean;
  reason: string;
  remainingEditCount?: number;
  editTimeLimitMinutes?: number;
}

/**
 * 批量编辑次数响应
 */
export type EditCountsMap = Record<number, number>;

/**
 * 编辑状态
 */
export interface EditState {
  editingMessageId: number | null;
  originalContent: string;
  editedContent: string;
  editReason: string;
  editType: EditType;
  isSubmitting: boolean;
  error: string | null;
}

/**
 * 编辑弹窗属性
 */
export interface EditModalProps {
  isOpen: boolean;
  messageId: number;
  originalContent: string;
  currentContent: string;
  editCount: number;
  maxEditCount: number;
  remainingEdits: number;
  onClose: () => void;
  onSubmit: (data: EditMessageRequest) => Promise<void>;
}

/**
 * 编辑历史弹窗属性
 */
export interface EditHistoryModalProps {
  isOpen: boolean;
  messageId: number;
  messageContent: string;
  onClose: () => void;
  onRevert?: (sequence: number) => Promise<void>;
}

/**
 * 编辑标记属性
 */
export interface EditMarkProps {
  editCount: number;
  lastEditedAt: string;
  editedBy?: string;
  showTooltip?: boolean;
  onClick?: () => void;
}

/**
 * 编辑器配置
 */
export interface EditorConfig {
  maxLength: number;
  minLength: number;
  showCharacterCount: boolean;
  allowEditReason: boolean;
  editReasonRequired: boolean;
  allowedEditTypes: EditType[];
}

/**
 * 默认编辑器配置
 */
export const DEFAULT_EDITOR_CONFIG: EditorConfig = {
  maxLength: 10000,
  minLength: 1,
  showCharacterCount: true,
  allowEditReason: true,
  editReasonRequired: false,
  allowedEditTypes: [
    EditType.NORMAL,
    EditType.CORRECTION,
    EditType.FORMATTING,
    EditType.CONTENT_UPDATE
  ]
};

/**
 * 编辑类型标签映射
 */
export const EDIT_TYPE_LABELS: Record<EditType, string> = {
  [EditType.NORMAL]: '普通编辑',
  [EditType.CORRECTION]: '纠错',
  [EditType.FORMATTING]: '格式调整',
  [EditType.CONTENT_UPDATE]: '内容更新',
  [EditType.REVERT]: '版本回滚',
  [EditType.SYSTEM]: '系统编辑'
};

/**
 * 编辑类型颜色映射
 */
export const EDIT_TYPE_COLORS: Record<EditType, string> = {
  [EditType.NORMAL]: 'blue',
  [EditType.CORRECTION]: 'orange',
  [EditType.FORMATTING]: 'purple',
  [EditType.CONTENT_UPDATE]: 'green',
  [EditType.REVERT]: 'red',
  [EditType.SYSTEM]: 'gray'
};

/**
 * 差异类型样式映射
 */
export const DIFF_TYPE_STYLES: Record<DiffType, { bg: string; text: string; prefix: string }> = {
  [DiffType.ADDED]: {
    bg: 'bg-green-50',
    text: 'text-green-700',
    prefix: '+'
  },
  [DiffType.REMOVED]: {
    bg: 'bg-red-50',
    text: 'text-red-700',
    prefix: '-'
  },
  [DiffType.UNCHANGED]: {
    bg: 'transparent',
    text: 'text-gray-700',
    prefix: ' '
  }
};
