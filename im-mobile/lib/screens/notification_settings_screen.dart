import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/push_notification_service.dart';
import '../models/push_message.dart';

class NotificationSettingsScreen extends StatefulWidget {
  const NotificationSettingsScreen({super.key});

  @override
  State<NotificationSettingsScreen> createState() => _NotificationSettingsScreenState();
}

class _NotificationSettingsScreenState extends State<NotificationSettingsScreen> {
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('通知设置'),
      ),
      body: Consumer<PushNotificationService>(
        builder: (context, service, child) {
          return ListView(
            children: [
              _buildMasterSwitch(service),
              const Divider(),
              _buildSoundSettings(service),
              const Divider(),
              _buildVibrationSettings(service),
              const Divider(),
              _buildNotificationTypes(),
              const Divider(),
              _buildMutedConversations(service),
              const Divider(),
              _buildTestSection(service),
            ],
          );
        },
      ),
    );
  }

  Widget _buildMasterSwitch(PushNotificationService service) {
    return SwitchListTile(
      title: const Text('接收推送通知'),
      subtitle: const Text('开启后将接收消息、通话等通知'),
      value: service.notificationsEnabled,
      onChanged: (value) => service.setNotificationsEnabled(value),
      secondary: Icon(
        service.notificationsEnabled ? Icons.notifications_active : Icons.notifications_off,
        color: service.notificationsEnabled ? Colors.blue : Colors.grey,
      ),
    );
  }

  Widget _buildSoundSettings(PushNotificationService service) {
    return SwitchListTile(
      title: const Text('提示音'),
      subtitle: const Text('收到通知时播放声音'),
      value: service.soundEnabled,
      onChanged: service.notificationsEnabled
          ? (value) => service.setSoundEnabled(value)
          : null,
      secondary: const Icon(Icons.volume_up),
    );
  }

  Widget _buildVibrationSettings(PushNotificationService service) {
    return SwitchListTile(
      title: const Text('振动'),
      subtitle: const Text('收到通知时振动'),
      value: service.vibrationEnabled,
      onChanged: service.notificationsEnabled
          ? (value) => service.setVibrationEnabled(value)
          : null,
      secondary: const Icon(Icons.vibration),
    );
  }

  Widget _buildNotificationTypes() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Padding(
          padding: EdgeInsets.fromLTRB(16, 16, 16, 8),
          child: Text(
            '通知类型',
            style: TextStyle(
              fontSize: 14,
              fontWeight: FontWeight.bold,
              color: Colors.grey,
            ),
          ),
        ),
        CheckboxListTile(
          title: const Text('消息通知'),
          subtitle: const Text('新消息提醒'),
          value: true,
          onChanged: (v) {},
          secondary: const Icon(Icons.message),
        ),
        CheckboxListTile(
          title: const Text('通话通知'),
          subtitle: const Text('语音/视频通话邀请'),
          value: true,
          onChanged: (v) {},
          secondary: const Icon(Icons.call),
        ),
        CheckboxListTile(
          title: const Text('系统通知'),
          subtitle: const Text('应用更新、安全提醒等'),
          value: true,
          onChanged: (v) {},
          secondary: const Icon(Icons.info),
        ),
      ],
    );
  }

  Widget _buildMutedConversations(PushNotificationService service) {
    return ListTile(
      leading: const Icon(Icons.do_not_disturb_on),
      title: const Text('免打扰设置'),
      subtitle: const Text('管理静音的会话和用户'),
      trailing: const Icon(Icons.chevron_right),
      onTap: () {
        Navigator.push(
          context,
          MaterialPageRoute(
            builder: (_) => const MutedConversationsScreen(),
          ),
        );
      },
    );
  }

  Widget _buildTestSection(PushNotificationService service) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Padding(
          padding: EdgeInsets.fromLTRB(16, 16, 16, 8),
          child: Text(
            '测试',
            style: TextStyle(
              fontSize: 14,
              fontWeight: FontWeight.bold,
              color: Colors.grey,
            ),
          ),
        ),
        ListTile(
          leading: const Icon(Icons.notification_add, color: Colors.green),
          title: const Text('发送测试通知'),
          subtitle: const Text('立即发送一条测试消息'),
          onTap: () => _sendTestNotification(service),
        ),
        ListTile(
          leading: const Icon(Icons.call, color: Colors.blue),
          title: const Text('测试通话通知'),
          subtitle: const Text('模拟来电通知'),
          onTap: () => _sendTestCallNotification(service),
        ),
        ListTile(
          leading: const Icon(Icons.delete_outline, color: Colors.red),
          title: const Text('清除所有通知'),
          onTap: () => service.cancelAllNotifications(),
        ),
      ],
    );
  }

  void _sendTestNotification(PushNotificationService service) {
    final message = PushMessage(
      id: DateTime.now().millisecondsSinceEpoch.toString(),
      type: 'text',
      senderId: 'test_user',
      senderName: '测试用户',
      conversationId: 'test_conv',
      content: '这是一条测试消息，用于验证通知功能是否正常工作。',
      timestamp: DateTime.now(),
    );
    service.showMessageNotification(message);
    
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('测试通知已发送')),
    );
  }

  void _sendTestCallNotification(PushNotificationService service) {
    service.showCallNotification(
      callerId: 'test_caller',
      callerName: '测试来电',
      callType: 'audio',
      callId: 'test_call_${DateTime.now().millisecondsSinceEpoch}',
    );
    
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('通话通知已发送')),
    );
  }
}

class MutedConversationsScreen extends StatelessWidget {
  const MutedConversationsScreen({super.key});

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('免打扰设置'),
      ),
      body: const Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.do_not_disturb, size: 64, color: Colors.grey),
            SizedBox(height: 16),
            Text(
              '暂无静音的会话',
              style: TextStyle(color: Colors.grey),
            ),
          ],
        ),
      ),
    );
  }
}
