// 移动端商户数据分析模型
import 'package:flutter/foundation.dart';

// 经营仪表盘数据
class MerchantDashboardModel {
  final int merchantId;
  final String statDate;
  final int todayVisitors;
  final int todayPageViews;
  final int todayStoreVisits;
  final int todayOrderCount;
  final double todayRevenue;
  final double visitorGrowthRate;
  final double revenueGrowthRate;
  final double orderGrowthRate;
  final double viewToVisitRate;
  final double visitToOrderRate;
  final double overallConversionRate;
  final int newCustomerCount;
  final int returningCustomerCount;
  final double newCustomerRatio;
  final int categoryRank;
  final int districtRank;
  final int currentOnlineUsers;
  final String peakHourRange;
  final List<TrafficTrendItem> sevenDayTrend;
  final CustomerSource sourceDistribution;

  MerchantDashboardModel({
    required this.merchantId,
    required this.statDate,
    required this.todayVisitors,
    required this.todayPageViews,
    required this.todayStoreVisits,
    required this.todayOrderCount,
    required this.todayRevenue,
    required this.visitorGrowthRate,
    required this.revenueGrowthRate,
    required this.orderGrowthRate,
    required this.viewToVisitRate,
    required this.visitToOrderRate,
    required this.overallConversionRate,
    required this.newCustomerCount,
    required this.returningCustomerCount,
    required this.newCustomerRatio,
    required this.categoryRank,
    required this.districtRank,
    required this.currentOnlineUsers,
    required this.peakHourRange,
    required this.sevenDayTrend,
    required this.sourceDistribution,
  });

  factory MerchantDashboardModel.fromJson(Map<String, dynamic> json) {
    return MerchantDashboardModel(
      merchantId: json['merchantId'] ?? 0,
      statDate: json['statDate'] ?? '',
      todayVisitors: json['todayVisitors'] ?? 0,
      todayPageViews: json['todayPageViews'] ?? 0,
      todayStoreVisits: json['todayStoreVisits'] ?? 0,
      todayOrderCount: json['todayOrderCount'] ?? 0,
      todayRevenue: (json['todayRevenue'] ?? 0).toDouble(),
      visitorGrowthRate: (json['visitorGrowthRate'] ?? 0).toDouble(),
      revenueGrowthRate: (json['revenueGrowthRate'] ?? 0).toDouble(),
      orderGrowthRate: (json['orderGrowthRate'] ?? 0).toDouble(),
      viewToVisitRate: (json['viewToVisitRate'] ?? 0).toDouble(),
      visitToOrderRate: (json['visitToOrderRate'] ?? 0).toDouble(),
      overallConversionRate: (json['overallConversionRate'] ?? 0).toDouble(),
      newCustomerCount: json['newCustomerCount'] ?? 0,
      returningCustomerCount: json['returningCustomerCount'] ?? 0,
      newCustomerRatio: (json['newCustomerRatio'] ?? 0).toDouble(),
      categoryRank: json['categoryRank'] ?? 0,
      districtRank: json['districtRank'] ?? 0,
      currentOnlineUsers: json['currentOnlineUsers'] ?? 0,
      peakHourRange: json['peakHourRange'] ?? '',
      sevenDayTrend: (json['sevenDayTrend'] as List? ?? [])
          .map((e) => TrafficTrendItem.fromJson(e))
          .toList(),
      sourceDistribution: CustomerSource.fromJson(json['sourceDistribution'] ?? {}),
    );
  }

  Map<String, dynamic> toJson() => {
    'merchantId': merchantId,
    'statDate': statDate,
    'todayVisitors': todayVisitors,
    'todayPageViews': todayPageViews,
    'todayStoreVisits': todayStoreVisits,
    'todayOrderCount': todayOrderCount,
    'todayRevenue': todayRevenue,
    'visitorGrowthRate': visitorGrowthRate,
    'revenueGrowthRate': revenueGrowthRate,
    'orderGrowthRate': orderGrowthRate,
    'viewToVisitRate': viewToVisitRate,
    'visitToOrderRate': visitToOrderRate,
    'overallConversionRate': overallConversionRate,
    'newCustomerCount': newCustomerCount,
    'returningCustomerCount': returningCustomerCount,
    'newCustomerRatio': newCustomerRatio,
    'categoryRank': categoryRank,
    'districtRank': districtRank,
    'currentOnlineUsers': currentOnlineUsers,
    'peakHourRange': peakHourRange,
    'sevenDayTrend': sevenDayTrend.map((e) => e.toJson()).toList(),
    'sourceDistribution': sourceDistribution.toJson(),
  };
}

// 流量趋势项
class TrafficTrendItem {
  final String date;
  final int visitors;
  final int pageViews;
  final int orders;
  final double revenue;

  TrafficTrendItem({
    required this.date,
    required this.visitors,
    required this.pageViews,
    required this.orders,
    required this.revenue,
  });

  factory TrafficTrendItem.fromJson(Map<String, dynamic> json) {
    return TrafficTrendItem(
      date: json['date'] ?? '',
      visitors: json['visitors'] ?? 0,
      pageViews: json['pageViews'] ?? 0,
      orders: json['orders'] ?? 0,
      revenue: (json['revenue'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'date': date,
    'visitors': visitors,
    'pageViews': pageViews,
    'orders': orders,
    'revenue': revenue,
  };
}

// 客户来源分布
class CustomerSource {
  final int searchSourceCount;
  final int recommendationSourceCount;
  final int directSourceCount;
  final int shareSourceCount;
  final int adSourceCount;

  CustomerSource({
    required this.searchSourceCount,
    required this.recommendationSourceCount,
    required this.directSourceCount,
    required this.shareSourceCount,
    required this.adSourceCount,
  });

  factory CustomerSource.fromJson(Map<String, dynamic> json) {
    return CustomerSource(
      searchSourceCount: json['searchSourceCount'] ?? 0,
      recommendationSourceCount: json['recommendationSourceCount'] ?? 0,
      directSourceCount: json['directSourceCount'] ?? 0,
      shareSourceCount: json['shareSourceCount'] ?? 0,
      adSourceCount: json['adSourceCount'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() => {
    'searchSourceCount': searchSourceCount,
    'recommendationSourceCount': recommendationSourceCount,
    'directSourceCount': directSourceCount,
    'shareSourceCount': shareSourceCount,
    'adSourceCount': adSourceCount,
  };

  int get total => searchSourceCount + recommendationSourceCount + 
                   directSourceCount + shareSourceCount + adSourceCount;
}
