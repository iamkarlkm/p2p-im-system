package com.im.backend.service.impl;

import com.im.backend.entity.LocationPoint;
import com.im.backend.service.ILocationService;
import com.im.backend.utils.SpatialIndexUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 位置服务实现
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Slf4j
@Service
public class LocationServiceImpl implements ILocationService {
    
    /** 位置点存储 */
    private static final Map<Long, LocationPoint> locationStore = new ConcurrentHashMap<>();
    private static final Map<Long, LocationPoint> userLatestLocation = new ConcurrentHashMap<>();
    private static long idCounter = 1;
    
    @Override
    public LocationPoint reportLocation(LocationPoint location) {
        location.setId(idCounter++);
        location.setCreateTime(java.time.LocalDateTime.now());
        location.setUpdateTime(java.time.LocalDateTime.now());
        
        locationStore.put(location.getId(), location);
        userLatestLocation.put(location.getUserId(), location);
        
        // 添加到空间索引
        SpatialIndexUtils.addLocation(location, 9);
        
        log.info("Location reported: user={}, lat={}, lon={}", 
                location.getUserId(), location.getLatitude(), location.getLongitude());
        
        return location;
    }
    
    @Override
    public List<LocationPoint> batchReportLocations(List<LocationPoint> locations) {
        List<LocationPoint> results = new ArrayList<>();
        for (LocationPoint location : locations) {
            results.add(reportLocation(location));
        }
        return results;
    }
    
    @Override
    public LocationPoint getLocationById(Long id) {
        return locationStore.get(id);
    }
    
    @Override
    public LocationPoint getUserLatestLocation(Long userId) {
        return userLatestLocation.get(userId);
    }
    
    @Override
    public List<LocationPoint> getUserLocationHistory(Long userId, int days) {
        List<LocationPoint> history = new ArrayList<>();
        for (LocationPoint point : locationStore.values()) {
            if (point.getUserId().equals(userId)) {
                history.add(point);
            }
        }
        return history.stream()
                .sorted(Comparator.comparing(LocationPoint::getCreateTime).reversed())
                .toList();
    }
    
    @Override
    public List<LocationPoint> nearbySearch(double latitude, double longitude, double radius) {
        return SpatialIndexUtils.circularQuery(latitude, longitude, radius);
    }
    
    @Override
    public List<LocationPoint> nearbySearchWithPage(double latitude, double longitude, double radius, int pageNum, int pageSize) {
        List<LocationPoint> all = nearbySearch(latitude, longitude, radius);
        int fromIndex = (pageNum - 1) * pageSize;
        int toIndex = Math.min(fromIndex + pageSize, all.size());
        
        if (fromIndex >= all.size()) {
            return Collections.emptyList();
        }
        return all.subList(fromIndex, toIndex);
    }
    
    @Override
    public List<LocationPoint> boundingBoxSearch(double minLat, double maxLat, double minLon, double maxLon) {
        return SpatialIndexUtils.boundingBoxQuery(minLat, maxLat, minLon, maxLon);
    }
    
    @Override
    public int deleteExpiredLocations() {
        int count = 0;
        Iterator<Map.Entry<Long, LocationPoint>> iterator = locationStore.entrySet().iterator();
        
        while (iterator.hasNext()) {
            LocationPoint point = iterator.next().getValue();
            if (point.isExpired()) {
                SpatialIndexUtils.removeLocation(point, 9);
                iterator.remove();
                count++;
            }
        }
        
        log.info("Deleted {} expired locations", count);
        return count;
    }
    
    @Override
    public boolean updateShareStatus(Long locationId, boolean isShared) {
        LocationPoint point = locationStore.get(locationId);
        if (point != null) {
            point.setIsShared(isShared);
            point.setUpdateTime(java.time.LocalDateTime.now());
            return true;
        }
        return false;
    }
    
    @Override
    public Long countNearbyUsers(double latitude, double longitude, double radius) {
        return (long) nearbySearch(latitude, longitude, radius).size();
    }
    
    @Override
    public Map<String, Object> getLocationDistribution(String geohash) {
        Map<String, Object> distribution = new HashMap<>();
        Set<LocationPoint> points = SpatialIndexUtils.getPointsInGrid(geohash);
        
        distribution.put("totalPoints", points.size());
        distribution.put("geohash", geohash);
        
        // 按类型统计
        Map<Integer, Long> typeCount = new HashMap<>();
        for (LocationPoint point : points) {
            typeCount.merge(point.getLocationType(), 1L, Long::sum);
        }
        distribution.put("typeDistribution", typeCount);
        
        return distribution;
    }
}
