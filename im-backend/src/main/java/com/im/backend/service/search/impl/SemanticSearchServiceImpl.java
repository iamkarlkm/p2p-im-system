package com.im.backend.service.search.impl;

import com.im.backend.dto.search.SemanticSearchRequestDTO;
import com.im.backend.dto.search.SemanticSearchResponseDTO;
import com.im.backend.entity.search.ConversationSession;
import com.im.backend.entity.search.SearchIntent;
import com.im.backend.entity.search.SemanticQuery;
import com.im.backend.service.search.SemanticSearchService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 语义搜索服务实现类
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Slf4j
@Service
public class SemanticSearchServiceImpl implements SemanticSearchService {
    
    // 意图识别关键词模式
    private static final Map<String, List<Pattern>> INTENT_PATTERNS = new HashMap<>();
    
    static {
        // 导航意图
        INTENT_PATTERNS.put(SearchIntent.INTENT_NAVIGATION, Arrays.asList(
            Pattern.compile("怎么去|怎么走|导航|路线|地图"),
            Pattern.compile("附近|周边|旁边")
        ));
        
        // 团购意图
        INTENT_PATTERNS.put(SearchIntent.INTENT_GROUP_BUY, Arrays.asList(
            Pattern.compile("团购|优惠|折扣|便宜|特价|券"),
            Pattern.compile("多少钱|价格|人均|消费")
        ));
        
        // 预约意图
        INTENT_PATTERNS.put(SearchIntent.INTENT_RESERVATION, Arrays.asList(
            Pattern.compile("预约|预订|订座|排队|取号"),
            Pattern.compile("有位置吗|有位吗|能订吗")
        ));
        
        // 比价意图
        INTENT_PATTERNS.put(SearchIntent.INTENT_PRICE_COMPARE, Arrays.asList(
            Pattern.compile("哪个便宜|性价比|对比|比较"),
            Pattern.compile("哪家划算|哪家好")
        ));
        
        // 详情意图
        INTENT_PATTERNS.put(SearchIntent.INTENT_DETAIL, Arrays.asList(
            Pattern.compile("怎么样|好不好|推荐|评价|评分"),
            Pattern.compile("特色|招牌|必点")
        ));
    }
    
    @Override
    public SemanticSearchResponseDTO semanticSearch(SemanticSearchRequestDTO request, Long userId) {
        long startTime = System.currentTimeMillis();
        log.info("Semantic search started - query: {}, userId: {}", request.getQuery(), userId);
        
        try {
            // 1. 理解查询
            SemanticQuery semanticQuery = understandQuery(request.getQuery(), userId, request.getSessionId());
            
            // 2. 解析意图
            Map<String, Object> context = buildContext(request);
            SearchIntent intent = parseIntent(request.getQuery(), context);
            
            // 3. 更新语义查询
            semanticQuery.setIntentType(intent.getPrimaryIntent());
            semanticQuery.setParseConfidence(intent.getConfidence());
            
            // 4. 检查是否需要澄清
            if (intent.getNeedsClarification() != null && intent.getNeedsClarification()) {
                return SemanticSearchResponseDTO.needsClarification(
                    request.getSessionId(),
                    intent.getClarificationQuestion(),
                    generateClarificationOptions(intent)
                );
            }
            
            // 5. 执行搜索
            List<SemanticSearchResponseDTO.SearchResultDTO> results = executeSearch(semanticQuery, request);
            
            // 6. 智能排序
            results = smartRankResults(results, semanticQuery, userId);
            
            // 7. 记录搜索历史
            recordSearchHistory(request.getQuery(), userId, results.size());
            
            long searchTime = System.currentTimeMillis() - startTime;
            
            // 8. 构建响应
            return SemanticSearchResponseDTO.builder()
                .status("SUCCESS")
                .sessionId(request.getSessionId())
                .intent(buildIntentDTO(intent))
                .results(results)
                .totalCount((long) results.size())
                .pageNum(request.getPageNum())
                .pageSize(request.getPageSize())
                .hasMore(results.size() >= request.getPageSize())
                .searchTimeMs(searchTime)
                .suggestedQueries(generateSuggestedQueries(semanticQuery))
                .build();
                
        } catch (Exception e) {
            log.error("Semantic search failed", e);
            return SemanticSearchResponseDTO.builder()
                .status("ERROR")
                .sessionId(request.getSessionId())
                .results(Collections.emptyList())
                .build();
        }
    }
    
