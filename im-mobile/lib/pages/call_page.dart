/// WebRTC实时流媒体传输 - 移动端通话页面
/// 功能#212 - WebRTC Mobile Call Page
/// 创建时间: 2026-03-27 01:45

import 'package:flutter/material.dart';
import 'package:flutter_webrtc/flutter_webrtc.dart';
import 'package:provider/provider.dart';
import '../models/webrtc_models.dart';
import '../services/webrtc_service.dart';

/// 通话页面
class CallPage extends StatefulWidget {
  final WebRTCSession? incomingSession;
  final String? calleeId;
  final String? calleeName;
  final String? calleeAvatar;
  final CallType callType;

  const CallPage({
    Key? key,
    this.incomingSession,
    this.calleeId,
    this.calleeName,
    this.calleeAvatar,
    this.callType = CallType.video,
  }) : super(key: key);

  @override
  State<CallPage> createState() => _CallPageState();
}

class _CallPageState extends State<CallPage> with TickerProviderStateMixin {
  late WebRTCService _webRTCService;
  bool _isInitialized = false;

  // 动画控制器
  late AnimationController _pulseController;
  late Animation<double> _pulseAnimation;

  @override
  void initState() {
    super.initState();
    _initializeAnimations();
    _initializeCall();
  }

  void _initializeAnimations() {
    _pulseController = AnimationController(
      duration: const Duration(seconds: 1),
      vsync: this,
    );
    _pulseAnimation = Tween<double>(begin: 1.0, end: 1.2).animate(
      CurvedAnimation(parent: _pulseController, curve: Curves.easeInOut),
    );
    _pulseController.repeat(reverse: true);
  }

