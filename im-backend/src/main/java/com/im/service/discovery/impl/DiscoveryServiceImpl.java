package com.im.service.discovery.impl;

import com.im.service.discovery.DiscoveryService;
import com.im.entity.discovery.*;
import com.im.mapper.discovery.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.StringRedisTemplate;
import lombok.extern.slf4j.Slf4j;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 探店发现服务实现类
 */
@Slf4j
@Service
public class DiscoveryServiceImpl implements DiscoveryService {
    
    @Autowired
    private DiscoveryRecommendationMapper recommendationMapper;
    
    @Autowired
    private DiscoveryNewStoreMapper newStoreMapper;
    
    @Autowired
    private DiscoveryRankingMapper rankingMapper;
    
    @Autowired
    private DiscoveryCheckInMapper checkInMapper;
    
    @Autowired
    private DiscoveryRouteMapper routeMapper;
    
    @Autowired
    private DiscoveryContentMapper contentMapper;
    
    @Autowired
    private StringRedisTemplate redisTemplate;
    
    @Override
    public List<DiscoveryRecommendation> getRecommendations(Long userId, Double longitude, Double latitude, 
                                                             Integer pageNum, Integer pageSize) {
        log.info("获取用户探店推荐, userId={}, longitude={}, latitude={}", userId, longitude, latitude);
        // 计算GeoHash
        String geoHash = calculateGeoHash(longitude, latitude, 6);
        
        // 从Redis获取用户画像标签
        Set<String> userTags = getUserProfileTags(userId);
        
        // 获取推荐列表（结合协同过滤和地理位置）
        List<DiscoveryRecommendation> recommendations = recommendationMapper
            .selectByUserAndLocation(userId, geoHash, longitude, latitude, (pageNum - 1) * pageSize, pageSize);
        
        // 补充实时数据
        recommendations.forEach(rec -> enrichRecommendationData(rec, userTags));
        
        return recommendations;
    }
    
    @Override
    public List<DiscoveryNewStore> getNewStores(String cityCode, Double longitude, Double latitude,
                                                Integer pageNum, Integer pageSize) {
        log.info("获取新店发现, cityCode={}", cityCode);
        return newStoreMapper.selectByCityAndLocation(cityCode, longitude, latitude, 
                                                      (pageNum - 1) * pageSize, pageSize);
    }
    
    @Override
    public List<DiscoveryRanking> getRankings(String cityCode, String rankingType, 
                                              Integer pageNum, Integer pageSize) {
        log.info("获取探店榜单, cityCode={}, rankingType={}", cityCode, rankingType);
        return rankingMapper.selectByCityAndType(cityCode, rankingType, 
                                                 (pageNum - 1) * pageSize, pageSize);
    }
    
    @Override
    public DiscoveryRanking getRankingDetail(Long rankingId) {
        log.info("获取榜单详情, rankingId={}", rankingId);
        DiscoveryRanking ranking = rankingMapper.selectById(rankingId);
        if (ranking != null && ranking.getItems() == null) {
            List<DiscoveryRankingItem> items = rankingMapper.selectRankingItems(rankingId);
            ranking.setItems(items);
        }
        return ranking;
    }
    
    @Override
    public DiscoveryCheckIn createCheckIn(DiscoveryCheckIn checkIn) {
        log.info("创建打卡记录, userId={}, poiId={}", checkIn.getUserId(), checkIn.getPoiId());
        checkIn.setCreateTime(new Date());
        checkIn.setUpdateTime(new Date());
        checkIn.setDeleted(false);
        checkInMapper.insert(checkIn);
        
        // 更新POI打卡计数
        incrementPoiCheckInCount(checkIn.getPoiId());
        
        // 更新用户探店统计
        updateUserDiscoveryStats(checkIn.getUserId());
        
        return checkIn;
    }
    
    @Override
    public List<DiscoveryCheckIn> getUserCheckIns(Long userId, Integer pageNum, Integer pageSize) {
        return checkInMapper.selectByUserId(userId, (pageNum - 1) * pageSize, pageSize);
    }
    
    @Override
    public List<DiscoveryCheckIn> getPoiCheckIns(Long poiId, Integer pageNum, Integer pageSize) {
        return checkInMapper.selectByPoiId(poiId, (pageNum - 1) * pageSize, pageSize);
    }
    
    @Override
    public List<DiscoveryRoute> getRoutes(String sceneTag, String cityCode, 
                                          Integer pageNum, Integer pageSize) {
        return routeMapper.selectBySceneAndCity(sceneTag, cityCode, 
                                                (pageNum - 1) * pageSize, pageSize);
    }
    
