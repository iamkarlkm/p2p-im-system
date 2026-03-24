import { DndSettings, DndSettingsRequest, DndStatus } from '../types/dnd-settings';

const API_BASE = '/api/v1/dnd';

function getHeaders(): HeadersInit {
  const userId = localStorage.getItem('userId') || '1';
  return {
    'Content-Type': 'application/json',
    'X-User-Id': userId,
  };
}

export async function fetchDndSettings(): Promise<DndSettings> {
  const resp = await fetch(`${API_BASE}/settings`, {
    headers: getHeaders(),
  });
  if (!resp.ok) throw new Error(`Failed to fetch DND settings: ${resp.status}`);
  return resp.json();
}

export async function saveDndSettings(settings: DndSettingsRequest): Promise<DndSettings> {
  const resp = await fetch(`${API_BASE}/settings`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify(settings),
  });
  if (!resp.ok) throw new Error(`Failed to save DND settings: ${resp.status}`);
  return resp.json();
}

export async function fetchDndStatus(): Promise<DndStatus> {
  const resp = await fetch(`${API_BASE}/status`, {
    headers: getHeaders(),
  });
  if (!resp.ok) throw new Error(`Failed to fetch DND status: ${resp.status}`);
  return resp.json();
}

export async function deleteDndSettings(): Promise<void> {
  const resp = await fetch(`${API_BASE}/settings`, {
    method: 'DELETE',
    headers: getHeaders(),
  });
  if (!resp.ok) throw new Error(`Failed to delete DND settings: ${resp.status}`);
}

export function formatRepeatDays(repeatDays: string): string {
  const dayNames = ['', '周一', '周二', '周三', '周四', '周五', '周六', '周日'];
  const days = repeatDays.split(',').map(d => parseInt(d.trim())).filter(d => d >= 1 && d <= 7);
  if (days.length === 7) return '每天';
  if (days.length === 5 && !days.includes(6) && !days.includes(7)) return '工作日';
  if (days.length === 2 && days.includes(6) && days.includes(7)) return '周末';
  return days.map(d => dayNames[d]).join('、');
}

export function formatTimeRange(startTime: string, endTime: string): string {
  return `${startTime} - ${endTime}`;
}
