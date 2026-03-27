import 'package:flutter/material.dart';
import 'package:intl/intl.dart';
import 'package:provider/provider.dart';
import '../models/message_attestation.dart';
import '../services/web3_attestation_service.dart';

class AttestationListPage extends StatefulWidget {
  const AttestationListPage({super.key});

  @override
  State<AttestationListPage> createState() => _AttestationListPageState();
}

class _AttestationListPageState extends State<AttestationListPage> {
  final ScrollController _scrollController = ScrollController();
  final TextEditingController _searchController = TextEditingController();
  
  int _currentPage = 0;
  bool _isLoadingMore = false;
  final List<MessageAttestation> _attestations = [];
  
  AttestationStatus? _filterStatus;
  BlockchainNetwork? _filterNetwork;

  @override
  void initState() {
    super.initState();
    _loadAttestations();
    _scrollController.addListener(_onScroll);
  }

  @override
  void dispose() {
    _scrollController.dispose();
    _searchController.dispose();
    super.dispose();
  }

  void _onScroll() {
    if (_scrollController.position.pixels >= _scrollController.position.maxScrollExtent - 200) {
      _loadMoreAttestations();
    }
  }

  Future<void> _loadAttestations() async {
    setState(() {
      _currentPage = 0;
      _attestations.clear();
    });
    
    final service = context.read<Web3AttestationService>();
    final filter = AttestationFilter(
      status: _filterStatus,
      network: _filterNetwork,
      searchQuery: _searchController.text.isNotEmpty ? _searchController.text : null,
    );
    
    final results = await service.listAttestations(
      filter: filter,
      page: _currentPage,
      size: 20,
    );
    
    setState(() {
      _attestations.addAll(results);
    });
  }

  Future<void> _loadMoreAttestations() async {
    if (_isLoadingMore) return;
    
    setState(() => _isLoadingMore = true);
    
    final service = context.read<Web3AttestationService>();
    final filter = AttestationFilter(
      status: _filterStatus,
      network: _filterNetwork,
      searchQuery: _searchController.text.isNotEmpty ? _searchController.text : null,
    );
    
    final results = await service.listAttestations(
      filter: filter,
      page: _currentPage + 1,
      size: 20,
    );
    
    setState(() {
      _isLoadingMore = false;
      if (results.isNotEmpty) {
        _currentPage++;
        _attestations.addAll(results);
      }
    });
  }

