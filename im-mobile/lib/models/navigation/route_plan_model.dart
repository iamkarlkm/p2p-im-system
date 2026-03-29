import 'package:im_mobile/services/navigation/navigation_service.dart';

/// 路线规划模型
/// 
/// 包含完整路线信息：步骤、距离、时间、路况等
class RoutePlanModel {
  /// 路线唯一标识
  final String id;
  
  /// 起点坐标
  final LatLng origin;
  
  /// 终点坐标
  final LatLng destination;
  
  /// 导航步骤列表
  final List<RouteStep> steps;
  
  /// 总距离（米）
  final double totalDistance;
  
  /// 预计总时间（秒）
  final int totalDuration;
  
  /// 过路费（元）
  final double tollCost;
  
  /// 路况条件
  final TrafficCondition trafficCondition;
  
  /// 路线编码（用于地图绘制）
  final String polyline;
  
  /// 是否为多目的地路线
  final bool isMultiStop;
  
  /// 途经点列表
  final List<LatLng>? waypoints;
  
  /// 路线标签（推荐、最快、最短等）
  final List<String> tags;
  
  /// 路线描述
  final String? description;
  
  /// 限行信息
  final List<String>? restrictions;
  
  /// 创建时间
  final DateTime createdAt;

  RoutePlanModel({
    required this.id,
    required this.origin,
    required this.destination,
    required this.steps,
    required this.totalDistance,
    required this.totalDuration,
    this.tollCost = 0.0,
    this.trafficCondition = TrafficCondition.smooth,
    required this.polyline,
    this.isMultiStop = false,
    this.waypoints,
    this.tags = const [],
    this.description,
    this.restrictions,
    DateTime? createdAt,
  }) : this.createdAt = createdAt ?? DateTime.now();

  /// 格式化距离显示
  String get formattedDistance {
    if (totalDistance >= 1000) {
      return '${(totalDistance / 1000).toStringAsFixed(1)}公里';
    } else {
      return '${totalDistance.round()}米';
    }
  }

  /// 格式化时间显示
  String get formattedDuration {
    if (totalDuration >= 3600) {
      final hours = totalDuration ~/ 3600;
      final minutes = (totalDuration % 3600) ~/ 60;
      return '${hours}小时${minutes > 0 ? '$minutes分钟' : ''}';
    } else if (totalDuration >= 60) {
      final minutes = totalDuration ~/ 60;
      return '$minutes分钟';
    } else {
      return '$totalDuration秒';
    }
  }

  /// 预计到达时间
  DateTime get estimatedArrivalTime {
    return DateTime.now().add(Duration(seconds: totalDuration));
  }

  /// 格式化到达时间
  String get formattedArrivalTime {
    final arrival = estimatedArrivalTime;
    final hour = arrival.hour.toString().padLeft(2, '0');
    final minute = arrival.minute.toString().padLeft(2, '0');
    return '$hour:$minute';
  }

  /// 平均速度（km/h）
  double get averageSpeed {
    return (totalDistance / 1000) / (totalDuration / 3600);
  }

  /// 是否有收费站
  bool get hasTolls => tollCost > 0;

  /// 路况图标
  String get trafficIcon {
    switch (trafficCondition) {
      case TrafficCondition.smooth:
        return '✅';
      case TrafficCondition.moderate:
        return '⚠️';
      case TrafficCondition.congested:
        return '🚧';
      case TrafficCondition.blocked:
        return '⛔';
    }
  }

  /// 路况颜色
  String get trafficColor {
    switch (trafficCondition) {
      case TrafficCondition.smooth:
        return '#4CAF50'; // 绿色
      case TrafficCondition.moderate:
        return '#FFC107'; // 黄色
      case TrafficCondition.congested:
        return '#FF9800'; // 橙色
      case TrafficCondition.blocked:
        return '#F44336'; // 红色
    }
  }

  /// 主路名（第一条主要道路）
  String? get mainRoadName {
    for (final step in steps) {
      if (step.roadName != null && step.roadName!.isNotEmpty) {
        return step.roadName;
      }
    }
    return null;
  }

