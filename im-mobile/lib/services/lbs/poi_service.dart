import 'dart:async';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:google_maps_flutter/google_maps_flutter.dart';
import '../models/poi_model.dart';
import '../models/user_model.dart';
import '../services/api_service.dart';
import '../services/location_service.dart';
import '../utils/logger.dart';

/// POI服务 - 兴趣点管理
/// 提供附近商家搜索、POI展示、智能推荐等功能
class POIService extends ChangeNotifier {
  static final POIService _instance = POIService._internal();
  factory POIService() => _instance;
  POIService._internal();

  // 依赖服务
  final ApiService _apiService = ApiService();
  final LocationService _locationService = LocationService();

  // 状态
  bool _isLoading = false;
  String? _error;
  
  // POI数据
  List<POI> _nearbyPOIs = [];
  List<POI> _recommendedPOIs = [];
  List<POI> _favoritePOIs = [];
  POI? _selectedPOI;
  
  // 分页
  int _currentPage = 0;
  static const int _pageSize = 20;
  bool _hasMore = true;
  
  // 搜索状态
  String? _lastSearchKeyword;
  POICategory? _lastSearchCategory;
  double _lastSearchRadius = 5000; // 默认5公里
  
  // 缓存
  final Map<String, POI> _poiCache = {};
  final Map<String, DateTime> _cacheTimestamps = {};
  static const Duration _cacheValidity = Duration(minutes: 5);

  // 流控制器
  final StreamController<List<POI>> _nearbyUpdateController = 
      StreamController<List<POI>>.broadcast();

  // Getters
  bool get isLoading => _isLoading;
  String? get error => _error;
  List<POI> get nearbyPOIs => List.unmodifiable(_nearbyPOIs);
  List<POI> get recommendedPOIs => List.unmodifiable(_recommendedPOIs);
  List<POI> get favoritePOIs => List.unmodifiable(_favoritePOIs);
  POI? get selectedPOI => _selectedPOI;
  bool get hasMore => _hasMore;
  Stream<List<POI>> get nearbyUpdates => _nearbyUpdateController.stream;

  // ==================== 附近POI搜索 ====================

