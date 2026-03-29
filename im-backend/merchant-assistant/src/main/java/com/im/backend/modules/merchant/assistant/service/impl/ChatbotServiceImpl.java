package com.im.backend.modules.merchant.assistant.service.impl;

import com.im.backend.modules.merchant.assistant.dto.*;
import com.im.backend.modules.merchant.assistant.entity.*;
import com.im.backend.modules.merchant.assistant.enums.*;
import com.im.backend.modules.merchant.assistant.repository.*;
import com.im.backend.modules.merchant.assistant.service.IChatbotService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * 智能客服服务实现
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl implements IChatbotService {
    
    private final ChatbotConfigMapper configMapper;
    private final ChatbotKnowledgeMapper knowledgeMapper;
    private final CustomerServiceSessionMapper sessionMapper;
    private final CustomerServiceMessageMapper messageMapper;
    
    @Override
    @Transactional
    public SessionResponse createSession(CreateSessionRequest request) {
        // 检查是否已有活跃会话
        CustomerServiceSession existingSession = sessionMapper.selectActiveSessionByUser(request.getUserId());
        if (existingSession != null) {
            return convertToSessionResponse(existingSession);
        }
        
        // 获取机器人配置
        ChatbotConfig config = configMapper.selectByMerchantId(request.getMerchantId());
        
        // 创建新会话
        CustomerServiceSession session = new CustomerServiceSession();
        session.setSessionId("CSS" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        session.setMerchantId(request.getMerchantId());
        session.setUserId(request.getUserId());
        session.setSessionStatus(SessionStatus.BOT.getCode());
        session.setMessageCount(0);
        session.setBotResolved(0);
        session.setSource(request.getSource());
        session.setStartTime(LocalDateTime.now());
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        
        sessionMapper.insert(session);
        
        // 发送欢迎消息
        if (config != null && config.getWelcomeMessage() != null) {
            sendSystemMessage(session.getSessionId(), config.getWelcomeMessage());
        }
        
        return convertToSessionResponse(session);
    }
    
    @Override
    @Transactional
    public MessageResponse sendMessage(SendMessageRequest request) {
        // 保存用户消息
        CustomerServiceMessage userMessage = new CustomerServiceMessage();
        userMessage.setMessageId("MSG" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        userMessage.setSessionId(request.getSessionId());
        userMessage.setSenderId(request.getSenderId());
        userMessage.setSenderType(request.getSenderType());
        userMessage.setMessageType(request.getMessageType());
        userMessage.setContent(request.getContent());
        userMessage.setMediaContent(request.getMediaContent());
        userMessage.setIsRead(false);
        userMessage.setCreateTime(LocalDateTime.now());
        
        messageMapper.insert(userMessage);
        
        // 更新会话消息数
        CustomerServiceSession session = sessionMapper.selectBySessionId(request.getSessionId());
        if (session != null) {
            session.setMessageCount(session.getMessageCount() + 1);
            sessionMapper.updateById(session);
            
            // 如果是用户消息且会话由机器人处理，生成智能回复
            if (SenderType.USER.getCode().equals(request.getSenderType()) 
                    && SessionStatus.BOT.getCode().equals(session.getSessionStatus())) {
                generateBotReply(session, request.getContent());
            }
        }
        
        return convertToMessageResponse(userMessage);
    }
    
    @Override
    public ChatbotReplyResponse getReply(ChatbotReplyRequest request) {
        // 获取商户知识库
        List<ChatbotKnowledge> knowledgeList = knowledgeMapper.selectByMerchantId(request.getMerchantId());
        
        // 简单的关键词匹配（实际应使用NLP）
        String userMessage = request.getUserMessage().toLowerCase();
        ChatbotKnowledge matchedKnowledge = null;
        double maxScore = 0.0;
        
        for (ChatbotKnowledge knowledge : knowledgeList) {
            double score = calculateMatchScore(userMessage, knowledge);
            if (score > maxScore) {
                maxScore = score;
                matchedKnowledge = knowledge;
            }
        }
        
        ChatbotReplyResponse response = new ChatbotReplyResponse();
        
        // 获取机器人配置
        ChatbotConfig config = configMapper.selectByMerchantId(request.getMerchantId());
        double threshold = config != null && config.getTransferThreshold() != null 
                ? config.getTransferThreshold() : 0.5;
        
        if (matchedKnowledge != null && maxScore >= threshold) {
            response.setReplyContent(matchedKnowledge.getAnswer());
            response.setKnowledgeId(matchedKnowledge.getId());
            response.setIntentTag(matchedKnowledge.getIntentTag());
            response.setConfidence(maxScore);
            response.setNeedTransfer(false);
        } else {
            response.setReplyContent("抱歉，我暂时无法理解您的问题，正在为您转接人工客服...");
            response.setConfidence(maxScore);
            response.setNeedTransfer(true);
        }
        
        return response;
    }
    
    @Override
    @Transactional
    public void transferToAgent(String sessionId, Long agentId) {
        CustomerServiceSession session = sessionMapper.selectBySessionId(sessionId);
        if (session != null) {
            session.setSessionStatus(SessionStatus.AGENT.getCode());
            session.setAgentId(agentId);
            session.setTransferTime(LocalDateTime.now());
            session.setUpdateTime(LocalDateTime.now());
            sessionMapper.updateById(session);
            
            // 发送转人工通知
            sendSystemMessage(sessionId, "已为您转接人工客服，请稍候...");
        }
    }
    
    @Override
    @Transactional
    public void endSession(String sessionId, Integer rating, String satisfaction) {
        CustomerServiceSession session = sessionMapper.selectBySessionId(sessionId);
        if (session != null) {
            session.setSessionStatus(SessionStatus.ENDED.getCode());
            session.setUserRating(rating);
            session.setSatisfaction(satisfaction);
            session.setEndTime(LocalDateTime.now());
            session.setUpdateTime(LocalDateTime.now());
            sessionMapper.updateById(session);
        }
    }
    
    @Override
    public List<MessageResponse> getSessionMessages(String sessionId) {
        List<CustomerServiceMessage> messages = messageMapper.selectBySessionId(sessionId);
        return messages.stream()
                .map(this::convertToMessageResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<SessionResponse> getPendingSessions(Long merchantId) {
        List<CustomerServiceSession> sessions = sessionMapper.selectPendingSessions(merchantId);
        return sessions.stream()
                .map(this::convertToSessionResponse)
                .collect(Collectors.toList());
    }
    
    // ============ 私有方法 ============
    
    private void generateBotReply(CustomerServiceSession session, String userMessage) {
        ChatbotReplyRequest request = new ChatbotReplyRequest();
        request.setSessionId(session.getSessionId());
        request.setUserMessage(userMessage);
        request.setMerchantId(session.getMerchantId());
        
        ChatbotReplyResponse reply = getReply(request);
        
        // 保存机器人回复
        CustomerServiceMessage botMessage = new CustomerServiceMessage();
        botMessage.setMessageId("MSG" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        botMessage.setSessionId(session.getSessionId());
        botMessage.setSenderId(0L);
        botMessage.setSenderType(SenderType.BOT.getCode());
        botMessage.setMessageType(MessageType.TEXT.getCode());
        botMessage.setContent(reply.getReplyContent());
        botMessage.setIsRead(false);
        botMessage.setIntentResult(reply.getIntentTag());
        botMessage.setConfidence(reply.getConfidence());
        botMessage.setKnowledgeId(reply.getKnowledgeId());
        botMessage.setCreateTime(LocalDateTime.now());
        
        messageMapper.insert(botMessage);
        
        // 如果需要转人工，更新会话状态
        if (reply.getNeedTransfer()) {
            session.setSessionStatus(SessionStatus.QUEUE.getCode());
            sessionMapper.updateById(session);
        } else {
            // 记录机器人已解决
            session.setBotResolved(1);
        }
        
        session.setMessageCount(session.getMessageCount() + 1);
        sessionMapper.updateById(session);
    }
    
    private void sendSystemMessage(String sessionId, String content) {
        CustomerServiceMessage message = new CustomerServiceMessage();
        message.setMessageId("MSG" + UUID.randomUUID().toString().replace("-", "").substring(0, 16));
        message.setSessionId(sessionId);
        message.setSenderId(0L);
        message.setSenderType(SenderType.SYSTEM.getCode());
        message.setMessageType(MessageType.TEXT.getCode());
        message.setContent(content);
        message.setIsRead(false);
        message.setCreateTime(LocalDateTime.now());
        
        messageMapper.insert(message);
    }
    
    private double calculateMatchScore(String userMessage, ChatbotKnowledge knowledge) {
        // 简单的关键词匹配算法
        double score = 0.0;
        
        // 检查标准问题匹配
        if (userMessage.contains(knowledge.getQuestion().toLowerCase())) {
            score += 0.5;
        }
        
        // 检查关键词匹配
        if (knowledge.getKeywords() != null) {
            String[] keywords = knowledge.getKeywords().split(",");
            for (String keyword : keywords) {
                if (userMessage.contains(keyword.trim().toLowerCase())) {
                    score += 0.25;
                }
            }
        }
        
        return Math.min(score, 1.0);
    }
    
    private SessionResponse convertToSessionResponse(CustomerServiceSession session) {
        SessionResponse response = new SessionResponse();
        response.setSessionId(session.getSessionId());
        response.setMerchantId(session.getMerchantId());
        response.setUserId(session.getUserId());
        response.setSessionStatus(session.getSessionStatus());
        response.setCurrentHandler(session.getSessionStatus());
        response.setCreateTime(session.getCreateTime());
        return response;
    }
    
    private MessageResponse convertToMessageResponse(CustomerServiceMessage message) {
        MessageResponse response = new MessageResponse();
        response.setMessageId(message.getMessageId());
        response.setSessionId(message.getSessionId());
        response.setSenderType(message.getSenderType());
        response.setSenderName(getSenderName(message.getSenderType()));
        response.setMessageType(message.getMessageType());
        response.setContent(message.getContent());
        response.setMediaContent(message.getMediaContent());
        response.setIsRead(message.getIsRead());
        response.setIntentResult(message.getIntentResult());
        response.setConfidence(message.getConfidence());
        response.setCreateTime(message.getCreateTime());
        return response;
    }
    
    private String getSenderName(String senderType) {
        if (SenderType.USER.getCode().equals(senderType)) {
            return "用户";
        } else if (SenderType.BOT.getCode().equals(senderType)) {
            return "智能客服";
        } else if (SenderType.AGENT.getCode().equals(senderType)) {
            return "人工客服";
        } else {
            return "系统";
        }
    }
}
