import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/voice_service.dart';

class AiInputBar extends StatefulWidget {
  final TextEditingController controller;
  final FocusNode focusNode;
  final bool isLoading;
  final VoidCallback onSend;
  final Function(String) onVoiceRecord;

  const AiInputBar({
    super.key,
    required this.controller,
    required this.focusNode,
    required this.isLoading,
    required this.onSend,
    required this.onVoiceRecord,
  });

  @override
  State<AiInputBar> createState() => _AiInputBarState();
}

class _AiInputBarState extends State<AiInputBar> {
  bool _isRecording = false;
  bool _showSend = false;

  @override
  void initState() {
    super.initState();
    widget.controller.addListener(() {
      setState(() {
        _showSend = widget.controller.text.trim().isNotEmpty;
      });
    });
  }

  @override
  Widget build(BuildContext context) {
    final voiceService = context.watch<VoiceService>();

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 10,
            offset: const Offset(0, -5),
          ),
        ],
      ),
      child: SafeArea(
        child: voiceService.isRecording
            ? _buildRecordingIndicator(voiceService)
            : _buildInputField(),
      ),
    );
  }

  Widget _buildInputField() {
    return Row(
      children: [
        IconButton(
          icon: const Icon(Icons.mic_none, color: Colors.grey),
          onPressed: _startRecording,
        ),
        Expanded(
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 16),
            decoration: BoxDecoration(
              color: Colors.grey.shade100,
              borderRadius: BorderRadius.circular(24),
            ),
            child: TextField(
              controller: widget.controller,
              focusNode: widget.focusNode,
              decoration: const InputDecoration(
                hintText: '输入消息...',
                border: InputBorder.none,
                contentPadding: EdgeInsets.symmetric(vertical: 12),
              ),
              maxLines: null,
              textInputAction: TextInputAction.send,
              onSubmitted: (_) => widget.onSend(),
            ),
          ),
        ),
        const SizedBox(width: 8),
        if (widget.isLoading)
          const SizedBox(
            width: 40,
            height: 40,
            child: Padding(
              padding: EdgeInsets.all(8),
              child: CircularProgressIndicator(strokeWidth: 2),
            ),
          )
        else if (_showSend)
          FilledButton(
            onPressed: widget.onSend,
            style: FilledButton.styleFrom(
              shape: const CircleBorder(),
              padding: const EdgeInsets.all(12),
            ),
            child: const Icon(Icons.send, size: 20),
          )
        else
          IconButton(
            icon: const Icon(Icons.add_circle_outline, color: Colors.grey),
            onPressed: () {},
          ),
      ],
    );
  }

  Widget _buildRecordingIndicator(VoiceService voiceService) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 12),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          IconButton(
            icon: const Icon(Icons.close, color: Colors.grey),
            onPressed: () => voiceService.cancelRecording(),
          ),
          const SizedBox(width: 16),
          Container(
            padding: const EdgeInsets.symmetric(horizontal: 24, vertical: 12),
            decoration: BoxDecoration(
              color: Colors.red.shade50,
              borderRadius: BorderRadius.circular(24),
            ),
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Icon(Icons.mic, color: Colors.red.shade400),
                const SizedBox(width: 8),
                Text(
                  voiceService.formattedDuration,
                  style: TextStyle(
                    color: Colors.red.shade400,
                    fontWeight: FontWeight.w600,
                  ),
                ),
              ],
            ),
          ),
          const SizedBox(width: 16),
          IconButton(
            icon: Icon(Icons.check_circle, color: Colors.green.shade400, size: 32),
            onPressed: () async {
              final path = await voiceService.stopRecording();
              if (path != null) {
                widget.onVoiceRecord(path);
              }
            },
          ),
        ],
      ),
    );
  }

  void _startRecording() async {
    final voiceService = context.read<VoiceService>();
    await voiceService.startRecording();
  }

  @override
  void dispose() {
    widget.controller.removeListener(() {});
    super.dispose();
  }
}
