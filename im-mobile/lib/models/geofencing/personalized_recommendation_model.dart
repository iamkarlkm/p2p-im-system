import 'dart:convert';
import 'smart_arrival_message_model.dart';

/// 个性化推荐模型类
/// 
/// @author IM Development Team
/// @since 2026-03-28
class PersonalizedRecommendation {
  final String recommendationId;
  final String userId;
  final String? merchantId;
  final String? scene;
  final String? welcomeMessage;
  final List<RecommendedProduct>? recommendedProducts;
  final List<RecommendedCoupon>? recommendedCoupons;
  final PersonalizedOffer? personalizedOffer;
  final MemberBenefitHint? memberBenefitHint;
  final List<String>? preferenceTags;

  PersonalizedRecommendation({
    required this.recommendationId,
    required this.userId,
    this.merchantId,
    this.scene,
    this.welcomeMessage,
    this.recommendedProducts,
    this.recommendedCoupons,
    this.personalizedOffer,
    this.memberBenefitHint,
    this.preferenceTags,
  });

  factory PersonalizedRecommendation.fromJson(Map<String, dynamic> json) {
    return PersonalizedRecommendation(
      recommendationId: json['recommendationId']?.toString() ?? '',
      userId: json['userId']?.toString() ?? '',
      merchantId: json['merchantId']?.toString(),
      scene: json['scene'],
      welcomeMessage: json['welcomeMessage'],
      recommendedProducts: json['recommendedProducts'] != null
          ? (json['recommendedProducts'] as List)
              .map((e) => RecommendedProduct.fromJson(e))
              .toList()
          : null,
      recommendedCoupons: json['recommendedCoupons'] != null
          ? (json['recommendedCoupons'] as List)
              .map((e) => RecommendedCoupon.fromJson(e))
              .toList()
          : null,
      personalizedOffer: json['personalizedOffer'] != null
          ? PersonalizedOffer.fromJson(json['personalizedOffer'])
          : null,
      memberBenefitHint: json['memberBenefitHint'] != null
          ? MemberBenefitHint.fromJson(json['memberBenefitHint'])
          : null,
      preferenceTags: json['preferenceTags'] != null
          ? List<String>.from(json['preferenceTags'])
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'recommendationId': recommendationId,
      'userId': userId,
      'merchantId': merchantId,
      'scene': scene,
      'welcomeMessage': welcomeMessage,
      'recommendedProducts': recommendedProducts?.map((e) => e.toJson()).toList(),
      'recommendedCoupons': recommendedCoupons?.map((e) => e.toJson()).toList(),
      'personalizedOffer': personalizedOffer?.toJson(),
      'memberBenefitHint': memberBenefitHint?.toJson(),
      'preferenceTags': preferenceTags,
    };
  }

  @override
  String toString() => jsonEncode(toJson());
}

/// 推荐商品模型
class RecommendedProduct {
  final String productId;
  final String name;
  final String? image;
  final double? originalPrice;
  final double? currentPrice;
  final String? recommendReason;
  final double? score;

  RecommendedProduct({
    required this.productId,
    required this.name,
    this.image,
    this.originalPrice,
    this.currentPrice,
    this.recommendReason,
    this.score,
  });

  factory RecommendedProduct.fromJson(Map<String, dynamic> json) {
    return RecommendedProduct(
      productId: json['productId']?.toString() ?? '',
      name: json['name'] ?? '',
      image: json['image'],
      originalPrice: json['originalPrice']?.toDouble(),
      currentPrice: json['currentPrice']?.toDouble(),
      recommendReason: json['recommendReason'],
      score: json['score']?.toDouble(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'productId': productId,
      'name': name,
      'image': image,
      'originalPrice': originalPrice,
      'currentPrice': currentPrice,
      'recommendReason': recommendReason,
      'score': score,
    };
  }
}

/// 推荐优惠券模型
class RecommendedCoupon {
  final String couponId;
  final String name;
  final String? type;
  final double? amount;
  final double? minSpend;
  final DateTime? validUntil;
  final String? usageNote;

  RecommendedCoupon({
    required this.couponId,
    required this.name,
    this.type,
    this.amount,
    this.minSpend,
    this.validUntil,
    this.usageNote,
  });

  factory RecommendedCoupon.fromJson(Map<String, dynamic> json) {
    return RecommendedCoupon(
      couponId: json['couponId']?.toString() ?? '',
      name: json['name'] ?? '',
      type: json['type'],
      amount: json['amount']?.toDouble(),
      minSpend: json['minSpend']?.toDouble(),
      validUntil: json['validUntil'] != null
          ? DateTime.parse(json['validUntil'])
          : null,
      usageNote: json['usageNote'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'couponId': couponId,
      'name': name,
      'type': type,
      'amount': amount,
      'minSpend': minSpend,
      'validUntil': validUntil?.toIso8601String(),
      'usageNote': usageNote,
    };
  }
}

/// 个性化优惠模型
class PersonalizedOffer {
  final String? title;
  final String? content;
  final double? discountAmount;
  final double? discountRate;
  final List<String>? applicableCategories;

  PersonalizedOffer({
    this.title,
    this.content,
    this.discountAmount,
    this.discountRate,
    this.applicableCategories,
  });

  factory PersonalizedOffer.fromJson(Map<String, dynamic> json) {
    return PersonalizedOffer(
      title: json['title'],
      content: json['content'],
      discountAmount: json['discountAmount']?.toDouble(),
      discountRate: json['discountRate']?.toDouble(),
      applicableCategories: json['applicableCategories'] != null
          ? List<String>.from(json['applicableCategories'])
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'title': title,
      'content': content,
      'discountAmount': discountAmount,
      'discountRate': discountRate,
      'applicableCategories': applicableCategories,
    };
  }
}

/// 会员权益提示模型
class MemberBenefitHint {
  final int? memberLevel;
  final String? levelName;
  final int? currentPoints;
  final int? pointsToEarn;
  final double? memberDiscount;
  final String? exclusiveService;

  MemberBenefitHint({
    this.memberLevel,
    this.levelName,
    this.currentPoints,
    this.pointsToEarn,
    this.memberDiscount,
    this.exclusiveService,
  });

  factory MemberBenefitHint.fromJson(Map<String, dynamic> json) {
    return MemberBenefitHint(
      memberLevel: json['memberLevel'],
      levelName: json['levelName'],
      currentPoints: json['currentPoints'],
      pointsToEarn: json['pointsToEarn'],
      memberDiscount: json['memberDiscount']?.toDouble(),
      exclusiveService: json['exclusiveService'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'memberLevel': memberLevel,
      'levelName': levelName,
      'currentPoints': currentPoints,
      'pointsToEarn': pointsToEarn,
      'memberDiscount': memberDiscount,
      'exclusiveService': exclusiveService,
    };
  }
}
