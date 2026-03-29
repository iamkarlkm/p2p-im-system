// 探店发现服务
// 生成时间: 2026-03-28 21:40

import 'dart:async';
import 'dart:convert';
import 'dart:math';
import 'package:http/http.dart' as http;
import '../../../core/api/api_client.dart';
import '../../../core/location/location_service.dart';
import '../../../core/storage/local_storage.dart';
import '../models/store_discovery_models.dart';

/// 探店发现服务
/// 提供智能探店推荐、探店路线规划、打卡记录等功能
class StoreDiscoveryService {
  static final StoreDiscoveryService _instance = StoreDiscoveryService._internal();
  factory StoreDiscoveryService() => _instance;
  StoreDiscoveryService._internal();

  final ApiClient _apiClient = ApiClient();
  final LocationService _locationService = LocationService();
  final LocalStorage _storage = LocalStorage();
  
  // 缓存配置
  static const String _cacheKeyRecommendations = 'store_recommendations';
  static const String _cacheKeyTrending = 'trending_stores';
  static const String _cacheKeyCheckins = 'user_checkins';
  static const Duration _cacheDuration = Duration(minutes: 5);
  
  // 状态管理
  final StreamController<List<StoreRecommendation>> _recommendationsController = 
      StreamController<List<StoreRecommendation>>.broadcast();
  final StreamController<StoreCheckinRecord> _checkinController = 
      StreamController<StoreCheckinRecord>.broadcast();
  
  Stream<List<StoreRecommendation>> get recommendationsStream => _recommendationsController.stream;
  Stream<StoreCheckinRecord> get checkinStream => _checkinController.stream;

  // ==================== 智能探店推荐 ====================
  
