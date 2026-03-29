import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import '../models/quantum_certificate.dart';
import '../services/certificate_verification_service.dart';

/// 证书详情视图
/// 显示证书的完整信息和验证状态
class CertificateDetailView extends StatefulWidget {
  final QuantumSecureCertificate certificate;

  const CertificateDetailView({
    Key? key,
    required this.certificate,
  }) : super(key: key);

  @override
  State<CertificateDetailView> createState() => _CertificateDetailViewState();
}

class _CertificateDetailViewState extends State<CertificateDetailView> {
  final CertificateVerificationService _verificationService = 
      CertificateVerificationService();
  
  bool _isVerifying = false;
  CertificateVerificationResult? _verificationResult;

  @override
  void initState() {
    super.initState();
    _runVerification();
  }

  Future<void> _runVerification() async {
    setState(() {
      _isVerifying = true;
    });

    try {
      final result = await _verificationService.verifyCertificate(
        widget.certificate,
      );
      setState(() {
        _verificationResult = result;
      });
    } catch (e) {
      // 忽略错误
    } finally {
      setState(() {
        _isVerifying = false;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('证书详情'),
        actions: [
          IconButton(
            icon: const Icon(Icons.share),
            tooltip: '导出',
            onPressed: _exportCertificate,
          ),
          IconButton(
            icon: const Icon(Icons.copy),
            tooltip: '复制ID',
            onPressed: () {
              Clipboard.setData(
                ClipboardData(text: widget.certificate.certificateId),
              );
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('证书ID已复制')),
              );
            },
          ),
        ],
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            // 状态卡片
            _buildStatusCard(),
            
            const SizedBox(height: 16),
            
            // 基本信息
            _buildSection(
              title: '基本信息',
              icon: Icons.info_outline,
              children: [
                _buildInfoRow('证书ID', widget.certificate.certificateId),
                _buildInfoRow('序列号', widget.certificate.serialNumber),
                _buildInfoRow('类型', widget.certificate.certificateTypeDisplay),
                _buildInfoRow('状态', widget.certificate.statusDisplay),
              ],
            ),
            
            const SizedBox(height: 16),
            
            // 主体信息
            _buildSection(
              title: '证书主体',
              icon: Icons.person_outline,
              children: [
                _buildInfoRow('名称', widget.certificate.subjectName),
                _buildInfoRow('ID', widget.certificate.subjectId),
              ],
            ),
            
            const SizedBox(height: 16),
            
            // 颁发者信息
            _buildSection(
              title: '颁发者',
              icon: Icons.verified_user,
              children: [
                _buildInfoRow('名称', widget.certificate.issuerName),
                _buildInfoRow('ID', widget.certificate.issuerId),
              ],
            ),
            
            const SizedBox(height: 16),
            
            // 加密算法
            _buildSection(
              title: '加密算法',
              icon: Icons.security,
              children: [
                _buildInfoRow('算法类型', widget.certificate.algorithmType),
                _buildInfoRow('显示名称', widget.certificate.algorithmDisplayName),
                _buildInfoRow('密钥大小', '${widget.certificate.keySize * 8} 位'),
              ],
            ),
            
            const SizedBox(height: 16),
            
            // 有效期
            _buildSection(
              title: '有效期',
              icon: Icons.timer,
              children: [
                _buildInfoRow('生效时间', _formatDateTime(widget.certificate.validFrom)),
                _buildInfoRow('过期时间', _formatDateTime(widget.certificate.validUntil)),
                _buildInfoRow('剩余天数', '${widget.certificate.daysUntilExpiry} 天'),
                if (widget.certificate.isExpiringSoon)
                  _buildWarningRow('警告', '证书即将过期'),
              ],
            ),
            
            const SizedBox(height: 16),
            
            // 用途
            _buildSection(
              title: '密钥用途',
              icon: Icons.vpn_key,
              children: [
                _buildChipList(widget.certificate.keyUsage),
              ],
            ),
            
            const SizedBox(height: 16),
            
            // 扩展用途
            if (widget.certificate.extendedKeyUsage.isNotEmpty)
              _buildSection(
                title: '扩展密钥用途',
                icon: Icons.extension,
                children: [
                  _buildChipList(widget.certificate.extendedKeyUsage),
                ],
              ),
            
            if (widget.certificate.extendedKeyUsage.isNotEmpty)
              const SizedBox(height: 16),
            
            // 主题备用名称
            if (widget.certificate.sanDnsNames != null && 
                widget.certificate.sanDnsNames!.isNotEmpty)
              _buildSection(
                title: 'DNS名称',
                icon: Icons.dns,
                children: [
                  _buildChipList(widget.certificate.sanDnsNames!),
                ],
              ),
            
            if (widget.certificate.sanDnsNames != null && 
                widget.certificate.sanDnsNames!.isNotEmpty)
              const SizedBox(height: 16),
            
            // IP地址
            if (widget.certificate.sanIpAddresses != null && 
                widget.certificate.sanIpAddresses!.isNotEmpty)
              _buildSection(
                title: 'IP地址',
                icon: Icons.computer,
                children: [
                  _buildChipList(widget.certificate.sanIpAddresses!),
                ],
              ),
            
            if (widget.certificate.sanIpAddresses != null && 
                widget.certificate.sanIpAddresses!.isNotEmpty)
              const SizedBox(height: 16),
            
            // 吊销信息
            if (widget.certificate.isRevoked)
              _buildSection(
                title: '吊销信息',
                icon: Icons.block,
                isError: true,
                children: [
                  _buildInfoRow('吊销时间', 
                    widget.certificate.revokedAt != null 
                      ? _formatDateTime(widget.certificate.revokedAt!) 
                      : '未知'),
                  _buildInfoRow('吊销原因', 
                    widget.certificate.revocationReason ?? '未知'),
                ],
              ),
            
            if (widget.certificate.isRevoked)
              const SizedBox(height: 16),
            
            // 验证结果
            if (_verificationResult != null)
              _buildVerificationResultSection(),
            
            const SizedBox(height: 16),
            
            // 元数据
            if (widget.certificate.metadata != null)
              _buildSection(
                title: '元数据',
                icon: Icons.data_object,
                children: [
                  ...widget.certificate.metadata!.entries.map(
                    (e) => _buildInfoRow(e.key, e.value.toString()),
                  ),
                ],
              ),
            
            const SizedBox(height: 32),
            
            // 操作按钮
            _buildActionButtons(),
            
            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }

  Widget _buildStatusCard() {
    final statusColor = _getStatusColor();
    final statusIcon = _getStatusIcon();
    final statusText = _getStatusText();

    return Card(
      elevation: 4,
      color: statusColor.withOpacity(0.1),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          children: [
            Row(
              children: [
                Container(
                  padding: const EdgeInsets.all(12),
                  decoration: BoxDecoration(
                    color: statusColor.withOpacity(0.2),
                    shape: BoxShape.circle,
                  ),
                  child: Icon(statusIcon, color: statusColor, size: 32),
                ),
                const SizedBox(width: 16),
                Expanded(
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: [
                      Text(
                        statusText,
                        style: TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.bold,
                          color: statusColor,
                        ),
                      ),
                      const SizedBox(height: 4),
                      Text(
                        widget.certificate.subjectName,
                        style: const TextStyle(fontSize: 14),
                      ),
                    ],
                  ),
                ),
              ],
            ),
            if (_isVerifying) ...[
              const SizedBox(height: 12),
              const LinearProgressIndicator(),
              const SizedBox(height: 8),
              const Text('正在验证证书...', style: TextStyle(fontSize: 12)),
            ],
          ],
        ),
      ),
    );
  }

  Color _getStatusColor() {
    if (widget.certificate.isRevoked) return Colors.red;
    if (widget.certificate.isExpired) return Colors.grey;
    if (widget.certificate.isExpiringSoon) return Colors.orange;
    return Colors.green;
  }

  IconData _getStatusIcon() {
    if (widget.certificate.isRevoked) return Icons.block;
    if (widget.certificate.isExpired) return Icons.error_outline;
    if (widget.certificate.isExpiringSoon) return Icons.timer;
    return Icons.verified;
  }

  String _getStatusText() {
    if (widget.certificate.isRevoked) return '已吊销';
    if (widget.certificate.isExpired) return '已过期';
    if (widget.certificate.isExpiringSoon) return '即将过期';
    return '证书有效';
  }

  Widget _buildSection({
    required String title,
    required IconData icon,
    required List<Widget> children,
    bool isError = false,
  }) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(icon, color: isError ? Colors.red : Colors.blue, size: 20),
                const SizedBox(width: 8),
                Text(
                  title,
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                    color: isError ? Colors.red : Colors.black87,
                  ),
                ),
              ],
            ),
            const Divider(height: 24),
            ...children,
          ],
        ),
      ),
    );
  }

  Widget _buildInfoRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 100,
            child: Text(
              label,
              style: TextStyle(
                fontSize: 14,
                color: Colors.grey[600],
              ),
            ),
          ),
          Expanded(
            child: SelectableText(
              value,
              style: const TextStyle(
                fontSize: 14,
                fontWeight: FontWeight.w500,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildWarningRow(String label, String value) {
    return Padding(
      padding: const EdgeInsets.symmetric(vertical: 4),
      child: Row(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          SizedBox(
            width: 100,
            child: Text(
              label,
              style: TextStyle(
                fontSize: 14,
                color: Colors.grey[600],
              ),
            ),
          ),
          Expanded(
            child: Text(
              value,
              style: const TextStyle(
                fontSize: 14,
                fontWeight: FontWeight.w500,
                color: Colors.orange,
              ),
            ),
          ),
        ],
      ),
    );
  }

  Widget _buildChipList(List<String> items) {
    return Wrap(
      spacing: 8,
      runSpacing: 8,
      children: items.map((item) {
        return Chip(
          label: Text(item),
          backgroundColor: Colors.blue.withOpacity(0.1),
          labelStyle: const TextStyle(fontSize: 12),
        );
      }).toList(),
    );
  }

  Widget _buildVerificationResultSection() {
    final result = _verificationResult!;
    
    return Card(
      color: result.isValid ? Colors.green.withOpacity(0.05) : Colors.red.withOpacity(0.05),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(
                  result.isValid ? Icons.verified : Icons.error,
                  color: result.isValid ? Colors.green : Colors.red,
                ),
                const SizedBox(width: 8),
                Text(
                  '验证结果',
                  style: TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.bold,
                    color: result.isValid ? Colors.green : Colors.red,
                  ),
                ),
              ],
            ),
            const Divider(height: 24),
            _buildInfoRow('验证状态', result.isValid ? '通过' : '失败'),
            _buildInfoRow('受信任', result.isTrusted ? '是' : '否'),
            _buildInfoRow('证书链', result.hasValidChain ? '有效' : '无效'),
            _buildInfoRow('链长度', '${result.chainLength}'),
            if (result.rootCaSubject != null)
              _buildInfoRow('根CA', result.rootCaSubject!),
            if (result.errorMessage != null)
              _buildInfoRow('错误', result.errorMessage!),
            if (result.warnings.isNotEmpty) ...[
              const SizedBox(height: 8),
              const Text('警告:', style: TextStyle(fontWeight: FontWeight.bold)),
              ...result.warnings.map((w) => Padding(
                padding: const EdgeInsets.only(left: 8, top: 4),
                child: Text('• $w', style: const TextStyle(fontSize: 12)),
              )),
            ],
          ],
        ),
      ),
    );
  }

  Widget _buildActionButtons() {
    return Row(
      children: [
        Expanded(
          child: ElevatedButton.icon(
            onPressed: _isVerifying ? null : _runVerification,
            icon: _isVerifying 
                ? const SizedBox(
                    width: 16,
                    height: 16,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  )
                : const Icon(Icons.verified_user),
            label: Text(_isVerifying ? '验证中...' : '重新验证'),
          ),
        ),
        const SizedBox(width: 12),
        Expanded(
          child: OutlinedButton.icon(
            onPressed: _exportCertificate,
            icon: const Icon(Icons.download),
            label: const Text('导出PEM'),
          ),
        ),
      ],
    );
  }

  String _formatDateTime(DateTime date) {
    return '${date.year}-${date.month.toString().padLeft(2, '0')}-${date.day.toString().padLeft(2, '0')} '
        '${date.hour.toString().padLeft(2, '0')}:${date.minute.toString().padLeft(2, '0')}';
  }

  void _exportCertificate() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('证书PEM'),
        content: SizedBox(
          width: double.maxFinite,
          child: SingleChildScrollView(
            child: SelectableText(
              widget.certificate.certificatePem,
              style: const TextStyle(fontFamily: 'monospace', fontSize: 10),
            ),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () {
              Clipboard.setData(
                ClipboardData(text: widget.certificate.certificatePem),
              );
              Navigator.pop(context);
              ScaffoldMessenger.of(context).showSnackBar(
                const SnackBar(content: Text('PEM已复制到剪贴板')),
              );
            },
            child: const Text('复制'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('关闭'),
          ),
        ],
      ),
    );
  }
}
