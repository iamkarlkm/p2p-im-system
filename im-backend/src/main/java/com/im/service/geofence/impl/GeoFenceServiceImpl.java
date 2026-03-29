package com.im.service.geofence.impl;

import com.im.entity.geofence.GeoFence;
import com.im.service.geofence.GeoFenceService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 地理围栏服务实现类
 */
@Service
public class GeoFenceServiceImpl implements GeoFenceService {
    
    private final Map<String, GeoFence> fenceStore = new ConcurrentHashMap<>();
    
    @Override
    public GeoFence createFence(GeoFence fence) {
        fence.setFenceId(UUID.randomUUID().toString());
        fence.setCreateTime(java.time.LocalDateTime.now());
        fence.setUpdateTime(java.time.LocalDateTime.now());
        fence.setEnabled(true);
        fenceStore.put(fence.getFenceId(), fence);
        return fence;
    }
    
    @Override
    public GeoFence updateFence(String fenceId, GeoFence fence) {
        fence.setFenceId(fenceId);
        fence.setUpdateTime(java.time.LocalDateTime.now());
        fenceStore.put(fenceId, fence);
        return fence;
    }
    
    @Override
    public void deleteFence(String fenceId) {
        fenceStore.remove(fenceId);
    }
    
    @Override
    public GeoFence getFenceById(String fenceId) {
        return fenceStore.get(fenceId);
    }
    
    @Override
    public List<GeoFence> getFencesByPoiId(String poiId) {
        return fenceStore.values().stream()
                .filter(f -> poiId.equals(f.getPoiId()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<GeoFence> getAllActiveFences() {
        return fenceStore.values().stream()
                .filter(f -> Boolean.TRUE.equals(f.getEnabled()))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean isPointInFence(Double longitude, Double latitude, GeoFence fence) {
        if ("CIRCLE".equals(fence.getFenceType())) {
            return isPointInCircle(longitude, latitude, fence);
        } else if ("POLYGON".equals(fence.getFenceType())) {
            return isPointInPolygon(longitude, latitude, fence);
        }
        return false;
    }
    
    /**
     * Haversine公式计算两点间距离
     */
    private double haversineDistance(Double lon1, Double lat1, Double lon2, Double lat2) {
        final double R = 6371000; // 地球半径(米)
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    /**
     * 检查点是否在圆形围栏内
     */
    private boolean isPointInCircle(Double longitude, Double latitude, GeoFence fence) {
        double distance = haversineDistance(
                longitude, latitude,
                fence.getCenterLongitude(), fence.getCenterLatitude());
        return distance <= fence.getRadius();
    }
    
    /**
     * 射线法检查点是否在多边形内
     */
    private boolean isPointInPolygon(Double longitude, Double latitude, GeoFence fence) {
        List<List<Double>> points = fence.getPolygonPoints();
        if (points == null || points.size() < 3) return false;
        
        boolean inside = false;
        int n = points.size();
        double x = longitude;
        double y = latitude;
        
        for (int i = 0, j = n - 1; i < n; j = i++) {
            double xi = points.get(i).get(0), yi = points.get(i).get(1);
            double xj = points.get(j).get(0), yj = points.get(j).get(1);
            
            if (((yi > y) != (yj > y)) &&
                    (x < (xj - xi) * (y - yi) / (yj - yi) + xi)) {
                inside = !inside;
            }
        }
        return inside;
    }
    
    @Override
    public List<GeoFence> checkPointInFences(Double longitude, Double latitude) {
        return getAllActiveFences().stream()
                .filter(f -> isPointInFence(longitude, latitude, f))
                .collect(Collectors.toList());
    }
    
    @Override
    public void enableFence(String fenceId) {
        GeoFence fence = fenceStore.get(fenceId);
        if (fence != null) {
            fence.setEnabled(true);
            fence.setUpdateTime(java.time.LocalDateTime.now());
        }
    }
    
    @Override
    public void disableFence(String fenceId) {
        GeoFence fence = fenceStore.get(fenceId);
        if (fence != null) {
            fence.setEnabled(false);
            fence.setUpdateTime(java.time.LocalDateTime.now());
        }
    }
}
