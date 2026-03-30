package com.im.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 群公告响应DTO
 * 功能ID: #30
 * 功能名称: 群公告
 * 
 * @author developer-agent
 * @since 2026-03-30
 */
public class GroupAnnouncementResponse {

    /** 公告ID */
    private Long id;

    /** 群组ID */
    private Long groupId;

    /** 公告标题 */
    private String title;

    /** 公告内容 */
    private String content;

    /** 创建者ID */
    private Long creatorId;

    /** 创建者昵称 */
    private String creatorNickname;

    /** 创建者头像 */
    private String creatorAvatar;

    /** 是否置顶 */
    private Boolean pinned;

    /** 置顶时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime pinnedAt;

    /** 是否已确认 */
    private Boolean confirmed;

    /** 已读人数 */
    private Integer readCount;

    /** 群组总人数 */
    private Integer totalMembers;

    /** 已读百分比 */
    private Double readPercentage;

    /** 附件URL列表 */
    private List<String> attachments;

    /** 创建时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    /** 更新时间 */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;

    /** 当前用户是否已读 */
    private Boolean hasRead;

    // 构造函数
    public GroupAnnouncementResponse() {}

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorNickname() {
        return creatorNickname;
    }

    public void setCreatorNickname(String creatorNickname) {
        this.creatorNickname = creatorNickname;
    }

    public String getCreatorAvatar() {
        return creatorAvatar;
    }

    public void setCreatorAvatar(String creatorAvatar) {
        this.creatorAvatar = creatorAvatar;
    }

    public Boolean getPinned() {
        return pinned;
    }

    public void setPinned(Boolean pinned) {
        this.pinned = pinned;
    }

    public LocalDateTime getPinnedAt() {
        return pinnedAt;
    }

    public void setPinnedAt(LocalDateTime pinnedAt) {
        this.pinnedAt = pinnedAt;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public Integer getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(Integer totalMembers) {
        this.totalMembers = totalMembers;
    }

    public Double getReadPercentage() {
        return readPercentage;
    }

    public void setReadPercentage(Double readPercentage) {
        this.readPercentage = readPercentage;
    }

    public List<String> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<String> attachments) {
        this.attachments = attachments;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Boolean getHasRead() {
        return hasRead;
    }

    public void setHasRead(Boolean hasRead) {
        this.hasRead = hasRead;
    }

    @Override
    public String toString() {
        return "GroupAnnouncementResponse{" +
                "id=" + id +
                ", groupId=" + groupId +
                ", title='" + title + '\'' +
                ", creatorId=" + creatorId +
                ", pinned=" + pinned +
                ", readCount=" + readCount +
                ", totalMembers=" + totalMembers +
                ", readPercentage=" + readPercentage +
                ", confirmed=" + confirmed +
                ", createdAt=" + createdAt +
                '}';
    }
}