  Future<void> _refresh() async {
    await _loadAttestations();
    await context.read<Web3AttestationService>().getStatistics();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('消息存证'),
        actions: [
          IconButton(
            icon: const Icon(Icons.filter_list),
            onPressed: _showFilterDialog,
          ),
          IconButton(
            icon: const Icon(Icons.add),
            onPressed: _showCreateDialog,
          ),
        ],
      ),
      body: Column(
        children: [
          _buildSearchBar(),
          _buildFilterChips(),
          _buildStatisticsCard(),
          Expanded(child: _buildAttestationList()),
        ],
      ),
    );
  }

  Widget _buildSearchBar() {
    return Padding(
      padding: const EdgeInsets.all(8.0),
      child: TextField(
        controller: _searchController,
        decoration: InputDecoration(
          hintText: '搜索消息ID或哈希...',
          prefixIcon: const Icon(Icons.search),
          suffixIcon: _searchController.text.isNotEmpty
              ? IconButton(
                  icon: const Icon(Icons.clear),
                  onPressed: () {
                    _searchController.clear();
                    _loadAttestations();
                  },
                )
              : null,
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8.0),
          ),
        ),
        onSubmitted: (_) => _loadAttestations(),
      ),
    );
  }

  Widget _buildFilterChips() {
    final filters = <Widget>[];
    
    if (_filterStatus != null) {
      filters.add(Chip(
        label: Text(_filterStatus!.name),
        onDeleted: () {
          setState(() => _filterStatus = null);
          _loadAttestations();
        },
      ));
    }
    
    if (_filterNetwork != null) {
      filters.add(Chip(
        label: Text(_filterNetwork!.name),
        onDeleted: () {
          setState(() => _filterNetwork = null);
          _loadAttestations();
        },
      ));
    }
    
    if (filters.isEmpty) return const SizedBox.shrink();
    
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 8.0),
      child: Wrap(
        spacing: 8.0,
        children: filters,
      ),
    );
  }

  Widget _buildStatisticsCard() {
    return Consumer<Web3AttestationService>(
      builder: (context, service, child) {
        final stats = service.statistics;
        if (stats == null) return const SizedBox.shrink();
        
        return Card(
          margin: const EdgeInsets.all(8.0),
          child: Padding(
            padding: const EdgeInsets.all(12.0),
            child: Row(
              mainAxisAlignment: MainAxisAlignment.spaceAround,
              children: [
                _buildStatItem('总存证', stats.totalCount.toString(), Icons.verified),
                _buildStatItem('已确认', stats.confirmedCount.toString(), Icons.check_circle, Colors.green),
                _buildStatItem('待处理', stats.pendingCount.toString(), Icons.pending, Colors.orange),
                _buildStatItem('失败', stats.failedCount.toString(), Icons.error, Colors.red),
              ],
            ),
          ),
        );
      },
    );
  }

  Widget _buildStatItem(String label, String value, IconData icon, [Color? color]) {
    return Column(
      children: [
        Icon(icon, color: color ?? Colors.blue),
        const SizedBox(height: 4),
        Text(value, style: const TextStyle(fontWeight: FontWeight.bold, fontSize: 18)),
        Text(label, style: const TextStyle(fontSize: 12, color: Colors.grey)),
      ],
    );
  }

  Widget _buildAttestationList() {
    if (_attestations.isEmpty) {
      return Center(
        child: Column(
          mainAxisAlignment: MainAxisAlignment.center,
          children: [
            const Icon(Icons.inbox, size: 64, color: Colors.grey),
            const SizedBox(height: 16),
            const Text('暂无存证记录', style: TextStyle(color: Colors.grey)),
            const SizedBox(height: 8),
            ElevatedButton(
              onPressed: _showCreateDialog,
              child: const Text('创建存证'),
            ),
          ],
        ),
      );
    }

    return RefreshIndicator(
      onRefresh: _refresh,
      child: ListView.builder(
        controller: _scrollController,
        padding: const EdgeInsets.all(8.0),
        itemCount: _attestations.length + (_isLoadingMore ? 1 : 0),
        itemBuilder: (context, index) {
          if (index >= _attestations.length) {
            return const Center(
              child: Padding(
                padding: EdgeInsets.all(16.0),
                child: CircularProgressIndicator(),
              ),
            );
          }
          
          final attestation = _attestations[index];
          return _buildAttestationCard(attestation);
        },
      ),
    );
  }

  Widget _buildAttestationCard(MessageAttestation attestation) {
    final color = _getStatusColor(attestation.status);
    
    return Card(
      margin: const EdgeInsets.only(bottom: 8.0),
      child: InkWell(
        onTap: () => _showAttestationDetail(attestation),
        child: Padding(
          padding: const EdgeInsets.all(12.0),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Row(
                children: [
                  Container(
                    padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
                    decoration: BoxDecoration(
                      color: color.withOpacity(0.1),
                      borderRadius: BorderRadius.circular(4),
                    ),
                    child: Text(
                      attestation.status.name,
                      style: TextStyle(color: color, fontSize: 12, fontWeight: FontWeight.bold),
                    ),
                  ),
                  const SizedBox(width: 8),
                  Text(
                    attestation.network.name,
                    style: const TextStyle(fontSize: 12, color: Colors.grey),
                  ),
                  const Spacer(),
                  Text(
                    DateFormat('MM-dd HH:mm').format(attestation.createdAt),
                    style: const TextStyle(fontSize: 12, color: Colors.grey),
                  ),
                ],
              ),
              const SizedBox(height: 8),
              Text(
                '消息ID: ${_truncateString(attestation.messageId, 20)}',
                style: const TextStyle(fontWeight: FontWeight.w500),
              ),
              const SizedBox(height: 4),
              Text(
                '哈希: ${_truncateString(attestation.messageHash, 30)}',
                style: const TextStyle(fontSize: 12, color: Colors.grey, fontFamily: 'monospace'),
              ),
              if (attestation.transactionHash != null) ...[
                const SizedBox(height: 4),
                Text(
                  '交易: ${_truncateString(attestation.transactionHash!, 30)}',
                  style: const TextStyle(fontSize: 12, color: Colors.blue, fontFamily: 'monospace'),
                ),
              ],
            ],
          ),
        ),
      ),
    );
  }

  Color _getStatusColor(AttestationStatus status) {
    switch (status) {
      case AttestationStatus.pending:
        return Colors.grey;
      case AttestationStatus.submitting:
        return Colors.blue;
      case AttestationStatus.confirming:
        return Colors.orange;
      case AttestationStatus.confirmed:
        return Colors.green;
      case AttestationStatus.failed:
        return Colors.red;
    }
  }

  String _truncateString(String str, int maxLength) {
    if (str.length <= maxLength) return str;
    return '${str.substring(0, maxLength ~/ 2)}...${str.substring(str.length - maxLength ~/ 2)}';
  }

  void _showFilterDialog() {
    showModalBottomSheet(
      context: context,
      builder: (context) => StatefulBuilder(
        builder: (context, setModalState) => Container(
          padding: const EdgeInsets.all(16.0),
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              const Text('筛选条件', style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold)),
              const SizedBox(height: 16),
              const Text('状态', style: TextStyle(fontWeight: FontWeight.w500)),
              const SizedBox(height: 8),
              Wrap(
                spacing: 8.0,
                children: AttestationStatus.values.map((status) => ChoiceChip(
                  label: Text(status.name),
                  selected: _filterStatus == status,
                  onSelected: (selected) {
                    setModalState(() => _filterStatus = selected ? status : null);
                  },
                )).toList(),
              ),
              const SizedBox(height: 16),
              const Text('区块链网络', style: TextStyle(fontWeight: FontWeight.w500)),
              const SizedBox(height: 8),
              Wrap(
                spacing: 8.0,
                children: BlockchainNetwork.values.map((network) => ChoiceChip(
                  label: Text(network.name),
                  selected: _filterNetwork == network,
                  onSelected: (selected) {
                    setModalState(() => _filterNetwork = selected ? network : null);
                  },
                )).toList(),
              ),
              const SizedBox(height: 16),
              SizedBox(
                width: double.infinity,
                child: ElevatedButton(
                  onPressed: () {
                    Navigator.pop(context);
                    _loadAttestations();
                  },
                  child: const Text('应用筛选'),
                ),
              ),
            ],
          ),
        ),
      ),
    );
  }

  void _showCreateDialog() {
    showDialog(
      context: context,
      builder: (context) => const CreateAttestationDialog(),
    ).then((_) => _loadAttestations());
  }

  void _showAttestationDetail(MessageAttestation attestation) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => AttestationDetailPage(attestation: attestation),
      ),
    );
  }
}

