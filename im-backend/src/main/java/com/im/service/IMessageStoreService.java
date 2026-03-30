package com.im.service;

import com.im.entity.ChatMessage;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 消息存储服务接口
 * 功能 #6: 消息存储与检索引擎
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public interface IMessageStoreService {
    
    /**
     * 保存消息
     */
    ChatMessage saveMessage(ChatMessage message);
    
    /**
     * 根据ID获取消息
     */
    ChatMessage getMessageById(String messageId);
    
    /**
     * 获取会话历史消息
     */
    List<ChatMessage> getConversationHistory(String conversationId, int limit, long beforeTimestamp);
    
    /**
     * 搜索消息
     */
    List<ChatMessage> searchMessages(String userId, String keyword, LocalDateTime startTime, LocalDateTime endTime);
    
    /**
     * 撤回消息
     */
    boolean recallMessage(String messageId, String operatorId);
    
    /**
     * 删除消息（对某用户隐藏）
     */
    boolean deleteMessageForUser(String messageId, String userId);
    
    /**
     * 标记消息已读
     */
    boolean markMessageRead(String messageId, String userId);
    
    /**
     * 获取未读消息数
     */
    int getUnreadCount(String userId, String conversationId);
    
    /**
     * 获取用户所有未读消息数
     */
    int getTotalUnreadCount(String userId);
    
    /**
     * 清理过期消息
     */
    int cleanupExpiredMessages(int days);
}
