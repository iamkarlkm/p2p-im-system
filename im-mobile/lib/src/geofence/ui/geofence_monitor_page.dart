import 'dart:async';

import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:provider/provider.dart';

import '../models/geofence_model.dart';
import '../service/geofence_service_manager.dart';
import '../service/store_visit_reminder_service.dart';

/// 地理围栏监控页面
/// 
/// 显示：
/// - 当前位置
/// - 附近围栏
/// - 到店记录
/// - 监控状态
class GeofenceMonitorPage extends StatefulWidget {
  const GeofenceMonitorPage({super.key});

  @override
  State<GeofenceMonitorPage> createState() => _GeofenceMonitorPageState();
}

class _GeofenceMonitorPageState extends State<GeofenceMonitorPage>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;
  StreamSubscription<GeofenceTriggerEvent>? _eventSubscription;
  
  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
    
    // 订阅围栏事件
    final manager = context.read<GeofenceServiceManager>();
    _eventSubscription = manager.eventStream.listen((event) {
      _showEventSnackBar(event);
    });
    
    // 初始化服务
    _initializeServices();
  }
  
  Future<void> _initializeServices() async {
    final manager = context.read<GeofenceServiceManager>();
    final reminderService = context.read<StoreVisitReminderService>();
    
    await manager.initialize();
    await reminderService.initialize();
    
    // 请求权限并开始监控
    final hasPermission = await manager.checkPermission();
    if (hasPermission && mounted) {
      await manager.startMonitoring();
    }
  }
  
  void _showEventSnackBar(GeofenceTriggerEvent event) {
    if (!mounted) return;
    
    String message;
    Color backgroundColor;
    
    switch (event.eventType) {
      case GeofenceEvent.enter:
        message = '🏪 进入围栏区域';
        backgroundColor = Colors.green;
        break;
      case GeofenceEvent.dwell:
        message = '⏰ 已停留 ${event.dwellDuration != null ? event.dwellDuration! ~/ 60000 : 0} 分钟';
        backgroundColor = Colors.orange;
        break;
      case GeofenceEvent.exit:
        message = '👋 离开围栏区域';
        backgroundColor = Colors.blue;
        break;
      default:
        return;
    }
    
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(message),
        backgroundColor: backgroundColor,
        duration: const Duration(seconds: 2),
      ),
    );
  }

  @override
  void dispose() {
    _tabController.dispose();
    _eventSubscription?.cancel();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('地理围栏监控'),
        bottom: TabBar(
          controller: _tabController,
          tabs: const [
            Tab(icon: Icon(Icons.location_on), text: '围栏'),
            Tab(icon: Icon(Icons.history), text: '记录'),
            Tab(icon: Icon(Icons.settings), text: '设置'),
          ],
        ),
        actions: [
          Consumer<GeofenceServiceManager>(
            builder: (context, manager, child) {
              return IconButton(
                icon: Icon(
                  manager.isMonitoring ? Icons.gps_fixed : Icons.gps_off,
                  color: manager.isMonitoring ? Colors.green : Colors.grey,
                ),
                onPressed: () async {
                  if (manager.isMonitoring) {
                    await manager.stopMonitoring();
                  } else {
                    await manager.startMonitoring();
                  }
                },
              );
            },
          ),
        ],
      ),
      body: TabBarView(
        controller: _tabController,
        children: const [
          _GeofenceListTab(),
          _VisitHistoryTab(),
          _MonitorSettingsTab(),
        ],
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _showAddGeofenceDialog(context),
        child: const Icon(Icons.add_location),
      ),
    );
  }
  
  void _showAddGeofenceDialog(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => const AddGeofenceDialog(),
    );
  }
}

/// 围栏列表标签页
class _GeofenceListTab extends StatelessWidget {
  const _GeofenceListTab();

