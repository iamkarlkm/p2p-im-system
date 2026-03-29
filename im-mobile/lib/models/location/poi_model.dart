import 'package:im_mobile/services/navigation/navigation_service.dart';

/// POI（兴趣点）模型
/// 
/// 用于表示地图上的商户、景点、设施等地点
class POIModel {
  /// POI唯一标识
  final String id;
  
  /// POI名称
  final String name;
  
  /// 坐标位置
  final LatLng location;
  
  /// 地址
  final String? address;
  
  /// 电话号码
  final String? phone;
  
  /// 分类编码
  final String? categoryCode;
  
  /// 分类名称
  final String? categoryName;
  
  /// 评分（0-5）
  final double? rating;
  
  /// 人均消费
  final double? averageCost;
  
  /// 营业时间
  final String? businessHours;
  
  /// 图片列表
  final List<String>? images;
  
  /// 缩略图
  final String? thumbnail;
  
  /// 距离（米）- 动态计算
  double? distance;
  
  /// 商户ID（如果是商家POI）
  final String? merchantId;
  
  /// 是否收藏
  bool isFavorite;
  
  /// 标签列表
  final List<String> tags;
  
  /// 设施列表
  final List<String> facilities;
  
  /// 停车场信息
  final ParkingInfo? parkingInfo;
  
  /// 室内地图信息
  final IndoorInfo? indoorInfo;
  
  /// 创建时间
  final DateTime createdAt;
  
  /// 更新时间
  final DateTime updatedAt;

  POIModel({
    required this.id,
    required this.name,
    required this.location,
    this.address,
    this.phone,
    this.categoryCode,
    this.categoryName,
    this.rating,
    this.averageCost,
    this.businessHours,
    this.images,
    this.thumbnail,
    this.distance,
    this.merchantId,
    this.isFavorite = false,
    this.tags = const [],
    this.facilities = const [],
    this.parkingInfo,
    this.indoorInfo,
    DateTime? createdAt,
    DateTime? updatedAt,
  })  : this.createdAt = createdAt ?? DateTime.now(),
        this.updatedAt = updatedAt ?? DateTime.now();

  /// 格式化距离显示
  String get formattedDistance {
    if (distance == null) return '';
    if (distance! >= 1000) {
      return '${(distance! / 1000).toStringAsFixed(1)}km';
    } else {
      return '${distance!.round()}m';
    }
  }

  /// 格式化评分显示
  String get formattedRating {
    if (rating == null) return '暂无评分';
    return '${rating!.toStringAsFixed(1)}分';
  }

  /// 评分星级
  List<bool> get ratingStars {
    final stars = <bool>[];
    final fullStars = rating?.floor() ?? 0;
    for (int i = 0; i < 5; i++) {
      stars.add(i < fullStars);
    }
    return stars;
  }

  /// 格式化人均消费
  String get formattedAverageCost {
    if (averageCost == null) return '';
    return '人均¥${averageCost!.toStringAsFixed(0)}';
  }

  /// 是否营业中
  bool get isOpen {
    if (businessHours == null) return true;
    // 简化判断，实际应该解析营业时间
    return true;
  }

  /// 营业状态文本
  String get businessStatus {
    return isOpen ? '营业中' : '已休息';
  }

  /// 类别图标
  String get categoryIcon {
    switch (categoryCode) {
      case 'food':
        return '🍽️';
      case 'hotel':
        return '🏨';
      case 'shopping':
        return '🛍️';
      case 'entertainment':
        return '🎬';
      case 'scenic':
        return '🏞️';
      case 'transport':
        return '🚇';
      case 'service':
        return '🔧';
      case 'medical':
        return '🏥';
      case 'education':
        return '🎓';
      case 'parking':
        return '🅿️';
      default:
        return '📍';
    }
  }

  /// 完整地址
  String get fullAddress {
    final parts = <String>[];
    if (address != null && address!.isNotEmpty) {
      parts.add(address!);
    }
    return parts.join(' ');
  }

  /// 是否有停车场
  bool get hasParking => parkingInfo != null;

  /// 是否支持室内导航
  bool get hasIndoorMap => indoorInfo != null;

