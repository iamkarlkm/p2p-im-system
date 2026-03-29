package com.im.backend.modules.location.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 共享位置数据DTO
 */
@Data
public class SharedLocationDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 用户昵称
     */
    private String userNickname;

    /**
     * 用户头像
     */
    private String userAvatar;

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
     * 电池电量
     */
    private Integer batteryLevel;

    /**
     * 是否移动中
     */
    private Boolean isMoving;

    /**
     * 位置时间戳
     */
    private LocalDateTime locationTime;
}
