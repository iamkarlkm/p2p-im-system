import React, { useEffect, useRef, useState, useCallback } from 'react';
import webRTCService, {
  WebRTCParticipant,
  CallState,
  CallType,
} from '../services/webRTCService';
import './VideoCallPanel.css';

interface VideoCallPanelProps {
  roomId: string;
  userId: string;
  displayName: string;
  callType: CallType;
  onCallEnd?: () => void;
}

interface CallStats {
  duration: number;
  localAudioLevel: number;
  remoteAudioLevel: number;
  bitrate: number;
  packetLoss: number;
}

const VideoCallPanel: React.FC<VideoCallPanelProps> = ({
  roomId,
  userId,
  displayName,
  callType,
  onCallEnd,
}) => {
  const localVideoRef = useRef<HTMLVideoElement>(null);
  const [participants, setParticipants] = useState<WebRTCParticipant[]>([]);
  const [callState, setCallState] = useState<CallState>('idle');
  const [isAudioEnabled, setIsAudioEnabled] = useState(true);
  const [isVideoEnabled, setIsVideoEnabled] = useState(callType !== 'audio');
  const [isScreenSharing, setIsScreenSharing] = useState(false);
  const [callDuration, setCallDuration] = useState(0);
  const [showStats, setShowStats] = useState(false);
  const [stats, setStats] = useState<CallStats>({
    duration: 0,
    localAudioLevel: 0,
    remoteAudioLevel: 0,
    bitrate: 0,
    packetLoss: 0,
  });
  const [layoutMode, setLayoutMode] = useState<'grid' | 'spotlight' | 'sidebar'>('grid');
  const [selectedParticipant, setSelectedParticipant] = useState<string | null>(null);
  const [showControls, setShowControls] = useState(true);
  const [isFullscreen, setIsFullscreen] = useState(false);
  const controlsTimeoutRef = useRef<NodeJS.Timeout | null>(null);
  const panelRef = useRef<HTMLDivElement>(null);

  // 初始化通话
  useEffect(() => {
    const initCall = async () => {
      try {
        await webRTCService.initializeCall(roomId, userId, callType, displayName);
        webRTCService.setLocalVideoElement(localVideoRef.current);
      } catch (error) {
        console.error('Failed to initialize call:', error);
      }
    };

    initCall();

    return () => {
      webRTCService.endCall('component-unmount');
    };
  }, [roomId, userId, displayName, callType]);

  // 监听事件
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

    const handleAudioToggled = (enabled: boolean) => {
      setIsAudioEnabled(enabled);
    };

    const handleVideoToggled = (enabled: boolean) => {
      setIsVideoEnabled(enabled);
    };

    const handleScreenShareStarted = () => {
      setIsScreenSharing(true);
    };

    const handleScreenShareStopped = () => {
      setIsScreenSharing(false);
    };

    const handleCallEnded = () => {
      onCallEnd?.();
    };

    webRTCService.on('callStateChanged', handleCallStateChanged);
    webRTCService.on('participantJoined', handleParticipantJoined);
    webRTCService.on('participantLeft', handleParticipantLeft);
    webRTCService.on('participantUpdated', handleParticipantUpdated);
    webRTCService.on('audioToggled', handleAudioToggled);
    webRTCService.on('videoToggled', handleVideoToggled);
    webRTCService.on('screenShareStarted', handleScreenShareStarted);
    webRTCService.on('screenShareStopped', handleScreenShareStopped);
    webRTCService.on('callEnded', handleCallEnded);

    return () => {
      webRTCService.off('callStateChanged', handleCallStateChanged);
      webRTCService.off('participantJoined', handleParticipantJoined);
      webRTCService.off('participantLeft', handleParticipantLeft);
      webRTCService.off('participantUpdated', handleParticipantUpdated);
      webRTCService.off('audioToggled', handleAudioToggled);
      webRTCService.off('videoToggled', handleVideoToggled);
      webRTCService.off('screenShareStarted', handleScreenShareStarted);
      webRTCService.off('screenShareStopped', handleScreenShareStopped);
      webRTCService.off('callEnded', handleCallEnded);
    };
  }, [onCallEnd]);

  // 通话时长计时器
  useEffect(() => {
    if (callState === 'connected') {
      const interval = setInterval(() => {
        const duration = webRTCService.getCallDuration();
        setCallDuration(duration);
        setStats(prev => ({
          ...prev,
          duration,
          localAudioLevel: Math.random() * 100, // 模拟数据
        }));
      }, 1000);
      return () => clearInterval(interval);
    }
  }, [callState]);

  // 鼠标控制显示
  const handleMouseMove = useCallback(() => {
    setShowControls(true);
    if (controlsTimeoutRef.current) {
      clearTimeout(controlsTimeoutRef.current);
    }
    controlsTimeoutRef.current = setTimeout(() => {
      if (callState === 'connected') {
        setShowControls(false);
      }
    }, 3000);
  }, [callState]);

  // 格式化时长
  const formatDuration = (ms: number): string => {
    const seconds = Math.floor(ms / 1000);
    const minutes = Math.floor(seconds / 60);
    const hours = Math.floor(minutes / 60);
    const secs = seconds % 60;
    const mins = minutes % 60;

    if (hours > 0) {
      return `${hours}:${mins.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`;
    }
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  // 控制处理函数
  const handleToggleAudio = async () => {
    await webRTCService.toggleAudio();
  };

  const handleToggleVideo = async () => {
    await webRTCService.toggleVideo();
  };

  const handleToggleScreenShare = async () => {
    if (isScreenSharing) {
      await webRTCService.stopScreenShare();
    } else {
      await webRTCService.startScreenShare();
    }
  };

  const handleEndCall = () => {
    webRTCService.endCall('user-ended');
    onCallEnd?.();
  };

  const handleToggleFullscreen = () => {
    if (!document.fullscreenElement) {
      panelRef.current?.requestFullscreen();
      setIsFullscreen(true);
    } else {
      document.exitFullscreen();
      setIsFullscreen(false);
    }
  };

  const handleSwitchLayout = () => {
    const layouts: ('grid' | 'spotlight' | 'sidebar')[] = ['grid', 'spotlight', 'sidebar'];
    const currentIndex = layouts.indexOf(layoutMode);
    setLayoutMode(layouts[(currentIndex + 1) % layouts.length]);
  };

  // 获取布局类名
  const getLayoutClass = () => {
    const participantCount = participants.length + 1;
    
    switch (layoutMode) {
      case 'spotlight':
        return 'layout-spotlight';
      case 'sidebar':
        return 'layout-sidebar';
      case 'grid':
      default:
        if (participantCount === 1) return 'layout-single';
        if (participantCount === 2) return 'layout-two';
        if (participantCount <= 4) return 'layout-four';
        return 'layout-grid';
    }
  };

  // 渲染参与者视频
  const renderParticipantVideos = () => {
    return participants.map(participant => (
      <ParticipantVideo
        key={participant.userId}
        participant={participant}
        isSelected={selectedParticipant === participant.userId}
        onClick={() => setSelectedParticipant(participant.userId)}
        layoutMode={layoutMode}
      />
    ));
  };

  // 渲染状态指示器
  const renderStatusIndicator = () => {
    switch (callState) {
      case 'connecting':
        return <div className="call-status connecting">正在连接...</div>;
      case 'ringing':
        return <div className="call-status ringing">等待接听...</div>;
      case 'reconnecting':
        return <div className="call-status reconnecting">重新连接中...</div>;
      default:
        return null;
    }
  };

  return (
    <div
      ref={panelRef}
      className={`video-call-panel ${isFullscreen ? 'fullscreen' : ''} ${getLayoutClass()}`}
      onMouseMove={handleMouseMove}
    >
      {/* 主视频区域 */}
      <div className="video-grid">
        {/* 本地视频 */}
        <div className={`local-video-container ${isScreenSharing ? 'screen-sharing' : ''}`}>
          <video
            ref={localVideoRef}
            className={`local-video ${!isVideoEnabled ? 'video-off' : ''}`}
            autoPlay
            muted
            playsInline
          />
          {!isVideoEnabled && (
            <div className="video-off-indicator">
              <div className="avatar-placeholder">{displayName.charAt(0)}</div>
            </div>
          )}
          <div className="local-video-label">
            <span className="participant-name">{displayName} (我)</span>
            {!isAudioEnabled && <span className="mute-indicator">🔇</span>}
          </div>
          {isScreenSharing && (
            <div className="screen-share-badge">
              <span>📺 正在共享屏幕</span>
            </div>
          )}
        </div>

        {/* 远程参与者视频 */}
        {renderParticipantVideos()}
      </div>

      {/* 状态指示器 */}
      {renderStatusIndicator()}

      {/* 通话信息栏 */}
      <div className={`call-info-bar ${showControls ? 'visible' : 'hidden'}`}>
        <div className="call-info-left">
          <span className="room-id">房间: {roomId}</span>
          <span className="participant-count">{participants.length + 1} 人</span>
        </div>
        <div className="call-info-center">
          <span className="call-duration">{formatDuration(callDuration)}</span>
        </div>
        <div className="call-info-right">
          <span className={`connection-quality quality-good`}>● 连接良好</span>
        </div>
      </div>

      {/* 控制栏 */}
      <div className={`control-bar ${showControls ? 'visible' : 'hidden'}`}>
        <div className="control-group primary">
          <button
            className={`control-btn ${!isAudioEnabled ? 'disabled' : ''}`}
            onClick={handleToggleAudio}
            title={isAudioEnabled ? '静音' : '取消静音'}
          >
            <span className="icon">{isAudioEnabled ? '🎤' : '🔇'}</span>
          </button>

          {callType !== 'audio' && (
            <button
              className={`control-btn ${!isVideoEnabled ? 'disabled' : ''}`}
              onClick={handleToggleVideo}
              title={isVideoEnabled ? '关闭摄像头' : '开启摄像头'}
            >
              <span className="icon">{isVideoEnabled ? '📹' : '📷'}</span>
            </button>
          )}

          <button
            className={`control-btn screen-share ${isScreenSharing ? 'active' : ''}`}
            onClick={handleToggleScreenShare}
            title={isScreenSharing ? '停止共享' : '共享屏幕'}
          >
            <span className="icon">{isScreenSharing ? '🛑' : '📺'}</span>
          </button>

          <button
            className="control-btn end-call"
            onClick={handleEndCall}
            title="结束通话"
          >
            <span className="icon">📞</span>
          </button>
        </div>

        <div className="control-group secondary">
          <button
            className="control-btn"
            onClick={handleSwitchLayout}
            title="切换布局"
          >
            <span className="icon">
              {layoutMode === 'grid' ? '⊞' : layoutMode === 'spotlight' ? '◉' : '☰'}
            </span>
          </button>

          <button
            className={`control-btn ${showStats ? 'active' : ''}`}
            onClick={() => setShowStats(!showStats)}
            title="通话统计"
          >
            <span className="icon">📊</span>
          </button>

          <button
            className="control-btn"
            onClick={handleToggleFullscreen}
            title={isFullscreen ? '退出全屏' : '全屏'}
          >
            <span className="icon">{isFullscreen ? '⛶' : '⛶'}</span>
          </button>
        </div>
      </div>

      {/* 统计面板 */}
      {showStats && (
        <div className="stats-panel">
          <div className="stats-header">
            <h4>通话统计</h4>
            <button onClick={() => setShowStats(false)}>✕</button>
          </div>
          <div className="stats-content">
            <div className="stat-item">
              <span className="stat-label">通话时长</span>
              <span className="stat-value">{formatDuration(stats.duration)}</span>
            </div>
            <div className="stat-item">
              <span className="stat-label">本地音频电平</span>
              <div className="audio-meter">
                <div
                  className="audio-meter-fill"
                  style={{ width: `${stats.localAudioLevel}%` }}
                />
              </div>
            </div>
            <div className="stat-item">
              <span className="stat-label">码率</span>
              <span className="stat-value">{(stats.bitrate / 1000).toFixed(1)} kbps</span>
            </div>
            <div className="stat-item">
              <span className="stat-label">丢包率</span>
              <span className="stat-value">{stats.packetLoss.toFixed(2)}%</span>
            </div>
          </div>
        </div>
      )}

      {/* 参与者列表 */}
      <div className="participants-sidebar">
        <h4>参与者 ({participants.length + 1})</h4>
        <div className="participant-list">
          <div className="participant-item self">
            <div className="participant-avatar">{displayName.charAt(0)}</div>
            <div className="participant-info">
              <span className="participant-name">{displayName} (我)</span>
              <span className="participant-status">
                {!isAudioEnabled && '🔇'}
                {!isVideoEnabled && '📷'}
                {isScreenSharing && '📺'}
              </span>
            </div>
          </div>
          {participants.map(p => (
            <div key={p.userId} className="participant-item">
              <div className="participant-avatar">
                {p.avatarUrl ? (
                  <img src={p.avatarUrl} alt={p.displayName} />
                ) : (
                  p.displayName.charAt(0)
                )}
              </div>
              <div className="participant-info">
                <span className="participant-name">{p.displayName}</span>
                <span className="participant-status">
                  {!p.isAudioEnabled && '🔇'}
                  {!p.isVideoEnabled && '📷'}
                  {p.isScreenSharing && '📺'}
                  {p.isSpeaking && '🎤'}
                </span>
              </div>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
};

// 参与者视频组件
interface ParticipantVideoProps {
  participant: WebRTCParticipant;
  isSelected: boolean;
  onClick: () => void;
  layoutMode: 'grid' | 'spotlight' | 'sidebar';
}

const ParticipantVideo: React.FC<ParticipantVideoProps> = ({
  participant,
  isSelected,
  onClick,
  layoutMode,
}) => {
  const videoRef = useRef<HTMLVideoElement>(null);

  useEffect(() => {
    webRTCService.setRemoteVideoElement(participant.userId, videoRef.current);
    return () => {
      webRTCService.setRemoteVideoElement(participant.userId, null);
    };
  }, [participant.userId]);

  return (
    <div
      className={`remote-video-container ${isSelected ? 'selected' : ''} ${layoutMode}`}
      onClick={onClick}
    >
      <video
        ref={videoRef}
        className={`remote-video ${!participant.isVideoEnabled ? 'video-off' : ''}`}
        autoPlay
        playsInline
      />
      {!participant.isVideoEnabled && (
        <div className="video-off-indicator">
          <div className="avatar-placeholder">
            {participant.avatarUrl ? (
              <img src={participant.avatarUrl} alt={participant.displayName} />
            ) : (
              participant.displayName.charAt(0)
            )}
          </div>
        </div>
      )}
      <div className="remote-video-label">
        <span className="participant-name">{participant.displayName}</span>
        {!participant.isAudioEnabled && <span className="mute-indicator">🔇</span>}
        {participant.isSpeaking && <span className="speaking-indicator">🎤</span>}
      </div>
      {participant.isScreenSharing && (
        <div className="screen-share-badge">
          <span>📺 正在共享屏幕</span>
        </div>
      )}
    </div>
  );
};

export default VideoCallPanel;
