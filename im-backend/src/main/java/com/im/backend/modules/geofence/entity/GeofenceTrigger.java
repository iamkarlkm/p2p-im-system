package com.im.backend.modules.geofence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户围栏触发记录实体
 * 记录用户进出围栏的事件
 */
@Data
@TableName("im_geofence_trigger")
public class GeofenceTrigger {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 围栏ID */
    private Long geofenceId;

    /** 用户ID */
    private Long userId;

    /** 关联商户ID */
    private Long merchantId;

    /** 关联门店ID */
    private Long storeId;

    /** 触发类型: ENTER-进入, EXIT-离开, DWELL-停留超时 */
    private String triggerType;

    /** 用户经度 */
    private Double longitude;

    /** 用户纬度 */
    private Double latitude;

    /** 定位精度(米) */
    private Double accuracy;

    /** 距离围栏边界(米,负值表示在围栏内) */
    private Double distanceFromBoundary;

    /** 置信度(0-100) */
    private Integer confidence;

    /** 设备信息 */
    private String deviceInfo;

    /** 触发时间 */
    private LocalDateTime triggerTime;

    /** 处理状态: PENDING-待处理, PROCESSED-已处理, IGNORED-已忽略 */
    private String processStatus;

    /** 处理结果 */
    private String processResult;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableLogic
    private Integer deleted;
}
