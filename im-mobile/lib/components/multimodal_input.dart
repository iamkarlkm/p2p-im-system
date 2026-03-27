import 'dart:async';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart';
import '../models/multimodal_message.dart';
import '../services/multimodal_message_processor.dart';

/// 多模态输入组件
class MultimodalInput extends StatefulWidget {
  final Function(String text, List<MessageAttachment> attachments) onSend;
  final VoidCallback? onVoiceRecordStart;
  final VoidCallback? onVoiceRecordStop;
  final bool isLoading;
  final String? hintText;

  const MultimodalInput({
    super.key,
    required this.onSend,
    this.onVoiceRecordStart,
    this.onVoiceRecordStop,
    this.isLoading = false,
    this.hintText,
  });

  @override
  State<MultimodalInput> createState() => _MultimodalInputState();
}

class _MultimodalInputState extends State<MultimodalInput> {
  final TextEditingController _textController = TextEditingController();
  final FocusNode _focusNode = FocusNode();
  final List<MessageAttachment> _attachments = [];
  final MultimodalMessageProcessor _processor = MultimodalMessageProcessor();
  final ImagePicker _imagePicker = ImagePicker();

  bool _isRecording = false;
  bool _showAttachmentOptions = false;

  bool get _canSend => _textController.text.trim().isNotEmpty || _attachments.isNotEmpty;

  @override
  void dispose() {
    _textController.dispose();
    _focusNode.dispose();
    super.dispose();
  }

  void _sendMessage() {
    if (!_canSend || widget.isLoading) return;

    final text = _textController.text.trim();
    final attachments = List<MessageAttachment>.from(_attachments);

    widget.onSend(text, attachments);

    _textController.clear();
    setState(() => _attachments.clear());
  }

  Future<void> _pickImage(ImageSource source) async {
    try {
      final XFile? image = await _imagePicker.pickImage(
        source: source,
        imageQuality: 85,
      );
      
      if (image != null) {
        await _uploadAttachment(File(image.path));
      }
    } catch (e) {
      // 处理错误
    }
    
    setState(() => _showAttachmentOptions = false);
  }

  Future<void> _pickVideo() async {
    try {
      final XFile? video = await _imagePicker.pickVideo(source: ImageSource.gallery);
      
      if (video != null) {
        await _uploadAttachment(File(video.path));
      }
    } catch (e) {
      // 处理错误
    }
    
    setState(() => _showAttachmentOptions = false);
  }

  Future<void> _uploadAttachment(File file) async {
    final attachment = await _processor.uploadAttachment(file);
    if (attachment != null) {
      setState(() => _attachments.add(attachment));
    }
  }

  void _removeAttachment(int index) {
    setState(() => _attachments.removeAt(index));
  }

  void _startVoiceRecording() {
    setState(() => _isRecording = true);
    widget.onVoiceRecordStart?.call();
  }

  void _stopVoiceRecording() {
    setState(() => _isRecording = false);
    widget.onVoiceRecordStop?.call();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        // 附件预览
        if (_attachments.isNotEmpty) _buildAttachmentPreview(),

        // 附件选项
        if (_showAttachmentOptions) _buildAttachmentOptions(),

        // 输入框
        Container(
          padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 8),
          decoration: BoxDecoration(
            color: Theme.of(context).colorScheme.surface,
            border: Border(
              top: BorderSide(color: Theme.of(context).dividerColor),
            ),
          ),
          child: SafeArea(
            child: Row(
              crossAxisAlignment: CrossAxisAlignment.end,
              children: [
                // 附件按钮
                IconButton(
                  onPressed: () => setState(() => _showAttachmentOptions = !_showAttachmentOptions),
                  icon: Icon(
                    _showAttachmentOptions ? Icons.close : Icons.add_circle_outline,
                    color: Theme.of(context).colorScheme.primary,
                  ),
                ),

                // 语音按钮
                IconButton(
                  onPressed: _isRecording ? _stopVoiceRecording : _startVoiceRecording,
                  icon: Icon(
                    _isRecording ? Icons.stop : Icons.mic_none,
                    color: _isRecording ? Colors.red : Theme.of(context).colorScheme.primary,
                  ),
                ),

                // 文本输入
                Expanded(
                  child: Container(
                    constraints: const BoxConstraints(maxHeight: 120),
                    decoration: BoxDecoration(
                      color: Theme.of(context).colorScheme.surfaceContainerHighest,
                      borderRadius: BorderRadius.circular(24),
                    ),
                    child: TextField(
                      controller: _textController,
                      focusNode: _focusNode,
                      maxLines: null,
                      textInputAction: TextInputAction.newline,
                      keyboardType: TextInputType.multiline,
                      onChanged: (_) => setState(() {}),
                      onSubmitted: (_) => _sendMessage(),
                      decoration: InputDecoration(
                        hintText: widget.hintText ?? '输入消息...',
                        contentPadding: const EdgeInsets.symmetric(
                          horizontal: 16,
                          vertical: 12,
                        ),
                        border: InputBorder.none,
                        suffixIcon: _textController.text.isNotEmpty
                            ? IconButton(
                                icon: const Icon(Icons.clear, size: 18),
                                onPressed: () {
                                  _textController.clear();
                                  setState(() {});
                                },
                              )
                            : null,
                      ),
                    ),
                  ),
                ),

                // 发送按钮
                if (_canSend)
                  IconButton(
                    onPressed: widget.isLoading ? null : _sendMessage,
                    icon: widget.isLoading
                        ? const SizedBox(
                            width: 20,
                            height: 20,
                            child: CircularProgressIndicator(strokeWidth: 2),
                          )
                        : const Icon(Icons.send),
                    color: Theme.of(context).colorScheme.primary,
                  )
                else
                  IconButton(
                    onPressed: () {},
                    icon: const Icon(Icons.thumb_up_outlined),
                    color: Theme.of(context).colorScheme.outline,
                  ),
              ],
            ),
          ),
        ),

