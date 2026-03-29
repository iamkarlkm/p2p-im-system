package com.im.service.recommendation.impl;

import com.im.dto.recommendation.RecommendationFeedRequestDTO;
import com.im.dto.recommendation.RecommendationFeedResponseDTO;
import com.im.entity.recommendation.*;
import com.im.service.recommendation.RecommendationFeedService;
import com.im.service.recommendation.recall.*;
import com.im.service.recommendation.rank.*;
import com.im.service.recommendation.diversity.*;
import com.im.util.GeoUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * 推荐信息流服务实现类
 * 实现多路召回、智能排序、多样性控制的完整推荐流程
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Slf4j
@Service
public class RecommendationFeedServiceImpl implements RecommendationFeedService {

    @Autowired
    private GeoRecallService geoRecallService;
    
    @Autowired
    private HotRecallService hotRecallService;
    
    @Autowired
    private CollaborativeFilteringRecallService cfRecallService;
    
    @Autowired
    private VectorRecallService vectorRecallService;
    
    @Autowired
    private SocialRecallService socialRecallService;
    
    @Autowired
    private RealtimeBehaviorRecallService realtimeRecallService;
    
    @Autowired
    private DeepRankService deepRankService;
    
    @Autowired
    private RuleRankService ruleRankService;
    
    @Autowired
    private DiversityControlService diversityControlService;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    private final ExecutorService recallExecutor = Executors.newFixedThreadPool(6);
    
    private static final String CACHE_KEY_PREFIX = "recommendation:feed:";
    private static final long CACHE_TTL_SECONDS = 300; // 5分钟缓存
    
    @Override
    public RecommendationFeedResponseDTO getRecommendationFeed(RecommendationFeedRequestDTO request) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("[推荐信息流] 开始生成推荐 feed, userId={}, scene={}", 
                    request.getUserId(), request.getScene());
            
            // 1. 检查缓存
            String cacheKey = buildCacheKey(request);
            RecommendationFeedResponseDTO cached = getFromCache(cacheKey);
            if (cached != null) {
                log.info("[推荐信息流] 命中缓存, userId={}", request.getUserId());
                return cached;
            }
            
            // 2. 执行多路召回
            long recallStart = System.currentTimeMillis();
            Map<String, List<RecallCandidate>> recallResults = executeMultiChannelRecall(
                request.getUserId(),
                request.getScene(),
                request.getLongitude(),
                request.getLatitude(),
                buildParamsFromRequest(request)
            );
            long recallTime = System.currentTimeMillis() - recallStart;
            
            // 3. 合并候选集
            List<RecallCandidate> mergedCandidates = mergeCandidates(recallResults);
            log.info("[推荐信息流] 召回完成, 候选集大小={}, 耗时={}ms", 
                    mergedCandidates.size(), recallTime);
            
            // 4. 执行排序
            long rankStart = System.currentTimeMillis();
            List<RecommendationItem> rankedItems = executeRanking(
                mergedCandidates,
                request.getUserId(),
                request.getScene(),
                request.getPageSize() * 3 // 多取一些用于多样性控制
            );
            long rankTime = System.currentTimeMillis() - rankStart;
            
            // 5. 多样性控制
            List<RecommendationItem> diversifiedItems = diversityControlService.applyDiversity(
                rankedItems, 
                request.getPageSize(),
                buildDiversityConfig(request)
            );
            
            // 6. 构建响应
            RecommendationFeedResponseDTO response = buildResponse(
                diversifiedItems,
                request,
                recallTime,
                rankTime,
                System.currentTimeMillis() - startTime,
                recallResults.keySet()
            );
            
            // 7. 缓存结果
            putToCache(cacheKey, response);
            
            // 8. 异步记录日志
            asyncLogRecommendation(request, response);
            
            log.info("[推荐信息流] 生成完成, userId={}, 返回数量={}, 总耗时={}ms",
                    request.getUserId(), diversifiedItems.size(), 
                    System.currentTimeMillis() - startTime);
            
