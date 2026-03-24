package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 消息过期规则实体
 */
@Entity
@Table(name = "message_expiration_rules",
       indexes = {
           @Index(name = "idx_user_conversation", columnList = "userId, conversationId"),
           @Index(name = "idx_expire_time", columnList = "expireTime")
       })
public class MessageExpirationRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 用户ID */
    @Column(nullable = false)
    private Long userId;

    /** 会话ID（null表示全局规则） */
    @Column
    private String conversationId;

    /** 会话类型：private/group/channel */
    @Column(length = 20)
    private String conversationType;

    /** 过期类型：READ_AFTER/SELF_DESTRUCT/TIME_BASED/GLOBAL */
    @Column(length = 30, nullable = false)
    private String expirationType;

    /** 过期时间（UTC） */
    @Column
    private LocalDateTime expireTime;

    /** 相对过期时长（秒）：阅后N秒/创建后N天等 */
    @Column
    private Long relativeSeconds;

    /** 是否激活 */
    @Column(nullable = false)
    private Boolean active = true;

    /** 是否为全局默认规则 */
    @Column(nullable = false)
    private Boolean globalDefault = false;

    /** 消息类型过滤：TEXT/IMAGE/FILE/ALL */
    @Column(length = 50)
    private String messageTypeFilter = "ALL";

    /** 已读后多久销毁（秒） */
    @Column
    private Long readDestroySeconds;

    /** 过期前是否发送提醒 */
    @Column(nullable = false)
    private Boolean preExpireNotice = false;

    /** 提前提醒时间（秒） */
    @Column
    private Long preExpireNoticeSeconds;

    /** 创建时间 */
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** 更新时间 */
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getConversationType() { return conversationType; }
    public void setConversationType(String conversationType) { this.conversationType = conversationType; }

    public String getExpirationType() { return expirationType; }
    public void setExpirationType(String expirationType) { this.expirationType = expirationType; }

    public LocalDateTime getExpireTime() { return expireTime; }
    public void setExpireTime(LocalDateTime expireTime) { this.expireTime = expireTime; }

    public Long getRelativeSeconds() { return relativeSeconds; }
    public void setRelativeSeconds(Long relativeSeconds) { this.relativeSeconds = relativeSeconds; }

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public Boolean getGlobalDefault() { return globalDefault; }
    public void setGlobalDefault(Boolean globalDefault) { this.globalDefault = globalDefault; }

    public String getMessageTypeFilter() { return messageTypeFilter; }
    public void setMessageTypeFilter(String messageTypeFilter) { this.messageTypeFilter = messageTypeFilter; }

    public Long getReadDestroySeconds() { return readDestroySeconds; }
    public void setReadDestroySeconds(Long readDestroySeconds) { this.readDestroySeconds = readDestroySeconds; }

    public Boolean getPreExpireNotice() { return preExpireNotice; }
    public void setPreExpireNotice(Boolean preExpireNotice) { this.preExpireNotice = preExpireNotice; }

    public Long getPreExpireNoticeSeconds() { return preExpireNoticeSeconds; }
    public void setPreExpireNoticeSeconds(Long preExpireNoticeSeconds) { this.preExpireNoticeSeconds = preExpireNoticeSeconds; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
