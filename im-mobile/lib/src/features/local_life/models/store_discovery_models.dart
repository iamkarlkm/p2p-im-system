// 探店发现数据模型
// 生成时间: 2026-03-28 21:40

import 'dart:convert';

/// 探店发现相关数据模型集合
class StoreDiscoveryModels {
  // 防止实例化
  StoreDiscoveryModels._();
}

/// 新店模型
class NewStore {
  final String id;
  final String name;
  final String category;
  final String? description;
  final double rating;
  final int reviewCount;
  final double latitude;
  final double longitude;
  final String address;
  final String? phone;
  final String? mainImage;
  final List<String> images;
  final double? averagePrice;
  final String? openingHours;
  final DateTime openingDate;
  final List<String> tags;
  final bool hasPromotion;
  final double distance;

  NewStore({
    required this.id,
    required this.name,
    required this.category,
    this.description,
    required this.rating,
    required this.reviewCount,
    required this.latitude,
    required this.longitude,
    required this.address,
    this.phone,
    this.mainImage,
    this.images = const [],
    this.averagePrice,
    this.openingHours,
    required this.openingDate,
    this.tags = const [],
    this.hasPromotion = false,
    this.distance = 0,
  });

  factory NewStore.fromJson(Map<String, dynamic> json) {
    return NewStore(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      category: json['category'] ?? '',
      description: json['description'],
      rating: (json['rating'] ?? 0.0).toDouble(),
      reviewCount: json['reviewCount'] ?? 0,
      latitude: (json['latitude'] ?? 0.0).toDouble(),
      longitude: (json['longitude'] ?? 0.0).toDouble(),
      address: json['address'] ?? '',
      phone: json['phone'],
      mainImage: json['mainImage'],
      images: List<String>.from(json['images'] ?? []),
      averagePrice: json['averagePrice']?.toDouble(),
      openingHours: json['openingHours'],
      openingDate: DateTime.parse(json['openingDate'] ?? DateTime.now().toIso8601String()),
      tags: List<String>.from(json['tags'] ?? []),
      hasPromotion: json['hasPromotion'] ?? false,
      distance: (json['distance'] ?? 0.0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'category': category,
      'description': description,
      'rating': rating,
      'reviewCount': reviewCount,
      'latitude': latitude,
      'longitude': longitude,
      'address': address,
      'phone': phone,
      'mainImage': mainImage,
      'images': images,
      'averagePrice': averagePrice,
      'openingHours': openingHours,
      'openingDate': openingDate.toIso8601String(),
      'tags': tags,
      'hasPromotion': hasPromotion,
      'distance': distance,
    };
  }
}

/// 探店达人模型
class StoreExplorer {
  final String userId;
  final String nickname;
  final String? avatar;
  final int level;
  final int experience;
  final int totalCheckins;
  final int uniqueStores;
  final int followers;
  final int following;
  final String? bio;
  final bool isVerified;
  final List<String> badges;
  final List<ExplorerStats> monthlyStats;

  StoreExplorer({
    required this.userId,
    required this.nickname,
    this.avatar,
    required this.level,
    required this.experience,
    required this.totalCheckins,
    required this.uniqueStores,
    required this.followers,
    required this.following,
    this.bio,
    this.isVerified = false,
    this.badges = const [],
    this.monthlyStats = const [],
  });

  factory StoreExplorer.fromJson(Map<String, dynamic> json) {
    return StoreExplorer(
      userId: json['userId'] ?? '',
      nickname: json['nickname'] ?? '',
      avatar: json['avatar'],
      level: json['level'] ?? 1,
      experience: json['experience'] ?? 0,
      totalCheckins: json['totalCheckins'] ?? 0,
      uniqueStores: json['uniqueStores'] ?? 0,
      followers: json['followers'] ?? 0,
      following: json['following'] ?? 0,
      bio: json['bio'],
      isVerified: json['isVerified'] ?? false,
      badges: List<String>.from(json['badges'] ?? []),
      monthlyStats: (json['monthlyStats'] as List? ?? [])
          .map((e) => ExplorerStats.fromJson(e))
          .toList(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'nickname': nickname,
      'avatar': avatar,
      'level': level,
      'experience': experience,
      'totalCheckins': totalCheckins,
      'uniqueStores': uniqueStores,
      'followers': followers,
      'following': following,
      'bio': bio,
      'isVerified': isVerified,
      'badges': badges,
      'monthlyStats': monthlyStats.map((e) => e.toJson()).toList(),
    };
  }
}

/// 达人月度统计
class ExplorerStats {
  final int year;
  final int month;
  final int checkinCount;
  final int newStores;
  final int notesPublished;
  final int likesReceived;

