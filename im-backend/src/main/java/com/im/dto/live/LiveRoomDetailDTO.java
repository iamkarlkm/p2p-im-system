package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 直播间详情响应DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "直播间详情响应")
public class LiveRoomDetailDTO {

    @Schema(description = "直播间ID")
    private Long roomId;

    @Schema(description = "主播用户ID")
    private Long anchorId;

    @Schema(description = "主播昵称")
    private String anchorNickname;

    @Schema(description = "主播头像")
    private String anchorAvatar;

    @Schema(description = "主播粉丝数")
    private Integer anchorFansCount;

    @Schema(description = "商户ID")
    private Long merchantId;

    @Schema(description = "商户名称")
    private String merchantName;

    @Schema(description = "直播间标题")
    private String title;

    @Schema(description = "直播间封面图片URL")
    private String coverImage;

    @Schema(description = "直播间简介")
    private String description;

    @Schema(description = "直播状态：0-预告 1-直播中 2-暂停 3-已结束 4-回放")
    private Integer status;

    @Schema(description = "直播状态文本")
    private String statusText;

    @Schema(description = "直播类型：1-普通直播 2-带货直播 3-活动直播")
    private Integer liveType;

    @Schema(description = "直播类型文本")
    private String liveTypeText;

    @Schema(description = "推流地址（仅主播可见）")
    private String pushUrl;

    @Schema(description = "拉流地址/播放地址")
    private String pullUrl;

    @Schema(description = "H5播放地址")
    private String h5Url;

    @Schema(description = "计划开始时间")
    private LocalDateTime plannedStartTime;

    @Schema(description = "实际开始时间")
    private LocalDateTime actualStartTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "当前观看人数")
    private Integer viewerCount;

    @Schema(description = "最高在线人数")
    private Integer peakOnlineCount;

    @Schema(description = "点赞数")
    private Long likeCount;

    @Schema(description = "分享数")
    private Integer shareCount;

    @Schema(description = "商品数量")
    private Integer productCount;

    @Schema(description = "是否允许评论：0-否 1-是")
    private Integer allowComment;

    @Schema(description = "是否允许连麦：0-否 1-是")
    private Integer allowLinkMic;

    @Schema(description = "可见范围：0-公开 1-粉丝 2-会员 3-密码")
    private Integer visibility;

    @Schema(description = "纬度")
    private Double latitude;

    @Schema(description = "经度")
    private Double longitude;

    @Schema(description = "地理位置名称")
    private String locationName;

    @Schema(description = "与当前用户距离（米）")
    private Double distance;

    @Schema(description = "直播标签")
    private List<String> tags;

    @Schema(description = "是否已关注主播")
    private Boolean isFollowed;

    @Schema(description = "当前用户是否在线")
    private Boolean isOnline;

    @Schema(description = "当前用户在该直播间的身份：0-观众 1-主播 2-管理员")
    private Integer userRole;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "商品列表（带货直播）")
    private List<LiveProductDTO> products;
}
