/// WebRTC实时流媒体传输 - 移动端服务层
/// 功能#212 - WebRTC Mobile Service
/// 创建时间: 2026-03-27 01:42

import 'dart:async';
import 'dart:convert';
import 'dart:math';
import 'package:flutter/material.dart';
import 'package:flutter_webrtc/flutter_webrtc.dart';
import '../models/webrtc_models.dart';

/// WebRTC服务 - 管理音视频通话核心逻辑
class WebRTCService extends ChangeNotifier {
  static final WebRTCService _instance = WebRTCService._internal();
  factory WebRTCService() => _instance;
  WebRTCService._internal();

  // 当前会话
  WebRTCSession? _currentSession;
  WebRTCSession? get currentSession => _currentSession;

  // WebRTC核心对象
  RTCPeerConnection? _peerConnection;
  MediaStream? _localStream;
  MediaStream? _remoteStream;
  
  // 视频渲染器
  final RTCVideoRenderer localRenderer = RTCVideoRenderer();
  final RTCVideoRenderer remoteRenderer = RTCVideoRenderer();

  // 通话状态
  CallState _callState = CallState.idle;
  CallState get callState => _callState;

  // 参与者列表
  final Map<String, Participant> _participants = {};
  Map<String, Participant> get participants => Map.unmodifiable(_participants);

  // 网络统计
  NetworkStats? _networkStats;
  NetworkStats? get networkStats => _networkStats;

  // 配置
  WebRTCConfig _config = WebRTCConfig.defaultConfig();
  WebRTCConfig get config => _config;

  // 弱网适配配置
  WeakNetworkConfig _weakNetworkConfig = WeakNetworkConfig.defaultConfig();
  
  // 美颜参数
  BeautyParams _beautyParams = BeautyParams.defaultParams();
  BeautyParams get beautyParams => _beautyParams;

  // 定时器
  Timer? _statsTimer;
  Timer? _reconnectTimer;
  Timer? _callDurationTimer;

  // 重连计数
  int _reconnectAttempts = 0;
  static const int maxReconnectAttempts = 5;

  // 信令WebSocket
  // TODO: 集成实际信令服务
  // WebSocketChannel? _signalingChannel;

  // Stream控制器
  final StreamController<SignalingMessage> _signalingController = 
      StreamController<SignalingMessage>.broadcast();
  Stream<SignalingMessage> get signalingStream => _signalingController.stream;

  final StreamController<NetworkStats> _statsController = 
      StreamController<NetworkStats>.broadcast();
  Stream<NetworkStats> get statsStream => _statsController.stream;

  // 事件回调
  Function(String sessionId)? onCallConnected;
  Function(String sessionId)? onCallEnded;
  Function(String error)? onError;
  Function(NetworkQuality quality)? onNetworkQualityChanged;

  /// 初始化
  Future<void> initialize() async {
    await localRenderer.initialize();
    await remoteRenderer.initialize();
  }

  /// 更新配置
  void updateConfig(WebRTCConfig config) {
    _config = config;
    notifyListeners();
  }

  /// 更新美颜参数
  void updateBeautyParams(BeautyParams params) {
    _beautyParams = params;
    _applyBeautyFilter();
    notifyListeners();
  }

  /// 应用美颜滤镜（使用Canvas处理）
  void _applyBeautyFilter() {
    if (!_config.enableBeautyFilter) return;
    
    // TODO: 集成GPUImage或自定义Shader实现美颜
    // 当前版本使用基础参数调节
    debugPrint('应用美颜滤镜: ${_config.beautyFilter.displayName}');
  }

  /// 发起通话
  Future<void> startCall({
    required String calleeId,
    required String calleeName,
    String? calleeAvatar,
    required CallType callType,
  }) async {
    try {
      _setCallState(CallState.calling);
      
      // 创建会话
      _currentSession = WebRTCSession(
        sessionId: _generateSessionId(),
        callerId: 'current_user_id', // TODO: 从用户服务获取
        callerName: 'Current User',
        calleeId: calleeId,
        calleeName: calleeName,
        calleeAvatar: calleeAvatar,
        callType: callType,
        createdAt: DateTime.now(),
      );

      // 获取本地媒体流
      await _getLocalStream();

      // 创建PeerConnection
      await _createPeerConnection();

      // 创建Offer
      final offer = await _peerConnection!.createOffer({
        'mandatory': {
          'OfferToReceiveAudio': true,
          'OfferToReceiveVideo': callType != CallType.audio,
        },
        'optional': [],
      });

      await _peerConnection!.setLocalDescription(offer);

      // 发送Offer信令
      _sendSignalingMessage(SignalingType.offer, {
        'sdp': offer.sdp,
        'type': offer.type,
      });

      notifyListeners();
    } catch (e) {
      _handleError('发起通话失败: $e');
    }
  }

