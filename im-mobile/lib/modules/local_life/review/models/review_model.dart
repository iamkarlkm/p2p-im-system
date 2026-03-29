/// 评价数据模型
class ReviewDetail {
  final String reviewId;
  final String merchantId;
  final String userId;
  final String userName;
  final String userAvatar;
  final int userLevel;
  final int overallRating;
  final int tasteRating;
  final int environmentRating;
  final int serviceRating;
  final int valueRating;
  final String content;
  final List<String> images;
  final VideoInfo? video;
  final double? perCapita;
  final bool isAnonymous;
  final bool isRecommended;
  final int likeCount;
  final int replyCount;
  final bool hasLiked;
  final String sentiment;
  final bool isHighQuality;
  final String reviewTime;
  final String diningDate;
  final List<String> tags;
  final MerchantReply? merchantReply;
  final String source;

  ReviewDetail({
    required this.reviewId,
    required this.merchantId,
    required this.userId,
    required this.userName,
    required this.userAvatar,
    required this.userLevel,
    required this.overallRating,
    required this.tasteRating,
    required this.environmentRating,
    required this.serviceRating,
    required this.valueRating,
    required this.content,
    required this.images,
    this.video,
    this.perCapita,
    required this.isAnonymous,
    required this.isRecommended,
    required this.likeCount,
    required this.replyCount,
    required this.hasLiked,
    required this.sentiment,
    required this.isHighQuality,
    required this.reviewTime,
    required this.diningDate,
    required this.tags,
    this.merchantReply,
    required this.source,
  });

  factory ReviewDetail.fromJson(Map<String, dynamic> json) {
    return ReviewDetail(
      reviewId: json['reviewId'].toString(),
      merchantId: json['merchantId'].toString(),
      userId: json['user']['userId'].toString(),
      userName: json['user']['nickname'],
      userAvatar: json['user']['avatar'] ?? '',
      userLevel: json['user']['level'] ?? 0,
      overallRating: json['overallRating'],
      tasteRating: json['tasteRating'] ?? 5,
      environmentRating: json['environmentRating'] ?? 5,
      serviceRating: json['serviceRating'] ?? 5,
      valueRating: json['valueRating'] ?? 5,
      content: json['content'] ?? '',
      images: (json['images'] as List<dynamic>?)?.cast<String>() ?? [],
      video: json['video'] != null ? VideoInfo.fromJson(json['video']) : null,
      perCapita: json['perCapita']?.toDouble(),
      isAnonymous: json['anonymous'] ?? false,
      isRecommended: json['recommended'] ?? true,
      likeCount: json['likeCount'] ?? 0,
      replyCount: json['replyCount'] ?? 0,
      hasLiked: json['hasLiked'] ?? false,
      sentiment: json['sentiment'] ?? 'NEUTRAL',
      isHighQuality: json['highQuality'] ?? false,
      reviewTime: json['reviewTime'],
      diningDate: json['diningDate'] ?? '',
      tags: (json['tags'] as List<dynamic>?)?.cast<String>() ?? [],
      merchantReply: json['merchantReply'] != null 
          ? MerchantReply.fromJson(json['merchantReply']) 
          : null,
      source: json['source'] ?? 'APP',
    );
  }

  ReviewDetail copyWith({
    bool? hasLiked,
    int? likeCount,
  }) {
    return ReviewDetail(
      reviewId: reviewId,
      merchantId: merchantId,
      userId: userId,
      userName: userName,
      userAvatar: userAvatar,
      userLevel: userLevel,
      overallRating: overallRating,
      tasteRating: tasteRating,
      environmentRating: environmentRating,
      serviceRating: serviceRating,
      valueRating: valueRating,
      content: content,
      images: images,
      video: video,
      perCapita: perCapita,
      isAnonymous: isAnonymous,
      isRecommended: isRecommended,
      likeCount: likeCount ?? this.likeCount,
      replyCount: replyCount,
      hasLiked: hasLiked ?? this.hasLiked,
      sentiment: sentiment,
      isHighQuality: isHighQuality,
      reviewTime: reviewTime,
      diningDate: diningDate,
      tags: tags,
      merchantReply: merchantReply,
      source: source,
    );
  }
}

class VideoInfo {
  final String videoUrl;
  final String coverUrl;
  final int duration;

  VideoInfo({
    required this.videoUrl,
    required this.coverUrl,
    required this.duration,
  });

  factory VideoInfo.fromJson(Map<String, dynamic> json) {
    return VideoInfo(
      videoUrl: json['videoUrl'],
      coverUrl: json['coverUrl'],
      duration: json['duration'] ?? 0,
    );
  }
}

