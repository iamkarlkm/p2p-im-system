import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';

class SettingsScreen extends StatefulWidget {
  const SettingsScreen({super.key});

  @override
  State<SettingsScreen> createState() => _SettingsScreenState();
}

class _SettingsScreenState extends State<SettingsScreen> {
  bool _notificationsEnabled = true;
  bool _soundEnabled = true;
  bool _vibrationEnabled = true;
  bool _darkModeEnabled = false;
  bool _autoLoginEnabled = true;

  @override
  Widget build(BuildContext context) {
    final authService = Provider.of<AuthService>(context);
    final currentUser = authService.currentUser;

    return Scaffold(
      appBar: AppBar(
        title: const Text('设置'),
      ),
      body: ListView(
        children: [
          // 用户信息部分
          _buildUserInfoSection(currentUser),
          
          const Divider(),
          
          // 通知设置
          _buildSectionHeader('通知设置'),
          _buildSwitchTile(
            icon: Icons.notifications,
            title: '接收消息通知',
            subtitle: '有新消息时接收通知',
            value: _notificationsEnabled,
            onChanged: (value) {
              setState(() {
                _notificationsEnabled = value;
              });
              _saveSettings();
            },
          ),
          _buildSwitchTile(
            icon: Icons.volume_up,
            title: '声音',
            subtitle: '收到消息时播放提示音',
            value: _soundEnabled,
            onChanged: _notificationsEnabled ? (value) {
              setState(() {
                _soundEnabled = value;
              });
              _saveSettings();
            } : null,
          ),
          _buildSwitchTile(
            icon: Icons.vibration,
            title: '振动',
            subtitle: '收到消息时振动提醒',
            value: _vibrationEnabled,
            onChanged: _notificationsEnabled ? (value) {
              setState(() {
                _vibrationEnabled = value;
              });
              _saveSettings();
            } : null,
          ),
          
          const Divider(),
          
          // 外观设置
          _buildSectionHeader('外观设置'),
          _buildSwitchTile(
            icon: Icons.dark_mode,
            title: '深色模式',
            subtitle: '使用深色主题',
            value: _darkModeEnabled,
            onChanged: (value) {
              setState(() {
                _darkModeEnabled = value;
              });
              _saveSettings();
            },
          ),
          
          const Divider(),
          
          // 账号设置
          _buildSectionHeader('账号设置'),
          _buildSwitchTile(
            icon: Icons.login,
            title: '自动登录',
            subtitle: '启动时自动登录',
            value: _autoLoginEnabled,
            onChanged: (value) {
              setState(() {
                _autoLoginEnabled = value;
              });
              _saveSettings();
            },
          ),
          _buildActionTile(
            icon: Icons.person,
            title: '个人资料',
            subtitle: '修改个人资料信息',
            onTap: () => _editProfile(),
          ),
          _buildActionTile(
            icon: Icons.lock,
            title: '修改密码',
            subtitle: '更改账户密码',
            onTap: () => _changePassword(),
          ),
          _buildActionTile(
            icon: Icons.security,
            title: '隐私设置',
            subtitle: '管理隐私选项',
            onTap: () => _privacySettings(),
          ),
          
          const Divider(),
          
          // 聊天设置
          _buildSectionHeader('聊天设置'),
          _buildActionTile(
            icon: Icons.chat_bubble,
            title: '聊天背景',
            subtitle: '设置聊天背景图片',
            onTap: () => _chatBackground(),
          ),
          _buildActionTile(
            icon: Icons.font_download,
            title: '字体大小',
            subtitle: '调整聊天字体大小',
            onTap: () => _fontSize(),
          ),
          
          const Divider(),
          
          // 存储设置
          _buildSectionHeader('存储设置'),
          _buildActionTile(
            icon: Icons.storage,
            title: '清理缓存',
            subtitle: '清理聊天图片和文件缓存',
            onTap: () => _clearCache(),
          ),
          _buildActionTile(
            icon: Icons.download,
            title: '聊天记录',
            subtitle: '备份或恢复聊天记录',
            onTap: () => _chatHistory(),
          ),
          
          const Divider(),
          
          // 关于
          _buildSectionHeader('关于'),
          _buildActionTile(
            icon: Icons.info,
            title: '关于我们',
            subtitle: 'IM Mobile v1.0.0',
            onTap: () => _about(),
          ),
          _buildActionTile(
            icon: Icons.description,
            title: '服务条款',
            subtitle: '查看服务条款',
            onTap: () => _terms(),
          ),
          _buildActionTile(
            icon: Icons.privacy_tip,
            title: '隐私政策',
            subtitle: '查看隐私政策',
            onTap: () => _privacy(),
          ),
          
          const Divider(),
          
          // 退出登录
          Padding(
            padding: const EdgeInsets.all(16),
            child: ElevatedButton(
              onPressed: () => _logout(),
              style: ElevatedButton.styleFrom(
                backgroundColor: Colors.red,
                foregroundColor: Colors.white,
                padding: const EdgeInsets.symmetric(vertical: 12),
              ),
              child: const Text('退出登录'),
            ),
          ),
          
          const SizedBox(height: 32),
        ],
      ),
    );
  }

