package com.im.backend.service;

import com.im.backend.dto.TypingStatusDTO;
import com.im.backend.dto.TypingRequest;
import com.im.backend.entity.TypingStatusEntity;
import com.im.backend.repository.TypingStatusRepository;
import com.im.backend.websocket.WebSocketSessionManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TypingStatusService {

    private final TypingStatusRepository typingStatusRepository;
    private final WebSocketSessionManager webSocketSessionManager;
    private final UserService userService;

    private static final long TYPING_EXPIRE_SECONDS = 5L;

    /** 用户发送Typing事件: upsert状态并通过WebSocket广播 */
    @Transactional
    public void handleTyping(String userId, TypingRequest request) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime expiresAt = now.plusSeconds(TYPING_EXPIRE_SECONDS);

        Optional<TypingStatusEntity> existing = typingStatusRepository
                .findByConversationIdAndUserId(request.getConversationId(), userId);

        TypingStatusEntity entity;
        if (existing.isPresent()) {
            entity = existing.get();
            entity.setUpdatedAt(now);
            entity.setExpiresAt(expiresAt);
        } else {
            entity = TypingStatusEntity.builder()
                    .conversationId(request.getConversationId())
                    .userId(userId)
                    .conversationType(request.getConversationType())
                    .updatedAt(now)
                    .expiresAt(expiresAt)
                    .build();
        }
        typingStatusRepository.save(entity);

        // 通过WebSocket广播给会话内其他人
        TypingStatusDTO dto = buildDTO(entity);
        webSocketSessionManager.broadcastToConversation(
                request.getConversationId(),
                buildTypingEvent("typing", dto),
                userId
        );
    }

    /** 停止Typing: 删除状态并通知 */
    @Transactional
    public void handleStopTyping(String userId, TypingRequest request) {
        typingStatusRepository.findByConversationIdAndUserId(request.getConversationId(), userId)
                .ifPresent(entity -> {
                    typingStatusRepository.delete(entity);
                    TypingStatusDTO dto = buildDTO(entity);
                    webSocketSessionManager.broadcastToConversation(
                            request.getConversationId(),
                            buildTypingEvent("stop_typing", dto),
                            userId
                    );
                });
    }

    /** 获取会话中当前所有Typing用户列表 */
    public List<TypingStatusDTO> getActiveTypingUsers(String conversationId) {
        return typingStatusRepository.findActiveByConversation(conversationId, LocalDateTime.now())
                .stream()
                .map(this::buildDTO)
                .collect(Collectors.toList());
    }

    /** 定时清理过期状态 (每10秒) */
    @Scheduled(fixedRate = 10000)
    @Transactional
    public void cleanupExpired() {
        int deleted = typingStatusRepository.deleteExpired(LocalDateTime.now());
        if (deleted > 0) {
            log.debug("Cleaned up {} expired typing statuses", deleted);
        }
    }

    private TypingStatusDTO buildDTO(TypingStatusEntity entity) {
        String userName = userService.getUserDisplayName(entity.getUserId());
        return TypingStatusDTO.builder()
                .conversationId(entity.getConversationId())
                .conversationType(entity.getConversationType())
                .userId(entity.getUserId())
                .userName(userName)
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    private String buildTypingEvent(String eventType, TypingStatusDTO dto) {
        return String.format(
                "{\"event\":\"typing_status\",\"type\":\"%s\",\"data\":{\"conversationId\":\"%s\",\"conversationType\":\"%s\",\"userId\":\"%s\",\"userName\":\"%s\"}}",
                eventType,
                dto.getConversationId(),
                dto.getConversationType(),
                dto.getUserId(),
                dto.getUserName() != null ? dto.getUserName() : ""
        );
    }
}
