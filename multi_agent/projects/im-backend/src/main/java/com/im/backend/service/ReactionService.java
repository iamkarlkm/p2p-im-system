package com.im.backend.service;

import com.im.backend.dto.ReactionDTO;
import com.im.backend.entity.MessageReactionEntity;
import com.im.backend.repository.MessageReactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReactionService {

    private final MessageReactionRepository reactionRepository;
    private final WebSocketService webSocketService;

    /** 添加/切换表情反应 */
    @Transactional
    public ReactionDTO toggleReaction(Long messageId, Long userId, String emoji, Boolean isCustom) {
        boolean exists = reactionRepository.existsByMessageIdAndUserIdAndEmoji(messageId, userId, emoji);

        if (exists) {
            reactionRepository.deleteByMessageIdAndUserIdAndEmoji(messageId, userId, emoji);
        } else {
            MessageReactionEntity reaction = MessageReactionEntity.builder()
                .messageId(messageId)
                .userId(userId)
                .emoji(emoji)
                .isCustom(isCustom != null ? isCustom : false)
                .createdAt(LocalDateTime.now())
                .build();
            reactionRepository.save(reaction);
        }

        // 推送 WebSocket 事件
        Map<String, Object> payload = new HashMap<>();
        payload.put("type", exists ? "reaction_removed" : "reaction_added");
        payload.put("messageId", messageId);
        payload.put("emoji", emoji);
        payload.put("userId", userId);
        payload.put("isCustom", isCustom);
        webSocketService.broadcastToMessageChannel(messageId, payload);

        return getReactionStats(messageId, userId);
    }

    /** 获取消息反应统计 */
    public ReactionDTO getReactionStats(Long messageId, Long userId) {
        List<Object[]> raw = reactionRepository.countByMessageIdGroupByEmoji(messageId);

        List<ReactionDTO.EmojiCount> reactions = raw.stream().map(row -> {
            String emoji = (String) row[0];
            long count = (Long) row[1];
            boolean userReacted = reactionRepository.existsByMessageIdAndUserIdAndEmoji(messageId, userId, emoji);
            return ReactionDTO.EmojiCount.builder()
                .emoji(emoji)
                .isCustom(false)
                .count((int) count)
                .userReacted(userReacted)
                .build();
        }).collect(Collectors.toList());

        return ReactionDTO.builder()
            .messageId(messageId)
            .reactions(reactions)
            .build();
    }

    /** 批量获取消息列表的反应统计 */
    public Map<Long, ReactionDTO> batchGetReactionStats(List<Long> messageIds, Long userId) {
        if (messageIds.isEmpty()) return Collections.emptyMap();

        List<Object[]> raw = reactionRepository.countByMessageIdsGroupByEmoji(messageIds);

        // 按 messageId 分组
        Map<Long, Map<String, Integer>> emojiCounts = new HashMap<>();
        for (Object[] row : raw) {
            Long msgId = (Long) row[0];
            String emoji = (String) row[1];
            int cnt = ((Long) row[2]).intValue();
            emojiCounts.computeIfAbsent(msgId, k -> new HashMap<>()).put(emoji, cnt);
        }

        // 按 messageId 构建 ReactionDTO
        Map<Long, ReactionDTO> result = new HashMap<>();
        for (Long msgId : messageIds) {
            Map<String, Integer> emojis = emojiCounts.getOrDefault(msgId, Collections.emptyMap());
            List<ReactionDTO.EmojiCount> counts = emojis.entrySet().stream().map(e -> {
                boolean userReacted = reactionRepository.existsByMessageIdAndUserIdAndEmoji(msgId, userId, e.getKey());
                return ReactionDTO.EmojiCount.builder()
                    .emoji(e.getKey())
                    .count(e.getValue())
                    .userReacted(userReacted)
                    .isCustom(false)
                    .build();
            }).collect(Collectors.toList());

            result.put(msgId, ReactionDTO.builder().messageId(msgId).reactions(counts).build());
        }
        return result;
    }

    /** 删除用户的所有反应 */
    @Transactional
    public void removeAllByUser(Long userId) {
        List<MessageReactionEntity> reactions = reactionRepository.findAll().stream()
            .filter(r -> r.getUserId().equals(userId))
            .collect(Collectors.toList());
        reactionRepository.deleteAll(reactions);
    }
}
