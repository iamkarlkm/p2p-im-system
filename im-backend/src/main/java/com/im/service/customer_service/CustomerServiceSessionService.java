package com.im.service.customer_service;

import com.im.dto.customer_service.*;
import java.util.List;

/**
 * 客服会话服务接口
 * 功能 #319 - 智能客服与工单管理系统
 */
public interface CustomerServiceSessionService {
    
    /**
     * 开始会话
     */
    SessionResponse startSession(StartSessionRequest request);
    
    /**
     * 结束会话
     */
    SessionResponse endSession(Long sessionId, Long operatorId);
    
    /**
     * 发送消息
     */
    MessageResponse sendMessage(SendMessageRequest request);
    
    /**
     * 获取会话详情
     */
    SessionResponse getSessionById(Long sessionId);
    
    /**
     * 获取用户会话列表
     */
    List<SessionResponse> getUserSessions(Long userId, Integer status);
    
    /**
     * 获取客服会话列表
     */
    List<SessionResponse> getAgentSessions(Long agentId, Integer status);
    
    /**
     * 转人工客服
     */
    SessionResponse transferToHuman(Long sessionId, Long agentId, String reason);
    
    /**
     * 标记消息已读
     */
    void markMessagesAsRead(Long sessionId, Long userId);
    
    /**
     * 获取会话消息列表
     */
    List<MessageResponse> getSessionMessages(Long sessionId, Long lastMessageId, Integer size);
    
    /**
     * 提交满意度评价
     */
    void submitSatisfaction(Long sessionId, Integer score, String comment);
}