  /// 获取个性化探店推荐
  /// [latitude] 当前纬度
  /// [longitude] 当前经度
  /// [radius] 搜索半径（米）
  /// [categoryFilter] 品类筛选
  /// [priceRange] 价格范围
  Future<List<StoreRecommendation>> getPersonalizedRecommendations({
    required double latitude,
    required double longitude,
    double radius = 5000,
    List<String>? categoryFilter,
    PriceRange? priceRange,
  }) async {
    try {
      // 先检查缓存
      final cached = await _getCachedRecommendations(latitude, longitude, radius);
      if (cached != null && cached.isNotEmpty) {
        _recommendationsController.add(cached);
        return cached;
      }
      
      final response = await _apiClient.post('/api/v1/store-discovery/recommendations', data: {
        'latitude': latitude,
        'longitude': longitude,
        'radius': radius,
        'categoryFilter': categoryFilter,
        'priceRange': priceRange?.toJson(),
        'timestamp': DateTime.now().millisecondsSinceEpoch,
      });
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final List<dynamic> stores = data['stores'] ?? [];
        
        final recommendations = stores.map((json) => 
          StoreRecommendation.fromJson(json)
        ).toList();
        
        // 缓存结果
        await _cacheRecommendations(latitude, longitude, radius, recommendations);
        _recommendationsController.add(recommendations);
        
        return recommendations;
      }
      
      throw Exception('获取推荐失败: ${response.statusCode}');
    } catch (e) {
      print('获取探店推荐失败: $e');
      return [];
    }
  }
  
  /// 获取基于位置的附近探店推荐
  Future<List<StoreRecommendation>> getNearbyStoreRecommendations({
    double? radius,
    List<String>? categories,
  }) async {
    try {
      final position = await _locationService.getCurrentPosition();
      return getPersonalizedRecommendations(
        latitude: position.latitude,
        longitude: position.longitude,
        radius: radius ?? 3000,
        categoryFilter: categories,
      );
    } catch (e) {
      print('获取附近探店推荐失败: $e');
      return [];
    }
  }
  
  /// 获取热门探店榜单
  Future<List<TrendingStore>> getTrendingStores({
    required TrendingType type,
    String? cityCode,
    String? districtCode,
    int limit = 20,
  }) async {
    try {
      // 检查缓存
      final cacheKey = '${_cacheKeyTrending}_${type.name}_$cityCode';
      final cached = await _storage.get(cacheKey);
      if (cached != null) {
        final cacheTime = DateTime.parse(cached['timestamp']);
        if (DateTime.now().difference(cacheTime) < _cacheDuration) {
          final List<dynamic> stores = cached['data'];
          return stores.map((json) => TrendingStore.fromJson(json)).toList();
        }
      }
      
      final response = await _apiClient.get('/api/v1/store-discovery/trending', queryParameters: {
        'type': type.name,
        'cityCode': cityCode,
        'districtCode': districtCode,
        'limit': limit,
      });
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final List<dynamic> stores = data['stores'] ?? [];
        
        final trendingStores = stores.map((json) => 
          TrendingStore.fromJson(json)
        ).toList();
        
        // 缓存结果
        await _storage.set(cacheKey, {
          'timestamp': DateTime.now().toIso8601String(),
          'data': stores,
        });
        
        return trendingStores;
      }
      
      return [];
    } catch (e) {
      print('获取热门榜单失败: $e');
      return [];
    }
  }
  
  /// 获取新店开业列表
  Future<List<NewStoreOpening>> getNewStoreOpenings({
    required double latitude,
    required double longitude,
    double radius = 10000,
    int daysSinceOpening = 30,
  }) async {
    try {
      final response = await _apiClient.get('/api/v1/store-discovery/new-openings', queryParameters: {
        'latitude': latitude,
        'longitude': longitude,
        'radius': radius,
        'daysSinceOpening': daysSinceOpening,
      });
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final List<dynamic> stores = data['stores'] ?? [];
        return stores.map((json) => NewStoreOpening.fromJson(json)).toList();
      }
      
      return [];
    } catch (e) {
      print('获取新店列表失败: $e');
      return [];
    }
  }

  // ==================== 探店路线规划 ====================
  
  /// 规划探店路线
  /// 使用TSP算法优化多家店铺的访问顺序
  Future<StoreTourRoute> planStoreTourRoute({
    required double startLatitude,
    required double startLongitude,
    required List<String> storeIds,
    TourOptimizationStrategy strategy = TourOptimizationStrategy.shortestDistance,
    Duration? maxDuration,
  }) async {
    try {
      final response = await _apiClient.post('/api/v1/store-discovery/plan-route', data: {
        'startLatitude': startLatitude,
        'startLongitude': startLongitude,
        'storeIds': storeIds,
        'strategy': strategy.name,
        'maxDurationMinutes': maxDuration?.inMinutes,
      });
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return StoreTourRoute.fromJson(data['route']);
      }
      
      throw Exception('路线规划失败');
    } catch (e) {
      print('探店路线规划失败: $e');
      // 返回默认顺序
      return StoreTourRoute(
        stores: [],
        totalDistance: 0,
        estimatedDuration: Duration.zero,
        routePolyline: '',
      );
    }
  }
  
  /// 智能推荐探店路线
  /// 根据用户偏好和时间自动推荐最优探店路线
  Future<List<StoreTourRoute>> getRecommendedTourRoutes({
    required double latitude,
    required double longitude,
    required int storeCount,
    Duration? availableTime,
    List<String>? preferredCategories,
  }) async {
    try {
      final response = await _apiClient.post('/api/v1/store-discovery/recommended-routes', data: {
        'latitude': latitude,
        'longitude': longitude,
        'storeCount': storeCount,
        'availableTimeMinutes': availableTime?.inMinutes ?? 180,
        'preferredCategories': preferredCategories,
      });
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final List<dynamic> routes = data['routes'] ?? [];
        return routes.map((json) => StoreTourRoute.fromJson(json)).toList();
      }
      
      return [];
    } catch (e) {
      print('获取推荐路线失败: $e');
      return [];
    }
  }

  // ==================== 探店打卡功能 ====================
  
  /// 执行探店打卡
  /// 使用地理围栏自动检测到店
  Future<StoreCheckinResult> checkInStore({
    required String storeId,
    required double latitude,
    required double longitude,
    String? note,
    List<String>? photoUrls,
    List<String>? tags,
    bool isAutoCheckin = false,
  }) async {
    try {
      final response = await _apiClient.post('/api/v1/store-discovery/checkin', data: {
        'storeId': storeId,
        'latitude': latitude,
        'longitude': longitude,
        'checkInTime': DateTime.now().toIso8601String(),
        'note': note,
        'photoUrls': photoUrls,
        'tags': tags,
        'isAutoCheckin': isAutoCheckin,
      });
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final result = StoreCheckinResult.fromJson(data);
        
        // 发送打卡事件
        _checkinController.add(StoreCheckinRecord(
          storeId: storeId,
          storeName: result.storeName,
          checkInTime: DateTime.now(),
          latitude: latitude,
          longitude: longitude,
          earnedPoints: result.earnedPoints,
        ));
        
        // 保存到本地记录
        await _saveCheckinRecord(result);
        
        return result;
      }
      
      throw Exception('打卡失败: ${response.statusCode}');
    } catch (e) {
      print('探店打卡失败: $e');
      rethrow;
    }
  }
  
  /// 自动检测到店并打卡
  /// 当用户进入店铺地理围栏时自动触发
  Future<StoreCheckinResult?> autoCheckInIfNearby({
    required String storeId,
    required double storeLatitude,
    required double storeLongitude,
    double checkInRadius = 100,
  }) async {
    try {
      final position = await _locationService.getCurrentPosition();
      final distance = _calculateDistance(
        position.latitude, position.longitude,
        storeLatitude, storeLongitude,
      );
      
      if (distance <= checkInRadius) {
        // 检查是否已经打卡过
        final hasCheckedIn = await _hasCheckedInToday(storeId);
        if (!hasCheckedIn) {
          return checkInStore(
            storeId: storeId,
            latitude: position.latitude,
            longitude: position.longitude,
            isAutoCheckin: true,
          );
        }
      }
      
      return null;
    } catch (e) {
      print('自动打卡检测失败: $e');
      return null;
    }
  }
  
  /// 获取用户探店打卡记录
  Future<List<StoreCheckinRecord>> getUserCheckinHistory({
    int page = 1,
    int pageSize = 20,
    DateTime? startDate,
    DateTime? endDate,
  }) async {
    try {
      // 先从本地获取
      final localRecords = await _getLocalCheckinRecords();
      
      // 同步服务器数据
      final response = await _apiClient.get('/api/v1/store-discovery/checkin-history', queryParameters: {
        'page': page,
        'pageSize': pageSize,
        'startDate': startDate?.toIso8601String(),
        'endDate': endDate?.toIso8601String(),
      });
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final List<dynamic> records = data['records'] ?? [];
        final serverRecords = records.map((json) => 
          StoreCheckinRecord.fromJson(json)
        ).toList();
        
        // 合并并去重
        final allRecords = [...localRecords, ...serverRecords];
        final uniqueRecords = <String, StoreCheckinRecord>{};
        for (var record in allRecords) {
          uniqueRecords[record.id] = record;
        }
        
        final result = uniqueRecords.values.toList()
          ..sort((a, b) => b.checkInTime.compareTo(a.checkInTime));
        
        return result;
      }
      
      return localRecords;
    } catch (e) {
      print('获取打卡记录失败: $e');
      return _getLocalCheckinRecords();
    }
  }
  
  /// 获取探店统计
  Future<StoreDiscoveryStats> getUserDiscoveryStats() async {
    try {
      final response = await _apiClient.get('/api/v1/store-discovery/stats');
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return StoreDiscoveryStats.fromJson(data);
      }
      
      // 计算本地统计
      final checkins = await _getLocalCheckinRecords();
      return StoreDiscoveryStats(
        totalCheckins: checkins.length,
        uniqueStores: checkins.map((c) => c.storeId).toSet().length,
        totalPointsEarned: checkins.fold(0, (sum, c) => sum + c.earnedPoints),
        currentStreak: _calculateStreak(checkins),
        favoriteCategory: '',
      );
    } catch (e) {
      print('获取探店统计失败: $e');
      return StoreDiscoveryStats.empty();
    }
  }

  // ==================== 探店达人功能 ====================
  
  /// 获取探店达人榜单
  Future<List<StoreDiscoveryInfluencer>> getInfluencerLeaderboard({
    String? cityCode,
    InfluencerRankingType rankingType = InfluencerRankingType.weekly,
    int limit = 50,
  }) async {
    try {
      final response = await _apiClient.get('/api/v1/store-discovery/influencers', queryParameters: {
        'cityCode': cityCode,
        'rankingType': rankingType.name,
        'limit': limit,
      });
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final List<dynamic> influencers = data['influencers'] ?? [];
        return influencers.map((json) => 
          StoreDiscoveryInfluencer.fromJson(json)
        ).toList();
      }
      
      return [];
    } catch (e) {
      print('获取达人榜单失败: $e');
      return [];
    }
  }
  
  /// 获取探店笔记列表
  Future<List<DiscoveryNote>> getDiscoveryNotes({
    String? storeId,
    String? userId,
    NoteSortType sortType = NoteSortType.recommended,
    int page = 1,
    int pageSize = 10,
  }) async {
    try {
      final response = await _apiClient.get('/api/v1/store-discovery/notes', queryParameters: {
        'storeId': storeId,
        'userId': userId,
        'sortType': sortType.name,
        'page': page,
        'pageSize': pageSize,
      });
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final List<dynamic> notes = data['notes'] ?? [];
        return notes.map((json) => DiscoveryNote.fromJson(json)).toList();
      }
      
      return [];
    } catch (e) {
      print('获取探店笔记失败: $e');
      return [];
    }
  }
  
  /// 发布探店笔记
  Future<bool> publishDiscoveryNote({
    required String storeId,
    required String content,
    List<String>? photoUrls,
    List<String>? videoUrls,
    List<String>? tags,
    int? rating,
    double? spentAmount,
  }) async {
    try {
      final response = await _apiClient.post('/api/v1/store-discovery/notes', data: {
        'storeId': storeId,
        'content': content,
        'photoUrls': photoUrls,
        'videoUrls': videoUrls,
        'tags': tags,
        'rating': rating,
        'spentAmount': spentAmount,
        'publishTime': DateTime.now().toIso8601String(),
      });
      
      return response.statusCode == 200 || response.statusCode == 201;
    } catch (e) {
      print('发布探店笔记失败: $e');
      return false;
    }
  }

  // ==================== 探店助手功能 ====================
  
  /// 获取最佳探店时间建议
  Future<BestVisitTimeSuggestion> getBestVisitTimeSuggestion(String storeId) async {
    try {
      final response = await _apiClient.get('/api/v1/store-discovery/best-visit-time/$storeId');
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return BestVisitTimeSuggestion.fromJson(data);
      }
      
      // 返回默认建议
      return BestVisitTimeSuggestion(
        storeId: storeId,
        recommendedTimes: [
          TimeSlotRecommendation(
            dayOfWeek: DateTime.now().weekday,
            startHour: 10,
            endHour: 11,
            crowdLevel: CrowdLevel.low,
            reason: '上午刚开门，人流较少',
          ),
        ],
        avoidTimes: [
          TimeSlotRecommendation(
            dayOfWeek: DateTime.now().weekday,
            startHour: 12,
            endHour: 13,
            crowdLevel: CrowdLevel.high,
            reason: '午餐高峰期，需要排队',
          ),
        ],
      );
    } catch (e) {
      print('获取最佳探店时间失败: $e');
      return BestVisitTimeSuggestion.empty(storeId);
    }
  }
  
  /// 获取探店预算建议
  Future<VisitBudgetEstimate> getVisitBudgetEstimate({
    required String storeId,
    required int peopleCount,
    VisitStyle style = VisitStyle.standard,
  }) async {
    try {
      final response = await _apiClient.get('/api/v1/store-discovery/budget-estimate/$storeId', queryParameters: {
        'peopleCount': peopleCount,
        'style': style.name,
      });
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        return VisitBudgetEstimate.fromJson(data);
      }
      
      return VisitBudgetEstimate.empty();
    } catch (e) {
      print('获取预算建议失败: $e');
      return VisitBudgetEstimate.empty();
    }
  }
  
  /// 订阅新店开业提醒
  Future<bool> subscribeToNewStoreAlerts({
    String? category,
    String? districtCode,
    double? alertRadius,
  }) async {
    try {
      final response = await _apiClient.post('/api/v1/store-discovery/subscribe-new-store', data: {
        'category': category,
        'districtCode': districtCode,
        'alertRadius': alertRadius,
      });
      
      return response.statusCode == 200;
    } catch (e) {
      print('订阅新店提醒失败: $e');
      return false;
    }
  }

  // ==================== 好友探店组队 ====================
  
  /// 创建探店组队
  Future<StoreDiscoveryGroup> createDiscoveryGroup({
    required String name,
    required String storeId,
    required DateTime plannedVisitTime,
    int? maxMembers,
    String? note,
  }) async {
    try {
      final response = await _apiClient.post('/api/v1/store-discovery/groups', data: {
        'name': name,
        'storeId': storeId,
        'plannedVisitTime': plannedVisitTime.toIso8601String(),
        'maxMembers': maxMembers ?? 10,
        'note': note,
      });
      
      if (response.statusCode == 200 || response.statusCode == 201) {
        final data = jsonDecode(response.body);
        return StoreDiscoveryGroup.fromJson(data['group']);
      }
      
      throw Exception('创建组队失败');
    } catch (e) {
      print('创建探店组队失败: $e');
      rethrow;
    }
  }
  
  /// 获取组队列表
  Future<List<StoreDiscoveryGroup>> getDiscoveryGroups({
    String? storeId,
    GroupStatus? status,
    int page = 1,
    int pageSize = 20,
  }) async {
    try {
      final response = await _apiClient.get('/api/v1/store-discovery/groups', queryParameters: {
        'storeId': storeId,
        'status': status?.name,
        'page': page,
        'pageSize': pageSize,
      });
      
      if (response.statusCode == 200) {
        final data = jsonDecode(response.body);
        final List<dynamic> groups = data['groups'] ?? [];
        return groups.map((json) => StoreDiscoveryGroup.fromJson(json)).toList();
      }
      
      return [];
    } catch (e) {
      print('获取组队列表失败: $e');
      return [];
    }
  }

  // ==================== 辅助方法 ====================
  
  /// 计算两点间距离（米）
  double _calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    const R = 6371000; // 地球半径（米）
    final phi1 = lat1 * pi / 180;
    final phi2 = lat2 * pi / 180;
    final deltaPhi = (lat2 - lat1) * pi / 180;
    final deltaLambda = (lon2 - lon1) * pi / 180;
    
    final a = sin(deltaPhi / 2) * sin(deltaPhi / 2) +
              cos(phi1) * cos(phi2) *
              sin(deltaLambda / 2) * sin(deltaLambda / 2);
    final c = 2 * atan2(sqrt(a), sqrt(1 - a));
    
    return R * c;
  }
  
  /// 获取缓存的推荐
  Future<List<StoreRecommendation>?> _getCachedRecommendations(
    double lat, double lng, double radius
  ) async {
    try {
      final cached = await _storage.get(_cacheKeyRecommendations);
      if (cached != null) {
        final cacheTime = DateTime.parse(cached['timestamp']);
        if (DateTime.now().difference(cacheTime) < _cacheDuration) {
          final cachedLat = cached['latitude'] as double;
          final cachedLng = cached['longitude'] as double;
          final distance = _calculateDistance(lat, lng, cachedLat, cachedLng);
          
          // 位置变化小于500米且半径相同则使用缓存
          if (distance < 500 && cached['radius'] == radius) {
            final List<dynamic> data = cached['data'];
            return data.map((json) => StoreRecommendation.fromJson(json)).toList();
          }
        }
      }
      return null;
    } catch (e) {
      return null;
    }
  }
  
  /// 缓存推荐结果
  Future<void> _cacheRecommendations(
    double lat, double lng, double radius, List<StoreRecommendation> recommendations
  ) async {
    try {
      await _storage.set(_cacheKeyRecommendations, {
        'timestamp': DateTime.now().toIso8601String(),
        'latitude': lat,
        'longitude': lng,
        'radius': radius,
        'data': recommendations.map((r) => r.toJson()).toList(),
      });
    } catch (e) {
      print('缓存推荐失败: $e');
    }
  }
  
  /// 保存打卡记录
  Future<void> _saveCheckinRecord(StoreCheckinResult result) async {
    try {
      final records = await _getLocalCheckinRecords();
      records.add(StoreCheckinRecord(
        id: result.checkinId,
        storeId: result.storeId,
        storeName: result.storeName,
        checkInTime: DateTime.now(),
        latitude: result.latitude,
        longitude: result.longitude,
        earnedPoints: result.earnedPoints,
        isSynced: true,
      ));
      
      await _storage.set(_cacheKeyCheckins, {
        'records': records.map((r) => r.toJson()).toList(),
      });
    } catch (e) {
      print('保存打卡记录失败: $e');
    }
  }
  
  /// 获取本地打卡记录
  Future<List<StoreCheckinRecord>> _getLocalCheckinRecords() async {
    try {
      final data = await _storage.get(_cacheKeyCheckins);
      if (data != null && data['records'] != null) {
        final List<dynamic> records = data['records'];
        return records.map((json) => StoreCheckinRecord.fromJson(json)).toList();
      }
    } catch (e) {
      print('读取本地打卡记录失败: $e');
    }
    return [];
  }
  
  /// 检查今天是否已经打卡
  Future<bool> _hasCheckedInToday(String storeId) async {
    final records = await _getLocalCheckinRecords();
    final today = DateTime.now();
    return records.any((r) => 
      r.storeId == storeId &&
      r.checkInTime.year == today.year &&
      r.checkInTime.month == today.month &&
      r.checkInTime.day == today.day
    );
  }
  
  /// 计算连续打卡天数
  int _calculateStreak(List<StoreCheckinRecord> checkins) {
    if (checkins.isEmpty) return 0;
    
    final sorted = checkins..sort((a, b) => b.checkInTime.compareTo(a.checkInTime));
    final today = DateTime.now();
    int streak = 0;
    DateTime? lastDate;
    
    for (var checkin in sorted) {
      final date = DateTime(checkin.checkInTime.year, checkin.checkInTime.month, checkin.checkInTime.day);
      
      if (lastDate == null) {
        // 第一天
        final diff = today.difference(date).inDays;
        if (diff <= 1) {
          streak = 1;
          lastDate = date;
        } else {
          break;
        }
      } else {
        final diff = lastDate.difference(date).inDays;
        if (diff == 1) {
          streak++;
          lastDate = date;
        } else if (diff == 0) {
          // 同一天多次打卡，跳过
          continue;
        } else {
          break;
        }
      }
    }
    
    return streak;
  }
  
  /// 清理缓存
  Future<void> clearCache() async {
    await _storage.delete(_cacheKeyRecommendations);
    await _storage.delete(_cacheKeyTrending);
  }
  
  /// 释放资源
  void dispose() {
    _recommendationsController.close();
    _checkinController.close();
  }
}

