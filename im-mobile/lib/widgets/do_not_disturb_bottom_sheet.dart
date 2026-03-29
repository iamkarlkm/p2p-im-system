// widgets/do_not_disturb_bottom_sheet.dart
import 'package:flutter/material.dart';
import '../models/do_not_disturb_period_model.dart';
import '../stores/do_not_disturb_store.dart';

class DoNotDisturbBottomSheet extends StatefulWidget {
  final DoNotDisturbStore store;
  final DoNotDisturbPeriodModel? period;
  final Future<void> Function(DoNotDisturbPeriodModel) onSave;

  const DoNotDisturbBottomSheet({
    Key? key,
    required this.store,
    this.period,
    required this.onSave,
  }) : super(key: key);

  @override
  State<DoNotDisturbBottomSheet> createState() =>
      _DoNotDisturbBottomSheetState();
}

class _DoNotDisturbBottomSheetState extends State<DoNotDisturbBottomSheet> {
  final _formKey = GlobalKey<FormState>();
  late final TextEditingController _nameController;
  late TimeOfDay _startTime;
  late TimeOfDay _endTime;
  late List<int> _activeDays;
  late bool _allowCalls;
  late bool _allowMentions;

  final List<String> _dayNames = ['周一', '周二', '周三', '周四', '周五', '周六', '周日'];

