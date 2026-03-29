package com.im.ai.service;

import com.im.ai.model.*;
import com.im.ai.repository.*;
import com.im.nlp.client.NlpClient;
import com.im.common.model.Message;
import com.im.common.model.UserSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * AI智能助手核心服务
 * 负责对话处理、意图识别、知识库检索、多轮对话管理
 */
@Slf4j
@Service
public class AiAssistantService {

    @Autowired
    private IntentClassifier intentClassifier;
    
    @Autowired
    private KnowledgeBaseService knowledgeBaseService;
    
    @Autowired
    private DialogueManager dialogueManager;
    
    @Autowired
    private ResponseGenerator responseGenerator;
    
    @Autowired
    private AiConversationRepository conversationRepository;
    
    @Autowired
    private KnowledgeBaseRepository knowledgeBaseRepository;
    
    @Autowired
    private NlpClient nlpClient;
    
    @Autowired
    private AiAssistantConfig config;

    private final Map<String, AiSessionContext> sessionContexts = new ConcurrentHashMap<>();

    /**
     * 处理用户消息并返回AI回复
     */
    public AiResponse processMessage(String userId, String sessionId, String message) {
        long startTime = System.currentTimeMillis();
        
        try {
            // 获取或创建会话上下文
            AiSessionContext context = getOrCreateSession(userId, sessionId);
            
            // 保存用户消息
            saveUserMessage(context, message);
            
            // 步骤1: 意图识别
            IntentResult intent = classifyIntent(message, context);
            log.debug("意图识别结果: {}, 置信度: {}", intent.getIntent(), intent.getConfidence());
            
            // 步骤2: 根据意图类型处理
            AiResponse response;
            switch (intent.getIntent()) {
                case KNOWLEDGE_QUERY:
                    response = handleKnowledgeQuery(message, context, intent);
                    break;
                case TASK_EXECUTION:
                    response = handleTaskExecution(message, context, intent);
                    break;
                case CONVERSATIONAL:
                    response = handleConversational(message, context);
                    break;
                case GREETING:
                    response = handleGreeting(context);
                    break;
                case GOODBYE:
                    response = handleGoodbye(context);
                    break;
                case HELP:
                    response = handleHelpRequest(context);
                    break;
                default:
                    response = handleUnknownIntent(message, context);
            }
            
            // 保存AI回复
            saveAiResponse(context, response);
            
            // 更新上下文状态
            updateSessionContext(context, message, response);
            
            // 记录响应时间
            long responseTime = System.currentTimeMillis() - startTime;
            response.setResponseTimeMs(responseTime);
            log.info("AI响应完成, 耗时: {}ms", responseTime);
            
            return response;
            
        } catch (Exception e) {
            log.error("AI处理消息时发生错误", e);
            return buildErrorResponse("抱歉,我遇到了一些问题,请稍后再试。");
        }
    }

    /**
     * 意图识别
     */
    private IntentResult classifyIntent(String message, AiSessionContext context) {
        // 使用NLP服务进行意图分类
        return intentClassifier.classify(message, context.getRecentMessages(5));
    }

    /**
     * 处理知识库查询
     */
    private AiResponse handleKnowledgeQuery(String message, AiSessionContext context, IntentResult intent) {
        // 提取查询关键词
        List<String> keywords = extractKeywords(message);
        
        // 从知识库检索相关内容
        List<KnowledgeEntry> knowledgeEntries = knowledgeBaseService.search(keywords, 5);
        
        if (knowledgeEntries.isEmpty()) {
            return AiResponse.builder()
                .type(ResponseType.TEXT)
                .content("抱歉,我暂时没有找到相关的信息。您可以尝试用其他方式描述您的问题。")
                .suggestions(Arrays.asList("联系人工客服", "查看帮助文档", "提交反馈"))
                .build();
        }
        
        // 使用检索到的知识生成回复
        String responseContent = responseGenerator.generateFromKnowledge(
            message, knowledgeEntries, context
        );
        
        return AiResponse.builder()
            .type(ResponseType.TEXT)
            .content(responseContent)
            .sourceKnowledge(knowledgeEntries)
            .confidence(calculateConfidence(knowledgeEntries))
            .build();
    }

    /**
     * 处理任务执行请求
     */
    private AiResponse handleTaskExecution(String message, AiSessionContext context, IntentResult intent) {
        String taskType = intent.getParameters().getOrDefault("task_type", "unknown");
        
        switch (taskType) {
            case "set_reminder":
                return handleSetReminder(message, context, intent);
            case "query_schedule":
                return handleQuerySchedule(message, context);
            case "send_message":
                return handleSendMessage(message, context, intent);
            case "search_user":
                return handleSearchUser(message, context, intent);
            default:
                return AiResponse.builder()
                    .type(ResponseType.TEXT)
                    .content("我理解您想要执行一个任务,但我不确定具体是什么。能再详细说明一下吗?")
                    .needClarification(true)
                    .build();
        }
    }

