package com.im.backend.service.impl;

import com.im.backend.dto.MessageReactionDTO;
import com.im.backend.dto.ReactionSummaryDTO;
import com.im.backend.model.MessageReaction;
import com.im.backend.repository.MessageReactionRepository;
import com.im.backend.service.MessageReactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 消息表情回应服务实现类
 * 
 * @author IM Development Team
 * @version 1.0
 * @since 2026-03-27
 */
@Service
@Transactional
public class MessageReactionServiceImpl implements MessageReactionService {

    private static final Logger logger = LoggerFactory.getLogger(MessageReactionServiceImpl.class);

    @Autowired
    private MessageReactionRepository reactionRepository;

    @Override
    public MessageReactionDTO addReaction(MessageReactionDTO reactionDTO) {
        logger.debug("Adding reaction: messageId={}, userId={}, emoji={}",
                reactionDTO.getMessageId(), reactionDTO.getUserId(), reactionDTO.getEmojiCode());

        // 检查是否已存在相同的回应
        Optional<MessageReaction> existing = reactionRepository
                .findByMessageIdAndUserIdAndEmojiCodeAndIsDeletedFalse(
                        reactionDTO.getMessageId(),
                        reactionDTO.getUserId(),
                        reactionDTO.getEmojiCode());

        if (existing.isPresent()) {
            logger.debug("Reaction already exists, returning existing");
            return MessageReactionDTO.fromEntity(existing.get());
        }

        // 创建新回应
        MessageReaction reaction = reactionDTO.toEntity();
        reaction.setIsDeleted(false);
        MessageReaction saved = reactionRepository.save(reaction);

        logger.info("Reaction added successfully: id={}", saved.getId());
        return MessageReactionDTO.fromEntity(saved);
    }

    @Override
    public void removeReaction(Long messageId, Long userId, String emojiCode) {
        logger.debug("Removing reaction: messageId={}, userId={}, emoji={}",
                messageId, userId, emojiCode);

        reactionRepository.softDeleteByMessageIdAndUserIdAndEmojiCode(
                messageId, userId, emojiCode, LocalDateTime.now());

        logger.info("Reaction removed successfully");
    }

    @Override
    public MessageReactionDTO toggleReaction(MessageReactionDTO reactionDTO) {
        Optional<MessageReaction> existing = reactionRepository
                .findByMessageIdAndUserIdAndEmojiCodeAndIsDeletedFalse(
                        reactionDTO.getMessageId(),
                        reactionDTO.getUserId(),
                        reactionDTO.getEmojiCode());

        if (existing.isPresent()) {
            // 已存在则删除
            removeReaction(reactionDTO.getMessageId(), reactionDTO.getUserId(), reactionDTO.getEmojiCode());
            return null;
        } else {
            // 不存在则添加
            return addReaction(reactionDTO);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public ReactionSummaryDTO getReactionSummary(Long messageId, Long currentUserId) {
        logger.debug("Getting reaction summary for messageId={}", messageId);

        ReactionSummaryDTO summary = new ReactionSummaryDTO();
        summary.setMessageId(messageId);

        // 获取所有回应
        List<MessageReaction> reactions = reactionRepository.findByMessageIdAndIsDeletedFalse(messageId);

        // 统计总数
        summary.setTotalReactions((long) reactions.size());

        // 按表情分组统计
        List<ReactionSummaryDTO.EmojiCountDTO> emojiCounts = reactions.stream()
                .collect(Collectors.groupingBy(MessageReaction::getEmojiCode, Collectors.counting()))
                .entrySet().stream()
                .map(entry -> {
                    ReactionSummaryDTO.EmojiCountDTO dto = new ReactionSummaryDTO.EmojiCountDTO();
                    dto.setEmojiCode(entry.getKey());
                    dto.setCount(entry.getValue());

                    // 获取用户ID列表
                    List<Long> userIds = reactions.stream()
                            .filter(r -> r.getEmojiCode().equals(entry.getKey()))
                            .map(MessageReaction::getUserId)
                            .collect(Collectors.toList());
                    dto.setUserIds(userIds);
                    dto.setIsCurrentUserIncluded(userIds.contains(currentUserId));

                    return dto;
                })
                .sorted((a, b) -> Long.compare(b.getCount(), a.getCount()))
                .collect(Collectors.toList());

        summary.setEmojiCounts(emojiCounts);
        summary.setUniqueEmojiCount(emojiCounts.size());

        // 检查当前用户是否回应过
        summary.setHasCurrentUserReacted(
                reactions.stream().anyMatch(r -> r.getUserId().equals(currentUserId)));

        // 获取当前用户的表情
        reactions.stream()
                .filter(r -> r.getUserId().equals(currentUserId))
                .findFirst()
                .ifPresent(r -> summary.setCurrentUserEmoji(r.getEmojiCode()));

        return summary;
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageReactionDTO> getReactionsByMessage(Long messageId) {
        return reactionRepository.findByMessageIdAndIsDeletedFalse(messageId).stream()
                .map(MessageReactionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MessageReactionDTO> getReactionsByMessage(Long messageId, Pageable pageable) {
        return reactionRepository.findByMessageIdAndIsDeletedFalse(messageId, pageable)
                .map(MessageReactionDTO::fromEntity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageReactionDTO> getReactionsByMessageAndUser(Long messageId, Long userId) {
        return reactionRepository.findByMessageIdAndUserIdAndIsDeletedFalse(messageId, userId).stream()
                .map(MessageReactionDTO::fromEntity)
                .collect(Collectors.toList());
    }

    @Override
    public void removeAllReactionsByUser(Long messageId, Long userId) {
        logger.debug("Removing all reactions for messageId={}, userId={}", messageId, userId);
        reactionRepository.softDeleteAllByMessageIdAndUserId(messageId, userId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReactionSummaryDTO.EmojiCountDTO> getPopularEmojis(Long conversationId, int limit) {
        LocalDateTime since = LocalDateTime.now().minusDays(30);
        Pageable pageable = PageRequest.of(0, limit);

        List<Object[]> results = reactionRepository.findPopularEmojisInConversation(
                conversationId, since, pageable);

        return results.stream()
                .map(obj -> {
                    ReactionSummaryDTO.EmojiCountDTO dto = new ReactionSummaryDTO.EmojiCountDTO();
                    dto.setEmojiCode((String) obj[0]);
                    dto.setCount((Long) obj[1]);
                    return dto;
                })
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasUserReacted(Long messageId, Long userId) {
        return reactionRepository.existsByMessageIdAndUserIdAndIsDeletedFalse(messageId, userId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReactionSummaryDTO> getReactionSummaries(List<Long> messageIds, Long currentUserId) {
        return messageIds.stream()
                .map(id -> getReactionSummary(id, currentUserId))
                .collect(Collectors.toList());
    }
}
