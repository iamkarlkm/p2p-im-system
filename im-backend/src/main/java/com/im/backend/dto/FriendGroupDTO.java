package com.im.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * 好友分组请求DTO
 */
public class FriendGroupDTO {

    @NotBlank(message = "分组名称不能为空")
    @Size(max = 50, message = "分组名称不能超过50个字符")
    private String groupName;

    @Size(max = 200, message = "分组描述不能超过200个字符")
    private String description;

    private Integer sortOrder;

    @Size(max = 20, message = "颜色标识不能超过20个字符")
    private String colorTag;

    @Size(max = 50, message = "图标不能超过50个字符")
    private String icon;

    // Getters and Setters
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
}
