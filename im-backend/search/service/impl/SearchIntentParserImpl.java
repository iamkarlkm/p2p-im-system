package com.im.search.service.impl;

import com.im.search.dto.SearchIntentDTO;
import com.im.search.service.SearchIntentParser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 搜索意图解析服务实现
 * 基于规则和NLP的意图识别
 */
@Slf4j
@Service
public class SearchIntentParserImpl implements SearchIntentParser {

    // 附近搜索模式
    private static final Pattern NEARBY_PATTERN = Pattern.compile("(附近|周边|旁边|周围|就近)");
    private static final Pattern NAVIGATION_PATTERN = Pattern.compile("(怎么去|导航|路线|怎么走|到.*怎么走)");
    private static final Pattern PRICE_PATTERN = Pattern.compile("(人均|价格|多少钱|便宜|贵)\\s*(\\d+)");
    private static final Pattern DISTANCE_PATTERN = Pattern.compile("(\\d+)\\s*(米|m|公里|km)");
    private static final Pattern CATEGORY_PATTERN = Pattern.compile(
            "(餐厅|美食|火锅|烧烤|日料|西餐|中餐|小吃|咖啡馆|酒吧|KTV|电影院|商场|超市|便利店|" +
            "公园|景点|酒店|医院|银行|加油站|停车场|厕所)"
    );
    private static final Pattern SCENE_PATTERN = Pattern.compile(
            "(适合带娃|遛娃|亲子|约会|聚会|商务|请客|一个人|情侣|朋友)"
    );
    private static final Pattern TIME_PATTERN = Pattern.compile("(24小时|通宵|深夜|早餐|午餐|晚餐|夜宵|周末)");
    
    // 分类映射
    private static final java.util.Map<String, String> CATEGORY_MAP = new java.util.HashMap<>();
    static {
        CATEGORY_MAP.put("餐厅", "餐饮");
        CATEGORY_MAP.put("美食", "餐饮");
        CATEGORY_MAP.put("火锅", "餐饮");
        CATEGORY_MAP.put("烧烤", "餐饮");
        CATEGORY_MAP.put("日料", "餐饮");
        CATEGORY_MAP.put("西餐", "餐饮");
        CATEGORY_MAP.put("中餐", "餐饮");
        CATEGORY_MAP.put("小吃", "餐饮");
        CATEGORY_MAP.put("咖啡馆", "餐饮");
        CATEGORY_MAP.put("酒吧", "娱乐");
        CATEGORY_MAP.put("KTV", "娱乐");
        CATEGORY_MAP.put("电影院", "娱乐");
        CATEGORY_MAP.put("商场", "购物");
        CATEGORY_MAP.put("超市", "购物");
        CATEGORY_MAP.put("便利店", "购物");
        CATEGORY_MAP.put("公园", "景点");
        CATEGORY_MAP.put("景点", "景点");
        CATEGORY_MAP.put("酒店", "酒店");
        CATEGORY_MAP.put("医院", "医疗");
        CATEGORY_MAP.put("银行", "金融");
        CATEGORY_MAP.put("加油站", "汽车");
        CATEGORY_MAP.put("停车场", "汽车");
    }

    @Override
    public SearchIntentDTO parse(String query, String sessionId) {
        log.debug("Parsing search intent for query: {}, session: {}", query, sessionId);
        
        SearchIntentDTO.SearchIntentDTOBuilder builder = SearchIntentDTO.builder()
                .originalQuery(query)
                .entities(new ArrayList<>());
        
        // 1. 意图识别
        SearchIntentDTO.IntentType intentType = recognizeIntent(query);
        builder.intentType(intentType);
        builder.confidence(0.85f);
        
        // 2. 实体提取
        List<SearchIntentDTO.SearchEntityDTO> entities = extractEntities(query);
        builder.entities(entities);
        
        // 3. 构建结构化查询
        SearchIntentDTO.StructuredQueryDTO structuredQuery = buildStructuredQuery(entities, query);
        builder.structuredQuery(structuredQuery);
        
        // 4. 生成搜索建议
        builder.searchSuggestions(generateSuggestions(query, entities));
        
        return builder.build();
    }
    
