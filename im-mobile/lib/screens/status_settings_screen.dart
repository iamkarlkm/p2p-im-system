import 'package:flutter/material.dart';
import '../components/status_selector.dart';
import '../components/status_subscription_manager.dart';
import '../components/friend_list_with_status.dart';

class StatusSettingsScreen extends StatefulWidget {
  const StatusSettingsScreen({super.key});

  @override
  State<StatusSettingsScreen> createState() => _StatusSettingsScreenState();
}

class _StatusSettingsScreenState extends State<StatusSettingsScreen>
    with SingleTickerProviderStateMixin {
  late TabController _tabController;

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 3, vsync: this);
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('状态管理'),
        bottom: TabBar(
          controller: _tabController,
          tabs: const [
            Tab(icon: Icon(Icons.emoji_emotions), text: '我的状态'),
            Tab(icon: Icon(Icons.people), text: '好友状态'),
            Tab(icon: Icon(Icons.notifications), text: '订阅管理'),
          ],
        ),
      ),
      body: TabBarView(
        controller: _tabController,
        children: const [
          _MyStatusTab(),
          _FriendsStatusTab(),
          _SubscriptionTab(),
        ],
      ),
    );
  }
}

class _MyStatusTab extends StatelessWidget {
  const _MyStatusTab();

  @override
  Widget build(BuildContext context) {
    return const SingleChildScrollView(
      child: Column(
        children: [
          StatusSelector(),
          _StatusPrivacyCard(),
          _StatusHelpCard(),
        ],
      ),
    );
  }
}

class _StatusPrivacyCard extends StatelessWidget {
  const _StatusPrivacyCard();

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 16),
      child: Column(
        children: [
          ListTile(
            leading: const Icon(Icons.privacy_tip_outlined),
            title: const Text('状态可见性'),
            subtitle: const Text('所有人可以看到我的在线状态'),
            trailing: Switch(value: true, onChanged: (v) {}),
          ),
          const Divider(height: 1),
          ListTile(
            leading: const Icon(Icons.schedule),
            title: const Text('显示最后在线时间'),
            subtitle: const Text('好友可以看到我的最后活跃时间'),
            trailing: Switch(value: true, onChanged: (v) {}),
          ),
        ],
      ),
    );
  }
}

class _StatusHelpCard extends StatelessWidget {
  const _StatusHelpCard();

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.all(16),
      child: ExpansionTile(
        leading: const Icon(Icons.help_outline),
        title: const Text('状态说明'),
        children: [
          _buildStatusHelpItem(
            icon: Icons.circle,
            color: Colors.green,
            title: '在线',
            description: '你当前正在使用应用',
          ),
          _buildStatusHelpItem(
            icon: Icons.access_time,
            color: Colors.orange,
            title: '离开',
            description: '你暂时离开了应用',
          ),
          _buildStatusHelpItem(
            icon: Icons.do_not_disturb_on,
            color: Colors.red,
            title: '忙碌',
            description: '你不希望被打扰',
          ),
          _buildStatusHelpItem(
            icon: Icons.visibility_off,
            color: Colors.grey,
            title: '隐身',
            description: '你看起来是离线的',
          ),
        ],
      ),
    );
  }

  Widget _buildStatusHelpItem({
    required IconData icon,
    required Color color,
    required String title,
    required String description,
  }) {
    return ListTile(
      leading: Icon(icon, color: color),
      title: Text(title),
      subtitle: Text(description),
    );
  }
}

class _FriendsStatusTab extends StatefulWidget {
  const _FriendsStatusTab();

  @override
  State<_FriendsStatusTab> createState() => _FriendsStatusTabState();
}

class _FriendsStatusTabState extends State<_FriendsStatusTab> {
  bool _groupByStatus = true;
  final List<Map<String, dynamic>> _mockFriends = [
    {'id': 'user_001', 'name': '张三', 'avatarUrl': null},
    {'id': 'user_002', 'name': '李四', 'avatarUrl': null},
    {'id': 'user_003', 'name': '王五', 'avatarUrl': null},
    {'id': 'user_004', 'name': '赵六', 'avatarUrl': null},
    {'id': 'user_005', 'name': '钱七', 'avatarUrl': null},
  ];

  @override
  Widget build(BuildContext context) {
    return Column(
      children: [
        Padding(
          padding: const EdgeInsets.all(16),
          child: Row(
            children: [
              const Text(
                '好友列表',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
              const Spacer(),
              TextButton.icon(
                onPressed: () => setState(() => _groupByStatus = !_groupByStatus),
                icon: Icon(_groupByStatus ? Icons.view_list : Icons.group_work),
                label: Text(_groupByStatus ? '分组显示' : '平铺显示'),
              ),
            ],
          ),
        ),
        Expanded(
          child: FriendListWithStatus(
            friends: _mockFriends,
            groupByStatus: _groupByStatus,
            showOnlineFirst: true,
            onFriendTap: (userId) {
              // 打开聊天或好友详情
            },
          ),
        ),
      ],
    );
  }
}

class _SubscriptionTab extends StatelessWidget {
  const _SubscriptionTab();

  @override
  Widget build(BuildContext context) {
    return const StatusSubscriptionManager();
  }
}
