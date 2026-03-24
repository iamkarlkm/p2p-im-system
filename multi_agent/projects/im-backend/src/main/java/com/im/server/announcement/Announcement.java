package com.im.server.announcement;

import java.time.LocalDateTime;
import java.util.*;

/**
 * 群公告实体
 * 支持Markdown富文本、置顶、编辑、历史记录
 */
public class Announcement {
    
    public enum AnnouncementType {
        NORMAL,      // 普通公告
        IMPORTANT,   // 重要公告
        PINNED       // 置顶公告
    }
    
    private String announcementId;
    private String groupId;
    private String authorId;
    private String authorName;
    private String title;
    private String content;          // Markdown内容
    private AnnouncementType type;
    private boolean pinned;           // 是否置顶
    private boolean edited;           // 是否被编辑过
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime pinnedAt;    // 置顶时间
    private int viewCount;            // 查看次数
    private int confirmCount;         // 确认人数
    private Set<String> confirmedUserIds; // 已确认用户
    private boolean deleted;
    private LocalDateTime deletedAt;

    public Announcement() {
        this.announcementId = UUID.randomUUID().toString();
        this.type = AnnouncementType.NORMAL;
        this.pinned = false;
        this.edited = false;
        this.createdAt = LocalDateTime.now();
        this.viewCount = 0;
        this.confirmCount = 0;
        this.confirmedUserIds = new HashSet<>();
        this.deleted = false;
    }

    // Builder模式
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final Announcement announcement = new Announcement();

        public Builder groupId(String groupId) {
            announcement.groupId = groupId;
            return this;
        }

        public Builder authorId(String authorId) {
            announcement.authorId = authorId;
            return this;
        }

        public Builder authorName(String authorName) {
            announcement.authorName = authorName;
            return this;
        }

        public Builder title(String title) {
            announcement.title = title;
            return this;
        }

        public Builder content(String content) {
            announcement.content = content;
            return this;
        }

        public Builder type(AnnouncementType type) {
            announcement.type = type;
            return this;
        }

        public Builder pinned(boolean pinned) {
            announcement.pinned = pinned;
            return this;
        }

        public Announcement build() {
            return announcement;
        }
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

    public AnnouncementType getType() {
        return type;
    }

    public void setType(AnnouncementType type) {
        this.type = type;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
        if (pinned) {
            this.pinnedAt = LocalDateTime.now();
        }
    }

    public boolean isEdited() {
        return edited;
    }

    public void setEdited(boolean edited) {
        this.edited = edited;
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

    public LocalDateTime getPinnedAt() {
        return pinnedAt;
    }

    public void setPinnedAt(LocalDateTime pinnedAt) {
        this.pinnedAt = pinnedAt;
    }

    public int getViewCount() {
        return viewCount;
    }

    public void setViewCount(int viewCount) {
        this.viewCount = viewCount;
    }

    public int getConfirmCount() {
        return confirmCount;
    }

    public void setConfirmCount(int confirmCount) {
        this.confirmCount = confirmCount;
    }

    public Set<String> getConfirmedUserIds() {
        return confirmedUserIds;
    }

    public void setConfirmedUserIds(Set<String> confirmedUserIds) {
        this.confirmedUserIds = confirmedUserIds;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
        if (deleted) {
            this.deletedAt = LocalDateTime.now();
        }
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    // 业务方法
    public void incrementViewCount() {
        this.viewCount++;
    }

    public boolean confirm(String userId) {
        if (confirmedUserIds.add(userId)) {
            this.confirmCount = confirmedUserIds.size();
            return true;
        }
        return false;
    }

    public boolean unconfirm(String userId) {
        if (confirmedUserIds.remove(userId)) {
            this.confirmCount = confirmedUserIds.size();
            return true;
        }
        return false;
    }

    public boolean hasConfirmed(String userId) {
        return confirmedUserIds.contains(userId);
    }

    public void updateContent(String title, String content) {
        this.title = title;
        this.content = content;
        this.updatedAt = LocalDateTime.now();
        this.edited = true;
    }

    public void pin() {
        this.pinned = true;
        this.pinnedAt = LocalDateTime.now();
        this.type = AnnouncementType.PINNED;
    }

    public void unpin() {
        this.pinned = false;
        this.pinnedAt = null;
        this.type = AnnouncementType.NORMAL;
    }

    public void softDelete() {
        this.deleted = true;
        this.deletedAt = LocalDateTime.now();
    }

    // 转为Map用于JSON序列化
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("announcementId", announcementId);
        map.put("groupId", groupId);
        map.put("authorId", authorId);
        map.put("authorName", authorName);
        map.put("title", title);
        map.put("content", content);
        map.put("type", type.name());
        map.put("pinned", pinned);
        map.put("edited", edited);
        map.put("createdAt", createdAt != null ? createdAt.toString() : null);
        map.put("updatedAt", updatedAt != null ? updatedAt.toString() : null);
        map.put("pinnedAt", pinnedAt != null ? pinnedAt.toString() : null);
        map.put("viewCount", viewCount);
        map.put("confirmCount", confirmCount);
        map.put("deleted", deleted);
        return map;
    }
}
