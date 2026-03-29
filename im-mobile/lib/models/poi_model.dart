/// POI（兴趣点）模型
class POI {
  final String id;
  final String name;
  final String? description;
  final double latitude;
  final double longitude;
  final String? address;
  final String? phone;
  final POICategory category;
  final String? subCategory;
  final double? rating;
  final int? reviewCount;
  final double? priceLevel; // 1-4 价格等级
  final String? priceRange; // 如 "¥50-100"
  final List<String>? tags;
  final List<String>? images;
  final String? thumbnail;
  final BusinessHours? businessHours;
  final bool? isOpenNow;
  final double distance; // 距离当前位置（米）
  final DateTime createdAt;
  final DateTime? updatedAt;
  final bool isFavorite;
  final Map<String, dynamic>? extra;

  POI({
    required this.id,
    required this.name,
    this.description,
    required this.latitude,
    required this.longitude,
    this.address,
    this.phone,
    required this.category,
    this.subCategory,
    this.rating,
    this.reviewCount,
    this.priceLevel,
    this.priceRange,
    this.tags,
    this.images,
    this.thumbnail,
    this.businessHours,
    this.isOpenNow,
    this.distance = 0,
    required this.createdAt,
    this.updatedAt,
    this.isFavorite = false,
    this.extra,
  });

  factory POI.fromJson(Map<String, dynamic> json) {
    return POI(
      id: json['id'],
      name: json['name'],
      description: json['description'],
      latitude: json['latitude'],
      longitude: json['longitude'],
      address: json['address'],
      phone: json['phone'],
      category: POICategory.fromCode(json['category'] ?? 'other'),
      subCategory: json['subCategory'],
      rating: json['rating']?.toDouble(),
      reviewCount: json['reviewCount'],
      priceLevel: json['priceLevel']?.toDouble(),
      priceRange: json['priceRange'],
      tags: json['tags']?.cast<String>(),
      images: json['images']?.cast<String>(),
      thumbnail: json['thumbnail'],
      businessHours: json['businessHours'] != null
          ? BusinessHours.fromJson(json['businessHours'])
          : null,
      isOpenNow: json['isOpenNow'],
      distance: json['distance']?.toDouble() ?? 0,
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: json['updatedAt'] != null
          ? DateTime.parse(json['updatedAt'])
          : null,
      isFavorite: json['isFavorite'] ?? false,
      extra: json['extra'],
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'name': name,
    'description': description,
    'latitude': latitude,
    'longitude': longitude,
    'address': address,
    'phone': phone,
    'category': category.code,
    'subCategory': subCategory,
    'rating': rating,
    'reviewCount': reviewCount,
    'priceLevel': priceLevel,
    'priceRange': priceRange,
    'tags': tags,
    'images': images,
    'thumbnail': thumbnail,
    'businessHours': businessHours?.toJson(),
    'isOpenNow': isOpenNow,
    'distance': distance,
    'createdAt': createdAt.toIso8601String(),
    'updatedAt': updatedAt?.toIso8601String(),
    'isFavorite': isFavorite,
    'extra': extra,
  };

  /// 创建副本
  POI copyWith({
    bool? isFavorite,
    double? distance,
    List<String>? tags,
  }) {
    return POI(
      id: id,
      name: name,
      description: description,
      latitude: latitude,
      longitude: longitude,
      address: address,
      phone: phone,
      category: category,
      subCategory: subCategory,
      rating: rating,
      reviewCount: reviewCount,
      priceLevel: priceLevel,
      priceRange: priceRange,
      tags: tags ?? this.tags,
      images: images,
      thumbnail: thumbnail,
      businessHours: businessHours,
      isOpenNow: isOpenNow,
      distance: distance ?? this.distance,
      createdAt: createdAt,
      updatedAt: updatedAt,
      isFavorite: isFavorite ?? this.isFavorite,
      extra: extra,
    );
  }

  String get formattedDistance {
    if (distance < 1000) {
      return '${distance.toInt()}m';
    } else {
      return '${(distance / 1000).toStringAsFixed(1)}km';
    }
  }

  String get formattedRating {
    if (rating == null) return '暂无评分';
    return '${rating!.toStringAsFixed(1)}分';
  }

  String get formattedPrice {
    if (priceRange != null) return priceRange!;
    if (priceLevel == null) return '';
    return '¥' * priceLevel!.toInt();
  }
}

/// POI分类
enum POICategory {
  restaurant('餐饮美食', 'restaurant', Icons.restaurant, Colors.orange),
  shopping('购物商场', 'shopping', Icons.shopping_bag, Colors.pink),
  entertainment('休闲娱乐', 'entertainment', Icons.movie, Colors.purple),
  hotel('酒店住宿', 'hotel', Icons.hotel, Colors.blue),
  scenic('景点景区', 'scenic', Icons.landscape, Colors.green),
  transport('交通出行', 'transport', Icons.directions_bus, Colors.indigo),
  life('生活服务', 'life', Icons.local_service, Colors.teal),
  medical('医疗健康', 'medical', Icons.local_hospital, Colors.red),
  education('教育培训', 'education', Icons.school, Colors.amber),
  sports('运动健身', 'sports', Icons.fitness_center, Colors.deepOrange),
  other('其他', 'other', Icons.place, Colors.grey);

  final String label;
  final String code;
  final dynamic icon; // IconData
  final dynamic color; // Color

  const POICategory(this.label, this.code, this.icon, this.color);

  factory POICategory.fromCode(String code) {
    return POICategory.values.firstWhere(
      (c) => c.code == code,
      orElse: () => POICategory.other,
    );
  }

