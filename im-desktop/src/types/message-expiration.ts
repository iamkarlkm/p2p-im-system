/**
 * 消息过期规则类型定义
 */

export type ExpirationType = 'READ_AFTER' | 'SELF_DESTRUCT' | 'TIME_BASED' | 'GLOBAL';
export type MessageTypeFilter = 'TEXT' | 'IMAGE' | 'FILE' | 'ALL';

export interface ExpirationRule {
  id: number;
  userId: number;
  conversationId: string | null;
  conversationType: string | null;
  expirationType: ExpirationType;
  expireTime: string | null; // ISO datetime
  relativeSeconds: number | null;
  active: boolean;
  globalDefault: boolean;
  messageTypeFilter: MessageTypeFilter;
  readDestroySeconds: number | null; // 阅后N秒
  preExpireNotice: boolean;
  preExpireNoticeSeconds: number | null;
  createdAt: string;
  updatedAt: string;
  remainingSeconds: number | null;
}

export interface ExpirationRuleRequest {
  id?: number;
  conversationId?: string;
  conversationType?: string;
  expirationType: ExpirationType;
  expireTime?: string;
  relativeSeconds?: number;
  active?: boolean;
  messageTypeFilter?: MessageTypeFilter;
  readDestroySeconds?: number;
  preExpireNotice?: boolean;
  preExpireNoticeSeconds?: number;
}

export interface ExpirationSettings {
  globalRule: ExpirationRule | null;
  conversationRules: ExpirationRule[];
}

export interface MessageExpirationState {
  rules: ExpirationRule[];
  globalRule: ExpirationRule | null;
  activeTimers: Map<number, NodeJS.Timeout>; // messageId -> timer
}

// WebSocket 事件类型
export interface ExpirationEvent {
  type: 'message_pre_expire' | 'message_destroyed';
  messageId: number;
  remainingSeconds?: number;
}
