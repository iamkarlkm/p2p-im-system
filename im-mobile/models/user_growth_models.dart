// 用户成长与会员权益系统 - Flutter模型定义
// 功能: 本地生活用户成长与会员权益体系模块

/// 用户等级定义
class UserLevelDefinition {
  final int? id;
  final String? levelCode;
  final String? levelName;
  final String? levelIcon;
  final String? levelColor;
  final int? levelOrder;
  final int? minGrowthValue;
  final int? maxGrowthValue;
  final List<LevelPrivilege>? privileges;
  final bool? enabled;

  UserLevelDefinition({
    this.id,
    this.levelCode,
    this.levelName,
    this.levelIcon,
    this.levelColor,
    this.levelOrder,
    this.minGrowthValue,
    this.maxGrowthValue,
    this.privileges,
    this.enabled,
  });

  factory UserLevelDefinition.fromJson(Map<String, dynamic> json) {
    return UserLevelDefinition(
      id: json['id'],
      levelCode: json['levelCode'],
      levelName: json['levelName'],
      levelIcon: json['levelIcon'],
      levelColor: json['levelColor'],
      levelOrder: json['levelOrder'],
      minGrowthValue: json['minGrowthValue'],
      maxGrowthValue: json['maxGrowthValue'],
      privileges: json['privileges'] != null
          ? (json['privileges'] as List)
              .map((e) => LevelPrivilege.fromJson(e))
              .toList()
          : null,
      enabled: json['enabled'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'id': id,
      'levelCode': levelCode,
      'levelName': levelName,
      'levelIcon': levelIcon,
      'levelColor': levelColor,
      'levelOrder': levelOrder,
      'minGrowthValue': minGrowthValue,
      'maxGrowthValue': maxGrowthValue,
      'privileges': privileges?.map((e) => e.toJson()).toList(),
      'enabled': enabled,
    };
  }
}

/// 等级特权
class LevelPrivilege {
  final String? privilegeType;
  final String? privilegeName;
  final String? privilegeIcon;
  final String? privilegeDesc;
  final String? privilegeValue;
  final int? dailyLimit;
  final int? monthlyLimit;

  LevelPrivilege({
    this.privilegeType,
    this.privilegeName,
    this.privilegeIcon,
    this.privilegeDesc,
    this.privilegeValue,
    this.dailyLimit,
    this.monthlyLimit,
  });

  factory LevelPrivilege.fromJson(Map<String, dynamic> json) {
    return LevelPrivilege(
      privilegeType: json['privilegeType'],
      privilegeName: json['privilegeName'],
      privilegeIcon: json['privilegeIcon'],
      privilegeDesc: json['privilegeDesc'],
      privilegeValue: json['privilegeValue'],
      dailyLimit: json['dailyLimit'],
      monthlyLimit: json['monthlyLimit'],
    );
  }

  Map<String, dynamic> toJson() {
    return {
      'privilegeType': privilegeType,
      'privilegeName': privilegeName,
      'privilegeIcon': privilegeIcon,
      'privilegeDesc': privilegeDesc,
      'privilegeValue': privilegeValue,
      'dailyLimit': dailyLimit,
      'monthlyLimit': monthlyLimit,
    };
  }
}

/// 用户成长记录
class UserGrowthRecord {
  final int? id;
  final int? userId;
  final int? currentLevelId;
  final String? currentLevelCode;
  final int? totalGrowthValue;
  final int? yearGrowthValue;
  final int? monthGrowthValue;
  final int? todayGrowthValue;
  final String? highestLevelCode;
  final int? upgradeCount;
  final int? downgradeCount;

  UserGrowthRecord({
    this.id,
    this.userId,
    this.currentLevelId,
    this.currentLevelCode,
    this.totalGrowthValue,
    this.yearGrowthValue,
    this.monthGrowthValue,
    this.todayGrowthValue,
    this.highestLevelCode,
    this.upgradeCount,
    this.downgradeCount,
  });

