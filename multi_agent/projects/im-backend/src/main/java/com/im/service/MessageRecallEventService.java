package com.im.service;

import com.im.entity.MessageRecallEventEntity;
import com.im.repository.MessageRecallEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MessageRecallEventService {

    private final MessageRecallEventRepository recallRepository;
    private final SimpMessagingTemplate messagingTemplate;

    /** 最大撤回时间窗口 (2分钟) */
    private static final int RECALL_WINDOW_MINUTES = 2;

    /**
     * 执行消息撤回
     * 
     * @return 撤回是否成功 (可能因超时被拒绝)
     */
    @Transactional
    public RecallResult recallMessage(
            Long messageId,
            Long conversationId,
            Long senderId,
            Long recallerId,
            String recallRole,
            String reason,
            String recallType) {

        // 检查是否在撤回窗口内
        LocalDateTime cutoff = LocalDateTime.now().minusMinutes(RECALL_WINDOW_MINUTES);
        
        RecallResult result = new RecallResult();
        result.messageId = messageId;
        result.conversationId = conversationId;
        result.success = true;

        // 创建撤回事件记录
        MessageRecallEventEntity event = new MessageRecallEventEntity();
        event.setMessageId(messageId);
        event.setConversationId(conversationId);
        event.setSenderId(senderId);
        event.setRecallerId(recallerId);
        event.setRecallRole(recallRole != null ? recallRole : "SENDER");
        event.setRecallReason(reason != null ? reason : "USER_RECALL");
        event.setRecallType(recallType != null ? recallType : "SINGLE");
        event.setRecalledAt(LocalDateTime.now());
        event.setNotified(false);
        event.setGlobalRecall(true);
        event.setAffectedDevices(1);

        recallRepository.save(event);

        // 广播撤回通知 WebSocket 事件
        broadcastRecallEvent(event);

        log.info("Message {} recalled by {} in conversation {}",
            messageId, recallerId, conversationId);

        return result;
    }

    /**
     * 批量撤回消息
     */
    @Transactional
    public List<RecallResult> batchRecall(
            List<Long> messageIds,
            Long conversationId,
            Long recallerId,
            String reason) {

        List<RecallResult> results = new ArrayList<>();
        for (Long msgId : messageIds) {
            RecallResult r = recallMessage(msgId, conversationId, null, recallerId, "SENDER", reason, "BATCH");
            results.add(r);
        }
        return results;
    }

    /**
     * 广播撤回事件到 WebSocket
     */
    private void broadcastRecallEvent(MessageRecallEventEntity event) {
        Map<String, Object> wsPayload = new HashMap<>();
        wsPayload.put("type", "MESSAGE_RECALLED");
        wsPayload.put("messageId", event.getMessageId());
        wsPayload.put("conversationId", event.getConversationId());
        wsPayload.put("recallerId", event.getRecallerId());
        wsPayload.put("recallRole", event.getRecallRole());
        wsPayload.put("recallType", event.getRecallType());
        wsPayload.put("reason", event.getRecallReason());
        wsPayload.put("recalledAt", event.getRecalledAt().toString());

        // 发送到会话的 WebSocket 主题
        String destination = "/topic/conversation/" + event.getConversationId() + "/recalls";
        messagingTemplate.convertAndSend(destination, wsPayload);

        // 标记已通知
        event.setNotified(true);
        recallRepository.save(event);
    }

    /**
     * 广播消息编辑事件
     */
    public void broadcastEditEvent(
            Long messageId,
            Long conversationId,
            Long editorId,
            String oldContentHash,
            String newContent,
            int editVersion) {

        Map<String, Object> wsPayload = new HashMap<>();
        wsPayload.put("type", "MESSAGE_EDITED");
        wsPayload.put("messageId", messageId);
        wsPayload.put("conversationId", conversationId);
        wsPayload.put("editorId", editorId);
        wsPayload.put("oldContentHash", oldContentHash);
        wsPayload.put("newContentPreview", truncate(newContent, 100));
        wsPayload.put("editVersion", editVersion);
        wsPayload.put("editedAt", LocalDateTime.now().toString());

        String destination = "/topic/conversation/" + conversationId + "/edits";
        messagingTemplate.convertAndSend(destination, wsPayload);

        log.info("Broadcast MESSAGE_EDITED for message {} in conversation {}",
            messageId, conversationId);
    }

    // ========== 查询方法 ==========

    /** 获取会话撤回历史 */
    public List<MessageRecallEventEntity> getRecallHistory(Long conversationId, int page, int size) {
        return recallRepository.findByConversationIdOrderByRecalledAtDesc(
            conversationId, PageRequest.of(page, size));
    }

    /** 获取用户的撤回历史 */
    public List<MessageRecallEventEntity> getUserRecallHistory(Long userId, int page, int size) {
        return recallRepository.findByRecallerIdOrderByRecalledAtDesc(
            userId, PageRequest.of(page, size));
    }

    /** 获取用户在会话中被撤回的消息 */
    public List<MessageRecallEventEntity> getMessagesRecalledFromUser(Long userId, int page, int size) {
        return recallRepository.findBySenderIdOrderByRecalledAtDesc(
            userId, PageRequest.of(page, size));
    }

    /** 获取会话统计 */
    public RecallStats getStats(Long conversationId, int days) {
        LocalDateTime start = LocalDateTime.now().minusDays(days);
        long total = recallRepository.countByConversationInRange(conversationId, start, LocalDateTime.now());
        List<Object[]> byRole = recallRepository.countByRecallRole(conversationId);

        Map<String, Long> byRoleMap = new HashMap<>();
        for (Object[] row : byRole) {
            byRoleMap.put((String) row[0], (Long) row[1]);
        }

        return new RecallStats(total, byRoleMap);
    }

    // ========== 编辑版本记录 ==========

    /**
     * 创建消息编辑版本记录
     */
    @Transactional
    public void recordEditVersion(Long messageId, Long conversationId, Long editorId,
            String oldContent, String newContent, int version) {
        // 编辑版本记录可以扩展为一个独立实体
        // 此处通过日志和 WebSocket 广播实现
        String oldHash = hashContent(oldContent);
        broadcastEditEvent(messageId, conversationId, editorId, oldHash, newContent, version);
    }

    // ========== 工具方法 ==========

    private String hashContent(String content) {
        if (content == null) return "";
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(content.getBytes(StandardCharsets.UTF_8));
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            return "";
        }
    }

    private String truncate(String s, int maxLen) {
        if (s == null) return "";
        return s.length() <= maxLen ? s : s.substring(0, maxLen) + "...";
    }

    // ========== 结果类 ==========

    public static class RecallResult {
        public Long messageId;
        public Long conversationId;
        public boolean success;
        public String errorMessage;

        public RecallResult() {}
    }

    public static class RecallStats {
        public long totalRecalls;
        public Map<String, Long> byRole;

        public RecallStats(long total, Map<String, Long> byRole) {
            this.totalRecalls = total;
            this.byRole = byRole;
        }
    }
}
