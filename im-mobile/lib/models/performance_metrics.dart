/// 性能指标数据模型
/// 对应后端 PerformanceMetricsEntity
/// 用于存储和传输量子通信性能监控数据

import 'dart:convert';

/// 性能指标模型类
class PerformanceMetrics {
  /// 指标ID
  final String id;
  
  /// 会话ID
  final String sessionId;
  
  /// 会话名称
  final String sessionName;
  
  /// 设备ID
  final String deviceId;
  
  /// 用户ID
  final String userId;
  
  /// 量子密钥分发速率 (kbps)
  final double qkdRate;
  
  /// 量子误码率 QBER (%)
  final double qber;
  
  /// 信道延迟 (ms)
  final double latency;
  
  /// 信道抖动 (ms)
  final double jitter;
  
  /// 丢包率 (%)
  final double packetLossRate;
  
  /// 信号稳定性评分 (0-100)
  final double signalStability;
  
  /// 量子纠缠质量 (0-100)
  final double entanglementQuality;
  
  /// 密钥生成速率 (keys/sec)
  final double keyGenerationRate;
  
  /// 成功握手次数
  final int successfulHandshakes;
  
  /// 失败握手次数
  final int failedHandshakes;
  
  /// 总传输字节数
  final int totalBytesTransferred;
  
  /// 加密数据量 (MB)
  final double encryptedDataVolume;
  
  /// 握手成功率 (%)
  final double handshakeSuccessRate;
  
  /// 平均密钥生成时间 (ms)
  final double avgKeyGenerationTime;
  
  /// 信道噪声级别
  final double channelNoiseLevel;
  
  /// 光子探测效率 (%)
  final double photonDetectionEfficiency;
  
  /// 测量时间戳
  final DateTime measurementTime;
  
  /// 测量持续时长 (秒)
  final int measurementDuration;
  
  /// 告警级别
  final AlertLevel alertLevel;
  
  /// 告警消息列表
  final List<String> alertMessages;
  
  /// 区域信息
  final String region;
  
  /// 网络类型
  final NetworkType networkType;
  
  /// 设备型号
  final String deviceModel;
  
  /// 操作系统版本
  final String osVersion;
  
  /// 应用版本
  final String appVersion;
  
  /// 创建时间
  final DateTime createdAt;
  
  /// 更新时间
  final DateTime updatedAt;
  
  /// 扩展属性
  final Map<String, dynamic> extraAttributes;

  PerformanceMetrics({
    required this.id,
    required this.sessionId,
    this.sessionName = '',
    required this.deviceId,
    required this.userId,
    this.qkdRate = 0.0,
    this.qber = 0.0,
    this.latency = 0.0,
    this.jitter = 0.0,
    this.packetLossRate = 0.0,
    this.signalStability = 100.0,
    this.entanglementQuality = 100.0,
    this.keyGenerationRate = 0.0,
    this.successfulHandshakes = 0,
    this.failedHandshakes = 0,
    this.totalBytesTransferred = 0,
    this.encryptedDataVolume = 0.0,
    this.handshakeSuccessRate = 100.0,
    this.avgKeyGenerationTime = 0.0,
    this.channelNoiseLevel = 0.0,
    this.photonDetectionEfficiency = 100.0,
    required this.measurementTime,
    this.measurementDuration = 0,
    this.alertLevel = AlertLevel.none,
    this.alertMessages = const [],
    this.region = '',
    this.networkType = NetworkType.unknown,
    this.deviceModel = '',
    this.osVersion = '',
    this.appVersion = '',
    required this.createdAt,
    required this.updatedAt,
    this.extraAttributes = const {},
  });