// ==================== 枚举类型 ====================

enum TrendingType {
  weekly,      // 本周热门
  monthly,     // 本月热门
  overall,     // 总榜
  newStores,   // 新店热门
  hiddenGems,  // 隐藏小店
  following,   // 关注的人
}

enum TourOptimizationStrategy {
  shortestDistance,  // 最短距离
  shortestTime,      // 最短时间
  bestExperience,    // 最佳体验
  lowestCost,        // 最低成本
}

enum InfluencerRankingType {
  daily,
  weekly,
  monthly,
  overall,
}

enum NoteSortType {
  recommended,  // 推荐
  latest,       // 最新
  popular,      // 热门
  nearby,       // 附近
}

enum GroupStatus {
  recruiting,   // 招募中
  full,         // 已满员
  completed,    // 已完成
  cancelled,    // 已取消
}

enum VisitStyle {
  budget,       // 经济型
  standard,     // 标准型
  premium,      // 品质型
  luxury,       // 豪华型
}

enum CrowdLevel {
  low,
  medium,
  high,
}

// ==================== 数据模型 ====================

class StoreRecommendation {
  final String id;
  final String name;
  final String category;
  final double rating;
  final int reviewCount;
  final double latitude;
  final double longitude;
  final double distance;
  final String? mainImage;
  final PriceRange? priceRange;
  final List<String> tags;
  final String? recommendReason;
  final double matchScore;