  Future<void> _initializeCall() async {
    _webRTCService = WebRTCService();
    await _webRTCService.initialize();

    _webRTCService.onCallConnected = (sessionId) {
      _pulseController.stop();
    };

    _webRTCService.onCallEnded = (sessionId) {
      Navigator.of(context).pop();
    };

    _webRTCService.onError = (error) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(error)),
      );
    };

    _webRTCService.onNetworkQualityChanged = (quality) {
      if (quality.index >= NetworkQuality.poor.index) {
        _showNetworkWarning(quality);
      }
    };

    setState(() => _isInitialized = true);

    // 处理来电或去电
    if (widget.incomingSession != null) {
      // 显示接听界面
    } else if (widget.calleeId != null) {
      await _webRTCService.startCall(
        calleeId: widget.calleeId!,
        calleeName: widget.calleeName ?? '未知用户',
        calleeAvatar: widget.calleeAvatar,
        callType: widget.callType,
      );
    }
  }

  void _showNetworkWarning(NetworkQuality quality) {
    if (!mounted) return;
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Row(
          children: [
            Icon(
              Icons.signal_cellular_connected_no_internet_4_bar,
              color: _getQualityColor(quality),
            ),
            const SizedBox(width: 8),
            Text('网络质量${quality.displayName}，建议调整视频质量'),
          ],
        ),
        backgroundColor: Colors.orange,
        duration: const Duration(seconds: 3),
      ),
    );
  }

  Color _getQualityColor(NetworkQuality quality) {
    switch (quality) {
      case NetworkQuality.excellent:
      case NetworkQuality.good:
        return Colors.green;
      case NetworkQuality.fair:
        return Colors.yellow;
      case NetworkQuality.poor:
        return Colors.orange;
      case NetworkQuality.bad:
        return Colors.red;
    }
  }

  @override
  void dispose() {
    _pulseController.dispose();
    _webRTCService.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (!_isInitialized) {
      return const Scaffold(
        backgroundColor: Colors.black,
        body: Center(
          child: CircularProgressIndicator(color: Colors.white),
        ),
      );
    }

    return ChangeNotifierProvider.value(
      value: _webRTCService,
      child: Consumer<WebRTCService>(
        builder: (context, service, child) {
          return Scaffold(
            backgroundColor: Colors.black,
            body: SafeArea(
              child: Stack(
                children: [
                  // 远程视频（大画面）
                  _buildRemoteVideo(service),
                  
                  // 本地视频（小窗口）
                  _buildLocalVideo(service),
                  
                  // 网络质量指示器
                  _buildNetworkIndicator(service),
                  
                  // 通话时长
                  _buildCallDuration(service),
                  
                  // 顶部信息栏
                  _buildTopBar(service),
                  
                  // 底部控制栏
                  _buildControlBar(service),
                  
                  // 来电接听界面
                  if (widget.incomingSession != null && 
                      service.callState == CallState.idle)
                    _buildIncomingCallOverlay(service),
                ],
              ),
            ),
          );
        },
      ),
    );
  }

  /// 远程视频渲染
  Widget _buildRemoteVideo(WebRTCService service) {
    return Positioned.fill(
      child: Container(
        color: Colors.black,
        child: service.remoteRenderer.srcObject != null
            ? RTCVideoView(
                service.remoteRenderer,
                objectFit: RTCVideoViewObjectFit.RTCVideoViewObjectFitCover,
              )
            : _buildWaitingPlaceholder(service),
      ),
    );
  }

  /// 等待中占位
  Widget _buildWaitingPlaceholder(WebRTCService service) {
    final session = service.currentSession;
    final isOutgoing = session?.callerId == 'current_user_id';
    final peerName = session?.getPeerName('current_user_id') ?? widget.calleeName ?? '未知用户';
    final peerAvatar = session?.getPeerAvatar('current_user_id') ?? widget.calleeAvatar;

    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          // 头像动画
          AnimatedBuilder(
            animation: _pulseAnimation,
            builder: (context, child) {
              return Transform.scale(
                scale: _pulseAnimation.value,
                child: CircleAvatar(
                  radius: 60,
                  backgroundImage: peerAvatar != null 
                      ? NetworkImage(peerAvatar) 
                      : null,
                  backgroundColor: Colors.grey[800],
                  child: peerAvatar == null
                      ? Text(
                          peerName.substring(0, 1).toUpperCase(),
                          style: const TextStyle(
                            fontSize: 48,
                            color: Colors.white,
                          ),
                        )
                      : null,
                ),
              );
            },
          ),
          const SizedBox(height: 24),
          Text(
            peerName,
            style: const TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.bold,
              color: Colors.white,
            ),
          ),
          const SizedBox(height: 12),
          Text(
            isOutgoing ? '正在呼叫...' : '等待接听...',
            style: TextStyle(
              fontSize: 16,
              color: Colors.grey[400],
            ),
          ),
          const SizedBox(height: 8),
          Text(
            service.callState.displayName,
            style: TextStyle(
              fontSize: 14,
              color: Colors.grey[500],
            ),
          ),
        ],
      ),
    );
  }

  /// 本地视频小窗口
  Widget _buildLocalVideo(WebRTCService service) {
    return Positioned(
      right: 16,
      top: 100,
      child: Container(
        width: 120,
        height: 160,
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(12),
          border: Border.all(color: Colors.white24, width: 2),
        ),
        child: ClipRRect(
          borderRadius: BorderRadius.circular(12),
          child: service.localRenderer.srcObject != null
              ? RTCVideoView(
                  service.localRenderer,
                  mirror: true,
                  objectFit: RTCVideoViewObjectFit.RTCVideoViewObjectFitCover,
                )
              : Container(color: Colors.grey[900]),
        ),
      ),
    );
  }

  /// 网络质量指示器
  Widget _buildNetworkIndicator(WebRTCService service) {
    final stats = service.networkStats;
    if (stats == null) return const SizedBox.shrink();

    return Positioned(
      left: 16,
      top: 100,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
        decoration: BoxDecoration(
          color: Colors.black54,
          borderRadius: BorderRadius.circular(20),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Icon(
              _getNetworkIcon(stats.quality),
              color: _getQualityColor(stats.quality),
              size: 16,
            ),
            const SizedBox(width: 6),
            Text(
              '${stats.rtt}ms',
              style: const TextStyle(
                color: Colors.white,
                fontSize: 12,
              ),
            ),
          ],
        ),
      ),
    );
  }

  IconData _getNetworkIcon(NetworkQuality quality) {
    switch (quality) {
      case NetworkQuality.excellent:
      case NetworkQuality.good:
        return Icons.signal_cellular_alt;
      case NetworkQuality.fair:
        return Icons.signal_cellular_alt_2_bar;
      case NetworkQuality.poor:
        return Icons.signal_cellular_alt_1_bar;
      case NetworkQuality.bad:
        return Icons.signal_cellular_off;
    }
  }

  /// 通话时长显示
  Widget _buildCallDuration(WebRTCService service) {
    if (service.callState != CallState.connected &&
        service.callState != CallState.reconnecting) {
      return const SizedBox.shrink();
    }

    return Positioned(
      top: 100,
      left: 0,
      right: 0,
      child: Center(
        child: Container(
          padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
          decoration: BoxDecoration(
            color: Colors.black54,
            borderRadius: BorderRadius.circular(20),
          ),
          child: Text(
            service.currentCallDuration,
            style: const TextStyle(
              color: Colors.white,
              fontSize: 18,
              fontWeight: FontWeight.w500,
              letterSpacing: 2,
            ),
          ),
        ),
      ),
    );
  }

  /// 顶部信息栏
  Widget _buildTopBar(WebRTCService service) {
    return Positioned(
      top: 0,
      left: 0,
      right: 0,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.topCenter,
            end: Alignment.bottomCenter,
            colors: [
              Colors.black.withOpacity(0.7),
              Colors.transparent,
            ],
          ),
        ),
        child: SafeArea(
          child: Row(
            children: [
              IconButton(
                icon: const Icon(Icons.arrow_back, color: Colors.white),
                onPressed: () => _showEndCallConfirm(service),
              ),
              const Spacer(),
              // 更多选项
              IconButton(
                icon: const Icon(Icons.more_vert, color: Colors.white),
                onPressed: () => _showMoreOptions(service),
              ),
            ],
          ),
        ),
      ),
    );
  }

  /// 底部控制栏
  Widget _buildControlBar(WebRTCService service) {
    return Positioned(
      left: 0,
      right: 0,
      bottom: 0,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 32),
        decoration: BoxDecoration(
          gradient: LinearGradient(
            begin: Alignment.bottomCenter,
            end: Alignment.topCenter,
            colors: [
              Colors.black.withOpacity(0.8),
              Colors.transparent,
            ],
          ),
        ),
        child: SafeArea(
          top: false,
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // 功能按钮行
              Row(
                mainAxisAlignment: MainAxisAlignment.spaceEvenly,
                children: [
                  // 切换摄像头
                  _buildControlButton(
                    icon: Icons.flip_camera_ios,
                    label: '切换',
                    onPressed: () => service.switchCamera(),
                    isActive: true,
                  ),
                  // 麦克风
                  _buildControlButton(
                    icon: service.isMicEnabled 
                        ? Icons.mic 
                        : Icons.mic_off,
                    label: service.isMicEnabled ? '静音' : '取消静音',
                    onPressed: () => service.toggleMute(),
                    isActive: service.isMicEnabled,
                    backgroundColor: service.isMicEnabled 
                        ? Colors.white24 
                        : Colors.red,
                  ),
                  // 摄像头
                  _buildControlButton(
                    icon: service.isCameraEnabled 
                        ? Icons.videocam 
                        : Icons.videocam_off,
                    label: service.isCameraEnabled ? '关闭视频' : '开启视频',
                    onPressed: () => service.toggleVideo(),
                    isActive: service.isCameraEnabled,
                    backgroundColor: service.isCameraEnabled 
                        ? Colors.white24 
                        : Colors.red,
                  ),
                  // 美颜
                  _buildControlButton(
                    icon: Icons.face,
                    label: '美颜',
                    onPressed: () => _showBeautyOptions(service),
                    isActive: false,
                  ),
                ],
              ),
              const SizedBox(height: 24),
              // 挂断按钮
              _buildHangupButton(service),
            ],
          ),
        ),
      ),
    );
  }

  /// 控制按钮
  Widget _buildControlButton({
    required IconData icon,
    required String label,
    required VoidCallback onPressed,
    required bool isActive,
    Color? backgroundColor,
  }) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          width: 56,
          height: 56,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: backgroundColor ?? Colors.white24,
          ),
          child: IconButton(
            icon: Icon(icon, color: Colors.white, size: 28),
            onPressed: onPressed,
          ),
        ),
        const SizedBox(height: 8),
        Text(
          label,
          style: const TextStyle(
            color: Colors.white70,
            fontSize: 12,
          ),
        ),
      ],
    );
  }

  /// 挂断按钮
  Widget _buildHangupButton(WebRTCService service) {
    return Container(
      width: 72,
      height: 72,
      decoration: const BoxDecoration(
        shape: BoxShape.circle,
        color: Colors.red,
        boxShadow: [
          BoxShadow(
            color: Colors.redAccent,
            blurRadius: 20,
            spreadRadius: 5,
          ),
        ],
      ),
      child: IconButton(
        icon: const Icon(Icons.call_end, color: Colors.white, size: 32),
        onPressed: () => service.endCall(),
      ),
    );
  }

  /// 来电接听覆盖层
  Widget _buildIncomingCallOverlay(WebRTCService service) {
    final session = widget.incomingSession!;
    
    return Container(
      color: Colors.black87,
      child: SafeArea(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Spacer(),
            // 来电提示
            Text(
              '${session.callType.displayName}来电',
              style: TextStyle(
                fontSize: 20,
                color: Colors.grey[400],
              ),
            ),
            const SizedBox(height: 32),
            // 来电者头像
            CircleAvatar(
              radius: 80,
              backgroundImage: session.callerAvatar != null
                  ? NetworkImage(session.callerAvatar!)
                  : null,
              backgroundColor: Colors.grey[800],
              child: session.callerAvatar == null
                  ? Text(
                      session.callerName.substring(0, 1).toUpperCase(),
                      style: const TextStyle(
                        fontSize: 64,
                        color: Colors.white,
                      ),
                    )
                  : null,
            ),
            const SizedBox(height: 24),
            // 来电者姓名
            Text(
              session.callerName,
              style: const TextStyle(
                fontSize: 32,
                fontWeight: FontWeight.bold,
                color: Colors.white,
              ),
            ),
            const Spacer(),
            // 接听/拒绝按钮
            Padding(
              padding: const EdgeInsets.symmetric(horizontal: 48),
              child: Row(
                mainAxisAlignment: MainAxisAlignment.spaceBetween,
                children: [
                  // 拒绝按钮
                  _buildIncomingButton(
                    icon: Icons.call_end,
                    label: '拒绝',
                    color: Colors.red,
                    onPressed: () => service.rejectCall(),
                  ),
                  // 接听按钮
                  _buildIncomingButton(
                    icon: Icons.call,
                    label: '接听',
                    color: Colors.green,
                    onPressed: () => service.answerCall(session),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 64),
          ],
        ),
      ),
    );
  }

  /// 来电按钮
  Widget _buildIncomingButton({
    required IconData icon,
    required String label,
    required Color color,
    required VoidCallback onPressed,
  }) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Container(
          width: 80,
          height: 80,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: color,
            boxShadow: [
              BoxShadow(
                color: color.withOpacity(0.5),
                blurRadius: 20,
                spreadRadius: 5,
              ),
            ],
          ),
          child: IconButton(
            icon: Icon(icon, color: Colors.white, size: 36),
            onPressed: onPressed,
          ),
        ),
        const SizedBox(height: 12),
        Text(
          label,
          style: const TextStyle(
            color: Colors.white,
            fontSize: 16,
          ),
        ),
      ],
    );
  }

  /// 显示更多选项
  void _showMoreOptions(WebRTCService service) {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      builder: (context) => Container(
        decoration: const BoxDecoration(
          color: Color(0xFF1E1E1E),
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
        child: SafeArea(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              Container(
                margin: const EdgeInsets.only(top: 8),
                width: 40,
                height: 4,
                decoration: BoxDecoration(
                  color: Colors.grey[600],
                  borderRadius: BorderRadius.circular(2),
                ),
              ),
              ListTile(
                leading: const Icon(Icons.screen_share, color: Colors.white),
                title: const Text('屏幕共享', style: TextStyle(color: Colors.white)),
                onTap: () {
                  Navigator.pop(context);
                  service.startScreenShare();
                },
              ),
              ListTile(
                leading: const Icon(Icons.high_quality, color: Colors.white),
                title: const Text('视频质量', style: TextStyle(color: Colors.white)),
                trailing: Text(
                  service.config.videoQuality.displayName,
                  style: TextStyle(color: Colors.grey[400]),
                ),
                onTap: () {
                  Navigator.pop(context);
                  _showVideoQualityOptions(service);
                },
              ),
              ListTile(
                leading: const Icon(Icons.volume_up, color: Colors.white),
                title: const Text('扬声器', style: TextStyle(color: Colors.white)),
                trailing: Switch(
                  value: true,
                  onChanged: (value) {},
                ),
              ),
              ListTile(
                leading: const Icon(Icons.info_outline, color: Colors.white),
                title: const Text('通话信息', style: TextStyle(color: Colors.white)),
                onTap: () {
                  Navigator.pop(context);
                  _showCallInfo(service);
                },
              ),
            ],
          ),
        ),
      ),
    );
  }

  /// 显示视频质量选项
  void _showVideoQualityOptions(WebRTCService service) {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      builder: (context) => Container(
        decoration: const BoxDecoration(
          color: Color(0xFF1E1E1E),
          borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
        ),
        child: SafeArea(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: VideoQuality.values.map((quality) {
              final isSelected = service.config.videoQuality == quality;
              return ListTile(
                title: Text(
                  quality.displayName,
                  style: TextStyle(
                    color: isSelected ? Colors.blue : Colors.white,
                  ),
                ),
                trailing: isSelected
                    ? const Icon(Icons.check, color: Colors.blue)
                    : null,
                onTap: () {
                  service.changeVideoQuality(quality);
                  Navigator.pop(context);
                },
              );
            }).toList(),
          ),
        ),
      ),
    );
  }

  /// 显示美颜选项
  void _showBeautyOptions(WebRTCService service) {
    showModalBottomSheet(
      context: context,
      backgroundColor: Colors.transparent,
      isScrollControlled: true,
      builder: (context) => DraggableScrollableSheet(
        initialChildSize: 0.6,
        minChildSize: 0.3,
        maxChildSize: 0.8,
        builder: (context, scrollController) {
          return Container(
            decoration: const BoxDecoration(
              color: Color(0xFF1E1E1E),
              borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
            ),
            child: Column(
              children: [
                Container(
                  margin: const EdgeInsets.only(top: 8),
                  width: 40,
                  height: 4,
                  decoration: BoxDecoration(
                    color: Colors.grey[600],
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
                const Padding(
                  padding: EdgeInsets.all(16),
                  child: Text(
                    '美颜设置',
                    style: TextStyle(
                      color: Colors.white,
                      fontSize: 18,
                      fontWeight: FontWeight.bold,
                    ),
                  ),
                ),
                Expanded(
                  child: ListView(
                    controller: scrollController,
                    children: [
                      // 美颜开关
                      SwitchListTile(
                        title: const Text('开启美颜', 
                            style: TextStyle(color: Colors.white)),
                        value: service.config.enableBeautyFilter,
                        onChanged: (value) {
                          service.updateConfig(WebRTCConfig(
                            iceServers: service.config.iceServers,
                            enableAudio: service.config.enableAudio,
                            enableVideo: service.config.enableVideo,
                            videoQuality: service.config.videoQuality,
                            enableBeautyFilter: value,
                            beautyFilter: service.config.beautyFilter,
                          ));
                        },
                      ),
                      const Divider(color: Colors.white24),
                      // 滤镜类型
                      ...BeautyFilter.values.map((filter) {
                        final isSelected = service.config.beautyFilter == filter;
                        return RadioListTile<BeautyFilter>(
                          title: Text(filter.displayName,
                              style: const TextStyle(color: Colors.white)),
                          value: filter,
                          groupValue: service.config.beautyFilter,
                          activeColor: Colors.blue,
                          onChanged: service.config.enableBeautyFilter
                              ? (value) {
                                  service.updateConfig(WebRTCConfig(
                                    iceServers: service.config.iceServers,
                                    enableAudio: service.config.enableAudio,
                                    enableVideo: service.config.enableVideo,
                                    videoQuality: service.config.videoQuality,
                                    enableBeautyFilter: true,
                                    beautyFilter: value!,
                                  ));
                                }
                              : null,
                        );
                      }).toList(),
                    ],
                  ),
                ),
              ],
            ),
          );
        },
      ),
    );
  }

  /// 显示通话信息
  void _showCallInfo(WebRTCService service) {
    final stats = service.networkStats;
    
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: const Color(0xFF1E1E1E),
        title: const Text('通话信息', style: TextStyle(color: Colors.white)),
        content: stats != null
            ? Column(
                mainAxisSize: MainAxisSize.min,
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  _buildInfoRow('延迟', '${stats.rtt} ms'),
                  _buildInfoRow('丢包率', '${stats.packetLossRate.toStringAsFixed(2)}%'),
                  _buildInfoRow('码率', '${stats.bitrate} kbps'),
                  _buildInfoRow('抖动', '${stats.jitter} ms'),
                  _buildInfoRow('音频电平', '${stats.audioLevel}'),
                  _buildInfoRow('帧率', '${stats.frameRate} fps'),
                  _buildInfoRow('网络质量', stats.qualityDescription),
                ],
              )
            : const Text('暂无统计信息', style: TextStyle(color: Colors.grey)),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(label, style: TextStyle(color: Colors.grey[400])),
          Text(value, style: const TextStyle(color: Colors.white)),
        ],
      ),
    );
  }

  /// 显示结束通话确认
  void _showEndCallConfirm(WebRTCService service) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: const Color(0xFF1E1E1E),
        title: const Text('结束通话', style: TextStyle(color: Colors.white)),
        content: const Text(
          '确定要结束当前通话吗？',
          style: TextStyle(color: Colors.white70),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              service.endCall();
            },
            child: const Text('结束', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }
}
