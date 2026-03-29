package com.im.service.poiim;

import com.im.entity.poiim.PoiImSession;
import com.im.entity.poiim.PoiImMessage;
import java.util.List;

/**
 * POI智能客服IM服务接口
 */
public interface PoiImService {
    
    /**
     * 创建会话
     */
    PoiImSession createSession(String poiId, String userId, String source);
    
    /**
     * 从围栏触发创建会话
     */
    PoiImSession createSessionFromFence(String poiId, String userId, Double longitude, Double latitude);
    
    /**
     * 发送消息
     */
    PoiImMessage sendMessage(String sessionId, String senderId, String senderType, String messageType, String content);
    
    /**
     * 获取会话历史消息
     */
    List<PoiImMessage> getSessionMessages(String sessionId, Integer page, Integer size);
    
    /**
     * 获取用户的会话列表
     */
    List<PoiImSession> getUserSessions(String userId);
    
    /**
     * 获取商家的会话列表
     */
    List<PoiImSession> getMerchantSessions(String merchantId, String status);
    
    /**
     * 分配客服
     */
    void assignAgent(String sessionId);
    
    /**
     * 转接客服
     */
    void transferAgent(String sessionId, String newAgentId);
    
    /**
     * 关闭会话
     */
    void closeSession(String sessionId);
    
    /**
     * 标记消息已读
     */
    void markMessagesRead(String sessionId, String userId);
    
    /**
     * 获取会话详情
     */
    PoiImSession getSession(String sessionId);
}
