package com.im.backend.service.impl;

import com.im.backend.dto.ScheduledMessageDTO;
import com.im.backend.mapper.ScheduledMessageMapper;
import com.im.backend.model.ScheduledMessage;
import com.im.backend.repository.ScheduledMessageRepository;
import com.im.backend.service.ScheduledMessageService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 定时消息服务实现类
 */
@Service
public class ScheduledMessageServiceImpl implements ScheduledMessageService {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledMessageServiceImpl.class);

    @Autowired
    private ScheduledMessageRepository scheduledMessageRepository;

    @Autowired
    private ScheduledMessageMapper scheduledMessageMapper;

    @Override
    @Transactional
    public ScheduledMessageDTO createScheduledMessage(Long senderId, ScheduledMessageDTO dto) {
        // 验证定时时间必须在未来
        if (dto.getScheduledTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("定时时间必须在未来");
        }

        ScheduledMessage message = new ScheduledMessage();
        message.setSenderId(senderId);
        message.setReceiverId(dto.getReceiverId());
        message.setContent(dto.getContent());
        message.setScheduledTime(dto.getScheduledTime());
        message.setStatus(ScheduledMessage.Status.PENDING);

        ScheduledMessage saved = scheduledMessageRepository.save(message);
        logger.info("创建定时消息成功: id={}, sender={}, scheduledTime={}", 
                saved.getId(), senderId, saved.getScheduledTime());
        
        return scheduledMessageMapper.toDTO(saved);
    }

    @Override
    @Transactional
    public ScheduledMessageDTO cancelScheduledMessage(Long messageId, Long senderId) {
        ScheduledMessage message = scheduledMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("定时消息不存在"));

        // 验证权限
        if (!message.getSenderId().equals(senderId)) {
            throw new RuntimeException("无权操作此消息");
        }

        // 只能取消待发送的消息
        if (message.getStatus() != ScheduledMessage.Status.PENDING) {
            throw new RuntimeException("只能取消待发送的消息");
        }

        message.setStatus(ScheduledMessage.Status.CANCELLED);
        ScheduledMessage updated = scheduledMessageRepository.save(message);
        
        logger.info("取消定时消息成功: id={}", messageId);
        return scheduledMessageMapper.toDTO(updated);
    }

    @Override
    @Transactional
    public void deleteScheduledMessage(Long messageId, Long senderId) {
        ScheduledMessage message = scheduledMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("定时消息不存在"));

        if (!message.getSenderId().equals(senderId)) {
            throw new RuntimeException("无权删除此消息");
        }

        scheduledMessageRepository.delete(message);
        logger.info("删除定时消息成功: id={}", messageId);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ScheduledMessageDTO> getScheduledMessage(Long messageId, Long senderId) {
        return scheduledMessageRepository.findById(messageId)
                .filter(m -> m.getSenderId().equals(senderId))
                .map(scheduledMessageMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScheduledMessageDTO> getUserScheduledMessages(Long senderId, Pageable pageable) {
        return scheduledMessageRepository.findBySenderIdOrderByScheduledTimeDesc(senderId, pageable)
                .map(scheduledMessageMapper::toDTO);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ScheduledMessageDTO> getUserScheduledMessagesByStatus(
            Long senderId, ScheduledMessage.Status status, Pageable pageable) {
        return scheduledMessageRepository.findBySenderIdAndStatusOrderByScheduledTimeDesc(senderId, status, pageable)
                .map(scheduledMessageMapper::toDTO);
    }

    @Override
    @Transactional
    public ScheduledMessageDTO updateScheduledMessage(Long messageId, Long senderId, ScheduledMessageDTO dto) {
        ScheduledMessage message = scheduledMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("定时消息不存在"));

        if (!message.getSenderId().equals(senderId)) {
            throw new RuntimeException("无权修改此消息");
        }

        // 只能修改待发送的消息
        if (message.getStatus() != ScheduledMessage.Status.PENDING) {
            throw new RuntimeException("只能修改待发送的消息");
        }

        // 验证新的定时时间
        if (dto.getScheduledTime().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("定时时间必须在未来");
        }

        message.setReceiverId(dto.getReceiverId());
        message.setContent(dto.getContent());
        message.setScheduledTime(dto.getScheduledTime());

        ScheduledMessage updated = scheduledMessageRepository.save(message);
        logger.info("更新定时消息成功: id={}", messageId);
        
        return scheduledMessageMapper.toDTO(updated);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ScheduledMessageDTO> getPendingMessagesForSending() {
        LocalDateTime now = LocalDateTime.now();
        return scheduledMessageRepository.findPendingMessagesBefore(now)
                .stream()
                .map(scheduledMessageMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void markAsSent(Long messageId) {
        ScheduledMessage message = scheduledMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("定时消息不存在"));
        
        message.setStatus(ScheduledMessage.Status.SENT);
        message.setSentTime(LocalDateTime.now());
        scheduledMessageRepository.save(message);
        
        logger.info("标记定时消息为已发送: id={}", messageId);
    }

    @Override
    @Transactional
    public void markAsFailed(Long messageId, String reason) {
        ScheduledMessage message = scheduledMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("定时消息不存在"));
        
        message.setStatus(ScheduledMessage.Status.FAILED);
        message.setFailureReason(reason);
        scheduledMessageRepository.save(message);
        
        logger.error("标记定时消息为发送失败: id={}, reason={}", messageId, reason);
    }

    @Override
    @Transactional(readOnly = true)
    public long countPendingMessages(Long senderId) {
        return scheduledMessageRepository.countBySenderIdAndStatus(senderId, ScheduledMessage.Status.PENDING);
    }
}