  /// 路线摘要（用于列表展示）
  String get summary {
    final roads = <String>[];
    for (final step in steps) {
      if (step.roadName != null && 
          step.roadName!.isNotEmpty && 
          !roads.contains(step.roadName)) {
        roads.add(step.roadName!);
      }
    }
    
    if (roads.isEmpty) return '全程${formattedDistance}';
    if (roads.length <= 2) return roads.join(' → ');
    return '${roads.first} → ... → ${roads.last}';
  }

  /// 转换为JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'origin': {'lat': origin.latitude, 'lng': origin.longitude},
      'destination': {'lat': destination.latitude, 'lng': destination.longitude},
      'steps': steps.map((s) => s.toJson()).toList(),
      'totalDistance': totalDistance,
      'totalDuration': totalDuration,
      'tollCost': tollCost,
      'trafficCondition': trafficCondition.name,
      'polyline': polyline,
      'isMultiStop': isMultiStop,
      'waypoints': waypoints?.map((w) => {'lat': w.latitude, 'lng': w.longitude}).toList(),
      'tags': tags,
      'description': description,
      'restrictions': restrictions,
      'createdAt': createdAt.toIso8601String(),
    };
  }

  /// 从JSON创建
  factory RoutePlanModel.fromJson(Map<String, dynamic> json) {
    return RoutePlanModel(
      id: json['id'],
      origin: LatLng(json['origin']['lat'], json['origin']['lng']),
      destination: LatLng(json['destination']['lat'], json['destination']['lng']),
      steps: (json['steps'] as List).map((s) => RouteStep.fromJson(s)).toList(),
      totalDistance: json['totalDistance'].toDouble(),
      totalDuration: json['totalDuration'],
      tollCost: json['tollCost']?.toDouble() ?? 0.0,
      trafficCondition: TrafficCondition.values.byName(json['trafficCondition']),
      polyline: json['polyline'],
      isMultiStop: json['isMultiStop'] ?? false,
      waypoints: json['waypoints'] != null
          ? (json['waypoints'] as List)
              .map((w) => LatLng(w['lat'], w['lng']))
              .toList()
          : null,
      tags: List<String>.from(json['tags'] ?? []),
      description: json['description'],
      restrictions: json['restrictions'] != null
          ? List<String>.from(json['restrictions'])
          : null,
      createdAt: DateTime.parse(json['createdAt']),
    );
  }

  /// 复制并修改
  RoutePlanModel copyWith({
    String? id,
    LatLng? origin,
    LatLng? destination,
    List<RouteStep>? steps,
    double? totalDistance,
    int? totalDuration,
    double? tollCost,
    TrafficCondition? trafficCondition,
    String? polyline,
    bool? isMultiStop,
    List<LatLng>? waypoints,
    List<String>? tags,
    String? description,
    List<String>? restrictions,
  }) {
    return RoutePlanModel(
      id: id ?? this.id,
      origin: origin ?? this.origin,
      destination: destination ?? this.destination,
      steps: steps ?? this.steps,
      totalDistance: totalDistance ?? this.totalDistance,
      totalDuration: totalDuration ?? this.totalDuration,
      tollCost: tollCost ?? this.tollCost,
      trafficCondition: trafficCondition ?? this.trafficCondition,
      polyline: polyline ?? this.polyline,
      isMultiStop: isMultiStop ?? this.isMultiStop,
      waypoints: waypoints ?? this.waypoints,
      tags: tags ?? this.tags,
      description: description ?? this.description,
      restrictions: restrictions ?? this.restrictions,
      createdAt: createdAt,
    );
  }

  @override
  String toString() {
    return 'RoutePlanModel(id: $id, distance: $formattedDistance, duration: $formattedDuration)';
  }
}

/// 导航步骤
class RouteStep {
  /// 转向指示
  final String instruction;
  
  /// 步骤距离（米）
  final double distance;
  
  /// 步骤时间（秒）
  final int duration;
  
  /// 起点坐标
  final LatLng startLocation;
  
  /// 终点坐标
  final LatLng endLocation;
  
  /// 朝向角度（0-360）
  final double heading;
  
  /// 道路名称
  final String? roadName;
  
  /// 动作类型
  final RouteAction action;
  
  /// 转向图标URL
  final String? iconUrl;
  
