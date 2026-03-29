/// 对话模型定义
/// 包含多轮对话会话、上下文管理相关的数据模型
/// 
/// Author: IM Development Team
/// Version: 1.0.0
/// Since: 2026-03-28

// ==================== 对话会话模型 ====================

/// 对话会话
class ConversationSession {
  /// 会话ID
  final String sessionId;
  
  /// 用户ID
  final int userId;
  
  /// 会话类型
  final String sessionType;
  
  /// 会话状态
  final String status;
  
  /// 当前意图
  final String? currentIntent;
  
  /// 对话轮次
  final int turnCount;
  
  /// 会话开始时间
  final DateTime startTime;
  
  /// 最后交互时间
  final DateTime lastInteractionTime;
  
  /// 用户位置经度
  final double? userLongitude;
  
  /// 用户位置纬度
  final double? userLatitude;
  
  /// 用户当前城市
  final String? userCity;
  
  /// 是否已推荐结果
  final bool hasRecommended;
  
  /// 推荐结果数量
  final int recommendationCount;
  
  /// 用户点击数量
  final int userClickCount;

  ConversationSession({
    required this.sessionId,
    required this.userId,
    required this.sessionType,
    this.status = 'ACTIVE',
    this.currentIntent,
    this.turnCount = 0,
    required this.startTime,
    required this.lastInteractionTime,
    this.userLongitude,
    this.userLatitude,
    this.userCity,
    this.hasRecommended = false,
    this.recommendationCount = 0,
    this.userClickCount = 0,
  });

  factory ConversationSession.fromJson(Map<String, dynamic> json) =>
      ConversationSession(
        sessionId: json['sessionId'],
        userId: json['userId'],
        sessionType: json['sessionType'],
        status: json['status'] ?? 'ACTIVE',
        currentIntent: json['currentIntent'],
        turnCount: json['turnCount'] ?? 0,
        startTime: DateTime.parse(json['startTime']),
        lastInteractionTime: DateTime.parse(json['lastInteractionTime']),
        userLongitude: json['userLongitude'],
        userLatitude: json['userLatitude'],
        userCity: json['userCity'],
        hasRecommended: json['hasRecommended'] ?? false,
        recommendationCount: json['recommendationCount'] ?? 0,
        userClickCount: json['userClickCount'] ?? 0,
      );

  /// 是否活跃
  bool get isActive => status == 'ACTIVE';
  
  /// 是否已结束
  bool get isEnded => status == 'ENDED';
  
  /// 获取转化率
  double get conversionRate {
    if (recommendationCount == 0) return 0.0;
    return userClickCount / recommendationCount;
  }

  /// 更新位置
  ConversationSession updateLocation(double longitude, double latitude, String? city) {
    return ConversationSession(
      sessionId: sessionId,
      userId: userId,
      sessionType: sessionType,
      status: status,
      currentIntent: currentIntent,
      turnCount: turnCount,
      startTime: startTime,
      lastInteractionTime: DateTime.now(),
      userLongitude: longitude,
      userLatitude: latitude,
      userCity: city ?? userCity,
      hasRecommended: hasRecommended,
      recommendationCount: recommendationCount,
      userClickCount: userClickCount,
    );
  }
}

// ==================== 对话消息模型 ====================

/// 对话消息
class ConversationMessage {
  /// 消息ID
  final String messageId;
  
  /// 会话ID
  final String sessionId;
  
  /// 消息类型
  final MessageType type;
  
  /// 消息内容
  final String content;
  
  /// 发送者类型
  final SenderType sender;
  
  /// 发送时间
  final DateTime sendTime;
  
  /// 附加数据
  final Map<String, dynamic>? extraData;

  ConversationMessage({
    required this.messageId,
    required this.sessionId,
    required this.type,
    required this.content,
    required this.sender,
    required this.sendTime,
    this.extraData,
  });

  /// 创建用户消息
  factory ConversationMessage.user({
    required String sessionId,
    required String content,
    Map<String, dynamic>? extraData,
  }) => ConversationMessage(
    messageId: DateTime.now().millisecondsSinceEpoch.toString(),
    sessionId: sessionId,
    type: MessageType.text,
    content: content,
    sender: SenderType.user,
    sendTime: DateTime.now(),
    extraData: extraData,
  );

