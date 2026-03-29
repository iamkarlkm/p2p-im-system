import 'package:flutter/material.dart';
import '../models/screenshot_notification.dart';
import '../services/screenshot_notification_service.dart';

class ScreenshotNotificationScreen extends StatefulWidget {
  const ScreenshotNotificationScreen({super.key});

  @override
  State<ScreenshotNotificationScreen> createState() => _ScreenshotNotificationScreenState();
}

class _ScreenshotNotificationScreenState extends State<ScreenshotNotificationScreen> {
  final ScreenshotNotificationService _service = ScreenshotNotificationService();
  ScreenshotSettings? _settings;
  List<ScreenshotEvent> _history = [];
  bool _isLoading = false;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    try {
      final settings = await _service.getSettings();
      final history = await _service.getHistory();
      setState(() {
        _settings = settings;
        _history = history;
        _isLoading = false;
      });
    } catch (e) {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _updateSettings(ScreenshotSettings newSettings) async {
    try {
      final updated = await _service.updateSettings(newSettings);
      setState(() => _settings = updated);
    } catch (e) {
      // Handle error
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('截屏通知')),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadData,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  _buildSettingsSection(),
                  const SizedBox(height: 24),
                  _buildHistorySection(),
                ],
              ),
            ),
    );
  }

  Widget _buildSettingsSection() {
    final settings = _settings;
    if (settings == null) return const SizedBox();

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('通知设置', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
            const SizedBox(height: 12),
            SwitchListTile(
              title: const Text('启用截屏通知'),
              value: settings.enableScreenshotNotification,
              onChanged: (v) => _updateSettings(settings.copyWith(enableScreenshotNotification: v)),
            ),
            SwitchListTile(
              title: const Text('截屏时通知对方'),
              value: settings.notifyOnCapture,
              onChanged: (v) => _updateSettings(settings.copyWith(notifyOnCapture: v)),
            ),
            SwitchListTile(
              title: const Text('接收截屏提醒'),
              value: settings.receiveScreenshotAlerts,
              onChanged: (v) => _updateSettings(settings.copyWith(receiveScreenshotAlerts: v)),
            ),
            SwitchListTile(
              title: const Text('联系人截屏提醒'),
              value: settings.alertForContacts,
              onChanged: (v) => _updateSettings(settings.copyWith(alertForContacts: v)),
            ),
            SwitchListTile(
              title: const Text('群聊截屏提醒'),
              value: settings.alertForGroups,
              onChanged: (v) => _updateSettings(settings.copyWith(alertForGroups: v)),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildHistorySection() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text('截屏历史', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
        const SizedBox(height: 12),
        if (_history.isEmpty)
          const Center(
            child: Padding(
              padding: EdgeInsets.all(32),
              child: Text('暂无截屏记录', style: TextStyle(color: Colors.grey)),
            ),
          )
        else
          ...(_history.map((event) => Card(
                margin: const EdgeInsets.only(bottom: 8),
                child: ListTile(
                  leading: const CircleAvatar(child: Text('📸')),
                  title: Text(event.capturedByUsername),
                  subtitle: Text(_formatTime(event.screenshotTime)),
                  trailing: Text(
                    event.conversationType == 'private' ? '私聊' : '群聊',
                    style: const TextStyle(color: Colors.grey, fontSize: 12),
                  ),
                ),
              ))),
      ],
    );
  }

  String _formatTime(DateTime dt) {
    return '${dt.year}-${dt.month.toString().padLeft(2, '0')}-${dt.day.toString().padLeft(2, '0')} ${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
  }
}
