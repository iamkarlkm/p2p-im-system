package com.im.server.push;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.*;

/**
 * 推送消息合并器
 * 
 * 将短时间内同一用户的多条推送合并为一条，减少打扰
 */
@Component
public class PushMessageMerger {

    private static final Logger log = LoggerFactory.getLogger(PushMessageMerger.class);

    @Autowired
    private PushConfig pushConfig;

    // 合并缓存: mergeKey -> MergedMessage
    private final ConcurrentHashMap<String, MergedMessage> mergeCache = new ConcurrentHashMap<>();

    // 过期清理线程
    private final ScheduledExecutorService cleanupExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
        Thread t = new Thread(r, "push-merger-cleanup");
        t.setDaemon(true);
        return t;
    });

    public PushMessageMerger() {
        // 每分钟清理过期合并消息
        cleanupExecutor.scheduleAtFixedRate(this::cleanupExpired, 1, 1, TimeUnit.MINUTES);
    }

    /**
     * 尝试合并消息
     * 
     * @param message 新消息
     * @return 合并后的消息（可能是新消息，也可能是已存在的合并消息）
     */
    public PushMessage tryMerge(PushMessage message) {
        if (message == null || message.getMergeKey() == null || message.getMergeKey().isEmpty()) {
            return message;
        }

        String mergeKey = message.getMergeKey();
        int windowSeconds = pushConfig.getMergeWindowSeconds();

        MergedMessage existing = mergeCache.get(mergeKey);

        if (existing == null) {
            // 无已有消息，创建新的合并消息
            MergedMessage merged = new MergedMessage(message, windowSeconds);
            mergeCache.put(mergeKey, merged);
            log.debug("Created new merged message: key={}", mergeKey);
            return merged.toPushMessage();
        }

        if (existing.isExpired()) {
            // 已过期，创建新的
            MergedMessage merged = new MergedMessage(message, windowSeconds);
            mergeCache.put(mergeKey, merged);
            log.debug("Created new merged message (expired): key={}", mergeKey);
            return merged.toPushMessage();
        }

        // 合并到已有消息
        existing.merge(message);
        log.debug("Merged message: key={}, count={}", mergeKey, existing.getCount());
        return existing.toPushMessage();
    }

    /**
     * 强制刷新（立即发送合并消息）
     */
    public void flush(String mergeKey) {
        MergedMessage merged = mergeCache.remove(mergeKey);
        if (merged != null) {
            log.debug("Flushed merged message: key={}, count={}", mergeKey, merged.getCount());
            // 实际发送逻辑在 PushService 中，这里只移除缓存
        }
    }

    /**
     * 清理过期合并消息
     */
    private void cleanupExpired() {
        List<String> expiredKeys = new ArrayList<>();
        for (Map.Entry<String, MergedMessage> entry : mergeCache.entrySet()) {
            if (entry.getValue().isExpired()) {
                expiredKeys.add(entry.getKey());
            }
        }
        for (String key : expiredKeys) {
            mergeCache.remove(key);
        }
        if (!expiredKeys.isEmpty()) {
            log.debug("Cleaned up {} expired merged messages", expiredKeys.size());
        }
    }

    public int getCacheSize() {
        return mergeCache.size();
    }

    /**
     * 合并消息内部类
     */
    private static class MergedMessage {
        private final String title;
        private final String conversationId;
        private final String conversationType;
        private final Long senderId;
        private final String senderName;
        private final String senderAvatar;
        private final String mergeKey;
        private final long createdAt;
        private final int windowSeconds;
        private int count = 1;
        private String lastContent;    // 最后一条内容
        private String lastMessageType; // 最后一条消息类型
        private final List<String> previews = new ArrayList<>(); // 内容预览（前3条）

        MergedMessage(PushMessage message, int windowSeconds) {
            this.title = buildMergedTitle(message);
            this.conversationId = message.getConversationId();
            this.conversationType = message.getConversationType();
            this.senderId = message.getSenderId();
            this.senderName = message.getSenderName();
            this.senderAvatar = message.getSenderAvatar();
            this.mergeKey = message.getMergeKey();
            this.createdAt = System.currentTimeMillis();
            this.windowSeconds = windowSeconds;
            this.lastContent = message.getBody();
            this.lastMessageType = message.getMessageType();
            if (message.getBody() != null) {
                this.previews.add(truncate(message.getBody(), 50));
            }
        }

        void merge(PushMessage message) {
            this.count++;
            this.lastContent = message.getBody();
            this.lastMessageType = message.getMessageType();
            if (message.getBody() != null && previews.size() < 3) {
                previews.add(truncate(message.getBody(), 50));
            }
        }

        boolean isExpired() {
            return System.currentTimeMillis() - createdAt > windowSeconds * 1000L;
        }

        int getCount() {
            return count;
        }

        PushMessage toPushMessage() {
            String body;
            if (count == 1) {
                body = lastContent;
            } else {
                body = buildMergedBody();
            }

            Map<String, String> data = new HashMap<>();
            data.put("type", "merged_message");
            data.put("count", String.valueOf(count));
            data.put("conversation_id", conversationId);
            data.put("conversation_type", conversationType);
            if (previews.size() == 1) {
                data.put("content", previews.get(0));
            }

            return PushMessage.builder()
                    .title(title)
                    .body(body)
                    .senderId(senderId)
                    .senderName(senderName)
                    .senderAvatar(senderAvatar)
                    .conversationId(conversationId)
                    .conversationType(conversationType)
                    .data(data)
                    .category("chat_message")
                    .priority(PushMessage.Priority.HIGH)
                    .mergeKey(mergeKey)
                    .build();
        }

        private String buildMergedTitle(PushMessage message) {
            if (count == 1) {
                return message.getTitle() != null ? message.getTitle() : senderName;
            }
            return senderName + " (" + count + " 条新消息)";
        }

        private String buildMergedBody() {
            if (previews.size() == 1) {
                return previews.get(0);
            }
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < previews.size(); i++) {
                sb.append(previews.get(i));
                if (i < previews.size() - 1) {
                    sb.append("\n");
                }
            }
            return sb.toString();
        }

        private String truncate(String s, int max) {
            if (s == null) return "";
            if (s.length() <= max) return s;
            return s.substring(0, max) + "...";
        }
    }
}