  StoreRecommendation({
    required this.id,
    required this.name,
    required this.category,
    required this.rating,
    required this.reviewCount,
    required this.latitude,
    required this.longitude,
    required this.distance,
    this.mainImage,
    this.priceRange,
    this.tags = const [],
    this.recommendReason,
    required this.matchScore,
  });

  factory StoreRecommendation.fromJson(Map<String, dynamic> json) => StoreRecommendation(
    id: json['id'] ?? '',
    name: json['name'] ?? '',
    category: json['category'] ?? '',
    rating: (json['rating'] ?? 0).toDouble(),
    reviewCount: json['reviewCount'] ?? 0,
    latitude: (json['latitude'] ?? 0).toDouble(),
    longitude: (json['longitude'] ?? 0).toDouble(),
    distance: (json['distance'] ?? 0).toDouble(),
    mainImage: json['mainImage'],
    priceRange: json['priceRange'] != null 
        ? PriceRange.fromJson(json['priceRange']) 
        : null,
    tags: List<String>.from(json['tags'] ?? []),
    recommendReason: json['recommendReason'],
    matchScore: (json['matchScore'] ?? 0).toDouble(),
  );

  Map<String, dynamic> toJson() => {
    'id': id,
    'name': name,
    'category': category,
    'rating': rating,
    'reviewCount': reviewCount,
    'latitude': latitude,
    'longitude': longitude,
    'distance': distance,
    'mainImage': mainImage,
    'priceRange': priceRange?.toJson(),
    'tags': tags,
    'recommendReason': recommendReason,
    'matchScore': matchScore,
  };
}

