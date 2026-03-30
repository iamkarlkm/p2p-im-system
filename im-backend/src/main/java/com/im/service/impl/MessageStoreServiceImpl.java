package com.im.service.impl;

import com.im.entity.ChatMessage;
import com.im.service.IMessageStoreService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 消息存储服务实现类
 * 功能 #6: 消息存储与检索引擎
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Service
public class MessageStoreServiceImpl implements IMessageStoreService {
    
    // 消息存储
    private final Map<String, ChatMessage> messages = new ConcurrentHashMap<>();
    
    // 会话消息索引
    private final Map<String, List<String>> conversationIndex = new ConcurrentHashMap<>();
    
    // 用户消息索引
    private final Map<String, List<String>> userMessageIndex = new ConcurrentHashMap<>();
    
    // 已读状态
    private final Map<String, Set<String>> readStatus = new ConcurrentHashMap<>();
    
    // 删除状态（用户对某消息的删除）
    private final Map<String, Set<String>> deletedStatus = new ConcurrentHashMap<>();
    
    @Override
    public ChatMessage saveMessage(ChatMessage message) {
        if (message.getMessageId() == null) {
            message.setMessageId(UUID.randomUUID().toString());
        }
        
        messages.put(message.getMessageId(), message);
        
        // 更新会话索引
        conversationIndex.computeIfAbsent(message.getConversationId(), k -> new ArrayList<>())
            .add(message.getMessageId());
        
        // 更新用户索引
        userMessageIndex.computeIfAbsent(message.getSenderId(), k -> new ArrayList<>())
            .add(message.getMessageId());
        userMessageIndex.computeIfAbsent(message.getReceiverId(), k -> new ArrayList<>())
            .add(message.getMessageId());
        
        return message;
    }
    
    @Override
    public ChatMessage getMessageById(String messageId) {
        return messages.get(messageId);
    }
    
    @Override
    public List<ChatMessage> getConversationHistory(String conversationId, int limit, long beforeTimestamp) {
        List<String> messageIds = conversationIndex.getOrDefault(conversationId, Collections.emptyList());
        
        return messageIds.stream()
            .map(messages::get)
            .filter(Objects::nonNull)
            .filter(m -> !m.getRecalled())
            .sorted(Comparator.comparing(ChatMessage::getSendTime).reversed())
            .limit(limit)
            .sorted(Comparator.comparing(ChatMessage::getSendTime))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<ChatMessage> searchMessages(String userId, String keyword, LocalDateTime startTime, LocalDateTime endTime) {
        List<String> messageIds = userMessageIndex.getOrDefault(userId, Collections.emptyList());
        
        return messageIds.stream()
            .map(messages::get)
            .filter(Objects::nonNull)
            .filter(m -> m.getContent() != null && m.getContent().contains(keyword))
            .filter(m -> startTime == null || !m.getSendTime().isBefore(startTime))
            .filter(m -> endTime == null || !m.getSendTime().isAfter(endTime))
            .sorted(Comparator.comparing(ChatMessage::getSendTime).reversed())
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean recallMessage(String messageId, String operatorId) {
        ChatMessage message = messages.get(messageId);
        if (message == null) return false;
        
        // 只能撤回自己发送的消息
        if (!message.getSenderId().equals(operatorId)) return false;
        
        // 检查是否在允许时间内（2分钟）
        if (!message.canRecall(2)) return false;
        
        message.recall("用户撤回");
        return true;
    }
    
    @Override
    public boolean deleteMessageForUser(String messageId, String userId) {
        deletedStatus.computeIfAbsent(userId, k -> ConcurrentHashMap.newKeySet()).add(messageId);
        return true;
    }
    
    @Override
    public boolean markMessageRead(String messageId, String userId) {
        ChatMessage message = messages.get(messageId);
        if (message == null) return false;
        
        readStatus.computeIfAbsent(messageId, k -> ConcurrentHashMap.newKeySet()).add(userId);
        message.markRead();
        return true;
    }
    
    @Override
    public int getUnreadCount(String userId, String conversationId) {
        List<String> messageIds = conversationIndex.getOrDefault(conversationId, Collections.emptyList());
        
        return (int) messageIds.stream()
            .map(messages::get)
            .filter(Objects::nonNull)
            .filter(m -> !m.getSenderId().equals(userId))
            .filter(m -> !isRead(m.getMessageId(), userId))
            .count();
    }
    
    @Override
    public int getTotalUnreadCount(String userId) {
        List<String> messageIds = userMessageIndex.getOrDefault(userId, Collections.emptyList());
        
        return (int) messageIds.stream()
            .map(messages::get)
            .filter(Objects::nonNull)
            .filter(m -> !m.getSenderId().equals(userId))
            .filter(m -> !isRead(m.getMessageId(), userId))
            .count();
    }
    
    @Override
    public int cleanupExpiredMessages(int days) {
        LocalDateTime before = LocalDateTime.now().minusDays(days);
        
        List<String> toDelete = messages.values().stream()
            .filter(m -> m.getSendTime().isBefore(before))
            .map(ChatMessage::getMessageId)
            .collect(Collectors.toList());
        
        toDelete.forEach(messages::remove);
        
        // 清理索引
        conversationIndex.values().forEach(list -> list.removeAll(toDelete));
        userMessageIndex.values().forEach(list -> list.removeAll(toDelete));
        
        return toDelete.size();
    }
    
    private boolean isRead(String messageId, String userId) {
        return readStatus.getOrDefault(messageId, Collections.emptySet()).contains(userId);
    }
}
