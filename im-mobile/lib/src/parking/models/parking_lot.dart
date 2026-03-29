import 'dart:convert';

/// 停车场数据模型
class ParkingLot {
  /// 停车场ID
  final String id;
  
  /// 停车场名称
  final String name;
  
  /// 地址
  final String address;
  
  /// 纬度
  final double latitude;
  
  /// 经度
  final double longitude;
  
  /// 围栏半径（米）
  final double? radius;
  
  /// 总车位数
  final int totalSpots;
  
  /// 空余车位数
  final int availableSpots;
  
  /// 车位更新时间
  final DateTime? availabilityUpdatedAt;
  
  /// 停车场类型
  final ParkingLotType type;
  
  /// 收费标准
  final List<ParkingRate> rates;
  
  /// 是否支持预约
  final bool supportsReservation;
  
  /// 是否支持充电桩
  final bool hasEVCharging;
  
  /// 充电桩数量
  final int? evChargingSpots;
  
  /// 营业时间
  final BusinessHours? businessHours;
  
  /// 楼层信息
  final List<ParkingFloor>? floors;
  
  /// 图片URL列表
  final List<String>? photos;
  
  /// 联系电话
  final String? phone;
  
  /// 是否支持无感支付
  final bool supportsAutoPayment;
  
  /// 支持的支付方式
  final List<String>? supportedPayments;
  
  /// 评分
  final double? rating;
  
  /// 用户评价数
  final int? reviewCount;
  
  /// 标签
  final List<String>? tags;
  
  /// 距离（搜索时填充）
  final double? distance;
  
  /// 预计步行时间（分钟）
  final int? walkingTime;

  ParkingLot({
    required this.id,
    required this.name,
    required this.address,
    required this.latitude,
    required this.longitude,
    this.radius,
    required this.totalSpots,
    required this.availableSpots,
    this.availabilityUpdatedAt,
    this.type = ParkingLotType.public,
    required this.rates,
    this.supportsReservation = false,
    this.hasEVCharging = false,
    this.evChargingSpots,
    this.businessHours,
    this.floors,
    this.photos,
    this.phone,
    this.supportsAutoPayment = false,
    this.supportedPayments,
    this.rating,
    this.reviewCount,
    this.tags,
    this.distance,
    this.walkingTime,
  });

  /// 从JSON创建
  factory ParkingLot.fromJson(Map<String, dynamic> json) {
    return ParkingLot(
      id: json['id'] as String,
      name: json['name'] as String,
      address: json['address'] as String,
      latitude: (json['latitude'] as num).toDouble(),
      longitude: (json['longitude'] as num).toDouble(),
      radius: json['radius'] != null ? (json['radius'] as num).toDouble() : null,
      totalSpots: json['totalSpots'] as int,
      availableSpots: json['availableSpots'] as int,
      availabilityUpdatedAt: json['availabilityUpdatedAt'] != null
          ? DateTime.parse(json['availabilityUpdatedAt'] as String)
          : null,
      type: ParkingLotType.values.firstWhere(
        (e) => e.name == json['type'],
        orElse: () => ParkingLotType.public,
      ),
      rates: (json['rates'] as List?)
              ?.map((r) => ParkingRate.fromJson(r as Map<String, dynamic>))
              .toList() ??
          [],
      supportsReservation: json['supportsReservation'] as bool? ?? false,
      hasEVCharging: json['hasEVCharging'] as bool? ?? false,
      evChargingSpots: json['evChargingSpots'] as int?,
      businessHours: json['businessHours'] != null
          ? BusinessHours.fromJson(json['businessHours'] as Map<String, dynamic>)
          : null,
      floors: (json['floors'] as List?)
          ?.map((f) => ParkingFloor.fromJson(f as Map<String, dynamic>))
          .toList(),
      photos: (json['photos'] as List?)?.cast<String>(),
      phone: json['phone'] as String?,
      supportsAutoPayment: json['supportsAutoPayment'] as bool? ?? false,
      supportedPayments: (json['supportedPayments'] as List?)?.cast<String>(),
      rating: json['rating'] != null ? (json['rating'] as num).toDouble() : null,
      reviewCount: json['reviewCount'] as int?,
      tags: (json['tags'] as List?)?.cast<String>(),
      distance: json['distance'] != null ? (json['distance'] as num).toDouble() : null,
      walkingTime: json['walkingTime'] as int?,
    );
  }

