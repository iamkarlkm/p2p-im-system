// Device Management Models

enum DeviceType { desktop, mobile, tablet, web, other }

enum LoginAction { login, logout, remove }

class Device {
  final int id;
  final String userId;
  final String deviceToken;
  final DeviceType deviceType;
  final String deviceName;
  final String deviceModel;
  final String osVersion;
  final String appVersion;
  final String browserInfo;
  final String ipAddress;
  final String location;
  final DateTime createdAt;
  final DateTime lastActiveAt;
  final bool isCurrent;
  final DateTime lastLoginAt;
  final bool isActive;
  final bool isTrusted;

  Device({
    required this.id,
    required this.userId,
    required this.deviceToken,
    required this.deviceType,
    required this.deviceName,
    this.deviceModel = '',
    this.osVersion = '',
    this.appVersion = '',
    this.browserInfo = '',
    this.ipAddress = '',
    this.location = '',
    required this.createdAt,
    required this.lastActiveAt,
    required this.isCurrent,
    required this.lastLoginAt,
    required this.isActive,
    required this.isTrusted,
  });

  factory Device.fromJson(Map<String, dynamic> json) {
    return Device(
      id: json['id'] ?? 0,
      userId: json['userId'] ?? '',
      deviceToken: json['deviceToken'] ?? '',
      deviceType: _parseDeviceType(json['deviceType']),
      deviceName: json['deviceName'] ?? '',
      deviceModel: json['deviceModel'] ?? '',
      osVersion: json['osVersion'] ?? '',
      appVersion: json['appVersion'] ?? '',
      browserInfo: json['browserInfo'] ?? '',
      ipAddress: json['ipAddress'] ?? '',
      location: json['location'] ?? '',
      createdAt: DateTime.tryParse(json['createdAt'] ?? '') ?? DateTime.now(),
      lastActiveAt: DateTime.tryParse(json['lastActiveAt'] ?? '') ?? DateTime.now(),
      isCurrent: json['isCurrent'] ?? false,
      lastLoginAt: DateTime.tryParse(json['lastLoginAt'] ?? '') ?? DateTime.now(),
      isActive: json['isActive'] ?? true,
      isTrusted: json['isTrusted'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'deviceToken': deviceToken,
      'deviceType': deviceType.name.toUpperCase(),
      'deviceName': deviceName,
      'deviceModel': deviceModel,
      'osVersion': osVersion,
      'appVersion': appVersion,
      'browserInfo': browserInfo,
      'ipAddress': ipAddress,
      'location': location,
      'createdAt': createdAt.toIso8601String(),
      'lastActiveAt': lastActiveAt.toIso8601String(),
      'isCurrent': isCurrent,
      'lastLoginAt': lastLoginAt.toIso8601String(),
      'isActive': isActive,
      'isTrusted': isTrusted,
    };
  }
}

class DeviceRegistrationRequest {
  final String? deviceToken;
  final DeviceType deviceType;
  final String deviceName;
  final String? deviceModel;
  final String? osVersion;
  final String? appVersion;
  final String? browserInfo;
  final String? ipAddress;
  final String? location;
  final double? latitude;
  final double? longitude;
  final bool? isTrusted;

  DeviceRegistrationRequest({
    this.deviceToken,
    required this.deviceType,
    required this.deviceName,
    this.deviceModel,
    this.osVersion,
    this.appVersion,
    this.browserInfo,
    this.ipAddress,
    this.location,
    this.latitude,
    this.longitude,
    this.isTrusted,
  });

