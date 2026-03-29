import 'dart:async';
import 'dart:convert';
import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';
import 'package:geolocator/geolocator.dart';

/// 地理围栏类型枚举
enum GeofenceType {
  /// 圆形围栏
  circle,
  
  /// 多边形围栏
  polygon,
  
  /// 线性围栏（沿路线）
  polyline,
}

/// 围栏触发事件类型
enum GeofenceEvent {
  /// 进入围栏
  enter,
  
  /// 离开围栏
  exit,
  
  /// 在围栏内停留超过设定时间
  dwell,
  
  /// 围栏内移动
  move,
}

/// 围栏状态
enum GeofenceStatus {
  /// 未进入
  outside,
  
  /// 已进入
  inside,
  
  /// 停留中
  dwelling,
  
  /// 未知
  unknown,
}

/// 地理围栏数据模型
class Geofence {
  /// 围栏唯一标识
  final String id;
  
  /// 围栏名称
  final String name;
  
  /// 围栏类型
  final GeofenceType type;
  
  /// 圆心/中心点纬度
  final double latitude;
  
  /// 圆心/中心点经度
  final double longitude;
  
  /// 圆形围栏半径（米）
  final double? radius;
  
  /// 多边形围栏顶点列表 [(lat, lng), ...]
  final List<Map<String, double>>? polygonPoints;
  
  /// 线性围栏路径点
  final List<Map<String, double>>? polylinePoints;
  
  /// 线性围栏缓冲区宽度
  final double? polylineBuffer;
  
  /// 触发事件类型列表
  final List<GeofenceEvent> triggers;
  
  /// 停留触发时间（毫秒），dwell事件需要
  final int dwellTime;
  
  /// 关联商户ID
  final String? merchantId;
  
  /// 关联POI ID
  final String? poiId;
  
  /// 围栏元数据
  final Map<String, dynamic>? metadata;
  
  /// 创建时间
  final DateTime createdAt;
  
  /// 过期时间（null表示永不过期）
  final DateTime? expiresAt;
  
  /// 是否启用
  final bool isActive;
  
  /// 最小触发置信度（0.0 - 1.0）
  final double minConfidence;

  const Geofence({
    required this.id,
    required this.name,
    required this.type,
    required this.latitude,
    required this.longitude,
    this.radius,
    this.polygonPoints,
    this.polylinePoints,
    this.polylineBuffer,
    required this.triggers,
    this.dwellTime = 300000, // 默认5分钟
    this.merchantId,
    this.poiId,
    this.metadata,
    required this.createdAt,
    this.expiresAt,
    this.isActive = true,
    this.minConfidence = 0.8,
  });

