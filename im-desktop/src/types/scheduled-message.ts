/**
 * 定时消息类型定义
 */

export type ScheduledMessageStatus = 'PENDING' | 'SENT' | 'CANCELLED' | 'FAILED';

export interface ScheduledMessage {
  id: number;
  senderId: number;
  receiverId: number;
  content: string;
  status: ScheduledMessageStatus;
  scheduledTime: string;
  sentTime?: string;
  failureReason?: string;
  createdAt: string;
  receiverNickname?: string;
  receiverAvatar?: string;
}

export interface CreateScheduledMessageRequest {
  receiverId: number;
  content: string;
  scheduledTime: string;
}

export interface UpdateScheduledMessageRequest {
  receiverId?: number;
  content?: string;
  scheduledTime?: string;
}

export interface ScheduledMessageStats {
  pendingCount: number;
}

export const StatusLabel: Record<ScheduledMessageStatus, string> = {
  PENDING: '待发送',
  SENT: '已发送',
  CANCELLED: '已取消',
  FAILED: '发送失败',
};

export const StatusColor: Record<ScheduledMessageStatus, string> = {
  PENDING: 'blue',
  SENT: 'green',
  CANCELLED: 'default',
  FAILED: 'red',
};
