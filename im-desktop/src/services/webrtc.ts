/**
 * WebRTC服务 - 桌面端
 * 实时音视频通话功能
 */

import { eventBus } from './event_bus';
import { NotificationService } from './notification';

// ICE服务器配置
const ICE_SERVERS = [
  { urls: 'stun:stun.l.google.com:19302' },
  { urls: 'stun:stun1.l.google.com:19302' },
];

// 通话类型
export type CallType = 'audio' | 'video';

// 通话状态
export type CallStatus = 
  | 'idle' 
  | 'calling' 
  | 'ringing' 
  | 'connected' 
  | 'ended' 
  | 'failed';

// 信令消息类型
type SignalingMessage = {
  type: string;
  [key: string]: any;
};

class WebRTCService {
  private peerConnection: RTCPeerConnection | null = null;
  private localStream: MediaStream | null = null;
  private remoteStream: MediaStream | null = null;
  
  private ws: WebSocket | null = null;
  private currentUserId: string = '';
  private targetUserId: string = '';
  
  private callStatus: CallStatus = 'idle';
  private callType: CallType = 'audio';
  private callId: string = '';
  
  private audioEnabled: boolean = true;
  private videoEnabled: boolean = true;
  
  private reconnectAttempts: number = 0;
  private maxReconnectAttempts: number = 3;
  private pingInterval: NodeJS.Timeout | null = null;

  /**
   * 初始化服务
   */
  async init(userId: string): Promise<void> {
    this.currentUserId = userId;
    this.connectSignaling();
  }

  /**
   * 连接信令服务器
   */
  private connectSignaling(): void {
    const wsUrl = `ws://localhost:8080/ws/signaling?userId=${this.currentUserId}`;
    
    try {
      this.ws = new WebSocket(wsUrl);
      
      this.ws.onopen = () => {
        console.log('WebRTC Signaling connected');
        this.reconnectAttempts = 0;
        this.startPing();
      };
      
      this.ws.onmessage = (event) => {
        const message: SignalingMessage = JSON.parse(event.data);
        this.handleSignalingMessage(message);
      };
      
      this.ws.onclose = () => {
        console.log('WebRTC Signaling disconnected');
        this.stopPing();
        this.attemptReconnect();
      };
      
      this.ws.onerror = (error) => {
        console.error('WebRTC Signaling error:', error);
      };
    } catch (error) {
      console.error('Failed to connect to signaling server:', error);
    }
  }

  /**
   * 尝试重连
   */
  private attemptReconnect(): void {
    if (this.reconnectAttempts < this.maxReconnectAttempts) {
      this.reconnectAttempts++;
      setTimeout(() => {
        console.log(`Reconnecting... attempt ${this.reconnectAttempts}`);
        this.connectSignaling();
      }, 2000 * this.reconnectAttempts);
    }
  }

  /**
   * 开始心跳
   */
  private startPing(): void {
    this.pingInterval = setInterval(() => {
      this.sendSignaling({ type: 'ping' });
    }, 30000);
  }

  /**
   * 停止心跳
   */
  private stopPing(): void {
    if (this.pingInterval) {
      clearInterval(this.pingInterval);
      this.pingInterval = null;
    }
  }

  /**
   * 发送信令消息
   */
  private sendSignaling(message: any): void {
    if (this.ws && this.ws.readyState === WebSocket.OPEN) {
      this.ws.send(JSON.stringify(message));
    }
  }

  /**
   * 处理信令消息
   */
  private async handleSignalingMessage(message: SignalingMessage): Promise<void> {
    switch (message.type) {
      case 'ice_config':
        console.log('Received ICE config');
        break;
        
      case 'offer':
        await this.handleOffer(message);
        break;
        
      case 'answer':
        await this.handleAnswer(message);
        break;
        
      case 'ice_candidate':
        await this.handleIceCandidate(message);
        break;
        
      case 'incoming_call':
        this.handleIncomingCall(message);
        break;
        
      case 'call_accepted':
        this.handleCallAccepted(message);
        break;
        
      case 'call_rejected':
        this.handleCallRejected(message);
        break;
        
      case 'call_ended':
        this.handleCallEnded(message);
        break;
        
      case 'audio_toggled':
        this.handleAudioToggled(message);
        break;
        
      case 'video_toggled':
        this.handleVideoToggled(message);
        break;
        
      case 'user_joined':
        eventBus.emit('webrtc:user_joined', message);
        break;
        
      case 'user_left':
        eventBus.emit('webrtc:user_left', message);
        break;
    }
  }