  /// 转换为JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'location': {'lat': location.latitude, 'lng': location.longitude},
      'address': address,
      'phone': phone,
      'categoryCode': categoryCode,
      'categoryName': categoryName,
      'rating': rating,
      'averageCost': averageCost,
      'businessHours': businessHours,
      'images': images,
      'thumbnail': thumbnail,
      'distance': distance,
      'merchantId': merchantId,
      'isFavorite': isFavorite,
      'tags': tags,
      'facilities': facilities,
      'parkingInfo': parkingInfo?.toJson(),
      'indoorInfo': indoorInfo?.toJson(),
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
    };
  }

  /// 从JSON创建
  factory POIModel.fromJson(Map<String, dynamic> json) {
    return POIModel(
      id: json['id'],
      name: json['name'],
      location: LatLng(json['location']['lat'], json['location']['lng']),
      address: json['address'],
      phone: json['phone'],
      categoryCode: json['categoryCode'],
      categoryName: json['categoryName'],
      rating: json['rating']?.toDouble(),
      averageCost: json['averageCost']?.toDouble(),
      businessHours: json['businessHours'],
      images: json['images'] != null ? List<String>.from(json['images']) : null,
      thumbnail: json['thumbnail'],
      distance: json['distance']?.toDouble(),
      merchantId: json['merchantId'],
      isFavorite: json['isFavorite'] ?? false,
      tags: List<String>.from(json['tags'] ?? []),
      facilities: List<String>.from(json['facilities'] ?? []),
      parkingInfo: json['parkingInfo'] != null
          ? ParkingInfo.fromJson(json['parkingInfo'])
          : null,
      indoorInfo: json['indoorInfo'] != null
          ? IndoorInfo.fromJson(json['indoorInfo'])
          : null,
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  /// 复制并修改
  POIModel copyWith({
    String? id,
    String? name,
    LatLng? location,
    String? address,
    String? phone,
    String? categoryCode,
    String? categoryName,
    double? rating,
    double? averageCost,
    String? businessHours,
    List<String>? images,
    String? thumbnail,
    double? distance,
    String? merchantId,
    bool? isFavorite,
    List<String>? tags,
    List<String>? facilities,
    ParkingInfo? parkingInfo,
    IndoorInfo? indoorInfo,
  }) {
    return POIModel(
      id: id ?? this.id,
      name: name ?? this.name,
      location: location ?? this.location,
      address: address ?? this.address,
      phone: phone ?? this.phone,
      categoryCode: categoryCode ?? this.categoryCode,
      categoryName: categoryName ?? this.categoryName,
      rating: rating ?? this.rating,
      averageCost: averageCost ?? this.averageCost,
      businessHours: businessHours ?? this.businessHours,
      images: images ?? this.images,
      thumbnail: thumbnail ?? this.thumbnail,
      distance: distance ?? this.distance,
      merchantId: merchantId ?? this.merchantId,
      isFavorite: isFavorite ?? this.isFavorite,
      tags: tags ?? this.tags,
      facilities: facilities ?? this.facilities,
      parkingInfo: parkingInfo ?? this.parkingInfo,
      indoorInfo: indoorInfo ?? this.indoorInfo,
      createdAt: createdAt,
      updatedAt: DateTime.now(),
    );
  }

  @override
  String toString() {
    return 'POIModel(id: $id, name: $name, location: $location)';
  }
}

/// 停车场信息
class ParkingInfo {
  /// 停车场ID
  final String? parkingId;
  
  /// 总车位数
  final int? totalSpaces;
  
  /// 空余车位数
  final int? availableSpaces;
  
  /// 停车费（元/小时）
  final double? hourlyRate;
  
  /// 封顶价格（元/天）
  final double? dailyCap;
  
  /// 是否支持预约
  final bool supportsReservation;
  
  /// 入口坐标
  final LatLng? entrance;
  
  /// 楼层信息
  final List<ParkingFloor>? floors;

  ParkingInfo({
    this.parkingId,
    this.totalSpaces,
    this.availableSpaces,
    this.hourlyRate,
    this.dailyCap,
    this.supportsReservation = false,
    this.entrance,
    this.floors,
  });

  /// 是否有空位
  bool get hasAvailableSpace {
    return availableSpaces != null && availableSpaces! > 0;
  }

  /// 空位比例
  double? get availabilityRatio {
    if (totalSpaces == null || availableSpaces == null) return null;
    return availableSpaces! / totalSpaces!;
  }

  Map<String, dynamic> toJson() {
    return {
      'parkingId': parkingId,
      'totalSpaces': totalSpaces,
      'availableSpaces': availableSpaces,
      'hourlyRate': hourlyRate,
      'dailyCap': dailyCap,
      'supportsReservation': supportsReservation,
      'entrance': entrance != null
          ? {'lat': entrance!.latitude, 'lng': entrance!.longitude}
          : null,
      'floors': floors?.map((f) => f.toJson()).toList(),
    };
  }

  factory ParkingInfo.fromJson(Map<String, dynamic> json) {
    return ParkingInfo(
      parkingId: json['parkingId'],
      totalSpaces: json['totalSpaces'],
      availableSpaces: json['availableSpaces'],
      hourlyRate: json['hourlyRate']?.toDouble(),
      dailyCap: json['dailyCap']?.toDouble(),
      supportsReservation: json['supportsReservation'] ?? false,
      entrance: json['entrance'] != null
          ? LatLng(json['entrance']['lat'], json['entrance']['lng'])
          : null,
      floors: json['floors'] != null
          ? (json['floors'] as List)
              .map((f) => ParkingFloor.fromJson(f))
              .toList()
          : null,
    );
  }
}

/// 停车楼层
class ParkingFloor {
  /// 楼层编号
  final String floorNumber;
  
  /// 楼层名称
  final String? floorName;
  
  /// 总车位数
  final int totalSpaces;
  
  /// 空余车位数
  final int availableSpaces;
  
  /// 车位类型分布
  final Map<String, int>? spaceTypes;

  ParkingFloor({
    required this.floorNumber,
    this.floorName,
    required this.totalSpaces,
    required this.availableSpaces,
    this.spaceTypes,
  });

  Map<String, dynamic> toJson() {
    return {
      'floorNumber': floorNumber,
      'floorName': floorName,
      'totalSpaces': totalSpaces,
      'availableSpaces': availableSpaces,
      'spaceTypes': spaceTypes,
    };
  }

  factory ParkingFloor.fromJson(Map<String, dynamic> json) {
    return ParkingFloor(
      floorNumber: json['floorNumber'],
      floorName: json['floorName'],
      totalSpaces: json['totalSpaces'],
      availableSpaces: json['availableSpaces'],
      spaceTypes: json['spaceTypes'] != null
          ? Map<String, int>.from(json['spaceTypes'])
          : null,
    );
  }
}

/// 室内地图信息
class IndoorInfo {
  /// 建筑ID
  final String buildingId;
  
  /// 建筑名称
  final String buildingName;
  
  /// 楼层列表
  final List<IndoorFloor> floors;
  
  /// 入口列表
  final List<IndoorEntrance> entrances;
  
  /// 是否有室内定位
  final bool hasIndoorPositioning;

  IndoorInfo({
    required this.buildingId,
    required this.buildingName,
    required this.floors,
    required this.entrances,
    this.hasIndoorPositioning = false,
  });

  /// 获取指定楼层
  IndoorFloor? getFloor(String floorNumber) {
    try {
      return floors.firstWhere((f) => f.floorNumber == floorNumber);
    } catch (e) {
      return null;
    }
  }

  /// 获取最近的入口
  IndoorEntrance? getNearestEntrance(LatLng location) {
    if (entrances.isEmpty) return null;
    
    IndoorEntrance? nearest;
    double minDistance = double.infinity;
    
    for (final entrance in entrances) {
      final distance = _calculateDistance(
        location.latitude,
        location.longitude,
        entrance.location.latitude,
        entrance.location.longitude,
      );
      
      if (distance < minDistance) {
        minDistance = distance;
        nearest = entrance;
      }
    }
    
    return nearest;
  }

  Map<String, dynamic> toJson() {
    return {
      'buildingId': buildingId,
      'buildingName': buildingName,
      'floors': floors.map((f) => f.toJson()).toList(),
      'entrances': entrances.map((e) => e.toJson()).toList(),
      'hasIndoorPositioning': hasIndoorPositioning,
    };
  }

  factory IndoorInfo.fromJson(Map<String, dynamic> json) {
    return IndoorInfo(
      buildingId: json['buildingId'],
      buildingName: json['buildingName'],
      floors: (json['floors'] as List)
          .map((f) => IndoorFloor.fromJson(f))
          .toList(),
      entrances: (json['entrances'] as List)
          .map((e) => IndoorEntrance.fromJson(e))
          .toList(),
      hasIndoorPositioning: json['hasIndoorPositioning'] ?? false,
    );
  }

  double _calculateDistance(double lat1, double lng1, double lat2, double lng2) {
    const double earthRadius = 6371000; // 地球半径（米）
    
    final dLat = _toRadians(lat2 - lat1);
    final dLng = _toRadians(lng2 - lng1);
    
    final a = 
        (dLat / 2).sin() * (dLat / 2).sin() +
        _toRadians(lat1).cos() * _toRadians(lat2).cos() *
        (dLng / 2).sin() * (dLng / 2).sin();
    
    final c = 2 * a.sqrt().asin();
    
    return earthRadius * c;
  }

  double _toRadians(double degrees) => degrees * 3.141592653589793 / 180;
}

/// 室内楼层
class IndoorFloor {
  /// 楼层编号
  final String floorNumber;
  
  /// 楼层名称
  final String floorName;
  
  /// 楼层高度（米）
  final double? elevation;
  
  /// 地图ID
  final String? mapId;

  IndoorFloor({
    required this.floorNumber,
    required this.floorName,
    this.elevation,
    this.mapId,
  });

  Map<String, dynamic> toJson() {
    return {
      'floorNumber': floorNumber,
      'floorName': floorName,
      'elevation': elevation,
      'mapId': mapId,
    };
  }

  factory IndoorFloor.fromJson(Map<String, dynamic> json) {
    return IndoorFloor(
      floorNumber: json['floorNumber'],
      floorName: json['floorName'],
      elevation: json['elevation']?.toDouble(),
      mapId: json['mapId'],
    );
  }
}

/// 室内入口
class IndoorEntrance {
  /// 入口ID
  final String entranceId;
  
  /// 入口名称
  final String entranceName;
  
  /// 坐标位置
  final LatLng location;
  
  /// 连接的楼层
  final String floorNumber;
  
  /// 入口类型
  final EntranceType type;

  IndoorEntrance({
    required this.entranceId,
    required this.entranceName,
    required this.location,
    required this.floorNumber,
    this.type = EntranceType.main,
  });

  Map<String, dynamic> toJson() {
    return {
      'entranceId': entranceId,
      'entranceName': entranceName,
      'location': {'lat': location.latitude, 'lng': location.longitude},
      'floorNumber': floorNumber,
      'type': type.name,
    };
  }

  factory IndoorEntrance.fromJson(Map<String, dynamic> json) {
    return IndoorEntrance(
      entranceId: json['entranceId'],
      entranceName: json['entranceName'],
      location: LatLng(json['location']['lat'], json['location']['lng']),
      floorNumber: json['floorNumber'],
      type: EntranceType.values.byName(json['type']),
    );
  }
}

/// 入口类型
enum EntranceType {
  main,      // 主入口
  side,      // 侧入口
  parking,   // 停车场入口
  metro,     // 地铁连接
  other,     // 其他
}

/// POI搜索参数
class POISearchParams {
  /// 搜索关键词
  final String? keyword;
  
  /// 分类编码
  final String? categoryCode;
  
  /// 中心坐标
  final LatLng? center;
  
  /// 搜索半径（米）
  final double? radius;
  
  /// 搜索区域
  final String? city;
  
  /// 排序方式
  final POISortType sortType;
  
  /// 页码
  final int page;
  
  /// 每页数量
  final int pageSize;
  
  /// 筛选条件
  final Map<String, dynamic>? filters;

  POISearchParams({
    this.keyword,
    this.categoryCode,
    this.center,
    this.radius = 5000,
    this.city,
    this.sortType = POISortType.distance,
    this.page = 1,
    this.pageSize = 20,
    this.filters,
  });

  Map<String, dynamic> toQueryParams() {
    return {
      if (keyword != null) 'keyword': keyword,
      if (categoryCode != null) 'category': categoryCode,
      if (center != null) ...{
        'lat': center!.latitude,
        'lng': center!.longitude,
      },
      if (radius != null) 'radius': radius,
      if (city != null) 'city': city,
      'sort': sortType.name,
      'page': page,
      'pageSize': pageSize,
      if (filters != null) ...filters!,
    };
  }
}

/// POI排序类型
enum POISortType {
  distance,    // 距离
  rating,      // 评分
  popularity,  // 热度
  priceAsc,    // 价格从低到高
  priceDesc,   // 价格从高到低
}

/// POI搜索结果
class POISearchResult {
  /// POI列表
  final List<POIModel> pois;
  
  /// 总数量
  final int total;
  
  /// 当前页
  final int page;
  
  /// 是否有更多
  final bool hasMore;
  
  /// 搜索建议
  final List<String>? suggestions;

  POISearchResult({
    required this.pois,
    required this.total,
    required this.page,
    required this.hasMore,
    this.suggestions,
  });

  factory POISearchResult.empty() {
    return POISearchResult(
      pois: [],
      total: 0,
      page: 1,
      hasMore: false,
    );
  }
}

/// POI收藏夹
class POIFavorite {
  /// 收藏ID
  final String id;
  
  /// POI信息
  final POIModel poi;
  
  /// 收藏时间
  final DateTime favoritedAt;
  
  /// 备注
  final String? note;
  
  /// 分组名称
  final String? groupName;

  POIFavorite({
    required this.id,
    required this.poi,
    required this.favoritedAt,
    this.note,
    this.groupName,
  });

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'poi': poi.toJson(),
      'favoritedAt': favoritedAt.toIso8601String(),
      'note': note,
      'groupName': groupName,
    };
  }

  factory POIFavorite.fromJson(Map<String, dynamic> json) {
    return POIFavorite(
      id: json['id'],
      poi: POIModel.fromJson(json['poi']),
      favoritedAt: DateTime.parse(json['favoritedAt']),
      note: json['note'],
      groupName: json['groupName'],
    );
  }
}
