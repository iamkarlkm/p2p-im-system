import 'dart:math';

import 'package:im_mobile/services/navigation/navigation_service.dart';

/// 坐标工具类
/// 
/// 提供坐标转换、距离计算等工具方法
class CoordinateUtils {
  static const double _earthRadius = 6371000; // 地球半径（米）

  /// 计算两点间的球面距离（Haversine公式）
  /// 
  /// [lat1] 起点纬度
  /// [lng1] 起点经度
  /// [lat2] 终点纬度
  /// [lng2] 终点经度
  /// 返回距离（米）
  static double calculateDistance(
    double lat1,
    double lng1,
    double lat2,
    double lng2,
  ) {
    final dLat = _toRadians(lat2 - lat1);
    final dLng = _toRadians(lng2 - lng1);

    final a = sin(dLat / 2) * sin(dLat / 2) +
        cos(_toRadians(lat1)) *
            cos(_toRadians(lat2)) *
            sin(dLng / 2) *
            sin(dLng / 2);

    final c = 2 * atan2(sqrt(a), sqrt(1 - a));

    return _earthRadius * c;
  }

  /// 计算两点间的球面距离（使用LatLng）
  static double distanceBetween(LatLng start, LatLng end) {
    return calculateDistance(
      start.latitude,
      start.longitude,
      end.latitude,
      end.longitude,
    );
  }

  /// 计算点到线段的距离
  /// 
  /// [px] 点纬度
  /// [py] 点经度
  /// [x1] 线段起点纬度
  /// [y1] 线段起点经度
  /// [x2] 线段终点纬度
  /// [y2] 线段终点经度
  static double distanceToLineSegment(
    double px,
    double py,
    double x1,
    double y1,
    double x2,
    double y2,
  ) {
    final A = px - x1;
    final B = py - y1;
    final C = x2 - x1;
    final D = y2 - y1;

    final dot = A * C + B * D;
    final lenSq = C * C + D * D;
    var param = -1.0;

    if (lenSq != 0) {
      param = dot / lenSq;
    }

    double xx, yy;

    if (param < 0) {
      xx = x1;
      yy = y1;
    } else if (param > 1) {
      xx = x2;
      yy = y2;
    } else {
      xx = x1 + param * C;
      yy = y1 + param * D;
    }

    final dx = px - xx;
    final dy = py - yy;

    return sqrt(dx * dx + dy * dy);
  }

  /// 计算方位角（从正北方向顺时针角度）
  /// 
  /// [lat1] 起点纬度
  /// [lng1] 起点经度
  /// [lat2] 终点纬度
  /// [lng2] 终点经度
  /// 返回角度（0-360）
  static double calculateBearing(
    double lat1,
    double lng1,
    double lat2,
    double lng2,
  ) {
    final dLng = _toRadians(lng2 - lng1);
    final lat1Rad = _toRadians(lat1);
    final lat2Rad = _toRadians(lat2);

    final y = sin(dLng) * cos(lat2Rad);
    final x = cos(lat1Rad) * sin(lat2Rad) -
        sin(lat1Rad) * cos(lat2Rad) * cos(dLng);

    var bearing = atan2(y, x);
    bearing = _toDegrees(bearing);
    bearing = (bearing + 360) % 360;

    return bearing;
  }

  /// 计算目标点坐标
  /// 
  /// [lat] 起点纬度
  /// [lng] 起点经度
  /// [distance] 距离（米）
  /// [bearing] 方位角（度）
  static LatLng calculateDestination(
    double lat,
    double lng,
    double distance,
    double bearing,
  ) {
    final latRad = _toRadians(lat);
    final lngRad = _toRadians(lng);
    final bearingRad = _toRadians(bearing);
    final angularDistance = distance / _earthRadius;

    final destLat = asin(
      sin(latRad) * cos(angularDistance) +
          cos(latRad) * sin(angularDistance) * cos(bearingRad),
    );

    final destLng = lngRad +
        atan2(
          sin(bearingRad) * sin(angularDistance) * cos(latRad),
          cos(angularDistance) - sin(latRad) * sin(destLat),
        );

    return LatLng(_toDegrees(destLat), _toDegrees(destLng));
  }

  /// 坐标系转换：WGS84 -> GCJ-02（火星坐标系）
  /// 
  /// 中国境内地图使用的坐标系
  static LatLng wgs84ToGcj02(double lat, double lng) {
    if (_outOfChina(lat, lng)) {
      return LatLng(lat, lng);
    }

    final dLat = _transformLat(lng - 105.0, lat - 35.0);
    final dLng = _transformLng(lng - 105.0, lat - 35.0);

    final radLat = lat / 180.0 * pi;
    var magic = sin(radLat);
    magic = 1 - 0.00669342162296594323 * magic * magic;
    final sqrtMagic = sqrt(magic);

    final newLat = lat + (dLat * 180.0) / ((6378245.0 * (1 - 0.00669342162296594323)) / (magic * sqrtMagic) * pi);
    final newLng = lng + (dLng * 180.0) / (6378245.0 / sqrtMagic * cos(radLat) * pi);

    return LatLng(newLat, newLng);
  }

