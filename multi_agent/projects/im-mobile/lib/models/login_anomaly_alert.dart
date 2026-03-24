class LoginAnomalyAlert {
  final int id;
  final int userId;
  final String alertType;
  final String? deviceId;
  final String? deviceName;
  final String? deviceType;
  final String? ipAddress;
  final String? location;
  final DateTime loginTime;
  final bool isConfirmed;
  final DateTime? confirmedAt;
  final bool isDismissed;
  final DateTime? dismissedAt;
  final int? riskScore;
  final String? riskFactors;
  final String? actionTaken;
  final DateTime createdAt;

  LoginAnomalyAlert({
    required this.id,
    required this.userId,
    required this.alertType,
    this.deviceId,
    this.deviceName,
    this.deviceType,
    this.ipAddress,
    this.location,
    required this.loginTime,
    required this.isConfirmed,
    this.confirmedAt,
    required this.isDismissed,
    this.dismissedAt,
    this.riskScore,
    this.riskFactors,
    this.actionTaken,
    required this.createdAt,
  });

  factory LoginAnomalyAlert.fromJson(Map<String, dynamic> json) {
    return LoginAnomalyAlert(
      id: json['id'],
      userId: json['userId'],
      alertType: json['alertType'],
      deviceId: json['deviceId'],
      deviceName: json['deviceName'],
      deviceType: json['deviceType'],
      ipAddress: json['ipAddress'],
      location: json['location'],
      loginTime: DateTime.parse(json['loginTime']),
      isConfirmed: json['isConfirmed'] ?? false,
      confirmedAt: json['confirmedAt'] != null ? DateTime.parse(json['confirmedAt']) : null,
      isDismissed: json['isDismissed'] ?? false,
      dismissedAt: json['dismissedAt'] != null ? DateTime.parse(json['dismissedAt']) : null,
      riskScore: json['riskScore'],
      riskFactors: json['riskFactors'],
      actionTaken: json['actionTaken'],
      createdAt: DateTime.parse(json['createdAt']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'alertType': alertType,
      'deviceId': deviceId,
      'deviceName': deviceName,
      'deviceType': deviceType,
      'ipAddress': ipAddress,
      'location': location,
      'loginTime': loginTime.toIso8601String(),
      'isConfirmed': isConfirmed,
      'confirmedAt': confirmedAt?.toIso8601String(),
      'isDismissed': isDismissed,
      'dismissedAt': dismissedAt?.toIso8601String(),
      'riskScore': riskScore,
      'riskFactors': riskFactors,
      'actionTaken': actionTaken,
      'createdAt': createdAt.toIso8601String(),
    };
  }
}