  ExplorerStats({
    required this.year,
    required this.month,
    required this.checkinCount,
    required this.newStores,
    required this.notesPublished,
    required this.likesReceived,
  });

  factory ExplorerStats.fromJson(Map<String, dynamic> json) {
    return ExplorerStats(
      year: json['year'] ?? 2026,
      month: json['month'] ?? 1,
      checkinCount: json['checkinCount'] ?? 0,
      newStores: json['newStores'] ?? 0,
      notesPublished: json['notesPublished'] ?? 0,
      likesReceived: json['likesReceived'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'year': year,
      'month': month,
      'checkinCount': checkinCount,
      'newStores': newStores,
      'notesPublished': notesPublished,
      'likesReceived': likesReceived,
    };
  }
}

/// 探店笔记模型
class ExplorerNote {
  final String id;
  final String userId;
  final String storeId;
  final String storeName;
  final String content;
  final List<String> images;
  final String? videoUrl;
  final int rating;
  final double? spentAmount;
  final List<String> tags;
  final DateTime publishTime;
  final int likeCount;
  final int commentCount;
  final int shareCount;
  final bool isLiked;
  final bool isCollected;
  final List<NoteComment> topComments;

  ExplorerNote({
    required this.id,
    required this.userId,
    required this.storeId,
    required this.storeName,
    required this.content,
    this.images = const [],
    this.videoUrl,
    required this.rating,
    this.spentAmount,
    this.tags = const [],
    required this.publishTime,
    this.likeCount = 0,
    this.commentCount = 0,
    this.shareCount = 0,
    this.isLiked = false,
    this.isCollected = false,
    this.topComments = const [],
  });

  factory ExplorerNote.fromJson(Map<String, dynamic> json) {
    return ExplorerNote(
      id: json['id'] ?? '',
      userId: json['userId'] ?? '',
      storeId: json['storeId'] ?? '',
      storeName: json['storeName'] ?? '',
      content: json['content'] ?? '',
      images: List<String>.from(json['images'] ?? []),
      videoUrl: json['videoUrl'],
      rating: json['rating'] ?? 5,
      spentAmount: json['spentAmount']?.toDouble(),
      tags: List<String>.from(json['tags'] ?? []),
      publishTime: DateTime.parse(json['publishTime'] ?? DateTime.now().toIso8601String()),
      likeCount: json['likeCount'] ?? 0,
      commentCount: json['commentCount'] ?? 0,
      shareCount: json['shareCount'] ?? 0,
      isLiked: json['isLiked'] ?? false,
      isCollected: json['isCollected'] ?? false,
      topComments: (json['topComments'] as List? ?? [])
          .map((e) => NoteComment.fromJson(e))
          .toList(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'storeId': storeId,
      'storeName': storeName,
      'content': content,
      'images': images,
      'videoUrl': videoUrl,
      'rating': rating,
      'spentAmount': spentAmount,
      'tags': tags,
      'publishTime': publishTime.toIso8601String(),
      'likeCount': likeCount,
      'commentCount': commentCount,
      'shareCount': shareCount,
      'isLiked': isLiked,
      'isCollected': isCollected,
      'topComments': topComments.map((e) => e.toJson()).toList(),
    };
  }
}

/// 笔记评论
class NoteComment {
  final String id;
  final String userId;
  final String nickname;
  final String? avatar;
  final String content;
  final DateTime createTime;
  final int likeCount;
  final bool isLiked;
  final NoteComment? replyTo;

  NoteComment({
    required this.id,
    required this.userId,
    required this.nickname,
    this.avatar,
    required this.content,
    required this.createTime,
    this.likeCount = 0,
    this.isLiked = false,
    this.replyTo,
  });

