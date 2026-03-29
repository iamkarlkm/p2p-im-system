import 'package:flutter/material.dart';
import '../models/message_sync.dart';
import '../services/message_sync_service.dart';

class MessageSyncScreen extends StatefulWidget {
  const MessageSyncScreen({super.key});

  @override
  State<MessageSyncScreen> createState() => _MessageSyncScreenState();
}

class _MessageSyncScreenState extends State<MessageSyncScreen> {
  final _service = MessageSyncService();
  List<SyncCheckpoint> _checkpoints = [];
  bool _loading = false;
  String? _error;
  DateTime? _lastSyncTime;

  @override
  void initState() {
    super.initState();
    _loadCheckpoints();
  }

  Future<void> _loadCheckpoints() async {
    setState(() { _loading = true; _error = null; });
    try {
      final checkpoints = await _service.fetchCheckpoints('mobile_device');
      setState(() {
        _checkpoints = checkpoints;
        _loading = false;
      });
    } catch (e) {
      setState(() { _error = e.toString(); _loading = false; });
    }
  }

  Future<void> _syncAll() async {
    setState(() { _loading = true; _error = null; });
    try {
      final request = SyncRequest(deviceId: 'mobile_device', limit: 50);
      final response = await _service.pullSync(request);
      setState(() {
        _lastSyncTime = response.syncTimestamp;
        _loading = false;
      });
      await _loadCheckpoints();
    } catch (e) {
      setState(() { _error = e.toString(); _loading = false; });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('消息同步'),
        actions: [
          IconButton(
            icon: _loading
                ? const SizedBox(
                    width: 20,
                    height: 20,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  )
                : const Icon(Icons.sync),
            onPressed: _loading ? null : _syncAll,
            tooltip: '同步所有会话',
          ),
        ],
      ),
      body: _loading && _checkpoints.isEmpty
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadCheckpoints,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  _buildSyncStatusCard(),
                  const SizedBox(height: 16),
                  _buildSyncInfo(),
                  const SizedBox(height: 16),
                  const Text('同步检查点', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 8),
                  if (_checkpoints.isEmpty)
                    const Card(
                      child: Padding(
                        padding: EdgeInsets.all(24),
                        child: Center(child: Text('暂无同步记录', style: TextStyle(color: Colors.grey))),
                      ),
                    )
                  else
                    ...(_checkpoints.map((cp) => _buildCheckpointTile(cp))),
                  if (_error != null)
                    Padding(
                      padding: const EdgeInsets.only(top: 16),
                      child: Text(_error!, style: const TextStyle(color: Colors.red, fontSize: 13)),
                    ),
                ],
              ),
            ),
    );
  }

  Widget _buildSyncStatusCard() {
    return Card(
      color: const Color(0xFF07C160).withValues(alpha: 0.1),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Row(
          children: [
            const Icon(Icons.cloud_done, color: Color(0xFF07C160), size: 40),
            const SizedBox(width: 12),
            Expanded(
              child: Column(
                crossAxisAlignment: CrossAxisAlignment.start,
                children: [
                  const Text('云端同步已启用', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 4),
                  Text(
                    _lastSyncTime != null
                        ? '上次同步: ${_formatTime(_lastSyncTime!)}'
                        : '点击同步按钮开始同步',
                    style: const TextStyle(fontSize: 13, color: Colors.grey),
                  ),
                ],
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildSyncInfo() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: const [
            Text('同步说明', style: TextStyle(fontSize: 14, fontWeight: FontWeight.bold)),
            SizedBox(height: 8),
            Text('• 消息会自动同步到云端', style: TextStyle(fontSize: 13, color: Colors.grey)),
            Text('• 在新设备登录后可同步历史消息', style: TextStyle(fontSize: 13, color: Colors.grey)),
            Text('• 最多保留30天消息历史', style: TextStyle(fontSize: 13, color: Colors.grey)),
            Text('• 删除的消息也会同步删除', style: TextStyle(fontSize: 13, color: Colors.grey)),
          ],
        ),
      ),
    );
  }

  Widget _buildCheckpointTile(SyncCheckpoint cp) {
    return Card(
      child: ListTile(
        leading: const Icon(Icons.chat_bubble_outline, color: Color(0xFF07C160)),
        title: Text('会话 ${cp.conversationId}'),
        subtitle: Text(
          '最后同步: ${_formatTime(cp.lastSyncedAt)}\n消息ID: ${cp.lastMessageId}',
          style: const TextStyle(fontSize: 12),
        ),
        isThreeLine: true,
      ),
    );
  }

  String _formatTime(DateTime dt) {
    return '${dt.year}-${dt.month.toString().padLeft(2, '0')}-${dt.day.toString().padLeft(2, '0')} '
        '${dt.hour.toString().padLeft(2, '0')}:${dt.minute.toString().padLeft(2, '0')}';
  }
}
