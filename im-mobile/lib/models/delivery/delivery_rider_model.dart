/// 骑手模型类 - 即时配送运力调度系统
/// 用于移动端展示骑手信息和状态
class DeliveryRiderModel {
  final int id;
  final int userId;
  final String name;
  final String phone;
  final String? avatar;
  final String workStatus;
  final String level;
  final double? longitude;
  final double? latitude;
  final DateTime? locationUpdatedAt;
  final int? currentZoneId;
  final int activeOrderCount;
  final int todayCompletedCount;
  final double todayIncome;
  final double rating;
  final int totalDeliveries;
  final double onTimeRate;
  final int? stationId;
  final String? vehicleType;
  final String? vehiclePlate;
  final DateTime? workStartTime;
  final String verifyStatus;
  final DateTime? healthCertExpire;
  final String? emergencyContact;
  final String? emergencyPhone;
  final DateTime createdAt;
  final DateTime updatedAt;

  DeliveryRiderModel({
    required this.id,
    required this.userId,
    required this.name,
    required this.phone,
    this.avatar,
    required this.workStatus,
    required this.level,
    this.longitude,
    this.latitude,
    this.locationUpdatedAt,
    this.currentZoneId,
    this.activeOrderCount = 0,
    this.todayCompletedCount = 0,
    this.todayIncome = 0.0,
    this.rating = 5.0,
    this.totalDeliveries = 0,
    this.onTimeRate = 1.0,
    this.stationId,
    this.vehicleType,
    this.vehiclePlate,
    this.workStartTime,
    required this.verifyStatus,
    this.healthCertExpire,
    this.emergencyContact,
    this.emergencyPhone,
    required this.createdAt,
    required this.updatedAt,
  });

  factory DeliveryRiderModel.fromJson(Map<String, dynamic> json) {
    return DeliveryRiderModel(
      id: json['id'],
      userId: json['userId'],
      name: json['name'],
      phone: json['phone'],
      avatar: json['avatar'],
      workStatus: json['workStatus'] ?? 'OFFLINE',
      level: json['level'] ?? 'BRONZE',
      longitude: json['longitude']?.toDouble(),
      latitude: json['latitude']?.toDouble(),
      locationUpdatedAt: json['locationUpdatedAt'] != null
          ? DateTime.parse(json['locationUpdatedAt'])
          : null,
      currentZoneId: json['currentZoneId'],
      activeOrderCount: json['activeOrderCount'] ?? 0,
      todayCompletedCount: json['todayCompletedCount'] ?? 0,
      todayIncome: json['todayIncome']?.toDouble() ?? 0.0,
      rating: json['rating']?.toDouble() ?? 5.0,
      totalDeliveries: json['totalDeliveries'] ?? 0,
      onTimeRate: json['onTimeRate']?.toDouble() ?? 1.0,
      stationId: json['stationId'],
      vehicleType: json['vehicleType'],
      vehiclePlate: json['vehiclePlate'],
      workStartTime: json['workStartTime'] != null
          ? DateTime.parse(json['workStartTime'])
          : null,
      verifyStatus: json['verifyStatus'] ?? 'PENDING',
      healthCertExpire: json['healthCertExpire'] != null
          ? DateTime.parse(json['healthCertExpire'])
          : null,
      emergencyContact: json['emergencyContact'],
      emergencyPhone: json['emergencyPhone'],
      createdAt: DateTime.parse(json['createdAt']),
      updatedAt: DateTime.parse(json['updatedAt']),
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'userId': userId,
      'name': name,
      'phone': phone,
      'avatar': avatar,
      'workStatus': workStatus,
      'level': level,
      'longitude': longitude,
      'latitude': latitude,
      'locationUpdatedAt': locationUpdatedAt?.toIso8601String(),
      'currentZoneId': currentZoneId,
      'activeOrderCount': activeOrderCount,
      'todayCompletedCount': todayCompletedCount,
      'todayIncome': todayIncome,
      'rating': rating,
      'totalDeliveries': totalDeliveries,
      'onTimeRate': onTimeRate,
      'stationId': stationId,
      'vehicleType': vehicleType,
      'vehiclePlate': vehiclePlate,
      'workStartTime': workStartTime?.toIso8601String(),
      'verifyStatus': verifyStatus,
      'healthCertExpire': healthCertExpire?.toIso8601String(),
      'emergencyContact': emergencyContact,
      'emergencyPhone': emergencyPhone,
      'createdAt': createdAt.toIso8601String(),
      'updatedAt': updatedAt.toIso8601String(),
    };
  }

  /// 获取工作状态显示文本
  String get workStatusText {
    switch (workStatus) {
      case 'IDLE':
        return '空闲';
      case 'BUSY':
        return '配送中';
      case 'OFFLINE':
        return '离线';
      case 'REST':
        return '休息';
      default:
        return '未知';
    }
  }

