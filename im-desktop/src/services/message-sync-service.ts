import { SyncRequest, SyncResponse, SyncCheckpoint } from '../types/message-sync';

const API_BASE = '/api/v1/sync';

function getHeaders(): HeadersInit {
  const userId = localStorage.getItem('userId') || '1';
  return {
    'Content-Type': 'application/json',
    'X-User-Id': userId,
  };
}

export async function pullSync(request: SyncRequest): Promise<SyncResponse> {
  const resp = await fetch(`${API_BASE}/pull`, {
    method: 'POST',
    headers: getHeaders(),
    body: JSON.stringify(request),
  });
  if (!resp.ok) throw new Error(`Sync pull failed: ${resp.status}`);
  return resp.json();
}

export async function fetchHistory(conversationId: number, lastId?: number, limit = 50): Promise<SyncResponse> {
  const params = new URLSearchParams();
  if (lastId) params.set('lastId', String(lastId));
  params.set('limit', String(limit));
  const resp = await fetch(`${API_BASE}/history/${conversationId}?${params}`, {
    headers: getHeaders(),
  });
  if (!resp.ok) throw new Error(`Fetch history failed: ${resp.status}`);
  return resp.json();
}

export async function fetchCheckpoints(deviceId: string): Promise<SyncCheckpoint[]> {
  const resp = await fetch(`${API_BASE}/checkpoints?deviceId=${deviceId}`, {
    headers: getHeaders(),
  });
  if (!resp.ok) throw new Error(`Fetch checkpoints failed: ${resp.status}`);
  const data = await resp.json();
  return data.checkpoints || [];
}

export async function deleteMessageFromHistory(messageId: number): Promise<void> {
  const resp = await fetch(`${API_BASE}/history/${messageId}`, {
    method: 'DELETE',
    headers: getHeaders(),
  });
  if (!resp.ok) throw new Error(`Delete from history failed: ${resp.status}`);
}

export function getDeviceId(): string {
  let deviceId = localStorage.getItem('deviceId');
  if (!deviceId) {
    deviceId = 'device_' + Math.random().toString(36).substring(2, 15);
    localStorage.setItem('deviceId', deviceId);
  }
  return deviceId;
}
