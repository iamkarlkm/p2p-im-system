import 'dart:convert';

/// POI搜索模型类
class PoiSearchModel {
  final String id;
  final String name;
  final String? brandName;
  final String category;
  final String? categoryIcon;
  final String address;
  final int distance;
  final String? distanceText;
  final double longitude;
  final double latitude;
  final double? rating;
  final int? ratingCount;
  final int? avgPrice;
  final String? priceText;
  final List<String> tags;
  final String? mainImage;
  final List<String> images;
  final String? businessHours;
  final bool? isOpen;
  final String? phone;
  final bool? hasWifi;
  final bool? hasParking;
  final List<String>? hotTags;
  final String? recommendation;
  final List<CouponInfo>? coupons;
  final List<ActivityInfo>? activities;
  final String? personalizedReason;
  final List<FriendRecommend>? friendRecommends;
  final Map<String, dynamic>? extFields;

  PoiSearchModel({
    required this.id,
    required this.name,
    this.brandName,
    required this.category,
    this.categoryIcon,
    required this.address,
    required this.distance,
    this.distanceText,
    required this.longitude,
    required this.latitude,
    this.rating,
    this.ratingCount,
    this.avgPrice,
    this.priceText,
    required this.tags,
    this.mainImage,
    required this.images,
    this.businessHours,
    this.isOpen,
    this.phone,
    this.hasWifi,
    this.hasParking,
    this.hotTags,
    this.recommendation,
    this.coupons,
    this.activities,
    this.personalizedReason,
    this.friendRecommends,
    this.extFields,
  });

  factory PoiSearchModel.fromJson(Map<String, dynamic> json) {
    return PoiSearchModel(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      brandName: json['brandName'],
      category: json['category'] ?? '',
      categoryIcon: json['categoryIcon'],
      address: json['address'] ?? '',
      distance: json['distance'] ?? 0,
      distanceText: json['distanceText'],
      longitude: (json['longitude'] ?? 0.0).toDouble(),
      latitude: (json['latitude'] ?? 0.0).toDouble(),
      rating: json['rating']?.toDouble(),
      ratingCount: json['ratingCount'],
      avgPrice: json['avgPrice'],
      priceText: json['priceText'],
      tags: List<String>.from(json['tags'] ?? []),
      mainImage: json['mainImage'],
      images: List<String>.from(json['images'] ?? []),
      businessHours: json['businessHours'],
      isOpen: json['isOpen'],
      phone: json['phone'],
      hasWifi: json['hasWifi'],
      hasParking: json['hasParking'],
      hotTags: json['hotTags'] != null ? List<String>.from(json['hotTags']) : null,
      recommendation: json['recommendation'],
      coupons: json['coupons'] != null
          ? (json['coupons'] as List).map((e) => CouponInfo.fromJson(e)).toList()
          : null,
      activities: json['activities'] != null
          ? (json['activities'] as List).map((e) => ActivityInfo.fromJson(e)).toList()
          : null,
      personalizedReason: json['personalizedReason'],
      friendRecommends: json['friendRecommends'] != null
          ? (json['friendRecommends'] as List).map((e) => FriendRecommend.fromJson(e)).toList()
          : null,
      extFields: json['extFields'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'brandName': brandName,
      'category': category,
      'categoryIcon': categoryIcon,
      'address': address,
      'distance': distance,
      'distanceText': distanceText,
      'longitude': longitude,
      'latitude': latitude,
      'rating': rating,
      'ratingCount': ratingCount,
      'avgPrice': avgPrice,
      'priceText': priceText,
      'tags': tags,
      'mainImage': mainImage,
      'images': images,
      'businessHours': businessHours,
      'isOpen': isOpen,
      'phone': phone,
      'hasWifi': hasWifi,
      'hasParking': hasParking,
      'hotTags': hotTags,
      'recommendation': recommendation,
      'coupons': coupons?.map((e) => e.toJson()).toList(),
      'activities': activities?.map((e) => e.toJson()).toList(),
      'personalizedReason': personalizedReason,
      'friendRecommends': friendRecommends?.map((e) => e.toJson()).toList(),
      'extFields': extFields,
    };
  }

  /// 获取显示名称
  String get displayName => brandName != null ? '$brandName · $name' : name;

  /// 获取评分显示
  String get ratingText => rating != null ? rating!.toStringAsFixed(1) : '暂无';

  /// 是否在营业
  bool get isOperating => isOpen ?? false;

  /// 获取导航URL
  String get navigationUrl =>
      'https://maps.apple.com/?daddr=$latitude,$longitude';
}

/// 优惠券信息
class CouponInfo {
  final String couponId;
  final String title;
  final String discount;
  final bool isNewUserOnly;

