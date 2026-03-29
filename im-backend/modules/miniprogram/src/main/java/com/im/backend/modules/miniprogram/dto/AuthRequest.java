package com.im.backend.modules.miniprogram.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

/**
 * 认证请求
 */
@Data
public class AuthRequest {

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "身份证号不能为空")
    private String identityNumber;

    /**
     * 身份证正面照片
     */
    private String idCardFront;

    /**
     * 身份证反面照片
     */
    private String idCardBack;

    /**
     * 手持身份证照片
     */
    private String idCardHold;
}
