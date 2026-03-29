/// 商户评价模型
class MerchantReview {
  final int id;
  final int merchantId;
  final int? poiId;
  final int userId;
  final int? orderId;
  final double overallRating;
  final double? tasteRating;
  final double? environmentRating;
  final double? serviceRating;
  final double? valueRating;
  final String content;
  final List<String>? images;
  final String? videoUrl;
  final String? videoCover;
  final int? videoDuration;
  final double? consumptionAmount;
  final double? perCapitaCost;
  final bool anonymous;
  final int reviewType; // 1-图文 2-视频
  final int source;
  final int likeCount;
  final int replyCount;
  final int viewCount;
  final bool recommended;
  final DateTime? diningTime;
  final int? diningPeople;
  final List<String>? tags;
  final bool experienced;
  final int? experienceType;
  final DateTime createTime;
  
  // 用户信息
  final String? userNickname;
  final String? userAvatar;
  final int? userLevel;
  
  // 商户信息
  final String? merchantName;
  final String? merchantLogo;
  
  // 交互状态
  final bool? hasLiked;
  final List<MerchantReviewReply>? replies;
  final String? ratingText;
  final bool? highQuality;

  MerchantReview({
    required this.id,
    required this.merchantId,
    this.poiId,
    required this.userId,
    this.orderId,
    required this.overallRating,
    this.tasteRating,
    this.environmentRating,
    this.serviceRating,
    this.valueRating,
    required this.content,
    this.images,
    this.videoUrl,
    this.videoCover,
    this.videoDuration,
    this.consumptionAmount,
    this.perCapitaCost,
    this.anonymous = false,
    this.reviewType = 1,
    this.source = 1,
    this.likeCount = 0,
    this.replyCount = 0,
    this.viewCount = 0,
    this.recommended = false,
    this.diningTime,
    this.diningPeople,
    this.tags,
    this.experienced = false,
    this.experienceType,
    required this.createTime,
    this.userNickname,
    this.userAvatar,
    this.userLevel,
    this.merchantName,
    this.merchantLogo,
    this.hasLiked,
    this.replies,
    this.ratingText,
    this.highQuality,
  });

  factory MerchantReview.fromJson(Map<String, dynamic> json) {
    return MerchantReview(
      id: json['id'] as int,
      merchantId: json['merchantId'] as int,
      poiId: json['poiId'] as int?,
      userId: json['userId'] as int,
      orderId: json['orderId'] as int?,
      overallRating: (json['overallRating'] as num).toDouble(),
      tasteRating: json['tasteRating'] != null ? (json['tasteRating'] as num).toDouble() : null,
      environmentRating: json['environmentRating'] != null ? (json['environmentRating'] as num).toDouble() : null,
      serviceRating: json['serviceRating'] != null ? (json['serviceRating'] as num).toDouble() : null,
      valueRating: json['valueRating'] != null ? (json['valueRating'] as num).toDouble() : null,
      content: json['content'] as String,
      images: json['images'] != null ? List<String>.from(json['images']) : null,
      videoUrl: json['videoUrl'] as String?,
      videoCover: json['videoCover'] as String?,
      videoDuration: json['videoDuration'] as int?,
      consumptionAmount: json['consumptionAmount'] != null ? (json['consumptionAmount'] as num).toDouble() : null,
      perCapitaCost: json['perCapitaCost'] != null ? (json['perCapitaCost'] as num).toDouble() : null,
      anonymous: json['anonymous'] as bool? ?? false,
      reviewType: json['reviewType'] as int? ?? 1,
      source: json['source'] as int? ?? 1,
      likeCount: json['likeCount'] as int? ?? 0,
      replyCount: json['replyCount'] as int? ?? 0,
      viewCount: json['viewCount'] as int? ?? 0,
      recommended: json['recommended'] as bool? ?? false,
      diningTime: json['diningTime'] != null ? DateTime.parse(json['diningTime']) : null,
      diningPeople: json['diningPeople'] as int?,
      tags: json['tags'] != null ? List<String>.from(json['tags']) : null,
      experienced: json['experienced'] as bool? ?? false,
      experienceType: json['experienceType'] as int?,
      createTime: DateTime.parse(json['createTime']),
      userNickname: json['userNickname'] as String?,
      userAvatar: json['userAvatar'] as String?,
      userLevel: json['userLevel'] as int?,
      merchantName: json['merchantName'] as String?,
      merchantLogo: json['merchantLogo'] as String?,
      hasLiked: json['hasLiked'] as bool?,
      replies: json['replies'] != null 
          ? (json['replies'] as List).map((e) => MerchantReviewReply.fromJson(e)).toList()
          : null,
      ratingText: json['ratingText'] as String?,
      highQuality: json['highQuality'] as bool?,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'merchantId': merchantId,
      'poiId': poiId,
      'userId': userId,
      'orderId': orderId,
      'overallRating': overallRating,
      'tasteRating': tasteRating,
      'environmentRating': environmentRating,
      'serviceRating': serviceRating,
      'valueRating': valueRating,
      'content': content,
      'images': images,
      'videoUrl': videoUrl,
      'videoCover': videoCover,
      'videoDuration': videoDuration,
      'consumptionAmount': consumptionAmount,
      'perCapitaCost': perCapitaCost,
      'anonymous': anonymous,
      'reviewType': reviewType,
      'source': source,
      'likeCount': likeCount,
      'replyCount': replyCount,
      'viewCount': viewCount,
      'recommended': recommended,
      'diningTime': diningTime?.toIso8601String(),
      'diningPeople': diningPeople,
      'tags': tags,
      'experienced': experienced,
      'experienceType': experienceType,
      'createTime': createTime.toIso8601String(),
    };
  }

