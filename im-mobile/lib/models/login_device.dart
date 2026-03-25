class LoginDevice {
  final int id;
  final int userId;
  final String deviceId;
  final String? deviceName;
  final String? deviceType;
  final String? deviceModel;
  final String? osVersion;
  final String? appVersion;
  final String? ipAddress;
  final String? location;
  final DateTime lastActiveTime;
  final DateTime firstLoginTime;
  final bool isCurrent;
  final bool isTrusted;
  final bool isRemoteTerminated;
  final DateTime? terminatedAt;

  LoginDevice({
    required this.id,
    required this.userId,
    required this.deviceId,
    this.deviceName,
    this.deviceType,
    this.deviceModel,
    this.osVersion,
    this.appVersion,
    this.ipAddress,
    this.location,
    required this.lastActiveTime,
    required this.firstLoginTime,
    required this.isCurrent,
    required this.isTrusted,
    required this.isRemoteTerminated,
    this.terminatedAt,
  });

  factory LoginDevice.fromJson(Map<String, dynamic> json) {
    return LoginDevice(
      id: json['id'],
      userId: json['userId'],
      deviceId: json['deviceId'],
      deviceName: json['deviceName'],
      deviceType: json['deviceType'],
      deviceModel: json['deviceModel'],
      osVersion: json['osVersion'],
      appVersion: json['appVersion'],
      ipAddress: json['ipAddress'],
      location: json['location'],
      lastActiveTime: DateTime.parse(json['lastActiveTime']),
      firstLoginTime: DateTime.parse(json['firstLoginTime']),
      isCurrent: json['isCurrent'] ?? false,
      isTrusted: json['isTrusted'] ?? false,
      isRemoteTerminated: json['isRemoteTerminated'] ?? false,
      terminatedAt: json['terminatedAt'] != null
          ? DateTime.parse(json['terminatedAt'])
          : null,
    );
  }
}
