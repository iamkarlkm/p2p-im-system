import EventEmitter from 'eventemitter3';
import { v4 as uuidv4 } from 'uuid';

export interface MediaStreamConfig {
  video: boolean | MediaTrackConstraints;
  audio: boolean | MediaTrackConstraints;
}

export interface PeerConnectionConfig {
  iceServers: RTCIceServer[];
  iceTransportPolicy?: RTCIceTransportPolicy;
  bundlePolicy?: RTCBundlePolicy;
  rtcpMuxPolicy?: RTCRtcpMuxPolicy;
}

export interface WebRTCCallSession {
  id: string;
  roomId: string;
  userId: string;
  peerConnections: Map<string, RTCPeerConnection>;
  localStream: MediaStream | null;
  remoteStreams: Map<string, MediaStream>;
  screenStream: MediaStream | null;
  isAudioEnabled: boolean;
  isVideoEnabled: boolean;
  isScreenSharing: boolean;
  startTime: Date;
  participants: string[];
}

export interface WebRTCParticipant {
  id: string;
  userId: string;
  displayName: string;
  avatarUrl?: string;
  isAudioEnabled: boolean;
  isVideoEnabled: boolean;
  isScreenSharing: boolean;
  isSpeaking: boolean;
  joinTime: Date;
}

export interface WebRTCSignalingMessage {
  type: 'offer' | 'answer' | 'ice-candidate' | 'join' | 'leave' | 'participant-update';
  sessionId: string;
  targetUserId?: string;
  senderUserId: string;
  data: any;
  timestamp: Date;
}

export type CallState = 'idle' | 'connecting' | 'ringing' | 'connected' | 'reconnecting' | 'ended';
export type CallType = 'audio' | 'video' | 'screen';

class WebRTCService extends EventEmitter {
  private static instance: WebRTCService;
  private currentSession: WebRTCCallSession | null = null;
  private participants: Map<string, WebRTCParticipant> = new Map();
  private signalingSocket: WebSocket | null = null;
  private reconnectAttempts: number = 0;
  private maxReconnectAttempts: number = 5;
  private reconnectTimeout: NodeJS.Timeout | null = null;
  private callState: CallState = 'idle';
  private localVideoElement: HTMLVideoElement | null = null;
  private remoteVideoElements: Map<string, HTMLVideoElement> = new Map();

  private defaultIceServers: RTCIceServer[] = [
    { urls: 'stun:stun.l.google.com:19302' },
    { urls: 'stun:stun1.l.google.com:19302' },
    { urls: 'stun:stun2.l.google.com:19302' },
  ];

  private constructor() {
    super();
  }

  public static getInstance(): WebRTCService {
    if (!WebRTCService.instance) {
      WebRTCService.instance = new WebRTCService();
    }
    return WebRTCService.instance;
  }

  public getCurrentSession(): WebRTCCallSession | null {
    return this.currentSession;
  }

  public getCallState(): CallState {
    return this.callState;
  }

  public getParticipants(): WebRTCParticipant[] {
    return Array.from(this.participants.values());
  }

  public async initializeCall(
    roomId: string,
    userId: string,
    callType: CallType = 'video',
    displayName: string
  ): Promise<void> {
    try {
      this.callState = 'connecting';
      this.emit('callStateChanged', this.callState);

      const sessionId = uuidv4();
      const session: WebRTCCallSession = {
        id: sessionId,
        roomId,
        userId,
        peerConnections: new Map(),
        localStream: null,
        remoteStreams: new Map(),
        screenStream: null,
        isAudioEnabled: true,
        isVideoEnabled: callType !== 'audio',
        isScreenSharing: false,
        startTime: new Date(),
        participants: [],
      };

      this.currentSession = session;

      // 获取本地媒体流
      await this.acquireLocalMedia(callType);

      // 连接信令服务器
      await this.connectSignalingServer(roomId, userId, displayName);

      this.callState = 'connected';
      this.emit('callStateChanged', this.callState);
      this.emit('sessionInitialized', session);

    } catch (error) {
      console.error('Failed to initialize call:', error);
      this.callState = 'ended';
      this.emit('callStateChanged', this.callState);
      this.emit('error', error);
      throw error;
    }
  }

  public async joinCall(
    roomId: string,
    userId: string,
    displayName: string,
    callType: CallType = 'video'
  ): Promise<void> {
    return this.initializeCall(roomId, userId, callType, displayName);
  }

