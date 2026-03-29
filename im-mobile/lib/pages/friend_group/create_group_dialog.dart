import 'package:flutter/material.dart';

class CreateGroupDialog extends StatefulWidget {
  const CreateGroupDialog({Key? key}) : super(key: key);

  @override
  State<CreateGroupDialog> createState() => _CreateGroupDialogState();
}

class _CreateGroupDialogState extends State<CreateGroupDialog> {
  final TextEditingController _controller = TextEditingController();

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('新建分组'),
      content: TextField(
        controller: _controller,
        decoration: const InputDecoration(
          hintText: '请输入分组名称',
          border: OutlineInputBorder(),
        ),
        maxLength: 20,
        autofocus: true,
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('取消'),
        ),
        ElevatedButton(
          onPressed: () {
            final name = _controller.text.trim();
            if (name.isNotEmpty) {
              Navigator.pop(context, name);
            }
          },
          child: const Text('创建'),
        ),
      ],
    );
  }
}
