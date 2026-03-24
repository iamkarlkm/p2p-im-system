package com.im.backend.dto;

import java.time.LocalDateTime;

/**
 * 过期规则请求DTO
 */
public class ExpirationRuleRequest {

    private Long id;
    private String conversationId;
    private String conversationType;
    private String expirationType;   // READ_AFTER, SELF_DESTRUCT, TIME_BASED, GLOBAL
    private LocalDateTime expireTime;
    private Long relativeSeconds;     // 相对秒数，如创建后N秒/阅后N秒
    private Boolean active;
    private String messageTypeFilter; // TEXT, IMAGE, FILE, ALL
    private Long readDestroySeconds;  // 阅后N秒销毁
    private Boolean preExpireNotice;
    private Long preExpireNoticeSeconds;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getMessageTypeFilter() { return messageTypeFilter; }
    public void setMessageTypeFilter(String messageTypeFilter) { this.messageTypeFilter = messageTypeFilter; }

    public Long getReadDestroySeconds() { return readDestroySeconds; }
    public void setReadDestroySeconds(Long readDestroySeconds) { this.readDestroySeconds = readDestroySeconds; }

    public Boolean getPreExpireNotice() { return preExpireNotice; }
    public void setPreExpireNotice(Boolean preExpireNotice) { this.preExpireNotice = preExpireNotice; }

    public Long getPreExpireNoticeSeconds() { return preExpireNoticeSeconds; }
    public void setPreExpireNoticeSeconds(Long preExpireNoticeSeconds) { this.preExpireNoticeSeconds = preExpireNoticeSeconds; }
}
