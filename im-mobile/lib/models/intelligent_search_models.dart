import 'package:flutter/foundation.dart';
import 'package:im_mobile/models/poi_model.dart';

/// 智能搜索结果模型
class IntelligentSearchResult {
  final int? searchId;
  final String? sessionId;
  final String originalQuery;
  final String? understoodIntent;
  final String? intentType;
  final double? intentConfidence;
  final List<ExtractedEntity>? extractedEntities;
  final bool? isMultiRound;
  final int? dialogRound;
  final String? smartReply;
  final List<SearchResultItem> results;
  final int totalCount;
  final int pageNum;
  final int pageSize;
  final int totalPages;
  final int? searchTimeMs;
  final bool? isZeroResult;
  final List<String>? suggestedQueries;
  final List<String>? hotSearches;
  final DateTime? searchTime;

  IntelligentSearchResult({
    this.searchId,
    this.sessionId,
    required this.originalQuery,
    this.understoodIntent,
    this.intentType,
    this.intentConfidence,
    this.extractedEntities,
    this.isMultiRound,
    this.dialogRound,
    this.smartReply,
    required this.results,
    required this.totalCount,
    required this.pageNum,
    required this.pageSize,
    required this.totalPages,
    this.searchTimeMs,
    this.isZeroResult,
    this.suggestedQueries,
    this.hotSearches,
    this.searchTime,
  });

  factory IntelligentSearchResult.fromJson(Map<String, dynamic> json) {
    return IntelligentSearchResult(
      searchId: json['searchId'],
      sessionId: json['sessionId'],
      originalQuery: json['originalQuery'] ?? '',
      understoodIntent: json['understoodIntent'],
      intentType: json['intentType'],
      intentConfidence: json['intentConfidence']?.toDouble(),
      extractedEntities: (json['extractedEntities'] as List?)
          ?.map((e) => ExtractedEntity.fromJson(e))
          .toList(),
      isMultiRound: json['isMultiRound'],
      dialogRound: json['dialogRound'],
      smartReply: json['smartReply'],
      results: (json['results'] as List?)
          ?.map((e) => SearchResultItem.fromJson(e))
          .toList() ?? [],
      totalCount: json['totalCount'] ?? 0,
      pageNum: json['pageNum'] ?? 1,
      pageSize: json['pageSize'] ?? 20,
      totalPages: json['totalPages'] ?? 0,
      searchTimeMs: json['searchTimeMs'],
      isZeroResult: json['isZeroResult'],
      suggestedQueries: (json['suggestedQueries'] as List?)
          ?.map((e) => e.toString())
          .toList(),
      hotSearches: (json['hotSearches'] as List?)
          ?.map((e) => e.toString())
          .toList(),
      searchTime: json['searchTime'] != null
          ? DateTime.parse(json['searchTime'])
          : null,
    );
  }
}

/// 提取的实体模型
class ExtractedEntity {
  final String entityType;
  final String entityValue;
  final String originalText;
  final double? confidence;

  ExtractedEntity({
    required this.entityType,
    required this.entityValue,
    required this.originalText,
    this.confidence,
  });

  factory ExtractedEntity.fromJson(Map<String, dynamic> json) {
    return ExtractedEntity(
      entityType: json['entityType'] ?? '',
      entityValue: json['entityValue'] ?? '',
      originalText: json['originalText'] ?? '',
      confidence: json['confidence']?.toDouble(),
    );
  }
}

/// 搜索结果项模型
class SearchResultItem {
  final int poiId;
  final String name;
  final String category;
  final String? categoryName;
  final String? mainImage;
  final List<String>? images;
  final double? rating;
  final int? ratingCount;
  final int? avgPrice;
  final int? distance;
  final String? distanceText;
  final String? address;
  final double? longitude;
  final double? latitude;
  final String? businessHours;
  final bool? isOpen;
  final String? phone;
  final List<String>? tags;
  final List<String>? promotions;
  final int? queueCount;
  final bool? canReserve;
  final bool? hasCoupon;
  final double? relevanceScore;
  final double? sortScore;
  final String? recommendReason;
  final List<FriendRecommend>? friendRecommends;
  final Map<String, dynamic>? extraProperties;

  SearchResultItem({
    required this.poiId,
    required this.name,
    required this.category,
    this.categoryName,
    this.mainImage,
    this.images,
    this.rating,
    this.ratingCount,
    this.avgPrice,
    this.distance,
    this.distanceText,
    this.address,
    this.longitude,
    this.latitude,
    this.businessHours,
    this.isOpen,
    this.phone,
    this.tags,
    this.promotions,
    this.queueCount,
    this.canReserve,
    this.hasCoupon,
    this.relevanceScore,
    this.sortScore,
    this.recommendReason,
    this.friendRecommends,
    this.extraProperties,
  });

