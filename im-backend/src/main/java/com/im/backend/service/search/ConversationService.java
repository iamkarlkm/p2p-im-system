package com.im.backend.service.search;

import com.im.backend.entity.search.ConversationSession;

import java.util.List;

/**
 * 对话服务接口
 * 
 * @author IM Development Team
 * @version 1.0.0
 * @since 2026-03-28
 */
public interface ConversationService {
    
    /**
     * 创建会话
     * 
     * @param userId 用户ID
     * @param sessionType 会话类型
     * @return 会话ID
     */
    String createSession(Long userId, String sessionType);
    
    /**
     * 创建会话实体
     * 
     * @param userId 用户ID
     * @param sessionType 会话类型
     * @return 会话实体
     */
    ConversationSession createSessionEntity(Long userId, String sessionType);
    
    /**
     * 获取会话
     * 
     * @param sessionId 会话ID
     * @return 会话实体
     */
    ConversationSession getSession(String sessionId);
    
    /**
     * 更新会话
     * 
     * @param session 会话实体
     */
    void updateSession(ConversationSession session);
    
    /**
     * 结束会话
     * 
     * @param sessionId 会话ID
     */
    void endSession(String sessionId);
    
    /**
     * 获取会话历史
     * 
     * @param sessionId 会话ID
     * @return 历史记录
     */
    List<Object> getSessionHistory(String sessionId);
    
    /**
     * 检查会话是否有效
     * 
     * @param sessionId 会话ID
     * @return 是否有效
     */
    boolean isSessionValid(String sessionId);
    
    /**
     * 清理过期会话
     */
    void cleanExpiredSessions();
    
    /**
     * 获取用户活跃会话
     * 
     * @param userId 用户ID
     * @return 会话列表
     */
    List<ConversationSession> getUserActiveSessions(Long userId);
}