  /// 接听通话
  Future<void> answerCall(WebRTCSession session) async {
    try {
      _currentSession = session;
      _setCallState(CallState.connecting);

      // 获取本地媒体流
      await _getLocalStream();

      // 创建PeerConnection
      await _createPeerConnection();

      notifyListeners();
    } catch (e) {
      _handleError('接听通话失败: $e');
    }
  }

  /// 拒绝通话
  Future<void> rejectCall() async {
    if (_currentSession != null) {
      _sendSignalingMessage(SignalingType.leave, {
        'reason': 'rejected',
      });
      await _endCall();
    }
  }

  /// 结束通话
  Future<void> endCall() async {
    _sendSignalingMessage(SignalingType.leave, {
      'reason': 'ended',
    });
    await _endCall();
  }

  /// 切换摄像头
  Future<void> switchCamera() async {
    if (_localStream != null) {
      final videoTrack = _localStream!
          .getVideoTracks()
          .firstWhere((track) => track.kind == 'video');
      await Helper.switchCamera(videoTrack);
    }
  }

  /// 切换麦克风
  Future<void> toggleMute() async {
    if (_localStream != null) {
      final audioTrack = _localStream!.getAudioTracks().first;
      final enabled = audioTrack.enabled;
      audioTrack.enabled = !enabled;
      
      _sendSignalingMessage(enabled ? SignalingType.mute : SignalingType.unmute, {});
      notifyListeners();
    }
  }

  /// 切换视频
  Future<void> toggleVideo() async {
    if (_localStream != null) {
      final videoTrack = _localStream!.getVideoTracks().first;
      videoTrack.enabled = !videoTrack.enabled;
      notifyListeners();
    }
  }

  /// 开始屏幕共享
  Future<void> startScreenShare() async {
    try {
      final screenStream = await navigator.mediaDevices.getDisplayMedia({
        'video': true,
        'audio': true,
      });

      // 替换视频轨道
      final screenTrack = screenStream.getVideoTracks().first;
      final sender = _peerConnection!.getSenders().firstWhere(
        (s) => s.track?.kind == 'video',
      );
      await sender.replaceTrack(screenTrack);

      _sendSignalingMessage(SignalingType.screenShareStart, {});
      notifyListeners();
    } catch (e) {
      _handleError('屏幕共享失败: $e');
    }
  }

  /// 停止屏幕共享
  Future<void> stopScreenShare() async {
    try {
      // 恢复摄像头视频
      final videoTrack = _localStream!.getVideoTracks().first;
      final sender = _peerConnection!.getSenders().firstWhere(
        (s) => s.track?.kind == 'video',
      );
      await sender.replaceTrack(videoTrack);

      _sendSignalingMessage(SignalingType.screenShareStop, {});
      notifyListeners();
    } catch (e) {
      _handleError('停止屏幕共享失败: $e');
    }
  }

  /// 切换视频质量
  Future<void> changeVideoQuality(VideoQuality quality) async {
    _config = WebRTCConfig(
      iceServers: _config.iceServers,
      enableAudio: _config.enableAudio,
      enableVideo: _config.enableVideo,
      videoQuality: quality,
      enableBeautyFilter: _config.enableBeautyFilter,
      beautyFilter: _config.beautyFilter,
    );

    // 应用新的编码参数
    final resolution = _config.videoResolution;
    final params = {
      'mandatory': {
        'minWidth': resolution['width'],
        'minHeight': resolution['height'],
        'maxWidth': resolution['width'],
        'maxHeight': resolution['height'],
      },
      'optional': [],
    };

    // 重新获取媒体流
    await _getLocalStream();
    
    notifyListeners();
  }

