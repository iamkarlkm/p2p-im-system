/// 性能监控仪表板组件
/// 提供量子通信性能的实时可视化展示

import 'dart:math';

import 'package:flutter/material.dart';

import '../models/performance_metrics.dart';
import '../services/performance_monitor_service.dart';

/// 性能监控仪表板
/// 
/// 展示实时性能指标、趋势图表和告警信息
class PerformanceDashboard extends StatefulWidget {
  /// 会话ID（可选，为空时显示整体监控）
  final String? sessionId;
  
  /// 监控配置
  final PerformanceMonitorConfig? config;
  
  /// 是否显示详细视图
  final bool showDetailed;
  
  /// 是否允许交互
  final bool enableInteraction;

  const PerformanceDashboard({
    Key? key,
    this.sessionId,
    this.config,
    this.showDetailed = true,
    this.enableInteraction = true,
  }) : super(key: key);

  @override
  State<PerformanceDashboard> createState() => _PerformanceDashboardState();
}

class _PerformanceDashboardState extends State<PerformanceDashboard>
    with SingleTickerProviderStateMixin {
  late PerformanceMonitorService _service;
  late PerformanceMonitorConfig _config;
  late TabController _tabController;
  
  int _selectedTimeRange = 60; // 默认60分钟
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _service = PerformanceMonitorService.instance;
    _config = widget.config ?? PerformanceMonitorConfig.defaultConfig();
    _tabController = TabController(length: 3, vsync: this);
    
    if (_config.autoStart) {
      _service.startMonitoring(interval: _config.interval);
    }
    
    _service.addListener(_onMetricsUpdate);
  }

  @override
  void dispose() {
    _tabController.dispose();
    _service.removeListener(_onMetricsUpdate);
    super.dispose();
  }

  void _onMetricsUpdate() {
    if (mounted) {
      setState(() {});
    }
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      elevation: 2,
      margin: const EdgeInsets.all(16),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          _buildHeader(),
          _buildHealthScoreSection(),
          if (widget.showDetailed) ...[
            const Divider(),
            _buildTabBar(),
            Expanded(
              child: TabBarView(
                controller: _tabController,
                children: [
                  _buildOverviewTab(),
                  _buildTrendsTab(),
                  _buildAlertsTab(),
                ],
              ),
            ),
          ],
        ],
      ),
    );
  }

  /// 构建头部
  Widget _buildHeader() {
    final metrics = _service.currentMetrics;
    
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          Icon(
            Icons.speed,
            color: Theme.of(context).primaryColor,
            size: 28,
          ),
          const SizedBox(width: 12),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  '量子通信性能监控',
                  style: TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  metrics != null
                      ? '最后更新: ${_formatTime(_service.lastUpdateTime)}'
                      : '等待数据...',
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.grey[600],
                  ),
                ),
              ],
            ),
          ),
          _buildMonitoringToggle(),
        ],
      ),
    );
  }

  /// 构建监控开关
  Widget _buildMonitoringToggle() {
    return Row(
      children: [
        Container(
          width: 8,
          height: 8,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            color: _service.isMonitoring ? Colors.green : Colors.grey,
          ),
        ),
        const SizedBox(width: 8),
        if (widget.enableInteraction)
          TextButton.icon(
            onPressed: () {
              if (_service.isMonitoring) {
                _service.stopMonitoring();
              } else {
                _service.startMonitoring(interval: _config.interval);
              }
            },
            icon: Icon(
              _service.isMonitoring ? Icons.stop : Icons.play_arrow,
            ),
            label: Text(_service.isMonitoring ? '停止' : '开始'),
          ),
      ],
    );
  }

  /// 构建健康评分区域
  Widget _buildHealthScoreSection() {
    final healthScore = _service.currentHealthScore;
    final healthStatus = _service.currentHealthStatus;
    
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(
        children: [
          Expanded(
            child: _buildHealthScoreCard(
              '健康评分',
              healthScore,
              _getScoreColor(healthScore),
              healthStatus,
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: _buildMetricCard(
              'QBER',
              _service.currentMetrics?.qber ?? 0,
              '%',
              _getQberColor(_service.currentMetrics?.qber ?? 0),
            ),
          ),
          const SizedBox(width: 12),
          Expanded(
            child: _buildMetricCard(
              '延迟',
              _service.currentMetrics?.latency ?? 0,
              'ms',
              _getLatencyColor(_service.currentMetrics?.latency ?? 0),
            ),
          ),
        ],
      ),
    );
  }

  /// 构建健康评分卡片
  Widget _buildHealthScoreCard(
    String title,
    double value,
    Color color,
    String status,
  ) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: color.withOpacity(0.1),
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: color.withOpacity(0.3)),
      ),
      child: Column(
        children: [
          Text(
            title,
            style: TextStyle(
              fontSize: 12,
              color: Colors.grey[600],
            ),
          ),
          const SizedBox(height: 8),
          Text(
            value.toStringAsFixed(0),
            style: TextStyle(
              fontSize: 32,
              fontWeight: FontWeight.bold,
              color: color,
            ),
          ),
          const SizedBox(height: 4),
          Text(
            status,
            style: TextStyle(
              fontSize: 12,
              color: color,
              fontWeight: FontWeight.w500,
            ),
          ),
        ],
      ),
    );
  }

  /// 构建指标卡片
  Widget _buildMetricCard(String title, double value, String unit, Color color) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.grey[50],
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.grey[200]!),
      ),
      child: Column(
        children: [
          Text(
            title,
            style: TextStyle(
              fontSize: 12,
              color: Colors.grey[600],
            ),
          ),
          const SizedBox(height: 8),
          Text(
            '${value.toStringAsFixed(1)}$unit',
            style: TextStyle(
              fontSize: 24,
              fontWeight: FontWeight.bold,
              color: color,
            ),
          ),
        ],
      ),
    );
  }

  /// 构建TabBar
  Widget _buildTabBar() {
    return TabBar(
      controller: _tabController,
      labelColor: Theme.of(context).primaryColor,
      unselectedLabelColor: Colors.grey,
      indicatorColor: Theme.of(context).primaryColor,
      tabs: const [
        Tab(icon: Icon(Icons.dashboard), text: '概览'),
        Tab(icon: Icon(Icons.trending_up), text: '趋势'),
        Tab(icon: Icon(Icons.notifications), text: '告警'),
      ],
    );
  }

  /// 构建概览Tab
  Widget _buildOverviewTab() {
    final metrics = _service.currentMetrics;
    
    if (metrics == null) {
      return const Center(
        child: Text('暂无性能数据'),
      );
    }
    
    return SingleChildScrollView(
      padding: const EdgeInsets.all(16),
      child: Column(
        children: [
          _buildMetricsGrid(metrics),
          const SizedBox(height: 16),
          _buildSessionInfoCard(metrics),
        ],
      ),
    );
  }

  /// 构建指标网格
  Widget _buildMetricsGrid(PerformanceMetrics metrics) {
    final items = [
      _MetricItem('QKD速率', '${metrics.qkdRate.toStringAsFixed(1)} kbps', Icons.network_check),
      _MetricItem('量子误码率', '${metrics.qber.toStringAsFixed(2)}%', Icons.error_outline),
      _MetricItem('信道抖动', '${metrics.jitter.toStringAsFixed(1)} ms', Icons.waves),
      _MetricItem('丢包率', '${metrics.packetLossRate.toStringAsFixed(2)}%', Icons.packet),
      _MetricItem('信号稳定性', '${metrics.signalStability.toStringAsFixed(1)}%', Icons.signal_cellular_alt),
      _MetricItem('纠缠质量', '${metrics.entanglementQuality.toStringAsFixed(1)}%', Icons.grain),
      _MetricItem('密钥生成', '${metrics.keyGenerationRate.toStringAsFixed(1)} k/s', Icons.key),
      _MetricItem('握手成功', '${metrics.handshakeSuccessRate.toStringAsFixed(1)}%', Icons.handshake),
    ];
    
    return GridView.builder(
      shrinkWrap: true,
      physics: const NeverScrollableScrollPhysics(),
      gridDelegate: const SliverGridDelegateWithFixedCrossAxisCount(
        crossAxisCount: 4,
        childAspectRatio: 1.5,
        crossAxisSpacing: 8,
        mainAxisSpacing: 8,
      ),
      itemCount: items.length,
      itemBuilder: (context, index) {
        final item = items[index];
        return _buildMetricGridItem(item);
      },
    );
  }

  /// 构建指标网格项
  Widget _buildMetricGridItem(_MetricItem item) {
    return Container(
      padding: const EdgeInsets.all(12),
      decoration: BoxDecoration(
        color: Colors.grey[50],
        borderRadius: BorderRadius.circular(8),
        border: Border.all(color: Colors.grey[200]!),
      ),
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(item.icon, size: 20, color: Colors.grey[600]),
          const SizedBox(height: 4),
          Text(
            item.value,
            style: const TextStyle(
              fontSize: 14,
              fontWeight: FontWeight.bold,
            ),
          ),
          Text(
            item.label,
            style: TextStyle(
              fontSize: 10,
              color: Colors.grey[600],
            ),
          ),
        ],
      ),
    );
  }

  /// 构建会话信息卡片
  Widget _buildSessionInfoCard(PerformanceMetrics metrics) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              '会话信息',
              style: TextStyle(
                fontSize: 16,
                fontWeight: FontWeight.bold,
              ),
            ),
            const SizedBox(height: 12),
            _buildInfoRow('会话ID', metrics.sessionId),
            _buildInfoRow('会话名称', metrics.sessionName),
            _buildInfoRow('设备ID', metrics.deviceId),
            _buildInfoRow('设备型号', metrics.deviceModel),
            _buildInfoRow('网络类型', metrics.networkType.label),
            _buildInfoRow('区域', metrics.region),
            _buildInfoRow('总字节数', _formatBytes(metrics.totalBytesTransferred)),
            _buildInfoRow('加密数据', '${metrics.encryptedDataVolume.toStringAsFixed(1)} MB'),
          ],
        ),
      ),
    );
  }

  /// 构建信息行
  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        children: [
          SizedBox(
            width: 100,
            child: Text(
              label,
              style: TextStyle(
                fontSize: 12,
                color: Colors.grey[600],
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(
                fontSize: 12,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
        ],
      ),
    );
  }

  /// 构建趋势Tab
  Widget _buildTrendsTab() {
    return Column(
      children: [
        _buildTimeRangeSelector(),
        Expanded(
          child: _buildTrendChart(),
        ),
      ],
    );
  }

  /// 构建时间范围选择器
  Widget _buildTimeRangeSelector() {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          const Text(
            '时间范围:',
            style: TextStyle(fontSize: 14),
          ),
          const SizedBox(width: 12),
          SegmentedButton<int>(
            segments: const [
              ButtonSegment(value: 15, label: Text('15分')),
              ButtonSegment(value: 60, label: Text('1小时')),
              ButtonSegment(value: 240, label: Text('4小时')),
              ButtonSegment(value: 1440, label: Text('24小时')),
            ],
            selected: {_selectedTimeRange},
            onSelectionChanged: (Set<int> selected) {
              setState(() {
                _selectedTimeRange = selected.first;
              });
            },
          ),
        ],
      ),
    );
  }

  /// 构建趋势图表
  Widget _buildTrendChart() {
    final trend = _service.calculateTrend(timeRange: _selectedTimeRange);
    
    if (trend.isEmpty) {
      return const Center(
        child: Text('暂无趋势数据'),
      );
    }
    
    return Padding(
      padding: const EdgeInsets.all(16),
      child: CustomPaint(
        size: const Size(double.infinity, double.infinity),
        painter: _TrendChartPainter(trend: trend),
      ),
    );
  }

  /// 构建告警Tab
  Widget _buildAlertsTab() {
    // 获取有告警的历史记录
    final alerts = _service.metricsHistory
        .where((m) => m.alertLevel != AlertLevel.none)
        .toList();
    
    if (alerts.isEmpty) {
      return const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.check_circle, size: 64, color: Colors.green),
            SizedBox(height: 16),
            Text(
              '暂无告警',
              style: TextStyle(
                fontSize: 18,
                color: Colors.green,
              ),
            ),
            SizedBox(height: 8),
            Text(
              '系统运行正常',
              style: TextStyle(
                fontSize: 14,
                color: Colors.grey,
              ),
            ),
          ],
        ),
      );
    }
    
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: alerts.length,
      itemBuilder: (context, index) {
        final alert = alerts[alerts.length - 1 - index]; // 倒序显示
        return _buildAlertCard(alert);
      },
    );
  }

  /// 构建告警卡片
  Widget _buildAlertCard(PerformanceMetrics metrics) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      color: _getAlertColor(metrics.alertLevel).withOpacity(0.1),
      child: ListTile(
        leading: Icon(
          _getAlertIcon(metrics.alertLevel),
          color: _getAlertColor(metrics.alertLevel),
        ),
        title: Text(
          '性能告警 - ${metrics.alertLevel.label}',
          style: TextStyle(
            color: _getAlertColor(metrics.alertLevel),
            fontWeight: FontWeight.bold,
          ),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('会话: ${metrics.sessionName}'),
            Text('时间: ${_formatTime(metrics.measurementTime)}'),
            if (metrics.alertMessages.isNotEmpty)
              Text('详情: ${metrics.alertMessages.join(', ')}'),
          ],
        ),
        isThreeLine: true,
      ),
    );
  }

  // ========== 辅助方法 ==========
  
  Color _getScoreColor(double score) {
    if (score >= 90) return Colors.green;
    if (score >= 80) return Colors.blue;
    if (score >= 60) return Colors.orange;
    if (score >= 40) return Colors.deepOrange;
    return Colors.red;
  }

  Color _getQberColor(double qber) {
    if (qber < 1.0) return Colors.green;
    if (qber < 2.0) return Colors.blue;
    if (qber < 5.0) return Colors.orange;
    return Colors.red;
  }

  Color _getLatencyColor(double latency) {
    if (latency < 50) return Colors.green;
    if (latency < 100) return Colors.blue;
    if (latency < 200) return Colors.orange;
    return Colors.red;
  }

  Color _getAlertColor(AlertLevel level) {
    switch (level) {
      case AlertLevel.none: return Colors.green;
      case AlertLevel.low: return Colors.blue;
      case AlertLevel.medium: return Colors.orange;
      case AlertLevel.high: return Colors.deepOrange;
      case AlertLevel.critical: return Colors.red;
    }
  }

  IconData _getAlertIcon(AlertLevel level) {
    switch (level) {
      case AlertLevel.none: return Icons.check_circle;
      case AlertLevel.low: return Icons.info;
      case AlertLevel.medium: return Icons.warning;
      case AlertLevel.high: return Icons.error;
      case AlertLevel.critical: return Icons.dangerous;
    }
  }

  String _formatTime(DateTime? time) {
    if (time == null) return '--:--';
    return '${time.hour.toString().padLeft(2, '0')}:${time.minute.toString().padLeft(2, '0')}:${time.second.toString().padLeft(2, '0')}';
  }

  String _formatBytes(int bytes) {
    if (bytes < 1024) return '$bytes B';
    if (bytes < 1024 * 1024) return '${(bytes / 1024).toStringAsFixed(1)} KB';
    if (bytes < 1024 * 1024 * 1024) return '${(bytes / (1024 * 1024)).toStringAsFixed(1)} MB';
    return '${(bytes / (1024 * 1024 * 1024)).toStringAsFixed(1)} GB';
  }
}