  factory NoteComment.fromJson(Map<String, dynamic> json) {
    return NoteComment(
      id: json['id'] ?? '',
      userId: json['userId'] ?? '',
      nickname: json['nickname'] ?? '',
      avatar: json['avatar'],
      content: json['content'] ?? '',
      createTime: DateTime.parse(json['createTime'] ?? DateTime.now().toIso8601String()),
      likeCount: json['likeCount'] ?? 0,
      isLiked: json['isLiked'] ?? false,
      replyTo: json['replyTo'] != null ? NoteComment.fromJson(json['replyTo']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'nickname': nickname,
      'avatar': avatar,
      'content': content,
      'createTime': createTime.toIso8601String(),
      'likeCount': likeCount,
      'isLiked': isLiked,
      'replyTo': replyTo?.toJson(),
    };
  }
}

/// 探店路线模型
class DiscoveryRoute {
  final String id;
  final String name;
  final String? description;
  final String creatorId;
  final String creatorName;
  final List<RoutePoint> points;
  final double totalDistance;
  final Duration estimatedTime;
  final int estimatedStores;
  final double estimatedCost;
  final List<String> tags;
  final int useCount;
  final int likeCount;
  final DateTime createTime;
  final bool isPublic;

  DiscoveryRoute({
    required this.id,
    required this.name,
    this.description,
    required this.creatorId,
    required this.creatorName,
    required this.points,
    required this.totalDistance,
    required this.estimatedTime,
    required this.estimatedStores,
    required this.estimatedCost,
    this.tags = const [],
    this.useCount = 0,
    this.likeCount = 0,
    required this.createTime,
    this.isPublic = true,
  });

  factory DiscoveryRoute.fromJson(Map<String, dynamic> json) {
    return DiscoveryRoute(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      description: json['description'],
      creatorId: json['creatorId'] ?? '',
      creatorName: json['creatorName'] ?? '',
      points: (json['points'] as List? ?? [])
          .map((e) => RoutePoint.fromJson(e))
          .toList(),
      totalDistance: (json['totalDistance'] ?? 0.0).toDouble(),
      estimatedTime: Duration(minutes: json['estimatedTimeMinutes'] ?? 0),
      estimatedStores: json['estimatedStores'] ?? 0,
      estimatedCost: (json['estimatedCost'] ?? 0.0).toDouble(),
      tags: List<String>.from(json['tags'] ?? []),
      useCount: json['useCount'] ?? 0,
      likeCount: json['likeCount'] ?? 0,
      createTime: DateTime.parse(json['createTime'] ?? DateTime.now().toIso8601String()),
      isPublic: json['isPublic'] ?? true,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'description': description,
      'creatorId': creatorId,
      'creatorName': creatorName,
      'points': points.map((e) => e.toJson()).toList(),
      'totalDistance': totalDistance,
      'estimatedTimeMinutes': estimatedTime.inMinutes,
      'estimatedStores': estimatedStores,
      'estimatedCost': estimatedCost,
      'tags': tags,
      'useCount': useCount,
      'likeCount': likeCount,
      'createTime': createTime.toIso8601String(),
      'isPublic': isPublic,
    };
  }
}

/// 路线点位
class RoutePoint {
  final String storeId;
  final String storeName;
  final double latitude;
  final double longitude;
  final int sequence;
  final Duration estimatedArrival;
  final String? recommendDish;
  final String? note;

  RoutePoint({
    required this.storeId,
    required this.storeName,
    required this.latitude,
    required this.longitude,
    required this.sequence,
    required this.estimatedArrival,
    this.recommendDish,
    this.note,
  });

