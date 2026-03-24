/// 数字孪生虚拟会议空间 API 服务
/// 提供与后端 API 的交互接口
/// 
/// @since 2026-03-23
/// @version 1.0.0
library;

import 'dart:convert';
import 'package:http/http.dart' as http;
import 'models/digital_twin_virtual_space_model.dart';

/// API 基础配置
const String _apiBaseUrl = String.fromEnvironment(
  'API_BASE_URL',
  defaultValue: 'http://localhost:8080/api/v1',
);
const String _digitalTwinEndpoint = '$_apiBaseUrl/digital-twin';

/// HTTP 请求工具类
class HttpClient {
  static Future<ApiResponse<T>> request<T>({
    required String endpoint,
    required String method,
    Map<String, dynamic>? data,
    Map<String, String>? headers,
    T Function(Map<String, dynamic>)? fromJson,
  }) async {
    final url = Uri.parse('$_digitalTwinEndpoint$endpoint');
    
    final requestHeaders = {
      'Content-Type': 'application/json',
      ...?headers,
    };
    
    http.Response response;
    
    switch (method.toUpperCase()) {
      case 'GET':
        response = await http.get(url, headers: requestHeaders);
        break;
      case 'POST':
        response = await http.post(
          url,
          headers: requestHeaders,
          body: data != null ? jsonEncode(data) : null,
        );
        break;
      case 'PUT':
        response = await http.put(
          url,
          headers: requestHeaders,
          body: data != null ? jsonEncode(data) : null,
        );
        break;
      case 'DELETE':
        response = await http.delete(url, headers: requestHeaders);
        break;
      default:
        throw Exception('Unsupported HTTP method: $method');
    }
    
    final result = jsonDecode(response.body) as Map<String, dynamic>;
    
    if (response.statusCode >= 400) {
      throw Exception(result['message'] ?? 'Request failed');
    }
    
    return ApiResponse<T>.fromJson(result, fromJson);
  }
}

/// 数字孪生虚拟空间服务类
class DigitalTwinService {
  /// 创建虚拟空间
  static Future<ApiResponse<Map<String, dynamic>>> createVirtualSpace({
    required String spaceName,
    required String spaceType,
    required String hostUserId,
    Map<String, dynamic>? config,
  }) async {
    final requestData = {
      'spaceName': spaceName,
      'spaceType': spaceType,
      'hostUserId': hostUserId,
      if (config != null) 'config': config,
    };
    
    return HttpClient.request<Map<String, dynamic>>(
      endpoint: '/spaces',
      method: 'POST',
      data: requestData,
      fromJson: (json) => json,
    );
  }
  
  /// 获取虚拟空间详情
  static Future<ApiResponse<VirtualSpaceInfo>> getVirtualSpace(String spaceId) async {
    return HttpClient.request<VirtualSpaceInfo>(
      endpoint: '/spaces/$spaceId',
      method: 'GET',
      fromJson: (json) => VirtualSpaceInfo.fromJson(json['space'] as Map<String, dynamic>),
    );
  }
  
  /// 更新虚拟空间
  static Future<ApiResponse<Map<String, dynamic>>> updateVirtualSpace({
    required String spaceId,
    required Map<String, dynamic> updates,
  }) async {
    return HttpClient.request<Map<String, dynamic>>(
      endpoint: '/spaces/$spaceId',
      method: 'PUT',
      data: updates,
      fromJson: (json) => json,
    );
  }
  
  /// 删除虚拟空间
  static Future<ApiResponse<Map<String, dynamic>>> deleteVirtualSpace(String spaceId) async {
    return HttpClient.request<Map<String, dynamic>>(
      endpoint: '/spaces/$spaceId',
      method: 'DELETE',
      fromJson: (json) => json,
    );
  }
  
