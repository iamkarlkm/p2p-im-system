// KPI概览模型
import 'package:flutter/foundation.dart';

class KPIOverviewModel {
  final double todayRevenue;
  final double revenueGrowth;
  final double yesterdayRevenue;
  final int todayOrders;
  final double orderGrowth;
  final int yesterdayOrders;
  final int todayVisitors;
  final double visitorGrowth;
  final double conversionRate;
  final double viewToVisitRate;
  final double visitToOrderRate;
  final double avgOrderValue;
  final double avgOrderValueGrowth;
  final double customerRating;
  final double ratingChange;
  final int newReviewsToday;
  final double replyRate;
  final int categoryRank;
  final int districtRank;
  final int currentOnlineUsers;
  final double weekGrowthRate;
  final double monthGrowthRate;

  KPIOverviewModel({
    required this.todayRevenue,
    required this.revenueGrowth,
    required this.yesterdayRevenue,
    required this.todayOrders,
    required this.orderGrowth,
    required this.yesterdayOrders,
    required this.todayVisitors,
    required this.visitorGrowth,
    required this.conversionRate,
    required this.viewToVisitRate,
    required this.visitToOrderRate,
    required this.avgOrderValue,
    required this.avgOrderValueGrowth,
    required this.customerRating,
    required this.ratingChange,
    required this.newReviewsToday,
    required this.replyRate,
    required this.categoryRank,
    required this.districtRank,
    required this.currentOnlineUsers,
    required this.weekGrowthRate,
    required this.monthGrowthRate,
  });

  factory KPIOverviewModel.fromJson(Map<String, dynamic> json) {
    return KPIOverviewModel(
      todayRevenue: (json['todayRevenue'] ?? 0).toDouble(),
      revenueGrowth: (json['revenueGrowth'] ?? 0).toDouble(),
      yesterdayRevenue: (json['yesterdayRevenue'] ?? 0).toDouble(),
      todayOrders: json['todayOrders'] ?? 0,
      orderGrowth: (json['orderGrowth'] ?? 0).toDouble(),
      yesterdayOrders: json['yesterdayOrders'] ?? 0,
      todayVisitors: json['todayVisitors'] ?? 0,
      visitorGrowth: (json['visitorGrowth'] ?? 0).toDouble(),
      conversionRate: (json['conversionRate'] ?? 0).toDouble(),
      viewToVisitRate: (json['viewToVisitRate'] ?? 0).toDouble(),
      visitToOrderRate: (json['visitToOrderRate'] ?? 0).toDouble(),
      avgOrderValue: (json['avgOrderValue'] ?? 0).toDouble(),
      avgOrderValueGrowth: (json['avgOrderValueGrowth'] ?? 0).toDouble(),
      customerRating: (json['customerRating'] ?? 0).toDouble(),
      ratingChange: (json['ratingChange'] ?? 0).toDouble(),
      newReviewsToday: json['newReviewsToday'] ?? 0,
      replyRate: (json['replyRate'] ?? 0).toDouble(),
      categoryRank: json['categoryRank'] ?? 0,
      districtRank: json['districtRank'] ?? 0,
      currentOnlineUsers: json['currentOnlineUsers'] ?? 0,
      weekGrowthRate: (json['weekGrowthRate'] ?? 0).toDouble(),
      monthGrowthRate: (json['monthGrowthRate'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'todayRevenue': todayRevenue,
    'revenueGrowth': revenueGrowth,
    'yesterdayRevenue': yesterdayRevenue,
    'todayOrders': todayOrders,
    'orderGrowth': orderGrowth,
    'yesterdayOrders': yesterdayOrders,
    'todayVisitors': todayVisitors,
    'visitorGrowth': visitorGrowth,
    'conversionRate': conversionRate,
    'viewToVisitRate': viewToVisitRate,
    'visitToOrderRate': visitToOrderRate,
    'avgOrderValue': avgOrderValue,
    'avgOrderValueGrowth': avgOrderValueGrowth,
    'customerRating': customerRating,
    'ratingChange': ratingChange,
    'newReviewsToday': newReviewsToday,
    'replyRate': replyRate,
    'categoryRank': categoryRank,
    'districtRank': districtRank,
    'currentOnlineUsers': currentOnlineUsers,
    'weekGrowthRate': weekGrowthRate,
    'monthGrowthRate': monthGrowthRate,
  };

  // 获取营收状态
  String get revenueStatus {
    if (revenueGrowth > 10) return 'excellent';
    if (revenueGrowth > 0) return 'good';
    if (revenueGrowth > -10) return 'warning';
    return 'danger';
  }

  // 获取排名状态
  String get rankStatus {
    if (categoryRank <= 3) return 'excellent';
    if (categoryRank <= 10) return 'good';
    if (categoryRank <= 20) return 'normal';
    return 'needs_improvement';
  }
}
