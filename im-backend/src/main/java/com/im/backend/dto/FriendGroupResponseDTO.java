package com.im.backend.dto;

import com.im.backend.model.UserFriendGroupMember;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 好友分组响应DTO
 */
public class FriendGroupResponseDTO {

    private Long id;
    private Long userId;
    private String groupName;
    private String description;
    private Integer sortOrder;
    private Boolean isDefault;
    private Integer memberCount;
    private Integer maxMembers;
    private String colorTag;
    private String icon;
    private Boolean isVisible;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<FriendGroupMemberDTO> members;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }

    public Boolean getIsDefault() {
        return isDefault;
    }

    public void setIsDefault(Boolean isDefault) {
        this.isDefault = isDefault;
    }

    public Integer getMemberCount() {
        return memberCount;
    }

    public void setMemberCount(Integer memberCount) {
        this.memberCount = memberCount;
    }

    public Integer getMaxMembers() {
        return maxMembers;
    }

    public void setMaxMembers(Integer maxMembers) {
        this.maxMembers = maxMembers;
    }

    public String getColorTag() {
        return colorTag;
    }

    public void setColorTag(String colorTag) {
        this.colorTag = colorTag;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Boolean getIsVisible() {
        return isVisible;
    }

    public void setIsVisible(Boolean isVisible) {
        this.isVisible = isVisible;
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

    public List<FriendGroupMemberDTO> getMembers() {
        return members;
    }

    public void setMembers(List<FriendGroupMemberDTO> members) {
        this.members = members;
    }

    /**
     * 内部分组成员DTO
     */
    public static class FriendGroupMemberDTO {
        private Long id;
        private Long friendId;
        private String friendNickname;
        private String friendAvatar;
        private Integer sortOrder;
        private String displayName;
        private Boolean isStarred;
        private Boolean isMuted;
        private String remark;
        private LocalDateTime addedAt;

        // Getters and Setters
        public Long getId() {
            return id;
        }

        public void setId(Long id) {
            this.id = id;
        }

        public Long getFriendId() {
            return friendId;
        }

        public void setFriendId(Long friendId) {
            this.friendId = friendId;
        }

        public String getFriendNickname() {
            return friendNickname;
        }

        public void setFriendNickname(String friendNickname) {
            this.friendNickname = friendNickname;
        }

        public String getFriendAvatar() {
            return friendAvatar;
        }

        public void setFriendAvatar(String friendAvatar) {
            this.friendAvatar = friendAvatar;
        }

        public Integer getSortOrder() {
            return sortOrder;
        }

        public void setSortOrder(Integer sortOrder) {
            this.sortOrder = sortOrder;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public Boolean getIsStarred() {
            return isStarred;
        }

        public void setIsStarred(Boolean isStarred) {
            this.isStarred = isStarred;
        }

        public Boolean getIsMuted() {
            return isMuted;
        }

        public void setIsMuted(Boolean isMuted) {
            this.isMuted = isMuted;
        }

        public String getRemark() {
            return remark;
        }

        public void setRemark(String remark) {
            this.remark = remark;
        }

        public LocalDateTime getAddedAt() {
            return addedAt;
        }

        public void setAddedAt(LocalDateTime addedAt) {
            this.addedAt = addedAt;
        }
    }
}
