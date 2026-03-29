import 'package:flutter/material.dart';

enum AiMessageType { user, assistant, error, system }

class AiMessage {
  final String id;
  final String content;
  final AiMessageType type;
  final DateTime timestamp;
  final Map<String, dynamic>? metadata;
  final bool isFavorite;
  final List<String>? attachments;

  AiMessage({
    required this.id,
    required this.content,
    required this.type,
    required this.timestamp,
    this.metadata,
    this.isFavorite = false,
    this.attachments,
  });

  factory AiMessage.fromJson(Map<String, dynamic> json) {
    return AiMessage(
      id: json['id'] ?? '',
      content: json['content'] ?? json['message'] ?? '',
      type: _parseType(json['type'] ?? json['role']),
      timestamp: json['timestamp'] != null 
          ? DateTime.parse(json['timestamp'])
          : DateTime.now(),
      metadata: json['metadata'] ?? json['context'],
      isFavorite: json['isFavorite'] ?? false,
      attachments: json['attachments'] != null 
          ? List<String>.from(json['attachments'])
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'content': content,
      'type': type.name,
      'timestamp': timestamp.toIso8601String(),
      'metadata': metadata,
      'isFavorite': isFavorite,
      'attachments': attachments,
    };
  }

  static AiMessageType _parseType(String type) {
    switch (type.toLowerCase()) {
      case 'user':
      case 'human':
        return AiMessageType.user;
      case 'assistant':
      case 'ai':
      case 'bot':
        return AiMessageType.assistant;
      case 'error':
        return AiMessageType.error;
      default:
        return AiMessageType.system;
    }
  }

  AiMessage copyWith({
    String? id,
    String? content,
    AiMessageType? type,
    DateTime? timestamp,
    Map<String, dynamic>? metadata,
    bool? isFavorite,
    List<String>? attachments,
  }) {
    return AiMessage(
      id: id ?? this.id,
      content: content ?? this.content,
      type: type ?? this.type,
      timestamp: timestamp ?? this.timestamp,
      metadata: metadata ?? this.metadata,
      isFavorite: isFavorite ?? this.isFavorite,
      attachments: attachments ?? this.attachments,
    );
  }

  bool get isVoice => metadata?['isVoice'] == true;
  bool get isVoiceResponse => metadata?['isVoiceResponse'] == true;
  String? get audioUrl => metadata?['audioUrl'];
  List<String>? get suggestions => metadata?['suggestions'] != null
      ? List<String>.from(metadata!['suggestions'])
      : null;
  double? get confidence => metadata?['confidence']?.toDouble();
  String? get intent => metadata?['intent'];
}

class AiConversation {
  final String id;
  final String title;
  final DateTime createdAt;
  final DateTime updatedAt;
  final int messageCount;
  final String? preview;
  final bool isPinned;

  AiConversation({
    required this.id,
    required this.title,
    required this.createdAt,
    required this.updatedAt,
    required this.messageCount,
    this.preview,
    this.isPinned = false,
  });

  factory AiConversation.fromJson(Map<String, dynamic> json) {
    return AiConversation(
      id: json['id'] ?? '',
      title: json['title'] ?? '未命名对话',
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'])
          : DateTime.now(),
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'])
          : DateTime.now(),
      messageCount: json['messageCount'] ?? 0,
      preview: json['preview'],
      isPinned: json['isPinned'] ?? false,
    );
  }

  String get formattedDate {
    final now = DateTime.now();
    final diff = now.difference(updatedAt);
    
    if (diff.inMinutes < 1) return '刚刚';
    if (diff.inHours < 1) return '${diff.inMinutes}分钟前';
    if (diff.inDays < 1) return '${diff.inHours}小时前';
    if (diff.inDays == 1) return '昨天';
    if (diff.inDays < 7) return '${diff.inDays}天前';
    return '${updatedAt.month}/${updatedAt.day}';
  }
}

enum AiFeature {
  chat,
  voice,
  image,
  document,
  code,
  translate,
  summarize,
}

extension AiFeatureExtension on AiFeature {
  String get displayName {
    switch (this) {
      case AiFeature.chat:
        return '对话';
      case AiFeature.voice:
        return '语音';
      case AiFeature.image:
        return '识图';
      case AiFeature.document:
        return '文档';
      case AiFeature.code:
        return '代码';
      case AiFeature.translate:
        return '翻译';
      case AiFeature.summarize:
        return '总结';
    }
  }

  IconData get icon {
    switch (this) {
      case AiFeature.chat:
        return Icons.chat_bubble_outline;
      case AiFeature.voice:
        return Icons.mic_none;
      case AiFeature.image:
        return Icons.image_outlined;
      case AiFeature.document:
        return Icons.description_outlined;
      case AiFeature.code:
        return Icons.code;
      case AiFeature.translate:
        return Icons.translate;
      case AiFeature.summarize:
        return Icons.summarize;
    }
  }
}

class AiRecommendation {
  final String id;
  final String title;
  final String description;
  final String category;
  final double relevance;
  final VoidCallback? onTap;

  AiRecommendation({
    required this.id,
    required this.title,
    required this.description,
    required this.category,
    required this.relevance,
    this.onTap,
  });
}
