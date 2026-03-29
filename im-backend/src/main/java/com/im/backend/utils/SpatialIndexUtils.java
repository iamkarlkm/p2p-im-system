package com.im.backend.utils;

import com.im.backend.entity.GeoHashGrid;
import com.im.backend.entity.LocationPoint;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 空间索引工具类
 * 管理GeoHash网格索引，支持高效的附近查询和范围查询
 * 
 * @author IM Development Team
 * @version 1.0
 */
public class SpatialIndexUtils {
    
    /** 内存中的GeoHash网格缓存 */
    private static final Map<String, GeoHashGrid> gridCache = new ConcurrentHashMap<>();
    
    /** 位置点索引：GeoHash -> 位置点列表 */
    private static final Map<String, Set<LocationPoint>> locationIndex = new ConcurrentHashMap<>();
    
    /** 网格热度统计 */
    private static final Map<String, Long> gridHeatMap = new ConcurrentHashMap<>();
    
    /**
     * 构建空间索引
     * 
     * @param points 位置点列表
     * @param precision GeoHash精度
     */
    public static void buildIndex(List<LocationPoint> points, int precision) {
        for (LocationPoint point : points) {
            String geohash = GeoHashUtils.encode(point.getLatitude(), point.getLongitude(), precision);
            
            // 添加到索引
            locationIndex.computeIfAbsent(geohash, k -> ConcurrentHashMap.newKeySet()).add(point);
            
            // 更新网格热度
            gridHeatMap.merge(geohash, 1L, Long::sum);
            
            // 创建或更新网格
            gridCache.computeIfAbsent(geohash, k -> new GeoHashGrid(k, precision))
                    .incrementPointCount();
        }
    }
    
    /**
     * 添加单个位置点到索引
     */
    public static void addLocation(LocationPoint point, int precision) {
        String geohash = GeoHashUtils.encode(point.getLatitude(), point.getLongitude(), precision);
        
        locationIndex.computeIfAbsent(geohash, k -> ConcurrentHashMap.newKeySet()).add(point);
        gridHeatMap.merge(geohash, 1L, Long::sum);
        
        gridCache.computeIfAbsent(geohash, k -> new GeoHashGrid(k, precision))
                .incrementPointCount();
    }
    
    /**
     * 从索引中移除位置点
     */
    public static void removeLocation(LocationPoint point, int precision) {
        String geohash = GeoHashUtils.encode(point.getLatitude(), point.getLongitude(), precision);
        
        Set<LocationPoint> points = locationIndex.get(geohash);
        if (points != null) {
            points.remove(point);
            if (points.isEmpty()) {
                locationIndex.remove(geohash);
                gridHeatMap.remove(geohash);
                gridCache.remove(geohash);
            } else {
                gridHeatMap.merge(geohash, -1L, Long::sum);
                GeoHashGrid grid = gridCache.get(geohash);
                if (grid != null) {
                    grid.decrementPointCount();
                }
            }
        }
    }
    
    /**
     * 圆形范围查询
     * 获取指定位置周围distance米范围内的所有位置点
     * 
     * @param latitude 中心纬度
     * @param longitude 中心经度
     * @param distance 距离（米）
     * @return 位置点列表（按距离排序）
     */
    public static List<LocationPoint> circularQuery(double latitude, double longitude, double distance) {
        List<LocationPoint> result = new ArrayList<>();
        
        // 计算合适的精度级别
        int precision = GeoHashUtils.calculatePrecision(distance);
        String centerGeohash = GeoHashUtils.encode(latitude, longitude, precision);
        
        // 获取中心网格和邻居网格
        Set<String> searchGeohashes = new HashSet<>();
        searchGeohashes.add(centerGeohash);
        Collections.addAll(searchGeohashes, GeoHashUtils.getNeighbors(centerGeohash));
        
        // 查询所有相关网格
        for (String geohash : searchGeohashes) {
            Set<LocationPoint> points = locationIndex.get(geohash);
            if (points != null) {
                for (LocationPoint point : points) {
                    // 精确距离过滤
                    double actualDistance = GeoHashUtils.calculateDistance(
                        centerGeohash, 
                        GeoHashUtils.encode(point.getLatitude(), point.getLongitude(), precision)
                    );
                    
                    if (actualDistance <= distance) {
                        point.setDistance(actualDistance);
                        result.add(point);
                    }
                }
            }
        }
        
        // 按距离排序
        result.sort(Comparator.comparingDouble(LocationPoint::getDistance));
        
        return result;
    }
    
