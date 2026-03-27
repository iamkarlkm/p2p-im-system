import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/user_status.dart';
import '../../services/user_status_service.dart';
import '../status_indicator.dart';

class FriendListWithStatus extends StatefulWidget {
  final List<Map<String, dynamic>> friends;
  final Function(String)? onFriendTap;
  final Function(String)? onFriendLongPress;
  final bool showOnlineFirst;
  final bool groupByStatus;

  const FriendListWithStatus({
    super.key,
    required this.friends,
    this.onFriendTap,
    this.onFriendLongPress,
    this.showOnlineFirst = true,
    this.groupByStatus = false,
  });

  @override
  State<FriendListWithStatus> createState() => _FriendListWithStatusState();
}

class _FriendListWithStatusState extends State<FriendListWithStatus> {
  @override
  void initState() {
    super.initState();
    _subscribeToFriends();
  }

  @override
  void didUpdateWidget(FriendListWithStatus oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (oldWidget.friends.length != widget.friends.length) {
      _subscribeToFriends();
    }
  }

  void _subscribeToFriends() {
    final userStatusService = context.read<UserStatusService>();
    for (final friend in widget.friends) {
      final userId = friend['id'] as String?;
      if (userId != null) {
        userStatusService.subscribeToStatus(userId);
      }
    }
  }

  List<Map<String, dynamic>> get _sortedFriends {
    if (!widget.showOnlineFirst) {
      return List.from(widget.friends);
    }

    final userStatusService = context.read<UserStatusService>();
    final sorted = List<Map<String, dynamic>>.from(widget.friends);
    
    sorted.sort((a, b) {
      final statusA = userStatusService.getFriendStatus(a['id']);
      final statusB = userStatusService.getFriendStatus(b['id']);
      
      final isOnlineA = statusA?.isOnline ?? false;
      final isOnlineB = statusB?.isOnline ?? false;
      
      if (isOnlineA && !isOnlineB) return -1;
      if (!isOnlineA && isOnlineB) return 1;
      
      // 都在线或都离线时，按名字排序
      final nameA = a['name'] as String? ?? '';
      final nameB = b['name'] as String? ?? '';
      return nameA.compareTo(nameB);
    });
    
    return sorted;
  }

  Map<String, List<Map<String, dynamic>>> get _groupedFriends {
    final userStatusService = context.read<UserStatusService>();
    final grouped = <String, List<Map<String, dynamic>>>{
      'online': [],
      'away': [],
      'busy': [],
      'offline': [],
    };

    for (final friend in _sortedFriends) {
      final status = userStatusService.getFriendStatus(friend['id']);
      final statusKey = status?.status ?? 'offline';
      grouped[statusKey]?.add(friend);
    }

    return grouped;
  }

  @override
  Widget build(BuildContext context) {
    if (widget.groupByStatus) {
      return _buildGroupedList();
    }
    return _buildSimpleList();
  }

  Widget _buildSimpleList() {
    final friends = _sortedFriends;
    
    if (friends.isEmpty) {
      return const Center(
        child: Padding(
          padding: EdgeInsets.all(32),
          child: Text('暂无好友', style: TextStyle(color: Colors.grey)),
        ),
      );
    }

    return ListView.builder(
      itemCount: friends.length,
      itemBuilder: (context, index) {
        final friend = friends[index];
        return _FriendListTile(
          friend: friend,
          onTap: () => widget.onFriendTap?.call(friend['id']),
          onLongPress: () => widget.onFriendLongPress?.call(friend['id']),
        );
      },
    );
  }

  Widget _buildGroupedList() {
    final grouped = _groupedFriends;
    final sections = [
      _buildSection('在线', grouped['online']!, Colors.green),
      _buildSection('离开', grouped['away']!, Colors.orange),
      _buildSection('忙碌', grouped['busy']!, Colors.red),
      _buildSection('离线', grouped['offline']!, Colors.grey),
    ];

    return ListView(
      children: sections.where((s) => s != null).expand((s) => s!).toList(),
    );
  }

  List<Widget>? _buildSection(String title, List<Map<String, dynamic>> friends, Color color) {
    if (friends.isEmpty) return null;

    return [
      Padding(
        padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
        child: Row(
          children: [
            Container(
              width: 8,
              height: 8,
              decoration: BoxDecoration(color: color, shape: BoxShape.circle),
            ),
            const SizedBox(width: 8),
            Text(
              '$title (${friends.length})',
              style: TextStyle(
                fontSize: 14,
                fontWeight: FontWeight.bold,
                color: Colors.grey.shade700,
              ),
            ),
          ],
        ),
      ),
      ...friends.map((friend) => _FriendListTile(
        friend: friend,
        onTap: () => widget.onFriendTap?.call(friend['id']),
        onLongPress: () => widget.onFriendLongPress?.call(friend['id']),
      )),
    ];
  }
}