  factory SearchResultItem.fromJson(Map<String, dynamic> json) {
    return SearchResultItem(
      poiId: json['poiId'] ?? 0,
      name: json['name'] ?? '',
      category: json['category'] ?? '',
      categoryName: json['categoryName'],
      mainImage: json['mainImage'],
      images: (json['images'] as List?)
          ?.map((e) => e.toString())
          .toList(),
      rating: json['rating']?.toDouble(),
      ratingCount: json['ratingCount'],
      avgPrice: json['avgPrice'],
      distance: json['distance'],
      distanceText: json['distanceText'],
      address: json['address'],
      longitude: json['longitude']?.toDouble(),
      latitude: json['latitude']?.toDouble(),
      businessHours: json['businessHours'],
      isOpen: json['isOpen'],
      phone: json['phone'],
      tags: (json['tags'] as List?)
          ?.map((e) => e.toString())
          .toList(),
      promotions: (json['promotions'] as List?)
          ?.map((e) => e.toString())
          .toList(),
      queueCount: json['queueCount'],
      canReserve: json['canReserve'],
      hasCoupon: json['hasCoupon'],
      relevanceScore: json['relevanceScore']?.toDouble(),
      sortScore: json['sortScore']?.toDouble(),
      recommendReason: json['recommendReason'],
      friendRecommends: (json['friendRecommends'] as List?)
          ?.map((e) => FriendRecommend.fromJson(e))
          .toList(),
      extraProperties: json['extraProperties'],
    );
  }

  PoiModel toPoiModel() {
    return PoiModel(
      id: poiId,
      name: name,
      category: category,
      categoryName: categoryName,
      mainImage: mainImage,
      images: images,
      rating: rating,
      ratingCount: ratingCount,
      avgPrice: avgPrice,
      distance: distance,
      address: address,
      longitude: longitude,
      latitude: latitude,
      businessHours: businessHours,
      isOpen: isOpen,
      phone: phone,
      tags: tags,
    );
  }
}

/// 好友推荐模型
class FriendRecommend {
  final int friendId;
  final String friendName;
  final String? friendAvatar;
  final String recommendType;
  final String? recommendText;

  FriendRecommend({
    required this.friendId,
    required this.friendName,
    this.friendAvatar,
    required this.recommendType,
    this.recommendText,
  });

  factory FriendRecommend.fromJson(Map<String, dynamic> json) {
    return FriendRecommend(
      friendId: json['friendId'] ?? 0,
      friendName: json['friendName'] ?? '',
      friendAvatar: json['friendAvatar'],
      recommendType: json['recommendType'] ?? '',
      recommendText: json['recommendText'],
    );
  }
}

/// 搜索建议模型
class SearchSuggestion {
  final String keyword;
  final List<SuggestionItem> suggestions;
  final List<String>? historySearches;
  final List<HotSearch>? hotSearches;
  final List<String>? discoveryKeywords;

  SearchSuggestion({
    required this.keyword,
    required this.suggestions,
    this.historySearches,
    this.hotSearches,
    this.discoveryKeywords,
  });

  factory SearchSuggestion.fromJson(Map<String, dynamic> json) {
    return SearchSuggestion(
      keyword: json['keyword'] ?? '',
      suggestions: (json['suggestions'] as List?)
          ?.map((e) => SuggestionItem.fromJson(e))
          .toList() ?? [],
      historySearches: (json['historySearches'] as List?)
          ?.map((e) => e.toString())
          .toList(),
      hotSearches: (json['hotSearches'] as List?)
          ?.map((e) => HotSearch.fromJson(e))
          .toList(),
      discoveryKeywords: (json['discoveryKeywords'] as List?)
          ?.map((e) => e.toString())
          .toList(),
    );
  }
}

/// 建议项模型
class SuggestionItem {
  final String type;
  final String text;
  final String? highlightedText;
  final int? poiId;
  final String? poiName;
  final String? categoryName;
  final String? icon;
  final String? distance;
  final double? rating;

  SuggestionItem({
    required this.type,
    required this.text,
    this.highlightedText,
    this.poiId,
    this.poiName,
    this.categoryName,
    this.icon,
    this.distance,
    this.rating,
  });

  factory SuggestionItem.fromJson(Map<String, dynamic> json) {
    return SuggestionItem(
      type: json['type'] ?? '',
      text: json['text'] ?? '',
      highlightedText: json['highlightedText'],
      poiId: json['poiId'],
      poiName: json['poiName'],
      categoryName: json['categoryName'],
      icon: json['icon'],
      distance: json['distance'],
      rating: json['rating']?.toDouble(),
    );
  }
}

/// 热门搜索模型
class HotSearch {
  final int rank;
  final String keyword;
  final int heat;
  final String trend;

  HotSearch({
    required this.rank,
    required this.keyword,
    required this.heat,
    required this.trend,
  });

  factory HotSearch.fromJson(Map<String, dynamic> json) {
    return HotSearch(
      rank: json['rank'] ?? 0,
      keyword: json['keyword'] ?? '',
      heat: json['heat'] ?? 0,
      trend: json['trend'] ?? 'FLAT',
    );
  }
}
