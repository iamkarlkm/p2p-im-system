package com.im.backend.modules.local.service.impl;

import com.im.backend.modules.local.dto.PersonalizedRecommendationRequest;
import com.im.backend.modules.local.dto.PersonalizedRecommendationResponse;
import com.im.backend.modules.local.service.PersonalizedRecommendationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 个性化推荐服务实现类
 * 实现多路召回推荐引擎和智能排序
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Slf4j
@Service
public class PersonalizedRecommendationServiceImpl implements PersonalizedRecommendationService {

    // 权重配置
    private static final double WEIGHT_PERSONALIZATION = 0.3;
    private static final double WEIGHT_DISTANCE = 0.25;
    private static final double WEIGHT_POPULARITY = 0.25;
    private static final double WEIGHT_QUALITY = 0.2;

    @Override
    public PersonalizedRecommendationResponse getRecommendations(PersonalizedRecommendationRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Getting recommendations for user: {} at [{}, {}]", 
                request.getUserId(), request.getLatitude(), request.getLongitude());
        
        try {
            // 1. 多路召回
            List<PersonalizedRecommendationResponse.RecommendationItem> allCandidates = new ArrayList<>();
            
            // Geo召回
            if (request.getRecallStrategies() == null || request.getRecallStrategies().contains("geo")) {
                List<PersonalizedRecommendationResponse.RecommendationItem> geoItems = geoRecall(
                        request.getUserId(), request.getLatitude(), request.getLongitude(), 
                        5000, 50);
                geoItems.forEach(item -> item.setRecallSource("geo"));
                allCandidates.addAll(geoItems);
            }
            
            // 热门召回
            if (request.getRecallStrategies() == null || request.getRecallStrategies().contains("hot")) {
                List<PersonalizedRecommendationResponse.RecommendationItem> hotItems = hotRecall(
                        request.getUserId(), request.getLatitude(), request.getLongitude(), 30);
                hotItems.forEach(item -> item.setRecallSource("hot"));
                allCandidates.addAll(hotItems);
            }
            
            // CF召回
            if (request.getRecallStrategies() == null || request.getRecallStrategies().contains("cf")) {
                List<PersonalizedRecommendationResponse.RecommendationItem> cfItems = collaborativeFilteringRecall(
                        request.getUserId(), 40);
                cfItems.forEach(item -> item.setRecallSource("cf"));
                allCandidates.addAll(cfItems);
            }
            
            // 向量召回
            if (request.getRecallStrategies() == null || request.getRecallStrategies().contains("vector")) {
                List<PersonalizedRecommendationResponse.RecommendationItem> vectorItems = vectorRecall(
                        request.getUserId(), 30);
                vectorItems.forEach(item -> item.setRecallSource("vector"));
                allCandidates.addAll(vectorItems);
            }
            
            int beforeDeduplication = allCandidates.size();
            
            // 2. 去重
            if (request.getDeduplicate() != null && request.getDeduplicate()) {
                allCandidates = deduplicate(allCandidates, request.getExposedItems());
            }
            
            int afterDeduplication = allCandidates.size();
            
            // 3. 智能排序
            List<PersonalizedRecommendationResponse.RecommendationItem> rankedItems = smartRank(
                    allCandidates, request.getUserId(), request.getSortStrategy());
            
            // 4. 分页
            int page = request.getPage() != null ? request.getPage() : 1;
            int pageSize = request.getPageSize() != null ? request.getPageSize() : 20;
            int fromIndex = (page - 1) * pageSize;
            int toIndex = Math.min(fromIndex + pageSize, rankedItems.size());
            
            List<PersonalizedRecommendationResponse.RecommendationItem> pagedItems = 
                    fromIndex < rankedItems.size() ? rankedItems.subList(fromIndex, toIndex) : new ArrayList<>();
            
            // 5. 构建响应
            PersonalizedRecommendationResponse.RecallStatistics stats = 
                    PersonalizedRecommendationResponse.RecallStatistics.builder()
                            .geoRecallCount((int) allCandidates.stream().filter(i -> "geo".equals(i.getRecallSource())).count())
                            .hotRecallCount((int) allCandidates.stream().filter(i -> "hot".equals(i.getRecallSource())).count())
                            .cfRecallCount((int) allCandidates.stream().filter(i -> "cf".equals(i.getRecallSource())).count())
                            .vectorRecallCount((int) allCandidates.stream().filter(i -> "vector".equals(i.getRecallSource())).count())
                            .totalBeforeDeduplication(beforeDeduplication)
                            .totalAfterDeduplication(afterDeduplication)
                            .build();
            
            PersonalizedRecommendationResponse.RecommendationExplanation explanation = 
                    PersonalizedRecommendationResponse.RecommendationExplanation.builder()
                            .mainReason("基于您的位置和偏好为您推荐")
                            .diversityNote("包含多种类型推荐")
                            .freshnessNote("基于实时数据")
                            .build();
            
            long processTime = System.currentTimeMillis() - startTime;
            
            return PersonalizedRecommendationResponse.builder()
                    .recommendationId("rec_" + System.currentTimeMillis())
                    .items(pagedItems)
                    .recallStats(stats)
                    .explanation(explanation)
                    .hasMore(rankedItems.size() > toIndex)
                    .recommendTimeMs(processTime)
                    .timestamp(System.currentTimeMillis())
                    .build();
            
        } catch (Exception e) {
            log.error("Error getting recommendations: {}", e.getMessage(), e);
            return buildErrorResponse();
        }
    }

    @Override
    public CompletableFuture<PersonalizedRecommendationResponse> getRecommendationsAsync(
            PersonalizedRecommendationRequest request) {
        return CompletableFuture.supplyAsync(() -> getRecommendations(request));
    }

    @Override
    public List<PersonalizedRecommendationResponse.RecommendationItem> geoRecall(
            String userId, Double latitude, Double longitude, Integer radius, Integer limit) {
        
        List<PersonalizedRecommendationResponse.RecommendationItem> items = new ArrayList<>();
        
        // 模拟Geo召回数据
        for (int i = 0; i < limit; i++) {
            int distance = 100 + i * 100;
            items.add(PersonalizedRecommendationResponse.RecommendationItem.builder()
                    .itemId("poi_geo_" + i)
                    .contentType("poi")
                    .recallSource("geo")
                    .score(1.0 - (double) distance / 5000)
                    .recommendReason("距离您" + distance + "米")
                    .poiData(PersonalizedRecommendationResponse.POIData.builder()
                            .poiId("poi_geo_" + i)
                            .name("附近热门商户" + i)
                            .address("地址" + i)
                            .latitude(latitude + Math.random() * 0.01)
                            .longitude(longitude + Math.random() * 0.01)
                            .distance(distance)
                            .rating(4.0 + Math.random())
                            .avgPrice(50 + (int)(Math.random() * 200))
                            .category("美食")
                            .isOpen(true)
                            .build())
                    .featureScores(PersonalizedRecommendationResponse.FeatureScores.builder()
                            .distanceScore(1.0 - (double) distance / 5000)
                            .build())
                    .build());
        }
        
        return items;
    }

    @Override
    public List<PersonalizedRecommendationResponse.RecommendationItem> hotRecall(
            String userId, Double latitude, Double longitude, Integer limit) {
        
        List<PersonalizedRecommendationResponse.RecommendationItem> items = new ArrayList<>();
        
        // 模拟热门召回数据
        for (int i = 0; i < limit; i++) {
            double hotScore = 0.9 - i * 0.02;
            items.add(PersonalizedRecommendationResponse.RecommendationItem.builder()
                    .itemId("poi_hot_" + i)
                    .contentType("poi")
                    .recallSource("hot")
                    .score(hotScore)
                    .recommendReason("本区域热门第" + (i + 1) + "名")
                    .poiData(PersonalizedRecommendationResponse.POIData.builder()
                            .poiId("poi_hot_" + i)
                            .name("热门商户" + i)
                            .address("热门地址" + i)
                            .latitude(latitude + Math.random() * 0.02)
                            .longitude(longitude + Math.random() * 0.02)
                            .distance(500 + (int)(Math.random() * 2000))
                            .rating(4.5 + Math.random() * 0.5)
                            .avgPrice(80 + (int)(Math.random() * 300))
                            .category("热门")
                            .isOpen(true)
                            .build())
                    .featureScores(PersonalizedRecommendationResponse.FeatureScores.builder()
                            .popularityScore(hotScore)
                            .build())
                    .build());
        }
        
        return items;
    }

    @Override
    public List<PersonalizedRecommendationResponse.RecommendationItem> collaborativeFilteringRecall(
            String userId, Integer limit) {
        
        List<PersonalizedRecommendationResponse.RecommendationItem> items = new ArrayList<>();
        
        // 模拟CF召回数据
        for (int i = 0; i < limit; i++) {
            double cfScore = 0.85 - i * 0.015;
            items.add(PersonalizedRecommendationResponse.RecommendationItem.builder()
                    .itemId("poi_cf_" + i)
                    .contentType("poi")
                    .recallSource("cf")
                    .score(cfScore)
                    .recommendReason("相似用户也喜欢")
                    .poiData(PersonalizedRecommendationResponse.POIData.builder()
                            .poiId("poi_cf_" + i)
                            .name("推荐商户" + i)
                            .address("推荐地址" + i)
                            .latitude(31.23 + Math.random() * 0.05)
                            .longitude(121.47 + Math.random() * 0.05)
                            .distance(800 + (int)(Math.random() * 3000))
                            .rating(4.2 + Math.random() * 0.6)
                            .avgPrice(60 + (int)(Math.random() * 250))
                            .category("推荐")
                            .isOpen(true)
                            .build())
                    .featureScores(PersonalizedRecommendationResponse.FeatureScores.builder()
                            .personalizationScore(cfScore)
                            .build())
                    .build());
        }
        
        return items;
    }

    @Override
    public List<PersonalizedRecommendationResponse.RecommendationItem> vectorRecall(
            String userId, Integer limit) {
        
        List<PersonalizedRecommendationResponse.RecommendationItem> items = new ArrayList<>();
        
        // 模拟向量召回数据
        for (int i = 0; i < limit; i++) {
            double vectorScore = 0.88 - i * 0.02;
            items.add(PersonalizedRecommendationResponse.RecommendationItem.builder()
                    .itemId("poi_vec_" + i)
                    .contentType("poi")
                    .recallSource("vector")
                    .score(vectorScore)
                    .recommendReason("符合您的口味偏好")
                    .poiData(PersonalizedRecommendationResponse.POIData.builder()
                            .poiId("poi_vec_" + i)
                            .name("向量推荐商户" + i)
                            .address("向量地址" + i)
                            .latitude(31.22 + Math.random() * 0.04)
                            .longitude(121.46 + Math.random() * 0.04)
                            .distance(1000 + (int)(Math.random() * 4000))
                            .rating(4.3 + Math.random() * 0.5)
                            .avgPrice(100 + (int)(Math.random() * 400))
                            .category("向量推荐")
                            .isOpen(true)
                            .build())
                    .featureScores(PersonalizedRecommendationResponse.FeatureScores.builder()
                            .personalizationScore(vectorScore)
                            .build())
                    .build());
        }
        
        return items;
    }

    @Override
    public List<PersonalizedRecommendationResponse.RecommendationItem> smartRank(
            List<PersonalizedRecommendationResponse.RecommendationItem> items, String userId, String strategy) {
        
        // 计算综合分数
        for (PersonalizedRecommendationResponse.RecommendationItem item : items) {
            PersonalizedRecommendationResponse.FeatureScores scores = item.getFeatureScores();
            if (scores == null) {
                scores = PersonalizedRecommendationResponse.FeatureScores.builder().build();
            }
            
            double personalization = scores.getPersonalizationScore() != null ? scores.getPersonalizationScore() : 0.5;
            double distance = scores.getDistanceScore() != null ? scores.getDistanceScore() : 0.5;
            double popularity = scores.getPopularityScore() != null ? scores.getPopularityScore() : 0.5;
            double quality = scores.getQualityScore() != null ? scores.getQualityScore() : 0.5;
            
            // 加权综合分数
            double finalScore = personalization * WEIGHT_PERSONALIZATION
                    + distance * WEIGHT_DISTANCE
                    + popularity * WEIGHT_POPULARITY
                    + quality * WEIGHT_QUALITY;
            
            item.setScore(finalScore);
            
            // 更新特征分数
            scores.setPersonalizationScore(personalization);
            scores.setDistanceScore(distance);
            scores.setPopularityScore(popularity);
            scores.setQualityScore(quality);
            item.setFeatureScores(scores);
        }
        
        // 排序
        return items.stream()
                .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
                .collect(Collectors.toList());
    }

    @Override
    public List<PersonalizedRecommendationResponse.RecommendationItem> deduplicate(
            List<PersonalizedRecommendationResponse.RecommendationItem> items, List<String> exposedItems) {
        
        Set<String> exposedSet = exposedItems != null ? new HashSet<>(exposedItems) : new HashSet<>();
        Set<String> seen = new HashSet<>();
        List<PersonalizedRecommendationResponse.RecommendationItem> deduplicated = new ArrayList<>();
        
        for (PersonalizedRecommendationResponse.RecommendationItem item : items) {
            String key = item.getItemId();
            if (!seen.contains(key) && !exposedSet.contains(key)) {
                seen.add(key);
                deduplicated.add(item);
            }
        }
        
        return deduplicated;
    }

    @Override
    public void recordFeedback(String userId, String itemId, String action, String context) {
        log.info("Recording feedback: user={}, item={}, action={}, context={}", 
                userId, itemId, action, context);
        // 实现反馈记录逻辑
    }

    @Override
    public PersonalizedRecommendationResponse refreshRecommendations(
            PersonalizedRecommendationRequest request, List<String> excludeIds) {
        
        // 合并已曝光列表
        List<String> exposed = request.getExposedItems() != null ? request.getExposedItems() : new ArrayList<>();
        exposed.addAll(excludeIds);
        request.setExposedItems(exposed);
        
        return getRecommendations(request);
    }
    
    private PersonalizedRecommendationResponse buildErrorResponse() {
        return PersonalizedRecommendationResponse.builder()
                .recommendationId("rec_" + System.currentTimeMillis())
                .items(new ArrayList<>())
                .hasMore(false)
                .recommendTimeMs(0L)
                .timestamp(System.currentTimeMillis())
                .build();
    }
}
