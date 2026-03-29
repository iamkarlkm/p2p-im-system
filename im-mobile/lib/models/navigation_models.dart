import 'package:latlong2/latlong.dart';

/// 导航相关数据模型
/// Navigation Data Models

/// 路线规划
class RoutePlan {
  final int id;
  final String name;
  final LatLng origin;
  final String originName;
  final LatLng destination;
  final String destinationName;
  final NavMode navMode;
  final RouteStrategy strategy;
  final int distance;
  final int duration;
  final String distanceText;
  final String durationText;
  final String trafficCondition;
  final String trafficConditionText;
  final double tollFee;
  final List<LatLng> routePoints;
  final List<RouteStep> steps;
  final List<LatLng> waypoints;

  RoutePlan({
    required this.id,
    this.name = '',
    required this.origin,
    this.originName = '',
    required this.destination,
    this.destinationName = '',
    required this.navMode,
    required this.strategy,
    required this.distance,
    required this.duration,
    required this.distanceText,
    required this.durationText,
    required this.trafficCondition,
    required this.trafficConditionText,
    this.tollFee = 0.0,
    required this.routePoints,
    required this.steps,
    this.waypoints = const [],
  });

  factory RoutePlan.fromJson(Map<String, dynamic> json) {
    return RoutePlan(
      id: json['routeId'] ?? 0,
      name: json['routeName'] ?? '',
      origin: LatLng(
        json['origin']['lat'] ?? 0.0,
        json['origin']['lng'] ?? 0.0,
      ),
      originName: json['origin']['name'] ?? '',
      destination: LatLng(
        json['destination']['lat'] ?? 0.0,
        json['destination']['lng'] ?? 0.0,
      ),
      destinationName: json['destination']['name'] ?? '',
      navMode: NavMode.values.firstWhere(
        (e) => e.value == json['navMode'],
        orElse: () => NavMode.driving,
      ),
      strategy: RouteStrategy.values.firstWhere(
        (e) => e.value == json['routeStrategy'],
        orElse: () => RouteStrategy.fastest,
      ),
      distance: json['distance'] ?? 0,
      duration: json['duration'] ?? 0,
      distanceText: json['distanceText'] ?? '',
      durationText: json['durationText'] ?? '',
      trafficCondition: json['trafficCondition'] ?? 'SMOOTH',
      trafficConditionText: _getTrafficText(json['trafficCondition']),
      tollFee: (json['tollFee'] ?? 0).toDouble(),
      routePoints: (json['routePoints'] as List? ?? [])
          .map((p) => LatLng(p[1], p[0]))
          .toList(),
      steps: (json['steps'] as List? ?? [])
          .map((s) => RouteStep.fromJson(s))
          .toList(),
      waypoints: (json['waypoints'] as List? ?? [])
          .map((p) => LatLng(p['lat'], p['lng']))
          .toList(),
    );
  }

  static String _getTrafficText(String? condition) {
    switch (condition) {
      case 'SMOOTH':
        return '畅通';
      case 'SLOW':
        return '缓行';
      case 'CONGESTED':
        return '拥堵';
      case 'SEVERE':
        return '严重拥堵';
      default:
        return '畅通';
    }
  }
}

/// 路线步骤
class RouteStep {
  final int index;
  final String instruction;
  final int distance;
  final int duration;
  final String action;
  final String roadName;
  final List<LatLng> points;
  final String voiceText;
  final String iconType;

  String get distanceText => _formatDistance(distance);

  RouteStep({
    required this.index,
    required this.instruction,
    required this.distance,
    required this.duration,
    required this.action,
    this.roadName = '',
    this.points = const [],
    this.voiceText = '',
    this.iconType = '',
  });

  factory RouteStep.fromJson(Map<String, dynamic> json) {
    return RouteStep(
      index: json['index'] ?? 0,
      instruction: json['instruction'] ?? '',
      distance: json['distance'] ?? 0,
      duration: json['duration'] ?? 0,
      action: json['action'] ?? 'straight',
      roadName: json['roadName'] ?? '',
      points: (json['points'] as List? ?? [])
          .map((p) => LatLng(p[1], p[0]))
          .toList(),
      voiceText: json['voiceText'] ?? '',
      iconType: json['iconType'] ?? '',
    );
  }

  String _formatDistance(int meters) {
    if (meters < 1000) {
      return '${meters}米';
    } else {
      return '${(meters / 1000).toStringAsFixed(1)}公里';
    }
  }
}

/// 导航状态
class NavigationStatus {
  final int sessionId;
  final int routeId;
  final CurrentLocation currentLocation;
  final CurrentStep? currentStep;
  final NextStep? nextStep;
  final String status;
  final bool isOffRoute;
  final int remainingDistance;
  final String remainingDistanceText;
  final int remainingDuration;
  final String remainingDurationText;
  final String currentRoad;
  final String voiceText;
  final bool needReroute;
  final List<TrafficSegment> trafficSegments;

