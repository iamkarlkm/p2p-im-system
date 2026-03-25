package com.im.server.reaction;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 表情回应仓储层（内存存储）
 */
public class ReactionRepository {

    // messageId -> List<MessageReaction>
    private final Map<String, List<MessageReaction>> messageReactions = new ConcurrentHashMap<>();

    // 唯一索引: messageId:userId:emoji -> reactionId (防重复反应)
    private final Map<String, String> uniqueReactionIndex = new ConcurrentHashMap<>();

    // ==================== CRUD ====================

    public boolean save(MessageReaction reaction) {
        String key = makeKey(reaction.getMessageId(), reaction.getUserId(), reaction.getEmoji());
        String existing = uniqueReactionIndex.putIfAbsent(key, reaction.getReactionId());
        if (existing != null) return false; // 已存在

        messageReactions.computeIfAbsent(reaction.getMessageId(), k -> new ArrayList<>())
            .add(reaction);
        return true;
    }

    public boolean delete(String messageId, String userId, String emoji) {
        String key = makeKey(messageId, userId, emoji);
        String removed = uniqueReactionIndex.remove(key);
        if (removed != null) {
            List<MessageReaction> reactions = messageReactions.get(messageId);
            if (reactions != null) {
                reactions.removeIf(r -> r.getUserId().equals(userId) && r.getEmoji().equals(emoji));
            }
            return true;
        }
        return false;
    }

    public Optional<MessageReaction> findById(String messageId, String userId, String emoji) {
        String key = makeKey(messageId, userId, emoji);
        String reactionId = uniqueReactionIndex.get(key);
        if (reactionId == null) return Optional.empty();
        List<MessageReaction> reactions = messageReactions.get(messageId);
        if (reactions == null) return Optional.empty();
        return reactions.stream()
            .filter(r -> r.getReactionId().equals(reactionId))
            .findFirst();
    }

    // ==================== 查询 ====================

    /**
     * 获取消息的所有回应
     */
    public List<MessageReaction> findByMessageId(String messageId) {
        return new ArrayList<>(messageReactions.getOrDefault(messageId, new ArrayList<>()));
    }

    /**
     * 获取消息的回应统计（按emoji分组）
     */
    public Map<String, ReactionStats> getReactionStats(String messageId) {
        List<MessageReaction> reactions = messageReactions.getOrDefault(messageId, new ArrayList<>());
        Map<String, ReactionStats> stats = new LinkedHashMap<>();

        for (MessageReaction r : reactions) {
            ReactionStats s = stats.computeIfAbsent(r.getEmoji(), k -> new ReactionStats(r.getEmoji()));
            s.addUser(r.getUserId());
        }
        return stats;
    }

    /**
     * 获取用户对某消息的所有反应emoji
     */
    public Set<String> getUserReactions(String messageId, String userId) {
        return messageReactions.getOrDefault(messageId, new ArrayList<>())
            .stream()
            .filter(r -> r.getUserId().equals(userId))
            .map(MessageReaction::getEmoji)
            .collect(Collectors.toSet());
    }

    /**
     * 检查用户是否对某消息添加了某emoji反应
     */
    public boolean hasUserReaction(String messageId, String userId, String emoji) {
        String key = makeKey(messageId, userId, emoji);
        return uniqueReactionIndex.containsKey(key);
    }

    /**
     * 获取用户参与的所有消息ID
     */
    public Set<String> findMessageIdsByUserId(String userId) {
        Set<String> result = new HashSet<>();
        for (List<MessageReaction> reactions : messageReactions.values()) {
            for (MessageReaction r : reactions) {
                if (r.getUserId().equals(userId)) {
                    result.add(r.getMessageId());
                }
            }
        }
        return result;
    }

    /**
     * 统计用户添加的反应数
     */
    public int countByUserId(String userId) {
        int count = 0;
        for (List<MessageReaction> reactions : messageReactions.values()) {
            for (MessageReaction r : reactions) {
                if (r.getUserId().equals(userId)) count++;
            }
        }
        return count;
    }

    // ==================== 批量操作 ====================

    /**
     * 批量删除某消息的所有回应
     */
    public int deleteByMessageId(String messageId) {
        List<MessageReaction> reactions = messageReactions.remove(messageId);
        if (reactions == null) return 0;
        int count = 0;
        for (MessageReaction r : reactions) {
            String key = makeKey(r.getMessageId(), r.getUserId(), r.getEmoji());
            uniqueReactionIndex.remove(key);
            count++;
        }
        return count;
    }

    /**
     * 清理过期回应（超过指定天数）
     */
    public int cleanupOldReactions(int maxAgeDays) {
        int count = 0;
        long cutoff = System.currentTimeMillis() - (maxAgeDays * 86400000L);
        for (String messageId : new ArrayList<>(messageReactions.keySet())) {
            List<MessageReaction> reactions = messageReactions.get(messageId);
            int before = reactions.size();
            reactions.removeIf(r -> {
                try {
                    long t = r.getCreatedAt().toInstant(java.time.ZoneOffset.of("+8")).toEpochMilli();
                    if (t < cutoff) {
                        String key = makeKey(r.getMessageId(), r.getUserId(), r.getEmoji());
                        uniqueReactionIndex.remove(key);
                        return true;
                    }
                } catch (Exception e) { return true; }
                return false;
            });
            count += before - reactions.size();
            if (reactions.isEmpty()) messageReactions.remove(messageId);
        }
        return count;
    }

    private String makeKey(String messageId, String userId, String emoji) {
        return messageId + ":" + userId + ":" + emoji;
    }

    // ==================== 统计类 ====================

    public static class ReactionStats {
        private final String emoji;
        private int count;
        private final Set<String> userIds = new HashSet<>();

        public ReactionStats(String emoji) { this.emoji = emoji; }

        public void addUser(String userId) {
            if (userIds.add(userId)) count++;
        }

        public String getEmoji() { return emoji; }
        public int getCount() { return count; }
        public Set<String> getUserIds() { return new HashSet<>(userIds); }
    }
}