  private async acquireLocalMedia(callType: CallType): Promise<void> {
    if (!this.currentSession) {
      throw new Error('No active session');
    }

    const constraints: MediaStreamConstraints = {
      audio: {
        echoCancellation: true,
        noiseSuppression: true,
        autoGainControl: true,
        sampleRate: 48000,
        channelCount: 2,
      },
      video: callType !== 'audio' ? {
        width: { ideal: 1280, min: 640 },
        height: { ideal: 720, min: 480 },
        frameRate: { ideal: 30, min: 15 },
        facingMode: 'user',
      } : false,
    };

    try {
      const stream = await navigator.mediaDevices.getUserMedia(constraints);
      this.currentSession.localStream = stream;

      // 监听轨道结束事件
      stream.getTracks().forEach(track => {
        track.onended = () => {
          console.log(`Track ended: ${track.kind}`);
          this.emit('trackEnded', { track, kind: track.kind });
        };
      });

      this.emit('localStreamAcquired', stream);
    } catch (error) {
      console.error('Failed to acquire local media:', error);
      throw new Error('无法访问摄像头或麦克风，请检查权限设置');
    }
  }

  private async connectSignalingServer(roomId: string, userId: string, displayName: string): Promise<void> {
    const wsUrl = `wss://api.im.example.com/webrtc/signaling/${roomId}?userId=${userId}&displayName=${encodeURIComponent(displayName)}`;
    
    return new Promise((resolve, reject) => {
      this.signalingSocket = new WebSocket(wsUrl);

      this.signalingSocket.onopen = () => {
        console.log('Signaling server connected');
        this.reconnectAttempts = 0;
        resolve();
      };

      this.signalingSocket.onmessage = (event) => {
        this.handleSignalingMessage(JSON.parse(event.data));
      };

      this.signalingSocket.onerror = (error) => {
        console.error('Signaling server error:', error);
        reject(error);
      };

      this.signalingSocket.onclose = () => {
        console.log('Signaling server disconnected');
        this.handleSignalingDisconnect();
      };
    });
  }

  private handleSignalingMessage(message: WebRTCSignalingMessage): void {
    switch (message.type) {
      case 'offer':
        this.handleRemoteOffer(message);
        break;
      case 'answer':
        this.handleRemoteAnswer(message);
        break;
      case 'ice-candidate':
        this.handleRemoteIceCandidate(message);
        break;
      case 'join':
        this.handleParticipantJoin(message);
        break;
      case 'leave':
        this.handleParticipantLeave(message);
        break;
      case 'participant-update':
        this.handleParticipantUpdate(message);
        break;
    }
  }

  private async handleParticipantJoin(message: WebRTCSignalingMessage): Promise<void> {
    const { userId, displayName, avatarUrl } = message.data;
    
    if (userId === this.currentSession?.userId) return;

    const participant: WebRTCParticipant = {
      id: uuidv4(),
      userId,
      displayName,
      avatarUrl,
      isAudioEnabled: true,
      isVideoEnabled: true,
      isScreenSharing: false,
      isSpeaking: false,
      joinTime: new Date(),
    };

    this.participants.set(userId, participant);
    this.currentSession?.participants.push(userId);

    // 创建对等连接
    await this.createPeerConnection(userId);

    // 发送offer
    await this.sendOffer(userId);

    this.emit('participantJoined', participant);
  }

  private handleParticipantLeave(message: WebRTCSignalingMessage): Promise<void> {
    const { userId } = message.data;
    
    const participant = this.participants.get(userId);
    if (participant) {
      this.participants.delete(userId);
      
      // 清理对等连接
      const pc = this.currentSession?.peerConnections.get(userId);
      if (pc) {
        pc.close();
        this.currentSession?.peerConnections.delete(userId);
      }

      // 清理远程流
      this.currentSession?.remoteStreams.delete(userId);

      this.emit('participantLeft', participant);
    }

    return Promise.resolve();
  }

  private handleParticipantUpdate(message: WebRTCSignalingMessage): void {
    const { userId, updates } = message.data;
    const participant = this.participants.get(userId);
    
    if (participant) {
      Object.assign(participant, updates);
      this.participants.set(userId, participant);
      this.emit('participantUpdated', participant);
    }
  }