  CouponInfo({
    required this.couponId,
    required this.title,
    required this.discount,
    required this.isNewUserOnly,
  });

  factory CouponInfo.fromJson(Map<String, dynamic> json) {
    return CouponInfo(
      couponId: json['couponId'] ?? '',
      title: json['title'] ?? '',
      discount: json['discount'] ?? '',
      isNewUserOnly: json['isNewUserOnly'] ?? false,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'couponId': couponId,
      'title': title,
      'discount': discount,
      'isNewUserOnly': isNewUserOnly,
    };
  }
}

/// 活动信息
class ActivityInfo {
  final String activityId;
  final String title;
  final String type;
  final String tag;

  ActivityInfo({
    required this.activityId,
    required this.title,
    required this.type,
    required this.tag,
  });

  factory ActivityInfo.fromJson(Map<String, dynamic> json) {
    return ActivityInfo(
      activityId: json['activityId'] ?? '',
      title: json['title'] ?? '',
      type: json['type'] ?? '',
      tag: json['tag'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'activityId': activityId,
      'title': title,
      'type': type,
      'tag': tag,
    };
  }
}

/// 好友推荐
class FriendRecommend {
  final int userId;
  final String userName;
  final String? avatar;
  final String? comment;
  final double? rating;

  FriendRecommend({
    required this.userId,
    required this.userName,
    this.avatar,
    this.comment,
    this.rating,
  });

  factory FriendRecommend.fromJson(Map<String, dynamic> json) {
    return FriendRecommend(
      userId: json['userId'] ?? 0,
      userName: json['userName'] ?? '',
      avatar: json['avatar'],
      comment: json['comment'],
      rating: json['rating']?.toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'userId': userId,
      'userName': userName,
      'avatar': avatar,
      'comment': comment,
      'rating': rating,
    };
  }
}

/// 搜索意图模型
class SearchIntentModel {
  final String originalQuery;
  final String intentType;
  final double confidence;
  final List<SearchEntity> entities;
  final StructuredQuery? structuredQuery;
  final List<String>? synonyms;
  final String? correctedQuery;
  final bool? needCorrection;
  final List<String>? searchSuggestions;

  SearchIntentModel({
    required this.originalQuery,
    required this.intentType,
    required this.confidence,
    required this.entities,
    this.structuredQuery,
    this.synonyms,
    this.correctedQuery,
    this.needCorrection,
    this.searchSuggestions,
  });

  factory SearchIntentModel.fromJson(Map<String, dynamic> json) {
    return SearchIntentModel(
      originalQuery: json['originalQuery'] ?? '',
      intentType: json['intentType'] ?? '',
      confidence: (json['confidence'] ?? 0.0).toDouble(),
      entities: (json['entities'] as List?)
              ?.map((e) => SearchEntity.fromJson(e))
              .toList() ??
          [],
      structuredQuery: json['structuredQuery'] != null
          ? StructuredQuery.fromJson(json['structuredQuery'])
          : null,
      synonyms: json['synonyms'] != null ? List<String>.from(json['synonyms']) : null,
      correctedQuery: json['correctedQuery'],
      needCorrection: json['needCorrection'],
      searchSuggestions: json['searchSuggestions'] != null
          ? List<String>.from(json['searchSuggestions'])
          : null,
    );
  }
}

/// 搜索实体
class SearchEntity {
  final String type;
  final String value;
  final String rawText;
  final int start;
  final int end;
  final Map<String, dynamic>? attributes;

