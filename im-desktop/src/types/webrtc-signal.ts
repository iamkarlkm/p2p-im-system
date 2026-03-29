export interface SignalRequest {
  roomId: string;
  userId: number;
  signalType: string;
  sdp?: string;
  sdpType?: string;
  candidate?: string;
  sdpMLineIndex?: number;
  sdpMid?: string;
  targetUserId?: number;
  callType?: 'AUDIO' | 'VIDEO';
  action?: string;
}

export interface SignalResponse {
  roomId: string;
  fromUserId: number;
  toUserId: number;
  signalType: string;
  sdp?: string;
  sdpType?: string;
  candidate?: string;
  sdpMLineIndex?: number;
  sdpMid?: string;
  callType?: string;
  status?: string;
  message?: string;
  timestamp: string;
  stunServers?: string;
  turnServers?: string;
  turnUsername?: string;
  turnCredential?: string;
}

export interface WebRTCConfig {
  iceServers: Array<{ urls: string }>;
  iceTransportPolicy: string;
  bundlePolicy: string;
  rtcpMuxPolicy: string;
}

export interface CallSession {
  roomId: string;
  callerId: number;
  calleeId: number;
  callType: 'AUDIO' | 'VIDEO';
  status: CallStatus;
  peerConnection?: RTCPeerConnection;
  localStream?: MediaStream;
  remoteStream?: MediaStream;
  createdAt: Date;
}

export type CallStatus =
  | 'INITIATING'
  | 'RINGING'
  | 'ACCEPTED'
  | 'CONNECTING'
  | 'CONNECTED'
  | 'REJECTED'
  | 'BUSY'
  | 'NO_ANSWER'
  | 'CANCELLED'
  | 'ENDED';

export const SignalType = {
  OFFER: 'offer',
  ANSWER: 'answer',
  ICE_CANDIDATE: 'ice_candidate',
  CALL_INVITE: 'call_invite',
  CALL_ACCEPTED: 'call_accepted',
  CALL_REJECTED: 'call_rejected',
  CALL_CANCELLED: 'call_cancelled',
  CALL_ENDED: 'call_ended',
  RINGING: 'ringing',
  BUSY: 'busy',
  NO_ANSWER: 'no_answer',
} as const;
