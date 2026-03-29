class Message {
  final String id;
  final String conversationId;
  final String? conversationName;
  final String senderId;
  final String? senderName;
  final String? senderAvatar;
  final String type;
  final String content;
  final DateTime timestamp;
  final bool isRead;
  final Map<String, dynamic>? extras;

  Message({
    required this.id,
    required this.conversationId,
    this.conversationName,
    required this.senderId,
    this.senderName,
    this.senderAvatar,
    required this.type,
    required this.content,
    required this.timestamp,
    this.isRead = false,
    this.extras,
  });

  factory Message.fromJson(Map<String, dynamic> json) {
    return Message(
      id: json['id'] ?? '',
      conversationId: json['conversationId'] ?? '',
      conversationName: json['conversationName'],
      senderId: json['senderId'] ?? '',
      senderName: json['senderName'],
      senderAvatar: json['senderAvatar'],
      type: json['type'] ?? 'text',
      content: json['content'] ?? '',
      timestamp: json['timestamp'] != null
          ? DateTime.parse(json['timestamp'])
          : DateTime.now(),
      isRead: json['isRead'] ?? false,
      extras: json['extras'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'conversationId': conversationId,
      'conversationName': conversationName,
      'senderId': senderId,
      'senderName': senderName,
      'senderAvatar': senderAvatar,
      'type': type,
      'content': content,
      'timestamp': timestamp.toIso8601String(),
      'isRead': isRead,
      'extras': extras,
    };
  }

  bool get isText => type == 'text';
  bool get isImage => type == 'image';
  bool get isVoice => type == 'voice';
  bool get isVideo => type == 'video';
  bool get isFile => type == 'file';
  bool get isLocation => type == 'location';

  String get displayContent {
    switch (type) {
      case 'image':
        return '[图片]';
      case 'voice':
        return '[语音消息]';
      case 'video':
        return '[视频]';
      case 'file':
        return '[文件] ${extras?['fileName'] ?? ''}';
      case 'location':
        return '[位置] ${extras?['address'] ?? ''}';
      default:
        return content;
    }
  }

  Message copyWith({
    String? id,
    String? conversationId,
    String? conversationName,
    String? senderId,
    String? senderName,
    String? senderAvatar,
    String? type,
    String? content,
    DateTime? timestamp,
    bool? isRead,
    Map<String, dynamic>? extras,
  }) {
    return Message(
      id: id ?? this.id,
      conversationId: conversationId ?? this.conversationId,
      conversationName: conversationName ?? this.conversationName,
      senderId: senderId ?? this.senderId,
      senderName: senderName ?? this.senderName,
      senderAvatar: senderAvatar ?? this.senderAvatar,
      type: type ?? this.type,
      content: content ?? this.content,
      timestamp: timestamp ?? this.timestamp,
      isRead: isRead ?? this.isRead,
      extras: extras ?? this.extras,
    );
  }

  @override
  String toString() {
    return 'Message(id: $id, type: $type, content: ${content.substring(0, content.length > 20 ? 20 : content.length)}...)';
  }
}
