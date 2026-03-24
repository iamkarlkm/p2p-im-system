import 'dart:async';
import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:flutter_webrtc/flutter_webrtc.dart';
import '../models/webrtc_signal.dart';

typedef SignalCallback = void Function(SignalResponse response);
typedef StatusCallback = void Function(CallStatus status);
typedef StreamCallback = void Function(MediaStream stream);

class WebRTCSignalService {
  RTCPeerConnection? _peerConnection;
  MediaStream? _localStream;
  MediaStream? _remoteStream;
  SignalingSocket? _socket;

  String? _roomId;
  int _userId = 0;
  CallStatus _status = CallStatus.initiating;

  final List<IceServer> _iceServers = [
    IceServer(urls: 'stun:stun.l.google.com:19302'),
    IceServer(urls: 'stun:stun1.l.google.com:19302'),
  ];

  final _incomingCallController = StreamController<SignalResponse>.broadcast();
  final _statusChangeController = StreamController<CallStatus>.broadcast();
  final _remoteStreamController = StreamController<MediaStream>.broadcast();

  Stream<SignalResponse> get onIncomingCall => _incomingCallController.stream;
  Stream<CallStatus> get onStatusChange => _statusChangeController.stream;
  Stream<MediaStream> get onRemoteStream => _remoteStreamController.stream;

  Future<void> initialize(int userId, String token) async {
    _userId = userId;
    _socket = SignalingSocket(token);
    _socket!.onSignal = _handleSignal;
    await _socket!.connect();
  }

  void _handleSignal(SignalResponse response) {
    switch (response.signalType) {
      case 'call_invite':
        _incomingCallController.add(response);
        break;
      case 'offer':
        _handleOffer(response);
        break;
      case 'answer':
        _handleAnswer(response);
        break;
      case 'ice_candidate':
        _handleIceCandidate(response);
        break;
      case 'ringing':
        _updateStatus(CallStatus.ringing);
        break;
      case 'call_accepted':
        _updateStatus(CallStatus.accepted);
        break;
      case 'call_rejected':
        _updateStatus(CallStatus.rejected);
        _cleanup();
        break;
      case 'call_cancelled':
        _updateStatus(CallStatus.cancelled);
        _cleanup();
        break;
      case 'call_ended':
        _updateStatus(CallStatus.ended);
        _cleanup();
        break;
      case 'busy':
        _updateStatus(CallStatus.busy);
        _cleanup();
        break;
      case 'no_answer':
        _updateStatus(CallStatus.noAnswer);
        _cleanup();
        break;
    }
  }

  Future<String> initiateCall(int targetUserId, String callType) async {
    _roomId = DateTime.now().millisecondsSinceEpoch.toString();
    _status = CallStatus.initiating;

    await _createPeerConnection();

    _localStream = await navigator.mediaDevices.getUserMedia({
      'audio': true,
      'video': callType == 'VIDEO',
    });

    _localStream!.getTracks().forEach((track) {
      _peerConnection!.addTrack(track, _localStream!);
    });

    final offer = await _peerConnection!.createOffer();
    await _peerConnection!.setLocalDescription(offer);

    _sendSignal(SignalRequest(
      roomId: _roomId!,
      userId: _userId,
      signalType: SignalType.offer.value,
      sdp: offer.sdp,
      sdpType: offer.type,
      targetUserId: targetUserId,
      callType: callType,
    ));

    return _roomId!;
  }

  Future<void> _handleOffer(SignalResponse response) async {
    _roomId = response.roomId;
    await _createPeerConnection();

    await _peerConnection!.setRemoteDescription(
      RTCSessionDescription(response.sdp, response.sdpType),
    );

    _localStream = await navigator.mediaDevices.getUserMedia({
      'audio': true,
      'video': response.callType == 'VIDEO',
    });

    _localStream!.getTracks().forEach((track) {
      _peerConnection!.addTrack(track, _localStream!);
    });

    final answer = await _peerConnection!.createAnswer();
    await _peerConnection!.setLocalDescription(answer);

    _sendSignal(SignalRequest(
      roomId: _roomId!,
      userId: _userId,
      signalType: SignalType.answer.value,
      sdp: answer.sdp,
      sdpType: answer.type,
    ));
  }

  Future<void> _handleAnswer(SignalResponse response) async {
    if (_peerConnection != null) {
      await _peerConnection!.setRemoteDescription(
        RTCSessionDescription(response.sdp, response.sdpType),
      );
      _updateStatus(CallStatus.connecting);
    }
  }