  /// 获取本地媒体流
  Future<void> _getLocalStream() async {
    final resolution = _config.videoResolution;
    
    final Map<String, dynamic> mediaConstraints = {
      'audio': _config.enableAudio ? {
        'echoCancellation': _config.enableEchoCancellation,
        'noiseSuppression': _config.enableNoiseSuppression,
        'autoGainControl': _config.enableAutoGainControl,
      } : false,
      'video': _config.enableVideo ? {
        'width': resolution['width'],
        'height': resolution['height'],
        'facingMode': 'user',
      } : false,
    };

    _localStream = await navigator.mediaDevices.getUserMedia(mediaConstraints);
    localRenderer.srcObject = _localStream;

    notifyListeners();
  }

  /// 创建PeerConnection
  Future<void> _createPeerConnection() async {
    final Map<String, dynamic> configuration = {
      'iceServers': _config.iceServers.map((s) => s.toJson()).toList(),
      'iceTransportPolicy': 'all',
      'bundlePolicy': 'max-bundle',
      'rtcpMuxPolicy': 'require',
    };

    _peerConnection = await createPeerConnection(configuration);

    // 添加本地流
    _localStream?.getTracks().forEach((track) {
      _peerConnection!.addTrack(track, _localStream!);
    });

    // ICE候选事件
    _peerConnection!.onIceCandidate = (RTCIceCandidate candidate) {
      _sendSignalingMessage(SignalingType.iceCandidate, {
        'candidate': candidate.candidate,
        'sdpMid': candidate.sdpMid,
        'sdpMLineIndex': candidate.sdpMLineIndex,
      });
    };

    // 远程流事件
    _peerConnection!.onTrack = (RTCTrackEvent event) {
      if (event.streams.isNotEmpty) {
        _remoteStream = event.streams[0];
        remoteRenderer.srcObject = _remoteStream;
        notifyListeners();
      }
    };

    // 连接状态变化
    _peerConnection!.onConnectionState = (RTCPeerConnectionState state) {
      _handleConnectionStateChange(state);
    };

    // ICE连接状态
    _peerConnection!.onIceConnectionState = (RTCIceConnectionState state) {
      debugPrint('ICE连接状态: $state');
    };
  }

  /// 处理连接状态变化
  void _handleConnectionStateChange(RTCPeerConnectionState state) {
    debugPrint('PeerConnection状态: $state');
    
    switch (state) {
      case RTCPeerConnectionState.RTCPeerConnectionStateConnected:
        _setCallState(CallState.connected);
        _currentSession?.startTime = DateTime.now();
        _startStatsTimer();
        _startCallDurationTimer();
        onCallConnected?.call(_currentSession!.sessionId);
        break;
      case RTCPeerConnectionState.RTCPeerConnectionStateDisconnected:
        _setCallState(CallState.reconnecting);
        _startReconnectTimer();
        break;
      case RTCPeerConnectionState.RTCPeerConnectionStateFailed:
        _handleError('连接失败');
        break;
      case RTCPeerConnectionState.RTCPeerConnectionStateClosed:
        _endCall();
        break;
      default:
        break;
    }
    notifyListeners();
  }

  /// 处理Offer信令
  Future<void> handleOffer(Map<String, dynamic> offer) async {
    try {
      await _peerConnection!.setRemoteDescription(
        RTCSessionDescription(offer['sdp'], offer['type']),
      );

      // 创建Answer
      final answer = await _peerConnection!.createAnswer({
        'mandatory': {
          'OfferToReceiveAudio': true,
          'OfferToReceiveVideo': _currentSession?.callType != CallType.audio,
        },
        'optional': [],
      });

      await _peerConnection!.setLocalDescription(answer);

      // 发送Answer
      _sendSignalingMessage(SignalingType.answer, {
        'sdp': answer.sdp,
        'type': answer.type,
      });
    } catch (e) {
      _handleError('处理Offer失败: $e');
    }
  }

  /// 处理Answer信令
  Future<void> handleAnswer(Map<String, dynamic> answer) async {
    try {
      await _peerConnection!.setRemoteDescription(
        RTCSessionDescription(answer['sdp'], answer['type']),
      );
    } catch (e) {
      _handleError('处理Answer失败: $e');
    }
  }

  /// 处理ICE候选
  Future<void> handleIceCandidate(Map<String, dynamic> candidate) async {
    try {
      await _peerConnection!.addCandidate(
        RTCIceCandidate(
          candidate['candidate'],
          candidate['sdpMid'],
          candidate['sdpMLineIndex'],
        ),
      );
    } catch (e) {
      debugPrint('添加ICE候选失败: $e');
    }
  }

