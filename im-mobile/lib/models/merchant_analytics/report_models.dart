// 移动端报表模型
import 'package:flutter/foundation.dart';

// 日报模型
class DailyReportModel {
  final String reportDate;
  final double revenue;
  final int orders;
  final int visitors;
  final int newReviews;
  final double avgRating;
  final double revenueGrowth;
  final int newCustomers;
  final int returningCustomers;
  final double conversionRate;
  final double refundRate;
  final double grossProfitRate;
  final int replyCount;

  DailyReportModel({
    required this.reportDate,
    required this.revenue,
    required this.orders,
    required this.visitors,
    required this.newReviews,
    required this.avgRating,
    required this.revenueGrowth,
    required this.newCustomers,
    required this.returningCustomers,
    required this.conversionRate,
    required this.refundRate,
    required this.grossProfitRate,
    required this.replyCount,
  });

  factory DailyReportModel.fromJson(Map<String, dynamic> json) {
    return DailyReportModel(
      reportDate: json['reportDate'] ?? '',
      revenue: (json['revenue'] ?? 0).toDouble(),
      orders: json['orders'] ?? 0,
      visitors: json['visitors'] ?? 0,
      newReviews: json['newReviews'] ?? 0,
      avgRating: (json['avgRating'] ?? 0).toDouble(),
      revenueGrowth: (json['revenueGrowth'] ?? 0).toDouble(),
      newCustomers: json['newCustomers'] ?? 0,
      returningCustomers: json['returningCustomers'] ?? 0,
      conversionRate: (json['conversionRate'] ?? 0).toDouble(),
      refundRate: (json['refundRate'] ?? 0).toDouble(),
      grossProfitRate: (json['grossProfitRate'] ?? 0).toDouble(),
      replyCount: json['replyCount'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() => {
    'reportDate': reportDate,
    'revenue': revenue,
    'orders': orders,
    'visitors': visitors,
    'newReviews': newReviews,
    'avgRating': avgRating,
    'revenueGrowth': revenueGrowth,
    'newCustomers': newCustomers,
    'returningCustomers': returningCustomers,
    'conversionRate': conversionRate,
    'refundRate': refundRate,
    'grossProfitRate': grossProfitRate,
    'replyCount': replyCount,
  };
}

// 周报模型
class WeeklyReportModel {
  final String weekEndDate;
  final double totalRevenue;
  final int totalOrders;
  final int totalVisitors;
  final int totalNewReviews;
  final double avgDailyRevenue;
  final double growthRate;
  final double avgRating;
  final int newCustomers;
  final List<DailySummary> dailySummaries;

  WeeklyReportModel({
    required this.weekEndDate,
    required this.totalRevenue,
    required this.totalOrders,
    required this.totalVisitors,
    required this.totalNewReviews,
    required this.avgDailyRevenue,
    required this.growthRate,
    required this.avgRating,
    required this.newCustomers,
    required this.dailySummaries,
  });

  factory WeeklyReportModel.fromJson(Map<String, dynamic> json) {
    return WeeklyReportModel(
      weekEndDate: json['weekEndDate'] ?? '',
      totalRevenue: (json['totalRevenue'] ?? 0).toDouble(),
      totalOrders: json['totalOrders'] ?? 0,
      totalVisitors: json['totalVisitors'] ?? 0,
      totalNewReviews: json['totalNewReviews'] ?? 0,
      avgDailyRevenue: (json['avgDailyRevenue'] ?? 0).toDouble(),
      growthRate: (json['growthRate'] ?? 0).toDouble(),
      avgRating: (json['avgRating'] ?? 0).toDouble(),
      newCustomers: json['newCustomers'] ?? 0,
      dailySummaries: (json['dailySummaries'] as List? ?? [])
          .map((e) => DailySummary.fromJson(e))
          .toList(),
    );
  }

  Map<String, dynamic> toJson() => {
    'weekEndDate': weekEndDate,
    'totalRevenue': totalRevenue,
    'totalOrders': totalOrders,
    'totalVisitors': totalVisitors,
    'totalNewReviews': totalNewReviews,
    'avgDailyRevenue': avgDailyRevenue,
    'growthRate': growthRate,
    'avgRating': avgRating,
    'newCustomers': newCustomers,
    'dailySummaries': dailySummaries.map((e) => e.toJson()).toList(),
  };
}

// 月报模型
class MonthlyReportModel {
  final int year;
  final int month;
  final double totalRevenue;
  final int totalOrders;
  final int totalVisitors;
  final double avgDailyRevenue;
  final double momGrowth;
  final double yoyGrowth;
  final double avgRating;
  final int newCustomers;
  final int activeMembers;
  final double memberContributionRate;
  final List<WeeklySummary> weeklySummaries;

  MonthlyReportModel({
    required this.year,
    required this.month,
    required this.totalRevenue,
    required this.totalOrders,
    required this.totalVisitors,
    required this.avgDailyRevenue,
    required this.momGrowth,
    required this.yoyGrowth,
    required this.avgRating,
    required this.newCustomers,
    required this.activeMembers,
    required this.memberContributionRate,
    required this.weeklySummaries,
  });

  factory MonthlyReportModel.fromJson(Map<String, dynamic> json) {
    return MonthlyReportModel(
      year: json['year'] ?? 0,
      month: json['month'] ?? 0,
      totalRevenue: (json['totalRevenue'] ?? 0).toDouble(),
      totalOrders: json['totalOrders'] ?? 0,
      totalVisitors: json['totalVisitors'] ?? 0,
      avgDailyRevenue: (json['avgDailyRevenue'] ?? 0).toDouble(),
      momGrowth: (json['momGrowth'] ?? 0).toDouble(),
      yoyGrowth: (json['yoyGrowth'] ?? 0).toDouble(),
      avgRating: (json['avgRating'] ?? 0).toDouble(),
      newCustomers: json['newCustomers'] ?? 0,
      activeMembers: json['activeMembers'] ?? 0,
      memberContributionRate: (json['memberContributionRate'] ?? 0).toDouble(),
      weeklySummaries: (json['weeklySummaries'] as List? ?? [])
          .map((e) => WeeklySummary.fromJson(e))
          .toList(),
    );
  }

  Map<String, dynamic> toJson() => {
    'year': year,
    'month': month,
    'totalRevenue': totalRevenue,
    'totalOrders': totalOrders,
    'totalVisitors': totalVisitors,
    'avgDailyRevenue': avgDailyRevenue,
    'momGrowth': momGrowth,
    'yoyGrowth': yoyGrowth,
    'avgRating': avgRating,
    'newCustomers': newCustomers,
    'activeMembers': activeMembers,
    'memberContributionRate': memberContributionRate,
    'weeklySummaries': weeklySummaries.map((e) => e.toJson()).toList(),
  };

  String get monthLabel => '$year年${month}月';
}

// 日汇总
class DailySummary {
  final String date;
  final double revenue;
  final int orders;
  final int visitors;
  final double avgRating;

  DailySummary({
    required this.date,
    required this.revenue,
    required this.orders,
    required this.visitors,
    required this.avgRating,
  });

  factory DailySummary.fromJson(Map<String, dynamic> json) {
    return DailySummary(
      date: json['date'] ?? '',
      revenue: (json['revenue'] ?? 0).toDouble(),
      orders: json['orders'] ?? 0,
      visitors: json['visitors'] ?? 0,
      avgRating: (json['avgRating'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'date': date,
    'revenue': revenue,
    'orders': orders,
    'visitors': visitors,
    'avgRating': avgRating,
  };
}

// 周汇总
class WeeklySummary {
  final int weekNumber;
  final double revenue;
  final int orders;
  final int visitors;
  final double growthRate;

  WeeklySummary({
    required this.weekNumber,
    required this.revenue,
    required this.orders,
    required this.visitors,
    required this.growthRate,
  });

  factory WeeklySummary.fromJson(Map<String, dynamic> json) {
    return WeeklySummary(
      weekNumber: json['weekNumber'] ?? 0,
      revenue: (json['revenue'] ?? 0).toDouble(),
      orders: json['orders'] ?? 0,
      visitors: json['visitors'] ?? 0,
      growthRate: (json['growthRate'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'weekNumber': weekNumber,
    'revenue': revenue,
    'orders': orders,
    'visitors': visitors,
    'growthRate': growthRate,
  };
}
