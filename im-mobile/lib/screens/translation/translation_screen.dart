import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/translation_service.dart';
import '../models/translation_model.dart';
import '../widgets/language_selector.dart';
import '../widgets/translation_result_card.dart';
import 'translation_history_screen.dart';

class TranslationScreen extends StatefulWidget {
  const TranslationScreen({Key? key}) : super(key: key);
  
  @override
  State<TranslationScreen> createState() => _TranslationScreenState();
}

class _TranslationScreenState extends State<TranslationScreen> {
  final TextEditingController _textController = TextEditingController();
  final ScrollController _scrollController = ScrollController();
  bool _showResult = false;
  TranslationResult? _lastResult;
  
  @override
  void dispose() {
    _textController.dispose();
    _scrollController.dispose();
    super.dispose();
  }
  
  Future<void> _performTranslation() async {
    final text = _textController.text.trim();
    if (text.isEmpty) {
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('请输入要翻译的文本')),
      );
      return;
    }
    
    final service = context.read<TranslationService>();
    final result = await service.translate(text);
    
    setState(() {
      _lastResult = result;
      _showResult = true;
    });
    
    if (!result.success && mounted) {
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(content: Text(result.error ?? '翻译失败'), backgroundColor: Colors.red),
      );
    }
  }
  
  void _clearText() {
    _textController.clear();
    setState(() {
      _showResult = false;
      _lastResult = null;
    });
  }
  
  void _copyResult() {
    if (_lastResult?.success ?? false) {
      final data = ClipboardData(text: _lastResult!.translatedText);
      Clipboard.setData(data);
      ScaffoldMessenger.of(context).showSnackBar(
        const SnackBar(content: Text('已复制到剪贴板')),
      );
    }
  }
  
  void _speakResult() {
    if (_lastResult?.success ?? false) {
    }
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('实时翻译'),
        elevation: 0,
        actions: [
          IconButton(
            icon: const Icon(Icons.history),
            onPressed: () => Navigator.push(
              context,
              MaterialPageRoute(builder: (_) => const TranslationHistoryScreen()),
            ),
          ),
          IconButton(
            icon: const Icon(Icons.settings),
            onPressed: () => _showSettingsDialog(),
          ),
        ],
      ),
      body: Consumer<TranslationService>(
        builder: (context, service, child) {
          return Column(
            children: [
              LanguageSelectorBar(
                sourceLanguage: service.sourceLanguage,
                targetLanguage: service.targetLanguage,
                onSourceChanged: service.setSourceLanguage,
                onTargetChanged: service.setTargetLanguage,
                onSwap: service.swapLanguages,
              ),
              Expanded(
                child: SingleChildScrollView(
                  controller: _scrollController,
                  padding: const EdgeInsets.all(16),
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.stretch,
                    children: [
                      _buildInputCard(service),
                      const SizedBox(height: 16),
                      if (_showResult && _lastResult != null)
                        TranslationResultCard(
                          result: _lastResult!,
                          onCopy: _copyResult,
                          onSpeak: _speakResult,
                          isOffline: _lastResult!.isOffline,
                        ),
                      if (service.history.isNotEmpty && !_showResult)
                        _buildRecentHistory(service),
                    ],
                  ),
                ),
              ),
              _buildBottomBar(service),
            ],
          );
        },
      ),
    );
  }
  
  Widget _buildInputCard(TranslationService service) {
    return Card(
      elevation: 2,
      shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              mainAxisAlignment: MainAxisAlignment.spaceBetween,
              children: [
                Text(
                  '输入文本',
                  style: Theme.of(context).textTheme.titleSmall?.copyWith(
                    color: Colors.grey[600],
                  ),
                ),
                if (_textController.text.isNotEmpty)
                  TextButton.icon(
                    onPressed: _clearText,
                    icon: const Icon(Icons.clear, size: 16),
                    label: const Text('清空'),
                  ),
              ],
            ),
            const SizedBox(height: 8),
            TextField(
              controller: _textController,
              maxLines: 5,
              maxLength: 5000,
              decoration: InputDecoration(
                hintText: '请输入要翻译的文本...',
                border: OutlineInputBorder(
                  borderRadius: BorderRadius.circular(8),
                  borderSide: BorderSide.none,
                ),
                filled: true,
                fillColor: Colors.grey[100],
                contentPadding: const EdgeInsets.all(12),
              ),
              onChanged: (_) => setState(() {}),
            ),
            const SizedBox(height: 8),
            Row(
              mainAxisAlignment: MainAxisAlignment.end,
              children: [
                Text(
                  '${_textController.text.length}/5000',
                  style: TextStyle(color: Colors.grey[500], fontSize: 12),
                ),
              ],
            ),
          ],
        ),
      ),
    );
  }
  
  Widget _buildRecentHistory(TranslationService service) {
    final recent = service.history.take(3).toList();
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            Text(
              '最近翻译',
              style: Theme.of(context).textTheme.titleMedium,
            ),
            TextButton(
              onPressed: () => Navigator.push(
                context,
                MaterialPageRoute(builder: (_) => const TranslationHistoryScreen()),
              ),
              child: const Text('查看全部'),
            ),
          ],
        ),
        const SizedBox(height: 8),
        ...recent.map((record) => _buildHistoryItem(record, service)),
      ],
    );
  }
  
  Widget _buildHistoryItem(TranslationRecord record, TranslationService service) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        title: Text(
          record.sourceText,
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
        ),
        subtitle: Text(
          record.translatedText,
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
          style: TextStyle(color: Colors.blue[700]),
        ),
        trailing: Text(
          record.displayTimestamp,
          style: TextStyle(color: Colors.grey[500], fontSize: 12),
        ),
        onTap: () {
          _textController.text = record.sourceText;
          setState(() {
            _lastResult = TranslationResult(
              success: true,
              translatedText: record.translatedText,
              detectedSourceLanguage: record.sourceLanguage,
              isOffline: record.isOffline,
            );
            _showResult = true;
          });
        },
      ),
    );
  }
  
  Widget _buildBottomBar(TranslationService service) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 10,
            offset: const Offset(0, -5),
          ),
        ],
      ),
      child: SafeArea(
        child: SizedBox(
          width: double.infinity,
          height: 48,
          child: ElevatedButton.icon(
            onPressed: service.isLoading ? null : _performTranslation,
            icon: service.isLoading
                ? const SizedBox(
                    width: 20,
                    height: 20,
                    child: CircularProgressIndicator(strokeWidth: 2, color: Colors.white),
                  )
                : const Icon(Icons.translate),
            label: Text(service.isLoading ? '翻译中...' : '翻译'),
            style: ElevatedButton.styleFrom(
              shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(8)),
            ),
          ),
        ),
      ),
    );
  }
  
  void _showSettingsDialog() {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('翻译设置'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            const Text('离线翻译'),
            SwitchListTile(
              title: const Text('启用离线翻译'),
              subtitle: const Text('无网络时使用本地缓存'),
              value: true,
              onChanged: (value) {},
            ),
            const Divider(),
            const Text('自动翻译'),
            SwitchListTile(
              title: const Text('输入时自动翻译'),
              subtitle: const Text('停止输入后自动翻译'),
              value: false,
              onChanged: (value) {},
            ),
          ],
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
}
