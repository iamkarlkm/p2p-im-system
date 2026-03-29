import 'package:flutter/material.dart';
import '../models/login_device.dart';
import '../services/login_device_service.dart';

class LoginDeviceScreen extends StatefulWidget {
  const LoginDeviceScreen({super.key});

  @override
  State<LoginDeviceScreen> createState() => _LoginDeviceScreenState();
}

class _LoginDeviceScreenState extends State<LoginDeviceScreen> {
  final LoginDeviceService _service = LoginDeviceService();
  List<LoginDevice> _devices = [];
  bool _loading = true;

  @override
  void initState() {
    super.initState();
    _loadDevices();
  }

  Future<void> _loadDevices() async {
    setState(() => _loading = true);
    try {
      _devices = await _service.getDevices();
    } catch (e) {
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('加载设备失败: $e')),
      );
    }
    if (mounted) setState(() => _loading = false);
  }

  Future<void> _terminateDevice(LoginDevice device) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('退出设备'),
        content: Text('确定要退出 "${device.deviceName ?? device.deviceId}" 吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx, false),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(ctx, true),
            child: const Text('确认退出'),
          ),
        ],
      ),
    );
    if (confirmed == true) {
      try {
        await _service.terminateDevice(device.deviceId);
        _devices.removeWhere((d) => d.deviceId == device.deviceId);
        if (mounted) setState(() {});
      } catch (e) {
        if (mounted) ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('退出失败: $e')),
        );
      }
    }
  }

  String _deviceTypeIcon(String? type) {
    switch (type?.toLowerCase()) {
      case 'desktop': return '💻';
      case 'mobile': return '📱';
      case 'tablet': return '📲';
      case 'web': return '🌐';
      default: return '💻';
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('登录设备管理'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadDevices,
          ),
        ],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _devices.isEmpty
              ? const Center(child: Text('暂无登录设备'))
              : RefreshIndicator(
                  onRefresh: _loadDevices,
                  child: ListView.builder(
                    itemCount: _devices.length,
                    itemBuilder: (context, index) {
                      final device = _devices[index];
                      return Card(
                        margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
                        child: ListTile(
                          leading: CircleAvatar(
                            child: Text(_deviceTypeIcon(device.deviceType)),
                          ),
                          title: Row(
                            children: [
                              Expanded(
                                child: Text(
                                  device.deviceName ?? device.deviceId,
                                  overflow: TextOverflow.ellipsis,
                                ),
                              ),
                              if (device.isCurrent)
                                Container(
                                  padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                                  decoration: BoxDecoration(
                                    color: Colors.green,
                                    borderRadius: BorderRadius.circular(8),
                                  ),
                                  child: const Text(
                                    '当前',
                                    style: TextStyle(color: Colors.white, fontSize: 12),
                                  ),
                                ),
                              if (device.isTrusted)
                                const Padding(
                                  padding: EdgeInsets.only(left: 4),
                                  child: Icon(Icons.verified, color: Colors.blue, size: 16),
                                ),
                            ],
                          ),
                          subtitle: Column(
                            crossAxisAlignment: CrossAxisAlignment.start,
                            children: [
                              if (device.deviceModel != null)
                                Text('型号: ${device.deviceModel}'),
                              if (device.osVersion != null)
                                Text('系统: ${device.osVersion}'),
                              if (device.location != null)
                                Text('位置: ${device.location}'),
                              if (device.ipAddress != null)
                                Text('IP: ${device.ipAddress}'),
                              Text('最后活跃: ${device.lastActiveTime}'),
                            ],
                          ),
                          isThreeLine: true,
                          trailing: device.isCurrent
                              ? null
                              : IconButton(
                                  icon: const Icon(Icons.logout, color: Colors.red),
                                  onPressed: () => _terminateDevice(device),
                                  tooltip: '退出此设备',
                                ),
                        ),
                      );
                    },
                  ),
                ),
    );
  }
}
