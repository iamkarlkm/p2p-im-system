import 'dart:convert';

/// 用户围栏状态模型类
/// 记录用户与各围栏的实时状态关系
/// 状态机: OUTSIDE → ENTERING → INSIDE → DWELLING → EXITING → OUTSIDE
/// 
/// @author IM Development Team
/// @since 2026-03-28
class UserGeofenceState {
  final String? id;
  final String userId;
  final String geofenceId;
  final GeofenceState currentState;
  final GeofenceState? previousState;
  final DateTime? stateUpdateTime;
  final DateTime? firstEnterTime;
  final DateTime? lastEnterTime;
  final DateTime? lastExitTime;
  final int totalDwellMinutes;
  final int currentDwellMinutes;
  final int enterCount;
  final int exitCount;
  final int triggerCount;
  final DateTime? lastTriggerTime;
  final double? currentLongitude;
  final double? currentLatitude;
  final double? locationAccuracy;
  final String? locationSource;
  final DateTime? locationReportTime;
  final int? confidenceScore;
  final bool? suspectedSpoofing;
  final double? distanceToBoundary;
  final bool subscribed;
  final DateTime? subscribeTime;
  final bool welcomeMessageSent;
  final bool thankYouMessageSent;
  final String? sessionId;
  final DateTime? sessionStartTime;
  final DateTime? sessionEndTime;
  final GeofenceZone? geofence;
  final String? geofenceName;

  UserGeofenceState({
    this.id,
    required this.userId,
    required this.geofenceId,
    required this.currentState,
    this.previousState,
    this.stateUpdateTime,
    this.firstEnterTime,
    this.lastEnterTime,
    this.lastExitTime,
    this.totalDwellMinutes = 0,
    this.currentDwellMinutes = 0,
    this.enterCount = 0,
    this.exitCount = 0,
    this.triggerCount = 0,
    this.lastTriggerTime,
    this.currentLongitude,
    this.currentLatitude,
    this.locationAccuracy,
    this.locationSource,
    this.locationReportTime,
    this.confidenceScore,
    this.suspectedSpoofing,
    this.distanceToBoundary,
    this.subscribed = false,
    this.subscribeTime,
    this.welcomeMessageSent = false,
    this.thankYouMessageSent = false,
    this.sessionId,
    this.sessionStartTime,
    this.sessionEndTime,
    this.geofence,
    this.geofenceName,
  });

