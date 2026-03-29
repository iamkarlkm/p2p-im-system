export interface GroupCall {
  callId: number;
  conversationId: number;
  initiatorId: number;
  callType: 'video' | 'audio';
  status: 'pending' | 'active' | 'ended';
  currentParticipants: number;
  maxParticipants: number;
  startedAt: string;
  endedAt?: string;
}

export interface GroupCallParticipant {
  userId: number;
  status: 'joined' | 'left' | 'pending';
  isMuted: boolean;
  isVideoEnabled: boolean;
  isScreenSharing: boolean;
  joinedAt: string;
  leftAt?: string;
}

export interface CreateCallRequest {
  conversationId: number;
  callType: 'video' | 'audio';
}

export interface CallState {
  isInCall: boolean;
  callId: number | null;
  isMuted: boolean;
  isVideoEnabled: boolean;
  isScreenSharing: boolean;
  participants: GroupCallParticipant[];
}