    @Override
    public SearchIntent parseIntent(String query, Map<String, Object> context) {
        SearchIntent.SearchIntentBuilder builder = SearchIntent.builder();
        
        double maxConfidence = 0.0;
        String primaryIntent = SearchIntent.INTENT_GENERAL;
        
        // 匹配意图模式
        for (Map.Entry<String, List<Pattern>> entry : INTENT_PATTERNS.entrySet()) {
            double confidence = calculateIntentConfidence(query, entry.getValue());
            if (confidence > maxConfidence) {
                maxConfidence = confidence;
                primaryIntent = entry.getKey();
            }
        }
        
        builder.primaryIntent(primaryIntent);
        builder.confidence(maxConfidence);
        builder.needsClarification(maxConfidence < 0.6);
        
        if (maxConfidence < 0.6) {
            builder.clarificationQuestion(generateClarificationQuestion(builder.build()));
        }
        
        // 提取实体
        builder.entities(extractEntities(query));
        
        // 情感分析
        builder.sentiment(analyzeSentiment(query));
        
        return builder.build();
    }
    
    @Override
    public SemanticQuery understandQuery(String query, Long userId, String sessionId) {
        SemanticQuery.SemanticQueryBuilder builder = SemanticQuery.builder();
        
        builder.rawQuery(query);
        builder.userId(userId);
        builder.sessionId(sessionId);
        builder.normalizedQuery(normalizeQuery(query));
        
        // 提取关键词
        builder.keywords(extractKeywords(query));
        
        // 解析POI分类
        builder.poiCategory(detectPOICategory(query));
        
        // 解析价格约束
        PriceConstraint priceConstraint = parsePriceConstraint(query);
        if (priceConstraint != null) {
            builder.minPrice(priceConstraint.minPrice);
            builder.maxPrice(priceConstraint.maxPrice);
        }
        
        // 解析距离约束
        builder.maxDistance(parseDistanceConstraint(query));
        
        // 解析评分约束
        builder.minRating(parseRatingConstraint(query));
        
        // 检测多轮对话
        builder.isMultiTurn(sessionId != null && !sessionId.isEmpty());
        
        return builder.build();
    }
    
    @Override
    public Map<String, Object> buildElasticsearchQuery(SemanticQuery semanticQuery) {
        Map<String, Object> query = new HashMap<>();
        
        // 构建bool查询
        Map<String, Object> boolQuery = new HashMap<>();
        List<Map<String, Object>> must = new ArrayList<>();
        List<Map<String, Object>> filter = new ArrayList<>();
        
        // 文本查询
        if (semanticQuery.getNormalizedQuery() != null) {
            must.add(Map.of("multi_match", Map.of(
                "query", semanticQuery.getNormalizedQuery(),
                "fields", Arrays.asList("name^3", "category^2", "tags^2", "address", "description"),
                "type", "best_fields",
                "fuzziness", "AUTO"
            )));
        }
        
        // 分类过滤
        if (semanticQuery.getPoiCategory() != null) {
            filter.add(Map.of("term", Map.of("category", semanticQuery.getPoiCategory())));
        }
        
        // 地理位置过滤
        if (semanticQuery.getLongitude() != null && semanticQuery.getLatitude() != null 
                && semanticQuery.getMaxDistance() != null) {
            filter.add(Map.of("geo_distance", Map.of(
                "distance", semanticQuery.getMaxDistance() + "m",
                "location", Map.of(
                    "lon", semanticQuery.getLongitude(),
                    "lat", semanticQuery.getLatitude()
                )
            )));
        }
        
        // 评分过滤
        if (semanticQuery.getMinRating() != null) {
            filter.add(Map.of("range", Map.of("rating", Map.of("gte", semanticQuery.getMinRating()))));
        }
        
        boolQuery.put("must", must);
        boolQuery.put("filter", filter);
        query.put("bool", boolQuery);
        
        return query;
    }
    
