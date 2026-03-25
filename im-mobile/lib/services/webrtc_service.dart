import 'dart:async';
import 'dart:convert';
import 'package:flutter/foundation.dart';
import 'package:webapi/webapi.dart' as webapi;

/// ICE服务器配置
const List<Map<String, dynamic>> iceServers = [
  {'urls': 'stun:stun.l.google.com:19302'},
  {'urls': 'stun:stun1.l.google.com:19302'},
];

/// 通话类型
enum CallType { audio, video }

/// 通话状态
enum CallStatus { idle, calling, ringing, connected, ended, failed }

/// 信令消息
class SignalingMessage {
  final String type;
  final Map<String, dynamic> data;

  SignalingMessage(this.type, this.data);
}

/// 通话信息
class CallInfo {
  final String callId;
  final String callerId;
  final String calleeId;
  final CallType callType;
  final CallStatus status;
  final DateTime startTime;
  DateTime? connectTime;

  CallInfo({
    required this.callId,
    required this.callerId,
    required this.calleeId,
    required this.callType,
    required this.status,
    required this.startTime,
    this.connectTime,
  });
}

/// WebRTC服务 - 移动端
class WebRTCService {
  WebRTCPeerConnection? _peerConnection;
  MediaStream? _localStream;
  MediaStream? _remoteStream;
  WebSocket? _ws;
  RTCDataChannel? _dataChannel;

  String _currentUserId = '';
  String _targetUserId = '';
  CallStatus _callStatus = CallStatus.idle;
  CallType _callType = CallType.audio;
  String _callId = '';
  bool _audioEnabled = true;
  bool _videoEnabled = true;

  // 回调函数
  Function(MediaStream)? onLocalStream;
  Function(MediaStream)? onRemoteStream;
  Function(CallStatus)? onCallStatusChanged;
  Function(bool)? onAudioChanged;
  Function(bool)? onVideoChanged;
  Function(CallInfo)? onIncomingCall;
  Function(String)? onError;
  Function()? onCleanup;

  // 心跳
  Timer? _pingTimer;
  int _reconnectAttempts = 0;
  static const int maxReconnectAttempts = 3;

  /// 初始化
  Future<void> init(String userId) async {
    _currentUserId = userId;
    _connectSignaling();
  }

  /// 连接信令服务器
  void _connectSignaling() {
    final wsUrl = 'ws://localhost:8080/ws/signaling?userId=$_currentUserId';
    
    try {
      _ws = WebSocket.connect(wsUrl);
      
      _ws!.onOpen.listen((event) {
        debugPrint('WebRTC Signaling connected');
        _reconnectAttempts = 0;
        _startPing();
      });
      
      _ws!.onMessage.listen((event) {
        final message = jsonDecode(event.data as String);
        _handleSignalingMessage(message);
      });
      
      _ws!.onClose.listen((event) {
        debugPrint('WebRTC Signaling disconnected');
        _stopPing();
        _attemptReconnect();
      });
      
      _ws!.onError.listen((error) {
        debugPrint('WebRTC Signaling error: $error');
      });
    } catch (e) {
      debugPrint('Failed to connect to signaling server: $e');
    }
  }

  /// 尝试重连
  void _attemptReconnect() {
    if (_reconnectAttempts < maxReconnectAttempts) {
      _reconnectAttempts++;
      Future.delayed(Duration(seconds: 2 * _reconnectAttempts), () {
        debugPrint('Reconnecting... attempt $_reconnectAttempts');
        _connectSignaling();
      });
    }
  }

  /// 开始心跳
  void _startPing() {
    _pingTimer = Timer.periodic(const Duration(seconds: 30), (_) {
      _sendSignaling({'type': 'ping'});
    });
  }

  /// 停止心跳
  void _stopPing() {
    _pingTimer?.cancel();
    _pingTimer = null;
  }

  /// 发送信令消息
  void _sendSignaling(Map<String, dynamic> message) {
    if (_ws != null && _ws!.readyState == WebSocket.open) {
      _ws!.add(jsonEncode(message));
    }
  }

