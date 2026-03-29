package com.im.backend.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@Schema(description = "分组统计信息")
public class GroupStat {

    @Schema(description = "分组ID")
    private Long groupId;

    @Schema(description = "分组名称")
    private String groupName;

    @Schema(description = "成员数量")
    private Long memberCount;

    @Schema(description = "排序顺序")
    private Integer sortOrder;
}