  /// 从JSON创建
  factory Geofence.fromJson(Map<String, dynamic> json) {
    return Geofence(
      id: json['id'] as String,
      name: json['name'] as String,
      type: GeofenceType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => GeofenceType.circle,
      ),
      latitude: (json['latitude'] as num).toDouble(),
      longitude: (json['longitude'] as num).toDouble(),
      radius: json['radius'] != null ? (json['radius'] as num).toDouble() : null,
      polygonPoints: json['polygonPoints'] != null
          ? List<Map<String, double>>.from(
              (json['polygonPoints'] as List).map((p) => {
                'lat': (p['lat'] as num).toDouble(),
                'lng': (p['lng'] as num).toDouble(),
              }),
            )
          : null,
      polylinePoints: json['polylinePoints'] != null
          ? List<Map<String, double>>.from(
              (json['polylinePoints'] as List).map((p) => {
                'lat': (p['lat'] as num).toDouble(),
                'lng': (p['lng'] as num).toDouble(),
              }),
            )
          : null,
      polylineBuffer: json['polylineBuffer'] != null
          ? (json['polylineBuffer'] as num).toDouble()
          : null,
      triggers: (json['triggers'] as List)
          .map((t) => GeofenceEvent.values.firstWhere(
                (e) => e.name == t,
                orElse: () => GeofenceEvent.enter,
              ))
          .toList(),
      dwellTime: json['dwellTime'] as int? ?? 300000,
      merchantId: json['merchantId'] as String?,
      poiId: json['poiId'] as String?,
      metadata: json['metadata'] as Map<String, dynamic>?,
      createdAt: DateTime.parse(json['createdAt'] as String),
      expiresAt: json['expiresAt'] != null
          ? DateTime.parse(json['expiresAt'] as String)
          : null,
      isActive: json['isActive'] as bool? ?? true,
      minConfidence: (json['minConfidence'] as num?)?.toDouble() ?? 0.8,
    );
  }

  /// 转换为JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'type': type.name,
      'latitude': latitude,
      'longitude': longitude,
      'radius': radius,
      'polygonPoints': polygonPoints,
      'polylinePoints': polylinePoints,
      'polylineBuffer': polylineBuffer,
      'triggers': triggers.map((t) => t.name).toList(),
      'dwellTime': dwellTime,
      'merchantId': merchantId,
      'poiId': poiId,
      'metadata': metadata,
      'createdAt': createdAt.toIso8601String(),
      'expiresAt': expiresAt?.toIso8601String(),
      'isActive': isActive,
      'minConfidence': minConfidence,
    };
  }

  /// 复制并修改
  Geofence copyWith({
    String? id,
    String? name,
    GeofenceType? type,
    double? latitude,
    double? longitude,
    double? radius,
    List<Map<String, double>>? polygonPoints,
    List<Map<String, double>>? polylinePoints,
    double? polylineBuffer,
    List<GeofenceEvent>? triggers,
    int? dwellTime,
    String? merchantId,
    String? poiId,
    Map<String, dynamic>? metadata,
    DateTime? createdAt,
    DateTime? expiresAt,
    bool? isActive,
    double? minConfidence,
  }) {
    return Geofence(
      id: id ?? this.id,
      name: name ?? this.name,
      type: type ?? this.type,
      latitude: latitude ?? this.latitude,
      longitude: longitude ?? this.longitude,
      radius: radius ?? this.radius,
      polygonPoints: polygonPoints ?? this.polygonPoints,
      polylinePoints: polylinePoints ?? this.polylinePoints,
      polylineBuffer: polylineBuffer ?? this.polylineBuffer,
      triggers: triggers ?? this.triggers,
      dwellTime: dwellTime ?? this.dwellTime,
      merchantId: merchantId ?? this.merchantId,
      poiId: poiId ?? this.poiId,
      metadata: metadata ?? this.metadata,
      createdAt: createdAt ?? this.createdAt,
      expiresAt: expiresAt ?? this.expiresAt,
      isActive: isActive ?? this.isActive,
      minConfidence: minConfidence ?? this.minConfidence,
    );
  }

  @override
  String toString() {
    return 'Geofence(id: $id, name: $name, type: ${type.name}, lat: $latitude, lng: $longitude)';
  }
}

/// 围栏触发事件数据
class GeofenceTriggerEvent {
  /// 事件ID
  final String id;
  
  /// 围栏ID
  final String geofenceId;
  
  /// 事件类型
  final GeofenceEvent eventType;
  
  /// 触发时间
  final DateTime timestamp;
  
  /// 触发时的位置
  final Position position;
  
  /// 置信度（0.0 - 1.0）
  final double confidence;
  
  /// 围栏状态
  final GeofenceStatus status;
  
  /// 停留时长（毫秒），dwell事件时有效
  final int? dwellDuration;
  
  /// 附加数据
  final Map<String, dynamic>? extraData;

  const GeofenceTriggerEvent({
    required this.id,
    required this.geofenceId,
    required this.eventType,
    required this.timestamp,
    required this.position,
    required this.confidence,
    required this.status,
    this.dwellDuration,
    this.extraData,
  });

  /// 从JSON创建
  factory GeofenceTriggerEvent.fromJson(Map<String, dynamic> json) {
    return GeofenceTriggerEvent(
      id: json['id'] as String,
      geofenceId: json['geofenceId'] as String,
      eventType: GeofenceEvent.values.firstWhere(
        (e) => e.name == json['eventType'],
        orElse: () => GeofenceEvent.enter,
      ),
      timestamp: DateTime.parse(json['timestamp'] as String),
      position: Position(
        latitude: (json['position']['latitude'] as num).toDouble(),
        longitude: (json['position']['longitude'] as num).toDouble(),
        timestamp: DateTime.parse(json['timestamp'] as String),
        accuracy: (json['position']['accuracy'] as num?)?.toDouble() ?? 0,
        altitude: (json['position']['altitude'] as num?)?.toDouble() ?? 0,
        heading: (json['position']['heading'] as num?)?.toDouble() ?? 0,
        speed: (json['position']['speed'] as num?)?.toDouble() ?? 0,
        speedAccuracy: 0,
        altitudeAccuracy: 0,
        headingAccuracy: 0,
      ),
      confidence: (json['confidence'] as num).toDouble(),
      status: GeofenceStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => GeofenceStatus.unknown,
      ),
      dwellDuration: json['dwellDuration'] as int?,
      extraData: json['extraData'] as Map<String, dynamic>?,
    );
  }

  /// 转换为JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'geofenceId': geofenceId,
      'eventType': eventType.name,
      'timestamp': timestamp.toIso8601String(),
      'position': {
        'latitude': position.latitude,
        'longitude': position.longitude,
        'accuracy': position.accuracy,
        'altitude': position.altitude,
        'heading': position.heading,
        'speed': position.speed,
      },
      'confidence': confidence,
      'status': status.name,
      'dwellDuration': dwellDuration,
      'extraData': extraData,
    };
  }

  @override
  String toString() {
    return 'GeofenceTriggerEvent(id: $id, geofenceId: $geofenceId, type: ${eventType.name}, confidence: ${confidence.toStringAsFixed(2)})';
  }
}

