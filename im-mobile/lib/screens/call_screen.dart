import 'package:flutter/material.dart';
import 'package:flutter_webrtc/flutter_webrtc.dart';
import '../services/webrtc_signal_service.dart';
import '../models/webrtc_signal.dart';

class CallScreen extends StatefulWidget {
  final int targetUserId;
  final String callType;
  final String? incomingRoomId;
  final int userId;
  final String token;

  const CallScreen({
    super.key,
    required this.targetUserId,
    required this.callType,
    this.incomingRoomId,
    required this.userId,
    required this.token,
  });

  @override
  State<CallScreen> createState() => _CallScreenState();
}

class _CallScreenState extends State<CallScreen> {
  final WebRTCSignalService _service = WebRTCSignalService();
  MediaStream? _localStream;
  MediaStream? _remoteStream;
  CallStatus _status = CallStatus.initiating;
  bool _isMuted = false;
  bool _isVideoOff = false;
  bool _isIncoming = false;
  SignalResponse? _incomingCall;
  Duration _duration = Duration.zero;

  @override
  void initState() {
    super.initState();
    _isIncoming = widget.incomingRoomId != null;
    _initService();
  }

  Future<void> _initService() async {
    await _service.initialize(widget.userId, widget.token);

    _service.onStatusChange.listen((status) {
      setState(() => _status = status);
      if (status == CallStatus.connected) {
        _startTimer();
      } else if (status == CallStatus.ended ||
          status == CallStatus.rejected ||
          status == CallStatus.cancelled) {
        _stopTimer();
        Future.delayed(const Duration(seconds: 2), () {
          if (mounted) Navigator.of(context).pop();
        });
      }
    });

    _service.onRemoteStream.listen((stream) {
      setState(() => _remoteStream = stream);
    });

    _service.onIncomingCall.listen((call) {
      setState(() {
        _incomingCall = call;
        _isIncoming = true;
      });
    });

    if (!_isIncoming) {
      await _service.initiateCall(widget.targetUserId, widget.callType);
    } else if (widget.incomingRoomId != null) {
      // Accept incoming automatically for demo
    }
  }

  void _startTimer() {
    Future.doWhile(() async {
      await Future.delayed(const Duration(seconds: 1));
      if (mounted && _status == CallStatus.connected) {
        setState(() => _duration += const Duration(seconds: 1));
        return true;
      }
      return false;
    });
  }

  void _stopTimer() {}

  String _formatDuration(Duration d) {
    final m = d.inMinutes.toString().padLeft(2, '0');
    final s = (d.inSeconds % 60).toString().padLeft(2, '0');
    return '$m:$s';
  }

  String _statusText() {
    switch (_status) {
      case CallStatus.initiating:
        return '正在呼叫...';
      case CallStatus.ringing:
        return '响铃中...';
      case CallStatus.connecting:
        return '连接中...';
      case CallStatus.connected:
        return '通话中';
      case CallStatus.rejected:
        return '对方已拒绝';
      case CallStatus.busy:
        return '对方忙线';
      case CallStatus.noAnswer:
        return '无人接听';
      case CallStatus.cancelled:
        return '已取消';
      case CallStatus.ended:
        return '通话结束';
    }
  }

  @override
  void dispose() {
    _service.disconnect();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (_isIncoming && _incomingCall != null) {
      return _buildIncomingView();
    }
    return Scaffold(
      backgroundColor: const Color(0xFF1a1a2e),
      body: SafeArea(
        child: Column(
          children: [
            Expanded(
              child: _buildCallView(),
            ),
            _buildControls(),
          ],
        ),
      ),
    );
  }