    @Override
    public SemanticSearchResponseDTO multiTurnSearch(SemanticSearchRequestDTO request, 
                                                      ConversationSession session, 
                                                      Long userId) {
        // 继承会话上下文
        String contextualQuery = mergeWithContext(request.getQuery(), session.getContext());
        request.setQuery(contextualQuery);
        
        // 更新会话
        session.incrementTurn();
        
        return semanticSearch(request, userId);
    }
    
    @Override
    public List<String> getSearchSuggestions(String query, Long userId) {
        if (query == null || query.length() < 2) {
            return Collections.emptyList();
        }
        
        // 基于用户历史的个性化建议
        List<String> suggestions = new ArrayList<>();
        
        // 通用建议
        suggestions.add(query + " 附近");
        suggestions.add(query + " 推荐");
        suggestions.add("附近好的" + query);
        suggestions.add(query + " 便宜");
        suggestions.add(query + " 人均消费");
        
        return suggestions.stream().distinct().limit(8).collect(Collectors.toList());
    }
    
    @Override
    public String recognizeVoiceQuery(String voiceData) {
        // 语音转文本（模拟实现）
        log.info("Voice recognition started");
        // 实际实现需要调用语音识别服务
        return "语音识别结果";
    }
    
    @Override
    public String generateClarificationQuestion(SearchIntent intent) {
        return switch (intent.getPrimaryIntent()) {
            case SearchIntent.INTENT_NAVIGATION -> "您想去哪家店？请提供更具体的店名或地址。";
            case SearchIntent.INTENT_GROUP_BUY -> "您想找什么类型的团购？美食、娱乐还是其他？";
            case SearchIntent.INTENT_RESERVATION -> "您想预约什么时间？需要几人位？";
            default -> "您能再详细描述一下您的需求吗？";
        };
    }
    
    @Override
    public List<SemanticSearchResponseDTO.SearchResultDTO> smartRankResults(
            List<SemanticSearchResponseDTO.SearchResultDTO> results, 
            SemanticQuery semanticQuery, 
            Long userId) {
        
        return results.stream()
            .map(result -> {
                double score = calculateRelevanceScore(result, semanticQuery, userId);
                result.setScore(score);
                return result;
            })
            .sorted((a, b) -> Double.compare(b.getScore(), a.getScore()))
            .collect(Collectors.toList());
    }
    
    @Override
    public void recordSearchHistory(String query, Long userId, int resultCount) {
        log.info("Recording search history - query: {}, userId: {}, results: {}", query, userId, resultCount);
        // 实际实现需要写入数据库
    }
    
    @Override
    public List<String> getHotSearches(String cityCode, int limit) {
        // 模拟热门搜索
        return Arrays.asList(
            "附近美食",
            "火锅推荐",
            "周末去哪玩",
            "亲子餐厅",
            "网红打卡",
            "便宜好吃",
            "24小时营业",
            "免费停车"
        ).subList(0, Math.min(limit, 8));
    }
    
    @Override
    public List<String> getPersonalizedSuggestions(Long userId, int limit) {
        // 模拟个性化推荐
        return Arrays.asList(
            "根据您的喜好推荐",
            "常去附近的店",
            "您可能感兴趣",
            "新开的店",
            "好友推荐"
        ).subList(0, Math.min(limit, 5));
    }
    
    // ========== 私有辅助方法 ==========
    
