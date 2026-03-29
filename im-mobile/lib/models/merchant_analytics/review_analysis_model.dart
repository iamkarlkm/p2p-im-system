// 移动端评价分析模型
import 'package:flutter/foundation.dart';

// 评价分析模型
class ReviewAnalysisModel {
  final int merchantId;
  final String statDate;
  final double overallRating;
  final double tasteRating;
  final double environmentRating;
  final double serviceRating;
  final double valueRating;
  final int totalReviews;
  final RatingDistribution ratingDistribution;
  final double positiveRate;
  final double negativeRate;
  final double neutralRate;
  final int todayNewReviews;
  final double todayAvgRating;
  final int withPhotoReviews;
  final int withVideoReviews;
  final double replyRate;
  final double avgReplyTimeHours;
  final int pendingReplyCount;
  final List<ReviewKeyword> positiveKeywords;
  final List<ReviewKeyword> negativeKeywords;
  final double categoryAvgRating;
  final int categoryRank;
  final bool negativeAlert;
  final String alertMessage;
  final List<ReviewTrendItem> trend;

  ReviewAnalysisModel({
    required this.merchantId,
    required this.statDate,
    required this.overallRating,
    required this.tasteRating,
    required this.environmentRating,
    required this.serviceRating,
    required this.valueRating,
    required this.totalReviews,
    required this.ratingDistribution,
    required this.positiveRate,
    required this.negativeRate,
    required this.neutralRate,
    required this.todayNewReviews,
    required this.todayAvgRating,
    required this.withPhotoReviews,
    required this.withVideoReviews,
    required this.replyRate,
    required this.avgReplyTimeHours,
    required this.pendingReplyCount,
    required this.positiveKeywords,
    required this.negativeKeywords,
    required this.categoryAvgRating,
    required this.categoryRank,
    required this.negativeAlert,
    required this.alertMessage,
    required this.trend,
  });

  factory ReviewAnalysisModel.fromJson(Map<String, dynamic> json) {
    return ReviewAnalysisModel(
      merchantId: json['merchantId'] ?? 0,
      statDate: json['statDate'] ?? '',
      overallRating: (json['overallRating'] ?? 0).toDouble(),
      tasteRating: (json['tasteRating'] ?? 0).toDouble(),
      environmentRating: (json['environmentRating'] ?? 0).toDouble(),
      serviceRating: (json['serviceRating'] ?? 0).toDouble(),
      valueRating: (json['valueRating'] ?? 0).toDouble(),
      totalReviews: json['totalReviews'] ?? 0,
      ratingDistribution: RatingDistribution.fromJson(json['ratingDistribution'] ?? {}),
      positiveRate: (json['positiveRate'] ?? 0).toDouble(),
      negativeRate: (json['negativeRate'] ?? 0).toDouble(),
      neutralRate: (json['neutralRate'] ?? 0).toDouble(),
      todayNewReviews: json['todayNewReviews'] ?? 0,
      todayAvgRating: (json['todayAvgRating'] ?? 0).toDouble(),
      withPhotoReviews: json['withPhotoReviews'] ?? 0,
      withVideoReviews: json['withVideoReviews'] ?? 0,
      replyRate: (json['replyRate'] ?? 0).toDouble(),
      avgReplyTimeHours: (json['avgReplyTimeHours'] ?? 0).toDouble(),
      pendingReplyCount: json['pendingReplyCount'] ?? 0,
      positiveKeywords: (json['positiveKeywords'] as List? ?? [])
          .map((e) => ReviewKeyword.fromJson(e))
          .toList(),
      negativeKeywords: (json['negativeKeywords'] as List? ?? [])
          .map((e) => ReviewKeyword.fromJson(e))
          .toList(),
      categoryAvgRating: (json['categoryAvgRating'] ?? 0).toDouble(),
      categoryRank: json['categoryRank'] ?? 0,
      negativeAlert: json['negativeAlert'] ?? false,
      alertMessage: json['alertMessage'] ?? '',
      trend: (json['trend'] as List? ?? [])
          .map((e) => ReviewTrendItem.fromJson(e))
          .toList(),
    );
  }

