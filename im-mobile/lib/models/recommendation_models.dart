/// 推荐信息流模型定义
/// 包含推荐项、召回候选、排序特征等相关数据模型
/// 
/// Author: IM Development Team
/// Version: 1.0.0
/// Since: 2026-03-28

// ==================== 推荐项模型 ====================

/// 推荐项
class RecommendationItem {
  /// 推荐项唯一ID
  final String itemId;
  
  /// 推荐项类型: POI, ACTIVITY, COUPON, GROUP, EVENT, CONTENT
  final String itemType;
  
  /// 业务ID
  final String businessId;
  
  /// 标题
  final String title;
  
  /// 副标题
  final String? subtitle;
  
  /// 主图URL
  final String? mainImage;
  
  /// 图片列表
  final List<String>? imageList;
  
  /// 缩略图URL
  final String? thumbnailUrl;
  
  /// 经度
  final double? longitude;
  
  /// 纬度
  final double? latitude;
  
  /// 地址
  final String? address;
  
  /// 距离（米）
  final int? distance;
  
  /// 距离显示文本
  final String? distanceText;
  
  /// 分类名称
  final String? categoryName;
  
  /// 评分
  final double? rating;
  
  /// 评分人数
  final int? ratingCount;
  
  /// 价格信息
  final PriceInfo? priceInfo;
  
  /// 标签列表
  final List<String>? tags;
  
  /// 推荐理由
  final String? recommendReason;
  
  /// 推荐标签
  final List<String>? recommendTags;
  
  /// 召回来源: GEO, HOT, CF_USER, CF_ITEM, VECTOR, SOCIAL, REALTIME
  final String? recallSource;
  
  /// 商户信息
  final MerchantInfo? merchantInfo;
  
  /// 活动信息
  final ActivityInfo? activityInfo;
  
  /// 优惠券信息
  final CouponInfo? couponInfo;
  
  /// 社交信息
  final SocialInfo? socialInfo;
  
  /// 场景标签
  final List<String>? sceneTags;
  
  /// 是否置顶
  final bool? isPinned;
  
  /// 是否推广
  final bool? isPromoted;
  
  /// 卡片样式类型
  final String? cardStyle;
  
  /// 发布时间
  final DateTime? publishTime;
  
  /// 扩展数据
  final Map<String, dynamic>? extraData;

  RecommendationItem({
    required this.itemId,
    required this.itemType,
    required this.businessId,
    required this.title,
    this.subtitle,
    this.mainImage,
    this.imageList,
    this.thumbnailUrl,
    this.longitude,
    this.latitude,
    this.address,
    this.distance,
    this.distanceText,
    this.categoryName,
    this.rating,
    this.ratingCount,
    this.priceInfo,
    this.tags,
    this.recommendReason,
    this.recommendTags,
    this.recallSource,
    this.merchantInfo,
    this.activityInfo,
    this.couponInfo,
    this.socialInfo,
    this.sceneTags,
    this.isPinned,
    this.isPromoted,
    this.cardStyle,
    this.publishTime,
    this.extraData,
  });

