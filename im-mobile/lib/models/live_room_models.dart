/// 小程序直播与本地电商 - 数据模型
/// Live Commerce Models
/// 
/// 作者: IM Development Team
/// 创建时间: 2026-03-28

// ==================== 直播间模型 ====================

/// 直播间状态枚举
enum LiveRoomStatus {
  preview,      // 预告
  living,       // 直播中
  paused,       // 暂停
  ended,        // 已结束
  replay,       // 回放
}

/// 直播类型枚举
enum LiveRoomType {
  normal,       // 普通直播
  commerce,     // 带货直播
  event,        // 活动直播
}

/// 直播间信息模型
class LiveRoom {
  final String roomId;
  final String anchorId;
  final String? merchantId;
  final String title;
  final String? description;
  final String? coverImage;
  final LiveRoomStatus status;
  final LiveRoomType liveType;
  final String? pullUrl;
  final String? h5Url;
  final DateTime? plannedStartTime;
  final DateTime? actualStartTime;
  final DateTime? endTime;
  final int viewerCount;
  final int peakOnlineCount;
  final int likeCount;
  final int shareCount;
  final int productCount;
  final int orderCount;
  final double salesAmount;
  final bool allowComment;
  final bool allowLinkMic;
  final String? anchorNickname;
  final String? anchorAvatar;
  final int? anchorFansCount;
  final List<String>? tags;
  final double? latitude;
  final double? longitude;
  final String? locationName;
  final double? distance;
  final bool? isFollowed;
  final DateTime? createTime;
  final List<LiveProduct>? products;

  LiveRoom({
    required this.roomId,
    required this.anchorId,
    this.merchantId,
    required this.title,
    this.description,
    this.coverImage,
    required this.status,
    required this.liveType,
    this.pullUrl,
    this.h5Url,
    this.plannedStartTime,
    this.actualStartTime,
    this.endTime,
    this.viewerCount = 0,
    this.peakOnlineCount = 0,
    this.likeCount = 0,
    this.shareCount = 0,
    this.productCount = 0,
    this.orderCount = 0,
    this.salesAmount = 0.0,
    this.allowComment = true,
    this.allowLinkMic = false,
    this.anchorNickname,
    this.anchorAvatar,
    this.anchorFansCount,
    this.tags,
    this.latitude,
    this.longitude,
    this.locationName,
    this.distance,
    this.isFollowed,
    this.createTime,
    this.products,
  });

  factory LiveRoom.fromJson(Map<String, dynamic> json) => LiveRoom(
    roomId: json['roomId'].toString(),
    anchorId: json['anchorId'].toString(),
    merchantId: json['merchantId']?.toString(),
    title: json['title'] ?? '',
    description: json['description'],
    coverImage: json['coverImage'],
    status: LiveRoomStatus.values.byName(json['status'] ?? 'preview'),
    liveType: LiveRoomType.values.byName(json['liveType'] ?? 'normal'),
    pullUrl: json['pullUrl'],
    h5Url: json['h5Url'],
    plannedStartTime: json['plannedStartTime'] != null 
        ? DateTime.parse(json['plannedStartTime']) : null,
    actualStartTime: json['actualStartTime'] != null 
        ? DateTime.parse(json['actualStartTime']) : null,
    endTime: json['endTime'] != null 
        ? DateTime.parse(json['endTime']) : null,
    viewerCount: json['viewerCount'] ?? 0,
    peakOnlineCount: json['peakOnlineCount'] ?? 0,
    likeCount: json['likeCount'] ?? 0,
    shareCount: json['shareCount'] ?? 0,
    productCount: json['productCount'] ?? 0,
    orderCount: json['orderCount'] ?? 0,
    salesAmount: (json['salesAmount'] ?? 0).toDouble(),
    allowComment: json['allowComment'] != 0,
    allowLinkMic: json['allowLinkMic'] == 1,
    anchorNickname: json['anchorNickname'],
    anchorAvatar: json['anchorAvatar'],
    anchorFansCount: json['anchorFansCount'],
    tags: json['tags']?.cast<String>(),
    latitude: json['latitude']?.toDouble(),
    longitude: json['longitude']?.toDouble(),
    locationName: json['locationName'],
    distance: json['distance']?.toDouble(),
    isFollowed: json['isFollowed'],
    createTime: json['createTime'] != null 
        ? DateTime.parse(json['createTime']) : null,
    products: json['products']?.map<LiveProduct>(
        (e) => LiveProduct.fromJson(e)).toList(),
  );

  Map<String, dynamic> toJson() => {
    'roomId': roomId,
    'anchorId': anchorId,
    'merchantId': merchantId,
    'title': title,
    'description': description,
    'coverImage': coverImage,
    'status': status.name,
    'liveType': liveType.name,
    'pullUrl': pullUrl,
    'h5Url': h5Url,
    'plannedStartTime': plannedStartTime?.toIso8601String(),
    'actualStartTime': actualStartTime?.toIso8601String(),
    'endTime': endTime?.toIso8601String(),
    'viewerCount': viewerCount,
    'peakOnlineCount': peakOnlineCount,
    'likeCount': likeCount,
    'shareCount': shareCount,
    'productCount': productCount,
    'orderCount': orderCount,
    'salesAmount': salesAmount,
    'allowComment': allowComment ? 1 : 0,
    'allowLinkMic': allowLinkMic ? 1 : 0,
    'anchorNickname': anchorNickname,
    'anchorAvatar': anchorAvatar,
    'anchorFansCount': anchorFansCount,
    'tags': tags,
    'latitude': latitude,
    'longitude': longitude,
    'locationName': locationName,
    'distance': distance,
    'isFollowed': isFollowed,
    'createTime': createTime?.toIso8601String(),
    'products': products?.map((e) => e.toJson()).toList(),
  };

  /// 获取状态文本
  String get statusText {
    switch (status) {
      case LiveRoomStatus.preview:
        return '预告';
      case LiveRoomStatus.living:
        return '直播中';
      case LiveRoomStatus.paused:
        return '暂停';
      case LiveRoomStatus.ended:
        return '已结束';
      case LiveRoomStatus.replay:
        return '回放';
    }
  }

  /// 获取类型文本
  String get liveTypeText {
    switch (liveType) {
      case LiveRoomType.normal:
        return '普通直播';
      case LiveRoomType.commerce:
        return '带货直播';
      case LiveRoomType.event:
        return '活动直播';
    }
  }

  /// 是否正在直播中
  bool get isLiving => status == LiveRoomStatus.living;

  /// 是否为带货直播
  bool get isCommerce => liveType == LiveRoomType.commerce;

  /// 获取格式化销售额
  String get formattedSalesAmount {
    if (salesAmount >= 10000) {
      return '${(salesAmount / 10000).toStringAsFixed(1)}万';
    }
    return salesAmount.toStringAsFixed(0);
  }

  /// 获取格式化观看人数
  String get formattedViewerCount {
    if (viewerCount >= 10000) {
      return '${(viewerCount / 10000).toStringAsFixed(1)}万';
    }
    return viewerCount.toString();
  }
}
