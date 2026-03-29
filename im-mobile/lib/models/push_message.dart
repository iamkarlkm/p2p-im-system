class PushMessage {
  final String id;
  final String type;
  final String senderId;
  final String senderName;
  final String? senderAvatar;
  final String conversationId;
  final String content;
  final DateTime timestamp;
  final Map<String, dynamic>? extras;

  PushMessage({
    required this.id,
    required this.type,
    required this.senderId,
    required this.senderName,
    this.senderAvatar,
    required this.conversationId,
    required this.content,
    required this.timestamp,
    this.extras,
  });

  factory PushMessage.fromJson(Map<String, dynamic> json) {
    return PushMessage(
      id: json['id'] ?? '',
      type: json['type'] ?? 'message',
      senderId: json['senderId'] ?? '',
      senderName: json['senderName'] ?? '未知用户',
      senderAvatar: json['senderAvatar'],
      conversationId: json['conversationId'] ?? '',
      content: json['content'] ?? '',
      timestamp: json['timestamp'] != null
          ? DateTime.parse(json['timestamp'])
          : DateTime.now(),
      extras: json['extras'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'type': type,
      'senderId': senderId,
      'senderName': senderName,
      'senderAvatar': senderAvatar,
      'conversationId': conversationId,
      'content': content,
      'timestamp': timestamp.toIso8601String(),
      'extras': extras,
    };
  }

  bool get isTextMessage => type == 'text' || type == 'message';
  bool get isImageMessage => type == 'image';
  bool get isVoiceMessage => type == 'voice';
  bool get isVideoMessage => type == 'video';
  bool get isFileMessage => type == 'file';
  bool get isSystemMessage => type == 'system';

  String get displayContent {
    switch (type) {
      case 'image':
        return '[图片]';
      case 'voice':
        return '[语音]';
      case 'video':
        return '[视频]';
      case 'file':
        return '[文件]';
      case 'location':
        return '[位置]';
      default:
        return content;
    }
  }

  PushMessage copyWith({
    String? id,
    String? type,
    String? senderId,
    String? senderName,
    String? senderAvatar,
    String? conversationId,
    String? content,
    DateTime? timestamp,
    Map<String, dynamic>? extras,
  }) {
    return PushMessage(
      id: id ?? this.id,
      type: type ?? this.type,
      senderId: senderId ?? this.senderId,
      senderName: senderName ?? this.senderName,
      senderAvatar: senderAvatar ?? this.senderAvatar,
      conversationId: conversationId ?? this.conversationId,
      content: content ?? this.content,
      timestamp: timestamp ?? this.timestamp,
      extras: extras ?? this.extras,
    );
  }

  @override
  String toString() {
    return 'PushMessage(id: $id, type: $type, sender: $senderName, content: $content)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is PushMessage && other.id == id;
  }

  @override
  int get hashCode => id.hashCode;
}
