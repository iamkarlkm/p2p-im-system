package com.im.backend.dto;

import java.time.LocalDateTime;

/**
 * 消息搜索请求DTO
 */
public class MessageSearchRequest {

    private String keyword;
    private String searchType; // ALL, PRIVATE, GROUP
    private Long conversationId;
    private Long senderId;
    private String contentType; // TEXT, IMAGE, FILE, VOICE
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private Integer page = 0;
    private Integer size = 20;
    private Boolean saveHistory = true; // 是否保存到搜索历史

    // Getters and Setters
    public String getKeyword() { return keyword; }
    public void setKeyword(String keyword) { this.keyword = keyword; }

    public String getSearchType() { return searchType; }
    public void setSearchType(String searchType) { this.searchType = searchType; }

    public Long getConversationId() { return conversationId; }
    public void setConversationId(Long conversationId) { this.conversationId = conversationId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public LocalDateTime getStartTime() { return startTime; }
    public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }

    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }

    public Integer getPage() { return page; }
    public void setPage(Integer page) { this.page = page; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public Boolean getSaveHistory() { return saveHistory; }
    public void setSaveHistory(Boolean saveHistory) { this.saveHistory = saveHistory; }
}
