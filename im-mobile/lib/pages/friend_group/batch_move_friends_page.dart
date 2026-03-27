import 'package:flutter/material.dart';
import '../../models/user.dart';
import '../../models/friend_group.dart';
import '../../services/batch_friend_service.dart';
import '../../services/friend_group_service.dart';
import '../../widgets/friend/friend_multi_select_item.dart';
import '../../utils/toast_util.dart';

/// 批量移动好友到分组页面
class BatchMoveFriendsPage extends StatefulWidget {
  final String? initialGroupId;
  
  const BatchMoveFriendsPage({
    Key? key,
    this.initialGroupId,
  }) : super(key: key);

  @override
  State<BatchMoveFriendsPage> createState() => _BatchMoveFriendsPageState();
}

class _BatchMoveFriendsPageState extends State<BatchMoveFriendsPage> {
  final BatchFriendService _batchService = BatchFriendService();
  final FriendGroupService _groupService = FriendGroupService();
  
  List<User> _friends = [];
  List<FriendGroup> _groups = [];
  Set<String> _selectedFriendIds = {};
  String? _targetGroupId;
  
  bool _isLoading = true;
  bool _isMoving = false;
  bool _selectAll = false;
  String? _currentGroupId;
  String _searchQuery = '';

  @override
  void initState() {
    super.initState();
    _currentGroupId = widget.initialGroupId;
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() => _isLoading = true);
    try {
      final results = await Future.wait([
        _batchService.getFriendsWithoutGroup(_currentGroupId),
        _groupService.getAllGroups(),
      ]);
      
      setState(() {
        _friends = results[0] as List<User>;
        _groups = results[1] as List<FriendGroup>;
        _isLoading = false;
      });
    } catch (e) {
      ToastUtil.showError('加载数据失败: $e');
      setState(() => _isLoading = false);
    }
  }

  List<User> get _filteredFriends {
    if (_searchQuery.isEmpty) return _friends;
    return _friends.where((friend) {
      return friend.nickname.toLowerCase().contains(_searchQuery.toLowerCase()) ||
             (friend.remark?.toLowerCase().contains(_searchQuery.toLowerCase()) ?? false);
    }).toList();
  }

  void _toggleSelectAll() {
    setState(() {
      _selectAll = !_selectAll;
      if (_selectAll) {
        _selectedFriendIds = _filteredFriends.map((f) => f.id).toSet();
      } else {
        _selectedFriendIds.clear();
      }
    });
  }

  void _toggleFriendSelection(String friendId) {
    setState(() {
      if (_selectedFriendIds.contains(friendId)) {
        _selectedFriendIds.remove(friendId);
        _selectAll = false;
      } else {
        _selectedFriendIds.add(friendId);
        if (_selectedFriendIds.length == _filteredFriends.length) {
          _selectAll = true;
        }
      }
    });
  }

  Future<void> _moveToGroup() async {
    if (_selectedFriendIds.isEmpty) {
      ToastUtil.showWarning('请先选择好友');
      return;
    }
    if (_targetGroupId == null) {
      ToastUtil.showWarning('请选择目标分组');
      return;
    }

    setState(() => _isMoving = true);
    try {
      await _batchService.batchMoveToGroup(
        _selectedFriendIds.toList(),
        _targetGroupId!,
      );
      ToastUtil.showSuccess('成功移动 ${_selectedFriendIds.length} 位好友');
      Navigator.pop(context, true);
    } catch (e) {
      ToastUtil.showError('移动失败: $e');
      setState(() => _isMoving = false);
    }
  }

  void _showGroupSelector() {
    showModalBottomSheet(
      context: context,
      builder: (context) => Container(
        padding: const EdgeInsets.all(16),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text(
              '选择目标分组',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 16),
            ..._groups.map((group) => ListTile(
              leading: Icon(
                Icons.folder,
                color: _targetGroupId == group.id ? Colors.blue : Colors.grey,
              ),
              title: Text(group.name),
              subtitle: Text('${group.memberCount} 人'),
              trailing: _targetGroupId == group.id
                  ? const Icon(Icons.check, color: Colors.blue)
                  : null,
              onTap: () {
                setState(() => _targetGroupId = group.id);
                Navigator.pop(context);
              },
            )),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('批量移动好友'),
        actions: [
          TextButton(
            onPressed: _selectedFriendIds.isEmpty ? null : _moveToGroup,
            child: _isMoving
                ? const SizedBox(
                    width: 20,
                    height: 20,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  )
                : Text(
                    '移动(${_selectedFriendIds.length})',
                    style: TextStyle(
                      color: _selectedFriendIds.isEmpty ? Colors.grey : Colors.white,
                    ),
                  ),
          ),
        ],
      ),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : Column(
              children: [
                // 搜索栏
                Padding(
                  padding: const EdgeInsets.all(12),
                  child: TextField(
                    decoration: InputDecoration(
                      hintText: '搜索好友',
                      prefixIcon: const Icon(Icons.search),
                      border: OutlineInputBorder(
                        borderRadius: BorderRadius.circular(8),
                      ),
                      contentPadding: const EdgeInsets.symmetric(horizontal: 12),
                    ),
                    onChanged: (value) => setState(() => _searchQuery = value),
                  ),
                ),
                
                // 目标分组选择器
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  decoration: BoxDecoration(
                    color: Colors.grey[100],
                    border: Border(
                      top: BorderSide(color: Colors.grey[300]!),
                      bottom: BorderSide(color: Colors.grey[300]!),
                    ),
                  ),
                  child: Row(
                    children: [
                      const Icon(Icons.folder_open, size: 20),
                      const SizedBox(width: 8),
                      const Text('移动到:'),
                      const SizedBox(width: 8),
                      Expanded(
                        child: GestureDetector(
                          onTap: _showGroupSelector,
                          child: Container(
                            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 8),
                            decoration: BoxDecoration(
                              color: Colors.white,
                              borderRadius: BorderRadius.circular(4),
                              border: Border.all(color: Colors.grey[300]!),
                            ),
                            child: Row(
                              mainAxisAlignment: MainAxisAlignment.spaceBetween,
                              children: [
                                Text(
                                  _targetGroupId == null
                                      ? '选择分组'
                                      : _groups.firstWhere((g) => g.id == _targetGroupId).name,
                                  style: TextStyle(
                                    color: _targetGroupId == null ? Colors.grey : Colors.black,
                                  ),
                                ),
                                const Icon(Icons.arrow_drop_down, size: 20),
                              ],
                            ),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
                
                // 全选按钮
                Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
                  child: Row(
                    children: [
                      Checkbox(
                        value: _selectAll,
                        onChanged: (_) => _toggleSelectAll(),
                      ),
                      Text('全选 (${_selectedFriendIds.length}/${_filteredFriends.length})'),
                      const Spacer(),
                      if (_currentGroupId != null)
                        Text(
                          '当前分组外的好友',
                          style: TextStyle(fontSize: 12, color: Colors.grey[600]),
                        ),
                    ],
                  ),
                ),
                
                // 好友列表
                Expanded(
                  child: _filteredFriends.isEmpty
                      ? Center(
                          child: Column(
                            mainAxisAlignment: MainAxisAlignment.center,
                            children: [
                              Icon(Icons.people_outline, size: 64, color: Colors.grey[400]),
                              const SizedBox(height: 16),
                              Text(
                                _searchQuery.isEmpty
                                    ? '暂无可移动的好友'
                                    : '未找到匹配的好友',
                                style: TextStyle(color: Colors.grey[600]),
                              ),
                            ],
                          ),
                        )
                      : ListView.builder(
                          itemCount: _filteredFriends.length,
                          itemBuilder: (context, index) {
                            final friend = _filteredFriends[index];
                            return FriendMultiSelectItem(
                              user: friend,
                              isSelected: _selectedFriendIds.contains(friend.id),
                              onToggle: () => _toggleFriendSelection(friend.id),
                            );
                          },
                        ),
                ),
              ],
            ),
    );
  }
}
