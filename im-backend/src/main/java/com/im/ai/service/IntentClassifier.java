package com.im.ai.service;

import com.im.ai.model.*;
import com.im.nlp.client.NlpClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 意图分类器
 * 使用规则+模型的混合方式识别用户意图
 */
@Slf4j
@Component
public class IntentClassifier {

    @Autowired
    private NlpClient nlpClient;

    // 规则模式定义
    private final Map<IntentType, List<Pattern>> intentPatterns = new EnumMap<>(IntentType.class);
    private final Map<IntentType, List<String>> intentKeywords = new EnumMap<>(IntentType.class);

    public IntentClassifier() {
        initPatterns();
        initKeywords();
    }

    /**
     * 初始化正则模式
     */
    private void initPatterns() {
        // 问候语模式
        intentPatterns.put(IntentType.GREETING, Arrays.asList(
            Pattern.compile("^(你好|您好|嗨|哈喽|hello|hi|hey|在吗|在不在)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^(早上好|下午好|晚上好|早安|午安|晚安)")
        ));

        // 告别语模式
        intentPatterns.put(IntentType.GOODBYE, Arrays.asList(
            Pattern.compile("(再见|拜拜|bye|goodbye|回头见|明天见|下次见|我先走了)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^(退出|关闭|结束对话)$")
        ));

        // 帮助请求模式
        intentPatterns.put(IntentType.HELP, Arrays.asList(
            Pattern.compile("(帮助|help|怎么用|如何使用|教程|指南|说明|documentation)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("^(你能做什么|你会什么|有什么功能|可以帮我什么)")
        ));

        // 感谢模式
        intentPatterns.put(IntentType.THANKS, Arrays.asList(
            Pattern.compile("(谢谢|感谢|thank|thx|多谢|谢了|麻烦你了)", Pattern.CASE_INSENSITIVE)
        ));

        // 道歉模式
        intentPatterns.put(IntentType.APOLOGY, Arrays.asList(
            Pattern.compile("(对不起|抱歉|不好意思|抱歉打扰|我的错|我错了)")
        ));

        // 任务执行模式 - 设置提醒
        intentPatterns.put(IntentType.TASK_EXECUTION, Arrays.asList(
            Pattern.compile("(设置|添加|创建).{0,5}(提醒|闹钟|备忘|日程)"),
            Pattern.compile("(提醒我|叫我|提示我).{0,10}(在|等到|当)"),
            Pattern.compile("(明天|后天|每天|每周).{0,5}(提醒|叫我)")
        ));

        // 知识查询模式
        intentPatterns.put(IntentType.KNOWLEDGE_QUERY, Arrays.asList(
            Pattern.compile("(什么是|什么是|怎么|如何|为什么|请问|告诉我关于|介绍一下)", Pattern.CASE_INSENSITIVE),
            Pattern.compile("(查询|查找|搜索|查看).{0,3}(信息|资料|数据|记录)")
        ));

        // 否定/拒绝模式
        intentPatterns.put(IntentType.NEGATION, Arrays.asList(
            Pattern.compile("^(不|不用|不要|不需要|算了|取消|否)$"),
            Pattern.compile("(不对|错误|错了|不是这样的|不是这个意思)")
        ));

        // 确认/肯定模式
        intentPatterns.put(IntentType.CONFIRMATION, Arrays.asList(
            Pattern.compile("^(是|是的|对|没错|好的|好|行|可以|确定|确认)$"),
            Pattern.compile("(就是这样|没错|对的|是的|没有问题)")
        ));
    }

    /**
     * 初始化关键词
     */
    private void initKeywords() {
        intentKeywords.put(IntentType.GREETING, Arrays.asList(
            "你好", "您好", "嗨", "哈喽", "hello", "hi", "在吗", "在"
        ));

        intentKeywords.put(IntentType.GOODBYE, Arrays.asList(
            "再见", "拜拜", "bye", "goodbye", "回头见", "走了", "退出", "关闭"
        ));

        intentKeywords.put(IntentType.HELP, Arrays.asList(
            "帮助", "help", "怎么用", "使用", "教程", "指南", "说明", "功能"
        ));

        intentKeywords.put(IntentType.TASK_EXECUTION, Arrays.asList(
            "提醒", "闹钟", "备忘", "日程", "预约", "安排", "任务"
        ));

        intentKeywords.put(IntentType.KNOWLEDGE_QUERY, Arrays.asList(
            "是什么", "什么是", "怎么", "如何", "为什么", "查询", "查找", "搜索"
        ));

        intentKeywords.put(IntentType.CONVERSATIONAL, Arrays.asList(
            "聊天", "说话", "聊聊", "谈谈", "心情", "感受", "想法"
        ));
    }

    /**
     * 执行意图分类
     */
    public IntentResult classify(String message, List<AiMessage> context) {
        // 步骤1: 使用规则匹配快速分类
        IntentResult ruleResult = classifyByRules(message);
        if (ruleResult != null && ruleResult.getConfidence() > 0.8) {
            log.debug("规则分类结果: {}, 置信度: {}", ruleResult.getIntent(), ruleResult.getConfidence());
            return ruleResult;
        }

        // 步骤2: 使用NLP模型分类
        IntentResult modelResult = classifyByModel(message, context);
        
        // 步骤3: 融合结果
        return mergeResults(ruleResult, modelResult);
    }

    /**
     * 基于规则的分类
     */
    private IntentResult classifyByRules(String message) {
        String normalizedMsg = message.trim().toLowerCase();
        Map<IntentType, Double> scores = new EnumMap<>(IntentType.class);

        // 正则匹配
        for (Map.Entry<IntentType, List<Pattern>> entry : intentPatterns.entrySet()) {
            double maxScore = 0;
            for (Pattern pattern : entry.getValue()) {
                if (pattern.matcher(message).find()) {
                    maxScore = Math.max(maxScore, 0.9);
                }
            }
            if (maxScore > 0) {
                scores.put(entry.getKey(), maxScore);
            }
        }

        // 关键词匹配
        for (Map.Entry<IntentType, List<String>> entry : intentKeywords.entrySet()) {
            double score = 0;
            for (String keyword : entry.getValue()) {
                if (normalizedMsg.contains(keyword.toLowerCase())) {
                    score += 0.3;
                }
            }
            if (score > 0) {
                scores.merge(entry.getKey(), Math.min(score, 0.8), Math::max);
            }
        }

        // 选择最高分
        return scores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(e -> IntentResult.builder()
                .intent(e.getKey())
                .confidence(e.getValue())
                .source("rules")
                .parameters(extractParameters(e.getKey(), message))
                .build())
            .orElse(null);
    }

    /**
     * 基于NLP模型的分类
     */
    private IntentResult classifyByModel(String message, List<AiMessage> context) {
        try {
            // 调用NLP服务进行意图分类
            NlpIntentResult nlpResult = nlpClient.classifyIntent(message);
            
            IntentType intentType = mapNlpIntent(nlpResult.getIntent());
            
            return IntentResult.builder()
                .intent(intentType)
                .confidence(nlpResult.getConfidence())
                .source("model")
                .parameters(nlpResult.getParameters())
                .build();
                
        } catch (Exception e) {
            log.warn("NLP模型分类失败,使用默认意图", e);
            return IntentResult.builder()
                .intent(IntentType.CONVERSATIONAL)
                .confidence(0.5)
                .source("fallback")
                .build();
        }
    }

    /**
     * 融合规则和模型的结果
     */
    private IntentResult mergeResults(IntentResult ruleResult, IntentResult modelResult) {
        if (ruleResult == null) {
            return modelResult;
        }
        if (modelResult == null) {
            return ruleResult;
        }

        // 如果两者意图一致,提高置信度
        if (ruleResult.getIntent() == modelResult.getIntent()) {
            double mergedConfidence = Math.min(
                ruleResult.getConfidence() * 0.4 + modelResult.getConfidence() * 0.6 + 0.1, 
                1.0
            );
            
            Map<String, String> mergedParams = new HashMap<>();
            mergedParams.putAll(ruleResult.getParameters());
            mergedParams.putAll(modelResult.getParameters());
            
            return IntentResult.builder()
                .intent(ruleResult.getIntent())
                .confidence(mergedConfidence)
                .source("merged")
                .parameters(mergedParams)
                .build();
        }

        // 如果置信度差距大,选择高置信度的
        if (Math.abs(ruleResult.getConfidence() - modelResult.getConfidence()) > 0.3) {
            return ruleResult.getConfidence() > modelResult.getConfidence() 
                ? ruleResult : modelResult;
        }

        // 置信度接近时,优先使用模型结果
        return modelResult;
    }

    /**
     * 提取意图参数
     */
    private Map<String, String> extractParameters(IntentType intent, String message) {
        Map<String, String> params = new HashMap<>();
        
        switch (intent) {
            case TASK_EXECUTION:
                extractTaskParameters(message, params);
                break;
            case KNOWLEDGE_QUERY:
                extractQueryParameters(message, params);
                break;
            case GREETING:
                extractGreetingParameters(message, params);
                break;
            default:
                break;
        }
        
        return params;
    }

    /**
     * 提取任务相关参数
     */
    private void extractTaskParameters(String message, Map<String, String> params) {
        // 提取时间表达式
        String timeExpr = extractTimeExpression(message);
        if (timeExpr != null) {
            params.put("time", timeExpr);
        }

        // 提取任务类型
        if (message.contains("提醒")) {
            params.put("task_type", "set_reminder");
        } else if (message.contains("查询") || message.contains("查看")) {
            params.put("task_type", "query_schedule");
        } else if (message.contains("发送") || message.contains("发消息")) {
            params.put("task_type", "send_message");
        } else if (message.contains("搜索") || message.contains("找")) {
            params.put("task_type", "search_user");
        }

        // 提取内容(提醒内容、消息内容等)
        String content = extractContent(message);
        if (content != null) {
            params.put("content", content);
        }

        // 提取目标用户
        String targetUser = extractTargetUser(message);
        if (targetUser != null) {
            params.put("target_user", targetUser);
        }

        // 提取消息内容
        String messageContent = extractMessageContent(message);
        if (messageContent != null) {
            params.put("message_content", messageContent);
        }
    }

    /**
     * 提取查询参数
     */
    private void extractQueryParameters(String message, Map<String, String> params) {
        // 提取查询主体
        String[] patterns = {
            "什么是(.+?)[？?]",
            "(.+?)是什么[？?]",
            "怎么(.+?)[？?]",
            "如何(.+?)[？?]",
            "为什么(.+?)[？?]",
            "查询(.+?)[的信息]?",
            "查找(.+?)[的信息]?"
        };

        for (String pattern : patterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(message);
            if (m.find()) {
                params.put("query_subject", m.group(1).trim());
                break;
            }
        }

        params.put("query", message);
    }

    /**
     * 提取问候参数
     */
    private void extractGreetingParameters(String message, Map<String, String> params) {
        if (message.contains("早上") || message.contains("早安")) {
            params.put("time_of_day", "morning");
        } else if (message.contains("下午") || message.contains("午安")) {
            params.put("time_of_day", "afternoon");
        } else if (message.contains("晚上") || message.contains("晚安")) {
            params.put("time_of_day", "evening");
        }
    }

    /**
     * 提取时间表达式
     */
    private String extractTimeExpression(String message) {
        // 匹配常见时间表达式
        String[] timePatterns = {
            "(明天|后天|大后天|今天|每天|每周|每月)",
            "(\\d{1,2})[点:：时](\\d{0,2})[分]?",
            "(早上|上午|中午|下午|晚上|凌晨)",
            "(一刻|半|整)"
        };

        for (String pattern : timePatterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(message);
            if (m.find()) {
                return m.group(0);
            }
        }
        return null;
    }

    /**
     * 提取内容
     */
    private String extractContent(String message) {
        // 提取引号中的内容或特定模式后的内容
        String[] contentPatterns = {
            "提醒我(.+)",
            "设置提醒(.+)",
            "叫我(.+)"
        };

        for (String pattern : contentPatterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(message);
            if (m.find()) {
                String content = m.group(1).trim();
                // 移除时间部分
                content = content.replaceAll("(明天|后天|今天|早上|下午|晚上|\\d{1,2}点)", "").trim();
                return content.isEmpty() ? null : content;
            }
        }
        return null;
    }

    /**
     * 提取目标用户
     */
    private String extractTargetUser(String message) {
        String[] patterns = {
            "给(.+?)发",
            "发给(.+?)[的]?",
            "给(.+?)发送",
            "告诉(.+?)[,，]?",
            "通知(.+?)[,，]?"
        };

        for (String pattern : patterns) {
            java.util.regex.Pattern p = java.util.regex.Pattern.compile(pattern);
            java.util.regex.Matcher m = p.matcher(message);
            if (m.find()) {
                return m.group(1).trim();
            }
        }
        return null;
    }

    /**
     * 提取消息内容
     */
    private String extractMessageContent(String message) {
        // 查找引号中的内容
        java.util.regex.Pattern p = java.util.regex.Pattern.compile("[\"\"'](.+?)[\"\"']");
        java.util.regex.Matcher m = p.matcher(message);
        if (m.find()) {
            return m.group(1);
        }
        
        // 查找"说"后面的内容
        p = java.util.regex.Pattern.compile("说[,，:]?(.+)");
        m = p.matcher(message);
        if (m.find()) {
            return m.group(1).trim();
        }
        
        return null;
    }

    /**
     * 映射NLP意图到系统意图
     */
    private IntentType mapNlpIntent(String nlpIntent) {
        Map<String, IntentType> intentMap = new HashMap<>();
        intentMap.put("greeting", IntentType.GREETING);
        intentMap.put("goodbye", IntentType.GOODBYE);
        intentMap.put("help", IntentType.HELP);
        intentMap.put("thanks", IntentType.THANKS);
        intentMap.put("task", IntentType.TASK_EXECUTION);
        intentMap.put("query", IntentType.KNOWLEDGE_QUERY);
        intentMap.put("chat", IntentType.CONVERSATIONAL);
        intentMap.put("confirm", IntentType.CONFIRMATION);
        intentMap.put("deny", IntentType.NEGATION);
        
        return intentMap.getOrDefault(nlpIntent.toLowerCase(), IntentType.CONVERSATIONAL);
    }

    /**
     * 批量分类(用于批量处理)
     */
    public List<IntentResult> batchClassify(List<String> messages) {
        return messages.stream()
            .map(msg -> classify(msg, Collections.emptyList()))
            .collect(Collectors.toList());
    }

    /**
     * 添加自定义规则
     */
    public void addCustomPattern(IntentType intent, String pattern) {
        intentPatterns.computeIfAbsent(intent, k -> new ArrayList<>())
            .add(Pattern.compile(pattern, Pattern.CASE_INSENSITIVE));
    }

    /**
     * 添加自定义关键词
     */
    public void addCustomKeywords(IntentType intent, List<String> keywords) {
        intentKeywords.computeIfAbsent(intent, k -> new ArrayList<>())
            .addAll(keywords);
    }
}