            return response;
            
        } catch (Exception e) {
            log.error("[推荐信息流] 生成失败, userId={}, error={}", 
                    request.getUserId(), e.getMessage(), e);
            return buildErrorResponse(request, e.getMessage());
        }
    }
    
    @Async
    @Override
    public CompletableFuture<RecommendationFeedResponseDTO> getRecommendationFeedAsync(
            RecommendationFeedRequestDTO request) {
        return CompletableFuture.completedFuture(getRecommendationFeed(request));
    }
    
    @Override
    public Map<String, List<RecallCandidate>> executeMultiChannelRecall(
            String userId, String scene, Double longitude, Double latitude, 
            Map<String, Object> params) {
        
        Map<String, List<RecallCandidate>> results = new ConcurrentHashMap<>();
        List<Future<?>> futures = new ArrayList<>();
        
        // GEO召回
        futures.add(recallExecutor.submit(() -> {
            results.put("GEO", geoRecallService.recall(userId, longitude, latitude, params));
        }));
        
        // 热门召回
        futures.add(recallExecutor.submit(() -> {
            results.put("HOT", hotRecallService.recall(userId, longitude, latitude, params));
        }));
        
        // 协同过滤召回（仅登录用户）
        if (userId != null && !userId.isEmpty()) {
            futures.add(recallExecutor.submit(() -> {
                results.put("CF_USER", cfRecallService.recallByUser(userId, params));
            }));
            
            futures.add(recallExecutor.submit(() -> {
                results.put("CF_ITEM", cfRecallService.recallByItem(userId, params));
            }));
        }
        
        // 向量召回
        futures.add(recallExecutor.submit(() -> {
            results.put("VECTOR", vectorRecallService.recall(userId, params));
        }));
        
        // 社交召回（仅登录用户）
        if (userId != null && !userId.isEmpty()) {
            futures.add(recallExecutor.submit(() -> {
                results.put("SOCIAL", socialRecallService.recall(userId, params));
            }));
        }
        
        // 实时行为召回
        futures.add(recallExecutor.submit(() -> {
            results.put("REALTIME", realtimeRecallService.recall(userId, params));
        }));
        
        // 等待所有召回完成
        for (Future<?> future : futures) {
            try {
                future.get(2, TimeUnit.SECONDS);
            } catch (Exception e) {
                log.warn("[多路召回] 某路召回超时或失败: {}", e.getMessage());
            }
        }
        
        return results;
    }
    
    @Override
    public List<RecommendationItem> executeRanking(List<RecallCandidate> candidates, 
                                                    String userId, String scene, Integer limit) {
        
        if (candidates == null || candidates.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 1. 去重
        Map<String, RecallCandidate> deduplicated = new LinkedHashMap<>();
        for (RecallCandidate candidate : candidates) {
            String key = candidate.getBusinessId() + "_" + candidate.getItemType();
            if (!deduplicated.containsKey(key)) {
                deduplicated.put(key, candidate);
            }
        }
        
        // 2. 构建特征
        List<RankingFeature> features = new ArrayList<>();
        for (RecallCandidate candidate : deduplicated.values()) {
            RankingFeature feature = buildRankingFeatures(candidate, userId, scene);
            features.add(feature);
        }
        
        // 3. 粗排（规则排序）
        List<RankingFeature> coarseRanked = ruleRankService.coarseRank(features, limit * 2);
        
        // 4. 精排（深度模型）
        List<RankingFeature> fineRanked = deepRankService.fineRank(coarseRanked, limit);
        
        // 5. 转换为推荐项
        return fineRanked.stream()
            .map(this::convertToRecommendationItem)
            .collect(Collectors.toList());
    }
    
    @Override
    public RankingFeature buildRankingFeatures(RecallCandidate candidate, String userId, String scene) {
        return RankingFeature.builder()
            .featureId(UUID.randomUUID().toString())
            .userId(userId)
            .itemId(candidate.getBusinessId())
            .recallSource(candidate.getRecallSource())
            .recallScore(candidate.getRecallScore())
            .userProfile(buildUserProfileFeature(userId))
            .userBehavior(buildUserBehaviorFeature(userId))
            .userContext(buildUserContextFeature(userId))
            .itemFeature(buildItemFeature(candidate))
            .itemStats(buildItemStatsFeature(candidate))
            .crossFeature(buildCrossFeature(candidate, userId))
            .sceneFeature(buildSceneFeature(scene))
            .featureGenerateTime(LocalDateTime.now())
            .featureVersion("v1.0.0")
            .build();
    }
    
    @Override
    public List<RecommendationItem> getNearbyRecommendations(Double longitude, Double latitude,
                                                              Integer radius, Integer pageNum, Integer pageSize) {
        Map<String, Object> params = new HashMap<>();
        params.put("radius", radius);
        
        List<RecallCandidate> candidates = geoRecallService.recall(null, longitude, latitude, params);
        return executeRanking(candidates, null, "NEARBY", pageSize);
    }
    
    @Override
    public List<RecommendationItem> getHomeFeed(String userId, Double longitude, Double latitude,
                                                 Integer pageNum, Integer pageSize) {
        RecommendationFeedRequestDTO request = RecommendationFeedRequestDTO.builder()
            .userId(userId)
            .scene("HOME")
            .longitude(longitude)
            .latitude(latitude)
            .pageNum(pageNum)
            .pageSize(pageSize)
            .build();
        
        RecommendationFeedResponseDTO response = getRecommendationFeed(request);
        return response.getItems() != null ? 
            response.getItems().stream()
                .map(this::convertDtoToItem)
                .collect(Collectors.toList()) : 
            Collections.emptyList();
    }
    
    @Override
    public List<RecommendationItem> getDiscoverFeed(String userId, Double longitude, Double latitude,
                                                     String category, Integer pageNum, Integer pageSize) {
        RecommendationFeedRequestDTO request = RecommendationFeedRequestDTO.builder()
            .userId(userId)
            .scene("DISCOVER")
            .longitude(longitude)
            .latitude(latitude)
            .categoryFilters(category != null ? Collections.singletonList(category) : null)
            .pageNum(pageNum)
            .pageSize(pageSize)
            .build();
        
        RecommendationFeedResponseDTO response = getRecommendationFeed(request);
        return response.getItems() != null ?
            response.getItems().stream()
                .map(this::convertDtoToItem)
                .collect(Collectors.toList()) :
            Collections.emptyList();
    }
    
    @Override
    public List<RecommendationItem> getGuessYouLike(String userId, Integer limit) {
        if (userId == null || userId.isEmpty()) {
            // 未登录用户返回热门
            Map<String, Object> params = new HashMap<>();
            params.put("limit", limit);
            List<RecallCandidate> candidates = hotRecallService.recall(null, null, null, params);
            return executeRanking(candidates, null, "FAVORITE", limit);
        }
        
        Map<String, Object> params = new HashMap<>();
        params.put("limit", limit);
        
        List<RecallCandidate> candidates = new ArrayList<>();
        candidates.addAll(cfRecallService.recallByUser(userId, params));
        candidates.addAll(vectorRecallService.recall(userId, params));
        
        return executeRanking(candidates, userId, "FAVORITE", limit);
    }
    
    @Override
    public List<RecommendationItem> getSceneBasedRecommendations(String userId, String sceneTag,
                                                                  Double longitude, Double latitude, 
                                                                  Integer limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("sceneTag", sceneTag);
        params.put("limit", limit);
        
        List<RecallCandidate> candidates = new ArrayList<>();
        candidates.addAll(geoRecallService.recall(userId, longitude, latitude, params));
        candidates.addAll(hotRecallService.recall(userId, longitude, latitude, params));
        
        return executeRanking(candidates, userId, "SCENE_" + sceneTag, limit);
    }
    
    @Override
    public List<RecommendationItem> getSimilarRecommendations(String itemId, String itemType, Integer limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("itemId", itemId);
        params.put("itemType", itemType);
        params.put("limit", limit);
        
        List<RecallCandidate> candidates = vectorRecallService.recallByItemSimilarity(itemId, itemType, limit);
        return executeRanking(candidates, null, "SIMILAR", limit);
    }
    
    @Override
    public List<RecommendationItem> getRelatedRecommendations(String itemId, String itemType,
                                                               String userId, Integer limit) {
        Map<String, Object> params = new HashMap<>();
        params.put("itemId", itemId);
        params.put("itemType", itemType);
        params.put("limit", limit);
        
        List<RecallCandidate> candidates = new ArrayList<>();
        candidates.addAll(cfRecallService.recallRelated(itemId, itemType, params));
        candidates.addAll(vectorRecallService.recallByItemSimilarity(itemId, itemType, limit / 2));
        
        return executeRanking(candidates, userId, "RELATED", limit);
    }
    
    @Override
    public void reportImpression(String userId, String itemId, String scene, 
                                  Integer position, Map<String, Object> extraParams) {
        // 异步记录曝光日志，用于模型训练和实时特征更新
        asyncLogImpression(userId, itemId, scene, position, extraParams);
    }
    
    @Override
    public void reportClick(String userId, String itemId, String scene, 
                             Integer position, Map<String, Object> extraParams) {
        // 异步记录点击日志，用于模型训练
        asyncLogClick(userId, itemId, scene, position, extraParams);
        
        // 更新实时特征
        updateRealtimeFeatures(userId, itemId, "click");
    }
    
    @Override
    public void reportConversion(String userId, String itemId, String scene, 
                                  String conversionType, Map<String, Object> extraParams) {
        // 异步记录转化日志
        asyncLogConversion(userId, itemId, scene, conversionType, extraParams);
        
        // 更新实时特征
        updateRealtimeFeatures(userId, itemId, "conversion_" + conversionType);
    }
    
    @Override
    public void refreshRecommendationCache(String userId, String scene) {
        String pattern = CACHE_KEY_PREFIX + userId + ":" + scene + "*";
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
            log.info("[推荐缓存] 刷新完成, userId={}, scene={}, 清除{}条缓存", 
                    userId, scene, keys.size());
        }
    }
    
    @Override
    public List<UserRecommendationFeed> getUserRecommendationHistory(String userId, Integer limit) {
        // 从数据库或缓存获取历史推荐记录
        String historyKey = "recommendation:history:" + userId;
        List<Object> history = redisTemplate.opsForList().range(historyKey, 0, limit - 1);
        
        if (history == null || history.isEmpty()) {
            return Collections.emptyList();
        }
        
        return history.stream()
            .map(obj -> (UserRecommendationFeed) obj)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<RecommendationFeedResponseDTO> batchGetRecommendations(
            List<RecommendationFeedRequestDTO> requests) {
        
        List<CompletableFuture<RecommendationFeedResponseDTO>> futures = requests.stream()
            .map(req -> getRecommendationFeedAsync(req))
            .collect(Collectors.toList());
        
        return futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());
    }
    
    // ==================== 私有辅助方法 ====================
    
    private String buildCacheKey(RecommendationFeedRequestDTO request) {
        return CACHE_KEY_PREFIX + request.getUserId() + ":" + request.getScene() + ":" +
               request.getPageNum() + ":" + request.getLongitude() + ":" + request.getLatitude();
    }
    
    private RecommendationFeedResponseDTO getFromCache(String key) {
        try {
            Object cached = redisTemplate.opsForValue().get(key);
            if (cached != null) {
                return (RecommendationFeedResponseDTO) cached;
            }
        } catch (Exception e) {
            log.warn("[推荐缓存] 读取失败: {}", e.getMessage());
        }
        return null;
    }
    
    private void putToCache(String key, RecommendationFeedResponseDTO response) {
        try {
            redisTemplate.opsForValue().set(key, response, CACHE_TTL_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            log.warn("[推荐缓存] 写入失败: {}", e.getMessage());
        }
    }
    
    private Map<String, Object> buildParamsFromRequest(RecommendationFeedRequestDTO request) {
        Map<String, Object> params = new HashMap<>();
        if (request.getCategoryFilters() != null) {
            params.put("categories", request.getCategoryFilters());
        }
        if (request.getPriceRangeFilters() != null) {
            params.put("priceRanges", request.getPriceRangeFilters());
        }
        if (request.getItemTypeFilters() != null) {
            params.put("itemTypes", request.getItemTypeFilters());
        }
        if (request.getMinRating() != null) {
            params.put("minRating", request.getMinRating());
        }
        if (request.getOnlyOpenNow() != null) {
            params.put("onlyOpenNow", request.getOnlyOpenNow());
        }
        if (request.getSceneTags() != null) {
            params.put("sceneTags", request.getSceneTags());
        }
        if (request.getKeyword() != null) {
            params.put("keyword", request.getKeyword());
        }
        params.put("radius", request.getSearchRadius());
        return params;
    }
    
    private UserRecommendationFeed.DiversityConfig buildDiversityConfig(
            RecommendationFeedRequestDTO request) {
        
        return UserRecommendationFeed.DiversityConfig.builder()
            .enableDiversity(true)
            .diversityAlgorithm("MMR")
            .maxMerchantRepeat(2)
            .typeRatio(new HashMap<String, Double>() {{
                put("POI", 0.5);
                put("ACTIVITY", 0.2);
                put("COUPON", 0.15);
                put("CONTENT", 0.15);
            }})
            .build();
    }
    
    private List<RecallCandidate> mergeCandidates(Map<String, List<RecallCandidate>> recallResults) {
        List<RecallCandidate> merged = new ArrayList<>();
        
        // 按优先级合并
        if (recallResults.containsKey("CF_USER")) {
            merged.addAll(recallResults.get("CF_USER"));
        }
        if (recallResults.containsKey("SOCIAL")) {
            merged.addAll(recallResults.get("SOCIAL"));
        }
        if (recallResults.containsKey("REALTIME")) {
            merged.addAll(recallResults.get("REALTIME"));
        }
        if (recallResults.containsKey("VECTOR")) {
            merged.addAll(recallResults.get("VECTOR"));
        }
        if (recallResults.containsKey("GEO")) {
            merged.addAll(recallResults.get("GEO"));
        }
        if (recallResults.containsKey("HOT")) {
            merged.addAll(recallResults.get("HOT"));
        }
        
        return merged;
    }
    
    private RecommendationFeedResponseDTO buildResponse(
            List<RecommendationItem> items,
            RecommendationFeedRequestDTO request,
            long recallTime,
            long rankTime,
            long totalTime,
            Set<String> recallStrategies) {
        
        List<RecommendationFeedResponseDTO.RecommendationItemDTO> itemDTOs = items.stream()
            .map(this::convertToItemDTO)
            .collect(Collectors.toList());
        
        RecommendationFeedResponseDTO.FeedStatsDTO stats = 
            RecommendationFeedResponseDTO.FeedStatsDTO.builder()
                .totalItems(itemDTOs.size())
                .recallTimeMs(recallTime)
                .sortTimeMs(rankTime)
                .totalGenerateTimeMs(totalTime)
                .build();
        
        RecommendationFeedResponseDTO.StrategyInfo strategyInfo =
            RecommendationFeedResponseDTO.StrategyInfo.builder()
                .recallStrategies(new ArrayList<>(recallStrategies))
                .sortStrategyVersion("v1.0.0")
                .algorithmVersion("deep_rank_v2")
                .build();
        
        return RecommendationFeedResponseDTO.builder()
            .status("SUCCESS")
            .code(200)
            .message("推荐生成成功")
            .userId(request.getUserId())
            .sessionId(UUID.randomUUID().toString())
            .items(itemDTOs)
            .pageNum(request.getPageNum())
            .pageSize(request.getPageSize())
            .hasMore(itemDTOs.size() >= request.getPageSize())
            .scene(request.getScene())
            .timestamp(LocalDateTime.now())
            .strategyInfo(strategyInfo)
            .statistics(stats)
            .build();
    }
    
    private RecommendationFeedResponseDTO buildErrorResponse(
            RecommendationFeedRequestDTO request, String errorMessage) {
        
        return RecommendationFeedResponseDTO.builder()
            .status("ERROR")
            .code(500)
            .message(errorMessage)
            .userId(request.getUserId())
            .scene(request.getScene())
            .timestamp(LocalDateTime.now())
            .items(Collections.emptyList())
            .hasMore(false)
            .build();
    }
    
    private RecommendationFeedResponseDTO.RecommendationItemDTO convertToItemDTO(
            RecommendationItem item) {
        
        return RecommendationFeedResponseDTO.RecommendationItemDTO.builder()
            .itemId(item.getItemId())
            .itemType(item.getItemType())
            .businessId(item.getBusinessId())
            .title(item.getTitle())
            .subtitle(item.getSubtitle())
            .mainImage(item.getMainImage())
            .imageList(item.getImageList())
            .longitude(item.getLongitude())
            .latitude(item.getLatitude())
            .address(item.getAddress())
            .distance(item.getDistance())
            .distanceText(item.getDistanceText())
            .categoryName(item.getCategoryName())
            .rating(item.getRating())
            .ratingCount(item.getRatingCount())
            .recommendReason(item.getRecommendReason())
            .recallSource(item.getRecallSource())
            .sceneTags(item.getSceneTags())
            .isPinned(item.getIsPinned())
            .isPromoted(item.getIsPromoted())
            .publishTime(item.getPublishTime())
            .build();
    }
    
    private RecommendationItem convertDtoToItem(
            RecommendationFeedResponseDTO.RecommendationItemDTO dto) {
        
        return RecommendationItem.builder()
            .itemId(dto.getItemId())
            .itemType(dto.getItemType())
            .businessId(dto.getBusinessId())
            .title(dto.getTitle())
            .subtitle(dto.getSubtitle())
            .mainImage(dto.getMainImage())
            .longitude(dto.getLongitude())
            .latitude(dto.getLatitude())
            .address(dto.getAddress())
            .distance(dto.getDistance())
            .rating(dto.getRating())
            .build();
    }
    
    private RecommendationItem convertToRecommendationItem(RankingFeature feature) {
        // 从特征和候选构建推荐项
        return RecommendationItem.builder()
            .itemId(feature.getItemId())
            .recallSource(feature.getRecallSource())
            .recallScore(feature.getRecallScore())
            .sortScore(feature.getFinalScore())
            .build();
    }
    
    // Feature building stubs
    private RankingFeature.UserProfileFeature buildUserProfileFeature(String userId) {
        // 从用户服务获取用户画像
        return RankingFeature.UserProfileFeature.builder().build();
    }
    
    private RankingFeature.UserBehaviorFeature buildUserBehaviorFeature(String userId) {
        // 从用户行为服务获取行为特征
        return RankingFeature.UserBehaviorFeature.builder().build();
    }
    
    private RankingFeature.UserContextFeature buildUserContextFeature(String userId) {
        // 构建用户上下文特征
        return RankingFeature.UserContextFeature.builder().build();
    }
    
    private RankingFeature.ItemFeature buildItemFeature(RecallCandidate candidate) {
        // 从物品服务获取物品特征
        return RankingFeature.ItemFeature.builder().build();
    }
    
    private RankingFeature.ItemStatsFeature buildItemStatsFeature(RecallCandidate candidate) {
        // 从统计服务获取物品统计特征
        return RankingFeature.ItemStatsFeature.builder().build();
    }
    
    private RankingFeature.CrossFeature buildCrossFeature(RecallCandidate candidate, String userId) {
        // 构建交叉特征
        return RankingFeature.CrossFeature.builder().build();
    }
    
    private RankingFeature.SceneFeature buildSceneFeature(String scene) {
        // 构建场景特征
        return RankingFeature.SceneFeature.builder().build();
    }
    
    // Async logging stubs
    private void asyncLogRecommendation(RecommendationFeedRequestDTO request, 
                                         RecommendationFeedResponseDTO response) {
        // 异步记录推荐日志
    }
    
    private void asyncLogImpression(String userId, String itemId, String scene, 
                                     Integer position, Map<String, Object> extraParams) {
        // 异步记录曝光
    }
    
    private void asyncLogClick(String userId, String itemId, String scene, 
                                Integer position, Map<String, Object> extraParams) {
        // 异步记录点击
    }
    
    private void asyncLogConversion(String userId, String itemId, String scene, 
                                     String conversionType, Map<String, Object> extraParams) {
        // 异步记录转化
    }
    
    private void updateRealtimeFeatures(String userId, String itemId, String actionType) {
        // 更新实时特征
    }
}