  @override
  void initState() {
    super.initState();
    final period = widget.period;
    _nameController = TextEditingController(text: period?.name ?? '');
    _startTime = period != null
        ? TimeOfDay(hour: period.startHour, minute: period.startMinute)
        : const TimeOfDay(hour: 22, minute: 0);
    _endTime = period != null
        ? TimeOfDay(hour: period.endHour, minute: period.endMinute)
        : const TimeOfDay(hour: 8, minute: 0);
    _activeDays = period?.activeDays.toList() ?? [1, 2, 3, 4, 5];
    _allowCalls = period?.allowCalls ?? false;
    _allowMentions = period?.allowMentions ?? true;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      decoration: const BoxDecoration(
        color: Colors.white,
        borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
      ),
      padding: EdgeInsets.only(
        bottom: MediaQuery.of(context).viewInsets.bottom + 16,
      ),
      child: SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            _buildHeader(),
            const Divider(),
            Flexible(
              child: SingleChildScrollView(
                padding: const EdgeInsets.all(16),
                child: Form(
                  key: _formKey,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      _buildNameField(),
                      const SizedBox(height: 20),
                      _buildTimeSelector(),
                      const SizedBox(height: 20),
                      _buildDaySelector(),
                      const SizedBox(height: 20),
                      _buildOptions(),
                      const SizedBox(height: 24),
                      _buildSaveButton(),
                    ],
                  ),
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildHeader() {
    return Padding(
      padding: const EdgeInsets.all(16),
      child: Row(
        children: [
          IconButton(
            icon: const Icon(Icons.close),
            onPressed: () => Navigator.pop(context),
          ),
          const Expanded(
            child: Text(
              '免打扰时段',
              textAlign: TextAlign.center,
              style: TextStyle(
                fontSize: 18,
                fontWeight: FontWeight.bold,
              ),
            ),
          ),
          const SizedBox(width: 48),
        ],
      ),
    );
  }

  Widget _buildNameField() {
    return TextFormField(
      controller: _nameController,
      decoration: InputDecoration(
        labelText: '时段名称',
        hintText: '例如：睡眠时间',
        prefixIcon: const Icon(Icons.label_outline),
        border: OutlineInputBorder(borderRadius: BorderRadius.circular(12)),
      ),
      validator: (value) {
        if (value == null || value.trim().isEmpty) {
          return '请输入时段名称';
        }
        return null;
      },
    );
  }

  Widget _buildTimeSelector() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          '时间段',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 12),
        Row(
          children: [
            Expanded(
              child: _buildTimeButton(
                label: '开始时间',
                time: _startTime,
                onTap: () => _selectTime(context, true),
              ),
            ),
            const Padding(
              padding: EdgeInsets.symmetric(horizontal: 12),
              child: Icon(Icons.arrow_forward, color: Colors.grey),
            ),
            Expanded(
              child: _buildTimeButton(
                label: '结束时间',
                time: _endTime,
                onTap: () => _selectTime(context, false),
              ),
            ),
          ],
        ),
      ],
    );
  }

  Widget _buildTimeButton({
    required String label,
    required TimeOfDay time,
    required VoidCallback onTap,
  }) {
    return InkWell(
      onTap: onTap,
      borderRadius: BorderRadius.circular(12),
      child: Container(
        padding: const EdgeInsets.symmetric(vertical: 16, horizontal: 12),
        decoration: BoxDecoration(
          border: Border.all(color: Colors.grey.shade300),
          borderRadius: BorderRadius.circular(12),
        ),
        child: Column(
          children: [
            Text(
              label,
              style: TextStyle(
                fontSize: 12,
                color: Colors.grey.shade600,
              ),
            ),
            const SizedBox(height: 4),
            Text(
              '${time.hour.toString().padLeft(2, '0')}:${time.minute.toString().padLeft(2, '0')}',
              style: const TextStyle(
                fontSize: 24,
                fontWeight: FontWeight.bold,
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildDaySelector() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            const Text(
              '重复日期',
              style: TextStyle(fontWeight: FontWeight.bold),
            ),
            TextButton(
              onPressed: _selectAllDays,
              child: const Text('全选'),
            ),
          ],
        ),
        const SizedBox(height: 8),
        Wrap(
          spacing: 8,
          children: List.generate(7, (index) {
            final day = index + 1;
            final isSelected = _activeDays.contains(day);
            return FilterChip(
              label: Text(_dayNames[index]),
              selected: isSelected,
              onSelected: (selected) {
                setState(() {
                  if (selected) {
                    _activeDays.add(day);
                  } else {
                    _activeDays.remove(day);
                  }
                  _activeDays.sort();
                });
              },
              selectedColor: Colors.blue.shade100,
              checkmarkColor: Colors.blue.shade700,
            );
          }),
        ),
      ],
    );
  }

  Widget _buildOptions() {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        const Text(
          '例外设置',
          style: TextStyle(fontWeight: FontWeight.bold),
        ),
        const SizedBox(height: 8),
        SwitchListTile(
          title: const Text('允许通话'),
          subtitle: const Text('免打扰期间仍接收语音/视频通话'),
          value: _allowCalls,
          onChanged: (value) => setState(() => _allowCalls = value),
          secondary: const Icon(Icons.call),
        ),
        SwitchListTile(
          title: const Text('允许@提及'),
          subtitle: const Text('被@时仍会收到通知'),
          value: _allowMentions,
          onChanged: (value) => setState(() => _allowMentions = value),
          secondary: const Icon(Icons.alternate_email),
        ),
      ],
    );
  }

  Widget _buildSaveButton() {
    return SizedBox(
      width: double.infinity,
      height: 50,
      child: ElevatedButton(
        onPressed: _save,
        style: ElevatedButton.styleFrom(
          shape: RoundedRectangleBorder(
            borderRadius: BorderRadius.circular(12),
          ),
        ),
        child: Text(
          widget.period == null ? '创建' : '保存',
          style: const TextStyle(fontSize: 16),
        ),
      ),
    );
  }

  void _selectAllDays() {
    setState(() {
      _activeDays = [1, 2, 3, 4, 5, 6, 7];
    });
  }

  Future<void> _selectTime(BuildContext context, bool isStart) async {
    final time = await showTimePicker(
      context: context,
      initialTime: isStart ? _startTime : _endTime,
    );
    if (time != null) {
      setState(() {
        if (isStart) {
          _startTime = time;
        } else {
          _endTime = time;
        }
      });
    }
  }

  void _save() async {
    if (!_formKey.currentState!.validate()) return;
    if (_activeDays.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('请至少选择一天')),
      );
      return;
    }

    final period = DoNotDisturbPeriodModel(
      id: widget.period?.id ?? '',
      userId: widget.period?.userId ?? '',
      name: _nameController.text.trim(),
      startHour: _startTime.hour,
      startMinute: _startTime.minute,
      endHour: _endTime.hour,
      endMinute: _endTime.minute,
      activeDays: _activeDays,
      isEnabled: widget.period?.isEnabled ?? true,
      allowCalls: _allowCalls,
      allowMentions: _allowMentions,
    );

    try {
      await widget.onSave(period);
      if (mounted) Navigator.pop(context);
    } catch (e) {
      // Error handled by store
    }
  }

  @override
  void dispose() {
    _nameController.dispose();
    super.dispose();
  }
}
