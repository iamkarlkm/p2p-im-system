import 'dart:async';
import 'dart:math';

import 'package:flutter/foundation.dart';
import 'package:geolocator/geolocator.dart';

import '../../api/local_life_api.dart';
import '../../models/merchant.dart';
import '../../models/user.dart';
import '../geofence/service/geofence_service_manager.dart';

/// 智能探店发现服务
/// 
/// 提供：
/// - 个性化新店发现
/// - 热门探店榜单
/// - 探店路线规划
/// - 探店打卡
/// - 探店内容社区
class StoreDiscoveryService extends ChangeNotifier {
  static final StoreDiscoveryService _instance = StoreDiscoveryService._internal();
  factory StoreDiscoveryService() => _instance;
  StoreDiscoveryService._internal();

  final LocalLifeApi _api = LocalLifeApi();
  final GeofenceServiceManager _geofenceManager = GeofenceServiceManager();
  
  /// 推荐店铺列表
  List<StoreRecommendation> _recommendations = [];
  
  /// 热门榜单
  List<StoreRanking> _rankings = [];
  
  /// 探店路线
  List<DiscoveryRoute> _routes = [];
  
  /// 用户探店记录
  final List<StoreDiscoveryRecord> _discoveryRecords = [];
  
  /// 关注的新店
  final List<String> _watchedStoreIds = [];
  
  /// 是否正在加载
  bool _isLoading = false;
  
  /// 当前用户
  User? _currentUser;
  
  /// 最后推荐时间
  DateTime? _lastRecommendationTime;
  
  /// 事件流控制器
  final _eventController = StreamController<DiscoveryEvent>.broadcast();
  
  bool _initialized = false;

  // ==================== Getters ====================
  
  List<StoreRecommendation> get recommendations => List.unmodifiable(_recommendations);
  List<StoreRanking> get rankings => List.unmodifiable(_rankings);
  List<DiscoveryRoute> get routes => List.unmodifiable(_routes);
  List<StoreDiscoveryRecord> get discoveryRecords => List.unmodifiable(_discoveryRecords);
  bool get isLoading => _isLoading;
  Stream<DiscoveryEvent> get eventStream => _eventController.stream;

  // ==================== 初始化 ====================
  
  Future<void> initialize({User? user}) async {
    if (_initialized) return;
    
    _currentUser = user;
    
    // 加载用户偏好设置
    await _loadUserPreferences();
    
    _initialized = true;
    debugPrint('StoreDiscoveryService initialized');
  }
  
  void setUser(User? user) {
    _currentUser = user;
    notifyListeners();
  }

  // ==================== 店铺发现 ====================
  