  /// 坐标系转换：GCJ-02 -> WGS84
  static LatLng gcj02ToWgs84(double lat, double lng) {
    if (_outOfChina(lat, lng)) {
      return LatLng(lat, lng);
    }

    final dLat = _transformLat(lng - 105.0, lat - 35.0);
    final dLng = _transformLng(lng - 105.0, lat - 35.0);

    final radLat = lat / 180.0 * pi;
    var magic = sin(radLat);
    magic = 1 - 0.00669342162296594323 * magic * magic;
    final sqrtMagic = sqrt(magic);

    final newLat = lat - (dLat * 180.0) / ((6378245.0 * (1 - 0.00669342162296594323)) / (magic * sqrtMagic) * pi);
    final newLng = lng - (dLng * 180.0) / (6378245.0 / sqrtMagic * cos(radLat) * pi);

    return LatLng(newLat, newLng);
  }

  /// 坐标系转换：GCJ-02 -> BD-09（百度坐标系）
  static LatLng gcj02ToBd09(double lat, double lng) {
    final z = sqrt(lng * lng + lat * lat) + 0.00002 * sin(lat * pi * 3000.0 / 180.0);
    final theta = atan2(lat, lng) + 0.000003 * cos(lng * pi * 3000.0 / 180.0);
    final bdLng = z * cos(theta) + 0.0065;
    final bdLat = z * sin(theta) + 0.006;
    return LatLng(bdLat, bdLng);
  }

  /// 坐标系转换：BD-09 -> GCJ-02
  static LatLng bd09ToGcj02(double bdLat, double bdLng) {
    final x = bdLng - 0.0065;
    final y = bdLat - 0.006;
    final z = sqrt(x * x + y * y) - 0.00002 * sin(y * pi * 3000.0 / 180.0);
    final theta = atan2(y, x) - 0.000003 * cos(x * pi * 3000.0 / 180.0);
    final gcjLng = z * cos(theta);
    final gcjLat = z * sin(theta);
    return LatLng(gcjLat, gcjLng);
  }

  /// 坐标系转换：WGS84 -> BD-09
  static LatLng wgs84ToBd09(double lat, double lng) {
    final gcj = wgs84ToGcj02(lat, lng);
    return gcj02ToBd09(gcj.latitude, gcj.longitude);
  }

  /// 坐标系转换：BD-09 -> WGS84
  static LatLng bd09ToWgs84(double bdLat, double bdLng) {
    final gcj = bd09ToGcj02(bdLat, bdLng);
    return gcj02ToWgs84(gcj.latitude, gcj.longitude);
  }

  /// 计算GeoHash
  static String encodeGeohash(double lat, double lng, {int precision = 12}) {
    const base32 = '0123456789bcdefghjkmnpqrstuvwxyz';
    
    var latRange = [-90.0, 90.0];
    var lngRange = [-180.0, 180.0];
    
    final bits = [16, 8, 4, 2, 1];
    var bit = 0;
    var ch = 0;
    
    final geohash = StringBuffer();
    
    while (geohash.length < precision) {
      if (bit % 2 == 0) {
        // 经度
        final mid = (lngRange[0] + lngRange[1]) / 2;
        if (lng >= mid) {
          ch |= bits[bit % 5];
          lngRange[0] = mid;
        } else {
          lngRange[1] = mid;
        }
      } else {
        // 纬度
        final mid = (latRange[0] + latRange[1]) / 2;
        if (lat >= mid) {
          ch |= bits[bit % 5];
          latRange[0] = mid;
        } else {
          latRange[1] = mid;
        }
      }
      
      bit++;
      if (bit % 5 == 0) {
        geohash.write(base32[ch]);
        ch = 0;
      }
    }
    
    return geohash.toString();
  }

  /// 获取相邻的GeoHash
  static Map<String, String> getAdjacentGeohashes(String geohash) {
    final result = <String, String>{};
    
    // 简化实现，实际应解码再计算
    result['top'] = geohash;
    result['bottom'] = geohash;
    result['left'] = geohash;
    result['right'] = geohash;
    
    return result;
  }

  /// 检查点是否在多边形内（射线法）
  static bool isPointInPolygon(LatLng point, List<LatLng> polygon) {
    if (polygon.length < 3) return false;

    var inside = false;
    var j = polygon.length - 1;

    for (var i = 0; i < polygon.length; i++) {
      final xi = polygon[i].longitude;
      final yi = polygon[i].latitude;
      final xj = polygon[j].longitude;
      final yj = polygon[j].latitude;

      final intersect =
          ((yi > point.latitude) != (yj > point.latitude)) &&
              (point.longitude <
                  (xj - xi) * (point.latitude - yi) / (yj - yi) + xi);

      if (intersect) inside = !inside;
      j = i;
    }

    return inside;
  }

