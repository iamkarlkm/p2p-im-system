package com.im.backend.service;

import com.im.backend.dto.MessageReadReceiptResponse;
import com.im.backend.entity.GroupMember;
import com.im.backend.entity.MessageReadReceipt;
import com.im.backend.repository.GroupMemberRepository;
import com.im.backend.repository.MessageReadReceiptRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息已读回执服务类
 * 对应功能 #16 - 消息已读回执功能
 */
@Service
public class MessageReadReceiptService {

    @Autowired
    private MessageReadReceiptRepository receiptRepository;
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    @Autowired
    private SimpMessagingTemplate messagingTemplate;
    
    /**
     * 标记私聊消息已读
     */
    @Transactional
    public void markPrivateMessageRead(Long messageId, Long userId, Long conversationId) {
        // 检查是否已存在
        if (receiptRepository.existsByMessageIdAndUserId(messageId, userId)) {
            return;
        }
        
        MessageReadReceipt receipt = new MessageReadReceipt();
        receipt.setMessageId(messageId);
        receipt.setUserId(userId);
        receipt.setConversationType(MessageReadReceipt.ConversationType.PRIVATE);
        receipt.setConversationId(conversationId);
        receipt.setReadAt(LocalDateTime.now());
        
        receiptRepository.save(receipt);
        
        // 发送已读回执通知给消息发送者
        sendReadReceiptNotification(messageId, userId);
    }
    
    /**
     * 标记群聊消息已读
     */
    @Transactional
    public void markGroupMessageRead(Long messageId, Long userId, Long groupId) {
        // 检查是否是群成员
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new RuntimeException("不是群成员");
        }
        
        // 检查是否已存在
        if (receiptRepository.existsByMessageIdAndUserId(messageId, userId)) {
            return;
        }
        
        MessageReadReceipt receipt = new MessageReadReceipt();
        receipt.setMessageId(messageId);
        receipt.setUserId(userId);
        receipt.setConversationType(MessageReadReceipt.ConversationType.GROUP);
        receipt.setConversationId(groupId);
        receipt.setReadAt(LocalDateTime.now());
        
        receiptRepository.save(receipt);
        
        // 发送已读回执通知给消息发送者
        sendReadReceiptNotification(messageId, userId);
    }
    
    /**
     * 批量标记会话消息已读
     */
    @Transactional
    public void markConversationMessagesRead(List<Long> messageIds, Long userId, 
                                              MessageReadReceipt.ConversationType type,
                                              Long conversationId) {
        for (Long messageId : messageIds) {
            if (!receiptRepository.existsByMessageIdAndUserId(messageId, userId)) {
                MessageReadReceipt receipt = new MessageReadReceipt();
                receipt.setMessageId(messageId);
                receipt.setUserId(userId);
                receipt.setConversationType(type);
                receipt.setConversationId(conversationId);
                receipt.setReadAt(LocalDateTime.now());
                receiptRepository.save(receipt);
            }
        }
    }
    
    /**
     * 获取消息的已读回执详情
     */
    public MessageReadReceiptResponse getMessageReadReceipt(Long messageId) {
        List<MessageReadReceipt> receipts = receiptRepository.findByMessageId(messageId);
        
        MessageReadReceiptResponse response = new MessageReadReceiptResponse();
        response.setMessageId(messageId);
        response.setReadCount(receipts.size());
        
        List<MessageReadReceiptResponse.ReadUserInfo> readUsers = receipts.stream()
            .map(r -> {
                MessageReadReceiptResponse.ReadUserInfo info = new MessageReadReceiptResponse.ReadUserInfo();
                info.setUserId(r.getUserId());
                info.setReadAt(r.getReadAt());
                return info;
            })
            .collect(Collectors.toList());
        
        response.setReadUsers(readUsers);
        
        if (!receipts.isEmpty()) {
            response.setLastReadAt(receipts.stream()
                .map(MessageReadReceipt::getReadAt)
                .max(LocalDateTime::compareTo)
                .orElse(null));
        }
        
        return response;
    }
    
    /**
     * 检查用户是否已读消息
     */
    public boolean isMessageReadByUser(Long messageId, Long userId) {
        return receiptRepository.existsByMessageIdAndUserId(messageId, userId);
    }
    
    /**
     * 获取消息已读数量
     */
    public long getMessageReadCount(Long messageId) {
        return receiptRepository.countByMessageId(messageId);
    }
    
    /**
     * 获取群消息已读统计
     */
    public MessageReadReceiptResponse getGroupMessageReadStats(Long messageId, Long groupId) {
        List<MessageReadReceipt> receipts = receiptRepository.findByMessageId(messageId);
        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        
        MessageReadReceiptResponse response = new MessageReadReceiptResponse();
        response.setMessageId(messageId);
        response.setReadCount(receipts.size());
        response.setTotalCount(members.size());
        
        List<MessageReadReceiptResponse.ReadUserInfo> readUsers = receipts.stream()
            .map(r -> {
                MessageReadReceiptResponse.ReadUserInfo info = new MessageReadReceiptResponse.ReadUserInfo();
                info.setUserId(r.getUserId());
                info.setReadAt(r.getReadAt());
                return info;
            })
            .collect(Collectors.toList());
        
        response.setReadUsers(readUsers);
        
        return response;
    }
    
    /**
     * 发送已读回执通知
     */
    private void sendReadReceiptNotification(Long messageId, Long readerId) {
        // 通过WebSocket发送已读通知
        messagingTemplate.convertAndSend("/topic/message/" + messageId + "/read", 
            new ReadReceiptEvent(messageId, readerId, LocalDateTime.now()));
    }
    
    /**
     * 已读回执事件
     */
    public static class ReadReceiptEvent {
        private Long messageId;
        private Long readerId;
        private LocalDateTime readAt;
        
        public ReadReceiptEvent(Long messageId, Long readerId, LocalDateTime readAt) {
            this.messageId = messageId;
            this.readerId = readerId;
            this.readAt = readAt;
        }
        
        // Getters
        public Long getMessageId() { return messageId; }
        public Long getReaderId() { return readerId; }
        public LocalDateTime getReadAt() { return readAt; }
    }
}
