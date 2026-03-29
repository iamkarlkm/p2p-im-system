/// 语义搜索模型定义
/// 包含自然语言搜索、多轮对话、语音搜索相关的数据模型
/// 
/// Author: IM Development Team
/// Version: 1.0.0
/// Since: 2026-03-28

// ==================== 语义搜索请求模型 ====================

/// 语义搜索请求
class SemanticSearchRequest {
  /// 用户自然语言查询
  final String query;
  
  /// 会话ID（多轮对话时使用）
  final String? sessionId;
  
  /// 用户当前经度
  final double longitude;
  
  /// 用户当前纬度
  final double latitude;
  
  /// 城市代码
  final String? cityCode;
  
  /// 区县代码
  final String? districtCode;
  
  /// 是否为语音输入
  final bool isVoiceInput;
  
  /// 语音数据（Base64编码）
  final String? voiceData;
  
  /// 搜索范围（米）
  final int? searchRadius;
  
  /// 页码
  final int pageNum;
  
  /// 每页大小
  final int pageSize;
  
  /// 排序方式
  final String? sortBy;
  
  /// 过滤器
  final SearchFilter? filters;
  
  /// 设备类型
  final String? deviceType;

  SemanticSearchRequest({
    required this.query,
    this.sessionId,
    required this.longitude,
    required this.latitude,
    this.cityCode,
    this.districtCode,
    this.isVoiceInput = false,
    this.voiceData,
    this.searchRadius,
    this.pageNum = 1,
    this.pageSize = 20,
    this.sortBy,
    this.filters,
    this.deviceType,
  });

  Map<String, dynamic> toJson() => {
    'query': query,
    'sessionId': sessionId,
    'longitude': longitude,
    'latitude': latitude,
    'cityCode': cityCode,
    'districtCode': districtCode,
    'isVoiceInput': isVoiceInput,
    'voiceData': voiceData,
    'searchRadius': searchRadius,
    'pageNum': pageNum,
    'pageSize': pageSize,
    'sortBy': sortBy,
    'filters': filters?.toJson(),
    'deviceType': deviceType,
  };

  factory SemanticSearchRequest.fromJson(Map<String, dynamic> json) =>
      SemanticSearchRequest(
        query: json['query'],
        sessionId: json['sessionId'],
        longitude: json['longitude'],
        latitude: json['latitude'],
        cityCode: json['cityCode'],
        districtCode: json['districtCode'],
        isVoiceInput: json['isVoiceInput'] ?? false,
        voiceData: json['voiceData'],
        searchRadius: json['searchRadius'],
        pageNum: json['pageNum'] ?? 1,
        pageSize: json['pageSize'] ?? 20,
        sortBy: json['sortBy'],
        filters: json['filters'] != null 
            ? SearchFilter.fromJson(json['filters']) 
            : null,
        deviceType: json['deviceType'],
      );

  /// 复制并修改
  SemanticSearchRequest copyWith({
    String? query,
    String? sessionId,
    double? longitude,
    double? latitude,
    String? cityCode,
    bool? isVoiceInput,
    int? pageNum,
  }) => SemanticSearchRequest(
    query: query ?? this.query,
    sessionId: sessionId ?? this.sessionId,
    longitude: longitude ?? this.longitude,
    latitude: latitude ?? this.latitude,
    cityCode: cityCode ?? this.cityCode,
    isVoiceInput: isVoiceInput ?? this.isVoiceInput,
    pageNum: pageNum ?? this.pageNum,
    pageSize: pageSize,
  );
}

/// 搜索过滤器
class SearchFilter {
  /// POI分类列表
  final List<String>? categories;
  
  /// 最低评分
  final double? minRating;
  
  /// 最高人均消费
  final double? maxPrice;
  
  /// 最低人均消费
  final double? minPrice;
  
  /// 营业状态
  final String? businessStatus;
  
  /// 特色标签
  final List<String>? tags;
  
  /// 是否有优惠
  final bool? hasDiscount;
  
  /// 是否支持预订
  final bool? supportReservation;

  SearchFilter({
    this.categories,
    this.minRating,
    this.maxPrice,
    this.minPrice,
    this.businessStatus,
    this.tags,
    this.hasDiscount,
    this.supportReservation,
  });

  Map<String, dynamic> toJson() => {
    'categories': categories,
    'minRating': minRating,
    'maxPrice': maxPrice,
    'minPrice': minPrice,
    'businessStatus': businessStatus,
    'tags': tags,
    'hasDiscount': hasDiscount,
    'supportReservation': supportReservation,
  };

