import 'dart:convert';

/// 围栏触发事件模型类
/// 
/// @author IM Development Team
/// @since 2026-03-28
class GeofenceEvent {
  final String eventId;
  final String userId;
  final String geofenceId;
  final String? geofenceName;
  final String? merchantId;
  final String? merchantName;
  final EventType eventType;
  final DateTime triggerTime;
  final double? longitude;
  final double? latitude;
  final int? dwellMinutes;
  final String? sessionId;
  final int? confidenceScore;
  final bool? messageSent;
  final String? messageId;

  GeofenceEvent({
    required this.eventId,
    required this.userId,
    required this.geofenceId,
    this.geofenceName,
    this.merchantId,
    this.merchantName,
    required this.eventType,
    required this.triggerTime,
    this.longitude,
    this.latitude,
    this.dwellMinutes,
    this.sessionId,
    this.confidenceScore,
    this.messageSent,
    this.messageId,
  });

  factory GeofenceEvent.fromJson(Map<String, dynamic> json) {
    return GeofenceEvent(
      eventId: json['eventId']?.toString() ?? '',
      userId: json['userId']?.toString() ?? '',
      geofenceId: json['geofenceId']?.toString() ?? '',
      geofenceName: json['geofenceName'],
      merchantId: json['merchantId']?.toString(),
      merchantName: json['merchantName'],
      eventType: EventType.values.firstWhere(
        (e) => e.name.toUpperCase() == (json['eventType'] ?? 'ENTER'),
        orElse: () => EventType.enter,
      ),
      triggerTime: json['triggerTime'] != null
          ? DateTime.parse(json['triggerTime'])
          : DateTime.now(),
      longitude: json['longitude']?.toDouble(),
      latitude: json['latitude']?.toDouble(),
      dwellMinutes: json['dwellMinutes'],
      sessionId: json['sessionId'],
      confidenceScore: json['confidenceScore'],
      messageSent: json['messageSent'],
      messageId: json['messageId']?.toString(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'eventId': eventId,
      'userId': userId,
      'geofenceId': geofenceId,
      'geofenceName': geofenceName,
      'merchantId': merchantId,
      'merchantName': merchantName,
      'eventType': eventType.name.toUpperCase(),
      'triggerTime': triggerTime.toIso8601String(),
      'longitude': longitude,
      'latitude': latitude,
      'dwellMinutes': dwellMinutes,
      'sessionId': sessionId,
      'confidenceScore': confidenceScore,
      'messageSent': messageSent,
      'messageId': messageId,
    };
  }

  String get displayText {
    switch (eventType) {
      case EventType.enter:
        return '进入 $geofenceName';
      case EventType.exit:
        return '离开 $geofenceName';
      case EventType.dwell:
        return '在 $geofenceName 停留 ${dwellMinutes ?? 0} 分钟';
    }
  }

  @override
  String toString() => jsonEncode(toJson());
}

/// 事件类型枚举
enum EventType {
  enter, // 进入
  exit,  // 离开
  dwell, // 停留
}
