/// 用户位置模型
class UserLocation {
  final double latitude;
  final double longitude;
  final double? accuracy;
  final double? altitude;
  final double? speed;
  final DateTime timestamp;
  final String? address;

  UserLocation({
    required this.latitude,
    required this.longitude,
    this.accuracy,
    this.altitude,
    this.speed,
    required this.timestamp,
    this.address,
  });

  factory UserLocation.fromPosition(dynamic position) {
    return UserLocation(
      latitude: position.latitude,
      longitude: position.longitude,
      accuracy: position.accuracy,
      altitude: position.altitude,
      speed: position.speed,
      timestamp: DateTime.now(),
    );
  }

  factory UserLocation.fromJson(Map<String, dynamic> json) {
    return UserLocation(
      latitude: json['latitude'],
      longitude: json['longitude'],
      accuracy: json['accuracy']?.toDouble(),
      altitude: json['altitude']?.toDouble(),
      speed: json['speed']?.toDouble(),
      timestamp: DateTime.parse(json['timestamp']),
      address: json['address'],
    );
  }

  Map<String, dynamic> toJson() => {
    'latitude': latitude,
    'longitude': longitude,
    'accuracy': accuracy,
    'altitude': altitude,
    'speed': speed,
    'timestamp': timestamp.toIso8601String(),
    'address': address,
  };

  @override
  String toString() => 'UserLocation($latitude, $longitude)';
}

/// 位置分享模型
class LocationShare {
  final String id;
  final String userId;
  final String? userName;
  final String? userAvatar;
  final double latitude;
  final double longitude;
  final String? address;
  final String? message;
  final DateTime createdAt;
  final DateTime expiresAt;
  final bool isActive;
  final bool isLive;

  LocationShare({
    required this.id,
    required this.userId,
    this.userName,
    this.userAvatar,
    required this.latitude,
    required this.longitude,
    this.address,
    this.message,
    required this.createdAt,
    required this.expiresAt,
    this.isActive = true,
    this.isLive = false,
  });

  factory LocationShare.fromJson(Map<String, dynamic> json) {
    return LocationShare(
      id: json['id'],
      userId: json['userId'],
      userName: json['userName'],
      userAvatar: json['userAvatar'],
      latitude: json['latitude'],
      longitude: json['longitude'],
      address: json['address'],
      message: json['message'],
      createdAt: DateTime.parse(json['createdAt']),
      expiresAt: DateTime.parse(json['expiresAt']),
      isActive: json['isActive'] ?? true,
      isLive: json['isLive'] ?? false,
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'userId': userId,
    'userName': userName,
    'userAvatar': userAvatar,
    'latitude': latitude,
    'longitude': longitude,
    'address': address,
    'message': message,
    'createdAt': createdAt.toIso8601String(),
    'expiresAt': expiresAt.toIso8601String(),
    'isActive': isActive,
    'isLive': isLive,
  };

  /// 检查是否过期
  bool get isExpired => DateTime.now().isAfter(expiresAt);

  /// 获取剩余时间（分钟）
  int get remainingMinutes {
    final diff = expiresAt.difference(DateTime.now());
    return diff.inMinutes.clamp(0, double.infinity).toInt();
  }
}

/// 实时位置分享会话
class LiveLocationSession {
  final String id;
  final String userId;
  final String? userName;
  final String? userAvatar;
  final DateTime startedAt;
  final Duration duration;
  UserLocation? currentLocation;
  bool isSharing;

  LiveLocationSession({
    required this.id,
    required this.userId,
    this.userName,
    this.userAvatar,
    required this.startedAt,
    this.duration = const Duration(hours: 1),
    this.currentLocation,
    this.isSharing = true,
  });

  factory LiveLocationSession.fromJson(Map<String, dynamic> json) {
    return LiveLocationSession(
      id: json['id'],
      userId: json['userId'],
      userName: json['userName'],
      userAvatar: json['userAvatar'],
      startedAt: DateTime.parse(json['startedAt']),
      duration: Duration(minutes: json['durationMinutes'] ?? 60),
      currentLocation: json['currentLocation'] != null
          ? UserLocation.fromJson(json['currentLocation'])
          : null,
      isSharing: json['isSharing'] ?? true,
    );
  }

  /// 检查是否仍在分享中
  bool get isActive {
    if (!isSharing) return false;
    final elapsed = DateTime.now().difference(startedAt);
    return elapsed < duration;
  }

  /// 获取剩余时间
  Duration get remainingTime {
    final elapsed = DateTime.now().difference(startedAt);
    final remaining = duration - elapsed;
    return remaining.isNegative ? Duration.zero : remaining;
  }
}

/// 附近的人模型
class NearbyUser {
  final String userId;
  final String? nickname;
  final String? avatar;
  final double latitude;
  final double longitude;
  final double distance; // 米
  final DateTime lastSeen;
  final bool isOnline;
  final double? similarity; // 相似度分数 0-1
  final Map<String, dynamic>? profile;

  NearbyUser({
    required this.userId,
    this.nickname,
    this.avatar,
    required this.latitude,
    required this.longitude,
    required this.distance,
    required this.lastSeen,
    this.isOnline = false,
    this.similarity,
    this.profile,
  });

  factory NearbyUser.fromJson(Map<String, dynamic> json) {
    return NearbyUser(
      userId: json['userId'],
      nickname: json['nickname'],
      avatar: json['avatar'],
      latitude: json['latitude'],
      longitude: json['longitude'],
      distance: json['distance'].toDouble(),
      lastSeen: DateTime.parse(json['lastSeen']),
      isOnline: json['isOnline'] ?? false,
      similarity: json['similarity']?.toDouble(),
      profile: json['profile'],
    );
  }

  String get formattedDistance {
    if (distance < 1000) {
      return '${distance.toInt()}m';
    } else {
      return '${(distance / 1000).toStringAsFixed(1)}km';
    }
  }
}