  factory RecommendationItem.fromJson(Map<String, dynamic> json) {
    return RecommendationItem(
      itemId: json['itemId'],
      itemType: json['itemType'],
      businessId: json['businessId'],
      title: json['title'],
      subtitle: json['subtitle'],
      mainImage: json['mainImage'],
      imageList: json['imageList'] != null 
          ? List<String>.from(json['imageList']) 
          : null,
      thumbnailUrl: json['thumbnailUrl'],
      longitude: json['longitude']?.toDouble(),
      latitude: json['latitude']?.toDouble(),
      address: json['address'],
      distance: json['distance'],
      distanceText: json['distanceText'],
      categoryName: json['categoryName'],
      rating: json['rating']?.toDouble(),
      ratingCount: json['ratingCount'],
      priceInfo: json['priceInfo'] != null 
          ? PriceInfo.fromJson(json['priceInfo']) 
          : null,
      tags: json['tags'] != null ? List<String>.from(json['tags']) : null,
      recommendReason: json['recommendReason'],
      recommendTags: json['recommendTags'] != null 
          ? List<String>.from(json['recommendTags']) 
          : null,
      recallSource: json['recallSource'],
      merchantInfo: json['merchantInfo'] != null 
          ? MerchantInfo.fromJson(json['merchantInfo']) 
          : null,
      activityInfo: json['activityInfo'] != null 
          ? ActivityInfo.fromJson(json['activityInfo']) 
          : null,
      couponInfo: json['couponInfo'] != null 
          ? CouponInfo.fromJson(json['couponInfo']) 
          : null,
      socialInfo: json['socialInfo'] != null 
          ? SocialInfo.fromJson(json['socialInfo']) 
          : null,
      sceneTags: json['sceneTags'] != null 
          ? List<String>.from(json['sceneTags']) 
          : null,
      isPinned: json['isPinned'],
      isPromoted: json['isPromoted'],
      cardStyle: json['cardStyle'],
      publishTime: json['publishTime'] != null 
          ? DateTime.parse(json['publishTime']) 
          : null,
      extraData: json['extraData'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'itemId': itemId,
      'itemType': itemType,
      'businessId': businessId,
      'title': title,
      'subtitle': subtitle,
      'mainImage': mainImage,
      'imageList': imageList,
      'thumbnailUrl': thumbnailUrl,
      'longitude': longitude,
      'latitude': latitude,
      'address': address,
      'distance': distance,
      'distanceText': distanceText,
      'categoryName': categoryName,
      'rating': rating,
      'ratingCount': ratingCount,
      'priceInfo': priceInfo?.toJson(),
      'tags': tags,
      'recommendReason': recommendReason,
      'recommendTags': recommendTags,
      'recallSource': recallSource,
      'merchantInfo': merchantInfo?.toJson(),
      'activityInfo': activityInfo?.toJson(),
      'couponInfo': couponInfo?.toJson(),
      'socialInfo': socialInfo?.toJson(),
      'sceneTags': sceneTags,
      'isPinned': isPinned,
      'isPromoted': isPromoted,
      'cardStyle': cardStyle,
      'publishTime': publishTime?.toIso8601String(),
      'extraData': extraData,
    };
  }
}

// ==================== 价格信息 ====================

/// 价格信息
class PriceInfo {
  /// 原价
  final double? originalPrice;
  
  /// 现价
  final double? currentPrice;
  
  /// 人均消费
  final double? avgPrice;
  
  /// 折扣信息
  final String? discountInfo;
  
  /// 价格描述
  final String? priceDescription;
  
  /// 是否有优惠
  final bool? hasDiscount;

  PriceInfo({
    this.originalPrice,
    this.currentPrice,
    this.avgPrice,
    this.discountInfo,
    this.priceDescription,
    this.hasDiscount,
  });

  factory PriceInfo.fromJson(Map<String, dynamic> json) {
    return PriceInfo(
      originalPrice: json['originalPrice']?.toDouble(),
      currentPrice: json['currentPrice']?.toDouble(),
      avgPrice: json['avgPrice']?.toDouble(),
      discountInfo: json['discountInfo'],
      priceDescription: json['priceDescription'],
      hasDiscount: json['hasDiscount'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'originalPrice': originalPrice,
      'currentPrice': currentPrice,
      'avgPrice': avgPrice,
      'discountInfo': discountInfo,
      'priceDescription': priceDescription,
      'hasDiscount': hasDiscount,
    };
  }

  /// 获取显示价格
  String get displayPrice {
    if (currentPrice != null) {
      return '¥${currentPrice!.toStringAsFixed(0)}';
    } else if (avgPrice != null) {
      return '人均¥${avgPrice!.toStringAsFixed(0)}';
    }
    return '';
  }
}

// ==================== 商户信息 ====================

/// 商户信息
class MerchantInfo {
  /// 商户ID
  final String merchantId;
  
  /// 商户名称
  final String merchantName;
  
  /// 商户LOGO
  final String? merchantLogo;
  
  /// 商户等级
  final int? merchantLevel;
  
  /// 是否认证
  final bool? isVerified;
  
  /// 总评分
  final double? overallRating;