  /// 辅助动作（如：靠左行驶）
  final String? auxiliaryAction;
  
  /// 路线编码
  final String? polyline;
  
  /// 是否已播报200米提示
  bool announced200m = false;
  
  /// 是否已播报50米提示
  bool announced50m = false;

  RouteStep({
    required this.instruction,
    required this.distance,
    required this.duration,
    required this.startLocation,
    required this.endLocation,
    required this.heading,
    this.roadName,
    required this.action,
    this.iconUrl,
    this.auxiliaryAction,
    this.polyline,
  });

  /// 格式化距离
  String get formattedDistance {
    if (distance >= 1000) {
      return '${(distance / 1000).toStringAsFixed(1)}公里';
    } else {
      return '${distance.round()}米';
    }
  }

  /// 动作图标
  String get actionIcon {
    switch (action) {
      case RouteAction.start:
        return '🚀';
      case RouteAction.arrive:
        return '🏁';
      case RouteAction.straight:
        return '⬆️';
      case RouteAction.turnLeft:
        return '⬅️';
      case RouteAction.turnRight:
        return '➡️';
      case RouteAction.turnSlightLeft:
        return '↖️';
      case RouteAction.turnSlightRight:
        return '↗️';
      case RouteAction.turnSharpLeft:
        return '⬅️⬅️';
      case RouteAction.turnSharpRight:
        return '➡️➡️';
      case RouteAction.uTurn:
        return '↩️';
      case RouteAction.merge:
        return '🔀';
      case RouteAction.rampLeft:
        return '🛣️⬅️';
      case RouteAction.rampRight:
        return '🛣️➡️';
      case RouteAction.ferry:
        return '⛴️';
      case RouteAction.roundaboutLeft:
        return '🔄⬅️';
      case RouteAction.roundaboutRight:
        return '🔄➡️';
      case RouteAction.keepLeft:
        return '⬅️↕️';
      case RouteAction.keepRight:
        return '↕️➡️';
    }
  }

  /// 动作描述
  String get actionDescription {
    switch (action) {
      case RouteAction.start:
        return '出发';
      case RouteAction.arrive:
        return '到达';
      case RouteAction.straight:
        return '直行';
      case RouteAction.turnLeft:
        return '左转';
      case RouteAction.turnRight:
        return '右转';
      case RouteAction.turnSlightLeft:
        return '稍向左转';
      case RouteAction.turnSlightRight:
        return '稍向右转';
      case RouteAction.turnSharpLeft:
        return '向左急转';
      case RouteAction.turnSharpRight:
        return '向右急转';
      case RouteAction.uTurn:
        return '掉头';
      case RouteAction.merge:
        return '并线';
      case RouteAction.rampLeft:
        return '左侧匝道';
      case RouteAction.rampRight:
        return '右侧匝道';
      case RouteAction.ferry:
        return '乘坐轮渡';
      case RouteAction.roundaboutLeft:
        return '环岛左转出口';
      case RouteAction.roundaboutRight:
        return '环岛右转出口';
      case RouteAction.keepLeft:
        return '靠左行驶';
      case RouteAction.keepRight:
        return '靠右行驶';
    }
  }

  Map<String, dynamic> toJson() {
    return {
      'instruction': instruction,
      'distance': distance,
      'duration': duration,
      'startLocation': {'lat': startLocation.latitude, 'lng': startLocation.longitude},
      'endLocation': {'lat': endLocation.latitude, 'lng': endLocation.longitude},
      'heading': heading,
      'roadName': roadName,
      'action': action.name,
      'iconUrl': iconUrl,
      'auxiliaryAction': auxiliaryAction,
      'polyline': polyline,
    };
  }

  factory RouteStep.fromJson(Map<String, dynamic> json) {
    return RouteStep(
      instruction: json['instruction'],
      distance: json['distance'].toDouble(),
      duration: json['duration'],
      startLocation: LatLng(json['startLocation']['lat'], json['startLocation']['lng']),
      endLocation: LatLng(json['endLocation']['lat'], json['endLocation']['lng']),
      heading: json['heading'].toDouble(),
      roadName: json['roadName'],
      action: RouteAction.values.byName(json['action']),
      iconUrl: json['iconUrl'],
      auxiliaryAction: json['auxiliaryAction'],
      polyline: json['polyline'],
    );
  }
}

