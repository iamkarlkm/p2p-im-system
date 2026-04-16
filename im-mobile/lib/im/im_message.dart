/// IM消息实体
class IMMessage {
  final String id;
  final String from;
  final String to;
  final MessageType type;
  final ContentType contentType;
  final String content;
  final int timestamp;
  final Map<String, dynamic>? extras;

  IMMessage({
    required this.id,
    required this.from,
    required this.to,
    required this.type,
    required this.contentType,
    required this.content,
    required this.timestamp,
    this.extras,
  });

  factory IMMessage.fromJson(Map<String, dynamic> json) {
    return IMMessage(
      id: json['id'] ?? '',
      from: json['from'] ?? '',
      to: json['to'] ?? '',
      type: MessageType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => MessageType.single,
      ),
      contentType: ContentType.values.firstWhere(
        (e) => e.name == json['contentType'],
        orElse: () => ContentType.text,
      ),
      content: json['content'] ?? '',
      timestamp: json['timestamp'] ?? 0,
      extras: json['extras'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'from': from,
      'to': to,
      'type': type.name,
      'contentType': contentType.name,
      'content': content,
      'timestamp': timestamp,
      'extras': extras,
    };
  }

  /// 是否为群消息
  bool get isGroup => type == MessageType.group;
  
  /// 消息发送时间
  DateTime get sendTime => DateTime.fromMillisecondsSinceEpoch(timestamp);
}

/// 消息类型
enum MessageType {
  single,    // 单聊
  group,     // 群聊
  system,    // 系统消息
}

/// 内容类型
enum ContentType {
  text,      // 文本
  image,     // 图片
  voice,     // 语音
  video,     // 视频
  file,      // 文件
  location,  // 位置
  card,      // 名片
  custom,    // 自定义
}
