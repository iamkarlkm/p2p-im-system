package com.im.mobile.listener;

import com.im.mobile.model.Message;

import java.util.UUID;

/**
 * 消息监听器接口 - 功能#9: 基础IM客户端SDK
 * 用于接收和处理IM消息
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
public interface MessageListener {
    
    /**
     * 获取监听器唯一ID
     * 
     * @return 监听器ID
     */
    default String getId() {
        return UUID.randomUUID().toString();
    }
    
    /**
     * 当收到新消息时调用
     * 
     * @param message 收到的消息
     */
    void onMessageReceived(Message message);
    
    /**
     * 当消息状态改变时调用
     * 
     * @param messageId 消息ID
     * @param status 新状态
     */
    default void onMessageStatusChanged(String messageId, int status) {
        // 默认空实现
    }
    
    /**
     * 当消息被撤回时调用
     * 
     * @param messageId 被撤回的消息ID
     */
    default void onMessageRecalled(String messageId) {
        // 默认空实现
    }
    
    /**
     * 当收到已读回执时调用
     * 
     * @param messageId 已读的消息ID
     * @param userId 已读用户ID
     */
    default void onMessageRead(String messageId, String userId) {
        // 默认空实现
    }
    
    /**
     * 当收到打字状态时调用
     * 
     * @param conversationId 会话ID
     * @param userId 用户ID
     * @param isTyping 是否正在输入
     */
    default void onTypingStatusChanged(String conversationId, String userId, boolean isTyping) {
        // 默认空实现
    }
    
    /**
     * 当连接状态改变时调用
     * 
     * @param connected 是否已连接
     */
    default void onConnectionStatusChanged(boolean connected) {
        // 默认空实现
    }
}
