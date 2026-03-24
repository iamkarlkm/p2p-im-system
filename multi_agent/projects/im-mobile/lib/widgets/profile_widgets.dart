/// 用户资料组件 - Flutter移动端

import 'package:flutter/material.dart';
import '../models/profile.dart';

class ProfileCardWidget extends StatelessWidget {
  final UserProfile profile;
  final bool isOnline;
  final VoidCallback? onEdit;
  final VoidCallback? onAvatarTap;
  final VoidCallback? onStatusTap;

  const ProfileCardWidget({
    super.key,
    required this.profile,
    this.isOnline = false,
    this.onEdit,
    this.onAvatarTap,
    this.onStatusTap,
  });

  Color _statusColor(UserStatus status) {
    switch (status) {
      case UserStatus.online:
        return Colors.green;
      case UserStatus.away:
        return Colors.orange;
      case UserStatus.busy:
      case UserStatus.doNotDisturb:
        return Colors.red;
      case UserStatus.invisible:
      case UserStatus.offline:
        return Colors.grey;
    }
  }

  @override
  Widget build(BuildContext context) {
    return Card(
      margin: const EdgeInsets.all(12),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                GestureDetector(
                  onTap: onAvatarTap,
                  child: Stack(
                    children: [
                      CircleAvatar(
                        radius: 36,
                        backgroundColor: Theme.of(context).primaryColor,
                        backgroundImage: profile.avatarUrl.isNotEmpty
                            ? NetworkImage(profile.avatarUrl)
                            : null,
                        child: profile.avatarUrl.isEmpty
                            ? Text(
                                profile.nickname.isNotEmpty
                                    ? profile.nickname[0].toUpperCase()
                                    : profile.userId[0].toUpperCase(),
                                style: const TextStyle(
                                  fontSize: 28,
                                  color: Colors.white,
                                  fontWeight: FontWeight.bold,
                                ),
                              )
                            : null,
                      ),
                      Positioned(
                        bottom: 2,
                        right: 2,
                        child: Container(
                          width: 14,
                          height: 14,
                          decoration: BoxDecoration(
                            color: _statusColor(profile.status),
                            shape: BoxShape.circle,
                            border: Border.all(color: Colors.white, width: 2),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        profile.nickname.isNotEmpty ? profile.nickname : profile.userId,
                        style: const TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                        ),
                      ),
                      const SizedBox(height: 4),
                      GestureDetector(
                        onTap: onStatusTap,
                        child: Row(
                          children: [
                            Container(
                              width: 8,
                              height: 8,
                              decoration: BoxDecoration(
                                color: _statusColor(profile.status),
                                shape: BoxShape.circle,
                              ),
                            ),
                            const SizedBox(width: 6),
                            Text(
                              isOnline ? profile.status.label : '离线',
                              style: TextStyle(
                                color: _statusColor(isOnline ? profile.status : UserStatus.offline),
                                fontSize: 14,
                              ),
                            ),
                            const SizedBox(width: 4),
                            const Icon(Icons.arrow_drop_down, size: 16),
                          ],
                        ),
                      ),
                    ],
                  ),
                ),
                if (onEdit != null)
                  IconButton(
                    icon: const Icon(Icons.edit),
                    onPressed: onEdit,
                  ),
              ],
            ),
            if (profile.signature.isNotEmpty) ...[
              const SizedBox(height: 12),
              Row(
                children: [
                  const Text('✍️', style: TextStyle(fontSize: 14)),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      profile.signature,
                      style: TextStyle(
                        color: Colors.grey[600],
                        fontStyle: FontStyle.italic,
                      ),
                    ),
                  ),
                ],
              ),
            ],
            const Divider(height: 24),
            _detailRow(Icons.email_outlined, '邮箱', profile.email ?? '未设置'),
            _detailRow(Icons.phone_outlined, '电话', profile.phone ?? '未设置'),
            _detailRow(Icons.person_outline, '性别', _genderLabel(profile.gender)),
            _detailRow(Icons.cake_outlined, '生日', profile.birthday ?? '未设置'),
            _detailRow(Icons.location_on_outlined, '地区', profile.region ?? '未设置'),
          ],
        ),
      ),
    );
  }

  Widget _detailRow(IconData icon, String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        children: [
          Icon(icon, size: 18, color: Colors.grey),
          const SizedBox(width: 8),
          Text('$label: ', style: const TextStyle(color: Colors.grey, fontSize: 14)),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(fontSize: 14),
              overflow: TextOverflow.ellipsis,
            ),
          ),
        ],
      ),
    );
  }

  String _genderLabel(String? gender) {
    if (gender == null) return '未设置';
    switch (gender.toUpperCase()) {
      case 'M':
        return '男';
      case 'F':
        return '女';
      default:
        return '未知';
    }
  }
}

class StatusSelectorSheet extends StatelessWidget {
  final UserStatus currentStatus;
  final ValueChanged<UserStatus> onSelect;

  const StatusSelectorSheet({
    super.key,
    required this.currentStatus,
    required this.onSelect,
  });

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(vertical: 16),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          Container(
            width: 40,
            height: 4,
            decoration: BoxDecoration(
              color: Colors.grey[300],
              borderRadius: BorderRadius.circular(2),
            ),
          ),
          const SizedBox(height: 16),
          const Text(
            '设置在线状态',
            style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
          ),
          const SizedBox(height: 8),
          ...UserStatus.values.where((s) => s != UserStatus.offline).map((s) {
            return ListTile(
              leading: Container(
                width: 12,
                height: 12,
                decoration: BoxDecoration(
                  color: _statusColor(s),
                  shape: BoxShape.circle,
                ),
              ),
              title: Text(s.label),
              trailing: currentStatus == s
                  ? const Icon(Icons.check, color: Colors.green)
                  : null,
              onTap: () {
                onSelect(s);
                Navigator.pop(context);
              },
            );
          }),
        ],
      ),
    );
  }

  Color _statusColor(UserStatus status) {
    switch (status) {
      case UserStatus.online:
        return Colors.green;
      case UserStatus.away:
        return Colors.orange;
      case UserStatus.busy:
      case UserStatus.doNotDisturb:
        return Colors.red;
      case UserStatus.invisible:
      case UserStatus.offline:
        return Colors.grey;
    }
  }
}

class FriendGroupWidget extends StatelessWidget {
  final String groupName;
  final int memberCount;
  final VoidCallback? onTap;

  const FriendGroupWidget({
    super.key,
    required this.groupName,
    required this.memberCount,
    this.onTap,
  });

  @override
  Widget build(BuildContext context) {
    return ListTile(
      leading: const Icon(Icons.folder_outlined),
      title: Text(groupName),
      subtitle: Text('$memberCount 位好友'),
      trailing: const Icon(Icons.chevron_right),
      onTap: onTap,
    );
  }
}
