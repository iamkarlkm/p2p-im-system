import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../services/offline_translation_manager.dart';

class OfflineTranslationScreen extends StatelessWidget {
  const OfflineTranslationScreen({Key? key}) : super(key: key);
  
  @override
  Widget build(BuildContext context) {
    return ChangeNotifierProvider(
      create: (_) => OfflineTranslationManager(),
      child: const _OfflineTranslationScreenContent(),
    );
  }
}

class _OfflineTranslationScreenContent extends StatefulWidget {
  const _OfflineTranslationScreenContent({Key? key}) : super(key: key);
  
  @override
  State<_OfflineTranslationScreenContent> createState() => _OfflineTranslationScreenContentState();
}

class _OfflineTranslationScreenContentState extends State<_OfflineTranslationScreenContent> {
  @override
  void initState() {
    super.initState();
    Future.microtask(() {
      context.read<OfflineTranslationManager>().fetchAvailablePackages();
    });
  }
  
  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: const Text('离线翻译包'),
        elevation: 0,
      ),
      body: Consumer<OfflineTranslationManager>(
        builder: (context, manager, child) {
          if (manager.isLoading && manager.packages.isEmpty) {
            return const Center(child: CircularProgressIndicator());
          }
          
          return ListView(
            padding: const EdgeInsets.all(16),
            children: [
              _buildStorageCard(manager),
              const SizedBox(height: 16),
              if (manager.hasDownloadedPackages) ...[
                _buildSectionTitle('已下载'),
                const SizedBox(height: 8),
                ...manager.getDownloadedPackages().map((p) => _buildPackageCard(p, manager)),
                const SizedBox(height: 16),
              ],
              _buildSectionTitle('可下载'),
              const SizedBox(height: 8),
              ...manager.getAvailablePackages().map((p) => _buildPackageCard(p, manager)),
            ],
          );
        },
      ),
    );
  }
  
  Widget _buildStorageCard(OfflineTranslationManager manager) {
    return Card(
      child: Padding(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Row(
              children: [
                Icon(Icons.storage, color: Colors.blue[700]),
                const SizedBox(width: 8),
                const Text(
                  '存储空间',
                  style: TextStyle(fontWeight: FontWeight.bold),
                ),
              ],
            ),
            const SizedBox(height: 12),
            LinearProgressIndicator(
              value: manager.totalCacheSize / (200 * 1024 * 1024), // 假设200MB上限
              backgroundColor: Colors.grey[200],
              valueColor: AlwaysStoppedAnimation<Color>(Colors.blue[700]!),
            ),
            const SizedBox(height: 8),
            Text(
              '已使用 ${manager.formattedTotalCacheSize}',
              style: TextStyle(color: Colors.grey[600], fontSize: 12),
            ),
            if (manager.totalCacheSize > 0) ...[
              const SizedBox(height: 8),
              TextButton(
                onPressed: () => _showClearCacheDialog(manager),
                child: const Text('清理缓存', style: TextStyle(color: Colors.red)),
              ),
            ],
          ],
        ),
      ),
    );
  }
  
  Widget _buildSectionTitle(String title) {
    return Text(
      title,
      style: const TextStyle(
        fontSize: 16,
        fontWeight: FontWeight.bold,
        color: Colors.grey,
      ),
    );
  }
  
  Widget _buildPackageCard(OfflineTranslationPackage package, OfflineTranslationManager manager) {
    return Card(
      margin: const EdgeInsets.only(bottom: 8),
      child: ListTile(
        leading: CircleAvatar(
          backgroundColor: package.isDownloaded ? Colors.green : Colors.blue,
          child: Icon(
            package.isDownloaded ? Icons.check : Icons.download,
            color: Colors.white,
          ),
        ),
        title: Text(package.languageName),
        subtitle: Column(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Text('${package.formattedSize} · ${package.entryCount}条词条'),
            if (package.isDownloading)
              LinearProgressIndicator(value: package.downloadProgress),
          ],
        ),
        trailing: package.isDownloaded
            ? IconButton(
                icon: const Icon(Icons.delete, color: Colors.red),
                onPressed: () => _showDeleteDialog(package, manager),
              )
            : package.isDownloading
                ? const SizedBox(
                    width: 24,
                    height: 24,
                    child: CircularProgressIndicator(strokeWidth: 2),
                  )
                : ElevatedButton(
                    onPressed: () => manager.downloadPackage(package.id),
                    child: const Text('下载'),
                  ),
      ),
    );
  }
  
  void _showClearCacheDialog(OfflineTranslationManager manager) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: const Text('清理缓存'),
        content: const Text('确定要删除所有离线翻译包吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              manager.clearAllCache();
              Navigator.pop(context);
            },
            child: const Text('清理', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }
  
  void _showDeleteDialog(OfflineTranslationPackage package, OfflineTranslationManager manager) {
    showDialog(
      context: context,
      builder: (context) => AlertDialog(
        title: Text('删除 ${package.languageName} 翻译包'),
        content: Text('确定要删除 ${package.languageName} 离线翻译包吗？'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context),
            child: const Text('取消'),
          ),
          TextButton(
            onPressed: () {
              manager.deletePackage(package.id);
              Navigator.pop(context);
            },
            child: const Text('删除', style: TextStyle(color: Colors.red)),
          ),
        ],
      ),
    );
  }
}
