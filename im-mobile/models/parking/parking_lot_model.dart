import 'package:json_annotation/json_annotation.dart';

part 'parking_lot_model.g.dart';

/// 停车场模型
/// 存储停车场基本信息、位置、容量、价格等数据
@JsonSerializable()
class ParkingLotModel {
  /// 停车场ID
  final String id;

  /// 停车场名称
  final String name;

  /// 停车场编码
  final String code;

  /// 所属商户ID
  final String? merchantId;

  /// 停车场类型：1-路侧停车 2-室内停车场 3-露天停车场 4-立体车库 5-机械车库
  final int type;

  /// 经度（WGS84坐标系）
  final double longitude;

  /// 纬度（WGS84坐标系）
  final double latitude;

  /// 详细地址
  final String address;

  /// 省市区编码
  final String? areaCode;

  /// 总车位数
  final int totalSpaces;

  /// 可用车位数
  final int availableSpaces;

  /// 已占用车位数
  final int occupiedSpaces;

  /// 充电桩数量
  final int? chargingPiles;

  /// 营业状态：0-关闭 1-营业中 2-已满 3-维护中
  final int status;

  /// 是否24小时营业
  final bool? isOpen24Hours;

  /// 基础价格（首小时）
  final double? basePrice;

  /// 计费单位价格
  final double? unitPrice;

  /// 计费单位时长（分钟）
  final int? unitDuration;

  /// 每日封顶价格
  final double? dailyCap;

  /// 夜间价格
  final double? nightPrice;

  /// 免费时长（分钟）
  final int? freeDuration;

  /// 是否支持无感支付
  final bool? supportsContactlessPayment;

  /// 支持的支付方式列表
  final List<String>? supportedPaymentMethods;

  /// 停车场入口坐标列表
  final List<Map<String, double>>? entranceCoordinates;

  /// 停车场出口坐标列表
  final List<Map<String, double>>? exitCoordinates;

  /// 楼层信息
  final String? floorInfo;

  /// 是否有室内导航
  final bool? hasIndoorNavigation;

  /// 是否支持预约停车
  final bool? supportsReservation;

  /// 是否支持共享停车
  final bool? supportsSharing;

  /// 停车场图片URL列表
  final List<String>? images;

  /// 停车场设施列表
  final List<String>? facilities;

  /// 评分（1-5分）
  final double? rating;

  /// 评分人数
  final int? ratingCount;

  /// 今日停车人次
  final int? todayParkingCount;

  /// 平均停车时长（分钟）
  final int? avgParkingDuration;

  /// 是否推荐
  final bool? isRecommended;

  /// 创建时间
  final DateTime? createTime;

  /// 更新时间
  final DateTime? updateTime;

  ParkingLotModel({
    required this.id,
    required this.name,
    required this.code,
    this.merchantId,
    required this.type,
    required this.longitude,
    required this.latitude,
    required this.address,
    this.areaCode,
    required this.totalSpaces,
    required this.availableSpaces,
    required this.occupiedSpaces,
    this.chargingPiles,
    required this.status,
    this.isOpen24Hours,
    this.basePrice,
    this.unitPrice,
    this.unitDuration,
    this.dailyCap,
    this.nightPrice,
    this.freeDuration,
    this.supportsContactlessPayment,
    this.supportedPaymentMethods,
    this.entranceCoordinates,
    this.exitCoordinates,
    this.floorInfo,
    this.hasIndoorNavigation,
    this.supportsReservation,
    this.supportsSharing,
    this.images,
    this.facilities,
    this.rating,
    this.ratingCount,
    this.todayParkingCount,
    this.avgParkingDuration,
    this.isRecommended,
    this.createTime,
    this.updateTime,
  });

  factory ParkingLotModel.fromJson(Map<String, dynamic> json) =>
      _$ParkingLotModelFromJson(json);

  Map<String, dynamic> toJson() => _$ParkingLotModelToJson(this);

  /// 获取空置率
  double get vacancyRate {
    if (totalSpaces == 0) return 0.0;
    return availableSpaces / totalSpaces;
  }

  /// 是否营业中
  bool get isOpen {
    return status == 1;
  }

  /// 是否有空位
  bool get hasAvailableSpace {
    return availableSpaces > 0;
  }

