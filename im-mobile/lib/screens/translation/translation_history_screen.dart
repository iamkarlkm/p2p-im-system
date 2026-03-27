import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/translation_service.dart';
import '../models/translation_model.dart';
import 'translation_screen.dart';

class TranslationHistoryScreen extends StatefulWidget {
  const TranslationHistoryScreen({Key? key}) : super(key: key);
  
  @override
  State<TranslationHistoryScreen> createState() => _TranslationHistoryScreenState();
}

class _TranslationHistoryScreenState extends State<TranslationHistoryScreen> {
  String _searchQuery = '';
  bool _showFavoritesOnly = false;
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('翻译历史'),
        elevation: 0,
        actions: [
          IconButton(
            icon: Icon(_showFavoritesOnly ? Icons.favorite : Icons.favorite_border),
            onPressed: () {
              setState(() {
                _showFavoritesOnly = !_showFavoritesOnly;
              });
            },
          ),
          PopupMenuButton<String>(
            onSelected: _handleMenuAction,
            itemBuilder: (context) => [
              const PopupMenuItem(
                value: 'clear',
                child: Row(
                  children: [
                    Icon(Icons.delete_outline, size: 20),
                    SizedBox(width: 8),
                    Text('清空历史'),
                  ],
                ),
              ),
              const PopupMenuItem(
                value: 'export',
                child: Row(
                  children: [
                    Icon(Icons.download, size: 20),
                    SizedBox(width: 8),
                    Text('导出历史'),
                  ],
                ),
              ),
            ],
          ),
        ],
      ),
      body: Column(
        children: [
          _buildSearchBar(),
          Expanded(
            child: Consumer<TranslationService>(
              builder: (context, service, child) {
                final records = _getFilteredRecords(service);
                
                if (records.isEmpty) {
                  return _buildEmptyState();
                }
                
                return ListView.builder(
                  padding: const EdgeInsets.all(16),
                  itemCount: records.length,
                  itemBuilder: (context, index) {
                    final record = records[index];
                    return _buildHistoryCard(record, service);
                  },
                );
              },
            ),
          ),
        ],
      ),
    );
  }
  
  Widget _buildSearchBar() {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.black.withOpacity(0.05),
            blurRadius: 4,
            offset: const Offset(0, 2),
          ),
        ],
      ),
      child: TextField(
        decoration: InputDecoration(
          hintText: '搜索翻译历史...',
          prefixIcon: const Icon(Icons.search),
          suffixIcon: _searchQuery.isNotEmpty
              ? IconButton(
                  icon: const Icon(Icons.clear),
                  onPressed: () {
                    setState(() {
                      _searchQuery = '';
                    });
                  },
                )
              : null,
          border: OutlineInputBorder(
            borderRadius: BorderRadius.circular(8),
            borderSide: BorderSide.none,
          ),
          filled: true,
          fillColor: Colors.grey[100],
          contentPadding: const EdgeInsets.symmetric(horizontal: 16, vertical: 12),
        ),
        onChanged: (value) {
          setState(() {
            _searchQuery = value;
          });
        },
      ),
    );
  }
  
  List<TranslationRecord> _getFilteredRecords(TranslationService service) {
    List<TranslationRecord> records = _showFavoritesOnly
        ? service.getFavoriteRecords()
        : service.history;
    
    if (_searchQuery.isNotEmpty) {
      final query = _searchQuery.toLowerCase();
      records = records.where((r) {
        return r.sourceText.toLowerCase().contains(query) ||
               r.translatedText.toLowerCase().contains(query);
      }).toList();
    }
    
    return records;
  }
  
  Widget _buildEmptyState() {
    return Center(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          Icon(
            _showFavoritesOnly ? Icons.favorite_border : Icons.history,
            size: 64,
            color: Colors.grey[400],
          ),
          const SizedBox(height: 16),
          Text(
            _showFavoritesOnly ? '暂无收藏' : '暂无翻译历史',
            style: TextStyle(
              fontSize: 16,
              color: Colors.grey[600],
            ),
          ),
          const SizedBox(height: 24),
          ElevatedButton.icon(
            onPressed: () => Navigator.pushReplacement(
              context,
              MaterialPageRoute(builder: (_) => const TranslationScreen()),
            ),
            icon: const Icon(Icons.translate),
            label: const Text('去翻译'),
          ),
        ],
      ),
    );
  }
  
  Widget _buildHistoryCard(TranslationRecord record, TranslationService service) {
    return Dismissible(
      key: Key(record.id),
      direction: DismissDirection.endToStart,
      background: Container(
        alignment: Alignment.centerRight,
        padding: const EdgeInsets.only(right: 16),
        decoration: BoxDecoration(
          color: Colors.red,
          borderRadius: BorderRadius.circular(12),
        ),
        child: const Icon(Icons.delete, color: Colors.white),
      ),
      onDismissed: (_) => service.deleteRecord(record.id),
      child: Card(
        margin: const EdgeInsets.only(bottom: 12),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
        child: InkWell(
          onTap: () => _showRecordDetail(record, service),
          borderRadius: BorderRadius.circular(12),
          child: Padding(
            padding: const EdgeInsets.all(16),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    _buildLanguageTag(record.sourceLanguageName),
                    const Padding(
                      padding: EdgeInsets.symmetric(horizontal: 8),
                      child: Icon(Icons.arrow_forward, size: 16, color: Colors.grey),
                    ),
                    _buildLanguageTag(record.targetLanguageName),
                    const Spacer(),
                    if (record.isOffline)
                      Container(
                        padding: const EdgeInsets.symmetric(horizontal: 6, vertical: 2),
                        decoration: BoxDecoration(
                          color: Colors.orange.withOpacity(0.2),
                          borderRadius: BorderRadius.circular(4),
                        ),
                        child: const Text(
                          '离线',
                          style: TextStyle(fontSize: 10, color: Colors.orange),
                        ),
                      ),
                    IconButton(
                      icon: Icon(
                        record.isFavorite ? Icons.favorite : Icons.favorite_border,
                        color: record.isFavorite ? Colors.red : Colors.grey,
                      ),
                      onPressed: () => _toggleFavorite(record, service),
                      padding: EdgeInsets.zero,
                      constraints: const BoxConstraints(),
                      iconSize: 20,
                    ),
                  ],
                ),
                const SizedBox(height: 12),
                Text(
                  record.sourceText,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                  style: const TextStyle(fontSize: 15),
                ),
                const SizedBox(height: 8),
                Text(
                  record.translatedText,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                  style: TextStyle(
                    fontSize: 15,
                    color: Colors.blue[700],
                    fontWeight: FontWeight.w500,
                  ),
                ),
                const SizedBox(height: 8),
                Row(
                  mainAxisAlignment: MainAxisAlignment.spaceBetween,
                  children: [
                    Text(
                      record.displayTimestamp,
                      style: TextStyle(
                        fontSize: 12,
                        color: Colors.grey[500],
                      ),
                    ),
                    Row(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        IconButton(
                          icon: const Icon(Icons.copy, size: 18),
                          onPressed: () => _copyToClipboard(record.translatedText),
                          padding: EdgeInsets.zero,
                          constraints: const BoxConstraints(),
                        ),
                        const SizedBox(width: 16),
                        IconButton(
                          icon: const Icon(Icons.share, size: 18),
                          onPressed: () => _shareRecord(record),
                          padding: EdgeInsets.zero,
                          constraints: const BoxConstraints(),
                        ),
                      ],
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
  
  Widget _buildLanguageTag(String text) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 8, vertical: 4),
      decoration: BoxDecoration(
        color: Colors.grey[200],
        borderRadius: BorderRadius.circular(4),
      ),
      child: Text(
        text,
        style: TextStyle(
          fontSize: 12,
          color: Colors.grey[700],
          fontWeight: FontWeight.w500,
        ),
      ),
    );
  }
  
  void _showRecordDetail(TranslationRecord record, TranslationService service) {
    showModalBottomSheet(
      context: context,
      isScrollControlled: true,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.vertical(top: Radius.circular(16)),
      ),
      builder: (context) => DraggableScrollableSheet(
        initialChildSize: 0.6,
        minChildSize: 0.4,
        maxChildSize: 0.8,
        expand: false,
        builder: (context, scrollController) => SingleChildScrollView(
          controller: scrollController,
          padding: const EdgeInsets.all(20),
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              Center(
                child: Container(
                  width: 40,
                  height: 4,
                  decoration: BoxDecoration(
                    color: Colors.grey[300],
                    borderRadius: BorderRadius.circular(2),
                  ),
                ),
              ),
              const SizedBox(height: 20),
              Row(
                children: [
                  _buildLanguageTag(record.sourceLanguageName),
                  const Padding(
                    padding: EdgeInsets.symmetric(horizontal: 12),
                    child: Icon(Icons.arrow_forward),
                  ),
                  _buildLanguageTag(record.targetLanguageName),
                ],
              ),
              const SizedBox(height: 20),
              const Text(
                '原文',
                style: TextStyle(
                  fontWeight: FontWeight.bold,
                  color: Colors.grey,
                ),
              ),
              const SizedBox(height: 8),
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.grey[100],
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Text(record.sourceText),
              ),
              const SizedBox(height: 20),
              const Text(
                '译文',
                style: TextStyle(
                  fontWeight: FontWeight.bold,
                  color: Colors.grey,
                ),
              ),
              const SizedBox(height: 8),
              Container(
                width: double.infinity,
                padding: const EdgeInsets.all(16),
                decoration: BoxDecoration(
                  color: Colors.blue.withOpacity(0.1),
                  borderRadius: BorderRadius.circular(8),
                ),
                child: Text(
                  record.translatedText,
                  style: TextStyle(
                    color: Colors.blue[800],
                    fontWeight: FontWeight.w500,
                  ),
                ),
              ),
              const SizedBox(height: 20),
              Text(
                '翻译时间: ${record.timestamp.toString()}',
                style: TextStyle(
                  fontSize: 12,
                  color: Colors.grey[500],
                ),
              ),
              if (record.isOffline)
                Text(
                  '翻译方式: 离线翻译',
                  style: TextStyle(
                    fontSize: 12,
                    color: Colors.orange[700],
                  ),
                ),
              const SizedBox(height: 24),
              Row(
                children: [
                  Expanded(
                    child: ElevatedButton.icon(
                      onPressed: () => _copyToClipboard(record.translatedText),
                      icon: const Icon(Icons.copy),
                      label: const Text('复制译文'),
                    ),
                  ),
                  const SizedBox(width: 12),
                  Expanded(
                    child: OutlinedButton.icon(
                      onPressed: () => _shareRecord(record),
                      icon: const Icon(Icons.share),
                      label: const Text('分享'),
                    ),
                  ),
                ],
              ),
              const SizedBox(height: 12),
              TextButton.icon(
                onPressed: () {
                  service.deleteRecord(record.id);
                  Navigator.pop(context);
                },
                icon: const Icon(Icons.delete, color: Colors.red),
                label: const Text('删除记录', style: TextStyle(color: Colors.red)),
              ),
            ],
          ),
        ),
      ),
    );
  }
  
  void _toggleFavorite(TranslationRecord record, TranslationService service) {
    if (record.isFavorite) {
      service.addToFavorites(record.id);
    } else {
      service.addToFavorites(record.id);
    }
    ScaffoldMessenger.of(context).showSnackBar(
      SnackBar(
        content: Text(record.isFavorite ? '已取消收藏' : '已添加到收藏'),
        duration: const Duration(seconds: 1),
      ),
    );
  }
  
  void _copyToClipboard(String text) {
    final data = ClipboardData(text: text);
    Clipboard.setData(data);
    ScaffoldMessenger.of(context).showSnackBar(
      const SnackBar(content: Text('已复制到剪贴板')),
    );
  }
  
  void _shareRecord(TranslationRecord record) {
  }
  
  void _handleMenuAction(String value) async {
    final service = context.read<TranslationService>();
    
    switch (value) {
      case 'clear':
        final confirmed = await showDialog<bool>(
          context: context,
          builder: (context) => AlertDialog(
            title: const Text('清空历史'),
            content: const Text('确定要清空所有翻译历史吗？此操作不可撤销。'),
            actions: [
              TextButton(
                onPressed: () => Navigator.pop(context, false),
                child: const Text('取消'),
              ),
              TextButton(
                onPressed: () => Navigator.pop(context, true),
                child: const Text('清空', style: TextStyle(color: Colors.red)),
              ),
            ],
          ),
        );
        
        if (confirmed == true) {
          await service.clearHistory();
          if (mounted) {
            ScaffoldMessenger.of(context).showSnackBar(
              const SnackBar(content: Text('历史已清空')),
            );
          }
        }
        break;
      case 'export':
        break;
    }
  }
}
