import { apiClient } from './apiClient';

export type CallType = 'AUDIO' | 'VIDEO';
export type CallStatus = 'INITIATED' | 'RINGING' | 'ANSWERED' | 'ENDED' | 'MISSED' | 'REJECTED' | 'FAILED';

export interface CallRecord {
  id: number;
  callId: string;
  callerId: string;
  callerName: string;
  calleeId: string;
  calleeName: string;
  conversationId: string;
  callType: CallType;
  status: CallStatus;
  startTime: string;
  answerTime?: string;
  endTime?: string;
  duration?: number;
  endedByCaller?: boolean;
}

export interface InitiateRequest {
  calleeId: string;
  conversationId: string;
  callType: CallType;
}

class CallService {
  async initiateCall(data: InitiateRequest): Promise<CallRecord> {
    const resp = await apiClient.post<CallRecord>('/calls/initiate', data);
    return resp.data;
  }

  async updateStatus(callId: string, status: string): Promise<CallRecord> {
    const resp = await apiClient.put<CallRecord>(`/calls/${encodeURIComponent(callId)}/status`, { status });
    return resp.data;
  }

  async endCall(callId: string, endedByCaller: boolean): Promise<CallRecord> {
    const resp = await apiClient.post<CallRecord>(`/calls/${encodeURIComponent(callId)}/end`, { endedByCaller });
    return resp.data;
  }

  async markMissed(callId: string): Promise<void> {
    await apiClient.post(`/calls/${encodeURIComponent(callId)}/missed`);
  }

  async getCallHistory(page: number = 0, size: number = 20): Promise<{ content: CallRecord[]; totalPages: number; totalElements: number }> {
    const resp = await apiClient.get('/calls/history', { params: { page, size } });
    return resp.data;
  }

  async getMissedCalls(): Promise<CallRecord[]> {
    const resp = await apiClient.get<CallRecord[]>('/calls/missed');
    return resp.data;
  }

  async deleteCall(id: number): Promise<void> {
    await apiClient.delete(`/calls/${id}`);
  }

  formatDuration(seconds?: number): string {
    if (!seconds) return '0s';
    const m = Math.floor(seconds / 60);
    const s = seconds % 60;
    return m > 0 ? `${m}分${s}秒` : `${s}秒`;
  }

  formatCallType(type: CallType): string {
    return type === 'AUDIO' ? '语音通话' : '视频通话';
  }

  getStatusLabel(status: CallStatus): string {
    const map: Record<CallStatus, string> = {
      INITIATED: '已拨打',
      RINGING: '响铃中',
      ANSWERED: '已接听',
      ENDED: '已结束',
      MISSED: '未接来电',
      REJECTED: '已拒绝',
      FAILED: '呼叫失败',
    };
    return map[status] || status;
  }
}

export const callService = new CallService();
