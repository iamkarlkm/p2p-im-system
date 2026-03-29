package com.im.backend.modules.local.search.service.impl;

import com.im.backend.modules.local.search.dto.*;
import com.im.backend.modules.local.search.entity.PoiKnowledgeGraph;
import com.im.backend.modules.local.search.entity.SearchHotKeyword;
import com.im.backend.modules.local.search.entity.SearchQueryLog;
import com.im.backend.modules.local.search.entity.SearchSynonymDictionary;
import com.im.backend.modules.local.search.enums.HotTrend;
import com.im.backend.modules.local.search.enums.SearchIntentType;
import com.im.backend.modules.local.search.repository.PoiKnowledgeGraphMapper;
import com.im.backend.modules.local.search.repository.SearchHotKeywordMapper;
import com.im.backend.modules.local.search.repository.SearchQueryLogMapper;
import com.im.backend.modules.local.search.repository.SearchSynonymDictionaryMapper;
import com.im.backend.modules.local.search.service.ISmartSearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 智能搜索服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class SmartSearchServiceImpl implements ISmartSearchService {

    private final SearchQueryLogMapper queryLogMapper;
    private final PoiKnowledgeGraphMapper knowledgeGraphMapper;
    private final SearchHotKeywordMapper hotKeywordMapper;
    private final SearchSynonymDictionaryMapper synonymDictionaryMapper;

    // 意图识别关键词模式
    private static final Map<String, Pattern> INTENT_PATTERNS = new HashMap<>();
    static {
        INTENT_PATTERNS.put("NAVIGATION", Pattern.compile("(怎么去|在哪里|怎么走|导航|路线|地址)"));
        INTENT_PATTERNS.put("PRICE_QUERY", Pattern.compile("(多少钱|价格|贵吗|便宜|人均|消费)"));
        INTENT_PATTERNS.put("RESERVATION", Pattern.compile("(预约|预订|订位|排队|等位)"));
        INTENT_PATTERNS.put("DELIVERY", Pattern.compile("(外卖|配送|送到|送餐)"));
        INTENT_PATTERNS.put("REVIEW", Pattern.compile("(评价|口碑|怎么样|好吃吗|推荐)"));
        INTENT_PATTERNS.put("GROUP_BUY", Pattern.compile("(团购|优惠|券|折扣|套餐)"));
    }

    @Override
    public SemanticSearchResponse semanticSearch(SemanticSearchRequest request) {
        long startTime = System.currentTimeMillis();
        SemanticSearchResponse response = new SemanticSearchResponse();

        // 1. 自然语言理解
        NluParseRequest nluRequest = new NluParseRequest();
        nluRequest.setQuery(request.getQuery());
        nluRequest.setContext(request.getSessionId());
        nluRequest.setLat(request.getLat());
        nluRequest.setLng(request.getLng());

        NluParseResponse nluResult = parseNaturalLanguage(nluRequest);
        response.setIntentType(nluResult.getIntent());
        response.setIntentConfidence(nluResult.getConfidence());
        response.setIsMultiTurn(nluResult.getNeedsClarification());

        // 2. 执行搜索
        List<SemanticSearchResponse.SearchResultItem> results = performSearch(
                nluResult, request.getLat(), request.getLng(), request.getRadius(),
                request.getSortBy(), request.getPageNum(), request.getPageSize()
        );

        // 3. 搜索纠错
        if (results.isEmpty() && !nluResult.getNormalizedQuery().equals(request.getQuery())) {
            response.setHasCorrection(true);
            response.setCorrectedQuery(nluResult.getNormalizedQuery());
        } else {
            response.setHasCorrection(false);
        }

        // 4. 生成相关推荐
        response.setRelatedQueries(generateRelatedQueries(nluResult));

        response.setResults(results);
        response.setTotal((long) results.size());
        response.setResponseTime((int) (System.currentTimeMillis() - startTime));

        // 5. 记录搜索日志
        saveSearchLog(request, response);

        return response;
    }

    @Override
    public NluParseResponse parseNaturalLanguage(NluParseRequest request) {
        NluParseResponse response = new NluParseResponse();
        String query = request.getQuery();
        response.setOriginalQuery(query);

        // 1. 标准化查询文本
        String normalized = normalizeQuery(query);
        response.setNormalizedQuery(normalized);

        // 2. 意图识别
        String intent = recognizeIntent(query);
        response.setIntent(intent);
        response.setConfidence(calculateIntentConfidence(query, intent));

        // 3. 实体抽取
        List<NluParseResponse.ExtractedEntity> entities = extractEntities(query);
        response.setEntities(entities);

        // 4. 槽位填充
        response.setSlots(extractSlots(query, entities));

        // 5. 判断是否需要澄清
        if (response.getConfidence() < 60) {
            response.setNeedsClarification(true);
            response.setClarificationPrompt(generateClarificationPrompt(intent));
        } else {
            response.setNeedsClarification(false);
        }

        // 6. 情感分析
        response.setSentiment(analyzeSentiment(query));

        return response;
    }

    @Override
    public List<String> getSearchSuggestions(SearchSuggestionRequest request) {
        String keyword = request.getKeyword();
        List<String> suggestions = new ArrayList<>();

        // 1. 从历史搜索中匹配
        List<PoiKnowledgeGraph> entities = knowledgeGraphMapper.searchByKeyword(keyword, 5);
        suggestions.addAll(entities.stream()
                .map(PoiKnowledgeGraph::getEntityName)
                .collect(Collectors.toList()));

        // 2. 从热词中匹配
        List<SearchHotKeyword> hotKeywords = hotKeywordMapper.selectRisingKeywords(
                LocalDateTime.now().toString().substring(0, 10), 5);
        suggestions.addAll(hotKeywords.stream()
                .filter(k -> k.getKeyword().contains(keyword))
                .map(SearchHotKeyword::getKeyword)
                .collect(Collectors.toList()));

        return suggestions.stream().distinct().limit(request.getLimit()).collect(Collectors.toList());
    }

    @Override
    public HotKeywordResponse getHotKeywords(String category, Integer limit) {
        HotKeywordResponse response = new HotKeywordResponse();
        String today = LocalDateTime.now().toString().substring(0, 10);

        List<SearchHotKeyword> keywords;
        if (category != null && !category.isEmpty()) {
            keywords = hotKeywordMapper.selectByCategory(category, today, limit);
        } else {
            keywords = hotKeywordMapper.selectByDateAndPeriod(today, "DAY");
        }

        List<HotKeywordResponse.HotKeywordItem> items = keywords.stream()
                .map(k -> {
                    HotKeywordResponse.HotKeywordItem item = new HotKeywordResponse.HotKeywordItem();
                    item.setKeyword(k.getKeyword());
                    item.setRank(k.getHotRank());
                    item.setSearchCount(k.getSearchCount());
                    item.setTrend(k.getTrend());
                    item.setTrendIcon(getTrendIcon(k.getTrend()));
                    return item;
                })
                .collect(Collectors.toList());

        response.setKeywords(items);
        response.setUpdateTime(LocalDateTime.now().toString());
        return response;
    }

    @Override
    public KnowledgeGraphResponse queryKnowledgeGraph(String entityName) {
        PoiKnowledgeGraph entity = knowledgeGraphMapper.selectByEntityName(entityName);
        if (entity == null) {
            return null;
        }

        KnowledgeGraphResponse response = new KnowledgeGraphResponse();
        response.setEntityId(entity.getEntityId());
        response.setEntityName(entity.getEntityName());
        response.setEntityType(entity.getEntityType());
        response.setProperties(parseJsonToMap(entity.getEntityProperties()));

        // 查询关联实体
        List<PoiKnowledgeGraph> related = knowledgeGraphMapper.selectRelatedEntities(entity.getEntityId());
        List<KnowledgeGraphResponse.RelatedEntity> relatedEntities = related.stream()
                .map(r -> {
                    KnowledgeGraphResponse.RelatedEntity re = new KnowledgeGraphResponse.RelatedEntity();
                    re.setEntityId(r.getEntityId());
                    re.setEntityName(r.getEntityName());
                    re.setEntityType(r.getEntityType());
                    re.setProperties(parseJsonToMap(r.getEntityProperties()));
                    return re;
                })
                .collect(Collectors.toList());

        response.setRelatedEntities(relatedEntities);
        return response;
    }

    @Override
    public String correctSearchQuery(String query) {
        // 简单的拼写纠错逻辑
        String normalized = query.toLowerCase()
                .replaceAll("火窝", "火锅")
                .replaceAll("烧烤", "烧烤")
                .replaceAll("kfc", "肯德基")
                .replaceAll("mc", "麦当劳");
        return normalized;
    }

    @Override
    public List<String> expandSynonyms(String keyword) {
        SearchSynonymDictionary dict = synonymDictionaryMapper.selectByStandardWord(keyword);
        if (dict != null && dict.getSynonyms() != null) {
            return Arrays.asList(dict.getSynonyms().split(","));
        }
        return Collections.singletonList(keyword);
    }

    // ============== 私有方法 ==============

    private String normalizeQuery(String query) {
        return query.toLowerCase()
                .replaceAll("[的|了|吗|呢|吧|啊]", "")
                .replaceAll("\\s+", " ")
                .trim();
    }

    private String recognizeIntent(String query) {
        for (Map.Entry<String, Pattern> entry : INTENT_PATTERNS.entrySet()) {
            if (entry.getValue().matcher(query).find()) {
                return entry.getKey();
            }
        }
        return SearchIntentType.DISCOVERY.getCode();
    }

    private Integer calculateIntentConfidence(String query, String intent) {
        // 根据匹配程度计算置信度
        Pattern pattern = INTENT_PATTERNS.get(intent);
        if (pattern != null && pattern.matcher(query).find()) {
            return 85;
        }
        return 60;
    }

    private List<NluParseResponse.ExtractedEntity> extractEntities(String query) {
        List<NluParseResponse.ExtractedEntity> entities = new ArrayList<>();

        // 简单规则匹配 - 地名、店名等
        // 实际项目中使用NLP模型
        if (query.contains("火锅")) {
            NluParseResponse.ExtractedEntity entity = new NluParseResponse.ExtractedEntity();
            entity.setText("火锅");
            entity.setType("CATEGORY");
            entity.setStartPos(query.indexOf("火锅"));
            entity.setEndPos(entity.getStartPos() + 2);
            entity.setNormalizedValue("火锅");
            entity.setConfidence(0.95);
            entities.add(entity);
        }

        return entities;
    }

    private Map<String, Object> extractSlots(String query, List<NluParseResponse.ExtractedEntity> entities) {
        Map<String, Object> slots = new HashMap<>();
        slots.put("location", extractLocation(query));
        slots.put("price_range", extractPriceRange(query));
        slots.put("entities", entities);
        return slots;
    }

    private String extractLocation(String query) {
        // 提取位置信息
        if (query.contains("附近")) return "nearby";
        if (query.contains("周边")) return "around";
        return null;
    }

    private String extractPriceRange(String query) {
        // 提取价格范围
        if (query.contains("便宜")) return "low";
        if (query.contains("贵") || query.contains("高档")) return "high";
        return "medium";
    }

    private String generateClarificationPrompt(String intent) {
        switch (intent) {
            case "NAVIGATION":
                return "请问您要导航到哪里？";
            case "PRICE_QUERY":
                return "请问您想了解哪个商家的价格？";
            default:
                return "请问您能描述得更详细一些吗？";
        }
    }

    private String analyzeSentiment(String query) {
        if (query.contains("好吃") || query.contains("推荐") || query.contains("不错")) {
            return "POSITIVE";
        }
        if (query.contains("难吃") || query.contains("差") || query.contains("坑")) {
            return "NEGATIVE";
        }
        return "NEUTRAL";
    }

    private List<SemanticSearchResponse.SearchResultItem> performSearch(
            NluParseResponse nluResult, Double lat, Double lng, Integer radius,
            String sortBy, Integer pageNum, Integer pageSize) {

        // 模拟搜索结果 - 实际项目中查询ES或数据库
        List<SemanticSearchResponse.SearchResultItem> results = new ArrayList<>();

        for (int i = 0; i < pageSize; i++) {
            SemanticSearchResponse.SearchResultItem item = new SemanticSearchResponse.SearchResultItem();
            item.setPoiId((long) (i + 1));
            item.setName("示例商户" + (i + 1));
            item.setCategory("美食");
            item.setAddress("示例地址" + (i + 1));
            item.setDistance(500 + i * 100);
            item.setRating(new BigDecimal("4.5"));
            item.setAvgPrice(80 + i * 10);
            item.setTags(Arrays.asList("热门", "推荐"));
            item.setBusinessHours("09:00-22:00");
            item.setIsOpen(true);
            item.setHotScore(85.0 - i * 2);
            item.setRelevanceScore(90.0 - i * 3);
            results.add(item);
        }

        return results;
    }

    private List<String> generateRelatedQueries(NluParseResponse nluResult) {
        List<String> related = new ArrayList<>();
        related.add(nluResult.getNormalizedQuery() + " 推荐");
        related.add("附近好吃的" + nluResult.getEntities().stream()
                .findFirst().map(NluParseResponse.ExtractedEntity::getText).orElse(""));
        return related;
    }

    private void saveSearchLog(SemanticSearchRequest request, SemanticSearchResponse response) {
        SearchQueryLog log = new SearchQueryLog();
        log.setUserId(request.getUserId());
        log.setOriginalQuery(request.getQuery());
        log.setNormalizedQuery(response.getCorrectedQuery());
        log.setIntentType(response.getIntentType());
        log.setIntentConfidence(response.getIntentConfidence());
        log.setSessionId(request.getSessionId());
        log.setIsMultiTurn(response.getIsMultiTurn());
        log.setUserLat(request.getLat());
        log.setUserLng(request.getLng());
        log.setResultCount(response.getResults().size());
        log.setResponseTime(response.getResponseTime());
        log.setSearchSource(request.getIsVoice() ? "VOICE" : "TEXT");
        log.setSearchTime(LocalDateTime.now());
        queryLogMapper.insert(log);
    }

    private String getTrendIcon(String trend) {
        switch (trend) {
            case "UP":
                return "🔥";
            case "DOWN":
                return "📉";
            case "NEW":
                return "🆕";
            case "EXPLOSIVE":
                return "💥";
            default:
                return "➡️";
        }
    }

    private Map<String, Object> parseJsonToMap(String json) {
        // 简化实现 - 实际使用JSON解析库
        return new HashMap<>();
    }

    // 内部类表示搜索结果
    private static class SearchResult {
    }
}
