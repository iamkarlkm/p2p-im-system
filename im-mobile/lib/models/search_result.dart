import 'message.dart';

class SearchResult {
  final Message message;
  final String highlightedContent;
  final double relevanceScore;
  final String? matchedField;

  SearchResult({
    required this.message,
    required this.highlightedContent,
    required this.relevanceScore,
    this.matchedField,
  });

  factory SearchResult.fromJson(Map<String, dynamic> json) {
    return SearchResult(
      message: Message.fromJson(json['message'] ?? {}),
      highlightedContent: json['highlightedContent'] ?? '',
      relevanceScore: (json['relevanceScore'] ?? 0.0).toDouble(),
      matchedField: json['matchedField'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'message': message.toJson(),
      'highlightedContent': highlightedContent,
      'relevanceScore': relevanceScore,
      'matchedField': matchedField,
    };
  }

  String get formattedTimestamp {
    final now = DateTime.now();
    final msgTime = message.timestamp;
    final diff = now.difference(msgTime);

    if (diff.inDays == 0) {
      return '${msgTime.hour.toString().padLeft(2, '0')}:${msgTime.minute.toString().padLeft(2, '0')}';
    } else if (diff.inDays == 1) {
      return '昨天';
    } else if (diff.inDays < 7) {
      final weekdays = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];
      return weekdays[msgTime.weekday - 1];
    } else {
      return '${msgTime.month}/${msgTime.day}';
    }
  }

  String get senderName {
    return message.senderName ?? '未知用户';
  }

  String get conversationName {
    return message.conversationName ?? '未知会话';
  }

  bool get isMatchInContent => matchedField == 'content';
  bool get isMatchInSender => matchedField == 'senderName';
}