/// 围栏监控状态
class GeofenceMonitoringState {
  /// 围栏ID
  final String geofenceId;
  
  /// 当前状态
  final GeofenceStatus status;
  
  /// 最后更新时间
  final DateTime lastUpdateTime;
  
  /// 进入时间
  final DateTime? enterTime;
  
  /// 停留时长（毫秒）
  final int dwellDuration;
  
  /// 最后已知位置
  final Position? lastPosition;
  
  /// 进入次数
  final int enterCount;
  
  /// 连续检测次数（用于置信度计算）
  final int consecutiveDetections;

  const GeofenceMonitoringState({
    required this.geofenceId,
    required this.status,
    required this.lastUpdateTime,
    this.enterTime,
    this.dwellDuration = 0,
    this.lastPosition,
    this.enterCount = 0,
    this.consecutiveDetections = 0,
  });

  /// 从JSON创建
  factory GeofenceMonitoringState.fromJson(Map<String, dynamic> json) {
    return GeofenceMonitoringState(
      geofenceId: json['geofenceId'] as String,
      status: GeofenceStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => GeofenceStatus.unknown,
      ),
      lastUpdateTime: DateTime.parse(json['lastUpdateTime'] as String),
      enterTime: json['enterTime'] != null
          ? DateTime.parse(json['enterTime'] as String)
          : null,
      dwellDuration: json['dwellDuration'] as int? ?? 0,
      lastPosition: json['lastPosition'] != null
          ? Position(
              latitude: (json['lastPosition']['latitude'] as num).toDouble(),
              longitude: (json['lastPosition']['longitude'] as num).toDouble(),
              timestamp: DateTime.now(),
              accuracy: (json['lastPosition']['accuracy'] as num?)?.toDouble() ?? 0,
              altitude: 0,
              heading: 0,
              speed: 0,
              speedAccuracy: 0,
              altitudeAccuracy: 0,
              headingAccuracy: 0,
            )
          : null,
      enterCount: json['enterCount'] as int? ?? 0,
      consecutiveDetections: json['consecutiveDetections'] as int? ?? 0,
    );
  }

  /// 转换为JSON
  Map<String, dynamic> toJson() {
    return {
      'geofenceId': geofenceId,
      'status': status.name,
      'lastUpdateTime': lastUpdateTime.toIso8601String(),
      'enterTime': enterTime?.toIso8601String(),
      'dwellDuration': dwellDuration,
      'lastPosition': lastPosition != null
          ? {
              'latitude': lastPosition!.latitude,
              'longitude': lastPosition!.longitude,
              'accuracy': lastPosition!.accuracy,
            }
          : null,
      'enterCount': enterCount,
      'consecutiveDetections': consecutiveDetections,
    };
  }

  /// 复制并修改
  GeofenceMonitoringState copyWith({
    String? geofenceId,
    GeofenceStatus? status,
    DateTime? lastUpdateTime,
    DateTime? enterTime,
    int? dwellDuration,
    Position? lastPosition,
    int? enterCount,
    int? consecutiveDetections,
  }) {
    return GeofenceMonitoringState(
      geofenceId: geofenceId ?? this.geofenceId,
      status: status ?? this.status,
      lastUpdateTime: lastUpdateTime ?? this.lastUpdateTime,
      enterTime: enterTime ?? this.enterTime,
      dwellDuration: dwellDuration ?? this.dwellDuration,
      lastPosition: lastPosition ?? this.lastPosition,
      enterCount: enterCount ?? this.enterCount,
      consecutiveDetections: consecutiveDetections ?? this.consecutiveDetections,
    );
  }
}
