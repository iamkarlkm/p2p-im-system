import 'package:flutter/material.dart';
import '../../models/friend_group.dart';
import '../../services/friend_group_service.dart';
import 'group_member_list_page.dart';
import 'create_group_dialog.dart';

class FriendGroupListPage extends StatefulWidget {
  const FriendGroupListPage({Key? key}) : super(key: key);

  @override
  State<FriendGroupListPage> createState() => _FriendGroupListPageState();
}

class _FriendGroupListPageState extends State<FriendGroupListPage> {
  final FriendGroupService _service = FriendGroupService();
  List<FriendGroup> _groups = [];
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadGroups();
  }

  Future<void> _loadGroups() async {
    try {
      setState(() => _isLoading = true);
      final groups = await _service.getGroups();
      setState(() {
        _groups = groups;
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

  Future<void> _showCreateDialog() async {
    final result = await showDialog<String>(
      context: context,
      builder: (context) => const CreateGroupDialog(),
    );
    if (result != null && result.isNotEmpty) {
      try {
        await _service.createGroup(CreateGroupRequest(name: result));
        _loadGroups();
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('创建失败: $e')),
        );
      }
    }
  }

  Future<void> _showRenameDialog(FriendGroup group) async {
    final controller = TextEditingController(text: group.name);
    final result = await showDialog<String>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('重命名分组'),
        content: TextField(
          controller: controller,
          decoration: const InputDecoration(hintText: '分组名称'),
          autofocus: true,
        ),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context), child: const Text('取消')),
          TextButton(
            onPressed: () => Navigator.pop(context, controller.text),
            child: const Text('确定'),
          ),
        ],
      ),
    );
    if (result != null && result.isNotEmpty && result != group.name) {
      try {
        await _service.updateGroup(group.id, UpdateGroupRequest(name: result));
        _loadGroups();
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('重命名失败: $e')),
        );
      }
    }
  }

  Future<void> _deleteGroup(FriendGroup group) async {
    final confirm = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('删除分组'),
        content: Text('确定要删除"${group.name}"吗？'),
        actions: [
          TextButton(onPressed: () => Navigator.pop(context, false), child: const Text('取消')),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            style: TextButton.styleFrom(foregroundColor: Colors.red),
            child: const Text('删除'),
          ),
        ],
      ),
    );
    if (confirm == true) {
      try {
        await _service.deleteGroup(group.id);
        _loadGroups();
      } catch (e) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('删除失败: $e')),
        );
      }
    }
  }

  void _openGroupMembers(FriendGroup group) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => GroupMemberListPage(group: group),
      ),
    ).then((_) => _loadGroups());
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('好友分组'),
        actions: [
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: _showCreateDialog,
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : _error != null
              ? Center(child: Text('错误: $_error'))
              : _groups.isEmpty
                  ? const Center(child: Text('暂无分组，点击右上角添加'))
                  : ReorderableListView.builder(
                      itemCount: _groups.length,
                      onReorder: (oldIndex, newIndex) async {
                        if (newIndex > oldIndex) newIndex--;
                        final item = _groups.removeAt(oldIndex);
                        _groups.insert(newIndex, item);
                        setState(() {});
                        try {
                          await _service.reorderGroups(
                            _groups.map((g) => g.id).toList(),
                          );
                        } catch (e) {
                          _loadGroups();
                        }
                      },
                      itemBuilder: (context, index) {
                        final group = _groups[index];
                        return _buildGroupItem(group, index);
                      },
                    ),
    );
  }

  Widget _buildGroupItem(FriendGroup group, int index) {
    return ListTile(
      key: ValueKey(group.id),
      leading: CircleAvatar(
        backgroundColor: Colors.blue.shade100,
        child: const Icon(Icons.folder, color: Colors.blue),
      ),
      title: Text(group.name),
      subtitle: Text('${group.memberCount ?? 0} 人'),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          IconButton(
            icon: const Icon(Icons.edit, size: 20),
            onPressed: () => _showRenameDialog(group),
          ),
          IconButton(
            icon: const Icon(Icons.delete, size: 20, color: Colors.red),
            onPressed: () => _deleteGroup(group),
          ),
          const Icon(Icons.drag_handle),
        ],
      ),
      onTap: () => _openGroupMembers(group),
    );
  }
}
