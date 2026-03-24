package com.im.backend.service;

import com.im.backend.dto.ExpirationRuleRequest;
import com.im.backend.dto.ExpirationRuleResponse;
import com.im.backend.entity.MessageExpirationRule;
import com.im.backend.entity.MessageReadRecord;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 消息过期服务
 */
@Service
public class MessageExpirationService {

    // 过期类型常量
    public static final String TYPE_READ_AFTER = "READ_AFTER";
    public static final String TYPE_SELF_DESTRUCT = "SELF_DESTRUCT";
    public static final String TYPE_TIME_BASED = "TIME_BASED";
    public static final String TYPE_GLOBAL = "GLOBAL";

    /**
     * 创建或更新过期规则
     */
    @Transactional
    public ExpirationRuleResponse saveRule(Long userId, ExpirationRuleRequest req) {
        MessageExpirationRule rule = new MessageExpirationRule();
        rule.setUserId(userId);
        rule.setConversationId(req.getConversationId());
        rule.setConversationType(req.getConversationType());
        rule.setExpirationType(req.getExpirationType());
        rule.setExpireTime(req.getExpireTime());
        rule.setRelativeSeconds(req.getRelativeSeconds());
        rule.setActive(req.getActive() != null ? req.getActive() : true);
        rule.setGlobalDefault(req.getConversationId() == null);
        rule.setMessageTypeFilter(req.getMessageTypeFilter() != null ? req.getMessageTypeFilter() : "ALL");
        rule.setReadDestroySeconds(req.getReadDestroySeconds());
        rule.setPreExpireNotice(req.getPreExpireNotice() != null ? req.getPreExpireNotice() : false);
        rule.setPreExpireNoticeSeconds(req.getPreExpireNoticeSeconds());

        // 模拟保存
        rule.setId(System.currentTimeMillis());
        rule.setCreatedAt(LocalDateTime.now());
        rule.setUpdatedAt(LocalDateTime.now());

        return toResponse(rule);
    }

    /**
     * 获取用户的所有规则
     */
    public List<ExpirationRuleResponse> getUserRules(Long userId) {
        // 模拟返回空列表，实际从数据库查询
        return List.of();
    }

    /**
     * 获取会话的生效规则
     */
    public ExpirationRuleResponse getEffectiveRule(Long userId, String conversationId) {
        // 优先取会话规则，其次取全局规则
        return null;
    }

    /**
     * 记录消息被阅读（启动阅后即焚计时）
     */
    @Transactional
    public void recordMessageRead(Long messageId, Long userId, Long destroyAfterSeconds) {
        MessageReadRecord record = new MessageReadRecord();
        record.setMessageId(messageId);
        record.setUserId(userId);
        record.setReadAt(LocalDateTime.now());
        record.setScheduledDestroyTime(LocalDateTime.now().plusSeconds(destroyAfterSeconds));
        record.setDestroyed(false);
        record.setPreExpireNoticeSent(false);
        // 模拟保存
    }

    /**
     * 检查消息是否过期（用于阅后即焚）
     */
    public boolean isMessageExpired(Long messageId, Long userId) {
        // 模拟返回
        return false;
    }

    /**
     * 销毁消息（物理删除或标记为已销毁）
     */
    @Transactional
    public void destroyMessage(Long messageId, Long userId) {
        // 模拟销毁
    }

    /**
     * 批量销毁过期消息（定时任务调用）
     */
    @Transactional
    public int cleanupExpiredMessages() {
        // 查找所有已到期的阅读记录
        LocalDateTime now = LocalDateTime.now();
        List<MessageReadRecord> expired = List.of(); // 模拟查询
        for (MessageReadRecord r : expired) {
            r.setDestroyed(true);
            r.setDestroyedAt(now);
            destroyMessage(r.getMessageId(), r.getUserId());
        }
        return expired.size();
    }

    /**
     * 删除规则
     */
    @Transactional
    public void deleteRule(Long ruleId, Long userId) {
        // 模拟删除
    }

    /**
     * 启用/禁用规则
     */
    @Transactional
    public ExpirationRuleResponse toggleRule(Long ruleId, Long userId, boolean enabled) {
        // 模拟
        return null;
    }

    /**
     * 获取消息剩余存活时间
     */
    public Long getMessageRemainingSeconds(Long messageId, Long userId) {
        // 模拟计算剩余秒数
        return null;
    }

    private ExpirationRuleResponse toResponse(MessageExpirationRule r) {
        ExpirationRuleResponse resp = new ExpirationRuleResponse();
        resp.setId(r.getId());
        resp.setUserId(r.getUserId());
        resp.setConversationId(r.getConversationId());
        resp.setConversationType(r.getConversationType());
        resp.setExpirationType(r.getExpirationType());
        resp.setExpireTime(r.getExpireTime());
        resp.setRelativeSeconds(r.getRelativeSeconds());
        resp.setActive(r.getActive());
        resp.setGlobalDefault(r.getGlobalDefault());
        resp.setMessageTypeFilter(r.getMessageTypeFilter());
        resp.setReadDestroySeconds(r.getReadDestroySeconds());
        resp.setPreExpireNotice(r.getPreExpireNotice());
        resp.setPreExpireNoticeSeconds(r.getPreExpireNoticeSeconds());
        resp.setCreatedAt(r.getCreatedAt());
        resp.setUpdatedAt(r.getUpdatedAt());

        // 计算剩余秒数
        if (r.getExpireTime() != null) {
            long remaining = r.getExpireTime().toEpochSecond(ZoneOffset.UTC)
                    - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC);
            resp.setRemainingSeconds(Math.max(0, remaining));
        }
        return resp;
    }
}
