import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/quantum_certificate.dart';
import '../services/certificate_verification_service.dart';
import 'certificate_detail_view.dart';
import 'certificate_qr_scanner.dart';

/// 量子安全证书管理器
/// 管理本地证书列表，支持导入、导出和验证
class CertificateManager extends StatefulWidget {
  const CertificateManager({Key? key}) : super(key: key);

  @override
  State<CertificateManager> createState() => _CertificateManagerState();
}

class _CertificateManagerState extends State<CertificateManager> 
    with SingleTickerProviderStateMixin {
  final CertificateVerificationService _verificationService = 
      CertificateVerificationService();
  
  List<QuantumSecureCertificate> _certificates = [];
  List<QuantumSecureCertificate> _filteredCertificates = [];
  bool _isLoading = true;
  String? _errorMessage;
  
  late TabController _tabController;
  String _searchQuery = '';
  String _filterStatus = 'ALL';

  @override
  void initState() {
    super.initState();
    _tabController = TabController(length: 4, vsync: this);
    _loadCertificates();
  }

  @override
  void dispose() {
    _tabController.dispose();
    super.dispose();
  }

  Future<void> _loadCertificates() async {
    setState(() {
      _isLoading = true;
      _errorMessage = null;
    });

    try {
      // TODO: 从存储服务加载证书列表
      // 这里模拟一些数据用于展示
      _certificates = [];
      _applyFilter();
    } catch (e) {
      setState(() {
        _errorMessage = '加载证书失败: $e';
      });
    } finally {
      setState(() {
        _isLoading = false;
      });
    }
  }

  void _applyFilter() {
    setState(() {
      _filteredCertificates = _certificates.where((cert) {
        // 搜索过滤
        final matchesSearch = _searchQuery.isEmpty ||
            cert.subjectName.toLowerCase().contains(_searchQuery.toLowerCase()) ||
            cert.issuerName.toLowerCase().contains(_searchQuery.toLowerCase()) ||
            cert.certificateId.toLowerCase().contains(_searchQuery.toLowerCase());
        
        // 状态过滤
        bool matchesStatus = true;
        switch (_filterStatus) {
          case 'VALID':
            matchesStatus = cert.isValid;
            break;
          case 'EXPIRED':
            matchesStatus = cert.isExpired;
            break;
          case 'EXPIRING_SOON':
            matchesStatus = cert.isExpiringSoon;
            break;
          case 'REVOKED':
            matchesStatus = cert.isRevoked;
            break;
        }
        
        return matchesSearch && matchesStatus;
      }).toList();
    });
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('量子安全证书管理'),
        bottom: TabBar(
          controller: _tabController,
          tabs: const [
            Tab(icon: Icon(Icons.security), text: '全部'),
            Tab(icon: Icon(Icons.verified), text: '有效'),
            Tab(icon: Icon(Icons.warning), text: '警告'),
            Tab(icon: Icon(Icons.error), text: '问题'),
          ],
        ),
        actions: [
          IconButton(
            icon: const Icon(Icons.qr_code_scanner),
            tooltip: '扫描二维码',
            onPressed: _scanQrCode,
          ),
          IconButton(
            icon: const Icon(Icons.add),
            tooltip: '导入证书',
            onPressed: _importCertificate,
          ),
          PopupMenuButton<String>(
            onSelected: (value) {
              switch (value) {
                case 'refresh':
                  _loadCertificates();
                  break;
                case 'clear_cache':
                  _clearCache();
                  break;
                case 'export_all':
                  _exportAllCertificates();
                  break;
              }
            },
            itemBuilder: (context) => [
              const PopupMenuItem(
                value: 'refresh',
                child: ListTile(
                  leading: Icon(Icons.refresh),
                  title: Text('刷新'),
                ),
              ),
              const PopupMenuItem(
                value: 'clear_cache',
                child: ListTile(
                  leading: Icon(Icons.delete_sweep),
                  title: Text('清除缓存'),
                ),
              ),
              const PopupMenuItem(
                value: 'export_all',
                child: ListTile(
                  leading: Icon(Icons.download),
                  title: Text('导出全部'),
                ),
              ),
            ],
          ),
        ],
      ),
      body: Column(
        children: [
          // 搜索栏
          Padding(
            padding: const EdgeInsets.all(8.0),
            child: TextField(
              decoration: InputDecoration(
                hintText: '搜索证书...',
                prefixIcon: const Icon(Icons.search),
                suffixIcon: _searchQuery.isNotEmpty
                    ? IconButton(
                        icon: const Icon(Icons.clear),
                        onPressed: () {
                          setState(() {
                            _searchQuery = '';
                          });
                          _applyFilter();
                        },
                      )
                    : null,
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8),
                ),
              ),
              onChanged: (value) {
                setState(() {
                  _searchQuery = value;
                });
                _applyFilter();
              },
            ),
          ),
          
          // 统计信息
          _buildStatisticsBar(),
          
          // 证书列表
          Expanded(
            child: _isLoading
                ? const Center(child: CircularProgressIndicator())
                : _errorMessage != null
                    ? _buildErrorView()
                    : _filteredCertificates.isEmpty
                        ? _buildEmptyView()
                        : _buildCertificateList(),
          ),
        ],
      ),
    );
  }

  Widget _buildStatisticsBar() {
    final validCount = _certificates.where((c) => c.isValid).length;
    final expiredCount = _certificates.where((c) => c.isExpired).length;
    final expiringSoonCount = _certificates.where((c) => c.isExpiringSoon).length;
    final revokedCount = _certificates.where((c) => c.isRevoked).length;

    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 16, vertical: 8),
      child: Row(
        mainAxisAlignment: MainAxisAlignment.spaceAround,
        children: [
          _buildStatItem('全部', _certificates.length, Colors.blue),
          _buildStatItem('有效', validCount, Colors.green),
          _buildStatItem('即将过期', expiringSoonCount, Colors.orange),
          _buildStatItem('已过期', expiredCount, Colors.red),
          _buildStatItem('已吊销', revokedCount, Colors.grey),
        ],
      ),
    );
  }

  Widget _buildStatItem(String label, int count, Color color) {
    return Column(
      children: [
        Text(
          count.toString(),
          style: TextStyle(
            fontSize: 20,
            fontWeight: FontWeight.bold,
            color: color,
          ),
        ),
        Text(
          label,
          style: TextStyle(
            fontSize: 12,
            color: Colors.grey[600],
          ),
        ),
      ],
    );
  }

  Widget _buildErrorView() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          const Icon(Icons.error_outline, size: 64, color: Colors.red),
          const SizedBox(height: 16),
          Text(
            _errorMessage!,
            textAlign: TextAlign.center,
            style: const TextStyle(color: Colors.red),
          ),
          const SizedBox(height: 16),
          ElevatedButton(
            onPressed: _loadCertificates,
            child: const Text('重试'),
          ),
        ],
      ),
    );
  }

  Widget _buildEmptyView() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(Icons.security, size: 64, color: Colors.grey[400]),
          const SizedBox(height: 16),
          Text(
            '暂无证书',
            style: TextStyle(
              fontSize: 18,
              color: Colors.grey[600],
            ),
          ),
          const SizedBox(height: 8),
          Text(
            '点击右上角的 + 导入证书',
            style: TextStyle(
              fontSize: 14,
              color: Colors.grey[500],
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildCertificateList() {
    return ListView.builder(
      itemCount: _filteredCertificates.length,
      itemBuilder: (context, index) {
        final cert = _filteredCertificates[index];
        return _buildCertificateCard(cert);
      },
    );
  }

  Widget _buildCertificateCard(QuantumSecureCertificate cert) {
    final statusColor = _getStatusColor(cert);
    final statusIcon = _getStatusIcon(cert);

    return Card(
      margin: const EdgeInsets.symmetric(horizontal: 12, vertical: 6),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: statusColor.withOpacity(0.2),
          child: Icon(statusIcon, color: statusColor),
        ),
        title: Text(
          cert.subjectName,
          style: const TextStyle(fontWeight: FontWeight.bold),
        ),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text(cert.issuerName),
            const SizedBox(height: 4),
            Row(
              children: [
                Container(
                  padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                  decoration: BoxDecoration(
                    color: Colors.blue.withOpacity(0.1),
                    borderRadius: BorderRadius.circular(4),
                  ),
                  child: Text(
                    cert.algorithmType,
                    style: const TextStyle(fontSize: 10),
                  ),
                ),
                const SizedBox(width: 8),
                Text(
                  '有效期: ${_formatDate(cert.validUntil)}',
                  style: TextStyle(
                    fontSize: 12,
                    color: cert.isExpiringSoon ? Colors.orange : Colors.grey,
                  ),
                ),
              ],
            ),
          ],
        ),
        trailing: Row(
          mainAxisSize: MainAxisSize.min,
          children: [
            if (cert.isExpiringSoon)
              const Tooltip(
                message: '即将过期',
                child: Icon(Icons.timer, color: Colors.orange, size: 20),
              ),
            if (cert.isRevoked)
              const Tooltip(
                message: '已吊销',
                child: Icon(Icons.block, color: Colors.red, size: 20),
              ),
            const Icon(Icons.chevron_right),
          ],
        ),
        onTap: () => _viewCertificateDetail(cert),
        onLongPress: () => _showCertificateOptions(cert),
      ),
    );
  }

  Color _getStatusColor(QuantumSecureCertificate cert) {
    if (cert.isRevoked) return Colors.red;
    if (cert.isExpired) return Colors.grey;
    if (cert.isExpiringSoon) return Colors.orange;
    return Colors.green;
  }

  IconData _getStatusIcon(QuantumSecureCertificate cert) {
    if (cert.isRevoked) return Icons.block;
    if (cert.isExpired) return Icons.error_outline;
    if (cert.isExpiringSoon) return Icons.timer;
    return Icons.verified;
  }

  String _formatDate(DateTime date) {
    return '${date.year}-${date.month.toString().padLeft(2, '0')}-${date.day.toString().padLeft(2, '0')}';
  }

  void _viewCertificateDetail(QuantumSecureCertificate cert) {
    Navigator.push(
      context,
      MaterialPageRoute(
        builder: (context) => CertificateDetailView(certificate: cert),
      ),
    );
  }

  void _showCertificateOptions(QuantumSecureCertificate cert) {
    showModalBottomSheet(
      context: context,
      builder: (context) => SafeArea(
        child: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            ListTile(
              leading: const Icon(Icons.visibility),
              title: const Text('查看详情'),
              onTap: () {
                Navigator.pop(context);
                _viewCertificateDetail(cert);
              },
            ),
            ListTile(
              leading: const Icon(Icons.verified_user),
              title: const Text('验证证书'),
              onTap: () {
                Navigator.pop(context);
                _verifyCertificate(cert);
              },
            ),
            ListTile(
              leading: const Icon(Icons.qr_code),
              title: const Text('显示二维码'),
              onTap: () {
                Navigator.pop(context);
                _showQrCode(cert);
              },
            ),
            ListTile(
              leading: const Icon(Icons.share),
              title: const Text('导出证书'),
              onTap: () {
                Navigator.pop(context);
                _exportCertificate(cert);
              },
            ),
            ListTile(
              leading: const Icon(Icons.copy),
              title: const Text('复制证书ID'),
              onTap: () {
                Navigator.pop(context);
                Clipboard.setData(ClipboardData(text: cert.certificateId));
                ScaffoldMessenger.of(context).showSnackBar(
                  const SnackBar(content: Text('证书ID已复制')),
                );
              },
            ),
            if (cert.certificateType != 'ROOT_CA')
              ListTile(
                leading: const Icon(Icons.delete, color: Colors.red),
                title: const Text('删除证书', style: TextStyle(color: Colors.red)),
                onTap: () {
                  Navigator.pop(context);
                  _deleteCertificate(cert);
                },
              ),
          ],
        ),
      ),
    );
  }

  Future<void> _verifyCertificate(QuantumSecureCertificate cert) async {
    showDialog(
      context: context,
      barrierDismissible: false,
      builder: (context) => const AlertDialog(
        content: Row(
          children: [
            CircularProgressIndicator(),
            SizedBox(width: 16),
            Text('正在验证证书...'),
          ],
        ),
      ),
    );

    try {
      final result = await _verificationService.verifyCertificate(cert);
      Navigator.pop(context);

      showDialog(
        context: context,
        builder: (context) => AlertDialog(
          title: Row(
            children: [
              Icon(
                result.isValid ? Icons.verified : Icons.error,
                color: result.isValid ? Colors.green : Colors.red,
              ),
              const SizedBox(width: 8),
              Text(result.isValid ? '验证成功' : '验证失败'),
            ],
          ),
          content: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Text('状态: ${result.isValid ? "有效" : "无效"}'),
              Text('受信任: ${result.isTrusted ? "是" : "否"}'),
              Text('吊销状态: ${result.isRevoked ? "已吊销" : "未吊销"}'),
              Text('证书链: ${result.hasValidChain ? "有效" : "无效"}'),
              if (result.chainLength > 0)
                Text('链长度: ${result.chainLength}'),
              if (result.errorMessage != null)
                Text('错误: ${result.errorMessage}', style: const TextStyle(color: Colors.red)),
              if (result.warnings.isNotEmpty) ...[
                const SizedBox(height: 8),
                const Text('警告:', style: TextStyle(fontWeight: FontWeight.bold)),
                ...result.warnings.map((w) => Text('• $w', style: const TextStyle(fontSize: 12))),
              ],
            ],
          ),
          actions: [
            TextButton(
              onPressed: () => Navigator.pop(context),
              child: const Text('确定'),
            ),
          ],
        ),
      );
    } catch (e) {
      Navigator.pop(context);
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('验证失败: $e')),
      );
    }
  }

  void _showQrCode(QuantumSecureCertificate cert) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('证书二维码'),
        content: SizedBox(
          width: 200,
          height: 200,
          child: Center(
            child: cert.qrCodeData != null
                ? Image.network(cert.qrCodeData!)
                : const Text('暂无二维码'),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('关闭'),
          ),
        ],
      ),
    );
  }

  Future<void> _importCertificate() async {
    // TODO: 实现证书导入
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('证书导入功能开发中...')),
    );
  }

  Future<void> _scanQrCode() async {
    final result = await Navigator.push<String>(
      context,
      MaterialPageRoute(
        builder: (context) => const CertificateQrScanner(),
      ),
    );

    if (result != null) {
      // 处理扫描结果
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text('扫描结果: $result')),
      );
    }
  }

  Future<void> _exportCertificate(QuantumSecureCertificate cert) async {
    // TODO: 实现证书导出
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('证书导出功能开发中...')),
    );
  }

  Future<void> _exportAllCertificates() async {
    // TODO: 实现批量导出
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('批量导出功能开发中...')),
    );
  }

  Future<void> _deleteCertificate(QuantumSecureCertificate cert) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('确认删除'),
        content: Text('确定要删除证书 "${cert.subjectName}" 吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('取消'),
          ),
          ElevatedButton(
            onPressed: () => Navigator.pop(context, true),
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            child: const Text('删除'),
          ),
        ],
      ),
    );

    if (confirmed == true) {
      setState(() {
        _certificates.removeWhere((c) => c.certificateId == cert.certificateId);
        _applyFilter();
      });
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('证书已删除')),
      );
    }
  }

  Future<void> _clearCache() async {
    await _verificationService.clearCache();
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('缓存已清除')),
    );
  }
}
