import 'package:flutter/material.dart';
import '../services/webrtc_service.dart';

/**
 * WebRTC通话UI组件
 */

/// 通话界面
class CallScreen extends StatefulWidget {
  final String targetUserId;
  final CallType callType;
  final WebRTCService service;

  const CallScreen({
    Key? key,
    required this.targetUserId,
    required this.callType,
    required this.service,
  }) : super(key: key);

  @override
  State<CallScreen> createState() => _CallScreenState();
}

class _CallScreenState extends State<CallScreen> {
  MediaStream? _localStream;
  MediaStream? _remoteStream;
  bool _audioEnabled = true;
  bool _videoEnabled = true;
  CallStatus _status = CallStatus.idle;
  String _duration = '00:00';
  DateTime? _startTime;
  Timer? _durationTimer;

  @override
  void initState() {
    super.initState();
    _initService();
  }

  void _initService() {
    widget.service.onLocalStream = (stream) {
      setState(() => _localStream = stream);
    };

    widget.service.onRemoteStream = (stream) {
      setState(() => _remoteStream = stream);
    };

    widget.service.onCallStatusChanged = (status) {
      setState(() {
        _status = status;
        if (status == CallStatus.connected) {
          _startDurationTimer();
        }
      });
    };

    widget.service.onAudioChanged = (enabled) {
      setState(() => _audioEnabled = enabled);
    };

    widget.service.onVideoChanged = (enabled) {
      setState(() => _videoEnabled = enabled);
    };

    widget.service.onCleanup = () {
      Navigator.of(context).pop();
    };

    // 发起通话
    widget.service.makeCall(
      widget.targetUserId,
      callType: widget.callType,
    );
  }

  void _startDurationTimer() {
    _startTime = DateTime.now();
    _durationTimer = Timer.periodic(const Duration(seconds: 1), (_) {
      if (_startTime != null) {
        final diff = DateTime.now().difference(_startTime!);
        setState(() {
          _duration = '${diff.inMinutes.toString().padLeft(2, '0')}:${diff.inSeconds.toString().padLeft(2, '0')}';
        });
      }
    });
  }