  /// 从JSON映射创建实例
  factory PerformanceMetrics.fromJson(Map<String, dynamic> json) {
    return PerformanceMetrics(
      id: json['id'] ?? '',
      sessionId: json['sessionId'] ?? '',
      sessionName: json['sessionName'] ?? '',
      deviceId: json['deviceId'] ?? '',
      userId: json['userId'] ?? '',
      qkdRate: (json['qkdRate'] ?? 0.0).toDouble(),
      qber: (json['qber'] ?? 0.0).toDouble(),
      latency: (json['latency'] ?? 0.0).toDouble(),
      jitter: (json['jitter'] ?? 0.0).toDouble(),
      packetLossRate: (json['packetLossRate'] ?? 0.0).toDouble(),
      signalStability: (json['signalStability'] ?? 100.0).toDouble(),
      entanglementQuality: (json['entanglementQuality'] ?? 100.0).toDouble(),
      keyGenerationRate: (json['keyGenerationRate'] ?? 0.0).toDouble(),
      successfulHandshakes: json['successfulHandshakes'] ?? 0,
      failedHandshakes: json['failedHandshakes'] ?? 0,
      totalBytesTransferred: json['totalBytesTransferred'] ?? 0,
      encryptedDataVolume: (json['encryptedDataVolume'] ?? 0.0).toDouble(),
      handshakeSuccessRate: (json['handshakeSuccessRate'] ?? 100.0).toDouble(),
      avgKeyGenerationTime: (json['avgKeyGenerationTime'] ?? 0.0).toDouble(),
      channelNoiseLevel: (json['channelNoiseLevel'] ?? 0.0).toDouble(),
      photonDetectionEfficiency: (json['photonDetectionEfficiency'] ?? 100.0).toDouble(),
      measurementTime: DateTime.parse(json['measurementTime'] ?? DateTime.now().toIso8601String()),
      measurementDuration: json['measurementDuration'] ?? 0,
      alertLevel: AlertLevel.fromString(json['alertLevel'] ?? 'NONE'),
      alertMessages: List<String>.from(json['alertMessages'] ?? []),
      region: json['region'] ?? '',
      networkType: NetworkType.fromString(json['networkType'] ?? 'UNKNOWN'),
      deviceModel: json['deviceModel'] ?? '',
      osVersion: json['osVersion'] ?? '',
      appVersion: json['appVersion'] ?? '',
      createdAt: DateTime.parse(json['createdAt'] ?? DateTime.now().toIso8601String()),
      updatedAt: DateTime.parse(json['updatedAt'] ?? DateTime.now().toIso8601String()),
      extraAttributes: json['extraAttributes'] ?? {},
    );
  }

