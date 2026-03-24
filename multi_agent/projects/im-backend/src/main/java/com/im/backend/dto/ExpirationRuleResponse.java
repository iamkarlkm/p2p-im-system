package com.im.backend.dto;

import java.time.LocalDateTime;

/**
 * 过期规则响应DTO
 */
public class ExpirationRuleResponse {

    private Long id;
    private Long userId;
    private String conversationId;
    private String conversationType;
    private String expirationType;
    private LocalDateTime expireTime;
    private Long relativeSeconds;
    private Boolean active;
    private Boolean globalDefault;
    private String messageTypeFilter;
    private Long readDestroySeconds;
    private Boolean preExpireNotice;
    private Long preExpireNoticeSeconds;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private Long remainingSeconds; // 剩余过期秒数（用于前端倒计时）

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

    public Long getRemainingSeconds() { return remainingSeconds; }
    public void setRemainingSeconds(Long remainingSeconds) { this.remainingSeconds = remainingSeconds; }
}
