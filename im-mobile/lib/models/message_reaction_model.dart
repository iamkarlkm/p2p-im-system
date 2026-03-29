import 'package:flutter/foundation.dart';

/// 反应类型枚举
enum ReactionType {
  emoji,
  customEmoji,
  sticker,
  shortcut,
}

/// 消息表情回应模型
class MessageReaction {
  final int? id;
  final int messageId;
  final int userId;
  final String? userName;
  final String? userAvatar;
  final int conversationId;
  final String emojiCode;
  final String? emojiDescription;
  final int? skinTone;
  final ReactionType reactionType;
  final bool isAnonymous;
  final String? clientMessageId;
  final DateTime createdAt;
  final DateTime? updatedAt;

  MessageReaction({
    this.id,
    required this.messageId,
    required this.userId,
    this.userName,
    this.userAvatar,
    required this.conversationId,
    required this.emojiCode,
    this.emojiDescription,
    this.skinTone,
    this.reactionType = ReactionType.emoji,
    this.isAnonymous = false,
    this.clientMessageId,
    required this.createdAt,
    this.updatedAt,
  });

  factory MessageReaction.fromJson(Map<String, dynamic> json) {
    return MessageReaction(
      id: json['id'] as int?,
      messageId: json['messageId'] as int,
      userId: json['userId'] as int,
      userName: json['userName'] as String?,
      userAvatar: json['userAvatar'] as String?,
      conversationId: json['conversationId'] as int,
      emojiCode: json['emojiCode'] as String,
      emojiDescription: json['emojiDescription'] as String?,
      skinTone: json['skinTone'] as int?,
      reactionType: ReactionType.values.firstWhere(
        (e) => e.name.toUpperCase() == (json['reactionType'] as String? ?? 'EMOJI').toUpperCase(),
        orElse: () => ReactionType.emoji,
      ),
      isAnonymous: json['isAnonymous'] as bool? ?? false,
      clientMessageId: json['clientMessageId'] as String?,
      createdAt: DateTime.parse(json['createdAt'] as String),
      updatedAt: json['updatedAt'] != null ? DateTime.parse(json['updatedAt'] as String) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'messageId': messageId,
      'userId': userId,
      'userName': userName,
      'userAvatar': userAvatar,
      'conversationId': conversationId,
      'emojiCode': emojiCode,
      'emojiDescription': emojiDescription,
      'skinTone': skinTone,
      'reactionType': reactionType.name.toUpperCase(),
      'isAnonymous': isAnonymous,
      'clientMessageId': clientMessageId,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt?.toIso8601String(),
    };
  }

