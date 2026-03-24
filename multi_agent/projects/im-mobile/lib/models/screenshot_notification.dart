class ScreenshotEvent {
  final String eventId;
  final int conversationId;
  final String conversationType;
  final int capturedByUserId;
  final String capturedByUsername;
  final DateTime screenshotTime;
  final String deviceType;
  final String message;

  ScreenshotEvent({
    required this.eventId,
    required this.conversationId,
    required this.conversationType,
    required this.capturedByUserId,
    required this.capturedByUsername,
    required this.screenshotTime,
    required this.deviceType,
    required this.message,
  });

  factory ScreenshotEvent.fromJson(Map<String, dynamic> json) {
    return ScreenshotEvent(
      eventId: json['eventId'] ?? '',
      conversationId: json['conversationId'] ?? 0,
      conversationType: json['conversationType'] ?? 'private',
      capturedByUserId: json['capturedByUserId'] ?? 0,
      capturedByUsername: json['capturedByUsername'] ?? 'Unknown',
      screenshotTime: DateTime.tryParse(json['screenshotTime'] ?? '') ?? DateTime.now(),
      deviceType: json['deviceType'] ?? 'mobile',
      message: json['message'] ?? '',
    );
  }

  Map<String, dynamic> toJson() => {
    'eventId': eventId,
    'conversationId': conversationId,
    'conversationType': conversationType,
    'capturedByUserId': capturedByUserId,
    'capturedByUsername': capturedByUsername,
    'screenshotTime': screenshotTime.toIso8601String(),
    'deviceType': deviceType,
    'message': message,
  };
}

class ScreenshotSettings {
  final int userId;
  final bool enableScreenshotNotification;
  final bool notifyOnCapture;
  final bool receiveScreenshotAlerts;
  final bool alertForContacts;
  final bool alertForGroups;
  final bool silentMode;

  ScreenshotSettings({
    required this.userId,
    this.enableScreenshotNotification = true,
    this.notifyOnCapture = true,
    this.receiveScreenshotAlerts = true,
    this.alertForContacts = true,
    this.alertForGroups = true,
    this.silentMode = false,
  });

  factory ScreenshotSettings.fromJson(Map<String, dynamic> json) {
    return ScreenshotSettings(
      userId: json['userId'] ?? 0,
      enableScreenshotNotification: json['enableScreenshotNotification'] ?? true,
      notifyOnCapture: json['notifyOnCapture'] ?? true,
      receiveScreenshotAlerts: json['receiveScreenshotAlerts'] ?? true,
      alertForContacts: json['alertForContacts'] ?? true,
      alertForGroups: json['alertForGroups'] ?? true,
      silentMode: json['silentMode'] ?? false,
    );
  }

  Map<String, dynamic> toJson() => {
    'userId': userId,
    'enableScreenshotNotification': enableScreenshotNotification,
    'notifyOnCapture': notifyOnCapture,
    'receiveScreenshotAlerts': receiveScreenshotAlerts,
    'alertForContacts': alertForContacts,
    'alertForGroups': alertForGroups,
    'silentMode': silentMode,
  };

  ScreenshotSettings copyWith({
    int? userId,
    bool? enableScreenshotNotification,
    bool? notifyOnCapture,
    bool? receiveScreenshotAlerts,
    bool? alertForContacts,
    bool? alertForGroups,
    bool? silentMode,
  }) {
    return ScreenshotSettings(
      userId: userId ?? this.userId,
      enableScreenshotNotification: enableScreenshotNotification ?? this.enableScreenshotNotification,
      notifyOnCapture: notifyOnCapture ?? this.notifyOnCapture,
      receiveScreenshotAlerts: receiveScreenshotAlerts ?? this.receiveScreenshotAlerts,
      alertForContacts: alertForContacts ?? this.alertForContacts,
      alertForGroups: alertForGroups ?? this.alertForGroups,
      silentMode: silentMode ?? this.silentMode,
    );
  }
}
