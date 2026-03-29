package com.im.service.poiim.impl;

import com.im.entity.poiim.*;
import com.im.service.poiim.PoiImService;
import com.im.service.poiim.MerchantAgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * POI智能客服IM服务实现
 */
@Service
public class PoiImServiceImpl implements PoiImService {
    
    @Autowired
    private MerchantAgentService agentService;
    
    private final Map<String, PoiImSession> sessionStore = new ConcurrentHashMap<>();
    private final Map<String, PoiImMessage> messageStore = new ConcurrentHashMap<>();
    private final Map<String, AgentKnowledgeBase> knowledgeStore = new ConcurrentHashMap<>();
    
    @Override
    public PoiImSession createSession(String poiId, String userId, String source) {
        PoiImSession session = new PoiImSession();
        session.setSessionId(UUID.randomUUID().toString());
        session.setPoiId(poiId);
        session.setUserId(userId);
        session.setSource(source);
        session.setSessionStatus("PENDING");
        session.setUnreadCount(0);
        session.setCreateTime(LocalDateTime.now());
        session.setLastMessageTime(LocalDateTime.now());
        
        // 自动分配客服
        assignAgent(session.getSessionId());
        
        sessionStore.put(session.getSessionId(), session);
        
        // 发送欢迎消息
        sendWelcomeMessage(session.getSessionId());
        
        return session;
    }
    
    @Override
    public PoiImSession createSessionFromFence(String poiId, String userId, Double longitude, Double latitude) {
        PoiImSession session = createSession(poiId, userId, "FENCE_TRIGGER");
        session.setUserLongitude(longitude);
        session.setUserLatitude(latitude);
        session.setDistanceToPoi(0.0); // 计算实际距离
        sessionStore.put(session.getSessionId(), session);
        return session;
    }
    
    /**
     * 发送欢迎消息
     */
    private void sendWelcomeMessage(String sessionId) {
        PoiImSession session = sessionStore.get(sessionId);
        if (session == null) return;
        
        PoiImMessage message = new PoiImMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setSessionId(sessionId);
        message.setSenderId("SYSTEM");
        message.setSenderType("SYSTEM");
        message.setSenderName("智能助手");
        message.setMessageType("TEXT");
        message.setContent("您好!欢迎咨询" + session.getPoiName() + ", 有什么可以帮您的吗?");
        message.setMessageStatus("SENT");
        message.setAutoReply(true);
        message.setCreateTime(LocalDateTime.now());
        
        messageStore.put(message.getMessageId(), message);
        updateSessionLastMessage(sessionId, message.getMessageId());
    }
    
    @Override
    public PoiImMessage sendMessage(String sessionId, String senderId, String senderType, 
                                     String messageType, String content) {
        PoiImMessage message = new PoiImMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setSessionId(sessionId);
        message.setSenderId(senderId);
        message.setSenderType(senderType);
        message.setMessageType(messageType);
        message.setContent(content);
        message.setMessageStatus("SENT");
        message.setCreateTime(LocalDateTime.now());
        
        messageStore.put(message.getMessageId(), message);
        updateSessionLastMessage(sessionId, message.getMessageId());
        
        // 如果是用户消息，尝试自动回复
        if ("USER".equals(senderType)) {
            tryAutoReply(sessionId, content);
        }
        
        return message;
    }
    
    /**
     * 尝试自动回复
     */
    private void tryAutoReply(String sessionId, String userMessage) {
        // 从知识库匹配答案
        String answer = matchKnowledgeBase(userMessage);
        if (answer != null) {
            PoiImMessage reply = new PoiImMessage();
            reply.setMessageId(UUID.randomUUID().toString());
            reply.setSessionId(sessionId);
            reply.setSenderId("BOT");
            reply.setSenderType("BOT");
            reply.setSenderName("智能客服");
            reply.setMessageType("TEXT");
            reply.setContent(answer);
            reply.setMessageStatus("SENT");
            reply.setAutoReply(true);
            reply.setCreateTime(LocalDateTime.now());
            
            messageStore.put(reply.getMessageId(), reply);
            updateSessionLastMessage(sessionId, reply.getMessageId());
        }
    }
    
    /**
     * 从知识库匹配答案
     */
    private String matchKnowledgeBase(String userMessage) {
        String lowerMessage = userMessage.toLowerCase();
        
        for (AgentKnowledgeBase kb : knowledgeStore.values()) {
            if (!Boolean.TRUE.equals(kb.getEnabled())) continue;
            
            // 关键词匹配
            for (String keyword : kb.getKeywords()) {
                if (lowerMessage.contains(keyword.toLowerCase())) {
                    return kb.getAnswer();
                }
            }
        }
        return null;
    }
    
