package com.im.server.reaction;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageReactionService {

    private final MessageReactionRepository reactionRepository;
    private final ReactionWebSocketHandler wsHandler;

    @Transactional
    public ReactionResult addReaction(String messageId, String userId, String emoji, String type) {
        log.info("Adding reaction: messageId={}, userId={}, emoji={}", messageId, userId, emoji);
        MessageReaction reaction = MessageReaction.builder()
                .reactionId(UUID.randomUUID().toString())
                .messageId(messageId)
                .userId(userId)
                .emoji(emoji)
                .type(MessageReaction.ReactionType.valueOf(type))
                .createdAt(Instant.now())
                .build();
        reactionRepository.save(reaction);
        ReactionStats stats = getReactionStats(messageId);
        wsHandler.broadcastReactionUpdate(messageId, stats);
        return new ReactionResult(reaction.getReactionId(), stats, true);
    }

    @Transactional
    public ReactionResult removeReaction(String messageId, String userId, String emoji) {
        log.info("Removing reaction: messageId={}, userId={}, emoji={}", messageId, userId, emoji);
        reactionRepository.deleteByMessageIdAndUserIdAndEmoji(messageId, userId, emoji);
        ReactionStats stats = getReactionStats(messageId);
        wsHandler.broadcastReactionUpdate(messageId, stats);
        return new ReactionResult(null, stats, false);
    }

    public List<ReactionWithUsers> getReactionsForMessage(String messageId) {
        List<MessageReaction> reactions = reactionRepository.findByMessageId(messageId);
        Map<String, List<String>> emojiUsers = new LinkedHashMap<>();
        Map<String, Long> emojiCounts = new HashMap<>();
        for (MessageReaction r : reactions) {
            emojiUsers.computeIfAbsent(r.getEmoji(), k -> new ArrayList<>()).add(r.getUserId());
            emojiCounts.merge(r.getEmoji(), 1L, Long::sum);
        }
        return emojiUsers.entrySet().stream()
                .map(e -> new ReactionWithUsers(e.getKey(), emojiCounts.get(e.getKey()), e.getValue()))
                .collect(Collectors.toList());
    }

    public ReactionStats getReactionStats(String messageId) {
        List<Object[]> raw = reactionRepository.countGroupByEmoji(messageId);
        Map<String, Long> counts = new LinkedHashMap<>();
        for (Object[] row : raw) {
            counts.put((String) row[0], (Long) row[1]);
        }
        return new ReactionStats(messageId, counts);
    }

    public record ReactionResult(String reactionId, ReactionStats stats, boolean added) {}
    public record ReactionWithUsers(String emoji, Long count, List<String> userIds) {}
    public record ReactionStats(String messageId, Map<String, Long> counts) {}
}
