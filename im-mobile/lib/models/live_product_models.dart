/// 小程序直播与本地电商 - 商品模型 (续)
/// 
/// 作者: IM Development Team
/// 创建时间: 2026-03-28

/// 直播商品状态枚举
enum LiveProductStatus {
  offline,      // 下架
  onSale,       // 上架
  soldOut,      // 售罄
}

/// 直播商品信息模型
class LiveProduct {
  final String productId;
  final String roomId;
  final String name;
  final String? subtitle;
  final String? mainImage;
  final List<String>? images;
  final String? description;
  final double originalPrice;
  final double livePrice;
  final int stock;
  final int soldCount;
  final int limitPerUser;
  final LiveProductStatus status;
  final int sortOrder;
  final bool isRecommended;
  final bool isExplaining;
  final String? detailUrl;
  final String? mpPagePath;
  final double freight;
  final int weight;
  final DateTime? createTime;
  final List<ProductSpec>? specifications;
  final List<ProductAttr>? attributes;

  LiveProduct({
    required this.productId,
    required this.roomId,
    required this.name,
    this.subtitle,
    this.mainImage,
    this.images,
    this.description,
    required this.originalPrice,
    required this.livePrice,
    this.stock = 0,
    this.soldCount = 0,
    this.limitPerUser = 0,
    required this.status,
    this.sortOrder = 0,
    this.isRecommended = false,
    this.isExplaining = false,
    this.detailUrl,
    this.mpPagePath,
    this.freight = 0,
    this.weight = 0,
    this.createTime,
    this.specifications,
    this.attributes,
  });

  factory LiveProduct.fromJson(Map<String, dynamic> json) => LiveProduct(
    productId: json['productId'].toString(),
    roomId: json['roomId'].toString(),
    name: json['name'] ?? '',
    subtitle: json['subtitle'],
    mainImage: json['mainImage'],
    images: json['images']?.cast<String>(),
    description: json['description'],
    originalPrice: (json['originalPrice'] ?? 0).toDouble(),
    livePrice: (json['livePrice'] ?? 0).toDouble(),
    stock: json['stock'] ?? 0,
    soldCount: json['soldCount'] ?? 0,
    limitPerUser: json['limitPerUser'] ?? 0,
    status: LiveProductStatus.values.byName(json['status'] ?? 'offline'),
    sortOrder: json['sortOrder'] ?? 0,
    isRecommended: json['isRecommended'] == 1,
    isExplaining: json['isExplaining'] == 1,
    detailUrl: json['detailUrl'],
    mpPagePath: json['mpPagePath'],
    freight: (json['freight'] ?? 0).toDouble(),
    weight: json['weight'] ?? 0,
    createTime: json['createTime'] != null 
        ? DateTime.parse(json['createTime']) : null,
    specifications: json['specifications']?.map<ProductSpec>(
        (e) => ProductSpec.fromJson(e)).toList(),
    attributes: json['attributes']?.map<ProductAttr>(
        (e) => ProductAttr.fromJson(e)).toList(),
  );

  Map<String, dynamic> toJson() => {
    'productId': productId,
    'roomId': roomId,
    'name': name,
    'subtitle': subtitle,
    'mainImage': mainImage,
    'images': images,
    'description': description,
    'originalPrice': originalPrice,
    'livePrice': livePrice,
    'stock': stock,
    'soldCount': soldCount,
    'limitPerUser': limitPerUser,
    'status': status.name,
    'sortOrder': sortOrder,
    'isRecommended': isRecommended ? 1 : 0,
    'isExplaining': isExplaining ? 1 : 0,
    'detailUrl': detailUrl,
    'mpPagePath': mpPagePath,
    'freight': freight,
    'weight': weight,
    'createTime': createTime?.toIso8601String(),
    'specifications': specifications?.map((e) => e.toJson()).toList(),
    'attributes': attributes?.map((e) => e.toJson()).toList(),
  };

  /// 状态文本
  String get statusText {
    switch (status) {
      case LiveProductStatus.offline:
        return '已下架';
      case LiveProductStatus.onSale:
        return '在售';
      case LiveProductStatus.soldOut:
        return '已售罄';
    }
  }

  /// 是否在售
  bool get isOnSale => status == LiveProductStatus.onSale && stock > 0;

