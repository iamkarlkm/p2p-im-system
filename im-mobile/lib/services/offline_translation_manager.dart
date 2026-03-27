import 'dart:convert';
import 'dart:io';
import 'package:flutter/foundation.dart';
import 'package:http/http.dart' as http;
import 'package:path_provider/path_provider.dart';
import 'package:shared_preferences/shared_preferences.dart';

class OfflineTranslationPackage {
  final String id;
  final String languageCode;
  final String languageName;
  final String version;
  final int size; // bytes
  final int entryCount;
  final DateTime updatedAt;
  final String downloadUrl;
  bool isDownloaded;
  bool isDownloading;
  double downloadProgress;
  
  OfflineTranslationPackage({
    required this.id,
    required this.languageCode,
    required this.languageName,
    required this.version,
    required this.size,
    required this.entryCount,
    required this.updatedAt,
    required this.downloadUrl,
    this.isDownloaded = false,
    this.isDownloading = false,
    this.downloadProgress = 0,
  });
  
  factory OfflineTranslationPackage.fromJson(Map<String, dynamic> json) {
    return OfflineTranslationPackage(
      id: json['id'] ?? '',
      languageCode: json['languageCode'] ?? '',
      languageName: json['languageName'] ?? '',
      version: json['version'] ?? '1.0.0',
      size: json['size'] ?? 0,
      entryCount: json['entryCount'] ?? 0,
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'])
          : DateTime.now(),
      downloadUrl: json['downloadUrl'] ?? '',
      isDownloaded: json['isDownloaded'] ?? false,
    );
  }
  
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'languageCode': languageCode,
      'languageName': languageName,
      'version': version,
      'size': size,
      'entryCount': entryCount,
      'updatedAt': updatedAt.toIso8601String(),
      'downloadUrl': downloadUrl,
      'isDownloaded': isDownloaded,
    };
  }
  
  String get formattedSize {
    if (size < 1024) return '$size B';
    if (size < 1024 * 1024) return '${(size / 1024).toStringAsFixed(1)} KB';
    return '${(size / (1024 * 1024)).toStringAsFixed(1)} MB';
  }
  
  OfflineTranslationPackage copyWith({
    bool? isDownloaded,
    bool? isDownloading,
    double? downloadProgress,
  }) {
    return OfflineTranslationPackage(
      id: id,
      languageCode: languageCode,
      languageName: languageName,
      version: version,
      size: size,
      entryCount: entryCount,
      updatedAt: updatedAt,
      downloadUrl: downloadUrl,
      isDownloaded: isDownloaded ?? this.isDownloaded,
      isDownloading: isDownloading ?? this.isDownloading,
      downloadProgress: downloadProgress ?? this.downloadProgress,
    );
  }
}

class OfflineTranslationManager extends ChangeNotifier {
  static const String baseUrl = 'https://api.im-system.com';
  
  final List<OfflineTranslationPackage> _packages = [];
  bool _isLoading = false;
  String? _error;
  int _totalCacheSize = 0;
  
  List<OfflineTranslationPackage> get packages => List.unmodifiable(_packages);
  bool get isLoading => _isLoading;
  String? get error => _error;
  int get totalCacheSize => _totalCacheSize;
  
  String get formattedTotalCacheSize {
    if (_totalCacheSize < 1024) return '$_totalCacheSize B';
    if (_totalCacheSize < 1024 * 1024) {
      return '${(_totalCacheSize / 1024).toStringAsFixed(1)} KB';
    }
    return '${(_totalCacheSize / (1024 * 1024)).toStringAsFixed(1)} MB';
  }
  
  OfflineTranslationManager() {
    _loadPackages();
    _calculateCacheSize();
  }
  
  Future<void> _loadPackages() async {
    _isLoading = true;
    notifyListeners();
    
    try {
      final prefs = await SharedPreferences.getInstance();
      final packagesJson = prefs.getString('offline_translation_packages');
      
      if (packagesJson != null) {
        final List<dynamic> data = jsonDecode(packagesJson);
        _packages.clear();
        for (final item in data) {
          _packages.add(OfflineTranslationPackage.fromJson(item));
        }
      } else {
        _packages.addAll(_getDefaultPackages());
      }
      
      await _checkDownloadedStatus();
    } catch (e) {
      _error = '加载离线包失败: $e';
      _packages.addAll(_getDefaultPackages());
    }
    
    _isLoading = false;
    notifyListeners();
  }
  
