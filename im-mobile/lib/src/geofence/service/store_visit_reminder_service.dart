import 'dart:async';
import 'dart:convert';

import 'package:flutter/foundation.dart';
import 'package:local_notifications/local_notifications.dart';

import '../models/geofence_model.dart';
import '../service/geofence_service_manager.dart';
import '../../api/local_life_api.dart';
import '../../models/user.dart';
import '../../models/merchant.dart';

/// 到店提醒服务
/// 
/// 负责：
/// - 处理到店触发事件
/// - 个性化推荐
/// - 推送通知
/// - 会员权益检查
class StoreVisitReminderService extends ChangeNotifier {
  static final StoreVisitReminderService _instance = StoreVisitReminderService._internal();
  factory StoreVisitReminderService() => _instance;
  StoreVisitReminderService._internal();

  final GeofenceServiceManager _geofenceManager = GeofenceServiceManager();
  final LocalLifeApi _api = LocalLifeApi();
  
  StreamSubscription<GeofenceTriggerEvent>? _eventSubscription;
  
  /// 当前用户
  User? _currentUser;
  
  /// 已触发的到店记录（去重用）
  final Set<String> _triggeredVisits = {};
  
  /// 最近到店记录
  final List<StoreVisitRecord> _recentVisits = [];
  
  /// 个性化推荐缓存
  Map<String, List<PersonalizedOffer>> _offerCache = {};
  
  /// 是否已初始化
  bool _initialized = false;

  // ==================== Getters ====================
  
  /// 最近到店记录
  List<StoreVisitRecord> get recentVisits => List.unmodifiable(_recentVisits);
  
  /// 当前用户
  User? get currentUser => _currentUser;

  // ==================== 初始化 ====================
  
  /// 初始化服务
  Future<void> initialize({User? user}) async {
    if (_initialized) return;
    
    _currentUser = user;
    
    // 订阅围栏事件
    _eventSubscription = _geofenceManager.eventStream.listen(
      _handleGeofenceEvent,
      onError: (error) {
        debugPrint('Geofence event error: $error');
      },
    );
    
    // 初始化本地通知
    await LocalNotifications.initialize();
    
    _initialized = true;
    debugPrint('StoreVisitReminderService initialized');
  }
  
  /// 设置当前用户
  void setUser(User? user) {
    _currentUser = user;
    notifyListeners();
  }

  // ==================== 围栏事件处理 ====================
  
  /// 处理围栏触发事件
  Future<void> _handleGeofenceEvent(GeofenceTriggerEvent event) async {
    switch (event.eventType) {
      case GeofenceEvent.enter:
        await _handleStoreEnter(event);
        break;
      case GeofenceEvent.dwell:
        await _handleStoreDwell(event);
        break;
      case GeofenceEvent.exit:
        await _handleStoreExit(event);
        break;
      default:
        break;
    }
  }
  
  /// 处理进入门店
  Future<void> _handleStoreEnter(GeofenceTriggerEvent event) async {
    final geofence = _geofenceManager.getGeofence(event.geofenceId);
    if (geofence == null) return;
    
    final merchantId = geofence.merchantId;
    if (merchantId == null) return;
    
    // 去重检查（同一门店5分钟内不重复触发）
    final cacheKey = '${merchantId}_${event.timestamp.millisecondsSinceEpoch ~/ 300000}';
    if (_triggeredVisits.contains(cacheKey)) return;
    _triggeredVisits.add(cacheKey);
    
    // 清理过期缓存
    _cleanupTriggeredCache();
    
    debugPrint('Store enter: $merchantId');
    
    // 获取商户信息
    final merchant = await _api.getMerchantDetail(merchantId);
    if (merchant == null) return;
    
    // 检查会员等级和权益
    final membershipBenefits = await _getMembershipBenefits(merchantId);
    
    // 生成个性化欢迎消息
    final welcomeMessage = _generateWelcomeMessage(merchant, membershipBenefits);
    
    // 获取个性化推荐
    final offers = await _getPersonalizedOffers(merchantId);
    
    // 发送本地通知
    await _sendWelcomeNotification(merchant, welcomeMessage, offers);
    
    // 记录到店
    final visitRecord = StoreVisitRecord(
      id: 'visit_${DateTime.now().millisecondsSinceEpoch}',
      merchantId: merchantId,
      merchantName: merchant.name,
      merchantLogo: merchant.logo,
      enterTime: event.timestamp,
      exitTime: null,
      location: VisitLocation(
        latitude: event.position.latitude,
        longitude: event.position.longitude,
        address: geofence.metadata?['address'] as String?,
      ),
      triggeredOffers: offers,
      membershipBenefitsUsed: membershipBenefits,
    );
    
    _recentVisits.insert(0, visitRecord);
    if (_recentVisits.length > 50) {
      _recentVisits.removeLast();
    }
    
    // 上报服务器
    await _api.recordStoreVisit(visitRecord);
    
    notifyListeners();
  }
  