  /// 折扣率
  double get discountRate {
    if (originalPrice <= 0 || livePrice <= 0) return 0;
    return (livePrice / originalPrice * 100).roundToDouble();
  }

  /// 折扣文本
  String get discountText {
    final rate = discountRate;
    if (rate >= 100) return '';
    if (rate >= 10) return '${rate.toInt()}折';
    return '${(rate / 10).toStringAsFixed(1)}折';
  }

  /// 节省金额
  double get savedAmount => originalPrice - livePrice;

  /// 是否包邮
  bool get isFreeShipping => freight <= 0;
}

/// 商品规格
class ProductSpec {
  final String name;
  final List<String> values;

  ProductSpec({required this.name, required this.values});

  factory ProductSpec.fromJson(Map<String, dynamic> json) => ProductSpec(
    name: json['name'] ?? '',
    values: json['values']?.cast<String>() ?? [],
  );

  Map<String, dynamic> toJson() => {
    'name': name,
    'values': values,
  };
}

/// 商品属性
class ProductAttr {
  final String name;
  final String value;

  ProductAttr({required this.name, required this.value});

  factory ProductAttr.fromJson(Map<String, dynamic> json) => ProductAttr(
    name: json['name'] ?? '',
    value: json['value'] ?? '',
  );

  Map<String, dynamic> toJson() => {
    'name': name,
    'value': value,
  };
}

/// 直播评论/弹幕模型
class LiveComment {
  final String commentId;
  final String roomId;
  final String senderId;
  final String senderNickname;
  final String? senderAvatar;
  final String content;
  final int commentType;  // 1-普通 2-礼物 3-系统 4-点赞 5-进入
  final bool isPinned;
  final bool isAnchor;
  final bool isAdmin;
  final String? giftName;
  final int? giftCount;
  final double? giftValue;
  final DateTime createTime;

  LiveComment({
    required this.commentId,
    required this.roomId,
    required this.senderId,
    required this.senderNickname,
    this.senderAvatar,
    required this.content,
    this.commentType = 1,
    this.isPinned = false,
    this.isAnchor = false,
    this.isAdmin = false,
    this.giftName,
    this.giftCount,
    this.giftValue,
    required this.createTime,
  });

  factory LiveComment.fromJson(Map<String, dynamic> json) => LiveComment(
    commentId: json['commentId'].toString(),
    roomId: json['roomId'].toString(),
    senderId: json['senderId'].toString(),
    senderNickname: json['senderNickname'] ?? '',
    senderAvatar: json['senderAvatar'],
    content: json['content'] ?? '',
    commentType: json['commentType'] ?? 1,
    isPinned: json['isPinned'] == 1,
    isAnchor: json['isAnchor'] == 1,
    isAdmin: json['isAdmin'] == 1,
    giftName: json['giftName'],
    giftCount: json['giftCount'],
    giftValue: json['giftValue']?.toDouble(),
    createTime: DateTime.parse(json['createTime'] ?? DateTime.now().toIso8601String()),
  );

  Map<String, dynamic> toJson() => {
    'commentId': commentId,
    'roomId': roomId,
    'senderId': senderId,
    'senderNickname': senderNickname,
    'senderAvatar': senderAvatar,
    'content': content,
    'commentType': commentType,
    'isPinned': isPinned ? 1 : 0,
    'isAnchor': isAnchor ? 1 : 0,
    'isAdmin': isAdmin ? 1 : 0,
    'giftName': giftName,
    'giftCount': giftCount,
    'giftValue': giftValue,
    'createTime': createTime.toIso8601String(),
  };

  /// 类型文本
  String get typeText {
    switch (commentType) {
      case 1:
        return '弹幕';
      case 2:
        return '礼物';
      case 3:
        return '系统';
      case 4:
        return '点赞';
      case 5:
        return '进入';
      default:
        return '';
    }
  }

  /// 是否为礼物消息
  bool get isGift => commentType == 2;

  /// 是否为系统消息
  bool get isSystem => commentType == 3;

  /// 时间显示文本
  String get timeText {
    final now = DateTime.now();
    final diff = now.difference(createTime);
    
    if (diff.inSeconds < 60) return '刚刚';
    if (diff.inMinutes < 60) return '${diff.inMinutes}分钟前';
    if (diff.inHours < 24) return '${diff.inHours}小时前';
    return '${diff.inDays}天前';
  }
}