class CreateAttestationDialog extends StatefulWidget {
  const CreateAttestationDialog({super.key});

  @override
  State<CreateAttestationDialog> createState() => _CreateAttestationDialogState();
}

class _CreateAttestationDialogState extends State<CreateAttestationDialog> {
  final _messageIdController = TextEditingController();
  final _contentController = TextEditingController();
  BlockchainNetwork _selectedNetwork = BlockchainNetwork.ethereum;
  bool _isCreating = false;

  @override
  Widget build(BuildContext context) {
    return AlertDialog(
      title: const Text('创建消息存证'),
      content: SingleChildScrollView(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: _messageIdController,
              decoration: const InputDecoration(
                labelText: '消息ID',
                hintText: '输入消息唯一标识',
              ),
            ),
            const SizedBox(height: 16),
            TextField(
              controller: _contentController,
              decoration: const InputDecoration(
                labelText: '消息内容',
                hintText: '输入要存证的消息内容',
              ),
              maxLines: 3,
            ),
            const SizedBox(height: 16),
            DropdownButtonFormField<BlockchainNetwork>(
              value: _selectedNetwork,
              decoration: const InputDecoration(labelText: '区块链网络'),
              items: BlockchainNetwork.values.map((network) => DropdownMenuItem(
                value: network,
                child: Text(network.name),
              )).toList(),
              onChanged: (value) => setState(() => _selectedNetwork = value!),
            ),
          ],
        ),
      ),
      actions: [
        TextButton(
          onPressed: () => Navigator.pop(context),
          child: const Text('取消'),
        ),
        ElevatedButton(
          onPressed: _isCreating ? null : _createAttestation,
          child: _isCreating
              ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2))
              : const Text('创建'),
        ),
      ],
    );
  }

  Future<void> _createAttestation() async {
    if (_messageIdController.text.isEmpty || _contentController.text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('请填写完整信息')),
      );
      return;
    }

    setState(() => _isCreating = true);

    final service = context.read<Web3AttestationService>();
    final result = await service.createAttestation(
      messageId: _messageIdController.text,
      messageContent: _contentController.text,
      network: _selectedNetwork,
    );

    setState(() => _isCreating = false);

    if (result != null && mounted) {
      Navigator.pop(context);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('存证创建成功')),
      );
    }
  }

  @override
  void dispose() {
    _messageIdController.dispose();
    _contentController.dispose();
    super.dispose();
  }
}