  /// 用户加入虚拟空间
  static Future<ApiResponse<Map<String, dynamic>>> joinVirtualSpace({
    required String spaceId,
    required String userId,
    String? avatarId,
  }) async {
    final queryParams = {
      'userId': userId,
      if (avatarId != null) 'avatarId': avatarId,
    };
    
    final endpoint = '/spaces/$spaceId/join?${Uri(queryParameters: queryParams).query}';
    
    return HttpClient.request<Map<String, dynamic>>(
      endpoint: endpoint,
      method: 'POST',
      fromJson: (json) => json,
    );
  }
  
  /// 用户离开虚拟空间
  static Future<ApiResponse<Map<String, dynamic>>> leaveVirtualSpace({
    required String spaceId,
    required String userId,
  }) async {
    return HttpClient.request<Map<String, dynamic>>(
      endpoint: '/spaces/$spaceId/leave',
      method: 'POST',
      data: {'userId': userId},
      fromJson: (json) => json,
    );
  }
  
  /// 配置空间音频
  static Future<ApiResponse<Map<String, dynamic>>> configureSpatialAudio({
    required String spaceId,
    required Map<String, dynamic> audioConfig,
  }) async {
    return HttpClient.request<Map<String, dynamic>>(
      endpoint: '/spaces/$spaceId/audio',
      method: 'POST',
      data: audioConfig,
      fromJson: (json) => json,
    );
  }
  
  /// 创建虚拟化身
  static Future<ApiResponse<Map<String, dynamic>>> createAvatar({
    required String userId,
    required String avatarName,
    Map<String, dynamic>? config,
  }) async {
    final requestData = {
      'userId': userId,
      'avatarName': avatarName,
      if (config != null) 'config': config,
    };
    
    return HttpClient.request<Map<String, dynamic>>(
      endpoint: '/avatars',
      method: 'POST',
      data: requestData,
      fromJson: (json) => json,
    );
  }
  
  /// 获取用户的所有化身
  static Future<ApiResponse<List<VirtualAvatarInfo>>> getUserAvatars(String userId) async {
    return HttpClient.request<List<VirtualAvatarInfo>>(
      endpoint: '/avatars?userId=$userId',
      method: 'GET',
      fromJson: (json) {
        final avatars = json['avatars'] as List;
        return avatars
            .map((a) => VirtualAvatarInfo.fromJson(a as Map<String, dynamic>))
            .toList();
      },
    );
  }
  
  /// 更新虚拟化身
  static Future<ApiResponse<Map<String, dynamic>>> updateAvatar({
    required String avatarId,
    required Map<String, dynamic> updates,
  }) async {
    return HttpClient.request<Map<String, dynamic>>(
      endpoint: '/avatars/$avatarId',
      method: 'PUT',
      data: updates,
      fromJson: (json) => json,
    );
  }
  
  /// 配置 AR/VR 集成
  static Future<ApiResponse<Map<String, dynamic>>> configureArVrIntegration({
    required String spaceId,
    required Map<String, dynamic> vrConfig,
  }) async {
    return HttpClient.request<Map<String, dynamic>>(
      endpoint: '/spaces/$spaceId/arvr',
      method: 'POST',
      data: vrConfig,
      fromJson: (json) => json,
    );
  }
  
  /// 配置场景模拟
  static Future<ApiResponse<Map<String, dynamic>>> configureSceneSimulation({
    required String spaceId,
    required Map<String, dynamic> sceneConfig,
  }) async {
    return HttpClient.request<Map<String, dynamic>>(
      endpoint: '/spaces/$spaceId/scenes',
      method: 'POST',
      data: sceneConfig,
      fromJson: (json) => json,
    );
  }
  
  /// 配置协作工具
  static Future<ApiResponse<Map<String, dynamic>>> configureCollaborationTools({
    required String spaceId,
    required Map<String, dynamic> toolsConfig,
  }) async {
    return HttpClient.request<Map<String, dynamic>>(
      endpoint: '/spaces/$spaceId/collaboration',
      method: 'POST',
      data: toolsConfig,
      fromJson: (json) => json,
    );
  }
  