  factory UserGrowthRecord.fromJson(Map<String, dynamic> json) {
    return UserGrowthRecord(
      id: json['id'],
      userId: json['userId'],
      currentLevelId: json['currentLevelId'],
      currentLevelCode: json['currentLevelCode'],
      totalGrowthValue: json['totalGrowthValue'],
      yearGrowthValue: json['yearGrowthValue'],
      monthGrowthValue: json['monthGrowthValue'],
      todayGrowthValue: json['todayGrowthValue'],
      highestLevelCode: json['highestLevelCode'],
      upgradeCount: json['upgradeCount'],
      downgradeCount: json['downgradeCount'],
    );
  }
}

/// 用户积分账户
class UserPointsAccount {
  final int? id;
  final int? userId;
  final int? availablePoints;
  final int? totalEarnedPoints;
  final int? totalSpentPoints;
  final int? frozenPoints;
  final int? expiringSoonPoints;
  final int? consecutiveSignDays;
  final int? totalSignDays;
  final String? pointsLevel;

  UserPointsAccount({
    this.id,
    this.userId,
    this.availablePoints,
    this.totalEarnedPoints,
    this.totalSpentPoints,
    this.frozenPoints,
    this.expiringSoonPoints,
    this.consecutiveSignDays,
    this.totalSignDays,
    this.pointsLevel,
  });

  factory UserPointsAccount.fromJson(Map<String, dynamic> json) {
    return UserPointsAccount(
      id: json['id'],
      userId: json['userId'],
      availablePoints: json['availablePoints'],
      totalEarnedPoints: json['totalEarnedPoints'],
      totalSpentPoints: json['totalSpentPoints'],
      frozenPoints: json['frozenPoints'],
      expiringSoonPoints: json['expiringSoonPoints'],
      consecutiveSignDays: json['consecutiveSignDays'],
      totalSignDays: json['totalSignDays'],
      pointsLevel: json['pointsLevel'],
    );
  }
}

/// 用户等级信息响应
class UserLevelInfoResponse {
  final int? userId;
  final LevelInfo? currentLevel;
  final LevelInfo? nextLevel;
  final LevelProgress? progress;
  final List<PrivilegeInfo>? privileges;
  final RetainInfo? retainInfo;

  UserLevelInfoResponse({
    this.userId,
    this.currentLevel,
    this.nextLevel,
    this.progress,
    this.privileges,
    this.retainInfo,
  });

  factory UserLevelInfoResponse.fromJson(Map<String, dynamic> json) {
    return UserLevelInfoResponse(
      userId: json['userId'],
      currentLevel: json['currentLevel'] != null
          ? LevelInfo.fromJson(json['currentLevel'])
          : null,
      nextLevel: json['nextLevel'] != null
          ? LevelInfo.fromJson(json['nextLevel'])
          : null,
      progress: json['progress'] != null
          ? LevelProgress.fromJson(json['progress'])
          : null,
      privileges: json['privileges'] != null
          ? (json['privileges'] as List)
              .map((e) => PrivilegeInfo.fromJson(e))
              .toList()
          : null,
      retainInfo: json['retainInfo'] != null
          ? RetainInfo.fromJson(json['retainInfo'])
          : null,
    );
  }
}

/// 等级信息
class LevelInfo {
  final String? levelCode;
  final String? levelName;
  final String? levelIcon;
  final String? levelColor;
  final int? levelOrder;
  final int? minGrowthValue;
  final int? maxGrowthValue;

  LevelInfo({
    this.levelCode,
    this.levelName,
    this.levelIcon,
    this.levelColor,
    this.levelOrder,
    this.minGrowthValue,
    this.maxGrowthValue,
  });

  factory LevelInfo.fromJson(Map<String, dynamic> json) {
    return LevelInfo(
      levelCode: json['levelCode'],
      levelName: json['levelName'],
      levelIcon: json['levelIcon'],
      levelColor: json['levelColor'],
      levelOrder: json['levelOrder'],
      minGrowthValue: json['minGrowthValue'],
      maxGrowthValue: json['maxGrowthValue'],
    );
  }
}

/// 等级进度
class LevelProgress {
  final int? currentGrowthValue;
  final int? needGrowthValue;
  final double? progressPercent;
  final String? progressText;

  LevelProgress({
    this.currentGrowthValue,
    this.needGrowthValue,
    this.progressPercent,
    this.progressText,
  });