  Map<String, dynamic> toJson() => {
    'merchantId': merchantId,
    'statDate': statDate,
    'overallRating': overallRating,
    'tasteRating': tasteRating,
    'environmentRating': environmentRating,
    'serviceRating': serviceRating,
    'valueRating': valueRating,
    'totalReviews': totalReviews,
    'ratingDistribution': ratingDistribution.toJson(),
    'positiveRate': positiveRate,
    'negativeRate': negativeRate,
    'neutralRate': neutralRate,
    'todayNewReviews': todayNewReviews,
    'todayAvgRating': todayAvgRating,
    'withPhotoReviews': withPhotoReviews,
    'withVideoReviews': withVideoReviews,
    'replyRate': replyRate,
    'avgReplyTimeHours': avgReplyTimeHours,
    'pendingReplyCount': pendingReplyCount,
    'positiveKeywords': positiveKeywords.map((e) => e.toJson()).toList(),
    'negativeKeywords': negativeKeywords.map((e) => e.toJson()).toList(),
    'categoryAvgRating': categoryAvgRating,
    'categoryRank': categoryRank,
    'negativeAlert': negativeAlert,
    'alertMessage': alertMessage,
    'trend': trend.map((e) => e.toJson()).toList(),
  };
}

// 评分分布
class RatingDistribution {
  final int fiveStarCount;
  final int fourStarCount;
  final int threeStarCount;
  final int twoStarCount;
  final int oneStarCount;

  RatingDistribution({
    required this.fiveStarCount,
    required this.fourStarCount,
    required this.threeStarCount,
    required this.twoStarCount,
    required this.oneStarCount,
  });

  factory RatingDistribution.fromJson(Map<String, dynamic> json) {
    return RatingDistribution(
      fiveStarCount: json['fiveStarCount'] ?? 0,
      fourStarCount: json['fourStarCount'] ?? 0,
      threeStarCount: json['threeStarCount'] ?? 0,
      twoStarCount: json['twoStarCount'] ?? 0,
      oneStarCount: json['oneStarCount'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() => {
    'fiveStarCount': fiveStarCount,
    'fourStarCount': fourStarCount,
    'threeStarCount': threeStarCount,
    'twoStarCount': twoStarCount,
    'oneStarCount': oneStarCount,
  };

  int get total => fiveStarCount + fourStarCount + threeStarCount + twoStarCount + oneStarCount;

  double getFiveStarRate() => total > 0 ? (fiveStarCount / total * 100) : 0;
  double getFourStarRate() => total > 0 ? (fourStarCount / total * 100) : 0;
}

// 评价关键词
class ReviewKeyword {
  final String word;
  final int count;
  final double sentiment;

  ReviewKeyword({
    required this.word,
    required this.count,
    required this.sentiment,
  });

  factory ReviewKeyword.fromJson(Map<String, dynamic> json) {
    return ReviewKeyword(
      word: json['word'] ?? '',
      count: json['count'] ?? 0,
      sentiment: (json['sentiment'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'word': word,
    'count': count,
    'sentiment': sentiment,
  };
}

// 评价趋势项
class ReviewTrendItem {
  final String date;
  final int newReviews;
  final double avgRating;

  ReviewTrendItem({
    required this.date,
    required this.newReviews,
    required this.avgRating,
  });

  factory ReviewTrendItem.fromJson(Map<String, dynamic> json) {
    return ReviewTrendItem(
      date: json['date'] ?? '',
      newReviews: json['newReviews'] ?? 0,
      avgRating: (json['avgRating'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'date': date,
    'newReviews': newReviews,
    'avgRating': avgRating,
  };
}