  /// 搜索虚拟空间
  static Future<ApiResponse<List<VirtualSpaceInfo>>> searchSpaces({
    String? keyword,
    String? spaceType,
    int? minCapacity,
    bool? hasArVr,
  }) async {
    final queryParams = <String, String>{};
    if (keyword != null) queryParams['keyword'] = keyword;
    if (spaceType != null) queryParams['spaceType'] = spaceType;
    if (minCapacity != null) queryParams['minCapacity'] = minCapacity.toString();
    if (hasArVr != null) queryParams['hasArVr'] = hasArVr.toString();
    
    return HttpClient.request<List<VirtualSpaceInfo>>(
      endpoint: '/spaces/search?${Uri(queryParameters: queryParams).query}',
      method: 'GET',
      fromJson: (json) {
        final spaces = json['spaces'] as List;
        return spaces
            .map((s) => VirtualSpaceInfo.fromJson(s as Map<String, dynamic>))
            .toList();
      },
    );
  }
  
  /// 获取活跃空间列表
  static Future<ApiResponse<List<VirtualSpaceInfo>>> getActiveSpaces() async {
    return HttpClient.request<List<VirtualSpaceInfo>>(
      endpoint: '/spaces/active',
      method: 'GET',
      fromJson: (json) {
        final spaces = json['spaces'] as List;
        return spaces
            .map((s) => VirtualSpaceInfo.fromJson(s as Map<String, dynamic>))
            .toList();
      },
    );
  }
  
  /// 获取空间统计信息
  static Future<ApiResponse<SpaceStatistics>> getSpaceStatistics(String spaceId) async {
    return HttpClient.request<SpaceStatistics>(
      endpoint: '/spaces/$spaceId/statistics',
      method: 'GET',
      fromJson: (json) => SpaceStatistics.fromJson(json['statistics'] as Map<String, dynamic>),
    );
  }
  
  /// 获取支持的 VR 设备列表
  static Future<ApiResponse<List<Map<String, dynamic>>>> getSupportedVrDevices() async {
    return HttpClient.request<List<Map<String, dynamic>>>(
      endpoint: '/vr-devices',
      method: 'GET',
      fromJson: (json) {
        final devices = json['devices'] as List;
        return devices.map((d) => d as Map<String, dynamic>).toList();
      },
    );
  }
  
  /// 获取支持的场景类型
  static Future<ApiResponse<List<Map<String, dynamic>>>> getSupportedSceneTypes() async {
    return HttpClient.request<List<Map<String, dynamic>>>(
      endpoint: '/scene-types',
      method: 'GET',
      fromJson: (json) {
        final sceneTypes = json['sceneTypes'] as List;
        return sceneTypes.map((s) => s as Map<String, dynamic>).toList();
      },
    );
  }
}

/// WebSocket 连接管理
class VirtualSpaceWebSocket {
  String? _spaceId;
  String? _userId;
  int _reconnectAttempts = 0;
  final int _maxReconnectAttempts = 5;
  final int _reconnectDelay = 1000;
  final Map<String, List<Function(Map<String, dynamic>)>> _eventListeners = {};
  
  VirtualSpaceWebSocket(this._userId);
  
  /// 连接到虚拟空间
  Future<void> connect(String spaceId, String token) async {
    _spaceId = spaceId;
    _reconnectAttempts = 0;
    
    // 注意：Flutter 中需要使用 websocket_channel 包
    // 这里提供接口定义，实际实现需要集成 websocket_channel
    print('Connecting to virtual space: $spaceId');
    print('WebSocket URL: ws://localhost:8080/ws/digital-twin/$spaceId?token=$token&userId=$_userId');
    
    // 实际实现示例:
    // final channel = WebSocketChannel.connect(
    //   Uri.parse('ws://localhost:8080/ws/digital-twin/$spaceId?token=$token&userId=$_userId'),
    // );
    // 
    // channel.stream.listen((message) {
    //   _handleMessage(message);
    // }, onDone: () {
    //   _attemptReconnect();
    // });
  }
  
