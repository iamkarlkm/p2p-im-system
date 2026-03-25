import 'dart:convert';
import 'package:http/http.dart' as http;
import '../models/sticker_pack.dart';
import '../models/sticker.dart';
import '../models/sticker_stats.dart';
import 'api_service.dart';

/// Sticker Service - Manages sticker packs and individual stickers
/// Handles API calls, local caching, and sticker usage tracking
class StickerService {
  final ApiService _apiService;
  static const String _cacheKey = 'sticker_packs_cache';
  static const Duration _cacheTtl = Duration(minutes: 5);
  
  final Map<String, _CachedData> _cache = {};
  final Set<String> _downloadQueue = {};
  final List<String> _recentlyUsed = [];
  static const int _maxRecentItems = 50;

  StickerService(this._apiService);

  // ==================== Sticker Pack CRUD ====================

  Future<StickerPack> createStickerPack(StickerPackCreateRequest pack) async {
    final response = await _apiService.post('/stickers/packs', pack.toJson());
    _invalidateCache();
    return StickerPack.fromJson(response.data);
  }

  Future<StickerPack?> getStickerPack(String packId) async {
    final cached = _getFromCache('pack_$packId');
    if (cached != null) return StickerPack.fromJson(cached);

    try {
      final response = await _apiService.get('/stickers/packs/$packId');
      _setCache('pack_$packId', response.data);
      return StickerPack.fromJson(response.data);
    } catch (e) {
      print('Failed to fetch sticker pack: $e');
      return null;
    }
  }

  Future<StickerPack> updateStickerPack(int id, Map<String, dynamic> updates) async {
    final response = await _apiService.put('/stickers/packs/$id', updates);
    _invalidateCache();
    return StickerPack.fromJson(response.data);
  }

  Future<void> deleteStickerPack(int id, {String? reason}) async {
    await _apiService.delete(
      '/stickers/packs/$id',
      queryParameters: {'reason': reason ?? 'User requested'},
    );
    _invalidateCache();
  }

  // ==================== Sticker Pack Search ====================

  Future<StickerPackSearchResult> searchStickerPacks({
    String? query,
    String? category,
    bool? isOfficial,
    bool? isFeatured,
    bool? isFree,
    double? minRating,
    int? minDownloads,
    int page = 0,
    int size = 20,
  }) async {
    final response = await _apiService.get(
      '/stickers/packs/search',
      queryParameters: {
        if (query != null) 'query': query,
        if (category != null) 'category': category,
        if (isOfficial != null) 'isOfficial': isOfficial.toString(),
        if (isFeatured != null) 'isFeatured': isFeatured.toString(),
        if (isFree != null) 'isFree': isFree.toString(),
        if (minRating != null) 'minRating': minRating.toString(),
        if (minDownloads != null) 'minDownloads': minDownloads.toString(),
        'page': page.toString(),
        'size': size.toString(),
      },
    );

    final data = response.data;
    final content = (data['content'] as List)
        .map((item) => StickerPack.fromJson(item))
        .toList();

    return StickerPackSearchResult(
      content: content,
      total: data['totalElements'] ?? 0,
    );
  }

  Future<List<StickerPack>> getFeaturedStickerPacks({int page = 0, int size = 20}) async {
    final cached = _getFromCache('featured_packs');
    if (cached != null) {
      return (cached as List).map((item) => StickerPack.fromJson(item)).toList();
    }

    final response = await _apiService.get(
      '/stickers/packs/featured',
      queryParameters: {'page': page.toString(), 'size': size.toString()},
    );
    
    final content = (response.data['content'] as List)
        .map((item) => StickerPack.fromJson(item))
        .toList();
    
    _setCache('featured_packs', response.data['content']);
    return content;
  }

  Future<List<StickerPack>> getOfficialStickerPacks({int page = 0, int size = 20}) async {
    final response = await _apiService.get(
      '/stickers/packs/official',
      queryParameters: {'page': page.toString(), 'size': size.toString()},
    );
    
    return (response.data['content'] as List)
        .map((item) => StickerPack.fromJson(item))
        .toList();
  }

  Future<List<StickerPack>> getFreeStickerPacks({int page = 0, int size = 20}) async {
    final response = await _apiService.get(
      '/stickers/packs/free',
      queryParameters: {'page': page.toString(), 'size': size.toString()},
    );
    
    return (response.data['content'] as List)
        .map((item) => StickerPack.fromJson(item))
        .toList();
  }

