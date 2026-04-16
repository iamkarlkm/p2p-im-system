package com.im.service.group.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 更新群组信息请求DTO
 *
 * @author IM System
 * @since 1.0.0
 */
@Data
public class UpdateGroupRequest {

    /**
     * 群组名称
     */
    @Size(max = 50, message = "群组名称不能超过50个字符")
    private String name;

    /**
     * 群组头像URL
     */
    @Size(max = 500, message = "头像URL不能超过500个字符")
    private String avatar;

    /**
     * 群组描述/简介
     */
    @Size(max = 500, message = "群组描述不能超过500个字符")
    private String description;

    /**
     * 加入方式：0-自由加入，1-需验证，2-邀请加入，3-禁止加入
     */
    @Min(value = 0, message = "加入方式无效")
    @Max(value = 3, message = "加入方式无效")
    private Integer joinType;

    /**
     * 发言权限：0-所有人可发言，1-仅管理员可发言
     */
    @Min(value = 0, message = "发言权限无效")
    @Max(value = 1, message = "发言权限无效")
    private Integer speakPermission;

    /**
     * 是否允许成员邀请
     */
    private Boolean allowMemberInvite;

    /**
     * 是否允许成员修改群名
     */
    private Boolean allowMemberModifyName;

    /**
     * 是否开启群验证
     */
    private Boolean enableVerify;

    /**
     * 群最大成员数
     */
    @Min(value = 2, message = "群组至少支持2人")
    @Max(value = 2000, message = "群组最大支持2000人")
    private Integer maxMembers;
}
