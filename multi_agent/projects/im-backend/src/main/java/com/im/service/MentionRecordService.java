package com.im.service;

import com.im.entity.MentionRecordEntity;
import com.im.repository.MentionRecordRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MentionRecordService {

    private final MentionRecordRepository mentionRepository;
    private static final Pattern AT_PATTERN = Pattern.compile("@([^\\s@]+)");
    private static final int MENTION_EXPIRE_DAYS = 7;

    // ========== 核心方法: 解析消息内容中的@提及 ==========

    /**
     * 从消息内容中解析所有@提及用户
     * 支持格式: @username @user123 @all @online @role:admin
     */
    public List<MentionParseResult> parseMentions(String content) {
        if (content == null || content.isBlank()) return Collections.emptyList();

        List<MentionParseResult> results = new ArrayList<>();
        Matcher matcher = AT_PATTERN.matcher(content);

        while (matcher.find()) {
            String target = matcher.group(1).trim();
            MentionParseResult result = new MentionParseResult();

            switch (target.toLowerCase()) {
                case "all" -> {
                    result.type = "ALL";
                    result.text = "@all";
                }
                case "online", "online_members", "online-members" -> {
                    result.type = "ONLINE_MEMBERS";
                    result.text = "@online";
                }
                case "here" -> {
                    result.type = "HERE";
                    result.text = "@here";
                }
                default -> {
                    if (target.startsWith("role:") || target.startsWith("role-")) {
                        result.type = "ROLE";
                        result.roleName = target.replaceFirst("^(role:|^role-)", "");
                        result.text = "@" + target;
                    } else if (target.startsWith("#")) {
                        result.type = "CHANNEL";
                        result.channelName = target;
                        result.text = target;
                    } else {
                        result.type = "USER";
                        result.username = target;
                        result.text = "@" + target;
                    }
                }
            }
            results.add(result);
        }
        return results;
    }

    /**
     * 为发送的消息创建@提及记录
     * 在消息发送后调用
     */
    @Transactional
    public List<MentionRecordEntity> createMentionRecords(
            Long messageId,
            Long conversationId,
            Long senderId,
            String content,
            List<Long> memberUserIds,
            Map<String, Long> usernameToUserId,
            Map<String, Long> roleNameToRoleId,
            Set<Long> onlineMemberIds) {

        List<MentionParseResult> mentions = parseMentions(content);
        if (mentions.isEmpty()) return Collections.emptyList();

        List<MentionRecordEntity> records = new ArrayList<>();
        Set<Long> alreadyMentioned = new HashSet<>();

        for (MentionParseResult m : mentions) {
            switch (m.type) {
                case "USER" -> {
                    Long userId = usernameToUserId.getOrDefault(m.username, null);
                    if (userId != null && !alreadyMentioned.contains(userId) && !userId.equals(senderId)) {
                        alreadyMentioned.add(userId);
                        records.add(buildRecord(messageId, conversationId, senderId,
                            userId, "USER", m.text, null, null));
                    }
                }
                case "ALL" -> {
                    for (Long uid : memberUserIds) {
                        if (!alreadyMentioned.contains(uid) && !uid.equals(senderId)) {
                            alreadyMentioned.add(uid);
                            records.add(buildRecord(messageId, conversationId, senderId,
                                uid, "ALL", m.text, null, null));
                        }
                    }
                }
                case "ONLINE_MEMBERS" -> {
                    for (Long uid : onlineMemberIds) {
                        if (!alreadyMentioned.contains(uid) && !uid.equals(senderId)) {
                            alreadyMentioned.add(uid);
                            records.add(buildRecord(messageId, conversationId, senderId,
                                uid, "ONLINE_MEMBERS", m.text, null, null));
                        }
                    }
                }
                case "ROLE" -> {
                    Long roleId = roleNameToRoleId.getOrDefault(m.roleName, null);
                    if (roleId != null) {
                        records.add(buildRecord(messageId, conversationId, senderId,
                            null, "ROLE", m.text, null, roleId));
                    }
                }
                case "CHANNEL" -> {
                    // CHANNEL 类型特殊处理，由 ChannelService 处理
                    records.add(buildRecord(messageId, conversationId, senderId,
                        null, "CHANNEL", m.text, m.channelName, null));
                }
            }
        }

        if (!records.isEmpty()) {
            List<MentionRecordEntity> saved = mentionRepository.saveAll(records);
            log.info("Created {} mention records for message {}", saved.size(), messageId);
            return saved;
        }
        return Collections.emptyList();
    }

    // ========== 查询方法 ==========

    /** 获取用户所有未读@提及 (带分页) */
    public List<MentionRecordEntity> getUnreadMentions(Long userId, int page, int size) {
        return mentionRepository.findByUserId(userId, PageRequest.of(page, size))
            .stream()
            .filter(r -> "UNREAD".equals(r.getStatus()))
            .collect(Collectors.toList());
    }

    /** 获取用户所有@提及 (分页) */
    public List<MentionRecordEntity> getUserMentions(Long userId, int page, int size) {
        return mentionRepository.findByUserId(userId, PageRequest.of(page, size));
    }

    /** 获取会话中的@提及历史 */
    public List<MentionRecordEntity> getConversationMentions(Long conversationId, int limit) {
        return mentionRepository.findByConversationIdAndMentionTypeNotOrderByCreatedAtAsc(
            conversationId, "CHANNEL");
    }

    /** 获取某用户在某会话的未读@数 */
    public long getUnreadCount(Long userId, Long conversationId) {
        return mentionRepository.countUnreadByUserAndConversation(userId, conversationId);
    }

    /** 获取用户所有未读@总数 */
    public long getTotalUnreadCount(Long userId) {
        return mentionRepository.countByMentionedUserIdAndStatus(userId, "UNREAD");
    }

    // ========== 操作方法 ==========

    /** 标记单条@为已读 */
    @Transactional
    public boolean markAsRead(Long mentionId, Long userId) {
        int updated = mentionRepository.markAsRead(mentionId, userId, LocalDateTime.now());
        return updated > 0;
    }

    /** 批量标记会话中@为已读 */
    @Transactional
    public int markAllRead(Long conversationId, Long userId) {
        return mentionRepository.markAllReadInConversation(conversationId, userId, LocalDateTime.now());
    }

    /** 忽略/关闭某条@提醒 */
    @Transactional
    public boolean dismissMention(Long mentionId, Long userId) {
        var opt = mentionRepository.findById(mentionId);
        if (opt.isPresent() && opt.get().getMentionedUserId().equals(userId)) {
            MentionRecordEntity r = opt.get();
            r.setStatus("DISMISSED");
            mentionRepository.save(r);
            return true;
        }
        return false;
    }

    /** 删除某消息的所有@记录 (消息被删除时调用) */
    @Transactional
    public void deleteByMessageId(Long messageId) {
        mentionRepository.deleteByMessageId(messageId);
        log.info("Deleted mention records for message {}", messageId);
    }

    // ========== 通知与推送 ==========

    /** 获取待推送的@通知列表 (定时任务调用) */
    public List<MentionRecordEntity> getPendingNotifications() {
        return mentionRepository.findPendingNotifications(LocalDateTime.now());
    }

    /** 标记@已推送 */
    @Transactional
    public void markNotified(Long mentionId) {
        mentionRepository.findById(mentionId).ifPresent(r -> {
            r.setNotified(true);
            mentionRepository.save(r);
        });
    }

    // ========== 统计 ==========

    /** 获取用户@统计 (用于仪表盘) */
    public MentionStats getStats(Long userId, int days) {
        LocalDateTime start = LocalDateTime.now().minusDays(days);
        long total = mentionRepository.countByUserInTimeRange(userId, start, LocalDateTime.now());
        long unread = mentionRepository.countByMentionedUserIdAndStatus(userId, "UNREAD");
        List<Object[]> byConv = mentionRepository.countUnreadGroupByConversation(userId);

        Map<Long, Long> byConversation = new HashMap<>();
        for (Object[] row : byConv) {
            byConversation.put((Long) row[0], (Long) row[1]);
        }

        return new MentionStats(total, unread, byConversation);
    }

    // ========== 辅助方法 ==========

    private MentionRecordEntity buildRecord(Long messageId, Long conversationId,
            Long senderId, Long mentionedUserId, String type, String text,
            String channelName, Long roleId) {
        MentionRecordEntity r = new MentionRecordEntity();
        r.setMessageId(messageId);
        r.setConversationId(conversationId);
        r.setSenderId(senderId);
        r.setMentionedUserId(mentionedUserId);
        r.setMentionType(type);
        r.setMentionText(text);
        r.setStatus("UNREAD");
        r.setNotified(false);
        r.setPushEnabled(true);
        r.setCreatedAt(LocalDateTime.now());
        r.setExpireAt(LocalDateTime.now().plusDays(MENTION_EXPIRE_DAYS));
        if (roleId != null) r.setRoleId(roleId);
        return r;
    }

    // ========== 内部类 ==========

    public static class MentionParseResult {
        public String type;      // USER / ALL / ONLINE_MEMBERS / ROLE / CHANNEL
        public String text;       // 原文 @xxx
        public String username;   // 用户名 (USER类型)
        public String roleName;   // 角色名 (ROLE类型)
        public String channelName; // 频道名 (CHANNEL类型)
    }

    public static class MentionStats {
        public long totalMentions;
        public long unreadCount;
        public Map<Long, Long> byConversation;

        public MentionStats(long total, long unread, Map<Long, Long> byConv) {
            this.totalMentions = total;
            this.unreadCount = unread;
            this.byConversation = byConv;
        }
    }
}
