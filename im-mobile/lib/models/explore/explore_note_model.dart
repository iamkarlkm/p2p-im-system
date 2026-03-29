/// 探店笔记数据模型
/// 用于移动端展示用户发布的探店笔记内容
/// 
/// Author: IM Development Team
/// Since: 2026-03-28

class ExploreNoteModel {
  /// 笔记ID
  final int id;
  
  /// 发布用户ID
  final int userId;
  
  /// 关联POI商户ID
  final int poiId;
  
  /// 商户名称
  final String poiName;
  
  /// 笔记标题
  final String title;
  
  /// 笔记内容
  final String content;
  
  /// 笔记类型：1-图文 2-短视频
  final int noteType;
  
  /// 封面图片URL
  final String coverImage;
  
  /// 图片列表
  final List<String> images;
  
  /// 视频URL
  final String videoUrl;
  
  /// 视频时长（秒）
  final int videoDuration;
  
  /// 探店评分（1-5分）
  final double rating;
  
  /// 口味评分
  final double tasteRating;
  
  /// 环境评分
  final double environmentRating;
  
  /// 服务评分
  final double serviceRating;
  
  /// 性价比评分
  final double valueRating;
  
  /// 人均消费（元）
  final double perCapitaCost;
  
  /// 消费标签
  final List<String> tags;
  
  /// 地理位置-经度
  final double longitude;
  
  /// 地理位置-纬度
  final double latitude;
  
  /// 地理位置名称
  final String locationName;
  
  /// 浏览次数
  final int viewCount;
  
  /// 点赞次数
  final int likeCount;
  
  /// 收藏次数
  final int favoriteCount;
  
  /// 评论次数
  final int commentCount;
  
  /// 分享次数
  final int shareCount;
  
  /// 笔记状态：0-草稿 1-已发布 2-审核中 3-已拒绝 4-已下架
  final int status;
  
  /// 是否精选：0-否 1-是
  final int isFeatured;
  
  /// 发布时间
  final DateTime publishTime;
  
  /// 发布用户信息
  final UserInfo? userInfo;

  ExploreNoteModel({
    required this.id,
    required this.userId,
    required this.poiId,
    required this.poiName,
    required this.title,
    required this.content,
    required this.noteType,
    required this.coverImage,
    required this.images,
    this.videoUrl = '',
    this.videoDuration = 0,
    required this.rating,
    this.tasteRating = 0,
    this.environmentRating = 0,
    this.serviceRating = 0,
    this.valueRating = 0,
    this.perCapitaCost = 0,
    required this.tags,
    required this.longitude,
    required this.latitude,
    required this.locationName,
    this.viewCount = 0,
    this.likeCount = 0,
    this.favoriteCount = 0,
    this.commentCount = 0,
    this.shareCount = 0,
    this.status = 1,
    this.isFeatured = 0,
    required this.publishTime,
    this.userInfo,
  });

  /// 从JSON解析
  factory ExploreNoteModel.fromJson(Map<String, dynamic> json) {
    return ExploreNoteModel(
      id: json['id'] ?? 0,
      userId: json['userId'] ?? 0,
      poiId: json['poiId'] ?? 0,
      poiName: json['poiName'] ?? '',
      title: json['title'] ?? '',
      content: json['content'] ?? '',
      noteType: json['noteType'] ?? 1,
      coverImage: json['coverImage'] ?? '',
      images: List<String>.from(json['images'] ?? []),
      videoUrl: json['videoUrl'] ?? '',
      videoDuration: json['videoDuration'] ?? 0,
      rating: (json['rating'] ?? 0).toDouble(),
      tasteRating: (json['tasteRating'] ?? 0).toDouble(),
      environmentRating: (json['environmentRating'] ?? 0).toDouble(),
      serviceRating: (json['serviceRating'] ?? 0).toDouble(),
      valueRating: (json['valueRating'] ?? 0).toDouble(),
      perCapitaCost: (json['perCapitaCost'] ?? 0).toDouble(),
      tags: List<String>.from(json['tags'] ?? []),
      longitude: (json['longitude'] ?? 0).toDouble(),
      latitude: (json['latitude'] ?? 0).toDouble(),
      locationName: json['locationName'] ?? '',
      viewCount: json['viewCount'] ?? 0,
      likeCount: json['likeCount'] ?? 0,
      favoriteCount: json['favoriteCount'] ?? 0,
      commentCount: json['commentCount'] ?? 0,
      shareCount: json['shareCount'] ?? 0,
      status: json['status'] ?? 1,
      isFeatured: json['isFeatured'] ?? 0,
      publishTime: DateTime.parse(json['publishTime'] ?? DateTime.now().toIso8601String()),
      userInfo: json['userInfo'] != null ? UserInfo.fromJson(json['userInfo']) : null,
    );
  }

  /// 转换为JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'poiId': poiId,
      'poiName': poiName,
      'title': title,
      'content': content,
      'noteType': noteType,
      'coverImage': coverImage,
      'images': images,
      'videoUrl': videoUrl,
      'videoDuration': videoDuration,
      'rating': rating,
      'tasteRating': tasteRating,
      'environmentRating': environmentRating,
      'serviceRating': serviceRating,
      'valueRating': valueRating,
      'perCapitaCost': perCapitaCost,
      'tags': tags,
      'longitude': longitude,
      'latitude': latitude,
      'locationName': locationName,
      'viewCount': viewCount,
      'likeCount': likeCount,
      'favoriteCount': favoriteCount,
      'commentCount': commentCount,
      'shareCount': shareCount,
      'status': status,
      'isFeatured': isFeatured,
      'publishTime': publishTime.toIso8601String(),
      'userInfo': userInfo?.toJson(),
    };
  }

  /// 获取笔记类型文本
  String get noteTypeText {
    switch (noteType) {
      case 1:
        return '图文';
      case 2:
        return '短视频';
      default:
        return '未知';
    }
  }

  /// 获取状态文本
  String get statusText {
    switch (status) {
      case 0:
        return '草稿';
      case 1:
        return '已发布';
      case 2:
        return '审核中';
      case 3:
        return '已拒绝';
      case 4:
        return '已下架';
      default:
        return '未知';
    }
  }

  /// 是否为精选笔记
  bool get isFeaturedNote => isFeatured == 1;

  /// 格式化的浏览数
  String get formattedViewCount {
    return _formatCount(viewCount);
  }

  /// 格式化的点赞数
  String get formattedLikeCount {
    return _formatCount(likeCount);
  }

  /// 格式化数字
  String _formatCount(int count) {
    if (count >= 10000) {
      return '${(count / 10000).toStringAsFixed(1)}w';
    } else if (count >= 1000) {
      return '${(count / 1000).toStringAsFixed(1)}k';
    }
    return count.toString();
  }
}

/// 用户信息简版
class UserInfo {
  final int id;
  final String nickname;
  final String avatar;
  final bool isInfluencer;
  final int influencerLevel;

  UserInfo({
    required this.id,
    required this.nickname,
    required this.avatar,
    this.isInfluencer = false,
    this.influencerLevel = 0,
  });

  factory UserInfo.fromJson(Map<String, dynamic> json) {
    return UserInfo(
      id: json['id'] ?? 0,
      nickname: json['nickname'] ?? '',
      avatar: json['avatar'] ?? '',
      isInfluencer: json['isInfluencer'] ?? false,
      influencerLevel: json['influencerLevel'] ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'nickname': nickname,
      'avatar': avatar,
      'isInfluencer': isInfluencer,
      'influencerLevel': influencerLevel,
    };
  }
}