  /// 处理在门店停留
  Future<void> _handleStoreDwell(GeofenceTriggerEvent event) async {
    final geofence = _geofenceManager.getGeofence(event.geofenceId);
    if (geofence == null) return;
    
    final merchantId = geofence.merchantId;
    if (merchantId == null) return;
    
    debugPrint('Store dwell: $merchantId, duration: ${event.dwellDuration}ms');
    
    // 长时间停留触发深度服务推荐
    if (event.dwellDuration != null && event.dwellDuration! > 600000) {
      // 停留超过10分钟
      await _sendDwellNotification(merchantId, event.dwellDuration!);
    }
  }
  
  /// 处理离开门店
  Future<void> _handleStoreExit(GeofenceTriggerEvent event) async {
    final geofence = _geofenceManager.getGeofence(event.geofenceId);
    if (geofence == null) return;
    
    final merchantId = geofence.merchantId;
    if (merchantId == null) return;
    
    debugPrint('Store exit: $merchantId');
    
    // 更新最近到店记录的离开时间
    final record = _recentVisits.firstWhere(
      (v) => v.merchantId == merchantId && v.exitTime == null,
      orElse: () => null as StoreVisitRecord,
    );
    
    if (record != null) {
      final index = _recentVisits.indexOf(record);
      _recentVisits[index] = record.copyWith(exitTime: event.timestamp);
      
      // 计算停留时长
      final duration = event.timestamp.difference(record.enterTime);
      
      // 发送感谢通知
      await _sendExitNotification(record, duration);
      
      // 邀请评价
      if (duration.inMinutes >= 10) {
        await _sendReviewInvitation(record);
      }
    }
    
    notifyListeners();
  }

  // ==================== 个性化服务 ====================
  
  /// 获取会员权益
  Future<List<MembershipBenefit>> _getMembershipBenefits(String merchantId) async {
    if (_currentUser == null) return [];
    
    try {
      final benefits = await _api.getMembershipBenefits(
        userId: _currentUser!.id,
        merchantId: merchantId,
      );
      return benefits.where((b) => !b.isUsed && !b.isExpired).toList();
    } catch (e) {
      debugPrint('Error getting membership benefits: $e');
      return [];
    }
  }
  
  /// 获取个性化优惠
  Future<List<PersonalizedOffer>> _getPersonalizedOffers(String merchantId) async {
    // 检查缓存
    final cached = _offerCache[merchantId];
    if (cached != null && cached.isNotEmpty) {
      return cached;
    }
    
    if (_currentUser == null) return [];
    
    try {
      final offers = await _api.getPersonalizedOffers(
        userId: _currentUser!.id,
        merchantId: merchantId,
        location: _geofenceManager.lastPosition != null
            ? {
                'lat': _geofenceManager.lastPosition!.latitude,
                'lng': _geofenceManager.lastPosition!.longitude,
              }
            : null,
      );
      
      // 缓存结果
      _offerCache[merchantId] = offers;
      
      return offers;
    } catch (e) {
      debugPrint('Error getting personalized offers: $e');
      return [];
    }
  }
  
  /// 生成欢迎消息
  String _generateWelcomeMessage(
    Merchant merchant,
    List<MembershipBenefit> benefits,
  ) {
    final userLevel = _currentUser?.membershipLevel ?? '普通会员';
    
    if (benefits.isNotEmpty) {
      final benefitNames = benefits.take(2).map((b) => b.name).join('、');
      return '欢迎$userLevel来到${merchant.name}！您有$benefitNames待领取';
    }
    
    return '欢迎光临${merchant.name}！${merchant.welcomeMessage ?? '期待为您服务'}';
  }

  // ==================== 通知发送 ====================
  
  /// 发送欢迎通知
  Future<void> _sendWelcomeNotification(
    Merchant merchant,
    String message,
    List<PersonalizedOffer> offers,
  ) async {
    // 构建通知内容
    String body = message;
    if (offers.isNotEmpty) {
      final topOffer = offers.first;
      body += '\n🎁 ${topOffer.title}';
      if (offers.length > 1) {
        body += ' 等${offers.length}个专属优惠';
      }
    }
    
    await LocalNotifications.show(
      id: merchant.id.hashCode,
      title: '🏪 ${merchant.name}',
      body: body,
      payload: jsonEncode({
        'type': 'store_welcome',
        'merchantId': merchant.id,
        'offers': offers.map((o) => o.id).toList(),
      }),
    );
  }
  
  /// 发送停留通知
  Future<void> _sendDwellNotification(String merchantId, int dwellDuration) async {
    final merchant = await _api.getMerchantDetail(merchantId);
    if (merchant == null) return;
    
    final minutes = dwellDuration ~/ 60000;
    
    await LocalNotifications.show(
      id: merchantId.hashCode + 1,
      title: '⏰ 在${merchant.name}已停留${minutes}分钟',
      body: '需要叫车回家吗？或查看附近其他好去处',
      payload: jsonEncode({
        'type': 'store_dwell',
        'merchantId': merchantId,
        'dwellMinutes': minutes,
      }),
    );
  }
  
