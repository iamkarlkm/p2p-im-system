package com.im.backend.modules.local.service.impl;

import com.im.backend.modules.local.dto.*;
import com.im.backend.modules.local.service.IntelligentAssistantService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * 智能对话助手服务实现类
 * 提供自然语言POI搜索、智能问答、多轮对话等功能实现
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Slf4j
@Service
public class IntelligentAssistantServiceImpl implements IntelligentAssistantService {

    // 会话上下文缓存（生产环境应使用Redis）
    private final Map<String, List<IntelligentAssistantRequest.DialogContext>> conversationCache = new HashMap<>();
    
    // 意图关键词映射
    private static final Map<String, List<String>> INTENT_KEYWORDS = new HashMap<>();
    
    static {
        INTENT_KEYWORDS.put("SEARCH", Arrays.asList("附近", "推荐", "哪里", "在哪", "有没有", "好吃的", "好玩的"));
        INTENT_KEYWORDS.put("NAVIGATE", Arrays.asList("怎么去", "导航", "路线", "怎么走", "到那里", "带我去"));
        INTENT_KEYWORDS.put("INQUIRE", Arrays.asList("多少钱", "怎么样", "好不好", "营业时间", "电话", "评价"));
        INTENT_KEYWORDS.put("COMPARE", Arrays.asList("哪个好", "对比", "比较", "哪个更好", "A和B"));
        INTENT_KEYWORDS.put("BOOK", Arrays.asList("预约", "预订", "订位", "排队", "取号"));
    }

    @Override
    public IntelligentAssistantResponse processDialog(IntelligentAssistantRequest request) {
        long startTime = System.currentTimeMillis();
        log.info("Processing dialog request: {}", request.getQuery());
        
        try {
            // 1. 意图识别
            IntentRecognitionResult intent = recognizeIntent(request.getQuery(), request.getContextHistory());
            
            // 2. 实体提取
            List<IntelligentAssistantResponse.ExtractedEntity> entities = extractEntities(request.getQuery());
            
            // 3. 执行搜索（如果是搜索意图）
            List<IntelligentAssistantResponse.RecommendedPOI> recommendations = new ArrayList<>();
            if ("SEARCH".equals(intent.getType()) || "INQUIRE".equals(intent.getType())) {
                recommendations = performPOISearch(request, intent);
            }
            
            // 4. 生成自然语言回复
            String naturalReply = generateNaturalReply(intent, recommendations);
            
            // 5. 构建响应
            IntelligentAssistantResponse.SearchIntentInfo intentInfo = IntelligentAssistantResponse.SearchIntentInfo.builder()
                    .type(intent.getType())
                    .confidence(intent.getConfidence())
                    .entities(entities)
                    .category(intent.getCategory())
                    .filters(intent.getFilters())
                    .build();
            
            IntelligentAssistantResponse response = IntelligentAssistantResponse.builder()
                    .responseId("resp_" + System.currentTimeMillis())
                    .conversationId(request.getConversationId())
                    .intent(intentInfo)
                    .naturalReply(naturalReply)
                    .recommendations(recommendations)
                    .relatedQA(generateRelatedQA(intent, recommendations))
                    .needClarification(false)
                    .suggestedActions(generateSuggestedActions(recommendations.isEmpty() ? null : recommendations.get(0)))
                    .processTimeMs(System.currentTimeMillis() - startTime)
                    .build();
            
            // 6. 更新对话上下文
            updateConversationContext(request.getConversationId(), request.getQuery(), naturalReply);
            
            log.info("Dialog processed successfully in {}ms", response.getProcessTimeMs());
            return response;
            
        } catch (Exception e) {
            log.error("Error processing dialog: {}", e.getMessage(), e);
            return buildErrorResponse(request.getConversationId());
        }
    }

    @Override
    public CompletableFuture<IntelligentAssistantResponse> processDialogAsync(IntelligentAssistantRequest request) {
        return CompletableFuture.supplyAsync(() -> processDialog(request));
    }

    @Override
    public IntentRecognitionResult recognizeIntent(String query, List<IntelligentAssistantRequest.DialogContext> context) {
        IntentRecognitionResult result = new IntentRecognitionResult();
        
        // 基于关键词的意图识别
        String detectedIntent = "SEARCH";
        double maxConfidence = 0.5;
        
        for (Map.Entry<String, List<String>> entry : INTENT_KEYWORDS.entrySet()) {
            for (String keyword : entry.getValue()) {
                if (query.contains(keyword)) {
                    detectedIntent = entry.getKey();
                    maxConfidence = 0.85;
                    break;
                }
            }
        }
        
        // 根据上下文调整意图
        if (context != null && !context.isEmpty()) {
            String lastIntent = context.get(context.size() - 1).getIntent();
            if (maxConfidence < 0.7 && lastIntent != null) {
                detectedIntent = lastIntent;
                maxConfidence = 0.75;
            }
        }
        
        result.setType(detectedIntent);
        result.setConfidence(maxConfidence);
        result.setCategory(extractCategory(query));
        result.setFilters(extractFilters(query));
        
        return result;
    }

