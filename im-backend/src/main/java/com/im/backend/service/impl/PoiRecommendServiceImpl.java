package com.im.backend.service.impl;

import com.im.backend.dto.*;
import com.im.backend.entity.PoiInfo;
import com.im.backend.service.IPoiRecommendService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * POI推荐服务实现
 * 
 * @author IM Development Team
 * @version 1.0
 */
@Slf4j
@Service
public class PoiRecommendServiceImpl implements IPoiRecommendService {
    
    /** POI缓存 */
    private static final Map<String, PoiInfo> poiCache = new ConcurrentHashMap<>();
    /** 用户偏好缓存 */
    private static final Map<Long, Map<String, Double>> userPreferenceCache = new ConcurrentHashMap<>();
    
    static {
        // 初始化一些示例POI数据
        initSamplePois();
    }
    
    private static void initSamplePois() {
        for (int i = 1; i <= 100; i++) {
            PoiInfo poi = new PoiInfo();
            poi.setPoiId("POI" + String.format("%05d", i));
            poi.setName("示例商家" + i);
            poi.setCategoryCode(i % 2 == 0 ? "1100" : "2100");
            poi.setCategoryName(i % 2 == 0 ? "中餐厅" : "购物中心");
            poi.setLatitude(31.2304 + (Math.random() - 0.5) * 0.1);
            poi.setLongitude(121.4737 + (Math.random() - 0.5) * 0.1);
            poi.setAddress("上海市示例地址" + i + "号");
            poi.setRating(BigDecimal.valueOf(3.5 + Math.random() * 1.5));
            poi.setReviewCount((int)(Math.random() * 500));
            poi.setPopularity((long)(Math.random() * 10000));
            poi.setIsOpen(true);
            poiCache.put(poi.getPoiId(), poi);
        }
    }
    
    @Override
    public NearbyPoiResponse getNearbyPois(NearbyPoiRequest request) {
        long startTime = System.currentTimeMillis();
        
        // 获取所有POI
        List<PoiInfo> allPois = new ArrayList<>(poiCache.values());
        
        // 距离过滤
        List<PoiInfo> filtered = allPois.stream()
                .filter(poi -> calculateDistance(
                        request.getLatitude(), request.getLongitude(),
                        poi.getLatitude(), poi.getLongitude()) <= request.getRadius())
                .peek(poi -> poi.setDistance(calculateDistance(
                        request.getLatitude(), request.getLongitude(),
                        poi.getLatitude(), poi.getLongitude())))
                .filter(poi -> request.getCategoryCode() == null || 
                        request.getCategoryCode().equals(poi.getCategoryCode()))
                .filter(poi -> request.getKeyword() == null || 
                        poi.getName().contains(request.getKeyword()))
                .filter(poi -> !request.getOpenOnly() || poi.getIsOpen())
                .filter(poi -> request.getMinRating() == null || 
                        poi.getRating().doubleValue() >= request.getMinRating())
                .collect(Collectors.toList());
        
        // 排序
        Comparator<PoiInfo> comparator = switch (request.getSortBy()) {
            case 2 -> Comparator.comparing(PoiInfo::getRating).reversed();
            case 3 -> Comparator.comparing(PoiInfo::getPopularity).reversed();
            case 4 -> Comparator.comparing(PoiInfo::calculateScore).reversed();
            default -> Comparator.comparing(PoiInfo::getDistance);
        };
        filtered.sort(comparator);
        
        // 分页
        int fromIndex = (request.getPageNum() - 1) * request.getPageSize();
        int toIndex = Math.min(fromIndex + request.getPageSize(), filtered.size());
        List<PoiInfo> pageResult = fromIndex < filtered.size() ? 
                filtered.subList(fromIndex, toIndex) : Collections.emptyList();
        
        NearbyPoiResponse response = NearbyPoiResponse.success(pageResult, filtered.size(), request);
        response.setResponseTime(System.currentTimeMillis() - startTime);
        response.setFromCache(false);
        
        return response;
    }
    
