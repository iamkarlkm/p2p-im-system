import 'dart:async';
import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;
import '../../models/live/live_room_model.dart';
import '../../utils/logger.dart';
import '../../config/api_config.dart';

/// 直播数据分析服务
/// 提供直播数据实时统计、分析、报表等功能
class LiveAnalyticsService extends ChangeNotifier {
  static final LiveAnalyticsService _instance = LiveAnalyticsService._internal();
  factory LiveAnalyticsService() => _instance;
  LiveAnalyticsService._internal();

  // 实时数据缓存
  final Map<String, LiveRealtimeData> _realtimeData = {};
  
  // 历史数据缓存
  final Map<String, LiveHistoryData> _historyData = {};

  // 数据更新定时器
  Timer? _updateTimer;

  // 当前监控的直播间
  String? _currentRoomId;

  // 错误信息
  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  // 加载状态
  bool _isLoading = false;
  bool get isLoading => _isLoading;

  /// 获取实时数据
  LiveRealtimeData? getRealtimeData(String roomId) => _realtimeData[roomId];

  /// 获取历史数据
  LiveHistoryData? getHistoryData(String roomId) => _historyData[roomId];

  /// 开始监控直播间数据
  void startMonitoring(String roomId) {
    Logger.log('LiveAnalyticsService', 'Start monitoring room: $roomId');
    
    _currentRoomId = roomId;
    _updateTimer?.cancel();
    
    // 立即获取一次数据
    _fetchRealtimeData(roomId);
    
    // 每10秒更新一次
    _updateTimer = Timer.periodic(Duration(seconds: 10), (timer) {
      if (_currentRoomId != null) {
        _fetchRealtimeData(_currentRoomId!);
      }
    });
  }

  /// 停止监控
  void stopMonitoring() {
    Logger.log('LiveAnalyticsService', 'Stop monitoring');
    _updateTimer?.cancel();
    _updateTimer = null;
    _currentRoomId = null;
  }

  /// 获取实时数据
  Future<void> _fetchRealtimeData(String roomId) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/analytics/realtime'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        _realtimeData[roomId] = LiveRealtimeData.fromJson(data);
        notifyListeners();
      }
    } catch (e, stackTrace) {
      Logger.error('LiveAnalyticsService', 'Failed to fetch realtime data', e, stackTrace);
    }
  }

  /// 获取直播历史数据分析
  Future<LiveHistoryData?> getLiveHistoryAnalytics(String roomId) async {
    _isLoading = true;
    notifyListeners();

    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/analytics/history'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        final historyData = LiveHistoryData.fromJson(data);
        _historyData[roomId] = historyData;
        notifyListeners();
        return historyData;
      }
      return null;
    } catch (e, stackTrace) {
      Logger.error('LiveAnalyticsService', 'Failed to get history analytics', e, stackTrace);
      _errorMessage = '获取历史数据失败';
      return null;
    } finally {
      _isLoading = false;
      notifyListeners();
    }
  }

  /// 获取观众画像分析
  Future<AudienceProfile?> getAudienceProfile(String roomId) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/analytics/audience'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return AudienceProfile.fromJson(data);
      }
      return null;
    } catch (e, stackTrace) {
      Logger.error('LiveAnalyticsService', 'Failed to get audience profile', e, stackTrace);
      return null;
    }
  }

  /// 获取流量来源分析
  Future<TrafficSourceAnalysis?> getTrafficSourceAnalysis(String roomId) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/analytics/traffic'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return TrafficSourceAnalysis.fromJson(data);
      }
      return null;
    } catch (e, stackTrace) {
      Logger.error('LiveAnalyticsService', 'Failed to get traffic source analysis', e, stackTrace);
      return null;
    }
  }

  /// 获取商品转化分析
  Future<ProductConversionAnalysis?> getProductConversionAnalysis(String roomId) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/analytics/products'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return ProductConversionAnalysis.fromJson(data);
      }
      return null;
    } catch (e, stackTrace) {
      Logger.error('LiveAnalyticsService', 'Failed to get product conversion analysis', e, stackTrace);
      return null;
    }
  }

  /// 获取互动数据分析
  Future<InteractionAnalysis?> getInteractionAnalysis(String roomId) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/analytics/interaction'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return InteractionAnalysis.fromJson(data);
      }
      return null;
    } catch (e, stackTrace) {
      Logger.error('LiveAnalyticsService', 'Failed to get interaction analysis', e, stackTrace);
      return null;
    }
  }

  /// 获取直播对比分析
  Future<LiveComparisonAnalysis?> getComparisonAnalysis(
    String roomId, {
    required DateTime startDate,
    required DateTime endDate,
  }) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/analytics/comparison')
            .replace(queryParameters: {
          'startDate': startDate.toIso8601String(),
          'endDate': endDate.toIso8601String(),
        }),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return LiveComparisonAnalysis.fromJson(data);
      }
      return null;
    } catch (e, stackTrace) {
      Logger.error('LiveAnalyticsService', 'Failed to get comparison analysis', e, stackTrace);
      return null;
    }
  }

  /// 获取直播趋势分析
  Future<LiveTrendAnalysis?> getTrendAnalysis(
    String streamerId, {
    required String period, // day, week, month
  }) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/analytics/trends/$streamerId')
            .replace(queryParameters: {'period': period}),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return LiveTrendAnalysis.fromJson(data);
      }
      return null;
    } catch (e, stackTrace) {
      Logger.error('LiveAnalyticsService', 'Failed to get trend analysis', e, stackTrace);
      return null;
    }
  }

  /// 导出直播数据报表
  Future<String?> exportReport(String roomId, {required String format}) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/analytics/export')
            .replace(queryParameters: {'format': format}),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return data['downloadUrl'];
      }
      return null;
    } catch (e, stackTrace) {
      Logger.error('LiveAnalyticsService', 'Failed to export report', e, stackTrace);
      return null;
    }
  }

  /// 清理资源
  @override
  void dispose() {
    stopMonitoring();
    super.dispose();
  }
}

