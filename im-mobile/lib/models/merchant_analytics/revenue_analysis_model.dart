// 移动端营收分析模型
import 'package:flutter/foundation.dart';

// 营收分析模型
class RevenueAnalysisModel {
  final int merchantId;
  final String statDate;
  final double totalRevenue;
  final int totalOrders;
  final double avgOrderValue;
  final double grossProfit;
  final double grossProfitRate;
  final double refundAmount;
  final int refundCount;
  final double refundRate;
  final double discountAmount;
  final int discountOrderCount;
  final double yesterdayRevenue;
  final double revenueGrowthRate;
  final List<RevenueTrendItem> sevenDayTrend;
  final List<RevenueTrendItem> thirtyDayTrend;
  final PaymentChannelDistribution paymentChannels;
  final RevenueComposition composition;

  RevenueAnalysisModel({
    required this.merchantId,
    required this.statDate,
    required this.totalRevenue,
    required this.totalOrders,
    required this.avgOrderValue,
    required this.grossProfit,
    required this.grossProfitRate,
    required this.refundAmount,
    required this.refundCount,
    required this.refundRate,
    required this.discountAmount,
    required this.discountOrderCount,
    required this.yesterdayRevenue,
    required this.revenueGrowthRate,
    required this.sevenDayTrend,
    required this.thirtyDayTrend,
    required this.paymentChannels,
    required this.composition,
  });

  factory RevenueAnalysisModel.fromJson(Map<String, dynamic> json) {
    return RevenueAnalysisModel(
      merchantId: json['merchantId'] ?? 0,
      statDate: json['statDate'] ?? '',
      totalRevenue: (json['totalRevenue'] ?? 0).toDouble(),
      totalOrders: json['totalOrders'] ?? 0,
      avgOrderValue: (json['avgOrderValue'] ?? 0).toDouble(),
      grossProfit: (json['grossProfit'] ?? 0).toDouble(),
      grossProfitRate: (json['grossProfitRate'] ?? 0).toDouble(),
      refundAmount: (json['refundAmount'] ?? 0).toDouble(),
      refundCount: json['refundCount'] ?? 0,
      refundRate: (json['refundRate'] ?? 0).toDouble(),
      discountAmount: (json['discountAmount'] ?? 0).toDouble(),
      discountOrderCount: json['discountOrderCount'] ?? 0,
      yesterdayRevenue: (json['yesterdayRevenue'] ?? 0).toDouble(),
      revenueGrowthRate: (json['revenueGrowthRate'] ?? 0).toDouble(),
      sevenDayTrend: (json['sevenDayTrend'] as List? ?? [])
          .map((e) => RevenueTrendItem.fromJson(e))
          .toList(),
      thirtyDayTrend: (json['thirtyDayTrend'] as List? ?? [])
          .map((e) => RevenueTrendItem.fromJson(e))
          .toList(),
      paymentChannels: PaymentChannelDistribution.fromJson(json['paymentChannels'] ?? {}),
      composition: RevenueComposition.fromJson(json['composition'] ?? {}),
    );
  }

  Map<String, dynamic> toJson() => {
    'merchantId': merchantId,
    'statDate': statDate,
    'totalRevenue': totalRevenue,
    'totalOrders': totalOrders,
    'avgOrderValue': avgOrderValue,
    'grossProfit': grossProfit,
    'grossProfitRate': grossProfitRate,
    'refundAmount': refundAmount,
    'refundCount': refundCount,
    'refundRate': refundRate,
    'discountAmount': discountAmount,
    'discountOrderCount': discountOrderCount,
    'yesterdayRevenue': yesterdayRevenue,
    'revenueGrowthRate': revenueGrowthRate,
    'sevenDayTrend': sevenDayTrend.map((e) => e.toJson()).toList(),
    'thirtyDayTrend': thirtyDayTrend.map((e) => e.toJson()).toList(),
    'paymentChannels': paymentChannels.toJson(),
    'composition': composition.toJson(),
  };
}