  /// 发送信令消息
  void _sendSignalingMessage(SignalingType type, dynamic data) {
    final message = SignalingMessage(
      type: type.name,
      sessionId: _currentSession?.sessionId ?? '',
      fromUserId: 'current_user_id',
      toUserId: _currentSession?.calleeId ?? '',
      data: data,
      timestamp: DateTime.now(),
    );
    
    // TODO: 通过WebSocket发送
    debugPrint('发送信令: ${message.toJson()}');
  }

  /// 开始统计定时器
  void _startStatsTimer() {
    _statsTimer?.cancel();
    _statsTimer = Timer.periodic(const Duration(seconds: 2), (timer) {
      _collectStats();
    });
  }

  /// 收集统计信息
  Future<void> _collectStats() async {
    if (_peerConnection == null) return;

    try {
      final stats = await _peerConnection!.getStats();
      
      int rtt = 0;
      double packetLoss = 0;
      int bitrate = 0;
      int jitter = 0;
      int bytesSent = 0;
      int bytesReceived = 0;
      int audioLevel = 0;
      int frameRate = 0;

      for (var report in stats) {
        if (report.type == 'candidate-pair' && report.selected == true) {
          rtt = (report.currentRoundTripTime * 1000).toInt();
        }
        if (report.type == 'inbound-rtp') {
          packetLoss = report.packetsLost / (report.packetsReceived + report.packetsLost) * 100;
          bytesReceived = report.bytesReceived ?? 0;
          jitter = (report.jitter * 1000).toInt();
          frameRate = report.framesPerSecond ?? 0;
        }
        if (report.type == 'outbound-rtp') {
          bytesSent = report.bytesSent ?? 0;
        }
        if (report.type == 'track') {
          audioLevel = ((report.audioLevel ?? 0) * 100).toInt();
        }
      }

      _networkStats = NetworkStats(
        rtt: rtt,
        packetLossRate: packetLoss,
        bitrate: bitrate,
        jitter: jitter,
        bytesSent: bytesSent,
        bytesReceived: bytesReceived,
        audioLevel: audioLevel,
        frameRate: frameRate,
        timestamp: DateTime.now(),
      );

      _statsController.add(_networkStats!);

      // 弱网适配
      _handleWeakNetwork(_networkStats!);

      notifyListeners();
    } catch (e) {
      debugPrint('收集统计信息失败: $e');
    }
  }

  /// 处理弱网情况
  void _handleWeakNetwork(NetworkStats stats) {
    if (!_weakNetworkConfig.enableAutoAdaptation) return;

    final quality = stats.quality;
    onNetworkQualityChanged?.call(quality);

    switch (_weakNetworkConfig.strategy) {
      case WeakNetworkStrategy.auto:
        if (stats.rtt > _weakNetworkConfig.audioOnlyRtt) {
          // 切换到纯音频
          _disableVideo();
        } else if (stats.rtt > _weakNetworkConfig.disableVideoRtt) {
          // 关闭视频
          _disableVideo();
        } else if (stats.rtt > _weakNetworkConfig.reduceQualityRtt) {
          // 降低视频质量
          changeVideoQuality(VideoQuality.low);
        }
        break;
      case WeakNetworkStrategy.reduceQuality:
        if (quality.index >= NetworkQuality.poor.index) {
          changeVideoQuality(VideoQuality.low);
        }
        break;
      case WeakNetworkStrategy.disableVideo:
        if (quality.index >= NetworkQuality.poor.index) {
          _disableVideo();
        }
        break;
      case WeakNetworkStrategy.audioOnly:
        if (quality.index >= NetworkQuality.fair.index) {
          _disableVideo();
        }
        break;
    }
  }

  /// 关闭视频（弱网适配）
  void _disableVideo() {
    if (_localStream != null) {
      final videoTrack = _localStream!.getVideoTracks().first;
      videoTrack.enabled = false;
      notifyListeners();
    }
  }

