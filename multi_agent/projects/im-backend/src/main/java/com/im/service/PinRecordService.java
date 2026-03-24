package com.im.service;

import com.im.entity.PinRecordEntity;
import com.im.repository.PinRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息/会话置顶服务
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class PinRecordService {

    private final PinRecordRepository pinRecordRepository;

    public static final String PIN_TYPE_CONVERSATION = "CONVERSATION";
    public static final String PIN_TYPE_MESSAGE = "MESSAGE";

    @Transactional
    public PinRecordEntity pinConversation(String userId, String conversationId) {
        if (pinRecordRepository.existsByConversationIdAndUserIdAndPinType(conversationId, userId, PIN_TYPE_CONVERSATION)) {
            throw new RuntimeException("该会话已在置顶列表中: " + conversationId);
        }

        Long maxOrder = pinRecordRepository.findMaxSortOrder(userId, PIN_TYPE_CONVERSATION);
        Long sortOrder = (maxOrder == null ? 0L : maxOrder) + 1;

        PinRecordEntity entity = PinRecordEntity.builder()
                .conversationId(conversationId)
                .userId(userId)
                .pinType(PIN_TYPE_CONVERSATION)
                .sortOrder(sortOrder)
                .pinnedAt(LocalDateTime.now())
                .build();

        entity = pinRecordRepository.save(entity);
        log.info("会话置顶: userId={}, conversationId={}", userId, conversationId);
        return entity;
    }

    @Transactional
    public void unpinConversation(String userId, String conversationId) {
        pinRecordRepository.deleteByConversationPin(conversationId, userId, PIN_TYPE_CONVERSATION);
        log.info("取消会话置顶: userId={}, conversationId={}", userId, conversationId);
    }

    public List<PinRecordEntity> getPinnedConversations(String userId) {
        return pinRecordRepository.findByUserIdAndPinTypeOrderBySortOrderDesc(userId, PIN_TYPE_CONVERSATION);
    }

    @Transactional
    public PinRecordEntity pinMessage(String userId, String conversationId, String messageId, String note) {
        if (pinRecordRepository.findByConversationIdAndMessageIdAndUserIdAndPinType(
                conversationId, messageId, userId, PIN_TYPE_MESSAGE).isPresent()) {
            throw new RuntimeException("该消息已在置顶列表中: " + messageId);
        }

        Long maxOrder = pinRecordRepository.findMaxSortOrder(userId, PIN_TYPE_MESSAGE);
        Long sortOrder = (maxOrder == null ? 0L : maxOrder) + 1;

        PinRecordEntity entity = PinRecordEntity.builder()
                .conversationId(conversationId)
                .messageId(messageId)
                .userId(userId)
                .pinType(PIN_TYPE_MESSAGE)
                .sortOrder(sortOrder)
                .pinnedAt(LocalDateTime.now())
                .note(note)
                .build();

        entity = pinRecordRepository.save(entity);
        log.info("消息置顶: userId={}, messageId={}", userId, messageId);
        return entity;
    }

    @Transactional
    public void unpinMessage(String userId, String messageId) {
        List<PinRecordEntity> records = pinRecordRepository.findByUserIdAndPinTypeOrderBySortOrderDesc(userId, PIN_TYPE_MESSAGE);
        records.stream()
                .filter(r -> messageId.equals(r.getMessageId()))
                .findFirst()
                .ifPresent(r -> {
                    pinRecordRepository.delete(r);
                    log.info("取消消息置顶: userId={}, messageId={}", userId, messageId);
                });
    }

    public List<PinRecordEntity> getPinnedMessages(String userId, String conversationId) {
        return pinRecordRepository.findByConversationIdAndUserIdAndPinType(conversationId, userId, PIN_TYPE_MESSAGE);
    }

    public boolean isConversationPinned(String userId, String conversationId) {
        return pinRecordRepository.existsByConversationIdAndUserIdAndPinType(conversationId, userId, PIN_TYPE_CONVERSATION);
    }

    @Transactional
    public void reorderPinnedConversations(String userId, List<String> orderedConversationIds) {
        for (int i = 0; i < orderedConversationIds.size(); i++) {
            String convId = orderedConversationIds.get(i);
            List<PinRecordEntity> records = pinRecordRepository.findByConversationIdAndUserIdAndPinType(
                    convId, userId, PIN_TYPE_CONVERSATION);
            records.forEach(r -> r.setSortOrder((long) (orderedConversationIds.size() - i)));
            pinRecordRepository.saveAll(records);
        }
        log.info("置顶会话排序更新: userId={}, count={}", userId, orderedConversationIds.size());
    }
}
