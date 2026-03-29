import 'dart:convert';
import 'dart:io';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import '../models/user.dart';

/// 消息类型枚举
enum MessageType {
  text,
  image,
  audio,
  video,
  file,
  mixed,
}

/// 消息附件模型
class MessageAttachment {
  final String id;
  final String fileName;
  final String fileType;
  final int fileSize;
  final String? url;
  final String? thumbnailUrl;
  final int? width;
  final int? height;
  final int? duration;

  MessageAttachment({
    required this.id,
    required this.fileName,
    required this.fileType,
    required this.fileSize,
    this.url,
    this.thumbnailUrl,
    this.width,
    this.height,
    this.duration,
  });

  factory MessageAttachment.fromJson(Map<String, dynamic> json) {
    return MessageAttachment(
      id: json['id'],
      fileName: json['fileName'],
      fileType: json['fileType'],
      fileSize: json['fileSize'],
      url: json['url'],
      thumbnailUrl: json['thumbnailUrl'],
      width: json['width'],
      height: json['height'],
      duration: json['duration'],
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'fileName': fileName,
    'fileType': fileType,
    'fileSize': fileSize,
    'url': url,
    'thumbnailUrl': thumbnailUrl,
    'width': width,
    'height': height,
    'duration': duration,
  };
}

/// 多模态消息模型
class MultimodalMessage {
  final String id;
  final String conversationId;
  final String? senderId;
  final String? receiverId;
  final MessageType type;
  final String content;
  final List<MessageAttachment> attachments;
  final DateTime timestamp;
  final bool isStreaming;
  final String? streamingContent;
  final Map<String, dynamic>? metadata;
  final MessageStatus status;

  MultimodalMessage({
    required this.id,
    required this.conversationId,
    this.senderId,
    this.receiverId,
    required this.type,
    required this.content,
    this.attachments = const [],
    required this.timestamp,
    this.isStreaming = false,
    this.streamingContent,
    this.metadata,
    this.status = MessageStatus.sent,
  });

  factory MultimodalMessage.fromJson(Map<String, dynamic> json) {
    return MultimodalMessage(
      id: json['id'],
      conversationId: json['conversationId'],
      senderId: json['senderId'],
      receiverId: json['receiverId'],
      type: MessageType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => MessageType.text,
      ),
      content: json['content'],
      attachments: (json['attachments'] as List?)
          ?.map((a) => MessageAttachment.fromJson(a))
          .toList() ?? [],
      timestamp: DateTime.parse(json['timestamp']),
      isStreaming: json['isStreaming'] ?? false,
      streamingContent: json['streamingContent'],
      metadata: json['metadata'],
      status: MessageStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => MessageStatus.sent,
      ),
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'conversationId': conversationId,
    'senderId': senderId,
    'receiverId': receiverId,
    'type': type.name,
    'content': content,
    'attachments': attachments.map((a) => a.toJson()).toList(),
    'timestamp': timestamp.toIso8601String(),
    'isStreaming': isStreaming,
    'streamingContent': streamingContent,
    'metadata': metadata,
    'status': status.name,
  };

  MultimodalMessage copyWith({
    String? id,
    String? conversationId,
    String? senderId,
    String? receiverId,
    MessageType? type,
    String? content,
    List<MessageAttachment>? attachments,
    DateTime? timestamp,
    bool? isStreaming,
    String? streamingContent,
    Map<String, dynamic>? metadata,
    MessageStatus? status,
  }) {
    return MultimodalMessage(
      id: id ?? this.id,
      conversationId: conversationId ?? this.conversationId,
      senderId: senderId ?? this.senderId,
      receiverId: receiverId ?? this.receiverId,
      type: type ?? this.type,
      content: content ?? this.content,
      attachments: attachments ?? this.attachments,
      timestamp: timestamp ?? this.timestamp,
      isStreaming: isStreaming ?? this.isStreaming,
      streamingContent: streamingContent ?? this.streamingContent,
      metadata: metadata ?? this.metadata,
      status: status ?? this.status,
    );
  }
}

/// 消息状态枚举
enum MessageStatus {
  sending,
  sent,
  delivered,
  read,
  failed,
}
