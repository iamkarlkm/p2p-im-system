import 'package:flutter/material.dart';
import '../models/call_record.dart';
import '../services/call_service.dart';

class CallHistoryPage extends StatefulWidget {
  const CallHistoryPage({super.key});

  @override
  State<CallHistoryPage> createState() => _CallHistoryPageState();
}

class _CallHistoryPageState extends State<CallHistoryPage> {
  final CallService _service = CallService();
  List<CallRecord> _records = [];
  bool _loading = false;
  String _filter = 'ALL';

  @override
  void initState() {
    super.initState();
    _loadHistory();
  }

  Future<void> _loadHistory() async {
    setState(() => _loading = true);
    try {
      final list = await _service.getCallHistory();
      setState(() {
        _records = _filter == 'MISSED'
            ? list.where((r) => r.status == 'MISSED').toList()
            : list;
        _loading = false;
      });
    } catch (e) {
      setState(() => _loading = false);
    }
  }

  Future<void> _deleteRecord(int id) async {
    await _service.deleteCall(id);
    setState(() => _records.removeWhere((r) => r.id == id));
  }

  String _formatTime(DateTime d) {
    final now = DateTime.now();
    final diff = now.difference(d);
    if (diff.inDays == 0) {
      return '${d.hour.toString().padLeft(2, '0')}:${d.minute.toString().padLeft(2, '0')}';
    }
    if (diff.inDays == 1) return '昨天';
    if (diff.inDays < 7) return '${diff.inDays}天前';
    return '${d.month}/${d.day}';
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('通话记录'),
        actions: [
          PopupMenuButton<String>(
            onSelected: (v) {
              setState(() => _filter = v);
              _loadHistory();
            },
            itemBuilder: (_) => [
              const PopupMenuItem(value: 'ALL', child: Text('全部')),
              const PopupMenuItem(value: 'MISSED', child: Text('未接来电')),
            ],
          ),
        ],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : _records.isEmpty
              ? const Center(child: Text('暂无通话记录', style: TextStyle(color: Colors.grey)))
              : RefreshIndicator(
                  onRefresh: _loadHistory,
                  child: ListView.separated(
                    itemCount: _records.length,
                    separatorBuilder: (_, __) => const Divider(height: 1),
                    itemBuilder: (context, i) {
                      final r = _records[i];
                      final isMissed = r.status == 'MISSED';
                      return ListTile(
                        leading: CircleAvatar(
                          backgroundColor: isMissed ? Colors.red.shade50 : Colors.grey.shade100,
                          child: Icon(
                            r.callType == 'AUDIO' ? Icons.call : Icons.videocam,
                            color: isMissed ? Colors.red : Colors.grey,
                          ),
                        ),
                        title: Text(
                          r.callerName.isNotEmpty ? r.callerName : r.callerId,
                          style: TextStyle(
                            color: isMissed ? Colors.red : null,
                            fontWeight: FontWeight.w500,
                          ),
                        ),
                        subtitle: Row(
                          children: [
                            Text(
                              _service.getStatusLabel(r.status),
                              style: TextStyle(
                                fontSize: 12,
                                color: isMissed ? Colors.red : Colors.grey,
                              ),
                            ),
                            const SizedBox(width: 8),
                            Text(_formatTime(r.startTime), style: const TextStyle(fontSize: 12, color: Colors.grey)),
                            if (r.duration != null && r.duration! > 0) ...[
                              const SizedBox(width: 8),
                              Text(_service.formatDuration(r.duration), style: const TextStyle(fontSize: 12, color: Colors.grey)),
                            ],
                          ],
                        ),
                        trailing: IconButton(
                          icon: const Icon(Icons.delete_outline, size: 20, color: Colors.grey),
                          onPressed: () => _deleteRecord(r.id),
                        ),
                      );
                    },
                  ),
                ),
    );
  }
}
