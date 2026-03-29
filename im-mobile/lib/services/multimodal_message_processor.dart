import 'dart:async';
import 'dart:convert';
import 'dart:io';
import 'package:http/http.dart' as http;
import 'package:http_parser/http_parser.dart';
import '../models/multimodal_message.dart';
import '../models/ai_assistant.dart';
import '../utils/api_client.dart';

/// 消息处理结果回调
typedef MessageProcessCallback = void Function(MultimodalMessage message);
typedef StreamProcessCallback = void Function(String chunk, bool isDone);

/// 多模态消息处理器服务
class MultimodalMessageProcessor {
  static final MultimodalMessageProcessor _instance = MultimodalMessageProcessor._internal();
  factory MultimodalMessageProcessor() => _instance;
  MultimodalMessageProcessor._internal();

  final ApiClient _apiClient = ApiClient();
  final Map<String, StreamSubscription> _activeStreams = {};

  /// 处理队列（用于保证消息顺序）
  final List<MultimodalMessage> _processingQueue = [];
  bool _isProcessing = false;

  /// 解析消息类型
  MessageType parseMessageType(String content, List<MessageAttachment> attachments) {
    if (attachments.isEmpty) return MessageType.text;
    
    if (attachments.length > 1) return MessageType.mixed;
    
    final attachment = attachments.first;
    final mimeType = attachment.fileType.toLowerCase();
    
    if (mimeType.startsWith('image/')) return MessageType.image;
    if (mimeType.startsWith('audio/')) return MessageType.audio;
    if (mimeType.startsWith('video/')) return MessageType.video;
    if (content.isNotEmpty) return MessageType.mixed;
    
    return MessageType.file;
  }

  /// 发送消息
  Future<MultimodalMessage> sendMessage({
    required String conversationId,
    required String content,
    List<MessageAttachment> attachments = const [],
    String? replyToMessageId,
  }) async {
    final message = MultimodalMessage(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      conversationId: conversationId,
      type: parseMessageType(content, attachments),
      content: content,
      attachments: attachments,
      timestamp: DateTime.now(),
      status: MessageStatus.sending,
    );

    try {
      final response = await _apiClient.post(
        '/api/multimodal/messages',
        body: {
          'conversationId': conversationId,
          'content': content,
          'attachments': attachments.map((a) => a.toJson()).toList(),
          'replyToMessageId': replyToMessageId,
        },
      );

      if (response.statusCode == 200 || response.statusCode == 201) {
        final data = jsonDecode(response.body);
        return message.copyWith(
          id: data['id'],
          status: MessageStatus.sent,
        );
      } else {
        return message.copyWith(status: MessageStatus.failed);
      }
    } catch (e) {
      return message.copyWith(status: MessageStatus.failed);
    }
  }

  /// 发送流式AI消息
  Future<void> sendStreamingMessage({
    required String conversationId,
    required String assistantId,
    required String content,
    List<MessageAttachment> attachments = const [],
    required StreamProcessCallback onChunk,
    Map<String, dynamic>? context,
  }) async {
    final streamId = '${conversationId}_${DateTime.now().millisecondsSinceEpoch}';
    
    try {
      final request = http.Request(
        'POST',
        Uri.parse('${_apiClient.baseUrl}/api/multimodal/assistants/$assistantId/stream'),
      );
      request.headers.addAll(_apiClient.headers);
      request.body = jsonEncode({
        'conversationId': conversationId,
        'content': content,
        'attachments': attachments.map((a) => a.toJson()).toList(),
        'context': context,
      });

      final response = await http.Client().send(request);
      
      if (response.statusCode != 200) {
        onChunk('', true);
        return;
      }

      final stream = response.stream
          .transform(utf8.decoder)
          .transform(const LineSplitter());

      _activeStreams[streamId] = stream.listen(
        (line) {
          if (line.startsWith('data: ')) {
            final data = line.substring(6);
            if (data == '[DONE]') {
              onChunk('', true);
            } else {
              try {
                final json = jsonDecode(data);
                final chunk = json['content'] ?? json['delta'] ?? '';
                onChunk(chunk, false);
              } catch (_) {
                onChunk(data, false);
              }
            }
          }
        },
        onError: (_) => onChunk('', true),
        onDone: () => onChunk('', true),
      );
    } catch (e) {
      onChunk('', true);
    }
  }