  static List<POICategory> get popularCategories => [
    restaurant,
    shopping,
    entertainment,
    hotel,
    scenic,
  ];
}

/// 营业时间
class BusinessHours {
  final List<DailyHours> weeklyHours;
  final String? specialHours;

  BusinessHours({
    required this.weeklyHours,
    this.specialHours,
  });

  factory BusinessHours.fromJson(Map<String, dynamic> json) {
    return BusinessHours(
      weeklyHours: (json['weeklyHours'] as List)
          .map((h) => DailyHours.fromJson(h))
          .toList(),
      specialHours: json['specialHours'],
    );
  }

  Map<String, dynamic> toJson() => {
    'weeklyHours': weeklyHours.map((h) => h.toJson()).toList(),
    'specialHours': specialHours,
  };

  /// 检查当前是否营业
  bool get isOpenNow {
    final now = DateTime.now();
    final weekday = now.weekday - 1; // 0 = Monday
    
    if (weekday >= weeklyHours.length) return false;
    
    final today = weeklyHours[weekday];
    if (today.isClosed) return false;

    final currentMinutes = now.hour * 60 + now.minute;
    final openMinutes = today.openHour! * 60 + today.openMinute!;
    final closeMinutes = today.closeHour! * 60 + today.closeMinute!;

    return currentMinutes >= openMinutes && currentMinutes < closeMinutes;
  }

  /// 获取今天的营业时间
  String get todayHours {
    final now = DateTime.now();
    final weekday = now.weekday - 1;
    
    if (weekday >= weeklyHours.length) return '未知';
    
    final today = weeklyHours[weekday];
    if (today.isClosed) return '今日休息';
    
    return '${today.openTime} - ${today.closeTime}';
  }
}

/// 每日营业时间
class DailyHours {
  final int dayOfWeek; // 0 = Monday
  final bool isClosed;
  final int? openHour;
  final int? openMinute;
  final int? closeHour;
  final int? closeMinute;

  DailyHours({
    required this.dayOfWeek,
    this.isClosed = false,
    this.openHour,
    this.openMinute,
    this.closeHour,
    this.closeMinute,
  });

  factory DailyHours.fromJson(Map<String, dynamic> json) {
    return DailyHours(
      dayOfWeek: json['dayOfWeek'],
      isClosed: json['isClosed'] ?? false,
      openHour: json['openHour'],
      openMinute: json['openMinute'],
      closeHour: json['closeHour'],
      closeMinute: json['closeMinute'],
    );
  }

  Map<String, dynamic> toJson() => {
    'dayOfWeek': dayOfWeek,
    'isClosed': isClosed,
    'openHour': openHour,
    'openMinute': openMinute,
    'closeHour': closeHour,
    'closeMinute': closeMinute,
  };

  String get openTime => isClosed 
      ? '休息' 
      : '${openHour.toString().padLeft(2, '0')}:${openMinute.toString().padLeft(2, '0')}';
  
  String get closeTime => isClosed 
      ? '休息' 
      : '${closeHour.toString().padLeft(2, '0')}:${closeMinute.toString().padLeft(2, '0')}';
}

/// POI评价
class POIReview {
  final String id;
  final String userId;
  final String? userName;
  final String? userAvatar;
  final double rating;
  final String? content;
  final List<String>? images;
  final DateTime createdAt;
  final int? helpfulCount;
  final bool? isHelpful;

  POIReview({
    required this.id,
    required this.userId,
    this.userName,
    this.userAvatar,
    required this.rating,
    this.content,
    this.images,
    required this.createdAt,
    this.helpfulCount,
    this.isHelpful,
  });

  factory POIReview.fromJson(Map<String, dynamic> json) {
    return POIReview(
      id: json['id'],
      userId: json['userId'],
      userName: json['userName'],
      userAvatar: json['userAvatar'],
      rating: json['rating'].toDouble(),
      content: json['content'],
      images: json['images']?.cast<String>(),
      createdAt: DateTime.parse(json['createdAt']),
      helpfulCount: json['helpfulCount'],
      isHelpful: json['isHelpful'],
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'userId': userId,
    'userName': userName,
    'userAvatar': userAvatar,
    'rating': rating,
    'content': content,
    'images': images,
    'createdAt': createdAt.toIso8601String(),
    'helpfulCount': helpfulCount,
    'isHelpful': isHelpful,
  };

  String get formattedDate {
    final now = DateTime.now();
    final diff = now.difference(createdAt);
    
    if (diff.inDays > 365) {
      return '${(diff.inDays / 365).floor()}年前';
    } else if (diff.inDays > 30) {
      return '${(diff.inDays / 30).floor()}个月前';
    } else if (diff.inDays > 0) {
      return '${diff.inDays}天前';
    } else if (diff.inHours > 0) {
      return '${diff.inHours}小时前';
    } else {
      return '刚刚';
    }
  }
}

// 导入Flutter依赖（实际使用时需要）
// ignore: constant_identifier_names
class Icons {
  static const String restaurant = 'restaurant';
  static const String shopping_bag = 'shopping_bag';
  static const String movie = 'movie';
  static const String hotel = 'hotel';
  static const String landscape = 'landscape';
  static const String directions_bus = 'directions_bus';
  static const String local_service = 'local_service';
  static const String local_hospital = 'local_hospital';
  static const String school = 'school';
  static const String fitness_center = 'fitness_center';
  static const String place = 'place';
}

// ignore: constant_identifier_names
class Colors {
  static const String orange = 'orange';
  static const String pink = 'pink';
  static const String purple = 'purple';
  static const String blue = 'blue';
  static const String green = 'green';
  static const String indigo = 'indigo';
  static const String teal = 'teal';
  static const String red = 'red';
  static const String amber = 'amber';
  static const String deepOrange = 'deepOrange';
  static const String grey = 'grey';
}
