import 'dart:convert';
import 'dart:async';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:http/http.dart' as http;
import 'package:web_socket_channel/web_socket_channel.dart';
import '../../models/live/live_room_model.dart';
import '../../models/live/live_product_model.dart';
import '../../models/live/live_message_model.dart';
import '../../models/live/live_gift_model.dart';
import '../../utils/logger.dart';
import '../../config/api_config.dart';

/// 小程序直播服务
/// 提供直播推流、播放、互动等核心功能
class MiniProgramLiveService extends ChangeNotifier {
  static final MiniProgramLiveService _instance = MiniProgramLiveService._internal();
  factory MiniProgramLiveService() => _instance;
  MiniProgramLiveService._internal();

  // 直播状态
  LiveRoomStatus _currentStatus = LiveRoomStatus.idle;
  LiveRoomStatus get currentStatus => _currentStatus;

  // 当前直播间信息
  LiveRoomModel? _currentRoom;
  LiveRoomModel? get currentRoom => _currentRoom;

  // WebSocket连接
  WebSocketChannel? _webSocketChannel;
  
  // 消息流控制器
  final StreamController<LiveMessageModel> _messageController = 
      StreamController<LiveMessageModel>.broadcast();
  Stream<LiveMessageModel> get messageStream => _messageController.stream;

  // 在线观众数
  int _onlineCount = 0;
  int get onlineCount => _onlineCount;

  // 点赞数
  int _likeCount = 0;
  int get likeCount => _likeCount;

  // 商品列表
  List<LiveProductModel> _products = [];
  List<LiveProductModel> get products => List.unmodifiable(_products);

  // 当前讲解商品
  LiveProductModel? _currentProduct;
  LiveProductModel? get currentProduct => _currentProduct;

  // 直播时长
  Timer? _liveDurationTimer;
  Duration _liveDuration = Duration.zero;
  Duration get liveDuration => _liveDuration;

  // 推流配置
  LivePushConfig? _pushConfig;
  LivePushConfig? get pushConfig => _pushConfig;

  // 播放配置
  LivePlayConfig? _playConfig;
  LivePlayConfig? get playConfig => _playConfig;

  // 直播统计
  LiveStatistics _statistics = LiveStatistics.empty();
  LiveStatistics get statistics => _statistics;

  // 错误信息
  String? _errorMessage;
  String? get errorMessage => _errorMessage;

  // 是否正在重连
  bool _isReconnecting = false;
  bool get isReconnecting => _isReconnecting;

  // 重连次数
  int _reconnectAttempts = 0;
  static const int maxReconnectAttempts = 5;

  /// 初始化直播服务
  Future<void> initialize() async {
    Logger.log('MiniProgramLiveService', 'Initializing live service...');
    
    try {
      // 初始化直播SDK
      await _initializeLiveSDK();
      
      // 加载直播配置
      await _loadLiveConfig();
      
      Logger.log('MiniProgramLiveService', 'Live service initialized successfully');
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Failed to initialize live service', e, stackTrace);
      _errorMessage = '直播服务初始化失败: $e';
      notifyListeners();
    }
  }

  /// 初始化直播SDK
  Future<void> _initializeLiveSDK() async {
    // 微信小程序直播组件初始化
    // 调用原生方法初始化推流和播放SDK
    const platform = MethodChannel('com.im.live/channel');
    
    try {
      final result = await platform.invokeMethod('initializeLiveSDK', {
        'appId': ApiConfig.appId,
        'liveAppId': ApiConfig.liveAppId,
      });
      
      if (result != true) {
        throw Exception('Live SDK initialization failed');
      }
    } on PlatformException catch (e) {
      throw Exception('Platform exception: ${e.message}');
    }
  }

  /// 加载直播配置
  Future<void> _loadLiveConfig() async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/config'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        
        _pushConfig = LivePushConfig.fromJson(data['pushConfig']);
        _playConfig = LivePlayConfig.fromJson(data['playConfig']);
        