  Future<void> _handleIceCandidate(SignalResponse response) async {
    if (_peerConnection != null && response.candidate != null) {
      await _peerConnection!.addCandidate(RTCIceCandidate(
        response.candidate,
        response.sdpMid,
        response.sdpMLineIndex ?? 0,
      ));
    }
  }

  Future<void> _createPeerConnection() async {
    final config = RTCConfiguration();
    config.iceServers = _iceServers
        .map((s) => {'urls': s.urls})
        .toList();

    _peerConnection = await createPeerConnection(config);

    _peerConnection!.onIceCandidate = (candidate) {
      if (candidate != null && _roomId != null) {
        _sendSignal(SignalRequest(
          roomId: _roomId!,
          userId: _userId,
          signalType: SignalType.iceCandidate.value,
          candidate: candidate.candidate,
          sdpMLineIndex: candidate.sdpMLineIndex,
          sdpMid: candidate.sdpMid,
        ));
      }
    };

    _peerConnection!.onTrack = (event) {
      if (event.streams.isNotEmpty) {
        _remoteStream = event.streams[0];
        _remoteStreamController.add(_remoteStream!);
      }
    };

    _peerConnection!.onConnectionState = (state) {
      if (state == RTCPeerConnectionState.RTCPeerConnectionStateConnected) {
        _updateStatus(CallStatus.connected);
      } else if (state == RTCPeerConnectionState.RTCPeerConnectionStateFailed ||
          state == RTCPeerConnectionState.RTCPeerConnectionStateDisconnected) {
        _updateStatus(CallStatus.ended);
        _cleanup();
      }
    };
  }

  void _sendSignal(SignalRequest request) {
    _socket?.send(request.toJson());
  }

  Future<void> acceptCall(String roomId) async {
    _roomId = roomId;
    await _createPeerConnection();

    _localStream = await navigator.mediaDevices.getUserMedia({
      'audio': true,
      'video': true,
    });

    _localStream!.getTracks().forEach((track) {
      _peerConnection!.addTrack(track, _localStream!);
    });

    _sendSignal(SignalRequest(
      roomId: roomId,
      userId: _userId,
      signalType: SignalType.answer.value,
    ));
  }

  void rejectCall(String roomId) {
    _sendSignal(SignalRequest(
      roomId: roomId,
      userId: _userId,
      signalType: SignalType.callRejected.value,
    ));
    _cleanup();
  }

  void cancelCall() {
    if (_roomId != null) {
      _sendSignal(SignalRequest(
        roomId: _roomId!,
        userId: _userId,
        signalType: SignalType.callCancelled.value,
      ));
    }
    _cleanup();
  }

  void endCall() {
    if (_roomId != null) {
      _sendSignal(SignalRequest(
        roomId: _roomId!,
        userId: _userId,
        signalType: SignalType.callEnded.value,
      ));
    }
    _cleanup();
  }

  void toggleMute(bool muted) {
    _localStream?.getAudioTracks().forEach((track) {
      track.enabled = !muted;
    });
  }

  void toggleVideo(bool enabled) {
    _localStream?.getVideoTracks().forEach((track) {
      track.enabled = enabled;
    });
  }

  void _updateStatus(CallStatus status) {
    _status = status;
    _statusChangeController.add(status);
  }

  void _cleanup() {
    _localStream?.getTracks().forEach((track) => track.stop());
    _localStream = null;
    _remoteStream?.getTracks().forEach((track) => track.stop());
    _remoteStream = null;
    _peerConnection?.close();
    _peerConnection = null;
    _roomId = null;
  }

  void disconnect() {
    _cleanup();
    _socket?.disconnect();
    _socket = null;
    _incomingCallController.close();
    _statusChangeController.close();
    _remoteStreamController.close();
  }

  MediaStream? get localStream => _localStream;
  MediaStream? get remoteStream => _remoteStream;
  CallStatus get status => _status;
  String? get roomId => _roomId;
}

class SignalingSocket {
  final String token;
  WebSocket? _ws;
  Function(SignalResponse)? onSignal;

  SignalingSocket(this.token);

  Future<void> connect() async {
    // In production, connect to actual WebSocket server
    // _ws = WebSocket('wss://api.example.com/webrtc');
  }

  void send(Map<String, dynamic> data) {
    // _ws?.add(jsonEncode(data));
  }

  void disconnect() {
    _ws?.close();
    _ws = null;
  }
}
