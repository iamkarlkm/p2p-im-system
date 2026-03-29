import 'dart:convert';
import 'package:http/http.dart' as http;
import 'package:latlong2/latlong.dart';

import '../models/navigation_models.dart';
import '../config/api_config.dart';

/// 导航服务
/// Navigation Service
class NavigationService {
  final http.Client _client;

  NavigationService({http.Client? client}) : _client = client ?? http.Client();

  /// 路线规划
  Future<RoutePlan> planRoute({
    required LatLng origin,
    required LatLng destination,
    String originName = '',
    String destinationName = '',
    NavMode navMode = NavMode.driving,
    RouteStrategy strategy = RouteStrategy.fastest,
    List<Waypoint>? waypoints,
    bool avoidHighway = false,
    bool avoidToll = false,
    bool avoidCongestion = false,
  }) async {
    final response = await _client.post(
      Uri.parse('${ApiConfig.baseUrl}/navigation/route-planning'),
      headers: await _getHeaders(),
      body: jsonEncode({
        'originLng': origin.longitude,
        'originLat': origin.latitude,
        'destinationLng': destination.longitude,
        'destinationLat': destination.latitude,
        'originName': originName,
        'destinationName': destinationName,
        'navMode': navMode.value,
        'routeStrategy': strategy.value,
        'waypoints': waypoints?.map((w) => w.toJson()).toList(),
        'avoidHighway': avoidHighway,
        'avoidToll': avoidToll,
        'avoidCongestion': avoidCongestion,
      }),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return RoutePlan.fromJson(data['data']);
    } else {
      throw Exception('路线规划失败: ${response.statusCode}');
    }
  }

  /// 开始导航
  Future<NavigationStatus> startNavigation(int routeId) async {
    final response = await _client.post(
      Uri.parse('${ApiConfig.baseUrl}/navigation/routes/$routeId/start'),
      headers: await _getHeaders(),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return NavigationStatus.fromJson(data['data']);
    } else {
      throw Exception('开始导航失败: ${response.statusCode}');
    }
  }

  /// 更新位置
  Future<NavigationStatus> updateLocation({
    required int sessionId,
    required double lng,
    required double lat,
    double? speed,
    int? heading,
  }) async {
    final response = await _client.post(
      Uri.parse('${ApiConfig.baseUrl}/navigation/sessions/$sessionId/location-update'),
      headers: await _getHeaders(),
      body: jsonEncode({
        'lng': lng,
        'lat': lat,
        'speed': speed,
        'heading': heading,
      }),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return NavigationStatus.fromJson(data['data']);
    } else {
      throw Exception('位置更新失败: ${response.statusCode}');
    }
  }

  /// 获取导航状态
  Future<NavigationStatus> getNavigationStatus(int sessionId) async {
    final response = await _client.get(
      Uri.parse('${ApiConfig.baseUrl}/navigation/sessions/$sessionId/status'),
      headers: await _getHeaders(),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return NavigationStatus.fromJson(data['data']);
    } else {
      throw Exception('获取导航状态失败: ${response.statusCode}');
    }
  }

  /// 暂停导航
  Future<NavigationStatus> pauseNavigation(int sessionId) async {
    final response = await _client.post(
      Uri.parse('${ApiConfig.baseUrl}/navigation/sessions/$sessionId/pause'),
      headers: await _getHeaders(),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return NavigationStatus.fromJson(data['data']);
    } else {
      throw Exception('暂停导航失败: ${response.statusCode}');
    }
  }

  /// 恢复导航
  Future<NavigationStatus> resumeNavigation(int sessionId) async {
    final response = await _client.post(
      Uri.parse('${ApiConfig.baseUrl}/navigation/sessions/$sessionId/resume'),
      headers: await _getHeaders(),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return NavigationStatus.fromJson(data['data']);
    } else {
      throw Exception('恢复导航失败: ${response.statusCode}');
    }
  }

  /// 结束导航
  Future<void> endNavigation(int sessionId) async {
    final response = await _client.post(
      Uri.parse('${ApiConfig.baseUrl}/navigation/sessions/$sessionId/end'),
      headers: await _getHeaders(),
    );

    if (response.statusCode != 200) {
      throw Exception('结束导航失败: ${response.statusCode}');
    }
  }

  /// 重新规划路线
  Future<RoutePlan> reroute(int sessionId) async {
    final response = await _client.post(
      Uri.parse('${ApiConfig.baseUrl}/navigation/sessions/$sessionId/reroute'),
      headers: await _getHeaders(),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      return RoutePlan.fromJson(data['data']);
    } else {
      throw Exception('重新规划路线失败: ${response.statusCode}');
    }
  }

  /// 收藏路线
  Future<void> favoriteRoute(int routeId) async {
    final response = await _client.post(
      Uri.parse('${ApiConfig.baseUrl}/navigation/routes/$routeId/favorite'),
      headers: await _getHeaders(),
    );

    if (response.statusCode != 200) {
      throw Exception('收藏路线失败: ${response.statusCode}');
    }
  }

  /// 取消收藏路线
  Future<void> unfavoriteRoute(int routeId) async {
    final response = await _client.post(
      Uri.parse('${ApiConfig.baseUrl}/navigation/routes/$routeId/unfavorite'),
      headers: await _getHeaders(),
    );

    if (response.statusCode != 200) {
      throw Exception('取消收藏路线失败: ${response.statusCode}');
    }
  }

  /// 获取收藏的路线列表
  Future<List<RoutePlan>> getFavoriteRoutes({int page = 1, int size = 20}) async {
    final response = await _client.get(
      Uri.parse('${ApiConfig.baseUrl}/navigation/routes/favorites?page=$page&size=$size'),
      headers: await _getHeaders(),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final List<dynamic> list = data['data'];
      return list.map((e) => RoutePlan.fromJson(e)).toList();
    } else {
      throw Exception('获取收藏路线失败: ${response.statusCode}');
    }
  }

  /// 删除路线
  Future<void> deleteRoute(int routeId) async {
    final response = await _client.delete(
      Uri.parse('${ApiConfig.baseUrl}/navigation/routes/$routeId'),
      headers: await _getHeaders(),
    );

    if (response.statusCode != 200) {
      throw Exception('删除路线失败: ${response.statusCode}');
    }
  }

  /// 获取导航历史
  Future<List<RoutePlan>> getNavigationHistory({int page = 1, int size = 20}) async {
    final response = await _client.get(
      Uri.parse('${ApiConfig.baseUrl}/navigation/history?page=$page&size=$size'),
      headers: await _getHeaders(),
    );

    if (response.statusCode == 200) {
      final data = jsonDecode(response.body);
      final List<dynamic> list = data['data'];
      return list.map((e) => RoutePlan.fromJson(e)).toList();
    } else {
      throw Exception('获取导航历史失败: ${response.statusCode}');
    }
  }

  Future<Map<String, String>> _getHeaders() async {
    final token = await ApiConfig.getToken();
    return {
      'Content-Type': 'application/json',
      'Authorization': 'Bearer $token',
    };
  }
}

/// 导航模式枚举
enum NavMode {
  driving('DRIVING'),
  walking('WALKING'),
  riding('RIDING'),
  transit('TRANSIT'),
  truck('TRUCK');

  final String value;
  const NavMode(this.value);
}

/// 路线策略枚举
enum RouteStrategy {
  fastest('FASTEST'),
  shortest('SHORTEST'),
  avoidCongestion('AVOID_CONGESTION'),
  economic('ECONOMIC'),
  highwayFirst('HIGHWAY_FIRST');

  final String value;
  const RouteStrategy(this.value);
}
