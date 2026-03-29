package com.im.dto.live;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDateTime;

/**
 * 直播评论DTO
 * 
 * @author IM Development Team
 * @since 2026-03-28
 */
@Data
@Schema(description = "直播评论/弹幕信息")
public class LiveCommentDTO {

    @Schema(description = "评论ID")
    private Long commentId;

    @Schema(description = "直播间ID")
    private Long roomId;

    @Schema(description = "发送者用户ID")
    private Long senderId;

    @Schema(description = "发送者昵称")
    private String senderNickname;

    @Schema(description = "发送者头像")
    private String senderAvatar;

    @Schema(description = "评论内容")
    private String content;

    @Schema(description = "评论类型：1-普通弹幕 2-礼物消息 3-系统消息 4-点赞 5-进入直播间")
    private Integer commentType;

    @Schema(description = "类型文本")
    private String commentTypeText;

    @Schema(description = "是否置顶：0-否 1-是")
    private Integer isPinned;

    @Schema(description = "是否主播消息：0-否 1-是")
    private Integer isAnchor;

    @Schema(description = "是否管理员：0-否 1-是")
    private Integer isAdmin;

    @Schema(description = "礼物名称（如果是礼物消息）")
    private String giftName;

    @Schema(description = "礼物数量")
    private Integer giftCount;

    @Schema(description = "礼物价值（元）")
    private java.math.BigDecimal giftValue;

    @Schema(description = "创建时间")
    private LocalDateTime createTime;

    @Schema(description = "时间显示文本（如：刚刚、3分钟前）")
    private String timeText;
}