  @override
  void dispose() {
    _durationTimer?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFF1A1A2E),
      body: SafeArea(
        child: Column(
          children: [
            _buildHeader(),
            Expanded(child: _buildVideoArea()),
            _buildControls(),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text(
                widget.targetUserId,
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 18,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 4),
              Text(
                _status == CallStatus.connected ? _duration : _getStatusText(),
                style: TextStyle(
                  color: _status == CallStatus.connected 
                    ? const Color(0xFF4ADE80) 
                    : Colors.grey,
                  fontSize: 14,
                ),
              ),
            ],
          ),
          _buildStatusBadge(),
        ],
      ),
    );
  }

  Widget _buildStatusBadge() {
    String text;
    Color color;
    
    switch (_status) {
      case CallStatus.calling:
        text = '呼叫中';
        color = const Color(0xFFFBBF24);
        break;
      case CallStatus.ringing:
        text = '响铃中';
        color = const Color(0xFF60A5FA);
        break;
      case CallStatus.connected:
        text = '已接通';
        color = const Color(0xFF4ADE80);
        break;
      default:
        return const SizedBox.shrink();
    }

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 4),
      decoration: BoxDecoration(
        color: color.withOpacity(0.2),
        borderRadius: BorderRadius.circular(12),
      ),
      child: Text(
        text,
        style: TextStyle(color: color, fontSize: 12),
      ),
    );
  }

  String _getStatusText() {
    switch (_status) {
      case CallStatus.calling:
        return '正在呼叫...';
      case CallStatus.ringing:
        return '等待接听...';
      case CallStatus.ended:
        return '通话结束';
      case CallStatus.failed:
        return '通话失败';
      default:
        return '';
    }
  }

  Widget _buildVideoArea() {
    if (widget.callType == CallType.audio) {
      return _buildAudioPlaceholder();
    }

    return Stack(
      children: [
        // 远程视频
        Center(
          child: _remoteStream != null
            ? RTCVideoView(_remoteStream!)
            : _buildWaitingView(),
        ),
        
        // 本地视频预览
        Positioned(
          bottom: 16,
          right: 16,
          child: Container(
            width: 120,
            height: 160,
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(12),
              border: Border.all(color: Colors.white24),
            ),
            clipBehavior: Clip.antiAlias,
            child: _localStream != null && _videoEnabled
              ? RTCVideoView(_localStream!, mirror: true)
              : Container(
                  color: Colors.grey[800],
                  child: const Icon(Icons.videocam_off, color: Colors.white54),
                ),
          ),
        ),
      ],
    );
  }

  Widget _buildAudioPlaceholder() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Container(
            width: 120,
            height: 120,
            decoration: BoxDecoration(
              shape: BoxShape.circle,
              gradient: const LinearGradient(
                colors: [Color(0xFF667EEA), Color(0xFF764BA2)],
              ),
              boxShadow: [
                BoxShadow(
                  color: const Color(0xFF667EEA).withOpacity(0.4),
                  blurRadius: 32,
                  spreadRadius: 8,
                ),
              ],
            ),
            child: const Icon(
              Icons.person,
              size: 64,
              color: Colors.white,
            ),
          ),
          const SizedBox(height: 24),
          Text(
            widget.targetUserId,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 24,
              fontWeight: FontWeight.bold,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            _status == CallStatus.connected ? _duration : _getStatusText(),
            style: const TextStyle(color: Colors.grey, fontSize: 16),
          ),
        ],
      ),
    );
  }

  Widget _buildWaitingView() {
    return const Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          CircularProgressIndicator(color: Colors.white54),
          SizedBox(height: 16),
          Text(
            '等待对方加入...',
            style: TextStyle(color: Colors.grey, fontSize: 16),
          ),
        ],
      ),
    );
  }

  Widget _buildControls() {
    return Container(
      padding: const EdgeInsets.all(24),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          // 静音
          _buildControlButton(
            icon: _audioEnabled ? Icons.mic : Icons.mic_off,
            color: _audioEnabled ? Colors.white : Colors.red,
            onTap: () => widget.service.toggleAudio(),
          ),
          
          // 挂断
          _buildControlButton(
            icon: Icons.call_end,
            color: Colors.red,
            isPrimary: true,
            onTap: () => widget.service.hangup(),
          ),
          
          // 摄像头
          if (widget.callType == CallType.video)
            _buildControlButton(
              icon: _videoEnabled ? Icons.videocam : Icons.videocam_off,
              color: _videoEnabled ? Colors.white : Colors.red,
              onTap: () => widget.service.toggleVideo(),
            )
          else
            const SizedBox(width: 60),
        ],
      ),
    );
  }

  Widget _buildControlButton({
    required IconData icon,
    required Color color,
    bool isPrimary = false,
    required VoidCallback onTap,
  }) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        width: isPrimary ? 70 : 60,
        height: isPrimary ? 70 : 60,
        decoration: BoxDecoration(
          shape: BoxShape.circle,
          color: isPrimary ? color : color.withOpacity(0.2),
          boxShadow: isPrimary
            ? [
                BoxShadow(
                  color: color.withOpacity(0.4),
                  blurRadius: 16,
                  spreadRadius: 4,
                ),
              ]
            : null,
        ),
        child: Icon(
          icon,
          color: isPrimary ? Colors.white : color,
          size: isPrimary ? 32 : 24,
        ),
      ),
    );
  }
}

/// 来电弹窗
class IncomingCallDialog extends StatelessWidget {
  final CallInfo callInfo;
  final VoidCallback onAccept;
  final VoidCallback onReject;

  const IncomingCallDialog({
    Key? key,
    required this.callInfo,
    required this.onAccept,
    required this.onReject,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Dialog(
      backgroundColor: const Color(0xFF1A1A2E),
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(16)),
      child: Padding(
        padding: const EdgeInsets.all(24),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            // 头像
            Container(
              width: 80,
              height: 80,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                gradient: const LinearGradient(
                  colors: [Color(0xFF667EEA), Color(0xFF764BA2)],
                ),
              ),
              child: const Icon(Icons.person, size: 40, color: Colors.white),
            ),
            const SizedBox(height: 16),
            
            // 呼叫者
            Text(
              callInfo.callerId,
              style: const TextStyle(
                color: Colors.white,
                fontSize: 20,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 8),
            
            // 通话类型
            Text(
              callInfo.callType == CallType.video ? '视频通话' : '语音通话',
              style: const TextStyle(color: Colors.grey, fontSize: 14),
            ),
            const SizedBox(height: 24),
            
            // 按钮
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceEvenly,
              children: [
                // 拒绝
                GestureDetector(
                  onTap: onReject,
                  child: Container(
                    width: 60,
                    height: 60,
                    decoration: const BoxDecoration(
                      shape: BoxShape.circle,
                      color: Colors.red,
                    ),
                    child: const Icon(Icons.call_end, color: Colors.white, size: 28),
                  ),
                ),
                
                // 接听
                GestureDetector(
                  onTap: onAccept,
                  child: Container(
                    width: 60,
                    height: 60,
                    decoration: const BoxDecoration(
                      shape: BoxShape.circle,
                      color: Color(0xFF22C55E),
                    ),
                    child: const Icon(Icons.call, color: Colors.white, size: 28),
                  ),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
}