  factory LevelProgress.fromJson(Map<String, dynamic> json) {
    return LevelProgress(
      currentGrowthValue: json['currentGrowthValue'],
      needGrowthValue: json['needGrowthValue'],
      progressPercent: json['progressPercent'],
      progressText: json['progressText'],
    );
  }
}

/// 特权信息
class PrivilegeInfo {
  final String? privilegeType;
  final String? privilegeName;
  final String? privilegeIcon;
  final String? privilegeDesc;
  final String? privilegeValue;
  final int? dailyLimit;
  final int? monthlyLimit;

  PrivilegeInfo({
    this.privilegeType,
    this.privilegeName,
    this.privilegeIcon,
    this.privilegeDesc,
    this.privilegeValue,
    this.dailyLimit,
    this.monthlyLimit,
  });

  factory PrivilegeInfo.fromJson(Map<String, dynamic> json) {
    return PrivilegeInfo(
      privilegeType: json['privilegeType'],
      privilegeName: json['privilegeName'],
      privilegeIcon: json['privilegeIcon'],
      privilegeDesc: json['privilegeDesc'],
      privilegeValue: json['privilegeValue'],
      dailyLimit: json['dailyLimit'],
      monthlyLimit: json['monthlyLimit'],
    );
  }
}

/// 保级信息
class RetainInfo {
  final bool? needRetain;
  final int? daysUntilDeadline;
  final int? retainMinGrowthValue;
  final int? currentYearGrowthValue;
  final bool? retainSuccess;
  final int? consecutiveRetainCount;

  RetainInfo({
    this.needRetain,
    this.daysUntilDeadline,
    this.retainMinGrowthValue,
    this.currentYearGrowthValue,
    this.retainSuccess,
    this.consecutiveRetainCount,
  });

  factory RetainInfo.fromJson(Map<String, dynamic> json) {
    return RetainInfo(
      needRetain: json['needRetain'],
      daysUntilDeadline: json['daysUntilDeadline'],
      retainMinGrowthValue: json['retainMinGrowthValue'],
      currentYearGrowthValue: json['currentYearGrowthValue'],
      retainSuccess: json['retainSuccess'],
      consecutiveRetainCount: json['consecutiveRetainCount'],
    );
  }
}

/// 用户积分信息响应
class UserPointsInfoResponse {
  final int? userId;
  final PointsAccountInfo? accountInfo;
  final SignInStatus? signInStatus;
  final PointsLevelInfo? pointsLevelInfo;
  final List<RecentTransaction>? recentTransactions;

  UserPointsInfoResponse({
    this.userId,
    this.accountInfo,
    this.signInStatus,
    this.pointsLevelInfo,
    this.recentTransactions,
  });

  factory UserPointsInfoResponse.fromJson(Map<String, dynamic> json) {
    return UserPointsInfoResponse(
      userId: json['userId'],
      accountInfo: json['accountInfo'] != null
          ? PointsAccountInfo.fromJson(json['accountInfo'])
          : null,
      signInStatus: json['signInStatus'] != null
          ? SignInStatus.fromJson(json['signInStatus'])
          : null,
      pointsLevelInfo: json['pointsLevelInfo'] != null
          ? PointsLevelInfo.fromJson(json['pointsLevelInfo'])
          : null,
      recentTransactions: json['recentTransactions'] != null
          ? (json['recentTransactions'] as List)
              .map((e) => RecentTransaction.fromJson(e))
              .toList()
          : null,
    );
  }
}

/// 积分账户信息
class PointsAccountInfo {
  final int? availablePoints;
  final int? totalEarnedPoints;
  final int? totalSpentPoints;
  final int? frozenPoints;
  final int? expiringSoonPoints;
  final DateTime? expiringSoonDate;

  PointsAccountInfo({
    this.availablePoints,
    this.totalEarnedPoints,
    this.totalSpentPoints,
    this.frozenPoints,
    this.expiringSoonPoints,
    this.expiringSoonDate,
  });