  /// 发送离开通知
  Future<void> _sendExitNotification(StoreVisitRecord record, Duration duration) async {
    final minutes = duration.inMinutes;
    
    await LocalNotifications.show(
      id: record.merchantId.hashCode + 2,
      title: '👋 感谢光临${record.merchantName}',
      body: '本次停留约$minutes分钟，期待您的下次到来',
      payload: jsonEncode({
        'type': 'store_exit',
        'merchantId': record.merchantId,
        'visitId': record.id,
      }),
    );
  }
  
  /// 发送评价邀请
  Future<void> _sendReviewInvitation(StoreVisitRecord record) async {
    await LocalNotifications.show(
      id: record.merchantId.hashCode + 3,
      title: '⭐ 对${record.merchantName}满意吗？',
      body: '分享您的体验，帮助更多朋友发现好店',
      payload: jsonEncode({
        'type': 'review_invitation',
        'merchantId': record.merchantId,
        'visitId': record.id,
      }),
    );
  }

  // ==================== 工具方法 ====================
  
  /// 清理触发缓存
  void _cleanupTriggeredCache() {
    final now = DateTime.now();
    final expired = <String>[];
    
    for (final key in _triggeredVisits) {
      final parts = key.split('_');
      if (parts.length == 2) {
        final timestamp = int.tryParse(parts[1]);
        if (timestamp != null) {
          final time = DateTime.fromMillisecondsSinceEpoch(timestamp * 300000);
          if (now.difference(time).inHours > 24) {
            expired.add(key);
          }
        }
      }
    }
    
    for (final key in expired) {
      _triggeredVisits.remove(key);
    }
  }
  
  /// 清除缓存
  void clearCache() {
    _offerCache.clear();
    notifyListeners();
  }
  
  /// 销毁
  @override
  void dispose() {
    _eventSubscription?.cancel();
    super.dispose();
  }
}

/// 到店记录
class StoreVisitRecord {
  final String id;
  final String merchantId;
  final String merchantName;
  final String? merchantLogo;
  final DateTime enterTime;
  final DateTime? exitTime;
  final VisitLocation location;
  final List<PersonalizedOffer> triggeredOffers;
  final List<MembershipBenefit> membershipBenefitsUsed;

  StoreVisitRecord({
    required this.id,
    required this.merchantId,
    required this.merchantName,
    this.merchantLogo,
    required this.enterTime,
    this.exitTime,
    required this.location,
    required this.triggeredOffers,
    required this.membershipBenefitsUsed,
  });

  Duration? get duration => exitTime != null
      ? exitTime!.difference(enterTime)
      : null;

  StoreVisitRecord copyWith({
    String? id,
    String? merchantId,
    String? merchantName,
    String? merchantLogo,
    DateTime? enterTime,
    DateTime? exitTime,
    VisitLocation? location,
    List<PersonalizedOffer>? triggeredOffers,
    List<MembershipBenefit>? membershipBenefitsUsed,
  }) {
    return StoreVisitRecord(
      id: id ?? this.id,
      merchantId: merchantId ?? this.merchantId,
      merchantName: merchantName ?? this.merchantName,
      merchantLogo: merchantLogo ?? this.merchantLogo,
      enterTime: enterTime ?? this.enterTime,
      exitTime: exitTime ?? this.exitTime,
      location: location ?? this.location,
      triggeredOffers: triggeredOffers ?? this.triggeredOffers,
      membershipBenefitsUsed: membershipBenefitsUsed ?? this.membershipBenefitsUsed,
    );
  }
}

/// 到店位置
class VisitLocation {
  final double latitude;
  final double longitude;
  final String? address;

  VisitLocation({
    required this.latitude,
    required this.longitude,
    this.address,
  });
}

/// 个性化优惠
class PersonalizedOffer {
  final String id;
  final String title;
  final String? description;
  final String? discountInfo;
  final DateTime? validUntil;
  final bool isClaimed;

  PersonalizedOffer({
    required this.id,
    required this.title,
    this.description,
    this.discountInfo,
    this.validUntil,
    this.isClaimed = false,
  });
}

/// 会员权益
class MembershipBenefit {
  final String id;
  final String name;
  final String? description;
  final bool isUsed;
  final bool isExpired;

  MembershipBenefit({
    required this.id,
    required this.name,
    this.description,
    this.isUsed = false,
    this.isExpired = false,
  });
}

/// 本地通知插件占位（实际项目中使用flutter_local_notifications）
class LocalNotifications {
  static Future<void> initialize() async {}
  
  static Future<void> show({
    required int id,
    required String title,
    required String body,
    String? payload,
  }) async {
    debugPrint('📱 Notification [$id]: $title - $body');
  }
}