  factory SearchFilter.fromJson(Map<String, dynamic> json) => SearchFilter(
    categories: json['categories']?.cast<String>(),
    minRating: json['minRating'],
    maxPrice: json['maxPrice'],
    minPrice: json['minPrice'],
    businessStatus: json['businessStatus'],
    tags: json['tags']?.cast<String>(),
    hasDiscount: json['hasDiscount'],
    supportReservation: json['supportReservation'],
  );
}

// ==================== 语义搜索响应模型 ====================

/// 语义搜索响应
class SemanticSearchResponse {
  /// 响应状态
  final String status;
  
  /// 会话ID
  final String? sessionId;
  
  /// 解析后的查询意图
  final SearchIntent? intent;
  
  /// 澄清问题
  final String? clarificationQuestion;
  
  /// 澄清选项
  final List<String>? clarificationOptions;
  
  /// 搜索结果列表
  final List<SearchResult> results;
  
  /// 推荐问题
  final List<String>? suggestedQueries;
  
  /// 总结果数
  final int totalCount;
  
  /// 当前页码
  final int pageNum;
  
  /// 每页大小
  final int pageSize;
  
  /// 是否还有更多
  final bool hasMore;
  
  /// 搜索耗时（毫秒）
  final int searchTimeMs;

  SemanticSearchResponse({
    required this.status,
    this.sessionId,
    this.intent,
    this.clarificationQuestion,
    this.clarificationOptions,
    this.results = const [],
    this.suggestedQueries,
    this.totalCount = 0,
    this.pageNum = 1,
    this.pageSize = 20,
    this.hasMore = false,
    this.searchTimeMs = 0,
  });

  /// 是否需要澄清
  bool get needsClarification => status == 'CLARIFICATION_NEEDED';
  
  /// 是否成功
  bool get isSuccess => status == 'SUCCESS';
  
  /// 是否无结果
  bool get hasNoResults => status == 'NO_RESULTS';

  factory SemanticSearchResponse.fromJson(Map<String, dynamic> json) =>
      SemanticSearchResponse(
        status: json['status'],
        sessionId: json['sessionId'],
        intent: json['intent'] != null 
            ? SearchIntent.fromJson(json['intent']) 
            : null,
        clarificationQuestion: json['clarificationQuestion'],
        clarificationOptions: json['clarificationOptions']?.cast<String>(),
        results: json['results']?.map<SearchResult>((e) => 
            SearchResult.fromJson(e)).toList() ?? [],
        suggestedQueries: json['suggestedQueries']?.cast<String>(),
        totalCount: json['totalCount'] ?? 0,
        pageNum: json['pageNum'] ?? 1,
        pageSize: json['pageSize'] ?? 20,
        hasMore: json['hasMore'] ?? false,
        searchTimeMs: json['searchTimeMs'] ?? 0,
      );
}

/// 搜索意图
class SearchIntent {
  /// 主要意图
  final String primaryIntent;
  
  /// 意图置信度
  final double confidence;
  
  /// 意图描述
  final String? description;
  
  /// 提取的关键词
  final List<String>? keywords;
  
  /// POI分类
  final String? poiCategory;
  
  /// 解析后的约束条件
  final Map<String, dynamic>? constraints;

  SearchIntent({
    required this.primaryIntent,
    required this.confidence,
    this.description,
    this.keywords,
    this.poiCategory,
    this.constraints,
  });

  factory SearchIntent.fromJson(Map<String, dynamic> json) => SearchIntent(
    primaryIntent: json['primaryIntent'],
    confidence: json['confidence'],
    description: json['description'],
    keywords: json['keywords']?.cast<String>(),
    poiCategory: json['poiCategory'],
    constraints: json['constraints'],
  );

  /// 是否为导航意图
  bool get isNavigation => primaryIntent == 'NAVIGATION';
  
  /// 是否为团购意图
  bool get isGroupBuy => primaryIntent == 'GROUP_BUY';
  
  /// 是否为预约意图
  bool get isReservation => primaryIntent == 'RESERVATION';
}

/// 搜索结果
class SearchResult {
  /// POI ID
  final int poiId;
  
  /// POI名称
  final String name;
  
  /// POI分类
  final String category;
  
  /// 分类名称
  final String? categoryName;
  
  /// 评分
  final double? rating;
  
  /// 评价数量
  final int? reviewCount;
  
  /// 人均消费
  final String? avgPrice;
  
  /// 地址
  final String? address;
  
  /// 距离（米）
  final int? distance;
  
