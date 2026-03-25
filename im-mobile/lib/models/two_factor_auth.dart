// 2FA Model for IM Mobile
class TwoFactorAuthModel {
  final int userId;
  final String? secret;
  final String? qrCodeUrl;
  final bool isEnabled;
  final bool isVerified;
  final List<String>? backupCodes;
  final int backupCodesUsed;
  final DateTime? lastVerifiedAt;
  final String? issuerName;
  final String? accountName;

  TwoFactorAuthModel({
    required this.userId,
    this.secret,
    this.qrCodeUrl,
    required this.isEnabled,
    required this.isVerified,
    this.backupCodes,
    this.backupCodesUsed = 0,
    this.lastVerifiedAt,
    this.issuerName,
    this.accountName,
  });

  factory TwoFactorAuthModel.fromJson(Map<String, dynamic> json) {
    return TwoFactorAuthModel(
      userId: json['userId'] ?? 0,
      secret: json['secret'],
      qrCodeUrl: json['qrCodeUrl'],
      isEnabled: json['isEnabled'] ?? false,
      isVerified: json['isVerified'] ?? false,
      backupCodes: json['backupCodes'] != null
          ? List<String>.from(json['backupCodes'])
          : null,
      backupCodesUsed: json['backupCodesUsed'] ?? 0,
      lastVerifiedAt: json['lastVerifiedAt'] != null
          ? DateTime.fromMillisecondsSinceEpoch(json['lastVerifiedAt'])
          : null,
      issuerName: json['issuerName'],
      accountName: json['accountName'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'secret': secret,
      'qrCodeUrl': qrCodeUrl,
      'isEnabled': isEnabled,
      'isVerified': isVerified,
      'backupCodes': backupCodes,
      'backupCodesUsed': backupCodesUsed,
      'lastVerifiedAt': lastVerifiedAt?.millisecondsSinceEpoch,
      'issuerName': issuerName,
      'accountName': accountName,
    };
  }

  int get remainingBackupCodes =>
      (backupCodes?.length ?? 0) - backupCodesUsed;
}

class TwoFactorSetupRequest {
  final String password;
  final String? issuerName;
  final String? accountName;

  TwoFactorSetupRequest({
    required this.password,
    this.issuerName,
    this.accountName,
  });

  Map<String, dynamic> toJson() {
    return {
      'password': password,
      if (issuerName != null) 'issuerName': issuerName,
      if (accountName != null) 'accountName': accountName,
    };
  }
}

class TwoFactorSetupResponse {
  final String secret;
  final String qrCodeUrl;
  final String manualEntryKey;
  final List<String> backupCodes;
  final String provisioningUri;
  final Map<String, dynamic>? appInfo;

  TwoFactorSetupResponse({
    required this.secret,
    required this.qrCodeUrl,
    required this.manualEntryKey,
    required this.backupCodes,
    required this.provisioningUri,
    this.appInfo,
  });

  factory TwoFactorSetupResponse.fromJson(Map<String, dynamic> json) {
    return TwoFactorSetupResponse(
      secret: json['secret'] ?? '',
      qrCodeUrl: json['qrCodeUrl'] ?? '',
      manualEntryKey: json['manualEntryKey'] ?? '',
      backupCodes: json['backupCodes'] != null
          ? List<String>.from(json['backupCodes'])
          : [],
      provisioningUri: json['provisioningUri'] ?? '',
      appInfo: json['appInfo'],
    );
  }
}

class TwoFactorVerifyRequest {
  final String code;
  final bool isBackupCode;
  final String? deviceName;

  TwoFactorVerifyRequest({
    required this.code,
    this.isBackupCode = false,
    this.deviceName,
  });

  Map<String, dynamic> toJson() {
    return {
      'code': code,
      'isBackupCode': isBackupCode,
      if (deviceName != null) 'deviceName': deviceName,
    };
  }
}

class TwoFactorVerifyResponse {
  final bool success;
  final String? token;
  final int? remainingBackupCodes;
  final String? message;
  final int? expiresIn;

  TwoFactorVerifyResponse({
    required this.success,
    this.token,
    this.remainingBackupCodes,
    this.message,
    this.expiresIn,
  });

  factory TwoFactorVerifyResponse.fromJson(Map<String, dynamic> json) {
    return TwoFactorVerifyResponse(
      success: json['success'] ?? false,
      token: json['token'],
      remainingBackupCodes: json['remainingBackupCodes'],
      message: json['message'],
      expiresIn: json['expiresIn'],
    );
  }
}

class TwoFactorStatusResponse {
  final bool isEnabled;
  final bool isVerified;
  final int backupCodesRemaining;
  final int? lastVerifiedAt;
  final String? issuerName;
  final String? accountName;
  final bool isRequired;

  TwoFactorStatusResponse({
    required this.isEnabled,
    required this.isVerified,
    required this.backupCodesRemaining,
    this.lastVerifiedAt,
    this.issuerName,
    this.accountName,
    required this.isRequired,
  });

  factory TwoFactorStatusResponse.fromJson(Map<String, dynamic> json) {
    return TwoFactorStatusResponse(
      isEnabled: json['isEnabled'] ?? false,
      isVerified: json['isVerified'] ?? false,
      backupCodesRemaining: json['backupCodesRemaining'] ?? 0,
      lastVerifiedAt: json['lastVerifiedAt'],
      issuerName: json['issuerName'],
      accountName: json['accountName'],
      isRequired: json['isRequired'] ?? false,
    );
  }
}
