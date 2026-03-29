package com.im.backend.modules.local.life.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 活动群组实体
 * 每个活动自动创建一个IM群组
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName("im_activity_group")
public class ActivityGroup {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 群组唯一标识 */
    private String groupCode;

    /** 关联活动ID */
    private Long activityId;

    /** 活动标题 */
    private String activityTitle;

    /** IM群组ID */
    private String imGroupId;

    /** 群组名称 */
    private String groupName;

    /** 群组头像 */
    private String groupAvatar;

    /** 群主用户ID */
    private Long ownerId;

    /** 成员数量 */
    private Integer memberCount;

    /** 群组状态: ACTIVE-活跃, DISABLED-已禁用, DISBANDED-已解散 */
    private String status;

    /** 是否开启位置共享 */
    private Boolean locationSharingEnabled;

    /** 位置共享开始时间 */
    private LocalDateTime locationSharingStartTime;

    /** 位置共享结束时间 */
    private LocalDateTime locationSharingEndTime;

    /** 集合地点经度 */
    private Double gatheringLongitude;

    /** 集合地点纬度 */
    private Double gatheringLatitude;

    /** 集合地点名称 */
    private String gatheringPlaceName;

    /** 群组公告 */
    private String announcement;

    /** 最后一条消息时间 */
    private LocalDateTime lastMessageTime;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    /** 是否删除 */
    @TableLogic
    private Boolean deleted;
}
