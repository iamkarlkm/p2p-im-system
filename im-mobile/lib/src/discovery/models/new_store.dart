import 'dart:convert';

/// 新店数据模型
class NewStore {
  /// 店铺ID
  final String id;
  
  /// 店铺名称
  final String name;
  
  /// 品牌名
  final String? brandName;
  
  /// 地址
  final String address;
  
  /// 纬度
  final double latitude;
  
  /// 经度
  final double longitude;
  
  /// 分类
  final String category;
  
  /// 子分类
  final List<String>? subCategories;
  
  /// 开业时间
  final DateTime openingDate;
  
  /// 是否预约开业
  final bool isPreview;
  
  /// 预览/开业状态
  final NewStoreStatus status;
  
  /// 营业时间
  final BusinessHours? businessHours;
  
  /// 人均消费
  final double? averageCost;
  
  /// 电话
  final String? phone;
  
  /// 图片列表
  final List<String>? photos;
  
  /// 特色标签
  final List<String>? tags;
  
  /// 简介
  final String? description;
  
  /// 促销活动
  final List<StorePromotion>? promotions;
  
  /// 距离
  final double? distance;
  
  /// 是否被关注
  final bool isWatched;
  
  /// 创建时间
  final DateTime createdAt;

  NewStore({
    required this.id,
    required this.name,
    this.brandName,
    required this.address,
    required this.latitude,
    required this.longitude,
    required this.category,
    this.subCategories,
    required this.openingDate,
    this.isPreview = false,
    this.status = NewStoreStatus.comingSoon,
    this.businessHours,
    this.averageCost,
    this.phone,
    this.photos,
    this.tags,
    this.description,
    this.promotions,
    this.distance,
    this.isWatched = false,
    required this.createdAt,
  });

  factory NewStore.fromJson(Map<String, dynamic> json) {
    return NewStore(
      id: json['id'] as String,
      name: json['name'] as String,
      brandName: json['brandName'] as String?,
      address: json['address'] as String,
      latitude: (json['latitude'] as num).toDouble(),
      longitude: (json['longitude'] as num).toDouble(),
      category: json['category'] as String,
      subCategories: (json['subCategories'] as List?)?.cast<String>(),
      openingDate: DateTime.parse(json['openingDate'] as String),
      isPreview: json['isPreview'] as bool? ?? false,
      status: NewStoreStatus.values.firstWhere(
        (e) => e.name == json['status'],
        orElse: () => NewStoreStatus.comingSoon,
      ),
      businessHours: json['businessHours'] != null
          ? BusinessHours.fromJson(json['businessHours'] as Map<String, dynamic>)
          : null,
      averageCost: json['averageCost'] != null
          ? (json['averageCost'] as num).toDouble()
          : null,
      phone: json['phone'] as String?,
      photos: (json['photos'] as List?)?.cast<String>(),
      tags: (json['tags'] as List?)?.cast<String>(),
      description: json['description'] as String?,
      promotions: (json['promotions'] as List?)
          ?.map((p) => StorePromotion.fromJson(p as Map<String, dynamic>))
          .toList(),
      distance: json['distance'] != null
          ? (json['distance'] as num).toDouble()
          : null,
      isWatched: json['isWatched'] as bool? ?? false,
      createdAt: DateTime.parse(json['createdAt'] as String),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'brandName': brandName,
      'address': address,
      'latitude': latitude,
      'longitude': longitude,
      'category': category,
      'subCategories': subCategories,
      'openingDate': openingDate.toIso8601String(),
      'isPreview': isPreview,
      'status': status.name,
      'businessHours': businessHours?.toJson(),
      'averageCost': averageCost,
      'phone': phone,
      'photos': photos,
      'tags': tags,
      'description': description,
      'promotions': promotions?.map((p) => p.toJson()).toList(),
      'distance': distance,
      'isWatched': isWatched,
      'createdAt': createdAt.toIso8601String(),
    };
  }

  /// 开业状态文本
  String get statusText {
    switch (status) {
      case NewStoreStatus.open:
        return '已开业';
      case NewStoreStatus.softOpen:
        return '试营业';
      case NewStoreStatus.comingSoon:
        final days = openingDate.difference(DateTime.now()).inDays;
        if (days <= 0) return '即将开业';
        return '$days天后开业';
      case NewStoreStatus.preview:
        return '预约中';
    }
  }

  /// 是否已开业
  bool get isOpen => status == NewStoreStatus.open || status == NewStoreStatus.softOpen;
}

/// 新店状态
enum NewStoreStatus {
  /// 已开业
  open,
  
  /// 试营业
  softOpen,
  
  /// 即将开业
  comingSoon,
  
  /// 预约预览
  preview,
}

/// 营业时间
class BusinessHours {
  final String? weekdayHours;
  final String? weekendHours;
  final bool is24Hours;

  BusinessHours({
    this.weekdayHours,
    this.weekendHours,
    this.is24Hours = false,
  });

  factory BusinessHours.fromJson(Map<String, dynamic> json) {
    return BusinessHours(
      weekdayHours: json['weekdayHours'] as String?,
      weekendHours: json['weekendHours'] as String?,
      is24Hours: json['is24Hours'] as bool? ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'weekdayHours': weekdayHours,
      'weekendHours': weekendHours,
      'is24Hours': is24Hours,
    };
  }
}

/// 店铺促销
class StorePromotion {
  final String id;
  final String title;
  final String? description;
  final DateTime? startDate;
  final DateTime? endDate;
  final PromotionType type;

  StorePromotion({
    required this.id,
    required this.title,
    this.description,
    this.startDate,
    this.endDate,
    this.type = PromotionType.discount,
  });