/// 实时数据
class LiveRealtimeData {
  final String roomId;
  final int onlineCount;
  final int totalViewers;
  final int likeCount;
  final int messageCount;
  final int giftCount;
  final double giftValue;
  final int orderCount;
  final double salesAmount;
  final List<int> viewerTrend; // 最近10分钟观众趋势
  final DateTime timestamp;

  LiveRealtimeData({
    required this.roomId,
    required this.onlineCount,
    required this.totalViewers,
    required this.likeCount,
    required this.messageCount,
    required this.giftCount,
    required this.giftValue,
    required this.orderCount,
    required this.salesAmount,
    required this.viewerTrend,
    required this.timestamp,
  });

  factory LiveRealtimeData.fromJson(Map<String, dynamic> json) {
    return LiveRealtimeData(
      roomId: json['roomId'] ?? '',
      onlineCount: json['onlineCount'] ?? 0,
      totalViewers: json['totalViewers'] ?? 0,
      likeCount: json['likeCount'] ?? 0,
      messageCount: json['messageCount'] ?? 0,
      giftCount: json['giftCount'] ?? 0,
      giftValue: (json['giftValue'] ?? 0.0).toDouble(),
      orderCount: json['orderCount'] ?? 0,
      salesAmount: (json['salesAmount'] ?? 0.0).toDouble(),
      viewerTrend: List<int>.from(json['viewerTrend'] ?? []),
      timestamp: DateTime.parse(json['timestamp']),
    );
  }

  /// 人均停留时长（估算）
  double get avgViewDuration => totalViewers > 0 ? (onlineCount * 60.0 / totalViewers) : 0;

  /// 互动率
  double get interactionRate => totalViewers > 0 ? ((likeCount + messageCount) / totalViewers * 100) : 0;

  /// 转化率
  double get conversionRate => totalViewers > 0 ? (orderCount / totalViewers * 100) : 0;
}

/// 历史数据
class LiveHistoryData {
  final String roomId;
  final Duration duration;
  final int peakViewers;
  final int totalViewers;
  final int totalLikes;
  final int totalMessages;
  final int totalGifts;
  final double totalGiftValue;
  final int totalOrders;
  final double totalSales;
  final double avgViewDuration;
  final List<TimeSeriesData> viewerTimeline;
  final List<TimeSeriesData> salesTimeline;
  final DateTime createdAt;

