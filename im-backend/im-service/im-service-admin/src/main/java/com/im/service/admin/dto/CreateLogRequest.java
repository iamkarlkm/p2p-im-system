package com.im.service.admin.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 创建管理员日志请求DTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateLogRequest {

    @NotNull(message = "管理员ID不能为空")
    private Long adminId;

    @NotBlank(message = "管理员用户名不能为空")
    @Size(max = 50, message = "用户名长度不能超过50个字符")
    private String adminUsername;

    @Size(max = 50, message = "真实姓名长度不能超过50个字符")
    private String adminRealName;

    @NotBlank(message = "操作类型不能为空")
    @Size(max = 20, message = "操作类型长度不能超过20个字符")
    private String operationType;

    @NotBlank(message = "操作模块不能为空")
    @Size(max = 20, message = "模块长度不能超过20个字符")
    private String module;

    @Size(max = 50, message = "目标类型长度不能超过50个字符")
    private String targetType;

    @Size(max = 64, message = "目标ID长度不能超过64个字符")
    private String targetId;

    @Size(max = 500, message = "描述长度不能超过500个字符")
    private String description;

    @Size(max = 10, message = "请求方法长度不能超过10个字符")
    private String requestMethod;

    @Size(max = 500, message = "请求URL长度不能超过500个字符")
    private String requestUrl;

    @Size(max = 10, message = "结果长度不能超过20个字符")
    private String result;

    @Size(max = 50, message = "IP地址长度不能超过50个字符")
    private String ipAddress;

    @Size(max = 500, message = "用户代理长度不能超过500个字符")
    private String userAgent;

    private Long duration;

    @Size(max = 1000, message = "错误信息长度不能超过1000个字符")
    private String errorMessage;
}
