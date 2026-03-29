// 移动端顾客画像模型
import 'package:flutter/foundation.dart';

// 顾客画像模型
class CustomerProfileModel {
  final int merchantId;
  final String statDate;
  final int totalCustomers;
  final int newCustomers;
  final int returningCustomers;
  final double retentionRate;
  final double newCustomerRatio;
  final double returningCustomerRatio;
  final GenderDistribution genderDistribution;
  final Map<String, int> ageDistribution;
  final List<GeoDistribution> geoDistribution;
  final Map<String, dynamic> frequencyDistribution;
  final Map<String, dynamic> monetaryDistribution;
  final Map<String, dynamic> rfmSegments;
  final double avgOrderValue;
  final int memberCount;
  final double memberRatio;
  final double memberContributionRate;
  final int highValueCustomerCount;
  final int churnRiskCount;

  CustomerProfileModel({
    required this.merchantId,
    required this.statDate,
    required this.totalCustomers,
    required this.newCustomers,
    required this.returningCustomers,
    required this.retentionRate,
    required this.newCustomerRatio,
    required this.returningCustomerRatio,
    required this.genderDistribution,
    required this.ageDistribution,
    required this.geoDistribution,
    required this.frequencyDistribution,
    required this.monetaryDistribution,
    required this.rfmSegments,
    required this.avgOrderValue,
    required this.memberCount,
    required this.memberRatio,
    required this.memberContributionRate,
    required this.highValueCustomerCount,
    required this.churnRiskCount,
  });

