package com.im.backend.controller;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
@Schema(description = "批量操作统计信息")
public class BatchOperationStats {

    @Schema(description = "好友总数")
    private Long totalFriends;

    @Schema(description = "已分组好友数")
    private Long groupedFriends;

    @Schema(description = "未分组好友数")
    private Long ungroupedFriends;

    @Schema(description = "星标好友数")
    private Long starredFriends;

    @Schema(description = "免打扰好友数")
    private Long mutedFriends;

    @Schema(description = "分组数量")
    private Integer groupCount;

    @Schema(description = "各分组统计")
    private List<GroupStat> groupStats;
}