// 营收趋势项
class RevenueTrendItem {
  final String date;
  final double revenue;
  final int orders;
  final double avgOrderValue;

  RevenueTrendItem({
    required this.date,
    required this.revenue,
    required this.orders,
    required this.avgOrderValue,
  });

  factory RevenueTrendItem.fromJson(Map<String, dynamic> json) {
    return RevenueTrendItem(
      date: json['date'] ?? '',
      revenue: (json['revenue'] ?? 0).toDouble(),
      orders: json['orders'] ?? 0,
      avgOrderValue: (json['avgOrderValue'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'date': date,
    'revenue': revenue,
    'orders': orders,
    'avgOrderValue': avgOrderValue,
  };
}

// 支付渠道分布
class PaymentChannelDistribution {
  final PaymentChannelItem wechatPay;
  final PaymentChannelItem alipay;
  final PaymentChannelItem unionPay;
  final PaymentChannelItem memberCard;
  final PaymentChannelItem other;

  PaymentChannelDistribution({
    required this.wechatPay,
    required this.alipay,
    required this.unionPay,
    required this.memberCard,
    required this.other,
  });

  factory PaymentChannelDistribution.fromJson(Map<String, dynamic> json) {
    return PaymentChannelDistribution(
      wechatPay: PaymentChannelItem.fromJson(json['wechatPay'] ?? {}),
      alipay: PaymentChannelItem.fromJson(json['alipay'] ?? {}),
      unionPay: PaymentChannelItem.fromJson(json['unionPay'] ?? {}),
      memberCard: PaymentChannelItem.fromJson(json['memberCard'] ?? {}),
      other: PaymentChannelItem.fromJson(json['other'] ?? {}),
    );
  }

  Map<String, dynamic> toJson() => {
    'wechatPay': wechatPay.toJson(),
    'alipay': alipay.toJson(),
    'unionPay': unionPay.toJson(),
    'memberCard': memberCard.toJson(),
    'other': other.toJson(),
  };
}

// 支付渠道项
class PaymentChannelItem {
  final double amount;
  final int count;
  final double ratio;
  final String name;

  PaymentChannelItem({
    required this.amount,
    required this.count,
    required this.ratio,
    required this.name,
  });

  factory PaymentChannelItem.fromJson(Map<String, dynamic> json) {
    return PaymentChannelItem(
      amount: (json['amount'] ?? 0).toDouble(),
      count: json['count'] ?? 0,
      ratio: (json['ratio'] ?? 0).toDouble(),
      name: json['name'] ?? '',
    );
  }

  Map<String, dynamic> toJson() => {
    'amount': amount,
    'count': count,
    'ratio': ratio,
    'name': name,
  };
}

// 营收构成
class RevenueComposition {
  final double productRevenue;
  final double serviceRevenue;
  final double deliveryRevenue;
  final double bookingRevenue;
  final double otherRevenue;

  RevenueComposition({
    required this.productRevenue,
    required this.serviceRevenue,
    required this.deliveryRevenue,
    required this.bookingRevenue,
    required this.otherRevenue,
  });

  factory RevenueComposition.fromJson(Map<String, dynamic> json) {
    return RevenueComposition(
      productRevenue: (json['productRevenue'] ?? 0).toDouble(),
      serviceRevenue: (json['serviceRevenue'] ?? 0).toDouble(),
      deliveryRevenue: (json['deliveryRevenue'] ?? 0).toDouble(),
      bookingRevenue: (json['bookingRevenue'] ?? 0).toDouble(),
      otherRevenue: (json['otherRevenue'] ?? 0).toDouble(),
    );
  }

  Map<String, dynamic> toJson() => {
    'productRevenue': productRevenue,
    'serviceRevenue': serviceRevenue,
    'deliveryRevenue': deliveryRevenue,
    'bookingRevenue': bookingRevenue,
    'otherRevenue': otherRevenue,
  };

  double get total => productRevenue + serviceRevenue + deliveryRevenue + bookingRevenue + otherRevenue;
}