class MerchantReply {
  final String content;
  final String replyTime;

  MerchantReply({
    required this.content,
    required this.replyTime,
  });

  factory MerchantReply.fromJson(Map<String, dynamic> json) {
    return MerchantReply(
      content: json['content'],
      replyTime: json['replyTime'],
    );
  }
}

/// 口碑统计模型
class ReputationStatistics {
  final String merchantId;
  final int totalReviews;
  final int validReviews;
  final int imageReviews;
  final int videoReviews;
  final double overallRating;
  final double tasteRating;
  final double environmentRating;
  final double serviceRating;
  final double valueRating;
  final RatingDistribution ratingDistribution;
  final double positiveRate;
  final double reputationScore;
  final String reputationLevel;
  final bool onReputationList;
  final String? listType;
  final int? listRank;
  final String trend;
  final List<TagStat> hotTags;

  ReputationStatistics({
    required this.merchantId,
    required this.totalReviews,
    required this.validReviews,
    required this.imageReviews,
    required this.videoReviews,
    required this.overallRating,
    required this.tasteRating,
    required this.environmentRating,
    required this.serviceRating,
    required this.valueRating,
    required this.ratingDistribution,
    required this.positiveRate,
    required this.reputationScore,
    required this.reputationLevel,
    required this.onReputationList,
    this.listType,
    this.listRank,
    required this.trend,
    required this.hotTags,
  });

  factory ReputationStatistics.fromJson(Map<String, dynamic> json) {
    return ReputationStatistics(
      merchantId: json['merchantId'].toString(),
      totalReviews: json['totalReviews'] ?? 0,
      validReviews: json['validReviews'] ?? 0,
      imageReviews: json['imageReviews'] ?? 0,
      videoReviews: json['videoReviews'] ?? 0,
      overallRating: json['overallRating']?.toDouble() ?? 0,
      tasteRating: json['tasteRating']?.toDouble() ?? 0,
      environmentRating: json['environmentRating']?.toDouble() ?? 0,
      serviceRating: json['serviceRating']?.toDouble() ?? 0,
      valueRating: json['valueRating']?.toDouble() ?? 0,
      ratingDistribution: RatingDistribution.fromJson(json['ratingDistribution'] ?? {}),
      positiveRate: json['positiveRate']?.toDouble() ?? 0,
      reputationScore: json['reputationScore']?.toDouble() ?? 0,
      reputationLevel: json['reputationLevel'] ?? 'D',
      onReputationList: json['onReputationList'] ?? false,
      listType: json['listType'],
      listRank: json['listRank'],
      trend: json['trend'] ?? 'STABLE',
      hotTags: (json['hotTags'] as List<dynamic>?)
              ?.map((e) => TagStat.fromJson(e))
              .toList() ??
          [],
    );
  }
}

class RatingDistribution {
  final int fiveStar;
  final int fourStar;
  final int threeStar;
  final int twoStar;
  final int oneStar;

  RatingDistribution({
    required this.fiveStar,
    required this.fourStar,
    required this.threeStar,
    required this.twoStar,
    required this.oneStar,
  });

  factory RatingDistribution.fromJson(Map<String, dynamic> json) {
    return RatingDistribution(
      fiveStar: json['fiveStar'] ?? 0,
      fourStar: json['fourStar'] ?? 0,
      threeStar: json['threeStar'] ?? 0,
      twoStar: json['twoStar'] ?? 0,
      oneStar: json['oneStar'] ?? 0,
    );
  }

  int get total => fiveStar + fourStar + threeStar + twoStar + oneStar;
}

class TagStat {
  final String tag;
  final int count;

  TagStat({required this.tag, required this.count});

  factory TagStat.fromJson(Map<String, dynamic> json) {
    return TagStat(
      tag: json['tag'],
      count: json['count'] ?? 0,
    );
  }
}

/// 分页结果
class PageResult<T> {
  final List<T> list;
  final int total;
  final int pageNum;
  final int pageSize;
  final int totalPages;

  PageResult({
    required this.list,
    required this.total,
    required this.pageNum,
    required this.pageSize,
    required this.totalPages,
  });

  factory PageResult.fromJson(
    Map<String, dynamic> json,
    T Function(Map<String, dynamic>) fromJson,
  ) {
    return PageResult(
      list: (json['list'] as List<dynamic>)
          .map((e) => fromJson(e as Map<String, dynamic>))
          .toList(),
      total: json['total'] ?? 0,
      pageNum: json['pageNum'] ?? 1,
      pageSize: json['pageSize'] ?? 10,
      totalPages: json['totalPages'] ?? 1,
    );
  }
}
