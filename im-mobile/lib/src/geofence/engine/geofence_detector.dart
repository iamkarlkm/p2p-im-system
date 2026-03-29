import 'dart:math';

import 'package:geolocator/geolocator.dart';

import '../models/geofence_model.dart';

/// 地理围栏检测引擎
/// 
/// 提供多种围栏检测算法：
/// 1. 圆形围栏 - Haversine球面距离计算
/// 2. 多边形围栏 - 射线法点包含检测
/// 3. 线性围栏 - 点到线段距离 + 缓冲区
class GeofenceDetector {
  /// 地球半径（米）
  static const double _earthRadius = 6371000;
  
  /// 检测位置与围栏的关系
  /// 
  /// 返回：
  /// - true: 在围栏内
  /// - false: 在围栏外
  static bool detect(Geofence geofence, Position position) {
    if (!geofence.isActive) {
      return false;
    }
    
    // 检查围栏是否过期
    if (geofence.expiresAt != null && 
        DateTime.now().isAfter(geofence.expiresAt!)) {
      return false;
    }
    
    switch (geofence.type) {
      case GeofenceType.circle:
        return _detectCircle(geofence, position);
      case GeofenceType.polygon:
        return _detectPolygon(geofence, position);
      case GeofenceType.polyline:
        return _detectPolyline(geofence, position);
    }
  }
  
  /// 检测并计算置信度
  /// 
  /// 返回 (是否在围栏内, 置信度)
  static (bool, double) detectWithConfidence(Geofence geofence, Position position) {
    if (!geofence.isActive) {
      return (false, 0.0);
    }
    
    if (geofence.expiresAt != null && 
        DateTime.now().isAfter(geofence.expiresAt!)) {
      return (false, 0.0);
    }
    
    switch (geofence.type) {
      case GeofenceType.circle:
        return _detectCircleWithConfidence(geofence, position);
      case GeofenceType.polygon:
        return _detectPolygonWithConfidence(geofence, position);
      case GeofenceType.polyline:
        return _detectPolylineWithConfidence(geofence, position);
    }
  }
  
  /// 圆形围栏检测
  static bool _detectCircle(Geofence geofence, Position position) {
    final distance = calculateDistance(
      geofence.latitude,
      geofence.longitude,
      position.latitude,
      position.longitude,
    );
    return distance <= (geofence.radius ?? 100);
  }
  
  /// 圆形围栏检测（带置信度）
  static (bool, double) _detectCircleWithConfidence(
    Geofence geofence, 
    Position position,
  ) {
    final radius = geofence.radius ?? 100;
    final distance = calculateDistance(
      geofence.latitude,
      geofence.longitude,
      position.latitude,
      position.longitude,
    );
    
    // 置信度计算：
    // - 在围栏中心时置信度最高(1.0)
    // - 在围栏边界时置信度为0.5
    // - 在围栏外距离越远置信度越低
    double confidence;
    if (distance <= radius) {
      // 在围栏内，距离中心越近置信度越高
      confidence = 1.0 - (distance / radius) * 0.5;
    } else {
      // 在围栏外
      final overflow = distance - radius;
      if (overflow > radius * 0.5) {
        confidence = 0.0;
      } else {
        confidence = 0.5 - (overflow / (radius * 0.5)) * 0.5;
      }
    }
    
    // 考虑定位精度
    if (position.accuracy > 0) {
      final accuracyFactor = math.max(0, 1 - position.accuracy / radius);
      confidence *= (0.7 + 0.3 * accuracyFactor);
    }
    
    return (distance <= radius, confidence.clamp(0.0, 1.0));
  }
  
  /// 多边形围栏检测（射线法）
  static bool _detectPolygon(Geofence geofence, Position position) {
    final points = geofence.polygonPoints;
    if (points == null || points.length < 3) {
      return false;
    }
    
    return _isPointInPolygon(
      position.latitude,
      position.longitude,
      points,
    );
  }
  
  /// 多边形围栏检测（带置信度）
  static (bool, double) _detectPolygonWithConfidence(
    Geofence geofence, 
    Position position,
  ) {
    final points = geofence.polygonPoints;
    if (points == null || points.length < 3) {
      return (false, 0.0);
    }
    
    final isInside = _isPointInPolygon(
      position.latitude,
      position.longitude,
      points,
    );
    
    // 计算到最近边的距离
    final distanceToEdge = _calculateDistanceToPolygonEdge(
      position.latitude,
      position.longitude,
      points,
    );
    
    // 估算多边形"半径"（对角线的一半）
    final polygonRadius = _estimatePolygonRadius(points);
    
    double confidence;
    if (isInside) {
      // 在多边形内，距离边越远置信度越高
      confidence = 0.5 + math.min(1.0, distanceToEdge / (polygonRadius * 0.3)) * 0.5;
    } else {
      // 在多边形外
      if (distanceToEdge > polygonRadius * 0.3) {
        confidence = 0.0;
      } else {
        confidence = 0.5 - (distanceToEdge / (polygonRadius * 0.3)) * 0.5;
      }
    }
    
    // 考虑定位精度
    if (position.accuracy > 0) {
      final accuracyFactor = math.max(0, 1 - position.accuracy / polygonRadius);
      confidence *= (0.7 + 0.3 * accuracyFactor);
    }
    
    return (isInside, confidence.clamp(0.0, 1.0));
  }
  