  MerchantInfo({
    required this.merchantId,
    required this.merchantName,
    this.merchantLogo,
    this.merchantLevel,
    this.isVerified,
    this.overallRating,
  });

  factory MerchantInfo.fromJson(Map<String, dynamic> json) {
    return MerchantInfo(
      merchantId: json['merchantId'],
      merchantName: json['merchantName'],
      merchantLogo: json['merchantLogo'],
      merchantLevel: json['merchantLevel'],
      isVerified: json['isVerified'],
      overallRating: json['overallRating']?.toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'merchantId': merchantId,
      'merchantName': merchantName,
      'merchantLogo': merchantLogo,
      'merchantLevel': merchantLevel,
      'isVerified': isVerified,
      'overallRating': overallRating,
    };
  }
}

// ==================== 活动信息 ====================

/// 活动信息
class ActivityInfo {
  /// 活动类型
  final String? activityType;
  
  /// 活动状态
  final String? activityStatus;
  
  /// 参与人数
  final int? participantCount;
  
  /// 剩余名额
  final int? remainingSlots;
  
  /// 是否已报名
  final bool? isRegistered;
  
  /// 活动标签
  final List<String>? activityTags;
  
  /// 开始时间
  final DateTime? startTime;
  
  /// 结束时间
  final DateTime? endTime;

  ActivityInfo({
    this.activityType,
    this.activityStatus,
    this.participantCount,
    this.remainingSlots,
    this.isRegistered,
    this.activityTags,
    this.startTime,
    this.endTime,
  });

  factory ActivityInfo.fromJson(Map<String, dynamic> json) {
    return ActivityInfo(
      activityType: json['activityType'],
      activityStatus: json['activityStatus'],
      participantCount: json['participantCount'],
      remainingSlots: json['remainingSlots'],
      isRegistered: json['isRegistered'],
      activityTags: json['activityTags'] != null 
          ? List<String>.from(json['activityTags']) 
          : null,
      startTime: json['startTime'] != null 
          ? DateTime.parse(json['startTime']) 
          : null,
      endTime: json['endTime'] != null 
          ? DateTime.parse(json['endTime']) 
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'activityType': activityType,
      'activityStatus': activityStatus,
      'participantCount': participantCount,
      'remainingSlots': remainingSlots,
      'isRegistered': isRegistered,
      'activityTags': activityTags,
      'startTime': startTime?.toIso8601String(),
      'endTime': endTime?.toIso8601String(),
    };
  }

  /// 获取状态显示文本
  String get statusText {
    switch (activityStatus) {
      case 'UPCOMING':
        return '即将开始';
      case 'ONGOING':
        return '进行中';
      case 'ENDED':
        return '已结束';
      case 'FULL':
        return '已满员';
      default:
        return '';
    }
  }
}

// ==================== 优惠券信息 ====================

/// 优惠券信息
class CouponInfo {
  /// 优惠券类型
  final String? couponType;
  
  /// 优惠价值
  final String? couponValue;
  
  /// 使用门槛
  final double? minOrderAmount;
  
  /// 剩余数量
  final int? remainingCount;
  
  /// 是否已领取
  final bool? isClaimed;
  
  /// 是否可用
  final bool? isAvailable;
  
  /// 过期时间
  final DateTime? expireTime;

  CouponInfo({
    this.couponType,
    this.couponValue,
    this.minOrderAmount,
    this.remainingCount,
    this.isClaimed,
    this.isAvailable,
    this.expireTime,
  });

  factory CouponInfo.fromJson(Map<String, dynamic> json) {
    return CouponInfo(
      couponType: json['couponType'],
      couponValue: json['couponValue'],
      minOrderAmount: json['minOrderAmount']?.toDouble(),
      remainingCount: json['remainingCount'],
      isClaimed: json['isClaimed'],
      isAvailable: json['isAvailable'],
      expireTime: json['expireTime'] != null 
          ? DateTime.parse(json['expireTime']) 
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'couponType': couponType,
      'couponValue': couponValue,
      'minOrderAmount': minOrderAmount,
      'remainingCount': remainingCount,
      'isClaimed': isClaimed,
      'isAvailable': isAvailable,
      'expireTime': expireTime?.toIso8601String(),
    };
  }

