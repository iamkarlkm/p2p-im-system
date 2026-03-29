package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 直播回放DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "直播回放信息")
public class LiveReplayDTO {

    @Schema(description = "回放ID")
    private Long replayId;

    @Schema(description = "直播间ID")
    private Long roomId;

    @Schema(description = "主播ID")
    private Long anchorId;

    @Schema(description = "主播昵称")
    private String anchorNickname;

    @Schema(description = "主播头像")
    private String anchorAvatar;

    @Schema(description = "回放标题")
    private String title;

    @Schema(description = "回放封面")
    private String coverImage;

    @Schema(description = "回放视频URL")
    private String videoUrl;

    @Schema(description = "视频时长（秒）")
    private Integer duration;

    @Schema(description = "视频时长显示文本")
    private String durationText;

    @Schema(description = "回放观看次数")
    private Integer viewCount;

    @Schema(description = "点赞数")
    private Long likeCount;

    @Schema(description = "评论数")
    private Integer commentCount;

    @Schema(description = "直播开始时间")
    private LocalDateTime liveStartTime;

    @Schema(description = "直播结束时间")
    private LocalDateTime liveEndTime;

    @Schema(description = "回放创建时间")
    private LocalDateTime createTime;
}
