package com.im.backend.service;

import com.im.backend.entity.MessageHistory;
import com.im.backend.entity.SyncCheckpoint;
import com.im.backend.repository.MessageHistoryRepository;
import com.im.backend.repository.SyncCheckpointRepository;
import com.im.backend.dto.MessageSyncItem;
import com.im.backend.dto.SyncRequest;
import com.im.backend.dto.SyncResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageHistoryService {

    private final MessageHistoryRepository messageHistoryRepository;
    private final SyncCheckpointRepository syncCheckpointRepository;

    private static final int DEFAULT_SYNC_LIMIT = 50;
    private static final int MAX_SYNC_LIMIT = 200;

    @Transactional
    public void saveMessageToHistory(Long messageId, Long conversationId, Long userId, Long senderId, String content, String contentType, LocalDateTime sentAt) {
        if (messageHistoryRepository.findByMessageId(messageId).isPresent()) {
            return;
        }
        MessageHistory history = MessageHistory.builder()
                .messageId(messageId)
                .conversationId(conversationId)
                .userId(userId)
                .senderId(senderId)
                .content(content)
                .contentType(contentType)
                .sentAt(sentAt)
                .syncedAt(LocalDateTime.now())
                .deleted(false)
                .build();
        messageHistoryRepository.save(history);
        log.debug("Message {} saved to history for user {}", messageId, userId);
    }

    public SyncResponse syncMessages(Long userId, SyncRequest request) {
        String deviceId = request.getDeviceId() != null ? request.getDeviceId() : "default";
        Long conversationId = request.getConversationId();
        Long lastMessageId = request.getLastMessageId() != null ? request.getLastMessageId() : 0L;
        int limit = request.getLimit() != null ? Math.min(request.getLimit(), MAX_SYNC_LIMIT) : DEFAULT_SYNC_LIMIT;

        Page<MessageHistory> page;
        if (request.getSince() != null) {
            if (conversationId != null) {
                page = messageHistoryRepository.findMessagesSince(userId, conversationId, request.getSince(), PageRequest.of(0, limit));
            } else {
                page = messageHistoryRepository.findAllMessagesSince(userId, request.getSince(), PageRequest.of(0, limit));
            }
        } else if (conversationId != null) {
            page = messageHistoryRepository.findNewMessages(userId, conversationId, lastMessageId, PageRequest.of(0, limit));
        } else {
            page = messageHistoryRepository.findAllMessagesSince(userId, LocalDateTime.now().minusDays(30), PageRequest.of(0, limit));
        }

        List<MessageSyncItem> items = page.getContent().stream()
                .map(this::toSyncItem)
                .collect(Collectors.toList());

        Long nextMessageId = page.hasNext() && !page.getContent().isEmpty()
                ? page.getContent().get(page.getContent().size() - 1).getMessageId()
                : null;

        String nextToken = nextMessageId != null ? generateSyncToken(userId, deviceId, nextMessageId) : null;

        if (!items.isEmpty() && deviceId != null) {
            Long convId = conversationId != null ? conversationId : 0L;
            saveCheckpoint(userId, deviceId, convId, nextMessageId != null ? nextMessageId : lastMessageId, nextToken);
        }

        return SyncResponse.builder()
                .messages(items)
                .nextMessageId(nextMessageId)
                .nextSyncToken(nextToken)
                .syncTimestamp(LocalDateTime.now())
                .hasMore(page.hasNext())
                .totalSynced(items.size())
                .build();
    }

    @Transactional
    public void markMessageDeleted(Long messageId, Long userId) {
        messageHistoryRepository.findByMessageId(messageId).ifPresent(history -> {
            history.setDeleted(true);
            messageHistoryRepository.save(history);
            log.info("Message {} marked as deleted in history for user {}", messageId, userId);
        });
    }

    public Optional<SyncCheckpoint> getCheckpoint(Long userId, String deviceId, Long conversationId) {
        return syncCheckpointRepository.findByUserIdAndDeviceIdAndConversationId(userId, deviceId, conversationId);
    }

    @Transactional
    public void saveCheckpoint(Long userId, String deviceId, Long conversationId, Long lastMessageId, String syncToken) {
        SyncCheckpoint checkpoint = syncCheckpointRepository
                .findByUserIdAndDeviceIdAndConversationId(userId, deviceId, conversationId)
                .orElse(SyncCheckpoint.builder()
                        .userId(userId)
                        .deviceId(deviceId)
                        .conversationId(conversationId)
                        .build());
        checkpoint.setLastMessageId(lastMessageId);
        checkpoint.setLastSyncedAt(LocalDateTime.now());
        checkpoint.setSyncToken(syncToken);
        syncCheckpointRepository.save(checkpoint);
    }

    public List<SyncCheckpoint> getAllCheckpoints(Long userId, String deviceId) {
        return syncCheckpointRepository.findByUserIdAndDeviceId(userId, deviceId);
    }

    private MessageSyncItem toSyncItem(MessageHistory history) {
        String action = history.getDeleted() ? "delete" : "upsert";
        return MessageSyncItem.builder()
                .messageId(history.getMessageId())
                .conversationId(history.getConversationId())
                .senderId(history.getSenderId())
                .content(history.getContent())
                .contentType(history.getContentType())
                .sentAt(history.getSentAt())
                .deleted(history.getDeleted())
                .syncAction(action)
                .build();
    }

    private String generateSyncToken(Long userId, String deviceId, Long lastMessageId) {
        String raw = userId + ":" + deviceId + ":" + lastMessageId + ":" + System.currentTimeMillis();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(raw.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hash).substring(0, 16);
        } catch (Exception e) {
            return String.valueOf(lastMessageId);
        }
    }
}
