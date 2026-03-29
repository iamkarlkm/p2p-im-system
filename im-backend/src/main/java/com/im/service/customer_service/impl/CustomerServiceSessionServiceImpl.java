package com.im.service.customer_service.impl;

import com.im.dto.customer_service.*;
import com.im.entity.customer_service.CustomerServiceMessage;
import com.im.entity.customer_service.CustomerServiceSession;
import com.im.enums.customer_service.MessageType;
import com.im.enums.customer_service.SessionStatus;
import com.im.service.customer_service.CustomerServiceSessionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 客服会话服务实现
 * 功能 #319 - 智能客服与工单管理系统
 */
@Slf4j
@Service
public class CustomerServiceSessionServiceImpl implements CustomerServiceSessionService {
    
    private final Map<Long, CustomerServiceSession> sessionStore = new ConcurrentHashMap<>();
    private final Map<Long, CustomerServiceMessage> messageStore = new ConcurrentHashMap<>();
    private final AtomicLong sessionIdGenerator = new AtomicLong(1);
    private final AtomicLong messageIdGenerator = new AtomicLong(1);
    
    @Override
    public SessionResponse startSession(StartSessionRequest request) {
        CustomerServiceSession session = new CustomerServiceSession();
        session.setId(sessionIdGenerator.getAndIncrement());
        session.setSessionNo(generateSessionNo());
        session.setUserId(request.getUserId());
        session.setSessionType(request.getSessionType());
        session.setStatus(SessionStatus.IN_PROGRESS.getCode());
        session.setTicketId(request.getTicketId());
        session.setStartTime(LocalDateTime.now());
        session.setLastActivityTime(LocalDateTime.now());
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        session.setDeleted(0);
        
        sessionStore.put(session.getId(), session);
        log.info("会话开始: {}", session.getSessionNo());
        
        return convertToResponse(session);
    }
    
    @Override
    public SessionResponse endSession(Long sessionId, Long operatorId) {
        CustomerServiceSession session = sessionStore.get(sessionId);
        if (session == null) return null;
        
        session.setStatus(SessionStatus.ENDED.getCode());
        session.setEndTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        
        return convertToResponse(session);
    }
    
    @Override
    public MessageResponse sendMessage(SendMessageRequest request) {
        CustomerServiceMessage message = new CustomerServiceMessage();
        message.setId(messageIdGenerator.getAndIncrement());
        message.setSessionId(request.getSessionId());
        message.setSenderType(request.getSenderType());
        message.setSenderId(request.getSenderId());
        message.setMessageType(request.getMessageType());
        message.setContent(request.getContent());
        message.setMediaUrl(request.getMediaUrl());
        message.setStatus(1);
        message.setCreateTime(LocalDateTime.now());
        message.setUpdateTime(LocalDateTime.now());
        message.setDeleted(0);
        
        messageStore.put(message.getId(), message);
        
        CustomerServiceSession session = sessionStore.get(request.getSessionId());
        if (session != null) {
            session.setLastActivityTime(LocalDateTime.now());
            session.setUpdateTime(LocalDateTime.now());
        }
        
        return convertToMessageResponse(message);
    }
    
    @Override
    public SessionResponse getSessionById(Long sessionId) {
        CustomerServiceSession session = sessionStore.get(sessionId);
        return session != null ? convertToResponse(session) : null;
    }
    
    @Override
    public List<SessionResponse> getUserSessions(Long userId, Integer status) {
        List<SessionResponse> result = new ArrayList<>();
        for (CustomerServiceSession session : sessionStore.values()) {
            if (!session.getUserId().equals(userId)) continue;
            if (status != null && !session.getStatus().equals(status)) continue;
            result.add(convertToResponse(session));
        }
        return result;
    }
    
    @Override
    public List<SessionResponse> getAgentSessions(Long agentId, Integer status) {
        List<SessionResponse> result = new ArrayList<>();
        for (CustomerServiceSession session : sessionStore.values()) {
            if (session.getAgentId() == null || !session.getAgentId().equals(agentId)) continue;
            if (status != null && !session.getStatus().equals(status)) continue;
            result.add(convertToResponse(session));
        }
        return result;
    }
    
    @Override
    public SessionResponse transferToHuman(Long sessionId, Long agentId, String reason) {
        CustomerServiceSession session = sessionStore.get(sessionId);
        if (session == null) return null;
        
        session.setAgentId(agentId);
        session.setSessionType(2);
        session.setTransferredToHuman(1);
        session.setTransferReason(reason);
        session.setUpdateTime(LocalDateTime.now());
        
        return convertToResponse(session);
    }
    
    @Override
    public void markMessagesAsRead(Long sessionId, Long userId) {
        log.info("标记消息已读: sessionId={}, userId={}", sessionId, userId);
    }
    
    @Override
    public List<MessageResponse> getSessionMessages(Long sessionId, Long lastMessageId, Integer size) {
        List<MessageResponse> result = new ArrayList<>();
        for (CustomerServiceMessage message : messageStore.values()) {
            if (!message.getSessionId().equals(sessionId)) continue;
            result.add(convertToMessageResponse(message));
        }
        return result;
    }
    
    @Override
    public void submitSatisfaction(Long sessionId, Integer score, String comment) {
        CustomerServiceSession session = sessionStore.get(sessionId);
        if (session != null) {
            session.setSatisfactionScore(score);
            session.setSatisfactionComment(comment);
            session.setUpdateTime(LocalDateTime.now());
        }
    }
    
    private String generateSessionNo() {
        return "SS" + System.currentTimeMillis() + String.format("%04d", sessionIdGenerator.get() % 10000);
    }
    
    private SessionResponse convertToResponse(CustomerServiceSession session) {
        SessionResponse response = new SessionResponse();
        response.setId(session.getId());
        response.setSessionNo(session.getSessionNo());
        response.setUserId(session.getUserId());
        response.setAgentId(session.getAgentId());
        response.setSessionType(session.getSessionType());
        response.setStatus(session.getStatus());
        response.setTicketId(session.getTicketId());
        response.setStartTime(session.getStartTime());
        response.setLastActivityTime(session.getLastActivityTime());
        
        SessionStatus status = SessionStatus.fromCode(session.getStatus());
        if (status != null) response.setStatusName(status.getName());
        
        response.setSessionTypeName(session.getSessionType() == 1 ? "机器人" : "人工");
        
        return response;
    }
    
    private MessageResponse convertToMessageResponse(CustomerServiceMessage message) {
        MessageResponse response = new MessageResponse();
        response.setId(message.getId());
        response.setSessionId(message.getSessionId());
        response.setSenderType(message.getSenderType());
        response.setSenderId(message.getSenderId());
        response.setMessageType(message.getMessageType());
        response.setContent(message.getContent());
        response.setMediaUrl(message.getMediaUrl());
        response.setStatus(message.getStatus());
        response.setRecalled(message.getRecalled());
        response.setReplyToMessageId(message.getReplyToMessageId());
        response.setCreateTime(message.getCreateTime());
        
        MessageType type = MessageType.fromCode(message.getMessageType());
        if (type != null) {
            response.setMessageTypeName(type.getName());
        }
        
        String[] senderTypes = {"", "用户", "客服", "机器人"};
        if (message.getSenderType() >= 1 && message.getSenderType() <= 3) {
            response.setSenderTypeName(senderTypes[message.getSenderType()]);
        }
        
        return response;
    }
}