  Map<String, dynamic> toJson() {
    return {
      if (deviceToken != null) 'deviceToken': deviceToken,
      'deviceType': deviceType.name.toUpperCase(),
      'deviceName': deviceName,
      if (deviceModel != null) 'deviceModel': deviceModel,
      if (osVersion != null) 'osVersion': osVersion,
      if (appVersion != null) 'appVersion': appVersion,
      if (browserInfo != null) 'browserInfo': browserInfo,
      if (ipAddress != null) 'ipAddress': ipAddress,
      if (location != null) 'location': location,
      if (latitude != null) 'latitude': latitude,
      if (longitude != null) 'longitude': longitude,
      if (isTrusted != null) 'isTrusted': isTrusted,
    };
  }
}

class LoginHistoryEntry {
  final int id;
  final String userId;
  final int deviceId;
  final String deviceToken;
  final DeviceType deviceType;
  final String deviceName;
  final String ipAddress;
  final String location;
  final DateTime loginTime;
  final DateTime? logoutTime;
  final LoginAction action;
  final String loginStatus;

  LoginHistoryEntry({
    required this.id,
    required this.userId,
    required this.deviceId,
    required this.deviceToken,
    required this.deviceType,
    required this.deviceName,
    required this.ipAddress,
    required this.location,
    required this.loginTime,
    this.logoutTime,
    required this.action,
    required this.loginStatus,
  });

  factory LoginHistoryEntry.fromJson(Map<String, dynamic> json) {
    return LoginHistoryEntry(
      id: json['id'] ?? 0,
      userId: json['userId'] ?? '',
      deviceId: json['deviceId'] ?? 0,
      deviceToken: json['deviceToken'] ?? '',
      deviceType: _parseDeviceType(json['deviceType']),
      deviceName: json['deviceName'] ?? '',
      ipAddress: json['ipAddress'] ?? '',
      location: json['location'] ?? '',
      loginTime: DateTime.tryParse(json['loginTime'] ?? '') ?? DateTime.now(),
      logoutTime: json['logoutTime'] != null
          ? DateTime.tryParse(json['logoutTime'])
          : null,
      action: _parseLoginAction(json['action']),
      loginStatus: json['loginStatus'] ?? 'SUCCESS',
    );
  }
}

class LoginHistoryPage {
  final List<LoginHistoryEntry> items;
  final int page;
  final int size;
  final int total;
  final int totalPages;

  LoginHistoryPage({
    required this.items,
    required this.page,
    required this.size,
    required this.total,
    required this.totalPages,
  });

  factory LoginHistoryPage.fromJson(Map<String, dynamic> json) {
    return LoginHistoryPage(
      items: (json['items'] as List? ?? [])
          .map((e) => LoginHistoryEntry.fromJson(e))
          .toList(),
      page: json['page'] ?? 0,
      size: json['size'] ?? 20,
      total: json['total'] ?? 0,
      totalPages: json['totalPages'] ?? 0,
    );
  }
}

class DeviceStats {
  final int totalDevices;
  final int activeDevices;
  final int trustedDevices;
  final DeviceType mostUsedDeviceType;
  final int activeSessions;

  DeviceStats({
    required this.totalDevices,
    required this.activeDevices,
    required this.trustedDevices,
    required this.mostUsedDeviceType,
    required this.activeSessions,
  });

  factory DeviceStats.fromJson(Map<String, dynamic> json) {
    return DeviceStats(
      totalDevices: json['totalDevices'] ?? 0,
      activeDevices: json['activeDevices'] ?? 0,
      trustedDevices: json['trustedDevices'] ?? 0,
      mostUsedDeviceType: _parseDeviceType(json['mostUsedDeviceType']),
      activeSessions: json['activeSessions'] ?? 0,
    );
  }
}

DeviceType _parseDeviceType(String? type) {
  switch (type?.toUpperCase()) {
    case 'DESKTOP':
      return DeviceType.desktop;
    case 'MOBILE':
      return DeviceType.mobile;
    case 'TABLET':
      return DeviceType.tablet;
    case 'WEB':
      return DeviceType.web;
    default:
      return DeviceType.other;
  }
}

LoginAction _parseLoginAction(String? action) {
  switch (action?.toUpperCase()) {
    case 'LOGIN':
      return LoginAction.login;
    case 'LOGOUT':
      return LoginAction.logout;
    case 'REMOVE':
      return LoginAction.remove;
    default:
      return LoginAction.login;
  }
}