    @Override
    public List<IntelligentAssistantResponse.ExtractedEntity> extractEntities(String query) {
        List<IntelligentAssistantResponse.ExtractedEntity> entities = new ArrayList<>();
        
        // 提取位置实体
        if (query.contains("附近") || query.contains("周边")) {
            entities.add(createEntity("LOCATION", "附近", query.indexOf("附近"), query.indexOf("附近") + 2));
        }
        
        // 提取分类实体（常见分类词）
        String[] categories = {"火锅", "烧烤", "西餐", "日料", "中餐", "咖啡店", "酒吧", "KTV", "电影院", "商场"};
        for (String cat : categories) {
            int index = query.indexOf(cat);
            if (index >= 0) {
                entities.add(createEntity("CATEGORY", cat, index, index + cat.length()));
            }
        }
        
        // 提取价格实体
        if (query.contains("人均") || query.contains("价格")) {
            entities.add(createEntity("PRICE", "价格相关", query.indexOf("人均"), query.indexOf("人均") + 2));
        }
        
        // 提取时间实体
        if (query.contains("今天") || query.contains("明天") || query.contains("周末")) {
            String[] times = {"今天", "明天", "周末"};
            for (String time : times) {
                int index = query.indexOf(time);
                if (index >= 0) {
                    entities.add(createEntity("TIME", time, index, index + time.length()));
                }
            }
        }
        
        return entities;
    }

    @Override
    public String generateNaturalReply(IntentRecognitionResult intent, List<IntelligentAssistantResponse.RecommendedPOI> results) {
        if (results == null || results.isEmpty()) {
            return "抱歉，暂时没有符合条件的结果。您可以尝试扩大搜索范围或调整筛选条件。";
        }
        
        StringBuilder reply = new StringBuilder();
        
        switch (intent.getType()) {
            case "SEARCH":
                reply.append("为您找到").append(results.size()).append("家");
                if (intent.getCategory() != null) {
                    reply.append(intent.getCategory());
                }
                reply.append("推荐：");
                break;
            case "NAVIGATE":
                reply.append("已为您规划路线，距离最近的").append(results.get(0).getName())
                     .append("约").append(results.get(0).getDistance()).append("米。");
                break;
            case "INQUIRE":
                reply.append("关于").append(results.get(0).getName()).append("：");
                reply.append("评分").append(results.get(0).getRating()).append("分，");
                reply.append("人均").append(results.get(0).getAvgPrice()).append("元，");
                reply.append("营业时间").append(results.get(0).getBusinessHours()).append("。");
                break;
            default:
                reply.append("以下是为您推荐的结果：");
        }
        
        return reply.toString();
    }

    @Override
    public void updateConversationContext(String conversationId, String userQuery, String assistantReply) {
        if (conversationId == null) {
            return;
        }
        
        List<IntelligentAssistantRequest.DialogContext> context = conversationCache.getOrDefault(conversationId, new ArrayList<>());
        
        // 添加用户输入
        context.add(IntelligentAssistantRequest.DialogContext.builder()
                .role("user")
                .content(userQuery)
                .timestamp(System.currentTimeMillis())
                .build());
        
        // 添加助手回复
        context.add(IntelligentAssistantRequest.DialogContext.builder()
                .role("assistant")
                .content(assistantReply)
                .timestamp(System.currentTimeMillis())
                .build());
        
        // 只保留最近10轮对话
        if (context.size() > 20) {
            context = context.subList(context.size() - 20, context.size());
        }
        
        conversationCache.put(conversationId, context);
    }

    @Override
    public POISemanticSearchResponse semanticSearch(POISemanticSearchRequest request) {
        // 实现语义搜索逻辑
        return POISemanticSearchResponse.builder()
                .searchId("search_" + System.currentTimeMillis())
                .totalCount(0)
                .results(new ArrayList<>())
                .searchTimeMs(50L)
                .zeroResult(true)
                .build();
    }

    @Override
    public String answerPOIQuestion(String poiId, String question) {
        // 实现智能问答逻辑
        return "该商户的详细信息可以通过商户详情页查看。";
    }

    @Override
    public List<IntelligentAssistantResponse.SuggestedAction> generateSuggestedActions(IntelligentAssistantResponse.RecommendedPOI poi) {
        if (poi == null) {
            return new ArrayList<>();
        }
        
        List<IntelligentAssistantResponse.SuggestedAction> actions = new ArrayList<>();
        
        // 导航操作
        Map<String, Object> navigateParams = new HashMap<>();
        navigateParams.put("latitude", poi.getLatitude());
        navigateParams.put("longitude", poi.getLongitude());
        navigateParams.put("name", poi.getName());
        actions.add(IntelligentAssistantResponse.SuggestedAction.builder()
                .type("NAVIGATE")
                .name("导航前往")
                .params(navigateParams)
                .build());
        
        // 电话操作
        Map<String, Object> callParams = new HashMap<>();
        // callParams.put("phone", poi.getPhone());
        actions.add(IntelligentAssistantResponse.SuggestedAction.builder()
                .type("CALL")
                .name("拨打电话")
                .params(callParams)
                .build());
        
        // 预约操作
        Map<String, Object> bookParams = new HashMap<>();
        bookParams.put("poiId", poi.getPoiId());
        actions.add(IntelligentAssistantResponse.SuggestedAction.builder()
                .type("BOOK")
                .name("立即预约")
                .params(bookParams)
                .build());
        
        return actions;
    }
    