  Future<List<StickerPack>> getNewestStickerPacks({int page = 0, int size = 20}) async {
    final response = await _apiService.get(
      '/stickers/packs/new',
      queryParameters: {'page': page.toString(), 'size': size.toString()},
    );
    
    return (response.data['content'] as List)
        .map((item) => StickerPack.fromJson(item))
        .toList();
  }

  Future<List<StickerPack>> getTrendingStickerPacks({int page = 0, int size = 20}) async {
    final response = await _apiService.get(
      '/stickers/packs/trending',
      queryParameters: {'page': page.toString(), 'size': size.toString()},
    );
    
    return (response.data['content'] as List)
        .map((item) => StickerPack.fromJson(item))
        .toList();
  }

  Future<List<StickerPack>> getTopStickerPacksByDownloads({int page = 0, int size = 20}) async {
    final response = await _apiService.get(
      '/stickers/packs/top/downloads',
      queryParameters: {'page': page.toString(), 'size': size.toString()},
    );
    
    return (response.data['content'] as List)
        .map((item) => StickerPack.fromJson(item))
        .toList();
  }

  Future<List<StickerPack>> getTopStickerPacksByLikes({int page = 0, int size = 20}) async {
    final response = await _apiService.get(
      '/stickers/packs/top/likes',
      queryParameters: {'page': page.toString(), 'size': size.toString()},
    );
    
    return (response.data['content'] as List)
        .map((item) => StickerPack.fromJson(item))
        .toList();
  }

  Future<List<StickerPack>> getTopStickerPacksByRating({int page = 0, int size = 20}) async {
    final response = await _apiService.get(
      '/stickers/packs/top/rating',
      queryParameters: {'page': page.toString(), 'size': size.toString()},
    );
    
    return (response.data['content'] as List)
        .map((item) => StickerPack.fromJson(item))
        .toList();
  }

  Future<List<StickerPack>> getRecommendedStickerPacks({int limit = 10}) async {
    final response = await _apiService.get(
      '/stickers/packs/recommended',
      queryParameters: {'limit': limit.toString()},
    );
    
    return (response.data as List)
        .map((item) => StickerPack.fromJson(item))
        .toList();
  }

  // ==================== Sticker Management ====================

  Future<List<Sticker>> getStickersInPack(int packId) async {
    final cached = _getFromCache('pack_${packId}_stickers');
    if (cached != null) {
      return (cached as List).map((item) => Sticker.fromJson(item)).toList();
    }

    final response = await _apiService.get('/stickers/packs/$packId/stickers');
    _setCache('pack_${packId}_stickers', response.data);
    
    return (response.data as List)
        .map((item) => Sticker.fromJson(item))
        .toList();
  }

  Future<Sticker> addStickerToPack(int packId, StickerCreateRequest sticker) async {
    final response = await _apiService.post(
      '/stickers/packs/$packId/stickers',
      sticker.toJson(),
    );
    _invalidateCache();
    return Sticker.fromJson(response.data);
  }

  Future<void> removeStickerFromPack(int packId, int stickerId) async {
    await _apiService.delete('/stickers/packs/$packId/stickers/$stickerId');
    _invalidateCache();
  }

  // ==================== Usage Tracking ====================

  Future<void> downloadStickerPack(int packId) async {
    final key = 'pack_$packId';
    if (_downloadQueue.contains(key)) return;

    _downloadQueue.add(key);
    try {
      await _apiService.post('/stickers/packs/$packId/download');
    } finally {
      _downloadQueue.remove(key);
    }
  }

  Future<void> useSticker(int stickerId, String conversationId, {String? messageId}) async {
    await _apiService.post(
      '/stickers/stickers/$stickerId/use',
      {
        'conversationId': conversationId,
        if (messageId != null) 'messageId': messageId,
      },
    );

    // Update recently used list
    final stickerKey = 'sticker_$stickerId';
    _recentlyUsed.remove(stickerKey);
    _recentlyUsed.insert(0, stickerKey);
    
    if (_recentlyUsed.length > _maxRecentItems) {
      _recentlyUsed.removeLast();
    }

    _saveRecentlyUsed();
  }

  Future<void> favoriteSticker(int stickerId) async {
    await _apiService.post('/stickers/stickers/$stickerId/favorite');
  }

  // ==================== Rating and Feedback ====================

  Future<void> rateStickerPack(int packId, double rating, {String? comment}) async {
    await _apiService.post(
      '/stickers/packs/$packId/rate',
      {
        'rating': rating,
        if (comment != null) 'comment': comment,
      },
    );
  }

