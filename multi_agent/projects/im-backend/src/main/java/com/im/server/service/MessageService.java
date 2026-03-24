package com.im.server.service;

import com.im.server.entity.Message;
import com.im.server.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * 消息服务
 */
@Service
@RequiredArgsConstructor
public class MessageService {
    
    private final MessageRepository messageRepository;
    
    /**
     * 发送消息
     */
    @Transactional
    public Message sendMessage(Long fromUserId, Long toUserId, Integer chatType, 
                                 Long chatId, Integer msgType, String content) {
        Message message = new Message();
        message.setMsgId(UUID.randomUUID().toString());
        message.setFromUserId(fromUserId);
        message.setToUserId(toUserId);
        message.setChatType(chatType);
        message.setChatId(chatId);
        message.setMsgType(msgType);
        message.setContent(content);
        message.setStatus(1); // 发送中
        
        return messageRepository.save(message);
    }
    
    /**
     * 获取私聊消息历史
     */
    public List<Message> getPrivateChatHistory(Long userId1, Long userId2, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Message> messages = messageRepository.findByFromUserIdAndToUserIdOrFromUserIdAndToUserId(
                userId1, userId2, userId2, userId1, pageRequest);
        return messages.getContent();
    }
    
    /**
     * 获取群聊消息历史
     */
    public List<Message> getGroupChatHistory(Long groupId, int page, int size) {
        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Message> messages = messageRepository.findByChatTypeAndChatId(2, groupId, pageRequest);
        return messages.getContent();
    }
    
    /**
     * 更新消息状态
     */
    @Transactional
    public void updateMessageStatus(String msgId, Integer status) {
        Message message = messageRepository.findByMsgId(msgId);
        if (message != null) {
            message.setStatus(status);
            messageRepository.save(message);
        }
    }
    
    /**
     * 标记消息为已读
     */
    @Transactional
    public void markAsRead(Long toUserId, Long fromUserId) {
        messageRepository.markMessagesAsRead(toUserId, fromUserId);
    }
    
    /**
     * 获取未读消息数
     */
    public int getUnreadCount(Long userId) {
        return messageRepository.countByToUserIdAndStatus(userId, 1);
    }
    
    /**
     * 根据消息ID查询消息
     */
    public Message getMessageById(String msgId) {
        return messageRepository.findByMsgId(msgId);
    }
    
    /**
     * 撤回消息
     */
    @Transactional
    public Message recallMessage(String msgId, Long userId) {
        Message message = messageRepository.findByMsgId(msgId);
        if (message == null) {
            throw new RuntimeException("消息不存在");
        }
        
        // 检查是否是发送者
        if (!message.getFromUserId().equals(userId)) {
            throw new RuntimeException("只能撤回自己发送的消息");
        }
        
        // 检查消息是否超过2分钟（不能撤回超过2分钟的消息）
        if (message.getCreateTime().plusMinutes(2).isBefore(java.time.LocalDateTime.now())) {
            throw new RuntimeException("消息已超过2分钟，无法撤回");
        }
        
        // 检查消息是否已经被撤回
        if (message.getStatus() == 6) {
            throw new RuntimeException("消息已经被撤回");
        }
        
        // 设置为已撤回状态
        message.setStatus(6); // 已撤回
        message.setIsRecalled(true);
        message.setRecallTime(java.time.LocalDateTime.now());
        
        return messageRepository.save(message);
    }
    
    /**
     * 获取会话的最近消息
     */
    public Message getLatestMessage(Long chatId, Integer chatType) {
        PageRequest pageRequest = PageRequest.of(0, 1, Sort.by(Sort.Direction.DESC, "createTime"));
        Page<Message> messages = messageRepository.findByChatTypeAndChatId(chatType, chatId, pageRequest);
        return messages.hasContent() ? messages.getContent().get(0) : null;
    }
    
    /**
     * 统计用户发送的消息数
     */
    public long countMessagesByUser(Long userId) {
        return messageRepository.countByFromUserId(userId);
    }
}
