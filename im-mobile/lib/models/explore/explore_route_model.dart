/// 探店路线数据模型
/// 用于移动端展示探店路线规划
/// 
/// Author: IM Development Team
/// Since: 2026-03-28

class ExploreRouteModel {
  /// 路线ID
  final int id;
  
  /// 创建用户ID
  final int userId;
  
  /// 路线名称
  final String routeName;
  
  /// 路线描述
  final String description;
  
  /// 路线主题标签
  final List<String> tags;
  
  /// 起点POI ID
  final int startPoiId;
  
  /// 起点名称
  final String startPoiName;
  
  /// 终点POI ID
  final int endPoiId;
  
  /// 终点名称
  final String endPoiName;
  
  /// 路线包含POI列表
  final List<RoutePoiItem> poiList;
  
  /// POI数量
  final int poiCount;
  
  /// 总距离（米）
  final double totalDistance;
  
  /// 预计总耗时（分钟）
  final int estimatedDuration;
  
  /// 预计总消费（元）
  final double estimatedCost;
  
  /// 交通方式：1-步行 2-骑行 3-驾车 4-公共交通 5-混合
  final int transportMode;
  
  /// 路线类型：1-系统推荐 2-用户自定义
  final int routeType;
  
  /// 封面图片
  final String coverImage;
  
  /// 浏览次数
  final int viewCount;
  
  /// 收藏次数
  final int favoriteCount;
  
  /// 使用次数
  final int useCount;
  
  /// 点赞次数
  final int likeCount;
  
  /// 路线状态：0-草稿 1-已发布 2-已下架
  final int status;
  
  /// 是否公开
  final bool isPublic;
  
  /// 是否精选路线
  final bool isFeatured;
  
  /// 创建时间
  final DateTime createTime;
  
  /// 创建者信息
  final UserInfo? creatorInfo;

  ExploreRouteModel({
    required this.id,
    required this.userId,
    required this.routeName,
    required this.description,
    required this.tags,
    required this.startPoiId,
    required this.startPoiName,
    required this.endPoiId,
    required this.endPoiName,
    required this.poiList,
    required this.poiCount,
    required this.totalDistance,
    required this.estimatedDuration,
    this.estimatedCost = 0,
    required this.transportMode,
    required this.routeType,
    required this.coverImage,
    this.viewCount = 0,
    this.favoriteCount = 0,
    this.useCount = 0,
    this.likeCount = 0,
    this.status = 1,
    this.isPublic = true,
    this.isFeatured = false,
    required this.createTime,
    this.creatorInfo,
  });

  factory ExploreRouteModel.fromJson(Map<String, dynamic> json) {
    return ExploreRouteModel(
      id: json['id'] ?? 0,
      userId: json['userId'] ?? 0,
      routeName: json['routeName'] ?? '',
      description: json['description'] ?? '',
      tags: List<String>.from(json['tags'] ?? []),
      startPoiId: json['startPoiId'] ?? 0,
      startPoiName: json['startPoiName'] ?? '',
      endPoiId: json['endPoiId'] ?? 0,
      endPoiName: json['endPoiName'] ?? '',
      poiList: (json['poiList'] as List?)
              ?.map((e) => RoutePoiItem.fromJson(e))
              .toList() ??
          [],
      poiCount: json['poiCount'] ?? 0,
      totalDistance: (json['totalDistance'] ?? 0).toDouble(),
      estimatedDuration: json['estimatedDuration'] ?? 0,
      estimatedCost: (json['estimatedCost'] ?? 0).toDouble(),
      transportMode: json['transportMode'] ?? 1,
      routeType: json['routeType'] ?? 2,
      coverImage: json['coverImage'] ?? '',
      viewCount: json['viewCount'] ?? 0,
      favoriteCount: json['favoriteCount'] ?? 0,
      useCount: json['useCount'] ?? 0,
      likeCount: json['likeCount'] ?? 0,
      status: json['status'] ?? 1,
      isPublic: json['isPublic'] ?? true,
      isFeatured: json['isFeatured'] ?? false,
      createTime: DateTime.parse(json['createTime'] ?? DateTime.now().toIso8601String()),
      creatorInfo: json['creatorInfo'] != null ? UserInfo.fromJson(json['creatorInfo']) : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'routeName': routeName,
      'description': description,
      'tags': tags,
      'startPoiId': startPoiId,
      'startPoiName': startPoiName,
      'endPoiId': endPoiId,
      'endPoiName': endPoiName,
      'poiList': poiList.map((e) => e.toJson()).toList(),
      'poiCount': poiCount,
      'totalDistance': totalDistance,
      'estimatedDuration': estimatedDuration,
      'estimatedCost': estimatedCost,
      'transportMode': transportMode,
      'routeType': routeType,
      'coverImage': coverImage,
      'viewCount': viewCount,
      'favoriteCount': favoriteCount,
      'useCount': useCount,
      'likeCount': likeCount,
      'status': status,
      'isPublic': isPublic,
      'isFeatured': isFeatured,
      'createTime': createTime.toIso8601String(),
      'creatorInfo': creatorInfo?.toJson(),
    };
  }

