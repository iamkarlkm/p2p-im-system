// pages/do_not_disturb_page.dart
import 'package:flutter/material.dart';
import 'package:flutter_mobx/flutter_mobx.dart';
import '../stores/do_not_disturb_store.dart';
import '../widgets/do_not_disturb_period_card.dart';
import '../widgets/do_not_disturb_bottom_sheet.dart';

class DoNotDisturbPage extends StatefulWidget {
  const DoNotDisturbPage({Key? key}) : super(key: key);

  @override
  State<DoNotDisturbPage> createState() => _DoNotDisturbPageState();
}

class _DoNotDisturbPageState extends State<DoNotDisturbPage> {
  final DoNotDisturbStore _store = DoNotDisturbStore();

  @override
  void initState() {
    super.initState();
    _store.loadPeriods();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('免打扰设置'),
        centerTitle: true,
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: () => _showCreateBottomSheet(context),
          ),
        ],
      ),
      body: Observer(
        builder: (_) {
          if (_store.isLoading && _store.periods.isEmpty) {
            return const Center(child: CircularProgressIndicator());
          }

          return RefreshIndicator(
            onRefresh: _store.loadPeriods,
            child: CustomScrollView(
              slivers: [
                SliverToBoxAdapter(
                  child: _buildGlobalSwitch(),
                ),
                SliverToBoxAdapter(
                  child: _buildStatusCard(),
                ),
                if (_store.periods.isEmpty)
                  SliverFillRemaining(
                    child: _buildEmptyState(),
                  )
                else
                  SliverPadding(
                    padding: const EdgeInsets.all(16),
                    sliver: SliverList(
                      delegate: SliverChildBuilderDelegate(
                        (context, index) {
                          final period = _store.periods[index];
                          return DoNotDisturbPeriodCard(
                            period: period,
                            onToggle: (enabled) => _store.togglePeriod(period.id, enabled),
                            onEdit: () => _showEditBottomSheet(context, period),
                            onDelete: () => _confirmDelete(context, period.id),
                          );
                        },
                        childCount: _store.periods.length,
                      ),
                    ),
                  ),
              ],
            ),
          );
        },
      ),
      floatingActionButton: FloatingActionButton(
        onPressed: () => _showCreateBottomSheet(context),
        child: const Icon(Icons.add),
      ),
    );
  }

  Widget _buildGlobalSwitch() {
    return Observer(
      builder: (_) => Card(
        margin: const EdgeInsets.all(16),
        child: SwitchListTile(
          title: const Text(
            '开启免打扰模式',
            style: TextStyle(fontWeight: FontWeight.bold),
          ),
          subtitle: const Text('在设置的时间段内静音消息通知'),
          value: _store.isGlobalEnabled,
          onChanged: _store.toggleGlobalSetting,
          secondary: Icon(
            _store.isGlobalEnabled ? Icons.notifications_off : Icons.notifications,
            color: _store.isGlobalEnabled ? Colors.red : Colors.green,
          ),
        ),
      ),
    );
  }

  Widget _buildStatusCard() {
    return Observer(
      builder: (_) {
        if (!_store.isGlobalEnabled) return const SizedBox.shrink();
        
        final isActive = _store.isInDoNotDisturbMode;
        return Card(
          margin: const EdgeInsets.symmetric(horizontal: 16),
          color: isActive ? Colors.orange.shade50 : Colors.green.shade50,
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Row(
              children: [
                Icon(
                  isActive ? Icons.do_not_disturb_on : Icons.check_circle,
                  color: isActive ? Colors.orange : Colors.green,
                  size: 32,
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        isActive ? '当前处于免打扰模式' : '免打扰模式已开启但未生效',
                        style: const TextStyle(
                          fontWeight: FontWeight.bold,
                          fontSize: 16,
                        ),
                      ),
                      const SizedBox(height: 4),
                      if (isActive)
                        Text(
                          '${_store.activePeriods.length}个时段生效中',
                          style: TextStyle(
                            color: Colors.grey.shade600,
                            fontSize: 14,
                          ),
                        )
                      else
                        Text(
                          '当前不在任何免打扰时段内',
                          style: TextStyle(
                            color: Colors.grey.shade600,
                            fontSize: 14,
                          ),
                        ),
                    ],
                  ),
                ),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            Icons.notifications_off_outlined,
            size: 80,
            color: Colors.grey.shade300,
          ),
          const SizedBox(height: 16),
          Text(
            '还没有设置免打扰时段',
            style: TextStyle(
              fontSize: 16,
              color: Colors.grey.shade600,
            ),
          ),
          const SizedBox(height: 8),
          Text(
            '点击右下角按钮添加',
            style: TextStyle(
              fontSize: 14,
              color: Colors.grey.shade400,
            ),
          ),
        ],
      ),
    );
  }

  void _showCreateBottomSheet(BuildContext context) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => DoNotDisturbBottomSheet(
        store: _store,
        onSave: (period) => _store.createPeriod(period),
      ),
    );
  }

  void _showEditBottomSheet(BuildContext context, period) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      backgroundColor: Colors.transparent,
      builder: (context) => DoNotDisturbBottomSheet(
        store: _store,
        period: period,
        onSave: (updated) => _store.updatePeriod(updated),
      ),
    );
  }

  void _confirmDelete(BuildContext context, String periodId) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('确认删除'),
        content: const Text('确定要删除这个免打扰时段吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              Navigator.pop(context);
              _store.deletePeriod(periodId);
            },
            child: const Text('删除', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }
}
