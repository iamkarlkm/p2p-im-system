class User {
  final String id;
  final String username;
  final String? nickname;
  final String? avatarUrl;
  final String? phone;
  final String? email;

  User({
    required this.id,
    required this.username,
    this.nickname,
    this.avatarUrl,
    this.phone,
    this.email,
  });

  factory User.fromJson(Map<String, dynamic> json) {
    return User(
      id: json['id'] ?? '',
      username: json['username'] ?? '',
      nickname: json['nickname'],
      avatarUrl: json['avatarUrl'],
      phone: json['phone'],
      email: json['email'],
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
    };
  }
}
