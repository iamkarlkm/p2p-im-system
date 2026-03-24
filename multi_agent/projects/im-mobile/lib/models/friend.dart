class Friend {
  final String id;
  final String userId;
  final String friendId;
  final String? friendRemark;
  final int status;
  final DateTime createTime;
  final User? user;

  Friend({
    required this.id,
    required this.userId,
    required this.friendId,
    this.friendRemark,
    required this.status,
    required this.createTime,
    this.user,
  });

  factory Friend.fromJson(Map<String, dynamic> json) {
    return Friend(
      id: json['id'] ?? '',
      userId: json['userId'] ?? '',
      friendId: json['friendId'] ?? '',
      friendRemark: json['friendRemark'],
      status: json['status'] ?? 1,
      createTime: json['createTime'] != null 
          ? DateTime.parse(json['createTime']) 
          : DateTime.now(),
      user: json['user'] != null ? User.fromJson(json['user']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'friendId': friendId,
      'friendRemark': friendRemark,
      'status': status,
      'createTime': createTime.toIso8601String(),
      'user': user?.toJson(),
    };
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
