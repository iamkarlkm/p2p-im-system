import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../../models/user_status.dart';
import '../../services/user_status_service.dart';

class StatusSelector extends StatefulWidget {
  final VoidCallback? onStatusChanged;

  const StatusSelector({
    super.key,
    this.onStatusChanged,
  });

  @override
  State<StatusSelector> createState() => _StatusSelectorState();
}

class _StatusSelectorState extends State<StatusSelector> {
  final TextEditingController _customStatusController = TextEditingController();
  String _selectedEmoji = '💬';
  bool _isLoading = false;

  final List<Map<String, dynamic>> _statusOptions = [
    {'value': 'online', 'label': '在线', 'color': Colors.green, 'icon': Icons.circle},
    {'value': 'away', 'label': '离开', 'color': Colors.orange, 'icon': Icons.access_time},
    {'value': 'busy', 'label': '忙碌', 'color': Colors.red, 'icon': Icons.do_not_disturb_on},
    {'value': 'invisible', 'label': '隐身', 'color': Colors.grey, 'icon': Icons.visibility_off},
  ];

  final List<String> _commonEmojis = [
    '💬', '😀', '😎', '🤔', '💪', '🎮', '🍔', '☕', '📚', '💻',
    '🎵', '🏃', '✈️', '🏠', '🎉', '💤', '🎯', '🚀', '🌟', '❤️',
  ];

  @override
  void initState() {
    super.initState();
    final userStatus = context.read<UserStatusService>().currentUserStatus;
    if (userStatus?.customStatus != null) {
      _customStatusController.text = userStatus!.customStatus!;
    }
    if (userStatus?.customStatusEmoji != null) {
      _selectedEmoji = userStatus!.customStatusEmoji!;
    }
  }

  @override
  void dispose() {
    _customStatusController.dispose();
    super.dispose();
  }

