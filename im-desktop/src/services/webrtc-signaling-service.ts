import { io, Socket } from 'socket.io-client';
import {
  SignalRequest,
  SignalResponse,
  WebRTCConfig,
  CallSession,
  CallStatus,
  SignalType,
} from '../types/webrtc-signal';

class WebRTCSignalingService {
  private socket: Socket | null = null;
  private peerConnection: RTCPeerConnection | null = null;
  private currentSession: CallSession | null = null;
  private localStream: MediaStream | null = null;
  private config: WebRTCConfig | null = null;
  private userId: number = 0;
  private listeners: Map<string, Set<Function>> = new Map();

  async initialize(userId: number, token: string): Promise<void> {
    this.userId = userId;
    this.config = await this.fetchWebRTCConfig();

    this.socket = io('/webrtc', {
      auth: { token },
      transports: ['websocket'],
    });

    this.socket.on('signal', (response: SignalResponse) => {
      this.handleIncomingSignal(response);
    });

    this.socket.on('connect', () => {
      console.log('[WebRTC] Socket connected');
    });

    this.socket.on('disconnect', () => {
      console.log('[WebRTC] Socket disconnected');
    });
  }

  private async fetchWebRTCConfig(): Promise<WebRTCConfig> {
    const resp = await fetch('/api/v1/webrtc/config');
    return resp.json();
  }

  private handleIncomingSignal(response: SignalResponse): void {
    if (response.fromUserId === this.userId) return;

    switch (response.signalType) {
      case SignalType.CALL_INVITE:
        this.emit('call_invite', response);
        break;
      case SignalType.OFFER:
        this.handleOffer(response);
        break;
      case SignalType.ANSWER:
        this.handleAnswer(response);
        break;
      case SignalType.ICE_CANDIDATE:
        this.handleIceCandidate(response);
        break;
      case SignalType.RINGING:
        this.emit('ringing', response);
        break;
      case SignalType.CALL_ACCEPTED:
        this.emit('call_accepted', response);
        break;
      case SignalType.CALL_REJECTED:
        this.endCall();
        this.emit('call_rejected', response);
        break;
      case SignalType.CALL_CANCELLED:
        this.endCall();
        this.emit('call_cancelled', response);
        break;
      case SignalType.CALL_ENDED:
        this.endCall();
        this.emit('call_ended', response);
        break;
      case SignalType.BUSY:
        this.emit('busy', response);
        break;
      case SignalType.NO_ANSWER:
        this.emit('no_answer', response);
        break;
    }
  }

  async initiateCall(targetUserId: number, callType: 'AUDIO' | 'VIDEO'): Promise<string> {
    const roomId = crypto.randomUUID();
    this.currentSession = {
      roomId,
      callerId: this.userId,
      calleeId: targetUserId,
      callType,
      status: 'INITIATING',
      createdAt: new Date(),
    };

    await this.createPeerConnection();
    this.localStream = await navigator.mediaDevices.getUserMedia({
      audio: true,
      video: callType === 'VIDEO',
    });
    this.localStream.getTracks().forEach(track => {
      this.peerConnection!.addTrack(track, this.localStream!);
    });

    const offer = await this.peerConnection!.createOffer();
    await this.peerConnection!.setLocalDescription(offer);

    this.sendSignal({
      roomId,
      userId: this.userId,
      signalType: SignalType.OFFER,
      sdp: offer.sdp ?? '',
      sdpType: offer.type,
      targetUserId,
      callType,
    });

    return roomId;
  }

  private async handleOffer(response: SignalResponse): Promise<void> {
    this.currentSession = {
      roomId: response.roomId,
      callerId: response.fromUserId,
      calleeId: response.toUserId,
      callType: (response.callType as 'AUDIO' | 'VIDEO') ?? 'AUDIO',
      status: 'RINGING',
      createdAt: new Date(),
    };

    this.emit('call_invite', response);
  }

  async acceptCall(roomId: string): Promise<void> {
    await this.createPeerConnection();
    this.localStream = await navigator.mediaDevices.getUserMedia({
      audio: true,
      video: this.currentSession?.callType === 'VIDEO',
    });
    this.localStream.getTracks().forEach(track => {
      this.peerConnection!.addTrack(track, this.localStream!);
    });

    this.sendSignal({
      roomId,
      userId: this.userId,
      signalType: SignalType.ANSWER,
      sdp: this.peerConnection!.localDescription?.sdp ?? '',
      sdpType: 'answer',
    });
  }

