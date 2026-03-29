package com.im.backend.modules.merchant.assistant.service;

import com.im.backend.modules.merchant.assistant.dto.*;

import java.util.List;

/**
 * 智能客服服务接口
 */
public interface IChatbotService {
    
    /**
     * 创建客服会话
     */
    SessionResponse createSession(CreateSessionRequest request);
    
    /**
     * 发送消息并获取智能回复
     */
    MessageResponse sendMessage(SendMessageRequest request);
    
    /**
     * 智能客服回复
     */
    ChatbotReplyResponse getReply(ChatbotReplyRequest request);
    
    /**
     * 转人工服务
     */
    void transferToAgent(String sessionId, Long agentId);
    
    /**
     * 结束会话
     */
    void endSession(String sessionId, Integer rating, String satisfaction);
    
    /**
     * 获取会话消息历史
     */
    List<MessageResponse> getSessionMessages(String sessionId);
    
    /**
     * 获取商户待处理会话列表
     */
    List<SessionResponse> getPendingSessions(Long merchantId);
}
