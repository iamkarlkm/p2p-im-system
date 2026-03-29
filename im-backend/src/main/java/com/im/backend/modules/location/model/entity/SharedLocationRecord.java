package com.im.backend.modules.location.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 共享位置记录实体
 * 记录用户实时位置历史
 */
@Data
@TableName("shared_location_record")
public class SharedLocationRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    /**
     * 所属会话ID
     */
    private String sessionId;

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 纬度
     */
    private Double latitude;

    /**
     * 经度
     */
    private Double longitude;

    /**
     * 精度(米)
     */
    private Double accuracy;

    /**
     * 海拔(米)
     */
    private Double altitude;

    /**
     * 速度(m/s)
     */
    private Double speed;

    /**
     * 方向(0-360度)
     */
    private Double bearing;

    /**
     * 电池电量(百分比)
     */
    private Integer batteryLevel;

    /**
     * 网络类型: WIFI-4G-5G
     */
    private String networkType;

    /**
     * 定位方式: GPS-卫星, NETWORK-网络, HYBRID-混合
     */
    private String locationProvider;

    /**
     * 位置时间戳
     */
    private LocalDateTime locationTime;

    /**
     * GeoHash值(用于空间索引)
     */
    private String geoHash;

    /**
     * 是否移动中
     */
    private Boolean isMoving;

    /**
     * 与前一点距离(米)
     */
    private Double distanceFromLast;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
