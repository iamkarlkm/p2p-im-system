import 'package:flutter/material.dart';
import 'package:intl/intl.dart';

class ScheduledMessageBottomSheet extends StatefulWidget {
  final Function(int receiverId, String content, DateTime scheduledTime) onSubmit;

  const ScheduledMessageBottomSheet({
    Key? key,
    required this.onSubmit,
  }) : super(key: key);

  @override
  _ScheduledMessageBottomSheetState createState() => _ScheduledMessageBottomSheetState();
}

class _ScheduledMessageBottomSheetState extends State<ScheduledMessageBottomSheet> {
  final _formKey = GlobalKey<FormState>();
  final _contentController = TextEditingController();
  int? _selectedReceiverId;
  DateTime _scheduledTime = DateTime.now().add(const Duration(days: 1));
  bool _loading = false;

  // 模拟好友列表，实际应该从好友服务获取
  final List<Map<String, dynamic>> _friends = [
    {'id': 1, 'nickname': '好友1', 'avatar': null},
    {'id': 2, 'nickname': '好友2', 'avatar': null},
    {'id': 3, 'nickname': '好友3', 'avatar': null},
  ];

  @override
  void dispose() {
    _contentController.dispose();
    super.dispose();
  }

  Future<void> _selectDateTime() async {
    final date = await showDatePicker(
      context: context,
      initialDate: _scheduledTime,
      firstDate: DateTime.now(),
      lastDate: DateTime.now().add(const Duration(days: 365)),
    );

    if (date != null) {
      final time = await showTimePicker(
        context: context,
        initialTime: TimeOfDay.fromDateTime(_scheduledTime),
      );

      if (time != null) {
        setState(() {
          _scheduledTime = DateTime(
            date.year,
            date.month,
            date.day,
            time.hour,
            time.minute,
          );
        });
      }
    }
  }

  void _submit() {
    if (_formKey.currentState?.validate() ?? false) {
      if (_selectedReceiverId == null) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('请选择接收者')),
        );
        return;
      }

      if (_scheduledTime.isBefore(DateTime.now())) {
        ScaffoldMessenger.of(context).showSnackBar(
          const SnackBar(content: Text('定时时间必须在将来')),
        );
        return;
      }

      setState(() => _loading = true);
      
      widget.onSubmit(
        _selectedReceiverId!,
        _contentController.text,
        _scheduledTime,
      ).then((_) {
        if (mounted) setState(() => _loading = false);
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: EdgeInsets.only(
        bottom: MediaQuery.of(context).viewInsets.bottom,
      ),
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      child: SafeArea(
        child: Padding(
          padding: const EdgeInsets.all(16),
          child: Form(
            key: _formKey,
            child: Column(
              mainAxisSize: MainAxisSize.min,
              crossAxisAlignment: CrossAxisAlignment.stretch,
              children: [
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    const Text(
                      '新建定时消息',
                      style: TextStyle(
                        fontSize: 18,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                    IconButton(
                      onPressed: () => Navigator.pop(context),
                      icon: const Icon(Icons.close),
                    ),
                  ],
                ),
                const SizedBox(height: 16),
                
                // 接收者选择
                DropdownButtonFormField<int>(
                  value: _selectedReceiverId,
                  hint: const Text('选择接收者'),
                  isExpanded: true,
                  items: _friends.map((friend) {
                    return DropdownMenuItem<int>(
                      value: friend['id'] as int,
                      child: Row(
                        children: [
                          CircleAvatar(
                            radius: 16,
                            child: Text(friend['nickname'][0]),
                          ),
                          const SizedBox(width: 12),
                          Text(friend['nickname'] as String),
                        ],
                      ),
                    );
                  }).toList(),
                  onChanged: (value) => setState(() => _selectedReceiverId = value),
                  decoration: const InputDecoration(
                    labelText: '接收者',
                    border: OutlineInputBorder(),
                  ),
                ),
                const SizedBox(height: 16),

                // 消息内容
                TextFormField(
                  controller: _contentController,
                  maxLines: 4,
                  maxLength: 5000,
                  decoration: const InputDecoration(
                    labelText: '消息内容',
                    hintText: '请输入要定时发送的消息内容...',
                    border: OutlineInputBorder(),
                  ),
                  validator: (value) {
                    if (value == null || value.isEmpty) {
                      return '请输入消息内容';
                    }
                    return null;
                  },
                ),
                const SizedBox(height: 16),

                // 发送时间
                ListTile(
                  contentPadding: EdgeInsets.zero,
                  leading: const Icon(Icons.schedule),
                  title: const Text('发送时间'),
                  subtitle: Text(
                    DateFormat('yyyy-MM-dd HH:mm').format(_scheduledTime),
                    style: const TextStyle(color: Colors.blue),
                  ),
                  trailing: TextButton(
                    onPressed: _selectDateTime,
                    child: const Text('修改'),
                  ),
                ),
                const SizedBox(height: 24),

                // 提交按钮
                ElevatedButton(
                  onPressed: _loading ? null : _submit,
                  style: ElevatedButton.styleFrom(
                    padding: const EdgeInsets.symmetric(vertical: 16),
                  ),
                  child: _loading
                      ? const SizedBox(
                          height: 20,
                          width: 20,
                          child: CircularProgressIndicator(strokeWidth: 2),
                        )
                      : const Text('创建定时消息'),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