  /// 转换为JSON
  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'name': name,
      'address': address,
      'latitude': latitude,
      'longitude': longitude,
      'radius': radius,
      'totalSpots': totalSpots,
      'availableSpots': availableSpots,
      'availabilityUpdatedAt': availabilityUpdatedAt?.toIso8601String(),
      'type': type.name,
      'rates': rates.map((r) => r.toJson()).toList(),
      'supportsReservation': supportsReservation,
      'hasEVCharging': hasEVCharging,
      'evChargingSpots': evChargingSpots,
      'businessHours': businessHours?.toJson(),
      'floors': floors?.map((f) => f.toJson()).toList(),
      'photos': photos,
      'phone': phone,
      'supportsAutoPayment': supportsAutoPayment,
      'supportedPayments': supportedPayments,
      'rating': rating,
      'reviewCount': reviewCount,
      'tags': tags,
      'distance': distance,
      'walkingTime': walkingTime,
    };
  }

  /// 复制并修改
  ParkingLot copyWith({
    String? id,
    String? name,
    String? address,
    double? latitude,
    double? longitude,
    double? radius,
    int? totalSpots,
    int? availableSpots,
    DateTime? availabilityUpdatedAt,
    ParkingLotType? type,
    List<ParkingRate>? rates,
    bool? supportsReservation,
    bool? hasEVCharging,
    int? evChargingSpots,
    BusinessHours? businessHours,
    List<ParkingFloor>? floors,
    List<String>? photos,
    String? phone,
    bool? supportsAutoPayment,
    List<String>? supportedPayments,
    double? rating,
    int? reviewCount,
    List<String>? tags,
    double? distance,
    int? walkingTime,
  }) {
    return ParkingLot(
      id: id ?? this.id,
      name: name ?? this.name,
      address: address ?? this.address,
      latitude: latitude ?? this.latitude,
      longitude: longitude ?? this.longitude,
      radius: radius ?? this.radius,
      totalSpots: totalSpots ?? this.totalSpots,
      availableSpots: availableSpots ?? this.availableSpots,
      availabilityUpdatedAt: availabilityUpdatedAt ?? this.availabilityUpdatedAt,
      type: type ?? this.type,
      rates: rates ?? this.rates,
      supportsReservation: supportsReservation ?? this.supportsReservation,
      hasEVCharging: hasEVCharging ?? this.hasEVCharging,
      evChargingSpots: evChargingSpots ?? this.evChargingSpots,
      businessHours: businessHours ?? this.businessHours,
      floors: floors ?? this.floors,
      photos: photos ?? this.photos,
      phone: phone ?? this.phone,
      supportsAutoPayment: supportsAutoPayment ?? this.supportsAutoPayment,
      supportedPayments: supportedPayments ?? this.supportedPayments,
      rating: rating ?? this.rating,
      reviewCount: reviewCount ?? this.reviewCount,
      tags: tags ?? this.tags,
      distance: distance ?? this.distance,
      walkingTime: walkingTime ?? this.walkingTime,
    );
  }

  /// 获取空位率
  double get occupancyRate {
    if (totalSpots == 0) return 0;
    return (totalSpots - availableSpots) / totalSpots;
  }

  /// 获取空位状态文本
  String get availabilityText {
    if (availableSpots == 0) return '已满';
    if (availableSpots < 10) return '紧张';
    if (availableSpots < 50) return '较少';
    return '充足';
  }

  /// 获取空位状态颜色
  String get availabilityColor {
    if (availableSpots == 0) return '#FF0000';
    if (availableSpots < 10) return '#FF6600';
    if (availableSpots < 50) return '#FFCC00';
    return '#00CC00';
  }

  @override
  String toString() {
    return 'ParkingLot(id: $id, name: $name, available: $availableSpots/$totalSpots)';
  }
}

/// 停车场类型
enum ParkingLotType {
  /// 公共停车场
  public,
  
  /// 商业停车场
  commercial,
  
  /// 住宅小区
  residential,
  
  /// 写字楼
  office,
  
  /// 路边停车
  onStreet,
  
  /// 共享停车
  shared,
}

/// 停车费率
class ParkingRate {
  /// 计费时段开始（分钟，从0点开始）
  final int startMinutes;
  
  /// 计费时段结束（分钟）
  final int endMinutes;
  
  /// 单价（元/小时）
  final double hourlyRate;
  
  /// 最高限价（元）
  final double? maxPrice;
  
  /// 免费时长（分钟）
  final int? freeMinutes;
  
  /// 是否按次计费
  final bool perEntry;
  
  /// 单次费用（按次计费时）
  final double? perEntryPrice;

  ParkingRate({
    required this.startMinutes,
    required this.endMinutes,
    required this.hourlyRate,
    this.maxPrice,
    this.freeMinutes,
    this.perEntry = false,
    this.perEntryPrice,
  });

  factory ParkingRate.fromJson(Map<String, dynamic> json) {
    return ParkingRate(
      startMinutes: json['startMinutes'] as int,
      endMinutes: json['endMinutes'] as int,
      hourlyRate: (json['hourlyRate'] as num).toDouble(),
      maxPrice: json['maxPrice'] != null ? (json['maxPrice'] as num).toDouble() : null,
      freeMinutes: json['freeMinutes'] as int?,
      perEntry: json['perEntry'] as bool? ?? false,
      perEntryPrice: json['perEntryPrice'] != null
          ? (json['perEntryPrice'] as num).toDouble()
          : null,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'startMinutes': startMinutes,
      'endMinutes': endMinutes,
      'hourlyRate': hourlyRate,
      'maxPrice': maxPrice,
      'freeMinutes': freeMinutes,
      'perEntry': perEntry,
      'perEntryPrice': perEntryPrice,
    };
  }