  Future<void> _updateStatus(String status) async {
    if (_isLoading) return;
    
    setState(() => _isLoading = true);
    
    final service = context.read<UserStatusService>();
    final success = await service.updateStatus(
      status: status,
      customStatus: _customStatusController.text.isNotEmpty
          ? _customStatusController.text
          : null,
      customStatusEmoji: _customStatusController.text.isNotEmpty
          ? _selectedEmoji
          : null,
    );
    
    setState(() => _isLoading = false);
    
    if (success && mounted) {
      widget.onStatusChanged?.call();
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('状态已更新'), duration: Duration(seconds: 1)),
      );
    } else if (mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('更新失败，请重试'), backgroundColor: Colors.red),
      );
    }
  }

  Future<void> _updateCustomStatus() async {
    if (_customStatusController.text.isEmpty) {
      _clearCustomStatus();
      return;
    }
    
    final service = context.read<UserStatusService>();
    final currentStatus = service.currentUserStatus?.status ?? 'online';
    
    await _updateStatus(currentStatus);
  }

  Future<void> _clearCustomStatus() async {
    _customStatusController.clear();
    final service = context.read<UserStatusService>();
    final currentStatus = service.currentUserStatus?.status ?? 'online';
    
    setState(() => _isLoading = true);
    
    final success = await service.updateStatus(
      status: currentStatus,
      customStatus: null,
      customStatusEmoji: null,
    );
    
    setState(() => _isLoading = false);
    
    if (success && mounted) {
      widget.onStatusChanged?.call();
    }
  }

  void _showStatusPicker() {
    final currentStatus = context.read<UserStatusService>().currentUserStatus?.status ?? 'online';
    
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              padding: const EdgeInsets.all(16),
              child: const Text(
                '选择状态',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
            ),
            const Divider(height: 1),
            ..._statusOptions.map((option) {
              final isSelected = currentStatus == option['value'];
              return ListTile(
                leading: Icon(
                  option['icon'] as IconData,
                  color: option['color'] as Color,
                ),
                title: Text(option['label'] as String),
                trailing: isSelected
                    ? const Icon(Icons.check, color: Colors.blue)
                    : null,
                onTap: () {
                  Navigator.pop(context);
                  _updateStatus(option['value'] as String);
                },
              );
            }),
            const SizedBox(height: 16),
          ],
        ),
      ),
    );
  }

  void _showEmojiPicker() {
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            Container(
              padding: const EdgeInsets.all(16),
              child: const Text(
                '选择表情',
                style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
              ),
            ),
            const Divider(height: 1),
            Wrap(
              spacing: 12,
              runSpacing: 12,
              children: _commonEmojis.map((emoji) {
                final isSelected = _selectedEmoji == emoji;
                return GestureDetector(
                  onTap: () {
                    setState(() => _selectedEmoji = emoji);
                    Navigator.pop(context);
                  },
                  child: Container(
                    width: 48,
                    height: 48,
                    decoration: BoxDecoration(
                      color: isSelected ? Colors.blue.shade100 : Colors.grey.shade100,
                      borderRadius: BorderRadius.circular(8),
                      border: isSelected
                          ? Border.all(color: Colors.blue, width: 2)
                          : null,
                    ),
                    child: Center(
                      child: Text(emoji, style: const TextStyle(fontSize: 24)),
                    ),
                  ),
                );
              }).toList(),
            ),
            const SizedBox(height: 16),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Consumer<UserStatusService>(
      builder: (context, service, child) {
        final currentStatus = service.currentUserStatus;
        final statusColor = currentStatus?.statusColor ?? Colors.grey;
        final statusText = currentStatus?.displayStatus ?? '离线';

        return Card(
          margin: const EdgeInsets.all(16),
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    const Text(
                      '当前状态',
                      style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                    ),
                    const Spacer(),
                    if (_isLoading)
                      const SizedBox(
                        width: 16,
                        height: 16,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      ),
                  ],
                ),
                const SizedBox(height: 16),
                
                InkWell(
                  onTap: _showStatusPicker,
                  borderRadius: BorderRadius.circular(8),
                  child: Container(
                    padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
                    decoration: BoxDecoration(
                      color: Colors.grey.shade100,
                      borderRadius: BorderRadius.circular(8),
                    ),
                    child: Row(
                      children: [
                        Container(
                          width: 12,
                          height: 12,
                          decoration: BoxDecoration(
                            color: statusColor,
                            shape: BoxShape.circle,
                          ),
                        ),
                        const SizedBox(width: 12),
                        Expanded(
                          child: Text(
                            statusText,
                            style: const TextStyle(fontSize: 16),
                          ),
                        ),
                        const Icon(Icons.chevron_right, color: Colors.grey),
                      ],
                    ),
                  ),
                ),
                
                const SizedBox(height: 20),
                const Text(
                  '自定义状态',
                  style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold),
                ),
                const SizedBox(height: 12),
                
                Row(
                  children: [
                    InkWell(
                      onTap: _showEmojiPicker,
                      borderRadius: BorderRadius.circular(8),
                      child: Container(
                        width: 48,
                        height: 48,
                        decoration: BoxDecoration(
                          color: Colors.grey.shade100,
                          borderRadius: BorderRadius.circular(8),
                        ),
                        child: Center(
                          child: Text(_selectedEmoji, style: const TextStyle(fontSize: 24)),
                        ),
                      ),
                    ),
                    const SizedBox(width: 12),
                    Expanded(
                      child: TextField(
                        controller: _customStatusController,
                        decoration: InputDecoration(
                          hintText: '输入自定义状态...',
                          filled: true,
                          fillColor: Colors.grey.shade100,
                          border: OutlineInputBorder(
                            borderRadius: BorderRadius.circular(8),
                            borderSide: BorderSide.none,
                          ),
                          suffixIcon: _customStatusController.text.isNotEmpty
                              ? IconButton(
                                  icon: const Icon(Icons.clear, size: 18),
                                  onPressed: _clearCustomStatus,
                                )
                              : null,
                        ),
                        onSubmitted: (_) => _updateCustomStatus(),
                      ),
                    ),
                    const SizedBox(width: 8),
                    ElevatedButton(
                      onPressed: _isLoading ? null : _updateCustomStatus,
                      child: const Text('保存'),
                    ),
                  ],
                ),
                
                const SizedBox(height: 20),
                const Divider(),
                const SizedBox(height: 12),
                
                const Text(
                  '快速设置',
                  style: TextStyle(fontSize: 14, color: Colors.grey),
                ),
                const SizedBox(height: 8),
                Wrap(
                  spacing: 8,
                  runSpacing: 8,
                  children: [
                    _buildQuickChip('工作中', '💻'),
                    _buildQuickChip('吃饭中', '🍔'),
                    _buildQuickChip('休息时', '☕'),
                    _buildQuickChip('玩游戏', '🎮'),
                    _buildQuickChip('学习中', '📚'),
                    _buildQuickChip('运动时', '🏃'),
                  ],
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildQuickChip(String label, String emoji) {
    return ActionChip(
      avatar: Text(emoji),
      label: Text(label),
      onPressed: () {
        setState(() {
          _selectedEmoji = emoji;
          _customStatusController.text = label;
        });
        _updateCustomStatus();
      },
    );
  }
}
