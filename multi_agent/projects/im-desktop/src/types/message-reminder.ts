// Message Reminder Types for IM Desktop

export interface ReminderRequest {
  messageId: number;
  conversationId: number;
  reminderTime: string;
  note?: string;
  repeatType?: string;
  remindBeforeMinutes?: number;
}

export interface ReminderResponse {
  id: number;
  messageId: number;
  conversationId: number;
  reminderTime: string;
  note?: string;
  isTriggered: boolean;
  isDismissed: boolean;
  createdAt: string;
  repeatType?: string;
  remindBeforeMinutes?: number;
  messagePreview?: string;
  conversationName?: string;
}

export interface ReminderState {
  reminders: ReminderResponse[];
  pendingCount: number;
  isLoading: boolean;
  error?: string;
}

export type RepeatType = 'none' | 'daily' | 'weekly' | 'monthly';