  /// 获取优惠券显示文本
  String get displayText {
    if (couponValue != null) {
      return couponValue!;
    }
    return '优惠券';
  }
}

// ==================== 社交信息 ====================

/// 社交信息
class SocialInfo {
  /// 点赞数
  final int? likeCount;
  
  /// 是否已点赞
  final bool? isLiked;
  
  /// 收藏数
  final int? favoriteCount;
  
  /// 是否已收藏
  final bool? isFavorited;
  
  /// 评论数
  final int? commentCount;
  
  /// 好友推荐语
  final String? friendRecommendText;
  
  /// 好友头像列表
  final List<String>? friendAvatarList;

  SocialInfo({
    this.likeCount,
    this.isLiked,
    this.favoriteCount,
    this.isFavorited,
    this.commentCount,
    this.friendRecommendText,
    this.friendAvatarList,
  });

  factory SocialInfo.fromJson(Map<String, dynamic> json) {
    return SocialInfo(
      likeCount: json['likeCount'],
      isLiked: json['isLiked'],
      favoriteCount: json['favoriteCount'],
      isFavorited: json['isFavorited'],
      commentCount: json['commentCount'],
      friendRecommendText: json['friendRecommendText'],
      friendAvatarList: json['friendAvatarList'] != null 
          ? List<String>.from(json['friendAvatarList']) 
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'likeCount': likeCount,
      'isLiked': isLiked,
      'favoriteCount': favoriteCount,
      'isFavorited': isFavorited,
      'commentCount': commentCount,
      'friendRecommendText': friendRecommendText,
      'friendAvatarList': friendAvatarList,
    };
  }
}

// ==================== 推荐信息流响应 ====================

/// 推荐信息流响应
class RecommendationFeedResponse {
  /// 响应状态
  final String status;
  
  /// 状态码
  final int code;
  
  /// 状态消息
  final String? message;
  
  /// 用户ID
  final String? userId;
  
  /// 会话ID
  final String? sessionId;
  
  /// 推荐项列表
  final List<RecommendationItem>? items;
  
  /// 当前页码
  final int? pageNum;
  
  /// 每页大小
  final int? pageSize;
  
  /// 是否还有更多
  final bool? hasMore;
  
  /// 下一页游标
  final String? nextCursor;
  
  /// 推荐场景
  final String? scene;
  
  /// 响应时间戳
  final DateTime? timestamp;
  
  /// 策略信息
  final StrategyInfo? strategyInfo;
  
  /// 场景上下文
  final SceneContext? sceneContext;
  
  /// 统计信息
  final FeedStats? statistics;
  
  /// 扩展数据
  final Map<String, dynamic>? extraData;

  RecommendationFeedResponse({
    required this.status,
    required this.code,
    this.message,
    this.userId,
    this.sessionId,
    this.items,
    this.pageNum,
    this.pageSize,
    this.hasMore,
    this.nextCursor,
    this.scene,
    this.timestamp,
    this.strategyInfo,
    this.sceneContext,
    this.statistics,
    this.extraData,
  });

  factory RecommendationFeedResponse.fromJson(Map<String, dynamic> json) {
    return RecommendationFeedResponse(
      status: json['status'],
      code: json['code'],
      message: json['message'],
      userId: json['userId'],
      sessionId: json['sessionId'],
      items: json['items'] != null 
          ? (json['items'] as List)
              .map((e) => RecommendationItem.fromJson(e))
              .toList() 
          : null,
      pageNum: json['pageNum'],
      pageSize: json['pageSize'],
      hasMore: json['hasMore'],
      nextCursor: json['nextCursor'],
      scene: json['scene'],
      timestamp: json['timestamp'] != null 
          ? DateTime.parse(json['timestamp']) 
          : null,
      strategyInfo: json['strategyInfo'] != null 
          ? StrategyInfo.fromJson(json['strategyInfo']) 
          : null,
      sceneContext: json['sceneContext'] != null 
          ? SceneContext.fromJson(json['sceneContext']) 
          : null,
      statistics: json['statistics'] != null 
          ? FeedStats.fromJson(json['statistics']) 
          : null,
      extraData: json['extraData'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'status': status,
      'code': code,
      'message': message,
      'userId': userId,
      'sessionId': sessionId,
      'items': items?.map((e) => e.toJson()).toList(),
      'pageNum': pageNum,
      'pageSize': pageSize,
      'hasMore': hasMore,
      'nextCursor': nextCursor,
      'scene': scene,
      'timestamp': timestamp?.toIso8601String(),
      'strategyInfo': strategyInfo?.toJson(),
      'sceneContext': sceneContext?.toJson(),
      'statistics': statistics?.toJson(),
      'extraData': extraData,
    };
  }

  /// 是否成功
  bool get isSuccess => status == 'SUCCESS' && code == 200;
}

// ==================== 策略信息 ====================

/// 策略信息
class StrategyInfo {
  /// 召回策略列表
  final List<String>? recallStrategies;
  