class _FriendListTile extends StatelessWidget {
  final Map<String, dynamic> friend;
  final VoidCallback? onTap;
  final VoidCallback? onLongPress;

  const _FriendListTile({
    required this.friend,
    this.onTap,
    this.onLongPress,
  });

  @override
  Widget build(BuildContext context) {
    return Consumer<UserStatusService>(
      builder: (context, service, child) {
        final userId = friend['id'] as String;
        final name = friend['name'] as String? ?? '未知用户';
        final avatarUrl = friend['avatarUrl'] as String?;
        final status = service.getFriendStatus(userId);

        return ListTile(
          leading: UserAvatarWithStatus(
            avatarUrl: avatarUrl,
            name: name,
            status: status?.status,
            size: 48,
          ),
          title: Text(
            name,
            style: const TextStyle(fontWeight: FontWeight.w500),
          ),
          subtitle: status?.customStatus != null
              ? Text(
                  '${status!.customStatusEmoji ?? '💬'} ${status.customStatus!}',
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.grey.shade600,
                  ),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                )
              : StatusIndicatorWithLabel(
                  status: status?.status,
                  indicatorSize: 8,
                  showLastSeen: status?.isOffline ?? true,
                  lastSeen: status?.lastSeen,
                ),
          onTap: onTap,
          onLongPress: onLongPress,
        );
      },
    );
  }
}

class OnlineFriendsList extends StatelessWidget {
  final Function(String)? onFriendTap;

  const OnlineFriendsList({
    super.key,
    this.onFriendTap,
  });

  @override
  Widget build(BuildContext context) {
    return Consumer<UserStatusService>(
      builder: (context, service, child) {
        final onlineFriends = service.getOnlineFriends();

        if (onlineFriends.isEmpty) {
          return const Center(
            child: Padding(
              padding: EdgeInsets.all(24),
              child: Column(
                mainAxisSize: MainAxisSize.min,
                children: [
                  Icon(Icons.people_outline, size: 48, color: Colors.grey),
                  SizedBox(height: 8),
                  Text('暂无在线好友', style: TextStyle(color: Colors.grey)),
                ],
              ),
            ),
          );
        }

        return ListView.builder(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          itemCount: onlineFriends.length,
          itemBuilder: (context, index) {
            final status = onlineFriends[index];
            return ListTile(
              leading: const CircleAvatar(
                backgroundColor: Colors.blue,
                child: Icon(Icons.person, color: Colors.white),
              ),
              title: Text('用户 ${status.userId.substring(0, 8)}...'),
              subtitle: status.customStatus != null
                  ? Text('${status.customStatusEmoji ?? '💬'} ${status.customStatus}')
                  : const Text('在线', style: TextStyle(color: Colors.green)),
              trailing: const StatusIndicator(status: 'online', size: 10),
              onTap: () => onFriendTap?.call(status.userId),
            );
          },
        );
      },
    );
  }
}

class FriendStatusSummary extends StatelessWidget {
  final List<Map<String, dynamic>> friends;

  const FriendStatusSummary({
    super.key,
    required this.friends,
  });

  @override
  Widget build(BuildContext context) {
    return Consumer<UserStatusService>(
      builder: (context, service, child) {
        int online = 0, away = 0, busy = 0, offline = 0;

        for (final friend in friends) {
          final status = service.getFriendStatus(friend['id']);
          switch (status?.status) {
            case 'online':
              online++;
              break;
            case 'away':
              away++;
              break;
            case 'busy':
              busy++;
              break;
            default:
              offline++;
          }
        }

        return Card(
          margin: const EdgeInsets.all(16),
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                const Text(
                  '好友状态概览',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 16),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceAround,
                  children: [
                    _buildStatItem('在线', online, Colors.green),
                    _buildStatItem('离开', away, Colors.orange),
                    _buildStatItem('忙碌', busy, Colors.red),
                    _buildStatItem('离线', offline, Colors.grey),
                  ],
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildStatItem(String label, int count, Color color) {
    return Column(
      children: [
        Container(
          width: 12,
          height: 12,
          decoration: BoxDecoration(color: color, shape: BoxShape.circle),
        ),
        const SizedBox(height: 4),
        Text(
          '$count',
          style: const TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
        ),
        Text(label, style: TextStyle(fontSize: 12, color: Colors.grey.shade600)),
      ],
    );
  }
}
