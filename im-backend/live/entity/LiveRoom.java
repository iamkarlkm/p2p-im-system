package com.im.live.entity;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 直播间实体
 */
@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "live_room")
public class LiveRoom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** 直播间标题 */
    @Column(name = "title", length = 200, nullable = false)
    private String title;

    /** 直播间封面 */
    @Column(name = "cover_image", length = 500)
    private String coverImage;

    /** 主播用户ID */
    @Column(name = "anchor_id", nullable = false)
    private Long anchorId;

    /** 主播昵称 */
    @Column(name = "anchor_name", length = 100)
    private String anchorName;

    /** 主播头像 */
    @Column(name = "anchor_avatar", length = 500)
    private String anchorAvatar;

    /** 关联商户ID */
    @Column(name = "merchant_id")
    private Long merchantId;

    /** 直播间状态：0-未开始 1-直播中 2-已结束 3-禁播 */
    @Column(name = "status", nullable = false)
    private Integer status;

    /** 直播推流地址 */
    @Column(name = "push_url", length = 1000)
    private String pushUrl;

    /** 直播拉流地址（播放地址） */
    @Column(name = "play_url", length = 1000)
    private String playUrl;

    /** 备用拉流地址 */
    @Column(name = "backup_play_url", length = 1000)
    private String backupPlayUrl;

    /** 推流码 */
    @Column(name = "stream_key", length = 200)
    private String streamKey;

    /** 房间类型：1-普通直播 2-带货直播 3-活动直播 */
    @Column(name = "room_type")
    private Integer roomType;

    /** 直播分类 */
    @Column(name = "category", length = 50)
    private String category;

    /** 直播简介 */
    @Column(name = "description", length = 2000)
    private String description;

    /** 计划开始时间 */
    @Column(name = "planned_start_time")
    private LocalDateTime plannedStartTime;

    /** 实际开始时间 */
    @Column(name = "actual_start_time")
    private LocalDateTime actualStartTime;

    /** 结束时间 */
    @Column(name = "end_time")
    private LocalDateTime endTime;

    /** 当前在线人数 */
    @Column(name = "online_count")
    private Integer onlineCount;

    /** 累计观看人数 */
    @Column(name = "total_view_count")
    private Integer totalViewCount;

    /** 点赞数 */
    @Column(name = "like_count")
    private Integer likeCount;

    /** 分享数 */
    @Column(name = "share_count")
    private Integer shareCount;

    /** 直播时长（秒） */
    @Column(name = "duration")
    private Integer duration;

    /** 是否允许评论 */
    @Column(name = "allow_comment")
    private Boolean allowComment;

    /** 是否允许点赞 */
    @Column(name = "allow_like")
    private Boolean allowLike;

    /** 是否允许分享 */
    @Column(name = "allow_share")
    private Boolean allowShare;

    /** 评论审核：0-无需审核 1-需要审核 */
    @Column(name = "comment_audit")
    private Integer commentAudit;

    /** 禁言用户列表（JSON） */
    @Column(name = "banned_users", length = 2000)
    private String bannedUsers;

    /** 管理员列表（JSON） */
    @Column(name = "managers", length = 2000)
    private String managers;

    /** 定位信息 */
    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "location_name", length = 200)
    private String locationName;

    /** 创建时间 */
    @CreationTimestamp
    @Column(name = "create_time")
    private LocalDateTime createTime;

    /** 更新时间 */
    @UpdateTimestamp
    @Column(name = "update_time")
    private LocalDateTime updateTime;

    /** 是否删除 */
    @Column(name = "is_deleted")
    private Boolean isDeleted;
}
