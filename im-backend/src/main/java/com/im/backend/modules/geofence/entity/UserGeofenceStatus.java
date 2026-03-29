package com.im.backend.modules.geofence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户围栏状态实时缓存实体
 * 用于Redis缓存当前用户在哪些围栏内
 */
@Data
@TableName("im_user_geofence_status")
public class UserGeofenceStatus {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 围栏ID */
    private Long geofenceId;

    /** 进入时间 */
    private LocalDateTime enterTime;

    /** 最后确认时间 */
    private LocalDateTime lastConfirmTime;

    /** 当前状态: INSIDE-在围栏内, OUTSIDE-在围栏外 */
    private String currentStatus;

    /** 连续确认次数 */
    private Integer confirmCount;

    /** 最后位置经度 */
    private Double lastLongitude;

    /** 最后位置纬度 */
    private Double lastLatitude;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