  /// 搜索附近POI
  Future<List<POI>> searchNearby({
    LatLng? location,
    POICategory? category,
    String? keyword,
    double radius = 5000, // 默认5公里
    int page = 0,
    bool refresh = false,
  }) async {
    if (_isLoading) return _nearbyPOIs;
    
    _isLoading = true;
    _error = null;
    notifyListeners();

    try {
      // 获取当前位置
      final searchLocation = location ?? await _locationService.getCurrentLocation();
      if (searchLocation == null) {
        throw Exception('无法获取当前位置');
      }

      // 保存搜索参数
      _lastSearchKeyword = keyword;
      _lastSearchCategory = category;
      _lastSearchRadius = radius;

      // 构建请求参数
      final params = {
        'latitude': searchLocation.latitude,
        'longitude': searchLocation.longitude,
        'radius': radius,
        'page': page,
        'size': _pageSize,
      };

      if (category != null) {
        params['category'] = category.code;
      }

      if (keyword != null && keyword.isNotEmpty) {
        params['keyword'] = keyword;
      }

      // 调用API
      final response = await _apiService.get('/lbs/poi/nearby', params: params);
      final data = jsonDecode(response.body);
      
      final List<dynamic> poiList = data['data']['list'] ?? [];
      final pois = poiList.map((json) => POI.fromJson(json)).toList();

      // 更新列表
      if (refresh || page == 0) {
        _nearbyPOIs = pois;
      } else {
        _nearbyPOIs.addAll(pois);
      }

      // 更新分页状态
      _currentPage = page;
      _hasMore = pois.length >= _pageSize;

      // 缓存POI详情
      for (final poi in pois) {
        _cachePOI(poi);
      }

      // 通知流更新
      _nearbyUpdateController.add(List.unmodifiable(_nearbyPOIs));

      Logger.i('POIService', '搜索到 ${pois.length} 个附近POI');
      return _nearbyPOIs;
    } catch (e) {
      _error = '搜索失败: $e';
      Logger.e('POIService', _error!);
      return _nearbyPOIs;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// 加载更多
  Future<List<POI>> loadMore() async {
    if (_isLoading || !_hasMore) return _nearbyPOIs;
    
    return searchNearby(
      location: null,
      category: _lastSearchCategory,
      keyword: _lastSearchKeyword,
      radius: _lastSearchRadius,
      page: _currentPage + 1,
    );
  }

  /// 刷新附近列表
  Future<List<POI>> refreshNearby() async {
    return searchNearby(
      location: null,
      category: _lastSearchCategory,
      keyword: _lastSearchKeyword,
      radius: _lastSearchRadius,
      page: 0,
      refresh: true,
    );
  }

  // ==================== 智能推荐 ====================

  /// 获取推荐POI（基于场景）
  Future<List<POI>> getRecommendedPOIs({
    required String scenario, // lunch, dinner, coffee, shopping, hotel
    LatLng? location,
    int limit = 10,
  }) async {
    _isLoading = true;
    notifyListeners();

    try {
      final searchLocation = location ?? await _locationService.getCurrentLocation();
      if (searchLocation == null) {
        throw Exception('无法获取当前位置');
      }

      final response = await _apiService.get('/lbs/poi/recommend', params: {
        'scenario': scenario,
        'latitude': searchLocation.latitude,
        'longitude': searchLocation.longitude,
        'limit': limit,
      });

      final data = jsonDecode(response.body);
      final List<dynamic> poiList = data['data']['list'] ?? [];
      final pois = poiList.map((json) => POI.fromJson(json)).toList();

      _recommendedPOIs = pois;
      
      // 缓存
      for (final poi in pois) {
        _cachePOI(poi);
      }

      Logger.i('POIService', '获取到 ${pois.length} 个推荐POI');
      return pois;
    } catch (e) {
      _error = '获取推荐失败: $e';
      Logger.e('POIService', _error!);
      return [];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// 根据场景获取推荐
  Future<List<POI>> getScenarioRecommendations(String scenario) async {
    final Map<String, String> scenarioMap = {
      '午餐': 'lunch',
      '晚餐': 'dinner',
      '咖啡': 'coffee',
      '下午茶': 'coffee',
      '购物': 'shopping',
      '酒店': 'hotel',
      '娱乐': 'entertainment',
      '运动': 'sports',
    };

    final apiScenario = scenarioMap[scenario] ?? scenario.toLowerCase();
    return getRecommendedPOIs(scenario: apiScenario);
  }

  // ==================== POI详情 ====================

  /// 获取POI详情
  Future<POI?> getPOIDetail(String poiId) async {
    // 先检查缓存
    final cachedPOI = _getCachedPOI(poiId);
    if (cachedPOI != null) {
      _selectedPOI = cachedPOI;
      notifyListeners();
      return cachedPOI;
    }

    _isLoading = true;
    notifyListeners();

    try {
      final response = await _apiService.get('/lbs/poi/$poiId');
      final data = jsonDecode(response.body);
      final poi = POI.fromJson(data['data']);

      _selectedPOI = poi;
      _cachePOI(poi);

      Logger.i('POIService', '获取POI详情: ${poi.name}');
      return poi;
    } catch (e) {
      _error = '获取POI详情失败: $e';
      Logger.e('POIService', _error!);
      return null;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// 选择POI
  void selectPOI(POI poi) {
    _selectedPOI = poi;
    notifyListeners();
  }

  /// 清除选择
  void clearSelection() {
    _selectedPOI = null;
    notifyListeners();
  }

  // ==================== 收藏功能 ====================

  /// 获取收藏的POI
  Future<List<POI>> getFavoritePOIs() async {
    try {
      final response = await _apiService.get('/lbs/poi/favorites');
      final data = jsonDecode(response.body);
      final List<dynamic> poiList = data['data'] ?? [];
      
      _favoritePOIs = poiList.map((json) => POI.fromJson(json)).toList();
      notifyListeners();

      return _favoritePOIs;
    } catch (e) {
      Logger.e('POIService', '获取收藏失败: $e');
      return [];
    }
  }

  /// 收藏POI
  Future<bool> favoritePOI(String poiId) async {
    try {
      await _apiService.post('/lbs/poi/$poiId/favorite', {});
      
      // 更新本地状态
      final poiIndex = _nearbyPOIs.indexWhere((p) => p.id == poiId);
      if (poiIndex >= 0) {
        _nearbyPOIs[poiIndex] = _nearbyPOIs[poiIndex].copyWith(isFavorite: true);
      }
      
      notifyListeners();
      Logger.i('POIService', '收藏POI: $poiId');
      return true;
    } catch (e) {
      Logger.e('POIService', '收藏失败: $e');
      return false;
    }
  }

  /// 取消收藏
  Future<bool> unfavoritePOI(String poiId) async {
    try {
      await _apiService.delete('/lbs/poi/$poiId/favorite');
      
      // 更新本地状态
      final poiIndex = _nearbyPOIs.indexWhere((p) => p.id == poiId);
      if (poiIndex >= 0) {
        _nearbyPOIs[poiIndex] = _nearbyPOIs[poiIndex].copyWith(isFavorite: false);
      }
      
      // 从收藏列表移除
      _favoritePOIs.removeWhere((p) => p.id == poiId);
      
      notifyListeners();
      Logger.i('POIService', '取消收藏POI: $poiId');
      return true;
    } catch (e) {
      Logger.e('POIService', '取消收藏失败: $e');
      return false;
    }
  }

  // ==================== POI评价 ====================

  /// 获取POI评价
  Future<List<POIReview>> getPOIReviews(String poiId, {int page = 0}) async {
    try {
      final response = await _apiService.get('/lbs/poi/$poiId/reviews', params: {
        'page': page,
        'size': 20,
      });

      final data = jsonDecode(response.body);
      final List<dynamic> reviewList = data['data']['list'] ?? [];
      
      return reviewList.map((json) => POIReview.fromJson(json)).toList();
    } catch (e) {
      Logger.e('POIService', '获取评价失败: $e');
      return [];
    }
  }

  /// 提交评价
  Future<bool> submitReview(String poiId, {
    required double rating,
    String? content,
    List<String>? images,
  }) async {
    try {
      await _apiService.post('/lbs/poi/$poiId/review', {
        'rating': rating,
        'content': content,
        'images': images,
      });

      Logger.i('POIService', '提交评价: $poiId');
      return true;
    } catch (e) {
      Logger.e('POIService', '提交评价失败: $e');
      return false;
    }
  }

  // ==================== 导航 ====================

  /// 计算到POI的距离和预计时间
  Future<NavigationInfo?> calculateNavigation(POI poi) async {
    final currentLocation = await _locationService.getCurrentLocation();
    if (currentLocation == null) return null;

    final distance = _locationService.calculateDistance(
      currentLocation,
      LatLng(poi.latitude, poi.longitude),
    );

    // 预估时间（步行: 5km/h, 驾车: 30km/h）
    final walkTimeMinutes = (distance / 5000 * 60).ceil();
    final driveTimeMinutes = (distance / 30000 * 60).ceil();

    return NavigationInfo(
      distance: distance,
      walkTimeMinutes: walkTimeMinutes,
      driveTimeMinutes: driveTimeMinutes,
      fromLocation: currentLocation,
      toLocation: LatLng(poi.latitude, poi.longitude),
    );
  }

  // ==================== 缓存管理 ====================

  void _cachePOI(POI poi) {
    _poiCache[poi.id] = poi;
    _cacheTimestamps[poi.id] = DateTime.now();
  }

  POI? _getCachedPOI(String poiId) {
    final poi = _poiCache[poiId];
    if (poi == null) return null;

    final timestamp = _cacheTimestamps[poiId];
    if (timestamp == null) return null;

    // 检查缓存是否过期
    if (DateTime.now().difference(timestamp) > _cacheValidity) {
      _poiCache.remove(poiId);
      _cacheTimestamps.remove(poiId);
      return null;
    }

    return poi;
  }

  /// 清除缓存
  void clearCache() {
    _poiCache.clear();
    _cacheTimestamps.clear();
    Logger.i('POIService', '清除POI缓存');
  }

  // ==================== 清理 ====================

  @override
  void dispose() {
    _nearbyUpdateController.close();
    super.dispose();
  }
}

/// 导航信息
class NavigationInfo {
  final double distance; // 米
  final int walkTimeMinutes;
  final int driveTimeMinutes;
  final LatLng fromLocation;
  final LatLng toLocation;

  NavigationInfo({
    required this.distance,
    required this.walkTimeMinutes,
    required this.driveTimeMinutes,
    required this.fromLocation,
    required this.toLocation,
  });

  String get formattedDistance {
    if (distance < 1000) {
      return '${distance.toInt()}m';
    } else {
      return '${(distance / 1000).toStringAsFixed(1)}km';
    }
  }

  String get formattedWalkTime {
    if (walkTimeMinutes < 60) {
      return '${walkTimeMinutes}分钟';
    } else {
      final hours = walkTimeMinutes ~/ 60;
      final mins = walkTimeMinutes % 60;
      return '${hours}小时${mins > 0 ? '$mins分钟' : ''}';
    }
  }

  String get formattedDriveTime {
    if (driveTimeMinutes < 60) {
      return '${driveTimeMinutes}分钟';
    } else {
      final hours = driveTimeMinutes ~/ 60;
      final mins = driveTimeMinutes % 60;
      return '${hours}小时${mins > 0 ? '$mins分钟' : ''}';
    }
  }
}
