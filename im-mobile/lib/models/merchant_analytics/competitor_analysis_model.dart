// 移动端竞品分析模型
import 'package:flutter/foundation.dart';

// 竞品分析模型
class CompetitorAnalysisModel {
  final int merchantId;
  final int competitorId;
  final String competitorName;
  final String competitorCategory;
  final String competitorDistrict;
  final double distanceKm;
  final double myRating;
  final double competitorRating;
  final double ratingGap;
  final int myReviewCount;
  final int competitorReviewCount;
  final int reviewCountGap;
  final double myAvgPrice;
  final double competitorAvgPrice;
  final double priceGap;
  final double priceAdvantage;
  final int myPopularityScore;
  final int competitorPopularityScore;
  final int popularityGap;
  final int myCategoryRank;
  final int competitorCategoryRank;
  final int rankGap;
  final int myMonthlyVisitors;
  final int competitorMonthlyVisitors;
  final double visitorShareRatio;
  final double myMarketShare;
  final double competitorMarketShare;
  final int totalCompetitorsInArea;
  final String recommendedStrategy;
  final String priorityActions;

  CompetitorAnalysisModel({
    required this.merchantId,
    required this.competitorId,
    required this.competitorName,
    required this.competitorCategory,
    required this.competitorDistrict,
    required this.distanceKm,
    required this.myRating,
    required this.competitorRating,
    required this.ratingGap,
    required this.myReviewCount,
    required this.competitorReviewCount,
    required this.reviewCountGap,
    required this.myAvgPrice,
    required this.competitorAvgPrice,
    required this.priceGap,
    required this.priceAdvantage,
    required this.myPopularityScore,
    required this.competitorPopularityScore,
    required this.popularityGap,
    required this.myCategoryRank,
    required this.competitorCategoryRank,
    required this.rankGap,
    required this.myMonthlyVisitors,
    required this.competitorMonthlyVisitors,
    required this.visitorShareRatio,
    required this.myMarketShare,
    required this.competitorMarketShare,
    required this.totalCompetitorsInArea,
    required this.recommendedStrategy,
    required this.priorityActions,
  });

  factory CompetitorAnalysisModel.fromJson(Map<String, dynamic> json) {
    return CompetitorAnalysisModel(
      merchantId: json['merchantId'] ?? 0,
      competitorId: json['competitorId'] ?? 0,
      competitorName: json['competitorName'] ?? '',
      competitorCategory: json['competitorCategory'] ?? '',
      competitorDistrict: json['competitorDistrict'] ?? '',
      distanceKm: (json['distanceKm'] ?? 0).toDouble(),
      myRating: (json['myRating'] ?? 0).toDouble(),
      competitorRating: (json['competitorRating'] ?? 0).toDouble(),
      ratingGap: (json['ratingGap'] ?? 0).toDouble(),
      myReviewCount: json['myReviewCount'] ?? 0,
      competitorReviewCount: json['competitorReviewCount'] ?? 0,
      reviewCountGap: json['reviewCountGap'] ?? 0,
      myAvgPrice: (json['myAvgPrice'] ?? 0).toDouble(),
      competitorAvgPrice: (json['competitorAvgPrice'] ?? 0).toDouble(),
      priceGap: (json['priceGap'] ?? 0).toDouble(),
      priceAdvantage: (json['priceAdvantage'] ?? 0).toDouble(),
      myPopularityScore: json['myPopularityScore'] ?? 0,
      competitorPopularityScore: json['competitorPopularityScore'] ?? 0,
      popularityGap: json['popularityGap'] ?? 0,
      myCategoryRank: json['myCategoryRank'] ?? 0,
      competitorCategoryRank: json['competitorCategoryRank'] ?? 0,
      rankGap: json['rankGap'] ?? 0,
      myMonthlyVisitors: json['myMonthlyVisitors'] ?? 0,
      competitorMonthlyVisitors: json['competitorMonthlyVisitors'] ?? 0,
      visitorShareRatio: (json['visitorShareRatio'] ?? 0).toDouble(),
      myMarketShare: (json['myMarketShare'] ?? 0).toDouble(),
      competitorMarketShare: (json['competitorMarketShare'] ?? 0).toDouble(),
      totalCompetitorsInArea: json['totalCompetitorsInArea'] ?? 0,
      recommendedStrategy: json['recommendedStrategy'] ?? '',
      priorityActions: json['priorityActions'] ?? '',
    );
  }