  /// 断开连接
  void disconnect() {
    _spaceId = null;
    print('Disconnected from virtual space');
  }
  
  /// 发送事件
  void sendEvent(String eventType, Map<String, dynamic>? data) {
    if (_spaceId == null) {
      print('WebSocket not connected');
      return;
    }
    
    final event = {
      'spaceId': _spaceId,
      'eventType': eventType,
      'userId': _userId,
      'data': data,
      'timestamp': DateTime.now().toIso8601String(),
    };
    
    print('Sending event: $eventType');
    // 实际实现: channel.sink.add(jsonEncode(event));
  }
  
  /// 更新用户位置
  void updatePosition({
    required double x,
    required double y,
    required double z,
    required double rx,
    required double ry,
    required double rz,
  }) {
    sendEvent('USER_JOINED', {
      'position': {'x': x, 'y': y, 'z': z},
      'rotation': {'x': rx, 'y': ry, 'z': rz},
    });
  }
  
  /// 切换语音状态
  void setVoiceActive(bool active) {
    sendEvent('USER_JOINED', {'voiceActive': active});
  }
  
  /// 更改化身表情
  void setExpression(String expression) {
    sendEvent('AVATAR_CHANGED', {'expressionState': expression});
  }
  
  /// 切换 VR 模式
  void toggleVrMode(bool enabled) {
    sendEvent('VR_MODE_TOGGLED', {'vrEnabled': enabled});
  }
  
  /// 监听事件
  void on(String eventType, Function(Map<String, dynamic>) callback) {
    _eventListeners.putIfAbsent(eventType, () => []);
    _eventListeners[eventType]!.add(callback);
  }
  
  /// 移除事件监听
  void off(String eventType, Function(Map<String, dynamic>) callback) {
    _eventListeners[eventType]?.remove(callback);
  }
  
  /// 处理接收到的消息
  void _handleMessage(String message) {
    try {
      final data = jsonDecode(message) as Map<String, dynamic>;
      final eventType = data['eventType'] as String;
      
      final listeners = _eventListeners[eventType];
      if (listeners != null) {
        for (final callback in listeners) {
          callback(data);
        }
      }
    } catch (e) {
      print('Failed to parse WebSocket message: $e');
    }
  }
  
  /// 尝试重新连接
  void _attemptReconnect() {
    if (_reconnectAttempts >= _maxReconnectAttempts || _spaceId == null) {
      print('Max reconnect attempts reached');
      return;
    }
    
    _reconnectAttempts++;
    final delay = _reconnectDelay * (2 ^ (_reconnectAttempts - 1));
    
    print('Attempting to reconnect in ${delay}ms (attempt $_reconnectAttempts/$_maxReconnectAttempts)');
    
    Future.delayed(Duration(milliseconds: delay), () {
      if (_spaceId != null) {
        connect(_spaceId!, 'reconnect-token');
      }
    });
  }
}

/// 默认配置常量
class DigitalTwinConstants {
  static const int defaultMaxCapacity = 50;
  static const bool defaultSpatialAudioEnabled = true;
  static const bool defaultVirtualAvatarEnabled = true;
  static const bool defaultCollaborationToolsEnabled = true;
  static const String defaultEnvironmentStyle = 'modern';
  static const String defaultReverbSettings = 'mediumRoom';
  static const String defaultAvatarCustomizationLevel = 'basic';
  static const String defaultPhysicsEngine = 'basic';
  static const String defaultLightingSystem = 'dynamic';
  static const double defaultAvatarHeight = 1.75;
  static const double defaultAvatarWeight = 70.0;
  static const String defaultAvatarStatus = 'ACTIVE';
}