  /// 转换为JSON映射
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'sessionId': sessionId,
      'sessionName': sessionName,
      'deviceId': deviceId,
      'userId': userId,
      'qkdRate': qkdRate,
      'qber': qber,
      'latency': latency,
      'jitter': jitter,
      'packetLossRate': packetLossRate,
      'signalStability': signalStability,
      'entanglementQuality': entanglementQuality,
      'keyGenerationRate': keyGenerationRate,
      'successfulHandshakes': successfulHandshakes,
      'failedHandshakes': failedHandshakes,
      'totalBytesTransferred': totalBytesTransferred,
      'encryptedDataVolume': encryptedDataVolume,
      'handshakeSuccessRate': handshakeSuccessRate,
      'avgKeyGenerationTime': avgKeyGenerationTime,
      'channelNoiseLevel': channelNoiseLevel,
      'photonDetectionEfficiency': photonDetectionEfficiency,
      'measurementTime': measurementTime.toIso8601String(),
      'measurementDuration': measurementDuration,
      'alertLevel': alertLevel.value,
      'alertMessages': alertMessages,
      'region': region,
      'networkType': networkType.value,
      'deviceModel': deviceModel,
      'osVersion': osVersion,
      'appVersion': appVersion,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
      'extraAttributes': extraAttributes,
    };
  }

  /// 从JSON字符串创建实例
  factory PerformanceMetrics.fromJsonString(String jsonString) {
    return PerformanceMetrics.fromJson(json.decode(jsonString));
  }

  /// 转换为JSON字符串
  String toJsonString() => json.encode(toJson());

  /// 计算综合健康评分 (0-100)
  double get healthScore {
    double score = 100.0;
    
    // QBER 影响 (权重: 25%)
    if (qber > 5.0) score -= 15;
    else if (qber > 2.0) score -= 5;
    else if (qber > 1.0) score -= 2;
    
    // 延迟影响 (权重: 20%)
    if (latency > 500) score -= 10;
    else if (latency > 200) score -= 5;
    else if (latency > 100) score -= 2;
    
    // 丢包率影响 (权重: 20%)
    if (packetLossRate > 5.0) score -= 15;
    else if (packetLossRate > 2.0) score -= 8;
    else if (packetLossRate > 1.0) score -= 3;
    
    // 信号稳定性影响 (权重: 15%)
    score -= ((100 - signalStability) * 0.15);
    
    // 纠缠质量影响 (权重: 10%)
    score -= ((100 - entanglementQuality) * 0.1);
    
    // 握手成功率影响 (权重: 10%)
    if (handshakeSuccessRate < 80) score -= 10;
    else if (handshakeSuccessRate < 95) score -= 5;
    
    return score.clamp(0.0, 100.0);
  }

  /// 获取健康状态描述
  String get healthStatus {
    final score = healthScore;
    if (score >= 90) return '优秀';
    if (score >= 80) return '良好';
    if (score >= 60) return '一般';
    if (score >= 40) return '较差';
    return '危险';
  }

  /// 获取健康状态颜色
  HealthStatusColor get healthStatusColor {
    final score = healthScore;
    if (score >= 90) return HealthStatusColor.excellent;
    if (score >= 80) return HealthStatusColor.good;
    if (score >= 60) return HealthStatusColor.fair;
    if (score >= 40) return HealthStatusColor.poor;
    return HealthStatusColor.critical;
  }

  /// 是否为异常状态
  bool get isAnomaly => alertLevel != AlertLevel.none;

  /// 是否有告警
  bool get hasAlerts => alertMessages.isNotEmpty;

  /// 总握手次数
  int get totalHandshakes => successfulHandshakes + failedHandshakes;

  /// 性能摘要
  PerformanceSummary get summary => PerformanceSummary(
    sessionId: sessionId,
    sessionName: sessionName,
    healthScore: healthScore,
    healthStatus: healthStatus,
    qber: qber,
    latency: latency,
    alertLevel: alertLevel,
    measurementTime: measurementTime,
  );

  /// 复制并修改
  PerformanceMetrics copyWith({
    String? id,
    String? sessionId,
    String? sessionName,
    String? deviceId,
    String? userId,
    double? qkdRate,
    double? qber,
    double? latency,
    double? jitter,
    double? packetLossRate,
    double? signalStability,
    double? entanglementQuality,
    double? keyGenerationRate,
    int? successfulHandshakes,
    int? failedHandshakes,
    int? totalBytesTransferred,
    double? encryptedDataVolume,
    double? handshakeSuccessRate,
    double? avgKeyGenerationTime,
    double? channelNoiseLevel,
    double? photonDetectionEfficiency,
    DateTime? measurementTime,
    int? measurementDuration,
    AlertLevel? alertLevel,
    List<String>? alertMessages,
    String? region,
    NetworkType? networkType,
    String? deviceModel,
    String? osVersion,
    String? appVersion,
    DateTime? createdAt,
    DateTime? updatedAt,
    Map<String, dynamic>? extraAttributes,
  }) {
    return PerformanceMetrics(
      id: id ?? this.id,
      sessionId: sessionId ?? this.sessionId,
      sessionName: sessionName ?? this.sessionName,
      deviceId: deviceId ?? this.deviceId,
      userId: userId ?? this.userId,
      qkdRate: qkdRate ?? this.qkdRate,
      qber: qber ?? this.qber,
      latency: latency ?? this.latency,
      jitter: jitter ?? this.jitter,
      packetLossRate: packetLossRate ?? this.packetLossRate,
      signalStability: signalStability ?? this.signalStability,
      entanglementQuality: entanglementQuality ?? this.entanglementQuality,
      keyGenerationRate: keyGenerationRate ?? this.keyGenerationRate,
      successfulHandshakes: successfulHandshakes ?? this.successfulHandshakes,
      failedHandshakes: failedHandshakes ?? this.failedHandshakes,
      totalBytesTransferred: totalBytesTransferred ?? this.totalBytesTransferred,
      encryptedDataVolume: encryptedDataVolume ?? this.encryptedDataVolume,
      handshakeSuccessRate: handshakeSuccessRate ?? this.handshakeSuccessRate,
      avgKeyGenerationTime: avgKeyGenerationTime ?? this.avgKeyGenerationTime,
      channelNoiseLevel: channelNoiseLevel ?? this.channelNoiseLevel,
      photonDetectionEfficiency: photonDetectionEfficiency ?? this.photonDetectionEfficiency,
      measurementTime: measurementTime ?? this.measurementTime,
      measurementDuration: measurementDuration ?? this.measurementDuration,
      alertLevel: alertLevel ?? this.alertLevel,
      alertMessages: alertMessages ?? this.alertMessages,
      region: region ?? this.region,
      networkType: networkType ?? this.networkType,
      deviceModel: deviceModel ?? this.deviceModel,
      osVersion: osVersion ?? this.osVersion,
      appVersion: appVersion ?? this.appVersion,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
      extraAttributes: extraAttributes ?? this.extraAttributes,
    );
  }

  @override
  String toString() {
    return 'PerformanceMetrics(id: $id, sessionId: $sessionId, healthScore: ${healthScore.toStringAsFixed(1)})';
  }
}

