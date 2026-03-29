package com.im.modules.merchant.automation.service;

import com.im.modules.merchant.automation.dto.*;
import java.util.List;

/**
 * 智能客服机器人服务接口
 */
public interface IChatbotService {
    
    /**
     * 处理用户消息并返回AI回复
     */
    ChatbotMessageResponse processMessage(ChatbotMessageRequest request);
    
    /**
     * 智能转人工
     */
    TransferToHumanResponse transferToHuman(TransferToHumanRequest request);
    
    /**
     * 获取会话历史
     */
    ChatSessionHistoryResponse getSessionHistory(String sessionId);
    
    /**
     * 获取商户活跃会话列表
     */
    List<ChatSessionHistoryResponse> getActiveSessions(String merchantId);
    
    /**
     * 关闭会话
     */
    void closeSession(String sessionId);
    
    /**
     * 获取待转人工的会话
     */
    List<TransferToHumanResponse> getPendingTransfers(String merchantId);
    
    /**
     * 人工客服接入
     */
    void acceptTransfer(String transferId, String agentId);
}