  /// 获取时间段描述
  String get timeRangeText {
    final startHour = startMinutes ~/ 60;
    final startMin = startMinutes % 60;
    final endHour = endMinutes ~/ 60;
    final endMin = endMinutes % 60;
    
    return '${startHour.toString().padLeft(2, '0')}:${startMin.toString().padLeft(2, '0')} - '
           '${endHour.toString().padLeft(2, '0')}:${endMin.toString().padLeft(2, '0')}';
  }
}

/// 营业时间
class BusinessHours {
  /// 是否24小时营业
  final bool is24Hours;
  
  /// 每日时段
  final List<DailyHours>? dailyHours;

  BusinessHours({
    this.is24Hours = false,
    this.dailyHours,
  });

  factory BusinessHours.fromJson(Map<String, dynamic> json) {
    return BusinessHours(
      is24Hours: json['is24Hours'] as bool? ?? false,
      dailyHours: (json['dailyHours'] as List?)
          ?.map((d) => DailyHours.fromJson(d as Map<String, dynamic>))
          .toList(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'is24Hours': is24Hours,
      'dailyHours': dailyHours?.map((d) => d.toJson()).toList(),
    };
  }

  /// 检查是否营业中
  bool isOpen([DateTime? dateTime]) {
    if (is24Hours) return true;
    
    final dt = dateTime ?? DateTime.now();
    final dayOfWeek = dt.weekday;
    
    final todayHours = dailyHours?.firstWhere(
      (d) => d.dayOfWeek == dayOfWeek,
      orElse: () => DailyHours(dayOfWeek: dayOfWeek, isOpen: false),
    );
    
    if (todayHours == null || !todayHours.isOpen) return false;
    
    final currentMinutes = dt.hour * 60 + dt.minute;
    return currentMinutes >= todayHours.openMinutes &&
           currentMinutes < todayHours.closeMinutes;
  }
}

/// 每日营业时间
class DailyHours {
  /// 星期几（1=周一，7=周日）
  final int dayOfWeek;
  
  /// 是否营业
  final bool isOpen;
  
  /// 开门时间（分钟）
  final int openMinutes;
  
  /// 关门时间（分钟）
  final int closeMinutes;

  DailyHours({
    required this.dayOfWeek,
    required this.isOpen,
    this.openMinutes = 0,
    this.closeMinutes = 0,
  });

  factory DailyHours.fromJson(Map<String, dynamic> json) {
    return DailyHours(
      dayOfWeek: json['dayOfWeek'] as int,
      isOpen: json['isOpen'] as bool? ?? true,
      openMinutes: json['openMinutes'] as int? ?? 0,
      closeMinutes: json['closeMinutes'] as int? ?? 0,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'dayOfWeek': dayOfWeek,
      'isOpen': isOpen,
      'openMinutes': openMinutes,
      'closeMinutes': closeMinutes,
    };
  }
}

/// 停车场楼层
class ParkingFloor {
  /// 楼层名称
  final String name;
  
  /// 楼层编号（负数为地下）
  final int level;
  
  /// 总车位数
  final int totalSpots;
  
  /// 空余车位数
  final int availableSpots;
  
  /// 区域列表
  final List<ParkingArea>? areas;

  ParkingFloor({
    required this.name,
    required this.level,
    required this.totalSpots,
    required this.availableSpots,
    this.areas,
  });

  factory ParkingFloor.fromJson(Map<String, dynamic> json) {
    return ParkingFloor(
      name: json['name'] as String,
      level: json['level'] as int,
      totalSpots: json['totalSpots'] as int,
      availableSpots: json['availableSpots'] as int,
      areas: (json['areas'] as List?)
          ?.map((a) => ParkingArea.fromJson(a as Map<String, dynamic>))
          .toList(),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'name': name,
      'level': level,
      'totalSpots': totalSpots,
      'availableSpots': availableSpots,
      'areas': areas?.map((a) => a.toJson()).toList(),
    };
  }
}

/// 停车区域
class ParkingArea {
  /// 区域标识
  final String code;
  
  /// 区域名称
  final String name;
  
  /// 总车位数
  final int totalSpots;
  
  /// 空余车位数
  final int availableSpots;

  ParkingArea({
    required this.code,
    required this.name,
    required this.totalSpots,
    required this.availableSpots,
  });

  factory ParkingArea.fromJson(Map<String, dynamic> json) {
    return ParkingArea(
      code: json['code'] as String,
      name: json['name'] as String,
      totalSpots: json['totalSpots'] as int,
      availableSpots: json['availableSpots'] as int,
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'code': code,
      'name': name,
      'totalSpots': totalSpots,
      'availableSpots': availableSpots,
    };
  }
}