  Widget _buildIncomingView() {
    return Scaffold(
      backgroundColor: const Color(0xFF1a1a2e),
      body: Center(
        child: Card(
          color: const Color(0xFF16213e),
          margin: const EdgeInsets.all(32),
          child: Padding(
            padding: const EdgeInsets.all(32),
            child: Column(
              mainAxisSize: MainAxisSize.min,
              children: [
                const Text(
                  '📞 来电',
                  style: TextStyle(color: Colors.white, fontSize: 24),
                ),
                const SizedBox(height: 16),
                Text(
                  '用户 ${_incomingCall?.fromUserId} 发起',
                  style: const TextStyle(color: Colors.white70),
                ),
                const SizedBox(height: 24),
                Row(
                  mainAxisSize: MainAxisSize.min,
                  children: [
                    ElevatedButton(
                      onPressed: () async {
                        await _service.acceptCall(_incomingCall!.roomId);
                        setState(() => _isIncoming = false);
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.green,
                      ),
                      child: const Text('接听'),
                    ),
                    const SizedBox(width: 24),
                    ElevatedButton(
                      onPressed: () {
                        _service.rejectCall(_incomingCall!.roomId);
                        Navigator.of(context).pop();
                      },
                      style: ElevatedButton.styleFrom(
                        backgroundColor: Colors.red,
                      ),
                      child: const Text('拒绝'),
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _buildCallView() {
    final isVideo = widget.callType == 'VIDEO';

    return Column(
      mainAxisAlignment: MainAxisAlignment.center,
      children: [
        if (isVideo && _remoteStream != null)
          Expanded(
            child: ClipRRect(
              borderRadius: BorderRadius.circular(12),
              child: RTCVideoView(_remoteStream!),
            ),
          )
        else if (isVideo)
          const Expanded(
            child: Center(
              child: CircleAvatar(
                radius: 60,
                backgroundColor: Color(0xFF4f46e5),
                child: Icon(Icons.person, size: 60, color: Colors.white),
              ),
            ),
          )
        else
          const Expanded(
            child: Center(
              child: CircleAvatar(
                radius: 60,
                backgroundColor: Color(0xFF4f46e5),
                child: Icon(Icons.person, size: 60, color: Colors.white),
              ),
            ),
          ),
        const SizedBox(height: 24),
        Text(
          '用户 ${_isIncoming ? _incomingCall?.fromUserId : widget.targetUserId}',
          style: const TextStyle(
            color: Colors.white,
            fontSize: 20,
            fontWeight: FontWeight.bold,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          _statusText(),
          style: const TextStyle(color: Colors.white70, fontSize: 16),
        ),
        if (_status == CallStatus.connected)
          Text(
            _formatDuration(_duration),
            style: const TextStyle(color: Color(0xFF4f46e5), fontSize: 18),
          ),
      ],
    );
  }

  Widget _buildControls() {
    final isVideo = widget.callType == 'VIDEO';

    return Padding(
      padding: const EdgeInsets.all(24),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          if (isVideo)
            _controlBtn(
              icon: _isVideoOff ? Icons.videocam_off : Icons.videocam,
              active: _isVideoOff,
              onPressed: () {
                setState(() => _isVideoOff = !_isVideoOff);
                _service.toggleVideo(!_isVideoOff);
              },
            ),
          _controlBtn(
            icon: _isMuted ? Icons.mic_off : Icons.mic,
            active: _isMuted,
            onPressed: () {
              setState(() => _isMuted = !_isMuted);
              _service.toggleMute(_isMuted);
            },
          ),
          _controlBtn(
            icon: Icons.call_end,
            active: true,
            isEnd: true,
            onPressed: () {
              _service.endCall();
              Navigator.of(context).pop();
            },
          ),
        ],
      ),
    );
  }

  Widget _controlBtn({
    required IconData icon,
    required bool active,
    bool isEnd = false,
    required VoidCallback onPressed,
  }) {
    return Container(
      width: 56,
      height: 56,
      decoration: BoxDecoration(
        shape: BoxShape.circle,
        color: isEnd ? Colors.red : (active ? Colors.red.shade400 : const Color(0xFF2a2a4a)),
      ),
      child: IconButton(
        icon: Icon(icon, color: Colors.white),
        onPressed: onPressed,
      ),
    );
  }
}