  List<OfflineTranslationPackage> _getDefaultPackages() {
    return [
      OfflineTranslationPackage(
        id: 'zh-en',
        languageCode: 'en',
        languageName: '英语',
        version: '1.0.0',
        size: 52428800, // 50MB
        entryCount: 100000,
        updatedAt: DateTime.now(),
        downloadUrl: '$baseUrl/offline/translations/zh-en.zip',
      ),
      OfflineTranslationPackage(
        id: 'zh-ja',
        languageCode: 'ja',
        languageName: '日语',
        version: '1.0.0',
        size: 41943040, // 40MB
        entryCount: 80000,
        updatedAt: DateTime.now(),
        downloadUrl: '$baseUrl/offline/translations/zh-ja.zip',
      ),
      OfflineTranslationPackage(
        id: 'zh-ko',
        languageCode: 'ko',
        languageName: '韩语',
        version: '1.0.0',
        size: 35651584, // 34MB
        entryCount: 70000,
        updatedAt: DateTime.now(),
        downloadUrl: '$baseUrl/offline/translations/zh-ko.zip',
      ),
      OfflineTranslationPackage(
        id: 'zh-fr',
        languageCode: 'fr',
        languageName: '法语',
        version: '1.0.0',
        size: 47185920, // 45MB
        entryCount: 90000,
        updatedAt: DateTime.now(),
        downloadUrl: '$baseUrl/offline/translations/zh-fr.zip',
      ),
      OfflineTranslationPackage(
        id: 'zh-de',
        languageCode: 'de',
        languageName: '德语',
        version: '1.0.0',
        size: 48234496, // 46MB
        entryCount: 95000,
        updatedAt: DateTime.now(),
        downloadUrl: '$baseUrl/offline/translations/zh-de.zip',
      ),
    ];
  }
  
  Future<void> _checkDownloadedStatus() async {
    try {
      final directory = await getApplicationDocumentsDirectory();
      final offlineDir = Directory('${directory.path}/offline_translations');
      
      if (!await offlineDir.exists()) return;
      
      for (var i = 0; i < _packages.length; i++) {
        final packageDir = Directory('${offlineDir.path}/${_packages[i].id}');
        if (await packageDir.exists()) {
          _packages[i] = _packages[i].copyWith(isDownloaded: true);
        }
      }
    } catch (e) {
      debugPrint('检查下载状态失败: $e');
    }
  }
  
  Future<void> _calculateCacheSize() async {
    try {
      final directory = await getApplicationDocumentsDirectory();
      final offlineDir = Directory('${directory.path}/offline_translations');
      
      if (!await offlineDir.exists()) {
        _totalCacheSize = 0;
        return;
      }
      
      int totalSize = 0;
      await for (final entity in offlineDir.list(recursive: true)) {
        if (entity is File) {
          totalSize += await entity.length();
        }
      }
      
      _totalCacheSize = totalSize;
      notifyListeners();
    } catch (e) {
      debugPrint('计算缓存大小失败: $e');
    }
  }
  
  Future<void> fetchAvailablePackages() async {
    _isLoading = true;
    notifyListeners();
    
    try {
      final response = await http.get(
        Uri.parse('$baseUrl/api/v1/translation/offline-packages'),
        headers: {'Authorization': 'Bearer ${await _getToken()}'},
      );
      
      if (response.statusCode == 200) {
        final List<dynamic> data = jsonDecode(response.body);
        
        for (final item in data) {
          final newPackage = OfflineTranslationPackage.fromJson(item);
          final existingIndex = _packages.indexWhere((p) => p.id == newPackage.id);
          
          if (existingIndex >= 0) {
            final existing = _packages[existingIndex];
            _packages[existingIndex] = newPackage.copyWith(
              isDownloaded: existing.isDownloaded,
            );
          } else {
            _packages.add(newPackage);
          }
        }
        
        await _savePackages();
      }
    } catch (e) {
      _error = '获取离线包列表失败: $e';
    }
    
    _isLoading = false;
    notifyListeners();
  }
  
