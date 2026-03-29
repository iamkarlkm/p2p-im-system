import 'package:flutter/material.dart';
import 'dart:async';

/// 语音搜索按钮组件
/// 支持长按录音、语音识别动画、实时波形显示
/// 
/// Author: IM Development Team
/// Since: 2026-03-28
class VoiceSearchButton extends StatefulWidget {
  final Function(String) onResult;
  final bool isListening;
  final Function(bool) onListeningChanged;

  const VoiceSearchButton({
    Key? key,
    required this.onResult,
    required this.isListening,
    required this.onListeningChanged,
  }) : super(key: key);

  @override
  State<VoiceSearchButton> createState() => _VoiceSearchButtonState();
}

class _VoiceSearchButtonState extends State<VoiceSearchButton>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;
  late Animation<double> _pulseAnimation;
  Timer? _recordingTimer;
  int _recordingSeconds = 0;
  bool _isCanceling = false;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 1000),
    );
    _pulseAnimation = Tween<double>(begin: 1.0, end: 1.3).animate(
      CurvedAnimation(
        parent: _animationController,
        curve: Curves.easeInOut,
      ),
    );
    _animationController.repeat(reverse: true);
  }

  @override
  void dispose() {
    _animationController.dispose();
    _recordingTimer?.cancel();
    super.dispose();
  }

  void _startRecording() {
    widget.onListeningChanged(true);
    _recordingSeconds = 0;
    _recordingTimer = Timer.periodic(const Duration(seconds: 1), (timer) {
      setState(() => _recordingSeconds++);
    });
    
    // 模拟语音识别（实际应调用语音识别SDK）
    Future.delayed(const Duration(seconds: 3), () {
      if (widget.isListening) {
        _stopRecording();
      }
    });
  }

  void _stopRecording() {
    widget.onListeningChanged(false);
    _recordingTimer?.cancel();
    
    if (!_isCanceling) {
      // 模拟识别结果
      widget.onResult('附近好吃的火锅');
    }
    _isCanceling = false;
  }

  void _cancelRecording() {
    _isCanceling = true;
    _stopRecording();
  }

  @override
  Widget build(BuildContext context) {
    if (widget.isListening) {
      return _buildListeningOverlay();
    }

    return GestureDetector(
      onLongPressStart: (_) => _startRecording(),
      onLongPressEnd: (_) => _stopRecording(),
      onLongPressMoveUpdate: (details) {
        // 检测是否滑动到取消区域
        if (details.localOffsetFromOrigin.dy < -100) {
          _isCanceling = true;
        } else {
          _isCanceling = false;
        }
      },
      child: Container(
        width: 48,
        height: 48,
        decoration: BoxDecoration(
          color: Colors.blue[500],
          shape: BoxShape.circle,
          boxShadow: [
            BoxShadow(
              color: Colors.blue.withOpacity(0.3),
              blurRadius: 8,
              offset: const Offset(0, 2),
            ),
          ],
        ),
        child: const Icon(
          Icons.mic,
          color: Colors.white,
        ),
      ),
    );
  }

  Widget _buildListeningOverlay() {
    return Stack(
      children: [
        // 全屏半透明背景
        Positioned.fill(
          child: GestureDetector(
            onTap: _cancelRecording,
            child: Container(
              color: Colors.black54,
            ),
          ),
        ),
        
        // 中央录音UI
        Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              // 波形动画
              _buildWaveform(),
              const SizedBox(height: 32),
              
              // 录音时间
              Text(
                '${_recordingSeconds}s',
                style: const TextStyle(
                  color: Colors.white,
                  fontSize: 24,
                  fontWeight: FontWeight.bold,
                ),
              ),
              const SizedBox(height: 16),
              
              // 提示文字
              Text(
                _isCanceling ? '松开取消' : '松开结束，上滑取消',
                style: TextStyle(
                  color: _isCanceling ? Colors.red[300] : Colors.white70,
                  fontSize: 14,
                ),
              ),
              const SizedBox(height: 48),
              
              // 录音按钮
              AnimatedBuilder(
                animation: _pulseAnimation,
                builder: (context, child) {
                  return Transform.scale(
                    scale: _pulseAnimation.value,
                    child: Container(
                      width: 80,
                      height: 80,
                      decoration: BoxDecoration(
                        color: _isCanceling ? Colors.red : Colors.blue,
                        shape: BoxShape.circle,
                      ),
                      child: Icon(
                        _isCanceling ? Icons.close : Icons.mic,
                        color: Colors.white,
                        size: 40,
                      ),
                    ),
                  );
                },
              ),
            ],
          ),
        ),
      ],
    );
  }

  Widget _buildWaveform() {
    return Container(
      width: 200,
      height: 60,
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        crossAxisAlignment: CrossAxisAlignment.center,
        children: List.generate(20, (index) {
          return AnimatedContainer(
            duration: const Duration(milliseconds: 100),
            width: 4,
            height: _getBarHeight(index),
            decoration: BoxDecoration(
              color: _isCanceling ? Colors.red[300] : Colors.blue[300],
              borderRadius: BorderRadius.circular(2),
            ),
          );
        }),
      ),
    );
  }

  double _getBarHeight(int index) {
    // 模拟波形高度变化
    final baseHeight = 20.0;
    final variation = (index % 3) * 10.0;
    final random = (DateTime.now().millisecond % 10).toDouble();
    return baseHeight + variation + random;
  }
}
