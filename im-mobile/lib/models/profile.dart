/// 用户资料模型 - Flutter移动端

class UserProfile {
  final String userId;
  final String nickname;
  final String avatarUrl;
  final String signature;
  final String? email;
  final String? phone;
  final String? gender;
  final String? birthday;
  final String? region;
  final String? language;
  final UserStatus status;
  final Map<String, dynamic>? customStatus;
  final DateTime createdAt;
  final DateTime updatedAt;

  UserProfile({
    required this.userId,
    this.nickname = '',
    this.avatarUrl = '',
    this.signature = '',
    this.email,
    this.phone,
    this.gender,
    this.birthday,
    this.region,
    this.language,
    this.status = UserStatus.online,
    this.customStatus,
    DateTime? createdAt,
    DateTime? updatedAt,
  })  : createdAt = createdAt ?? DateTime.now(),
        updatedAt = updatedAt ?? DateTime.now();

  factory UserProfile.fromJson(Map<String, dynamic> json) {
    return UserProfile(
      userId: json['userId'] ?? '',
      nickname: json['nickname'] ?? '',
      avatarUrl: json['avatarUrl'] ?? '',
      signature: json['signature'] ?? '',
      email: json['email'],
      phone: json['phone'],
      gender: json['gender'],
      birthday: json['birthday'],
      region: json['region'],
      language: json['language'],
      status: UserStatus.fromString(json['status'] ?? 'ONLINE'),
      customStatus: json['customStatus'],
      createdAt: json['createdAt'] != null
          ? DateTime.tryParse(json['createdAt']) ?? DateTime.now()
          : DateTime.now(),
      updatedAt: json['updatedAt'] != null
          ? DateTime.tryParse(json['updatedAt']) ?? DateTime.now()
          : DateTime.now(),
    );
  }

  Map<String, dynamic> toJson() => {
        'userId': userId,
        'nickname': nickname,
        'avatarUrl': avatarUrl,
        'signature': signature,
        'email': email,
        'phone': phone,
        'gender': gender,
        'birthday': birthday,
        'region': region,
        'language': language,
        'status': status.name,
        'customStatus': customStatus,
        'createdAt': createdAt.toIso8601String(),
        'updatedAt': updatedAt.toIso8601String(),
      };

  UserProfile copyWith({
    String? userId,
    String? nickname,
    String? avatarUrl,
    String? signature,
    String? email,
    String? phone,
    String? gender,
    String? birthday,
    String? region,
    String? language,
    UserStatus? status,
    Map<String, dynamic>? customStatus,
  }) {
    return UserProfile(
      userId: userId ?? this.userId,
      nickname: nickname ?? this.nickname,
      avatarUrl: avatarUrl ?? this.avatarUrl,
      signature: signature ?? this.signature,
      email: email ?? this.email,
      phone: phone ?? this.phone,
      gender: gender ?? this.gender,
      birthday: birthday ?? this.birthday,
      region: region ?? this.region,
      language: language ?? this.language,
      status: status ?? this.status,
      customStatus: customStatus ?? this.customStatus,
      createdAt: createdAt,
      updatedAt: DateTime.now(),
    );
  }
}

enum UserStatus {
  online,
  away,
  busy,
  doNotDisturb,
  invisible,
  offline;

  static UserStatus fromString(String? s) {
    switch (s?.toUpperCase()) {
      case 'ONLINE':
        return UserStatus.online;
      case 'AWAY':
        return UserStatus.away;
      case 'BUSY':
        return UserStatus.busy;
      case 'DO_NOT_DISTURB':
        return UserStatus.doNotDisturb;
      case 'INVISIBLE':
        return UserStatus.invisible;
      default:
        return UserStatus.offline;
    }
  }

  String get label {
    switch (this) {
      case UserStatus.online:
        return '在线';
      case UserStatus.away:
        return '离开';
      case UserStatus.busy:
        return '忙碌';
      case UserStatus.doNotDisturb:
        return '请勿打扰';
      case UserStatus.invisible:
        return '隐身';
      case UserStatus.offline:
        return '离线';
    }
  }

  String get apiValue => name.toUpperCase();
}

class FriendRemark {
  final String friendId;
  final String remark;
  final String groupName;
  final List<String> tags;
  final DateTime updatedAt;

  FriendRemark({
    required this.friendId,
    this.remark = '',
    this.groupName = '',
    this.tags = const [],
    DateTime? updatedAt,
  }) : updatedAt = updatedAt ?? DateTime.now();

  factory FriendRemark.fromJson(Map<String, dynamic> json) {
    return FriendRemark(
      friendId: json['friendId'] ?? '',
      remark: json['remark'] ?? '',
      groupName: json['groupName'] ?? '',
      tags: (json['tags'] as List<dynamic>?)?.cast<String>() ?? [],
      updatedAt: json['updatedAt'] != null
          ? DateTime.tryParse(json['updatedAt']) ?? DateTime.now()
          : DateTime.now(),
    );
  }
}

class FriendGroup {
  final String groupId;
  final String name;
  final int sortOrder;
  final int memberCount;

  FriendGroup({
    required this.groupId,
    required this.name,
    this.sortOrder = 0,
    this.memberCount = 0,
  });

  factory FriendGroup.fromJson(Map<String, dynamic> json) {
    return FriendGroup(
      groupId: json['groupId'] ?? '',
      name: json['name'] ?? '',
      sortOrder: json['sortOrder'] ?? 0,
      memberCount: json['memberCount'] ?? 0,
    );
  }
}
