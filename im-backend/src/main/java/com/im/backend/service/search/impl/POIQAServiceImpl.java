package com.im.backend.service.search.impl;

import com.im.backend.dto.search.POIQADTO;
import com.im.backend.service.search.POIQAService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Pattern;

/**
 * POI问答服务实现类
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
@Slf4j
@Service
public class POIQAServiceImpl implements POIQAService {
    
    // 问题类型匹配模式
    private static final Map<String, List<Pattern>> QUESTION_PATTERNS = new HashMap<>();
    
    static {
        QUESTION_PATTERNS.put("BUSINESS_HOURS", Arrays.asList(
            Pattern.compile("几点开门|几点关门|营业时间|什么时候开门|什么时候关门|几点开始营业|几点结束营业|今天营业吗|周末营业吗"),
            Pattern.compile("open|close|hours|营业")
        ));
        
        QUESTION_PATTERNS.put("PRICE_RANGE", Arrays.asList(
            Pattern.compile("多少钱|人均|价格|贵不贵|便宜|消费|价位|大概多少钱"),
            Pattern.compile("price|cost|how much|人均")
        ));
        
        QUESTION_PATTERNS.put("SPECIALTY", Arrays.asList(
            Pattern.compile("特色|招牌|推荐|什么好吃|必点|招牌菜|拿手菜|特色菜"),
            Pattern.compile("specialty|signature|recommend|best")
        ));
        
        QUESTION_PATTERNS.put("PARKING", Arrays.asList(
            Pattern.compile("停车|停车场|好停车吗|停车位|停车费|附近停车"),
            Pattern.compile("parking|park|停车")
        ));
        
        QUESTION_PATTERNS.put("QUEUE", Arrays.asList(
            Pattern.compile("排队|要等多久|人多吗|需要排队吗|现在人多吗|排队久吗"),
            Pattern.compile("queue|wait|line|排队")
        ));
        
        QUESTION_PATTERNS.put("LOCATION", Arrays.asList(
            Pattern.compile("在哪里|地址|怎么去|怎么走|位置|在哪|方位"),
            Pattern.compile("where|location|address|怎么去")
        ));
        
        QUESTION_PATTERNS.put("CONTACT", Arrays.asList(
            Pattern.compile("电话|联系方式|怎么联系|预订电话|联系电话"),
            Pattern.compile("phone|contact|call|电话")
        ));
        
        QUESTION_PATTERNS.put("COMPARISON", Arrays.asList(
            Pattern.compile("哪个好|哪个更好|和.*比|比较|对比|有什么区别"),
            Pattern.compile("better|compare|vs|比较")
        ));
    }
    
    @Override
    public POIQADTO.Response answerQuestion(POIQADTO dto) {
        long startTime = System.currentTimeMillis();
        
        log.info("POI QA - poiId: {}, question: {}", dto.getPoiId(), dto.getQuestion());
        
        // 1. 识别问题类型
        String questionType = recognizeQuestionType(dto.getQuestion());
        
        // 2. 生成回答
        String answer = generateAnswer(dto.getPoiId(), questionType, dto.getQuestion());
        
        // 3. 计算置信度
        double confidence = calculateConfidence(dto.getQuestion(), answer, questionType);
        
        // 4. 判断是否需要转人工
        boolean needsTransfer = needsHumanTransfer(dto.getQuestion(), confidence);
        
        // 5. 构建响应
        POIQADTO.Response.ResponseBuilder builder = POIQADTO.Response.builder()
            .status("SUCCESS")
            .sessionId(dto.getSessionId())
            .questionType(questionType)
            .answer(answer)
            .confidence(confidence)
            .isRealTime(isRealTimeQuestion(questionType))
            .needsHumanTransfer(needsTransfer)
            .responseTimeMs(System.currentTimeMillis() - startTime);
        
        // 添加详细答案
        builder.detailedAnswer(buildDetailedAnswer(dto.getPoiId(), questionType));
        
        // 添加建议操作
        builder.suggestedActions(getSuggestedActions(questionType));
        
        // 添加相关问题
        builder.relatedQuestions(getRelatedQuestions(questionType));
        
        // 如果置信度低，添加转人工原因
        if (needsTransfer) {
            builder.transferReason("置信度较低，建议转人工处理");
        }
        
        return builder.build();
    }
    
    @Override
    public String recognizeQuestionType(String question) {
        String normalizedQuestion = question.toLowerCase();
        
        double maxScore = 0;
        String detectedType = "GENERAL";
        
        for (Map.Entry<String, List<Pattern>> entry : QUESTION_PATTERNS.entrySet()) {
            double score = 0;
            for (Pattern pattern : entry.getValue()) {
                if (pattern.matcher(normalizedQuestion).find()) {
                    score += 0.5;
                }
            }
            if (score > maxScore) {
                maxScore = score;
                detectedType = entry.getKey();
            }
        }
        
        return detectedType;
    }
    
    @Override
    public String generateAnswer(Long poiId, String questionType, String question) {
        // 模拟POI数据
        Map<String, Object> poiData = getPOIData(poiId);
        
        return switch (questionType) {
            case "BUSINESS_HOURS" -> generateBusinessHoursAnswer(poiData);
            case "PRICE_RANGE" -> generatePriceAnswer(poiData);
            case "SPECIALTY" -> generateSpecialtyAnswer(poiData);
            case "PARKING" -> generateParkingAnswer(poiData);
            case "QUEUE" -> generateQueueAnswer(poiId);
            case "LOCATION" -> generateLocationAnswer(poiData);
            case "CONTACT" -> generateContactAnswer(poiData);
            case "COMPARISON" -> generateComparisonAnswer(poiId, question);
            default -> generateGeneralAnswer(poiData, question);
        };
    }
    
    @Override
    public String getRealTimeInfo(Long poiId, String infoType) {
        // 模拟实时信息获取
        return switch (infoType) {
            case "QUEUE" -> "当前排队约" + (int) (Math.random() * 20) + "桌，预计等待" + (int) (Math.random() * 30 + 10) + "分钟";
            case "BUSINESS_HOURS" -> "营业中，营业时间 09:00-22:00";
            default -> "暂无实时信息";
        };
    }
    
    @Override
    public boolean needsHumanTransfer(String question, double confidence) {
        // 以下情况需要转人工
        if (confidence < 0.5) return true;
        if (question.contains("投诉") || question.contains("退款") || question.contains("纠纷")) return true;
        if (question.contains("人工") || question.contains("客服")) return true;
        if (question.length() > 100) return true; // 复杂问题
        return false;
    }
    
    // ========== 私有辅助方法 ==========
    
    private double calculateConfidence(String question, String answer, String questionType) {
        double confidence = 0.7;
        
        // 问题越具体，置信度越高
        if (question.length() > 10 && question.length() < 30) {
            confidence += 0.1;
        }
        
        // 有明确答案类型
        if (!"GENERAL".equals(questionType)) {
            confidence += 0.1;
        }
        
        // 答案质量检查
        if (answer != null && answer.length() > 10) {
            confidence += 0.1;
        }
        
        return Math.min(confidence, 0.95);
    }
    
    private boolean isRealTimeQuestion(String questionType) {
        return "QUEUE".equals(questionType) || "BUSINESS_HOURS".equals(questionType);
    }
    
    private Map<String, Object> buildDetailedAnswer(Long poiId, String questionType) {
        Map<String, Object> details = new HashMap<>();
        
        switch (questionType) {
            case "BUSINESS_HOURS" -> {
                details.put("weekdayHours", "09:00-22:00");
                details.put("weekendHours", "10:00-23:00");
                details.put("holidayHours", "10:00-22:00");
                details.put("isOpenNow", true);
            }
            case "PRICE_RANGE" -> {
                details.put("minPrice", 50);
                details.put("maxPrice", 150);
                details.put("avgPrice", 100);
                details.put("currency", "CNY");
            }
            case "SPECIALTY" -> {
                details.put("specialties", Arrays.asList("招牌菜A", "特色菜B", "必点菜C"));
                details.put("recommendationReason", "根据用户好评推荐");
            }
            case "PARKING" -> {
                details.put("hasParking", true);
                details.put("parkingType", "地面+地下停车场");
                details.put("parkingFee", "前2小时免费");
                details.put("parkingSpaces", 200);
            }
        }
        
        return details;
    }
    
    private List<String> getSuggestedActions(String questionType) {
        return switch (questionType) {
            case "BUSINESS_HOURS" -> Arrays.asList("导航到店", "立即预约");
            case "PRICE_RANGE" -> Arrays.asList("查看菜单", "团购优惠");
            case "QUEUE" -> Arrays.asList("在线取号", "查看附近其他店");
            case "SPECIALTY" -> Arrays.asList("查看图片", "查看评价");
            case "LOCATION" -> Arrays.asList("导航", "查看地图");
            case "PARKING" -> Arrays.asList("导航到停车场", "查看周边停车");
            default -> Arrays.asList("查看详情", "收藏店铺");
        };
    }
    
    private List<String> getRelatedQuestions(String questionType) {
        return switch (questionType) {
            case "BUSINESS_HOURS" -> Arrays.asList("周末营业吗？", "节假日营业吗？", "最早几点开门？");
            case "PRICE_RANGE" -> Arrays.asList("有什么优惠活动？", "性价比怎么样？", "有套餐吗？");
            case "SPECIALTY" -> Arrays.asList("推荐菜有哪些？", "招牌菜是什么？", "必点菜品？");
            case "QUEUE" -> Arrays.asList("现在排队人多吗？", "可以预约吗？", "有包间吗？");
            case "PARKING" -> Arrays.asList("停车费多少钱？", "停车位多吗？", "好停车吗？");
            default -> Arrays.asList();
        };
    }
    
    // 模拟POI数据
    private Map<String, Object> getPOIData(Long poiId) {
        Map<String, Object> data = new HashMap<>();
        data.put("id", poiId);
        data.put("name", "示例店铺");
        data.put("rating", 4.5);
        data.put("reviewCount", 1234);
        data.put("address", "示例地址");
        data.put("phone", "400-123-4567");
        data.put("businessHours", "09:00-22:00");
        data.put("avgPrice", 100);
        return data;
    }
    
    private String generateBusinessHoursAnswer(Map<String, Object> poiData) {
        return "我们的营业时间是每天 09:00-22:00，周末延长至 23:00。" +
               "当前营业中，欢迎您随时光临！";
    }
    
    private String generatePriceAnswer(Map<String, Object> poiData) {
        int avgPrice = (int) poiData.getOrDefault("avgPrice", 100);
        return "我们店的人均消费约" + avgPrice + "元左右，" +
               "性价比高，还有各种优惠活动可以参与。";
    }
    
    private String generateSpecialtyAnswer(Map<String, Object> poiData) {
        return "我们的招牌菜有：特色菜A、招牌菜B、必点菜C。" +
               "这些都是客人评价最高的菜品，强烈推荐您尝试！";
    }
    
    private String generateParkingAnswer(Map<String, Object> poiData) {
        return "店附近有充足的停车位，包括地面和地下停车场。" +
               "前2小时免费停车，之后每小时5元。";
    }
    
    private String generateQueueAnswer(Long poiId) {
        return getRealTimeInfo(poiId, "QUEUE");
    }
    
    private String generateLocationAnswer(Map<String, Object> poiData) {
        return "我们位于" + poiData.get("address") + "，" +
               "交通便利，地铁X号线Y出口步行5分钟即到。";
    }
    
    private String generateContactAnswer(Map<String, Object> poiData) {
        return "预订电话：" + poiData.get("phone") + "，" +
               "营业时间随时接听，期待您的来电！";
    }
    
    private String generateComparisonAnswer(Long poiId, String question) {
        return "每家店都有自己的特色，我们店的特点是..." +
               "建议您根据个人喜好选择。";
    }
    
    private String generateGeneralAnswer(Map<String, Object> poiData, String question) {
        return "感谢您的问题！我们店是" + poiData.get("name") + "，" +
               "评分" + poiData.get("rating") + "分，" +
               "欢迎您的光临！";
    }
}
