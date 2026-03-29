package com.im.modules.merchant.automation.service.impl;

import com.im.modules.merchant.automation.dto.*;
import com.im.modules.merchant.automation.entity.ChatbotMessage;
import com.im.modules.merchant.automation.entity.ChatbotSession;
import com.im.modules.merchant.automation.enums.ChatSessionStatus;
import com.im.modules.merchant.automation.enums.MessageSenderType;
import com.im.modules.merchant.automation.enums.TransferStatus;
import com.im.modules.merchant.automation.repository.ChatbotMessageMapper;
import com.im.modules.merchant.automation.repository.ChatbotSessionMapper;
import com.im.modules.merchant.automation.service.IChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 智能客服机器人服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements IChatbotService {
    
    private final ChatbotSessionMapper sessionMapper;
    private final ChatbotMessageMapper messageMapper;
    
    // FAQ知识库关键词匹配
    private static final Map<Pattern, String> FAQ_RESPONSES = new LinkedHashMap<>();
    
    static {
        FAQ_RESPONSES.put(Pattern.compile("营业.*?(时间|几点|多久)"), "我们的营业时间是每天 09:00-22:00，节假日正常营业~");
        FAQ_RESPONSES.put(Pattern.compile("(电话|联系方式|怎么联系)"), "您可以通过以下方式联系我们：\n客服热线：400-xxx-xxxx\n在线客服：点击右下角客服图标");
        FAQ_RESPONSES.put(Pattern.compile("(地址|在哪|位置|怎么去)"), "我们的地址是：[商户地址]，您可以点击导航按钮获取路线~");
        FAQ_RESPONSES.put(Pattern.compile("(价格|多少钱|费用|收费)"), "我们的价格非常实惠！具体可以查看菜单/服务列表，现在下单还有优惠哦~");
        FAQ_RESPONSES.put(Pattern.compile("(优惠|折扣|活动|促销)"), "现在有超值优惠活动！新用户首单立减20元，满100减15，详情查看首页活动专区~");
        FAQ_RESPONSES.put(Pattern.compile("(配送|外卖|送货|多久到)"), "我们提供快速配送服务，一般30-60分钟送达，具体时间根据距离而定~");
        FAQ_RESPONSES.put(Pattern.compile("(退|换|退款|售后)"), "我们支持7天无理由退换，如有问题请联系客服处理，会尽快为您解决~");
        FAQ_RESPONSES.put(Pattern.compile("(预约|预订|订位)"), "支持在线预约！您可以点击"预约服务"选择时间和人数，我们会为您保留位置~");
        FAQ_RESPONSES.put(Pattern.compile("(会员|积分|vip)"), "成为会员享专属权益！消费1元=1积分，积分可兑换优惠券，会员日还有双倍积分~");
        FAQ_RESPONSES.put(Pattern.compile("(招聘|工作|求职|应聘)"), "抱歉，招聘相关问题请拨打人事电话：xxx-xxxx-xxxx，或发送简历至 hr@merchant.com");
    }
    
    @Override
    @Transactional
    public ChatbotMessageResponse processMessage(ChatbotMessageRequest request) {
        String merchantId = request.getMerchantId();
        String userId = request.getUserId();
        String message = request.getMessage();
        
        // 获取或创建会话
        ChatbotSession session = getOrCreateSession(merchantId, userId);
        String sessionId = session.getSessionId();
        
        // 保存用户消息
        ChatbotMessage userMessage = new ChatbotMessage();
        userMessage.setSessionId(sessionId);
        userMessage.setMerchantId(merchantId);
        userMessage.setUserId(userId);
        userMessage.setSenderType(String.valueOf(MessageSenderType.USER.getCode()));
        userMessage.setSenderId(userId);
        userMessage.setContent(message);
        userMessage.setMessageType("TEXT");
        userMessage.setIsRead(false);
        messageMapper.insert(userMessage);
        
        // 更新会话消息数
        sessionMapper.incrementMessageCount(sessionId);
        
        // AI处理消息
        String intent = recognizeIntent(message);
        double confidence = calculateConfidence(message, intent);
        
        // 生成回复
        String reply = generateReply(message, intent, session);
        
        // 判断是否需转人工
        boolean needTransfer = checkNeedTransfer(message, intent, confidence, session);
        
        // 保存AI回复
        ChatbotMessage aiMessage = new ChatbotMessage();
        aiMessage.setSessionId(sessionId);
        aiMessage.setMerchantId(merchantId);
        aiMessage.setUserId(userId);
        aiMessage.setSenderType(String.valueOf(MessageSenderType.AI_BOT.getCode()));
        aiMessage.setSenderId("AI_BOT");
        aiMessage.setContent(reply);
        aiMessage.setMessageType("TEXT");
        aiMessage.setIntent(intent);
        aiMessage.setConfidence(confidence);
        aiMessage.setReplyToMessageId(userMessage.getMessageId());
        aiMessage.setIsRead(false);
        messageMapper.insert(aiMessage);
        
        // 更新会话
        session.setIntent(intent);
        session.setConfidence(confidence);
        if (needTransfer) {
            session.setStatus(ChatSessionStatus.WAITING_TRANSFER.getCode());
            session.setNeedTransfer(true);
            session.setTransferReason("复杂问题需人工处理");
            session.setTransferTime(LocalDateTime.now());
        } else {
            session.setStatus(ChatSessionStatus.AI_SERVING.getCode());
        }
        sessionMapper.updateById(session);
        
        return ChatbotMessageResponse.builder()
                .messageId(aiMessage.getMessageId())
                .sessionId(sessionId)
                .merchantId(merchantId)
                .userId(userId)
                .replyContent(reply)
                .replyType("TEXT")
                .suggestedQuestions(generateSuggestedQuestions(intent))
                .quickActions(generateQuickActions(intent))
                .needTransferToHuman(needTransfer)
                .transferReason(needTransfer ? "复杂问题需人工处理" : null)
                .confidence(confidence)
                .intent(intent)
                .replyTime(LocalDateTime.now())
                .build();
    }
    
    @Override
    public TransferToHumanResponse transferToHuman(TransferToHumanRequest request) {
        String sessionId = request.getSessionId();
        
        ChatbotSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            throw new RuntimeException("会话不存在");
        }
        
        session.setStatus(ChatSessionStatus.WAITING_TRANSFER.getCode());
        session.setNeedTransfer(true);
        session.setTransferReason(request.getReason());
        session.setTransferTime(LocalDateTime.now());
        sessionMapper.updateById(session);
        
        return TransferToHumanResponse.builder()
                .transferId(UUID.randomUUID().toString())
                .sessionId(sessionId)
                .merchantId(session.getMerchantId())
                .userId(session.getUserId())
                .status(TransferStatus.QUEUEING.getCode())
                .statusName(TransferStatus.QUEUEING.getDesc())
                .queuePosition(calculateQueuePosition(session.getMerchantId()))
                .estimatedWaitTime(3)
                .transferTime(LocalDateTime.now())
                .reason(request.getReason())
                .priority(request.getPriority())
                .build();
    }
    
    @Override
    public ChatSessionHistoryResponse getSessionHistory(String sessionId) {
        ChatbotSession session = sessionMapper.selectById(sessionId);
        if (session == null) {
            return null;
        }
        
        List<ChatbotMessage> messages = messageMapper.findBySessionId(sessionId, 100);
        
        return buildSessionHistoryResponse(session, messages);
    }
    
    @Override
    public List<ChatSessionHistoryResponse> getActiveSessions(String merchantId) {
        // 实现获取活跃会话逻辑
        return new ArrayList<>();
    }
    
    @Override
    public void closeSession(String sessionId) {
        ChatbotSession session = sessionMapper.selectById(sessionId);
        if (session != null) {
            session.setStatus(ChatSessionStatus.CLOSED.getCode());
            session.setEndTime(LocalDateTime.now());
            sessionMapper.updateById(session);
        }
    }
    
    @Override
    public List<TransferToHumanResponse> getPendingTransfers(String merchantId) {
        List<ChatbotSession> sessions = sessionMapper.findPendingTransferSessions(merchantId);
        return sessions.stream()
                .map(s -> TransferToHumanResponse.builder()
                        .sessionId(s.getSessionId())
                        .merchantId(s.getMerchantId())
                        .userId(s.getUserId())
                        .status(TransferStatus.QUEUEING.getCode())
                        .statusName(TransferStatus.QUEUEING.getDesc())
                        .transferTime(s.getTransferTime())
                        .reason(s.getTransferReason())
                        .build())
                .collect(Collectors.toList());
    }
    
    @Override
    public void acceptTransfer(String transferId, String agentId) {
        // 实现人工客服接入逻辑
        ChatbotSession session = sessionMapper.selectById(transferId);
        if (session != null) {
            session.setStatus(ChatSessionStatus.HUMAN_SERVING.getCode());
            session.setAgentId(agentId);
            sessionMapper.updateById(session);
        }
    }
    
    // ============ 私有方法 ============
    
    private ChatbotSession getOrCreateSession(String merchantId, String userId) {
        ChatbotSession session = sessionMapper.findActiveSession(merchantId, userId);
        if (session == null) {
            session = new ChatbotSession();
            session.setMerchantId(merchantId);
            session.setUserId(userId);
            session.setStatus(ChatSessionStatus.INIT.getCode());
            session.setMessageCount(0);
            session.setNeedTransfer(false);
            session.setStartTime(LocalDateTime.now());
            sessionMapper.insert(session);
        }
        return session;
    }
    
    private String recognizeIntent(String message) {
        String msg = message.toLowerCase();
        if (msg.contains("营业") || msg.contains("时间") || msg.contains("几点")) {
            return "QUERY_BUSINESS_HOURS";
        } else if (msg.contains("价格") || msg.contains("多少钱") || msg.contains("费用")) {
            return "QUERY_PRICE";
        } else if (msg.contains("地址") || msg.contains("在哪") || msg.contains("位置")) {
            return "QUERY_ADDRESS";
        } else if (msg.contains("优惠") || msg.contains("折扣") || msg.contains("活动")) {
            return "QUERY_PROMOTION";
        } else if (msg.contains("预约") || msg.contains("预订") || msg.contains("订位")) {
            return "BOOKING";
        } else if (msg.contains("投诉") || msg.contains("退款") || msg.contains("售后")) {
            return "COMPLAINT";
        }
        return "GENERAL";
    }
    
    private double calculateConfidence(String message, String intent) {
        if ("GENERAL".equals(intent)) {
            return 0.6;
        }
        return 0.85 + Math.random() * 0.1;
    }
    
    private String generateReply(String message, String intent, ChatbotSession session) {
        // 先匹配FAQ
        for (Map.Entry<Pattern, String> entry : FAQ_RESPONSES.entrySet()) {
            if (entry.getKey().matcher(message).find()) {
                return entry.getValue();
            }
        }
        
        // 根据意图回复
        switch (intent) {
            case "QUERY_BUSINESS_HOURS":
                return "我们的营业时间是每天 09:00-22:00，随时欢迎您的光临~";
            case "QUERY_PRICE":
                return "我们的产品和服务价格都很实惠！您可以查看具体菜单或商品列表，现在下单还有专属优惠哦~";
            case "QUERY_ADDRESS":
                return "我们的地址在 [商户地址]，您可以点击导航按钮查看路线，期待您的到来~";
            case "QUERY_PROMOTION":
                return "当前有多重优惠活动！新用户专享、满减活动、会员折扣等，详情查看首页活动专区~";
            case "BOOKING":
                return "支持在线预约哦！点击"预约服务"选择时间，我们会为您提前准备~";
            case "COMPLAINT":
                return "非常抱歉给您带来不好的体验，我会立即为您转接人工客服处理，请稍候~";
            default:
                return "您好！我是智能客服助手，请问有什么可以帮助您的？您可以询问营业时间、优惠活动、预约服务等信息~";
        }
    }
    
    private boolean checkNeedTransfer(String message, String intent, double confidence, ChatbotSession session) {
        String msg = message.toLowerCase();
        if (msg.contains("投诉") || msg.contains("人工") || msg.contains("客服") || 
            msg.contains("退款") || msg.contains("退货") || confidence < 0.5) {
            return true;
        }
        if (session.getMessageCount() > 10) {
            return true;
        }
        return false;
    }
    
    private List<String> generateSuggestedQuestions(String intent) {
        List<String> questions = new ArrayList<>();
        questions.add("现在有什么优惠活动？");
        questions.add("营业时间是什么时候？");
        questions.add("怎么预约服务？");
        return questions;
    }
    
    private List<ChatbotMessageResponse.QuickAction> generateQuickActions(String intent) {
        List<ChatbotMessageResponse.QuickAction> actions = new ArrayList<>();
        actions.add(ChatbotMessageResponse.QuickAction.builder()
                .action("VIEW_MENU")
                .label("查看菜单")
                .value("menu")
                .icon("restaurant")
                .build());
        actions.add(ChatbotMessageResponse.QuickAction.builder()
                .action("MAKE_APPOINTMENT")
                .label("立即预约")
                .value("booking")
                .icon("calendar")
                .build());
        actions.add(ChatbotMessageResponse.QuickAction.builder()
                .action("VIEW_PROMOTIONS")
                .label("优惠活动")
                .value("promotions")
                .icon("gift")
                .build());
        return actions;
    }
    
    private ChatSessionHistoryResponse buildSessionHistoryResponse(ChatbotSession session, List<ChatbotMessage> messages) {
        ChatSessionHistoryResponse.ChatMessage lastMessage = null;
        if (!messages.isEmpty()) {
            ChatbotMessage last = messages.get(messages.size() - 1);
            lastMessage = ChatSessionHistoryResponse.ChatMessage.builder()
                    .messageId(last.getMessageId())
                    .senderType(last.getSenderType())
                    .senderId(last.getSenderId())
                    .content(last.getContent())
                    .messageType(last.getMessageType())
                    .sendTime(last.getCreateTime())
                    .isRead(last.getIsRead())
                    .build();
        }
        
        List<ChatSessionHistoryResponse.ChatMessage> chatMessages = messages.stream()
                .map(m -> ChatSessionHistoryResponse.ChatMessage.builder()
                        .messageId(m.getMessageId())
                        .senderType(m.getSenderType())
                        .senderId(m.getSenderId())
                        .content(m.getContent())
                        .messageType(m.getMessageType())
                        .sendTime(m.getCreateTime())
                        .isRead(m.getIsRead())
                        .build())
                .collect(Collectors.toList());
        
        return ChatSessionHistoryResponse.builder()
                .sessionId(session.getSessionId())
                .merchantId(session.getMerchantId())
                .userId(session.getUserId())
                .status(session.getStatus())
                .statusName(ChatSessionStatus.getDescByCode(session.getStatus()))
                .agentId(session.getAgentId())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .messageCount(session.getMessageCount())
                .lastMessageTime(lastMessage != null ? lastMessage.getSendTime() : null)
                .lastMessagePreview(lastMessage != null ? 
                        lastMessage.getContent().substring(0, Math.min(50, lastMessage.getContent().length())) : null)
                .messages(chatMessages)
                .build();
    }
    
    private int calculateQueuePosition(String merchantId) {
        List<ChatbotSession> pending = sessionMapper.findPendingTransferSessions(merchantId);
        return pending.size();
    }
}
