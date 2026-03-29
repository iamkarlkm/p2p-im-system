/// 性能监控服务
/// 提供量子通信性能的实时监控、数据获取和分析功能

import 'dart:async';
import 'dart:convert';
import 'dart:math';

import 'package:flutter/foundation.dart';

import '../models/performance_metrics.dart';

/// 性能监控服务类
/// 负责管理性能数据的获取、缓存和分析
class PerformanceMonitorService extends ChangeNotifier {
  static final PerformanceMonitorService _instance = PerformanceMonitorService._internal();
  
  /// 单例实例
  static PerformanceMonitorService get instance => _instance;
  
  PerformanceMonitorService._internal() {
    _init();
  }

  // ========== 内部状态 ==========
  
  /// 当前性能指标
  PerformanceMetrics? _currentMetrics;
  
  /// 历史性能数据缓存
  final List<PerformanceMetrics> _metricsHistory = [];
  
  /// 性能数据流控制器
  final StreamController<PerformanceMetrics> _metricsController = 
      StreamController<PerformanceMetrics>.broadcast();
  
  /// 告警流控制器
  final StreamController<PerformanceAlert> _alertController = 
      StreamController<PerformanceAlert>.broadcast();
  
  /// 定时器
  Timer? _monitoringTimer;
  
  /// 是否正在监控
  bool _isMonitoring = false;
  
  /// 监控间隔（秒）
  int _monitoringInterval = 30;
  
  /// 历史数据最大保存数量
  static const int _maxHistorySize = 1000;
  
  /// 告警规则列表
  final List<AlertRule> _alertRules = [];
  
  /// 最后更新时间
  DateTime? _lastUpdateTime;

  // ========== Getters ==========
  
  /// 当前性能指标
  PerformanceMetrics? get currentMetrics => _currentMetrics;
  
  /// 性能数据流
  Stream<PerformanceMetrics> get metricsStream => _metricsController.stream;
  
  /// 告警流
  Stream<PerformanceAlert> get alertStream => _alertController.stream;
  
  /// 是否正在监控
  bool get isMonitoring => _isMonitoring;
  
  /// 监控间隔
  int get monitoringInterval => _monitoringInterval;
  
  /// 历史数据
  List<PerformanceMetrics> get metricsHistory => List.unmodifiable(_metricsHistory);
  
  /// 最后更新时间
  DateTime? get lastUpdateTime => _lastUpdateTime;
  
  /// 当前会话的健康评分
  double get currentHealthScore => _currentMetrics?.healthScore ?? 0.0;
  
  /// 当前会话的健康状态
  String get currentHealthStatus => _currentMetrics?.healthStatus ?? '未知';

  // ========== 初始化 ==========
  
  void _init() {
    _setupDefaultAlertRules();
  }
  
  /// 设置默认告警规则
  void _setupDefaultAlertRules() {
    _alertRules.addAll([
      AlertRule(
        id: 'qber_high',
        name: '高QBER告警',
        description: '量子误码率超过阈值',
        metric: AlertMetric.qber,
        operator: AlertOperator.greaterThan,
        threshold: 5.0,
        level: AlertLevel.high,
      ),
      AlertRule(
        id: 'latency_high',
        name: '高延迟告警',
        description: '通信延迟超过阈值',
        metric: AlertMetric.latency,
        operator: AlertOperator.greaterThan,
        threshold: 500,
        level: AlertLevel.medium,
      ),
      AlertRule(
        id: 'packet_loss_high',
        name: '高丢包率告警',
        description: '数据包丢失率超过阈值',
        metric: AlertMetric.packetLossRate,
        operator: AlertOperator.greaterThan,
        threshold: 5.0,
        level: AlertLevel.high,
      ),
      AlertRule(
        id: 'stability_low',
        name: '信号稳定性告警',
        description: '信号稳定性低于阈值',
        metric: AlertMetric.signalStability,
        operator: AlertOperator.lessThan,
        threshold: 60.0,
        level: AlertLevel.medium,
      ),
    ]);
  }

  // ========== 监控控制 ==========
  
  /// 开始性能监控
  /// 
  /// [interval] 监控间隔（秒），默认30秒
  void startMonitoring({int interval = 30}) {
    if (_isMonitoring) return;
    
    _monitoringInterval = interval;
    _isMonitoring = true;
    
    // 立即获取一次数据
    _fetchMetrics();
    
    // 启动定时器
    _monitoringTimer = Timer.periodic(
      Duration(seconds: _monitoringInterval),
      (_) => _fetchMetrics(),
    );
    
    notifyListeners();
  }
  