  private async createPeerConnection(targetUserId: string): Promise<RTCPeerConnection> {
    if (!this.currentSession) {
      throw new Error('No active session');
    }

    const config: RTCConfiguration = {
      iceServers: this.defaultIceServers,
      iceTransportPolicy: 'all',
      bundlePolicy: 'balanced',
      rtcpMuxPolicy: 'require',
    };

    const pc = new RTCPeerConnection(config);

    // 添加本地轨道
    if (this.currentSession.localStream) {
      this.currentSession.localStream.getTracks().forEach(track => {
        if (this.currentSession?.localStream) {
          pc.addTrack(track, this.currentSession.localStream!);
        }
      });
    }

    // ICE候选事件
    pc.onicecandidate = (event) => {
      if (event.candidate) {
        this.sendSignalingMessage({
          type: 'ice-candidate',
          sessionId: this.currentSession!.id,
          targetUserId,
          senderUserId: this.currentSession!.userId,
          data: { candidate: event.candidate },
          timestamp: new Date(),
        });
      }
    };

    // 连接状态变化
    pc.onconnectionstatechange = () => {
      console.log(`Connection state with ${targetUserId}:`, pc.connectionState);
      this.emit('connectionStateChanged', { userId: targetUserId, state: pc.connectionState });
    };

    // ICE连接状态
    pc.oniceconnectionstatechange = () => {
      console.log(`ICE connection state with ${targetUserId}:`, pc.iceConnectionState);
      if (pc.iceConnectionState === 'failed') {
        pc.restartIce();
      }
    };

    // 远程轨道到达
    pc.ontrack = (event) => {
      const [remoteStream] = event.streams;
      if (remoteStream) {
        this.currentSession?.remoteStreams.set(targetUserId, remoteStream);
        this.emit('remoteStreamAcquired', { userId: targetUserId, stream: remoteStream });
      }
    };

    this.currentSession.peerConnections.set(targetUserId, pc);
    return pc;
  }

  private async sendOffer(targetUserId: string): Promise<void> {
    const pc = this.currentSession?.peerConnections.get(targetUserId);
    if (!pc) return;

    const offer = await pc.createOffer({
      offerToReceiveAudio: true,
      offerToReceiveVideo: true,
    });

    await pc.setLocalDescription(offer);

    this.sendSignalingMessage({
      type: 'offer',
      sessionId: this.currentSession!.id,
      targetUserId,
      senderUserId: this.currentSession!.userId,
      data: { sdp: offer.sdp },
      timestamp: new Date(),
    });
  }

  private async handleRemoteOffer(message: WebRTCSignalingMessage): Promise<void> {
    const { senderUserId, data } = message;
    
    let pc = this.currentSession?.peerConnections.get(senderUserId);
    if (!pc) {
      pc = await this.createPeerConnection(senderUserId);
    }

    await pc.setRemoteDescription(new RTCSessionDescription({
      type: 'offer',
      sdp: data.sdp,
    }));

    const answer = await pc.createAnswer();
    await pc.setLocalDescription(answer);

    this.sendSignalingMessage({
      type: 'answer',
      sessionId: this.currentSession!.id,
      targetUserId: senderUserId,
      senderUserId: this.currentSession!.userId,
      data: { sdp: answer.sdp },
      timestamp: new Date(),
    });
  }

  private async handleRemoteAnswer(message: WebRTCSignalingMessage): Promise<void> {
    const { senderUserId, data } = message;
    const pc = this.currentSession?.peerConnections.get(senderUserId);
    
    if (pc) {
      await pc.setRemoteDescription(new RTCSessionDescription({
        type: 'answer',
        sdp: data.sdp,
      }));
    }
  }

  private async handleRemoteIceCandidate(message: WebRTCSignalingMessage): Promise<void> {
    const { senderUserId, data } = message;
    const pc = this.currentSession?.peerConnections.get(senderUserId);
    
    if (pc && data.candidate) {
      await pc.addIceCandidate(new RTCIceCandidate(data.candidate));
    }
  }

  private sendSignalingMessage(message: WebRTCSignalingMessage): void {
    if (this.signalingSocket?.readyState === WebSocket.OPEN) {
      this.signalingSocket.send(JSON.stringify(message));
    }
  }