  /// 获取个性化推荐店铺
  Future<List<StoreRecommendation>> fetchRecommendations({
    Position? location,
    int limit = 20,
    bool refresh = false,
  }) async {
    _isLoading = true;
    notifyListeners();
    
    try {
      Position? searchLocation = location;
      if (searchLocation == null) {
        searchLocation = await Geolocator.getCurrentPosition();
      }
      
      // 检查是否需要刷新（缓存30分钟）
      if (!refresh && 
          _lastRecommendationTime != null &&
          DateTime.now().difference(_lastRecommendationTime!).inMinutes < 30 &&
          _recommendations.isNotEmpty) {
        _isLoading = false;
        notifyListeners();
        return _recommendations;
      }
      
      final results = await _api.getStoreRecommendations(
        userId: _currentUser?.id,
        latitude: searchLocation.latitude,
        longitude: searchLocation.longitude,
        limit: limit,
        preferences: _getUserPreferences(),
      );
      
      _recommendations = results;
      _lastRecommendationTime = DateTime.now();
      
      return results;
    } catch (e) {
      debugPrint('Error fetching recommendations: $e');
      return [];
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }
  
  /// 获取热门榜单
  Future<List<StoreRanking>> fetchRankings({
    Position? location,
    RankingType type = RankingType.hot,
    String? category,
  }) async {
    try {
      Position? searchLocation = location;
      if (searchLocation == null) {
        searchLocation = await Geolocator.getCurrentPosition();
      }
      
      final results = await _api.getStoreRankings(
        type: type,
        latitude: searchLocation.latitude,
        longitude: searchLocation.longitude,
        category: category,
      );
      
      _rankings = results;
      notifyListeners();
      
      return results;
    } catch (e) {
      debugPrint('Error fetching rankings: $e');
      return [];
    }
  }
  
  /// 发现附近新店
  Future<List<StoreRecommendation>> discoverNewStores({
    Position? location,
    double radius = 5000,
  }) async {
    try {
      Position? searchLocation = location;
      if (searchLocation == null) {
        searchLocation = await Geolocator.getCurrentPosition();
      }
      
      return await _api.discoverNewStores(
        latitude: searchLocation.latitude,
        longitude: searchLocation.longitude,
        radius: radius,
        excludeIds: _discoveryRecords.map((r) => r.storeId).toList(),
      );
    } catch (e) {
      debugPrint('Error discovering new stores: $e');
      return [];
    }
  }

  // ==================== 探店路线规划 ====================
  
  /// 规划探店路线
  Future<DiscoveryRoute?> planDiscoveryRoute({
    required List<String> targetStoreIds,
    Position? startLocation,
    RouteOptimizeStrategy strategy = RouteOptimizeStrategy.shortest,
  }) async {
    try {
      Position? start = startLocation;
      if (start == null) {
        start = await Geolocator.getCurrentPosition();
      }
      
      final route = await _api.planDiscoveryRoute(
        startLat: start.latitude,
        startLng: start.longitude,
        storeIds: targetStoreIds,
        strategy: strategy,
      );
      
      _routes.add(route);
      notifyListeners();
      
      return route;
    } catch (e) {
      debugPrint('Error planning route: $e');
      return null;
    }
  }
  
  /// 获取推荐路线
  Future<List<DiscoveryRoute>> fetchRecommendedRoutes({
    Position? location,
    String? theme,
  }) async {
    try {
      Position? searchLocation = location;
      if (searchLocation == null) {
        searchLocation = await Geolocator.getCurrentPosition();
      }
      
      final routes = await _api.getRecommendedDiscoveryRoutes(
        latitude: searchLocation.latitude,
        longitude: searchLocation.longitude,
        theme: theme,
      );
      
      _routes = routes;
      notifyListeners();
      
      return routes;
    } catch (e) {
      debugPrint('Error fetching routes: $e');
      return [];
    }
  }

  // ==================== 探店打卡 ====================
  
  /// 打卡店铺
  Future<StoreDiscoveryRecord> checkInStore({
    required String storeId,
    required String storeName,
    String? photoPath,
    String? review,
    List<String>? tags,
    Position? location,
  }) async {
    final record = StoreDiscoveryRecord(
      id: 'disc_${DateTime.now().millisecondsSinceEpoch}',
      storeId: storeId,
      storeName: storeName,
      checkInTime: DateTime.now(),
      photoPath: photoPath,
      review: review,
      tags: tags,
      location: location != null
          ? DiscoveryLocation(
              latitude: location.latitude,
              longitude: location.longitude,
            )
          : null,
    );
    
    _discoveryRecords.insert(0, record);
    if (_discoveryRecords.length > 100) {
      _discoveryRecords.removeLast();
    }
    
    // 上报服务器
    await _api.recordStoreCheckIn(record);
    
    // 发送事件
    _eventController.add(DiscoveryEvent.checkIn(record));
    
    notifyListeners();
    debugPrint('Store check-in: $storeName');
    
    return record;
  }
  
  /// 自动打卡（基于地理围栏）
  Future<void> autoCheckIn(String storeId, String storeName) async {
    // 检查是否已经打过卡
    final alreadyChecked = _discoveryRecords.any(
      (r) => r.storeId == storeId &&
             DateTime.now().difference(r.checkInTime).inHours < 24,
    );
    
    if (alreadyChecked) return;
    
    final position = await Geolocator.getCurrentPosition();
    
    await checkInStore(
      storeId: storeId,
      storeName: storeName,
      location: position,
    );
  }

  // ==================== 关注新店 ====================
  
  /// 关注店铺开业提醒
  Future<void> watchStoreOpening(String storeId) async {
    if (_watchedStoreIds.contains(storeId)) return;
    
    _watchedStoreIds.add(storeId);
    await _api.watchStoreOpening(
      userId: _currentUser?.id ?? '',
      storeId: storeId,
    );
    
    notifyListeners();
  }
  
  /// 取消关注
  Future<void> unwatchStoreOpening(String storeId) async {
    _watchedStoreIds.remove(storeId);
    await _api.unwatchStoreOpening(
      userId: _currentUser?.id ?? '',
      storeId: storeId,
    );
    
    notifyListeners();
  }
  
  /// 是否已关注
  bool isWatchingStore(String storeId) {
    return _watchedStoreIds.contains(storeId);
  }

  // ==================== 探店时机推荐 ====================
  
  /// 获取最佳探店时机
  Future<VisitRecommendation> getBestVisitTime(String storeId) async {
    try {
      return await _api.getBestVisitTime(storeId);
    } catch (e) {
      debugPrint('Error getting best visit time: $e');
      return VisitRecommendation(
        storeId: storeId,
        recommendedTimes: [],
        avoidTimes: [],
        reason: '暂无推荐数据',
      );
    }
  }
  
  /// 获取预计等待时间
  Future<int> getEstimatedWaitTime(String storeId) async {
    try {
      return await _api.getEstimatedWaitTime(storeId);
    } catch (e) {
      return 0;
    }
  }

  // ==================== 内容社区 ====================
  
  /// 发布探店内容
  Future<DiscoveryPost> createPost({
    required String storeId,
    required String content,
    List<String>? photos,
    List<String>? videos,
    double? rating,
    List<String>? tags,
  }) async {
    final post = DiscoveryPost(
      id: 'post_${DateTime.now().millisecondsSinceEpoch}',
      storeId: storeId,
      authorId: _currentUser?.id ?? '',
      authorName: _currentUser?.nickname ?? '匿名用户',
      authorAvatar: _currentUser?.avatar,
      content: content,
      photos: photos,
      videos: videos,
      rating: rating,
      tags: tags,
      createdAt: DateTime.now(),
    );
    
    await _api.createDiscoveryPost(post);
    
    _eventController.add(DiscoveryEvent.postCreated(post));
    
    return post;
  }
  
  /// 获取探店内容
  Future<List<DiscoveryPost>> fetchDiscoveryPosts({
    String? storeId,
    String? authorId,
    int limit = 20,
    int offset = 0,
  }) async {
    try {
      return await _api.getDiscoveryPosts(
        storeId: storeId,
        authorId: authorId,
        limit: limit,
        offset: offset,
      );
    } catch (e) {
      debugPrint('Error fetching posts: $e');
      return [];
    }
  }

  // ==================== 组队探店 ====================
  
  /// 创建探店队伍
  Future<DiscoveryTeam> createTeam({
    required String storeId,
    required DateTime planTime,
    required int maxMembers,
    String? description,
  }) async {
    final team = DiscoveryTeam(
      id: 'team_${DateTime.now().millisecondsSinceEpoch}',
      storeId: storeId,
      creatorId: _currentUser?.id ?? '',
      creatorName: _currentUser?.nickname ?? '匿名用户',
      planTime: planTime,
      maxMembers: maxMembers,
      description: description,
      members: [
        TeamMember(
          userId: _currentUser?.id ?? '',
          nickname: _currentUser?.nickname ?? '匿名用户',
          avatar: _currentUser?.avatar,
          joinedAt: DateTime.now(),
          isCreator: true,
        ),
      ],
      createdAt: DateTime.now(),
    );
    
    await _api.createDiscoveryTeam(team);
    
    return team;
  }
  
  /// 加入队伍
  Future<void> joinTeam(String teamId) async {
    await _api.joinDiscoveryTeam(
      teamId: teamId,
      userId: _currentUser?.id ?? '',
      nickname: _currentUser?.nickname ?? '匿名用户',
      avatar: _currentUser?.avatar,
    );
  }

  // ==================== 辅助方法 ====================
  
  Map<String, dynamic> _getUserPreferences() {
    return {
      'priceRange': _currentUser?.pricePreference,
      'categories': _currentUser?.favoriteCategories,
      'avoidCrowded': true,
    };
  }
  
  Future<void> _loadUserPreferences() async {
    // 从本地加载用户偏好
    // 简化实现
  }

  @override
  void dispose() {
    _eventController.close();
    super.dispose();
  }
}

// ==================== 数据模型 ====================

/// 店铺推荐
class StoreRecommendation {
  final String id;
  final String name;
  final String address;
  final String category;
  final double latitude;
  final double longitude;
  final double distance;
  final double? rating;
  final int? reviewCount;
  final String? mainPhoto;
  final String? priceRange;
  final List<String>? tags;
  final String?推荐理由;
  final double matchScore;