/// 指标项数据结构
class _MetricItem {
  final String label;
  final String value;
  final IconData icon;
  
  _MetricItem(this.label, this.value, this.icon);
}

/// 趋势图表绘制器
class _TrendChartPainter extends CustomPainter {
  final List<PerformanceTrendPoint> trend;
  
  _TrendChartPainter({required this.trend});

  @override
  void paint(Canvas canvas, Size size) {
    if (trend.isEmpty) return;
    
    final paint = Paint()
      ..strokeWidth = 2
      ..style = PaintingStyle.stroke;
    
    // 绘制网格
    _drawGrid(canvas, size);
    
    // 绘制健康评分曲线
    _drawLine(
      canvas, size,
      trend.map((t) => t.healthScore).toList(),
      Colors.green,
      100,
    );
    
    // 绘制延迟曲线
    _drawLine(
      canvas, size,
      trend.map((t) => t.latency).toList(),
      Colors.blue,
      500,
    );
    
    // 绘制QBER曲线
    _drawLine(
      canvas, size,
      trend.map((t) => t.qber * 20).toList(), // 放大显示
      Colors.orange,
      100,
    );
  }

  void _drawGrid(Canvas canvas, Size size) {
    final gridPaint = Paint()
      ..color = Colors.grey[300]!
      ..strokeWidth = 1
      ..style = PaintingStyle.stroke;
    
    // 水平线
    for (int i = 0; i <= 5; i++) {
      final y = size.height * i / 5;
      canvas.drawLine(
        Offset(0, y),
        Offset(size.width, y),
        gridPaint,
      );
    }
    
    // 垂直线
    for (int i = 0; i <= 10; i++) {
      final x = size.width * i / 10;
      canvas.drawLine(
        Offset(x, 0),
        Offset(x, size.height),
        gridPaint,
      );
    }
  }

