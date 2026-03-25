package com.im.server.entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 群公告实体类
 * 
 * @author IM System
 * @version 1.0
 */
public class GroupAnnouncement {

    private String announcementId;        // 公告ID
    private String groupId;                // 群ID
    private String authorId;               // 发布者ID
    private String authorName;             // 发布者昵称
    private String authorAvatar;            // 发布者头像
    private String title;                  // 公告标题
    private String content;                // 公告内容（支持Markdown）
    private String summary;                // 内容摘要（用于预览）
    private boolean pinned;                // 是否置顶
    private boolean edited;                // 是否被编辑过
    private LocalDateTime editedAt;        // 编辑时间
    private LocalDateTime createdAt;       // 创建时间
    private LocalDateTime updatedAt;      // 更新时间
    private boolean deleted;               // 是否已删除（软删除）
    private int readCount;                // 已读人数
    private int unreadCount;               // 未读人数
    private int commentCount;              // 评论数

    public GroupAnnouncement() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        this.pinned = false;
        this.edited = false;
        this.deleted = false;
        this.readCount = 0;
        this.unreadCount = 0;
        this.commentCount = 0;
    }

    // Getters and Setters
    public String getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(String announcementId) {
        this.announcementId = announcementId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getAuthorAvatar() {
        return authorAvatar;
    }

    public void setAuthorAvatar(String authorAvatar) {
        this.authorAvatar = authorAvatar;
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
        // 自动生成摘要
        if (content != null && content.length() > 100) {
            this.summary = content.substring(0, 100) + "...";
        } else {
            this.summary = content;
        }
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
    }

    public LocalDateTime getEditedAt() {
        return editedAt;
    }

    public void setEditedAt(LocalDateTime editedAt) {
        this.editedAt = editedAt;
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

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getReadCount() {
        return readCount;
    }

    public void setReadCount(int readCount) {
        this.readCount = readCount;
    }

    public int getUnreadCount() {
        return unreadCount;
    }

    public void setUnreadCount(int unreadCount) {
        this.unreadCount = unreadCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    // Builder模式
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final GroupAnnouncement announcement;

        public Builder() {
            this.announcement = new GroupAnnouncement();
        }

        public Builder announcementId(String announcementId) {
            announcement.setAnnouncementId(announcementId);
            return this;
        }

        public Builder groupId(String groupId) {
            announcement.setGroupId(groupId);
            return this;
        }

        public Builder authorId(String authorId) {
            announcement.setAuthorId(authorId);
            return this;
        }

        public Builder authorName(String authorName) {
            announcement.setAuthorName(authorName);
            return this;
        }

        public Builder authorAvatar(String authorAvatar) {
            announcement.setAuthorAvatar(authorAvatar);
            return this;
        }

        public Builder title(String title) {
            announcement.setTitle(title);
            return this;
        }

        public Builder content(String content) {
            announcement.setContent(content);
            return this;
        }

        public Builder summary(String summary) {
            announcement.setSummary(summary);
            return this;
        }

        public Builder pinned(boolean pinned) {
            announcement.setPinned(pinned);
            return this;
        }

        public Builder createdAt(LocalDateTime createdAt) {
            announcement.setCreatedAt(createdAt);
            return this;
        }

        public Builder updatedAt(LocalDateTime updatedAt) {
            announcement.setUpdatedAt(updatedAt);
            return this;
        }

        public GroupAnnouncement build() {
            return announcement;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GroupAnnouncement that = (GroupAnnouncement) o;
        return Objects.equals(announcementId, that.announcementId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(announcementId);
    }

    @Override
    public String toString() {
        return "GroupAnnouncement{" +
                "announcementId='" + announcementId + '\'' +
                ", groupId='" + groupId + '\'' +
                ", authorId='" + authorId + '\'' +
                ", title='" + title + '\'' +
                ", pinned=" + pinned +
                ", deleted=" + deleted +
                '}';
    }
}
