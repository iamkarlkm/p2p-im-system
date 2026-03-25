class DndSettings {
  final int? id;
  final int? userId;
  final bool enabled;
  final String startTime;
  final String endTime;
  final String timezone;
  final String repeatDays;
  final bool allowMentions;
  final bool allowStarred;
  final String? customMessage;
  final DateTime? createdAt;
  final DateTime? updatedAt;

  DndSettings({
    this.id,
    this.userId,
    required this.enabled,
    required this.startTime,
    required this.endTime,
    required this.timezone,
    required this.repeatDays,
    required this.allowMentions,
    required this.allowStarred,
    this.customMessage,
    this.createdAt,
    this.updatedAt,
  });

  factory DndSettings.fromJson(Map<String, dynamic> json) {
    return DndSettings(
      id: json['id'] as int?,
      userId: json['userId'] as int?,
      enabled: json['enabled'] as bool? ?? false,
      startTime: json['startTime'] as String? ?? '22:00',
      endTime: json['endTime'] as String? ?? '08:00',
      timezone: json['timezone'] as String? ?? 'Asia/Shanghai',
      repeatDays: json['repeatDays'] as String? ?? '1,2,3,4,5,6,7',
      allowMentions: json['allowMentions'] as bool? ?? true,
      allowStarred: json['allowStarred'] as bool? ?? true,
      customMessage: json['customMessage'] as String?,
      createdAt: json['createdAt'] != null ? DateTime.tryParse(json['createdAt']) : null,
      updatedAt: json['updatedAt'] != null ? DateTime.tryParse(json['updatedAt']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      if (id != null) 'id': id,
      if (userId != null) 'userId': userId,
      'enabled': enabled,
      'startTime': startTime,
      'endTime': endTime,
      'timezone': timezone,
      'repeatDays': repeatDays,
      'allowMentions': allowMentions,
      'allowStarred': allowStarred,
      if (customMessage != null) 'customMessage': customMessage,
    };
  }

  DndSettings copyWith({
    int? id,
    int? userId,
    bool? enabled,
    String? startTime,
    String? endTime,
    String? timezone,
    String? repeatDays,
    bool? allowMentions,
    bool? allowStarred,
    String? customMessage,
  }) {
    return DndSettings(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      enabled: enabled ?? this.enabled,
      startTime: startTime ?? this.startTime,
      endTime: endTime ?? this.endTime,
      timezone: timezone ?? this.timezone,
      repeatDays: repeatDays ?? this.repeatDays,
      allowMentions: allowMentions ?? this.allowMentions,
      allowStarred: allowStarred ?? this.allowStarred,
      customMessage: customMessage ?? this.customMessage,
    );
  }
}

class DndStatus {
  final bool inDndPeriod;
  final bool allowMention;
  final bool allowStarred;

  DndStatus({
    required this.inDndPeriod,
    required this.allowMention,
    required this.allowStarred,
  });

  factory DndStatus.fromJson(Map<String, dynamic> json) {
    return DndStatus(
      inDndPeriod: json['inDndPeriod'] as bool? ?? false,
      allowMention: json['allowMention'] as bool? ?? true,
      allowStarred: json['allowStarred'] as bool? ?? true,
    );
  }
}

DndSettings get defaultDndSettings => DndSettings(
      enabled: false,
      startTime: '22:00',
      endTime: '08:00',
      timezone: 'Asia/Shanghai',
      repeatDays: '1,2,3,4,5,6,7',
      allowMentions: true,
      allowStarred: true,
    );