  StoreRecommendation({
    required this.id,
    required this.name,
    required this.address,
    required this.category,
    required this.latitude,
    required this.longitude,
    required this.distance,
    this.rating,
    this.reviewCount,
    this.mainPhoto,
    this.priceRange,
    this.tags,
    this.推荐理由,
    required this.matchScore,
  });
}

/// 店铺榜单
class StoreRanking {
  final String id;
  final String name;
  final int rank;
  final double score;
  final RankingType type;
  final String? trend; // up, down, stable
  final String? category;

  StoreRanking({
    required this.id,
    required this.name,
    required this.rank,
    required this.score,
    required this.type,
    this.trend,
    this.category,
  });
}

/// 榜单类型
enum RankingType {
  hot,        // 热门榜
  newStore,   // 新店榜
  rating,     // 好评榜
  taste,      // 口味榜
  service,    // 服务榜
  environment, // 环境榜
}

/// 探店路线
class DiscoveryRoute {
  final String id;
  final String name;
  final String? description;
  final List<RouteStop> stops;
  final double totalDistance;
  final int estimatedDuration;
  final String? theme;
  final String? coverImage;

  DiscoveryRoute({
    required this.id,
    required this.name,
    this.description,
    required this.stops,
    required this.totalDistance,
    required this.estimatedDuration,
    this.theme,
    this.coverImage,
  });
}

/// 路线站点
class RouteStop {
  final String storeId;
  final String storeName;
  final int order;
  final double distanceFromPrevious;
  final int estimatedStayMinutes;
  final String? recommendation;

