import 'package:flutter/material.dart';
import '../models/dnd_settings.dart';
import '../services/dnd_settings_service.dart';

class DndSettingsScreen extends StatefulWidget {
  const DndSettingsScreen({super.key});

  @override
  State<DndSettingsScreen> createState() => _DndSettingsScreenState();
}

class _DndSettingsScreenState extends State<DndSettingsScreen> {
  final _service = DndSettingsService();
  DndSettings _settings = defaultDndSettings;
  DndStatus? _status;
  bool _loading = true;
  String? _error;

  @override
  void initState() {
    super.initState();
    _loadData();
  }

  Future<void> _loadData() async {
    setState(() { _loading = true; _error = null; });
    try {
      final settings = await _service.fetchSettings();
      final status = await _service.fetchStatus();
      setState(() {
        _settings = settings;
        _status = status;
        _loading = false;
      });
    } catch (e) {
      setState(() {
        _error = e.toString();
        _loading = false;
      });
    }
  }

  Future<void> _saveSettings(DndSettings updated) async {
    setState(() { _loading = true; });
    try {
      final saved = await _service.saveSettings(updated);
      final status = await _service.fetchStatus();
      setState(() {
        _settings = saved;
        _status = status;
        _loading = false;
      });
    } catch (e) {
      setState(() { _loading = false; _error = e.toString(); });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('免打扰设置'),
        leading: IconButton(
          icon: const Icon(Icons.arrow_back),
          onPressed: () => Navigator.pop(context),
        ),
        actions: [
          if (_status?.inDndPeriod == true)
            const Padding(
              padding: EdgeInsets.only(right: 16),
              child: Center(child: Icon(Icons.do_not_disturb, color: Colors.red)),
            ),
        ],
      ),
      body: _loading && _settings.enabled
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadData,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  _buildMainToggle(),
                  if (_settings.enabled) ...[
                    const SizedBox(height: 16),
                    _buildTimeSection(),
                    const SizedBox(height: 16),
                    _buildRepeatSection(),
                    const SizedBox(height: 16),
                    _buildExceptionSection(),
                    const SizedBox(height: 16),
                    _buildAutoReplySection(),
                  ],
                  if (_status?.inDndPeriod == true) ...[
                    const SizedBox(height: 16),
                    _buildActiveBadge(),
                  ],
                  if (_error != null) ...[
                    const SizedBox(height: 16),
                    Text(_error!, style: const TextStyle(color: Colors.red, fontSize: 13)),
                  ],
                ],
              ),
            ),
    );
  }

  Widget _buildMainToggle() {
    return Card(
      child: SwitchListTile(
        title: const Text('开启免打扰'),
        subtitle: const Text('在指定时间段内静默推送'),
        value: _settings.enabled,
        activeColor: const Color(0xFF07C160),
        onChanged: (val) {
          final updated = _settings.copyWith(enabled: val);
          _saveSettings(updated);
        },
      ),
    );
  }

  Widget _buildTimeSection() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('时间段设置', style: TextStyle(fontSize: 14, color: Colors.grey)),
            const SizedBox(height: 12),
            Row(
              children: [
                Expanded(
                  child: _TimePickerField(
                    label: '开始时间',
                    value: _settings.startTime,
                    onChanged: (time) {
                      _saveSettings(_settings.copyWith(startTime: time));
                    },
                  ),
                ),
                const Padding(
                  padding: EdgeInsets.symmetric(horizontal: 12),
                  child: Text('—', style: TextStyle(fontSize: 18, color: Colors.grey)),
                ),
                Expanded(
                  child: _TimePickerField(
                    label: '结束时间',
                    value: _settings.endTime,
                    onChanged: (time) {
                      _saveSettings(_settings.copyWith(endTime: time));
                    },
                  ),
                ),
              ],
            ),
            const SizedBox(height: 8),
            Text('时区: ${_settings.timezone}', style: const TextStyle(fontSize: 12, color: Colors.grey)),
          ],
        ),
      ),
    );
  }

  Widget _buildRepeatSection() {
    final days = _settings.repeatDays.split(',').map((d) => int.tryParse(d.trim())).where((d) => d != null).cast<int>().toList();
    final dayLabels = ['一', '二', '三', '四', '五', '六', '日'];

    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('重复设置', style: TextStyle(fontSize: 14, color: Colors.grey)),
            const SizedBox(height: 12),
            Wrap(
              spacing: 8,
              runSpacing: 8,
              children: List.generate(7, (i) {
                final value = i + 1;
                final active = days.contains(value);
                return GestureDetector(
                  onTap: () {
                    final newDays = List<int>.from(days);
                    if (active) {
                      newDays.remove(value);
                    } else {
                      newDays.add(value);
                      newDays.sort();
                    }
                    _saveSettings(_settings.copyWith(repeatDays: newDays.join(',')));
                  },
                  child: Container(
                    width: 40,
                    height: 40,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      color: active ? const Color(0xFF07C160) : Colors.white,
                      border: Border.all(color: active ? const Color(0xFF07C160) : Colors.grey.shade300),
                    ),
                    child: Center(
                      child: Text(
                        dayLabels[i],
                        style: TextStyle(fontSize: 13, color: active ? Colors.white : Colors.grey.shade700),
                      ),
                    ),
                  ),
                );
              }),
            ),
            const SizedBox(height: 12),
            Row(
              children: [
                _PresetChip(label: '每天', onTap: () => _saveSettings(_settings.copyWith(repeatDays: '1,2,3,4,5,6,7'))),
                const SizedBox(width: 8),
                _PresetChip(label: '工作日', onTap: () => _saveSettings(_settings.copyWith(repeatDays: '1,2,3,4,5'))),
                const SizedBox(width: 8),
                _PresetChip(label: '周末', onTap: () => _saveSettings(_settings.copyWith(repeatDays: '6,7'))),
              ],
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildExceptionSection() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('例外设置', style: TextStyle(fontSize: 14, color: Colors.grey)),
            const SizedBox(height: 4),
            SwitchListTile(
              title: const Text('允许@提及'),
              subtitle: const Text('被@时仍可收到通知', style: TextStyle(fontSize: 12)),
              value: _settings.allowMentions,
              activeColor: const Color(0xFF07C160),
              contentPadding: EdgeInsets.zero,
              onChanged: (val) {
                _saveSettings(_settings.copyWith(allowMentions: val));
              },
            ),
            SwitchListTile(
              title: const Text('允许星标好友'),
              subtitle: const Text('星标联系人消息仍可推送', style: TextStyle(fontSize: 12)),
              value: _settings.allowStarred,
              activeColor: const Color(0xFF07C160),
              contentPadding: EdgeInsets.zero,
              onChanged: (val) {
                _saveSettings(_settings.copyWith(allowStarred: val));
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildAutoReplySection() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('自动回复', style: TextStyle(fontSize: 14, color: Colors.grey)),
            const SizedBox(height: 12),
            TextField(
              controller: TextEditingController(text: _settings.customMessage ?? ''),
              decoration: const InputDecoration(
                hintText: '免打扰时自动回复内容（可选）',
                border: OutlineInputBorder(),
              ),
              maxLines: 3,
              onChanged: (val) {
                _saveSettings(_settings.copyWith(customMessage: val));
              },
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildActiveBadge() {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
      decoration: BoxDecoration(
        color: Colors.yellow.shade50,
        borderRadius: BorderRadius.circular(12),
        border: Border.all(color: Colors.yellow.shade200),
      ),
      child: Row(
        children: [
          const Icon(Icons.do_not_disturb_on, color: Colors.orange),
          const SizedBox(width: 8),
          const Expanded(
            child: Text('当前处于免打扰状态', style: TextStyle(fontSize: 14, color: Colors.deepOrange)),
          ),
        ],
      ),
    );
  }
}

class _TimePickerField extends StatelessWidget {
  final String label;
  final String value;
  final ValueChanged<String> onChanged;

  const _TimePickerField({
    required this.label,
    required this.value,
    required this.onChanged,
  });

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(label, style: const TextStyle(fontSize: 12, color: Colors.grey)),
        const SizedBox(height: 4),
        GestureDetector(
          onTap: () async {
            final parts = value.split(':');
            final initialTime = TimeOfDay(
              hour: int.parse(parts[0]),
              minute: int.parse(parts[1]),
            );
            final picked = await showTimePicker(context: context, initialTime: initialTime);
            if (picked != null) {
              final timeStr = '${picked.hour.toString().padLeft(2, '0')}:${picked.minute.toString().padLeft(2, '0')}';
              onChanged(timeStr);
            }
          },
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 10),
            decoration: BoxDecoration(
              border: Border.all(color: Colors.grey.shade300),
              borderRadius: BorderRadius.circular(8),
            ),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(value, style: const TextStyle(fontSize: 14)),
                const Icon(Icons.access_time, size: 18, color: Colors.grey),
              ],
            ),
          ),
        ),
      ],
    );
  }
}

class _PresetChip extends StatelessWidget {
  final String label;
  final VoidCallback onTap;

  const _PresetChip({required this.label, required this.onTap});

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: Container(
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 6),
        decoration: BoxDecoration(
          borderRadius: BorderRadius.circular(16),
          border: Border.all(color: Colors.grey.shade300),
        ),
        child: Text(label, style: const TextStyle(fontSize: 13)),
      ),
    );
  }
}
