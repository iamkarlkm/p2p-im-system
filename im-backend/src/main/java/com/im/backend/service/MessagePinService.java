package com.im.backend.service;

import com.im.backend.dto.MessagePinDTO;
import com.im.backend.model.Message;
import com.im.backend.model.PinnedMessage;
import com.im.backend.model.User;
import com.im.backend.repository.MessageRepository;
import com.im.backend.repository.PinnedMessageRepository;
import com.im.backend.repository.UserRepository;
import com.im.backend.service.notification.NotificationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 消息置顶服务
 * 处理消息置顶、取消置顶、排序等功能
 */
@Service
public class MessagePinService {
    
    private static final Logger logger = LoggerFactory.getLogger(MessagePinService.class);
    
    @Autowired
    private PinnedMessageRepository pinnedMessageRepository;
    
    @Autowired
    private MessageRepository messageRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ConversationPermissionService permissionService;
    
    /**
     * 置顶消息
     */
    @Transactional
    public PinnedMessage pinMessage(MessagePinDTO dto, Long userId) {
        logger.info("Pinning message {} in conversation {} by user {}", 
                    dto.getMessageId(), dto.getConversationId(), userId);
        
        // 验证权限
        if (!permissionService.canPinMessage(dto.getConversationId(), userId)) {
            throw new SecurityException("无权置顶此会话的消息");
        }
        
        // 检查消息是否存在
        Message message = messageRepository.findById(dto.getMessageId())
            .orElseThrow(() -> new IllegalArgumentException("消息不存在"));
        
        // 检查是否已置顶
        Optional<PinnedMessage> existingPin = pinnedMessageRepository
            .findActiveByMessageIdAndConversationId(dto.getMessageId(), dto.getConversationId(), LocalDateTime.now());
        
        if (existingPin.isPresent()) {
            throw new IllegalStateException("该消息已被置顶");
        }
        
        // 检查置顶数量限制
        Long currentPinCount = pinnedMessageRepository
            .countActiveByConversationId(dto.getConversationId(), LocalDateTime.now());
        
        if (currentPinCount >= PinnedMessage.MAX_PINS_PER_CONVERSATION) {
            throw new IllegalStateException("置顶消息数量已达上限(50条)");
        }
        
        // 创建置顶记录
        PinnedMessage pinnedMessage = new PinnedMessage();
        pinnedMessage.setConversationId(dto.getConversationId());
        pinnedMessage.setMessageId(dto.getMessageId());
        pinnedMessage.setPinnedBy(userId);
        pinnedMessage.setPinNote(dto.getPinNote());
        pinnedMessage.setExpiresAt(dto.getExpiresAt());
        
        // 计算排序顺序
        Integer maxOrder = pinnedMessageRepository.findMaxPinOrder(dto.getConversationId(), LocalDateTime.now());
        pinnedMessage.setPinOrder(dto.getPinOrder() != null ? dto.getPinOrder() : maxOrder + 1);
        
        PinnedMessage saved = pinnedMessageRepository.save(pinnedMessage);
        
        // 填充关联数据
        saved.setMessage(message);
        userRepository.findById(userId).ifPresent(saved::setPinnedByUser);
        
        // 发送通知
        if (Boolean.TRUE.equals(dto.getSendNotification())) {
            notificationService.sendPinNotification(dto.getConversationId(), userId, message);
        }
        
        logger.info("Message {} pinned successfully, pinId={}", dto.getMessageId(), saved.getId());
        return saved;
    }
    
    /**
     * 取消置顶
     */
    @Transactional
    public void unpinMessage(Long pinId, Long userId) {
        logger.info("Unpinning message {}, user={}", pinId, userId);
        
        PinnedMessage pin = pinnedMessageRepository.findById(pinId)
            .orElseThrow(() -> new IllegalArgumentException("置顶记录不存在"));
        
        // 验证权限
        if (!canManagePin(pin, userId)) {
            throw new SecurityException("无权取消此置顶");
        }
        
        pinnedMessageRepository.deactivateById(pinId);
        
        // 发送取消置顶通知
        notificationService.sendUnpinNotification(pin.getConversationId(), userId, pin.getMessageId());
        
        logger.info("Message unpinned successfully, pinId={}", pinId);
    }
    
    /**
     * 根据消息ID取消置顶
     */
    @Transactional
    public void unpinByMessageId(Long messageId, Long conversationId, Long userId) {
        logger.info("Unpinning message {} in conversation {}, user={}", messageId, conversationId, userId);
        
        Optional<PinnedMessage> pin = pinnedMessageRepository
            .findActiveByMessageIdAndConversationId(messageId, conversationId, LocalDateTime.now());
        
        if (pin.isPresent()) {
            if (!canManagePin(pin.get(), userId)) {
                throw new SecurityException("无权取消此置顶");
            }
            pinnedMessageRepository.deactivateByMessageId(messageId, conversationId);
            notificationService.sendUnpinNotification(conversationId, userId, messageId);
        }
    }
    
    /**
     * 获取会话置顶消息列表
     */
    public List<PinnedMessage> getPinnedMessages(Long conversationId, Long userId) {
        // 验证访问权限
        if (!permissionService.canAccessConversation(conversationId, userId)) {
            throw new SecurityException("无权访问此会话");
        }
        
        List<PinnedMessage> pins = pinnedMessageRepository
            .findActiveByConversationId(conversationId, LocalDateTime.now());
        
        // 填充消息详情
        return populateMessageDetails(pins);
    }
    