  factory RoutePoint.fromJson(Map<String, dynamic> json) {
    return RoutePoint(
      storeId: json['storeId'] ?? '',
      storeName: json['storeName'] ?? '',
      latitude: (json['latitude'] ?? 0.0).toDouble(),
      longitude: (json['longitude'] ?? 0.0).toDouble(),
      sequence: json['sequence'] ?? 0,
      estimatedArrival: Duration(minutes: json['estimatedArrivalMinutes'] ?? 0),
      recommendDish: json['recommendDish'],
      note: json['note'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'storeId': storeId,
      'storeName': storeName,
      'latitude': latitude,
      'longitude': longitude,
      'sequence': sequence,
      'estimatedArrivalMinutes': estimatedArrival.inMinutes,
      'recommendDish': recommendDish,
      'note': note,
    };
  }
}

/// 探店挑战模型
class DiscoveryChallenge {
  final String id;
  final String title;
  final String description;
  final String? coverImage;
  final DateTime startTime;
  final DateTime endTime;
  final ChallengeType type;
  final List<ChallengeTask> tasks;
  final int participantCount;
  final int completedCount;
  final List<Reward> rewards;
  final bool isJoined;
  final double progress;

  DiscoveryChallenge({
    required this.id,
    required this.title,
    required this.description,
    this.coverImage,
    required this.startTime,
    required this.endTime,
    required this.type,
    required this.tasks,
    this.participantCount = 0,
    this.completedCount = 0,
    this.rewards = const [],
    this.isJoined = false,
    this.progress = 0.0,
  });

  factory DiscoveryChallenge.fromJson(Map<String, dynamic> json) {
    return DiscoveryChallenge(
      id: json['id'] ?? '',
      title: json['title'] ?? '',
      description: json['description'] ?? '',
      coverImage: json['coverImage'],
      startTime: DateTime.parse(json['startTime'] ?? DateTime.now().toIso8601String()),
      endTime: DateTime.parse(json['endTime'] ?? DateTime.now().toIso8601String()),
      type: ChallengeType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => ChallengeType.checkin,
      ),
      tasks: (json['tasks'] as List? ?? [])
          .map((e) => ChallengeTask.fromJson(e))
          .toList(),
      participantCount: json['participantCount'] ?? 0,
      completedCount: json['completedCount'] ?? 0,
      rewards: (json['rewards'] as List? ?? [])
          .map((e) => Reward.fromJson(e))
          .toList(),
      isJoined: json['isJoined'] ?? false,
      progress: (json['progress'] ?? 0.0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'description': description,
      'coverImage': coverImage,
      'startTime': startTime.toIso8601String(),
      'endTime': endTime.toIso8601String(),
      'type': type.name,
      'tasks': tasks.map((e) => e.toJson()).toList(),
      'participantCount': participantCount,
      'completedCount': completedCount,
      'rewards': rewards.map((e) => e.toJson()).toList(),
      'isJoined': isJoined,
      'progress': progress,
    };
  }
}

enum ChallengeType {
  checkin,      // 打卡挑战
  review,       // 评价挑战
  photo,        // 拍照挑战
  route,        // 路线挑战
  social,       // 社交挑战
}

/// 挑战任务
class ChallengeTask {
  final String id;
  final String title;
  final String description;
  final TaskType type;
  final int targetCount;
  final int currentCount;
  final bool isCompleted;
  final DateTime? completedTime;

  ChallengeTask({
    required this.id,
    required this.title,
    required this.description,
    required this.type,
    required this.targetCount,
    this.currentCount = 0,
    this.isCompleted = false,
    this.completedTime,
  });

  factory ChallengeTask.fromJson(Map<String, dynamic> json) {
    return ChallengeTask(
      id: json['id'] ?? '',
      title: json['title'] ?? '',
      description: json['description'] ?? '',
      type: TaskType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => TaskType.checkin,
      ),
      targetCount: json['targetCount'] ?? 1,
      currentCount: json['currentCount'] ?? 0,
      isCompleted: json['isCompleted'] ?? false,
      completedTime: json['completedTime'] != null 
          ? DateTime.parse(json['completedTime']) 
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'description': description,
      'type': type.name,
      'targetCount': targetCount,
      'currentCount': currentCount,
      'isCompleted': isCompleted,
      'completedTime': completedTime?.toIso8601String(),
    };
  }
}

enum TaskType {
  checkin,      // 打卡
  review,       // 评价
  photo,        // 拍照
  share,        // 分享
  follow,       // 关注
  invite,       // 邀请
}

/// 奖励
class Reward {
  final String id;
  final RewardType type;
  final String name;
  final String? description;
  final String? iconUrl;
  final double? value;
  final bool isClaimed;

