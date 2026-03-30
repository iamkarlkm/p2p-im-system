package com.im.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.List;

/**
 * 群公告创建/更新请求DTO
 * 功能ID: #30
 * 功能名称: 群公告
 * 
 * @author developer-agent
 * @since 2026-03-30
 */
public class GroupAnnouncementRequest {

    /** 群组ID */
    @NotNull(message = "群组ID不能为空")
    private Long groupId;

    /** 公告标题 */
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;

    /** 公告内容 */
    @NotBlank(message = "公告内容不能为空")
    @Size(max = 5000, message = "内容长度不能超过5000字符")
    private String content;

    /** 是否置顶 */
    private Boolean pinned = false;

    /** 附件URL列表 */
    private List<String> attachments;

    // 构造函数
    public GroupAnnouncementRequest() {}

    public GroupAnnouncementRequest(Long groupId, String title, String content) {
        this.groupId = groupId;
        this.title = title;
        this.content = content;
    }

    // Getters and Setters
    public Long getGroupId() {
        return groupId;
    }

    public void setGroupId(Long groupId) {
        this.groupId = groupId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    @Override
    public String toString() {
        return "GroupAnnouncementRequest{" +
                "groupId=" + groupId +
                ", title='" + title + '\'' +
                ", contentLength=" + (content != null ? content.length() : 0) +
                ", pinned=" + pinned +
                ", attachmentsCount=" + (attachments != null ? attachments.size() : 0) +
                '}';
    }
}
