// types/do-not-disturb.ts
export interface DoNotDisturbPeriod {
  id: string;
  userId: string;
  name: string;
  startHour: number;
  startMinute: number;
  endHour: number;
  endMinute: number;
  activeDays: number[];
  isEnabled: boolean;
  allowCalls: boolean;
  allowMentions: boolean;
  createdAt?: string;
  updatedAt?: string;
  isCurrentlyActive?: boolean;
}

export interface CreateDoNotDisturbPeriodRequest {
  name: string;
  startHour: number;
  startMinute: number;
  endHour: number;
  endMinute: number;
  activeDays: number[];
  isEnabled?: boolean;
  allowCalls?: boolean;
  allowMentions?: boolean;
}

export interface UpdateDoNotDisturbPeriodRequest {
  name: string;
  startHour: number;
  startMinute: number;
  endHour: number;
  endMinute: number;
  activeDays: number[];
  allowCalls?: boolean;
  allowMentions?: boolean;
}

export interface DoNotDisturbStatus {
  isInDoNotDisturbMode: boolean;
  shouldAllowCalls: boolean;
  shouldAllowMentions: boolean;
}

export const DAY_NAMES = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];

export function formatTimeRange(period: DoNotDisturbPeriod): string {
  const formatTime = (hour: number, minute: number) => 
    `${hour.toString().padStart(2, '0')}:${minute.toString().padStart(2, '0')}`;
  return `${formatTime(period.startHour, period.startMinute)} - ${formatTime(period.endHour, period.endMinute)}`;
}

export function getActiveDaysText(activeDays: number[]): string {
  if (activeDays.length === 7) return '每天';
  if (activeDays.length === 5 && activeDays.every(d => d >= 1 && d <= 5)) return '工作日';
  if (activeDays.length === 2 && activeDays.every(d => d === 6 || d === 7)) return '周末';
  
  return activeDays.map(d => DAY_NAMES[(d - 1) % 7]).join('、');
}

export function isCurrentlyActive(period: DoNotDisturbPeriod): boolean {
  if (!period.isEnabled) return false;
  
  const now = new Date();
  const currentDay = now.getDay() === 0 ? 7 : now.getDay();
  
  if (!period.activeDays.includes(currentDay)) return false;
  
  const currentMinutes = now.getHours() * 60 + now.getMinutes();
  const startMinutes = period.startHour * 60 + period.startMinute;
  const endMinutes = period.endHour * 60 + period.endMinute;
  
  if (startMinutes <= endMinutes) {
    return currentMinutes >= startMinutes && currentMinutes <= endMinutes;
  } else {
    return currentMinutes >= startMinutes || currentMinutes <= endMinutes;
  }
}