  /// 计算预计停车费用
  double calculateEstimatedFee(int durationMinutes) {
    if (basePrice == null) return 0.0;

    final free = freeDuration ?? 0;
    if (durationMinutes <= free) return 0.0;

    int chargeMinutes = durationMinutes - free;
    double totalFee = basePrice!;

    if (chargeMinutes > 60 && unitPrice != null && unitDuration != null) {
      int extraUnits = ((chargeMinutes - 60) / unitDuration!).ceil();
      totalFee += unitPrice! * extraUnits;
    }

    // 每日封顶
    if (dailyCap != null && totalFee > dailyCap!) {
      totalFee = dailyCap!;
    }

    return totalFee;
  }

  /// 获取停车场类型文本
  String get typeText {
    switch (type) {
      case 1:
        return '路侧停车';
      case 2:
        return '室内停车场';
      case 3:
        return '露天停车场';
      case 4:
        return '立体车库';
      case 5:
        return '机械车库';
      default:
        return '其他';
    }
  }

  /// 获取状态文本
  String get statusText {
    switch (status) {
      case 0:
        return '关闭';
      case 1:
        return '营业中';
      case 2:
        return '已满';
      case 3:
        return '维护中';
      default:
        return '未知';
    }
    }

  /// 格式化价格显示
  String get formattedPrice {
    if (basePrice == null) return '价格未知';
    return '¥${basePrice!.toStringAsFixed(2)}/小时';
  }

  /// 获取空置率文本
  String get vacancyRateText {
    final rate = (vacancyRate * 100).toInt();
    if (rate > 50) return '空位充足';
    if (rate > 20) return '空位紧张';
    return '即将满位';
  }

  /// 获取空置率颜色
  String get vacancyRateColor {
    final rate = vacancyRate;
    if (rate > 0.5) return '#4CAF50'; // 绿色
    if (rate > 0.2) return '#FF9800'; // 橙色
    return '#F44336'; // 红色
  }

  /// 计算距离评分
  int calculateDistanceScore(double distance) {
    if (distance <= 100) return 100;
    if (distance <= 500) return 80;
    if (distance <= 1000) return 60;
    if (distance <= 2000) return 40;
    return 20;
  }

  /// 计算综合推荐分数
  int calculateRecommendScore(double distance) {
    int distanceScore = calculateDistanceScore(distance);
    int priceScore = basePrice != null && basePrice! <= 10 ? 100 : basePrice! <= 20 ? 60 : 30;
    int vacancyScore = (vacancyRate * 100).toInt();
    int ratingScore = rating != null ? (rating! * 20).toInt() : 50;

    // 加权计算
    return (distanceScore * 0.4 + priceScore * 0.2 + vacancyScore * 0.2 + ratingScore * 0.2).round();
  }
}

/// 附近停车场模型（带距离信息）
@JsonSerializable()
class NearbyParkingLotModel extends ParkingLotModel {
  /// 距离（米）
  final double distance;

  /// 距离文本
  final String distanceText;

  /// 步行时间（分钟）
  final int? walkTime;

  /// 驾车时间（分钟）
  final int? driveTime;

  NearbyParkingLotModel({
    required String id,
    required String name,
    required String code,
    String? merchantId,
    required int type,
    required double longitude,
    required double latitude,
    required String address,
    String? areaCode,
    required int totalSpaces,
    required int availableSpaces,
    required int occupiedSpaces,
    int? chargingPiles,
    required int status,
    bool? isOpen24Hours,
    double? basePrice,
    double? unitPrice,
    int? unitDuration,
    double? dailyCap,
    double? nightPrice,
    int? freeDuration,
    bool? supportsContactlessPayment,
    List<String>? supportedPaymentMethods,
    List<Map<String, double>>? entranceCoordinates,
    List<Map<String, double>>? exitCoordinates,
    String? floorInfo,
    bool? hasIndoorNavigation,
    bool? supportsReservation,
    bool? supportsSharing,
    List<String>? images,
    List<String>? facilities,
    double? rating,
    int? ratingCount,
    int? todayParkingCount,
    int? avgParkingDuration,
    bool? isRecommended,
    DateTime? createTime,
    DateTime? updateTime,
    required this.distance,
    required this.distanceText,
    this.walkTime,
    this.driveTime,
  }) : super(
          id: id,
          name: name,
          code: code,
          merchantId: merchantId,
          type: type,
          longitude: longitude,
          latitude: latitude,
          address: address,
          areaCode: areaCode,
          totalSpaces: totalSpaces,
          availableSpaces: availableSpaces,
          occupiedSpaces: occupiedSpaces,
          chargingPiles: chargingPiles,
          status: status,
          isOpen24Hours: isOpen24Hours,
          basePrice: basePrice,
          unitPrice: unitPrice,
          unitDuration: unitDuration,
          dailyCap: dailyCap,
          nightPrice: nightPrice,
          freeDuration: freeDuration,
          supportsContactlessPayment: supportsContactlessPayment,
          supportedPaymentMethods: supportedPaymentMethods,
          entranceCoordinates: entranceCoordinates,
          exitCoordinates: exitCoordinates,
          floorInfo: floorInfo,
          hasIndoorNavigation: hasIndoorNavigation,
          supportsReservation: supportsReservation,
          supportsSharing: supportsSharing,
          images: images,
          facilities: facilities,
          rating: rating,
          ratingCount: ratingCount,
          todayParkingCount: todayParkingCount,
          avgParkingDuration: avgParkingDuration,
          isRecommended: isRecommended,
          createTime: createTime,
          updateTime: updateTime,
        );