  private async handleAnswer(response: SignalResponse): Promise<void> {
    if (this.peerConnection) {
      await this.peerConnection.setRemoteDescription(
        new RTCSessionDescription({ type: response.sdpType!, sdp: response.sdp })
      );
      this.currentSession!.status = 'CONNECTING';
      this.emit('status_change', this.currentSession);
    }
    this.emit('call_accepted', response);
  }

  private async handleIceCandidate(response: SignalResponse): Promise<void> {
    if (this.peerConnection) {
      await this.peerConnection.addIceCandidate(
        new RTCIceCandidate({
          candidate: response.candidate,
          sdpMLineIndex: response.sdpMLineIndex ?? 0,
          sdpMid: response.sdpMid ?? null,
        })
      );
    }
  }

  private async createPeerConnection(): Promise<void> {
    const servers = this.config?.iceServers ?? [
      { urls: 'stun:stun.l.google.com:19302' },
    ];

    this.peerConnection = new RTCPeerConnection({ iceServers: servers });

    this.peerConnection.onicecandidate = (event) => {
      if (event.candidate) {
        this.sendSignal({
          roomId: this.currentSession!.roomId,
          userId: this.userId,
          signalType: SignalType.ICE_CANDIDATE,
          candidate: event.candidate.candidate,
          sdpMLineIndex: event.candidate.sdpMLineIndex ?? 0,
          sdpMid: event.candidate.sdpMid ?? undefined,
        });
      }
    };

    this.peerConnection.ontrack = (event) => {
      if (this.currentSession) {
        this.currentSession.remoteStream = event.streams[0];
        this.emit('remote_stream', this.currentSession);
      }
    };

    this.peerConnection.onconnectionstatechange = () => {
      if (this.currentSession) {
        const state = this.peerConnection!.connectionState;
        if (state === 'connected') {
          this.currentSession.status = 'CONNECTED';
        } else if (['disconnected', 'failed', 'closed'].includes(state)) {
          this.currentSession.status = 'ENDED';
        }
        this.emit('status_change', this.currentSession);
      }
    };
  }

  private sendSignal(request: SignalRequest): void {
    if (this.socket?.connected) {
      this.socket.emit('signal', request);
    }
  }

  async rejectCall(roomId: string): Promise<void> {
    this.sendSignal({
      roomId,
      userId: this.userId,
      signalType: SignalType.CALL_REJECTED,
    });
    this.endCall();
  }

  async cancelCall(roomId: string): Promise<void> {
    this.sendSignal({
      roomId,
      userId: this.userId,
      signalType: SignalType.CALL_CANCELLED,
    });
    this.endCall();
  }

  endCall(): void {
    if (this.localStream) {
      this.localStream.getTracks().forEach(track => track.stop());
      this.localStream = null;
    }
    if (this.peerConnection) {
      this.peerConnection.close();
      this.peerConnection = null;
    }
    if (this.currentSession) {
      this.currentSession.status = 'ENDED';
      this.emit('call_ended', this.currentSession);
    }
    this.currentSession = null;
  }

  toggleMute(muted: boolean): void {
    this.localStream?.getAudioTracks().forEach(track => {
      track.enabled = !muted;
    });
  }

  toggleVideo(enabled: boolean): void {
    this.localStream?.getVideoTracks().forEach(track => {
      track.enabled = enabled;
    });
  }

  on(event: string, callback: Function): void {
    if (!this.listeners.has(event)) {
      this.listeners.set(event, new Set());
    }
    this.listeners.get(event)!.add(callback);
  }

  off(event: string, callback: Function): void {
    this.listeners.get(event)?.delete(callback);
  }

  private emit(event: string, data?: any): void {
    this.listeners.get(event)?.forEach(cb => cb(data));
  }

  getCurrentSession(): CallSession | null {
    return this.currentSession;
  }

  disconnect(): void {
    this.endCall();
    this.socket?.disconnect();
    this.socket = null;
  }
}

export const webrtcService = new WebRTCSignalingService();
export default webrtcService;