/// 路线动作类型
enum RouteAction {
  start,              // 出发
  arrive,             // 到达
  straight,           // 直行
  turnLeft,           // 左转
  turnRight,          // 右转
  turnSlightLeft,     // 稍向左转
  turnSlightRight,    // 稍向右转
  turnSharpLeft,      // 向左急转
  turnSharpRight,     // 向右急转
  uTurn,              // 掉头
  merge,              // 并线
  rampLeft,           // 左侧匝道
  rampRight,          // 右侧匝道
  ferry,              // 轮渡
  roundaboutLeft,     // 环岛左转
  roundaboutRight,    // 环岛右转
  keepLeft,           // 靠左
  keepRight,          // 靠右
}

/// 实时导航状态
class NavigationState {
  /// 当前步骤索引
  final int currentStepIndex;
  
  /// 剩余距离
  final double remainingDistance;
  
  /// 剩余时间
  final int remainingDuration;
  
  /// 当前速度
  final double currentSpeed;
  
  /// 当前位置
  final LatLng? currentLocation;
  
  /// 是否偏离路线
  final bool isDeviated;
  
  /// 下一转向距离
  final double? distanceToNextTurn;
  
  /// 下一转向指示
  final String? nextTurnInstruction;

  NavigationState({
    required this.currentStepIndex,
    required this.remainingDistance,
    required this.remainingDuration,
    required this.currentSpeed,
    this.currentLocation,
    this.isDeviated = false,
    this.distanceToNextTurn,
    this.nextTurnInstruction,
  });

  /// 格式化剩余距离
  String get formattedRemainingDistance {
    if (remainingDistance >= 1000) {
      return '${(remainingDistance / 1000).toStringAsFixed(1)}公里';
    } else {
      return '${remainingDistance.round()}米';
    }
  }

  /// 格式化剩余时间
  String get formattedRemainingDuration {
    if (remainingDuration >= 3600) {
      final hours = remainingDuration ~/ 3600;
      final minutes = (remainingDuration % 3600) ~/ 60;
      return '${hours}小时${minutes}分钟';
    } else if (remainingDuration >= 60) {
      return '${remainingDuration ~/ 60}分钟';
    } else {
      return '$remainingDuration秒';
    }
  }

  /// 格式化下一转向距离
  String? get formattedDistanceToNextTurn {
    if (distanceToNextTurn == null) return null;
    if (distanceToNextTurn! >= 1000) {
      return '${(distanceToNextTurn! / 1000).toStringAsFixed(1)}公里';
    } else {
      return '${distanceToNextTurn!.round()}米';
    }
  }

  /// 进度百分比
  double getProgressPercentage(double totalDistance) {
    if (totalDistance <= 0) return 0.0;
    return (1 - remainingDistance / totalDistance) * 100;
  }

  NavigationState copyWith({
    int? currentStepIndex,
    double? remainingDistance,
    int? remainingDuration,
    double? currentSpeed,
    LatLng? currentLocation,
    bool? isDeviated,
    double? distanceToNextTurn,
    String? nextTurnInstruction,
  }) {
    return NavigationState(
      currentStepIndex: currentStepIndex ?? this.currentStepIndex,
      remainingDistance: remainingDistance ?? this.remainingDistance,
      remainingDuration: remainingDuration ?? this.remainingDuration,
      currentSpeed: currentSpeed ?? this.currentSpeed,
      currentLocation: currentLocation ?? this.currentLocation,
      isDeviated: isDeviated ?? this.isDeviated,
      distanceToNextTurn: distanceToNextTurn ?? this.distanceToNextTurn,
      nextTurnInstruction: nextTurnInstruction ?? this.nextTurnInstruction,
    );
  }
}

/// 路线比较结果
class RouteComparison {
  final RoutePlanModel routeA;
  final RoutePlanModel routeB;
  
  RouteComparison({required this.routeA, required this.routeB});
  
  /// 距离差异（米）
  double get distanceDiff => routeB.totalDistance - routeA.totalDistance;
  
