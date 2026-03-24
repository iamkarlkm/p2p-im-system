import 'package:flutter/material.dart';
import '../models/login_anomaly_alert.dart';
import '../services/login_anomaly_alert_service.dart';

class LoginAnomalyAlertScreen extends StatefulWidget {
  const LoginAnomalyAlertScreen({super.key});

  @override
  State<LoginAnomalyAlertScreen> createState() => _LoginAnomalyAlertScreenState();
}

class _LoginAnomalyAlertScreenState extends State<LoginAnomalyAlertScreen> {
  final LoginAnomalyAlertService _service = LoginAnomalyAlertService();
  List<LoginAnomalyAlert> _alerts = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _loadAlerts();
  }

  Future<void> _loadAlerts() async {
    setState(() => _loading = true);
    try {
      _alerts = await _service.getPendingAlerts();
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('加载告警失败: $e')),
      );
    }
    if (mounted) setState(() => _loading = false);
  }

  Future<void> _confirmAlert(int alertId) async {
    try {
      await _service.confirmAlert(alertId);
      _alerts.removeWhere((a) => a.id == alertId);
      if (mounted) setState(() {});
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('确认失败: $e')),
      );
    }
  }

  Future<void> _dismissAlert(int alertId) async {
    try {
      await _service.dismissAlert(alertId);
      _alerts.removeWhere((a) => a.id == alertId);
      if (mounted) setState(() {});
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('忽略失败: $e')),
      );
    }
  }

  Color _riskColor(int? score) {
    if (score == null) return Colors.grey;
    if (score >= 70) return Colors.red;
    if (score >= 40) return Colors.orange;
    return Colors.green;
  }

  String _alertTypeLabel(String type) {
    switch (type) {
      case 'NEW_DEVICE': return '新设备登录';
      case 'CROSS_REGION': return '异地登录';
      case 'ABNORMAL_FREQUENCY': return '异常频率';
      case 'UNKNOWN_DEVICE': return '未知设备';
      default: return '安全告警';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('登录异常告警'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadAlerts,
          ),
        ],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _alerts.isEmpty
              ? const Center(child: Text('暂无告警'))
              : RefreshIndicator(
                  onRefresh: _loadAlerts,
                  child: ListView.builder(
                    itemCount: _alerts.length,
                    itemBuilder: (context, index) {
                      final alert = _alerts[index];
                      return Card(
                        margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                        child: ListTile(
                          leading: CircleAvatar(
                            backgroundColor: _riskColor(alert.riskScore),
                            child: const Icon(Icons.warning, color: Colors.white),
                          ),
                          title: Text(_alertTypeLabel(alert.alertType)),
                          subtitle: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              if (alert.deviceName != null)
                                Text('设备: ${alert.deviceName}'),
                              if (alert.location != null)
                                Text('位置: ${alert.location}'),
                              if (alert.ipAddress != null)
                                Text('IP: ${alert.ipAddress}'),
                              Text('时间: ${alert.loginTime}'),
                              if (alert.riskScore != null)
                                Text('风险: ${alert.riskScore}%',
                                    style: TextStyle(color: _riskColor(alert.riskScore))),
                            ],
                          ),
                          isThreeLine: true,
                          trailing: Row(
                            mainAxisSize: MainAxisSize.min,
                            children: [
                              IconButton(
                                icon: const Icon(Icons.check, color: Colors.green),
                                onPressed: () => _confirmAlert(alert.id),
                                tooltip: '确认是本人',
                              ),
                              IconButton(
                                icon: const Icon(Icons.close, color: Colors.grey),
                                onPressed: () => _dismissAlert(alert.id),
                                tooltip: '忽略',
                              ),
                            ],
                          ),
                        ),
                      );
                    },
                  ),
                ),
    );
  }
}