  /// 停止性能监控
  void stopMonitoring() {
    _monitoringTimer?.cancel();
    _monitoringTimer = null;
    _isMonitoring = false;
    notifyListeners();
  }
  
  /// 更新监控间隔
  void updateMonitoringInterval(int interval) {
    _monitoringInterval = interval;
    if (_isMonitoring) {
      // 重启监控以应用新间隔
      stopMonitoring();
      startMonitoring(interval: interval);
    }
  }

  // ========== 数据获取 ==========
  
  /// 从服务器获取最新性能指标
  Future<void> _fetchMetrics() async {
    try {
      // TODO: 替换为实际的API调用
      // final response = await apiClient.get('/performance/current');
      // final metrics = PerformanceMetrics.fromJson(response.data);
      
      // 模拟数据（实际使用时应从API获取）
      final metrics = await _simulateFetchMetrics();
      
      _updateMetrics(metrics);
    } catch (e) {
      debugPrint('获取性能指标失败: $e');
    }
  }
  
  /// 模拟获取性能数据（用于测试）
  Future<PerformanceMetrics> _simulateFetchMetrics() async {
    // 模拟网络延迟
    await Future.delayed(const Duration(milliseconds: 100));
    
    final random = Random();
    final now = DateTime.now();
    
    return PerformanceMetrics(
      id: 'perf_${now.millisecondsSinceEpoch}',
      sessionId: 'session_${random.nextInt(1000)}',
      sessionName: '测试会话',
      deviceId: 'device_${random.nextInt(100)}',
      userId: 'user_${random.nextInt(50)}',
      qkdRate: 1000 + random.nextDouble() * 500,
      qber: random.nextDouble() * 3,
      latency: 20 + random.nextDouble() * 30,
      jitter: random.nextDouble() * 5,
      packetLossRate: random.nextDouble() * 0.5,
      signalStability: 80 + random.nextDouble() * 20,
      entanglementQuality: 85 + random.nextDouble() * 15,
      keyGenerationRate: 50 + random.nextDouble() * 20,
      successfulHandshakes: 100 + random.nextInt(50),
      failedHandshakes: random.nextInt(5),
      totalBytesTransferred: 1000000 + random.nextInt(500000),
      encryptedDataVolume: 50 + random.nextDouble() * 30,
      handshakeSuccessRate: 95 + random.nextDouble() * 5,
      avgKeyGenerationTime: 10 + random.nextDouble() * 5,
      channelNoiseLevel: random.nextDouble() * 0.1,
      photonDetectionEfficiency: 85 + random.nextDouble() * 10,
      measurementTime: now,
      measurementDuration: _monitoringInterval,
      alertLevel: AlertLevel.none,
      alertMessages: const [],
      region: 'CN-Beijing',
      networkType: NetworkType.wifi,
      deviceModel: 'QuantumDevice-X1',
      osVersion: 'Android 14',
      appVersion: '1.0.0',
      createdAt: now,
      updatedAt: now,
    );
  }
  
  /// 手动刷新性能数据
  Future<void> refreshMetrics() async {
    await _fetchMetrics();
  }
  
  /// 获取历史性能数据
  /// 
  /// [startTime] 开始时间
  /// [endTime] 结束时间
  /// [limit] 最大返回数量
  Future<List<PerformanceMetrics>> fetchHistoryMetrics({
    DateTime? startTime,
    DateTime? endTime,
    int? limit,
  }) async {
    // TODO: 替换为实际的API调用
    // 这里从缓存中筛选
    var filtered = _metricsHistory.where((m) {
      if (startTime != null && m.measurementTime.isBefore(startTime)) return false;
      if (endTime != null && m.measurementTime.isAfter(endTime)) return false;
      return true;
    }).toList();
    
    if (limit != null && filtered.length > limit) {
      filtered = filtered.sublist(filtered.length - limit);
    }
    
    return filtered;
  }

  // ========== 数据更新 ==========
  
  /// 更新性能指标
  void _updateMetrics(PerformanceMetrics metrics) {
    _currentMetrics = metrics;
    _lastUpdateTime = DateTime.now();
    
    // 添加到历史记录
    _metricsHistory.add(metrics);
    
    // 限制历史记录大小
    if (_metricsHistory.length > _maxHistorySize) {
      _metricsHistory.removeAt(0);
    }
    
    // 检查告警
    _checkAlerts(metrics);
    
    // 通知监听器
    _metricsController.add(metrics);
    notifyListeners();
  }
  
  /// 设置当前性能指标（外部调用）
  void setMetrics(PerformanceMetrics metrics) {
    _updateMetrics(metrics);
  }

  // ========== 告警处理 ==========
  