  /// 线性围栏检测（点到线路径的距离）
  static bool _detectPolyline(Geofence geofence, Position position) {
    final points = geofence.polylinePoints;
    if (points == null || points.length < 2) {
      return false;
    }
    
    final buffer = geofence.polylineBuffer ?? 50;
    final minDistance = _calculateMinDistanceToPolyline(
      position.latitude,
      position.longitude,
      points,
    );
    
    return minDistance <= buffer;
  }
  
  /// 线性围栏检测（带置信度）
  static (bool, double) _detectPolylineWithConfidence(
    Geofence geofence, 
    Position position,
  ) {
    final points = geofence.polylinePoints;
    if (points == null || points.length < 2) {
      return (false, 0.0);
    }
    
    final buffer = geofence.polylineBuffer ?? 50;
    final minDistance = _calculateMinDistanceToPolyline(
      position.latitude,
      position.longitude,
      points,
    );
    
    final isInside = minDistance <= buffer;
    
    double confidence;
    if (isInside) {
      // 在线路缓冲区中，距离线路越近置信度越高
      confidence = 1.0 - (minDistance / buffer) * 0.5;
    } else {
      // 在缓冲区外
      final overflow = minDistance - buffer;
      if (overflow > buffer) {
        confidence = 0.0;
      } else {
        confidence = 0.5 - (overflow / buffer) * 0.5;
      }
    }
    
    // 考虑定位精度
    if (position.accuracy > 0) {
      final accuracyFactor = math.max(0, 1 - position.accuracy / buffer);
      confidence *= (0.7 + 0.3 * accuracyFactor);
    }
    
    return (isInside, confidence.clamp(0.0, 1.0));
  }
  
  /// 射线法判断点是否在多边形内
  /// 
  /// 算法：从点向右发射水平射线，计算与多边形边的交点数量
  /// - 奇数个交点：在多边形内
  /// - 偶数个交点：在多边形外
  static bool _isPointInPolygon(
    double lat,
    double lng,
    List<Map<String, double>> polygon,
  ) {
    bool inside = false;
    int j = polygon.length - 1;
    
    for (int i = 0; i < polygon.length; i++) {
      final vi = polygon[i];
      final vj = polygon[j];
      
      final viLat = vi['lat']!;
      final viLng = vi['lng']!;
      final vjLat = vj['lat']!;
      final vjLng = vj['lng']!;
      
      // 检查边是否与射线相交
      if (((viLng > lng) != (vjLng > lng)) &&
          (lat < (vjLat - viLat) * (lng - viLng) / (vjLng - viLng) + viLat)) {
        inside = !inside;
      }
      j = i;
    }
    
    return inside;
  }
  
  /// 计算点到多边形边的最小距离
  static double _calculateDistanceToPolygonEdge(
    double lat,
    double lng,
    List<Map<String, double>> polygon,
  ) {
    double minDistance = double.infinity;
    
    for (int i = 0; i < polygon.length; i++) {
      final p1 = polygon[i];
      final p2 = polygon[(i + 1) % polygon.length];
      
      final distance = _calculateDistanceToSegment(
        lat,
        lng,
        p1['lat']!,
        p1['lng']!,
        p2['lat']!,
        p2['lng']!,
      );
      
      if (distance < minDistance) {
        minDistance = distance;
      }
    }
    
    return minDistance;
  }
  
  /// 计算点到折线的最小距离
  static double _calculateMinDistanceToPolyline(
    double lat,
    double lng,
    List<Map<String, double>> polyline,
  ) {
    double minDistance = double.infinity;
    
    for (int i = 0; i < polyline.length - 1; i++) {
      final p1 = polyline[i];
      final p2 = polyline[i + 1];
      
      final distance = _calculateDistanceToSegment(
        lat,
        lng,
        p1['lat']!,
        p1['lng']!,
        p2['lat']!,
        p2['lng']!,
      );
      
      if (distance < minDistance) {
        minDistance = distance;
      }
    }
    
    return minDistance;
  }
  
