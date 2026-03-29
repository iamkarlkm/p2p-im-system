import 'dart:convert';
import 'package:flutter/foundation.dart';

/// 直播间模型
class LiveRoomModel {
  final String id;
  final String title;
  final String description;
  final String? coverImage;
  final String streamerId;
  final String streamerName;
  final String? streamerAvatar;
  final LiveRoomStatus status;
  final int onlineCount;
  final int likeCount;
  final DateTime? startTime;
  final DateTime? endTime;
  final Duration? duration;
  final List<String>? tags;
  final bool isPublic;
  final String? category;
  final Map<String, dynamic>? extraData;
  final DateTime createdAt;
  final DateTime updatedAt;

  LiveRoomModel({
    required this.id,
    required this.title,
    required this.description,
    this.coverImage,
    required this.streamerId,
    required this.streamerName,
    this.streamerAvatar,
    this.status = LiveRoomStatus.preparing,
    this.onlineCount = 0,
    this.likeCount = 0,
    this.startTime,
    this.endTime,
    this.duration,
    this.tags,
    this.isPublic = true,
    this.category,
    this.extraData,
    required this.createdAt,
    required this.updatedAt,
  });

  factory LiveRoomModel.fromJson(Map<String, dynamic> json) {
    return LiveRoomModel(
      id: json['id'] ?? '',
      title: json['title'] ?? '',
      description: json['description'] ?? '',
      coverImage: json['coverImage'],
      streamerId: json['streamerId'] ?? '',
      streamerName: json['streamerName'] ?? '',
      streamerAvatar: json['streamerAvatar'],
      status: LiveRoomStatus.values.byName(json['status'] ?? 'preparing'),
      onlineCount: json['onlineCount'] ?? 0,
      likeCount: json['likeCount'] ?? 0,
      startTime: json['startTime'] != null 
          ? DateTime.parse(json['startTime']) 
          : null,
      endTime: json['endTime'] != null 
          ? DateTime.parse(json['endTime']) 
          : null,
      duration: json['duration'] != null 
          ? Duration(seconds: json['duration']) 
          : null,
      tags: json['tags'] != null 
          ? List<String>.from(json['tags']) 
          : null,
      isPublic: json['isPublic'] ?? true,
      category: json['category'],
      extraData: json['extraData'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'title': title,
    'description': description,
    'coverImage': coverImage,
    'streamerId': streamerId,
    'streamerName': streamerName,
    'streamerAvatar': streamerAvatar,
    'status': status.name,
    'onlineCount': onlineCount,
    'likeCount': likeCount,
    'startTime': startTime?.toIso8601String(),
    'endTime': endTime?.toIso8601String(),
    'duration': duration?.inSeconds,
    'tags': tags,
    'isPublic': isPublic,
    'category': category,
    'extraData': extraData,
    'createdAt': createdAt.toIso8601String(),
    'updatedAt': updatedAt.toIso8601String(),
  };

  LiveRoomModel copyWith({
    String? id,
    String? title,
    String? description,
    String? coverImage,
    String? streamerId,
    String? streamerName,
    String? streamerAvatar,
    LiveRoomStatus? status,
    int? onlineCount,
    int? likeCount,
    DateTime? startTime,
    DateTime? endTime,
    Duration? duration,
    List<String>? tags,
    bool? isPublic,
    String? category,
    Map<String, dynamic>? extraData,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return LiveRoomModel(
      id: id ?? this.id,
      title: title ?? this.title,
      description: description ?? this.description,
      coverImage: coverImage ?? this.coverImage,
      streamerId: streamerId ?? this.streamerId,
      streamerName: streamerName ?? this.streamerName,
      streamerAvatar: streamerAvatar ?? this.streamerAvatar,
      status: status ?? this.status,
      onlineCount: onlineCount ?? this.onlineCount,
      likeCount: likeCount ?? this.likeCount,
      startTime: startTime ?? this.startTime,
      endTime: endTime ?? this.endTime,
      duration: duration ?? this.duration,
      tags: tags ?? this.tags,
      isPublic: isPublic ?? this.isPublic,
      category: category ?? this.category,
      extraData: extraData ?? this.extraData,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  @override
  String toString() {
    return 'LiveRoomModel(id: $id, title: $title, status: $status, onlineCount: $onlineCount)';
  }
}

/// 直播间状态
enum LiveRoomStatus {
  preparing,   // 准备中
  living,      // 直播中
  paused,      // 暂停中
  ended,       // 已结束
  banned,      // 被封禁
}

/// 直播商品模型
class LiveProductModel {
  final String id;
  final String roomId;
  final String name;
  final String? description;
  final List<String>? images;
  final double price;
  final double? originalPrice;
  final int stock;
  final int sold;
  final bool isExplaining;  // 是否正在讲解
  final int explainOrder;   // 讲解顺序
  final String? detailUrl;
  final Map<String, dynamic>? extraData;
  final DateTime createdAt;
  final DateTime updatedAt;

  LiveProductModel({
    required this.id,
    required this.roomId,
    required this.name,
    this.description,
    this.images,
    required this.price,
    this.originalPrice,
    this.stock = 0,
    this.sold = 0,
    this.isExplaining = false,
    this.explainOrder = 0,
    this.detailUrl,
    this.extraData,
    required this.createdAt,
    required this.updatedAt,
  });

  factory LiveProductModel.fromJson(Map<String, dynamic> json) {
    return LiveProductModel(
      id: json['id'] ?? '',
      roomId: json['roomId'] ?? '',
      name: json['name'] ?? '',
      description: json['description'],
      images: json['images'] != null 
          ? List<String>.from(json['images']) 
          : null,
      price: (json['price'] ?? 0.0).toDouble(),
      originalPrice: json['originalPrice'] != null 
          ? (json['originalPrice'] as num).toDouble() 
          : null,
      stock: json['stock'] ?? 0,
      sold: json['sold'] ?? 0,
      isExplaining: json['isExplaining'] ?? false,
      explainOrder: json['explainOrder'] ?? 0,
      detailUrl: json['detailUrl'],
      extraData: json['extraData'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'roomId': roomId,
    'name': name,
    'description': description,
    'images': images,
    'price': price,
    'originalPrice': originalPrice,
    'stock': stock,
    'sold': sold,
    'isExplaining': isExplaining,
    'explainOrder': explainOrder,
    'detailUrl': detailUrl,
    'extraData': extraData,
    'createdAt': createdAt.toIso8601String(),
    'updatedAt': updatedAt.toIso8601String(),
  };

  /// 获取折扣信息
  double? get discount {
    if (originalPrice != null && originalPrice! > 0) {
      return (price / originalPrice! * 10).clamp(0.0, 10.0);
    }
    return null;
  }

  /// 获取折扣字符串
  String get discountText {
    final d = discount;
    if (d != null) {
      return '${d.toStringAsFixed(1)}折';
    }
    return '';
  }

  /// 是否已售罄
  bool get isSoldOut => stock <= 0;

  LiveProductModel copyWith({
    String? id,
    String? roomId,
    String? name,
    String? description,
    List<String>? images,
    double? price,
    double? originalPrice,
    int? stock,
    int? sold,
    bool? isExplaining,
    int? explainOrder,
    String? detailUrl,
    Map<String, dynamic>? extraData,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return LiveProductModel(
      id: id ?? this.id,
      roomId: roomId ?? this.roomId,
      name: name ?? this.name,
      description: description ?? this.description,
      images: images ?? this.images,
      price: price ?? this.price,
      originalPrice: originalPrice ?? this.originalPrice,
      stock: stock ?? this.stock,
      sold: sold ?? this.sold,
      isExplaining: isExplaining ?? this.isExplaining,
      explainOrder: explainOrder ?? this.explainOrder,
      detailUrl: detailUrl ?? this.detailUrl,
      extraData: extraData ?? this.extraData,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }

  @override
  String toString() {
    return 'LiveProductModel(id: $id, name: $name, price: $price, stock: $stock)';
  }
}

/// 直播消息模型
class LiveMessageModel {
  final String id;
  final String roomId;
  final LiveMessageType type;
  final String? userId;
  final String? userName;
  final String? userAvatar;
  final String? content;
  final LiveGiftModel? gift;
  final String? productId;
  final LiveProductModel? product;
  final int? userCount;
  final int? likeCount;
  final DateTime timestamp;
  final Map<String, dynamic>? extraData;

  LiveMessageModel({
    required this.id,
    required this.roomId,
    required this.type,
    this.userId,
    this.userName,
    this.userAvatar,
    this.content,
    this.gift,
    this.productId,
    this.product,
    this.userCount,
    this.likeCount,
    required this.timestamp,
    this.extraData,
  });

  factory LiveMessageModel.fromJson(Map<String, dynamic> json) {
    return LiveMessageModel(
      id: json['id'] ?? '',
      roomId: json['roomId'] ?? '',
      type: LiveMessageType.values.byName(json['type'] ?? 'chat'),
      userId: json['userId'],
      userName: json['userName'],
      userAvatar: json['userAvatar'],
      content: json['content'],
      gift: json['gift'] != null ? LiveGiftModel.fromJson(json['gift']) : null,
      productId: json['productId'],
      product: json['product'] != null 
          ? LiveProductModel.fromJson(json['product']) 
          : null,
      userCount: json['userCount'],
      likeCount: json['likeCount'],
      timestamp: DateTime.parse(json['timestamp']),
      extraData: json['extraData'],
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'roomId': roomId,
    'type': type.name,
    'userId': userId,
    'userName': userName,
    'userAvatar': userAvatar,
    'content': content,
    'gift': gift?.toJson(),
    'productId': productId,
    'product': product?.toJson(),
    'userCount': userCount,
    'likeCount': likeCount,
    'timestamp': timestamp.toIso8601String(),
    'extraData': extraData,
  };

  /// 是否是系统消息
  bool get isSystemMessage => type == LiveMessageType.system;

  /// 获取显示文本
  String get displayText {
    switch (type) {
      case LiveMessageType.chat:
        return content ?? '';
      case LiveMessageType.enter:
        return '$userName 进入了直播间';
      case LiveMessageType.leave:
        return '$userName 离开了直播间';
      case LiveMessageType.like:
        return '$userName 点赞了';
      case LiveMessageType.gift:
        return '$userName 送出了 ${gift?.name ?? '礼物'}';
      case LiveMessageType.product:
        return '商品已更新: ${product?.name ?? ''}';
      case LiveMessageType.currentProduct:
        return '当前讲解: ${product?.name ?? ''}';
      case LiveMessageType.follow:
        return '$userName 关注了主播';
      case LiveMessageType.share:
        return '$userName 分享了直播间';
      case LiveMessageType.system:
        return content ?? '系统消息';
      case LiveMessageType.userCount:
        return '当前在线: $userCount 人';
    }
  }

  @override
  String toString() {
    return 'LiveMessageModel(id: $id, type: $type, userName: $userName)';
  }
}

/// 直播消息类型
enum LiveMessageType {
  chat,           // 聊天消息
  enter,          // 进入直播间
  leave,          // 离开直播间
  like,           // 点赞
  gift,           // 礼物
  product,        // 商品更新
  currentProduct, // 当前讲解商品
  follow,         // 关注
  share,          // 分享
  system,         // 系统消息
  userCount,      // 在线人数更新
}

/// 直播礼物模型
class LiveGiftModel {
  final String id;
  final String name;
  final String? icon;
  final double price;
  final String? animation;
  final int? animationDuration;
  final Map<String, dynamic>? extraData;

  LiveGiftModel({
    required this.id,
    required this.name,
    this.icon,
    required this.price,
    this.animation,
    this.animationDuration,
    this.extraData,
  });

  factory LiveGiftModel.fromJson(Map<String, dynamic> json) {
    return LiveGiftModel(
      id: json['id'] ?? '',
      name: json['name'] ?? '',
      icon: json['icon'],
      price: (json['price'] ?? 0.0).toDouble(),
      animation: json['animation'],
      animationDuration: json['animationDuration'],
      extraData: json['extraData'],
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'name': name,
    'icon': icon,
    'price': price,
    'animation': animation,
    'animationDuration': animationDuration,
    'extraData': extraData,
  };

  @override
  String toString() {
    return 'LiveGiftModel(id: $id, name: $name, price: $price)';
  }
}

/// 直播预约模型
class LiveAppointmentModel {
  final String id;
  final String roomId;
  final String title;
  final String? coverImage;
  final DateTime scheduledStartTime;
  final String? description;
  final String? streamerId;
  final String? streamerName;
  final bool isReminded;
  final DateTime? remindTime;
  final DateTime createdAt;
  final DateTime updatedAt;

  LiveAppointmentModel({
    required this.id,
    required this.roomId,
    required this.title,
    this.coverImage,
    required this.scheduledStartTime,
    this.description,
    this.streamerId,
    this.streamerName,
    this.isReminded = false,
    this.remindTime,
    required this.createdAt,
    required this.updatedAt,
  });

  factory LiveAppointmentModel.fromJson(Map<String, dynamic> json) {
    return LiveAppointmentModel(
      id: json['id'] ?? '',
      roomId: json['roomId'] ?? '',
      title: json['title'] ?? '',
      coverImage: json['coverImage'],
      scheduledStartTime: DateTime.parse(json['scheduledStartTime']),
      description: json['description'],
      streamerId: json['streamerId'],
      streamerName: json['streamerName'],
      isReminded: json['isReminded'] ?? false,
      remindTime: json['remindTime'] != null 
          ? DateTime.parse(json['remindTime']) 
          : null,
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  Map<String, dynamic> toJson() => {
    'id': id,
    'roomId': roomId,
    'title': title,
    'coverImage': coverImage,
    'scheduledStartTime': scheduledStartTime.toIso8601String(),
    'description': description,
    'streamerId': streamerId,
    'streamerName': streamerName,
    'isReminded': isReminded,
    'remindTime': remindTime?.toIso8601String(),
    'createdAt': createdAt.toIso8601String(),
    'updatedAt': updatedAt.toIso8601String(),
  };

  /// 获取倒计时文本
  String get countdownText {
    final now = DateTime.now();
    final diff = scheduledStartTime.difference(now);
    
    if (diff.isNegative) {
      return '直播中';
    }
    
    if (diff.inDays > 0) {
      return '${diff.inDays}天后开播';
    } else if (diff.inHours > 0) {
      return '${diff.inHours}小时后开播';
    } else if (diff.inMinutes > 0) {
      return '${diff.inMinutes}分钟后开播';
    } else {
      return '即将开播';
    }
  }

  /// 是否应该提醒
  bool shouldRemind() {
    if (isReminded) return false;
    
    final now = DateTime.now();
    final diff = scheduledStartTime.difference(now);
    
    // 开播前15分钟提醒
    return diff.inMinutes <= 15 && diff.inMinutes > 0;
  }

  @override
  String toString() {
    return 'LiveAppointmentModel(id: $id, title: $title, scheduledStartTime: $scheduledStartTime)';
  }
}
