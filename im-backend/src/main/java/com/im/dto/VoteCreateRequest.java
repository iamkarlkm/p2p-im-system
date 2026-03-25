package com.im.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

public class VoteCreateRequest {
    @NotNull(message = "消息ID不能为空")
    private Long messageId;
    
    @NotNull(message = "群组ID不能为空")
    private Long groupId;
    
    @NotNull(message = "用户ID不能为空")
    private Long userId;
    
    @NotEmpty(message = "投票标题不能为空")
    @Size(max = 500, message = "标题不能超过500字符")
    private String title;
    
    @Size(max = 2000, message = "描述不能超过2000字符")
    private String description;
    
    @NotEmpty(message = "投票选项不能为空")
    @Size(min = 2, max = 10, message = "至少2个选项，最多10个选项")
    private List<String> options;
    
    @NotNull(message = "匿名设置不能为空")
    private Boolean isAnonymous = false;
    
    @NotNull(message = "多选设置不能为空")
    private Boolean allowMultipleChoice = false;
    
    private LocalDateTime endTime;
    
    // Getters and Setters
    public Long getMessageId() { return messageId; }
    public void setMessageId(Long messageId) { this.messageId = messageId; }
    
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public List<String> getOptions() { return options; }
    public void setOptions(List<String> options) { this.options = options; }
    
    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }
    
    public Boolean getAllowMultipleChoice() { return allowMultipleChoice; }
    public void setAllowMultipleChoice(Boolean allowMultipleChoice) { this.allowMultipleChoice = allowMultipleChoice; }
    
    public LocalDateTime getEndTime() { return endTime; }
    public void setEndTime(LocalDateTime endTime) { this.endTime = endTime; }
}