  /// 检查告警规则
  void _checkAlerts(PerformanceMetrics metrics) {
    for (final rule in _alertRules) {
      if (rule.check(metrics)) {
        final alert = PerformanceAlert(
          id: '${rule.id}_${DateTime.now().millisecondsSinceEpoch}',
          ruleId: rule.id,
          ruleName: rule.name,
          description: rule.description,
          level: rule.level,
          metric: rule.metric,
          threshold: rule.threshold,
          actualValue: _getMetricValue(metrics, rule.metric),
          timestamp: DateTime.now(),
          metricsId: metrics.id,
          isResolved: false,
        );
        
        _alertController.add(alert);
      }
    }
  }
  
  /// 获取指标值
  double _getMetricValue(PerformanceMetrics metrics, AlertMetric metric) {
    switch (metric) {
      case AlertMetric.qber: return metrics.qber;
      case AlertMetric.latency: return metrics.latency;
      case AlertMetric.packetLossRate: return metrics.packetLossRate;
      case AlertMetric.signalStability: return metrics.signalStability;
      case AlertMetric.entanglementQuality: return metrics.entanglementQuality;
      case AlertMetric.handshakeSuccessRate: return metrics.handshakeSuccessRate;
    }
  }
  
  /// 添加自定义告警规则
  void addAlertRule(AlertRule rule) {
    _alertRules.add(rule);
  }
  
  /// 移除告警规则
  void removeAlertRule(String ruleId) {
    _alertRules.removeWhere((r) => r.id == ruleId);
  }
  
  /// 启用/禁用告警规则
  void toggleAlertRule(String ruleId, bool enabled) {
    final index = _alertRules.indexWhere((r) => r.id == ruleId);
    if (index >= 0) {
      final rule = _alertRules[index];
      _alertRules[index] = AlertRule(
        id: rule.id,
        name: rule.name,
        description: rule.description,
        metric: rule.metric,
        operator: rule.operator,
        threshold: rule.threshold,
        level: rule.level,
        enabled: enabled,
      );
    }
  }

  // ========== 数据分析 ==========
  
  /// 计算性能趋势
  /// 
  /// [timeRange] 时间范围（分钟）
  List<PerformanceTrendPoint> calculateTrend({int timeRange = 60}) {
    final cutoff = DateTime.now().subtract(Duration(minutes: timeRange));
    final relevantMetrics = _metricsHistory
        .where((m) => m.measurementTime.isAfter(cutoff))
        .toList();
    
    if (relevantMetrics.isEmpty) return [];
    
    return relevantMetrics.map((m) => PerformanceTrendPoint(
      timestamp: m.measurementTime,
      qkdRate: m.qkdRate,
      qber: m.qber,
      latency: m.latency,
      healthScore: m.healthScore,
    )).toList();
  }
  
  /// 计算统计聚合
  /// 
  /// [startTime] 开始时间
  /// [endTime] 结束时间
  PerformanceAggregate? calculateAggregate({
    DateTime? startTime,
    DateTime? endTime,
  }) {
    final effectiveStart = startTime ?? DateTime.now().subtract(const Duration(hours: 24));
    final effectiveEnd = endTime ?? DateTime.now();
    
    final relevantMetrics = _metricsHistory.where((m) {
      return m.measurementTime.isAfter(effectiveStart) &&
             m.measurementTime.isBefore(effectiveEnd);
    }).toList();
    
    if (relevantMetrics.isEmpty) return null;
    
    double sumQkdRate = 0;
    double sumQber = 0;
    double sumLatency = 0;
    double sumHealthScore = 0;
    int alertCount = 0;
    
    for (final m in relevantMetrics) {
      sumQkdRate += m.qkdRate;
      sumQber += m.qber;
      sumLatency += m.latency;
      sumHealthScore += m.healthScore;
      if (m.alertLevel != AlertLevel.none) alertCount++;
    }
    
    final count = relevantMetrics.length;
    
    return PerformanceAggregate(
      avgQkdRate: sumQkdRate / count,
      avgQber: sumQber / count,
      avgLatency: sumLatency / count,
      avgHealthScore: sumHealthScore / count,
      totalSessions: count,
      alertCount: alertCount,
      startTime: effectiveStart,
      endTime: effectiveEnd,
    );
  }
  
