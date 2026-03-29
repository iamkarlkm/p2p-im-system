import 'package:flutter/material.dart';
import '../models/report.dart';
import '../services/report_service.dart';

class ReportScreen extends StatefulWidget {
  const ReportScreen({super.key});

  @override
  State<ReportScreen> createState() => _ReportScreenState();
}

class _ReportScreenState extends State<ReportScreen> {
  final ReportService _service = ReportService();
  final List<Report> _reports = [];
  bool _isLoading = false;
  String? _selectedReason;

  final _reasons = ['色情内容', '暴力血腥', '诈骗信息', '仇恨言论', '垃圾广告', '侵犯隐私', '谣言虚假信息', '其他违规'];
  final _descController = TextEditingController();

  @override
  void initState() {
    super.initState();
    _loadReports();
  }

  Future<void> _loadReports() async {
    setState(() => _isLoading = true);
    final reports = await _service.getMyReports();
    setState(() { _reports.addAll(reports); _isLoading = false; });
  }

  Future<void> _submitReport() async {
    if (_selectedReason == null) return;
    final report = await _service.submitReport(
      reportedMessageId: 0, reportedUserId: 0, conversationId: 0,
      conversationType: 'private', reportReason: _selectedReason!,
      reportCategory: _selectedReason!, description: _descController.text,
    );
    if (report != null) {
      setState(() => _reports.insert(0, report));
      _descController.clear();
      if (mounted) ScaffoldMessenger.of(context).showSnackBar(const SnackBar(content: Text('举报已提交')));
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('举报与内容审核')),
      body: _isLoading
          ? const Center(child: CircularProgressIndicator())
          : RefreshIndicator(
              onRefresh: _loadReports,
              child: ListView(
                padding: const EdgeInsets.all(16),
                children: [
                  Card(
                    child: Padding(
                      padding: const EdgeInsets.all(16),
                      child: Column(
                        crossAxisAlignment: CrossAxisAlignment.start,
                        children: [
                          const Text('提交举报', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                          const SizedBox(height: 12),
                          DropdownButtonFormField<String>(
                            decoration: const InputDecoration(labelText: '举报原因'),
                            items: _reasons.map((r) => DropdownMenuItem(value: r, child: Text(r))).toList(),
                            onChanged: (v) => setState(() => _selectedReason = v),
                          ),
                          const SizedBox(height: 12),
                          TextField(
                            controller: _descController,
                            decoration: const InputDecoration(labelText: '详细描述', border: OutlineInputBorder()),
                            maxLines: 4,
                          ),
                          const SizedBox(height: 12),
                          ElevatedButton(
                            onPressed: _submitReport,
                            style: ElevatedButton.styleFrom(backgroundColor: Colors.red, foregroundColor: Colors.white),
                            child: const Text('提交举报'),
                          ),
                        ],
                      ),
                    ),
                  ),
                  const SizedBox(height: 16),
                  const Text('我的举报记录', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
                  const SizedBox(height: 12),
                  if (_reports.isEmpty)
                    const Center(child: Text('暂无举报记录', style: TextStyle(color: Colors.grey)))
                  else
                    ...(_reports.map((r) => Card(
                      margin: const EdgeInsets.only(bottom: 8),
                      child: ListTile(
                        title: Text(r.reportReason),
                        subtitle: Text(r.description.isEmpty ? '无描述' : r.description),
                        trailing: Chip(label: Text(_getStatusText(r.status))),
                      ),
                    ))),
                ],
              ),
            ),
    );
  }

  String _getStatusText(String status) {
    final map = {'PENDING': '待处理', 'REVIEWING': '审核中', 'RESOLVED': '已处理', 'DISMISSED': '已驳回'};
    return map[status] ?? status;
  }
}