  SearchEntity({
    required this.type,
    required this.value,
    required this.rawText,
    required this.start,
    required this.end,
    this.attributes,
  });

  factory SearchEntity.fromJson(Map<String, dynamic> json) {
    return SearchEntity(
      type: json['type'] ?? '',
      value: json['value'] ?? '',
      rawText: json['rawText'] ?? '',
      start: json['start'] ?? 0,
      end: json['end'] ?? 0,
      attributes: json['attributes'],
    );
  }
}

/// 结构化查询
class StructuredQuery {
  final List<String> keywords;
  final List<String>? categories;
  final LocationConstraint? location;
  final PriceConstraint? price;
  final double? minRating;
  final List<String>? tags;

  StructuredQuery({
    required this.keywords,
    this.categories,
    this.location,
    this.price,
    this.minRating,
    this.tags,
  });

  factory StructuredQuery.fromJson(Map<String, dynamic> json) {
    return StructuredQuery(
      keywords: List<String>.from(json['keywords'] ?? []),
      categories: json['categories'] != null
          ? List<String>.from(json['categories'])
          : null,
      location: json['location'] != null
          ? LocationConstraint.fromJson(json['location'])
          : null,
      price: json['price'] != null ? PriceConstraint.fromJson(json['price']) : null,
      minRating: json['minRating']?.toDouble(),
      tags: json['tags'] != null ? List<String>.from(json['tags']) : null,
    );
  }
}

/// 位置约束
class LocationConstraint {
  final double? longitude;
  final double? latitude;
  final int? radius;
  final String? area;
  final String? landmark;

  LocationConstraint({
    this.longitude,
    this.latitude,
    this.radius,
    this.area,
    this.landmark,
  });

  factory LocationConstraint.fromJson(Map<String, dynamic> json) {
    return LocationConstraint(
      longitude: json['longitude']?.toDouble(),
      latitude: json['latitude']?.toDouble(),
      radius: json['radius'],
      area: json['area'],
      landmark: json['landmark'],
    );
  }
}

/// 价格约束
class PriceConstraint {
  final int? min;
  final int? max;
  final int? level;

  PriceConstraint({this.min, this.max, this.level});

  factory PriceConstraint.fromJson(Map<String, dynamic> json) {
    return PriceConstraint(
      min: json['min'],
      max: json['max'],
      level: json['level'],
    );
  }
}

/// 搜索响应模型
class SearchResponseModel {
  final int total;
  final int page;
  final int size;
  final int totalPages;
  final List<PoiSearchModel> results;
  final SearchIntentModel? intent;
  final int took;
  final String? correctedQuery;
  final bool? hasCorrection;
  final List<String>? suggestions;
  final List<String>? hotSearches;
  final bool? hasNext;

  SearchResponseModel({
    required this.total,
    required this.page,
    required this.size,
    required this.totalPages,
    required this.results,
    this.intent,
    required this.took,
    this.correctedQuery,
    this.hasCorrection,
    this.suggestions,
    this.hotSearches,
    this.hasNext,
  });

  factory SearchResponseModel.fromJson(Map<String, dynamic> json) {
    return SearchResponseModel(
      total: json['total'] ?? 0,
      page: json['page'] ?? 0,
      size: json['size'] ?? 0,
      totalPages: json['totalPages'] ?? 0,
      results: (json['results'] as List?)
              ?.map((e) => PoiSearchModel.fromJson(e))
              .toList() ??
          [],
      intent: json['intent'] != null
          ? SearchIntentModel.fromJson(json['intent'])
          : null,
      took: json['took'] ?? 0,
      correctedQuery: json['correctedQuery'],
      hasCorrection: json['hasCorrection'],
      suggestions: json['suggestions'] != null
          ? List<String>.from(json['suggestions'])
          : null,
      hotSearches: json['hotSearches'] != null
          ? List<String>.from(json['hotSearches'])
          : null,
      hasNext: json['hasNext'],
    );
  }
}
