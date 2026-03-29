import 'package:flutter/material.dart';

enum OnlineStatus {
  online,
  away,
  busy,
  offline,
  invisible,
}

class UserStatus {
  final String userId;
  final String status;
  final String? customStatus;
  final String? customStatusEmoji;
  final DateTime lastSeen;
  final DateTime? lastActive;
  final String? deviceInfo;
  final String? clientVersion;

  UserStatus({
    required this.userId,
    required this.status,
    this.customStatus,
    this.customStatusEmoji,
    required this.lastSeen,
    this.lastActive,
    this.deviceInfo,
    this.clientVersion,
  });

  factory UserStatus.fromJson(Map<String, dynamic> json) {
    return UserStatus(
      userId: json['userId'] ?? json['id'] ?? '',
      status: json['status'] ?? 'offline',
      customStatus: json['customStatus'],
      customStatusEmoji: json['customStatusEmoji'],
      lastSeen: json['lastSeen'] != null
          ? DateTime.parse(json['lastSeen'])
          : DateTime.now(),
      lastActive: json['lastActive'] != null
          ? DateTime.parse(json['lastActive'])
          : null,
      deviceInfo: json['deviceInfo'],
      clientVersion: json['clientVersion'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'status': status,
      'customStatus': customStatus,
      'customStatusEmoji': customStatusEmoji,
      'lastSeen': lastSeen.toIso8601String(),
      'lastActive': lastActive?.toIso8601String(),
      'deviceInfo': deviceInfo,
      'clientVersion': clientVersion,
    };
  }

  bool get isOnline => status == 'online';
  bool get isAway => status == 'away';
  bool get isBusy => status == 'busy';
  bool get isOffline => status == 'offline' || status == 'invisible';

  OnlineStatus get onlineStatus {
    switch (status) {
      case 'online':
        return OnlineStatus.online;
      case 'away':
        return OnlineStatus.away;
      case 'busy':
        return OnlineStatus.busy;
      case 'invisible':
        return OnlineStatus.invisible;
      default:
        return OnlineStatus.offline;
    }
  }

  Color get statusColor {
    switch (status) {
      case 'online':
        return Colors.green;
      case 'away':
        return Colors.orange;
      case 'busy':
        return Colors.red;
      case 'invisible':
        return Colors.grey;
      default:
        return Colors.grey.shade400;
    }
  }

  String get displayStatus {
    if (customStatus != null && customStatus!.isNotEmpty) {
      return '$customStatusEmoji ${customStatus!}';
    }
    switch (status) {
      case 'online':
        return '在线';
      case 'away':
        return '离开';
      case 'busy':
        return '忙碌';
      case 'invisible':
        return '隐身';
      default:
        return '离线';
    }
  }

  String get shortStatus {
    switch (status) {
      case 'online':
        return '在线';
      case 'away':
        return '离开';
      case 'busy':
        return '忙碌';
      case 'invisible':
        return '隐身';
      default:
        return '离线';
    }
  }

  String get lastSeenText {
    if (isOnline) return '当前在线';
    
    final now = DateTime.now();
    final diff = now.difference(lastSeen);
    
    if (diff.inMinutes < 1) {
      return '刚刚';
    } else if (diff.inMinutes < 60) {
      return '${diff.inMinutes}分钟前';
    } else if (diff.inHours < 24) {
      return '${diff.inHours}小时前';
    } else if (diff.inDays < 7) {
      return '${diff.inDays}天前';
    } else {
      return '${lastSeen.month}月${lastSeen.day}日';
    }
  }

  UserStatus copyWith({
    String? userId,
    String? status,
    String? customStatus,
    String? customStatusEmoji,
    DateTime? lastSeen,
    DateTime? lastActive,
    String? deviceInfo,
    String? clientVersion,
  }) {
    return UserStatus(
      userId: userId ?? this.userId,
      status: status ?? this.status,
      customStatus: customStatus ?? this.customStatus,
      customStatusEmoji: customStatusEmoji ?? this.customStatusEmoji,
      lastSeen: lastSeen ?? this.lastSeen,
      lastActive: lastActive ?? this.lastActive,
      deviceInfo: deviceInfo ?? this.deviceInfo,
      clientVersion: clientVersion ?? this.clientVersion,
    );
  }

  @override
  String toString() {
    return 'UserStatus(userId: $userId, status: $status, customStatus: $customStatus)';
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) return true;
    return other is UserStatus &&
        other.userId == userId &&
        other.status == status &&
        other.customStatus == customStatus;
  }

  @override
  int get hashCode => Object.hash(userId, status, customStatus);
}
