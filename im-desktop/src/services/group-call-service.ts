import { GroupCall, GroupCallParticipant, CreateCallRequest } from '../types/group-call';
import { apiClient } from './api-client';

export class GroupCallService {
  async createCall(request: CreateCallRequest): Promise<GroupCall> {
    const response = await apiClient.post('/calls/group/create', request);
    return response.data;
  }

  async joinCall(callId: number): Promise<void> {
    await apiClient.post(`/calls/group/${callId}/join`);
  }

  async leaveCall(callId: number): Promise<void> {
    await apiClient.post(`/calls/group/${callId}/leave`);
  }

  async toggleMute(callId: number): Promise<void> {
    await apiClient.post(`/calls/group/${callId}/mute`);
  }

  async toggleVideo(callId: number): Promise<void> {
    await apiClient.post(`/calls/group/${callId}/video`);
  }

  async toggleScreenShare(callId: number, enable: boolean): Promise<void> {
    await apiClient.post(`/calls/group/${callId}/screen-share`, { enable });
  }

  async getCallInfo(callId: number): Promise<{ call: GroupCall; participants: GroupCallParticipant[] }> {
    const response = await apiClient.get(`/calls/group/${callId}`);
    return response.data;
  }

  async endCall(callId: number): Promise<void> {
    await apiClient.post(`/calls/group/${callId}/end`);
  }
}

export const groupCallService = new GroupCallService();
