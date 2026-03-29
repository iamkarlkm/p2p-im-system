import 'package:flutter/material.dart';
import '../../models/friend_group.dart';
import '../../services/friend_group_service.dart';
import '../chat/chat_page.dart';

class GroupMemberListPage extends StatefulWidget {
  final FriendGroup group;

  const GroupMemberListPage({Key? key, required this.group}) : super(key: key);

  @override
  State<GroupMemberListPage> createState() => _GroupMemberListPageState();
}

class _GroupMemberListPageState extends State<GroupMemberListPage> {
  final FriendGroupService _service = FriendGroupService();
  List<FriendGroupMember> _members = [];
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadMembers();
  }

  Future<void> _loadMembers() async {
    try {
      setState(() => _isLoading = true);
      final members = await _service.getGroupMembers(widget.group.id);
      setState(() {
        _members = members;
        _isLoading = false;
        _error = null;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  Future<void> _toggleStar(FriendGroupMember member) async {
    try {
      await _service.toggleStar(widget.group.id, member.friendId, !member.isStarred);
      _loadMembers();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('操作失败: $e')),
      );
    }
  }

  Future<void> _toggleMute(FriendGroupMember member) async {
    try {
      await _service.toggleMute(widget.group.id, member.friendId, !member.isMuted);
      _loadMembers();
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('操作失败: $e')),
      );
    }
  }

  Future<void> _moveMember(FriendGroupMember member) async {
    // 简化的移动到分组逻辑
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('移动到分组功能开发中')),
    );
  }

  Future<void> _removeMember(FriendGroupMember member) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('移除好友'),
        content: Text('确定要从"${widget.group.name}"移除"${member.friendName}"吗？'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('取消')),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            style: TextButton.styleFrom(foregroundColor: Colors.red),
            child: const Text('移除'),
          ),
        ],
      ),
    );
    if (confirm == true) {
      try {
        await _service.removeMember(widget.group.id, member.friendId);
        _loadMembers();
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('移除失败: $e')),
        );
      }
    }
  }

  void _startChat(FriendGroupMember member) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => ChatPage(
          userId: member.friendId,
          userName: member.friendName,
          avatar: member.friendAvatar,
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text(widget.group.name),
        actions: [
          IconButton(
            icon: const Icon(Icons.person_add),
            onPressed: () {
              // 添加好友到分组
            },
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? Center(child: Text('错误: $_error'))
              : _members.isEmpty
                  ? const Center(child: Text('暂无成员'))
                  : ListView.builder(
                      itemCount: _members.length,
                      itemBuilder: (context, index) => _buildMemberItem(_members[index]),
                    ),
    );
  }

  Widget _buildMemberItem(FriendGroupMember member) {
    return ListTile(
      leading: CircleAvatar(
        backgroundImage: member.friendAvatar != null
            ? NetworkImage(member.friendAvatar!)
            : null,
        child: member.friendAvatar == null
            ? Text(member.friendName[0])
            : null,
      ),
      title: Row(
        children: [
          Text(member.friendName),
          if (member.isStarred)
            const Padding(
              padding: EdgeInsets.only(left: 4),
              child: Icon(Icons.star, size: 16, color: Colors.orange),
            ),
        ],
      ),
      subtitle: member.isMuted ? const Text('消息免打扰', style: TextStyle(color: Colors.grey)) : null,
      trailing: PopupMenuButton<String>(
        onSelected: (value) {
          switch (value) {
            case 'chat':
              _startChat(member);
              break;
            case 'star':
              _toggleStar(member);
              break;
            case 'mute':
              _toggleMute(member);
              break;
            case 'move':
              _moveMember(member);
              break;
            case 'remove':
              _removeMember(member);
              break;
          }
        },
        itemBuilder: (context) => [
          const PopupMenuItem(value: 'chat', child: Text('发消息')),
          PopupMenuItem(
            value: 'star',
            child: Text(member.isStarred ? '取消星标' : '设为星标'),
          ),
          PopupMenuItem(
            value: 'mute',
            child: Text(member.isMuted ? '取消免打扰' : '消息免打扰'),
          ),
          const PopupMenuItem(value: 'move', child: Text('移动到其他分组')),
          const PopupMenuItem(value: 'remove', child: Text('从分组移除', style: TextStyle(color: Colors.red))),
        ],
      ),
      onTap: () => _startChat(member),
    );
  }
}
