import 'dart:convert';

/// 地理围栏模型类
/// 智能到店服务核心数据模型
/// 
/// @author IM Development Team
/// @since 2026-03-28
class GeofenceZone {
  final String? id;
  final String name;
  final String? description;
  final GeofenceType type;
  final int level;
  final String? parentId;
  final String? poiId;
  final String? merchantId;
  final double? centerLongitude;
  final double? centerLatitude;
  final int? radius;
  final List<Map<String, double>>? polygonPoints;
  final String? geoHash;
  final double? area;
  final double? perimeter;
  final TriggerCondition triggerCondition;
  final int? dwellTime;
  final DateTime? effectiveStartTime;
  final DateTime? effectiveEndTime;
  final String? businessHours;
  final String? effectiveWeekdays;
  final bool? holidayEffective;
  final int? userLevelLimit;
  final int? minVisitCount;
  final int? maxTriggerCount;
  final int? cooldownHours;
  final bool enabled;
  final GeofenceStatus status;
  final GeofenceSource source;
  final DateTime? createTime;
  final DateTime? updateTime;
  final List<GeofenceZone>? subGeofences;

  GeofenceZone({
    this.id,
    required this.name,
    this.description,
    required this.type,
    required this.level,
    this.parentId,
    this.poiId,
    this.merchantId,
    this.centerLongitude,
    this.centerLatitude,
    this.radius,
    this.polygonPoints,
    this.geoHash,
    this.area,
    this.perimeter,
    this.triggerCondition = TriggerCondition.enter,
    this.dwellTime,
    this.effectiveStartTime,
    this.effectiveEndTime,
    this.businessHours,
    this.effectiveWeekdays,
    this.holidayEffective,
    this.userLevelLimit,
    this.minVisitCount,
    this.maxTriggerCount,
    this.cooldownHours,
    this.enabled = true,
    this.status = GeofenceStatus.active,
    this.source = GeofenceSource.system,
    this.createTime,
    this.updateTime,
    this.subGeofences,
  });

  factory GeofenceZone.fromJson(Map<String, dynamic> json) {
    return GeofenceZone(
      id: json['id']?.toString(),
      name: json['name'] ?? '',
      description: json['description'],
      type: GeofenceType.values.firstWhere(
        (e) => e.name.toUpperCase() == (json['type'] ?? 'CIRCLE'),
        orElse: () => GeofenceType.circle,
      ),
      level: json['level'] ?? 3,
      parentId: json['parentId']?.toString(),
      poiId: json['poiId']?.toString(),
      merchantId: json['merchantId']?.toString(),
      centerLongitude: json['centerLongitude']?.toDouble(),
      centerLatitude: json['centerLatitude']?.toDouble(),
      radius: json['radius'],
      polygonPoints: json['polygonPoints'] != null
          ? List<Map<String, double>>.from(json['polygonPoints'])
          : null,
      geoHash: json['geoHash'],
      area: json['area']?.toDouble(),
      perimeter: json['perimeter']?.toDouble(),
      triggerCondition: TriggerCondition.values.firstWhere(
        (e) => e.name.toUpperCase() == (json['triggerCondition'] ?? 'ENTER'),
        orElse: () => TriggerCondition.enter,
      ),
      dwellTime: json['dwellTime'],
      effectiveStartTime: json['effectiveStartTime'] != null
          ? DateTime.parse(json['effectiveStartTime'])
          : null,
      effectiveEndTime: json['effectiveEndTime'] != null
          ? DateTime.parse(json['effectiveEndTime'])
          : null,
      businessHours: json['businessHours'],
      effectiveWeekdays: json['effectiveWeekdays'],
      holidayEffective: json['holidayEffective'],
      userLevelLimit: json['userLevelLimit'],
      minVisitCount: json['minVisitCount'],
      maxTriggerCount: json['maxTriggerCount'],
      cooldownHours: json['cooldownHours'],
      enabled: json['enabled'] ?? true,
      status: GeofenceStatus.values.firstWhere(
        (e) => e.name.toUpperCase() == (json['status'] ?? 'ACTIVE'),
        orElse: () => GeofenceStatus.active,
      ),
      source: GeofenceSource.values.firstWhere(
        (e) => e.name.toUpperCase() == (json['source'] ?? 'SYSTEM'),
        orElse: () => GeofenceSource.system,
      ),
      createTime: json['createTime'] != null
          ? DateTime.parse(json['createTime'])
          : null,
      updateTime: json['updateTime'] != null
          ? DateTime.parse(json['updateTime'])
          : null,
      subGeofences: json['subGeofences'] != null
          ? (json['subGeofences'] as List)
              .map((e) => GeofenceZone.fromJson(e))
              .toList()
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'type': type.name.toUpperCase(),
      'level': level,
      'parentId': parentId,
      'poiId': poiId,
      'merchantId': merchantId,
      'centerLongitude': centerLongitude,
      'centerLatitude': centerLatitude,
      'radius': radius,
      'polygonPoints': polygonPoints,
      'geoHash': geoHash,
      'area': area,
      'perimeter': perimeter,
      'triggerCondition': triggerCondition.name.toUpperCase(),
      'dwellTime': dwellTime,
      'effectiveStartTime': effectiveStartTime?.toIso8601String(),
      'effectiveEndTime': effectiveEndTime?.toIso8601String(),
      'businessHours': businessHours,
      'effectiveWeekdays': effectiveWeekdays,
      'holidayEffective': holidayEffective,
      'userLevelLimit': userLevelLimit,
      'minVisitCount': minVisitCount,
      'maxTriggerCount': maxTriggerCount,
      'cooldownHours': cooldownHours,
      'enabled': enabled,
      'status': status.name.toUpperCase(),
      'source': source.name.toUpperCase(),
      'createTime': createTime?.toIso8601String(),
      'updateTime': updateTime?.toIso8601String(),
    };
  }

  @override
  String toString() => jsonEncode(toJson());
}

/// 围栏类型枚举
enum GeofenceType {
  circle,    // 圆形
  polygon,   // 多边形
  rectangle, // 矩形
  irregular, // 不规则
}

/// 触发条件枚举
enum TriggerCondition {
  enter, // 进入
  exit,  // 离开
  dwell, // 停留
}

/// 围栏状态枚举
enum GeofenceStatus {
  active,  // 激活
  paused,  // 暂停
  expired, // 过期
}

/// 围栏来源枚举
enum GeofenceSource {
  system,  // 系统自动
  merchant,// 商户创建
  admin,   // 管理员
}