  @override
  Widget build(BuildContext context) {
    return Consumer<GeofenceServiceManager>(
      builder: (context, manager, child) {
        final geofences = manager.activeGeofences;
        
        if (geofences.isEmpty) {
          return const Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(Icons.location_off, size: 64, color: Colors.grey),
                SizedBox(height: 16),
                Text('暂无围栏', style: TextStyle(color: Colors.grey)),
                SizedBox(height: 8),
                Text('点击右下角添加', style: TextStyle(color: Colors.grey, fontSize: 12)),
              ],
            ),
          );
        }
        
        return ListView.builder(
          itemCount: geofences.length,
          itemBuilder: (context, index) {
            final geofence = geofences[index];
            final state = manager.getMonitoringState(geofence.id);
            
            return GeofenceListTile(
              geofence: geofence,
              state: state,
              onDelete: () => manager.unregisterGeofence(geofence.id),
            );
          },
        );
      },
    );
  }
}

/// 围栏列表项
class GeofenceListTile extends StatelessWidget {
  final Geofence geofence;
  final GeofenceMonitoringState? state;
  final VoidCallback? onDelete;

  const GeofenceListTile({
    super.key,
    required this.geofence,
    this.state,
    this.onDelete,
  });

  @override
  Widget build(BuildContext context) {
    final statusColor = _getStatusColor(state?.status);
    
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: statusColor.withOpacity(0.2),
          child: Icon(
            _getTypeIcon(geofence.type),
            color: statusColor,
          ),
        ),
        title: Text(geofence.name),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(_getSubtitleText()),
            if (state != null)
              Text(
                '状态: ${_getStatusText(state!.status)}',
                style: TextStyle(
                  color: statusColor,
                  fontSize: 12,
                  fontWeight: FontWeight.bold,
                ),
              ),
          ],
        ),
        trailing: onDelete != null
            ? IconButton(
                icon: const Icon(Icons.delete_outline, color: Colors.red),
                onPressed: () => _showDeleteConfirm(context),
              )
            : null,
        onTap: () => _showGeofenceDetail(context),
      ),
    );
  }
  
  String _getSubtitleText() {
    switch (geofence.type) {
      case GeofenceType.circle:
        return '圆形围栏 · 半径 ${geofence.radius?.toInt() ?? 100}米';
      case GeofenceType.polygon:
        return '多边形围栏 · ${geofence.polygonPoints?.length ?? 0}个顶点';
      case GeofenceType.polyline:
        return '线性围栏 · 缓冲 ${geofence.polylineBuffer?.toInt() ?? 50}米';
    }
  }
  
  Color _getStatusColor(GeofenceStatus? status) {
    switch (status) {
      case GeofenceStatus.inside:
        return Colors.green;
      case GeofenceStatus.dwelling:
        return Colors.orange;
      case GeofenceStatus.outside:
        return Colors.grey;
      default:
        return Colors.blue;
    }
  }
  
  String _getStatusText(GeofenceStatus status) {
    switch (status) {
      case GeofenceStatus.inside:
        return '在围栏内';
      case GeofenceStatus.dwelling:
        return '停留中';
      case GeofenceStatus.outside:
        return '在围栏外';
      case GeofenceStatus.unknown:
        return '未知';
    }
  }
  
  IconData _getTypeIcon(GeofenceType type) {
    switch (type) {
      case GeofenceType.circle:
        return Icons.circle_outlined;
      case GeofenceType.polygon:
        return Icons.crop_square;
      case GeofenceType.polyline:
        return Icons.timeline;
    }
  }
  
  void _showDeleteConfirm(BuildContext context) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('删除围栏'),
        content: Text('确定要删除"${geofence.name}"吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              onDelete?.call();
            },
            child: const Text('删除', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }
  
  void _showGeofenceDetail(BuildContext context) {
    showModalBottomSheet(
      context: context,
      builder: (context) => GeofenceDetailSheet(
        geofence: geofence,
        state: state,
      ),
    );
  }
}

