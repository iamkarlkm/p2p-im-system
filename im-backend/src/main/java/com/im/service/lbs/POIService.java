package com.im.service.lbs;

import com.im.model.POI;
import com.im.model.POICategory;
import com.im.repository.POIRepository;
import com.im.cache.POICache;
import com.im.common.Result;
import com.im.common.PageResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * POI兴趣点服务
 * 管理商家、景点、服务等地理位置信息
 * 
 * @author IM Development Team
 * @version 1.0.0
 */
@Slf4j
@Service
public class POIService {

    @Autowired
    private POIRepository poiRepository;
    
    @Autowired
    private POICache poiCache;
    
    // POI搜索默认半径（公里）
    private static final double DEFAULT_SEARCH_RADIUS = 5.0;
    
    // 热门POI缓存数量
    private static final int HOT_POI_CACHE_SIZE = 100;
    
    // POI评分权重
    private static final double RATING_WEIGHT = 0.3;
    private static final double DISTANCE_WEIGHT = 0.4;
    private static final double POPULARITY_WEIGHT = 0.3;
    
    /**
     * 创建POI
     */
    public Result<POI> createPOI(POICreateRequest request) {
        try {
            // 验证坐标
            if (!isValidCoordinate(request.getLatitude(), request.getLongitude())) {
                return Result.fail("无效的地理坐标");
            }
            
            // 检查是否已存在
            List<POI> existing = poiRepository.findByNameAndLocation(
                    request.getName(), request.getLatitude(), request.getLongitude(), 0.1);
            if (!existing.isEmpty()) {
                return Result.fail("该位置附近已存在同名POI");
            }
            
            POI poi = POI.builder()
                    .id(generatePOIId())
                    .name(request.getName())
                    .category(request.getCategory())
                    .subCategory(request.getSubCategory())
                    .latitude(request.getLatitude())
                    .longitude(request.getLongitude())
                    .address(request.getAddress())
                    .phone(request.getPhone())
                    .businessHours(request.getBusinessHours())
                    .rating(0.0)
                    .ratingCount(0)
                    .priceRange(request.getPriceRange())
                    .tags(request.getTags())
                    .images(request.getImages())
                    .description(request.getDescription())
                    .ownerId(request.getOwnerId())
                    .status(1) // 待审核
                    .createTime(LocalDateTime.now())
                    .updateTime(LocalDateTime.now())
                    .build();
            
            poi = poiRepository.save(poi);
            
            // 更新缓存
            poiCache.putPOI(poi);
            poiCache.addToGeoIndex(poi);
            
            log.info("创建POI成功: {} [{}]", poi.getName(), poi.getId());
            return Result.success(poi);
            
        } catch (Exception e) {
            log.error("创建POI失败: {}", e.getMessage(), e);
            return Result.fail("创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量导入POI
     */
    public Result<POIImportResult> batchImportPOI(List<POICreateRequest> requests) {
        try {
            int success = 0;
            int failed = 0;
            List<String> failedNames = new ArrayList<>();
            
            List<POI> pois = new ArrayList<>();
            
            for (POICreateRequest request : requests) {
                try {
                    if (!isValidCoordinate(request.getLatitude(), request.getLongitude())) {
                        failed++;
                        failedNames.add(request.getName() + "(坐标无效)");
                        continue;
                    }
                    
                    POI poi = POI.builder()
                            .id(generatePOIId())
                            .name(request.getName())
                            .category(request.getCategory())
                            .subCategory(request.getSubCategory())
                            .latitude(request.getLatitude())
                            .longitude(request.getLongitude())
                            .address(request.getAddress())
                            .phone(request.getPhone())
                            .businessHours(request.getBusinessHours())
                            .rating(0.0)
                            .ratingCount(0)
                            .priceRange(request.getPriceRange())
                            .tags(request.getTags())
                            .images(request.getImages())
                            .description(request.getDescription())
                            .ownerId(request.getOwnerId())
                            .status(1)
                            .createTime(LocalDateTime.now())
                            .updateTime(LocalDateTime.now())
                            .build();
                    
                    pois.add(poi);
                    success++;
                    
                } catch (Exception ex) {
                    failed++;
                    failedNames.add(request.getName());
                }
            }
            
            // 批量保存
            if (!pois.isEmpty()) {
                poiRepository.saveAll(pois);
                
                // 批量更新缓存
                for (POI poi : pois) {
                    poiCache.putPOI(poi);
                    poiCache.addToGeoIndex(poi);
                }
            }
            
            POIImportResult result = new POIImportResult(success, failed, failedNames);
            log.info("批量导入POI完成: 成功{}，失败{}", success, failed);
            return Result.success(result);
            
        } catch (Exception e) {
            log.error("批量导入POI失败: {}", e.getMessage(), e);
            return Result.fail("导入失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新POI信息
     */
    public Result<POI> updatePOI(String poiId, POIUpdateRequest request) {
        try {
            Optional<POI> optional = poiRepository.findById(poiId);
            if (!optional.isPresent()) {
                return Result.fail("POI不存在");
            }
            
            POI poi = optional.get();
            
            // 更新字段
            if (request.getName() != null) poi.setName(request.getName());
            if (request.getCategory() != null) poi.setCategory(request.getCategory());
            if (request.getSubCategory() != null) poi.setSubCategory(request.getSubCategory());
            if (request.getAddress() != null) poi.setAddress(request.getAddress());
            if (request.getPhone() != null) poi.setPhone(request.getPhone());
            if (request.getBusinessHours() != null) poi.setBusinessHours(request.getBusinessHours());
            if (request.getPriceRange() != null) poi.setPriceRange(request.getPriceRange());
            if (request.getTags() != null) poi.setTags(request.getTags());
            if (request.getImages() != null) poi.setImages(request.getImages());
            if (request.getDescription() != null) poi.setDescription(request.getDescription());
            if (request.getStatus() != null) poi.setStatus(request.getStatus());
            
            poi.setUpdateTime(LocalDateTime.now());
            
            poi = poiRepository.save(poi);
            
            // 更新缓存
            poiCache.putPOI(poi);
            poiCache.addToGeoIndex(poi);
            
            log.info("更新POI成功: {}", poiId);
            return Result.success(poi);
            
        } catch (Exception e) {
            log.error("更新POI失败: {}", e.getMessage(), e);
            return Result.fail("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取POI详情
     */
    public Result<POI> getPOIDetail(String poiId) {
        try {
            // 先查缓存
            POI cached = poiCache.getPOI(poiId);
            if (cached != null) {
                // 异步增加浏览量
                poiCache.incrementViewCount(poiId);
                return Result.success(cached);
            }
            
            Optional<POI> optional = poiRepository.findById(poiId);
            if (!optional.isPresent()) {
                return Result.fail("POI不存在");
            }
            
            POI poi = optional.get();
            poiCache.putPOI(poi);
            poiCache.incrementViewCount(poiId);
            
            return Result.success(poi);
            
        } catch (Exception e) {
            log.error("获取POI详情失败: {}", e.getMessage(), e);
            return Result.fail("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除POI
     */
    public Result<Void> deletePOI(String poiId) {
        try {
            poiRepository.deleteById(poiId);
            poiCache.deletePOI(poiId);
            poiCache.removeFromGeoIndex(poiId);
            
            log.info("删除POI成功: {}", poiId);
            return Result.success();
            
        } catch (Exception e) {
            log.error("删除POI失败: {}", e.getMessage(), e);
            return Result.fail("删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索附近POI
     */
    public Result<List<NearbyPOI>> searchNearbyPOI(Double latitude, Double longitude, 
                                                    String category, String keyword,
                                                    Double radiusKm, Integer limit) {
        try {
            if (radiusKm == null) radiusKm = DEFAULT_SEARCH_RADIUS;
            if (limit == null) limit = 50;
            
            // 从地理索引获取附近POI
            List<String> nearbyIds = poiCache.searchNearbyPOIs(
                    longitude, latitude, radiusKm, category);
            
            // 获取POI详情
            List<NearbyPOI> results = new ArrayList<>();
            for (String poiId : nearbyIds) {
                POI poi = poiCache.getPOI(poiId);
                if (poi == null) continue;
                
                // 关键词过滤
                if (keyword != null && !keyword.isEmpty()) {
                    if (!poi.getName().contains(keyword) && 
                        !poi.getTags().contains(keyword)) {
                        continue;
                    }
                }
                
                // 计算距离
                double distance = haversineDistance(
                        latitude, longitude, poi.getLatitude(), poi.getLongitude());
                
                NearbyPOI nearby = NearbyPOI.builder()
                        .poiId(poi.getId())
                        .name(poi.getName())
                        .category(poi.getCategory())
                        .categoryName(getCategoryName(poi.getCategory()))
                        .latitude(poi.getLatitude())
                        .longitude(poi.getLongitude())
                        .address(poi.getAddress())
                        .rating(poi.getRating())
                        .ratingCount(poi.getRatingCount())
                        .priceRange(poi.getPriceRange())
                        .tags(poi.getTags())
                        .images(poi.getImages())
                        .distance(distance)
                        .isOpen(isBusinessOpen(poi.getBusinessHours()))
                        .build();
                
                results.add(nearby);
            }
            
            // 按距离排序
            results.sort(Comparator.comparing(NearbyPOI::getDistance));
            
            // 限制数量
            if (results.size() > limit) {
                results = results.subList(0, limit);
            }
            
            log.info("搜索附近POI: 坐标({},{})，类别{}，找到{}个结果", 
                    latitude, longitude, category, results.size());
            return Result.success(results);
            
        } catch (Exception e) {
            log.error("搜索附近POI失败: {}", e.getMessage(), e);
            return Result.fail("搜索失败: " + e.getMessage());
        }
    }
    
    /**
     * 智能推荐POI
     */
    public Result<List<NearbyPOI>> recommendPOI(Long userId, Double latitude, Double longitude,
                                                 String scene, Integer limit) {
        try {
            if (limit == null) limit = 20;
            
            // 获取用户偏好
            UserPreference preference = poiCache.getUserPreference(userId);
            
            // 搜索范围内的POI
            List<String> nearbyIds = poiCache.searchNearbyPOIs(
                    longitude, latitude, 10.0, null);
            
            List<ScoredPOI> scoredPOIs = new ArrayList<>();
            
            for (String poiId : nearbyIds) {
                POI poi = poiCache.getPOI(poiId);
                if (poi == null || poi.getStatus() != 2) continue; // 只推荐已审核的
                
                // 计算距离
                double distance = haversineDistance(
                        latitude, longitude, poi.getLatitude(), poi.getLongitude());
                
                // 计算推荐分数
                double score = calculateRecommendationScore(poi, distance, preference, scene);
                
                scoredPOIs.add(new ScoredPOI(poi, distance, score));
            }
            
            // 按分数排序
            scoredPOIs.sort((a, b) -> Double.compare(b.score, a.score));
            
            // 转换为结果
            List<NearbyPOI> results = scoredPOIs.stream()
                    .limit(limit)
                    .map(sp -> NearbyPOI.builder()
                            .poiId(sp.poi.getId())
                            .name(sp.poi.getName())
                            .category(sp.poi.getCategory())
                            .categoryName(getCategoryName(sp.poi.getCategory()))
                            .latitude(sp.poi.getLatitude())
                            .longitude(sp.poi.getLongitude())
                            .address(sp.poi.getAddress())
                            .rating(sp.poi.getRating())
                            .ratingCount(sp.poi.getRatingCount())
                            .priceRange(sp.poi.getPriceRange())
                            .tags(sp.poi.getTags())
                            .images(sp.poi.getImages())
                            .distance(sp.distance)
                            .isOpen(isBusinessOpen(sp.poi.getBusinessHours()))
                            .recommendScore(sp.score)
                            .build())
                    .collect(Collectors.toList());
            
            log.info("为用户[{}]推荐POI，场景{}，返回{}个结果", userId, scene, results.size());
            return Result.success(results);
            
        } catch (Exception e) {
            log.error("推荐POI失败: {}", e.getMessage(), e);
            return Result.fail("推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取POI分类列表
     */
    public Result<List<POICategory>> getCategories() {
        try {
            List<POICategory> categories = poiCache.getAllCategories();
            if (categories == null || categories.isEmpty()) {
                categories = loadDefaultCategories();
                poiCache.saveCategories(categories);
            }
            return Result.success(categories);
        } catch (Exception e) {
            log.error("获取POI分类失败: {}", e.getMessage(), e);
            return Result.fail("获取分类失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取热门POI
     */
    public Result<List<NearbyPOI>> getHotPOI(Double latitude, Double longitude, Integer limit) {
        try {
            if (limit == null) limit = 20;
            
            // 从缓存获取热门POI
            List<String> hotIds = poiCache.getHotPOIIds(limit * 2);
            
            List<NearbyPOI> results = new ArrayList<>();
            for (String poiId : hotIds) {
                POI poi = poiCache.getPOI(poiId);
                if (poi == null) continue;
                
                double distance = haversineDistance(
                        latitude, longitude, poi.getLatitude(), poi.getLongitude());
                
                NearbyPOI nearby = NearbyPOI.builder()
                        .poiId(poi.getId())
                        .name(poi.getName())
                        .category(poi.getCategory())
                        .categoryName(getCategoryName(poi.getCategory()))
                        .latitude(poi.getLatitude())
                        .longitude(poi.getLongitude())
                        .address(poi.getAddress())
                        .rating(poi.getRating())
                        .ratingCount(poi.getRatingCount())
                        .priceRange(poi.getPriceRange())
                        .tags(poi.getTags())
                        .images(poi.getImages())
                        .distance(distance)
                        .isOpen(isBusinessOpen(poi.getBusinessHours()))
                        .build();
                
                results.add(nearby);
            }
            
            // 按距离排序
            results.sort(Comparator.comparing(NearbyPOI::getDistance));
            
            if (results.size() > limit) {
                results = results.subList(0, limit);
            }
            
            return Result.success(results);
            
        } catch (Exception e) {
            log.error("获取热门POI失败: {}", e.getMessage(), e);
            return Result.fail("获取失败: " + e.getMessage());
        }
    }
    
    /**
     * 审核POI
     */
    public Result<Void> auditPOI(String poiId, Integer status, String reason) {
        try {
            Optional<POI> optional = poiRepository.findById(poiId);
            if (!optional.isPresent()) {
                return Result.fail("POI不存在");
            }
            
            POI poi = optional.get();
            poi.setStatus(status); // 2=通过, 3=拒绝
            poi.setUpdateTime(LocalDateTime.now());
            
            poiRepository.save(poi);
            poiCache.putPOI(poi);
            
            log.info("审核POI: {}，状态: {}", poiId, status);
            return Result.success();
            
        } catch (Exception e) {
            log.error("审核POI失败: {}", e.getMessage(), e);
            return Result.fail("审核失败: " + e.getMessage());
        }
    }
    
    // ==================== 私有方法 ====================
    
    private boolean isValidCoordinate(Double latitude, Double longitude) {
        return latitude != null && longitude != null
                && latitude >= -90 && latitude <= 90
                && longitude >= -180 && longitude <= 180;
    }
    
    private String generatePOIId() {
        return "POI" + System.currentTimeMillis() + new Random().nextInt(1000);
    }
    
    private double calculateRecommendationScore(POI poi, double distance, 
                                                UserPreference preference, String scene) {
        double score = 0.0;
        
        // 评分分数 (0-5)
        double ratingScore = poi.getRating() / 5.0 * RATING_WEIGHT;
        
        // 距离分数 (越近越高)
        double distanceScore = Math.max(0, 1 - distance / 10.0) * DISTANCE_WEIGHT;
        
        // 热度分数
        double popularityScore = Math.min(1, poi.getRatingCount() / 100.0) * POPULARITY_WEIGHT;
        
        score = ratingScore + distanceScore + popularityScore;
        
        // 根据场景调整
        if (scene != null) {
            switch (scene) {
                case "dining":
                    if (Arrays.asList("restaurant", "cafe", "bar").contains(poi.getCategory())) {
                        score *= 1.5;
                    }
                    break;
                case "shopping":
                    if (Arrays.asList("shopping", "mall", "supermarket").contains(poi.getCategory())) {
                        score *= 1.5;
                    }
                    break;
                case "entertainment":
                    if (Arrays.asList("entertainment", "cinema", "ktv").contains(poi.getCategory())) {
                        score *= 1.5;
                    }
                    break;
            }
        }
        
        // 用户偏好调整
        if (preference != null && preference.getPreferredCategories() != null) {
            if (preference.getPreferredCategories().contains(poi.getCategory())) {
                score *= 1.3;
            }
        }
        
        // 营业中加分
        if (isBusinessOpen(poi.getBusinessHours())) {
            score *= 1.2;
        }
        
        return score;
    }
    
    private boolean isBusinessOpen(String businessHours) {
        if (businessHours == null || businessHours.isEmpty()) {
            return true; // 默认营业
        }
        
        try {
            // 简化的营业时间判断
            // 格式: "09:00-22:00"
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
    
    private String getCategoryName(String category) {
        Map<String, String> map = new HashMap<>();
        map.put("restaurant", "餐厅");
        map.put("cafe", "咖啡厅");
        map.put("bar", "酒吧");
        map.put("shopping", "购物");
        map.put("mall", "商场");
        map.put("supermarket", "超市");
        map.put("entertainment", "娱乐");
        map.put("cinema", "电影院");
        map.put("ktv", "KTV");
        map.put("hotel", "酒店");
        map.put("scenic", "景点");
        map.put("transport", "交通");
        map.put("hospital", "医院");
        map.put("bank", "银行");
        map.put("gas", "加油站");
        return map.getOrDefault(category, "其他");
    }
    
    private List<POICategory> loadDefaultCategories() {
        List<POICategory> categories = new ArrayList<>();
        
        categories.add(POICategory.builder().id("restaurant").name("餐饮").icon("🍽️").sort(1).build());
        categories.add(POICategory.builder().id("cafe").name("咖啡厅").icon("☕").sort(2).build());
        categories.add(POICategory.builder().id("shopping").name("购物").icon("🛍️").sort(3).build());
        categories.add(POICategory.builder().id("entertainment").name("娱乐").icon("🎮").sort(4).build());
        categories.add(POICategory.builder().id("hotel").name("酒店").icon("🏨").sort(5).build());
        categories.add(POICategory.builder().id("scenic").name("景点").icon("🏞️").sort(6).build());
        categories.add(POICategory.builder().id("transport").name("交通").icon("🚇").sort(7).build());
        categories.add(POICategory.builder().id("hospital").name("医疗").icon("🏥").sort(8).build());
        categories.add(POICategory.builder().id("bank").name("金融").icon("🏦").sort(9).build());
        categories.add(POICategory.builder().id("life").name("生活服务").icon("🔧").sort(10).build());
        
        return categories;
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
    
    // 内部类
    private static class ScoredPOI {
        POI poi;
        double distance;
        double score;
        
        ScoredPOI(POI poi, double distance, double score) {
            this.poi = poi;
            this.distance = distance;
            this.score = score;
        }
    }
}

/**
 * POI创建请求
 */
class POICreateRequest {
    private String name;
    private String category;
    private String subCategory;
    private Double latitude;
    private Double longitude;
    private String address;
    private String phone;
    private String businessHours;
    private Integer priceRange;
    private List<String> tags;
    private List<String> images;
    private String description;
    private Long ownerId;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    public Double getLatitude() { return latitude; }
    public void setLatitude(Double latitude) { this.latitude = latitude; }
    public Double getLongitude() { return longitude; }
    public void setLongitude(Double longitude) { this.longitude = longitude; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getBusinessHours() { return businessHours; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }
    public Integer getPriceRange() { return priceRange; }
    public void setPriceRange(Integer priceRange) { this.priceRange = priceRange; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Long getOwnerId() { return ownerId; }
    public void setOwnerId(Long ownerId) { this.ownerId = ownerId; }
}

/**
 * POI更新请求
 */
class POIUpdateRequest {
    private String name;
    private String category;
    private String subCategory;
    private String address;
    private String phone;
    private String businessHours;
    private Integer priceRange;
    private List<String> tags;
    private List<String> images;
    private String description;
    private Integer status;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getSubCategory() { return subCategory; }
    public void setSubCategory(String subCategory) { this.subCategory = subCategory; }
    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getBusinessHours() { return businessHours; }
    public void setBusinessHours(String businessHours) { this.businessHours = businessHours; }
    public Integer getPriceRange() { return priceRange; }
    public void setPriceRange(Integer priceRange) { this.priceRange = priceRange; }
    public List<String> getTags() { return tags; }
    public void setTags(List<String> tags) { this.tags = tags; }
    public List<String> getImages() { return images; }
    public void setImages(List<String> images) { this.images = images; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public Integer getStatus() { return status; }
    public void setStatus(Integer status) { this.status = status; }
}

/**
 * POI导入结果
 */
class POIImportResult {
    private int successCount;
    private int failedCount;
    private List<String> failedNames;
    
    public POIImportResult(int successCount, int failedCount, List<String> failedNames) {
        this.successCount = successCount;
        this.failedCount = failedCount;
        this.failedNames = failedNames;
    }
    
    public int getSuccessCount() { return successCount; }
    public int getFailedCount() { return failedCount; }
    public List<String> getFailedNames() { return failedNames; }
}

/**
 * 附近POI
 */
class NearbyPOI {
    private String poiId;
    private String name;
    private String category;
    private String categoryName;
    private Double latitude;
    private Double longitude;
    private String address;
    private Double rating;
    private Integer ratingCount;
    private Integer priceRange;
    private List<String> tags;
    private List<String> images;
    private Double distance;
    private Boolean isOpen;
    private Double recommendScore;
    
    public static NearbyPOIBuilder builder() {
        return new NearbyPOIBuilder();
    }
    
    public static class NearbyPOIBuilder {
        private NearbyPOI poi = new NearbyPOI();
        
        public NearbyPOIBuilder poiId(String poiId) { poi.poiId = poiId; return this; }
        public NearbyPOIBuilder name(String name) { poi.name = name; return this; }
        public NearbyPOIBuilder category(String category) { poi.category = category; return this; }
        public NearbyPOIBuilder categoryName(String categoryName) { poi.categoryName = categoryName; return this; }
        public NearbyPOIBuilder latitude(Double latitude) { poi.latitude = latitude; return this; }
        public NearbyPOIBuilder longitude(Double longitude) { poi.longitude = longitude; return this; }
        public NearbyPOIBuilder address(String address) { poi.address = address; return this; }
        public NearbyPOIBuilder rating(Double rating) { poi.rating = rating; return this; }
        public NearbyPOIBuilder ratingCount(Integer ratingCount) { poi.ratingCount = ratingCount; return this; }
        public NearbyPOIBuilder priceRange(Integer priceRange) { poi.priceRange = priceRange; return this; }
        public NearbyPOIBuilder tags(List<String> tags) { poi.tags = tags; return this; }
        public NearbyPOIBuilder images(List<String> images) { poi.images = images; return this; }
        public NearbyPOIBuilder distance(Double distance) { poi.distance = distance; return this; }
        public NearbyPOIBuilder isOpen(Boolean isOpen) { poi.isOpen = isOpen; return this; }
        public NearbyPOIBuilder recommendScore(Double recommendScore) { poi.recommendScore = recommendScore; return this; }
        
        public NearbyPOI build() { return poi; }
    }
    
    // Getters
    public String getPoiId() { return poiId; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getCategoryName() { return categoryName; }
    public Double getLatitude() { return latitude; }
    public Double getLongitude() { return longitude; }
    public String getAddress() { return address; }
    public Double getRating() { return rating; }
    public Integer getRatingCount() { return ratingCount; }
    public Integer getPriceRange() { return priceRange; }
    public List<String> getTags() { return tags; }
    public List<String> getImages() { return images; }
    public Double getDistance() { return distance; }
    public Boolean getIsOpen() { return isOpen; }
    public Double getRecommendScore() { return recommendScore; }
}

/**
 * 用户偏好
 */
class UserPreference {
    private Long userId;
    private List<String> preferredCategories;
    private List<String> preferredTags;
    private Integer preferredPriceRange;
    
    public List<String> getPreferredCategories() { return preferredCategories; }
    public void setPreferredCategories(List<String> preferredCategories) { this.preferredCategories = preferredCategories; }
}
