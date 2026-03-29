package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 直播间列表项DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "直播间列表项")
public class LiveRoomListDTO {

    @Schema(description = "直播间ID")
    private Long roomId;

    @Schema(description = "主播用户ID")
    private Long anchorId;

    @Schema(description = "主播昵称")
    private String anchorNickname;

    @Schema(description = "主播头像")
    private String anchorAvatar;

    @Schema(description = "商户ID")
    private Long merchantId;

    @Schema(description = "商户名称")
    private String merchantName;

    @Schema(description = "直播间标题")
    private String title;

    @Schema(description = "直播间封面图片URL")
    private String coverImage;

    @Schema(description = "直播状态：0-预告 1-直播中 2-暂停 3-已结束 4-回放")
    private Integer status;

    @Schema(description = "状态文本")
    private String statusText;

    @Schema(description = "直播类型：1-普通直播 2-带货直播 3-活动直播")
    private Integer liveType;

    @Schema(description = "直播类型文本")
    private String liveTypeText;

    @Schema(description = "当前观看人数")
    private Integer viewerCount;

    @Schema(description = "点赞数")
    private Long likeCount;

    @Schema(description = "计划开始时间")
    private LocalDateTime plannedStartTime;

    @Schema(description = "实际开始时间")
    private LocalDateTime actualStartTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "纬度")
    private Double latitude;

    @Schema(description = "经度")
    private Double longitude;

    @Schema(description = "与当前用户距离（米）")
    private Double distance;

    @Schema(description = "地理位置名称")
    private String locationName;

    @Schema(description = "商品数量（带货直播）")
    private Integer productCount;

    @Schema(description = "是否推荐")
    private Boolean isRecommended;
}