  /// 距离描述
  final String? distanceDesc;
  
  /// 主图URL
  final String? mainImage;
  
  /// 图片列表
  final List<String>? images;
  
  /// 营业时间
  final String? businessHours;
  
  /// 是否营业中
  final bool? isOpen;
  
  /// 营业状态描述
  final String? openStatusDesc;
  
  /// 特色标签
  final List<String>? tags;
  
  /// 推荐菜/服务
  final List<String>? recommendations;
  
  /// 优惠信息
  final List<DiscountInfo>? discounts;
  
  /// 经度
  final double? longitude;
  
  /// 纬度
  final double? latitude;
  
  /// 电话号码
  final String? phone;
  
  /// 排序得分
  final double? score;
  
  /// 推荐理由
  final String? recommendReason;
  
  /// 快捷操作
  final List<String>? quickActions;

  SearchResult({
    required this.poiId,
    required this.name,
    required this.category,
    this.categoryName,
    this.rating,
    this.reviewCount,
    this.avgPrice,
    this.address,
    this.distance,
    this.distanceDesc,
    this.mainImage,
    this.images,
    this.businessHours,
    this.isOpen,
    this.openStatusDesc,
    this.tags,
    this.recommendations,
    this.discounts,
    this.longitude,
    this.latitude,
    this.phone,
    this.score,
    this.recommendReason,
    this.quickActions,
  });

  factory SearchResult.fromJson(Map<String, dynamic> json) => SearchResult(
    poiId: json['poiId'],
    name: json['name'],
    category: json['category'],
    categoryName: json['categoryName'],
    rating: json['rating'],
    reviewCount: json['reviewCount'],
    avgPrice: json['avgPrice'],
    address: json['address'],
    distance: json['distance'],
    distanceDesc: json['distanceDesc'],
    mainImage: json['mainImage'],
    images: json['images']?.cast<String>(),
    businessHours: json['businessHours'],
    isOpen: json['isOpen'],
    openStatusDesc: json['openStatusDesc'],
    tags: json['tags']?.cast<String>(),
    recommendations: json['recommendations']?.cast<String>(),
    discounts: json['discounts']?.map<DiscountInfo>((e) => 
        DiscountInfo.fromJson(e)).toList(),
    longitude: json['longitude'],
    latitude: json['latitude'],
    phone: json['phone'],
    score: json['score'],
    recommendReason: json['recommendReason'],
    quickActions: json['quickActions']?.cast<String>(),
  );

  /// 获取格式化的距离
  String get formattedDistance {
    if (distance == null) return '';
    if (distance! < 1000) return '${distance}m';
    return '${(distance! / 1000).toStringAsFixed(1)}km';
  }

  /// 获取格式化的评分
  String get formattedRating {
    if (rating == null) return '';
    return rating!.toStringAsFixed(1);
  }
}

/// 优惠信息
class DiscountInfo {
  /// 优惠类型
  final String type;
  
  /// 优惠标题
  final String title;
  
  /// 优惠内容
  final String content;
  
  /// 原价
  final double? originalPrice;
  
  /// 现价
  final double? currentPrice;

  DiscountInfo({
    required this.type,
    required this.title,
    required this.content,
    this.originalPrice,
    this.currentPrice,
  });

  factory DiscountInfo.fromJson(Map<String, dynamic> json) => DiscountInfo(
    type: json['type'],
    title: json['title'],
    content: json['content'],
    originalPrice: json['originalPrice'],
    currentPrice: json['currentPrice'],
  );

  /// 获取折扣率
  double? get discountRate {
    if (originalPrice == null || currentPrice == null || originalPrice == 0) {
      return null;
    }
    return currentPrice! / originalPrice!;
  }
}

// ==================== 搜索建议模型 ====================

/// 搜索建议
class SearchSuggestion {
  /// 建议文本
  final String text;
  
  /// 建议类型
  final String type;
  
  /// 相关POI数量
  final int? poiCount;
  
  /// 是否热门
  final bool isHot;

  SearchSuggestion({
    required this.text,
    required this.type,
    this.poiCount,
    this.isHot = false,
  });
}

// ==================== 搜索历史模型 ====================

/// 搜索历史
class SearchHistory {
  /// 搜索关键词
  final String query;
  
  /// 搜索时间
  final DateTime searchTime;
  
  /// 搜索结果数
  final int? resultCount;
  
  /// 是否点击过结果
  final bool hasClicked;

  SearchHistory({
    required this.query,
    required this.searchTime,
    this.resultCount,
    this.hasClicked = false,
  });
}