/// 围栏详情弹窗
class GeofenceDetailSheet extends StatelessWidget {
  final Geofence geofence;
  final GeofenceMonitoringState? state;

  const GeofenceDetailSheet({
    super.key,
    required this.geofence,
    this.state,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            geofence.name,
            style: Theme.of(context).textTheme.headlineSmall,
          ),
          const SizedBox(height: 16),
          _buildInfoRow('类型', geofence.type.name),
          _buildInfoRow('坐标', '${geofence.latitude.toStringAsFixed(6)}, ${geofence.longitude.toStringAsFixed(6)}'),
          if (geofence.radius != null)
            _buildInfoRow('半径', '${geofence.radius!.toInt()}米'),
          _buildInfoRow('触发事件', geofence.triggers.map((t) => t.name).join(', ')),
          _buildInfoRow('置信度阈值', '${(geofence.minConfidence * 100).toInt()}%'),
          if (state != null) ...[
            const Divider(),
            _buildInfoRow('当前状态', state!.status.name),
            if (state!.enterTime != null)
              _buildInfoRow('进入时间', state!.enterTime!.toString().substring(0, 19)),
            if (state!.dwellDuration > 0)
              _buildInfoRow('停留时长', '${state!.dwellDuration ~/ 60000}分钟'),
          ],
          const SizedBox(height: 16),
          SizedBox(
            width: double.infinity,
            child: ElevatedButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('关闭'),
            ),
          ),
        ],
      ),
    );
  }
  
  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 100,
            child: Text(
              label,
              style: const TextStyle(color: Colors.grey),
            ),
          ),
          Expanded(
            child: Text(value),
          ),
        ],
      ),
    );
  }
}

/// 到店历史标签页
class _VisitHistoryTab extends StatelessWidget {
  const _VisitHistoryTab();

  @override
  Widget build(BuildContext context) {
    return Consumer<StoreVisitReminderService>(
      builder: (context, service, child) {
        final visits = service.recentVisits;
        
        if (visits.isEmpty) {
          return const Center(
            child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                Icon(Icons.history, size: 64, color: Colors.grey),
                SizedBox(height: 16),
                Text('暂无到店记录', style: TextStyle(color: Colors.grey)),
              ],
            ),
          );
        }
        
        return ListView.builder(
          itemCount: visits.length,
          itemBuilder: (context, index) {
            final visit = visits[index];
            return Card(
              margin: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
              child: ListTile(
                leading: visit.merchantLogo != null
                    ? CircleAvatar(backgroundImage: NetworkImage(visit.merchantLogo!))
                    : const CircleAvatar(child: Icon(Icons.store)),
                title: Text(visit.merchantName),
                subtitle: Text(
                  '${visit.enterTime.toString().substring(0, 16)} · '
                  '${visit.duration != null ? '${visit.duration!.inMinutes}分钟' : '进行中'}',
                ),
                trailing: visit.triggeredOffers.isNotEmpty
                    ? Chip(
                        label: Text('${visit.triggeredOffers.length}个优惠'),
                        backgroundColor: Colors.orange.shade100,
                      )
                    : null,
              ),
            );
          },
        );
      },
    );
  }
}

/// 监控设置标签页
class _MonitorSettingsTab extends StatelessWidget {
  const _MonitorSettingsTab();

