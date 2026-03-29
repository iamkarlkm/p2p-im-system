package com.im.backend.modules.local_life.checkin.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 签到记录实体
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("checkin_record")
public class CheckinRecord extends BaseEntity {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * POI ID
     */
    private String poiId;

    /**
     * POI名称
     */
    private String poiName;

    /**
     * POI类型
     */
    private String poiType;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * GeoHash编码(8位精度)
     */
    private String geoHash;

    /**
     * 签到距离(米)
     */
    private Integer checkinDistance;

    /**
     * 签到状态
     */
    private String status;

    /**
     * 获得积分
     */
    private Integer earnedPoints;

    /**
     * 连续签到天数
     */
    private Integer streakDays;

    /**
     * 签到设备ID
     */
    private String deviceId;

    /**
     * 设备指纹
     */
    private String deviceFingerprint;

    /**
     * 签到时间
     */
    private LocalDateTime checkinTime;

    /**
     * 签到日期(yyyy-MM-dd)
     */
    private String checkinDate;

    /**
     * 是否首次在该POI签到
     */
    private Boolean firstTimeAtPoi;

    /**
     * 签到图片URL
     */
    private String imageUrl;

    /**
     * 签到备注
     */
    private String remark;

    /**
     * 是否删除
     */
    @TableLogic
    private Integer deleted;
}
