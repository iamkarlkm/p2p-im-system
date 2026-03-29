/**
 * 消息定时撤回类型定义
 */

export enum ConversationType {
  PRIVATE = 'PRIVATE',
  GROUP = 'GROUP',
  CHANNEL = 'CHANNEL',
}

export enum RecallStatus {
  PENDING = 'PENDING',
  EXECUTED = 'EXECUTED',
  CANCELLED = 'CANCELLED',
  FAILED = 'FAILED',
  EXPIRED = 'EXPIRED',
}

export interface ScheduledMessageRecall {
  id: number;
  userId: number;
  messageId: number;
  conversationId: number;
  conversationType: ConversationType;
  messageContent?: string;
  messageContentPreview?: string;
  scheduledRecallTime: string;
  scheduledSeconds: number;
  status: RecallStatus;
  statusDisplay?: string;
  recallReason?: string;
  notifyReceivers?: boolean;
  customNotifyMessage?: string;
  isCancelable?: boolean;
  cancelDeadline?: string;
  executedAt?: string;
  createdAt: string;
  updatedAt: string;
  // 扩展字段
  remainingSeconds?: number;
  canCancel?: boolean;
  senderName?: string;
  senderAvatar?: string;
  conversationName?: string;
}

export interface CreateScheduledRecallRequest {
  messageId: number;
  conversationId: number;
  conversationType: ConversationType;
  messageContent?: string;
  scheduledSeconds: number;
  recallReason?: string;
  notifyReceivers?: boolean;
  customNotifyMessage?: string;
  isCancelable?: boolean;
}

export interface UpdateScheduledTimeRequest {
  newSeconds: number;
}

export interface ScheduledRecallStats {
  pendingCount: number;
  totalCount: number;
}

export interface ScheduledRecallCheckResult {
  isScheduled: boolean;
}

export interface ScheduledRecallExecuteResult {
  success: boolean;
}

export interface ScheduledRecallBatchResult {
  executedCount: number;
}

export interface ScheduledRecallCleanupResult {
  deletedCount: number;
}

// 推荐的时间选项（秒）
export const RECOMMENDED_TIME_OPTIONS = [30, 60, 120, 300, 600, 1800, 3600];

// 格式化时间选项
export function formatTimeOption(seconds: number): string {
  if (seconds < 60) return `${seconds}秒`;
  if (seconds < 3600) return `${Math.floor(seconds / 60)}分钟`;
  return `${Math.floor(seconds / 3600)}小时`;
}

// 获取状态显示文本
export function getStatusDisplay(status: RecallStatus): string {
  const statusMap: Record<RecallStatus, string> = {
    [RecallStatus.PENDING]: '待执行',
    [RecallStatus.EXECUTED]: '已撤回',
    [RecallStatus.CANCELLED]: '已取消',
    [RecallStatus.FAILED]: '执行失败',
    [RecallStatus.EXPIRED]: '已过期',
  };
  return statusMap[status] || '未知';
}

// 获取状态颜色
export function getStatusColor(status: RecallStatus): string {
  const colorMap: Record<RecallStatus, string> = {
    [RecallStatus.PENDING]: '#FF9800',
    [RecallStatus.EXECUTED]: '#4CAF50',
    [RecallStatus.CANCELLED]: '#9E9E9E',
    [RecallStatus.FAILED]: '#F44336',
    [RecallStatus.EXPIRED]: '#795548',
  };
  return colorMap[status] || '#000000';
}

// 检查是否可以取消
export function canCancelRecall(recall: ScheduledMessageRecall): boolean {
  if (recall.status !== RecallStatus.PENDING) return false;
  if (recall.isCancelable === false) return false;
  
  const now = new Date();
  if (recall.cancelDeadline && new Date(recall.cancelDeadline) < now) {
    return false;
  }
  if (recall.scheduledRecallTime && new Date(recall.scheduledRecallTime) < now) {
    return false;
  }
  return true;
}

// 计算剩余秒数
export function getRemainingSeconds(recall: ScheduledMessageRecall): number {
  if (recall.status !== RecallStatus.PENDING || !recall.scheduledRecallTime) {
    return 0;
  }
  const now = new Date();
  const scheduledTime = new Date(recall.scheduledRecallTime);
  if (now >= scheduledTime) return 0;
  return Math.floor((scheduledTime.getTime() - now.getTime()) / 1000);
}

// 格式化剩余时间
export function formatRemainingTime(seconds: number): string {
  if (seconds <= 0) return '即将撤回';
  if (seconds < 60) return `${seconds}秒后撤回`;
  if (seconds < 3600) {
    const minutes = Math.floor(seconds / 60);
    const remainingSecs = seconds % 60;
    return `${minutes}分${remainingSecs}秒后撤回`;
  }
  const hours = Math.floor(seconds / 3600);
  const remainingMins = Math.floor((seconds % 3600) / 60);
  return `${hours}小时${remainingMins}分后撤回`;
}

// 获取定时时间显示
export function getScheduledTimeDisplay(scheduledTime: string): string {
  const now = new Date();
  const scheduled = new Date(scheduledTime);
  const diffMs = scheduled.getTime() - now.getTime();
  const diffSecs = Math.floor(diffMs / 1000);
  const diffMins = Math.floor(diffSecs / 60);
  const diffHours = Math.floor(diffMins / 60);
  const diffDays = Math.floor(diffHours / 24);

  if (diffDays > 0) return `${diffDays}天后`;
  if (diffHours > 0) return `${diffHours}小时后`;
  if (diffMins > 0) return `${diffMins}分钟后`;
  return `${diffSecs}秒后`;
}