  factory UserGeofenceState.fromJson(Map<String, dynamic> json) {
    return UserGeofenceState(
      id: json['id']?.toString(),
      userId: json['userId']?.toString() ?? '',
      geofenceId: json['geofenceId']?.toString() ?? '',
      currentState: GeofenceState.values.firstWhere(
        (e) => e.name.toUpperCase() == (json['currentState'] ?? 'OUTSIDE'),
        orElse: () => GeofenceState.outside,
      ),
      previousState: json['previousState'] != null
          ? GeofenceState.values.firstWhere(
              (e) => e.name.toUpperCase() == json['previousState'],
              orElse: () => GeofenceState.outside,
            )
          : null,
      stateUpdateTime: json['stateUpdateTime'] != null
          ? DateTime.parse(json['stateUpdateTime'])
          : null,
      firstEnterTime: json['firstEnterTime'] != null
          ? DateTime.parse(json['firstEnterTime'])
          : null,
      lastEnterTime: json['lastEnterTime'] != null
          ? DateTime.parse(json['lastEnterTime'])
          : null,
      lastExitTime: json['lastExitTime'] != null
          ? DateTime.parse(json['lastExitTime'])
          : null,
      totalDwellMinutes: json['totalDwellMinutes'] ?? 0,
      currentDwellMinutes: json['currentDwellMinutes'] ?? 0,
      enterCount: json['enterCount'] ?? 0,
      exitCount: json['exitCount'] ?? 0,
      triggerCount: json['triggerCount'] ?? 0,
      lastTriggerTime: json['lastTriggerTime'] != null
          ? DateTime.parse(json['lastTriggerTime'])
          : null,
      currentLongitude: json['currentLongitude']?.toDouble(),
      currentLatitude: json['currentLatitude']?.toDouble(),
      locationAccuracy: json['locationAccuracy']?.toDouble(),
      locationSource: json['locationSource'],
      locationReportTime: json['locationReportTime'] != null
          ? DateTime.parse(json['locationReportTime'])
          : null,
      confidenceScore: json['confidenceScore'],
      suspectedSpoofing: json['suspectedSpoofing'],
      distanceToBoundary: json['distanceToBoundary']?.toDouble(),
      subscribed: json['subscribed'] ?? false,
      subscribeTime: json['subscribeTime'] != null
          ? DateTime.parse(json['subscribeTime'])
          : null,
      welcomeMessageSent: json['welcomeMessageSent'] ?? false,
      thankYouMessageSent: json['thankYouMessageSent'] ?? false,
      sessionId: json['sessionId'],
      sessionStartTime: json['sessionStartTime'] != null
          ? DateTime.parse(json['sessionStartTime'])
          : null,
      sessionEndTime: json['sessionEndTime'] != null
          ? DateTime.parse(json['sessionEndTime'])
          : null,
      geofenceName: json['geofenceName'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'geofenceId': geofenceId,
      'currentState': currentState.name.toUpperCase(),
      'previousState': previousState?.name.toUpperCase(),
      'stateUpdateTime': stateUpdateTime?.toIso8601String(),
      'firstEnterTime': firstEnterTime?.toIso8601String(),
      'lastEnterTime': lastEnterTime?.toIso8601String(),
      'lastExitTime': lastExitTime?.toIso8601String(),
      'totalDwellMinutes': totalDwellMinutes,
      'currentDwellMinutes': currentDwellMinutes,
      'enterCount': enterCount,
      'exitCount': exitCount,
      'triggerCount': triggerCount,
      'lastTriggerTime': lastTriggerTime?.toIso8601String(),
      'currentLongitude': currentLongitude,
      'currentLatitude': currentLatitude,
      'locationAccuracy': locationAccuracy,
      'locationSource': locationSource,
      'locationReportTime': locationReportTime?.toIso8601String(),
      'confidenceScore': confidenceScore,
      'suspectedSpoofing': suspectedSpoofing,
      'distanceToBoundary': distanceToBoundary,
      'subscribed': subscribed,
      'subscribeTime': subscribeTime?.toIso8601String(),
      'welcomeMessageSent': welcomeMessageSent,
      'thankYouMessageSent': thankYouMessageSent,
      'sessionId': sessionId,
      'sessionStartTime': sessionStartTime?.toIso8601String(),
      'sessionEndTime': sessionEndTime?.toIso8601String(),
      'geofenceName': geofenceName,
    };
  }

  bool get isInside => currentState == GeofenceState.inside || 
                       currentState == GeofenceState.dwelling;
  
  bool get isOutside => currentState == GeofenceState.outside;
  
  bool get isDwelling => currentState == GeofenceState.dwelling;

  UserGeofenceState copyWith({
    GeofenceState? currentState,
    GeofenceState? previousState,
    DateTime? stateUpdateTime,
    DateTime? lastEnterTime,
    DateTime? lastExitTime,
    int? totalDwellMinutes,
    int? currentDwellMinutes,
    int? enterCount,
    int? exitCount,
    int? triggerCount,
    DateTime? lastTriggerTime,
    double? currentLongitude,
    double? currentLatitude,
    double? locationAccuracy,
    String? locationSource,
    DateTime? locationReportTime,
    int? confidenceScore,
    bool? subscribed,
    String? sessionId,
    DateTime? sessionStartTime,
    DateTime? sessionEndTime,
  }) {
    return UserGeofenceState(
      id: id,
      userId: userId,
      geofenceId: geofenceId,
      currentState: currentState ?? this.currentState,
      previousState: previousState ?? this.previousState,
      stateUpdateTime: stateUpdateTime ?? this.stateUpdateTime,
      firstEnterTime: firstEnterTime,
      lastEnterTime: lastEnterTime ?? this.lastEnterTime,
      lastExitTime: lastExitTime ?? this.lastExitTime,
      totalDwellMinutes: totalDwellMinutes ?? this.totalDwellMinutes,
      currentDwellMinutes: currentDwellMinutes ?? this.currentDwellMinutes,
      enterCount: enterCount ?? this.enterCount,
      exitCount: exitCount ?? this.exitCount,
      triggerCount: triggerCount ?? this.triggerCount,
      lastTriggerTime: lastTriggerTime ?? this.lastTriggerTime,
      currentLongitude: currentLongitude ?? this.currentLongitude,
      currentLatitude: currentLatitude ?? this.currentLatitude,
      locationAccuracy: locationAccuracy ?? this.locationAccuracy,
      locationSource: locationSource ?? this.locationSource,
      locationReportTime: locationReportTime ?? this.locationReportTime,
      confidenceScore: confidenceScore ?? this.confidenceScore,
      suspectedSpoofing: suspectedSpoofing,
      distanceToBoundary: distanceToBoundary,
      subscribed: subscribed ?? this.subscribed,
      subscribeTime: subscribeTime,
      welcomeMessageSent: welcomeMessageSent,
      thankYouMessageSent: thankYouMessageSent,
      sessionId: sessionId ?? this.sessionId,
      sessionStartTime: sessionStartTime ?? this.sessionStartTime,
      sessionEndTime: sessionEndTime ?? this.sessionEndTime,
      geofence: geofence,
      geofenceName: geofenceName,
    );
  }

  @override
  String toString() => jsonEncode(toJson());
}

/// 围栏状态枚举
enum GeofenceState {
  outside,   // 外部
  entering,  // 进入中
  inside,    // 内部
  dwelling,  // 停留中
  exiting,   // 离开中
}