  /// 排序策略版本
  final String? sortStrategyVersion;
  
  /// A/B测试分组
  final String? abTestGroup;
  
  /// 算法版本
  final String? algorithmVersion;

  StrategyInfo({
    this.recallStrategies,
    this.sortStrategyVersion,
    this.abTestGroup,
    this.algorithmVersion,
  });

  factory StrategyInfo.fromJson(Map<String, dynamic> json) {
    return StrategyInfo(
      recallStrategies: json['recallStrategies'] != null 
          ? List<String>.from(json['recallStrategies']) 
          : null,
      sortStrategyVersion: json['sortStrategyVersion'],
      abTestGroup: json['abTestGroup'],
      algorithmVersion: json['algorithmVersion'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'recallStrategies': recallStrategies,
      'sortStrategyVersion': sortStrategyVersion,
      'abTestGroup': abTestGroup,
      'algorithmVersion': algorithmVersion,
    };
  }
}

// ==================== 场景上下文 ====================

/// 场景上下文
class SceneContext {
  /// 当前时段
  final String? timeSegment;
  
  /// 是否周末
  final bool? isWeekend;
  
  /// 是否节假日
  final bool? isHoliday;
  
  /// 节假日名称
  final String? holidayName;
  
  /// 天气状况
  final String? weatherCondition;
  
  /// 温度
  final int? temperature;
  
  /// 场景标签
  final List<String>? sceneTags;

  SceneContext({
    this.timeSegment,
    this.isWeekend,
    this.isHoliday,
    this.holidayName,
    this.weatherCondition,
    this.temperature,
    this.sceneTags,
  });

  factory SceneContext.fromJson(Map<String, dynamic> json) {
    return SceneContext(
      timeSegment: json['timeSegment'],
      isWeekend: json['isWeekend'],
      isHoliday: json['isHoliday'],
      holidayName: json['holidayName'],
      weatherCondition: json['weatherCondition'],
      temperature: json['temperature'],
      sceneTags: json['sceneTags'] != null 
          ? List<String>.from(json['sceneTags']) 
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'timeSegment': timeSegment,
      'isWeekend': isWeekend,
      'isHoliday': isHoliday,
      'holidayName': holidayName,
      'weatherCondition': weatherCondition,
      'temperature': temperature,
      'sceneTags': sceneTags,
    };
  }
}

// ==================== 信息流统计 ====================

/// 信息流统计
class FeedStats {
  /// 总推荐项数
  final int? totalItems;
  
  /// 各类型数量
  final Map<String, int>? typeCounts;
  
  /// 召回耗时（毫秒）
  final int? recallTimeMs;
  
  /// 排序耗时（毫秒）
  final int? sortTimeMs;
  
  /// 总生成耗时（毫秒）
  final int? totalGenerateTimeMs;

  FeedStats({
    this.totalItems,
    this.typeCounts,
    this.recallTimeMs,
    this.sortTimeMs,
    this.totalGenerateTimeMs,
  });