class PriceRange {
  final double min;
  final double max;
  final String currency;

  PriceRange({
    required this.min,
    required this.max,
    this.currency = 'CNY',
  });

  factory PriceRange.fromJson(Map<String, dynamic> json) => PriceRange(
    min: (json['min'] ?? 0).toDouble(),
    max: (json['max'] ?? 0).toDouble(),
    currency: json['currency'] ?? 'CNY',
  );

  Map<String, dynamic> toJson() => {
    'min': min,
    'max': max,
    'currency': currency,
  };
}

class TrendingStore {
  final String id;
  final String name;
  final int rank;
  final double trendingScore;
  final double rating;
  final int weeklyVisits;

  TrendingStore({
    required this.id,
    required this.name,
    required this.rank,
    required this.trendingScore,
    required this.rating,
    required this.weeklyVisits,
  });

  factory TrendingStore.fromJson(Map<String, dynamic> json) => TrendingStore(
    id: json['id'] ?? '',
    name: json['name'] ?? '',
    rank: json['rank'] ?? 0,
    trendingScore: (json['trendingScore'] ?? 0).toDouble(),
    rating: (json['rating'] ?? 0).toDouble(),
    weeklyVisits: json['weeklyVisits'] ?? 0,
  );
}

class NewStoreOpening {
  final String id;
  final String name;
  final DateTime openingDate;
  final String category;
  final double distance;
  final bool hasOpeningPromotion;

