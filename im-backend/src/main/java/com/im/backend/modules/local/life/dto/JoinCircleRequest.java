package com.im.backend.modules.local.life.dto;

import lombok.Data;
import javax.validation.constraints.*;

/**
 * 加入圈子请求DTO
 */
@Data
public class JoinCircleRequest {

    @NotNull(message = "圈子ID不能为空")
    private Long circleId;

    @Size(max = 200, message = "申请理由最多200字")
    private String applyReason;
}