    // ==================== 私有辅助方法 ====================
    
    private IntelligentAssistantResponse.ExtractedEntity createEntity(String type, String value, int start, int end) {
        return IntelligentAssistantResponse.ExtractedEntity.builder()
                .type(type)
                .value(value)
                .startPos(start)
                .endPos(end)
                .build();
    }
    
    private String extractCategory(String query) {
        String[] categories = {"火锅", "烧烤", "西餐", "日料", "韩料", "中餐", "川菜", "粤菜", "咖啡店", "酒吧", "KTV", "电影院"};
        for (String cat : categories) {
            if (query.contains(cat)) {
                return cat;
            }
        }
        return null;
    }
    
    private IntelligentAssistantResponse.FilterConditions extractFilters(String query) {
        IntelligentAssistantResponse.FilterConditions filters = new IntelligentAssistantResponse.FilterConditions();
        
        // 距离筛选
        if (query.contains("近") || query.contains("附近")) {
            filters.setDistance(3000);
        } else if (query.contains("远") || query.contains("远点")) {
            filters.setDistance(10000);
        }
        
        // 价格筛选
        if (query.contains("便宜") || query.contains("实惠")) {
            filters.setPriceRange("0-100");
        } else if (query.contains("高档") || query.contains("高端")) {
            filters.setPriceRange("300+");
        }
        
        // 评分筛选
        if (query.contains("高分") || query.contains("好评")) {
            filters.setMinRating(4.5);
        }
        
        filters.setSortBy("relevance");
        return filters;
    }
    
    private List<IntelligentAssistantResponse.RecommendedPOI> performPOISearch(
            IntelligentAssistantRequest request, IntentRecognitionResult intent) {
        // 模拟搜索结果
        List<IntelligentAssistantResponse.RecommendedPOI> results = new ArrayList<>();
        
        // 添加模拟数据
        results.add(IntelligentAssistantResponse.RecommendedPOI.builder()
                .poiId("poi_001")
                .name("海底捞火锅（陆家嘴店）")
                .address("上海市浦东新区陆家嘴环路1000号")
                .distance(1200)
                .rating(4.8)
                .avgPrice(150)
                .category("火锅")
                .businessHours("10:00-02:00")
                .isOpen(true)
                .recommendReason("距离最近的高分火锅店，服务热情")
                .tags(Arrays.asList("网红店", "服务热情", "24小时"))
                .build());
        
        results.add(IntelligentAssistantResponse.RecommendedPOI.builder()
                .poiId("poi_002")
                .name("呷哺呷哺（八佰伴店）")
                .address("上海市浦东新区张杨路501号")
                .distance(800)
                .rating(4.5)
                .avgPrice(80)
                .category("火锅")
                .businessHours("10:00-22:00")
                .isOpen(true)
                .recommendReason("性价比高，适合快餐式火锅")
                .tags(Arrays.asList("实惠", "快捷"))
                .build());
        
        results.add(IntelligentAssistantResponse.RecommendedPOI.builder()
                .poiId("poi_003")
                .name("凑凑火锅（世纪汇店）")
                .address("上海市浦东新区世纪大道1192号")
                .distance(2100)
                .rating(4.7)
                .avgPrice(180)
                .category("火锅")
                .businessHours("11:00-24:00")
                .isOpen(true)
                .recommendReason("台式火锅，奶茶很有名")
                .tags(Arrays.asList("台式火锅", "奶茶", "环境好"))
                .build());
        
        return results;
    }
    
    private List<IntelligentAssistantResponse.QAItem> generateRelatedQA(
            IntentRecognitionResult intent, List<IntelligentAssistantResponse.RecommendedPOI> results) {
        List<IntelligentAssistantResponse.QAItem> qaList = new ArrayList<>();
        
        if (!results.isEmpty()) {
            qaList.add(IntelligentAssistantResponse.QAItem.builder()
                    .question("这家店的人均消费是多少？")
                    .answer(results.get(0).getName() + "的人均消费约" + results.get(0).getAvgPrice() + "元")
                    .source("商户信息")
                    .build());
            
            qaList.add(IntelligentAssistantResponse.QAItem.builder()
                    .question("现在需要排队吗？")
                    .answer("可以通过商户详情页查看实时排队情况")
                    .source("系统推荐")
                    .build());
        }
        
        return qaList;
    }
    
    private IntelligentAssistantResponse buildErrorResponse(String conversationId) {
        return IntelligentAssistantResponse.builder()
                .responseId("resp_" + System.currentTimeMillis())
                .conversationId(conversationId)
                .naturalReply("抱歉，处理您的请求时出现了问题，请稍后重试。")
                .recommendations(new ArrayList<>())
                .needClarification(true)
                .clarificationQuestion("您可以尝试用其他方式描述您的需求吗？")
                .processTimeMs(0L)
                .build();
    }
}