        Logger.log('MiniProgramLiveService', 'Live config loaded successfully');
      } else {
        throw Exception('Failed to load live config: ${response.statusCode}');
      }
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Failed to load live config', e, stackTrace);
      // 使用默认配置
      _pushConfig = LivePushConfig.defaultConfig();
      _playConfig = LivePlayConfig.defaultConfig();
    }
  }

  /// 创建直播间
  Future<LiveRoomModel> createLiveRoom({
    required String title,
    required String description,
    String? coverImage,
    List<String>? tags,
    bool isPublic = true,
    String? password,
    Map<String, dynamic>? extraData,
  }) async {
    Logger.log('MiniProgramLiveService', 'Creating live room: $title');
    
    try {
      final response = await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms'),
        headers: await ApiConfig.getHeaders(),
        body: json.encode({
          'title': title,
          'description': description,
          'coverImage': coverImage,
          'tags': tags,
          'isPublic': isPublic,
          'password': password,
          'extraData': extraData,
        }),
      );

      if (response.statusCode == 201 || response.statusCode == 200) {
        final data = json.decode(response.body);
        _currentRoom = LiveRoomModel.fromJson(data);
        
        Logger.log('MiniProgramLiveService', 'Live room created: ${_currentRoom!.id}');
        notifyListeners();
        
        return _currentRoom!;
      } else {
        throw Exception('Failed to create live room: ${response.statusCode}');
      }
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Failed to create live room', e, stackTrace);
      _errorMessage = '创建直播间失败: $e';
      notifyListeners();
      rethrow;
    }
  }

  /// 开始推流（主播端）
  Future<void> startPushing() async {
    if (_currentRoom == null) {
      throw Exception('No live room created');
    }

    Logger.log('MiniProgramLiveService', 'Starting live push for room: ${_currentRoom!.id}');
    
    try {
      // 获取推流地址
      final pushUrl = await _getPushUrl(_currentRoom!.id);
      
      // 调用原生推流
      const platform = MethodChannel('com.im.live/channel');
      final result = await platform.invokeMethod('startLivePush', {
        'roomId': _currentRoom!.id,
        'pushUrl': pushUrl,
        'config': _pushConfig?.toJson(),
      });

      if (result == true) {
        _currentStatus = LiveRoomStatus.pushing;
        
        // 开始直播时长计时
        _startDurationTimer();
        
        // 连接WebSocket
        await _connectWebSocket();
        
        // 通知服务器直播开始
        await _notifyLiveStarted();
        
        Logger.log('MiniProgramLiveService', 'Live push started successfully');
        notifyListeners();
      } else {
        throw Exception('Failed to start live push');
      }
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Failed to start pushing', e, stackTrace);
      _errorMessage = '开始推流失败: $e';
      _currentStatus = LiveRoomStatus.error;
      notifyListeners();
      rethrow;
    }
  }

  /// 停止推流
  Future<void> stopPushing() async {
    Logger.log('MiniProgramLiveService', 'Stopping live push');
    
    try {
      const platform = MethodChannel('com.im.live/channel');
      await platform.invokeMethod('stopLivePush');

      // 停止计时
      _stopDurationTimer();
      
      // 断开WebSocket
      await _disconnectWebSocket();
      
      // 通知服务器直播结束
      await _notifyLiveEnded();
      
      _currentStatus = LiveRoomStatus.idle;
      Logger.log('MiniProgramLiveService', 'Live push stopped');
      notifyListeners();
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error stopping push', e, stackTrace);
    }
  }

  /// 开始播放（观众端）
  Future<void> startPlaying(String roomId) async {
    Logger.log('MiniProgramLiveService', 'Starting live play for room: $roomId');
    
    try {
      // 获取直播间信息
      final room = await _getRoomInfo(roomId);
      _currentRoom = room;
      
      // 获取播放地址
      final playUrl = await _getPlayUrl(roomId);
      
      // 调用原生播放
      const platform = MethodChannel('com.im.live/channel');
      final result = await platform.invokeMethod('startLivePlay', {
        'roomId': roomId,
        'playUrl': playUrl,
        'config': _playConfig?.toJson(),
      });

      if (result == true) {
        _currentStatus = LiveRoomStatus.playing;
        
        // 连接WebSocket
        await _connectWebSocket();
        
        // 进入直播间
        await _enterRoom(roomId);
        
        // 加载商品列表
        await _loadProducts(roomId);
        
        Logger.log('MiniProgramLiveService', 'Live play started successfully');
        notifyListeners();
      } else {
        throw Exception('Failed to start live play');
      }
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Failed to start playing', e, stackTrace);
      _errorMessage = '开始播放失败: $e';
      _currentStatus = LiveRoomStatus.error;
      notifyListeners();
      rethrow;
    }
  }

  /// 停止播放
  Future<void> stopPlaying() async {
    Logger.log('MiniProgramLiveService', 'Stopping live play');
    
    try {
      const platform = MethodChannel('com.im.live/channel');
      await platform.invokeMethod('stopLivePlay');

      // 断开WebSocket
      await _disconnectWebSocket();
      
      // 离开直播间
      if (_currentRoom != null) {
        await _leaveRoom(_currentRoom!.id);
      }
      
      _currentStatus = LiveRoomStatus.idle;
      _currentRoom = null;
      _products = [];
      _currentProduct = null;
      
      Logger.log('MiniProgramLiveService', 'Live play stopped');
      notifyListeners();
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error stopping play', e, stackTrace);
    }
  }

  /// 获取推流地址
  Future<String> _getPushUrl(String roomId) async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/push-url'),
      headers: await ApiConfig.getHeaders(),
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return data['pushUrl'];
    } else {
      throw Exception('Failed to get push URL');
    }
  }

  /// 获取播放地址
  Future<String> _getPlayUrl(String roomId) async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/play-url'),
      headers: await ApiConfig.getHeaders(),
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return data['playUrl'];
    } else {
      throw Exception('Failed to get play URL');
    }
  }

  /// 获取直播间信息
  Future<LiveRoomModel> _getRoomInfo(String roomId) async {
    final response = await http.get(
      Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId'),
      headers: await ApiConfig.getHeaders(),
    );

    if (response.statusCode == 200) {
      final data = json.decode(response.body);
      return LiveRoomModel.fromJson(data);
    } else {
      throw Exception('Failed to get room info');
    }
  }

  /// 连接WebSocket
  Future<void> _connectWebSocket() async {
    if (_currentRoom == null) return;

    try {
      final wsUrl = '${ApiConfig.wsUrl}/live/${_currentRoom!.id}';
      _webSocketChannel = WebSocketChannel.connect(Uri.parse(wsUrl));
      
      _webSocketChannel!.stream.listen(
        _onWebSocketMessage,
        onError: _onWebSocketError,
        onDone: _onWebSocketDone,
      );

      Logger.log('MiniProgramLiveService', 'WebSocket connected');
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'WebSocket connection failed', e, stackTrace);
    }
  }

  /// 断开WebSocket
  Future<void> _disconnectWebSocket() async {
    await _webSocketChannel?.sink.close();
    _webSocketChannel = null;
    Logger.log('MiniProgramLiveService', 'WebSocket disconnected');
  }

  /// WebSocket消息处理
  void _onWebSocketMessage(dynamic message) {
    try {
      final data = json.decode(message);
      final msg = LiveMessageModel.fromJson(data);
      
      _messageController.add(msg);
      
      // 处理不同类型的消息
      switch (msg.type) {
        case LiveMessageType.userCount:
          _onlineCount = msg.userCount ?? _onlineCount;
          notifyListeners();
          break;
        case LiveMessageType.like:
          _likeCount = msg.likeCount ?? _likeCount;
          notifyListeners();
          break;
        case LiveMessageType.product:
          _handleProductMessage(msg);
          break;
        case LiveMessageType.currentProduct:
          _updateCurrentProduct(msg);
          break;
        default:
          break;
      }
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error processing WebSocket message', e, stackTrace);
    }
  }

  /// WebSocket错误处理
  void _onWebSocketError(error) {
    Logger.error('MiniProgramLiveService', 'WebSocket error', error);
    _attemptReconnect();
  }

  /// WebSocket连接关闭
  void _onWebSocketDone() {
    Logger.log('MiniProgramLiveService', 'WebSocket connection closed');
    _attemptReconnect();
  }

  /// 尝试重连
  Future<void> _attemptReconnect() async {
    if (_isReconnecting || _reconnectAttempts >= maxReconnectAttempts) return;
    
    _isReconnecting = true;
    _reconnectAttempts++;
    notifyListeners();

    Logger.log('MiniProgramLiveService', 'Attempting to reconnect... ($_reconnectAttempts/$maxReconnectAttempts)');

    await Future.delayed(Duration(seconds: _reconnectAttempts * 2));
    
    try {
      await _connectWebSocket();
      _reconnectAttempts = 0;
      Logger.log('MiniProgramLiveService', 'Reconnected successfully');
    } catch (e) {
      Logger.error('MiniProgramLiveService', 'Reconnect failed', e);
    } finally {
      _isReconnecting = false;
      notifyListeners();
    }
  }

  /// 处理商品消息
  void _handleProductMessage(LiveMessageModel message) {
    if (message.product != null) {
      final index = _products.indexWhere((p) => p.id == message.product!.id);
      if (index >= 0) {
        _products[index] = message.product!;
      } else {
        _products.add(message.product!);
      }
      notifyListeners();
    }
  }

  /// 更新当前讲解商品
  void _updateCurrentProduct(LiveMessageModel message) {
    if (message.productId != null) {
      _currentProduct = _products.firstWhere(
        (p) => p.id == message.productId,
        orElse: () => null as LiveProductModel,
      );
      notifyListeners();
    }
  }

  /// 进入直播间
  Future<void> _enterRoom(String roomId) async {
    try {
      await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/enter'),
        headers: await ApiConfig.getHeaders(),
      );
    } catch (e) {
      Logger.error('MiniProgramLiveService', 'Error entering room', e);
    }
  }

  /// 离开直播间
  Future<void> _leaveRoom(String roomId) async {
    try {
      await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/leave'),
        headers: await ApiConfig.getHeaders(),
      );
    } catch (e) {
      Logger.error('MiniProgramLiveService', 'Error leaving room', e);
    }
  }

  /// 加载商品列表
  Future<void> _loadProducts(String roomId) async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/$roomId/products'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        _products = (data['products'] as List)
            .map((p) => LiveProductModel.fromJson(p))
            .toList();
        notifyListeners();
      }
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error loading products', e, stackTrace);
    }
  }

  /// 发送聊天消息
  Future<void> sendChatMessage(String content) async {
    if (_currentRoom == null) return;

    final message = {
      'type': 'chat',
      'content': content,
      'roomId': _currentRoom!.id,
      'timestamp': DateTime.now().millisecondsSinceEpoch,
    };

    _webSocketChannel?.sink.add(json.encode(message));
  }

  /// 发送点赞
  Future<void> sendLike() async {
    if (_currentRoom == null) return;

    final message = {
      'type': 'like',
      'roomId': _currentRoom!.id,
      'timestamp': DateTime.now().millisecondsSinceEpoch,
    };

    _webSocketChannel?.sink.add(json.encode(message));
    
    // 本地点赞数+1
    _likeCount++;
    notifyListeners();
  }

  /// 发送礼物
  Future<void> sendGift(LiveGiftModel gift) async {
    if (_currentRoom == null) return;

    try {
      // 先调用API扣费
      final response = await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/gifts/send'),
        headers: await ApiConfig.getHeaders(),
        body: json.encode({
          'roomId': _currentRoom!.id,
          'giftId': gift.id,
          'count': 1,
        }),
      );

      if (response.statusCode == 200) {
        // 发送礼物消息
        final message = {
          'type': 'gift',
          'giftId': gift.id,
          'giftName': gift.name,
          'giftIcon': gift.icon,
          'roomId': _currentRoom!.id,
          'timestamp': DateTime.now().millisecondsSinceEpoch,
        };

        _webSocketChannel?.sink.add(json.encode(message));
      } else {
        throw Exception('Failed to send gift');
      }
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error sending gift', e, stackTrace);
      rethrow;
    }
  }

  /// 切换讲解商品（主播端）
  Future<void> switchCurrentProduct(String productId) async {
    if (_currentRoom == null) return;

    try {
      await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/${_currentRoom!.id}/current-product'),
        headers: await ApiConfig.getHeaders(),
        body: json.encode({'productId': productId}),
      );

      // 更新本地状态
      _currentProduct = _products.firstWhere((p) => p.id == productId);
      notifyListeners();
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error switching product', e, stackTrace);
      rethrow;
    }
  }

  /// 上架商品（主播端）
  Future<void> addProduct(LiveProductModel product) async {
    if (_currentRoom == null) return;

    try {
      final response = await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/${_currentRoom!.id}/products'),
        headers: await ApiConfig.getHeaders(),
        body: json.encode(product.toJson()),
      );

      if (response.statusCode == 201) {
        final data = json.decode(response.body);
        final newProduct = LiveProductModel.fromJson(data);
        _products.add(newProduct);
        notifyListeners();
      } else {
        throw Exception('Failed to add product');
      }
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error adding product', e, stackTrace);
      rethrow;
    }
  }

  /// 下架商品（主播端）
  Future<void> removeProduct(String productId) async {
    if (_currentRoom == null) return;

    try {
      await http.delete(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/${_currentRoom!.id}/products/$productId'),
        headers: await ApiConfig.getHeaders(),
      );

      _products.removeWhere((p) => p.id == productId);
      
      if (_currentProduct?.id == productId) {
        _currentProduct = null;
      }
      
      notifyListeners();
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error removing product', e, stackTrace);
      rethrow;
    }
  }

  /// 开始直播时长计时
  void _startDurationTimer() {
    _liveDuration = Duration.zero;
    _liveDurationTimer = Timer.periodic(Duration(seconds: 1), (timer) {
      _liveDuration += Duration(seconds: 1);
      notifyListeners();
    });
  }

  /// 停止直播时长计时
  void _stopDurationTimer() {
    _liveDurationTimer?.cancel();
    _liveDurationTimer = null;
  }

  /// 通知服务器直播开始
  Future<void> _notifyLiveStarted() async {
    if (_currentRoom == null) return;

    try {
      await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/${_currentRoom!.id}/start'),
        headers: await ApiConfig.getHeaders(),
      );
    } catch (e) {
      Logger.error('MiniProgramLiveService', 'Error notifying live start', e);
    }
  }

  /// 通知服务器直播结束
  Future<void> _notifyLiveEnded() async {
    if (_currentRoom == null) return;

    try {
      await http.post(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/${_currentRoom!.id}/end'),
        headers: await ApiConfig.getHeaders(),
        body: json.encode({
          'duration': _liveDuration.inSeconds,
        }),
      );
    } catch (e) {
      Logger.error('MiniProgramLiveService', 'Error notifying live end', e);
    }
  }

  /// 暂停直播
  Future<void> pauseLive() async {
    if (_currentStatus != LiveRoomStatus.pushing) return;

    try {
      const platform = MethodChannel('com.im.live/channel');
      await platform.invokeMethod('pauseLivePush');
      
      _currentStatus = LiveRoomStatus.paused;
      notifyListeners();
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error pausing live', e, stackTrace);
    }
  }

  /// 恢复直播
  Future<void> resumeLive() async {
    if (_currentStatus != LiveRoomStatus.paused) return;

    try {
      const platform = MethodChannel('com.im.live/channel');
      await platform.invokeMethod('resumeLivePush');
      
      _currentStatus = LiveRoomStatus.pushing;
      notifyListeners();
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error resuming live', e, stackTrace);
    }
  }

  /// 切换摄像头
  Future<void> switchCamera() async {
    try {
      const platform = MethodChannel('com.im.live/channel');
      await platform.invokeMethod('switchCamera');
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error switching camera', e, stackTrace);
    }
  }

  /// 切换闪光灯
  Future<void> toggleFlash() async {
    try {
      const platform = MethodChannel('com.im.live/channel');
      await platform.invokeMethod('toggleFlash');
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error toggling flash', e, stackTrace);
    }
  }

  /// 切换美颜
  Future<void> toggleBeauty(bool enabled) async {
    try {
      const platform = MethodChannel('com.im.live/channel');
      await platform.invokeMethod('toggleBeauty', {'enabled': enabled});
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error toggling beauty', e, stackTrace);
    }
  }

  /// 设置美颜级别
  Future<void> setBeautyLevel(int level) async {
    try {
      const platform = MethodChannel('com.im.live/channel');
      await platform.invokeMethod('setBeautyLevel', {'level': level});
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error setting beauty level', e, stackTrace);
    }
  }

  /// 静音推流
  Future<void> toggleMute(bool muted) async {
    try {
      const platform = MethodChannel('com.im.live/channel');
      await platform.invokeMethod('toggleMute', {'muted': muted});
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error toggling mute', e, stackTrace);
    }
  }

  /// 获取直播统计
  Future<void> refreshStatistics() async {
    if (_currentRoom == null) return;

    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/${_currentRoom!.id}/statistics'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        _statistics = LiveStatistics.fromJson(data);
        notifyListeners();
      }
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error loading statistics', e, stackTrace);
    }
  }

  /// 获取直播间列表
  Future<List<LiveRoomModel>> getLiveRoomList({
    int page = 1,
    int pageSize = 20,
    String? category,
    String? keyword,
  }) async {
    try {
      final queryParams = <String, String>{
        'page': page.toString(),
        'pageSize': pageSize.toString(),
      };
      
      if (category != null) queryParams['category'] = category;
      if (keyword != null) queryParams['keyword'] = keyword;

      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms').replace(queryParameters: queryParams),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return (data['rooms'] as List)
            .map((r) => LiveRoomModel.fromJson(r))
            .toList();
      } else {
        throw Exception('Failed to get live room list');
      }
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error getting room list', e, stackTrace);
      rethrow;
    }
  }

  /// 搜索直播间
  Future<List<LiveRoomModel>> searchLiveRooms(String keyword) async {
    return getLiveRoomList(keyword: keyword);
  }

  /// 获取推荐直播间
  Future<List<LiveRoomModel>> getRecommendedRooms() async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/recommended'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return (data['rooms'] as List)
            .map((r) => LiveRoomModel.fromJson(r))
            .toList();
      } else {
        throw Exception('Failed to get recommended rooms');
      }
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error getting recommended rooms', e, stackTrace);
      rethrow;
    }
  }

  /// 获取我的直播间
  Future<List<LiveRoomModel>> getMyLiveRooms() async {
    try {
      final response = await http.get(
        Uri.parse('${ApiConfig.baseUrl}/api/v1/live/rooms/my'),
        headers: await ApiConfig.getHeaders(),
      );

      if (response.statusCode == 200) {
        final data = json.decode(response.body);
        return (data['rooms'] as List)
            .map((r) => LiveRoomModel.fromJson(r))
            .toList();
      } else {
        throw Exception('Failed to get my rooms');
      }
    } catch (e, stackTrace) {
      Logger.error('MiniProgramLiveService', 'Error getting my rooms', e, stackTrace);
      rethrow;
    }
  }

  /// 清理资源
  @override
  void dispose() {
    _stopDurationTimer();
    _disconnectWebSocket();
    _messageController.close();
    super.dispose();
  }
}

