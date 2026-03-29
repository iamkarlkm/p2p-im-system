package com.im.server.service;

import com.im.server.entity.ReadReceipt;
import com.im.server.entity.Message;
import com.im.server.repository.ReadReceiptRepository;
import com.im.server.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 消息已读回执服务
 * 
 * 功能特性：
 * 1. 单聊消息已读状态跟踪
 * 2. 群聊消息已读列表（谁读了这条消息）
 * 3. 未读消息计数（会话级别）
 * 4. 已读回执推送（WebSocket）
 * 5. 已读状态同步
 */
@Service
public class ReadReceiptService {

    @Autowired
    private ReadReceiptRepository repository;

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private static final String UNREAD_COUNT_KEY = "unread:count:";
    private static final String READ_RECEIPTS_KEY = "read:receipts:";

    /**
     * 标记消息已读
     */
    @Transactional
    public ReadReceipt markAsRead(Long messageId, Long userId) {
        // 检查是否已经读过
        Optional<ReadReceipt> existing = repository.findByMessageIdAndUserId(messageId, userId);
        if (existing.isPresent()) {
            return existing.get();
        }

        // 获取消息
        Optional<Message> messageOpt = messageRepository.findById(messageId);
        if (messageOpt.isEmpty()) {
            throw new IllegalArgumentException("消息不存在");
        }

        Message message = messageOpt.get();

        // 创建已读回执
        ReadReceipt receipt = new ReadReceipt();
        receipt.setMessageId(messageId);
        receipt.setUserId(userId);
        receipt.setConversationId(message.getConversationId());
        receipt.setReadAt(Instant.now());
        receipt.setReadStatus("READ");

        ReadReceipt saved = repository.save(receipt);

        // 更新未读计数
        decrementUnreadCount(message.getConversationId(), userId);

        // 更新Redis缓存
        String cacheKey = READ_RECEIPTS_KEY + messageId;
        redisTemplate.opsForSet().add(cacheKey, userId.toString());

        // 通知发送者（通过WebSocket）
        notifyMessageRead(message.getSenderId(), messageId, userId);

        return saved;
    }

    /**
     * 批量标记已读（标记会话中的所有消息为已读）
     */
    @Transactional
    public List<ReadReceipt> markConversationAsRead(Long conversationId, Long userId) {
        // 获取会话中所有未读消息
        List<Message> unreadMessages = messageRepository.findUnreadMessages(conversationId, userId);
        
        List<ReadReceipt> receipts = new ArrayList<>();
        for (Message message : unreadMessages) {
            if (!message.getSenderId().equals(userId)) {
                // 不标记自己发送的消息
                try {
                    ReadReceipt receipt = markAsRead(message.getId(), userId);
                    receipts.add(receipt);
                } catch (Exception e) {
                    // 忽略已存在的回执
                }
            }
        }
        
        return receipts;
    }

    /**
     * 获取消息的已读用户列表（群聊）
     */
    public List<ReadReceipt> getReadReceipts(Long messageId) {
        return repository.findByMessageId(messageId);
    }

    /**
     * 获取已读用户ID列表
     */
    public List<Long> getReadUserIds(Long messageId) {
        List<ReadReceipt> receipts = repository.findByMessageId(messageId);
        return receipts.stream()
                .map(ReadReceipt::getUserId)
                .collect(Collectors.toList());
    }

    /**
     * 检查消息是否已被某用户阅读
     */
    public boolean isReadByUser(Long messageId, Long userId) {
        // 先检查缓存
        String cacheKey = READ_RECEIPTS_KEY + messageId;
        Boolean isMember = redisTemplate.opsForSet().isMember(cacheKey, userId.toString());
        if (isMember != null && isMember) {
            return true;
        }

        // 查询数据库
        return repository.findByMessageIdAndUserId(messageId, userId).isPresent();
    }

    /**
     * 获取会话未读消息数量
     */
    public long getUnreadCount(Long conversationId, Long userId) {
        String key = UNREAD_COUNT_KEY + conversationId + ":" + userId;
        
        // 先从Redis获取
        Object cached = redisTemplate.opsForValue().get(key);
        if (cached != null) {
            return Long.parseLong(cached.toString());
        }

        // 从数据库查询
        long count = messageRepository.countUnreadMessages(conversationId, userId);
        
        // 缓存结果
        redisTemplate.opsForValue().set(key, count);
        
        return count;
    }

    /**
     * 获取多个会话的未读数量
     */
    public Map<Long, Long> getUnreadCounts(List<Long> conversationIds, Long userId) {
        Map<Long, Long> counts = new HashMap<>();
        
        for (Long conversationId : conversationIds) {
            counts.put(conversationId, getUnreadCount(conversationId, userId));
        }
        
        return counts;
    }

    /**
     * 获取会话的最后已读消息ID
     */
    public Long getLastReadMessageId(Long conversationId, Long userId) {
        return repository.findLastReadMessageId(conversationId, userId);
    }

    /**
     * 获取用户的所有未读会话
     */
    public List<Long> getUnreadConversations(Long userId) {
        return repository.findUnreadConversations(userId);
    }

    /**
     * 减少未读计数
     */
    private void decrementUnreadCount(Long conversationId, Long userId) {
        String key = UNREAD_COUNT_KEY + conversationId + ":" + userId;
        
        Long current = (Long) redisTemplate.opsForValue().get(key);
        if (current != null && current > 0) {
            redisTemplate.opsForValue().decrement(key);
        }
    }

    /**
     * 重置未读计数
     */
    public void resetUnreadCount(Long conversationId, Long userId) {
        String key = UNREAD_COUNT_KEY + conversationId + ":" + userId;
        
        // 从数据库重新计算
        long count = messageRepository.countUnreadMessages(conversationId, userId);
        
        // 更新缓存
        redisTemplate.opsForValue().set(key, count);
    }

    /**
     * 通知发送者消息已被阅读
     */
    private void notifyMessageRead(Long senderId, Long messageId, Long readerId) {
        // 通过WebSocket发送通知
        // TODO: 实现WebSocket推送
        System.out.println("消息已读通知: messageId=" + messageId + ", readerId=" + readerId);
    }

    /**
     * 获取消息的已读统计
     */
    public ReadStatistics getReadStatistics(Long messageId) {
        List<ReadReceipt> receipts = repository.findByMessageId(messageId);
        
        ReadStatistics stats = new ReadStatistics();
        stats.messageId = messageId;
        stats.totalReads = receipts.size();
        stats.readUserIds = receipts.stream()
                .map(ReadReceipt::getUserId)
                .collect(Collectors.toList());
        stats.firstReadAt = receipts.stream()
                .map(ReadReceipt::getReadAt)
                .min(Instant::compareTo)
                .orElse(null);
        stats.lastReadAt = receipts.stream()
                .map(ReadReceipt::getReadAt)
                .max(Instant::compareTo)
                .orElse(null);
        
        return stats;
    }

    /**
     * 清除用户的所有未读计数
     */
    public void clearAllUnreadCounts(Long userId) {
        // 清除所有相关的Redis缓存
        Set<String> keys = redisTemplate.keys(UNREAD_COUNT_KEY + "*:" + userId);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    /**
     * 已读统计内部类
     */
    public static class ReadStatistics {
        public Long messageId;
        public int totalReads;
        public List<Long> readUserIds;
        public Instant firstReadAt;
        public Instant lastReadAt;
    }
}