  /// 获取等级显示文本
  String get levelText {
    switch (level) {
      case 'DIAMOND':
        return '钻石骑手';
      case 'PLATINUM':
        return '铂金骑手';
      case 'GOLD':
        return '金牌骑手';
      case 'SILVER':
        return '银牌骑手';
      case 'BRONZE':
        return '铜牌骑手';
      default:
        return '普通骑手';
    }
  }

  /// 检查是否可接单
  bool get isAvailable => workStatus == 'IDLE' && activeOrderCount < maxOrderLimit;

  /// 获取最大接单限制
  int get maxOrderLimit {
    switch (level) {
      case 'DIAMOND':
        return 8;
      case 'PLATINUM':
        return 6;
      case 'GOLD':
        return 5;
      case 'SILVER':
        return 4;
      default:
        return 3;
    }
  }

  /// 复制并修改
  DeliveryRiderModel copyWith({
    int? id,
    int? userId,
    String? name,
    String? phone,
    String? avatar,
    String? workStatus,
    String? level,
    double? longitude,
    double? latitude,
    DateTime? locationUpdatedAt,
    int? currentZoneId,
    int? activeOrderCount,
    int? todayCompletedCount,
    double? todayIncome,
    double? rating,
    int? totalDeliveries,
    double? onTimeRate,
    int? stationId,
    String? vehicleType,
    String? vehiclePlate,
    DateTime? workStartTime,
    String? verifyStatus,
    DateTime? healthCertExpire,
    String? emergencyContact,
    String? emergencyPhone,
    DateTime? createdAt,
    DateTime? updatedAt,
  }) {
    return DeliveryRiderModel(
      id: id ?? this.id,
      userId: userId ?? this.userId,
      name: name ?? this.name,
      phone: phone ?? this.phone,
      avatar: avatar ?? this.avatar,
      workStatus: workStatus ?? this.workStatus,
      level: level ?? this.level,
      longitude: longitude ?? this.longitude,
      latitude: latitude ?? this.latitude,
      locationUpdatedAt: locationUpdatedAt ?? this.locationUpdatedAt,
      currentZoneId: currentZoneId ?? this.currentZoneId,
      activeOrderCount: activeOrderCount ?? this.activeOrderCount,
      todayCompletedCount: todayCompletedCount ?? this.todayCompletedCount,
      todayIncome: todayIncome ?? this.todayIncome,
      rating: rating ?? this.rating,
      totalDeliveries: totalDeliveries ?? this.totalDeliveries,
      onTimeRate: onTimeRate ?? this.onTimeRate,
      stationId: stationId ?? this.stationId,
      vehicleType: vehicleType ?? this.vehicleType,
      vehiclePlate: vehiclePlate ?? this.vehiclePlate,
      workStartTime: workStartTime ?? this.workStartTime,
      verifyStatus: verifyStatus ?? this.verifyStatus,
      healthCertExpire: healthCertExpire ?? this.healthCertExpire,
      emergencyContact: emergencyContact ?? this.emergencyContact,
      emergencyPhone: emergencyPhone ?? this.emergencyPhone,
      createdAt: createdAt ?? this.createdAt,
      updatedAt: updatedAt ?? this.updatedAt,
    );
  }
}

/// 骑手等级枚举
enum RiderLevel {
  bronze('BRONZE', '铜牌', 0xFFCD7F32),
  silver('SILVER', '银牌', 0xFFC0C0C0),
  gold('GOLD', '金牌', 0xFFFFD700),
  platinum('PLATINUM', '铂金', 0xFFE5E4E2),
  diamond('DIAMOND', '钻石', 0xFFB9F2FF);

  final String code;
  final String name;
  final int color;

  const RiderLevel(this.code, this.name, this.color);

  static RiderLevel fromCode(String code) {
    return RiderLevel.values.firstWhere(
      (e) => e.code == code,
      orElse: () => RiderLevel.bronze,
    );
  }
}

/// 骑手工作状态枚举
enum RiderWorkStatus {
  idle('IDLE', '空闲', 0xFF4CAF50),
  busy('BUSY', '配送中', 0xFFFF9800),
  offline('OFFLINE', '离线', 0xFF9E9E9E),
  rest('REST', '休息', 0xFF2196F3);

  final String code;
  final String name;
  final int color;

  const RiderWorkStatus(this.code, this.name, this.color);

  static RiderWorkStatus fromCode(String code) {
    return RiderWorkStatus.values.firstWhere(
      (e) => e.code == code,
      orElse: () => RiderWorkStatus.offline,
    );
  }
}
