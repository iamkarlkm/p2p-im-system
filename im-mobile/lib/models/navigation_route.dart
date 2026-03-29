import 'package:freezed_annotation/freezed_annotation.dart';

part 'navigation_route.freezed.dart';
part 'navigation_route.g.dart';

/// 导航路线模型类
/// 存储路线规划结果和导航相关信息
@freezed
class NavigationRoute with _$NavigationRoute {
  const factory NavigationRoute({
    required int routeId,
    required String routeName,
    required LocationInfo start,
    required LocationInfo end,
    required String travelMode,
    required String routeStrategy,
    required int totalDistance,
    required String totalDistanceText,
    required int estimatedDuration,
    required String estimatedDurationText,
    required String estimatedArrivalTime,
    required double estimatedCost,
    required String routePolyline,
    required List<RouteStep> steps,
    List<RouteSegment>? segments,
    List<WaypointInfo>? waypoints,
    TrafficInfo? trafficInfo,
    TollInfo? tollInfo,
    RestrictionInfo? restrictionInfo,
    required List<String> tags,
    bool? isFavorite,
    int? usageCount,
    DateTime? lastUsedTime,
  }) = _NavigationRoute;

  factory NavigationRoute.fromJson(Map<String, dynamic> json) =>
      _$NavigationRouteFromJson(json);

  const NavigationRoute._();

  /// 获取路线的主要标签描述
  String get mainTag => tags.isNotEmpty ? tags.first : '路线';

  /// 是否包含收费路段
  bool get hasToll => tollInfo?.totalTollFee != null && tollInfo!.totalTollFee > 0;

  /// 是否有限行
  bool get hasRestriction => restrictionInfo?.hasRestriction ?? false;

  /// 是否有路况信息
  bool get hasTrafficInfo => trafficInfo != null;

  /// 路况状态文本
  String get trafficStatusText => trafficInfo?.overallStatusText ?? '未知';
}

/// 位置信息模型
@freezed
class LocationInfo with _$LocationInfo {
  const factory LocationInfo({
    int? poiId,
    required String name,
    required double longitude,
    required double latitude,
    String? address,
  }) = _LocationInfo;

  factory LocationInfo.fromJson(Map<String, dynamic> json) =>
      _$LocationInfoFromJson(json);
}

/// 导航步骤模型
@freezed
class RouteStep with _$RouteStep {
  const factory RouteStep({
    required int stepIndex,
    required String instruction,
    required int distance,
    String? distanceText,
    required int duration,
    String? durationText,
    required String turnType,
    String? roadName,
    required String actionIcon,
    String? polyline,
  }) = _RouteStep;

  factory RouteStep.fromJson(Map<String, dynamic> json) =>
      _$RouteStepFromJson(json);

  const RouteStep._();

  /// 是否为起点
  bool get isStart => turnType == 'START';

  /// 是否为终点
  bool get isEnd => turnType == 'END';

  /// 是否为途经点
  bool get isWaypoint => turnType == 'WAYPOINT';
}

/// 路线分段模型
@freezed
class RouteSegment with _$RouteSegment {
  const factory RouteSegment({
    required int segmentIndex,
    required String segmentType,
    required LocationInfo start,
    required LocationInfo end,
    required int distance,
    required int duration,
    String? roadType,
    String? roadName,
    required String trafficStatus,
    required bool tollRoad,
    required double tollFee,
    required String turnType,
    required String turnInstruction,
  }) = _RouteSegment;

  factory RouteSegment.fromJson(Map<String, dynamic> json) =>
      _$RouteSegmentFromJson(json);

  const RouteSegment._();

  /// 路况状态颜色
  String get trafficColor {
    switch (trafficStatus) {
      case 'SMOOTH':
        return '#4CAF50'; // 绿色
      case 'SLOW':
        return '#FF9800'; // 橙色
      case 'CONGESTED':
        return '#F44336'; // 红色
      case 'SEVERE':
        return '#9C27B0'; // 紫色
      default:
        return '#757575'; // 灰色
    }
  }
}

/// 途经点信息模型
@freezed
class WaypointInfo with _$WaypointInfo {
  const factory WaypointInfo({
    required int index,
    int? poiId,
    required String name,
    required double longitude,
    required double latitude,
    required int distanceFromStart,
    required int estimatedArrivalTime,
  }) = _WaypointInfo;

  factory WaypointInfo.fromJson(Map<String, dynamic> json) =>
      _$WaypointInfoFromJson(json);
}

/// 实时路况信息模型
@freezed
class TrafficInfo with _$TrafficInfo {
  const factory TrafficInfo({
    required int smoothDistance,
    required int slowDistance,
    required int congestedDistance,
    required int severelyCongestedDistance,
    required String overallStatus,
    required String overallStatusText,
    required String updateTime,
  }) = _TrafficInfo;