    /**
     * 处理日常对话
     */
    private AiResponse handleConversational(String message, AiSessionContext context) {
        // 使用对话管理器生成回复
        String reply = dialogueManager.generateReply(message, context);
        
        return AiResponse.builder()
            .type(ResponseType.TEXT)
            .content(reply)
            .personality(context.getPersonality())
            .build();
    }

    /**
     * 处理问候语
     */
    private AiResponse handleGreeting(AiSessionContext context) {
        String greeting = generatePersonalizedGreeting(context);
        
        List<String> suggestions = Arrays.asList(
            "查询我的日程",
            "最近有什么新消息",
            "帮我设置提醒",
            "查看帮助"
        );
        
        return AiResponse.builder()
            .type(ResponseType.TEXT)
            .content(greeting)
            .suggestions(suggestions)
            .build();
    }

    /**
     * 处理告别语
     */
    private AiResponse handleGoodbye(AiSessionContext context) {
        String farewell = generatePersonalizedFarewell(context);
        
        return AiResponse.builder()
            .type(ResponseType.TEXT)
            .content(farewell)
            .endSession(true)
            .build();
    }

    /**
     * 处理帮助请求
     */
    private AiResponse handleHelpRequest(AiSessionContext context) {
        String helpContent = """
            我是您的AI智能助手,可以帮助您:
            
            📚 **知识查询**
            - 询问产品功能、使用方法
            - 查询账户相关问题
            - 获取帮助文档
            
            ⚡ **任务执行**
            - 设置提醒和日程
            - 查询消息和通知
            - 搜索联系人
            
            💬 **日常对话**
            - 闲聊陪伴
            - 问答互动
            - 情感支持
            
            有什么我可以帮您的吗?
            """;
        
        return AiResponse.builder()
            .type(ResponseType.TEXT)
            .content(helpContent)
            .build();
    }

    /**
     * 处理未知意图
     */
    private AiResponse handleUnknownIntent(String message, AiSessionContext context) {
        // 尝试用通用模型生成回复
        String fallbackResponse = responseGenerator.generateFallback(message, context);
        
        return AiResponse.builder()
            .type(ResponseType.TEXT)
            .content(fallbackResponse)
            .lowConfidence(true)
            .suggestions(Arrays.asList(
                "重新描述您的问题",
                "联系人工客服",
                "提交反馈帮助我们改进"
            ))
            .build();
    }

    /**
     * 设置提醒
     */
    private AiResponse handleSetReminder(String message, AiSessionContext context, IntentResult intent) {
        String timeStr = intent.getParameters().get("time");
        String content = intent.getParameters().get("content");
        
        if (timeStr == null || content == null) {
            return AiResponse.builder()
                .type(ResponseType.TEXT)
                .content("请告诉我提醒的时间和内容,例如:\"明天上午9点提醒我开会\"")
                .needClarification(true)
                .build();
        }
        
        // 解析时间
        LocalDateTime reminderTime = parseTimeExpression(timeStr);
        
        // 创建提醒
        Reminder reminder = Reminder.builder()
            .userId(context.getUserId())
            .content(content)
            .remindAt(reminderTime)
            .createdAt(LocalDateTime.now())
            .build();
        
        // 保存提醒
        saveReminder(reminder);
        
        return AiResponse.builder()
            .type(ResponseType.TEXT)
            .content(String.format("已为您设置提醒:\"%s\", 将在 %s 提醒您。", 
                content, formatTime(reminderTime)))
            .actionCompleted(true)
            .build();
    }

    /**
     * 查询日程
     */
    private AiResponse handleQuerySchedule(String message, AiSessionContext context) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime tomorrow = now.plusDays(1);
        
        List<Reminder> reminders = getUserReminders(context.getUserId(), now, tomorrow);
        
        if (reminders.isEmpty()) {
            return AiResponse.builder()
                .type(ResponseType.TEXT)
                .content("您今天没有待办的提醒事项。需要我帮您设置一个吗?")
                .suggestions(Arrays.asList("设置提醒", "查看明天的日程"))
                .build();
        }
        
        StringBuilder sb = new StringBuilder("您今天的日程:\n\n");
        for (int i = 0; i < reminders.size(); i++) {
            Reminder r = reminders.get(i);
            sb.append(String.format("%d. %s - %s\n", i + 1, formatTime(r.getRemindAt()), r.getContent()));
        }
        