    /**
     * 识别意图类型
     */
    private SearchIntentDTO.IntentType recognizeIntent(String query) {
        if (NAVIGATION_PATTERN.matcher(query).find()) {
            return SearchIntentDTO.IntentType.NAVIGATION;
        }
        if (query.contains("预约") || query.contains("订位") || query.contains("排队")) {
            return SearchIntentDTO.IntentType.RESERVATION;
        }
        if (query.contains("团购") || query.contains("优惠") || query.contains("券")) {
            return SearchIntentDTO.IntentType.GROUP_BUYING;
        }
        if (query.contains("哪个好") || query.contains("对比") || query.contains("比较")) {
            return SearchIntentDTO.IntentType.PRICE_COMPARISON;
        }
        if (query.contains("推荐") || query.contains("有什么")) {
            return SearchIntentDTO.IntentType.RECOMMENDATION;
        }
        if (NEARBY_PATTERN.matcher(query).find() || query.contains("附近")) {
            return SearchIntentDTO.IntentType.NEARBY_SEARCH;
        }
        if (query.contains("营业时间") || query.contains("电话") || query.contains("怎么样")) {
            return SearchIntentDTO.IntentType.QA;
        }
        return SearchIntentDTO.IntentType.NEARBY_SEARCH;
    }
    
    /**
     * 提取实体
     */
    private List<SearchIntentDTO.SearchEntityDTO> extractEntities(String query) {
        List<SearchIntentDTO.SearchEntityDTO> entities = new ArrayList<>();
        
        // 提取分类实体
        Matcher categoryMatcher = CATEGORY_PATTERN.matcher(query);
        while (categoryMatcher.find()) {
            entities.add(SearchIntentDTO.SearchEntityDTO.builder()
                    .type(SearchIntentDTO.EntityType.CATEGORY)
                    .value(CATEGORY_MAP.getOrDefault(categoryMatcher.group(), categoryMatcher.group()))
                    .rawText(categoryMatcher.group())
                    .start(categoryMatcher.start())
                    .end(categoryMatcher.end())
                    .build());
        }
        
        // 提取价格实体
        Matcher priceMatcher = PRICE_PATTERN.matcher(query);
        while (priceMatcher.find()) {
            entities.add(SearchIntentDTO.SearchEntityDTO.builder()
                    .type(SearchIntentDTO.EntityType.PRICE)
                    .value(priceMatcher.group(2))
                    .rawText(priceMatcher.group())
                    .start(priceMatcher.start())
                    .end(priceMatcher.end())
                    .attributes(java.util.Map.of("value", Integer.parseInt(priceMatcher.group(2))))
                    .build());
        }
        
        // 提取距离实体
        Matcher distanceMatcher = DISTANCE_PATTERN.matcher(query);
        while (distanceMatcher.find()) {
            int value = Integer.parseInt(distanceMatcher.group(1));
            String unit = distanceMatcher.group(2);
            int meters = unit.contains("公里") || unit.equals("km") ? value * 1000 : value;
            
            entities.add(SearchIntentDTO.SearchEntityDTO.builder()
                    .type(SearchIntentDTO.EntityType.DISTANCE)
                    .value(String.valueOf(meters))
                    .rawText(distanceMatcher.group())
                    .start(distanceMatcher.start())
                    .end(distanceMatcher.end())
                    .attributes(java.util.Map.of("meters", meters))
                    .build());
        }
        
        // 提取场景实体
        Matcher sceneMatcher = SCENE_PATTERN.matcher(query);
        while (sceneMatcher.find()) {
            entities.add(SearchIntentDTO.SearchEntityDTO.builder()
                    .type(SearchIntentDTO.EntityType.TAG)
                    .value(sceneMatcher.group())
                    .rawText(sceneMatcher.group())
                    .start(sceneMatcher.start())
                    .end(sceneMatcher.end())
                    .build());
        }
        
        // 提取时间实体
        Matcher timeMatcher = TIME_PATTERN.matcher(query);
        while (timeMatcher.find()) {
            entities.add(SearchIntentDTO.SearchEntityDTO.builder()
                    .type(SearchIntentDTO.EntityType.TIME)
                    .value(timeMatcher.group())
                    .rawText(timeMatcher.group())
                    .start(timeMatcher.start())
                    .end(timeMatcher.end())
                    .build());
        }
        
        return entities;
    }
    
