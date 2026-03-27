import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/voice_service.dart';

class AiVoicePlayer extends StatefulWidget {
  final String audioPath;
  final bool isSmall;

  const AiVoicePlayer({
    super.key,
    required this.audioPath,
    this.isSmall = false,
  });

  @override
  State<AiVoicePlayer> createState() => _AiVoicePlayerState();
}

class _AiVoicePlayerState extends State<AiVoicePlayer>
    with SingleTickerProviderStateMixin {
  late AnimationController _animationController;

  @override
  void initState() {
    super.initState();
    _animationController = AnimationController(
      vsync: this,
      duration: const Duration(milliseconds: 800),
    );
  }

  @override
  Widget build(BuildContext context) {
    final voiceService = context.watch<VoiceService>();
    final isPlaying = voiceService.isPlaying;

    if (isPlaying) {
      _animationController.repeat();
    } else {
      _animationController.stop();
      _animationController.reset();
    }

    return GestureDetector(
      onTap: () => _togglePlay(voiceService),
      child: Container(
        padding: EdgeInsets.symmetric(
          horizontal: widget.isSmall ? 8 : 12,
          vertical: widget.isSmall ? 4 : 8,
        ),
        decoration: BoxDecoration(
          color: Colors.blue.shade50,
          borderRadius: BorderRadius.circular(20),
        ),
        child: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            AnimatedBuilder(
              animation: _animationController,
              builder: (context, child) {
                return Icon(
                  isPlaying ? Icons.pause : Icons.play_arrow,
                  color: Colors.blue.shade600,
                  size: widget.isSmall ? 16 : 20,
                );
              },
            ),
            const SizedBox(width: 4),
            _buildWaveform(),
            const SizedBox(width: 8),
            Text(
              '语音',
              style: TextStyle(
                color: Colors.blue.shade600,
                fontSize: widget.isSmall ? 11 : 13,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildWaveform() {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: List.generate(4, (index) {
        return AnimatedBuilder(
          animation: _animationController,
          builder: (context, child) {
            final height = 4 + (index % 2 == 0 ? 8 : 4) * _animationController.value;
            return Container(
              margin: const EdgeInsets.symmetric(horizontal: 1),
              width: 2,
              height: height,
              decoration: BoxDecoration(
                color: Colors.blue.shade400,
                borderRadius: BorderRadius.circular(1),
              ),
            );
          },
        );
      }),
    );
  }

  void _togglePlay(VoiceService voiceService) {
    if (voiceService.isPlaying) {
      voiceService.stopPlaying();
    } else {
      if (widget.audioPath.startsWith('http')) {
        voiceService.playFromUrl(widget.audioPath);
      } else {
        voiceService.playRecording(widget.audioPath);
      }
    }
  }

  @override
  void dispose() {
    _animationController.dispose();
    super.dispose();
  }
}
