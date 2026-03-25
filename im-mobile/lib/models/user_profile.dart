/**
 * 用户资料数据模型
 */

class UserProfile {
  final int userId;
  final String nickname;
  final String avatarUrl;
  final String bio;
  final int gender; // 0-未知, 1-男, 2-女
  final DateTime? birthday;
  final String? email;
  final String? phone;
  final OnlineStatus onlineStatus;
  final String? statusText;
  final String? country;
  final String? city;
  final String? language;
  final String? timezone;
  final DateTime? updatedAt;

  UserProfile({
    required this.userId,
    required this.nickname,
    this.avatarUrl = '',
    this.bio = '',
    this.gender = 0,
    this.birthday,
    this.email,
    this.phone,
    this.onlineStatus = OnlineStatus.offline,
    this.statusText,
    this.country,
    this.city,
    this.language,
    this.timezone,
    this.updatedAt,
  });

  factory UserProfile.fromJson(Map<String, dynamic> json) {
    return UserProfile(
      userId: json['userId'] ?? 0,
      nickname: json['nickname'] ?? '未知用户',
      avatarUrl: json['avatarUrl'] ?? '',
      bio: json['bio'] ?? '',
      gender: json['gender'] ?? 0,
      birthday: json['birthday'] != null
          ? DateTime.tryParse(json['birthday'])
          : null,
      email: json['email'],
      phone: json['phone'],
      onlineStatus: OnlineStatus.fromString(json['onlineStatus'] ?? 'OFFLINE'),
      statusText: json['statusText'],
      country: json['country'],
      city: json['city'],
      language: json['language'],
      timezone: json['timezone'],
      updatedAt: json['updatedAt'] != null
          ? DateTime.tryParse(json['updatedAt'])
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'nickname': nickname,
      'avatarUrl': avatarUrl,
      'bio': bio,
      'gender': gender,
      'birthday': birthday?.toIso8601String(),
      'email': email,
      'phone': phone,
      'onlineStatus': onlineStatus.value,
      'statusText': statusText,
      'country': country,
      'city': city,
      'language': language,
      'timezone': timezone,
    };
  }

  UserProfile copyWith({
    int? userId,
    String? nickname,
    String? avatarUrl,
    String? bio,
    int? gender,
    DateTime? birthday,
    String? email,
    String? phone,
    OnlineStatus? onlineStatus,
    String? statusText,
    String? country,
    String? city,
    String? language,
    String? timezone,
  }) {
    return UserProfile(
      userId: userId ?? this.userId,
      nickname: nickname ?? this.nickname,
      avatarUrl: avatarUrl ?? this.avatarUrl,
      bio: bio ?? this.bio,
      gender: gender ?? this.gender,
      birthday: birthday ?? this.birthday,
      email: email ?? this.email,
      phone: phone ?? this.phone,
      onlineStatus: onlineStatus ?? this.onlineStatus,
      statusText: statusText ?? this.statusText,
      country: country ?? this.country,
      city: city ?? this.city,
      language: language ?? this.language,
      timezone: timezone ?? this.timezone,
      updatedAt: DateTime.now(),
    );
  }
}

enum OnlineStatus {
  online('ONLINE', '在线', 0xFF52C41A),
  away('AWAY', '离开', 0xFFFAAD14),
  busy('BUSY', '忙碌', 0xFFF5222D),
  dnd('DND', '请勿打扰', 0xFFF5222D),
  invisible('INVISIBLE', '隐身', 0xFFD9D9D9),
  offline('OFFLINE', '离线', 0xFFD9D9D9);

  final String value;
  final String label;
  final int color;

  const OnlineStatus(this.value, this.label, this.color);

  static OnlineStatus fromString(String? value) {
    return OnlineStatus.values.firstWhere(
      (s) => s.value == (value ?? 'OFFLINE'),
      orElse: () => OnlineStatus.offline,
    );
  }
}

class FriendGroup {
  final int id;
  final int userId;
  final String groupName;
  final int sortOrder;
  final DateTime createdAt;
  final DateTime? updatedAt;

  FriendGroup({
    required this.id,
    required this.userId,
    required this.groupName,
    required this.sortOrder,
    required this.createdAt,
    this.updatedAt,
  });

  factory FriendGroup.fromJson(Map<String, dynamic> json) {
    return FriendGroup(
      id: json['id'] ?? 0,
      userId: json['userId'] ?? 0,
      groupName: json['groupName'] ?? '默认分组',
      sortOrder: json['sortOrder'] ?? 0,
      createdAt: json['createdAt'] != null
          ? DateTime.parse(json['createdAt'])
          : DateTime.now(),
      updatedAt: json['updatedAt'] != null
          ? DateTime.tryParse(json['updatedAt'])
          : null,
    );
  }
}

class FriendRemark {
  final int id;
  final int userId;
  final int friendId;
  final String? remarkName;
  final int? groupId;
  final bool isPinned;
  final DateTime addedAt;
  final DateTime? updatedAt;

  FriendRemark({
    required this.id,
    required this.userId,
    required this.friendId,
    this.remarkName,
    this.groupId,
    this.isPinned = false,
    required this.addedAt,
    this.updatedAt,
  });

  factory FriendRemark.fromJson(Map<String, dynamic> json) {
    return FriendRemark(
      id: json['id'] ?? 0,
      userId: json['userId'] ?? 0,
      friendId: json['friendId'] ?? 0,
      remarkName: json['remarkName'],
      groupId: json['groupId'],
      isPinned: json['isPinned'] ?? false,
      addedAt: json['addedAt'] != null
          ? DateTime.parse(json['addedAt'])
          : DateTime.now(),
      updatedAt: json['updatedAt'] != null
          ? DateTime.tryParse(json['updatedAt'])
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'friendId': friendId,
      'remarkName': remarkName,
      'groupId': groupId,
      'isPinned': isPinned,
    };
  }
}
