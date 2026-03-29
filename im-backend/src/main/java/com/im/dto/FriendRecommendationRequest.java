package com.im.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;
import java.util.List;

/**
 * 好友推荐请求DTO
 * 
 * @author IM Team
 * @since 2026-03-27
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "好友推荐请求")
public class FriendRecommendationRequest {

    @NotEmpty(message = "目标用户ID列表不能为空")
    @Size(max = 100, message = "一次最多处理100个用户")
    @Schema(description = "目标用户ID列表", example = "[1001, 1002, 1003]")
    private List<Long> targetUserIds;

    @Schema(description = "推荐算法类型", example = "MUTUAL_FRIEND")
    private String algorithmType;

    @Schema(description = "最小推荐分数", example = "0.5")
    private Double minScore;

    @Schema(description = "是否只显示在线用户", example = "false")
    private Boolean onlineOnly;

    @Schema(description = "排除已发送好友请求的用户", example = "true")
    private Boolean excludePending;
}
