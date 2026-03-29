package com.im.backend.modules.poi.customer_service.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.im.backend.modules.poi.customer_service.dto.*;
import com.im.backend.modules.poi.customer_service.entity.PoiCustomerService;
import com.im.backend.modules.poi.customer_service.entity.PoiCustomerServiceSession;

import java.util.List;

/**
 * POI智能客服服务接口
 */
public interface IPoiCustomerService extends IService<PoiCustomerService> {

    /**
     * 创建客服会话
     */
    SessionResponse createSession(Long userId, CreateSessionRequest request);

    /**
     * 分配客服
     */
    PoiCustomerService assignAgent(Long poiId, String inquiryType);

    /**
     * 发送消息
     */
    MessageResponse sendMessage(Long senderId, String senderType, SendMessageRequest request);

    /**
     * 处理机器人回复
     */
    FaqMatchResult handleRobotReply(String sessionId, String userMessage);

    /**
     * 转接人工客服
     */
    boolean transferToHuman(String sessionId, String reason);

    /**
     * 获取用户会话列表
     */
    List<SessionResponse> getUserSessions(Long userId);

    /**
     * 获取客服会话列表
     */
    List<SessionResponse> getAgentSessions(Long agentId);

    /**
     * 关闭会话
     */
    boolean closeSession(String sessionId, String closeReason);

    /**
     * 获取会话消息历史
     */
    List<MessageResponse> getSessionMessages(String sessionId, Integer page, Integer size);

    /**
     * 标记消息已读
     */
    boolean markMessagesAsRead(String sessionId, String readerType);

    /**
     * 撤回消息
     */
    boolean recallMessage(String messageId, Long operatorId);

    /**
     * 评价会话
     */
    boolean rateSession(String sessionId, Integer rating, String comment);

    /**
     * 获取POI客服列表
     */
    List<PoiCustomerService> getPoiAgents(Long poiId);

    /**
     * 更新客服在线状态
     */
    boolean updateAgentStatus(Long agentId, String status);

    /**
     * 心跳保活
     */
    boolean agentHeartbeat(Long agentId);
}
