import 'package:flutter/material.dart';
import '../models/device.dart';
import '../services/device_service.dart';

class DeviceManagementScreen extends StatefulWidget {
  final DeviceService deviceService;

  const DeviceManagementScreen({super.key, required this.deviceService});

  @override
  State<DeviceManagementScreen> createState() => _DeviceManagementScreenState();
}

class _DeviceManagementScreenState extends State<DeviceManagementScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;
  List<Device> _devices = [];
  DeviceStats? _stats;
  LoginHistoryPage? _history;
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 2, vsync: this);
    _loadData();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadData() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });
    try {
      final results = await Future.wait([
        widget.deviceService.getUserDevices(),
        widget.deviceService.getDeviceStats(),
      ]);
      if (mounted) {
        setState(() {
          _devices = results[0] as List<Device>;
          _stats = results[1] as DeviceStats;
          _isLoading = false;
        });
      }
    } catch (e) {
      if (mounted) {
        setState(() {
          _error = e.toString();
          _isLoading = false;
        });
      }
    }
  }

  Future<void> _loadHistory() async {
    try {
      final history = await widget.deviceService.getLoginHistory();
      if (mounted) {
        setState(() {
          _history = history;
        });
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('加载登录历史失败: $e')),
        );
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('设备管理'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadData,
          ),
        ],
        bottom: TabBar(
          controller: _tabController,
          onTap: (index) {
            if (index == 1 && _history == null) {
              _loadHistory();
            }
          },
          tabs: const [
            Tab(text: '设备列表'),
            Tab(text: '登录历史'),
          ],
        ),
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? Center(
                  child: Column(
                    mainAxisAlignment: MainAxisAlignment.center,
                    children: [
                      Text(_error!, style: const TextStyle(color: Colors.red)),
                      const SizedBox(height: 16),
                      ElevatedButton(
                        onPressed: _loadData,
                        child: const Text('重试'),
                      ),
                    ],
                  ),
                )
              : TabBarView(
                  controller: _tabController,
                  children: [
                    _buildDeviceList(),
                    _buildLoginHistory(),
                  ],
                ),
    );
  }

  Widget _buildDeviceList() {
    if (_devices.isEmpty) {
      return const Center(child: Text('暂无设备记录'));
    }

    return Column(
      children: [
        if (_stats != null) _buildStatsCards(),
        Expanded(
          child: RefreshIndicator(
            onRefresh: _loadData,
            child: ListView.separated(
              padding: const EdgeInsets.all(16),
              itemCount: _devices.length,
              separatorBuilder: (_, __) => const SizedBox(height: 12),
              itemBuilder: (context, index) {
                final device = _devices[index];
                return _buildDeviceCard(device);
              },
            ),
          ),
        ),
      ],
    );
  }

  Widget _buildStatsCards() {
    return Container(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          _statItem('📱', '${_stats!.totalDevices}', '总设备'),
          _statItem('✅', '${_stats!.activeDevices}', '活跃'),
          _statItem('🔐', '${_stats!.trustedDevices}', '可信'),
          _statItem(
            widget.deviceService.getDeviceIcon(_stats!.mostUsedDeviceType),
            _stats!.mostUsedDeviceType.name.toUpperCase(),
            '常用',
          ),
        ],
      ),
    );
  }

  Widget _statItem(String icon, String value, String label) {
    return Expanded(
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 12),
        margin: const EdgeInsets.symmetric(horizontal: 4),
        decoration: BoxDecoration(
          color: Colors.grey[100],
          borderRadius: BorderRadius.circular(8),
        ),
        child: Column(
          children: [
            Text(icon, style: const TextStyle(fontSize: 20)),
            const SizedBox(height: 4),
            Text(value, style: const TextStyle(fontWeight: FontWeight.bold)),
            Text(label, style: TextStyle(fontSize: 11, color: Colors.grey[600])),
          ],
        ),
      ),
    );
  }

  Widget _buildDeviceCard(Device device) {
    final icon = widget.deviceService.getDeviceIcon(device.deviceType);
    final lastActive = widget.deviceService.formatLastActive(device.lastActiveAt);

    return Card(
      elevation: device.isCurrent ? 2 : 0,
      color: device.isCurrent ? Colors.blue[50] : null,
      shape: RoundedRectangleBorder(
        borderRadius: BorderRadius.circular(12),
        side: BorderSide(
          color: device.isCurrent ? Colors.blue[200]! : Colors.grey[300]!,
        ),
      ),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Text(icon, style: const TextStyle(fontSize: 32)),
                const SizedBox(width: 12),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Row(
                        children: [
                          Expanded(
                            child: Text(
                              device.deviceName,
                              style: const TextStyle(
                                fontWeight: FontWeight.w600,
                                fontSize: 16,
                              ),
                            ),
                          ),
                          if (device.isCurrent)
                            Container(
                              padding: const EdgeInsets.symmetric(
                                horizontal: 8,
                                vertical: 2,
                              ),
                              decoration: BoxDecoration(
                                color: Colors.blue,
                                borderRadius: BorderRadius.circular(4),
                              ),
                              child: const Text(
                                '当前',
                                style: TextStyle(color: Colors.white, fontSize: 11),
                              ),
                            ),
                          if (device.isTrusted) ...[
                            const SizedBox(width: 4),
                            Container(
                              padding: const EdgeInsets.symmetric(
                                horizontal: 8,
                                vertical: 2,
                              ),
                              decoration: BoxDecoration(
                                color: Colors.green,
                                borderRadius: BorderRadius.circular(4),
                              ),
                              child: const Text(
                                '可信',
                                style: TextStyle(color: Colors.white, fontSize: 11),
                              ),
                            ),
                          ],
                          if (!device.isActive) ...[
                            const SizedBox(width: 4),
                            Container(
                              padding: const EdgeInsets.symmetric(
                                horizontal: 8,
                                vertical: 2,
                              ),
                              decoration: BoxDecoration(
                                color: Colors.grey,
                                borderRadius: BorderRadius.circular(4),
                              ),
                              child: const Text(
                                '离线',
                                style: TextStyle(color: Colors.white, fontSize: 11),
                              ),
                            ),
                          ],
                        ],
                      ),
                      const SizedBox(height: 4),
                      Text(
                        device.deviceModel.isNotEmpty
                            ? device.deviceModel
                            : device.deviceType.name,
                        style: TextStyle(fontSize: 13, color: Colors.grey[700]),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                const Icon(Icons.location_on, size: 14, color: Colors.grey),
                const SizedBox(width: 4),
                Text(
                  device.location.isNotEmpty
                      ? device.location
                      : (device.ipAddress.isNotEmpty ? device.ipAddress : '未知'),
                  style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                ),
                const Spacer(),
                Text(
                  '最后活跃: $lastActive',
                  style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                ),
              ],
            ),
            if (device.isActive && !device.isCurrent) ...[
              const SizedBox(height: 12),
              Row(
                mainAxisAlignment: MainAxisAlignment.end,
                children: [
                  if (!device.isTrusted)
                    _actionBtn('信任', Colors.green, () => _trustDevice(device.id)),
                  const SizedBox(width: 8),
                  _actionBtn(
                    '注销',
                    Colors.red,
                    () => _deactivateDevice(device.id),
                  ),
                ],
              ),
            ],
          ],
        ),
      ),
    );
  }

  Widget _actionBtn(String label, Color color, VoidCallback onPressed) {
    return TextButton(
      onPressed: onPressed,
      style: TextButton.styleFrom(
        foregroundColor: color,
        padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
        backgroundColor: color.withOpacity(0.1),
        shape: RoundedRectangleBorder(
          borderRadius: BorderRadius.circular(8),
        ),
      ),
      child: Text(label),
    );
  }

  Widget _buildLoginHistory() {
    if (_history == null) {
      return Center(
        child: ElevatedButton(
          onPressed: _loadHistory,
          child: const Text('加载登录历史'),
        ),
      );
    }

    if (_history!.items.isEmpty) {
      return const Center(child: Text('暂无登录记录'));
    }

    return RefreshIndicator(
      onRefresh: _loadHistory,
      child: ListView.separated(
        padding: const EdgeInsets.all(16),
        itemCount: _history!.items.length,
        separatorBuilder: (_, __) => const SizedBox(height: 8),
        itemBuilder: (context, index) {
          final entry = _history!.items[index];
          return _buildHistoryItem(entry);
        },
      ),
    );
  }

  Widget _buildHistoryItem(LoginHistoryEntry entry) {
    final icon = widget.deviceService.getDeviceIcon(entry.deviceType);
    final actionText = entry.action == LoginAction.login
        ? '登录'
        : entry.action == LoginAction.logout
            ? '退出'
            : '移除';
    final statusColor =
        entry.loginStatus == 'SUCCESS' ? Colors.green : Colors.red;

    return Card(
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
      child: Padding(
        padding: const EdgeInsets.all(12),
        child: Row(
          children: [
            Text(icon, style: const TextStyle(fontSize: 28)),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  Row(
                    children: [
                      Text(
                        actionText,
                        style: const TextStyle(fontWeight: FontWeight.w600),
                      ),
                      const SizedBox(width: 8),
                      Container(
                        padding: const EdgeInsets.symmetric(
                          horizontal: 6,
                          vertical: 1,
                        ),
                        decoration: BoxDecoration(
                          color: statusColor.withOpacity(0.1),
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: Text(
                          entry.loginStatus,
                          style: TextStyle(
                            color: statusColor,
                            fontSize: 11,
                          ),
                        ),
                      ),
                    ],
                  ),
                  const SizedBox(height: 4),
                  Text(
                    '${entry.deviceName} · ${entry.ipAddress}',
                    style: TextStyle(fontSize: 12, color: Colors.grey[700]),
                  ),
                  Text(
                    entry.location.isNotEmpty ? entry.location : '未知位置',
                    style: TextStyle(fontSize: 12, color: Colors.grey[500]),
                  ),
                  Text(
                    _formatDateTime(entry.loginTime),
                    style: TextStyle(fontSize: 11, color: Colors.grey[400]),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  String _formatDateTime(DateTime dt) {
    return '${dt.year}-${dt.month.toString().padLeft(2, '0')}-${dt.day.toString().padLeft(2, '0')} '
        '${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
  }

  Future<void> _trustDevice(int deviceId) async {
    try {
      await widget.deviceService.trustDevice(deviceId);
      await _loadData();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('设备已标记为可信')),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('操作失败: $e')),
        );
      }
    }
  }

  Future<void> _deactivateDevice(int deviceId) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('确认注销'),
        content: const Text('确定要注销该设备吗？该设备将被强制退出登录。'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('确定'),
          ),
        ],
      ),
    );

    if (confirm != true) return;

    try {
      await widget.deviceService.deactivateDevice(deviceId);
      await _loadData();
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('设备已注销')),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('操作失败: $e')),
        );
      }
    }
  }
}