/// 告警级别枚举
enum AlertLevel {
  none('NONE', '正常'),
  low('LOW', '低'),
  medium('MEDIUM', '中'),
  high('HIGH', '高'),
  critical('CRITICAL', '严重');

  final String value;
  final String label;

  const AlertLevel(this.value, this.label);

  factory AlertLevel.fromString(String value) {
    return AlertLevel.values.firstWhere(
      (e) => e.value == value.toUpperCase(),
      orElse: () => AlertLevel.none,
    );
  }

  /// 获取优先级数值 (越高越严重)
  int get priority {
    switch (this) {
      case AlertLevel.none: return 0;
      case AlertLevel.low: return 1;
      case AlertLevel.medium: return 2;
      case AlertLevel.high: return 3;
      case AlertLevel.critical: return 4;
    }
  }

  /// 是否为严重告警
  bool get isSevere => this == AlertLevel.high || this == AlertLevel.critical;

  /// 颜色标识
  String get colorHex {
    switch (this) {
      case AlertLevel.none: return '#10B981';
      case AlertLevel.low: return '#3B82F6';
      case AlertLevel.medium: return '#F59E0B';
      case AlertLevel.high: return '#EF4444';
      case AlertLevel.critical: return '#DC2626';
    }
  }
}

/// 网络类型枚举
enum NetworkType {
  unknown('UNKNOWN', '未知'),
  wifi('WIFI', 'WiFi'),
  cellular4G('4G', '4G'),
  cellular5G('5G', '5G'),
  ethernet('ETHERNET', '以太网'),
  quantum('QUANTUM', '量子网络');

  final String value;
  final String label;

  const NetworkType(this.value, this.label);

  factory NetworkType.fromString(String value) {
    return NetworkType.values.firstWhere(
      (e) => e.value == value.toUpperCase(),
      orElse: () => NetworkType.unknown,
    );
  }

  /// 是否为移动网络
  bool get isMobile => this == NetworkType.cellular4G || this == NetworkType.cellular5G;

  /// 是否为高速网络
  bool get isHighSpeed => this == NetworkType.cellular5G || this == NetworkType.quantum;
}