    @Override
    public DiscoveryRoute getRouteDetail(Long routeId) {
        DiscoveryRoute route = routeMapper.selectById(routeId);
        if (route != null && route.getPoiList() == null) {
            List<DiscoveryRoutePoi> poiList = routeMapper.selectRoutePois(routeId);
            route.setPoiList(poiList);
        }
        return route;
    }
    
    @Override
    public DiscoveryRoute createRoute(DiscoveryRoute route) {
        route.setCreateTime(new Date());
        route.setUpdateTime(new Date());
        route.setStatus("DRAFT");
        routeMapper.insert(route);
        
        // 插入路线POI节点
        if (route.getPoiList() != null && !route.getPoiList().isEmpty()) {
            for (DiscoveryRoutePoi poi : route.getPoiList()) {
                poi.setRouteId(route.getId());
            }
            routeMapper.batchInsertRoutePois(route.getPoiList());
        }
        
        return route;
    }
    
    @Override
    public List<DiscoveryContent> getContents(Long poiId, String contentType, 
                                              Integer pageNum, Integer pageSize) {
        return contentMapper.selectByPoiAndType(poiId, contentType, 
                                                (pageNum - 1) * pageSize, pageSize);
    }
    
    @Override
    public List<DiscoveryContent> searchContents(String keyword, String cityCode,
                                                 Integer pageNum, Integer pageSize) {
        return contentMapper.searchByKeyword(keyword, cityCode, 
                                             (pageNum - 1) * pageSize, pageSize);
    }
    
    @Override
    public boolean checkGeofenceCheckIn(Long userId, Double longitude, Double latitude) {
        // 查询用户附近的POI
        List<Map<String, Object>> nearbyPois = getNearbyPois(longitude, latitude, 200);
        
        for (Map<String, Object> poi : nearbyPois) {
            Long poiId = (Long) poi.get("id");
            Double poiLng = (Double) poi.get("longitude");
            Double poiLat = (Double) poi.get("latitude");
            Integer radius = (Integer) poi.getOrDefault("checkInRadius", 100);
            
            // 计算距离
            double distance = calculateDistance(longitude, latitude, poiLng, poiLat);
            
            if (distance <= radius) {
                // 自动创建打卡记录
                DiscoveryCheckIn checkIn = DiscoveryCheckIn.builder()
                    .userId(userId)
                    .poiId(poiId)
                    .longitude(longitude)
                    .latitude(latitude)
                    .checkInType("AUTO")
                    .enterGeofence(true)
                    .checkInTime(new Date())
                    .build();
                createCheckIn(checkIn);
                return true;
            }
        }
        return false;
    }
    
    @Override
    public Map<String, Object> getUserDiscoveryStats(Long userId) {
        Map<String, Object> stats = new HashMap<>();
        
        // 打卡统计
        Integer totalCheckIns = checkInMapper.countByUserId(userId);
        Integer totalPois = checkInMapper.countDistinctPoisByUserId(userId);
        
        stats.put("totalCheckIns", totalCheckIns);
        stats.put("totalPois", totalPois);
        stats.put("expertLevel", calculateExpertLevel(totalCheckIns));
        stats.put("footprintCities", checkInMapper.countDistinctCitiesByUserId(userId));
        
        return stats;
    }
    
    @Override
    public List<Map<String, Object>> getNearbyHotSpots(Double longitude, Double latitude, 
                                                       Integer radius, Integer limit) {
        return recommendationMapper.selectNearbyHotSpots(longitude, latitude, radius, limit);
    }
    
    @Override
    public DiscoveryRoute generateSmartRoute(Long userId, Double startLng, Double startLat,
                                             String sceneTag, Integer poiCount, Double maxDistance) {
        log.info("生成智能探店路线, userId={}, sceneTag={}", userId, sceneTag);
        
        // 获取用户偏好标签
        Set<String> userTags = getUserProfileTags(userId);
        
        // 基于TSP算法生成最优路线
        List<Map<String, Object>> optimalPois = calculateOptimalRoute(startLng, startLat, 
                                                                      userTags, poiCount, maxDistance);
        
        DiscoveryRoute route = DiscoveryRoute.builder()
            .userId(userId)
            .routeType("SYSTEM")
            .sceneTag(sceneTag)
            .startLongitude(startLng)
            .startLatitude(startLat)
            .status("PUBLISHED")
            .publishTime(new Date())
            .build();
        
        // 构建路线POI节点
        List<DiscoveryRoutePoi> poiList = new ArrayList<>();
        int sequence = 1;
        for (Map<String, Object> poi : optimalPois) {
            DiscoveryRoutePoi routePoi = DiscoveryRoutePoi.builder()
                .poiId((Long) poi.get("poiId"))
                .sequence(sequence++)
                .poiName((String) poi.get("name"))
                .poiType((String) poi.get("type"))
                .longitude((Double) poi.get("longitude"))
                .latitude((Double) poi.get("latitude"))
                .estimatedCost((java.math.BigDecimal) poi.get("avgPrice"))
                .recommendText((String) poi.get("recommendReason"))
                .build();
            poiList.add(routePoi);
        }
        route.setPoiList(poiList);
        route.setPoiCount(poiList.size());
        
        return route;
    }
    