  /// 创建系统消息
  factory ConversationMessage.system({
    required String sessionId,
    required String content,
    Map<String, dynamic>? extraData,
  }) => ConversationMessage(
    messageId: DateTime.now().millisecondsSinceEpoch.toString(),
    sessionId: sessionId,
    type: MessageType.text,
    content: content,
    sender: SenderType.system,
    sendTime: DateTime.now(),
    extraData: extraData,
  );

  factory ConversationMessage.fromJson(Map<String, dynamic> json) =>
      ConversationMessage(
        messageId: json['messageId'],
        sessionId: json['sessionId'],
        type: MessageType.values.firstWhere(
          (e) => e.name == json['type'],
          orElse: () => MessageType.text,
        ),
        content: json['content'],
        sender: SenderType.values.firstWhere(
          (e) => e.name == json['sender'],
          orElse: () => SenderType.user,
        ),
        sendTime: DateTime.parse(json['sendTime']),
        extraData: json['extraData'],
      );
}

/// 消息类型
enum MessageType {
  text,       // 文本
  voice,      // 语音
  image,      // 图片
  location,   // 位置
  result,     // 搜索结果
  clarification, // 澄清问题
}

/// 发送者类型
enum SenderType {
  user,       // 用户
  system,     // 系统
  assistant,  // 助手
}

// ==================== 多轮查询模型 ====================

/// 多轮查询
class MultiTurnQuery {
  /// 原始查询
  final String rawQuery;
  
  /// 上下文增强后的查询
  final String contextualQuery;
  
  /// 继承的上下文信息
  final Map<String, dynamic> inheritedContext;
  
  /// 当前轮次
  final int turnNumber;
  
  /// 是否为澄清查询
  final bool isClarification;

  MultiTurnQuery({
    required this.rawQuery,
    required this.contextualQuery,
    this.inheritedContext = const {},
    required this.turnNumber,
    this.isClarification = false,
  });
}

// ==================== 意图识别模型 ====================

/// 意图识别结果
class IntentRecognition {
  /// 主要意图
  final String primaryIntent;
  
  /// 意图置信度
  final double confidence;
  
  /// 次要意图列表
  final List<String> secondaryIntents;
  
  /// 提取的槽位
  final Map<String, dynamic> slots;
  
  /// 是否需要澄清
  final bool needsClarification;
  
  /// 澄清问题
  final String? clarificationQuestion;

  IntentRecognition({
    required this.primaryIntent,
    required this.confidence,
    this.secondaryIntents = const [],
    this.slots = const {},
    this.needsClarification = false,
    this.clarificationQuestion,
  });

  /// 是否高置信度
  bool get isHighConfidence => confidence >= 0.8;
  
  /// 是否中等置信度
  bool get isMediumConfidence => confidence >= 0.5 && confidence < 0.8;
  
  /// 是否低置信度
  bool get isLowConfidence => confidence < 0.5;
}

// ==================== 上下文模型 ====================

/// 对话上下文
class ConversationContext {
  /// 已收集的槽位
  final Map<String, dynamic> collectedSlots;
  
  /// 待澄清的槽位
  final List<String> pendingSlots;
  
  /// 历史查询
  final List<String> queryHistory;
  
  /// 已推荐的POI IDs
  final List<int> recommendedPoiIds;
  
  /// 用户点击的POI IDs
  final List<int> clickedPoiIds;
  
  /// 会话主题
  final String? topic;

  ConversationContext({
    this.collectedSlots = const {},
    this.pendingSlots = const [],
    this.queryHistory = const [],
    this.recommendedPoiIds = const [],
    this.clickedPoiIds = const [],
    this.topic,
  });

  /// 添加上下文
  ConversationContext addSlot(String key, dynamic value) {
    final newSlots = Map<String, dynamic>.from(collectedSlots);
    newSlots[key] = value;
    return ConversationContext(
      collectedSlots: newSlots,
      pendingSlots: pendingSlots,
      queryHistory: queryHistory,
      recommendedPoiIds: recommendedPoiIds,
      clickedPoiIds: clickedPoiIds,
      topic: topic,
    );
  }

  /// 添加入历史
  ConversationContext addToHistory(String query) {
    return ConversationContext(
      collectedSlots: collectedSlots,
      pendingSlots: pendingSlots,
      queryHistory: [...queryHistory, query],
      recommendedPoiIds: recommendedPoiIds,
      clickedPoiIds: clickedPoiIds,
      topic: topic,
    );
  }
}