/// 直播间状态枚举
enum LiveRoomStatus {
  idle,        // 空闲
  pushing,     // 推流中（主播）
  playing,     // 播放中（观众）
  paused,      // 暂停
  connecting,  // 连接中
  error,       // 错误
}

/// 推流配置
class LivePushConfig {
  final int resolution;      // 分辨率
  final int bitrate;         // 码率
  final int frameRate;       // 帧率
  final bool enableBeauty;   // 是否开启美颜
  final int beautyLevel;     // 美颜级别
  final bool enableNoiseReduction; // 是否开启降噪

  LivePushConfig({
    this.resolution = 720,
    this.bitrate = 1500,
    this.frameRate = 30,
    this.enableBeauty = true,
    this.beautyLevel = 3,
    this.enableNoiseReduction = true,
  });

  factory LivePushConfig.fromJson(Map<String, dynamic> json) {
    return LivePushConfig(
      resolution: json['resolution'] ?? 720,
      bitrate: json['bitrate'] ?? 1500,
      frameRate: json['frameRate'] ?? 30,
      enableBeauty: json['enableBeauty'] ?? true,
      beautyLevel: json['beautyLevel'] ?? 3,
      enableNoiseReduction: json['enableNoiseReduction'] ?? true,
    );
  }

  Map<String, dynamic> toJson() => {
    'resolution': resolution,
    'bitrate': bitrate,
    'frameRate': frameRate,
    'enableBeauty': enableBeauty,
    'beautyLevel': beautyLevel,
    'enableNoiseReduction': enableNoiseReduction,
  };

