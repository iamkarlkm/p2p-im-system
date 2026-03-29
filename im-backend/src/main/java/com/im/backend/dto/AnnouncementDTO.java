package com.im.backend.dto;

import com.im.backend.model.GroupAnnouncement;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 公告请求DTO
 * 用于发布公告、编辑公告的请求参数
 */
public class AnnouncementDTO {

    /**
     * 公告ID（编辑时使用）
     */
    private Long announcementId;

    /**
     * 群组ID
     */
    @NotNull(message = "群组ID不能为空")
    private Long groupId;

    /**
     * 公告标题
     */
    @NotBlank(message = "公告标题不能为空")
    @Size(max = 200, message = "标题长度不能超过200字符")
    private String title;

    /**
     * 公告内容
     */
    @NotBlank(message = "公告内容不能为空")
    @Size(max = 10000, message = "内容长度不能超过10000字符")
    private String content;

    /**
     * 公告类型：NORMAL, IMPORTANT, PINNED
     */
    private String announcementType = "NORMAL";

    /**
     * 是否允许评论
     */
    private Boolean allowComment = false;

    /**
     * 附件列表
     */
    private List<AttachmentDTO> attachments;

    /**
     * 生效开始时间
     */
    private LocalDateTime effectiveStart;

    /**
     * 生效结束时间
     */
    private LocalDateTime effectiveEnd;

    /**
     * 是否发送通知
     */
    private Boolean sendNotification = true;

    /**
     * 通知范围：ALL, ADMIN, PARTIAL
     */
    private String notificationScope = "ALL";

    /**
     * 需要确认的成员ID列表（当notificationScope为PARTIAL时使用）
     */
    private Set<Long> notifyMemberIds;

    /**
     * 是否需要成员确认
     */
    private Boolean needConfirm = false;

    // Getters and Setters
    public Long getAnnouncementId() {
        return announcementId;
    }

    public void setAnnouncementId(Long announcementId) {
        this.announcementId = announcementId;
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

    public String getAnnouncementType() {
        return announcementType;
    }

    public void setAnnouncementType(String announcementType) {
        this.announcementType = announcementType;
    }

    public Boolean getAllowComment() {
        return allowComment;
    }

    public void setAllowComment(Boolean allowComment) {
        this.allowComment = allowComment;
    }

    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public LocalDateTime getEffectiveStart() {
        return effectiveStart;
    }

    public void setEffectiveStart(LocalDateTime effectiveStart) {
        this.effectiveStart = effectiveStart;
    }

    public LocalDateTime getEffectiveEnd() {
        return effectiveEnd;
    }

    public void setEffectiveEnd(LocalDateTime effectiveEnd) {
        this.effectiveEnd = effectiveEnd;
    }

    public Boolean getSendNotification() {
        return sendNotification;
    }

    public void setSendNotification(Boolean sendNotification) {
        this.sendNotification = sendNotification;
    }

    public String getNotificationScope() {
        return notificationScope;
    }

    public void setNotificationScope(String notificationScope) {
        this.notificationScope = notificationScope;
    }

    public Set<Long> getNotifyMemberIds() {
        return notifyMemberIds;
    }

    public void setNotifyMemberIds(Set<Long> notifyMemberIds) {
        this.notifyMemberIds = notifyMemberIds;
    }

    public Boolean getNeedConfirm() {
        return needConfirm;
    }

    public void setNeedConfirm(Boolean needConfirm) {
        this.needConfirm = needConfirm;
    }

    /**
     * 转换为实体对象
     */
    public GroupAnnouncement toEntity(Long publisherId) {
        GroupAnnouncement announcement = new GroupAnnouncement();
        announcement.setGroupId(this.groupId);
        announcement.setPublisherId(publisherId);
        announcement.setTitle(this.title);
        announcement.setContent(this.content);
        announcement.setAnnouncementType(
            GroupAnnouncement.AnnouncementType.valueOf(this.announcementType)
        );
        announcement.setAllowComment(this.allowComment);
        announcement.setEffectiveStart(this.effectiveStart);
        announcement.setEffectiveEnd(this.effectiveEnd);
        announcement.setSendNotification(this.sendNotification);
        if (this.notificationScope != null) {
            announcement.setNotificationScope(
                GroupAnnouncement.NotificationScope.valueOf(this.notificationScope)
            );
        }
        return announcement;
    }

    /**
     * 附件DTO内部类
     */
    public static class AttachmentDTO {
        private String fileName;
        private String fileUrl;
        private String fileType;
        private Long fileSize;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public String getFileUrl() {
            return fileUrl;
        }

        public void setFileUrl(String fileUrl) {
            this.fileUrl = fileUrl;
        }

        public String getFileType() {
            return fileType;
        }

        public void setFileType(String fileType) {
            this.fileType = fileType;
        }

        public Long getFileSize() {
            return fileSize;
        }

        public void setFileSize(Long fileSize) {
            this.fileSize = fileSize;
        }
    }
}
