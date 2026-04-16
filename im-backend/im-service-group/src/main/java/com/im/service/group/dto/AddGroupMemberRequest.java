package com.im.service.group.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

import java.util.List;

/**
 * 添加群成员请求DTO
 *
 * @author IM System
 * @since 1.0.0
 */
@Data
public class AddGroupMemberRequest {

    /**
     * 要添加的用户ID列表
     */
    @NotEmpty(message = "至少选择一个用户")
    @Size(max = 50, message = "一次最多添加50个成员")
    private List<Long> userIds;

    /**
     * 邀请人ID（可选，如果是成员邀请）
     */
    private Long invitedBy;

    /**
     * 进群方式：0-创建，1-邀请，2-扫码，3-链接，4-搜索加入
     */
    @Min(value = 0, message = "进群方式无效")
    @Max(value = 4, message = "进群方式无效")
    private Integer joinType = 1;

    /**
     * 验证消息（如果是需要验证的进群方式）
     */
    @Size(max = 200, message = "验证消息不能超过200个字符")
    private String verifyMessage;
}