    @Override
    public List<Map<String, Object>> getUserFootprintMap(Long userId, String cityCode) {
        return checkInMapper.selectUserFootprint(userId, cityCode);
    }
    
    @Override
    public List<Map<String, Object>> getExpertUsers(String cityCode, Integer pageNum, Integer pageSize) {
        return checkInMapper.selectExpertUsers(cityCode, (pageNum - 1) * pageSize, pageSize);
    }
    
    @Override
    public boolean followExpert(Long userId, Long expertId, boolean follow) {
        String key = "discovery:user:following:" + userId;
        if (follow) {
            redisTemplate.opsForSet().add(key, expertId.toString());
        } else {
            redisTemplate.opsForSet().remove(key, expertId.toString());
        }
        return true;
    }
    
    @Override
    public List<DiscoveryContent> getFollowingExpertContents(Long userId, Integer pageNum, Integer pageSize) {
        // 获取关注列表
        String key = "discovery:user:following:" + userId;
        Set<String> followingIds = redisTemplate.opsForSet().members(key);
        
        if (followingIds == null || followingIds.isEmpty()) {
            return Collections.emptyList();
        }
        
        List<Long> expertIds = followingIds.stream()
            .map(Long::parseLong)
            .collect(Collectors.toList());
        
        return contentMapper.selectByUserIds(expertIds, (pageNum - 1) * pageSize, pageSize);
    }
    
    // ==================== 私有辅助方法 ====================
    
    private String calculateGeoHash(Double longitude, Double latitude, int precision) {
        // 简化版GeoHash计算
        return String.format("%.3f,%.3f", longitude, latitude).replace(".", "").substring(0, precision);
    }
    
    private Set<String> getUserProfileTags(Long userId) {
        String key = "discovery:user:tags:" + userId;
        return redisTemplate.opsForSet().members(key);
    }
    
    private void enrichRecommendationData(DiscoveryRecommendation rec, Set<String> userTags) {
        // 计算综合匹配度
        double matchScore = 0;
        if (rec.getMatchTags() != null && userTags != null) {
            long matchedTags = rec.getMatchTags().stream()
                .filter(userTags::contains)
                .count();
            matchScore = (double) matchedTags / rec.getMatchTags().size();
        }
        rec.setRecommendScore(rec.getRecommendScore() * (0.7 + 0.3 * matchScore));
    }
    
    private void incrementPoiCheckInCount(Long poiId) {
        String key = "discovery:poi:checkin:count:" + poiId;
        redisTemplate.opsForValue().increment(key);
    }
    
    private void updateUserDiscoveryStats(Long userId) {
        String key = "discovery:user:stats:" + userId;
        redisTemplate.opsForHash().increment(key, "checkInCount", 1);
    }
    
    private int calculateExpertLevel(int totalCheckIns) {
        if (totalCheckIns >= 100) return 5;
        if (totalCheckIns >= 50) return 4;
        if (totalCheckIns >= 20) return 3;
        if (totalCheckIns >= 5) return 2;
        return 1;
    }
    
    private double calculateDistance(Double lng1, Double lat1, Double lng2, Double lat2) {
        final double R = 6371000; // 地球半径（米）
        double latDistance = Math.toRadians(lat2 - lat1);
        double lngDistance = Math.toRadians(lng2 - lng1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lngDistance / 2) * Math.sin(lngDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    private List<Map<String, Object>> getNearbyPois(Double longitude, Double latitude, Integer radius) {
        return recommendationMapper.selectNearbyPois(longitude, latitude, radius);
    }
    
    private List<Map<String, Object>> calculateOptimalRoute(Double startLng, Double startLat,
                                                            Set<String> userTags, Integer poiCount, 
                                                            Double maxDistance) {
        // 简化版TSP路线规划
        List<Map<String, Object>> candidates = recommendationMapper
            .selectRouteCandidates(startLng, startLat, userTags, poiCount * 3, maxDistance);
        
        // 按距离排序，选择最近的poiCount个
        candidates.sort((a, b) -> {
            Double distA = (Double) a.get("distance");
            Double distB = (Double) b.get("distance");
            return distA.compareTo(distB);
        });
        
        return candidates.subList(0, Math.min(poiCount, candidates.size()));
    }
}
