package com.im.backend.modules.location.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 位置共享成员DTO
 */
@Data
public class LocationShareMemberDTO {

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
     * 成员角色
     */
    private String role;

    /**
     * 成员状态
     */
    private String status;

    /**
     * 最后位置纬度
     */
    private Double lastLat;

    /**
     * 最后位置经度
     */
    private Double lastLng;

    /**
     * 最后位置更新时间
     */
    private LocalDateTime lastLocationTime;

    /**
     * 最后位置精度
     */
    private Double lastAccuracy;

    /**
     * 是否已到达目的地
     */
    private Boolean hasArrived;

    /**
     * 到达时间
     */
    private LocalDateTime arrivedAt;

    /**
     * 距离目的地(米)
     */
    private Integer distanceToDestination;

    /**
     * 预计到达时间
     */
    private LocalDateTime estimatedArrivalTime;

    /**
     * 电池电量
     */
    private Integer batteryLevel;

    /**
     * 移动速度
     */
    private Double speed;

    /**
     * 加入时间
     */
    private LocalDateTime joinedAt;
}
