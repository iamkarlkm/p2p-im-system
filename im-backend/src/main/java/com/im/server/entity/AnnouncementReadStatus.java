package com.im.server.entity;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 公告已读状态实体
 * 
 * @author IM System
 * @version 1.0
 */
public class AnnouncementReadStatus {

    private String statusId;               // 状态ID
    private String announcementId;         // 公告ID
    private String userId;                // 用户ID
    private boolean read;                  // 是否已读
    private LocalDateTime readAt;          // 阅读时间
    private LocalDateTime createdAt;      // 创建时间

    public AnnouncementReadStatus() {
        this.read = false;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and Setters
    public String getStatusId() {
        return statusId;
    }

    public void setStatusId(String statusId) {
        this.statusId = statusId;
    }

    public String getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(String announcementId) {
        this.announcementId = announcementId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Builder模式
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private final AnnouncementReadStatus status;

        public Builder() {
            this.status = new AnnouncementReadStatus();
        }

        public Builder statusId(String statusId) {
            status.setStatusId(statusId);
            return this;
        }

        public Builder announcementId(String announcementId) {
            status.setAnnouncementId(announcementId);
            return this;
        }

        public Builder userId(String userId) {
            status.setUserId(userId);
            return this;
        }

        public Builder read(boolean read) {
            status.setRead(read);
            return this;
        }

        public Builder readAt(LocalDateTime readAt) {
            status.setReadAt(readAt);
            return this;
        }

        public AnnouncementReadStatus build() {
            return status;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnnouncementReadStatus that = (AnnouncementReadStatus) o;
        return Objects.equals(statusId, that.statusId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(statusId);
    }

    @Override
    public String toString() {
        return "AnnouncementReadStatus{" +
                "statusId='" + statusId + '\'' +
                ", announcementId='" + announcementId + '\'' +
                ", userId='" + userId + '\'' +
                ", read=" + read +
                '}';
    }
}
