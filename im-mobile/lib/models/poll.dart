/**
 * 投票模型 (Poll Model)
 * Flutter Mobile 实现
 */

class Poll {
  final String pollId;
  final String creatorId;
  final String groupId;
  final String conversationId;
  final String question;
  final List<PollOption> options;
  final bool anonymous;
  final bool multiSelect;
  final DateTime? deadline;
  final PollStatus status;
  final int totalVotes;
  final int totalParticipants;
  final DateTime createdAt;
  final DateTime updatedAt;
  final bool allowOptionAdd;
  final String? messageId;
  final bool hasVoted;
  final List<String> votedOptionIds;
  final int? remainingSeconds;

  Poll({
    required this.pollId,
    required this.creatorId,
    required this.groupId,
    required this.conversationId,
    required this.question,
    required this.options,
    this.anonymous = false,
    this.multiSelect = false,
    this.deadline,
    this.status = PollStatus.active,
    this.totalVotes = 0,
    this.totalParticipants = 0,
    required this.createdAt,
    required this.updatedAt,
    this.allowOptionAdd = true,
    this.messageId,
    this.hasVoted = false,
    this.votedOptionIds = const [],
    this.remainingSeconds,
  });

  bool get isActive => status == PollStatus.active && !isExpired;

  bool get isExpired {
    if (deadline == null) return false;
    return DateTime.now().isAfter(deadline!);
  }

  factory Poll.fromJson(Map<String, dynamic> json) {
    return Poll(
      pollId: json['pollId'] ?? '',
      creatorId: json['creatorId'] ?? '',
      groupId: json['groupId'] ?? '',
      conversationId: json['conversationId'] ?? '',
      question: json['question'] ?? '',
      options: (json['options'] as List<dynamic>?)
              ?.map((o) => PollOption.fromJson(o))
              .toList() ??
          [],
      anonymous: json['anonymous'] ?? false,
      multiSelect: json['multiSelect'] ?? false,
      deadline: json['deadline'] != null
          ? DateTime.tryParse(json['deadline'])
          : null,
      status: PollStatus.fromString(json['status']),
      totalVotes: json['totalVotes'] ?? 0,
      totalParticipants: json['totalParticipants'] ?? 0,
      createdAt: DateTime.tryParse(json['createdAt'] ?? '') ?? DateTime.now(),
      updatedAt: DateTime.tryParse(json['updatedAt'] ?? '') ?? DateTime.now(),
      allowOptionAdd: json['allowOptionAdd'] ?? true,
      messageId: json['messageId'],
      hasVoted: json['hasVoted'] ?? false,
      votedOptionIds:
          (json['votedOptionIds'] as List<dynamic>?)?.cast<String>() ?? [],
      remainingSeconds: json['remainingSeconds'],
    );
  }

  Map<String, dynamic> toJson() => {
        'pollId': pollId,
        'creatorId': creatorId,
        'groupId': groupId,
        'conversationId': conversationId,
        'question': question,
        'options': options.map((o) => o.toJson()).toList(),
        'anonymous': anonymous,
        'multiSelect': multiSelect,
        'deadline': deadline?.toIso8601String(),
        'status': status.name.toUpperCase(),
        'totalVotes': totalVotes,
        'totalParticipants': totalParticipants,
        'createdAt': createdAt.toIso8601String(),
        'updatedAt': updatedAt.toIso8601String(),
        'allowOptionAdd': allowOptionAdd,
        'messageId': messageId,
        'hasVoted': hasVoted,
        'votedOptionIds': votedOptionIds,
        'remainingSeconds': remainingSeconds,
      };

  Poll copyWith({
    String? pollId,
    String? creatorId,
    String? groupId,
    String? conversationId,
    String? question,
    List<PollOption>? options,
    bool? anonymous,
    bool? multiSelect,
    DateTime? deadline,
    PollStatus? status,
    int? totalVotes,
    int? totalParticipants,
    DateTime? createdAt,
    DateTime? updatedAt,
    bool? allowOptionAdd,
    String? messageId,
    bool? hasVoted,
    List<String>? votedOptionIds,
    int? remainingSeconds,
  }) {
    return Poll(
      pollId: pollId ?? this.pollId,
      creatorId: creatorId ?? this.creatorId,
      groupId: groupId ?? this.groupId,
      conversationId: conversationId ?? this.conversationId,
      question: question ?? this.question,
      options: options ?? this.options,
      anonymous: anonymous ?? this.anonymous,
      multiSelect: multiSelect ?? this.multiSelect,
      deadline: deadline ?? this.deadline,
      status: status ?? this.status,
      totalVotes: totalVotes ?? this.totalVotes,
      totalParticipants:
          totalParticipants ?? this.totalParticipants,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      allowOptionAdd: allowOptionAdd ?? this.allowOptionAdd,
      messageId: messageId ?? this.messageId,
      hasVoted: hasVoted ?? this.hasVoted,
      votedOptionIds: votedOptionIds ?? this.votedOptionIds,
      remainingSeconds: remainingSeconds ?? this.remainingSeconds,
    );
  }
}

class PollOption {
  final String optionId;
  final String optionText;
  final int voteCount;
  final double percentage;
  final bool hasVoted;
  final List<String>? voterIds;

  PollOption({
    required this.optionId,
    required this.optionText,
    this.voteCount = 0,
    this.percentage = 0.0,
    this.hasVoted = false,
    this.voterIds,
  });

  factory PollOption.fromJson(Map<String, dynamic> json) {
    return PollOption(
      optionId: json['optionId'] ?? '',
      optionText: json['optionText'] ?? '',
      voteCount: json['voteCount'] ?? 0,
      percentage: (json['percentage'] ?? 0.0).toDouble(),
      hasVoted: json['hasVoted'] ?? false,
      voterIds: (json['voterIds'] as List<dynamic>?)?.cast<String>(),
    );
  }

  Map<String, dynamic> toJson() => {
        'optionId': optionId,
        'optionText': optionText,
        'voteCount': voteCount,
        'percentage': percentage,
        'hasVoted': hasVoted,
        'voterIds': voterIds,
      };
}

enum PollStatus {
  active,
  closed,
  cancelled;

  static PollStatus fromString(String? s) {
    switch (s?.toUpperCase()) {
      case 'CLOSED':
        return PollStatus.closed;
      case 'CANCELLED':
        return PollStatus.cancelled;
      default:
        return PollStatus.active;
    }
  }

  String get label {
    switch (this) {
      case PollStatus.active:
        return '进行中';
      case PollStatus.closed:
        return '已结束';
      case PollStatus.cancelled:
        return '已取消';
    }
  }
}

class CreatePollRequest {
  final String creatorId;
  final String groupId;
  final String? conversationId;
  final String question;
  final List<String> optionTexts;
  final bool anonymous;
  final bool multiSelect;
  final DateTime? deadline;
  final bool allowOptionAdd;
  final String? messageId;

  CreatePollRequest({
    required this.creatorId,
    required this.groupId,
    this.conversationId,
    required this.question,
    required this.optionTexts,
    this.anonymous = false,
    this.multiSelect = false,
    this.deadline,
    this.allowOptionAdd = true,
    this.messageId,
  });

  Map<String, dynamic> toJson() => {
        'creatorId': creatorId,
        'groupId': groupId,
        'conversationId': conversationId,
        'question': question,
        'optionTexts': optionTexts,
        'anonymous': anonymous,
        'multiSelect': multiSelect,
        'deadline': deadline?.toIso8601String(),
        'allowOptionAdd': allowOptionAdd,
        'messageId': messageId,
      };
}
