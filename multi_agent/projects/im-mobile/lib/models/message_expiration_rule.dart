/**
 * 消息过期规则模型
 */
class MessageExpirationRule {
  final int id;
  final int userId;
  final String? conversationId;
  final String? conversationType;
  final String expirationType; // READ_AFTER, SELF_DESTRUCT, TIME_BASED, GLOBAL
  final DateTime? expireTime;
  final int? relativeSeconds;
  final bool active;
  final bool globalDefault;
  final String messageTypeFilter; // TEXT, IMAGE, FILE, ALL
  final int? readDestroySeconds;
  final bool preExpireNotice;
  final int? preExpireNoticeSeconds;
  final DateTime createdAt;
  final DateTime updatedAt;
  final int? remainingSeconds;

  MessageExpirationRule({
    required this.id,
    required this.userId,
    this.conversationId,
    this.conversationType,
    required this.expirationType,
    this.expireTime,
    this.relativeSeconds,
    required this.active,
    required this.globalDefault,
    required this.messageTypeFilter,
    this.readDestroySeconds,
    required this.preExpireNotice,
    this.preExpireNoticeSeconds,
    required this.createdAt,
    required this.updatedAt,
    this.remainingSeconds,
  });

  factory MessageExpirationRule.fromJson(Map<String, dynamic> json) {
    return MessageExpirationRule(
      id: json['id'],
      userId: json['userId'],
      conversationId: json['conversationId'],
      conversationType: json['conversationType'],
      expirationType: json['expirationType'],
      expireTime: json['expireTime'] != null ? DateTime.parse(json['expireTime']) : null,
      relativeSeconds: json['relativeSeconds'],
      active: json['active'] ?? true,
      globalDefault: json['globalDefault'] ?? false,
      messageTypeFilter: json['messageTypeFilter'] ?? 'ALL',
      readDestroySeconds: json['readDestroySeconds'],
      preExpireNotice: json['preExpireNotice'] ?? false,
      preExpireNoticeSeconds: json['preExpireNoticeSeconds'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
      remainingSeconds: json['remainingSeconds'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'conversationId': conversationId,
      'conversationType': conversationType,
      'expirationType': expirationType,
      'expireTime': expireTime?.toIso8601String(),
      'relativeSeconds': relativeSeconds,
      'active': active,
      'globalDefault': globalDefault,
      'messageTypeFilter': messageTypeFilter,
      'readDestroySeconds': readDestroySeconds,
      'preExpireNotice': preExpireNotice,
      'preExpireNoticeSeconds': preExpireNoticeSeconds,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'remainingSeconds': remainingSeconds,
    };
  }

  String get typeLabel {
    switch (expirationType) {
      case 'READ_AFTER':
      case 'SELF_DESTRUCT':
        return '阅后即焚';
      case 'TIME_BASED':
        return '定时过期';
      case 'GLOBAL':
        return '全局默认';
      default:
        return expirationType;
    }
  }

  String get timeLabel {
    if (readDestroySeconds != null) return '${readDestroySeconds}秒后销毁';
    if (relativeSeconds != null) {
      if (relativeSeconds! < 60) return '${relativeSeconds}秒';
      if (relativeSeconds! < 3600) return '${(relativeSeconds! / 60).floor()}分钟';
      if (relativeSeconds! < 86400) return '${(relativeSeconds! / 3600).floor()}小时';
      return '${(relativeSeconds! / 86400).floor()}天';
    }
    return '永久';
  }
}

class ExpirationRuleRequest {
  final int? id;
  final String? conversationId;
  final String? conversationType;
  final String expirationType;
  final DateTime? expireTime;
  final int? relativeSeconds;
  final bool? active;
  final String messageTypeFilter;
  final int? readDestroySeconds;
  final bool preExpireNotice;
  final int? preExpireNoticeSeconds;

  ExpirationRuleRequest({
    this.id,
    this.conversationId,
    this.conversationType,
    required this.expirationType,
    this.expireTime,
    this.relativeSeconds,
    this.active,
    this.messageTypeFilter = 'ALL',
    this.readDestroySeconds,
    this.preExpireNotice = false,
    this.preExpireNoticeSeconds,
  });

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      if (conversationId != null) 'conversationId': conversationId,
      if (conversationType != null) 'conversationType': conversationType,
      'expirationType': expirationType,
      if (expireTime != null) 'expireTime': expireTime!.toIso8601String(),
      if (relativeSeconds != null) 'relativeSeconds': relativeSeconds,
      if (active != null) 'active': active,
      'messageTypeFilter': messageTypeFilter,
      if (readDestroySeconds != null) 'readDestroySeconds': readDestroySeconds,
      'preExpireNotice': preExpireNotice,
      if (preExpireNoticeSeconds != null) 'preExpireNoticeSeconds': preExpireNoticeSeconds,
    };
  }
}
