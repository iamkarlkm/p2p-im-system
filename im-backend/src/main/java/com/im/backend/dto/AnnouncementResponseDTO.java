package com.im.backend.dto;

import com.im.backend.model.GroupAnnouncement;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

/**
 * 公告响应DTO
 * 用于返回公告详情列表
 */
public class AnnouncementResponseDTO {

    /**
     * 公告ID
     */
    private Long id;

    /**
     * 群组ID
     */
    private Long groupId;

    /**
     * 发布者信息
     */
    private UserInfoDTO publisher;

    /**
     * 公告标题
     */
    private String title;

    /**
     * 公告内容
     */
    private String content;

    /**
     * 公告状态
     */
    private String status;

    /**
     * 公告类型
     */
    private String announcementType;

    /**
     * 是否允许评论
     */
    private Boolean allowComment;

    /**
     * 阅读人数
     */
    private Integer readCount;

    /**
     * 确认人数
     */
    private Integer confirmCount;

    /**
     * 需要确认的人数
     */
    private Integer needConfirmCount;

    /**
     * 群组成员总数
     */
    private Integer totalMembers;

    /**
     * 阅读率
     */
    private Double readRate;

    /**
     * 确认率
     */
    private Double confirmRate;

    /**
     * 附件列表
     */
    private List<AttachmentDTO> attachments;

    /**
     * 是否已读
     */
    private Boolean hasRead;

    /**
     * 是否已确认
     */
    private Boolean hasConfirmed;

    /**
     * 撤回时间
     */
    private LocalDateTime withdrawnAt;

    /**
     * 撤回人信息
     */
    private UserInfoDTO withdrawnBy;

    /**
     * 撤回原因
     */
    private String withdrawReason;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;

    /**
     * 已读用户ID集合（仅管理员可见）
     */
    private Set<Long> readUserIds;

    /**
     * 确认用户ID集合（仅管理员可见）
     */
    private Set<Long> confirmedUserIds;

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

    public UserInfoDTO getPublisher() {
        return publisher;
    }

    public void setPublisher(UserInfoDTO publisher) {
        this.publisher = publisher;
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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public Integer getReadCount() {
        return readCount;
    }

    public void setReadCount(Integer readCount) {
        this.readCount = readCount;
    }

    public Integer getConfirmCount() {
        return confirmCount;
    }

    public void setConfirmCount(Integer confirmCount) {
        this.confirmCount = confirmCount;
    }

    public Integer getNeedConfirmCount() {
        return needConfirmCount;
    }

    public void setNeedConfirmCount(Integer needConfirmCount) {
        this.needConfirmCount = needConfirmCount;
    }

    public Integer getTotalMembers() {
        return totalMembers;
    }

    public void setTotalMembers(Integer totalMembers) {
        this.totalMembers = totalMembers;
    }

    public Double getReadRate() {
        return readRate;
    }

    public void setReadRate(Double readRate) {
        this.readRate = readRate;
    }

    public Double getConfirmRate() {
        return confirmRate;
    }

    public void setConfirmRate(Double confirmRate) {
        this.confirmRate = confirmRate;
    }

    public List<AttachmentDTO> getAttachments() {
        return attachments;
    }

    public void setAttachments(List<AttachmentDTO> attachments) {
        this.attachments = attachments;
    }

    public Boolean getHasRead() {
        return hasRead;
    }

    public void setHasRead(Boolean hasRead) {
        this.hasRead = hasRead;
    }

    public Boolean getHasConfirmed() {
        return hasConfirmed;
    }

    public void setHasConfirmed(Boolean hasConfirmed) {
        this.hasConfirmed = hasConfirmed;
    }

    public LocalDateTime getWithdrawnAt() {
        return withdrawnAt;
    }

    public void setWithdrawnAt(LocalDateTime withdrawnAt) {
        this.withdrawnAt = withdrawnAt;
    }

    public UserInfoDTO getWithdrawnBy() {
        return withdrawnBy;
    }

    public void setWithdrawnBy(UserInfoDTO withdrawnBy) {
        this.withdrawnBy = withdrawnBy;
    }

    public String getWithdrawReason() {
        return withdrawReason;
    }

    public void setWithdrawReason(String withdrawReason) {
        this.withdrawReason = withdrawReason;
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

    public Set<Long> getReadUserIds() {
        return readUserIds;
    }

    public void setReadUserIds(Set<Long> readUserIds) {
        this.readUserIds = readUserIds;
    }

    public Set<Long> getConfirmedUserIds() {
        return confirmedUserIds;
    }

    public void setConfirmedUserIds(Set<Long> confirmedUserIds) {
        this.confirmedUserIds = confirmedUserIds;
    }

    /**
     * 从实体转换为DTO
     */
    public static AnnouncementResponseDTO fromEntity(GroupAnnouncement announcement) {
        AnnouncementResponseDTO dto = new AnnouncementResponseDTO();
        dto.setId(announcement.getId());
        dto.setGroupId(announcement.getGroupId());
        dto.setTitle(announcement.getTitle());
        dto.setContent(announcement.getContent());
        dto.setStatus(announcement.getStatus().name());
        dto.setAnnouncementType(announcement.getAnnouncementType().name());
        dto.setAllowComment(announcement.getAllowComment());
        dto.setReadCount(announcement.getReadCount());
        dto.setConfirmCount(announcement.getConfirmCount());
        dto.setNeedConfirmCount(announcement.getNeedConfirmCount());
        dto.setTotalMembers(announcement.getTotalMembers());
        dto.setReadRate(announcement.getReadRate());
        dto.setConfirmRate(announcement.getConfirmRate());
        dto.setWithdrawnAt(announcement.getWithdrawnAt());
        dto.setWithdrawReason(announcement.getWithdrawReason());
        dto.setCreatedAt(announcement.getCreatedAt());
        dto.setUpdatedAt(announcement.getUpdatedAt());
        return dto;
    }

    /**
     * 附件DTO
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

    /**
     * 用户信息DTO
     */
    public static class UserInfoDTO {
        private Long id;
        private String nickname;
        private String avatar;

        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getAvatar() {
            return avatar;
        }

        public void setAvatar(String avatar) {
            this.avatar = avatar;
        }
    }
}