  /// 处理信令消息
  void _handleSignalingMessage(Map<String, dynamic> message) {
    final type = message['type'] as String;
    
    switch (type) {
      case 'ice_config':
        debugPrint('Received ICE config');
        break;
        
      case 'offer':
        _handleOffer(message);
        break;
        
      case 'answer':
        _handleAnswer(message);
        break;
        
      case 'ice_candidate':
        _handleIceCandidate(message);
        break;
        
      case 'incoming_call':
        _handleIncomingCall(message);
        break;
        
      case 'call_accepted':
        _handleCallAccepted(message);
        break;
        
      case 'call_rejected':
        _handleCallRejected(message);
        break;
        
      case 'call_ended':
        _handleCallEnded(message);
        break;
        
      case 'audio_toggled':
        onAudioChanged?.call(message['enabled'] as bool);
        break;
        
      case 'video_toggled':
        onVideoChanged?.call(message['enabled'] as bool);
        break;
    }
  }

  /// 创建RTCPeerConnection
  Future<WebRTCPeerConnection> _createPeerConnection() async {
    final config = RTCConfiguration();
    config.iceServers = iceServers;
    
    final pc = await createPeerConnection(config);
    
    // 添加本地媒体轨道
    if (_localStream != null) {
      for (final track in _localStream!.getVideoTracks()) {
        await pc.addTrack(track, _localStream!);
      }
      for (final track in _localStream!.getAudioTracks()) {
        await pc.addTrack(track, _localStream!);
      }
    }
    
    // 收集ICE候选
    pc.onIceCandidate = (candidate) {
      if (candidate != null) {
        _sendSignaling({
          'type': 'ice_candidate',
          'targetUserId': _targetUserId,
          'candidate': candidate.candidate,
          'sdpMid': candidate.sdpMid,
          'sdpMLineIndex': candidate.sdpMLineIndex,
        });
      }
    };
    
    // 接收远程媒体轨道
    pc.onTrack = (event) {
      if (event.streams.isNotEmpty) {
        _remoteStream = event.streams[0];
        onRemoteStream?.call(_remoteStream!);
      }
    };
    
    // 连接状态变化
    pc.onConnectionState = (state) {
      debugPrint('Connection state: $state');
      
      if (state == RTCPeerConnectionState.RTCPeerConnectionStateConnected) {
        _callStatus = CallStatus.connected;
        onCallStatusChanged?.call(_callStatus);
      } else if (state == RTCPeerConnectionState.RTCPeerConnectionStateDisconnected ||
                 state == RTCPeerConnectionState.RTCPeerConnectionStateFailed) {
        _callStatus = CallStatus.ended;
        onCallStatusChanged?.call(_callStatus);
      }
    };
    
    _peerConnection = pc;
    return pc;
  }

  /// 发起通话
  Future<void> makeCall(String targetUserId, {CallType callType = CallType.video}) async {
    if (_callStatus != CallStatus.idle) {
      debugPrint('Already in a call');
      return;
    }
    
    _targetUserId = targetUserId;
    _callType = callType;
    _callId = _generateCallId();
    
    // 获取本地媒体流
    await _acquireLocalMedia(callType);
    
    // 发送呼叫请求
    _callStatus = CallStatus.calling;
    onCallStatusChanged?.call(_callStatus);
    
    _sendSignaling({
      'type': 'call',
      'calleeId': targetUserId,
      'callType': callType == CallType.video ? 'video' : 'audio',
    });
    
    // 创建WebRTC连接并发送offer
    final pc = await _createPeerConnection();
    final offer = await pc.createOffer({});
    await pc.setLocalDescription(offer);
    
    _sendSignaling({
      'type': 'offer',
      'targetUserId': targetUserId,
      'sdp': pc.localDescription?.sdp,
    });
  }

  /// 接收通话
  Future<void> acceptCall(String callId) async {
    _callId = callId;
    
    await _acquireLocalMedia(_callType);
    
    _sendSignaling({
      'type': 'accept_call',
      'callId': callId,
    });
    
    await _createPeerConnection();
  }

  /// 拒绝通话
  void rejectCall(String callId) {
    _sendSignaling({
      'type': 'reject_call',
      'callId': callId,
    });
    
    _cleanup();
  }

  /// 挂断通话
  void hangup() {
    _sendSignaling({
      'type': 'hangup',
      'callId': _callId,
    });
    
    _cleanup();
  }

  /// 获取本地媒体流
  Future<MediaStream> _acquireLocalMedia(CallType callType) async {
    final constraints = {
      'audio': true,
      'video': callType == CallType.video,
    };
    
    try {
      _localStream = await navigator.mediaDevices.getUserMedia(constraints);
      onLocalStream?.call(_localStream!);
      return _localStream!;
    } catch (e) {
      debugPrint('Failed to get local media: $e');
      rethrow;
    }
  }

