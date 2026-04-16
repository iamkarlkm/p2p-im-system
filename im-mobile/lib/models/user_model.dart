/// 用户模型 - 功能#9 基础IM客户端SDK
/// 时间: 2026-04-01 09:25
class UserModel {
  final String id;
  final String username;
  final String nickname;
  final String avatar;
  final String email;
  final String phone;
  final int status; // 0: 禁用, 1: 正常
  final int lastLoginTime;

  UserModel({
    required this.id,
    required this.username,
    this.nickname = '',
    this.avatar = '',
    this.email = '',
    this.phone = '',
    this.status = 1,
    this.lastLoginTime = 0,
  });

  /// 从JSON解析
  factory UserModel.fromJson(Map<String, dynamic> json) {
    return UserModel(
      id: json['id']?.toString() ?? '',
      username: json['username'] ?? '',
      nickname: json['nickname'] ?? '',
      avatar: json['avatar'] ?? '',
      email: json['email'] ?? '',
      phone: json['phone'] ?? '',
      status: json['status'] ?? 1,
      lastLoginTime: json['lastLoginTime'] ?? 0,
    );
  }

  /// 转换为JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'username': username,
      'nickname': nickname,
      'avatar': avatar,
      'email': email,
      'phone': phone,
      'status': status,
      'lastLoginTime': lastLoginTime,
    };
  }

  /// 复制并修改
  UserModel copyWith({
    String? id,
    String? username,
    String? nickname,
    String? avatar,
    String? email,
    String? phone,
    int? status,
    int? lastLoginTime,
  }) {
    return UserModel(
      id: id ?? this.id,
      username: username ?? this.username,
      nickname: nickname ?? this.nickname,
      avatar: avatar ?? this.avatar,
      email: email ?? this.email,
      phone: phone ?? this.phone,
      status: status ?? this.status,
      lastLoginTime: lastLoginTime ?? this.lastLoginTime,
    );
  }

  /// 显示名称（优先使用昵称）
  String get displayName => nickname.isNotEmpty ? nickname : username;

  /// 是否有效
  bool get isValid => id.isNotEmpty && status == 1;
}

/// 好友模型
class FriendModel {
  final String userId;
  final String username;
  final String nickname;
  final String avatar;
  final String remark; // 备注名
  final int addTime;
  final int status; // 0: 待确认, 1: 好友, 2: 已删除

  FriendModel({
    required this.userId,
    required this.username,
    this.nickname = '',
    this.avatar = '',
    this.remark = '',
    this.addTime = 0,
    this.status = 1,
  });

  factory FriendModel.fromJson(Map<String, dynamic> json) {
    return FriendModel(
      userId: json['userId']?.toString() ?? '',
      username: json['username'] ?? '',
      nickname: json['nickname'] ?? '',
      avatar: json['avatar'] ?? '',
      remark: json['remark'] ?? '',
      addTime: json['addTime'] ?? 0,
      status: json['status'] ?? 1,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'username': username,
      'nickname': nickname,
      'avatar': avatar,
      'remark': remark,
      'addTime': addTime,
      'status': status,
    };
  }

  /// 显示名称（优先备注，其次昵称，最后用户名）
  String get displayName {
    if (remark.isNotEmpty) return remark;
    if (nickname.isNotEmpty) return nickname;
    return username;
  }
}

/// 好友申请模型
class FriendRequestModel {
  final String id;
  final String fromUserId;
  final String fromUsername;
  final String fromAvatar;
  final String toUserId;
  final String message; // 验证消息
  final int status; // 0: 待处理, 1: 已同意, 2: 已拒绝
  final int createTime;

  FriendRequestModel({
    required this.id,
    required this.fromUserId,
    this.fromUsername = '',
    this.fromAvatar = '',
    required this.toUserId,
    this.message = '',
    this.status = 0,
    this.createTime = 0,
  });

  factory FriendRequestModel.fromJson(Map<String, dynamic> json) {
    return FriendRequestModel(
      id: json['id']?.toString() ?? '',
      fromUserId: json['fromUserId']?.toString() ?? '',
      fromUsername: json['fromUsername'] ?? '',
      fromAvatar: json['fromAvatar'] ?? '',
      toUserId: json['toUserId']?.toString() ?? '',
      message: json['message'] ?? '',
      status: json['status'] ?? 0,
      createTime: json['createTime'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'fromUserId': fromUserId,
      'fromUsername': fromUsername,
      'fromAvatar': fromAvatar,
      'toUserId': toUserId,
      'message': message,
      'status': status,
      'createTime': createTime,
    };
  }
}