  /**
   * 创建RTCPeerConnection
   */
  private async createPeerConnection(): Promise<RTCPeerConnection> {
    const pc = new RTCPeerConnection({ iceServers: ICE_SERVERS });
    
    // 添加本地媒体轨道
    if (this.localStream) {
      this.localStream.getTracks().forEach(track => {
        pc.addTrack(track, this.localStream!);
      });
    }
    
    // 收集ICE候选
    pc.onicecandidate = (event) => {
      if (event.candidate) {
        this.sendSignaling({
          type: 'ice_candidate',
          targetUserId: this.targetUserId,
          candidate: event.candidate.candidate,
          sdpMid: event.candidate.sdpMid,
          sdpMLineIndex: event.candidate.sdpMLineIndex,
        });
      }
    };
    
    // 接收远程媒体轨道
    pc.ontrack = (event) => {
      this.remoteStream = event.streams[0];
      eventBus.emit('webrtc:remote_stream', this.remoteStream);
    };
    
    // 连接状态变化
    pc.onconnectionstatechange = () => {
      console.log('Connection state:', pc.connectionState);
      
      switch (pc.connectionState) {
        case 'connected':
          this.callStatus = 'connected';
          eventBus.emit('webrtc:connected');
          break;
        case 'disconnected':
        case 'failed':
          this.callStatus = 'ended';
          eventBus.emit('webrtc:disconnected');
          break;
      }
    };
    
    // ICE连接状态变化
    pc.oniceconnectionstatechange = () => {
      console.log('ICE connection state:', pc.iceConnectionState);
    };
    
    this.peerConnection = pc;
    return pc;
  }

  /**
   * 发起通话
   */
  async makeCall(targetUserId: string, callType: CallType = 'video'): Promise<void> {
    if (this.callStatus !== 'idle') {
      console.warn('Already in a call');
      return;
    }
    
    this.targetUserId = targetUserId;
    this.callType = callType;
    this.callId = this.generateCallId();
    
    // 获取本地媒体流
    await this.acquireLocalMedia(callType);
    
    // 创建通话记录
    this.callStatus = 'calling';
    eventBus.emit('webrtc:calling', { targetUserId, callType });
    
    // 发送呼叫请求
    this.sendSignaling({
      type: 'call',
      calleeId: targetUserId,
      callType: callType,
    });
    
    // 创建WebRTC连接并发送offer
    const pc = await this.createPeerConnection();
    const offer = await pc.createOffer();
    await pc.setLocalDescription(offer);
    
    this.sendSignaling({
      type: 'offer',
      targetUserId: targetUserId,
      sdp: pc.localDescription?.sdp,
    });
  }

  /**
   * 接收通话
   */
  async acceptCall(callId: string): Promise<void> {
    this.callId = callId;
    
    await this.acquireLocalMedia(this.callType);
    
    this.sendSignaling({
      type: 'accept_call',
      callId: callId,
    });
    
    // 创建answer
    const pc = await this.createPeerConnection();
    // offer已经在handleOffer中处理了
  }

  /**
   * 拒绝通话
   */
  rejectCall(callId: string): void {
    this.sendSignaling({
      type: 'reject_call',
      callId: callId,
    });
    
    this.cleanup();
  }

  /**
   * 挂断通话
   */
  hangup(): void {
    this.sendSignaling({
      type: 'hangup',
      callId: this.callId,
    });
    
    this.cleanup();
  }

  /**
   * 获取本地媒体流
   */
  private async acquireLocalMedia(callType: CallType): Promise<MediaStream> {
    const constraints: MediaStreamConstraints = {
      audio: true,
      video: callType === 'video',
    };
    
    try {
      this.localStream = await navigator.mediaDevices.getUserMedia(constraints);
      eventBus.emit('webrtc:local_stream', this.localStream);
      return this.localStream;
    } catch (error) {
      console.error('Failed to get local media:', error);
      throw error;
    }
  }

  /**
   * 处理收到的offer
   */
  private async handleOffer(message: SignalingMessage): Promise<void> {
    this.targetUserId = message.fromUserId;
    
    const pc = await this.createPeerConnection();
    await pc.setRemoteDescription(new RTCSessionDescription({
      type: 'offer',
      sdp: message.sdp,
    }));
    
    const answer = await pc.createAnswer();
    await pc.setLocalDescription(answer);
    
    this.sendSignaling({
      type: 'answer',
      targetUserId: message.fromUserId,
      sdp: pc.localDescription?.sdp,
    });
  }

  /**
   * 处理收到的answer
   */
  private async handleAnswer(message: SignalingMessage): Promise<void> {
    if (this.peerConnection) {
      await this.peerConnection.setRemoteDescription(new RTCSessionDescription({
        type: 'answer',
        sdp: message.sdp,
      }));
    }
  }

  /**
   * 处理收到的ICE候选
   */
  private async handleIceCandidate(message: SignalingMessage): Promise<void> {
    if (this.peerConnection) {
      await this.peerConnection.addIceCandidate(new RTCIceCandidate({
        candidate: message.candidate,
        sdpMid: message.sdpMid,
        sdpMLineIndex: message.sdpMLineIndex,
      }));
    }
  }

  /**
   * 处理来电
   */
  private handleIncomingCall(message: SignalingMessage): void {
    this.callId = message.callId;
    this.callType = message.callType as CallType;
    this.targetUserId = message.callerId;
    this.callStatus = 'ringing';
    
    NotificationService.show({
      title: '📞 来电',
      body: `${message.callerId} 发起${message.callType === 'video' ? '视频' : '语音'}通话`,
    });
    
    eventBus.emit('webrtc:incoming_call', message);
  }

