package com.im.service.lbs;

import com.im.model.Location;
import com.im.model.GeoPoint;
import com.im.repository.LocationRepository;
import com.im.cache.GeoCache;
import com.im.common.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.geo.Distance;
import org.springframework.data.geo.Metrics;
import org.springframework.data.redis.connection.RedisGeoCommands;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 地理位置服务
 * 负责用户位置更新、地理围栏、位置历史记录
 * 
 * @author IM Development Team
 * @version 1.0.0
 */
@Slf4j
@Service
public class LocationService {

    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private GeoCache geoCache;
    
    // 地理空间索引键前缀
    private static final String GEO_KEY_PREFIX = "geo:user:";
    
    // 位置更新间隔（秒）
    private static final int LOCATION_UPDATE_INTERVAL = 30;
    
    // 最大位置历史记录数
    private static final int MAX_LOCATION_HISTORY = 100;
    
    // 地理围栏检测半径（米）
    private static final double GEOFENCE_RADIUS_METERS = 500.0;
    
    /**
     * 更新用户位置
     */
    public Result<Location> updateUserLocation(Long userId, Double latitude, Double longitude, 
                                                String address, String poiName) {
        try {
            // 验证坐标有效性
            if (!isValidCoordinate(latitude, longitude)) {
                return Result.fail("无效的地理坐标");
            }
            
            // 检查更新频率
            if (!canUpdateLocation(userId)) {
                return Result.fail("位置更新过于频繁，请" + LOCATION_UPDATE_INTERVAL + "秒后再试");
            }
            
            // 创建位置记录
            Location location = Location.builder()
                    .userId(userId)
                    .latitude(latitude)
                    .longitude(longitude)
                    .address(address)
                    .poiName(poiName)
                    .accuracy(0.0)
                    .source("GPS")
                    .timestamp(LocalDateTime.now())
                    .build();
            
            // 保存到数据库
            location = locationRepository.save(location);
            
            // 更新Redis地理空间索引
            updateGeoIndex(userId, latitude, longitude);
            
            // 异步检测地理围栏
            CompletableFuture.runAsync(() -> checkGeoFences(userId, latitude, longitude));
            
            // 更新缓存
            geoCache.putUserLocation(userId, location);
            
            log.info("用户[{}]位置已更新: {}, {}", userId, latitude, longitude);
            return Result.success(location);
            
        } catch (Exception e) {
            log.error("更新用户位置失败: {}", e.getMessage(), e);
            return Result.fail("位置更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量更新用户位置
     */
    public Result<Void> batchUpdateLocations(List<LocationUpdateRequest> requests) {
        try {
            List<Location> locations = requests.stream()
                    .filter(req -> isValidCoordinate(req.getLatitude(), req.getLongitude()))
                    .map(req -> Location.builder()
                            .userId(req.getUserId())
                            .latitude(req.getLatitude())
                            .longitude(req.getLongitude())
                            .address(req.getAddress())
                            .poiName(req.getPoiName())
                            .timestamp(LocalDateTime.now())
                            .build())
                    .collect(Collectors.toList());
            
            // 批量保存
            locationRepository.saveAll(locations);
            
            // 批量更新Redis
            Map<Long, GeoPoint> geoPoints = new HashMap<>();
            for (LocationUpdateRequest req : requests) {
                geoPoints.put(req.getUserId(), new GeoPoint(req.getLongitude(), req.getLatitude()));
            }
            geoCache.batchUpdateGeoPoints(geoPoints);
            
            log.info("批量更新位置成功，共{}条记录", locations.size());
            return Result.success();
            
        } catch (Exception e) {
            log.error("批量更新位置失败: {}", e.getMessage(), e);
            return Result.fail("批量更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户当前位置
     */
    public Result<Location> getUserCurrentLocation(Long userId) {
        try {
            // 先查缓存
            Location cached = geoCache.getUserLocation(userId);
            if (cached != null) {
                return Result.success(cached);
            }
            
            // 查数据库最新位置
            Optional<Location> latest = locationRepository.findTopByUserIdOrderByTimestampDesc(userId);
            if (latest.isPresent()) {
                geoCache.putUserLocation(userId, latest.get());
                return Result.success(latest.get());
            }
            
            return Result.fail("未找到用户位置信息");
            
        } catch (Exception e) {
            log.error("获取用户位置失败: {}", e.getMessage(), e);
            return Result.fail("获取位置失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户位置历史
     */
    public Result<List<Location>> getUserLocationHistory(Long userId, LocalDateTime startTime, 
                                                          LocalDateTime endTime, Integer limit) {
        try {
            List<Location> history;
            if (limit != null && limit > 0) {
                history = locationRepository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(
                        userId, startTime, endTime, limit);
            } else {
                history = locationRepository.findByUserIdAndTimestampBetweenOrderByTimestampDesc(
                        userId, startTime, endTime);
            }
            
            return Result.success(history);
            
        } catch (Exception e) {
            log.error("获取位置历史失败: {}", e.getMessage(), e);
            return Result.fail("获取历史记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索附近的人
     */
    public Result<List<NearbyUser>> findNearbyUsers(Long userId, Double latitude, Double longitude, 
                                                     Double radiusKm, Integer limit) {
        try {
            // 使用Redis GEOSEARCH搜索附近用户
            List<NearbyUser> nearbyUsers = geoCache.findNearbyUsers(
                    userId, longitude, latitude, radiusKm, limit);
            
            // 过滤和排序
            nearbyUsers = nearbyUsers.stream()
                    .filter(u -> !u.getUserId().equals(userId))
                    .sorted(Comparator.comparing(NearbyUser::getDistance))
                    .limit(limit != null ? limit : 50)
                    .collect(Collectors.toList());
            
            // 补充用户信息
            enrichUserInfo(nearbyUsers);
            
            log.info("用户[{}]搜索附近的人，找到{}个结果", userId, nearbyUsers.size());
            return Result.success(nearbyUsers);
            
        } catch (Exception e) {
            log.error("搜索附近的人失败: {}", e.getMessage(), e);
            return Result.fail("搜索失败: " + e.getMessage());
        }
    }
    
    /**
     * 创建地理围栏
     */
    public Result<GeoFence> createGeoFence(Long userId, String name, Double latitude, 
                                           Double longitude, Double radius, String type) {
        try {
            GeoFence fence = GeoFence.builder()
                    .userId(userId)
                    .name(name)
                    .latitude(latitude)
                    .longitude(longitude)
                    .radius(radius)
                    .type(type)
                    .status(1)
                    .createTime(LocalDateTime.now())
                    .build();
            
            // 保存围栏
            geoCache.saveGeoFence(fence);
            
            log.info("用户[{}]创建地理围栏: {}", userId, name);
            return Result.success(fence);
            
        } catch (Exception e) {
            log.error("创建地理围栏失败: {}", e.getMessage(), e);
            return Result.fail("创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取用户的地理围栏列表
     */
    public Result<List<GeoFence>> getUserGeoFences(Long userId) {
        try {
            List<GeoFence> fences = geoCache.getUserGeoFences(userId);
            return Result.success(fences);
        } catch (Exception e) {
            log.error("获取地理围栏失败: {}", e.getMessage(), e);
            return Result.fail("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除地理围栏
     */
    public Result<Void> deleteGeoFence(Long userId, String fenceId) {
        try {
            geoCache.deleteGeoFence(userId, fenceId);
            log.info("用户[{}]删除地理围栏: {}", userId, fenceId);
            return Result.success();
        } catch (Exception e) {
            log.error("删除地理围栏失败: {}", e.getMessage(), e);
            return Result.fail("删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 计算两点距离
     */
    public Result<Double> calculateDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        try {
            double distance = haversineDistance(lat1, lon1, lat2, lon2);
            return Result.success(distance);
        } catch (Exception e) {
            log.error("计算距离失败: {}", e.getMessage(), e);
            return Result.fail("计算失败: " + e.getMessage());
        }
    }
    
    /**
     * 逆地理编码：坐标转地址
     */
    public Result<String> reverseGeocode(Double latitude, Double longitude) {
        try {
            // 调用地图服务API
            String address = geoCache.reverseGeocode(latitude, longitude);
            return Result.success(address);
        } catch (Exception e) {
            log.error("逆地理编码失败: {}", e.getMessage(), e);
            return Result.fail("编码失败: " + e.getMessage());
        }
    }
    
    // ==================== 私有方法 ====================
    
    private boolean isValidCoordinate(Double latitude, Double longitude) {
        return latitude != null && longitude != null
                && latitude >= -90 && latitude <= 90
                && longitude >= -180 && longitude <= 180;
    }
    
    private boolean canUpdateLocation(Long userId) {
        String key = "loc:update:" + userId;
        Long ttl = geoCache.getKeyTTL(key);
        return ttl == null || ttl <= 0;
    }
    
    private void updateGeoIndex(Long userId, Double latitude, Double longitude) {
        String key = GEO_KEY_PREFIX + "all";
        geoCache.addGeoPoint(key, longitude, latitude, userId.toString());
    }
    
    private void checkGeoFences(Long userId, Double latitude, Double longitude) {
        try {
            List<GeoFence> fences = geoCache.getUserGeoFences(userId);
            for (GeoFence fence : fences) {
                double distance = haversineDistance(
                        latitude, longitude, fence.getLatitude(), fence.getLongitude());
                
                boolean inside = distance <= fence.getRadius();
                boolean wasInside = geoCache.wasUserInFence(userId, fence.getId());
                
                if (inside && !wasInside) {
                    // 进入围栏
                    triggerGeoFenceEvent(userId, fence, "ENTER", distance);
                    geoCache.setUserInFence(userId, fence.getId(), true);
                } else if (!inside && wasInside) {
                    // 离开围栏
                    triggerGeoFenceEvent(userId, fence, "EXIT", distance);
                    geoCache.setUserInFence(userId, fence.getId(), false);
                }
            }
        } catch (Exception e) {
            log.error("地理围栏检测失败: {}", e.getMessage(), e);
        }
    }
    
    private void triggerGeoFenceEvent(Long userId, GeoFence fence, String eventType, Double distance) {
        GeoFenceEvent event = GeoFenceEvent.builder()
                .userId(userId)
                .fenceId(fence.getId())
                .fenceName(fence.getName())
                .eventType(eventType)
                .distance(distance)
                .timestamp(LocalDateTime.now())
                .build();
        
        // 发送通知
        geoCache.publishGeoFenceEvent(event);
        log.info("用户[{}]{}地理围栏: {}，距离: {}米", 
                userId, eventType.equals("ENTER") ? "进入" : "离开", fence.getName(), (int)(distance * 1000));
    }
    
    private void enrichUserInfo(List<NearbyUser> users) {
        // 补充用户昵称、头像等信息
        for (NearbyUser user : users) {
            // 从用户服务获取信息
            // 这里简化处理
        }
    }
    
    /**
     * Haversine公式计算两点间距离（公里）
     */
    private double haversineDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371; // 地球半径（公里）
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
}

/**
 * 位置更新请求
 */
class LocationUpdateRequest {
    private Long userId;
    private Double latitude;
    private Double longitude;
    private String address;
    private String poiName;
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPoiName() { return poiName; }
    public void setPoiName(String poiName) { this.poiName = poiName; }
}

/**
 * 附近用户
 */
class NearbyUser {
    private Long userId;
    private String nickname;
    private String avatar;
    private Double latitude;
    private Double longitude;
    private Double distance; // 公里
    private LocalDateTime lastActive;
    
    // Getters and Setters
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getNickname() { return nickname; }
    public void setNickname(String nickname) { this.nickname = nickname; }
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public Double getDistance() { return distance; }
    public void setDistance(Double distance) { this.distance = distance; }
    public LocalDateTime getLastActive() { return lastActive; }
    public void setLastActive(LocalDateTime lastActive) { this.lastActive = lastActive; }
}

/**
 * 地理围栏
 */
class GeoFence {
    private String id;
    private Long userId;
    private String name;
    private Double latitude;
    private Double longitude;
    private Double radius; // 米
    private String type;
    private Integer status;
    private LocalDateTime createTime;
    
    @java.beans.ConstructorProperties({"id", "userId", "name", "latitude", "longitude", "radius", "type", "status", "createTime"})
    GeoFence(String id, Long userId, String name, Double latitude, Double longitude, Double radius, String type, Integer status, LocalDateTime createTime) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
        this.type = type;
        this.status = status;
        this.createTime = createTime;
    }
    
    public static GeoFenceBuilder builder() {
        return new GeoFenceBuilder();
    }
    
    public String getId() { return id; }
    public Long getUserId() { return userId; }
    public String getName() { return name; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Double getRadius() { return radius; }
    public String getType() { return type; }
    public Integer getStatus() { return status; }
    public LocalDateTime getCreateTime() { return createTime; }
    
    public static class GeoFenceBuilder {
        private String id = UUID.randomUUID().toString();
        private Long userId;
        private String name;
        private Double latitude;
        private Double longitude;
        private Double radius;
        private String type;
        private Integer status;
        private LocalDateTime createTime;
        
        public GeoFenceBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public GeoFenceBuilder name(String name) {
            this.name = name;
            return this;
        }
        
        public GeoFenceBuilder latitude(Double latitude) {
            this.latitude = latitude;
            return this;
        }
        
        public GeoFenceBuilder longitude(Double longitude) {
            this.longitude = longitude;
            return this;
        }
        
        public GeoFenceBuilder radius(Double radius) {
            this.radius = radius;
            return this;
        }
        
        public GeoFenceBuilder type(String type) {
            this.type = type;
            return this;
        }
        
        public GeoFenceBuilder status(Integer status) {
            this.status = status;
            return this;
        }
        
        public GeoFenceBuilder createTime(LocalDateTime createTime) {
            this.createTime = createTime;
            return this;
        }
        
        public GeoFence build() {
            return new GeoFence(id, userId, name, latitude, longitude, radius, type, status, createTime);
        }
    }
}

/**
 * 地理围栏事件
 */
class GeoFenceEvent {
    private Long userId;
    private String fenceId;
    private String fenceName;
    private String eventType; // ENTER/EXIT
    private Double distance;
    private LocalDateTime timestamp;
    
    @java.beans.ConstructorProperties({"userId", "fenceId", "fenceName", "eventType", "distance", "timestamp"})
    GeoFenceEvent(Long userId, String fenceId, String fenceName, String eventType, Double distance, LocalDateTime timestamp) {
        this.userId = userId;
        this.fenceId = fenceId;
        this.fenceName = fenceName;
        this.eventType = eventType;
        this.distance = distance;
        this.timestamp = timestamp;
    }
    
    public static GeoFenceEventBuilder builder() {
        return new GeoFenceEventBuilder();
    }
    
    public Long getUserId() { return userId; }
    public String getFenceId() { return fenceId; }
    public String getFenceName() { return fenceName; }
    public String getEventType() { return eventType; }
    public Double getDistance() { return distance; }
    public LocalDateTime getTimestamp() { return timestamp; }
    
    public static class GeoFenceEventBuilder {
        private Long userId;
        private String fenceId;
        private String fenceName;
        private String eventType;
        private Double distance;
        private LocalDateTime timestamp;
        
        public GeoFenceEventBuilder userId(Long userId) {
            this.userId = userId;
            return this;
        }
        
        public GeoFenceEventBuilder fenceId(String fenceId) {
            this.fenceId = fenceId;
            return this;
        }
        
        public GeoFenceEventBuilder fenceName(String fenceName) {
            this.fenceName = fenceName;
            return this;
        }
        
        public GeoFenceEventBuilder eventType(String eventType) {
            this.eventType = eventType;
            return this;
        }
        
        public GeoFenceEventBuilder distance(Double distance) {
            this.distance = distance;
            return this;
        }
        
        public GeoFenceEventBuilder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public GeoFenceEvent build() {
            return new GeoFenceEvent(userId, fenceId, fenceName, eventType, distance, timestamp);
        }
    }
}
