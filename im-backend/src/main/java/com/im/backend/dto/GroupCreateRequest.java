package com.im.backend.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.util.List;

/**
 * 创建群组请求DTO
 * 功能 #5: 群组管理基础模块
 */
@Data
public class GroupCreateRequest {

    /**
     * 群组名称
     */
    @NotBlank(message = "群组名称不能为空")
    @Size(min = 1, max = 50, message = "群组名称长度必须在1-50个字符之间")
    private String name;

    /**
     * 群组描述
     */
    @Size(max = 500, message = "群组描述不能超过500个字符")
    private String description;

    /**
     * 群组头像URL
     */
    @Size(max = 500, message = "头像URL长度不能超过500")
    private String avatar;

    /**
     * 群组类型: 0-普通群 1-付费群 2-企业群
     */
    @Min(value = 0, message = "群组类型不合法")
    @Max(value = 2, message = "群组类型不合法")
    private Integer groupType = 0;

    /**
     * 最大成员数
     */
    @Min(value = 2, message = "群组至少需要2人")
    @Max(value = 2000, message = "群组成员数不能超过2000")
    private Integer maxMemberCount = 500;

    /**
     * 入群验证方式: 0-无需验证 1-需要验证 2-禁止加入
     */
    @Min(value = 0, message = "入群验证方式不合法")
    @Max(value = 2, message = "入群验证方式不合法")
    private Integer joinType = 0;

    /**
     * 初始成员列表(用户ID列表)
     */
    @Size(max = 1999, message = "初始成员数不能超过最大成员数限制")
    private List<Long> initialMembers;

    /**
     * 群组公告
     */
    @Size(max = 2000, message = "公告长度不能超过2000个字符")
    private String announcement;

    /**
     * 扩展字段(JSON格式)
     */
    @Size(max = 4000, message = "扩展字段长度不能超过4000")
    private String extra;

    /**
     * 验证请求参数
     */
    public void validate() {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("群组名称不能为空");
        }
        if (maxMemberCount != null && initialMembers != null) {
            if (initialMembers.size() >= maxMemberCount) {
                throw new IllegalArgumentException("初始成员数不能超过最大成员数限制");
            }
        }
    }
}
