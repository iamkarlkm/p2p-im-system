package com.im.message.service.impl;

import com.im.message.dto.SelfDestructMessageDTO;
import com.im.message.model.SelfDestructMessage;
import com.im.message.repository.SelfDestructMessageRepository;
import com.im.message.service.SelfDestructMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 阅后即焚消息服务实现
 * 
 * @author IM Development Team
 * @since 1.0.0
 */
@Service
public class SelfDestructMessageServiceImpl implements SelfDestructMessageService {

    private static final Logger logger = LoggerFactory.getLogger(SelfDestructMessageServiceImpl.class);

    private final SelfDestructMessageRepository messageRepository;

    @Autowired
    public SelfDestructMessageServiceImpl(SelfDestructMessageRepository messageRepository) {
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional
    public SelfDestructMessageDTO createMessage(String senderId, SelfDestructMessageDTO.CreateRequest request) {
        logger.info("Creating self-destruct message from {} to {}", senderId, request.getReceiverId());

        SelfDestructMessage message = new SelfDestructMessage();
        message.setConversationId(request.getConversationId());
        message.setSenderId(senderId);
        message.setReceiverId(request.getReceiverId());
        message.setMessageContent(request.getMessageContent());
        message.setDurationSeconds(request.getDurationSeconds());
        message.setAllowForward(request.getAllowForward() != null ? request.getAllowForward() : false);
        message.setAllowScreenshot(request.getAllowScreenshot() != null ? request.getAllowScreenshot() : false);
        message.setBlurPreview(request.getBlurPreview() != null ? request.getBlurPreview() : true);
        
        if (request.getNotificationMessage() != null) {
            message.setNotificationMessage(request.getNotificationMessage());
        }

        // 设置内容类型
        try {
            message.setContentType(SelfDestructMessage.ContentType.valueOf(request.getContentType()));
        } catch (IllegalArgumentException e) {
            message.setContentType(SelfDestructMessage.ContentType.TEXT);
        }

        SelfDestructMessage saved = messageRepository.save(message);
        logger.info("Self-destruct message created: {}", saved.getId());

        return SelfDestructMessageDTO.fromEntityForSender(saved);
    }

    @Override
    @Transactional
    public SelfDestructMessageDTO.ReadResponse readMessage(String messageId, String receiverId) {
        logger.info("Reading self-destruct message: {} by receiver: {}", messageId, receiverId);

        Optional<SelfDestructMessage> optional = messageRepository.findByIdAndReceiverId(messageId, receiverId);
        if (optional.isEmpty()) {
            throw new RuntimeException("Message not found or access denied");
        }

        SelfDestructMessage message = optional.get();

        // 检查是否已销毁
        if (message.getIsDestroyed()) {
            throw new RuntimeException("Message has been destroyed");
        }

        // 标记为已读
        if (!message.getIsRead()) {
            message.markAsRead();
            messageRepository.save(message);
            logger.info("Message {} marked as read, will be destroyed at {}", messageId, message.getScheduledDestroyAt());
        }

        SelfDestructMessageDTO.ReadResponse response = new SelfDestructMessageDTO.ReadResponse();
        response.setMessageId(messageId);
        response.setMessageContent(message.getMessageContent());
        response.setRemainingSeconds(message.getRemainingSeconds());
        response.setDurationSeconds(message.getDurationSeconds());
        response.setAllowScreenshot(message.getAllowScreenshot());

        return response;
    }

    @Override
    public SelfDestructMessageDTO getMessageForSender(String messageId, String senderId) {
        Optional<SelfDestructMessage> optional = messageRepository.findByIdAndSenderId(messageId, senderId);
        if (optional.isEmpty()) {
            throw new RuntimeException("Message not found or access denied");
        }

        return SelfDestructMessageDTO.fromEntityForSender(optional.get());
    }

    @Override
    public SelfDestructMessageDTO getMessageForReceiver(String messageId, String receiverId) {
        Optional<SelfDestructMessage> optional = messageRepository.findByIdAndReceiverId(messageId, receiverId);
        if (optional.isEmpty()) {
            throw new RuntimeException("Message not found or access denied");
        }

        SelfDestructMessage message = optional.get();
        boolean canViewContent = message.getIsRead() && !message.isExpired();

        return SelfDestructMessageDTO.fromEntityForReceiver(message, canViewContent);
    }

    @Override
    public List<SelfDestructMessageDTO> getMessagesByConversation(String conversationId, String userId) {
        List<SelfDestructMessage> messages = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId);

        return messages.stream()
                .filter(m -> !m.getIsDestroyed())
                .map(m -> {
                    boolean isSender = m.getSenderId().equals(userId);
                    if (isSender) {
                        return SelfDestructMessageDTO.fromEntityForSender(m);
                    } else {
                        boolean canView = m.getIsRead() && !m.isExpired();
                        return SelfDestructMessageDTO.fromEntityForReceiver(m, canView);
                    }
                })
                .collect(Collectors.toList());
    }