    private double calculateIntentConfidence(String query, List<Pattern> patterns) {
        double confidence = 0.0;
        for (Pattern pattern : patterns) {
            if (pattern.matcher(query).find()) {
                confidence += 0.4;
            }
        }
        return Math.min(confidence, 0.95);
    }
    
    private String normalizeQuery(String query) {
        return query.toLowerCase()
            .replaceAll("[？?。.,，!！]", "")
            .trim();
    }
    
    private String extractKeywords(String query) {
        // 简单的关键词提取
        List<String> stopWords = Arrays.asList("的", "了", "在", "是", "我", "有", "和", "就", "不", "人", "都", "一", "一个", "上", "也", "很", "到", "说", "要", "去", "你", "会", "着", "没有", "看", "好", "自己", "这");
        
        return Arrays.stream(query.split("\\s+"))
            .filter(word -> !stopWords.contains(word))
            .collect(Collectors.joining(","));
    }
    
    private String detectPOICategory(String query) {
        if (query.contains("火锅") || query.contains("烧烤") || query.contains("川菜") || query.contains("粤菜")) {
            return "餐饮";
        }
        if (query.contains("电影") || query.contains("KTV") || query.contains("酒吧")) {
            return "娱乐";
        }
        if (query.contains("酒店") || query.contains("民宿")) {
            return "住宿";
        }
        if (query.contains("超市") || query.contains("商场")) {
            return "购物";
        }
        return null;
    }
    
    private PriceConstraint parsePriceConstraint(String query) {
        PriceConstraint constraint = new PriceConstraint();
        
        // 匹配 "人均100以下"
        java.util.regex.Matcher matcher = Pattern.compile("人均(\\d+)以下|人均(\\d+)左右|人均(\\d+)以内").matcher(query);
        if (matcher.find()) {
            String price = matcher.group(1) != null ? matcher.group(1) : matcher.group(2);
            if (price != null) {
                constraint.maxPrice = Double.parseDouble(price);
            }
        }
        
        // 匹配 "100-200元"
        matcher = Pattern.compile("(\\d+)[-~到](\\d+)").matcher(query);
        if (matcher.find()) {
            constraint.minPrice = Double.parseDouble(matcher.group(1));
            constraint.maxPrice = Double.parseDouble(matcher.group(2));
        }
        
        return constraint;
    }
    
    private Integer parseDistanceConstraint(String query) {
        if (query.contains("附近") || query.contains("周边")) {
            return 3000; // 3公里
        }
        if (query.contains("步行")) {
            return 1000; // 1公里
        }
        if (query.contains("远一点")) {
            return 10000; // 10公里
        }
        return null;
    }
    
    private Double parseRatingConstraint(String query) {
        if (query.contains("五星") || query.contains("好评") || query.contains("最好")) {
            return 4.5;
        }
        if (query.contains("不错") || query.contains("还可以")) {
            return 4.0;
        }
        return null;
    }
    
    private String analyzeSentiment(String query) {
        if (query.contains("好") || query.contains("棒") || query.contains("推荐")) {
            return "POSITIVE";
        }
        if (query.contains("差") || query.contains("坑") || query.contains("不要")) {
            return "NEGATIVE";
        }
        return "NEUTRAL";
    }
    
    private String extractEntities(String query) {
        Map<String, List<String>> entities = new HashMap<>();
        
        // 提取地点
        entities.put("location", Arrays.asList("附近", "周边"));
        
        // 提取时间
        if (query.contains("今天") || query.contains("明天") || query.contains("周末")) {
            entities.put("time", Arrays.asList("今天", "明天", "周末"));
        }
        
        return entities.toString();
    }
    
    private Map<String, Object> buildContext(SemanticSearchRequestDTO request) {
        Map<String, Object> context = new HashMap<>();
        context.put("longitude", request.getLongitude());
        context.put("latitude", request.getLatitude());
        context.put("cityCode", request.getCityCode());
        return context;
    }
    