  // ==================== Admin Operations ====================

  Future<void> approveStickerPack(int packId) async {
    await _apiService.post('/stickers/admin/packs/$packId/approve');
    _invalidateCache();
  }

  Future<void> rejectStickerPack(int packId, String reason) async {
    await _apiService.post(
      '/stickers/admin/packs/$packId/reject',
      {'reason': reason},
    );
    _invalidateCache();
  }

  Future<void> featureStickerPack(int packId) async {
    await _apiService.post('/stickers/admin/packs/$packId/feature');
    _invalidateCache();
  }

  Future<void> unfeatureStickerPack(int packId) async {
    await _apiService.post('/stickers/admin/packs/$packId/unfeature');
    _invalidateCache();
  }

  // ==================== Statistics ====================

  Future<StickerStats> getStickerPackStatistics(int packId) async {
    final response = await _apiService.get('/stickers/packs/$packId/stats');
    return StickerStats.fromJson(response.data);
  }

  Future<StickerSystemStats> getSystemStatistics() async {
    final response = await _apiService.get('/stickers/stats');
    return StickerSystemStats.fromJson(response.data);
  }

  // ==================== Cache Management ====================

  dynamic _getFromCache(String key) {
    final cached = _cache[key];
    if (cached != null && DateTime.now().difference(cached.timestamp) < _cacheTtl) {
      return cached.data;
    }
    _cache.remove(key);
    return null;
  }

  void _setCache(String key, dynamic data) {
    _cache[key] = _CachedData(data: data, timestamp: DateTime.now());
  }

  void _invalidateCache() {
    _cache.clear();
  }

  // ==================== Recently Used Management ====================

  List<String> getRecentlyUsed() => List.unmodifiable(_recentlyUsed);

  void _saveRecentlyUsed() {
    // TODO: Implement persistent storage using shared_preferences
    print('Recently used stickers: $_recentlyUsed');
  }

  void _loadRecentlyUsed() {
    // TODO: Implement persistent storage using shared_preferences
  }

  // ==================== Initialization ====================

  void init() {
    _loadRecentlyUsed();
  }
}

class _CachedData {
  final dynamic data;
  final DateTime timestamp;

  _CachedData({required this.data, required this.timestamp});
}

class StickerPackSearchResult {
  final List<StickerPack> content;
  final int total;

  StickerPackSearchResult({required this.content, required this.total});
}

class StickerPackCreateRequest {
  final String name;
  final String? description;
  final String author;
  final String category;
  final List<String>? tags;
  final bool? isFree;
  final double? price;
  final String? licenseType;

  StickerPackCreateRequest({
    required this.name,
    this.description,
    required this.author,
    required this.category,
    this.tags,
    this.isFree,
    this.price,
    this.licenseType,
  });

  Map<String, dynamic> toJson() => {
        'name': name,
        if (description != null) 'description': description,
        'author': author,
        'category': category,
        if (tags != null) 'tags': tags,
        if (isFree != null) 'isFree': isFree,
        if (price != null) 'price': price,
        if (licenseType != null) 'licenseType': licenseType,
      };
}

class StickerCreateRequest {
  final String name;
  final String? description;
  final String imageUrl;
  final String thumbnailUrl;
  final String? animatedUrl;
  final int width;
  final int height;
  final int fileSizeBytes;
  final String fileFormat;
  final bool isAnimated;
  final int? frameCount;
  final double? durationSeconds;
  final int? frameRate;
  final int? sortOrder;

  StickerCreateRequest({
    required this.name,
    this.description,
    required this.imageUrl,
    required this.thumbnailUrl,
    this.animatedUrl,
    required this.width,
    required this.height,
    required this.fileSizeBytes,
    required this.fileFormat,
    required this.isAnimated,
    this.frameCount,
    this.durationSeconds,
    this.frameRate,
    this.sortOrder,
  });

  Map<String, dynamic> toJson() => {
        'name': name,
        if (description != null) 'description': description,
        'imageUrl': imageUrl,
        'thumbnailUrl': thumbnailUrl,
        if (animatedUrl != null) 'animatedUrl': animatedUrl,
        'width': width,
        'height': height,
        'fileSizeBytes': fileSizeBytes,
        'fileFormat': fileFormat,
        'isAnimated': isAnimated,
        if (frameCount != null) 'frameCount': frameCount,
        if (durationSeconds != null) 'durationSeconds': durationSeconds,
        if (frameRate != null) 'frameRate': frameRate,
        if (sortOrder != null) 'sortOrder': sortOrder,
      };
}