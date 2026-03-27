package com.im.backend.dto;

import jakarta.validation.constraints.NotNull;

/**
 * 移动好友到分组请求DTO
 */
public class MoveFriendToGroupDTO {

    @NotNull(message = "好友ID不能为空")
    private Long friendId;

    @NotNull(message = "目标分组ID不能为空")
    private Long targetGroupId;

    private Integer sortOrder;

    private String displayName;

    // Getters and Setters
    public Long getFriendId() {
        return friendId;
    }

    public void setFriendId(Long friendId) {
        this.friendId = friendId;
    }

    public Long getTargetGroupId() {
        return targetGroupId;
    }

    public void setTargetGroupId(Long targetGroupId) {
        this.targetGroupId = targetGroupId;
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
}