    private SemanticSearchResponseDTO.SearchIntentDTO buildIntentDTO(SearchIntent intent) {
        return SemanticSearchResponseDTO.SearchIntentDTO.builder()
            .primaryIntent(intent.getPrimaryIntent())
            .confidence(intent.getConfidence())
            .description(getIntentDescription(intent.getPrimaryIntent()))
            .build();
    }
    
    private String getIntentDescription(String intent) {
        return switch (intent) {
            case SearchIntent.INTENT_NAVIGATION -> "导航到店";
            case SearchIntent.INTENT_GROUP_BUY -> "团购优惠";
            case SearchIntent.INTENT_RESERVATION -> "预约订座";
            case SearchIntent.INTENT_PRICE_COMPARE -> "价格比较";
            case SearchIntent.INTENT_DETAIL -> "查看详情";
            default -> "通用搜索";
        };
    }
    
    private List<String> generateClarificationOptions(SearchIntent intent) {
        return switch (intent.getPrimaryIntent()) {
            case SearchIntent.INTENT_GROUP_BUY -> Arrays.asList("美食团购", "娱乐团购", "酒店优惠");
            case SearchIntent.INTENT_RESERVATION -> Arrays.asList("今天", "明天", "本周末");
            default -> Arrays.asList("附近推荐", "热门榜单", "新店开业");
        };
    }
    
    private List<String> generateSuggestedQueries(SemanticQuery query) {
        List<String> suggestions = new ArrayList<>();
        
        if (query.isFoodQuery()) {
            suggestions.add("附近好吃的" + query.getPoiCategory());
            suggestions.add(query.getPoiCategory() + " 人均100以下");
        }
        
        suggestions.add("附近还有类似的店吗");
        suggestions.add(query.getRawQuery() + " 推荐");
        
        return suggestions.stream().limit(3).collect(Collectors.toList());
    }
    
    private double calculateRelevanceScore(SemanticSearchResponseDTO.SearchResultDTO result, 
                                          SemanticQuery query, Long userId) {
        double score = 0.0;
        
        // 距离评分（越近越好）
        if (result.getDistance() != null) {
            score += Math.max(0, 30 - result.getDistance() / 100);
        }
        
        // 评分权重
        if (result.getRating() != null) {
            score += result.getRating() * 5;
        }
        
        // 人气权重
        if (result.getReviewCount() != null) {
            score += Math.min(result.getReviewCount() / 100.0, 10);
        }
        
        // 匹配度加成
        if (query.getPoiCategory() != null && query.getPoiCategory().equals(result.getCategory())) {
            score += 15;
        }
        
        return score;
    }
    
    private List<SemanticSearchResponseDTO.SearchResultDTO> executeSearch(SemanticQuery query, 
                                                                          SemanticSearchRequestDTO request) {
        // 模拟搜索结果
        List<SemanticSearchResponseDTO.SearchResultDTO> results = new ArrayList<>();
        
        // 生成模拟数据
        for (int i = 0; i < request.getPageSize(); i++) {
            results.add(SemanticSearchResponseDTO.SearchResultDTO.builder()
                .poiId((long) (i + 1))
                .name("示例店铺" + (i + 1))
                .category(query.getPoiCategory() != null ? query.getPoiCategory() : "餐饮")
                .rating(4.0 + Math.random())
                .reviewCount((int) (Math.random() * 1000))
                .distance((int) (Math.random() * 5000))
                .address("示例地址" + (i + 1))
                .businessHours("09:00-22:00")
                .isOpen(true)
                .tags(Arrays.asList("推荐", "热门"))
                .quickActions(Arrays.asList("导航", "电话", "团购"))
                .build());
        }
        
        return results;
    }
    
    private String mergeWithContext(String query, String context) {
        if (context == null || context.isEmpty()) {
            return query;
        }
        // 简单的上下文合并
        return query;
    }
    
    // 价格约束内部类
    private static class PriceConstraint {
        Double minPrice;
        Double maxPrice;
    }
}
