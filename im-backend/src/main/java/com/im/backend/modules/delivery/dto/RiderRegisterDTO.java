package com.im.backend.modules.delivery.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * 骑手注册DTO
 */
@Data
public class RiderRegisterDTO {

    @NotNull(message = "用户ID不能为空")
    private Long userId;

    @NotBlank(message = "真实姓名不能为空")
    private String realName;

    @NotBlank(message = "手机号不能为空")
    private String phone;

    @NotNull(message = "所属站点不能为空")
    private Long stationId;

    private Integer maxOrderCount = 5;
}
