package com.im.service.group.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 更新成员角色请求DTO
 *
 * @author IM System
 * @since 1.0.0
 */
@Data
public class UpdateMemberRoleRequest {

    /**
     * 成员角色：0-成员，1-管理员，2-群主
     */
    @NotNull(message = "角色不能为空")
    @Min(value = 0, message = "角色无效")
    @Max(value = 2, message = "角色无效")
    private Integer role;

    /**
     * 是否有邀请权限
     */
    private Boolean canInvite;

    /**
     * 是否有禁言权限
     */
    private Boolean canMute;

    /**
     * 是否有修改群信息权限
     */
    private Boolean canModifyInfo;

    /**
     * 是否有移除成员权限
     */
    private Boolean canRemoveMember;
}