    /**
     * 更新会话最后消息
     */
    private void updateSessionLastMessage(String sessionId, String messageId) {
        PoiImSession session = sessionStore.get(sessionId);
        if (session != null) {
            session.setLastMessageId(messageId);
            session.setLastMessageTime(LocalDateTime.now());
        }
    }
    
    @Override
    public List<PoiImMessage> getSessionMessages(String sessionId, Integer page, Integer size) {
        int pageNum = page != null ? page : 0;
        int pageSize = size != null ? size : 20;
        
        return messageStore.values().stream()
                .filter(m -> sessionId.equals(m.getSessionId()))
                .sorted((a, b) -> b.getCreateTime().compareTo(a.getCreateTime()))
                .skip(pageNum * pageSize)
                .limit(pageSize)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PoiImSession> getUserSessions(String userId) {
        return sessionStore.values().stream()
                .filter(s -> userId.equals(s.getUserId()))
                .sorted((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<PoiImSession> getMerchantSessions(String merchantId, String status) {
        return sessionStore.values().stream()
                .filter(s -> merchantId.equals(s.getMerchantId()))
                .filter(s -> status == null || status.equals(s.getSessionStatus()))
                .sorted((a, b) -> b.getLastMessageTime().compareTo(a.getLastMessageTime()))
                .collect(Collectors.toList());
    }
    
    @Override
    public void assignAgent(String sessionId) {
        PoiImSession session = sessionStore.get(sessionId);
        if (session == null) return;
        
        MerchantAgent agent = agentService.assignBestAgent(session.getPoiId(), session.getQueryTags());
        if (agent != null) {
            session.setCurrentAgentId(agent.getAgentId());
            session.setAgentName(agent.getAgentName());
            session.setSessionStatus("ACTIVE");
            session.setMerchantId(agent.getMerchantId());
            
            agentService.incrementSessionCount(agent.getAgentId());
        }
    }
    
    @Override
    public void transferAgent(String sessionId, String newAgentId) {
        PoiImSession session = sessionStore.get(sessionId);
        if (session == null) return;
        
        // 减少原客服会话数
        if (session.getCurrentAgentId() != null) {
            agentService.decrementSessionCount(session.getCurrentAgentId());
        }
        
        // 更新新客服
        MerchantAgent newAgent = agentService.getAgent(newAgentId);
        if (newAgent != null) {
            session.setCurrentAgentId(newAgentId);
            session.setAgentName(newAgent.getAgentName());
            session.setSessionStatus("ACTIVE");
            
            agentService.incrementSessionCount(newAgentId);
            
            // 发送转接通知
            PoiImMessage message = new PoiImMessage();
            message.setMessageId(UUID.randomUUID().toString());
            message.setSessionId(sessionId);
            message.setSenderId("SYSTEM");
            message.setSenderType("SYSTEM");
            message.setMessageType("TEXT");
            message.setContent("已为您转接至客服 " + newAgent.getAgentName());
            message.setMessageStatus("SENT");
            message.setCreateTime(LocalDateTime.now());
            messageStore.put(message.getMessageId(), message);
        }
    }
    
    @Override
    public void closeSession(String sessionId) {
        PoiImSession session = sessionStore.get(sessionId);
        if (session == null) return;
        
        session.setSessionStatus("CLOSED");
        session.setCloseTime(LocalDateTime.now());
        
        // 减少客服会话数
        if (session.getCurrentAgentId() != null) {
            agentService.decrementSessionCount(session.getCurrentAgentId());
        }
        
        // 发送结束消息
        PoiImMessage message = new PoiImMessage();
        message.setMessageId(UUID.randomUUID().toString());
        message.setSessionId(sessionId);
        message.setSenderId("SYSTEM");
        message.setSenderType("SYSTEM");
        message.setMessageType("TEXT");
        message.setContent("会话已结束, 感谢您的咨询!");
        message.setMessageStatus("SENT");
        message.setCreateTime(LocalDateTime.now());
        messageStore.put(message.getMessageId(), message);
    }
    
    @Override
    public void markMessagesRead(String sessionId, String userId) {
        messageStore.values().stream()
                .filter(m -> sessionId.equals(m.getSessionId()))
                .filter(m -> !userId.equals(m.getSenderId()))
                .forEach(m -> m.setMessageStatus("READ"));
        
        PoiImSession session = sessionStore.get(sessionId);
        if (session != null) {
            session.setUnreadCount(0);
        }
    }
    
    @Override
    public PoiImSession getSession(String sessionId) {
        return sessionStore.get(sessionId);
    }
}
