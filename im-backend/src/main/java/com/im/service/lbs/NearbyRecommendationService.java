package com.im.service.lbs;

import com.im.model.Location;
import com.im.model.POI;
import com.im.model.UserProfile;
import com.im.repository.LocationRepository;
import com.im.repository.POIRepository;
import com.im.repository.UserRepository;
import com.im.cache.RecommendationCache;
import com.im.common.Result;
import com.im.service.ml.RecommendationMLService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 附近推荐服务
 * 基于地理位置的智能推荐算法引擎
 * 支持附近的人、附近商家、活动推荐
 * 
 * @author IM Development Team
 * @version 1.0.0
 */
@Slf4j
@Service
public class NearbyRecommendationService {

    @Autowired
    private LocationRepository locationRepository;
    
    @Autowired
    private POIRepository poiRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RecommendationCache recommendationCache;
    
    @Autowired
    private RecommendationMLService mlService;
    
    // 推荐半径配置（公里）
    private static final double NEARBY_PEOPLE_RADIUS = 5.0;
    private static final double NEARBY_POI_RADIUS = 10.0;
    private static final double NEARBY_EVENT_RADIUS = 20.0;
    
    // 推荐权重
    private static final double DISTANCE_WEIGHT = 0.25;
    private static final double SIMILARITY_WEIGHT = 0.25;
    private static final double ACTIVITY_WEIGHT = 0.20;
    private static final double POPULARITY_WEIGHT = 0.15;
    private static final double FRESHNESS_WEIGHT = 0.15;
    
    // 活跃时间阈值（分钟）
    private static final int ACTIVE_THRESHOLD_MINUTES = 30;
    