        return AiResponse.builder()
            .type(ResponseType.TEXT)
            .content(sb.toString())
            .build();
    }

    /**
     * 处理发送消息请求
     */
    private AiResponse handleSendMessage(String message, AiSessionContext context, IntentResult intent) {
        String targetUser = intent.getParameters().get("target_user");
        String content = intent.getParameters().get("message_content");
        
        if (targetUser == null) {
            return AiResponse.builder()
                .type(ResponseType.TEXT)
                .content("您想要发送消息给谁?请告诉我对方的名称。")
                .needClarification(true)
                .build();
        }
        
        return AiResponse.builder()
            .type(ResponseType.CONFIRMATION)
            .content(String.format("您确定要发送消息给 %s 吗?\n\n消息内容:\"%s\"", targetUser, content))
            .confirmationData(Map.of(
                "action", "send_message",
                "target", targetUser,
                "content", content
            ))
            .build();
    }

    /**
     * 处理搜索用户请求
     */
    private AiResponse handleSearchUser(String message, AiSessionContext context, IntentResult intent) {
        String query = intent.getParameters().getOrDefault("query", message);
        
        // 执行用户搜索
        List<UserSearchResult> results = searchUsers(query, 5);
        
        if (results.isEmpty()) {
            return AiResponse.builder()
                .type(ResponseType.TEXT)
                .content("未找到匹配的用户,请尝试其他关键词。")
                .build();
        }
        
        return AiResponse.builder()
            .type(ResponseType.USER_LIST)
            .content(String.format("找到 %d 位用户:", results.size()))
            .userResults(results)
            .build();
    }

    // ============ 辅助方法 ============

    private AiSessionContext getOrCreateSession(String userId, String sessionId) {
        return sessionContexts.computeIfAbsent(sessionId, id -> {
            AiSessionContext ctx = new AiSessionContext();
            ctx.setUserId(userId);
            ctx.setSessionId(sessionId);
            ctx.setCreatedAt(LocalDateTime.now());
            ctx.setPersonality(config.getDefaultPersonality());
            return ctx;
        });
    }

    private void saveUserMessage(AiSessionContext context, String message) {
        AiMessage msg = AiMessage.builder()
            .role(MessageRole.USER)
            .content(message)
            .timestamp(LocalDateTime.now())
            .build();
        context.addMessage(msg);
    }

    private void saveAiResponse(AiSessionContext context, AiResponse response) {
        AiMessage msg = AiMessage.builder()
            .role(MessageRole.ASSISTANT)
            .content(response.getContent())
            .timestamp(LocalDateTime.now())
            .build();
        context.addMessage(msg);
    }

    private void updateSessionContext(AiSessionContext context, String message, AiResponse response) {
        context.setLastInteractionTime(LocalDateTime.now());
        context.incrementInteractionCount();
        
        // 保存到持久化存储
        conversationRepository.save(context.toEntity());
    }

    private List<String> extractKeywords(String message) {
        // 使用NLP服务提取关键词
        return nlpClient.extractKeywords(message, 5);
    }

    private String generatePersonalizedGreeting(AiSessionContext context) {
        int hour = LocalDateTime.now().getHour();
        String timeGreeting;
        
        if (hour < 12) {
            timeGreeting = "早上好";
        } else if (hour < 18) {
            timeGreeting = "下午好";
        } else {
            timeGreeting = "晚上好";
        }
        
        if (context.getInteractionCount() == 0) {
            return String.format("%s! 我是您的AI助手,很高兴为您服务。有什么可以帮您的吗?", timeGreeting);
        } else {
            return String.format("%s! 欢迎回来。今天有什么我可以帮您的?", timeGreeting);
        }
    }

    private String generatePersonalizedFarewell(AiSessionContext context) {
        String[] farewells = {
            "再见! 有需要随时找我。",
            "好的,再见! 祝您有愉快的一天。",
            "再见! 期待下次为您服务。",
            "好的,慢走! 有问题随时联系我。"
        };
        
        int index = (int) (Math.random() * farewells.length);
        return farewells[index];
    }

    private double calculateConfidence(List<KnowledgeEntry> entries) {
        if (entries.isEmpty()) return 0.0;
        return entries.stream()
            .mapToDouble(KnowledgeEntry::getRelevanceScore)
            .average()
            .orElse(0.0);
    }

    private AiResponse buildErrorResponse(String message) {
        return AiResponse.builder()
            .type(ResponseType.ERROR)
            .content(message)
            .build();
    }

    private LocalDateTime parseTimeExpression(String timeStr) {
        // 使用时间表达式解析器
        return TimeExpressionParser.parse(timeStr);
    }

    private String formatTime(LocalDateTime time) {
        return time.format(java.time.format.DateTimeFormatter.ofPattern("MM月dd日 HH:mm"));
    }

    private void saveReminder(Reminder reminder) {
        // 保存提醒到数据库
    }

    private List<Reminder> getUserReminders(String userId, LocalDateTime start, LocalDateTime end) {
        // 从数据库获取提醒列表
        return Collections.emptyList();
    }

    private List<UserSearchResult> searchUsers(String query, int limit) {
        // 执行用户搜索
        return Collections.emptyList();
    }

    /**
     * 获取用户的对话历史
     */
    public Page<AiConversation> getConversationHistory(String userId, Pageable pageable) {
        return conversationRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable);
    }

    /**
     * 清除会话上下文
     */
    public void clearSession(String sessionId) {
        sessionContexts.remove(sessionId);
        conversationRepository.deleteBySessionId(sessionId);
    }

    /**
     * 获取会话统计信息
     */
    public SessionStats getSessionStats(String sessionId) {
        AiSessionContext context = sessionContexts.get(sessionId);
        if (context == null) {
            return null;
        }
        
        return SessionStats.builder()
            .sessionId(sessionId)
            .userId(context.getUserId())
            .interactionCount(context.getInteractionCount())
            .createdAt(context.getCreatedAt())
            .lastInteractionTime(context.getLastInteractionTime())
            .build();
    }
}