    @Override
    public Page<SelfDestructMessageDTO> getMessagesByConversation(String conversationId, String userId, Pageable pageable) {
        Page<SelfDestructMessage> page = messageRepository.findByConversationIdOrderByCreatedAtDesc(conversationId, pageable);

        List<SelfDestructMessageDTO> dtos = page.getContent().stream()
                .filter(m -> !m.getIsDestroyed())
                .map(m -> {
                    boolean isSender = m.getSenderId().equals(userId);
                    if (isSender) {
                        return SelfDestructMessageDTO.fromEntityForSender(m);
                    } else {
                        boolean canView = m.getIsRead() && !m.isExpired();
                        return SelfDestructMessageDTO.fromEntityForReceiver(m, canView);
                    }
                })
                .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, page.getTotalElements());
    }

    @Override
    public List<SelfDestructMessageDTO> getSentMessages(String senderId) {
        return messageRepository.findBySenderIdOrderByCreatedAtDesc(senderId).stream()
                .map(SelfDestructMessageDTO::fromEntityForSender)
                .collect(Collectors.toList());
    }

    @Override
    public List<SelfDestructMessageDTO> getReceivedMessages(String receiverId) {
        return messageRepository.findByReceiverIdOrderByCreatedAtDesc(receiverId).stream()
                .map(m -> SelfDestructMessageDTO.fromEntityForReceiver(m, false))
                .collect(Collectors.toList());
    }

    @Override
    public Long getUnreadCount(String receiverId) {
        return messageRepository.countUnreadByReceiverId(receiverId);
    }

    @Override
    public Long getUnreadCountByConversation(String conversationId, String receiverId) {
        return messageRepository.countUnreadByConversationAndReceiver(conversationId, receiverId);
    }

    @Override
    @Transactional
    public void deleteMessage(String messageId, String senderId) {
        Optional<SelfDestructMessage> optional = messageRepository.findByIdAndSenderId(messageId, senderId);
        if (optional.isEmpty()) {
            throw new RuntimeException("Message not found or access denied");
        }

        SelfDestructMessage message = optional.get();
        message.markAsDestroyed();
        messageRepository.save(message);
        logger.info("Message {} deleted by sender", messageId);
    }

    @Override
    @Transactional
    public SelfDestructMessageDTO.ScreenshotDetectResponse detectScreenshot(String messageId, 
            SelfDestructMessageDTO.ScreenshotDetectRequest request, String detectorId) {
        
        Optional<SelfDestructMessage> optional = messageRepository.findById(messageId);
        if (optional.isEmpty()) {
            throw new RuntimeException("Message not found");
        }

        SelfDestructMessage message = optional.get();

        // 只有发送者能查看截图记录
        if (!message.getSenderId().equals(detectorId)) {
            throw new RuntimeException("Access denied");
        }

        // 更新截图检测
        messageRepository.recordScreenshot(messageId, LocalDateTime.now());
        message.recordScreenshot();

        SelfDestructMessageDTO.ScreenshotDetectResponse response = new SelfDestructMessageDTO.ScreenshotDetectResponse();
        response.setDetected(true);
        response.setTotalCount(message.getScreenshotCount());
        response.setWarningMessage("Screenshot detected! Total count: " + message.getScreenshotCount());

        logger.warn("Screenshot detected for message {}. Total count: {}", messageId, message.getScreenshotCount());

        return response;
    }

    @Override
    public List<SelfDestructMessageDTO> getScreenshotDetectedMessages(String senderId) {
        return messageRepository.findScreenshotDetectedBySender(senderId).stream()
                .map(SelfDestructMessageDTO::fromEntityForSender)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void destroyMessage(String messageId, String userId) {
        Optional<SelfDestructMessage> optional = messageRepository.findById(messageId);
        if (optional.isEmpty()) {
            throw new RuntimeException("Message not found");
        }

        SelfDestructMessage message = optional.get();

        // 发送者或接收者都可以销毁
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            throw new RuntimeException("Access denied");
        }

        message.markAsDestroyed();
        messageRepository.save(message);
        logger.info("Message {} manually destroyed by user {}", messageId, userId);
    }

    @Override
    public Boolean isMessageDestroyed(String messageId) {
        Optional<SelfDestructMessage> optional = messageRepository.findById(messageId);
        return optional.map(SelfDestructMessage::getIsDestroyed).orElse(true);
    }

    @Override
    public Integer getRemainingSeconds(String messageId, String userId) {
        Optional<SelfDestructMessage> optional = messageRepository.findById(messageId);
        if (optional.isEmpty()) {
            return 0;
        }

        SelfDestructMessage message = optional.get();
        if (!message.getSenderId().equals(userId) && !message.getReceiverId().equals(userId)) {
            return 0;
        }

        return message.getRemainingSeconds();
    }

    @Override
    @Transactional
    public void cleanupOldDestroyedMessages() {
        LocalDateTime beforeDate = LocalDateTime.now().minusDays(7);
        int deleted = messageRepository.deleteOldDestroyedMessages(beforeDate);
        logger.info("Cleaned up {} old destroyed messages", deleted);
    }

    @Override
    @Transactional
    public void processExpiredMessages() {
        List<SelfDestructMessage> expired = messageRepository.findExpiredMessages(LocalDateTime.now());
        
        for (SelfDestructMessage message : expired) {
            message.markAsDestroyed();
            messageRepository.save(message);
            logger.info("Auto-destroyed expired message: {}", message.getId());
        }

        if (!expired.isEmpty()) {
            logger.info("Processed {} expired messages", expired.size());
        }
    }
}
