package com.im.backend.modules.local_life.service;

import com.im.backend.modules.local_life.dto.ConversationResponseDTO;
import com.im.backend.modules.local_life.dto.NaturalLanguageSearchRequestDTO;
import com.im.backend.modules.local_life.entity.ConversationSession;

/**
 * 智能对话服务接口
 * 管理用户与AI助手的多轮对话
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
public interface ConversationService {

    /**
     * 创建新会话
     *
     * @param userId 用户ID
     * @param sessionType 会话类型
     * @return 会话ID
     */
    String createSession(Long userId, String sessionType);

    /**
     * 处理自然语言查询并返回对话响应
     *
     * @param request 搜索请求
     * @param userId 用户ID
     * @return 对话响应
     */
    ConversationResponseDTO processQuery(NaturalLanguageSearchRequestDTO request, Long userId);

    /**
     * 获取会话详情
     *
     * @param sessionId 会话ID
     * @return 会话实体
     */
    ConversationSession getSession(String sessionId);

    /**
     * 更新会话上下文
     *
     * @param sessionId 会话ID
     * @param contextKey 上下文键
     * @param contextValue 上下文值
     */
    void updateContext(String sessionId, String contextKey, Object contextValue);

    /**
     * 结束会话
     *
     * @param sessionId 会话ID
     */
    void endSession(String sessionId);

    /**
     * 清理超时会话
     *
     * @return 清理的会话数量
     */
    int cleanupTimeoutSessions();

    /**
     * 获取会话历史
     *
     * @param sessionId 会话ID
     * @param limit 限制条数
     * @return 历史响应列表
     */
    java.util.List<ConversationResponseDTO> getSessionHistory(String sessionId, int limit);

    /**
     * 评价会话满意度
     *
     * @param sessionId 会话ID
     * @param score 满意度分数 1-5
     */
    void rateSession(String sessionId, Integer score);
}