  MessageReaction copyWith({
    int? id,
    int? messageId,
    int? userId,
    String? userName,
    String? userAvatar,
    int? conversationId,
    String? emojiCode,
    String? emojiDescription,
    int? skinTone,
    ReactionType? reactionType,
    bool? isAnonymous,
    String? clientMessageId,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return MessageReaction(
      id: id ?? this.id,
      messageId: messageId ?? this.messageId,
      userId: userId ?? this.userId,
      userName: userName ?? this.userName,
      userAvatar: userAvatar ?? this.userAvatar,
      conversationId: conversationId ?? this.conversationId,
      emojiCode: emojiCode ?? this.emojiCode,
      emojiDescription: emojiDescription ?? this.emojiDescription,
      skinTone: skinTone ?? this.skinTone,
      reactionType: reactionType ?? this.reactionType,
      isAnonymous: isAnonymous ?? this.isAnonymous,
      clientMessageId: clientMessageId ?? this.clientMessageId,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  @override
  String toString() {
    return 'MessageReaction(id: $id, messageId: $messageId, userId: $userId, emojiCode: $emojiCode)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is MessageReaction && other.id == id;
  }

  @override
  int get hashCode => id.hashCode;
}

/// 表情计数模型
class EmojiCount {
  final String emojiCode;
  final String? emojiDescription;
  final int count;
  final List<int> userIds;
  final bool isCurrentUserIncluded;

  EmojiCount({
    required this.emojiCode,
    this.emojiDescription,
    required this.count,
    required this.userIds,
    this.isCurrentUserIncluded = false,
  });

  factory EmojiCount.fromJson(Map<String, dynamic> json) {
    return EmojiCount(
      emojiCode: json['emojiCode'] as String,
      emojiDescription: json['emojiDescription'] as String?,
      count: (json['count'] as num).toInt(),
      userIds: (json['userIds'] as List<dynamic>?)?.map((e) => (e as num).toInt()).toList() ?? [],
      isCurrentUserIncluded: json['isCurrentUserIncluded'] as bool? ?? false,
    );
  }
}

/// 反应用户模型
class ReactionUser {
  final int userId;
  final String userName;
  final String? userAvatar;
  final String emojiCode;
  final DateTime reactedAt;

  ReactionUser({
    required this.userId,
    required this.userName,
    this.userAvatar,
    required this.emojiCode,
    required this.reactedAt,
  });

  factory ReactionUser.fromJson(Map<String, dynamic> json) {
    return ReactionUser(
      userId: json['userId'] as int,
      userName: json['userName'] as String,
      userAvatar: json['userAvatar'] as String?,
      emojiCode: json['emojiCode'] as String,
      reactedAt: DateTime.parse(json['reactedAt'] as String),
    );
  }
}

/// 表情回应汇总模型
class ReactionSummary {
  final int messageId;
  final int conversationId;
  final int totalReactions;
  final int uniqueEmojiCount;
  final List<EmojiCount> emojiCounts;
  final List<ReactionUser>? recentUsers;
  final bool hasCurrentUserReacted;
  final String? currentUserEmoji;

  ReactionSummary({
    required this.messageId,
    required this.conversationId,
    required this.totalReactions,
    required this.uniqueEmojiCount,
    required this.emojiCounts,
    this.recentUsers,
    required this.hasCurrentUserReacted,
    this.currentUserEmoji,
  });

  factory ReactionSummary.fromJson(Map<String, dynamic> json) {
    return ReactionSummary(
      messageId: json['messageId'] as int,
      conversationId: json['conversationId'] as int,
      totalReactions: (json['totalReactions'] as num).toInt(),
      uniqueEmojiCount: (json['uniqueEmojiCount'] as num).toInt(),
      emojiCounts: (json['emojiCounts'] as List<dynamic>)
          .map((e) => EmojiCount.fromJson(e as Map<String, dynamic>))
          .toList(),
      recentUsers: (json['recentUsers'] as List<dynamic>?)
          ?.map((e) => ReactionUser.fromJson(e as Map<String, dynamic>))
          .toList(),
      hasCurrentUserReacted: json['hasCurrentUserReacted'] as bool? ?? false,
      currentUserEmoji: json['currentUserEmoji'] as String?,
    );
  }

  /// 获取最热门的表情
  List<EmojiCount> getTopEmojis({int limit = 3}) {
    return emojiCounts.take(limit).toList();
  }
}

/// 添加反应请求
class AddReactionRequest {
  final int messageId;
  final int userId;
  final int conversationId;
  final String emojiCode;
  final String? emojiDescription;
  final int? skinTone;
  final ReactionType reactionType;
  final bool isAnonymous;
  final String? clientMessageId;

  AddReactionRequest({
    required this.messageId,
    required this.userId,
    required this.conversationId,
    required this.emojiCode,
    this.emojiDescription,
    this.skinTone,
    this.reactionType = ReactionType.emoji,
    this.isAnonymous = false,
    this.clientMessageId,
  });

  Map<String, dynamic> toJson() {
    return {
      'messageId': messageId,
      'userId': userId,
      'conversationId': conversationId,
      'emojiCode': emojiCode,
      'emojiDescription': emojiDescription,
      'skinTone': skinTone,
      'reactionType': reactionType.name.toUpperCase(),
      'isAnonymous': isAnonymous,
      'clientMessageId': clientMessageId,
    };
  }
}

/// 常用表情
const List<String> commonEmojis = [
  '👍', '❤️', '😂', '😮', '😢', '😡', '🎉', '🔥',
  '👏', '🙏', '🤔', '👀', '✅', '❌', '⭐', '💯',
];

/// 表情分类
class EmojiCategory {
  final String id;
  final String name;
  final String icon;
  final List<String> emojiCodes;

  EmojiCategory({
    required this.id,
    required this.name,
    required this.icon,
    required this.emojiCodes,
  });
}

/// 默认表情分类
final List<EmojiCategory> defaultEmojiCategories = [
  EmojiCategory(
    id: 'recent',
    name: '最近使用',
    icon: '🕐',
    emojiCodes: [],
  ),
  EmojiCategory(
    id: 'smileys',
    name: '表情',
    icon: '😀',
    emojiCodes: ['😀', '😃', '😄', '😁', '😅', '😂', '🤣', '😊'],
  ),
  EmojiCategory(
    id: 'people',
    name: '人物',
    icon: '👍',
    emojiCodes: ['👍', '👎', '👏', '🙌', '🤝', '👊', '✊', '👌'],
  ),
  EmojiCategory(
    id: 'symbols',
    name: '符号',
    icon: '❤️',
    emojiCodes: ['❤️', '🧡', '💛', '💚', '💙', '💜', '✅', '❌'],
  ),
];