  Map<String, dynamic> toJson() => {
    'merchantId': merchantId,
    'competitorId': competitorId,
    'competitorName': competitorName,
    'competitorCategory': competitorCategory,
    'competitorDistrict': competitorDistrict,
    'distanceKm': distanceKm,
    'myRating': myRating,
    'competitorRating': competitorRating,
    'ratingGap': ratingGap,
    'myReviewCount': myReviewCount,
    'competitorReviewCount': competitorReviewCount,
    'reviewCountGap': reviewCountGap,
    'myAvgPrice': myAvgPrice,
    'competitorAvgPrice': competitorAvgPrice,
    'priceGap': priceGap,
    'priceAdvantage': priceAdvantage,
    'myPopularityScore': myPopularityScore,
    'competitorPopularityScore': competitorPopularityScore,
    'popularityGap': popularityGap,
    'myCategoryRank': myCategoryRank,
    'competitorCategoryRank': competitorCategoryRank,
    'rankGap': rankGap,
    'myMonthlyVisitors': myMonthlyVisitors,
    'competitorMonthlyVisitors': competitorMonthlyVisitors,
    'visitorShareRatio': visitorShareRatio,
    'myMarketShare': myMarketShare,
    'competitorMarketShare': competitorMarketShare,
    'totalCompetitorsInArea': totalCompetitorsInArea,
    'recommendedStrategy': recommendedStrategy,
    'priorityActions': priorityActions,
  };

  // 是否有评分优势
  bool get hasRatingAdvantage => myRating > competitorRating;
  
  // 是否有价格优势
  bool get hasPriceAdvantage => myAvgPrice < competitorAvgPrice;
  
  // 排名是否领先
  bool get isRankLeading => myCategoryRank < competitorCategoryRank;
}

// 市场排名
class MarketRanking {
  final int categoryRank;
  final int districtRank;
  final int totalInCategory;
  final int totalInDistrict;
  final int ratingRank;

  MarketRanking({
    required this.categoryRank,
    required this.districtRank,
    required this.totalInCategory,
    required this.totalInDistrict,
    required this.ratingRank,
  });

  factory MarketRanking.fromJson(Map<String, dynamic> json) {
    return MarketRanking(
      categoryRank: json['categoryRank'] ?? 0,
      districtRank: json['districtRank'] ?? 0,
      totalInCategory: json['totalInCategory'] ?? 0,
      totalInDistrict: json['totalInDistrict'] ?? 0,
      ratingRank: json['ratingRank'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() => {
    'categoryRank': categoryRank,
    'districtRank': districtRank,
    'totalInCategory': totalInCategory,
    'totalInDistrict': totalInDistrict,
    'ratingRank': ratingRank,
  };

  // 品类排名百分比（越小越好）
  double get categoryRankPercent => totalInCategory > 0 
      ? (categoryRank / totalInCategory * 100) 
      : 0;
  
  // 商圈排名百分比
  double get districtRankPercent => totalInDistrict > 0 
      ? (districtRank / totalInDistrict * 100) 
      : 0;
}

// 市场份额
class MarketShare {
  final double visitorShare;
  final double revenueShare;
  final double reviewShare;

  MarketShare({
    required this.visitorShare,
    required this.revenueShare,
    required this.reviewShare,
  });

  factory MarketShare.fromJson(Map<String, dynamic> json) {
    return MarketShare(
      visitorShare: (json['visitorShare'] ?? 0).toDouble(),
      revenueShare: (json['revenueShare'] ?? 0).toDouble(),
      reviewShare: (json['reviewShare'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'visitorShare': visitorShare,
    'revenueShare': revenueShare,
    'reviewShare': reviewShare,
  };

  // 综合市场份额指数
  double get compositeIndex => (visitorShare + revenueShare + reviewShare) / 3;
}

// 竞品对比维度
class CompetitorComparisonDimension {
  final String dimension;
  final double myScore;
  final double competitorScore;
  final String advantage; // 'me', 'competitor', 'equal'

  CompetitorComparisonDimension({
    required this.dimension,
    required this.myScore,
    required this.competitorScore,
    required this.advantage,
  });

  factory CompetitorComparisonDimension.fromJson(Map<String, dynamic> json) {
    return CompetitorComparisonDimension(
      dimension: json['dimension'] ?? '',
      myScore: (json['myScore'] ?? 0).toDouble(),
      competitorScore: (json['competitorScore'] ?? 0).toDouble(),
      advantage: json['advantage'] ?? '',
    );
  }

  Map<String, dynamic> toJson() => {
    'dimension': dimension,
    'myScore': myScore,
    'competitorScore': competitorScore,
    'advantage': advantage,
  };

  bool get isMyAdvantage => advantage == 'me';
  bool get isCompetitorAdvantage => advantage == 'competitor';
}