        // 录音中指示器
        if (_isRecording) _buildRecordingIndicator(),
      ],
    );
  }

  Widget _buildAttachmentPreview() {
    return Container(
      height: 80,
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 8),
      child: ListView.builder(
        scrollDirection: Axis.horizontal,
        itemCount: _attachments.length,
        itemBuilder: (context, index) {
          final attachment = _attachments[index];
          return _buildAttachmentItem(attachment, index);
        },
      ),
    );
  }

  Widget _buildAttachmentItem(MessageAttachment attachment, int index) {
    final isImage = attachment.fileType.startsWith('image/');
    final isVideo = attachment.fileType.startsWith('video/');
    final isAudio = attachment.fileType.startsWith('audio/');

    return Container(
      width: 70,
      margin: const EdgeInsets.only(right: 8),
      child: Stack(
        children: [
          Container(
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(8),
              color: Theme.of(context).colorScheme.surfaceContainerHighest,
            ),
            child: ClipRRect(
              borderRadius: BorderRadius.circular(8),
              child: isImage && attachment.thumbnailUrl != null
                  ? Image.network(
                      attachment.thumbnailUrl!,
                      width: 70,
                      height: 70,
                      fit: BoxFit.cover,
                    )
                  : Center(
                      child: Icon(
                        isImage
                            ? Icons.image
                            : isVideo
                                ? Icons.videocam
                                : isAudio
                                    ? Icons.audio_file
                                    : Icons.insert_drive_file,
                        size: 32,
                        color: Theme.of(context).colorScheme.primary,
                      ),
                    ),
            ),
          ),
          // 删除按钮
          Positioned(
            top: 2,
            right: 2,
            child: GestureDetector(
              onTap: () => _removeAttachment(index),
              child: Container(
                padding: const EdgeInsets.all(2),
                decoration: const BoxDecoration(
                  color: Colors.red,
                  shape: BoxShape.circle,
                ),
                child: const Icon(Icons.close, size: 12, color: Colors.white),
              ),
            ),
          ),
          // 文件类型标签
          if (!isImage)
            Positioned(
              bottom: 2,
              left: 2,
              right: 2,
              child: Container(
                padding: const EdgeInsets.symmetric(horizontal: 4, vertical: 2),
                decoration: BoxDecoration(
                  color: Colors.black54,
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(
                  attachment.fileName.split('.').last.toUpperCase(),
                  style: const TextStyle(
                    color: Colors.white,
                    fontSize: 8,
                  ),
                  textAlign: TextAlign.center,
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                ),
              ),
            ),
        ],
      ),
    );
  }

  Widget _buildAttachmentOptions() {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 12),
      decoration: BoxDecoration(
        color: Theme.of(context).colorScheme.surfaceContainerLow,
        border: Border(
          top: BorderSide(color: Theme.of(context).dividerColor),
        ),
      ),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceEvenly,
        children: [
          _buildAttachmentOption(
            icon: Icons.photo_camera,
            label: '拍照',
            onTap: () => _pickImage(ImageSource.camera),
            color: Colors.blue,
          ),
          _buildAttachmentOption(
            icon: Icons.photo_library,
            label: '相册',
            onTap: () => _pickImage(ImageSource.gallery),
            color: Colors.green,
          ),
          _buildAttachmentOption(
            icon: Icons.videocam,
            label: '视频',
            onTap: _pickVideo,
            color: Colors.orange,
          ),
          _buildAttachmentOption(
            icon: Icons.folder,
            label: '文件',
            onTap: () {
              // 文件选择器
              setState(() => _showAttachmentOptions = false);
            },
            color: Colors.purple,
          ),
        ],
      ),
    );
  }

  Widget _buildAttachmentOption({
    required IconData icon,
    required String label,
    required VoidCallback onTap,
    required Color color,
  }) {
    return InkWell(
      onTap: onTap,
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 56,
            height: 56,
            decoration: BoxDecoration(
              color: color.withValues(alpha: 0.1),
              shape: BoxShape.circle,
            ),
            child: Icon(icon, color: color, size: 28),
          ),
          const SizedBox(height: 4),
          Text(label, style: const TextStyle(fontSize: 12)),
        ],
      ),
    );
  }

  Widget _buildRecordingIndicator() {
    return Container(
      padding: const EdgeInsets.all(16),
      color: Colors.red.withValues(alpha: 0.1),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.mic, color: Colors.red),
          const SizedBox(width: 8),
          const Text(
            '录音中...',
            style: TextStyle(color: Colors.red),
          ),
          const SizedBox(width: 16),
          TextButton(
            onPressed: _stopVoiceRecording,
            style: TextButton.styleFrom(
              foregroundColor: Colors.red,
            ),
            child: const Text('停止'),
          ),
        ],
      ),
    );
  }
}
