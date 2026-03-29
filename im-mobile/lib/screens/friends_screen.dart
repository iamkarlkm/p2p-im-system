import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/auth_service.dart';
import '../services/friend_service.dart';
import '../models/user.dart';

class FriendsScreen extends StatefulWidget {
  const FriendsScreen({super.key});

  @override
  State<FriendsScreen> createState() => _FriendsScreenState();
}

class _FriendsScreenState extends State<FriendsScreen> {
  final TextEditingController _searchController = TextEditingController();
  List<User> _friends = [];
  List<User> _filteredFriends = [];
  bool _isLoading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadFriends();
  }

  @override
  void dispose() {
    _searchController.dispose();
    super.dispose();
  }

  Future<void> _loadFriends() async {
    setState(() {
      _isLoading = true;
      _error = null;
    });

    try {
      final friendService = Provider.of<FriendService>(context, listen: false);
      final friends = await friendService.getFriends();
      
      setState(() {
        _friends = friends;
        _filteredFriends = friends;
        _isLoading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _isLoading = false;
      });
    }
  }

  void _filterFriends(String query) {
    setState(() {
      if (query.isEmpty) {
        _filteredFriends = _friends;
      } else {
        _filteredFriends = _friends.where((friend) {
          final nickname = friend.nickname?.toLowerCase() ?? '';
          final username = friend.username.toLowerCase();
          final searchQuery = query.toLowerCase();
          return nickname.contains(searchQuery) || username.contains(searchQuery);
        }).toList();
      }
    });
  }

  void _showAddFriendDialog() {
    final controller = TextEditingController();
    
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('添加好友'),
        content: TextField(
          controller: controller,
          decoration: const InputDecoration(
            hintText: '输入用户名或ID',
            prefixIcon: Icon(Icons.search),
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
                await _sendFriendRequest(controller.text);
              }
            },
            child: const Text('添加'),
          ),
        ],
      ),
    );
  }

  Future<void> _sendFriendRequest(String userId) async {
    try {
      final friendService = Provider.of<FriendService>(context, listen: false);
      await friendService.sendFriendRequest(userId);
      
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('好友请求已发送')),
        );
      }
    } catch (e) {
      if (mounted) {
        ScaffoldMessenger.of(context).showSnackBar(
          SnackBar(content: Text('添加失败: $e')),
        );
      }
    }
  }

  void _showFriendOptions(User friend) {
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
                _openChat(friend);
              },
            ),
            ListTile(
              leading: const Icon(Icons.info),
              title: const Text('查看资料'),
              onTap: () {
                Navigator.pop(context);
                _showFriendInfo(friend);
              },
            ),
            ListTile(
              leading: const Icon(Icons.block, color: Colors.red),
              title: const Text('删除好友', style: TextStyle(color: Colors.red)),
              onTap: () {
                Navigator.pop(context);
                _deleteFriend(friend);
              },
            ),
          ],
        ),
      ),
    );
  }

  void _openChat(User friend) {
    // 跳转到聊天页面
    Navigator.of(context).pushNamed('/chat', arguments: friend);
  }

  void _showFriendInfo(User friend) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text(friend.nickname ?? friend.username),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            if (friend.avatarUrl != null)
              Center(
                child: CircleAvatar(
                  radius: 40,
                  backgroundImage: NetworkImage(friend.avatarUrl!),
                ),
              ),
            const SizedBox(height: 16),
            _buildInfoRow('用户名', friend.username),
            if (friend.nickname != null)
              _buildInfoRow('昵称', friend.nickname!),
            if (friend.email != null)
              _buildInfoRow('邮箱', friend.email!),
            if (friend.phone != null)
              _buildInfoRow('电话', friend.phone!),
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

  Future<void> _deleteFriend(User friend) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('删除好友'),
        content: Text('确定要删除好友 ${friend.nickname ?? friend.username} 吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            child: const Text('删除', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      try {
        final friendService = Provider.of<FriendService>(context, listen: false);
        await friendService.deleteFriend(friend.id);
        
        setState(() {
          _friends.removeWhere((f) => f.id == friend.id);
          _filteredFriends.removeWhere((f) => f.id == friend.id);
        });
        
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            const SnackBar(content: Text('已删除好友')),
          );
        }
      } catch (e) {
        if (mounted) {
          ScaffoldMessenger.of(context).showSnackBar(
            SnackBar(content: Text('删除失败: $e')),
          );
        }
      }
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('好友列表'),
        actions: [
          IconButton(
            icon: const Icon(Icons.person_add),
            onPressed: _showAddFriendDialog,
          ),
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _loadFriends,
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
                hintText: '搜索好友...',
                prefixIcon: const Icon(Icons.search),
                suffixIcon: _searchController.text.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear),
                        onPressed: () {
                          _searchController.clear();
                          _filterFriends('');
                        },
                      )
                    : null,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(24),
                ),
                filled: true,
                fillColor: Colors.grey[100],
              ),
              onChanged: _filterFriends,
            ),
          ),
          
          // 好友列表
          Expanded(
            child: _buildFriendsList(),
          ),
        ],
      ),
    );
  }

  Widget _buildFriendsList() {
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
              onPressed: _loadFriends,
              child: const Text('重试'),
            ),
          ],
        ),
      );
    }

    if (_filteredFriends.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            Icon(Icons.people_outline, size: 64, color: Colors.grey[400]),
            const SizedBox(height: 16),
            Text(
              _searchController.text.isEmpty
                  ? '暂无好友'
                  : '没有找到好友',
              style: TextStyle(color: Colors.grey[600], fontSize: 16),
            ),
            if (_searchController.text.isEmpty) ...[
              const SizedBox(height: 16),
              ElevatedButton.icon(
                onPressed: _showAddFriendDialog,
                icon: const Icon(Icons.person_add),
                label: const Text('添加好友'),
              ),
            ],
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _loadFriends,
      child: ListView.builder(
        itemCount: _filteredFriends.length,
        itemBuilder: (context, index) {
          final friend = _filteredFriends[index];
          return _buildFriendItem(friend);
        },
      ),
    );
  }

  Widget _buildFriendItem(User friend) {
    return ListTile(
      leading: CircleAvatar(
        backgroundImage: friend.avatarUrl != null
            ? NetworkImage(friend.avatarUrl!)
            : null,
        child: friend.avatarUrl == null
            ? Text(
                (friend.nickname ?? friend.username).substring(0, 1).toUpperCase(),
                style: const TextStyle(fontWeight: FontWeight.bold),
              )
            : null,
      ),
      title: Text(friend.nickname ?? friend.username),
      subtitle: Text(
        friend.email ?? friend.phone ?? friend.username,
        style: TextStyle(color: Colors.grey[600], fontSize: 12),
      ),
      trailing: const Icon(Icons.chevron_right),
      onTap: () => _showFriendOptions(friend),
    );
  }
}