  factory LivePushConfig.defaultConfig() => LivePushConfig();
}

/// 播放配置
class LivePlayConfig {
  final bool enableHardwareDecode;  // 硬件解码
  final int bufferTime;             // 缓冲时间
  final bool autoPlay;              // 自动播放

  LivePlayConfig({
    this.enableHardwareDecode = true,
    this.bufferTime = 1000,
    this.autoPlay = true,
  });

  factory LivePlayConfig.fromJson(Map<String, dynamic> json) {
    return LivePlayConfig(
      enableHardwareDecode: json['enableHardwareDecode'] ?? true,
      bufferTime: json['bufferTime'] ?? 1000,
      autoPlay: json['autoPlay'] ?? true,
    );
  }

  Map<String, dynamic> toJson() => {
    'enableHardwareDecode': enableHardwareDecode,
    'bufferTime': bufferTime,
    'autoPlay': autoPlay,
  };

  factory LivePlayConfig.defaultConfig() => LivePlayConfig();
}

/// 直播统计
class LiveStatistics {
  final int totalViewers;      // 总观看人数
  final int peakViewers;       // 峰值观看人数
  final int totalLikes;        // 总点赞数
  final double totalGifts;     // 总礼物价值
  final int totalProducts;     // 商品总数
  final int soldProducts;      // 已售商品数
  final double totalSales;     // 总销售额
  final Duration duration;     // 直播时长

