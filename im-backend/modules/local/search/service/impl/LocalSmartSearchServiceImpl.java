package com.im.backend.modules.local.search.service.impl;

import com.im.backend.modules.local.search.dto.*;
import com.im.backend.modules.local.search.entity.*;
import com.im.backend.modules.local.search.service.LocalSmartSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 本地生活智能搜索服务实现
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LocalSmartSearchServiceImpl implements LocalSmartSearchService {
    
    private final StringRedisTemplate redisTemplate;
    
    private static final String SEARCH_CACHE_PREFIX = "search:";
    private static final String HOT_SEARCH_KEY = "hot:searches";
    private static final String USER_SEARCH_HISTORY_PREFIX = "search:history:";
    
    @Override
    public SmartSearchResponse smartSearch(SmartSearchRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("开始智能搜索: query={}, location=({}, {})", 
                    request.getQuery(), request.getLongitude(), request.getLatitude());
            
            // 1. 查询标准化和纠错
            String normalizedQuery = normalizeQuery(request.getQuery());
            boolean isCorrected = false;
            String originalQuery = null;
            
            if (!normalizedQuery.equals(request.getQuery())) {
                isCorrected = true;
                originalQuery = request.getQuery();
            }
            
            // 2. 解析搜索意图
            SearchIntent intent = parseSearchIntentInternal(normalizedQuery, request.getUserId());
            
            // 3. 构建搜索参数
            SearchParams searchParams = buildSearchParams(request, intent);
            
            // 4. 检查缓存
            String cacheKey = request.buildCacheKey();
            SmartSearchResponse cachedResult = getFromCache(cacheKey);
            if (cachedResult != null) {
                log.info("命中搜索缓存: {}", cacheKey);
                return cachedResult;
            }
            
            // 5. 执行搜索
            List<SmartSearchResponse.SearchResultItemDTO> results = executeSearch(searchParams);
            
            // 6. 排序
            results = reorderResults(results, request.getSortBy(), request.getUserId());
            
            // 7. 分页
            int total = results.size();
            int offset = request.getOffset();
            int end = Math.min(offset + request.getPageSize(), total);
            List<SmartSearchResponse.SearchResultItemDTO> pageResults = 
                    offset < total ? results.subList(offset, end) : new ArrayList<>();
            
            // 8. 获取推荐
            List<String> suggestions = generateSuggestions(normalizedQuery, intent);
            List<String> relatedQueries = generateRelatedQueries(normalizedQuery, intent);
            
            // 9. 保存搜索历史
            saveSearchHistory(request, intent);
            
            // 10. 更新热门搜索
            updateHotSearches(normalizedQuery);
            
            // 11. 构建响应
            SmartSearchResponse response = SmartSearchResponse.builder()
                    .originalQuery(request.getQuery())
                    .normalizedQuery(normalizedQuery)
                    .isCorrected(isCorrected)
                    .correctedFrom(isCorrected ? originalQuery : null)
                    .intent(buildIntentDTO(intent))
                    .results(pageResults)
                    .suggestions(suggestions)
                    .relatedQueries(relatedQueries)
                    .total((long) total)
                    .pageNum(request.getPageNum())
                    .pageSize(request.getPageSize())
                    .totalPages((int) Math.ceil((double) total / request.getPageSize()))
                    .responseTime(System.currentTimeMillis() - startTime)
                    .isZeroResult(results.isEmpty())
                    .zeroResultTip(results.isEmpty() ? generateZeroResultTip(request, intent) : null)
                    .build();
            
            // 12. 缓存结果
            putToCache(cacheKey, response);
            
            log.info("智能搜索完成: query={}, results={}, time={}ms", 
                    request.getQuery(), total, response.getResponseTime());
            
            return response;
            
        } catch (Exception e) {
            log.error("智能搜索失败: {}", request.getQuery(), e);
            throw new RuntimeException("搜索失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public SemanticSearchResponse semanticSearch(SemanticSearchRequest request) {
        long startTime = System.currentTimeMillis();
        
        try {
            log.info("开始语义搜索: query={}", request.getNaturalQuery());
            
            // 1. 解析自然语言查询
            SemanticParseResult parseResult = parseNaturalLanguage(request);
            
            // 2. 检查是否需要澄清
            if (parseResult.isNeedClarification()) {
                return SemanticSearchResponse.builder()
                        .parseResult(buildSemanticParseDTO(parseResult))
                        .needClarification(true)
                        .clarificationPrompt(parseResult.getClarificationPrompt())
                        .conversationId(request.getConversationId())
                        .responseTime(System.currentTimeMillis() - startTime)
                        .build();
            }
            
            // 3. 执行语义搜索
            List<SemanticSearchResponse.SemanticSearchResultDTO> results = 
                    executeSemanticSearch(parseResult, request);
            
            // 4. 生成对话回复
            String dialogueResponse = generateDialogueResponse(parseResult, results);
            
            log.info("语义搜索完成: query={}, results={}, time={}ms",
                    request.getNaturalQuery(), results.size(), System.currentTimeMillis() - startTime);
            
            return SemanticSearchResponse.builder()
                    .parseResult(buildSemanticParseDTO(parseResult))
                    .results(results)
                    .dialogueResponse(dialogueResponse)
                    .needClarification(false)
                    .conversationId(request.getConversationId())
                    .currentTurn(request.getDialogueHistory() != null ? 
                            request.getDialogueHistory().size() + 1 : 1)
                    .responseTime(System.currentTimeMillis() - startTime)
                    .build();
            
        } catch (Exception e) {
            log.error("语义搜索失败: {}", request.getNaturalQuery(), e);
            throw new RuntimeException("语义搜索失败: " + e.getMessage(), e);
        }
    }
    
    @Override
    public SearchIntent parseSearchIntent(String query, Long userId) {
        return parseSearchIntentInternal(query, userId);
    }
    
    @Override
    public SmartSearchResponse multiTurnSearch(SmartSearchRequest request, String conversationId) {
        // 获取对话上下文
        SearchContext context = getSearchContext(conversationId);
        
        // 合并当前查询与上下文
        String mergedQuery = mergeWithContext(request.getQuery(), context);
        
        // 创建新的搜索请求
        SmartSearchRequest mergedRequest = SmartSearchRequest.builder()
                .query(mergedQuery)
                .longitude(request.getLongitude())
                .latitude(request.getLatitude())
                .radius(request.getRadius())
                .sortBy(request.getSortBy())
                .conversationId(conversationId)
                .isFollowUp(true)
                .build();
        
        // 执行搜索
        SmartSearchResponse response = smartSearch(mergedRequest);
        
        // 更新对话上下文
        updateSearchContext(conversationId, request, response);
        
        return response;
    }
    
    @Override
    public List<String> getSearchSuggestions(String prefix, Double longitude, Double latitude, Integer limit) {
        if (!StringUtils.hasText(prefix) || prefix.length() < 2) {
            return new ArrayList<>();
        }
        
        limit = limit != null ? limit : 10;
        
        // 从Redis获取热门搜索建议
        Set<String> suggestions = redisTemplate.opsForZSet()
                .rangeByScore("search:suggestions:" + prefix.toLowerCase(), 0, Double.MAX_VALUE, 0, limit);
        
        if (suggestions != null && !suggestions.isEmpty()) {
            return new ArrayList<>(suggestions);
        }
        
        // 生成默认建议
        return generateDefaultSuggestions(prefix, limit);
    }
    
    @Override
    public List<String> getHotSearches(Double longitude, Double latitude, Integer limit) {
        limit = limit != null ? limit : 10;
        
        // 从Redis获取热门搜索
        Set<String> hotSearches = redisTemplate.opsForZSet()
                .reverseRange(HOT_SEARCH_KEY, 0, limit - 1);
        
        if (hotSearches != null && !hotSearches.isEmpty()) {
            return new ArrayList<>(hotSearches);
        }
        
        // 返回默认热门搜索
        return Arrays.asList("火锅", "烧烤", "日料", "咖啡厅", "KTV", "电影院");
    }
    
    @Override
    public List<SmartSearchResponse.KnowledgeGraphRecommendationDTO> getKgRecommendations(Long poiId, Integer limit) {
        // 从知识图谱获取推荐
        List<SmartSearchResponse.KnowledgeGraphRecommendationDTO> recommendations = new ArrayList<>();
        
        // 相似推荐
        recommendations.add(SmartSearchResponse.KnowledgeGraphRecommendationDTO.builder()
                .type("SIMILAR")
                .title("相似推荐")
                .pois(getSimilarPois(poiId, limit))
                .build());
        
        // 互补推荐
        recommendations.add(SmartSearchResponse.KnowledgeGraphRecommendationDTO.builder()
                .type("COMPLEMENTARY")
                .title("搭配推荐")
                .pois(getComplementaryPois(poiId, limit))
                .build());
        
        return recommendations;
    }
    
    @Override
    public void recordSearchClick(Long queryId, Long poiId, Integer index) {
        log.info("记录搜索点击: queryId={}, poiId={}, index={}", queryId, poiId, index);
        // 更新搜索查询的点击信息
        // 用于优化搜索排序
    }
    
    @Override
    public List<LocalSearchQuery> getSearchHistory(Long userId, Integer limit) {
        limit = limit != null ? limit : 20;
        
        // 从Redis获取搜索历史
        List<String> history = redisTemplate.opsForList()
                .range(USER_SEARCH_HISTORY_PREFIX + userId, 0, limit - 1);
        
        if (history == null || history.isEmpty()) {
            return new ArrayList<>();
        }
        
        // 解析搜索历史
        return history.stream()
                .map(this::parseSearchHistoryItem)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    public void clearSearchHistory(Long userId) {
        redisTemplate.delete(USER_SEARCH_HISTORY_PREFIX + userId);
        log.info("清空搜索历史: userId={}", userId);
    }
    
    @Override
    public void deleteSearchHistory(Long userId, Long queryId) {
        // 从列表中移除指定查询
        String key = USER_SEARCH_HISTORY_PREFIX + userId;
        // 实际实现需要遍历列表查找并删除
        log.info("删除搜索历史: userId={}, queryId={}", userId, queryId);
    }
    
    @Override
    public SmartSearchResponse voiceSearch(byte[] audioData, Double longitude, Double latitude, String dialect) {
        // 1. 语音识别
        String recognizedText = recognizeSpeech(audioData, dialect);
        
        // 2. 执行搜索
        SmartSearchRequest request = SmartSearchRequest.builder()
                .query(recognizedText)
                .longitude(longitude)
                .latitude(latitude)
                .isVoice(true)
                .dialect(dialect)
                .build();
        
        return smartSearch(request);
    }
    
    @Override
    public Map<String, Object> getSearchStatistics(String startTime, String endTime) {
        Map<String, Object> statistics = new HashMap<>();
        
        // 搜索次数统计
        statistics.put("totalSearches", 10000);
        statistics.put("uniqueUsers", 5000);
        statistics.put("avgResponseTime", 120);
        
        // 热门搜索词
        statistics.put("topQueries", getHotSearches(null, null, 10));
        
        // 零结果率
        statistics.put("zeroResultRate", 0.05);
        
        return statistics;
    }
    
    @Override
    public List<SmartSearchResponse.SearchResultItemDTO> reorderResults(
            List<SmartSearchResponse.SearchResultItemDTO> results, String sortBy, Long userId) {
        
        if (results == null || results.isEmpty()) {
            return results;
        }
        
        switch (sortBy) {
            case "DISTANCE":
                results.sort(Comparator.comparing(SmartSearchResponse.SearchResultItemDTO::getDistance));
                break;
            case "RATING":
                results.sort(Comparator.comparing(SmartSearchResponse.SearchResultItemDTO::getRating, 
                        Comparator.reverseOrder()));
                break;
            case "POPULARITY":
                results.sort(Comparator.comparing(SmartSearchResponse.SearchResultItemDTO::getHeatScore,
                        Comparator.reverseOrder()));
                break;
            case "PRICE_ASC":
                results.sort(Comparator.comparing(SmartSearchResponse.SearchResultItemDTO::getAvgPrice,
                        Comparator.nullsLast(Comparator.naturalOrder())));
                break;
            case "PRICE_DESC":
                results.sort(Comparator.comparing(SmartSearchResponse.SearchResultItemDTO::getAvgPrice,
                        Comparator.nullsLast(Comparator.reverseOrder())));
                break;
            case "SMART":
            default:
                results = smartReorder(results, userId);
                break;
        }
        
        return results;
    }
    
    // ==================== 私有方法 ====================
    
    private String normalizeQuery(String query) {
        if (!StringUtils.hasText(query)) {
            return "";
        }
        // 去除多余空格
        String normalized = query.trim().replaceAll("\\s+", " ");
        // 转换为小写
        normalized = normalized.toLowerCase();
        // 繁体转简体（实际项目中使用OpenCC）
        return normalized;
    }
    
    private SearchIntent parseSearchIntentInternal(String query, Long userId) {
        // 意图解析逻辑
        SearchIntent intent = new SearchIntent();
        intent.setPrimaryIntent(detectPrimaryIntent(query));
        intent.setConfidence(0.85);
        intent.setEntities(extractEntities(query));
        intent.setSortPreference(detectSortPreference(query));
        intent.setSceneTag(detectScene(query));
        return intent;
    }
    
    private String detectPrimaryIntent(String query) {
        if (query.contains("怎么去") || query.contains("导航") || query.contains("路线")) {
            return "NAVIGATION";
        } else if (query.contains("团购") || query.contains("优惠") || query.contains("便宜")) {
            return "GROUPON";
        } else if (query.contains("预约") || query.contains("订位")) {
            return "RESERVATION";
        } else if (query.contains("哪个好") || query.contains("对比")) {
            return "COMPARISON";
        }
        return "INFO";
    }
    
    private String extractEntities(String query) {
        // 实体提取逻辑
        List<String> entities = new ArrayList<>();
        // 简化的实体提取
        if (query.contains("火锅")) entities.add("火锅");
        if (query.contains("烧烤")) entities.add("烧烤");
        if (query.contains("附近")) entities.add("附近");
        return entities.toString();
    }
    
    private String detectSortPreference(String query) {
        if (query.contains("最近") || query.contains("最近距离")) {
            return "DISTANCE";
        } else if (query.contains("最好") || query.contains("评分高")) {
            return "RATING";
        } else if (query.contains("便宜")) {
            return "PRICE_ASC";
        } else if (query.contains("高档") || query.contains("贵")) {
            return "PRICE_DESC";
        }
        return "SMART";
    }
    
    private String detectScene(String query) {
        if (query.contains("约会") || query.contains("浪漫")) {
            return "DATE";
        } else if (query.contains("家庭") || query.contains("带娃") || query.contains("亲子")) {
            return "FAMILY";
        } else if (query.contains("商务") || query.contains("请客")) {
            return "BUSINESS";
        } else if (query.contains("朋友") || query.contains("聚会")) {
            return "FRIENDS";
        }
        return null;
    }
    
    private SearchParams buildSearchParams(SmartSearchRequest request, SearchIntent intent) {
        SearchParams params = new SearchParams();
        params.setQuery(request.getQuery());
        params.setLongitude(request.getLongitude());
        params.setLatitude(request.getLatitude());
        params.setRadius(request.getRadius());
        params.setPoiType(request.getPoiType());
        params.setCategory(request.getCategory());
        params.setMinRating(request.getMinRating());
        params.setMinPrice(request.getMinPrice());
        params.setMaxPrice(request.getMaxPrice());
        params.setFeatures(request.getFeatures());
        params.setSceneTag(request.getSceneTag());
        return params;
    }
    
    private SmartSearchResponse getFromCache(String cacheKey) {
        try {
            // 实际项目中从Redis获取缓存
            return null;
        } catch (Exception e) {
            return null;
        }
    }
    
    private void putToCache(String cacheKey, SmartSearchResponse response) {
        try {
            // 实际项目中存入Redis，过期时间5分钟
            // redisTemplate.opsForValue().set(cacheKey, response, 5, TimeUnit.MINUTES);
        } catch (Exception e) {
            log.warn("缓存搜索结果失败: {}", cacheKey);
        }
    }
    
    private List<SmartSearchResponse.SearchResultItemDTO> executeSearch(SearchParams params) {
        // 实际项目中调用Elasticsearch或数据库查询
        // 这里返回模拟数据
        List<SmartSearchResponse.SearchResultItemDTO> results = new ArrayList<>();
        
        // 模拟搜索结果
        for (int i = 0; i < 20; i++) {
            results.add(SmartSearchResponse.SearchResultItemDTO.builder()
                    .poiId((long) (i + 1))
                    .name("示例商家 " + (i + 1))
                    .rating(4.0 + Math.random())
                    .avgPrice(50 + Math.random() * 200)
                    .distance((i + 1) * 100)
                    .distanceText((i + 1) * 100 + "米")
                    .address("示例地址 " + (i + 1))
                    .build());
        }
        
        return results;
    }
    
    private List<String> generateSuggestions(String query, SearchIntent intent) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add(query + " 附近");
        suggestions.add(query + " 优惠");
        suggestions.add("最好的" + query);
        suggestions.add("便宜的" + query);
        return suggestions;
    }
    
    private List<String> generateRelatedQueries(String query, SearchIntent intent) {
        List<String> related = new ArrayList<>();
        related.add("附近的" + query);
        related.add(query + " 推荐");
        related.add(query + " 评价");
        return related;
    }
    
    private String generateZeroResultTip(SmartSearchRequest request, SearchIntent intent) {
        return "抱歉，没有找到相关结果。建议您：\n1. 检查关键词是否拼写正确\n2. 扩大搜索范围\n3. 尝试其他关键词";
    }
    
    private void saveSearchHistory(SmartSearchRequest request, SearchIntent intent) {
        if (request.getUserId() == null) {
            return;
        }
        
        String key = USER_SEARCH_HISTORY_PREFIX + request.getUserId();
        String historyItem = String.format("%d|%s|%s", 
                System.currentTimeMillis(), request.getQuery(), intent.getPrimaryIntent());
        
        redisTemplate.opsForList().leftPush(key, historyItem);
        redisTemplate.opsForList().trim(key, 0, 99); // 保留最近100条
        redisTemplate.expire(key, 30, TimeUnit.DAYS);
    }
    
    private void updateHotSearches(String query) {
        redisTemplate.opsForZSet().incrementScore(HOT_SEARCH_KEY, query, 1);
    }
    
    private SmartSearchResponse.SearchIntentDTO buildIntentDTO(SearchIntent intent) {
        return SmartSearchResponse.SearchIntentDTO.builder()
                .primaryIntent(intent.getPrimaryIntent())
                .confidence(intent.getConfidence())
                .intentDescription(getIntentDescription(intent.getPrimaryIntent()))
                .entities(intent.getEntityList())
                .timeConstraint(intent.getTimeConstraint())
                .priceConstraint(intent.getPriceRangeDescription())
                .sceneTag(intent.getSceneTag())
                .build();
    }
    
    private String getIntentDescription(String intent) {
        switch (intent) {
            case "NAVIGATION": return "导航前往";
            case "GROUPON": return "查找优惠";
            case "RESERVATION": return "预约服务";
            case "COMPARISON": return "对比选择";
            case "INFO": return "了解详情";
            default: return "搜索";
        }
    }
    
    private SemanticParseResult parseNaturalLanguage(SemanticSearchRequest request) {
        // 自然语言解析逻辑
        SemanticParseResult result = new SemanticParseResult();
        result.setQuery(request.getNaturalQuery());
        result.setIntent(detectPrimaryIntent(request.getNaturalQuery()));
        result.setConfidence(0.88);
        result.setNeedClarification(false);
        return result;
    }
    
    private List<SemanticSearchResponse.SemanticSearchResultDTO> executeSemanticSearch(
            SemanticParseResult parseResult, SemanticSearchRequest request) {
        // 执行语义搜索
        List<SemanticSearchResponse.SemanticSearchResultDTO> results = new ArrayList<>();
        
        // 模拟结果
        for (int i = 0; i < 10; i++) {
            results.add(SemanticSearchResponse.SemanticSearchResultDTO.builder()
                    .poiId((long) (i + 1))
                    .name("匹配商家 " + (i + 1))
                    .rating(4.0 + Math.random())
                    .avgPrice(50 + Math.random() * 200)
                    .distance((i + 1) * 150)
                    .matchReason("符合您的" + parseResult.getIntent() + "需求")
                    .matchScore(0.8 + Math.random() * 0.2)
                    .semanticMatch(true)
                    .build());
        }
        
        return results;
    }
    
    private String generateDialogueResponse(SemanticParseResult parseResult, 
            List<SemanticSearchResponse.SemanticSearchResultDTO> results) {
        if (results.isEmpty()) {
            return "抱歉，没有找到符合条件的结果。您可以尝试放宽条件或者换一个描述方式。";
        }
        return String.format("为您找到%d家符合条件的商家，评分最高的「%s」距离您%d米，人均消费%.0f元。",
                results.size(),
                results.get(0).getName(),
                results.get(0).getDistance(),
                results.get(0).getAvgPrice());
    }
    
    private SemanticSearchResponse.SemanticParseResultDTO buildSemanticParseDTO(SemanticParseResult result) {
        return SemanticSearchResponse.SemanticParseResultDTO.builder()
                .originalQuery(result.getQuery())
                .intent(result.getIntent())
                .confidence(result.getConfidence())
                .build();
    }
    
    private SearchContext getSearchContext(String conversationId) {
        // 获取对话上下文
        return new SearchContext();
    }
    
    private void updateSearchContext(String conversationId, SmartSearchRequest request, 
            SmartSearchResponse response) {
        // 更新对话上下文
    }
    
    private String mergeWithContext(String query, SearchContext context) {
        // 合并查询与上下文
        return query;
    }
    
    private LocalSearchQuery parseSearchHistoryItem(String item) {
        try {
            String[] parts = item.split("\\|");
            if (parts.length >= 2) {
                LocalSearchQuery query = new LocalSearchQuery();
                query.setRawQuery(parts[1]);
                query.setIntentType(parts.length > 2 ? parts[2] : "INFO");
                return query;
            }
        } catch (Exception e) {
            log.warn("解析搜索历史失败: {}", item);
        }
        return null;
    }
    
    private List<String> generateDefaultSuggestions(String prefix, int limit) {
        List<String> suggestions = new ArrayList<>();
        suggestions.add(prefix + "附近");
        suggestions.add(prefix + "推荐");
        suggestions.add("最好的" + prefix);
        return suggestions.subList(0, Math.min(suggestions.size(), limit));
    }
    
    private String recognizeSpeech(byte[] audioData, String dialect) {
        // 语音识别逻辑
        return "语音识别的搜索内容";
    }
    
    private List<SmartSearchResponse.SimplePoiDTO> getSimilarPois(Long poiId, Integer limit) {
        List<SmartSearchResponse.SimplePoiDTO> pois = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            pois.add(SmartSearchResponse.SimplePoiDTO.builder()
                    .poiId((long) (i + 100))
                    .name("相似商家 " + (i + 1))
                    .rating(4.5)
                    .distanceText("500米")
                    .build());
        }
        return pois;
    }
    
    private List<SmartSearchResponse.SimplePoiDTO> getComplementaryPois(Long poiId, Integer limit) {
        List<SmartSearchResponse.SimplePoiDTO> pois = new ArrayList<>();
        for (int i = 0; i < limit; i++) {
            pois.add(SmartSearchResponse.SimplePoiDTO.builder()
                    .poiId((long) (i + 200))
                    .name("搭配商家 " + (i + 1))
                    .rating(4.3)
                    .distanceText("300米")
                    .build());
        }
        return pois;
    }
    
    private List<SmartSearchResponse.SearchResultItemDTO> smartReorder(
            List<SmartSearchResponse.SearchResultItemDTO> results, Long userId) {
        // 智能排序算法
        // 综合考虑距离、评分、热度、个性化等因素
        return results.stream()
                .sorted((a, b) -> {
                    double scoreA = calculateSmartScore(a, userId);
                    double scoreB = calculateSmartScore(b, userId);
                    return Double.compare(scoreB, scoreA);
                })
                .collect(Collectors.toList());
    }
    
    private double calculateSmartScore(SmartSearchResponse.SearchResultItemDTO item, Long userId) {
        double distanceScore = item.getDistance() != null ? 
                Math.max(0, 1 - (double) item.getDistance() / 5000) : 0;
        double ratingScore = item.getRating() != null ? item.getRating() / 5.0 : 0;
        double heatScore = item.getHeatScore() != null ? 
                Math.min(1, item.getHeatScore() / 1000.0) : 0;
        
        return distanceScore * 0.3 + ratingScore * 0.4 + heatScore * 0.3;
    }
    
    // ==================== 内部类 ====================
    
    @Data
    private static class SearchParams {
        private String query;
        private Double longitude;
        private Double latitude;
        private Integer radius;
        private String poiType;
        private String category;
        private Double minRating;
        private Double minPrice;
        private Double maxPrice;
        private List<String> features;
        private String sceneTag;
    }
    
    @Data
    private static class SemanticParseResult {
        private String query;
        private String intent;
        private Double confidence;
        private Boolean needClarification;
        private String clarificationPrompt;
    }
    
    @Data
    private static class SearchContext {
        private String lastQuery;
        private String lastIntent;
        private List<String> accumulatedFilters;
    }
}