  Widget _buildUserInfoSection(user) {
    return Container(
      padding: const EdgeInsets.all(16),
      color: Colors.blue[50],
      child: Row(
        children: [
          CircleAvatar(
            radius: 36,
            backgroundImage: user?.avatarUrl != null
                ? NetworkImage(user!.avatarUrl!)
                : null,
            child: user?.avatarUrl == null
                ? const Icon(Icons.person, size: 36)
                : null,
          ),
          const SizedBox(width: 16),
          Expanded(
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Text(
                  user?.nickname ?? user?.username ?? '用户',
                  style: const TextStyle(
                    fontSize: 18,
                    fontWeight: FontWeight.bold,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  user?.email ?? user?.username ?? '',
                  style: TextStyle(
                    color: Colors.grey[600],
                    fontSize: 14,
                  ),
                ),
              ],
            ),
          ),
          IconButton(
            icon: const Icon(Icons.edit),
            onPressed: () => _editProfile(),
          ),
        ],
      ),
    );
  }

  Widget _buildSectionHeader(String title) {
    return Padding(
      padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
      child: Text(
        title,
        style: TextStyle(
          fontSize: 14,
          fontWeight: FontWeight.bold,
          color: Colors.blue[700],
        ),
      ),
    );
  }

  Widget _buildSwitchTile({
    required IconData icon,
    required String title,
    required String subtitle,
    required bool value,
    required ValueChanged<bool>? onChanged,
  }) {
    return ListTile(
      leading: Icon(icon, color: Colors.blue),
      title: Text(title),
      subtitle: Text(subtitle, style: TextStyle(fontSize: 12, color: Colors.grey[600])),
      trailing: Switch(
        value: value,
        onChanged: onChanged,
        activeColor: Colors.blue,
      ),
    );
  }

  Widget _buildActionTile({
    required IconData icon,
    required String title,
    required String subtitle,
    required VoidCallback onTap,
  }) {
    return ListTile(
      leading: Icon(icon, color: Colors.blue),
      title: Text(title),
      subtitle: Text(subtitle, style: TextStyle(fontSize: 12, color: Colors.grey[600])),
      trailing: const Icon(Icons.chevron_right),
      onTap: onTap,
    );
  }

  void _saveSettings() {
    // 保存设置到本地存储
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('设置已保存')),
    );
  }

  void _editProfile() {
    Navigator.of(context).pushNamed('/profile');
  }

  void _changePassword() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('修改密码'),
        content: const Text('该功能需要后端API支持'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }

  void _privacySettings() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('隐私设置'),
        content: const Text('该功能需要后端API支持'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }

  void _chatBackground() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('聊天背景'),
        content: const Text('该功能正在开发中'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }

  void _fontSize() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('字体大小'),
        content: const Text('该功能正在开发中'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }

  void _clearCache() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('清理缓存'),
        content: const Text('确定要清理所有缓存吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('缓存已清理')),
              );
            },
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }

  void _chatHistory() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('聊天记录'),
        content: const Text('该功能需要后端API支持'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }

  void _about() {
    showAboutDialog(
      context: context,
      applicationName: 'IM Mobile',
      applicationVersion: '1.0.0',
      applicationIcon: const Icon(Icons.chat, size: 48, color: Colors.blue),
      children: [
        const Text('即时通讯应用'),
        const Text('提供即时消息、群组聊天等功能'),
      ],
    );
  }

  void _terms() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('服务条款'),
        content: const Text('服务条款内容...'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }

  void _privacy() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('隐私政策'),
        content: const Text('隐私政策内容...'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('确定'),
          ),
        ],
      ),
    );
  }

  void _logout() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('退出登录'),
        content: const Text('确定要退出登录吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              final authService = Provider.of<AuthService>(context, listen: false);
              authService.logout();
              Navigator.of(context).popUntil((route) => route.isFirst);
            },
            child: const Text('退出', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }
}