  /// 计算平均分
  double calculateAverageRating() {
    final ratings = [tasteRating, environmentRating, serviceRating, valueRating]
        .where((r) => r != null)
        .toList();
    if (ratings.isEmpty) return overallRating;
    return ratings.reduce((a, b) => a! + b!)! / ratings.length;
  }

  /// 获取星级颜色
  String get ratingColor {
    if (overallRating >= 4.5) return '#FF6B6B'; // 极佳-红色
    if (overallRating >= 4.0) return '#FFA500'; // 很好-橙色
    if (overallRating >= 3.5) return '#FFD700'; // 不错-金色
    if (overallRating >= 3.0) return '#90EE90'; // 一般-浅绿
    return '#808080'; // 较差-灰色
  }

  /// 是否为视频评价
  bool get isVideoReview => videoUrl != null && videoUrl!.isNotEmpty;

  /// 是否有图片
  bool get hasImages => images != null && images!.isNotEmpty;

  /// 获取显示标题
  String get displayTitle {
    if (merchantName != null && merchantName!.isNotEmpty) {
      return merchantName!;
    }
    return '商户评价';
  }

  /// 获取格式化的人均消费
  String? get formattedPerCapita {
    if (perCapitaCost == null) return null;
    return '人均¥${perCapitaCost!.toStringAsFixed(0)}';
  }
}

/// 评价回复模型
class MerchantReviewReply {
  final int id;
  final int reviewId;
  final int? parentId;
  final int replyType; // 1-用户 2-商家 3-平台
  final int replyBy;
  final String replyName;
  final String? replyAvatar;
  final int? replyTo;
  final String? replyToName;
  final String content;
  final List<String>? images;
  final int likeCount;
  final bool official;
  final bool merchantPinned;
  final DateTime createTime;
  final List<MerchantReviewReply>? children;

  MerchantReviewReply({
    required this.id,
    required this.reviewId,
    this.parentId,
    required this.replyType,
    required this.replyBy,
    required this.replyName,
    this.replyAvatar,
    this.replyTo,
    this.replyToName,
    required this.content,
    this.images,
    this.likeCount = 0,
    this.official = false,
    this.merchantPinned = false,
    required this.createTime,
    this.children,
  });

  factory MerchantReviewReply.fromJson(Map<String, dynamic> json) {
    return MerchantReviewReply(
      id: json['id'] as int,
      reviewId: json['reviewId'] as int,
      parentId: json['parentId'] as int?,
      replyType: json['replyType'] as int,
      replyBy: json['replyBy'] as int,
      replyName: json['replyName'] as String,
      replyAvatar: json['replyAvatar'] as String?,
      replyTo: json['replyTo'] as int?,
      replyToName: json['replyToName'] as String?,
      content: json['content'] as String,
      images: json['images'] != null ? List<String>.from(json['images']) : null,
      likeCount: json['likeCount'] as int? ?? 0,
      official: json['official'] as bool? ?? false,
      merchantPinned: json['merchantPinned'] as bool? ?? false,
      createTime: DateTime.parse(json['createTime']),
      children: json['children'] != null
          ? (json['children'] as List).map((e) => MerchantReviewReply.fromJson(e)).toList()
          : null,
    );
  }

  /// 获取回复者类型标签
  String get typeLabel {
    switch (replyType) {
      case 1:
        return '用户';
      case 2:
        return '商家';
      case 3:
        return '官方';
      default:
        return '';
    }
  }

  /// 获取回复者类型颜色
  String get typeColor {
    switch (replyType) {
      case 1:
        return '#666666';
      case 2:
        return '#FF6B6B';
      case 3:
        return '#4A90E2';
      default:
        return '#666666';
    }
  }
}

/// 评价统计模型
class MerchantReviewStatistic {
  final int merchantId;
  final double overallRating;
  final double? tasteRating;
  final double? environmentRating;
  final double? serviceRating;
  final double? valueRating;
  final int totalCount;
  final int fiveStarCount;
  final int fourStarCount;
  final int threeStarCount;
  final int twoStarCount;
  final int oneStarCount;
  final int withImageCount;
  final int withVideoCount;
  final int positiveCount;
  final int neutralCount;
  final int negativeCount;
  final double positiveRate;
  final int dailyNewCount;
  final int weeklyNewCount;
  final int monthlyNewCount;
  final StarDistribution starDistribution;
  final TagStatistic tagStatistic;