  /// 取消流式响应
  void cancelStream(String conversationId) {
    final streamId = _activeStreams.keys
        .firstWhere((k) => k.startsWith(conversationId), orElse: () => '');
    if (streamId.isNotEmpty) {
      _activeStreams[streamId]?.cancel();
      _activeStreams.remove(streamId);
    }
  }

  /// 上传附件
  Future<MessageAttachment?> uploadAttachment(File file, {String? conversationId}) async {
    try {
      final uri = Uri.parse('${_apiClient.baseUrl}/api/multimodal/upload');
      final request = http.MultipartRequest('POST', uri);
      
      request.headers.addAll(_apiClient.headers);
      if (conversationId != null) {
        request.fields['conversationId'] = conversationId;
      }

      final mimeType = _getMimeType(file.path);
      request.files.add(await http.MultipartFile.fromPath(
        'file',
        file.path,
        contentType: MediaType.parse(mimeType),
      ));

      final response = await request.send();
      final responseData = await response.stream.bytesToString();

      if (response.statusCode == 200 || response.statusCode == 201) {
        final data = jsonDecode(responseData);
        return MessageAttachment.fromJson(data);
      }
      return null;
    } catch (e) {
      return null;
    }
  }

  /// 获取Mime类型
  String _getMimeType(String filePath) {
    final ext = filePath.split('.').last.toLowerCase();
    final mimeTypes = {
      'jpg': 'image/jpeg',
      'jpeg': 'image/jpeg',
      'png': 'image/png',
      'gif': 'image/gif',
      'webp': 'image/webp',
      'mp4': 'video/mp4',
      'mov': 'video/quicktime',
      'mp3': 'audio/mpeg',
      'wav': 'audio/wav',
      'm4a': 'audio/m4a',
      'pdf': 'application/pdf',
      'doc': 'application/msword',
      'docx': 'application/vnd.openxmlformats-officedocument.wordprocessingml.document',
    };
    return mimeTypes[ext] ?? 'application/octet-stream';
  }

  /// 处理接收到的消息
  MultimodalMessage processIncomingMessage(Map<String, dynamic> data) {
    return MultimodalMessage.fromJson(data);
  }

  /// 批量处理消息
  List<MultimodalMessage> processIncomingMessages(List<dynamic> dataList) {
    return dataList.map((d) => MultimodalMessage.fromJson(d)).toList();
  }

  /// 创建文本消息
  MultimodalMessage createTextMessage(String conversationId, String content) {
    return MultimodalMessage(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      conversationId: conversationId,
      type: MessageType.text,
      content: content,
      timestamp: DateTime.now(),
    );
  }

  /// 创建图片消息
  MultimodalMessage createImageMessage(
    String conversationId,
    MessageAttachment attachment, {
    String caption = '',
  }) {
    return MultimodalMessage(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      conversationId: conversationId,
      type: MessageType.image,
      content: caption,
      attachments: [attachment],
      timestamp: DateTime.now(),
    );
  }

  /// 创建音频消息
  MultimodalMessage createAudioMessage(
    String conversationId,
    MessageAttachment attachment, {
    String? transcript,
  }) {
    return MultimodalMessage(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      conversationId: conversationId,
      type: MessageType.audio,
      content: transcript ?? '',
      attachments: [attachment],
      timestamp: DateTime.now(),
      metadata: transcript != null ? {'transcript': transcript} : null,
    );
  }

  /// 创建视频消息
  MultimodalMessage createVideoMessage(
    String conversationId,
    MessageAttachment attachment, {
    String caption = '',
  }) {
    return MultimodalMessage(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      conversationId: conversationId,
      type: MessageType.video,
      content: caption,
      attachments: [attachment],
      timestamp: DateTime.now(),
    );
  }

  /// 释放资源
  void dispose() {
    for (final sub in _activeStreams.values) {
      sub.cancel();
    }
    _activeStreams.clear();
  }
}