  NewStoreOpening({
    required this.id,
    required this.name,
    required this.openingDate,
    required this.category,
    required this.distance,
    required this.hasOpeningPromotion,
  });

  factory NewStoreOpening.fromJson(Map<String, dynamic> json) => NewStoreOpening(
    id: json['id'] ?? '',
    name: json['name'] ?? '',
    openingDate: DateTime.parse(json['openingDate'] ?? DateTime.now().toIso8601String()),
    category: json['category'] ?? '',
    distance: (json['distance'] ?? 0).toDouble(),
    hasOpeningPromotion: json['hasOpeningPromotion'] ?? false,
  );
}

class StoreTourRoute {
  final List<RouteStop> stores;
  final double totalDistance;
  final Duration estimatedDuration;
  final String routePolyline;

  StoreTourRoute({
    required this.stores,
    required this.totalDistance,
    required this.estimatedDuration,
    required this.routePolyline,
  });

  factory StoreTourRoute.fromJson(Map<String, dynamic> json) => StoreTourRoute(
    stores: (json['stores'] as List? ?? [])
        .map((s) => RouteStop.fromJson(s))
        .toList(),
    totalDistance: (json['totalDistance'] ?? 0).toDouble(),
    estimatedDuration: Duration(minutes: json['estimatedDurationMinutes'] ?? 0),
    routePolyline: json['routePolyline'] ?? '',
  );
}

class RouteStop {
  final String storeId;
  final String storeName;
  final int sequence;
  final double latitude;
  final double longitude;
  final Duration estimatedArrival;

  RouteStop({
    required this.storeId,
    required this.storeName,
    required this.sequence,
    required this.latitude,
    required this.longitude,
    required this.estimatedArrival,
  });

  factory RouteStop.fromJson(Map<String, dynamic> json) => RouteStop(
    storeId: json['storeId'] ?? '',
    storeName: json['storeName'] ?? '',
    sequence: json['sequence'] ?? 0,
    latitude: (json['latitude'] ?? 0).toDouble(),
    longitude: (json['longitude'] ?? 0).toDouble(),
    estimatedArrival: Duration(minutes: json['estimatedArrivalMinutes'] ?? 0),
  );
}

