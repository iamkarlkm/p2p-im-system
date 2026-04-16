package com.im.common.util;

/**
 * 地理计算工具类
 * 提供距离计算、坐标转换等功能
 */
public class GeoUtils {

    private static final double EARTH_RADIUS_KM = 6371.0;
    private static final double EARTH_RADIUS_M = 6371000.0;

    /**
     * 计算两点之间的距离（米）
     * 使用Haversine公式
     */
    public static double calculateDistance(double lng1, double lat1, double lng2, double lat2) {
        double radLat1 = Math.toRadians(lat1);
        double radLat2 = Math.toRadians(lat2);
        double radLng1 = Math.toRadians(lng1);
        double radLng2 = Math.toRadians(lng2);

        double a = radLat1 - radLat2;
        double b = radLng1 - radLng2;

        double s = 2 * Math.asin(Math.sqrt(
            Math.pow(Math.sin(a / 2), 2) +
            Math.cos(radLat1) * Math.cos(radLat2) * Math.pow(Math.sin(b / 2), 2)
        ));

        return s * EARTH_RADIUS_M;
    }

    /**
     * 计算两点之间的距离（千米）
     */
    public static double calculateDistanceKm(double lng1, double lat1, double lng2, double lat2) {
        return calculateDistance(lng1, lat1, lng2, lat2) / 1000.0;
    }

    /**
     * 判断点是否在圆形围栏内
     */
    public static boolean isInCircle(double pointLng, double pointLat, 
                                      double centerLng, double centerLat, double radiusMeters) {
        double distance = calculateDistance(pointLng, pointLat, centerLng, centerLat);
        return distance <= radiusMeters;
    }

    /**
     * 计算GeoHash
     * 简化版实现
     */
    public static String encodeGeoHash(double lng, double lat, int precision) {
        String base32 = "0123456789bcdefghjkmnpqrstuvwxyz";
        double[] lngRange = {-180.0, 180.0};
        double[] latRange = {-90.0, 90.0};
        StringBuilder geohash = new StringBuilder();
        boolean isEven = true;
        int bit = 0, ch = 0;

        while (geohash.length() < precision) {
            double mid;
            if (isEven) {
                mid = (lngRange[0] + lngRange[1]) / 2.0;
                if (lng >= mid) {
                    ch |= (1 << (4 - bit));
                    lngRange[0] = mid;
                } else {
                    lngRange[1] = mid;
                }
            } else {
                mid = (latRange[0] + latRange[1]) / 2.0;
                if (lat >= mid) {
                    ch |= (1 << (4 - bit));
                    latRange[0] = mid;
                } else {
                    latRange[1] = mid;
                }
            }

            isEven = !isEven;
            if (bit < 4) {
                bit++;
            } else {
                geohash.append(base32.charAt(ch));
                bit = 0;
                ch = 0;
            }
        }

        return geohash.toString();
    }

    /**
     * 使用射线法判断点是否在多边形内
     */
    public static boolean isPointInPolygon(double lng, double lat, double[][] polygon) {
        boolean inside = false;
        int n = polygon.length;
        
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = polygon[i][0], yi = polygon[i][1];
            double xj = polygon[j][0], yj = polygon[j][1];
            
            boolean intersect = ((yi > lat) != (yj > lat)) &&
                (lng < (xj - xi) * (lat - yi) / (yj - yi) + xi);
            
            if (intersect) {
                inside = !inside;
            }
        }
        
        return inside;
    }
}