/// 健康状态颜色枚举
enum HealthStatusColor {
  excellent,
  good,
  fair,
  poor,
  critical;

  /// 颜色十六进制值
  String get hex {
    switch (this) {
      case HealthStatusColor.excellent: return '#10B981';
      case HealthStatusColor.good: return '#3B82F6';
      case HealthStatusColor.fair: return '#F59E0B';
      case HealthStatusColor.poor: return '#EF4444';
      case HealthStatusColor.critical: return '#DC2626';
    }
  }

  /// 颜色描述
  String get description {
    switch (this) {
      case HealthStatusColor.excellent: return '优秀';
      case HealthStatusColor.good: return '良好';
      case HealthStatusColor.fair: return '一般';
      case HealthStatusColor.poor: return '较差';
      case HealthStatusColor.critical: return '危险';
    }
  }
}

/// 性能摘要类
class PerformanceSummary {
  final String sessionId;
  final String sessionName;
  final double healthScore;
  final String healthStatus;
  final double qber;
  final double latency;
  final AlertLevel alertLevel;
  final DateTime measurementTime;

  PerformanceSummary({
    required this.sessionId,
    required this.sessionName,
    required this.healthScore,
    required this.healthStatus,
    required this.qber,
    required this.latency,
    required this.alertLevel,
    required this.measurementTime,
  });

  Map<String, dynamic> toJson() => {
    'sessionId': sessionId,
    'sessionName': sessionName,
    'healthScore': healthScore,
    'healthStatus': healthStatus,
    'qber': qber,
    'latency': latency,
    'alertLevel': alertLevel.value,
    'measurementTime': measurementTime.toIso8601String(),
  };
}

/// 性能趋势数据点
class PerformanceTrendPoint {
  final DateTime timestamp;
  final double qkdRate;
  final double qber;
  final double latency;
  final double healthScore;

  PerformanceTrendPoint({
    required this.timestamp,
    required this.qkdRate,
    required this.qber,
    required this.latency,
    required this.healthScore,
  });