class AttestationDetailPage extends StatefulWidget {
  final MessageAttestation attestation;

  const AttestationDetailPage({super.key, required this.attestation});

  @override
  State<AttestationDetailPage> createState() => _AttestationDetailPageState();
}

class _AttestationDetailPageState extends State<AttestationDetailPage> {
  late MessageAttestation _attestation;
  VerificationResult? _verificationResult;
  bool _isVerifying = false;

  @override
  void initState() {
    super.initState();
    _attestation = widget.attestation;
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('存证详情'),
        actions: [
          IconButton(
            icon: const Icon(Icons.refresh),
            onPressed: _refreshAttestation,
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            _buildStatusCard(),
            const SizedBox(height: 16),
            _buildInfoCard(),
            const SizedBox(height: 16),
            if (_attestation.transactionHash != null) _buildBlockchainCard(),
            const SizedBox(height: 16),
            _buildVerificationCard(),
            const SizedBox(height: 16),
            _buildActionsCard(),
          ],
        ),
      ),
    );
  }

  Widget _buildStatusCard() {
    final color = _getStatusColor(_attestation.status);
    
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          children: [
            Icon(Icons.verified, size: 48, color: color),
            const SizedBox(height: 8),
            Text(
              _attestation.status.name,
              style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold, color: color),
            ),
            const SizedBox(height: 4),
            Text(
              _attestation.network.name,
              style: const TextStyle(color: Colors.grey),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('基本信息', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            const Divider(),
            _buildInfoRow('存证ID', _attestation.id),
            _buildInfoRow('消息ID', _attestation.messageId),
            _buildInfoRow('创建时间', DateFormat('yyyy-MM-dd HH:mm:ss').format(_attestation.createdAt)),
            if (_attestation.confirmedAt != null)
              _buildInfoRow('确认时间', DateFormat('yyyy-MM-dd HH:mm:ss').format(_attestation.confirmedAt!)),
          ],
        ),
      ),
    );
  }

  Widget _buildBlockchainCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('链上信息', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
            const Divider(),
            _buildInfoRow('交易哈希', _attestation.transactionHash!),
            _buildInfoRow('区块哈希', _attestation.blockHash ?? '待定'),
            _buildInfoRow('区块高度', _attestation.blockNumber?.toString() ?? '待定'),
            _buildInfoRow('确认数', _attestation.confirmCount.toString()),
            if (_attestation.gasUsed != null)
              _buildInfoRow('Gas消耗', _attestation.gasUsed.toString()),
          ],
        ),
      ),
    );
  }

  Widget _buildVerificationCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                const Text('验证结果', style: TextStyle(fontSize: 16, fontWeight: FontWeight.bold)),
                ElevatedButton(
                  onPressed: _isVerifying ? null : _verifyAttestation,
                  child: _isVerifying
                      ? const SizedBox(width: 20, height: 20, child: CircularProgressIndicator(strokeWidth: 2))
                      : const Text('验证'),
                ),
              ],
            ),
            const Divider(),
            if (_verificationResult == null)
              const Center(
                child: Padding(
                  padding: EdgeInsets.all(16.0),
                  child: Text('点击验证按钮进行存证验证', style: TextStyle(color: Colors.grey)),
                ),
              )
            else ...[
              Row(
                children: [
                  Icon(
                    _verificationResult!.isValid ? Icons.check_circle : Icons.error,
                    color: _verificationResult!.isValid ? Colors.green : Colors.red,
                    size: 32,
                  ),
                  const SizedBox(width: 8),
                  Expanded(
                    child: Text(
                      _verificationResult!.message,
                      style: TextStyle(
                        color: _verificationResult!.isValid ? Colors.green : Colors.red,
                        fontWeight: FontWeight.bold,
                      ),
                    ),
                  ),
                ],
              ),
              if (_verificationResult!.computedHash != null) ...[
                const SizedBox(height: 8),
                _buildInfoRow('计算哈希', _verificationResult!.computedHash!),
                _buildInfoRow('存储哈希', _verificationResult!.storedHash ?? 'N/A'),
              ],
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildActionsCard() {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16.0),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            if (_attestation.transactionHash != null)
              ElevatedButton.icon(
                onPressed: () => _openExplorer(_attestation.explorerTransactionUrl),
                icon: const Icon(Icons.open_in_new),
                label: const Text('查看交易'),
              ),
            const SizedBox(height: 8),
            ElevatedButton.icon(
              onPressed: _exportProof,
              icon: const Icon(Icons.download),
              label: const Text('导出证明'),
            ),
          ],
        ),
      ),
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4.0),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 80,
            child: Text(label, style: const TextStyle(color: Colors.grey, fontSize: 12)),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(fontFamily: 'monospace', fontSize: 12),
              overflow: TextOverflow.ellipsis,
            ),
          ),
          IconButton(
            icon: const Icon(Icons.copy, size: 16),
            onPressed: () => _copyToClipboard(value),
            padding: EdgeInsets.zero,
            constraints: const BoxConstraints(),
          ),
        ],
      ),
    );
  }

  Color _getStatusColor(AttestationStatus status) {
    switch (status) {
      case AttestationStatus.pending:
        return Colors.grey;
      case AttestationStatus.submitting:
        return Colors.blue;
      case AttestationStatus.confirming:
        return Colors.orange;
      case AttestationStatus.confirmed:
        return Colors.green;
      case AttestationStatus.failed:
        return Colors.red;
    }
  }

  Future<void> _refreshAttestation() async {
    final service = context.read<Web3AttestationService>();
    final updated = await service.getAttestationById(_attestation.id);
    if (updated != null) {
      setState(() => _attestation = updated);
    }
  }

  Future<void> _verifyAttestation() async {
    setState(() => _isVerifying = true);
    
    final service = context.read<Web3AttestationService>();
    final result = await service.verifyAttestation(_attestation.id);
    
    setState(() {
      _isVerifying = false;
      _verificationResult = result;
    });
  }

  void _openExplorer(String url) {
    // Launch URL
  }

  void _exportProof() {
    // Export proof implementation
  }

  void _copyToClipboard(String text) {
    // Copy to clipboard
  }
}