  factory NearbyParkingLotModel.fromJson(Map<String, dynamic> json) =>
      _$NearbyParkingLotModelFromJson(json);

  @override
  Map<String, dynamic> toJson() => _$NearbyParkingLotModelToJson(this);
}

/// 推荐停车场模型
@JsonSerializable()
class RecommendParkingLotModel extends NearbyParkingLotModel {
  /// 推荐分数
  final int recommendScore;

  /// 推荐理由
  final String recommendReason;

  RecommendParkingLotModel({
    required String id,
    required String name,
    required String code,
    String? merchantId,
    required int type,
    required double longitude,
    required double latitude,
    required String address,
    String? areaCode,
    required int totalSpaces,
    required int availableSpaces,
    required int occupiedSpaces,
    int? chargingPiles,
    required int status,
    bool? isOpen24Hours,
    double? basePrice,
    double? unitPrice,
    int? unitDuration,
    double? dailyCap,
    double? nightPrice,
    int? freeDuration,
    bool? supportsContactlessPayment,
    List<String>? supportedPaymentMethods,
    List<Map<String, double>>? entranceCoordinates,
    List<Map<String, double>>? exitCoordinates,
    String? floorInfo,
    bool? hasIndoorNavigation,
    bool? supportsReservation,
    bool? supportsSharing,
    List<String>? images,
    List<String>? facilities,
    double? rating,
    int? ratingCount,
    int? todayParkingCount,
    int? avgParkingDuration,
    bool? isRecommended,
    DateTime? createTime,
    DateTime? updateTime,
    required double distance,
    required String distanceText,
    int? walkTime,
    int? driveTime,
    required this.recommendScore,
    required this.recommendReason,
  }) : super(
          id: id,
          name: name,
          code: code,
          merchantId: merchantId,
          type: type,
          longitude: longitude,
          latitude: latitude,
          address: address,
          areaCode: areaCode,
          totalSpaces: totalSpaces,
          availableSpaces: availableSpaces,
          occupiedSpaces: occupiedSpaces,
          chargingPiles: chargingPiles,
          status: status,
          isOpen24Hours: isOpen24Hours,
          basePrice: basePrice,
          unitPrice: unitPrice,
          unitDuration: unitDuration,
          dailyCap: dailyCap,
          nightPrice: nightPrice,
          freeDuration: freeDuration,
          supportsContactlessPayment: supportsContactlessPayment,
          supportedPaymentMethods: supportedPaymentMethods,
          entranceCoordinates: entranceCoordinates,
          exitCoordinates: exitCoordinates,
          floorInfo: floorInfo,
          hasIndoorNavigation: hasIndoorNavigation,
          supportsReservation: supportsReservation,
          supportsSharing: supportsSharing,
          images: images,
          facilities: facilities,
          rating: rating,
          ratingCount: ratingCount,
          todayParkingCount: todayParkingCount,
          avgParkingDuration: avgParkingDuration,
          isRecommended: isRecommended,
          createTime: createTime,
          updateTime: updateTime,
          distance: distance,
          distanceText: distanceText,
          walkTime: walkTime,
          driveTime: driveTime,
        );

  factory RecommendParkingLotModel.fromJson(Map<String, dynamic> json) =>
      _$RecommendParkingLotModelFromJson(json);

  @override
  Map<String, dynamic> toJson() => _$RecommendParkingLotModelToJson(this);
}