  factory StorePromotion.fromJson(Map<String, dynamic> json) {
    return StorePromotion(
      id: json['id'] as String,
      title: json['title'] as String,
      description: json['description'] as String?,
      startDate: json['startDate'] != null
          ? DateTime.parse(json['startDate'] as String)
          : null,
      endDate: json['endDate'] != null
          ? DateTime.parse(json['endDate'] as String)
          : null,
      type: PromotionType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => PromotionType.discount,
      ),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'title': title,
      'description': description,
      'startDate': startDate?.toIso8601String(),
      'endDate': endDate?.toIso8601String(),
      'type': type.name,
    };
  }

  /// 是否有效
  bool get isValid {
    final now = DateTime.now();
    if (startDate != null && now.isBefore(startDate!)) return false;
    if (endDate != null && now.isAfter(endDate!)) return false;
    return true;
  }
}

/// 促销类型
enum PromotionType {
  /// 折扣
  discount,
  
  /// 满减
  reduction,
  
  /// 赠品
  gift,
  
  /// 优惠券
  coupon,
  
  /// 会员专享
  memberOnly,
}

/// 达人认证信息
class ExpertProfile {
  final String userId;
  final String nickname;
  final String? avatar;
  final ExpertLevel level;
  final int checkInCount;
  final int followerCount;
  final List<String>? specialties;
  final String? bio;
  final bool isVerified;

  ExpertProfile({
    required this.userId,
    required this.nickname,
    this.avatar,
    this.level = ExpertLevel.beginner,
    this.checkInCount = 0,
    this.followerCount = 0,
    this.specialties,
    this.bio,
    this.isVerified = false,
  });

  factory ExpertProfile.fromJson(Map<String, dynamic> json) {
    return ExpertProfile(
      userId: json['userId'] as String,
      nickname: json['nickname'] as String,
      avatar: json['avatar'] as String?,
      level: ExpertLevel.values.firstWhere(
        (e) => e.name == json['level'],
        orElse: () => ExpertLevel.beginner,
      ),
      checkInCount: json['checkInCount'] as int? ?? 0,
      followerCount: json['followerCount'] as int? ?? 0,
      specialties: (json['specialties'] as List?)?.cast<String>(),
      bio: json['bio'] as String?,
      isVerified: json['isVerified'] as bool? ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'nickname': nickname,
      'avatar': avatar,
      'level': level.name,
      'checkInCount': checkInCount,
      'followerCount': followerCount,
      'specialties': specialties,
      'bio': bio,
      'isVerified': isVerified,
    };
  }

  /// 等级名称
  String get levelName {
    switch (level) {
      case ExpertLevel.beginner:
        return '探店新手';
      case ExpertLevel.intermediate:
        return '探店达人';
      case ExpertLevel.advanced:
        return '资深达人';
      case ExpertLevel.expert:
        return '探店专家';
      case ExpertLevel.master:
        return '探店大师';
    }
  }
}

/// 达人等级
enum ExpertLevel {
  beginner,      // 新手
  intermediate,  // 进阶
  advanced,      // 资深
  expert,        // 专家
  master,        // 大师
}

/// 探店预算规划
class DiscoveryBudget {
  final double totalBudget;
  final int storeCount;
  final double averagePerStore;
  final List<BudgetItem>? items;
  final String? note;

  DiscoveryBudget({
    required this.totalBudget,
    required this.storeCount,
    required this.averagePerStore,
    this.items,
    this.note,
  });

  factory DiscoveryBudget.fromJson(Map<String, dynamic> json) {
    return DiscoveryBudget(
      totalBudget: (json['totalBudget'] as num).toDouble(),
      storeCount: json['storeCount'] as int,
      averagePerStore: (json['averagePerStore'] as num).toDouble(),
      items: (json['items'] as List?)
          ?.map((i) => BudgetItem.fromJson(i as Map<String, dynamic>))
          .toList(),
      note: json['note'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'totalBudget': totalBudget,
      'storeCount': storeCount,
      'averagePerStore': averagePerStore,
      'items': items?.map((i) => i.toJson()).toList(),
      'note': note,
    };
  }
}

/// 预算项
class BudgetItem {
  final String category;
  final double estimatedCost;
  final String? note;

  BudgetItem({
    required this.category,
    required this.estimatedCost,
    this.note,
  });

  factory BudgetItem.fromJson(Map<String, dynamic> json) {
    return BudgetItem(
      category: json['category'] as String,
      estimatedCost: (json['estimatedCost'] as num).toDouble(),
      note: json['note'] as String?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'category': category,
      'estimatedCost': estimatedCost,
      'note': note,
    };
  }
}

/// 避坑指南
class StoreTip {
  final String id;
  final String storeId;
  final String content;
  final String authorId;
  final String authorName;
  final int helpfulCount;
  final DateTime createdAt;
  final List<String>? tags;

  StoreTip({
    required this.id,
    required this.storeId,
    required this.content,
    required this.authorId,
    required this.authorName,
    this.helpfulCount = 0,
    required this.createdAt,
    this.tags,
  });

  factory StoreTip.fromJson(Map<String, dynamic> json) {
    return StoreTip(
      id: json['id'] as String,
      storeId: json['storeId'] as String,
      content: json['content'] as String,
      authorId: json['authorId'] as String,
      authorName: json['authorName'] as String,
      helpfulCount: json['helpfulCount'] as int? ?? 0,
      createdAt: DateTime.parse(json['createdAt'] as String),
      tags: (json['tags'] as List?)?.cast<String>(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'storeId': storeId,
      'content': content,
      'authorId': authorId,
      'authorName': authorName,
      'helpfulCount': helpfulCount,
      'createdAt': createdAt.toIso8601String(),
      'tags': tags,
    };
  }
}
