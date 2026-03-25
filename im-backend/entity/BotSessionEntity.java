package com.im.backend.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * 机器人会话实体 - 跟踪用户与机器人的对话
 */
@Entity
@Table(name = "im_bot_session",
       indexes = {
           @Index(name = "idx_session_bot", columnList = "botId"),
           @Index(name = "idx_session_user", columnList = "userId"),
           @Index(name = "idx_session_conversation", columnList = "conversationId"),
           @Index(name = "idx_session_status", columnList = "status")
       })
public class BotSessionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 会话UUID */
    @Column(nullable = false, unique = true, length = 36)
    private String sessionId;

    /** 所属机器人ID */
    @Column(nullable = false, length = 36)
    private String botId;

    /** 用户ID */
    @Column(nullable = false, length = 36)
    private String userId;

    /** 关联会话ID (IM会话) */
    @Column(nullable = false, length = 36)
    private String conversationId;

    /** 对话上下文 (JSON格式存储历史消息摘要) */
    @Column(columnDefinition = "TEXT")
    private String contextJson;

    /** 上下文令牌数 */
    @Column(nullable = false)
    private Integer contextTokens;

    /** 对话轮次计数 */
    @Column(nullable = false)
    private Integer turnCount;

    /** 消耗令牌数 */
    @Column(nullable = false)
    private Long totalTokensUsed;

    /** 会话状态: ACTIVE/ENDED/TIMEOUT */
    @Column(nullable = false, length = 20)
    private String status;

    /** 结束原因: USER_END/BOT_END/TIMEOUT/ERROR */
    @Column(length = 30)
    private String endReason;

    /** 系统提示词版本 */
    @Column(length = 50)
    private String promptVersion;

    /** 创建时间 */
    @Column(nullable = false)
    private LocalDateTime createdAt;

    /** 最后消息时间 */
    private LocalDateTime lastMessageAt;

    /** 结束时间 */
    private LocalDateTime endedAt;

    // ========== Getters and Setters ==========

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getBotId() { return botId; }
    public void setBotId(String botId) { this.botId = botId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public String getContextJson() { return contextJson; }
    public void setContextJson(String contextJson) { this.contextJson = contextJson; }

    public Integer getContextTokens() { return contextTokens; }
    public void setContextTokens(Integer contextTokens) { this.contextTokens = contextTokens; }

    public Integer getTurnCount() { return turnCount; }
    public void setTurnCount(Integer turnCount) { this.turnCount = turnCount; }

    public Long getTotalTokensUsed() { return totalTokensUsed; }
    public void setTotalTokensUsed(Long totalTokensUsed) { this.totalTokensUsed = totalTokensUsed; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getEndReason() { return endReason; }
    public void setEndReason(String endReason) { this.endReason = endReason; }

    public String getPromptVersion() { return promptVersion; }
    public void setPromptVersion(String promptVersion) { this.promptVersion = promptVersion; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getLastMessageAt() { return lastMessageAt; }
    public void setLastMessageAt(LocalDateTime lastMessageAt) { this.lastMessageAt = lastMessageAt; }

    public LocalDateTime getEndedAt() { return endedAt; }
    public void setEndedAt(LocalDateTime endedAt) { this.endedAt = endedAt; }
}