    @Override
    public LocationRecommendResponse getPersonalizedRecommendations(LocationRecommendRequest request) {
        long startTime = System.currentTimeMillis();
        
        // 构建推荐请求
        NearbyPoiRequest nearbyRequest = new NearbyPoiRequest();
        nearbyRequest.setLatitude(request.getLatitude());
        nearbyRequest.setLongitude(request.getLongitude());
        nearbyRequest.setRadius(request.getRadius());
        nearbyRequest.setPageNum(1);
        nearbyRequest.setPageSize(request.getRecommendCount() * 3);
        
        NearbyPoiResponse nearbyResponse = getNearbyPois(nearbyRequest);
        
        // 计算个性化分数
        List<LocationRecommendResponse.RecommendItem> recommendations = new ArrayList<>();
        Map<String, Double> userPrefs = userPreferenceCache.getOrDefault(request.getUserId(), new HashMap<>());
        
        for (PoiInfo poi : nearbyResponse.getPoiList()) {
            LocationRecommendResponse.RecommendItem item = new LocationRecommendResponse.RecommendItem();
            item.setPoi(poi);
            item.setDistance(poi.getDistance());
            
            // 计算推荐分数
            double score = calculateRecommendScore(poi, request, userPrefs);
            item.setScore(score);
            
            // 推荐原因
            item.setReason(generateRecommendReason(poi, request));
            
            recommendations.add(item);
        }
        
        // 排序并截取
        recommendations.sort(Comparator.comparing(LocationRecommendResponse.RecommendItem::getScore).reversed());
        recommendations = recommendations.subList(0, Math.min(request.getRecommendCount(), recommendations.size()));
        
        LocationRecommendResponse response = LocationRecommendResponse.success(recommendations, request);
        response.setResponseTime(System.currentTimeMillis() - startTime);
        response.setRecommendReason("基于您的位置和偏好推荐");
        
        // 个性化因子
        LocationRecommendResponse.PersonalizationFactors factors = new LocationRecommendResponse.PersonalizationFactors();
        factors.setDistanceWeight(0.3);
        factors.setRatingWeight(0.2);
        factors.setPopularityWeight(0.2);
        factors.setPreferenceWeight(0.3);
        factors.setCategoryPreferences(userPrefs);
        response.setFactors(factors);
        
        return response;
    }
    
    @Override
    public NearbyPoiResponse getHotPois(double latitude, double longitude, int radius, int limit) {
        NearbyPoiRequest request = new NearbyPoiRequest();
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setRadius(radius);
        request.setPageNum(1);
        request.setPageSize(limit);
        request.setSortBy(3); // 按人气排序
        
        return getNearbyPois(request);
    }
    
    @Override
    public LocationRecommendResponse getGuessYouLike(Long userId, double latitude, double longitude) {
        LocationRecommendRequest request = new LocationRecommendRequest();
        request.setUserId(userId);
        request.setLatitude(latitude);
        request.setLongitude(longitude);
        request.setRecommendCount(10);
        request.setRadius(5000);
        
        return getPersonalizedRecommendations(request);
    }
    
    @Override
    public void refreshRecommendCache(Long userId) {
        userPreferenceCache.remove(userId);
        log.info("Refreshed recommend cache for user {}", userId);
    }
    
    @Override
    public String getRecommendReason(Long poiId, Long userId) {
        PoiInfo poi = poiCache.values().stream()
                .filter(p -> p.getId().equals(poiId))
                .findFirst()
                .orElse(null);
        
        if (poi == null) return "推荐商家";
        
        if (poi.isHighRated()) {
            return "高分好评商家";
        } else if (poi.isPopular()) {
            return "人气热门商家";
        } else if (poi.getDistance() != null && poi.getDistance() < 500) {
            return "距离您很近";
        }
        
        return "推荐商家";
    }
    
    private double calculateRecommendScore(PoiInfo poi, LocationRecommendRequest request, Map<String, Double> userPrefs) {
        double score = 0.0;
        
        // 距离分数（越近越高）
        if (poi.getDistance() != null) {
            score += Math.max(0, 10 - poi.getDistance() / 500) * 0.3;
        }
        
        // 评分分数
        if (poi.getRating() != null) {
            score += poi.getRating().doubleValue() * 0.2;
        }
        
        // 人气分数
        long popularityScore = Math.min(poi.getPopularity() / 1000, 10);
        score += popularityScore * 0.2;
        
        // 偏好匹配分数
        Double pref = userPrefs.get(poi.getCategoryCode());
        if (pref != null) {
            score += pref * 0.3;
        }
        
        return score;
    }
    
    private String generateRecommendReason(PoiInfo poi, LocationRecommendRequest request) {
        List<String> reasons = new ArrayList<>();
        
        if (poi.isHighRated()) reasons.add("评分高");
        if (poi.isPopular()) reasons.add("人气旺");
        if (poi.getDistance() != null && poi.getDistance() < 1000) reasons.add("距离近");
        
        return reasons.isEmpty() ? "推荐商家" : String.join("·", reasons);
    }
    
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final double R = 6371000;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                   Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                   Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}