  MerchantReviewStatistic({
    required this.merchantId,
    required this.overallRating,
    this.tasteRating,
    this.environmentRating,
    this.serviceRating,
    this.valueRating,
    required this.totalCount,
    required this.fiveStarCount,
    required this.fourStarCount,
    required this.threeStarCount,
    required this.twoStarCount,
    required this.oneStarCount,
    required this.withImageCount,
    required this.withVideoCount,
    required this.positiveCount,
    required this.neutralCount,
    required this.negativeCount,
    required this.positiveRate,
    required this.dailyNewCount,
    required this.weeklyNewCount,
    required this.monthlyNewCount,
    required this.starDistribution,
    required this.tagStatistic,
  });

  factory MerchantReviewStatistic.fromJson(Map<String, dynamic> json) {
    return MerchantReviewStatistic(
      merchantId: json['merchantId'] as int,
      overallRating: (json['overallRating'] as num).toDouble(),
      tasteRating: json['tasteRating'] != null ? (json['tasteRating'] as num).toDouble() : null,
      environmentRating: json['environmentRating'] != null ? (json['environmentRating'] as num).toDouble() : null,
      serviceRating: json['serviceRating'] != null ? (json['serviceRating'] as num).toDouble() : null,
      valueRating: json['valueRating'] != null ? (json['valueRating'] as num).toDouble() : null,
      totalCount: json['totalCount'] as int? ?? 0,
      fiveStarCount: json['fiveStarCount'] as int? ?? 0,
      fourStarCount: json['fourStarCount'] as int? ?? 0,
      threeStarCount: json['threeStarCount'] as int? ?? 0,
      twoStarCount: json['twoStarCount'] as int? ?? 0,
      oneStarCount: json['oneStarCount'] as int? ?? 0,
      withImageCount: json['withImageCount'] as int? ?? 0,
      withVideoCount: json['withVideoCount'] as int? ?? 0,
      positiveCount: json['positiveCount'] as int? ?? 0,
      neutralCount: json['neutralCount'] as int? ?? 0,
      negativeCount: json['negativeCount'] as int? ?? 0,
      positiveRate: (json['positiveRate'] as num).toDouble(),
      dailyNewCount: json['dailyNewCount'] as int? ?? 0,
      weeklyNewCount: json['weeklyNewCount'] as int? ?? 0,
      monthlyNewCount: json['monthlyNewCount'] as int? ?? 0,
      starDistribution: StarDistribution.fromJson(json['starDistribution'] ?? {}),
      tagStatistic: TagStatistic.fromJson(json['tagStatistic'] ?? {}),
    );
  }

  /// 获取星级文本
  String get ratingText {
    if (overallRating >= 4.5) return '极佳';
    if (overallRating >= 4.0) return '非常好';
    if (overallRating >= 3.5) return '不错';
    if (overallRating >= 3.0) return '一般';
    return '较差';
  }
}

/// 星级分布
class StarDistribution {
  final double fiveStarPercent;
  final double fourStarPercent;
  final double threeStarPercent;
  final double twoStarPercent;
  final double oneStarPercent;

  StarDistribution({
    required this.fiveStarPercent,
    required this.fourStarPercent,
    required this.threeStarPercent,
    required this.twoStarPercent,
    required this.oneStarPercent,
  });

  factory StarDistribution.fromJson(Map<String, dynamic> json) {
    return StarDistribution(
      fiveStarPercent: (json['fiveStarPercent'] as num? ?? 0).toDouble(),
      fourStarPercent: (json['fourStarPercent'] as num? ?? 0).toDouble(),
      threeStarPercent: (json['threeStarPercent'] as num? ?? 0).toDouble(),
      twoStarPercent: (json['twoStarPercent'] as num? ?? 0).toDouble(),
      oneStarPercent: (json['oneStarPercent'] as num? ?? 0).toDouble(),
    );
  }
}

/// 标签统计
class TagStatistic {
  final int tasteGoodCount;
  final int envGoodCount;
  final int serviceGoodCount;
  final int valueGoodCount;
  final int returningCount;

  TagStatistic({
    required this.tasteGoodCount,
    required this.envGoodCount,
    required this.serviceGoodCount,
    required this.valueGoodCount,
    required this.returningCount,
  });

  factory TagStatistic.fromJson(Map<String, dynamic> json) {
    return TagStatistic(
      tasteGoodCount: json['tasteGoodCount'] as int? ?? 0,
      envGoodCount: json['envGoodCount'] as int? ?? 0,
      serviceGoodCount: json['serviceGoodCount'] as int? ?? 0,
      valueGoodCount: json['valueGoodCount'] as int? ?? 0,
      returningCount: json['returningCount'] as int? ?? 0,
    );
  }
}

/// 排序类型
enum ReviewSortType {
  latest('latest', '最新'),
  highest('highest', '评分最高'),
  lowest('lowest', '评分最低'),
  liked('liked', '最多点赞'),
  recommended('recommended', '优质推荐'),
  image('image', '有图评价'),
  video('video', '视频评价');

  final String code;
  final String label;

  const ReviewSortType(this.code, this.label);

  static ReviewSortType fromCode(String? code) {
    return values.firstWhere(
      (e) => e.code == code,
      orElse: () => latest,
    );
  }
}