    /**
     * 分页获取置顶消息
     */
    public Page<PinnedMessage> getPinnedMessagesPageable(Long conversationId, Long userId, Pageable pageable) {
        if (!permissionService.canAccessConversation(conversationId, userId)) {
            throw new SecurityException("无权访问此会话");
        }
        
        Page<PinnedMessage> pins = pinnedMessageRepository
            .findActiveByConversationIdPageable(conversationId, LocalDateTime.now(), pageable);
        
        return pins.map(pin -> {
            messageRepository.findById(pin.getMessageId()).ifPresent(pin::setMessage);
            userRepository.findById(pin.getPinnedBy()).ifPresent(pin::setPinnedByUser);
            return pin;
        });
    }
    
    /**
     * 更新置顶排序
     */
    @Transactional
    public void updatePinOrder(Long pinId, Integer newOrder, Long userId) {
        logger.info("Updating pin order: pinId={}, newOrder={}, user={}", pinId, newOrder, userId);
        
        PinnedMessage pin = pinnedMessageRepository.findById(pinId)
            .orElseThrow(() -> new IllegalArgumentException("置顶记录不存在"));
        
        if (!canManagePin(pin, userId)) {
            throw new SecurityException("无权修改此置顶");
        }
        
        if (newOrder < 0) {
            throw new IllegalArgumentException("排序顺序不能为负数");
        }
        
        pinnedMessageRepository.updatePinOrder(pinId, newOrder);
        logger.info("Pin order updated successfully");
    }
    
    /**
     * 批量更新排序
     */
    @Transactional
    public void batchUpdatePinOrder(List<MessagePinDTO.PinOrderUpdateDTO> updates, Long userId) {
        logger.info("Batch updating pin orders, count={}", updates.size());
        
        for (MessagePinDTO.PinOrderUpdateDTO update : updates) {
            updatePinOrder(update.getPinId(), update.getNewOrder(), userId);
        }
    }
    
    /**
     * 获取用户所有置顶消息
     */
    public List<PinnedMessage> getUserPinnedMessages(Long userId) {
        List<PinnedMessage> pins = pinnedMessageRepository
            .findActiveByPinnedBy(userId, LocalDateTime.now());
        
        return populateMessageDetails(pins);
    }
    
    /**
     * 检查消息是否已置顶
     */
    public boolean isMessagePinned(Long messageId, Long conversationId) {
        return pinnedMessageRepository
            .findActiveByMessageIdAndConversationId(messageId, conversationId, LocalDateTime.now())
            .isPresent();
    }
    
    /**
     * 取消会话所有置顶
     */
    @Transactional
    public void unpinAllMessages(Long conversationId, Long userId) {
        logger.info("Unpinning all messages in conversation {}, user={}", conversationId, userId);
        
        if (!permissionService.isConversationAdmin(conversationId, userId)) {
            throw new SecurityException("只有管理员可以取消所有置顶");
        }
        
        pinnedMessageRepository.deactivateAllByConversationId(conversationId);
        logger.info("All messages unpinned in conversation {}", conversationId);
    }
    
    /**
     * 清理过期置顶
     */
    @Transactional
    public int cleanExpiredPins() {
        logger.info("Cleaning expired pinned messages");
        int count = pinnedMessageRepository.cleanExpiredPins(LocalDateTime.now());
        logger.info("Cleaned {} expired pins", count);
        return count;
    }
    
    /**
     * 置顶置顶消息
     */
    @Transactional
    public PinnedMessage updatePinNote(Long pinId, String note, Long userId) {
        PinnedMessage pin = pinnedMessageRepository.findById(pinId)
            .orElseThrow(() -> new IllegalArgumentException("置顶记录不存在"));
        
        if (!canManagePin(pin, userId)) {
            throw new SecurityException("无权修改此置顶");
        }
        
        pin.setPinNote(note);
        return pinnedMessageRepository.save(pin);
    }
    
    // 辅助方法
    
    private boolean canManagePin(PinnedMessage pin, Long userId) {
        // 置顶者或管理员可以管理
        return pin.getPinnedBy().equals(userId) || 
               permissionService.isConversationAdmin(pin.getConversationId(), userId);
    }
    
    private List<PinnedMessage> populateMessageDetails(List<PinnedMessage> pins) {
        List<Long> messageIds = pins.stream()
            .map(PinnedMessage::getMessageId)
            .distinct()
            .collect(Collectors.toList());
        
        List<Long> userIds = pins.stream()
            .map(PinnedMessage::getPinnedBy)
            .distinct()
            .collect(Collectors.toList());
        
        List<Message> messages = messageRepository.findAllById(messageIds);
        List<User> users = userRepository.findAllById(userIds);
        
        return pins.stream().map(pin -> {
            messages.stream()
                .filter(m -> m.getId().equals(pin.getMessageId()))
                .findFirst()
                .ifPresent(pin::setMessage);
            
            users.stream()
                .filter(u -> u.getId().equals(pin.getPinnedBy()))
                .findFirst()
                .ifPresent(pin::setPinnedByUser);
            
            return pin;
        }).collect(Collectors.toList());
    }
}
