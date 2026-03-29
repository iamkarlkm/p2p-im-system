package com.im.backend.modules.geofence.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 到店记录实体
 * 记录用户的到店访问
 */
@Data
@TableName("im_arrival_record")
public class ArrivalRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /** 用户ID */
    private Long userId;

    /** 商户ID */
    private Long merchantId;

    /** 门店ID */
    private Long storeId;

    /** 进入时间 */
    private LocalDateTime enterTime;

    /** 离开时间 */
    private LocalDateTime leaveTime;

    /** 停留时长(分钟) */
    private Integer stayDurationMinutes;

    /** 进入位置经度 */
    private Double enterLongitude;

    /** 进入位置纬度 */
    private Double enterLatitude;

    /** 离开位置经度 */
    private Double leaveLongitude;

    /** 离开位置纬度 */
    private Double leaveLatitude;

    /** 到店次数(当天第几次) */
    private Integer arrivalCount;

    /** 会员等级 */
    private String memberLevel;

    /** 触发围栏ID */
    private Long triggerGeofenceId;

    /** 状态: IN_STORE-在店, LEFT-已离店, PROCESSED-已处理 */
    private String status;

    /** 处理标记: NEW-新客, OLD-老客, VIP-VIP客户 */
    private String customerTag;

    /** 个性化服务已推送 */
    private Boolean servicePushed;

    /** 创建时间 */
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    /** 更新时间 */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;
}