  void _drawLine(Canvas canvas, Size size, List<double> values, Color color, double maxValue) {
    if (values.isEmpty) return;
    
    final paint = Paint()
      ..color = color
      ..strokeWidth = 2
      ..style = PaintingStyle.stroke;
    
    final path = Path();
    final stepX = size.width / (values.length - 1);
    
    for (int i = 0; i < values.length; i++) {
      final x = i * stepX;
      final normalizedValue = (values[i] / maxValue).clamp(0.0, 1.0);
      final y = size.height - (normalizedValue * size.height);
      
      if (i == 0) {
        path.moveTo(x, y);
      } else {
        path.lineTo(x, y);
      }
    }
    
    canvas.drawPath(path, paint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) => true;
}

/// 简化版性能指示器
/// 
/// 用于在空间有限的位置显示性能状态
class PerformanceIndicator extends StatelessWidget {
  final double? size;
  final bool showLabel;
  
  const PerformanceIndicator({
    Key? key,
    this.size,
    this.showLabel = true,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: PerformanceMonitorService.instance,
      builder: (context, child) {
        final service = PerformanceMonitorService.instance;
        final score = service.currentHealthScore;
        final status = service.currentHealthStatus;
        
        return Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              width: size ?? 12,
              height: size ?? 12,
              decoration: BoxDecoration(
                shape: BoxShape.circle,
                color: _getScoreColor(score),
                boxShadow: [
                  BoxShadow(
                    color: _getScoreColor(score).withOpacity(0.3),
                    blurRadius: 4,
                    spreadRadius: 1,
                  ),
                ],
              ),
            ),
            if (showLabel) ...[
              const SizedBox(width: 8),
              Text(
                status,
                style: TextStyle(
                  fontSize: 12,
                  color: _getScoreColor(score),
                  fontWeight: FontWeight.w500,
                ),
              ),
            ],
          ],
        );
      },
    );
  }
  
  Color _getScoreColor(double score) {
    if (score >= 90) return Colors.green;
    if (score >= 80) return Colors.blue;
    if (score >= 60) return Colors.orange;
    if (score >= 40) return Colors.deepOrange;
    return Colors.red;
  }
}