  private handleSignalingDisconnect(): void {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      this.callState = 'reconnecting';
      this.emit('callStateChanged', this.callState);

      const delay = Math.min(1000 * Math.pow(2, this.reconnectAttempts), 30000);
      
      this.reconnectTimeout = setTimeout(() => {
        if (this.currentSession) {
          const participant = this.participants.get(this.currentSession.userId);
          this.connectSignalingServer(
            this.currentSession.roomId,
            this.currentSession.userId,
            participant?.displayName || 'Unknown'
          ).catch(console.error);
        }
      }, delay);
    } else {
      this.endCall('signaling-disconnected');
    }
  }

  public async toggleAudio(): Promise<boolean> {
    if (!this.currentSession?.localStream) return false;

    const audioTrack = this.currentSession.localStream.getAudioTracks()[0];
    if (audioTrack) {
      audioTrack.enabled = !audioTrack.enabled;
      this.currentSession.isAudioEnabled = audioTrack.enabled;
      
      this.sendSignalingMessage({
        type: 'participant-update',
        sessionId: this.currentSession.id,
        senderUserId: this.currentSession.userId,
        data: {
          userId: this.currentSession.userId,
          updates: { isAudioEnabled: audioTrack.enabled },
        },
        timestamp: new Date(),
      });

      this.emit('audioToggled', audioTrack.enabled);
      return audioTrack.enabled;
    }
    return false;
  }

  public async toggleVideo(): Promise<boolean> {
    if (!this.currentSession?.localStream) return false;

    const videoTrack = this.currentSession.localStream.getVideoTracks()[0];
    if (videoTrack) {
      videoTrack.enabled = !videoTrack.enabled;
      this.currentSession.isVideoEnabled = videoTrack.enabled;
      
      this.sendSignalingMessage({
        type: 'participant-update',
        sessionId: this.currentSession.id,
        senderUserId: this.currentSession.userId,
        data: {
          userId: this.currentSession.userId,
          updates: { isVideoEnabled: videoTrack.enabled },
        },
        timestamp: new Date(),
      });

      this.emit('videoToggled', videoTrack.enabled);
      return videoTrack.enabled;
    }
    return false;
  }

  public async startScreenShare(): Promise<MediaStream | null> {
    if (!this.currentSession) return null;

    try {
      const screenStream = await navigator.mediaDevices.getDisplayMedia({
        video: {
          cursor: 'always',
          displaySurface: 'monitor',
        } as MediaTrackConstraints,
        audio: false,
      });

      this.currentSession.screenStream = screenStream;
      this.currentSession.isScreenSharing = true;

      // 替换视频轨道
      const screenVideoTrack = screenStream.getVideoTracks()[0];
      
      this.currentSession.peerConnections.forEach(async (pc) => {
        const sender = pc.getSenders().find(s => 
          s.track?.kind === 'video' && !s.track?.label?.includes('screen')
        );
        if (sender) {
          await sender.replaceTrack(screenVideoTrack);
        }
      });

      // 监听屏幕共享结束
      screenVideoTrack.onended = () => {
        this.stopScreenShare();
      };

      this.sendSignalingMessage({
        type: 'participant-update',
        sessionId: this.currentSession.id,
        senderUserId: this.currentSession.userId,
        data: {
          userId: this.currentSession.userId,
          updates: { isScreenSharing: true },
        },
        timestamp: new Date(),
      });

      this.emit('screenShareStarted', screenStream);
      return screenStream;

    } catch (error) {
      console.error('Failed to start screen share:', error);
      return null;
    }
  }

  public async stopScreenShare(): Promise<void> {
    if (!this.currentSession) return;

    const screenStream = this.currentSession.screenStream;
    if (screenStream) {
      screenStream.getTracks().forEach(track => track.stop());
      this.currentSession.screenStream = null;
      this.currentSession.isScreenSharing = false;

      // 恢复摄像头视频
      const videoTrack = this.currentSession.localStream?.getVideoTracks()[0];
      if (videoTrack) {
        this.currentSession.peerConnections.forEach(async (pc) => {
          const sender = pc.getSenders().find(s => s.track?.kind === 'video');
          if (sender) {
            await sender.replaceTrack(videoTrack);
          }
        });
      }

      this.sendSignalingMessage({
        type: 'participant-update',
        sessionId: this.currentSession.id,
        senderUserId: this.currentSession.userId,
        data: {
          userId: this.currentSession.userId,
          updates: { isScreenSharing: false },
        },
        timestamp: new Date(),
      });

      this.emit('screenShareStopped');
    }
  }

  public async switchCamera(): Promise<boolean> {
    if (!this.currentSession?.localStream) return false;

    const videoTrack = this.currentSession.localStream.getVideoTracks()[0];
    if (!videoTrack) return false;

    const currentFacingMode = videoTrack.getSettings().facingMode;
    const newFacingMode = currentFacingMode === 'user' ? 'environment' : 'user';

    try {
      const newStream = await navigator.mediaDevices.getUserMedia({
        video: { facingMode: newFacingMode },
        audio: false,
      });

      const newVideoTrack = newStream.getVideoTracks()[0];

      // 替换所有连接中的轨道
      this.currentSession.peerConnections.forEach(async (pc) => {
        const sender = pc.getSenders().find(s => s.track?.kind === 'video');
        if (sender) {
          await sender.replaceTrack(newVideoTrack);
        }
      });

      // 停止旧轨道
      videoTrack.stop();

      // 更新本地流
      this.currentSession.localStream.removeTrack(videoTrack);
      this.currentSession.localStream.addTrack(newVideoTrack);

      this.emit('cameraSwitched', newFacingMode);
      return true;

    } catch (error) {
      console.error('Failed to switch camera:', error);
      return false;
    }
  }

  public setLocalVideoElement(element: HTMLVideoElement | null): void {
    this.localVideoElement = element;
    if (element && this.currentSession?.localStream) {
      element.srcObject = this.currentSession.localStream;
      element.play().catch(console.error);
    }
  }

  public setRemoteVideoElement(userId: string, element: HTMLVideoElement | null): void {
    if (element) {
      this.remoteVideoElements.set(userId, element);
      const remoteStream = this.currentSession?.remoteStreams.get(userId);
      if (remoteStream) {
        element.srcObject = remoteStream;
        element.play().catch(console.error);
      }
    } else {
      this.remoteVideoElements.delete(userId);
    }
  }

  public endCall(reason: string = 'user-ended'): void {
    if (this.reconnectTimeout) {
      clearTimeout(this.reconnectTimeout);
      this.reconnectTimeout = null;
    }

    // 停止所有媒体流
    this.currentSession?.localStream?.getTracks().forEach(track => track.stop());
    this.currentSession?.screenStream?.getTracks().forEach(track => track.stop());

    // 关闭所有对等连接
    this.currentSession?.peerConnections.forEach(pc => pc.close());

    // 关闭信令连接
    if (this.signalingSocket) {
      this.signalingSocket.close();
      this.signalingSocket = null;
    }

    // 发送离开消息
    if (this.currentSession && this.signalingSocket?.readyState === WebSocket.OPEN) {
      this.sendSignalingMessage({
        type: 'leave',
        sessionId: this.currentSession.id,
        senderUserId: this.currentSession.userId,
        data: { userId: this.currentSession.userId },
        timestamp: new Date(),
      });
    }

    this.callState = 'ended';
    this.emit('callStateChanged', this.callState);
    this.emit('callEnded', { reason, duration: this.getCallDuration() });

    this.currentSession = null;
    this.participants.clear();
    this.remoteVideoElements.clear();
    this.reconnectAttempts = 0;
  }

  public getCallDuration(): number {
    if (!this.currentSession) return 0;
    return Date.now() - this.currentSession.startTime.getTime();
  }

  public getLocalAudioLevel(): number {
    if (!this.currentSession?.localStream) return 0;
    // 简化实现，实际应该使用 AudioContext 分析
    return 0;
  }

  public getRemoteAudioLevel(userId: string): number {
    if (!this.currentSession?.remoteStreams.has(userId)) return 0;
    return 0;
  }

  public async applyVideoFilter(filterType: 'none' | 'blur' | 'grayscale' | 'sepia'): Promise<void> {
    // 视频滤镜实现，实际项目中可能需要使用 Canvas 处理
    this.emit('videoFilterApplied', filterType);
  }

  public async setVideoQuality(quality: 'low' | 'medium' | 'high' | 'hd'): Promise<void> {
    const constraints: MediaTrackConstraints = {
      width: quality === 'hd' ? 1920 : quality === 'high' ? 1280 : quality === 'medium' ? 854 : 640,
      height: quality === 'hd' ? 1080 : quality === 'high' ? 720 : quality === 'medium' ? 480 : 360,
      frameRate: quality === 'low' ? 15 : 30,
    };

    const videoTrack = this.currentSession?.localStream?.getVideoTracks()[0];
    if (videoTrack) {
      await videoTrack.applyConstraints(constraints);
      this.emit('videoQualityChanged', quality);
    }
  }
}

export const webRTCService = WebRTCService.getInstance();
export default webRTCService;
