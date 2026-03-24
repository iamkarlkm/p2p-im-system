package com.im.backend.dto;

/**
 * 搜索请求DTO
 */
public class SearchRequest {

    private String query;
    private String index; // messages, users, groups
    private String conversationId;
    private Long userId; // filter by sender/receiver
    private Long fromUserId;
    private Long toUserId;
    private String type; // TEXT, IMAGE, FILE, AUDIO, VIDEO
    private String startTime; // ISO timestamp
    private String endTime;
    private Integer from; // pagination
    private Integer size; // default 20, max 100
    private String sortBy; // relevance, time, _score
    private String sortOrder; // asc, desc
    private Boolean fuzzy; // fuzzy matching
    private String highlightPreTag;
    private String highlightPostTag;
    private String groupId; // for group message search
    private Long sinceMessageId; // search since a specific message

    // Getters and Setters
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }

    public String getIndex() { return index; }
    public void setIndex(String index) { this.index = index; }

    public String getConversationId() { return conversationId; }
    public void setConversationId(String conversationId) { this.conversationId = conversationId; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public Long getFromUserId() { return fromUserId; }
    public void setFromUserId(Long fromUserId) { this.fromUserId = fromUserId; }

    public Long getToUserId() { return toUserId; }
    public void setToUserId(Long toUserId) { this.toUserId = toUserId; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public Integer getFrom() { return from; }
    public void setFrom(Integer from) { this.from = from; }

    public Integer getSize() { return size; }
    public void setSize(Integer size) { this.size = size; }

    public String getSortBy() { return sortBy; }
    public void setSortBy(String sortBy) { this.sortBy = sortBy; }

    public String getSortOrder() { return sortOrder; }
    public void setSortOrder(String sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getFuzzy() { return fuzzy; }
    public void setFuzzy(Boolean fuzzy) { this.fuzzy = fuzzy; }

    public String getHighlightPreTag() { return highlightPreTag; }
    public void setHighlightPreTag(String highlightPreTag) { this.highlightPreTag = highlightPreTag; }

    public String getHighlightPostTag() { return highlightPostTag; }
    public void setHighlightPostTag(String highlightPostTag) { this.highlightPostTag = highlightPostTag; }

    public String getGroupId() { return groupId; }
    public void setGroupId(String groupId) { this.groupId = groupId; }

    public Long getSinceMessageId() { return sinceMessageId; }
    public void setSinceMessageId(Long sinceMessageId) { this.sinceMessageId = sinceMessageId; }
}