  LiveStatistics({
    this.totalViewers = 0,
    this.peakViewers = 0,
    this.totalLikes = 0,
    this.totalGifts = 0.0,
    this.totalProducts = 0,
    this.soldProducts = 0,
    this.totalSales = 0.0,
    this.duration = Duration.zero,
  });

  factory LiveStatistics.fromJson(Map<String, dynamic> json) {
    return LiveStatistics(
      totalViewers: json['totalViewers'] ?? 0,
      peakViewers: json['peakViewers'] ?? 0,
      totalLikes: json['totalLikes'] ?? 0,
      totalGifts: (json['totalGifts'] ?? 0.0).toDouble(),
      totalProducts: json['totalProducts'] ?? 0,
      soldProducts: json['soldProducts'] ?? 0,
      totalSales: (json['totalSales'] ?? 0.0).toDouble(),
      duration: Duration(seconds: json['duration'] ?? 0),
    );
  }

  Map<String, dynamic> toJson() => {
    'totalViewers': totalViewers,
    'peakViewers': peakViewers,
    'totalLikes': totalLikes,
    'totalGifts': totalGifts,
    'totalProducts': totalProducts,
    'soldProducts': soldProducts,
    'totalSales': totalSales,
    'duration': duration.inSeconds,
  };

  factory LiveStatistics.empty() => LiveStatistics();
}