  NavigationStatus({
    required this.sessionId,
    required this.routeId,
    required this.currentLocation,
    this.currentStep,
    this.nextStep,
    required this.status,
    this.isOffRoute = false,
    required this.remainingDistance,
    required this.remainingDistanceText,
    required this.remainingDuration,
    required this.remainingDurationText,
    this.currentRoad = '',
    this.voiceText = '',
    this.needReroute = false,
    this.trafficSegments = const [],
  });

  factory NavigationStatus.fromJson(Map<String, dynamic> json) {
    return NavigationStatus(
      sessionId: json['sessionId'] ?? 0,
      routeId: json['routeId'] ?? 0,
      currentLocation: CurrentLocation.fromJson(json['currentLocation'] ?? {}),
      currentStep: json['currentStep'] != null
          ? CurrentStep.fromJson(json['currentStep'])
          : null,
      nextStep: json['nextStep'] != null
          ? NextStep.fromJson(json['nextStep'])
          : null,
      status: json['status'] ?? 'NAVIGATING',
      isOffRoute: json['isOffRoute'] ?? false,
      remainingDistance: json['remainingDistance'] ?? 0,
      remainingDistanceText: json['remainingDistanceText'] ?? '',
      remainingDuration: json['remainingDuration'] ?? 0,
      remainingDurationText: json['remainingDurationText'] ?? '',
      currentRoad: json['currentRoad'] ?? '',
      voiceText: json['voiceText'] ?? '',
      needReroute: json['needReroute'] ?? false,
      trafficSegments: (json['trafficSegments'] as List? ?? [])
          .map((s) => TrafficSegment.fromJson(s))
          .toList(),
    );
  }
}

/// 当前位置
class CurrentLocation {
  final double lng;
  final double lat;
  final int speed;
  final int heading;
  final String roadName;

  CurrentLocation({
    required this.lng,
    required this.lat,
    this.speed = 0,
    this.heading = 0,
    this.roadName = '',
  });

  factory CurrentLocation.fromJson(Map<String, dynamic> json) {
    return CurrentLocation(
      lng: (json['lng'] ?? 0).toDouble(),
      lat: (json['lat'] ?? 0).toDouble(),
      speed: json['speed'] ?? 0,
      heading: json['heading'] ?? 0,
      roadName: json['roadName'] ?? '',
    );
  }
}

/// 当前步骤
class CurrentStep {
  final int index;
  final String instruction;
  final int remainingDistance;
  final String remainingDistanceText;
  final String voiceText;
  final String action;
  final String roadName;

  CurrentStep({
    required this.index,
    required this.instruction,
    required this.remainingDistance,
    required this.remainingDistanceText,
    required this.voiceText,
    required this.action,
    this.roadName = '',
  });

  factory CurrentStep.fromJson(Map<String, dynamic> json) {
    return CurrentStep(
      index: json['index'] ?? 0,
      instruction: json['instruction'] ?? '',
      remainingDistance: json['remainingDistance'] ?? 0,
      remainingDistanceText: json['remainingDistanceText'] ?? '',
      voiceText: json['voiceText'] ?? '',
      action: json['action'] ?? 'straight',
      roadName: json['roadName'] ?? '',
    );
  }
}

/// 下一步
class NextStep {
  final int index;
  final String instruction;
  final int distance;
  final String action;
  final String roadName;

  NextStep({
    required this.index,
    required this.instruction,
    required this.distance,
    required this.action,
    this.roadName = '',
  });

  factory NextStep.fromJson(Map<String, dynamic> json) {
    return NextStep(
      index: json['index'] ?? 0,
      instruction: json['instruction'] ?? '',
      distance: json['distance'] ?? 0,
      action: json['action'] ?? 'straight',
      roadName: json['roadName'] ?? '',
    );
  }
}

/// 拥堵路段
class TrafficSegment {
  final int startIndex;
  final int endIndex;
  final String level;
  final int length;
  final int speed;

  TrafficSegment({
    required this.startIndex,
    required this.endIndex,
    required this.level,
    required this.length,
    required this.speed,
  });

  factory TrafficSegment.fromJson(Map<String, dynamic> json) {
    return TrafficSegment(
      startIndex: json['startIndex'] ?? 0,
      endIndex: json['endIndex'] ?? 0,
      level: json['level'] ?? 'SMOOTH',
      length: json['length'] ?? 0,
      speed: json['speed'] ?? 0,
    );
  }
}

/// 途经点
class Waypoint {
  final double lng;
  final double lat;
  final String name;

  Waypoint({
    required this.lng,
    required this.lat,
    this.name = '',
  });

  Map<String, dynamic> toJson() => {
        'lng': lng,
        'lat': lat,
        'name': name,
      };
}

/// 导航模式
enum NavMode {
  driving('DRIVING'),
  walking('WALKING'),
  riding('RIDING'),
  transit('TRANSIT'),
  truck('TRUCK');

  final String value;
  const NavMode(this.value);
}

/// 路线策略
enum RouteStrategy {
  fastest('FASTEST'),
  shortest('SHORTEST'),
  avoidCongestion('AVOID_CONGESTION'),
  economic('ECONOMIC'),
  highwayFirst('HIGHWAY_FIRST');

  final String value;
  const RouteStrategy(this.value);
}
