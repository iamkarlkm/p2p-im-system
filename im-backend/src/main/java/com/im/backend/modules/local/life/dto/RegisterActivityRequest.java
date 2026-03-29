package com.im.backend.modules.local.life.dto;

import lombok.Data;
import javax.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * 活动报名请求DTO
 */
@Data
public class RegisterActivityRequest {

    @NotNull(message = "活动ID不能为空")
    private Long activityId;

    @NotNull(message = "报名人数不能为空")
    @Min(value = 1, message = "至少报名1人")
    @Max(value = 10, message = "单次最多报名10人")
    private Integer participantCount;

    private List<String> participantNames;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "请输入正确的手机号码")
    private String contactPhone;

    @Size(max = 500, message = "备注最多500字")
    private String remark;
}