class StoreCheckinResult {
  final String checkinId;
  final String storeId;
  final String storeName;
  final double latitude;
  final double longitude;
  final int earnedPoints;
  final int currentStreak;
  final bool isFirstVisit;
  final String? badgeEarned;

  StoreCheckinResult({
    required this.checkinId,
    required this.storeId,
    required this.storeName,
    required this.latitude,
    required this.longitude,
    required this.earnedPoints,
    required this.currentStreak,
    required this.isFirstVisit,
    this.badgeEarned,
  });

  factory StoreCheckinResult.fromJson(Map<String, dynamic> json) => StoreCheckinResult(
    checkinId: json['checkinId'] ?? '',
    storeId: json['storeId'] ?? '',
    storeName: json['storeName'] ?? '',
    latitude: (json['latitude'] ?? 0).toDouble(),
    longitude: (json['longitude'] ?? 0).toDouble(),
    earnedPoints: json['earnedPoints'] ?? 0,
    currentStreak: json['currentStreak'] ?? 0,
    isFirstVisit: json['isFirstVisit'] ?? false,
    badgeEarned: json['badgeEarned'],
  );
}

class StoreCheckinRecord {
  final String id;
  final String storeId;
  final String storeName;
  final DateTime checkInTime;
  final double latitude;
  final double longitude;
  final int earnedPoints;
  final bool isSynced;

  StoreCheckinRecord({
    String? id,
    required this.storeId,
    required this.storeName,
    required this.checkInTime,
    required this.latitude,
    required this.longitude,
    required this.earnedPoints,
    this.isSynced = false,
  }) : id = id ?? DateTime.now().millisecondsSinceEpoch.toString();

  factory StoreCheckinRecord.fromJson(Map<String, dynamic> json) => StoreCheckinRecord(
    id: json['id'],
    storeId: json['storeId'] ?? '',
    storeName: json['storeName'] ?? '',
    checkInTime: DateTime.parse(json['checkInTime'] ?? DateTime.now().toIso8601String()),
    latitude: (json['latitude'] ?? 0).toDouble(),
    longitude: (json['longitude'] ?? 0).toDouble(),
    earnedPoints: json['earnedPoints'] ?? 0,
    isSynced: json['isSynced'] ?? false,
  );

  Map<String, dynamic> toJson() => {
    'id': id,
    'storeId': storeId,
    'storeName': storeName,
    'checkInTime': checkInTime.toIso8601String(),
    'latitude': latitude,
    'longitude': longitude,
    'earnedPoints': earnedPoints,
    'isSynced': isSynced,
  };
}

class StoreDiscoveryStats {
  final int totalCheckins;
  final int uniqueStores;
  final int totalPointsEarned;
  final int currentStreak;
  final String favoriteCategory;

  StoreDiscoveryStats({
    required this.totalCheckins,
    required this.uniqueStores,
    required this.totalPointsEarned,
    required this.currentStreak,
    required this.favoriteCategory,
  });

  factory StoreDiscoveryStats.fromJson(Map<String, dynamic> json) => StoreDiscoveryStats(
    totalCheckins: json['totalCheckins'] ?? 0,
    uniqueStores: json['uniqueStores'] ?? 0,
    totalPointsEarned: json['totalPointsEarned'] ?? 0,
    currentStreak: json['currentStreak'] ?? 0,
    favoriteCategory: json['favoriteCategory'] ?? '',
  );

  factory StoreDiscoveryStats.empty() => StoreDiscoveryStats(
    totalCheckins: 0,
    uniqueStores: 0,
    totalPointsEarned: 0,
    currentStreak: 0,
    favoriteCategory: '',
  );
}

class StoreDiscoveryInfluencer {
  final String userId;
  final String nickname;
  final String? avatarUrl;
  final int rank;
  final int checkinCount;
  final int followerCount;
  final String? bio;

  StoreDiscoveryInfluencer({
    required this.userId,
    required this.nickname,
    this.avatarUrl,
    required this.rank,
    required this.checkinCount,
    required this.followerCount,
    this.bio,
  });

  factory StoreDiscoveryInfluencer.fromJson(Map<String, dynamic> json) => StoreDiscoveryInfluencer(
    userId: json['userId'] ?? '',
    nickname: json['nickname'] ?? '',
    avatarUrl: json['avatarUrl'],
    rank: json['rank'] ?? 0,
    checkinCount: json['checkinCount'] ?? 0,
    followerCount: json['followerCount'] ?? 0,
    bio: json['bio'],
  );
}

class DiscoveryNote {
  final String id;
  final String userId;
  final String storeId;
  final String content;
  final List<String> photoUrls;
  final List<String> videoUrls;
  final int likeCount;
  final int commentCount;
  final DateTime publishTime;
  final int? rating;

