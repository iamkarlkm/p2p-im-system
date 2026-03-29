package com.im.backend.entity;

import javax.persistence.*;
import java.time.Instant;
import java.util.Objects;

/**
 * 引用消息侧边栏实体
 * 存储用户在侧边栏中查看的引用消息的展示信息
 */
@Entity
@Table(name = "message_quote_sidebar",
       indexes = {
           @Index(name = "idx_quote_sidebar_user_session", columnList = "userId, sessionId"),
           @Index(name = "idx_quote_sidebar_created_at", columnList = "createdAt"),
           @Index(name = "idx_quote_sidebar_quote_id", columnList = "quoteId")
       })
public class MessageQuoteSidebarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 引用的消息ID
     */
    @Column(name = "quote_id", nullable = false)
    private Long quoteId;

    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;

    /**
     * 会话ID
     */
    @Column(name = "session_id", nullable = false)
    private Long sessionId;

    /**
     * 侧边栏位置索引（用于排序）
     */
    @Column(name = "sidebar_index")
    private Integer sidebarIndex;

    /**
     * 是否固定在侧边栏
     */
    @Column(name = "is_pinned")
    private Boolean isPinned = false;

    /**
     * 引用消息的预览内容（截取前100个字符）
     */
    @Column(name = "preview_content", length = 500)
    private String previewContent;

    /**
     * 引用消息的发送者ID
     */
    @Column(name = "sender_id")
    private Long senderId;

    /**
     * 引用消息的发送者昵称
     */
    @Column(name = "sender_nickname", length = 100)
    private String senderNickname;

    /**
     * 引用消息的类型：TEXT, IMAGE, VOICE, VIDEO, FILE, SYSTEM
     */
    @Column(name = "message_type", length = 20)
    private String messageType;

    /**
     * 引用消息的创建时间（原消息时间）
     */
    @Column(name = "original_created_at")
    private Instant originalCreatedAt;

    /**
     * 最后查看时间
     */
    @Column(name = "last_viewed_at")
    private Instant lastViewedAt;

    /**
     * 侧边栏记录创建时间
     */
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;

    /**
     * 更新时间
     */
    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }

    // 构造器
    public MessageQuoteSidebarEntity() {
    }

    public MessageQuoteSidebarEntity(Long quoteId, Long userId, Long sessionId, String previewContent, 
                                     Long senderId, String senderNickname, String messageType, 
                                     Instant originalCreatedAt) {
        this.quoteId = quoteId;
        this.userId = userId;
        this.sessionId = sessionId;
        this.previewContent = previewContent;
        this.senderId = senderId;
        this.senderNickname = senderNickname;
        this.messageType = messageType;
        this.originalCreatedAt = originalCreatedAt;
        this.sidebarIndex = 0;
        this.isPinned = false;
        this.lastViewedAt = Instant.now();
    }

    // Getter 和 Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getQuoteId() {
        return quoteId;
    }

    public void setQuoteId(Long quoteId) {
        this.quoteId = quoteId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getSessionId() {
        return sessionId;
    }

    public void setSessionId(Long sessionId) {
        this.sessionId = sessionId;
    }

    public Integer getSidebarIndex() {
        return sidebarIndex;
    }

    public void setSidebarIndex(Integer sidebarIndex) {
        this.sidebarIndex = sidebarIndex;
    }

    public Boolean getIsPinned() {
        return isPinned;
    }

    public void setIsPinned(Boolean pinned) {
        isPinned = pinned;
    }

    public String getPreviewContent() {
        return previewContent;
    }

    public void setPreviewContent(String previewContent) {
        this.previewContent = previewContent;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderNickname() {
        return senderNickname;
    }

    public void setSenderNickname(String senderNickname) {
        this.senderNickname = senderNickname;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public Instant getOriginalCreatedAt() {
        return originalCreatedAt;
    }

    public void setOriginalCreatedAt(Instant originalCreatedAt) {
        this.originalCreatedAt = originalCreatedAt;
    }

    public Instant getLastViewedAt() {
        return lastViewedAt;
    }

    public void setLastViewedAt(Instant lastViewedAt) {
        this.lastViewedAt = lastViewedAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Instant updatedAt) {
        this.updatedAt = updatedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageQuoteSidebarEntity that = (MessageQuoteSidebarEntity) o;
        return Objects.equals(id, that.id) && Objects.equals(quoteId, that.quoteId) 
               && Objects.equals(userId, that.userId) && Objects.equals(sessionId, that.sessionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, quoteId, userId, sessionId);
    }

    @Override
    public String toString() {
        return "MessageQuoteSidebarEntity{" +
                "id=" + id +
                ", quoteId=" + quoteId +
                ", userId=" + userId +
                ", sessionId=" + sessionId +
                ", sidebarIndex=" + sidebarIndex +
                ", isPinned=" + isPinned +
                ", previewContent='" + previewContent + '\'' +
                ", senderId=" + senderId +
                ", senderNickname='" + senderNickname + '\'' +
                ", messageType='" + messageType + '\'' +
                ", originalCreatedAt=" + originalCreatedAt +
                ", lastViewedAt=" + lastViewedAt +
                ", createdAt=" + createdAt +
                ", updatedAt=" + updatedAt +
                '}';
    }
}