    /**
     * 获取附近的人推荐
     */
    public Result<List<RecommendedPerson>> getNearbyPeopleRecommendations(
            Long userId, Double latitude, Double longitude, Integer limit) {
        try {
            if (limit == null) limit = 20;
            
            // 获取用户画像
            UserProfile userProfile = getUserProfile(userId);
            
            // 搜索附近用户
            List<Location> nearbyLocations = locationRepository.findNearbyUsers(
                    latitude, longitude, NEARBY_PEOPLE_RADIUS, limit * 3);
            
            List<RecommendedPerson> recommendations = new ArrayList<>();
            
            for (Location loc : nearbyLocations) {
                // 跳过自己
                if (loc.getUserId().equals(userId)) continue;
                
                // 检查用户是否活跃
                if (!isUserActive(loc.getTimestamp())) continue;
                
                // 获取目标用户画像
                UserProfile targetProfile = getUserProfile(loc.getUserId());
                
                // 计算距离
                double distance = haversineDistance(
                        latitude, longitude, loc.getLatitude(), loc.getLongitude());
                
                // 计算推荐分数
                double score = calculatePersonRecommendationScore(
                        userProfile, targetProfile, distance, loc.getTimestamp());
                
                if (score > 0.3) { // 最低分数阈值
                    RecommendedPerson person = RecommendedPerson.builder()
                            .userId(loc.getUserId())
                            .nickname(targetProfile.getNickname())
                            .avatar(targetProfile.getAvatar())
                            .gender(targetProfile.getGender())
                            .age(targetProfile.getAge())
                            .bio(targetProfile.getBio())
                            .tags(targetProfile.getTags())
                            .latitude(loc.getLatitude())
                            .longitude(loc.getLongitude())
                            .distance(distance)
                            .lastActive(loc.getTimestamp())
                            .similarityScore(calculateUserSimilarity(userProfile, targetProfile))
                            .recommendScore(score)
                            .build();
                    
                    recommendations.add(person);
                }
            }
            
            // 排序并限制数量
            recommendations.sort((a, b) -> Double.compare(b.getRecommendScore(), a.getRecommendScore()));
            if (recommendations.size() > limit) {
                recommendations = recommendations.subList(0, limit);
            }
            
            // 记录推荐历史
            recordRecommendationHistory(userId, "people", recommendations.size());
            
            log.info("为用户[{}]推荐附近的人，返回{}个结果", userId, recommendations.size());
            return Result.success(recommendations);
            
        } catch (Exception e) {
            log.error("获取附近的人推荐失败: {}", e.getMessage(), e);
            return Result.fail("获取推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取附近商家推荐
     */
    public Result<List<RecommendedMerchant>> getNearbyMerchantRecommendations(
            Long userId, Double latitude, Double longitude, String category, Integer limit) {
        try {
            if (limit == null) limit = 20;
            
            // 获取用户偏好
            UserPreference preference = getUserPreference(userId);
            
            // 获取时间上下文
            TimeContext timeContext = getCurrentTimeContext();
            
            // 搜索附近商家
            List<POI> nearbyPOIs = poiRepository.findNearbyPOIs(
                    latitude, longitude, NEARBY_POI_RADIUS, category, limit * 3);
            
            List<RecommendedMerchant> recommendations = new ArrayList<>();
            
            for (POI poi : nearbyPOIs) {
                // 只返回已审核的商家
                if (poi.getStatus() != 2) continue;
                
                // 计算距离
                double distance = haversineDistance(
                        latitude, longitude, poi.getLatitude(), poi.getLongitude());
                
                // 计算推荐分数
                double score = calculateMerchantRecommendationScore(
                        poi, distance, preference, timeContext);
                
                RecommendedMerchant merchant = RecommendedMerchant.builder()
                        .merchantId(poi.getId())
                        .name(poi.getName())
                        .category(poi.getCategory())
                        .categoryName(getCategoryName(poi.getCategory()))
                        .latitude(poi.getLatitude())
                        .longitude(poi.getLongitude())
                        .address(poi.getAddress())
                        .phone(poi.getPhone())
                        .rating(poi.getRating())
                        .ratingCount(poi.getRatingCount())
                        .priceRange(poi.getPriceRange())
                        .businessHours(poi.getBusinessHours())
                        .tags(poi.getTags())
                        .images(poi.getImages())
                        .distance(distance)
                        .isOpen(isBusinessOpen(poi.getBusinessHours()))
                        .recommendReason(generateRecommendReason(poi, timeContext))
                        .recommendScore(score)
                        .build();
                
                recommendations.add(merchant);
            }
            
            // 排序并限制
            recommendations.sort((a, b) -> Double.compare(b.getRecommendScore(), a.getRecommendScore()));
            if (recommendations.size() > limit) {
                recommendations = recommendations.subList(0, limit);
            }
            
            recordRecommendationHistory(userId, "merchant", recommendations.size());
            
            log.info("为用户[{}]推荐附近商家，返回{}个结果", userId, recommendations.size());
            return Result.success(recommendations);
            
        } catch (Exception e) {
            log.error("获取商家推荐失败: {}", e.getMessage(), e);
            return Result.fail("获取推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取场景化推荐
     */
    public Result<List<RecommendedItem>> getSceneBasedRecommendations(
            Long userId, Double latitude, Double longitude, String scene, Integer limit) {
        try {
            if (limit == null) limit = 15;
            
            List<RecommendedItem> results = new ArrayList<>();
            
            switch (scene) {
                case "lunch":
                    results = getLunchRecommendations(userId, latitude, longitude, limit);
                    break;
                case "dinner":
                    results = getDinnerRecommendations(userId, latitude, longitude, limit);
                    break;
                case "coffee":
                    results = getCoffeeRecommendations(userId, latitude, longitude, limit);
                    break;
                case "shopping":
                    results = getShoppingRecommendations(userId, latitude, longitude, limit);
                    break;
                case "entertainment":
                    results = getEntertainmentRecommendations(userId, latitude, longitude, limit);
                    break;
                case "hotel":
                    results = getHotelRecommendations(userId, latitude, longitude, limit);
                    break;
                default:
                    return Result.fail("未知场景: " + scene);
            }
            
            recordRecommendationHistory(userId, "scene:" + scene, results.size());
            return Result.success(results);
            
        } catch (Exception e) {
            log.error("获取场景推荐失败: {}", e.getMessage(), e);
            return Result.fail("获取推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取个性化LBS流
     */
    public Result<List<LBSFeedItem>> getPersonalizedLBSFeed(
            Long userId, Double latitude, Double longitude, Integer page, Integer size) {
        try {
            if (page == null) page = 1;
            if (size == null) size = 20;
            
            String cacheKey = String.format("lbs:feed:%d:%d:%d", userId, page, size);
            
            // 尝试从缓存获取
            List<LBSFeedItem> cached = recommendationCache.getLBSFeed(cacheKey);
            if (cached != null && !cached.isEmpty()) {
                return Result.success(cached);
            }
            
            // 获取多样化内容
            List<LBSFeedItem> feed = new ArrayList<>();
            
            // 1. 附近热门商家（40%）
            Result<List<RecommendedMerchant>> merchantResult = 
                    getNearbyMerchantRecommendations(userId, latitude, longitude, null, size / 2);
            if (merchantResult.isSuccess()) {
                for (RecommendedMerchant m : merchantResult.getData()) {
                    feed.add(LBSFeedItem.builder()
                            .type("merchant")
                            .merchantId(m.getMerchantId())
                            .title(m.getName())
                            .subtitle(m.getCategoryName())
                            .image(m.getImages() != null && !m.getImages().isEmpty() ? m.getImages().get(0) : null)
                            .distance(m.getDistance())
                            .rating(m.getRating())
                            .tags(m.getTags())
                            .score(m.getRecommendScore())
                            .build());
                }
            }
            
            // 2. 附近的人（30%）
            Result<List<RecommendedPerson>> peopleResult = 
                    getNearbyPeopleRecommendations(userId, latitude, longitude, size / 3);
            if (peopleResult.isSuccess()) {
                for (RecommendedPerson p : peopleResult.getData()) {
                    feed.add(LBSFeedItem.builder()
                            .type("person")
                            .userId(p.getUserId())
                            .title(p.getNickname())
                            .subtitle(p.getBio())
                            .avatar(p.getAvatar())
                            .distance(p.getDistance())
                            .tags(p.getTags())
                            .score(p.getRecommendScore())
                            .build());
                }
            }
            
            // 3. 附近活动（30%）
            List<LBSFeedItem> events = getNearbyEvents(latitude, longitude, size / 3);
            feed.addAll(events);
            
            // 打乱顺序增加多样性
            Collections.shuffle(feed);
            
            // 重新按分数排序
            feed.sort((a, b) -> Double.compare(b.getScore(), a.getScore()));
            
            // 分页
            int fromIndex = (page - 1) * size;
            int toIndex = Math.min(fromIndex + size, feed.size());
            if (fromIndex < feed.size()) {
                feed = feed.subList(fromIndex, toIndex);
            } else {
                feed = new ArrayList<>();
            }
            
            // 缓存结果
            recommendationCache.putLBSFeed(cacheKey, feed, 300); // 5分钟缓存
            
            return Result.success(feed);
            
        } catch (Exception e) {
            log.error("获取LBS流失败: {}", e.getMessage(), e);
            return Result.fail("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取实时热门地点
     */
    public Result<List<HotSpot>> getRealTimeHotSpots(Double latitude, Double longitude, Integer limit) {
        try {
            if (limit == null) limit = 10;
            
            // 从缓存获取实时热点
            List<HotSpot> hotSpots = recommendationCache.getHotSpots(latitude, longitude, 10.0);
            
            if (hotSpots == null || hotSpots.isEmpty()) {
                // 计算实时热度
                hotSpots = calculateHotSpots(latitude, longitude);
                recommendationCache.putHotSpots(latitude, longitude, hotSpots, 600); // 10分钟
            }
            
            // 计算距离并排序
            for (HotSpot spot : hotSpots) {
                double distance = haversineDistance(
                        latitude, longitude, spot.getLatitude(), spot.getLongitude());
                spot.setDistance(distance);
            }
            
            hotSpots.sort(Comparator.comparing(HotSpot::getHeatScore).reversed());
            
            if (hotSpots.size() > limit) {
                hotSpots = hotSpots.subList(0, limit);
            }
            
            return Result.success(hotSpots);
            
        } catch (Exception e) {
            log.error("获取热门地点失败: {}", e.getMessage(), e);
            return Result.fail("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 记录用户反馈
     */
    public Result<Void> recordFeedback(Long userId, String itemType, String itemId, 
                                        String action, Double rating) {
        try {
            RecommendationFeedback feedback = RecommendationFeedback.builder()
                    .userId(userId)
                    .itemType(itemType)
                    .itemId(itemId)
                    .action(action) // click, like, share, visit
                    .rating(rating)
                    .timestamp(LocalDateTime.now())
                    .build();
            
            recommendationCache.saveFeedback(feedback);
            
            // 异步更新用户画像
            CompletableFuture.runAsync(() -> updateUserProfile(userId, itemType, itemId, action));
            
            return Result.success();
            
        } catch (Exception e) {
            log.error("记录反馈失败: {}", e.getMessage(), e);
            return Result.fail("记录失败: " + e.getMessage());
        }
    }
    
    // ==================== 场景化推荐实现 ====================
    
    private List<RecommendedItem> getLunchRecommendations(Long userId, Double lat, Double lon, Integer limit) {
        // 午餐场景：营业中、距离近、评分高、上菜快
        List<POI> pois = poiRepository.findNearbyPOIs(lat, lon, 3.0, "restaurant", limit * 2);
        return pois.stream()
                .filter(p -> isBusinessOpen(p.getBusinessHours()))
                .map(p -> convertToRecommendedItem(p, lat, lon, "午餐推荐"))
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    private List<RecommendedItem> getDinnerRecommendations(Long userId, Double lat, Double lon, Integer limit) {
        // 晚餐场景：环境好、评分高、适合聚餐
        List<POI> pois = poiRepository.findNearbyPOIs(lat, lon, 5.0, "restaurant", limit * 2);
        return pois.stream()
                .filter(p -> p.getRating() >= 4.0)
                .map(p -> convertToRecommendedItem(p, lat, lon, "晚餐精选"))
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    private List<RecommendedItem> getCoffeeRecommendations(Long userId, Double lat, Double lon, Integer limit) {
        List<POI> pois = poiRepository.findNearbyPOIs(lat, lon, 2.0, "cafe", limit * 2);
        return pois.stream()
                .filter(p -> isBusinessOpen(p.getBusinessHours()))
                .map(p -> convertToRecommendedItem(p, lat, lon, "咖啡时光"))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    private List<RecommendedItem> getShoppingRecommendations(Long userId, Double lat, Double lon, Integer limit) {
        List<POI> pois = poiRepository.findNearbyPOIsByCategories(
                lat, lon, 8.0, Arrays.asList("shopping", "mall", "supermarket"), limit * 2);
        return pois.stream()
                .map(p -> convertToRecommendedItem(p, lat, lon, "购物好去处"))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    private List<RecommendedItem> getEntertainmentRecommendations(Long userId, Double lat, Double lon, Integer limit) {
        List<POI> pois = poiRepository.findNearbyPOIsByCategories(
                lat, lon, 10.0, Arrays.asList("entertainment", "cinema", "ktv"), limit * 2);
        return pois.stream()
                .filter(p -> isBusinessOpen(p.getBusinessHours()))
                .map(p -> convertToRecommendedItem(p, lat, lon, "娱乐推荐"))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    private List<RecommendedItem> getHotelRecommendations(Long userId, Double lat, Double lon, Integer limit) {
        List<POI> pois = poiRepository.findNearbyPOIs(lat, lon, 10.0, "hotel", limit * 2);
        return pois.stream()
                .filter(p -> p.getRating() >= 3.5)
                .map(p -> convertToRecommendedItem(p, lat, lon, "酒店住宿"))
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .limit(limit)
                .collect(Collectors.toList());
    }
    
    private List<LBSFeedItem> getNearbyEvents(Double lat, Double lon, Integer limit) {
        // 模拟附近活动数据
        List<LBSFeedItem> events = new ArrayList<>();
        // 实际应从活动服务获取
        return events;
    }
    
    // ==================== 私有方法 ====================
    
    private double calculatePersonRecommendationScore(UserProfile user, UserProfile target, 
                                                       double distance, LocalDateTime lastActive) {
        double score = 0.0;
        
        // 距离分数（越近越高）
        double distanceScore = Math.max(0, 1 - distance / NEARBY_PEOPLE_RADIUS) * DISTANCE_WEIGHT;
        
        // 相似度分数
        double similarityScore = calculateUserSimilarity(user, target) * SIMILARITY_WEIGHT;
        
        // 活跃度分数
        long minutesAgo = java.time.Duration.between(lastActive, LocalDateTime.now()).toMinutes();
        double activityScore = Math.max(0, 1 - minutesAgo / 60.0) * ACTIVITY_WEIGHT;
        
        // 新鲜度分数（首次推荐加分）
        double freshnessScore = FRESHNESS_WEIGHT;
        
        score = distanceScore + similarityScore + activityScore + freshnessScore;
        
        return Math.min(1.0, score);
    }
    
    private double calculateMerchantRecommendationScore(POI poi, double distance, 
                                                         UserPreference preference, TimeContext context) {
        double score = 0.0;
        
        // 距离分数
        double distanceScore = Math.max(0, 1 - distance / NEARBY_POI_RADIUS) * DISTANCE_WEIGHT;
        
        // 评分分数
        double ratingScore = (poi.getRating() / 5.0) * POPULARITY_WEIGHT;
        
        // 热度分数
        double popularityScore = Math.min(1, poi.getRatingCount() / 100.0) * 0.1;
        
        // 匹配度分数
        double matchScore = 0.0;
        if (preference != null && preference.getPreferredCategories() != null) {
            if (preference.getPreferredCategories().contains(poi.getCategory())) {
                matchScore = SIMILARITY_WEIGHT;
            }
        }
        
        // 营业状态分数
        double openScore = isBusinessOpen(poi.getBusinessHours()) ? FRESHNESS_WEIGHT : 0;
        
        score = distanceScore + ratingScore + popularityScore + matchScore + openScore;
        
        return Math.min(1.0, score);
    }
    
    private double calculateUserSimilarity(UserProfile u1, UserProfile u2) {
        if (u1 == null || u2 == null) return 0.0;
        
        double score = 0.0;
        int factors = 0;
        
        // 年龄相似度
        if (u1.getAge() != null && u2.getAge() != null) {
            double ageDiff = Math.abs(u1.getAge() - u2.getAge());
            score += Math.max(0, 1 - ageDiff / 20.0);
            factors++;
        }
        
        // 性别偏好
        if (u1.getGender() != null && u2.getGender() != null) {
            if (u1.getGender().equals(u2.getGender())) {
                score += 0.5;
            }
            factors++;
        }
        
        // 标签相似度
        if (u1.getTags() != null && u2.getTags() != null) {
            Set<String> common = new HashSet<>(u1.getTags());
            common.retainAll(u2.getTags());
            if (!u1.getTags().isEmpty()) {
                score += (double) common.size() / u1.getTags().size();
                factors++;
            }
        }
        
        return factors > 0 ? score / factors : 0.0;
    }
    
    private boolean isUserActive(LocalDateTime lastActive) {
        if (lastActive == null) return false;
        long minutesAgo = java.time.Duration.between(lastActive, LocalDateTime.now()).toMinutes();
        return minutesAgo <= ACTIVE_THRESHOLD_MINUTES;
    }
    
    private boolean isBusinessOpen(String businessHours) {
        if (businessHours == null || businessHours.isEmpty()) return true;
        try {
            String[] parts = businessHours.split("-");
            if (parts.length != 2) return true;
            java.time.LocalTime now = java.time.LocalTime.now();
            java.time.LocalTime open = java.time.LocalTime.parse(parts[0].trim());
            java.time.LocalTime close = java.time.LocalTime.parse(parts[1].trim());
            return !now.isBefore(open) && !now.isAfter(close);
        } catch (Exception e) {
            return true;
        }
    }
    
    private String generateRecommendReason(POI poi, TimeContext context) {
        List<String> reasons = new ArrayList<>();
        
        if (poi.getRating() >= 4.5) reasons.add("评分超高");
        if (poi.getRatingCount() > 100) reasons.add(poi.getRatingCount() + "条评价");
        if (isBusinessOpen(poi.getBusinessHours())) reasons.add("营业中");
        
        if (context.isMealTime()) {
            reasons.add("适合" + context.getMealType());
        }
        
        return String.join(" · ", reasons);
    }
    
    private List<HotSpot> calculateHotSpots(Double lat, Double lon) {
        // 基于用户密度和POI密度计算热点
        List<HotSpot> spots = new ArrayList<>();
        // 实现省略...
        return spots;
    }
    
    private RecommendedItem convertToRecommendedItem(POI poi, Double lat, Double lon, String reason) {
        double distance = haversineDistance(lat, lon, poi.getLatitude(), poi.getLongitude());
        double score = (poi.getRating() / 5.0) * 0.4 + Math.max(0, 1 - distance / 5.0) * 0.6;
        
        return RecommendedItem.builder()
                .id(poi.getId())
                .type("merchant")
                .name(poi.getName())
                .category(poi.getCategory())
                .image(poi.getImages() != null && !poi.getImages().isEmpty() ? poi.getImages().get(0) : null)
                .distance(distance)
                .rating(poi.getRating())
                .recommendReason(reason)
                .score(score)
                .build();
    }
    
    private UserProfile getUserProfile(Long userId) {
        UserProfile profile = recommendationCache.getUserProfile(userId);
        if (profile == null) {
            // 从数据库加载
            profile = loadUserProfileFromDB(userId);
            if (profile != null) {
                recommendationCache.putUserProfile(userId, profile);
            }
        }
        return profile;
    }
    
    private UserProfile loadUserProfileFromDB(Long userId) {
        // 实现省略
        return new UserProfile();
    }
    
    private UserPreference getUserPreference(Long userId) {
        return recommendationCache.getUserPreference(userId);
    }
    
    private TimeContext getCurrentTimeContext() {
        java.time.LocalTime now = java.time.LocalTime.now();
        int hour = now.getHour();
        
        TimeContext context = new TimeContext();
        context.setMealTime(hour >= 11 && hour <= 13 || hour >= 17 && hour <= 20);
        context.setMealType(hour >= 11 && hour <= 13 ? "午餐" : "晚餐");
        context.setWeekend(java.time.LocalDate.now().getDayOfWeek().getValue() >= 6);
        
        return context;
    }
    
    private void recordRecommendationHistory(Long userId, String type, int count) {
        recommendationCache.recordHistory(userId, type, count);
    }
    
    private void updateUserProfile(Long userId, String itemType, String itemId, String action) {
        // 实现省略
    }
    
    private String getCategoryName(String category) {
        Map<String, String> map = new HashMap<>();
        map.put("restaurant", "餐厅");
        map.put("cafe", "咖啡厅");
        map.put("shopping", "购物");
        map.put("entertainment", "娱乐");
        map.put("hotel", "酒店");
        return map.getOrDefault(category, "其他");
    }
    
    private double haversineDistance(Double lat1, Double lon1, Double lat2, Double lon2) {
        final int R = 6371;
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
 * 推荐的人
 */
class RecommendedPerson {
    private Long userId;
    private String nickname;
    private String avatar;
    private Integer gender;
    private Integer age;
    private String bio;
    private List<String> tags;
    private Double latitude;
    private Double longitude;
    private Double distance;
    private LocalDateTime lastActive;
    private Double similarityScore;
    private Double recommendScore;
    
    public static RecommendedPersonBuilder builder() {
        return new RecommendedPersonBuilder();
    }
    
    public static class RecommendedPersonBuilder {
        private RecommendedPerson p = new RecommendedPerson();
        public RecommendedPersonBuilder userId(Long v) { p.userId = v; return this; }
        public RecommendedPersonBuilder nickname(String v) { p.nickname = v; return this; }
        public RecommendedPersonBuilder avatar(String v) { p.avatar = v; return this; }
        public RecommendedPersonBuilder gender(Integer v) { p.gender = v; return this; }
        public RecommendedPersonBuilder age(Integer v) { p.age = v; return this; }
        public RecommendedPersonBuilder bio(String v) { p.bio = v; return this; }
        public RecommendedPersonBuilder tags(List<String> v) { p.tags = v; return this; }
        public RecommendedPersonBuilder latitude(Double v) { p.latitude = v; return this; }
        public RecommendedPersonBuilder longitude(Double v) { p.longitude = v; return this; }
        public RecommendedPersonBuilder distance(Double v) { p.distance = v; return this; }
        public RecommendedPersonBuilder lastActive(LocalDateTime v) { p.lastActive = v; return this; }
        public RecommendedPersonBuilder similarityScore(Double v) { p.similarityScore = v; return this; }
        public RecommendedPersonBuilder recommendScore(Double v) { p.recommendScore = v; return this; }
        public RecommendedPerson build() { return p; }
    }
    
    public Long getUserId() { return userId; }
    public String getNickname() { return nickname; }
    public String getAvatar() { return avatar; }
    public Integer getGender() { return gender; }
    public Integer getAge() { return age; }
    public String getBio() { return bio; }
    public List<String> getTags() { return tags; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public Double getDistance() { return distance; }
    public LocalDateTime getLastActive() { return lastActive; }
    public Double getSimilarityScore() { return similarityScore; }
    public Double getRecommendScore() { return recommendScore; }
}

/**
 * 推荐的商家
 */
class RecommendedMerchant {
    private String merchantId;
    private String name;
    private String category;
    private String categoryName;
    private Double latitude;
    private Double longitude;
    private String address;
    private String phone;
    private Double rating;
    private Integer ratingCount;
    private Integer priceRange;
    private String businessHours;
    private List<String> tags;
    private List<String> images;
    private Double distance;
    private Boolean isOpen;
    private String recommendReason;
    private Double recommendScore;
    
    public static RecommendedMerchantBuilder builder() {
        return new RecommendedMerchantBuilder();
    }
    
    public static class RecommendedMerchantBuilder {
        private RecommendedMerchant m = new RecommendedMerchant();
        public RecommendedMerchantBuilder merchantId(String v) { m.merchantId = v; return this; }
        public RecommendedMerchantBuilder name(String v) { m.name = v; return this; }
        public RecommendedMerchantBuilder category(String v) { m.category = v; return this; }
        public RecommendedMerchantBuilder categoryName(String v) { m.categoryName = v; return this; }
        public RecommendedMerchantBuilder latitude(Double v) { m.latitude = v; return this; }
        public RecommendedMerchantBuilder longitude(Double v) { m.longitude = v; return this; }
        public RecommendedMerchantBuilder address(String v) { m.address = v; return this; }
        public RecommendedMerchantBuilder phone(String v) { m.phone = v; return this; }
        public RecommendedMerchantBuilder rating(Double v) { m.rating = v; return this; }
        public RecommendedMerchantBuilder ratingCount(Integer v) { m.ratingCount = v; return this; }
        public RecommendedMerchantBuilder priceRange(Integer v) { m.priceRange = v; return this; }
        public RecommendedMerchantBuilder businessHours(String v) { m.businessHours = v; return this; }
        public RecommendedMerchantBuilder tags(List<String> v) { m.tags = v; return this; }
        public RecommendedMerchantBuilder images(List<String> v) { m.images = v; return this; }
        public RecommendedMerchantBuilder distance(Double v) { m.distance = v; return this; }
        public RecommendedMerchantBuilder isOpen(Boolean v) { m.isOpen = v; return this; }
        public RecommendedMerchantBuilder recommendReason(String v) { m.recommendReason = v; return this; }
        public RecommendedMerchantBuilder recommendScore(Double v) { m.recommendScore = v; return this; }
        public RecommendedMerchant build() { return m; }
    }
    
    public String getMerchantId() { return merchantId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getCategoryName() { return categoryName; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getAddress() { return address; }
    public String getPhone() { return phone; }
    public Double getRating() { return rating; }
    public Integer getRatingCount() { return ratingCount; }
    public Integer getPriceRange() { return priceRange; }
    public String getBusinessHours() { return businessHours; }
    public List<String> getTags() { return tags; }
    public List<String> getImages() { return images; }
    public Double getDistance() { return distance; }
    public Boolean getIsOpen() { return isOpen; }
    public String getRecommendReason() { return recommendReason; }
    public Double getRecommendScore() { return recommendScore; }
}

/**
 * 推荐项
 */
class RecommendedItem {
    private String id;
    private String type;
    private String name;
    private String category;
    private String image;
    private Double distance;
    private Double rating;
    private String recommendReason;
    private Double score;
    
    public static RecommendedItemBuilder builder() {
        return new RecommendedItemBuilder();
    }
    
    public static class RecommendedItemBuilder {
        private RecommendedItem i = new RecommendedItem();
        public RecommendedItemBuilder id(String v) { i.id = v; return this; }
        public RecommendedItemBuilder type(String v) { i.type = v; return this; }
        public RecommendedItemBuilder name(String v) { i.name = v; return this; }
        public RecommendedItemBuilder category(String v) { i.category = v; return this; }
        public RecommendedItemBuilder image(String v) { i.image = v; return this; }
        public RecommendedItemBuilder distance(Double v) { i.distance = v; return this; }
        public RecommendedItemBuilder rating(Double v) { i.rating = v; return this; }
        public RecommendedItemBuilder recommendReason(String v) { i.recommendReason = v; return this; }
        public RecommendedItemBuilder score(Double v) { i.score = v; return this; }
        public RecommendedItem build() { return i; }
    }
    
    public String getId() { return id; }
    public String getType() { return type; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getImage() { return image; }
    public Double getDistance() { return distance; }
    public Double getRating() { return rating; }
    public String getRecommendReason() { return recommendReason; }
    public Double getScore() { return score; }
}

/**
 * LBS流项目
 */
class LBSFeedItem {
    private String type; // merchant, person, event
    private String merchantId;
    private Long userId;
    private String title;
    private String subtitle;
    private String image;
    private String avatar;
    private Double distance;
    private Double rating;
    private List<String> tags;
    private Double score;
    
    public static LBSFeedItemBuilder builder() {
        return new LBSFeedItemBuilder();
    }
    
    public static class LBSFeedItemBuilder {
        private LBSFeedItem f = new LBSFeedItem();
        public LBSFeedItemBuilder type(String v) { f.type = v; return this; }
        public LBSFeedItemBuilder merchantId(String v) { f.merchantId = v; return this; }
        public LBSFeedItemBuilder userId(Long v) { f.userId = v; return this; }
        public LBSFeedItemBuilder title(String v) { f.title = v; return this; }
        public LBSFeedItemBuilder subtitle(String v) { f.subtitle = v; return this; }
        public LBSFeedItemBuilder image(String v) { f.image = v; return this; }
        public LBSFeedItemBuilder avatar(String v) { f.avatar = v; return this; }
        public LBSFeedItemBuilder distance(Double v) { f.distance = v; return this; }
        public LBSFeedItemBuilder rating(Double v) { f.rating = v; return this; }
        public LBSFeedItemBuilder tags(List<String> v) { f.tags = v; return this; }
        public LBSFeedItemBuilder score(Double v) { f.score = v; return this; }
        public LBSFeedItem build() { return f; }
    }
    
    public String getType() { return type; }
    public String getMerchantId() { return merchantId; }
    public Long getUserId() { return userId; }
    public String getTitle() { return title; }
    public String getSubtitle() { return subtitle; }
    public String getImage() { return image; }
    public String getAvatar() { return avatar; }
    public Double getDistance() { return distance; }
    public Double getRating() { return rating; }
    public List<String> getTags() { return tags; }
    public Double getScore() { return score; }
}

/**
 * 热点
 */
class HotSpot {
    private String id;
    private String name;
    private String type;
    private Double latitude;
    private Double longitude;
    private Double heatScore;
    private Integer userCount;
    private Double distance;
    
    public void setDistance(Double distance) { this.distance = distance; }
    public Double getDistance() { return distance; }
    public Double getHeatScore() { return heatScore; }
}

/**
 * 推荐反馈
 */
class RecommendationFeedback {
    private Long userId;
    private String itemType;
    private String itemId;
    private String action;
    private Double rating;
    private LocalDateTime timestamp;
    
    public static RecommendationFeedbackBuilder builder() {
        return new RecommendationFeedbackBuilder();
    }
    
    public static class RecommendationFeedbackBuilder {
        private RecommendationFeedback f = new RecommendationFeedback();
        public RecommendationFeedbackBuilder userId(Long v) { f.userId = v; return this; }
        public RecommendationFeedbackBuilder itemType(String v) { f.itemType = v; return this; }
        public RecommendationFeedbackBuilder itemId(String v) { f.itemId = v; return this; }
        public RecommendationFeedbackBuilder action(String v) { f.action = v; return this; }
        public RecommendationFeedbackBuilder rating(Double v) { f.rating = v; return this; }
        public RecommendationFeedbackBuilder timestamp(LocalDateTime v) { f.timestamp = v; return this; }
        public RecommendationFeedback build() { return f; }
    }
    
    public Long getUserId() { return userId; }
    public String getItemType() { return itemType; }
    public String getItemId() { return itemId; }
    public String getAction() { return action; }
    public Double getRating() { return rating; }
    public LocalDateTime getTimestamp() { return timestamp; }
}

/**
 * 时间上下文
 */
class TimeContext {
    private boolean isMealTime;
    private String mealType;
    private boolean isWeekend;
    
    public boolean isMealTime() { return isMealTime; }
    public void setMealTime(boolean v) { this.isMealTime = v; }
    public String getMealType() { return mealType; }
    public void setMealType(String v) { this.mealType = v; }
    public boolean isWeekend() { return isWeekend; }
    public void setWeekend(boolean v) { this.isWeekend = v; }
}
