// 移动端经营洞察模型
import 'package:flutter/foundation.dart';

// 经营洞察模型
class BusinessInsightModel {
  final int id;
  final int merchantId;
  final String insightType;
  final String insightLevel;
  final String insightTitle;
  final String insightDescription;
  final String recommendedAction;
  final double confidenceScore;
  final double expectedImpact;
  final bool isRead;
  final DateTime? readAt;
  final DateTime createdAt;

  BusinessInsightModel({
    required this.id,
    required this.merchantId,
    required this.insightType,
    required this.insightLevel,
    required this.insightTitle,
    required this.insightDescription,
    required this.recommendedAction,
    required this.confidenceScore,
    required this.expectedImpact,
    required this.isRead,
    this.readAt,
    required this.createdAt,
  });

  factory BusinessInsightModel.fromJson(Map<String, dynamic> json) {
    return BusinessInsightModel(
      id: json['id'] ?? 0,
      merchantId: json['merchantId'] ?? 0,
      insightType: json['insightType'] ?? '',
      insightLevel: json['insightLevel'] ?? '',
      insightTitle: json['insightTitle'] ?? '',
      insightDescription: json['insightDescription'] ?? '',
      recommendedAction: json['recommendedAction'] ?? '',
      confidenceScore: (json['confidenceScore'] ?? 0).toDouble(),
      expectedImpact: (json['expectedImpact'] ?? 0).toDouble(),
      isRead: json['isRead'] ?? false,
      readAt: json['readAt'] != null ? DateTime.parse(json['readAt']) : null,
      createdAt: json['createdAt'] != null 
          ? DateTime.parse(json['createdAt']) 
          : DateTime.now(),
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'merchantId': merchantId,
    'insightType': insightType,
    'insightLevel': insightLevel,
    'insightTitle': insightTitle,
    'insightDescription': insightDescription,
    'recommendedAction': recommendedAction,
    'confidenceScore': confidenceScore,
    'expectedImpact': expectedImpact,
    'isRead': isRead,
    'readAt': readAt?.toIso8601String(),
    'createdAt': createdAt.toIso8601String(),
  };

  // 获取级别颜色
  String get levelColor {
    switch (insightLevel) {
      case 'CRITICAL':
        return '#FF5252';
      case 'WARNING':
        return '#FFB74D';
      case 'OPPORTUNITY':
        return '#66BB6A';
      case 'INFO':
      default:
        return '#42A5F5';
    }
  }

  // 获取类型图标
  String get typeIcon {
    switch (insightType) {
      case 'REVENUE':
        return '💰';
      case 'TRAFFIC':
        return '👥';
      case 'OPERATION':
        return '⚙️';
      case 'MARKETING':
        return '📢';
      case 'COMPETITOR':
        return '🏆';
      default:
        return '💡';
    }
  }
}

// 洞察类型枚举
enum InsightType {
  revenue('REVENUE', '营收'),
  traffic('TRAFFIC', '客流'),
  operation('OPERATION', '运营'),
  marketing('MARKETING', '营销'),
  competitor('COMPETITOR', '竞品');

  final String code;
  final String label;
  const InsightType(this.code, this.label);
}

// 洞察级别枚举
enum InsightLevel {
  critical('CRITICAL', '紧急', '#FF5252'),
  warning('WARNING', '警告', '#FFB74D'),
  opportunity('OPPORTUNITY', '机会', '#66BB6A'),
  info('INFO', '提示', '#42A5F5');

  final String code;
  final String label;
  final String color;
  const InsightLevel(this.code, this.label, this.color);
}

// 客流高峰预测
class PeakHourPrediction {
  final String peakHour1;
  final String peakHour2;
  final int predictedMaxCapacity;
  final String staffingSuggestion;

  PeakHourPrediction({
    required this.peakHour1,
    required this.peakHour2,
    required this.predictedMaxCapacity,
    required this.staffingSuggestion,
  });

  factory PeakHourPrediction.fromJson(Map<String, dynamic> json) {
    return PeakHourPrediction(
      peakHour1: json['peakHour1'] ?? '',
      peakHour2: json['peakHour2'] ?? '',
      predictedMaxCapacity: json['predictedMaxCapacity'] ?? 0,
      staffingSuggestion: json['staffingSuggestion'] ?? '',
    );
  }

  Map<String, dynamic> toJson() => {
    'peakHour1': peakHour1,
    'peakHour2': peakHour2,
    'predictedMaxCapacity': predictedMaxCapacity,
    'staffingSuggestion': staffingSuggestion,
  };
}

// 爆款商品
class HotProduct {
  final int productId;
  final String productName;
  final int salesCount;
  final double revenue;
  final double growthRate;

  HotProduct({
    required this.productId,
    required this.productName,
    required this.salesCount,
    required this.revenue,
    required this.growthRate,
  });

  factory HotProduct.fromJson(Map<String, dynamic> json) {
    return HotProduct(
      productId: json['productId'] ?? 0,
      productName: json['productName'] ?? '',
      salesCount: json['salesCount'] ?? 0,
      revenue: (json['revenue'] ?? 0).toDouble(),
      growthRate: (json['growthRate'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'productId': productId,
    'productName': productName,
    'salesCount': salesCount,
    'revenue': revenue,
    'growthRate': growthRate,
  };
}

// 营销活动效果
class MarketingEffect {
  final String activityName;
  final int participationCount;
  final double revenueGenerated;
  final double roi;
  final int newCustomers;

  MarketingEffect({
    required this.activityName,
    required this.participationCount,
    required this.revenueGenerated,
    required this.roi,
    required this.newCustomers,
  });

  factory MarketingEffect.fromJson(Map<String, dynamic> json) {
    return MarketingEffect(
      activityName: json['activityName'] ?? '',
      participationCount: json['participationCount'] ?? 0,
      revenueGenerated: (json['revenueGenerated'] ?? 0).toDouble(),
      roi: (json['roi'] ?? 0).toDouble(),
      newCustomers: json['newCustomers'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() => {
    'activityName': activityName,
    'participationCount': participationCount,
    'revenueGenerated': revenueGenerated,
    'roi': roi,
    'newCustomers': newCustomers,
  };
}