    /**
     * 构建结构化查询
     */
    private SearchIntentDTO.StructuredQueryDTO buildStructuredQuery(
            List<SearchIntentDTO.SearchEntityDTO> entities, String query) {
        
        SearchIntentDTO.StructuredQueryDTO.StructuredQueryDTOBuilder builder = 
                SearchIntentDTO.StructuredQueryDTO.builder();
        
        List<String> keywords = new ArrayList<>();
        List<String> categories = new ArrayList<>();
        List<String> tags = new ArrayList<>();
        SearchIntentDTO.LocationConstraintDTO.LocationConstraintDTOBuilder locationBuilder = 
                SearchIntentDTO.LocationConstraintDTO.builder();
        SearchIntentDTO.PriceConstraintDTO.PriceConstraintDTOBuilder priceBuilder = 
                SearchIntentDTO.PriceConstraintDTO.builder();
        
        for (SearchIntentDTO.SearchEntityDTO entity : entities) {
            switch (entity.getType()) {
                case CATEGORY:
                    categories.add(entity.getValue());
                    break;
                case TAG:
                    tags.add(entity.getValue());
                    break;
                case DISTANCE:
                    if (entity.getAttributes() != null && entity.getAttributes().containsKey("meters")) {
                        locationBuilder.radius((Integer) entity.getAttributes().get("meters"));
                    }
                    break;
                case PRICE:
                    if (entity.getAttributes() != null && entity.getAttributes().containsKey("value")) {
                        int price = (Integer) entity.getAttributes().get("value");
                        // 判断是上限还是下限
                        if (query.contains("以下") || query.contains("以内")) {
                            priceBuilder.max(price);
                        } else if (query.contains("以上")) {
                            priceBuilder.min(price);
                        } else {
                            priceBuilder.max(price);
                        }
                    }
                    break;
                default:
                    break;
            }
        }
        
        // 提取关键词（去掉停用词）
        String[] words = query.split("[\\s的之还吗呢啊]+");
        for (String word : words) {
            if (word.length() > 1 && !isStopWord(word)) {
                keywords.add(word);
            }
        }
        
        // 设置默认值
        if (locationBuilder.build().getRadius() == null) {
            locationBuilder.radius(5000); // 默认5公里
        }
        
        builder.keywords(keywords);
        builder.categories(categories);
        builder.tags(tags);
        builder.location(locationBuilder.build());
        builder.price(priceBuilder.build());
        
        return builder.build();
    }
    
    /**
     * 生成搜索建议
     */
    private List<String> generateSuggestions(String query, List<SearchIntentDTO.SearchEntityDTO> entities) {
        List<String> suggestions = new ArrayList<>();
        
        // 基于查询生成建议
        if (query.contains("火锅")) {
            suggestions.add("附近好吃的火锅推荐");
            suggestions.add("人均100以下的火锅店");
            suggestions.add("24小时营业的火锅店");
        } else if (query.contains("公园")) {
            suggestions.add("适合遛娃的公园");
            suggestions.add("可以带狗的公园");
            suggestions.add("有儿童游乐设施的公园");
        } else {
            suggestions.add(query + " 附近");
            suggestions.add("好吃的" + query);
            suggestions.add("适合聚会的" + query);
        }
        
        return suggestions;
    }
    
    /**
     * 判断是否为停用词
     */
    private boolean isStopWord(String word) {
        List<String> stopWords = List.of("附近", "周边", "好的", "推荐", "地方", "有没有", "一下", "什么");
        return stopWords.contains(word);
    }
}
