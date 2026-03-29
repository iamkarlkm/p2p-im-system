package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import javax.validation.constraints.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 创建直播间请求DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "创建直播间请求")
public class CreateLiveRoomRequestDTO {

    @NotBlank(message = "直播间标题不能为空")
    @Size(max = 100, message = "标题长度不能超过100个字符")
    @Schema(description = "直播间标题", required = true, example = "春季新品发布会")
    private String title;

    @Size(max = 500, message = "简介长度不能超过500个字符")
    @Schema(description = "直播间简介", example = "本次直播将发布2026春季新款服饰")
    private String description;

    @Schema(description = "直播间封面图片URL")
    private String coverImage;

    @NotNull(message = "直播类型不能为空")
    @Min(value = 1, message = "直播类型不合法")
    @Max(value = 3, message = "直播类型不合法")
    @Schema(description = "直播类型：1-普通直播 2-带货直播 3-活动直播", required = true, example = "2")
    private Integer liveType;

    @Schema(description = "计划开始时间")
    private LocalDateTime plannedStartTime;

    @Schema(description = "直播分类ID")
    private Long categoryId;

    @Schema(description = "直播标签")
    private List<String> tags;

    @Schema(description = "是否允许评论：0-否 1-是", example = "1")
    private Integer allowComment;

    @Schema(description = "是否允许连麦：0-否 1-是", example = "0")
    private Integer allowLinkMic;

    @Schema(description = "可见范围：0-公开 1-粉丝 2-会员 3-密码", example = "0")
    private Integer visibility;

    @Schema(description = "直播间密码（当可见范围为3时需要）")
    private String roomPassword;

    @Schema(description = "纬度")
    private Double latitude;

    @Schema(description = "经度")
    private Double longitude;

    @Schema(description = "地理位置名称")
    private String locationName;
}