  /// 生成优化建议
  List<OptimizationSuggestion> generateOptimizationSuggestions() {
    final suggestions = <OptimizationSuggestion>[];
    
    if (_currentMetrics == null) return suggestions;
    
    final m = _currentMetrics!;
    
    // QBER 优化建议
    if (m.qber > 2.0) {
      suggestions.add(OptimizationSuggestion(
        id: 'opt_qber_${DateTime.now().millisecondsSinceEpoch}',
        title: '降低量子误码率',
        description: '当前QBER为${m.qber.toStringAsFixed(2)}%，建议检查量子信道干扰源，调整光子探测器灵敏度。',
        type: OptimizationType.hardware,
        priority: 1,
        expectedImprovement: 5.0,
      ));
    }
    
    // 延迟优化建议
    if (m.latency > 200) {
      suggestions.add(OptimizationSuggestion(
        id: 'opt_latency_${DateTime.now().millisecondsSinceEpoch}',
        title: '优化通信延迟',
        description: '当前延迟为${m.latency.toStringAsFixed(1)}ms，建议选择更近的量子节点或优化网络路由。',
        type: OptimizationType.network,
        priority: 2,
        expectedImprovement: 10.0,
      ));
    }
    
    // 稳定性优化建议
    if (m.signalStability < 80) {
      suggestions.add(OptimizationSuggestion(
        id: 'opt_stability_${DateTime.now().millisecondsSinceEpoch}',
        title: '提升信号稳定性',
        description: '当前信号稳定性为${m.signalStability.toStringAsFixed(1)}%，建议启用错误纠正机制或更换信道。',
        type: OptimizationType.protocol,
        priority: 3,
        expectedImprovement: 8.0,
      ));
    }
    
    return suggestions..sort((a, b) => a.priority.compareTo(b.priority));
  }

  // ========== 资源释放 ==========
  
  /// 清理历史数据
  void clearHistory() {
    _metricsHistory.clear();
    notifyListeners();
  }
  
  /// 释放资源
  @override
  void dispose() {
    stopMonitoring();
    _metricsController.close();
    _alertController.close();
    super.dispose();
  }
}

/// 性能告警类
class PerformanceAlert {
  /// 告警ID
  final String id;
  
  /// 规则ID
  final String ruleId;
  
  /// 规则名称
  final String ruleName;
  
  /// 告警描述
  final String description;
  
  /// 告警级别
  final AlertLevel level;
  
  /// 相关指标
  final AlertMetric metric;
  
  /// 阈值
  final double threshold;
  
  /// 实际值
  final double actualValue;
  
  /// 触发时间
  final DateTime timestamp;
  
  /// 关联的性能指标ID
  final String metricsId;
  
  /// 是否已解决
  final bool isResolved;

  PerformanceAlert({
    required this.id,
    required this.ruleId,
    required this.ruleName,
    required this.description,
    required this.level,
    required this.metric,
    required this.threshold,
    required this.actualValue,
    required this.timestamp,
    required this.metricsId,
    this.isResolved = false,
  });

  Map<String, dynamic> toJson() => {
    'id': id,
    'ruleId': ruleId,
    'ruleName': ruleName,
    'description': description,
    'level': level.value,
    'metric': metric.toString(),
    'threshold': threshold,
    'actualValue': actualValue,
    'timestamp': timestamp.toIso8601String(),
    'metricsId': metricsId,
    'isResolved': isResolved,
  };
}

/// 性能监控配置
class PerformanceMonitorConfig {
  /// 监控间隔（秒）
  final int interval;
  
  /// 是否自动开始监控
  final bool autoStart;
  
  /// 历史数据保留时间（小时）
  final int historyRetentionHours;
  
  /// 是否启用告警
  final bool enableAlerts;
  
  /// 是否启用实时通知
  final bool enableRealtimeNotifications;
  
  /// 低性能阈值
  final double lowPerformanceThreshold;

  PerformanceMonitorConfig({
    this.interval = 30,
    this.autoStart = false,
    this.historyRetentionHours = 24,
    this.enableAlerts = true,
    this.enableRealtimeNotifications = true,
    this.lowPerformanceThreshold = 60.0,
  });

  factory PerformanceMonitorConfig.defaultConfig() => PerformanceMonitorConfig();
  
  factory PerformanceMonitorConfig.fromJson(Map<String, dynamic> json) {
    return PerformanceMonitorConfig(
      interval: json['interval'] ?? 30,
      autoStart: json['autoStart'] ?? false,
      historyRetentionHours: json['historyRetentionHours'] ?? 24,
      enableAlerts: json['enableAlerts'] ?? true,
      enableRealtimeNotifications: json['enableRealtimeNotifications'] ?? true,
      lowPerformanceThreshold: (json['lowPerformanceThreshold'] ?? 60.0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'interval': interval,
    'autoStart': autoStart,
    'historyRetentionHours': historyRetentionHours,
    'enableAlerts': enableAlerts,
    'enableRealtimeNotifications': enableRealtimeNotifications,
    'lowPerformanceThreshold': lowPerformanceThreshold,
  };
}
