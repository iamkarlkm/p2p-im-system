/// 消息模型 - 功能#9 基础IM客户端SDK
/// 时间: 2026-04-01 09:24
class MessageModel {
  final String id;
  final String conversationId;
  final String senderId;
  final String senderName;
  final String senderAvatar;
  final String content;
  final String messageType; // TEXT, IMAGE, VOICE, VIDEO, FILE
  final int timestamp;
  final MessageStatus status;
  final Map<String, dynamic>? extra;

  MessageModel({
    required this.id,
    required this.conversationId,
    required this.senderId,
    required this.senderName,
    this.senderAvatar = '',
    required this.content,
    this.messageType = 'TEXT',
    required this.timestamp,
    this.status = MessageStatus.sending,
    this.extra,
  });

  /// 从JSON解析
  factory MessageModel.fromJson(Map<String, dynamic> json) {
    return MessageModel(
      id: json['id'] ?? '',
      conversationId: json['conversationId'] ?? '',
      senderId: json['senderId'] ?? '',
      senderName: json['senderName'] ?? '',
      senderAvatar: json['senderAvatar'] ?? '',
      content: json['content'] ?? '',
      messageType: json['messageType'] ?? 'TEXT',
      timestamp: json['timestamp'] ?? DateTime.now().millisecondsSinceEpoch,
      status: MessageStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => MessageStatus.sending,
      ),
      extra: json['extra'],
    );
  }

  /// 转换为JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'conversationId': conversationId,
      'senderId': senderId,
      'senderName': senderName,
      'senderAvatar': senderAvatar,
      'content': content,
      'messageType': messageType,
      'timestamp': timestamp,
      'status': status.name,
      'extra': extra,
    };
  }

  /// 复制并修改
  MessageModel copyWith({
    String? id,
    String? conversationId,
    String? senderId,
    String? senderName,
    String? senderAvatar,
    String? content,
    String? messageType,
    int? timestamp,
    MessageStatus? status,
    Map<String, dynamic>? extra,
  }) {
    return MessageModel(
      id: id ?? this.id,
      conversationId: conversationId ?? this.conversationId,
      senderId: senderId ?? this.senderId,
      senderName: senderName ?? this.senderName,
      senderAvatar: senderAvatar ?? this.senderAvatar,
      content: content ?? this.content,
      messageType: messageType ?? this.messageType,
      timestamp: timestamp ?? this.timestamp,
      status: status ?? this.status,
      extra: extra ?? this.extra,
    );
  }

  /// 获取显示时间
  String get displayTime {
    final date = DateTime.fromMillisecondsSinceEpoch(timestamp);
    final now = DateTime.now();
    
    if (date.year == now.year && date.month == now.month && date.day == now.day) {
      return '${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
    } else if (date.year == now.year && date.month == now.month && date.day == now.day - 1) {
      return '昨天 ${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
    } else {
      return '${date.month}/${date.day}';
    }
  }

  /// 是否是自己发送的
  bool isSelf(String currentUserId) => senderId == currentUserId;
}

/// 消息状态
enum MessageStatus {
  sending,    // 发送中
  sent,       // 已发送
  delivered,  // 已送达
  read,       // 已读
  failed,     // 发送失败
}

/// 会话模型
class ConversationModel {
  final String id;
  final String type; // SINGLE, GROUP
  final String name;
  final String avatar;
  final String lastMessage;
  final String lastMessageType;
  final int lastMessageTime;
  final int unreadCount;
  final List<String> memberIds;

  ConversationModel({
    required this.id,
    this.type = 'SINGLE',
    required this.name,
    this.avatar = '',
    this.lastMessage = '',
    this.lastMessageType = 'TEXT',
    this.lastMessageTime = 0,
    this.unreadCount = 0,
    this.memberIds = const [],
  });

  factory ConversationModel.fromJson(Map<String, dynamic> json) {
    return ConversationModel(
      id: json['id'] ?? '',
      type: json['type'] ?? 'SINGLE',
      name: json['name'] ?? '',
      avatar: json['avatar'] ?? '',
      lastMessage: json['lastMessage'] ?? '',
      lastMessageType: json['lastMessageType'] ?? 'TEXT',
      lastMessageTime: json['lastMessageTime'] ?? 0,
      unreadCount: json['unreadCount'] ?? 0,
      memberIds: List<String>.from(json['memberIds'] ?? []),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'type': type,
      'name': name,
      'avatar': avatar,
      'lastMessage': lastMessage,
      'lastMessageType': lastMessageType,
      'lastMessageTime': lastMessageTime,
      'unreadCount': unreadCount,
      'memberIds': memberIds,
    };
  }
}