  LiveHistoryData({
    required this.roomId,
    required this.duration,
    required this.peakViewers,
    required this.totalViewers,
    required this.totalLikes,
    required this.totalMessages,
    required this.totalGifts,
    required this.totalGiftValue,
    required this.totalOrders,
    required this.totalSales,
    required this.avgViewDuration,
    required this.viewerTimeline,
    required this.salesTimeline,
    required this.createdAt,
  });

  factory LiveHistoryData.fromJson(Map<String, dynamic> json) {
    return LiveHistoryData(
      roomId: json['roomId'] ?? '',
      duration: Duration(seconds: json['duration'] ?? 0),
      peakViewers: json['peakViewers'] ?? 0,
      totalViewers: json['totalViewers'] ?? 0,
      totalLikes: json['totalLikes'] ?? 0,
      totalMessages: json['totalMessages'] ?? 0,
      totalGifts: json['totalGifts'] ?? 0,
      totalGiftValue: (json['totalGiftValue'] ?? 0.0).toDouble(),
      totalOrders: json['totalOrders'] ?? 0,
      totalSales: (json['totalSales'] ?? 0.0).toDouble(),
      avgViewDuration: (json['avgViewDuration'] ?? 0.0).toDouble(),
      viewerTimeline: (json['viewerTimeline'] as List?)
          ?.map((t) => TimeSeriesData.fromJson(t))
          .toList() ?? [],
      salesTimeline: (json['salesTimeline'] as List?)
          ?.map((t) => TimeSeriesData.fromJson(t))
          .toList() ?? [],
      createdAt: DateTime.parse(json['createdAt']),
    );
  }

  /// 互动率
  double get interactionRate => totalViewers > 0 ? (totalLikes + totalMessages) / totalViewers * 100 : 0;

  /// 转化率
  double get conversionRate => totalViewers > 0 ? totalOrders / totalViewers * 100 : 0;

  /// 客单价
  double get avgOrderValue => totalOrders > 0 ? totalSales / totalOrders : 0;
}

/// 时间序列数据
class TimeSeriesData {
  final DateTime time;
  final double value;

  TimeSeriesData({
    required this.time,
    required this.value,
  });

  factory TimeSeriesData.fromJson(Map<String, dynamic> json) {
    return TimeSeriesData(
      time: DateTime.parse(json['time']),
      value: (json['value'] ?? 0.0).toDouble(),
    );
  }
}

/// 观众画像
class AudienceProfile {
  final Map<String, double> genderDistribution; // 性别分布
  final Map<String, double> ageDistribution; // 年龄分布
  final Map<String, double> cityDistribution; // 城市分布
  final Map<String, double> deviceDistribution; // 设备分布
  final Map<String, double> sourceDistribution; // 来源分布
  final List<String> topInterests; // 兴趣标签

  AudienceProfile({
    required this.genderDistribution,
    required this.ageDistribution,
    required this.cityDistribution,
    required this.deviceDistribution,
    required this.sourceDistribution,
    required this.topInterests,
  });

  factory AudienceProfile.fromJson(Map<String, dynamic> json) {
    return AudienceProfile(
      genderDistribution: Map<String, double>.from(json['genderDistribution'] ?? {}),
      ageDistribution: Map<String, double>.from(json['ageDistribution'] ?? {}),
      cityDistribution: Map<String, double>.from(json['cityDistribution'] ?? {}),
      deviceDistribution: Map<String, double>.from(json['deviceDistribution'] ?? {}),
      sourceDistribution: Map<String, double>.from(json['sourceDistribution'] ?? {}),
      topInterests: List<String>.from(json['topInterests'] ?? []),
    );
  }
}

/// 流量来源分析
class TrafficSourceAnalysis {
  final List<TrafficSource> sources;
  final int totalVisitors;

  TrafficSourceAnalysis({
    required this.sources,
    required this.totalVisitors,
  });

  factory TrafficSourceAnalysis.fromJson(Map<String, dynamic> json) {
    return TrafficSourceAnalysis(
      sources: (json['sources'] as List?)
          ?.map((s) => TrafficSource.fromJson(s))
          .toList() ?? [],
      totalVisitors: json['totalVisitors'] ?? 0,
    );
  }
}