  /// 计算多边形面积
  static double calculatePolygonArea(List<LatLng> polygon) {
    if (polygon.length < 3) return 0.0;

    var area = 0.0;
    final n = polygon.length;

    for (var i = 0; i < n; i++) {
      final j = (i + 1) % n;
      area += polygon[i].longitude * polygon[j].latitude;
      area -= polygon[j].longitude * polygon[i].latitude;
    }

    area = area.abs() / 2.0;
    return area * 111320 * 111320; // 转换为平方米（近似）
  }

  /// 计算多边形中心点
  static LatLng calculatePolygonCenter(List<LatLng> polygon) {
    if (polygon.isEmpty) return LatLng(0, 0);
    if (polygon.length == 1) return polygon[0];

    var x = 0.0;
    var y = 0.0;
    var z = 0.0;

    for (final point in polygon) {
      final lat = _toRadians(point.latitude);
      final lng = _toRadians(point.longitude);

      x += cos(lat) * cos(lng);
      y += cos(lat) * sin(lng);
      z += sin(lat);
    }

    x /= polygon.length;
    y /= polygon.length;
    z /= polygon.length;

    final lng = atan2(y, x);
    final hyp = sqrt(x * x + y * y);
    final lat = atan2(z, hyp);

    return LatLng(_toDegrees(lat), _toDegrees(lng));
  }

  /// 计算包围盒
  static LatLngBounds calculateBounds(List<LatLng> points) {
    if (points.isEmpty) {
      return LatLngBounds(
        southwest: LatLng(0, 0),
        northeast: LatLng(0, 0),
      );
    }

    double minLat = points[0].latitude;
    double maxLat = points[0].latitude;
    double minLng = points[0].longitude;
    double maxLng = points[0].longitude;

    for (final point in points.skip(1)) {
      minLat = min(minLat, point.latitude);
      maxLat = max(maxLat, point.latitude);
      minLng = min(minLng, point.longitude);
      maxLng = max(maxLng, point.longitude);
    }

    return LatLngBounds(
      southwest: LatLng(minLat, minLng),
      northeast: LatLng(maxLat, maxLng),
    );
  }

  /// 度转弧度
  static double _toRadians(double degrees) {
    return degrees * pi / 180.0;
  }

  /// 弧度转度
  static double _toDegrees(double radians) {
    return radians * 180.0 / pi;
  }

  /// 判断是否在中国境外
  static bool _outOfChina(double lat, double lng) {
    return lng < 72.004 || lng > 137.8347 || lat < 0.8293 || lat > 55.8271;
  }

  /// 坐标转换辅助函数
  static double _transformLat(double x, double y) {
    var ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * sqrt(x.abs());
    ret += (20.0 * sin(6.0 * x * pi) + 20.0 * sin(2.0 * x * pi)) * 2.0 / 3.0;
    ret += (20.0 * sin(y * pi) + 40.0 * sin(y / 3.0 * pi)) * 2.0 / 3.0;
    ret += (160.0 * sin(y / 12.0 * pi) + 320 * sin(y * pi / 30.0)) * 2.0 / 3.0;
    return ret;
  }

  static double _transformLng(double x, double y) {
    var ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * sqrt(x.abs());
    ret += (20.0 * sin(6.0 * x * pi) + 20.0 * sin(2.0 * x * pi)) * 2.0 / 3.0;
    ret += (20.0 * sin(x * pi) + 40.0 * sin(x / 3.0 * pi)) * 2.0 / 3.0;
    ret += (150.0 * sin(x / 12.0 * pi) + 300.0 * sin(x / 30.0 * pi)) * 2.0 / 3.0;
    return ret;
  }
}

/// 扩展方法
extension LatLngExtension on LatLng {
  /// 计算到另一点的距离
  double distanceTo(LatLng other) {
    return CoordinateUtils.distanceBetween(this, other);
  }

  /// 计算方位角
  double bearingTo(LatLng other) {
    return CoordinateUtils.calculateBearing(
      latitude,
      longitude,
      other.latitude,
      other.longitude,
    );
  }

  /// 转换为WGS84坐标
  LatLng toWgs84() {
    return CoordinateUtils.gcj02ToWgs84(latitude, longitude);
  }

  /// 转换为GCJ02坐标
  LatLng toGcj02() {
    return CoordinateUtils.wgs84ToGcj02(latitude, longitude);
  }

  /// 转换为BD09坐标
  LatLng toBd09() {
    return CoordinateUtils.wgs84ToBd09(latitude, longitude);
  }

  /// 获取GeoHash
  String toGeohash({int precision = 12}) {
    return CoordinateUtils.encodeGeohash(latitude, longitude, precision: precision);
  }

  /// 格式化显示
  String toFormattedString() {
    return '${latitude.toStringAsFixed(6)}, ${longitude.toStringAsFixed(6)}';
  }
}