  Future<void> downloadPackage(String packageId) async {
    final index = _packages.indexWhere((p) => p.id == packageId);
    if (index < 0) return;
    
    _packages[index] = _packages[index].copyWith(
      isDownloading: true,
      downloadProgress: 0,
    );
    notifyListeners();
    
    try {
      final directory = await getApplicationDocumentsDirectory();
      final offlineDir = Directory('${directory.path}/offline_translations');
      if (!await offlineDir.exists()) {
        await offlineDir.create(recursive: true);
      }
      
      final packageDir = Directory('${offlineDir.path}/$packageId');
      if (!await packageDir.exists()) {
        await packageDir.create();
      }
      
      final package = _packages[index];
      final filePath = '${packageDir.path}/translation.zip';
      
      final request = http.Request('GET', Uri.parse(package.downloadUrl));
      request.headers['Authorization'] = 'Bearer ${await _getToken()}';
      
      final response = await http.Client().send(request);
      
      if (response.statusCode == 200) {
        final file = File(filePath);
        final sink = file.openWrite();
        final contentLength = response.contentLength ?? 0;
        int received = 0;
        
        await for (final chunk in response.stream) {
          sink.add(chunk);
          received += chunk.length;
          
          if (contentLength > 0) {
            _packages[index] = _packages[index].copyWith(
              downloadProgress: received / contentLength,
            );
            notifyListeners();
          }
        }
        
        await sink.close();
        
        await _extractPackage(filePath, packageDir.path);
        
        _packages[index] = _packages[index].copyWith(
          isDownloaded: true,
          isDownloading: false,
          downloadProgress: 1,
        );
        
        await _savePackages();
        await _calculateCacheSize();
        
        notifyListeners();
      } else {
        throw Exception('下载失败: ${response.statusCode}');
      }
    } catch (e) {
      _packages[index] = _packages[index].copyWith(
        isDownloading: false,
        downloadProgress: 0,
      );
      _error = '下载失败: $e';
      notifyListeners();
    }
  }
  
  Future<void> _extractPackage(String zipPath, String targetDir) async {
    try {
      final zipFile = File(zipPath);
      if (await zipFile.exists()) {
        await zipFile.delete();
      }
    } catch (e) {
      debugPrint('清理压缩包失败: $e');
    }
  }
  
  Future<void> deletePackage(String packageId) async {
    final index = _packages.indexWhere((p) => p.id == packageId);
    if (index < 0) return;
    
    try {
      final directory = await getApplicationDocumentsDirectory();
      final packageDir = Directory('${directory.path}/offline_translations/$packageId');
      
      if (await packageDir.exists()) {
        await packageDir.delete(recursive: true);
      }
      
      _packages[index] = _packages[index].copyWith(isDownloaded: false);
      await _savePackages();
      await _calculateCacheSize();
      
      notifyListeners();
    } catch (e) {
      _error = '删除失败: $e';
      notifyListeners();
    }
  }
  
  Future<void> clearAllCache() async {
    try {
      final directory = await getApplicationDocumentsDirectory();
      final offlineDir = Directory('${directory.path}/offline_translations');
      
      if (await offlineDir.exists()) {
        await offlineDir.delete(recursive: true);
      }
      
      for (var i = 0; i < _packages.length; i++) {
        _packages[i] = _packages[i].copyWith(isDownloaded: false);
      }
      
      _totalCacheSize = 0;
      await _savePackages();
      notifyListeners();
    } catch (e) {
      _error = '清理缓存失败: $e';
      notifyListeners();
    }
  }
  
  Future<String?> translateOffline(String text, String targetLanguage) async {
    try {
      final directory = await getApplicationDocumentsDirectory();
      final packageDir = Directory(
        '${directory.path}/offline_translations/zh-$targetLanguage'
      );
      
      if (!await packageDir.exists()) return null;
      
      final cacheFile = File('${packageDir.path}/cache.json');
      if (!await cacheFile.exists()) return null;
      
      final content = await cacheFile.readAsString();
      final cache = jsonDecode(content) as Map<String, dynamic>;
      
      return cache[text] as String?;
    } catch (e) {
      debugPrint('离线翻译失败: $e');
      return null;
    }
  }
  
  Future<void> _savePackages() async {
    try {
      final prefs = await SharedPreferences.getInstance();
      final packagesJson = jsonEncode(_packages.map((p) => p.toJson()).toList());
      await prefs.setString('offline_translation_packages', packagesJson);
    } catch (e) {
      debugPrint('保存离线包信息失败: $e');
    }
  }
  
  Future<String> _getToken() async {
    final prefs = await SharedPreferences.getInstance();
    return prefs.getString('auth_token') ?? '';
  }
  
  List<OfflineTranslationPackage> getDownloadedPackages() {
    return _packages.where((p) => p.isDownloaded).toList();
  }
  
  List<OfflineTranslationPackage> getAvailablePackages() {
    return _packages.where((p) => !p.isDownloaded).toList();
  }
  
  bool get hasDownloadedPackages {
    return _packages.any((p) => p.isDownloaded);
  }
}
