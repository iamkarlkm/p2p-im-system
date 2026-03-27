package com.im.backend.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
@Schema(description = "批量移动好友请求")
public class BatchMoveFriendRequest {

    @NotEmpty(message = "好友ID列表不能为空")
    @Schema(description = "要移动的好友ID列表", required = true)
    private List<Long> friendIds;

    @NotNull(message = "目标分组ID不能为空")
    @Schema(description = "目标分组ID", required = true)
    private Long targetGroupId;

    @Schema(description = "源分组ID（可选，用于从原分组移除）")
    private Long sourceGroupId;

    @Schema(description = "是否保持原分组关系", defaultValue = "false")
    private Boolean keepOriginalGroup = false;

    @Schema(description = "操作备注")
    private String remark;
}