  factory TrafficInfo.fromJson(Map<String, dynamic> json) =>
      _$TrafficInfoFromJson(json);

  const TrafficInfo._();

  /// 畅通路段占比
  double get smoothRatio {
    final total = smoothDistance + slowDistance + congestedDistance + severelyCongestedDistance;
    if (total == 0) return 0;
    return smoothDistance / total;
  }

  /// 拥堵路段占比
  double get congestedRatio {
    final total = smoothDistance + slowDistance + congestedDistance + severelyCongestedDistance;
    if (total == 0) return 0;
    return (congestedDistance + severelyCongestedDistance) / total;
  }
}

/// 收费信息模型
@freezed
class TollInfo with _$TollInfo {
  const factory TollInfo({
    required int tollCount,
    required double totalTollFee,
    List<TollGate>? tollGates,
  }) = _TollInfo;

  factory TollInfo.fromJson(Map<String, dynamic> json) =>
      _$TollInfoFromJson(json);
}

/// 收费站模型
@freezed
class TollGate with _$TollGate {
  const factory TollGate({
    required String name,
    required double fee,
    required double longitude,
    required double latitude,
  }) = _TollGate;

  factory TollGate.fromJson(Map<String, dynamic> json) =>
      _$TollGateFromJson(json);
}

/// 限行信息模型
@freezed
class RestrictionInfo with _$RestrictionInfo {
  const factory RestrictionInfo({
    required bool hasRestriction,
    String? restrictionType,
    String? restrictionDesc,
    List<String>? restrictedRoads,
  }) = _RestrictionInfo;

  factory RestrictionInfo.fromJson(Map<String, dynamic> json) =>
      _$RestrictionInfoFromJson(json);
}

/// 路线规划请求模型
@freezed
class RouteRequest with _$RouteRequest {
  const factory RouteRequest({
    required double startLongitude,
    required double startLatitude,
    String? startName,
    required double endLongitude,
    required double endLatitude,
    String? endName,
    required TravelMode travelMode,
    RouteStrategy? routeStrategy,
    List<WaypointRequest>? waypoints,
    bool? avoidHighway,
    bool? avoidToll,
    bool? avoidCongestion,
    String? plateNumber,
    int? userId,
  }) = _RouteRequest;

  factory RouteRequest.fromJson(Map<String, dynamic> json) =>
      _$RouteRequestFromJson(json);
}

/// 途经点请求模型
@freezed
class WaypointRequest with _$WaypointRequest {
  const factory WaypointRequest({
    required double longitude,
    required double latitude,
    String? name,
    bool? required,
    int? stayDuration,
  }) = _WaypointRequest;

  factory WaypointRequest.fromJson(Map<String, dynamic> json) =>
      _$WaypointRequestFromJson(json);
}

/// 出行方式枚举
enum TravelMode {
  drive('DRIVE', '驾车'),
  walk('WALK', '步行'),
  ride('RIDE', '骑行'),
  bus('BUS', '公交'),
  truck('TRUCK', '货车');

  final String code;
  final String label;

  const TravelMode(this.code, this.label);

  static TravelMode fromCode(String code) {
    return TravelMode.values.firstWhere(
      (mode) => mode.code == code,
      orElse: () => TravelMode.drive,
    );
  }

  /// 图标
  String get icon {
    switch (this) {
      case TravelMode.drive:
        return '🚗';
      case TravelMode.walk:
        return '🚶';
      case TravelMode.ride:
        return '🚴';
      case TravelMode.bus:
        return '🚌';
      case TravelMode.truck:
        return '🚚';
    }
  }

  /// 是否支持实时路况
  bool get supportsTraffic => this == TravelMode.drive || this == TravelMode.truck;

  /// 默认速度(m/s)
  int get defaultSpeed {
    switch (this) {
      case TravelMode.drive:
        return 12;
      case TravelMode.truck:
        return 10;
      case TravelMode.bus:
        return 8;
      case TravelMode.ride:
        return 4;
      case TravelMode.walk:
        return 1;
    }
  }
}

/// 路线策略枚举
enum RouteStrategy {
  fastest('FASTEST', '最快'),
  shortest('SHORTEST', '最短'),
  avoidTraffic('AVOID_TRAFFIC', '避堵'),
  economic('ECONOMIC', '经济');

  final String code;
  final String label;

  const RouteStrategy(this.code, this.label);

  static RouteStrategy fromCode(String code) {
    return RouteStrategy.values.firstWhere(
      (strategy) => strategy.code == code,
      orElse: () => RouteStrategy.fastest,
    );
  }
}
