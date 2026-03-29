package com.im.service;

import com.im.dto.MessageExpirationDTO;
import com.im.dto.MessageExpirationRequest;
import com.im.entity.MessageExpirationEntity;
import com.im.repository.MessageExpirationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息过期服务
 * 处理会话级过期策略和阅后即焚模式
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class MessageExpirationService {

    private final MessageExpirationRepository expirationRepository;

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Transactional
    public MessageExpirationDTO setExpiration(String userId, MessageExpirationRequest request) {
        MessageExpirationEntity entity;

        if (request.getConversationId() != null) {
            // 群聊会话级策略
            entity = expirationRepository.findByConversationId(request.getConversationId())
                    .orElseGet(() -> MessageExpirationEntity.builder()
                            .conversationId(request.getConversationId())
                            .createdBy(userId)
                            .build());
        } else {
            // 私聊策略 (基于 sender + receiver)
            List<MessageExpirationEntity> existing = expirationRepository
                    .findBySenderIdAndReceiverId(userId, request.getReceiverId());
            entity = existing.isEmpty() ? MessageExpirationEntity.builder()
                    .senderId(userId)
                    .receiverId(request.getReceiverId())
                    .createdBy(userId)
                    .build() : existing.get(0);
        }

        entity.setExpirationType(request.getExpirationType());
        entity.setDurationSeconds(request.getDurationSeconds() != null ? request.getDurationSeconds() : 0L);
        entity.setEnabled(request.getEnabled());

        if (request.getExpireAt() != null) {
            entity.setExpireAt(LocalDateTime.parse(request.getExpireAt(), FORMATTER));
        }

        entity = expirationRepository.save(entity);
        log.info("消息过期策略设置: userId={}, type={}, conversationId={}",
                userId, request.getExpirationType(), request.getConversationId());
        return MessageExpirationDTO.fromEntity(entity);
    }

    public MessageExpirationDTO getConversationExpiration(String conversationId) {
        return expirationRepository.findByConversationId(conversationId)
                .map(MessageExpirationDTO::fromEntity)
                .orElse(MessageExpirationDTO.builder()
                        .conversationId(conversationId)
                        .expirationType("OFF")
                        .enabled(false)
                        .build());
    }

    public MessageExpirationDTO getPrivateExpiration(String senderId, String receiverId) {
        List<MessageExpirationEntity> list = expirationRepository.findBySenderIdAndReceiverId(senderId, receiverId);
        if (list.isEmpty()) {
            // 也检查反向
            list = expirationRepository.findBySenderIdAndReceiverId(receiverId, senderId);
        }
        return list.isEmpty() ? null : MessageExpirationDTO.fromEntity(list.get(0));
    }

    @Transactional
    public void deleteExpiration(String conversationId) {
        expirationRepository.deleteByConversationId(conversationId);
        log.info("消息过期策略删除: conversationId={}", conversationId);
    }

    /** 检查消息是否需要过期处理 */
    public boolean shouldExpireMessage(String conversationId, LocalDateTime messageTime) {
        return expirationRepository.findByConversationId(conversationId)
                .filter(e -> e.getEnabled())
                .map(e -> {
                    if ("DURATION".equals(e.getExpirationType())) {
                        return messageTime.plusSeconds(e.getDurationSeconds()).isBefore(LocalDateTime.now());
                    }
                    if ("SCHEDULE".equals(e.getExpirationType())) {
                        return e.getExpireAt() != null && e.getExpireAt().isBefore(LocalDateTime.now());
                    }
                    return false;
                }).orElse(false);
    }

    /** 获取所有过期时间表 (定时任务调用) */
    public List<MessageExpirationEntity> findExpiredSchedules() {
        return expirationRepository.findExpiredSchedules(LocalDateTime.now());
    }
}
