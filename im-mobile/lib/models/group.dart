class Group {
  final String id;
  final String groupId;
  final String groupName;
  final String? avatarUrl;
  final String ownerId;
  final int memberCount;
  final String? notice;
  final String? description;
  final DateTime createTime;
  final DateTime updateTime;

  Group({
    required this.id,
    required this.groupId,
    required this.groupName,
    this.avatarUrl,
    required this.ownerId,
    required this.memberCount,
    this.notice,
    this.description,
    required this.createTime,
    required this.updateTime,
  });

  factory Group.fromJson(Map<String, dynamic> json) {
    return Group(
      id: json['id']?.toString() ?? '',
      groupId: json['groupId'] ?? '',
      groupName: json['groupName'] ?? '',
      avatarUrl: json['avatarUrl'],
      ownerId: json['ownerId']?.toString() ?? '',
      memberCount: json['memberCount'] ?? 0,
      notice: json['notice'],
      description: json['description'] ?? json['notice'],
      createTime: json['createTime'] != null 
          ? DateTime.parse(json['createTime']) 
          : DateTime.now(),
      updateTime: json['updateTime'] != null 
          ? DateTime.parse(json['updateTime']) 
          : DateTime.now(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'groupId': groupId,
      'groupName': groupName,
      'avatarUrl': avatarUrl,
      'ownerId': ownerId,
      'memberCount': memberCount,
      'notice': notice,
      'description': description,
      'createTime': createTime.toIso8601String(),
      'updateTime': updateTime.toIso8601String(),
    };
  }
}

class GroupMember {
  final String id;
  final String groupId;
  final String userId;
  final int role; // 1: Member, 2: Admin, 3: Owner
  final String? nickname;
  final DateTime joinTime;
  final User? user;

  GroupMember({
    required this.id,
    required this.groupId,
    required this.userId,
    required this.role,
    this.nickname,
    required this.joinTime,
    this.user,
  });

  factory GroupMember.fromJson(Map<String, dynamic> json) {
    return GroupMember(
      id: json['id']?.toString() ?? '',
      groupId: json['groupId']?.toString() ?? '',
      userId: json['userId']?.toString() ?? '',
      role: json['role'] ?? 1,
      nickname: json['nickname'],
      joinTime: json['joinTime'] != null 
          ? DateTime.parse(json['joinTime']) 
          : DateTime.now(),
      user: json['user'] != null ? User.fromJson(json['user']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'groupId': groupId,
      'userId': userId,
      'role': role,
      'nickname': nickname,
      'joinTime': joinTime.toIso8601String(),
      'user': user?.toJson(),
    };
  }
  
  String get roleName {
    switch (role) {
      case 1:
        return '成员';
      case 2:
        return '管理员';
      case 3:
        return '群主';
      default:
        return '成员';
    }
  }
}

class User {
  final String id;
  final String username;
  final String? nickname;
  final String? avatarUrl;
  final String? phone;
  final String? email;
  final int? status;

  User({
    required this.id,
    required this.username,
    this.nickname,
    this.avatarUrl,
    this.phone,
    this.email,
    this.status,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id']?.toString() ?? '',
      username: json['username'] ?? '',
      nickname: json['nickname'],
      avatarUrl: json['avatarUrl'],
      phone: json['phone'],
      email: json['email'],
      status: json['status'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'username': username,
      'nickname': nickname,
      'avatarUrl': avatarUrl,
      'phone': phone,
      'email': email,
      'status': status,
    };
  }
}