  factory CustomerProfileModel.fromJson(Map<String, dynamic> json) {
    return CustomerProfileModel(
      merchantId: json['merchantId'] ?? 0,
      statDate: json['statDate'] ?? '',
      totalCustomers: json['totalCustomers'] ?? 0,
      newCustomers: json['newCustomers'] ?? 0,
      returningCustomers: json['returningCustomers'] ?? 0,
      retentionRate: (json['retentionRate'] ?? 0).toDouble(),
      newCustomerRatio: (json['newCustomerRatio'] ?? 0).toDouble(),
      returningCustomerRatio: (json['returningCustomerRatio'] ?? 0).toDouble(),
      genderDistribution: GenderDistribution.fromJson(json['genderDistribution'] ?? {}),
      ageDistribution: Map<String, int>.from(json['ageDistribution'] ?? {}),
      geoDistribution: (json['geoDistribution'] as List? ?? [])
          .map((e) => GeoDistribution.fromJson(e))
          .toList(),
      frequencyDistribution: json['frequencyDistribution'] ?? {},
      monetaryDistribution: json['monetaryDistribution'] ?? {},
      rfmSegments: json['rfmSegments'] ?? {},
      avgOrderValue: (json['avgOrderValue'] ?? 0).toDouble(),
      memberCount: json['memberCount'] ?? 0,
      memberRatio: (json['memberRatio'] ?? 0).toDouble(),
      memberContributionRate: (json['memberContributionRate'] ?? 0).toDouble(),
      highValueCustomerCount: json['highValueCustomerCount'] ?? 0,
      churnRiskCount: json['churnRiskCount'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() => {
    'merchantId': merchantId,
    'statDate': statDate,
    'totalCustomers': totalCustomers,
    'newCustomers': newCustomers,
    'returningCustomers': returningCustomers,
    'retentionRate': retentionRate,
    'newCustomerRatio': newCustomerRatio,
    'returningCustomerRatio': returningCustomerRatio,
    'genderDistribution': genderDistribution.toJson(),
    'ageDistribution': ageDistribution,
    'geoDistribution': geoDistribution.map((e) => e.toJson()).toList(),
    'frequencyDistribution': frequencyDistribution,
    'monetaryDistribution': monetaryDistribution,
    'rfmSegments': rfmSegments,
    'avgOrderValue': avgOrderValue,
    'memberCount': memberCount,
    'memberRatio': memberRatio,
    'memberContributionRate': memberContributionRate,
    'highValueCustomerCount': highValueCustomerCount,
    'churnRiskCount': churnRiskCount,
  };
}

// 性别分布
class GenderDistribution {
  final int maleCount;
  final int femaleCount;
  final int unknownCount;
  final double maleRatio;
  final double femaleRatio;
  final double unknownRatio;

  GenderDistribution({
    required this.maleCount,
    required this.femaleCount,
    required this.unknownCount,
    required this.maleRatio,
    required this.femaleRatio,
    required this.unknownRatio,
  });

  factory GenderDistribution.fromJson(Map<String, dynamic> json) {
    return GenderDistribution(
      maleCount: json['maleCount'] ?? 0,
      femaleCount: json['femaleCount'] ?? 0,
      unknownCount: json['unknownCount'] ?? 0,
      maleRatio: (json['maleRatio'] ?? 0).toDouble(),
      femaleRatio: (json['femaleRatio'] ?? 0).toDouble(),
      unknownRatio: (json['unknownRatio'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'maleCount': maleCount,
    'femaleCount': femaleCount,
    'unknownCount': unknownCount,
    'maleRatio': maleRatio,
    'femaleRatio': femaleRatio,
    'unknownRatio': unknownRatio,
  };
}

// 地域分布
class GeoDistribution {
  final String district;
  final int count;
  final double ratio;

  GeoDistribution({
    required this.district,
    required this.count,
    required this.ratio,
  });

  factory GeoDistribution.fromJson(Map<String, dynamic> json) {
    return GeoDistribution(
      district: json['district'] ?? '',
      count: json['count'] ?? 0,
      ratio: (json['ratio'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'district': district,
    'count': count,
    'ratio': ratio,
  };
}

// RFM客群
class RFMSegment {
  final String code;
  final String label;
  final int count;
  final String description;

  RFMSegment({
    required this.code,
    required this.label,
    required this.count,
    required this.description,
  });

  factory RFMSegment.fromJson(Map<String, dynamic> json) {
    return RFMSegment(
      code: json['code'] ?? '',
      label: json['label'] ?? '',
      count: json['count'] ?? 0,
      description: json['description'] ?? '',
    );
  }

  Map<String, dynamic> toJson() => {
    'code': code,
    'label': label,
    'count': count,
    'description': description,
  };
}

// 高价值客户
class HighValueCustomer {
  final int customerId;
  final String customerName;
  final double totalSpent;
  final int visitCount;
  final double avgOrderValue;
  final String memberLevel;

  HighValueCustomer({
    required this.customerId,
    required this.customerName,
    required this.totalSpent,
    required this.visitCount,
    required this.avgOrderValue,
    required this.memberLevel,
  });

  factory HighValueCustomer.fromJson(Map<String, dynamic> json) {
    return HighValueCustomer(
      customerId: json['customerId'] ?? 0,
      customerName: json['customerName'] ?? '',
      totalSpent: (json['totalSpent'] ?? 0).toDouble(),
      visitCount: json['visitCount'] ?? 0,
      avgOrderValue: (json['avgOrderValue'] ?? 0).toDouble(),
      memberLevel: json['memberLevel'] ?? '',
    );
  }

  Map<String, dynamic> toJson() => {
    'customerId': customerId,
    'customerName': customerName,
    'totalSpent': totalSpent,
    'visitCount': visitCount,
    'avgOrderValue': avgOrderValue,
    'memberLevel': memberLevel,
  };
}

// 流失风险客户
class ChurnRiskCustomer {
  final int customerId;
  final String customerName;
  final String lastVisitDate;
  final int totalVisits;
  final double riskScore;
  final String suggestedAction;

  ChurnRiskCustomer({
    required this.customerId,
    required this.customerName,
    required this.lastVisitDate,
    required this.totalVisits,
    required this.riskScore,
    required this.suggestedAction,
  });

  factory ChurnRiskCustomer.fromJson(Map<String, dynamic> json) {
    return ChurnRiskCustomer(
      customerId: json['customerId'] ?? 0,
      customerName: json['customerName'] ?? '',
      lastVisitDate: json['lastVisitDate'] ?? '',
      totalVisits: json['totalVisits'] ?? 0,
      riskScore: (json['riskScore'] ?? 0).toDouble(),
      suggestedAction: json['suggestedAction'] ?? '',
    );
  }

  Map<String, dynamic> toJson() => {
    'customerId': customerId,
    'customerName': customerName,
    'lastVisitDate': lastVisitDate,
    'totalVisits': totalVisits,
    'riskScore': riskScore,
    'suggestedAction': suggestedAction,
  };
}
