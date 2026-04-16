package com.im.service.group.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

/**
 * 禁言成员请求DTO
 *
 * @author IM System
 * @since 1.0.0
 */
@Data
public class MuteMemberRequest {

    /**
     * 禁言时长（分钟）
     * null或0表示永久禁言
     */
    @Min(value = 0, message = "禁言时长不能为负数")
    @Max(value = 525600, message = "禁言时长不能超过1年")
    private Integer durationMinutes;

    /**
     * 禁言原因
     */
    @Size(max = 200, message = "禁言原因不能超过200个字符")
    private String reason;
}
