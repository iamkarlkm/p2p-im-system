package com.im.server.service;

import com.im.server.netty.WebSocketMessageHandler;
import com.im.server.netty.dto.WsMessage;
import com.im.server.entity.Message;
import com.im.server.repository.MessageRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 聊天服务
 */
@Service
public class ChatService {
    
    private static final Logger logger = LoggerFactory.getLogger(ChatService.class);
    
    private static final String MESSAGE_CACHE_PREFIX = "im:message:cache:";
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    /**
     * 保存消息
     */
    @Transactional
    public Message saveMessage(String from, String to, String content, String msgId) {
        Message message = new Message();
        message.setFromUser(from);
        message.setToUser(to);
        message.setContent(content);
        message.setMsgId(msgId);
        message.setTimestamp(System.currentTimeMillis());
        message.setStatus(0); // 0: 未读
        
        return messageRepository.save(message);
    }
    
    /**
     * 发送消息到群组
     */
    public void sendToGroup(String groupId, WsMessage wsMessage) {
        // TODO: 获取群组成员，发送消息给每个成员
        // 1. 从数据库获取群组成员列表
        // 2. 遍历成员，发送消息
        logger.info("发送消息到群组: groupId={}", groupId);
    }
    
    /**
     * 获取聊天记录
     */
    public List<Message> getChatHistory(String userId1, String userId2, int page, int size) {
        return messageRepository.findChatHistory(userId1, userId2, page * size, size);
    }
    
    /**
     * 标记消息为已读
     */
    @Transactional
    public void markAsRead(String messageId) {
        messageRepository.updateStatus(messageId, 1);
    }
    
    /**
     * 获取未读消息数
     */
    public int getUnreadCount(String userId) {
        return messageRepository.countUnread(userId);
    }
}