  factory PerformanceTrendPoint.fromJson(Map<String, dynamic> json) {
    return PerformanceTrendPoint(
      timestamp: DateTime.parse(json['timestamp']),
      qkdRate: (json['qkdRate'] ?? 0.0).toDouble(),
      qber: (json['qber'] ?? 0.0).toDouble(),
      latency: (json['latency'] ?? 0.0).toDouble(),
      healthScore: (json['healthScore'] ?? 100.0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'timestamp': timestamp.toIso8601String(),
    'qkdRate': qkdRate,
    'qber': qber,
    'latency': latency,
    'healthScore': healthScore,
  };
}

/// 性能统计聚合
class PerformanceAggregate {
  final double avgQkdRate;
  final double avgQber;
  final double avgLatency;
  final double avgHealthScore;
  final int totalSessions;
  final int alertCount;
  final DateTime startTime;
  final DateTime endTime;

  PerformanceAggregate({
    required this.avgQkdRate,
    required this.avgQber,
    required this.avgLatency,
    required this.avgHealthScore,
    required this.totalSessions,
    required this.alertCount,
    required this.startTime,
    required this.endTime,
  });

  factory PerformanceAggregate.fromJson(Map<String, dynamic> json) {
    return PerformanceAggregate(
      avgQkdRate: (json['avgQkdRate'] ?? 0.0).toDouble(),
      avgQber: (json['avgQber'] ?? 0.0).toDouble(),
      avgLatency: (json['avgLatency'] ?? 0.0).toDouble(),
      avgHealthScore: (json['avgHealthScore'] ?? 100.0).toDouble(),
      totalSessions: json['totalSessions'] ?? 0,
      alertCount: json['alertCount'] ?? 0,
      startTime: DateTime.parse(json['startTime'] ?? DateTime.now().toIso8601String()),
      endTime: DateTime.parse(json['endTime'] ?? DateTime.now().toIso8601String()),
    );
  }

  Map<String, dynamic> toJson() => {
    'avgQkdRate': avgQkdRate,
    'avgQber': avgQber,
    'avgLatency': avgLatency,
    'avgHealthScore': avgHealthScore,
    'totalSessions': totalSessions,
    'alertCount': alertCount,
    'startTime': startTime.toIso8601String(),
    'endTime': endTime.toIso8601String(),
  };
}

/// 性能告警规则
class AlertRule {
  final String id;
  final String name;
  final String description;
  final AlertMetric metric;
  final AlertOperator operator;
  final double threshold;
  final AlertLevel level;
  final bool enabled;

  AlertRule({
    required this.id,
    required this.name,
    required this.description,
    required this.metric,
    required this.operator,
    required this.threshold,
    required this.level,
    this.enabled = true,
  });

  /// 检查指标是否触发告警
  bool check(PerformanceMetrics metrics) {
    if (!enabled) return false;
    
    double value = 0;
    switch (metric) {
      case AlertMetric.qber: value = metrics.qber; break;
      case AlertMetric.latency: value = metrics.latency; break;
      case AlertMetric.packetLossRate: value = metrics.packetLossRate; break;
      case AlertMetric.signalStability: value = metrics.signalStability; break;
      case AlertMetric.entanglementQuality: value = metrics.entanglementQuality; break;
      case AlertMetric.handshakeSuccessRate: value = metrics.handshakeSuccessRate; break;
    }

    switch (operator) {
      case AlertOperator.greaterThan: return value > threshold;
      case AlertOperator.lessThan: return value < threshold;
      case AlertOperator.equals: return value == threshold;
      case AlertOperator.greaterOrEqual: return value >= threshold;
      case AlertOperator.lessOrEqual: return value <= threshold;
    }
  }
}

/// 告警指标枚举
enum AlertMetric {
  qber,
  latency,
  packetLossRate,
  signalStability,
  entanglementQuality,
  handshakeSuccessRate,
}

/// 告警操作符枚举
enum AlertOperator {
  greaterThan,
  lessThan,
  equals,
  greaterOrEqual,
  lessOrEqual,
}

/// 性能优化建议
class OptimizationSuggestion {
  final String id;
  final String title;
  final String description;
  final OptimizationType type;
  final int priority;
  final bool isImplemented;
  final DateTime? implementedAt;
  final double? expectedImprovement;

  OptimizationSuggestion({
    required this.id,
    required this.title,
    required this.description,
    required this.type,
    required this.priority,
    this.isImplemented = false,
    this.implementedAt,
    this.expectedImprovement,
  });

  factory OptimizationSuggestion.fromJson(Map<String, dynamic> json) {
    return OptimizationSuggestion(
      id: json['id'] ?? '',
      title: json['title'] ?? '',
      description: json['description'] ?? '',
      type: OptimizationType.fromString(json['type'] ?? 'general'),
      priority: json['priority'] ?? 0,
      isImplemented: json['isImplemented'] ?? false,
      implementedAt: json['implementedAt'] != null ? DateTime.parse(json['implementedAt']) : null,
      expectedImprovement: json['expectedImprovement']?.toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'title': title,
    'description': description,
    'type': type.value,
    'priority': priority,
    'isImplemented': isImplemented,
    'implementedAt': implementedAt?.toIso8601String(),
    'expectedImprovement': expectedImprovement,
  };
}

/// 优化类型枚举
enum OptimizationType {
  network('network', '网络优化'),
  protocol('protocol', '协议优化'),
  hardware('hardware', '硬件优化'),
  algorithm('algorithm', '算法优化'),
  general('general', '通用优化');

  final String value;
  final String label;

  const OptimizationType(this.value, this.label);

  factory OptimizationType.fromString(String value) {
    return OptimizationType.values.firstWhere(
      (e) => e.value == value,
      orElse: () => OptimizationType.general,
    );
  }
}
