export interface DndSettings {
  id?: number;
  userId?: number;
  enabled: boolean;
  startTime: string;
  endTime: string;
  timezone: string;
  repeatDays: string;
  allowMentions: boolean;
  allowStarred: boolean;
  customMessage?: string;
  createdAt?: string;
  updatedAt?: string;
}

export interface DndStatus {
  inDndPeriod: boolean;
  allowMention: boolean;
  allowStarred: boolean;
}

export interface DndSettingsRequest {
  enabled?: boolean;
  startTime?: string;
  endTime?: string;
  timezone?: string;
  repeatDays?: string;
  allowMentions?: boolean;
  allowStarred?: boolean;
  customMessage?: string;
}

export const DEFAULT_DND_SETTINGS: DndSettings = {
  enabled: false,
  startTime: '22:00',
  endTime: '08:00',
  timezone: 'Asia/Shanghai',
  repeatDays: '1,2,3,4,5,6,7',
  allowMentions: true,
  allowStarred: true,
};