  /// 获取交通方式文本
  String get transportModeText {
    switch (transportMode) {
      case 1:
        return '步行';
      case 2:
        return '骑行';
      case 3:
        return '驾车';
      case 4:
        return '公共交通';
      case 5:
        return '混合';
      default:
        return '未知';
    }
  }

  /// 获取交通方式图标
  String get transportModeIcon {
    switch (transportMode) {
      case 1:
        return '🚶';
      case 2:
        return '🚴';
      case 3:
        return '🚗';
      case 4:
        return '🚌';
      case 5:
        return '🔄';
      default:
        return '📍';
    }
  }

  /// 格式化的距离
  String get formattedDistance {
    if (totalDistance >= 1000) {
      return '${(totalDistance / 1000).toStringAsFixed(1)} km';
    }
    return '${totalDistance.toStringAsFixed(0)} m';
  }

  /// 格式化的时间
  String get formattedDuration {
    if (estimatedDuration >= 60) {
      final hours = estimatedDuration ~/ 60;
      final minutes = estimatedDuration % 60;
      if (minutes > 0) {
        return '${hours}h ${minutes}min';
      }
      return '${hours}h';
    }
    return '${estimatedDuration}min';
  }

  /// 格式化的消费金额
  String get formattedCost {
    if (estimatedCost >= 10000) {
      return '¥${(estimatedCost / 10000).toStringAsFixed(1)}w';
    }
    return '¥${estimatedCost.toStringAsFixed(0)}';
  }
}

/// 路线POI节点
class RoutePoiItem {
  /// POI ID
  final int poiId;
  
  /// POI名称
  final String poiName;
  
  /// 顺序索引
  final int orderIndex;
  
  /// 经度
  final double longitude;
  
  /// 纬度
  final double latitude;
  
  /// 地址
  final String address;
  
  /// 预计停留时间（分钟）
  final int estimatedStayTime;
  
  /// 推荐消费金额
  final double recommendedCost;
  
  /// 推荐活动
  final String recommendedActivity;
  
  /// 图片URL
  final String imageUrl;

  RoutePoiItem({
    required this.poiId,
    required this.poiName,
    required this.orderIndex,
    required this.longitude,
    required this.latitude,
    required this.address,
    this.estimatedStayTime = 30,
    this.recommendedCost = 0,
    this.recommendedActivity = '',
    this.imageUrl = '',
  });

  factory RoutePoiItem.fromJson(Map<String, dynamic> json) {
    return RoutePoiItem(
      poiId: json['poiId'] ?? 0,
      poiName: json['poiName'] ?? '',
      orderIndex: json['orderIndex'] ?? 0,
      longitude: (json['longitude'] ?? 0).toDouble(),
      latitude: (json['latitude'] ?? 0).toDouble(),
      address: json['address'] ?? '',
      estimatedStayTime: json['estimatedStayTime'] ?? 30,
      recommendedCost: (json['recommendedCost'] ?? 0).toDouble(),
      recommendedActivity: json['recommendedActivity'] ?? '',
      imageUrl: json['imageUrl'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'poiId': poiId,
      'poiName': poiName,
      'orderIndex': orderIndex,
      'longitude': longitude,
      'latitude': latitude,
      'address': address,
      'estimatedStayTime': estimatedStayTime,
      'recommendedCost': recommendedCost,
      'recommendedActivity': recommendedActivity,
      'imageUrl': imageUrl,
    };
  }
}

/// 用户信息简版
class UserInfo {
  final int id;
  final String nickname;
  final String avatar;

  UserInfo({
    required this.id,
    required this.nickname,
    required this.avatar,
  });

  factory UserInfo.fromJson(Map<String, dynamic> json) {
    return UserInfo(
      id: json['id'] ?? 0,
      nickname: json['nickname'] ?? '',
      avatar: json['avatar'] ?? '',
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'nickname': nickname,
      'avatar': avatar,
    };
  }
}
