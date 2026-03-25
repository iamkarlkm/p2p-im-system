import 'dart:convert';
import 'dart:typed_data';
import 'package:flutter/material.dart';
import '../models/two_factor_auth.dart';
import '../services/two_factor_auth_service.dart';

class TwoFactorAuthScreen extends StatefulWidget {
  final int userId;

  TwoFactorAuthScreen({required this.userId});

  @override
  State<TwoFactorAuthScreen> createState() => _TwoFactorAuthScreenState();
}

class _TwoFactorAuthScreenState extends State<TwoFactorAuthScreen> {
  final _service = TwoFactorAuthService();
  TwoFactorSetupResponse? _setupData;
  bool _isLoading = false;
  String? _error;
  String _step = 'idle';
  final _codeController = TextEditingController();
  bool _isEnabled = false;
  int _backupCodesRemaining = 0;

  @override
  void initState() {
    super.initState();
    _fetchStatus();
  }

  @override
  void dispose() {
    _codeController.dispose();
    super.dispose();
  }

  Future<void> _fetchStatus() async {
    setState(() => _isLoading = true);
    try {
      final status = await _service.getStatus(widget.userId);
      if (status != null) {
        setState(() {
          _isEnabled = status.enabled;
          _backupCodesRemaining = status.backupCodesRemaining;
        });
      }
    } catch (e) {
      _error = e.toString();
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _startSetup() async {
    setState(() => _isLoading = true);
    _error = null;
    try {
      final data = await _service.setup(widget.userId);
      setState(() {
        _setupData = data;
        _step = 'setup';
      });
    } catch (e) {
      setState(() => _error = e.toString());
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _confirmEnable() async {
    final code = _codeController.text.trim();
    if (code.isEmpty || code.length < 6) return;

    setState(() => _isLoading = true);
    try {
      final success = await _service.enable(widget.userId, code);
      if (success) {
        setState(() {
          _isEnabled = true;
          _step = 'complete';
          _fetchStatus();
        });
      } else {
        setState(() => _error = '验证码错误');
      }
    } catch (e) {
      setState(() => _error = e.toString());
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Future<void> _disable() async {
    final code = _codeController.text.trim();
    if (code.isEmpty) return;

    setState(() => _isLoading = true);
    try {
      final success = await _service.disable(widget.userId, code);
      if (success) {
        setState(() {
          _isEnabled = false;
          _step = 'idle';
          _setupData = null;
          _codeController.clear();
        });
        _fetchStatus();
      } else {
        setState(() => _error = '验证码错误');
      }
    } catch (e) {
      setState(() => _error = e.toString());
    } finally {
      setState(() => _isLoading = false);
    }
  }

  Widget _buildQrImage(String base64Data) {
    try {
      final Uint8List bytes = base64Decode(base64Data);
      return Image.memory(bytes, width: 180, height: 180);
    } catch (e) {
      return Container(
        width: 180,
        height: 180,
        color: Colors.grey[200],
        child: const Icon(Icons.qr_code, size: 100),
      );
    }
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: const Text('两步验证 (2FA)')),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            if (_step == 'idle') ...[
              const Text(
                '保护您的账户安全，启用两步验证。',
                style: TextStyle(fontSize: 16),
              ),
              const SizedBox(height: 16),
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: Colors.grey[100],
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Column(
                  children: [
                    Row(
                      mainAxisAlignment: MainAxisAlignment.spaceBetween,
                      children: [
                        const Text('状态:'),
                        Text(
                          _isEnabled ? '已启用' : '未启用',
                          style: TextStyle(
                            fontWeight: FontWeight.bold,
                            color: _isEnabled ? Colors.green : Colors.red,
                          ),
                        ),
                      ],
                    ),
                    if (_isEnabled) ...[
                      const SizedBox(height: 8),
                      Row(
                        mainAxisAlignment: MainAxisAlignment.spaceBetween,
                        children: [
                          const Text('备用码剩余:'),
                          Text('$_backupCodesRemaining 个'),
                        ],
                      ),
                    ],
                  ],
                ),
              ),
              const SizedBox(height: 20),
              ElevatedButton(
                onPressed: _isLoading ? null : _startSetup,
                style: ElevatedButton.styleFrom(backgroundColor: Colors.blue),
                child: _isLoading
                    ? const SizedBox(
                        width: 20,
                        height: 20,
                        child: CircularProgressIndicator(strokeWidth: 2),
                      )
                    : const Text('启用两步验证'),
              ),
              if (_isEnabled) ...[
                const SizedBox(height: 12),
                OutlinedButton(
                  onPressed: () => _showDisableDialog(),
                  style: OutlinedButton.styleFrom(foregroundColor: Colors.red),
                  child: const Text('关闭两步验证'),
                ),
              ],
            ],
            if (_step == 'setup' && _setupData != null) ...[
              const Text('扫描下方二维码，或手动输入密钥：'),
              const SizedBox(height: 12),
              Center(child: _buildQrImage(_setupData!.qrCodeBase64)),
              const SizedBox(height: 12),
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: Colors.grey[100],
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('手动密钥:',
                        style: TextStyle(fontWeight: FontWeight.bold)),
                    const SizedBox(height: 4),
                    Text(_setupData!.manualEntryKey,
                        style: const TextStyle(fontFamily: 'monospace')),
                  ],
                ),
              ),
              const SizedBox(height: 12),
              Container(
                padding: const EdgeInsets.all(12),
                decoration: BoxDecoration(
                  color: Colors.amber[50],
                  borderRadius: BorderRadius.circular(8),
                  border: Border.all(color: Colors.amber),
                ),
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    const Text('备用码 (请妥善保存):',
                        style: TextStyle(fontWeight: FontWeight.bold)),
                    const SizedBox(height: 8),
                    Wrap(
                      spacing: 8,
                      runSpacing: 8,
                      children: _setupData!.backupCodes
                          .map((code) => Chip(
                                label: Text(code,
                                    style: const TextStyle(
                                        fontFamily: 'monospace', fontSize: 12)),
                                backgroundColor: Colors.amber[100],
                              ))
                          .toList(),
                    ),
                  ],
                ),
              ),
              const SizedBox(height: 16),
              TextField(
                controller: _codeController,
                keyboardType: TextInputType.number,
                maxLength: 6,
                decoration: const InputDecoration(
                  labelText: '输入6位验证码确认启用',
                  border: OutlineInputBorder(),
                ),
              ),
              const SizedBox(height: 12),
              ElevatedButton(
                onPressed: _isLoading ? null : _confirmEnable,
                style: ElevatedButton.styleFrom(backgroundColor: Colors.blue),
                child: _isLoading
                    ? const CircularProgressIndicator(strokeWidth: 2)
                    : const Text('确认启用'),
              ),
              const SizedBox(height: 8),
              TextButton(
                onPressed: () => setState(() => _step = 'idle'),
                child: const Text('取消'),
              ),
            ],
            if (_step == 'complete') ...[
              const Icon(Icons.check_circle, color: Colors.green, size: 80),
              const SizedBox(height: 16),
              const Text('两步验证已成功启用！',
                  style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
                  textAlign: TextAlign.center),
              const SizedBox(height: 16),
              ElevatedButton(
                onPressed: () => setState(() => _step = 'idle'),
                child: const Text('完成'),
              ),
            ],
            if (_error != null) ...[
              const SizedBox(height: 12),
              Container(
                padding: const EdgeInsets.all(8),
                decoration: BoxDecoration(
                  color: Colors.red[50],
                  borderRadius: BorderRadius.circular(4),
                ),
                child: Text(_error!, style: const TextStyle(color: Colors.red)),
              ),
            ],
          ],
        ),
      ),
    );
  }

  void _showDisableDialog() {
    _codeController.clear();
    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: const Text('关闭两步验证'),
        content: TextField(
          controller: _codeController,
          decoration: const InputDecoration(
            labelText: '输入验证码或备用码',
            border: OutlineInputBorder(),
          ),
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: const Text('取消'),
          ),
          ElevatedButton(
            onPressed: () {
              Navigator.pop(ctx);
              _disable();
            },
            style: ElevatedButton.styleFrom(backgroundColor: Colors.red),
            child: const Text('确认关闭'),
          ),
        ],
      ),
    );
  }
}