  /// 处理offer
  Future<void> _handleOffer(Map<String, dynamic> message) async {
    _targetUserId = message['fromUserId'] as String;
    
    final pc = await _createPeerConnection();
    await pc.setRemoteDescription(
      RTCSessionDescription(message['sdp'] as String, 'offer'),
    );
    
    final answer = await pc.createAnswer({});
    await pc.setLocalDescription(answer);
    
    _sendSignaling({
      'type': 'answer',
      'targetUserId': message['fromUserId'],
      'sdp': pc.localDescription?.sdp,
    });
  }

  /// 处理answer
  Future<void> _handleAnswer(Map<String, dynamic> message) async {
    if (_peerConnection != null) {
      await _peerConnection!.setRemoteDescription(
        RTCSessionDescription(message['sdp'] as String, 'answer'),
      );
    }
  }

  /// 处理ICE候选
  Future<void> _handleIceCandidate(Map<String, dynamic> message) async {
    if (_peerConnection != null) {
      await _peerConnection!.addCandidate(
        RTCIceCandidate(
          message['candidate'] as String,
          message['sdpMid'] as String?,
          message['sdpMLineIndex'] as int?,
        ),
      );
    }
  }

  /// 处理来电
  void _handleIncomingCall(Map<String, dynamic> message) {
    _callId = message['callId'] as String;
    _callType = message['callType'] == 'video' ? CallType.video : CallType.audio;
    _targetUserId = message['callerId'] as String;
    _callStatus = CallStatus.ringing;
    onCallStatusChanged?.call(_callStatus);
    
    onIncomingCall?.call(CallInfo(
      callId: _callId,
      callerId: _targetUserId,
      calleeId: _currentUserId,
      callType: _callType,
      status: _callStatus,
      startTime: DateTime.now(),
    ));
  }

  /// 处理通话被接受
  void _handleCallAccepted(Map<String, dynamic> message) {
    _callStatus = CallStatus.connected;
    onCallStatusChanged?.call(_callStatus);
  }

  /// 处理通话被拒绝
  void _handleCallRejected(Map<String, dynamic> message) {
    _callStatus = CallStatus.ended;
    _cleanup();
    onError?.call('Call rejected');
  }

  /// 处理通话结束
  void _handleCallEnded(Map<String, dynamic> message) {
    _cleanup();
    onCleanup?.call();
  }

  /// 切换音频
  void toggleAudio() {
    if (_localStream != null) {
      final audioTrack = _localStream!.getAudioTracks().firstOrNull;
      if (audioTrack != null) {
        _audioEnabled = !_audioEnabled;
        audioTrack.enabled = _audioEnabled;
        
        _sendSignaling({
          'type': 'toggle_audio',
          'enabled': _audioEnabled,
        });
        
        onAudioChanged?.call(_audioEnabled);
      }
    }
  }

  /// 切换视频
  void toggleVideo() {
    if (_localStream != null) {
      final videoTrack = _localStream!.getVideoTracks().firstOrNull;
      if (videoTrack != null) {
        _videoEnabled = !_videoEnabled;
        videoTrack.enabled = _videoEnabled;
        
        _sendSignaling({
          'type': 'toggle_video',
          'enabled': _videoEnabled,
        });
        
        onVideoChanged?.call(_videoEnabled);
      }
    }
  }

  /// 切换摄像头
  Future<void> switchCamera() async {
    if (_localStream == null || _callType != CallType.video) return;
    
    final videoTrack = _localStream!.getVideoTracks().firstOrNull;
    if (videoTrack == null) return;
    
    // 切换摄像头
    await Helper.switchCamera(track: videoTrack);
  }

  /// 获取本地媒体流
  MediaStream? getLocalStream() => _localStream;

  /// 获取远程媒体流
  MediaStream? getRemoteStream() => _remoteStream;

  /// 获取通话状态
  CallStatus getCallStatus() => _callStatus;

  /// 是否启用音频
  bool isAudioEnabled() => _audioEnabled;

  /// 是否启用视频
  bool isVideoEnabled() => _videoEnabled;

  /// 生成通话ID
  String _generateCallId() => 'call_${DateTime.now().millisecondsSinceEpoch}';

  /// 清理资源
  void _cleanup() {
    _localStream?.getTracks().forEach((track) => track.stop());
    _localStream = null;
    
    _peerConnection?.close();
    _peerConnection = null;
    
    _remoteStream = null;
    _callStatus = CallStatus.idle;
    _callId = '';
    
    onCleanup?.call();
  }

  /// 销毁服务
  void destroy() {
    hangup();
    _stopPing();
    
    _ws?.close();
    _ws = null;
  }
}