    /**
     * 矩形范围查询
     * 
     * @param minLat 最小纬度
     * @param maxLat 最大纬度
     * @param minLon 最小经度
     * @param maxLon 最大经度
     * @return 位置点列表
     */
    public static List<LocationPoint> boundingBoxQuery(double minLat, double maxLat, double minLon, double maxLon) {
        List<LocationPoint> result = new ArrayList<>();
        
        // 计算覆盖范围的GeoHash网格
        Set<String> coverGeohashes = calculateCoverGeohashes(minLat, maxLat, minLon, maxLon);
        
        // 查询所有相关网格
        for (String geohash : coverGeohashes) {
            Set<LocationPoint> points = locationIndex.get(geohash);
            if (points != null) {
                for (LocationPoint point : points) {
                    double lat = point.getLatitude();
                    double lon = point.getLongitude();
                    
                    // 精确边界过滤
                    if (lat >= minLat && lat <= maxLat && lon >= minLon && lon <= maxLon) {
                        result.add(point);
                    }
                }
            }
        }
        
        return result;
    }
    
    /**
     * 多边形范围查询
     * 使用射线法判断点是否在多边形内
     */
    public static List<LocationPoint> polygonQuery(List<double[]> polygon) {
        List<LocationPoint> result = new ArrayList<>();
        
        // 计算多边形边界框
        double minLat = Double.MAX_VALUE, maxLat = -Double.MAX_VALUE;
        double minLon = Double.MAX_VALUE, maxLon = -Double.MAX_VALUE;
        
        for (double[] point : polygon) {
            minLat = Math.min(minLat, point[0]);
            maxLat = Math.max(maxLat, point[0]);
            minLon = Math.min(minLon, point[1]);
            maxLon = Math.max(maxLon, point[1]);
        }
        
        // 先使用边界框过滤
        List<LocationPoint> candidates = boundingBoxQuery(minLat, maxLat, minLon, maxLon);
        
        // 使用射线法精确判断
        for (LocationPoint point : candidates) {
            if (isPointInPolygon(point.getLatitude(), point.getLongitude(), polygon)) {
                result.add(point);
            }
        }
        
        return result;
    }
    
    /**
     * 射线法判断点是否在多边形内
     */
    private static boolean isPointInPolygon(double lat, double lon, List<double[]> polygon) {
        boolean inside = false;
        int n = polygon.size();
        
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = polygon.get(i)[1], yi = polygon.get(i)[0];
            double xj = polygon.get(j)[1], yj = polygon.get(j)[0];
            
            boolean intersect = ((yi > lat) != (yj > lat)) &&
                    (lon < (xj - xi) * (lat - yi) / (yj - yi) + xi);
            
            if (intersect) {
                inside = !inside;
            }
        }
        
        return inside;
    }
    
    /**
     * 计算覆盖边界框的GeoHash网格
     */
    private static Set<String> calculateCoverGeohashes(double minLat, double maxLat, double minLon, double maxLon) {
        Set<String> geohashes = new HashSet<>();
        
        // 使用5级精度计算覆盖网格
        int precision = 5;
        
        // 起始点
        String start = GeoHashUtils.encode(minLat, minLon, precision);
        String end = GeoHashUtils.encode(maxLat, maxLon, precision);
        
        geohashes.add(start);
        geohashes.add(end);
        
        // 添加中心点和周围网格
        double centerLat = (minLat + maxLat) / 2;
        double centerLon = (minLon + maxLon) / 2;
        String center = GeoHashUtils.encode(centerLat, centerLon, precision);
        
        geohashes.add(center);
        Collections.addAll(geohashes, GeoHashUtils.getNeighbors(center));
        
        return geohashes;
    }
    
    /**
     * 获取网格热度排名
     * 
     * @param topN 前N个
     * @return 热度排名列表
     */
    public static List<Map.Entry<String, Long>> getHotGrids(int topN) {
        return gridHeatMap.entrySet()
                .stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(topN)
                .toList();
    }
    
    /**
     * 获取指定网格内的所有位置点
     */
    public static Set<LocationPoint> getPointsInGrid(String geohash) {
        return locationIndex.getOrDefault(geohash, Collections.emptySet());
    }
    
    /**
     * 获取网格统计信息
     */
    public static Map<String, Object> getGridStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("totalGrids", gridCache.size());
        stats.put("totalIndexedPoints", locationIndex.values().stream().mapToInt(Set::size).sum());
        stats.put("hotGridCount", gridHeatMap.size());
        
        // 计算平均每个网格的点数
        int totalPoints = (int) stats.get("totalIndexedPoints");
        int totalGrids = (int) stats.get("totalGrids");
        stats.put("avgPointsPerGrid", totalGrids > 0 ? totalPoints / totalGrids : 0);
        
        return stats;
    }
    
    /**
     * 清空索引
     */
    public static void clearIndex() {
        gridCache.clear();
        locationIndex.clear();
        gridHeatMap.clear();
    }
}