  Reward({
    required this.id,
    required this.type,
    required this.name,
    this.description,
    this.iconUrl,
    this.value,
    this.isClaimed = false,
  });

  factory Reward.fromJson(Map<String, dynamic> json) {
    return Reward(
      id: json['id'] ?? '',
      type: RewardType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => RewardType.points,
      ),
      name: json['name'] ?? '',
      description: json['description'],
      iconUrl: json['iconUrl'],
      value: json['value']?.toDouble(),
      isClaimed: json['isClaimed'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'type': type.name,
      'name': name,
      'description': description,
      'iconUrl': iconUrl,
      'value': value,
      'isClaimed': isClaimed,
    };
  }
}

enum RewardType {
  points,       // 积分
  badge,        // 徽章
  coupon,       // 优惠券
  vip,          // VIP体验
  gift,         // 实物礼品
}

/// 探店筛选条件
class DiscoveryFilter {
  final List<String> categories;
  final PriceRangeFilter? priceRange;
  final double? minRating;
  final SortType sortType;
  final double? maxDistance;
  final List<String> tags;
  final bool openNow;
  final bool hasPromotion;

  DiscoveryFilter({
    this.categories = const [],
    this.priceRange,
    this.minRating,
    this.sortType = SortType.recommended,
    this.maxDistance,
    this.tags = const [],
    this.openNow = false,
    this.hasPromotion = false,
  });

  Map<String, dynamic> toQueryParams() {
    return {
      if (categories.isNotEmpty) 'categories': categories.join(','),
      if (priceRange != null) 'minPrice': priceRange!.min,
      if (priceRange != null) 'maxPrice': priceRange!.max,
      if (minRating != null) 'minRating': minRating,
      'sortType': sortType.name,
      if (maxDistance != null) 'maxDistance': maxDistance,
      if (tags.isNotEmpty) 'tags': tags.join(','),
      if (openNow) 'openNow': 'true',
      if (hasPromotion) 'hasPromotion': 'true',
    };
  }
}

class PriceRangeFilter {
  final double min;
  final double max;

  PriceRangeFilter({required this.min, required this.max});
}

enum SortType {
  recommended,  // 推荐
  distance,     // 距离
  rating,       // 评分
  popularity,   // 人气
  newest,       // 最新
}

/// 探店通知设置
class DiscoveryNotificationSettings {
  bool newStoreAlert;
  bool trendingStoreAlert;
  bool challengeReminder;
  bool dailyRecommendation;
  double alertRadius;
  List<String> interestedCategories;
  TimeOfDay? dailyPushTime;

  DiscoveryNotificationSettings({
    this.newStoreAlert = true,
    this.trendingStoreAlert = true,
    this.challengeReminder = true,
    this.dailyRecommendation = true,
    this.alertRadius = 3000,
    this.interestedCategories = const [],
    this.dailyPushTime,
  });

  factory DiscoveryNotificationSettings.fromJson(Map<String, dynamic> json) {
    return DiscoveryNotificationSettings(
      newStoreAlert: json['newStoreAlert'] ?? true,
      trendingStoreAlert: json['trendingStoreAlert'] ?? true,
      challengeReminder: json['challengeReminder'] ?? true,
      dailyRecommendation: json['dailyRecommendation'] ?? true,
      alertRadius: (json['alertRadius'] ?? 3000).toDouble(),
      interestedCategories: List<String>.from(json['interestedCategories'] ?? []),
      dailyPushTime: json['dailyPushTime'] != null 
          ? TimeOfDay(
              hour: json['dailyPushTime']['hour'] ?? 9,
              minute: json['dailyPushTime']['minute'] ?? 0,
            )
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'newStoreAlert': newStoreAlert,
      'trendingStoreAlert': trendingStoreAlert,
      'challengeReminder': challengeReminder,
      'dailyRecommendation': dailyRecommendation,
      'alertRadius': alertRadius,
      'interestedCategories': interestedCategories,
      'dailyPushTime': dailyPushTime != null ? {
        'hour': dailyPushTime!.hour,
        'minute': dailyPushTime!.minute,
      } : null,
    };
  }
}

class TimeOfDay {
  final int hour;
  final int minute;

  TimeOfDay({required this.hour, required this.minute});
}
