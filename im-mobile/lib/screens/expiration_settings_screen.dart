/**
 * 消息过期设置页面
 */
import 'package:flutter/material.dart';
import '../models/message_expiration_rule.dart';
import '../services/message_expiration_service.dart';
import 'package:dio/dio.dart';

class ExpirationSettingsScreen extends StatefulWidget {
  const ExpirationSettingsScreen({super.key});

  @override
  State<ExpirationSettingsScreen> createState() => _ExpirationSettingsScreenState();
}

class _ExpirationSettingsScreenState extends State<ExpirationSettingsScreen> {
  final _service = MessageExpirationService(Dio());
  List<MessageExpirationRule> _rules = [];
  MessageExpirationRule? _globalRule;
  bool _loading = true;

  // 新规则表单
  String _newExpirationType = 'TIME_BASED';
  int? _newRelativeSeconds;
  String _newMessageFilter = 'ALL';
  bool _newPreExpireNotice = false;

  @override
  void initState() {
    super.initState();
    _loadRules();
  }

  Future<void> _loadRules() async {
    setState(() => _loading = true);
    try {
      final rules = await _service.getUserRules();
      final global = await _service.getGlobalRule();
      setState(() {
        _rules = rules.where((r) => r.conversationId != null).toList();
        _globalRule = global;
        _loading = false;
      });
    } catch (e) {
      setState(() => _loading = false);
    }
  }

  Future<void> _createGlobalRule() async {
    if (_newRelativeSeconds == null) return;
    try {
      final req = ExpirationRuleRequest(
        expirationType: _newExpirationType,
        relativeSeconds: _newRelativeSeconds,
        messageTypeFilter: _newMessageFilter,
        preExpireNotice: _newPreExpireNotice,
        active: true,
      );
      final rule = await _service.createRule('', req);
      setState(() {
        _globalRule = rule;
      });
      if (mounted) Navigator.pop(context);
    } catch (e) {
      ScaffoldMessenger.of(context).showSnackBar(SnackBar(content: Text('创建规则失败: $e')));
    }
  }

  Future<void> _deleteRule(MessageExpirationRule rule) async {
    await _service.deleteRule(rule.id);
    setState(() {
      _rules.removeWhere((r) => r.id == rule.id);
    });
  }

  Future<void> _toggleRule(MessageExpirationRule rule) async {
    final updated = await _service.toggleRule(rule.id, !rule.active);
    setState(() {
      final idx = _rules.indexWhere((r) => r.id == rule.id);
      if (idx >= 0) _rules[idx] = updated;
    });
  }

  void _showCreateDialog() {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      builder: (ctx) => Padding(
        padding: EdgeInsets.only(
          bottom: MediaQuery.of(ctx).viewInsets.bottom,
          left: 16, right: 16, top: 16,
        ),
        child: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('创建过期规则', style: Theme.of(ctx).textTheme.titleLarge),
            const SizedBox(height: 16),
            DropdownButtonFormField<String>(
              value: _newExpirationType,
              decoration: const InputDecoration(labelText: '过期类型'),
              items: const [
                DropdownMenuItem(value: 'TIME_BASED', child: Text('定时过期')),
                DropdownMenuItem(value: 'SELF_DESTRUCT', child: Text('阅后即焚')),
                DropdownMenuItem(value: 'GLOBAL', child: Text('全局默认')),
              ],
              onChanged: (v) => setState(() => _newExpirationType = v!),
            ),
            const SizedBox(height: 12),
            TextField(
              decoration: const InputDecoration(
                labelText: '过期时长（秒）',
                hintText: '如: 3600 = 1小时',
              ),
              keyboardType: TextInputType.number,
              onChanged: (v) => _newRelativeSeconds = int.tryParse(v),
            ),
            const SizedBox(height: 12),
            DropdownButtonFormField<String>(
              value: _newMessageFilter,
              decoration: const InputDecoration(labelText: '消息类型'),
              items: const [
                DropdownMenuItem(value: 'ALL', child: Text('所有消息')),
                DropdownMenuItem(value: 'TEXT', child: Text('仅文本')),
                DropdownMenuItem(value: 'IMAGE', child: Text('仅图片')),
                DropdownMenuItem(value: 'FILE', child: Text('仅文件')),
              ],
              onChanged: (v) => setState(() => _newMessageFilter = v!),
            ),
            const SizedBox(height: 12),
            SwitchListTile(
              title: const Text('过期前发送提醒'),
              value: _newPreExpireNotice,
              onChanged: (v) => setState(() => _newPreExpireNotice = v),
            ),
            const SizedBox(height: 16),
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                TextButton(
                  onPressed: () => Navigator.pop(ctx),
                  child: const Text('取消'),
                ),
                const SizedBox(width: 8),
                FilledButton(
                  onPressed: _createGlobalRule,
                  child: const Text('保存'),
                ),
              ],
            ),
            const SizedBox(height: 16),
          ],
        ),
      ),
    );
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('消息过期设置'),
        actions: [
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: _showCreateDialog,
          ),
        ],
      ),
      body: _loading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadRules,
              child: ListView(
                children: [
                  _buildSection('全局默认规则', [
                    if (_globalRule != null)
                      _buildRuleTile(_globalRule!, isGlobal: true)
                    else
                      ListTile(
                        title: const Text('未设置全局规则'),
                        subtitle: const Text('Tap + 创建'),
                        trailing: IconButton(
                          icon: const Icon(Icons.add),
                          onPressed: _showCreateDialog,
                        ),
                      ),
                  ]),
                  _buildSection('会话规则 (${_rules.length})', _rules.map((r) => _buildRuleTile(r)).toList()),
                ],
              ),
            ),
    );
  }

  Widget _buildSection(String title, List<Widget> children) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Padding(
          padding: const EdgeInsets.fromLTRB(16, 16, 16, 8),
          child: Text(title, style: Theme.of(context).textTheme.titleSmall?.copyWith(color: Colors.grey)),
        ),
        ...children,
      ],
    );
  }

  Widget _buildRuleTile(MessageExpirationRule rule, {bool isGlobal = false}) {
    return ListTile(
      leading: CircleAvatar(
        backgroundColor: rule.active ? Colors.red.shade100 : Colors.grey.shade200,
        child: Icon(Icons.timer, color: rule.active ? Colors.red : Colors.grey),
      ),
      title: Text(rule.typeLabel),
      subtitle: Text('${rule.timeLabel} • ${rule.messageTypeFilter}'),
      trailing: Row(
        mainAxisSize: MainAxisSize.min,
        children: [
          Switch(
            value: rule.active,
            onChanged: (_) => isGlobal ? {} : _toggleRule(rule),
          ),
          if (!isGlobal)
            IconButton(
              icon: const Icon(Icons.delete_outline, color: Colors.red),
              onPressed: () => _deleteRule(rule),
            ),
        ],
      ),
    );
  }
}