  RouteStop({
    required this.storeId,
    required this.storeName,
    required this.order,
    required this.distanceFromPrevious,
    required this.estimatedStayMinutes,
    this.recommendation,
  });
}

/// 路线优化策略
enum RouteOptimizeStrategy {
  shortest,   // 最短距离
  fastest,    // 最短时间
  rating,     // 最高评分优先
  balanced,   // 平衡型
}

/// 探店记录
class StoreDiscoveryRecord {
  final String id;
  final String storeId;
  final String storeName;
  final DateTime checkInTime;
  final String? photoPath;
  final String? review;
  final List<String>? tags;
  final DiscoveryLocation? location;

  StoreDiscoveryRecord({
    required this.id,
    required this.storeId,
    required this.storeName,
    required this.checkInTime,
    this.photoPath,
    this.review,
    this.tags,
    this.location,
  });
}

/// 探店位置
class DiscoveryLocation {
  final double latitude;
  final double longitude;

  DiscoveryLocation({
    required this.latitude,
    required this.longitude,
  });
}

/// 访问推荐
class VisitRecommendation {
  final String storeId;
  final List<RecommendedTimeSlot> recommendedTimes;
  final List<RecommendedTimeSlot> avoidTimes;
  final String reason;

  VisitRecommendation({
    required this.storeId,
    required this.recommendedTimes,
    required this.avoidTimes,
    required this.reason,
  });
}

/// 推荐时段
class RecommendedTimeSlot {
  final String dayOfWeek;
  final String timeRange;
  final int expectedCrowdLevel;

  RecommendedTimeSlot({
    required this.dayOfWeek,
    required this.timeRange,
    required this.expectedCrowdLevel,
  });
}

/// 探店内容
class DiscoveryPost {
  final String id;
  final String storeId;
  final String authorId;
  final String authorName;
  final String? authorAvatar;
  final String content;
  final List<String>? photos;
  final List<String>? videos;
  final double? rating;
  final List<String>? tags;
  final DateTime createdAt;
  int likeCount;
  int commentCount;

  DiscoveryPost({
    required this.id,
    required this.storeId,
    required this.authorId,
    required this.authorName,
    this.authorAvatar,
    required this.content,
    this.photos,
    this.videos,
    this.rating,
    this.tags,
    required this.createdAt,
    this.likeCount = 0,
    this.commentCount = 0,
  });
}

/// 探店队伍
class DiscoveryTeam {
  final String id;
  final String storeId;
  final String creatorId;
  final String creatorName;
  final DateTime planTime;
  final int maxMembers;
  final String? description;
  final List<TeamMember> members;
  final DateTime createdAt;

  DiscoveryTeam({
    required this.id,
    required this.storeId,
    required this.creatorId,
    required this.creatorName,
    required this.planTime,
    required this.maxMembers,
    this.description,
    required this.members,
    required this.createdAt,
  });
}

/// 队员
class TeamMember {
  final String userId;
  final String nickname;
  final String? avatar;
  final DateTime joinedAt;
  final bool isCreator;

  TeamMember({
    required this.userId,
    required this.nickname,
    this.avatar,
    required this.joinedAt,
    this.isCreator = false,
  });
}

/// 探店事件
sealed class DiscoveryEvent {
  const DiscoveryEvent();
  factory DiscoveryEvent.checkIn(StoreDiscoveryRecord record) = DiscoveryCheckInEvent;
  factory DiscoveryEvent.postCreated(DiscoveryPost post) = DiscoveryPostCreatedEvent;
}

class DiscoveryCheckInEvent extends DiscoveryEvent {
  final StoreDiscoveryRecord record;
  const DiscoveryCheckInEvent(this.record);
}

class DiscoveryPostCreatedEvent extends DiscoveryEvent {
  final DiscoveryPost post;
  const DiscoveryPostCreatedEvent(this.post);
}