/// 流量来源
class TrafficSource {
  final String name;
  final int visitors;
  final double percentage;

  TrafficSource({
    required this.name,
    required this.visitors,
    required this.percentage,
  });

  factory TrafficSource.fromJson(Map<String, dynamic> json) {
    return TrafficSource(
      name: json['name'] ?? '',
      visitors: json['visitors'] ?? 0,
      percentage: (json['percentage'] ?? 0.0).toDouble(),
    );
  }
}

/// 商品转化分析
class ProductConversionAnalysis {
  final List<ProductConversion> products;
  final double avgConversionRate;

  ProductConversionAnalysis({
    required this.products,
    required this.avgConversionRate,
  });

  factory ProductConversionAnalysis.fromJson(Map<String, dynamic> json) {
    return ProductConversionAnalysis(
      products: (json['products'] as List?)
          ?.map((p) => ProductConversion.fromJson(p))
          .toList() ?? [],
      avgConversionRate: (json['avgConversionRate'] ?? 0.0).toDouble(),
    );
  }
}

/// 商品转化数据
class ProductConversion {
  final String productId;
  final String productName;
  final int impressions;
  final int clicks;
  final int orders;
  final double sales;
  final double clickThroughRate;
  final double conversionRate;

  ProductConversion({
    required this.productId,
    required this.productName,
    required this.impressions,
    required this.clicks,
    required this.orders,
    required this.sales,
    required this.clickThroughRate,
    required this.conversionRate,
  });

  factory ProductConversion.fromJson(Map<String, dynamic> json) {
    return ProductConversion(
      productId: json['productId'] ?? '',
      productName: json['productName'] ?? '',
      impressions: json['impressions'] ?? 0,
      clicks: json['clicks'] ?? 0,
      orders: json['orders'] ?? 0,
      sales: (json['sales'] ?? 0.0).toDouble(),
      clickThroughRate: (json['clickThroughRate'] ?? 0.0).toDouble(),
      conversionRate: (json['conversionRate'] ?? 0.0).toDouble(),
    );
  }
}

/// 互动分析
class InteractionAnalysis {
  final int totalMessages;
  final int uniqueChatters;
  final double messagesPerMinute;
  final List<KeywordStat> topKeywords;
  final List<InteractionPeak> peaks;
  final Map<String, int> messageTypeDistribution;

  InteractionAnalysis({
    required this.totalMessages,
    required this.uniqueChatters,
    required this.messagesPerMinute,
    required this.topKeywords,
    required this.peaks,
    required this.messageTypeDistribution,
  });

  factory InteractionAnalysis.fromJson(Map<String, dynamic> json) {
    return InteractionAnalysis(
      totalMessages: json['totalMessages'] ?? 0,
      uniqueChatters: json['uniqueChatters'] ?? 0,
      messagesPerMinute: (json['messagesPerMinute'] ?? 0.0).toDouble(),
      topKeywords: (json['topKeywords'] as List?)
          ?.map((k) => KeywordStat.fromJson(k))
          .toList() ?? [],
      peaks: (json['peaks'] as List?)
          ?.map((p) => InteractionPeak.fromJson(p))
          .toList() ?? [],
      messageTypeDistribution: Map<String, int>.from(json['messageTypeDistribution'] ?? {}),
    );
  }
}

/// 关键词统计
class KeywordStat {
  final String keyword;
  final int count;

  KeywordStat({
    required this.keyword,
    required this.count,
  });

  factory KeywordStat.fromJson(Map<String, dynamic> json) {
    return KeywordStat(
      keyword: json['keyword'] ?? '',
      count: json['count'] ?? 0,
    );
  }
}

/// 互动高峰
class InteractionPeak {
  final DateTime time;
  final int messageCount;

  InteractionPeak({
    required this.time,
    required this.messageCount,
  });

  factory InteractionPeak.fromJson(Map<String, dynamic> json) {
    return InteractionPeak(
      time: DateTime.parse(json['time']),
      messageCount: json['messageCount'] ?? 0,
    );
  }
}

/// 直播对比分析
class LiveComparisonAnalysis {
  final List<LiveSummary> lives;
  final ComparisonMetrics metrics;

