package com.im.backend.dto;

import com.im.backend.entity.GroupMessage;

/**
 * 群消息请求DTO
 * 对应功能 #15 - 群聊功能
 */
public class GroupMessageRequest {
    
    private Long groupId;
    private GroupMessage.MessageType type;
    private String content;
    private String extra;
    
    // Getters and Setters
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    
    public GroupMessage.MessageType getType() { return type; }
    public void setType(GroupMessage.MessageType type) { this.type = type; }
    
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    
    public String getExtra() { return extra; }
    public void setExtra(String extra) { this.extra = extra; }
}
