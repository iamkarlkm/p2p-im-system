import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/group_service.dart';
import '../models/group.dart';
import '../models/user.dart';

class GroupsScreen extends StatefulWidget {
  const GroupsScreen({super.key});

  @override
  State<GroupsScreen> createState() => _GroupsScreenState();
}

class _GroupsScreenState extends State<GroupsScreen> {
  List<Group> _groups = [];
  List<Group> _filteredGroups = [];
  bool _isLoading = true;
  String? _error;
  final TextEditingController _searchController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _loadGroups();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _loadGroups() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final groupService = Provider.of<GroupService>(context, listen: false);
      final groups = await groupService.getMyGroups();
      
      setState(() {
        _groups = groups;
        _filteredGroups = groups;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  void _filterGroups(String query) {
    setState(() {
      if (query.isEmpty) {
        _filteredGroups = _groups;
      } else {
        _filteredGroups = _groups.where((group) {
          final groupName = group.groupName?.toLowerCase() ?? '';
          final searchQuery = query.toLowerCase();
          return groupName.contains(searchQuery);
        }).toList();
      }
    });
  }

  void _showCreateGroupDialog() {
    final nameController = TextEditingController();
    final descController = TextEditingController();

    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('创建群组'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: nameController,
              decoration: const InputDecoration(
                labelText: '群名称',
                hintText: '输入群名称',
                prefixIcon: Icon(Icons.group),
              ),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: descController,
              decoration: const InputDecoration(
                labelText: '群描述',
                hintText: '输入群描述（可选）',
                prefixIcon: Icon(Icons.description),
              ),
              maxLines: 2,
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () async {
              if (nameController.text.isNotEmpty) {
                Navigator.pop(context);
                await _createGroup(nameController.text, descController.text);
              }
            },
            child: const Text('创建'),
          ),
        ],
      ),
    );
  }

  Future<void> _createGroup(String name, String? description) async {
    try {
      final groupService = Provider.of<GroupService>(context, listen: false);
      final group = await groupService.createGroup(name, description);
      
      setState(() {
        _groups.add(group);
        _filteredGroups.add(group);
      });
      
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('群组 "$name" 创建成功')),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('创建失败: $e')),
        );
      }
    }
  }

  void _showGroupOptions(Group group) {
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: const Icon(Icons.chat),
              title: const Text('发送消息'),
              onTap: () {
                Navigator.pop(context);
                _openGroupChat(group);
              },
            ),
            ListTile(
              leading: const Icon(Icons.info),
              title: const Text('群信息'),
              onTap: () {
                Navigator.pop(context);
                _showGroupInfo(group);
              },
            ),
            ListTile(
              leading: const Icon(Icons.person_add),
              title: const Text('邀请好友'),
              onTap: () {
                Navigator.pop(context);
                _inviteFriend(group);
              },
            ),
            ListTile(
              leading: const Icon(Icons.exit_to_app, color: Colors.red),
              title: const Text('退出群组', style: TextStyle(color: Colors.red)),
              onTap: () {
                Navigator.pop(context);
                _leaveGroup(group);
              },
            ),
          ],
        ),
      ),
    );
  }

  void _openGroupChat(Group group) {
    Navigator.of(context).pushNamed('/group-chat', arguments: group);
  }

  void _showGroupInfo(Group group) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(group.groupName ?? '群组'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (group.avatarUrl != null)
              Center(
                child: CircleAvatar(
                  radius: 40,
                  backgroundImage: NetworkImage(group.avatarUrl!),
                ),
              ),
            const SizedBox(height: 16),
            if (group.description != null) ...[
              const Text(
                '群描述:',
                style: TextStyle(fontWeight: FontWeight.bold),
              ),
              Text(group.description!),
              const SizedBox(height: 8),
            ],
            _buildInfoRow('群ID', group.id),
            _buildInfoRow('成员数', '${group.memberCount ?? 0}'),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('关闭'),
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
            width: 60,
            child: Text(
              '$label:',
              style: const TextStyle(fontWeight: FontWeight.bold),
            ),
          ),
          Expanded(child: Text(value)),
        ],
      ),
    );
  }

  void _inviteFriend(Group group) {
    final controller = TextEditingController();
    
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('邀请好友'),
        content: TextField(
          controller: controller,
          decoration: const InputDecoration(
            hintText: '输入好友用户名',
            prefixIcon: Icon(Icons.person_add),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () async {
              if (controller.text.isNotEmpty) {
                Navigator.pop(context);
                await _inviteUserToGroup(group.id, controller.text);
              }
            },
            child: const Text('邀请'),
          ),
        ],
      ),
    );
  }

  Future<void> _inviteUserToGroup(String groupId, String username) async {
    try {
      final groupService = Provider.of<GroupService>(context, listen: false);
      await groupService.inviteUser(groupId, username);
      
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('邀请已发送')),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('邀请失败: $e')),
        );
      }
    }
  }

  Future<void> _leaveGroup(Group group) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('退出群组'),
        content: Text('确定要退出群组 "${group.groupName}" 吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('退出', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      try {
        final groupService = Provider.of<GroupService>(context, listen: false);
        await groupService.leaveGroup(group.id);
        
        setState(() {
          _groups.removeWhere((g) => g.id == group.id);
          _filteredGroups.removeWhere((g) => g.id == group.id);
        });
        
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('已退出群组')),
          );
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('退出失败: $e')),
          );
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('群组列表'),
        actions: [
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: _showCreateGroupDialog,
          ),
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadGroups,
          ),
        ],
      ),
      body: Column(
        children: [
          // 搜索框
          Padding(
            padding: const EdgeInsets.all(16),
            child: TextField(
              controller: _searchController,
              decoration: InputDecoration(
                hintText: '搜索群组...',
                prefixIcon: const Icon(Icons.search),
                suffixIcon: _searchController.text.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear),
                        onPressed: () {
                          _searchController.clear();
                          _filterGroups('');
                        },
                      )
                    : null,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(24),
                ),
                filled: true,
                fillColor: Colors.grey[100],
              ),
              onChanged: _filterGroups,
            ),
          ),
          
          // 群组列表
          Expanded(
            child: _buildGroupsList(),
          ),
        ],
      ),
    );
  }

  Widget _buildGroupsList() {
    if (_isLoading) {
      return const Center(child: CircularProgressIndicator());
    }

    if (_error != null) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Text('Error: $_error', style: const TextStyle(color: Colors.red)),
            const SizedBox(height: 16),
            ElevatedButton(
              onPressed: _loadGroups,
              child: const Text('重试'),
            ),
          ],
        ),
      );
    }

    if (_filteredGroups.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.group_outlined, size: 64, color: Colors.grey[400]),
            const SizedBox(height: 16),
            Text(
              _searchController.text.isEmpty
                  ? '暂无群组'
                  : '没有找到群组',
              style: TextStyle(color: Colors.grey[600], fontSize: 16),
            ),
            if (_searchController.text.isEmpty) ...[
              const SizedBox(height: 16),
              ElevatedButton.icon(
                onPressed: _showCreateGroupDialog,
                icon: const Icon(Icons.add),
                label: const Text('创建群组'),
              ),
            ],
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadGroups,
      child: ListView.builder(
        itemCount: _filteredGroups.length,
        itemBuilder: (context, index) {
          final group = _filteredGroups[index];
          return _buildGroupItem(group);
        },
      ),
    );
  }

  Widget _buildGroupItem(Group group) {
    return ListTile(
      leading: CircleAvatar(
        backgroundImage: group.avatarUrl != null
            ? NetworkImage(group.avatarUrl!)
            : null,
        child: group.avatarUrl == null
            ? const Icon(Icons.group)
            : null,
      ),
      title: Text(group.groupName ?? '群组'),
      subtitle: Text(
        group.description ?? '点击查看详情',
        maxLines: 1,
        overflow: TextOverflow.ellipsis,
        style: TextStyle(color: Colors.grey[600], fontSize: 12),
      ),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          if (group.memberCount != null && group.memberCount! > 0)
            Container(
              padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 2),
              decoration: BoxDecoration(
                color: Colors.grey[200],
                borderRadius: BorderRadius.circular(12),
              ),
              child: Text(
                '${group.memberCount}人',
                style: TextStyle(color: Colors.grey[600], fontSize: 12),
              ),
            ),
          const SizedBox(width: 8),
          const Icon(Icons.chevron_right),
        ],
      ),
      onTap: () => _showGroupOptions(group),
    );
  }
}