  LiveComparisonAnalysis({
    required this.lives,
    required this.metrics,
  });

  factory LiveComparisonAnalysis.fromJson(Map<String, dynamic> json) {
    return LiveComparisonAnalysis(
      lives: (json['lives'] as List?)
          ?.map((l) => LiveSummary.fromJson(l))
          .toList() ?? [],
      metrics: ComparisonMetrics.fromJson(json['metrics'] ?? {}),
    );
  }
}

/// 直播摘要
class LiveSummary {
  final String roomId;
  final String title;
  final DateTime startTime;
  final Duration duration;
  final int viewers;
  final int likes;
  final double sales;

  LiveSummary({
    required this.roomId,
    required this.title,
    required this.startTime,
    required this.duration,
    required this.viewers,
    required this.likes,
    required this.sales,
  });

  factory LiveSummary.fromJson(Map<String, dynamic> json) {
    return LiveSummary(
      roomId: json['roomId'] ?? '',
      title: json['title'] ?? '',
      startTime: DateTime.parse(json['startTime']),
      duration: Duration(seconds: json['duration'] ?? 0),
      viewers: json['viewers'] ?? 0,
      likes: json['likes'] ?? 0,
      sales: (json['sales'] ?? 0.0).toDouble(),
    );
  }
}

/// 对比指标
class ComparisonMetrics {
  final double avgViewers;
  final double avgLikes;
  final double avgSales;
  final double avgDuration;

  ComparisonMetrics({
    required this.avgViewers,
    required this.avgLikes,
    required this.avgSales,
    required this.avgDuration,
  });

  factory ComparisonMetrics.fromJson(Map<String, dynamic> json) {
    return ComparisonMetrics(
      avgViewers: (json['avgViewers'] ?? 0.0).toDouble(),
      avgLikes: (json['avgLikes'] ?? 0.0).toDouble(),
      avgSales: (json['avgSales'] ?? 0.0).toDouble(),
      avgDuration: (json['avgDuration'] ?? 0.0).toDouble(),
    );
  }
}

/// 直播趋势分析
class LiveTrendAnalysis {
  final String period;
  final List<TrendData> viewerTrend;
  final List<TrendData> salesTrend;
  final List<TrendData> interactionTrend;
  final GrowthMetrics growth;

  LiveTrendAnalysis({
    required this.period,
    required this.viewerTrend,
    required this.salesTrend,
    required this.interactionTrend,
    required this.growth,
  });

  factory LiveTrendAnalysis.fromJson(Map<String, dynamic> json) {
    return LiveTrendAnalysis(
      period: json['period'] ?? '',
      viewerTrend: (json['viewerTrend'] as List?)
          ?.map((t) => TrendData.fromJson(t))
          .toList() ?? [],
      salesTrend: (json['salesTrend'] as List?)
          ?.map((t) => TrendData.fromJson(t))
          .toList() ?? [],
      interactionTrend: (json['interactionTrend'] as List?)
          ?.map((t) => TrendData.fromJson(t))
          .toList() ?? [],
      growth: GrowthMetrics.fromJson(json['growth'] ?? {}),
    );
  }
}

/// 趋势数据
class TrendData {
  final DateTime date;
  final double value;

  TrendData({
    required this.date,
    required this.value,
  });

  factory TrendData.fromJson(Map<String, dynamic> json) {
    return TrendData(
      date: DateTime.parse(json['date']),
      value: (json['value'] ?? 0.0).toDouble(),
    );
  }
}

/// 增长指标
class GrowthMetrics {
  final double viewerGrowth;
  final double salesGrowth;
  final double interactionGrowth;

  GrowthMetrics({
    required this.viewerGrowth,
    required this.salesGrowth,
    required this.interactionGrowth,
  });

  factory GrowthMetrics.fromJson(Map<String, dynamic> json) {
    return GrowthMetrics(
      viewerGrowth: (json['viewerGrowth'] ?? 0.0).toDouble(),
      salesGrowth: (json['salesGrowth'] ?? 0.0).toDouble(),
      interactionGrowth: (json['interactionGrowth'] ?? 0.0).toDouble(),
    );
  }
}