  factory PointsAccountInfo.fromJson(Map<String, dynamic> json) {
    return PointsAccountInfo(
      availablePoints: json['availablePoints'],
      totalEarnedPoints: json['totalEarnedPoints'],
      totalSpentPoints: json['totalSpentPoints'],
      frozenPoints: json['frozenPoints'],
      expiringSoonPoints: json['expiringSoonPoints'],
      expiringSoonDate: json['expiringSoonDate'] != null
          ? DateTime.parse(json['expiringSoonDate'])
          : null,
    );
  }
}

/// 签到状态
class SignInStatus {
  final bool? todaySigned;
  final int? consecutiveDays;
  final int? totalSignDays;
  final int? todayReward;
  final int? tomorrowReward;

  SignInStatus({
    this.todaySigned,
    this.consecutiveDays,
    this.totalSignDays,
    this.todayReward,
    this.tomorrowReward,
  });

  factory SignInStatus.fromJson(Map<String, dynamic> json) {
    return SignInStatus(
      todaySigned: json['todaySigned'],
      consecutiveDays: json['consecutiveDays'],
      totalSignDays: json['totalSignDays'],
      todayReward: json['todayReward'],
      tomorrowReward: json['tomorrowReward'],
    );
  }
}

/// 积分等级信息
class PointsLevelInfo {
  final String? level;
  final String? levelName;
  final int? pointsToNextLevel;
  final double? progressPercent;

  PointsLevelInfo({
    this.level,
    this.levelName,
    this.pointsToNextLevel,
    this.progressPercent,
  });

  factory PointsLevelInfo.fromJson(Map<String, dynamic> json) {
    return PointsLevelInfo(
      level: json['level'],
      levelName: json['levelName'],
      pointsToNextLevel: json['pointsToNextLevel'],
      progressPercent: json['progressPercent'],
    );
  }
}

/// 最近交易记录
class RecentTransaction {
  final String? transactionType;
  final int? points;
  final String? sourceType;
  final String? sourceDesc;
  final DateTime? transactionTime;

  RecentTransaction({
    this.transactionType,
    this.points,
    this.sourceType,
    this.sourceDesc,
    this.transactionTime,
  });

  factory RecentTransaction.fromJson(Map<String, dynamic> json) {
    return RecentTransaction(
      transactionType: json['transactionType'],
      points: json['points'],
      sourceType: json['sourceType'],
      sourceDesc: json['sourceDesc'],
      transactionTime: json['transactionTime'] != null
          ? DateTime.parse(json['transactionTime'])
          : null,
    );
  }
}

/// 签到结果
class SignInResult {
  final bool? success;
  final int? consecutiveDays;
  final int? rewardPoints;
  final int? rewardGrowth;
  final String? badgeCode;
  final String? badgeName;

  SignInResult({
    this.success,
    this.consecutiveDays,
    this.rewardPoints,
    this.rewardGrowth,
    this.badgeCode,
    this.badgeName,
  });

  factory SignInResult.fromJson(Map<String, dynamic> json) {
    return SignInResult(
      success: json['success'],
      consecutiveDays: json['consecutiveDays'],
      rewardPoints: json['rewardPoints'],
      rewardGrowth: json['rewardGrowth'],
      badgeCode: json['badgeCode'],
      badgeName: json['badgeName'],
    );
  }
}

/// 签到日历项
class SignInCalendarItem {
  final int? day;
  final bool? signed;
  final int? rewardPoints;
  final bool? today;
  final bool? future;

  SignInCalendarItem({
    this.day,
    this.signed,
    this.rewardPoints,
    this.today,
    this.future,
  });

  factory SignInCalendarItem.fromJson(Map<String, dynamic> json) {
    return SignInCalendarItem(
      day: json['day'],
      signed: json['signed'],
      rewardPoints: json['rewardPoints'],
      today: json['today'],
      future: json['future'],
    );
  }
}

/// API响应包装
class ApiResponse<T> {
  final int? code;
  final String? message;
  final T? data;

  ApiResponse({
    this.code,
    this.message,
    this.data,
  });

  factory ApiResponse.fromJson(Map<String, dynamic> json, T Function(dynamic) fromJsonT) {
    return ApiResponse(
      code: json['code'],
      message: json['message'],
      data: json['data'] != null ? fromJsonT(json['data']) : null,
    );
  }

  bool get isSuccess => code == 200;
}