  /// 计算点到线段的最短距离
  /// 
  /// 使用向量投影法，考虑线段端点情况
  static double _calculateDistanceToSegment(
    double lat,
    double lng,
    double lat1,
    double lng1,
    double lat2,
    double lng2,
  ) {
    // 将坐标转换为米（近似）
    const double metersPerDegreeLat = 111320;
    final metersPerDegreeLng = 111320 * math.cos(_toRadians(lat));
    
    // 转换为局部笛卡尔坐标（米）
    final x = lng * metersPerDegreeLng;
    final y = lat * metersPerDegreeLat;
    final x1 = lng1 * metersPerDegreeLng;
    final y1 = lat1 * metersPerDegreeLat;
    final x2 = lng2 * metersPerDegreeLng;
    final y2 = lat2 * metersPerDegreeLat;
    
    // 向量 AB
    final dx = x2 - x1;
    final dy = y2 - y1;
    
    // 向量 AP
    final apx = x - x1;
    final apy = y - y1;
    
    // 计算投影参数 t
    final ab2 = dx * dx + dy * dy;
    
    if (ab2 == 0) {
      // A和B重合，直接返回到A的距离
      return calculateDistance(lat, lng, lat1, lng1);
    }
    
    final t = math.max(0, math.min(1, (apx * dx + apy * dy) / ab2));
    
    // 投影点坐标
    final projX = x1 + t * dx;
    final projY = y1 + t * dy;
    
    // 转换回经纬度计算距离
    final projLng = projX / metersPerDegreeLng;
    final projLat = projY / metersPerDegreeLat;
    
    return calculateDistance(lat, lng, projLat, projLng);
  }
  
  /// 估算多边形半径（对角线的一半）
  static double _estimatePolygonRadius(List<Map<String, double>> polygon) {
    if (polygon.isEmpty) return 100;
    
    // 计算中心点
    double centerLat = 0;
    double centerLng = 0;
    for (final point in polygon) {
      centerLat += point['lat']!;
      centerLng += point['lng']!;
    }
    centerLat /= polygon.length;
    centerLng /= polygon.length;
    
    // 计算最大距离
    double maxDistance = 0;
    for (final point in polygon) {
      final distance = calculateDistance(
        centerLat,
        centerLng,
        point['lat']!,
        point['lng']!,
      );
      if (distance > maxDistance) {
        maxDistance = distance;
      }
    }
    
    return maxDistance;
  }
  
  /// 计算两个经纬度坐标之间的Haversine距离（米）
  /// 
  /// Haversine公式用于计算球面上两点间的最短距离
  static double calculateDistance(
    double lat1,
    double lng1,
    double lat2,
    double lng2,
  ) {
    final dLat = _toRadians(lat2 - lat1);
    final dLng = _toRadians(lng2 - lng1);
    
    final a = math.sin(dLat / 2) * math.sin(dLat / 2) +
              math.cos(_toRadians(lat1)) * math.cos(_toRadians(lat2)) *
              math.sin(dLng / 2) * math.sin(dLng / 2);
    
    final c = 2 * math.atan2(math.sqrt(a), math.sqrt(1 - a));
    
    return _earthRadius * c;
  }
  
  /// 度转弧度
  static double _toRadians(double degrees) {
    return degrees * math.pi / 180;
  }
  
  /// 计算多边形面积（平方米）
  /// 
  /// 使用球面多边形面积公式
  static double calculatePolygonArea(List<Map<String, double>> polygon) {
    if (polygon.length < 3) return 0;
    
    double area = 0;
    final n = polygon.length;
    
    for (int i = 0; i < n; i++) {
      final j = (i + 1) % n;
      final p1 = polygon[i];
      final p2 = polygon[j];
      
      final lat1 = _toRadians(p1['lat']!);
      final lat2 = _toRadians(p2['lat']!);
      final lng1 = _toRadians(p1['lng']!);
      final lng2 = _toRadians(p2['lng']!);
      
      area += (lng2 - lng1) * (2 + math.sin(lat1) + math.sin(lat2));
    }
    
    area = area * _earthRadius * _earthRadius / 2;
    return area.abs();
  }
  
  /// 计算包围盒
  /// 
  /// 返回 (minLat, minLng, maxLat, maxLng)
  static (double, double, double, double) calculateBoundingBox(
    List<Map<String, double>> points,
  ) {
    if (points.isEmpty) return (0, 0, 0, 0);
    
    double minLat = points[0]['lat']!;
    double maxLat = points[0]['lat']!;
    double minLng = points[0]['lng']!;
    double maxLng = points[0]['lng']!;
    
    for (final point in points.skip(1)) {
      final lat = point['lat']!;
      final lng = point['lng']!;
      
      if (lat < minLat) minLat = lat;
      if (lat > maxLat) maxLat = lat;
      if (lng < minLng) minLng = lng;
      if (lng > maxLng) maxLng = lng;
    }
    
    return (minLat, minLng, maxLat, maxLng);
  }
  
  /// 快速包围盒检测
  /// 
  /// 用于快速过滤，避免昂贵的精确计算
  static bool quickBoundingBoxCheck(
    double lat,
    double lng,
    double minLat,
    double minLng,
    double maxLat,
    double maxLng,
    double buffer,
  ) {
    return lat >= (minLat - buffer) &&
           lat <= (maxLat + buffer) &&
           lng >= (minLng - buffer) &&
           lng <= (maxLng + buffer);
  }
}

// 导入math库别名
import 'dart:math' as math;