  DiscoveryNote({
    required this.id,
    required this.userId,
    required this.storeId,
    required this.content,
    required this.photoUrls,
    required this.videoUrls,
    required this.likeCount,
    required this.commentCount,
    required this.publishTime,
    this.rating,
  });

  factory DiscoveryNote.fromJson(Map<String, dynamic> json) => DiscoveryNote(
    id: json['id'] ?? '',
    userId: json['userId'] ?? '',
    storeId: json['storeId'] ?? '',
    content: json['content'] ?? '',
    photoUrls: List<String>.from(json['photoUrls'] ?? []),
    videoUrls: List<String>.from(json['videoUrls'] ?? []),
    likeCount: json['likeCount'] ?? 0,
    commentCount: json['commentCount'] ?? 0,
    publishTime: DateTime.parse(json['publishTime'] ?? DateTime.now().toIso8601String()),
    rating: json['rating'],
  );
}

class BestVisitTimeSuggestion {
  final String storeId;
  final List<TimeSlotRecommendation> recommendedTimes;
  final List<TimeSlotRecommendation> avoidTimes;

  BestVisitTimeSuggestion({
    required this.storeId,
    required this.recommendedTimes,
    required this.avoidTimes,
  });

  factory BestVisitTimeSuggestion.fromJson(Map<String, dynamic> json) => BestVisitTimeSuggestion(
    storeId: json['storeId'] ?? '',
    recommendedTimes: (json['recommendedTimes'] as List? ?? [])
        .map((t) => TimeSlotRecommendation.fromJson(t))
        .toList(),
    avoidTimes: (json['avoidTimes'] as List? ?? [])
        .map((t) => TimeSlotRecommendation.fromJson(t))
        .toList(),
  );

  factory BestVisitTimeSuggestion.empty(String storeId) => BestVisitTimeSuggestion(
    storeId: storeId,
    recommendedTimes: [],
    avoidTimes: [],
  );
}

class TimeSlotRecommendation {
  final int dayOfWeek;
  final int startHour;
  final int endHour;
  final CrowdLevel crowdLevel;
  final String reason;

  TimeSlotRecommendation({
    required this.dayOfWeek,
    required this.startHour,
    required this.endHour,
    required this.crowdLevel,
    required this.reason,
  });

  factory TimeSlotRecommendation.fromJson(Map<String, dynamic> json) => TimeSlotRecommendation(
    dayOfWeek: json['dayOfWeek'] ?? 1,
    startHour: json['startHour'] ?? 0,
    endHour: json['endHour'] ?? 0,
    crowdLevel: CrowdLevel.values.firstWhere(
      (e) => e.name == json['crowdLevel'],
      orElse: () => CrowdLevel.medium,
    ),
    reason: json['reason'] ?? '',
  );
}

class VisitBudgetEstimate {
  final double minAmount;
  final double maxAmount;
  final double averageAmount;
  final String currency;
  final List<String> includedItems;

  VisitBudgetEstimate({
    required this.minAmount,
    required this.maxAmount,
    required this.averageAmount,
    required this.currency,
    required this.includedItems,
  });

  factory VisitBudgetEstimate.fromJson(Map<String, dynamic> json) => VisitBudgetEstimate(
    minAmount: (json['minAmount'] ?? 0).toDouble(),
    maxAmount: (json['maxAmount'] ?? 0).toDouble(),
    averageAmount: (json['averageAmount'] ?? 0).toDouble(),
    currency: json['currency'] ?? 'CNY',
    includedItems: List<String>.from(json['includedItems'] ?? []),
  );

  factory VisitBudgetEstimate.empty() => VisitBudgetEstimate(
    minAmount: 0,
    maxAmount: 0,
    averageAmount: 0,
    currency: 'CNY',
    includedItems: [],
  );
}

class StoreDiscoveryGroup {
  final String id;
  final String name;
  final String storeId;
  final String creatorId;
  final DateTime plannedVisitTime;
  final int maxMembers;
  final int currentMembers;
  final GroupStatus status;
  final String? note;

  StoreDiscoveryGroup({
    required this.id,
    required this.name,
    required this.storeId,
    required this.creatorId,
    required this.plannedVisitTime,
    required this.maxMembers,
    required this.currentMembers,
    required this.status,
    this.note,
  });

  factory StoreDiscoveryGroup.fromJson(Map<String, dynamic> json) => StoreDiscoveryGroup(
    id: json['id'] ?? '',
    name: json['name'] ?? '',
    storeId: json['storeId'] ?? '',
    creatorId: json['creatorId'] ?? '',
    plannedVisitTime: DateTime.parse(json['plannedVisitTime'] ?? DateTime.now().toIso8601String()),
    maxMembers: json['maxMembers'] ?? 10,
    currentMembers: json['currentMembers'] ?? 1,
    status: GroupStatus.values.firstWhere(
      (e) => e.name == json['status'],
      orElse: () => GroupStatus.recruiting,
    ),
    note: json['note'],
  );
}
