import 'package:flutter/material.dart';
import 'package:latlong2/latlong.dart';

import '../models/navigation_models.dart';

/// 路线规划结果页面
/// Route Planning Result Screen
class RoutePlanningResultScreen extends StatelessWidget {
  final RoutePlan routePlan;

  const RoutePlanningResultScreen({
    Key? key,
    required this.routePlan,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: const Color(0xFFF5F5F5),
      body: Column(
        children: [
          // 顶部地图预览
          _buildMapPreview(),

          // 路线概览信息
          _buildRouteOverview(),

          // 路线步骤列表
          Expanded(
            child: _buildStepsList(),
          ),

          // 底部操作栏
          _buildBottomActions(context),
        ],
      ),
    );
  }

  Widget _buildMapPreview() {
    return Container(
      height: 200,
      color: Colors.grey[300],
      child: Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.map, size: 48, color: Colors.grey[600]),
            const SizedBox(height: 8),
            Text(
              '路线地图预览',
              style: TextStyle(color: Colors.grey[600]),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildRouteOverview() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 8,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: Column(
        children: [
          Row(
            children: [
              Expanded(
                child: _buildInfoItem(
                  icon: Icons.schedule,
                  value: routePlan.durationText,
                  label: '预计时间',
                  color: Colors.blue,
                ),
              ),
              Container(
                width: 1,
                height: 40,
                color: Colors.grey[300],
              ),
              Expanded(
                child: _buildInfoItem(
                  icon: Icons.straighten,
                  value: routePlan.distanceText,
                  label: '总距离',
                  color: Colors.green,
                ),
              ),
              Container(
                width: 1,
                height: 40,
                color: Colors.grey[300],
              ),
              Expanded(
                child: _buildInfoItem(
                  icon: Icons.traffic,
                  value: routePlan.trafficConditionText,
                  label: '路况',
                  color: _getTrafficColor(),
                ),
              ),
            ],
          ),
          if (routePlan.tollFee > 0) ...[
            const SizedBox(height: 12),
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
              decoration: BoxDecoration(
                color: Colors.orange.withOpacity(0.1),
                borderRadius: BorderRadius.circular(16),
              ),
              child: Row(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(
                    Icons.attach_money,
                    size: 16,
                    color: Colors.orange[700],
                  ),
                  const SizedBox(width: 4),
                  Text(
                    '过路费约 ${routePlan.tollFee}元',
                    style: TextStyle(
                      color: Colors.orange[700],
                      fontSize: 12,
                    ),
                  ),
                ],
              ),
            ),
          ],
        ],
      ),
    );
  }

  Widget _buildInfoItem({
    required IconData icon,
    required String value,
    required String label,
    required Color color,
  }) {
    return Column(
      children: [
        Icon(icon, color: color, size: 24),
        const SizedBox(height: 4),
        Text(
          value,
          style: const TextStyle(
            fontSize: 16,
            fontWeight: FontWeight.bold,
          ),
        ),
        Text(
          label,
          style: TextStyle(
            fontSize: 12,
            color: Colors.grey[600],
          ),
        ),
      ],
    );
  }

  Color _getTrafficColor() {
    switch (routePlan.trafficCondition) {
      case 'SMOOTH':
        return Colors.green;
      case 'SLOW':
        return Colors.orange;
      case 'CONGESTED':
      case 'SEVERE':
        return Colors.red;
      default:
        return Colors.grey;
    }
  }

  Widget _buildStepsList() {
    return ListView.builder(
      padding: const EdgeInsets.all(16),
      itemCount: routePlan.steps.length,
      itemBuilder: (context, index) {
        final step = routePlan.steps[index];
        final isFirst = index == 0;
        final isLast = index == routePlan.steps.length - 1;

        return _buildStepItem(
          step: step,
          index: index,
          isFirst: isFirst,
          isLast: isLast,
        );
      },
    );
  }

  Widget _buildStepItem({
    required RouteStep step,
    required int index,
    required bool isFirst,
    required bool isLast,
  }) {
    return IntrinsicHeight(
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          // 步骤指示器
          Column(
            children: [
              Container(
                width: 32,
                height: 32,
                decoration: BoxDecoration(
                  color: isFirst ? Colors.blue : Colors.grey[300],
                  shape: BoxShape.circle,
                ),
                child: Center(
                  child: isFirst
                      ? const Icon(Icons.navigation, color: Colors.white, size: 18)
                      : Text(
                          '$index',
                          style: TextStyle(
                            color: Colors.grey[700],
                            fontSize: 12,
                            fontWeight: FontWeight.bold,
                          ),
                        ),
                ),
              ),
              if (!isLast)
                Expanded(
                  child: Container(
                    width: 2,
                    color: Colors.grey[300],
                  ),
                ),
            ],
          ),
          const SizedBox(width: 12),

          // 步骤内容
          Expanded(
            child: Container(
              padding: const EdgeInsets.only(bottom: 16),
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Text(
                    step.instruction,
                    style: const TextStyle(
                      fontSize: 15,
                      fontWeight: FontWeight.w500,
                    ),
                  ),
                  const SizedBox(height: 4),
                  Row(
                    children: [
                      Text(
                        step.roadName.isNotEmpty ? step.roadName : '无名道路',
                        style: TextStyle(
                          fontSize: 13,
                          color: Colors.grey[600],
                        ),
                      ),
                      Text(
                        ' · ',
                        style: TextStyle(color: Colors.grey[400]),
                      ),
                      Text(
                        step.distanceText,
                        style: TextStyle(
                          fontSize: 13,
                          color: Colors.grey[600],
                        ),
                      ),
                    ],
                  ),
                  if (step.voiceText.isNotEmpty) ...[
                    const SizedBox(height: 8),
                    Container(
                      padding: const EdgeInsets.symmetric(
                        horizontal: 10,
                        vertical: 6,
                      ),
                      decoration: BoxDecoration(
                        color: Colors.blue.withOpacity(0.1),
                        borderRadius: BorderRadius.circular(8),
                      ),
                      child: Row(
                        mainAxisSize: MainAxisSize.min,
                        children: [
                          Icon(
                            Icons.volume_up,
                            size: 14,
                            color: Colors.blue[700],
                          ),
                          const SizedBox(width: 4),
                          Flexible(
                            child: Text(
                              step.voiceText,
                              style: TextStyle(
                                fontSize: 12,
                                color: Colors.blue[700],
                              ),
                            ),
                          ),
                        ],
                      ),
                    ),
                  ],
                ],
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildBottomActions(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.1),
            blurRadius: 8,
            offset: const Offset(0, -2),
          ),
        ],
      ),
      child: SafeArea(
        child: Row(
          children: [
            Expanded(
              child: OutlinedButton.icon(
                onPressed: () => _showSimulateDialog(context),
                icon: const Icon(Icons.play_circle_outline),
                label: const Text('模拟导航'),
                style: OutlinedButton.styleFrom(
                  padding: const EdgeInsets.symmetric(vertical: 14),
                ),
              ),
            ),
            const SizedBox(width: 16),
            Expanded(
              flex: 2,
              child: ElevatedButton.icon(
                onPressed: () => _startNavigation(context),
                icon: const Icon(Icons.navigation),
                label: const Text(
                  '开始导航',
                  style: TextStyle(fontSize: 16),
                ),
                style: ElevatedButton.styleFrom(
                  backgroundColor: Colors.blue,
                  foregroundColor: Colors.white,
                  padding: const EdgeInsets.symmetric(vertical: 14),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  void _showSimulateDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('模拟导航'),
        content: const Text('是否开始模拟导航演示？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(context);
              _startSimulatedNavigation(context);
            },
            child: const Text('开始'),
          ),
        ],
      ),
    );
  }

  void _startNavigation(BuildContext context) {
    Navigator.pushNamed(
      context,
      '/navigation',
      arguments: {
        'routePlan': routePlan,
        'isSimulated': false,
      },
    );
  }

  void _startSimulatedNavigation(BuildContext context) {
    Navigator.pushNamed(
      context,
      '/navigation',
      arguments: {
        'routePlan': routePlan,
        'isSimulated': true,
      },
    );
  }
}