  /// 开始通话时长定时器
  void _startCallDurationTimer() {
    _callDurationTimer?.cancel();
    _callDurationTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      notifyListeners();
    });
  }

  /// 开始重连定时器
  void _startReconnectTimer() {
    if (_reconnectAttempts >= maxReconnectAttempts) {
      _handleError('重连次数超过上限');
      return;
    }

    _reconnectTimer?.cancel();
    _reconnectAttempts++;
    
    final delay = Duration(seconds: pow(2, _reconnectAttempts).toInt());
    debugPrint('${_reconnectAttempts}秒后尝试重连...');
    
    _reconnectTimer = Timer(delay, () {
      _reconnect();
    });
  }

  /// 重连
  Future<void> _reconnect() async {
    try {
      _setCallState(CallState.reconnecting);
      
      // 关闭旧连接
      await _peerConnection?.close();
      
      // 重新创建连接
      await _createPeerConnection();
      
      // 如果是呼叫方，重新发送Offer
      if (_currentSession?.callerId == 'current_user_id') {
        final offer = await _peerConnection!.createOffer({
          'mandatory': {
            'OfferToReceiveAudio': true,
            'OfferToReceiveVideo': _currentSession?.callType != CallType.audio,
          },
          'optional': [],
        });
        await _peerConnection!.setLocalDescription(offer);
        _sendSignalingMessage(SignalingType.offer, {
          'sdp': offer.sdp,
          'type': offer.type,
        });
      }
    } catch (e) {
      _handleError('重连失败: $e');
    }
  }

  /// 设置通话状态
  void _setCallState(CallState state) {
    _callState = state;
    _currentSession?.state = state;
    notifyListeners();
  }

  /// 处理错误
  void _handleError(String error) {
    debugPrint('WebRTC错误: $error');
    _setCallState(CallState.failed);
    onError?.call(error);
  }

  /// 结束通话
  Future<void> _endCall() async {
    _setCallState(CallState.ended);
    
    if (_currentSession != null) {
      _currentSession!.endTime = DateTime.now();
      
      // 保存通话记录
      await _saveCallHistory();
      
      onCallEnded?.call(_currentSession!.sessionId);
    }

    // 清理资源
    await _cleanup();
    
    notifyListeners();
  }

  /// 保存通话记录
  Future<void> _saveCallHistory() async {
    if (_currentSession == null) return;

    final history = CallHistory(
      id: _generateSessionId(),
      sessionId: _currentSession!.sessionId,
      peerId: _currentSession!.calleeId,
      peerName: _currentSession!.calleeName,
      peerAvatar: _currentSession!.calleeAvatar,
      callType: _currentSession!.callType,
      isOutgoing: _currentSession!.callerId == 'current_user_id',
      isConnected: _currentSession!.startTime != null,
      startTime: _currentSession!.createdAt,
      endTime: _currentSession!.endTime,
      duration: _currentSession!.durationInSeconds,
    );

    // TODO: 保存到本地数据库
    debugPrint('保存通话记录: ${history.toJson()}');
  }

  /// 清理资源
  Future<void> _cleanup() async {
    _statsTimer?.cancel();
    _reconnectTimer?.cancel();
    _callDurationTimer?.cancel();

    await _peerConnection?.close();
    _peerConnection = null;

    _localStream?.getTracks().forEach((track) => track.stop());
    _localStream = null;

    _remoteStream = null;
    localRenderer.srcObject = null;
    remoteRenderer.srcObject = null;

    _participants.clear();
    _networkStats = null;
    _currentSession = null;
    _reconnectAttempts = 0;
  }

  /// 生成会话ID
  String _generateSessionId() {
    return '${DateTime.now().millisecondsSinceEpoch}_${Random().nextInt(10000)}';
  }

  /// 释放资源
  @override
  void dispose() {
    _cleanup();
    localRenderer.dispose();
    remoteRenderer.dispose();
    _signalingController.close();
    _statsController.close();
    super.dispose();
  }

  /// 检查是否有正在进行的通话
  bool get hasActiveCall => 
      _callState == CallState.calling || 
      _callState == CallState.ringing || 
      _callState == CallState.connecting || 
      _callState == CallState.connected ||
      _callState == CallState.reconnecting;

  /// 获取当前通话时长
  String get currentCallDuration {
    if (_currentSession?.startTime == null) return '00:00';
    return _currentSession!.formattedDuration;
  }

  /// 获取麦克风状态
  bool get isMicEnabled {
    if (_localStream == null) return true;
    return _localStream!.getAudioTracks().first.enabled;
  }

  /// 获取摄像头状态
  bool get isCameraEnabled {
    if (_localStream == null) return true;
    return _localStream!.getVideoTracks().first.enabled;
  }
}