  /// 时间差异（秒）
  int get durationDiff => routeB.totalDuration - routeA.totalDuration;
  
  /// 费用差异（元）
  double get costDiff => routeB.tollCost - routeA.tollCost;
  
  /// 距离差异百分比
  double get distanceDiffPercent {
    if (routeA.totalDistance == 0) return 0.0;
    return (distanceDiff / routeA.totalDistance) * 100;
  }
  
  /// 时间差异百分比
  double get durationDiffPercent {
    if (routeA.totalDuration == 0) return 0.0;
    return (durationDiff / routeA.totalDuration) * 100;
  }
  
  /// 推荐路线
  RoutePlanModel get recommendedRoute {
    // 综合考虑时间和距离
    final scoreA = routeA.totalDuration * 0.6 + (routeA.totalDistance / 10) * 0.4;
    final scoreB = routeB.totalDuration * 0.6 + (routeB.totalDistance / 10) * 0.4;
    return scoreA <= scoreB ? routeA : routeB;
  }
  
  /// 生成对比描述
  String generateComparisonDescription() {
    final buffer = StringBuffer();
    
    if (durationDiff.abs() > 60) {
      if (durationDiff < 0) {
        buffer.write('方案二快${(durationDiff.abs() / 60).round()}分钟');
      } else {
        buffer.write('方案一快${(durationDiff.abs() / 60).round()}分钟');
      }
    }
    
    if (distanceDiff.abs() > 100) {
      if (buffer.isNotEmpty) buffer.write('，');
      if (distanceDiff < 0) {
        buffer.write('方案二近${(distanceDiff.abs() / 1000).toStringAsFixed(1)}公里');
      } else {
        buffer.write('方案一近${(distanceDiff.abs() / 1000).toStringAsFixed(1)}公里');
      }
    }
    
    if (costDiff != 0) {
      if (buffer.isNotEmpty) buffer.write('，');
      if (costDiff < 0) {
        buffer.write('方案二省${costDiff.abs()}元');
      } else {
        buffer.write('方案一省${costDiff.abs()}元');
      }
    }
    
    return buffer.toString();
  }
}

/// 历史导航记录
class NavigationHistory {
  final String id;
  final String routeId;
  final LatLng origin;
  final LatLng destination;
  final String? destinationName;
  final double actualDistance;
  final int actualDuration;
  final DateTime startTime;
  final DateTime endTime;
  final NavigationMode mode;
  final List<LatLng> trajectory;

  NavigationHistory({
    required this.id,
    required this.routeId,
    required this.origin,
    required this.destination,
    this.destinationName,
    required this.actualDistance,
    required this.actualDuration,
    required this.startTime,
    required this.endTime,
    required this.mode,
    this.trajectory = const [],
  });

  /// 平均速度
  double get averageSpeed {
    return (actualDistance / 1000) / (actualDuration / 3600);
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'routeId': routeId,
      'origin': {'lat': origin.latitude, 'lng': origin.longitude},
      'destination': {'lat': destination.latitude, 'lng': destination.longitude},
      'destinationName': destinationName,
      'actualDistance': actualDistance,
      'actualDuration': actualDuration,
      'startTime': startTime.toIso8601String(),
      'endTime': endTime.toIso8601String(),
      'mode': mode.name,
      'trajectory': trajectory.map((t) => {'lat': t.latitude, 'lng': t.longitude}).toList(),
    };
  }

  factory NavigationHistory.fromJson(Map<String, dynamic> json) {
    return NavigationHistory(
      id: json['id'],
      routeId: json['routeId'],
      origin: LatLng(json['origin']['lat'], json['origin']['lng']),
      destination: LatLng(json['destination']['lat'], json['destination']['lng']),
      destinationName: json['destinationName'],
      actualDistance: json['actualDistance'].toDouble(),
      actualDuration: json['actualDuration'],
      startTime: DateTime.parse(json['startTime']),
      endTime: DateTime.parse(json['endTime']),
      mode: NavigationMode.values.byName(json['mode']),
      trajectory: (json['trajectory'] as List)
          .map((t) => LatLng(t['lat'], t['lng']))
          .toList(),
    );
  }
}