  /**
   * 处理通话被接受
   */
  private handleCallAccepted(message: SignalingMessage): void {
    this.callStatus = 'connected';
    eventBus.emit('webrtc:call_accepted', message);
  }

  /**
   * 处理通话被拒绝
   */
  private handleCallRejected(message: SignalingMessage): void {
    this.callStatus = 'ended';
    this.cleanup();
    eventBus.emit('webrtc:call_rejected', message);
  }

  /**
   * 处理通话结束
   */
  private handleCallEnded(message: SignalingMessage): void {
    this.cleanup();
    eventBus.emit('webrtc:call_ended', message);
  }

  /**
   * 处理音频切换
   */
  private handleAudioToggled(message: SignalingMessage): void {
    eventBus.emit('webrtc:audio_toggled', message);
  }

  /**
   * 处理视频切换
   */
  private handleVideoToggled(message: SignalingMessage): void {
    eventBus.emit('webrtc:video_toggled', message);
  }

  /**
   * 切换音频
   */
  toggleAudio(): void {
    if (this.localStream) {
      const audioTrack = this.localStream.getAudioTracks()[0];
      if (audioTrack) {
        this.audioEnabled = !this.audioEnabled;
        audioTrack.enabled = this.audioEnabled;
        
        this.sendSignaling({
          type: 'toggle_audio',
          enabled: this.audioEnabled,
        });
        
        eventBus.emit('webrtc:audio_changed', this.audioEnabled);
      }
    }
  }

  /**
   * 切换视频
   */
  toggleVideo(): void {
    if (this.localStream) {
      const videoTrack = this.localStream.getVideoTracks()[0];
      if (videoTrack) {
        this.videoEnabled = !this.videoEnabled;
        videoTrack.enabled = this.videoEnabled;
        
        this.sendSignaling({
          type: 'toggle_video',
          enabled: this.videoEnabled,
        });
        
        eventBus.emit('webrtc:video_changed', this.videoEnabled);
      }
    }
  }

  /**
   * 切换摄像头
   */
  async switchCamera(): Promise<void> {
    if (!this.localStream || this.callType !== 'video') return;
    
    const videoTrack = this.localStream.getVideoTracks()[0];
    if (!videoTrack) return;
    
    // 获取所有摄像头
    const devices = await navigator.mediaDevices.enumerateDevices();
    const cameras = devices.filter(d => d.kind === 'videoinput');
    
    if (cameras.length < 2) return;
    
    const currentCameraId = videoTrack.getSettings().deviceId;
    const currentIndex = cameras.findIndex(c => c.deviceId === currentCameraId);
    const nextIndex = (currentIndex + 1) % cameras.length;
    
    // 停止当前摄像头
    videoTrack.stop();
    
    // 切换到新摄像头
    const newStream = await navigator.mediaDevices.getUserMedia({
      video: { deviceId: { exact: cameras[nextIndex].deviceId } },
    });
    
    const newVideoTrack = newStream.getVideoTracks()[0];
    
    // 替换轨道
    const sender = this.peerConnection?.getSenders().find(s => s.track?.kind === 'video');
    if (sender) {
      await sender.replaceTrack(newVideoTrack);
    }
    
    // 更新本地流
    this.localStream.removeTrack(videoTrack);
    this.localStream.addTrack(newVideoTrack);
    
    eventBus.emit('webrtc:local_stream', this.localStream);
  }

  /**
   * 获取本地媒体流
   */
  getLocalStream(): MediaStream | null {
    return this.localStream;
  }

  /**
   * 获取远程媒体流
   */
  getRemoteStream(): MediaStream | null {
    return this.remoteStream;
  }

  /**
   * 获取通话状态
   */
  getCallStatus(): CallStatus {
    return this.callStatus;
  }

  /**
   * 获取音频状态
   */
  isAudioEnabled(): boolean {
    return this.audioEnabled;
  }

  /**
   * 获取视频状态
   */
  isVideoEnabled(): boolean {
    return this.videoEnabled;
  }

  /**
   * 生成通话ID
   */
  private generateCallId(): string {
    return `call_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`;
  }

  /**
   * 清理资源
   */
  private cleanup(): void {
    // 停止本地媒体流
    if (this.localStream) {
      this.localStream.getTracks().forEach(track => track.stop());
      this.localStream = null;
    }
    
    // 关闭WebRTC连接
    if (this.peerConnection) {
      this.peerConnection.close();
      this.peerConnection = null;
    }
    
    this.remoteStream = null;
    this.callStatus = 'idle';
    this.callId = '';
    
    eventBus.emit('webrtc:cleanup');
  }

  /**
   * 销毁服务
   */
  destroy(): void {
    this.hangup();
    this.stopPing();
    
    if (this.ws) {
      this.ws.close();
      this.ws = null;
    }
  }
}

export const webRTCService = new WebRTCService();