  factory FeedStats.fromJson(Map<String, dynamic> json) {
    return FeedStats(
      totalItems: json['totalItems'],
      typeCounts: json['typeCounts'] != null 
          ? Map<String, int>.from(json['typeCounts']) 
          : null,
      recallTimeMs: json['recallTimeMs'],
      sortTimeMs: json['sortTimeMs'],
      totalGenerateTimeMs: json['totalGenerateTimeMs'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'totalItems': totalItems,
      'typeCounts': typeCounts,
      'recallTimeMs': recallTimeMs,
      'sortTimeMs': sortTimeMs,
      'totalGenerateTimeMs': totalGenerateTimeMs,
    };
  }
}

// ==================== 推荐请求参数 ====================

/// 推荐信息流请求参数
class RecommendationFeedRequest {
  /// 用户ID
  final String? userId;
  
  /// 会话ID
  final String? sessionId;
  
  /// 分页游标
  final String? cursor;
  
  /// 页码
  final int pageNum;
  
  /// 每页大小
  final int pageSize;
  
  /// 推荐场景
  final String scene;
  
  /// 经度
  final double longitude;
  
  /// 纬度
  final double latitude;
  
  /// 搜索半径
  final int searchRadius;
  
  /// 分类筛选
  final List<String>? categoryFilters;
  
  /// 价格区间筛选
  final Set<String>? priceRangeFilters;
  
  /// 推荐类型筛选
  final Set<String>? itemTypeFilters;
  
  /// 最低评分
  final double? minRating;
  
  /// 排序方式
  final String sortBy;
  
  /// 场景标签
  final Set<String>? sceneTags;
  
  /// 关键词
  final String? keyword;
  
  /// 是否只显示营业中
  final bool onlyOpenNow;

  RecommendationFeedRequest({
    this.userId,
    this.sessionId,
    this.cursor,
    this.pageNum = 1,
    this.pageSize = 20,
    required this.scene,
    required this.longitude,
    required this.latitude,
    this.searchRadius = 5000,
    this.categoryFilters,
    this.priceRangeFilters,
    this.itemTypeFilters,
    this.minRating,
    this.sortBy = 'RECOMMEND',
    this.sceneTags,
    this.keyword,
    this.onlyOpenNow = false,
  });

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'sessionId': sessionId,
      'cursor': cursor,
      'pageNum': pageNum,
      'pageSize': pageSize,
      'scene': scene,
      'longitude': longitude,
      'latitude': latitude,
      'searchRadius': searchRadius,
      'categoryFilters': categoryFilters,
      'priceRangeFilters': priceRangeFilters?.toList(),
      'itemTypeFilters': itemTypeFilters?.toList(),
      'minRating': minRating,
      'sortBy': sortBy,
      'sceneTags': sceneTags?.toList(),
      'keyword': keyword,
      'onlyOpenNow': onlyOpenNow,
    };
  }
}

// ==================== 场景标签常量 ====================

/// 推荐场景常量
class RecommendationScenes {
  static const String nearby = 'NEARBY';
  static const String home = 'HOME';
  static const String discover = 'DISCOVER';
  static const String favorite = 'FAVORITE';
  static const String scene = 'SCENE';
  static const String search = 'SEARCH';
  static const String detail = 'DETAIL';
}

/// 推荐项类型常量
class RecommendationItemTypes {
  static const String poi = 'POI';
  static const String activity = 'ACTIVITY';
  static const String coupon = 'COUPON';
  static const String group = 'GROUP';
  static const String event = 'EVENT';
  static const String content = 'CONTENT';
}

/// 排序方式常量
class RecommendationSortBy {
  static const String recommend = 'RECOMMEND';
  static const String distance = 'DISTANCE';
  static const String rating = 'RATING';
  static const String heat = 'HEAT';
  static const String priceAsc = 'PRICE_ASC';
  static const String priceDesc = 'PRICE_DESC';
  static const String newest = 'NEWEST';
}

/// 场景标签常量
class SceneTags {
  static const String breakfast = 'BREAKFAST';
  static const String lunch = 'LUNCH';
  static const String dinner = 'DINNER';
  static const String night = 'NIGHT';
  static const String weekend = 'WEEKEND';
  static const String dating = 'DATING';
  static const String family = 'FAMILY';
  static const String business = 'BUSINESS';
  static const String party = 'PARTY';
}