  @override
  Widget build(BuildContext context) {
    return Consumer<GeofenceServiceManager>(
      builder: (context, manager, child) {
        return ListView(
          padding: const EdgeInsets.all(16),
          children: [
            Card(
              child: ListTile(
                leading: const Icon(Icons.info_outline),
                title: const Text('监控状态'),
                subtitle: Text(manager.isMonitoring ? '运行中' : '已停止'),
                trailing: Switch(
                  value: manager.isMonitoring,
                  onChanged: (value) async {
                    if (value) {
                      await manager.startMonitoring();
                    } else {
                      await manager.stopMonitoring();
                    }
                  },
                ),
              ),
            ),
            const SizedBox(height: 8),
            Card(
              child: Column(
                children: [
                  ListTile(
                    leading: const Icon(Icons.location_on),
                    title: const Text('围栏数量'),
                    trailing: Text('${manager.geofenceCount}个'),
                  ),
                  const Divider(height: 1),
                  ListTile(
                    leading: const Icon(Icons.gps_fixed),
                    title: const Text('最后位置'),
                    subtitle: manager.lastPosition != null
                        ? Text(
                            '${manager.lastPosition!.latitude.toStringAsFixed(6)}, '
                            '${manager.lastPosition!.longitude.toStringAsFixed(6)}',
                          )
                        : const Text('暂无位置'),
                  ),
                ],
              ),
            ),
            const SizedBox(height: 16),
            ElevatedButton.icon(
              onPressed: () => _showClearConfirm(context, manager),
              icon: const Icon(Icons.delete_forever),
              label: const Text('清空所有围栏'),
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.red,
                foregroundColor: Colors.white,
              ),
            ),
          ],
        );
      },
    );
  }
  
  void _showClearConfirm(BuildContext context, GeofenceServiceManager manager) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('清空围栏'),
        content: const Text('确定要删除所有围栏吗？此操作不可恢复。'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () async {
              Navigator.pop(context);
              await manager.clearAllGeofences();
            },
            child: const Text('清空', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }
}

/// 添加围栏对话框（简化版）
class AddGeofenceDialog extends StatefulWidget {
  const AddGeofenceDialog({super.key});

  @override
  State<AddGeofenceDialog> createState() => _AddGeofenceDialogState();
}

class _AddGeofenceDialogState extends State<AddGeofenceDialog> {
  final _nameController = TextEditingController();
  final _radiusController = TextEditingController(text: '100');
  GeofenceType _type = GeofenceType.circle;

  @override
  void dispose() {
    _nameController.dispose();
    _radiusController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('添加围栏'),
      content: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: _nameController,
              decoration: const InputDecoration(
                labelText: '围栏名称',
                hintText: '例如：XX商店',
              ),
            ),
            const SizedBox(height: 16),
            DropdownButtonFormField<GeofenceType>(
              value: _type,
              decoration: const InputDecoration(labelText: '围栏类型'),
              items: GeofenceType.values.map((type) {
                return DropdownMenuItem(
                  value: type,
                  child: Text(type.name),
                );
              }).toList(),
              onChanged: (value) {
                if (value != null) {
                  setState(() => _type = value);
                }
              },
            ),
            if (_type == GeofenceType.circle) ...[
              const SizedBox(height: 16),
              TextField(
                controller: _radiusController,
                decoration: const InputDecoration(
                  labelText: '半径（米）',
                ),
                keyboardType: TextInputType.number,
              ),
            ],
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('取消'),
        ),
        TextButton(
          onPressed: () => _addGeofence(context),
          child: const Text('添加'),
        ),
      ],
    );
  }
  
  Future<void> _addGeofence(BuildContext context) async {
    final name = _nameController.text.trim();
    if (name.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('请输入围栏名称')),
      );
      return;
    }
    
    final manager = context.read<GeofenceServiceManager>();
    final lastPosition = manager.lastPosition;
    
    if (lastPosition == null) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('请先获取当前位置')),
      );
      return;
    }
    
    final geofence = Geofence(
      id: 'gf_${DateTime.now().millisecondsSinceEpoch}',
      name: name,
      type: _type,
      latitude: lastPosition.latitude,
      longitude: lastPosition.longitude,
      radius: _type == GeofenceType.circle
          ? double.tryParse(_radiusController.text) ?? 100
          : null,
      triggers: const [GeofenceEvent.enter, GeofenceEvent.exit],
      createdAt: DateTime.now(),
    );
    
    await manager.registerGeofence(geofence);
    
    if (mounted) {
      Navigator.pop(context);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('围栏添加成功')),
      );
    }
  }
}
