import React, { useState, useEffect, useCallback } from 'react';
import { webRTCService, WebRTCParticipant, CallState } from '../services/webRTCService';
import './MeetingControls.css';

export interface MeetingSettings {
  enableRecording: boolean;
  enableTranscription: boolean;
  enableNoiseSuppression: boolean;
  enableEchoCancellation: boolean;
  videoQuality: 'low' | 'medium' | 'high' | 'hd';
  audioInputDevice: string;
  audioOutputDevice: string;
  videoInputDevice: string;
}

interface MeetingControlsProps {
  roomId: string;
  onLeaveMeeting: () => void;
  onInviteParticipants: () => void;
}

interface RecordingState {
  isRecording: boolean;
  startTime: Date | null;
  duration: number;
  fileSize: number;
}

const MeetingControls: React.FC<MeetingControlsProps> = ({
  roomId,
  onLeaveMeeting,
  onInviteParticipants,
}) => {
  const [participants, setParticipants] = useState<WebRTCParticipant[]>([]);
  const [callState, setCallState] = useState<CallState>('idle');
  const [showSettings, setShowSettings] = useState(false);
  const [showParticipants, setShowParticipants] = useState(false);
  const [showChat, setShowChat] = useState(false);
  const [showInvite, setShowInvite] = useState(false);
  const [recording, setRecording] = useState<RecordingState>({
    isRecording: false,
    startTime: null,
    duration: 0,
    fileSize: 0,
  });
  const [settings, setSettings] = useState<MeetingSettings>({
    enableRecording: false,
    enableTranscription: false,
    enableNoiseSuppression: true,
    enableEchoCancellation: true,
    videoQuality: 'high',
    audioInputDevice: 'default',
    audioOutputDevice: 'default',
    videoInputDevice: 'default',
  });
  const [devices, setDevices] = useState<{
    audioInputs: MediaDeviceInfo[];
    audioOutputs: MediaDeviceInfo[];
    videoInputs: MediaDeviceInfo[];
  }>({
    audioInputs: [],
    audioOutputs: [],
    videoInputs: [],
  });
  const [chatMessages, setChatMessages] = useState<Array<{
    id: string;
    sender: string;
    message: string;
    timestamp: Date;
    isMe: boolean;
  }>>([]);
  const [newMessage, setNewMessage] = useState('');

  // 监听状态变化
  useEffect(() => {
    const handleCallStateChanged = (state: CallState) => {
      setCallState(state);
    };

    const handleParticipantJoined = (participant: WebRTCParticipant) => {
      setParticipants(prev => [...prev, participant]);
    };

    const handleParticipantLeft = (participant: WebRTCParticipant) => {
      setParticipants(prev => prev.filter(p => p.userId !== participant.userId));
    };

    const handleParticipantUpdated = (participant: WebRTCParticipant) => {
      setParticipants(prev =>
        prev.map(p => (p.userId === participant.userId ? participant : p))
      );
    };

    webRTCService.on('callStateChanged', handleCallStateChanged);
    webRTCService.on('participantJoined', handleParticipantJoined);
    webRTCService.on('participantLeft', handleParticipantLeft);
    webRTCService.on('participantUpdated', handleParticipantUpdated);

    return () => {
      webRTCService.off('callStateChanged', handleCallStateChanged);
      webRTCService.off('participantJoined', handleParticipantJoined);
      webRTCService.off('participantLeft', handleParticipantLeft);
      webRTCService.off('participantUpdated', handleParticipantUpdated);
    };
  }, []);

  // 获取媒体设备列表
  useEffect(() => {
    const getDevices = async () => {
      try {
        const deviceList = await navigator.mediaDevices.enumerateDevices();
        setDevices({
          audioInputs: deviceList.filter(d => d.kind === 'audioinput'),
          audioOutputs: deviceList.filter(d => d.kind === 'audiooutput'),
          videoInputs: deviceList.filter(d => d.kind === 'videoinput'),
        });
      } catch (error) {
        console.error('Failed to get devices:', error);
      }
    };

    getDevices();
  }, []);

  // 录音时长计时
  useEffect(() => {
    let interval: NodeJS.Timeout;
    if (recording.isRecording) {
      interval = setInterval(() => {
        setRecording(prev => ({
          ...prev,
          duration: prev.duration + 1,
          fileSize: prev.fileSize + Math.floor(Math.random() * 1000),
        }));
      }, 1000);
    }
    return () => clearInterval(interval);
  }, [recording.isRecording]);

  // 格式化时长
  const formatDuration = (seconds: number): string => {
    const hours = Math.floor(seconds / 3600);
    const mins = Math.floor((seconds % 3600) / 60);
    const secs = seconds % 60;
    
    if (hours > 0) {
      return `${hours}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  // 格式化文件大小
  const formatFileSize = (bytes: number): string => {
    if (bytes < 1024) return `${bytes} B`;
    if (bytes < 1024 * 1024) return `${(bytes / 1024).toFixed(1)} KB`;
    return `${(bytes / (1024 * 1024)).toFixed(1)} MB`;
  };

  // 开始/停止录音
  const toggleRecording = useCallback(() => {
    if (recording.isRecording) {
      // 停止录音
      setRecording({
        isRecording: false,
        startTime: null,
        duration: 0,
        fileSize: 0,
      });
    } else {
      // 开始录音
      setRecording({
        isRecording: true,
        startTime: new Date(),
        duration: 0,
        fileSize: 0,
      });
    }
  }, [recording.isRecording]);

  // 切换设置
  const toggleSetting = (key: keyof MeetingSettings) => {
    setSettings(prev => ({
      ...prev,
      [key]: !prev[key],
    }));
  };

  // 设置视频质量
  const setVideoQuality = async (quality: MeetingSettings['videoQuality']) => {
    setSettings(prev => ({ ...prev, videoQuality: quality }));
    await webRTCService.setVideoQuality(quality);
  };

  // 发送聊天消息
  const sendChatMessage = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newMessage.trim()) return;

    const message = {
      id: Math.random().toString(36).substr(2, 9),
      sender: '我',
      message: newMessage,
      timestamp: new Date(),
      isMe: true,
    };

    setChatMessages(prev => [...prev, message]);
    setNewMessage('');
  };

  // 复制会议链接
  const copyMeetingLink = () => {
    const link = `https://im.example.com/meeting/${roomId}`;
    navigator.clipboard.writeText(link);
  };

  return (
    <div className="meeting-controls">
      {/* 顶部工具栏 */}
      <div className="meeting-toolbar">
        <div className="toolbar-left">
          <div className="meeting-info">
            <span className="room-label">会议号</span>
            <span className="room-id">{roomId}</span>
            <button className="copy-btn" onClick={copyMeetingLink} title="复制会议链接">
              📋
            </button>
          </div>
          {recording.isRecording && (
            <div className="recording-indicator">
              <span className="recording-dot"></span>
              <span className="recording-time">{formatDuration(recording.duration)}</span>
            </div>
          )}
        </div>

        <div className="toolbar-center">
          <button
            className={`toolbar-btn ${showParticipants ? 'active' : ''}`}
            onClick={() => setShowParticipants(!showParticipants)}
          >
            <span className="icon">👥</span>
            <span className="badge">{participants.length + 1}</span>
          </button>
          <button
            className={`toolbar-btn ${showChat ? 'active' : ''}`}
            onClick={() => setShowChat(!showChat)}
          >
            <span className="icon">💬</span>
            {chatMessages.length > 0 && <span className="badge">{chatMessages.length}</span>}
          </button>
          <button
            className={`toolbar-btn ${recording.isRecording ? 'recording' : ''}`}
            onClick={toggleRecording}
          >
            <span className="icon">⏺</span>
            <span className="label">{recording.isRecording ? '停止' : '录制'}</span>
          </button>
          <button
            className="toolbar-btn"
            onClick={() => setShowSettings(!showSettings)}
          >
            <span className="icon">⚙️</span>
          </button>
        </div>

        <div className="toolbar-right">
          <button className="invite-btn" onClick={onInviteParticipants}>
            <span className="icon">+</span>
            邀请
          </button>
          <button className="leave-btn" onClick={onLeaveMeeting}>
            离开会议
          </button>
        </div>
      </div>

      {/* 参与者面板 */}
      {showParticipants && (
        <div className="side-panel participants-panel">
          <div className="panel-header">
            <h3>参与者 ({participants.length + 1})</h3>
            <button className="close-btn" onClick={() => setShowParticipants(false)}>
              ✕
            </button>
          </div>
          <div className="panel-content">
            <div className="participant-list">
              {participants.map(participant => (
                <div key={participant.userId} className="participant-item">
                  <div className="participant-avatar">
                    {participant.avatarUrl ? (
                      <img src={participant.avatarUrl} alt={participant.displayName} />
                    ) : (
                      participant.displayName.charAt(0)
                    )}
                  </div>
                  <div className="participant-info">
                    <span className="participant-name">{participant.displayName}</span>
                    <span className="participant-status">
                      {participant.isSpeaking && '🎤 发言中'}
                      {participant.isScreenSharing && '📺 共享屏幕'}
                    </span>
                  </div>
                  <div className="participant-actions">
                    {!participant.isAudioEnabled && <span className="status-icon muted">🔇</span>}
                    {!participant.isVideoEnabled && <span className="status-icon">📷</span>}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {/* 聊天面板 */}
      {showChat && (
        <div className="side-panel chat-panel">
          <div className="panel-header">
            <h3>会议聊天</h3>
            <button className="close-btn" onClick={() => setShowChat(false)}>
              ✕
            </button>
          </div>
          <div className="chat-messages">
            {chatMessages.length === 0 ? (
              <div className="empty-chat">暂无消息</div>
            ) : (
              chatMessages.map(msg => (
                <div key={msg.id} className={`chat-message ${msg.isMe ? 'self' : ''}`}>
                  <div className="message-header">
                    <span className="sender">{msg.sender}</span>
                    <span className="time">
                      {msg.timestamp.toLocaleTimeString([], { hour: '2-digit', minute: '2-digit' })}
                    </span>
                  </div>
                  <div className="message-content">{msg.message}</div>
                </div>
              ))
            )}
          </div>
          <form className="chat-input" onSubmit={sendChatMessage}>
            <input
              type="text"
              value={newMessage}
              onChange={(e) => setNewMessage(e.target.value)}
              placeholder="发送消息..."
            />
            <button type="submit" disabled={!newMessage.trim()}>
              发送
            </button>
          </form>
        </div>
      )}

      {/* 设置面板 */}
      {showSettings && (
        <div className="side-panel settings-panel">
          <div className="panel-header">
            <h3>会议设置</h3>
            <button className="close-btn" onClick={() => setShowSettings(false)}>
              ✕
            </button>
          </div>
          <div className="panel-content">
            <div className="settings-section">
              <h4>音频设置</h4>
              <div className="setting-item">
                <label>麦克风</label>
                <select
                  value={settings.audioInputDevice}
                  onChange={(e) => setSettings(prev => ({ ...prev, audioInputDevice: e.target.value }))}
                >
                  {devices.audioInputs.map(device => (
                    <option key={device.deviceId} value={device.deviceId}>
                      {device.label || `麦克风 ${device.deviceId.slice(0, 4)}`}
                    </option>
                  ))}
                </select>
              </div>
              <div className="setting-item">
                <label>扬声器</label>
                <select
                  value={settings.audioOutputDevice}
                  onChange={(e) => setSettings(prev => ({ ...prev, audioOutputDevice: e.target.value }))}
                >
                  {devices.audioOutputs.map(device => (
                    <option key={device.deviceId} value={device.deviceId}>
                      {device.label || `扬声器 ${device.deviceId.slice(0, 4)}`}
                    </option>
                  ))}
                </select>
              </div>
              <div className="setting-toggle">
                <label>降噪</label>
                <button
                  className={`toggle ${settings.enableNoiseSuppression ? 'on' : ''}`}
                  onClick={() => toggleSetting('enableNoiseSuppression')}
                >
                  {settings.enableNoiseSuppression ? '开启' : '关闭'}
                </button>
              </div>
              <div className="setting-toggle">
                <label>回声消除</label>
                <button
                  className={`toggle ${settings.enableEchoCancellation ? 'on' : ''}`}
                  onClick={() => toggleSetting('enableEchoCancellation')}
                >
                  {settings.enableEchoCancellation ? '开启' : '关闭'}
                </button>
              </div>
            </div>

            <div className="settings-section">
              <h4>视频设置</h4>
              <div className="setting-item">
                <label>摄像头</label>
                <select
                  value={settings.videoInputDevice}
                  onChange={(e) => setSettings(prev => ({ ...prev, videoInputDevice: e.target.value }))}
                >
                  {devices.videoInputs.map(device => (
                    <option key={device.deviceId} value={device.deviceId}>
                      {device.label || `摄像头 ${device.deviceId.slice(0, 4)}`}
                    </option>
                  ))}
                </select>
              </div>
              <div className="setting-item">
                <label>视频质量</label>
                <div className="quality-options">
                  {(['low', 'medium', 'high', 'hd'] as const).map(quality => (
                    <button
                      key={quality}
                      className={`quality-btn ${settings.videoQuality === quality ? 'active' : ''}`}
                      onClick={() => setVideoQuality(quality)}
                    >
                      {quality === 'low' && '标清'}
                      {quality === 'medium' && '清晰'}
                      {quality === 'high' && '高清'}
                      {quality === 'hd' && '超清'}
                    </button>
                  ))}
                </div>
              </div>
            </div>

            <div className="settings-section">
              <h4>高级功能</h4>
              <div className="setting-toggle">
                <label>自动录制</label>
                <button
                  className={`toggle ${settings.enableRecording ? 'on' : ''}`}
                  onClick={() => toggleSetting('enableRecording')}
                >
                  {settings.enableRecording ? '开启' : '关闭'}
                </button>
              </div>
              <div className="setting-toggle">
                <label>实时字幕</label>
                <button
                  className={`toggle ${settings.enableTranscription ? 'on' : ''}`}
                  onClick={() => toggleSetting('enableTranscription')}
                >
                  {settings.enableTranscription ? '开启' : '关闭'}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* 状态指示器 */}
      <div className="connection-status">
        <span className={`status-dot ${callState === 'connected' ? 'good' : callState === 'reconnecting' ? 'warning' : 'bad'}`} />
        <span className="status-text">
          {callState === 'connected' && '连接正常'}
          {callState === 'connecting' && '正在连接...'}
          {callState === 'reconnecting' && '重新连接...'}
          {callState === 'ended' && '已断开'}
        </span>
      </div>
    </div>
  );
};

export default MeetingControls;
